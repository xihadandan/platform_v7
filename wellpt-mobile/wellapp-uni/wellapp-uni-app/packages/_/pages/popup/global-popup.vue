<template>
  <view :style="theme" id="globalPopup-box">
    <uni-popup
      ref="popup"
      :type="options.type || 'bottom'"
      :borderRadius="options.borderRadius || '16pt  16pt  0pt  0pt'"
      :background-color="options.backgroundColor || '#ffffff'"
      @change="onChange"
      :is-mask-click="options.isMaskClick"
      @maskClick="onMaskClick"
    >
      <view class="popup-container">
        <view class="popop-title" v-if="options.title">
          <view class="left"></view>
          <view class="center">
            <text>{{ options.title }}</text>
          </view>
          <view class="right">
            <uni-w-button type="text" @click="onClose" icon="iconfont icon-ptkj-dacha-xiao"></uni-w-button>
          </view>
        </view>
        <!-- 自定义组件，需要真实存在且全局定义 -->
        <template v-if="options.componentName">
          <RenderDevelopTemplate
            v-if="options.isTemplate"
            :isComponent="options.componentName"
            :options="options.options"
            @close="onClose"
          ></RenderDevelopTemplate>
          <!-- 附件列表弹框内容 -->
          <popup-file-list
            v-else-if="options.componentName == 'popup-file-list'"
            :options="options.options"
            @close="onClose"
          ></popup-file-list>
          <!-- 用户信息-弹框内容 -->
          <popup-user-info
            v-if="options.componentName == 'popup-user-info'"
            :options="options.options"
            @close="onClose"
          ></popup-user-info>
        </template>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { isFunction } from "lodash";
export default {
  name: "ptGlobalPopup",
  props: {
    propOptions: Object,
  },
  data() {
    return {
      eventChannel: null,
      options: {},
    };
  },
  components: {},
  // #ifdef APP-PLUS
  onLoad() {
    this.eventChannel = this.getOpenerEventChannel();
    this.eventChannel.on("ptPopup", (options) => {
      if (options) {
        this.options = options;
      }
      this.$nextTick(() => {
        if (this.$refs.popup) {
          this.$refs.popup.open();
        }
      });
    });
  },
  // #endif
  mounted() {
    if (this.propOptions) {
      this.options = this.propOptions;
      this.$nextTick(() => {
        if (this.$refs.popup) {
          this.$refs.popup.open();
        }
      });
    }
  },
  methods: {
    show(options) {
      if (options) {
        this.options = options;
      }
      this.$nextTick(() => {
        if (this.$refs.popup) {
          this.$refs.popup.open();
        }
      });
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
      this.$refs.popup.close();
      // #endif
    },
    onChange() {
      let _this = this;
      if (isFunction(_this.options.onChange)) {
        _this.options.onChange();
      }
    },
    onMaskClick() {
      let _this = this;
      // #ifdef H5
      if (isFunction(_this.options.onMaskClick)) {
        _this.options.onMaskClick();
      }
      // #endif
      // #ifndef H5
      uni.navigateBack({
        delta: 1,
        success() {
          if (isFunction(_this.options.onMaskClick)) {
            _this.options.onMaskClick();
          }
        },
      });
      // #endif
    },
  },
};
</script>

<style lang="scss">
page {
  background: transparent;
}
.popup-container {
  .popop-title {
    padding: 14px 5rem 8px;
    font-size: var(--w-font-size-base);
    display: flex;
    align-items: center;
    justify-content: space-between;
    position: relative;

    .left {
      position: absolute;
      left: 10px;
    }

    .center {
      flex: 1;
      text-align: center;
      font-size: var(--w-font-size-lg);
      color: $uni-main-color;
      font-weight: bold;
    }

    .right {
      position: absolute;
      right: 10px;
    }
  }
}
</style>
