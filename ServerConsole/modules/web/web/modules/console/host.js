/* 主机管理模块 */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    
    // 主机列表操作菜单
    var hostMenus = [{
        name: 'add-host',
        text: i18n.get('consoleHost_add_host'),
        order: 1,
        onClick: function(host) {
            // 添加主机
        }
    }, {
        name: 'edit-host',
        text: i18n.get('consoleHost_edit_host'),
        order: 2,
        onClick: function(host) {
            // 编辑主机
        }
    }, {
        name: 'delete-host',
        text: i18n.get('consoleHost_delete_host'),
        order: 3,
        onClick: function(host) {
            // 删除主机
        }
    }];
    
    function hostInit() {
        var tpl = Handlebars.compile(require('./host/hostlist.tpl'));
        init.insertCenter(tpl);
        $('#hostlist').table({
            url: '/host/api/listHost?order=' + config.order,
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
                width: 300
            }, {
                field: 'ip',
                title: 'IP',
                width: 400
            }, {
                field: 'status',
                title: '状态',
                width: 650
            }],
            onInited: function() {
                $('#hostlist').on('click', function(event) {
                    event.preventDefault();
                    $('#hostlist').table('clearSelections');
                    if (!$('#xmenu').length) {
                        /*$(this).parents('.xtable-row').click();
                        $.menu({
                            showIcon: false,
                            rows: _.sortBy(hostMenus, 'order'),
                            data: $('#datalist').table('getSelected'),
                            position: {
                                left: event.pageX - 85,
                                top: event.pageY + 10
                            }
                        });*/
                    } else {
                        $(document).trigger('click');
                    }
                    return false;
                });
            },
        });
    }
    hostInit();
});