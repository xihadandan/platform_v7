define([ "jquery", "server", "commons", "constant", "appContext", "appModal" ], function($, server, commons, constant,
        appContext, appModal) {
	var JDS = server.JDS;
    var Browser = commons.Browser;
    var StringUtils = commons.StringUtils;
    var StringBuilder = commons.StringBuilder;

    var snAction = Browser.getQueryString("snAction");
    var addedColor = "#DDFADE";
    var changedColor = "#D6F0FF";
    var deletedColor = "#FFE7E7";

    var subformHeadTpl = new StringBuilder();
    subformHeadTpl.append('<table class="table" style="display: block;">')
    subformHeadTpl.append('  <thead>');
    subformHeadTpl.append('      <tr>');
    subformHeadTpl.append('          <th>序号</th>');
    subformHeadTpl.append('          <th>是否新增</th>');
    subformHeadTpl.append('          <th>是否删除</th>');
    subformHeadTpl.append('          <th>是否变更</th>');
    subformHeadTpl.append('          <th>变更列</th>');
    subformHeadTpl.append('      </tr>');
    subformHeadTpl.append('  </thead>');
    subformHeadTpl.append('  <tbody>{0}</tbody>');
    subformHeadTpl.append('</table>')
    // 公共模块
    var baseModule = {
        showButtons : function() {
            this.hideButtons();
        },
        // 获取侧边tab数据
        getSidebarTabData : function() {
            var _self = this;
            var tabs = [];
            // 比较快照版本显示变更标识tab
            if (snAction == "1") {
                tabs = [ {
                    name : "变更标识",
                    id : "snapshot_marker_info"
                } ];
            }
            return tabs;
        },
        // 显示侧边tab
        showSidebarTab : function(tabName, $tabContentContainer) {
            var _self = this;
            var dyform = _self.getDyform();
            if (tabName == "变更标识") {
                var lineThrouthStyle = "text-decoration:line-through";
                var sb = new StringBuilder();
                // 标识说明
                sb.append("<div class='snapshot-marker-info'>")
                sb.appendFormat("<div style='background-color:{0}'>字段值添加。</div>", addedColor);
                sb.appendFormat("<div style='background-color:{0};{1}'>字段值删除。</div>", deletedColor, lineThrouthStyle);
                sb.appendFormat("<div style='background-color:{0}'>字段值变更。</div>", changedColor);
                sb.append("</div>");
                // 主表字段
                sb.append("<div class='row'>");
                if (_self.changedMainFields) {
                    sb.append("<div class='mainform-changed-fields'>");
                    sb.append("主表差异字段：");
                    sb.append(_self.changedMainFields.join(", "));
                    sb.append("</div>");
                }
                var subformRecords = _self.changedSubformRecords;
                if (subformRecords) {
                    sb.append("<div class='subform-changed-info'>");
                    sb.append("从表差异信息：");
                    sb.append("</div>");
                    for ( var formUuid in subformRecords) {
                        var dataList = subformRecords[formUuid];
                        var subform = dyform.getSubform(dyform.getFormId(formUuid));
                        sb.append(subform.getDisplayName());
                        // 提取排序号
                        $.each(dataList, function(i, data) {
                            var seqNo = subform.getSeqNo(formUuid, data.rowId);
                            subform.getRowData(formUuid, data.rowId, function(rowData) {
                                data.seqNO = rowData.seqNO;
                            });
                        });
                        // 数据排序
                        dataList.sort(function(a, b) {
                            if (StringUtils.isBlank(a.seqNO)) {
                                a.seqNO = 0;
                            }
                            if (StringUtils.isBlank(b.seqNO)) {
                                b.seqNO = 0;
                            }
                            var a1 = parseInt(a.seqNO);
                            var b1 = parseInt(b.seqNO);
                            if (a1 < b1) {
                                return -1;
                            } else if (a1 > b1) {
                                return 1;
                            }
                            return 0;
                        });
                        var bodyTpl = new StringBuilder();
                        // 提取排序号
                        $.each(dataList, function(i, data) {
                            bodyTpl.append("<tr>")
                            bodyTpl.appendFormat("<td>{0}</td>", data.seqNO)
                            bodyTpl.appendFormat("<td>{0}</td>", data.isAdded ? "是" : "否")
                            bodyTpl.appendFormat("<td>{0}</td>", data.isDeleted ? "是" : "否")
                            bodyTpl.appendFormat("<td>{0}</td>", data.isUpdated ? "是" : "否");
                            bodyTpl.appendFormat("<td>{0}</td>", data.isUpdated ? data.updateFields.join(", ") : "");
                            bodyTpl.append("</tr>")
                        });
                        sb.appendFormat(subformHeadTpl.toString(), bodyTpl.toString());
                    }
                }
                if (_self.changedMainFields == null && _self.changedSubformRecords == null) {
                    sb.append("无字段差异信息！");
                }
                sb.append("</div>");
                $tabContentContainer.html(sb.toString());
            }
        }
    };

    // 1、比较快照版本模块
    var compareSnapshotsModule = {
        // 准备初始化表单
        prepareInitDyform : function(dyformOptions) {
            var _self = this;
            var snId1 = Browser.getQueryString("snId1");
            var snId2 = Browser.getQueryString("snId2");
            var snapshotDatas = [];
            JDS.restfulPost({
	            url : ctx + "/api/workflow/work/getFlowDataSnapshotByIds",
                data : {snapshotIds: [ snId1, snId2 ]},
                async : false,
                success : function(result) {
                    var dataList = result.data;
                    $.each(dataList, function(i) {
                        snapshotDatas.push(JSON.parse(this));
                    });
                }
            });
            if (snapshotDatas && snapshotDatas[0] && snapshotDatas[0].dyFormData) {
                dyformOptions.formData = snapshotDatas[0].dyFormData;
            }
            dyformOptions.displayAsLabel = true;
            _self.snapshotDatas = snapshotDatas;
        },
        onDyformInitSuccess : function() {
            var _self = this;
            // 调用父类方法
            _self._superApply(arguments);
            var newerFormData = _self.snapshotDatas[0].dyFormData;
            var olderFormData = _self.snapshotDatas[1].dyFormData;
            if (newerFormData == null || newerFormData.formDatas == null || olderFormData == null
                    || olderFormData.formDatas == null) {
                appModal.info("存在空表单数据，无法进行比较！");
                return;
            }

            var dyform = _self.getDyform();
            var newerFormDatas = newerFormData.formDatas;
            var olderFormDatas = olderFormData.formDatas;
            // 数据UUID MAP
            var newerDataUuidMap = {};
            $.each(newerFormDatas, function(formUuid, formDatas) {
                // 遍历数据列表
                $.each(formDatas, function(i, formData) {
                    // 获取旧数据的表单
                    var dataUuid = formData.uuid;
                    newerDataUuidMap[dataUuid] = dataUuid;
                    var olderFormData = _self.getOlderFormData(formUuid, dataUuid, olderFormDatas);
                    if (olderFormData != null) {
                        for ( var key in formData) {
                            if (olderFormData.hasOwnProperty(key)) {
                                // 字段值对象化字符串比较
                                var obj1 = {
                                    v : formData[key]
                                };
                                var obj2 = {
                                    v : olderFormData[key]
                                };
                                if (JSON.stringify(obj1) != JSON.stringify(obj2)) {
                                    if (olderFormData[key] == null || olderFormData[key] == "") {
                                        // 新增
                                        _self.markAdded(key, dataUuid, formUuid);
                                    } else if (formData[key] == null || formData[key] == "") {
                                        // 删除
                                        _self.markDeleted(key, olderFormData, dataUuid, formUuid);
                                    } else {
                                        // 变更
                                        _self.markChanged(key, dataUuid, formUuid);
                                    }
                                }
                            } else {
                                // 字段属性为新增
                                _self.markAdded(key, dataUuid, formUuid);
                            }
                        }
                    } else {
                        // 表单数据为新增
                        for ( var key in formData) {
                            // 新增
                            _self.markAdded(key, dataUuid, formUuid, false);
                        }
                        // 从表数据新增
                        // if (!_self.isMainForm(formUuid)) {
                        _self.addChangedSubformRecord(formUuid, dataUuid, true, false, false, []);
                        // }
                    }
                });
            });
            // 从表行删除
            if (olderFormDatas != null) {
                $.each(olderFormDatas, function(formUuid, formDatas) {
                    // 遍历数据列表
                    $.each(formDatas, function(i, formData) {
                        if (newerDataUuidMap[formData.uuid] == null) {
                            _self.markDeleted(null, formData, formData.uuid, formUuid);
                        }
                    });
                });
            }
        },
        // 标记新增
        markAdded : function(fieldName, dataUuid, formUuid, addChangedSubformRecord) {
            this.markFieldColor(fieldName, addedColor, dataUuid, formUuid, addChangedSubformRecord);
        },
        // 标记变更
        markChanged : function(fieldName, dataUuid, formUuid) {
            this.markFieldColor(fieldName, changedColor, dataUuid, formUuid);
        },
        // 标记删除
        markDeleted : function(fieldName, olderFormData, dataUuid, formUuid) {
            var _self = this;
            var dyform = _self.getDyform();
            if (_self.isMainForm(formUuid)) {
                // 主表
                var field = dyform.getField(fieldName);
                if (field != null) {
                    // 添加变更的主表字段信息
                    _self.addChangedMainField(field);
                    field.setValue(olderFormData[fieldName]);
                    var $ctlEl = $(field.getCtlElement());
                    if ($ctlEl.closest("td").length > 0) {
                        $ctlEl.closest("td").find(".labelclass, .downloadAll").css({
                            "background-color" : deletedColor,
                            "text-decoration" : "line-through"
                        });
                    } else {
                        $ctlEl.parent().find(".labelclass, .downloadAll").css({
                            "background-color" : deletedColor,
                            "text-decoration" : "line-through"
                        });
                    }
                }
            } else {
                var subform = dyform.getSubform(dyform.getFormId(formUuid));
                var rowDataUuid = subform.addRowData(olderFormData);
                for ( var fieldName in olderFormData) {
                    $("*[name='" + rowDataUuid + "___" + fieldName + "']").closest("td").css({
                        "background-color" : deletedColor,
                        "text-decoration" : "line-through"
                    });
                }
                // 添加删除的从表信息
                _self.addChangedSubformRecord(formUuid, rowDataUuid, false, true, false, []);
            }
        },
        markFieldColor : function(fieldName, color, dataUuid, formUuid, addChangedSubformRecord) {
            var _self = this;
            var dyform = _self.getDyform();
            if (_self.isMainForm(formUuid)) {
                // 主表
                var field = dyform.getField(fieldName);
                if (field != null) {
                    // 添加变更的主表字段信息
                    _self.addChangedMainField(field);
                    var $ctlEl = $(field.getCtlElement());
                    $ctlEl.css({
                        "background-color" : color
                    });
                    if ($ctlEl.closest("td").length > 0) {
                        $ctlEl.closest("td").find(".labelclass, .downloadAll").css({
                            "background-color" : color
                        });
                    } else {
                        $ctlEl.parent().find(".labelclass, .downloadAll").css({
                            "background-color" : color
                        });
                    }
                }
            } else {
                // 从表
                $("*[name='" + dataUuid + "___" + fieldName + "']").closest("td").css({
                    "background-color" : color
                });
                // 添加从表变更信息
                if (addChangedSubformRecord !== false) {
                    var subform = dyform.getSubform(dyform.getFormId(formUuid));
                    var subformField = dyform.getField(fieldName, dataUuid);
                    if (subformField != null) {
                        var fileName = subformField.getDisplayName();
                        var subformConfig = subform.getSubformConfig();
                        if (subformConfig && subformConfig.fields && subformConfig.fields[fieldName]) {
                            fileName = subformConfig.fields[fieldName].displayName;
                        }
                        _self.addChangedSubformRecord(formUuid, dataUuid, false, false, true, [ fileName ]);
                    }
                }
            }
        },
        isMainForm : function(formUuid) {
            return this.getDyform().getFormUuid() == formUuid;
        },
        getOlderFormData : function(formUuid, dataUuid, olderFormDatas) {
            var formDatas = olderFormDatas[formUuid];
            if (formDatas == null) {
                return null;
            }
            for (var i = 0; i < formDatas.length; i++) {
                if (formDatas[i].uuid == dataUuid) {
                    return formDatas[i];
                }
            }
            return null;
        },
        // 添加变更的主表字段
        addChangedMainField : function(field) {
            var _self = this;
            var changedMainFields = _self.changedMainFields || [];
            changedMainFields.push(field.getCtlName());
            _self.changedMainFields = changedMainFields;
        },
        // 添加变更的从表记录
        addChangedSubformRecord : function(subformUuid, rowId, isAdded, isDeleted, isUpdated, updateFields) {
            var _self = this;
            var changedSubformRecords = _self.changedSubformRecords || {};
            if (changedSubformRecords[subformUuid] == null) {
                changedSubformRecords[subformUuid] = [];
            }
            // 获取已存在的字段更新信息
            var getChangedSubformRecord = function(records, rowId) {
                for (var i = 0; i < records.length; i++) {
                    if (records[i].rowId == rowId) {
                        return records[i];
                    }
                }
                return null;
            }
            // 合并字段更新信息
            var changedRecord = getChangedSubformRecord(changedSubformRecords[subformUuid], rowId);
            if (isUpdated && changedRecord != null) {
                changedRecord.updateFields = changedRecord.updateFields.concat(updateFields);
            } else {
                var changedRecordInfo = {
                    rowId : rowId,
                    isAdded : isAdded,
                    isDeleted : isDeleted,
                    isUpdated : isUpdated,
                    updateFields : updateFields
                };
                changedSubformRecords[subformUuid].push(changedRecordInfo);
            }
            _self.changedSubformRecords = changedSubformRecords;
        }
    };
    // 2、查看快照数据模块
    var viewSnapshotModule = {
        // 准备初始化表单
        prepareInitDyform : function(dyformOptions) {
            var _self = this;
            var snId = Browser.getQueryString("snId");
            var snapshotData = null;
            JDS.restfulPost({
	            url : ctx + "/api/workflow/work/getFlowDataSnapshotByIds",
                data : {snapshotIds: [ snId ]},
                async : false,
                success : function(result) {
                    snapshotData = JSON.parse(result.data[0]);
                }
            });
            if (snapshotData && snapshotData.dyFormData) {
                dyformOptions.formData = snapshotData.dyFormData;
            }
            dyformOptions.displayAsLabel = true;
        }
    };

    // 比较快照版本
    if (snAction == "1") {
        return $.extend(compareSnapshotsModule, baseModule);
    } else {
        // 查看快照数据
        return $.extend(viewSnapshotModule, baseModule);
    }
});