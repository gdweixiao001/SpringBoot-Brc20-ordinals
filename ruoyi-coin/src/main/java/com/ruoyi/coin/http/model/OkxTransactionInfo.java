package com.ruoyi.coin.http.model;

import lombok.Data;

@Data
public class OkxTransactionInfo {

	private String chain;//链路类型

	private String amount;//数量
	private Long createOn;//成交时间
	private String from;//卖出者
	private String to;//买入者
	private String status;//状态值：1=成功  2=进行中

	private String ticker;//铭文
	private String tickerIcon;//铭文图标
	private String tickerType;//暂时不知道

	private String contractAddress;//合约地址

	private String txHash;//交易哈希

	private String platformIcon;//客户端图标
	private String platformName;//客户端名称 也就是哪里购买的

	private TotalPriceModel totalPrice;//总价相关
	private UnitPriceModel unitPrice;//单价相关

	private String inscriptionNum;//铭文高度

	private String nftId;//nftId
	private String project;//项目名称
	private String tokenId;//tokenId

	//成交类型
	private String type; //5=成交  22=下架  3=降价  21=上架 6=转移
	private String typeIcon;
	private String typeName;//SALE=成交   CANCEL_LIST=下架  UPDATE_PRICE=降价  LIST=上架  TRANSFER=转移

	//总价相关
	@Data
	public static class TotalPriceModel{
		private String currency;//币种
		private String currencyUrl;//币种名称
		private String price;//btc总量
		private String satPrice;//总量
		private String usdPrice;//折换成usdt量
	}

	//单价相关
	@Data
	public static class UnitPriceModel{
		private String currency;//币种
		private String currencyUrl;//币种名称
		private String price;//btc单价
		private String satPrice;//单价 TotalPriceModel.satPrice / UnitPriceModel.satPrice  =铭文数量
		private String usdPrice;//折换成usdt价格
	}


}

