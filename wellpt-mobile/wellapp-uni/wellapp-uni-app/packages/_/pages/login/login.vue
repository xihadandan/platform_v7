<template>
  <view class="content" :style="theme">
    <w-login
      :system="system"
      @updateSystem="updateSystem"
      :systemSelectable="showSystemSelect"
      :showLoginSettings="showLoginSetting"
    ></w-login>
    <w-upgrade-center></w-upgrade-center>
  </view>
</template>

<script>
/**
 * 登录页面
 * imgUrl: logo图片地址
 * title: logo标题
 * loginBg: 登录页面背景图片组件
 */
import { storage } from "wellapp-uni-framework";
export default {
  data() {
    return {
      system: undefined,
      showSystemSelect: process.env.VUE_APP_HIDE_APP_SYSTEM_SELECT !== "true",
      showLoginSetting: process.env.VUE_APP_HIDE_APP_LOGIN_SETTING !== "true",
    };
  },
  components: {},
  onLoad(options) {
    // 由地址参数获取登录的系统标志
    if (options.system_id != undefined) {
      this.system = options.system_id;
    }
    if (options.showSystemSelect != undefined) {
      this.showSystemSelect = options.showSystemSelect == "true";
    }
    if (options.showAppLoginSetting != undefined) {
      this.showLoginSetting = options.showAppLoginSetting == "true";
    }
  },
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    updateSystem() {
      this.system = storage.getSystem();
    },
    // 标题处理
    titleFormat() {
      return this.$t("loginComponent.logoTitle", "一体化办公助手");
    },
  },
};
</script>
