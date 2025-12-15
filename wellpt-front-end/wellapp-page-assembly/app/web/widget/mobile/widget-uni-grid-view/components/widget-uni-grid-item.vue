<template>
  <div class="widget-uni-grid-item" :class="configuration.displayStyle || 'iconTop_textBottom'" :style="[groupStyle, groupBGStyle]">
    <div class="icon-box-container" v-if="item.icon.src != undefined">
      <div class="icon-box" :style="[iconBGStyle, iconStyle]">
        <!-- 图片 -->
        <img
          :src="item.icon.src"
          v-if="item.icon.src.startsWith('/')"
          :style="{
            width: iconStyle ? iconStyle.fontSize : '100%',
            height: iconStyle ? iconStyle.fontSize : '100%'
          }"
        />
        <Icon class="icon" :type="item.icon.src" />
      </div>
    </div>
    <div class="text-box" :style="[titleStyle]" v-if="item.text">
      <div :class="titleClass" v-html="item.text"></div>
      <div v-if="item.subtext" :class="subTitleClass" class="subtext" v-html="item.subtext"></div>
    </div>
  </div>
</template>

<script type="text/babel">
import { assign } from 'lodash';
export default {
  name: 'WidgetUniGridItem',
  props: {
    item: {
      type: Object,
      requried: true
    },
    configuration: Object
  },
  data() {
    return {};
  },
  watch: {},
  beforeCreate() {},
  components: {},
  computed: {
    groupBGStyle() {
      let param = 'groupStyle';
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
      let param = 'groupStyle';
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
      let param = 'iconStyle';
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
      let param = 'iconStyle';
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
      let param = 'titleStyle';
      let style = {};
      if (this.configuration[param]) {
        style = this.getTeamStyle(this.configuration[param]);
      }
      if (this.item[param]) {
        assign(style, this.getTeamStyle(this.item[param]));
      }
      return style;
    },
    titleClass() {
      let arr = [];
      if (this.configuration.titleStyle) {
        if (this.configuration.titleStyle.titleEllipsis) {
          arr.push('w-ellipsis-' + this.configuration.titleStyle.titleEllipsis);
        }
      }
      return arr.join(' ');
    },
    subTitleClass() {
      let arr = [];
      if (this.configuration.titleStyle) {
        if (this.configuration.titleStyle.subTitleEllipsis) {
          arr.push('w-ellipsis-' + this.configuration.titleStyle.subTitleEllipsis);
        }
      }
      return arr.join(' ');
    }
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
          backgroundSize
        } = data.backgroundStyle;

        if (backgroundColor) {
          style.backgroundColor = this.getColorValue(backgroundColor);
        }
        let bgImgStyle = bgImageUseInput ? backgroundImageInput : backgroundImage;
        if (bgImgStyle) {
          let isUrl =
            bgImgStyle.startsWith('data:') ||
            bgImgStyle.startsWith('http') ||
            bgImgStyle.startsWith('/') ||
            bgImgStyle.startsWith('../') ||
            bgImgStyle.startsWith('./');
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
    getTeamStyle(data) {
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
        style.fontSize = data.size + 'px';
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
        style['--uni-grid-item-subtitle-size'] = data.subSize + 'px';
      }
      if (data.subColor) {
        style['--uni-grid-item-subtitle-color'] = data.subColor;
      }
      return style;
    },
    formatBorderRadius(borderRadius) {
      if (borderRadius != undefined) {
        if (Array.isArray(borderRadius)) {
          return borderRadius
            .map(item => {
              return item + 'px';
            })
            .join(' ');
        } else {
          return borderRadius + 'px';
        }
      }
      return undefined;
    },
    getColorValue(color) {
      if (color) {
        return color.startsWith('#') ? color : `var(${color})`;
      }
      return '';
    }
  },
  mounted() {}
};
</script>
<style lang="less">
.widget-uni-grid-item {
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
      margin: var(--w-margin-2xs) 0 0;
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
    font-size: 38px;
    color: var(--w-primary-color);
    display: flex;
    align-items: center;
    justify-content: center;
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
