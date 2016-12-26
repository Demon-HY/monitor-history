/* maintain.js 维护模块 */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleMaintain_maintainManagement'),
        cls: 'fa-heartbeat',
        name: 'maintain',
        order: 10,
        callback: function() {
            AppRouter.navigate('maintainlist/' + Math.random(), {trigger: true});
        }
    });
    AppRouter.route("maintainlist/:rand", function() {
        AppEvent.trigger('console.maintainlist.beforeLoad');
        var $nav = $('div[name=maintainlist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
        maintainInit();
        AppEvent.trigger('console.maintainlist.afterLoad');
    });
    // 维护操作菜单
    var maintainMenus = [{
        name: 'add-maintain',
        text: i18n.get('consoleMaintain_add_maintain'),
        order: 1,
        onClick: function(maintain) {
            // 添加维护
        }
    }, {
        name: 'edit-maintain',
        text: i18n.get('consoleMaintain_edit_maintain'),
        order: 2,
        onClick: function(maintain) {
            // 编辑维护
        }
    }, {
        name: 'delete-maintain',
        text: i18n.get('consoleMaintain_delete_maintain'),
        order: 3,
        onClick: function(maintain) {
            // 删除维护
        }
    }];
    
    function maintainInit() {
        var tpl = Handlebars.compile(require('./maintain/maintainlist.tpl'));
        init.insertCenter(tpl);
        $('.body-center').layout();
        $('input').placeholder();
        $('#maintainlist').table({
            url: '/maintain/api/listMaintain?order=' + config.order,
            pagination: true,
            fitColumns: true,
            showBorder: false,
            toolbar: '.data-toolbar',
            columns: [{
                field: 'id',
                sortable: true, /* 是否允许排序 */
                checkbox: true
            }, {
                field: 'maintain_id',
                title: i18n.get('consoleMaintain_maintain_id'),
                width: 100
            }, {
                field: 'name',
                title: i18n.get('consoleMaintain_maintain_name'),
                width: 200
            }, {
                field: 'content',
                title: i18n.get('consoleMaintain_maintain_content'),
                width: 200
            }, {
                field: 'start_time',
                title: i18n.get('consoleMaintain_maintain_start_time'),
                width: 200
            }, {
                field: 'end_time',
                title: i18n.get('consoleMaintain_maintain_end_time'),
                width: 200
            }, {
                field: 'opt',
                title: i18n.get('consoleMaintain_maintain_operation'),
                width: 80,
                formatter: function(val, row, index) {
                    return '<i class="fa fa-cog col-opt maintain-opt"></i>';
                }
            }],
            onInited: function() {
                $('#maintainlist').on('click', '.maintain-opt', function(event) {
                    event.preventDefault();
                    var $this = $(this);
                    $('#maintainlist').table('clearSelections');
                    if (!$('#xmenu').length) {
                        $this.parents('.xtable-row').click();
                        $.menu({
                            showIcon: false,
                            rows: _.sortBy(maintainMenus, 'order'),
                            data: $('#maintainlist').table('getSelected'),
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