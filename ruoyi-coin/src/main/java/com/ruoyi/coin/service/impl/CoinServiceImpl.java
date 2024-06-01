package com.ruoyi.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.coin.mapper.CoinMapper;
import com.ruoyi.coin.service.ICoinSqlService;
import com.ruoyi.coin.service.IMjkjBaseSqlService;
import com.ruoyi.coin.service.IOkxService;
import com.ruoyi.common.utils.MjkjUtils;
import com.ruoyi.common.utils.blade.Func;
import com.ruoyi.common.utils.blade.tool.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公共调用
 */
@Service
public class CoinServiceImpl implements ICoinSqlService {


    @Autowired
    private CoinMapper coinMapper;

    @Autowired
    private IMjkjBaseSqlService baseSqlService;


    // 根据表属性获取所有数据 多条件 分页
    @Override
    public IPage<Map<String, Object>> getCoinSymbolPage(IPage page, Map<String, Object> params, Long userId) {

        IPage pages = coinMapper.getCoinSymbolPage(page, params);
        List<Map<String, Object>> records = pages.getRecords();
        if (Func.isEmpty(records)) {
            MjkjUtils.setPageResult(params, pages);
        }
        boolean vipFlag = this.isVip(userId);//判断是否是vip


        for (Map<String, Object> dataMap : records) {
            Integer progress = MjkjUtils.getMap2Integer(dataMap, "minted_progress");
            String volume_btc_str = MjkjUtils.getMap2BigDfomat(dataMap, "volume_btc", 8, "BTC");//交易量
            String volume_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "volume_uset", 4, "U");//交易量
            Integer symbolId = MjkjUtils.getMap2Integer(dataMap, "symbol_id");

            String market_cap_btc_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_btc", 8, "BTC");//市值
            String market_cap_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_usdt", 4, "USDT");//市值
            String symbol_create_address = MjkjUtils.getMap2Str(dataMap, "symbol_create_address");//创建人


            if (progress == 0) {
                dataMap.put("minted_progress", "-");
            } else {
                BigDecimal divide = BigDecimal.valueOf(progress).divide(BigDecimal.valueOf(100));
                dataMap.put("minted_progress", divide.stripTrailingZeros().toPlainString() + "%");
            }

            //市值相关
            dataMap.put("market_cap_btc_str", market_cap_btc_str);
            dataMap.put("market_cap_usdt_str", market_cap_usdt_str);

            //交易量相关
            dataMap.put("volume_btc_str", volume_btc_str);
            dataMap.put("volume_usdt_str", volume_usdt_str);

            //判断是否是vip，如果是vip的话，可以直接进入查看，如果不是vip的话，则需要加入才能查看
            dataMap.put("isVipFlag", vipFlag);

            boolean joinFlag = this.isSymbolMember(userId, symbolId);
            if (joinFlag) {
                dataMap.put("isJoinFlag", true);
            } else {//
                dataMap.put("isJoinFlag", false);
            }

            boolean adminFlag = this.isSymbolAdmin(userId, symbolId);
            if (adminFlag) {
                dataMap.put("adminFlag", true);
            } else {//
                dataMap.put("adminFlag", false);
            }


            //处理地址数
            String sameCreateUserCou="0";
            if(Func.isNotEmpty(symbol_create_address)){
                QueryWrapper queryWrapper = MjkjUtils.getQueryWrapper();
                queryWrapper.eq("symbol_create_address",symbol_create_address);
                queryWrapper.ne("symbol_id",symbolId);
                List<Map<String, Object>> createAddressMapList = baseSqlService.getDataListByWrapper("coin_symbol", queryWrapper);
                sameCreateUserCou = createAddressMapList.size()+"";
            }
            if(Func.equals(sameCreateUserCou,"0")){
                sameCreateUserCou="-";
            }

            dataMap.put("sameCreateUserCou",sameCreateUserCou);
            dataMap.put("symbolId",symbolId);
        }
        return pages;

    }


    //获取持币地址
    @Override
    public IPage<Map<String, Object>> getCoinAddressPage(IPage page, Map<String, Object> params) {
        IPage pages = coinMapper.getCoinAddressPage(page, params);
        List<Map<String, Object>> records = pages.getRecords();

        for (Map<String, Object> dataMap : records) {
            BigDecimal amount = MjkjUtils.getMap2BigD(dataMap, "amount");
            String symbol_name = MjkjUtils.getMap2Str(dataMap, "symbol_name");

            BigDecimal ratio_val = MjkjUtils.getMap2BigD(dataMap, "ratio_val");//占比

            if (Func.isEmpty(ratio_val) || ratio_val.compareTo(BigDecimal.ZERO) != 1) {
                dataMap.put("ratio_val", "-");
            } else {
                BigDecimal multiply = ratio_val.setScale(6, BigDecimal.ROUND_UP).multiply(BigDecimal.valueOf(100));

                dataMap.put("ratio_val", multiply.stripTrailingZeros().toPlainString() + "%");
            }

            if (Func.isEmpty(amount) || amount.compareTo(BigDecimal.ZERO) != 1) {
                dataMap.put("amount", "-");
            } else {
                dataMap.put("amount", amount.stripTrailingZeros().toPlainString() + " " + symbol_name);
            }

        }
        return pages;
    }


    //获取交易信息
    @Override
    public IPage<Map<String, Object>> getCoinTxPage(IPage page, Map<String, Object> params) {
        IPage pages = coinMapper.getCoinTxPage(page, params);
        List<Map<String, Object>> records = pages.getRecords();
        for (Map<String, Object> dataMap : records) {
            String from_address = MjkjUtils.getMap2Str(dataMap, "from_address");
            String to_address = MjkjUtils.getMap2Str(dataMap, "to_address");
            String tx_hash = MjkjUtils.getMap2Str(dataMap, "tx_hash");

            dataMap.put("from_address_limit", subStr(from_address));
            dataMap.put("to_address_limit", subStr(to_address));
            dataMap.put("tx_hash_limit", subStr(tx_hash));

        }
        return pages;
    }

    //获取社区信息
    @Override
    public IPage<Map<String, Object>> getCoinPiazzaPage(IPage page, Map<String, Object> params,Long nowUserId) {
        Integer symbolId = MjkjUtils.getMap2Integer(params, "symbolId");
        boolean symbolAdminFlag = this.isSymbolAdmin(nowUserId, symbolId);

        IPage pages = coinMapper.getCoinPiazzaPage(page, params);
        List<Map<String,Object>> records = pages.getRecords();

        boolean createFlag =false;
        for(Map<String,Object> dataMap:records){
            Long user_id = MjkjUtils.getMap2Long(dataMap, "user_id");
            if(Func.equals(nowUserId,user_id)){
                createFlag=true;
            }
            dataMap.put("symbolAdminFlag",symbolAdminFlag);
            dataMap.put("createFlag",createFlag);//chuagnj
        }
        return pages;
    }

    //获取评论列表
    @Override
    public List<Map<String, Object>> getCoinPiazzaReplyList(String piazzaId) {
        return coinMapper.getCoinPiazzaReplyList(piazzaId);
    }

    //获取成员列表
    @Override
    public IPage<Map<String, Object>> getCoinMemberPage(IPage page, Integer symbolId) {
        return coinMapper.getCoinMemberPage(page, symbolId);
    }

    //获取发展历程列表
    @Override
    public List<Map<String,Object>> getEventMapList(Integer symbolId,List<String> initList){
        //获取发展历程
        QueryWrapper eventWrapper = MjkjUtils.getQueryWrapper();
        eventWrapper.eq("symbol_id", symbolId);
        eventWrapper.select("content");
        eventWrapper.orderByDesc("create_time");
        List<Map<String, Object>> eventMapList = baseSqlService.getDataListByWrapper("coin_symbol_event_log", eventWrapper);
        if (Func.isEmpty(eventMapList)) {//默认添加一个
            initList.stream().forEach(content->{
                Map<String, Object> addMap = new HashMap<>();
                addMap.put("create_user", 1);
                addMap.put("create_time", DateUtil.now());
                addMap.put("symbol_id", symbolId);
                addMap.put("content", content);
                baseSqlService.baseInsertData("coin_symbol_event_log", addMap);
            });
            eventMapList = baseSqlService.getDataListByWrapper("coin_symbol_event_log", eventWrapper);
        }
        return eventMapList;
    }

    //获取相同创建
    public IPage<Map<String, Object>> getCoiSameCreateUserPage(IPage page,Long userId, Integer symbolId, String createUserAddress){
        IPage<Map<String, Object>> pages = coinMapper.getCoiSameCreateUserPage(page, symbolId, createUserAddress);
        List<Map<String, Object>> records = page.getRecords();
        for(Map<String,Object> dataMap:records){
            Integer selectSymbolId = MjkjUtils.getMap2Integer(dataMap, "symbol_id");
            boolean isVipFlag = this.isVip(userId);
            boolean isJoinFlag = this.isSymbolMember(userId, selectSymbolId);

            dataMap.put("isVipFlag",isVipFlag);
            dataMap.put("isJoinFlag",isJoinFlag);

            String market_cap_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_usdt", 2, "U");
            dataMap.put("market_cap_usdt_str",market_cap_usdt_str);
        }
        return pages;
    }


    //是否是超级管理员
    public boolean isSysAdmin(Long userId){
        return userId==1;
    }

    //判断某个人是否是vip
    @Override
    public boolean isVip(Long userId) {
        Map<String, Object> userInfo = baseSqlService.getDataOneByField("coin_user_info", "user_id", userId);
        if (Func.isEmpty(userInfo)) {
            return false;
        }
        String is_vip = MjkjUtils.getMap2Str(userInfo, "is_vip");
        if (Func.isNotEmpty(is_vip) && (Func.equals(is_vip, "1") || Func.equals(is_vip, "2"))) {//是否会员 0=不是会员1=每月  2=临时
            return true;
        }
        return false;

    }

    //是否是社区成员
    @Override
    public boolean isSymbolMember(Long userId, Integer symbolId) {
        QueryWrapper queryWrapper = MjkjUtils.getQueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("symbol_id", symbolId);
        Map<String, Object> dataMap = baseSqlService.getDataOneByWrapper("coin_symbol_member", queryWrapper);
        return Func.isNotEmpty(dataMap);
    }


    //是否是社区管理员
    @Override
    public boolean isSymbolAdmin(Long userId, Integer symbolId) {
        if (userId == 1) {//超级管理员
            return true;
        }
        QueryWrapper queryWrapper = MjkjUtils.getQueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("symbol_id", symbolId);
        queryWrapper.eq("is_admin", 1);
        Map<String, Object> dataMap = baseSqlService.getDataOneByWrapper("coin_symbol_member", queryWrapper);
        return Func.isNotEmpty(dataMap);
    }


    private String subStr(String str) {
        if (Func.isEmpty(str)) {
            return "";
        }
        String startStr = str.substring(0, 6);
        String endStr = str.substring(str.length() - 6);
        return startStr + "..." + endStr;
    }
}
