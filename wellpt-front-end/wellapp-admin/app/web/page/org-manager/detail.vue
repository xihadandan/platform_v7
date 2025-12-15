<template>
  <HtmlWrapper :title="title">
    <OrgDetailSetting :orgUuid="uuid" :orgId="id" @changeOrgVersion="changeOrgVersion" />
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import OrgDetailSetting from '../../template/organization/org-detail-setting.vue';
export default {
  name: 'OrgManagerDetail',
  props: {},
  components: { OrgDetailSetting },
  computed: {},
  data() {
    return { title: '组织版本' };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    changeOrgVersion(version) {
      if (version != undefined && version.organization != undefined) {
        this.title = version.organization.name;
      }
    }
  }
};
</script>
