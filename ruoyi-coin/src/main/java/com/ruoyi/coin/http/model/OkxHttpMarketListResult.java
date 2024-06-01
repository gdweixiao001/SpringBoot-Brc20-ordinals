package com.ruoyi.coin.http.model;

import lombok.Data;

import java.util.List;

@Data
public class OkxHttpMarketListResult {
	private Integer code;
	private OkxDataModel data;


	/**
	 * 交易明细
	 */
	@Data
	public static class OkxDataModel {
		private List<ItemModel> items;
		private Boolean hasNext;
		private String cursor;
	}

	@Data
	public static class ItemModel {
		private String linkUrl;
		private String amount;//铭文数量
		private Long createOn;//创建日期
		private String ticker;//铭文名称
		private String tickerIcon;
		private String floorPrice;//最低价格
		private String inscriptionNum;//铭文高度
		private TotalPriceModel totalPrice;
		private UnitPriceModel unitPrice;

		private String totalBtc;
		private String totalUsdt;
		private String onePrice;
	}


	//总价相关
	@Data
	public static class TotalPriceModel {
		private String currency;//币种
		private String currencyUrl;//币种名称
		private String price;//btc总量
		private String satPrice;//总量
		private String usdPrice;//折换成usdt量
	}

	//单价相关
	@Data
	public static class UnitPriceModel {
		private String currency;//币种
		private String currencyUrl;//币种名称
		private String price;//btc单价
		private String satPrice;//单价 TotalPriceModel.satPrice / UnitPriceModel.satPrice  =铭文数量
		private String usdPrice;//折换成usdt价格
	}
}
