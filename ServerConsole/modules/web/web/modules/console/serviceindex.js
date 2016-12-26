/* serviceindex.js 服务指标模块 */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleServiceIndex_serviceIndexManagement'),
        cls: 'fa-user-md',
        name: 'serviceindex',
        order: 4,
        callback: function() {
            AppRouter.navigate('serviceindexlist/' + Math.random(), {trigger: true});
        }
    });
    AppRouter.route("serviceindexlist/:rand", function() {
        AppEvent.trigger('console.serviceindexlist/:rand.beforeLoad');
        var $nav = $('div[name=serviceindexlist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
        serviceIndexInit();
        AppEvent.trigger('console.serviceindexlist.afterLoad');
    });
    // 服务指标操作菜单
    var serviceIndexMenus = [{
        name: 'add-serviceindex',
        text: i18n.get('consoleServiceIndex_add_service_index'),
        order: 1,
        onClick: function(serviceindex) {
            // 添加服务指标
        }
    }, {
        name: 'edit-serviceindex',
        text: i18n.get('consoleServiceIndex_edit_service_index'),
        order: 2,
        onClick: function(serviceindex) {
            // 编辑服务指标
        }
    }, {
        name: 'delete-serviceindex',
        text: i18n.get('consoleServiceIndex_delete_service_index'),
        order: 3,
        onClick: function(serviceindex) {
            // 删除服务指标
        }
    }];
    
    function serviceIndexInit() {
        
    }
});