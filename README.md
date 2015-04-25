vagrant-hadoop-2.4.1-spark-1.3.0
================================

# Introduction

Vagrant project to spin up a cluster of 4 virtual machines with Hadoop v2.4.1 and Spark v1.3.0. 

1. node1 : HDFS NameNode + Spark Master
2. node2 : YARN ResourceManager + JobHistoryServer + ProxyServer
3. node3 : HDFS DataNode + YARN NodeManager + Spark Slave
4. node4 : HDFS DataNode + YARN NodeManager + Spark Slave

# Getting Started

1. [Download and install VirtualBox](https://www.virtualbox.org/wiki/Downloads)
2. [Download and install Vagrant](http://www.vagrantup.com/downloads.html).
3. Run ```vagrant box add centos65 https://github.com/2creatives/vagrant-centos/releases/download/v6.5.1/centos65-x86_64-20131205.box```
4. Git clone this project, and change directory (cd) into this project (directory).
5. Run ```vagrant up``` to create the VM.
6. Run ```vagrant ssh``` to get into your VM.
7. Run ```vagrant destroy``` when you want to destroy and get rid of the VM.

Some gotcha's.

1. Make sure you download Vagrant v1.4.3 or higher.
2. Make sure when you clone this project, you preserve the Unix/OSX end-of-line (EOL) characters. The scripts will fail with Windows EOL characters.
3. Make sure you have 4Gb of free memory for the VM. You may change the Vagrantfile to specify smaller memory requirements.
4. This project has NOT been tested with the VMWare provider for Vagrant.
5. You may change the script (common.sh) to point to a different location for Hadoop and Spark to be downloaded from. Here is a list of mirrors for Hadoop: http://www.apache.org/dyn/closer.cgi/hadoop/common/.

# Advanced Stuff

If you have the resources (CPU + Disk Space + Memory), you may modify Vagrantfile to have even more HDFS DataNodes, YARN NodeManagers, and Spark slaves. Just find the line that says "numNodes = 4" in Vagrantfile and increase that number. The scripts should dynamically provision the additional slaves for you.

# Make the VMs setup faster
You can make the VM setup even faster if you pre-download the Hadoop, Spark, and Oracle JDK into the /resources directory.

1. /resources/hadoop-2.4.1.tar.gz
2. /resources/spark-1.3.0-bin-hadoop2.tgz
3. /resources/jdk-7u51-linux-x64.gz

The setup script will automatically detect if these files (with precisely the same names) exist and use them instead. If you are using slightly different versions, you will have to modify the script accordingly.

# Post Provisioning
After you have provisioned the cluster, you need to run some commands to initialize your Hadoop cluster. Note, you need to be root to complete these post-provisioning steps. (Type in "su" and the password is "vagrant"). 

SSH into node1 and issue the following command.

1. $HADOOP_PREFIX/bin/hdfs namenode -format myhadoop

## Start Hadoop Daemons (HDFS + YARN)
SSH into node1 and issue the following commands to start HDFS.

1. $HADOOP_PREFIX/sbin/hadoop-daemon.sh --config $HADOOP_CONF_DIR --script hdfs start namenode
2. $HADOOP_PREFIX/sbin/hadoop-daemons.sh --config $HADOOP_CONF_DIR --script hdfs start datanode

SSH into node2 and issue the following commands to start YARN.

1. $HADOOP_YARN_HOME/sbin/yarn-daemon.sh --config $HADOOP_CONF_DIR start resourcemanager
2. $HADOOP_YARN_HOME/sbin/yarn-daemons.sh --config $HADOOP_CONF_DIR start nodemanager
3. $HADOOP_YARN_HOME/sbin/yarn-daemon.sh start proxyserver --config $HADOOP_CONF_DIR
4. $HADOOP_PREFIX/sbin/mr-jobhistory-daemon.sh start historyserver --config $HADOOP_CONF_DIR

### Test YARN
Run the following command to make sure you can run a MapReduce job.

```
yarn jar /usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.4.1.jar pi 2 100
```

## Start Spark in Standalone Mode
SSH into node1 and issue the following command.

1. $SPARK_HOME/sbin/start-all.sh

### Test Spark on YARN
You can test if Spark can run on YARN by issuing the following command. Try NOT to run this command on the slave nodes.

```
$SPARK_HOME/bin/spark-submit --class org.apache.spark.examples.SparkPi \
    --master yarn \
    --num-executors 10 \
    --executor-cores 2 \
    $SPARK_HOME/lib/spark-examples*.jar \
    100
```

### Test code directly on Spark	
```
$SPARK_HOME/bin/spark-submit --class org.apache.spark.examples.SparkPi \
    --master spark://node1:7077 \
    --num-executors 10 \
    --executor-cores 2 \
    $SPARK_HOME/lib/spark-examples*.jar \
    100
```
	
### Test Spark using Shell
Start the Spark shell using the following command. Try NOT to run this command on the slave nodes.

```
$SPARK_HOME/bin/spark-shell --master spark://node1:7077
```

Then go here https://spark.apache.org/docs/latest/quick-start.html to start the tutorial. Most likely, you will have to load data into HDFS to make the tutorial work (Spark cannot read data on the local file system).

# Web UI
You can check the following URLs to monitor the Hadoop daemons.

1. [NameNode] (http://10.211.55.101:50070/dfshealth.html)
2. [ResourceManager] (http://10.211.55.102:8088/cluster)
3. [JobHistory] (http://10.211.55.102:19888/jobhistory)
4. [Spark] (http://10.211.55.101:8080)

# Vagrant boxes
A list of available Vagrant boxes is shown at http://www.vagrantbox.es. 

# Vagrant box location
The Vagrant box is downloaded to the ~/.vagrant.d/boxes directory. On Windows, this is C:/Users/{your-username}/.vagrant.d/boxes.

# References
This project was kludge together with great pointers from all around the internet. All references made inside the files themselves.

# Copyright Stuff
Copyright 2014 Jee Vang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
