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

  // 平台管理_产品集成_流水号旧数据列表_异常数据_视图组件二开
  var AppSerialNumberExceptionDataWidgetDevelopment = function () {
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
  commons.inherit(AppSerialNumberExceptionDataWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
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
        appModal.confirm('确认要删除异常数据吗?', function (result) {
          if (result) {
            server.JDS.call({
              service: 'serialNumberOldDataService.deleteByUuids',
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

    // 行点击查看详情
    onClickRow: function (rowNum, rowData, $element, field) {
      // 触发应用列表行点击事件
      this.widget.trigger('AppSerialNumberExceptionData.editRow', {
        rowData: rowData,
        ui: this.widget
      });
    },

    afterRender: function (options, configuration) {
      var _self = this;
      this.widget.on('AppSerialNumberExceptionData.refresh', function () {
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
          targetTabName: '异常数据'
        });
      }
      return this.getWidget().refresh(this.options);
    }
  });
  return AppSerialNumberExceptionDataWidgetDevelopment;
});
