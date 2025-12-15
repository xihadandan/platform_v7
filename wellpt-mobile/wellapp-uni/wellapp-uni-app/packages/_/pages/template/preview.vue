<template>
  <view style="width: 100vw">
    <w-template :widget="widget" v-if="widget != undefined" ref="wTemplate"></w-template>
    <view class="un-know-result" v-if="unKnowTemplate">
      <view class="title"><text>404</text></view>
      <view class="description"><text> 移动端服务目录 src/development/template 下无该模板文件 </text></view>
    </view>
  </view>
</template>
<style lang="scss" scoped>
.un-know-result {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  > .title {
    font-weight: bolder;
    font-size: 34px;
    margin-bottom: 12px;
  }
  > .description {
    color: rgb(184, 184, 184);
    font-size: 11px;
  }
}
</style>
<script>
import { storage } from "wellapp-uni-framework";

export default {
  props: {},
  components: {},
  computed: {},
  data() {
    return { widget: undefined, unKnowTemplate: false };
  },
  onLoad(options) {
    if (options.accessToken) {
      storage.setAccessToken(options.accessToken);
    }
    this.widget = {
      configuration: {
        templateName: options.templateName,
      },
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.unKnowTemplate = this.$refs.wTemplate.$children[0].$children.length == 0;
  },
  methods: {},
};
</script>
