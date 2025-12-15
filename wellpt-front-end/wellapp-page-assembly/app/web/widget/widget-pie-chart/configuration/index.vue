<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <EchartsVersion />
        <a-form-model-item label="名称">
          <a-input v-model="widget.title" />
        </a-form-model-item>
        <a-form-model-item label="JS模块">
          <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetChartDevelopment" width="205px" />
        </a-form-model-item>
        <a-form-model-item label="高度">
          <a-input-group compact>
            <a-input-number v-model="widget.configuration.style.height" style="width: 100px" :min="1" />
            <a-button>px</a-button>
          </a-input-group>
        </a-form-model-item>
        <a-form-model-item label="渲染模式">
          <a-radio-group v-model="widget.configuration.renderer" button-style="solid" size="small">
            <a-radio-button value="canvas">Canvas</a-radio-button>
            <a-radio-button value="svg">SVG</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item>
          <template slot="label">
            自动刷新
            <a-checkbox v-model="widget.configuration.autoRefresh" />
          </template>
          <a-input-group compact v-show="widget.configuration.autoRefresh">
            <a-input-number v-model="widget.configuration.refreshWaitSeconds" :min="1" />
            <a-button>秒</a-button>
          </a-input-group>
        </a-form-model-item>
        <a-collapse :bordered="false" expandIconPosition="right">
          <a-collapse-panel key="datasetSetting" header="数据集定义">
            <DatasetConfiguration :widget="widget" :designer="designer" :datasetColumns="datasetColumns" />
          </a-collapse-panel>
          <a-collapse-panel key="seriesSetting">
            <template slot="header">
              <a-tooltip title="用于构建图表中的一组数据以及数据呈现的图表类型">
                系列定义
                <a-icon type="info-circle" style="font-size: inherit" />
              </a-tooltip>
            </template>
            <SeriesListConfiguration :widget="widget" :designer="designer" :datasetColumns="datasetColumns" />
          </a-collapse-panel>

          <a-collapse-panel key="titleSetting" header="标题定义">
            <TitleConfiguration :widget="widget" :designer="designer" />
          </a-collapse-panel>
          <a-collapse-panel key="legendSetting" header="图例定义">
            <LegendConfiguration :widget="widget" :designer="designer" :legend="widget.configuration.legendConfig" />
          </a-collapse-panel>
          <a-collapse-panel key="clrSetting">
            <template slot="header">
              <a-tooltip title="如果系列定义没有设置颜色，则会依次循环从该列表中取颜色作为系列颜色">
                调色盘
                <a-icon type="info-circle" style="font-size: inherit" />
              </a-tooltip>
            </template>
            <ColorConfiguration :widget="widget" :designer="designer" />
          </a-collapse-panel>
        </a-collapse>
      </a-tab-pane>

      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration
          :widget="widget"
          :designer="designer"
          :varOptions="[
            { label: '图形名称', value: 'componentType' },
            { label: '系列类型', value: 'seriesType' },
            { label: '类目名', value: 'name' },
            { label: '数据值', value: 'value' }
          ]"
        >
          <template slot="eventParamValueHelpSlot">
            <div style="width: 600px">
              <p>
                1. 支持通过
                <a-tag>${ 参数 }</a-tag>
                表达式解析以下对象值:
              </p>
              <ul>
                <li>componentType : 当前点击的图形元素所属的组件名称, 其值如 series 、markLine、markPoint、timeLine 等</li>
                <li>seriesType : 系列类型。值可能为 line、bar、pie 等。当 componentType 为 series 时有意义</li>
                <li>seriesIndex : 当前系列下标。当 componentType 为 series 时有意义</li>
                <li>seriesName : 当前系列名称。当 componentType 为 series 时有意义</li>
                <li>name : 数据名, 类目名</li>
                <li>data : 数据项</li>
                <li>value : 数据值</li>
              </ul>
            </div>
          </template>
        </WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import DatasetConfiguration from '../../widget-bar-chart/configuration/dataset-configuration.vue';
