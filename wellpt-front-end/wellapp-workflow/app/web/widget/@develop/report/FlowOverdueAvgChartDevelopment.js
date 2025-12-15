import FlowChartDevelopmentBase from './FlowChartDevelopmentBase.js';

class FlowOverdueAvgChartDevelopment extends FlowChartDevelopmentBase {

  get META() {
    return {
      name: '流程统计分析_流程平均逾期时长排名图表二开',
      hook: {
        itemClick: '查看详情',
      }
    }
  }

  created() {
    this.maxBarCount = 12;
    this.getSetting().then(setting => {
      this.maxBarCount = setting.flowOverdue && setting.flowOverdue.maxAvgOverdueBarCount || 12;
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
        let param = params[0];
        let avgOverdueDay = 0;
        let avgOverdueHour = 0;
        let avgOverdueMinute = 0;
        let avgOverdueTimeInMinute = parseFloat(param.data.avgOverdueTimeInMinute);
        if (avgOverdueTimeInMinute >= 60 * 24) {
          avgOverdueDay = parseInt(avgOverdueTimeInMinute / (60 * 24));
        }
        if (avgOverdueTimeInMinute >= 60) {
          avgOverdueHour = parseInt((avgOverdueTimeInMinute - avgOverdueDay * 60 * 24) / 60);
        }
        avgOverdueMinute = avgOverdueTimeInMinute % 60;
        let timeLabel = "";
        if (avgOverdueDay > 0) {
          timeLabel += avgOverdueDay + "天";
        }
        if (avgOverdueHour > 0) {
          timeLabel += avgOverdueHour + "小时";
        }
        timeLabel += avgOverdueMinute + "分钟";

        let label = param.name + "<br/>";
        label += param.marker + param.seriesName + "<span style='padding-left:20px' />" + timeLabel + "<br/>";
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
            _this.getPageContext().emitEvent('iGgNlioQqhUgdbYjrfipnnmdQWHbNJaq:showDrawer', {
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

}
export default FlowOverdueAvgChartDevelopment;
