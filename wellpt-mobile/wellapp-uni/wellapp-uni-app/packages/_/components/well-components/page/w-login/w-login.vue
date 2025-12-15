<template>
  <view class="w-login">
    <slot name="login-header"> </slot>
    <slot name="login-body">
      <RenderDevelopTemplate :isComponent="loginBg">
        <view class="login-bg-header">
          <image v-if="imgUrl" :src="imgUrl" class="image"></image>
        </view>
        <view class="w-login-form-container">
          <view class="w-login-form-body" :style="{ minHeight: formMinHeight }">
            <view v-if="title" class="title">{{ title }}</view>
            <wLoginForm
              :login-way="currentLoginWay"
              :show-image-verify-code="showImageVerifyCode"
              :show-switch-login-way="isShowSwitchLoginWay"
              :show-login-settings="showLoginSettings"
              :univerify-api-key="univerifyApiKey"
              :univerify-api-secret="univerifyApiSecret"
              :univerify-cloud-function-name="univerifyCloudFunctionName"
              :inputBorder="inputBorder"
              :formLabelHidden="formLabelHidden"
              :loginSettingsPageUrl="loginSettingsPageUrl"
              :mainPageUrl="mainPageUrl"
              :system="system"
              :enableI18nLanguage="enableI18nLanguage && languageOptions.length > 0"
              :languageText="selectedLanguageText"
              :captchaOptions="captchaOptions"
              @showSwitchLoginWayPopup="onShowSwitchLoginWayClick"
              @showLocalePopup="onChangeLocaleClick"
              @showLoginSettingsPopup="onPopupSettingOpen"
            />
          </view>
        </view>
      </RenderDevelopTemplate>
    </slot>
    <!-- 弹窗内容 -->
    <uni-popup ref="popup" background-color="#ffffff" borderRadius="16px 16px 0 0">
      <w-popup-select
        class="switch-login-way-popup"
        :title="$t('loginComponent.selectLoginType', '选择登录方式')"
        :items="loginWayItems"
        @close="onSwitchLoginWayClose"
        @ok="onSwitchLoginWayOk"
      ></w-popup-select>
    </uni-popup>
    <!-- locale弹窗内容 -->
    <uni-popup ref="popupLocale" background-color="#ffffff" borderRadius="16px 16px 0 0">
      <w-popup-select
        class="switch-login-way-popup"
        :title="$t('loginComponent.selectLanguage', '选择语言')"
        :items="languageOptionItems"
        @close="onSwitchLocaleClose"
        @ok="onSwitchLocale"
      ></w-popup-select>
    </uni-popup>
    <!-- 登录设置弹窗内容 -->
    <uni-popup ref="popupSetting" background-color="#ffffff" borderRadius="16px 16px 0 0">
      <view class="pt-popop-title">
        <view class="left"></view>
        <view class="center">
          <text>{{ $t("loginComponent.loginSettings", "登录设置") }}</text>
        </view>
        <view class="right">
          <uni-w-button type="text" @click="onPopupSettingClose" icon="iconfont icon-ptkj-dacha-xiao"></uni-w-button>
        </view>
      </view>
      <loginSetting :showNavBar="false" :systemSelectable="systemSelectable" @ok="onPopupSettingOk" />
    </uni-popup>
  </view>
</template>

