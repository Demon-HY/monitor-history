/* action.js */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleAction_actionManagement'),
        cls: 'fa-bell',
        name: 'action',
        order: 6
    });
});