/* actionoperation.js 报警条件模块 */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleActionOperation_actionOperationManagement'),
        cls: 'fa-bell',
        name: 'actionoperation',
        order: 9,
        callback: function() {
            AppRouter.navigate('actionoperationlist/' + Math.random(), {trigger: true});
        }
    });
    AppRouter.route("actionoperationlist/:rand", function() {
        AppEvent.trigger('console.actionoperationlist.beforeLoad');
        var $nav = $('div[name=actionoperationlist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
        actionOperationInit();
        AppEvent.trigger('console.actionoperationlist.afterLoad');
    });
    // 报警操作菜单
    var actionOperationMenus = [{
        name: 'add-actionoperation',
        text: i18n.get('consoleActionOperation_add_action_operation'),
        order: 1,
        onClick: function(actionoperation) {
            // 添加报警条件
        }
    }, {
        name: 'edit-actionoperation',
        text: i18n.get('consoleActionOperation_edit_action_operation'),
        order: 2,
        onClick: function(actionoperation) {
            // 编辑报警条件
        }
    }, {
        name: 'delete-actionoperation',
        text: i18n.get('consoleActionOperation_delete_action_operation'),
        order: 3,
        onClick: function(triggerexpression) {
            // 删除报警条件
        }
    }];
    
    function actionOperationInit() {
        var tpl = Handlebars.compile(require('./actionoperation/actionoperationlist.tpl'));
        init.insertCenter(tpl);
        $('.body-center').layout();
        $('input').placeholder();
        $('#actionoperationlist').table({
            url: '/action/api/listActionOperation?order=' + config.order,
            pagination: true,
            fitColumns: true,
            showBorder: false,
            toolbar: '.data-toolbar',
            columns: [{
                field: 'id',
                sortable: true, /* 是否允许排序 */
                checkbox: true
            }, {
                field: 'action_operation_id',
                title: i18n.get('consoleActionOperation_actionoperation_id'),
                width: 100
            }, {
                field: 'action_id',
                title: i18n.get('consoleAction_action_id'),
                width: 100
            }, {
                field: 'name',
                title: i18n.get('consoleActionOperation_actionoperation_name'),
                width: 200
            }, {
                field: 'step',
                title: i18n.get('consoleActionOperation_actionoperation_step'),
                width: 200
            }, {
                field: 'action_type',
                title: i18n.get('consoleActionOperation_actionoperation_action_type'),
                width: 200
            }, {
                field: 'msg_format',
                title: i18n.get('consoleActionOperation_actionoperation_msg_format'),
                width: 200
            }, {
                field: 'opt',
                title: i18n.get('consoleActionOperation_actionoperation_operation'),
                width: 80,
                formatter: function(val, row, index) {
                    return '<i class="fa fa-cog col-opt actionoperation-opt"></i>';
                }
            }],
            onInited: function() {
                $('#actionoperationlist').on('click', '.actionoperation-opt', function(event) {
                    event.preventDefault();
                    var $this = $(this);
                    $('#actionoperationlist').table('clearSelections');
                    if (!$('#xmenu').length) {
                        $this.parents('.xtable-row').click();
                        $.menu({
                            showIcon: false,
                            rows: _.sortBy(actionOperationMenus, 'order'),
                            data: $('#actionoperationlist').table('getSelected'),
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