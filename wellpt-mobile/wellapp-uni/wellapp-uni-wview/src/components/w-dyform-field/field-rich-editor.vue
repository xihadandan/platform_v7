<template>
  <dyform-field-base class="w-rich-editor" :dyformField="dyformField">
    <template slot="editable">
      <uni-forms-item style="height: 36px" :label="fieldLabel" :required="isRequired"></uni-forms-item>
      <view>
        <editor
          :id="'editor_' + fieldDefinition.name"
          class="editor-container"
          placeholder="请输入"
          @ready="onEditorReady"
          @input="onEditorInputChange"
        ></editor>
      </view>
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
  methods: {
    onEditorReady() {
      // #ifdef MP-BAIDU
      this.editorCtx = requireDynamicLib("editorLib").createEditorContext("editorId");
      // #endif

      // #ifdef APP-PLUS || H5 ||MP-WEIXIN
      uni
        .createSelectorQuery()
        .select("#editor_" + this.fieldDefinition.name)
        .context((res) => {
          this.editorCtx = res.context;
        })
        .exec();
      // #endif
      var html = this.formData[this.fieldDefinition.name];
      if (html != null) {
        this.editorCtx.setContents({ html: html });
      }
    },
    onEditorInputChange: function (event) {
      this.formData[this.fieldDefinition.name] = event.detail.html;
    },
  },
  computed: {
    getDisplayValue: function () {
      let text = this.formData[this.fieldDefinition.name];
      return text == null ? "" : text;
    },
    isRequired: function () {
      return this.dyformField.isRequired();
    },
  },
};
</script>

<style lang="scss" scoped>
.w-rich-editor {
  .uni-list-cell {
    justify-content: flex-start;
  }
  .textarea-label {
    margin-left: 30px;
  }
}
</style>
