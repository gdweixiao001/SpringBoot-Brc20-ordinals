package com.ruoyi.coin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 统计相关
 */
public interface ICoinTjService {
    //获取评分
    IPage<Map<String, Object>> getScoreCoinPage(IPage page);

    //获取最近部署的铭文列表
    IPage<Map<String, Object>> getDeployedCoinPage(IPage page);

    //获取最近上头像的铭文列表
    IPage<Map<String, Object>> getUploadImgCoinPage(IPage page);


    //获取最近铸造完成的铭文数
    IPage<Map<String, Object>> getMintFinallyCoinPage(IPage page);

    //获取地址排行榜
    IPage<Map<String, Object>> getRankByAddress(IPage page);

    //获取交易额
    IPage<Map<String, Object>> getRankByVolume(IPage page);

    //获取市值
    IPage<Map<String, Object>> getRankByMarket(IPage page);
    //获取交易数
    IPage<Map<String, Object>> getRankByTransactions(IPage page);

    //获取排行榜详情数据
    IPage<Map<String, Object>> getRankDetailPage(String type,String isAscStr,IPage page);

    //获取今天增长地址数
    IPage<Map<String, Object>> getCollectAddressPageByTime(IPage page,String timeStr);
    //获取今天成交笔数
    IPage<Map<String, Object>> getCollectTradePageByTime(IPage page,String timeStr);
    //获取今天交易额
    IPage<Map<String, Object>> getCollectVolumePageByTime(IPage page,String timeStr);
    //获取今天市值
    IPage<Map<String, Object>> getCollectMarketPageByTime(IPage page,String timeStr);

    //获取前100名占比
    BigDecimal getAddress100Rate(Integer symbolId);
}
