<template>
  <div>
    <component :is="widget.configuration.useDecoration" :widget="widget" />
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import Decoration from './decoration-style/index.js';
export default {
  name: 'WidgetDecoration',
  mixins: [widgetMixin],
  props: {},
  components: { ...Decoration },
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
