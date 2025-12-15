import WidgetTabDevelopment from '@develop/WidgetTabDevelopment';
class FlowHandleCountTabDevelopment extends WidgetTabDevelopment {

  get META() {
    return {
      name: '流程统计分析_流程办理情况统计表页签二开',
    }
  }

  onTabClick(id) {
    // 按人员统计
    if (id == "tab-ZRyoEAKsRIflPjXvlJtyTDkbdWgUSTsn") {
      this.getPageContext().emitEvent('showUserCustomSearchOfFlowHandleCount');
    } else if (id == "tab-fWLClTAUDlqohDMxWgNjkfOnrCUvzAux") {
      this.getPageContext().emitEvent('showDeptCustomSearchOfFlowHandleCount');
    } else {
      console.log("Unknown tab id: " + id);
    }
  }
}

export default FlowHandleCountTabDevelopment;
