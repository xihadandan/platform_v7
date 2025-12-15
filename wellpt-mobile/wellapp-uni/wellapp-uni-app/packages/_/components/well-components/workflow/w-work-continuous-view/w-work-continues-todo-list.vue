<template>
  <view class="w-work-continues-todo-list">
    <view class="todo-list__header">
      <uni-search-bar
        class="keyword-query-input"
        radius="5"
        :placeholder="$t('global.placeholder', '请输入搜索内容')"
        :cancelText="$t('global.cancel', '取消')"
        clearButton="auto"
        cancelButton="auto"
        @confirm="keywordQuery"
        @cancel="cancelKeywordQuery"
        @clear="keywordQuery"
      />
      <view class="list-filter" @tap="filterOpen">
        <icon type="pticon iconfont icon-ptkj-shaixuan" class="uni-mr-2" :size="16" />
        <text>{{ $t("WorkflowWork.sift", "筛选") }}</text>
      </view>
      <view class="list-setting" @tap="settingOpen">
        <icon type="pticon iconfont icon-ptkj-shezhi"  class="uni-mr-2" :size="16" />
        <text>{{ $t("WorkflowWork.opinionManager.operation.setting", "设置") }}</text>
      </view>
    </view>
    <view class="scroll-view">
      <scroll-view class="scroll-view-box" scroll-y="true" @scrolltolower="onScrollToLower">
        <uni-list class="todo-list">
          <uni-list-item
            direction="column"
            :border="false"
            :class="{ 'todo-list-item': true, active: isCurrentWorkData(item) }"
            v-for="(item, index) in list"
            :key="item.uuid"
            clickable
            @tap="onTodoItemClick(item, index)"
          >
            <template v-slot:body>
              <view :class="{ 'item-details': true }">
                <view class="title">
                  <template v-if="item.titleRenderValue">
                    <rich-text :nodes="item.titleRenderValue"></rich-text>
                  </template>
                  <template v-else>{{ item.title }}</template>
                </view>
                <view>
                  <w-icon icon="iconfont icon-luojizujian-luojibianpai" :size="14"></w-icon>
                  {{ $t("WorkflowWork.currentTask", "当前环节") }}：{{ item.taskName }}
                </view>
                <view>
                  <w-icon icon="iconfont icon-ptkj-shizhongxuanzeshijian" :size="14"> </w-icon>
                  {{ $t("WorkflowWork.processName", "流程名称") }}：{{ item.flowName }}
                </view>
                <view>
                  <w-icon icon="iconfont icon-szgy-huoqujiaohaorenyuanxinxi" :size="14"></w-icon>
                  {{ $t("WorkflowWork.initiator", "发起人") }}：{{
                    item.flowStartUserName || item.flowStartUserIdRenderValue || item.flowStartUserId
                  }}
                </view>
              </view>
            </template>
          </uni-list-item>
        </uni-list>
        <template v-if="list.length == 0">
          <uni-w-empty v-if="searchText" type="search" :text="noMatchText"></uni-w-empty>
          <uni-w-empty v-else></uni-w-empty>
        </template>
        <uni-w-empty v-else-if="showLoadingMore" noImage :text="loadMoreText"></uni-w-empty>
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
    showLoadingMore: Boolean,
  },
  components: {},
  data() {
    let loadMoreText = this.$t("global.loading", "加载更多");
    let noMatchText = this.$t("global.noMatch", "无匹配数据");
    return {
      loadMoreText,
      noMatchText,
    };
  },
  created: function () {
    var _self = this;
    var options = _self.options;
  },
  onUnload: function () {},
  mounted: function () {},
  computed: {},
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
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
