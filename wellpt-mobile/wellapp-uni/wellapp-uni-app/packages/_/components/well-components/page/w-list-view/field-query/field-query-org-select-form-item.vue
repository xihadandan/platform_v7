<template>
  <uni-forms-item :label="criterion.label">
    <w-org-select
      :orgUuid="options.orgUuid"
      :isPathValue="options.isPathValue"
      :displayStyle="options.inputDisplayStyle"
      title=""
      :multiSelect="options.multiSelect"
      :titleDisplay="options.titleDisplay"
      :checkableTypes="options.checkableTypes"
      :orgType="options.orgType"
      :defaultOrgType="options.defaultOrgType"
      :bordered="true"
      showGlobalPopup
      v-model="value"
      @change="onChange"
      ref="orgSelectQueryRef"
    />
  </uni-forms-item>
</template>

<script>
import _ from "lodash";
import mixin from "./file-query-option-mixin";
export default {
  mixins: [mixin],
  data() {
    let value = this.criterion.value || [];
    return {
      value,
    };
  },
  computed: {},
  mounted() {},
  methods: {
    onChange({ value, label, nodes }) {
      console.log("组织弹出框选择变更：", arguments);
      this.$set(this.criterion, "value", this.value);
      console.log(JSON.stringify(this.criterion));
    },
    onClear() {
      this.$refs.orgSelectQueryRef.onClearInput();
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
