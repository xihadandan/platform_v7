<template>
  <view class="uni-w-numbox" :class="{ 'has-border': inputBorder, disabled: disabled, 'has-step': stepBtnShow }">
    <view
      @click="_calcValue('minus')"
      class="uni-numbox__minus uni-numbox-btns"
      :style="{ background: btnBackground, width: height + 'px' }"
      v-if="stepBtnShow"
    >
      <text
        class="uni-numbox--text"
        :class="{ 'uni-numbox--disabled': inputValue <= min || disabled }"
        :style="{ color }"
      >
        <w-icon icon="iconfont icon-ptkj-jianhao" :size="12" />
      </text>
    </view>
    <input
      :disabled="disabled"
      @input="_onInput"
      @focus="_onFocus"
      @blur="_onBlur"
      class="uni-numbox__value"
      :type="inputType"
      v-model="showValue"
      :placeholder="placeholder"
      :style="{ background, color, width: widthWithPx, height: height + 'px' }"
      keyboardType="number"
    />
    <view
      @click="_calcValue('plus')"
      class="uni-numbox__plus uni-numbox-btns"
      :style="{ background: btnBackground, width: height + 'px' }"
      v-if="stepBtnShow"
    >
      <text
        class="uni-numbox--text"
        :class="{ 'uni-numbox--disabled': inputValue >= max || disabled }"
        :style="{ color }"
      >
        <w-icon icon="iconfont icon-ptkj-jiahao" :size="12" />
      </text>
    </view>
  </view>
</template>
<script>
/**
 * NumberBox 数字输入框
 * @description 带加减按钮的数字输入框
 * @tutorial https://ext.dcloud.net.cn/plugin?id=31
 * @property {Number} value 输入框当前值
 * @property {Number} min 最小值
 * @property {Number} max 最大值
 * @property {Number} step 每次点击改变的间隔大小
 * @property {String} background 背景色
 * @property {String} color 字体颜色（前景色）
 * @property {Number} width 输入框宽度(单位:px)
 * @property {Boolean} disabled = [true|false] 是否为禁用状态
 * @event {Function} change 输入框值改变时触发的事件，参数为输入框当前的 value
 * @event {Function} focus 输入框聚焦时触发的事件，参数为 event 对象
 * @event {Function} blur 输入框失焦时触发的事件，参数为 event 对象
 */

