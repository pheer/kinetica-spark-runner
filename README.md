# Kinetica Spark Ingest/Egress Runner

Pulls from relational sources.  The postgres jdbc driver is included in the bundle jar.

## Prerequisites

* Maven
* Spark (local or cluster mode)

## Tech Stack

* Kinetica Spark connector
* Spark env


## Build
`mvn clean install`

Target directory will have a tar.gz file with the following files:

* `kinetica-spark-runner-1.0-SNAPSHOT-uber.jar`
* `conf/app_config.yml`


## Run pyspark

Example of how to run this on KIO ingest node:

`SPARK_HOME=/opt/gpudb/kitools/kio/lib/python3.6/site-packages/pyspark-2.3.2-py3.6.egg/pyspark /opt/gpudb/kitools/kio/kio run_cmd /opt/gpudb/bin/gpudb_python kingest_rdbms.py conf/app_config.yml`


## To install pyyaml in gpudb env

`/opt/gpudb/bin/gpudb_env pip install pyyaml`


## Run java
The jar has all dependencies embedded and can be used with spark-submit command:

`spark-submit --master local  --verbose --jars target/kinetica-spark-runner-1.0-SNAPSHOT-uber.jar --driver-class-path target/kinetica-spark-runner-1.0-SNAPSHOT-uber.jar --class com.kinetica.spark.sqlextract.KineticaRelationalPull target/kinetica-spark-runner-1.0-SNAPSHOT-uber.jar app_config.yml`

or on gpudb kio box w/other spark options via cmdline:

`SPARK_HOME=/opt/gpudb/kitools/kio/lib/python3.6/site-packages/pyspark-2.3.2-py3.6.egg/pyspark/ /opt/gpudb/kitools/kio/kio run_cmd /opt/gpudb/kitools/kio/bin/spark-submit --executor-memory 32g --driver-memory 32g --conf spark.yarn.executor.memoryOverhead=8000 --conf spark.memory.offHeap.enabled=true --conf spark.memory.offHeap.size=32g --master yarn-client  --verbose --class com.kinetica.spark.sqlextract.KineticaRelationalPull kinetica-spark-runner-1.0-SNAPSHOT-uber.jar  conf/app_config.yml`


## Config 

The app_config.yml file holds all configuration needed


```yaml
######
# spark master in EMR, cloudera etc. master would be yarn
######
sparkMaster: local[*]

# colmns that should be shard keys in kinetica (java spark only)
# this won't be used if it doesn't match incoming column name
shardKeys:
   - rowid

# any primary keys to set in Kinetica (java spark only)
# this won't be used if it doesn't match incoming column name
primaryKeys:
   - none

# wkt geom that will be endoded as geometry in Kinetica (java spark only)
# this won't be used if it doesn't match incoming column name
wktFields:
   - geom

# what to encode as dictionary. low cardinality columns are good examples - e.g. gender, color
# this won't be used if it doesn't match incoming column name (java only)
dictFields:


# defines jdbc properties to pull from
# For full list of options See: https://spark.apache.org/docs/latest/sql-data-sources-jdbc.html
relationalOpts:
   url: jdbc:postgresql://host/postgres
   dbtable: (select rowid, enodebid, cov_uuid, st_astext(geom) as geom from ca5) as temptbl
   user: postgres
   password: Kinetica1!
   driver: org.postgresql.Driver
   fetchsize: 500
   numPartitions: 500
   partitionColumn: rowid
   lowerBound: 1
   upperBound: 26925

sparkOpts:
  spark.jars: /tmp/kinetica-spark-runner-1.0-SNAPSHOT-uber.jar
  spark.driver.memory: 32g
  spark.executor.memory: 32g
  spark.memory.offHeap.enabled: true
  spark.memory.offHeap.size: 32g

# This section defines the kinetica connection and ingestion configuration
# For full list of options See: https://www.kinetica.com/docs/connectors/spark_guide.html#property-reference for full list of valid options
kineticaIngest:
   database.url: http://172.30.1.10:9191
   database.jdbc_url: jdbc:kinetica://172.30.1.10:9191;ParentSet=test
   database.username: admin
   database.password: Kinetica1!
   ingester.batch_size: 500
   ingester.num_threads: 4
   table.name: test_table
   table.create: false
   


```

## Egress Example

```
SPARK_HOME=/opt/gpudb/kitools/kio/lib/python3.6/site-packages/pyspark-2.3.2-py3.6.egg/pyspark /opt/gpudb/kitools/kio/kio run_cmd /opt/gpudb/bin/gpudb_python egress.py egress.yaml
```
 
