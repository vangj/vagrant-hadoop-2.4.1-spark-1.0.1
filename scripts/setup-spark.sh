#!/bin/bash
source "/vagrant/scripts/common.sh"

function installLocalSpark {
 # $SPARK_ARCHIVE=spark-1.1.1-bin-hadoop2.4
        echo "install spark from local file"
        echo $SPARK_ARCHIVE
        FILE=/vagrant/resources/$SPARK_ARCHIVE
        tar -xzf $FILE -C /usr/local
        echo "untared spark"
}

function installRemoteSpark {
        echo "install spark from remote file"
        curl -o /vagrant/resources/$SPARK_ARCHIVE -O -L $SPARK_MIRROR_DOWNLOAD
        tar -xzf /vagrant/resources/$SPARK_ARCHIVE -C /usr/local
}

function setupSpark {
        echo "setup spark"
        sudo cp -f /vagrant/resources/spark/slaves /usr/local/$SPARK_ARCHIVE/conf
}

function setupEnvVars {
        echo "creating spark environment variables"
        sudo cp -f $SPARK_RES_DIR/spark.sh /etc/profile.d/spark.sh
}

function installSpark {
        echo "go in" $SPARK_ARCHIVE
#       if resourceExists $SPARK_ARCHIVE; then
                installLocalSpark
#       else
#               installRemoteSpark
#       fi
        sudo ln -s /usr/local/$SPARK_ARCHIVE /usr/local/spark
}

echo "setup spark"

installSpark
setupSpark
setupEnvVars
