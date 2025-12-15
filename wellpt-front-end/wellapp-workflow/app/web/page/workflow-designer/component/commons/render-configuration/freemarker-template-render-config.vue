<template>
  <a-form-model-item label="模板">
    <a-textarea
      v-model="options.template"
      :auto-size="{ minRows: 6, maxRows: 10 }"
      placeholder="请输入freemarker支持的语法表达式，内置变量${value}当前字段值，${columnIndex}当前列索引，${rowData}当前行数据"
    />
  </a-form-model-item>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'freemarkerTemplateRendererConfig',
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
