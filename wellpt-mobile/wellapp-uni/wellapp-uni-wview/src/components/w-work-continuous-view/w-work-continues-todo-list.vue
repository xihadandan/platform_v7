<template>
  <view class="w-work-continues-todo-list">
    <uni-nav-bar
      :statusBar="true"
      :border="false"
      :shadow="true"
      :fixed="true"
      title="待办列表"
      :rightText="pagingInfo.totalCount + ''"
    ></uni-nav-bar>
    <view class="todo-list__header">
      <uni-search-bar
        class="keyword-query-input"
        radius="5"
        placeholder="搜索"
        clearButton="auto"
        cancelButton="auto"
        @confirm="keywordQuery"
        @cancel="cancelKeywordQuery"
        @clear="keywordQuery"
      />
      <view class="list-filter" @tap="filterOpen">
        <icon type="pticon iconfont icon-ptkj-shaixuan" class="uni-mr-4" :size="16" />
        <text>筛选</text>
      </view>
      <view class="list-setting" @tap="settingOpen">
        <icon type="pticon iconfont icon-ptkj-shezhi" :size="16" />
        <text>设置</text>
      </view>
    </view>
    <view class="scroll-view">
      <scroll-view class="scroll-view-box" scroll-y="true" @scrolltolower="onScrollToLower">
        <uni-list class="todo-list">
          <uni-list-item
            class="todo-list-item"
            v-for="(item, index) in list"
            :key="item.uuid"
            clickable
            @tap="onTodoItemClick(item, index)"
          >
            <template v-slot:body>
              <view :class="{ 'item-details': true, active: isCurrentWorkData(item) }">
                <view class="title">
                  <template v-if="item.titleRenderValue">
                    <rich-text :nodes="item.titleRenderValue"></rich-text>
                  </template>
                  <template v-else>{{ item.title }}</template>
                </view>
                <view>当前环节：{{ item.taskName }}</view>
                <view>
                  流程名称：{{ item.flowName }} | 发起人：{{
                    item.flowStartUserName || item.flowStartUserIdRenderValue || item.flowStartUserId
                  }}
                </view>
              </view>
            </template>
          </uni-list-item>
        </uni-list>
        <uni-w-empty v-if="list.length == 0"></uni-w-empty>
      </scroll-view>
    </view>
  </view>
</template>

<script>
export default {
  props: {
    options: {
      type: Object,
      default: () => {},
    },
    list: {
      type: Array,
      default: () => [],
    },
    current: {
      type: Object,
      default: () => {},
    },
    pagingInfo: {
      type: Object,
      default: () => {
        return {
          totalCount: 0,
        };
      },
    },
  },
  components: {},
  data() {
    return {};
  },
  created: function () {
    var _self = this;
    var options = _self.options;
  },
  onUnload: function () {},
  mounted: function () {},
  computed: {},
  methods: {
    // 判断记录是否当前工作区数据
    isCurrentWorkData(item) {
      return (item.taskInstUuid || item.uuid) == (this.options.workBean && this.options.workBean.taskInstUuid);
    },
    onScrollToLower() {
      this.$emit("loadMore");
    },
    filterOpen() {
      this.$emit("filterOpen");
    },
    settingOpen() {
      this.$emit("settingOpen");
    },
    keywordQuery: function (e) {
      var _self = this;
      _self.searchText = e.value;
      this.$emit("search", _self.searchText);
    },
    cancelKeywordQuery: function (e) {
      console.log("cancelKeywordQuery", e);
      var _self = this;
      _self.searchText = "";
      this.$emit("search", _self.searchText);
    },
    onTodoItemClick(item, index) {
      this.$emit("itemClick", item, index);
    },
  },
  watch: {},
};
</script>

<style lang="scss" scoped></style>
