<template>
  <HtmlWrapper title="登录">
    <div class="spin-center" v-if="loading">
      <a-spin />
    </div>
    <template v-else>
      <component :is="loginConf.type" :config="loginConf.config" v-if="loginConf.type" />
      <a-result status="404" title="404" sub-title="数据丢失" v-else>
        <template #extra></template>
      </a-result>
    </template>
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
// import '../../login-widget/index';
import '../login/component/index.js';

export default {
  name: 'LoginDesignPreview',
  props: {},
  components: {},
  computed: {},
  provide() {
    return {
      designMode: true
    };
  },
  data() {
    return {
      loading: true,
      loginConf: { type: undefined, config: {} }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    let item = sessionStorage.getItem(this.uuid);
    if (item) {
      this.loginConf = JSON.parse(item);
    }
    this.loading = false;
  },
  methods: {}
};
</script>
