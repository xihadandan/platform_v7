<template>
  <view class="w-html" :class="customClassCom" v-if="isEmptyUniJsModule">
    <!-- <view v-html="widget.configuration.content"></view> -->
    <rich-text :nodes="widget.configuration.content"></rich-text>
  </view>
  <w-widget-development v-else :widget="widget" :parent="parent"></w-widget-development>
</template>

<script>
import mixin from "../page-widget-mixin";
export default {
  mixins: [mixin],
  data() {
    return {};
  },
  created: function () {},
  methods: {},
};
</script>

<style lang="scss" scoped>
.w-html {
  background: $uni-bg-secondary-color;
}
</style>
