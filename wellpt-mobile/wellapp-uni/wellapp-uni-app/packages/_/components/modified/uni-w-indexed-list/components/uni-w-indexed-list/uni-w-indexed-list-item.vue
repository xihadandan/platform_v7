<template>
  <view>
    <view v-if="loaded || list.itemIndex < 15" class="uni-indexed-list__title-wrapper">
      <text v-if="list.items && list.items.length > 0" class="uni-indexed-list__title">{{ list.key }}</text>
    </view>
    <view v-if="(loaded || list.itemIndex < 15) && list.items && list.items.length > 0" class="uni-indexed-list__list">
      <view
        v-for="(item, index) in list.items"
        :key="index"
        class="uni-indexed-list__item"
        hover-class="uni-indexed-list__item--hover"
      >
        <view class="uni-indexed-list__item-container" @click="onClick(idx, index, item)">
          <view
            class="uni-indexed-list__item-border"
            :class="{ 'uni-indexed-list__item-border--last': index === list.items.length - 1 }"
          >
            <view v-if="showSelect" style="margin-right: 20rpx">
              <uni-icons
                :type="item.checked ? 'checkbox-filled' : 'circle'"
                :color="item.checked ? 'var(--w-primary-color)' : '#C0C0C0'"
                size="24"
              />
            </view>
            <slot name="prefix" :item="item" :index="index" :list="list"></slot>
            <view class="uni-indexed-list__item-content"
              >{{ item.name
              }}<text class="uni-indexed-list__item-sub-content w-ellipsis-1" v-show="item.subName">{{
                item.subName
              }}</text></view
            >
            <slot name="suffix" :item="item" :index="index" :list="list"></slot>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: "UniWIndexedListItem",
  emits: ["itemClick"],
  props: {
    loaded: {
      type: Boolean,
      default: false,
    },
    idx: {
      type: Number,
      default: 0,
    },
    list: {
      type: Object,
      default() {
        return {};
      },
    },
    showSelect: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    onClick(idx, index, item) {
      this.$emit("itemClick", {
        idx,
        index,
        item,
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.uni-indexed-list__list {
  background-color: $uni-bg-color;
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: column;
  border-top-style: solid;
  border-top-width: 1px;
  border-top-color: #dedede;
}

.uni-indexed-list__item {
  font-size: 14px;
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex: 1;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}

.uni-indexed-list__item-container {
  padding-left: 15px;
  flex: 1;
  position: relative;
  /* #ifndef APP-NVUE */
  display: flex;
  box-sizing: border-box;
  /* #endif */
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  /* #ifdef H5 */
  cursor: pointer;
  /* #endif */
}

.uni-indexed-list__item-border {
  flex: 1;
  position: relative;
  /* #ifndef APP-NVUE */
  display: flex;
  box-sizing: border-box;
  /* #endif */
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  min-height: 56px;
  height: auto;
  padding: var(--w-padding-2xs);
  padding-left: 0;
  // border-bottom-style: solid;
  // border-bottom-width: 1px;
  // border-bottom-color: #dedede;

  .uni-indexed-list__item-content {
    &::after {
      content: "";
      position: absolute;
      width: 100%;
      height: 1px;
      background-color: var(--w-border-color-mobile);
      bottom: calc(0px - var(--w-padding-xs));
    }
  }
}

.uni-indexed-list__item-border--last {
  border-bottom-width: 0px;
  .uni-indexed-list__item-content {
    &::after {
      content: none;
    }
  }
}

.uni-indexed-list__item-content {
  position: relative;
  flex: 1;
  font-weight: 600;
  font-size: 16px;
  color: #333333;
  line-height: 20px;
  padding-right: 14px;
}

.uni-indexed-list__item-sub-content {
  // display: block;
  padding-top: 6px;
  font-weight: 400;
  font-size: 12px;
  color: #999999;
  line-height: 12px;
  width: calc(100% - 20px);
}

.uni-indexed-list {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: row;
}

.uni-indexed-list__title-wrapper {
  /* #ifndef APP-NVUE */
  display: flex;
  width: 100%;
  /* #endif */
  background-color: #f7f7f7;
}

.uni-indexed-list__title {
  padding: 6px 12px;
  line-height: 24px;
  font-size: 16px;
  font-weight: 500;
}
</style>
