<template>
  <page-meta :page-style="'overflow:' + pageOverflow"></page-meta>
  <view :style="theme" class="welldo-index-content">
    <uni-nav-bar
      class="pt-nav-bar"
      v-if="customNavBar"
      :statusBar="true"
      :border="false"
      :shadow="false"
      :fixed="true"
      color="black"
      :title="navBarTitle"
      @clickLeft="goBack"
    ></uni-nav-bar>
    <!-- <uni-w-easyinput type="textarea" v-model="cesUrl"></uni-w-easyinput
    ><uni-w-button text="跳转" @click="redirect(cesUrl)"></uni-w-button> -->
    <template v-if="this.loginType == 'local' && this._$USER">
      <w-app-page
        v-if="appPiPath != '' || widgetDefId != '' || pageId != ''"
        :appPiPath="appPiPath"
        :pageUuid="pageUuid"
        :pageId="pageId"
        :widgetDefId="widgetDefId"
      ></w-app-page>
    </template>
  </view>
</template>

<script>
import { mapMutations, mapState } from "vuex";
import { storage } from "wellapp-uni-framework";
import { isEmpty } from "lodash";
export default {
  computed: mapState(["customNavBar", "navBarTitle", "pageOverflow"]),
  data() {
    this.setCustomTabBar(false);
    return {
      appPiPath: "",
      pageUuid: "",
      widgetDefId: "",
      pageId: "page_20250224135518",
      loginType: undefined,
    };
  },
  onLoad: function (options) {
    const _self = this;
    // for (var key of ["appPiPath", "pageUuid", "pageId", "widgetDefId"]) {
    //   _self[key] = options[key] || "";
    // }
  },
  onShow: function () {
    const _self = this;
    console.log("app page show");
    let appPage = "";
    this.loginType = uni.getStorageSync("login_type");
    if (this.loginType == "local" && this._$USER) {
      this.login(storage.getUsername());
    } else {
      uni.reLaunch({
        url: "/packages/_/pages/login/login",
      });
    }
    // 组件定义加载的页面ID
    if (!isEmpty(_self.widgetDefId)) {
      appPage = "widgetDefId_" + _self.widgetDefId;
    } else {
      appPage = _self.appPiPath + "_" + (_self.pageUuid || _self.pageId);
    }
    _self.setCustomNavBar(true);
    _self.setCurrentAppPage(appPage);
    _self.updateNavBarTitleByAppPage(appPage);
    _self.setPageOverFlow(); //默认页面可以滚动
  },
  methods: {
    ...mapMutations([
      "login",
      "setCustomNavBar",
      "setCurrentAppPage",
      "updateNavBarTitleByAppPage",
      "setCustomTabBar",
      "setPageOverFlow",
    ]),
    goBack() {
      uni.navigateBack({
        delta: 1,
      });
    },
    redirect(url) {
      uni.navigateTo({
        url,
      });
    },
  },
};
</script>

<style scoped lang="scss">
.welldo-index-content {
  .w-app-page {
    padding: var(--w-padding-2xs) var(--w-padding-xs) var(--w-padding-md);
    width: auto;
    ::v-deep .w-swiper {
      margin-bottom: var(--w-margin-xs);
      background: transparent;
    }
    ::v-deep .w-card.uni-card {
      margin-bottom: var(--w-margin-xs) !important;
      padding: 0 !important;
      border-radius: 8px;
      box-shadow: rgba(0, 0, 0, 0.02) 0px 0px 8px 2px !important;
      .uni-section {
        border-bottom-color: transparent;

        .uni-section-header {
          padding: var(--w-padding-xs) var(--w-padding-xs) var(--w-padding-3xs);
        }

        .uni-section__content-title {
          color: var(--w-text-color-mobile) !important;
          font-size: var(--w-font-size-lg) !important;
          font-weight: bold;
        }
      }

      .uni-card__content {
        padding: 0 !important;
      }
    }
  }
}
</style>
