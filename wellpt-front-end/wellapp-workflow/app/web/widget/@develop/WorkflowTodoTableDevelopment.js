import WorkflowTableDevelopmentBase from './WorkflowTableDevelopmentBase';
import { snowfalkeId } from '@framework/vue/utils/util';
import { isEmpty } from 'lodash';

class WorkflowTodoTableDevelopment extends WorkflowTableDevelopmentBase {
  get META() {
    return {
      name: '工作流程待办表格二开',
      hook: {
        viewTodo: '查看待办详情',
        deleteTask: '删除环节实例',
        recoverTask: '恢复环节实例',
        submit: '提交',
        directRollback: '直接退回',
        transfer: '转办',
        counterSign: '会签'
      },
      // 将来可能支持的事件参数配置选项
      eventParams: {
        viewTodo: {
          key: 'supportsContinuousWorkView',
          value: 'false',
          remark: '是否支持进入连续签批true是false否，默认false',
          autoFill: false
        }
      }
    };
  }

  /**
   * 查看待办详情
   *
   * @param {*} evt
   */
  viewTodo(evt) {
    const _this = this;
    let meta = evt.meta || {};
    let taskInstUuid = meta.taskInstUuid ? meta.taskInstUuid : meta.uuid;
    let taskIdentityUuid = meta.taskIdentityUuid || '';
    let flowInstUuid = meta.flowInstUuid;
    let url = null;
    let requestCode = _this.setSupportsContinuousWorkViewParamsIfRequired(evt);
    if (!isEmpty(taskIdentityUuid)) {
      url = `/workflow/work/view/todo?taskInstUuid=${taskInstUuid}&taskIdentityUuid=${taskIdentityUuid}&flowInstUuid=${flowInstUuid}&_requestCode=${requestCode}`;
    } else {
      url = `/workflow/work/view/todo?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}&_requestCode=${requestCode}`;
    }
    if (_this.isDisabledContinousWorkView(evt)) {
      window.open(_this.addSystemPrefix(url), '_blank');
    } else {
      this.getContinousWorkSetting().then(config => {
        if (config.enabledContinuousWorkView && config.defaultContinuousWorkView) {
          url += '&continuousMode=1';
        }
        window.open(_this.addSystemPrefix(url), '_blank');
      });
    }
  }

  getContinousWorkSetting() {
    if (this.continousWorkSetting) {
      return Promise.resolve(this.continousWorkSetting);
    }
    return $axios.get(`/proxy/api/workflow/setting/getByKey?key=GENERAL`).then(({ data: result }) => {
      let config = {};
      if (result.data && result.data.attrVal) {
        config = JSON.parse(result.data.attrVal);
        this.continousWorkSetting = config;
      }
      return config;
    }).catch(() => {
      return {};
    });
  }

  /**
   * 设置支持连续签批的参数
   *
   * @param {*} evt
   */
  setSupportsContinuousWorkViewParamsIfRequired(evt) {
    const _this = this;
    // 是否支持连续签批参数
    let supportsContinuousWorkView = (evt.eventParams && evt.eventParams.supportsContinuousWorkView) || 'false';
    let requestCode = snowfalkeId();
    if (supportsContinuousWorkView == 'true') {
      let dataProvider = _this.$widget.getDataSourceProvider();
      let cwvDataStoreParams = { ...((dataProvider && dataProvider.options) || {}) };
      delete cwvDataStoreParams['receiver'];
      delete cwvDataStoreParams['onDataChange'];
      sessionStorage.setItem(`cwvDataStoreParams_${requestCode}`, JSON.stringify(cwvDataStoreParams));
    }
    return requestCode;
  }

  /**
   * 是否禁用连续签批
   *
   * @param {*} evt
   * @returns
   */
  isDisabledContinousWorkView(evt) {
    return !(evt.eventParams && evt.eventParams.supportsContinuousWorkView == 'true');
  }

  /**
   * 删除工作
   *
   * @param {*} evt
   * @returns
   */
  deleteTask(evt) {
    this.doDeleteTask(evt, `/api/workflow/work/delete`, '确认删除流程(只有处于拟稿环节的待办流程才能删除)？');
  }

  /**
  * 恢复工作
  *
  * @param {*} evt
  * @returns
  */
  recoverTask(evt) {
    this.doRecoverTask(evt, `/proxy/api/workflow/work/recover`, '确认恢复流程？');
  }

