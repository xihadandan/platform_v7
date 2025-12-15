import FlowChartDevelopmentBase from './FlowChartDevelopmentBase.js';

class FlowHandleOverdueOfAvgByUserChartDevelopment extends FlowChartDevelopmentBase {

  get META() {
    return {
      name: '流程统计分析_办理逾期分析_人员平均逾期时长',
      hook: {
        itemClick: '查看详情',
      }
    }
  }

  created() {
    this.maxBarCount = 12;
    this.getSetting().then(setting => {
      this.maxBarCount = setting.flowHandleOverdue && setting.flowHandleOverdue.maxUserBarCount || 12;
    });
  }

  mounted() {
    this.getPageContext().handleEvent('searchUserFlowHandleOverdue', searchForm => {
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
    let userId = $event.data.userId;
    let title = `人员逾期明细 - ${$event.name}`;
    _this.getPageContext().emitEvent('HdCoJppgJgrMhBjtWcHXgPEHWVuIzdZO:showDrawer', {
      eventParams: {
        ..._this.$widget.params,
        userId,
        title
      }
    });
  }

  beforeChartRender(options) {
    const _this = this;
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
        let label = params[0].axisValueLabel + "<br/>";
        params.forEach(param => {
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
            timeLabel = `${avgOverdueDay}天${avgOverdueHour}小时${avgOverdueMinute}分`;
          } else if (avgOverdueHour > 0) {
            timeLabel = `${avgOverdueHour}小时${avgOverdueMinute}分`;
          } else {
            timeLabel = `${avgOverdueMinute}分`;
          }
          label += param.marker + `<span style='display:inline-block;width:120px;'>${param.seriesName}</span>` + timeLabel + "<br/>";
        });
        return label;
      }
    });

    options.toolbox = {
      feature: {
        myTool1: {
          show: true,
          title: '更多',
          icon: 'path://M-6.2277 -1.9018L-3.5491 -1.9018L-3.5491 4.8214L-6.2277 4.8214L-6.2277 -1.9018ZM-1.3527 -4.5536L1.3259 -4.5536L1.3259 4.8214L-1.3527 4.8214L-1.3527 -4.5536ZM3.5491 -7.5L6.2277 -7.5L6.2277 4.8214L3.5491 4.8214L3.5491 -7.5ZM-7.192 7.5L7.192 7.5',
          onclick: function () {
            _this.getPageContext().emitEvent('SONAAqTfBGYrnEVuVozPzKjvGlhIrCTf:showDrawer', {
              eventParams: {
                ..._this.$widget.params,
                title: _this.$widget.configuration.titleConfig.text
              }
            });
          }
        },
        saveAsImage: {},
        // myTool2: {
        //   show: true,
        //   title: '导出',
        //   icon: 'path://M-2.7513 -7.5L-5.9524 -4.2989L-2.6984 -1.045M-5.8466 -4.2989L5.9524 -4.2989L5.9524 7.5L-5.8466 7.5L-5.8466 0.6217',
        //   onclick: function () {
        //   }
        // },
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
export default FlowHandleOverdueOfAvgByUserChartDevelopment;
