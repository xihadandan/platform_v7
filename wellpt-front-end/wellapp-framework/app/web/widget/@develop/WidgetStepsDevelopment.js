import VueWidgetDevelopment from './VueWidgetDevelopment';

class WidgetStepsDevelopment extends VueWidgetDevelopment {

  // 下一步
  stepNext() {
    this.$widget.stepNext();
  }

  // 上一步
  stepPrev() {
    this.$widget.stepPrev();
  }

  get ROOT_CLASS() {
    return 'WidgetStepsDevelopment'
  }
}


export default WidgetStepsDevelopment;
