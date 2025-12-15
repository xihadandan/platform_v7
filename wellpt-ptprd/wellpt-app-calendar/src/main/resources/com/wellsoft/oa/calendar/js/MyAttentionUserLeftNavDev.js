define(["constant", "commons", "server", "LeftSidebarWidgetDevelopment", "appModal", "multiOrg"], function (constant, commons, server,
                                                                                                            LeftSidebarWidgetDevelopment, appModal) {
    var JDS = server.JDS;
    var currUser = server.SpringSecurityUtils.getUserDetails();
    // 页面组件二开基础
    var MyAttentionUserLeftNavDev = function () {
        LeftSidebarWidgetDevelopment.apply(this, arguments);
    };
    // 接口方法
    commons.inherit(MyAttentionUserLeftNavDev, LeftSidebarWidgetDevelopment, {
        //取消关注图标事件
        initAttentionEditIconEvent: function () {
            var _self = this;
            var menus = this.getWidget();
            //定义编辑日历本事件
            $(menus.element).find(".sidebar-nav-item-icon").each(function () {
                $(this).click(function () {
                    var $menuItem = $(this).parents(".mainnav-menu-item");
                    var menuId = $menuItem.attr('menuid');
                    var user = _self.widget.menuItemMap[menuId].user;
                    $table = $("<table class='table table-border'>");
                    $userTr = $("<tr><td>关注人</td><td>" + user.userName + "</td></tr>");
                    $deptTr = $("<tr><td>所在部门</td><td>" + user.mainDepartmentName + "</td></tr>");
                    $jobTr = $("<tr><td>职位</td><td>" + user.mainJobName + "</td></tr>");
                    $table.append($userTr, $deptTr, $jobTr);
                    appModal.dialog({
                        title: "详情",
                        message: $table,
                        buttons: {
                            cancelAttentionBtn: {
                                label: "取消关注",
                                className: 'btn btn-primary',
                                callback: function () {
                                    appModal.confirm("确定取消关注?", function (result) {
                                        if (result) {
                                            _self._cancelAttention(user);
                                        }
                                    });
                                }
                            },
                            closeBtn: {
                                label: '关闭',
                                className: 'btn btn-default',
                            }
                        }
                    });
                    return false;
                });

            });
        },

        _cancelAttention: function (user) {
            var _self = this;
            server.JDS.call({
                service: "attentionFacade.cancelAttentionUser",
                data: [user.id],
                success: function (result) {
                    if (result && result.success) {
                        appModal.success('取消成功', function () {
                            _self.refresh();
                        });
                    }
                }
            });
        },

        afterRenderView: function (options) {
            //初始化关注图标的事件
            this.initAttentionEditIconEvent();
        },

        // 组件初始化
        init: function () {
            var _self = this;
            var menus = this.getWidget();
            //默认选中第一个
            var $firstMenu = $(menus.element).find(".mainnav-menu-item").first();
            $firstMenu.trigger("click");
            //初始化下面的添加关注按钮
            _self._initAddAttentionButton();
            //默认选中第一个
            if ($firstMenu.length == 0) {
                //$(menus.element).append("暂无关注,请添加");
            }
        },

        _initAddAttentionButton: function () {
            var _self = this;
            $("button.js-add-attention-user").click(function () {
                $.unit2.open({
                    valueField: "",
                    labelField: "",
                    title: "选择人员",
                    type: "all",
                    multiple: true,
                    selectTypes: "U",
                    valueFormat: "justId",
                    excludeValues: [currUser.userId],
                    callback: function (values, labels) {
                        if (values && values.length > 0) {
                            _self._addAttentionUser(values);
                        }
                    }
                });
            });
        },

        _addAttentionUser: function (users) {
            var _self = this;
            server.JDS.call({
                service: "attentionFacade.addAttentionUsers",
                data: [users],
                success: function (result) {
                    if (result && result.success) {
                        appModal.success('添加关注成功', function () {
                            _self.refresh();
                        });
                    }
                }
            });
        },

    });
    return MyAttentionUserLeftNavDev;
});