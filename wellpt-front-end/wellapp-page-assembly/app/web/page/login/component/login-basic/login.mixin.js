import thumbnail from './thumbnail.png';
import { encode, decode } from 'js-base64';
import { merge, get, set } from 'lodash';
import { queryString } from '@framework/vue/utils/util';
export default {
  inject: ['csrf', 'designMode'],
  props: {
    config: Object,
    rules: {
      type: Object,
      default: function () {
        return {};
      }
    },
    errorMsg: String
  },
  thumbnail,
  computed: {
    enableRememberAcct() {
      return this.rules && this.rules.ACCT_PWD_LOGIN && this.rules.ACCT_PWD_LOGIN.rememberAcct;
    },
    enableRememberPwd() {
      return this.rules && this.rules.ACCT_PWD_LOGIN && this.rules.ACCT_PWD_LOGIN.rememberPwd;
    },
    enableCaptcha() {
      return (
        this.rules &&
        this.rules.ACCT_PWD_LOGIN &&
        this.rules.ACCT_PWD_LOGIN.loginCheckEnable &&
        this.rules.ACCT_PWD_LOGIN.loginCheckType == 'picture'
      );
    },
    enableForgetPwd() {
      return this.rules && this.rules.ACCT_PWD_LOGIN && this.rules.ACCT_PWD_LOGIN.enableForgetPwd;
    },
    enableUserRegister() {
      return this.rules && this.rules.ACCT_PWD_LOGIN && this.rules.ACCT_PWD_LOGIN.enableUserRegister;
    },
    enableI18nLanguage() {
      return this.config.loginWin.enableI18nLanguage === true;
    },
    enableFeishuLogin() {
      return this.rules && this.rules.FEISHU_LOGIN && this.rules.FEISHU_LOGIN.feishuLoginEnabled;
    },
    enableFeishuSsoLogin() {
      return this.rules && this.rules.FEISHU_SSO_LOGIN && this.rules.FEISHU_SSO_LOGIN.feishuSsoLoginEnabled;
    },
    enableDingtalkLogin() {
      return this.rules && this.rules.DING_LOGIN && this.rules.DING_LOGIN.dingDingLoginEnabled;
    },
    enableDingtalkSsoLogin() {
      return this.rules && this.rules.DING_LOGIN && this.rules.DING_LOGIN.dingDingLoginEnabled;
    },
    enableWeixinLogin() {
      return this.rules && this.rules.WEIXIN_LOGIN && this.rules.WEIXIN_LOGIN.weixinLoginEnabled;
    },
    selectedLanguageText() {
      for (let o of this.languageOptions) {
        if (o.value == this.selectedLanguage) {
          return o.label;
        }
      }
      return '';
    },
    vLoginWinTitle() {
      return this.$t('loginComponent.loginWinTitle', this.config.loginWin.titleBar.title.content);
    },
    vFooterContent() {
      return this.$t('loginComponent.footerContent', this.config.footer);
    },
    vSystemTitle() {
      return this.$t('loginComponent.systemTitle', this.config.title);
    }
  },
  data() {
    return {
      captchaInputErrorCount: 0,
      userName: undefined,
      password: undefined,
      rememberAccount: true,
      rememberPassword: true,
      errorMsg: this.errorMsg,
      captcha: undefined,
      inputCaptcha: undefined,
      logining: false,
      languageOptions: [],
      selectedLanguage: 'zh_CN',
      loginFormKey: 'accountLogin'
    };
  },
  beforeMount() {
    this.initLocalStorage();
    if (this.enableI18nLanguage) {
      this.selectedLanguage = this.$i18n.locale;
      // 获取配置中的国际化json
      let i18nPaths = ['i18n', 'loginWin.titleBar.title.i18n'];
      let configI18ns = {};
      for (let p of i18nPaths) {
        let i18n = get(this.config, p, undefined);
        if (i18n != undefined) {
          for (let locale in i18n) {
            let obj = {};
            for (let key in i18n[locale]) {
              set(obj, key, i18n[locale][key]);
            }
            merge(configI18ns, { [locale]: obj });
          }
        }
      }
      for (let key in configI18ns) {
        this.$i18n.mergeLocaleMessage(key, configI18ns[key]);
      }

      this.fetchLocaleOptions();
      this.asyncLoadLocale(this.selectedLanguage).then(message => {
        this.$i18n.mergeLocaleMessage(this.selectedLanguage, message);
      });
    }
    if (this.enableCaptcha) {
      this.refreshCaptcha();
    }
    if (this.enableRememberAcct) {
      if (this.systemLocalStorage.get('rememberAcct')) {
        this.rememberAccount = true;
        this.userName = this.systemLocalStorage.get('rememberAcct');
      }
    } else {
      this.systemLocalStorage.set('rememberAcct', undefined);
      this.userName = undefined;
    }
    if (this.enableRememberPwd) {
      if (this.systemLocalStorage.get('rememberPassword')) {
        this.rememberPassword = true;
        this.password = decode(decodeURI(this.systemLocalStorage.get('rememberPassword')));
      }
    } else {
      this.systemLocalStorage.set('rememberPassword', undefined);
      this.password = undefined;
    }
  },
  mounted() {
    let form = document.querySelector('#form');
    if (form) {
      if (this._$SYSTEM_ID) {
        let input = document.createElement('input');
        input.setAttribute('id', 'systemId');
        input.setAttribute('name', 'systemId');
        input.setAttribute('type', 'hidden');
        input.setAttribute('value', this._$SYSTEM_ID);
        form.appendChild(input);
      }

      document.body.addEventListener('keydown', event => {
        if (event.key === 'Enter') {
          this.submitLoginForm();
        }
      });
    }
    let input = document.querySelector('input[type=text]');
    if (input) {
      input.focus();
    }

    this.showFeishuLoginIfRequired();
    if (this.enableFeishuSsoLogin) {
      this.feishuSsoLogin();
    }
    if (this.enableDingtalkLogin) {
      this.dingtalkSsoLogin();
    }
  },

  methods: {
    asyncLoadLocale(locale) {
      return new Promise((resolve, reject) => {
        import(/* webpackChunkName: "locale-[request]" */ `@locale/${locale}.json`)
          .then(module => {
            resolve(module.default);
          })
          .catch(error => {
            resolve(undefined);
            console.error(`Failed to load antd locale: ${locale}`, error);
          });
      });
    },
    addCookie(name, value) {
      var exp = new Date();
      exp.setTime(exp.getTime() + +7 * 24 * 60 * 60 * 1000);
      document.cookie = name + '=' + value + ';expires=' + exp.toGMTString() + '; path=/';
    },
    deleteCookie(name) {
      document.cookie = encodeURIComponent(name) + '=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/';
    },
    onSelectLanguage(e) {
      this.selectedLanguage = e.key;
      // this.$loading();
      this.asyncLoadLocale(this.selectedLanguage).then(message => {
        this.$i18n.mergeLocaleMessage(this.selectedLanguage, message);
        this.$i18n.locale = this.selectedLanguage;
        // this.$loading(false);
      });
    },
    fetchLocaleOptions() {
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/proxy/api/app/codeI18n/getAllLocales`, { params: {} })
          .then(({ data }) => {
            if (data.code == 0) {
              for (let d of data.data) {
                this.languageOptions.push({
                  label: d.name,
                  value: d.locale,
                  description: d.remark || d.name,
                  transCode: d.translateCode
                });
              }
            }
            resolve();
          })
          .catch(error => {});
      });
    },
    openUserRegPage(target) {
      window.open(`/sys/${this._$SYSTEM_ID}/user_reg`, target == 'newWindow' ? '_blank' : '_self');
    },
    openUserForgetPwdPage(target) {
      window.open(`/sys/${this._$SYSTEM_ID}/user_forget_pwd`, target == 'newWindow' ? '_blank' : '_self');
    },
    initLocalStorage() {
      const systemId = this._$SYSTEM_ID;
      this.systemLocalStorage = {
        get: function (key) {
          if (localStorage[systemId + '_LoginLocalStorage']) {
            return JSON.parse(localStorage[systemId + '_LoginLocalStorage'])[key];
          }
          return null;
        },

        set: function (key, value) {
          if (localStorage[systemId + '_LoginLocalStorage']) {
            let obj = JSON.parse(localStorage[systemId + '_LoginLocalStorage']);
            obj[key] = value;
            localStorage.setItem(systemId + '_LoginLocalStorage', JSON.stringify(obj));
          } else {
            localStorage.setItem(systemId + '_LoginLocalStorage', JSON.stringify({ [key]: value }));
          }
        }
      };
    },
    createFormInputElement(id, name, value, type = 'hidden') {
      let form = document.querySelector('#form');
      if (form) {
        let input = document.createElement('input');
        input.setAttribute('id', id);
        input.setAttribute('name', name);
        input.setAttribute('type', type);
        input.setAttribute('value', value);
        form.appendChild(input);
        return input;
      }
    },
    submitLoginForm() {
      if (!this.designMode) {
        this.errorMsg = undefined;
        this.logining = true;
        let _submit = () => {
          this.password = encode(encodeURI(this.password));
          let urlParams = queryString(location.search.substring(1));
          if (this.enableRememberPwd) {
            this.systemLocalStorage.set('rememberPassword', this.password);
          }
          if (this.enableRememberAcct) {
            this.systemLocalStorage.set('rememberAcct', this.userName);
          }
          this.createFormInputElement('j_password', 'password', this.password);
          if (['local', 'unittest'].includes(window.__INITIAL_STATE__._CONTEXT_STATE_.ENV) && urlParams.testTokenExpiration != undefined) {
            // 测试环境下允许临时跳转登录的 token 时效性
            this.createFormInputElement('testTokenExpiration', 'testTokenExpiration', urlParams.testTokenExpiration);
          }
          if (this.enableI18nLanguage) {
            this.addCookie(`locale${this._$SYSTEM_ID ? `.${this._$SYSTEM_ID}` : ''}`, this.selectedLanguage);
          }
          this.$nextTick(() => {
            document.querySelector('#form').submit();
          });
        };
        if (this.enableCaptcha) {
          this.checkCaptcha().then(({ success, msg, code }) => {
            if (!success) {
              this.errorMsg = this.$t('loginComponent.error.' + code, msg);
              this.logining = false;
              this.captchaInputErrorCount++;
              if (
                msg == '验证码已失效' ||
                (this.rules.ACCT_PWD_LOGIN &&
                  this.rules.ACCT_PWD_LOGIN.errorCountRefresh != undefined &&
                  this.captchaInputErrorCount == this.rules.ACCT_PWD_LOGIN.errorCountRefresh)
              ) {
                this.refreshCaptcha();
                this.captchaInputErrorCount = 0;
              }
            } else {
              _submit();
            }
          });
        } else {
          _submit();
        }
      }
    },

    requestCaptcha(params) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/captcha`, { params })
          .then(({ data }) => {
            resolve(data);
          })
          .catch(error => {});
      });
    },
    refreshCaptcha() {
      this.requestCaptcha({
        height: 40,
        errorCountRefresh: this.rules.ACCT_PWD_LOGIN ? this.rules.ACCT_PWD_LOGIN.errorCountRefresh : undefined,
        expiredTime: this.rules.ACCT_PWD_LOGIN ? this.rules.ACCT_PWD_LOGIN.loginCheckTimeout : undefined,
        loginBoxAccountCodeType: this.rules.ACCT_PWD_LOGIN ? this.rules.ACCT_PWD_LOGIN.picturePattern : undefined
      }).then(data => {
        this.captcha = data;
      });
    },
    checkCaptcha() {
      return new Promise((resolve, reject) => {
        if (this.inputCaptcha) {
          this.requestCaptcha({
            ignorecase: this.rules.ACCT_PWD_LOGIN.letterCase ? '0' : '1',
            verify: this.inputCaptcha
          }).then(data => {
            resolve(data);
          });
        } else {
          resolve(false);
        }
      });
    },
    showFeishuLoginIfRequired() {
      if (!this.enableFeishuLogin) {
        return;
      }
      let urlParams = queryString(location.search.substring(1));
      if (urlParams.code && urlParams.state == 'feishu_login') {
        this.loginFormKey = 'feishuLogin';
      }

      $axios
        .get('/proxy/api/feishu/config/getEnabledAppId')
        .then(({ data: result }) => {
          if (result.data) {
          } else {
            this.$message.warning(this.$t('loginComponent.feishuConfigError', '飞书接入未启用或未配置应用ID'));
          }
        })
        .catch(error => {});
    },
    feishuSsoLogin() {
      this.deleteCookie('jwt');

      import('../feishu/sso-login.js').then(({ ssoLogin }) => {
        ssoLogin({
          onReady: () => {},
          onLogining: () => {
            this.$loading(this.$t('loginComponent.sso.logining', '单点登录中') + '...');
          },
          onSuccess: () => {
            this.$loading(false);
            this.$message.success(this.$t('loginComponent.sso.success', '单点登录成功'));
            window.location.href = `${window.location.origin}/sys/${this._$SYSTEM_ID}/index`;
          },
          onFail: () => {
            this.$loading(false);
            this.$message.error(this.$t('loginComponent.sso.failed', '单点登录失败'));
          }
        });
      });
    },
    dingtalkSsoLogin() {
      this.deleteCookie('jwt');

      import('../dingtalk/sso-login.js').then(({ ssoLogin }) => {
        ssoLogin({
          onReady: () => {},
          onLogining: () => {
            this.$loading(this.$t('loginComponent.sso.logining', '单点登录中') + '...');
          },
          onSuccess: () => {
            this.$loading(false);
            this.$message.success(this.$t('loginComponent.sso.success', '单点登录成功'));
            window.location.href = `${window.location.origin}/sys/${this._$SYSTEM_ID}/index`;
          },
          onFail: () => {
            this.$loading(false);
            this.$message.error(this.$t('loginComponent.sso.failed', '单点登录失败'));
          }
        });
      });
    }
  }
};
