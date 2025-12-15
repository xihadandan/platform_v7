import WidgetChartDevelopment from '@develop/WidgetChartDevelopment';

class FlowInstanceCountByCategoryChartDevelopment extends WidgetChartDevelopment {

  mounted() {
    this.getPageContext().handleEvent('searchFlowInstCount', searchForm => {
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

  beforeChartRender(options) {
    options.tooltip.show = false;
    let label = options.series[0].label || {};
    label.formatter = (params) => {
      return `{count|${params.data.数量}}\n${params.name}`;
    };
    // label.formatter = "{count|{c}}\n{b}";
    label.rich = {
      count: {
        fontSize: 22,
        fontWeight: 'bold',
        marginBottom: '10px',
      }
    }
    options.series[0].label = label;
    options.series[0].labelLine = {
      show: false,
    }
    options.series[0].labelLayout = {
      x: "45%",
      y: '50%',
    }
    // options.series[0].left = 0;
    // options.series[0].right = '66.6667%';
    // let completed = JSON.parse(JSON.stringify(options.series[0]));
    // completed.id = completed.id + '_completed';
    // completed.datasetIndex = 1;
    // completed.left = '33.3333%';
    // completed.right = '33.3333%';
    // let uncompleted = JSON.parse(JSON.stringify(options.series[0]));
    // uncompleted.datasetIndex = 2;
    // uncompleted.id = uncompleted.id + '_uncompleted';
    // uncompleted.left = '66.6667%';
    // uncompleted.right = 0;
    // options.series.push(completed);
    // options.series.push(uncompleted);
  }

  get META() {
    return {
      name: '流程统计分析_流程实例发起数量统计图表二开',
    }
  }
}
export default FlowInstanceCountByCategoryChartDevelopment;
