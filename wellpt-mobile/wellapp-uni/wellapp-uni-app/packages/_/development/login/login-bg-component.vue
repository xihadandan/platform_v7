<template>
  <view class="login-bg">
    <!-- Generator: Adobe Illustrator 26.3.1, SVG Export Plug-In . SVG Version: 6.00 Build 0)  -->
    <svg
      v-if="isH5"
      version="1.1"
      id="图层_1"
      xmlns="http://www.w3.org/2000/svg"
      xmlns:xlink="http://www.w3.org/1999/xlink"
      x="0px"
      y="0px"
      viewBox="0 0 390 338"
      style="enable-background: new 0 0 390 338"
      xml:space="preserve"
    >
      <rect id="主题色替换" style="fill: var(--w-primary-color)" width="390" height="338" />
      <polygon id="不变" style="opacity: 0.05; enable-background: new" points="195,337.75 0,0 0,338 390,338 390,0 " />
    </svg>
    <!-- App 端使用 image 标签 -->
    <image v-else :src="svgUrl" style="width: 100%; height: 338px" mode="aspectFill" />
    <slot></slot>
  </view>
</template>
<script>
import { utils } from "wellapp-uni-framework";
export default {
  name: "LoginBgComponent",
  data() {
    return {};
  },
  computed: {
    isH5() {
      return process.env.VUE_APP_PLATFORM === "h5";
    },
    svgUrl() {
      return (
        "data:image/svg+xml;utf8," +
        encodeURIComponent(`
        <svg
          version="1.1"
          id="图层_1"
          xmlns="http://www.w3.org/2000/svg"
          xmlns:xlink="http://www.w3.org/1999/xlink"
          x="0px"
          y="0px"
          viewBox="0 0 390 338"
          style="enable-background: new 0 0 390 338"
          xml:space="preserve"
        >
          <rect id="主题色替换" style="fill: ${this.themeColor}" width="390" height="338" />
          <polygon id="不变" style="opacity: 0.05; enable-background: new" points="195,337.75 0,0 0,338 390,338 390,0 " />
        </svg>
      `)
      );
    },
  },
  created() {
    this.themeColor = `rgba(${utils.getStringJsonValue(this.theme, "--w-primary-color-rgb")},1)`;
  },
  methods: {},
};
</script>
<style lang="scss" scoped>
.login-bg {
  position: relative;
  text-align: center;
}
</style>
