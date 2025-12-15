<template>
  <div class="widget-table-search" v-if="searchConfiguration.type != null">
    <a-config-provider :locale="locale">
      <component
        v-if="
          widget.configuration.search.enableTemplate &&
          widget.configuration.search.searchTemplateName &&
          widget.configuration.search.searchTemplatePosition == 'top'
        "
        :is="widget.configuration.search.searchTemplateName"
        @search="onCustomSearch"
      />
      <!-- 关键字/高级搜索 -->
      <a-form-model
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        :model="searchForm"
        v-if="searchConfiguration.type == 'keywordAdvanceSearch'"
        :layout="showKeywordSearch ? 'inline' : 'horizontal'"
        ref="searchForm"
      >
        <div v-if="enableKeywordSearch || enableAdvanceSearch" class="widget-table-search-form-row" v-show="showKeywordSearch">
          <template v-if="!visibleKeywordSearch">
            <a-form-model-item prop="keyword">
              <a-input
                v-model.trim="searchForm.keyword"
                :placeholder="$t('WidgetTable.search.keywordPlaceholder', '关键字')"
                allow-clear
                @change="onChangeKeyword"
                @keyup="onKeyupSearch"
              >
                <a-select slot="addonBefore" v-model.trim="searchForm.keywordColumn" style="width: 90px" v-if="hasColumnSearchGroup">
                  <a-select-option value="0">{{ $t('WidgetTable.search.searchAllOption', '全部') }}</a-select-option>
                  <a-select-option
                    v-for="(opt, i) in searchConfiguration.columnSearchGroup"
                    :key="'columnSearchGroupOption_' + i"
                    :value="opt.dataIndex"
                  >
                    {{ $t(opt.id, opt.title) }}
                  </a-select-option>
                </a-select>

                <a-icon slot="prefix" type="search" />
              </a-input>
            </a-form-model-item>
            <a-form-model-item>
              <a-button type="primary" @click="onKeywordSearchClick">{{ $t('WidgetTable.search.searchButtonText', '查询') }}</a-button>
            </a-form-model-item>
          </template>

          <a-form-model-item v-show="enableAdvanceSearch && enableKeywordSearch">
            <a-button type="link" @click="openMoreAdvanceSearch">
              {{ $t('WidgetTable.search.more', '更多') }}
              <a-icon type="double-right" :rotate="90" />
            </a-button>
          </a-form-model-item>
        </div>

        <div v-if="enableAdvanceSearch" v-show="!showKeywordSearch">
          <a-row
            class="widget-table-search-form-row advance-form"
            :style="{
              display: 'flex',
              'flex-wrap': 'wrap',
              width: ' calc(100% - 5px)'
            }"
          >
            <a-col :span="searchColSpan" v-for="(item, i) in searchConfiguration.columnAdvanceSearchGroup" :key="'searchItem_' + i">
              <a-form-model-item :prop="'COLUMN_' + item.id">
                <template slot="label">
                  <span class="advance-label" :title="$t(item.id, item.title)">{{ $t(item.id, item.title) }}</span>
                  {{ item.title ? ':' : '' }}
                </template>
                <template v-if="item.searchInputType.type == 'datePicker'">
                  <a-range-picker
                    v-if="item.searchInputType.options.range"
                    :ref="'COLUMN_' + item.id"
                    v-model="searchForm['COLUMN_' + item.id]"
                    :disabled-date="currentDate => disabledDate(currentDate, item.searchInputType.options)"
                    :disabled-time="currentDate => disabledTime(currentDate, item.searchInputType.options)"
                    :mode="[item.mode, item.mode]"
                    :show-time="
                      item.showTime
                        ? {
                            hideDisabledOptions: true,
                            defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')],
                            format: item.showTime.format
                          }
                        : false
                    "
                    :format="item.searchInputType.options.datePattern"
                    @openChange="datePickeHandleOpenChange"
                    @panelChange="
                      (value, mode) => rangePickerHandlePanelChange(value, mode, item.searchInputType.options.datePattern, item)
                    "
                  />
                  <template v-else-if="item.mode == 'month'">
                    <a-month-picker
                      :format="item.searchInputType.options.datePattern"
                      :disabled-date="currentDate => disabledDate(currentDate, item.searchInputType.options)"
                      :disabled-time="currentDate => disabledTime(currentDate, item.searchInputType.options)"
                      v-model="searchForm['COLUMN_' + item.id]"
                      allowClear
                    />
                  </template>
                  <template v-else-if="item.mode == 'datetime'">
                    <a-date-picker
                      :format="item.searchInputType.options.datePattern"
                      :show-time="
                        item.showTime
                          ? {
                              hideDisabledOptions: false,
                              defaultValue: moment('00:00:00', 'HH:mm:ss'),
                              format: item.showTime.format
                            }
                          : false
                      "
                      :disabled-date="currentDate => disabledDate(currentDate, item.searchInputType.options)"
                      :disabled-time="currentDate => disabledTime(currentDate, item.searchInputType.options)"
                      v-model="searchForm['COLUMN_' + item.id]"
                      allowClear
                    />
                  </template>
                  <a-date-picker
                    v-else
                    :format="item.searchInputType.options.datePattern"
                    :disabled-date="currentDate => disabledDate(currentDate, item.searchInputType.options)"
                    :disabled-time="currentDate => disabledTime(currentDate, item.searchInputType.options)"
                    v-model="searchForm['COLUMN_' + item.id]"
                    :mode="item.mode"
                    :ref="'COLUMN_' + item.id"
                    @panelChange="(value, mode) => datePickerHandlePanelChange(value, mode, item.searchInputType.options.datePattern, item)"
                    allowClear
                  />
                </template>
                <template v-if="item.searchInputType.type == 'select'">
                  <a-select
                    :show-search="item.searchInputType.options.searchable"
                    style="width: 100%"
                    v-model="searchForm['COLUMN_' + item.id]"
                    :filter-option="filterOption"
                    :mode="item.searchInputType.options.multiSelectEnable ? 'multiple' : 'default'"
                    :options="advanceColumnSearchStorage[item.id]"
                    allowClear
                  ></a-select>
                </template>
                <template v-if="item.searchInputType.type == 'checkbox'">
                  <a-checkbox-group v-model="searchForm['COLUMN_' + item.id]" :options="advanceColumnSearchStorage[item.id]" />
                </template>
                <template v-if="isSwitchType(item)">
                  <a-switch
                    v-model="searchForm['COLUMN_' + item.id]"
                    :checkedChildren="advanceColumnSearchStorage[item.id][0].label"
                    :unCheckedChildren="advanceColumnSearchStorage[item.id][1].label"
                  />
                </template>
                <template v-else-if="item.searchInputType.type == 'radio'">
                  <a-radio-group v-model="searchForm['COLUMN_' + item.id]" :options="advanceColumnSearchStorage[item.id]" />
                </template>
                <template v-if="item.searchInputType.type == 'input'">
                  <a-input v-model="searchForm['COLUMN_' + item.id]" autocomplete="off" @keyup="onKeyupSearch" allow-clear />
                </template>
                <template v-if="item.searchInputType.type == 'organization'">
                  <OrgSelect
                    :orgUuid="item.searchInputType.options.orgUuid"
                    :displayStyle="item.searchInputType.options.inputDisplayStyle || 'label'"
                    :multiSelect="item.searchInputType.options.multiSelect"
                    :orgType="item.searchInputType.options.orgType"
                    :defaultOrgType="item.searchInputType.options.defaultOrgType"
                    :checkableTypes="item.searchInputType.options.checkableTypes"
                    v-model="searchForm['COLUMN_' + item.id]"
                    :isPathValue="item.searchInputType.options.isPathValue"
                    :titleDisplay="item.searchInputType.options.titlePath === true ? 'titlePath' : 'title'"
                    ref="orgSelect"
                  />
                </template>
                <template v-if="item.searchInputType.type == 'groupSelect'">
                  <a-select
                    :show-search="item.searchInputType.options.searchable"
                    style="width: 100%"
                    v-model="searchForm['COLUMN_' + item.id]"
                    :filter-option="filterOptionForSelectGroup"
                    :mode="item.searchInputType.options.multiSelectEnable ? 'multiple' : 'default'"
                    allowClear
                  >
                    <a-select-opt-group v-for="(grp, i) in advanceColumnSearchStorage[item.id]" :key="'optionGroup-' + i">
                      <span slot="label" class="select-group-divider">
                        <label>{{ grp.label }}</label>
                      </span>
                      <a-select-option v-for="opt in grp.children" :key="opt.value" :title="opt.label" :value="opt.value">
                        {{ opt.label }}
                      </a-select-option>
                    </a-select-opt-group>
                  </a-select>
                </template>
                <template v-if="item.searchInputType.type == 'treeSelect'">
                  <a-tree-select
                    :class="[item.searchInputType.options.searchable ? '' : 'unsearchable-tree-select']"
                    :replaceFields="{ title: 'name', value: 'id' }"
                    :multiple="item.searchInputType.options.multiSelect"
                    :showSearch="item.searchInputType.options.searchable"
                    :treeCheckStrictly="false"
                    treeNodeLabelProp="title"
                    v-model="searchForm['COLUMN_' + item.id]"
                    style="width: 100%"
                    :tree-data="advanceColumnSearchStorage[item.id]"
                    :treeCheckable="item.searchInputType.options.multiSelect"
                    show-checked-strategy="SHOW_ALL"
                    treeNodeFilterProp="title"
                    :dropdownStyle="{
                      'max-height': '400px'
                    }"
                    allowClear
                  />
                </template>
              </a-form-model-item>
            </a-col>
          </a-row>

          <a-row class="widget-table-search-form-row" style="margin-bottom: var(--w-margin-xs)">
            <a-col :span="6" :offset="18" :style="{ textAlign: 'right' }">
              <a-space>
                <a-button type="primary" @click="handleKeywordSearch(1)">{{ $t('WidgetTable.search.searchButtonText', '查询') }}</a-button>
                <a-button @click="handleReset">{{ $t('WidgetTable.search.resetButtonText', '重置') }}</a-button>
                <a-button type="link" v-if="enableAdvanceSearch && enableKeywordSearch" class="more" @click="showKeywordSearch = true">
                  {{ $t('WidgetTable.search.collapse', '收起') }}
                  <a-icon type="double-right" :rotate="-90" />
                </a-button>
              </a-space>
            </a-col>
          </a-row>
        </div>
      </a-form-model>
      <component
        v-if="
          widget.configuration.search.enableTemplate &&
          widget.configuration.search.searchTemplateName &&
          widget.configuration.search.searchTemplatePosition == 'bottom'
        "
        :is="widget.configuration.search.searchTemplateName"
        @search="onCustomSearch"
      />
    </a-config-provider>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import moment from 'moment';
