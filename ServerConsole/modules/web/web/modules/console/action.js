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
        AppEvent.trigger('console.actionlist/:rand.beforeLoad');
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
        
    }
});