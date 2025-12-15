define(["constant", "commons", "server", "appContext", "appModal", "formBuilder", "HtmlWidgetDevelopment"],
    function (constant, commons, server, appContext, appModal, formBuilder, HtmlWidgetDevelopment) {
        var StringUtils = commons.StringUtils;
        var StringBuilder = commons.StringBuilder;
        var JDS = server.JDS;

        //  平台应用_公共资源_业务通讯录编辑二开
        var AppWorkflowCategorySetViewWidgetDevelopment = function () {
            HtmlWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppWorkflowCategorySetViewWidgetDevelopment, HtmlWidgetDevelopment, {
            // 准备创建回调
            prepare: function () { },
            // 创建后回调
            create: function () { },
            // 初始化回调
            init: function () {
                var _self = this;

                var bean = {
                    "uuid" : null,
                    "name" : null,
                    "code" : null
                };
                // 表单选择器
                var form_selector = "#workflow_category_form";
                var _uuid;
                var _rowData;
                if(_self.getWidgetParams().uuid) {
                    _rowData = _self.getWidgetParams();
                    _uuid = _rowData.uuid;
                    bean = _rowData;
                    $(form_selector).json2form(bean);
                    $("#workflow_category_code").prop("readonly", "readonly");
                } else {
                    JDS.restfulPost({
						url: ctx + "/api/workflow/category/generateFlowCategoryCode",
                        service : "flowCategoryService.generateFlowCategoryCode",
                        success : function(result) {
                            $(":input[name='code']").val(result.data);
                        }
                    });
                    $("#workflow_category_code").prop("readonly", "");
                }

                var validator = $.common.validation.validate("#workflow_category_form", "flowCategory");

                // 保存流程分类
                $("#workflow_category_btn_save").click(function() {
                    if (!validator.form()) {
                        return false;
                    }
                    $(form_selector).form2json(bean);
                    JDS.restfulPost({
					    url: ctx + "/api/workflow/category/save",
                        data : bean,
                        validate : true,
                        success : function(result) {
                            appModal.success("保存成功！");
                            appContext.getNavTabWidget().refreshParentTab();
                        }
                    });
                });

            },


            refresh: function () {
                var _self = this;
                _self.init()
            }
        });
        return AppWorkflowCategorySetViewWidgetDevelopment;
    });

