<template>
  <HtmlWrapper :title="title">
    <ModDataCleanup />
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import ModDataCleanup from '../../template/system-tools/mod-data-cleanup.vue';
export default {
  name: 'DataCleanupIndex',
  props: {},
  components: { ModDataCleanup },
  computed: {},
  data() {
    return { title: '数据清理' };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
