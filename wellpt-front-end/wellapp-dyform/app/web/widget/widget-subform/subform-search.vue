<template>
  <div v-if="enableKeywordSearch || enableAdvanceSearch">
    <a-form-model
      class="search-form"
      ref="form"
      :colon="false"
      :layout="showKeywordSearch ? 'horizontal' : 'inline'"
      :model="searchForm.column"
      :labelAlign="widget.configuration.search.advanceSearchLabelAlign || 'right'"
    >
      <template v-if="enableKeywordSearch">
        <div v-show="showKeywordSearch">
          <a-form-model-item style="margin-bottom: 0px" :wrapper-col="{ span: 24, style: { width: '100%', textAlign: 'right' } }">
            <template v-if="!visibleKeywordSearch">
              <a-input-search
                style="width: 300px; margin-right: 8px"
                v-model.trim="searchForm.keyword"
                :enter-button="$t('WidgetSubform.search.confirmButtonTitle', '查询')"
                size="small"
                :loading="searching"
                allowClear
                @change="onChangeKeyword"
                :placeholder="$t('keywordSearchPlaceholder', widget.configuration.search.keywordSearchPlaceholder)"
                @search="search"
              />
            </template>

            <a-button size="small" type="link" @click="openMoreAdvanceSearch" v-show="enableAdvanceSearch && enableKeywordSearch">
              {{ $t('WidgetSubform.search.searchMoreText', '更多') }}
              <a-icon type="double-right" :rotate="90" />
            </a-button>
          </a-form-model-item>
        </div>
      </template>

      <template v-if="enableAdvanceSearch">
        <div
          :style="{
            display: !showKeywordSearch ? 'flex' : 'none'
          }"
        >
          <div style="flex: auto">
            <template v-for="(column, i) in columns">
              <a-form-model-item
                class="advance-search-field-form-item"
                :label="$t(column.id, column.title)"
                :label-col="{
                  style: {
                    minWidth: vLabelColWidth
                  }
                }"
                :wrapper-col="{
                  style: {
                    width: vWrapperColWidth
                  }
                }"
              >
                <div v-show="false" v-if="column.comparator == 'like'">
                  <!-- 模糊匹配查询代理组件 -->
                  <component
                    :is="column.wtype"
                    :widget="column.widget[0]"
                    :ref="'field_' + column.widget[0].configuration.code"
                    :labelHidden="true"
                    stopEvent
                    :form="proxyDyform"
                  />
                </div>
                <a-input v-if="column.comparator == 'like'" v-model="searchForm.column[column.dataIndex]" allow-clear />
                <div
                  v-else
                  :style="
                    column.comparator == 'between' && column.widget.length == 2
                      ? {
                          display: 'flex',
                          alignItems: 'flex-start'
                        }
                      : {}
                  "
                >
                  <template v-if="column.comparator == 'between' && column.widget.length == 2">
                    <component
                      :is="column.wtype"
                      :widget="column.widget[0]"
                      :ref="'field_' + column.widget[0].configuration.code"
                      :labelHidden="true"
                      stopEvent
                      :form="dyform"
                    />
                    <span style="padding: 0px 6px">
                      {{ column.separator }}
                    </span>

                    <component
                      :is="column.wtype"
                      :widget="column.widget[1]"
                      :ref="'field_' + column.widget[1].configuration.code"
                      :labelHidden="true"
                      stopEvent
                      :form="dyform"
                    />
                  </template>
                  <component
                    v-else
                    :is="column.wtype"
                    :widget="column.widget[0]"
                    :ref="'field_' + column.widget[0].configuration.code"
                    :labelHidden="true"
                    stopEvent
                    :form="dyform"
                  />
                </div>
              </a-form-model-item>
            </template>
          </div>
          <div style="width: 215px; display: flex; align-items: end">
            <a-space>
              <a-button-group size="small">
                <a-button type="primary" @click="search" :loading="searching">
                  {{ $t('WidgetSubform.search.confirmButtonTitle', '查询') }}
                </a-button>
                <a-button @click="handleReset">{{ $t('WidgetSubform.search.resetButtonTitle', '重置') }}</a-button>
              </a-button-group>

              <a-button
                size="small"
                type="link"
                v-if="enableAdvanceSearch && enableKeywordSearch"
                class="more"
                @click="showKeywordSearch = true"
              >
                {{ $t('WidgetSubform.search.collapseButtonText', '收起') }}
                <a-icon type="double-right" :rotate="-90" />
              </a-button>
            </a-space>
          </div>
        </div>
      </template>
    </a-form-model>
    <!-- 代理搜索组件 -->
    <a-form-model v-if="enableKeywordSearch" style="display: none">
      <template v-for="(wgt, i) in proxySearchFieldWidget">
        <component
          :is="wgt.wtype"
          :widget="wgt"
          :ref="'proxySearchField_' + wgt.configuration.code"
          :labelHidden="true"
          stopEvent
          :form="proxyDyform"
        />
      </template>
    </a-form-model>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { createDyform } from '../../framework/vue/dyform/dyform';
