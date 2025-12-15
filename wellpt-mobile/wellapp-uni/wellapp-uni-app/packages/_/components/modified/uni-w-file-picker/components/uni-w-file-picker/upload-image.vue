<template>
  <view class="uni-file-picker__container">
    <view class="file-picker__box" v-for="(item, index) in filesList" :key="index" :style="boxStyle">
      <view class="file-picker__box-content" :style="borderStyle">
        <image class="file-image" :src="item.url" mode="aspectFill" @click.stop="prviewImage(item, index)"></image>
        <view v-if="delIcon && !readonly" class="icon-del-box" @click.stop="delFile(index)">
          <view class="icon-del"></view>
          <view class="icon-del rotate"></view>
        </view>
        <view v-if="downloadIcon && !readonly" class="icon-download-box" @click.stop="downloadImage(item, index)">
          <w-icon icon="arrow-down" color="var(--w-color-white)" />
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
    <view v-if="showAddIcon" class="file-picker__box" :style="boxStyle">
      <view class="file-picker__box-content is-add" @click="choose">
        <slot>
          <w-icon icon="iconfont icon-ptkj-jiahao" :size="24"></w-icon>
          <view class="add-text">{{ $t("WidgetFormFileUpload.clickUpload", "点击上传") }}</view>
        </slot>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: "uploadImage",
  emits: ["uploadFiles", "choose", "delFile", "choosePictureForLib"],
  props: {
    filesList: {
      type: Array,
      default() {
        return [];
      },
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    disablePreview: {
      type: Boolean,
      default: false,
    },
    limit: {
      type: [Number, String],
    },
    imageStyles: {
      type: Object,
      default() {
        return {
          width: "auto",
          height: "auto",
          border: {},
        };
      },
    },
    delIcon: {
      type: Boolean,
      default: true,
    },
    readonly: {
      type: Boolean,
      default: false,
    },
    addIcon: {
      type: Boolean,
      default: true,
    },
    downloadIcon: {
      type: Boolean,
      default: true,
    },
    showProgress: Boolean,
    pictureForLib: Boolean, //图片库上传
  },
  computed: {
    showAddIcon() {
      if (!this.addIcon) {
        return this.addIcon;
      }
      // if (this.limit) {
      //   return this.filesList.length < this.limit && !this.readonly;
      // }
      return true;
    },
    styles() {
      let styles = {
        width: "auto",
        height: "auto",
        border: {},
      };
      return Object.assign(styles, this.imageStyles);
    },
    boxStyle() {
      const { width = "auto", height = "auto" } = this.styles;
      let obj = {};
      if (height === "auto") {
        if (width !== "auto") {
          obj.height = this.value2px(width);
          obj["padding-top"] = 0;
        } else {
          obj.height = 0;
        }
      } else {
        obj.height = this.value2px(height);
        obj["padding-top"] = 0;
      }

      if (width === "auto") {
        if (height !== "auto") {
          obj.width = this.value2px(height);
        } else {
          obj.width = "33.3%";
        }
      } else {
        obj.width = this.value2px(width);
      }

      let classles = "";
      for (let i in obj) {
        classles += `${i}:${obj[i]};`;
      }
      return classles;
    },
    borderStyle() {
      let { border } = this.styles;
      let obj = {};
      const widthDefaultValue = 1;
      const radiusDefaultValue = 4;
      if (typeof border === "boolean") {
        obj.border = border ? "1px var(--w-border-color-mobile) solid" : "none";
      } else {
        let width = (border && border.width) || widthDefaultValue;
        width = this.value2px(width);
        let radius = (border && border.radius) || radiusDefaultValue;
        radius = this.value2px(radius);
        obj = {
          "border-width": width,
          "border-style": (border && border.style) || "solid",
          "border-color": (border && border.color) || "var(--w-border-color-mobile)",
          "border-radius": radius,
        };
      }
      let classles = "";
      for (let i in obj) {
        classles += `${i}:${obj[i]};`;
      }
      return classles;
    },
  },
  methods: {
    uploadFiles(item, index) {
      this.$emit("uploadFiles", item);
    },
    choose() {
      if (this.pictureForLib) {
        this.$emit("choosePictureForLib");
      } else {
        this.$emit("choose");
      }
    },
    delFile(index) {
      this.$emit("delFile", index);
    },
    downloadImage(item, index) {
      this.$emit("downloadImage", {
        index,
        item,
      });
    },
    prviewImage(img, index) {
      // #ifdef H5
      this.addImageStyleByWeb();
      // #endif
      let urls = [];
      if (Number(this.limit) === 1 && this.disablePreview && !this.disabled) {
        this.$emit("choose");
      }
      if (this.disablePreview) return;
      this.filesList.forEach((i) => {
        urls.push(i.url);
      });

      uni.previewImage({
        urls: urls,
        current: index,
        longPressActions: {
          itemList: ["发送给朋友", "保存图片", "收藏"],
          success: function (data) {
            console.log("选中了第" + (data.tapIndex + 1) + "个按钮,第" + (data.index + 1) + "张图片");
          },
          fail: function (err) {
            console.log(err.errMsg);
          },
        },
      });
    },
    addImageStyleByWeb() {
      const styleEl = document.querySelector("#file-image-style");
      // if (this.downloadIcon) {
      //   if (styleEl) {
      //     styleEl.remove();
      //   }
      //   return;
      // }
      if (styleEl) {
        // 存在不再添加
        return;
      }
      let temp = document.createElement("style");
      temp.id = "file-image-style";
      (document.head || document.body).appendChild(temp);
      let css = `
        .image-view-view img.image-view-img {
          pointer-events: none;
        }
      `;
      temp.innerHTML = css; // 禁止长按保存图片
    },
    value2px(value) {
      if (typeof value === "number") {
        value += "px";
      } else {
        if (value.indexOf("%") === -1) {
          value = value.indexOf("px") !== -1 ? value : value + "px";
        }
      }
      return value;
    },
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
  },
};
</script>

