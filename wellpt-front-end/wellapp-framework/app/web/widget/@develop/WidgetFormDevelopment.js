import VueWidgetDevelopment from './VueWidgetDevelopment';

class WidgetFormDevelopment extends VueWidgetDevelopment {

  /**
   * 设置表单数据
   * @param {表单数据} formData
   */
  setFormData(formData) {
    for (let k in formData) {
      this.$widget.$set(this.$widget.form, k, formData[k]);
    }
  }

  /**
   * 设置字段
   * @param {字段编码} field
   * @param {字段值} value
   */
  setField(field, value) {
    this.$widget.$set(this.$widget.form, field, value);
  }

  setFieldReadOnly(field, readOnly) {
    this.$widget.setFieldReadOnly(field, readOnly);
  }

  /**
   * 清除表单校验信息
   */
  clearValidate() {
    this.$widget.$refs.form.clearValidate();
  }

  validateForm() {
    return new Promise((resolve, reject) => {
      this.$widget.validateFormData(function (vali, msg) {
        resolve({
          success: vali,
          msg
        })
      })
    })
  }

  /**
   * 重置表单数据
   */
  resetForm() {
    this.$widget.$refs.form.resetFields();
  }

  get ROOT_CLASS() {
    return 'WidgetFormDevelopment'
  }
}

export default WidgetFormDevelopment;
