define(function(require, exports, module) {
    module.exports = {
        parseForm: function(id) {
            var $obj = $(id);
            var obj = {};
            var arr = $obj.serializeArray();
            $.each(arr, function(i, v) {
                obj[v.name] = v.value;
                if (typeof(v.value) == 'string') {
                    obj[v.name] = $.trim(v.value);
                }
            });
            return obj;
        },
        transdate: function(time) {
            var date = new Date();
            date.setFullYear(time.substring(0, 4));
            date.setMonth(time.substring(5, 7) - 1);
            date.setDate(time.substring(8, 10));
            date.setHours(time.substring(11, 13));
            date.setMinutes(time.substring(14, 16));
            date.setSeconds(time.substring(17, 19));
            return Date.parse(date) / 1000;
        },
        getCookie: function(str) {
            var str = str ? str : document.cookie;
            var arr = str.split('; ');
            var cookie = {};
            $.each(arr, function(i, v) {
                var tmp = v.split('=');
                cookie[tmp[0]] = tmp[1];
            });
            return cookie;
        },
        setCookie: function(key, value, expire) {
            var date = new Date();
            date.setTime(date.getTime() + expire * 3600 * 1000);
            var str = key + '=' + value + '; expires=' + date.toGMTString() + ';';
            document.cookie = str;
        },
        openWindow: function(url, isLocal) {
            var eventUrl = url;
            if (isLocal === true) {
                var origin = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '');
                eventUrl = origin + '/web/' + url;
            }
            if (!AppEvent.triggerHandler('openWindow', [eventUrl])) {
                window.open(url);
            }
        },
        // 格式化日期显示
        formatDate: function(date, minute) {
            if (!date) return '';
            var d = new Date();
            if ((date + '').length == 10) d.setTime(date * 1000);
            else d.setTime(date);
            var year = d.getFullYear();
            var month = d.getMonth() + 1 < 10 ? '0' + (d.getMonth() + 1) : d.getMonth() + 1;
            var day = d.getDate() < 10 ? '0' + d.getDate() : d.getDate();
            var hour = d.getHours() < 10 ? '0' + d.getHours() : d.getHours();
            var min = d.getMinutes() < 10 ? '0' + d.getMinutes() : d.getMinutes();
            if (minute) return year + '-' + month + '-' + day + ' ' + hour + ':' + min;
            return year + '-' + month + '-' + day;
        },
        // 获取url参数，返回key / value
        getRequest: function() {
            var url = window.location.search;
            var request = new Object();
            if (url.indexOf("?") != -1) {
                var str = url.substr(1);
                strs = str.split("&");
                for (var i = 0; i < strs.length; i++) {
                    request[strs[i].split("=")[0]] = (strs[i].split("=")[1]);
                }
            }
            return request;
        },
        checkFlash: function() {
            var hasFlash = false;
            var version = 0;
            var browser = $.browser().browser;
            if (browser.name == 'Internet Explorer') {
                try {
                    var swf = new ActiveXObject('ShockwaveFlash.ShockwaveFlash');
                    if (swf) {
                        hasFlash = true;
                        version = swf.GetVariable("$version");
                        version = parseInt(version.split(" ")[1].split(",")[0]);
                    }
                } catch (e) {
                    hasFlash = false;
                    version = 0;
                }
            } else {
                if (navigator.plugins && navigator.plugins.length > 0) {
                    var swf = navigator.plugins["Shockwave Flash"];
                    if (swf) {
                        hasFlash = true;
                        var words = swf.description.split(" ");
                        for (var i = 0; i < words.length; ++i) {
                            if (isNaN(parseInt(words[i]))) continue;
                            version = parseInt(words[i]);
                        }
                    }
                }
            }
            return {
                hasFlash: hasFlash,
                version: version
            };
        },
        formData: function(id) {
            id = id ? id : '#fm';
            var data = this.parseForm(id);
            $.each(data, function(i, v) {
                if ($("input[name='" + i + "']").is(':hidden')) delete data[i];
            });
            return data;
        },
        /*
            生成密码强度规则
            util.createPwdRules(['number','uppercase','lowercase','char'])
            --->
            {   
                pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{6,16}$/,
                tip: '密码必须包含大写字母、数字和字符'
            }
        */
        createPwdRules: function(arr,limit) {
            var valids = {};
            limit=limit||false;
            var obj = {};
            arr.forEach(function(v, i) {
                obj[v] = true;
            });
            var defaultTip=i18n.get('length')+"6-16"+",";
            valids.tip=defaultTip;
            if (arr.length == 1) {
                switch (arr[0]) {
                    case '': 
                        valids.pattern = /^[^\u4e00-\u9fa5]{6,16}$/;
                        valids.tip = i18n.get('password_length_of_6_to_16');
                        break;
                    case 'number':
                        valids.pattern = /^(?=.*\d)[^\u4e00-\u9fa5]{6,16}$/;
                        valids.tip += i18n.get('passwords_must_contain') + i18n.get('numbers');
                        break;
                    case 'lowercase':
                        valids.pattern = /^(?=.*[a-z])[^\u4e00-\u9fa5]{6,16}$/;
                        valids.tip += i18n.get('passwords_must_contain') + i18n.get('lowercase_letters');
                        break;
                    case 'uppercase':
                        valids.pattern = /^(?=.*[A-Z])[^\u4e00-\u9fa5]{6,16}$/;
                        valids.tip += i18n.get('passwords_must_contain') + i18n.get('capital_letters');
                        break;
                    case 'char':
                        valids.pattern = /^(?=.*[^A-Za-z0-9])[^\u4e00-\u9fa5]{6,16}$/;
                        valids.tip += i18n.get('passwords_must_contain') + i18n.get('special_characters');
                        break;
                }
            } else if (arr.length == 2) {
                if (obj.number) {
                    if (obj.lowercase) {
                        valids.pattern = /^(?=.*\d)(?=.*[a-z])[^\u4e00-\u9fa5]{6,16}$/;
                        valids.tip += i18n.get('passwords_must_contain') + i18n.get('numbers') + ',' + i18n.get('lowercase_letters');
                    } else if (obj.uppercase) {
                        valids.pattern = /^(?=.*\d)(?=.*[A-Z])[^\u4e00-\u9fa5]{6,16}$/;
                        valids.tip += i18n.get('passwords_must_contain') + i18n.get('numbers') + ',' + i18n.get('capital_letters');
                    } else if (obj.char) {
                        valids.pattern = /^(?=.*\d)(?=.*[^A-Za-z0-9])[^\u4e00-\u9fa5]{6,16}$/;
                        valids.tip += i18n.get('passwords_must_contain') + i18n.get('numbers') + ',' + i18n.get('special_characters');
                    }
                } else if (obj.lowercase) {
                    if (obj.uppercase) {
                        valids.pattern = /^(?=.*[a-z])(?=.*[A-Z])[^\u4e00-\u9fa5]{6,16}$/;
                        valids.tip += i18n.get('passwords_must_contain') + i18n.get('lowercase_letters') + ',' + i18n.get('capital_letters');
                    } else if (obj[char]) {
                        valids.pattern = /^(?=.*[a-z])(?=.*[^A-Za-z0-9])[^\u4e00-\u9fa5]{6,16}$/;
                        valids.tip += i18n.get('passwords_must_contain') + i18n.get('lowercase_letters') + ',' + i18n.get('special_characters');
                    }
                } else if (obj.uppercase && obj.char) {
                    valids.pattern = /^(?=.*[A-Z])(?=.*[^A-Za-z0-9])[^\u4e00-\u9fa5]{6,16}$/;
                    valids.tip += i18n.get('passwords_must_contain') + i18n.get('capital_letters') + ',' + i18n.get('special_characters');
                }
            } else if (arr.length == 3) {
                if (!obj.number) {
                    valids.pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9])[^\u4e00-\u9fa5]{6,16}$/;
                    valids.tip += i18n.get('passwords_must_contain') + i18n.get('lowercase_letters') + ',' + i18n.get('capital_letters') + ',' + i18n.get('special_characters');
                } else if (!obj.lowercase) {
                    valids.pattern = /^(?=.*\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9])[^\u4e00-\u9fa5]{6,16}$/;
                    valids.tip += i18n.get('passwords_must_contain') + i18n.get('numbers') + ',' + i18n.get('capital_letters') + ',' + i18n.get('special_characters');
                } else if (!obj.uppercase) {
                    valids.pattern = /^(?=.*\d)(?=.*[a-z])(?=.*[^A-Za-z0-9])[^\u4e00-\u9fa5]{6,16}$/;
                    valids.tip += i18n.get('passwords_must_contain') + i18n.get('numbers') + ',' + i18n.get('lowercase_letters') + ',' + i18n.get('special_characters');
                } else if (!obj.char) {
                    valids.pattern = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[^\u4e00-\u9fa5]{6,16}$/;
                    valids.tip += i18n.get('passwords_must_contain') + i18n.get('numbers') + ',' + i18n.get('lowercase_letters') + ',' + i18n.get('capital_letters');
                }
            } else {
                valids.pattern = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9])[^\u4e00-\u9fa5]{6,16}$/;
                valids.tip += i18n.get('passwords_must_contain') + i18n.get('numbers') + ',' + i18n.get('lowercase_letters') + ',' + i18n.get('capital_letters') + ',' + i18n.get('special_characters');
            }
            return valids;
        }
    };
});