<template>
  <view class="content">
    <view class="echarts" id="echarts" :prop="option"></view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      option: {
        title: {
          // text: "ECharts 入门示例",
        },
        tooltip: {},
        legend: {
          data: ["销量"],
        },
        xAxis: {
          data: ["衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子"],
        },
        yAxis: {},
        series: [
          {
            name: "销量",
            type: "bar",
            data: [5, 20, 36, 10, 10, 20],
          },
        ],
      },
    };
  },
  onLoad() {},
  methods: {},
};
</script>

<script module="echarts" lang="renderjs">
 import * as echarts from 'echarts';

export default {
	mounted() {
    this.initEcharts()
	},
	methods: {
		initEcharts() {
      let myChart = echarts.init(document.getElementById('echarts'))
      // 观测更新的数据在 view 层可以直接访问到
      myChart.setOption(this.option)
		},
	}
}
</script>

<style>
.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.echarts {
  width: 100%;
  height: 300px;
}
</style>
