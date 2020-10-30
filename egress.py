import sys
import yaml
from pyspark.sql import SparkSession
import time

def relational_kinetica_egress():
    start_time = time.time()
    sparkBuilder = SparkSession.builder.appName("Python Spark Kinetica Egress")

    for k,v in sparkOpts.items():
        sparkBuilder.config(k, v)

    spark = sparkBuilder.getOrCreate();


    for k,v in hadoopOpts.items():
        spark._jsc.hadoopConfiguration().set(k, v)

    #df = spark.read.parquet(appOpts.get('input_dir'))
    #df = spark.read.format("com.kinetica.spark").options(**kineticaEgress).load()
    df = spark.read.format("csv").option("header","true").load(appOpts.get('input_dir'))

    print("================ Partitions to Process ======>>>>>>>>>>>>>>")
    print(df.rdd.getNumPartitions())
    print("===========================================================")
    all_rows = df.count()
    print("----schema----")
    print(df.printSchema())
    print("--------------")
    #df.write.format("jdbc").options(**relationalOpts).save()
    #df.write.format("csv").mode("overwrite").save("/tmp/foobang")
    # df.write.format("csv").mode("overwrite").save(appOpts.get('csv_output_dir'))
    df.write.option("repartition", appOpts.get('repartition')).parquet(appOpts.get('output_dir'))

    spark.stop()

    end_time = time.time()
    print("================ Export finished ====================")
    print(str(all_rows)+" datasets exported.")
    print('Execution time in seconds: %f' % (end_time-start_time))
    print("=====================================================")


if __name__ == "__main__":

    if len(sys.argv) < 1:
        print ("Usage: %s <app_config.yaml>" % sys.argv[0])
        quit()

    relationalOpts = {}
    sparkOpts = {}
    kineticaEgress = {}
    hadoopOpt = {}
    appOpts = {}
    
    with open(sys.argv[1]) as f:
        root = yaml.load_all(f, Loader=yaml.FullLoader)

        for rootList in root:
            relationalOpts = rootList['relationalOpts']
            sparkOpts = rootList['sparkOpts']
            kineticaEgress = rootList['kineticaEgress']
            hadoopOpts = rootList['hadoopOpts']
            appOpts = rootList['appOpts']

    relational_kinetica_egress()
