<template>
  <view :style="theme" style="width: 100%">
    <uni-nav-bar
      class="pt-nav-bar"
      :title="$t('WorkflowWork.startNewWork', '发起流程')"
      :shadow="false"
      :border="false"
      statusBar
      @clickLeft="back"
      left-icon="left"
    />
    <view style="background-color: #ffffff">
      <uni-search-bar
        class="keyword-query-input"
        radius="5"
        :placeholder="$t('WorkflowWork.searchWorkflowPlaceholder', '搜索流程')"
        :cancelText="$t('global.cancel', '取消')"
        clearButton="auto"
        cancelButton="auto"
        @confirm="keywordQuery"
        @cancel="cancelKeywordQuery"
        @clear="keywordQuery"
      />
    </view>
    <uni-collapse class="start-new-work" accordion v-model="activeKey">
      <uni-collapse-item
        v-for="(node, index1) in definitionNodes"
        :index="index1"
        :key="index1"
        :show-animation="true"
        title-border="none"
        :border="false"
        :show-arrow="false"
        :class="[activeKey === index1 + '' ? 'item-open' : '']"
      >
        <template slot="title">
          <view class="title-left f_g_1">
            <view class="title-icon" :style="node.iconStyle ? 'background-' + node.iconStyle : ''">
              <w-icon
                :icon="
                  node.id == 'recent_use'
                    ? 'iconfont icon-oa-zuijinfangwen'
                    : node.iconSkin
                    ? node.iconSkin
                    : 'iconfont icon-luojizujian-luojibianpai'
                "
                :size="16"
              ></w-icon>
            </view>
            <rich-text :nodes="node.name" class="title-text"></rich-text>
            ({{ node.children && node.children.length ? node.children.length : 0 }})
          </view>
          <view class="title-right f_s_0">
            <view class="title-right-icon">
              <w-icon
                :icon="activeKey === index1 + '' ? 'iconfont icon-ptkj-jianhao' : 'iconfont icon-ptkj-jiahao'"
                :size="14"
              ></w-icon>
            </view>
          </view>
        </template>
        <uni-list class="uni-list" :border="false">
          <uni-list-item
            v-for="(item, index2) in node.children"
            :border="false"
            :index="index2"
            :key="index2"
            clickable
            @click="itemClick($event, item)"
            v-show="
              node.id !== 'recent_use' ||
              (node.id == 'recent_use' && index2 < 4) ||
              (node.id == 'recent_use' && index2 > 4 && showMore)
            "
          >
            <template v-slot:body>
              <view class="flex" style="width: 100%">
                <w-icon
                  icon="iconfont icon-ptkj_bianhao"
                  :size="10"
                  color="var(--w-primary-color-4)"
                  style="margin-right: var(--w-padding-3xs)"
                ></w-icon>
                <view class="f_g_1">
                  <rich-text :nodes="item.name" class="title-text"></rich-text>
                </view>
                <!-- <view>
                  <w-icon icon="iconfont icon-jiantou-01" :size="16" color="var(--w-text-color-light)"></w-icon>
                </view> -->
              </view>
            </template>
          </uni-list-item>
          <uni-w-button
            type="link"
            style="margin-top: var(--w-margin-2xs)"
            v-if="node.id == 'recent_use' && node.children.length > 4"
            @click="showMore = !showMore"
            >{{ $t(showMore ? "global.collapse" : "global.loadMore") }}</uni-w-button
          >
        </uni-list>
      </uni-collapse-item>
    </uni-collapse>
    <uni-w-empty
      type="search"
      :text="$t('global.noMatch', '无匹配数据')"
      v-if="searchText && definitionNodes.length == 0"
    ></uni-w-empty>
  </view>
</template>

