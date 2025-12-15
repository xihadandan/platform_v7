<template>
  <uni-popup ref="popup" type="bottom" border-radius="16px 16px 0 0" :background-color="'#ffffff'">
    <view class="popup-container">
      <view class="popop-title">
        <view class="center">
          <text>{{ $t("WidgetFormFileUpload.addPicture", "添加图片") }}</text>
        </view>
        <view class="right">
          <uni-w-button type="text" @click="onClose" icon="iconfont icon-ptkj-dacha-xiao"></uni-w-button>
        </view>
      </view>
      <view class="popup-content" style="height: 80vh">
        <template v-if="isShow">
          <!-- <uni-w-tabs :list="tabs" :current="current" equalWidth itemMaxWidth="120"></uni-w-tabs> -->
          <template v-if="current == 0">
            <pictureLibCategory
              ref="categoryRef"
              :pictureLibUuids="pictureLibUuids"
              @change="onLibSelectChange"
            ></pictureLibCategory>
          </template>
        </template>
        <view style="padding: var(--w-padding-2xs) var(--w-padding-xs)" v-if="selectedCount">
          <uni-w-button-group :buttons="buttons" :gutter="16" @click="onTrigger"></uni-w-button-group>
        </view>
      </view>
    </view>
  </uni-popup>
</template>
<script>
import { appContext, utils } from "wellapp-uni-framework";
import pictureLibCategory from "./picture-lib-category.vue";

export default {
  name: "PictureLibPicker",
  components: {
    pictureLibCategory,
  },

  props: {
    pictureLibUuids: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      isShow: false,
      current: 0,
      tabs: [
        { name: this.$t("WidgetFormFileUpload.pictureLibrary", "图片库") },
        { name: this.$t("WidgetFormFileUpload.localUpload", "本地上传") },
      ],
      selectedCount: 0, //选中图片数量
    };
  },
  computed: {
    buttons() {
      let btns = [
        {
          title: this.$t("global.clear", "清空"),
          code: "onClear",
        },
      ];
      let confirmTitle = this.$t("WidgetFormFileUpload.confirm", "确定");
      if (this.selectedCount) {
        confirmTitle += `(${this.selectedCount})`;
      }
      btns.push({
        title: confirmTitle,
        code: "onOk",
        type: "primary",
      });
      return btns;
    },
  },
  mounted() {},
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    showPopup() {
      this.$refs.popup.open();
      this.isShow = true;
      this.$nextTick(() => {
        this.onClear();
      });
    },
    onClose() {
      this.$refs.popup.close();
      this.isShow = false;
    },
    onTrigger(e, button) {
      if (button && this[button.code]) {
        this[button.code]();
      }
    },
    onClear() {
      this.$refs.categoryRef.clearSelectPicture();
    },
    onOk() {
      this.$refs.categoryRef.confirmSelectPicture((files) => {
        this.$emit("confirm", files);
      });
    },
    onLibSelectChange(ids) {
      this.selectedCount = ids.length;
    },
  },
};
</script>
<style lang="scss" scoped>
.popup-container {
  .popop-title {
    padding: 14px 5rem 8px;
    font-size: var(--w-font-size-base);
    display: flex;
    align-items: center;
    justify-content: space-between;
    position: relative;

    .left {
      position: absolute;
      left: 10px;
    }

    .center {
      flex: 1;
      text-align: center;
      font-size: var(--w-font-size-lg);
      color: $uni-main-color;
      font-weight: bold;
    }

    .right {
      position: absolute;
      right: 10px;
    }
  }
}
</style>
