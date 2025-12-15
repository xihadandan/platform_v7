<template>
  <view :style="theme" style="width: 100%">
    <view>
      <uni-search-bar
        class="keyword-query-input"
        radius="5"
        placeholder="搜索流程"
        clearButton="auto"
        cancelButton="auto"
        @confirm="keywordQuery"
        @cancel="cancelKeywordQuery"
        @clear="keywordQuery"
      />
    </view>
    <uni-collapse class="start-new-work" accordion>
      <uni-collapse-item v-for="(node, index1) in definitionNodes" :index="index1" :key="index1" :show-animation="true">
        <template slot="title">
          <w-icon icon="iconfont icon-ptkj-liucheng" color="var(--color-primary)" :size="14"></w-icon>
          <rich-text :nodes="node.name" class="title-text"></rich-text>
          ({{ node.children && node.children.length ? node.children.length : 0 }})
        </template>
        <uni-list class="uni-list">
          <uni-list-item
            v-for="(item, index2) in node.children"
            :index="index2"
            :key="index2"
            :show-extra-icon="true"
            :extra-icon="extraIcon"
            clickable
            @click="itemClick($event, item)"
          >
            <template v-slot:body>
              <rich-text :nodes="item.name" class="title-text"></rich-text>
            </template>
          </uni-list-item>
        </uni-list>
      </uni-collapse-item>
    </uni-collapse>
    <uni-w-empty type="search" text="无匹配数据" v-if="searchText && definitionNodes.length == 0"></uni-w-empty>
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
    };
  },
  onLoad: function () {
    var _self = this;
    uni.showLoading({
      title: "加载中...",
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
              nodes.push(node);
            }
          });
          _self.definitionNodes = nodes;
          _self.allCategoryNodeJson = JSON.stringify(nodes);
          uni.hideLoading();
        });
      })
      .catch((e) => {
        uni.hideLoading();
      });
  },
  methods: {
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
      // 关闭当前页面，跳转到流程实例页面
      uni.redirectTo({
        url: "/uni_modules/w-app/pages/workflow/work_view?flowDefId=" + item.data,
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
        // 查询分类名称
        if (categoryName.indexOf(searchText) != -1) {
          node.name = categoryName.replace(searchText, `<span class="match-text">${searchText}</span>`);
          retNodes.push(node);

          // 分类下的流程名称
          let children = node.children || [];
          children.forEach((child) => {
            let flowName = child.name || "";
            if (flowName.indexOf(searchText) != -1) {
              child.name = flowName.replace(searchText, `<span class="match-text">${searchText}</span>`);
            }
          });
        } else {
          // 查询分类下的流程名称
          let children = node.children || [];
          let matchChildren = [];
          children.forEach((child) => {
            let flowName = child.name || "";
            if (flowName.indexOf(searchText) != -1) {
              child.name = flowName.replace(searchText, `<span class="match-text">${searchText}</span>`);
              matchChildren.push(child);
            }
          });
          if (matchChildren.length > 0) {
            node.children = matchChildren;
            retNodes.push(node);
          }
        }
      });
      return retNodes;
    },
  },
};
</script>

<style lang="scss" scoped>
.start-new-work {
  background-color: $uni-bg-color;
  ::v-deep .uni-collapse-item__title-box,
  ::v-deep .uni-collapse-item__title-wrap {
    background-color: $uni-bg-color;
    color: $uni-text-color;
  }
  ::v-deep .uni-collapse-item__title-wrap {
    display: flex;
    padding: 0 15px;
    height: 48px;
    line-height: 48px;
    color: #303133;
    font-size: 13px;
    font-weight: 500;
    cursor: pointer;
    outline: none;
    width: calc(100% - 60px);
    .match-text {
      color: #e33033;
    }
    .title-text {
      max-width: calc(100% - 30px);
      > div {
        flex: 1;
        font-size: 14px;
        white-space: nowrap;
        color: inherit;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }

  .uni-list {
    background-color: $uni-bg-secondary-color;

    ::v-deep .uni-list-item {
      background-color: $uni-bg-secondary-color;
    }
    ::v-deep .uni-list-item__content-title {
      color: $uni-text-color;
    }
    .uni-list-item__container {
      .title-text {
        font-size: 14px;
        align-self: center;
        color: $uni-text-color;
        ::v-deep .match-text {
          color: #e33033;
        }
      }
    }
  }
}
</style>
