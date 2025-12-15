define([
  'jquery',
  'server',
  'commons',
  'constant',
  'appContext',
  'appModal',
  'DmsDataServices',
  'BizDataView',
  'DmsActionDispatcher',
  'DyformExplain',
  'WorkFlowErrorHandler',
  'WorkFlowInteraction'
], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal,
  DmsDataServices,
  BizDataView,
  DmsActionDispatcher,
  DyformExplain,
  WorkFlowErrorHandler,
  WorkFlowInteraction
) {
  var JDS = server.JDS;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var UUID = commons.UUID;
  // 数据管理单据开发
  var BizProcessItemView = function () {
    var _self = this;
    BizDataView.apply(_self, arguments);

    _self.workFlowMap = {};
    _self.errorHandler = new WorkFlowErrorHandler(_self);
  };
  commons.inherit(BizProcessItemView, BizDataView, {
    // 初始化
    init: function (options) {
      var _self = this;
      options.extraParams = options.extraParams || {};
      _self.options = options;
      _self.itemInstUuid = options.itemInstUuid;
      _self.processDefId = options.processDefId;
      _self.itemId = options.itemId;
      _self.formUuid = options.formUuid;
      _self.dataUuid = options.dataUuid;
      _self.dyformSelector = options.dyformSelector;
    },
    // 加载数据
    load: function () {
      var _self = this;
      var success = function () {
        _self.onLoadSuccess.apply(_self, arguments);
        _self.initLoadedData.apply(_self, arguments);
      };
      var failure = function () {
        _self.onLoadFailure.apply(_self, arguments);
      };

      JDS.restfulPost({
        url: '/proxy/api/biz/process/item/instance/get',
        data: {
          itemInstUuid: _self.itemInstUuid,
          processDefId: _self.processDefId,
          itemId: _self.itemId,
          formUuid: _self.formUuid,
          dataUuid: _self.dataUuid
        },
        success: function (result) {
          success.apply(_self, arguments);
        },
        error: function (error) {
          failure.apply(_self, arguments);
        }
      });
    },
    prepareInitDyform: function (dyformOptions) {
      var _self = this;
      _self._superApply(arguments);

      dyformOptions.dyformDevelopment = dyformOptions.dyformDevelopment || {};
      dyformOptions.dyformDevelopment.beforeSetData = function () {
        this._superApply(arguments);
        // 添加材料是否必填验证
        _self.addMaterialRequiredValidation(this.dyform);
      };
    },
    // 表单初始化成功回调
    onDyformInitSuccess: function () {
      var _self = this;
      _self._superApply(arguments);

      // 加载办理情怀材料
      _self.loadSituationMaterialIfRequired();
      // 加载分发事项办件实例数据
      _self.loadDispenseItemInstances();
    },
    // 添加材料是否必填验证
    addMaterialRequiredValidation: function (dyform) {
      var _self = this;
      var formConfig = _self.bizData.processItemConfig.formConfig;
      var materialSubformId = formConfig.materialSubformId;
      var materialRequiredField = formConfig.materialRequiredField;
      var materialFileField = formConfig.materialFileField;
      if (StringUtils.isBlank(materialSubformId) || StringUtils.isBlank(materialRequiredField) || StringUtils.isBlank(materialFileField)) {
        return;
      }
      dyform.registerFormula({
        triggerElements: [`${materialSubformId}:${materialRequiredField}`],
        formula: function () {
          // 触发公式
          var dataUuid = this.getDataUuid();
          var materialRequiredCtl = dyform.getControl(materialRequiredField, dataUuid);
          var materialFileCtl = dyform.getControl(materialFileField, dataUuid);
          var materialRequired = materialRequiredCtl.getValue();
          if (materialRequired == '1') {
            materialFileCtl.setRequired(true);
          } else if (materialRequired == '0') {
            materialFileCtl.setRequired(false);
          }
        }
      });
    },
    loadSituationMaterialIfRequired: function () {
      var _self = this;
      if (!_self.isDraft() || _self.bizData.enabledSituation != true) {
        return;
      }
      var bizData = this.bizData;
      var itemId = bizData.itemId;
      var processDefId = bizData.processDefId;
      var conditonJsonMap = {};
      JDS.restfulPost({
        url: `/proxy/api/biz/process/item/instance/listItemSituationMaterial`,
        data: {
          itemId,
          processDefId,
          conditonJsonMap: JSON.stringify(conditonJsonMap)
        },
        contentType: 'application/x-www-form-urlencoded',
        success: function (result) {
          var materials = result.data || [];
          _self.updateSituationMaterials(materials);
        }
      });
    },
    // 更新办理情形材料
    updateSituationMaterials: function (materials) {
      var _self = this;
      var materialCodes = {};
      $.each(materials, function (i, material) {
        materialCodes[material.materialCode] = material.materialCode;
      });
      var materialSubformId = _self.bizData.processItemConfig.formConfig.materialSubformId;
      var materialCodeField = _self.bizData.processItemConfig.formConfig.materialCodeField;
      var subform = _self.dyform.getSubform(materialSubformId);
      var subformDataList = subform.getData();
      $.each(subformDataList, function (i, rowData) {
        var materialCode = rowData[materialCodeField];
        if (materialCodes[materialCode] == null) {
          subform.deleteRowData(rowData.id);
        }
      });
    },
    loadDispenseItemInstances: function () {
      var _self = this;
      var $container = _self.$element;
      var dispenseItem = _self.bizData.processItemConfig && _self.bizData.processItemConfig.dispenseItem;
      var dispenseItemPlaceHolder = _self.bizData.processItemConfig && _self.bizData.processItemConfig.dispenseItemPlaceHolder;
      if (
        _self.isDraft() ||
        _self.bizData.itemType == '10' ||
        !dispenseItem ||
        StringUtils.isBlank(dispenseItemPlaceHolder) ||
        StringUtils.isBlank(_self.itemInstUuid)
      ) {
        return;
      }
      var tableId = UUID.createUUID();
      var $processItemContainer =
        $("th[blockcode='" + dispenseItemPlaceHolder + "']", $container).length > 0
          ? $("th[blockcode='" + dispenseItemPlaceHolder + "']", $container)
          : $("td[blockcode='" + dispenseItemPlaceHolder + "']", $container);
      $processItemTable = $processItemContainer.closest('table');
      $processItemTable.after($(`<div id='${tableId}'></div>`));
      appContext.renderWidget({
        renderTo: '#' + tableId,
        widgetDefId: 'wBootstrapTable_CA0612BC2C20000126A05F5581F015B1',
        forceRenderIfConflict: true,
        callback: function () {
          $('#' + tableId + ' > .ui-wBootstrapTable')
            .css({
              overflow: 'auto'
            })
            .children('.bootstrap-table')
            .css({
              marginTop: '0px'
            });
          $processItemTable.css({ marginBottom: '10px' });
        },
        onPrepare: {
          wBootstrapTable_CA0612BC2C20000126A05F5581F015B1: function () {
            listViewWidget = this;
            listViewWidget.develops.push({
              beforeLoadData: function () {
                listViewWidget.addParam('parentItemInstUuid', _self.itemInstUuid);
              }
            });
          }
        }
      });
    },
    // 创建右边栏
    createSidebar: function () {
      var _self = this;
      var defaultShowTab = '';
      var tabShowType = 'float';
      var tabs = _self.getSidebarTabData();
      if (tabs && tabs.length > 0) {
        appContext.require(['AppTabs'], function () {
          // 右侧标签页
          $.fn.appTabs({
            siblings: $('#dyform').parent(),
            tabs: (function () {
              return tabs;
            })(), // tab数据项
            tabShowType: tabShowType, // 展现
            defaultShowTab: defaultShowTab, // 默认展示的标签页
            dragable: true, // 是否可拖动
            showStickPin: true, // 显示固定按钮
            tabPrefix: 'wf_right-sidebar_tab_',
            top: $('.widget-header').height(), // 距离顶部距离
            bottom: $('.footer').outerHeight(), // 距离底部距离
            tabWidth: '450px',
            successCallback: function () {
              $('#dyform').parent().css('padding-right', 11);
              $(document).on('siblingContainerResize', function () {
                var _dyform = _self.getDyform();
                if (_dyform) {
                  _dyform.autoWidth();
                }
              });
            }
          });
          $('#rightMenuTabNavContainer').on('show.bs.tab', '.pull-right-nav>li>a[role=tab]', function (e) {
            var $tab = $(e.target);
            var tabName = $tab.text();
            _self.showSidebarTab(tabName, $($tab.attr('href') + '_content'));
          });
          // tab打开时触发激活的tab显示
          if ($('button.open-tab', '#rightMenuContainer').hasClass('open-tab-active')) {
            $('ul>li.active', '#rightMenuTabNavContainer').find('a').trigger('show.bs.tab');
          }
        });
      }
    },
    // 获取侧边tab数据
    getSidebarTabData: function () {
      var _self = this;
      var tabs = [];
      if (!_self.isDraft()) {
        tabs.push({
          name: '办理过程',
          id: 'biz_item_operation'
        });
      }
      return tabs;
    },
    // 显示侧边tab
    showSidebarTab: function (tabName, $tabContentContainer) {
      var _self = this;
      if (tabName == '办理过程') {
        if (_self.processViewer == null) {
          _self.showItemOperation($('#biz_item_operation_content'));
        }
      }
    },
    showItemOperation: function ($container) {
      var _self = this;
      if (_self.itemOperationLoaded) {
        return;
      }
      _self.itemOperationLoaded = true;
      var bizData = _self.bizData;
      var itemInstUuid = bizData.itemInstUuid;
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      JDS.restfulGet({
        url: `/proxy/api/biz/process/item/instance/listProcessItemOperationByUuid?itemInstUuid=${itemInstUuid}`,
        success: function (result) {
          var dataList = result.data;
          $container.append(templateEngine.renderById('biz-process-item-operation', { operations: dataList }));
        }
      });
    },
    // 表单是否显示为文本
    isDyformDisplayAsLabel: function () {
      return this.isOver();
    },
    getActions: function () {
      var _self = this;
      // 草稿
      if (_self.isDraft()) {
        return [
          { id: 'save', name: '保存', validate: false },
          { id: 'submit', name: '提交', validate: true },
          { id: 'printForm', name: '打印表单', validate: false }
        ];
      } else if (_self.isOver()) {
        // 是否办结
        return [{ id: 'printForm', name: '打印表单', validate: false }];
      } else if (_self.isTimerStarted()) {
        // 计时器已启动
        // 计时器计时中
        if (_self.isTimerTiming()) {
          return [
            { id: 'save', name: '保存', validate: false },
            { id: 'submit', name: '提交', validate: true },
            { id: 'pauseTimer', name: '暂停计时', validate: false },
            { id: 'cancel', name: '撤销', validate: true },
            { id: 'complete', name: '办结', validate: true },
            { id: 'printForm', name: '打印表单', validate: false }
          ];
        } else {
          // 计时器暂停中
          return [
            { id: 'save', name: '保存', validate: false },
            { id: 'submit', name: '提交', validate: true },
            { id: 'resumeTimer', name: '恢复计时', validate: false },
            { id: 'cancel', name: '撤销', validate: true },
            { id: 'complete', name: '办结', validate: true },
            { id: 'printForm', name: '打印表单', validate: false }
          ];
        }
      } else {
        // 计时器未启动
        return [
          { id: 'save', name: '保存', validate: false },
          { id: 'submit', name: '提交', validate: true },
          { id: 'startTimer', name: '开始计时', validate: false },
          { id: 'cancel', name: '撤销', validate: true },
          { id: 'complete', name: '办结', validate: true },
          { id: 'printForm', name: '打印表单', validate: false }
        ];
      }
      return [];
    },
    // 是否新增数据
    isNewItem: function () {
      return StringUtils.isBlank(this.itemInstUuid);
    },
    // 是否草稿
    isDraft: function () {
      return StringUtils.isBlank(this.dataUuid) || this.bizData.state == '00';
    },
    // 是否办结
    isOver: function () {
      return this.bizData.state == '30';
    },
    // 计时器是否已启动
    isTimerStarted: function () {
      return this.bizData.timerState != 0;
    },
    // 是否计时中
    isTimerTiming: function () {
      return this.bizData.timerState == 1;
    },
    // 重新加载单据
    reload: function (itemInstUuid) {
      if (StringUtils.isNotBlank(itemInstUuid)) {
        var url = `/biz/process/item/instance/view?itemInstUuid=${itemInstUuid}`;
        appContext.getWindowManager().refresh(url);
      } else {
        appContext.getWindowManager().refresh();
      }
    },
    // 保存
    save: function (options) {
      var _self = this;
      var bizData = _self.bizData;
      bizData.dyFormData = options.data.dyFormData;
      JDS.restfulPost({
        url: '/proxy/api/biz/process/item/instance/save',
        data: bizData,
        success: function (result) {
          var itemInstUuid = result.data;
          appModal.success('保存成功！', function () {
            _self.reload(itemInstUuid);
          });
        }
      });
    },
    // 触发提交处理
    triggerSubmit: function () {
      this.triggerActionById('submit');
    },
    // 根据事项ID获取流程对象
    getWorkFlowByItemId: function (itemId) {
      var _self = this;
      var workFlowMap = _self.workFlowMap;
      if (workFlowMap[itemId] == null) {
        workFlowMap[itemId] = new WorkFlowInteraction();
        workFlowMap[itemId]._tempData2WorkData();
      }
      return workFlowMap[itemId];
    },
    // 收集流程对象数据
    collectWorkData: function () {
      var _self = this;
      var workFlowMap = _self.workFlowMap;
      for (var key in workFlowMap) {
        workFlowMap[key]._tempData2WorkData();
      }
    },
    // 获取流程数据Map<事项ID, 流程数据>
    getWorkDataMap: function () {
      var _self = this;
      var workDataMap = {};
      var workFlowMap = _self.workFlowMap;
      for (var key in workFlowMap) {
        workDataMap[key] = workFlowMap[key].getWorkData();
      }
      return workDataMap;
    },
    // 提交
    submit: function (options) {
      var _self = this;
      var bizData = _self.bizData;
      bizData.dyFormData = options.data.dyFormData;
      _self.collectWorkData();
      var interactionTaskDataMap = _self.getWorkDataMap();
      bizData.interactionTaskDataMap = interactionTaskDataMap;
      JDS.restfulPost({
        url: '/proxy/api/biz/process/item/instance/submit',
        data: bizData,
        success: function (result) {
          if (result.code == -5002) {
            var wfOptions = {};
            wfOptions.callback = _self.triggerSubmit;
            wfOptions.callbackContext = _self;
            wfOptions.workFlow = _self.getWorkFlowByItemId(result.data.data.itemId);
            _self.errorHandler.handle(result, null, null, wfOptions);
          } else {
            appModal.success({
              message: '提交成功！',
              resultCode: 0
            });
          }
        },
        error: function (jqXHR, statusText, error) {
          var wfOptions = {};
          wfOptions.callback = _self.triggerSubmit;
          wfOptions.callbackContext = _self;
          if (jqXHR.responseJSON.data) {
            wfOptions.workFlow = _self.getWorkFlowByItemId(jqXHR.responseJSON.data.data.itemId);
            _self.errorHandler.handle(jqXHR, null, null, wfOptions);
          } else {
            appModal.error({
              message: jqXHR.responseJSON.msg || '提交失败！'
            });
          }
        }
      });
    },
    // 开始计时
    startTimer: function (options) {
      var _self = this;
      var bizData = _self.bizData;
      var processItemConfig = bizData.processItemConfig || {};
      var formConfig = processItemConfig.formConfig || {};
      var timeLimitField = formConfig.timeLimitField;
      var timeLimit = null;
      try {
        timeLimit = _self.dyform.getFieldValue(timeLimitField);
        if (StringUtils.isBlank(timeLimit)) {
          _self.showChooseTimeLimitDialog({
            processItemConfig,
            callback: function (timeLimitValue) {
              _self.dyform.setFieldValue(timeLimitField, timeLimitValue);
              _self.triggerActionById(options.action);
            }
          });
          return;
        }
        timeLimit = parseInt(timeLimit);
      } catch (e) {
        console.error(e);
      }
      if (isNaN(timeLimit)) {
        appModal.error('请输入有效的办理时限！');
        return;
      }

      bizData.dyFormData = options.data.dyFormData;
      JDS.restfulPost({
        url: '/proxy/api/biz/process/item/instance/startTimer',
        data: bizData,
        success: function (result) {
          var itemInstUuid = result.data;
          appModal.success({
            message: '计时器已启动！',
            callback: function () {
              _self.reload(itemInstUuid);
            }
          });
        }
      });
    },
    showChooseTimeLimitDialog: function (options) {
      var _self = this;
      var bizData = _self.bizData;
      var dlgId = UUID.createUUID();
      var dlgSelector = '#' + dlgId;
      var title = '选择办理时限';
      var message = "<div id='" + dlgId + "'></div>";
      var dlgOptions = {
        title: title,
        message: message,
        size: 'middle',
        shown: function () {
          // 启用办理情形
          if (bizData.enabledSituation == true) {
            _self.loadSituationTimeLimit(function (data) {
              formBuilder.buildSelect2({
                select2: {
                  data: data,
                  defaultBlank: true,
                  remoteSearch: false
                },
                label: '办理时限',
                name: 'timeLimit',
                labelClass: 'required',
                placeholder: '',
                container: dlgSelector
              });
            });
          } else {
            formBuilder.buildSelect2({
              select2: {
                serviceName: 'bizItemDefinitionFacadeService',
                queryMethod: 'listTimeLimitDataSelectData',
                defaultBlank: true,
                remoteSearch: false,
                params: {
                  itemDefId: options.processItemConfig.itemDefId,
                  itemCode: options.processItemConfig.itemCode,
                  timeLimitType: options.processItemConfig.timeLimitType
                }
              },
              label: '办理时限',
              name: 'timeLimit',
              labelClass: 'required',
              placeholder: '',
              container: dlgSelector
            });
          }
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var timeLimit = $("input[name='timeLimit']", dlgSelector).val();
              if (StringUtils.isBlank(timeLimit)) {
                appModal.warning('请选择办理时限！');
                return false;
              }
              options.callback.call(_self, timeLimit);
              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {
              return true;
            }
          }
        }
      };
      appModal.dialog(dlgOptions);
    },
    loadSituationTimeLimit: function (callback) {
      var _self = this;
      var bizData = this.bizData;
      var itemId = bizData.itemId;
      var processDefId = bizData.processDefId;
      var conditonJsonMap = {};
      var timeLimitType = bizData.processItemConfig.timeLimitType;
      JDS.restfulPost({
        url: `/proxy/api/biz/process/item/instance/listItemSituationTimeLimit`,
        data: {
          itemId,
          processDefId,
          conditonJsonMap: JSON.stringify(conditonJsonMap)
        },
        contentType: 'application/x-www-form-urlencoded',
        success: function (result) {
          var timeLimits = result.data || [];
          callback.call(
            _self,
            $.map(timeLimits, function (item) {
              return { id: item.timeLimit, text: item.timeLimit + '个' + (timeLimitType == '1' ? '工作日' : '自然日') };
            })
          );
        }
      });
    },
    // 暂停计时
    pauseTimer: function (options) {
      var _self = this;
      var bizData = _self.bizData;
      bizData.dyFormData = options.data.dyFormData;
      JDS.restfulPost({
        url: '/proxy/api/biz/process/item/instance/pauseTimer',
        data: bizData,
        success: function (result) {
          var itemInstUuid = result.data;
          appModal.success({
            message: '计时器已暂停！',
            callback: function () {
              _self.reload(itemInstUuid);
            }
          });
        }
      });
    },
    resumeTimer: function (options) {
      var _self = this;
      var bizData = _self.bizData;
      bizData.dyFormData = options.data.dyFormData;
      JDS.restfulPost({
        url: '/proxy/api/biz/process/item/instance/resumeTimer',
        data: bizData,
        success: function (result) {
          var itemInstUuid = result.data;
          appModal.success({
            message: '计时器已恢复！',
            callback: function () {
              _self.reload(itemInstUuid);
            }
          });
        }
      });
    },
    cancel: function (options) {
      var _self = this;
      var bizData = _self.bizData;
      bizData.dyFormData = options.data.dyFormData;
      JDS.restfulPost({
        url: '/proxy/api/biz/process/item/instance/cancel',
        data: bizData,
        success: function (result) {
          appModal.success({
            message: '已撤销！',
            resultCode: 0
          });
        }
      });
    },
    complete: function (options) {
      var _self = this;
      var bizData = _self.bizData;
      bizData.dyFormData = options.data.dyFormData;
      JDS.restfulPost({
        url: '/proxy/api/biz/process/item/instance/complete',
        data: bizData,
        success: function (_result) {
          appModal.success({
            message: '已办结！',
            resultCode: 0
          });
        }
      });
    }
  });
  return BizProcessItemView;
});
