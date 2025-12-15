const DataStore = require("./index.js");
/**
 * 徽章：BootstrapTableViewGetCount
 * 通过数量统计JS脚本
 * 表格视图的数据总数
 */
let TableViewGetCount = function () {};
TableViewGetCount.prototype.getCount = function (listViewId, callback, params) {
  let _self = this;
  uni.request({
    service: "appContextService.getAppWidgetDefinitionById",
    data: [listViewId, null],
    success: function (result) {
      _self.listViewWidgetDefinition = JSON.parse(result.data.data.definitionJson);
      _self.configuration = _self.listViewWidgetDefinition && _self.listViewWidgetDefinition.configuration;
      _self.getDataProvider(callback, params);
    },
  });
};
TableViewGetCount.prototype.getDataProvider = function (callback, params) {
  let _self = this;
  _self._dataProvider = new DataStore({
    type: _self.configuration.type,
    dataStoreId: _self.getDataStoreId(),
    onDataChange: function (data, count) {
      callback(params, count);
    },
    defaultCriterions: _self.getDefaultConditions(),
    autoCount: true,
    pageSize: _self.configuration.pageSize || 20,
  });
  _self._dataProvider.getCount(true);
  // _self._dataProvider.load();
};
/**
 * 获取默认条件查询
 */
TableViewGetCount.prototype.getDefaultConditions = function () {
  let _self = this;
  var defaultConditions = [];
  var configuration = _self.configuration;
  if (!_.isEmpty(configuration.defaultCondition)) {
    var criterion = {};
    criterion.sql = configuration.defaultCondition;
    defaultConditions.push(criterion);
  }
  return defaultConditions;
};
/**
 * 获取数据源ID
 */
TableViewGetCount.prototype.getDataStoreId = function () {
  return this.configuration.dataStoreId || this.configuration.dataSourceId;
};
module.exports = TableViewGetCount;
