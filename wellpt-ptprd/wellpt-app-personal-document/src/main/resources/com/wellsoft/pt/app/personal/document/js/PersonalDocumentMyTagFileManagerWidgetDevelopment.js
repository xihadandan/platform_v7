define(["constant", "commons", "server", "FileManagerWidgetDevelopment", "DmsTagFragment"], function (constant,
                                                                                                      commons, server, FileManagerWidgetDevelopment, DmsTagFragment) {
    var JDS = server.JDS;
    var StringUtils = commons.StringUtils;
    var StringBuilder = commons.StringBuilder;
    var rootFolderNameTpl = '<span class="root-item-icon glyphicon glyphicon-stop" style="color:{0};"></span>';
    rootFolderNameTpl += '<span class="root-item-label">{1}</span>';
    // 页面组件二开基础
    var PersonalDocumentMyTagFileManagerWidgetDevelopment = function () {
        FileManagerWidgetDevelopment.apply(this, arguments);
    };
    var dmsTagFragment = new DmsTagFragment();
    // 接口方法
    commons.inherit(PersonalDocumentMyTagFileManagerWidgetDevelopment, FileManagerWidgetDevelopment, dmsTagFragment
        .extend({
            // 将文件管理的归属夹调整为最近访问
            prepare: function () {
                var _self = this;
                // 调用父类提交方法
                _self._superApply(arguments);
                var widget = _self.getWidget();
                // 变更归属夹到我的标签
                var menuitem = $(".subnav-menu-item.nav-menu-active").data("menuitem");
                if (menuitem && menuitem.tagName) {
                    var rootFolderName = new StringBuilder();
                    rootFolderName.appendFormat(rootFolderNameTpl, menuitem.tagColor, menuitem.tagName);
                    widget.getRootFolder().name = rootFolderName.toString();
                } else {
                    widget.getRootFolder().name = "我的标签";
                }
                // 显示导航夹操作按钮
                widget.setShowNavFolderActions(true);
                // 显示工具栏操作按钮
                widget.setShowToolbarActions(true);
                // 使用自定义的文件库数据源
                widget.setUseCustomFileManagerDataStore(true);
                JDS.call({
                    service: "personalDocumentService.getMyFolder",
                    success: function (result) {
                        var folder = result.data;
                        // 设置弹出框使用的根目录
                        widget.setRootFolderForDialog({
                            uuid: folder.uuid,
                            name: "全部文档"
                        });
                    }
                });
            },
            // 内容展示完回调
            onPostBody: function () {
                // 调用DmsTagFragment的markBodyTags进行打标签
                this.markBodyTags();
            },
            // 添加标记为按钮
            beforeUpdateToolbar: function (fileActions) {
                // 调用DmsTagFragment的appendTagToolbar生成标记为按钮
                this.appendTagToolbar(fileActions);
            },
            // 加载数据前回调
            beforeLoadData: function () {
                var _self = this;
                widget = _self.getWidget();
                var menuitem = $(".subnav-menu-item.nav-menu-active").data("menuitem");
                // 我的标签文件列表加载
                if (menuitem && menuitem.navType == 3) {
                    widget.removeParam("tagUuid");
                    widget.addParam("tagUuid", menuitem.uuid);
                }
            }
        }));
    return PersonalDocumentMyTagFileManagerWidgetDevelopment;
});