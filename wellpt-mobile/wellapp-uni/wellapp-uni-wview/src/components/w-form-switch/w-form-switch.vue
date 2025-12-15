<template>
  <uni-forms-item :label="itemLabel" :name="formModelItemProp">
    <template v-if="!displayAsLabel">
      <uni-w-switch
        ref="switchRef"
        v-model="checked"
        @change="onChange"
        :disabled="disable || readonly"
        :readOnly="readonly"
      >
        <template v-if="widget.configuration.uniConfiguration.switchStyle == 'label'">
          <text slot="checkedChildren">{{ widget.configuration.checkedLabel }}</text>
          <text slot="unCheckedChildren">{{ widget.configuration.uncheckedLabel }}</text>
        </template>
      </uni-w-switch>
    </template>
    <view v-else :title="displayValue()">{{ displayValue() }}</view>
  </uni-forms-item>
</template>
<style lang="sass"></style>
<script type="text/babel">
import formElement from "../w-dyform/form-element.mixin";

export default {
  mixins: [formElement],
  props: {},
  components: {},
  computed: {},
  data() {
    return {
      checked: this.formData[this.widget.configuration.code] == this.widget.configuration.checkedValue,
      allowValues: [this.widget.configuration.checkedValue, this.widget.configuration.uncheckedValue],
    };
  },
  beforeCreate() {},
  created() {
    if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
      this.$set(this.widget.configuration, "uniConfiguration", {
        switchStyle: this.widget.configuration.switchStyle == "icon" ? "" : this.widget.configuration.switchStyle,
      });
    }
  },
  beforeMount() {},
  mounted() {
    this.emitChange();
  },
  methods: {
    onChange(e) {
      let checked = e.detail.value;
      this.checked = checked;
      this.formData[this.widget.configuration.code] =
        this.widget.configuration[checked ? "checkedValue" : "uncheckedValue"];
      // console.log(this.formData[this.widget.configuration.code]);
      this.emitChange();
    },
    setValue(val) {
      let i = -1;
      if (typeof val == "boolean") {
        // 真/假值转换
        i = val ? 0 : 1;
      } else {
        if (typeof this.widget.configuration.checkedValue == "number") {
          val = val ? Number(val) : null;
          i = this.allowValues.indexOf(val);
        } else {
          i = this.allowValues.indexOf(val);
        }
      }
      if (i != -1) {
        this.formData[this.widget.configuration.code] = this.allowValues[i];
        this.checked = i == 0;
      } else {
        console.error("开关设值的可选值为：", this.allowValues);
      }
      this.clearValidate();
    },
    displayValue(v, template) {
      let checked = this.checked;
      if (v != undefined) {
        checked = this.widget.configuration.checkedValue == v;
      }
      if (this.widget.configuration.uneditableDisplayState == "label") {
        // 不可编辑模式 纯文本
        if (this.widget.configuration.uniConfiguration.switchStyle == "label") {
          return checked ? this.widget.configuration.checkedLabel : this.widget.configuration.uncheckedLabel;
        } else {
          return checked ? this.widget.configuration.checkedValue : this.widget.configuration.uncheckedValue;
        }
      }
    },
  },
};
</script>
