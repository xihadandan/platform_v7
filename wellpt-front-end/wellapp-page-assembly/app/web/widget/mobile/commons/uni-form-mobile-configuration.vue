<template>
  <a-form-model :model="widget.configuration" :label-col="{ span: 6 }" :wrapper-col="{ span: 15 }">
    <a-form-model-item label="标签名称">
      <a-input v-model="widget.configuration.label" />
    </a-form-model-item>
    <a-form-model-item v-if="supportsDisplayMode" label="显示模式" >
      <a-radio-group v-model="widget.configuration.displayMode" :options="displayModeOptions" />
    </a-form-model-item>
  </a-form-model>
</template>
<style>
</style>
<script type="text/babel">
export default {
  name: 'WidgetUniFormMobileConfiguration',
  props: {
    widget: Object,
    designer: Object,
    supportsDisplayMode: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      displayModeOptions: [
        {label: '单行', value: 'default'},
        {label: '双行', value: 'multiple'}
      ]
    };
  },
  created() {
    if(this.widget.configuration && !this.widget.configuration.hasOwnProperty('label')) {
      this.$set(this.widget.configuration, "label", this.widget.configuration.name || "");
    }
    if(this.supportsDisplayMode && this.widget.configuration && !this.widget.configuration.hasOwnProperty('displayMode')) {
      this.$set(this.widget.configuration, "displayMode", "default");
    }
  }
};
</script>
