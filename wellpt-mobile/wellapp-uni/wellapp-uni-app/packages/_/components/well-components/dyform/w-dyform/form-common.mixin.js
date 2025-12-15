const DataStore = require("wellapp-uni-framework").DataStore;
import { storage, utils } from "wellapp-uni-framework";
export default {
  data() {
    return {
      editMode: this.widget.configuration.editMode || {},
      optionType: this.widget.configuration.options ? this.widget.configuration.options.type : undefined,
      optionDataAutoSet: this.widget.configuration.optionDataAutoSet, // 选项自动设置（联动）
      relateKey: this.widget.configuration.relateKey, // 联动字段
      relateValue: undefined, // 联动值
      intGtZero: /^[1-9]\d*$/,
      optionsCacheKey: undefined, // 字典、数据仓库等缓存key
    };
  },
  methods: {
    isNumber(value) {
      return !isNaN(parseFloat(value)) && isFinite(value);
    },
    // 是否正整数
    isIntGtZero(count) {
      return this.intGtZero.test(count) ? count : null;
    },
    // 改变正整数
    changeIntGtZero(value, key) {
      if (this.isIntGtZero(value)) {
        this.widget.configuration[key] = Number(value);
      } else {
        this.widget.configuration[key] = null;
      }
    },
    /*
      增加选中数规则
      max 最大选中数
      min 最小选中数
    */
    addSelectedNumRules({ max, min }) {
      if (max || min) {
        this.customRules.push({
          trigger: "change",
          validateFunction: (rule, value, data, callback) => {
            // 未选择，或者选择数小于最少个数
            let curValue = this.formData[this.widget.configuration.code];
            if (!curValue) {
              if (min) {
                callback(this.$t("widgetDyform.leastNumber", { min }, "请选择至少" + min + "项"));
              } else {
                callback();
              }
              return;
            }
            curValue = Array.isArray(curValue)
              ? curValue
              : curValue.split(this.widget.configuration.tokenSeparators || ";");
            if (min && (!curValue || (curValue && curValue.length < min))) {
              callback(this.$t("widgetDyform.leastNumber", { min }, "请选择至少" + min + "项"));
            } else if (max && curValue && curValue.length > max) {
              callback(this.$t("widgetDyform.mostNumber", { max }, "最多可选" + max + "项"));
            } else {
              callback();
            }
          },
        });
      }
    },
    // 监听联动值变化 （监听表单值变化,用于联动选项处理）
    listenRelateChanged(callback) {
      this.form.handleEvent("formDataChanged", () => {
        const relateValue = this.form.getFieldValue(this.relateKey); // 获取主表单值
        if (this.relateValue != relateValue) {
          this.relateValue = relateValue;
          if (typeof callback === "function") {
            callback();
          }
        }
      });
    },
    // 数据仓库
    getDataSourceProvider(options, loadDataUrl, loadDataCntUrl) {
      if (this.dataSourceProvider) {
        return this.dataSourceProvider;
      } else {
        let pageSize = this.pageSize || 10;
        let option = {
          dataStoreId: options.dataSourceId,
          onDataChange: function (data, count, params) {},
          receiver: this,
          defaultCriterions: options.defaultCondition //默认条件
            ? [
                {
                  sql: options.defaultCondition,
                },
              ]
            : [],
          pageSize: pageSize * 1000, // FIXME: 下拉数据多的情况下，会明显卡顿，是否增加分页加载数据(需要引导用户知道需要滚动加载更多)
        };
        if (loadDataUrl != undefined) {
          option.loadDataUrl = loadDataUrl;
        }
        if (loadDataCntUrl != undefined) {
          option.loadDataCntUrl = loadDataCntUrl;
        }
        this.dataSourceProvider = new DataStore(option);
        return this.dataSourceProvider;
      }
    },
    addDataSourceParams(params) {
      if (this.dataSourceProvider) {
        for (let k in params) {
          this.dataSourceProvider.addParam(k, params[k]);
        }
      }
    },
    clearDataSourceParams() {
      if (this.dataSourceProvider) {
        this.dataSourceProvider.clearParams();
      }
    },
    deleteDataSourceParams(...key) {
      if (this.dataSourceProvider) {
        for (let k of key) {
          this.dataSourceProvider.removeParam(k);
        }
      }
    },

    // 数据仓库获取数据
    getLabelValueOptionByDataSource(options, callback) {
      let _this = this;
      let key = utils.md5(JSON.stringify(options));
      this.optionsCacheKey = key;
      // 多次重复调用，去缓存获取，有则返回，无则排队等待调用完成后被回调
      storage.getStorageCache(
        key,
        () => {
          return new Promise((resolve, reject) => {
            let dataSourceProvider = this.dataSourceProvider;
            if (!this.dataSourceProvider) {
              dataSourceProvider = this.getDataSourceProvider(options);
            }
            dataSourceProvider.load().then((data) => {
              let o = [],
                labels = [];
              if (data.data && data.data.length) {
                for (let i = 0, len = data.data.length; i < len; i++) {
                  let label =
                    data.data[i][options.dataSourceLabelColumn] != undefined
                      ? data.data[i][options.dataSourceLabelColumn].trim()
                      : "";
                  o.push({
                    value: data.data[i][options.dataSourceValueColumn],
                    label: label,
                  });
                  if (label != "") {
                    labels.push(label);
                  }
                }
              }
              let then = function () {
                let cacheData = {
                  options: o,
                  results: data.data,
                };
                resolve(cacheData, key);
                if (typeof callback === "function") {
                  callback(o, data.data, key);
                }
              };
              if (options.autoTranslate && _this.$i18n.locale !== "zh_CN") {
                _this.$i18n.$translate(_this, labels, "zh", _this.$i18n.locale.split("_")[0]).then((map) => {
                  for (let i of o) {
                    if (map[i.label]) {
                      i.label = map[i.label];
                    }
                  }
                  then();
                });
              } else {
                then();
              }
            });
          });
        },
        (data) => {
          if (typeof callback === "function") {
            callback(data.options, data.results, key);
          }
          if (!this.dataSourceProvider) {
            this.dataSourceProvider = this.getDataSourceProvider(options);
          }
        }
      );
    },
    getLabelValueOptionByDataModel(options, callback) {
      let _this = this;
      let key = utils.md5(JSON.stringify(options));
      this.optionsCacheKey = key;
      // 多次重复调用，去缓存获取，有则返回，无则排队等待调用完成后被回调
      storage.getStorageCache(
        key,
        () => {
          return new Promise((resolve, reject) => {
            let dataSourceProvider = this.dataSourceProvider;
            if (!this.dataSourceProvider) {
              dataSourceProvider = this.getDataSourceProvider(options, `/api/dm/loadData/${options.dataModelUuid}`);
            }
            dataSourceProvider.load().then((data) => {
              let o = [],
                labels = [];
              if (data.data && data.data.length) {
                for (let i = 0, len = data.data.length; i < len; i++) {
                  let label =
                    data.data[i][options.dataSourceLabelColumn] != undefined
                      ? data.data[i][options.dataSourceLabelColumn].trim()
                      : "";
                  o.push({
                    value: data.data[i][options.dataSourceValueColumn],
                    label,
                  });
                  if (label != "") {
                    labels.push(label);
                  }
                }
              }
              let then = function () {
                let cacheData = {
                  options: o,
                  results: data.data,
                };
                resolve(cacheData, key);
                if (typeof callback === "function") {
                  callback(o, data.data, key);
                }
              };
              if (options.autoTranslate && _this.$i18n.locale !== "zh_CN") {
                _this.$i18n.$translate(labels, "zh", _this.$i18n.locale.split("_")[0]).then((map) => {
                  for (let i of o) {
                    if (map[i.label]) {
                      i.label = map[i.label];
                    }
                  }
                  then();
                });
              } else {
                then();
              }
            });
          });
        },
        (data) => {
          if (typeof callback === "function") {
            callback(data.options, data.results, key);
          }
          if (!this.dataSourceProvider) {
            this.dataSourceProvider = this.getDataSourceProvider(options, `/api/dm/loadData/${options.dataModelUuid}`);
          }
        }
      );
    },
    convertTextValueOptions(options, textField = "label", valueField = "value") {
      let result = [];
      if (options) {
        for (let o of options) {
          result.push({
            text: this.$t(o.id, o[textField]),
            value: o[valueField],
          });
        }
        return result;
      }
      return options;
    },
    getLabelValueOptionByDataDic(dataDicUuid, callback) {
      let key = `LabelOptionByDataDic:${dataDicUuid}`;
      this.optionsCacheKey = key;
      storage.getStorageCache(
        key,
        () => {
          return new Promise((resolve, reject) => {
            this.$axios
              .post("/json/data/services", {
                serviceName: "cdDataDictionaryFacadeService",
                methodName: "listLocaleItemByDictionaryCode",
                // methodName: "listItemByDictionaryCode",
                args: JSON.stringify([dataDicUuid]),
              })
              .then(({ data }) => {
                let options = [];
                if (data.code == 0 && data.data) {
                  options = data.data;
                }
                resolve(options);
                if (typeof callback == "function") {
                  callback(options);
                }
              });
          });
        },
        (data) => {
          if (typeof callback === "function") {
            callback(data, key);
          }
        }
      );
    },
    // 移除字典缓存
    removeLabelOptionByDataDic(dataDicUuid, callback) {
      let key = `LabelOptionByDataDic:${dataDicUuid}`;
      storage.removeStorageCache(key).then(() => {
        // 移除后重新获取
        this.getLabelValueOptionByDataDic(dataDicUuid, callback);
      });
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    // 联动常量
    relateSelfDefine(arg) {
      let _options = [];
      const relateList = arg.configuration.relateList;
      const value = arg.relateValue;
      let optionsList = [];
      for (let index = 0; index < relateList.length; index++) {
        const item = relateList[index];
        if (item.condition == "eq" && value == item.relateVal) {
          optionsList = optionsList.concat(item.options);
        } else if (item.condition == "ne" && value != item.relateVal) {
          optionsList = optionsList.concat(item.options);
        } else if (item.condition == "lt" && value && Number(value) < Number(item.relateVal)) {
          optionsList = optionsList.concat(item.options);
        } else if (item.condition == "gt" && value && Number(value) > Number(item.relateVal)) {
          optionsList = optionsList.concat(item.options);
        } else if (item.condition == "le" && value && Number(value) <= Number(item.relateVal)) {
          optionsList = optionsList.concat(item.options);
        } else if (item.condition == "ge" && value && Number(value) >= Number(item.relateVal)) {
          optionsList = optionsList.concat(item.options);
        } else if (item.condition == "like" && typeof value === "string" && value.indexOf(item.relateVal) > -1) {
          optionsList = optionsList.concat(item.options);
        } else if (item.condition == "nlike" && typeof value === "string" && value.indexOf(item.relateVal) == -1) {
          optionsList = optionsList.concat(item.options);
        }
      }
      if (optionsList.length) {
        optionsList = Array.from(new Set(optionsList));
        const defineOptions = arg.configuration.options.defineOptions;
        _options = defineOptions.filter((item) => {
          return optionsList.includes(item.value);
        });
      }
      arg.callback(_options);
    },
    // 根据被联动参数的值来获取数据字典
    fetchDataDict(value) {
      if (!value) {
        return;
      }
      this.$axios
        .post("/json/data/services", {
          serviceName: "cdDataDictionaryFacadeService",
          methodName: "listItemByDictionaryCode",
          args: JSON.stringify([value]), // 传数据字典的type
        })
        .then(({ data }) => {
          console.log(data.data);
        });
    },
    // 刷新选项
    refreshOptions(options) {
      if (this.optionType === "selfDefine" || this.optionType === "dataDictionary") {
        if (options.eventParams && options.eventParams.options) {
          if (typeof this.getSelectOptionByValue === "function") {
            this.getSelectOptionByValue(options.eventParams.options);
          }
          return false;
        }
      }
      // 默认清除缓存,如果是从表字段，参数传removeCache:true时才会进行清除缓存，如果相同搜索条件时，请在第一次调用时传入removeCache:true
      if (
        this.optionsCacheKey &&
        (!this.$attrs.isSubformCell ||
          (this.$attrs.isSubformCell && options && options.eventParams && options.eventParams.removeCache))
      ) {
        storage.removeStorageCache(this.optionsCacheKey);
      }
      if (options && options.eventParams && options.eventParams.hasOwnProperty("removeCache")) {
        delete options.eventParams.removeCache;
      }
      if (options != undefined && options.params != undefined) {
        this.addDataSourceParams(options.params);
      }
      if (options != undefined && options.$evtWidget) {
        // 通过组件事件派发进来的逻辑
        if (!isEmpty(options.eventParams)) {
          this.addDataSourceParams(options.eventParams);
        }

        if (options.wEventParams) {
          // 修改默认查询条件
          if (this.dataSourceProvider && options.wEventParams.defaultCondition != undefined) {
            this.dataSourceProvider.setDefaultCriterions(
              options.wEventParams.defaultCondition
                ? [
                    {
                      sql: options.wEventParams.defaultCondition,
                    },
                  ]
                : []
            );
          }
        }
      }
      //重新加载备选项
      if (this.refetchOption && typeof this.refetchOption == "function") {
        this.refetchOption();
      }
    },
  },
};
