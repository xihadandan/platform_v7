<template>
  <uni-popup ref="popup" type="bottom" border-radius="16px 16px 0 0" :background-color="'#ffffff'">
    <view class="popup-container">
      <view class="popop-title">
        <view class="center">
          <text>{{ parent.$t("WidgetFormFileUpload.fileSource.myMaterials", "我的材料") }}</text>
        </view>
        <view class="right">
          <uni-w-button type="text" @click="onClose" icon="iconfont icon-ptkj-dacha-xiao"></uni-w-button>
        </view>
      </view>
      <view class="popup-content" style="height: 80vh">
        <w-table
          v-if="widget"
          ref="widgetTable"
          v-show="isShow"
          :widget="widget"
          style="height: 100%"
          @getDataSourceProvider="getDataSourceProvider"
          @buttonClick="buttonClick"
        ></w-table>
      </view>
    </view>
  </uni-popup>
</template>
<script>
import { appContext, utils } from "wellapp-uni-framework";

export default {
  name: "MyMaterialFileSource",
  props: {
    parent: Object,
  },

  data() {
    return {
      widget: undefined,
      isShow: false,
    };
  },
  mounted() {},
  methods: {
    getWidget() {
      let widgetTableId = "UVNVYsHdBYpWxXhSHFAPGXxOqrCOSmUx";
      appContext.getWidget(this, widgetTableId).then((definition) => {
        this.widget = definition;
        uni.hideLoading();
        setTimeout(() => {
          this.isShow = true;
        }, 500);
      });
    },
    showPopup() {
      this.$refs.popup.open();
      if (!this.widget) {
        this.getWidget();
      } else {
        setTimeout(() => {
          this.isShow = true;
        }, 500);
      }
    },
    onClose() {
      this.$refs.popup.close();
      this.isShow = false;
    },
    getDataSourceProvider(args) {
      if (args[0] && args[0].options && args[0].options.renderers.length == 0) {
        args[0].options.renderers.push({
          columnIndex: "repoFileUuids",
          param: {
            rendererType: "materialFileDataStoreRender",
          },
        });
      }
    },
    buttonClick(args) {
      let button = args[0],
        selectedRows = args[1];
      if (button.title == "确定") {
        let logicFileInfos = [];
        selectedRows.forEach((row) => {
          if (row.logicFileInfos && Array.isArray(row.logicFileInfos)) {
            row.logicFileInfos.forEach((item) => {
              logicFileInfos.push(item);
            });
          }
        });
        if (logicFileInfos.length == 0) {
          uni.$ptToastShow({
            title: this.parent.$t("WidgetFormFileUpload.fileSource.selectMaterial", "请选择要上传的材料附件！"),
          });
          return;
        }
        this.$emit("confirm", logicFileInfos);
        this.onClose();
      }
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
