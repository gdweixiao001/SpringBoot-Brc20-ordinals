/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ruoyi.coin.service.impl;

import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.coin.http.OkxHttpUtils;
import com.ruoyi.coin.http.model.*;
import com.ruoyi.coin.service.ICoinTjService;
import com.ruoyi.coin.service.IMjkjBaseSqlService;
import com.ruoyi.coin.service.IOkxService;
import com.ruoyi.common.utils.MjkjUtils;
import com.ruoyi.common.utils.blade.Func;
import com.ruoyi.common.utils.blade.tool.DateUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * 服务实现类
 *
 * @author BladeX
 * @since 2021-05-20
 */
@Slf4j
@Service
public class OkxServiceImpl implements IOkxService {

    @Autowired
    private IMjkjBaseSqlService baseSqlService;

    @Autowired
    private ICoinTjService coinTjService;

    @Override
    public String getHostUrl(){
        Map<String, Object> dataMap = baseSqlService.getDataOneByField("coin_param", "param_key", "okx_host_url");
        return MjkjUtils.getMap2Str(dataMap,"param_val");
    }

    //获取需要同步的币种数
    private List<Map<String, Object>> getSymbolMapList() {
        QueryWrapper<Object> queryWrapper = MjkjUtils.getQueryWrapper();
        queryWrapper.select("id", "symbol_id", "symbol_name");
        //queryWrapper.isNull("symbol_img");
        queryWrapper.orderByAsc("create_time");
        List<Map<String, Object>> symbolMapList = baseSqlService.getDataListByWrapper("coin_symbol", queryWrapper);
        return symbolMapList;
    }

    //同步所有铭文
    @Override
    public void syncSymbolList() {
        String hostUrl = this.getHostUrl();

        Map<String, Object> paramMap = baseSqlService.getDataOneByField("coin_param", "param_key", "sync_symbol_page");
        Integer page = MjkjUtils.getMap2Integer(paramMap, "param_val");
        String paramId = MjkjUtils.getMap2Str(paramMap, "id");

        Date now = DateUtil.now();
        while (true) {
            page++;
            List<OkxTickerInfo> list = OkxHttpUtils.getTickList(hostUrl,null, page, "asc");
            if (Func.isEmpty(list)) {
                return;
            }

            for (OkxTickerInfo info : list) {
                String timestampStr = info.getDeployedTime() + "000";//转为毫秒
                Date deployedTime = new Date(Func.toLong(timestampStr));

                Map<String, Object> symbolMap = baseSqlService.getDataOneByField("coin_symbol", "symbol_id", info.getId());
                if (Func.isNotEmpty(symbolMap)) {
                    continue;
                }

                //向上取整
                long max_cou_mint = new BigDecimal(info.getSupply()).divide(new BigDecimal(info.getLimitPerMint()), 0, BigDecimal.ROUND_UP).longValue();

                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("symbol_id", info.getId());//'币种id'
                dataMap.put("symbol_name", info.getTicker());//币种名称
                dataMap.put("deployed_time", deployedTime);//部署时间
                dataMap.put("limit_per_mint", info.getLimitPerMint());//'单次铸造量'
                dataMap.put("max_cou_mint", max_cou_mint);//最大铸造笔数
                dataMap.put("supply_num", info.getSupply());//支持总量
                dataMap.put("next_handle_time",now);
                baseSqlService.baseInsertData("coin_symbol", dataMap);
            }
            if(list.size()==50){//
                Map<String, Object> updateParamMap = new HashMap<>();
                updateParamMap.put("param_val", page);
                baseSqlService.baseUpdateData("coin_param", updateParamMap, paramId);
            }


        }

    }


    //同步铭文详情
    @Override
    public void syncSymbolInfo() {
        String hostUrl = this.getHostUrl();

        Date now = DateUtil.now();
        String nowStr = DateUtil.format(now, DateUtil.PATTERN_DATETIME);

        QueryWrapper<Object> queryWrapper = MjkjUtils.getQueryWrapper();
        queryWrapper.select("id", "symbol_id", "symbol_name");
        queryWrapper.le("next_handle_time",nowStr); // <=
        //queryWrapper.eq("symbol_id",32475);
        List<Map<String, Object>> symbolMapList = baseSqlService.getDataListByWrapper("coin_symbol", queryWrapper);


        Semaphore semaphore = new Semaphore(3);
        symbolMapList.parallelStream().forEach(symbolMap -> {
            try {
                semaphore.acquire(); // 等待直到获取一个许可
                // 处理item
                String id = MjkjUtils.getMap2Str(symbolMap, "id");
                String symbolId = MjkjUtils.getMap2Str(symbolMap, "symbol_id");
                String symbolName = MjkjUtils.getMap2Str(symbolMap, "symbol_name");

                //处理铭文详情
                this.handleSymbolInfo(hostUrl,symbolName, symbolId, id);
            } catch (Exception e){
                e.printStackTrace();
            }finally {
                semaphore.release(); // 释放许可
            }
        });
    }

