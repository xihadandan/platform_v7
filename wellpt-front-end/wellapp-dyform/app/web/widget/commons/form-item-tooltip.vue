<template>
  <span>
    <span>{{ label }}</span>
    <a-tooltip placement="topRight" :arrowPointAtCenter="true">
      <template #title>
        <div v-html="text"></div>
      </template>
      <a-icon type="exclamation-circle" />
    </a-tooltip>
  </span>
</template>
<style>
</style>
<script type="text/babel">
export default {
  name: 'FormItemTooltip',
  mixins: [],
  props: {
    label: {
      type: String,
      default: '字段编码'
    },
    text: {
      type: String,
      default:
        '1 单个表单中的字段编码不可重复 <br /> 2 只能以下划线或小写英文字母开头 <br /> 3 仅支持字母、数字、下划线 <br /> 4 不能为系统预留字段'
    }
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
