import { deepClone } from '@framework/vue/utils/util';
import { isEmpty } from 'lodash';
import WorkFlowInteraction from '@workflow/app/web/page/workflow-work/component/WorkFlowInteraction.js';
class BizWorkViewIntegrationFragment {
  constructor(vueInstance) {
    this.vueInstance = vueInstance;
    this.$widget = vueInstance;
  }

  created() {
    const _this = this;
    let workView = _this.vueInstance.workView;
    _this.workView = workView;
    console.log('workView created', workView);
    if (_this.vueInstance.workData && !isEmpty(_this.vueInstance.workData.flowInstUuid)) {
      if (workView.isDraft()) {
        _this.wrapperSubmitItem(workView);
      }
      _this.getProcessItemConfig().then(processItemConfig => {
        if (processItemConfig) {
          let workData = _this.vueInstance.workData;
          let dyformData = workData.dyFormData;
          let subformData = _this.getSubformData(dyformData);
          for (let subformUuid in subformData) {
            // 添加材料是否必填验证
            _this.workView.loading.visible = true;
            setTimeout(() => {
              _this.addMaterialRequiredValidation(_this.vueInstance.$refs.dyform.dyform, subformUuid, processItemConfig);
              _this.workView.loading.visible = false;
            }, 1000);
          }
        }
      });
      return;
    }
    _this.wrapperSubmitItem(workView);

    let processDefId = _this.getProcessDefId();
    let processItemIds = _this.getProcessItemIds();
    let workData = deepClone(_this.vueInstance.workData);
    delete workData.dyFormData;
    let url = `/proxy/api/biz/process/item/instance/getWorkDataByWorkBean?processDefId=${processDefId}&processItemIds=${processItemIds}`;
    $axios.post(url, workData).then(({ data }) => {
      if (data.data) {
        _this.initWorkData(data.data);
      }
    });
  }

  wrapperSubmitItem(workView) {
    const _this = this;
    workView.__submit__ = workView.submit;
    workView.submit = () => {
      // 签署意见
      if (workView.openToSignIfRequiredSubmit.apply(workView, arguments)) {
        return;
      }
      let workData = workView.getWorkData();
      workView.validateDyformData(workData, function (validate) {
        if (!validate) {
          return;
        }
        _this.submitItem(workView.collectFormData());
      });
    };
  }

  submitItem(dyformData) {
    const _this = this;
    let workData = _this.workView.getWorkData();
    let flowInstUuid = workData.flowInstUuid;
    let bizData = {
      processDefId: _this.getProcessDefId(),
      itemId: _this.getProcessItemIds()
    };
    workData.dyFormData = dyformData;
    _this.collectWorkData();
    // 收集自定义按钮操作的信息
    _this.workView.collectIfUseCustomDynamicButton();
    let interactionTaskDataMap = _this.getWorkDataMap();
    bizData.interactionTaskDataMap = interactionTaskDataMap;
    bizData.extraParams = workData.extraParams || {};
    bizData.workData = workData;
    _this.workView.showLoading(
      _this.workView.currentAction && _this.workView.currentAction.text ? _this.workView.currentAction.text : _this.$widget.$t('WorkflowWork.operation.Submit', '提交')
    );
    $axios
      .post(`/proxy/api/biz/process/item/instance/startFromWorkflow?flowInstUuid=${flowInstUuid}`, bizData)
      .then(result => {
        let data = result.data;
        if (data.code == -5002) {
          let wfOptions = {};
          wfOptions.callback = _this.workView.submit;
          wfOptions.callbackContext = _this;
          wfOptions.workFlow = _this.getWorkFlowByItemId(data.data.data.itemId);
          _this.workView.errorHandler.handle(data, null, null, wfOptions);
        } else {
          _this.workView.allowUnloadWorkData = true;
          _this.workView.onSubmitSuccess.apply(_this.workView, [result]);
        }
      })
      .catch(({ response }) => {
        let wfOptions = {};
        wfOptions.callback = _this.workView.submit;
        wfOptions.callbackContext = _this;
        if (response.data && response.data.data && response.data.data.data) {
          wfOptions.workFlow = _this.getWorkFlowByItemId(response.data.data.data.itemId);
          _this.workView.errorHandler.handle(response, null, null, wfOptions);
        } else {
          _this.vueInstance.$message.error((response.data && response.data.msg) || '提交失败！');
        }
      })
      .finally(() => {
        _this.workView.hideLoading();
      });
  }

