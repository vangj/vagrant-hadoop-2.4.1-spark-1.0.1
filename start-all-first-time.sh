#!/bin/sh -x

vagrant ssh node1 -c 'sudo -E $HADOOP_PREFIX/bin/hdfs namenode -format myhadoop'

./start-all.sh