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
});