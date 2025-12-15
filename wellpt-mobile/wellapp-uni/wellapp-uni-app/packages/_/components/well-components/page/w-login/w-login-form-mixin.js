import uniProvider from "./uni-provider.js";
import { mapMutations } from "vuex";
import { device, storage } from "wellapp-uni-framework";
import { trim as stringTrim, isEmpty, each as forEach, findIndex } from "lodash";
module.exports = {
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
    // 输入框是否显示边框
    inputBorder: {
      type: Boolean,
      default: false,
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
    languageText: String,
    // 表单标签名隐藏
    formLabelHidden: {
      type: Boolean,
      default: true,
    },
    captchaOptions: Object,
  },
  data() {
    const _self = this;
    return {
      loginSuccessRedirect: _self.mainPageUrl, //_self.system ? "/packages/_/pages/system/index" : _self.mainPageUrl,
      // loginWayItems, // 登录方式
      providerList: [], // uni-app支持的oauth登录方式
      username: "",
      mobilePhone: "",
      password: "",
      loginBtnDisabled: true,
      loginBtnLoading: false,
      remeberMe: [],
      smsVerifyCode: "",
      smsId: "",
      smsVerifyCodeDuration: 0,
      imageVerifyCode: "", // 图形验证码
      randImageVerifyCode: "", //自动生成的随机图形验证码
    };
  },
  created() {
    const _self = this;
    // 加载uni-app支持的oauth登录方式
    uniProvider.getLoginProvider(function (providerList) {
      _self.providerList = providerList;
      forEach(providerList, function (item) {
        item.checked = item.value == _self.currentLoginWay;
      });
      // _self.loginWayItems = _self.loginWayItems.concat(providerList);
    });
    // 创建随机图形验证码
    if (_self.showImageVerifyCode) {
      _self.createRandImageVerifyCode();
    }
    if (this.system) {
      storage.setSystem(this.system);
    }
    this.setHasLogin();
  },
  mounted() {
    const _self = this;
    // 记住密码
    var remeberMe = uni.getStorageSync("remeber_me");
    if (remeberMe) {
      _self.remeberMe = [true];
      _self.username = uni.getStorageSync("remeber_me_username");
      _self.password = uni.getStorageSync("remeber_me_password");
    }

    // #ifdef H5
    this.ssoLoginIfRequired();
    // #endif
  },
  computed: {
    currentLoginWay() {
      return this.loginWay || "usrPwd";
    },
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
    ...mapMutations(["loginByUserDetails", "setHasLogin"]),
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
    onLoginSettingsClick: function () {
      // uni.navigateTo({
      //   url: this.loginSettingsPageUrl,
      // });
      this.$emit("showLoginSettingsPopup");
    },
    // 发送短信验证码
    onSendSmsVerifyCodeClick: function () {
      const _self = this;
      if (_self.smsVerifyCodeDuration) {
        uni.showModal({
          content: _self.$t(
            "loginComponent.error.durationRetry",
            { duration: _self.smsVerifyCodeDuration },
            `请在${_self.smsVerifyCodeDuration}秒后重试`
          ),
          showCancel: false,
          confirmText: this.$t("global.confirm", "确认"),
        });
        return;
      }
      if (!/^1\d{10}$/.test(_self.mobilePhone)) {
        uni.showModal({
          content: _self.$t("loginComponent.error.phoneError", "手机号码填写错误"),
          showCancel: false,
          confirmText: this.$t("global.confirm", "确认"),
        });
        return;
      }
      // uni.setStorageSync("uni_id_token", "");
      storage.clearAccessToken();
      uni.request({
        url: `/security/aid/sendMobileSms?mobilePhone=${_self.mobilePhone}&deviceId=${_self.captchaOptions.deviceId}`,
        method: "POST",
        success: (e) => {
          if (e.data.code == 0) {
            uni.showModal({
              content: _self.$t("loginComponent.error.vcodeSendSuccess", "验证码发送成功，请注意查收"),
              showCancel: false,
              confirmText: this.$t("global.confirm", "确认"),
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
              content:
                _self.$t("loginComponent.error.vcodeSendFail", "验证码发送失败") + "：" + _self.messageMap(e.data.msg),
              showCancel: false,
              confirmText: this.$t("global.confirm", "确认"),
            });
          }
        },
        fail(e) {
          uni.showModal({
            content: _self.$t("loginComponent.error.vcodeSendFail", "验证码发送失败"),
            showCancel: false,
            confirmText: this.$t("global.confirm", "确认"),
          });
        },
      });
    },
    onShowSwitchLoginWayClick: function () {
      this.$emit("showSwitchLoginWayPopup");
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

      let systemId = _self.system || storage.getSystem();
      if (!systemId) {
        uni.showModal({
          content: _self.$t("loginComponent.error.systemIdUndefined", "请在登录设置中配置系统ID！"),
          showCancel: false,
          confirmText: this.$t("global.confirm", "确认"),
        });
        _self.$emit("showLoginSettingsPopup");
        return;
      }
      // 手机号码非空检验
      if (_self.currentLoginWay == "usrPhonePwd") {
        if (!/^1\d{10}$/.test(mobilePhone)) {
          uni.showModal({
            content: _self.$t("loginComponent.error.phoneError", "手机号码填写错误"),
            showCancel: false,
            confirmText: this.$t("global.confirm", "确认"),
          });
          return;
        }
      }
      // 密码非空检验
      if (isEmpty(stringTrim(password))) {
        uni.showToast({
          icon: "none",
          title: _self.$t("loginComponent.error.passwordunEmpty", "密码不能为空！"),
        });
        return;
      }
      // 图形验证码
      if (_self.showImageVerifyCode) {
        if (isEmpty(imageVerifyCode)) {
          uni.showToast({
            icon: "none",
            title: _self.$t("loginComponent.error.captchaError", "验证码错误"),
          });
          return;
        } else if (_self.randImageVerifyCode != imageVerifyCode) {
          uni.showToast({
            icon: "none",
            title: _self.$t("loginComponent.error.pcodeErroe", "图形验证码填写错误！"),
          });
          return;
        }
      }

      if (_self.remeberMe[0]) {
        uni.setStorageSync("remeber_me_username", username);
        uni.setStorageSync("remeber_me_password", password);
      } else {
        uni.removeStorage({ key: "remeber_me_username" });
        uni.removeStorage({ key: "remeber_me_password" });
      }
      uni.setStorageSync("remeber_me", _self.remeberMe[0]);
      // uni.setStorageSync("uni_id_token", "");
      storage.clearAccessToken();

      const data = {
        username,
        mobilePhone,
        password,
        systemId,
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
            confirmText: this.$t("global.confirm", "确认"),
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
          content: _self.$t("loginComponent.error.phoneError", "手机号码填写错误"),
          showCancel: false,
          confirmText: this.$t("global.confirm", "确认"),
        });
        return;
      }
      if (!/^\d{6}$/.test(smsVerifyCode)) {
        uni.showModal({
          content: _self.$t("loginComponent.error.captchaError", "验证码错误"),
          showCancel: false,
          confirmText: this.$t("global.confirm", "确认"),
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
            confirmText: this.$t("global.confirm", "确认"),
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
            confirmText: this.$t("global.confirm", "确认"),
          });
        },
        complete: () => {},
      });
    },
    loginRequestSuccess(res) {
      const _self = this;
      // 登录成功
      if (res.data.code == 0) {
        let system = _self.system;
        let data = res.data.data;
        // storage.setAccessToken(data.token);
        // storage.setUsername(data.userName);
        // storage.setUserDetails(data);
        // _self.login(data.userName);
        console.log("登录成功, 返回用户信息: ", data);
        // 系统Id在登录页地址里体现
        // 如果登录携带的系统id不在登录的当前用户里，则取当前用户系统id
        // let userSystemOrgDetails = data.userSystemOrgDetails;
        // if (userSystemOrgDetails && userSystemOrgDetails.details && userSystemOrgDetails.details.length) {
        //   if (system) {
        //     let hasSystem = findIndex(userSystemOrgDetails.details, (sys) => {
        //       return sys.system == system;
        //     });
        //     if (hasSystem == -1) {
        //       system = userSystemOrgDetails.details[0].system;
        //     }
        //   } else {
        //     system = userSystemOrgDetails.details[0].system;
        //   }
        // }
        // storage.setSystem(system);
        _self.loginByUserDetails(data);
        // uni.setStorageSync("uni_id_token", data.token);
        // uni.setStorageSync("username", data.userName);
        // uni.setStorageSync("user_details", data);
        uni.setStorageSync("login_type", "local");
        uni.setStorageSync("uni_id_has_pwd", true);

        let loginSuccessRedirect = uni.getStorageSync("loginSuccessRedirect");
        if (loginSuccessRedirect) {
          uni.removeStorageSync("loginSuccessRedirect");
          uni.reLaunch({
            url: loginSuccessRedirect,
          });
        } else {
          _self.toMainPage(data.userName);
        }
        _self.$emit("loginSuccess", data);
      } else {
        // 登录失败
        uni.showToast({ icon: "none", title: _self.$t("loginComponent.error." + res.data.msg, res.data.msg) });
      }
    },
    toMainPage(userName) {
      const _self = this;
      /**
       * 强制登录时使用reLaunch方式跳转过来
       * 返回首页也使用reLaunch方式
       */
      let url = _self.loginSuccessRedirect;
      // let loginSuccessRedirect = uni.getStorageSync("loginSuccessRedirect");
      // if (loginSuccessRedirect) {
      //   url = loginSuccessRedirect;
      // }
      uni.reLaunch({
        url,
      });
    },
    onChangeLocaleClick() {
      this.$emit("showLocalePopup");
    },
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    messageMap(msg) {
      let map = {
        phoneMultiple: "手机号码存在多个对应的用户信息",
        phoneError: "手机号码填写错误！",
        phoneNoUser: "不存在手机号码对应的用户信息",
      };
      return this.$i18n.getMsgI18nsByMap(this, map, msg, "loginComponent.error");
    },
    ssoLoginIfRequired() {
      const _this = this;
      let loginProvider = uni.getStorageSync("login_provider");
      if (!loginProvider) {
        loginProvider = _this.getPageParameter("login_provider");
      }
      if (loginProvider == "feishu_sso" || window.location.href.indexOf("feishu_sso") != -1) {
        import("./feishu/sso-login.js").then(({ ssoLogin }) => {
          ssoLogin(this.ssoLoginOptions());
        });
      } else if (loginProvider == "dingtalk_sso" || window.location.href.indexOf("dingtalk_sso") != -1) {
        import("./dingtalk/sso-login.js").then(({ ssoLogin }) => {
          ssoLogin(this.ssoLoginOptions());
        });
      } else if (loginProvider == "weixin_sso" || window.location.href.indexOf("weixin_sso") != -1) {
        import("./weixin/sso-login.js").then(({ ssoLogin }) => {
          ssoLogin(this.ssoLoginOptions());
        });
      }
    },
    ssoLoginOptions() {
      const _this = this;
      return {
        $widget: _this,
        onReady: () => {},
        onLogining: () => {
          uni.showToast({
            icon: "none",
            title: _this.$t("loginComponent.sso.logining", "单点登录中") + "...",
          });
        },
        onSuccess: (data) => {
          uni.showToast({
            icon: "none",
            title: this.$t("loginComponent.sso.success", "单点登录成功"),
          });
          _this.loginByUserDetails(data);
          // uni.setStorageSync("uni_id_token", data.token);
          // uni.setStorageSync("username", data.userName);
          // uni.setStorageSync("user_details", data);
          uni.setStorageSync("login_type", "local");
          uni.setStorageSync("uni_id_has_pwd", false);
          let loginSuccessRedirect = uni.getStorageSync("loginSuccessRedirect") || this.mainPageUrl;
          uni.removeStorageSync("login_provider");
          uni.removeStorageSync("loginSuccessRedirect");
          _this.$emit("loginSuccess", data);
          uni.reLaunch({
            url: loginSuccessRedirect,
          });
          // window.location.href = `${window.location.origin}/sys/${this._$SYSTEM_ID}/index`;
        },
        onFail: () => {
          uni.showToast({
            icon: "none",
            title: this.$t("loginComponent.sso.failed", "单点登录失败"),
          });
        },
      };
    },
  },
};
