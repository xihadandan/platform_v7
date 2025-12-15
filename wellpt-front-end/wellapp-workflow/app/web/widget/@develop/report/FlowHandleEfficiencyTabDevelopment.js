import WidgetTabDevelopment from '@develop/WidgetTabDevelopment';
class FlowHandleEfficiencyTabDevelopment extends WidgetTabDevelopment {

  get META() {
    return {
      name: '流程统计分析_办理效率分析页签二开',
    }
  }

  onTabClick(id) {
    // 按人员统计
    if (id == "tab-SXaiWwzPXbEchomsxRSAmyeKALfQwRaZ") {
      this.getPageContext().emitEvent('showUserCustomSearchOfFlowHandleEfficiency');
    } else if (id == "tab-zHywqwWyfqSpBWqfKbVDtLpxmiMNWvIB") {
      this.getPageContext().emitEvent('showDeptCustomSearchOfFlowHandleEfficiency');
    } else {
      console.log("Unknown tab id: " + id);
    }
  }
}

export default FlowHandleEfficiencyTabDevelopment;
