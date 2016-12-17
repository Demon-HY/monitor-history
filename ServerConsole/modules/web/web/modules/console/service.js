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
        order: 3
    });
});