<template>
  <!--
    不在此处实现用户个性化定制，是因为用户个性化设置需要即时预览布局效果，布局刷新会销毁该组件
    因此把用户个性化组件放到页面层级上，通过这里进行唤醒
  -->
  <span @click.stop="openUserPreferenceCustomize">
    <slot></slot>
  </span>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'HeaderThemeSet',
  props: {},
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    openUserPreferenceCustomize() {
      if (this.$root.$refs.userPreferenceCustomize != undefined) {
        this.$root.$refs.userPreferenceCustomize.visible = true;
      }
    }
  }
};
</script>
