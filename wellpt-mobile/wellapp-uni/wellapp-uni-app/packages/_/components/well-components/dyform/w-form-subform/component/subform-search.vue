<template>
  <view class="search-box" v-if="enableAdvanceSearch || enableKeywordSearch">
    <!-- 搜索 -->
    <uni-search-bar
      v-if="enableKeywordSearch"
      class="keyword-query-input"
      radius="5"
      :placeholder="$t('keywordSearchPlaceholder', widget.configuration.search.keywordSearchPlaceholder)"
      :cancelText="$t('global.cancel', '取消')"
      clearButton="auto"
      cancelButton="auto"
      @confirm="keywordQuery"
      @cancel="cancelKeywordQuery"
    />
    <view class="subform-filter" v-if="enableAdvanceSearch" @tap="openFieldQueryDialog">
      <text style="padding-right: 4px">{{ $t("WidgetListView.filter", "筛选") }}</text>
      <w-icon icon="pticon iconfont icon-ptkj-shaixuan" :size="14" />
    </view>

    <!-- 字段选择弹出框 -->
    <uni-popup
      ref="fieldQueryPopup"
      :isMaskClick="false"
      :style="fieldQueryPopupStyle"
      background-color="#ffffff"
      borderRadius="16px 16px 0 0"
    >
      <view class="pt-popop-title">
        <view class="left"></view>
        <view class="center">
          <text>{{ $t("WidgetListView.filter", "筛选") }}</text>
        </view>
        <view class="right">
          <uni-w-button
            type="text"
            @click="$refs.fieldQueryPopup.close()"
            icon="iconfont icon-ptkj-dacha-xiao"
          ></uni-w-button>
        </view>
      </view>
      <view class="pt-popop-content">
        <uni-forms class="field-query-form" labelPosition="top" labelWidth="auto">
          <template v-for="(column, i) in columns">
            <view v-show="false" v-if="column.comparator == 'like'">
              <!-- 模糊匹配查询代理组件 -->
              <widget
                :widget="column.widget[0]"
                :form="proxyDyform"
                :unValidateWidget="true"
                stopEvent
                :ref="'field_' + column.widget[0].configuration.code"
              />
            </view>
            <template v-if="column.comparator == 'like'">
              <uni-forms-item
                :label="$t(column.widget[0].id, column.widget[0].configuration.name)"
                :name="searchForm.column[column.dataIndex]"
                :ref="'field_' + column.widget[0].configuration.code"
              >
                <uni-w-easyinput v-model="searchForm.column[column.dataIndex]" clearable />
              </uni-forms-item>
            </template>
            <template v-else-if="column.comparator == 'between' && column.widget.length == 2">
              <widget
                :widget="column.widget[0]"
                :ref="'field_' + column.widget[0].configuration.code"
                :unValidateWidget="true"
                stopEvent
                :form="dyform"
                class="start-time"
              />
              <view style="text-align: center">
                {{ column.separator }}
              </view>
              <widget
                :widget="column.widget[1]"
                :ref="'field_' + column.widget[1].configuration.code"
                :unValidateWidget="true"
                stopEvent
                :form="dyform"
              />
            </template>
            <widget
              v-else
              :widget="column.widget[0]"
              :ref="'field_' + column.widget[0].configuration.code"
              :unValidateWidget="true"
              stopEvent
              :form="dyform"
            />
          </template>
        </uni-forms>
      </view>
      <view class="field-query-button-group">
        <uni-w-button-group :buttons="buttons" :gutter="16" @click="onTrigger"></uni-w-button-group>
      </view>
    </uni-popup>

    <!-- 代理行数据组件 -->
    <uni-forms
      v-if="enableKeywordSearch && proxyDyform"
      :model="proxyDyform.formData"
      ref="proxyFormRef"
      style="display: none"
    >
      <template v-for="(wgt, i) in proxySearchFieldWidget">
        <widget
          :widget="wgt"
          :ref="'proxySearchField_' + wgt.configuration.code"
          :unValidateWidget="true"
          stopEvent
          :form="proxyDyform"
        />
      </template>
    </uni-forms>
  </view>
</template>
<script type="text/babel">
import { utils, md5 } from "wellapp-uni-framework";
import { createDyform } from "../../w-dyform/dyform";
import { isObject, debounce } from "lodash";