<script>
import { storage, device, utils } from "wellapp-uni-framework";
import { trim as stringTrim, isEmpty, each as forEach, findIndex } from "lodash";
import wLoginForm from "./w-login-form.vue";
import loginSetting from "@/packages/_/pages/login/login_settings.vue";
import logo from "./resource/wellInfo-logo.svg";
const captchaOptions = {
  deviceId: device.getDeviceUUID(),
  scene: "login",
  timestamp: new Date().getTime(),
};
export default {
  props: {
    system: {
      type: String,
    },
    // 登录方式，用户名密码usrPwd、用户名手机号密码usrPhonePwd，短信验证码sms
    loginWay: {
      type: String,
      default: "usrPwd",
    },
    // 是否显示图形验证码，对登录方式为usrPwd、usrPhonePwd时有效
    showImageVerifyCode: {
      type: Boolean,
      default: false,
    },
    // 是否显示切换登录方式
    showSwitchLoginWay: {
      type: Boolean,
      default: true,
    },
    // 是否显示登录设置
    showLoginSettings: {
      type: Boolean,
      default: true,
    },
    // 本机一键登录apiKey
    univerifyApiKey: {
      type: String,
      default: "", // 71d59321236dd317afa728f7353a7bfe
    },
    // 本机一键登录apiSecret
    univerifyApiSecret: {
      type: String,
      default: "", // 612673421c936c2e440eb0d6fa41d551
    },
    // 本机一键登录云函数名
    univerifyCloudFunctionName: {
      type: String,
      default: "univerify-login",
    },
    // 输入框是否显示边框
    inputBorder: {
      type: Boolean,
      default: true,
    },
    // 登录设置页面URL
    loginSettingsPageUrl: {
      type: String,
      default: "/packages/_/pages/login/login_settings",
    },
    // 登录成功跳转的主页面
    mainPageUrl: {
      type: String,
      default: INDEX_PAGE_PATH || "/pages/welldo/index",
    },
    // 归属系统
    system: {
      type: String,
    },
    // 国际化
    enableI18nLanguage: {
      type: Boolean,
      default: true,
    },
    // 表单标签名隐藏
    formLabelHidden: {
      type: Boolean,
      default: true,
    },
    // 系统是否可选
    systemSelectable: {
      type: Boolean,
      default: true,
    },
    titleFormat: {
      type: Function,
    },
    loginBg: {
      type: String,
      default: "login-bg-component",
    },
    imgUrl: {
      type: String,
      default: logo,
    },
  },
  components: {
    wLoginForm,
    loginSetting,
  },
  data() {
    const _self = this;
    let currentLoginWay = _self.loginWay || "usrPwd";
    // 可切换登录方式时，设置最近选择的登录方式
    if (_self.showSwitchLoginWay) {
      currentLoginWay = uni.getStorageSync("current_login_way") || currentLoginWay;
    }
    let isShowSwitchLoginWay = _self.showSwitchLoginWay;
    let selectedLanguage = uni.getStorageSync("locale") || "zh_CN";
    let title = this.titleFormat ? this.titleFormat() : this.$t("loginComponent.logoTitle", "一体化办公助手");
    return {
      currentLoginWay,
      isShowSwitchLoginWay,
      languageOptions: [],
      selectedLanguage,
      formMinHeight: undefined,
      captchaOptions,
      title,
    };
  },
  created() {
    const _self = this;
    // 后端系统参数的登录配置
    // FIXME: 修改移动端登录的相关参数配置获取方式，应该独立一个移动应用登录配置模块，读取该模块里面的配置参数
    // 移动端的登录配置例如logo样式等，还是参考pc端登录页设置实现
    // _self.loadLoginConfig();
    _self.fetchLocaleOptions();
  },
  mounted() {
    this.$nextTick(() => {
      this.getFormSelectorQuery();
    });
  },
  computed: {
    loginWayItems() {
      let _self = this;
      let loginWayItems = [
        {
          text: _self.$t("loginComponent.usrPwd", "用户名/密码登录"),
          value: "usrPwd",
        },
        {
          text: _self.$t("loginComponent.usrPhonePwd", "用户名/手机号/密码登录"),
          value: "usrPhonePwd",
        },
        {
          text: _self.$t("loginComponent.sms", "短信验证码登录"),
          value: "sms",
        },
      ];
      forEach(loginWayItems, function (item) {
        item.checked = item.value == _self.currentLoginWay;
      });
      return loginWayItems;
    },
    languageOptionItems() {
      let _self = this;
      let items = utils.deepClone(this.languageOptions);
      forEach(items, function (item) {
        item.checked = item.value == _self.selectedLanguage;
      });
      return items;
    },
    selectedLanguageText() {
      this.title = this.titleFormat ? this.titleFormat() : this.$t("loginComponent.logoTitle", "一体化办公助手");
      for (let o of this.languageOptions) {
        if (o.value == this.selectedLanguage) {
          return o.text;
        }
      }
      return "";
    },
  },
  watch: {},
  methods: {
    loadLoginConfig: function () {
      const _self = this;
      // uni.setStorageSync("uni_id_token", "");
      storage.clearAccessToken();
      uni.request({
        url: "/basicdata/system/param/get?key=uni-app.login.config&timestamp=" + captchaOptions.timestamp,
        method: "GET",
        contentType: "application/json",
        dataType: "json",
        success: function (success) {
          let data = success.data.data;
          if (!isEmpty(data)) {
            _self.configFromSystemParams(data);
          }
        },
      });
    },
    configFromSystemParams: function (value) {
      const _self = this;
      let values = value.split(";");
      for (let index = 0; index < values.length; index++) {
        let configKeyValue = values[index];
        let configPairs = configKeyValue.split("=");
        if (configPairs.length != 2) {
          continue;
        }
        let configKey = configPairs[0];
        let configValue = configPairs[1];
        // logo标题
        if (configKey == "logoTitle") {
          _self.title = configValue;
        } else if (configKey == "logoImg") {
          // logo图片
          _self.imgUrl = configValue;
        } else if (configKey == "loginWay") {
          // 登录方式
          _self.currentLoginWay = configValue;
        } else if (configKey == "showSwitchLoginWay") {
          // 是否显示切换登录方式
          _self.isShowSwitchLoginWay = configValue == "true";
        }
      }
    },
    onShowSwitchLoginWayClick: function () {
      this.$refs.popup.open("bottom");
    },
    onSwitchLoginWayClose: function () {
      this.$refs.popup.close("bottom");
    },
    onSwitchLoginWayOk: function (value) {
      const _self = this;
      _self.currentLoginWay = value;
      uni.setStorageSync("current_login_way", value);
      _self.$refs.popup.close("bottom");
    },
    onPopupSettingClose() {
      this.$refs.popupSetting.close("bottom");
    },
    onPopupSettingOk() {
      this.$emit("updateSystem");
      this.onPopupSettingClose();
    },
    onPopupSettingOpen() {
      this.$refs.popupSetting.open("bottom");
    },
    fetchLocaleOptions() {
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/api/app/codeI18n/getAllLocales`, {
            params: {},
          })
          .then(({ data }) => {
            if (data.code == 0) {
              for (let d of data.data) {
                this.languageOptions.push({
                  text: d.name,
                  value: d.locale,
                  description: d.remark || d.name,
                  transCode: d.translateCode,
                });
              }
            }
            resolve();
          })
          .catch((error) => {});
      });
    },
    onChangeLocaleClick() {
      this.$refs.popupLocale.open("bottom");
    },
    onSwitchLocaleClose: function () {
      this.$refs.popupLocale.close("bottom");
    },
    onSwitchLocale(value) {
      const _self = this;
      _self.selectedLanguage = value;
      uni.setStorageSync("locale", value);
      this.$i18n.locale = value;
      this.asyncLoadLocale(this.selectedLanguage).then((message) => {
        this.$i18n.mergeLocaleMessage(this.selectedLanguage, message);
      });
      _self.$refs.popupLocale.close("bottom");
    },
    asyncLoadLocale(locale, all) {
      return new Promise((resolve, reject) => {
        const data = require(`@locale/${locale}.json`);
        resolve(data);
      });
    },
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    getFormSelectorQuery() {
      let _self = this;
      const views = uni.createSelectorQuery().in(this);
      views
        .select(".w-login-form-container")
        .boundingClientRect((data) => {
          if (data) {
            _self.formMinHeight = `calc(100vh - ${data.top}px - 2*38px)`;
          }
        })
        .exec();
    },
  },
};
</script>

<style lang="scss" scoped>
.w-login {
  --w-title-font-size: calc(var(--w-font-size) + 14px);

  .login-bg {
    .login-bg-header {
      position: absolute;
      /* #ifdef H5 */
      top: 16%;
      /* #endif */
      /* #ifdef APP-PLUS */
      top: 26%;
      /* #endif */
      left: 50%;
      margin-left: -66px;
    }

    .w-login-form-container {
      position: absolute;
      top: calc(32% + 132px);
      width: calc(100% - 24px);
      left: 12px;

      .w-login-form-body {
        border-radius: 8px;
        padding: 20px 0;
        background: #ffffff;
        margin-bottom: 30px;

        .w-login-form {
          padding: 0 20px;

          ::v-deep .uni-w-easyinput {
            .is-input-border {
              border-radius: 8px;
            }

            .uni-easyinput__content {
              min-height: 44px;
            }
          }
        }
      }
    }
  }

  .image {
    width: 132px;
    height: 132px;
  }

  .title {
    font-size: var(--w-title-font-size);
    color: var(--w-text-color-mobile);
    padding: 8px 20px 28px;
    text-align: center;
    line-height: calc(var(--w-title-font-size) + 4px);
  }
}
</style>
