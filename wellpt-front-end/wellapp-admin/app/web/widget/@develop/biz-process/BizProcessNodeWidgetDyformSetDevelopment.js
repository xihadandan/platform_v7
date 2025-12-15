import WidgetDyformSetDevelopment from '@develop/WidgetDyformSetDevelopment';

class BizProcessNodeWidgetDyformSetDevelopment extends WidgetDyformSetDevelopment {
  get META() {
    return {
      name: '业务阶段办件——表单设置',
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
    _this.$nodeWidget = _this.$widget.parent();
    _this.$nodeWidget.setDyformWidget(_this.wDyform);
    _this.$nodeWidget.onDyformMounted(_this.wDyform);
  }

  /**
   * 保存
   *
   * @param {*} $evt
   */
  save($evt) {
    this.$nodeWidget.save($evt);
  }

  /**
   * 打印表单
   */
  printForm($evt) {
    this.$nodeWidget.printForm($evt);
  }
}

export default BizProcessNodeWidgetDyformSetDevelopment;
