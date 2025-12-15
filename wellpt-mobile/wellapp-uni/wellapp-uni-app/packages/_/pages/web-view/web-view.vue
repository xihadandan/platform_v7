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
    if (options && options.redirectPageTitle) {
      this.title = decodeURI(options.redirectPageTitle);
      uni.setNavigationBarTitle({ title: this.title });
    }
  },
  methods: {
    getMessage(event) {
      uni.showModal({
        content: JSON.stringify(event.detail),
        showCancel: false,
        confirmText: this.$t("global.confirm", "确认"),
      });
    },
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
  },
};
</script>

<style></style>
