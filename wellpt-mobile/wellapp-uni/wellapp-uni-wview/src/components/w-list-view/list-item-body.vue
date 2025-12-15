<template>
  <rich-text
    v-if="configuration.itemContentType == '2'"
    class="list-item-body"
    :nodes="renderCustomRowContent(rowData)"
  ></rich-text>
  <view
    v-else
    class="list-item-body uni-list-item__content"
    :class="{ 'uni-list-item__content--center': configuration.selectable }"
  >
    <text v-if="rowData.title" class="uni-list-item__content-title">{{ rowData.title }}</text>
    <text v-if="rowData.subtitle" class="uni-list-item__content-subtitle">{{ rowData.subtitle }}</text>
    <text v-if="rowData.note" class="uni-list-item__content-note">{{ rowData.note }}</text>
  </view>
</template>

<script>
import mixin from "./item-body-mixin";
export default {
  mixins: [mixin],
  methods: {},
};
</script>

<style lang="scss" scoped>
.list-item-body {
}
.uni-list-item__content {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  padding-right: 8px;
  flex: 1;
  color: #3b4144;
  // overflow: hidden;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
}

.uni-list-item__content--center {
  justify-content: center;
}

.uni-list-item__content-title {
  font-size: $uni-font-size-base;
  color: #3b4144;
  overflow: hidden;
}

.uni-list-item__content-subtitle {
  font-size: $uni-font-size-sm;
  margin-top: 10rpx;
  color: #3b4144;
  overflow: hidden;
}

.uni-list-item__content-note {
  margin-top: 6rpx;
  color: $uni-text-color-grey;
  font-size: $uni-font-size-sm;
  overflow: hidden;
}

.unread {
  font-weight: bold;
}
</style>
