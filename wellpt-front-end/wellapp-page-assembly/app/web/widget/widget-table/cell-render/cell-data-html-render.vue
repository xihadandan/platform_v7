<template>
  <span v-html="text"></span>
</template>
<style lang="less"></style>
<script type="text/babel">
import cellRenderMixin from './cellRenderMixin';
export default {
  name: 'CellDataHtmlRender',
  mixins: [cellRenderMixin],
  title: 'HTML代码渲染器',
  props: {},
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
