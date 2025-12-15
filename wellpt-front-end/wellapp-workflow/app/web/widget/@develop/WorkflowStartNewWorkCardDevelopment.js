import WidgetCardDevelopment from '@develop/WidgetCardDevelopment';
import WorkflowStartNewWork from './WorkflowStartNewWork';

class WorkflowStartNewWorkCardDevelopment extends WidgetCardDevelopment {
  get META() {
    return {
      name: '工作流程_发起工作卡片二开'
    };
  }

  /**
   * 组件加载后发起工作
   */
  mounted() {
    const _this = this;
    _this.startNewWork();

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
    let cardBodyElement = _this.$widget.$el.querySelector('.ant-card-body');
    let contentElement = document.createElement('div');
    contentElement.classList.add('start-new-work-container');
    cardBodyElement.appendChild(contentElement);
    let workflowStartNewWork = new WorkflowStartNewWork(_this.$widget);
    let newWorkOptions = {
      renderTo: '.start-new-work-container',
      itemLayout: { count: 4, span: 6 }
    };
    workflowStartNewWork.startNewWork(newWorkOptions);
  }
}

export default WorkflowStartNewWorkCardDevelopment;
