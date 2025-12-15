<template>
  <a-layout-footer>
    <template v-for="(wgt, index) in widget.configuration.widgets">
      <component
        :key="wgt.id"
        :is="resolveWidgetType(wgt)"
        :widget="wgt"
        :index="index"
        :widgetsOfParent="widget.configuration.widgets"
        :designer="designer"
      ></component>
    </template>
  </a-layout-footer>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
export default {
  name: 'WidgetLayoutFooter',
  mixins: [widgetMixin],

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
