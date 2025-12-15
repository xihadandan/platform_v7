<template>
  <view class="about" :style="theme">
    <w-section title="应用信息" type="line">
      <uni-list v-if="appInfo">
        <uni-list-item title="ID" :rightText="appInfo.appid" />
        <uni-list-item title="版本" :rightText="appInfo.version" />
        <uni-list-item title="版本号" :rightText="appInfo.versionCode" />
        <!-- <uni-list-item title="uni-app版本" :rightText="appInfo.uniVersion" /> -->
      </uni-list>
    </w-section>
    <w-section title="资源包信息" type="line">
      <uni-list v-if="widgetInfo">
        <uni-list-item title="名称" :rightText="widgetInfo.name" />
        <uni-list-item title="ID" :rightText="widgetInfo.appid" />
        <uni-list-item title="版本" :rightText="widgetInfo.version" />
        <uni-list-item title="版本号" :rightText="widgetInfo.versionCode" />
      </uni-list>
    </w-section>
  </view>
</template>

<script>
export default {
  data() {
    return {
      appInfo: null,
      widgetInfo: null,
    };
  },
  created() {
    const _self = this;
    // #ifdef APP-PLUS
    _self.appInfo = plus.runtime;
    plus.runtime.getProperty(plus.runtime.appid, function (widgetInfo) {
      _self.widgetInfo = widgetInfo;
    });
    // #endif
  },
};
</script>

<style lang="scss" scoped>
.about {
  display: flex;
  flex-direction: column;
  width: 100%;

  background-color: $uni-bg-color;
}
</style>
