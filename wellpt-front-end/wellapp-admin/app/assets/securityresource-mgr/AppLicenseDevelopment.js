define([ "constant", "commons", "server", "appContext","appModal", "HtmlWidgetDevelopment" ],
    function(constant, commons, server,appContext,appModal, HtmlWidgetDevelopment) {
        // 平台管理_产品集成_页面引用资源列表_视图组件二开
        var AppLicenseDevelopment = function() {
            HtmlWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppLicenseDevelopment, HtmlWidgetDevelopment, {
            init: function(){
                // 许可证上传
                this.getStatus()
                $("#btn_upload").on("click", function() {
                    if ($("#upload").val() == "") {
                        appModal.error("请选择.lic许可文件！");
                    } else {
                        // 上传文件
                        $.ajaxFileUpload({
                            url : ctx + "/security/license/upload",// 链接到服务器的地址
                            secureuri : false,
                            fileElementId : 'upload',// 文件选择框的ID属性
                            dataType : 'text', // 服务器返回的数据格式
                            success : function(data, status) {
                                if (data == "true") {
                                    var fileId = data[0].fileID;
                                    server.JDS.call({
                                        service:'licenseService.installLicense',
                                        // fileId:‘文件上传ID（通过平台默认上传控件，上传后会返回文件ID，可以看formBuilder.buidFileUpload ,文件上传功能也需要通过这个插件渲染）’
                                        data:[fileId],
                                        version:"",
                                        success:function(res){
                                            if(res.data){
                                                //安装成功
                                                $(".upload_result_info").text("认证成功！");
                                            }else{
                                                $(".upload_result_info").text("认证失败，请上传有效的许可证！")
                                            }
                                        }
                                    })
                                } else {
                                    $(".upload_result_info").text("认证失败，请上传有效的许可证！")
                                }
                            },
                            error : function() {
                                $(".upload_result_info").text("认证失败，服务器错误！")
                            }
                        });
                    }
                });
            },
            getStatus: function(){             // 判断是否认证
                server.JDS.call({
                    service:'licenseService.verifyCurrentMachine',
                    data:[],
                    version:"",
                    success:function(res){
                        var permit=res.success;
                        var machineCode=res.data;
                        if(permit){
                            $("#businessEdition").show();
                            $("#trialVersion").hide()
                        }else{
                            $("#machineCode").val(machineCode)
                            $("#trialVersion").show()
                            $("#businessEdition").hide();
                        }
                    }
                });
            }
        })
        return AppLicenseDevelopment;
    })

