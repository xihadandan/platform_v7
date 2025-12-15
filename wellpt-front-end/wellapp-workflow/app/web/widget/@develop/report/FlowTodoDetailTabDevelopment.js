import WidgetTabDevelopment from '@develop/WidgetTabDevelopment';
class FlowTodoDetailTabDevelopment extends WidgetTabDevelopment {

  get META() {
    return {
      name: '流程统计分析_待办流程分析_流程办理情况统计表页签二开',
    }
  }

  onTabClick(id) {
    // 按人员统计
    if (id == "tab-tvjtwjXVCgINHJAiujXsGSnbIbognvHk") {
      this.getPageContext().emitEvent('showUserCustomSearchOfFlowTodo');
    } else if (id == "tab-CRtxrRlobCjupbjYZcWPjhiWhoRvifcG") {
      this.getPageContext().emitEvent('showDeptCustomSearchOfFlowTodo');
    } else {
      console.log("Unknown tab id: " + id);
    }
  }
}

export default FlowTodoDetailTabDevelopment;
