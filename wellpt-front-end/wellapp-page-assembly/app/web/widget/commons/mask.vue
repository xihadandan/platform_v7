<!-- 遮罩层组件 -->
<template>
  <div class="mask" v-if="showMask" :class="{ pointer: closable }" :style="vStyle" @click="onClick"></div>
</template>
<script>
export default {
  name: 'MaskComponent',
  props: {
    value: {
      type: Boolean,
      default: false
    },
    zIndex: Number,
    bgColor: String,
    closable: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    vStyle() {
      let style = {};
      if (this.zIndex) {
        style.zIndex = this.zIndex;
      }
      if (this.bgColor) {
        style.backgroundColor = this.bgColor;
      }
      return style;
    }
  },
  data() {
    return {
      showMask: this.value
    };
  },
  methods: {
    showOverlay() {
      this.showMask = true;
      this.$emit('input', this.showMask);
    },
    hideOverlay() {
      this.showMask = false;
      this.$emit('input', this.showMask);
    },
    onClick() {
      if (this.closable) {
        this.$emit('onClose');
        this.hideOverlay();
      }
    }
  },
  watch: {
    value(v) {
      this.showMask = v;
    }
  }
};
</script>
<style scoped>
.mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5); /* 半透明遮罩 */
  z-index: 1000; /* 确保遮罩层在其他内容之上 */

  &.pointer {
    cursor: pointer;
  }
}
</style>
