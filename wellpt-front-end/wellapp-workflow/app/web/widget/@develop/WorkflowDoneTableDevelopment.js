import WorkflowTableDevelopmentBase from './WorkflowTableDevelopmentBase';
import { addDbUrl } from '@framework/vue/utils/function';

class WorkflowDoneTableDevelopment extends WorkflowTableDevelopmentBase {
  get META() {
    return {
      name: '工作流程已办表格二开',
      hook: {
        viewDone: '查看已办详情',
        cancel: '撤回'
      }
    };
  }

  /**
   * 查看已办详情
   *
   * @param {*} evt
   */
  viewDone(evt) {
    let meta = evt.meta || {};
    let taskInstUuid = meta.taskInstUuid ? meta.taskInstUuid : meta.uuid;
    let flowInstUuid = meta.flowInstUuid;
    let url = `/workflow/work/view/done?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`;
    url = addDbUrl(evt, url);
    window.open(this.addSystemPrefix(url), '_blank');
  }

  /**
   * 撤回
   *
   * @param {*} evt
   */
  cancel(evt) {
    let _this = this;
    let taskInstUuids = _this.getSelectedUuids(evt);
    if (taskInstUuids.length == 0) {
      _this.$widget.$message.error(_this.$widget.$t('WorkflowWork.develop.selectRecord', '请选择记录！'));
      return;
    } else if (taskInstUuids.length > 1) {
      _this.$widget.$message.error(_this.$widget.$t('WorkflowWork.develop.onlyOneRecordSelected', '只能选择一条记录！'));
      return;
    }

    let isBatch = evt.meta && evt.meta.uuid == null;
    _this.openToSignOpinionIfRequired({
      title: _this.$widget.$t('WorkflowWork.develop.withdrawalProcessing', '撤回办理'),
      label: _this.$widget.$t('WorkflowWork.develop.withdrawalOpinion', '撤回意见'),
      taskInstUuids,
      required: isBatch ? false : _this.isRequiredCancelOpinion(taskInstUuids),
      onOk: function (opinion) {
        _this.handleCancel(taskInstUuids, 0, opinion);
      }
    });
  }

  /**
   * 撤回处理
   *
   * @param {*} taskInstUuids
   */
  handleCancel(taskInstUuids, index, opinion) {
    let _this = this;
    _this.isAllowedCancel(taskInstUuids).then(data => {
      if (data.data) {
        _this.doCancel(taskInstUuids, index, opinion);
      } else {
        if (data.msg != '成功') {
          _this.$widget.$message.error(data.msg || _this.$widget.$t('WorkflowWork.develop.processCompletedNotWithdrawn', '环节已办理，无法撤回！'));
        } else {
          _this.$widget.$message.error(_this.$widget.$t('WorkflowWork.develop.processCompletedNotWithdrawn', '环节已办理，无法撤回！'));
        }
      }
    });
  }

  /**
   * 撤回
   * @param {*} taskInstUuids
   */
  doCancel(taskInstUuids, index, opinion = {}) {
    const _this = this;
    _this.showMask(_this.$widget.$t('WorkflowWork.develop.withdrawing', '撤回中...'));
    $axios
      .post('/api/workflow/work/cancel', { taskInstUuids, opinionText: opinion.text, opinionFiles: opinion.files })
      .then(({ data }) => {
        _this.hideMask();
        if (data.code == 0) {
          _this.$widget.$message.success(_this.$widget.$t('WorkflowWork.message.cancelSuccess', '撤回成功！'));
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

  /**
   * 判断流程是否允许撤回
   *
   * @param {*} taskInstUuids
   * @returns
   */
  isAllowedCancel(taskInstUuids) {
    return $axios
      .post('/api/workflow/work/isAllowedCancel', { taskInstUuids })
      .then(({ data }) => {
        return data;
      })
      .catch(() => {
        return false;
      });
  }

  /**
   * 判断流程是否需要撤回意见
   *
   * @param {*} taskInstUuids
   * @returns
   */
  isRequiredCancelOpinion(taskInstUuids) {
    return $axios
      .post('/api/workflow/work/isRequiredCancelOpinion', { taskInstUuids })
      .then(({ data }) => {
        return data.data;
      })
      .catch(() => {
        return false;
      });
  }
}

export default WorkflowDoneTableDevelopment;