    //处理归集
    public void handleCollect(String symbolId, OkxHttpTickerInfoResult.OkxTickerInfo info) {
        Date now = DateUtil.now();
        Date yesterday = DateUtil.plusDays(now, -1);

        String nowStr = DateUtil.format(now, DateUtil.PATTERN_DATE);
        String yesterdayStr = DateUtil.format(yesterday, DateUtil.PATTERN_DATE);

        List<String> list = Func.toStrList("trade,volume,market,address");
        list.parallelStream().forEach(type -> {
            if (Func.equals(type, "trade")) {
                this.handleCollectTradeCou(symbolId, info.getTransactions(), yesterdayStr, nowStr);
            } else if (Func.equals(type, "volume")) {
                this.handleCollectVolumeCou(symbolId, info.getUsdVolume(), yesterdayStr, nowStr);
            } else if (Func.equals(type, "market")) {
                this.handleCollectMarketCou(symbolId, info.getUsdMarketCap(), yesterdayStr, nowStr);
            } else if (Func.equals(type, "address")) {
                this.handleCollectAddressCou(symbolId, info.getHolders(), yesterdayStr, nowStr);
            }
        });
    }

    //处理交易记录
    private void handleCollectTradeCou(String symbolId, String transactionsStr, String yesterdayStr, String nowStr) {
        if (Func.isEmpty(transactionsStr)) {
            return;
        }
        try {
            //处理当天统计
            QueryWrapper<Object> yesterdayTradeWrapper = new QueryWrapper<>();
            yesterdayTradeWrapper.eq("symbol_id", symbolId);
            yesterdayTradeWrapper.eq("tj_time", yesterdayStr);
            Map<String, Object> tradeCouMap = baseSqlService.getDataOneByWrapper("coin_collect_trade_cou", yesterdayTradeWrapper);
            int yesterday_cou = 0;
            if (Func.isNotEmpty(tradeCouMap)) {
                yesterday_cou = MjkjUtils.getMap2Integer(tradeCouMap, "today_cou");
            }

            int today_cou = Func.toInt(transactionsStr);

            int subTradCou = today_cou - yesterday_cou;

            //获取今天数据
            QueryWrapper<Object> todayTradeWrapper = new QueryWrapper<>();
            todayTradeWrapper.eq("symbol_id", symbolId);
            todayTradeWrapper.eq("tj_time", nowStr);
            Map<String, Object> nowTradeCouMap = baseSqlService.getDataOneByWrapper("coin_collect_trade_cou", todayTradeWrapper);
            if (Func.isEmpty(nowTradeCouMap)) {
                Map<String, Object> addMap = new HashMap<>();
                addMap.put("symbol_id", symbolId);
                addMap.put("tj_time", nowStr);
                addMap.put("yesterday_cou", yesterday_cou);
                addMap.put("today_cou", today_cou);
                addMap.put("sub_cou", subTradCou);
                baseSqlService.baseInsertData("coin_collect_trade_cou", addMap);
            } else {
                String id = MjkjUtils.getMap2Str(nowTradeCouMap, "id");
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("today_cou", today_cou);
                updateMap.put("sub_cou", subTradCou);
                baseSqlService.baseUpdateData("coin_collect_trade_cou", updateMap, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //处理交易额记录
    private void handleCollectVolumeCou(String symbolId, String usdVolumeStr, String yesterdayStr, String nowStr) {
        if (Func.isEmpty(usdVolumeStr)) {
            return;
        }
        try {
            //处理当天统计
            QueryWrapper<Object> yesterdayTradeWrapper = new QueryWrapper<>();
            yesterdayTradeWrapper.eq("symbol_id", symbolId);
            yesterdayTradeWrapper.eq("tj_time", yesterdayStr);
            Map<String, Object> tradeCouMap = baseSqlService.getDataOneByWrapper("coin_collect_volume_cou", yesterdayTradeWrapper);
            BigDecimal yesterday_cou = BigDecimal.ZERO;
            if (Func.isNotEmpty(tradeCouMap)) {
                yesterday_cou = MjkjUtils.getMap2BigD(tradeCouMap, "today_cou");
            }
            BigDecimal today_cou = new BigDecimal(usdVolumeStr);

            BigDecimal sub_cou = today_cou.subtract(yesterday_cou);


            //获取今天数据
            QueryWrapper<Object> todayTradeWrapper = new QueryWrapper<>();
            todayTradeWrapper.eq("symbol_id", symbolId);
            todayTradeWrapper.eq("tj_time", nowStr);
            Map<String, Object> nowTradeCouMap = baseSqlService.getDataOneByWrapper("coin_collect_volume_cou", todayTradeWrapper);
            if (Func.isEmpty(nowTradeCouMap)) {
                Map<String, Object> addMap = new HashMap<>();
                addMap.put("symbol_id", symbolId);
                addMap.put("tj_time", nowStr);
                addMap.put("yesterday_cou", yesterday_cou);
                addMap.put("today_cou", today_cou);
                addMap.put("sub_cou", sub_cou);
                baseSqlService.baseInsertData("coin_collect_volume_cou", addMap);
            } else {
                String id = MjkjUtils.getMap2Str(nowTradeCouMap, "id");
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("today_cou", today_cou);
                updateMap.put("sub_cou", sub_cou);
                baseSqlService.baseUpdateData("coin_collect_volume_cou", updateMap, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //处理市值记录
    private void handleCollectMarketCou(String symbolId, String usdMarketStr, String yesterdayStr, String nowStr) {
        if (Func.isEmpty(usdMarketStr)) {
            return;
        }
        try {
            //处理当天统计
            QueryWrapper<Object> yesterdayTradeWrapper = new QueryWrapper<>();
            yesterdayTradeWrapper.eq("symbol_id", symbolId);
            yesterdayTradeWrapper.eq("tj_time", yesterdayStr);
            Map<String, Object> tradeCouMap = baseSqlService.getDataOneByWrapper("coin_collect_market_cou", yesterdayTradeWrapper);
            BigDecimal yesterday_cou = BigDecimal.ZERO;
            if (Func.isNotEmpty(tradeCouMap)) {
                yesterday_cou = MjkjUtils.getMap2BigD(tradeCouMap, "today_cou");
            }
            BigDecimal today_cou = yesterday_cou;
            if (Func.isNotEmpty(usdMarketStr)) {
                today_cou = new BigDecimal(usdMarketStr);
            }


            BigDecimal sub_cou = today_cou.subtract(yesterday_cou);


            //获取今天数据
            QueryWrapper<Object> todayTradeWrapper = new QueryWrapper<>();
            todayTradeWrapper.eq("symbol_id", symbolId);
            todayTradeWrapper.eq("tj_time", nowStr);
            Map<String, Object> nowTradeCouMap = baseSqlService.getDataOneByWrapper("coin_collect_market_cou", todayTradeWrapper);
            if (Func.isEmpty(nowTradeCouMap)) {
                Map<String, Object> addMap = new HashMap<>();
                addMap.put("symbol_id", symbolId);
                addMap.put("tj_time", nowStr);
                addMap.put("yesterday_cou", yesterday_cou);
                addMap.put("today_cou", today_cou);
                addMap.put("sub_cou", sub_cou);
                baseSqlService.baseInsertData("coin_collect_market_cou", addMap);
            } else {
                String id = MjkjUtils.getMap2Str(nowTradeCouMap, "id");
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("today_cou", today_cou);
                updateMap.put("sub_cou", sub_cou);
                baseSqlService.baseUpdateData("coin_collect_market_cou", updateMap, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //处理统计地址数
    private void handleCollectAddressCou(String symbolId, String addressStr, String yesterdayStr, String nowStr) {
        if (Func.isEmpty(addressStr)) {
            return;
        }
        try {
            //处理当天统计
            QueryWrapper<Object> yesterdayTradeWrapper = new QueryWrapper<>();
            yesterdayTradeWrapper.eq("symbol_id", symbolId);
            yesterdayTradeWrapper.eq("tj_time", yesterdayStr);
            Map<String, Object> tradeCouMap = baseSqlService.getDataOneByWrapper("coin_collect_address_cou", yesterdayTradeWrapper);
            Integer yesterday_cou = 0;
            if (Func.isNotEmpty(tradeCouMap)) {
                yesterday_cou = MjkjUtils.getMap2Integer(tradeCouMap, "today_cou");
            }
            Integer today_cou = yesterday_cou;

            if (Func.isNotEmpty(addressStr)) {
                today_cou = Func.toInt(addressStr);
            }

            int sub_cou = today_cou - yesterday_cou;

            //获取今天数据
            QueryWrapper<Object> todayTradeWrapper = new QueryWrapper<>();
            todayTradeWrapper.eq("symbol_id", symbolId);
            todayTradeWrapper.eq("tj_time", nowStr);
            Map<String, Object> nowTradeCouMap = baseSqlService.getDataOneByWrapper("coin_collect_address_cou", todayTradeWrapper);
            if (Func.isEmpty(nowTradeCouMap)) {
                Map<String, Object> addMap = new HashMap<>();
                addMap.put("symbol_id", symbolId);
                addMap.put("tj_time", nowStr);
                addMap.put("yesterday_cou", yesterday_cou);
                addMap.put("today_cou", today_cou);
                addMap.put("sub_cou", sub_cou);
                baseSqlService.baseInsertData("coin_collect_address_cou", addMap);
            } else {
                String id = MjkjUtils.getMap2Str(nowTradeCouMap, "id");
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("today_cou", today_cou);
                updateMap.put("sub_cou", sub_cou);
                baseSqlService.baseUpdateData("coin_collect_address_cou", updateMap, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void syncTxList() {
        String hostUrl = this.getHostUrl();
        List<Map<String, Object>> symbolMapList = getSymbolMapList();


        Semaphore semaphore = new Semaphore(3);
        symbolMapList.parallelStream().forEach(symbolMap -> {
            try {
                semaphore.acquire(); // 等待直到获取一个许可
                // 处理item
                String id = MjkjUtils.getMap2Str(symbolMap, "id");
                String symbolId = MjkjUtils.getMap2Str(symbolMap, "symbol_id");
                String symbolName = MjkjUtils.getMap2Str(symbolMap, "symbol_name");
                this.handleTxList(hostUrl,symbolName, symbolId);
            }catch (Exception e){

            }finally {
                semaphore.release(); // 释放许可
            }
        });

    }

    //同步交易列表
    @Transactional
    public void handleTxList(String hostUrl,String symbolName, String symbolId) {
        //判断是否有初始化了
        Map<String, Object> tradeMap = baseSqlService.getDataOneByField("coin_trade_list", "symbol_id", symbolId);
        boolean initFlag=true;
        if(Func.isNotEmpty(tradeMap)){
            initFlag=false;
        }


        Long timeBoundary = null;
        String cursor = "";
        int page = 0;
        while (true) {
            page++;
            if (page > 20) {//100页，5000条
                return;
            }

            OkxHttpTrxListResult.OkxTransactionInfoModel infoModel = OkxHttpUtils.getTransactionList(hostUrl,symbolName, null, timeBoundary, cursor);
            if (Func.isEmpty(infoModel)) {
                return;
            }
            List<OkxTransactionInfo> transactionList = infoModel.getActivityList();
            Boolean hasNext = infoModel.getHasNext();
            cursor = infoModel.getCursor();
            for (OkxTransactionInfo info : transactionList) {
                String timestampStr = info.getCreateOn() + "";//转为毫秒
                Date txTime = new Date(Func.toLong(timestampStr));
                String txHash = info.getTxHash();
                timeBoundary = info.getCreateOn();


                Map<String, Object> txMap = null;
                if (Func.isNotEmpty(txHash)) {//有哈希，以哈希为准
                    QueryWrapper wrapper = MjkjUtils.getQueryWrapper();
                    wrapper.eq("tx_hash", txHash);
                    wrapper.eq("symbol_id", symbolId);
                    wrapper.select("id", "tx_status");
                    txMap = baseSqlService.getDataOneByWrapper("coin_trade_list", wrapper);

                    if(!initFlag && Func.isNotEmpty(txMap)){//说明之前已经处理过了
                        return;
                    }

                } else {//没有哈希，
                    QueryWrapper wrapper = MjkjUtils.getQueryWrapper();
                    wrapper.eq("nft_id", info.getNftId());
                    wrapper.eq("symbol_id", symbolId);
                    wrapper.select("id", "tx_status");
                    txMap = baseSqlService.getDataOneByWrapper("coin_trade_list", wrapper);
                }


                boolean addFlag = true;
                String id = null;
                if (!Func.isEmpty(txMap)) {
                    id = MjkjUtils.getMap2Str(txMap, "id");
                    Integer tx_status = MjkjUtils.getMap2Integer(txMap, "tx_status");
                    if (tx_status == 2) {//说明已经完成了，不用操作了
                        continue;
                    }
                    addFlag = false;
                }

                //新增
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("symbol_id", symbolId);//
                dataMap.put("tx_amount", info.getAmount());//'交易量'
                dataMap.put("tx_time", txTime);//'交易时间'
                dataMap.put("tx_status", info.getStatus());//'交易状态'
                dataMap.put("from_address", info.getFrom());//'卖方地址'
                dataMap.put("to_address", info.getTo());//'买方地址'
                dataMap.put("tx_hash", info.getTxHash());//'交易哈希'
                dataMap.put("source_name", info.getPlatformName());//'来源'
                dataMap.put("inscription_num", info.getInscriptionNum());//'铭文高度'
                dataMap.put("tx_type", info.getType());//'交易类型'
                dataMap.put("nft_id", info.getNftId());
                dataMap.put("total_price_btc", info.getTotalPrice().getPrice());//'总价(BTC)'
                dataMap.put("total_sat_btc", info.getTotalPrice().getSatPrice());//'总量'
                dataMap.put("total_price_usdt", info.getTotalPrice().getUsdPrice());//'总价(USDT)'
                dataMap.put("unit_price_btc", info.getUnitPrice().getPrice());//'单价(BTC)'
                dataMap.put("unit_sat_btc", info.getUnitPrice().getSatPrice());//'单量'
                dataMap.put("unit_price_usdt", info.getUnitPrice().getUsdPrice());//'单价(USDT)'
                if (addFlag) {
                    baseSqlService.baseInsertData("coin_trade_list", dataMap);
                } else {
                    baseSqlService.baseUpdateData("coin_trade_list", dataMap, id);
                }
            }
            //说明没有下一页了
            if (!hasNext) {
                return;
            }


        }

    }

    //同步交易列表
    private void handleSymbolInfo(String hostUrl,String symbolName, String symbolId, String id) {
        OkxHttpTickerInfoResult.OkxTickerInfo info = OkxHttpUtils.getTickInfo(hostUrl,symbolName);
        if (Func.isEmpty(info)) {
            return;
        }


        String image = info.getImage();
        Date imageTime = null;//头像时间
        if (Func.isNotEmpty(image) && !image.contains("_default_") && !image.contains("_unknown")) {//已上头像
            imageTime = DateUtil.now();
        }


        Map<String, Object> symbolMap = baseSqlService.getDataOneByField("coin_symbol", "symbol_id", symbolId);
        if (Func.isEmpty(symbolMap)) {
            return;
        }
        Date select_ImgTime = MjkjUtils.getMap2DateTime(symbolMap, "img_time");
        String select_minted_progress = MjkjUtils.getMap2Str(symbolMap, "minted_progress");


        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("symbol_img", info.getImage());//'币种图标'

        updateMap.put("confirmed_minted", info.getConfirmedMinted());//''已铸造总量''
        updateMap.put("confirmed_minted1h", info.getConfirmedMinted1h());//'1h已铸造总量'
        updateMap.put("confirmed_minted24h", info.getConfirmedMinted24h());//''24h已铸造总量''

        updateMap.put("symbol_create_address", info.getCreator());//创建人
        updateMap.put("holders", info.getHolders());//'地址数'
        updateMap.put("floor_price", info.getFloorPrice());//最低价

        if (Func.isNotEmpty(info.getMarketCap())) {
            updateMap.put("market_cap_btc", info.getMarketCap());//市值-BTC
        }
        if (Func.isNotEmpty(info.getUsdMarketCap())) {
            updateMap.put("market_cap_usdt", info.getUsdMarketCap());//市值-usdt
        }


        updateMap.put("zdf", info.getPriceChangeRate24H());//24小时涨跌幅

        updateMap.put("transactions", info.getTransactions());//'总交易数'

        if (Func.isNotEmpty(info.getVolume())) {
            updateMap.put("volume_btc", info.getVolume());//'交易总量-BTC'
        }

        if (Func.isNotEmpty(info.getUsdVolume())) {
            updateMap.put("volume_usdt", info.getUsdVolume());//'交易总量-USDT'
        }
        updateMap.put("volume_24h",info.getVolumeIn24h());//24小时成交量


        if (Func.isNotEmpty(imageTime) && Func.isEmpty(select_ImgTime)) {
            updateMap.put("img_time", imageTime);//上头像时间
        }
        //处理进度
        if (!Func.equals(select_minted_progress, "10000")) {//未铸造完成，需要更新
            String supplyStr = info.getSupply();//总
            String totalMintedStr = info.getTotalMinted();
            BigDecimal supply = new BigDecimal(supplyStr);
            BigDecimal totalMinted = new BigDecimal(totalMintedStr);
            String minted_progress = "0";
            if (supply.compareTo(totalMinted) == 0) {
                minted_progress = "10000";
            } else {
                minted_progress = totalMinted.divide(supply, 4, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(10000)).stripTrailingZeros().toPlainString();
            }
            if (Func.equals(minted_progress, "10000")) {
                updateMap.put("minted_finally_time", DateUtil.now());
            }
            updateMap.put("minted_progress", minted_progress);
        }

        //更新下次执行时间
        Date now = DateUtil.now();
        Date next_handle_time=null;
        String volumeIn24hStr = info.getVolumeIn24h();
        BigDecimal volumeIn24h = new BigDecimal(volumeIn24hStr);
        if(volumeIn24h.compareTo(BigDecimal.ZERO)==1){//有成交量
            next_handle_time = DateUtil.plusMinutes(now, 30);//30分钟
        }else{//没有成交量
            String holdersStr = info.getHolders();
            int holders = Func.toInt(holdersStr);
            if(holders<100){
                next_handle_time = DateUtil.plusHours(now, 3);//12小时
            }else{
                next_handle_time = DateUtil.plusHours(now, 2);//6小时
            }
        }
        updateMap.put("next_handle_time",next_handle_time);
        baseSqlService.baseUpdateData("coin_symbol", updateMap, id);


        //处理归集数据
        this.handleCollect(symbolId, info);

    }

    //同步持币地址列表（每天同步一次）
    @Override
    public void syncHoderList() {
        String hostUrl = this.getHostUrl();

        List<Map<String, Object>> symbolMapList = getSymbolMapList();

        Semaphore semaphore = new Semaphore(3);
        symbolMapList.parallelStream().forEach(symbolMap -> {
            try {
                semaphore.acquire(); // 等待直到获取一个许可
                // 处理item
                Integer symbolId = MjkjUtils.getMap2Integer(symbolMap, "symbol_id");
                String symbolName = MjkjUtils.getMap2Str(symbolMap, "symbol_name");

                //先删除，再新建
                baseSqlService.baseZdyRealDeleteSql("coin_symbol_hoder","symbol_id",Func.toStr(symbolId));

                this.handleHoderList(hostUrl,symbolName, symbolId);
            }catch (Exception e){

            }finally {
                semaphore.release(); // 释放许可
            }
        });

    }

    //处理同步数据
    public void handleHoderList(String hostUrl,String symbolName, Integer symbolId) {
        int page = 0;
        int ind_sort = 0;
        while (true) {
            page++;
            if (page > 2) {
                return;
            }
            List<OkxHttpHoderResult.ownersModel> hoderList = OkxHttpUtils.getHoderList(hostUrl,symbolName, page);
            if (Func.isEmpty(hoderList)) {
                return;
            }
            for (OkxHttpHoderResult.ownersModel model : hoderList) {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("symbol_id", symbolId);//
                dataMap.put("amount", model.getAmount());
                dataMap.put("owner_address", model.getOwnerAddress());
                dataMap.put("ratio_val", model.getRatio());
                dataMap.put("ind_sort", ++ind_sort);
                baseSqlService.baseInsertData("coin_symbol_hoder", dataMap);
            }
            int size = hoderList.size();
            if (size < 50) {
                return;
            }

        }
    }


    //获取热门铭文
    @Override
    public List<OkxTickerInfo> getHotTickInfo() {
        String hostUrl = this.getHostUrl();
        List<OkxTickerInfo> hotTickInfoList = OkxHttpUtils.getHotTickInfo(hostUrl);
        return hotTickInfoList;
    }

    //计算币种得分
    @Override
    public void handleSymbolScore() {
        List<Map<String, Object>> symbolMapList = baseSqlService.getDataByTable("coin_symbol");
        //List<Map<String, Object>> symbolMapList = baseSqlService.getDataListByField("coin_symbol", "symbol_name", "rats");
        Semaphore semaphore = new Semaphore(5);

        Date now = DateUtil.now();
        Date yesterday = DateUtil.plusDays(now, -1);
        String yesterdayStr = DateUtil.format(yesterday, DateUtil.PATTERN_DATE);

        symbolMapList.parallelStream().forEach(dataMap -> {
            try {
                semaphore.acquire(); // 等待直到获取一个许可
                // 处理item
                int startScore=0;//开始分为50

                String id = MjkjUtils.getMap2Str(dataMap, "id");
                Integer symbolId = MjkjUtils.getMap2Integer(dataMap, "symbol_id");

                Integer holders = MjkjUtils.getMap2Integer(dataMap, "holders");//地址数
                if(holders>=5000){//大于5k得10分
                    startScore+=20;
                }else if(holders>4000){//大于4k得7分
                    startScore+=15;
                }else if(holders>3000){//大于4k得5分
                    startScore+=10;
                }else if(holders>1500){//1500-2000 4分
                    startScore+=8;
                }else if(holders>1000){//1000-1500 3分
                    startScore+=5;
                }else if(holders>500){//500-1000 2
                    startScore+=3;
                }else if(holders>50){//50-500 1
                    startScore+=1;
                }
                //处理头像 +2
                Date imgTime = MjkjUtils.getMap2DateTime(dataMap, "img_time");
                if(Func.isNotEmpty(imgTime)){
                    startScore+=5;
                }
                //交易笔数
                Long transactions = MjkjUtils.getMap2Long(dataMap, "transactions");
                if(transactions>10000){
                    startScore+=20;
                }else if(transactions>5000){
                    startScore+=15;
                }else if(transactions>4500){
                    startScore+=10;
                }else if(transactions>4000){
                    startScore+=7;
                }else if(transactions>3500){
                    startScore+=6;
                }else if(transactions>3000){
                    startScore+=5;
                }else if(transactions>2000){
                    startScore+=4;
                }else if(transactions>1000){
                    startScore+=3;
                }else if(transactions>500){
                    startScore+=2;
                }else if(transactions>200){
                    startScore+=1;
                }
                //获取涨跌幅
                BigDecimal zdf = MjkjUtils.getMap2BigD(dataMap, "zdf");
                if(zdf.compareTo(BigDecimal.ZERO)==1){
                    startScore+=2;
                }
                //24小时有交易量
                BigDecimal volume_24h = MjkjUtils.getMap2BigD(dataMap, "volume_24h");
                if(Func.isNotEmpty(volume_24h) && volume_24h.compareTo(BigDecimal.ZERO)==1){
                    startScore+=5;
                }
                //获取昨日的地址变化数
                QueryWrapper addressWrapper = MjkjUtils.getQueryWrapper();
                addressWrapper.eq("symbol_id",symbolId);
                addressWrapper.eq("tj_time",yesterdayStr);
                addressWrapper.gt("yesterday_cou",0);
                addressWrapper.select("sub_cou");
                Map<String, Object> addressCouMap = baseSqlService.getDataOneByWrapper("coin_collect_address_cou", addressWrapper);
                Integer address_sub_cou = MjkjUtils.getMap2Integer(addressCouMap, "sub_cou");
                if(address_sub_cou>50){
                    startScore+=20;
                }else if(address_sub_cou>30){
                    startScore+=15;
                }else if(address_sub_cou>10){
                    startScore+=10;
                }else if(address_sub_cou>0){
                    startScore+=5;
                }

                //获取昨日成交数
                QueryWrapper tradeWrapper = MjkjUtils.getQueryWrapper();
                tradeWrapper.eq("symbol_id",symbolId);
                tradeWrapper.eq("tj_time",yesterdayStr);
                tradeWrapper.gt("yesterday_cou",0);
                tradeWrapper.select("sub_cou");
                Map<String, Object> tradeCouMap = baseSqlService.getDataOneByWrapper("coin_collect_trade_cou", tradeWrapper);
                Integer trade_sub_cou = MjkjUtils.getMap2Integer(tradeCouMap, "sub_cou");
                if(trade_sub_cou>50){
                    startScore+=20;
                }else if(trade_sub_cou>30){
                    startScore+=15;
                }else if(trade_sub_cou>10){
                    startScore+=10;
                }else if(trade_sub_cou>0){
                    startScore+=5;
                }

                //获取昨日成交数
                QueryWrapper volumeWrapper = MjkjUtils.getQueryWrapper();
                volumeWrapper.eq("symbol_id",symbolId);
                volumeWrapper.eq("tj_time",yesterdayStr);
                volumeWrapper.gt("yesterday_cou",0);
                volumeWrapper.select("sub_cou");
                Map<String, Object> volumeCouMap = baseSqlService.getDataOneByWrapper("coin_collect_volume_cou", volumeWrapper);
                BigDecimal volume_sub_cou = MjkjUtils.getMap2BigD(volumeCouMap, "sub_cou");
                if(volume_sub_cou.compareTo(BigDecimal.valueOf(1))==1){
                    startScore+=20;
                }else if(volume_sub_cou.compareTo(BigDecimal.valueOf(0.5))==1){
                    startScore+=15;
                }else if(volume_sub_cou.compareTo(BigDecimal.valueOf(0.01))==1){
                    startScore+=10;
                }else if(trade_sub_cou>0){
                    startScore+=5;
                }


                //获取前100名占比
                BigDecimal address100Rate = coinTjService.getAddress100Rate(symbolId);
                if(address100Rate.compareTo(BigDecimal.valueOf(20))==1){
                    startScore+=0;
                }else if(address100Rate.compareTo(BigDecimal.valueOf(10))==1){
                    startScore+=1;
                }else if(address100Rate.compareTo(BigDecimal.valueOf(5))==1){
                    startScore+=3;
                }else if(address100Rate.compareTo(BigDecimal.valueOf(4))==1){
                    startScore+=5;
                }else if(address100Rate.compareTo(BigDecimal.valueOf(3))==1){
                    startScore+=7;
                }else if(address100Rate.compareTo(BigDecimal.valueOf(2))==1){
                    startScore+=8;
                }else{
                    startScore+=10;
                }
                //获取群成员数
                List<Map<String, Object>> memberDataList = baseSqlService.getDataListByField("coin_symbol_member", "symbol_id", symbolId);
                int memberCou = memberDataList.size();
                if(memberCou>100){
                    startScore+=10;
                }else if(memberCou>70){
                    startScore+=9;
                }else if(memberCou>50){
                    startScore+=8;
                }else if(memberCou>40){
                    startScore+=7;
                }else if(memberCou>30){
                    startScore+=4;
                }else if(memberCou>20){
                    startScore+=3;
                }else if(memberCou>10){
                    startScore+=2;
                }else if(memberCou>1){
                    startScore+=1;
                }
                //获取社区帖子
                List<Map<String, Object>> piazzaDataList = baseSqlService.getDataListByField("coin_symbol_piazza", "symbol_id", symbolId);
                int piazzaCou = piazzaDataList.size();
                if(piazzaCou>500){
                    startScore+=10;
                }else if(piazzaCou>400){
                    startScore+=9;
                }else if(piazzaCou>300){
                    startScore+=8;
                }else if(piazzaCou>200){
                    startScore+=7;
                }else if(piazzaCou>100){
                    startScore+=4;
                }else if(piazzaCou>50){
                    startScore+=3;
                }else if(piazzaCou>20){
                    startScore+=2;
                }else if(piazzaCou>1){
                    startScore+=1;
                }
                if(startScore>100){
                    startScore=100;
                }

                Map<String,Object> updateScoreMap=new HashMap<>();
                updateScoreMap.put("score",startScore);
                baseSqlService.baseUpdateData("coin_symbol",updateScoreMap,id);
            }catch (Exception e){

            }finally {
                semaphore.release(); // 释放许可
            }
        });

    }

    //校验VIP是否过期
    @Override
    public void checkVip(){
        //校验是否过期
        QueryWrapper queryWrapper = MjkjUtils.getQueryWrapper();
        queryWrapper.eq("is_vip",1);//会员
        queryWrapper.isNotNull("expire_vip_time");
        List<Map<String, Object>> vipMapList = baseSqlService.getDataListByWrapper("coin_user_info", queryWrapper);
        if(Func.isEmpty(vipMapList)){
            return;
        }
        Date now = DateUtil.now();

        for(Map<String,Object> dataMap:vipMapList){
            String id = MjkjUtils.getMap2Str(dataMap, "id");
            Date vipTime = MjkjUtils.getMap2DateTime(dataMap, "expire_vip_time");
            if(vipTime.getTime()>now.getTime()){
                continue;
            }
            Map<String,Object> updateMap=new HashMap<>();
            updateMap.put("expire_vip_time",null);
            updateMap.put("is_vip",0);
            baseSqlService.baseUpdateData("coin_user_info",updateMap,id);

        }

    }

    //校验VIP是否过期
    @Override
    public void createDaily(){
        Date now = DateUtil.now();
        Date yesterday = DateUtil.plusDays(now, -1);
        String yesterdayStr = DateUtil.format(yesterday, DateUtil.PATTERN_DATE);

        //List<Map<String, Object>> symbolMapList = baseSqlService.getDataListByField("coin_symbol", "symbol_name", "rats");
        List<Map<String, Object>> symbolMapList = baseSqlService.getDataByTable("coin_symbol");

        Semaphore semaphore = new Semaphore(3);
        symbolMapList.parallelStream().forEach(symbolMap -> {
            try {
                semaphore.acquire(); // 等待直到获取一个许可
                Integer symbolId = MjkjUtils.getMap2Integer(symbolMap, "symbol_id");
                String floor_price = MjkjUtils.getMap2Str(symbolMap, "floor_price");
                Integer score = MjkjUtils.getMap2Integer(symbolMap, "score");


                StringBuilder sb=new StringBuilder();
                sb.append(yesterdayStr+" 日报：</br>");

                if(Func.isNotEmpty(floor_price)){
                    sb.append("最低价："+floor_price).append(" sats</br>");
                }

                //获取昨日的成交量
                QueryWrapper volumeWrapper = MjkjUtils.getQueryWrapper();
                volumeWrapper.eq("symbol_id",symbolId);
                volumeWrapper.eq("tj_time",yesterdayStr);
                volumeWrapper.gt("yesterday_cou",0);//>0
                Map<String, Object> volumeMap =  baseSqlService.getDataOneByWrapper("coin_collect_volume_cou",volumeWrapper);
                String volume_sub = MjkjUtils.getMap2BigDfomat(volumeMap, "sub_cou", 4, "USDT");//sub_cou
                String volume_today = MjkjUtils.getMap2BigDfomat(volumeMap, "today_cou", 4, "USDT");//today_cou


                //获取昨日的交易数
                QueryWrapper tradeWrapper = MjkjUtils.getQueryWrapper();
                tradeWrapper.eq("symbol_id",symbolId);
                tradeWrapper.eq("tj_time",yesterdayStr);
                tradeWrapper.gt("yesterday_cou",0);
                Map<String, Object> tradeMap =  baseSqlService.getDataOneByWrapper("coin_collect_trade_cou",tradeWrapper);
                String trade_sub = MjkjUtils.getMap2Str(tradeMap, "sub_cou");//sub_cou
                String trade_today = MjkjUtils.getMap2Str(tradeMap, "today_cou");//today_cou

                //获取昨日的市值
                QueryWrapper marketWrapper = MjkjUtils.getQueryWrapper();
                marketWrapper.eq("symbol_id",symbolId);
                marketWrapper.eq("tj_time",yesterdayStr);
                marketWrapper.gt("yesterday_cou",0);
                Map<String, Object> marketMap =  baseSqlService.getDataOneByWrapper("coin_collect_market_cou",marketWrapper);
                String market_sub = MjkjUtils.getMap2BigDfomat(marketMap, "today_cou", 4, "USDT");//sub_cou
                sb.append("总市值："+market_sub).append("</br>");
                sb.append("总成交额："+volume_today).append("　　24H净增长："+volume_sub).append("</br>");
                sb.append("总成交数："+trade_today).append("　　24H净增长："+trade_sub).append("</br>");


                //获取昨日的地址
                QueryWrapper addressWrapper = MjkjUtils.getQueryWrapper();
                addressWrapper.eq("symbol_id",symbolId);
                addressWrapper.eq("tj_time",yesterdayStr);
                addressWrapper.gt("yesterday_cou",0);
                Map<String, Object> addressMap =  baseSqlService.getDataOneByWrapper("coin_collect_address_cou",addressWrapper);
                String today_cou = MjkjUtils.getMap2Str(addressMap, "today_cou");
                String sub_cou = MjkjUtils.getMap2Str(addressMap, "sub_cou");
                sb.append("总人数："+today_cou).append("  24H净增长：").append(sub_cou).append("</br>");

                sb.append("评分："+score).append("</br>");

                Map<String,Object> eventMap=new HashMap<>();
                eventMap.put("symbol_id",symbolId);
                eventMap.put("content",sb.toString());
                baseSqlService.baseInsertData("coin_symbol_event_log",eventMap);
            }catch (Exception e){

            }finally {
                semaphore.release(); // 释放许可
            }
        });




    }
}
