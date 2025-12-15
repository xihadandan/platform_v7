import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import BizProcessStartItem from './BizProcessStartItem';
import { isEmpty } from 'lodash';

class BizProcessItemInstanceTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '业务事项办件表格二开',
      hook: {
        startItem: '发起事项办件'
      }
    };
  }

  /**
   *
   */
  mounted() {
    const _this = this;
    // 监听页面提示信息
    _this.$widget.pageContext.handleCrossTabEvent('item:detail:change', result => {
      if (result.message) {
        _this.$widget.$message.success(result.message);
      }
      _this.$widget.refetch();
    });
  }

  startItem(event) {
    const _this = this;
    let eventParams = event.eventParams || {};
    _this.eventParams = eventParams;
    // 事件参数配置丢失，暂时写死
    let processDefId = eventParams.processDefId;
    let processNodeId = eventParams.processNodeId; // 'node_MmGIAMOiEpFWEqjEVWsVaFxNDVULtXpS';
    let itemCode = eventParams.itemCode; // 'test_matter_1'
    if (isEmpty(processDefId)) {
      event.$evtWidget.$message.error('业务流程定义ID不能为空！');
      return;
    }

    let bizProcessStartItem = new BizProcessStartItem(_this.$widget);
    let newItemOptions = { processDefId, processNodeId, itemCode, eventParams };
    bizProcessStartItem.startItem(newItemOptions);
  }

}

export default BizProcessItemInstanceTableDevelopment;
