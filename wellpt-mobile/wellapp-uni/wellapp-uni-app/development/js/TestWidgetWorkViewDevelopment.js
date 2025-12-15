import WidgetWorkViewDevelopment from '@develop/WidgetWorkViewDevelopment';
class TestWidgetWorkViewDevelopment extends WidgetWorkViewDevelopment {
  get META() {
    return {
      name: '测试流程二开',
      scope: 'flow' // 设计器中可配置的流程、环节二开。flow流程二开，task// 环节二开，为空时为流程二开
    };
  }

  created() {
    console.log('created', this.workView);
  }

  /**
   * 表单组件加载
   */
  dyformMounted($dyformWidget) {
    console.log('dyformMounted');
  }
}
export default TestWidgetWorkViewDevelopment;
