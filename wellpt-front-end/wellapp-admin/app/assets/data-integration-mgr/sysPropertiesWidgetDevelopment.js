define([ "constant", "commons", "server", "appContext","appModal","wSelect2", "HtmlWidgetDevelopment" ],
    function(constant, commons, server,appContext,appModal,wSelect2, HtmlWidgetDevelopment) {
        var JDS = server.JDS;

        var sysPropertiesWidgetDevelopment = function() {
            HtmlWidgetDevelopment.apply(this, arguments);
        };
        commons.inherit(sysPropertiesWidgetDevelopment, HtmlWidgetDevelopment, {
            init: function(){

                var fieldList = ["sg_query_plan_project","sg_download_ydgh_attachment","xt_wl","sg_flow_template_file","dj_line_enable","dj_qysl_client_system_enable","sqld_enable","wssb_enable"]

                for(var i = 0; i < fieldList.length; i++){
                    var field = fieldList[i];
                    var dataList = [];
                    if(field == "sg_query_plan_project" || field == "sg_download_ydgh_attachment"){
                        dataList = [

                            {id:"true",text:"是"},
                            {id:"false",text:"否"}
                        ]
                    }else{
                        dataList = [
                            {id:"false",text:"禁用"},
                            {id:"true",text:"启用"}
                        ]
                    }
                    $("#" + field).wSelect2({
                        data:dataList,
                        valueField: field,
                        remoteSearch : false
                    })
                }

                function getdata(){
                    JDS.call({
                        service : "exchangeDataConfigService.getAllSysProperties",
                        data: [""],
                        async:false,
                        validate : true,
                        version:"",
                        success : function(result) {
                            var dataMap = result.data;
                            for(var key in dataMap){
                                if($("#"+key).length>0){
                                    $("#"+key).val(dataMap[key]["proValue"]);
                                    if(fieldList.indexOf(key)>-1){
                                        $("#"+key).trigger("change");
                                    }
                                }
                            }
                        }
                    });
                }
                getdata();

                $("#sys_param_btn_save").off().on("click",function() {
                    var itemList = new Array();
                    $("input").each(function(){
                        var item = new Object();
                        var proCnName = $(this).parent().prev().text();
                        var proEnName = $(this).attr("id");
                        var proValue = $(this).val();
                        if(proEnName !== undefined){
                            item["proCnName"] = proCnName;
                            item["proEnName"] = proEnName;
                            item["proValue"] = proValue;
                            item["moduleId"] = "SYSPROPERTIES";
                            itemList.push(item);
                        }
                    });

                    JDS.call({
                        service : "exchangeDataConfigService.saveSysPropertiesList",
                        data : [ itemList ],
                        async:false,
                        validate : true,
                        version:"",
                        success : function(result) {
                            if(result.data){
                                appModal.success("保存成功！");
                            }else{
                                appModal.error("保存异常！");
                            }
                        }
                    });
                });

            },
            refresh:function(){
                this.init()
            }
        })
        return sysPropertiesWidgetDevelopment;
    })

