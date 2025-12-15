import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import { getQueryString } from '@framework/vue/utils/function.js';

class workingTimePlanCheckHistoryTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '工作时间方案历史工作时间表格二开',
      hook: {}
    };
  }

  mounted() {
    let _this = this;
    this.widget = this.getWidget();
    _this.criterions = [];
    let uuid = getQueryString('uuid');
    this.setCriterions(uuid);
    // 监听变化
    this.widget.pageContext.handleEvent('refetchWorkingTimePlanCheckHistoryManangeTable', uuid => {
      let criterions = [];
      // if (uuid) {
      //   criterions.push({
      //     columnIndex: 'workTimePlanUuid',
      //     value: uuid,
      //     type: 'eq'
      //   });
      //   _this.widget.clearOtherConditions(_this.criterions);
      //   _this.criterions = criterions;
      //   _this.widget.addOtherConditions(criterions);
      //   _this.widget.refetch(true);
      // }
    });
  }

  setCriterions(uuid) {
    let _this = this;
    let criterions = [];
    if (uuid) {
      criterions.push({
        columnIndex: 'workTimePlanUuid',
        value: uuid,
        type: 'eq'
      });
      _this.widget.clearOtherConditions(_this.criterions);
      _this.criterions = criterions;
      _this.widget.addOtherConditions(criterions);
    }
  }
}

export default workingTimePlanCheckHistoryTableDevelopment;
