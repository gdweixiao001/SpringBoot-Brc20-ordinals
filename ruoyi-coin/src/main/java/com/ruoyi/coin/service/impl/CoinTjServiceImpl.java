package com.ruoyi.coin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.coin.mapper.CoinTjMapper;
import com.ruoyi.coin.service.ICoinTjService;
import com.ruoyi.common.utils.MjkjUtils;
import com.ruoyi.common.utils.blade.Func;
import com.ruoyi.common.utils.blade.tool.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 统计相关
 */
@Service
public class CoinTjServiceImpl implements ICoinTjService {

    @Autowired
    private CoinTjMapper coinTjMapper;

    //获取最近三天
    private String getPre3DayStr(){
        Date now = DateUtil.now();
        Date pre3Day = DateUtil.plusDays(now, -1000);

        String pre3DayStr = DateUtil.format(pre3Day, DateUtil.PATTERN_DATE);
        return pre3DayStr;
    }

    //评分
    @Override
    public IPage<Map<String, Object>> getScoreCoinPage(IPage page){
        IPage<Map<String, Object>> pages = coinTjMapper.getScoreCoinPage(page);
        return pages;
    }

    //获取最近部署的铭文列表
    @Override
    public IPage<Map<String, Object>> getDeployedCoinPage(IPage page){
        String startTimeStr = this.getPre3DayStr();
        IPage<Map<String, Object>> pages = coinTjMapper.getDeployedCoinPage(page,startTimeStr);
        return pages;
    }

    //获取最近上头像的铭文列表
    @Override
    public IPage<Map<String, Object>> getUploadImgCoinPage(IPage page){
        String startTimeStr = this.getPre3DayStr();
        IPage<Map<String, Object>> pages = coinTjMapper.getUploadImgCoinPage(page, startTimeStr);
        return pages;
    }


    //获取最近铸造完成的铭文数
    @Override
    public IPage<Map<String, Object>> getMintFinallyCoinPage(IPage page){
        String startTimeStr = this.getPre3DayStr();
        IPage<Map<String, Object>> pages = coinTjMapper.getMintFinallyCoinPage(page, startTimeStr);
        return pages;
    }

    //获取地址排行榜
    @Override
    public IPage<Map<String, Object>> getRankByAddress(IPage page){
        IPage<Map<String, Object>> pages = coinTjMapper.getRankByAddress(page);
        List<Map<String, Object>> records = pages.getRecords();
        for(Map<String, Object> dataMap:records ){
            String market_cap_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_usdt",2,"U");
            dataMap.put("market_cap_usdt_str", market_cap_usdt_str);
        }

        return pages;
    }

    @Override
    public IPage<Map<String, Object>> getRankByVolume(IPage page){
        IPage<Map<String, Object>> pages = coinTjMapper.getRankByVolume(page);
        List<Map<String, Object>> records = pages.getRecords();
        for(Map<String, Object> dataMap:records ){
            String market_cap_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_usdt",2,"U");
            dataMap.put("market_cap_usdt_str", market_cap_usdt_str);

            String volume_usdt = MjkjUtils.getMap2BigDfomat(dataMap, "volume_usdt",2,"U");
            dataMap.put("volume_usdt_str", volume_usdt);
        }

        return pages;
    }

    //获取市值
    public IPage<Map<String, Object>> getRankByMarket(IPage page){
        IPage<Map<String, Object>> pages = coinTjMapper.getMarketByVolume(page);
        List<Map<String, Object>> records = pages.getRecords();
        for(Map<String, Object> dataMap:records ){
            String market_cap_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_usdt",2,"U");
            dataMap.put("market_cap_usdt_str", market_cap_usdt_str);
        }

        return pages;
    }

    //获取市值
    public IPage<Map<String, Object>> getRankByTransactions(IPage page){
        IPage<Map<String, Object>> pages = coinTjMapper.getRankByTransactions(page);
        List<Map<String, Object>> records = pages.getRecords();
        for(Map<String, Object> dataMap:records ){
            String market_cap_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_usdt",2,"U");
            dataMap.put("market_cap_usdt_str", market_cap_usdt_str);
        }

        return pages;
    }

    //获取排行榜详情数据
    public IPage<Map<String, Object>> getRankDetailPage(String type,String isAscStr,IPage page){
        IPage<Map<String, Object>> pages = coinTjMapper.getRankDetailPage(type,isAscStr,page);
        List<Map<String, Object>> records = pages.getRecords();
        for(Map<String, Object> dataMap:records ){
            String volume_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "volume_usdt",2,"U");
            dataMap.put("volume_usdt_str", volume_usdt_str);

            String market_cap_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_usdt",2,"U");
            dataMap.put("market_cap_usdt_str", market_cap_usdt_str);
        }
        return pages;
    }

    //获取今天增长地址数
    @Override
    public IPage<Map<String, Object>> getCollectAddressPageByTime(IPage page,String timeStr){
        IPage<Map<String, Object>> pages = coinTjMapper.getCollectAddressPageByTime(page, timeStr);
        return pages;
    }
    //获取今天成交笔数
    @Override
    public IPage<Map<String, Object>> getCollectTradePageByTime(IPage page,String timeStr){
        IPage<Map<String, Object>> pages = coinTjMapper.getCollectTradePageByTime(page, timeStr);
        return pages;
    }

    //获取今天交易额
    @Override
    public IPage<Map<String, Object>> getCollectVolumePageByTime(IPage page,String timeStr){
        IPage<Map<String, Object>> pages = coinTjMapper.getCollectVolumePageByTime(page, timeStr);
        List<Map<String, Object>> records = pages.getRecords();
        for(Map<String,Object> dataMap:records){
            String yesterday_cou_str = MjkjUtils.getMap2BigDfomat(dataMap, "yesterday_cou", 2, "U");
            String today_cou_str = MjkjUtils.getMap2BigDfomat(dataMap, "today_cou", 2, "U");
            String sub_cou_str = MjkjUtils.getMap2BigDfomat(dataMap, "sub_cou", 2, "U");

            dataMap.put("yesterday_cou_str",yesterday_cou_str);
            dataMap.put("today_cou_str",today_cou_str);
            dataMap.put("sub_cou_str",sub_cou_str);
        }

        return pages;
    }
    //获取今天市值
    @Override
    public IPage<Map<String, Object>> getCollectMarketPageByTime(IPage page,String timeStr){
        IPage<Map<String, Object>> pages = coinTjMapper.getCollectMarketPageByTime(page, timeStr);
        List<Map<String, Object>> records = pages.getRecords();
        for(Map<String,Object> dataMap:records){
            String yesterday_cou_str = MjkjUtils.getMap2BigDfomat(dataMap, "yesterday_cou", 2, "U");
            String today_cou_str = MjkjUtils.getMap2BigDfomat(dataMap, "today_cou", 2, "U");
            String sub_cou_str = MjkjUtils.getMap2BigDfomat(dataMap, "sub_cou", 2, "U");

            dataMap.put("yesterday_cou_str",yesterday_cou_str);
            dataMap.put("today_cou_str",today_cou_str);
            dataMap.put("sub_cou_str",sub_cou_str);
        }

        return pages;
    }

    //获取前100名占比
    @Override
    public BigDecimal getAddress100Rate(Integer symbolId){
        BigDecimal rate = coinTjMapper.getAddress100Rate(symbolId);
        if(Func.isEmpty(rate)){
            rate=BigDecimal.ZERO;
        }
        return rate;
    }
}
