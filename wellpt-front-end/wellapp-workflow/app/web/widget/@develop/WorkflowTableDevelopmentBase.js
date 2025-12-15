import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import WorkFlowErrorHandler from '@workflow/app/web/page/workflow-work/component/WorkFlowErrorHandler.js';
import WorkFlow from '@workflow/app/web/page/workflow-work/component/WorkFlow.js';
import { isEmpty, upperFirst } from 'lodash';
import './css/index.less';
import '../../page/workflow-work/css/index.less';

class WorkflowTableDevelopmentBase extends WidgetTableDevelopment {
  constructor($widget) {
    super($widget);

    this.workFlows = {};

    // 刷新表格事件监听，页面提示由页面二开WorkflowPageDevelopment监听
    this.refreshTableEventHandler = e => {
      if (e.key === 'workflow:detail:change') {
        if (!e.newValue) {
          return;
        }
        this.refresh();
      }
    };
  }

  /**
   *
   */
  mounted() {
    super.mounted();

    window.addEventListener('storage', this.refreshTableEventHandler);
  }

  /**
   *
   */
  beforeDestroy() {
    super.beforeDestroy();

    window.removeEventListener('storage', this.refreshTableEventHandler);
  }

  /**
   * 获取选择的uuid列表
   *
   * @param {*} evt
   * @returns
   */
  getSelectedUuids(evt) {
    const _this = this;
    let uuid = evt.meta.uuid;
    let uuids = _this.$widget.getSelectedRowKeys();
    uuids = uuid && typeof uuid == 'string' ? [uuid] : uuids;
    // 过滤掉列表中已不存在的数据
    uuids = uuids.filter(uuid => _this.$widget.getRowsByKeys([uuid]).length > 0);
    return uuids;
  }

  /**
   * 获取选择的数据列表
   *
   * @param {*} evt
   * @returns
   */
  getSelection(evt) {
    const _this = this;
    let uuid = evt.meta.uuid;
    let rowData = evt.meta;
    let selection = _this.$widget.getSelectedRows();
    return uuid && typeof uuid == 'string' ? [rowData] : selection;
  }

  /**
   * 加载工作流程
   *
   * @param {*} taskInstUuid
   * @returns
   */
  loadWorkFlow(taskInstUuid) {
    const _this = this;
    if (_this.workFlows[taskInstUuid]) {
      return Promise.resolve(_this.workFlows[taskInstUuid]);
    }

    return $axios
      .post('/api/workflow/work/getTodoWorkData', {
        taskInstUuid: taskInstUuid,
        loadDyFormData: false
      })
      .then(({ data: result }) => {
        if (result.code == 0 && result.data) {
          _this.workFlows[taskInstUuid] = new WorkFlow(result.data, _this);
          return _this.workFlows[taskInstUuid];
        } else {
          return null;
        }
      })
      .catch(err => {
        return null;
      });
  }

  /**
   * 移除工作流程
   *
   * @param {*} taskInstUuid
   */
  removeWorkFlow(taskInstUuid) {
    delete this.workFlows[taskInstUuid];
  }

  /**
   * 刷新表格
   */
  refresh() {
    this.$widget.refetch();
  }

  /**
   * 校验环节操作
   *
   * @param {*} taskInstUuid
   * @param {*} action
   * @param {*} actionName
   * @param {*} opinion
   * @param {*} batchInfo
   * @returns
   */
  checkTaskAction(taskInstUuid, action, actionName, opinion, batchInfo) {
    const _this = this;
    // console.log("检查环节锁", taskInstUuid);
    return _this
      .getTaskLock(taskInstUuid)
      .then(locks => {
        // 检查环节锁
        if (locks.length > 0) {
          batchInfo.errorCount++;
          batchInfo.errorMsgs.push(_this.getTitleByTaskInstUuid(taskInstUuid) + '因"文档被锁定"执行失败');
          return false;
        } else {
          return true;
        }
      })
      .then(valid => {
        if (!valid) {
          return valid;
        }
        // 操作权限检测
        // console.log("操作权限检测", taskInstUuid, action);
        return _this.isAllowedAction([taskInstUuid], action).then(allowed => {
          if (!allowed) {
            batchInfo.errorCount++;
            batchInfo.errorMsgs.push(_this.getTitleByTaskInstUuid(taskInstUuid) + `因“操作人员没有“${actionName}”权限”执行失败`);
          }

          return allowed;
        });
      })
      .then(valid => {
        if (!valid) {
          return valid;
        }

        // 判断操作是否必填意见
        // console.log("判断操作是否必填意见", taskInstUuid, action);
        return _this.isRequiredActionOpinion([taskInstUuid], action).then(required => {
          if (required && opinion && isEmpty(opinion.text)) {
            batchInfo.errorCount++;
            batchInfo.errorMsgs.push(_this.getTitleByTaskInstUuid(taskInstUuid) + '因“环节要求必须签署意见但未填写”执行失败');
            return false;
          } else {
            return true;
          }
        });
      });
  }

