<template>
  <view :style="theme" class="content">
    <scroll-view style="height: 100vh" scroll-y="true">
      <w-app-page v-if="appPageDefinitionJson != ''" :appPageDefinitionJson="appPageDefinitionJson"></w-app-page>
      <w-app-page
        v-else-if="appPiPath != '' || widgetDefId != ''"
        :appPiPath="appPiPath"
        :pageUuid="pageUuid"
        :widgetDefId="widgetDefId"
      ></w-app-page>
    </scroll-view>
  </view>
</template>

<script>
import { mapMutations } from "vuex";
import { storage } from "wellapp-uni-framework";
import { isEmpty } from "lodash";
export default {
  data() {
    return {
      appPiPath: "",
      pageUuid: "",
      appPageDefinitionJson: "",
      widgetDefId: "",
    };
  },
  onLoad: function (options) {
    const _this = this;
    let appPiPath = "";
    let pageUuid = "";
    let widgetDefId = "";
    let accessToken = "";
    for (var key in options) {
      if (key == "appPiPath") {
        appPiPath = options[key];
      } else if (key == "pageUuid") {
        pageUuid = options[key];
      } else if (key == "widgetDefId") {
        widgetDefId = options[key];
      } else if (key == "accessToken") {
        accessToken = options[key];
      }
    }
    _this.appPiPath = appPiPath;
    _this.pageUuid = pageUuid;
    _this.widgetDefId = widgetDefId;
    // uni.setStorageSync("uni_id_token", accessToken);
    storage.setAccessToken(accessToken);
    _this.parentOrigin = options.origin;
    _this.notifyReadyForParentPreview(options.origin);
  },
  onShow: function () {
    const _this = this;
    console.log("手机预览页面展示");
    var appPage = _this.appPiPath + "_" + _this.pageUuid;
    // 组件定义加载的页面ID
    if (!isEmpty(_this.widgetDefId)) {
      appPage = "widgetDefId_" + _this.widgetDefId;
    } else {
      appPage = _this.appPiPath + "_" + _this.pageUuid;
    }
    _this.setCustomNavBar(true);
    _this.setCurrentAppPage(appPage);
    _this.updateNavBarTitleByAppPage(appPage);
    // 设置标题栏
    _this.setNavBarTitle("手机预览");
  },
  methods: {
    notifyReadyForParentPreview(origin) {
      window.parent.postMessage("uni server preview ready", origin);
      let _this = this;
      window.addEventListener(
        "message",
        function (event) {
          if (event.origin !== origin) {
            return;
          }
          _this.appPageDefinitionJson = event.data;
          console.log("Received message from parent:", event.data);
        },
        false
      );

      var observer = new MutationObserver((mutations) => {
        for (let item of mutations) {
          if (item.type === "childList") {
            const scrollHeight = document.body.scrollHeight;
            window.parent.postMessage("uni height : " + scrollHeight, origin);
            break;
          }
        }
      });

      observer.observe(document, {
        attributes: true,
        childList: true,
        subtree: true,
      });
    },
    ...mapMutations(["setCustomNavBar", "setCurrentAppPage", "updateNavBarTitleByAppPage", "setNavBarTitle"]),
  },
  mounted() {
    window.parent.postMessage("uni mounted", this.parentOrigin);
  },
};
</script>

<style scoped>
.content {
  width: 100vw;
  height: 100vh;
  padding: 0;
}
</style>
