import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class WfFlowSchemaLogTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '流程管理_流程定义修改日志表格二开',
      hook: {}
    };
  }

  /**
   * 加载数据前回调方法
   *
   * @param {*} params
   */
  beforeLoadData(params) {
    const _this = this;
    let $pageWidget = _this.$widget.pageContext.getVueWidgetById('page_flow_schema_log');
    _this.$widget.deleteDataSourceParams(['flowSchemaUuid']);
    if ($pageWidget && $pageWidget.eventParams && $pageWidget.eventParams.flowSchemaUuid) {
      _this.$widget.addDataSourceParams($pageWidget.eventParams);
    } else {
      _this.$widget.addDataSourceParams({ flowSchemaUuid: '-1' });
    }
  }
}

export default WfFlowSchemaLogTableDevelopment;
