<template>
  <div id="weixin_qr_login_container"></div>
</template>

<script>
export default {
  name: 'WeixinQrLogin',
  props: {
    language: String
  },
  beforeMount() {
    this.loadScriptPromise = new Promise((resolve, reject) => {
      const script = document.createElement('script');
      script.src = 'https://wwcdn.weixin.qq.com/node/open/js/wecom-jssdk-2.0.2.js';
      script.onload = () => {
        console.log('Weixin qrLogin loaded and ready');
        resolve();
      };
      script.onerror = () => {
        console.error('Error loading weixin qrLogin script');
        reject();
      };
      document.head.appendChild(script);
    });
  },
  mounted() {
    this.loadScriptPromise.then(() => {
      this.getAppInfo().then(appInfo => {
        this.showQrLogin(appInfo.corpId, appInfo.appId);
      });
    });
    document.cookie = encodeURIComponent('jwt') + '=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/';
  },
  methods: {
    addCookie(name, value) {
      var exp = new Date();
      exp.setTime(exp.getTime() + +7 * 24 * 60 * 60 * 1000);
      document.cookie = name + '=' + value + ';expires=' + exp.toGMTString() + '; path=/';
    },
    showQrLogin(corpId, agentId) {
      const _this = this;
      const redirect_uri = `${window.location.origin}/sys/${this._$SYSTEM_ID}/login`;
      // 初始化登录组件
      const wwLogin = ww.createWWLoginPanel({
        el: '#weixin_qr_login_container',
        params: {
          login_type: 'CorpApp',
          appid: corpId,
          agentid: agentId,
          redirect_uri,
          state: 'weixin_login',
          redirect_type: 'callback',
          lang: _this.language == 'en_US' ? 'en' : 'zh'
        },
        onCheckWeComLogin({ isWeComLogin }) {
          console.log(isWeComLogin);
        },
        onLoginSuccess({ code }) {
          console.log({ code });
          if (_this.language) {
            _this.addCookie('locale', _this.language);
          }
          _this.doLogin(code);
        },
        onLoginFail(err) {
          console.log(err);
        }
      });
    },
    doLogin(authCode) {
      this.$loading(this.$t('loginComponent.login', '登录') + '...');
      $axios
        .post(`/login/weixinAuth?code=${authCode}`)
        .then(({ data: result }) => {
          this.$loading(false);
          window.location.href = `${window.location.origin}/sys/${this._$SYSTEM_ID}/index`;
        })
        .catch(error => {
          this.$loading(false);
          this.$message.error(this.$t('loginComponent.error.failed', '登录失败！'));
        });
    },
    getAppInfo() {
      return $axios
        .get('/proxy/api/weixin/config/getEnabledAppInfo')
        .then(({ data: result }) => {
          if (result.data) {
            this.appId = result.data;
            return Promise.resolve(result.data);
          } else {
            this.disabled = true;
            return Promise.reject();
          }
        })
        .catch(error => {
          this.disabled = true;
          return Promise.reject();
        });
    }
  }
};
</script>

<style lang="less" scoped>
#weixin_qr_login_container {
  position: relative;
  left: -60px;
}
</style>
