/* web/module/console/main.js */
/* 控制台入口 */
$(function(){
	// 前端配置文件
    var config = require("config");
    var util=require('util');
    
    // 全局事件对象
    window.AppEvent = $({});
    var Router = Backbone.Router.extend({
        routes: {
            "*other": function(callback, args) {
                return false;
            }
        },
        execute: function(callback, args) {
            if (callback) callback.apply(this, args);
        }
    });
    // 全局路由对象
    window.AppRouter = new Router();
    
    // 侧边栏
    $('.nav').click(function(event){
    	// 选中效果
    	$('.nav').css("background-color", "#FFF");
    	$(this).css("background-color", "#EBECF8");
    });
    
    // 右边内容部分
    /* host */
    
});

