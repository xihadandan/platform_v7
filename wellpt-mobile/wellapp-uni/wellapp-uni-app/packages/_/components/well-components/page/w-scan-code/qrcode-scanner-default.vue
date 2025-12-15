<template>
  <div id="reader"></div>
</template>

<script>
import { Html5QrcodeScanner } from "html5-qrcode";

export default {
  name: "QrcodeScannerDefault",
  props: {
    qrbox: {
      type: Number,
      default: 250,
    },
    fps: {
      type: Number,
      default: 10,
    },
  },
  mounted() {
    const config = {
      fps: this.fps,
      qrbox: this.qrbox,
      // supportedScanTypes: 1,
    };
    const html5QrcodeScanner = new Html5QrcodeScanner("reader", config);
    html5QrcodeScanner.render(this.onScanSuccess);
  },
  methods: {
    onScanSuccess(decodedText, decodedResult) {
      this.$emit("result", decodedText, decodedResult);
    },
  },
};
</script>
