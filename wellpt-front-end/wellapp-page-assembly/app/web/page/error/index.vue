<template>
  <HtmlWrapper :title="vTitle">
    <Error :error-code="errorCode" :sub-title="subTitle" />
  </HtmlWrapper>
</template>
<style lang="less" scoped></style>
<script type="text/babel">
export default {
  name: 'ErrorIndex',
  props: {},
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {
    vTitle() {
      if (this.title.startsWith('SystemErrorCode.')) {
        return this.$t(this.title, this.errorCode);
      }
      return this.title;
    }
  },
  created() {},

  methods: {},
  mounted() {}
};
</script>
