A quick tutorial on using the logistic regression classifier.
=============================================================

# Make a data directory in HDFS.
```
hdfs dfs -mkdir /data
```
# Copy the /tutorials/data/lr-[train|test].csv to HDFS.
```
hdfs dfs -copyFromLocal lr-train.csv /data
hdfs dfs -copyFromLocal lr-test.csv /data
```

Note this data set was taken from 
[Handbook of Biological Statistics](http://www.sph.emory.edu/~dkleinb/datasets/cancer.dat)
from the project [logistic-regression](https://github.com/tpeng/logistic-regression).

# Make an output directory in HDFS.
```
hdfs dfs -mkdir /output
```
# Run the application on YARN.
```
$SPARK_HOME/bin/spark-submit --class learn.scala.LogisticRegressionApp \
    --master yarn \
    --num-executors 10 \
    --executor-cores 2 \
    learn-scala-0.0.1-SNAPSHOT.jar \
    /data/lr-train.csv \
    /data/lr-test.csv \
    /output/lr-yarn \
    3000 \
    0.0001
```
# Run the application on Spark.
```
$SPARK_HOME/bin/spark-submit --class learn.scala.LogisticRegressionApp \
    --master spark://node1:7077 \
    --num-executors 10 \
    --executor-cores 2 \
    learn-scala-0.0.1-SNAPSHOT.jar \
    /data/lr-train.csv \
    /data/lr-test.csv \
    /output/lr-spark \
    3000 \
    0.0001
```    
# Check the outputs from jobs on YARN and Spark.
```
hdfs dfs -cat /output/lr-yarn/part-00000
hdfs dfs -cat /output/lr-spark/part-00000
```
## Delete the output directories.
```
hdfs dfs -rmr /output/lr-yarn
hdfs dfs -rmr /output/lr-spark
```