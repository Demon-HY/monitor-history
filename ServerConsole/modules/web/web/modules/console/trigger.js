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
        AppEvent.trigger('console.triggerlist/:rand.beforeLoad');
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
        
    }
});