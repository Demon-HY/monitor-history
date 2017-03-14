#!/bin/bash

##
## 构建 monitor 服务端 / SDK 发行包
##

# run init.sh
sh init.sh

RED='\e[1;91m'
GREN='\e[1;92m'
WITE='\e[1;97m'
NC='\e[0m'

# 定义变量
export ANT_HOME=/usr/local/apache-ant-1.9.2
export PATH=${PATH}:${ANT_HOME}/bin

BUILD_DIR=$(pwd)
DIST_DIR=$(pwd)/dist
MONITOR_SRC_HOME=$(dirname $(pwd))
TMP_DIR=$DIST_DIR/tmp
REPORT_DIR=$DIST_DIR/report
LOG_DIR=/var/log/buildsvr

# 准备目录
rm -rf $TMP_DIR
mkdir -p $TMP_DIR
rm -rf $REPORT_DIR
mkdir -p $REPORT_DIR
# rm -rf $REMOTE_WORKSPACE/report

# 编译 monitor 到 jar 包
python make_build_xml.py $MONITOR_SRC_HOME $DIST_DIR > $TMP_DIR/build.xml
if [ "$?" != "0" ]; then
    echo "Make build.xml failed." | tee $LOG_DIR/build.err.log
    exit 1
fi

# 将build.xml文件复制到$TMP_DIR/build.xml（该语句只是测试固定build.xml使用，与上一句代码有互斥关系）
# cp $monitor_SRC_HOME/build/build.xml $TMP_DIR/build.xml

# ant目录清理
ant -f $TMP_DIR/build.xml clean
if [ "$?" != "0" ]; then
    echo "Ant clean failed." | tee $LOG_DIR/build.err.log
    exit 1
fi

# ant编译、打包、执行单元测试、出报告
ant -f $TMP_DIR/build.xml coveragereport
if [ "$?" != "0" ]; then
    echo "Ant build monitor jar or test failed." |tee $LOG_DIR/build.err.log
    exit 1
fi

# 复制单元测试报告到jenkins默认目录（）
# cp -a $REPORT_DIR $REMOTE_WORKSPACE

# 编译 javadoc
 #ant -f $TMP_DIR/build.xml javadoc
 #if [ "$?" != "0" ]; then
 #echo "Ant build javadoc failed." |tee $LOG_DIR/build.err.log
   # exit 1
 #fi

## Class文件混淆
Obfuscated=""
if [ -d dasho ]; then
    cd dasho && ./dasho.sh $DIST_DIR/monitor/lib/monitor $MONITOR_SRC_HOME $DIST_DIR/monitor/lib/monitor 
    if [ "$?" != "0" ]; then
        echo "DashO obfuscate failed" |tee $LOG_DIR/build.err.log
        ntpdate 1.cn.pool.ntp.org
        exit 1
    fi
    Obfuscated="-obf"
    cd ..
fi

## 整理发生包目录文件
# 准备版本信息
REV=$(svn info ../ | grep 'Revision:' | awk '{print $2}')
VER=rev$REV${Obfuscated}.$(date +%y%m%d%H%M).$(hostname)
PKGNAME=$1-$VER

