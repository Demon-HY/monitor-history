#coding:utf-8

"""
该 python 脚本将分析 modules 目录中的模块依赖，并生成 ant 构建 xml
构建 xml 只负责生成jar文件
"""

import os
import traceback
import sys

if len(sys.argv) == 3 and \
   os.path.isdir(os.path.join(sys.argv[1], "monitor")) and \
   os.path.isdir(os.path.join(sys.argv[1], "modules")) and \
   not os.path.isfile(sys.argv[2]):
    MONITOR_SRC_HOME = sys.argv[1]
    BUILD_HOME = sys.argv[2]
else:
    print "Usage:", sys.argv[0], "{MONITOR_SRC_HOME}", "{BUILD_HOME}"
    sys.exit(1)

TMP_DIR = BUILD_HOME + "/tmp"
TMP_CLASS_DIR = TMP_DIR + "/class"
REPORT_DIR = BUILD_HOME + "/report"
FINAL_TARGET = BUILD_HOME + "/monitor"
monitor_LIB_DIR = "%s/monitor/lib" % MONITOR_SRC_HOME
monitor_LIB_TEST = "%s/monitor/lib_test_need" % MONITOR_SRC_HOME
XMODULES_DIR = "%s/modules" % MONITOR_SRC_HOME


# 递归将MONITOR lib中jar包保存到lib_file_set中
def GetFileSet(dir, fileSet):

    if os.path.isdir(dir):
        val = """<fileset dir="%s" includes="*.jar"/>""" % dir
        fileSet.append(val)
        for s in os.listdir(dir):
            if s.find(".svn") != -1:
                continue

            newDir=os.path.join(dir, s)
            if os.path.isdir(newDir):
                GetFileSet(newDir, fileSet)


lib_file_set = []
GetFileSet(monitor_LIB_DIR, lib_file_set)


# 将modules中的jar包追加到lib_file_set中
def HandleModules(dir, fileSet):

    if os.path.isdir(dir):

        for s in os.listdir(dir):

            newDir=os.path.join(dir, s)
            newDir=os.path.join(newDir, "lib")

            if os.path.isdir(newDir):
                GetFileSet(newDir, fileSet)


HandleModules(XMODULES_DIR, lib_file_set)


val = """<fileset dir="%s" includes="*.jar"/>""" % ("%s/lib/monitor" % FINAL_TARGET)
lib_file_set.append(val)

lib_file_set_str = ""
for s in lib_file_set:
    lib_file_set_str = lib_file_set_str + s


# For known MONITOR core
XML_LINES = [
"""
<project name="build" basedir="." xmlns:jacoco="antlib:org.jacoco.ant">
	  <property name="report.path" value="%s"/>
""" % REPORT_DIR,
"""
<property name="report.path.coverage" value="%s/coverage"/>
""" % REPORT_DIR,
"""
    <target name="clean">
        <delete dir="%s"/>
    </target>
""" % FINAL_TARGET,
"""
		<path id="classpath_monitor_test_coverage">
        <fileset dir="/%s" includes="*.jar"/>
    </path>
""" % monitor_LIB_TEST,
"""
    <taskdef uri="antlib:org.jacoco.ant" resource="org/jacoco/ant/antlib.xml">  
        <classpath path="%s/jacocoant.jar">
        </classpath> 
    </taskdef>
""" % monitor_LIB_TEST,
"""
    <path id="classpath_monitor">
        %s
    </path>
    <target name="compile_monitor">
        <mkdir dir="%s" />
        <javac includeantruntime="false" debug="true" debuglevel="lines,source,vars" srcdir="%s" destdir="%s" classpathref="classpath_monitor" />
    </target>
    <target name="jar_monitor" depends="compile_monitor">
        <mkdir dir="%s" />
        <jar destfile="%s" basedir="%s" />
    </target>
""" % (lib_file_set_str,
       "%s/class/monitor" % TMP_DIR,
       "%s/monitor/src" % MONITOR_SRC_HOME,
       "%s/class/monitor" % TMP_DIR,
       "%s/lib/monitor/" % FINAL_TARGET,
       "%s/lib/monitor/monitor.jar" % FINAL_TARGET,
       "%s/class/monitor" % TMP_DIR)
]

XML_JUNIT_FILESETS = [
"""
    <fileset dir="%s/monitor">
        <include name="**/*Test.class"/>
    </fileset>
""" % TMP_CLASS_DIR
]

