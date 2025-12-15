<!-- 附件列表弹框内容 -->
<template>
  <view class="popop-content">
    <uni-w-simple-list-upload
      :fileIds="options.files"
      title=""
      ref="filePicker"
      :downloadAllType="options.downloadAllType"
      :fileNameRepeat="options.allowNameRepeat"
      :editable="options.editable || false"
      :headerButtons="options.headerButtons || headerButtons"
      :buttons="options.buttons"
      @change="onFileChange"
      @actionTap="onActionTap"
      @actionTapAfter="actionTapAfter"
    >
    </uni-w-simple-list-upload>
  </view>
</template>
<script>
import { isFunction } from "lodash";
export default {
  name: "PopupFileList",
  props: {
    options: Object,
  },
  components: {},
  computed: {},
  data() {
    return {
      headerButtons: [
        {
          buttonName: this.$t("WidgetFormFileUpload.button.onClickAllDownload", "全部下载"),
          title: this.$t("WidgetFormFileUpload.button.onClickAllDownload", "全部下载"),
          type: "primary",
          size: "default",
          block: true,
          code: "onClickAllDownload",
          btnShowType: "show",
          icon: "pticon iconfont icon-ptkj-xiazai",
        },
      ],
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    $t() {
      if (this.$i18n) {
        return this.$i18n.$t(this, ...arguments);
      }
      return arguments[1];
    },

    onFileChange() {
      let _this = this;
      if (isFunction(_this.options.onFileChange)) {
        _this.options.onFileChange();
      }
    },
    closePopup() {
      let _this = this;
      if (isFunction(_this.options.onClosePopup)) {
        _this.options.onClosePopup();
      }
      this.$emit("close");
    },
    onActionTap(e, button) {
      let _this = this;
      if (isFunction(_this.options[button.code])) {
        _this.options[button.code](e);
      }
    },
    actionTapAfter(e, button) {
      if (button.toClosePopup !== false) {
        this.$emit("close");
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.popop-content {
  padding: 0 12px 20px;
  max-height: 70vh;
  overflow: auto;
}
</style>
