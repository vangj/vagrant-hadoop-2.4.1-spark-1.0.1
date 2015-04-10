A quick tutorial on using the Naive Bayes classifier for discrete data code.
============================================================================

# Make a data directory in HDFS.
```
hdfs dfs -mkdir /data
```
# Copy the /tutorials/data/dnb-[train|test].csv to HDFS.
```
hdfs dfs -copyFromLocal dnb-train.csv /data
hdfs dfs -copyFromLocal dnb-test.csv /data
```
# Make an output directory in HDFS.
```
hdfs dfs -mkdir /output
```
# Run the Naive Bayes application on YARN.
```
$SPARK_HOME/bin/spark-submit --class learn.scala.DiscreteNaiveBayesClassifierApp \
    --master yarn \
    --num-executors 10 \
    --executor-cores 2 \
    learn-scala-0.0.1-SNAPSHOT.jar \
    /data/dnb-train.csv \
    /data/dnb-test.csv \
    /output/dnb-yarn
```
# Run the Naive Bayes application on Spark.
```
$SPARK_HOME/bin/spark-submit --class learn.scala.DiscreteNaiveBayesClassifierApp \
    --master spark://node1:7077 \
    --num-executors 10 \
    --executor-cores 2 \
    learn-scala-0.0.1-SNAPSHOT.jar \
    /data/dnb-train.csv \
    /data/dnb-test.csv \
    /output/dnb-spark
```    
# Check the outputs from jobs on YARN and Spark.
```
hdfs dfs -cat /output/dnb-yarn/part-00000
hdfs dfs -cat /output/dnb-spark/part-00000
```
## Delete the output directories.
```
hdfs dfs -rmr /output/dnb-yarn
hdfs dfs -rmr /output/dnb-spark
```