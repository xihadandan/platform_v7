<template>
  <view v-if="visible" class="global-toast" :class="'pos-' + position">
    <image v-if="iconSrc" :src="iconSrc" class="toast-icon" />
    <text class="toast-text">{{ message }}</text>
  </view>
</template>

<script>
import { isFunction } from "lodash";
export default {
  name: "PtToast",
  data() {
    return {
      eventChannel: null,
      visible: false,
      message: '',
      iconSrc: '',
      position: 'center',
      timer: null,
      lastToastTime: 0, // 上次显示时间
      debounceGap: 1000, // 防抖间隔（单位 ms）
      options: {},
    }
  },
  onLoad() {
    this.eventChannel = this.getOpenerEventChannel();
    this.eventChannel.on("ptToast", (options) => {
      this.handleToast(options)
    })
  },
  created() {
    // 监听全局 toast 事件
    uni.$on('GToast', this.handleToast);
  },
  beforeDestroy() {
    uni.$off('GToast', this.handleToast);
  },
  methods: {
    handleToast(options) {
      this.options = options;
      const now = Date.now();
      const force = options.force || false;

      if (!force && now - this.lastToastTime < this.debounceGap) return;

      this.lastToastTime = now;
      this.show(options);
    },

    show({ title = '', icon = 'none', duration = 2000, position = 'center',onCLose = null}) {
      console.log(title)
      this.message = title;
      this.position = position;

      const icons = {
        success: '/static/success.png',
        loading: '/static/loading.gif',
        error: '/static/error.png'
      };

      this.iconSrc = icons[icon] || '';

      this.visible = true;
      clearTimeout(this.timer);
      this.timer = setTimeout(() => {
        this.visible = false;
        this.onClose(onCLose)
      }, duration);
    },
    onClose(_onCLose) {
      let _this = this;
      // #ifndef H5
      uni.navigateBack({
        delta: 1,
        success() {
          if (isFunction(_this.options.onClose)) {
            _this.options.onClose();
          }
          if(isFunction(_onCLose)){
            _onCLose();
          }
        },
      });
      // #endif

      // #ifdef H5
      if (isFunction(_this.options.onClose)) {
        _this.options.onClose();
      }
        if(isFunction(_onCLose)){
          _onCLose();
        }
      // #endif
    },
  }
}
</script>

<style>
page {
  background: transparent;
}
.global-toast {
  position: fixed;
  left: 50%;
  transform: translateX(-50%);
  max-width: 70%;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 30rpx;
  border-radius: 16rpx;
  text-align: center;
  z-index: 9999;
}
.pos-top {
  top: 200rpx;
}
.pos-center {
  top: 50%;
  transform: translate(-50%, -50%);
}
.pos-bottom {
  bottom: 150rpx;
}

.toast-icon {
  width: 48rpx;
  height: 48rpx;
  margin-bottom: 16rpx;
}

.toast-text {
  font-size: 28rpx;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
