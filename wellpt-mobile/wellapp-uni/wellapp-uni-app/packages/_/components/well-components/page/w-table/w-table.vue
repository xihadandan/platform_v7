<template>
  <view class="w-table" :class="customClassCom" v-show="vShow">
    <template v-if="widget.configuration.enableTitle">
      <view
        class="_header-title"
        :style="{
          backgroundColor: '#fff',
          padding: '8px',
          color: widget.configuration.titleColor ? widget.configuration.titleColor : '',
        }"
      >
        {{ $t("title", this.widget.title) }}
      </view>
    </template>
    <w-list-view
      ref="listView"
      :widget="listWidget"
      :parent="widget"
      :key="widget.wtype + '_' + widget.id"
      @wListViewOnShow="wListViewOnShow"
      @getDataSourceProvider="getDataSourceProvider"
      @selectionChange="selectionChange"
      @beforeLoadData="beforeLoadData"
      @itemClick="itemClick"
      @buttonClick="buttonClick"
      @beforeRenderRowData="beforeRenderRowData"
    ></w-list-view>
  </view>
</template>

<script>
// #ifndef APP-PLUS
import "./index.scss";
// #endif
import pageWidgetMixin from "../page-widget-mixin";
import { assign, filter, find, each } from "lodash";
import { utils } from "wellapp-uni-framework";
export default {
  mixins: [pageWidgetMixin],
  name: "w-table",
  props: {
    id: String,
    widget: Object, // 组件定义
    widgetsOfParent: Array, // 父组件的所有子组件
    parent: Object, // 父组件
    index: Number, //当前组件在父组件的子组件列表的序号
  },
  data() {
    return {
      listWidget: this.setListWidget(),
    };
  },
  computed: {
    // 相关方法暴露为事件，提供外部调用
    defaultEvents() {
      return [{ id: "refetch", title: "重新加载数据", information: "可通过事件参数传递查询参数进行加载数据" }];
    },
  },
  methods: {
    refetch(options) {
      this.refresh(options);
    },
    refresh(options) {
      this.$refs.listView.refresh(options);
    },
    setListWidget() {
      if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
        let showColums = this.widget.configuration.columns.filter((col) => !col.hidden);
        let uniDefaultTemplateOptions = [
          { label: "标题", value: "title" },
          { label: "小标题", value: "subtitle" },
          { label: "备注", value: "note" },
          { label: "右侧文本", value: "rightText" },
        ];
        let templateProperties = [];
        if (showColums.length > 0) {
          for (let i = 0; i < showColums.length; i++) {
            let col = showColums[i];
            let templateOption = uniDefaultTemplateOptions[i];
            if (templateOption) {
              templateProperties.push({
                uuid: utils.generateId(),
                title: col.title,
                name: templateOption.value,
                mapColumn: col.dataIndex,
                mapColumnName: col.title,
                renderer: {},
                sortOrder: "",
              });
            } else {
              break;
            }
          }
        }
        this.$set(this.widget.configuration, "uniConfiguration", {
          displayStyle: "2", //1列表2卡片
          templateProperties,
          showSortOrder: true,
          readMarker: false,
          backgroundStyle: {
            backgroundColor: "#ffffff", // 白底
            backgroundImage: undefined,
            backgroundImageInput: undefined,
            bgImageUseInput: false,
            backgroundRepeat: undefined,
            backgroundPosition: undefined,
          },
        });
      }
      let primaryFieldCol = find(this.widget.configuration.columns, (col) => col.primaryKey);
      let configuration = {
        hasSearch:
          this.widget.configuration.search.type == "keywordAdvanceSearch" &&
          this.widget.configuration.search.keywordSearchEnable &&
          this.widget.configuration.search.keywordSearchColumns.length > 0,
        pageSize: 5,
        readMarker: false,
        columns: this.widget.configuration.columns,
        templateProperties: [],
        dataModelId: this.widget.configuration.dataModelId,
        dataModelName: this.widget.configuration.dataModelName,
        dataModelType: this.widget.configuration.dataModelType,
        dataModelUuid: this.widget.configuration.dataModelUuid,
        dataSourceId: this.widget.configuration.dataSourceId,
        dataSourceName: this.widget.configuration.dataSourceName,
        readMarkerField: "",
        primaryField: primaryFieldCol ? primaryFieldCol.dataIndex : "", // 主键
        itemContentType: "default",
        rowClickEvent: {
          enable: this.widget.configuration.rowClickEvent.enable,
          option: this.widget.configuration.rowClickEvent.eventHandlers.length
            ? this.widget.configuration.rowClickEvent.eventHandlers[0]
            : {},
        },
        buttons: this.getBottons(),
        query: {
          fields: this.getQueryFields(),
          fieldSearch: this.widget.configuration.search.advanceSearchEnable,
        },
        keyword:
          this.widget.configuration.search.type == "keywordAdvanceSearch" &&
          this.widget.configuration.search.keywordSearchEnable,
        fieldSearch: this.widget.configuration.search.advanceSearchEnable,
        showSortOrder: true,
        displayStyle: "2",
        template: "",
        customTemplateHtml: "",
        backgroundStyle: {
          bgImageUseInput: false,
          backgroundRepeat: "no-repeat",
          backgroundPosition: "top",
        },
        padding: "",
        margin: "",
        spacing: "",
        borderRadius: "",
        mainStyle: "",
        uniJsModule: "",
        rightWidth: "",
      };
      let widget = utils.deepClone(this.widget);
      widget.configuration = assign(widget.configuration, configuration, widget.configuration.uniConfiguration);
      console.log(widget.configuration);
      this.listWidget = widget;
      return widget;
    },
    getBottons() {
      let buttons = [];
      // if (this.parent.wtype == "WidgetDataManagerView") {
      //   // 数据管理按钮功能尚未开发，暂时隐藏
      //   return buttons;
      // }
      let rowButtons = this.widget.configuration.rowButton.buttons;
      let headerButtons = this.widget.configuration.headerButton.buttons;
      if (this.widget.configuration.rowButton.enable) {
        each(rowButtons, (btn) => {
          buttons.push(this.getButton(btn, !this.widget.configuration.uniConfiguration.rowButtonPosition ? "5" : "3"));
        });
      }
      if (this.widget.configuration.headerButton.enable) {
        each(headerButtons, (btn) => {
          buttons.push(this.getButton(btn, "2"));
        });
      }
      return buttons;
    },
    // position； [{ label: '表格头部', value: '1' },{ label: '表格底部', value: '2' },// { label: '悬浮行上', value: '4' },{ label: '行末', value: '3' }， { label: '行下', value: '5' }],
    getButton(button, position) {
      return assign(
        {},
        {
          checked: false,
          code: button.id,
          type: button.style.type || "primary",
          mini: false,
          plain: false,
          group: "",
          position: [position],
          text: button.title,
          icon: button.style.icon || "",
          defaultVisible: button.visible,
          eventManger: {
            trigger: "click",
          },
          uuid: button.id,
          hiddenText: button.style.textHidden,
          textHidden: button.style.textHidden,
        },
        button
      );
    },
    getQueryFields() {
      let fields = [];
      if (this.widget.configuration.search.advanceSearchEnable) {
        each(this.widget.configuration.search.columnAdvanceSearchGroup, (item) => {
          let _item = {
            queryOptions: {
              options: item.searchInputType.options,
              queryType: item.searchInputType.type,
            },
            name: item.dataIndex,
            label: item.title,
            uuid: item.id,
          };
          fields.push(assign({}, _item, item));
        });
      }
      return fields;
    },
    refetch() {
      this.$refs.listView.refetch(arguments);
    },
    itemClick() {
      this.$emit("itemClick", arguments);
    },
    buttonClick() {
      this.$emit("buttonClick", arguments);
    },
    beforeLoadData() {
      this.$emit("beforeLoadData", arguments);
    },
    selectionChange() {
      this.$emit("selectionChange", arguments);
    },
    beforeRenderRowData() {
      this.$emit("beforeRenderRowData", arguments);
    },
    getDataSourceProvider() {
      this.$emit("getDataSourceProvider", arguments);
    },
    wListViewOnShow() {
      this.$emit("wListViewOnShow", arguments);
    },
  },
  watch: {
    widget: {
      deep: true,
      handler(val) {
        this.setListWidget();
      },
    },
  },
};
</script>

<style>
/* #ifdef APP-PLUS */
@import "./index.scss";
/* #endif */
</style>
