<template>
  <div>
    <ColorSelectConfiguration
      label="背景颜色"
      v-model="value"
      typeField="defaultBgColor"
      colorField="bgColor"
      radioSize="small"
    ></ColorSelectConfiguration>
    <ColorSelectConfiguration
      label="线段颜色"
      v-model="value"
      typeField="defaultColor"
      colorField="strokeColor"
      radioSize="small"
    ></ColorSelectConfiguration>
  </div>
</template>

<script>
import ColorSelectConfiguration from '@pageAssembly/app/web/widget/commons/color-select-configuration.vue';
export default {
  name: 'WidgetNumericalBarProgressBarColorConfiguration',
  props: {
    value: Object
  },
  components: { ColorSelectConfiguration },
  provide() {},
  data() {
    return {};
  },
  methods: {}
};
</script>
