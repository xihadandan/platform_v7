<template>
  <div id="dingtalk_qr_login_container"></div>
</template>

<script>
export default {
  name: 'DingtalkQrLogin',
  props: {
    rules: Object,
    language: String
  },
  beforeMount() {
    this.loadScriptPromise = new Promise((resolve, reject) => {
      const script = document.createElement('script');
      script.src = 'https://g.alicdn.com/dingding/h5-dingtalk-login/0.21.0/ddlogin.js';
      script.onload = () => {
        console.log('Dingtalk qrLogin loaded and ready');
        resolve();
      };
      script.onerror = () => {
        console.error('Error loading dingtalk qrLogin script');
        reject();
      };
      document.head.appendChild(script);
    });
  },
  mounted() {
    this.loadScriptPromise.then(() => {
      this.getClientId().then(clientId => {
        this.showQrLogin(clientId);
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
    showQrLogin(clientId) {
      const _this = this;
      // 在需要的时候，调用 window.DTFrameLogin 方法构造登录二维码，并处理登录成功或失败的回调。
      const redirect_uri = `${window.location.origin}/sys/${this._$SYSTEM_ID}/login`;
      window.DTFrameLogin(
        {
          id: 'dingtalk_qr_login_container',
          width: 300,
          height: 300
        },
        {
          redirect_uri: encodeURIComponent(redirect_uri),
          client_id: clientId,
          scope: 'openid',
          response_type: 'code',
          state: 'dingtalk_login',
          prompt: 'consent'
        },
        loginResult => {
          const { redirectUrl, authCode, state } = loginResult;
          // 这里可以直接进行重定向
          // window.location.href = redirectUrl;
          // 也可以在不跳转页面的情况下，使用code进行授权
          console.log(authCode);
          if (_this.language) {
            _this.addCookie('locale', _this.language);
          }
          _this.doLogin(authCode);
        },
        errorMsg => {
          // 这里一般需要展示登录失败的具体原因,可以使用toast等轻提示
          console.error(`errorMsg of errorCbk: ${errorMsg}`);
        }
      );
    },
    doLogin(authCode) {
      this.$loading(this.$t('loginComponent.login', '登录') + '...');
      $axios
        .post(`/login/dingtalkAuth?code=${authCode}`)
        .then(({ data: result }) => {
          this.$loading(false);
          window.location.href = `${window.location.origin}/sys/${this._$SYSTEM_ID}/index`;
        })
        .catch(error => {
          this.$loading(false);
          this.$message.error(this.$t('loginComponent.error.failed', '登录失败！'));
        });
    },
    getClientId() {
      return $axios
        .get('/proxy/api/dingtalk/config/getEnabledClientId')
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

<style></style>
