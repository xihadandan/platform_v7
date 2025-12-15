<template>
  <CreatePage ref="page" isBigscreen />
</template>
<style lang="less"></style>
<script type="text/babel">
import CreatePage from './create-page.vue';

export default {
  name: 'CreateBigscreen',
  inject: ['currentModule'],
  props: {
    groupOptions: Array,
    isPc: Boolean
  },
  components: { CreatePage },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},

  mounted() {},
  methods: {
    save(callback) {
      this.$refs.page.save(callback);
    }
  }
};
</script>
