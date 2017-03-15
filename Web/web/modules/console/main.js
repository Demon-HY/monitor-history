/* web/module/console/main.js */
/* 控制台入口 */
define(function(require, exports, module) {
	// 前端配置文件
    var config = require("config");
    var util=require('util');
    
    // 全局事件对象
    window.AppEvent = $({});
    var Router = Backbone.Router.extend({
        routes: {
            "*other": function(callback, args) {
                if (!(window.location.hash.substr(1))) {
                    var $node = $('.nav-wrap').find('.nav-item:eq(0)');
                    $node.click();
                    if ($node.next('.nav-child').length) {
                        $node.next('.nav-child').find('.nav-item:eq(0)').click();
                    }
                } else {
                    $.alert('您要访问的路径不存在！');
                    $('.body-center').empty();
                }
                return false;
            }
        },
        execute: function(callback, args) {
            $(document).trigger('click');
            document.title = config.title;
            $('.nav-current').removeClass('nav-current');
            if (callback) callback.apply(this, args);
        }
    });
    // 全局路由对象
    window.AppRouter = new Router();
    
    // 需要加载的模块
    var modules;
    $.request({
        url: '/user/api/getUserInfo',
        done: function(data) {
            if (data.stat == 'OK') {
                window.userinfo = data.user;
                
                var module = ['helper', './init'];
                var init = function () {
                    module = _.uniq(module);
                    var plugins = config.plugins.console;
                    $.each(plugins, function(i, v) {
                        if (v.split('/').length > 1) {
                            plugins[i] = '../../plugins/' + v;
                        } else {
                            plugins[i] = '../../plugins/' + v + '/index';
                        }
                    });
                    modules = _.union(modules, config.modules.console, plugins);
                    require.async(modules, function() {
                        // 模块加载完成之后执行各模块初始化
                        $.each(arguments, function(i, v) {
                            if (typeof(v) == 'object' && v.hasOwnProperty("initialize")) v.initialize();
                        });
                        // 启动路由监听
                        Backbone.history.start();
                    });
                };
                init();
            }
        }
    });
    
    // 侧边栏
    $('.nav').click(function(event){
        // 选中效果
        $('.nav').css("background-color", "#FFF");
        $(this).css("background-color", "#EBECF8");
    });
});

