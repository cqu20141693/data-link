#! /bin/sh

if [ $# -ge 2 ]; then
  SPRING_PROFILE=$1
  echo "$SPRING_PROFILE"

  APP_STARTER_JAR=$2
  echo "$APP_STARTER_JAR"
fi

if [ "$SPRING_PROFILE" == "dev" ]; then
  XMX="2g"
  XMS="1g"
  XSS="256k"
  MDMS="256m"
elif [ "$SPRING_PROFILE" == "prod" ]; then
  XMX="2g"
  XMS="1g"
  XSS="1024k"
  MDMS="1g"
fi

COMMAND="java -server
 -Xmx$XMX
 -Xms$XMS
 -Xss$XSS
 -XX:MaxDirectMemorySize=$MDMS
 -XX:+UseG1GC
 -XX:+UseCompressedOops
 -XX:+UseCompressedClassPointers
 -XX:+SegmentedCodeCache
 -XX:+PrintCommandLineFlags
 -XX:+ExplicitGCInvokesConcurrent
 -jar -Dspring.profiles.active=$SPRING_PROFILE ../$APP_STARTER_JAR"

start() {
  echo "$COMMAND"
  nohup $COMMAND >nohup.out 2>&1 &
}

echo "===start to deploy==="
PID=$(ps -ef |grep "$APP_STARTER_JAR" |grep -v grep| grep -v "deploy.sh" |awk '{print $2}')
if [ -z $PID ];then
  start
else
  echo "kill process pid is $PID"
  (sleep 30 ; kill -9 "$PID" > /dev/null 2>&1) &
  WATCH_DOG=$!
  kill -15 "$PID"
  wait "$PID" > /dev/null 2>&1
  kill $WATCH_DOG > /dev/null 2>&1
  start
fi
echo "===start to check health==="
i=1
while [ ${i} -le 10 ]; do
  sleep 3
  value=$(curl localhost:9093/actuator/health)
  if [ "$value" == '{"status":"UP"}' ]; then
    break;
  fi
  i=$(expr ${i} + 1)
done

if [ ${i} -le 10 ]; then
  # 预留时间
  sleep 2
  exit 0;
else
  exit 1;
fi