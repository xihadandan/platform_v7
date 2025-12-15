import WidgetTabDevelopment from '@develop/WidgetTabDevelopment';
class FlowTodoTabDevelopment extends WidgetTabDevelopment {

  get META() {
    return {
      name: '流程统计分析_待办流程分析页签二开',
    }
  }

  onTabClick(id) {
    // 按人员统计
    if (id == "tab-UAEezRGzGeRifpWJdTusZHdVnMgtAsBJ") {
      this.getPageContext().emitEvent('showUserCustomSearchOfFlowTodo');
    } else if (id == "tab-kjlWUwrsfWFFoOPWjdYfcDqOJnZnepgV") {
      this.getPageContext().emitEvent('showDeptCustomSearchOfFlowTodo');
    } else {
      console.log("Unknown tab id: " + id);
    }
  }
}

export default FlowTodoTabDevelopment;
