<template>
  <view :class="['uni-table-checkbox', radio ? 'radio' : '']" @click.stop="selected">
    <view v-if="!indeterminate" class="checkbox__inner" :class="{ 'is-checked': isChecked, 'is-disable': isDisabled }">
      <view class="checkbox__inner-icon"></view>
    </view>
    <view v-else class="checkbox__inner checkbox--indeterminate">
      <view class="checkbox__inner-icon"></view>
    </view>
  </view>
</template>

<script>
export default {
  name: "TableCheckbox",
  emits: ["checkboxSelected"],
  props: {
    radio: {
      type: Boolean,
      default: false,
    },
    indeterminate: {
      type: Boolean,
      default: false,
    },
    checked: {
      type: [Boolean, String],
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    index: {
      type: Number,
      default: -1,
    },
    cellData: {
      type: Object,
      default() {
        return {};
      },
    },
  },
  watch: {
    checked(newVal) {
      if (typeof this.checked === "boolean") {
        this.isChecked = newVal;
      } else {
        this.isChecked = true;
      }
    },
    indeterminate(newVal) {
      this.isIndeterminate = newVal;
    },
  },
  data() {
    return {
      isChecked: false,
      isDisabled: false,
      isIndeterminate: false,
    };
  },
  created() {
    if (typeof this.checked === "boolean") {
      this.isChecked = this.checked;
    }
    this.isDisabled = this.disabled;
  },
  methods: {
    selected() {
      if (this.isDisabled) return;
      this.isIndeterminate = false;
      this.isChecked = !this.isChecked;
      this.$emit("checkboxSelected", {
        checked: this.isChecked,
        data: this.cellData,
      });
    },
  },
};
</script>

<style lang="scss">
$uni-primary: var(--w-primary-color);
$border-color: var(--w-border-color-mobile);
$disable: 0.4;

.uni-table-checkbox {
  --uni-table-checkbox-width: 16px;

  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  position: relative;
  margin: 5px 0;
  cursor: pointer;

  &.radio {
    .checkbox__inner {
      border-radius: 8px;
    }
  }

  // 多选样式
  .checkbox__inner {
    /* #ifndef APP-NVUE */
    flex-shrink: 0;
    box-sizing: border-box;
    /* #endif */
    position: relative;
    width: var(--uni-table-checkbox-width);
    height: var(--uni-table-checkbox-width);
    border: 1px solid $border-color;
    border-radius: 4px;
    background-color: #fff;
    z-index: 1;

    .checkbox__inner-icon {
      position: absolute;
      /* #ifdef APP-NVUE */
      top: calc(var(--uni-table-checkbox-width) * 0.2 / 2); //2px;
      /* #endif */
      /* #ifndef APP-NVUE */
      top: calc(var(--uni-table-checkbox-width) * 0.2 / 2); //2px;
      /* #endif */
      left: calc(var(--uni-table-checkbox-width) * 0.6 / 2); //5px;
      height: calc(var(--uni-table-checkbox-width) * 0.5 - 1px); //7px;
      width: calc(var(--uni-table-checkbox-width) * 0.3 - 1px); //3px;
      border: 2px solid #fff;
      border-left: 0;
      border-top: 0;
      opacity: 0;
      transform-origin: center;
      transform: rotate(45deg);
      box-sizing: content-box;
    }

    &.checkbox--indeterminate {
      border-color: $uni-primary;
      background-color: $uni-primary;

      .checkbox__inner-icon {
        position: absolute;
        opacity: 1;
        transform: rotate(0deg);
        height: 2px;
        top: 0;
        bottom: 0;
        margin: auto;
        left: 0px;
        right: 0px;
        bottom: 0;
        width: auto;
        border: none;
        border-radius: 2px;
        transform: scale(0.5);
        background-color: #fff;
      }
    }
    &:hover {
      border-color: $uni-primary;
    }
    // 禁用
    &.is-disable {
      /* #ifdef H5 */
      cursor: not-allowed;
      /* #endif */
      background-color: #f2f6fc;
      border-color: $border-color;
    }

    // 选中
    &.is-checked {
      border-color: $uni-primary;
      background-color: $uni-primary;

      .checkbox__inner-icon {
        opacity: 1;
        transform: rotate(45deg);
      }

      // 选中禁用
      &.is-disable {
        opacity: $disable;
      }
    }
  }
}
</style>