import { groupBy, template as stringTemplate, map } from 'lodash';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import DataSourceBase from '@pageAssembly/app/web/assets/js/commons/dataSource.base.js';
import { executeJSFormula } from '@framework/vue/utils/util';

export default {
  name: 'WidgetTableSearchForm',
  inject: {
    widgetContext: {
      from: 'widgetContext'
    },
    pageContext: {
      from: 'pageContext'
    },
    visibleKeywordSearch: {
      default: function () {
        return this._provided[this.$parent.widget.id + ':visibleKeywordSearch'] || false;
      }
    },
    locale: {
      from: 'locale'
    }
  },
  props: {
    widget: Object
  },
  provide() {
    return {
      widgetTableSearchContext: this
    };
  },
  data() {
    let data = {
      advanceColumnSearchStorage: {},
      fetchAdvance: true,
      searchForm: { keyword: undefined, keywordColumn: '0' },
      enableAdvanceSearch:
        this.widget.configuration.search.type == 'keywordAdvanceSearch' &&
        this.widget.configuration.search.advanceSearchEnable &&
        this.widget.configuration.search.columnAdvanceSearchGroup.length > 0,

      enableKeywordSearch:
        (this.widget.configuration.search.type == 'keywordAdvanceSearch' &&
          this.widget.configuration.search.keywordSearchEnable &&
          this.widget.configuration.search.keywordSearchColumns.length > 0) ||
        (this.widget.configuration.search.columnSearchGroupEnable && this.widget.configuration.search.columnSearchGroup.length > 0),

      showKeywordSearch:
        this.widget.configuration.search.type == 'keywordAdvanceSearch' &&
        (this.widget.configuration.search.keywordSearchEnable || this.widget.configuration.search.columnSearchGroupEnable) &&
        (!this.widget.configuration.search.advanceSearchEnable ||
          (this.widget.configuration.search.advanceSearchEnable && !this.widget.configuration.search.defaultExpandAdvanceSearch)),

      isModePanelChangeFlag: 0 // 日期区间组件模式切换状态
    };

    this.initAdvanceSearchDataParam(data);
    data.customSearchParam = {};
    return data;
  },

  beforeCreate() {},
  components: { OrgSelect },
  computed: {
    labelCol() {
      return { span: this.rowColNumber === 1 ? 2 : 6 };
    },
    wrapperCol() {
      return { span: this.showKeywordSearch ? 24 : this.rowColNumber === 1 ? 22 : 18 };
    },

    rowColNumber() {
      return this.widget.configuration.search.advanceSearchPerRowColNumber;
    },
    searchColSpan() {
      return Math.ceil(24 / this.widget.configuration.search.advanceSearchPerRowColNumber);
    },

    configuration() {
      return this.widget.configuration;
    },

    hasColumnSearchGroup() {
      return this.configuration.search.columnSearchGroupEnable === true && this.configuration.search.columnSearchGroup.length > 0;
    },
    searchConfiguration() {
      return this.configuration.search;
    },
    //组合查询字段的操作符
    columnSearchGroupMap() {
      let group = {};
      for (let i = 0, len = this.searchConfiguration.columnSearchGroup.length; i < len; i++) {
        group[this.searchConfiguration.columnSearchGroup[i].dataIndex] = this.searchConfiguration.columnSearchGroup[i];
      }
      return group;
    },
    columnAdvanceSearchGroupMap() {
      let group = {};
      for (let i = 0, len = this.searchConfiguration.columnAdvanceSearchGroup.length; i < len; i++) {
        group[this.searchConfiguration.columnAdvanceSearchGroup[i].id] = this.searchConfiguration.columnAdvanceSearchGroup[i];
      }
      return group;
    }
  },
  created() {
    this.pageContext.handleEvent(`${this.widget.id}:keywordSearch`, this.handleEventKeywordSearch);
  },
  methods: {
    handleEventKeywordSearch(keyword) {
      this.searchForm.keyword = keyword;
      this.onKeywordSearchClick();
    },
    $t() {
      if (this.widgetContext != undefined) {
        return this.widgetContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
    moment,
    onKeyupSearch(e) {
      if (e.key == 'Enter') {
        this.handleKeywordSearch(1);
      }
    },
    disabledDate(date, option) {
      if (option.selectRangeLimitType == 'beforeTodayUnselect') {
        return date < moment().endOf('day').add(-1, 'd');
      } else if (option.selectRangeLimitType == 'afterTodayUnselect') {
        return date >= moment().endOf('day');
      }

      return false;
    },
    disabledTime(date, option) {
      return false;
    },
    setDatePickerMode(options, index) {
      let showTime = false,
        mode = 'date';
      let datePattern = options.datePattern;
      if (datePattern) {
        let datePatternArr = datePattern.split(' ');
        // 有时间
        if (datePattern.indexOf('HH') > -1) {
          showTime = {
            format: datePatternArr[1]
          };
          mode = 'datetime';
        } else if (datePattern.indexOf('DD') == -1) {
          if (datePattern.indexOf('MM') > -1) {
            mode = 'month';
          } else {
            mode = 'year';
          }
        }
      }
      this.widget.configuration.search.columnAdvanceSearchGroup[index].showTime = showTime;
      this.widget.configuration.search.columnAdvanceSearchGroup[index].mode = mode;
    },
    rangePickerHandlePanelChange(value, mode, datePattern, item) {
      if (mode && Array.isArray(mode) && (mode.includes('month') || mode.includes('year'))) {
        // 月份范围选择，需要这边触发变更（onChange 不会自动触发)
        this.searchForm['COLUMN_' + item.id] = value;
        if (this.$refs['COLUMN_' + item.id]) {
          let index = this.rangeModeIndex(mode);
          if (index > -1) {
            this.$refs['COLUMN_' + item.id][0].$refs.picker.$refs.calendarInstance.setState({ sMode: mode });
            this.isModePanelChangeFlag++;
          } else if (this.isModePanelChangeFlag) {
            this.$refs['COLUMN_' + item.id][0].$refs.picker.$refs.calendarInstance.setState({ sMode: mode });
            this.isModePanelChangeFlag--;
          } else if (value.length == 2) {
            // 关闭弹框
            this.$refs['COLUMN_' + item.id][0].$refs.picker.setState({ sOpen: false });
          }
        }
      }
    },
    // 计算日期区间面板模式，如果存在且与当前模式不一致，则切换日历模式
    rangeModeIndex(mode) {
      // 日历面板模式，如果要切换的模式排在当前模式前面，则不做改变
      let modeArr = ['date', 'month', 'year', 'decade'];
      if (mode[0] && mode[0] != this.mode[0]) {
        if (modeArr.indexOf(mode[0]) < modeArr.indexOf(this.mode[0])) {
          this.isModePanelChangeFlag = 0;
          return -1;
        }
        return 0;
      } else if (mode[1] && mode[1] != 'date' && mode[1] != this.mode[1]) {
        if (modeArr.indexOf(mode[1]) < modeArr.indexOf(this.mode[1])) {
          this.isModePanelChangeFlag = 0;
          return -1;
        }
        return 1;
      }
      return -1;
    },
    datePickeHandleOpenChange(open) {
      if (open) {
        this.isModePanelChangeFlag = 0;
      }
    },
    datePickerHandlePanelChange(value, mode, datePattern, item) {
      // 日期组件，除了date模式外的其他模式，选中后不会触发onChange
      if (mode != 'date') {
        this.searchForm['COLUMN_' + item.id] = value;
        if (this.$refs['COLUMN_' + item.id]) {
          if (mode) {
            // 切换日历模式
            this.$refs['COLUMN_' + item.id][0].$refs.picker.$refs.calendarInstance.setState({ sMode: mode });
          } else if (value) {
            // 关闭弹框
            this.$refs['COLUMN_' + item.id][0].$refs.picker.$children[0].setState({ sOpen: false });
          }
        }
      }
    },
    onChangeKeyword() {
      this.$nextTick(() => {
        if (this.searchForm.keyword == '' || this.searchForm.keyword == null) {
          this.$parent.fetch({});
        }
      });
    },
    onCustomSearch(customSearchParam) {
      this.customSearchParam = {};
      if (customSearchParam) {
        if (customSearchParam.hasOwnProperty('keyword')) {
          this.customSearchParam.keyword = customSearchParam.keyword;
        }
        if (customSearchParam.hasOwnProperty('criterions')) {
          this.customSearchParam.criterions = customSearchParam.criterions;
        }
        if (customSearchParam.hasOwnProperty('params')) {
          this.$parent.addDataSourceParams(customSearchParam.params);
        }
      }
      this.handleKeywordSearch(1);
    },
    getSearchCriterionParams(init) {
      let formData = this.searchForm;
      let params = { keyword: formData.keyword, criterions: [], params: {} };
      let isKeywordColumnSearch = this.hasColumnSearchGroup && formData.keywordColumn != undefined && formData.keywordColumn != '0';
      if (isKeywordColumnSearch && formData.keyword) {
        // 组合字段查询的
        params.criterions.push({
          columnIndex: formData.keywordColumn,
          type: this.columnSearchGroupMap[formData.keywordColumn].operator,
          value: formData.keyword
        });
      }
      // “全部” 按组合字段查询
      if (this.hasColumnSearchGroup && formData.keywordColumn == '0' && formData.keyword) {
        isKeywordColumnSearch = true;
        let { columnSearchGroupEnable, columnSearchGroup } = this.widget.configuration.search;
        if (columnSearchGroupEnable && columnSearchGroup.length > 0) {
          let cr = [];
          for (let i = 0, len = columnSearchGroup.length; i < len; i++) {
            cr.push({
              columnIndex: columnSearchGroup[i].dataIndex,
              type: columnSearchGroup[i].operator,
              value: formData.keyword
            });
          }
          params.criterions.push({ type: 'or', conditions: cr });
        }
      }

      if (!this.showKeywordSearch || init) {
        for (let k in formData) {
          if (k.indexOf('COLUMN_') == 0) {
            let id = k.substr(7);
            let conf = this.columnAdvanceSearchGroupMap[id];
            if (this.isSwitchType(conf)) {
              // 开关值为true||false,都通过
            } else if (formData[k] == undefined || formData[k] == '') {
              continue;
            }
            //高级搜索
            if (conf.searchInputType.type == 'datePicker') {
              let pattern = conf.searchInputType.options.datePattern;
              if (conf.searchInputType.options.range) {
                // 日期范围的
                if (formData[k].length == 2 && formData[k][0] != undefined && formData[k][1] != undefined) {
                  params.criterions.push({
                    columnIndex: conf.dataIndex,
                    type: 'between',
                    value: [
                      typeof formData[k][0] == 'string' ? formData[k][0] : formData[k][0].format(pattern),
                      typeof formData[k][1] == 'string' ? formData[k][1] : formData[k][1].format(pattern)
                    ]
                  });
                } else if (formData[k].length == 1 && (formData[k][0] != undefined || formData[k][1] != undefined)) {
                  let date = formData[k][formData[k][0] != undefined ? 0 : 1];
                  params.criterions.push({
                    columnIndex: conf.dataIndex,
                    type: formData[k][0] != undefined ? '>=' : '<=',
                    value: typeof date == 'string' ? date : date.format(pattern)
                  });
                }
              } else {
                if (formData[k]) {
                  params.criterions.push({
                    columnIndex: conf.dataIndex,
                    type: conf.operator,
                    value: typeof formData[k] == 'string' ? formData[k] : formData[k].format(pattern)
                  });
                }
              }
              continue;
            }
            let value = '';
            if (this.isSwitchType(conf)) {
              value = this.advanceColumnSearchStorage[id][formData[k] ? 0 : 1].value;
            } else {
              value = Array.isArray(formData[k]) ? formData[k].join(';') : formData[k];
            }
            let criterion = {
              columnIndex: conf.dataIndex,
              type: conf.operator,
              value
            };
            if (conf.operator == 'exists') {
              let sql = conf.sql,
                compiler = stringTemplate(sql);
              try {
                sql = compiler({ value: `:${conf.id}` });
              } catch (error) {
                console.error(error);
              }
              criterion.sql = sql;
              delete criterion.value;
              params.params[conf.id] = formData[k];
            }

            params.criterions.push(criterion);
          }
        }
      }

      if (this.widget.configuration.search.keywordSearchEnable && !isKeywordColumnSearch && formData.keyword) {
        let { keywordSearchEnable, keywordSearchColumns } = this.widget.configuration.search;
        if (keywordSearchEnable && keywordSearchColumns.length > 0 && formData.keyword != null) {
          let cr = [];
          for (let i = 0, len = keywordSearchColumns.length; i < len; i++) {
            cr.push({
              columnIndex: keywordSearchColumns[i],
              value: formData.keyword,
              type: 'like'
            });
          }
          params.criterions.push({ type: 'or', conditions: cr });
        }
      }

      if (this.customSearchParam.hasOwnProperty('keyword')) {
        params.keyword = this.customSearchParam.keyword;
      }

      if (this.customSearchParam.hasOwnProperty('criterions')) {
        params.criterions.push(...this.customSearchParam.criterions);
      }
      return params;
    },
    onKeywordSearchClick() {
      this.handleKeywordSearch(1);
    },
    handleKeywordSearch(current) {
      if (typeof current == 'number' && this.$parent.pagination !== false) {
        this.$parent.pagination.current = current;
      }
      this.$parent.fetch(this.getSearchCriterionParams());
    },

    handleReset() {
      this.$refs.searchForm.resetFields();
      this.initAdvanceSearchDataParam(this.$data, false);
      // for (let i = 0, len = this.widget.configuration.search.columnAdvanceSearchGroup.length; i < len; i++) {
      //   let search = this.widget.configuration.search.columnAdvanceSearchGroup[i];
      //   this.searchForm['COLUMN_' + search.id] = undefined;
      // }
      this.handleKeywordSearch(1);
    },

    openMoreAdvanceSearch() {
      this.showKeywordSearch = !this.showKeywordSearch;
      if (!this.showKeywordSearch && this.enableAdvanceSearch) {
        this.fetchAdvanceColumnSearchOptions();
      }
    },

    initAdvanceSearchDataParam(data, init = true) {
      data.hasDefaultValue = false; // 是否有默认值
      let variableData = this.widgetContext.widgetDependentVariableDataSource();
      for (let i = 0, len = this.widget.configuration.search.columnAdvanceSearchGroup.length; i < len; i++) {
        let search = this.widget.configuration.search.columnAdvanceSearchGroup[i];
        data.searchForm['COLUMN_' + search.id] = undefined;
        let searchInputType = search.searchInputType;
        if (
          searchInputType.type == 'checkbox' ||
          (['select', 'groupSelect', 'treeSelect'].includes(searchInputType.type) && searchInputType.options.multiSelectEnable) ||
          (searchInputType.type == 'datePicker' && searchInputType.options.range)
        ) {
          data.searchForm['COLUMN_' + search.id] = [];
        }
        if (init) {
          if (
            search.defaultValueFormula &&
            search.defaultValueFormula.value != undefined &&
            search.defaultValueFormula.value.trim() != ''
          ) {
            // 默认值公式
            data.hasDefaultValue = true;
            // 默认值公式计算
            executeJSFormula(search.defaultValueFormula.value, variableData).then(result => {
              if (search.searchInputType.type == 'datePicker') {
                let setDataValue = (d, index) => {
                  if (d instanceof Date) {
                    if (index != undefined) {
                      data.searchForm['COLUMN_' + search.id].push(moment(d));
                    } else {
                      data.searchForm['COLUMN_' + search.id] = moment(d);
                    }
                  } else if (typeof d == 'string') {
                    // 时间字符串
                    let dateParsed = moment(
                      d,
                      ['YYYY-MM-DD HH:mm:ss', 'YYYY年MM月DD日 HH时mm分ss秒', 'YYYY年MM月DD日 HH:mm:ss', 'YYYYMMDDHHmmSS'],
                      true
                    );
                    if (!dateParsed.isValid()) {
                      console.error('不支持的日期格式识别: ', d);
                    } else {
                      if (index != undefined) {
                        data.searchForm['COLUMN_' + search.id].push(dateParsed);
                      } else {
                        data.searchForm['COLUMN_' + search.id] = dateParsed;
                      }
                    }
                  } else if (typeof d == 'number') {
                    // 时间戳
                    if (index != undefined) {
                      data.searchForm['COLUMN_' + search.id].push(moment(d));
                    } else {
                      data.searchForm['COLUMN_' + search.id] = moment(d);
                    }
                  }
                };
                if (searchInputType.options.range) {
                  if (Array.isArray(result) && result.length == 2) {
                    for (let i = 0; i < result.length; i++) {
                      setDataValue(result[i], i);
                    }
                  }
                } else {
                  setDataValue(result);
                }
              } else {
                data.searchForm['COLUMN_' + search.id] = result;
              }
            });
          } else if (search.defaultValue != undefined && search.defaultValue.trim() != '') {
            // 兼容旧的默认值计算逻辑
            data.hasDefaultValue = true;
            data.searchForm['COLUMN_' + search.id] = search.defaultValue;
            let searchInputType = search.searchInputType;
            if (searchInputType.type == 'datePicker') {
              if (searchInputType.options.range) {
                let parts = search.defaultValue.split(';');
                data.searchForm['COLUMN_' + search.id] = [parts[0], parts.length > 1 ? parts[1] : undefined];
              }
            } else if (
              searchInputType.type == 'checkbox' ||
              (['select', 'groupSelect', 'treeSelect'].includes(searchInputType.type) && searchInputType.options.multiSelectEnable)
            ) {
              data.searchForm['COLUMN_' + search.id] = search.defaultValue.split(';');
            }
          }

          data.advanceColumnSearchStorage[search.id] = null;
          if (search.searchInputType.type == 'radio' && search.searchInputType.options.switchStyle) {
            data.advanceColumnSearchStorage[search.id] = [{}, {}]; // 单选-开关模式的参数
          }
        }
        if (search.searchInputType.type == 'datePicker') {
          this.setDatePickerMode(search.searchInputType.options, i);
        }
      }
    },

    fetchAdvanceColumnSearchOptions() {
      if (this.fetchAdvanceColumnSearchOptionsInvoked) {
        return;
      }

      for (let i = 0, len = this.searchConfiguration.columnAdvanceSearchGroup.length; i < len; i++) {
        let search = this.searchConfiguration.columnAdvanceSearchGroup[i];
        if (['select', 'checkbox', 'radio', 'treeSelect'].indexOf(search.searchInputType.type) != -1) {
          if (search.searchInputType.options.type == 'selfDefine') {
            this.advanceColumnSearchStorage[search.id] = map(search.searchInputType.options.defineOptions, item => {
              item.label = this.$t(search.id + '_' + item.id, item.label);
              return item;
            });
            this.initSwitchValue(search);
          } else if (search.searchInputType.options.type == 'dataDictionary') {
            this.fetchAdvanceSearchSelectOptionByDataDic(search.searchInputType.options.dataDictionaryUuid, search.id, search);
          } else if (search.searchInputType.options.type == 'dataSource') {
            if (search.searchInputType.type == 'treeSelect') {
              this.fetchDataSourceDataToTreeOption(search.searchInputType.options, search.id);
            } else {
              this.fetchAdvanceSearchSelectOptionByDataSource(search.searchInputType.options, search.id, search);
            }
          }
        } else if (search.searchInputType.type == 'groupSelect') {
          this.fetchDataSourceData(
            search.searchInputType.options.dataSourceId,
            search.searchInputType.options.defaultCondition ? [{ sql: search.searchInputType.options.defaultCondition }] : []
          ).then(results => {
            if (results) {
              this.advanceColumnSearchStorage[search.id] = this.convertList2GroupOptions(results, search.searchInputType.options);
            }
          });
        }
      }
      this.fetchAdvanceColumnSearchOptionsInvoked = true;
    },
    convertList2GroupOptions(list, opt) {
      let options = [];
      let { dataSourceValueColumn, dataSourceLabelColumn, dataSourceGroupColumn } = opt;
      let groupMap = groupBy(list, d => {
        return d[dataSourceGroupColumn];
      });
      for (let g in groupMap) {
        let group = {
          label: g,
          children: []
        };
        let children = groupMap[g];
        for (let i = 0, len = children.length; i < len; i++) {
          group.children.push({
            label: children[i][dataSourceLabelColumn],
            value: children[i][dataSourceValueColumn]
          });
        }
        options.push(group);
      }

      return options;
    },

    fetchAdvanceSearchSelectOptionByDataSource(options, id, search) {
      $axios
        .post('/common/select2/query', {
          serviceName: 'select2DataStoreQueryService',
          queryMethod: 'loadSelectData',
          dataStoreId: options.dataSourceId,
          idColumnIndex: options.dataSourceValueColumn,
          textColumnIndex: options.dataSourceLabelColumn
        })
        .then(({ data }) => {
          let o = [];
          if (data.results && data.results.length) {
            for (let i = 0, len = data.results.length; i < len; i++) {
              o.push({
                value: data.results[i].id,
                label: data.results[i].text
              });
            }
            this.advanceColumnSearchStorage[id] = o;
            this.initSwitchValue(search);
          }
        });
    },
    fetchDataSourceData(dataSourceId, defaultCriterions) {
      return new Promise((resolve, reject) => {
        this.dataSourceProvider = new DataSourceBase({
          dataStoreId: dataSourceId,
          onDataChange: function (data, count, params) {
            let results = data.data;
            resolve(results);
          },
          receiver: this,
          defaultCriterions,
          pageSize: -1
        });
        this.dataSourceProvider.load();
      });
    },

    fetchDataSourceDataToTreeOption(options, id) {
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'cdDataStoreService',
            methodName: 'loadTreeNodes',
            args: JSON.stringify([
              {
                dataStoreId: options.dataSourceId,
                uniqueColumn: options.dataSourceKeyColumn,
                parentColumn: options.dataSourceParentColumn,
                displayColumn: options.dataSourceLabelColumn,
                valueColumn: options.dataSourceValueColumn,
                async: false
              }
            ])
          })
          .then(({ data }) => {
            if (data.code == 0) {
              let treeNodeValueReplace = list => {
                if (list) {
                  for (let i = 0, len = list.length; i < len; i++) {
                    treeNodeValueReplace(list[i].children);
                    list[i].id = list[i].data[options.dataSourceValueColumn];
                  }
                }
              };
              treeNodeValueReplace(data.data);
              this.advanceColumnSearchStorage[id] = data.data;
            }
          });
      });
    },
    fetchAdvanceSearchSelectOptionByDataDic(dataDicUuid, id, search) {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'cdDataDictionaryFacadeService',
          methodName: 'listItemByDictionaryCode',
          args: JSON.stringify([dataDicUuid])
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            // data.data.selectable = false;
            let options = data.data;
            // for (let i = 0, len = data.data.length; i < len; i++) {
            //   options.push({
            //     label: data.data[i].name,
            //     value: data.data[i].code
            //   });
            // }
            _this.advanceColumnSearchStorage[id] = options;
            _this.initSwitchValue(search);
          }
        });
    },

    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    filterOptionForSelectGroup(input, option) {
      if (option.componentOptions.tag == 'a-select-option') {
        return option.componentOptions.propsData.title.toLowerCase().indexOf(input.toLowerCase()) >= 0;
      }
      return false;
    },

    initSwitchValue(search) {
      if (search.searchInputType.type == 'radio' && search.searchInputType.options.switchStyle) {
        if (this.advanceColumnSearchStorage[search.id].length == 0) {
          this.advanceColumnSearchStorage[search.id].push({
            label: '',
            value: true
          });
          this.advanceColumnSearchStorage[search.id].push({
            label: '',
            value: false
          });
        }
      }
      if (this.isSwitchType(search)) {
        this.searchForm['COLUMN_' + search.id] =
          this.searchForm['COLUMN_' + search.id] == this.advanceColumnSearchStorage[search.id][0].value;
      }
    },
    isSwitchType(search) {
      return (
        search.searchInputType.type == 'radio' &&
        search.searchInputType.options.switchStyle &&
        this.advanceColumnSearchStorage[search.id].length == 2
      );
    }
  },
  mounted() {
    if (this.enableAdvanceSearch && !this.showKeywordSearch) {
      this.fetchAdvanceColumnSearchOptions();
      // 树形下拉不支持多选情况下禁用搜索功能，通过挂载后dom操作实现
      let treeSelect = this.$el.querySelectorAll('.unsearchable-tree-select');
      if (treeSelect) {
        for (let t of treeSelect) {
          let inputSearch = t.querySelector('.ant-select-search__field');
          if (inputSearch) {
            inputSearch.setAttribute('readonly', 'true');
          }
        }
      }
    }
  },
  watch: {
    showKeywordSearch: {
      handler(value) {
        this.$emit('changeShowKeywordSearch', value);
      }
    }
  }
};
</script>
