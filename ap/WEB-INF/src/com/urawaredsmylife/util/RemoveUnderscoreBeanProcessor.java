package com.urawaredsmylife.util;

import java.beans.PropertyDescriptor;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.dbutils.BeanProcessor;

/**
 * DbUtilsのカラム名マッピングをアンダースコアを除去した形で行うためのクラス
 * 
 * @author motoy3d
 * 
 */
public class RemoveUnderscoreBeanProcessor extends BeanProcessor {

	/**
	 * アンダースコアを除去してマッピング
	 */
	protected int[] mapColumnsToProperties(ResultSetMetaData rsmd,
			PropertyDescriptor[] props) throws SQLException {

		int cols = rsmd.getColumnCount();
		int[] columnToProperty = new int[cols + 1];
		Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

		for (int col = 1; col <= cols; col++) {
			String columnName = rsmd.getColumnLabel(col);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(col);
			}
			for (int i = 0; i < props.length; i++) {
				if (columnName.replaceAll("_", "").equalsIgnoreCase(
						props[i].getName())) {
					columnToProperty[col] = i;
					break;
				}
			}
		}
		return columnToProperty;
	}

}
