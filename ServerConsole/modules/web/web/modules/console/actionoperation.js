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
        AppEvent.trigger('console.actionoperationlist/:rand.beforeLoad');
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
        
    }
});