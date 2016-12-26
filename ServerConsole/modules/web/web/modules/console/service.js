/* service.js 服务模块 */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleService_serviceManagement'),
        cls: 'fa-user-md',
        name: 'service',
        order: 3,
        callback: function() {
            AppRouter.navigate('servicelist/' + Math.random(), {trigger: true});
        }
    });
    AppRouter.route("servicelist/:rand", function() {
        AppEvent.trigger('console.servicelist.beforeLoad');
        var $nav = $('div[name=servicelist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
       serviceInit();
        AppEvent.trigger('console.servicelist.afterLoad');
    });
    // 服务列表操作菜单
    var serviceMenus = [{
        name: 'add-service',
        text: i18n.get('consoleService_add_service'),
        order: 1,
        onClick: function(service) {
            // 添加服务
        }
    }, {
        name: 'edit-service',
        text: i18n.get('consoleService_edit_service'),
        order: 2,
        onClick: function(service) {
            // 编辑服务
        }
    }, {
        name: 'delete-service',
        text: i18n.get('consoleService_delete_service'),
        order: 3,
        onClick: function(service) {
            // 删除服务
        }
    }];
    
    function serviceInit() {
        var tpl = Handlebars.compile(require('./service/servicelist.tpl'));
        init.insertCenter(tpl);
        $('.body-center').layout();
        $('input').placeholder();
        $('#servicelist').table({
            url: '/service/api/listService?order=' + config.order,
            pagination: true,
            fitColumns: true,
            showBorder: false,
            toolbar: '.data-toolbar',
            columns: [{
                field: 'id',
                sortable: true, /* 是否允许排序 */
                checkbox: true
            }, {
                field: 'service_id',
                title: i18n.get('consoleService_service_id'),
                width: 100
            }, {
                field: 'name',
                title: i18n.get('consoleService_service_name'),
                width: 200
            }, {
                field: 'interval',
                title: i18n.get('consoleService_service_interval'),
                width: 200
            }, {
                field: 'plugin_name',
                title: i18n.get('consoleService_plugin_name'),
                width: 400
            }, {
                field: 'status',
                title: i18n.get('consoleService_service_status'),
                width: 200
            }, {
                field: 'memo',
                title: i18n.get('consoleService_service_memo'),
                width: 400
            }, {
                field: 'opt',
                title: i18n.get('consoleService_service_operation'),
                width: 80,
                formatter: function(val, row, index) {
                    return '<i class="fa fa-cog col-opt service-opt"></i>';
                }
            }],
            onInited: function() {
                $('#servicelist').on('click', '.service-opt', function(event) {
                    event.preventDefault();
                    var $this = $(this);
                    $('#servicelist').table('clearSelections');
                    if (!$('#xmenu').length) {
                        $this.parents('.xtable-row').click();
                        $.menu({
                            showIcon: false,
                            rows: _.sortBy(serviceMenus, 'order'),
                            data: $('#servicelist').table('getSelected'),
                            position: {
                                left: event.pageX - 85,
                                top: event.pageY + 10
                            }
                        })
                    } else {
                        $(document).trigger('click');
                    }
                    return false;
                });
            }
        });
    }
});