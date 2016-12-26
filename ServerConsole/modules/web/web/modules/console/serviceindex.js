/* serviceindex.js 服务指标模块 */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleServiceIndex_serviceIndexManagement'),
        cls: 'fa-user-md',
        name: 'serviceindex',
        order: 4,
        callback: function() {
            AppRouter.navigate('serviceindexlist/' + Math.random(), {trigger: true});
        }
    });
    AppRouter.route("serviceindexlist/:rand", function() {
        AppEvent.trigger('console.serviceindexlist.beforeLoad');
        var $nav = $('div[name=serviceindexlist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
        serviceIndexInit();
        AppEvent.trigger('console.serviceindexlist.afterLoad');
    });
    // 服务指标操作菜单
    var serviceIndexMenus = [{
        name: 'add-serviceindex',
        text: i18n.get('consoleServiceIndex_add_serviceindex'),
        order: 1,
        onClick: function(serviceindex) {
            // 添加服务指标
        }
    }, {
        name: 'edit-serviceindex',
        text: i18n.get('consoleServiceIndex_edit_serviceindex'),
        order: 2,
        onClick: function(serviceindex) {
            // 编辑服务指标
        }
    }, {
        name: 'delete-serviceindex',
        text: i18n.get('consoleServiceIndex_delete_serviceindex'),
        order: 3,
        onClick: function(serviceindex) {
            // 删除服务指标
        }
    }];
    
    function serviceIndexInit() {
        var tpl = Handlebars.compile(require('./serviceindex/serviceindexlist.tpl'));
        init.insertCenter(tpl);
        $('.body-center').layout();
        $('input').placeholder();
        $('#serviceindexlist').table({
            url: '/service/api/listServiceIndex?order=' + config.order,
            pagination: true,
            fitColumns: true,
            showBorder: false,
            toolbar: '.data-toolbar',
            columns: [{
                field: 'id',
                sortable: true,
                checkbox: true
            }, {
                field: 'service_index_id',
                title: i18n.get('consoleServiceIndex_serviceindex_id'),
                width: 100
            }, {
                field: 'service_id',
                title: i18n.get('consoleService_service_id'),
                width: 100
            }, {
                field: 'name',
                title: i18n.get('consoleServiceIndex_serviceindex_name'),
                width: 200
            }, {
                field: 'key',
                title: i18n.get('consoleServiceIndex_serviceindex_key'),
                width: 200
            }, {
                field: 'type',
                title: i18n.get('consoleServiceIndex_serviceindex_type'),
                width: 200
            }, {
                field: 'memo',
                title: i18n.get('consoleServiceIndex_serviceindex_memo'),
                width: 350
            }, {
                field: 'opt',
                title: i18n.get('consoleServiceIndex_serviceindex_operation'),
                width: 80,
                formatter: function(val, row, index) {
                    return '<i class="fa fa-cog col-opt serviceindex-opt"></i>';
                }
            }],
            onInited: function() {
                $('#serviceindexlist').on('click', '.serviceindex-opt', function(event) {
                    event.preventDefault();
                    var $this = $(this);
                    $('#serviceindexlist').table('clearSelections');
                    if (!$('#xmenu').length) {
                        $this.parents('.xtable-row').click();
                        $.menu({
                            showIcon: false,
                            rows: _.sortBy(serviceIndexMenus, 'order'),
                            data: $('#serviceindexlist').table('getSelected'),
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