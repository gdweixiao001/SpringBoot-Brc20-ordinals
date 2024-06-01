package com.ruoyi.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.utils.blade.Func;
import com.ruoyi.common.utils.blade.tool.DateUtil;


import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class MjkjUtils {

	public static Map<String, String> syncUbwTradeMap = new HashMap<>();

	public static String WAV=".wav";
	/**
	 * 获取map值
	 * (map的value是单个的数据时使用)
	 *
	 * @param map 查询封装的一条数据
	 * @param key 高查条件的key
	 * @return
	 */
	public static String getMap2Str(Map<String, Object> map, String key) {
		return getMap2Str(map,key,"");
	}
	public static String getMap2Str(Map<String, Object> map, String key,String defaultStr) {
		// 参数校验:
		if (Func.isEmpty(key) || Func.isEmpty(map)) {
			return defaultStr;
		}
		// 根据高查条件的key获取value
		Object o = map.get(key);
		if (Func.isEmpty(o)) {
			return defaultStr;
		}
		// object -> String
		String value = Func.toStr(o);
		// 返回
		return value;
	}


	/**
	 * 获取map值
	 *
	 * @param map 查询封装的一条数据
	 * @param key 高查条件的key
	 * @return
	 */
	public static <T> List<T> getMapAll2List(Map<String, Object> map, String key) {
		// 参数校验:
		if (Func.isEmpty(key) || Func.isEmpty(map)) {
			return Collections.emptyList();
		}

		Object o = map.get(key);
		// 根据高查条件的key获取value
		if (o instanceof List)
			return (List<T>) o;
		T[] arr = (T[]) Func.toStr(o).replaceAll("[\\[\\]()]", "").split(",");
		return Arrays.asList(arr);
	}


	public static String getMap2DateTimeStr(Map<String, Object> map, String key,String fomat,String defaultStr) {
		Date date = getMap2DateTime(map, key);
		if(Func.isNotEmpty(date)){
			return DateUtil.format(date, fomat);
		}
		return defaultStr;
	}
	/**
	 * 获取map值
	 * (map的value是单个的数据时使用)
	 *
	 * @param map 查询封装的一条数据
	 * @param key 高查条件的key
	 * @return
	 */
	public static Date getMap2DateTime(Map<String, Object> map, String key) {
		// 参数校验:
		if (Func.isEmpty(key) || Func.isEmpty(map)) {
			return null;
		}
		// 根据高查条件的key获取value
		Object o = map.get(key);
		if (Func.isEmpty(o)) {
			return null;
		}
		if (o instanceof Date) {
			if (o instanceof java.sql.Date || o instanceof Time) {
				return new Date(((Date) o).getTime());
			}
			return (Date) o;
		}
		if (o instanceof LocalDateTime) {
			LocalDateTime localDateTime = (LocalDateTime) o;
			Date date = Date.from(localDateTime.atZone( ZoneId.systemDefault()).toInstant());
			return date;
		}
		// object -> String
		String value = Func.toStr(o);
		return DateUtil.parse(value, DateUtil.DATETIME_FORMAT);
	}

	/**
	 * 是否为昨天
	 *
	 * @param map 查询封装的一条数据
	 * @param key 高查条件的key
	 * @return
	 */
	public static boolean isYesterday(Map<String, Object> map, String key) {
		// 参数校验:
		if (Func.isEmpty(key) || Func.isEmpty(map)) {
			return false;
		}
		// 根据高查条件的key获取value
		Object o = map.get(key);
		if (Func.isEmpty(o)) {
			return false;
		}
		if (o instanceof Date)
			return cn.hutool.core.date.DateUtil.isSameDay(cn.hutool.core.date.DateUtil.yesterday(), (Date) o);
		// object -> String
		String value = Func.toStr(o);
		return cn.hutool.core.date.DateUtil.isSameDay(cn.hutool.core.date.DateUtil.yesterday(), DateUtil.parse(value, DateUtil.DATETIME_FORMAT));
	}

	/**
	 * 指定日期是否为三天内
	 *
	 * @param map 查询封装的一条数据
	 * @param key 高查条件的key
	 * @return
	 */
	public static boolean isBetweenThirdDays(Map<String, Object> map, String key) {
		// 参数校验:
		if (Func.isEmpty(key) || Func.isEmpty(map)) {
			return false;
		}
		// 根据高查条件的key获取value
		Object o = map.get(key);
		if (Func.isEmpty(o)) {
			return false;
		}
		if (o instanceof Date)
			return cn.hutool.core.date.DateUtil.betweenDay(cn.hutool.core.date.DateUtil.date(), (Date) o, false) < 3;
		// object -> String
		String value = Func.toStr(o);
		return cn.hutool.core.date.DateUtil.betweenDay(cn.hutool.core.date.DateUtil.date(), DateUtil.parse(value, DateUtil.DATETIME_FORMAT), false) < 3;
	}

	/**
	 * 指定日期是否为一周内
	 *
	 * @param map 查询封装的一条数据
	 * @param key 高查条件的key
	 * @return
	 */
	public static boolean isBetweenWeek(Map<String, Object> map, String key) {
		// 参数校验:
		if (Func.isEmpty(key) || Func.isEmpty(map)) {
			return false;
		}
		// 根据高查条件的key获取value
		Object o = map.get(key);
		if (Func.isEmpty(o)) {
			return false;
		}
		if (o instanceof Date)
			return cn.hutool.core.date.DateUtil.betweenWeek(cn.hutool.core.date.DateUtil.date(), (Date) o, false) == 0;
		// object -> String
		String value = Func.toStr(o);
		return cn.hutool.core.date.DateUtil.betweenWeek(cn.hutool.core.date.DateUtil.date(), DateUtil.parse(value, DateUtil.DATETIME_FORMAT), false) == 0;
	}

	/**
	 * 指定日期是否为一月内
	 *
	 * @param map 查询封装的一条数据
	 * @param key 高查条件的key
	 * @return
	 */
	public static boolean isBetweenMonth(Map<String, Object> map, String key) {
		// 参数校验:
		if (Func.isEmpty(key) || Func.isEmpty(map)) {
			return false;
		}
		// 根据高查条件的key获取value
		Object o = map.get(key);
		if (Func.isEmpty(o)) {
			return false;
		}
		if (o instanceof Date)
			return cn.hutool.core.date.DateUtil.betweenMonth(cn.hutool.core.date.DateUtil.date(), (Date) o, false) == 0;
		// object -> String
		String value = Func.toStr(o);
		return cn.hutool.core.date.DateUtil.betweenMonth(cn.hutool.core.date.DateUtil.date(), DateUtil.parse(value, DateUtil.DATETIME_FORMAT), false) == 0;
	}

	/**
	 * 获取map值
	 * (map的value是单个的数据时使用)
	 *
	 * @param map 查询封装的一条数据
	 * @param key 高查条件的key
	 * @return
	 */
	public static boolean contains(Map<String, Object> map, String key) {
		// 参数校验:
		if (Func.isEmpty(key) || Func.isEmpty(map)) {
			return false;
		}
		if (map.containsKey(key) && !Func.toStr(map.get(key)).equals(""))
			return true;
		return false;
	}

	/**
	 * 获取map值
	 * (map的value是单个的数据时使用)
	 *
	 * @param map 查询封装的一条数据
	 * @param key 高查条件的key
	 * @return
	 */
	public static BigDecimal getMap2BigD(Map<String, Object> map, String key) {
		// 参数校验:
		if (Func.isEmpty(key) || Func.isEmpty(map)) {
			return BigDecimal.ZERO;
		}
		Object o = map.get(key);
		if (Func.isEmpty(o)) {
			return BigDecimal.ZERO;
		}
		// object -> BigDecimal
		BigDecimal bigDecimal = new BigDecimal(Func.toStr(o));
		// 返回
		return bigDecimal;
	}

	/**
	 * 获取map值
	 * (map的value是单个的数据时使用)
	 *
	 * @param map 查询封装的一条数据
	 * @param key 高查条件的key
	 * @return
	 */
	public static String getMap2BigDfomat(Map<String, Object> map, String key,Integer scale,String endStr) {
		// 参数校验:
		if (Func.isEmpty(key) || Func.isEmpty(map)) {
			return "-";
		}
		Object o = map.get(key);
		if (Func.isEmpty(o)) {
			return "-";
		}
		// object -> BigDecimal
		BigDecimal bigDecimal = new BigDecimal(Func.toStr(o));

		if(Func.isNotEmpty(endStr)){
			if(bigDecimal.compareTo(BigDecimal.ZERO)==0){
				return "-";
			}
		}

		// 返回
		return bigDecimal.setScale(scale,BigDecimal.ROUND_UP).stripTrailingZeros().toPlainString()+" "+endStr;
	}

	public static BigDecimal getBigDSafe(Map<?, BigDecimal> map, Object key) {
		// 参数校验:
		if (Func.isEmpty(key) || Func.isEmpty(map)) {
			return BigDecimal.ZERO;
		}
		Object o = map.get(key);
		if (Func.isEmpty(o)) {
			return BigDecimal.ZERO;
		}
		return map.get(key);
	}

	public static BigDecimal getBigDecimalDefaultOne(Map<?, Object> map, Object key) {
		if (Func.isEmpty(key) || Func.isEmpty(map)) {
			return BigDecimal.ONE;
		}
		Object o = map.get(key);
		if (Func.isEmpty(o)) {
			return BigDecimal.ONE;
		}
		// object -> BigDecimal
		BigDecimal bigDecimal = new BigDecimal(Func.toStr(o));
		// 返回
		return bigDecimal;
	}

	/**
	 * 获取map值
	 *
	 * @param map
	 * @param key
	 * @return
	 */
	public static Long getMap2Long(Map<String, Object> map, String key) {
		String str = getMap2Str(map, key);
		if (Func.isEmpty(str)) {
			return -1L;
		}
		return Func.toLong(str);
	}

	/**
	 * 获取map值
	 *
	 * @param map
	 * @param key
	 * @return
	 */
	public static Double getMap2Double(Map<String, Object> map, String key) {
		String str = getMap2Str(map, key);
		if (Func.isEmpty(str)) {
			return -1D;
		}
		return Func.toDouble(str);
	}

	/**
	 * 获取map值
	 *
	 * @param map
	 * @param key
	 * @return
	 */
	public static Integer getMap2Integer(Map<String, Object> map, String key) {
		String str = getMap2Str(map, key);
		if (Func.isEmpty(str)) {
			return -1;
		}
		return Func.toInt(str);
	}

	/**
	 * 获取map值
	 *
	 * @param map
	 * @param key
	 * @return
	 */
	public static Map<String, Object> getMap2Map(Map<String, Object> map, String key) {
		Map<String, Object> map2 = new HashMap<>();
		// 参数校验:
		if (Func.isEmpty(key) || Func.isEmpty(map) || Func.isEmpty(map.get(key))) {
			return map2;
		}
		map2 = (Map<String, Object>) map.get(key);
		return map2;
	}


	/**
	 * 获取map值
	 *
	 * @param map
	 * @param key
	 * @return
	 */
	public static List<Map<String, Object>> getMap2List1(Map<String, Object> map, String key) {
		List<Map<String, Object>> list = new ArrayList<>();
		// 参数校验:
		if (Func.isEmpty(key) || Func.isEmpty(map) || Func.isEmpty(map.get(key))) {
			return list;
		}
		list = (List<Map<String, Object>>) map.get(key);
		return list;
	}

	/**
	 * 获取分页信息
	 *
	 * @param params
	 * @return 为空则不分页
	 */
	public static Page getPage(Map<String, Object> params) {

		Integer pageNo = params.get("pageNo") == null ? 1 : Integer.parseInt(params.get("pageNo").toString());
		Integer pageSzie = params.get("pageSize") == null ? 10 : Integer.parseInt(params.get("pageSize").toString());

		if (pageSzie == -521) {
			Page<Map<String, Object>> page = new Page<>(pageNo, pageSzie);
			return page;
		}
		Page<Map<String, Object>> page = new Page<>(pageNo, pageSzie);
		return page;
	}

	/**
	 * 获取分页信息
	 *
	 * @return 为空则不分页
	 */
	public static Page getPage(Integer pageNo, Integer pageSize) {
		if(Func.isEmpty(pageNo)){
			pageNo=1;
		}
		if(Func.isEmpty(pageSize)){
			pageSize=10;
		}

		if (pageSize == -521) {
			Page<Map<String, Object>> page = new Page<>(pageNo, pageSize);
			return page;
		}
		Page<Map<String, Object>> page = new Page<>(pageNo, pageSize);
		return page;
	}

	/**
	 * 设置分页返回结果
	 *
	 * @param params
	 * @param ipage
	 */
	public static void setPageResult(Map<String, Object> params, IPage ipage) {
		params.put("dataTotal", ipage.getTotal());
		params.put("dataRecords", ipage.getRecords());
	}

	public static void setPageResult(Map<String, Object> params, IPage ipage, Map<String, Object> otherMap) {
		params.put("dataTotal", ipage.getTotal());
		params.put("dataRecords", ipage.getRecords());
		params.put("dataOther", otherMap);
	}

	/**
	 * 封装查询条件
	 *
	 * @param obj 字段，值 ...
	 * @return
	 */
	public static QueryWrapper queryWrapper(Object... obj) {
		if (obj.length % 2 != 0)
			throw new RuntimeException("参数个数错误！");
		QueryWrapper queryWrapper = new QueryWrapper();
		for (int i = 0; i < obj.length / 2; i++) {
			queryWrapper.eq(obj[i * 2], obj[(i * 2 + 1)]);
		}
		return queryWrapper;
	}

	/**
	 * 使用stream流去比较两个数组是否相等
	 * 方法5
	 */
	public static boolean checkDiffrent4(List<String> list, List<String> list1) {
		long st = System.nanoTime();
		/** 先将集合转成stream流进行排序然后转成字符串进行比较 */
		return list.stream().sorted().collect(Collectors.joining())
			.equals(list1.stream().sorted().collect(Collectors.joining()));
	}

	/**
	 * 计算价格
	 *
	 * @param quoteSetting
	 * @return
	 */
	public static BigDecimal calcAmount(JSONArray quoteSetting, BigDecimal amount) {
		if (null == quoteSetting) {
			return amount;
		}
		BigDecimal coefficient = quoteSetting.getBigDecimal(1);
		if (null == coefficient) {
			return amount;
		}
		BigDecimal calcAmount = amount.multiply(coefficient).setScale(6, BigDecimal.ROUND_UP);
		if (calcAmount.compareTo(new BigDecimal("0")) == 0) {
			return amount;
		}
		return calcAmount.stripTrailingZeros();
	}

	public static String getId(JSONObject jsonObject) {
		String id = "";
		if (jsonObject.containsKey("ch")) {
			id = jsonObject.getString("ch");
			if (id == null || id.split("\\.").length < 3) {
				return null;
			}
		}
		if (jsonObject.containsKey("rep")) {
			id = jsonObject.getString("rep");
			if (id == null || id.split("\\.").length < 3) {
				return null;
			}
		}
		if (id.equals("")) {
			return null;
		}
		return id;
	}

	public static long getSecond(String period) {
		long second = 0;
		if (period.equals("1min")) {
			second = 1 * 60L;
		} else if (period.equals("5min")) {
			second = 5 * 60L;
		} else if (period.equals("15min")) {
			second = 15 * 60L;
		} else if (period.equals("30min")) {
			second = 30 * 60L;
		} else if (period.equals("60min")) {
			second = 60 * 60L;
		} else if (period.equals("4hour")) {
			second = 4 * 3600L;
		} else if (period.equals("1day")) {
			second = 24 * 3600L;
		} else if (period.equals("1mon")) {
			second = 30 * 24 * 3600L;
		} else if (period.equals("1week")) {
			second = 7 * 24 * 3600L;
		}
		return second;
	}

	//获取7天后
	public static Date get7Day(Date now) {
		String format = DateUtil.format(now, "yyyy-MM-dd HH");
		format = format + ":00:00";
		Date newDate = DateUtil.parse(format, DateUtil.PATTERN_DATETIME);
		Date result = DateUtil.plusDays(newDate, 7);
		return result;
	}

	public static long getTimeSubHour(Date startDate, Date endDate) {
		//String startDateStr = DateUtil.format(startDate, "yyyy-MM-dd HH");
		//startDateStr=startDateStr+":00:00";
		//startDate = DateUtil.parse(startDateStr, "yyyy-MM-dd HH:mm:ss");

		long diff = endDate.getTime() - startDate.getTime();
		long hours = diff / 60 / 60 / 1000;
		hours = hours + 1;//向上取整
		return hours;
	}


	public static Map<String, Map<String, Object>> list2Map(List<Map<String, Object>> dataList, String key) {
		Map<String, Map<String, Object>> resultMap = new HashMap<>();
		if (Func.isNotEmpty(dataList)) {
			dataList.forEach(map -> {
				resultMap.put(Func.toStr(map.get(key)), map);
			});
		}
		return resultMap;
	}


	public static Map<String, Map<String, Object>> list2Map2UpperCase(List<Map<String, Object>> dataList, String key) {
		Map<String, Map<String, Object>> resultMap = new HashMap<>();
		if (Func.isNotEmpty(dataList)) {
			dataList.forEach(map -> {
				resultMap.put(Func.toStr(map.get(key)).toUpperCase(), map);
			});
		}
		return resultMap;
	}




	public static boolean getMap2Boolean(Map<String, Object> map, String key) {
		if (Func.isEmpty(map))
			return false;
		if (!map.containsKey(key))
			return false;
		return Func.toBoolean(map.get(key));
	}

	public static <T> List<T> mergeList(List<T> dataList, List<T> dataList2) {
		ArrayList<T> list = new ArrayList<>();
		if (Func.isNotEmpty(dataList))
			list.addAll(dataList);
		if (Func.isNotEmpty(dataList2))
			list.addAll(dataList2);
		return list;
	}

	/**
	 * 获取小数位
	 *
	 * @param num
	 * @return
	 */
	public static Integer getDecimalNum(BigDecimal num) {
		String str = num.stripTrailingZeros().toPlainString();
		if (str.indexOf(".") != -1) {//是小数
			String subStr = str.split("\\.")[1];
			return subStr.length();
		} else {
			return 0;
		}
	}


	/**
	 * 清空列表
	 *
	 * @param list
	 */
	public static void clearList(List<Map<String, Object>> list) {
		if (Func.isNotEmpty(list)) {
			list.clear();
		}
	}


	/**
	 * 数据脱敏
	 * @return
	 */
	public static String desensitizeStr(String str,String nullDfStr){
		if(Func.isEmpty(str)){
			return nullDfStr;
		}
		if(str.length()==1){
			return nullDfStr;
		}else if(str.length()==2){
			return str.substring(0,1)+"*";
		}else if(str.length()==3){
			return str.substring(0,1)+"*"+str.substring(2,3);
		}else{
			return str.substring(0,1)+"**"+str.substring(str.length()-1,str.length());
		}
	}

	//缓存
	public static String getRewardAdvertRedisKey(Long bladeUserId,boolean isLikeFlag){
		Date now = DateUtil.now();
		String yyyyMMdd = DateUtil.format(now, DateUtil.PATTERN_DATE);
		String redisKey="";
		if(isLikeFlag){
			redisKey="REWARD_ADVERT:"+yyyyMMdd+":"+bladeUserId+":*";
		}else{
			redisKey="REWARD_ADVERT:"+yyyyMMdd+":"+bladeUserId+":"+ IdWorker.getIdStr();
		}
		return redisKey;
	}

	//缓存
	public static String getRedisKeyMoreFun(String wxuserId,String type){
		String redisKey="MORE_FUN:WXUSER_ID_"+wxuserId+":TYPE_"+type;
		return redisKey;
	}


	//获取key
	public static String getRedisKeyWuserId(Long bladeUserId){
		String redisKey="WXUSER:WUSER_ID:"+bladeUserId;
		return redisKey;
	}
	//获取key
	public static String getRedisKeyChatCode(Long bladeUserId){
		String redisKey="WXUSER:CHAT_CODE:"+bladeUserId;
		return redisKey;
	}

	//获取签到
	public static String getRedisKeySign(Long bladeUserId,Date date){
		String yyyyMMdd = DateUtil.format(date, DateUtil.PATTERN_DATE);

		String redisKey="WXUSER:SIGN:"+bladeUserId+":yyyyMMdd_"+yyyyMMdd;
		return redisKey;
	}

	/**
	 * 获取周几
	 * @param date
	 * @return
	 */
	public static String getWeek(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int weekIdx = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		switch (weekIdx) {
			case 1:
				return "星期一";
			case 2:
				return "星期二";
			case 3:
				return "星期三";
			case 4:
				return "星期四";
			case 5:
				return "星期五";
			case 6:
				return "星期六";
			default:
				return "星期日";
		}
	}


	public static List<String> getDateWeek() {
		Date now = DateUtil.now();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);

		int b = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (b == 0) {
			b = 7;
		}
		Date fdate;
		List<String> list = new ArrayList();
		Long fTime = now.getTime() - b * 24 * 3600000;
		for (int a = 1; a <= 7; a++) {
			fdate = new Date();
			fdate.setTime(fTime + (a * 24 * 3600000));
			String fdateStr = DateUtil.format(fdate, DateUtil.PATTERN_DATE);
			list.add(a - 1,fdateStr );
		}
		return list;
	}





	//获取查询数据
	public static QueryWrapper getQueryWrapper(){
		QueryWrapper<Object> wrapper = new QueryWrapper<>();
		wrapper.eq("is_deleted", 0);
		return wrapper;
	}


	public static String formatBigNum(BigDecimal val,Integer scale,String remark){
		if(Func.isEmpty(val) || val.compareTo(BigDecimal.ZERO)!=1){
			return "-";
		}
		return val.setScale(scale,BigDecimal.ROUND_UP).stripTrailingZeros().toPlainString()+remark;

	}

	/**
	 * 获取参数
	 *
	 * @param request
	 * @return
	 */
	public static Map<String, Object> getParameterMap(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<>();

		//获取所有参数
		Map<String, String[]> parameterMap = request.getParameterMap();
		Iterator<Map.Entry<String, String[]>> iterator = parameterMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String[]> map = iterator.next();

			String key = map.getKey();
			String[] values = map.getValue();

			// 字符串计数器
			String vauleStr = "";
			if (Func.isNotEmpty(values)) {
				// value不是字符数组,转成字符串
				if (!(values instanceof String[])) {
					vauleStr = values.toString();
				} else {
					for (String val : values) {
						vauleStr = val + ",";
					}
					vauleStr = vauleStr.substring(0, vauleStr.length() - 1);
				}
			} else {
				vauleStr = "";
			}
			// 重新封装请求参数:
			resultMap.put(key, vauleStr);
		}

		if (request.getMethod().equalsIgnoreCase("POST")) {
			try {
				BufferedReader reader = request.getReader();
				StringBuilder sb = new StringBuilder();
				String line;
				while (null != (line = reader.readLine())) {
					sb.append(line);
				}
				Map<String, Object> readMap = JSONObject.parseObject(sb.toString(), Map.class);

				if (Func.isNotEmpty(readMap)) {
					readMap.forEach((k,v) ->
							resultMap.put(k, Func.isNotEmpty(v) && v.toString().startsWith("[") ?
									v.toString().replace("[", "").replace("]", "") :
									v.toString()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultMap;
	}
}
