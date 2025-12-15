<template>
  <view :class="[btnClass, iconOnlyClass]" @tap="onTap">
    <slot name="prefix"></slot>
    <u-loading-icon v-if="loading" :mode="loadingMode" :size="iconSize"></u-loading-icon>
    <template v-else>
      <image
        v-if="iconUrl && !imageError"
        style="width: var(--w-button-icon-size); height: var(--w-button-icon-size)"
        class="w-button__image-icon"
        mode="aspectFit"
        :src="iconUrl"
        @error="errorHandle"
      />
      <w-icon
        class="w-button__image-icon"
        v-else-if="iconJson"
        :iconConfig="icon"
        :size="size"
        :singleSize="iconSize"
      />
      <w-icon
        class="w-button__image-icon"
        v-else-if="iconName"
        :icon="iconName"
        iconStyle="color: var(--w-button-font-color); font-size: var(--w-button-icon-size)"
        :size="iconSize"
      ></w-icon>
    </template>
    <slot>
      {{ textHidden ? "" : text }}
    </slot>
    <slot name="suffix"></slot>
  </view>
</template>
<script>
import { storage, utils } from "wellapp-uni-framework";
export default {
  name: "uni-w-button",
  props: {
    type: {
      type: String,
      default: "default",
    },
    size: {
      type: String,
      default: "default",
    },
    block: Boolean,
    shape: String,
    icon: String, //按钮前置图标
    text: String, //按钮文本
    textHidden: Boolean, //隐藏按钮文本
    iconOnly: Boolean,
    disabled: Boolean,
    ghost: Boolean,
    status: String, // 状态按钮，主题色将改成状态色，success、info、warning、error
    newline: Boolean, // 图标和文本换行
    loading: Boolean,
    loadingMode: {
      type: String,
      default: "spinner",
    },
  },
  computed: {
    iconConfig() {
      let obj = {};
      //  {
      //   iconClass: "ant-iconfont right",
      //   shape: "rect",
      //   style: "outlined",
      //   colorVar: "--w-primary-color",
      //   showBackground: false,
      //   cssStyle: { color: "var(--w-primary-color)", borderRadius: "20%" },
      // };
      if (this.icon.startsWith("{") && this.icon.endsWith("}")) {
        obj = JSON.parse(this.icon);
      } else {
        obj.iconClass = this.icon;
      }

      return obj;
    },
    btnClass() {
      let name = ["w-button"];
      name.push("w-button-" + (this.type || "default"));
      if (this.size) {
        if (this.size == "small") {
          name.push("w-button-sm");
        } else if (this.size == "large") {
          name.push("w-button-lg");
        }
      }
      if (this.block) {
        name.push("w-button-block");
      }
      if (this.shape) {
        name.push("w-button-" + this.shape);
      }
      if (this.iconOnlyClass) {
        name.push("icon-only");
      }
      if (this.disabled) {
        name.push("w-button-disabled");
      }
      if (this.ghost) {
        name.push("w-button-ghost");
      }
      if (this.status && ["success", "info", "warning", "danger"].indexOf(this.status) > -1) {
        name.push("w-button-" + this.status);
      }
      if (this.newline && this.icon && !this.iconOnlyClass) {
        name.push("w-button-newline");
      }
      return name.join(" ");
    },
    iconOnlyClass() {
      if (this.iconOnly) {
        return this.iconOnly;
      } else if (this.icon) {
        let prefix = this.$slots.prefix != undefined;
        let suffix = this.$slots.suffix != undefined;
        if (!prefix && !suffix) {
          if (this.$slots.default) {
            if (!this.$slots.default[0].text) {
              return true;
            }
          } else if (this.textHidden || !this.text) {
            return true;
          }
        }
      }
      return false;
    },
    // 图片式图标
    iconUrl() {
      if (this.icon && this.icon.startsWith("/")) {
        if (this.icon.startsWith("/static/")) {
          return this.icon;
        } else {
          return storage.fillAccessResourceUrl(this.icon);
        }
      } else if (this.icon && this.icon.startsWith("http")) {
        return this.icon;
      }
      return "";
    },
    // icon存在且不是图片式图标，就是正常图标
    iconName() {
      if (this.icon && (!this.iconUrl || (this.iconUrl && this.imageError))) {
        return this.iconConfig.iconClass;
      }
      return "";
    },
    iconSize() {
      if (this.size) {
        if (this.size == "small") {
          return 14;
        } else if (this.size == "large") {
          return 18;
        }
      }
      return 16;
    },
  },
  data() {
    let iconJson;
    if (this.icon && utils.isValidJSON(this.icon)) {
      iconJson = JSON.parse(this.icon);
    }
    return {
      iconJson,
      imageError: false,
    };
  },
  methods: {
    onTap(e) {
      if (!this.disabled) {
        this.$emit("click", e);
      }
    },
    errorHandle(e) {
      this.imageError = true;
    },
  },
};
</script>
<style lang="scss" scoped></style>