XML_COVERAGE_CLASS_FILESETS = [
"""
    <fileset dir="%s%s/monitor"/>
""" % (TMP_CLASS_DIR,"/monitor")
]

XML_COVERAGE_SRC_FILESETS = [
"""
    <fileset dir="%s%s"/>
""" % (MONITOR_SRC_HOME,"/monitor/src")
]

# For modules
def _read_dependence(module_prop_path):
    if not os.path.isfile(module_prop_path):
        return None

    deps = set()
    try:
        for line in open(module_prop_path):
            line = line.strip()
            if line.startswith("#"):
                continue
            if line.startswith("dependence"):
                names = line.split("=")[-1]
                for name in names.split(","):
                    if name:
                        deps.add(name)
        return deps
    except:
        traceback.print_exc()

PROCESSED_MODULES = set()
PROCESSING_MODULES = set()

def _process_module(module_name):

    if module_name in PROCESSED_MODULES:
        return

    deps = _read_dependence("%s/modules/%s/module.properties" % (MONITOR_SRC_HOME, module_name))
    if deps == None:
        return

    PROCESSING_MODULES.add(module_name)
    for _module_name in deps:
        if _module_name in PROCESSING_MODULES:
            sys.stderr.write("loop module dependency detected, %s\n" % module_name)
            sys.exit(1)
        _process_module(_module_name)

    deps.add("monitor")
    LINES = [
        """
   <target name="compile_%s" depends="%s" >
       <mkdir dir="%s" />
       <javac includeantruntime="false" debug="true" debuglevel="lines,source,vars" srcdir="%s" destdir="%s" classpathref="classpath_monitor">
       <compilerarg line="-XDignore.symbol.file" />
       </javac>
       <copy todir="%s" failonerror="false" >
    	 <fileset dir="%s">
    	 <include name="testres/"/>
    	 </fileset>
       </copy>
    </target>
       """ % (module_name,
              ",".join(["jar_%s" % _module_name for _module_name in deps]),
              "%s/class/%s" % (TMP_DIR, module_name),
              "%s/modules/%s/src" % (MONITOR_SRC_HOME, module_name),
              "%s/class/%s" % (TMP_DIR, module_name),
              "%s/class/%s" % (TMP_DIR, module_name),
              "%s/modules/%s/src" % (MONITOR_SRC_HOME, module_name)),
        """
    <target name="jar_%s" depends="compile_%s" >
        <jar destfile="%s" basedir="%s" />
    </target>
        """ % (module_name,
               module_name,
               "%s/lib/monitor/%s.jar" % (FINAL_TARGET, module_name),
               "%s/class/%s" % (TMP_DIR, module_name))
    ]

    XML_LINES.extend(LINES)
    
#Junit所需字符串 
    XML_JUNIT_FILESET = """
        <fileset dir="%s/%s">
            <include name="**/*Test.class"/>
        </fileset>
    """ % (TMP_CLASS_DIR,module_name)
    XML_JUNIT_FILESETS.append(XML_JUNIT_FILESET)
 
#覆盖率所需字符串   
    XML_COVERAGE_CLASS_FILESET = """
        <fileset dir="%s/%s"/>
    """ % (TMP_CLASS_DIR,module_name)
    XML_COVERAGE_CLASS_FILESETS.append(XML_COVERAGE_CLASS_FILESET)
    XML_COVERAGE_SRC_FILESET = """
        <fileset dir="%s/%s/src"/>
    """ %(XMODULES_DIR,module_name)
    XML_COVERAGE_SRC_FILESETS.append(XML_COVERAGE_SRC_FILESET)

    PROCESSING_MODULES.remove(module_name)
    PROCESSED_MODULES.add(module_name)

for dir_name in os.listdir("%s/modules" % MONITOR_SRC_HOME):
    if dir_name.startswith("."):
        continue
    _process_module(dir_name)

STR_JUNIT_FILESETS = "\n".join(XML_JUNIT_FILESETS)
STR_COVERAGE_CLASS_FILESETS = "\n".join(XML_COVERAGE_CLASS_FILESETS)
STR_COVERAGE_SRC_FILESETS = "\n".join(XML_COVERAGE_SRC_FILESETS)


