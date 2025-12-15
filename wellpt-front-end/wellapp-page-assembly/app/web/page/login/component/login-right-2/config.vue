<template>
  <LoginBasicConfig :config="config" />
</template>
<style lang="less"></style>
<script type="text/babel">
import LoginBasicConfig from '../login-basic/config.vue';
export default {
  name: 'LoginRight2Config',
  props: {
    config: Object
  },
  components: { LoginBasicConfig },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    generateDefaultConfig() {
      return {
        backgroundColor: undefined,
        backgroundImage: ['/static/images/login/login_page-bg_3.png'],
        backgroundCarousel: {
          enable: false,
          duration: 3
        },
        logo: '/static/images/login/logo-vertical.svg',
        logoWidth: 250,
        logoHeight: 100,
        logoMargin: [0, 0, 0, 0],
        title: undefined,
        titleMargin: [0, 0, 0, 0],
        footer: undefined,
        loginWin: {
          primaryColor: '#488cee',
          backgroundColor: '#fff',
          backgroundImage: undefined,
          titleBar: {
            enable: false,
            layout: 'horizontal',
            gutter: 16,
            logo: {
              enable: false,
              width: undefined,
              height: undefined,
              src: undefined
            },
            title: {
              enable: false,
              width: undefined,
              height: undefined,
              content: undefined
            }
          }
        } // 登录窗口设置
      };
    }
  }
};
</script>
