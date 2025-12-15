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

  // 平台管理_产品集成_模块_打印模板列表_视图组件二开
  var AppPrintTptListViewWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppPrintTptListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 组件准备
    prepare: function () {},

    beforeRender: function (options, configuration) {
      // 归属模块ID
      this.widget.addParam('moduleId', this._moduleId());
    },

    afterRender: function (options, configuration) {
      var _self = this;
      if (!this._moduleId()) {
        this.widget.element.find('.li_class_btn_reference').hide();
        this.widget.element.find('.li_class_btn_cancel_ref').hide();
      }
      this.widget.on('AppPrintTemplateListView.refresh', function () {
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
          targetTabName: '打印模板'
        });
      }
      return this.getWidget().refresh(this.options);
    },

    onLoadSuccess: function () {
      var rows = this.getData();
      var $tableElement = this.widget.$tableElement;
      for (var i = 0, len = rows.length; i < len; i++) {
        $tableElement
          .find('tr[data-index=' + i + ']')
          .find('td:eq(1)')
          .html(rows[i].name + '(' + Number(rows[i].version).toFixed(1) + ')');
        if (rows[i].isRef === 1) {
          var $nameTd = $tableElement.find('tr[data-index=' + i + ']').find('td:eq(1)');
          (function ($td, moduleId) {
            server.JDS.call({
              service: 'appModuleMgr.getModuleDetail',
              data: [moduleId],
              version: '',
              success: function (result) {
                if (result.success && result.data) {
                  $td.append(
                    $('<a>', {
                      class: 'glyphicon glyphicon-link',
                      style: 'color:#5cce66;margin-left:3px;transform: rotate(50deg);text-decoration: none;',
                      title: '引自：' + (result.data.appSystemBean ? result.data.appSystemBean.name + '/' : '') + result.data.name
                    })
                  );
                }
              },
              error: function (jqXHR) {},
              async: true
            });
          })($nameTd, rows[i].moduleId);
        }
      }
    },

    onResetQueryFields: function () {},

    definition_import: function () {
      this.onImport();
    },

    definition_export: function () {
      this.onExport('printTemplate');
    },

    refrencePrintTemplateDialogHtml: function () {
      var $div = $('<div>', { class: 'container-fluid' });
      var $row1 = $('<div>', { class: 'row form-group' }).append(
        $('<div>', { class: 'col-sm-2' }).text('所属模块'),
        $('<div>', { class: 'col-sm-10' }).append(
          $('<input>', {
            type: 'hidden',
            id: 'moduleBelong',
            style: 'width:400px;'
          })
        )
      );
      var $row2 = $('<div>', { class: 'row form-group' }).append(
        $('<div>', { class: 'col-sm-2 required' }).text('引用打印模板'),
        $('<div>', { class: 'col-sm-10' }).append(
          $('<input>', {
            type: 'hidden',
            id: 'modulePrintTemplateInput',
            style: 'width:400px;'
          })
        )
      );

      $div.append($row1, $row2);
      return $div[0].outerHTML;
    },

    _moduleId: function () {
      return this.getWidgetParams().moduleId;
    },

    //引用其他模块的打印模板
    btn_reference: function () {
      //弹窗
      var _self = this;
      var $dialog;
      var dialogOpts = {
        title: '引用打印模板',
        message: _self.refrencePrintTemplateDialogHtml(),
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function (result) {
              var commitResult = false;
              var moduleDsUuid = $('#modulePrintTemplateInput').val();
              if (!moduleDsUuid) {
                return false;
              }
              server.JDS.call({
                service: 'moduleFunctionConfigRefFacadeService.saveModuleFunctionConfigRef',
                data: [
                  [
                    {
                      refUuid: moduleDsUuid,
                      moduleId: _self._moduleId(),
                      entityClass: 'CdPrintTemplateRefEntity'
                    }
                  ]
                ],
                version: '',
                success: function (result) {
                  if (result.success) {
                    commitResult = true;
                    appModal.info('引用打印模板成功');
                    _self.refresh();
                  }
                },
                error: function (jqXHR) {
                  appModal.alert('引用失败');
                },
                async: false
              });
              return commitResult;
            }
          },

          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function (result) {}
          }
        },
        shown: function () {
          var $moduleSelect = $('#moduleBelong', $dialog);
          $moduleSelect.wSelect2({
            valueField: 'moduleBelong',
            remoteSearch: false,
            serviceName: 'appModuleMgr',
            queryMethod: 'loadSelectData',
            defaultBlankText: '全部',
            defaultBlankValue: '-1',
            params: {
              //systemUnitId:server.SpringSecurityUtils.getCurrentUserUnitId()
              excludeIds: _self._moduleId()
            }
          });

          $moduleSelect
            .on('change', function () {
              var moduleId = $('#moduleBelong').val();
              if (!moduleId) {
                moduleId = -100;
              }
              if (moduleId == -1) {
                moduleId = '';
              }
              $('#modulePrintTemplateInput')
                .val('')
                .wSelect2({
                  valueField: 'modulePrintTemplateInput',
                  remoteSearch: false,
                  serviceName: 'printTemplateFacadeService',
                  queryMethod: 'loadPrintTemplateSelectionByModule',
                  params: {
                    moduleId: moduleId,
                    idProperty: 'uuid',
                    excludeModuleIds: _self._moduleId()
                  }
                });
            })
            .trigger('change');
        },
        size: 'middle'
      };
      $dialog = appModal.dialog(dialogOpts);
    },

    // 删除
    btn_delete: function () {
      var _self = this;
      var rowData = _self.getSelectRowData();
      if (rowData.length > 0) {
        var name = rowData[0].name;
        appModal.confirm('确认要删除打印模板吗?', function (result) {
          if (result) {
            server.JDS.call({
              service: 'printTemplateService.deleteByUuids',
              version: '',
              data: [
                (function () {
                  var uuids = [];
                  for (var i = 0, len = rowData.length; i < len; i++) {
                    uuids.push(rowData[i].uuid);
                  }
                  return uuids;
                })()
              ],
              success: function (result) {
                appModal.info('刪除成功');
                _self.refresh(); //刷新表格
              },
              error: function (jqXHR) {
                var faultData = JSON.parse(jqXHR.responseText);
                appModal.alert(faultData.msg);
              }
            });
          }
        });
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
        if (!_self.checkSelection()) {
          return [];
        }
        rowData = _self.getSelections();
      }
      return rowData;
    },

    btn_add: function () {
      // 触发打印模板列表行点击新增事件
      this.widget.trigger('AppPrintTemplateListView.editRow', {
        ui: this.widget
      });
    },

    // 行点击查看详情
    onClickRow: function (rowNum, rowData, $element, field) {
      // 触发打印模板行点击事件
      this.widget.trigger('AppPrintTemplateListView.editRow', {
        rowData: rowData,
        ui: this.widget
      });
    },

    //取消引用
    btn_cancel_ref: function () {
      var _self = this;
      var rowData = _self.getSelectRowData();
      if (rowData.length == 0) {
        return;
      }
      appModal.confirm('确认取消引用打印模板吗?', function (result) {
        if (result) {
          server.JDS.call({
            service: 'moduleFunctionConfigRefFacadeService.deleteModuleFunctionConfigRef',
            version: '',
            data: [
              (function () {
                var cancelObj = [];
                for (var i = 0, len = rowData.length; i < len; i++) {
                  cancelObj.push({
                    refUuid: rowData[i].uuid,
                    moduleId: _self._moduleId(),
                    entityClass: 'CdPrintTemplateRefEntity'
                  });
                }
                return cancelObj;
              })()
            ],
            success: function (result) {
              appModal.info('取消引用成功');
              _self.refresh(); //刷新表格
            },
            error: function (jqXHR) {
              var faultData = JSON.parse(jqXHR.responseText);
              appModal.alert(faultData.msg);
            }
          });
        }
      });
    }
  });
  return AppPrintTptListViewWidgetDevelopment;
});
