<template>
  <HtmlWrapper :title="title">
    <WidgetVpage
      v-if="acctPwdLoginPolicy.userRegPageType == 'page' && acctPwdLoginPolicy.userRegPageId"
      :pageId="acctPwdLoginPolicy.userRegPageId"
      :security="false"
      @updatePageTitle="onUpdatePageTitle"
    />
    <iframe
      v-else-if="acctPwdLoginPolicy.userRegPageType == 'url' && acctPwdLoginPolicy.userRegUrl"
      :src="acctPwdLoginPolicy.userRegUrl"
      :style="{ minHeight: 'calc(100vh)', border: 'none', width: '100%', height: '100%', display: 'block' }"
    />
    <Error v-else :error-code="404" />
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import '@installPageWidget';
export default {
  name: 'UserReg',
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
          this.title = this.acctPwdLoginPolicy.userRegPageTitle || '注册';
          if (this.acctPwdLoginPolicy.userRegUrl.startsWith('/')) {
            // 增加crsf token
            if (EASY_ENV_IS_BROWSER && this.acctPwdLoginPolicy.userRegUrl) {
              this.acctPwdLoginPolicy.userRegUrl +=
                (this.acctPwdLoginPolicy.userRegUrl.indexOf('?') != -1 ? '&' : '?') + '_csrf=' + window.__INITIAL_STATE__.csrf;
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
      if (!this.acctPwdLoginPolicy.userRegPageTitle) {
        this.title = e;
      }
    }
  }
};
</script>
