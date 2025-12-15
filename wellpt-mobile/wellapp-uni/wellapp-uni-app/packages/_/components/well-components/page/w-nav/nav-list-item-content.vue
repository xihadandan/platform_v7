<template>
  <view class="nav-list-item-content">
    <view v-if="item.thumbImgUrl">
      <image :src="item.thumbImgUrl" class="image" :class="['image--' + thumbSize]" />
    </view>
    <view v-else-if="item.extraIcon">
      <uni-icons class="icon" :type="item.extraIcon.type" :size="22"></uni-icons>
    </view>
    <view class="uni-ml-2"></view>
    <text>{{ item.name }} </text>
  </view>
</template>

<script>
export default {
  props: {
    item: Object,
    thumbSize: String,
  },
};
</script>

<style lang="scss" scoped>
.nav-list-item-content {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  color: $uni-text-color;

  .image {
    /* #ifndef APP-NVUE */
    display: block;
    /* #endif */
    height: $uni-img-size-base;
    width: $uni-img-size-base;
  }

  .image--lg {
    height: $uni-img-size-lg;
    width: $uni-img-size-lg;
  }
  .image--base {
    height: $uni-img-size-base;
    width: $uni-img-size-base;
  }
  .image--sm {
    height: $uni-img-size-sm;
    width: $uni-img-size-sm;
  }

  .icon {
    color: $uni-icon-color !important;
  }
}
</style>
