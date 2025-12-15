<template>
  <view>
    <web-view :style="theme" :src="url" @message="getMessage"></web-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      url: "",
      title: "",
    };
  },
  onLoad(options) {
    if (options && options.url) {
      this.url = options.url;
    }
    if (options && options.title) {
      uni.setNavigationBarTitle({ title: options.title });
      this.title = options.title;
    }
  },
  methods: {
    getMessage(event) {
      uni.showModal({
        content: JSON.stringify(event.detail),
        showCancel: false,
      });
    },
  },
};
</script>

<style></style>
