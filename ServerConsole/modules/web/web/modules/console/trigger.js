/* trigger.js */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleTrigger_triggerManagement'),
        cls: 'fa-exclamation-triangle',
        name: 'trigger',
        order: 5
    });
});