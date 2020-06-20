#!/bin/sh

MAIN_JAR="xc-mini-exec.jar"

SCRIPTS_DIR=`dirname "$0"`
PROJECT_DIR=`cd $SCRIPTS_DIR && pwd`
DT=`date +"%Y%m%d_%H%M%S"`
STAT_DATE=`date +"%Y%m%d"`
LOG_DIR="/home/logs/xc-mini"

MEM_OPTS="-Xms200m -Xmx500m"
GC_OPTS="$GC_OPTS -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:CMSInitiatingOccupancyFraction=70"
GC_OPTS="$GC_OPTS -Xloggc:${LOG_DIR}/gc_${DT}.log"
GC_OPTS="$GC_OPTS -XX:+PrintGCDateStamps -XX:+PrintGCDetails"
GC_OPTS="$GC_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOG_DIR}/heapdump_${DT}.hprof"
GC_OPTS="$GC_OPTS -XX:-OmitStackTraceInFastThrow"
START_OPTS="$START_OPTS -Djava.io.tmpdir=$PROJECT_DIR/tmp/"
START_OPTS="$START_OPTS -Duser.dir=$PROJECT_DIR"



#run java
mkdir -p "$PROJECT_DIR/tmp/"
mkdir -p "$PROJECT_DIR/logs/"
ps aux | grep $MAIN_JAR | grep -v grep | awk '{print $2}' | xargs kill -9 > /dev/null 2>&1
nohup java -jar $MEM_OPTS $GC_OPTS $START_OPTS $MAIN_JAR --spring.profiles.active=prod > /dev/null 2>&1 &
