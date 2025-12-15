const DataStore = require("wellapp-uni-framework").DataStore;
export default {
  data() {
    return {
      editMode: this.widget.configuration.editMode || {},
      optionType: this.widget.configuration.options ? this.widget.configuration.options.type : undefined,
      optionDataAutoSet: this.widget.configuration.optionDataAutoSet, // 选项自动设置（联动）
      relateKey: this.widget.configuration.relateKey, // 联动字段
      relateValue: undefined, // 联动值
      intGtZero: /^[1-9]\d*$/,
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
              if (min != undefined) {
                callback("请选择至少" + min + "项");
              } else {
                callback();
              }
              return;
            }
            curValue = Array.isArray(curValue)
              ? curValue
              : curValue.split(this.widget.configuration.tokenSeparators || ";");
            if (min && (!curValue || (curValue && curValue.length < min))) {
              callback("请选择至少" + min + "项");
            } else if (max && curValue && curValue.length > max) {
              callback("最多可选" + max + "项");
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
      let dataSourceProvider = this.dataSourceProvider;
      if (!this.dataSourceProvider) {
        dataSourceProvider = this.getDataSourceProvider(options);
      }
      dataSourceProvider.load().then((data) => {
        let o = [];
        if (data.data && data.data.length) {
          for (let i = 0, len = data.data.length; i < len; i++) {
            o.push({
              value: data.data[i][options.dataSourceValueColumn],
              label: data.data[i][options.dataSourceLabelColumn],
            });
          }
        }
        if (typeof callback === "function") {
          callback(o, data.data);
        }
      });
    },
    getLabelValueOptionByDataModel(options, callback) {
      this.$axios
        .post(`/api/dm/loadData/${options.dataModelUuid}`, {
          criterions: options.defaultCondition != undefined ? [{ sql: options.defaultCondition }] : [],
        })
        .then(({ data }) => {
          let o = [];
          if (data.data.data) {
            for (let i = 0, len = data.data.data.length; i < len; i++) {
              o.push({
                value: data.data.data[i][options.dataSourceValueColumn],
                label: data.data.data[i][options.dataSourceLabelColumn],
              });
            }
          }

          if (typeof callback === "function") {
            callback(o, data.data.data);
          }
        });
    },
    convertTextValueOptions(options, textField = "label", valueField = "value") {
      let result = [];
      if (options) {
        for (let o of options) {
          result.push({
            text: o[textField],
            value: o[valueField],
          });
        }
        return result;
      }
      return options;
    },
    getLabelValueOptionByDataDic(dataDicUuid, callback) {
      this.$axios
        .post("/json/data/services", {
          serviceName: "cdDataDictionaryFacadeService",
          methodName: "listItemByDictionaryCode",
          args: JSON.stringify([dataDicUuid]),
        })
        .then(({ data }) => {
          let options = [];
          if (data.code == 0 && data.data) {
            options = data.data;
          }
          if (typeof callback == "function") {
            callback(options);
          }
        });
    },
    // 移除字典缓存
    removeLabelOptionByDataDic(dataDicUuid, callback) {
      let key = `LabelOptionByDataDic:${dataDicUuid}`;
      this.$tempStorage.removeItem(key, (data) => {
        delete this.$tempStorage._queue[key];
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
  },
};
