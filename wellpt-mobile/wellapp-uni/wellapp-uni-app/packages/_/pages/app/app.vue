<template>
  <page-meta :page-style="'overflow:' + pageOverflow"></page-meta>
  <view :style="theme" class="content">
    <uni-nav-bar
      class="pt-nav-bar"
      v-if="customNavBar"
      :statusBar="true"
      :border="false"
      :shadow="false"
      :fixed="true"
      color="black"
      left-icon="left"
      :title="navBarTitle"
      @clickLeft="goBack"
    ></uni-nav-bar>
    <w-app-page
      v-if="appPiPath != '' || widgetDefId != '' || pageId != '' || pageUuid != ''"
      :appPiPath="appPiPath"
      :pageUuid="pageUuid"
      :pageId="pageId"
      :navBarTitle="title"
      :widgetDefId="widgetDefId"
    ></w-app-page>
  </view>
</template>

<script>
import { mapMutations, mapState } from "vuex";
import { isEmpty } from "lodash";
export default {
  computed: mapState(["customNavBar", "navBarTitle", "pageOverflow"]),
  data() {
    this.setCustomTabBar(false);
    return {
      appPiPath: "",
      pageUuid: "",
      widgetDefId: "",
      pageId: "",
      title: "",
    };
  },
  onLoad: function (options) {
    console.log("app/app onLoad");
    const _self = this;
    for (var key of ["appPiPath", "pageUuid", "pageId", "widgetDefId"]) {
      _self[key] = options[key] || "";
    }
    _self.title = options.redirectPageTitle ? decodeURI(options.redirectPageTitle) : "";
    this.setPageOptions(options);
  },
  onShow: function () {
    const _self = this;
    console.log("app page show");
    let appPage = "";
    // 组件定义加载的页面ID
    if (!isEmpty(_self.widgetDefId)) {
      appPage = "widgetDefId_" + _self.widgetDefId;
    } else {
      appPage = _self.appPiPath + "_" + (_self.pageUuid || _self.pageId);
    }
    // _self.setCustomNavBar(true);
    _self.setCurrentAppPage(appPage);
    _self.updateNavBarTitleByAppPage(appPage);
    _self.setPageOverFlow(); //默认页面可以滚动
  },
  methods: {
    ...mapMutations([
      "setCustomNavBar",
      "setCurrentAppPage",
      "updateNavBarTitleByAppPage",
      "setCustomTabBar",
      "setPageOverFlow",
      "setPageOptions",
    ]),
    goBack() {
      uni.navigateBack({
        delta: 1,
      });
    },
  },
};
</script>

<style scoped>
.content {
  padding: 0;
  width: 100%;
}
</style>
