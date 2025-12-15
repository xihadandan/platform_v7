const _ = require("lodash");
module.exports = {
  props: {
    criterion: Object,
  },
  data() {
    return {
      items: [],
    };
  },
  created() {
    let _self = this;
    let queryOptions = _self.criterion.queryOptions || {};
    let optionType = queryOptions.optionType;
    let optionValue = queryOptions.optionValue || [];
    let options = queryOptions.options || {};
    let items = [];
    // 常量
    if (optionType == "1" || options.type == "selfDefine") {
      if (options.type == "selfDefine") {
        optionValue = _.map(options.defineOptions, (item) => {
          return {
            text: item.label,
            id: item.value,
          };
        });
      }
      _.forEach(optionValue, function (item) {
        items.push({ text: item.text, value: item.id });
      });
      _self.items = items;
    } else if (optionType == "2") {
      // 数据字典
      uni.request({
        service: "dataDictionaryMaintain.getDataDictionariesByParentUuid",
        method: "POST",
        data: [queryOptions.dataDic],
        success: function (result) {
          if (result.data.code == 0) {
            _self.items = _.map(result.data.data, function (data) {
              return {
                value: data.code,
                text: data.name,
              };
            });
          }
        },
      });
    } else if (optionType == "3" || options.type == "dataSource") {
      // 数据仓库
      uni.request({
        service: "viewComponentService.loadAllData",
        method: "POST",
        data: [queryOptions.dataStore || options.dataSourceId],
        success: function (result) {
          if (result.data.code == 0) {
            _self.items = _.map(result.data.data.data, function (data) {
              return {
                value: data[queryOptions.valueColumn || options.dataSourceValueColumn],
                text: data[queryOptions.textColumn || options.dataSourceLabelColumn],
              };
            });
          }
        },
      });
    } else if (options.type == "dataDictionary") {
      // 数据字典
      uni.request({
        service: "cdDataDictionaryFacadeService.listItemByDictionaryCode",
        method: "POST",
        data: [options.dataDictionaryUuid],
        success: function (result) {
          if (result.data.code == 0) {
            _self.items = _.map(result.data.data, function (data) {
              return {
                value: data.value,
                text: data.label,
              };
            });
          }
        },
      });
    }
  },
  methods: {
    getDisplayValue() {
      const _self = this;
      let values = (_self.criterion.value && _self.criterion.value.split(";")) || [];
      let displayValue = [];
      _.forEach(_self.items, function (item) {
        if (values.indexOf(item.value) >= 0) {
          displayValue.push(item.text);
        }
      });
      return displayValue.join(";");
    },
  },
};
