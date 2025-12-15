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
      <template v-if="componentType == 'select'">
        <a-select
          ref="curCompRef"
          v-model="value"
          :mode="selectMode"
          :allowClear="allowClear"
          :showSearch="showSearch"
          :filter-option="isFilterOption"
          :placeholder="$t('placeholder', widget.configuration.placeholder)"
          :getPopupContainer="getPopupContainer()"
          :disabled="disable || readonly"
          :showArrow="true"
          :style="{ width: '100%', maxWidth: '1920px' }"
          :dropdownStyle="{ ...dropdownMaxWidthStyle }"
          :dropdownMenuStyle="dropdownMenuStyle"
          :dropdownClassName="dropdownClassName"
          :dropdownMatchSelectWidth="dropdownMatchSelectWidth"
          @search="onSelectSearch"
          @focus="onFocus"
          @change="onSelectChange"
          @blur="onBlur"
          @popupScroll="onPopupScroll"
          @dropdownVisibleChange="onDropdownVisibleChange"
          optionLabelProp="title"
          :class="['widget-form-select', fetched ? '' : 'select-option-unloaded', showSearch ? '' : 'select-multiple-readonly']"
        >
          <a-icon type="loading" slot="suffixIcon" v-if="loading" />
          <template slot="dropdownRender" slot-scope="menu">
            <v-nodes :vnodes="menu" />
            <a-spin size="small" :style="{ marginLeft: '10px' }" v-show="loading" />
            <div v-if="isMultiple && editMode.selectCheckAll" class="widget-select-check-all" @click="checkAllChange">
              <a-checkbox :checked="checkAll">{{ $t('WidgetFormSelect.selectAll', '全选') }}</a-checkbox>
            </div>
            <div class="search-loading-div" v-show="searchLoading">
              <a-spin size="small" />
            </div>
          </template>
          <!-- 多选时选中图标 -->
          <template slot="menuItemSelectedIcon">
            <Icon type="pticon iconfont icon-ptkj-duoxuan-weixuan" />
          </template>
          <a-select-option
            v-for="(item, index) in selectOptions"
            :key="item.value"
            :value="item.value"
            :disabled="item.disabled"
            :title="item.label"
            :class="[editMoreItem === item.value ? 'edit-more-item' : '']"
          >
            {{ item.label }}
            <span class="extend_column" v-if="item.extend_label">
              {{ item.extend_label }}
            </span>
            <edit-dict-operation
              ref="dictRef"
              :selectOptions="selectOptions"
              :current="item"
              :currentIndex="index"
              :configuration="widget.configuration"
              @visible="editVisibleChange"
              @addOption="onAddOption"
              @delOption="onDelOption"
              @sortOption="onSortOption"
              @editMoreItemChange="editMoreItemChange"
            />
          </a-select-option>
        </a-select>
      </template>
      <template v-if="componentType == 'select-group'">
        <a-select
          ref="curCompRef"
          v-model="value"
          :mode="selectMode"
          :allowClear="allowClear"
          :showArrow="true"
          :showSearch="showSearch"
          :filter-option="isFilterOption"
          :placeholder="$t('placeholder', widget.configuration.placeholder)"
          :getPopupContainer="getPopupContainer()"
          :disabled="disable || readonly"
          :style="{ width: '100%', maxWidth: '1920px' }"
          :dropdownStyle="{ ...dropdownMaxWidthStyle }"
          :dropdownMenuStyle="dropdownMenuStyle"
          :dropdownClassName="dropdownClassName"
          :dropdownMatchSelectWidth="dropdownMatchSelectWidth"
          @search="onSelectSearch"
          @change="onSelectChange"
          @blur="onBlur"
          @focus="onFocus"
          @dropdownVisibleChange="onDropdownVisibleChange"
          optionLabelProp="title"
          :class="['widget-form-select', fetched ? '' : 'select-option-unloaded', showSearch ? '' : 'select-multiple-readonly']"
        >
          <template slot="dropdownRender" slot-scope="menu">
            <v-nodes :vnodes="menu" />
            <a-spin size="small" :style="{ marginLeft: '10px' }" v-show="loading" />
            <div v-if="isMultiple && editMode.selectCheckAll" class="widget-select-check-all" @click="checkAllChange">
              <a-checkbox :checked="checkAll">{{ $t('WidgetFormSelect.selectAll', '全选') }}</a-checkbox>
            </div>
            <div class="search-loading-div" v-show="searchLoading">
              <a-spin size="small" />
            </div>
          </template>
          <!-- 多选时选中图标 -->
          <template slot="menuItemSelectedIcon">
            <Icon type="pticon iconfont icon-ptkj-duoxuan-weixuan" />
          </template>
          <a-select-opt-group v-for="(grp, i) in selectOptions" :key="'optionGroup-' + i">
            <span slot="label" class="select-group-divider">
              <label :title="grp.label">{{ grp.label }}</label>
            </span>
            <a-select-option
              v-for="(opt, index) in grp.options"
              :key="opt.value"
              :value="opt.value"
              :title="opt.label"
              :class="[editMoreItem === opt.value ? 'edit-more-item' : '']"
            >
              {{ opt.label }}
              <span class="extend_column" v-if="opt.extend_label">
                {{ opt.extend_label }}
              </span>
              <edit-dict-operation
                ref="dictRef"
                :selectOptions="grp.options"
                :current="opt"
                :currentIndex="index"
                :parentIndex="i"
                :configuration="widget.configuration"
                @visible="editVisibleChange"
                @addOption="onAddOption"
                @delOption="onDelOption"
                @sortOption="onSortOption"
                @editMoreItemChange="editMoreItemChange"
              />
            </a-select-option>
          </a-select-opt-group>
        </a-select>
      </template>
      <template v-if="componentType == 'select-tree'">
        <div v-if="this.designMode">
          <!-- 设计模式情况下的显示效果 -->
          <a-tree-select
            :allowClear="allowClear"
            :showSearch="showSearch"
            :multiple="isMultiple"
            :placeholder="$t('placeholder', widget.configuration.placeholder)"
            :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
            style="width: 100%"
            :showArrow="true"
          />
        </div>
        <w-tree-select
          ref="curCompRef"
          v-else
          :treeData="treeData"
          :value="value"
          :multiple="isMultiple"
          :editMode="widget.configuration.editMode"
          :dropdownMatchSelectWidth="dropdownMatchSelectWidth"
          :showCheckedStrategy="widget.configuration.editMode.selectParent ? 'SHOW_ALL' : 'SHOW_CHILD'"
          :placeholder="$t('placeholder', widget.configuration.placeholder)"
          :disabled="disable || readonly"
          :dataSourceLoadEveryTime="
            (widget.configuration.options.type == 'dataSource' || widget.configuration.options.type == 'dataModel') &&
            widget.configuration.options.dataSourceLoadEveryTime
          "
          @reloadTreeData="reloadTreeData"
          @change="onChangeTree"
          @blur="onBlur"
          :dropdownStyle="{ maxHeight: '400px', overflow: 'auto', ...dropdownMaxWidthStyle }"
          :style="{ width: '100%', maxWidth: '1920px' }"
          :class="[showSearch ? '' : 'select-multiple-readonly']"
        />
      </template>
    </template>
    <span v-show="displayAsLabel" class="textonly" :title="selectedLabel">{{ selectedLabel }}</span>
  </a-form-model-item>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formCommonMixin from '../mixin/form-common.mixin';