export default {
  name: "SubformSearch",
  inject: ["widgetSubformContext"],
  props: { widget: Object, fields: Array },
  data() {
    let formData = {},
      dyform = createDyform();
    dyform.formData = formData;
    return {
      dyform,
      enableAdvanceSearch:
        this.widget.configuration.search.type == "keywordAdvanceSearch" &&
        this.widget.configuration.search.advanceSearchEnable &&
        this.widget.configuration.search.columnAdvanceSearchGroup.length > 0,

      enableKeywordSearch:
        (this.widget.configuration.search.type == "keywordAdvanceSearch" &&
          this.widget.configuration.search.keywordSearchEnable &&
          this.widget.configuration.search.keywordSearchColumns.length > 0) ||
        (this.widget.configuration.search.columnSearchGroupEnable &&
          this.widget.configuration.search.columnSearchGroup.length > 0),
      inputQueryValue: "",
      columns: [],
      searchForm: { keyword: undefined, keywordColumn: "0", column: formData },
      proxySearchFieldWidget: [],
      buttons: [
        {
          title: this.$t("global.reset", "重置"),
          code: "onReset",
        },
        {
          title: this.$t("global.search", "查询"),
          code: "onOk",
          type: "primary",
        },
      ],
    };
  },
  computed: {
    subformFieldMap() {
      let map = {};
      for (let i = 0, len = this.fields.length; i < len; i++) {
        map[this.fields[i].configuration.code] = this.fields[i];
      }
      return map;
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
  },
  created() {
    // this.pageContext.handleEvent(`${this.widget.id}:keywordSearch`, this.handleEventKeywordSearch);
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
    this.initAdvanceSearch(codes);
  },
  mounted() {
    for (let key in this.$refs) {
      if (key.startsWith("field_") || key.startsWith("proxySearchField_")) {
        // 清空字段组件的校验规则，避免出现规则提示
        let rules = this.$refs[key][0] && this.$refs[key][0].$refs.widget.rules;
        rules.splice(0, rules.length);
      }
    }
  },
  methods: {
    $t() {
      if (this.widgetSubformContext != undefined) {
        return this.widgetSubformContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
    initAdvanceSearch(codes) {
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
            if (colWidget.configuration.uniConfiguration) {
              colWidget.configuration.uniConfiguration.bordered = true;
            } else {
              colWidget.configuration.uniConfiguration = { bordered: true };
            }
            let searchCol = this.columnAdvanceSearchDataIndexMap[colWidget.configuration.code];
            this.$set(this.searchForm.column, searchCol.dataIndex, undefined);
            if (searchCol && searchCol.comparator == "between") {
              let from = this.cloneWidget(colWidget.configuration.code),
                to = this.cloneWidget(colWidget.configuration.code);
              to.configuration.code = colWidget.configuration.code + "_$BETWEEN_END";
              if (from.subtype == "Range" && from.wtype == "WidgetFormDatePicker") {
                // 日期范围要改为非范围显示
                from.configuration.range = false;
                to.configuration.range = false;
              }
              if (from.configuration.uniConfiguration) {
                from.configuration.uniConfiguration.bordered = true;
              } else {
                from.configuration.uniConfiguration = { bordered: true };
              }
              if (to.configuration.uniConfiguration) {
                to.configuration.uniConfiguration.bordered = true;
              } else {
                to.configuration.uniConfiguration = { bordered: true };
              }
              to.configuration.fieldNameVisible = false;

              this.columns.push({
                id: searchCol.id,
                title: searchCol.title,
                comparator: searchCol.comparator,
                dataIndex: searchCol.dataIndex,
                endDataIndex: to.configuration.code,
                separator: searchCol.separator || "至",
                wtype: from.wtype,
                widget: [from, to],
              });
              this.$set(this.searchForm.column, to.configuration.code, undefined);
            } else {
              this.columns.push({
                id: searchCol.id,
                title: searchCol.title,
                comparator: searchCol.comparator,
                wtype: colWidget.wtype,
                dataIndex: searchCol.dataIndex,
                widget: [colWidget],
              });
            }
          }
        }
      }
    },
    isEmptyObject(obj) {
      if (obj == null || obj == undefined || obj === "" || (Array.isArray(obj) && obj.length == 0)) {
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
      if (typeof obj == "object") {
        let keys = Object.keys(obj);
        for (let k in obj) {
          if (obj[k] == null || obj[k] == undefined || obj[k] === "" || (Array.isArray(obj[k]) && obj[k].length == 0)) {
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
        colWidget.id = utils.generateId();
        // 删除默认值
        delete colWidget.configuration.defaultValue;
        delete colWidget.configuration.defaultValueOption;
        colWidget.configuration.defaultDisplayState = "edit"; // 强制为编辑状态

        delete colWidget.configuration.placeholder;

        if (colWidget.wtype == "WidgetFormDatePicker") {
          colWidget.configuration.maxValueSetting.valueType = "no";
          colWidget.configuration.minValueSetting.valueType = "no";
        }
        return colWidget;
      }
      return undefined;
    },
    // 行高级匹配
    rowAdvanceMatcher(row, rowid) {
      return new Promise((resolve, reject) => {
        let promise = [];
        for (let i = 0, len = this.columns.length; i < len; i++) {
          let advanceColumnSearch = this.columns[i],
            code = advanceColumnSearch.dataIndex;
          if (this.$refs["field_" + code] && row[code] != undefined) {
            let refField = this.$refs["field_" + code][0] && this.$refs["field_" + code][0].$refs.widget,
              searchValue = this.searchForm.column[code];
            if (advanceColumnSearch.comparator == "between") {
              searchValue = [searchValue, this.searchForm.column[advanceColumnSearch.endDataIndex]];
            }
            if (searchValue == undefined || searchValue === "" || this.isEmptyObject(searchValue)) {
              continue;
            }
            if (typeof refField.onFilter == "function") {
              let p = refField.onFilter({
                searchValue,
                comparator: advanceColumnSearch.comparator,
                source: row[code],
                ignoreCase: true,
              });
              if (typeof p == "boolean") {
                promise.push(Promise.resolve(p));
                // if (p === true) {
                //   resolve(rowid);
                //   return;
                // }
              } else if (typeof p.then == "function") {
                promise.push(p);
              }
            } else {
              let p = false;
              if (advanceColumnSearch.comparator == undefined || advanceColumnSearch.comparator == "like") {
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
            .then((data) => {
              for (let d = 0, dlen = data.length; d < dlen; d++) {
                if (!data[d]) {
                  resolve(undefined);
                  return;
                }
              }
              resolve(rowid);
            })
            .catch((err) => {
              this.searching = false;
              console.error("搜索异常: ", err);
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
          let refField = this.$refs["proxySearchField_" + code];
          if (refField) {
            refField = refField[0] && refField[0].$refs.widget;
            let p = refField.onFilter({
              searchValue: this.searchForm.keyword.trim(),
              comparator: "like",
              source: row[code],
              ignoreCase: true,
            });
            if (typeof p == "boolean") {
              if (p === true) {
                resolve(rowid);
                return;
              }
            } else if (typeof p.then == "function") {
              promise.push(p);
            }
          }
        }
        if (promise.length) {
          Promise.all(promise)
            .then((data) => {
              for (let d = 0, dlen = data.length; d < dlen; d++) {
                if (data[d]) {
                  resolve(rowid);
                  return;
                }
              }
              resolve(undefined);
            })
            .catch((err) => {
              this.searching = false;
              console.error("搜索异常: ", err);
            });
        } else {
          resolve(undefined);
        }
      });
    },
    // 搜索
    keywordQuery(e) {
      var _self = this;
      _self.searchForm.keyword = e.value;
      _self.search();
    },
    cancelKeywordQuery(e) {
      var _self = this;
      _self.searchForm.keyword = "";
      _self.search();
    },
    openFieldQueryDialog() {
      this.$refs.fieldQueryPopup.open("bottom");
    },
    onChangeSearchForm() {},
    onTrigger(e, button) {
      if (button && this[button.code]) {
        this[button.code]();
      }
    },
    onOk() {
      this.search(true);
    },
    onReset() {
      this.searchForm.keyword = undefined;
      for (let key in this.$refs) {
        if (key.startsWith("field_")) {
          this.$refs[key][0] && this.$refs[key][0].$refs.widget.resetField();
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
      this.$emit("reset");
    },
    /**
     * isAdvanceSearch: true: 高级搜索，false: 关键字搜索
     */
    search: debounce(function (isAdvanceSearch) {
      // 调用代理组件把从表的一些特殊字段组件调用其组件自身的过滤器进行查询
      let promise = [];
      if (
        (!isAdvanceSearch && (this.searchForm.keyword == undefined || this.searchForm.keyword == "")) ||
        (isAdvanceSearch && this.isEmptyObject(this.searchForm.column))
      ) {
        this.searchMD5 = undefined;
        this.$emit("reset");
        return;
      }
      if (this.searching) {
        return;
      }

      let current = utils.md5(
        JSON.stringify({
          rowMd5: this.widgetSubformContext.rowDataAsMD5,
          searchParams: !isAdvanceSearch ? this.searchForm.keyword : this.searchForm.column,
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
          this[!isAdvanceSearch ? "rowKeywordMatcher" : "rowAdvanceMatcher"](
            this.widgetSubformContext.rowForms[row[this.widgetSubformContext.rowKey]].formData,
            rowid
          )
        );
      }

      Promise.all(promise)
        .then((data) => {
          let keys = [];
          for (let i = 0, len = data.length; i < len; i++) {
            if (data[i] != undefined) {
              keys.push(data[i]);
            }
          }
          this.$emit("onFilteredRowKeys", keys);
          this.searching = false;
          if (isAdvanceSearch) {
            this.$refs.fieldQueryPopup.close();
          }
        })
        .catch((err) => {
          this.searching = false;
          console.error("搜索异常: ", err);
        });
    }, 200),
  },
};
</script>
