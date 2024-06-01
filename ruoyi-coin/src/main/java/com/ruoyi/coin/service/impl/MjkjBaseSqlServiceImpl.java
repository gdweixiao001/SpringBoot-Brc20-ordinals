package com.ruoyi.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ruoyi.coin.mapper.MjkjBaseSqlMapper;
import com.ruoyi.coin.service.IMjkjBaseSqlService;
import com.ruoyi.common.utils.MjkjUtils;
import com.ruoyi.common.utils.blade.DataTypeUtil;
import com.ruoyi.common.utils.blade.Func;
import com.ruoyi.common.utils.blade.tool.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 公共调用
 */
@Service
public class MjkjBaseSqlServiceImpl implements IMjkjBaseSqlService {


	@Autowired
	private MjkjBaseSqlMapper mjkjBaseSqlMapper;



	private String getSelectField() {
		return "*";
	}

	private String getSelectField(List<String> customFieldList) {
		String selectField = "";
		if (Func.isNotEmpty(customFieldList)) {
			for (int i = 0; i < customFieldList.size(); i++) {
				String field = customFieldList.get(i);
				if (i == 0) {
					selectField = field;
				} else {
					selectField += "," + field;
				}
			}
		} else {
			selectField = "*";
		}
		return selectField;
	}

	//获取某个表的所有数据
	@Override
	public List<Map<String, Object>> getDataByTable(String tableName) {
		String selectField = this.getSelectField();
		return mjkjBaseSqlMapper.getDataByTable(selectField, tableName);
	}


	//自定义字段
	@Override
	public List<Map<String, Object>> getDataByTable(String tableName, List<String> customFieldList) {
		return mjkjBaseSqlMapper.getDataByTable(this.getSelectField(customFieldList), tableName);
	}



	//根据id获取某一个表的一条数据
	@Override
	public Map<String, Object> getTableById(String tableName, String id) {
		return mjkjBaseSqlMapper.getTableById(this.getSelectField(), tableName, id);
	}

	@Override
	public Map<String, Object> getTableByIdL(String tableName, Long id) {
		Map<String, Object> dataMap = mjkjBaseSqlMapper.getTableByIdL(this.getSelectField(), tableName, id);
		String idStr = MjkjUtils.getMap2Str(dataMap, "id");
		dataMap.put("idStr",idStr);
		return dataMap;
	}

	@Override
	public Map<String, Object> getTableById(String tableName, String id, List<String> customFieldList) {
		return mjkjBaseSqlMapper.getTableById(this.getSelectField(customFieldList), tableName, id);
	}



	//根据id获取某一个表的一条数据 进行过滤
	@Override
	public List<Map<String, Object>> getDataListByField(String tableName, String whereFieldName, Object whereFieldValue) {
		return mjkjBaseSqlMapper.getDataListByField(this.getSelectField(), tableName, whereFieldName, whereFieldValue);
	}

	@Override
	public List<Map<String, Object>> getDataListByField(String tableName, String whereFieldName, Object whereFieldValue, List<String> customFieldList) {
		return mjkjBaseSqlMapper.getDataListByField(this.getSelectField(customFieldList), tableName, whereFieldName, whereFieldValue);
	}


	//根据id获取某一个表的一条数据 进行删除过滤 排序
	@Override
	public List<Map<String, Object>> getDataListByFieldOrderBy(String tableName, String whereFieldName, Object whereFieldValue, String orderByField, String orderStr) {
		if (Func.isEmpty(orderStr)) {
			orderStr = "DESC";
		}
		return mjkjBaseSqlMapper.getDataListByFieldOrderBy(this.getSelectField(), tableName, whereFieldName, whereFieldValue, orderByField, orderStr);
	}

	@Override
	public List<Map<String, Object>> getDataListByFieldOrderBy(String tableName, String whereFieldName, Object whereFieldValue, String orderByField, String orderStr, List<String> customFieldList) {
		if (Func.isEmpty(orderStr)) {
			orderStr = "DESC";
		}
		return mjkjBaseSqlMapper.getDataListByFieldOrderBy(this.getSelectField(customFieldList), tableName, whereFieldName, whereFieldValue, orderByField, orderStr);
	}