import DataSourceBase from '@pageAssembly/app/web/assets/js/commons/dataSource.base.js';
import {
  debounce,
  uniqBy,
  orderBy,
  groupBy,
  kebabCase,
  map,
  each,
  union,
  filter,
  isArray,
  findIndex,
  isEmpty,
  template as stringTemplate
} from 'lodash';
import WTreeSelect from './components/WTreeSelect';
import editDictOperation from './components/editDictOperation';
import md5 from '@framework/vue/utils/md5';
import {
  swapArrayElements,
  deepClone,
  jsonataEvaluate,
  evaluateConvertJsonDataFromSchema,
  addElementResizeDetector
} from '@framework/vue/utils/util';
import './css/select.less';

export default {
  extends: FormElement,
  name: 'WidgetFormSelect',
  mixins: [widgetMixin, formCommonMixin],
  data() {
    return {
      openSelect: undefined,
      extraProps: {},
      options: [], // 全部的选项数据
      groupOptions: [], // 记录分组结构的全部分组数据
      selectOptions: [], // 下拉框选项
      fetched: false,
      treeData: [],
      treeDatas: [], // 全部的树选项数据
      value: undefined,
      tempValue: null, // 临时值，用于当选项未加载时候的值存储
      selectedLabels: [], // 选中label数组
      treeFocus: false,
      valueLabelMap: {},
      dataSourceResults: [],
      optionsValueMap: {},
      pageSize: 50,
      pageIndex: 0,
      loading: false,
      checkAll: false, // 全选
      componentType: this.widget.configuration.type,
      allowClear: this.widget.configuration.editMode.allowClear,
      showSearch: this.widget.configuration.editMode.showSearch,
      selectMode: this.widget.configuration.editMode.selectMode,
      tokenSeparators: this.widget.configuration.tokenSeparators,
      dropdownClassName: kebabCase(this.widget.wtype) + ' ps__child--consume', //阻止外层下拉滚动
      popoverVisible: false,
      curDictUuid: '',
      dropdownScope: false,
      dropdownListener: false,
      editMoreItem: undefined,
      isOpen: false,
      searchLoading: false,
      treeOptionParams: {}, // 树形下拉框数据仓库搜索参数
      treeOptionDefaultCondition: undefined, // 树形下拉框数据仓库默认筛选条件
      cacheKey: undefined,
      dataModelDatasMap: {},
      dropdownMatchSelectWidth: true
    };
  },
  components: {
    WTreeSelect,
    VNodes: {
      functional: true,
      render: (h, ctx) => ctx.props.vnodes
    },
    editDictOperation
  },
  computed: {
    // 选中label字符串
    selectedLabel() {
      return this.selectedLabels.join(this.tokenSeparators);
    },
    // 是否多选
    isMultiple() {
      return this.selectMode === 'multiple';
    },
    dataSourceDataMapping() {
      return this.widget.configuration.options.dataSourceDataMapping;
    },
    enableOptions() {
      if (this.componentType == 'select-group') {
        // 可选项
        return filter(this.options, item => {
          return !item.disabled;
        });
      } else {
        // 可选项
        return filter(this.selectOptions, item => {
          return !item.disabled;
        });
      }
    },
    dropdownMenuStyle() {
      let marginTop = 0;
      if (this.isMultiple && this.editMode.selectCheckAll) {
        // 全选
        marginTop += 40;
      }
      return {
        marginTop: marginTop + 'px'
      };
    },
    // 下拉菜单和选择器不同宽，设置下拉框最大宽度
    dropdownMaxWidthStyle() {
      let style = {};
      if (!this.dropdownMatchSelectWidth) {
        style.maxWidth = '300px';
      }
      return style;
    },
    isFilterOption() {
      return this.optionType == 'selfDefine' || this.optionType == 'dataDictionary' ? this.filterOption : false;
    },
    defaultEvents() {
      if (!this.designMode) {
        return [
          {
            id: 'refetchOption',
            title: '重新加载备选项'
          }
        ];
      }
      let eventParams = [];
      if (this.optionType === 'selfDefine' || this.optionType === 'dataDictionary') {
        let eventParamKeyOptions = {
          paramKey: 'options',
          remark: '选项',
          valueSource: {
            inputType:
              this.componentType == 'select-tree' ? 'tree-select' : this.componentType == 'select-group' ? 'group-select' : 'multi-select',
            options: this.eventValueOptions
          }
        };
        if (this.componentType == 'select-tree') {
          eventParamKeyOptions.valueSource.showCheckedStrategy = 'SHOW_ALL';
          eventParamKeyOptions.valueSource.replaceFields = {
            children: 'children',
            title: 's_tree_title',
            key: 's_tree_key',
            value: 's_tree_value'
          };
        }
        eventParams.push(eventParamKeyOptions);
      }
      if (this.optionType == 'dataSource' || this.optionType == 'dataModel') {
        eventParams.push({
          paramKey: 'removeCache',
          remark: '在从表里，该值为true会清除缓存，主表里默认清除缓存',
          valueSource: {
            inputType: 'select',
            options: [
              { label: '是', value: true },
              { label: '否', value: false }
            ]
          }
        });
      }
      return [
        {
          id: 'refetchOption',
          title: '重新加载备选项',
          eventParams
        }
      ];
    },
    eventValueOptions() {
      if (this.componentType == 'select-tree') {
        return this.treeData;
      } else {
        return this.selectOptions;
      }
    },
    selectValueOptionMap() {
      let map = {};
      for (let i = 0, len = this.selectOptions.length; i < len; i++) {
        let opt = this.selectOptions[i];
        if (opt.options != undefined) {
          // 分组的下拉数据
          for (let j = 0, jlen = opt.options.length; j < jlen; j++) {
            map[opt.options[j].value] = opt.options[j];
          }
        } else {
          // 普通下拉数据
          map[this.selectOptions[i].value] = this.selectOptions[i];
        }
      }
      return map;
    }
  },
  mounted() {
    if (!this.designMode) {
      this.init();
      if (this.$attrs.isSubformCell) {
        addElementResizeDetector(this.$el.parentNode, () => {
          let width = this.$el.parentNode.offsetWidth;
          if (width < 200) {
            this.dropdownMatchSelectWidth = false;
          }
        });
      }
    } else {
      this.getEventValueOptions();
    }
  },
  methods: {
    refreshOptions(options) {
      this.refetchOption(options);
    },
    refetchOption(options) {
      if (this.optionType === 'selfDefine' || this.optionType === 'dataDictionary') {
        if (options.eventParams && options.eventParams.options) {
          if (this.componentType == 'select-tree') {
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
        if (!isEmpty(options.eventParams)) {
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
      if (this.componentType == 'select-tree') {
        this.fetchTreeData();
      } else {
        this.fetchSelectOptions();
      }
    },

    // 下拉框，通过事件配置的选项值，筛选常量和字典下拉备选项
    getSelectOptionByValue(values) {
      let selectOptions = this.selectOptions.splice(0, this.selectOptions.length);
      if (this.componentType == 'select-group') {
        if (this.groupOptions.length == 0) {
          this.groupOptions = deepClone(selectOptions);
        }
        each(this.groupOptions, opt => {
          let opts = [];
          each(opt.options, child => {
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
        each(this.options, opt => {
          if (values.indexOf(opt.value) > -1) {
            this.selectOptions.push(opt);
          }
        });
      }
    },
    // 树形下拉框，通过事件配置的选项值，筛选常量和字典下拉备选项
    getTreeDataByValue(values) {
      let treeDatas = deepClone(this.treeDatas);
      this.treeData = this.filterSearchTree(treeDatas, node => {
        if (values.includes(node.s_tree_value)) {
          return true;
        }
        return false;
      });
    },

    filterOption(input, option) {
      if (this.componentType == 'select-group') {
        if (option.componentOptions.tag == 'a-select-option') {
          return option.componentOptions.propsData.title.toLowerCase().indexOf(input.toLowerCase()) >= 0;
        }
        return false;
      }
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    init() {
      if (this.optionDataAutoSet && this.relateKey) {
        this.listenRelateChanged(() => {
          this.checkRelateValue();
        });
      }
      this.addSelectedNumRules({
        min: this.widget.configuration.minCount,
        max: this.widget.configuration.maxCount
      });
      this.setMultipleModeNotSearch();

      this.setValue(this.formData[this.fieldCode], false); // 设初始值
      // 不是联动获取备选项
      if (!this.optionDataAutoSet && (this.componentType == 'select' || this.componentType == 'select-group')) {
        this.fetchSelectOptions();
      }
      if (!this.optionDataAutoSet && this.componentType == 'select-tree') {
        this.fetchTreeData();
      }
    },
    // 多选模式不能搜索
    setMultipleModeNotSearch() {
      if (!this.showSearch && this.isMultiple && this.$refs.curCompRef) {
        const curCompDom = this.$refs.curCompRef.$el;
        const searchField = curCompDom.querySelector('input[class="ant-select-search__field"]');
        searchField && searchField.setAttribute('readonly', 'true');
      }
    },

    fetchSelectOptions() {
      this.selectOptions.splice(0, this.selectOptions.length);
      const options = this.widget.configuration.options;
      if (this.optionType == 'selfDefine') {
        this.getSelectOptionBySelfDefine();
      } else if (this.optionType == 'dataDictionary') {
        this.fetchSelectOptionByDataDic(options.dataDictionaryUuid);
      } else if (this.optionType == 'dataSource') {
        this.fetchSelectOptionByDataSource(options);
      } else if (this.optionType == 'dataModel') {
        this.fetchSelectOptionByDataSource(
          options,
          `/proxy/api/dm/loadData/${options.dataModelUuid}`,
          `/proxy/api/dm/loadDataCount/${options.dataModelUuid}`
        );
      } else if (this.optionType == 'apiLinkService') {
        this.fetchSelectOptionByApiLink(options);
      }
    },
    fetchSelectOptionByApiLink(options) {
      let _this = this;
      _this.selectOptions.splice(0, _this.selectOptions.length);
      _this.loading = true;
      this.fetchDataByApiLinkInvocation(options.apiInvocationConfig).then(results => {
        _this.loading = false;
        if (Array.isArray(results) && results.length > 0) {
          let opts = [];
          let initValue = _this.getValue();
          if (typeof initValue === 'string') {
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
            if (_this.componentType === 'select-group') {
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
            if (_this.componentType === 'select-group') {
              opts = orderBy(opts, ['group']);
            }
            _this.options = opts;
            _this.fetched = true;
            _this.loading = false;
            _this.onPageOptions((_this.pageIndex = 0), 0, initValOptions, 'group');
            _this.setValueLabelMap(opts);
            _this.setSelectedLabels();
            _this.emitChange({}, false);
            _this.setRelatedValue();
          };

          if (options.autoTranslate && _this.$i18n.locale !== 'zh_CN') {
            _this.$translate(labels, 'zh', _this.$i18n.locale.split('_')[0]).then(map => {
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
      if (this.componentType == 'select-group') {
        let groups = [],
          grpIdx = {};
        // 分组
        for (let i = 0, len = this.options.length; i < len; i++) {
          if (grpIdx[this.options[i].group] == undefined) {
            let groupName = this.options[i].group;
            let groupIndex = findIndex(this.widget.configuration.options.groupOptions, { value: this.options[i].group });
            if (groupIndex > -1) {
              groupName = this.$t(this.widget.configuration.options.groupOptions[groupIndex].id, this.options[i].group);
            }
            groups.push({ label: groupName, options: [] });
            grpIdx[this.options[i].group] = groups.length - 1;
          }
          groups[grpIdx[this.options[i].group]].options.push(this.options[i]);
        }
        this.selectOptions = groups;
      } else {
        this.selectOptions.push(...this.widget.configuration.options.defineOptions);
      }
      this.fetched = true;
      if (this.designMode) {
        return false;
      }
      this.setValueLabelMap(this.selectOptions);
      this.setSelectedLabels();
      this.setRelatedValue();
      this.emitChange({}, false);
    },
    // 获取下拉框数据字典
    fetchSelectOptionByDataDic(dataDicUuid) {
      let _this = this,
        isGroup = this.componentType === 'select-group';
      this.loading = true;

      let afterFetchData = data => {
        let selectOptions = [];
        for (let i = 0, len = data.length; i < len; i++) {
          let opt = {};
          if (isGroup) {
            let group = {
              label: data[i].label,
              options: [],
              uuid: data[i].uuid,
              parentUuid: null
            };
            let jlen = data[i].children.length;
            for (let j = 0; j < jlen; j++) {
              opt = {
                label: data[i].children[j].label,
                value: data[i].children[j].value,
                uuid: data[i].children[j].uuid,
                parentUuid: group.uuid
              };
              group.options.push(opt);
              _this.options.push(opt);
            }
            if (jlen > 0) selectOptions.push(group);
          } else {
            opt = {
              label: data[i].label,
              value: data[i].value,
              uuid: data[i].uuid
            };
            selectOptions.push(opt);
            _this.options.push(opt);
          }
        }
        _this.loading = false;
        _this.fetched = true;
        _this.selectOptions.splice(0, _this.selectOptions.length);
        _this.selectOptions.push(...selectOptions);
        if (this.designMode) {
          return false;
        }
        _this.setValueLabelMap(_this.selectOptions);
        _this.setSelectedLabels();
        _this.setRelatedValue();
        _this.emitChange({}, false);
      };
      this.getLabelValueOptionByDataDic(dataDicUuid, (results, key) => {
        if (results && results.length) {
          this.curDictUuid = results[0]['dataDictUuid'];
        }
        afterFetchData.call(_this, results);
        if (key) {
          this.cacheKey = key;
        }
      });
    },
    // 获取下拉框数据仓库
    fetchSelectOptionByDataSource(options, loadDataUrl, loadDataCntUrl) {
      let _this = this;
      let valueColIndex = options.dataSourceValueColumn,
        labelColIndex = options.dataSourceLabelColumn,
        groupColIndex = options.groupColumn,
        extColIndex = this.widget.configuration.options.dataSourceExtendColumn;
      this.loading = true;
      let afterLoadData = results => {
        if (results.length > 0) {
          let opts = [];
          let initValue = _this.getValue();
          if (typeof initValue === 'string') {
            initValue = [initValue];
          }
          let initValOptions = [],
            labels = [];
          for (let i = 0, len = results.length; i < len; i++) {
            let opt = { label: results[i][labelColIndex], value: results[i][valueColIndex] };
            if (opt.label != undefined) {
              labels.push(opt.label);
            }
            if (extColIndex) {
              opt.extend_label = results[i][extColIndex];
              if (opt.extend_label != undefined) {
                labels.push(opt.extend_label);
              }
            }
            if (_this.componentType === 'select-group') {
              opt[groupColIndex] = results[i][groupColIndex];
              if (opt[groupColIndex] != undefined) {
                labels.push(opt[groupColIndex]);
              }
            }
            if (initValue && initValue.indexOf(results[i][valueColIndex]) != -1) {
              initValOptions.push(opt);
            }
            if (results[i]['uuid']) {
              opt.uuid = results[i]['uuid'];
            }
            opts.push(opt);
            _this.optionsValueMap[results[i][valueColIndex]] = results[i];
          }
          let _continue = () => {
            _this.dataSourceResults = results;
            if (_this.componentType === 'select-group') {
              opts = orderBy(opts, [groupColIndex]);
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

          if (options.autoTranslate && _this.$i18n.locale !== 'zh_CN') {
            _this.$translate(labels, 'zh', _this.$i18n.locale.split('_')[0]).then(map => {
              for (let o of opts) {
                let rst = _this.optionsValueMap[o.value];
                if (o.label && map[o.label.trim()]) {
                  o.label = map[o.label.trim()];
                  rst[labelColIndex] = o.label;
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
      let dataSourceProvider = this.getDataSourceProvider(options, loadDataUrl, loadDataCntUrl, () => {
        _this.$tempStorage.removeCache(_this.cacheKey).then(() => {
          _this.fetchSelectOptions();
        });
      });
      let cacheKey = md5(
        JSON.stringify({
          options,
          dsOptions: {
            defaultCriterions: dataSourceProvider.options.defaultCriterions,
            params: dataSourceProvider.options.params,
            criterions: dataSourceProvider.options.criterions
          }
        }) + this.dataSourceProviderQueryHash || ''
      );
      this.cacheKey = cacheKey;
      this.$tempStorage.getCache(
        cacheKey,
        () => {
          return new Promise((resolve, reject) => {
            this.dataSourceProvider.load().then(data => {
              resolve(data);
            });
          });
        },
        results => {
          afterLoadData.call(_this, results.data);
        }
      );
    },
    // getDataSourceProvider(options, loadDataUrl, loadDataCntUrl) {
    //   if (this.dataSourceProvider) {
    //     return this.dataSourceProvider;
    //   } else {
    //     let option = {
    //       dataStoreId: options.dataSourceId,
    //       onDataChange: function (data, count, params) {},
    //       receiver: this,
    //       defaultCriterions: options.defaultCondition //默认条件
    //         ? [
    //             {
    //               sql: options.defaultCondition
    //             }
    //           ]
    //         : [],
    //       pageSize: this.pageSize * 1000 // FIXME: 下拉数据多的情况下，会明显卡顿，是否增加分页加载数据(需要引导用户知道需要滚动加载更多)
    //     };
    //     if (loadDataUrl != undefined) {
    //       option.loadDataUrl = loadDataUrl;
    //     }
    //     if (loadDataCntUrl != undefined) {
    //       option.loadDataCntUrl = loadDataCntUrl;
    //     }
    //     this.dataSourceProvider = new DataSourceBase(option);
    //     return this.dataSourceProvider;
    //   }
    // },
    addDataSourceParams(params) {
      if (this.componentType == 'select-tree') {
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
      if (this.componentType == 'select-tree') {
        this.treeOptionParams = {};
      } else if (this.dataSourceProvider) {
        this.dataSourceProvider.clearParams();
      }
    },
    deleteDataSourceParams(...key) {
      if (this.componentType == 'select-tree') {
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
      if (this.componentType == 'select-tree') {
        return key ? this.treeOptionParams[key] : this.treeOptionParams;
      } else if (this.dataSourceProvider) {
        return key ? this.dataSourceProvider.getParam(key) : this.dataSourceProvider.options.params;
      }
      return undefined;
    },
    setValueLabelMap(options) {
      if (this.componentType === 'select-tree') {
        const tree2Map = data => {
          data.map(child => {
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
          const label = options[i].label;
          if (value && label) {
            this.valueLabelMap[value] = label;
          }
          if (options[i]['options']) {
            this.setValueLabelMap(options[i]['options']);
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
      if (typeof values == 'string') {
        values = values.split(this.tokenSeparators);
      }
      for (let i = 0, len = values.length; i < len; i++) {
        if (this.valueLabelMap[values[i]] != undefined) {
          this.selectedLabels.push(this.valueLabelMap[values[i]]);
        }
      }
      return this.selectedLabels;
    },
    // 下拉框搜索
    onSelectSearch: debounce(function (v) {
      if (!this.isFilterOption) {
        if (v) {
          this.searchLoading = true;
          let selectOptions = this.options.filter(
            // o => (o.label && o.label.toString().indexOf(v) != -1) || (o.value && o.value.toString().indexOf(v) != -1)
            o => o.label && o.label.toString().indexOf(v) != -1
          );
          this.searchValue = v;
          if (this.componentType == 'select-group') {
            // 分组
            let g = groupBy(selectOptions, this.widget.configuration.options.groupColumn);
            let groups = [];
            for (let k in g) {
              groups.push({ label: k, options: g[k] });
            }
            selectOptions = groups;
          }
          this.selectOptions = deepClone(selectOptions);
          this.searchLoading = false;
          this.optionsCompletedRendered = true; // 加载完成
        } else {
          this.onPageOptions((this.pageIndex = 0));
        }
      }
    }, 500),
    // 下拉框选中 option
    onSelectChange(value, opt) {
      if (this.enableOptions && (value == undefined || value.length < this.enableOptions.length)) {
        // 如果当前选中数小于可选项总数，全选不选中
        this.checkAll = false;
      }
      this.selectedLabels.splice(0, this.selectedLabels.length);
      this.selectedOptions = [];
      if (value == '' || value == undefined || value == null || value.length == 0) {
        this.formData[this.fieldCode] = null;
      }
      if (opt) {
        if (Array.isArray(opt)) {
          for (let i = 0, len = opt.length; i < len; i++) {
            const label = opt[i].componentOptions ? opt[i].componentOptions.children[0].text.trim() : opt[i].label;
            this.selectedLabels.push(label);
            this.selectedOptions.push({ label, value: opt[i].value || opt[i].data.key });
          }
          this.formData[this.fieldCode] = value.join(this.tokenSeparators);
        } else {
          const label = opt.componentOptions ? opt.componentOptions.children[0].text.trim() : opt.label;
          this.selectedLabels.push(label);
          this.selectedOptions.push({ label, value });
          this.formData[this.fieldCode] = value;
        }
      }
      this.setRelatedValue();
      // 选中值，默认open不控制下拉框显示隐藏
      if (this.openSelect) {
        this.delOpenProp();
      }
      // 单选时，选中值就直接隐藏下拉框
      if (!this.isMultiple) {
        this.inputBlur();
      }
      this.emitChange();
    },
    fetchFormData(formUuid, dataUuid) {
      return new Promise((resolve, reject) => {
        const params = {
          args: JSON.stringify([formUuid, dataUuid]),
          methodName: 'getFormData',
          serviceName: 'dyFormFacade'
        };
        window.$axios
          .post('/json/data/services', {
            ...params
          })
          .then(res => {
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
    setRelatedValue() {
      let value = this.value;
      if (this.widget.configuration.displayValueField) {
        // 显示值字段
        this.form.setFieldValue(this.widget.configuration.displayValueField, this.selectedLabel);
      }
      // 设置关联值
      if (this.fetched && this.dataSourceDataMapping.length > 0) {
        const setMappingValues = selectedValue => {
          this.dataSourceDataMapping.map(item => {
            let targetValue = [];
            let sourceField = item['sourceField'];
            const targetField = item['targetField'];
            if (selectedValue) {
              selectedValue.map(v => {
                if (this.optionsValueMap[v] && this.optionsValueMap[v][sourceField]) {
                  targetValue.push(this.optionsValueMap[v][sourceField]);
                } else {
                  sourceField = sourceField.toLowerCase();
                  if (this.dataModelDatasMap[v] && this.dataModelDatasMap[v][sourceField]) {
                    if (Array.isArray(this.dataModelDatasMap[v][sourceField])) {
                      this.dataModelDatasMap[v][sourceField].map(data => {
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
            this.form.setFieldValue(targetField, targetValue.length ? targetValue.join(';') : null);
          });
        };

        // if (this.optionType == 'dataSource') {
        //   // 数据仓库
        //   for (let i = 0, len = this.dataSourceDataMapping.length; i < len; i++) {
        //     let allVal = [];
        //     if (value) {
        //       if (typeof value == 'string') {
        //         if (this.optionsValueMap[value] && this.optionsValueMap[value][this.dataSourceDataMapping[i].sourceField]) {
        //           allVal.push(this.optionsValueMap[value][this.dataSourceDataMapping[i].sourceField] || null);
        //         }
        //       } else {
        //         each(value, item => {
        //           if (this.optionsValueMap[item] && this.optionsValueMap[item][this.dataSourceDataMapping[i].sourceField]) {
        //             allVal.push(this.optionsValueMap[item][this.dataSourceDataMapping[i].sourceField] || null);
        //           }
        //         });
        //       }
        //     }
        //     this.form.setFieldValue(this.dataSourceDataMapping[i].targetField, allVal.length ? allVal.join(';') : null);
        //   }
        // }

        if (this.optionType == 'dataModel' || this.optionType == 'dataSource') {
          if (value && typeof value === 'string') {
            value = value.split(';');
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
            const sourceField = item['sourceField'];
            const targetField = item['targetField'];
            sourceFieldMap[sourceField] = item;

            if (value) {
              for (let i = 0; i < value.length; i++) {
                const v = value[i];
                if (
                  this.optionsValueMap[v] &&
                  !this.dataModelDatasMap[v] &&
                  (this.optionsValueMap[v][sourceField] === null || this.optionsValueMap[v][sourceField] === undefined)
                ) {
                  formUuid = this.optionsValueMap[v]['FORM_UUID'] || this.optionsValueMap[v]['formUuid'];
                  const dataUuid = this.optionsValueMap[v]['UUID'] || this.optionsValueMap[v]['uuid'];
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
            Promise.all(allFetch).then(res => {
              res.map(item => {
                if (item && item[formUuid]) {
                  item[formUuid].map(d => {
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

    onPopupScroll: debounce(function (evt) {
      if (evt.target.scrollHeight != 0 && evt.target.scrollHeight - evt.target.scrollTop <= 300 && !this.optionsCompletedRendered) {
        // 加载更多
        this.onPageOptions(++this.pageIndex, 200);
        let scrollTop = evt.target.scrollTop;
        setTimeout(() => {
          evt.target.scrollTop = scrollTop;
        }, 0);
        // this.addOpenProp(true);
      }
    }, 200),
    // 展开下拉菜单的回调
    onDropdownVisibleChange: function (visible) {
      if (visible && this.searchValue != undefined) {
        this.searchValue = null;
        // 单选的清空下，要自动清理掉搜索值，搜索第一页的数据
        this.onPageOptions((this.pageIndex = 0), 0, this.selectOptions);
      }
      if (this.checkAll) {
        // 全选框选中时，判断是否要取消选中
        this.$nextTick(() => {
          if (this.value.length < this.enableOptions.length || this.enableOptions.length == 0) {
            // 如果当前选中数小于可选项总数，全选不选中
            this.checkAll = false;
          }
        });
      }
      if (!visible && !this.isOpen) {
        this.editMoreItem = undefined;
      }
    },
    // 分页选项数据
    onPageOptions(pageIndex, delay, extraOptions, groupCol) {
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
        let _options = uniqBy(_this.selectOptions.concat(_this.options.slice(start, end)).concat(extraOptions || []), 'value');
        if (_this.componentType == 'select-group') {
          // 分组
          let g = groupBy(_options, groupCol || _this.widget.configuration.options.groupColumn);
          let groups = [];
          for (let k in g) {
            groups.push({ label: k, options: g[k] });
          }
          _this.selectOptions = groups;
        } else {
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
    // 获取树形数据
    fetchTreeData() {
      const options = this.widget.configuration.options;
      if (options.type == 'selfDefine') {
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
      if (this.designMode) {
        this.treeData = this.reSetTreeData(this.widget.configuration.treeData);
        this.treeDatas = deepClone(this.treeData);
        return false;
      }
      this.treeData = this.reSetTreeData(this.widget.configuration.treeData);
      this.treeDatas = deepClone(this.treeData);
      this.setValueLabelMap(this.treeData);
      this.setSelectedLabels();
      this.setRelatedValue();
      this.emitChange({}, false);
    },
    // 获取树形下拉框 数据字典备选项
    fetchTreeDataByDataDic(dataDicUuid) {
      this.getLabelValueOptionByDataDic(dataDicUuid, (results, key) => {
        if (key) {
          this.cacheKey = key;
        }
        if (this.designMode) {
          this.treeData = this.reSetTreeData(results);
          this.treeDatas = deepClone(this.treeData);
          return false;
        }
        this.treeData = this.reSetTreeData(results);
        this.treeDatas = deepClone(this.treeData);
        this.setValueLabelMap(this.treeData);
        this.setSelectedLabels();
        this.setRelatedValue();
        this.emitChange({}, false);
      });
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

    // 获取树形下拉框 数据仓库备选项
    fetchTreeDataByDataSource(options, isDataModel, e) {
      this.fetched = false;
      let _this = this;
      options = deepClone(options);
      options.defaultCondition = this.treeOptionDefaultCondition === undefined ? options.defaultCondition : this.treeOptionDefaultCondition;
      options.params = this.treeOptionParams;
      let { defaultCondition, params } =
        e != undefined
          ? e
          : this.explainDefaultCondition(options.defaultCondition, data => {
              _this.$tempStorage.removeCache(_this.cacheKey).then(() => {
                _this.fetchTreeDataByDataSource(options, isDataModel, data);
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
                  if (this.$i18n.locale !== 'zh_CN' && options.autoTranslate) {
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
              })
              .catch(e => {
                console.error(e);
              });
          });
        },
        results => {
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
    fetchTreeDataByApiLink(options) {
      let _this = this;
      this.fetchDataByApiLinkInvocation(options.apiInvocationConfig).then(results => {
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
    sortTreeData(data) {
      if (this.widget.configuration.options.type == 'dataSource' || this.widget.configuration.options.type == 'dataModel') {
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
    reSetTreeData(treeData) {
      this.selectOptions.splice(0, this.selectOptions.length);
      const tree2Map = data => {
        data = this.sortTreeData(data);
        return map(data, child => {
          child.s_tree_key = child.id || child.uuid;
          child.s_tree_value = child.real || child.value || child.s_tree_key;
          child.s_tree_title = child.display || child.name || child.label;
          if (this.widget.configuration.options.type == 'selfDefine') {
            // 自定义取配置的国际化值
            child.s_tree_title = this.$t(child.id, child.s_tree_title);
          }
          this.selectOptions.push({
            label: child.s_tree_title,
            value: child.s_tree_value
          });
          // 所有选项data值
          this.optionsValueMap[child.s_tree_value] = child.data;
          if (this.widget.configuration.options.dataSourceExtendColumn) {
            child.s_tree_extend = child.data[this.widget.configuration.options.dataSourceExtendColumn];
          }
          if (!child.s_tree_label) {
            child.s_tree_label = child.s_tree_title;
          }
          if (child.children && child.children.length) {
            each(child.children, c_child => {
              c_child.s_tree_title = c_child.display || c_child.name || c_child.label;
              if (this.widget.configuration.options.type == 'selfDefine') {
                // 自定义取配置的国际化值
                c_child.s_tree_title = this.$t(c_child.id, c_child.s_tree_title);
              }
              if (this.widget.configuration.editMode.allPath) {
                c_child.s_tree_label = child.s_tree_label + '-' + c_child.s_tree_title;
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
    onChangeTree(value, label, extra) {
      if (!label && isArray(value)) {
        label = map(value, 'label');
      }
      this.selectTreeNodeChange(value, label, extra);
    },
    // 选中树节点
    selectTreeNodeChange(val, label, extra) {
      this.formData[this.fieldCode] = null;
      if (Array.isArray(val)) {
        this.selectedLabels = label;
        this.formData[this.fieldCode] = val.join(this.tokenSeparators);
      } else {
        this.selectedLabels = [label];
        this.formData[this.fieldCode] = val;
      }
      this.value = val;
      this.setRelatedValue();
      this.emitChange();
    },
    // 监听下拉框是否展开
    dropdownEventListener(callback) {
      const dropdownNode = this.$el.querySelector(`.${this.dropdownClassName}`);
      if (dropdownNode) {
        this.dropdownListener = true;
        dropdownNode.addEventListener('mouseenter', () => {
          this.dropdownScope = true;
          if (typeof callback == 'function') {
            callback(this.dropdownScope);
          }
        });
        dropdownNode.addEventListener('mouseleave', () => {
          this.dropdownScope = false;
          if (!this.isOpen) {
            this.delOpenProp();
          }
          if (typeof callback == 'function') {
            callback(this.dropdownScope);
          }
        });
      }
    },
    delOpenProp() {
      this.openSelect = false;
      if (this.componentType !== 'select-tree') {
        delete this.$refs.curCompRef.$refs.vcSelect.$props.open;
      }
    },
    addOpenProp(value) {
      this.openSelect = value;
      if (this.componentType !== 'select-tree') {
        this.$refs.curCompRef.$refs.vcSelect.$props.open = value;
      }
    },
    onBlur() {
      // 如果更多操作弹框出现且单选时，失焦不改变open值，显示下拉框
      if (this.popoverVisible && !this.isMultiple) {
      } else if (!this.popoverVisible) {
        this.delOpenProp();
      }
      this.emitBlur();
    },
    onFocus() {
      // 如果isOpen为true，下拉框为打开状态
      if (this.isOpen) {
        this.addOpenProp(true);
      }
      this.isOpen = false;
      if (!this.dropdownListener) {
        this.$nextTick(() => {
          this.dropdownEventListener();
        });
      }
    },
    // 更多操作弹框显示隐藏
    editVisibleChange(visible) {
      this.popoverVisible = visible;
      // 显示时，下拉框默认open为true，一直显示
      if (visible) {
        this.isOpen = true;
        this.addOpenProp(true);
      } else {
        // 隐藏时，如果下拉框时在显示中，则不改变open保持true，并给选择框焦点
        this.isOpen = false;
        this.inputFocus();
      }
    },
    inputFocus() {
      if (this.componentType !== 'select-tree') {
        this.$refs.curCompRef.$refs.vcSelect.maybeFocus(true, true);
      }
      // this.$refs.curCompRef.$refs.vcSelect.$el.focus();
    },
    inputBlur() {
      if (this.componentType !== 'select-tree') {
        this.$refs.curCompRef.$refs.vcSelect.maybeFocus(false);
      }
      // this.$refs.curCompRef.$refs.vcSelect.$el.blur();
    },
    // 删除备选项
    onDelOption(args) {
      const { index, item, parentIndex } = args;

      let reqOptions = {
        method: 'post',
        url: ''
      };
      let fetchUrl = '';
      const optionType = this.widget.configuration.options.type;
      if (optionType === 'dataDictionary') {
        fetchUrl = '/proxy/api/datadict/deleteItem';
        reqOptions.url = `${fetchUrl}/${item.uuid}`;
      } else if (optionType === 'dataSource') {
        fetchUrl = this.widget.configuration.editMode.optionDelFetchUrl;
        let req = {
          ...args
        };
        req.dataStoreId = this.widget.configuration.options.dataSourceId;
        reqOptions.data = req;
      }
      if (!fetchUrl) {
        this.$message.error('请求地址不能为空');
        return;
      }

      this.$axios(reqOptions).then(res => {
        if (item.parentUuid) {
          this.selectOptions[parentIndex]['options'].splice(index, 1);
          let hasIndex = findIndex(this.options, { uuid: item.uuid });
          if (hasIndex > -1) {
            this.options.splice(hasIndex, 1);
          }
        } else {
          this.selectOptions.splice(index, 1);
        }
        if (optionType === 'dataDictionary') {
          this.removeLabelOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
        }
      });
      this.popoverVisible = false;
      this.editMoreItem = undefined;
      this.$refs.curCompRef.blur();
      // this.inputFocus();
    },
    // 新增备选项
    onAddOption(args) {
      const { index, parentUuid, parentIndex } = args;
      const req = {
        ...args
      };
      if (parentUuid) {
        req.parentUuid = parentUuid;
      }

      let fetchUrl = '';
      const optionType = this.widget.configuration.options.type;
      if (optionType === 'dataDictionary') {
        fetchUrl = '/proxy/api/datadict/saveItem';
        req.dataDictUuid = this.curDictUuid; // 所在数据字典UUID
      } else if (optionType === 'dataSource') {
        fetchUrl = this.widget.configuration.editMode.optionAddFetchUrl;
        req.dataStoreId = this.widget.configuration.options.dataSourceId;
      }
      if (!fetchUrl) {
        this.$message.error('请求地址不能为空');
        return;
      }

      this.$axios({
        method: 'post',
        url: fetchUrl,
        data: req
      }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          const itemUuid = data.data;
          req.uuid = itemUuid;

          if (parentUuid) {
            this.selectOptions[parentIndex]['options'].push(req);
            this.options.push(req);
          } else {
            this.selectOptions.push(req);
          }
          if (optionType === 'dataDictionary') {
            this.removeLabelOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
          }
        }
      });
      // this.inputFocus();
    },
    // 排序备选项
    onSortOption(args) {
      const { direction, item, index } = args;

      let reqOptions = {
        method: 'post',
        url: ''
      };
      let fetchUrl = '';
      const optionType = this.widget.configuration.options.type;
      if (optionType === 'dataDictionary') {
        fetchUrl = '/proxy/api/datadict/updateItemSortOrder';
        reqOptions.url = `${fetchUrl}`;
      } else if (optionType === 'dataSource') {
        fetchUrl = this.widget.configuration.editMode.optionSortFetchUrl;
        let req = {
          ...args
        };
        req.dataStoreId = this.widget.configuration.options.dataSourceId;
        reqOptions.data = req;
      }
      if (!fetchUrl) {
        console.error('请求地址不能为空');
        return;
      }

      swapArrayElements(
        [item.uuid],
        this.selectOptions,
        function (a, b) {
          return a == b.uuid;
        },
        direction,
        () => {}
      );
      const sortMap = {};
      this.selectOptions.forEach((item, index) => {
        sortMap[item.uuid] = index;
      });
      if (optionType === 'dataDictionary') {
        reqOptions.data = sortMap;
      } else if (optionType === 'dataSource') {
        reqOptions.data.sortMap = sortMap;
      }

      this.$axios(reqOptions).then(res => {
        if (optionType === 'dataDictionary') {
          this.removeLabelOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
        }
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
        if (v && typeof v === 'string') {
          this.value = v.split(this.tokenSeparators);
        } else if (!v) {
          this.value = [];
        }
      }

      this.formData[this.fieldCode] = Array.isArray(v) ? v.join(this.tokenSeparators) : v;

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
        if (typeof value == 'string') {
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
      if (this.optionType === 'selfDefine') {
        // 常量
        if (this.componentType === 'select') {
          this.relateSelfDefine();
        }
        if (this.componentType === 'select-tree') {
          this.relateTreeSelfDefine();
        }
      }
      if (this.optionType === 'dataDictionary') {
        if (this.componentType === 'select-tree') {
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
        this.selectOptions = defineOptions.filter(item => {
          return selectOptions.includes(item.value);
        });
        this.setValueLabelMap(this.selectOptions);
      }
      this.fetched = true;
      this.optionsChangeAfter();
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
      this.emitChange({}, false);
    },
    optionsChangeAfter() {
      if (this.componentType === 'select' && this.optionType === 'selfDefine') {
        let val = this.formData[this.fieldCode];
        if (val) {
          let index = findIndex(this.selectOptions, { value: val });
          if (index == -1) {
            this.setValue(null);
          }
        }
        this.setSelectedLabels();
        this.emitChange();
      }
    },
    // 根据条件过滤出树结构
    filterSearchTree(nodes, predicate, wrapMatchFn = () => false) {
      // 如果已经没有节点了，结束递归
      if (!(nodes && nodes.length)) {
        return [];
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
          node.children = subs || [];
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
    checkAllChange: debounce(function () {
      this.checkAll = !this.checkAll;
      if (this.checkAll) {
        // 全选
        this.$set(this, 'value', union(this.value, map(this.enableOptions, 'value')));
        this.onSelectChange(this.value, this.enableOptions);
      } else {
        // 全不选
        this.$set(this, 'value', []);
        this.onSelectChange([], []);
      }
    }),
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
    },
    // 更多操作当前指向选项
    editMoreItemChange(value) {
      this.editMoreItem = value;
    },

    onFilter({ searchValue, comparator, source, ignoreCase }) {
      return new Promise((resolve, reject) => {
        if (source != undefined) {
          // 由外部提供数据源进行判断
          let sources = source.split(';');
          let searchValues = searchValue.split(';');
          for (let i = 0, len = searchValues.length; i < len; i++) {
            if (comparator == 'like') {
              // 模糊匹配
              for (let j = 0, jlen = sources.length; j < jlen; j++) {
                let s = sources[j];
                if (this.selectValueOptionMap[s] != undefined) {
                  let label = this.selectValueOptionMap[s].label;
                  if (
                    ignoreCase ? label.toLowerCase().indexOf(searchValues[i].toLowerCase()) != -1 : label.indexOf(searchValues[i]) != -1
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
    getEventValueOptions() {
      if (this.designMode && this.widget.configuration.options) {
        const options = this.widget.configuration.options;
        if (this.componentType == 'select-tree') {
          if (options.type == 'selfDefine' || (options.type == 'dataDictionary' && options.dataDictionaryUuid)) {
            this.fetchTreeData();
          }
        } else {
          if (options.type == 'selfDefine' || (options.type == 'dataDictionary' && options.dataDictionaryUuid)) {
            this.fetchSelectOptions();
          }
        }
      }
    }
  }
};
</script>

<style lang="less">
.select-multiple-readonly {
  .ant-select-selection--multiple {
    cursor: pointer;
  }
}
// .ps__child--consume{
//   .ant-select-dropdown-menu-item{
//     overflow: visible;
//   }
// }
.ant-select-dropdown-content {
  position: relative;
}
</style>
