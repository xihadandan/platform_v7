define(["jquery", "commons", "constant", "server", "ListViewWidgetDevelopment", "appModal", "wUnit2"],
    function ($, commons, constant, server, ListViewWidgetDevelopment, appModal, wUnit2) {
        var DmsDocExcContactBookDevelopment = function () {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        commons.inherit(DmsDocExcContactBookDevelopment, ListViewWidgetDevelopment, {

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

            collectionContactInfoData: function () {
                var _self = this;
                var contactInfo = {
                    moduleId: _self.getModuleId()
                };
                $(".dyform :input.editableClass").each(function (i) {
                    contactInfo[$(this).attr('id')] = $(this).val();
                });

                return contactInfo;
            },


            getModuleId: function () {
                if (appContext.getCurrentUserAppData().appData.module) {
                    return appContext.getCurrentUserAppData().appData.module.id;
                }
                return appContext.getCurrentUserAppData().appData.dispatchAppPath.split("/")[2];

            },

            validateData: function (data) {
                var rst = {
                    msg: '',
                    success: true
                };

                if (!data.contactName || $.trim(data.contactName).length == 0) {
                    rst.success = false;
                    rst.msg = '姓名必填';
                }
                return rst;
            },

            addContactBook: function (data) {
                var _self = this;
                var action = data.UUID ? '编辑' : '新建';
                var $dialog = this.popDialog(this.createContactBookInfoHtml(data), action + "联系人", {
                    confirm: {
                        label: "确定", className: "btn-primary", callback: function () {
                            var contactInfo = _self.collectionContactInfoData();
                            var validateRst = _self.validateData(contactInfo);
                            if (!validateRst.success) {
                                appModal.alert(validateRst.msg);
                                return false;
                            }

                            if (data && data.UUID) {
                                contactInfo.uuid = data.UUID;
                            }
                            var commitRet = false;
                            server.JDS.call({
                                service: 'docExchangerFacadeService.saveContactBook',
                                data: [contactInfo],
                                success: function (result) {
                                    appModal.info(action + '联系人成功');
                                    commitRet = true;
                                    _self.refresh();
                                },
                                error: function (jqXHR) {
                                    appModal.alert(action + "联系人失败");
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

                    $("#contactUnitName").wSelect2({
                        serviceName: "docExchangerComponentService",
                        queryMethod: "getContactBookUnitSelectData",
                        labelField: "contactUnitName",
                        params: {moduleId: _self.getModuleId()},
                        valueField: "contactUnitUuid",
                        remoteSearch: false
                    });

                    $("#relaUserName").on('click', function () {
                        $.unit2.open({
                            unitId: SpringSecurityUtils.getCurrentUserUnitId(),
                            valueField: "relaUserId",
                            labelField: "relaUserName",
                            title: "选择用户",
                            type: "MyUnit;MyDept",
                            multiple: false,
                            selectTypes: "U",
                            valueFormat: "justId"
                        });
                    });

                    if ($("#relaUserId").val()) {
                        var userVo = OrgUtils.getUserBeanById($("#relaUserId").val());
                        $("#relaUserName").val(userVo.userName);
                    }


                });

            },

            editContactBook: function () {
                var _self = this;
                var selections = this.getSelections();
                if (selections.length == 0 || selections.length > 1) {
                    appModal.info('请选中一行进行编辑');
                    return;
                }
                this.addContactBook(selections[0]);
            },

            delContactBook: function () {
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
                                service: 'docExchangerFacadeService.deleteContactBook',
                                data: [uuids],
                                success: function (result) {
                                    appModal.info('删除联系人成功');
                                    commitRet = true;
                                    _self.refresh();
                                },
                                error: function (jqXHR) {
                                    appModal.alert("删除联系人失败");
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

            createContactBookInfoHtml: function (data) {
                var $table = $("<table>", {"class": "table"});
                var $nameTr = $("<tr>").append(
                    $("<td>", {"class": "label-td required"}).append('姓名'),
                    $("<td>").append($("<input>", {
                        "type": "text", "id": "contactName",
                        "maxlength": 20, "class": "editableClass",
                        "value": data ? data.CONTACT_NAME : ''
                    }))
                );
                var $emailTr = $("<tr>").append(
                    $("<td>", {"class": "label-td"}).append('个人邮箱'),
                    $("<td>").append($("<input>", {
                        "type": "text", "id": "personalEmail",
                        "maxlength": 60, "class": "editableClass",
                        "value": data ? data.PERSONAL_EMAIL : ''
                    }))
                );
                var $cnbrTr = $("<tr>").append(
                    $("<td>", {"class": "label-td"}).append('手机号码'),
                    $("<td>").append($("<input>", {
                        "type": "text", "id": "cellphoneNumber",
                        "maxlength": 30, "class": "editableClass",
                        "value": data ? data.CELLPHONE_NUMBER : ''
                    }))
                );
                var $unitTr = $("<tr>").append(
                    $("<td>", {"class": "label-td"}).append('所属单位'),
                    $("<td>").append($("<input>", {
                        "type": "text", "id": "contactUnitName", "class": "editableClass"
                    }), $("<input>", {
                        "type": "hidden",
                        "id": "contactUnitUuid",
                        "class": "editableClass",
                        "value": data ? data.CONTACT_UNIT_UUID : ""
                    }))
                );

                var $sysUserTr = $("<tr>").append(
                    $("<td>", {"class": "label-td"}).append('关联系统用户'),
                    $("<td>").append($("<input>", {
                        "type": "text", "id": "relaUserName",
                        "class": "editableClass", "readonly": ""
                    }), $("<input>", {
                        "type": "hidden",
                        "id": "relaUserId",
                        "class": "editableClass",
                        "value": data ? data.RELA_USER_ID : ""
                    }))
                );


                var $remarkTr = $("<tr>").append(
                    $("<td>", {"class": "label-td"}).append('备注'),
                    $("<td>").append($("<textarea>", {
                        "id": "remark", "class": "editableClass", "maxlength": 150,
                        "style": "resize:vertical;height:100px;width:100%;"
                    }).text(data ? (data.REMARK ? data.REMARK : '' ): ''))
                );


                $table.append($nameTr, $emailTr, $cnbrTr, $unitTr, $sysUserTr, $remarkTr);
                return ($("<form>", {"class": "dyform"}).append($table))[0].outerHTML
            },

            popDialog: function (html, title, buttons, shownCallback, size) {
                return appModal.dialog({
                    title: title,
                    message: html,
                    size: size ? size : 'large',
                    buttons: buttons,
                    shown: function () {
                        if ($.isFunction(shownCallback)) {
                            shownCallback();
                        }
                    }
                });
            },

        });

        return DmsDocExcContactBookDevelopment;
    }
);