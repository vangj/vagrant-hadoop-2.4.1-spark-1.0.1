#!/bin/bash
source "/vagrant/scripts/common.sh"

function formatNameNode {
	$HADOOP_PREFIX/bin/hdfs namenode -format myhadoop -force -noninteractive
	echo "formatted namenode"
}

function startHDFS {
	$HADOOP_PREFIX/sbin/hadoop-daemon.sh --config $HADOOP_CONF_DIR --script hdfs start namenode
	$HADOOP_PREFIX/sbin/hadoop-daemons.sh --config $HADOOP_CONF_DIR --script hdfs start datanode
	echo "started hdfs"
}

function startYarn {
	ssh node2 '$HADOOP_YARN_HOME/sbin/yarn-daemon.sh --config $HADOOP_CONF_DIR start resourcemanager'
	ssh node2 '$HADOOP_YARN_HOME/sbin/yarn-daemons.sh --config $HADOOP_CONF_DIR start nodemanager'
	ssh node2 '$HADOOP_YARN_HOME/sbin/yarn-daemon.sh start proxyserver --config $HADOOP_CONF_DIR'
	ssh node2 '$HADOOP_PREFIX/sbin/mr-jobhistory-daemon.sh start historyserver --config $HADOOP_CONF_DIR'
	echo "started yarn"
}

function createEventLogDir {
	hdfs dfs -mkdir /tmp
	hdfs dfs -mkdir /tmp/spark-events
	echo "created spark event log dir"
}

function startSpark {
	$SPARK_HOME/sbin/start-all.sh
	$SPARK_HOME/sbin/start-history-server.sh
	echo "started spark"
}

formatNameNode
startHDFS
startYarn
createEventLogDir
startSpark