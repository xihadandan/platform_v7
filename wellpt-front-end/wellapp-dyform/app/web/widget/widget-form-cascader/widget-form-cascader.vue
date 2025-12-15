<template>
  <a-form-model-item
    v-show="!hidden"
    :prop="formModelItemProp"
    :rules="rules"
    :ref="widget.configuration.code"
    :label="itemLabel"
    :colon="displayAsLabel"
    :class="widgetClass"
  >
    <template v-if="!displayAsLabel">
      <a-cascader
        ref="cascaderRef"
        v-model="value"
        :allowClear="allowClear"
        :changeOnSelect="changeOnSelect"
        :showSearch="showSearch ? { render: renderFilteredOption } : false"
        :options="treeData"
        :placeholder="widget.configuration.placeholder"
        :getPopupContainer="getPopupContainer()"
        :disabled="disable || readonly"
        :expandTrigger="expandTrigger"
        :popupClassName="dropdownClassName"
        :fieldNames="fieldNames"
        :displayRender="displayRender"
        :popupVisible="popupVisible"
        @popupVisibleChange="onPopupVisibleChange"
        @search="onSelectSearch"
        @change="onSelectChange"
        @focus="onSelectFocus"
        @blur="onSelectBlur"
      >
        <a-icon type="loading" slot="suffixIcon" v-if="loading" />
      </a-cascader>
    </template>
    <span v-show="displayAsLabel" class="textonly" :title="selectedLabel">{{ selectedLabel }}</span>
  </a-form-model-item>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formCommonMixin from '../mixin/form-common.mixin';
