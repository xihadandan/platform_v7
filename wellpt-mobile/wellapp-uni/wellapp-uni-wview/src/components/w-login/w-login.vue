<template>
  <view class="w-login">
    <view class="login-header">
      <slot name="login-header">
        <image v-if="imgUrl" :src="imgUrl" class="image"></image>
        <view v-if="title" class="title">{{ title }}</view>
      </slot>
    </view>
    <!-- 用户名密码、用户名手机号密码登录方式 -->
    <view v-if="currentLoginWay == 'usrPwd' || currentLoginWay == 'usrPhonePwd'" class="login-content">
      <slot name="login-content">
        <view>
          <view class="login-row">
            <text class="title">账号：</text>
            <uni-easyinput
              type="text"
              v-model="username"
              :inputBorder="inputBorder"
              placeholder="请输入账号"
            ></uni-easyinput>
          </view>
          <view v-if="currentLoginWay == 'usrPhonePwd'" class="login-row">
            <text class="title">手机：</text>
            <uni-easyinput
              type="text"
              v-model="mobilePhone"
              :inputBorder="inputBorder"
              placeholder="请输入手机号码"
            ></uni-easyinput>
          </view>
          <view class="login-row">
            <text class="title">密码：</text>
            <uni-easyinput
              type="password"
              v-model="password"
              :inputBorder="inputBorder"
              placeholder="请输入密码"
            ></uni-easyinput>
          </view>
          <view v-if="showImageVerifyCode" class="login-row image-verify-code">
            <text class="title">验证码：</text>
            <uni-easyinput
              type="text"
              v-model="imageVerifyCode"
              :inputBorder="inputBorder"
              placeholder="请输入验证码"
            ></uni-easyinput>
            <image :src="imageVerifyCodeUrl" class="image" @click="createRandImageVerifyCode"></image>
          </view>
        </view>
        <view class="input-row uni-flex uni-row content-space-between">
          <view class="remeber-me">
            <checkbox-group @change="onRemeberMeCheckboxChange">
              <label>
                <checkbox class="checkbox" color="#FFCC33" :checked="remeberMe" />
                记住账号密码
              </label>
            </checkbox-group>
          </view>
          <view v-if="showLoginSettings" class="login-settings">
            <text @click="onLoginSettingsClick">登录设置</text>
          </view>
        </view>
      </slot>
    </view>
    <!-- 短信验证码登录方式 -->
    <view v-if="currentLoginWay == 'sms'" class="login-content">
      <slot name="login-content">
        <view>
          <view class="login-row">
            <text class="title">手机：</text>
            <uni-easyinput
              class="m-input"
              type="text"
              clearable
              v-model="mobilePhone"
              :inputBorder="inputBorder"
              placeholder="请输入手机号码"
            ></uni-easyinput>
          </view>
          <view class="login-row">
            <text class="title">验证码：</text>
            <uni-easyinput
              type="text"
              v-model="smsVerifyCode"
              :inputBorder="inputBorder"
              placeholder="请输入验证码"
            ></uni-easyinput>
            <view class="send-code-btn" @click="onSendSmsVerifyCodeClick">{{
              smsVerifyCodeDuration ? smsVerifyCodeDuration + "s" : "发送验证码"
            }}</view>
          </view>
        </view>
        <view class="input-row uni-flex uni-row content-space-between">
          <view></view>
          <view v-if="showLoginSettings" class="login-settings">
            <text @click="onLoginSettingsClick">登录设置</text>
          </view>
        </view>
      </slot>
    </view>
    <view class="login-btn btn-row">
      <slot name="login-btn">
        <view v-if="isUniLoginProvider">
          <template v-for="(provider, key) in providerList">
            <button
              v-show="provider.value == currentLoginWay"
              type="primary"
              class="page-body-button"
              @click="onUniLoginProviderClick(provider)"
              :key="key"
            >
              {{ provider.text }}
            </button>
          </template>
        </view>
        <button
          v-else
          type="primary"
          class="primary"
          :disabled="loginBtnDisabled"
          :loading="loginBtnLoading"
          @tap="onLoginClick"
        >
          登录
        </button>
      </slot>
    </view>
    <view v-if="showSwitchLoginWay" class="uni-center uni-mt-8">
      <text @tap="onShowSwitchLoginWayClick">切换登录方式</text>
    </view>

    <!-- 弹窗内容 -->
    <uni-popup ref="popup">
      <w-popup-select
        class="switch-login-way-popup"
        title="选择登录方式"
        :items="loginWayItems"
        @ok="onSwitchLoginWayOk"
      ></w-popup-select>
    </uni-popup>
  </view>
</template>

