<template>
  <selectRefetchEventConfiguration :widget="widget" :eventHandler="eventHandler" :fromWidget="fromWidget" :designer="designer" />
</template>
<style lang="less"></style>
<script type="text/babel">
import selectRefetchEventConfiguration from '../../../widget-form-select/configuration/event/select-refetch-event-configuration.vue';
export default {
  name: 'WidgetFormTagRefreshOptionsEventConfiguration',
  props: {
    widget: Object,
    fromWidget: Object,
    designer: Object,
    eventHandler: Object
  },
  components: { selectRefetchEventConfiguration },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {
    if (this.eventHandler.wEventParams == undefined) {
      this.$set(this.eventHandler, 'wEventParams', {});
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
