package com.ruoyi.coin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 铭文
 */
public interface CoinMapper {

	//获取铭文列表
	IPage<Map<String, Object>> getCoinSymbolPage(@Param("page") IPage<Map<String, Object>> page, @Param("params") Map<String, Object> params);

	//获取持币地址
	IPage<Map<String, Object>> getCoinAddressPage(@Param("page") IPage<Map<String, Object>> page, @Param("params") Map<String, Object> params);

	//获取持币地址
	IPage<Map<String, Object>> getCoinTxPage(@Param("page") IPage<Map<String, Object>> page, @Param("params") Map<String, Object> params);

	//获取社区信息
	IPage<Map<String, Object>> getCoinPiazzaPage(IPage page, Map<String, Object> params);

	//获取评论列表
	List<Map<String,Object>> getCoinPiazzaReplyList(String piazzaId);

	//获取成员列表
	IPage<Map<String, Object>> getCoinMemberPage(@Param("page") IPage<Map<String, Object>> page, Integer symbolId);

	//获取相同创建
	IPage<Map<String, Object>> getCoiSameCreateUserPage(IPage page,Integer symbolId, String createUserAddress);

}
