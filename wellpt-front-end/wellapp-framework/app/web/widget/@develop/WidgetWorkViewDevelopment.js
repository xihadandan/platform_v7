import VueWidgetDevelopment from './VueWidgetDevelopment';

class WidgetWorkViewDevelopment extends VueWidgetDevelopment {
  /**
   * @param {*} $widget
   */
  constructor($widget) {
    super($widget);
  }

  /**
   * 获取流程实例对象
   */
  get workView() {
    return this.$widget.workView;
  }

  /**
   * 获取流程数据
   */
  getWorkData() {
    return this.$widget.workView.getWorkData();
  }

  /**
   * 表单组件加载
   */
  dyformMounted($dyformWidget) {
    console.log('dyformMounted');
  }

  get workflowVersion() {
    return this.workView.version;
  }

  get ROOT_CLASS() {
    return 'WidgetWorkViewDevelopment';
  }
}

export default WidgetWorkViewDevelopment;
