import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import { upperFirst } from 'lodash';

class FlowHandleCountTableDevelopment extends WidgetTableDevelopment {

  get META() {
    return {
      name: '流程统计分析_流程办理情况统计表表格二开',
      hook: {
        rowClick: '行点击',
      }
    }
  }

  rowClick($event) {
    const _this = this;
    let rowData = $event.meta;
    let dataIndex = $event.$evt.target.className;
    let countName = _this.getCountName(dataIndex);
    if (!countName) {
      return;
    }
    let title = rowData.title;
    if (rowData.deptId) {
      title = `${rowData.deptName} - ${countName}`;
    } else {
      title = `${rowData.userName} - ${countName}`;
    }
    let queryName = _this.getQueryName(dataIndex, rowData);
    _this.getPageContext().emitEvent('mdHpuOsRbLHRdJXOWnYnITbgrSFSaAap:showDrawer', {
      eventParams: {
        ..._this.$widget.dataSourceParams,
        title,
        queryName,
        byDept: !!rowData.deptId,
        orgId: rowData.deptId || rowData.userId,
      }
    });
  }

  getCountName(dataIndex) {
    let column = this.$widget.configuration.columns.find(column => column.dataIndex === dataIndex);
    return column && column.title;
  }

  getQueryName(dataIndex, rowData) {
    if (rowData.deptId) {
      return `flowHandle${upperFirst(dataIndex)}DetailByDeptQuery`;
    } else {
      return `flowHandle${upperFirst(dataIndex)}DetailByUserQuery`;
    }
  }

}

export default FlowHandleCountTableDevelopment;
