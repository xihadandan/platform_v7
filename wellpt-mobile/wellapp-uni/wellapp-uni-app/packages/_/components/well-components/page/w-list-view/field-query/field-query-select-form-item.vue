<template>
  <uni-forms-item :label="criterion.label">
    <uni-w-data-select
      :componentType="componentType"
      v-model="value"
      :title="criterion.label"
      :initOptions="items"
      :localdata="items"
      :multiple="isMultiSelect"
      :showSearch="options.searchable"
      dropType="picker"
      bordered
      :clear="allowClear"
      @change="onChange"
    ></uni-w-data-select>
  </uni-forms-item>
</template>

<script>
import mixin from "./file-query-option-mixin";
export default {
  mixins: [mixin],
  data() {
    return {
      value: this.criterion.value,
    };
  },
  computed: {
    isMultiSelect: function () {
      let queryOptions = this.criterion.queryOptions || {};
      let options = queryOptions.options || {};
      return queryOptions && (queryOptions.multiple == "1" || options.multiSelectEnable);
    },
    componentType() {
      return this.criterion.queryType == "groupSelect" ? "select-group" : "select";
    },
  },
  mounted() {
    const _self = this;
    _self.$set(_self.criterion, "displayValue", this.getDisplayValue());
  },
  methods: {
    onChange: function (value, selectedLabel) {
      const _self = this;
      _self.$set(_self.criterion, "value", Array.isArray(value) ? value.join(";") : value);
      _self.$set(_self.criterion, "displayValue", selectedLabel);
    },
    onValueChange: function (value) {
      const _self = this;
      this.value = this.criterion.value;
    },
  },
};
</script>

<style lang="scss" scoped>
.input-container {
  /* #ifndef APP-NVUE */
  display: flex;
  box-sizing: border-box;
  /* #endif */
  flex-direction: row;
  align-items: center;
  border: 1px solid $uni-border-1;
  border-radius: 4px;

  font-size: 14px;
  min-height: 36px;
}

.input-text {
  width: 100%;
  padding-left: 10px;
}
</style>
