<template>
  <dyform-field-base class="w-number-input" :dyformField="dyformField">
    <template slot="editable-input">
      <uni-easyinput
        v-model="formData[fieldDefinition.name]"
        type="number"
        :maxlength="maxlength"
        placeholder="请输入"
      />
    </template>
  </dyform-field-base>
</template>

<script>
import dyformFieldBase from "./field-base.vue";
export default {
  components: { dyformFieldBase },
  props: {
    dyformField: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      items: [],
      fieldDefinition: this.dyformField.getDefinition(),
      formData: this.dyformField.getFormData(),
    };
  },
  created() {},
  methods: {},
  computed: {
    isRequired: function () {
      return this.dyformField.isRequired();
    },
    maxlength: function () {
      var _self = this;
      var fieldDefinition = _self.fieldDefinition;
      var length = fieldDefinition.length || "7";
      if (
        (fieldDefinition.dbDataType == "13" ||
          fieldDefinition.dbDataType == "131" ||
          fieldDefinition.dbDataType == "132") &&
        fieldDefinition.length > 9
      ) {
        length = "9";
      } else if (fieldDefinition.dbDataType == "14" && fieldDefinition.length > 16) {
        length = "16";
      } else if (fieldDefinition.dbDataType == "15" && fieldDefinition.length > 12) {
        length = "12";
      } else if (fieldDefinition.dbDataType == "12" && fieldDefinition.length > 18) {
        length = "18";
      }
      return length;
    },
  },
};
</script>

<style scoped>
.w-number-input {
}
</style>
