define(["jquery", "commons", "constant", "server", "ListViewWidgetDevelopment", "appModal", "wUnit2"],
    function ($, commons, constant, server, ListViewWidgetDevelopment, appModal, wUnit2) {
        var DmsDocExcContactBookUnitDevelopment = function () {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        commons.inherit(DmsDocExcContactBookUnitDevelopment, ListViewWidgetDevelopment, {

            beforeRender: function (options, configuration) {
                var _self = this;
                this.loadCssLink();

            },

            afterRender: function (options, configuration) {
                var _self = this;

            },

            loadCssLink: function () {
                $("head").append(
                    $("<link>", {
                        "href": ctx + "/resources/pt/css/dyform/explain/dyform.css",
                        "rel": "stylesheet"
                    })
                );
            },

            addUnit: function (data) {
                var _self = this;
                var action = data.UUID ? '编辑' : '新建';
                var $dialog = this.popDialog(this.createUnitInfoHtml(data), action + "单位", {
                    confirm: {
                        label: "确定", className: "btn-primary", callback: function () {
                            var unitData = {
                                unitName: $("#unitName").val(),
                                unitCode: $("#unitCode").val(),
                                fullUnitName: $("#fullUnitName").val(),
                                moduleId: _self.getModuleId()
                            };
                            if (data && data.UUID) {
                                unitData.uuid = data.UUID;
                            }
                            if ($.trim(unitData.unitName).length == 0) {
                                appModal.info('请输入单位简称')
                                return false;
                            }
                            var commitRet = false;
                            server.JDS.call({
                                service: 'docExchangerFacadeService.saveContactBookUnit',
                                data: [unitData],
                                success: function (result) {
                                    appModal.info(action + '单位成功');
                                    commitRet = true;
                                    _self.refresh();
                                },
                                error: function (jqXHR) {
                                    appModal.alert(action + "单位失败");
                                    commitRet = false;
                                },
                                async: false
                            });


                            return commitRet;
                        }
                    },
                    cancel: {
                        label: "取消", className: "btn-default", callback: function () {
                        }
                    }
                }, function () {
                });

            },

            editUnit: function () {
                var _self = this;
                var selections = this.getSelections();
                if (selections.length == 0 || selections.length > 1) {
                    appModal.info('请选中一行进行编辑');
                    return;
                }
                this.addUnit(selections[0]);
            },

            delUnit: function () {
                var _self = this;
                var selections = this.getSelections();
                if (selections.length == 0) {
                    appModal.info('请选中需要删除的行数据');
                    return;
                }
                var $dialog = this.popDialog('确定要删除吗？', "提示", {
                    confirm: {
                        label: "确定", className: "btn-primary", callback: function () {
                            var commitRet = false;
                            var uuids = [];
                            for (var i = 0; i < selections.length; i++) {
                                uuids.push(selections[i]['UUID']);
                            }
                            server.JDS.call({
                                service: 'docExchangerFacadeService.deleteContactBookUnit',
                                data: [uuids],
                                success: function (result) {
                                    appModal.info('删除单位成功');
                                    commitRet = true;
                                    _self.refresh();
                                },
                                error: function (jqXHR) {
                                    appModal.alert("删除单位失败");
                                    commitRet = false;
                                },
                                async: false
                            });
                            return commitRet;
                        }
                    },
                    cancel: {
                        label: "取消", className: "btn-default", callback: function () {
                        }
                    }
                }, function () {
                }, 'small');
            },

            createUnitInfoHtml: function (data) {
                var $table = $("<table>", {"class": "table"});
                var $nameTr = $("<tr>").append(
                    $("<td>", {"class": "label-td required"}).append('单位简称'),
                    $("<td>").append($("<input>", {
                        "type": "text", "id": "unitName",
                        "maxlength": 64, "class": "editableClass",
                        "value": data ? data.UNIT_NAME : ''
                    }))
                );
                var $fullnameTr = $("<tr>").append(
                    $("<td>", {"class": "label-td"}).append('单位全称'),
                    $("<td>").append($("<input>", {
                        "type": "text", "id": "fullUnitName",
                        "maxlength": 120, "class": "editableClass",
                        "value": data ? data.FULL_UNIT_NAME : ''
                    }))
                );
                var $codeTr = $("<tr>").append(
                    $("<td>", {"class": "label-td"}).append('单位编码'),
                    $("<td>").append($("<input>", {
                        "type": "text", "id": "unitCode",
                        "maxlength": 20, "class": "editableClass",
                        "value": data ? data.UNIT_CODE : ''
                    }))
                );

                $table.append($nameTr, $fullnameTr, $codeTr);
                return ($("<form>", {"class": "dyform"}).append($table))[0].outerHTML
            },

            popDialog: function (html, title, buttons, shownCallback, size) {
                return appModal.dialog({
                    title: title,
                    message: html,
                    size: size ? size : 'middle',
                    buttons: buttons,
                    shown: function () {
                        if ($.isFunction(shownCallback)) {
                            shownCallback();
                        }
                    }
                });
            },

            getModuleId: function () {
                if (appContext.getCurrentUserAppData().appData.module) {
                    return appContext.getCurrentUserAppData().appData.module.id;
                }
                return appContext.getCurrentUserAppData().appData.dispatchAppPath.split("/")[2];

            },

        });

        return DmsDocExcContactBookUnitDevelopment;
    }
);