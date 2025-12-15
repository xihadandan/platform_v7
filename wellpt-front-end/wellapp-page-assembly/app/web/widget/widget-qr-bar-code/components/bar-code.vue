<template>
  <canvas ref="canvas"></canvas>
</template>

<script>
import BWIPJS from 'bwip-js'; // 安装：npm install bwip-js

export default {
  name: 'Barcode',
  props: {
    type: { type: String, default: 'code128' }, // 'code128' | 'pdf417'
    text: { type: String, required: true }, // 条码内容
    scale: { type: Number, default: 6 }, // 缩放倍数
    height: { type: Number, default: 10 }, // 行高（1D条码）
    heightPx: { type: Number, default: 100 },
    includetext: { type: Boolean, default: false } // 是否显示文字
  },
  methods: {
    renderBarcode() {
      try {
        // BWIPJS.load();
        BWIPJS.render(
          {
            bcid: this.type, // code128 / pdf417
            text: this.text,
            scale: this.scale,
            height: this.height,
            includetext: this.includetext
          },
          this.$refs.canvas
        );
      } catch (err) {
        console.error('Barcode render error:', err);
      }
    },
    drawBarcode2() {
      if (!this.$refs.canvas) return;
      try {
        // 使用 toCanvas API（不会报 scale 错误）
        BWIPJS.toCanvas(this.$refs.canvas, {
          bcid: this.type, // 'code128' | 'pdf417'
          text: this.text, // 条码内容
          scale: this.scale, // 缩放倍数
          height: this.height, // 条码高度（1D 条码）
          includetext: this.includetext
        });
      } catch (err) {
        console.error('Barcode render error:', err);
      }
    },
    drawBarcode() {
      // demo https://bwip-js.metafloor.com/demo/demo.html
      const canvas = this.$refs.canvas;
      if (!canvas) return;

      const scale = this.scale;
      const targetPx = this.heightPx;
      const height = targetPx / scale; // 反算逻辑高度

      try {
        let opts = {
          bcid: this.type,
          text: this.text,
          scale,
          height // 按目标像素计算 height
          // includetext: this.includetext
        };

        if (this.includetext) {
          opts.alttext = this.text;
        }
        // 1. 先渲染
        BWIPJS.toCanvas(canvas, opts);

        // 2. 重新创建一个目标大小的canvas，并拷贝渲染内容
        if (this.heightPx > 0) {
          const tmp = document.createElement('canvas');
          const ctx = tmp.getContext('2d');

          tmp.width = canvas.width; // 原始宽度
          tmp.height = canvas.height; // 原始高度
          ctx.drawImage(canvas, 0, 0); // 备份原始图

          // 覆盖canvas为目标高度
          const ctx2 = canvas.getContext('2d');
          const targetCanvasWidth = tmp.width * (targetPx / tmp.height);
          canvas.width = targetCanvasWidth;
          canvas.height = targetPx;

          // 按比例缩放绘制
          ctx2.drawImage(tmp, 0, 0, canvas.width, canvas.height);
        }
      } catch (err) {
        console.error('Barcode render error:', err);
      }
    }
  },
  watch: {
    text(newVal, oldVal) {
      this.drawBarcode();
    },
    type() {
      this.drawBarcode();
    },
    heightPx() {
      this.drawBarcode();
    }
  },
  mounted() {
    // this.renderBarcode();
    this.drawBarcode();
  }
};
</script>

<style scoped>
canvas {
  max-width: 100%;
}
</style>
