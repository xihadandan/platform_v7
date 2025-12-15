import FlowChartDevelopmentBase from './FlowChartDevelopmentBase.js';

class FlowHandleEfficiencyByDeptChartDevelopment extends FlowChartDevelopmentBase {

  get META() {
    return {
      name: '流程统计分析_办理效率分析按部门统计',
      hook: {
        itemClick: '查看详情',
      }
    }
  }

  created() {
    this.maxBarCount = 12;
    this.getSetting().then(setting => {
      this.maxBarCount = setting.flowHandleEfficiency && setting.flowHandleEfficiency.maxDeptBarCount || 12;
    });
  }

  mounted() {
    this.getPageContext().handleEvent('searchDeptFlowHandleEfficiency', searchForm => {
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
    let deptId = $event.data.deptId;
    let title = `部门办理效率 - ${$event.name}`;
    _this.getPageContext().emitEvent('nrJYEzrNeqUBWHavBVdkaYkpGUWigOHh:showDrawer', {
      eventParams: {
        ..._this.$widget.params,
        deptId,
        title
      }
    });
  }

  beforeChartRender(options) {
    const _this = this;
    options.grid[0].containLabel = true
    if (options.xAxis[0]) {
      options.xAxis[0].max = _this.maxBarCount - 1;
      options.xAxis[0].axisLabel = Object.assign(options.xAxis[0].axisLabel || {}, {
        interval: 0,// 显示所有标签
        width: 100,
        overflow: 'break'
      });
    }

    // 纵坐标设置
    if (options.dataset[0]) {
      let maxTimeInHour = 0;
      options.dataset[0].source && options.dataset[0].source.forEach(item => {
        maxTimeInHour = Math.max(maxTimeInHour, parseInt(item.avgHandleTimeInHour));
      });
      let yAxisMax = _this.getYAxisMax(maxTimeInHour);
      options.yAxis[0].position = 'left';
      options.yAxis[0].min = 0;
      options.yAxis[0].interval = yAxisMax / 5;
      options.yAxis[0].max = yAxisMax;
      // 最大操作次数
      let maxCount = 0;
      options.dataset[1].source && options.dataset[1].source.forEach(item => {
        maxCount = Math.max(maxCount, parseInt(item.handleCount));
      });
      let yAxisMaxOfCount = _this.getYAxisMax(maxCount);
      options.yAxis.push({
        gridIndex: 0,
        name: '操作次数',
        type: 'value',
        min: 0,
        interval: yAxisMaxOfCount / 5,
        max: yAxisMaxOfCount,
        position: 'right',
      });
      options.series[1].yAxisIndex = 1;
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
          if (param.data.hasOwnProperty('avgHandleTimeInMinute')) {
            let avgHandleDay = 0;
            let avgHandleHour = 0;
            let avgHandleMinute = 0;
            let avgHandleTimeInMinute = parseFloat(param.data.avgHandleTimeInMinute);
            if (avgHandleTimeInMinute >= 60 * 24) {
              avgHandleDay = parseInt(avgHandleTimeInMinute / (60 * 24));
            }
            if (avgHandleTimeInMinute >= 60) {
              avgHandleHour = parseInt((avgHandleTimeInMinute - avgHandleDay * 60 * 24) / 60);
            }
            avgHandleMinute = avgHandleTimeInMinute % 60;
            let timeLabel = "";
            if (avgHandleDay > 0) {
              timeLabel = `${avgHandleDay}天${avgHandleHour}小时${avgHandleMinute}分`;
            } else if (avgHandleHour > 0) {
              timeLabel = `${avgHandleHour}小时${avgHandleMinute}分`;
            } else {
              timeLabel = `${avgHandleMinute}分`;
            }
            label += param.marker + `<span style='display:inline-block;width:120px;'>${param.seriesName}</span>` + timeLabel + "<br/>";
          } else {
            label += param.marker + `<span style='display:inline-block;width:120px;'>${param.seriesName}</span>` + param.data.handleCount + "<br/>";
          }
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
            _this.getPageContext().emitEvent('SoVxKNBrZjXjMuwzPgbjLLWpVTogIkWD:showDrawer', {
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
export default FlowHandleEfficiencyByDeptChartDevelopment;
