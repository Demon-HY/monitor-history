1. config.json是运行环境的配置文件，配置项说明如下：
   server：服务端ip地址和端口，如 192.168.1.182:80
   path: 本地工作目录的绝对路径，如 d:/www/netdisk
   port: 本地服务所使用的端口，默认 8088
   local: 请求本地文件的路径列表，不满足列表中的请求则被代理到server

2. 前端代码位于web目录下，缺省页面 login.html 必须要有

3. 配置好config.json之后运行 run.bat 即可启动本地服务，通过 http://localhost:port 来访问， 如 http://localhost:8088

4. 需要安装Node.js和Ruby环境，并且加入系统环境变量，用于自动化构建和sass编译
   https://nodejs.org/download/
   http://rubyinstaller.org/downloads/
   安装完成之后在web目录下面依次执行
   gem install sass
   npm install grunt-cli -g
   npm install

5. 安装成功之后在web目录下面执行 grunt dev 即可开启web\wap的sass实时编译，后续一些实时监控的任务也会加入到里面来

6、grunt wap_icons 手机版icons合成
   grunt icons  web版icons合成__

