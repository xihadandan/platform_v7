<template>
  <uni-forms-item :label="criterion.label">
    <custom-w-tree-select
      :title="criterion.label"
      :clearable="allowClear"
      :mutiple="isMultiSelect"
      :search="options.searchable"
      contentHeight="400px"
      :listData="items"
      linkage
      bordered
      v-model="value"
      @changeValue="onChange"
    ></custom-w-tree-select>
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
      return queryOptions && options.multiSelect;
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
