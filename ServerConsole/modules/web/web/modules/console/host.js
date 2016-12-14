/* 主机管理模块 */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    
    function hostInit() {
        var tpl = Handlebars.compile(require('./host/hostlist.tpl'));
        init.insertCenter(tpl);
        
        $('#hostlist').table({
            url: '/host/api/getHostList?order=' + config.order,
            pagination: true,
            fitColumns: true,
            showBorder: false,
            toolbar: '.data-toolbar',
            columns: [{
                field: 'id',
                sortable: true, /* 是否允许排序 */
                checkbox: true
            }, {
                field: 'id',
                title: 'Id',
                width: 200
            }, {
                field: 'ip',
                title: 'IP',
                width: 200
            }, {
                field: 'status',
                title: '状态',
                width: 270
            }, 
        });
    }
});