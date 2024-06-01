package com.ruoyi.coin.http.model;

import lombok.Data;

/**
 * 成交信息
 */
@Data
public class CjxxInfo {
	private String avgPrice;//均价
	private String btcVolume;//BTC成交量
	private String volume;//成交量
	private String salesCount;//成交笔数
}