  /**
   * 显示批量操作结果
   *
   * @param {*} param0
   */
  showBatchResultMessage({ successCount, errorCount, errorMsgs }) {
    const _this = this;
    let successMsg = '成功' + successCount + '条，失败' + errorCount + '条。';
    if (errorCount > 0) {
      successMsg += '其中：' + errorMsgs.join(';');
    }
    if (successCount > 0) {
      _this.$widget.$message.success(successMsg);
    } else {
      _this.$widget.$message.error(successMsg);
    }
  }

  /**
   * 错误处理
   *
   * @param {*} err
   */
  handleError(err, workflow, callback) {
    if (!this.errorHandler) {
      this.errorHandler = new WorkFlowErrorHandler(this);
    }
    var options = {};
    options.workFlow = workflow;
    options.callback = callback;
    options.callbackContext = this;
    this.errorHandler.handle(err, null, null, options);
  }

  /**
   * 打开组织选择框
   */
  openOrgSelect(options) {
    return new Promise((resolve, reject) => {
      WorkFlowErrorHandler.openOrgSelect({
        type: 'all',
        multiple: true,
        selectTypes: 'all',
        valueFormat: options.valueFormat || 'justId',
        ...options,
        locale: this.$widget.locale,
        callback(values) {
          resolve(values);
        }
      });
    });
  }

  /**
   * 显示错误信息
   *
   * @param {*} msg
   */
  showError(msg) {
    if (typeof msg == 'string') {
      this.$widget.$message.error(msg);
    } else {
      this.$widget.$message.error(msg.content || msg.message);
    }
  }

  /**
   * 显示遮罩
   *
   * @param {*} options
   */
  showLoading(options) {
    if (options && options.tip) {
      super.showLoading(options.tip);
    } else if (options) {
      super.showLoading(options);
    }
  }

  /**
   * 显示遮罩
   *
   * @param {*} tip
   */
  showMask(tip) {
    this.showLoading(tip);
  }

  /**
   * 隐藏遮罩
   *
   * @param {*} tip
   */
  hideMask() {
    this.hideLoading();
  }

