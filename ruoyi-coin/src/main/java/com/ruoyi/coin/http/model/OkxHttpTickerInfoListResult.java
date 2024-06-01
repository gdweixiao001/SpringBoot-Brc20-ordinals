package com.ruoyi.coin.http.model;

import lombok.Data;

import java.util.List;

/**
 * 铭文详情
 */
@Data
public class OkxHttpTickerInfoListResult {
	private Integer code;
	private DataModel data;

	/**
	 * 铭文详情
	 */
	@Data
	public static class DataModel{
		private List<OkxTickerInfo> list;
	}

}
