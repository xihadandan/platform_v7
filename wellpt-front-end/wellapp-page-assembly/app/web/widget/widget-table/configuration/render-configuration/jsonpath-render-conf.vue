<template>
  <a-form-model-item label="表达式">
    <a-textarea
      v-model="options.jsonpath"
      :auto-size="{ minRows: 6, maxRows: 10 }"
      placeholder="请输入jsonpath支持的语法表达式，例如：$..book[0]"
    />
  </a-form-model-item>
</template>
<style>
</style>
<script type="text/babel">
export default {
  name: 'JsonPathRendererConfig',
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
