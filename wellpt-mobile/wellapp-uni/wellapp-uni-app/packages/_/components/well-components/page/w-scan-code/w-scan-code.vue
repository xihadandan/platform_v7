<template>
  <view class="w-scan-code">
    <view
      :style="{
        textAlign: widget.configuration.align,
      }"
    >
      <uni-w-button
        :type="configuration.type"
        :block="configuration.block"
        :size="configuration.size"
        :icon="configuration.icon"
        :text="$t(widget.id, widget.title)"
        :textHidden="configuration.textHidden"
        @click="handleScan"
      />
    </view>
    <full-screen-scanner ref="scanner" @success="onScanSuccess" @fail="onScanFail" />

    <uni-popup ref="scanImageTipDialog" type="dialog">
      <uni-popup-dialog
        type="error"
        cancelText="关闭"
        confirmText="确定"
        :content="failMsg"
        @confirm="confirmScanImage"
      ></uni-popup-dialog>
    </uni-popup>

    <uni-popup ref="resultLinkDialog" type="dialog">
      <uni-popup-dialog title="是否跳转" @confirm="confirmRedirectLink">
        <view style="max-height: 90px; overflow-y: auto; word-wrap: break-word; word-break: break-all">{{
          result
        }}</view>
      </uni-popup-dialog>
    </uni-popup>

    <uni-popup
      ref="resultCustomPopup"
      class="scan-result-custom-popup"
      type="bottom"
      background-color="#ffffff"
      borderRadius="16px 16px 0 0"
    >
      <view class="result-custom-container">
        <view class="_title">扫描到以下内容</view>
        <view class="_result-view">{{ result }}</view>
        <view class="_tips">
          <text class="_tips-text">如需使用，可通过复制操作获取内容</text>
          <uni-w-button type="text" icon="iconfont icon-ptkj-fuzhi" @click="copyScanResult" />
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import mixin from "../page-widget-mixin";
import QrcodeScannerDefault from "./qrcode-scanner-default.vue";
import FullScreenScanner from "./full-screen-scanner.vue";

export default {
  name: "w-scan-code",
  mixins: [mixin],
  components: {
    FullScreenScanner,
    QrcodeScannerDefault,
  },
  data() {
    return {
      result: "",
      failMsg: "",
    };
  },
  computed: {
    configuration() {
      return this.widget.configuration;
    },
  },
  methods: {
    onScan(decodedText, decodedResult) {
      console.log(decodedText, decodedResult);
    },
    // 点击扫描
    handleScan() {
      // #ifdef APP-PLUS
      uni.scanCode({
        scanType: ["barCode", "qrCode", "datamatrix", "pdf417"],
        success: ({ result }) => {
          this.onScanSuccess(result);
        },
        fail: (res) => {
          console.log("fail", res);
        },
      });
      // #endif
      // #ifdef H5
      // this.$refs.scanner.visible = true;
      // this.$refs.scanner.cameraAvailable = false;

      // this.openScanner();
      this.startScan();
      // #endif
    },
    // 打开扫描仪
    openScanner() {
      this.$refs.scanner.open();
    },
    // 开始扫描
    startScan() {
      this.$refs.scanner.startScan();
    },
    // 扫描成功
    onScanSuccess(code) {
      this.result = code;
      // uni.showToast({ title: "扫码成功", icon: "success" });
      if (code.indexOf("http") === 0) {
        this.$refs.resultLinkDialog.open();
      } else {
        this.$refs.resultCustomPopup.open();
      }
    },
    // 扫描失败
    onScanFail(msg, type) {
      if (type === "failFromStart") {
        this.failMsg = msg + "，是否通过上传图片识别";
        this.$refs.scanImageTipDialog.open();
      } else {
        uni.showToast({ title: msg, icon: "none" });
      }
    },
    // 确认跳转链接
    confirmRedirectLink() {
      let url = this.result;
      if (url.indexOf("#") > 0) {
        url = url.split("#")[1];
      }
      uni.redirectTo({
        url,
      });
    },
    // 确认扫描图片
    confirmScanImage() {
      this.$refs.scanner.selectImage();
    },
    // 复制扫描结果
    copyScanResult() {
      uni.setClipboardData({
        data: this.result,
        showToast: false,
        success: (args) => {
          uni.showToast({
            title: this.$t("WidgetFormFileUpload.message.copyDone", "已复制"),
            icon: "success",
            duration: 2000,
          });
        },
      });
    },
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    // 请求相机权限
    requestCameraPermissions() {},
    // 停止扫描
    handleStopScanning() {},
  },
};
</script>

<style lang="scss">
.scan-result-custom-popup {
  z-index: 1000;
}
.result-custom-container {
  height: 70vh;
  // display: flex;
  // flex-direction: column;
  // padding: 20px;
  > ._title {
    height: 50px;
    line-height: 50px;
    text-align: center;
  }
  > ._result-view {
    min-height: 50px;
    border-radius: 5px;
    border: 1px #f4f5f9;
    margin: 10px;
    background-color: #fff;
    box-shadow: 0 1px 4px 3px rgba(0, 0, 0, 0.1);
    padding: 20px;
    word-wrap: break-word;
    word-break: normal;
    box-sizing: border-box;
  }
  > ._tips {
    height: 40px;
    line-height: 40px;
    text-align: center;
    > ._tips-text {
      vertical-align: middle;
    }
    > .w-button-text {
      --w-button-font-color: var(--w-primary-color);
    }
  }
}
</style>
