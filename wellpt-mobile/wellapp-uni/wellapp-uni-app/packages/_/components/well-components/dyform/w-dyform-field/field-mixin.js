"use strict";

module.exports = {
  props: {
    dyformField: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      formScope: this.dyformField.getFormScope(),
      fieldDefinition: this.dyformField.getDefinition(),
      formData: this.dyformField.getFormData(),
    };
  },
  computed: {
    fieldLabel() {
      return this.fieldDefinition.tagName || this.fieldDefinition.displayName;
    },
  },
};
