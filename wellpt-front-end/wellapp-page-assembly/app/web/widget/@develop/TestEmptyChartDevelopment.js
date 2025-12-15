import WidgetChartDevelopment from '@develop/WidgetChartDevelopment';

class TestEmptyChartDevelopment extends WidgetChartDevelopment {


  testReturnOption() {
    return new Promise((resolve, reject) => {
      let data = Array.from({ length: 5 }, () => parseInt(Math.random(100, 1000) * 1000))
      resolve({
        xAxis: {
          max: 'dataMax'
        },
        yAxis: {
          type: 'category',
          data: ['A', 'B', 'C', 'D', 'E'],
          inverse: true,
          animationDuration: 300,
          animationDurationUpdate: 300,
          max: 2
        },
        series: [
          {
            realtimeSort: true,
            name: 'X',
            type: 'bar',
            data: data,
            label: {
              show: true,
              position: 'right',
              valueAnimation: true
            }
          }
        ],
        legend: {
          show: true
        },
        animationDuration: 0,
        animationDurationUpdate: 3000,
        animationEasing: 'linear',
        animationEasingUpdate: 'linear'
      })
    })

  }

  get META() {
    return {
      name: '测试自定义图表二开',
      hook: {
        testReturnOption: '返回配置'
      }
    }
  }
}
export default TestEmptyChartDevelopment;