<script>
import { isEmpty, each } from "lodash";
import { utils, storage } from "wellapp-uni-framework";
export default {
  data() {
    return {
      extraIcon: {
        color: "var(--text-color)",
        size: "6",
        type: "pticon iconfont icon-ptkj-jiedian",
      },
      definitionNodes: [],
      allCategoryNodeJson: [],
      searchText: "",
      activeKey: "",
      nullImg: "/static/resource/images/search-empty.png",
      showMore: false,
    };
  },
  onLoad: function () {
    var _self = this;
    uni.showLoading({
      title: this.$t("global.loading", "加载中..."),
    });
    _self
      .checkIsShowRecentUsed()
      .then((showRecentUsed) => {
        console.log(showRecentUsed);
        _self.$axios.get(`/api/workflow/new/work/getMobileFlowDefinitions`).then(({ data }) => {
          var dataList = data.data;
          var nodes = [];
          each(dataList, function (node) {
            if (
              node.children &&
              node.children.length > 0 &&
              (node.id != "recent_use" || (showRecentUsed && node.id == "recent_use"))
            ) {
              if (node.id == "recent_use") {
                node.name = _self.$t("WorkflowWork.opinionManager.recentTabTitle", "最近使用");
              }
              nodes.push(node);
            }
          });
          _self.definitionNodes = nodes;
          _self.allCategoryNodeJson = JSON.stringify(nodes);
          _self.$nextTick(() => {
            _self.activeKey = "0";
          });
          uni.hideLoading();
        });
      })
      .catch((e) => {
        uni.hideLoading();
      });
  },
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    // 检查流程管理-全局设置是否显示最近使用
    checkIsShowRecentUsed() {
      return this.$axios.get(`/api/workflow/setting/getByKey?key=GENERAL`).then(({ data: result }) => {
        if (result.data && result.data.attrVal) {
          let setting = JSON.parse(result.data.attrVal);
          return setting.showRecentUsed != null ? setting.showRecentUsed : true;
        } else {
          return false;
        }
      });
    },
    itemClick: function (e, item) {
      console.log(e);
      console.log(item);
      this.addRecentUsed(item.data);
      // 关闭当前页面，跳转到流程实例页面
      uni.redirectTo({
        url: "/packages/_/pages/workflow/work_view?flowDefId=" + item.data,
      });
    },
    /**
     * 记录最近使用
     *
     * @param {*} flowDefId
     */
    addRecentUsed(flowDefId) {
      return this.$axios.post("/json/data/services", {
        serviceName: "recentUseFacadeService",
        methodName: "use",
        args: JSON.stringify([flowDefId, "WORKFLOW"]),
      });
    },
    keywordQuery: function (e) {
      console.log("keywordQuery");
      var _self = this;
      _self.searchText = e.value;
      this.onSearchFlow();
    },
    cancelKeywordQuery: function (e) {
      console.log("cancelKeywordQuery", e);
      var _self = this;
      _self.searchText = "";
      this.onSearchFlow();
    },
    onSearchFlow() {
      const _this = this;
      _this.activeKey = "all";
      let searchText = _this.searchText.trim();
      if (isEmpty(searchText)) {
        if (_this.allCategoryNodeJson) {
          _this.definitionNodes = JSON.parse(_this.allCategoryNodeJson);
        } else {
          console.error("allCategoryNodeJson is null");
        }
      } else {
        _this.definitionNodes = _this.searchFlow(searchText, JSON.parse(_this.allCategoryNodeJson));
      }
    },
    searchFlow(searchText, categoryNodes) {
      let retNodes = [];
      categoryNodes.forEach((node) => {
        let categoryName = node.name || "";
        let categoryNameInclude = this.highlightText(categoryName);
        // 查询分类名称
        if (categoryNameInclude.test) {
          node.name = categoryNameInclude.text;
          retNodes.push(node);

          // 分类下的流程名称
          let children = node.children || [];
          children.forEach((child) => {
            let flowName = child.name || "";
            let flowNameInclude = this.highlightText(flowName);
            if (flowNameInclude.test) {
              child.name = flowNameInclude.text;
            }
          });
        } else {
          // 查询分类下的流程名称
          let children = node.children || [];
          let matchChildren = [];
          children.forEach((child) => {
            let flowName = child.name || "";
            let flowNameInclude = this.highlightText(flowName);
            if (flowNameInclude.test) {
              child.name = flowNameInclude.text;
              matchChildren.push(child);
            }
          });
          if (matchChildren.length > 0) {
            node.children = matchChildren;
            retNodes.push(node);
          }
        }
      });
      if (retNodes.length) {
        this.activeKey = "0";
      }
      return retNodes;
    },
    back() {
      uni.navigateBack({
        delta: 1,
      });
    },
    // 高亮显示匹配文本
    highlightText(text) {
      if (!text || !this.searchText || typeof text !== "string") {
        return {
          test: false,
          text: text,
        };
      }
      const escapedText = this.searchText.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
      const regex = new RegExp(`(${escapedText})`, "gi");
      return {
        test: regex.test(text),
        text: text.replace(regex, '<span class="match-text">$1</span>'),
      };
    },
  },
};
</script>