  // 根据事项ID获取流程对象
  getWorkFlowByItemId(itemId) {
    const _this = this;
    let workFlowMap = _this.workFlowMap || {};
    if (workFlowMap[itemId] == null) {
      workFlowMap[itemId] = new WorkFlowInteraction();
      workFlowMap[itemId]._tempData2WorkData();
    }
    _this.workFlowMap = workFlowMap;
    return workFlowMap[itemId];
  }
  // 收集流程对象数据
  collectWorkData() {
    const _this = this;
    let workFlowMap = _this.workFlowMap || {};
    for (let key in workFlowMap) {
      workFlowMap[key]._tempData2WorkData();
    }
    _this.workFlowMap = workFlowMap;
  }

  // 获取流程数据Map<事项ID, 流程数据>
  getWorkDataMap() {
    const _this = this;
    let workDataMap = {};
    let workFlowMap = _this.workFlowMap || {};
    for (let key in workFlowMap) {
      workDataMap[key] = workFlowMap[key].getWorkData();
    }
    _this.workFlowMap = workFlowMap;
    return workDataMap;
  }

  initWorkData(workData) {
    const _this = this;
    Object.assign(_this.vueInstance.workData, workData);
    let dyformData = workData.dyFormData;
    let processItemConfig = workData.extraParams && workData.extraParams['processItemConfig'];
    let mainformData = _this.getMainformData(dyformData);
    let subformData = _this.getSubformData(dyformData);
    // 更新表单字段映射值
    if (mainformData != null) {
      Object.assign(_this.vueInstance.$refs.dyform.dyform.formData, mainformData);
      for (let key in mainformData) {
        if (key != 'uuid') {
          _this.vueInstance.$refs.dyform.dyform.setFieldValue(key, mainformData[key]);
        }
      }
    }
    // 更新材料从表
    try {
      for (let subformUuid in subformData) {
        let dataList = subformData[subformUuid];
        dataList.forEach(data => {
          // 标记为新增数据
          delete data.uuid;
          _this.vueInstance.$refs.dyform.$emit(`WidgetSubform:${subformUuid}:addRow`, data);
        });
        // 添加材料是否必填验证
        _this.workView.loading.visible = true;
        setTimeout(() => {
          _this.addMaterialRequiredValidation(_this.vueInstance.$refs.dyform.dyform, subformUuid, processItemConfig);
          _this.workView.loading.visible = false;
        }, 1000);
      }
    } catch (error) {
      console.error(error);
    }

    // 发起流程时添加流程运行时参数
    let workView = _this.vueInstance.workView;
    if (isEmpty(workData.flowInstUuid)) {
      workView.addExtraParam('custom_rt_processDefId', _this.getProcessDefId());
      workView.addExtraParam('custom_rt_processItemIds', _this.getProcessItemIds());
      // workView.addExtraParam('custom_rt_flowBizDefId', _this.getFlowBizDefId());
      workView.addExtraParam('custom_rt_workViewFragment', 'BizWorkViewIntegrationFragment');
      // workView.addExtraParam('custom_rt_flowListener', 'wfFlowBusinessFlowListener');
    }

    // 业务主体字段映射
    if (_this.hasEntityFieldMapping(processItemConfig)) {
      _this.addEntityFieldMappingListener(_this.vueInstance.$refs.dyform.dyform, processItemConfig);
    }
  }

  // 表单数据变更监听回调
  dyformDataChanged() {
    if (this.$entityIdField) {
      this.handletEntityIdFieldChangeIfRequired();
    }
  }

  addEntityFieldMappingListener(dyform, processItemConfig) {
    let entityIdField = processItemConfig.formConfig.entityIdField;
    let field = dyform.getField(entityIdField);
    if (field) {
      this.$latestEntityIdValue = field.getValue();
      this.$entityIdField = field;
    }
  }

  handletEntityIdFieldChangeIfRequired() {
    const _this = this;
    let entityIdValue = _this.$entityIdField.getValue();
    if (_this.$latestEntityIdValue != entityIdValue) {
      _this.$latestEntityIdValue = entityIdValue;
      _this.getProcessItemConfig().then(processItemConfig => {
        if (processItemConfig) {
          let processDefId = _this.getProcessDefId();
          let flowInstUuid = _this.vueInstance.workData.flowInstUuid;
          $axios
            .get(`/proxy/api/biz/process/instance/getEntityFormDataOfMainform`, {
              params: {
                processDefId,
                flowInstUuid,
                entityId: entityIdValue
              }
            })
            .then(({ data: result }) => {
              if (result.data) {
                _this.setEntityFieldMapping(_this.vueInstance.$refs.dyform.dyform, result.data, processItemConfig);
              }
            });
        }
      });
    }
  }