<style lang="scss">
.uni-file-picker__container {
  /* #ifndef APP-NVUE */
  display: flex;
  box-sizing: border-box;
  /* #endif */
  flex-wrap: wrap;
  margin: -5px;
}

.file-picker__box {
  position: relative;
  // flex: 0 0 33.3%;
  width: 33.3%;
  height: 0;
  padding-top: 33.33%;
  /* #ifndef APP-NVUE */
  box-sizing: border-box;
  /* #endif */
}

.file-picker__box-content {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  margin: 5px;
  border-radius: 4px;
  overflow: hidden;
}

.file-picker__progress {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  /* border: 1px red solid; */
  z-index: 2;
}

.file-picker__progress-item {
  width: 100%;
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
  font-size: 12px;
  background-color: rgba(0, 0, 0, 0.4);
}

.file-image {
  width: 100%;
  height: 100%;
}

.is-add {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  align-items: center;
  justify-content: center;
  flex-direction: column;
  border: 1px var(--w-border-color-darker) dashed;
  color: var(--w-text-color-light);

  .add-text {
    font-size: var(--w-font-size-base);
  }
}

.icon-add {
  width: 50px;
  height: 5px;
  background-color: #f1f1f1;
  border-radius: 2px;
}

.rotate {
  position: absolute;
  transform: rotate(90deg);
}

.icon-del-box {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  align-items: center;
  justify-content: center;
  position: absolute;
  top: 3px;
  right: 3px;
  height: 26px;
  width: 26px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 2;
  transform: rotate(-45deg);
}

.icon-download-box {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  align-items: center;
  justify-content: center;
  position: absolute;
  bottom: 3px;
  right: 3px;
  height: 26px;
  width: 26px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 2;
}

.icon-del {
  width: 15px;
  height: 2px;
  background-color: #fff;
  border-radius: 2px;
}
</style>
