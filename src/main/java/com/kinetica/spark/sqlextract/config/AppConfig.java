package com.kinetica.spark.sqlextract.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppConfig {

	private String sparkMaster;

	private List<String> shardKeys = new ArrayList<String>();
	private List<String> primaryKeys = new ArrayList<String>();
	private List<String> wktFields = new ArrayList<String>();
	private List<String> dictFields = new ArrayList<String>();

	private Map<String, String> kineticaIngest = new HashMap<String, String>();

	private Map<String, String> relationalOpts = new HashMap<String, String>();

	private Map<String, String> sparkOpts = new HashMap<String, String>();

	private Map<String, String> appOpts = new HashMap<String, String>();
	private Map<String, String> hadoopOpts = new HashMap<String, String>();

	public List<String> getShardKeys() {
		return shardKeys;
	}

	public void setShardKeys(List<String> shardKeys) {
		this.shardKeys = shardKeys;
	}

	public List<String> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(List<String> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public Map<String, String> getKineticaIngest() {
		return kineticaIngest;
	}

	public void setKineticaIngest(Map<String, String> kineticaIngest) {
		this.kineticaIngest = kineticaIngest;
	}

	public String getSparkMaster() {
		return sparkMaster;
	}

	public void setSparkMaster(String sparkMaster) {
		this.sparkMaster = sparkMaster;
	}

	public Map<String, String> getRelationalOpts() {
		return relationalOpts;
	}

	public void setRelationalOpts(Map<String, String> relationalOpts) {
		this.relationalOpts = relationalOpts;
	}

	public List<String> getWktFields() {
		return wktFields;
	}

	public void setWktFields(List<String> wktFields) {
		this.wktFields = wktFields;
	}

	public List<String> getDictFields() {
		return dictFields;
	}

	public void setDictFields(List<String> dictFields) {
		this.dictFields = dictFields;
	}

	public Map<String, String> getSparkOpts() {
		return sparkOpts;
	}

	public void setSparkOpts(Map<String, String> sparkOpts) {
		this.sparkOpts = sparkOpts;
	}

	@Override
	public String toString() {
		return "AppConfig [sparkMaster=" + sparkMaster + ", shardKeys=" + shardKeys + ", primaryKeys=" + primaryKeys
				+ ", wktFields=" + wktFields + ", dictFields=" + dictFields + ", kineticaIngest=" + kineticaIngest
				+ ", relationalOpts=" + relationalOpts + ", sparkOpts=" + sparkOpts + "]";
	}

	public Map<String, String> getAppOpts() {
		return appOpts;
	}

	public void setAppOpts(Map<String, String> appOpts) {
		this.appOpts = appOpts;
	}

	public Map<String, String> getHadoopOpts() {
		return hadoopOpts;
	}

	public void setHadoopOpts(Map<String, String> hadoopOpts) {
		this.hadoopOpts = hadoopOpts;
	}



}