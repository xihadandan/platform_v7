import VueWidgetDevelopment from "./VueWidgetDevelopment";

class WidgetDyformDevelopment extends VueWidgetDevelopment {
  /**
   * 显示或者隐藏字段
   * @param {字段编码} fieldName
   * @param {是否显示} visible
   */
  setFieldVisible(fieldName, visible = true) {
    this.dyform.setFieldVisible(fieldName, visible);
  }

  /**
   * 设置字段值
   * @param {字段编码} fieldName
   * @param {值} value
   * @param {是否触发设值变更} emitChange
   */
  setFieldValue(fieldName, value, emitChange = true) {
    this.dyform.setFieldValue(fieldName, value, emitChange);
  }

  /**
   * 获取字段值
   * @param {字段编码} fieldName
   */
  getFieldValue(fieldName) {
    return this.dyform.getFieldValue(fieldName);
  }

  /**
   * 根据字段编码获取字段组件实例
   * @param {字段编码} fieldName
   * @returns
   */
  getField(fieldName) {
    return this.dyform.getField(fieldName);
  }

  /**
   * 设置字段的必填性
   * @param {字段编码} fieldName
   * @param {是否必填} required
   */
  setFieldRequired(fieldName, required = true) {
    this.dyform.setFieldRequired(fieldName, required);
  }

  /**
   * 设置字段的只读性
   * @param {字段编码} fieldName
   * @param {是否只读} readonly
   */
  setFieldReadOnly(fieldName, readonly = true) {
    this.dyform.setFieldReadOnly(fieldName, readonly);
  }
  /**
   * 设置字段的可编辑性（不可编辑时会根据组件的不可编辑样式自适应）
   * @param {字段编码} fieldName
   * @param {是否可编辑} editable
   */
  setFieldEditable(fieldName, editable = true) {
    this.dyform.setFieldEditable(fieldName, editable);
  }

  /**
   * 设置字段的禁用
   * @param {字段编码} fieldName
   * @param {是否禁用} disable
   */
  setFieldDisable(fieldName, disable = true) {
    this.dyform.setFieldDisable(fieldName, disable);
  }

  /**
   * 设置字段为显示文本状态（该方法不会按不可编辑组件自适应）
   * @param {字段编码} fieldName
   */
  setFieldDisplayAsLabel(fieldName) {
    this.dyform.setFieldDisplayAsLabel(fieldName);
  }

  /**
   * 根据编码设置表单布局的可见性
   * @param {编码} code
   * @param {是否可见} visible
   */
  setFormLayoutVisible(code, visible = true) {
    this.dyform.setFormLayoutVisible(code, visible);
  }

  /**
   * 收集表单数据
   * @param {*} validate 是否校验
   * @param {*} callback 回调函数 ( 当validate = true 时候，校验结束回调函数 ，函数入参: 校验结果 , 校验信息 , 收集的表单数据)
   */
  collectFormData() {
    return this.wDyform.collectFormData.apply(this.wDyform, arguments);
  }

  /**
   * 校验函数
   * @param {校验回调函数} callback
   */
  validateFormData() {
    this.wDyform.validateFormData.apply(this.wDyform, arguments);
  }

  /**
   * 清除校验结果
   */
  clearValidate() {
    this.wDyform.clearValidate();
  }

  /**
   * 表单数据发生变更后触发
   */
  afterFormDataChanged() {}

  /**
   * 设置表单展示状态:'edit', 'readonly', 'disable', 'label'
   */
  setFormDisplayState(v) {
    this.wDyform.setFormDisplayState(v);
  }

  addNamedParams(params) {
    this.wDyform.addNamedParams(params);
  }

  /**
   * 清空参数对象
   */
  clearNamedParams() {
    this.wDyform.clearNamedParams();
  }

  /**
   * 设置从表列字段可见性
   * @param {*} formId  从表ID 或者 从表组件ID
   * @param {*} fieldName 字段编码
   * @param {*} visible
   */
  setSubformFieldVisible(formId, fieldName, visible = true) {
    this.dyform.setSubformFieldVisible(formId, fieldName, visible);
  }

  /**
   * 设置从表列字段可编辑性
   * @param {*} formId  从表ID 或者 从表组件ID
   * @param {*} fieldName 字段编码
   * @param {*} editable
   */
  setSubformFieldEditable(formId, fieldName, editable = true) {
    this.dyform.setSubformFieldEditable(formId, fieldName, editable);
  }

  /**
   * 设置从表列字段必填性
   * @param {*} formId  从表ID 或者 从表组件ID
   * @param {*} fieldName 字段编码
   * @param {*} required
   */
  setSubformFieldRequired(formId, fieldName, required = true) {
    this.dyform.setSubformFieldRequired(formId, fieldName, required);
  }

  /**
   * 根据条件函数删除从表数据
   * @param {*} formId
   * @param {*} filter Function (item){return item.xxx=='yyy' }
   */
  deleteSubformByFilter(formId, filter) {
    this.dyform.deleteSubformByFilter(formId, filter);
  }

  /**
   * 清理表单级缓存
   * @param {*} key
   * @returns
   */
  removeCache(key) {
    return this.dyform.removeCache(key);
  }

  /**
   * 获取表单实例
   */
  get dyform() {
    if (this.wDyform) {
      return this.wDyform.dyform;
    }
    return undefined;
  }

  get wDyform() {
    return this.$widget;
  }

  get ROOT_CLASS() {
    return "WidgetDyformDevelopment";
  }
}

export default WidgetDyformDevelopment;
