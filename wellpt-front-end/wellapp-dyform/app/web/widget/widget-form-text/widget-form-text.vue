<template>
  <div v-html="text" :id="widget.id" :class="['no-form-item-widget', widgetClass]"></div>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formMixin from '../mixin/form-common.mixin';
export default {
  extends: FormElement,
  name: 'WidgetFormText',
  mixins: [widgetMixin, formMixin],
  data() {
    return { text: this.$t(this.widget.id, this.widget.configuration.content) };
  },
  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    if (!this.designMode) {
      if (this.dyform.$textset == undefined) {
        this.dyform.$textset = {};
      }
      this.dyform.$textset[this.widget.id] = this;
    }
  },
  methods: {
    setFormulaCalculateFieldValue() {
      this.tryCalculateFormulaApiDataValue();
      let v = this.getFormulaCalculateValue();
      this.setValue(v);
    },
    setValue(text) {
      this.text = text;
    },

    getValue() {
      return this.text;
    }
  },
  mounted() {}
};
</script>
