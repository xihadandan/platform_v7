import WorkflowTableDevelopmentBase from './WorkflowTableDevelopmentBase';
import { addDbUrl } from '@framework/vue/utils/function';
import { isEmpty } from 'lodash';
class WorkflowMonitorTableDevelopment extends WorkflowTableDevelopmentBase {
  get META() {
    return {
      name: '工作流程监控表格二开',
      hook: {
        viewMonitor: '查看监控详情',
        logicalDeleteTaskByAdmin: '管理员逻辑删除',
        deleteTaskByAdmin: '管理员删除',
        remind: '催办',
        releaseLock: '解锁',
        gotoTask: '跳转环节',
        handOver: '移交办理'
      }
    };
  }

  /**
   * 查看监控详情
   *
   * @param {*} evt
   */
  viewMonitor(evt) {
    let meta = evt.meta || {};
    let taskInstUuid = meta.taskInstUuid ? meta.taskInstUuid : meta.uuid;
    let flowInstUuid = meta.flowInstUuid;
    let url = `/workflow/work/view/monitor?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`;
    url = addDbUrl(evt, url);
    window.open(this.addSystemPrefix(url), '_blank');
  }

  /**
   * 管理员逻辑删除
   *
   * @param {*} evt
   */
  logicalDeleteTaskByAdmin(evt) {
    this.doDeleteTask(evt, `/api/workflow/work/logicalDeleteByAdmin`, '确认删除流程？');
  }

  /**
   * 管理员删除
   *
   * @param {*} evt
   */
  deleteTaskByAdmin(evt) {
    this.doDeleteTask(evt, `/api/workflow/work/deleteByAdmin`, '确认删除工作(只有处于拟稿环节的工作才能删除)？');
  }

