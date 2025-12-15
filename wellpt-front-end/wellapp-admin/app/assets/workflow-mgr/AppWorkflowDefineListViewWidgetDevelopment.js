define([
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'AppPtMgrListViewWidgetDevelopment',
  'AppPtMgrCommons',
  'formBuilder'
], function (constant, commons, server, appContext, appModal, AppPtMgrListViewWidgetDevelopment, AppPtMgrCommons, formBuilder) {
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;

  // 平台管理_产品集成_模块_流程定义列表_视图组件二开
  var AppWorkflowDefineListViewWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppWorkflowDefineListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 组件准备
    prepare: function () {},

    beforeRender: function (options, configuration) {
      // 归属模块ID
      this.widget.addParam('moduleId', this._moduleId());
      if (!this._moduleId()) {
        configuration.buttons.splice(1, 1); // 产品之外的流程定义去掉引入流程按钮
      }
    },

    afterRender: function (options, configuration) {
      var _self = this;
      var $element = $(_self.widget.element);
      var $searchInput = $element.find("input[type='search']");
      var $select = $('<select>', {
        style: 'width:100px;'
      });
      $select.append(
        $('<option>', {
          value: ''
        }).html('全部'),
        $('<option>', {
          value: '1'
        }).text('启用'),
        $('<option>', {
          value: '0'
        }).text('禁用')
      );
      var $div = $('<div>', {
        class: 'pull-right search'
      }).append($select);
      $div.insertAfter($searchInput.parent());

      $select.on('change', function () {
        var val = $(this).val();
        _self.widget.getDataProvider().setKeyword({});
        //清除类型查询
        if (_self.lastEanbledSelectConditions) {
          _self.widget.clearOtherConditions(_self.lastEanbledSelectConditions);
        }
        if (val) {
          var enabledCondition = {
            columnIndex: 'enabled',
            value: val,
            type: 'eq'
          };
          _self.widget.addOtherConditions([enabledCondition]);
          _self.lastEanbledSelectConditions = enabledCondition;
        }
      });

      _self.$enabledSelect = $select;

      this.widget.on('RefreshWorkflowDefineTable', function () {
        _self.refresh();
      });

      // 流程清单导出回调
      configuration.exportCallback = function () {
        $.ajax({
          type: 'get',
          url: ctx + '/api/log/manage/operation/saveFlowListExportLogManageOperation',
          dataType: 'json',
          success: function (res) {
            if (res.code == 0) {
            } else {
              appModal.error(res.msg);
            }
          }
        });
      };
    },

    onLoadSuccess: function () {
      var rows = this.getData();
      var $tableElement = this.widget.$tableElement;
      var thIndex = $tableElement.find("th[data-field='name']").index();
      for (var i = 0, len = rows.length; i < len; i++) {
        var version = rows[i].version;
        var name = rows[i].name + '(' + (version % 1 === 0 ? version + '.0' : version) + ')';
        $tableElement
          .find('tr[data-index=' + i + ']')
          .find('td:eq(' + thIndex + ')')
          .html(name);
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

    onResetQueryFields: function () {
      this.$enabledSelect.find('option:eq(0)').prop('selected', true);
      this.$enabledSelect.trigger('change');
    },

    refrenceWorkflowDialogHtml: function () {
      var $div = $('<div>', {
        class: 'container-fluid'
      });
      var $row1 = $('<div>', {
        class: 'row form-group'
      }).append(
        $('<div>', {
          class: 'col-sm-2'
        }).text('所属模块'),
        $('<div>', {
          class: 'col-sm-10'
        }).append(
          $('<input>', {
            type: 'hidden',
            id: 'moduleBelong',
            style: 'width:400px;'
          })
        )
      );
      var $row2 = $('<div>', {
        class: 'row form-group'
      }).append(
        $('<div>', {
          class: 'col-sm-2 required'
        }).text('引用流程'),
        $('<div>', {
          class: 'col-sm-10'
        }).append(
          $('<input>', {
            type: 'hidden',
            id: 'moduleWorkflowInput',
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

    refresh: function () {
      //刷新徽章
      var tabpanel = this.widget.element.parents('.active');
      if (tabpanel.length > 0) {
        var id = tabpanel.attr('id');
        id = id.substring(0, id.indexOf('-'));
        $('#' + id).trigger(constant.WIDGET_EVENT.BadgeRefresh, {
          targetTabName: '流程定义'
        });
      }
      return this.getWidget().refresh(this.options);
    },

    //引用其他模块的流程
    reference_workflow: function () {
      //弹窗
      var _self = this;
      var $dialog;
      var dialogOpts = {
        title: '引用流程',
        message: _self.refrenceWorkflowDialogHtml(),
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function (result) {
              var commitResult = false;
              var moduleWfUuid = $('#moduleWorkflowInput').val();
              if (!moduleWfUuid) {
                return false;
              }
              server.JDS.call({
                service: 'moduleFunctionConfigRefFacadeService.saveModuleFunctionConfigRef',
                data: [
                  [
                    {
                      refUuid: moduleWfUuid,
                      moduleId: _self._moduleId(),
                      entityClass: 'FlowDefinitionRefEntity',
                      functionType: 'flowDefinition'
                    }
                  ]
                ],
                version: '',
                success: function (result) {
                  if (result.success) {
                    commitResult = true;
                    appModal.success('引用流程成功');
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
              $('#moduleWorkflowInput').wSelect2({
                valueField: 'moduleWorkflowInput',
                remoteSearch: false,
                serviceName: 'flowDefinitionService',
                queryMethod: 'loadSelectDataWorkflowDefinition',
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
    btn_delete_all: function () {
      var _self = this;
      var rowData = _self.getSelections();
      if (rowData.length > 0) {
        for (var i = 0; i < rowData.length; i++) {
          if (rowData[i].isRef === 1) {
            appModal.error('选中记录中存在引用流程，引用流程不能删除');
            // break;
            return false;
          }
        }
        _self.deleteMultiFlow(rowData);
      } else {
        appModal.error('请先选择记录！');
      }
    },

    btn_delete: function () {
      var _self = this;
      var rowData = _self.getSelectRowData();
      if (rowData.length > 0) {
        _self.deleteFlow(rowData);
      }
    },

    deleteFlow: function (rowData) {
      var _self = this;
      appModal.confirm('确认要删除流程吗?', function (result) {
        if (result) {
          $.ajax({
            url: '/api/workflow/definition/logical-delete',
            type: 'POST',
            dataType: 'json',
            data: {
              uuid: rowData[0].uuid
            },
            async: false,
            success: function (data) {
              if (data && data.code === 0) {
                appModal.success('刪除成功');
                _self.refresh(); //刷新表格
              } else {
                appModal.alert(data.msg);
              }
            },
            error: function (data) {
              appModal.alert(data.msg);
            }
          });
        }
      });
    },

    deleteMultiFlow: function (rowData) {
      var _self = this;
      var uuids = [];
      for (var i = 0; i < rowData.length; i++) {
        uuids.push(rowData[i].uuid);
      }

      appModal.confirm('确认要删除流程吗?', function (result) {
        if (result) {
          $.ajax({
            url: '/api/workflow/definition/logical-delete-all',
            type: 'POST',
            dataType: 'json',
            data: {
              uuids: uuids
            },
            async: false,
            success: function (data) {
              if (data && data.code === 0) {
                appModal.success('刪除成功');
                _self.refresh(); //刷新表格
              } else {
                appModal.alert(data.msg);
              }
            },
            error: function (data) {
              appModal.alert(data.msg);
            }
          });
        }
      });
    },

    btn_add_new: function () {
      var url = ctx + '/web/app/pt-mgr/pt-wf-mgr/pt-wf-designer.html';
      top.open(url, '_blank');
    },
    btn_edit_new: function (e, ui, rowData) {
      var url = ctx + '/web/app/pt-mgr/pt-wf-mgr/pt-wf-designer.html?id=' + rowData.uuid;
      top.open(url, '_blank');
    },
    btn_flow_simulation: function () {
      var _self = this;
      var selection = _self.getSelection();
      if (selection.length != 1) {
        appModal.error('请选择一条记录');
        return;
      }
      var flowDefId = selection[0].id;
      appContext.require(['app-workflow-start-flow-simulation'], function (app) {
        new app({
          ui: _self,
          flowDefId: flowDefId
        });
      });
    },
    // 复制
    btn_copy: function () {
      var _self = this;
      var selection = _self.getSelectRowData();
      if (selection.length != 1) {
        appModal.error('请选择一条记录！');
        return;
      }
      var rowData = selection[0];
      // 验证输入
      var checkInputValues = function () {
        var $container = $('.dlg_copy_definition');
        var newFlowDefName = $.trim($("input[name='new_flow_def_name']", $container).val());
        var newFlowDefId = $.trim($("input[name='new_flow_def_id']", $container).val());
        if (StringUtils.isBlank(newFlowDefName)) {
          appModal.error('流程名称不能为空！');
          return false;
        }
        if (StringUtils.isBlank(newFlowDefId)) {
          appModal.error('流程ID不能为空！');
          return false;
        }
        return true;
      };
      // 复制处理
      var copyFlowDefinition = function (successCallback) {
        var $container = $('.dlg_copy_definition');
        var newFlowDefName = $.trim($("input[name='new_flow_def_name']", $container).val());
        var newFlowDefId = $.trim($("input[name='new_flow_def_id']", $container).val());
        var copyResult = false;
        JDS.call({
          service: 'flowSchemeService.copy',
          data: [rowData.uuid, newFlowDefName, newFlowDefId],
          async: false,
          success: function () {
            if ($.isFunction(successCallback)) {
              successCallback.apply(this, arguments);
            } else {
              appModal.success('复制成功！');
            }
            copyResult = true;
            _self.refresh();
          }
        });
        return copyResult;
      };
      appModal.dialog({
        title: '复制流程定义',
        message: "<div class='dlg_copy_definition'></div>",
        shown: function () {
          var $container = $('.dlg_copy_definition');
          formBuilder.buildLabel({
            container: $container,
            labelColSpan: '12',
            label: '请输入新流程的名称和ID：'
          });
          formBuilder.buildInput({
            container: $container,
            labelColSpan: '3',
            controlColSpan: '9',
            isRequired: true,
            label: '流程名称',
            name: 'new_flow_def_name',
            value: rowData.name + ' - 副本'
          });
          formBuilder.buildInput({
            container: $container,
            labelColSpan: '3',
            controlColSpan: '9',
            isRequired: true,
            label: '流程ID',
            name: 'new_flow_def_id',
            value: rowData.id
          });
        },
        buttons: {
          confirm: {
            label: '保存',
            className: 'well-btn w-btn-primary',
            callback: function () {
              if (!checkInputValues()) {
                return false;
              }
              return copyFlowDefinition();
            }
          },
          confirmAndEdit: {
            label: '保存并编辑',
            className: 'well-btn w-btn-primary',
            callback: function () {
              if (!checkInputValues()) {
                return false;
              }
              return copyFlowDefinition(function (result) {
                appModal.success('复制成功！', function () {
                  _self.btn_edit_new(null, null, {
                    uuid: result.data
                  });
                });
              });
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {}
          }
        }
      });
    },
    onDblClickRow: function (rowNum, row, $element, field) {
      var windowOptions = {};
      windowOptions.url = ctx + '/web/app/pt-mgr/pt-wf-mgr/pt-wf-designer.html?id=' + row.uuid + '&isRef=' + row.isRef;
      windowOptions.target = '_blank';
      appContext.openWindow(windowOptions);
    },

    getSelectRowData: function () {
      var _self = this;
      var rowData = [];
      var $toolbarDiv = $(event.target).closest('div.div_lineEnd_toolbar');
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

    btn_definition_import: function () {
      var _self = this;
      // 定义导入
      $.iexportData['import']({
        callback: function () {
          _self.refresh();
        }
      });
    },

    btn_export: function () {
      // 定义导出
      this.onExport('flowDefinition');
    },
    btn_definition_export: function () {
      var rowData = this.getSelectRowData();
      $.iexportData['export']({
        uuid: rowData[0].uuid,
        type: 'flowDefinition'
      });
    },
    btn_check_dependence: function (e) {
      // 查看依赖
      var index = $(e.target).parents('tr').data('index');
      var rowData = this.getData()[index];
      var uuid = rowData.uuid;
      var type = 'flowDefinition';
      $.iexportData.viewDependences({
        uuid: uuid,
        type: type
      });
    },
    //格式化行级按钮
    lineEnderButtonHtmlFormat: function (format, row, index) {
      var $html = $(format.before);
      $html.find(row.isRef === 1 ? '.li_class_btn_delete' : '.li_class_btn_cancel_ref').remove();
      $html.find('.li_class_see_modify_log').remove();
      format.after = $html[0].outerHTML;
    },

    //取消引用
    btn_cancel_ref: function () {
      var _self = this;
      var rowData = _self.getSelectRowData();
      if (rowData.length == 0) {
        return;
      }
      appModal.confirm('确认取消引用流程定义吗?', function (result) {
        if (result) {
          server.JDS.call({
            service: 'moduleFunctionConfigRefFacadeService.deleteModuleFunctionConfigRef',
            data: [
              [
                {
                  refUuid: rowData[0].uuid,
                  moduleId: _self._moduleId(),
                  entityClass: 'FlowDefinitionRefEntity'
                }
              ]
            ],
            version: '',
            success: function (result) {
              appModal.success('取消引用成功');
              _self.refresh(); //刷新表格
            },
            error: function (jqXHR) {
              var faultData = JSON.parse(jqXHR.responseText);
              appModal.alert(faultData.msg);
            }
          });
        }
      });
    },

    // 流程清单导出
    btn_flow_menu_export: function () {},

    see_modify_log: function () {
      //弹窗
      var _self = this;
      var $dialog;
      var rowData = _self.getSelectRowData();
      if (rowData.length == 0) {
        return;
      }
      var dialogOpts = {
        title: '查看修改日志',
        message: '<table id="table_log_info"></table>',
        shown: function () {
          var $logTable = $('#table_log_info', $dialog);
          $logTable.bootstrapTable('destroy').bootstrapTable({
            data: [],
            idField: 'uuid',
            pagination: true,
            striped: false,
            showColumns: false,
            width: 500,
            columns: [
              {
                field: 'uuid',
                title: 'UUID',
                visible: false
              },
              {
                field: 'xml',
                title: '比较XML',
                formatter: function (value, row, index) {
                  var $img = $('<img>', {
                    src: ctx + '/pt/workflow/images/search.png',
                    title: '比较XML',
                    class: 'compare-xml',
                    uuid: row.uuid
                  });
                  return $img[0].outerHTML;
                }
              },
              {
                field: 'time',
                title: '修改时间'
              },
              {
                field: 'version',
                title: '流程小版本号'
              },
              {
                field: 'content',
                title: '内容'
              }
            ]
          });

          server.JDS.call({
            service: 'identityReplaceService.getUpdateContentLog',
            data: [null, {}, rowData[0].uuid, ctx],
            version: '',
            success: function (result) {
              if (result.success && result.data) {
                var data = [];
                for (var i = 0, len = result.data.dataList.length; i < len; i++) {
                  var d = result.data.dataList[i];
                  data.push({
                    xml: d.cell[1],
                    time: d.cell[2],
                    version: d.cell[3],
                    content: d.cell[4],
                    uuid: d.cell[0]
                  });
                }
                $logTable.bootstrapTable('load', data);
              }
            },
            error: function (jqXHR) {},
            async: true
          });

          $('#table_log_info', $dialog).on('click', '.compare-xml', function () {
            _self._showDiffDialog($(this).attr('uuid'));
          });
        },
        size: 'large'
      };
      $dialog = appModal.dialog(dialogOpts);
    },

    _showDiffDialog: function (uuid) {
      // IdentityReplaceService
      server.JDS.call({
        service: 'identityReplaceService.compareXml',
        data: [uuid],
        version: '',
        success: function (res) {
          if (res.success && res.data) {
            var result = res.data;
            var $dialog = appModal.dialog({
              title: '查看修改日志',
              message:
                '<div id="diffContainer" class="container-fluid" style="max-height: 500px;overflow-y: auto;">' +
                '<label>差异XML</label><div id="diffXml" class="row"></div>' +
                '<label>流程XML</label><textarea id="newXml" class="form-control" rows="3"></textarea>' +
                '<label>小版本XML</label><textarea id="oldXml" class="form-control" rows="3"></textarea></div>',
              shown: function () {
                var xml = result.split('小版本XML:');
                var diff = xml[1].split('差异XML：');
                var diffCode = diff[1].split(',');
                var diffHtml = '';
                for (var i = 0; i < diffCode.length; i++) {
                  diffHtml += diffCode[i] + '<br/>';
                }
                $('#diffXml', $dialog).html(diffHtml);
                //主方法，运用递归实现
                $('#newXml', $dialog).html(xml[0].replace('流程XML:', '').replace(new RegExp(/(><)/g), '>\r\n<'));
                $('#oldXml', $dialog).html(diff[0].replace(new RegExp(/(><)/g), '>\r\n<'));
              },
              size: 'large'
            });
          }
        },
        error: function (jqXHR) {},
        async: true
      });
    }
  });

  return AppWorkflowDefineListViewWidgetDevelopment;
});
