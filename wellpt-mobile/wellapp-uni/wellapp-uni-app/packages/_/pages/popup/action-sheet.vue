<template>
  <view :style="theme">
    <w-button-action-sheet
      :button="actions"
      ref="actionSheetRef"
      :cancelText="cancelText"
      @selectClick="sheetActionClick"
      @close="onClose"
    />
  </view>
</template>

<script>
import { isFunction } from "lodash";
export default {
  name: "ptActionSheet",
  data() {
    return {
      eventChannel: null,
      options: {},
      actions: [],
      cancelText: "",
    };
  },
  onLoad() {
    this.eventChannel = this.getOpenerEventChannel();
    this.eventChannel.on("ptActionSheet", (options) => {
      if (options) {
        this.options = options;
        this.actions = options.actions;
      }
      if (this.options.cancelText) {
        this.cancelText = this.options.cancelText;
      }
      if (this.$refs.actionSheetRef) {
        this.$refs.actionSheetRef.show = true;
      } else {
        this.$nextTick(() => {
          this.$refs.actionSheetRef.show = true;
        });
      }
    });
  },
  methods: {
    show(options) {
      if (options) {
        this.options = options;
        this.actions = options.actions;
      }
      if (this.options.cancelText) {
        this.cancelText = this.options.cancelText;
      }
      if (this.$refs.actionSheetRef) {
        this.$refs.actionSheetRef.show = true;
      } else {
        this.$nextTick(() => {
          this.$refs.actionSheetRef.show = true;
        });
      }
    },
    sheetActionClick(e) {
      let _this = this;
      if (isFunction(_this.options.click)) {
        _this.options.click(e); //, e.button
      }
    },
    onClose() {
      let _this = this;
      // #ifndef H5
      uni.navigateBack({
        delta: 1,
        success() {
          if (isFunction(_this.options.onClose)) {
            _this.options.onClose();
          }
        },
      });
      // #endif

      // #ifdef H5
      if (isFunction(_this.options.onClose)) {
        _this.options.onClose();
      }
      // #endif
    },
  },
};
</script>

<style>
page {
  background: transparent;
}
</style>