# 拷贝 modules 目录
mkdir -p $DIST_DIR/monitor/lib/3rdparty
cp -r $MONITOR_SRC_HOME/monitor/lib/* $DIST_DIR/monitor/lib/3rdparty

find $DIST_DIR/monitor/lib/3rdparty -name '.?*' | xargs -I{} -n 1 rm -rf {}

# 暂时关闭 svn 功能
#svn export $MONITOR_SRC_HOME/modules $DIST_DIR/monitor/modules
#svn export $MONITOR_SRC_HOME/reports $DIST_DIR/monitor/reports
cp -r $MONITOR_SRC_HOME/modules $DIST_DIR/monitor/modules
cp -r $MONITOR_SRC_HOME/reports $DIST_DIR/monitor/reports


ls $DIST_DIR/monitor/modules | xargs -I{} -n 1 rm -rf $DIST_DIR/monitor/modules/{}/src

# newWeb
#cd $MONITOR_SRC_HOME/web/netdisk && grunt build;cd $BUILD_DIR
test -e /root/$1_web.zip && rm $DIST_DIR/monitor/modules/web/web -rf && unzip /root/$1_web.zip -d $DIST_DIR/monitor/modules/web/
#cp $MONITOR_SRC_HOME/web/netdisk/dist/web $DIST_DIR/monitor/modules/web/. -a

# 输出版本号
echo "BUILD ENV:" >> $DIST_DIR/monitor/README
echo rev $PKGNAME >> $DIST_DIR/monitor/README
uname -a >> $DIST_DIR/monitor/README
echo "{\"build\": \"${PKGNAME}\"}" > $DIST_DIR/monitor/modules/web/web/version.json

# 拷贝 run.sh server.properties
cp ./run.sh $DIST_DIR/monitor
sed -i '/monitor.conf.version/ s/.*/monitor.conf.version='"$REV"'/' ./server.properties
cp ./server.properties $DIST_DIR/monitor/server.properties.example
cp ./readme $DIST_DIR/monitor/server.readme
ln -s /var/log/monitor $DIST_DIR/monitor/log

# 拷贝 test 目录
#svn export $MONITOR_SRC_HOME/test $DIST_DIR/monitor/test
cp -r $MONITOR_SRC_HOME/test $DIST_DIR/monitor/test

python -m compileall -l $DIST_DIR/monitor/test &&
rm -rf $DIST_DIR/monitor/test/*.py
cp $MONITOR_SRC_HOME/test/config.py $DIST_DIR/monitor/test

# 打 javadoc 包
# test -d /data/pub/monitor && cp -r $TMP_DIR/javadoc /data/pub/monitor/$PKGNAME-javadoc

# 打 tar 包
mv $DIST_DIR/monitor $DIST_DIR/$PKGNAME &&
cd $DIST_DIR &&
tar -zcf $PKGNAME.tar.gz $PKGNAME

# 运行测试
'''
cd $BUILD_DIR
if [ -f test.sh ]; then
    echo -e ${GREN}"Start regression test!"${NC}
    bash test.sh $DIST_DIR/$PKGNAME
    if [ "$?" != "0" ]; then
        echo -e ${RED}"Regression Testing Failed!"${NC}
        exit 1
    fi
fi
'''

#echo -e ${RED}"PROJECT_NAME IS ${PROJECT_NAME}"${NC}
echo -e ${RED}"PROJECT_NAME IS $1"${NC}

# 移动安装包到 /data/pub/monitor
# test -d /data/pub/monitor && mv $DIST_DIR/$PKGNAME.tar.gz /data/pub/monitor/
# test -h /opt/monitor && mv $PKGNAME /opt && ln -snf /opt/$PKGNAME /opt/monitor
if [ ! -d /data/pub/$1 ]; then
    mkdir /data/pub/$1
fi
mv $DIST_DIR/$PKGNAME.tar.gz /data/pub/$1/

# 创建单元测试报告存放目录
if [ ! -d /data/pub/junitreport/$1 ]; then
    mkdir /data/pub/junitreport/$1
fi

if [ ! -d /data/pub/junitreport/$1/${BUILD_NUMBER} ]; then
    mkdir /data/pub/junitreport/$1/${BUILD_NUMBER}
fi

# 移动单元测试报告到存放目录
test -d $REPORT_DIR && mv $REPORT_DIR /data/pub/junitreport/$1/${BUILD_NUMBER}/

# 将TESTS-TestSuites.xml数据放回$REPORT_DIR，保证生成单元测试趋势报告的需要 
mkdir -p $REPORT_DIR
test -e /data/pub/junitreport/$1/${BUILD_NUMBER}/report/TESTS-TestSuites.xml && cp /data/pub/junitreport/$1/${BUILD_NUMBER}/report/TESTS-TestSuites.xml $REPORT_DIR
