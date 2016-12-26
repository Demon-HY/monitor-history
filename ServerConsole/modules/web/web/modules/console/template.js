/* template.js 模板模块 */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleTemplate_templateManagement'),
        cls: 'fa-edit',
        name: 'template',
        order: 5,
        callback: function() {
            AppRouter.navigate('templatelist/' + Math.random(), {trigger: true});
        }
    });
    AppRouter.route("templatelist/:rand", function() {
        AppEvent.trigger('console.templatelist.beforeLoad');
        var $nav = $('div[name=templatelist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
        templateInit();
        AppEvent.trigger('console.templatelist.afterLoad');
    });
    // 模板操作菜单
    var templateMenus = [{
        name: 'add-template',
        text: i18n.get('consoleTemplate_add_template'),
        order: 1,
        onClick: function(template) {
            // 添加模板
        }
    }, {
        name: 'edit-template',
        text: i18n.get('consoleTemplate_edit_template'),
        order: 2,
        onClick: function(template) {
            // 编辑模板
        }
    }, {
        name: 'delete-template',
        text: i18n.get('consoleTemplate_delete_template'),
        order: 3,
        onClick: function(template) {
            // 删除模板
        }
    }];
    
    function templateInit() {
        var tpl = Handlebars.compile(require('./template/templatelist.tpl'));
        init.insertCenter(tpl);
        $('.body-center').layout();
        $('input').placeholder();
        $('#templatelist').table({
            url: '/template/api/listTemplate?order=' + config.order,
            pagination: true,
            fitColumns: true,
            showBorder: false,
            toolbar: '.data-toolbar',
            columns: [{
                field: 'id',
                sortable: true,
                checkbox: true
            }, {
                field: 'template_id',
                title: i18n.get('consoleServiceIndex_serviceindex_id'),
                width: 100
            }, {
                field: 'name',
                title: i18n.get('consoleTemplate_template_name'),
                width: 200
            }, {
                field: 'ctime',
                title: i18n.get('consoleTemplate_template_ctime'),
                width: 200
            }, {
                field: 'mtime',
                title: i18n.get('consoleTemplate_template_mtime'),
                width: 200
            }, {
                field: 'opt',
                title: i18n.get('consoleTemplate_template_operation'),
                width: 80,
                formatter: function(val, row, index) {
                    return '<i class="fa fa-cog col-opt template-opt"></i>';
                }
            }],
            onInited: function() {
                $('#templatelist').on('click', '.template-opt', function(event) {
                    event.preventDefault();
                    var $this = $(this);
                    $('#templatelist').table('clearSelections');
                    if (!$('#xmenu').length) {
                        $this.parents('.xtable-row').click();
                        $.menu({
                            showIcon: false,
                            rows: _.sortBy(templateMenus, 'order'),
                            data: $('#templatelist').table('getSelected'),
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