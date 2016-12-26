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
        AppEvent.trigger('console.triggerexpressionlist/:rand.beforeLoad');
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
        
    }
});