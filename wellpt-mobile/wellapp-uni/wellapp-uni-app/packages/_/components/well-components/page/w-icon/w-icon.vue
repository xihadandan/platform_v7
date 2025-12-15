<template>
  <view v-if="icon || iconConfig" class="w-icon" @tap="onTap">
    <template v-if="isIconfont || isPc">
      <template v-if="isGroupIcon">
        <icon :class="iconClass" :style="groupIconStyle" :size="groupIconFontSize" :type="iconType" />
      </template>
      <icon
        v-else
        :class="iconClass"
        :style="iconStyleResult"
        :color="color"
        :size="iconFontSize"
        :type="iconType"
      ></icon>
    </template>
    <uni-icons
      v-else
      :class="iconClass"
      :style="iconStyle"
      :color="color"
      :size="iconFontSize"
      :type="icon"
    ></uni-icons>
  </view>
</template>
<script>
import { utils } from "wellapp-uni-framework";
export default {
  props: {
    icon: String,
    iconClass: String | Object | Array,
    iconStyle: String | Object,
    color: String,
    size: {
      type: [String, Number],
      default: 20,
      validator: function validator(val) {
        return !val || typeof val === "number" || ["small", "large", "default"].includes(val);
      },
    },
    singleSize: Number,
    data: String | Object | Array,
    isPc: Boolean, // 配置得来，主要处理ant图标名有时不加ant-iconfont
    iconConfig: String,
    groupIconRate: {
      type: Number,
      default: 1.6,
    },
  },
  data() {
    let iconJson;
    if (this.iconConfig && utils.isValidJSON(this.iconConfig)) {
      iconJson = JSON.parse(this.iconConfig);
    }
    return {
      iconJson,
    };
  },
  computed: {
    groupIconFontSize() {
      return this.groupIconSize / this.groupIconRate;
    },
    iconFontSize() {
      if (this.groupIconSize && this.iconJson && !this.isGroupIcon) {
        if (this.singleSize) {
          return this.singleSize;
        }
      }
      return this.groupIconSize;
    },
    groupIconSize() {
      let size = 20;
      if (typeof this.size === "number") {
        size = this.size;
      }
      if (this.isGroupIcon) {
        if (["small", "large", "default"].includes(this.size)) {
          size = { small: 18, default: 20, large: 24 }[this.size];
        }
      }
      return size;
    },
    groupIconStyle() {
      let style = {};
      if (this.isGroupIcon) {
        const size = this.groupIconSize;
        style = {
          ...this.iconJson.cssStyle,
          textAlign: "center",
          width: `${size}px`,
          height: `${size}px`,
          lineHeight: `${size}px`,
        };
      }

      return style;
    },
    isGroupIcon() {
      let show = false;
      if (this.iconJson && this.iconJson.showBackground && this.iconJson.cssStyle["background-color"] && this.size) {
        show = true;
      }
      return show;
    },
    isIconfont() {
      if (this.iconJson) {
        return this.iconJson.iconClass && this.iconJson.iconClass.indexOf("iconfont") > -1;
      } else if (this.iconConfig) {
        return this.iconConfig.indexOf("iconfont") > -1;
      }
      return this.icon && this.icon.indexOf("iconfont") > -1;
    },
    iconType() {
      let currentIcon;
      if (this.icon) {
        currentIcon = this.icon;
      } else if (this.iconJson) {
        currentIcon = this.iconJson.iconClass;
      } else if (this.iconConfig) {
        currentIcon = this.iconConfig;
      }
      if (currentIcon.indexOf("iconfont") == -1) {
        return "pticon ant-iconfont " + currentIcon;
      } else if (currentIcon.indexOf("ant-iconfont") > -1) {
        return currentIcon.replace(/\b(?=ant-iconfont)\b/, "pticon ");
      }
      return currentIcon.replace(/\b(?=iconfont)\b/, "pticon ");
    },
    iconStyleResult() {
      if (this.iconJson && !this.isGroupIcon && this.iconJson.color) {
        if (this.iconStyle) {
          if (typeof this.iconStyle === "string") {
            return this.iconStyle + `;color:${this.iconJson.color}`;
          } else {
            return { ...this.iconStyle, color: this.iconJson.color };
          }
        }
        return { color: this.iconJson.color };
      }
      return this.iconStyle;
    },
  },
  methods: {
    onTap(e) {
      this.$emit("onTap", this.data, e);
    },
  },
};
</script>
<style lang="scss" scoped>
.w-icon {
  display: inline-block;
  uni-icon {
    vertical-align: middle;
  }
}
</style>
