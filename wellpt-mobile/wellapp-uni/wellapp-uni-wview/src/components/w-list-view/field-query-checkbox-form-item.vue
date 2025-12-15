<template>
  <uni-forms-item :label="criterion.label">
    <uni-data-checkbox v-model="criterion.value" multiple :localdata="items" />
  </uni-forms-item>
</template>

<script>
import mixin from "./file-query-option-mixin";
export default {
  mixins: [mixin],
  data() {
    return {};
  },
};
</script>

<style lang="scss" scoped></style>
