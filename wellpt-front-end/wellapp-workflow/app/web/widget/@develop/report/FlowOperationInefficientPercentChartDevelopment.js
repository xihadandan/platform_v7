import FlowChartDevelopmentBase from './FlowChartDevelopmentBase.js';

class FlowOperationInefficientPercentChartDevelopment extends FlowChartDevelopmentBase {

  get META() {
    return {
      name: '流程统计分析_流程行为分析_流程低效操作率图表二开',
      hook: {
        itemClick: '查看详情',
      }
    }
  }

  created() {
    this.maxBarCount = 12;
    this.getSetting().then(setting => {
      this.maxBarCount = setting.flowOperation && setting.flowOperation.maxInefficientBarCount || 12;
    });
  }

  mounted() {
    this.getPageContext().handleEvent('searchFlowOperation', searchForm => {
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

  beforeLoadData(params) {
    const _this = this;
    let $widget = _this.$widget;
    if ($widget.widget.id == 'ZmPNxDoQhFiLkybVbkUonXQXEUXHGYMo') {
      params["actionCodes"] = [4, 5];
      params["seriesName"] = "退回率";
    } else if ($widget.widget.id == 'mCVjzAnAUbtRFJCFGHEavCYXdyPkBuNS') {
      params["actionCodes"] = [7];
      params["seriesName"] = "转办率";
    } else if ($widget.widget.id == 'MRSZKfTsItpqQYKiXiCJtJhmcUNjZneh') {
      params["actionCodes"] = [8];
      params["seriesName"] = "会签率";
    } else if ($widget.widget.id == 'LhvCklYnVeuaZqnQKKhxiMaIEAPykBjV') {
      params["actionCodes"] = [33];
      params["seriesName"] = "加签率";
    } else if ($widget.widget.id == 'MVGEJLbWIimoHjWNOpAWEpzZZrSloBNq') {
      params["actionCodes"] = [15];
      params["seriesName"] = "移交率";
    } else if ($widget.widget.id == 'jUcDSuLKHcTpJhwsvtQXAcsKeTrwVvYM') {
      params["actionCodes"] = [16];
      params["seriesName"] = "跳转率";
    }
    _this.$widget.params = params;
  }

  itemClick($event) {
    const _this = this;
    let flowDefId = $event.data.id;
    let title = `流程环节低效操作 - ${$event.name}`;
    if (_this.$widget.params.seriesName) {
      title = `流程${_this.$widget.params.seriesName} - ${$event.name}`;
    }
    _this.getPageContext().emitEvent('sKksGabqZOqOQdtOMOklVrFVIaOUpSyC:showDrawer', {
      eventParams: {
        ..._this.$widget.params,
        flowDefId,
        title
      }
    });
  }

  beforeChartRender(options) {
    const _this = this;
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
      valueFormatter(value) {
        return value + "%";
      }
    });

    // 隐藏标题，只用于下载的图片名称
    options.title.textStyle = {
      fontSize: 0
    }

    // toolbox设置
    options.toolbox = {
      feature: {
        myTool1: {
          show: true,
          title: '更多',
          icon: 'path://M-6.2277 -1.9018L-3.5491 -1.9018L-3.5491 4.8214L-6.2277 4.8214L-6.2277 -1.9018ZM-1.3527 -4.5536L1.3259 -4.5536L1.3259 4.8214L-1.3527 4.8214L-1.3527 -4.5536ZM3.5491 -7.5L6.2277 -7.5L6.2277 4.8214L3.5491 4.8214L3.5491 -7.5ZM-7.192 7.5L7.192 7.5',
          onclick: function () {
            let title = _this.$widget.configuration.titleConfig.text;
            if (_this.$widget.params.seriesName) {
              title = `流程${_this.$widget.params.seriesName}`;
            }
            _this.getPageContext().emitEvent('WeMmHoHUwfobAhRShRgdYZHkKuYpLSIM:showDrawer', {
              eventParams: {
                ..._this.$widget.params,
                title
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
export default FlowOperationInefficientPercentChartDevelopment;