import SeriesListConfiguration from '../../widget-bar-chart/configuration/series-list-configuration.vue';
import ColorConfiguration from '../../widget-bar-chart/configuration/color-configuration.vue';
import TitleConfiguration from '../../widget-bar-chart/configuration/title-configuration.vue';
import LegendConfiguration from '../../widget-bar-chart/configuration/legend-configuration.vue';
import EchartsVersion from '../../widget-bar-chart/configuration/echarts-version.vue';

import { generateId } from '@framework/vue/utils/util';

export default {
  name: 'WidgetPieChartConfiguration',
  props: { widget: Object, designer: Object },
  components: {
    DatasetConfiguration,
    SeriesListConfiguration,
    ColorConfiguration,
    TitleConfiguration,
    LegendConfiguration,
    EchartsVersion
  },
  computed: {},
  data() {
    return { datasetColumns: [] };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {},

  configuration(widget) {
    let conf = {
      renderer: 'canvas',
      jsModules: [],
      style: {
        height: 300
      },
      dataSourceType: 'static',
      datasetJson: ` {
      source: [
        ['dimension', '系列 1', '系列 2', '系列 3', '系列 4', '系列 5', '系列 6'],
        ['维度 A', 56.5, 82.1, 88.7, 70.1, 53.4, 85.1],
        ['维度 B', 51.1, 51.4, 55.1, 53.3, 73.8, 68.7],
        ['维度 C', 40.1, 62.2, 69.5, 36.4, 45.2, 32.5],
        ['维度 D', 25.2, 37.1, 41.2, 18, 33.9, 49.1]
      ]
  }`,
      datasetService: undefined,
      color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc'],
      series: [
        {
          id: generateId(9),
          name: undefined,
          type: 'pie',
          seriesLayoutBy: 'column',
          datasetIndex: 0,
          dataFrom: {
            type: 'dataset',
            service: undefined,
            from: 'static',
            dataJson: undefined
          },
          itemStyle: {
            color: undefined,
            borderRadius: 0
          },
          label: {
            show: true,
            formatter: undefined,
            position: 'outside'
          },
          stack: null,
          center: [50, 60],
          centerUnit: ['%', '%'],
          radius: [0, 60],
          radiusUnit: ['px', '%']
        }
      ],

      titleConfig: {
        show: true,
        text: '饼图',
        subtext: undefined,
        leftUnit: 'px',
        left: 10,
        top: 10,
        topUnit: 'px',
        bottomUnit: 'px',
        rightUnit: 'px',
        itemGap: 10,
        textStyle: {
          color: '#333333',
          fontWeight: 'bold',
          fontFamily: 'sans-serif',
          fontSize: 16
        },
        subtextStyle: { color: undefined, fontSize: 12, fontWeight: undefined, fontFamily: 'sans-serif' }
      },
      legendConfig: {
        type: 'plain',
        id: generateId(9),
        show: true,
        orient: 'horizontal',
        left: undefined,
        leftUnit: '%',
        top: 32,
        topUnit: 'px',
        right: 20,
        rightUnit: 'px',
        bottom: undefined,
        bottomUnit: '%',
        width: undefined,
        widthUnit: 'auto',
        height: undefined,
        heightUnit: 'auto',
        align: 'auto',
        itemGap: 10,
        itemWidth: 25,
        itemHeight: 14,
        formatter: undefined,
        selectedMode: 'multiple',
        textStyle: {
          color: '#333'
        }
      },
      domEvents: [
        {
          id: 'onClick',
          title: '点击时候触发',
          codeSource: 'codeEditor',
          jsFunction: undefined,
          widgetEvent: [],
          customScript: undefined // 事件脚本
        }
      ]
    };

    if (widget != undefined && widget.useScope == 'bigScreen') {
      conf.style.width = 600;
      conf.legendConfig.textStyle.color = '#fff';
    }
    return conf;
  }
};
</script>
