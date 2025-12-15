define([ "constant", "commons", "server", "appContext","appModal", "formBuilder","HtmlWidgetDevelopment","multiOrg","comboTree"],
    function(constant, commons, server,appContext,appModal, formBuilder, HtmlWidgetDevelopment) {
        var StringUtils = commons.StringUtils;
        var StringBuilder = commons.StringBuilder;
        var JDS = server.JDS;

        // 平台管理_流程定义设计器二开

        var WorkFlowDesignerWidgetDevelopment = function() {
            HtmlWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(WorkFlowDesignerWidgetDevelopment, HtmlWidgetDevelopment, {
            // 准备创建回调
            prepare: function () {},
            // 创建后回调
            create: function () {},
            // 初始化回调
            init: function() {
                var StringBuilder = commons.StringBuilder;
                var _self = this;
                _self.widget.element.find('.designer-controls li').on('click',function () {
                    var $this = $(this);
                    if($this.hasClass('active')) {
                        $this.removeClass('active');
                        goSelectedTool = null;
                    } else {
                        _self.widget.element.find('.designer-controls li').removeClass('active');
                        $this.addClass('active');
                        if(!window.goSelectedTool) {
                            goSelectedTool = {};
                        }
                        goSelectedTool.Type = $this.attr('data-type');
                    }
                    document.getElementById('newWorkFlowDesigner').contentWindow.goWorkFlow.curAddObject = goSelectedTool ? goSelectedTool.Type : null;
                })
            },
            refresh: function () {
                var _self = this;
                _self.init()
            }
        });
        return WorkFlowDesignerWidgetDevelopment;
    });

