#!/usr/bin/env bash

CLASSPATH=$1
MODULE_NAME=$2

CLASSPATH="${CLASSPATH}:$(cat exonum-java-binding/exonum-java-binding-core/target/ejb-core-classpath.txt)"

JAVA_HOME="${JAVA_HOME:-$(java -XshowSettings:properties -version 2>&1 > /dev/null | grep 'java.home' | awk '{print $3}')}/"
echo "JAVA_HOME=${JAVA_HOME}"

# Find the directory containing libjvm (the relative path has changed in Java 9)
JVM_LIB_PATH="$(find ${JAVA_HOME} -type f -name libjvm.* | xargs -n1 dirname)"
echo "JVM_LIB_PATH=${JVM_LIB_PATH}"

CURRENT_DIR=$(pwd)
echo "CURRENT_DIR=${CURRENT_DIR}"

EJB_ROOT=$(realpath "../../..")
echo "PROJ_ROOT=${EJB_ROOT}"

echo "<====> TEMPLATE <====>"

EJB_LIBPATH="${JVM_LIB_PATH}:."

LD_LIBRARY_PATH=.:${JVM_LIB_PATH} ejb-app generate-template \
  common.toml \
  --validators-count 1

echo "<====> GENERATE <====>"

LD_LIBRARY_PATH=.:${JVM_LIB_PATH} ejb-app generate-config \
  common.toml pub.toml sec.toml \
  --ejb-classpath $CLASSPATH \
  --ejb-libpath $EJB_LIBPATH \
  --ejb-log-config-path "./log_config.xml" \
  --peer-address 127.0.0.1:5400

echo "<====> FINALIZE <====>"

LD_LIBRARY_PATH=.:${JVM_LIB_PATH} ejb-app finalize \
  sec.toml node.toml \
  --ejb-module-name $MODULE_NAME \
  --ejb-port 6000 \
  --public-configs pub.toml

echo "<====> RUN <====>"

LD_LIBRARY_PATH=.:${JVM_LIB_PATH} ejb-app run \
  -d db0 -c node.toml --public-api-address 0.0.0.0:6001