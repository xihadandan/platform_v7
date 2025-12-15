define([ "constant", "commons", "server", "ListViewWidgetDevelopment" ],
    function(constant, commons, server, ListViewWidgetDevelopment) {
        // 视图组件二开基础
        var AppSyncManageListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppSyncManageListDevelopment, ListViewWidgetDevelopment, {
            lineEnderButtonHtmlFormat:function(format, row, index){
                console.log(row)
                var cd = row.trigger_name == null || row.trigger_name == "";// create or drop
                var ed = row.status == "ENABLED";//enable or disable

                var btn = "<div class='div_lineEnd_toolbar' style='display:inline-block;margin-left:10px;margin-right:10px;padding-bottom:1px;overflow: visible' index='"+index+"'>" ;
                btn += cd ? ("<button type='button' class='well-btn w-btn-primary btn_class_btn_create'>创建</button>"):("<button type='button' class='well-btn w-btn-danger btn_class_btn_del'>删除</button>");
                btn += cd ? "" :(ed ? ("<button type='button' class='well-btn w-btn-primary btn_class_btn_stop'>停用</button>") : ("<button type='button' class='well-btn w-btn-primary btn_class_btn_start'>启用</button>"));
                btn += cd ? "" : ("<button type='button' class='well-btn w-btn-primary btn_class_btn_reGenerate'>重新编译</button>");
                btn += "</div>";
                format.after = btn;

            },

            triggerServices:function (serviceName,operateName, index){
                var self = this;
                var localTableName = "";
                if(typeof index == "undefined" || index == null){
                    var rowids = this.getSelectionPrimaryKeys();
                    if (rowids.length <= 0) {
                        appModal.error("请选择记录!");
                        return true;
                    }
                    for(var i=0; i < rowids.length; i++){
                        localTableName += rowids[i] + ";";
                    }
                    if(localTableName.endsWith(";") == true){
                        localTableName = localTableName.substring(0, localTableName.length - 1);
                    }
                }else{
                    var data = this.getData()
                    localTableName = data[index].table_name;
                }
                $.ajax({
                    type : "post",
                    url : ctx+"/syn/trigger/" + serviceName,
                    data: {tableName : localTableName},
                    success : function(result) {
                        if(result == "success"){
                            appModal.success(operateName + "成功.",function(){
                                self.refresh()
                            })
                        }else {
                            appModal.error(result.msg);
                        };
                    }
                });
            },

            btn_create: function (e){
                var index = $(e.target).parents("tr").data("index");
                this.triggerServices("create","创建",index);
            },

            btn_del: function (e){
                var index = $(e.target).parents("tr").data("index");
                this.triggerServices("drop","删除",index);
            },

            btn_start: function (e){
                var index = $(e.target).parents("tr").data("index");
                this.triggerServices("enable","启用",index);
            },

            btn_stop: function (e){
                var index = $(e.target).parents("tr").data("index");
                this.triggerServices("disable","停用",index);
            },

            btn_reGenerate: function (e){
                var index = $(e.target).parents("tr").data("index");
                this.triggerServices("reGenerate","重新编译",index);
            },

            btn_createAll: function (e){
                this.triggerServices("create","创建");
            },

            btn_delAll: function (){
                var rowData = this.getSelections()[0]
                if(rowData.status == null || rowData.status == "" ){
                    appModal.error("请先创建再删除")
                    return false;
                }else{
                    this.triggerServices("drop","删除");
                }
            },

            btn_startAll: function (){
                var rowData = this.getSelections()[0]
                if(rowData.status == null || rowData.status == "" ){
                    appModal.error("请先创建再启用")
                    return false;
                }else{
                    this.triggerServices("enable","启用");
                }
            },

            btn_stopAll: function (){
                var rowData = this.getSelections()[0]
                if(rowData.status == null || rowData.status == "" ){
                    appModal.error("请先创建再停用")
                    return false;
                }else{
                    this.triggerServices("disable","停用");
                }
            }
        });

        return AppSyncManageListDevelopment;
    });

