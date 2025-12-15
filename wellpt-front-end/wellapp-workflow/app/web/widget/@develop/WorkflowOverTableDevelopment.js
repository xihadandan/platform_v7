import WorkflowDoneTableDevelopment from './WorkflowDoneTableDevelopment';
import { addDbUrl } from '@framework/vue/utils/function';
import WorkflowSendToApprove from './WorkflowSendToApprove';
class WorkflowOverTableDevelopment extends WorkflowDoneTableDevelopment {
  get META() {
    return {
      name: '工作流程办结表格二开',
      hook: {
        viewOver: '查看办结详情',
        cancel: '撤回',
        sendToApprove: '送审批'
      }
    };
  }

  /**
   * 查看办结详情
   *
   * @param {*} evt
   */
  viewOver(evt) {
    let meta = evt.meta || {};
    let taskInstUuid = meta.taskInstUuid ? meta.taskInstUuid : meta.uuid;
    let flowInstUuid = meta.flowInstUuid;
    let url = `/workflow/work/view/over?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`;
    url = addDbUrl(evt, url);
    window.open(this.addSystemPrefix(url), '_blank');
  }

  /**
   * 送审批
   *
   * @param {*} evt
   */
  sendToApprove(evt) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    // 事件参数
    let contentType = evt.eventParams && evt.eventParams.contentType;
    let flowDefId = evt.eventParams && evt.eventParams.flowDefId;
    let flowTitle = evt.eventParams && evt.eventParams.flowTitle;
    let flowCategoryCode = evt.eventParams && evt.eventParams.flowCategoryCode;
    let botRuleId = evt.eventParams && evt.eventParams.botRuleId;
    let autoSubmit = (evt.eventParams && evt.eventParams.autoSubmit) || true;
    let successMsg = (evt.eventParams && evt.eventParams.successMsg) || '送审批成功！';

    let selection = _this.getSelection(evt);
    let links = [];
    selection.forEach(rowData => {
      links.push({
        title: rowData.title || rowData.flowTitle,
        url: _this.getLinkUrl(rowData)
      });
    });

    let rowData = selection[0];
    let approveOptions = {
      ui: _this.$widget,
      contentType,
      formUuid: rowData.formUuid,
      dataUuid: rowData.dataUuid,
      links,
      flowDefId,
      flowTitle,
      flowCategoryCode,
      botRuleId,
      autoSubmit,
      successMsg
      // successCallback: function (result, msg) {
      //   _this.$widget.$message.success(successMsg || msg);
      // }
    };
    let workflowSendToApprove = new WorkflowSendToApprove(approveOptions);
    workflowSendToApprove.sendToApprove();
  }

  /**
   * 获取流程数据链接URL地址
   *
   * @param {*} rowData
   * @returns
   */
  getLinkUrl(rowData) {
    let taskInstUuid = rowData.uuid || '';
    let flowInstUuid = rowData.flowInstUuid;
    let url = `/workflow/work/view/work?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`;
    return this.addSystemPrefix(url);
  }
}

export default WorkflowOverTableDevelopment;