import { isObject, debounce } from 'lodash';
import { generateId, copyToClipboard } from '@framework/vue/utils/util';
import md5 from '@framework/vue/utils/md5';

const keyupEvent = debounce(function (e) {
  if (e.code == 'Enter' || e.keyCode == 13) {
    $app.pageContext.emitEvent('WidgetSubform:focus:search');
  }
}, 300);
export default {
  name: 'SubformSearch',
  inject: {
    widgetSubformContext: {
      from: 'widgetSubformContext'
    },
    designMode: {
      from: 'designMode'
    },
    pageContext: {
      from: 'pageContext'
    },
    visibleKeywordSearch: {
      default: function () {
        return this._provided[this.$parent.widget.id + ':visibleKeywordSearch'] || false;
      }
    }
  },
  props: { widget: Object, fields: Array },
  components: {},
  computed: {
    vLabelColWidth() {
      return this.widget.configuration.search.advanceSearchLabelColWidth != undefined
        ? `${this.widget.configuration.search.advanceSearchLabelColWidth}px`
        : '120px';
    },
    vWrapperColWidth() {
      return this.widget.configuration.search.advanceSearchWrapperColWidth != undefined
        ? `${this.widget.configuration.search.advanceSearchWrapperColWidth}px`
        : '120px';
    },

    columnAdvanceSearchDataIndexMap() {
      let map = {};
      if (this.widget.configuration.search.columnAdvanceSearchGroup) {
        for (let i = 0, len = this.widget.configuration.search.columnAdvanceSearchGroup.length; i < len; i++) {
          map[this.widget.configuration.search.columnAdvanceSearchGroup[i].dataIndex] =
            this.widget.configuration.search.columnAdvanceSearchGroup[i];
        }
      }
      return map;
    },
    subformFieldMap() {
      let map = {};
      for (let i = 0, len = this.fields.length; i < len; i++) {
        map[this.fields[i].configuration.code] = this.fields[i];
      }
      return map;
    }
  },
  data() {
    let formData = {},
      dyform = createDyform();
    dyform.formData = formData;
    return {
      columns: [],
      dyform,
      searchForm: { keyword: undefined, keywordColumn: '0', column: formData },
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

      proxySearchFieldWidget: [],
      searching: false
    };
  },
  beforeCreate() {},
  created() {
    this.pageContext.handleEvent(`${this.widget.id}:keywordSearch`, this.handleEventKeywordSearch);
    this.onChangeSearchForm = debounce(this.onChangeSearchForm.bind(this), 200);

    let codes = [];
    if (this.enableKeywordSearch) {
      for (let i = 0, len = this.widget.configuration.search.keywordSearchColumns.length; i < len; i++) {
        if (!codes.includes(this.widget.configuration.search.keywordSearchColumns[i])) {
          let wgt = this.cloneWidget(this.widget.configuration.search.keywordSearchColumns[i]);
          if (wgt) {
            this.proxySearchFieldWidget.push(wgt);
          }
        }
      }
      this.proxyDyform = createDyform();
      this.proxyDyform.vueInstance = this;
    }
    this.dyform.vueInstance = this;
    if (this.enableAdvanceSearch) {
      for (let i = 0, len = this.widget.configuration.search.columnAdvanceSearchGroup.length; i < len; i++) {
        let colWidget = this.cloneWidget(this.widget.configuration.search.columnAdvanceSearchGroup[i].dataIndex);
        if (colWidget == undefined) {
          continue;
        }
        if (!codes.includes(colWidget.configuration.code)) {
          this.proxySearchFieldWidget.push(colWidget);
        }
        if (colWidget != undefined) {
          let searchCol = this.columnAdvanceSearchDataIndexMap[colWidget.configuration.code];
          this.$set(this.searchForm.column, searchCol.dataIndex, undefined);
          if (searchCol && searchCol.comparator == 'between') {
            let from = this.cloneWidget(colWidget.configuration.code),
              to = this.cloneWidget(colWidget.configuration.code);
            to.configuration.code = colWidget.configuration.code + '_$BETWEEN_END';
            if (from.subtype == 'Range' && from.wtype == 'WidgetFormDatePicker') {
              // 日期范围要改为非范围显示
              from.configuration.range = false;
              to.configuration.range = false;
            }

            this.columns.push({
              id: searchCol.id,
              title: searchCol.title,
              comparator: searchCol.comparator,
              dataIndex: searchCol.dataIndex,
              endDataIndex: to.configuration.code,
              separator: searchCol.separator || '至',
              wtype: from.wtype,
              widget: [from, to]
            });
            this.$set(this.searchForm.column, to.configuration.code, undefined);
          } else {
            this.columns.push({
              id: searchCol.id,
              title: searchCol.title,
              comparator: searchCol.comparator,
              wtype: colWidget.wtype,
              dataIndex: searchCol.dataIndex,
              widget: [colWidget]
            });
          }
        }
      }
    }

    if (!this.enableKeywordSearch) {
      this.showKeywordSearch = false;
    }
  },
  beforeMount() {},
  mounted() {
    for (let key in this.$refs) {
      if (key.startsWith('field_') || key.startsWith('proxySearchField_')) {
        // 清空字段组件的校验规则，避免出现规则提示
        let rules = this.$refs[key][0].rules;
        rules.splice(0, rules.length);
      }
    }
  },
  methods: {
    handleEventKeywordSearch(keyword) {
      this.searchForm.keyword = keyword;
      this.search();
    },
    $t() {
      if (this.widgetSubformContext != undefined) {
        return this.widgetSubformContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
    isEmptyObject(obj) {
      if (obj == null || obj == undefined || obj === '' || (Array.isArray(obj) && obj.length == 0)) {
        return true;
      }
      if (Array.isArray(obj)) {
        let emptyCnt = 0;
        for (let i = 0, len = obj.length; i < len; i++) {
          if (this.isEmptyObject(obj[i])) {
            emptyCnt++;
          }
        }
        if (emptyCnt == obj.length) {
          return true;
        }
        return false;
      }
      if (typeof obj == 'object') {
        let keys = Object.keys(obj);
        for (let k in obj) {
          if (obj[k] == null || obj[k] == undefined || obj[k] === '' || (Array.isArray(obj[k]) && obj[k].length == 0)) {
            keys.splice(keys.indexOf(k), 1);
          }
          if (Array.isArray(obj[k])) {
            let emptyCnt = 0;
            for (let i = 0; i < obj[k].length; i++) {
              if (this.isEmptyObject(obj[k][i])) {
                emptyCnt++;
              }
            }
            if (emptyCnt == obj[k].length) {
              keys.splice(keys.indexOf(k), 1);
            }
          }
        }
        return keys.length == 0;
      }
      return false;
    },
    cloneWidget(dataIndex) {
      if (this.subformFieldMap[dataIndex]) {
        let colWidget = JSON.parse(JSON.stringify(this.subformFieldMap[dataIndex]));
        colWidget.id = generateId();
        // 删除默认值
        delete colWidget.configuration.defaultValue;
        delete colWidget.configuration.defaultValueOption;
        colWidget.configuration.defaultDisplayState = 'edit'; // 强制为编辑状态

        delete colWidget.configuration.placeholder;

        if (colWidget.wtype == 'WidgetFormDatePicker') {
          colWidget.configuration.maxValueSetting.valueType = 'no';
          colWidget.configuration.minValueSetting.valueType = 'no';
        }
        return colWidget;
      }
      return undefined;
    },

    handleReset() {
      this.searchForm.keyword = undefined;
      for (let key in this.$refs) {
        if (key.startsWith('field_')) {
          this.$refs[key][0].resetField();
        }
      }
      if (this.columns.length) {
        for (let col of this.columns) {
          this.searchForm.column[col.dataIndex] = undefined;
          if (col.endDataIndex) {
            this.searchForm.column[col.endDataIndex] = undefined;
          }
        }
      }
      this.$emit('reset');
    },
    openMoreAdvanceSearch() {
      this.showKeywordSearch = !this.showKeywordSearch;
      if (!this.showKeywordSearch && this.enableAdvanceSearch) {
        this.searchForm.keyword = undefined;
      }
    },

    onChangeKeyword() {
      if (this.searchForm.keyword == undefined || this.searchForm.keyword == '') {
        this.searchMD5 = undefined;
        this.$emit('reset');
      }
    },

    search: debounce(function () {
      // 调用代理组件把从表的一些特殊字段组件调用其组件自身的过滤器进行查询
      let promise = [];
      if (
        (this.showKeywordSearch && (this.searchForm.keyword == undefined || this.searchForm.keyword == '')) ||
        (!this.showKeywordSearch && this.isEmptyObject(this.searchForm.column))
      ) {
        this.searchMD5 = undefined;
        this.$emit('reset');
        return;
      }
      if (this.searching) {
        return;
      }

      let current = md5(
        JSON.stringify({
          rowMd5: this.widgetSubformContext.rowDataAsMD5,
          searchParams: this.showKeywordSearch ? this.searchForm.keyword : this.searchForm.column
        })
      );
      if (this.searchMD5 == current) {
        return;
      }
      this.searchMD5 = current;
      this.searching = true;
      for (let j = 0, jlen = this.widgetSubformContext.rows.length; j < jlen; j++) {
        let row = this.widgetSubformContext.rows[j],
          rowid = row[this.widgetSubformContext.rowKey];

        promise.push(
          this[this.showKeywordSearch ? 'rowKeywordMatcher' : 'rowAdvanceMatcher'](
            this.widgetSubformContext.rowForms[row[this.widgetSubformContext.rowKey]].formData,
            rowid
          )
        );
      }

      Promise.all(promise)
        .then(data => {
          let keys = [];
          for (let i = 0, len = data.length; i < len; i++) {
            if (data[i] != undefined) {
              keys.push(data[i]);
            }
          }
          this.$emit('onFilteredRowKeys', keys);
          this.searching = false;
        })
        .catch(err => {
          this.searching = false;
          console.error('搜索异常: ', err);
        });
    }, 200),

    // 行高级匹配
    rowAdvanceMatcher(row, rowid) {
      return new Promise((resolve, reject) => {
        let promise = [];
        for (let i = 0, len = this.columns.length; i < len; i++) {
          let advanceColumnSearch = this.columns[i],
            code = advanceColumnSearch.dataIndex;
          if (this.$refs['field_' + code] && row[code] != undefined) {
            let refField = this.$refs['field_' + code][0],
              searchValue = this.searchForm.column[code];
            if (advanceColumnSearch.comparator == 'between') {
              searchValue = [searchValue, this.searchForm.column[advanceColumnSearch.endDataIndex]];
            }
            if (searchValue == undefined || searchValue === '' || this.isEmptyObject(searchValue)) {
              continue;
            }
            if (typeof refField.onFilter == 'function') {
              let p = refField.onFilter({
                searchValue,
                comparator: advanceColumnSearch.comparator,
                source: row[code],
                ignoreCase: true
              });
              if (typeof p == 'boolean') {
                promise.push(Promise.resolve(p));
                // if (p === true) {
                //   resolve(rowid);
                //   return;
                // }
              } else if (typeof p.then == 'function') {
                promise.push(p);
              }
            } else {
              let p = false;
              if (advanceColumnSearch.comparator == undefined || advanceColumnSearch.comparator == 'like') {
                p = row[code].toString().toLowerCase().indexOf(searchValue.toLowerCase()) != -1;
              } else {
                p = row[code].toString().toLowerCase() == searchValue.toLowerCase();
              }
              promise.push(Promise.resolve(p));
              // if (p) {
              //   resolve(rowid);
              //   return;
              // }
            }
          }
        }
        if (promise.length) {
          Promise.all(promise)
            .then(data => {
              for (let d = 0, dlen = data.length; d < dlen; d++) {
                if (!data[d]) {
                  resolve(undefined);
                  return;
                }
              }
              resolve(rowid);
            })
            .catch(err => {
              this.searching = false;
              console.error('搜索异常: ', err);
            });
        } else {
          resolve(undefined);
        }
      });
    },

    // 行关键字匹配
    rowKeywordMatcher(row, rowid) {
      return new Promise((resolve, reject) => {
        let promise = [];
        for (let i = 0, len = this.widget.configuration.search.keywordSearchColumns.length; i < len; i++) {
          let code = this.widget.configuration.search.keywordSearchColumns[i];
          let refField = this.$refs['proxySearchField_' + code];
          if (refField) {
            refField = refField[0];
            let p = refField.onFilter({
              searchValue: this.searchForm.keyword.trim(),
              comparator: 'like',
              source: row[code],
              ignoreCase: true
            });
            if (typeof p == 'boolean') {
              if (p === true) {
                resolve(rowid);
                return;
              }
            } else if (typeof p.then == 'function') {
              promise.push(p);
            }
          }
        }
        if (promise.length) {
          Promise.all(promise)
            .then(data => {
              for (let d = 0, dlen = data.length; d < dlen; d++) {
                if (data[d]) {
                  resolve(rowid);
                  return;
                }
              }
              resolve(undefined);
            })
            .catch(err => {
              this.searching = false;
              console.error('搜索异常: ', err);
            });
        } else {
          resolve(undefined);
        }
      });
    },

    onChangeSearchForm() {
      document.removeEventListener('keyup', keyupEvent, true);
      document.addEventListener('keyup', keyupEvent, true);
      this.pageContext.offEvent('WidgetSubform:focus:search').handleEvent('WidgetSubform:focus:search', () => {
        this.search();
      });
    }
  },

  watch: {
    searchForm: {
      deep: true,
      handler() {
        if (!this.designMode) {
          this.onChangeSearchForm();
        }
      }
    }
  }
};
</script>
