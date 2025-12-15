import WidgetTabDevelopment from '@develop/WidgetTabDevelopment';
class FlowHandleOverdueTabDevelopment extends WidgetTabDevelopment {

  get META() {
    return {
      name: '流程统计分析_办理逾期分析页签二开',
    }
  }

  onTabClick(id) {
    // 按人员统计
    if (id == "tab-uqcmWNrOQRNdEMrCSLJUmUctvtjMKDtG") {
      this.getPageContext().emitEvent('showUserCustomSearchOfFlowHandleOverdue');
    } else if (id == "tab-GEOChkxeuHBikDNswePNPknjzMLlCOXQ") {
      this.getPageContext().emitEvent('showDeptCustomSearchOfFlowHandleOverdue');
    } else {
      console.log("Unknown tab id: " + id);
    }
  }
}

export default FlowHandleOverdueTabDevelopment;
