define(['commons', 'constant', 'server', 'appContext', 'GetCountBase'], function (commons, constant, server, appContext, GetCountBase) {
  var StringUtils = commons.StringUtils;
  var BootstrapTableViewGetCount = function () {
    GetCountBase.apply(this, arguments);
  };
  commons.inherit(BootstrapTableViewGetCount, GetCountBase, {
    getCount: function (options) {
      var _self = this;
      if (StringUtils.isBlank(options.data.widgetDefId)) {
        console.error('BootstrapTableViewGetCount widgetDefId is null');
        return;
      }

      this.getWidgetDefinition(options.data.widgetDefId, function (widgetDefinition) {
        if (widgetDefinition) {
          var configuration = widgetDefinition.configuration;
          if (StringUtils.isBlank(configuration.dataStoreId)) {
            console.error('BootstrapTableViewGetCount dataStoreId is null');
            return;
          }

          appContext.require(['dataStoreBase'], function (DataStore) {
            var _dataProvider = new DataStore({
              dataStoreId: configuration.dataStoreId,
              defaultCriterions: [].concat(_self.getDefaultConditions(configuration)).concat(options.data.otherConditions),
              params: options.data.params || {}, //添加提供数据仓库查询的参数
              onDataChange: function (data, count) {
                _self.applyCallback(options, count);
              }
            });
            _dataProvider.getCount(true);
          });
        }
      });
    },

    getDefaultConditions: function (configuration) {
      var defaultConditions = [];
      if (commons.StringUtils.isNotBlank(configuration.defaultCondition)) {
        var criterion = {};
        criterion.sql = configuration.defaultCondition;
        defaultConditions.push(criterion);
      }
      return defaultConditions;
    },
    getWidgetDefinition: function (widgetDefId, callback) {
      appContext.getWidgetDefinition(widgetDefId, false, function (widgetDefinition) {
        var definitionJson = null;
        if (widgetDefinition) {
          definitionJson = JSON.parse(widgetDefinition.definitionJson);
        } else {
          definitionJson = {
            configuration: {}
          };
        }
        callback(definitionJson);
      });
    }
  });
  return BootstrapTableViewGetCount;
});
