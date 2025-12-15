define(['constant', 'commons', 'server', 'appContext', 'appModal', 'WorkFlowInteraction'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  WorkFlowInteraction
) {
  // 平台应用_业务流程管理_流程集成_流程片段二开
  var UrlUtils = commons.UrlUtils;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var JDS = server.JDS;
  return {
    getProcessDefId: function () {
      var epProcessDefId = $("input[name='ep_processDefId']").val();
      if (StringUtils.isBlank(epProcessDefId)) {
        epProcessDefId = $("input[name='custom_rt_processDefId']").val();
      }
      return epProcessDefId;
    },
    getProcessItemIds: function () {
      var epProcessItemIds = $("input[name='ep_processItemIds']").val();
      if (StringUtils.isBlank(epProcessItemIds)) {
        epProcessItemIds = $("input[name='custom_rt_processItemIds']").val();
      }
      return epProcessItemIds;
    },
    getFlowBizDefId: function () {
      var epFlowBizDefId = $("input[name='ep_flowBizDefId']").val();
      if (StringUtils.isBlank(epFlowBizDefId)) {
        epFlowBizDefId = $("input[name='custom_rt_flowBizDefId']").val();
      }
      return epFlowBizDefId;
    },
    load: function () {
      var _self = this;
      var workData = _self.getWorkData();
      if (StringUtils.isBlank(workData.flowInstUuid)) {
        var oriLoadData = _self.workFlow.loadData;
        _self.workFlow.loadData = function () {
          var processDefId = _self.getProcessDefId();
          var processItemIds = _self.getProcessItemIds();
          arguments[0] = `/proxy/api/biz/process/item/instance/getWorkDataByWorkBean?processDefId=${processDefId}&processItemIds=${processItemIds}`;
          oriLoadData.apply(this, arguments);
        };
      }
      // 调用父类方法
      _self._superApply(arguments);
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
    // 表单初始化成功后处理
    onDyformInitSuccess: function () {
      var _self = this;
      // 调用父类方法
      _self._superApply(arguments);
      if (_self.isDraft()) {
        // 业务流程定义ID
        _self.epProcessDefId = _self.getProcessDefId();
        // 业务事项ID
        _self.epProcessItemIds = _self.getProcessItemIds();
        // 流程业务ID
        _self.epFlowBizDefId = _self.getFlowBizDefId();

        var workData = _self.getWorkData();
        // 发起流程时添加流程运行时参数
        if (StringUtils.isBlank(workData.flowInstUuid)) {
          _self.addExtraParam('custom_rt_processDefId', _self.epProcessDefId);
          _self.addExtraParam('custom_rt_processItemIds', _self.epProcessItemIds);
          _self.addExtraParam('custom_rt_flowBizDefId', _self.epFlowBizDefId);
          _self.addExtraParam('custom_rt_workViewFragment', 'BizWorkViewIntegrationFragment');
          _self.addExtraParam('custom_rt_flowListener', 'wfFlowBusinessFlowListener');
        }
      }
    },
    // 添加材料是否必填验证
    addMaterialRequiredValidation: function (dyform) {
      var _self = this;
      var workData = _self.getWorkData();
      var extraParams = workData.extraParams || {};
      var formConfig = (extraParams.processItemConfig && extraParams.processItemConfig.formConfig) || {};
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
      delete extraParams.processItemConfig;
    },
    // 提交
    submit: function () {
      var _self = this;
      if (_self.isDraft()) {
        // 获取表单数据
        _self.getDyformData(function (dyformData) {
          try {
            // 提交事项
            if (_self.validateDyformData() === true) {
              _self.submitItem(dyformData);
            }
          } catch (e) {
            appModal.error('表单数据验证出错' + e + '，无法提交数据！');
            throw e;
          }
        });
      } else {
        // 调用父类方法
        _self._superApply(arguments);
      }
    },
    // 提交事项
    submitItem: function (dyformData) {
      var _self = this;
      var workData = _self.getWorkData();
      var flowInstUuid = workData.flowInstUuid;
      var bizData = {
        processDefId: _self.epProcessDefId,
        itemId: _self.epProcessItemIds,
        dyFormData: dyformData
      };
      _self.collectWorkData();
      var interactionTaskDataMap = _self.getWorkDataMap();
      bizData.interactionTaskDataMap = interactionTaskDataMap;
      bizData.extraParams = workData.extraParams || {};
      JDS.restfulPost({
        url: `/proxy/api/biz/process/item/instance/startFromWorkflow?flowInstUuid=${flowInstUuid}`,
        data: bizData,
        mask: true,
        success: function (result) {
          if (result.code == -5002) {
            var wfOptions = {};
            wfOptions.callback = _self.submit;
            wfOptions.callbackContext = _self;
            wfOptions.workFlow = _self.getWorkFlowByItemId(result.data.data.itemId);
            _self.errorHandler.handle(result, null, null, wfOptions);
          } else {
            _self.allowUnloadWorkData = true;
            _self.onSubmitSuccess.apply(_self, arguments);
            // appModal.success({
            //   message: '提交成功！',
            //   resultCode: 0
            // });
          }
        },
        error: function (jqXHR, statusText, error) {
          var wfOptions = {};
          wfOptions.callback = _self.submit;
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
    // 根据事项ID获取流程对象
    getWorkFlowByItemId: function (itemId) {
      var _self = this;
      var workFlowMap = _self.workFlowMap || {};
      if (workFlowMap[itemId] == null) {
        workFlowMap[itemId] = new WorkFlowInteraction();
        workFlowMap[itemId]._tempData2WorkData();
      }
      _self.workFlowMap = workFlowMap;
      return workFlowMap[itemId];
    },
    // 收集流程对象数据
    collectWorkData: function () {
      var _self = this;
      var workFlowMap = _self.workFlowMap || {};
      for (var key in workFlowMap) {
        workFlowMap[key]._tempData2WorkData();
      }
      _self.workFlowMap = workFlowMap;
    },
    // 获取流程数据Map<事项ID, 流程数据>
    getWorkDataMap: function () {
      var _self = this;
      var workDataMap = {};
      var workFlowMap = _self.workFlowMap || {};
      for (var key in workFlowMap) {
        workDataMap[key] = workFlowMap[key].getWorkData();
      }
      _self.workFlowMap = workFlowMap;
      return workDataMap;
    }
  };
});
