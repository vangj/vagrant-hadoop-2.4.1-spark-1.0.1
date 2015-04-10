A quick tutorial on using the Pearson correlation code.
=======================================================

# Make a data directory in HDFS.
```
hdfs dfs -mkdir /data
```
# Copy the /tutorials/data/test.csv to HDFS.
```
hdfs dfs -copyFromLocal test.csv /data
```
# Make an output directory in HDFS.
```
hdfs dfs -mkdir /output
```
# Run the Pearson correlation application on YARN.
```
$SPARK_HOME/bin/spark-submit --class learn.scala.PearsonCorrelationApp \
    --master yarn \
    --num-executors 10 \
    --executor-cores 2 \
    learn-scala-0.0.1-SNAPSHOT.jar \
    /data/test.csv \
    /output/pearson-yarn
```
# Run the Pearson correlation application on Spark.
```
$SPARK_HOME/bin/spark-submit --class learn.scala.PearsonCorrelationApp \
    --master spark://node1:7077 \
    --num-executors 10 \
    --executor-cores 2 \
    learn-scala-0.0.1-SNAPSHOT.jar \
    /data/test.csv \
    /output/pearson-spark
```    
# Check the outputs from jobs on YARN and Spark.
```
hdfs dfs -cat /output/pearson-yarn/part-00000
hdfs dfs -cat /output/pearson-spark/part-00000
```
## Delete the output directories.
```
hdfs dfs -rmr /output/pearson-yarn
hdfs dfs -rmr /output/pearson-spark
```