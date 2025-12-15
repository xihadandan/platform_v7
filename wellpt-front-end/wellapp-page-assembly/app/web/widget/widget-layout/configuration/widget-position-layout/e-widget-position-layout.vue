<template>
  <EditWrapper :widget="widget" :index="index" :widgetsOfParent="widgetsOfParent" :designer="designer" :parent="parent">
    <DragResizePanel :widget="widget" :designer="designer" />
  </EditWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import DragResizePanel from './drag-resize-panel.vue';

export default {
  name: 'EWidgetPositionLayout',
  mixins: [editWgtMixin],
  props: {},
  components: { DragResizePanel },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {}
};
</script>
