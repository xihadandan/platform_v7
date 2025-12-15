import FlowChartDevelopmentBase from './FlowChartDevelopmentBase.js';

class FlowTodoCountByUserChartDevelopment extends FlowChartDevelopmentBase {

  get META() {
    return {
      name: '流程统计分析_待办流程分析按人员_待办数量排名',
      hook: {
        itemClick: '查看详情',
      }
    }
  }

  created() {
    this.maxBarCount = 12;
    this.getSetting().then(setting => {
      this.maxBarCount = setting.flowTodo && setting.flowTodo.maxUserBarCount || 12;
    });
  }

  mounted() {
    this.getPageContext().handleEvent('searchUserFlowTodo', searchForm => {
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
    let title = `人员待办 - ${$event.name}`;
    _this.getPageContext().emitEvent('nDAKBbTwHuZStKlflvVKONUKLXgZuBlg:showDrawer', {
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
    options.tooltip = Object.assign(options.tooltip || {}, {
      trigger: 'axis',
      borderColor: "#333",
      backgroundColor: "rgba(50,50,50,0.7)",
      textStyle: {
        color: '#fff'
      },
      formatter: (params, ticket) => {
        let arrays = [...params];
        arrays.reverse();
        let todoCount = 0;
        arrays.map(item => { todoCount += parseInt(item.data.count) });
        let label = `待办数量${todoCount}<br/>`;
        arrays.forEach(param => {
          label += param.marker + param.seriesName + "<span style='padding-left:20px' />" + param.data.count + "<br/>";
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
            _this.getPageContext().emitEvent('SbjWPuheQgfKBPHuWJyHKmNBTMXZKqFM:showDrawer', {
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
export default FlowTodoCountByUserChartDevelopment;