  /**
   * 提交
   *
   * @param {*} evt
   */
  submit(evt) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error(_this.$widget.$t('WorkflowWork.develop.selectRecord', '请选择记录！'));
      return;
    }

    let isBatch = evt.meta && evt.meta.uuid == null;
    _this.openToSignOpinionIfRequired({
      title: '提交办理',
      label: '签署意见',
      taskInstUuids,
      required: isBatch ? false : _this.isRequiredSubmitOpinion(taskInstUuids),
      onOk: function (opinion) {
        _this.handleSubmit(taskInstUuids, 0, opinion, { isBatch, uuids: [], successCount: 0, errorCount: 0, errorMsgs: [] });
      }
    });
  }

  /**
   * 处理提交
   *
   * @param {*} taskInstUuids
   * @param {*} index
   * @param {*} opinion
   * @param {*} batchInfo
   */
  handleSubmit(taskInstUuids, index, opinion, batchInfo) {
    const _this = this;
    // 提交结束
    if (taskInstUuids.length == index) {
      if (batchInfo.isBatch) {
        _this.showBatchResultMessage(batchInfo);
      } else {
        if (batchInfo.successCount > 0) {
          _this.$widget.$message.success(_this.$widget.$t('WorkflowWork.message.submitSuccess', '提交成功！'));
        } else {
          _this.$widget.$message.error(_this.$widget.$t('WorkflowWork.message.submitFail', '提交失败！') + (batchInfo.errorMsgs.length > 0 ? batchInfo.errorMsgs[0] : ''));
        }
      }
      _this.refresh();
    } else {
      let taskInstUuid = taskInstUuids[index];
      _this.checkTaskAction(taskInstUuid, 'submit', _this.$widget.$t('WorkflowWork.operation.Submit', '提交'), opinion, batchInfo).then(valid => {
        // 验证成功，继续提交处理
        if (valid) {
          _this.doSubmit(taskInstUuids, index, opinion, batchInfo);
        } else {
          // 验证失败，进入下一条
          _this.handleSubmit(taskInstUuids, ++index, opinion, batchInfo);
        }
      });
    }
  }

  /**
   * 提交处理
   *
   * @param {*} taskInstUuids
   * @param {*} index
   * @param {*} opinion
   * @param {*} batchInfo
   */
  doSubmit(taskInstUuids, index, opinion = {}, batchInfo) {
    const _this = this;
    let taskInstUuid = taskInstUuids[index];
    _this.loadWorkFlow(taskInstUuid).then(workFlow => {
      if (workFlow == null) {
        batchInfo.errorCount++;
        batchInfo.errorMsgs.push(_this.getTitleByTaskInstUuid(taskInstUuid) + '因"数据加载错误"执行失败');
        _this.handleSubmit(taskInstUuids, ++index, opinion, batchInfo);
        return;
      }

      // 验证必填字段是否为空
      _this.requiredFieldIsBlank(workFlow.workData).then(({ requiredFieldIsBlank }) => {
        if (requiredFieldIsBlank) {
          batchInfo.errorCount++;
          batchInfo.errorMsgs.push(workFlow.workData.title + '因“源环节有配置字段必填”执行失败');
          _this.handleSubmit(taskInstUuids, ++index, opinion, batchInfo);
        } else {
        }
      });

      let success = () => {
        // 处理成功，进入下一条
        batchInfo.successCount++;
        _this.removeWorkFlow(taskInstUuid);
        _this.handleSubmit(taskInstUuids, ++index, opinion, batchInfo);
      };
      let failure = ({ data: result }) => {
        // 处理失败，进入下一条
        batchInfo.errorCount++;
        let data = result.data || {};
        let errorMsg = '';
        let title = workFlow.workData.title;
        if (data.errorCode == 'RequiredFieldIsBlank' || data.errorCode == 'FormDataValidateException') {
          errorMsg = title + '因“源环节有配置字段必填”执行失败';
        } else if (data.errorCode == 'JudgmentBranchFlowNotFound' || data.errorCode == 'MultiJudgmentBranch') {
          errorMsg = title + '因“下一办理环节为非单向直流”执行失败';
        } else if (
          data.errorCode == 'TaskNotAssignedUser' ||
          data.errorCode == 'TaskNotAssignedCopyUser' ||
          data.errorCode == 'TaskNotAssignedMonitor' ||
          data.errorCode == 'ChooseSpecificUser' ||
          data.errorCode == 'OnlyChooseOneUser'
        ) {
          errorMsg = title + '因“下一办理环节的需要选择办理人员”执行失败';
        } else if (data.errorCode == 'ChooseArchiveFolder') {
          errorMsg = title + '因“下一办理环节的需要选择归档夹”执行失败';
        } else if (result.msg) {
          errorMsg = title + '因"' + result.msg + '"执行失败';
        } else {
          errorMsg = title + '因"系统服务异常"执行失败';
        }
        batchInfo.errorMsgs.push(errorMsg);
        _this.handleSubmit(taskInstUuids, ++index, opinion, batchInfo);
      };
      workFlow.workData.opinionText = opinion.text;
      workFlow.workData.opinionFiles = opinion.files;
      workFlow.workData.action = '提交';
      workFlow.workData.actionType = 'Submit';
      workFlow.submit(success, failure);
    });
  }

  /**
   * 判断表单数据必填字段是否为空
   *
   * @param {*} workData
   * @returns
   */
  requiredFieldIsBlank(workData) {
    return $axios
      .post(`/proxy/api/workflow/work/requiredFieldIsBlank`, workData)
      .then(({ data: result }) => {
        return result.data;
      })
      .catch(() => {
        return false;
      });
  }

  /**
   * 判断是否提交必填意见
   *
   * @param {*} taskInstUuids
   * @returns
   */
  isRequiredSubmitOpinion(taskInstUuids) {
    return this.isRequiredActionOpinion(taskInstUuids, 'submit');
  }

  /**
   * 直接退回
   *
   * @param {*} evt
   */
  directRollback(evt) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    let isBatch = evt.meta && evt.meta.uuid == null;
    _this.openToSignOpinionIfRequired({
      title: '直接退回',
      label: '退回原因',
      taskInstUuids,
      required: isBatch ? false : _this.isRequiredRollbackOpinion(taskInstUuids),
      onOk: function (opinion) {
        _this.handleDirectRollback(taskInstUuids, 0, opinion, { isBatch, uuids: [], successCount: 0, errorCount: 0, errorMsgs: [] });
      }
    });
  }

  /**
   * 处理直接退回
   *
   * @param {*} taskInstUuids
   * @param {*} index
   * @param {*} opinion
   * @param {*} batchInfo
   */
  handleDirectRollback(taskInstUuids, index, opinion, batchInfo) {
    const _this = this;
    // 直接退回结束
    if (taskInstUuids.length == index) {
      if (batchInfo.isBatch) {
        _this.showBatchResultMessage(batchInfo);
      } else {
        if (batchInfo.successCount > 0) {
          _this.$widget.$message.success('直接退回成功！');
        } else {
          _this.$widget.$message.error('直接退回失败！' + (batchInfo.errorMsgs.length > 0 ? batchInfo.errorMsgs[0] : ''));
        }
      }
      _this.refresh();
    } else {
      let taskInstUuid = taskInstUuids[index];
      _this.checkTaskAction(taskInstUuid, 'directRollback', '直接退回', opinion, batchInfo).then(valid => {
        // 验证成功，继续直接退回处理
        if (valid) {
          _this.doDirectRollback(taskInstUuids, index, opinion, batchInfo);
        } else {
          // 验证失败，进入下一条
          _this.handleDirectRollback(taskInstUuids, ++index, opinion, batchInfo);
        }
      });
    }
  }

  /**
   * 直接退回处理
   *
   * @param {*} taskInstUuids
   * @param {*} index
   * @param {*} opinion
   * @param {*} batchInfo
   */
  doDirectRollback(taskInstUuids, index, opinion = {}, batchInfo) {
    const _this = this;
    let taskInstUuid = taskInstUuids[index];
    _this.loadWorkFlow(taskInstUuid).then(workFlow => {
      if (workFlow == null) {
        batchInfo.errorCount++;
        batchInfo.errorMsgs.push(_this.getTitleByTaskInstUuid(taskInstUuid) + '因"数据加载错误"执行失败');
        _this.handleDirectRollback(taskInstUuids, ++index, opinion, batchInfo);
        return;
      }

      let success = ({ data: result }) => {
        // 处理成功，进入下一条
        batchInfo.successCount++;
        _this.removeWorkFlow(taskInstUuid);
        _this.handleDirectRollback(taskInstUuids, ++index, opinion, batchInfo);
      };
      let failure = ({ data: result }) => {
        // 处理失败，进入下一条
        batchInfo.errorCount++;
        batchInfo.errorMsgs.push(workFlow.workData.title + '因"' + result.msg + '"执行失败');
        _this.handleDirectRollback(taskInstUuids, ++index, opinion, batchInfo);
      };
      workFlow.workData.opinionText = opinion.text;
      workFlow.workData.opinionFiles = opinion.files;
      workFlow.workData.action = '直接退回';
      workFlow.workData.actionType = 'DirectRollback';
      workFlow.workData.rollbackToPreTask = true;
      workFlow.rollback(success, failure);
    });
  }

  /**
   * 判断是否转办必填意见
   *
   * @param {*} taskInstUuids
   * @returns
   */
  isRequiredRollbackOpinion(taskInstUuids) {
    return this.isRequiredActionOpinion(taskInstUuids, 'directRollback');
  }

  /**
   * 转办
   *
   * @param {*} evt
   */
  transfer(evt) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    let isBatch = evt.meta && evt.meta.uuid == null;
    _this.openToSignOpinionIfRequired({
      title: '转办办理',
      label: '转办原因',
      taskInstUuids,
      required: isBatch ? false : _this.isRequiredTransferOpinion(taskInstUuids),
      onOk: function (opinion) {
        _this.handleTransfer(taskInstUuids, 0, opinion, { isBatch, uuids: [], successCount: 0, errorCount: 0, errorMsgs: [] });
      }
    });
  }

  /**
   * 处理转办
   *
   * @param {*} taskInstUuids
   * @param {*} index
   * @param {*} opinion
   * @param {*} batchInfo
   */
  handleTransfer(taskInstUuids, index, opinion, batchInfo) {
    const _this = this;
    // 检验结束
    if (taskInstUuids.length == index) {
      // console.log("检验结束", batchInfo);
      // if (batchInfo.errorCount == taskInstUuids.length) {
      //   _this.showBatchResultMessage(batchInfo);
      // } else {
      //   _this.doTransfer(opinion, batchInfo);
      // }
      _this.hideMask();
      if (batchInfo.isBatch) {
        _this.showBatchResultMessage(batchInfo);
      } else {
        if (batchInfo.successCount > 0) {
          _this.$widget.$message.success('转办成功！');
        } else {
          _this.$widget.$message.error('转办失败！' + (batchInfo.errorMsgs.length > 0 ? batchInfo.errorMsgs[0] : ''));
        }
      }
      _this.refresh();
    } else {
      let taskInstUuid = taskInstUuids[index];
      // console.log("检查环节锁", taskInstUuid);
      _this.checkTaskAction(taskInstUuid, 'transfer', '转办', opinion, batchInfo).then(valid => {
        if (valid) {
          // batchInfo.successCount++;
          // batchInfo.uuids.push(taskInstUuid);
          _this.doTransfer(taskInstUuids, index, opinion, batchInfo);
        } else {
          _this.handleTransfer(taskInstUuids, ++index, opinion, batchInfo);
        }
      });
    }
  }

  /**
   * 转办处理
   * @param {*} taskInstUuids
   * @param {*} index
   * @param {*} opinion
   * @param {*} batchInfo
   */
  doTransfer(taskInstUuids, index, opinion = {}, batchInfo) {
    const _this = this;
    let doTransferService = (taskInstUuids, index, opinion, batchInfo) => {
      $axios
        .post('/api/workflow/work/transfer', { taskInstUuids: [taskInstUuids[index]], userIds: batchInfo.userIds, opinionText: opinion.text, opinionFiles: opinion.files })
        .then(({ data: result }) => {
          if (result.code === 0) {
            // 处理成功，进入下一条
            batchInfo.successCount++;
            _this.handleTransfer(taskInstUuids, ++index, opinion, batchInfo);
          } else {
            batchInfo.errorCount++;
            batchInfo.errorMsgs.push(result.data && result.data.data && result.data.data.msg || '系统服务异常');
            _this.handleTransfer(taskInstUuids, ++index, opinion, batchInfo);
          }
        })
        .catch(() => {
          batchInfo.errorCount++;
          batchInfo.errorMsgs.push('系统服务异常');
          _this.handleTransfer(taskInstUuids, ++index, opinion, batchInfo);
        }).finally(() => {
          // _this.hideMask();
        });
    }

    if (batchInfo.userIds) {
      doTransferService(taskInstUuids, index, opinion, batchInfo);
    } else {
      _this.openOrgSelect({
        title: '选择转办人员',
        valueFormat: 'all'
      })
        .then(values => {
          if (values && values.length > 0) {
            let userIds = values;
            batchInfo.userIds = userIds;
            _this.showMask('转办中...');
            doTransferService(taskInstUuids, index, opinion, batchInfo);
          } else {
            _this.$widget.$message.warning('转办人员不能为空!');
            _this.doTransfer(taskInstUuids, index, opinion, batchInfo);
          }
        });
    }
  }

  /**
   * 判断是否转办必填意见
   *
   * @param {*} taskInstUuids
   */
  isRequiredTransferOpinion(taskInstUuids) {
    return this.isRequiredActionOpinion(taskInstUuids, 'transfer');
  }

  /**
   * 会签
   *
   * @param {*} evt
   */
  counterSign(evt) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    let isBatch = evt.meta && evt.meta.uuid == null;
    _this.openToSignOpinionIfRequired({
      title: '会签办理',
      label: '会签原因',
      taskInstUuids,
      required: isBatch ? false : _this.isRequiredCounterSignOpinion(taskInstUuids),
      onOk: function (opinion) {
        _this.handleCounterSign(taskInstUuids, 0, opinion, { isBatch, uuids: [], successCount: 0, errorCount: 0, errorMsgs: [] });
      }
    });
  }

  /**
   * 处理会签
   *
   * @param {*} taskInstUuids
   * @param {*} index
   * @param {*} opinion
   * @param {*} batchInfo
   */
  handleCounterSign(taskInstUuids, index, opinion, batchInfo) {
    const _this = this;
    // 检验结束
    if (taskInstUuids.length == index) {
      // console.log("检验结束", batchInfo);
      if (batchInfo.errorCount == taskInstUuids.length) {
        _this.showBatchResultMessage(batchInfo);
      } else {
        _this.doCounterSign(opinion, batchInfo);
      }
    } else {
      let taskInstUuid = taskInstUuids[index];
      // console.log("检查环节锁", taskInstUuid);
      _this.checkTaskAction(taskInstUuid, 'counterSign', '会签', opinion, batchInfo).then(valid => {
        if (valid) {
          batchInfo.successCount++;
          batchInfo.uuids.push(taskInstUuid);
        }
        _this.handleCounterSign(taskInstUuids, ++index, opinion, batchInfo);
      });
    }
  }

  /**
   * 会签处理
   *
   * @param {*} opinion
   * @param {*} batchInfo
   */
  doCounterSign(opinion = {}, batchInfo) {
    const _this = this;
    _this
      .openOrgSelect({
        title: '选择会签人员',
        valueFormat: 'all'
      })
      .then(values => {
        if (values && values.length > 0) {
          let userIds = values;
          _this.showMask('会签中...');
          $axios
            .post('/api/workflow/work/counterSign', { taskInstUuids: batchInfo.uuids, userIds, opinionText: opinion.text, opinionFiles: opinion.files })
            .then(({ data: result }) => {
              if (result.code === 0) {
                if (batchInfo.isBatch) {
                  _this.showBatchResultMessage(batchInfo);
                } else {
                  _this.$widget.$message.success(_this.$widget.$t('WorkflowWork.message.counterSignSuccess', '会签成功!'));
                }
                _this.refresh();
              } else {
                if (batchInfo.isBatch) {
                  batchInfo.errorMsgs.push(result.data && result.data.data && result.data.data.msg || '系统服务异常');
                  _this.showBatchResultMessage({
                    ...batchInfo,
                    successCount: 0,
                    errorCount: batchInfo.successCount + batchInfo.errorCount
                  });
                } else {
                  _this.$widget.$message.success(_this.$widget.$t('WorkflowWork.message.counterSignFail', '会签失败!'));
                }
              }
            })
            .catch(() => {
              if (batchInfo.isBatch) {
                batchInfo.errorMsgs.push('系统服务异常');
                _this.showBatchResultMessage({ ...batchInfo, successCount: 0, errorCount: batchInfo.successCount + batchInfo.errorCount });
              } else {
                _this.$widget.$message.success(_this.$widget.$t('WorkflowWork.message.counterSignFail', '会签失败!'));
              }
            }).finally(() => {
              _this.hideMask();
            });
        } else {
          _this.$widget.$message.warning('会签人员不能为空!');
          _this.doCounterSign(opinion, batchInfo);
        }
      });
  }

  /**
   * 判断是否会签必填意见
   *
   * @param {*} taskInstUuids
   */
  isRequiredCounterSignOpinion(taskInstUuids) {
    return this.isRequiredActionOpinion(taskInstUuids, 'counterSign');
  }
}

export default WorkflowTodoTableDevelopment;