<style lang="scss" scoped>
.start-new-work {
  background-color: transparent;
  ::v-deep .uni-collapse-item {
    padding: var(--w-padding-xs) var(--w-padding-xs) 0;
    .uni-collapse-item__title {
      border-radius: 8px;
      background-color: #ffffff;
    }

    &.item-open {
      .uni-collapse-item__title {
        border-bottom-left-radius: 0px;
        border-bottom-right-radius: 0px;
      }
    }
    .uni-collapse-item__title-wrap {
      display: flex;
      align-items: center;
      .title-left {
        display: flex;
        padding: 0 15px;
        height: 48px;
        font-size: var(--w-font-size-lg);
        color: var(--w-text-color-mobile);
        font-weight: bold;
        cursor: pointer;
        outline: none;
        align-items: center;
        .match-text {
          color: #e33033;
        }
        .title-text {
          padding-right: var(--w-margin-2xs);
          > div {
            flex: 1;
            font-size: 16px;
            display: -webkit-box;
            overflow: hidden;
            white-space: normal !important;
            text-overflow: ellipsis;
            word-wrap: break-word;
            -webkit-line-clamp: 1;
            -webkit-box-orient: vertical;
            word-break: break-all;
          }
        }
        .title-icon {
          flex-shrink: 0;
          border-radius: 4px;
          height: 24px;
          width: 24px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #ffffff;
          background-color: var(--w-primary-color);
          margin-right: var(--w-margin-2xs);
          .w-icon {
            display: inherit;
          }
        }
      }
      .title-right {
        text-align: right;
        padding-right: var(--w-padding-xs);

        .title-right-icon {
          flex-shrink: 0;
          border-radius: 50%;
          height: 20px;
          width: 20px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: var(--w-primary-color);
          background-color: var(--w-primary-color-2);
          margin-right: var(--w-margin-2xs);
          .w-icon {
            display: inherit;
          }
        }
      }
    }

    &:last-child {
      padding-bottom: var(--w-padding-xs);
    }

    .uni-collapse-item__wrap-content,
    .uni-collapse-item__wrap {
      border-bottom-left-radius: 8px;
      border-bottom-right-radius: 8px;
    }
  }

  ::v-deep .uni-list {
    background-color: $uni-bg-secondary-color;
    padding-bottom: var(--w-margin-2xs);
    border-bottom-left-radius: 8px;
    border-bottom-right-radius: 8px;

    &::after {
      content: none;
    }

    .uni-list-item {
      margin: var(--w-margin-2xs) var(--w-margin-xs) 0;
    }
    .uni-list-item__content-title {
      color: $uni-text-color;
    }
    .uni-list-item__container {
      border-radius: 8px;
      background-color: var(--w-primary-color-1);
      padding: var(--w-padding-xs) var(--w-padding-xs);
      .title-text {
        font-size: var(--w-font-size-md);
        align-self: center;
        color: $uni-text-color;
        .match-text {
          color: #e33033;
        }
      }
    }
  }
}
</style>
