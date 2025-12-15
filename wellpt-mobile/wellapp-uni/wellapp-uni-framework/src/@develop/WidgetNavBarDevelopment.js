import VueWidgetDevelopment from "./VueWidgetDevelopment";

class WidgetNavBarDevelopment extends VueWidgetDevelopment {
  get ROOT_CLASS() {
    return "WidgetNavBarDevelopment";
  }
  get pageContext() {
    return this.getPageContext();
  }
  // 设置下拉框值
  setSelectValue(value) {
    if (this.$widget.$refs.widgetSelect) {
      this.$widget.$refs.widgetSelect.setValue(value);
    }
  }
  getSelectValue() {
    this.$widget.$refs.widgetSelect.value;
  }
}

export default WidgetNavBarDevelopment;
