<template>
  <view class="uni-w-color-picker" :class="{ 'has-border': inputBorder, disabled: disabled }">
    <slot>
      <view
        :class="['uni-w-color-picker__container flex f_y_c', 'uni-w-color-picker__container-' + size]"
        :style="{ width: containerWidth }"
        @tap="openColorPicker"
      >
        <view
          :class="['uni-w-color-picker__block', 'uni-w-color-picker__block-' + size]"
          :style="{ background: valueCom }"
        ></view>
        <view v-if="showText && valueCom" class="uni-w-color-picker__text">
          {{ valueCom }}
        </view>
      </view>
    </slot>
    <ColorPicker
      v-model="showColorPopup"
      :hexcolor="valueCom"
      :type="type"
      :spareColor="spareColor"
      @confirm="changeColorinput"
    >
      <view slot="bottom"><slot name="picker-bottom"></slot></view>
    </ColorPicker>
  </view>
</template>

<script>
import ColorPicker from "./color-picker.vue";
export default {
  emits: ["update:modelValue", "change", "confirm"],
  props: {
    value: String | Object, // #fffff  or { h: 150, s: 0.66, v: 0.30 } or { r: 255, g: 0, b: 0 },
    modelValue: String | Object, // #fffff  or { h: 150, s: 0.66, v: 0.30 } or { r: 255, g: 0, b: 0 },
    spareColor: {
      type: Array,
      default() {
        return [];
      },
    },
    size: {
      type: String,
      default: "defalut",
    },
    showText: {
      type: Boolean,
      default: false,
    },
    type: {
      type: String,
      default: "Sketch", //Twitter:预设颜色,Chrome:色盘,Sketch:组合面板
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    inputBorder: {
      type: Boolean,
      default: false,
    },
  },
  components: { ColorPicker },
  computed: {
    valueCom() {
      // #ifndef VUE3
      return this.value;
      // #endif

      // #ifdef VUE3
      return this.modelValue;
      // #endif
    },
  },
  mounted() {
    // 组件渲染完成时，检查value是否为true，如果是，弹出popup
    if (this.valueCom) {
      this.open();
    }
  },
  data() {
    let containerWidth = this.showText ? "" : "fit-content";
    return {
      showColorPopup: false,
      containerWidth,
    };
  },
  created() {},
  methods: {
    changeColorinput(evt) {
      let result = evt.hex;

      // #ifndef VUE3
      this.$emit("input", result);
      // #endif

      // #ifdef VUE3
      this.$emit("update:modelValue", result);
      // #endif
    },
    openColorPicker() {
      if (!this.disabled) {
        this.showColorPopup = true;
      }
    },
  },
  watch: {},
};
</script>

<style lang="scss" scoped>
.uni-w-color-picker {
  width: 100%;

  &.has-border {
    .uni-w-color-picker__container {
      border: 1px solid var(--w-border-color-base);
      border-radius: var(--w-border-radius-2);

      .uni-w-color-picker__block {
        border-color: transparent;
        margin-left: var(--w-margin-3xs);
      }
    }
  }

  &.disabled {
    .uni-w-color-picker__text {
      color: var(--text-color-disable);
    }
  }
}
.uni-w-color-picker__container {
  border: none;
  font-size: var(--w-font-size-base);
  line-height: 32px;
  height: 32px;
  &-lg {
    font-size: var(--w-font-size-lg);
    line-height: 40px;
    height: 40px;
  }
  &-sm {
    line-height: 24px;
    height: 24px;
  }

  .uni-w-color-picker__text {
    margin-right: var(--w-margin-2xs);
  }
}
// 色块样式
.uni-w-color-picker__block {
  border-radius: var(--w-border-radius-2);
  display: inline-block;
  cursor: pointer;
  width: 22px;
  height: 22px;
  margin: var(--w-margin-3xs);
  margin-left: 0;
  border: 1px solid var(--w-border-color-base);

  &-sm {
    width: 14px;
    height: 14px;
  }
  &-lg {
    width: 30px;
    height: 30px;
  }
}
</style>