export default {
  name: "UniNumberBox",
  emits: ["change", "input", "update:modelValue", "blur", "focus"],
  props: {
    value: {
      type: [Number, String],
      default: "",
    },
    modelValue: {
      type: [Number, String],
      default: "",
    },
    min: {
      type: Number,
      default: 0,
    },
    max: {
      type: Number,
      default: 100,
    },
    step: {
      type: Number,
      default: 1,
    },
    background: {
      type: String,
      default: "#ffffff",
    },
    btnBackground: {
      type: String,
      default: "",
    },
    color: {
      type: String,
      default: "",
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    width: {
      type: [Number, String],
      default: "",
    },
    height: {
      type: Number,
      default: 32,
    },
    // 显示加减号
    stepBtnShow: {
      type: Boolean,
      default: true,
    },
    // 显示边框
    inputBorder: {
      type: Boolean,
      default: false,
    },
    placeholder: {
      type: String,
      default: "",
    },
    // 小数位
    precision: {
      type: Number,
    },
    // 格式化显示，输入框type会改成text
    formatNumber: {
      type: Boolean,
      default: false,
    },
    //指定输入框展示值的格式
    formatter: {
      type: Function,
    },
    //指定从 formatter 里转换回数字的方式，和 formatter 搭配使用
    parser: {
      type: Function,
    },
  },
  data() {
    // let inputType = "number";
    // // 有格式化的，默认使用文本键盘
    // if (this.formatNumber) {
    let inputType = "text";
    let inputValue = "";
    // }
    if (this.value || this.value === 0 || this.value === "0") {
      inputValue = this.value;
    } else if (this.modelValue || this.modelValue === 0 || this.modelValue === "0") {
      inputValue = this.modelValue;
    }
    return {
      inputType,
      valueUpdate: false,
      inputValue,
    };
  },
  watch: {
    modelValue(val) {
      this.inputValue = val;
    },
    value(val) {
      this.inputValue = val;
    },
  },
  computed: {
    widthWithPx() {
      if (this.width) {
        if (typeof this.width == "string") {
          return this.width;
        }
        return this.width + "px";
      } else {
        if (this.stepBtnShow) {
          return `calc(100% - ${this.height * 2}px)`;
        }
        return "100%";
      }
    },
    showValue: {
      get() {
        let valueUpdate = this.valueUpdate;
        if (this.formatNumber && typeof this.formatter == "function") {
          let formatValue = this.formatter(this.inputValue);
          return formatValue;
        }
        // 将数字转换为字符串，并移除非数字字符
        return this.inputValue;
      },
      set(val) {
        console.log(val);
      },
    },
  },
  created() {},
  methods: {
    _calcValue(type) {
      if (this.disabled) {
        return;
      }
      const scale = this._getDecimalScale();
      let value = this.inputValue * scale;
      let step = this.step * scale;
      if (type === "minus") {
        value -= step;
        if (value < this.min * scale) {
          return;
        }
        if (value > this.max * scale) {
          value = this.max * scale;
        }
      }

      if (type === "plus") {
        value += step;
        if (value > this.max * scale) {
          return;
        }
        if (value < this.min * scale) {
          value = this.min * scale;
        }
      }

      this.$set(this, "inputValue", (value / scale).toFixed(String(scale).length - 1));
      this.valueEmit();
    },
    _getDecimalScale() {
      let scale = 1;
      if (this.precision || (!this.precision && this.precision === 0)) {
        scale = Math.pow(10, this.precision);
      }
      // 浮点型
      else if (~~this.step !== this.step) {
        scale = Math.pow(10, String(this.step).split(".")[1].length);
      }
      return scale;
    },
    _onBlur(event) {
      this.$emit("blur", event);
      let value = event.target.value.trim();
      if (this.formatNumber && typeof this.parser == "function") {
        value = this.parser(value);
      }
      if (isNaN(value)) {
        this.$set(this, "inputValue", value);
        this.valueEmit();
        return;
      }
      value = value === "" || value === null || value === undefined ? null : Number(value);
      if (value || value === 0) {
        if (value > this.max) {
          value = this.max;
        } else if (value < this.min) {
          value = this.min;
        }
        const scale = this._getDecimalScale();
        this.$set(this, "inputValue", value.toFixed(String(scale).length - 1) || "");
      } else {
        this.$set(this, "inputValue", value);
      }
      this.valueEmit();
    },
    _onFocus(event) {
      this.$emit("focus", event);
    },
    _onInput(event) {
      let value = event.target.value.trim();
      if (this.formatNumber && typeof this.parser == "function") {
        value = this.parser(value);
      }
      this.$set(this, "inputValue", value || null);
      if (this.precision || (!this.precision && this.precision === 0)) {
        if (this.precision) {
          const regex = new RegExp("/^(\d+)?(\.\d{0," + this.precision + "})?$/");
          // 如果不匹配，则直接返回原来的值
          // 如果输入值不符合正则表达式，则设置为上一个合法的值
          if (!regex.test(value)) {
            const regex1 = new RegExp("(\\.\\d{" + this.precision + "})(.*)");
            this.$set(this, "inputValue", value.replace(regex1, "$1"));
          } else {
            this.$set(this, "inputValue", value);
          }
        }
      }
      this.valueEmit();
    },
    valueEmit() {
      let val = this.inputValue;
      let value = val === "" ? null : val;
      if (val && typeof val === "string") {
        if (isNaN(val)) {
          // const regex1 = new RegExp(/^-?\d+(\.\d+)?$/);
          // value = value.replace(regex1, "$1");
        } else {
          value = Number(val);
        }
      }
      // TODO vue2 兼容
      this.$emit("input", value);
      // TODO vue3 兼容
      this.$emit("update:modelValue", value);
      this.$emit("change", value);

      this.valueUpdate = !this.valueUpdate;
    },
  },
};
</script>
<style lang="scss" scoped>
$uni-border-1: #f2f2f2;
$box-height: 32px;
$bg: #fafafa;
$br: 4px;
$color: var(--w-text-color-mobile);

.uni-w-numbox {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: row;
  width: 100%;
  border-radius: 4px;

  &.has-border {
    .uni-numbox__minus {
      border-right: 1px solid $uni-border-1;
    }
    .uni-numbox__plus {
      border-left: 1px solid $uni-border-1;
    }
    .uni-numbox__value {
      border: 1px solid $uni-border-1;
      padding: 0 8px;
      border-radius: $br;
    }
  }

  &.has-step {
    .uni-numbox__value {
      margin: 0 8px;
    }
  }

  &.disabled {
    .uni-numbox__value {
      background-color: $bg !important;
    }
    .uni-numbox--text {
      color: var(--text-color-disable);
    }
    .uni-numbox-btns {
      background-color: $bg;
    }
  }

  .uni-numbox-btns {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    align-items: center;
    justify-content: center;
    width: $box-height;
    text-align: center;
    background-color: #fafafa;
    border-radius: $br;
    /* #ifdef H5 */
    cursor: pointer;
    /* #endif */
  }

  .uni-numbox__value {
    margin: 0 2px;
    background-color: $bg;
    min-width: 40px;
    height: $box-height;
    // text-align: center;
    font-size: 14px;
    border-width: 0;
    color: $color;
    border-radius: 4px;
    outline: none !important;

    &::-webkit-inner-spin-button,
    &::-webkit-outer-spin-button {
      // 隐藏默认按钮
      -webkit-appearance: none;
    }
    &::placeholder {
      color: var(--text-color-placeholder);
      font-size: var(--w-font-size-base);
    }
  }

  .uni-numbox__minus {
    // border-top-left-radius: $br;
    // border-bottom-left-radius: $br;
  }

  .uni-numbox__plus {
    // border-top-right-radius: $br;
    // border-bottom-right-radius: $br;
  }

  .uni-numbox--text {
    // fix nvue
    line-height: 20px;
    margin-bottom: 2px;
    font-size: 20px;
    font-weight: 300;
    color: $color;
  }

  .uni-numbox .uni-numbox--disabled {
    color: var(--text-color-disable) !important;
    /* #ifdef H5 */
    cursor: not-allowed;
    /* #endif */
  }
}
</style>
