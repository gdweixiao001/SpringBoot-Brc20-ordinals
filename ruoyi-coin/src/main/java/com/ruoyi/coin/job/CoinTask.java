package com.ruoyi.coin.job;

import com.ruoyi.coin.service.IOkxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定时任务调度
 * 铭文
 *
 * @author ruoyi
 */
@Component("coinTask")
public class CoinTask {

    @Autowired
    private IOkxService okxService;


    //同步铭文列表
    public void syncSymbolList() {
        okxService.syncSymbolList();
    }


    /**
     * 同步铭文详情
     */
    public void syncSymbolInfo() {
        okxService.syncSymbolInfo();
    }


    /**
     * 同步交易信息
     */
    public void syncTxList() {
        okxService.syncTxList();
    }

    /**
     * 同步持币地址列表  1天一次
     */
    public void syncHoderList() {
        okxService.syncHoderList();
    }

    /**
     * 计算币种得分
     */
    public void handleSymbolScore() {
        okxService.handleSymbolScore();
    }

    /**
     * 计算VIP是否到期
     */
    public void checkVip() {
        okxService.checkVip();
    }

    /**
     * 生成日报
     */
    public void createDaily() {
        okxService.createDaily();
    }
}
