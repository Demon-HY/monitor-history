/* group.js */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleGroup_groupManagement'),
        cls: 'fa-users',
        name: 'group',
        order: 2
    });
    AppRouter.route("grouplist/:rand", function() {
        AppEvent.trigger('console.grouplist/:rand.beforeLoad');
        var $nav = $('div[name=grouplist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
        groupInit();
        AppEvent.trigger('console.grouplist.afterLoad');
    });
});