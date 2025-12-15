<template>
  <uni-forms-item
    v-if="!hidden"
    :label="itemLabel"
    :name="formModelItemProp"
    :label-position="widget.configuration.labelPosition"
    :class="widgetClass"
    :style="itemStyle"
    :ref="fieldCode"
  >
    <uni-w-editor
      v-model="formData[fieldCode]"
      ref="quillEditor"
      :editable="editable"
      :readOnly="readonly"
      :disable="disable"
      :displayAsLabel="displayAsLabel"
      :htmlCodec="widget.configuration.htmlCodec"
      :placeholder="$t('placeholder', widget.configuration.placeholder)"
      @change="onChange"
    >
    </uni-w-editor>
  </uni-forms-item>
</template>
<style lang="sass"></style>
<script type="text/babel">
import formElement from "../w-dyform/form-element.mixin";
import formCommonMixin from "../w-dyform/form-common.mixin";
export default {
  name: "WFormRichTextEditor",
  mixins: [formElement, formCommonMixin],
  data() {
    return {};
  },
  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    setValue(v) {
      if (this.$refs.quillEditor) {
        this.$refs.quillEditor.setHtml(v || "");
      }
      this.formData[this.fieldCode] = v || "";
    },
    displayValue() {
      return this.$refs.quillEditor.displayValue();
    },
    onChange(value) {
      this.emitChange();
    },
  },
  mounted() {},
};
</script>
