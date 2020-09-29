package com.kinetica.spark.sqlextract;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import com.google.common.collect.Maps;
import com.kinetica.spark.sqlextract.config.AppConfig;
import com.kinetica.spark.util.table.SparkKineticaTableUtil;

public class KineticaRelationalPull {

	private static final Logger LOG = Logger.getLogger(KineticaRelationalPull.class);
	private static final String APP_NAME = "KineticaRelationalPull";

	public static void main(String[] args) throws ClassNotFoundException {

		if (args.length != 1) {
			System.out.println("Usage: app_config.yml, args passed: " + args);
			return;
		}

		
		// Load Application configuration
		Yaml yaml = new Yaml(new CustomClassLoaderConstructor(
				com.kinetica.spark.sqlextract.config.AppConfig.class.getClassLoader()));

		AppConfig config;
		try (InputStream in = Files.newInputStream(Paths.get(args[0]))) {
			config = yaml.loadAs(in, AppConfig.class);
		} catch (Exception e) {
			//throw new RuntimeException("Failed to parse config file. exception: " + e);
			yaml = new Yaml();
			try (InputStream in = Files.newInputStream(Paths.get(args[0]))) {
				config = yaml.loadAs(in, AppConfig.class);
			} catch (Exception e2) {
				e2.printStackTrace();
				throw new RuntimeException("Failed to parse config file. exception: " + e2);
	
			}

		}
		
		LOG.info(config);

		SparkSession sparkSession = SparkSession.builder().appName(APP_NAME).master("local[*]").getOrCreate();

		Dataset<Row> df = sparkSession.read().format("jdbc").options(getOptsFromMap(config.getRelationalOpts())).load();

		// pull df from relational data source
		// df = Flatten.flatten_all(df);

		if (CollectionUtils.isNotEmpty(config.getShardKeys()))
			SparkKineticaTableUtil.setShardKeys(config.getShardKeys());
		if (CollectionUtils.isNotEmpty(config.getPrimaryKeys()))
			SparkKineticaTableUtil.setPrimarykeys(config.getPrimaryKeys());
		if (CollectionUtils.isNotEmpty(config.getWktFields())) {
			for (String wktField : config.getWktFields())
				SparkKineticaTableUtil.setWktfield(wktField);
		}
		if (CollectionUtils.isNotEmpty(config.getDictFields()))
			SparkKineticaTableUtil.setDictEncodingFields(config.getDictFields());

		df.write().format("com.kinetica.spark").options(getOptsFromMap(config.getKineticaIngest())).save();

		sparkSession.stop();
	}

	public static Map<String, String> getOptsFromMap(Map<String, String> props) {
		Map<String, String> opts = Maps.newHashMap();
		for (Entry<String, String> entry : props.entrySet()) {
			opts.put(entry.getKey(), entry.getValue());
		}
		return opts;
	}

}
