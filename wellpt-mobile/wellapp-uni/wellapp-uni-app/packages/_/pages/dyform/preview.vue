<template>
  <view class="w-dyform-container" :style="theme">
    <button @click="onClickChangeForm" v-if="chooseDyformToPreview" style="margin-top: var(--status-bar-height)">
      {{ $t("global.selectForm", "选择表单") }}
    </button>
    <!-- <scroll-view style="height: 100vh; width: 100vw" scroll-y="true"> -->
    <w-dyform
      v-if="definitionJson || dyformOptions.formData || formUuid"
      class="w-dyform-preview"
      :options="dyformOptions"
      ref="wDyform"
      :key="dyformKey"
    ></w-dyform>
    <uni-w-fab-button v-if="hasSaveBtn && (definitionJson || dyformOptions.formData || formUuid)" @onTap="onSave">
      <uni-w-button type="primary">保存</uni-w-button>
    </uni-w-fab-button>
    <!-- </scroll-view> -->

    <uni-popup ref="alertDialog" type="dialog" style="width: 200px">
      <uni-popup-dialog
        :cancelText="$t('global.close', '关闭')"
        :confirmText="$t('global.confirm', '确定')"
        :title="$t('global.sure', '确认')"
        :content="$t('global.isSure', { name: $t('WidgetSubform.button.save', '保存') }, '确认要保存吗?')"
        @confirm="dialogConfirm"
      ></uni-popup-dialog>
    </uni-popup>

    <uni-popup ref="message" type="message">
      <uni-popup-message :type="msgType" :message="messageText" :duration="2000"></uni-popup-message>
    </uni-popup>
    <uni-popup ref="selectFormPopup" background-color="#fff" type="bottom">
      <view>
        <uni-search-bar
          :focus="true"
          v-model="formKeyword"
          @input="getFormDefinition"
          :placeholder="$t('global.searchplaceholder', '请输入搜索内容')"
          :cancelText="$t('global.cancel', '取消')"
        >
        </uni-search-bar>
        <view>
          <scroll-view style="height: 300px; width: 100vw" scroll-y="true">
            <uni-list>
              <template v-for="item in formSelectOptions">
                <uni-list-item
                  :clickable="true"
                  :title="item.name"
                  :key="item.id"
                  :note="item.id"
                  @click="onSelectForm(item)"
                >
                </uni-list-item>
              </template>
            </uni-list>
          </scroll-view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>
<script>
import { storage } from "wellapp-uni-framework";
// import io from "socket.io-client";

