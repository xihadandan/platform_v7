define(["mui", "commons", "constant", "server", "formBuilder", "MobileListDevelopmentBase"], function
    ($, commons, constant, server, formBuilder, MobileListDevelopmentBase) {
    var StringUtils = commons.StringUtils;
    var StringBuilder = commons.StringBuilder;

    var MobliePersonalDocumentRecycleBinFileManagerWidgetDevelopment = function () {
        MobileListDevelopmentBase.apply(this, arguments);
    };
    //回收站
    commons.inherit(MobliePersonalDocumentRecycleBinFileManagerWidgetDevelopment, MobileListDevelopmentBase, {
        beforeRender: function () {
            var widget = this.getWidget();
            var configuration = widget.getConfiguration();
            widget.setListFileMode("listFolderAndFiles");
            server.JDS.call({
                service: "personalDocumentService.getMyFolder",
                async: false,
                success: function (result) {
                    var folder = result.data;
                    configuration.belongToFolderUuid = folder.uuid;
                }
            });
        }
    });
    return MobliePersonalDocumentRecycleBinFileManagerWidgetDevelopment;
});