<template>
  <div id="feishu_qr_login_container">
    <span v-if="disabled">{{ $t('loginComponent.feishuConfigError', '飞书接入未启用或未配置应用ID') }}</span>
  </div>
</template>

<script>
export default {
  name: 'FeishuQrLogin',
  props: {
    language: String
  },
  data() {
    return {
      appId: '',
      disabled: false
    };
  },
  beforeMount() {
    this.loadScriptPromise = new Promise((resolve, reject) => {
      const script = document.createElement('script');
      script.src = 'https://lf-package-cn.feishucdn.com/obj/feishu-static/lark/passport/qrcode/LarkSSOSDKWebQRCode-1.0.3.js';
      script.onload = () => {
        console.log('Feishu qrLogin loaded and ready');
        resolve();
      };
      script.onerror = () => {
        console.error('Error loading feishu qrLogin script');
        reject();
      };
      document.head.appendChild(script);
    });
  },
  mounted() {
    this.getAppId().then(() => {
      this.loadScriptPromise.then(() => {
        this.qrLogin().then(({ tokenInfo }) => {
          if (tokenInfo && tokenInfo.accessToken) {
            this.$loading(this.$t('loginComponent.login', '登录') + '...');
            $axios
              .post(`/login/feishuToken?feishuUserAccessToken=${tokenInfo.accessToken}`)
              .then(({ data: result }) => {
                this.$loading(false);
                window.location.href = `${window.location.origin}/sys/${this._$SYSTEM_ID}/index`;
              })
              .catch(error => {
                this.$loading(false);
                this.$message.error(this.$t('loginComponent.error.failed', '登录失败！'));
              });
          }
        });
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
    qrLogin() {
      const _this = this;
      return new Promise((resolve, reject) => {
        const appId = this.appId; //'cli_a7539bf6c7b3d013'; //process.env.REACT_APP_ID;
        const redirect_uri = `${window.location.origin}/sys/${this._$SYSTEM_ID}/login`;
        $axios
          .post('/login/feishuUserTokenInfo', {
            loginTime: `${new Date().getTime()}`,
            redirect_uri: redirect_uri,
            url: window.location.href
          })
          .then(({ data: result }) => {
            var gotoUrl = `https://passport.feishu.cn/suite/passport/oauth/authorize?client_id=${appId}&redirect_uri=${encodeURI(
              redirect_uri
            )}&response_type=code&state=feishu_login`;
            var QRLoginObj = window.QRLogin({
              id: 'feishu_qr_login_container',
              goto: `${gotoUrl}`,
              style:
                'width: 260px;height: 260px; border: 0;box-shadow: 0px 0px 8px 0px rgba(0, 0, 0, 0.12);position: relative;left: 50%;margin-left: -130px;transform: scale(0.78);top: -26px;margin-bottom: -55px;'
            });
            var handleMessage = function (event) {
              var origin = event.origin;
              if (QRLoginObj.matchOrigin(origin) && window.location.href.indexOf('/login') > -1) {
                if (_this.language) {
                  _this.addCookie('locale', _this.language);
                }
                var loginTmpCode = event.data.tmp_code;
                window.location.href = `${gotoUrl}&tmp_code=${loginTmpCode}`;
              }
            };
            if (typeof window.addEventListener !== 'undefined') {
              window.addEventListener('message', handleMessage, false);
            } else if (typeof window.attachEvent !== 'undefined') {
              window.attachEvent('onmessage', handleMessage);
            }
            if (result.code == 0) {
              resolve({ tokenInfo: result.data });
            }
          })
          .catch(err => {
            resolve(false);
          });
      });
    },
    getAppId() {
      return $axios
        .get('/proxy/api/feishu/config/getEnabledAppId')
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
