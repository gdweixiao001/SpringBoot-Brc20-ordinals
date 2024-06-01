package com.ruoyi.coin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

/**
 * 铭文定制
 */
public interface ICoinSqlService {


	//根据表属性获取所有数据 多条件 分页
	IPage<Map<String, Object>> getCoinSymbolPage(IPage page, Map<String, Object> params,Long userId);

	//获取持币地址
	IPage<Map<String, Object>> getCoinAddressPage(IPage page, Map<String, Object> params);

	//获取交易信息
	IPage<Map<String, Object>> getCoinTxPage(IPage page, Map<String, Object> params);

	//获取社区信息
	IPage<Map<String, Object>> getCoinPiazzaPage(IPage page, Map<String, Object> params,Long nowUserId);

	//获取评论列表
	List<Map<String,Object>> getCoinPiazzaReplyList(String piazzaId);

	//获取成员列表
	IPage<Map<String, Object>> getCoinMemberPage(IPage page,Integer symbolId);

	//获取发展历程列表
	List<Map<String,Object>> getEventMapList(Integer symbolId,List<String> initList);

	//获取相同创建
	IPage<Map<String, Object>> getCoiSameCreateUserPage(IPage page,Long userId, Integer symbolId, String createUserAddress);

	//是否是超级管理员
	boolean isSysAdmin(Long userId);

	//判断某个人是否是vip
	boolean isVip(Long userId);

	//是否有加入某一个币种社区里面
	boolean isSymbolMember(Long userId,Integer symbolId);

	boolean isSymbolAdmin(Long userId,Integer symbolId);
}
