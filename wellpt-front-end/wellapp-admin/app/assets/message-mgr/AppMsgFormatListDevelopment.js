define([ "constant", "commons", "server", "appContext","appModal", "ListViewWidgetDevelopment" ],
    function(constant, commons, server,appContext,appModal, ListViewWidgetDevelopment) {
        var StringUtils = commons.StringUtils;
        var JDS = server.JDS;

        var AppMsgFormatListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppMsgFormatListDevelopment, ListViewWidgetDevelopment, {
            // 视图组件渲染前回调方法，子类可覆盖
            beforeRender : function(options, configuration) {
                console.log(options,configuration)
                if(this._moduleId()){
                    $.each(configuration.columns, function(i,item){
                        if(item.name == "name" || item.name == "code"){
                            item.hidden = "0"
                        }else{
                            item.hidden = "1"
                        }
                    })
                }
            },
            _moduleId: function () {
                return this.getWidgetParams().moduleId
            },
            beforeLoadData : function(options, configuration) {
                var uuid = this.getParam("classifyUuid");
                this.removeParam("classifyUuid");
                this.clearOtherConditions()
                if(uuid == undefined){
                    var id = $(this.widget.element).siblings(".ui-wHtml").attr("id");
                    if(appContext.getWidgetById(id)){
                        var $wHtml = $(appContext.getWidgetById(id).element);
                        var $items = $wHtml.find("#msg_category_tree .msg-category-item.hasSelectCate");
                        if($items.size()>0){
                            uuid = $items.data("uuid");
                        }
                    }
                }

                if(uuid == "user"){
                    this.addOtherConditions([{
                        columnIndex : 'sender',
                        value : "system",
                        type : 'ne'
                    }])
                }else if(uuid != undefined && uuid != "all"){
                    this.addOtherConditions([{
                        columnIndex : 'classifyUuid',
                        value : uuid,
                        type : 'eq'
                    }])
                }

                if(localStorage.getItem("moduleId")){
                    localStorage.removeItem("moduleId")
                }

                var $list = $(this.widget.element).parents(".msg-content-list");
                if($list.parent().hasClass("tab-pane")){
                    this.widget.addParam('moduleId', this.getWidgetParams().moduleId);
                    localStorage.setItem("moduleId",this.getWidgetParams().moduleId)
                }
            },
            btn_delete: function(){
                var self = this
                var datas = this.getSelections();
                if (datas.length == 0) {
                    appModal.error("请选择记录！");
                    return;
                }

                var ids = [];
                for(var i = 0; i < datas.length; i++ ){
                    ids.push(datas[i].id);
                }

                appModal.confirm("确定要删除所选记录吗？",function(res){
                    if(res){
                        JDS.call({
                            service:"messageTemplateService.deleteAllById",
                            data:[ids],
                            version:"",
                            success:function(result) {
                                appModal.success("删除成功！",function(){
                                    self.refresh()
                                });
                            }
                        });
                    }

                })
            },
            btn_deleteOne: function(e){
                var self = this;
                var index = $(e.target).parents("tr").data("index");
                var datas = this.getData();
                var ids = [datas[index].id];
                appModal.confirm("确定要删除所选记录吗？",function(res){
                    if(res){
                        JDS.call({
                            service:"messageTemplateService.deleteAllById",
                            data:[ids],
                            version:"",
                            success:function(result) {
                                appModal.success("删除成功!",function(){
                                    self.refresh()
                                });
                            }
                        });
                    }
                })
            },
            btn_export: function() {
                this.onExport("messageTemplate")
            },
            btn_import: function(){
                this.onImport()
            }
        });
        return AppMsgFormatListDevelopment;
    });

