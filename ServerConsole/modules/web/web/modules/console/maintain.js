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
        AppEvent.trigger('console.maintainlist/:rand.beforeLoad');
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
        
    }
});