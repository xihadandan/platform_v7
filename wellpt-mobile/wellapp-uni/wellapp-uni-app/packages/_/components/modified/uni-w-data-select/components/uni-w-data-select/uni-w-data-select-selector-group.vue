<template>
  <view class="uni-select__w-selector-group">
    <view class="label">
      <slot name="label"></slot>
    </view>
    <slot></slot>
  </view>
</template>

<script>
export default {
  name: "uniWDataSelectSelectorGroup",
  props: {},
  data() {
    return {};
  },
  created() {},
  mounted() {},
  computed: {},
  filters: {},
  watch: {},
  methods: {},
};
</script>

<style lang="scss">
.uni-select__w-selector-group {
  .label {
    color: $uni-text-color-grey;
    padding: 0 12px;
  }
  ::v-deep .uni-select__selector-item {
    .uni-select__selector-item-text-view {
      justify-content: start !important;
    }
  }
}
</style>
