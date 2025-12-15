define(["constant", "commons", "server", "FileManagerWidgetDevelopment"], function (constant, commons, server,
                                                                                    FileManagerWidgetDevelopment) {
    var JDS = server.JDS;
    // 页面组件二开基础
    var PersonalDocumentFileManagerWidgetDevelopment = function () {
        FileManagerWidgetDevelopment.apply(this, arguments);
    };
    // 接口方法
    commons.inherit(PersonalDocumentFileManagerWidgetDevelopment, FileManagerWidgetDevelopment, {
        // 将文件管理的归属夹调整为我的文件夹
        prepare: function () {
            var _self = this;
            var widget = _self.getWidget();
            // 列出当前夹下的夹跟文件
            widget.setListFileMode("listFolderAndFiles");
            // 添加允许显示分享按钮
            widget.addAllowedActionInToolbar("share");
            JDS.call({
                service: "personalDocumentService.getMyFolder",
                async: false,
                success: function (result) {
                    var folder = result.data;
                    var configuration = widget.getConfiguration();
                    // 变更归属夹到我的个人文档
                    configuration.belongToFolderUuid = folder.uuid;
                    configuration.belongToFolderName = "全部文档";
                }
            });
        }
    });
    return PersonalDocumentFileManagerWidgetDevelopment;
});