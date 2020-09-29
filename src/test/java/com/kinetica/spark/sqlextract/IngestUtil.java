package com.kinetica.spark.sqlextract;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gpudb.BulkInserter;
import com.gpudb.GPUdb;
import com.gpudb.RecordObject;
import com.gpudb.Type;
import com.gpudb.protocol.CreateTableRequest;
import com.gpudb.protocol.GetRecordsRequest;
import com.gpudb.protocol.GetRecordsResponse;

public class IngestUtil {

	static Logger logger = LoggerFactory.getLogger(IngestUtil.class);

	public static class SampleRecordTestObj extends RecordObject {
		@RecordObject.Column(order = 0, properties = { "data" })
		public double col1;
		@RecordObject.Column(order = 1, properties = { "data" })
		public String col2;
		@RecordObject.Column(order = 2, properties = { "data" })
		public String group_id;

		private SampleRecordTestObj() {
		}
	}

	public void ingestData(GPUdb gpudb, String tableName, int numRecords) throws Exception {

		// Register the desired data type with GPUdb
		Type type = RecordObject.getType(SampleRecordTestObj.class);
		// The type ID returned by GPUdb is needed to create a table later
		String type_id = type.create(gpudb);
		logger.info("Type id of newly created type: {} ", type_id);

		Map<String, String> create_table_options = GPUdb.options(CreateTableRequest.Options.NO_ERROR_IF_EXISTS,
				CreateTableRequest.Options.TRUE);
		gpudb.createTable(tableName, type_id, create_table_options);

		BulkInserter<SampleRecordTestObj> bulkInserter = new BulkInserter<SampleRecordTestObj>(gpudb, tableName, type,
				numRecords, null);

		// Generate data to be inserted into the table
		for (int i = 0; i < numRecords; i++) {
			SampleRecordTestObj record = new SampleRecordTestObj();
			record.put(0, (i + 0.1));
			record.put(1, ("string " + String.valueOf(i)));
			record.put(2, "Group 1");
			bulkInserter.insert(record);

		}

		bulkInserter.flush();

	}

	public int countTableRows(GPUdb gpudb, String tableName) throws Exception {
		// Retrieve the inserted records
		Map<String, String> blank_options = new LinkedHashMap<String, String>();
		GetRecordsRequest getRecordsReq = new GetRecordsRequest(tableName, 0, Integer.MAX_VALUE, blank_options);
		GetRecordsResponse<Object> getRecordsRsp = gpudb.getRecords(getRecordsReq);
		List<Object> retData = getRecordsRsp.getData();
		return retData.size();
	}
}
