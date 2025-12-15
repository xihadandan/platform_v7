<template>
  <js-module-select
    :value="currentValue"
    :multiSelect="false"
    :labelInValue="labelInValue"
    dependencyFilter="WidgetWorkViewDevelopment"
    @change="onChange"
  />
</template>

<script>
import JsModuleSelect from '@pageAssembly/app/web/widget/commons/js-module-select.vue';
export default {
  name: 'CustomJsModule',
  props: {
    value: {
      type: String
    },
    labelInValue: {
      type: Boolean,
      default: false
    }
  },
  components: {
    JsModuleSelect
  },
  computed: {
    currentValue() {
      let value = undefined;
      if (this.value) {
        value = this.value;
      }
      return value;
    }
  },
  methods: {
    onChange(value, option) {
      if (this.labelInValue && value) {
        value = value.value;
      }
      if (value === undefined) {
        value = '';
      }
      this.$emit('input', value);
      this.$emit('change', value, option);
    }
  }
};
</script>
