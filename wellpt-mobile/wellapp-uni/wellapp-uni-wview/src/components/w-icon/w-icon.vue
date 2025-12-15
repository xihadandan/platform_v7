<template>
  <text v-if="icon" class="w-icon" @tap="onTap">
    <icon v-if="isIconfont" :class="iconClass" :style="iconStyle" :color="color" :size="size" :type="iconType"></icon>
    <uni-icons v-else :class="iconClass" :style="iconStyle" :color="color" :size="size" :type="icon"></uni-icons>
  </text>
</template>
<script>
export default {
  props: {
    icon: String,
    iconClass: String | Object | Array,
    iconStyle: String | Object,
    color: String,
    size: Number,
    data: String | Object | Array,
  },
  data() {
    return {};
  },
  computed: {
    isIconfont() {
      return this.icon && this.icon.indexOf("iconfont") > -1;
    },
    iconType() {
      return this.icon.replace(/\b(?=iconfont)\b/, "pticon ");
    },
  },
  methods: {
    onTap(e) {
      this.$emit("onTap", this.data);
    },
  },
};
</script>
<style lang="scss" scoped>
.w-icon {
  uni-icon {
    vertical-align: middle;
  }
}
</style>