  // 是否有业务主体字段映射
  hasEntityFieldMapping(processItemConfig) {
    if (!processItemConfig || !processItemConfig.formConfig || !processItemConfig.formConfig.entityIdField) {
      return false;
    }

    let formConfig = processItemConfig.formConfig || {};
    let fieldMappings = formConfig.fieldMappings || [];
    let materialFieldMappings = formConfig.materialFieldMappings || [];
    let entityMappingIndex = fieldMappings.findIndex(item => item.sourceType == 'entity');
    if (entityMappingIndex == -1) {
      entityMappingIndex = materialFieldMappings.findIndex(item => item.sourceType == 'entity');
    }
    return entityMappingIndex != -1;
  }

  // 设置业务主体字段映射
  setEntityFieldMapping(dyform, dataMap, processItemConfig) {
    let formConfig = processItemConfig.formConfig || {};
    // 办理单主表字段映射
    let fieldMappings = formConfig.fieldMappings || [];
    fieldMappings
      .filter(item => item.sourceType == 'entity')
      .forEach(item => {
        let sourceValue = dataMap[item.sourceField];
        dyform.setFieldValue(item.targetField, sourceValue);
      });

    // 办理单材料从表字段映射
    let materialFieldMappings = formConfig.materialFieldMappings || [];
    let materialSubformId = formConfig.materialSubformId;
    materialFieldMappings
      .filter(item => item.sourceType == 'entity')
      .forEach(item => {
        let sourceValue = dataMap[item.sourceField];
        let subformObject = dyform.subform || {};
        for (let subformUuid in subformObject) {
          let subformRecords = subformObject[subformUuid];
          subformRecords.forEach(record => {
            if (record.formId == materialSubformId) {
              record.setFieldValue(item.targetField, sourceValue);
            }
          });
        }
      });
  }

  // 添加材料是否必填验证
  addMaterialRequiredValidation(dyform, subformUuid, processItemConfig) {
    if (!processItemConfig) {
      return;
    }
    let formConfig = processItemConfig.formConfig || {};
    let materialRequiredField = formConfig.materialRequiredField;
    let materialFileField = formConfig.materialFileField;
    let subformRecords = dyform.subform[subformUuid];
    if (!materialRequiredField || !materialFileField || !subformRecords) {
      return;
    }
    subformRecords.forEach(record => {
      let materialRequired = record.formData[materialRequiredField];
      let setFieldRequired = (record, materialFileField, materialRequired) => {
        let field = record.getField(materialFileField);
        setTimeout(
          () => {
            if (field == null) {
              setFieldRequired(record, materialFileField, materialRequired);
              return;
            }
            if (materialRequired == '1') {
              record.setFieldRequired(materialFileField, true);
            } else {
              record.setFieldRequired(materialFileField, false);
            }
          },
          field == null ? 500 : 0
        );
      };
      setFieldRequired(record, materialFileField, materialRequired);
    });
  }

  getMainformData(dyformData) {
    let formUuid = dyformData.formUuid;
    let formDatas = dyformData.formDatas || {};
    for (let key in formDatas) {
      if (key == formUuid) {
        let dataList = formDatas[key];
        return dataList[0] || {};
      }
    }
    return {};
  }

  getSubformData(dyformData) {
    let subformData = {};
    let formUuid = dyformData.formUuid;
    let formDatas = dyformData.formDatas || {};
    for (let key in formDatas) {
      if (key != formUuid) {
        subformData[key] = formDatas[key];
      }
    }
    return subformData;
  }

  getProcessItemConfig() {
    if (this.$processItemConfig) {
      return Promise.resolve(this.$processItemConfig);
    }
    let workData = this.vueInstance.workData;
    let processItemConfig = workData.extraParams && workData.extraParams['processItemConfig'];
    if (processItemConfig) {
      return Promise.resolve(processItemConfig);
    }
    return $axios
      .get(
        `/proxy/api/biz/process/definition/getProcessItemConfig?processDefId=${this.getProcessDefId()}&processItemIds=${this.getProcessItemIds()}`
      )
      .then(({ data: result }) => {
        this.$processItemConfig = result.data;
        return result.data;
      });
  }

  getProcessDefId() {
    let workData = this.vueInstance.workData;
    let extraParams = workData.extraParams || {};
    return extraParams['ep_processDefId'] || extraParams['custom_rt_processDefId'];
  }

  getProcessItemIds() {
    let workData = this.vueInstance.workData;
    let extraParams = workData.extraParams || {};
    return extraParams['ep_processItemIds'] || extraParams['custom_rt_processItemIds'];
  }

  getFlowBizDefId() {
    let workData = this.vueInstance.workData;
    let extraParams = workData.extraParams || {};
    return extraParams['ep_flowBizDefId'] || extraParams['custom_rt_flowBizDefId'];
  }
}

export default BizWorkViewIntegrationFragment;
