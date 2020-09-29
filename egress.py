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

    df = spark.read.format("com.kinetica.spark").options(**kineticaEgress).load()

    print("================ Partitions to Process ======>>>>>>>>>>>>>>")
    print(df.rdd.getNumPartitions())
    print("===========================================================")
    all_rows = df.count()

    #df.write.format("jdbc").options(**relationalOpts).save()
    df.write.format("csv").mode("overwrite").save("/tmp/foobang")

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

    with open(sys.argv[1]) as f:
        root = yaml.load_all(f, Loader=yaml.FullLoader)

        for rootList in root:
            relationalOpts = rootList['relationalOpts']
            sparkOpts = rootList['sparkOpts']
            kineticaEgress = rootList['kineticaEgress']

    relational_kinetica_egress()
