<template>
  <view class="content">
    <w-app-page appPiPath="/pt-mobile"></w-app-page>
    <!-- <w-app-page
            appPiPath="/pt_mobile_demo/pt_mobile_demo_home"
            pageUuid="1bdd0814-e9d8-4385-94f0-40ce9c0bd812"
        ></w-app-page> -->
    <!-- <view class="w-app-page">
            <view class="w-page-container"></view>
        </view> -->
    <image class="logo" src="/static/logo.png"></image>
    <view>
      <text class="title">{{ title }}</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      title: "Hello",
    };
  },
  onLoad() {},
  methods: {},
};
</script>

<style lang="scss" scoped>
.content {
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.logo {
  height: 200rpx;
  width: 200rpx;
  margin: 200rpx auto 50rpx auto;
}

.text-area {
  display: flex;
  justify-content: center;
}

.title {
  font-size: 36rpx;
  color: $uni-primary;
}
</style>
