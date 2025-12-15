<template>
  <view class="w-list-view" :class="customClassCom" :style="mainStyle" v-if="isEmptyUniJsModule">
    <view class="list-header" v-if="hasListHeader" :style="{ marginBottom: listHeaderMarginButtom + 'px' }">
      <slot name="list-header">
        <slot name="list-keyword-query">
          <view class="list-header-box" v-if="enableKeywordQuery">
            <uni-search-bar
              v-if="enableKeywordQuery"
              class="keyword-query-input"
              radius="5"
              :placeholder="$t('WidgetListView.select', '搜索')"
              :cancelText="$t('global.cancel', '取消')"
              clearButton="auto"
              cancelButton="auto"
              @confirm="keywordQuery"
              @cancel="cancelKeywordQuery"
            />
            <view
              class="list-filter"
              v-if="((showSortOrder && hasCheckbox) || (!showSortOrder && !hasCheckbox)) && enableFieldQuery"
              @tap="openFieldQueryDialog"
            >
              <text style="padding-right: 4px">{{ $t("WidgetListView.filter", "筛选") }}</text>
              <w-icon icon="pticon iconfont icon-ptkj-shaixuan" :size="14" />
            </view>
            <view
              class="table-end-btn-control"
              :class="[checkedState ? 'active' : '']"
              v-if="!showSortOrder && hasCheckbox && !enableFieldQuery"
              @tap="enterEditState"
            >
              <text style="padding-right: 4px">{{ $t("WidgetListView.multiple", "多选") }}</text>
              <w-icon icon="iconfont icon-ptkj-duoxuan" :size="14" />
            </view>
          </view>
        </slot>
        <slot name="list-filter-sort">
          <view
            class="list-filter-sort"
            v-if="
              showSortOrder ||
              (!enableKeywordQuery &&
                enableFieldQuery &&
                !((showSortOrder && hasCheckbox) || (!showSortOrder && !hasCheckbox))) ||
              (!enableKeywordQuery && hasCheckbox) ||
              (enableFieldQuery && hasCheckbox)
            "
          >
            <view
              class="list-sort"
              :class="[selectedSortOrder && selectedSortOrder.text ? 'active' : '']"
              v-if="showSortOrder"
              @tap="chooseSortOrder"
            >
              <text style="padding-right: 4px">
                {{
                  selectedSortOrder && selectedSortOrder.text
                    ? selectedSortOrder.text
                    : $t("WidgetListView.defaultSort", "默认排序")
                }}
              </text>
              <w-icon icon="pticon iconfont icon-ptkj-qiehuanpaixu" :size="14" />
            </view>
            <view
              class="list-filter"
              v-if="
                enableFieldQuery &&
                (!((showSortOrder && hasCheckbox) || (!showSortOrder && !hasCheckbox)) || !enableKeywordQuery)
              "
              @tap="openFieldQueryDialog"
            >
              <text style="padding-right: 4px">{{ $t("WidgetListView.filter", "筛选") }}</text>
              <w-icon icon="pticon iconfont icon-ptkj-shaixuan" :size="14" />
            </view>
            <view
              class="table-end-btn-control"
              :class="[checkedState ? 'active' : '']"
              :style="{ width: !showSortOrder && !enableFieldQuery ? '100%' : '' }"
              v-if="hasCheckbox"
              @tap="enterEditState"
            >
              <text style="padding-right: 4px">{{ $t("WidgetListView.multiple", "多选") }}</text>
              <w-icon icon="iconfont icon-ptkj-duoxuan" :size="14" />
            </view>
          </view>
        </slot>
      </slot>
    </view>
    <scroll-view
      class="list-body"
      :style="scrollViewStyle"
      ref="scrollViewRef"
      scroll-y="true"
      refresher-enabled="true"
      :refresher-triggered="refresherTriggered"
      :refresher-threshold="44"
      :scroll-top="scrollTop"
      @scrolltoupper="onScrollToUpper"
      @scrolltolower="onScrollToLower"
      @refresherpulling="onRefresherPulling"
      @refresherrefresh="onRefresherRefresh"
      @refresherrestore="onRefresherRestore"
      @refresherabort="onRefresherAbort"
    >
      <!--卡片风格-->
      <template v-if="isCardStyle">
        <!-- 左滑操作 -->
        <!-- \uni-ui\lib\uni-swipe-action-item\render.js moveDirection(),state.deltaX === 0时，延时200ms触发关闭，用于解决左滑按钮点击失效问题-->
        <uni-swipe-action class="uni-list list-item-swipe-action" ref="swipeRef">
          <template v-for="(row, index) in data">
            <uni-swipe-action-item
              :threshold="0"
              :key="widget.id + '_' + index"
              :style="swiperItemStyle"
              :disabled="checkedState || !hasRowEndButtons"
            >
              <template v-slot:right>
                <swipe-button-group
                  :buttons="getRowEndButtons()"
                  :rowData="row"
                  :visibleJudgementData="defaultVisibleJudgementData"
                  @swipButtonClick="swipButtonClick"
                ></swipe-button-group>
              </template>
              <view class="item-card-container">
                <view v-if="configuration.selectable && checkedState" style="margin-right: 12px" class="extraIcon">
                  <table-checkbox
                    :checked="row.selected"
                    @checkboxSelected="itemClick($event, row)"
                    style="--uni-table-checkbox-width: 20px"
                  />
                  <!-- <w-icon
                    :color="row.extraIcon.color"
                    :size="row.extraIcon.size"
                    :icon="row.extraIcon.type"
                    @onTap="itemClick($event, row)"
                  /> -->
                </view>
                <uni-card
                  class="item-card"
                  :is-shadow="false"
                  :spacing="cardSpacing || '0 12px'"
                  margin="0"
                  :padding="cardPadding || '12px 0'"
                  :key="index"
                  :style="[backGroundStyle, cardStyle]"
                  @click="itemClick($event, row)"
                >
                  <slot name="list-item" :item="row">
                    <card-item-body
                      :configuration="configuration"
                      :rowData="row"
                      :rowIndex="index"
                      :clientRendererOptions="clientRendererOptions"
                      :styleConfiguration="styleConfiguration"
                      :buttons="_rowBottomButtons"
                      @button-click="swipButtonClick"
                    ></card-item-body>
                  </slot>
                </uni-card>
              </view>
            </uni-swipe-action-item>
          </template>
        </uni-swipe-action>
      </template>
      <template v-else>
        <uni-swipe-action class="uni-list list-item-swipe-action" ref="swipeRef">
          <template v-for="(row, index) in data">
            <uni-swipe-action-item
              :threshold="0"
              :key="widget.id + '_' + index"
              :style="{ marginRight: checkedState ? '-44px' : '' }"
              :disabled="checkedState || !hasRowEndButtons"
            >
              <template v-slot:right>
                <swipe-button-group
                  :buttons="getRowEndButtons()"
                  :rowData="row"
                  :widget="widget"
                  :visibleJudgementData="defaultVisibleJudgementData"
                  @swipButtonClick="swipButtonClick"
                ></swipe-button-group>
              </template>
              <view class="item-card-container">
                <view
                  v-if="configuration.selectable && checkedState"
                  style="margin-right: 12px; margin-left: 12px"
                  class="extraIcon"
                >
                  <table-checkbox
                    :checked="row.selected"
                    @checkboxSelected="itemClick($event, row)"
                    style="--uni-table-checkbox-width: 20px"
                  />
                </view>
                <uni-list-item
                  :class="row.isUnread ? 'unread' : ''"
                  :style="[backGroundStyle, listStyle]"
                  clickable
                  @click="itemClick($event, row)"
                >
                  <template slot="body">
                    <slot name="list-item" :item="row">
                      <list-item-body
                        :configuration="configuration"
                        :rowData="row"
                        :rowIndex="index"
                        :clientRendererOptions="clientRendererOptions"
                        :styleConfiguration="styleConfiguration"
                        :buttons="_rowBottomButtons"
                        @button-click="swipButtonClick"
                      ></list-item-body>
                    </slot>
                  </template>
                </uni-list-item>
              </view>
            </uni-swipe-action-item>
          </template>
        </uni-swipe-action>
      </template>
      <view class="loading-more" v-if="showLoadMore && data.length > 0">
        <slot name="loading-more" :loadMoreText="loadMoreText">
          <text class="loading-more-text">{{ loadMoreText }}</text>
        </slot>
      </view>
      <view class="no-match" v-else-if="isCustomQuery">
        <slot name="no-match" :noMatchText="noMatchText">
          <uni-w-empty :text="noMatchText" type="search"></uni-w-empty>
        </slot>
      </view>
      <view v-if="data.length == 0 && !isCustomQuery">
        <uni-w-empty></uni-w-empty>
      </view>
      <!-- 高度填充 -->
      <view class="fill-placeholder" :style="fillPlaceHolderStyle" v-if="!dyform"></view>
    </scroll-view>
    <view
      class="list-toolbar"
      :class="{ 'list-toolbar-custom-tab-bar': customTabBar, 'in-dyform': !!dyform }"
      v-if="hasTableEndButtons || checkedState"
    >
      <view class="toolbar-container">
        <view class="check-info" v-if="checkedState">
          <table-checkbox
            :checked="checkAll"
            :indeterminate="selectIndeterminate"
            @checkboxSelected="selectAllItem"
            style="--uni-table-checkbox-width: 20px"
            ref="checkAllRef"
          />
          <view style="margin-left: 8px; font-size: 16px; white-space: nowrap" @click="onSelectAllText">
            {{ $t("global.selectAll", "全选") }}({{ selection.length }})
          </view>
        </view>
        <view class="table-end-btn-group">
          <uni-w-button-group
            style="width: 100%"
            :buttons="tableEndButtons"
            :gutter="16"
            :max="tableEndButtons.length < 2 ? 0 : 2"
            @click="tableEndButtonClick"
          ></uni-w-button-group>
        </view>
      </view>
    </view>
    <!-- <view class="uni-mask" v-if="isLoading"></view> -->

    <!-- 选择排序弹出框 -->
    <uni-popup ref="sortPopup" :style="sortOrderPopupStyle" background-color="#ffffff" borderRadius="16px 16px 0 0">
      <view class="pt-popop-title">
        <view class="left"></view>
        <view class="center">
          <text>{{ $t("WidgetListView.sort", "排序") }}</text>
        </view>
        <view class="right">
          <uni-w-button type="text" @click="onSortPopupClose" icon="iconfont icon-ptkj-dacha-xiao"></uni-w-button>
        </view>
      </view>
      <view class="popup-sort-dialog">
        <view class="popup-content">
          <scroll-view class="popup-content-scroll-view" scroll-y="true">
            <view class="uni-list">
              <radio-group @change="onSortOrderRadioChange">
                <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in sortOrderConfigs" :key="index">
                  <view style="display: none">
                    <radio :value="item.id" />
                  </view>
                  <view class="popup-check-item">
                    <view style="flex: 1">{{ item.text }}</view>
                    <view class="popup-check-icon" v-if="item.checked">
                      <uni-icons type="checkmarkempty" color="var(--w-primary-color)" />
                    </view>
                  </view>
                </label>
              </radio-group>
            </view>
          </scroll-view>
        </view>
      </view>
    </uni-popup>
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
      <field-query-dialog
        class="w-list-view-popup"
        :queryFields="queryFields"
        :widget="widget"
        @cancel="$refs.fieldQueryPopup.close()"
        @ok="onFieldQueryOk"
      ></field-query-dialog>
    </uni-popup>
  </view>
  <w-widget-development v-else :widget="widget" :parent="parent"></w-widget-development>
