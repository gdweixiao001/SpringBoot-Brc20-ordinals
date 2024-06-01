package com.ruoyi.coin.service.impl;

import com.ruoyi.coin.mapper.CoinMapper;
import com.ruoyi.coin.mapper.CoinTradeMapper;
import com.ruoyi.coin.service.ICoinTradeService;
import com.ruoyi.coin.service.IMjkjBaseSqlService;
import com.ruoyi.common.utils.MjkjUtils;
import com.ruoyi.common.utils.blade.Func;
import com.ruoyi.common.utils.blade.tool.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 成交相关
 */
@Service
public class CoinTradeServiceImpl implements ICoinTradeService {


    @Autowired
    private CoinTradeMapper coinTradeMapper;

    @Autowired
    private IMjkjBaseSqlService baseSqlService;

    //获取24小时总成交额
    @Override
    public String get24HTradeTotalPrice(Integer symbolId) {
        Date now = DateUtil.now();
        Date preNow = DateUtil.plusDays(now, -1);
        String timeStr = DateUtil.format(preNow, DateUtil.PATTERN_DATETIME);

        BigDecimal totalPrice = coinTradeMapper.get24HTradeTotalPrice(symbolId, timeStr);
        if (Func.isEmpty(totalPrice) || totalPrice.compareTo(BigDecimal.ZERO) != 1) {
            return "-";
        }
        return totalPrice.setScale(4, BigDecimal.ROUND_UP).stripTrailingZeros().toPlainString();

    }

    //获取24小时总成数
    @Override
    public String get24HTradeTotalCou(Integer symbolId) {
        Date now = DateUtil.now();
        Date preNow = DateUtil.plusDays(now, -100);
        String timeStr = DateUtil.format(preNow, DateUtil.PATTERN_DATETIME);

        Integer cou = coinTradeMapper.get24HTradeTotalCou(symbolId, timeStr);
        if (Func.isEmpty(cou) || cou<=0) {
            return "-";
        }
        return Func.toStr(cou);

    }

    //获取社区活跃度
    @Override
    public String getSignStr(Integer symbolId){
        Date now = DateUtil.now();
        Date preNow = DateUtil.plusDays(now, -100);
        String timeStr = DateUtil.format(preNow, DateUtil.PATTERN_DATETIME);

        Integer cou = coinTradeMapper.getSignCou(symbolId, timeStr);
        if (Func.isEmpty(cou) || cou<=0) {
            return "-";
        }
        if(cou<10){
            return "低";
        }else if(cou<20){
            return "中";
        }else if(cou<100){
            return "高";
        }else if(cou>100){
            return "极高";
        }
        return "-";
    }

}
