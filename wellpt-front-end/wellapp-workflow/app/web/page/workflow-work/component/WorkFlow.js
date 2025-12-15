'use strict';
import { isFunction, isEmpty } from 'lodash';
import WorkFlowInteraction from './WorkFlowInteraction.js';
import { deepClone } from '@framework/vue/utils/util';
// 操作服务
var serviceName = 'workV53Service';
// var printUrl = '/workflow/work/v53/print';
var dot = '.';
var services = {
  getWorkData: serviceName + dot + 'getWorkData',
  save: serviceName + dot + 'save',
  submit: serviceName + dot + 'submit',
  rollback: serviceName + dot + 'rollbackWithWorkData',
  rollbackToMainFlow: serviceName + dot + 'rollbackToMainFlowWithWorkData',
  cancel: serviceName + dot + 'cancelWithWorkData',
  getTodoTaskByFlowInstUuid: serviceName + dot + 'getTodoTaskByFlowInstUuid',
  transfer: serviceName + dot + 'transferWithWorkData',
  counterSign: serviceName + dot + 'counterSignWithWorkData',
  copyTo: serviceName + dot + 'copyTo',
  attention: serviceName + dot + 'attention',
  unfollow: serviceName + dot + 'unfollow',
  remind: serviceName + dot + 'remind',
  getPrintTemplates: serviceName + dot + 'getPrintTemplates',
  print: serviceName + dot + 'print',
  getDyformFileFieldDefinitions: serviceName + dot + 'getDyformFileFieldDefinitions',
  handOver: serviceName + dot + 'handOver',
  gotoTask: serviceName + dot + 'gotoTask',
  suspend: serviceName + dot + 'suspend',
  resume: serviceName + dot + 'resume',
  remove: serviceName + dot + 'delete'
};

let DEFAULT_SETTINGS = {
  OPINION_FILE: {
    enabled: true,
    numLimit: 10,
    sizeLimit: 100,
    sizeLimitUnit: 'MB',
    accept: [],
    allowNameRepeat: true,
    downloadAllType: '1'
  },
  OPINION_EDITOR: {
    showMode: 'sidebar' // all、modal、sidebar
  },
  PROCESS_VIEWER: {
    showMode: 'sidebar' // all、modal、sidebar
  }
};

let FlowSettings = function (settings) {
  this.settings = settings;
};
FlowSettings.prototype.get = function (key) {
  return this.settings[key];
};

// JSON服务
class WorkFlow extends WorkFlowInteraction {
  constructor(workData, workView) {
    super(workData);
    this.workView = workView;
    this.beforeServiceCallback = function () { };
    this.afterServiceCallback = function () { };
  }

  getSettings() {
    if (this.settings) {
      return this.settings;
    }

    if (this.workView && this.workView.options && !isEmpty(this.workView.options.settings)) {
      this.settings = Promise.resolve(new FlowSettings(this.workView.options.settings));
      return this.settings;
    }

    if (EASY_ENV_IS_BROWSER) {
      this.settings = $axios
        .get('/proxy/api/workflow/setting/list')
        .then(({ data: result }) => {
          if (result.data) {
            let settings = deepClone(DEFAULT_SETTINGS);
            result.data.forEach(item => {
              let attrVal = JSON.parse(item.attrVal);
              attrVal.enabled = item.enabled;
              settings[item.attrKey] = attrVal;
            });
            return new FlowSettings(settings);
          } else {
            return new FlowSettings(DEFAULT_SETTINGS);
          }
        })
        .catch(() => {
          return new FlowSettings(DEFAULT_SETTINGS);
        });
      return this.settings;
    } else {
      return Promise.resolve(new FlowSettings(DEFAULT_SETTINGS));
    }
  }

  /**
   * 获取工作栏显示的最大按钮数
   */
  getToolbarMaxActionCount(defaultValue) {
    const _this = this;
    let maxCount = defaultValue;
    if (_this.workView && _this.workView.$widget && _this.workView.$widget.$clientCommonApi) {
      return _this.workView.$widget.$clientCommonApi
        .getSystemParamValue('workflow.toolbarPlaceholder.button.max')
        .then(value => {
          if (value) {
            try {
              maxCount = parseInt(value);
            } catch (e) {
              console.error(e);
            }
          }
          return maxCount;
        })
        .catch(error => {
          return maxCount;
        });
    }

    return window.$axios
      .get(`/basicdata/system/param/get`, {
        params: { key: 'workflow.toolbarPlaceholder.button.max' }
      })
      .then(({ data: result }) => {
        let value = result.data;
        try {
          maxCount = parseInt(value);
        } catch (e) {
          console.error(e);
        }
        return maxCount;
      })
      .catch(error => {
        return maxCount;
      });
  }

