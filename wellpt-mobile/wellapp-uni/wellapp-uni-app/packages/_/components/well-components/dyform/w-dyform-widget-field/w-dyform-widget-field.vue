<template>
  <!-- 表单字段 -->
  <w-dyform-field v-if="fieldDefinition" :fieldDefinition="fieldDefinition" :formScope="formScope"></w-dyform-field>
  <!-- 从表 -->
  <w-subform v-else-if="subformDefinition" :subformDefinition="subformDefinition" :formScope="formScope"></w-subform>
</template>
<script>
export default {
  name: "wDyformWidgetField",
  props: {
    widget: {
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
      fieldDefinition: null,
      subformDefinition: null,
    };
  },
  created: function () {
    // 从表字段
    if (this.widget.wtype == "WidgetUniSubform") {
      // 合并配置
      var suformDefinition = this.mergeSubformConfiguration();
      this.subformDefinition = suformDefinition;
    } else {
      // 合并配置
      var fieldDefinition = this.mergeFieldConfiguration();
      // 转换控件类型
      this.convertInputMode(fieldDefinition);
      this.fieldDefinition = fieldDefinition;
    }
  },
  methods: {
    // 合并从表配置
    mergeSubformConfiguration() {
      var fieldDefinition = Object.assign({}, this.widget, this.widget.configuration);
      fieldDefinition.isShow = true;
      return fieldDefinition;
    },
    // 合并字段配置
    mergeFieldConfiguration() {
      var fieldDefinition = Object.assign({}, this.widget, this.widget.configuration);
      // 显示文本
      fieldDefinition.displayName = fieldDefinition.configuration.label;
      // 显示模式1、可编辑
      fieldDefinition.showType = "1";
      // 字段编码
      fieldDefinition.name = this.widget.configuration.code;
      return fieldDefinition;
    },
    convertInputMode: function (fieldDefinition) {
      let inputMode = fieldDefinition.inputMode;
      let wtype = fieldDefinition.wtype;
      let type = fieldDefinition.configuration.type;
      switch (wtype) {
        // 单选框
        case "WidgetFormRadio":
          inputMode = "17";
          break;
        // 复选框
        case "WidgetFormCheckbox":
          inputMode = "18";
          break;
        // 日期
        case "WidgetUniFormDatePicker":
          inputMode = "30";
          fieldDefinition.contentFormat = "1";
          break;
        // 文件上传4、6、33
        case "WidgetUniFormFileUpload":
          inputMode = "4";
          break;
        // 富文本
        case "WidgetUniFormRichTextEditor":
          inputMode = "2";
          break;
      }
      if (type && inputMode == "-1") {
        switch (type) {
          // 输入框
          case "input":
            inputMode = "1";
            break;
          // 密码框
          case "input-password":
            inputMode = "1";
            fieldDefinition.isPasswdInput = "true";
            break;
          // 数据输入框
          case "input-number":
            inputMode = "31";
            break;
          // 文本域
          case "textarea":
            inputMode = "20";
            break;
          // 下拉框
          case "select":
            inputMode = "19";
            break;
        }
      }
      fieldDefinition.inputMode = inputMode;
    },
  },
};
</script>

<style scoped></style>
