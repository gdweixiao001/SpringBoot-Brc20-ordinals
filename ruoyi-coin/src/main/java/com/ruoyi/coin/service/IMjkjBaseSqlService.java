package com.ruoyi.coin.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

/**
 * 魔晶公共定制
 */
public interface IMjkjBaseSqlService {
	/*
	* customFieldList 为自定义列
	*
	* */

	//获取某个表的所有数据
	List<Map<String, Object>> getDataByTable(String tableName);
	List<Map<String, Object>> getDataByTable(String tableName,List<String> customFieldList);


	//根据id获取某一个表的一条数据
	Map<String, Object> getTableById(String tableName, String id);
	Map<String, Object> getTableByIdL(String tableName, Long id);
	Map<String, Object> getTableById(String tableName, String id,List<String> customFieldList);//自定义列


	//根据id获取某一个表的数据
	List<Map<String, Object>> getDataListByField(String tableName, String whereFieldName, Object whereFieldValue);
	List<Map<String, Object>> getDataListByField(String tableName, String whereFieldName, Object whereFieldValue,List<String> customFieldList);


	//根据id获取某一个表的数据  排序
	List<Map<String, Object>> getDataListByFieldOrderBy(String tableName, String whereFieldName, Object whereFieldValue,String orderByField,String orderStr);
	List<Map<String, Object>> getDataListByFieldOrderBy(String tableName, String whereFieldName, Object whereFieldValue,String orderByField,String orderStr,List<String> customFieldList);


	//获取一条数据
	Map<String, Object> getDataOneByField(String tableName, String whereFieldName, Object whereFieldValue);
	Map<String, Object> getDataOneByField(String tableName, String whereFieldName, Object whereFieldValue,List<String> customFieldList);

	//获取一条数据 排序
	Map<String, Object> getDataOneByFieldOrderBy(String tableName, String whereFieldName, Object whereFieldValue,String orderByField,String orderStr);
	Map<String, Object> getDataOneByFieldOrderBy(String tableName, String whereFieldName, Object whereFieldValue,String orderByField,String orderStr,List<String> customFieldList);


	//根据id获取某一个表的数据 type=A:全部  L：左边模糊  R右模糊
	List<Map<String, Object>> getDataListByLike(String tableName, String whereFieldName, Object whereFieldValue,String type);
	List<Map<String, Object>> getDataListByLike(String tableName, String whereFieldName, Object whereFieldValue,String type,List<String> customFieldList);

	//根据id获取某一个表的数据
	List<Map<String, Object>> getDataListByIn(String tableName, String whereFieldName, List<Object> whereFieldValue);
	List<Map<String, Object>> getDataListByIn(String tableName, String whereFieldName, List<Object> whereFieldValue,List<String> customFieldList);


	//将表数据封装为map
	Map<String,Map<String,Object>> getData2Map(String tableName,String key,Boolean redisFlag);
	//------------------查询封装已完成---------------------------

	//单个新增
	Long baseInsertData(String tableName, Map<String, Object> dataMap);

	//不对操作人进行封装
	Long baseSimpleIntegerSql(String tableName, Map<String, Object> dataMap);

	//单个修改
	void baseUpdateData(String tableName,Map<String, Object> map,String id);
	void baseUpdateDataLong(String tableName,Map<String, Object> map,Long id);

	void baseUpdateDataWhere(String tableName,Map<String, Object> map,String whereCol, String whereVal);

	void baseUpdateDataTenantIgnore(String tableName,Map<String, Object> map,String id);


	//公共删除-逻辑
	Integer baseDeleteSqlStr(String tableName, String id);
	Integer baseDeleteSql(String tableName, Long id);
	//真实删除
	Integer baseRealDeleteSql(String tableName, Long id);

	//	自定义公共删除-逻辑
	Integer baseZdyDeleteSql(String tableName, String whereFieldName, Long id);
	//	自定义公共删除-逻辑
	Integer baseZdyDeleteSql(String tableName, String whereFieldName, String id);
	//	自定义公共删除-逻辑
	Integer baseZdyRealDeleteSql(String tableName, String whereFieldName, String id);

	//--------------其他--------------------
	//根据表属性获取所有数据 多条件
	List<Map<String, Object>> getDataListByWrapper(String tableName, Wrapper wrapper);

	Map<String, Object> getDataOneByWrapper(String tableName, Wrapper wrapper);

	//根据表属性获取所有数据 多条件 分页
	IPage<Map<String, Object>> getDataIPageWrapper(String tableName, IPage page, Wrapper wrapper);

	//生成邀请码
     String creatInviteCode();
}
