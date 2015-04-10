A quick tutorial on using the count keyword application.
========================================================

# Make a data directory in HDFS.
```
hdfs dfs -mkdir /data
```
# Copy the /tutorials/data/test.text to HDFS.
```
hdfs dfs -copyFromLocal test.text /data
```

Note the contents of this text files comes from
[Micromegas by Voltaire](https://www.gutenberg.org/ebooks/30123).

# Run the count keyword application on YARN.
```
$SPARK_HOME/bin/spark-submit --class learn.scala.CountKeywordApp \
    --master yarn \
    --num-executors 10 \
    --executor-cores 2 \
    learn-scala-0.0.1-SNAPSHOT.jar \
    /data/test.txt \
    the
```
# Run the count keyword application on Spark.
```    
$SPARK_HOME/bin/spark-submit --class learn.scala.CountKeywordApp \
    --master spark://node1:7077 \
    --num-executors 10 \
    --executor-cores 2 \
    learn-scala-0.0.1-SNAPSHOT.jar \
    /data/test.txt \
    the
```