// 页面初始化模块
define(function(require, exports, module) {
	var config = require('config');
	
	/* 对外抛出的方法 */
	module.exports = {
		insertCenter: function(html) {
            $(".main-r").html(html);
        },
	};
});