</template>

<script>
import { mapState } from "vuex";
import { storage, appContext, utils } from "wellapp-uni-framework";
import moment from "moment";
import mixin from "../page-widget-mixin";
import { DataStore } from "wellapp-uni-framework";
import fieldQueryDialog from "./field-query-dialog.vue";
import cardItemBody from "./card-item-body.vue";
import listItemBody from "./list-item-body.vue";
import swipeButtonGroup from "./swipe-button-group.vue";
import { each, isEmpty, includes, camelCase, isNumber, template as stringTemplate, cloneDeep, get } from "lodash";
import TableCheckbox from "../../dyform/w-form-subform/component/table-checkbox.vue";

export default {
  mixins: [mixin],
  provide() {
    return {
      widgetListContext: this,
    };
  },
  components: { fieldQueryDialog, cardItemBody, listItemBody, swipeButtonGroup, TableCheckbox },
  data() {
    const _self = this;
    let configuration = _self.widget.configuration || {};
    let queryConfig = configuration.query || {};
    let loadMoreText = this.$t("global.loading", "加载更多");
    let noMoreText = this.$t("global.noMore", "没有更多数据了");
    let noMatchText = this.$t("global.noMatch", "无匹配数据");
    let queryFields = queryConfig.fields || [];
    return {
      configuration: configuration,
      enableKeywordQuery: configuration.hasSearch || queryConfig.keyword,
      enableFieldQuery: queryConfig.fieldSearch && queryConfig.fields.length > 0,
      queryFields,
      showClearIcon: false,
      inputQueryValue: "",
      scrollViewStyle: {
        height: "600px",
        // "background-color": "red"
      },
      fillPlaceHolderStyle: {
        width: "100%",
        height: "10px",
      },
      scrollTop: 0,
      scrollTopUpdated: false,
      windowHeight: null,
      refresherTriggered: false,
      isLoading: false,
      isRefreshing: false,
      showLoadMore: false,
      loadMoreText,
      loadMoreText1: loadMoreText,
      noMatchText,
      noMoreText,
      data: [],
      showSortOrder: false, // 是否显示排序
      sortOrderConfigs: [],
      sortOrderPopupStyle: {
        // top: "100px",
      },
      selectedSortOrder: null, // 用户选择的排序
      otherOrders: {},
      fieldQueryPopupStyle: {
        top: "100px",
      },
      userCustomCriterions: [],
      otherConditions: [],
      isCustomQuery: false, // 是否用户进行的查询
      checkedState: false, // 是否处于多选状态
      selection: [], //选择的数据
      buttonRect: {},
      _tableEndButtons: [],
      _rowBottomButtons: [],
      tableEndMoreButtons: [],
      listHeaderMarginButtom: 4,
      dataSourceProvider: undefined,
      dataSourceParams: {},
      primaryColumnKey: configuration.primaryField,
      cardStyleObject: [
        { name: "titleTextColor", style: "--w-list-view-card-title-color" },
        { name: "subTitleColor", style: "--w-list-view-card-subtitle-color" },
        { name: "rightTextColor", style: "--w-list-view-card-extra-color" },
        { name: "leftContentTextColor", style: "--w-list-view-card-content-label-color" },
        { name: "rightContentTextColor", style: "--w-list-view-card-content-value-color" },
        { name: "justifyContent", style: "--w-list-view-card-content-justify" },
      ],
      listStyleObject: [
        { name: "titleTextColor", style: "--w-list-view-list-title-color" },
        { name: "subTitleColor", style: "--w-list-view-list-subtitle-color" },
        { name: "noteColor", style: "--w-list-view-list-note-color" },
        { name: "rightTextColor", style: "--w-list-view-list-extra-color" },
        { name: "leftContentTextColor", style: "--w-list-view-list-content-label-color" },
        { name: "rightContentTextColor", style: "--w-list-view-list-content-value-color" },
      ],
      serverDataRenders: [], // 后端接口渲染函数
      __TABLE__: { selectedRowCount: undefined },
    };
  },
  created() {
    var _self = this;
    uni.showLoading();
    // 创建数据仓库
    var configuration = _self.getConfiguration();
    _self.getDataSourceProvider();

    _self.initData();

    _self.initSortOrderConfigs();
    // 初始化筛选
    _self.queryFieldsInit();

    this._tableEndButtons = _self.getTableEndButtons();
    this._rowBottomButtons = _self.getRowBottomButtons();
    // _self.createActionSheetIfRequried();

    _self.pageContext.handleEvent("refresh_list_data", (res) => {
      _self.onRefresherRefresh(res);
    });

    // 监听刷新事件
    uni.$off("refresh");
    uni.$on("refresh", (res) => {
      _self.pageContext.emitEvent("refresh_list_data", res);
    });
  },
  mounted: function () {
    this.listViewShowChange();
    // this.initScrollViewHeight();
  },
  destroyed() {
    (this.max = 0), (this.data = []), (this.loadMoreText = ""), (this.showLoadMore = false);
  },
  computed: {
    ...mapState(["customTabBar"]),
    // 是否卡片风格
    isCardStyle: function () {
      let configuration = this.widget.configuration || {};
      return configuration.displayStyle == "2";
    },
    defaultVisibleJudgementData() {
      return {
        ...this._vShowByDateTime,
        ...this._vShowByUserData,
        ...this._vShowByWorkflowData,
        ...(this.dyform != undefined ? { __DYFORM__: { editable: this.dyform.displayState == "edit" } } : {}),
        ...(this.dyform != undefined ? this.dyform.formData || {} : this._showByData || {}),
        _URL_PARAM_: this.vUrlParams,
      };
    },
    vTableHeadButtonVisibleJudgementData() {
      let __TABLE__ = this.$data.__TABLE__;
      __TABLE__.selectedRowCount = this.selectedRows.length;
      return {
        __TABLE__: {
          ...__TABLE__,
        },
        ...this.defaultVisibleJudgementData,
      };
    },
    vTableMeta() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        selectedRows: this.selectedRows,
      };
    },
    // 是否多选按钮(表格组件多选，或者，数据列表有底部按钮)
    hasCheckbox() {
      return (
        (this.widget.wtype !== "WidgetTable" && !isEmpty(this.tableEndButtons)) ||
        (this.widget.wtype == "WidgetTable" && this.widget.configuration.rowSelectType == "checkbox")
      );
    },
    // 是否底部按钮
    hasTableEndButtons: function () {
      return !isEmpty(this.tableEndButtons);
    },
    // 是否有行未操作按钮
    hasRowEndButtons: function () {
      let buttons = this.getRowEndButtons();
      return !isEmpty(buttons);
    },
    tableEndButtons() {
      let btns = [];
      let buttons = utils.deepClone(this._tableEndButtons);
      for (let i = 0, len = buttons.length; i < len; i++) {
        let btn = buttons[i];
        let visible = btn.defaultVisible;
        // 根据页面变量决定是否展示
        if (btn.defaultVisibleVar && btn.defaultVisibleVar.enable) {
          let _compareData = { ...this.rowData, ...this.vTableHeadButtonVisibleJudgementData };
          if (btn.defaultVisibleVar.code) {
            let code = btn.defaultVisibleVar.code,
              value = btn.defaultVisibleVar.value,
              valueType = btn.defaultVisibleVar.valueType;
            operator = btn.defaultVisibleVar.operator;
            if (valueType == "variable") {
              try {
                value = get(_compareData, value);
              } catch (error) {
                console.error("无法解析变量值", value);
              }
            }
            visible = utils.expressionCompare(_compareData, code, operator, value) ? visible : !visible;
          } else if (btn.defaultVisibleVar.match != undefined && btn.defaultVisibleVar.conditions != undefined) {
            let multiMatch = (compareData) => {
              // 多组条件判断
              let match = btn.defaultVisibleVar.match == "all";
              for (let i = 0, len = btn.defaultVisibleVar.conditions.length; i < len; i++) {
                let { code, operator, value, valueType } = btn.defaultVisibleVar.conditions[i];
                if (valueType == "variable") {
                  try {
                    value = get(_compareData, value);
                  } catch (error) {
                    console.error("无法解析变量值", value);
                  }
                }
                let result = utils.expressionCompare(compareData, code, operator, value);
                if (btn.defaultVisibleVar.match == "all" && !result) {
                  match = false;
                  break;
                }
                if (btn.defaultVisibleVar.match == "any" && result) {
                  match = true;
                  break;
                }
              }
              return match;
            };

            visible = multiMatch(_compareData) ? visible : !visible;
          }
        }

        if (visible || visible == undefined) {
          btns.push(btn);
        }
      }
      return btns;
    },
    hasListHeader() {
      return this.enableKeywordQuery || this.showSortOrder || this.enableFieldQuery || this.hasCheckbox;
    },
    selectIndeterminate() {
      return this.selection.length > 0 && this.selection.length < this.data.length;
    },
    checkAll() {
      return this.selection.length > 0 && this.selection.length <= this.data.length;
    },
    styleConfiguration() {
      if (this.configuration.hasOwnProperty("styleConfiguration")) {
        return this.configuration.styleConfiguration;
      } else {
        return {
          backgroundStyle: this.configuration.backgroundStyle,
          mainStyle: this.configuration.mainStyle,
        };
      }
    },
    cardStyle() {
      let style = {};
      if (this.styleConfiguration.borderColor) {
        style["--w-list-view-card-border-color"] = this.styleConfiguration.borderColor;
      }
      if (this.styleConfiguration.borderRadius || this.styleConfiguration.borderRadius === 0) {
        style.borderRadius = this.styleValueFormat(this.styleConfiguration.borderRadius);
      }
      if (this.styleConfiguration.rightWidth) {
        style["--w-list-view-card-extra-width"] = this.styleValueFormat(this.styleConfiguration.rightWidth);
      }
      if (this.styleConfiguration.footerHr) {
        style["--w-list-view-card-footer-border-color"] =
          this.styleConfiguration.footerHrColor || "var(--w-border-color-lighter)";
        style["--w-list-view-card-footer-margin"] =
          this.styleValueFormat(this.styleConfiguration.footerMargin) || "var(--w-margin-2xs) 0 0";
        style["--w-list-view-card-footer-padding"] =
          this.styleValueFormat(this.styleConfiguration.footerPadding) || "var(--w-margin-2xs) 0 0";
      }
      return style;
    },
    listStyle() {
      let style = {};
      if (this.styleConfiguration.spacing || this.styleConfiguration.spacing === 0) {
        style.padding = this.styleValueFormat(this.styleConfiguration.spacing);
      }
      if (this.styleConfiguration.rightWidth) {
        style["--w-list-view-list-extra-width"] = this.styleValueFormat(this.styleConfiguration.rightWidth);
      }
      if (this.styleConfiguration.footerHr) {
        style["--w-list-view-list-footer-border-color"] =
          this.styleConfiguration.footerHrColor || "var(--w-border-color-lighter)";
        style["--w-list-view-list-footer-margin"] =
          this.styleValueFormat(this.styleConfiguration.footerMargin) || "var(--w-margin-2xs) 0 0";
        style["--w-list-view-list-footer-padding"] =
          this.styleValueFormat(this.styleConfiguration.footerPadding) || "var(--w-margin-2xs) 0 0";
      }
      return style;
    },
    backGroundStyle() {
      let style = {};
      if (this.styleConfiguration.backgroundStyle) {
        let {
          backgroundColor,
          backgroundImage,
          backgroundImageInput,
          bgImageUseInput,
          backgroundPosition,
          backgroundRepeat,
          backgroundSize,
        } = this.styleConfiguration.backgroundStyle;

        if (backgroundColor) {
          style.backgroundColor = backgroundColor;
        }
        let bgImgStyle = bgImageUseInput ? backgroundImageInput : backgroundImage;
        if (bgImgStyle) {
          let isUrl =
            bgImgStyle.startsWith("data:") ||
            bgImgStyle.startsWith("http") ||
            bgImgStyle.startsWith("/") ||
            bgImgStyle.startsWith("../") ||
            bgImgStyle.startsWith("./");
          style.backgroundImage = isUrl ? `url("${bgImgStyle}")` : bgImgStyle;
        }
        if (backgroundPosition) {
          style.backgroundPosition = backgroundPosition;
        }
        if (backgroundRepeat) {
          style.backgroundRepeat = backgroundRepeat;
        }
        if (backgroundSize) {
          style.backgroundSize = backgroundSize;
        }
      }
      return style;
    },
    swiperItemStyle() {
      let style = {};
      let margin = ["6px", "12px"];
      style.margin = "6px 12px";
      if (this.styleConfiguration.margin) {
        style.margin = this.styleValueFormat(this.styleConfiguration.margin);
        margin = style.margin.split(" ");
      }
      if (this.checkedState && this.styleConfiguration.selectable) {
        style.marginRight = "-32px";
      } else {
        if (margin.length == 1) {
          style.marginRight = margin[0];
        } else if (margin.length >= 2) {
          style.marginRight = margin[1];
        }
      }
      return style;
    },
    clientRendererOptions() {
      let options = {};
      each(this.configuration.templateProperties, function (property) {
        if (property.renderer && !isEmpty(property.renderer.rendererType) && property.renderer.isClient) {
          options[property.mapColumn] = property.renderer;
        }
      });
      return options;
    },
    cardPadding() {
      return this.styleValueFormat(this.styleConfiguration.padding);
    },
    cardSpacing() {
      return this.styleValueFormat(this.styleConfiguration.spacing);
    },
    mainStyle() {
      let style = {};
      if (this.styleConfiguration.mainStyle) {
        let str = this.styleConfiguration.mainStyle.split(";");
        str.forEach((item) => {
          var itemArr = item.split(":");
          if (itemArr.length == 2 && itemArr[0] && itemArr[1]) {
            itemArr[0] = itemArr[0].replace(/[\s\p{C}]/gu, "");
            if (itemArr[0].startsWith("--")) {
              style[itemArr[0]] = itemArr[1].split(";")[0];
            } else {
              style[camelCase(itemArr[0])] = itemArr[1].split(";")[0];
            }
          }
        });
      }
      if (this.isCardStyle) {
        this.cardStyleObject.forEach((item) => {
          if (this.styleConfiguration[item.name]) {
            style[item.style] = this.styleValueFormat(this.styleConfiguration[item.name]);
            if (item.name == "justifyContent" && this.styleConfiguration.justifyContent == "space-between") {
              style["--w-list-view-card-content-value-justify"] = "flex-end";
              style["--w-list-view-card-content-value-align"] = "right";
            }
          }
        });
        for (let key in this.styleConfiguration) {
          if (key.startsWith("card") || key.startsWith("item")) {
            let name = "--w-list-view-" + key.replace(/([a-z])([A-Z])/g, "$1-$2").toLowerCase();
            let value = this.styleValueFormat(this.styleConfiguration[key]);
            if (value) {
              style[name] = value;
            }
          }
        }
      } else {
        this.listStyleObject.forEach((item) => {
          if (this.styleConfiguration[item.name]) {
            style[item.style] = this.styleConfiguration[item.name];
          }
        });
        for (let key in this.styleConfiguration) {
          if (key.startsWith("list") || key.startsWith("item")) {
            let name = "--w-list-view-" + key.replace(/([a-z])([A-Z])/g, "$1-$2").toLowerCase();
            let value = this.styleValueFormat(this.styleConfiguration[key]);
            if (value) {
              style[name] = value;
            }
          }
        }
      }
      return style;
    },
    selectedRows() {
      return this.selection;
    },
    selectedRowKeys() {
      return this.selectedRows.map((item) => item[this.configuration.primaryField]);
    },
  },
  methods: {
    // 初始化筛选字段内容
    queryFieldsInit() {
      let _self = this;
      if (!this.enableFieldQuery) {
        return false;
      }
      let key = "wListViewFieldQueryItems_" + this.widget.id;
      let variableData = this.widgetDependentVariableDataSource();
      let retCriterionsPromise = [];
      each(this.queryFields, function (querField) {
        let queryType = querField.queryOptions.queryType;
        let options = querField.queryOptions.options;
        // 清空筛选框内选项值
        if (["select2", "select", "checkbox", "radio", "treeSelect", "groupSelect"].indexOf(queryType) != -1) {
          storage.removeStorageCache(key + "_" + querField.uuid);
        }
        if (
          queryType == "checkbox" ||
          (["select", "groupSelect", "treeSelect"].includes(queryType) &&
            (options.multiSelectEnable || options.multiSelect)) ||
          (queryType == "datePicker" && options.range)
        ) {
          querField.value = [];
          querField.initValue = [];
        }
        let criterions = new Promise((resolve, reject) => {
          querField.value = "";
          querField.initValue = "";
          if (
            querField.defaultValueFormula &&
            querField.defaultValueFormula.value != undefined &&
            querField.defaultValueFormula.value.trim() != ""
          ) {
            // 默认值公式计算
            utils.executeJSFormula(querField.defaultValueFormula.value, variableData).then((result) => {
              if (queryType == "datePicker") {
                let setDataValue = (d, index) => {
                  let value = undefined;
                  if (d instanceof Date) {
                    value = moment(d).format("YYYY-MM-DD HH:mm:ss");
                  } else if (typeof d == "string") {
                    // 时间字符串
                    let dateParsed = moment(
                      d,
                      [
                        "YYYY-MM-DD HH:mm:ss",
                        "YYYY年MM月DD日 HH时mm分ss秒",
                        "YYYY年MM月DD日 HH:mm:ss",
                        "YYYYMMDDHHmmSS",
                      ],
                      true
                    );
                    if (!dateParsed.isValid()) {
                      console.error("不支持的日期格式识别: ", d);
                    } else {
                      value = dateParsed.format("YYYY-MM-DD HH:mm:ss");
                    }
                  } else if (typeof d == "number") {
                    // 时间戳
                    value = moment(d).format("YYYY-MM-DD HH:mm:ss");
                  }
                  return value;
                };
                if (options.range) {
                  let rangeVal = [];
                  if (Array.isArray(result) && result.length == 2) {
                    for (let i = 0; i < result.length; i++) {
                      let res = setDataValue(result[i], i);
                      if (res) {
                        rangeVal.push(res);
                      }
                    }
                    if (rangeVal.length == 2) {
                      querField.value = rangeVal;
                      querField.initValue = querField.value;
                      resolve({
                        columnIndex: querField.name,
                        value: querField.value,
                        type: "between",
                      });
                    }
                  }
                } else if (result) {
                  querField.value = setDataValue(result);
                  querField.initValue = querField.value;
                  resolve({
                    columnIndex: querField.name,
                    value: querField.value,
                    type: querField.operator,
                  });
                } else {
                  resolve();
                }
              } else if (result || result === "0" || result === 0) {
                querField.value = result;
                querField.initValue = querField.value;
                resolve({
                  columnIndex: querField.name,
                  value: querField.value,
                  type: querField.operator,
                });
              } else {
                resolve();
              }
            });
          } else if (querField.defaultValue != undefined && querField.defaultValue.trim() != "") {
            // 兼容旧的默认值计算逻辑
            querField.value = querField.defaultValue;
            if (queryType == "datePicker") {
              if (options.range) {
                let parts = querField.defaultValue.split(";");
                querField.value = [parts[0], parts.length > 1 ? parts[1] : undefined];
              }
            } else if (
              queryType == "checkbox" ||
              (["select", "groupSelect", "treeSelect"].includes(queryType) &&
                (options.multiSelectEnable || options.multiSelect))
            ) {
              querField.value = querField.defaultValue.split(";");
            }
            if (querField.value || querField.value === "0" || querField.value === 0) {
              querField.initValue = querField.value;
              resolve({
                columnIndex: querField.name,
                value: querField.value,
                type: queryType == "datePicker" && options.range ? "between" : querField.operator,
              });
            } else {
              resolve();
            }
          } else {
            resolve();
          }
        });
        retCriterionsPromise.push(criterions);
      });
      if (retCriterionsPromise.length) {
        Promise.all(retCriterionsPromise).then((res) => {
          let userCustomCriterions = [];
          each(res, (item) => {
            if (item) {
              userCustomCriterions.push(item);
            }
          });
          this.$emit("afterAdvanceSearchInit", userCustomCriterions);
          this.invokeDevelopmentMethod("afterAdvanceSearchInit", userCustomCriterions);
        });
      } else {
        this.$emit("afterAdvanceSearchInit");
        this.invokeDevelopmentMethod("afterAdvanceSearchInit");
      }
    },
    clearQueryInput: function (event) {
      this.inputQueryValue = event.detail.value;
      if (event.detail.value.length > 0) {
        this.showClearIcon = true;
      } else {
        this.showClearIcon = false;
      }
    },
    clearQueryInputTextIcon: function () {
      this.inputQueryValue = "";
      this.showClearIcon = false;
    },
    onScrollToUpper: function (e) {
      console.log("onScrollToUpper", e);
    },
    onScrollToLower: function (e) {
      console.log("onScrollToLower", e);
      // 更新滚动视图高度
      // this.updateScrollViewHeightIfRequired(e);
      this.loadMoreData();
    },
    onDragend(e) {
      console.log("onDragEnd", e);
    },
    onRefresherPulling(e) {
      console.log("onRefresherPulling", e);
      // 防止上滑页面也触发下拉
      if (e.detail.deltaY < 0) {
        return;
      }
    },
    onRefresherRefresh(e) {
      console.log("onRefresherRefresh", e);
      if (this.isRefreshing) {
        return;
      }
      this.isRefreshing = true;
      this.refresherTriggered = true;
      // 更新滚动视图高度
      // this.updateScrollViewHeightIfRequired(e);
      // 上拉刷新初始化数据
      // 模拟异步加载数据
      setTimeout(() => {
        this.data.splice(0, this.data.length);
        this.initData();
      }, 500);
    },
    onRefresherRestore() {
      console.log("onRefresherRestore");
      // 需要重置
      this.refresherTriggered = "restore";
    },
    onRefresherAbort() {
      console.log("onRefresherAbort");
    },
    refresh: function (options) {
      this.scrollTop = 0;
      this.onRefresherRefresh(options);
    },
    stopLoadingData: function (init) {
      console.log("stopLoadingData");
      var _self = this;
      _self.isLoading = false;
      _self.loadMoreText = "";
      _self.isRefreshing = false;
      _self.refresherTriggered = false;
      var dataList = _self.getDataSourceProvider().getData().data;
      // 没有查询结果
      if (init && dataList.length == 0) {
        _self.isCustomQuery =
          !isEmpty(_self.inputQueryValue) || (_self.userCustomCriterions && _self.userCustomCriterions.length > 0);
        if (_self.isCustomQuery) {
          _self.showLoadMore = false;
          _self.noMatchText = this.noMatchText;
        }
      } else if (dataList.length == 0) {
        _self.loadMoreText = this.noMoreText;
      } else {
        _self.isCustomQuery = false;
      }
    },
    initData() {
      var _self = this;
      _self.isLoading = true;
      _self.load(true);
    },
    loadMoreData: function (e) {
      console.log("loadMoreData", e);
      var _self = this;
      _self.isLoading = true;
      _self.showLoadMore = true;
      _self.loadMoreText = this.loadMoreText1;
      _self.getDataSourceProvider().nextPage(false);
    },
    keywordQuery: function (e) {
      console.log("keywordQuery");
      var _self = this;
      _self.inputQueryValue = e.value;
      _self.load(true);
    },
    cancelKeywordQuery: function (e) {
      console.log("cancelKeywordQuery", e);
      var _self = this;
      _self.inputQueryValue = "";
      _self.load(true);
    },
    itemClick: function (e, item) {
      var _self = this;
      var configuration = _self.getConfiguration();
      if (!_self.checkedState) {
        let url = "";
        if (configuration.detailPageUrl) {
          url = configuration.detailPageUrl;
          console.log(JSON.stringify(item));
          _self.setPageParameter("item", item);
        } else if (configuration.rowClickEvent && configuration.rowClickEvent.enable) {
          let eventHandler = configuration.rowClickEvent.option || {};
          if (eventHandler.actionType == "redirectPage" && eventHandler.pageType == "url") {
            // url = _self.getUrlData(eventHandler, eventParams);
            _self.setPageParameter("item", item);
          }

          appContext.dispatchEvent({
            ui: _self,
            meta: item,
            ...eventHandler,
          });
        }
        // 标记已阅
        _self.markRead(item);
        if (url) {
          uni.navigateTo({ url: url });
        }
      }
      // 选择中，取消选中
      if (configuration.selectable) {
        if (_self.hasSelected(item)) {
          _self.removeSelectItem(item);
        } else {
          if (!configuration.multiSelect) {
            _self.removeAllSelected();
          }
          _self.addSelectItem(item);
        }
        _self.updateItemExtraIcon(item);
        // 发出选择变更事件
        _self.$emit("selectionChange", _self.selection, _self.checkedState, configuration);
      }
      _self.$emit("itemClick", item, _self.checkedState, configuration);
    },
    // 添加选择的数据
    addSelectItem: function (item) {
      var _self = this;
      var selection = _self.selection || [];
      if (!_self.hasSelected(item)) {
        selection.push(item);
      }
      _self.selection = selection;
    },
    // 取消选择
    removeSelectItem: function (item) {
      var _self = this;
      var selection = _self.selection || [];
      for (var i = 0; i < selection.length; i++) {
        if (Object.prototype.hasOwnProperty.call(selection[i], "uuid") && selection[i].uuid == item.uuid) {
          selection.splice(i, 1);
        } else if (Object.prototype.hasOwnProperty.call(selection[i], "UUID") && selection[i].UUID == item.UUID) {
          selection.splice(i, 1);
        }
      }
    },
    onSelectAllText() {
      this.$refs.checkAllRef && this.$refs.checkAllRef.selected();
    },
    // 全选
    selectAllItem: function () {
      var _self = this;
      if (_self.selection.length > 0) {
        this.removeAllSelected();
      } else {
        _self.selection = [];
        var dataLis = _self.data;
        each(dataLis, function (item) {
          _self.addSelectItem(item);
          _self.updateItemExtraIcon(item);
        });
      }
    },
    // 移除所有选择
    removeAllSelected: function () {
      var _self = this;
      _self.selection = [];
      var dataLis = _self.data;
      each(dataLis, function (data) {
        _self.updateItemExtraIcon(data);
        var extraIcon = data.extraIcon;
        if (extraIcon) {
          extraIcon.type = "iconfont icon-ptkj-duoxuan-weixuan";
          extraIcon.color = "var(--w-border-color-light)";
        }
      });
    },
    hasSelected: function (item) {
      var _self = this;
      var selection = _self.selection || [];
      for (var i = 0; i < selection.length; i++) {
        if (Object.prototype.hasOwnProperty.call(selection[i], "uuid") && selection[i].uuid == item.uuid) {
          return true;
        } else if (Object.prototype.hasOwnProperty.call(selection[i], "UUID") && selection[i].UUID == item.UUID) {
          return true;
        }
      }
      return false;
    },
    /**
     * 通知数据变更
     */
    onDataChange: function (init) {
      var _self = this;
      console.log("onDataChange");
      var configuration = _self.getConfiguration();
      // var pageSize = configuration.pageSize || 20;
      var dataList = _self.getDataSourceProvider().getData().data;
      each(dataList, function (rowData) {
        _self.renderRowData(rowData, configuration);
      });
      // 初始化数据
      if (init) {
        _self.data = dataList;
      } else {
        // 加载更多
        if (dataList.length > 0) {
          _self.data = _self.data.concat(dataList);
        }
      }
      // 更新已阅未阅
      _self.updateUnreadMarker(dataList);

      // 更新占位符高度
      _self.updateFillPlaceHolderHeightIfRequired();
      _self.isLoading = false;
    },
    // 更新占位符高度
    updateFillPlaceHolderHeightIfRequired: function () {
      var _self = this;
      if (!!this.dyform) {
        return false;
      }
      setTimeout(function () {
        const query = uni.createSelectorQuery().in(_self);
        query
          .select(".uni-list")
          .boundingClientRect((data) => {
            // console.log("uni-list: " + JSON.stringify(data));
            // 编辑状态底部操作栏高度
            var tableEndToolbarHeight = _self.hasTableEndButtons || _self.checkedState ? 50 : 0;
            var diff = _self.scrollViewHeight - data.height;
            // console.log("diff: " + diff);
            if (diff >= 0) {
              _self.fillPlaceHolderStyle.height = diff + tableEndToolbarHeight + 10 + "px";
            } else {
              _self.fillPlaceHolderStyle.height = tableEndToolbarHeight + 0 + "px";
            }
          })
          .exec();
      }, 500);
    },
    initScrollViewHeight: function () {
      const _self = this;
      // 更新窗口高度
      uni.getSystemInfo({
        success: (result) => {
          // console.log("getSystemInfo", JSON.stringify(result));
          var customTabBarHeight = _self.customTabBar ? 50 : 0;
          var windowHeight = result.windowHeight - customTabBarHeight;
          if (this.dyform && this.styleConfiguration.maxHeightInDyform) {
            // 有配置表单内最大高度，使用该高度
            windowHeight = this.styleConfiguration.maxHeightInDyform;
          }
          if (this.hasListHeader) {
            const query = uni.createSelectorQuery().in(this);
            query
              .select(".list-header")
              .boundingClientRect((data) => {
                // console.log("data: " + JSON.stringify(data));
                let height = windowHeight - data.bottom - this.listHeaderMarginButtom;
                if (this.dyform) {
                  height += data.top;
                  delete this.scrollViewStyle.height;
                  this.$set(this.scrollViewStyle, "maxHeight", height + "px");
                } else {
                  this.scrollViewHeight = windowHeight - data.bottom - this.listHeaderMarginButtom;
                  this.scrollViewStyle.height = this.scrollViewHeight + "px";
                }
                // console.log("this.scrollViewStyle.height: " + this.scrollViewHeight);
              })
              .exec();
          } else {
            if (this.dyform) {
              delete this.scrollViewStyle.height;
              this.$set(this.scrollViewStyle, "maxHeight", windowHeight + "px");
            } else {
              this.scrollViewHeight = windowHeight;
              this.scrollViewStyle.height = this.scrollViewHeight + "px";
            }
          }
        },
      });
    },
    updateScrollViewHeightIfRequired: function (e) {
      if (!this.scrollTopUpdated) {
        return;
      }
      this.scrollTopUpdated = true;
      if (e && e.currentTarget && e.currentTarget.offsetTop && this.windowHeight) {
        this.scrollViewStyle.height = this.windowHeight - e.currentTarget.offsetTop + "px";
      }
    },
    /**
     * 获取数据源对象
     */
    getDataSourceProvider: function () {
      if (this.dataSourceProvider == undefined) {
        let _this = this;

        _this.serverDataRenders = this.getRenderers();

        // 创建数据源
        let dsOptions = {
          type: _this.configuration.type,
          onDataChange: function (data, count, params) {
            _this.stopLoadingData(params);
            _this.onDataChange(params);
          },
          renderers: _this.serverDataRenders,
          defaultOrders: _this.getDefaultSort(),
          defaultCriterions: _this.getDefaultConditions(),
          receiver: _this,
          autoCount: false,
          pageSize: _this.configuration.pageSize || 20,
        };
        if (
          this.configuration.dataSourceId &&
          (this.configuration.rowDataFrom == "dataSource" || this.configuration.rowDataFrom == undefined)
        ) {
          dsOptions.dataStoreId = this.configuration.dataSourceId;
          this.dataSourceProvider = new DataStore(dsOptions);
        } else if (this.configuration.rowDataFrom == "dataModel" && this.configuration.dataModelUuid) {
          dsOptions.loadDataUrl = "/api/dm/loadData/" + this.configuration.dataModelUuid;
          dsOptions.loadDataCntUrl = "/api/dm/loadDataCount/" + this.configuration.dataModelUuid;
          // dsOptions.dataStoreId = this.configuration.dataModelUuid;
          this.dataSourceProvider = new DataStore(dsOptions);
        }
      }
      this.$emit("getDataSourceProvider", this.dataSourceProvider);
      this.invokeDevelopmentMethod("getDataSourceProvider", this.dataSourceProvider);
      return this.dataSourceProvider;
    },
    addDataSourceParams(params) {
      if (this.dataSourceProvider) {
        for (let k in params) {
          this.dataSourceProvider.addParam(k, params[k]);
          this.$set(this.dataSourceParams, k, params[k]);
        }
      }
    },
    clearDataSourceParams() {
      if (this.dataSourceProvider) {
        this.dataSourceProvider.clearParams();
        this.dataSourceParams = {};
      }
    },
    deleteDataSourceParams(...key) {
      if (this.dataSourceProvider) {
        for (let k of key) {
          this.dataSourceProvider.removeParam(k);
          this.$delete(this.dataSourceParams, k);
        }
      }
    },
    /**
     * 添加额外的查询条件
     */
    addOtherConditions: function (conditions) {
      const _this = this;
      let otherConditions = _this.otherConditions || [];
      let addConditions = conditions.filter((condition) => {
        let index = otherConditions.findIndex(
          (otherCondition) => JSON.stringify(condition) == JSON.stringify(otherCondition)
        );
        return index == -1;
      });
      _this.otherConditions = [...otherConditions, ...addConditions];
    },
    /**
     * 情况额外查询条件,condition为空是清楚全部，否则清楚等于condition的一条额外查询条件
     */
    clearOtherConditions: function (condition) {
      const _this = this;
      let otherConditions = _this.otherConditions || [];
      if (Array.isArray(condition)) {
        condition.forEach((cond) => {
          _this.otherConditions = otherConditions.filter(
            (otherCondition) => JSON.stringify(cond) != JSON.stringify(otherCondition)
          );
        });
      } else if (condition) {
        _this.otherConditions = otherConditions.filter(
          (otherCondition) => JSON.stringify(condition) != JSON.stringify(otherCondition)
        );
      } else {
        _this.otherConditions = [];
      }
    },
    /**
     * 通知数据源回调根据条件加载数据
     */
    load: function (data) {
      var _self = this;
      this.$emit("beforeLoadData", data);
      this.invokeDevelopmentMethod("beforeLoadData", data);
      var criterions = _self._collectCriterion();
      var keyword = _self._collectSearchWord();

      if ((data === true || this.data.length == 0) && !this.isRefreshing) {
        _self.isLoading = true;
        uni.showLoading();
      }

      var otherConditions = _self.otherConditions || [];
      if (otherConditions.length) {
        each(otherConditions, function (condition) {
          criterions.push(condition);
        });
      }
      // 用户设置的查询条件
      if (_self.userCustomCriterions) {
        each(_self.userCustomCriterions, function (condition) {
          criterions.push(condition);
        });
      }
      var orders = [];
      // 用户选择的排序
      if (_self.selectedSortOrder) {
        orders.push({
          sortName: _self.selectedSortOrder.sortName,
          sortOrder: _self.selectedSortOrder.sortOrder,
        });
      }
      var otherOrders = _self.otherOrders || {};
      each(otherOrders, function (sortName, sortOrder) {
        orders.push({
          sortName: sortName,
          sortOrder: sortOrder,
        });
      });

      _self
        .getDataSourceProvider()
        .load(
          {
            pagination: {
              currentPage: 1,
            },
            orders: orders,
            keyword: keyword,
            criterions: criterions,
          },
          data
        )
        .catch(() => {
          _self.isLoading = false;
        });
    },
    renderRowData: function (rowData, configuration) {
      var _self = this;
      // 可选择
      if (configuration.selectable) {
        _self.updateItemExtraIcon(rowData);
      } else if (_self.checkedState) {
        _self.addItemExtraIcon(rowData);
      }
      rowData.clientRendererOptions = {};
      rowData.iconPropertyObject = {};
      _self.$emit("beforeRenderRowData", rowData, configuration);
      this.invokeDevelopmentMethod("beforeRenderRowData", rowData, configuration);
      // 支持的templateProperties属性title、note、rightText
      var itemHtml = configuration.templateHtml;
      // console.log("template: " + itemHtml);
      each(configuration.templateProperties, function (property) {
        // var re = eval('/\\{' + property.name + '\\}/mg;');
        var value = rowData[property.mapColumn];
        if (property.renderer && !isEmpty(property.renderer.rendererType)) {
          if (rowData.hasOwnProperty(property.mapColumn + "RenderValue")) {
            value = rowData[property.mapColumn + "RenderValue"];
          }
          if (property.renderer.isClient) {
            rowData.clientRendererOptions[property.mapColumn] = property.renderer;
            if (property.name) {
              rowData.clientRendererOptions[property.name] = property.renderer;
            }
          }
        }
        if (property.name) {
          rowData["property_" + property.name] = value;
        }
        if (property.icon) {
          rowData.iconPropertyObject[property.mapColumn] = property.icon;
          if (property.name) {
            rowData.iconPropertyObject[property.name] = property.icon;
          }
        }
        // itemHtml = itemHtml.replace(re, value || '');
      });
      // Object.keys(rowData).forEach(key => {
      //     itemHtml = itemHtml.replace(new RegExp(`{${key}}`,'g'), rowData[key]);
      // })
      // console.log("itemHtml: " + itemHtml);
      return itemHtml;
    },
    // renderCustomRowContent: function (rowData) {
    //   let _self = this;
    //   let itemContent = _self.configuration.customTemplateHtml || "";
    //   Object.keys(rowData).forEach((key) => {
    //     itemContent = itemContent.replace(new RegExp(`\{${key}\}`, "g"), rowData[key]);
    //   });
    //   return itemContent;
    // },
    updateItemExtraIcon: function (item) {
      var _self = this;
      if (_self.configuration.selectable) {
        var checked = _self.hasSelected(item);
        _self.$set(item, "selected", checked);
        _self.$set(item, "extraIcon", item.extraIcon || { size: 20 });
        _self.$set(
          item.extraIcon,
          "type",
          checked ? "iconfont icon-ptkj-duoxuan-xuanzhong" : "iconfont icon-ptkj-duoxuan-weixuan"
        );
        _self.$set(item.extraIcon, "color", checked ? "var(--w-primary-color)" : "var(--w-border-color-light)");
      }
    },
    addItemExtraIcon: function (item) {
      if (!item.extraIcon) {
        item.extraIcon = {
          type: "iconfont icon-ptkj-duoxuan-weixuan",
          size: 20,
          color: "var(--w-border-color-light)",
        };
      }
    },
    /**
     * 根据当前页的数据刷新当前页的未读信息
     */
    updateUnreadMarker: function (data) {
      var _self = this;
      var configuration = _self.getConfiguration();
      if (configuration.readMarker && data.length && !isEmpty(configuration.readMarkerField)) {
        var uuids = data.map(function (row) {
          return row[configuration.readMarkerField];
        });
        uni.request({
          url: "/api/readMarker/unReadList",
          header: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
          },
          method: "POST",
          data: { tagDataKeys: uuids.join(",") },
          success: function (result) {
            var unreadUuids = result.data.data || [];
            if (unreadUuids.length > 0) {
              each(data, function (rowData) {
                var keyValue = rowData[configuration.readMarkerField || configuration.primaryField];
                if (includes(unreadUuids, keyValue)) {
                  _self.$set(rowData, "isUnread", true);
                }
              });
            }
          },
        });
      }
    },
    /**
     * 数据标记已读
     */
    markRead: function (item) {
      let _self = this;
      if (item.isUnread !== true) {
        return;
      }
      let configuration = _self.getConfiguration();
      let entityUuid = item[configuration.readMarkerField];
      if (isEmpty(entityUuid)) {
        return;
      }
      item.isUnread = false;
      uni.request({
        url: `/api/readMarker/markRead/${entityUuid}`,
        method: "GET",
      });
    },
    /**
     * 获取列渲染器
     */
    getRenderers: function () {
      var renderers = [];
      var configuration = this.getConfiguration();
      each(configuration.templateProperties, function (property) {
        if (property.renderer && !isEmpty(property.renderer.rendererType) && !property.renderer.isClient) {
          renderers.push({
            columnIndex: property.mapColumn,
            param: Object.assign(
              {
                rendererType: property.renderer.rendererType,
              },
              property.renderer.options
            ),
          });
        }
      });
      return renderers;
    },
    /**
     * 选择排序
     */
    chooseSortOrder: function () {
      const _self = this;
      _self.$refs.sortPopup.open("bottom");
    },
    onSortPopupClose() {
      const _self = this;
      _self.$refs.sortPopup.close();
    },
    /**
     * 排序切换
     */
    onSortOrderRadioChange: function (event) {
      var _self = this;
      var items = _self.sortOrderConfigs;
      _self.selectedSortOrder = null;
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        var value = item.id;
        if (value === event.detail.value) {
          if (item.checked) {
            // _self.$set(item, "checked", false);
          } else {
            _self.$set(item, "checked", true);
          }
          _self.selectedSortOrder = item;
        } else {
          // item.checked = false;
          _self.$set(item, "checked", false);
        }
      }

      // 关闭排序选择弹出框
      _self.$refs.sortPopup.close();

      // 重新加载数据
      _self.load(true);
    },
    /**
     * 初始化排序配置
     */
    initSortOrderConfigs: function () {
      let _self = this;
      let sortOrderConfigs = [];
      let index = 0;
      each(this.widget.configuration.templateProperties, function (property) {
        if (property.sortOrder == "asc" || property.sortOrder == "desc") {
          let name = _self.$t(property.uuid, property.title) || property.mapColumnName;
          sortOrderConfigs.push({
            sortName: property.mapColumn,
            sortOrder: "asc",
            text: `${_self.$t("WidgetListView.ascSort", { name }, "按" + name + "升序")}`,
            id: property.mapColumn + "_asc",
            checked: index === 0 && property.sortOrder == "asc",
          });
          sortOrderConfigs.push({
            sortName: property.mapColumn,
            sortOrder: "desc",
            text: `${_self.$t("WidgetListView.descSort", { name }, "按" + name + "降序")}`,
            id: property.mapColumn + "_desc",
            checked: index === 0 && property.sortOrder == "desc",
          });
          index++;
        }
      });
      _self.sortOrderConfigs = sortOrderConfigs;
      _self.showSortOrder = this.widget.configuration.showSortOrder && sortOrderConfigs.length > 0;
    },
    /**
     * 获取默认排序信息
     */
    getDefaultSort: function () {
      var defaultSort = [];
      var configuration = this.getConfiguration();
      each(configuration.templateProperties, function (property) {
        if (property.sortOrder == "asc" || property.sortOrder == "desc") {
          defaultSort.push({
            sortName: property.mapColumn,
            sortOrder: property.sortOrder,
          });
        }
      });
      // 表格的排序配置
      if (this.widget.wtype == "WidgetTable" && configuration.defaultSort && configuration.defaultSort.length > 0) {
        for (let i = 0, len = this.widget.configuration.defaultSort.length; i < len; i++) {
          let sortItem = this.widget.configuration.defaultSort[i];
          let hasIndex = defaultSort.findIndex((item) => item.sortName == sortItem.dataIndex);
          if (hasIndex == -1) {
            defaultSort.push({
              sortName: sortItem.dataIndex,
              sortOrder: sortItem.sortType,
            });
          }
        }
      }
      return defaultSort;
    },
    /**
     * 打开字段查询弹出框
     */
    openFieldQueryDialog: function () {
      const _self = this;
      // const query = uni.createSelectorQuery().in(this);
      // query
      //   .select(".list-filter")
      //   .boundingClientRect((data) => {
      //     console.log(data.bottom);
      //     _self.fieldQueryPopupStyle.top = data.bottom + "px";
      //     _self.$refs.fieldQueryPopup.open("top");
      //   })
      //   .exec();
      _self.$refs.fieldQueryPopup.open("bottom");
    },
    onFieldQueryOk: function (criterions) {
      const _self = this;
      _self.isLoading = true;
      _self.$refs.fieldQueryPopup.close();

      // 用户设置的查询条件
      _self.userCustomCriterions = criterions;

      // 重新加载数据
      _self.load(true);
    },
    /**
     * 获取默认条件查询
     */
    getDefaultConditions: function () {
      var defaultConditions = [];
      var configuration = this.getConfiguration();
      if (!isEmpty(configuration.defaultCondition)) {
        var criterion = {};
        let compiler = stringTemplate(configuration.defaultCondition);
        let sql = configuration.defaultCondition;
        try {
          // 在表单域内
          let data = cloneDeep(this.vPageState || {});
          if (this.dyform) {
            Object.assign(data, {
              FORM_DATA: this.dyform.formData,
            });
          }
          sql = compiler(data);
        } catch (error) {
          console.error("解析模板字符串错误: ", error);
          throw new Error("表格默认条件变量解析异常");
        }
        criterion.sql = sql;
        defaultConditions.push(criterion);
      }
      return defaultConditions;
    },
    /**
     * 收集查询条件
     */
    _collectCriterion: function () {
      // var options = this.options;
      var _self = this;
      var configuration = this.getConfiguration();
      var criterions = [];
      if (_self.enableKeywordQuery && _self.inputQueryValue) {
        var orcriterion = {
          conditions: [],
          type: "or",
        };
        // 表格组件，按表格组件关键字字段搜索，其他按字段定义的所有选中字段搜索
        if (this.widget.wtype == "WidgetTable") {
          if (
            configuration.search &&
            configuration.search.keywordSearchColumns &&
            configuration.search.keywordSearchColumns.length > 0
          ) {
            each(configuration.search.keywordSearchColumns, function (column) {
              orcriterion.conditions.push({
                columnIndex: column,
                value: _self.inputQueryValue,
                type: "like",
              });
            });
          }
        } else {
          each(configuration.templateProperties, function (property) {
            orcriterion.conditions.push({
              columnIndex: property.mapColumn,
              value: _self.inputQueryValue,
              type: "like",
            });
          });
        }
        criterions.push(orcriterion);
      }
      return criterions;
    },
    _collectSearchWord: function () {
      return this.inputQueryValue;
    },
    getTableEndButtons: function () {
      return this.getButtonByPosition("2");
    },
    getRowEndButtons: function () {
      return this.getButtonByPosition("3");
    },
    getRowBottomButtons: function () {
      return this.getButtonByPosition("5");
    },
    getButtonByPosition: function (btnPos) {
      let _self = this;
      let buttons = this.configuration.buttons || [];
      let btns = [];
      let moreBtn = {
        id: "more",
        text: this.$t("global.more", "更多"),
        children: [],
      };
      each(buttons, function (button) {
        let position = button.position || [];
        if (position.indexOf(btnPos) != -1 && (button.defaultVisible === undefined || button.defaultVisible)) {
          if (button.icon && button.icon.className) {
            if (button.icon.className.startsWith("/") && !button.icon.src) {
              if (button.icon.className.startsWith("/static/")) {
                button.icon.src = button.icon.className;
              } else {
                button.icon.src = storage.fillAccessResourceUrl(button.icon.className);
              }
            }
          }
          button.text = _self.$t(button.uuid, button.text);
          if (btnPos == "2") {
            btns.push(
              Object.assign(button, {
                type: button.type,
                icon: button.icon && button.icon.className,
                title: button.text,
                textHidden: button.hiddenText,
              })
            );
          } else {
            if (!button.title) {
              button.title = button.text;
            }
            btns.push(Object.assign({}, button));
          }
        }
      });
      if (this.widget.wtype !== "WidgetTable") {
        if (btnPos == "3" && btns.length > 2) {
          let noGroupButtons = btns.splice(0, 1);
          moreBtn.children = btns;
          noGroupButtons.push(moreBtn);
          btns = noGroupButtons;
        }
      }
      return btns;
    },
    swipButtonClick: function (button, item) {
      this.buttonClick(button, item);
    },
    tableEndButtonClick: function (event, button) {
      const _self = this;
      // 更多
      if (button.id == "more") {
        _self.tableEndActionSheetTap();
      } else {
        _self.buttonClick(button, _self.selection);
      }
    },
    buttonClick: function (button, selection) {
      const _self = this;
      // 执行操作
      if (
        this.widget.wtype == "WidgetTable" &&
        button.code &&
        (button.code.startsWith("export_") || button.code.startsWith("import_"))
      ) {
        uni.$ptToastShow({
          title: "暂不支持数据导入、导出功能",
        });
      } else if (this.widget.wtype == "WidgetTable" && button.eventHandler) {
        let eventHandler = button.eventHandler || {};
        let eventParams = button.eventHandler.eventParams || [];
        let dispatchEventHandler = () => {
          if (eventHandler.actionType == "redirectPage" && eventHandler.pageType == "url") {
            eventHandler.meta = selection;
            appContext.dispatchEvent({
              ui: _self,
              ...eventHandler,
            });
          } else {
            eventHandler.meta = selection;
            appContext.dispatchEvent({
              ui: _self,
              ...eventHandler,
            });
            _self.$emit("buttonClick", button, _self.selection);
          }
        };
        let dispatch = () => {
          if (
            eventHandler.actionType == "workflow" &&
            eventHandler.workflowIdSource == "selectedRowColumnValue" &&
            eventHandler.workflowIdColumn
          ) {
            // 解析选中行对应的流程ID
            if (eventHandler.meta && eventHandler.meta[eventHandler.workflowIdColumn]) {
              eventHandler.workflowId = eventHandler.meta[eventHandler.workflowIdColumn];
            } else if (this.selection && this.selection.length > 0) {
              eventHandler.workflowId = this.selection[0][eventHandler.workflowIdColumn];
            }
          }
          if (eventHandler.actionType == "workflow" && eventHandler.workflowId == undefined) {
            uni.$ptToastShow({
              icon: "error",
              title: _self.$t("WidgetListView.errorWorkflowIdExplain", "解析流程错误, 请确认数据的流程配置正确性"),
            });
            return;
          }
          // 确认框提示
          if (button.confirmConfig && button.confirmConfig.enable) {
            let _title = _self.$t("Widget." + button.id + "_popconfirmTitle", button.confirmConfig.title),
              _content =
                button.confirmConfig.popType == "confirm"
                  ? _self.$t("Widget." + button.id + "_popconfirmContent", button.confirmConfig.content)
                  : undefined;

            uni.showModal({
              title: _title,
              confirmText: button.confirmConfig.okText
                ? _self.$t("Widget." + button.id + "_popconfirmOkText", button.confirmConfig.okText)
                : this.$t("global.confirm", "确认"),
              cancelText: button.confirmConfig.cancelText
                ? _self.$t("Widget." + button.id + "_popconfirmCancelText", button.confirmConfig.cancelText)
                : this.$t("global.cancel", "取消"),
              content: _content,
              success: function (res) {
                if (res.confirm) {
                  dispatchEventHandler();
                } else if (res.cancel) {
                }
              },
            });
          } else {
            dispatchEventHandler();
          }
        };
        dispatch();
      } else if (button.eventHandler) {
        appContext.startApp({
          ui: _self,
          appId: button.eventHandler.id,
          appType: button.eventHandler.type,
          appPath: button.eventHandler.path,
          params: button.eventParams && button.eventParams.params,
          pageUrl: button.pageUrl,
          appFunction: button.appFunction,
          data: selection,
          rowData: selection, // 兼容pc端数据格式
        });
      } else if (button.eventManger) {
        let eventHandler = button.eventManger || {};
        let eventParams = button.eventManger.eventParams || [];
        if (eventHandler.actionType == "redirectPage" && eventHandler.pageType == "url") {
          eventHandler.data = selection;
          appContext.dispatchEvent({
            ui: _self,
            ...eventHandler,
          });
        } else {
          eventHandler.data = selection;
          appContext.dispatchEvent({
            ui: _self,
            ...eventHandler,
          });
        }
      } else {
        _self.$emit("buttonClick", button, _self.selection);
      }
    },
    createActionSheetIfRequried: function () {
      const _self = this;
      let showBtnCount = 2;
      let actions = _self.getTableEndButtons();
      // iconfont替换为uniui-iconfont
      // each(actions, function (action) {
      //   action.cssClass =
      //     action.cssClass + " " + ((action.icon && action.icon.className) || "").replace(/\b(?=iconfont)\b/, "uniui-");
      // });
      // if (actions.length <= showBtnCount) {
      //   _self.tableEndButtons = actions;
      // } else {
      //   let newActions = [];
      //   let newMoreActions = [];
      //   for (var i = 0; i < actions.length; i++) {
      //     let action = actions[i];
      //     if (i < showBtnCount - 1) {
      //       newActions.push(action);
      //     } else if (i == showBtnCount - 1) {
      //       newActions.push({
      //         id: "more",
      //         text: "更多",
      //       });
      //       newMoreActions.push(action);
      //     } else {
      //       newMoreActions.push(action);
      //     }
      //   }
      _self.tableEndButtons = actions;
      // _self.tableEndMoreButtons = newMoreActions;
      // }
    },
    tableEndActionSheetTap() {
      const _self = this;
      let itemList = [];
      for (let i = 0; i < _self.tableEndMoreButtons.length; i++) {
        itemList.push(_self.tableEndMoreButtons[i].text);
      }
      uni.showActionSheet({
        itemList: itemList,
        popover: {
          // 104: navbar + topwindow 高度，暂时 fix createSelectorQuery 在 pc 上获取 top 不准确的 bug
          top: _self.buttonRect.top + 104 + _self.buttonRect.height,
          left: _self.buttonRect.left + _self.buttonRect.width / 2,
        },
        success: (e) => {
          console.log(e.tapIndex);
          _self.tableEndButtonClick(e, _self.tableEndMoreButtons[e.tapIndex]);
        },
      });
    },
    /**
     * 进入编辑状态
     */
    enterEditState: function () {
      const _self = this;
      if (_self.checkedState) {
        _self.leaveEditState();
      } else {
        _self.$refs.swipeRef && _self.$refs.swipeRef.closeAll();
        _self.checkedState = true;
        _self.$set(_self.configuration, "selectable", true);
        _self.configuration.multiSelect = true;
      }
    },
    /**
     * 离开编辑状态
     */
    leaveEditState: function () {
      const _self = this;
      _self.checkedState = false;
      _self.$set(_self.configuration, "selectable", false);
      _self.configuration.multiSelect = false;
    },
    deleteRowsByKeys(keys) {
      for (let i = 0; i < this.data.length; i++) {
        if (keys.includes(this.data[i][this.primaryColumnKey])) {
          this.data.splice(i--, 1);
        }
      }
    },
    refetch() {
      this.load(true);
    },
    getSelectedRowKeys() {
      return this.selection.map((item) => item[this.primaryColumnKey]);
    },
    getSelectedRows() {
      return this.selection;
    },
    clearRowStyle() {},
    setRowStyle() {},
    /**
     * 获取配置对象
     */
    getConfiguration: function () {
      return this.widget.configuration;
    },
    /**
     * 获取配置对象
     */
    getId: function () {
      return this.widget.id;
    },
    /**
     * 获取数据源ID
     */
    getDataStoreId: function () {
      return this.configuration.dataStoreId || this.configuration.dataSourceId;
    },
    getUrlData(eventHandler, eventParams) {
      let url = eventHandler.url;
      each(eventParams, (item, index) => {
        if (index == 0) {
          if (url.indexOf("?") == -1) {
            url += "?";
          }
          url += item.paramKey + "=" + item.paramValue;
        } else {
          url += "&" + item.paramKey + "=" + item.paramValue;
        }
      });
      return url;
    },
    // 格式化样式值
    styleValueFormat(value) {
      if (isNumber(value)) {
        return value + "px";
      } else if (Array.isArray(value)) {
        return value
          .map((item) => {
            return (item || 0) + "px";
          })
          .join(" ");
      }
      return value;
    },
    // 列表进入可视区域后，计算滚动区域高度, 不包括页面返回显示
    // todo 待模拟器再看
    listViewShowChange() {
      let _this = this;
      const view = uni.createSelectorQuery().in(this).select(".w-list-view");
      const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            // 元素进入视口时执行的操作
            // 检查view是否可见的逻辑
            view
              .boundingClientRect((data) => {
                if (data.width > 0 && data.height > 0) {
                  // view已经显示，执行你的操作
                  _this.initScrollViewHeight();
                  _this.$emit("wListViewOnShow", _this);
                  _this.invokeDevelopmentMethod("wListViewOnShow", _this);
                  console.log("View is visible");
                  // 你的操作代码
                }
              })
              .exec();
            observer.unobserve(entry.target); // 停止观察该元素，避免重复触发
          }
        });
      });
      observer.observe(view._component.$el);
    },
  },
  watch: {
    isLoading(v) {
      if (!v) {
        uni.hideLoading();
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.w-list-view {
  $w-list-view-header-bg-color: #ffffff;
  color: $uni-text-color;
  position: relative;

  //卡片样式
  --w-list-view-card-border-color: #ffffff;
  --w-list-view-card-title-size: var(--w-font-size-md);
  --w-list-view-card-title-color: var(--w-text-color-mobile);
  --w-list-view-card-title-weight: bold;
  --w-list-view-card-title-border-color: var(--w-border-color-mobile);
  --w-list-view-card-title-padding: 0 0 var(--w-padding-xs);
  --w-list-view-card-title-margin: 0 0 var(--w-margin-3xs);
  --w-list-view-card-content-label-width: 100px;
  --w-list-view-card-content-label-size: var(--w-font-size-base);
  --w-list-view-card-content-label-color: var(--w-text-color-base);
  --w-list-view-card-content-label-weight: normal;
  --w-list-view-card-content-value-size: var(--w-font-size-base);
  --w-list-view-card-content-value-color: var(--w-text-color-mobile);
  --w-list-view-card-content-value-weight: normal;
  --w-list-view-card-content-justify: normal;
  --w-list-view-card-content-padding: var(--w-padding-3xs) 0 0;
  --w-list-view-card-content-value-justify: normal;
  --w-list-view-card-content-value-align: left;

  --w-list-view-card-footer-margin: 0;
  --w-list-view-card-footer-padding: var(--w-padding-3xs) 0 0;
  --w-list-view-card-footer-border-color: transparent;

  --w-list-view-card-subtitle-size: var(--w-font-size-base);
  --w-list-view-card-subtitle-color: var(--w-text-color-base);
  --w-list-view-card-subtitle-weight: normal;
  --w-list-view-card-subtitle-padding: 0;
  --w-list-view-card-subtitle-margin: var(--w-margin-3xs) 0 0;

  --w-list-view-card-extra-size: var(--w-font-size-base);
  --w-list-view-card-extra-color: var(--w-text-color-light);
  --w-list-view-card-extra-weight: normal;
  --w-list-view-card-extra-padding: 0;
  --w-list-view-card-extra-margin: 0;
  --w-list-view-card-extra-width: 100px;
  // 列表样式
  --w-list-view-list-title-size: var(--w-font-size-md);
  --w-list-view-list-title-color: var(--w-text-color-mobile);
  --w-list-view-list-title-weight: bold;
  --w-list-view-list-title-padding: 0;
  --w-list-view-list-title-margin: 0;

  --w-list-view-list-subtitle-size: var(--w-font-size-base);
  --w-list-view-list-subtitle-color: var(--w-text-color-base);
  --w-list-view-list-subtitle-weight: normal;
  --w-list-view-list-subtitle-padding: 0;
  --w-list-view-list-subtitle-margin: var(--w-margin-3xs) 0 0;

  --w-list-view-list-note-size: var(--w-font-size-sm);
  --w-list-view-list-note-color: var(--w-text-color-light);
  --w-list-view-list-note-weight: normal;
  --w-list-view-list-note-padding: 0;
  --w-list-view-list-note-margin: var(--w-margin-3xs) 0 0;

  --w-list-view-list-extra-size: var(--w-font-size-base);
  --w-list-view-list-extra-color: var(--w-text-color-light);
  --w-list-view-list-extra-weight: normal;
  --w-list-view-list-extra-padding: 0;
  --w-list-view-list-extra-margin: 0;

  --w-list-view-list-extra-width: 100px;

  --w-list-view-list-content-label-size: var(--w-font-size-base);
  --w-list-view-list-content-label-color: var(--w-text-color-base);
  --w-list-view-list-content-label-weight: normal;
  --w-list-view-list-content-value-size: var(--w-font-size-base);
  --w-list-view-list-content-value-color: var(--w-text-color-mobile);
  --w-list-view-list-content-value-weight: normal;
  --w-list-view-list-content-justify: space-between;
  --w-list-view-list-content-value-justify: flex-end;
  --w-list-view-list-content-padding: var(--w-padding-3xs) 0 0;

  --w-list-view-list-footer-margin: 0;
  --w-list-view-list-footer-padding: var(--w-padding-3xs) 0 0;
  --w-list-view-list-footer-border-color: transparent;

  --w-list-view-list-item-border-color: var(--w-border-color-light);

  --dashbord-timingState-padding: 12px;

  --w-list-cell-data-org-element-render-title-margin-left: 4px;
  --w-list-cell-data-org-element-render-title-color: var(--w-text-color-mobile);
  --w-list-cell-data-org-element-render-title-size: var(--w-font-size-base);
  --w-list-cell-data-org-element-render-title-weight: normal;

  --w-list-view-item-footer-button-hr-color: var(--w-border-color-light);
  --w-list-view-item-footer-button-hr-height: 18px;
  --w-list-view-item-footer-button-padding: 0;

  uni-icon {
    vertical-align: middle;
  }

  .list-header {
    padding: 8px;
    background-color: $w-list-view-header-bg-color;
    border-bottom: 1px solid var(--w-gray-color-2);
    .list-header-box {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;

      .keyword-query-input {
        flex: 1;
      }
    }

    .keyword-query-input {
      padding: 0;
      ::v-deep .uni-searchbar__box-search-input {
        color: $uni-text-color;
      }
      ::v-deep .uni-searchbar__box {
        background-color: var(--bg-search-bar-color) !important;
      }
      ::v-deep .uni-searchbar__cancel {
        color: $uni-text-color;
      }

      & + .list-filter,
      & + .table-end-btn-control {
        margin-left: 8px;
      }
    }

    .list-header-box + .list-filter-sort {
      margin-top: 8px;
    }

    .table-end-btn-control {
      text-align: right;
    }

    .list-filter-sort {
      display: flex;
      flex-direction: row;
      align-items: center;
      > * {
        flex-grow: 1;
        text-align: center;
      }
      .icon {
        color: $uni-icon-color !important;
      }

      .list-filter-sort-active-text {
        border-radius: 12px;
        background-color: var(--w-primary-color-2);
        color: var(--w-primary-color);
        font-size: var(--w-font-size-sm);
        padding: 0 var(--w-padding-xs);
        line-height: 24px;
        height: 24px;
        display: block;
      }
    }

    .list-filter {
      & + .table-end-btn-control {
        margin-left: 8px;
      }
    }

    .list-sort {
      .active {
        color: $uni-text-color-active;
      }
      & + .list-filter {
        margin-left: 8px;
      }
      & + .table-end-btn-control {
        margin-left: 8px;
      }
    }

    .btn-class {
      border: 1px solid var(--w-primary-color-3);
      border-radius: 4px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--w-primary-color);
      width: 24px;
      height: 24px;
    }
    .active {
      color: var(--w-primary-color);
    }
  }

  .list-body {
    ::v-deep .uni-list {
      background-color: $uni-bg-color;

      .uni-card__content {
        color: $uni-text-color;
      }

      .uni-list-item {
        background-color: $uni-bg-secondary-color;
        flex-grow: 1;

        .uni-list--border:after {
          background-color: var(--w-list-view-list-item-border-color);
        }
      }

      .list-item-radio {
        margin-right: var(--w-margin-2xs);
      }

      &::after {
        content: none;
      }
    }
  }

  .item-card-container {
    display: flex;
    flex-wrap: nowrap;
    align-items: center;
  }

  .item-card {
    background-color: $uni-bg-secondary-color;
  }
  ::v-deep .uni-card--border {
    border: 1px solid var(--w-list-view-card-border-color);
  }
  ::v-deep .item-card-body {
    --card-title-size: var(--w-list-view-card-title-size);
    --card-title-color: var(--w-list-view-card-title-color);
    --card-title-weight: var(--w-list-view-card-title-weight);
    --card-title-border-color: var(--w-list-view-card-title-border-color);
    --card-title-padding: var(--w-list-view-card-title-padding);
    --card-title-margin: var(--w-list-view-card-title-margin);
    --card-content-label-width: var(--w-list-view-card-content-label-width);
    --card-content-label-size: var(--w-list-view-card-content-label-size);
    --card-content-label-color: var(--w-list-view-card-content-label-color);
    --card-content-label-weight: var(--w-list-view-card-content-label-weight);
    --card-content-value-size: var(--w-list-view-card-content-value-size);
    --card-content-value-color: var(--w-list-view-card-content-value-color);
    --card-content-value-weight: var(--w-list-view-card-content-value-weight);
    --card-content-justify: var(--w-list-view-card-content-justify);
    --card-content-padding: var(--w-list-view-card-content-padding);
    --card-content-value-justify: var(--w-list-view-card-content-value-justify);
    --card-content-value-align: var(--w-list-view-card-content-value-align);

    --card-footer-margin: var(--w-list-view-card-footer-margin);
    --card-footer-padding: var(--w-list-view-card-footer-padding);
    --card-footer-border-color: var(--w-list-view-card-footer-border-color);

    --card-subtitle-size: var(--w-list-view-card-subtitle-size);
    --card-subtitle-color: var(--w-list-view-card-subtitle-color);
    --card-subtitle-weight: var(--w-list-view-card-subtitle-weight);
    --card-subtitle-padding: var(--w-list-view-card-subtitle-padding);
    --card-subtitle-margin: var(--w-list-view-card-subtitle-margin);

    --card-extra-size: var(--w-list-view-card-extra-size);
    --card-extra-color: var(--w-list-view-card-extra-color);
    --card-extra-weight: var(--w-list-view-card-extra-weight);
    --card-extra-padding: var(--w-list-view-card-extra-padding);
    --card-extra-margin: var(--w-list-view-card-extra-margin);
    --card-extra-width: var(--w-list-view-card-extra-width);

    --item-footer-button-hr-color: var(--w-list-view-item-footer-button-hr-color);
    --item-footer-button-hr-height: var(--w-list-view-item-footer-button-hr-height);
    --item-footer-button-padding: var(--w-list-view-item-footer-button-padding);

    .cell-data-org-element-render {
      --cell-data-org-element-render-title-margin-left: var(--w-list-cell-data-org-element-render-title-margin-left);
      --cell-data-org-element-render-title-color: var(--w-list-cell-data-org-element-render-title-color);
      --cell-data-org-element-render-title-size: var(--w-list-cell-data-org-element-render-title-size);
      --cell-data-org-element-render-title-weight: var(--w-list-cell-data-org-element-render-title-weight);
    }
  }
  ::v-deep .list-item-body {
    --list-title-size: var(--w-list-view-list-title-size);
    --list-title-color: var(--w-list-view-list-title-color);
    --list-title-weight: var(--w-list-view-list-title-weight);
    --list-title-padding: var(--w-list-view-list-title-padding);
    --list-title-margin: var(--w-list-view-list-title-margin);

    --list-subtitle-size: var(--w-list-view-list-subtitle-size);
    --list-subtitle-color: var(--w-list-view-list-subtitle-color);
    --list-subtitle-weight: var(--w-list-view-list-subtitle-weight);
    --list-subtitle-padding: var(--w-list-view-list-subtitle-padding);
    --list-subtitle-margin: var(--w-list-view-list-subtitle-margin);

    --list-note-size: var(--w-list-view-list-note-size);
    --list-note-color: var(--w-list-view-list-note-color);
    --list-note-weight: var(--w-list-view-list-note-weight);
    --list-note-padding: var(--w-list-view-list-note-padding);
    --list-note-margin: var(--w-list-view-list-note-margin);

    --list-extra-size: var(--w-list-view-list-extra-size);
    --list-extra-color: var(--w-list-view-list-extra-color);
    --list-extra-weight: var(--w-list-view-list-extra-weight);
    --list-extra-padding: var(--w-list-view-list-extra-padding);
    --list-extra-margin: var(--w-list-view-list-extra-margin);

    --list-extra-width: var(--w-list-view-list-extra-width);

    --list-content-label-size: var(--w-list-view-list-content-label-size);
    --list-content-label-color: var(--w-list-view-list-content-label-color);
    --list-content-label-weight: var(--w-list-view-list-content-label-weight);
    --list-content-value-size: var(--w-list-view-list-content-value-size);
    --list-content-value-color: var(--w-list-view-list-content-value-color);
    --list-content-value-weight: var(--w-list-view-list-content-value-weight);
    --list-content-justify: var(--w-list-view-list-content-justify);
    --list-content-padding: var(--w-list-view-list-content-padding);
    --list-content-value-justify: var(--w-list-view-list-content-value-justify);

    --list-footer-margin: var(--w-list-view-list-footer-margin);
    --list-footer-padding: var(--w-list-view-list-footer-padding);
    --list-footer-border-color: var(--w-list-view-list-footer-border-color);

    --item-footer-button-hr-color: var(--w-list-view-item-footer-button-hr-color);
    --item-footer-button-hr-height: var(--w-list-view-item-footer-button-hr-height);
    --item-footer-button-padding: var(--w-list-view-item-footer-button-padding);

    .cell-data-org-element-render {
      --cell-data-org-element-render-title-margin-left: var(--w-list-cell-data-org-element-render-title-margin-left);
      --cell-data-org-element-render-title-color: var(--w-list-cell-data-org-element-render-title-color);
      --cell-data-org-element-render-title-size: var(--w-list-cell-data-org-element-render-title-size);
      --cell-data-org-element-render-title-weight: var(--w-list-cell-data-org-element-render-title-weight);
    }
  }

  .list-item-swipe-action {
    width: 100%;
  }

  .swipe-button {
    /* #ifndef APP-NVUE */
    display: flex;
    height: 100%;
    /* #endif */
    flex: 1;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    padding: 0 20px;
    background-color: #ff5a5f;
  }

  .swipe-buttion-icon {
    margin-right: 2px;
  }

  .swipe-button-text {
    color: $uni-text-color;
    font-size: 16px;
  }

  .list-toolbar {
    position: absolute;
    width: 100%;
    bottom: var(--window-bottom, 0);
    background-color: #ffffff;
    z-index: 10;
    box-shadow: 0 -1px 4px $uni-bg-color;
    padding: 0 12px;
    width: calc(100% - 24px);

    &.in-dyform {
      position: relative;
    }

    .toolbar-container {
      display: flex;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      height: 50px;
      line-height: 50px;
    }

    .check-info {
      width: 140px;

      display: flex;
      flex-direction: row;
      justify-content: flex-start;
      align-items: center;
    }

    .check-info-icon {
      margin-left: 14px;
    }
    .check-info-text {
      padding-top: 4px;
      margin-left: 2px;
    }

    .table-end-btn-group {
      margin-right: 3px;
      flex: 1;
      display: flex;
      flex-direction: row;
      justify-content: flex-start;
      align-items: center;
    }

    .uniui-iconfont::before {
      margin-right: 2px;
    }
    .table-end-btn {
      flex: 1;
      margin-right: 10px;
    }
    .btn-inverse {
      background-color: $uni-btn-color-inverse; // #000000; // 黑色
      color: $uni-text-color-inverse;
    }
    .btn-default {
      background-color: $uni-btn-color-default; // #d4d4d4; // 灰色
      color: $uni-text-color;
    }
    .btn-primary {
      background-color: $uni-btn-color-primary; // #007aff; // 蓝色
      color: $uni-text-color-inverse;
    }
    .btn-success {
      background-color: $uni-btn-color-success; // #3aa322; // 绿色
      color: $uni-text-color-inverse;
    }
    .btn-info {
      background-color: $uni-btn-color-info; // #2aaedd; // 浅蓝
      color: $uni-text-color-inverse;
    }
    .btn-warning {
      background-color: $uni-btn-color-warning; // #e99f00; // 橙色
      color: $uni-text-color-inverse;
    }
    .btn-danger {
      background-color: $uni-btn-color-danger; // #e33033; // 红色
      color: $uni-text-color-inverse;
    }
    .image-icon {
      vertical-align: middle;
    }
  }
  .list-toolbar-custom-tab-bar {
    padding-bottom: 50px;
  }
}

.loading-more,
.no-match {
  align-items: center;
  justify-content: center;
  padding-top: 10px;
  padding-bottom: 10px;
  text-align: center;
}

.loading-more-text,
.no-match-text {
  font-size: 28rpx;
  color: #999;
}

.popup-sort-dialog {
  background-color: $uni-bg-color;
  // height: 300px;

  .uni-list {
    background-color: $uni-bg-secondary-color;
  }

  .popup-title {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    align-items: center;
    justify-content: center;
    height: 40px;
    border-bottom: 1px solid $uni-border-3;
  }

  .popup-title-text {
    font-size: 16px;
    color: $uni-main-color;
    font-weight: bold;
  }

  .popup-content {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    justify-content: center;
    // padding-top: 10px;

    .popup-content-scroll-view {
      max-height: 200px;
    }

    .hide-confirm-btn {
      height: 250px;
    }

    .popup-check-item {
      display: flex;
      flex-direction: row;
      width: 100%;
      height: 26px;
      justify-content: center;
      align-items: center;

      .popup-check-icon {
        width: 20px;
        padding-right: 6px;
      }
    }
  }
}
::v-deep .w-list-view-popup {
  border-radius: 8px 8px 0 0;
}

::v-deep * {
  .dashbord-timingState {
    --dashbord-timingState-background-color: var(--w-primary-color-1);
    --dashbord-timingState-text-color: var(--w-primary-color);
    display: flex;
    align-items: center;
    padding: var(--dashbord-timingState-padding);
    // background: linear-gradient(to bottom, var(--dashbord-timingState-background-color), #ffffff);
    .tag {
      background-color: var(--dashbord-timingState-background-color);
      color: var(--dashbord-timingState-text-color);
      border-radius: 4px;
      height: 24px;
      line-height: 24px;
      padding: 0 4px;
      font-size: var(--w-font-size-base);
      flex-shrink: 0;
      margin-left: var(--w-margin-xs);
    }
    &.solid {
      --dashbord-timingState-background-color: var(--w-primary-color);
      --dashbord-timingState-text-color: #ffffff;
    }
  }
  .dashbord-circle-overdue {
    --dashbord-timingState-background-color: var(--w-danger-color-1);
    --dashbord-timingState-text-color: var(--w-danger-color);
    &.solid {
      --dashbord-timingState-background-color: var(--w-danger-color);
      --dashbord-timingState-text-color: #ffffff;
    }
  }

  .dashbord-circle-due {
    --dashbord-timingState-background-color: var(--w-warning-color-1);
    --dashbord-timingState-text-color: var(--w-warning-color);
    &.solid {
      --dashbord-timingState-background-color: var(--w-warning-color);
      --dashbord-timingState-text-color: #ffffff;
    }
  }

  .dashbord-circle-alarm {
    --dashbord-timingState-background-color: var(--w-warning-color-3);
    --dashbord-timingState-text-color: var(--w-warning-color);
    &.solid {
      --dashbord-timingState-background-color: var(--w-warning-color-7);
      --dashbord-timingState-text-color: #ffffff;
    }
  }

  .dashbord-circle-normal {
  }
}
</style>
