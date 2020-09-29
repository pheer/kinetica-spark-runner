package com.kinetica.spark.sqlextract.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.yaml.snakeyaml.Yaml;

public class TestYaml {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub

		Yaml yaml = new Yaml();

		File coreYml = new File("app_config.yml");

//		InputStream in = TestYaml.class.getClassLoader().getResourceAsStream("app_config.yaml");

		AppConfig yamlFile = yaml.loadAs(new FileInputStream(coreYml), AppConfig.class);

		System.out.print(yamlFile);
	}

}
