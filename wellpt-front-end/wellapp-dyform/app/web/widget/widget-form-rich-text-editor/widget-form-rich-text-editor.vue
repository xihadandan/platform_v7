<template>
  <a-form-model-item
    v-show="!hidden"
    :prop="formModelItemProp"
    :rules="rules"
    :ref="widget.configuration.code"
    :label="itemLabel"
    :colon="displayAsLabel"
    :class="widgetClass"
  >
    <QuillEditor
      v-model="formData[widget.configuration.code]"
      ref="quillEditor"
      :editable="editable"
      :readOnly="readonly"
      :disable="disable"
      :displayAsLabel="displayAsLabel"
      :htmlCodec="widget.configuration.htmlCodec"
      :placeholder="$t('placeholder', widget.configuration.placeholder)"
      @change="onChange"
    />
  </a-form-model-item>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formMixin from '../mixin/form-common.mixin';
import QuillEditor from '@pageAssembly/app/web/lib/quill-editor';

export default {
  extends: FormElement,
  name: 'WidgetFormRichTextEditor',
  mixins: [widgetMixin, formMixin],

  data() {
    return {};
  },
  beforeCreate() {},
  components: { QuillEditor },
  computed: {},
  created() {},
  methods: {
    setValue(v) {
      if (this.$refs.quillEditor) {
        this.$refs.quillEditor.setHtml(v || '');
      }
      this.formData[this.fieldCode] = v || '';
    },
    displayValue() {
      return this.$refs.quillEditor.displayValue();
    },
    onChange(value) {
      this.emitChange();
    },
    getValue() {
      if (this.form.formData[this.fieldCode] == '<p><br></p>') {
        return '';
      }
      return this.form.formData[this.fieldCode];
    }
  },
  mounted() {}
};
</script>
