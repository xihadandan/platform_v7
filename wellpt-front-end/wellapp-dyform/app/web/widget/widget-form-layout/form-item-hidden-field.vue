<template>
  <a-form-model-item
    :prop="widget.configuration.code"
    :rules="createWidgetValidateRules(widget)"
    v-show="false"
    :label="widget.configuration.name"
  ></a-form-model-item>
</template>
<style lang="less"></style>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';

export default {
  name: 'FormItemHiddenField',
  extends: FormElement,
  mixins: [widgetMixin],
  props: {},
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {
    this.isFormModelItemHiddenField = true;
  },
  beforeMount() {},
  mounted() {},
  methods: {
    afterDisplayStateChanged(oldState, currentState) {
      this.$emit('fieldStateChange', { oldState, currentState });
    }
  }
};
</script>
