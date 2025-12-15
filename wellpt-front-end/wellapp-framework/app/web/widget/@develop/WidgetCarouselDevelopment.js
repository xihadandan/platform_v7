import VueWidgetDevelopment from './VueWidgetDevelopment';

class WidgetCarouselDevelopment extends VueWidgetDevelopment {

  // 切换到下一面板
  slickNext() {
    this.$widget.slickNext();
  }

  // 切换到上一面板
  slickPrev() {
    this.$widget.slickPrev();
  }

  // 切换到指定面板
  slickGoTo(slideNumber, dontAnimate) {
    this.$widget.slickGoTo(slideNumber, dontAnimate);
  }

  get ROOT_CLASS() {
    return 'WidgetCarouselDevelopment'
  }
}


export default WidgetCarouselDevelopment;