export default {
  data() {
    return {
      formKeyword: "",
      formUuid: "",
      dataUuid: "",
      definitionJson: "",
      msgType: "success",
      dyformKey: new Date().getTime(),
      chooseDyformToPreview: false,
      messageText: "",
      dyformOptions: {
        formData: null,
        formUuid: null,
        dataUuid: null,
      },
      formSelectOptions: {},
      hasSaveBtn: false, // 显示保存按钮
    };
  },
  onLoad(options) {
    var _this = this;
    if (options.formUuid) {
      _this.formUuid = options.formUuid;
      _this.dyformOptions.formUuid = _this.formUuid;
    }
    if (options.dataUuid) {
      _this.dataUuid = options.dataUuid;
      _this.dyformOptions.dataUuid = _this.dataUuid;
    }
    if (options.displayState) {
      _this.dyformOptions.displayState = options.displayState;
    }
    if (options.accessToken) {
      storage.setAccessToken(options.accessToken);
      _this.accessToken = options.accessToken;
    }
    if (options.hasSaveBtn) {
      _this.hasSaveBtn = !!options.hasSaveBtn;
    }

    if (options.origin) {
      _this.formUuid = "";
      _this.notifyReadyForParentPreview(options.origin);
    } else if (options.jsonid) {
      _this.jsonid = options.jsonid;
      _this.formUuid = "";
      _this.designerDomain = options.domain;
    }

    if (options.chooseDyform) {
      _this.chooseDyformToPreview = true;
    }
  },
  computed: {},
  created() {},
  mounted() {
    if (this.jsonid && this.designerDomain) {
      this.$axios
        .get(`${this.designerDomain}/uni-app-design-json/get`, {
          params: {
            jsonid: this.jsonid,
          },
          headers: {
            Authorization: "Bearer " + this.accessToken,
          },
        })
        .then(({ data }) => {
          this.definitionJson = data;
          this.dyformOptions.formDefinition = data;
        });

      // let socket = io("http://192.168.0.116:18069/wellapp-designer", {
      //   // transports: ["polling"],
      //   query: {
      //     jsonid: this.jsonid,
      //   },
      // });
      // let _this = this;
      // uni.showLoading();
      // socket.on("connect", function () {
      //   console.log("ws connected");
      // });

      // socket.on("connect-designer", function (res) {
      //   console.log("connect-designer", res);
      //   uni.hideLoading();
      //   if (res.jsonid == _this.jsonid) {
      //     if (res.json) {
      //       _this.definitionJson = res.json;
      //       _this.dyformOptions.formDefinition = _this.definitionJson;
      //     }
      //   }
      // });
    }
    this.getFormDefinition();
  },

  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    onSelectForm(item) {
      this.$refs.selectFormPopup.close();
      this.formUuid = item.uuid;
      this.dyformKey = item.uuid + "_" + new Date().getTime();
      this.dyformOptions.formUuid = item.uuid;
      this.formKeyword = "";
    },
    onClickChangeForm() {
      this.$refs.selectFormPopup.open();
    },
    getFormDefinition() {
      this.$axios
        .get(`/api/dyform/definition/queryFormDefinitionIgnoreJsonByKeyword`, {
          params: {
            keyword: this.formKeyword,
            pageIndex: 1,
            pageSize: 10,
          },
        })
        .then(({ data }) => {
          this.formSelectOptions = data.data;
        })
        .catch((error) => {
          console.log(error);
        });
    },
    onSave() {
      let _this = this;
      this.$refs.wDyform.collectFormData(true, function (valid, msg, formData) {
        console.log("收集表单数据", arguments);
        if (valid) {
          _this.$refs.alertDialog.open();
          _this.commitFormData = formData;
        }
      });
    },
    dialogConfirm() {
      let _this = this;
      uni.showLoading({ title: this.$t("global.saving", "保存中"), mask: true });
      this.$axios
        .post("/api/dyform/data/saveFormData", this.commitFormData.dyFormData)
        .then(({ data }) => {
          uni.hideLoading();
          if (data.code === 0) {
            _this.msgType = "success";
            _this.messageText = this.$t(
              "global.successText",
              { name: this.$t("WidgetSubform.button.save", "保存") },
              "保存成功!"
            );
            //   _this.$message.success("保存成功");
            //   if (_this.vDataUuid) {
            //     window.location.reload();
            //   } else {
            //     window.location.href += "&dataUuid=" + data.data;
            //   }
            // } else {
            //   _this.saveLoading = false;
            //   _this.$message.error("保存失败");
            //   console.error(data);
            window.parent.postMessage("uni server form data save success : " + data.data, _this.previewOrigin);
          } else {
            _this.msgType = "error";
            _this.messageText = this.$t(
              "global.failText",
              { name: this.$t("WidgetSubform.button.save", "保存") },
              "保存失败!"
            );
          }
          _this.$refs.message.open();
        })
        .catch((error) => {
          uni.hideLoading();

          // _this.$loading(false);
          // _this.saveLoading = false;
          // _this.$message.error("保存失败");
          console.error(error);
        });
    },
    notifyReadyForParentPreview(origin) {
      this.previewOrigin = origin;
      window.parent.postMessage("uni server preview ready", origin);
      let _this = this;
      window.addEventListener(
        "message",
        function (event) {
          if (event.origin !== origin) {
            return;
          }
          let definitionJson = event.data;
          if (definitionJson === "save form data") {
            _this.onSave();
            return;
          } else if (definitionJson.startsWith("change form displayState")) {
            _this.dyformOptions.displayState = definitionJson.split(":")[1].trim();
            _this.dyformKey = new Date().getTime();
            return;
          }

          _this.dyformOptions.formDefinition = definitionJson;
          _this.definitionJson = definitionJson;
          console.log("Received message from parent:", event.data);
        },
        false
      );
    },
  },
};
</script>
<style lang="scss" scoped>
.w-dyform-preview {
  width: 100vw;
  height: 100vh;
  background-color: var(--w-bg-color-mobile-bg);
}
.w-dyform-container {
  .popup-dialog {
    width: 300px;
    border-radius: 11px;
    background-color: #fff;
  }
  .w-dyform-buttons {
    display: flex;
    flex-direction: row;
    justify-content: space-around;
    align-items: center;
    background-color: #fff;
    border-top: 1px #eee solid;
    height: 45px;
    position: fixed;
    bottom: 0;
    width: 100vw;
    > .button {
      display: flex;
      flex-direction: row;
      align-items: center;
    }
  }
}
</style>