  /**
   * 解锁
   *
   * @param {*} evt
   */
  releaseLock(evt) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    _this.showMask('解锁中...');
    $axios
      .post('/api/workflow/work/releaseAllLock', { taskInstUuids })
      .then(({ data: result }) => {
        _this.hideMask();
        if (result.code == 0) {
          _this.$widget.$message.success('解锁成功！');
        } else {
          _this.handleError(result);
        }
      })
      .catch(({ response }) => {
        _this.hideMask();
        _this.handleError(response);
      });
  }

  /**
   * 跳转环节
   *
   * @param {*} evt
   */
  gotoTask(evt) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    let isBatch = evt.meta && evt.meta.uuid == null;
    _this.openToSignOpinionIfRequired({
      title: '跳转环节',
      label: '跳转原因',
      taskInstUuids,
      required: isBatch ? false : _this.isRequiredGotoTaskOpinion(taskInstUuids),
      onOk: function (opinion) {
        _this.handleGotoTask(taskInstUuids, 0, opinion, { isBatch, uuids: [], successCount: 0, errorCount: 0, errorMsgs: [] });
      }
    });
  }

  /**
   * 处理跳转环节
   *
   * @param {*} taskInstUuids
   * @param {*} index
   * @param {*} opinion
   * @param {*} batchInfo
   */
  handleGotoTask(taskInstUuids, index, opinion, batchInfo) {
    const _this = this;
    // 跳转结束
    if (taskInstUuids.length == index) {
      if (batchInfo.isBatch) {
        _this.showBatchResultMessage(batchInfo);
      } else {
        if (batchInfo.successCount > 0) {
          _this.$widget.$message.success('跳转成功！');
        } else {
          _this.$widget.$message.error('跳转失败！' + (batchInfo.errorMsgs.length > 0 ? batchInfo.errorMsgs[0] : ''));
        }
      }
      _this.refresh();
    } else {
      let taskInstUuid = taskInstUuids[index];
      // 记录第一条选择的数据
      if (index == 0) {
        batchInfo.referenceRowData = _this.getRowDataByTaskInstUuid(taskInstUuid);
      }
      _this.checkTaskAction(taskInstUuid, 'gotoTask', '跳转', opinion, batchInfo).then(valid => {
        // 验证成功，继续跳转处理
        if (valid) {
          _this.doGotoTask(taskInstUuids, index, opinion, batchInfo);
        } else {
          // 验证失败，进入下一条
          _this.handleGotoTask(taskInstUuids, ++index, opinion, batchInfo);
        }
      });
    }
  }

  /**
   * 跳转处理
   *
   * @param {*} taskInstUuids
   * @param {*} index
   * @param {*} opinion
   * @param {*} batchInfo
   */
  doGotoTask(taskInstUuids, index, opinion = {}, batchInfo) {
    const _this = this;
    let taskInstUuid = taskInstUuids[index];
    _this.loadWorkFlow(taskInstUuid).then(workFlow => {
      if (workFlow == null) {
        batchInfo.errorCount++;
        batchInfo.errorMsgs.push(_this.getTitleByTaskInstUuid(taskInstUuid) + '因"数据加载错误"执行失败');
        _this.handleGotoTask(taskInstUuids, ++index, opinion, batchInfo);
        return;
      }

      // 第一条选择的数据信息对比
      if (index != 0 && batchInfo.referenceRowData) {
        let rowData = _this.getRowDataByTaskInstUuid(taskInstUuid);
        if (batchInfo.referenceRowData.taskId != rowData.taskId || batchInfo.referenceRowData.flowDefUuid != rowData.flowDefUuid) {
          batchInfo.errorCount++;
          batchInfo.errorMsgs.push(workFlow.workData.title + '因"与选中列表中第一笔数据的流程不同"执行失败');
          // 批量处理，处理下一条
          _this.handleGotoTask(taskInstUuids, ++index, opinion, batchInfo);
          return;
        }
      }

      // 合并第一条的交互数据
      if (batchInfo.referenceTempData != null) {
        workFlow.tempData = JSON.parse(JSON.stringify(batchInfo.referenceTempData));
        for (let key in batchInfo.referenceTempData) {
          workFlow.workData[key] = batchInfo.referenceTempData[key];
        }
      }

      let success = ({ data: result }) => {
        // 处理成功，进入下一条
        // 记录第一条数据交互选择的内容
        if (index == 0) {
          batchInfo.referenceTempData = JSON.parse(JSON.stringify(workFlow.tempData));
          for (let key in batchInfo.referenceTempData) {
            batchInfo.referenceTempData[key] = workFlow.workData[key];
          }
        }
        batchInfo.successCount++;
        _this.removeWorkFlow(taskInstUuid);
        _this.handleGotoTask(taskInstUuids, ++index, opinion, batchInfo);
      };
      let failure = ({ data: result }) => {
        _this.handleError(result, workFlow, () => {
          _this.doGotoTask(taskInstUuids, index, opinion, batchInfo);
        });
        // // 处理失败，进入下一条
        // batchInfo.errorCount++;
        // batchInfo.errorMsgs.push(_this.getTitleByTaskInstUuid(taskInstUuid) + '因"' + result.data.msg + '"执行失败');
        // _this.handleGotoTask(taskInstUuids, ++index, opinion, batchInfo);
      };
      workFlow.workData.opinionText = opinion.text;
      workFlow.workData.opinionFiles = opinion.files;
      workFlow.workData.action = '环节跳转';
      workFlow.workData.actionType = 'GotoTask';
      workFlow.gotoTask(success, failure);
    });
  }

  /**
   * 判断流程是否需要跳转环节意见
   *
   * @param {*} taskInstUuids
   * @returns
   */
  isRequiredGotoTaskOpinion(taskInstUuids) {
    return this.isRequiredActionOpinion(taskInstUuids, 'gotoTask');
  }

  /**
   * 移交办理
   *
   * @param {*} evt
   */
  handOver(evt) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    let isBatch = evt.meta && evt.meta.uuid == null;
    _this.openToSignOpinionIfRequired({
      title: '移交办理',
      label: '移交原因',
      taskInstUuids,
      required: isBatch ? false : _this.isRequiredHandOverOpinion(taskInstUuids),
      onOk: function (opinion) {
        _this.handleHandOver(taskInstUuids, 0, opinion, { isBatch, uuids: [], successCount: 0, errorCount: 0, errorMsgs: [] });
      }
    });
  }

  /**
   * 处理移交办理
   *
   * @param {*} taskInstUuids
   * @param {*} index
   * @param {*} opinion
   */
  handleHandOver(taskInstUuids, index, opinion, batchInfo) {
    const _this = this;
    // 校验结束
    if (taskInstUuids.length == index) {
      // console.log("检验结束", batchInfo);
      if (batchInfo.errorCount == taskInstUuids.length) {
        _this.showBatchResultMessage(batchInfo);
      } else {
        _this.doHandOver(opinion, batchInfo);
      }
    } else {
      let taskInstUuid = taskInstUuids[index];
      // console.log("校验环节操作", taskInstUuid);
      _this.checkTaskAction(taskInstUuid, 'handOver', '移交', opinion, batchInfo).then(valid => {
        if (valid) {
          batchInfo.successCount++;
          batchInfo.uuids.push(taskInstUuid);
        }
        _this.handleHandOver(taskInstUuids, ++index, opinion, batchInfo);
      });
    }
  }

  /**
   * 移交处理
   *
   * @param {*} opinion
   * @param {*} batchInfo
   */
  doHandOver(opinion = {}, batchInfo) {
    const _this = this;
    _this
      .openOrgSelect({
        title: '选择移交人员',
        valueFormat: 'all'
      })
      .then(values => {
        if (values && values.length > 0) {
          let userIds = values;
          $axios
            .post('/api/workflow/work/handOver', {
              taskInstUuids: batchInfo.uuids,
              userIds,
              opinionText: opinion.text,
              opinionFiles: opinion.files
            })
            .then(({ data: result }) => {
              if (result.code === 0) {
                if (batchInfo.isBatch) {
                  _this.showBatchResultMessage(batchInfo);
                } else {
                  _this.$widget.$message.success('移交办理人成功！');
                }
                _this.refresh();
              } else {
                if (batchInfo.isBatch) {
                  batchInfo.errorMsgs.push('系统服务异常');
                  _this.showBatchResultMessage({
                    ...batchInfo,
                    successCount: 0,
                    errorCount: batchInfo.successCount + batchInfo.errorCount
                  });
                } else {
                  _this.$widget.$message.success('移交失败!');
                }
              }
            })
            .catch(() => {
              if (batchInfo.isBatch) {
                batchInfo.errorMsgs.push('系统服务异常');
                _this.showBatchResultMessage({ ...batchInfo, successCount: 0, errorCount: batchInfo.successCount + batchInfo.errorCount });
              } else {
                _this.$widget.$message.success('移交失败!');
              }
            });
        } else {
          _this.$widget.$message.warning('请选择移交人员!');
          _this.doHandOver(opinion, batchInfo);
        }
      });
  }

  /**
   * 判断流程是否需要移交办理意见
   *
   * @param {*} taskInstUuids
   * @returns
   */
  isRequiredHandOverOpinion(taskInstUuids) {
    return this.isRequiredActionOpinion(taskInstUuids, 'handOver');
  }
}

export default WorkflowMonitorTableDevelopment;
