<template>
  <div>
    <div
      :style="{ height: vContainerHeight, width: vContainerWidth, backgroundColor: widget.useScope == 'bigScreen' ? 'unset' : '#fff' }"
    ></div>
    <a-empty
      v-if="showEmptyConfigTip"
      style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%)"
      description="图表暂无配置"
    ></a-empty>
    <div class="spin-center" v-if="loading">
      <a-spin />
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import chartMixin from '../widget-bar-chart/chart.mixin';
export default {
  name: 'WidgetEmptyChart',
  mixins: [widgetMixin, chartMixin],
  props: {},
  components: {},
  computed: {
    showEmptyConfigTip() {
      return (
        this.designMode &&
        ((this.widget.configuration.optionSourceType == 'static' && !this.widget.configuration.optionJson) ||
          (this.widget.configuration.optionSourceType == 'jsModule' && !this.widget.configuration.optionJsFunction))
      );
    }
  },
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    initChart() {
      let { optionSourceType, optionJson, optionJsFunction, autoRefresh, refreshWaitSeconds } = this.widget.configuration;
      let option = undefined;
      if (optionSourceType == 'static' && optionJson) {
        try {
          let func = new Function('echarts', 'return ' + optionJson);
          option = func.apply(this, [this.echarts]);
        } catch (error) {
          console.error('自定义图表配置输入异常: ', error);
        }
        if (option) {
          this.chart.setOption(option);
        }
      } else if (optionSourceType == 'jsModule' && optionJsFunction) {
        option = this.invokeJsFunction(optionJsFunction);
        if (option) {
          if (option instanceof Promise) {
            option.then(opt => {
              this.chart.setOption(opt);
              if (autoRefresh) {
                this.startTimeoutRefreshChart(1000 * refreshWaitSeconds);
              }
            });
          } else {
            this.chart.setOption(option);
            if (autoRefresh) {
              this.startTimeoutRefreshChart(1000 * refreshWaitSeconds);
            }
          }
        }
      }
      this.chart.hideLoading();
    }
  }
};
</script>
