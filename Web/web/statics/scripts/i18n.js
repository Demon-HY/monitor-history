var Lang = (function() {
    var getCookie = function(str) {
		str = str ? str : document.cookie;
        var arr = str.split('; ');
        var cookie = {};
		for (var i = 0; i < arr.length; i++) {
			var tmp = arr[i].split('=');
            cookie[tmp[0]] = tmp[1];
			if(i == arr.length - 1) {
				return cookie;
			}
		}
        return cookie;
    };
    
    var setCookie = function(key, value, expire) {
        var date = new Date();
        date.setTime(date.getTime() + expire * 3600 * 1000);
        var str = key + '=' + value + '; expires=' + date.toGMTString() + ';';
        document.cookie = str;
    };

    var getRequest = function() {
        var url = window.location.search;
        var request = {};
        if (url.indexOf('?') != -1) {
            var str = url.substr(1);
            var strs = str.split('&');
            for (var i = 0; i < strs.length; i++) {
                request[strs[i].split('=')[0]] = (strs[i].split("=")[1]);
            }
        }
        return request;
    };

    var language = '',
        locale = {};
    if (navigator.language) {
        language = navigator.language;
    } else {
        language = navigator.browserLanguage;
    }

    var cookie = getCookie();
	
    if (cookie.hasOwnProperty('lang')) {
        language = cookie.lang;
    }
	
    var request = getRequest();
    if (request.hasOwnProperty('lang')) {
        language = request.lang;
    }
    if (language.toLowerCase() == 'zh-cn') language = 'zh-CN';
    if (language.toLowerCase() == 'zh-tw' || language.toLowerCase() == 'zh-hk') language = 'zh-TW';
    if (language.substr(0, 2) != 'zh') {
        language = 'en';
    }
    window.language = language;
    var Lang = function(){};
	var loadPackUrl = '';
    Lang.prototype.init = function(obj) {
        var self = this;
    };

    Lang.prototype.loadPack = function(url) {
		loadPackUrl = url;
		$.ajax({
			type: 'GET',
            url: url + language + '.json',
			async: false,
			success: function(data) {
                locale = data;
            }
		});
    };
    
    Lang.prototype.get = function(key) {
        if (locale.hasOwnProperty(key)) {
            return locale[key];
        } else {
            if(window.addEventListener){ 
               console.warn('The key "'+ key +'" is undefined '+ loadPackUrl + language + '.json');
            }
			return key;
        }
    };

    Lang.prototype.set = function(key, value) {
        locale[key] = value;
    };

    Lang.prototype.getLang = function() {
        return language;
    };

    Lang.prototype.setLang = function(lang) {
        setCookie('lang', lang, 24 * 365);
    };
    
    return Lang;
})();
