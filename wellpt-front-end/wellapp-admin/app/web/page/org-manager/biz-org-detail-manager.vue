<template>
  <HtmlWrapper :title="title">
    <BizOrgDetail :bizOrgUuid="uuid" />
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import BizOrgDetail from '../../template/organization/biz-org/biz-org-detail.vue';
export default {
  name: 'BizOrgDetailManager',
  props: {},
  components: { BizOrgDetail },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
