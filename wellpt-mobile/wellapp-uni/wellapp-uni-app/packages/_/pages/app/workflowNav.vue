<template>
  <view :style="theme" class="content">
    <view class="workflow_nav_content">
      <w-app-page
        v-if="appPiPath != '' || widgetDefId != '' || pageId != ''"
        :appPiPath="appPiPath"
        :pageUuid="pageUuid"
        :pageId="pageId"
        :widgetDefId="widgetDefId"
      ></w-app-page>
    </view>
  </view>
</template>

<script>
import { mapMutations } from "vuex";
import { isEmpty } from "lodash";
export default {
  data() {
    this.setCustomTabBar(false);
    return {
      appPiPath: "",
      pageUuid: "",
      widgetDefId: "",
      pageId: "",
    };
  },
  onLoad: function (options) {
    const _self = this;
    for (var key of ["appPiPath", "pageUuid", "pageId", "widgetDefId"]) {
      _self[key] = options[key] || "";
    }
  },
  onShow: function () {
    const _self = this;
    console.log("app page show");
    let appPage = "";
    // 组件定义加载的页面ID
    if (!isEmpty(_self.widgetDefId)) {
      appPage = "widgetDefId_" + _self.widgetDefId;
    } else {
      appPage = _self.appPiPath + "_" + _self.pageUuid || _self.pageId;
    }
  },
  methods: {
    ...mapMutations([
      "setCustomNavBar",
      "setCurrentAppPage",
      "updateNavBarTitleByAppPage",
      "setCustomTabBar",
      "setNavBarTitle",
    ]),
  },
};
</script>

<style scoped lang="scss">
.content {
  padding: 0;
  width: 100%;

  .workflow_nav_content {
    padding: 40px 10px 0 10px;
    background: linear-gradient(#488cee 0%, #ffffff 50%);

    ::v-deep .w-grid-view {
      border-radius: 8px;
    }
  }
}
</style>
