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
        :title="$t(widget.id, widget.configuration.name)"
        :initOptions="options"
        :localdata="selectOptions"
        :disabled="disable || readonly"
        :placeholder="$t('placeholder', widget.configuration.placeholder)"
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
            <uni-w-easyinput v-model="editDictValue" class="align-left"></uni-w-easyinput>
            <view style="padding-left: 10px">
              <uni-w-button type="primary" @tap="onAddOption" :disabled="!editDictValue" style="vertical-align: middle">
                {{ $t("WidgetFormSelect.add", "添加") }}
              </uni-w-button>
            </view>
          </view>
        </view>
        <template v-slot:selectorItem="{ item, index, selectedOptions, parentIndex }">
          <template v-if="showMore && (optionSort || optionDel || (componentType == 'select-group' && optionAdd))">
            <view
              class="flex options-operate-box"
              v-if="selectedOptions.length || (componentType == 'select-group' && optionAdd)"
            >
              <w-icon
                class="options-operate"
                v-if="optionSort && selectedOptions.length - 1 > index"
                icon="arrow-down"
                @tap.native.stop="onSortOption({ item, index, selectedOptions, parentIndex })"
                :title="$t('WidgetFormSelect.moveDown', '下移')"
                title=""
                :size="14"
                color="var(--color-primary)"
              ></w-icon>
              <w-icon
                class="options-operate"
                v-if="optionSort && index > 0"
                icon="arrow-up"
                :title="$t('WidgetFormSelect.moveUp', '上移')"
                title=""
                color="var(--color-primary)"
                @tap.native.stop="onSortOption({ direction: 'forward', item, index, selectedOptions, parentIndex })"
                :size="14"
              ></w-icon>
              <w-icon
                class="options-operate"
                v-if="componentType == 'select-group' && optionAdd"
                icon="plus"
                @tap.native.stop="onSelectedDictOption({ item, index, selectedOptions, parentIndex })"
                :title="$t('WidgetFormSelect.add', '添加')"
                :size="14"
                color="var(--color-primary)"
              ></w-icon>
              <w-icon
                class="options-operate"
                v-if="optionDel && selectedOptions.length > 0"
                icon="iconfont icon-ptkj-shanchu"
                :title="$t('WidgetFormSelect.delete', '删除')"
                title=""
                color="var(--color-error)"
                @tap.native.stop="onDelOption({ item, index, selectedOptions, parentIndex })"
                :size="14"
              ></w-icon>
            </view>
          </template>
          <!-- 数据仓库，拓展字段展示 -->
          <template v-else-if="optionType == 'dataSource' && widget.configuration.options.dataSourceExtendColumn">
            <view class="extend_column" v-if="item.extend_label">
              {{ item.extend_label }}
            </view>
          </template>
        </template>
        <!-- 数据仓库、非分组且数据未加载完全时展示 -->
        <template slot="selectorLoadMore" v-if="optionType == 'dataSource' && componentType == 'select'">
          <uni-load-more v-show="!optionsCompletedRendered" :status="loadMoreStatus" :content-text="contentText" />
        </template>
      </uni-w-data-select>
      <custom-w-tree-select
        :disabled="disable || readonly"
        :title="$t(widget.id, widget.configuration.name)"
        :placeholder="$t('placeholder', widget.configuration.placeholder)"
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
        @showPickerPopup="showTreePickerPopup"
        @changeValue="onChangeTree"
        @selectChange="onChangeTreeLabel"
        @clearCurrentNode="onClearSelectedDictOption"
      >
        <!-- 父级choseParent可选时联动linkage -->
        <!-- 数据字典，更多操作 -->
        <view v-if="false && showMore && optionAdd" slot="operateBar" style="width: 100%">
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
                {{ $t("WidgetFormSelect.add", "添加") }}
              </button>
            </view>
          </view>
        </view>
        <template v-slot:nodeRight="{ item, sindex, selectedOptions }">
          <template v-if="false && showMore && (optionSort || optionDel || optionAdd)">
            <view class="flex options-operate-box" v-if="selectedOptions.length">
              <w-icon
                class="options-operate"
                v-if="optionSort && selectedOptions.length - 1 > sindex"
                icon="arrow-down"
                @tap.native.stop="onSortOption({ item, sindex, selectedOptions })"
                :title="$t('WidgetFormSelect.moveDown', '下移')"
                title=""
                :size="14"
                color="var(--color-primary)"
              ></w-icon>
              <w-icon
                class="options-operate"
                v-if="optionSort && sindex > 0"
                icon="arrow-up"
                :title="$t('WidgetFormSelect.moveUp', '上移')"
                title=""
                color="var(--color-primary)"
                @tap.native.stop="onSortOption({ direction: 'forward', item, sindex, selectedOptions })"
                :size="14"
              ></w-icon>
              <w-icon
                class="options-operate"
                v-if="optionAdd"
                icon="plus"
                @tap.native.stop="onSelectedDictOption({ item, sindex, selectedOptions })"
                :title="$t('WidgetFormSelect.add', '添加')"
                :size="14"
                color="var(--color-primary)"
              ></w-icon>
              <w-icon
                class="options-operate"
                v-if="optionDel && selectedOptions.length > 0"
                icon="trash"
                :title="$t('WidgetFormSelect.delete', '删除')"
                title=""
                color="var(--color-error)"
                @tap.native.stop="onDelOption({ item, sindex, selectedOptions })"
                :size="14"
              ></w-icon>
            </view>
          </template>
          <!-- 数据仓库，拓展字段展示 -->
          <template v-else-if="optionType == 'dataSource' && widget.configuration.options.dataSourceExtendColumn">
            <view
              :class="{ extend_column: true, 'multiple_item-extend_column': editMode.selectMultiple }"
              v-if="item.s_tree_extend"
            >
              {{ item.s_tree_extend }}
            </view>
          </template>
        </template>
      </custom-w-tree-select>
    </template>
    <view v-else class="textonly" :title="selectedLabel">{{ selectedLabel }}</view>
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
import { DataStore, utils, storage } from "wellapp-uni-framework";
import { debounce, uniqBy, orderBy, groupBy, kebabCase, map, each, union, filter, isArray, findIndex } from "lodash";
// #ifndef APP-PLUS
import "./css/index.scss";
// #endif

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
    defaultEvents() {
      let eventParams = [];
      if (this.optionType === "selfDefine" || this.optionType === "dataDictionary") {
        let eventParamKeyOptions = {
          paramKey: "options",
          remark: "选项",
          valueSource: {
            inputType:
              this.componentType == "select-tree"
                ? "tree-select"
                : this.componentType == "select-group"
                ? "group-select"
                : "multi-select",
            options: this.eventValueOptions,
          },
        };
        if (this.componentType == "select-tree") {
          eventParamKeyOptions.valueSource.showCheckedStrategy = "SHOW_ALL";
          eventParamKeyOptions.valueSource.replaceFields = {
            children: "children",
            title: "s_tree_title",
            key: "s_tree_key",
            value: "s_tree_value",
          };
        }
        eventParams.push(eventParamKeyOptions);
      }
      if (this.optionType == "dataSource" || this.optionType == "dataModel") {
        eventParams.push({
          paramKey: "removeCache",
          remark: "在从表里，该值为true会清除缓存，主表里默认清除缓存",
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
      bordered: this.widget.configuration.uniConfiguration
        ? this.widget.configuration.uniConfiguration.bordered
        : false,
      showLine: this.widget.configuration.uniConfiguration.showLine, // 带连接线的树
      editDictValue: "", // 添加字典项
      editDictProps: undefined, // 当前字典项
      optionsCompletedRendered: true, // 初始不显示
      contentText: {
        contentdown: this.$t("global.loadMore", "加载更多"),
        contentrefresh: this.$t("global.loading", "加载中"),
        contentnomore: this.$t("global.noMore", "没有更多数据了"),
      },
      treeOptionParams: {}, // 树形下拉框数据仓库搜索参数
      treeOptionDefaultCondition: undefined, // 树形下拉框数据仓库默认筛选条件
      cacheKey: undefined,
      dataModelDatasMap: {},
    };
  },
  beforeCreate() {},
  created() {
    if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
      this.$set(this.widget.configuration, "uniConfiguration", { dropType: "picker", bordered: false });
    }
  },
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
      if (this.optionType === "selfDefine" || this.optionType === "dataDictionary") {
        if (options.eventParams && options.eventParams.options) {
          if (this.componentType == "select-tree") {
            this.getTreeDataByValue(options.eventParams.options);
          } else {
            this.getSelectOptionByValue(options.eventParams.options);
          }
          return false;
        }
      }
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
        if (!isEmpty(options.eventParams)) {
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
      if (this.componentType == "select-tree") {
        this.fetchTreeData();
      } else {
        this.fetchSelectOptions();
      }
    },

    // 下拉框，通过事件配置的选项值，筛选常量和字典下拉备选项
    getSelectOptionByValue(values) {
      let selectOptions = this.selectOptions.splice(0, this.selectOptions.length);
      if (this.componentType == "select-group") {
        if (this.groupOptions.length == 0) {
          this.groupOptions = utils.deepClone(selectOptions);
        }
        each(this.groupOptions, (opt) => {
          let opts = [];
          each(opt.options, (child) => {
            if (values.indexOf(child.value) > -1) {
              opts.push(child);
            }
          });
          if (opts.length) {
            opt.options = opts;
            this.selectOptions.push(opt);
          }
        });
      } else {
        each(this.options, (opt) => {
          if (values.indexOf(opt.value) > -1) {
            this.selectOptions.push(opt);
          }
        });
      }
    },
    // 树形下拉框，通过事件配置的选项值，筛选常量和字典下拉备选项
    getTreeDataByValue(values) {
      let treeDatas = utils.deepClone(this.treeDatas);
      this.treeData = this.filterSearchTree(treeDatas, (node) => {
        if (values.includes(node.s_tree_value)) {
          return true;
        }
        return false;
      });
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
      } else if (this.optionType == "apiLinkService") {
        this.fetchSelectOptionByApiLink(options);
      }
    },
    fetchSelectOptionByApiLink(options) {
      let _this = this;
      _this.selectOptions.splice(0, _this.selectOptions.length);
      _this.loading = true;
      this.fetchDataByApiLinkInvocation(options.apiInvocationConfig).then((results) => {
        _this.loading = false;
        if (Array.isArray(results) && results.length > 0) {
          let opts = [];
          let initValue = _this.getValue();
          if (typeof initValue === "string") {
            initValue = [initValue];
          }
          let initValOptions = [],
            labels = [];
          for (let i = 0, len = results.length; i < len; i++) {
            let opt = { label: results[i].label, value: results[i].value };
            if (opt.label != undefined) {
              labels.push(opt.label);
            }
            if (results[i].extend_label) {
              opt.extend_label = results[i].extend_label;
              if (opt.extend_label != undefined) {
                labels.push(opt.extend_label);
              }
            }
            if (_this.componentType === "select-group") {
              opt.group = results[i].group;
              if (opt.group != undefined) {
                labels.push(opt.group);
              }
            }
            if (initValue && initValue.indexOf(results[i].value) != -1) {
              initValOptions.push(opt);
            }
            opts.push(opt);
            _this.optionsValueMap[results[i].value] = results[i];
          }
          let _continue = () => {
            if (_this.componentType === "select-group") {
              opts = orderBy(opts, ["group"]);
            }
            _this.options = opts;
            _this.fetched = true;
            _this.loading = false;
            _this.onPageOptions((_this.pageIndex = 0), 0, initValOptions, "group");
            _this.setValueLabelMap(opts);
            _this.setSelectedLabels();
            _this.emitChange({}, false);
            _this.setRelatedValue();
          };

          if (options.autoTranslate && _this.$i18n.locale !== "zh_CN") {
            _this.$i18n.$translate(labels, "zh", _this.$i18n.locale.split("_")[0]).then((map) => {
              for (let o of opts) {
                let rst = _this.optionsValueMap[o.value];
                if (o.label && map[o.label.trim()]) {
                  o.label = map[o.label.trim()];
                  rst.label = o.label;
                }
                if (o.extend_label && map[o.extend_label.trim()]) {
                  o.extend_label = map[o.extend_label.trim()];
                  rst.extend_label = o.extend_label;
                }
                if (o.group && map[o.group]) {
                  o.group = map[o.group];
                  rst.group = o.group;
                }
              }
              _continue();
            });
          } else {
            _continue();
          }
        }
      });
    },
    // 获取下拉框常量备选项
    getSelectOptionBySelfDefine() {
      this.options = this.widget.configuration.options.defineOptions;
      for (let o of this.options) {
        o.label = this.$t(o.id, o.label);
      }
      if (this.componentType == "select-group") {
        let groups = [],
          grpIdx = {};
        // 分组
        for (let i = 0, len = this.options.length; i < len; i++) {
          if (grpIdx[this.options[i].group] == undefined) {
            let groupName = this.options[i].group;
            let groupIndex = findIndex(this.widget.configuration.options.groupOptions, {
              value: this.options[i].group,
            });
            if (groupIndex > -1) {
              groupName = this.$t(this.widget.configuration.options.groupOptions[groupIndex].id, this.options[i].group);
            }
            groups.push({ label: groupName, options: [] });
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
          let initValOptions = [],
            labels = [];
          for (let i = 0, len = results.length; i < len; i++) {
            let opt = { text: results[i][labelColIndex], value: results[i][valueColIndex] };
            if (opt.text != undefined) {
              labels.push(opt.text);
            }
            if (this.widget.configuration.options.dataSourceExtendColumn) {
              opt.extend_label = results[i][this.widget.configuration.options.dataSourceExtendColumn];
              if (opt.extend_label != undefined) {
                labels.push(opt.extend_label);
              }
            }
            if (_this.componentType === "select-group") {
              opt[groupColIndex] = results[i][groupColIndex];
              if (opt[groupColIndex] != undefined) {
                labels.push(opt[groupColIndex]);
              }
            }
            if (initValue && initValue.indexOf(results[i][valueColIndex]) != -1) {
              initValOptions.push(opt);
            }
            opts.push(opt);
            _this.optionsValueMap[results[i][valueColIndex]] = results[i];
          }
          let _continue = () => {
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
          };

          if (options.autoTranslate && _this.$i18n.locale !== "zh_CN") {
            _this.$i18n.$translate(_this, labels, "zh", _this.$i18n.locale.split("_")[0]).then((map) => {
              for (let o of opts) {
                let rst = _this.optionsValueMap[o.value];
                if (o.text && map[o.text.trim()]) {
                  o.text = map[o.text.trim()];
                  rst[labelColIndex] = o.text;
                }
                if (o.extend_label && map[o.extend_label.trim()]) {
                  o.extend_label = map[o.extend_label.trim()];
                  rst[extColIndex] = o.extend_label;
                }
                if (o[groupColIndex] && map[o[groupColIndex.trim()]]) {
                  o[groupColIndex] = map[o[groupColIndex.trim()]];
                  rst[groupColIndex] = o[groupColIndex];
                }
              }
              _continue();
            });
          } else {
            _continue();
          }
        } else {
          _this.loading = false;
        }
      };
      this.cacheKey = utils.md5(
        JSON.stringify({
          options,
          loadDataUrl,
          loadDataCntUrl,
        })
      );
      let dataSourceProvider = this.getDataSourceProvider(options, loadDataUrl, loadDataCntUrl);
      // 多次重复调用，去缓存获取，有则返回，无则排队等待调用完成后被回调
      storage.getStorageCache(
        this.cacheKey,
        () => {
          return new Promise((resolve, reject) => {
            dataSourceProvider.load().then((data) => {
              resolve(data);
              // afterLoadData.call(_this, data.data);
            });
          });
        },
        (data) => {
          afterLoadData.call(_this, data.data);
        }
      );
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
      if (this.componentType == "select-tree") {
        for (let k in params) {
          this.treeOptionParams[k] = params[k];
        }
      } else if (this.dataSourceProvider) {
        for (let k in params) {
          this.dataSourceProvider.addParam(k, params[k]);
        }
      }
    },
    clearDataSourceParams() {
      if (this.componentType == "select-tree") {
        this.treeOptionParams = {};
      } else if (this.dataSourceProvider) {
        this.dataSourceProvider.clearParams();
      }
    },
    deleteDataSourceParams(...key) {
      if (this.componentType == "select-tree") {
        for (let k of key) {
          delete this.treeOptionParams[k];
        }
      } else if (this.dataSourceProvider) {
        for (let k of key) {
          this.dataSourceProvider.removeParam(k);
        }
      }
    },
    // 有key则返回key对应参数，无key返回全部参数
    getDataSourceParam(key) {
      if (this.componentType == "select-tree") {
        return key ? this.treeOptionParams[key] : this.treeOptionParams;
      } else if (this.dataSourceProvider) {
        return key ? this.dataSourceProvider.getParam(key) : this.dataSourceProvider.options.params;
      }
      return undefined;
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
    getSelectedLabel(selectedLabels) {
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
      if (this.fetched && this.dataSourceDataMapping.length > 0) {
        const setMappingValues = (selectedValue) => {
          this.dataSourceDataMapping.map((item) => {
            let targetValue = [];
            let sourceField = item["sourceField"];
            const targetField = item["targetField"];
            if (selectedValue) {
              selectedValue.map((v) => {
                if (this.optionsValueMap[v] && this.optionsValueMap[v][sourceField]) {
                  targetValue.push(this.optionsValueMap[v][sourceField]);
                } else {
                  sourceField = sourceField.toLowerCase();
                  if (this.dataModelDatasMap[v] && this.dataModelDatasMap[v][sourceField]) {
                    if (Array.isArray(this.dataModelDatasMap[v][sourceField])) {
                      this.dataModelDatasMap[v][sourceField].map((data) => {
                        if (data.fileID) {
                          targetValue.push(data.fileID);
                        }
                      });
                    } else {
                      targetValue.push(this.dataModelDatasMap[v][sourceField]);
                    }
                  }
                }
              });
            }
            this.form.setFieldValue(targetField, targetValue.length ? targetValue.join(";") : null);
          });
        };

        if (this.optionType == "dataModel" || this.optionType == "dataSource") {
          if (value && typeof value === "string") {
            value = value.split(";");
          }
          if (!this.dataModelDatasMap) {
            this.dataModelDatasMap = {};
          }
          let needFetchFormData = false;
          let allFetch = [],
            dataUuids = [],
            formUuid,
            sourceFieldMap = {};
          for (let index = 0; index < this.dataSourceDataMapping.length; index++) {
            const item = this.dataSourceDataMapping[index];
            const sourceField = item["sourceField"];
            const targetField = item["targetField"];
            sourceFieldMap[sourceField] = item;

            if (value) {
              for (let i = 0; i < value.length; i++) {
                const v = value[i];
                if (
                  this.optionsValueMap[v] &&
                  !this.dataModelDatasMap[v] &&
                  (this.optionsValueMap[v][sourceField] === null || this.optionsValueMap[v][sourceField] === undefined)
                ) {
                  formUuid = this.optionsValueMap[v]["FORM_UUID"] || this.optionsValueMap[v]["formUuid"];
                  const dataUuid = this.optionsValueMap[v]["UUID"] || this.optionsValueMap[v]["uuid"];
                  if (formUuid && !dataUuids.includes(dataUuid)) {
                    dataUuids.push(dataUuid);
                    allFetch.push(this.fetchFormData(formUuid, dataUuid));
                  }
                  needFetchFormData = true;
                }
              }
            }
          }
          if (needFetchFormData) {
            Promise.all(allFetch).then((res) => {
              res.map((item) => {
                if (item && item[formUuid]) {
                  item[formUuid].map((d) => {
                    this.dataModelDatasMap[d.uuid] = d;
                  });
                }
              });
              setMappingValues(value);
            });
          } else {
            setMappingValues(value);
          }
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
            if (parentIndex !== undefined) {
              this.selectOptions[parentIndex]["options"].splice(index, 1);
            }
            let hasIndex = findIndex(this.options, (opt) => {
              return opt.uuid == item.uuid;
            });
            if (hasIndex > -1) {
              this.options.splice(hasIndex, 1);
            }
          } else {
            this.selectOptions.splice(index, 1);
          }
          this.removeLabelOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
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
              if (parentIndex !== undefined) {
                this.selectOptions[parentIndex]["options"].push(req);
              }
              this.options.push(req);
            } else {
              this.selectOptions.push(req);
            }
            this.removeLabelOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
          }
        }
      });
      // this.inputFocus();
    },
    // 排序字典序
    onSortOption({ direction, item, index, selectedOptions }, event) {
      this.onSelectedDictOption({ item, index, selectedOptions });
      utils.swapArrayElements(
        [item.uuid],
        selectedOptions,
        function (a, b) {
          return a == b.uuid;
        },
        direction,
        () => {}
      );
      const req = {};
      selectedOptions.forEach((item, index) => {
        req[item.uuid] = index;
      });

      this.$axios({
        method: "post",
        url: `/api/datadict/updateItemSortOrder`,
        data: req,
      }).then((res) => {
        this.removeLabelOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
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
          item.text = item.label;
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

      console.log(this.widget.configuration.options.dataSourceDataMapping);
      this.setRelatedValue();
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
      } else if (options.type == "dataModel") {
        this.fetchTreeDataByDataSource(options, true);
      } else if (options.type == "apiLinkService") {
        this.fetchTreeDataByApiLink(options);
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
      this.fetched = true;
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
        this.fetched = true;
        this.setValueLabelMap(this.treeData);
        this.setSelectedLabes();
        this.setRelatedValue();
        this.emitChange({}, false);
      });
    },
    // 获取树形下拉框 数据仓库备选项
    fetchTreeDataByDataSource(options, isDataModel) {
      let _this = this;
      this.fetched = false;
      options = utils.deepClone(options);
      options.defaultCondition =
        this.treeOptionDefaultCondition === undefined ? options.defaultCondition : this.treeOptionDefaultCondition;
      options.params = this.treeOptionParams;
      const key = `TreeNodesByDataSource:${options.dataSourceId}:${utils.md5(JSON.stringify(options))}`;
      this.cacheKey = key;
      // 多次重复调用，去缓存获取，有则返回，无则排队等待调用完成后被回调
      storage.getStorageCache(
        this.cacheKey,
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
            this.$axios.post("/json/data/services", requiredOption).then(({ data }) => {
              if (data.code == 0) {
                let results = data.data;
                let _continue = () => {
                  resolve(this.treeData || []);
                };
                if (options.autoTranslate && _this.$i18n.locale !== "zh_CN") {
                  let labels = this.translateTreeData(results);
                  _this.$i18n
                    .$translate(_this, labels, "zh", _this.$i18n.locale.split("_")[0])
                    .then((translateData) => {
                      this.treeData = this.reSetTreeData(results, translateData);
                      _continue();
                    });
                } else {
                  this.treeData = this.reSetTreeData(results);
                  _continue();
                }
              }
            });
          });
        },
        (results) => {
          this.treeData = this.reSetTreeData(results);
          this.treeDatas = utils.deepClone(this.treeData);
          this.fetched = true;
          this.setValueLabelMap(this.treeData);
          this.setSelectedLabes();
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
            _this.treeData = _this.reSetTreeData(list);
            _this.treeDatas = deepClone(_this.treeData);
            _this.setValueLabelMap(_this.treeData);
            _this.setSelectedLabels();
            _this.setRelatedValue();
            _this.emitChange({}, false);
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
    translateTreeData(treeData, labels = []) {
      const tree2Map = (data) => {
        data = this.sortTreeData(data);
        each(data, (child) => {
          child.s_tree_title = child.display || child.name || child.label;
          labels.push(child.s_tree_title);
          if (this.widget.configuration.options.dataSourceExtendColumn) {
            child.s_tree_extend = child.data[this.widget.configuration.options.dataSourceExtendColumn];
            labels.push(child.s_tree_extend);
          }
          if (child.children && child.children.length) {
            each(child.children, (c_child) => {
              c_child.s_tree_title = c_child.display || c_child.name || c_child.label;
              labels.push(c_child.s_tree_title);
            });
            tree2Map(child.children);
          }
        });
      };
      tree2Map(treeData);
      return labels;
    },
    sortTreeData(data) {
      if (
        this.widget.configuration.options.type == "dataSource" ||
        this.widget.configuration.options.type == "dataModel"
      ) {
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
    reSetTreeData(treeData, translateData = {}) {
      const tree2Map = (data) => {
        return map(data, (child) => {
          child.s_tree_key = child.id || child.uuid;
          child.s_tree_value = child.real || child.value || child.s_tree_key;
          child.s_tree_title = child.display || child.name || child.label;
          if (this.widget.configuration.options.type == "selfDefine") {
            // 自定义取配置的国际化值
            child.s_tree_title = this.$t(child.id, child.s_tree_title);
          } else if (child.s_tree_title && translateData[child.s_tree_title]) {
            child.s_tree_title = translateData[child.s_tree_title];
          }
          // 所有选项data值
          this.optionsValueMap[child.s_tree_value] = child.data;
          if (this.widget.configuration.options.dataSourceExtendColumn) {
            child.s_tree_extend = child.data[this.widget.configuration.options.dataSourceExtendColumn];
            if (child.s_tree_extend && translateData[child.s_tree_extend]) {
              child.s_tree_extend = translateData[child.s_tree_extend];
            }
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
    onFilter({ searchValue, comparator, source, ignoreCase }) {
      return new Promise((resolve, reject) => {
        if (source != undefined) {
          // 由外部提供数据源进行判断
          let sources = source.split(";");
          let searchValues = searchValue.split(";");
          for (let i = 0, len = searchValues.length; i < len; i++) {
            if (comparator == "like") {
              // 模糊匹配
              for (let j = 0, jlen = sources.length; j < jlen; j++) {
                let s = sources[j];
                if (this.selectValueOptionMap[s] != undefined) {
                  let label = this.selectValueOptionMap[s].label;
                  if (
                    ignoreCase
                      ? label.toLowerCase().indexOf(searchValues[i].toLowerCase()) != -1
                      : label.indexOf(searchValues[i]) != -1
                  ) {
                    resolve(true);
                    return;
                  }
                }
              }
            } else {
              for (let j = 0, jlen = sources.length; j < jlen; j++) {
                if (sources.includes(searchValues[i])) {
                  resolve(true);
                  return;
                }
              }
            }
          }
          resolve(false);
        }
        //TODO: 判断本组件值是否匹配
        resolve(false);
      });
    },
    fetchFormData(formUuid, dataUuid) {
      return new Promise((resolve, reject) => {
        const params = {
          args: JSON.stringify([formUuid, dataUuid]),
          methodName: "getFormData",
          serviceName: "dyFormFacade",
        };
        this.$axios
          .post("/json/data/services", {
            ...params,
          })
          .then((res) => {
            if (res.status === 200) {
              if (res.data && res.data.code === 0) {
                const data = res.data.data;
                resolve(data);
              } else {
                reject(res.data);
              }
            } else {
              reject(res);
            }
          });
      });
    },
    showTreePickerPopup(show) {
      if (show) {
        if (
          (this.widget.configuration.options.type == "dataSource" ||
            this.widget.configuration.options.type == "dataModel") &&
          this.widget.configuration.options.dataSourceLoadEveryTime
        ) {
          this.reloadTreeData();
        }
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
  },
};
</script>
