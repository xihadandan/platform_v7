import { isEmpty } from 'lodash';
import WorkFlowErrorHandler from '@workflow/app/web/page/workflow-work/component/WorkFlowErrorHandler.js';
import WorkFlowInteraction from '@workflow/app/web/page/workflow-work/component/WorkFlowInteraction.js';
import WorkflowStartNewWork from './WorkflowStartNewWork';

const workViewApproveFragment = 'WorkViewSendToApproveFragment';
class WorkflowSendToApprove {
  constructor(options) {
    this.options = options;
    this.$widget = options.ui
    // 指定送审内容
    let contentType = options.contentType;
    if (!isEmpty(contentType)) {
      if (contentType.indexOf(';') != -1) {
        this.contentOptions = contentType.split(';');
      } else {
        this.sendContent = {
          type: contentType
        };
      }
    }
    // 指定流程ID
    let flowDefId = options.flowDefId;
    if (!isEmpty(flowDefId)) {
      this.approveFlow = {
        flowDefId,
        newWorkUrl: `/workflow/work/new/${flowDefId}?relateBizDef=true`
      };
    }
    this.workFlow = new WorkFlowInteraction();
    this.errorHandler = new WorkFlowErrorHandler(this);
  }

  /**
   * 送审批
   */
  sendToApprove() {
    const _this = this;
    let sendContent = _this.sendContent;
    let approveFlow = _this.approveFlow;
    let autoSubmit = _this.options.autoSubmit;
    // 发送内容为空，选择内容
    if (sendContent == null) {
      _this.showSelectSendContentDialog();
    } else if (approveFlow == null) {
      // 检验是否允许进行单据转换
      _this.checkAllowedConvertDyformDataByBotRuleIdIfRequired().then(allowed => {
        if (allowed) {
          // 审批流程为空，选择流程
          _this.selectApproveFlow();
        } else {
          console.log('checkAllowedConvertDyformDataByBotRuleIdIfRequired: false');
        }
      });
    } else {
      // 检验是否允许进行单据转换
      _this.checkAllowedConvertDyformDataByBotRuleIdIfRequired().then(allowed => {
        if (allowed) {
          // 自动提交，不打开发起流程页面进行提交
          if (autoSubmit == true || autoSubmit == 'true') {
            _this.autoSubmit2Approve();
          } else {
            // 打开发起流程页面进行提交
            _this.open2Approve();
          }
        } else {
          console.log('checkAllowedConvertDyformDataByBotRuleIdIfRequired: false');
        }
      });
    }
  }

