<template>
  <view class="content" :style="theme">
    <u-loading-page :loading="loading"></u-loading-page>
    <w-app-page v-if="hasLogin && !loading" :appPageDefinitionJson="appPageDefinitionJson"></w-app-page>
    <u-empty mode="page" v-if="!loading && appPageDefinitionJson == undefined" icon="/static/images/page_404.png" />
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
      appPageDefinitionJson: undefined,
      loading: true,
    };
  },
  computed: {
    ...mapState(["forcedLogin", "hasLogin", "userName"]),
  },
  onLoad(options) {
    uni.showTabBar();
    const loginType = uni.getStorageSync("login_type");
    if (loginType === "local") {
      this.login(storage.getUsername());
      return;
    }
    if (options.system) {
      this.system = options.system;
    }
    this.guideToLogin();
  },
  created() {
    if (this._$USER != undefined && this._$SYSTEM_ID) {
      this.getSystemAuthenticatePage(this._$SYSTEM_ID, this._$USER.tenantId);
    }
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
    getSystemAuthenticatePage(system, tenant) {
      this.loading = true;
      this.$axios
        .get(`/webapp/systemAuthenticatePage`, {
          params: {
            system,
            tenant,
          },
        })
        .then(({ data }) => {
          console.log("获取系统下的授权主页", data);
          this.loading = false;
          if (data.data) {
            this.appPageDefinitionJson = data.data.definitionJson;
          }
        })
        .catch((error) => {
          this.loading = false;
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
