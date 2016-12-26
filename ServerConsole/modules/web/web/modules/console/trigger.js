/* trigger.js 触发器模块 */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleTrigger_triggerManagement'),
        cls: 'fa-exclamation-triangle',
        name: 'trigger',
        order: 6,
        callback: function() {
            AppRouter.navigate('triggerlist/' + Math.random(), {trigger: true});
        }
    });
    AppRouter.route("triggerlist/:rand", function() {
        AppEvent.trigger('console.triggerlist.beforeLoad');
        var $nav = $('div[name=triggerlist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
        triggerInit();
        AppEvent.trigger('console.triggerlist.afterLoad');
    });
    // 触发器操作菜单
    var triggerMenus = [{
        name: 'add-trigger',
        text: i18n.get('consoleTrigger_add_trigger'),
        order: 1,
        onClick: function(trigger) {
            // 添加触发器
        }
    }, {
        name: 'edit-trigger',
        text: i18n.get('consoleTrigger_edit_trigger'),
        order: 2,
        onClick: function(trigger) {
            // 编辑触发器
        }
    }, {
        name: 'delete-trigger',
        text: i18n.get('consoleTrigger_delete_trigger'),
        order: 3,
        onClick: function(trigger) {
            // 删除触发器
        }
    }];
    
    function triggerInit() {
        var tpl = Handlebars.compile(require('./trigger/triggerlist.tpl'));
        init.insertCenter(tpl);
        $('.body-center').layout();
        $('input').placeholder();
        $('#triggerlist').table({
            url: '/trigger/api/listTrigger?order=' + config.order,
            pagination: true,
            fitColumns: true,
            showBorder: false,
            toolbar: '.data-toolbar',
            columns: [{
                field: 'id',
                sortable: true, /* 是否允许排序 */
                checkbox: true
            }, {
                field: 'trigger_id',
                title: i18n.get('consoleTrigger_trigger_id'),
                width: 100
            }, {
                field: 'name',
                title: i18n.get('consoleTrigger_trigger_name'),
                width: 200
            }, {
                field: 'severity',
                title: i18n.get('consoleTrigger_trigger_severity'),
                width: 200
            }, {
                field: 'enabled',
                title: i18n.get('consoleTrigger_trigger_enabled'),
                width: 200
            }, {
                field: 'memo',
                title: i18n.get('consoleTrigger_trigger_memo'),
                width: 350
            }, {
                field: 'opt',
                title: i18n.get('consoleTrigger_trigger_operation'),
                width: 80,
                formatter: function(val, row, index) {
                    return '<i class="fa fa-cog col-opt trigger-opt"></i>';
                }
            }],
            onInited: function() {
                $('#triggerlist').on('click', '.trigger-opt', function(event) {
                    event.preventDefault();
                    var $this = $(this);
                    $('#triggerlist').table('clearSelections');
                    if (!$('#xmenu').length) {
                        $this.parents('.xtable-row').click();
                        $.menu({
                            showIcon: false,
                            rows: _.sortBy(triggerMenus, 'order'),
                            data: $('#triggerlist').table('getSelected'),
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