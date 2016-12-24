/* service.js */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleService_serviceManagement'),
        cls: 'fa-user-md',
        name: 'service',
        order: 3,
        callback: function() {
            AppRouter.navigate('servicelist/' + Math.random(), {trigger: true});
        }
    });
    AppRouter.route("servicelist/:rand", function() {
        AppEvent.trigger('console.servicelist/:rand.beforeLoad');
        var $nav = $('div[name=servicelist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
       serviceInit();
        AppEvent.trigger('console.servicelist.afterLoad');
    });
    // 服务列表操作菜单
    var serviceMenus = [{
        name: 'add-service',
        text: i18n.get('consoleService_add_service'),
        order: 1,
        onClick: function(service) {
            // 添加群组
        }
    }, {
        name: 'edit-service',
        text: i18n.get('consoleService_edit_service'),
        order: 2,
        onClick: function(service) {
            // 编辑群组
        }
    }, {
        name: 'delete-service',
        text: i18n.get('consoleService_delete_service'),
        order: 3,
        onClick: function(service) {
            // 删除群组
        }
    }];
    
    function serviceInit() {
        
    }
});