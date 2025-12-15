<template>
  <view class="w-list-view" :class="customClass" v-if="isEmptyUniJsModule">
    <view class="list-header" v-if="hasListHeader" :style="{ marginBottom: listHeaderMarginButtom + 'px' }">
      <slot name="list-header">
        <slot name="list-keyword-query">
          <view class="list-header-box" v-if="enableKeywordQuery">
            <uni-search-bar
              v-if="enableKeywordQuery"
              class="keyword-query-input"
              radius="5"
              placeholder="搜索"
              clearButton="auto"
              cancelButton="auto"
              @confirm="keywordQuery"
              @cancel="cancelKeywordQuery"
            />
            <view class="list-filter" v-if="!showSortOrder && enableFieldQuery" @tap="openFieldQueryDialog">
              <icon type="pticon iconfont icon-ptkj-shaixuan" class="uni-mr-4" :size="16" />
              <text>筛选</text>
            </view>
            <view class="table-end-btn-control" v-if="hasTableEndButtons">
              <icon
                type="pticon iconfont icon-ptkj-gengduocaozuo"
                :size="16"
                v-if="!editState"
                @click="enterEditState"
              />
              <text v-if="editState" @click="leaveEditState">完成</text>
            </view>
          </view>
        </slot>
        <slot name="list-filter-sort">
          <view
            class="list-filter-sort"
            v-if="
              showSortOrder || (!enableKeywordQuery && enableFieldQuery) || (!enableKeywordQuery && hasTableEndButtons)
            "
          >
            <view class="list-sort" v-if="showSortOrder" @tap="chooseSortOrder">
              <icon type="pticon iconfont icon-ptkj-qiehuanpaixu" class="uni-mr-4" :size="16" />
              <text>排序</text>
              <text class="uni-ml-2 active" v-if="selectedSortOrder">{{ selectedSortOrder.text }}</text>
            </view>
            <view
              class="list-filter"
              v-if="(!enableKeywordQuery || showSortOrder) && enableFieldQuery"
              @tap="openFieldQueryDialog"
            >
              <icon type="pticon iconfont icon-ptkj-shaixuan" class="uni-mr-4" :size="16" />
              <text>筛选</text>
            </view>
            <view
              class="table-end-btn-control"
              :style="{ width: !showSortOrder && !enableFieldQuery ? '100%' : '' }"
              v-if="!enableKeywordQuery && hasTableEndButtons"
            >
              <icon
                type="pticon iconfont icon-ptkj-gengduocaozuo"
                :size="16"
                v-if="!editState"
                @click="enterEditState"
              />
              <text v-if="!editState && !showSortOrder && !enableFieldQuery" @click="enterEditState"> 更多操作 </text>
              <text v-if="editState" @click="leaveEditState">完成</text>
            </view>
          </view>
        </slot>
      </slot>
    </view>
    <scroll-view
      class="list-body"
      :style="scrollViewStyle"
      scroll-y="true"
      refresher-enabled="true"
      :refresher-triggered="refresherTriggered"
      :refresher-threshold="44"
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
        <uni-swipe-action class="uni-list list-item-swipe-action" v-if="hasRowEndButtons">
          <template v-for="(row, index) in data">
            <uni-swipe-action-item :threshold="0" :key="index">
              <template v-slot:right>
                <swipe-button-group
                  :buttons="getRowEndButtons()"
                  :rowData="row"
                  @swipButtonClick="swipButtonClick"
                ></swipe-button-group>
              </template>
              <uni-card
                class="item-card"
                :is-shadow="false"
                padding="10px 4px 14px"
                :key="index"
                @click="itemClick($event, row)"
              >
                <slot name="list-item" :item="row">
                  <card-item-body :configuration="configuration" :rowData="row"></card-item-body>
                </slot>
              </uni-card>
            </uni-swipe-action-item>
          </template>
        </uni-swipe-action>
        <view v-else class="uni-list">
          <template v-for="(row, index) in data">
            <uni-card
              class="item-card"
              :is-shadow="false"
              padding="10px 4px 14px"
              :key="index"
              @click="itemClick($event, row)"
            >
              <slot name="list-item" :item="row">
                <card-item-body :configuration="configuration" :rowData="row"></card-item-body>
              </slot>
            </uni-card>
          </template>
        </view>
      </template>
      <template v-else>
        <uni-swipe-action class="uni-list list-item-swipe-action" v-if="hasRowEndButtons">
          <template v-for="(row, index) in data">
            <uni-swipe-action-item :threshold="0" :key="index">
              <template v-slot:right>
                <swipe-button-group
                  :buttons="getRowEndButtons()"
                  :rowData="row"
                  @swipButtonClick="swipButtonClick"
                ></swipe-button-group>
              </template>
              <uni-list-item
                :show-extra-icon="configuration.selectable"
                :extra-icon="row.extraIcon"
                :rightText="row.rightText"
                :class="row.isUnread ? 'unread' : ''"
                clickable
                @click="itemClick($event, row)"
              >
                <template slot="body">
                  <slot name="list-item" :item="row">
                    <list-item-body :configuration="configuration" :rowData="row"></list-item-body>
                  </slot>
                </template>
              </uni-list-item>
            </uni-swipe-action-item>
          </template>
        </uni-swipe-action>
        <uni-list v-else class="uni-list">
          <uni-list-item
            v-for="(row, index) in data"
            :key="index"
            :show-extra-icon="configuration.selectable"
            :extra-icon="row.extraIcon"
            :rightText="row.rightText"
            :class="row.isUnread ? 'unread' : ''"
            clickable
            @click="itemClick($event, row)"
          >
            <template slot="body">
              <slot name="list-item" :item="row">
                <list-item-body :configuration="configuration" :rowData="row"></list-item-body>
              </slot>
            </template>
          </uni-list-item>
        </uni-list>
      </template>
      <view class="loading-more" v-if="showLoadMore">
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
      <view class="fill-placeholder" :style="fillPlaceHolderStyle"></view>
    </scroll-view>
    <view
      class="list-toolbar"
      :class="{ 'list-toolbar-custom-tab-bar': customTabBar }"
      v-if="hasTableEndButtons && editState"
    >
      <view class="toolbar-container">
        <view class="check-info">
          <uni-icons
            class="check-info-icon"
            v-show="selection.length == 0"
            type="circle"
            color="#c0c0c0"
            size="24"
            @click="selectAllItem"
          ></uni-icons>
          <uni-icons
            class="check-info-icon"
            v-show="selection.length > 0"
            type="circle-filled"
            color="#007aff"
            size="24"
            @click="removeAllSelected"
          ></uni-icons>
          <text v-if="selection.length == data.length" class="check-info-text"> 全选({{ selection.length }}) </text>
          <text v-else-if="selection.length > 0" class="check-info-text"> 已选({{ selection.length }}) </text>
          <text v-else class="check-info-text">全选</text>
        </view>
        <view class="table-end-btn-group">
          <template v-for="(button, index) in tableEndButtons">
            <button
              class="table-end-btn"
              :type="button.type || 'defalut'"
              :plain="button.plain"
              :size="button.mini ? 'mini' : ''"
              @click="tableEndButtonClick($event, button)"
              :key="index"
            >
              <template v-if="button.icon && button.icon.className">
                <image
                  v-if="button.icon.src"
                  style="width: 16px; height: 16px"
                  class="image-icon"
                  mode="aspectFit"
                  :src="button.icon.src"
                  :class="button.hiddenText ? '' : 'uni-mr-4'"
                />
                <w-icon
                  v-else
                  :icon="button.icon && button.icon.className"
                  color="inherit"
                  :size="16"
                  :class="button.hiddenText ? '' : 'uni-mr-4'"
                ></w-icon>
                <text v-if="!button.hiddenText">{{ button.text }}</text>
              </template>
              <text v-else>{{ button.text }}</text>
            </button>
          </template>
        </view>
      </view>
    </view>
    <view class="uni-mask" v-if="isLoading"></view>

    <!-- 选择排序弹出框 -->
    <uni-popup ref="sortPopup" :style="sortOrderPopupStyle">
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
                      <uni-icons type="checkmarkempty" color="#2979ff" />
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
    <uni-popup ref="fieldQueryPopup" :isMaskClick="false" :style="fieldQueryPopupStyle">
      <field-query-dialog
        class="w-list-view-popup"
        :queryFields="queryFields"
        @cancel="$refs.fieldQueryPopup.close()"
        @ok="onFieldQueryOk"
      ></field-query-dialog>
    </uni-popup>
  </view>
  <w-widget-development v-else :widget="widget" :parent="parent"></w-widget-development>