#XML_LINES.append(
#"""
#<target name="junit" depends="monitor">
#    	 <mkdir dir="${report.path}"/>
#
#        <junit printsummary="true" fork="true">
#        		<formatter type="xml" usefile="true"/>
#         		 <classpath>  
#            		<path refid="classpath_monitor"/>
#            </classpath> 
#         		 
#         		<batchtest fork="true" todir="${report.path}" haltonfailure="no">
#         			  %s	
#         		</batchtest>
#        </junit>
#  
#    </target>
#    
#    <target name="junitreport" depends="junit">
#    		<junitreport todir="${report.path}">
#            <fileset dir="${report.path}">
#                <include name="TEST-*.xml" />
#            </fileset>
#            <report format="frames" todir="${report.path}" />
#        </junitreport>
#        
#		</target>
#
#""" % STR_JUNIT_FILESETS
#)

XML_LINES.append(
"""
<target name="coverage" depends="monitor">
        <jacoco:coverage destfile="${basedir}/jacoco.exec">  
            <junit haltonfailure="false" fork="true" printsummary="true">
            		<classpath>
                	  <!--<path refid="classpath_monitor_test_libfirst"/>-->
            		    <!--<path refid="classpath_monitor_test_batch"/>-->
            		    <path refid="classpath_monitor"/>
            		    <path refid="classpath_monitor_test_coverage"/>
            		    <!--<dirset dir="/root/jenkins/workspace/build_monitor/modulized/build/dist/tmp/class"/>-->
            		</classpath>   
                <formatter type="xml" />  
                    <batchtest fork="true" todir="${report.path}" haltonfailure="no">
                    %s     				    
         		        </batchtest>  
            </junit> 
             
        </jacoco:coverage>  
    </target>  
    
    <target name="junitreport" depends="coverage">
        <junitreport todir="${report.path}">
            <fileset dir="${report.path}">
                <include name="TEST-*.xml" />
            </fileset>
            <report format="frames" todir="${report.path}" />
        </junitreport>
    </target>
    
    <target name="coveragereport" depends="junitreport">  
    		<mkdir dir="${report.path.coverage}" />
        <jacoco:report>  
            <executiondata>  
                <file file="${basedir}/jacoco.exec" />  
            </executiondata>  
            <structure name="Monitor Project unit test coverage rate report.">  
                <classfiles>  
                    %s
                </classfiles>  
                <sourcefiles encoding="UTF-8">  
                    %s
                </sourcefiles>  
            </structure>  
  
            <html footer="Monitor Project." destdir="${report.path.coverage}" />  
            <csv destfile="${report.path.coverage}/coverage-report.csv" />  
            <xml destfile="${report.path.coverage}/coverage-report.xml" />  
        </jacoco:report>  
    </target> 
""" % (STR_JUNIT_FILESETS,STR_COVERAGE_CLASS_FILESETS,STR_COVERAGE_SRC_FILESETS)
) 

# For javadoc
JAVASRC4DOC = set()

def _is_javadoc_file(filepath):
    if not filepath.endswith(".java"):
        return False

    head = open(filepath).read(4096).lower()
    if "@javadoc" in head:
        return True
    return False

def _get_javadoc_src(srcdir):
    for relative_path, dirnames, filenames in os.walk(srcdir):
        for filename in filenames:

            filepath = os.path.join(srcdir, relative_path, filename)
            if _is_javadoc_file(filepath):
                JAVASRC4DOC.add(filepath)

_get_javadoc_src("%s/monitor/src" % MONITOR_SRC_HOME)
for name in PROCESSED_MODULES:
    _get_javadoc_src("%s/modules/%s/src" % (MONITOR_SRC_HOME, name))

XML_LINES.append(
"""
    <target name="javadoc">
        <javadoc access="public" classpathref="classpath_monitor" destdir="%s/javadoc"
        packagenames="monitor.*,module.*" sourcefiles="%s" />
    </target>
""" % (TMP_DIR, ",".join(JAVASRC4DOC))
)

# End
XML_LINES.append(
"""
    <target name="monitor_dirs">
        <mkdir dir="%s" />
    </target>
    <target name="monitor" depends="monitor_dirs,%s" />
""" % ("%s/lib/monitor" % FINAL_TARGET,
       ",".join(["jar_" + module_name for module_name in PROCESSED_MODULES]))
)

XML_LINES.append("</project>")
print "\n".join(XML_LINES)
