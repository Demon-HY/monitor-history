/* action.js 报警模块 */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleAction_actionManagement'),
        cls: 'fa-bell',
        name: 'action',
        order: 8,
        callback: function() {
            AppRouter.navigate('actionlist/' + Math.random(), {trigger: true});
        }
    });
    AppRouter.route("actionlist/:rand", function() {
        AppEvent.trigger('console.actionlist.beforeLoad');
        var $nav = $('div[name=actionlist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
        actionInit();
        AppEvent.trigger('console.actionlist.afterLoad');
    });
    // 报警操作菜单
    var actionMenus = [{
        name: 'add-action',
        text: i18n.get('consoleAction_add_action'),
        order: 1,
        onClick: function(action) {
            // 添加报警
        }
    }, {
        name: 'edit-action',
        text: i18n.get('consoleAction_edit_action'),
        order: 2,
        onClick: function(action) {
            // 编辑报警
        }
    }, {
        name: 'delete-action',
        text: i18n.get('consoleAction_delete_action'),
        order: 3,
        onClick: function(triggerexpression) {
            // 删除报警
        }
    }];
    
    function actionInit() {
        var tpl = Handlebars.compile(require('./action/actionlist.tpl'));
        init.insertCenter(tpl);
        $('.body-center').layout();
        $('input').placeholder();
        $('#actionlist').table({
            url: '/action/api/listAction?order=' + config.order,
            pagination: true,
            fitColumns: true,
            showBorder: false,
            toolbar: '.data-toolbar',
            columns: [{
                field: 'id',
                sortable: true, /* 是否允许排序 */
                checkbox: true
            }, {
                field: 'action_id',
                title: i18n.get('consoleAction_action_id'),
                width: 100
            }, {
                field: 'name',
                title: i18n.get('consoleAction_action_name'),
                width: 200
            }, {
                field: 'interval',
                title: i18n.get('consoleAction_action_interval'),
                width: 200
            }, {
                field: 'notice',
                title: i18n.get('consoleAction_action_notice'),
                width: 200
            }, {
                field: 'subject',
                title: i18n.get('consoleAction_action_subject'),
                width: 200
            }, {
                field: 'message',
                title: i18n.get('consoleAction_action_message'),
                width: 350
            }, {
                field: 'enabled',
                title: i18n.get('consoleAction_action_enabled'),
                width: 350
            }, {
                field: 'opt',
                title: i18n.get('consoleAction_action_operation'),
                width: 80,
                formatter: function(val, row, index) {
                    return '<i class="fa fa-cog col-opt action-opt"></i>';
                }
            }],
            onInited: function() {
                $('#actionlist').on('click', '.action-opt', function(event) {
                    event.preventDefault();
                    var $this = $(this);
                    $('#actionlist').table('clearSelections');
                    if (!$('#xmenu').length) {
                        $this.parents('.xtable-row').click();
                        $.menu({
                            showIcon: false,
                            rows: _.sortBy(actionMenus, 'order'),
                            data: $('#actionlist').table('getSelected'),
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