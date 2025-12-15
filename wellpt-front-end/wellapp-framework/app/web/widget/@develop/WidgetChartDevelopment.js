import VueWidgetDevelopment from './VueWidgetDevelopment';


class WidgetChartDevelopment extends VueWidgetDevelopment {


  beforeChartRender(options) {

  }



  afterChartFinished() {

  }


  updateOption(options) {
    this.$widget.chart.setOption(options);
  }

  /**
   * 派发图表事件
   * @param {*} action
   */
  dispatchChartAction(action) {
    this.$widget.chart.dispatchAction(action);
  }


  get ROOT_CLASS() {
    return 'WidgetChartDevelopment'
  }
}

export default WidgetChartDevelopment;
