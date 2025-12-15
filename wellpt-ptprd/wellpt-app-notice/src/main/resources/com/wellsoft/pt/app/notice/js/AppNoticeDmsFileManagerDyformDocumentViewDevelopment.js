define(["jquery", "server", "commons", "constant", "appContext", "appModal", "DmsFileManagerDyformDocumentView"],
    function ($, server, commons, constant, appContext, appModal, DmsFileManagerDyformDocumentView) {
        var StringBuilder = commons.StringBuilder;
        // 平台应用_通知公告_文件库表单二开
        var AppNoticeDmsFileManagerDyformDocumentViewDevelopment = function () {
            DmsFileManagerDyformDocumentView.apply(this, arguments);
        };
        commons.inherit(AppNoticeDmsFileManagerDyformDocumentViewDevelopment, DmsFileManagerDyformDocumentView, {
            // 表单初始化成功处理
            // 初始化
            prepareInitDyform: function (dyformOptions) {
                var botDyformUuid = Browser.getQueryString("botDyformUuid");
                var circleUuid = Browser.getQueryString("circleUuid");
                var formUuid = Browser.getQueryString("formUuid");
                var circleName = Browser.getQueryString("circleName");
                if (StringUtils.isNotBlank(botDyformUuid) && StringUtils.isNotBlank(circleUuid) && StringUtils.isNotBlank(formUuid)) {
                    JDS.call({
                        service: "oaNoticeRegisterDockingFacadeService.getDyformExchangeData",
                        data: [formUuid, botDyformUuid, circleUuid],
                        async: false,
                        success: function (result) {
                            debugger;
                            var data = result.data;
//	            				if(data && data[0]&&data[0][0]){
//	            					dyform.setFieldValue("notice_title", data[0][0].notice_title);
//	            					dyform.getControl("notice_content").setCkValue(data[0][0].notice_content);
//	            					dyform.setFieldValue("sw_register_uuid", data[0][0].sw_register_uuid);
//	            				}
                            console.log("单据转换结果：");
                            console.log(data);
                            if (data) {
                                dyformOptions.formData.formDatas = data;
                            }
                        }
                    });
                }
            },
            onInitDyformSuccess: function () {
                var _self = this;
                var dyform = _self.getDyform();
                var circleName = Browser.getQueryString("circleName");
                // 是否显示为文本
                if (dyform.isDisplayAsLabel()) {
                    // 通过表单初始化数据获取
                    var formData = dyform.getOption().formData;
                    var mainFormData = formData.formDatas[formData.formUuid][0];
                    var titleColor = mainFormData["title_color_value"];
                    if ($.fn.css && titleColor) {
                        $("span[name='notice_title']", _self.getDyformSelector()).css({
                            color: titleColor
                        });
                    }

                    // 移出公告内容的鼠标标题提示
                    $.fn.removeAttr && $("span[name=notice_content]").removeAttr("title");
                }
                dyform.setFieldValue("circle_name", circleName);
            },
            saveEventHandler: function (event_type, args) {
                var _self = this;
                var dyform = _self.getDyform();
                var circleUuid = Browser.getQueryString("circleUuid");
                var circleName = Browser.getQueryString("circleName");
                var result;
                if (_self.defineDyformEvents != 'undefined' && _self.defineDyformEvents != undefined) {
                    if (_self.defineDyformEvents[event_type]) {
                        _self.getDyformData(function (dyFormData) {
                            appContext.eval(_self.defineDyformEvents[event_type], this, $.extend({
                                dyformData: dyFormData
                            }, args), function (v) {
                                result = v;
                            });
                        });
                    }
                }
                if (event_type == "beforeSave_event") {
                    /**
                     * 通过formuuid ,datauuid 获取源表单，将流转方式存入源表单
                     */
                    var swRegisterFormUuid = dyform.getFieldValue("sw_register_formUuid");
                    var swRegisterUuid = dyform.getFieldValue("sw_register_uuid");
                    if (StringUtils.isNotBlank(swRegisterFormUuid) && StringUtils.isNotBlank(swRegisterUuid)) {
                        JDS.call({
                            service: "oaNoticeRegisterDockingFacadeService.saveCircleWayToRegister",
                            data: [swRegisterFormUuid, swRegisterUuid, circleName],
                            async: false,
                            success: function (result) {
                                console.log(result.data);
                            }
                        });
                    }
                }
                return result;
            }
        });
        // 发布状态字段事件管理配置下面代码
        // var $dyform = DyformFacade.get$dyform();
        // var dmsFileUuid = $("#dms_fileUuid").val();
        // if(dmsFileUuid === null || $.trim(dmsFileUuid) === "") {
        // var stayTime = DyformFacade.getDateByDateDaysParam(new Date(), 7,
        // "after");
        // var formatDate = DyformFacade.getDateStrByDateAndFormat(stayTime,
        // "yyyy-MM-dd");
        // $dyform.setFieldValueByFieldName("stay_time", formatDate);
        // }

        // 保存前后事件处理

        return AppNoticeDmsFileManagerDyformDocumentViewDevelopment;
    });