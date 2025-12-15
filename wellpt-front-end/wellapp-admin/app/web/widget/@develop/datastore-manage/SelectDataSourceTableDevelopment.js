import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class SelectDataSourceTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '数据仓库管理表格_选择数据源二开',
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
    _this.$widget.deleteDataSourceParams(['dbLinkConfUuid']);
    if (!params.criterions) {
      return;
    }
    const emptyColumnRecords = params.criterions.filter(item => {
      // 检查 columnIndex 属性是否为空（undefined,）
      return item.columnIndex === undefined && item.value != undefined;
    });
    //去掉为空的记录
    params.criterions = params.criterions.filter(item => {
      return !(item.columnIndex === undefined && item.value != undefined);
    });
    if (emptyColumnRecords.length) {
      // params.params.dbLinkConfUuid = emptyColumnRecords[0].value;
      _this.$widget.addDataSourceParams({ dbLinkConfUuid: emptyColumnRecords[0].value });
    }
  }
}

export default SelectDataSourceTableDevelopment;
