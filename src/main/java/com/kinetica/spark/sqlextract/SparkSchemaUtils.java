package com.kinetica.spark.sqlextract;

import org.apache.spark.sql.types.ArrayType;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class SparkSchemaUtils {

	public static String flattenSchema(StructType schema, String prefix) {
		final StringBuilder selectSQLQuery = new StringBuilder();

		for (StructField field : schema.fields()) {
			final String fieldName = field.name();

			if (fieldName.startsWith("@")) {
				continue;
			}

			String colName = prefix == null ? fieldName : (prefix + "." + fieldName);
			String colNameTarget = colName.replace(".", "_");

			DataType dtype = field.dataType();
			if (dtype.getClass().equals(ArrayType.class)) {
				dtype = ((ArrayType) dtype).elementType();

			}
			if (dtype.getClass().equals(StructType.class)) {
				selectSQLQuery.append(flattenSchema((StructType) dtype, colName));
			} else {
				selectSQLQuery.append(colName);
				selectSQLQuery.append(" as ");
				selectSQLQuery.append(colNameTarget);
			}

			selectSQLQuery.append(",");
		}

		if (selectSQLQuery.length() > 0) {
			selectSQLQuery.deleteCharAt(selectSQLQuery.length() - 1);
		}

		return selectSQLQuery.toString();

	}
}
