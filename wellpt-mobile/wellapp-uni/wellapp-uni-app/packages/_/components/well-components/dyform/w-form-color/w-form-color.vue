<template>
  <uni-forms-item
    :label="itemLabel"
    :name="formModelItemProp"
    :label-position="widget.configuration.labelPosition"
    :class="widgetClass"
    :style="itemStyle"
    :ref="fieldCode"
  >
    <template v-if="!displayAsLabel">
      <uni-w-color-picker
        v-model="formData[widget.configuration.code]"
        :spareColor="widget.configuration.defaultColors"
        :disabled="disable || readonly"
        :type="picker"
        :showText="widget.configuration.showText"
        :inputBorder="widget.configuration.uniConfiguration ? widget.configuration.uniConfiguration.inputBorder : false"
        :size="widget.configuration.size"
      >
        <template slot="picker-bottom" v-if="pickerChange">
          <view style="text-align: right; padding: 20px 10px 10px">
            <text style="color: var(--w-primary-color)" @click="onPickerChange">{{ pickerChangeText }}</text>
          </view>
        </template>
      </uni-w-color-picker>
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
  computed: {
    pickerChangeText() {
      if (this.picker == "Sketch") {
        return this.$t("global.collapse", "收起");
      } else {
        return this.$t("global.more", "更多");
      }
    },
  },
  data() {
    let pickerChange = this.widget.configuration.picker == "Twitter" && this.widget.configuration.pickerChange;
    return {
      picker: this.widget.configuration.picker,
      pickerChange,
    };
  },
  beforeCreate() {},
  created() {
    if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
      this.$set(this.widget.configuration, "uniConfiguration", { inputBorder: false });
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    setValue(val) {
      this.$set(this.formData, this.widget.configuration.code, val);
      this.emitChange();
    },
    displayValue() {
      return this.formData[this.widget.configuration.code];
    },
    onPickerChange() {
      if (this.picker == "Sketch") {
        this.picker = this.widget.configuration.picker == "Sketch" ? "Twitter" : this.widget.configuration.picker;
      } else {
        this.picker = "Sketch";
      }
    },
  },
};
</script>
