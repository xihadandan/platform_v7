<template>
  <view :class="['nav-bar-select', `content-align-${contentAlignDir}`]">
    <uni-data-select
      :localdata="formatSelectOptions"
      :multiple="isMultiple"
      :clear="true"
      v-model="value"
      @change="onChange"
      emptyTips="无选项"
    />
  </view>
</template>

<script>
import { debounce, uniqBy, orderBy, groupBy } from "lodash";
import pageWidgetMixin from "../page-widget-mixin";
import { DataStore } from "wellapp-uni-framework";

export default {
  name: "nav-bar-select",
  mixins: [pageWidgetMixin],
  props: {
    contentAlignDir: {
      type: String,
      default: "right",
    },
    navBarWidget: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      value: null,
      tokenSeparators: ";",
      options: [], // 全部的选项数据
      selectOptions: [], // 下拉框选项
      fetched: false,
      selectedLabels: [], // 选中label数组
      valueLabelMap: {},
      dataSourceResults: [],
      optionsValueMap: {},
      pageSize: 50,
      pageIndex: 0,
      loading: false,
      dropType: "dropdown",
    };
  },
  computed: {
    isMultiple() {
      return this.widget.configuration.selectMode === "multiple";
    },
    formatSelectOptions() {
      return this.selectOptions.map((item) => {
        return {
          text: item.label,
          value: item.value,
        };
      });
    },
  },
  created() {
    // this.pageContext.handleEvent(`${this.navBarWidget.id}:${this.widget.id}:setValue`, this.setValue);
    this.fetchSelectOptions();
  },
  methods: {
    fetchSelectOptions() {
      // 常量
      if (this.widget.configuration.options.type == "selfDefine") {
        this.options = this.widget.configuration.options.defineOptions;
        this.selectOptions = this.widget.configuration.options.defineOptions;
        for (let o of this.selectOptions) {
          o.label = this.$t(o.id, o.label);
          this.optionsValueMap[o.value] = o;
        }
        this.fetched = true;
      } else if (this.widget.configuration.options.type == "dataDictionary") {
        this.fetchSelectOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
      } else if (this.widget.configuration.options.type == "dataSource") {
        this.fetchSelectOptionByDataSource(this.widget.configuration.options);
      } else if (this.widget.configuration.options.type == "dataModel") {
        this.fetchSelectOptionByDataSource(
          this.widget.configuration.options,
          `/proxy/api/dm/loadData/${this.widget.configuration.options.dataModelUuid}`,
          `/proxy/api/dm/loadDataCount/${this.widget.configuration.options.dataModelUuid}`
        );
      }
      // else if (this.widget.configuration.options.type == 'apiLinkService') {
      //   this.fetchSelectOptionByApiLink(this.widget.configuration.options);
      // }
    },
    // 获取下拉框数据字典
    fetchSelectOptionByDataDic(dataDicUuid) {
      let _this = this,
        isGroup = this.widget.configuration.type === "select-group";
      this.loading = true;
      this.$axios
        .post("/json/data/services", {
          serviceName: "cdDataDictionaryFacadeService",
          methodName: "listItemByDictionaryCode",
          args: JSON.stringify([dataDicUuid]),
        })
        .then(({ data }) => {
          let selectOptions = [];
          if (data.code == 0 && data.data) {
            for (let i = 0, len = data.data.length; i < len; i++) {
              let opt = {};
              if (isGroup) {
                let group = { label: data.data[i].name, options: [] },
                  jlen = data.data[i].children.length;
                for (let j = 0; j < jlen; j++) {
                  opt = {
                    label: data.data[i].children[j].label,
                    value: data.data[i].children[j].value,
                  };
                  group.options.push(opt);
                  _this.options.push(opt);
                }
                if (jlen > 0) selectOptions.push(group);
              } else {
                opt = {
                  label: data.data[i].label,
                  value: data.data[i].value,
                };
                _this.optionsValueMap[opt.value] = data.data[i];
                selectOptions.push(opt);
                _this.options.push(opt);
              }
            }
          }
          _this.loading = false;
          _this.fetched = true;
          _this.selectOptions = selectOptions;
          _this.setValueLabelMap(selectOptions);
          _this.setSelectedLabels();
        });
    },
    // 获取下拉框数据仓库
    fetchSelectOptionByDataSource(options, loadDataUrl, loadDataCntUrl) {
      let _this = this;
      let valueColIndex = options.dataSourceValueColumn,
        labelColIndex = options.dataSourceLabelColumn,
        groupColIndex = options.groupColumn;
      let opt = { dataStoreId: options.dataSourceId };
      if (loadDataUrl != undefined) {
        opt.loadDataUrl = loadDataUrl;
      }
      if (loadDataCntUrl != undefined) {
        opt.loadDataCntUrl = loadDataCntUrl;
      }
      this.loading = true;
      this.dataSourceProvider = new DataStore({
        ...opt,
        onDataChange: function (data, count, params) {
          _this.loading = false;
          let results = data.data;
          if (results.length > 0) {
            let opts = [];
            let initValue = _this.getValue();
            if (typeof initValue === "string") {
              initValue = [initValue];
            }
            let initValOptions = [],
              labels = [];
            for (let i = 0, len = results.length; i < len; i++) {
              let opt = { label: results[i][labelColIndex], value: results[i][valueColIndex] };
              if (opt.label != undefined) {
                labels.push(opt.label);
              }
              if (initValue && initValue.indexOf(results[i][valueColIndex]) != -1) {
                initValOptions.push(opt);
              }
              opts.push(opt);
              _this.optionsValueMap[results[i][valueColIndex]] = results[i];
            }
            _this.dataSourceResults = results;
            if (_this.widget.configuration.type === "select-group") {
              opts = orderBy(opts, [groupColIndex]);
            }

            let _continue = () => {
              _this.options = opts;
              _this.fetched = true;
              _this.selectOptions = opts;
              _this.setValueLabelMap(opts);
              _this.setSelectedLabels();
              _this.onPageOptions((_this.pageIndex = 0), 0, initValOptions);
            };

            if (options.autoTranslate && _this.$i18n.locale !== "zh_CN") {
              _this.$i18n.$translate(labels, "zh", _this.$i18n.locale.split("_")[0]).then((map) => {
                for (let o of opts) {
                  let rst = _this.optionsValueMap[o.value];
                  if (o.label && map[o.label.trim()]) {
                    o.label = map[o.label.trim()];
                    rst[labelColIndex] = o.label;
                  }
                }
                _continue();
              });
            } else {
              _continue();
            }
          }
        },
        receiver: this,
        defaultCriterions: options.defaultCondition //默认条件
          ? [
              {
                sql: options.defaultCondition,
              },
            ]
          : [],
        pageSize: this.pageSize * 1000, // FIXME: 下拉数据多的情况下，会明显卡顿，是否增加分页加载数据(需要引导用户知道需要滚动加载更多)
      });
      this.dataSourceProvider.load();
    },
    setSelectedLabels() {
      this.selectedLabels.splice(0, this.selectedLabels.length);
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
    setValueLabelMap(options) {
      for (let i = 0, len = options.length; i < len; i++) {
        const value = options[i].value;
        const label = options[i].label;
        if (value && label) {
          this.valueLabelMap[value] = label;
        }
        if (options[i]["options"]) {
          this.setValueLabelMap(options[i]["options"]);
        }
      }
    },
    getValue() {
      return this.value;
    },
    setValue(value) {
      this.value = value;
    },
    onChange(value) {
      const optionItem = this.optionsValueMap[value];
      this.$emit("change", {
        value,
        optionItem,
      });
      this.pageContext.emitEvent(`${this.navBarWidget.id}:${this.widget.id}:change`, {
        value,
        optionItem,
      });
    },
  },
};
</script>

<style lang="scss">
.nav-bar-select {
  ::v-deep .uni-stat__select {
    .uni-select {
      border-color: transparent;
    }
    .uni-select__input-text {
      max-width: 100px;
      font-size: 14px;
      color: var(--color-title);
    }
    .uni-icons {
      &.uniui-bottom {
        font-family: "ant-iconfont";
        &::before {
          content: "\e7c7";
          color: var(--color-title);
        }
      }
      &.uniui-top {
        font-family: "ant-iconfont";
        &::before {
          content: "\e7c2";
          color: var(--color-title);
        }
      }
      &.uniui-clear {
        &::before {
          font-size: 20px;
        }
      }
    }
    .uni-select__selector {
      width: 120px;
    }
  }

  &.content-align-right {
    ::v-deep .uni-select__selector {
      left: auto;
      right: 0;
      .uni-popper__arrow_bottom {
        left: auto;
        right: 10%;
      }
    }
  }
}
</style>
