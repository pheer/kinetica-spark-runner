package com.kinetica.spark.sqlextract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import com.google.common.collect.Maps;

public class SampleKineticaIngest {

	public static void main(String[] args) {

		SparkSession sparkSession = SparkSession.builder().appName("SampleHerePullSpark").master("local").getOrCreate();
//		SparkKineticaTableUtil.setWktfield("");
		
		Dataset<Row> df = genData(sparkSession);
		df.write().format("com.kinetica.spark").options(getKineticaOpts()).save();;
		sparkSession.stop();
	}

	public static Dataset<Row> genData(SparkSession spark) {
		List<Row> list = new ArrayList<Row>();
		list.add(RowFactory.create("one"));
		list.add(RowFactory.create("two"));
		list.add(RowFactory.create("three"));
		list.add(RowFactory.create("four"));
		List<org.apache.spark.sql.types.StructField> listOfStructField = new ArrayList<org.apache.spark.sql.types.StructField>();
		listOfStructField.add(DataTypes.createStructField("test", DataTypes.StringType, true));
		StructType structType = DataTypes.createStructType(listOfStructField);
		Dataset<Row> data = spark.createDataFrame(list, structType);
		data.show();
		return data;
	}

	public static Map<String, String> getKineticaOpts() {
		String host = "localhost";
		String url = String.format("http://%s:9191", host);
		
		Map<String, String> opts = Maps.newHashMap();		
		opts.put("database.url", url);
		opts.put("database.jdbc_url", String.format("jdbc:kinetica://%s:9191;URL=%s", host, url));
		opts.put("database.username", "admin");
		opts.put("database.password", "Kinetica1!");
		opts.put("ingester.batch_size", "5000");
		opts.put("ingester.num_threads", "4");
		opts.put("table.name", "from_spark");
		opts.put("table.create", "true");
		
		return opts;

	}
}
