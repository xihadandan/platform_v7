<template>
  <dyform-field-base class="w-textarea" :dyformField="dyformField">
    <template slot="editable-input">
      <uni-easyinput
        class="textarea-editor"
        type="textarea"
        v-model="formData[fieldDefinition.name]"
        placeholder="请输入"
      />
    </template>
    <template slot="displayAsLabel">
      <uni-forms-item :label="fieldLabel" :required="isRequired">
        <view class="textarea-label">
          <rich-text :nodes="getDisplayValue"></rich-text>
        </view>
      </uni-forms-item>
    </template>
    <template slot="disabled">
      <uni-forms-item :label="fieldLabel" :required="isRequired">
        <view class="textarea-label">
          <rich-text :nodes="getDisplayValue"></rich-text>
        </view>
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
    getDisplayValue: function () {
      let text = this.formData[this.fieldDefinition.name];
      /* eslint-disable */
      if (typeof text == "string") {
        text = text.replace(new RegExp("\n", "gm"), "<br/>");
      }
      /* eslint-enable */
      return text == null ? "" : text;
    },
    isRequired: function () {
      return this.dyformField.isRequired();
    },
  },
};
</script>

<style lang="scss" scoped>
.w-textarea {
  .textarea-editor {
    margin-top: 2px;
  }
  .textarea-label {
    margin-top: 8px;
    margin-left: 30px;
  }
}
</style>
