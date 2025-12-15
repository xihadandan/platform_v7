<template>
  <view class="wgt-update-dialog" :style="theme">
    <view class="wgt-update-title">
      <text class="wgt-update-title-text">{{ title }}</text>
    </view>
    <view class="wgt-update-content progress-box">
      <progress :percent="updatePercent" show-info stroke-width="3" />
      <view>{{ updateTips }}</view>
    </view>
  </view>
</template>

<script>
export default {
  props: {
    title: String,
    updatePercent: Number,
    updateTips: String,
  },
};
</script>

<style lang="scss" scoped>
.wgt-update-dialog {
  width: 100%;
  border-radius: 11px;
  background-color: $uni-bg-secondary-color;
  display: flex;
  flex-direction: column;

  .wgt-update-title {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    justify-content: center;
    padding-top: 25px;
  }

  .wgt-update-title-text {
    font-size: 16px;
    font-weight: 500;
  }

  .wgt-update-content {
    margin: 10px 5px 15px 5px;
  }
}
</style>
