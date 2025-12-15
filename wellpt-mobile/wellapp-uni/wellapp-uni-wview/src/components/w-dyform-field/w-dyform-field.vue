<template>
  <view class="dyform-field">
    <!-- 单行文本 -->
    <dyform-field-text-input
      v-if="fieldDefinition.inputMode == '1' && dyformField"
      :dyformField="dyformField"
    ></dyform-field-text-input>
    <!-- 富文本 -->
    <dyform-field-rich-editor
      v-else-if="fieldDefinition.inputMode == '2' && dyformField"
      :dyformField="dyformField"
    ></dyform-field-rich-editor>
    <!-- 单选框 -->
    <dyform-field-radio
      v-else-if="fieldDefinition.inputMode == '17' && dyformField"
      :dyformField="dyformField"
    ></dyform-field-radio>
    <!-- 复选框 -->
    <dyform-field-checkbox
      v-else-if="fieldDefinition.inputMode == '18' && dyformField"
      :dyformField="dyformField"
    ></dyform-field-checkbox>
    <!-- 下拉框 -->
    <dyform-field-select
      v-else-if="(fieldDefinition.inputMode == '19' || fieldDefinition.inputMode == '199') && dyformField"
      :dyformField="dyformField"
    ></dyform-field-select>
    <!-- 多行文本框 -->
    <dyform-field-textarea
      v-else-if="fieldDefinition.inputMode == '20' && dyformField"
      :dyformField="dyformField"
    ></dyform-field-textarea>
    <!-- 弹出搜索框 -->
    <dyform-field-dialog
      v-else-if="fieldDefinition.inputMode == '26' && dyformField"
      :dyformField="dyformField"
    ></dyform-field-dialog>
    <!-- 流水号 -->
    <dyform-field-serial-number
      v-else-if="(fieldDefinition.inputMode == '7' || fieldDefinition.inputMode == '29') && dyformField"
      :dyformField="dyformField"
    ></dyform-field-serial-number>
    <!-- 日期控件 -->
    <dyform-field-date-picker
      v-else-if="fieldDefinition.inputMode == '30' && dyformField != null"
      :dyformField="dyformField"
    ></dyform-field-date-picker>
    <!-- 数字输入框 -->
    <dyform-field-number-input
      v-else-if="fieldDefinition.inputMode == '31' && dyformField != null"
      :dyformField="dyformField"
    ></dyform-field-number-input>
    <!-- 附件 -->
    <dyform-field-file-upload
      v-else-if="
        (fieldDefinition.inputMode == '4' || fieldDefinition.inputMode == '6' || fieldDefinition.inputMode == '33') &&
        dyformField != null
      "
      :dyformField="dyformField"
    ></dyform-field-file-upload>
    <!-- 嵌入页面 -->
    <dyform-field-embedded
      v-else-if="fieldDefinition.inputMode == '40' && dyformField != null"
      :dyformField="dyformField"
    ></dyform-field-embedded>
    <!-- 职位 -->
    <dyform-field-job-select
      v-else-if="fieldDefinition.inputMode == '41' && dyformField != null"
      :dyformField="dyformField"
    ></dyform-field-job-select>
    <!-- 组织选择框 -->
    <dyform-field-unit
      v-else-if="fieldDefinition.inputMode == '43' && dyformField != null"
      :dyformField="dyformField"
    ></dyform-field-unit>
    <!-- 分组下拉框 -->
    <dyform-field-group-select
      v-else-if="fieldDefinition.inputMode == '191' && dyformField != null"
      :dyformField="dyformField"
    ></dyform-field-group-select>
    <!-- 通用字段 -->
    <dyform-field-base v-else-if="dyformField != null" :dyformField="dyformField"></dyform-field-base>
  </view>
</template>

<script>
// const _ = require('lodash');
import DyformField from "./uni-DyformField.js";
import dyformFieldBase from "./field-base.vue";
import dyformFieldTextInput from "./field-textinput.vue";
import dyformFieldRichEditor from "./field-rich-editor.vue";
import dyformFieldSelect from "./field-select.vue";
import dyformFieldTextarea from "./field-textarea.vue";
import dyformFieldDialog from "./field-dialog.vue";
import dyformFieldSerialNumber from "./field-serial-number.vue";
import dyformFieldRadio from "./field-radio.vue";
import dyformFieldCheckbox from "./field-checkbox.vue";
import dyformFieldDatePicker from "./field-date-picker.vue";
import dyformFieldNumberInput from "./field-number-input.vue";
import dyformFieldFileUpload from "./field-file-upload.vue";
import dyformFieldEmbedded from "./field-embedded.vue";
import dyformFieldJobSelect from "./field-job-select.vue";
import dyformFieldUnit from "./field-unit.vue";
import dyformFieldGroupSelect from "./field-group-select.vue";
export default {
  components: {
    dyformFieldBase,
    dyformFieldTextInput,
    dyformFieldRichEditor,
    dyformFieldSelect,
    dyformFieldTextarea,
    dyformFieldDialog,
    dyformFieldSerialNumber,
    dyformFieldRadio,
    dyformFieldCheckbox,
    dyformFieldDatePicker,
    dyformFieldNumberInput,
    dyformFieldFileUpload,
    dyformFieldEmbedded,
    dyformFieldJobSelect,
    dyformFieldUnit,
    dyformFieldGroupSelect,
  },
  props: {
    fieldDefinition: {
      type: Object,
      required: true,
    },
    formScope: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      dyformField: null,
    };
  },
  created() {
    var _self = this;
    _self.formData = _self.formScope.getFormData();
    // console.log(JSON.stringify(_self.fieldDefinition))
    // 添加表单字段
    var dyformField = new DyformField({
      formScope: _self.formScope,
      definition: _self.fieldDefinition,
      formData: _self.formData,
      value: _self.formData[_self.fieldDefinition.name],
    });
    _self.dyformField = dyformField;
    if (!_self.formScope.isSubform()) {
      _self.formScope.getDyform().addField(dyformField);
    } else {
      var subform = _self.formScope.getSubform();
      if (subform != null) {
        subform.addField(dyformField);
      }
    }
  },
  methods: {},
  computed: {},
};
</script>

<style scoped>
.dyform-field {
}
</style>