</template>

<script>
let loadMoreText = "正在加载...";
let noMoreText = "没有更多数据了!";
let noMatchText = "无匹配数据!";
import { mapState } from "vuex";
import { storage, appContext } from "wellapp-uni-framework";
import mixin from "../page-widget-mixin";
import { DataStore } from "wellapp-uni-framework";
import fieldQueryDialog from "./field-query-dialog.vue";
import cardItemBody from "./card-item-body.vue";
import listItemBody from "./list-item-body.vue";
import swipeButtonGroup from "./swipe-button-group.vue";
import { each, isEmpty, includes } from "lodash";
export default {
  mixins: [mixin],
  components: { fieldQueryDialog, cardItemBody, listItemBody, swipeButtonGroup },
  data() {
    const _self = this;
    let configuration = _self.widget.configuration || {};
    let queryConfig = configuration.query || {};
    return {
      configuration: configuration,
      enableKeywordQuery: configuration.hasSearch || queryConfig.keyword,
      enableFieldQuery: queryConfig.fieldSearch && queryConfig.fields.length > 0,
      queryFields: queryConfig.fields || [],
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
      scrollTopUpdated: false,
      windowHeight: null,
      refresherTriggered: false,
      isLoading: false,
      isRefreshing: false,
      showLoadMore: false,
      loadMoreText: loadMoreText,
      noMatchText: noMatchText,
      data: [],
      showSortOrder: false, // 是否显示排序
      sortOrderConfigs: [],
      sortOrderPopupStyle: {
        top: "100px",
      },
      selectedSortOrder: null, // 用户选择的排序
      otherOrders: {},
      fieldQueryPopupStyle: {
        top: "100px",
      },
      otherConditions: [],
      isCustomQuery: false, // 是否用户进行的查询
      editState: false, // 是否处于编辑状态
      selection: [], //选择的数据
      buttonRect: {},
      tableEndButtons: [],
      tableEndMoreButtons: [],
      listHeaderMarginButtom: 10,
    };
  },
  created() {
    var _self = this;
    // 创建数据仓库
    var configuration = _self.getConfiguration();
    _self._dataProvider = new DataStore({
      type: configuration.type,
      dataStoreId: _self.getDataStoreId(),
      onDataChange: function (data, count, params) {
        _self.stopLoadingData(params);
        _self.onDataChange(params);
      },
      renderers: _self.getRenderers(),
      defaultOrders: _self.getDefaultSort(),
      defaultCriterions: _self.getDefaultConditions(),
      receiver: _self,
      autoCount: false,
      pageSize: configuration.pageSize || 20,
    });

    // 初始化数据
    _self.initData();

    _self.initSortOrderConfigs();

    _self.createActionSheetIfRequried();

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
    this.initScrollViewHeight();
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
    // 是否有表格尾部操作按钮
    hasTableEndButtons: function () {
      return !isEmpty(this.tableEndButtons);
    },
    // 是否有行未操作按钮
    hasRowEndButtons: function () {
      let buttons = this.getRowEndButtons();
      return !isEmpty(buttons);
    },
    hasListHeader() {
      return this.enableKeywordQuery || this.showSortOrder || this.enableFieldQuery || this.hasTableEndButtons;
    },
  },
  methods: {
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
      this.refresherTriggered = true;
    },
    onRefresherRefresh(e) {
      console.log("onRefresherRefresh", e);
      if (this.isRefreshing) {
        return;
      }
      this.isRefreshing = true;
      // 更新滚动视图高度
      // this.updateScrollViewHeightIfRequired(e);
      // 上拉刷新初始化数据
      this.initData();
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
      this.onRefresherRefresh(options);
    },
    stopLoadingData: function (init) {
      console.log("stopLoadingData");
      var _self = this;
      _self.isLoading = false;
      _self.loadMoreText = "";
      _self.isRefreshing = false;
      _self.refresherTriggered = false;
      var dataList = _self.getDataProvider().getData().data;
      // 没有查询结果
      if (init && dataList.length == 0) {
        _self.isCustomQuery =
          !isEmpty(_self.inputQueryValue) || (_self.userCustomCriterions && _self.userCustomCriterions.length > 0);
        if (_self.isCustomQuery) {
          _self.showLoadMore = false;
          _self.noMatchText = noMatchText;
        }
      } else if (dataList.length == 0) {
        _self.loadMoreText = noMoreText;
      }
    },
    initData() {
      console.log("initData");
      var _self = this;
      _self.isLoading = true;
      _self.load(true);
    },
    loadMoreData: function (e) {
      console.log("loadMoreData", e);
      var _self = this;
      _self.isLoading = true;
      _self.showLoadMore = true;
      _self.loadMoreText = loadMoreText;
      _self.getDataProvider().nextPage(false);
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
      if (!_self.editState) {
        let url = "";
        if (configuration.detailPageUrl) {
          url = configuration.detailPageUrl;
          console.log(JSON.stringify(item));
          _self.setPageParameter("item", item);
        } else if (configuration.rowClickEvent && configuration.rowClickEvent.enable) {
          let eventHandler = configuration.rowClickEvent.option || {};
          let eventParams = configuration.rowClickEvent.option.eventParams || {};
          if (eventHandler.actionType == "redirectPage" && eventHandler.pageType == "url") {
            url = _self.getUrlData(eventHandler, eventParams);
            _self.setPageParameter("item", item);
          } else {
            appContext.dispatchEvent({
              ui: _self,
              meta: item,
              ...eventHandler,
            });
          }
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
        _self.$emit("selectionChange", _self.selection, _self.editState, configuration);
      }
      _self.$emit("itemClick", item, _self.editState, configuration);
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
    // 全选
    selectAllItem: function () {
      var _self = this;
      _self.selection = [];
      var dataLis = _self.data;
      each(dataLis, function (item) {
        _self.addSelectItem(item);
        _self.updateItemExtraIcon(item);
      });
    },
    // 移除所有选择
    removeAllSelected: function () {
      var _self = this;
      _self.selection = [];
      var dataLis = _self.data;
      each(dataLis, function (data) {
        var extraIcon = data.extraIcon;
        if (extraIcon) {
          extraIcon.type = "circle";
          extraIcon.color = "#C0C0C0";
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
      var configuration = _self.getConfiguration();
      // var pageSize = configuration.pageSize || 20;
      var dataList = _self.getDataProvider().getData().data;
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
    },
    // 更新占位符高度
    updateFillPlaceHolderHeightIfRequired: function () {
      var _self = this;
      setTimeout(function () {
        const query = uni.createSelectorQuery().in(_self);
        query
          .select(".uni-list")
          .boundingClientRect((data) => {
            // console.log("uni-list: " + JSON.stringify(data));
            // 编辑状态底部操作栏高度
            var tableEndToolbarHeight = _self.editState ? 50 : 0;
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
          if (this.hasListHeader) {
            const query = uni.createSelectorQuery().in(this);
            query
              .select(".list-header")
              .boundingClientRect((data) => {
                // console.log("data: " + JSON.stringify(data));
                this.scrollViewHeight = windowHeight - data.bottom - this.listHeaderMarginButtom;
                this.scrollViewStyle.height = this.scrollViewHeight + "px";
                // console.log("this.scrollViewStyle.height: " + this.scrollViewHeight);
              })
              .exec();
          } else {
            this.scrollViewHeight = windowHeight;
            this.scrollViewStyle.height = this.scrollViewHeight + "px";
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
    getDataProvider: function () {
      return this._dataProvider;
    },
    /**
     * 通知数据源回调根据条件加载数据
     */
    load: function (data) {
      var _self = this;
      var criterions = _self._collectCriterion();
      var keyword = _self._collectSearchWord();

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

      _self.getDataProvider().load(
        {
          pagination: {
            currentPage: 1,
          },
          orders: orders,
          keyword: keyword,
          criterions: criterions,
        },
        data
      );
    },
    renderRowData: function (rowData, configuration) {
      var _self = this;
      // 可选择
      if (configuration.selectable) {
        _self.updateItemExtraIcon(rowData);
      } else if (_self.hasTableEndButtons) {
        _self.addItemExtraIcon(rowData);
      }
      _self.$emit("beforeRenderRowData", rowData, configuration);
      // 支持的templateProperties属性title、note、rightText
      var itemHtml = configuration.templateHtml;
      // console.log("template: " + itemHtml);
      each(configuration.templateProperties, function (property) {
        // var re = eval('/\\{' + property.name + '\\}/mg;');
        var value = rowData[property.mapColumn];
        if (property.renderer && !isEmpty(property.renderer.rendererType)) {
          value = rowData[property.mapColumn + "RenderValue"];
        }
        rowData[property.name] = value;
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
        _self.$set(item, "extraIcon", item.extraIcon || { size: 24 });
        _self.$set(item.extraIcon, "type", checked ? "checkbox-filled" : "circle");
        _self.$set(item.extraIcon, "color", checked ? "#007aff" : "#C0C0C0");
      }
    },
    addItemExtraIcon: function (item) {
      if (!item.extraIcon) {
        item.extraIcon = {
          type: "circle",
          size: 24,
          color: "#C0C0C0",
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
        if (property.renderer && !isEmpty(property.renderer.rendererType)) {
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
      const query = uni.createSelectorQuery().in(this);
      query
        .select(".list-sort")
        .boundingClientRect((data) => {
          console.log(data.bottom);
          _self.sortOrderPopupStyle.top = data.bottom + "px";
          _self.$refs.sortPopup.open("top");
        })
        .exec();
    },
    /*
     */
    onSortOrderRadioChange: function (event) {
      var _self = this;
      var items = _self.sortOrderConfigs;
      _self.selectedSortOrder = null;
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        var value = item.id;
        if (value === event.detail.value) {
          // item.checked = true;
          _self.$set(item, "checked", true);
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
      each(this.widget.configuration.templateProperties, function (property) {
        if (property.sortOrder == "asc" || property.sortOrder == "desc") {
          let ascRemark = "";
          let descRemark = "";
          if (property.dataType == "Date") {
            ascRemark = "从远到近";
            descRemark = "从近到远";
          } else {
            ascRemark = "升序";
            descRemark = "降序";
          }
          sortOrderConfigs.push({
            sortName: property.mapColumn,
            sortOrder: "asc",
            text: "按" + property.title + ascRemark,
            id: property.mapColumn + "_asc",
          });
          sortOrderConfigs.push({
            sortName: property.mapColumn,
            sortOrder: "desc",
            text: "按" + property.title + descRemark,
            id: property.mapColumn + "_desc",
          });
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
        criterion.sql = configuration.defaultCondition;
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
        each(configuration.templateProperties, function (property) {
          orcriterion.conditions.push({
            columnIndex: property.mapColumn,
            value: _self.inputQueryValue,
            type: "like",
          });
        });
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
    getButtonByPosition: function (btnPos) {
      let buttons = this.configuration.buttons || [];
      let btns = [];
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
          btns.push(Object.assign({}, button));
        }
      });
      return btns;
    },
    swipButtonClick: function (button, item) {
      this.buttonClick(button, [item]);
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
      if (button.eventHandler) {
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
          let url = _self.getUrlData(eventHandler, eventParams);
          if (url) {
            uni.navigateTo({
              url: url,
            });
          }
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
      if (actions.length <= showBtnCount) {
        _self.tableEndButtons = actions;
      } else {
        let newActions = [];
        let newMoreActions = [];
        for (var i = 0; i < actions.length; i++) {
          let action = actions[i];
          if (i < showBtnCount - 1) {
            newActions.push(action);
          } else if (i == showBtnCount - 1) {
            newActions.push({
              id: "more",
              text: "更多",
            });
            newMoreActions.push(action);
          } else {
            newMoreActions.push(action);
          }
        }
        _self.tableEndButtons = newActions;
        _self.tableEndMoreButtons = newMoreActions;
      }
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
      _self.editState = true;
      _self.$set(_self.configuration, "selectable", true);
      _self.configuration.multiSelect = true;
    },
    /**
     * 离开编辑状态
     */
    leaveEditState: function () {
      const _self = this;
      _self.editState = false;
      _self.$set(_self.configuration, "selectable", false);
      _self.configuration.multiSelect = false;
    },
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
  },
};
</script>

<style lang="scss" scoped>
.w-list-view {
  $w-list-view-header-bg-color: #ffffff;
  color: $uni-text-color;

  uni-icon {
    vertical-align: middle;
  }

  .list-header {
    padding: 12px;
    background-color: $w-list-view-header-bg-color;
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
        margin-left: 12px;
      }
    }

    .list-header-box + .list-filter-sort {
      margin-top: 12px;
    }

    .table-end-btn-control {
      text-align: right;
    }

    .list-filter-sort {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
      .icon {
        color: $uni-icon-color !important;
      }
    }

    .list-filter {
      & + .table-end-btn-control {
        margin-left: 12px;
      }
    }

    .list-sort {
      .active {
        color: $uni-text-color-active;
      }
    }
  }

  .list-body {
    .uni-list {
      background-color: $uni-bg-color;

      ::v-deep .uni-card__content {
        color: $uni-text-color;
      }

      ::v-deep .uni-list-item {
        background-color: $uni-bg-secondary-color;
      }
      ::v-deep .uni-list-item__content-title,
      ::v-deep .uni-list-item__content-subtitle,
      ::v-deep .uni-list-item__extra-text {
        color: $uni-text-color;
      }
    }
  }

  .item-card {
    margin: 5px 10px !important;
    background-color: $uni-bg-secondary-color;

    ::v-deep .item-card-title {
      border-bottom: 1px solid $uni-border-1;
      // box-shadow: $uni-shadow-base;
      font-weight: bold;
    }
    ::v-deep .content-label {
      min-width: 90px;
      color: $uni-text-color-grey;
    }
  }
  ::v-deep .uni-card--border {
    border: 1px solid $uni-border-3;
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
    position: fixed;
    width: 100%;
    bottom: var(--window-bottom, 0);
    background-color: $uni-bg-color;
    z-index: 10;
    box-shadow: 0 -1px 4px $uni-bg-color;

    .toolbar-container {
      display: flex;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      height: 50px;
      line-height: 50px;
    }

    .check-info {
      width: 110px;

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
</style>
