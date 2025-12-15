define([ "constant", "commons", "server", "ListViewWidgetDevelopment" ],
    function(constant, commons, server, ListViewWidgetDevelopment) {
        // 视图组件二开基础
        var AppApiDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppApiDevelopment, ListViewWidgetDevelopment, {
            // 视图组件渲染完成回调方法，子类可覆盖
            afterRender : function(options, configuration) {
                var self = this
                $("#btn_resend").live("click",function(){   // 弹窗按钮-重发
                    resendCommand({
                        uuids:[$("#commandUuid").val()],
                        requestBody: $("#requestBody").val()
                    },self)
                })
            },
            btn_check: function(e){           // 查看报文
                var index = $(e.target).parents("tr").data("index")
                var data = this.getData()
                var html = initTab()
                loadCommandRequestBodyAndResponseBody(data[index].uuid,html)
            },
            btn_resend: function(e){           // 重发
                var index = $(e.target).parents("tr").data("index")
                var data = this.getData()
                var self = this
                appModal.confirm({
                    message:"确定要重发吗？",
                    callback:function(){
                        resendCommand({
                            uuids: [data[index].uuid],
                            requestBody: ""
                        },self)
                    }
                })
            },
            btn_all_send: function(){         // 批量重发
                var selectedUuids = this.getSelectionUuids()
                resendCommand({
                    uuids:selectedUuids
                },this)
            },
            lineEnderButtonHtmlFormat:function(format, row, index){             // 隐藏重发按钮
                if(row.apiMode != "OUT"){
                    format.after = "<div class='div_lineEnd_toolbar' style='display:inline-block;margin-left:10px;margin-right:10px;padding-bottom:1px;overflow: visible' index='0'><button type='button' class='btn btn-primary btn-bg-color btn_class_btn_check' title='查看报文'>查看报文</button></div>"
                }
            }
        });

        function resendCommand(param,ele) {             // 重发
            if (!param || (param.uuids && param.uuids.length == 0)) {
                appModal.warning("请选中记录")
            }else{
                JDS.call({
                    service: "apiCommandFacadeService.resend",
                    data: [param.uuids, param.requestBody],
                    version: '',
                    success: function (result) {
                        if(result.success){
                            setTimeout(function () {
                                appModal.hide()
                                ele.refresh()
                            },300)
                        }
                    }
                });
            }
        }

        function loadCommandRequestBodyAndResponseBody(uuid,html) {         // tab 表单内容
            JDS.call({
                service: "apiCommandFacadeService.getCommandDetailByCommandUuid",
                data: [uuid],
                version: '',
                async: false,
                success: function (result) {
                    var bean = result.data;
                    appModal.dialog({message:html,title:"详情",buttons:null})
                    $("#command_form").json2form(bean);
                }
            });
        }

        function initTab(){            // 弹窗tab
            var html =  '<form action="" id="command_form" class="form-widget"> ' +
                            '<ul class="nav nav-tabs" role="tablist"> ' +
                                '<li role="presentation"><a href="#tabs-1" aria-controls="tabs-1" role="tab" data-toggle="tab" aria-expanded="true">请求</a></li> ' +
                                '<li role="presentation" class="active"><a href="#tabs-2" aria-controls="tabs-2" role="tab" data-toggle="tab" aria-expanded="true">响应</a></li> ' +
                            '</ul>'+
                            '<div class="tab-content" style="margin-top: 20px;">' +
                                '<div id="tabs-1" role="tabpanel" class="tab-pane"> <input type="hidden" id="commandUuid" name="commandUuid"/>'+
                                    '<div class="form-horizontal">'+
                                        '<div class="form-group">'+
                                            '<label for="requestBody" class="col-sm-2 control-label">报文</label>'+
                                            '<div class="col-sm-10">'+
                                                '<textarea type="text" class="form-control" id="requestBody" name="requestBody" rows="10" cols="50"></textarea>'+
                                            '</div>'+
                                        '</div>'+
                                    '</div>'+
                                '</div>'+
                                '<div id="tabs-2" role="tabpanel" class="tab-pane active">'+
                                    '<div class="form-horizontal">'+
                                        '<div class="form-group">'+
                                            '<label for="responseBody" class="col-sm-2 control-label">报文</label>'+
                                            '<div class="col-sm-10">'+
                                                '<textarea type="text" class="form-control" id="responseBody" name="responseBody" rows="10" cols="50"></textarea>'+
                                            '</div>'+
                                        '</div>'+
                                    '</div>'+
                                '</div>' +
                            '</div>'+
                            '<div class="row text-center">'+
                                 '<button id="btn_resend" type="button" class="btn btn-primary">重发</button>'+
                            '</div>'
                        '</form>'
            return html
        }

        return AppApiDevelopment;
    });

