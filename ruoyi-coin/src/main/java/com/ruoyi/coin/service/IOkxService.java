package com.ruoyi.coin.service;

import com.ruoyi.coin.http.model.OkxTickerInfo;

import java.util.List;

/**
 */
public interface IOkxService {

	String getHostUrl();


	void syncSymbolList();


	//同步铭文详情(当前的基本情况)
	void syncSymbolInfo();

	//同步交易列表
	void syncTxList();

	//同步持币地址列表
	void syncHoderList();


	//----------------------
	//获取热门铭文
	List<OkxTickerInfo> getHotTickInfo();

	//计算币种得分
	void handleSymbolScore();

	//校验VIP是否过期
	void checkVip();

	//校验VIP是否过期
	void createDaily();
}