  /**
   * 删除工作处理
   *
   * @param {*} evt
   * @param {*} deleteUrl
   * @param {*} defaultConfirmMsg
   * @returns
   */
  doDeleteTask(evt, deleteUrl, defaultConfirmMsg) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error(_this.$widget.$t('WorkflowWork.develop.selectRecord', '请选择记录！'));
      return;
    }

    let confirmMsg = (evt.eventParams && evt.eventParams.confirmMsg) || defaultConfirmMsg;
    evt.$evtWidget.$confirm({
      title: _this.$widget.$t('WorkflowWork.message.confirmModal', '确认框'),
      content: confirmMsg,
      okText: _this.$widget.locale.Popconfirm.okText,
      cancelText: _this.$widget.locale.Popconfirm.cancelText,
      onOk() {
        _this.showMask('删除中...');
        $axios
          .post(deleteUrl, { taskInstUuids })
          .then(({ data: result }) => {
            _this.hideMask();
            if (result.code == 0) {
              // 刷新表格
              _this.$widget.refetch();
              _this.$widget.$message.success('删除成功！');
            } else {
              if (result.data && result.data.data && result.data.data.msg) {
                _this.$widget.$message.error(result.data.data.msg);
              } else {
                // _this.$widget.$message.error(result.msg || '删除失败！');
                _this.handleError(result);
              }
            }
          })
          .catch(({ response }) => {
            _this.hideMask();
            // _this.$widget.$message.error('删除失败！');
            _this.handleError(response);
          });
      }
    });
  }

  /**
  * 恢复工作处理
  *
  * @param {*} evt
  * @param {*} deleteUrl
  * @param {*} defaultConfirmMsg
  * @returns
  */
  doRecoverTask(evt, deleteUrl, defaultConfirmMsg) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error(_this.$widget.$t('WorkflowWork.develop.selectRecord', '请选择记录！'));
      return;
    }

    let confirmMsg = (evt.eventParams && evt.eventParams.confirmMsg) || defaultConfirmMsg;
    evt.$evtWidget.$confirm({
      title: _this.$widget.$t('WorkflowWork.message.confirmModal', '确认框'),
      content: confirmMsg,
      okText: _this.$widget.$t('WorkflowWork.ok', '确定'),
      cancelText: _this.$widget.$t('WorkflowWork.opinionManager.operation.cancel', '取消'),
      onOk() {
        _this.showMask('恢复中...');
        $axios
          .post(deleteUrl, { taskInstUuids })
          .then(({ data: result }) => {
            _this.hideMask();
            if (result.code == 0) {
              // 刷新表格
              _this.$widget.refetch();
              _this.$widget.$message.success('恢复成功！');
            } else {
              if (result.data && result.data.data && result.data.data.msg) {
                _this.$widget.$message.error(result.data.data.msg);
              } else {
                _this.handleError(result);
              }
            }
          })
          .catch(({ response }) => {
            _this.hideMask();
            _this.handleError(response);
          });
      }
    });
  }

  /**
   * 操作权限检测
   *
   * @param {*} taskInstUuids
   * @param {*} action
   * @returns
   */
  isAllowedAction(taskInstUuids, action) {
    return $axios
      .post(`/api/workflow/work/isAllowed${upperFirst(action)}`, { taskInstUuids })
      .then(({ data }) => {
        return data.data;
      })
      .catch(() => {
        return false;
      });
  }

  /**
   * 判断流程是否需要办理意见
   *
   * @param {*} taskInstUuids
   * @returns
   */
  isRequiredActionOpinion(taskInstUuids, action) {
    return $axios
      .post(`/api/workflow/work/isRequired${upperFirst(action)}Opinion`, { taskInstUuids })
      .then(({ data }) => {
        return data.data;
      })
      .catch(() => {
        return false;
      });
  }

  /**
   * 签署意见
   *
   * @param {*} options
   */
  openToSignOpinionIfRequired(options) {
    const _this = this;
    let locale = _this.$widget.locale || {};
    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale">
      <a-modal :title="title" dialogClass="pt-modal" :visible="visible" width="700px" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
        <WorkflowTableSignOpinionContent ref="opinionContent" v-if="showSignOpinionContent" :label="label" :required="required"></WorkflowTableSignOpinionContent>
      </a-modal>
      </a-config-provider>`,
      components: { WorkflowTableSignOpinionContent: () => import('./template/workflow-table-sign-opinion-content.vue') },
      data: function () {
        let required = options.required || false;
        this.requiredPromise = Promise.resolve(required);
        return {
          locale,
          showSignOpinionContent: false,
          visible: true,
          title: options.title,
          label: options.label,
          required: options.required
        };
      },
      created() {
        this.requiredPromise
          .then(required => {
            this.required = required;
            this.showSignOpinionContent = true;
          })
          .catch(err => {
            this.required = false;
            this.showSignOpinionContent = true;
          });
      },
      methods: {
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          this.$refs.opinionContent.collectOption().then(opinion => {
            if (options.onOk) {
              options.onOk.call(_this, opinion);
            }
            this.visible = false;
            this.$destroy();
          });
        }
      }
    });
    let modal = new Modal({ i18n: this.$widget.$i18n });
    modal.$mount();
  }

  /**
   * 检查环节锁
   *
   * @param {*} taskInstUuid
   */
  getTaskLock(taskInstUuid) {
    return $axios
      .get(`/api/workflow/work/listLockInfo?taskInstUuid=${taskInstUuid}`)
      .then(({ data: result }) => {
        let taskLocks = result.data;
        return taskLocks || [];
      })
      .catch(err => {
        return [];
      });
  }

  /**
   * 获取行数据
   *
   * @param {*} taskInstUuid
   */
  getRowDataByTaskInstUuid(taskInstUuid) {
    const _this = this;
    let dataProvider = _this.$widget.getDataSourceProvider();
    let selection = (dataProvider.data && dataProvider.data.data) || [];
    let rowData = selection.find(item => item.uuid == taskInstUuid || item.taskInstUuid == taskInstUuid);
    return rowData;
  }

  /**
   * 获取流程标题
   *
   * @param {*} taskInstUuid
   * @returns
   */
  getTitleByTaskInstUuid(taskInstUuid) {
    let rowData = this.getRowDataByTaskInstUuid(taskInstUuid);
    return rowData != null ? rowData.title : '';
  }

  /**
   * 催办
   *
   * @param {*} evt
   */
  remind(evt) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error(_this.$widget.$t('WorkflowWork.develop.selectRecord', '请选择记录！'));
      return;
    }

    _this.showMask(_this.$widget.$t('WorkflowWork.develop.urgentProcessing', '催办中...'));
    $axios
      .post('/api/workflow/work/remind', { taskInstUuids })
      .then(({ data }) => {
        _this.hideMask();
        if (data.code == 0) {
          _this.$widget.$message.success(_this.$widget.$t('WorkflowWork.subFlowShareData.message.urgeSuccess', '催办成功!'));
          _this.refresh();
        } else {
          _this.handleError(data);
        }
      })
      .catch(err => {
        _this.hideMask();
        _this.handleError(err);
      });
  }

  addSystemPrefix(url) {
    const _this = this;
    if (_this.$widget._$SYSTEM_ID && url && !url.startsWith('/sys/')) {
      url = `/sys/${_this.$widget._$SYSTEM_ID}/_${url}`;
    }
    return url;
  }
}

export default WorkflowTableDevelopmentBase;
