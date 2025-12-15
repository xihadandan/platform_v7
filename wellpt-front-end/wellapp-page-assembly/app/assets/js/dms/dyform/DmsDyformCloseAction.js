define("DmsDyformCloseAction", ["jquery", "commons", "constant", "server", "appContext", "appModal",
    "DmsDyformActionBase"], function ($, commons, constant, server, appContext, appModal, DmsDyformActionBase) {
    var StringBuilder = commons.StringBuilder;
    // 表单单据操作_关闭
    var DmsDyformCloseAction = function () {
        DmsDyformActionBase.apply(this, arguments);
    }
    commons.inherit(DmsDyformCloseAction, DmsDyformActionBase, {
        btn_dyform_close: function (options) {
            var _self = this;
            var params = options.params || {};
            var documentData = options.data;
            // 直接关闭时不进行表单数据变更验证提示
            if (params.directClose != "true" && _self.isDocumentDataChanged(documentData)) {
                appModal.confirm("文档数据已变更，是否放弃您所做的更改！", function (result) {
                    if (result) {
                        delete options.data;
                        _self.dmsDataServices.performed(options);
                    }
                });
            } else {
                delete options.data;
                _self.dmsDataServices.performed(options);
            }
        },
        isDocumentDataChanged: function (documentData) {
            if (documentData == null || documentData.dyFormData == null) {
                return false;
            }
            var dyFormData = documentData.dyFormData;
            // 添加的数据
            var addedFormDatas = dyFormData.addedFormDatas;
            if (addedFormDatas != null && JSON.stringify(addedFormDatas) != "{}") {
                return true;
            }
            // 删除的数据
            var deletedFormDatas = dyFormData.deletedFormDatas;
            for (var key in deletedFormDatas) {
                var deletedFormData = deletedFormDatas[key];
                if ($.isArray(deletedFormData) && deletedFormData.length > 0) {
                    return true;
                }
            }
            // 更新的数据
            var updatedFormDatas = dyFormData.updatedFormDatas;
            for (var key in updatedFormDatas) {
                var updatedFormData = updatedFormDatas[key];
                if (updatedFormData) {
                    for (var p in updatedFormData) {
                        var updatedFields = updatedFormData[p];
                        // 忽略从表排序号
                        if ($.isArray(updatedFields) && updatedFields.length > 0) {
                            var updateFieldsCount = 0;
                            $.each(updatedFields,function (i,item) {
                                if($.inArray(item, ['id', 'seqNO', 'sort_order']) < 0) {
                                    updateFieldsCount++;
                                }
                            });
                            if(updateFieldsCount) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
    });
    return DmsDyformCloseAction;
});