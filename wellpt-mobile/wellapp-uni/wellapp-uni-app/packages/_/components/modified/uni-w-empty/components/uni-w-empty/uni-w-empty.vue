<template>
  <view class="uni-w-empty" :style="emptyStyle">
    <image class="uni-w-empty__image" v-if="nullImg" mode="aspectFit" :src="nullImg" :style="imageStyle"></image>
    <view class="uni-w-empty__text" v-if="showText" :style="imageStyle">{{
      text || $t("global.noData", "暂无数据")
    }}</view>
    <slot></slot>
  </view>
</template>
<script>
export default {
  name: "uni-w-empty",
  props: {
    type: {
      type: String,
      default: "",
    },
    text: String,
    imageUrl: {
      type: String,
      default: "",
    },
    imageStyle: {
      type: String,
      Object,
      default: "",
    },
    textStyle: {
      type: String,
      Object,
      default: "",
    },
    emptyStyle: {
      type: String,
      Object,
      default: "",
    },
    noImage: Boolean,
    showText: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {};
  },
  computed: {
    nullImg() {
      if (this.noImage) {
        return "";
      }
      if (this.imageUrl) {
        return this.imageUrl;
      }
      let img = "/static/images/empty/data-empty.png";
      if (this.type) {
        switch (this.type) {
          case "404": // 404
            img = "/static/images/empty/404-empty.png";
            break;
          case "image": // 图片
            img = "/static/images/empty/img-error-empty.png";
            break;
          case "jurisdiction": //权限
            img = "/static/images/empty/jurisdiction-empty.png";
            break;
          case "list": //列表
            img = "/static/images/empty/list-empty.png";
            break;
          case "network": // 网络
            img = "/static/images/empty/network-empty.png";
            break;
          case "search": // 搜索
            img = "/static/images/empty/search-empty.png";
            break;
          case "system": // 系统维护
            img = "/static/images/empty/system-maintenance-empty.png";
            break;
          default:
            break;
        }
      }
      return img;
    },
  },
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
  },
};
</script>
<style lang="scss" scoped>
.uni-w-empty {
  width: 100%;
  text-align: center;

  .uni-w-empty__image {
    width: 200px;
    height: 200px;
  }
  .uni-w-empty__text {
    color: $uni-text-color-grey;
    font-size: $uni-font-size-base;
    margin: 0 auto;
    padding: var(--w-padding-2xs) 30px;
  }
}
</style>
