<template>
  <div
    class="w-grid-item"
    :class="configuration.displayStyle || 'iconTop_textBottom'"
    :style="[groupStyle, groupBGStyle]"
  >
    <view class="icon-box-container" v-if="item.icon.src != undefined">
      <view class="icon-box" :style="[iconBGStyle, iconStyle]">
        <view
          v-if="item.icon.src.startsWith('/')"
          :style="{
            width: iconStyle ? iconStyle.fontSize + 'px' : '100%',
            height: iconStyle ? iconStyle.fontSize + 'px' : '100%',
          }"
        >
          <image class="image" mode="aspectFit" :src="item.icon.url" style="width: 100%; height: 100%" />
        </view>
        <w-icon v-else :icon="item.icon.src" :size="iconStyle ? iconStyle.fontSize : undefined"></w-icon>
      </view>
    </view>
    <view class="text-box" :style="[titleStyle]" v-if="item.text">
      <rich-text :class="titleClass" v-if="item.text" :nodes="$t(item.uuid, item.text)"></rich-text>
      <rich-text
        :class="[subTitleClass, 'subtext']"
        v-if="item.subtext"
        :nodes="$t(item.uuid + '_subTitle', item.subtext)"
      ></rich-text>
    </view>
  </div>
</template>

<script type="text/babel">
import { assign } from "lodash";
import { storage } from "wellapp-uni-framework";
export default {
  name: "WGridItem",
  inject: ["widgetGirdViewContext"],
  props: {
    item: {
      type: Object,
      requried: true,
    },
    configuration: Object,
  },
  data() {
    return {};
  },
  watch: {},
  beforeCreate() {},
  components: {},
  computed: {
    groupBGStyle() {
      let param = "groupStyle";
      let style = {};
      if (this.configuration[param] && this.configuration[param].backgroundStyle) {
        style = this.getBackGroundStyle(this.configuration[param]);
      }
      if (this.item[param] && this.item[param].backgroundStyle) {
        assign(style, this.getBackGroundStyle(this.item[param]));
      }
      return style;
    },
    groupStyle() {
      let param = "groupStyle";
      let style = {};
      if (this.configuration[param]) {
        style = this.getTeamStyle(this.configuration[param]);
      }
      if (this.item[param]) {
        assign(style, this.getTeamStyle(this.item[param]));
      }
      return style;
    },
    iconBGStyle() {
      let param = "iconStyle";
      let style = {};
      if (this.configuration[param] && this.configuration[param].backgroundStyle) {
        style = this.getBackGroundStyle(this.configuration[param]);
      }
      if (this.item[param] && this.item[param].backgroundStyle) {
        assign(style, this.getBackGroundStyle(this.item[param]));
      }
      return style;
    },
    iconStyle() {
      let param = "iconStyle";
      let style = {};
      if (this.configuration[param]) {
        style = this.getTeamStyle(this.configuration[param]);
      }
      if (this.item[param]) {
        assign(style, this.getTeamStyle(this.item[param]));
      }
      return style;
    },
    titleStyle() {
      let param = "titleStyle";
      let style = {};
      if (this.configuration[param]) {
        style = this.getTeamStyle(this.configuration[param], "title");
      }
      if (this.item[param]) {
        assign(style, this.getTeamStyle(this.item[param]), "title");
      }
      return style;
    },
    titleClass() {
      let arr = [];
      if (this.configuration.titleStyle) {
        if (this.configuration.titleStyle.titleEllipsis) {
          arr.push("w-ellipsis-" + this.configuration.titleStyle.titleEllipsis);
        }
      }
      return arr.join(" ");
    },
    subTitleClass() {
      let arr = [];
      if (this.configuration.titleStyle) {
        if (this.configuration.titleStyle.subTitleEllipsis) {
          arr.push("w-ellipsis-" + this.configuration.titleStyle.subTitleEllipsis);
        }
      }
      return arr.join(" ");
    },
  },
  created() {},
  methods: {
    getBackGroundStyle(data) {
      let style = {};
      if (data) {
        let {
          backgroundColor,
          backgroundImage,
          backgroundImageInput,
          bgImageUseInput,
          backgroundPosition,
          backgroundRepeat,
          backgroundSize,
        } = data.backgroundStyle;

        if (backgroundColor) {
          style.backgroundColor = this.getColorValue(backgroundColor);
        }
        let bgImgStyle = bgImageUseInput ? backgroundImageInput : backgroundImage;
        if (bgImgStyle) {
          let isUrl =
            bgImgStyle.startsWith("data:") ||
            bgImgStyle.startsWith("http") ||
            bgImgStyle.startsWith("/") ||
            bgImgStyle.startsWith("../") ||
            bgImgStyle.startsWith("./");
          if (bgImgStyle.startsWith("/proxy-repository/")) {
            bgImgStyle = storage.fillAccessResourceUrl(bgImgStyle.split("/proxy-repository")[1]);
          }
          style.backgroundImage = isUrl ? `url("${bgImgStyle}")` : bgImgStyle;
        }
        if (backgroundPosition) {
          style.backgroundPosition = backgroundPosition;
        }
        if (backgroundRepeat) {
          style.backgroundRepeat = backgroundRepeat;
        }
        if (backgroundSize) {
          style.backgroundSize = backgroundSize;
        }
      }
      return style;
    },
    getTeamStyle(data, type) {
      let style = {};
      if (data.padding) {
        style.padding = data.padding;
      }
      if (data.margin) {
        style.margin = data.margin;
      }
      if (data.borderColor) {
        style.borderColor = this.getColorValue(data.borderColor);
      }
      if (data.size) {
        style.fontSize = data.size;
        if (type == "title") {
          style.fontSize += "px";
        }
      }
      if (data.color) {
        style.color = this.getColorValue(data.color);
      }
      if (data.width) {
        style.width = data.width;
      }
      if (data.height) {
        style.height = data.height;
      }
      if (data.borderRadius || data.borderRadius === 0) {
        style.borderRadius = this.formatBorderRadius(data.borderRadius);
      }
      if (data.subSize) {
        style["--uni-grid-item-subtitle-size"] = data.subSize;
      }
      if (data.subColor) {
        style["--uni-grid-item-subtitle-color"] = data.subColor;
      }
      return style;
    },
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
    getColorValue(color) {
      if (color) {
        return color.startsWith("#") ? color : `var(${color})`;
      }
      return "";
    },
    $t() {
      if (this.widgetGirdViewContext != undefined) {
        return this.widgetGirdViewContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
  },
  mounted() {},
};
</script>
<style lang="scss">
.w-grid-item {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  border: 1px solid transparent;
  padding: var(--w-padding-2xs);
  --uni-grid-item-subtitle-size: var(--w-font-size-sm);
  --uni-grid-item-subtitle-color: var(--w-text-color-light);

  // 图标左、文字右
  &.iconLeft_textRight {
    flex-direction: row;
    justify-content: flex-start;
    .icon-box {
      margin: 0 var(--w-margin-2xs) 0 0;
    }
  }
  // 文字左、图标右
  &.textLeft_IconRight {
    flex-direction: row-reverse;
    justify-content: space-between;
    .icon-box {
      margin: 0 0 0 var(--w-margin-2xs);
    }
  }
  // 图标上、文字下（居中）
  &.iconTop_textBottom {
    text-align: center;
    .icon-box {
      margin: 20px 0 0;
    }
    .text-box {
      margin: var(--w-margin-2xs) 2px 0;
    }
  }
  // 文字上居左、图标下居右
  &.textTop_iconBottom {
    flex-direction: column-reverse;
    align-items: normal;
    .icon-box-container {
      display: flex;
      justify-content: flex-end;
    }
    .icon-box {
    }
    .text-box {
    }
  }
  .icon-box {
    // font-size: 38px;
    color: var(--w-primary-color);
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid transparent;
    .icon {
      font-size: inherit;
      color: inherit;
    }
    img {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }
  }

  .text-box {
    font-size: 14px;
    color: var(--w-text-color-dark);
    .subtext {
      font-size: var(--uni-grid-item-subtitle-size);
      color: var(--uni-grid-item-subtitle-color);
    }
  }
}
</style>
