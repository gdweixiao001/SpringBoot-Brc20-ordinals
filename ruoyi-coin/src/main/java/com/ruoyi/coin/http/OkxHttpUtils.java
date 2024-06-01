package com.ruoyi.coin.http;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ruoyi.coin.http.model.*;
import com.ruoyi.common.utils.MjkjUtils;
import com.ruoyi.common.utils.blade.Func;


import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求工具类
 */
public class OkxHttpUtils {

	public static void main(String[] args) {
		//https://www.lgdquant.com/okx-unrestricted-website/
		//获取交易明细
		//getTransactionList("wzrd",5,20);
		//getTickInfo(1);
		//List<OkxTickerInfo> list = getTickInfo("wzrd", 1, 0);
		//System.out.println(list);
		////OkxHttpMarketListResult.OkxDataModel model = getMarketList("wzrd", 1, null, 1);
		//System.out.println(model);

/*		https://www.mingjianyiren.com

		或

		https://www.nuancebot.com

		或

		https://www.nuancebot.com

		或

		https://www.nuancecode.com

		或

		https://www.kjgklfh.com

		或

		https://www.jasjdgh.com*/

		//List<OkxTickerInfo> hotTickInfo = getHotTickInfo("https://www.jasjdgh.com");
		//System.out.println(JSONUtil.toJsonStr(hotTickInfo));

		String url="https://www.mingjianyiren.com/priapi/v1/nft/inscription/rc20/tokens/PUPS%20?tickerType=0&token=PUPS ";
		String s = HttpUtil.get(url, Charset.defaultCharset());
		System.out.println(s);
	}




	//获取热门铭文
	public static List<OkxTickerInfo> getHotTickInfo(String hostUrl) {
		//String hostUrl="https://www.mingjianyiren.com";
		//设置代理
		//setProxy();
		//https://www.okx.com/priapi/v1/nft/inscription/rc20/tokens?
		//scope=4&page=1&size=50&sortBy=volume&sort=desc&tokensLike=&timeType=1&tickerType=0&walletAddress=&t=1714712979836


		String url = hostUrl+"/priapi/v1/nft/inscription/rc20/tokens";
		Map<String, Object> param = new HashMap<>();
		param.put("page", 1);//分页
		param.put("size", 6);//每页大小
		param.put("tickerType", 0);
		param.put("sortBy", "volume");//部署时间
		param.put("sort", "desc");//降序
		param.put("timeType", 1);//小时
		param.put("scope",4);

		String json = HttpUtil.get(url, param);

		OkxHttpTickerInfoListResult result = JSONUtil.toBean(json, OkxHttpTickerInfoListResult.class);
		if (result.getCode() != 0) {//状态不符合
			return null;
		}
		OkxHttpTickerInfoListResult.DataModel data = result.getData();
		if (Func.isEmpty(data)) {
			return null;
		}
		List<OkxTickerInfo> list = data.getList();
		if (Func.isEmpty(list)) {
			return null;
		}
		//有数据，进行处理
		return list;
	}

	/**
	 * OkxTickerInfo
	 * 获取币种详情
	 *
	 * @param page
	 */
	/**
	 *
	 * @param symbolName 铭文名称 模糊
	 * @param page
	 * @param timeType 0=全部 1=24小时  2=7天  3=30天
	 * @return
	 */
	public static List<OkxTickerInfo> getTickList(String hostUrl,String symbolName,Integer page,String orderBy) {
		//设置代理
		//setProxy();

		String url = hostUrl+"/priapi/v1/nft/inscription/rc20/tokens";
		Map<String, Object> param = new HashMap<>();
		param.put("page", page);//分页
		param.put("size", 50);//每页大小
		if(Func.isNotEmpty(symbolName)){
			param.put("tokensLike",symbolName);
		}

		param.put("timeType",0);//0全部

		param.put("tickerType", 0);
		param.put("sortBy", "deployedTime");//部署时间
		param.put("sort", orderBy);//升序


		String json = HttpUtil.get(url, param);

		OkxHttpTickerInfoListResult result = JSONUtil.toBean(json, OkxHttpTickerInfoListResult.class);
		if (result.getCode() != 0) {//状态不符合
			return null;
		}
		OkxHttpTickerInfoListResult.DataModel data = result.getData();
		if (Func.isEmpty(data)) {
			return null;
		}
		List<OkxTickerInfo> list = data.getList();
		if (Func.isEmpty(list)) {
			return null;
		}
		//有数据，进行处理
		return list;

	}

	public static OkxHttpTickerInfoResult.OkxTickerInfo getTickInfo(String hostUrl,String symbolName) {
		//设置代理
		//setProxy();

		//https://www.okx.com/priapi/v1/nft/inscription/rc20/tokens/BTBD?token=BTBD&tickerId=&walletAddress=&tickerType=0&t=1714726886583

		symbolName = URLUtil.encode(symbolName);


		String url = hostUrl+"/priapi/v1/nft/inscription/rc20/tokens/"+symbolName;
		Map<String, Object> param = new HashMap<>();
		param.put("token",symbolName);
		param.put("tickerType", 0);

		/*String paramStr = HttpUtil.toParams(param);
		String tmpUrl=url+"?"+paramStr;*/
		String json = HttpUtil.get(url,param);

		OkxHttpTickerInfoResult result = JSONUtil.toBean(json, OkxHttpTickerInfoResult.class);
		if (result.getCode() != 0) {//状态不符合
			return null;
		}
		OkxHttpTickerInfoResult.OkxTickerInfo data = result.getData();
		if (Func.isEmpty(data)) {
			return null;
		}
		//有数据，进行处理
		return data;

	}


