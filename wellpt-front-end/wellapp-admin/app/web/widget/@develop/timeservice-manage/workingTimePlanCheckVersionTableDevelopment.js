import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import { getQueryString } from '@framework/vue/utils/function.js';

class workingTimePlanCheckVersionTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '工作时间方案版本历史表格二开',
      hook: {}
    };
  }

  mounted() {
    let _this = this;
    this.widget = this.getWidget();
    _this.criterions = [];
    let id = getQueryString('id');
    this.setCriterions(id);
    // 监听变化
    this.widget.pageContext.handleEvent('refetchWorkingTimePlanCheckVersionManangeTable', id => {
      let criterions = [];
      if (id) {
        criterions.push({
          columnIndex: 'id',
          value: id,
          type: 'eq'
        });
        _this.widget.clearOtherConditions(_this.criterions);
        _this.criterions = criterions;
        _this.widget.addOtherConditions(criterions);
        _this.widget.refetch(true);
      }
    });
  }
  setCriterions(id) {
    let _this = this;
    let criterions = [];
    if (id) {
      criterions.push({
        columnIndex: 'id',
        value: id,
        type: 'eq'
      });
      _this.widget.clearOtherConditions(_this.criterions);
      _this.criterions = criterions;
      _this.widget.addOtherConditions(criterions);
    }
  }
}

export default workingTimePlanCheckVersionTableDevelopment;
