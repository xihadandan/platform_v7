define(['constant', 'commons', 'server', 'appContext', 'appModal', 'AppPtMgrListViewWidgetDevelopment', 'AppPtMgrCommons'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  AppPtMgrListViewWidgetDevelopment,
  AppPtMgrCommons
) {
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;

  // 平台管理_产品集成_流水号旧数据列表_记录数据_视图组件二开
  var AppSerialNumberRecordDataWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  //定义serializeObject方法，序列化表单
  $.fn.serializeJson = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
      if (o[this.name]) {
        if (!o[this.name].push) {
          o[this.name] = [o[this.name]];
        }
        o[this.name].push(this.value || '');
      } else {
        o[this.name] = this.value || '';
      }
    });
    return o;
  };

  // 接口方法
  commons.inherit(AppSerialNumberRecordDataWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 组件准备
    prepare: function () {},

    beforeRender: function (options, configuration) {
      // 归属模块ID
      this.widget.addParam('moduleId', this._moduleId());
    },

    _moduleId: function () {
      return this.getWidgetParams().moduleId;
    },

    // 删除
    btn_delete: function () {
      var _self = this;
      var rowData = _self.getSelectRowData();
      if (rowData.length > 0) {
        appModal.confirm('确认要删除记录数据吗?', function (result) {
          if (result) {
            server.JDS.call({
              service: 'serialNumberRecordService.deleteByUuids',
              version: '',
              data: [
                (function () {
                  var uuids = [];
                  for (var i = 0, len = rowData.length; i < len; i++) {
                    uuids.push(rowData[i].UUID);
                  }
                  return uuids;
                })()
              ],
              success: function (result) {
                appModal.success('刪除成功');
                _self.refresh(); //刷新表格
              },
              error: function (jqXHR) {
                var faultData = JSON.parse(jqXHR.responseText);
                appModal.alert(faultData.msg);
              }
            });
          }
        });
      } else {
        appModal.error('请选择记录！');
        return false;
      }
    },

    getSelectRowData: function () {
      var _self = this;
      var $toolbarDiv = $(event.target).closest('div');
      var rowData = [];
      if ($toolbarDiv.is('.div_lineEnd_toolbar')) {
        //行级点击操作
        var index = $toolbarDiv.attr('index');
        var allData = _self.getData();
        rowData = [allData[index]];
      } else {
        if (_self.getSelectionIndexes().length == 0) {
          return [];
        }
        rowData = _self.getSelections();
      }
      return rowData;
    },

    _getAttentinDialogHtml: function () {
      var $formHtml = $("<form id='sn_record'>");
      var $table = $("<table class='table table-border'>");

      $table.append(
        "<tr><td><font style='color: red'>*</font>字段名</td><td><input type='text' id='fieldName' name='fieldName' class='form-control'></td></tr>"
      );
      $table.append(
        "<tr><td><font style='color: red'>*</font>流水号定义Id</td><td><input type='text' id='snId' name='snId' class='form-control'></td></tr>"
      );
      $table.append(
        "<tr><td><font style='color: red'>*</font>表名</td><td><input type='text' id='objectName' name='objectName' class='form-control'><input type='hidden' id='objectType' name='objectType' value='1'></td></tr>"
      );
      $table.append(
        "<tr><td><font style='color: red'>*</font>流水号</td><td><input type='text' id='serialNo' name='serialNo' class='form-control'></td></tr>"
      );
      $table.append(
        "<tr><td><font style='color: red'>*</font>使用流水号的数据UUID</td><td><input type='text' id='dataUuid' name='dataUuid' class='form-control'></td></tr>"
      );
      $table.append(
        "<tr><td><font style='color: red'>*</font>指针</td><td><input type='text' id='pointer' name='pointer' class='form-control'></td></tr>"
      );
      $table.append("<tr><td>关键部分</td><td><input type='text' id='keyPart' name='keyPart' class='form-control'></td></tr>");
      $table.append("<tr><td>头部</td><td><input type='text' id='headPart' name='headPart' class='form-control'></td></tr>");
      $table.append("<tr><td>尾部</td><td><input type='text' id='lastPart' name='lastPart' class='form-control'></td></tr>");

      $formHtml.append($table);
      return $formHtml;
    },

    btn_add: function () {
      var _self = this;
      var $dialog = appModal.dialog({
        title: '添加记录数据',
        message: _self._getAttentinDialogHtml(),
        size: 'large',
        height: 600,
        shown: function (_$dialog) {},
        buttons: {
          ok: {
            label: '确定',
            className: 'btn btn-primary',
            callback: function () {
              $dialog.find('.error').remove();
              var fieldName = $dialog.find('#fieldName').val();
              var snId = $dialog.find('#snId').val();
              var objectName = $dialog.find('#objectName').val();
              var serialNo = $dialog.find('#serialNo').val();
              var dataUuid = $dialog.find('#dataUuid').val();
              var pointer = $dialog.find('#pointer').val();

              var validateFlag = true;
              if (commons.StringUtils.isBlank(fieldName)) {
                _self._showErr($dialog, 'fieldName', '字段名不能为空');
                validateFlag = false;
              }
              if (commons.StringUtils.isBlank(snId)) {
                _self._showErr($dialog, 'snId', '流水号定义Id不能为空');
                validateFlag = false;
              }
              if (commons.StringUtils.isBlank(objectName)) {
                _self._showErr($dialog, 'objectName', '表名不能为空');
                validateFlag = false;
              }
              if (commons.StringUtils.isBlank(serialNo)) {
                _self._showErr($dialog, 'serialNo', '流水号不能为空');
                validateFlag = false;
              }
              if (commons.StringUtils.isBlank(dataUuid)) {
                _self._showErr($dialog, 'dataUuid', '使用流水号的数据UUID不能为空');
                validateFlag = false;
              }
              if (commons.StringUtils.isBlank(pointer)) {
                _self._showErr($dialog, 'pointer', '指针不能为空');
                validateFlag = false;
              }
              if (validateFlag) {
                return _self._addRecordData($('#sn_record').serializeJson());
              } else {
                return false;
              }
            }
          },
          cancel: {
            label: '取消',
            className: 'btn btn-default'
          }
        }
      });
    },

    _addRecordData: function (jsonObj) {
      var _self = this;
      var flg = false;
      server.JDS.call({
        service: 'serialNumberFacadeService.addSerialNumberRecord',
        data: jsonObj,
        async: false,
        success: function (result) {
          if (result.success) {
            flg = true;
            appModal.success(result.msg, function () {
              _self.refresh();
            });
          } else {
            appModal.error(result.msg);
          }
        }
      });
      return flg;
    },

    _showErr: function ($dialog, eleId, errMsg) {
      var $td = $dialog.find('#' + eleId).parents('td');
      var $err = $td.find('.error');
      if ($err.length == 0) {
        $err = $("<div class='error' style='color:red'>");
        $err.html(errMsg);
        $td.append($err);
      } else {
        $err.html(errMsg);
      }
    },

    // 行点击查看详情
    onClickRow: function (rowNum, rowData, $element, field) {
      // 触发应用列表行点击事件
      this.widget.trigger('AppSerialNumberRecordData.editRow', {
        rowData: rowData,
        ui: this.widget
      });
    },

    afterRender: function (options, configuration) {
      var _self = this;
      this.widget.on('AppSerialNumberRecordData.refresh', function () {
        _self.refresh();
      });
    },

    refresh: function () {
      //刷新徽章
      var tabpanel = this.widget.element.parents('.active');
      if (tabpanel.length > 0) {
        var id = tabpanel.attr('id');
        id = id.substring(0, id.indexOf('-'));
        $('#' + id).trigger(constant.WIDGET_EVENT.BadgeRefresh, {
          targetTabName: '记录数据'
        });
      }
      return this.getWidget().refresh(this.options);
    }
  });
  return AppSerialNumberRecordDataWidgetDevelopment;
});
