import md5 from '@framework/vue/utils/md5';
import DataSourceBase from '@pageAssembly/app/web/assets/js/commons/dataSource.base.js';
import { isEmpty, template as stringTemplate, cloneDeep, debounce, mixin } from 'lodash';
import defaultFieldEventMixins from './defaultFieldEventMixins';
export default {
  mixins: [defaultFieldEventMixins],
  data() {
    return {
      editMode: this.widget.configuration.editMode || {},
      optionType: this.widget.configuration.options ? this.widget.configuration.options.type : undefined,
      optionDataAutoSet: this.widget.configuration.optionDataAutoSet, // 选项自动设置（联动）
      relateKey: this.widget.configuration.relateKey, // 联动字段
      relateValue: undefined, // 联动值
      intGtZero: /^[1-9]\d*$/,
      optionsCacheKey: undefined // 字典、数据仓库等缓存key
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
        this.rules.push({
          trigger: 'change',
          validator: (rule, value, callback) => {
            // 未选择，或者选择数小于最少个数
            let curValue = this.value || value;
            if (!curValue) {
              if (min != undefined) {
                callback(this.$t('WidgetFormSelect.selectedMin', { num: min }));
              } else {
                callback();
              }
              return;
            }
            curValue = Array.isArray(curValue) ? curValue : curValue.split(this.configuration.tokenSeparators || ';');
            if (min && (!curValue || (curValue && curValue.length < min))) {
              callback(this.$t('WidgetFormSelect.selectedMin', { num: min }));
            } else if (max && curValue && curValue.length > max) {
              callback(this.$t('WidgetFormSelect.selectedMax', { num: max }));
            } else {
              callback();
            }
          }
        });
      }
    },
    // 监听联动值变化 （监听表单值变化,用于联动选项处理）
    listenRelateChanged(callback) {
      this.form.handleEvent('formDataChanged', () => {
        const relateValue = this.form.getFieldValue(this.relateKey); // 获取主表单值
        if (this.relateValue != relateValue) {
          this.relateValue = relateValue;
          if (typeof callback === 'function') {
            callback();
          }
        }
      });
    },
    convertDefaultConditionSqlArray(defaultCondition) {
      if (defaultCondition) {
        let compiler = stringTemplate(defaultCondition);
        let sql = defaultCondition;
        try {
          // 在表单域内
          let data = cloneDeep(this.vPageState || {});
          if (this.form) {
            Object.assign(data, {
              FORM_DATA: this.form.formData
            });
          }
          sql = compiler(data);
        } catch (error) {
          console.error('解析模板字符串错误: ', error);
          throw new Error('表格默认条件变量解析异常');
        }
        return sql != undefined && sql.trim() !== '' ? [{ sql: sql.trim() }] : [];
      }
      return [];
    },
    // 数据仓库
    getDataSourceProvider(options, loadDataUrl, loadDataCntUrl, afterConditionChangeCallback) {
      if (this.dataSourceProvider) {
        return this.dataSourceProvider;
      } else {
        let pageSize = this.pageSize || 10;
        let option = {
            dataStoreId: options.dataSourceId,
            onDataChange: function (data, count, params) {},
            receiver: this,
            pageSize: pageSize * 1000 // FIXME: 下拉数据多的情况下，会明显卡顿，是否增加分页加载数据(需要引导用户知道需要滚动加载更多)
          },
          _this = this;

        if (loadDataUrl != undefined) {
          option.loadDataUrl = loadDataUrl;
        }
        if (loadDataCntUrl != undefined) {
          option.loadDataCntUrl = loadDataCntUrl;
        }
        this.dataSourceProvider = new DataSourceBase(option);
        let defaultCondition = options.defaultCondition;
        // 表单字段联动条件
        if (options.defaultCondition && options.defaultCondition.includes('FORM_DATA.')) {
          // 根据表单字段数据联动的条件
          let extractFormDataVariables = function (string) {
            const regex = /FORM_DATA\.([a-zA-Z_][a-zA-Z0-9_]*)/g;
            const matches = [...string.matchAll(regex)];
            return [...new Set(matches.map(match => match[1]))];
          };
          let variables = extractFormDataVariables(options.defaultCondition);
          let resolveVariables = formData => {
            let values = [],
              condition = options.defaultCondition;
            if (formData) {
              for (let v of variables) {
                values.push(formData[v]);
              }
            }
            for (let v of variables) {
              // 传递表单变量命名参数
              let field = _this.form.getField(v);
              let paramKey = 'FORM_DATA_' + v;
              if (field) {
                if (field.widget.wtype == 'WidgetFormDatePicker') {
                  // 传递格式化
                  paramKey = paramKey + `#DATE(${field.contentFormat})`;
                }
              }
              condition = condition.replace(new RegExp(':FORM_DATA.' + v, 'g'), ':FORM_DATA_' + v);
              _this.dataSourceProvider.addParam(paramKey, formData[v]);
            }
            return condition;
          };
          if (variables.length) {
            const dataSourceProviderWatchDepFormDataChange = debounce(function (newValue) {
              let condition = resolveVariables(newValue);
              let hash = md5(JSON.stringify({ params: _this.dataSourceProvider.options.params, defaultCondition: condition }));
              if (_this.dataSourceProvider.dataSourceProviderQueryHash != hash) {
                _this.dataSourceProvider.dataSourceProviderQueryHash = hash;
                _this.dataSourceProvider.setDefaultCriterions(_this.convertDefaultConditionSqlArray(condition));
                if (typeof afterConditionChangeCallback === 'function') {
                  afterConditionChangeCallback();
                }
              }
            }, 600);

            this.$watch(
              'formData',
              (newValue, oldValue) => {
                dataSourceProviderWatchDepFormDataChange(newValue);
              },
              { deep: true }
            );

            defaultCondition = resolveVariables(this.formData);
          }
          _this.dataSourceProvider.dataSourceProviderQueryHash = md5(
            JSON.stringify({ params: _this.dataSourceProvider.options.params, defaultCondition })
          );
        }

        this.dataSourceProvider.setDefaultCriterions(this.convertDefaultConditionSqlArray(defaultCondition));
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
      if (!_this.dataSourceProvider) {
        _this.getDataSourceProvider(options, options.loadDataUrl, options.loadDataCountUrl, () => {
          _this.$tempStorage.removeCache(_this.optionsCacheKey).then(() => {
            _this.getLabelValueOptionByDataSource(options, callback);
          });
        });
      }
      let key = md5(JSON.stringify(options) + _this.dataSourceProvider.dataSourceProviderQueryHash || '');
      this.optionsCacheKey = key;
      // 多次重复调用，去缓存获取，有则返回，无则排队等待调用完成后被回调
      if (typeof options.loading == 'function') {
        options.loading(true);
      }

      this.$tempStorage.getCache(
        key,
        () => {
          return new Promise((resolve, reject) => {
            _this.dataSourceProvider
              .load()
              .then(data => {
                let o = [],
                  labels = [];
                if (data.data && data.data.length) {
                  for (let i = 0, len = data.data.length; i < len; i++) {
                    let label =
                      data.data[i][options.dataSourceLabelColumn] != undefined ? data.data[i][options.dataSourceLabelColumn].trim() : '';
                    o.push({
                      value: data.data[i][options.dataSourceValueColumn],
                      label
                    });
                    if (label != '') {
                      labels.push(label);
                    }
                  }
                }
                let then = function () {
                  let cacheData = {
                    options: o,
                    results: data.data
                  };
                  resolve(cacheData, key);
                  if (typeof callback === 'function') {
                    callback(o, data.data, key);
                  }
                };
                if (options.autoTranslate && _this.$i18n.locale !== 'zh_CN') {
                  _this.$translate(labels, 'zh', _this.$i18n.locale.split('_')[0]).then(map => {
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
              })
              .catch(e => {
                if (typeof options.loading == 'function') {
                  options.loading(false);
                }
                resolve({
                  options: [],
                  results: []
                });
              });
          });
        },
        data => {
          if (typeof options.loading == 'function') {
            options.loading(false);
          }
          if (typeof callback === 'function') {
            callback(data.options, data.results, key);
          }
        }
      );
    },
    getLabelValueOptionByDataModel(options, callback) {
      let _this = this;
      if (!this.dataSourceProvider) {
        this.getDataSourceProvider(
          options,
          `/proxy/api/dm/loadData/${options.dataModelUuid}`,
          '/proxy/api/dm/loadDataCount/' + options.dataModelUuid,
          () => {
            _this.$tempStorage.removeCache(_this.optionsCacheKey).then(() => {
              _this.getLabelValueOptionByDataModel(options, callback);
            });
          }
        );
      }
      let key = md5(JSON.stringify(options) + this.dataSourceProvider.dataSourceProviderQueryHash || '');
      this.optionsCacheKey = key;

      // 多次重复调用，去缓存获取，有则返回，无则排队等待调用完成后被回调
      this.$tempStorage.getCache(
        key,
        () => {
          return new Promise((resolve, reject) => {
            _this.dataSourceProvider
              .load()
              .then(data => {
                let o = [],
                  labels = [];
                if (data.data && data.data.length) {
                  for (let i = 0, len = data.data.length; i < len; i++) {
                    let label =
                      data.data[i][options.dataSourceLabelColumn] != undefined ? data.data[i][options.dataSourceLabelColumn].trim() : '';
                    o.push({
                      value: data.data[i][options.dataSourceValueColumn],
                      label
                    });
                    if (label != '') {
                      labels.push(label);
                    }
                  }
                }
                let then = function () {
                  let cacheData = {
                    options: o,
                    results: data.data
                  };
                  resolve(cacheData, key);
                  if (typeof callback === 'function') {
                    callback(o, data.data, key);
                  }
                };
                if (options.autoTranslate && _this.$i18n.locale !== 'zh_CN') {
                  _this.$translate(labels, 'zh', _this.$i18n.locale.split('_')[0]).then(map => {
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
              })
              .catch(e => {
                if (typeof options.loading == 'function') {
                  options.loading(false);
                }
                resolve({
                  options: [],
                  results: []
                });
              });
          });
        },
        data => {
          if (typeof callback === 'function') {
            callback(data.options, data.results, key);
          }
        }
      );
    },
    getLabelValueOptionByDataDic(dataDicUuid, callback) {
      let key = `LabelOptionByDataDic:${dataDicUuid}`;
      this.optionsCacheKey = key;
      this.$tempStorage.getCache(
        key,
        () => {
          return new Promise((resolve, reject) => {
            $axios
              .post('/json/data/services', {
                serviceName: 'cdDataDictionaryFacadeService',
                methodName: 'listLocaleItemByDictionaryCode',
                args: JSON.stringify([dataDicUuid])
              })
              .then(({ data }) => {
                let options = [];
                if (data.code == 0 && data.data) {
                  options = data.data;
                }
                resolve(options, key);
              });
          });
        },
        data => {
          if (typeof callback === 'function') {
            callback(data, key);
          }
        }
      );
    },

    // 移除字典缓存
    removeLabelOptionByDataDic(dataDicUuid, callback) {
      let key = `LabelOptionByDataDic:${dataDicUuid}`;
      this.$tempStorage.removeItem(key, data => {
        delete this.$tempStorage._queue[key];
        // 移除后重新获取
        this.getLabelValueOptionByDataDic(dataDicUuid, callback);
      });
    },
    // 清除缓存
    removeCacheByKey(key) {
      return this.$tempStorage != undefined
        ? this.$tempStorage.removeCache(key)
        : new Promise((resolve, reject) => {
            resolve();
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
        if (item.condition == 'eq' && value == item.relateVal) {
          optionsList = optionsList.concat(item.options);
        } else if (item.condition == 'ne' && value != item.relateVal) {
          optionsList = optionsList.concat(item.options);
        } else if (item.condition == 'lt' && value && Number(value) < Number(item.relateVal)) {
          optionsList = optionsList.concat(item.options);
        } else if (item.condition == 'gt' && value && Number(value) > Number(item.relateVal)) {
          optionsList = optionsList.concat(item.options);
        } else if (item.condition == 'le' && value && Number(value) <= Number(item.relateVal)) {
          optionsList = optionsList.concat(item.options);
        } else if (item.condition == 'ge' && value && Number(value) >= Number(item.relateVal)) {
          optionsList = optionsList.concat(item.options);
        } else if (item.condition == 'like' && typeof value === 'string' && value.indexOf(item.relateVal) > -1) {
          optionsList = optionsList.concat(item.options);
        } else if (item.condition == 'nlike' && typeof value === 'string' && value.indexOf(item.relateVal) == -1) {
          optionsList = optionsList.concat(item.options);
        }
      }
      if (optionsList.length) {
        optionsList = Array.from(new Set(optionsList));
        const defineOptions = arg.configuration.options.defineOptions;
        _options = defineOptions.filter(item => {
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
        .post('/json/data/services', {
          serviceName: 'cdDataDictionaryFacadeService',
          methodName: 'listLocaleItemByDictionaryCode',
          args: JSON.stringify([value]) // 传数据字典的type
        })
        .then(({ data }) => {
          console.log(data.data);
        });
    },
    // 刷新选项
    refreshOptions(options) {
      if (this.optionType === 'selfDefine' || this.optionType === 'dataDictionary') {
        if (options.eventParams && options.eventParams.options) {
          if (typeof this.getSelectOptionByValue === 'function') {
            this.getSelectOptionByValue(options.eventParams.options);
          }
          return false;
        }
      }
      // 默认清除缓存,如果是从表字段，参数传removeCache:true时才会进行清除缓存，如果相同搜索条件时，请在第一次调用时传入removeCache:true
      if (
        this.optionsCacheKey &&
        (!this.$attrs.isSubformCell || (this.$attrs.isSubformCell && options && options.eventParams && options.eventParams.removeCache))
      ) {
        this.removeCacheByKey(this.optionsCacheKey);
      }
      if (options && options.eventParams && options.eventParams.hasOwnProperty('removeCache')) {
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
                      sql: options.wEventParams.defaultCondition
                    }
                  ]
                : []
            );
          }
        }
      }
      //重新加载备选项
      if (this.refetchOption && typeof this.refetchOption == 'function') {
        this.refetchOption();
      }
    }
  }
};
