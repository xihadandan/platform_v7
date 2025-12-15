<template>
  <a-form-model-item label="展示类型">
    <a-radio-group v-model="options.showType">
      <a-radio value="0">展示全路径</a-radio>
      <a-radio value="1">展示名称</a-radio>
    </a-radio-group>
  </a-form-model-item>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'orgElementRendererConfig',
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
  created() {},
  methods: {},
  mounted() {}
};
</script>
