<template>
  <view class="nav-card-content uni-mt-8 uni-mb-8">
    <view class="nav-card-content-image" v-if="item.thumbImgUrl">
      <image :src="item.thumbImgUrl" class="image" :class="['image--' + thumbSize]" />
    </view>
    <view class="nav-card-content-icon" v-else-if="item.extraIcon">
      <uni-icons class="icon" :type="item.extraIcon.type" :size="50"></uni-icons>
    </view>
    <view class="uni-ml-2"></view>
    <text class="text">{{ item.name }} </text>
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
.nav-card-content {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;

  .nav-card-content-image,
  .nav-card-content-icon {
    display: flex;
  }

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

  .text {
    color: $uni-text-color;
  }
}
</style>
