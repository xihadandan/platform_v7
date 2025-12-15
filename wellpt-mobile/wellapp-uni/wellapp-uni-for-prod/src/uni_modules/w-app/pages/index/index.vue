<template>
  <view class="content" :style="theme">
    <w-app-page
      v-if="hasLogin"
      :appPiPath="appPiPath"
      :pageUuid="pageUuid"
      :workbench="enableWorkbench"
      :workbenchPath="workbenchPath"
    ></w-app-page>
    <view v-if="!hasLogin" class="hello">
      <view class="title">您好 游客。</view>
      <view class="ul">
        <view>在 “我的” 中点击 “登录” 可以 “登录您的账户”</view>
      </view>
    </view>
  </view>
</template>

<script>
import { mapState, mapMutations } from "vuex";
import { univerifyLogin } from "@/common/univerify.js";
import { storage } from "wellapp-uni-framework";

export default {
  data() {
    return {
      appPiPath: "/pt_mobile_demo/pt_mobile_demo_home",
      pageUuid: "",
      enableWorkbench: true,
      workbenchPath: "/pages/main/main",
    };
  },
  computed: mapState(["forcedLogin", "hasLogin", "userName"]),
  onLoad() {
    uni.showTabBar();
    const loginType = uni.getStorageSync("login_type");
    if (loginType === "local") {
      this.login(storage.getUsername());
      return;
    }
    this.guideToLogin();
  },
  onShow() {
    console.log("onShow");
    this.setCustomNavBar(false);
  },
  methods: {
    ...mapMutations(["login", "setCustomNavBar"]),
    guideToLogin() {
      uni.showModal({
        title: "未登录",
        content: "您未登录，需要登录后才能继续",
        /**
         * 如果需要强制登录，不显示取消按钮
         */
        showCancel: !this.forcedLogin,
        success: (res) => {
          if (res.confirm) {
            univerifyLogin().catch((err) => {
              if (err === false) return;
              /**
               * 如果需要强制登录，使用reLaunch方式
               */
              if (this.forcedLogin) {
                uni.reLaunch({
                  url: "../login/login",
                });
              } else {
                uni.navigateTo({
                  url: "../login/login",
                });
              }
            });
          }
        },
      });
    },
  },
};
</script>

<style>
.content {
  padding: 0;
}
.hello {
  display: flex;
  flex: 1;
  flex-direction: column;
}

.title {
  color: #8f8f94;
  margin-top: 25px;
}

.ul {
  font-size: 15px;
  color: #8f8f94;
  margin-top: 25px;
}

.ul > view {
  line-height: 25px;
}
</style>
