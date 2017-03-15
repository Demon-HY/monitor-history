const http = require('http'),
    httpProxy = require('http-proxy'),
    proxy = httpProxy.createProxyServer({}),
    fs = require('fs'),
    path = require('path'),
    getContentType = require('./content-type'),
    config = require('./config.json'),
	open = require('open');

console.log('1');


proxy.on('error', function(err, req, res) {
    res.writeHead(500, {
        'Content-Type': 'text/plain'
    });
    res.end('500 error');
});


const server = http.createServer(function(req, res) {
    var _url = req.url,
        _file,
        _localPath,
        _localFile,
        _ext,
        _stream,
        _isLocal = false;
    for (var i in config.map) {
        if (_url.startsWith(i)) {
            _url = config.map[i] + _url.substr(i.length);
        }
    }
    console.log('url=' + _url);
    for (var i = 0; i < config.local.length; i++) {
        var length = config.local[i].length;
        if (_url.startsWith(config.local[i])) {
            _isLocal = true;
            break;
        }
    }
    if (_isLocal === true) {
        _file = _url.replace(/\?.*/ig, '');
        _ext = path.extname(_file);
        _localPath = path.dirname(__dirname);
        _localFile = _localPath + _file;
        if (fs.existsSync(_localFile)) {
            res.writeHead(200, {
                'Content-Type': getContentType(_ext),
                'Server': 'Coco'
            });
            _stream = fs.createReadStream(_localFile, {
                flags: 'r',
                encoding: null
            });
            _stream.on('error', function() {
                res.writeHead(404, {
                    'Content-Type': 'text/html'
                });
                res.end('<h1>404, Read Error</h1>');
            });
            _stream.pipe(res);
            _stream.on('end', function() {
                _stream = null;
            });
        } else {
            res.writeHead(404, {
                'Content-Type': 'text/html'
            });
            res.end('<h1>404, Not Found</h1>');
        }
    } else {
        proxy.web(req, res, {
            target: 'http://' + config.server
        });
    }
});


server.on('error', function(e) {
    console.log(e);
});

console.log('代理服务器 ' + config.server);
console.log('WWW路径 ' + path.dirname(__dirname))
console.log('启动成功,请访问 http://127.0.0.1:' + config.dev_port);
console.log('正在监听端口：' + config.dev_port + '...');
server.listen(config.dev_port);
open('http://127.0.0.1:' + config.dev_port + '/web/login.html');
