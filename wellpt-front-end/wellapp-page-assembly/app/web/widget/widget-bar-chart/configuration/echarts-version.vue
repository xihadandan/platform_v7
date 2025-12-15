<template>
  <div style="text-align: right; padding-right: 21px; color: var(--w-primary-color)">
    <label style="cursor: pointer; font-size: 11px" @click="redirectToEchartsIndex">ECharts 版本{{ version }}</label>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { version } from 'echarts';
export default {
  name: 'EchartsVersion',
  props: {},
  components: {},
  computed: {},
  data() {
    return { version };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    redirectToEchartsIndex() {
      window.open('https://echarts.apache.org/examples/zh/index.html', '_blank');
    }
  }
};
</script>
