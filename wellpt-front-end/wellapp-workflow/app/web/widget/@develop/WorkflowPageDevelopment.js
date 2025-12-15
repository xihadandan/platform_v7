import VuePageDevelopment from '@develop/VuePageDevelopment';
import WorkflowStartNewWork from './WorkflowStartNewWork';

class WorkflowPageDevelopment extends VuePageDevelopment {
  get META() {
    return {
      name: '工作流程页面二开',
      hook: {
        startNewWork: '发起工作'
      }
    };
  }

  /**
   *
   */
  mounted() {
    const _this = this;
    // 监听页面提示信息
    _this.$widget.pageContext.handleCrossTabEvent('workflow:detail:change', result => {
      if (result.message) {
        _this.$widget.$message.success(result.message);
      }
    });
  }

  /**
   * 发起工作
   *
   * @param {*} evt
   */
  startNewWork(evt) {
    const _this = this;
    let workflowStartNewWork = new WorkflowStartNewWork(_this.$widget);
    let newWorkOptions = {};
    workflowStartNewWork.startNewWork(newWorkOptions);
  }
}

export default WorkflowPageDevelopment;
