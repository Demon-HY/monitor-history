/* host.js 主机管理模块 */
define(function(require, exports, module) {
    var util = require('util');
    var init = require('./init');
    var config = require('config');
    var Handlebars = require('Handlebars');
    init.addNav({
        text: i18n.get('consoleHost_hostManagement'),
        cls: 'fa-server',
        name: 'host',
        order: 1,
        callback: function() {
            AppRouter.navigate('hostlist/' + Math.random(), {trigger: true});
        }
    });
    AppRouter.route("hostlist/:rand", function() {
        AppEvent.trigger('console.hostlist.beforeLoad');
        var $nav = $('div[name=hostlist]');
        $nav.addClass('nav-current');
        if ($nav.parents('.nav-child').is(':hidden')) {
            $nav.parents('.nav-child').prev('.nav-item').click();
        }
        hostInit();
        AppEvent.trigger('console.hostlist.afterLoad');
    });
    // 主机列表操作菜单
    var hostMenus = [{
        name: 'add-host',
        text: i18n.get('consoleHost_add_host'),
        order: 1,
        onClick: function(host) {
            addHost();
        }
    }, {
        name: 'edit-host',
        text: i18n.get('consoleHost_edit_host'),
        order: 2,
        onClick: function(host) {
            // 编辑主机
            editHost(host);
        }
    }, {
        name: 'delete-host',
        text: i18n.get('consoleHost_delete_host'),
        order: 3,
        onClick: function(host) {
            // 删除主机
        }
    }];

    function hostInit() {
        var tpl = Handlebars.compile(require('./host/hostlist.tpl'));
        init.insertCenter(tpl);
        $('.body-center').layout();
        $('input').placeholder();
        $('#hostlist').table({
            url: '/host/api/listHost?order=' + config.order,
            pagination: true,
            fitColumns: true,
            showBorder: false,
            toolbar: '.data-toolbar',
            columns: [{
                field: 'id',
                sortable: true, /* 是否允许排序 */
                checkbox: true
            }, {
                field: 'host_id',
                title: i18n.get('consoleHost_host_id'),
                width: 100
            }, {
                field: 'name',
                title: i18n.get('consoleHost_host_name'),
                width: 200
            }, {
                field: 'monitored',
                title: i18n.get('consoleHost_monitored'),
                width: 200
            }, {
                field: 'interval',
                title: i18n.get('consoleHost_host_interval'),
                width: 200
            }, {
                field: 'ip',
                title: i18n.get('consoleHost_hostIP'),
                width: 200
            }, {
                field: 'status',
                title: i18n.get('consoleHost_host_status'),
                width: 200
            }, {
                field: 'memo',
                title: i18n.get('consoleHost_host_memo'),
                width: 350
            },  {
                field: 'ctime',
                title: i18n.get('consoleHost_host_ctime'),
                width: 200
            }, {
                field: 'mtime',
                title: i18n.get('consoleHost_host_mtime'),
                width: 200
            }, {
                field: 'opt',
                title: i18n.get('consoleHost_host_operation'),
                width: 80,
                formatter: function(val, row, index) {
                    return '<i class="fa fa-cog col-opt host-opt"></i>';
                }
            }],
            onInited: function() {
                $('#hostlist').on('click', '.host-opt', function(event) {
                    event.preventDefault();
                    var $this = $(this);
                    $('#hostlist').table('clearSelections');
                    if (!$('#xmenu').length) {
                        $this.parents('.xtable-row').click();
                        $.menu({
                            showIcon: false,
                            rows: _.sortBy(hostMenus, 'order'),
                            data: $('#hostlist').table('getSelected'),
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
        //add host
        $(".add-host").click(function() {
            addHost();
        });
        // edit host
        $('.edit-host').click(function(){
            editHost();
        });
    }

    // 添加主机
    function addHost () {
        if (!$('#create-dialog').length) {
            $('body').append('<div id="create-dialog"></div>');
        }
        $('#create-dialog').dialog({
            title: i18n.get('consoleHost_add_host'),
            width: 600,
            height: 650,
            data: Handlebars.compile(require('./host/create.tpl')),
            onInited: function() {
                // 添加群组
                $.request({
                    url: '/group/api/listGroup',
                    done: function(data) {
                        if (data.stat == 'OK') {
                            if (data.rows.length > 0) {
                                // 获取群组的 checkbox 组件，这样在下面插入的时候可以不用每次都去查询
                                $checkGroup = $('.checkbox-group');
                                for (let index in data.rows) {
                                    let mapGroupInfo = data.rows[index];
                                    var checkboxGroupHtml = "<div class='fl'><label><input name='group' type='checkbox' value='" + mapGroupInfo.get('name') + "' id=" 
                                        + mapGroupInfo.get('group_id') + "/></label></div>";
                                    $checkGroup.append(checkboxGroupHtml);
                                }
                            }
                        } else {
                            $.alert(data.errText);
                        }
                    }
                });
                // 添加模板
                $.request({
                    url: '/template/api/listTemplate',
                    done: function(data) {
                        if (data.stat == 'OK') {
                            if (data.rows.length > 0) {
                                // 获取模板的 checkbox 组件，这样在下面插入的时候可以不用每次都去查询
                                $checkTemp = $('.checkbox-template');
                                for (let index in data.rows) {
                                    let mapTempInfo = data.rows[index];
                                    var checkboxTempHtml = "<div class='fl'><label><input name='template' type='checkbox' value='" + mapTempInfo.get('name') + "' id=" 
                                        + mapTempInfo.get('template_id') + "/></label></div>";
                                    $checkTemp.append(checkboxTempHtml);
                                }
                            }
                        } else {
                            $.alert(data.errText);
                        }
                    }
                });
            },
            buttons: [{
                text: i18n.get('consoleHost_confirm'),
                name: 'save',
                cls: 'btn-primary',
                handler: function() {
                    // 表单验证
                    let res = $('#fm').valid('formValid');
                    
                    let listGroup = [];
                    $('#fm input[name=group]:checked').each(function(){
                        listGroup.push($(this).attr('id'));    
                    });
                    let listTemp = [];
                    $('#fm input[name=template]:checked').each(function(){
                        listTemp.push($(this).attr('id'));    
                    });
                    let data = {
                        'name': $('#fm input[name=name]').val(),
                        'ip': $('#fm input[name=ip]').val(),
                        'listgroup': listGroup,
                        'listTemplate': listTemp,
                        'monitored': $('#fm select[name=monitored]').val(),
                        'interval': $('#fm input[name=interval]').val(),
                        'status': $('#fm select[name=status]').val(),
                        'memo': $('#fm textarea[name=memo]').val()
                    }
                    if (res) {
                        $.request({
                            url: '/host/api/addHost',
                            params: data,
                            done: function(data) {
                                if (data.stat == 'OK') {
                                    $('#create-dialog').dialog('close');
                                    $('#hostlist').table('reload');
                                    // $.tip('主机添加成功！');
                                    $.tip(i18n.get('consoleHost_success_addHost'));
                                } else {
                                    $.alert(data.errText);
                                }
                            }
                        });
                    }
                }
            }, {
                text: i18n.get('consoleHost_cancel'),
                name: 'cancel',
                handler: function() {
                    $('#create-dialog').dialog('close');
                }
            }]
        });
    }

    function editHost(host) {
        if (!$('#edit-dialog').length) {
            $('body').append('<div id="edit-dialog"></div>');
        }
        $('#edit-dialog').dialog({
            title: i18n.get('consoleHost_edit_host'),
            width: 600,
            height: 650,
            data: Handlebars.compile(require('./host/edit.tpl')),
            onInited: function() {
                // 添加群组
                var checkboxGroupHtml = "<div class='fl'><label><input name='group' type='checkbox' value='' />群组1</label></div>";
                $('.checkbox-group').append(checkboxGroupHtml)
                .append(checkboxGroupHtml);

                // 添加模板
                var checkboxTempHtml = "<div class='fl'><label><input name='template' type='checkbox' value='' />模板1</label></div>";
                $('.checkbox-template').append(checkboxTempHtml)
                .append(checkboxTempHtml)
                .append(checkboxTempHtml);
            },
            buttons: [{
                text: i18n.get('consoleHost_confirm'),
                name: 'save',
                cls: 'btn-primary',
                handler: function() {
                    var res = $('#fm').valid('formValid');
                    var form = util.parseForm('#fm');
                    if (res) {
                        /*$.request({
                            url: '/host/api/addHost',
                            params: form,
                            done: function(data) {
                                if (data.stat == 'OK') {
                                    $('#create-dialog').dialog('close');
                                    $('#hostlist').table('reload');
                                    // $.tip('主机添加成功！');
                                    $.tip(i18n.get('consoleHost_success_addHost'));
                                } else {
                                    $.alert(data.errText);
                                }
                            }
                        });*/
                    }
                }
            }, {
                text: i18n.get('consoleHost_cancel'),
                name: 'cancel',
                handler: function() {
                    $('#edit-dialog').dialog('close');
                }
            }]
        });
    }
});