import WidgetDyformDevelopment from '@develop/WidgetDyformDevelopment';

class TestWidgetDyformDevelopment extends WidgetDyformDevelopment {

  beforeMount() {
    // this.getPageContext().handleEvent(`widget:mounted:UAqyFCUKiulEKozaCjJUOmcHUacesImA`, function (e) {
    //   let widget = e.widget;
    //   console.log('当挂载成功从表组件: ', widget.configuration.name,
    //     '获取到当前行从表的数据: ', e.$.formData,
    //     '获取主表数据', e.$.dyform.formData);


    // })
  }
  afterSubformDataChanged() {
    console.log('测试从表表单二开 2 的 afterSubformDataChanged', arguments)
  }

  get META() {
    return {
      name: '测试表单二开2',
      hook: {

      }
    }
  }
}
export default TestWidgetDyformDevelopment;