	//获取一条数据
	@Override
	public Map<String, Object> getDataOneByField(String tableName, String whereFieldName, Object whereFieldValue) {
		List<Map<String, Object>> dataList = this.getDataListByField(tableName, whereFieldName, whereFieldValue);
		if (Func.isEmpty(dataList)) {
			return null;
		}
		return dataList.get(0);
	}

	@Override
	public Map<String, Object> getDataOneByField(String tableName, String whereFieldName, Object whereFieldValue, List<String> customFieldList) {
		List<Map<String, Object>> dataList = this.getDataListByField(tableName, whereFieldName, whereFieldValue, customFieldList);
		if (Func.isEmpty(dataList)) {
			return null;
		}
		return dataList.get(0);
	}

	//获取一条数据 排序
	@Override
	public Map<String, Object> getDataOneByFieldOrderBy(String tableName, String whereFieldName, Object whereFieldValue, String orderByField, String orderStr) {
		if (Func.isEmpty(orderStr)) {
			orderStr = "DESC";
		}
		List<Map<String, Object>> dataList = this.getDataListByFieldOrderBy(tableName, whereFieldName, whereFieldValue, orderByField, orderStr);
		if (Func.isEmpty(dataList)) {
			return null;
		}
		return dataList.get(0);
	}

	//获取一条数据 排序
	@Override
	public Map<String, Object> getDataOneByFieldOrderBy(String tableName, String whereFieldName, Object whereFieldValue, String orderByField, String orderStr, List<String> customFieldList) {
		if (Func.isEmpty(orderStr)) {
			orderStr = "DESC";
		}
		List<Map<String, Object>> dataList = this.getDataListByFieldOrderBy(tableName, whereFieldName, whereFieldValue, orderByField, orderStr, customFieldList);
		if (Func.isEmpty(dataList)) {
			return null;
		}
		return dataList.get(0);
	}

	//根据id获取某一个表的一条数据 type=ALL:全部  L：左边模糊  R右模糊
	@Override
	public List<Map<String, Object>> getDataListByLike(String tableName, String whereFieldName, Object whereFieldValue, String type) {
		if (Func.isEmpty(type)) {
			type = "ALL";
		}
		return mjkjBaseSqlMapper.getDataListByLike(this.getSelectField(), tableName, whereFieldName, whereFieldValue, type);
	}

	@Override
	public List<Map<String, Object>> getDataListByLike(String tableName, String whereFieldName, Object whereFieldValue, String type, List<String> customFieldList) {
		if (Func.isEmpty(type)) {
			type = "ALL";
		}
		return mjkjBaseSqlMapper.getDataListByLike(this.getSelectField(customFieldList), tableName, whereFieldName, whereFieldValue, type);
	}

	@Override
	public List<Map<String, Object>> getDataListByIn(String tableName, String whereFieldName, List<Object> whereFieldValue) {
		return mjkjBaseSqlMapper.getDataListByIn(this.getSelectField(), tableName, whereFieldName, whereFieldValue);
	}

	@Override
	public List<Map<String, Object>> getDataListByIn(String tableName, String whereFieldName, List<Object> whereFieldValue, List<String> customFieldList) {
		return mjkjBaseSqlMapper.getDataListByIn(this.getSelectField(customFieldList), tableName, whereFieldName, whereFieldValue);
	}



	//将表数据封装为map
	@Override
	public Map<String, Map<String, Object>> getData2Map(String tableName, String key, Boolean redisFlag) {
		List<Map<String, Object>> dataList = mjkjBaseSqlMapper.getDataByTable(this.getSelectField(), tableName);
		Map<String, Map<String, Object>> resultMap = new HashMap<>();
		if (Func.isNotEmpty(dataList)) {
			dataList.forEach(map -> {
				resultMap.put(Func.toStr(map.get(key)), map);
			});
		}
		return resultMap;
	}

