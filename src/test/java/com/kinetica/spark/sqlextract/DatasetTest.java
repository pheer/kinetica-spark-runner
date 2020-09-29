package com.kinetica.spark.sqlextract;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.ArrayType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.kinetica.spark.util.Flatten;

public class DatasetTest {

	@Test
	public void scratch() throws Exception {
		
		SparkSession spark = SparkSession.builder().appName("SampleHerePullSpark").master("local").getOrCreate();
		
		
		ArrayList<Row> list = new ArrayList<Row>();
		list.add(RowFactory.create( Lists.newArrayList(RowFactory.create("moo1","moo2", "moo3"))));
		list.add(RowFactory.create(Lists.newArrayList(RowFactory.create("val11","val22", "val33"))));
		

		Dataset<Row> data = spark.createDataFrame(list, getDatasetSchema());
		data.printSchema();
		data.show(20, false);
		
		
//		List<String> cols = Lists.newArrayList(data.columns());
//		Dataset<Row> seg;
//		data.columns();
//		if (cols.contains("segments")) {
//			seg =
//					data.select(
//				        org.apache.spark.sql.functions.explode(data.col("segments")).as("seg")); //.select("seg.*");
//		} else {
//			seg = data;
//		}


		
//		data.createOrReplaceTempView("tmp_view");
//		String flattenSql = SparkSchemaUtils.flattenSchema(data.schema(), null);
//		System.out.println(flattenSql);
//		Dataset<Row> flattenDf = spark.sql("SELECT " + flattenSql + " FROM tmp_view");

		Dataset<Row> flattenDf = Flatten.flatten_all(data);
		flattenDf.printSchema();
		flattenDf.show(20, false);
		
	}
	
	private static StructType getDatasetSchema() {
		List<org.apache.spark.sql.types.StructField> listOfStructField = new ArrayList<org.apache.spark.sql.types.StructField>();
		listOfStructField.add(DataTypes.createStructField("item1", DataTypes.StringType, true));
		listOfStructField.add(DataTypes.createStructField("item2", DataTypes.StringType, true));
		listOfStructField.add(DataTypes.createStructField("item3", DataTypes.StringType, true));

		return new StructType(
				new StructField[] { 
						new StructField("segments", new ArrayType(DataTypes.createStructType(listOfStructField), true), false, Metadata.empty()) 
				});

	}
}
