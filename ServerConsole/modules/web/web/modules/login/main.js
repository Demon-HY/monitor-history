define(function(require, exports, module) {
	var util = require('util');
    var request = util.getRequest();
    // 前端配置文件
    var config = require("config");
    // 登录页插件
    var plugins = config.plugins.login;
    $.each(plugins, function(i, v) {
        if (v.split('/').length > 1) {
            plugins[i] = '../../plugins/' + v;
        } else {
            plugins[i] = '../../plugins/' + v + '/index';
        }
    });
    
    require.async(plugins, function() {
        // 模块加载完成之后执行各模块初始化
        $.each(arguments, function(i, v) {
            if (v.hasOwnProperty("initialize")) v.initialize();
        });
        loginInit();
    });
    
    function loginInit() {
    	// 验证登录
    	$.request({
            url: '/auth/api/checkLogin',
            checkLogin: false,
            done: function(data) {
                if (data.stat == 'OK') {
                    window.location.href = '/web/index.html';
                }
            }
        });
    };
	
	
	// 检测浏览器cookie是否开启
    function detectCookie() {
        var dt = new Date();
        dt.setSeconds(dt.getSeconds() + 60);
        document.cookie = "cookietest=1; expires=" + dt.toGMTString();
        var cookiesEnabled = document.cookie.indexOf("cookietest=") != -1;
        if(!cookiesEnabled) { 
            alert('该浏览器未启用cookie，请启用cookie后再进行操作！');
        }
    }
});