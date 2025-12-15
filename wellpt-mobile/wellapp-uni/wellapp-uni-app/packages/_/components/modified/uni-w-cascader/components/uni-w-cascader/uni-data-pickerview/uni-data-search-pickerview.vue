<template>
  <view class="uni-w-cascader-search-pickerview">
    <view v-if="data.length == 0 && !loading">
      <uni-w-empty></uni-w-empty>
    </view>
    <scroll-view v-else class="list" :scroll-y="true" style="height: calc(100% - 50px)">
      <view
        class="item"
        :class="{ 'is-disabled': !!item.disable }"
        v-for="(item, j) in data"
        :key="'cascader_search_' + j"
        @click="handleNodeClick(item, j)"
      >
        <rich-text
          class="item-text"
          :class="{ 'item-text-overflow': ellipsis }"
          v-if="item.text"
          :nodes="item.text"
        ></rich-text>
        <!-- <view class="check" v-if="item.value.join(';') == searchSelected.value.join(';')"></view> -->
      </view>
    </scroll-view>
    <view class="loading-cover" v-if="loading">
      <uni-load-more class="load-more" :contentText="loadMore" status="loading"></uni-load-more>
    </view>
  </view>
</template>

<script>
export default {
  name: "UniDataSearchPickerView",
  emits: ["nodeclick", "change", "datachange", "update:modelValue"],
  mixins: [],
  props: {
    data: {
      type: Boolean,
      default: false,
    },
    loading: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      searchSelected: [], // 选中内容
    };
  },
  created() {},
  methods: {
    handleNodeClick(item, i, j) {
      if (item.disable) {
        return;
      }
      this.$emit("change", item.data);
    },
  },
};
</script>

<style lang="scss">
$uni-primary: var(--w-primary-color) !default;

.uni-w-cascader-search-pickerview {
  flex: 1;
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: column;
  overflow: hidden;
  height: 100%;

  .error-text {
    color: var(--w-danger-color);
  }

  .loading-cover {
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(255, 255, 255, 0.5);
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: column;
    align-items: center;
    z-index: 1001;
  }

  .load-more {
    /* #ifndef APP-NVUE */
    margin: auto;
    /* #endif */
  }

  .error-message {
    background-color: #fff;
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    padding: 15px;
    opacity: 0.9;
    z-index: 102;
  }

  /* #ifdef APP-NVUE */
  .selected-area {
    width: 750rpx;
  }
  /* #endif */

  .selected-list {
    /* #ifndef APP-NVUE */
    display: flex;
    flex-wrap: nowrap;
    /* #endif */
    flex-direction: row;
    padding: 0 5px;
    border-bottom: 1px solid var(--w-border-color-mobile);
  }

  .selected-item {
    margin-left: 10px;
    margin-right: 10px;
    padding: 12px 0;
    text-align: center;
    /* #ifndef APP-NVUE */
    white-space: nowrap;
    /* #endif */
  }

  .selected-item-text-overflow {
    width: 168px;
    /* fix nvue */
    overflow: hidden;
    /* #ifndef APP-NVUE */
    width: 6em;
    white-space: nowrap;
    text-overflow: ellipsis;
    -o-text-overflow: ellipsis;
    /* #endif */
  }

  .selected-item-active {
    border-bottom: 2px solid $uni-primary;
  }

  .selected-item-text {
    color: $uni-primary;
  }

  .tab-c {
    position: relative;
    flex: 1;
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    overflow: hidden;
  }

  .list {
    flex: 1;
  }

  .item {
    padding: 12px 15px;
    /* border-bottom: 1px solid #f0f0f0; */
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    justify-content: space-between;
  }

  .is-disabled {
    opacity: 0.5;
  }

  .item-text {
    /* flex: 1; */
    color: #333333;

    .search-text {
      color: var(--w-danger-color);
    }
  }

  .item-text-overflow {
    width: 280px;
    /* fix nvue */
    overflow: hidden;
    /* #ifndef APP-NVUE */
    width: 20em;
    white-space: nowrap;
    text-overflow: ellipsis;
    -o-text-overflow: ellipsis;
    /* #endif */
  }

  .check {
    margin-right: 5px;
    border: 2px solid $uni-primary;
    border-left: 0;
    border-top: 0;
    height: 12px;
    width: 6px;
    transform-origin: center;
    /* #ifndef APP-NVUE */
    transition: all 0.3s;
    /* #endif */
    transform: rotate(45deg);
  }
}
</style>
