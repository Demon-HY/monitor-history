# monitor
分布式监控系统

# 项目需求:

1. 同时监控多台服务器的硬件信息,比如 CPU,网卡,硬盘等
2. 监控服务器上各个软件的运行状态
3. 实现报警功能,希望当某一台服务器的某一个服务达到阈值就触发报警
4. 远程操作服务器

# 项目结构
|--docs(项目中用到的一些知识整理)
|--MonitorClient(客户端，用来采集数据和向服务端汇报数据，并执行服务端发送的特定指令)
    |--__init__.py
    |--main
        |--__init__.py
        |--MonitorClient.py(程序入口)
    |--conf(配置)
        |--__init__.py
        |--settings.py
    |--core(核心逻辑)
        |--__init__.py
        |--client.py(和服务端通讯的 HTTP 接口类)
        |--main.py(主程序)
    |--plugins(插件)
        |--__init__.py
        |--Linux(基于 Linux 插件)
        |--Windows(基于 Windows 插件)
        |--plugin_api.py(插件对外接口)
|--ServerConsole(控制中心，它是连接客户端和数据中心的控制台，主要是做逻辑处理和数据的收发工作)
    |--build(项目构建)
    |--modules(项目模块化，每个模块是一个单独的工程)
        |--action(报警)
            |--bin(可执行文件)
            |--src(源码)
                |--module.aciton(包路径)
                    |--Init.java(模块初始化类)
                    |--ActionHttpApi.java(模块对外的 HTTP 接口类)
                    |--ActionApi.java(接口核心逻辑处理类)
                    |--ActionModel.java(接口数据库操作类)
            |--action.xml(多语言配置)
            |--module.properties(模块配置文件，用于设置一些参数和记录其他模块的依赖关系)
        |--auth(认证)
        |--group(群组)
        |--host(主机)
        |--imageCode(图片验证码)
        |--initdata(项目初始化)
        |--maintain(维护)
        |--SDK(工具包，提供一些公共类和对外的接口)
        |--service(服务)
        |--template(模板)
        |--trigger(触发器)
        |--user(用户)
        |--web(实现web的静态文件访问和重定向)
    |--monitor(控制中心核心主类，modules下的模块由该工程去加载)
        |--bin(可执行文件)
        |--conf(配置文件)
            |--server.properties
        |--lib(用到的第三方库)
        |--src(源码)
    |--test(接口测试)
        |--api(访问服务端的HTTP接口)
            |--__init__.py
            |--auth_api.py(访问服务端 auth 模块的接口)
            |--util.py(工具包，提供了POST请求和GET请求的封装)
        |--test_api(对上面api接口的测试)
            |--__init__.py
            |--test_auth.py(测试 auth 模块接口)
        |--config.py(运行时配置)
        |--hlp.py(提供了获取用户token，清除用户，生成随机数等方法)
        |--restat.py(服务端返回错误码汇总)
        |--run_all_test.py(运行test_api目录下所有以 'test_' 开头的测试文件)
        |--test_auth.py(只运行auth模块的测试)
|--ServerData(数据中心，处理控制中心传递的数据，并返回给控制中心)
|--Web(web 端源码)
    |--node_modules(node 模块)
        |--fs(node fs模块)
    |--tools_dev_server(node 核心服务和运行配置)
        |--node_modules(node 提供的一个小服务，主要用来在本地测试时做反向代理)
        |--config.json(配置文件)
        |--content-type.js(记录了各种支持的返回类型)
        |--dev_server.js(服务入口)
        |--package.json(包信息)
        |--run_dev.bat(Windows 下运行脚本)
    |--web(web 源码)
    |--run_dev.bat(把 tools_dev_server 下的运行脚本复制到了这里，方便启动)
|--README.md

# 工作记录
工作任务安排被迁移到了 Trello 上面，访问地址是 https://trello.com/b/az8GMGPA
