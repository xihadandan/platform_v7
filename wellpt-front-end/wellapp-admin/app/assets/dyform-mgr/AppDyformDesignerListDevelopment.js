define([ "constant", "commons", "server", "ListViewWidgetDevelopment" ],
    function(constant, commons, server, ListViewWidgetDevelopment) {
        // 视图组件二开基础
        var AppDyformDesignerListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 平台管理_公共资源_表单字段列表组件二开
        commons.inherit(AppDyformDesignerListDevelopment, ListViewWidgetDevelopment, {
            init: function () {
                var _self = this;
                var queueTypeArr = [{
                    id: 'all',
                    text: '全部'
                },{
                    id: 'P',
                    text: '存储单据'
                },{
                    id: 'V',
                    text: '展现单据'
                },{
                    id: 'M',
                    text: '手机单据'
                },{
                    id: 'MST',
                    text: '子表单'
                }];

                var $search = _self.getWidget().element.find('.keyword-search-wrap');
                var $typeSelect = $('<input>',{
                    id: 'formType',
                    "class": 'query-fields',
                    style: 'width: 100px'
                });
                var $typeSelectWrap = $('<div>',{
                    "class": 'pull-left',
                    style: 'width: 100px'
                });
                $typeSelectWrap.append($typeSelect);
                $search.before($typeSelectWrap);
                $typeSelect.wellSelect({
                    valueField: 'formType',
                    searchable: false,
                    style: {'height':'34px'},
                    data: queueTypeArr
                }).wellSelect('val','all');

                $typeSelect.on('change',function() {
                    var $this = $(this);
                    var type = $this.val();
                    if (StringUtils.isNotBlank(type)) {
                        if(type === 'all') {
                            type = '';
                        }
                        _self.widget.addParam('formType',type);
                    }
                    _self.widget.refresh(true);
                });
            },
            //定义导出
            btn_export: function () {
                this.onExport('formDefinition');
            },
            //定义导入
            btn_import: function () {
                this.onImport();
            },
            btn_dependence: function () {

            },
            btn_del: function(){
                var self = this;
                var uuids = self.getSelectionUuids();
                var rowDatas = self.getSelections();
                if (!uuids.length) {
                    appModal.error("请选择记录!");
                    return;
                }

                var ids = uuids.join(",");
                appModal.confirm("确定要删除所选资源？",function(result){
                    if(result){
                        JDS.call({
                            service : "dyFormFacade.dropForm",
                            data : uuids,
                            success : function(result) {
                                appModal.success("删除成功!");
                                self.refresh()
                            },
                            error : function(jqXHR){
                                var faultData = JSON.parse(jqXHR.responseText);
                                appModal.alert(faultData.msg);
                            }
                        });
                    }
                });
            },
            btn_preview: function (e,ui,rowData) {
                var url = '/pt/dyform/definition/open?formUuid=' + rowData.uuid;
                appContext.getNavTabWidget().createTab('dyform',rowData.name,'iframe',url);
            },
            btn_del_lineEnd: function (e,ui,rowData) {
                var self = this;
                appModal.confirm("确定要删除所选资源？",function(result){
                    if(result){
                        JDS.call({
                            service : "dyFormFacade.dropForm",
                            data : rowData.uuid,
                            success : function(result) {
                                appModal.success("删除成功!");
                                self.refresh()
                            },
                            error : function(jqXHR){
                                var faultData = JSON.parse(jqXHR.responseText);
                                appModal.alert(faultData.msg);
                            }
                        });
                    }
                });
            }
        });

        return AppDyformDesignerListDevelopment;
    });

