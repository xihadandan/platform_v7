define([
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'cookie',
  'formBuilder',
  'AppPtMgrListViewWidgetDevelopment',
  'AppPtMgrCommons'
], function (constant, commons, server, appContext, appModal, cookie, formBuilder, AppPtMgrListViewWidgetDevelopment, AppPtMgrCommons) {
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;
  var UUID = commons.UUID;
  var Validation = server.Validation;

  // 平台管理_产品集成_页面列表_视图组件二开
  var AppPageDefinitionListViewWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppPageDefinitionListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 组件准备
    prepare: function () {
      var _self = this;
      var widget = _self.getWidget();
      // 获取传给组件的参数
      var params = _self.getWidgetParams();
      var otherConditions = [];
      // 产品信息信息UUID
      if (StringUtils.isNotBlank(params.appPiUuid)) {
        var condition = {
          columnIndex: 'appPiUuid',
          value: params.appPiUuid,
          type: 'eq'
        };
        otherConditions.push(condition);
      }
      widget.addOtherConditions(otherConditions);
      // 触发列表准备加载事件
      _self.trigger('AppListView.prepare', params);
    },
    lineEnderButtonHtmlFormat: function (format, row, index) {
      var $html = $(format.before);
      var systemId = this._systemId();
      var userId = this._getUserId();
      if (!systemId || userId == 'U0000000000') {
        $html.find('.btn_class_btn_user_manage').remove();
        format.after = $html[0].outerHTML;
      }
    },
    _getUserId: function () {
      return $.cookie('cookie.current.userId');
    },
    _systemId: function () {
      return this.getWidgetParams().systemId;
    },
    onPostBody: function (dataList) {
      console.log(1);
      var _self = this;
      var widget = _self.getWidget();
      var $element = $(widget.element);
      // _self.maxVersionPageUuid = null;
      $.each(dataList, function (i, data) {
        if (data.isRef == 1) {
          $element
            .find("tr[data-index='" + i + "']")
            .find('button.btn_class_btn_del')
            .hide();
        } else {
          $element
            .find("tr[data-index='" + i + "']")
            .find('button.btn_class_btn_cancel_ref')
            .hide();
        }
        // if (_self._systemId() && data.maxVersionPageUuid && data.isDefault == 1) {
        //   _self.maxVersionPageUuid = data.maxVersionPageUuid;
        // }
        // if (_self.maxVersionPageUuid == data.uuid) {
        //   dataList.splice(i, 1);
        //   dataList.unshift(data);
        // }
      });
      $(_self.widget.element).prepend("<input type='hidden' name='unitId' id='unitId'><input type='hidden' name='unitName' id='unitName'>");
      if (!_self._systemId() || _self._getUserId() == 'U0000000000') {
        $(_self.widget.element).find('.table-handle').width(110);
        $(_self.widget.element).find('.table-handle .th-inner').css('width', '');
      } else {
        $(_self.widget.element).find('.table-handle').width(220);
        $(_self.widget.element).find('.table-handle .th-inner').css('width', '');
      }
    },
    onLoadSuccess: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $element = $(widget.element);
      var dataList = _self.getData();
      $.each(dataList, function (i, data) {
        var $width = 45;
        if (data.isRef == 1) {
          $width += 25;
          JDS.call({
            service: 'appPageDefinitionManager.getRefPageInfo',
            data: [data.uuid],
            version: '',
            async: false,
            success: function (result) {
              var title = result.data.piPathName || '';
              if (StringUtils.isNotBlank(title)) {
                title = '引自：' + title;
              }
              $element
                .find("tr[data-index='" + i + "']")
                .find('a.glyphicon-link')
                .attr('title', title);
            }
          });
        }
        if (!_self._systemId()) {
          $element.find('.isMobilePage').hide();
          var pcIndex = $element.find("[data-field='isPc']").index();
          $element.find("[data-field='isPc']").hide();
          $element
            .find('tr')
            .find('td:eq(' + pcIndex + ')')
            .hide();
        } else {
          if (data.wtype == 'wMobilePage') {
            $width += 25;
          }
        }
        $element
          .find("tr[data-index='" + i + "']")
          .find('.page-title')
          .css({
            'max-width': 'calc(100% - ' + $width + 'px)'
          });
        // if (_self.maxVersionPageUuid && _self.maxVersionPageUuid == data.uuid) {
        //   $element
        //     .find("tr[data-index='" + i + "']")
        //     .find('.tree-icon')
        //     .trigger('click');
        // }
        // if (data.isDefault == '1') {
        //   $element.find('.isDefaultTags').parent().css({
        //     position: 'relative'
        //   });
        // }
      });
    },
    // 新增
    btn_add: function () {
      var _self = this;
      var widget = _self.getWidget();
      // 触发应用列表行点击新增事件
      widget.trigger('AppPageDefinitionListView.addRow', {
        ui: _self,
        rowNum: _self.widget.getData().length
      });
    },
    // 复制页面
    btn_copy: function () {
      var _self = this;
      var params = _self.getWidgetParams();
      _self.showCopyPageDialog(params.appPiUuid);
    },
    showCopyPageDialog: function (appPiUuid) {
      var _self = this;
      var dlgId = UUID.createUUID();
      var title = '复制页面';
      var message = "<div id='" + dlgId + "'></div>",
        $dialog;

      var dlgOptions = {
        title: title,
        message: message,
        size: 'middle',
        shown: function () {
          formBuilder.buildSelect2({
            select2: {
              serviceName: 'appPageDefinitionMgr',
              defaultBlank: true,
              remoteSearch: true
            },
            events: {
              change: function () {
                $('#copyPageId', $('#' + dlgId))
                  // .val(AppPtMgrCommons.idGenerator.generate(null, 'page_'))
                  .parents('.form-group')
                  .removeClass('hide');
              }
            },
            container: $('#' + dlgId),
            name: 'copyPageUuid',
            diaplay: 'copyPageName',
            label: '复制页面',
            labelColSpan: '3',
            labelClass: 'required',
            controlColSpan: '9'
          });

          formBuilder.buildInput({
            container: $('#' + dlgId),
            label: 'ID',
            name: 'copyPageId',
            value: AppPtMgrCommons.idGenerator.generate(null, 'page_'),
            labelColSpan: '3',
            labelClass: 'required',
            divClass: 'hide',
            controlColSpan: '9'
          });
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var copyPageUuid = $('#copyPageUuid', $('#' + dlgId)).val();
              if (StringUtils.isBlank(copyPageUuid)) {
                appModal.error('请选择页面！');
                return false;
              }
              var copyPageId = $('#copyPageId', $('#' + dlgId)).val();
              if (StringUtils.isBlank(copyPageId)) {
                appModal.error('请输入ID');
                return false;
              }
              $('#' + dlgId)
                .parents('.modal-body')
                .first()
                .next()
                .find('button')
                .attr('disabled', 'disabled');
              JDS.call({
                service: 'pageDefinitionService.copyPageDefinition',
                data: [appPiUuid, copyPageUuid, copyPageId],
                async: false,
                version: '',
                success: function (result) {
                  if (result && result.code !== 0) {
                    appModal.error('页面ID已经存在！');
                    $('#' + dlgId)
                      .parents('.modal-body')
                      .first()
                      .next()
                      .find('button')
                      .removeAttr('disabled');
                    return true;
                  }

                  appModal.success('页面复制成功！');
                  _self.refresh();
                  $dialog.modal('hide');
                  return true;
                }
              });
              return false;
            }
          }
        }
      };
      $dialog = appModal.dialog(dlgOptions);
    },
    // 引用页面
    btn_ref: function () {
      var _self = this;
      var params = _self.getWidgetParams();
      _self.showRefPageDialog(params.appPiUuid);
    },
    showRefPageDialog: function (piUuid) {
      var _self = this;
      var dlgId = UUID.createUUID();
      var title = '引用页面';
      var message = "<div id='" + dlgId + "'></div>";
      var dlgOptions = {
        title: title,
        message: message,
        size: 'middle',
        shown: function () {
          var excludeUuids = $.map(_self.getData(), function (data) {
            return data.uuid;
          });
          formBuilder.buildSelect2({
            select2: {
              serviceName: 'appPageDefinitionMgr',
              defaultBlank: true,
              remoteSearch: true,
              params: {
                excludeUuids: excludeUuids.join(';')
              }
            },
            container: $('#' + dlgId),
            name: 'refPageUuid',
            diaplay: 'refPageName',
            label: '引用页面',
            labelColSpan: '3',
            labelClass: 'required',
            controlColSpan: '9'
          });
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var refPageUuid = $('#refPageUuid', $('#' + dlgId)).val();
              if (StringUtils.isBlank(refPageUuid)) {
                appModal.error('请选择页面！');
                return false;
              }
              JDS.call({
                service: 'appPageDefinitionManager.refPageDefinition',
                data: [piUuid, refPageUuid],
                async: false,
                version: '',
                success: function (result) {
                  appModal.success('页面引用成功！');
                  _self.refresh();
                }
              });
              return true;
            }
          }
        }
      };
      appModal.dialog(dlgOptions);
    },
    // 使用者管理
    btn_user_manage: function (e) {
      var _self = this;
      var index = $(e.target).parents('tr').data('index');
      var data = this.getData()[index];
      var title = data.name + '(' + data.version + ')';
      $('#unitId').val('');
      $('#unitName').val('');
      JDS.restfulGet({
        url: ctx + '/proxy/api/page/definition/manager/getEleIds',
        data: {
          appPiUuid: data.appPiUuid,
          uuid: data.uuid
        },
        contentType: 'application/x-www-form-urlencoded',
        success: function (result) {
          if (result.code == 0) {
            if (result.data.length > 0) {
              var id = [];
              var name = [];
              var datas = result.data;
              $.each(datas, function (index, item) {
                id.push(item.id);
                name.push(item.name);
              });
              $('#unitId').val(id.join(';'));
              $('#unitName').val(name.join(';'));
            }
            $.unit2.open({
              labelField: 'unitName',
              valueField: 'unitId',
              title: title + ' - 使用者管理',
              type: 'MyUnit;Role',
              multiple: true,
              selectTypes: 'all',
              valueFormat: 'justId',
              callback: function (ids, names, treeNode) {
                var unitId = $('#unitId').val().split(';');

                JDS.restfulPost({
                  url: ctx + '/proxy/api/page/definition/manager/saveEleIds',
                  data: {
                    appPiUuid: data.appPiUuid,
                    uuid: data.uuid,
                    eleIds: unitId.join(',')
                  },
                  contentType: 'application/x-www-form-urlencoded',
                  success: function (result) {
                    if (result.code == 0) {
                    } else {
                      parent.appModal.dialogError(result.msg);
                    }
                  }
                });
              }
            });
          } else {
            parent.appModal.dialogError(result.msg);
          }
        }
      });
    },
    // 删除
    btn_del: function (e, options, rowData) {
      var _self = this;
      var name = rowData.name;
      // if (rowData.isDefault) {
      //   appModal.confirm(
      //     '确定要删除系统默认工作台[' +
      //       rowData.name +
      //       '(' +
      //       rowData.version +
      //       ')' +
      //       ']吗？删除后，列表第1个工作台，将被标记为系统默认工作台，您可以自行修改',
      //     function (result) {
      //       if (result) {
      //         JDS.call({
      //           service: 'appPageDefinitionManager.remove',
      //           data: rowData.uuid,
      //           success: function (result) {
      //             appModal.success('删除成功！');
      //             _self.trigger('AppPageDefinitionListView.deleteRow', {
      //               rowData: rowData
      //             });
      //             // 删除成功刷新列表
      //             _self.refresh();
      //           }
      //         });
      //       }
      //     }
      //   );
      //   return;
      // }
      appModal.confirm('确定要删除页面[' + name + ']吗？', function (result) {
        if (result) {
          // JDS.call({
          //   service: 'appPageDefinitionManager.remove',
          //   data: rowData.uuid,
          //   success: function (result) {
          server.JDS.restfulRequest({
            url: `/proxy/api/app/pagemanager/deletePage/${rowData.uuid}`,
            type: 'DELETE',
            success: function (result) {
              appModal.success('删除成功！');
              _self.trigger('AppPageDefinitionListView.deleteRow', {
                rowData: rowData
              });
              // 删除成功刷新列表
              _self.refresh();
            }
          });
        }
      });
    },
    // 取消引用
    btn_cancel_ref: function (e, options, rowData) {
      var _self = this;
      var name = rowData.name;
      appModal.confirm('确定要取消页面[' + name + ']的引用吗？', function (result) {
        if (result) {
          JDS.call({
            service: 'appPageDefinitionManager.cancelRef',
            data: [rowData.uuid, rowData.appPiUuid],
            success: function (result) {
              appModal.success('取消成功！');
              _self.trigger('AppPageDefinitionListView.deleteRow', {
                rowData: rowData
              });
              // 删除成功刷新列表
              _self.refresh();
            }
          });
        }
      });
    },
    // 行点击查看应用详情详情
    onClickRow: function (rowNum, rowData, $element, field) {
      var _self = this;
      var widget = _self.getWidget();
      // 触发应用列表行点击事件
      widget.trigger('AppPageDefinitionListView.clickRow', {
        rowData: rowData
      });
    },
    // 定义导出
    btn_export: function () {
      var _self = this;
      if (!_self.checkSelection(true)) {
        return;
      }
      var uuid = _self.getSelectionUuids().join(';');
      var type = 'appPageDefinition';
      $.iexportData['export']({
        uuid: uuid,
        type: type
      });
    },
    // 定义导入
    btn_import: function () {
      var _self = this;
      $.iexportData['import']({
        callback: function () {
          _self.refresh();
        }
      });
    },
    // 定义导入日志
    btn_imp_log: function () {
      $.iexportData.viewImportLog({});
    },
    // 查看依赖
    btn_view_vependences: function () {
      var _self = this;
      if (!_self.checkSelection(true)) {
        return;
      }
      var uuid = _self.getSelectionUuids().join(';');
      var type = 'appPageDefinition';
      $.iexportData.viewDependences({
        uuid: uuid,
        type: type
      });
    },
    afterRender: function (options, configuration) {
      var _self = this;
      this.widget.on('RefreshView', function () {
        _self.refresh();
      });
    }
  });
  return AppPageDefinitionListViewWidgetDevelopment;
});
