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
      <uni-w-data-select
        v-if="componentType == 'select' || componentType == 'select-group'"
        :componentType="componentType"
        v-model="value"
        :title="widget.configuration.name"
        :initOptions="options"
        :localdata="selectOptions"
        :disabled="disable || readonly"
        :placeholder="widget.configuration.placeholder"
        :multiple="editMode.selectMultiple"
        :clear="allowClear"
        :showSearch="showSearch"
        :selectCheckAll="editMode.selectCheckAll"
        :tokenSeparators="tokenSeparators"
        :dropType="dropType"
        :moreStatus="!optionsCompletedRendered"
        :groupColumn="widget.configuration.options.groupColumn"
        :bordered="bordered"
        :currentNode="editDictProps && editDictProps.item"
        @change="onSelectChange"
        @getLabel="getSelectedLabel"
        @scrolltolower="onPopupScroll"
        @clearCurrentNode="onClearSelectedDictOption"
      >
        <!-- 数据字典，更多操作 -->
        <view v-if="showMore && optionAdd" slot="operateBar">
          <view
            class="flex f_x_s f_y_c"
            v-if="(componentType == 'select-group' && editDictProps) || componentType !== 'select-group'"
          >
            <uni-w-easyinput v-model="editDictValue"></uni-w-easyinput>
            <view style="padding-left: 10px">
              <button
                type="primary"
                size="mini"
                @tap="onAddOption"
                :disabled="!editDictValue"
                style="vertical-align: middle"
              >
                添加
              </button>
            </view>
          </view>
        </view>
        <template
          v-slot:selectorItem="{ props }"
          v-if="showMore && (optionSort || optionDel || (componentType == 'select-group' && optionAdd))"
        >
          <view
            class="flex options-operate-box"
            v-if="props.selectOptions.length || (componentType == 'select-group' && optionAdd)"
          >
            <w-icon
              class="options-operate"
              v-if="optionSort && props.selectOptions.length - 1 > props.index"
              icon="arrow-down"
              @tap.native.stop="onSortOption(props)"
              title="下移"
              :size="14"
              color="var(--color-primary)"
            ></w-icon>
            <w-icon
              class="options-operate"
              v-if="optionSort && props.index > 0"
              icon="arrow-up"
              title="上移"
              color="var(--color-primary)"
              @tap.native.stop="onSortOption({ direction: 'forward', ...props })"
              :size="14"
            ></w-icon>
            <w-icon
              class="options-operate"
              v-if="componentType == 'select-group' && optionAdd"
              icon="pticon iconfont icon-ptkj-jiahao"
              @tap.native.stop="onSelectedDictOption(props)"
              title="添加"
              :size="14"
              color="var(--color-primary)"
            ></w-icon>
            <w-icon
              class="options-operate"
              v-if="optionDel && props.selectOptions.length > 0"
              icon="pticon iconfont icon-ptkj-shanchu"
              title="删除"
              color="var(--color-error)"
              @tap.native.stop="onDelOption(props)"
              :size="14"
            ></w-icon>
          </view>
        </template>
        <!-- 数据仓库，拓展字段展示 -->
        <template
          v-slot:selectorItem="{ props }"
          v-else-if="optionType == 'dataSource' && widget.configuration.options.dataSourceExtendColumn"
        >
          <view class="extend_column" v-if="props.item.extend_label">
            {{ props.item.extend_label }}
          </view>
        </template>
        <!-- 数据仓库、非分组且数据未加载完全时展示 -->
        <template slot="selectorLoadMore" v-if="optionType == 'dataSource' && componentType == 'select'">
          <uni-load-more v-show="!optionsCompletedRendered" :status="loadMoreStatus" :content-text="contentText" />
        </template>
      </uni-w-data-select>
      <custom-w-tree-select
        :disabled="disable || readonly"
        :title="widget.configuration.name"
        :placeholder="widget.configuration.placeholder"
        :clearable="allowClear"
        :canSelectAll="editMode.selectCheckAll"
        :tokenSeparators="tokenSeparators"
        :search="showSearch"
        contentHeight="400px"
        v-else-if="componentType == 'select-tree'"
        :listData="treeData"
        dataLabel="s_tree_title"
        :bordered="bordered"
        :showLine="showLine"
        v-model="value"
        dataValue="s_tree_value"
        :mutiple="editMode.selectMultiple"
        :choseParent="editMode.selectParent"
        :linkage="editMode.selectParent"
        :pathMode="editMode.allPath"
        pathHyphen="-"
        :lazyLoadChildren="editMode.loadAsync"
        :collapse="editMode.allCollapse"
        :currentNode="editDictProps && editDictProps.item"
        @changeValue="onChangeTree"
        @selectChange="onChangeTreeLabel"
        @clearCurrentNode="onClearSelectedDictOption"
      >
        <!-- 父级choseParent可选时联动linkage -->
        <!-- 数据仓库，拓展字段展示 -->
        <template
          v-slot:nodeRight="{ props }"
          v-if="optionType == 'dataSource' && widget.configuration.options.dataSourceExtendColumn"
        >
          <view
            class="extend_column"
            :class="{ 'multiple_item-extend_column': editMode.selectMultiple }"
            v-if="props.item.s_tree_extend"
          >
            {{ props.item.s_tree_extend }}
          </view>
        </template>
        <!-- 数据字典，更多操作 -->
        <view v-if="showMore && optionAdd" slot="operateBar" style="width: 100%">
          <view class="flex f_x_s f_y_c" v-if="editDictProps">
            <uni-w-easyinput v-model="editDictValue"></uni-w-easyinput>
            <view style="padding-left: 10px">
              <button
                type="primary"
                size="mini"
                @tap="onAddOption"
                :disabled="!editDictValue"
                style="vertical-align: middle"
              >
                添加
              </button>
            </view>
          </view>
        </view>
        <template v-slot:nodeRight="{ props }" v-if="showMore && (optionSort || optionDel || optionAdd)">
          <view class="flex options-operate-box" v-if="props.selectOptions.length">
            <w-icon
              class="options-operate"
              v-if="optionSort && props.selectOptions.length - 1 > props.index"
              icon="arrow-down"
              @tap.native.stop="onSortOption(props)"
              title="下移"
              :size="14"
              color="var(--color-primary)"
            ></w-icon>
            <w-icon
              class="options-operate"
              v-if="optionSort && props.index > 0"
              icon="arrow-up"
              title="上移"
              color="var(--color-primary)"
              @tap.native.stop="onSortOption({ direction: 'forward', ...props })"
              :size="14"
            ></w-icon>
            <w-icon
              class="options-operate"
              v-if="optionAdd"
              icon="pticon iconfont icon-ptkj-jiahao"
              @tap.native.stop="onSelectedDictOption(props)"
              title="添加"
              :size="14"
              color="var(--color-primary)"
            ></w-icon>
            <w-icon
              class="options-operate"
              v-if="optionDel && props.selectOptions.length > 0"
              icon="pticon iconfont icon-ptkj-shanchu"
              title="删除"
              color="var(--color-error)"
              @tap.native.stop="onDelOption(props)"
              :size="14"
            ></w-icon>
          </view>
        </template>
      </custom-w-tree-select>
    </template>
    <view v-else class="textonly" :title="selectedLabel">{{ selectedLabel }}</view>
  </uni-forms-item>
