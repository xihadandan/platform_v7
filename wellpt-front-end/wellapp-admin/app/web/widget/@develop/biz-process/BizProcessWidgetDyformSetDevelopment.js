import WidgetDyformSetDevelopment from '@develop/WidgetDyformSetDevelopment';

class BizProcessWidgetDyformSetDevelopment extends WidgetDyformSetDevelopment {
  get META() {
    return {
      name: '业务流程办件——表单设置',
      hook: {
        save: '保存',
        printForm: '打印表单'
      }
    };
  }

  /**
   *
   */
  dyformMounted() {
    const _this = this;
    _this.$processWidget = _this.$widget.parent();
    _this.$processWidget.setDyformWidget(_this.wDyform);
    _this.$processWidget.onDyformMounted(_this.wDyform);
  }

  /**
   * 保存
   *
   * @param {*} $evt
   */
  save($evt) {
    this.$processWidget.save($evt);
  }

  /**
   * 打印表单
   */
  printForm($evt) {
    this.$processWidget.printForm($evt);
  }
}

export default BizProcessWidgetDyformSetDevelopment;
