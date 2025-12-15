<template>
  <view class="uni-w-switch" :disabled="disabled" :class="switchClass" @click="_onClick">
    <view class="uni-switch-wrapper">
      <view
        v-show="type === 'switch'"
        :class="[switchChecked ? 'uni-switch-input-checked' : '']"
        :style="{ backgroundColor: switchChecked ? color : '#DFDFDF', borderColor: switchChecked ? color : '#DFDFDF' }"
        class="uni-switch-input"
      >
        <view class="switch-label">
          <slot v-if="checked" name="checkedChildren"></slot>
          <slot v-else name="unCheckedChildren"></slot>
        </view>
      </view>
      <view
        v-show="type === 'checkbox'"
        :class="[switchChecked ? 'uni-checkbox-input-checked' : '']"
        :style="{ color: color }"
        class="uni-checkbox-input"
      />
    </view>
  </view>
</template>
<script>
import emitter from "@dcloudio/uni-h5/src/core/view/mixins/emitter.js";
export default {
  name: "UniWSwitch",
  mixins: [emitter],
  model: {
    prop: "checked",
  },
  props: {
    name: {
      type: String,
      default: "",
    },
    checked: {
      type: [Boolean, String],
      default: false,
    },
    type: {
      type: String,
      default: "switch",
    },
    id: {
      type: String,
      default: "",
    },
    disabled: {
      type: [Boolean, String],
      default: false,
    },
    color: {
      type: String,
      default: "var(--w-primary-color)",
    },
    size: String,
  },
  data() {
    return {
      switchChecked: this.checked,
    };
  },
  computed: {
    switchClass() {
      let classArr = [];
      if (this.size == "small") {
        classArr.push("uni-w-switch-sm");
      } else if (this.size == "large") {
        classArr.push("uni-w-switch-lg");
      }
      return classArr.join(" ");
    },
  },
  watch: {
    checked(val) {
      this.switchChecked = val;
    },
  },
  created() {
    this.$dispatch("Form", "uni-form-group-update", {
      type: "add",
      vm: this,
    });
  },
  beforeDestroy() {
    this.$dispatch("Form", "uni-form-group-update", {
      type: "remove",
      vm: this,
    });
  },
  listeners: {
    "label-click": "_onClick",
    "@label-click": "_onClick",
  },
  methods: {
    _onClick($event) {
      if (this.disabled) {
        return;
      }
      this.switchChecked = !this.switchChecked;
      $event.detail.value = this.switchChecked;
      this.$emit("input", this.switchChecked);
      this.$emit("change", $event);
    },
    _resetFormData() {
      this.switchChecked = false;
    },
    _getFormData() {
      const data = {};
      if (this.name !== "") {
        data.value = this.switchChecked;
        data.key = this.name;
      }
      return data;
    },
  },
};
</script>
<style lang="scss">
.uni-w-switch {
  -webkit-tap-highlight-color: transparent;
  display: inline-block;
  cursor: pointer;
}

.uni-w-switch[hidden] {
  display: none;
}

.uni-w-switch[disabled] {
  cursor: not-allowed;
  .switch-label {
    color: var(--text-color-disable);
  }
}

.uni-w-switch.uni-w-switch-sm {
  transform: scale(0.7);
}
.uni-w-switch.uni-w-switch-lg {
  transform: scale(1.1);
}

.uni-w-switch .uni-switch-wrapper {
  display: -webkit-inline-flex;
  display: inline-flex;
  -webkit-align-items: center;
  align-items: center;
  vertical-align: middle;
}

.uni-w-switch .uni-switch-input {
  -webkit-appearance: none;
  appearance: none;
  position: relative;
  min-width: 48px;
  width: auto;
  height: 24px;
  margin-right: 5px;
  border: 1px solid var(--w-border-color-mobile);
  outline: 0;
  border-radius: 12px;
  box-sizing: border-box;
  background-color: var(--w-bg-color-mobile-th);
  transition: background-color 0.1s, border 0.1s;
}

.uni-w-switch[disabled] .uni-switch-input {
  opacity: 0.7;
}

.uni-w-switch .uni-switch-input:before {
  content: " ";
  position: absolute;
  top: 0;
  left: 0;
  width: calc(100% - 0px);
  height: 22px;
  border-radius: 12px;
  background-color: var(--w-fill-color-base);
  transition: -webkit-transform 0.3s;
  transition: transform 0.3s;
  transition: transform 0.3s, -webkit-transform 0.3s;
}

.uni-w-switch .uni-switch-input:after {
  content: " ";
  position: absolute;
  top: 50%;
  left: 1px;
  width: 22px;
  height: 22px;
  border-radius: 15px;
  background-color: #ffffff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.4);
  transform: translateY(-50%);
  transition: all 0.2s cubic-bezier(0.78, 0.14, 0.15, 0.86);
  -webkit-transition: all 0.2s cubic-bezier(0.78, 0.14, 0.15, 0.86);
}

.uni-w-switch .uni-switch-input.uni-switch-input-checked {
  border-color: var(--w-primary-color);
  background-color: var(--w-primary-color);
}

.uni-w-switch .uni-switch-input.uni-switch-input-checked:before {
  -webkit-transform: scale(0);
  transform: scale(0);
}

.uni-w-switch .uni-switch-input.uni-switch-input-checked:after {
  -webkit-transform: translate(-100%, -50%);
  transform: translate(-100%, -50%);
  left: 100%;
  margin-left: -1px;
}

.uni-w-switch .uni-switch-input .switch-label {
  display: block;
  margin-right: 6px;
  margin-left: 26px;
  line-height: 22px;
  position: relative;
  z-index: 1;
}
.uni-w-switch .uni-switch-input.uni-switch-input-checked .switch-label {
  margin-left: 6px;
  margin-right: 26px;
  color: #ffffff;
}

.uni-w-switch .uni-checkbox-input {
  margin-right: 5px;
  -webkit-appearance: none;
  appearance: none;
  outline: 0;
  border: 1px solid #d1d1d1;
  background-color: #ffffff;
  border-radius: 3px;
  width: 22px;
  height: 22px;
  position: relative;
  color: var(--w-primary-color);
}

.uni-w-switch:not([disabled]) .uni-checkbox-input:hover {
  border-color: var(--w-primary-color);
}

.uni-w-switch .uni-checkbox-input.uni-checkbox-input-checked:before {
  font: normal normal normal 14px/1 "uni";
  content: "\EA08";
  color: inherit;
  font-size: 22px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -48%) scale(0.73);
  -webkit-transform: translate(-50%, -48%) scale(0.73);
}

.uni-w-switch .uni-checkbox-input.uni-checkbox-input-disabled {
  background-color: var(--bg-disable-color);
}

.uni-w-switch .uni-checkbox-input.uni-checkbox-input-disabled:before {
  color: var(--text-color-disable);
}
</style>
