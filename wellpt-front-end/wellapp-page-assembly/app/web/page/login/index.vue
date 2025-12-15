<template>
  <HtmlWrapper>
    <component
      :is="loginConfig.type"
      :config="loginConfig.config"
      :errorMsg="loginErrorMsg"
      ref="login"
      v-if="loginConfig.type"
      :rules="rules"
    />
    <a-result status="404" title="未配置登录页" v-else></a-result>
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import './component/index';

export default {
  name: 'LoginRender',
  props: {},
  components: {},
  computed: {
    loginErrorMsg() {
      let message = this.errorMsg || '';
      let i18nMsg = this.$t('LoginRender.loginFailedAgain', null);
      if (i18nMsg) {
        message = message.replace(new RegExp('登录失效，请重新登录!', 'g'), i18nMsg);
      }
      return message;
    }
  },
  data() {
    return {
      rules: {}
    };
  },
  beforeCreate() {},
  created() {
    if (this.loginPolicy) {
      // 登陆方式规则
      for (let i = 0, len = this.loginPolicy.length; i < len; i++) {
        let policy = this.loginPolicy[i];
        if (policy.enabled) {
          this.rules[this.loginPolicy[i].id] = JSON.parse(policy.defJson);
        }
      }

      console.log('登录方式规则', this.rules);
    }
  },
  mounted() {},
  methods: {}
};
</script>
