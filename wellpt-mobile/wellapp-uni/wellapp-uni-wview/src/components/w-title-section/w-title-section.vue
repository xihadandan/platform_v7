<template>
  <view class="w-title-section" :style="{ borderRadius: formatBorderRadius(configuration.borderRadius) }">
    <uni-w-section
      :title="configuration.title"
      :sub-title="configuration.subTitle"
      :titleColor="configuration.titleColor"
      :subTitleColor="configuration.subTitleColor"
      :type="configuration.decorationType == 'custom' ? null : configuration.decorationType"
      :headerStyle="{
        backgroundImage: backgroundImage,
        backgroundRepeat: 'no-repeat',
        backgroundSize: 'cover',
      }"
    >
      <template v-slot:decoration v-if="configuration.decorationType == 'custom'">
        <view class="icon-box" v-if="configuration.icon.src != undefined">
          <image
            v-if="configuration.icon.src.startsWith('/')"
            class="image"
            mode="aspectFit"
            :src="configuration.icon.url"
          />
          <uni-icons
            v-else
            :custom-prefix="configuration.icon.src.startsWith('iconfont') ? 'iconfont' : undefined"
            :type="configuration.icon.src"
            :size="configuration.icon.fontSize"
            :color="configuration.icon.color"
          ></uni-icons>
        </view>
      </template>
      <template v-slot:default v-if="configuration.allowContent && configuration.widgets.length > 0">
        <template v-for="item in configuration.widgets">
          <w-widget :widget="item" :parent="{}" :key="item.id"></w-widget>
        </template>
      </template>
      <template
        v-slot:right
        v-if="configuration.titleClickable && (configuration.titleClickText || configuration.titleClickIcon)"
      >
        <view>
          <text :style="{ color: configuration.titleColor }">{{ configuration.titleClickText }}</text>
          <uni-icons
            v-if="configuration.titleClickIcon"
            :custom-prefix="configuration.titleClickIcon.startsWith('iconfont') ? 'iconfont' : undefined"
            :type="configuration.titleClickIcon"
            :color="configuration.titleColor"
          ></uni-icons>
        </view>
      </template>
    </uni-w-section>
  </view>
</template>

<script>
import mixin from "../page-widget-mixin";
import { storage, appContext } from "wellapp-uni-framework";

export default {
  name: "w-title-section",
  mixins: [mixin],
  data() {
    return {
      configuration: this.widget.configuration || {},
      backgroundImage: undefined,
    };
  },
  components: {
    "w-widget": () => import("../w-widget/w-widget.vue"),
  },
  computed: {},
  created() {
    if (this.configuration.backgroundImage) {
      this.backgroundImage = `url(${storage.fillAccessResourceUrl(this.configuration.backgroundImage)})`;
      console.log("背景图片", this.backgroundImage);
    }

    if (this.configuration.icon.src && this.configuration.icon.src.startsWith("/")) {
      if (this.configuration.icon.src.startsWith("/static/")) {
        this.configuration.icon.url = this.configuration.icon.src;
      } else {
        this.configuration.icon.url = storage.fillAccessResourceUrl(this.configuration.icon.src);
      }
    }
  },
  methods: {
    formatBorderRadius(borderRadius) {
      if (borderRadius != undefined) {
        if (Array.isArray(borderRadius)) {
          return borderRadius
            .map((item) => {
              return item + "px";
            })
            .join(" ");
        } else {
          return borderRadius + "px";
        }
      }
      return undefined;
    },
  },
};
</script>

<style lang="scss">
.w-title-section {
  background-color: transparent;
  margin-bottom: 12px;
  overflow: hidden;

  .uni-section__content-title {
    font-weight: 600;
  }

  .icon-box {
    width: 40px;
    margin-right: 8px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    .image {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }
  }
}
</style>
