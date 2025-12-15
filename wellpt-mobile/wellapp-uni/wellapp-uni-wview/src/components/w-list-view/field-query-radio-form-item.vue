<template>
  <uni-forms-item :label="criterion.label">
    <switch v-if="isSwitch" :checked="criterion.value" @change="changeSwitch" />
    <uni-data-checkbox v-else v-model="criterion.value" :localdata="items" />
  </uni-forms-item>
</template>

<script>
import mixin from "./file-query-option-mixin";
export default {
  mixins: [mixin],
  data() {
    return {};
  },
  computed: {
    isSwitch: function () {
      let queryOptions = this.criterion.queryOptions || {};
      let options = queryOptions.options || {};
      return options.switchStyle;
    },
  },
  methods: {
    changeSwitch(e) {
      this.$set(this.criterion, "value", e.detail.value);
    },
  },
};
</script>

<style lang="scss" scoped></style>
