<template>
  <view v-show="visible" class="scanner-overlay">
    <!-- 摄像头扫描区域 -->
    <view v-show="cameraAvailable" class="scanner-box">
      <div id="qr-reader"></div>
      <!-- <view class="scanner-line"></view> -->
    </view>

    <!-- 如果摄像头不可用，显示上传按钮 -->
    <view v-show="!cameraAvailable" class="upload-box">
      <text class="upload-tip">摄像头不可用，请选择二维码图片</text>
      <button class="upload-btn" @click="selectImage">选择二维码图片</button>
    </view>

    <!-- 关闭按钮 -->
    <view class="close-btn" @click="close">×</view>
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
      cameraAvailable: true,
    };
  },
  methods: {
    /** 外部调用：打开扫码 */
    async open() {
      this.visible = true;
      this.cameraAvailable = true;
      this.$nextTick(() => this.startScan());
    },

    /** 外部调用：关闭扫码 */
    async close() {
      await this.stopScan();
      this.visible = false;
    },

    /** 启动摄像头扫码 */
    async startScan() {
      try {
        const devices = await Html5Qrcode.getCameras();
        if (!devices.length) throw new Error("未检测到摄像头");

        const backCam = devices.find((d) => d.label.toLowerCase().includes("back"));
        this.cameraId = backCam ? backCam.id : devices[0].id;

        this.html5QrCode = new Html5Qrcode("qr-reader");
        await this.html5QrCode.start(
          this.cameraId,
          { fps: 10, qrbox: { width: 280, height: 280 } },
          (decodedText) => this.handleSuccess(decodedText),
          () => {}
        );

        this.visible = true;

        this.isScanning = true;
        // setTimeout(() => this.fixCanvasStyle(), 300);
        this.fixCanvasStyle();
      } catch (err) {
        console.warn("摄像头启动失败:", err);
        this.cameraAvailable = false; // 切换到上传模式
        if (typeof err === "string" && err.indexOf("https or localhost") !== -1) {
          err = "仅在https或localhost等安全上下文中支持摄像头访问";
        }
        this.$emit("fail", "摄像头启动失败: " + err, "failFromStart");
      }
    },

    /** 停止摄像头 */
    async stopScan() {
      if (this.html5QrCode && this.isScanning) {
        await this.html5QrCode.stop();
        this.isScanning = false;
      }
    },

    /** 处理扫码成功 */
    handleSuccess(result) {
      this.$emit("success", result);
      this.close();
    },

    /** 修复 video/canvas 样式 */
    fixCanvasStyle() {
      const container = this.$el.querySelector("#qr-reader");
      const elements = container?.querySelectorAll("canvas, video") || [];
      elements.forEach((el) => {
        el.style.width = "280px";
        el.style.height = "280px";
        el.style.objectFit = "cover";
        el.style.position = "absolute";
        el.style.top = "0";
        el.style.left = "0";
      });
    },

    /** 从本地相册选择二维码图片并解码 */
    async selectImage() {
      // H5 版本：使用 input 触发本地相册
      const input = document.createElement("input");
      input.type = "file";
      input.accept = "image/*";
      input.style.display = "none";
      document.body.appendChild(input);
      input.click();

      input.onchange = async (e) => {
        const file = e.target.files[0];
        document.body.removeChild(input);
        if (!file) return;

        try {
          if (!this.html5QrCode) {
            this.html5QrCode = new Html5Qrcode("qr-reader");
          }
          const result = await this.html5QrCode.scanFile(file, true);
          this.handleSuccess(result);
        } catch (err) {
          console.error("图片解码失败:", err);
          this.$emit("fail", "图片解析失败");
        }
      };
    },
  },
};
</script>

<style lang="scss" scoped>
/* 全屏遮罩 */
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

/* 摄像头容器 */
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

/* 上传模式 */
.upload-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
}
.upload-tip {
  margin-bottom: 10px;
  font-size: 16px;
}
.upload-btn {
  background: #00aa00;
  color: white;
  padding: 8px 16px;
  border-radius: 5px;
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
