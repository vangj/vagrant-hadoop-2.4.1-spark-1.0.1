#!/bin/sh -x

vagrant ssh node1 -c 'sudo -E $HADOOP_PREFIX/sbin/hadoop-daemon.sh --config $HADOOP_CONF_DIR --script hdfs start namenode'
vagrant ssh node1 -c 'sudo -E $HADOOP_PREFIX/sbin/hadoop-daemons.sh --config $HADOOP_CONF_DIR --script hdfs start datanode'

vagrant ssh node2 -c 'sudo -E $HADOOP_YARN_HOME/sbin/yarn-daemon.sh --config $HADOOP_CONF_DIR start resourcemanager'
vagrant ssh node2 -c 'sudo -E $HADOOP_YARN_HOME/sbin/yarn-daemons.sh --config $HADOOP_CONF_DIR start nodemanager'
vagrant ssh node2 -c 'sudo -E $HADOOP_YARN_HOME/sbin/yarn-daemon.sh start proxyserver --config $HADOOP_CONF_DIR'
vagrant ssh node2 -c 'sudo -E $HADOOP_PREFIX/sbin/mr-jobhistory-daemon.sh start historyserver --config $HADOOP_CONF_DIR'

vagrant ssh node1 -c 'sudo -E $SPARK_HOME/sbin/start-all.sh'
