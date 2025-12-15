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
    /* 
    WorkView文件下collectIfUseCustomDynamicButton方法 没增加判断 currentAction.eventHandler前使用
    如已增加判断可去除
    */
    // this.workView.collectIfUseCustomDynamicButton = this.collectIfUseCustomDynamicButton
  }
  collectIfUseCustomDynamicButton() {
    const currentAction = this.currentAction;
    if (currentAction == null || currentAction.eventHandler) {
      return;
    }
    let workData = this.workFlow.getWorkData();
    let btnId = currentAction.id;
    let btn_submit = this.workFlow.actionCode.submit;
    // 处理自定义动态按钮
    let customDynamicButton = {};
    if (btnId !== btn_submit) {
      customDynamicButton.id = btnId;
      customDynamicButton.code = currentAction.code;
      customDynamicButton.newCode = btnId;
    } else {
      customDynamicButton = null;
    }
    workData.submitButtonId = btnId;
    workData.customDynamicButton = customDynamicButton;
  }
  // 自定义按钮-事件处理-配置js模块后点击自定义按钮会调用此方法
  // 流程设计示例 uuid=351398306758262784
  actionPerformed(action, event) {
    this.workView.submit()
  }
  /**
   * 表单组件加载
   */
  dyformMounted($dyformWidget) {
    console.log('dyformMounted');
  }
}
export default TestWidgetWorkViewDevelopment;