	/**
	 * 获取交易列表
	 *
	 * @param tickName     铭文符号
	 * @param trxType      5=成交  22=下架  3=降价  21=上架 6=转移
	 * @param timeBoundary 大于某一个时间
	 */
	public static OkxHttpTrxListResult.OkxTransactionInfoModel getTransactionList(String hostUrl,String tickName, Integer trxType, Long timeBoundary, String cursor) {

		//设置代理
		//setProxy();
		tickName = URLUtil.encode(tickName);

		String url = hostUrl+"/priapi/v1/nft/inscription/rc20/detail/activity";

		Map<String, Object> param = new HashMap<>();
		param.put("tick", tickName);
		param.put("ticker", tickName);//币种
		param.put("type", trxType);
		param.put("tickerType", "0");
		if (Func.isNotEmpty(timeBoundary)) {
			param.put("timeBoundary", timeBoundary);
		}
		if (Func.isNotEmpty(cursor)) {
			param.put("cursor", cursor);
		}
		param.put("pageSize", 50);
		String json = HttpUtil.get(url, param);

		OkxHttpTrxListResult result = JSONUtil.toBean(json, OkxHttpTrxListResult.class);
		if (result.getCode() != 0) {//状态不符合
			return null;
		}
		OkxHttpTrxListResult.OkxTransactionInfoModel data = result.getData();
		if (Func.isEmpty(data)) {
			return null;
		}
		List<OkxTransactionInfo> transactionInfoList = data.getActivityList();
		if (Func.isEmpty(transactionInfoList)) {
			return null;
		}
		//有数据，进行处理
		return data;
	}


	/**
	 * 获取持币地址
	 *
	 * @param tickName
	 * @param page
	 * @return
	 */
	public static List<OkxHttpHoderResult.ownersModel> getHoderList(String hostUrl,String tickName, Integer page) {
		tickName = URLUtil.encode(tickName);
		//设置代理
		//setProxy();
		String url = hostUrl+"/priapi/v1/nft/inscription/rc20/detail/analytics/owners";

		Map<String, Object> param = new HashMap<>();
		param.put("pageIndex", page);
		param.put("pageSize", 50);
		param.put("ticker", tickName);//币种
		param.put("tickerType", "0");
		String json = HttpUtil.get(url, param);

		OkxHttpHoderResult result = JSONUtil.toBean(json, OkxHttpHoderResult.class);
		if (result.getCode() != 0) {//状态不符合
			return null;
		}
		OkxHttpHoderResult.OkxHoderInfoModel data = result.getData();

		if (Func.isEmpty(data)) {
			return null;
		}
		List<OkxHttpHoderResult.ownersModel> list = data.getOwners();

		if (Func.isEmpty(list)) {
			return null;
		}
		//有数据，进行处理
		return list;
	}

	/**
	 * 获取市场挂单
	 * @param tickName
	 * @param orderType 1=价格从低到高  2=价格从高到低  5=总价从低到高 6=总价从高到低  3=最新上架
	 * @param cursor
	 * @param page
	 * @return
	 */
	public static OkxHttpMarketListResult.OkxDataModel getMarketList(String hostUrl,String tickName, Integer orderType, String cursor, Integer page) {
		try{
			//设置代理
			//setProxy();
			String url = hostUrl+"/priapi/v1/nft/inscription/ordi-rc20/detail/items";

			tickName = URLUtil.encode(tickName);


			Map<String, Object> param = new HashMap<>();
			param.put("pageNum", page);
			param.put("pageSize", 50);
			param.put("ticker", tickName);//币种
			param.put("tickerType", "0");
			param.put("orderType", orderType);
			if (Func.isNotEmpty(cursor)) {
				param.put("cursor", cursor);
			}
			String json = HttpUtil.get(url, param);


			OkxHttpMarketListResult result = JSONUtil.toBean(json, OkxHttpMarketListResult.class);
			if (result.getCode() != 0) {//状态不符合
				return null;
			}
			OkxHttpMarketListResult.OkxDataModel data = result.getData();

			if (Func.isEmpty(data)) {
				return null;
			}
			List<OkxHttpMarketListResult.ItemModel> items = data.getItems();

			if (Func.isEmpty(items)) {
				return null;
			}
			//有数据，进行处理
			items.stream().forEach(item -> {
				OkxHttpMarketListResult.TotalPriceModel total = item.getTotalPrice();
				if (Func.isNotEmpty(total)) {//处理位数
					BigDecimal totalBtc = new BigDecimal(total.getPrice());
					item.setTotalBtc(MjkjUtils.formatBigNum(totalBtc, 4, ""));

					BigDecimal totalUset = new BigDecimal(total.getUsdPrice());
					item.setTotalUsdt(MjkjUtils.formatBigNum(totalUset, 2, ""));
				}


				OkxHttpMarketListResult.UnitPriceModel unitPriceModel = item.getUnitPrice();
				if (Func.isNotEmpty(unitPriceModel)) {//处理位数
					BigDecimal satPrice = new BigDecimal(unitPriceModel.getSatPrice());
					item.setOnePrice(MjkjUtils.formatBigNum(satPrice, 4, ""));
				}

				item.setLinkUrl("https://www.okx.com/zh-hans/web3/marketplace/inscription/ordinals/token/" + item.getTicker());
			});

			return data;
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}


	//设置代理
	public static void setProxy() {
		System.setProperty("jdk.tls.useExtendedMasterSecret", "false");//设置环境变量
		String proxy = "127.0.0.1";
		int port = 10809;
		System.setProperty("proxyType", "4");
		System.setProperty("proxyPort", Integer.toString(port));
		System.setProperty("proxyHost", proxy);
		System.setProperty("proxySet", "true");
	}

}