  /**
   * 显示选择发送内容弹出框
   */
  showSelectSendContentDialog() {
    const _this = this;
    const approveTitle = this.$widget.$t('WorkflowWork.selectSendApproval', '选择送审批内容')
    let contentTitle = _this.options.contentTitle || approveTitle;
    let contentOptions = _this.contentOptions;
    let locale = (_this.options.ui && _this.options.ui.locale) || {};

    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale">
        <a-modal dialogClass="pt-modal wf-error-hander-modal" :title="title" :visible="visible" width="500px" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
        <div style="height: 160px; overflow:auto">
          <WorkflowSendToApproveContent ref="approveContent" :values="contentOptions" :evtWidget="evtWidget"></WorkflowSendToApproveContent>
        </div>
        </a-modal>
      </a-config-provider>`,
      components: { WorkflowSendToApproveContent: () => import('./template/workflow-send-to-approve-content.vue') },
      data: function () {
        return {
          title: contentTitle,
          contentOptions,
          visible: true,
          locale,
          evtWidget: _this.$widget
        };
      },
      created() { },
      methods: {
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          this.$refs.approveContent
            .collectContent()
            .then(content => {
              _this.sendContent = content;
              _this.sendToApprove();

              this.visible = false;
              this.$destroy();
            })
            .catch(() => {
              _this.options.ui.$message.error(approveTitle);
            });
        }
      }
    });
    let modal = new Modal();
    modal.$mount();
  }

  /**
   * 检验是否允许进行单据转换
   */
  checkAllowedConvertDyformDataByBotRuleIdIfRequired() {
    const _this = this;
    let sendContent = _this.sendContent;
    let formUuid = _this.options.formUuid;
    let botRuleId = _this.options.botRuleId;
    let approveFlow = _this.approveFlow || {};
    let flowDefId = approveFlow.flowDefId || '';
    if (sendContent != '2' || isEmpty(botRuleId) || isEmpty(flowDefId) || _this.allowedConvertDyformDataByBotRuleId) {
      return Promise.resolve(true);
    }

    let url = `/api/workflow/approve/isAllowedConvertDyformDataByBotRuleId?sourceFormUuid=${formUuid}&botRuleId=${botRuleId}&flowDefId=${flowDefId}`;
    return $axios.get(url).then(({ data }) => {
      _this.allowedConvertDyformDataByBotRuleId = data.data;
      if (!_this.allowedConvertDyformDataByBotRuleId) {
        _this.options.ui.$message.error(`源表单或者目标单据和单据转换规则${botRuleId}中配置的不一致！`);
      }
      return _this.allowedConvertDyformDataByBotRuleId;
    });
  }

  /**
   * 选择送审批流程
   */
  selectApproveFlow() {
    const _this = this;
    let workflowStartNewWork = new WorkflowStartNewWork(_this.$widget);
    let title = this.options.flowTitle || this.$widget.$t('WorkflowWork.selectSendApprovalFlow', '选择送审批流程');
    let newWorkOptions = {
      title,
      categoryCode: _this.options.flowCategoryCode,
      onOk(flowDefId) {
        _this.approveFlow = {
          flowDefId,
          newWorkUrl: `/workflow/work/new/${flowDefId}?relateBizDef=true`
        };
        _this.sendToApprove();
      }
    };
    workflowStartNewWork.startNewWork(newWorkOptions);
  }

  // 自动提交，不打开发起流程页面进行提交
  autoSubmit2Approve() {
    const _this = this;
    let sendType = parseInt(_this.sendContent.type);
    switch (sendType) {
      case 1:
        // 源文送审批
        _this.autoSendSource2Approve(sendType);
        break;
      case 2:
        // 复制源文送审批
        _this.autoSendCopySource2Approve(sendType);
        break;
      case 3:
        // 原文作为链接送审批
        _this.autoSendLink2Approve(sendType);
        break;
      default:
        _this.options.ui.$message.error('无法识别选择送审的内容');
    }
  }

  /**
   * 源文送审批
   */
  autoSendSource2Approve() {
    const _this = this;
    let approveContent = {
      formUuid: _this.options.formUuid,
      dataUuid: _this.options.dataUuid,
      links: _this.options.links
    };
    _this.autoSend2Approve(approveContent);
  }

  /**
   * 复制源文送审批
   */
  autoSendCopySource2Approve() {
    const _this = this;
    let approveContent = {
      formUuid: _this.options.formUuid,
      dataUuid: _this.options.dataUuid,
      botRuleId: _this.options.botRuleId,
      links: _this.options.links
    };
    _this.autoSend2Approve(approveContent);
  }

  /**
   * 原文作为链接送审批
   */
  autoSendLink2Approve() {
    const _this = this;
    let approveContent = {
      links: _this.options.links
    };
    _this.autoSend2Approve(approveContent);
  }

  /**
   * 自动提交后台送审批
   *
   * @param {*} approveContent
   */
  autoSend2Approve(approveContent) {
    const _this = this;
    approveContent.type = _this.sendContent.type;
    approveContent.flowDefId = _this.approveFlow.flowDefId;
    _this.workFlow._tempData2WorkData();
    let interactionTaskData = _this.workFlow.getWorkData();
    $axios
      .post('/api/workflow/approve/sendToApprove', { approveContent, interactionTaskData })
      .then(({ data }) => {
        if (data.code == -5002) {
          var options = {};
          options.callback = _this.sendToApprove;
          options.callbackContext = _this;
          options.workFlow = _this.workFlow;
          _this.errorHandler.handle(data, null, null, options);
        } else {
          _this.onSubmitSuccess.apply(_this, [data]);
        }
      })
      .catch(({ response }) => {
        let options = {};
        console.log(response);
        options.callback = _this.sendToApprove;
        options.callbackContext = _this;
        options.workFlow = _this.workFlow;
        _this.errorHandler.handle(response.data, null, null, options);
      });
  }

  /**
   * 显示错误信息
   *
   * @param {*} msg
   */
  showError(msg) {
    this.options.ui.$message.error(msg.title || msg);
  }

  /**
   * 提交成功处理
   */
  onSubmitSuccess(result) {
    const _this = this;
    let options = _this.options;
    let successCallback = options.successCallback;
    let autoSubmitResult = options.autoSubmitResult || {};
    if (isEmpty(autoSubmitResult.msg)) {
      autoSubmitResult.msg = (options.successMsg || '送审批成功！') + _this.getMsgTips(result.data.taskTodoUsers, '已提交至');
    }

    if (successCallback) {
      successCallback.apply(_this, [result, autoSubmitResult.msg]);
    } else {
      // 提示信息
      _this.options.ui.$message.success(autoSubmitResult.msg, 2, () => {
        // 关闭当前窗口
        if (autoSubmitResult.close) {
          window.close();
        } else if (autoSubmitResult.refresh) {
          // 刷新当前窗口/组件
          if (_this.options.ui.refresh) {
            _this.options.ui.refresh();
          } else {
            window.reload();
          }
        }
      });
    }
  }

  /**
   * 获取提交至人员提示信息
   *
   * @param {*} todoUsers
   * @param {*} msgPrefix
   * @returns
   */
  getMsgTips(todoUsers, msgPrefix) {
    let msgName = '';
    if (todoUsers && Object.keys(todoUsers).length > 0) {
      let userName = [];
      for (let k in todoUsers) {
        let users = todoUsers[k];
        for (let i in users) {
          userName.push(users[i]);
        }
      }
      if (userName.length > 0) {
        msgName = msgPrefix + userName.join('，') + '！';
      }
    }
    return msgName;
  }

  /**
   * 打开发起流程页面进行提交
   */
  open2Approve() {
    const _this = this;
    let sendType = parseInt(_this.sendContent.type);
    switch (sendType) {
      case 1:
        // 源文送审批
        _this.openSource2Approve(sendType);
        break;
      case 2:
        // 复制源文送审批
        _this.openCopySource2Approve(sendType);
        break;
      case 3:
        // 原文作为链接送审批
        _this.openLink2Approve(sendType);
        break;
      default:
        _this.options.ui.$message.error('无法识别选择送审的内容');
    }
  }

  /**
   * 源文送审批
   *
   * @param {*} type
   */
  openSource2Approve(type) {
    const _this = this;
    let contents = {
      links: _this.options.links,
      type
    };
    let urlParams = {
      formUuid: _this.options.formUuid || '',
      dataUuid: _this.options.dataUuid || '',
      ep_workViewFragment: workViewApproveFragment,
      ep_sendContent: encodeURIComponent(JSON.stringify(contents))
    };
    _this.openApproveFlow(urlParams);
  }

  /**
   * 复制源文送审批
   *
   * @param {*} type
   */
  openCopySource2Approve(type) {
    const _this = this;
    let contents = {
      formUuid: _this.options.formUuid || '',
      dataUuid: _this.options.dataUuid || '',
      botRuleId: _this.options.botRuleId || '',
      onlyFillEditableField: _this.options.onlyFillEditableField || false,
      links: _this.options.links,
      type
    };
    let urlParams = {
      ep_workViewFragment: workViewApproveFragment,
      ep_sendContent: encodeURIComponent(JSON.stringify(contents))
    };
    _this.openApproveFlow(urlParams);
  }

  /**
   * 原文作为链接送审批
   *
   * @param {*} type
   */
  openLink2Approve(type) {
    const _this = this;
    let contents = {
      links: _this.options.links,
      type
    };
    let urlParams = {
      ep_workViewFragment: workViewApproveFragment,
      ep_sendContent: encodeURIComponent(JSON.stringify(contents))
    };
    _this.openApproveFlow(urlParams);
  }

  // 打开发起审批流程
  openApproveFlow(urlParams) {
    const _this = this;
    let newWorkUrl = _this.approveFlow.newWorkUrl;
    if (newWorkUrl.indexOf('?') == -1) {
      newWorkUrl += '?';
    }
    let index = 0;
    for (let key in urlParams) {
      let paramPark = key + '=' + urlParams[key];
      if (index == 0) {
        if (newWorkUrl.indexOf('?') == -1) {
          newWorkUrl += '?' + paramPark;
        } else {
          newWorkUrl += '&' + paramPark;
        }
      } else {
        newWorkUrl += '&' + paramPark;
      }
      index++;
    }
    if (_this.options.ui && _this.options.ui._$SYSTEM_ID) {
      newWorkUrl = `/sys/${_this.options.ui._$SYSTEM_ID}/_` + newWorkUrl;
    }
    // 默认发起流程
    window.open(newWorkUrl, '_blank');
  }
}

export default WorkflowSendToApprove;