  /**
   * 是否显示操作提示
   */
  isShowActionTip() {
    const _this = this;
    return _this.getSettings().then(settings => {
      let general = settings.get('GENERAL');
      return general && general.showActionTip;
    });
    // if (_this.showActionTip != null) {
    //   return Promise.resolve(_this.showActionTip);
    // } else if (_this.workView && _this.workView.$widget && _this.workView.$widget.$clientCommonApi) {
    //   return _this.workView.$widget.$clientCommonApi
    //     .getSystemParamValue('workflow.work.action.tip.enabled')
    //     .then(value => {
    //       _this.showActionTip = value == '1' || value == 'true';
    //       return _this.showActionTip;
    //     })
    //     .catch(error => {
    //       return false;
    //     });
    // }

    // return window.$axios
    //   .get(`/basicdata/system/param/get`, {
    //     params: { key: 'workflow.work.action.tip.enabled' }
    //   })
    //   .then(({ data: result }) => {
    //     let value = result.data;
    //     _this.showActionTip = value == '1' || value == 'true';
    //     return _this.showActionTip;
    //   })
    //   .catch(error => {
    //     return false;
    //   });
  }
}
Object.assign(WorkFlow.prototype, WorkFlowInteraction.prototype, {
  // 加载数据
  load: function (ajaxUrl, method, successCallback, failureCallback) {
    var _self = this;
    var success = function (result) {
      _self.workData = result.data.data;
      // 工作流数据存储到临时数据
      _self._workData2TempData();
      if (isFunction(successCallback)) {
        successCallback.call(_self, result);
      }
    };
    var workData = Object.assign({}, _self.workData);
    //			if(appContext.isMobileApp() && StringUtils.isNotBlank(workData.defaultMFormUuid)){
    //				workData.formUuid = workData.defaultMFormUuid;
    //			}else if(StringUtils.isNotBlank(workData.defaultVFormUuid)){
    //				workData.formUuid = workData.defaultVFormUuid;
    //			}
    // _self._service(services.getWorkData, [workData], success, failureCallback);
    if (method == 'GET') {
      _self._ajaxGetService(ajaxUrl, workData, success, failureCallback);
    } else {
      _self._ajaxPostService(ajaxUrl, workData, success, failureCallback);
    }
  },
  // 获取表单数据
  getDyformData: function () {
    return this.workData.dyFormData;
  },
  // 服务前处理
  setBeforeServiceCallback: function (callback) {
    this.beforeServiceCallback = callback;
  },
  // 服务后处理
  setAfterServiceCallback: function (callback) {
    this.afterServiceCallback = callback;
  },
  // 服务处理
  _service: function (service, data, successCallback, failureCallback) {
    var _self = this;
    if (isFunction(_self.beforeServiceCallback)) {
      _self.beforeServiceCallback.apply(_self, arguments);
    }
    var urlParams = {
      isMobileApp: true
    };
    uni.request({
      service: service,
      data: data,
      urlParams: urlParams,
      success: function () {
        // 清理临时数据
        _self.clearTempData();
        if (isFunction(_self.afterServiceCallback)) {
          _self.afterServiceCallback.apply(_self, arguments);
        }
        if (isFunction(successCallback)) {
          successCallback.apply(_self, arguments);
        }
      },
      error: function () {
        if (isFunction(_self.afterServiceCallback)) {
          _self.afterServiceCallback.apply(_self, arguments);
        }
        if (isFunction(failureCallback)) {
          failureCallback.apply(_self, arguments);
        }
      }
    });
  },
  _ajaxGetService: function (ajaxUrl, data, successCallback, failureCallback, refresh) {
    this._ajaxService(ajaxUrl, 'GET', data, successCallback, failureCallback, refresh);
  },
  _ajaxPostService: function (ajaxUrl, data, successCallback, failureCallback, refresh) {
    this._ajaxService(ajaxUrl, 'POST', data, successCallback, failureCallback, refresh);
  },
  _ajaxService: function (ajaxUrl, type, data, successCallback, failureCallback) {
    var _self = this;
    if (isFunction(_self.beforeServiceCallback)) {
      _self.beforeServiceCallback.apply(_self, arguments);
    }
    var _action = data.action;
    if (!_action && data.workBean && data.workBean.action) {
      _action = data.workBean.action;
    }
    let loadingText = _self.workView.$widget.$t('WorkflowWork.operation.' + data.actionType, '');
    if (_action) {
      _self.workView.showLoading({ tip: (loadingText ? loadingText : _action ? _action : _self.workView.$widget.$t('WorkflowWork.operation.Submit', '提交')) + '...' });
    }
    let request = null;
    if (type == 'GET') {
      request = $axios.get(ajaxUrl);
    } else {
      request = $axios.post(ajaxUrl, data);
    }

    request
      .then(function (result) {
        if (_action) {
          _self.workView.hideLoading();
          // uni.hideLoading();
        }
        if (result.data.code == -5002) {
          if (isFunction(_self.afterServiceCallback)) {
            _self.afterServiceCallback.apply(_self, arguments);
          }
          if (isFunction(failureCallback)) {
            failureCallback.apply(_self, arguments);
          }
        } else {
          // 清理临时数据
          _self.clearTempData();
          if (isFunction(_self.afterServiceCallback)) {
            _self.afterServiceCallback.apply(_self, arguments);
          }
          if (isFunction(successCallback)) {
            successCallback.apply(_self, arguments);
          }
        }
      })
      .catch(({ response }) => {
        console.error(response);
        if (_action) {
          _self.workView.hideLoading();
        }
        if (isFunction(_self.afterServiceCallback)) {
          _self.afterServiceCallback.apply(_self, [response]);
        }
        if (isFunction(failureCallback)) {
          failureCallback.apply(_self, [response]);
        }
      });
  },
  // 保存
  save: function (successCallback, failureCallback) {
    var _self = this;
    var success = function (result) {
      // 更新流程实例UUID
      var data = result.data.data;
      _self.workData.flowInstUuid = data.flowInstUuid;
      successCallback.apply(_self, arguments);
    };
    // _self._service(services.save, [_self.workData], success, failureCallback, true);
    var ajaxUrl = '/api/workflow/work/save';
    _self._ajaxPostService(ajaxUrl, _self.workData, success, failureCallback, true);
  },
  // 提交
  submit: function (successCallback, failureCallback) {
    var _self = this;
    // 临时数据合并到工作流数据
    _self._tempData2WorkData();
    // _self._service(services.submit, [_self.workData], successCallback, failureCallback);
    var ajaxUrl = '/api/workflow/work/submit';
    _self._ajaxPostService(ajaxUrl, _self.workData, successCallback, failureCallback);
  },
  complete: function (successCallback, failureCallback) {
    const _this = this;
    // 临时数据合并到工作流数据
    _this._tempData2WorkData();
    let ajaxUrl = '/proxy/api/workflow/work/complete';
    _this._ajaxPostService(ajaxUrl, _this.workData, successCallback, failureCallback);
  },
  // 退回
  rollback: function (successCallback, failureCallback) {
    var _self = this;
    // 临时数据合并到工作流数据
    _self._tempData2WorkData();
    // _self._service(services.rollback, [_self.workData], successCallback, failureCallback);
    var ajaxUrl = '/api/workflow/work/rollback';
    _self._ajaxPostService(ajaxUrl, _self.workData, successCallback, failureCallback);
  },
  // 退回主流程
  rollbackToMainFlow: function (successCallback, failureCallback) {
    var _self = this;
    // 临时数据合并到工作流数据
    _self._tempData2WorkData();
    // _self._service(services.rollbackToMainFlow, [_self.workData], successCallback, failureCallback);
    var ajaxUrl = '/api/workflow/work/rollbackToMainFlow';
    _self._ajaxPostService(ajaxUrl, _self.workData, successCallback, failureCallback);
  },
  // 撤回
  cancel: function (successCallback, failureCallback) {
    var _self = this;
    // 临时数据合并到工作流数据
    _self._tempData2WorkData();
    // _self._service(services.cancel, [_self.workData], successCallback, failureCallback);
    var ajaxUrl = '/api/workflow/work/cancelWithWorkData';
    _self._ajaxPostService(ajaxUrl, _self.workData, successCallback, failureCallback);
  },
  // 根据流程实例获取待办任务
  getTodoTaskByFlowInstUuid: function (successCallback, failureCallback) {
    var _self = this;
    // _self._service(services.getTodoTaskByFlowInstUuid, [_self.workData.flowInstUuid], successCallback, failureCallback);
    var ajaxUrl = '/api/workflow/work/getTodoTaskByFlowInstUuid?flowInstUuid=' + _self.workData.flowInstUuid;
    _self._ajaxGetService(ajaxUrl, {}, successCallback, failureCallback);
  },
  // 转办
  transfer: function (transferUserIds, successCallback, failureCallback) {
    var _self = this;
    // 临时数据合并到工作流数据
    _self._tempData2WorkData();
    // _self._service(services.transfer, [_self.workData, transferUserIds], successCallback, failureCallback);
    var ajaxUrl = '/api/workflow/work/transfer';
    var data = {
      workBean: _self.workData,
      taskInstUuids: [_self.workData.taskInstUuid],
      userIds: transferUserIds,
      transferUsers: _self.transferUsers,
      opinionText: _self.workData.opinionText
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
  },
  // 会签
  counterSign: function (counterSignUserIds, successCallback, failureCallback) {
    var _self = this;
    // 临时数据合并到工作流数据
    _self._tempData2WorkData();
    // _self._service(services.counterSign, [_self.workData, counterSignUserIds], successCallback, failureCallback);
    var ajaxUrl = '/api/workflow/work/counterSign';
    var data = {
      workBean: _self.workData,
      taskInstUuids: [_self.workData.taskInstUuid],
      userIds: counterSignUserIds,
      opinionText: _self.workData.opinionText,
      signUsers: _self.signUsers
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
  },
  // 加签
  addSign: function (addSignUserIds, successCallback, failureCallback) {
    const _self = this;
    // 临时数据合并到工作流数据
    _self._tempData2WorkData();
    let ajaxUrl = '/api/workflow/work/addSign';
    let data = {
      workBean: _self.workData,
      taskInstUuids: [_self.workData.taskInstUuid],
      userIds: addSignUserIds,
      opinionText: _self.workData.opinionText,
      signUsers: _self.addSignUsers
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
  },
  // 抄送
  copyTo: function (copyToUserIds, successCallback, failureCallback) {
    var _self = this;
    // _self._service(services.copyTo, [taskInstUuids, copyToUserIds], successCallback, failureCallback, true);
    var ajaxUrl = '/api/workflow/work/copyTo';
    var data = {
      taskInstUuids: [_self.workData.taskInstUuid],
      userIds: copyToUserIds,
      aclRole: _self.workData.aclRole
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback, true);
  },
  // 关注
  attention: function (taskInstUuids, successCallback, failureCallback) {
    var _self = this;
    // _self._service(services.attention, [taskInstUuids], successCallback, failureCallback, true);
    var ajaxUrl = '/api/workflow/work/attention';
    var data = {
      taskInstUuids: taskInstUuids
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback, true);
  },
  // 取消关注
  unfollow: function (taskInstUuids, successCallback, failureCallback) {
    var _self = this;
    // _self._service(services.unfollow, [taskInstUuids], successCallback, failureCallback, true);
    var ajaxUrl = '/api/workflow/work/unfollow';
    var data = {
      taskInstUuids: taskInstUuids
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback, true);
  },
  // 催办
  remind: function (taskInstUuids, opinionLabel, opinionValue, opinionText, opinionFiles, successCallback, failureCallback) {
    var _self = this;
    // _self._service(services.remind, [taskInstUuids, opinionLabel, opinionValue, opinionText], successCallback, failureCallback);
    var ajaxUrl = '/api/workflow/work/remind';
    var data = {
      taskInstUuids: taskInstUuids,
      opinionLabel: opinionLabel,
      opinionValue: opinionValue,
      opinionText: opinionText,
      opinionFiles
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
  },
  // 获取套打模板
  getPrintTemplates: function (successCallback, failureCallback) {
    var _self = this;
    // _self._service(services.getPrintTemplates, [_self.workData.taskInstUuid, _self.workData.flowInstUuid], successCallback, failureCallback);
    var ajaxUrl = '/api/workflow/work/getPrintTemplates';
    var data = {
      taskInstUuid: _self.workData.taskInstUuid,
      flowInstUuid: _self.workData.flowInstUuid
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
  },
  // 套打
  print: function (printTemplateId, printTemplateUuid, printTemplateLang, successCallback, failureCallback) {
    var _self = this;
    if (typeof printTemplateUuid === 'function') {
      successCallback = printTemplateUuid;
      printTemplateUuid = null;
    }
    if (typeof printTemplateLang === 'function') {
      failureCallback = printTemplateLang;
      printTemplateLang = null;
    }
    // _self._service(services.print, [_self.workData.taskInstUuid, printTemplateId, printTemplateUuid, printTemplateLang], successCallback, failureCallback, true);
    var ajaxUrl = '/api/workflow/work/print';
    var data = {
      taskInstUuid: _self.workData.taskInstUuid,
      printTemplateId: printTemplateId,
      printTemplateUuid: printTemplateUuid,
      printTemplateLang: printTemplateLang,
      action: '套打'
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback, true);
    // var _self = this;
    // var StringBuilder = commons.StringBuilder;
    // var sb = new StringBuilder();
    // sb.append(printUrl);
    // sb.append("?taskInstUuid=" + _self.workData.taskInstUuid);
    // sb.append("&flowInstUuid=" + _self.workData.flowInstUuid);
    // sb.append("&printTemplateId=" + printTemplate.id);
    // sb.append("&printTemplateName=" + printTemplate.name);
    // sb.append("&filename=" + _self.workData.title);
    // appContext.getWindowManager().open(sb.toString(), "_self");
  },
  // 获取表单附件字段的定义信息
  getDyformFileFieldDefinitions: function (successCallback, failureCallback) {
    var _self = this;
    _self._service(services.getDyformFileFieldDefinitions, [_self.workData.taskInstUuid], successCallback, failureCallback);
    var ajaxUrl = '/api/workflow/work/getDyformFileFieldDefinitions?taskInstUuid=' + _self.workData.taskInstUuid;
    _self._ajaxGetService(ajaxUrl, {}, successCallback, failureCallback);
  },
  // 移交
  handOver: function (handOverUserIds, successCallback, failureCallback) {
    var _self = this;
    // 临时数据合并到工作流数据
    _self._tempData2WorkData();
    // _self._service(services.handOver, [_self.workData, handOverUserIds], successCallback, failureCallback);
    var ajaxUrl = '/api/workflow/work/handOver';
    var data = {
      taskInstUuids: [_self.workData.taskInstUuid],
      userIds: handOverUserIds,
      opinionLabel: _self.workData.opinionLabel,
      opinionValue: _self.workData.opinionValue,
      opinionText: _self.workData.opinionText,
      opinionFiles: _self.workData.opinionFiles
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
  },
  // 跳转
  gotoTask: function (successCallback, failureCallback) {
    var _self = this;
    // 临时数据合并到工作流数据
    _self._tempData2WorkData();
    // _self._service(services.gotoTask, [_self.workData], successCallback, failureCallback);
    var ajaxUrl = '/api/workflow/work/gotoTaskWithWorkData';
    _self._ajaxPostService(ajaxUrl, _self.workData, successCallback, failureCallback);
  },
  // 挂起
  suspend: function (taskInstUuids, successCallback, failureCallback) {
    var _self = this;
    // _self._service(services.suspend, [taskInstUuids], successCallback, failureCallback);
    var ajaxUrl = '/api/workflow/work/suspend';
    var data = {
      taskInstUuids: taskInstUuids
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
  },
  // 恢复
  resume: function (taskInstUuids, successCallback, failureCallback) {
    var _self = this;
    // _self._service(services.resume, [taskInstUuids], successCallback, failureCallback, true);
    var ajaxUrl = '/api/workflow/work/resume';
    var data = {
      taskInstUuids: taskInstUuids
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback, true);
  },
  // 删除草稿
  removeDraft: function (flowInstUuids, successCallback, failureCallback) {
    var _self = this;
    var ajaxUrl = '/api/workflow/work/deleteDraft';
    var data = {
      flowInstUuids: flowInstUuids
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
  },
  // 删除
  remove: function (taskInstUuids, successCallback, failureCallback) {
    var _self = this;
    var ajaxUrl = '/api/workflow/work/delete';
    var data = {
      taskInstUuids: taskInstUuids
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
  },
  // 管理员删除
  removeByAdmin: function (taskInstUuids, successCallback, failureCallback) {
    var _self = this;
    var ajaxUrl = '/api/workflow/work/logicalDeleteByAdmin';
    var data = {
      taskInstUuids: taskInstUuids
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
  },
  // 恢复
  recover: function (taskInstUuids, successCallback, failureCallback) {
    const _self = this;
    let ajaxUrl = '/proxy/api/workflow/work/recover';
    let data = {
      taskInstUuids
    };
    _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
  },
});

export default WorkFlow;
