import sys
import yaml
from pyspark.sql import SparkSession
import time


def relational_kinetica_ingest():

    start_time = time.time()
    sparkBuilder = SparkSession.builder.appName("Python Spark Kinetica Ingestion").master(master)

    for k,v in sparkOpts.items():
        sparkBuilder.config(k, v)

    spark = sparkBuilder.getOrCreate();

    df = spark.read.format("jdbc").options(**relationalOpts).load()

    print("================ Partitions to Process ======>>>>>>>>>>>>>>")
    print(df.rdd.getNumPartitions())
    print("===========================================================")
    all_rows = df.count()

    df.write.format("com.kinetica.spark").options(**kineticaIngest).save()

    spark.stop()

    end_time = time.time()
    print("================ Import finished ====================")
    print(str(all_rows)+" datasets imported.")
    print('Execution time in seconds: %f' % (end_time-start_time))
    print("=====================================================")


if __name__ == "__main__":

    if len(sys.argv) < 1:
        print ("Usage: %s <app_config.yaml>" % sys.argv[0])
        quit()

    relationalOpts = {}
    sparkOpts = {}
    kineticaIngest = {}
    master = ""

    with open(sys.argv[1]) as f:
        root = yaml.load_all(f, Loader=yaml.FullLoader)

        for rootList in root:
            master = rootList['sparkMaster']
            relationalOpts = rootList['relationalOpts']
            sparkOpts = rootList['sparkOpts']
            kineticaIngest = rootList['kineticaIngest']

    relational_kinetica_ingest()
