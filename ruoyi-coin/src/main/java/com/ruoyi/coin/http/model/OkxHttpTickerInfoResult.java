package com.ruoyi.coin.http.model;

import lombok.Data;

import java.util.List;

/**
 * 铭文详情
 */
@Data
public class OkxHttpTickerInfoResult {
	private Integer code;
	private OkxTickerInfo data;


	/***
	 * 币种详情
	 */
	@Data
	public class OkxTickerInfo {

		private String chain;//链路类型

		private String confirmedMinted;//铸造总量
		private String confirmedMinted1h;//1小时铸造量
		private String confirmedMinted24h;//24小时铸造量

		private String deployedTime;//部署时间
		private String holders;//地址数

		private Integer id;

		private String image;//图标

		private String inscriptionId;

		private String limitPerMint;//单次铸造量
		private String totalMinted;//总铸造量
		private Integer mintedProgress;//10000 => 100%
		private String mintType;//0=已经铸造完成

		private String priceChangeRate;//涨跌幅

		private String ticker;//铭文名称
		private String tickerId;//铭文id
		private String tickerType;//?

		private String transactions;//总交易数

		private String volume;//BTC交易总量  总交易额
		private String volumeCurrencyUrl;//交易总量图标
		private String usdVolume;//转为USDT的交易总量
		private String volumeIn24h;//24小时成交量
		private String usdVolumeIn24h;

		private String supply;//总供应数量

		private String marketCap;//市值
		private String usdMarketCap;//小数就是精度数


		private String creator;//币种创建人
		private String floorPrice;//地板价
		private String priceChangeRate24H;//24小时涨跌幅


	}


}