	//初始化默认值
	private Map<String, Object> initMap(Map<String, Object> map) {
		if (Func.isEmpty(map)) {
			map = new LinkedHashMap<>();
		}
		Object id = map.get("id");
		if (Func.isEmpty(id)) {
			map.put("id", IdWorker.getId());
		}
		/*if (Func.isEmpty(MjkjUtils.getMap2Str(map, "status"))) {
			map.put("status", 1);
		}*/
		String is_deleted = MjkjUtils.getMap2Str(map, "is_deleted");
		if (Func.isEmpty(is_deleted)) {
			map.put("is_deleted", "0");
		}
		map.put("create_time", DateUtil.now());

		/*BladeUser user = AuthUtil.getUser();
		if (Func.isNotEmpty(user)) {
			String tenantId = user.getTenantId();
			Long userId = user.getUserId();
			String deptId = Func.toStrList(user.getDeptId()).get(0);
			if (Func.isEmpty(map.get("tenant_id"))) {
				map.put("tenant_id", tenantId);
			}

			if (Func.isEmpty(map.get("create_user"))) {
				map.put("create_user", userId);
			}


		}*/
		return map;
	}

	//单个新增
	@Override
	public Long baseInsertData(String tableName, Map<String, Object> map) {
		map = this.initMap(map);

		String feildSql = "";
		String dataSql = "";
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String field = entry.getKey();
			Object value = entry.getValue();
			feildSql += field + ",";
			dataSql += DataTypeUtil.getSql(field, value) + ",";
		}
		if (feildSql.endsWith(",")) {
			feildSql = feildSql.substring(0, feildSql.length() - 1);
			dataSql = dataSql.substring(0, dataSql.length() - 1);
		}
		String sql = "insert into " + tableName + "(" + feildSql + ") values (" + dataSql + ")";

