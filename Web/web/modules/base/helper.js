define(function(require, exports, module) {
    var Handlebars = require('../../statics/scripts/handlebars.min');
    var util = require('./util');
    /* 日期格式转换 */
    Handlebars.registerHelper('formatDate', function(value) {
        return util.formatDate(value);
    });
    Handlebars.registerHelper('formatTime', function(value) {
        return util.formatDate(value, true);
    });
    /* 0,1,2,3... -> 1,2,3,4.. */
    Handlebars.registerHelper('formatIndex', function(value) {
        return value + 1;
    });
    /* 判断数组长度是否为0 */
    Handlebars.registerHelper('arrayLength', function(value, options) {
        if (value.length > 0) {
            return options.fn(this);
        }
    });
    Handlebars.registerHelper('t', function(key) {
        return i18n.get(key);
    });
    /* 比较，如：{{#compare value '==' 'abc'}}aaaa{{/compare}} */
    Handlebars.registerHelper('compare', function(left, operator, right, options) {
        if (arguments.length < 3) {
            throw new Error('Handlerbars Helper "compare" needs 2 parameters');
        }
        var operators = {
            '==': function(l, r) {
                return l == r;
            },
            '===': function(l, r) {
                return l === r;
            },
            '!=': function(l, r) {
                return l != r;
            },
            '!==': function(l, r) {
                return l !== r;
            },
            '<': function(l, r) {
                return l < r;
            },
            '>': function(l, r) {
                return l > r;
            },
            '<=': function(l, r) {
                return l <= r;
            },
            '>=': function(l, r) {
                return l >= r;
            },
            'typeof': function(l, r) {
                return typeof l == r;
            }
        };
        if (!operators[operator]) {
            throw new Error('Handlerbars Helper "compare" doesn\'t know the operator ' + operator);
        }
        var result = operators[operator](left, right);
        if (result) {
            return options.fn(this);
        } else {
            return options.inverse(this);
        }
    });
});