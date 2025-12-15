<template>
  <div style="position: absolute; left: 0px; right: 0px; width: 100%; height: 100%" class="login-basic">
    <a-carousel
      effect="fade"
      :autoplay="true"
      :pauseOnHover="false"
      :dots="false"
      :autoplaySpeed="vConfig.backgroundCarousel.duration * 1000"
      v-if="vConfig.backgroundCarousel.enable"
      :style="{
        position: 'absolute',
        top: '0px',
        left: '0px',
        width: '100%',
        height: '100%',
        backgroundColor: 'var(' + vConfig.backgroundColor + ')' || 'transparent'
      }"
    >
      <div
        v-for="(item, i) in vConfig.backgroundImage"
        :style="{
          backgroundImage: `url('${item}')`,
          backgroundSize: 'cover',
          height: '100%'
        }"
      ></div>
    </a-carousel>
    <a-dropdown v-if="enableI18nLanguage" class="login-language-dropdown">
      <a-button type="text" icon="global" style="width: auto">
        {{ selectedLanguageText }}
        <Icon type="caret-down" style="margin-left: 10px"></Icon>
      </a-button>
      <a-menu slot="overlay" @click="onSelectLanguage">
        <template v-for="(option, i) in languageOptions">
          <a-menu-item :key="option.value">{{ option.label }}</a-menu-item>
        </template>
      </a-menu>
    </a-dropdown>
    <div v-if="config.logo" class="login-head" :style="logoHeaderStyle">
      <img :src="config.logo" class="logo" :style="vLogoStyle" />
    </div>
    <div v-if="config.title" class="ql-editor" :style="titleHeaderStyle">
      <div v-html="vSystemTitle" class="title" :style="vTitleStyle"></div>
    </div>
    <div :style="vStyle" class="login-basic-center">
      <div class="login-win" :style="loginWinStyle" @click.stop="() => {}">
        <div class="title-bar" v-if="vConfig.loginWin.titleBar.enable" :style="titleBarStyle">
          <img
            v-if="vConfig.loginWin.titleBar.logo.enable && vConfig.loginWin.titleBar.logo.src != undefined"
            :src="vConfig.loginWin.titleBar.logo.src"
            class="logo"
            :style="loginWinLogoStyle"
          />
          <div
            v-if="vConfig.loginWin.titleBar.title.enable && vConfig.loginWin.titleBar.title.content"
            class="title ql-editor"
            :style="loginWinTitleStyle"
            v-html="vLoginWinTitle"
          ></div>
        </div>
        <a-tabs default-active-key="accountLogin" size="small">
          <a-tab-pane key="accountLogin" :tab="$t('loginComponent.acctLogin', '账号登录')" class="acct-login-tab">
            <div class="rowInput">
              <div class="row">
                <a-input v-model.trim="userName" size="large" :placeholder="$t('loginComponent.acctInputPlaceholder', '请输入用户名')">
                  <template slot="prefix">
                    <a-icon type="user" />
                  </template>
                </a-input>
              </div>
              <div class="row">
                <a-input-password v-model="password" size="large" :placeholder="$t('loginComponent.pwdInputPlaceholder', '请输入密码')">
                  <template slot="prefix">
                    <a-icon type="lock" />
                  </template>
                </a-input-password>
              </div>
              <div class="row" v-if="enableCaptcha" style="display: flex">
                <a-input v-model="inputCaptcha" style="margin-right: 10px" size="large">
                  <template slot="prefix">
                    <a-icon type="code" />
                  </template>
                </a-input>
                <span v-html="captcha" @click.stop="refreshCaptcha"></span>
              </div>
            </div>
            <div class="row">
              <a-button type="primary" :loading="logining" :block="true" @click.stop="submitLoginForm" size="large">
                {{ $t('loginComponent.login', '登录') }}
              </a-button>
            </div>
            <div
              class="row acct-pwd-reg-forget-link-row"
              v-if="enableRememberAcct || enableRememberPwd || enableForgetPwd || enableUserRegister"
            >
              <div v-if="enableRememberAcct || enableRememberPwd">
                <a-checkbox v-model="rememberAccount" v-if="enableRememberAcct">
                  {{ $t('loginComponent.rememberAcct', '记住账号') }}
                </a-checkbox>
                <a-checkbox v-model="rememberPassword" v-if="enableRememberPwd">
                  {{ $t('loginComponent.rememberPwd', '记住密码') }}
                </a-checkbox>
              </div>
              <div v-if="enableForgetPwd || enableUserRegister">
                <a-button
                  size="small"
                  type="link"
                  v-if="enableForgetPwd"
                  @click="openUserForgetPwdPage(rules.ACCT_PWD_LOGIN.userForgetPwdTarget)"
                >
                  {{ $t('loginComponent.forgetPwd', '忘记密码') }}
                </a-button>
                <a-divider type="vertical" style="margin: 0px" v-if="enableForgetPwd && enableUserRegister" />
                <a-button size="small" type="link" v-if="enableUserRegister" @click="openUserRegPage(rules.ACCT_PWD_LOGIN.userRegTarget)">
                  {{ $t('loginComponent.register', '注册') }}
                </a-button>
              </div>
            </div>
          </a-tab-pane>
          <a-tab-pane
            v-if="enableFeishuLogin"
            key="feishuLogin"
            :tab="$t('loginComponent.feishuQrLogin', '飞书扫码登录')"
            class="acct-login-tab"
          >
            <FeishuQrLogin :language="selectedLanguage"></FeishuQrLogin>
          </a-tab-pane>
          <a-tab-pane
            v-if="enableDingtalkLogin"
            key="dingtalkLogin"
            :tab="$t('loginComponent.dingtalkQrLogin', '钉钉扫码登录')"
            class="acct-login-tab"
          >
            <DingtalkQrLogin :language="selectedLanguage" :rules="rules.DING_LOGIN"></DingtalkQrLogin>
          </a-tab-pane>
          <a-tab-pane
            v-if="enableWeixinLogin"
            key="weixinLogin"
            :tab="$t('loginComponent.weixinQrLogin', '企业微信扫码登录')"
            class="acct-login-tab"
          >
            <WeixinQrLogin :language="selectedLanguage"></WeixinQrLogin>
          </a-tab-pane>
        </a-tabs>
        <a-alert type="error" :message="$t('loginComponent.error.' + errorMsg, errorMsg)" banner :show-icon="false" v-if="errorMsg" />
      </div>
    </div>

    <div class="login-footer ql-editor" v-if="config.footer">
      <span v-html="vFooterContent" class="footer"></span>
    </div>

    <form action="/login" method="post" id="form" name="form" class="hidden">
      <input id="j_username" name="username" type="hidden" v-model.trim="userName" />
      <input id="j_lnalg_code" name="j_lnalg_code" type="hidden" value="1" />
      <input id="j_pwdalg_code" name="j_pwdalg_code" type="hidden" value="2" />
      <input id="unitId" name="unitId" type="hidden" />
      <input id="loginType" name="loginType" type="hidden" value="1" />
      <input id="textCert" name="textCert" type="hidden" />
      <input id="textSignData" name="textSignData" type="hidden" />
      <input id="idNumber" name="idNumber" type="hidden" />
      <input id="loginSource" name="loginSource" type="hidden" value="1" />
      <input id="certType" name="certType" type="hidden" />
      <input type="hidden" name="_csrf" :value="csrf" />
      <input id="userOs" name="userOs" type="hidden" />
      <input id="browser" name="browser" type="hidden" />
      <input id="browserVersion" name="browserVersion" type="hidden" />
    </form>
  </div>
