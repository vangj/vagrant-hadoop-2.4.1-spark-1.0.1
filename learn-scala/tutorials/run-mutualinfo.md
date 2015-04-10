A quick tutorial on using the mutual information code.
=======================================================

# Make a data directory in HDFS.
```
hdfs dfs -mkdir /data
```
# Copy the /tutorials/data/mi.csv to HDFS.
```
hdfs dfs -copyFromLocal mi.csv /data
```
# Make an output directory in HDFS.
```
hdfs dfs -mkdir /output
```
# Run the mutual information application on YARN.
```
$SPARK_HOME/bin/spark-submit --class learn.scala.MutualInformationApp \
    --master yarn \
    --num-executors 10 \
    --executor-cores 2 \
    learn-scala-0.0.1-SNAPSHOT.jar \
    /data/mi.csv \
    /output/mi-yarn
```
# Run the mutual information application on Spark.
```
$SPARK_HOME/bin/spark-submit --class learn.scala.MutualInformationApp \
    --master spark://node1:7077 \
    --num-executors 10 \
    --executor-cores 2 \
    learn-scala-0.0.1-SNAPSHOT.jar \
    /data/mi.csv \
    /output/mi-spark
```    
# Check the outputs from jobs on YARN and Spark.
```
hdfs dfs -cat /output/mi-yarn/part-00000
hdfs dfs -cat /output/mi-spark/part-00000
```
## Delete the output directories.
```
hdfs dfs -rmr /output/mi-yarn
hdfs dfs -rmr /output/mi-spark
```