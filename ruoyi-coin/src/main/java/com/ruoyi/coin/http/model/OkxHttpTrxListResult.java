package com.ruoyi.coin.http.model;

import lombok.Data;

import java.util.List;

@Data
public class OkxHttpTrxListResult {
	private Integer code;
	private OkxTransactionInfoModel data;


	/**
	 * 交易明细
	 */
	@Data
	public static class OkxTransactionInfoModel{
		private List<OkxTransactionInfo> activityList;
		private Boolean hasNext;
		private String cursor;
	}

}
