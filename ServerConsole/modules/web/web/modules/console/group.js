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
        order: 2,
        callback: function() {
            AppRouter.navigate('grouplist/' + Math.random(), {trigger: true});
        }
    });
    AppRouter.route("grouplist/:rand", function() {
        AppEvent.trigger('console.grouplist.beforeLoad');
        var $nav = $('div[name=grouplist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
        groupInit();
        AppEvent.trigger('console.grouplist.afterLoad');
    });
    // 群组列表操作菜单
    var groupMenus = [{
        name: 'add-group',
        text: i18n.get('consoleGroup_add_group'),
        order: 1,
        onClick: function(group) {
            // 添加群组
        }
    }, {
        name: 'edit-group',
        text: i18n.get('consoleGroup_edit_group'),
        order: 2,
        onClick: function(group) {
            // 编辑群组
        }
    }, {
        name: 'delete-group',
        text: i18n.get('consoleGroup_delete_group'),
        order: 3,
        onClick: function(group) {
            // 删除群组
        }
    }];

    function groupInit() {
        var tpl = Handlebars.compile(require('./group/grouplist.tpl'));
        init.insertCenter(tpl);
        $('.body-center').layout();
        $('input').placeholder();
        $('#grouplist').table({
            url: '/group/api/listGroup?order=' + config.order,
            pagination: true,
            fitColumns: true,
            showBorder: false,
            toolbar: '.data-toolbar',
            columns: [{
                field: 'id',
                sortable: true,
                checkbox: true
            }, {
                field: 'group_id',
                title: i18n.get('consoleGroup_group_id'),
                width: 100
            }, {
                field: 'name',
                title: i18n.get('consoleGroup_group_name'),
                width: 200
            }, {
                field: 'memo',
                title: i18n.get('consoleGroup_group_memo'),
                width: 350
            }, {
                field: 'ctime',
                title: i18n.get('consoleGroup_group_ctime'),
                width: 200
            }, {
                field: 'mtime',
                title: i18n.get('consoleGroup_group_mtime'),
                width: 200
            }, {
                field: 'opt',
                title: i18n.get('consoleGroup_group_operation'),
                width: 80,
                formatter: function(val, row, index) {
                    return '<i class="fa fa-cog col-opt group-opt"></i>';
                }
            }],
            onInited: function() {
                $('#grouplist').on('click', '.group-opt', function(event) {
                    event.preventDefault();
                    var $this = $(this);
                    $('#grouplist').table('clearSelections');
                    if (!$('#xmenu').length) {
                        $this.parents('.xtable-row').click();
                        $.menu({
                            showIcon: false,
                            rows: _.sortBy(groupMenus, 'order'),
                            data: $('#grouplist').table('getSelected'),
                            position: {
                                left: event.pageX - 85,
                                top: event.pageY + 10
                            }
                        })
                    } else {
                        $(document).trigger('click');
                    }
                    return false;
                });
            }
        });
    }
});