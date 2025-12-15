<template>
  <a-popover title="常用正则表达式">
    <template slot="content">
      <a-row type="flex" v-for="(opt, i) in options" :key="'reg_' + i" style="flex-wrap: nowrap; margin-bottom: 8px; width: 480px">
        <a-col flex="150px">{{ opt.label }}</a-col>
        <a-col flex="auto">
          <a-tag @click="e => onClickCopy(e, opt.value)">{{ opt.value }}</a-tag>
        </a-col>
      </a-row>
    </template>
    <a-icon type="info-circle" theme="filled" />
  </a-popover>
</template>
<style lang="less"></style>
<script type="text/babel">
import { copyToClipboard } from '@framework/vue/utils/util';

export default {
  name: 'JavaRegExpExamplePopover',
  props: {},
  components: {},
  computed: {},
  data() {
    return {
      options: [
        {
          label: '电子邮件',
          value: '[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}'
        },
        {
          label: '整数',
          value: '\\d+'
        },
        {
          label: '浮点数',
          value: '-?\\d+(\\.\\d+)?'
        },

        {
          label: '手机号码',
          value: '^1[3-9]\\d{9}$'
        },
        {
          label: '邮政编码(6位)',
          value: '\\d{6}'
        },
        {
          label: '中文',
          value: '[\\u4e00-\\u9fa5]+'
        },
        {
          label: '匹配固定长度字符串',
          value: '^\\w{8}$'
        }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onClickCopy(e, text) {
      let _this = this;
      copyToClipboard(text, e, function (success) {
        if (success) {
          _this.$message.success('已复制');
        }
      });
    }
  }
};
</script>
