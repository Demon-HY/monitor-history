/* template.js 模板模块 */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleTemplate_templateManagement'),
        cls: 'fa-edit',
        name: 'template',
        order: 5,
        callback: function() {
            AppRouter.navigate('templatelist/' + Math.random(), {trigger: true});
        }
    });
    AppRouter.route("templatelist/:rand", function() {
        AppEvent.trigger('console.templatelist/:rand.beforeLoad');
        var $nav = $('div[name=templatelist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
        templateInit();
        AppEvent.trigger('console.templatelist.afterLoad');
    });
    // 模板操作菜单
    var serviceIndexMenus = [{
        name: 'add-template',
        text: i18n.get('consoleTemplat_add_template'),
        order: 1,
        onClick: function(template) {
            // 添加模板
        }
    }, {
        name: 'edit-template',
        text: i18n.get('consoleTemplat_edit_template'),
        order: 2,
        onClick: function(template) {
            // 编辑模板
        }
    }, {
        name: 'delete-template',
        text: i18n.get('consoleTemplat_delete_template'),
        order: 3,
        onClick: function(template) {
            // 删除模板
        }
    }];
    
    function templateInit() {
        
    }
});