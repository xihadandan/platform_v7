<template>
  <!-- #ifdef H5 -->
  <tbody>
    <slot></slot>
  </tbody>
  <!-- #endif -->
  <!-- #ifndef H5 -->
  <view class="uni-w-tbody"><slot></slot></view>
  <!-- #endif -->
</template>

<script>
export default {
  name: "uniWBody",
  options: {
    virtualHost: true,
  },
  data() {
    return {};
  },
  created() {},
  methods: {},
};
</script>

<style lang="scss">
/* #ifndef APP-NVUE */
.uni-w-tbody {
  display: table-row-group;
}
/* #endif */
</style>
