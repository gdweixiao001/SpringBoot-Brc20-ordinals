package com.ruoyi.coin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 统计相关
 */
public interface CoinTjMapper {

    //评分高低
    IPage<Map<String, Object>> getScoreCoinPage(IPage page);

    //获取最近部署的铭文列表
    IPage<Map<String, Object>> getDeployedCoinPage(IPage page,String startTimeStr);

    //获取最近上头像的铭文列表
    IPage<Map<String, Object>> getUploadImgCoinPage(IPage page,String startTimeStr);

    //获取最近铸造完成的铭文数
    IPage<Map<String, Object>> getMintFinallyCoinPage(IPage page,String startTimeStr);

    //获取地址排行榜
    IPage<Map<String, Object>> getRankByAddress(IPage page);

    //获取交易额排行榜
    IPage<Map<String, Object>> getRankByVolume(IPage page);

    //获取市值排行榜
    IPage<Map<String, Object>> getMarketByVolume(IPage page);

    //获取交易数排行榜
    IPage<Map<String, Object>> getRankByTransactions(IPage page);

    //获取排行榜详情数据
    IPage<Map<String, Object>> getRankDetailPage(String type,String isAscStr,IPage page);

    //今日净增长数
    IPage<Map<String, Object>> getCollectAddressPageByTime(IPage page,String timeStr);

    //今日成交笔数
    IPage<Map<String, Object>> getCollectTradePageByTime(IPage page,String timeStr);

    //获取今天交易额
    IPage<Map<String, Object>> getCollectVolumePageByTime(IPage page,String timeStr);

    //获取今天市值
    IPage<Map<String, Object>> getCollectMarketPageByTime(IPage page,String timeStr);

    //获取我的铭文
    List<Map<String,Object>> getMySymbolList(Long userId);

    //获取前100名占比
    BigDecimal getAddress100Rate(Integer symbolId);
}
