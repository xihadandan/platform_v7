<template>
  <div>
    <Icon :type="text" style="font-size: 20px" :size="32"></Icon>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import cellRenderMixin from './cellRenderMixin';
export default {
  name: 'CellDataIconRender',
  mixins: [cellRenderMixin],
  title: '图标渲染器',
  props: {},
  components: {},
  computed: {},
  data() {
    return {
      loading: false
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
