package com.ruoyi.coin.service;

import java.math.BigDecimal;

/**
 * 成交相关
 */
public interface ICoinTradeService {

	//获取24小时总成交额
	String get24HTradeTotalPrice(Integer symbolId);
	//获取24小时成交数
	String get24HTradeTotalCou(Integer symbolId);

	//获取社区活跃度
	String getSignStr(Integer symbolId);

}
