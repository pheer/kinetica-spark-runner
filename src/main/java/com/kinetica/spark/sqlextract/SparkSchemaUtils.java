package com.kinetica.spark.sqlextract;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.ArrayType;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import scala.collection.JavaConverters;
import scala.collection.immutable.Seq;

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
	
	
	public static Dataset<Row> flattenJSONdf(Dataset<Row> ds) {

		StructField[] fields = ds.schema().fields();

		List<String> fieldsNames = new ArrayList<>();
		for (StructField s : fields) {
			fieldsNames.add(s.name());
		}

		for (int i = 0; i < fields.length; i++) {

			StructField field = fields[i];
			DataType fieldType = field.dataType();
			String fieldName = field.name();

			if (fieldType instanceof ArrayType) {
				List<String> fieldNamesExcludingArray = new ArrayList<String>();
				for (String fieldName_index : fieldsNames) {
					if (!fieldName.equals(fieldName_index))
						fieldNamesExcludingArray.add(fieldName_index);
				}

				List<String> fieldNamesAndExplode = new ArrayList<>(fieldNamesExcludingArray);
				String s = String.format("explode_outer(%s) as %s", fieldName, fieldName);
				fieldNamesAndExplode.add(s);

				String[]  exFieldsWithArray = new String[fieldNamesAndExplode.size()];
				Dataset<Row> exploded_ds = ds.selectExpr(fieldNamesAndExplode.toArray(exFieldsWithArray));

//				 explodedDf.show();

				return flattenJSONdf(exploded_ds);

			}
			else if (fieldType instanceof StructType) {

				String[] childFieldnames_struct = ((StructType) fieldType).fieldNames();

				List<String> childFieldnames = new ArrayList<>();
				for (String childName : childFieldnames_struct) {
					childFieldnames.add(fieldName + "." + childName);
				}

				List<String> newfieldNames = new ArrayList<>();
				for (String fieldName_index : fieldsNames) {
					if (!fieldName.equals(fieldName_index))
						newfieldNames.add(fieldName_index);
				}

				newfieldNames.addAll(childFieldnames);

				List<Column> renamedStrutctCols = new ArrayList<>();

				for(String newFieldNames_index : newfieldNames){
					renamedStrutctCols.add( new Column(newFieldNames_index.toString()).as(newFieldNames_index.toString().replace(".", "_")));
				}

				scala.collection.Seq<Column> renamedStructCols_seq = JavaConverters.collectionAsScalaIterableConverter(renamedStrutctCols).asScala().toSeq();

				Dataset<Row> ds_struct = ds.select(renamedStructCols_seq);

				return flattenJSONdf(ds_struct);
			}
			else{

			}

		}
		return ds;
	}
}
