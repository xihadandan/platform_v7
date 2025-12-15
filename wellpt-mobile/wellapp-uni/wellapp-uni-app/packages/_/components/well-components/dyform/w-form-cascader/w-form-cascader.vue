<template>
  <uni-forms-item
    :label="itemLabel"
    :name="formModelItemProp"
    :label-position="widget.configuration.labelPosition"
    :ref="fieldCode"
    :class="widgetClass"
    :style="itemStyle"
  >
    <template v-if="!displayAsLabel">
      <uni-w-cascader
        :placeholder="$t('placeholder', widget.configuration.placeholder)"
        :popup-title="$t('placeholder', widget.configuration.placeholder)"
        :localdata="treeData"
        :split="tokenSeparators"
        :clearIcon="allowClear"
        :border="bordered"
        :changeOnSelect="changeOnSelect"
        :showSearch="showSearch"
        :readonly="disable || readonly"
        v-model="value"
        :map="{ text: 'opt_label', value: 'opt_value' }"
        ref="cascaderRef"
        @change="onSelectChange"
        @nodeclick="onNodeClick"
        @popupopen="onPopupOpen"
      >
      </uni-w-cascader>
    </template>
    <view v-show="displayAsLabel" class="textonly" :title="selectedLabel">{{ selectedLabel }}</view>
  </uni-forms-item>
</template>
<style lang="scss">
/* #ifdef APP-PLUS */
@import "./css/index.scss";
/* #endif */
</style>
<script type="text/babel">
import formElement from "../w-dyform/form-element.mixin";
import formCommonMixin from "../w-dyform/form-common.mixin";
import { utils, storage } from "wellapp-uni-framework";
import { orderBy, kebabCase, map, each, isArray, findIndex } from "lodash";
// #ifndef APP-PLUS
import "./css/index.scss";
// #endif

