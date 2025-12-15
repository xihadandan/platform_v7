<template>
  <div>
    <component :is="widget.configuration.useBorderStyle" :widget="widget" />
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import BorderStyle1 from './border-style/border-style-1.vue';
import BorderStyle2 from './border-style/border-style-2.vue';

export default {
  name: 'WidgetBorderStyle',
  mixins: [widgetMixin],
  props: {},
  components: { BorderStyle1, BorderStyle2 },
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
