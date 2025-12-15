<template>
  <dyform-field-base class="w-textinput" :dyformField="dyformField">
    <template slot="editable-input">
      <uni-easyinput
        v-model="formData[fieldDefinition.name]"
        :type="isPassword ? 'password' : 'text'"
        placeholder="请输入"
      />
    </template>
    <template slot="displayAsLabel">
      <uni-forms-item :label="fieldLabel" :required="isRequired">
        <label>{{ getDisplayValue }}</label>
      </uni-forms-item>
    </template>
    <template slot="disabled">
      <uni-forms-item :label="fieldLabel" :required="isRequired">
        <label>{{ getDisplayValue }}</label>
      </uni-forms-item>
    </template>
  </dyform-field-base>
</template>

<script>
import dyformFieldBase from "./field-base.vue";
import mixin from "./field-mixin";
export default {
  mixins: [mixin],
  components: { dyformFieldBase },
  data() {
    return {};
  },
  created() {},
  methods: {},
  computed: {
    isPassword: function () {
      return this.fieldDefinition.isPasswdInput == "true";
    },
    getDisplayValue: function () {
      return this.isPassword ? "******" : this.formData[this.fieldDefinition.name];
    },
    isRequired: function () {
      return this.dyformField.isRequired();
    },
  },
};
</script>

<style scoped>
.field {
}
.uni-list-cell {
  justify-content: flex-start;
}
.textarea-label {
  margin-left: 30px;
}
</style>
