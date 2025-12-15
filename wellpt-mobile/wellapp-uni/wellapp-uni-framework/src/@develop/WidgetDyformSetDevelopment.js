import WidgetDyformDevelopment from './WidgetDyformDevelopment';

class WidgetDyformSetDevelopment extends WidgetDyformDevelopment {
  /**
   * 获取表单实例
   */
  get wDyform() {
    if (this.$widget) {
      return this.$widget.$refs.wDyform;
    }
    return undefined;
  }

  get ROOT_CLASS() {
    return 'WidgetDyformSetDevelopment';
  }
}

export default WidgetDyformSetDevelopment;
