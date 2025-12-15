<template>
  <HtmlWrapper :title="title">
    <WidgetVpage
      v-if="acctPwdLoginPolicy.userForgetPwdPageType == 'page' && acctPwdLoginPolicy.userForgetPwdPageId"
      :pageId="acctPwdLoginPolicy.userForgetPwdPageId"
      :security="false"
      @updatePageTitle="onUpdatePageTitle"
    />
    <iframe
      v-else-if="acctPwdLoginPolicy.userForgetPwdPageType == 'url' && acctPwdLoginPolicy.userForgetPwdUrl"
      :src="acctPwdLoginPolicy.userForgetPwdUrl"
      :style="{ minHeight: 'calc(100vh)', border: 'none', width: '100%', height: '100%', display: 'block' }"
    />
    <Error v-else :error-code="404" />
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import '@installPageWidget';
export default {
  name: 'UserForgetPwd',
  props: {},
  components: {},
  computed: {},
  data() {
    return {
      loading: true,
      rules: {},
      acctPwdLoginPolicy: {}
    };
  },
  beforeCreate() {},
  created() {
    if (this.loginPolicy) {
      for (let i = 0, len = this.loginPolicy.length; i < len; i++) {
        let policy = this.loginPolicy[i];
        if (policy.id == 'ACCT_PWD_LOGIN') {
          this.acctPwdLoginPolicy = JSON.parse(policy.defJson);
          this.title = this.acctPwdLoginPolicy.userForgetPwdPageTitle || '忘记密码';
          if (this.acctPwdLoginPolicy.userForgetPwdUrl.startsWith('/')) {
            // 增加crsf token
            if (EASY_ENV_IS_BROWSER && this.acctPwdLoginPolicy.userForgetPwdUrl) {
              this.acctPwdLoginPolicy.userForgetPwdUrl +=
                (this.acctPwdLoginPolicy.userForgetPwdUrl.indexOf('?') != -1 ? '&' : '?') + '_csrf=' + window.__INITIAL_STATE__.csrf;
            }
          }
          break;
        }
      }
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    onUpdatePageTitle(e) {
      if (!this.acctPwdLoginPolicy.userForgetPwdPageTitle) {
        this.title = e;
      }
    }
  }
};
</script>
