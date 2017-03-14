#!/bin/bash

CONF_FILE="/etc/monitor/server.properties"
STDOUT="/var/log/monitor/stdout"

# 或需要将 run.sh 安装至 /etc/init.d 作开机自启动
# 请将 MONITOR_HOME 改为 XSERVER 的安装目录
MONITOR_HOME="/opt/monitor"

if [ ! -f "$CONF_FILE" ]; then
    echo "Conf file $CONF_FILE not found"
    exit 1
fi

LOG_DIR=$(dirname $STDOUT)
mkdir -p $LOG_DIR
chmod 777 $LOG_DIR

HEAP_MAX=$(free | grep Mem | awk '{print int($2*6/10/1024)}')
HEAP_INIT=$(free | grep Mem | awk '{print int($2*3/10/1024)}')
GC_LOG=$LOG_DIR/gc.log
GC_ARG="-Xms${HEAP_INIT}m -Xmx${HEAP_MAX}m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime -XX:+PrintHeapAtGC -verbose:GC -Xloggc:$GC_LOG"
GC_ARG="$GC_ARG -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=1"
EXEC="java -cp $MONITOR_HOME/dServer/lib/*:$MONITOR_HOME/module/*/bin/*:/opt/monitor/lib/monitor.jar $GC_ARG monitor.Main "$CONF_FILE

RED='\e[1;91m'
GREN='\e[1;92m'
WITE='\e[1;97m'
NC='\e[0m'

function start_service() {
    if [ $(ps -ef | grep -F "$EXEC" | grep -vc grep) -gt 0 ]; then
        echo -e $RED"Service already running."$NC
        status_service
        exit 0
    fi

    echo "$EXEC"
    echo "==================== Starting Service ==========================" >> $STDOUT
    echo $(date '+%F %T') >> $STDOUT
    LAST_POS=$(ls -al $STDOUT  | awk '{print $5}')

    $EXEC 1>>$STDOUT 2>&1 &

    for (( i=0; i<240; i++ )); do

        if [ $(tail -n 2 $STDOUT | grep -c -F "Server:main: Started @") -gt 0 ]; then
            echo
            ps -ef | grep -v grep | grep --color=always -F "$EXEC"
            CUR_POS=$(ls -al $STDOUT  | awk '{print $5}')
            tail -c $(expr $CUR_POS - $LAST_POS) $STDOUT
            echo -e $GREN"Service started."$NC
            return
        fi

        printf "."
        sleep 0.5
    done

    echo
    CUR_POS=$(ls -al $STDOUT  | awk '{print $5}')
    tail -c $(expr $CUR_POS - $LAST_POS) $STDOUT
    echo -e $RED"Service start failed."$NC
    exit 1
}

function kill_service() {
    if [ $(ps -ef | grep -F "$EXEC" | grep -vc grep) -lt 1 ]; then
        echo -e $RED"Service is not running."$NC
        exit 1
    fi

    kill $(ps -ef | grep -F "$EXEC" | grep -v grep | awk '{print $2}')

    for (( i=0; i<40; i++ )); do
        if [ $(ps -ef | grep -F "$EXEC" | grep -vc grep) -eq 0 ]; then
            echo
            echo -e $GREN"Stop service successfully."$NC
            return
        fi

        printf "."
        sleep 0.2
    done

    echo
    echo -e $RED"Stop service failed."$NC
    exit 1
}

function status_service() {
    if [ $(ps -ef | grep -F "$EXEC" | grep -vc grep) -lt 1 ]; then
        echo -e $RED"Service is not running."$NC
        exit 1
    fi

    ps -ef | grep -v grep | grep --color=always -F "$EXEC"
}

MODE=${1}
case ${MODE} in
    "start")
        start_service
        ;;

    "restart")
        kill_service
        start_service
        ;;

    "stop")
        kill_service
        ;;

    "status")
        status_service
        ;;        
        
    *)
        # usage
        echo -e "Usage: $0 { start | stop | restart | status }"
        exit 1
        ;;
esac

