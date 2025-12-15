<script>
/* 环节属性-表单设置-表单字段-附件按钮设置 */
import FormSettingsSubformButton from './form-settings-subform-button.vue';

export default {
  name: 'FormSettingsFileButton',
  extends: FormSettingsSubformButton,
  methods: {
    // 获取按钮选项
    getButtonOptions() {
      this.configuration.headerButton.forEach(item => {
        if (item.btnShowType === 'edit') {
          this.buttonOptions.edit.headerButton.push(item);
        } else {
          this.buttonOptions.show.headerButton.push(item);
        }
      });
      this.configuration.rowButton.forEach(item => {
        if (item.btnShowType === 'edit') {
          this.buttonOptions.edit.rowButton.push(item);
        } else {
          this.buttonOptions.show.rowButton.push(item);
        }
      });
    },
    // 获取流程上按钮数据
    getFlowButton() {
      let exist = false;
      let checkedValue = this.initCheckedValue();
      this.formBtnRightSettings.forEach((item, index) => {
        if (item.value) {
          const value = item.value.split('=');
          const prefix = value[0];
          const buttons = value[1];
          if (prefix === this.getFlowValuePrefix()) {
            checkedValue = JSON.parse(buttons);
            exist = true;
            this.flowButtonIndex = index;
          }
        }
      });
      return { exist, checkedValue };
    },
    // 获取表单上按钮数据
    getFormButton() {
      const buttonOptions = this.buttonOptions;
      let exist = false;
      let checkedValue = this.initCheckedValue();
      for (const key in buttonOptions) {
        for (const k in buttonOptions[key]) {
          for (let index = 0; index < buttonOptions[key][k].length; index++) {
            const item = buttonOptions[key][k][index];
            if (item.defaultFlag) {
              checkedValue[key][k].push(item.id);
              exist = true;
            }
          }
        }
      }
      return { exist, checkedValue };
    },
    // 把表单数据添加到流程中
    setFlowButton(checkedValue) {
      let buttonIndex = this.flowButtonIndex;
      if (buttonIndex !== -1) {
        this.formBtnRightSettings.splice(buttonIndex, 1);
      }
      this.flowButtonIndex = this.formBtnRightSettings.length;
      checkedValue = this.checkedValueArr2Str(checkedValue);
      let fieldItem = this.fieldItem();

      fieldItem.value = `${this.getFlowValuePrefix()}=${JSON.stringify(checkedValue)}`;
      this.formBtnRightSettings.push(fieldItem);
    },
    // 获取流程数据前缀
    getFlowValuePrefix() {
      let valuePrefix = '';
      if (this.field.subformField) {
        // 从表字段
        valuePrefix = `${this.field.subformUuid}_${this.field.code}`;
      } else {
        valuePrefix = this.field.code;
      }
      return valuePrefix;
    }
  }
};
</script>
