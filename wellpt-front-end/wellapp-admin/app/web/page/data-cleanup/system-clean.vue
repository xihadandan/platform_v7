<template>
  <HtmlWrapper :title="title">
    <DataCleanup />
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import DataCleanup from '../../template/system-tools/data-cleanup.vue';
export default {
  name: 'SystemCleanup',
  props: {},
  components: { DataCleanup },
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
