<template>
  <view class="uni-file-picker__files">
    <slot name="description"></slot>
    <view v-if="!readonly" class="files-button" @click="choose">
      <slot></slot>
    </view>
    <!-- :class="{'is-text-box':showType === 'list'}" -->
    <view v-if="list.length > 0" class="uni-file-picker__lists is-text-box" :style="borderStyle">
      <!-- ,'is-list-card':showType === 'list-card' -->
      <view
        class="uni-file-picker__lists-box"
        v-for="(item, index) in list"
        :key="index"
        :class="{
          'files-border': index !== 0 && styles.dividline,
        }"
        :style="[index !== 0 && styles.dividline && borderLineStyle, styles.boxStyle]"
      >
        <view class="uni-file-picker__item" :style="styles.itemStyle">
          <view v-if="item.icon" :class="'files__icon f_s_0'" :style="{ backgroundColor: item.iconBgColor }">
            <w-icon :size="20" :icon="item.icon"></w-icon>
          </view>
          <view class="f_g_1">
            <view class="files__name w-ellipsis-1">{{ item.name }}</view>
            <view class="files__size">{{ item.formatSize }}</view>
          </view>
          <view class="flex f_s_0">
            <!-- 删除按钮 -->
            <uni-w-button
              v-if="delIcon && !readonly"
              icon="iconfont icon-ptkj-shanchu"
              size="small"
              type="link"
              @click="delFile(index)"
            ></uni-w-button>
            <!-- 其他按钮 -->
            <uni-w-button-group
              :buttons="buttons"
              size="small"
              type="link"
              @click="buttonClick($event, item, index)"
              :max="1"
              :moreButton="{
                title: '',
                icon: 'iconfont icon-ptkj-gengduocaozuo',
                type: 'link',
              }"
              :replaceFields="{
                type: 'type',
                size: 'size',
                icon: 'icon',
                status: 'status',
                title: 'buttonName',
              }"
            ></uni-w-button-group>
          </view>
        </view>
        <view
          v-if="showProgress && ((item.progress && item.progress !== 100) || item.progress === 0)"
          class="file-picker__progress"
        >
          <progress
            class="file-picker__progress-item"
            :percent="item.progress === -1 ? 0 : item.progress"
            stroke-width="4"
            :backgroundColor="item.errMsg ? 'var(--w-danger-color)' : 'var(--w-bg-color-mobile-th)'"
          />
        </view>
        <view v-if="item.status === 'error'" class="file-picker__mask" @click.stop="uploadFiles(item, index)">
          {{ $t("WidgetFormFileUpload.reupload", "点击重试") }}
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getFileIcon, formatSize } from "./utils.js";
export default {
  name: "uploadFile",
  emits: ["uploadFiles", "choose", "delFile"],
  props: {
    filesList: {
      type: Array,
      default() {
        return [];
      },
    },
    delIcon: {
      type: Boolean,
      default: true,
    },
    limit: {
      type: [Number, String],
      default: 9,
    },
    showType: {
      type: String,
      default: "",
    },
    listStyles: {
      type: Object,
      default() {
        return {
          // 是否显示边框
          border: false,
          // 是否显示分隔线
          dividline: false,
          // 线条样式
          borderStyle: {},
          itemStyle: {},
          boxStyle: {},
        };
      },
    },
    readonly: {
      type: Boolean,
      default: false,
    },
    buttons: {
      type: Array,
      default: [],
    },
    showProgress: Boolean,
  },
  computed: {
    list() {
      let files = [];
      this.filesList.forEach((v) => {
        let icon = getFileIcon(v.name);
        v.icon = icon.icon;
        v.iconBgColor = icon.bgColor;
        v.formatSize = formatSize(v.size || v.fileSize);
        files.push(v);
      });
      return files;
    },
    styles() {
      let styles = {
        border: true,
        dividline: true,
        "border-style": {},
      };
      return Object.assign(styles, this.listStyles);
    },
    borderStyle() {
      let { borderStyle, border } = this.styles;
      let obj = {};
      if (!border) {
        obj.border = "none";
      } else {
        let width = (borderStyle && borderStyle.width) || 1;
        width = this.value2px(width);
        let radius = (borderStyle && borderStyle.radius) || 5;
        radius = this.value2px(radius);
        obj = {
          "border-width": width,
          "border-style": (borderStyle && borderStyle.style) || "solid",
          "border-color": (borderStyle && borderStyle.color) || "var(--w-border-color-mobile)",
          "border-radius": radius,
        };
      }
      let classles = "";
      for (let i in obj) {
        classles += `${i}:${obj[i]};`;
      }
      return classles;
    },
    borderLineStyle() {
      let obj = {};
      let { borderStyle } = this.styles;
      if (borderStyle && borderStyle.color) {
        obj["border-color"] = borderStyle.color;
      }
      if (borderStyle && borderStyle.width) {
        let width = (borderStyle && borderStyle.width) || 1;
        let style = (borderStyle && borderStyle.style) || 0;
        if (typeof width === "number") {
          width += "px";
        } else {
          width = width.indexOf("px") ? width : width + "px";
        }
        obj["border-width"] = width;

        if (typeof style === "number") {
          style += "px";
        } else {
          style = style.indexOf("px") ? style : style + "px";
        }
        obj["border-top-style"] = style;
      }
      let classles = "";
      for (let i in obj) {
        classles += `${i}:${obj[i]};`;
      }
      return classles;
    },
  },

  methods: {
    onFileActionTap(event, file, fileIndex) {
      const itemList = this.buttons.map((item) => {
        if (item.defaultFlag) {
          return item.buttonName;
        }
      });
      uni.showActionSheet({
        itemList,
        success: (args) => {
          const tapIndex = args.tapIndex;
          this.$emit("actionTap", {
            tapIndex,
            button: this.buttons[tapIndex],
            file,
            fileIndex,
          });
        },
        complete: function (args) {
          console.log(args);
        },
      });
    },
    uploadFiles(item, index) {
      this.$emit("uploadFiles", {
        item,
        index,
      });
    },
    choose() {
      this.$emit("choose");
    },
    delFile(index) {
      this.$emit("delFile", index);
    },
    value2px(value) {
      if (typeof value === "number") {
        value += "px";
      } else {
        value = value.indexOf("px") !== -1 ? value : value + "px";
      }
      return value;
    },
    buttonClick($event, file, fileIndex) {
      this.$emit("actionTap", {
        button: $event.button,
        file,
        fileIndex,
      });
    },
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
  },
};
</script>

