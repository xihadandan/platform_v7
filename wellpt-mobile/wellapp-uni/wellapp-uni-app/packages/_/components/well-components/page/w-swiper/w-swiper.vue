<template>
  <view class="w-swiper" :class="customClassCom" :style="swiperDotStyle" v-if="isEmptyUniJsModule">
    <uni-swiper-dot
      class="uni-swiper-dot-box"
      @clickItem="clickItem"
      :info="swiperItems"
      :current="current"
      :mode="configuration.dotShape"
      :dots-styles="dotsStyles"
      field="text"
      :style="swiperDotStyle"
    >
      <swiper
        class="swiper"
        circular
        :indicator-dots="indicatorDots"
        :autoplay="autoplay"
        :interval="interval"
        :duration="duration"
        :current="swiperDotIndex"
        @change="change"
      >
        <swiper-item v-for="item in swiperItems" :key="item.id" :style="{ borderRadius: borderRadius }">
          <view class="swiper-item" :style="swiperStyle" @click="clickSwiperItem(item)">
            <image v-if="item._imageUrl" class="image" mode="aspectFit" :src="item._imageUrl" />
            <view class="swiper-text w-ellipsis" v-if="item.text">{{ $t(item.uuid, item.text) }}</view>
          </view>
        </swiper-item>
      </swiper>
    </uni-swiper-dot>
  </view>
  <w-widget-development v-else :widget="widget" :parent="parent"></w-widget-development>
</template>

<script>
import mixin from "../page-widget-mixin";
import { storage } from "wellapp-uni-framework";
export default {
  mixins: [mixin],
  data() {
    let dotColorJson = { white: "255,255,255", black: "0,0,0", primary: "var(--w-parimay-color-rgb)" };
    let dotColor = dotColorJson[this.widget.configuration.dotColorType || "white"];
    return {
      configuration: this.widget.configuration || {},
      swiperItems: [],
      indicatorDots: false,
      autoplay: false,
      interval: 2500,
      duration: 500,
      current: 0,
      swiperDotIndex: 0,
      swiperStyle: {
        height: "300px",
      },
      swiperDotStyle: {
        height: "300px",
      },
      borderRadius: this.widget.configuration.borderRadius ? this.widget.configuration.borderRadius + "px" : "8px",
      dotColor,
    };
  },
  computed: {
    dotsStyles() {
      if (this.swiperItems.length == 1) {
        return {
          width: 0,
          height: 0,
          color: "transparent",
          backgroundColor: "transparent",
          border: "unset",
          selectedBackgroundColor: "transparent",
          selectedBorder: "transparent",
        };
      }
      let dotsStyles = {
        backgroundColor: "rgba(0, 0, 0, .3)",
        border: "1px rgba(0, 0, 0, .3) solid",
        color: "#fff",
        selectedBackgroundColor: "rgba(0, 0, 0, .9)",
        selectedBorder: "1px rgba(0, 0, 0, .9) solid",
      };
      if (this.dotColor) {
        dotsStyles.backgroundColor = `rgba(${this.dotColor}, .3)`;
        dotsStyles.border = "1px " + `rgba(${this.dotColor}, .3)` + " solid";
        dotsStyles.selectedBackgroundColor = `rgba(${this.dotColor}, .9)`;
        dotsStyles.selectedBorder = "1px " + `rgba(${this.dotColor}, .9)` + " solid";
      }

      return dotsStyles;
    },
  },
  created() {
    var _self = this;
    var configuration = this.widget.configuration || {};
    if (configuration.sliderHeight == "auto") {
      //轮播图高度
      if (configuration.dotPosition) {
        // this.swiperDotStyle.height = 320 + 'px';
      }
    } else if (parseInt(configuration.sliderHeight) > 0) {
      this.swiperStyle.height = configuration.sliderHeight + "px";
      this.swiperDotStyle.height = configuration.sliderHeight + "px";
      if (configuration.dotPosition) {
        this.swiperDotStyle.height = parseFloat(configuration.sliderHeight) + 25 + "px";
      }
    }

    if (configuration.time) {
      //轮播定时时间
      this.interval = parseInt(configuration.time);
    }
    if (configuration.isLoop) {
      //是否定时轮播
      this.autoplay = configuration.isLoop[0] && configuration.isLoop[0] == "on";
    }

    if (configuration.dotMode) {
      this.mode = configuration.dotMode;
    }

    let sliderItems = (configuration.sliderImg ? configuration.sliderImg.sliderItems : configuration.sliderItems) || [];
    var items = [];
    for (var index = 0; index < sliderItems.length; index++) {
      var item = sliderItems[index];
      if (item.hidden != "1" && item.src) {
        item._imageUrl = item.src;
        if (item.src.startsWith("url(")) {
          // base64图片码
          item._imageUrl = item.src.substring(5, item.src.length - 2);
        } else if (item.src.startsWith("/proxy-repository/")) {
          item._imageUrl = storage.fillAccessResourceUrl(item.src.split("/proxy-repository")[1]);
        }
        items.push(item);
      }
    }
    _self.swiperItems = items;
    if (items.length == 0) {
      this.swiperStyle = {
        height: "0px",
      };
    }
  },
  methods: {
    change(e) {
      this.current = e.detail.current;
      this.$emit("change", e);
    },
    clickItem(e) {
      this.swiperDotIndex = e;
      this.$emit("itemClick", e);
    },
    clickSwiperItem(e) {
      // this.$emit("swiperItemClick", e);
      if (e.eventHandler && e.eventHandler.actionType) {
        this.appContext.dispatchEvent({
          ui: this,
          ...e.eventHandler,
        });
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.w-swiper {
  /* width:690rpx; */
  width: 100%;
  background: $uni-bg-secondary-color;
}

.swiper {
  height: inherit;
}

.swiper-item {
  display: block;
  /* height: 300rpx;
    line-height: 300rpx; */
  text-align: center;
}

.image {
  margin: 0;
  width: 100%;
  height: 100%;
}

.swiper-text {
  text-shadow: $uni-shadow-base; //1px 1px 2px black;
  position: absolute;
  bottom: 0px;
  z-index: 1;
  background: rgba(0, 0, 0, 0.3);
  width: 100%;
  line-height: 34px;
  color: #fff;
  padding: 0 12px;
  text-align: left;
  width: calc(100% - 24px);
}
</style>