export default {
  mixins: [formElement, formCommonMixin],
  data() {
    return {
      treeData: [],
      treeDatas: [], // 全部的树选项数据
      value: undefined,
      expandTrigger: this.widget.configuration.editMode.expandTrigger,
      changeOnSelect: this.widget.configuration.editMode.changeOnSelect,
      allowClear: this.widget.configuration.editMode.allowClear,
      showSearch: this.widget.configuration.editMode.showSearch,
      tokenSeparators: this.widget.configuration.tokenSeparators || "",
      valueSeparators: "/",
      bordered: this.widget.configuration.uniConfiguration
        ? this.widget.configuration.uniConfiguration.bordered
        : false,
      gradualLoad: false && this.widget.configuration.editMode.gradualLoad, // 不要逐级加载
      popupVisible: undefined,
      dropdownClassName: kebabCase(this.widget.wtype) + " ps__child--consume", //阻止外层下拉滚动
      optionsValueMap: {},
      selectOptions: [],
      selectedLabels: [], // 选中label数组
      valueLabelMap: {},
      searchLoading: false,
      treeOptionParams: {}, // 树形下拉框数据仓库搜索参数
      treeOptionDefaultCondition: undefined, // 树形下拉框数据仓库默认筛选条件
      cacheKey: undefined,
      fieldNames: { value: "opt_value", label: "opt_label", children: "children" },
      popupOpen: false,
      intervalSetRealValue: undefined,
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
      if (this.optionType == "dataSource" || this.optionType == "dataModel") {
        eventParams.push({
          paramKey: "removeCache",
          remark: "在从表里，该值为是（真值）会清除缓存，主表里默认清除缓存",
          valueSource: {
            inputType: "select",
            options: [
              { label: "是", value: true },
              { label: "否", value: false },
            ],
          },
        });
      }
      return [
        {
          id: "refetchOption",
          title: "重新加载备选项",
          eventParams,
        },
      ];
    },
  },
  created() {
    if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
      this.$set(this.widget.configuration, "uniConfiguration", { bordered: false });
    }
    this.init();
  },
  methods: {
    setValue(v, validate = true) {
      this.value = v;
      if (v && typeof v === "string") {
        this.value = v.split(this.valueSeparators);
      } else if (!v) {
        this.value = [];
      }

      this.formData[this.fieldCode] = Array.isArray(v) ? v.join(this.valueSeparators) : v;

      // 字典逐级加载，需判断当前值的项是否加载，否则显示不出来
      if (this.optionType === "dataDictionary" && this.gradualLoad && this.value.length) {
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
        (!this.$attrs.isSubformCell ||
          (this.$attrs.isSubformCell && options && options.eventParams && options.eventParams.removeCache))
      ) {
        storage.removeStorageCache(this.cacheKey);
      }
      if (options && options.eventParams && options.eventParams.hasOwnProperty("removeCache")) {
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
            if (this.componentType == "select-tree") {
              this.treeOptionDefaultCondition = options.wEventParams.defaultCondition || undefined;
            } else {
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
      if (options.type == "selfDefine") {
        this.getTreeDataBySelfDefine();
      } else if (options.type == "dataDictionary") {
        this.fetchTreeDataByDataDic(options.dataDictionaryUuid);
      } else if (options.type == "dataSource") {
        this.fetchTreeDataByDataSource(options);
      } else if (options.type == "dataModel") {
        this.fetchTreeDataByDataSource(options, true);
      } else if (options.type == "apiLinkService") {
        this.fetchTreeDataByApiLink(options);
      }
    },
    // 获取自定义下拉树数据
    getTreeDataBySelfDefine() {
      this.treeData = this.reSetTreeData(this.widget.configuration.treeData);
      this.treeDatas = utils.deepClone(this.treeData);
      this.setValueLabelMap(this.treeData);
      this.setSelectedLabels();
      this.setRelatedValue();
      this.emitChange({}, false);
    },
    // 获取树形下拉框 数据仓库备选项
    fetchTreeDataByDataSource(options, isDataModel) {
      this.fetched = false;
      options = utils.deepClone(options);
      options.defaultCondition =
        this.treeOptionDefaultCondition === undefined ? options.defaultCondition : this.treeOptionDefaultCondition;
      options.params = this.treeOptionParams;
      const key = `cascaderNodesByDataSource:${options.dataSourceId}:${utils.md5(JSON.stringify(options))}`;
      this.cacheKey = key;
      storage.getStorageCache(
        key,
        () => {
          return new Promise((resolve, reject) => {
            let requiredOption = {
              serviceName: "cdDataStoreService",
              methodName: "loadTreeNodes",
              args: JSON.stringify([
                {
                  dataStoreId: options.dataSourceId,
                  uniqueColumn: options.dataSourceKeyColumn,
                  parentColumn: options.dataSourceParentColumn,
                  displayColumn: options.dataSourceLabelColumn,
                  valueColumn: options.dataSourceValueColumn,
                  defaultCondition: options.defaultCondition,
                  params: options.params,
                  async: false,
                },
              ]),
            };
            if (isDataModel) {
              requiredOption = {
                serviceName: "dataModelService",
                methodName: "loadTreeNodes",
                args: JSON.stringify([
                  {
                    dataModelUuid: options.dataModelUuid,
                    uniqueColumn: options.dataSourceKeyColumn,
                    parentColumn: options.dataSourceParentColumn,
                    displayColumn: options.dataSourceLabelColumn,
                    valueColumn: options.dataSourceValueColumn,
                    defaultCondition: options.defaultCondition,
                    params: options.params,
                    async: false,
                  },
                ]),
              };
            }
            this.$axios
              .post("/json/data/services", requiredOption)
              .then(({ data }) => {
                if (data.code == 0) {
                  if (options.autoTranslate) {
                    let words = [];
                    let cascadeMap = (list) => {
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
                    this.$i18n.$translate(words, "zh", this.$i18n.locale.split("_")[0]).then((result) => {
                      let setTranslateLabel = (list) => {
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
              .catch((e) => {
                console.error(e);
              });
          });
        },
        (results) => {
          this.treeData = this.reSetTreeData(results);
          this.treeDatas = utils.deepClone(this.treeData);
          this.fetched = true;
          this.setValueLabelMap(this.treeData);
          this.setSelectedLabels();
          this.setRelatedValue();
          this.emitChange({}, false);
        }
      );
    },
    fetchTreeDataByApiLink(options) {
      let _this = this;
      this.fetchDataByApiLinkInvocation(options.apiInvocationConfig).then((results) => {
        let list = results != undefined ? (Array.isArray ? results : [results]) : [];
        if (list.length > 0) {
          let _continue = () => {
            this.treeData = this.reSetTreeData(list);
            this.treeDatas = deepClone(_this.treeData);
            this.fetched = true;
            this.setValueLabelMap(_this.treeData);
            this.setSelectedLabels();
            this.setRelatedValue();
            this.emitChange({}, false);
          };
          if (_this.$i18n.locale !== "zh_CN" && options.autoTranslate) {
            let words = [];
            let cascadeMap = (list) => {
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
            _this.$i18n.$translate(words, "zh", _this.$i18n.locale.split("_")[0]).then((result) => {
              let setTranslateLabel = (list) => {
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
      if (this.optionType === "selfDefine") {
        this.relateTreeSelfDefine();
      }
      if (this.optionType === "dataDictionary") {
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
        this.treeData = this.filterSearchTree(treeData, (node) => {
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
          this.treeData = this.reSetTreeData(results);
          this.treeDatas = utils.deepClone(this.treeData);
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
        each(parentIndexArr, (item) => {
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
        this.treeDatas = utils.deepClone(this.treeData);
        if (typeof callback == "function") {
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
        return map(data, (child) => {
          child.opt_level = level;
          child.opt_key = child.id || child.uuid;
          child.opt_value = child.real || child.value || child.opt_key;
          child.opt_title = child.display || child.name || child.label;
          child.isleaf = !child.children || child.children.length == 0;
          if (this.optionType == "selfDefine") {
            // 自定义取配置的国际化值
            child.opt_title = this.$t(child.id, child.opt_title);
          }
          this.selectOptions.push({
            label: child.opt_title,
            value: child.opt_value,
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
            child.isleaf = true;
            delete child.children;
          } else if (child.children && child.children.length) {
            child.isleaf = false;
            each(child.children, (c_child) => {
              c_child.opt_title = c_child.display || c_child.name || c_child.label;
            });
            child.children = tree2Map(child.children, level + 1);
          } else {
            if (this.optionType === "dataDictionary" && this.gradualLoad && child.parent) {
              if (parentItemUuid == child.uuid && addOptions) {
                child.children = addOptions;
                each(child.children, (c_child) => {
                  c_child.opt_title = c_child.display || c_child.name || c_child.label;
                });
                child.children = tree2Map(child.children, level + 1);
              }
              child.isleaf = false;
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
      if (this.widget.configuration.options.type == "dataSource") {
        if (this.widget.configuration.options.sortField) {
          return orderBy(
            data,
            (item) => {
              return item.data[this.widget.configuration.options.sortField];
            },
            [this.widget.configuration.options.sortType || "asc"]
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
      if (typeof values == "string") {
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
      const tree2Map = (data) => {
        data.map((child) => {
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
      if (this.widget.configuration.displayValueField) {
        let displayValue = this.widget.configuration.editMode.allPath
          ? this.selectedLabel
          : this.selectedLabels.length
          ? this.selectedLabels[this.selectedLabels.length - 1]
          : "";
        // 显示值字段
        this.form.setFieldValue(this.widget.configuration.displayValueField, displayValue);
      }
      // 设置关联值
      if (
        (this.optionType == "dataSource" || this.optionType == "dataModel") &&
        this.fetched &&
        this.dataSourceDataMapping.length > 0
      ) {
        for (let i = 0, len = this.dataSourceDataMapping.length; i < len; i++) {
          let allVal = [];
          if (value) {
            if (typeof value == "string") {
              if (
                this.optionsValueMap[value] &&
                this.optionsValueMap[value][this.dataSourceDataMapping[i].sourceField]
              ) {
                allVal.push(this.optionsValueMap[value][this.dataSourceDataMapping[i].sourceField] || null);
              }
            } else {
              each(value, (item) => {
                if (
                  this.optionsValueMap[item] &&
                  this.optionsValueMap[item][this.dataSourceDataMapping[i].sourceField]
                ) {
                  allVal.push(this.optionsValueMap[item][this.dataSourceDataMapping[i].sourceField] || null);
                }
              });
            }
          }

          this.form.setFieldValue(
            this.dataSourceDataMapping[i].targetField,
            allVal.length ? allVal[allVal.length - 1] : null
          );
        }
      }
      if (this.intervalSetRealValue) {
        clearInterval(this.intervalSetRealValue);
      }

      if (
        this.widget.configuration.textWidgetId &&
        this.dyform.$textset &&
        this.dyform.$textset[this.widget.configuration.textWidgetId]
      ) {
        let realVal = this.formData[this.fieldCode] ? this.formData[this.fieldCode].split(this.valueSeparators) : "";
        if (realVal) {
          if (!this.widget.configuration.editMode.valueAllPath && realVal.length) {
            realVal = realVal[realVal.length - 1];
          } else {
            realVal = realVal.join(this.valueSeparators);
          }
        }
        // 在文本中显示真实值
        this.form.setText(this.widget.configuration.textWidgetId, realVal);
      } else if (this.widget.configuration.textWidgetId) {
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
        if (typeof value == "string") {
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
        if (item.condition == "eq" && value == item.relateVal) {
          selectOptions = selectOptions.concat(item.options);
        } else if (item.condition == "ne" && value != item.relateVal) {
          selectOptions = selectOptions.concat(item.options);
        } else if (item.condition == "lt" && Number(value) < Number(item.relateVal)) {
          selectOptions = selectOptions.concat(item.options);
        } else if (item.condition == "gt" && Number(value) > Number(item.relateVal)) {
          selectOptions = selectOptions.concat(item.options);
        } else if (item.condition == "le" && Number(value) <= Number(item.relateVal)) {
          selectOptions = selectOptions.concat(item.options);
        } else if (item.condition == "ge" && Number(value) >= Number(item.relateVal)) {
          selectOptions = selectOptions.concat(item.options);
        } else if (item.condition == "like" && typeof value === "string" && value.indexOf(item.relateVal) > -1) {
          selectOptions = selectOptions.concat(item.options);
        } else if (item.condition == "nlike" && typeof value === "string" && value.indexOf(item.relateVal) == -1) {
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
      if (this.optionType === "dataDictionary" && this.gradualLoad && opt) {
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
      if (value == "" || value == undefined || value == null || value.length == 0) {
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
    onNodeClick(value, opt) {},
    onPopupOpen() {
      this.dataSourceLoadEveryTime();
    },
    dataSourceLoadEveryTime() {
      // 来源是数据仓库-每次点击选择框重新加载数据
      if (
        (this.widget.configuration.options.type == "dataSource" ||
          this.widget.configuration.options.type == "dataModel") &&
        this.widget.configuration.options.dataSourceLoadEveryTime
      ) {
        this.reloadTreeData();
      }
    },
    reloadTreeData() {
      const options = this.widget.configuration.options;
      // 重新加载树数据，先清缓存
      storage.removeStorageCache(this.cacheKey).then(() => {
        if (options.type == "dataSource") {
          this.fetchTreeDataByDataSource(options);
        } else if (options.type == "dataModel") {
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
        var node =
          label.indexOf(inputValue) > -1
            ? this.$refs.cascaderRef.highlightKeyword(label, inputValue, "ant-cascader")
            : label;
        return index === 0 ? node : [this.tokenSeparators, node];
      });
      let title = this.renderFilteredOptionTitle({ path });
      return h("span", { attrs: { class: "ant-cascader-menu-item-filter", title } }, pathNode);
    },
    renderFilteredOptionTitle({ path }) {
      let title = path.map((option, index) => {
        var label = option[this.fieldNames.label];
        return index === 0 ? label : [this.tokenSeparators, label].join("");
      });
      return title.join("");
    },
    getPopupContainer() {
      return (triggerNode) => {
        if (triggerNode.closest(".ps")) {
          if (triggerNode.closest(".ps").clientHeight < 400) {
            // 页面高度小于400时，挂载到body下
            return document.body;
          } else {
            return triggerNode.closest(".ps");
          }
        } else if (triggerNode.closest(".widget-subform")) {
          return triggerNode.closest(".widget-subform");
        }
        return triggerNode.parentNode;
      };
    },
  },
  mounted() {},
};
</script>