<script>
import uniProvider from "./uni-provider.js";
import { mapMutations } from "vuex";
import { device, storage } from "wellapp-uni-framework";
import { trim as stringTrim, isEmpty, each as forEach } from "lodash";
// let weixinAuthService;
const captchaOptions = {
  deviceId: device.getDeviceUUID(),
  scene: "login",
  timestamp: new Date().getTime(),
};
export default {
  props: {
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
    logoTitle: String, // logo标题
    logoImg: String, // logo图片
    // 输入框是否显示边框
    inputBorder: {
      type: Boolean,
      default: false,
    },
    // 登录设置页面URL
    loginSettingsPageUrl: {
      type: String,
      default: "/uni_modules/w-app/pages/login/login_settings",
    },
    // 登录成功跳转的主页面
    mainPageUrl: {
      type: String,
      default: "/pages/index/index",
    },
    // 归属系统
    system: {
      type: String,
    },
  },
  data() {
    const _self = this;
    let loginWayItems = [
      { text: "用户名/密码登录", value: "usrPwd" },
      { text: "用户名/手机号/密码登录", value: "usrPhonePwd" },
      { text: "短信验证码登录", value: "sms" },
    ];
    let currentLoginWay = _self.loginWay || "usrPwd";
    // 可切换登录方式时，设置最近选择的登录方式
    if (_self.showSwitchLoginWay) {
      currentLoginWay = uni.getStorageSync("current_login_way") || currentLoginWay;
    }
    forEach(loginWayItems, function (item) {
      item.checked = item.value == currentLoginWay;
    });
    return {
      loginSuccessRedirect: _self.system ? "/uni_modules/w-app/pages/system/index" : _self.mainPageUrl,
      title: _self.logoTitle,
      imgUrl: _self.logoImg,
      loginWayItems, // 登录方式
      providerList: [], // uni-app支持的oauth登录方式
      currentLoginWay,
      username: "",
      mobilePhone: "",
      password: "",
      loginBtnDisabled: false,
      loginBtnLoading: false,
      remeberMe: false,
      smsVerifyCode: "",
      smsId: "",
      smsVerifyCodeDuration: 0,
      imageVerifyCode: "", // 图形验证码
      randImageVerifyCode: "", //自动生成的随机图形验证码
    };
  },
  created() {
    const _self = this;
    // 后端系统参数的登录配置
    _self.loadLoginConfig();
    // 加载uni-app支持的oauth登录方式
    uniProvider.getLoginProvider(function (providerList) {
      _self.providerList = providerList;
      forEach(providerList, function (item) {
        item.checked = item.value == _self.currentLoginWay;
      });
      _self.loginWayItems = _self.loginWayItems.concat(providerList);
    });
    // 创建随机图形验证码
    if (_self.showImageVerifyCode) {
      _self.createRandImageVerifyCode();
    }
  },
  mounted() {
    const _self = this;
    // 记住密码
    var remeberMe = uni.getStorageSync("remeber_me");
    if (remeberMe) {
      _self.remeberMe = true;
      _self.username = uni.getStorageSync("remeber_me_username");
      _self.password = uni.getStorageSync("remeber_me_password");
    }
  },
  computed: {
    isUniLoginProvider: function () {
      const _self = this;
      let loginWay = _self.currentLoginWay;
      return !(loginWay == "usrPwd" || loginWay == "usrPhonePwd" || loginWay == "sms");
    },
    imageVerifyCodeUrl: function () {
      let backendUrl = storage.getWellappBackendUrl();
      let url = backendUrl + "/common/verification/graphic/verify/code/show?verifycode=" + this.randImageVerifyCode;
      return url;
    },
  },
  watch: {
    username(val) {
      const _self = this;
      if (_self.currentLoginWay == "usrPwd" || _self.currentLoginWay == "usrPhonePwd") {
        if (stringTrim(val)) {
          this.loginBtnDisabled = false;
        } else {
          this.loginBtnDisabled = true;
        }
      }
    },
    mobilePhone(val) {
      const _self = this;
      if (_self.currentLoginWay == "sms") {
        if (stringTrim(val)) {
          this.loginBtnDisabled = false;
        } else {
          this.loginBtnDisabled = true;
        }
      }
    },
  },
  methods: {
    ...mapMutations(["loginByUserDetails"]),
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
          _self.showSwitchLoginWay = configValue == "true";
        }
      }
    },
    createRandImageVerifyCode: function () {
      const _self = this;
      uni.request({
        url: "/security/aid/createImageVerifyCode",
        method: "POST",
        success: function (result) {
          if (result.data.code == 0) {
            let verifyCode = result.data.data;
            _self.randImageVerifyCode = verifyCode.code;
          }
        },
      });
    },
    onRemeberMeCheckboxChange: function (event) {
      var _self = this;
      var values = event.detail.value;
      if (values.length > 0) {
        _self.remeberMe = true;
      } else {
        _self.remeberMe = false;
      }
    },
    onLoginSettingsClick: function () {
      uni.navigateTo({
        url: this.loginSettingsPageUrl,
      });
    },
    // 发送短信验证码
    onSendSmsVerifyCodeClick: function () {
      const _self = this;
      if (_self.smsVerifyCodeDuration) {
        uni.showModal({
          content: `请在${_self.smsVerifyCodeDuration}秒后重试`,
          showCancel: false,
        });
        return;
      }
      if (!/^1\d{10}$/.test(_self.mobilePhone)) {
        uni.showModal({
          content: "手机号码填写错误",
          showCancel: false,
        });
        return;
      }
      // uni.setStorageSync("uni_id_token", "");
      storage.clearAccessToken();
      uni.request({
        url: `/security/aid/sendMobileSms?mobilePhone=${_self.mobilePhone}&deviceId=${captchaOptions.deviceId}`,
        method: "POST",
        success: (e) => {
          if (e.data.code == 0) {
            uni.showModal({
              content: "验证码发送成功，请注意查收",
              showCancel: false,
            });
            _self.smsId = e.data.data.id;
            _self.smsVerifyCodeDuration = 60;
            _self.codeInterVal = setInterval(() => {
              _self.smsVerifyCodeDuration--;
              if (_self.smsVerifyCodeDuration === 0) {
                if (_self.codeInterVal) {
                  clearInterval(_self.codeInterVal);
                  _self.codeInterVal = null;
                }
              }
            }, 1000);
          } else {
            uni.showModal({
              content: "验证码发送失败：" + e.data.msg,
              showCancel: false,
            });
          }
        },
        fail(e) {
          uni.showModal({
            content: "验证码发送失败",
            showCancel: false,
          });
        },
      });
    },
    onShowSwitchLoginWayClick: function () {
      this.$refs.popup.open("bottom");
    },
    onSwitchLoginWayOk: function (value) {
      const _self = this;
      _self.currentLoginWay = value;
      uni.setStorageSync("current_login_way", value);
      _self.$refs.popup.close("bottom");
    },
    onLoginClick: function () {
      const _self = this;
      if (_self.currentLoginWay == "usrPwd" || _self.currentLoginWay == "usrPhonePwd") {
        _self.loginByPwd();
      } else if (_self.currentLoginWay == "sms") {
        _self.loginBySms();
      }
    },
    // uni-app提供的登录方式
    onUniLoginProviderClick: function (provider) {
      console.log(provider);
      const _self = this;
      let loginMethod = provider.value + "Login";
      // 本机一键登录
      if (uniProvider[loginMethod] && provider.value == "univerify") {
        uniProvider[loginMethod](
          provider,
          _self.univerifyCloudFunctionName,
          function (phoneNumber) {
            _self.loginByUniverify(phoneNumber);
          },
          function (error) {
            console.log(error);
            // 一键登录点击其他登录方式
            if (error.code == "30002") {
              _self.onShowSwitchLoginWayClick();
            }
          }
        );
      } else {
        _self.$emit("uniLogin", provider);
      }
    },
    async loginByPwd() {
      const _self = this;
      let username = stringTrim(_self.username);
      let mobilePhone = stringTrim(_self.mobilePhone);
      let password = stringTrim(_self.password);
      let imageVerifyCode = stringTrim(_self.imageVerifyCode);

      // 手机号码非空检验
      if (_self.currentLoginWay == "usrPhonePwd") {
        if (!/^1\d{10}$/.test(mobilePhone)) {
          uni.showModal({
            content: "手机号码填写错误！",
            showCancel: false,
          });
          return;
        }
      }
      // 密码非空检验
      if (isEmpty(stringTrim(password))) {
        uni.showToast({
          icon: "none",
          title: "密码不能为空！",
        });
        return;
      }
      // 图形验证码
      if (_self.showImageVerifyCode) {
        if (isEmpty(imageVerifyCode)) {
          uni.showToast({
            icon: "none",
            title: "验证码填写错误！",
          });
          return;
        } else if (_self.randImageVerifyCode != imageVerifyCode) {
          uni.showToast({
            icon: "none",
            title: "图形验证码填写错误！",
          });
          return;
        }
      }

      if (_self.remeberMe) {
        uni.setStorageSync("remeber_me_username", username);
        uni.setStorageSync("remeber_me_password", password);
      } else {
        uni.removeStorage({ key: "remeber_me_username" });
        uni.removeStorage({ key: "remeber_me_password" });
      }
      uni.setStorageSync("remeber_me", _self.remeberMe);
      // uni.setStorageSync("uni_id_token", "");
      storage.clearAccessToken();

      const data = {
        username,
        mobilePhone,
        password,
        systemId: _self.system,
        // json: JSON.stringify({ apiServiceName: "security.login" }),
        // captcha: _self.captchaText,
        // ...captchaOptions,
      };
      _self.loginBtnLoading = true;
      uni.request({
        url: `${_self.currentLoginWay == "usrPwd" ? "/login/1" : "/loginByUsrPhonePwd"}`,
        method: "POST",
        data: data,
        success: (res) => {
          _self.loginRequestSuccess(res);
        },
        fail: (e) => {
          uni.showModal({
            content: JSON.stringify(e),
            showCancel: false,
          });
        },
        complete: () => {
          _self.loginBtnLoading = false;
        },
      });
    },
    async loginBySms() {
      const _self = this;
      let mobilePhone = stringTrim(_self.mobilePhone);
      let smsVerifyCode = stringTrim(_self.smsVerifyCode);
      let smsId = _self.smsId;

      if (!/^1\d{10}$/.test(mobilePhone)) {
        uni.showModal({
          content: "手机号码填写错误",
          showCancel: false,
        });
        return;
      }
      if (!/^\d{6}$/.test(smsVerifyCode)) {
        uni.showModal({
          content: "验证码填写错误",
          showCancel: false,
        });
        return;
      }

      const data = {
        mobilePhone,
        smsVerifyCode,
        smsId,
      };
      _self.loginBtnLoading = true;
      uni.request({
        url: `/loginByMobileSms`,
        method: "POST",
        data: data,
        success: (res) => {
          _self.loginRequestSuccess(res);
        },
        fail: (e) => {
          uni.showModal({
            content: JSON.stringify(e),
            showCancel: false,
          });
        },
        complete: () => {
          _self.loginBtnLoading = false;
        },
      });
    },
    async loginByUniverify(phoneNumber) {
      const _self = this;
      let data = {
        mobilePhone: phoneNumber,
        apiKey: _self.univerifyApiKey,
        apiSecret: _self.univerifyApiSecret,
      };
      uni.request({
        url: `/loginByUniverify`,
        method: "POST",
        data: data,
        success: (res) => {
          _self.loginRequestSuccess(res);
        },
        fail: (e) => {
          uni.showModal({
            content: JSON.stringify(e),
            showCancel: false,
          });
        },
        complete: () => {},
      });
    },
    loginRequestSuccess(res) {
      const _self = this;
      // 登录成功
      if (res.data.code == 0) {
        let data = res.data.data;
        // storage.setAccessToken(data.token);
        // storage.setUsername(data.userName);
        // storage.setUserDetails(data);
        // _self.login(data.userName);
        console.log("登录成功, 返回用户信息: ", data);
        storage.setSystem(_self.system);
        _self.loginByUserDetails(data);
        // uni.setStorageSync("uni_id_token", data.token);
        // uni.setStorageSync("username", data.userName);
        // uni.setStorageSync("user_details", data);
        uni.setStorageSync("login_type", "local");
        uni.setStorageSync("uni_id_has_pwd", true);
        _self.toMainPage(data.userName);
        _self.$emit("loginSuccess", data);
      } else {
        // 登录失败
        uni.showToast({ icon: "none", title: res.data.msg });
      }
    },
    toMainPage(userName) {
      const _self = this;
      /**
       * 强制登录时使用reLaunch方式跳转过来
       * 返回首页也使用reLaunch方式
       */
      let url = _self.loginSuccessRedirect;
      let loginSuccessRedirect = uni.getStorageSync("loginSuccessRedirect");
      if (loginSuccessRedirect) {
        url = loginSuccessRedirect;
      }
      uni.reLaunch({
        url,
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.w-login {
  width: 100%;
  color: $uni-text-color;
  background-color: $uni-bg-secondary-color;

  .login-row {
    display: flex;
    align-items: center;
    border-bottom: 1px solid #f4f4f4;
    height: 45px;
  }

  .login-header {
    width: 100%;
    text-align: center;

    background-position: center center;
    background-size: 100% 100%;

    .image {
      width: 145px;
      height: 145px;
    }

    .title {
      font-size: 22px;
      color: #000;
      text-align: center;
      margin-bottom: 20px;
    }
  }

  .login-content {
    background-color: $uni-bg-secondary-color;
    padding: 0px 12px;
    .send-code-btn {
      width: 120px;
      text-align: center;
      color: #0faeff;
    }

    .content-space-between {
      justify-content: space-between;
    }

    .image-verify-code {
      .image {
        width: 100px;
        height: 40px;
      }
    }

    .remeber-me {
      .checkbox {
        transform: scale(0.7);
      }
    }

    .login-settings {
      margin-right: 6px;
    }
  }

  .switch-login-way-popup {
    background-color: #fff;
  }
}
</style>