</template>
<style lang="sass"></style>
<script type="text/babel">
import formElement from "../w-dyform/form-element.mixin";
import formCommonMixin from "../w-dyform/form-common.mixin";
import { DataStore, utils, swapArrayElements, generateId } from "wellapp-uni-framework";
import { debounce, uniqBy, orderBy, groupBy, kebabCase, map, each, union, filter, isArray, findIndex } from "lodash";
import "./css/index.scss";

export default {
  mixins: [formElement, formCommonMixin],
  props: {},
  components: {},
  computed: {
    // 选中label字符串
    selectedLabel() {
      return this.selectedLabels.join(this.tokenSeparators);
    },
    // 是否多选
    isMultiple() {
      return this.selectMode === "multiple";
    },
    dataSourceDataMapping() {
      return this.widget.configuration.options.dataSourceDataMapping;
    },
    // 显示更多操作
    showMore() {
      if (this.optionType == "dataDictionary" && this.editMode) {
        let showMore = this.editMode.showMore;
        return showMore && (this.optionAdd || this.optionSort || this.optionDel);
      }
      return false;
    },
    // 添加
    optionAdd() {
      return this.editMode && this.editMode.optionAdd;
    },
    // 排序
    optionSort() {
      return this.editMode && this.editMode.optionSort;
    },
    // 删除
    optionDel() {
      return this.editMode && this.editMode.optionDel;
    },
    newOption() {
      let newOption = {
        value: "",
        label: "",
      };
      return newOption;
    },
    loadMoreStatus() {
      if (this.loading) {
        return "loading";
      } else if (this.optionsCompletedRendered) {
        return "noMore";
      }
      return "more";
    },
  },
  data() {
    if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
      this.$set(this.widget.configuration, "uniConfiguration", { dropType: "picker", bordered: false });
    }
    return {
      value: this.widget.configuration.editMode.selectMultiple ? [] : null,
      fetched: false,
      options: [], // 全部的选项数据
      selectOptions: [], // 下拉框选项
      tempValue: null, // 临时值，用于当选项未加载时候的值存储
      selectedLabels: [], // 选中label数组
      componentType: this.widget.configuration.type,
      valueLabelMap: {},
      dataSourceResults: [],
      optionsValueMap: {},
      treeData: [],
      treeDatas: [], // 全部的树选项数据
      loading: false,
      pageSize: 50,
      pageIndex: 0,
      editMode: this.widget.configuration.editMode,
      allowClear: this.widget.configuration.editMode.allowClear,
      showSearch: this.widget.configuration.editMode.showSearch,
      selectMode: this.widget.configuration.editMode.selectMode,
      tokenSeparators: this.widget.configuration.tokenSeparators,
      dropType: this.widget.configuration.uniConfiguration.dropType,
      bordered: this.widget.configuration.uniConfiguration.bordered,
      showLine: this.widget.configuration.uniConfiguration.showLine, // 带连接线的树
      editDictValue: "", // 添加字典项
      editDictProps: undefined, // 当前字典项
      optionsCompletedRendered: true, // 初始不显示
      contentText: {
        contentdown: "加载更多",
        contentrefresh: "加载中",
        contentnomore: "没有更多数据了",
      },
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.addSelectedNumRules({
      min: this.widget.configuration.minCount,
      max: this.widget.configuration.maxCount,
    });
  },
  mounted() {
    this.init();
  },
  methods: {
    refetchOption(options) {
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
            this.dataSourceProvider.setDefaultCriterions([
              {
                sql: options.wEventParams.defaultCondition,
              },
            ]);
          }
        }
      }
      this.fetchSelectOptions();
    },

    init() {
      if (this.optionDataAutoSet && this.relateKey) {
        this.listenRelateChanged(() => {
          this.checkRelateValue();
        });
      }

      this.setValue(this.formData[this.fieldCode], false); // 设初始值
      // 不是联动获取备选项
      if (!this.optionDataAutoSet && (this.componentType == "select" || this.componentType == "select-group")) {
        this.fetchSelectOptions();
      }
      if (!this.optionDataAutoSet && this.componentType == "select-tree") {
        this.fetchTreeData();
      }
    },
    onPopupScroll: debounce(function (evt) {
      if (!this.optionsCompletedRendered && this.optionType == "dataSource" && this.componentType == "select") {
        // 加载更多
        this.onPageOptions(++this.pageIndex, 200);
      }
    }, 200),
    fetchSelectOptions() {
      this.selectOptions.splice(0, this.selectOptions.length);
      const options = this.widget.configuration.options;
      if (this.optionType == "selfDefine") {
        this.getSelectOptionBySelfDefine();
      } else if (this.optionType == "dataDictionary") {
        this.fetchSelectOptionByDataDic(options.dataDictionaryUuid);
      } else if (this.optionType == "dataSource") {
        this.fetchSelectOptionByDataSource(options);
      } else if (this.optionType == "dataModel") {
        this.fetchSelectOptionByDataSource(
          options,
          `/api/dm/loadData/${options.dataModelUuid}`,
          `/api/dm/loadDataCount/${options.dataModelUuid}`
        );
      }
    },
    // 获取下拉框常量备选项
    getSelectOptionBySelfDefine() {
      this.options = this.widget.configuration.options.defineOptions;
      if (this.componentType == "select-group") {
        let groups = [],
          grpIdx = {};
        // 分组
        for (let i = 0, len = this.options.length; i < len; i++) {
          if (grpIdx[this.options[i].group] == undefined) {
            groups.push({ label: this.options[i].group, options: [] });
            grpIdx[this.options[i].group] = groups.length - 1;
          }
          if (!this.options[i].text) {
            this.options[i].text = this.options[i].label;
          }
          groups[grpIdx[this.options[i].group]].options.push(this.options[i]);
        }
        this.selectOptions = groups;
      } else {
        this.selectOptions.push(...this.convertTextValueOptions(this.widget.configuration.options.defineOptions));
      }
      this.fetched = true;
      this.setValueLabelMap(this.selectOptions);
      this.setSelectedLabels();
      this.setRelatedValue();
      this.emitChange({}, false);
    },
    // 获取下拉框数据字典
    fetchSelectOptionByDataDic(dataDicUuid) {
      let _this = this,
        isGroup = this.componentType === "select-group";
      this.loading = true;
      let afterFetchData = (data) => {
        let selectOptions = [];
        for (let i = 0, len = data.length; i < len; i++) {
          let opt = {};
          if (isGroup) {
            let group = {
              label: data[i].label,
              options: [],
              uuid: data[i].uuid,
              parentUuid: null,
            };
            let jlen = data[i].children.length;
            for (let j = 0; j < jlen; j++) {
              opt = {
                text: data[i].children[j].label,
                value: data[i].children[j].value,
                uuid: data[i].children[j].uuid,
                parentUuid: group.uuid,
              };
              group.options.push(opt);
              _this.options.push(opt);
            }
            if (jlen > 0) selectOptions.push(group);
          } else {
            opt = {
              text: data[i].label,
              value: data[i].value,
              uuid: data[i].uuid,
            };
            selectOptions.push(opt);
            _this.options.push(opt);
          }
        }
        _this.loading = false;
        _this.fetched = true;
        _this.selectOptions.splice(0, _this.selectOptions.length);
        _this.selectOptions.push(...selectOptions);
        _this.setValueLabelMap(_this.selectOptions);
        _this.setSelectedLabels();
        _this.setRelatedValue();
        _this.emitChange({}, false);
      };
      this.getLabelValueOptionByDataDic(dataDicUuid, (results) => {
        if (results && results.length) {
          this.curDictUuid = results[0]["dataDictUuid"];
        }
        afterFetchData.call(_this, results);
      });
    },
    // 获取下拉框数据仓库
    fetchSelectOptionByDataSource(options, loadDataUrl, loadDataCntUrl) {
      let _this = this;
      let valueColIndex = options.dataSourceValueColumn,
        labelColIndex = options.dataSourceLabelColumn,
        groupColIndex = options.groupColumn;
      this.loading = true;
      let afterLoadData = (results) => {
        if (results.length > 0) {
          let opts = [];
          let initValue = _this.getValue();
          if (typeof initValue === "string") {
            initValue = [initValue];
          }
          let initValOptions = [];
          for (let i = 0, len = results.length; i < len; i++) {
            let opt = { text: results[i][labelColIndex], value: results[i][valueColIndex] };
            if (this.widget.configuration.options.dataSourceExtendColumn) {
              opt.extend_label = results[i][this.widget.configuration.options.dataSourceExtendColumn];
            }
            if (_this.componentType === "select-group") {
              opt[groupColIndex] = results[i][groupColIndex];
            }
            if (initValue && initValue.indexOf(results[i][valueColIndex]) != -1) {
              initValOptions.push(opt);
            }
            opts.push(opt);
            _this.optionsValueMap[results[i][valueColIndex]] = results[i];
          }
          _this.dataSourceResults = results;
          if (_this.componentType === "select-group") {
            opts = orderBy(opts, (item) => {
              return item[groupColIndex];
            });
          }
          _this.options = opts;
          _this.fetched = true;
          _this.loading = false;
          _this.onPageOptions((_this.pageIndex = 0), 0, initValOptions);
          _this.setValueLabelMap(opts);
          _this.setSelectedLabels();
          _this.emitChange({}, false);
          _this.setRelatedValue();
        } else {
          _this.loading = false;
        }
      };
      let dataSourceProvider = this.getDataSourceProvider(options, loadDataUrl, loadDataCntUrl);
      dataSourceProvider.load().then((data) => {
        afterLoadData.call(_this, data.data);
      });
    }, // 分页选项数据
    onPageOptions(pageIndex, delay, extraOptions) {
      this.loading = true;
      let end = (pageIndex + 1) * this.pageSize,
        start = pageIndex * this.pageSize,
        _this = this;
      if (this.options.length <= end) {
        end = this.options.length;
        this.optionsCompletedRendered = true;
      } else {
        this.optionsCompletedRendered = false;
      }
      if (pageIndex === 0) {
        _this.selectOptions = [];
      }
      let newSelection = function () {
        if (_this.componentType == "select-group") {
          // 分组,不分页获取
          let g = groupBy(_this.options, (item) => {
            return item[_this.widget.configuration.options.groupColumn];
          });
          let groups = [];
          for (let k in g) {
            groups.push({ label: k, options: g[k] });
          }
          _this.selectOptions = groups;
          _this.optionsCompletedRendered = true;
        } else {
          let _options = uniqBy(
            _this.selectOptions.concat(_this.options.slice(start, end)).concat(extraOptions || []),
            (item) => item.value
          );
          _this.selectOptions = _options;
        }
        _this.loading = false;
      };
      if (delay == undefined) {
        newSelection();
      } else {
        setTimeout(function () {
          newSelection();
        }, delay);
      }
    },
    getDataSourceProvider(options, loadDataUrl, loadDataCntUrl) {
      if (this.dataSourceProvider) {
        return this.dataSourceProvider;
      } else {
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
          pageSize: this.pageSize * 1000, // FIXME: 下拉数据多的情况下，会明显卡顿，是否增加分页加载数据(需要引导用户知道需要滚动加载更多)
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
    setValueLabelMap(options) {
      if (this.componentType === "select-tree") {
        const tree2Map = (data) => {
          data.map((child) => {
            this.valueLabelMap[child.s_tree_value] = child.s_tree_label;
            if (child.children) {
              tree2Map(child.children);
            }
          });
        };
        tree2Map(options);
      } else {
        for (let i = 0, len = options.length; i < len; i++) {
          const value = options[i].value;
          const label = options[i].label || options[i].text;
          if (value && label) {
            this.valueLabelMap[value] = label;
          }
          if (options[i]["options"]) {
            this.setValueLabelMap(options[i]["options"]);
          }
        }
      }
    },
    setSelectedLabels() {
      this.selectedLabels.splice(0, this.selectedLabels.length);
      // this.selectedLabels.length = 0;
      let values = this.value;
      if (values == undefined) {
        return [];
      }
      if (typeof values == "string") {
        values = values.split(this.tokenSeparators);
      }
      for (let i = 0, len = values.length; i < len; i++) {
        if (this.valueLabelMap[values[i]] != undefined) {
          this.selectedLabels.push(this.valueLabelMap[values[i]]);
        }
      }
      return this.selectedLabels;
    },

    // 下拉框选中 option
    onSelectChange(value, selectedLabel) {
      this.selectedOptions = [];
      if (value == "" || value == undefined || value == null || value.length == 0) {
        this.formData[this.fieldCode] = null;
      }
      if (this.$attrs.isSubformCell) {
        this.formData.__MODEL__.label[this.fieldCode] = null;
      }
      if (value) {
        if (Array.isArray(value)) {
          this.formData[this.fieldCode] = value.join(this.tokenSeparators);
        } else {
          this.formData[this.fieldCode] = value;
        }
        if (this.$attrs.isSubformCell) {
          this.formData.__MODEL__.label[this.fieldCode] = this.selectedLabels.join(this.tokenSeparators);
        }
      }
      // console.log("值：", this.formData[this.fieldCode]);
      this.emitChange();
    },
    getSelectedLabel(selectedLabels, label) {
      // console.log("显示值：", label);
      this.selectedLabels = selectedLabels;
      this.setRelatedValue();
    },
    setRelatedValue(selectedLabel) {
      let value = this.value;
      if (this.widget.configuration.displayValueField) {
        // 显示值字段
        this.form.setFieldValue(
          this.widget.configuration.displayValueField,
          selectedLabel ? selectedLabel : this.selectedLabel
        );
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
          this.form.setFieldValue(this.dataSourceDataMapping[i].targetField, allVal.length ? allVal.join(";") : null);
        }
      }

      if (this.widget.configuration.textWidgetId) {
        // 在文本中显示真实值
        this.form.setText(this.widget.configuration.textWidgetId, this.formData[this.fieldCode]);
      }
    },
    onBlur() {
      this.emitBlur();
    },
    onFocus() {},
    // 删除 字典项uuid
    onDelOption({ index, item, parentIndex }) {
      this.onClearSelectedDictOption();
      this.$axios.post(`/api/datadict/deleteItem/${item.uuid}`).then((res) => {
        if (this.componentType == "select-tree") {
          this.fetchTreeDataByDataDic(this.widget.configuration.options.dataDictionaryUuid);
        } else {
          if (item.parentUuid) {
            this.selectOptions[parentIndex]["options"].splice(index, 1);
            let hasIndex = findIndex(this.options, (opt) => {
              return opt.uuid == item.uuid;
            });
            if (hasIndex > -1) {
              this.options.splice(hasIndex, 1);
            }
          } else {
            this.selectOptions.splice(index, 1);
          }
          // this.removeLabelOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
          this.getLabelValueOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
        }
      });
      this.popoverVisible = false;
      this.editMoreItem = undefined;
      // this.inputFocus();
    },
    // 选择要添加的字典项
    onSelectedDictOption(props) {
      this.editDictProps = utils.deepClone(props);
    },
    // 清除要添加的字典项
    onClearSelectedDictOption() {
      this.editDictProps = undefined;
    },
    // 新增 字典项
    onAddOption() {
      let option = {
        label: this.editDictValue,
        text: this.editDictValue,
        value: utils.generateId(),
      };
      if (this.editDictProps) {
        option.index = this.editDictProps.index;
        option.parentIndex = this.editDictProps.parentIndex;
        option.parentUuid = this.editDictProps.item.parentUuid || "";
      }
      const { index, parentUuid, parentIndex } = option;
      const req = {
        ...option,
        dataDictUuid: this.curDictUuid, // 所在数据字典UUID
      };
      if (parentUuid) {
        req.parentUuid = parentUuid;
      }
      this.$axios({
        method: "post",
        url: `/api/datadict/saveItem`,
        data: req,
      }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          const itemUuid = data.data;
          req.uuid = itemUuid;
          if (this.componentType == "select-tree") {
            this.fetchTreeDataByDataDic(this.widget.configuration.options.dataDictionaryUuid);
          } else {
            if (parentUuid) {
              this.selectOptions[parentIndex]["options"].push(req);
              this.options.push(req);
            } else {
              this.selectOptions.push(req);
            }
            // this.removeLabelOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
            this.getLabelValueOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
          }
        }
      });
      // this.inputFocus();
    },
    // 排序字典序
    onSortOption({ direction, item, index, selectOptions }, event) {
      this.onSelectedDictOption({ item, index, selectOptions });
      utils.swapArrayElements(
        [item.uuid],
        selectOptions,
        function (a, b) {
          return a == b.uuid;
        },
        direction,
        () => {}
      );
      const req = {};
      selectOptions.forEach((item, index) => {
        req[item.uuid] = index;
      });

      this.$axios({
        method: "post",
        url: `/api/datadict/updateItemSortOrder`,
        data: req,
      }).then((res) => {
        // this.removeLabelOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
        this.getLabelValueOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
      });
      // this.inputFocus();
    },
    onChange(values) {
      this.setValue(values);
    },
    setValue(v, validate = true) {
      this.value = v;
      if (this.isMultiple) {
        // 多选情况
        if (typeof v === "string") {
          this.value = v.split(this.tokenSeparators);
        } else if (v == null) {
          this.value = [];
        }
      }

      this.formData[this.fieldCode] = Array.isArray(v) ? v.join(this.tokenSeparators) : v;

      if (this.widget.configuration.textWidgetId) {
        // 在文本中显示真实值
        this.form.setText(this.widget.configuration.textWidgetId, this.formData[this.fieldCode]);
      }
      this.setSelectedLabels();
      this.setRelatedValue();
      this.clearValidate();
      this.emitChange({}, validate);
    },
    displayValue(value) {
      // 通过value换label
      if (value != undefined) {
        let labels = [];
        let values = value;
        if (typeof value == "string") {
          values = value.split(this.tokenSeparators);
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

    // 判断联动方式
    checkRelateValue() {
      if (this.optionType === "selfDefine") {
        // 常量
        if (this.componentType === "select") {
          this.relateSelfDefine();
        }
        if (this.componentType === "select-tree") {
          this.relateTreeSelfDefine();
        }
      }
      if (this.optionType === "dataDictionary") {
        if (this.componentType === "select-tree") {
          this.fetchTreeDataByDataDic(this.relateValue);
        } else {
          // 数据字典
          this.fetchSelectOptionByDataDic(this.relateValue);
        }
      }
    },
    // 联动常量
    relateSelfDefine() {
      this.selectOptions = [];
      const relateList = this.widget.configuration.relateList;
      const value = this.relateValue;
      const selectOptions = this.relateToOptions({ relateList, value });
      if (selectOptions.length) {
        const defineOptions = this.widget.configuration.options.defineOptions;
        this.selectOptions = defineOptions.filter((item) => {
          return selectOptions.includes(item.value);
        });
        this.setValueLabelMap(this.selectOptions);
      }
      this.fetched = true;
      this.optionsChangeAfter();
    },

    // 树型下拉框，值变化事件
    onChangeTree(val, label, extra) {
      this.formData[this.fieldCode] = null;
      if (Array.isArray(val)) {
        this.formData[this.fieldCode] = val.join(this.tokenSeparators);
      } else {
        this.formData[this.fieldCode] = val;
      }
      this.selectedLabels = label;
      // console.log("值：", this.formData[this.fieldCode]);
      this.value = val;

      this.emitChange();
    },
    onChangeTreeLabel(selectLists) {
      this.selectedLabels = map(selectLists, (item) => {
        return this.editMode.allPath ? item.path : item.s_tree_title;
      });
      // console.log("显示值：", this.selectedLabel);
      this.setRelatedValue();
    },
    // 获取树形数据
    fetchTreeData() {
      const options = this.widget.configuration.options;
      if (options.type == "selfDefine") {
        this.getTreeDataBySelfDefine();
      } else if (options.type == "dataDictionary") {
        this.fetchTreeDataByDataDic(options.dataDictionaryUuid);
      } else if (options.type == "dataSource") {
        this.fetchTreeDataByDataSource(options);
      }
    },
    setSelectedLabes() {
      this.selectedLabels.splice(0, this.selectedLabels.length);
      // this.selectedLabels.length = 0;
      let values = this.value;
      if (values == undefined) {
        return [];
      }
      if (typeof values == "string") {
        values = values.split(this.tokenSeparators);
      }
      for (let i = 0, len = values.length; i < len; i++) {
        if (this.valueLabelMap[values[i]] != undefined) {
          this.selectedLabels.push(this.valueLabelMap[values[i]]);
        }
      }
      return this.selectedLabels;
    },
    // 获取自定义下拉树数据
    getTreeDataBySelfDefine() {
      this.treeData = this.reSetTreeData(this.widget.configuration.treeData);
      this.treeDatas = utils.deepClone(this.treeData);
      this.setValueLabelMap(this.treeData);
      this.setSelectedLabes();
      this.setRelatedValue();
      this.emitChange({}, false);
    },
    // 获取树形下拉框 数据字典备选项
    fetchTreeDataByDataDic(dataDicUuid) {
      this.getLabelValueOptionByDataDic(dataDicUuid, (results) => {
        this.treeData = this.reSetTreeData(results);
        this.treeDatas = utils.deepClone(this.treeData);
        this.curDictUuid = results[0]["dataDictUuid"];
        this.setValueLabelMap(this.treeData);
        this.setSelectedLabes();
        this.setRelatedValue();
        this.emitChange({}, false);
      });
    },
    // 获取树形下拉框 数据仓库备选项
    fetchTreeDataByDataSource(options) {
      this.$axios
        .post("/json/data/services", {
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
              async: false,
            },
          ]),
        })
        .then(({ data }) => {
          if (data.code == 0) {
            let results = data.data;
            this.treeData = this.reSetTreeData(results);
            this.treeDatas = utils.deepClone(this.treeData);
            this.setValueLabelMap(this.treeData);
            this.setSelectedLabes();
            this.setRelatedValue();
            this.emitChange({}, false);
          }
        });
    },
    reSetTreeData(treeData) {
      const tree2Map = (data) => {
        return map(data, (child) => {
          child.s_tree_key = child.id || child.uuid;
          child.s_tree_value = child.real || child.value || child.s_tree_key;
          child.s_tree_title = child.display || child.name || child.label;
          if (this.widget.configuration.options.dataSourceExtendColumn) {
            child.s_tree_extend = child.data[this.widget.configuration.options.dataSourceExtendColumn];
          }
          if (!child.s_tree_label) {
            child.s_tree_label = child.s_tree_title;
          }
          if (child.children && child.children.length) {
            each(child.children, (c_child) => {
              c_child.s_tree_title = c_child.display || c_child.name || c_child.label;
              if (this.widget.configuration.editMode.allPath) {
                c_child.s_tree_label = child.s_tree_label + "-" + c_child.s_tree_title;
              } else {
                c_child.s_tree_label = c_child.s_tree_title;
              }
            });
            child.children = tree2Map(child.children);
          }
          return child;
        });
      };
      return tree2Map(treeData);
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
      this.emitChange({}, false);
    },
    optionsChangeAfter() {
      if (this.componentType === "select" && this.optionType === "selfDefine") {
        let val = this.formData[this.fieldCode];
        if (val) {
          let index = findIndex(this.selectOptions, (opt) => {
            return opt.value == val;
          });
          if (index == -1) {
            this.setValue(null);
          }
        }
        this.setSelectedLabels();
        this.emitChange();
      }
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
  },
};
</script>
