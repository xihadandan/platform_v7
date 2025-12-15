define("AppNoticeDocumentViewDevelopment", ["jquery", "server", "commons", "constant", "appContext", "appModal",
    "DmsDyformDocumentView"], function ($, server, commons, constant, appContext, appModal, DmsDyformDocumentView) {
    var StringBuilder = commons.StringBuilder;
    // 平台应用_通知公告_数据管理表单二开
    var AppNoticeDocumentViewDevelopment = function () {
        DmsDyformDocumentView.apply(this, arguments);
    };
    var colors = {
        "003017001": "red!important",
        "003017002": "green!important",
        "003017003": "blue!important",
        "003017004": "yellow!important"
    }
    var getNoticeDataActionId = "bnt_notice_dyform_get_data";
    commons.inherit(AppNoticeDocumentViewDevelopment, DmsDyformDocumentView, {
        // 获取加载数据的操作ID
        getLoadDataActionId: function () {
            return getNoticeDataActionId;
        },
        // 准备初始化表单
        prepareInitDyform: function (dyformOptions) {
            var _self = this;
            var documentData = _self.getDocumentData();
            // 表单是否显示为文本
            if (documentData.extras) {
                dyformOptions.displayAsLabel = documentData.extras.displayAsLabel;
            }
            // 外部模块引用表单二开扩展
            dyformOptions.dyformDevelopment = {
                onInit: function (options) {
                },
                beforeParseForm: function () {
                    // var dyform = this.getDyform();
                    // console.log(this);
                    // console.log(dyform);
                    // console.log("beforeParseForm");
                }
            }
        },
        // 表单初始化成功处理
        onInitDyformSuccess: function () {
            var _self = this;
            var dyform = _self.getDyform();
            // 是否显示为文本
            if (dyform.isDisplayAsLabel()) {
                // 通过表单初始化数据获取
                var formData = dyform.getOption().formData;
                var mainFormData = formData.formDatas[formData.formUuid][0];
                var colorCode = mainFormData["title_color_label"];
                if (colors[colorCode]) {
                    var colorStyle = "color :" + colors[colorCode];
                    $("span[name='notice_title']", _self.getDyformSelector()).attr("style", colorStyle);
                }

                // 移出公告内容的鼠标标题提示
                $("span[name=notice_content]").removeAttr("title");
            }
        }
    });
    return AppNoticeDocumentViewDevelopment;
});