</template>
<style lang="less">
.login-basic {
  .ql-editor {
    position: absolute;
    width: 100%;
    z-index: 1;
    height: auto;
    min-height: 0px;
    padding: 0px;
  }
  .ant-carousel {
    div {
      height: 100%;
    }
  }
  .login-head {
    position: absolute;
    min-height: 0px;
    // width: 100%;
    // display: flex;
    z-index: 1;
    // align-items: center;
  }
  .login-footer {
    position: absolute;
    z-index: 1;
    bottom: 0px;
    height: auto;
    min-height: 0px;
    width: 100%;
    text-align: center;
    padding: 25px 0px;
  }
  .login-language-dropdown {
    position: fixed;
    right: 20px;
    top: 24px;
    z-index: 2;
    --w-button-background: rgba(255, 255, 255, 0.1);
    --w-button-background-hover: rgba(255, 255, 255, 0.5);
    --w-button-background-active: rgba(255, 255, 255, 0.5);
    --w-button-font-color-hover: var(--w-text-color-dark);
    --w-button-font-color-active: var(--w-text-color-dark);
  }
}
</style>
<script type="text/babel">
import '@pageAssembly/app/web/lib/quill-editor/quill-editor.less';
import thumbnail from './thumbnail.png';
import loginMixin from './login.mixin';
import '../../index.less';
import FeishuQrLogin from '../feishu/qr-login.vue';
import DingtalkQrLogin from '../dingtalk/qr-login.vue';
import WeixinQrLogin from '../weixin/qr-login.vue';
export default {
  name: 'LoginBasic',
  mixins: [loginMixin],
  components: { FeishuQrLogin, DingtalkQrLogin, WeixinQrLogin },
  order: 1,
  thumbnail,
  computed: {
    vConfig() {
      return this.config;
    },
    logoHeaderStyle() {
      let style = {};
      if (this.vConfig.logo) {
        if (this.vConfig.logoHAlign) {
          if (this.vConfig.logoHAlign == 'center') {
            style.left = '50%';
            if (this.vConfig.logoWidth) {
              style.marginLeft = 0 - this.vConfig.logoWidth / 2 + 'px';
            }
          } else if (this.vConfig.logoHAlign == 'right') {
            style.right = '0px';
          }
        }
        if (this.vConfig.logoVAlign) {
          if (this.vConfig.logoVAlign == 'center') {
            style.top = '50%';
            if (this.vConfig.logoWidth) {
              style.marginTop = 0 - this.vConfig.logoHeight / 2 + 'px';
            }
          } else if (this.vConfig.logoVAlign == 'bottom') {
            style.bottom = '0px';
          }
        }
      }
      return style;
    },
    titleHeaderStyle() {
      let style = {};
      if (this.vConfig.title) {
        if (this.vConfig.titleVAlign) {
          if (this.vConfig.titleVAlign == 'center') {
            style.top = '50%';
          } else if (this.vConfig.titleVAlign == 'bottom') {
            style.bottom = '0px';
          }
        }
      }
      return style;
    },
    vStyle() {
      let style = {};
      if (!this.vConfig.backgroundCarousel.enable) {
        style.backgroundImage = `url('${this.vConfig.backgroundImage[0]}')`;
        if (this.vConfig.backgroundColor) {
          style.backgroundColor = `var(${this.vConfig.backgroundColor})`;
        }
      }

      return style;
    },
    titleBarStyle() {
      let style = {};
      style['flex-direction'] = this.vConfig.loginWin.titleBar.layout == 'horizontal' ? 'row' : 'column';
      style['color'] = 'var(--w-primary-color)';
      return style;
    },
    loginWinStyle() {
      let style = {},
        loginWin = this.vConfig.loginWin;
      if (loginWin.primaryColor) {
        style['--w-primary-color'] = loginWin.primaryColor;
      }
      if (loginWin.backgroundColor) {
        style.backgroundColor = loginWin.backgroundColor;
      }
      if (loginWin.backgroundImage) {
        style.backgroundImage = `url('${loginWin.backgroundImage}')`;
        style.backgroundSize = 'cover';
      }

      return style;
    },
    vTitleStyle() {
      let style = {};
      if (this.vConfig.titleMargin) {
        style.margin = this.vConfig.titleMargin.join('px ') + 'px';
      }
      return style;
    },
    vLogoStyle() {
      let style = {};
      if (this.vConfig.logoWidth) {
        style.width = this.vConfig.logoWidth + 'px';
      }
      if (this.vConfig.logoHeight) {
        style.height = this.vConfig.logoHeight + 'px';
      }
      if (this.vConfig.logoMargin) {
        style.margin = this.vConfig.logoMargin.join('px ') + 'px';
      }
      return style;
    },
    loginWinLogoStyle() {
      let style = {},
        titleBar = this.vConfig.loginWin.titleBar,
        title = this.vConfig.loginWin.titleBar.title,
        logo = this.vConfig.loginWin.titleBar.logo;
      if (logo.width) {
        style.width = logo.width + 'px';
      }
      if (logo.height) {
        style.height = logo.height + 'px';
      }
      style['object-fit'] = 'contain';
      return style;
    },
    loginWinTitleStyle() {
      let style = {},
        titleBar = this.vConfig.loginWin.titleBar,
        title = this.vConfig.loginWin.titleBar.title,
        logo = this.vConfig.loginWin.titleBar.logo;
      if (title.width) {
        style.width = title.width + 'px';
      }
      if (title.height && titleBar.layout === 'vertical') {
        style.height = title.height + 'px';
      }
      if (logo.enable && logo.src && titleBar.gutter) {
        style[titleBar.layout === 'horizontal' ? 'margin-left' : 'margin-top'] = titleBar.gutter + 'px';
      }
      return style;
    }
  },
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (this.enableCaptcha) {
      this.refreshCaptcha();
    }
  },
  mounted() {},
  methods: {}
};
</script>
