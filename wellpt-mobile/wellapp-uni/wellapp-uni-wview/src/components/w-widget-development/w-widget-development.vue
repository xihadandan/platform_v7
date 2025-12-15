<template>
  <view :class="widget.wtype + '-widget-development'">
    <!-- #ifdef H5 || APP-PLUS -->
    <component :is="componentType" :widget="componentDefinition" :parent="parent" />
    <!-- #endif -->
    <!-- #ifdef MP -->
    <!-- #endif -->
  </view>
</template>

<script>
export default {
  props: {
    widget: {
      type: Object,
      required: true,
    },
    parent: Object,
  },
  data() {
    let componentType = this.widget.configuration.uniJsModule;
    delete this.widget.configuration.uniJsModule;
    return {
      componentType: componentType,
      componentDefinition: this.widget,
    };
  },
};
</script>
