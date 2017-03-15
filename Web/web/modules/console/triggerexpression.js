/* triggerexpression.js 触发条件模块 */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleTriggerExpression_triggerExpressionManagement'),
        cls: 'fa-exclamation-triangle',
        name: 'triggerexpression',
        order: 7,
        callback: function() {
            AppRouter.navigate('triggerexpressionlist/' + Math.random(), {trigger: true});
        }
    });
    AppRouter.route("triggerexpressionlist/:rand", function() {
        AppEvent.trigger('console.triggerexpressionlist.beforeLoad');
        var $nav = $('div[name=triggerexpressionlist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
        triggerExpressionInit();
        AppEvent.trigger('console.triggerexpressionlist.afterLoad');
    });
    // 触发条件操作菜单
    var triggerExpressionMenus = [{
        name: 'add-triggerexpression',
        text: i18n.get('consoleTriggerExpression_add_triggerexpression'),
        order: 1,
        onClick: function(triggerexpression) {
            // 添加触发条件
        }
    }, {
        name: 'edit-triggerexpression',
        text: i18n.get('consoleTriggerExpression_edit_triggerexpression'),
        order: 2,
        onClick: function(triggerexpression) {
            // 编辑触发条件
        }
    }, {
        name: 'delete-triggerexpression',
        text: i18n.get('consoleTriggerExpression_delete_triggerexpression'),
        order: 3,
        onClick: function(triggerexpression) {
            // 删除触发条件
        }
    }];
    
    function triggerExpressionInit() {
        var tpl = Handlebars.compile(require('./triggerexpression/triggerexpressionlist.tpl'));
        init.insertCenter(tpl);
        $('.body-center').layout();
        $('input').placeholder();
        $('#triggerexpressionlist').table({
            url: '/trigger/api/listTriggerExpression?order=' + config.order,
            pagination: true,
            fitColumns: true,
            showBorder: false,
            toolbar: '.data-toolbar',
            columns: [{
                field: 'id',
                sortable: true, /* 是否允许排序 */
                checkbox: true
            }, {
                field: 'trigger_expression_id',
                title: i18n.get('consoleTriggerExpression_triggerexpression_id'),
                width: 100
            }, {
                field: 'service_id',
                title: i18n.get('consoleService_service_id'),
                width: 100
            }, {
                field: 'service_index_id',
                title: i18n.get('consoleServiceIndex_serviceindex_id'),
                width: 100
            }, {
                field: 'key',
                title: i18n.get('consoleTriggerExpression_triggerexpression_key'),
                width: 200
            }, {
                field: 'operator_type',
                title: i18n.get('consoleTriggerExpression_triggerexpression_operator_type'),
                width: 200
            }, {
                field: 'func',
                title: i18n.get('consoleTriggerExpression_triggerexpression_func'),
                width: 200
            }, {
                field: 'params',
                title: i18n.get('consoleTriggerExpression_triggerexpression_params'),
                width: 200
            }, {
                field: 'threshold',
                title: i18n.get('consoleTriggerExpression_triggerexpression_threshold'),
                width: 200
            }, {
                field: 'logic_type',
                title: i18n.get('consoleTriggerExpression_triggerexpression_logic_type'),
                width: 200
            }, {
                field: 'opt',
                title: i18n.get('consoleTriggerExpression_triggerexpression_operation'),
                width: 80,
                formatter: function(val, row, index) {
                    return '<i class="fa fa-cog col-opt triggerexpression-opt"></i>';
                }
            }],
            onInited: function() {
                $('#triggerexpressionlist').on('click', '.triggerexpression-opt', function(event) {
                    event.preventDefault();
                    var $this = $(this);
                    $('#triggerexpressionlist').table('clearSelections');
                    if (!$('#xmenu').length) {
                        $this.parents('.xtable-row').click();
                        $.menu({
                            showIcon: false,
                            rows: _.sortBy(triggerExpressionMenus, 'order'),
                            data: $('#triggerexpressionlist').table('getSelected'),
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