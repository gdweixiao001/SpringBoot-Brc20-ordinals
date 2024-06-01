package com.ruoyi.common.utils.blade;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 数据类型公共类
 */
public class DataTypeUtil {

	public static boolean isNumberType(String type) {
		return "int".equals(type) || "double".equals(type) || "BigDecimal".equals(type) || "Integer".equals(type) || "Double".equals(type) || "BigInt".equals(type);
	}

	public static boolean isNotNumberType(String type) {
		return !isNumberType(type);
	}

	public static boolean isDateType(String type) {
		return "Date".equalsIgnoreCase(type) || "datetime".equalsIgnoreCase(type) || "Timestamp".equalsIgnoreCase(type);
	}


	public static String getSql(String dbFieldName, Object dbType) {
		if (dbType instanceof Integer) {
			return "#{" + dbFieldName + ",jdbcType=INTEGER}";
		} else if (dbType instanceof Long) {
			return "#{" + dbFieldName + ",jdbcType=BIGINT}";
		} else if (dbType instanceof Double) {
			return "#{" + dbFieldName + ",jdbcType=DOUBLE}";
		} else if (dbType instanceof BigDecimal) {
			return "#{" + dbFieldName + ",jdbcType=DECIMAL}";
		} else if (dbType instanceof Date) {
			return "#{" + dbFieldName + "}";
		} else {
			return "#{" + dbFieldName + ",jdbcType=VARCHAR}";
		}
	}
}
