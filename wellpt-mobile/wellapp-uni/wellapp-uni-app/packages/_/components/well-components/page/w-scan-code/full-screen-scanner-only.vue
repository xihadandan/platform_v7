<template>
  <view>
    <view v-if="visible" class="scanner-overlay">
      <!-- 扫描框容器 -->
      <view class="scanner-box">
        <div id="qr-reader"></div>
        <!-- <view class="scanner-line"></view> -->
      </view>

      <!-- 关闭按钮 -->
      <view class="close-btn" @click="close">×</view>
    </view>
  </view>
</template>

<script>
import { Html5Qrcode } from "html5-qrcode";

export default {
  name: "FullScreenScanner",
  data() {
    return {
      visible: false,
      html5QrCode: null,
      isScanning: false,
      cameraId: "",
    };
  },
  methods: {
    // 外部调用：打开扫码
    async open() {
      this.visible = true;
      this.$nextTick(() => this.startScan());
    },

    // 外部调用：关闭扫码
    async close() {
      await this.stopScan();
      this.visible = false;
    },

    // 启动扫码
    async startScan() {
      try {
        const devices = await Html5Qrcode.getCameras();
        if (!devices.length) {
          this.$emit("fail", "未检测到摄像头");
          this.close();
          return;
        }

        const backCam = devices.find((d) => d.label.toLowerCase().includes("back"));
        this.cameraId = backCam ? backCam.id : devices[0].id;

        this.html5QrCode = new Html5Qrcode("qr-reader");

        await this.html5QrCode.start(
          backCam ? this.cameraId : { facingMode: "environment" },
          {
            fps: 10,
            qrbox: { width: 280, height: 280 }, // 强制正方形扫码区域
          },
          (decodedText) => this.handleSuccess(decodedText),
          () => {}
        );

        this.isScanning = true;

        // 确保 canvas/video 强制裁剪为正方形
        // setTimeout(() => this.fixCanvasStyle(), 100);
        this.fixCanvasStyle();
      } catch (err) {
        this.$emit("fail", "摄像头启动失败: " + err);
        this.close();
      }
    },

    // 停止扫码
    async stopScan() {
      if (this.html5QrCode && this.isScanning) {
        await this.html5QrCode.stop();
        this.isScanning = false;
      }
    },

    // 扫码成功
    handleSuccess(result) {
      this.$emit("success", result);
      this.close();
    },

    // 修正 canvas/video 样式，确保是正方形
    fixCanvasStyle() {
      const container = this.$el.querySelector("#qr-reader");
      const canvas = container?.querySelector("canvas");
      const video = container?.querySelector("video");

      [canvas, video].forEach((el) => {
        if (el) {
          el.style.width = "280px";
          el.style.height = "280px";
          el.style.objectFit = "cover";
          el.style.position = "absolute";
          el.style.top = "0";
          el.style.left = "0";
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
/* 全屏黑色背景 */
.scanner-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.9);
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

/* 扫描框容器 */
.scanner-box {
  width: 280px;
  height: 280px;
  // border: 2px solid #00ff00;
  position: relative;
  overflow: hidden;
}

#qr-reader {
  width: 280px !important;
  height: 280px !important;
  position: relative;
  overflow: hidden;
}

/* 扫描线动画 */
.scanner-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: #00ff00;
  animation: scanMove 2s linear infinite;
}
@keyframes scanMove {
  0% {
    top: 0;
  }
  100% {
    top: 100%;
  }
}

/* 关闭按钮 */
.close-btn {
  position: absolute;
  top: 40px;
  right: 30px;
  font-size: 40px;
  color: white;
  cursor: pointer;
}
</style>
