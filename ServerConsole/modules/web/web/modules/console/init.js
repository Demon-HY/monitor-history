// 页面初始化模块
define(function(require, exports, module) {
	var config = require('config');
	var Handlebars = require('Handlebars');
	// 侧边栏二级菜单
	var navMenus = [];
	
	function logout() {
        $.request({
            url: '/auth/api/logout',
            always: function() {
                window.location.href = '/web/login.html';
            }
        })
    };
    // 点击头像后，弹出的菜单选项
    var profileMenus = [{
        name: 'monitor',
        text: i18n.get('monitor'),
        order: 1,
        onClick: function() {
            window.location.href = 'monitor.html'; // 跳转到数据中心
        }
    }];
	/* 对外抛出的方法 */
	module.exports = {
	    initialize: function() {
	        AppEvent.trigger('console.init.beforeLoad');
	        // 头部信息
	        var headTpl = Handlebars.compile(require('./init/head.tpl'));
            $('.body-north').html(headTpl({
                "nickName": window.userinfo.nickName,
            }));
            // 左侧菜单栏
            navMenus = _.indexBy(_.sortBy(navMenus, "order"), "name");
            var navData = {};
            for (var i in navMenus) {
                if (!navMenus[i].hasOwnProperty('parent')) {
                    navData[i] = navMenus[i];
                    navData[i].children = [];
                }
            }
            for (var i in navMenus) {
                if (navMenus[i].hasOwnProperty('parent')) {
                    navData[navMenus[i].parent]['children'].push(navMenus[i]);
                }
            }
            navData = _.values(navData);
            var menuTpl = Handlebars.compile(require('./init/nav-menu.tpl'));
            $('.nav-wrap').html(menuTpl({
                rows: navData
            }));
            $('.nav-wrap').on('click', '.nav-item', function(event) {
                var $this = $(this);
                var name = $this.attr('name');
                if ($this.next('.nav-child').length) {
                    $this.next('.nav-child').slideToggle('fast');
                    var $arrow = $this.find('.nav-arrow');
                    if ($arrow.hasClass('fa-caret-right')) {
                        $arrow.removeClass('fa-caret-right').addClass('fa-caret-down');
                    } else {
                        $arrow.removeClass('fa-caret-down').addClass('fa-caret-right');
                    }
                }
                if (navMenus[name].hasOwnProperty('callback')) {
                    navMenus[name]['callback'].call(this);
                }
            });
            // 头像被点击
            $('.nav-photo').bind('click', function(event) {
                var $this = $(this);
                if (!$('#xmenu').length) {
                    $.menu({
                        showIcon: false,
                        rows: _.sortBy(profileMenus, 'order'),
                        position: {
                            left: $this.offset().left - 130,
                            top: 60
                        }
                    });
                    AppEvent.trigger('init.photoClick');
                } else {
                    $(document).trigger('click');
                }
                return false;
            });
            // 点击退出按钮
            $('.user-logout').bind('click', function(event) {
                logout();
            });
            AppEvent.trigger('console.init.afterLoad');
	    },
	    addNav: function(item) {
            if (item.hasOwnProperty('init') && item.init() === false) {
                return false;
            }
            navMenus.push(item);
        },
		insertCenter: function(html) {
		    $(".body-center").html(html);
        },
        deleteProfileMenus: function(name) {
            profileMenus = _.filter(profileMenus, function(item) {
                return item.name != name;
            });
        }
	};
});