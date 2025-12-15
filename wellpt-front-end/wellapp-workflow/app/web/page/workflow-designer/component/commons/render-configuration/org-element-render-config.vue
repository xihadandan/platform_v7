<template>
  <a-form-model-item label="展示类型">
    <a-radio-group v-model="options.showType">
      <a-radio value="titlePath">展示全路径</a-radio>
      <a-radio value="title">展示名称</a-radio>
    </a-radio-group>
  </a-form-model-item>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'OrgElementRenderConfig',
  mixins: [],
  props: {
    options: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    if (!this.options.showType) {
      this.$set(this.options, 'showType', 'title');
    }
  },
  methods: {},
  mounted() {}
};
</script>
