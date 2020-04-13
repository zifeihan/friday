#!/bin/sh
cd `pwd`

STR=`jps -l | grep "fun.codec.friday.starter.Controller" | awk  -F ' '  '{print $1}'`
if [ ! -z "$STR" ]; then
    kill -9 $STR > /dev/null 2>&1
    sleep 2
else
  echo "没有Java进程"
fi

APP_HOME=$(dirname "$PWD")

echo "app.path : ${APP_HOME}"

echo "APP_HOME:"${APP_HOME}
DIR_LOG=${APP_HOME}/logs

if [ ! -e $DIR_LOG ] ; then
    mkdir -p $DIR_LOG
fi

FILE_STDOUT_LOG=$DIR_LOG/stdout.log
FILE_STDERR_LOG=$DIR_LOG/stderr.log

DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=28004,server=y,suspend=n"

${JAVA_HOME}/bin/java $DEBUG_OPTS -cp "${APP_HOME}/lib/*:${JAVA_HOME}/lib/*" \
-Dapp.home=${APP_HOME} \
fun.codec.friday.starter.Controller > ${FILE_STDOUT_LOG} 2>${FILE_STDERR_LOG} &
