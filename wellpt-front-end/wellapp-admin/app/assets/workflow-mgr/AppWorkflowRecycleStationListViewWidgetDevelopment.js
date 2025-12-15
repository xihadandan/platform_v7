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

  // 平台管理_产品集成_流程定义回收站_视图组件二开
  var AppWorkflowRecycleStationListViewWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppWorkflowRecycleStationListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 组件准备
    prepare: function () {},

    beforeRender: function (options, configuration) {},

    afterRender: function (options, configuration) {
      var _self = this;
      var $element = $(_self.widget.element);

      this.widget.on('RefreshWorkflowDefineTable', function () {
        _self.refresh();
      });

      server.JDS.call({
        service: 'wfFlowDefinitionCleanupConfigFacadeService.getDto',
        version: '',
        async: false,
        validate: false,
        success: function (result) {
          var retentionDays = result.data.retentionDays;
          var $alert = $('<div id="cleanUpAlert" class="alert alert-warning" style="padding: 8px 15px; margin-bottom: 15px;"></div>');
          $alert.append(
            '<button type="button" data-dismiss="alert" class="close" style="position:relative; top: -6px;' +
              ' right:-4px;">&times;</button>'
          );
          $alert.append('<i class="iconfont icon-ptkj-mianxingjinggaotishi text-warning font-size-xl"></i>');
          $alert.append('<span class="text-color" style="margin-left: 6px;">系统自动清除</span>');
          $alert.append('<span id="alertRetentionDays" class="text-warning" style="margin: 0 3px;">' + retentionDays + '</span>');
          $alert.append('<span class="text-color">天前删除的流程定义。</span>');
          $alert.insertBefore($element.find('.fixed-table-header'));

          if (!result.data.enabled) {
            $alert.hide();
          }
        }
      });
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
    btn_physical_delete_all: function () {
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
        _self.deleteMultiFlows(rowData);
      } else {
        appModal.error('请选择流程定义！');
      }
    },

    btn_recovery_all: function () {
      var _self = this;
      var rowData = _self.getSelections();
      if (rowData.length > 0) {
        _self.recoveryMultiFlow(rowData);
      } else {
        appModal.error('请选择流程定义！');
      }
    },

    btn_del_all: function () {
      var _self = this;
      var rowData = _self.getSelections();
      if (rowData.length > 0) {
        _self.deleteMultiFlow(rowData);
      } else {
        appModal.error('请选择流程定义！');
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

      appModal.confirm('确认要彻底删除流程吗?', function (result) {
        if (result) {
          $.ajax({
            url: '/api/workflow/definition/physical-delete',
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

      appModal.confirm('确认要彻底删除流程吗?', function (result) {
        if (result) {
          $.ajax({
            url: '/api/workflow/definition/physical-delete-all',
            type: 'POST',
            dataType: 'json',
            data: {
              uuids: uuids
            },
            traditional: true,
            async: false,
            success: function (data) {
              if (data && data.code === 0) {
                appModal.success('刪除成功！');
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

    btn_recovery: function () {
      var _self = this;
      var rowData = _self.getSelectRowData();
      if (rowData.length > 0) {
        _self.recoveryFlow(rowData);
      }
    },

    recoveryFlow: function (rowData) {
      var _self = this;

      $.ajax({
        url: '/api/workflow/definition/recovery',
        type: 'POST',
        dataType: 'json',
        data: {
          uuid: rowData[0].uuid
        },
        async: false,
        success: function (data) {
          if (data && data.code === 0) {
            appModal.success('恢复成功！恢复的流程需要重新启用');
            _self.refresh(); //刷新表格
          } else {
            appModal.alert(data.msg);
          }
        },
        error: function (data) {
          appModal.alert(data.msg);
        }
      });
    },

    recoveryMultiFlow: function (rowData) {
      var _self = this;
      var uuids = [];
      for (var i = 0; i < rowData.length; i++) {
        uuids.push(rowData[i].uuid);
      }

      $.ajax({
        url: '/api/workflow/definition/recovery-all',
        type: 'POST',
        dataType: 'json',
        data: {
          uuids: uuids
        },
        traditional: true,
        async: false,
        success: function (data) {
          if (data && data.code === 0) {
            appModal.success('恢复成功！恢复的流程需要重新启用');
            _self.refresh(); //刷新表格
          } else {
            appModal.alert(data.msg);
          }
        },
        error: function (data) {
          appModal.alert(data.msg);
        }
      });
    },

    btn_schedule_setting: function () {
      var _self = this;
      var html = _self.getScheduleSettingDialogHtml();

      var $dialog = appModal.dialog({
        title: '定时清除设置',
        size: 'middle',
        message: html,
        shown: function () {
          _self.bindScheduleSettingEvents($dialog);

          if ($('#autoCleanUpEnabled', $dialog).val() === '0') {
            $('#autoCleanUpRow', $dialog).hide();
          }
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var enabled = $('#autoCleanUpEnabled', $dialog).val() === '1';
              var retentionDays = $('#retentionDays', $dialog).val();
              var uuid = $('#autoCleanUpUuid', $dialog).val() || null;

              if (!retentionDays) {
                appModal.error('自动清除周期必填！');
                return false;
              }

              $.ajax({
                url: '/api/workflow/definition/cleanup/config',
                type: 'POST',
                dataType: 'json',
                data: {
                  uuid: uuid,
                  enabled: enabled,
                  retentionDays: retentionDays
                },
                async: false,
                success: function (data) {
                  appModal.success('保存成功！');
                  var $element = $(_self.widget.element);
                  if (enabled) {
                    $('#cleanUpAlert', $element).show();
                    $('#cleanUpAlert #alertRetentionDays', $element).text(retentionDays);
                  } else {
                    $('#cleanUpAlert', $element).hide();
                  }
                }
              });
            }
          },
          cancel: {
            label: '关闭',
            className: 'btn-default'
          }
        }
      });
    },

    bindScheduleSettingEvents: function ($dialog) {
      $('#autoCleanUpEnabledBtn', $dialog)
        .off()
        .on('click', function () {
          if ($(this).hasClass('active')) {
            // 关闭
            $(this).removeClass('active');
            $(this).prev().val('0');
            $('#autoCleanUpRow', $dialog).hide();
          } else {
            $(this).addClass('active');
            $(this).prev().val('1');
            $('#autoCleanUpRow', $dialog).show();
          }
        });

      $('#retentionDays', $dialog)
        .off()
        .on('input', function (e) {
          e.target.value = e.target.value.replace(/(^0|\D)/g, '');
        });
    },

    getScheduleSettingDialogHtml: function () {
      var enabled;
      var retentionDays;
      var uuid;

      $.ajax({
        url: '/api/workflow/definition/cleanup/config',
        type: 'GET',
        dataType: 'json',
        async: false,
        success: function (result) {
          uuid = result.data.uuid || '';
          enabled = !!result.data.enabled;
          retentionDays = result.data.retentionDays || 90;
        }
      });

      var html = '';
      html += '<div class="well-form form-horizontal">';
      html += '      <input type="text" name="autoCleanUpUuid" hidden id="autoCleanUpUuid" value="' + uuid + '">';

      // 控件 - 定时清除
      html += '  <div class="form-group">';
      html += '    <label for="autoCleanUpEnabled" class="well-form-label control-label">';
      html += '      定时清除';
      html += '      <div class="w-tooltip">';
      html += '        <i class="iconfont icon-ptkj-tishishuoming"></i>';
      html += '        <div class="w-tooltip-content">开启后系统将自动清除已删除一段时间的流程（已有流程实例的流程无法自动清除）</div>';
      html += '      </div>';
      html += '    </label>';
      html += '    <div>';
      html += '      <input type="text" name="autoCleanUpEnabled" hidden id="autoCleanUpEnabled" value="' + (enabled ? '1' : '0') + '">';
      html += '      <div id="autoCleanUpEnabledBtn" style="top:5px;" class="switch-wrap switch-button ' + (enabled ? 'active' : '') + '">';
      html += '        <span class="switch-text switch-open">开启</span>';
      html += '        <span class="switch-radio"></span>';
      html += '        <span class="switch-text switch-close">关闭</span>';
      html += '      </div>';
      html += '    </div>';
      html += '  </div>';

      // 控件 - 自动清除周期
      html += '  <div class="form-group" id="autoCleanUpRow">';
      html += '    <label for="retentionDays" class="well-form-label control-label required">自动清除周期</label>';
      html += '    <div>';
      html += '      <span>系统自动清除</span>';
      html +=
        '      <input style="width:60px; margin: 0 8px;" type="number" name="retentionDays" id="retentionDays" value="' +
        retentionDays +
        '">';
      html += '      <span>天前删除的流程定义。</span>';
      html += '    </div>';
      html += '  </div>';

      html += '</div>';

      return html;
    },

    btn_view: function (e, ui, rowData) {
      var url = ctx + '/web/app/pt-mgr/pt-wf-mgr/pt-wf-designer.html?readOnly=true&id=' + rowData.uuid;
      top.open(url, '_blank');
    },

    onDblClickRow: function (rowNum, row, $element, field) {
      var windowOptions = {};
      windowOptions.url = ctx + '/web/app/pt-mgr/pt-wf-mgr/pt-wf-designer.html?readOnly=true&id=' + row.uuid;
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
        icon - ptkj - mianxingjinggaotishi;
        if (!_self.checkSelection()) {
          return [];
        }
        rowData = _self.getSelections();
      }
      return rowData;
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
    }
  });

  return AppWorkflowRecycleStationListViewWidgetDevelopment;
});