		map.put("select_sql", sql);
		System.out.println(map.get(sql));
		Long result = mjkjBaseSqlMapper.baseInsertSql(map);
		return result;
	}

	//不对操作人进行封装
	@Override
	public Long baseSimpleIntegerSql(String tableName, Map<String, Object> dataMap) {
		String feildSql = "";
		String dataSql = "";
		for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
			String field = entry.getKey();
			Object value = entry.getValue();
			feildSql += field + ",";
			dataSql += DataTypeUtil.getSql(field, value) + ",";
		}
		if (feildSql.endsWith(",")) {
			feildSql = feildSql.substring(0, feildSql.length() - 1);
			dataSql = dataSql.substring(0, dataSql.length() - 1);
		}
		String sql = "insert into " + tableName + "(" + feildSql + ") values (" + dataSql + ")";

		dataMap.put("select_sql", sql);
		Long result = mjkjBaseSqlMapper.baseInsertSql(dataMap);
		return result;
	}

	//单个修改
	@Override
	public void baseUpdateData(String tableName, Map<String, Object> map, String id) {

		map.put("update_time", DateUtil.now());
		/*BladeUser user = AuthUtil.getUser();
		if (Func.isNotEmpty(user)) {
			Long userId = user.getUserId();
			map.put("update_user", userId);
		}*/


		String updateSql = "update " + tableName + " set ";
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String field = entry.getKey();
			Object value = entry.getValue();
			if (Func.equals("id", field)) {
				continue;
			}

			updateSql += field + " = " + DataTypeUtil.getSql(field, value) + ",";
		}
		if (updateSql.endsWith(",")) {
			updateSql = updateSql.substring(0, updateSql.length() - 1);
		}
		String sql = updateSql + " where id ='" + id + "'";
		map.put("select_sql", sql);
		mjkjBaseSqlMapper.baseUpdateSql(map);
	}

	//单个修改
	@Override
	public void baseUpdateDataLong(String tableName, Map<String, Object> map, Long id) {

		map.put("update_time", DateUtil.now());
/*		BladeUser user = AuthUtil.getUser();
		if (Func.isNotEmpty(user)) {
			Long userId = user.getUserId();
			map.put("update_user", userId);
		}*/


		String updateSql = "update " + tableName + " set ";
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String field = entry.getKey();
			Object value = entry.getValue();
			if (Func.equals("id", field)) {
				continue;
			}

			updateSql += field + " = " + DataTypeUtil.getSql(field, value) + ",";
		}
		if (updateSql.endsWith(",")) {
			updateSql = updateSql.substring(0, updateSql.length() - 1);
		}
		String sql = updateSql + " where id =" + id;
		map.put("select_sql", sql);
		mjkjBaseSqlMapper.baseUpdateSql(map);
	}

	//单个修改
	@Override
	public void baseUpdateDataWhere(String tableName, Map<String, Object> map, String whereCol, String whereVal) {

		map.put("update_time", DateUtil.now());
/*
		BladeUser user = AuthUtil.getUser();
		if (Func.isNotEmpty(user)) {
			Long userId = user.getUserId();
			map.put("update_user", userId);
		}
*/


		String updateSql = "update " + tableName + " set ";
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String field = entry.getKey();
			Object value = entry.getValue();
			if (Func.equals("id", field)) {
				continue;
			}

			updateSql += field + " = " + DataTypeUtil.getSql(field, value) + ",";
		}
		if (updateSql.endsWith(",")) {
			updateSql = updateSql.substring(0, updateSql.length() - 1);
		}
		String sql = updateSql + " where " + whereCol + " = '" + whereVal + "'";
		map.put("select_sql", sql);
		mjkjBaseSqlMapper.baseUpdateSql(map);
	}


	@Override
	public void baseUpdateDataTenantIgnore(String tableName, Map<String, Object> map, String id) {
		this.baseUpdateData(tableName, map, id);
	}

	//公共删除
	@Override
	public Integer baseDeleteSqlStr(String tableName, String id) {
		return mjkjBaseSqlMapper.baseDeleteSqlStr(tableName, id);
	}

	@Override
	public Integer baseDeleteSql(String tableName, Long id) {
		return mjkjBaseSqlMapper.baseDeleteSql(tableName, id);
	}

	//真实删除
	@Override
	public Integer baseRealDeleteSql(String tableName, Long id) {
		return mjkjBaseSqlMapper.baseRealDeleteSql(tableName, id);
	}

	//	自定义公共删除
	@Override
	public Integer baseZdyDeleteSql(String tableName, String whereFieldName, Long id) {
		return mjkjBaseSqlMapper.baseZdyDeleteSql(tableName, whereFieldName, id);
	}

	//	自定义公共删除 string
	@Override
	public Integer baseZdyDeleteSql(String tableName, String whereFieldName, String id) {
		return mjkjBaseSqlMapper.baseZdyDeleteSql(tableName, whereFieldName, id);
	}

	//	自定义公共删除 string
	@Override
	public Integer baseZdyRealDeleteSql(String tableName, String whereFieldName, String id) {
		return mjkjBaseSqlMapper.baseZdyRealDeleteSql(tableName, whereFieldName, id);
	}



	// 根据表属性获取所有数据 多条件
	@Override
	public List<Map<String, Object>> getDataListByWrapper(String tableName, Wrapper wrapper) {
		if (Func.isEmpty(tableName)) {
			return null;
		}
		return mjkjBaseSqlMapper.getDataListByWrapper(tableName, wrapper);
	}

	// 根据表属性获取所有数据 多条件
	@Override
	public Map<String, Object> getDataOneByWrapper(String tableName, Wrapper wrapper) {
		if (Func.isEmpty(tableName)) {
			return new HashMap<>();
		}
		Map<String, Object> dataMap = mjkjBaseSqlMapper.getDataOneByWrapper(tableName, wrapper);
		return dataMap;
	}


	// 根据表属性获取所有数据 多条件 分页
	@Override
	public IPage<Map<String, Object>> getDataIPageWrapper(String tableName, IPage page, Wrapper wrapper) {
		if (Func.isEmpty(tableName)) {
			return null;
		}
		return mjkjBaseSqlMapper.getDataListByWrapper(tableName, page, wrapper);
	}


	//生成邀请码
	@Autowired
	public synchronized String creatInviteCode(){
		while (true){
			String random = Func.random(6);
			Map<String, Object> userInfoMap = this.getDataOneByField("coin_user_info", "invite_code", random);
			if(Func.isEmpty(userInfoMap)){
				return random;
			}
		}

	}

/*
	public static void main(String[] args) {
		String random = Func.random(6);
		System.out.println(random);
	}
*/

}
