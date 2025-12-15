import FlowChartDevelopmentBase from './FlowChartDevelopmentBase.js';

class FlowOverdueCountChartDevelopment extends FlowChartDevelopmentBase {

  get META() {
    return {
      name: '流程统计分析_流程逾期数量排名图表二开',
      hook: {
        itemClick: '查看详情',
      }
    }
  }

  created() {
    this.maxBarCount = 12;
    this.getSetting().then(setting => {
      this.maxBarCount = setting.flowOverdue && setting.flowOverdue.maxOverdueBarCount || 12;
    });
  }

  mounted() {
    this.$widget.$el.style.cssText += `padding-top:20px;background-color:var(--w-color-white)`;
    this.getPageContext().handleEvent('searchFlowOverdue', searchForm => {
      let params = {};
      for (let key in searchForm) {
        if (searchForm[key] != null) {
          params[key] = searchForm[key];
        }
      }
      this.$widget.params = params;
      this.$widget.initChart();
    });
  }

  itemClick($event) {
    const _this = this;
    let flowDefId = $event.data.id;
    let title = `逾期流程明细 - ${$event.name}`;
    _this.getPageContext().emitEvent('ZmnCjLRWGMyiQldzpEvArAZpoHcnVPsZ:showDrawer', {
      eventParams: {
        ..._this.$widget.params,
        flowDefId,
        title
      }
    });
  }

  beforeChartRender(options) {
    const _this = this;
    options.grid[0].containLabel = true
    // 横坐标设置
    if (options.xAxis[0]) {
      options.xAxis[0].max = _this.maxBarCount - 1;
      options.xAxis[0].axisLabel = Object.assign(options.xAxis[0].axisLabel || {}, {
        interval: 0,// 显示所有标签
        width: 100,
        overflow: 'break'
      });
    }

    // 纵坐标设置
    let maxCount = 0;
    options.dataset[0].source && options.dataset[0].source.forEach(item => {
      maxCount = Math.max(maxCount, parseInt(item.count));
    });
    let yAxisMax = _this.getYAxisMax(maxCount);
    options.yAxis[0].position = 'left';
    options.yAxis[0].min = 0;
    options.yAxis[0].interval = yAxisMax / 5;
    options.yAxis[0].max = yAxisMax;
    options.yAxis.push({
      gridIndex: 0,
      name: '累计占比',
      type: 'value',
      min: 0,
      max: 100,
      position: 'right',
    });
    options.series[1].yAxisIndex = 1;

    // tooltip设置
    options.tooltip = Object.assign(options.tooltip || {}, {
      trigger: 'axis',
      borderColor: "#333",
      backgroundColor: "rgba(50,50,50,0.7)",
      textStyle: {
        color: '#fff'
      },
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      },
      formatter: (params, ticket) => {
        let label = params[0].axisValueLabel + "<br/>";
        params.forEach(param => {
          if (param.data.hasOwnProperty('percent')) {
            label += "<span style='padding-left:12px' /><span style='display:inline-block;width:80px;'>逾期率</span>" + (param.data.percent + '%') + "<br/>";
          }
          label += param.marker + `<span style='display:inline-block;width:80px;'>${param.seriesName}</span>` + (param.data.count || (param.data.totalPercent + '%')) + "<br/>";
        });
        return label;
      }
    });

    // toolbox设置
    options.toolbox = {
      right: 20,
      feature: {
        myTool1: {
          show: true,
          title: '更多',
          icon: 'path://M-6.2277 -1.9018L-3.5491 -1.9018L-3.5491 4.8214L-6.2277 4.8214L-6.2277 -1.9018ZM-1.3527 -4.5536L1.3259 -4.5536L1.3259 4.8214L-1.3527 4.8214L-1.3527 -4.5536ZM3.5491 -7.5L6.2277 -7.5L6.2277 4.8214L3.5491 4.8214L3.5491 -7.5ZM-7.192 7.5L7.192 7.5',
          onclick: function () {
            _this.getPageContext().emitEvent('DCGiZuBLCkscxkawgGPBleLXcFxoSbye:showDrawer', {
              eventParams: {
                ..._this.$widget.params,
                title: _this.$widget.configuration.titleConfig.text
              }
            });
          }
        },
        saveAsImage: {},
        myTool3: {
          show: true,
          title: '刷新',
          icon: 'M-6.9994 0.9083M4.5416 -2.9654L7.1597 -2.9654L7.1597 -5.6904M7.0261 -2.6448C5.9041 -5.6102 2.8051 -7.8543 -0.8549 -7.4536C-4.6485 -7.0261 -7.5872 -3.6867 -7.4536 0.1603M-4.5416 2.9654L-7.1864 2.9654L-7.1864 5.6904M-7.0261 2.6448C-5.9041 5.6102 -2.8051 7.8543 0.8549 7.4536C4.6485 7.0261 7.5872 3.6867 7.4536 -0.1603',
          onclick: function () {
            _this.$widget.initChart();
          }
        },
      }
    }
  }

  getYAxisMax(count) {
    let length = String(count).length;
    let max = Math.pow(10, length - 1) * 10;
    if (max > 10) {
      if (max * 0.2 > count) {
        return max * 0.2;
      }
      if (max * 0.4 > count) {
        return max * 0.4;
      }
      if (max * 0.6 > count) {
        return max * 0.6;
      }
      if (max * 0.8 > count) {
        return max * 0.8;
      }
    }
    return max;
  }

}
export default FlowOverdueCountChartDevelopment;
