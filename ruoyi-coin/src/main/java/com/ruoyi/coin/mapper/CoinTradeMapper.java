package com.ruoyi.coin.mapper;

import java.math.BigDecimal;

/**
 * 成交相关
 */
public interface CoinTradeMapper {

	//获取24小时总成交额
	BigDecimal get24HTradeTotalPrice(Integer symbolId, String startTime);

	//获取24小时总成交数
	Integer get24HTradeTotalCou(Integer symbolId, String startTime);

	//获取社区活跃度
	Integer getSignCou(Integer symbolId,String startTime);
}