<style lang="scss">
.uni-file-picker__files {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: column;
  justify-content: flex-start;
}

.files-button {
  // border: 1px red solid;
}

.uni-file-picker__lists {
  position: relative;
  margin-top: 5px;
  overflow: hidden;
}

.file-picker__mask {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  justify-content: center;
  align-items: center;
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  left: 0;
  color: #fff;
  font-size: 14px;
  background-color: rgba(0, 0, 0, 0.4);
  border-radius: inherit;
}

.uni-file-picker__lists-box {
  position: relative;
  border-radius: 4px;
  background-color: var(--w-bg-color-mobile-th);
  margin-bottom: 8px;
}

.uni-file-picker__item {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  align-items: center;
  padding: 12px 8px;

  .files__icon {
    border-radius: 8px;
    width: 32px;
    height: 32px;
    color: #ffffff;
    background-color: var(--w-primary-color);
    margin-right: 8px;
    text-align: center;
    line-height: 32px;
  }
  .files__name {
    font-size: 14px;
    color: var(--w-text-color-mobile);
    margin-right: 16px;
    /* #ifndef APP-NVUE */
    word-break: break-all;
    word-wrap: break-word;
    /* #endif */
  }
  .files__size {
    font-size: 12px;
    color: var(--w-text-color-light);
  }
}

.files-border {
  border-top: 1px var(--w-border-color-mobile) solid;
}

.icon-files {
  /* #ifndef APP-NVUE */
  position: static;
  background-color: initial;
  /* #endif */
}

// .icon-files .icon-del {
// 	background-color: #333;
// 	width: 12px;
// 	height: 1px;
// }

.is-list-card {
  border: 1px #eee solid;
  margin-bottom: 5px;
  border-radius: 5px;
  box-shadow: 0 0 2px 0px rgba(0, 0, 0, 0.1);
  padding: 5px;
}

.files__image {
  width: 40px;
  height: 40px;
  margin-right: 10px;
}

.header-image {
  width: 100%;
  height: 100%;
}

.is-text-box {
  border: 1px #eee solid;
  border-radius: 5px;
}

.is-text-image {
  width: 25px;
  height: 25px;
  margin-left: 5px;
}

.rotate {
  position: absolute;
  transform: rotate(90deg);
}

.icon-del-box {
  /* #ifndef APP-NVUE */
  display: flex;
  margin: auto 0;
  /* #endif */
  align-items: center;
  justify-content: center;
  position: absolute;
  top: 0px;
  bottom: 0;
  right: 5px;
  height: 26px;
  width: 26px;
  // border-radius: 50%;
  // background-color: rgba(0, 0, 0, 0.5);
  z-index: 2;
  transform: rotate(-45deg);
  margin-right: 25px;
}

.icon-del {
  width: 15px;
  height: 1px;
  background-color: #333;
  // border-radius: 1px;
}

/* #ifdef H5 */
@media all and (min-width: 768px) {
  .uni-file-picker__files {
    max-width: 375px;
  }
}

/* #endif */
</style>
