define(["constant", "commons", "server", "FileManagerWidgetDevelopment"], function (constant, commons, server,
                                                                                    FileManagerWidgetDevelopment) {
    var JDS = server.JDS;
    // 页面组件二开基础
    var PersonalDocumentShareWithMeFileManagerWidgetDevelopment = function () {
        FileManagerWidgetDevelopment.apply(this, arguments);
    };
    // 接口方法
    commons.inherit(PersonalDocumentShareWithMeFileManagerWidgetDevelopment, FileManagerWidgetDevelopment, {
        // 将文件管理的归属夹调整为与我分享
        prepare: function () {
            var _self = this;
            var widget = _self.getWidget();
            var configuration = widget.getConfiguration();
            // 变更归属夹到与我分享
            configuration.belongToFolderUuid = "";
            configuration.belongToFolderName = "与我分享";
            // 不显示导航夹操作按钮
            widget.setShowNavFolderActions(false);
            // 使用自定义的文件库数据源
            widget.setUseCustomFileManagerDataStore(true);
        }
    });
    return PersonalDocumentShareWithMeFileManagerWidgetDevelopment;
});