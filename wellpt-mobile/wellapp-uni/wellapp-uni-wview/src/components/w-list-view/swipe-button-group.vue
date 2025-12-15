<template>
  <view class="swipe-button-group">
    <template v-for="(button, btnIndex) in buttons">
      <view
        class="swipe-button"
        :class="button.cssClass"
        :style="{
          backgroundColor: button.bgColor || 'initial',
          color: button.textColor || 'initial',
        }"
        @click.stop="swipButtonClick(button)"
        :key="btnIndex"
      >
        <template v-if="button.icon && button.icon.className">
          <image
            v-if="button.icon.src"
            style="width: 24px; height: 24px"
            class="image"
            mode="aspectFit"
            :src="button.icon.src"
          />
          <w-icon
            v-else
            :icon="button.icon && button.icon.className"
            iconClass="swipe-buttion-icon"
            color="inherit"
            :size="24"
          ></w-icon>
          <view v-if="!button.hiddenText" class="swipe-button-text">{{ button.text }}</view>
        </template>
        <view v-else class="swipe-button-text">{{ button.text }}</view>
      </view>
    </template>
  </view>
</template>

<script>
export default {
  props: {
    buttons: Array,
    rowData: Object,
  },
  methods: {
    swipButtonClick: function (button) {
      this.$emit("swipButtonClick", button, this.rowData);
    },
  },
  computed: {},
  data() {
    return {};
  },
};
</script>

<style lang="scss" scoped>
.swipe-button-group {
  /* #ifndef APP-NVUE */
  display: flex;
  height: 100%;
  /* #endif */
  flex: 1;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  // padding: 0 20px;
  // background-color: #ff5a5f;

  .swipe-button {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 0 20px;
    height: 100%;
  }

  .swipe-buttion-icon {
    margin-right: 2px;
    padding-bottom: 4px;
    color: inherit;
  }

  .swipe-button-text {
    color: inherit;
    font-size: $uni-font-size-base;
  }

  .btn-inverse {
    background-color: $uni-btn-color-inverse; // #000000; // 黑色
    color: $uni-text-color-inverse;
  }
  .btn-default {
    background-color: $uni-btn-color-default; // #d4d4d4; // 灰色
    color: $uni-text-color;
  }
  .btn-primary {
    background-color: $uni-btn-color-primary; // #007aff; // 蓝色
    color: $uni-text-color-inverse;
  }
  .btn-success {
    background-color: $uni-btn-color-success; // #3aa322; // 绿色
    color: $uni-text-color-inverse;
  }
  .btn-info {
    background-color: $uni-btn-color-info; // #2aaedd; // 浅蓝
    color: $uni-text-color-inverse;
  }
  .btn-warning {
    background-color: $uni-btn-color-warning; // #e99f00; // 橙色
    color: $uni-text-color-inverse;
  }
  .btn-danger {
    background-color: $uni-btn-color-danger; // #e33033; // 红色
    color: $uni-text-color-inverse;
  }
}
</style>
