package com.ruoyi.coin.http.model;

import lombok.Data;

import java.util.List;

@Data
public class OkxHttpHoderResult {
	private Integer code;
	private OkxHoderInfoModel data;


	/**
	 * 交易明细
	 */
	@Data
	public static class OkxHoderInfoModel{
		private List<ownersModel> owners;
	}

	@Data
	public static class ownersModel{
		private String amount;
		private String ownerAddress;
		private String ratio;
	}

}
