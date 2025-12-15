import WidgetTabDevelopment from '@develop/WidgetTabDevelopment';
class FlowEfficientyDetailTabDevelopment extends WidgetTabDevelopment {

  get META() {
    return {
      name: '流程统计分析_流程效率分析_流程用时详细统计表页签二开',
    }
  }

  onTabClick(id) {
    // 流程用时
    if (id == "tab-UOEQSaSNuJLvtAiMsMqduiQOyNREataI") {
      this.getPageContext().emitEvent('showFlowDetailTableOfFlowEfficienty');
    } else if (id == "tab-KXCzhSCBMhTJIKzGrQfHzJRnPniJIOyn") {
      this.getPageContext().emitEvent('showTaskDetailTableOfFlowEfficienty');
    } else {
      console.log("Unknown tab id: " + id);
    }
  }
}

export default FlowEfficientyDetailTabDevelopment;