import DataSourceBase from '@pageAssembly/app/web/assets/js/commons/dataSource.base.js';
import { orderBy, kebabCase, map, each, isArray, findIndex, debounce, template as stringTemplate } from 'lodash';
import md5 from '@framework/vue/utils/md5';
import { swapArrayElements, deepClone, jsonataEvaluate, evaluateConvertJsonDataFromSchema } from '@framework/vue/utils/util';
import './css/index.less';
export default {
  extends: FormElement,
  name: 'WidgetFormCascader',
  mixins: [widgetMixin, formCommonMixin],
  data() {
    return {
      treeData: [],
      treeDatas: [], // 全部的树选项数据
      value: undefined,
      expandTrigger: this.widget.configuration.editMode.expandTrigger,
      changeOnSelect: this.widget.configuration.editMode.changeOnSelect,
      allowClear: this.widget.configuration.editMode.allowClear,
      showSearch: this.widget.configuration.editMode.showSearch,
      tokenSeparators: this.widget.configuration.tokenSeparators || '',
      valueSeparators: '/',
      gradualLoad: false && this.widget.configuration.editMode.gradualLoad, // 不要逐级加载
      popupVisible: undefined,
      dropdownClassName: kebabCase(this.widget.wtype) + ' ps__child--consume', //阻止外层下拉滚动
      optionsValueMap: {},
      selectOptions: [],
      selectedLabels: [], // 选中label数组
      valueLabelMap: {},
      searchLoading: false,
      loading: false,
      treeOptionParams: {}, // 树形下拉框数据仓库搜索参数
      treeOptionDefaultCondition: undefined, // 树形下拉框数据仓库默认筛选条件
      cacheKey: undefined,
      fieldNames: { value: 'opt_value', label: 'opt_label', children: 'children' },
      popupOpen: false,
      intervalSetRealValue: undefined
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    // 选中label字符串
    selectedLabel() {
      return this.selectedLabels.join(this.tokenSeparators);
    },
    dataSourceDataMapping() {
      return this.widget.configuration.options.dataSourceDataMapping;
    },
    defaultEvents() {
      let eventParams = [];
      if (this.designMode) {
        if (this.optionType == 'dataSource' || this.optionType == 'dataModel') {
          eventParams.push({
            paramKey: 'removeCache',
            remark: '在从表里，该值为是（真值）会清除缓存，主表里默认清除缓存',
            valueSource: {
              inputType: 'select',
              options: [
                { label: '是', value: true },
                { label: '否', value: false }
              ]
            }
          });
        }
      }
      return [
        {
          id: 'refetchOption',
          title: '重新加载备选项',
          eventParams
        }
      ];
    }
  },
  created() {
    if (!this.designMode) {
      this.init();
    }
  },
  methods: {
    setValue(v, validate = true) {
      this.value = v;
      if (v && typeof v === 'string') {
        this.value = v.split(this.valueSeparators);
      } else if (!v) {
        this.value = [];
      }

      this.formData[this.fieldCode] = Array.isArray(v) ? v.join(this.valueSeparators) : v;

      // 字典逐级加载，需判断当前值的项是否加载，否则显示不出来
      if (this.optionType === 'dataDictionary' && this.gradualLoad && this.value.length) {
        this.getAllLevelDataBaseOnValue();
      }

      this.setSelectedLabels();
      this.setRelatedValue();
      this.clearValidate();
      this.emitChange({}, validate);
    },
    refreshOptions(options) {
      this.refetchOption(options);
    },
    refetchOption(options) {
      // 默认清除缓存,如果是从表字段，参数传removeCache:true时才会进行清除缓存，如果相同搜索条件时，请在第一次调用时传入removeCache:true
      if (
        this.cacheKey &&
        (!this.$attrs.isSubformCell || (this.$attrs.isSubformCell && options && options.eventParams && options.eventParams.removeCache))
      ) {
        this.removeCacheByKey(this.cacheKey);
      }
      if (options && options.eventParams && options.eventParams.hasOwnProperty('removeCache')) {
        delete options.eventParams.removeCache;
      }
      if (options != undefined && options.params != undefined) {
        this.addDataSourceParams(options.params);
      }
      if (options != undefined && options.$evtWidget) {
        // 通过组件事件派发进来的逻辑
        if (options.eventParams) {
          this.addDataSourceParams(options.eventParams);
        }

        if (options.wEventParams) {
          // 修改默认查询条件
          if (options.wEventParams.defaultCondition != undefined) {
            if (this.componentType == 'select-tree') {
              this.treeOptionDefaultCondition = options.wEventParams.defaultCondition || undefined;
            } else {
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
      }
      this.fetchTreeData();
    },

    init() {
      if (this.optionDataAutoSet && this.relateKey) {
        this.listenRelateChanged(() => {
          this.checkRelateValue();
        });
      }
      this.setValue(this.formData[this.fieldCode], false); // 设初始值
      if (!this.optionDataAutoSet) {
        this.fetchTreeData();
      }
    },
    fetchTreeData() {
      const options = this.widget.configuration.options;
      this.loading = true;
      if (options.type == 'selfDefine') {
        this.loading = false;
        this.getTreeDataBySelfDefine();
      } else if (options.type == 'dataDictionary') {
        this.fetchTreeDataByDataDic(options.dataDictionaryUuid);
      } else if (options.type == 'dataSource') {
        this.fetchTreeDataByDataSource(options);
      } else if (options.type == 'dataModel') {
        this.fetchTreeDataByDataSource(options, true);
      } else if (options.type == 'apiLinkService') {
        this.fetchTreeDataByApiLink(options);
      }
    },
    // 获取自定义下拉树数据
    getTreeDataBySelfDefine() {
      this.treeData = this.reSetTreeData(this.widget.configuration.treeData);
      this.treeDatas = deepClone(this.treeData);
      this.setValueLabelMap(this.treeData);
      this.setSelectedLabels();
      this.setRelatedValue();
      this.emitChange({}, false);
    },
    // 获取树形下拉框 数据仓库备选项
    fetchTreeDataByDataSource(options, isDataModel, e) {
      this.fetched = false;
      this.loading = true;
      options = deepClone(options);
      options.defaultCondition = this.treeOptionDefaultCondition === undefined ? options.defaultCondition : this.treeOptionDefaultCondition;
      options.params = this.treeOptionParams;
      let { defaultCondition, params } =
        e != undefined
          ? e
          : this.explainDefaultCondition(options.defaultCondition, data => {
              this.$tempStorage.removeCache(this.cacheKey).then(() => {
                this.fetchTreeDataByDataSource(options, isDataModel, data);
              });
            });
      options.defaultCondition = defaultCondition;
      options.params = { ...(options.params || {}), ...params };
      this.cacheKey = md5(JSON.stringify({ options }) + this.dataSourceProviderQueryHash || '');
      this.$tempStorage.getCache(
        this.cacheKey,
        () => {
          return new Promise((resolve, reject) => {
            let requiredOption = {
              serviceName: 'cdDataStoreService',
              methodName: 'loadTreeNodes',
              args: JSON.stringify([
                {
                  dataStoreId: options.dataSourceId,
                  uniqueColumn: options.dataSourceKeyColumn,
                  parentColumn: options.dataSourceParentColumn,
                  displayColumn: options.dataSourceLabelColumn,
                  valueColumn: options.dataSourceValueColumn,
                  defaultCondition: options.defaultCondition,
                  params: options.params,
                  async: false
                }
              ])
            };
            if (isDataModel) {
              requiredOption = {
                serviceName: 'dataModelService',
                methodName: 'loadTreeNodes',
                args: JSON.stringify([
                  {
                    dataModelUuid: options.dataModelUuid,
                    uniqueColumn: options.dataSourceKeyColumn,
                    parentColumn: options.dataSourceParentColumn,
                    displayColumn: options.dataSourceLabelColumn,
                    valueColumn: options.dataSourceValueColumn,
                    defaultCondition: options.defaultCondition,
                    params: options.params,
                    async: false
                  }
                ])
              };
            }
            $axios
              .post('/json/data/services', requiredOption)
              .then(({ data }) => {
                if (data.code == 0) {
                  if (options.autoTranslate) {
                    let words = [];
                    let cascadeMap = list => {
                      for (let l of list) {
                        let data = l.data;
                        words.push(data[options.dataSourceLabelColumn]);
                        if (options.dataSourceExtendColumn) {
                          words.push(data[options.dataSourceExtendColumn]);
                        }
                        if (l.children && l.children.length) {
                          cascadeMap(l.children);
                        }
                      }
                    };
                    cascadeMap(data.data);
                    this.$translate(words, 'zh', this.$i18n.locale.split('_')[0]).then(result => {
                      let setTranslateLabel = list => {
                        for (let l of list) {
                          let data = l.data;
                          if (result[l.name]) {
                            l.name = result[l.name];
                            data[options.dataSourceLabelColumn] = l.name;
                          }
                          if (options.dataSourceExtendColumn && result[data[options.dataSourceExtendColumn]]) {
                            data[options.dataSourceExtendColumn] = result[data[options.dataSourceExtendColumn]];
                          }
                          if (l.children && l.children.length) {
                            setTranslateLabel(l.children);
                          }
                        }
                      };
                      setTranslateLabel(data.data);
                      resolve(data.data || []);
                    });
                  } else {
                    resolve(data.data || []);
                  }
                }
                this.fetched = true;
              })
              .catch(e => {
                console.error(e);
              });
          });
        },
        results => {
          this.loading = false;
          this.treeData = this.reSetTreeData(results);
          this.treeDatas = deepClone(this.treeData);
          this.fetched = true;
          this.setValueLabelMap(this.treeData);
          this.setSelectedLabels();
          this.setRelatedValue();
          this.emitChange({}, false);
        }
      );
    },
    compileDefaultCondition(defaultCondition) {
      let compiler = stringTemplate(defaultCondition);
      let sql = defaultCondition;
      try {
        let data = {};
        if (this.form) {
          Object.assign(data, {
            FORM_DATA: this.form.formData
          });
        }
        sql = compiler(data);
      } catch (error) {
        console.error('解析模板字符串错误: ', error);
        throw new Error('默认条件变量解析异常');
      }
      return sql != undefined ? sql.trim() : undefined;
    },

    explainDefaultCondition(defaultCondition, afterConditionChangeCallback) {
      // 表单字段联动条件
      let params = {},
        _this = this;
      if (defaultCondition && defaultCondition.includes('FORM_DATA.') && this.lastDefaultCondition != defaultCondition) {
        // 根据表单字段数据联动的条件
        let extractFormDataVariables = function (string) {
          const regex = /FORM_DATA\.([a-zA-Z_][a-zA-Z0-9_]*)/g;
          const matches = [...string.matchAll(regex)];
          return [...new Set(matches.map(match => match[1]))];
        };
        let variables = extractFormDataVariables(defaultCondition);
        let resolveVariables = formData => {
          let values = [],
            condition = defaultCondition,
            params = {};
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
                paramKey = paramKey + `#DATE(${field.getFixedFormat})`;
              }
            }
            condition = condition.replace(new RegExp(':FORM_DATA\.' + v, 'g'), ':FORM_DATA_' + v);
            params[paramKey] = formData[v];
          }
          return { condition, params };
        };

        if (variables.length) {
          const dataSourceProviderWatchDepFormDataChange = debounce(function (newValue) {
            let { condition, params } = resolveVariables(newValue);
            let hash = md5(JSON.stringify({ params, condition }));
            if (_this.dataSourceProviderQueryHash != hash) {
              _this.dataSourceProviderQueryHash = hash;
              if (typeof afterConditionChangeCallback === 'function') {
                afterConditionChangeCallback({ defaultCondition: _this.compileDefaultCondition(condition), params });
              }
            }
          }, 600);

          if (this.unwatchConditionFormData == undefined) {
            this.unwatchConditionFormData = this.$watch(
              'formData',
              (newValue, oldValue) => {
                dataSourceProviderWatchDepFormDataChange(newValue);
              },
              { deep: true }
            );
          }

          let result = resolveVariables(this.formData);
          defaultCondition = result.condition;
          params = result.params;
        }
        _this.dataSourceProviderQueryHash = md5(JSON.stringify({ params, defaultCondition }));
      }

      return { defaultCondition: _this.compileDefaultCondition(defaultCondition), params };
    },
    fetchTreeDataByApiLink(options) {
      let _this = this;
      this.fetchDataByApiLinkInvocation(options.apiInvocationConfig).then(results => {
        let list = results != undefined ? (Array.isArray ? results : [results]) : [];
        if (list.length > 0) {
          let _continue = () => {
            this.loading = false;
            this.treeData = this.reSetTreeData(list);
            this.treeDatas = deepClone(_this.treeData);
            this.fetched = true;
            this.setValueLabelMap(_this.treeData);
            this.setSelectedLabels();
            this.setRelatedValue();
            this.emitChange({}, false);
          };
          if (_this.$i18n.locale !== 'zh_CN' && options.autoTranslate) {
            let words = [];
            let cascadeMap = list => {
              for (let l of list) {
                words.push(l.name);
                if (l.subTitle) {
                  words.push(l.subTitle);
                }
                if (l.children && l.children.length) {
                  cascadeMap(l.children);
                }
              }
            };
            cascadeMap(list);
            _this.$translate(words, 'zh', _this.$i18n.locale.split('_')[0]).then(result => {
              let setTranslateLabel = list => {
                for (let l of list) {
                  if (result[l.name]) {
                    l.name = result[l.name];
                  }

                  if (l.subTitle != undefined && result[l.subTitle]) {
                    l.subTitle = result[l.subTitle];
                  }

                  if (l.children && l.children.length) {
                    setTranslateLabel(l.children);
                  }
                }
              };
              setTranslateLabel(list);
              _continue();
            });
          } else {
            _continue();
          }
        }
      });
    },
    // 判断联动方式
    checkRelateValue() {
      if (this.optionType === 'selfDefine') {
        this.relateTreeSelfDefine();
      }
      if (this.optionType === 'dataDictionary') {
        this.fetchTreeDataByDataDic(this.relateValue);
      }
    },
    // 树形常量联动
    relateTreeSelfDefine() {
      this.treeData = [];
      const relateList = this.widget.configuration.relateList;
      const value = this.relateValue;
      let treeData = this.reSetTreeData(this.widget.configuration.treeData);
      const selectOptions = this.relateToOptions({ relateList, value });
      if (selectOptions.length) {
        this.treeData = this.filterSearchTree(treeData, node => {
          if (selectOptions.includes(node.id)) {
            return true;
          }
          return false;
        });
      }
      this.setValueLabelMap(treeData);
      this.setSelectedLabels();
      this.setRelatedValue();
      this.emitChange({}, false);
    },
    // 获取树形下拉框 数据字典备选项
    fetchTreeDataByDataDic(dataDicUuid) {
      this.getLabelValueOptionByDataDic(
        dataDicUuid,
        (results, key) => {
          this.loading = false;
          this.treeData = this.reSetTreeData(results);
          this.treeDatas = deepClone(this.treeData);
          // 逐级加载，且当前值大于一级
          if (this.gradualLoad && this.value && this.value.length > 1) {
            this.getAllLevelDataBaseOnValue();
          } else {
            this.setValueLabelMap(this.treeData);
            this.setSelectedLabels();
            this.setRelatedValue();
            this.emitChange({}, false);
          }
          if (key) {
            this.cacheKey = key;
          }
        },
        !this.gradualLoad // 是否加载全部，逐级加载为true时加载一级
      );
    },
    // 逐级加载，根据当前值获取当前值所在层级的字典数据
    getAllLevelDataBaseOnValue() {
      // 获取到的字典数据已有一层，从第二层开始取
      const tree2Map = (index, parentIndexArr) => {
        let treeData = this.treeData;
        each(parentIndexArr, item => {
          treeData = treeData[item].children;
        });
        let hasIndex = findIndex(treeData, { opt_value: this.value[index] });
        if (hasIndex > -1) {
          if (this.valueLabelMap[this.value[index + 1]]) {
            // 下级已加载
            if (this.value.length - 1 > index + 1) {
              // 还有下级
              parentIndexArr.push(hasIndex);
              tree2Map(index + 1, parentIndexArr);
            }
          } else {
            this.getLabelValueOptionByParentItemUuid(
              treeData[hasIndex].uuid,
              this.value.length - 1 > index + 1
                ? () => {
                    parentIndexArr.push(hasIndex);
                    tree2Map(index + 1, parentIndexArr);
                  }
                : undefined
            );
          }
        }
      };
      tree2Map(0, []);
    },
    getLabelValueOptionByParentItemUuid(parentItemUuid, callback) {
      this.LabelOptionByParentItemUuidFromDataDic(parentItemUuid, (results, key) => {
        this.treeData = this.reSetTreeData(this.treeDatas, parentItemUuid, results);
        this.treeDatas = deepClone(this.treeData);
        if (typeof callback == 'function') {
          callback();
        } else {
          this.setValueLabelMap(this.treeData);
          this.setSelectedLabels();
          this.setRelatedValue();
          this.emitChange({}, false);
        }
      });
    },
    reSetTreeData(treeData, parentItemUuid, addOptions) {
      this.selectOptions.splice(0, this.selectOptions.length);
      const tree2Map = (data, level) => {
        data = this.sortTreeData(data);
        return map(data, child => {
          child.opt_level = level;
          child.opt_key = child.id || child.uuid;
          child.opt_value = child.real || child.value || child.opt_key;
          child.opt_title = child.display || child.name || child.label;
          if (this.optionType == 'selfDefine') {
            // 自定义取配置的国际化值
            child.opt_title = this.$t(child.id, child.opt_title);
          }
          this.selectOptions.push({
            label: child.opt_title,
            value: child.opt_value
          });
          // 所有选项data值
          this.optionsValueMap[child.opt_value] = child.data || child;
          if (this.widget.configuration.options.dataSourceExtendColumn) {
            child.opt_extend = child.data[this.widget.configuration.options.dataSourceExtendColumn];
          }
          if (!child.opt_label) {
            child.opt_label = child.opt_title;
          }
          // 如果当前层级大于等于设置的数据显示层级，则没有下级
          if (this.widget.configuration.datalevel && level >= this.widget.configuration.datalevel) {
            child.isLeaf = true;
            delete child.children;
          } else if (child.children && child.children.length) {
            child.isLeaf = false;
            each(child.children, c_child => {
              c_child.opt_title = c_child.display || c_child.name || c_child.label;
            });
            child.children = tree2Map(child.children, level + 1);
          } else {
            if (this.optionType === 'dataDictionary' && this.gradualLoad && child.parent) {
              if (parentItemUuid == child.uuid && addOptions) {
                child.children = addOptions;
                each(child.children, c_child => {
                  c_child.opt_title = c_child.display || c_child.name || c_child.label;
                });
                child.children = tree2Map(child.children, level + 1);
              }
              child.isLeaf = false;
            } else {
              delete child.children;
            }
          }
          return child;
        });
      };
      return tree2Map(treeData, 1);
    },
    sortTreeData(data) {
      if (this.widget.configuration.options.type == 'dataSource') {
        if (this.widget.configuration.options.sortField) {
          return orderBy(
            data,
            item => {
              return item.data[this.widget.configuration.options.sortField];
            },
            [this.widget.configuration.options.sortType || 'asc']
          );
        }
      }
      return data;
    },
    setSelectedLabels() {
      this.selectedLabels.splice(0, this.selectedLabels.length);
      let values = this.value;
      if (values == undefined) {
        return [];
      }
      if (typeof values == 'string') {
        values = values.split(this.valueSeparators);
      }
      for (let i = 0, len = values.length; i < len; i++) {
        if (this.valueLabelMap[values[i]] != undefined) {
          this.selectedLabels.push(this.valueLabelMap[values[i]]);
        }
      }
      return this.selectedLabels;
    },
    setValueLabelMap(options) {
      const tree2Map = data => {
        data.map(child => {
          this.valueLabelMap[child.opt_value] = child.opt_label;
          if (child.children) {
            tree2Map(child.children);
          }
        });
      };
      tree2Map(options);
    },
    setRelatedValue() {
      let value = this.value;
      if (this.widget.configuration.displayValueField && this.selectedLabels.length) {
        let displayValue = this.configuration.editMode.allPath
          ? this.selectedLabel
          : this.selectedLabels.length
          ? this.selectedLabels[this.selectedLabels.length - 1]
          : '';
        // 显示值字段
        this.form.setFieldValue(this.widget.configuration.displayValueField, displayValue);
      }
      // 设置关联值
      if ((this.optionType == 'dataSource' || this.optionType == 'dataModel') && this.fetched && this.dataSourceDataMapping.length > 0) {
        for (let i = 0, len = this.dataSourceDataMapping.length; i < len; i++) {
          let allVal = [];
          if (value) {
            if (typeof value == 'string') {
              if (this.optionsValueMap[value] && this.optionsValueMap[value][this.dataSourceDataMapping[i].sourceField]) {
                allVal.push(this.optionsValueMap[value][this.dataSourceDataMapping[i].sourceField] || null);
              }
            } else {
              each(value, item => {
                if (this.optionsValueMap[item] && this.optionsValueMap[item][this.dataSourceDataMapping[i].sourceField]) {
                  allVal.push(this.optionsValueMap[item][this.dataSourceDataMapping[i].sourceField] || null);
                }
              });
            }
          }

          this.form.setFieldValue(this.dataSourceDataMapping[i].targetField, allVal.length ? allVal[allVal.length - 1] : null);
        }
      }
      if (this.intervalSetRealValue) {
        clearInterval(this.intervalSetRealValue);
      }

      if (this.widget.configuration.textWidgetId && this.dyform.$textset && this.dyform.$textset[this.widget.configuration.textWidgetId]) {
        let realVal = this.formData[this.fieldCode] ? this.formData[this.fieldCode].split(this.valueSeparators) : '';
        if (realVal) {
          if (!this.configuration.editMode.valueAllPath && realVal.length) {
            realVal = realVal[realVal.length - 1];
          } else {
            realVal = realVal.join(this.valueSeparators);
          }
        }
        // 在文本中显示真实值
        this.form.setText(this.widget.configuration.textWidgetId, realVal);
      } else if (this.widget.configuration.textWidgetId && !this.$attrs.isSubformCell) {
        // 从表时不显示
        this.intervalSetRealValue = setInterval(() => {
          this.setRelatedValue();
        }, 1000);
      }
    },
    displayValue(value) {
      // 通过value换label
      if (value != undefined) {
        let labels = [];
        let values = value;
        if (typeof value == 'string') {
          values = value.split(this.valueSeparators);
        }

        for (let i = 0, len = values.length; i < len; i++) {
          if (this.valueLabelMap[values[i]] != undefined) {
            labels.push(this.valueLabelMap[values[i]]);
          }
        }
        return labels.join(this.tokenSeparators);
      }
      return this.selectedLabel;
    },
    // 根据条件过滤出树结构
    filterSearchTree(nodes, predicate, wrapMatchFn = () => false) {
      // 如果已经没有节点了，结束递归
      if (!(nodes && nodes.length)) {
        return undefined;
      }
      const newChildren = [];
      for (let i = 0; i < nodes.length; i++) {
        const node = nodes[i];
        // 想要截止匹配的那一层（如果有匹配的则不用递归了，直接取下面所有的子节点）
        if (wrapMatchFn(node) && predicate(node)) {
          newChildren.push(node);
          continue;
        }
        const subs = this.filterSearchTree(node.children, predicate, wrapMatchFn);

        // 以下两个条件任何一个成立，当前节点都应该加入到新子节点集中
        // 1. 子孙节点中存在符合条件的，即 subs 数组中有值
        // 2. 自己本身符合条件
        if ((subs && subs.length) || predicate(node)) {
          if (subs) {
            node.children = subs;
          }
          newChildren.push(node);
        }
      }
      return newChildren.length ? newChildren : [];
    },
    // 联动常量-根据联动项、联动条件获取备选项
    relateToOptions({ relateList, value }) {
      let selectOptions = [];
      for (let index = 0; index < relateList.length; index++) {
        const item = relateList[index];
        if (item.condition == 'eq' && value == item.relateVal) {
          selectOptions = selectOptions.concat(item.options);
        } else if (item.condition == 'ne' && value != item.relateVal) {
          selectOptions = selectOptions.concat(item.options);
        } else if (item.condition == 'lt' && Number(value) < Number(item.relateVal)) {
          selectOptions = selectOptions.concat(item.options);
        } else if (item.condition == 'gt' && Number(value) > Number(item.relateVal)) {
          selectOptions = selectOptions.concat(item.options);
        } else if (item.condition == 'le' && Number(value) <= Number(item.relateVal)) {
          selectOptions = selectOptions.concat(item.options);
        } else if (item.condition == 'ge' && Number(value) >= Number(item.relateVal)) {
          selectOptions = selectOptions.concat(item.options);
        } else if (item.condition == 'like' && typeof value === 'string' && value.indexOf(item.relateVal) > -1) {
          selectOptions = selectOptions.concat(item.options);
        } else if (item.condition == 'nlike' && typeof value === 'string' && value.indexOf(item.relateVal) == -1) {
          selectOptions = selectOptions.concat(item.options);
        }
      }
      if (selectOptions.length) {
        selectOptions = Array.from(new Set(selectOptions));
      }
      return selectOptions;
    },
    addDataSourceParams(params) {
      for (let k in params) {
        this.treeOptionParams[k] = params[k];
      }
    },
    clearDataSourceParams() {
      this.treeOptionParams = {};
    },
    deleteDataSourceParams(...key) {
      for (let k of key) {
        delete this.treeOptionParams[k];
      }
    },
    // 有key则返回key对应参数，无key返回全部参数
    getDataSourceParam(key) {
      return key ? this.treeOptionParams[key] : this.treeOptionParams;
    },
    onSelectChange(value, opt) {
      if (this.optionType === 'dataDictionary' && this.gradualLoad && opt) {
        // 属于逐级加载的
        let lastOpt = opt[opt.length - 1];
        if (lastOpt.parent && lastOpt.children && lastOpt.children.length === 0) {
          this.getLabelValueOptionByParentItemUuid(lastOpt.uuid);
          if (!this.changeOnSelect) {
            this.value = [];
            return false;
          }
        }
      }
      this.selectedLabels.splice(0, this.selectedLabels.length);
      this.selectedOptions = [];
      if (value == '' || value == undefined || value == null || value.length == 0) {
        this.formData[this.fieldCode] = null;
      }
      if (opt) {
        if (Array.isArray(opt)) {
          for (let i = 0, len = opt.length; i < len; i++) {
            const label = opt[i][this.fieldNames.label];
            this.selectedLabels.push(label);
            this.selectedOptions.push({ label, value: opt[i][this.fieldNames.value] });
          }
          this.formData[this.fieldCode] = value.join(this.valueSeparators);
        } else {
          const label = opt[i][this.fieldNames.label];
          this.selectedLabels.push(label);
          this.selectedOptions.push({ label, value });
          this.formData[this.fieldCode] = value;
        }
      }
      this.setRelatedValue();
      this.emitChange();
    },
    onSelectSearch(value) {},
    dataSourceLoadEveryTime() {
      // 来源是数据仓库-每次点击选择框重新加载数据
      if (
        (this.widget.configuration.options.type == 'dataSource' || this.widget.configuration.options.type == 'dataModel') &&
        this.widget.configuration.options.dataSourceLoadEveryTime
      ) {
        this.reloadTreeData();
      }
    },
    onSelectBlur() {
      this.popupVisible = false;
    },
    onSelectFocus() {
      this.popupVisible = true;
      if (!this.popupOpen) {
        this.dataSourceLoadEveryTime();
      }
      if (this.changeOnSelect && this.expandTrigger === 'hover') {
        // 父级可选且悬停展开时，点击父级时修改e.type = 'click'
        let $children = this.$refs.cascaderRef.$children[0];
        let handleMenuSelect = $children.handleMenuSelect;
        $children.handleMenuSelect = (targetOption, menuIndex, e) => {
          let newEvent = e;
          if (isArray(newEvent) && newEvent.length > 0) {
            newEvent = {
              type: e[0].type
            };
            if (newEvent.type === 'mouseenter') {
              newEvent.type = 'click';
            }
          }
          handleMenuSelect(targetOption, menuIndex, newEvent);
        };
      }
    },
    onPopupVisibleChange(open) {
      if (!open) {
        this.$refs.cascaderRef.inputValue = '';
      }
      this.popupOpen = open;
    },
    reloadTreeData() {
      const options = this.widget.configuration.options;
      // 重新加载树数据，先清缓存
      this.removeCacheByKey(this.cacheKey).then(() => {
        if (options.type == 'dataSource') {
          this.fetchTreeDataByDataSource(options);
        } else if (options.type == 'dataModel') {
          this.fetchTreeDataByDataSource(options, true);
        }
      });
    },
    displayRender({ labels, selectedOptions }) {
      return labels.join(this.tokenSeparators);
    },
    renderFilteredOption({ inputValue, path }) {
      var h = this.$createElement;
      let pathNode = path.map((option, index) => {
        var label = option[this.fieldNames.label];
        var node = label.indexOf(inputValue) > -1 ? this.$refs.cascaderRef.highlightKeyword(label, inputValue, 'ant-cascader') : label;
        return index === 0 ? node : [this.tokenSeparators, node];
      });
      let title = this.renderFilteredOptionTitle({ path });
      return h('span', { attrs: { class: 'ant-cascader-menu-item-filter', title } }, pathNode);
    },
    renderFilteredOptionTitle({ path }) {
      let title = path.map((option, index) => {
        var label = option[this.fieldNames.label];
        return index === 0 ? label : [this.tokenSeparators, label].join('');
      });
      return title.join('');
    },
    getPopupContainer() {
      return triggerNode => {
        if (triggerNode.closest('.ps')) {
          if (triggerNode.closest('.ps').clientHeight < 400) {
            // 页面高度小于400时，挂载到body下
            return document.body;
          } else {
            return triggerNode.closest('.ps');
          }
        } else if (triggerNode.closest('.widget-subform')) {
          return triggerNode.closest('.widget-subform');
        }
        return triggerNode.parentNode;
      };
    }
  },
  mounted() {}
};
</script>
