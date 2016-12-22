/* host.js 主机管理模块 */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleHost_hostManagement'),
        cls: 'fa-server',
        name: 'host',
        order: 1,
        callback: function() {
            AppRouter.navigate('hostlist/' + Math.random(), {trigger: true});
        }
    });
    AppRouter.route("hostlist/:rand", function() {
        AppEvent.trigger('console.hostlist.beforeLoad');
        var $nav = $('div[name=hostlist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
        hostInit();
        AppEvent.trigger('console.hostlist.afterLoad');
    });
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
        $('.body-center').layout();
        $('input').placeholder();
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
                field: 'host_id',
                title: 'Id',
                width: 100
            }, {
                field: 'name',
                title: 'Name',
                width: 200
            }, {
                field: 'monitored',
                title: 'Monitored',
                width: 200
            }, {
                field: 'interval',
                title: 'Interval',
                width: 200
            }, {
                field: 'ip',
                title: 'IP',
                width: 400
            }, {
                field: 'status',
                title: 'Status',
                width: 200
            }, {
                field: 'memo',
                title: 'Memo',
                width: 400
            }],
            onInited: function() {
                $('#hostlist').on('click', function(event) {
                    event.preventDefault();
                    $('#hostlist').table('clearSelections');
                    /*if (!$('#xmenu').length) {
                        $(this).parents('.xtable-row').click();
                        $.menu({
                            showIcon: false,
                            rows: _.sortBy(hostMenus, 'order'),
                            data: $('#datalist').table('getSelected'),
                            position: {
                                left: event.pageX - 85,
                                top: event.pageY + 10
                            }
                        });
                    } else {
                        $(document).trigger('click');
                    }*/
                    return false;
                });
            },
        });
        // add host
        $(".add-host").click(function() {
            addHost();
        });
    }

    // 添加主机
    function addHost () {
        if (!$('#create-dialog').length) {
            $('body').append('<div id="create-dialog"></div>');
        }
        $('#create-dialog').dialog({
            title: i18n.get('consoleHost_add_host'),
            width: 600,
            height: 650,
            data: Handlebars.compile(require('./host/create.tpl')),
            onInited: function() {},
            buttons: [{
                text: i18n.get('consoleHost_confirm'),
                name: 'save',
                cls: 'btn-primary',
                handler: function() {
                    var res = $('#fm').valid('formValid');
                    if (res) {
                        $.request({
                            url: '/host/api/addHost',
                            params: form,
                            done: function(data) {
                                if (data.stat == 'OK') {
                                    $('#create-dialog').dialog('close');
                                    $('#datalist').table('reload');
                                    // $.tip('主机添加成功！');
                                    $.tip(i18n.get('consoleHost_success_addHost'));
                                } else {
                                    $.alert(data.errText);
                                }
                            }
                        });
                    }
                }
            }, {
                text: i18n.get('consoleHost_cancel'),
                name: 'cancel',
                handler: function() {
                    $('#create-dialog').dialog('close');
                }
            }]
        });
    }
});