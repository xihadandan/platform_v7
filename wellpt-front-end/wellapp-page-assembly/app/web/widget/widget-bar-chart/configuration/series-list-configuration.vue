<template>
  <div>
    <a-table
      class="widget-chart-series-table no-border"
      rowKey="id"
      :columns="columnDefine"
      :dataSource="widget.configuration.series"
      :pagination="false"
      :bordered="false"
      size="small"
    >
      <template slot="nameSlot" slot-scope="text, record, index">
        <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-column-handler" :style="{ cursor: 'move' }" />
        <a-input v-model="record.name" size="small" :style="{ width: '95px' }" :placeholder="'系列 ' + (index + 1)"></a-input>
        <a-icon type="bar-chart" v-if="record.type == 'bar'" title="柱状图" />
        <a-icon type="line-chart" v-if="record.type == 'line'" title="折线图" />
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <WidgetDesignDrawer :id="'seriesConfig' + record.id" :title="'编辑系列 ' + (index + 1)" :designer="designer">
          <a-button size="small" type="link" :title="'编辑系列 ' + (index + 1)">
            <Icon type="pticon iconfont icon-ptkj-shezhi" />
          </a-button>
          <template slot="content">
            <SeriesConfiguration :widget="widget" :designer="designer" :series="record" :datasetColumns="datasetColumns" />
          </template>
        </WidgetDesignDrawer>
        <a-button
          size="small"
          type="link"
          v-if="widget.wtype !== 'WidgetPieChart' && widget.configuration.series.length > 1"
          @click="widget.configuration.series.splice(index, 1)"
          title="删除"
        >
          <Icon type="pticon iconfont icon-ptkj-shanchu" />
        </a-button>
      </template>
      <template slot="footer">
        <WidgetDesignDrawer :id="'seriesConfig' + widget.id" title="新增系列" :designer="designer">
          <a-button type="link" icon="plus" size="small" :style="{ paddingLeft: '7px' }" v-if="widget.wtype !== 'WidgetPieChart'">
            新增
          </a-button>
          <template slot="content">
            <SeriesConfiguration
              :widget="widget"
              :designer="designer"
              :series="newSeriesItem"
              ref="seriesConfig"
              :datasetColumns="datasetColumns"
            />
          </template>
          <template slot="footer" slot-scope="{ close }">
            <a-button size="small" type="primary" @click.stop="onConfirmOk(close)">确定</a-button>
          </template>
        </WidgetDesignDrawer>
      </template>
    </a-table>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import draggable from '@framework/vue/designer/draggable';
import SeriesConfiguration from './series-configuration.vue';
export default {
  name: 'SeriesListConfiguration',
  mixins: [draggable],
  props: {
    widget: Object,
    designer: Object,
    datasetColumns: Array
  },
  components: { SeriesConfiguration },
  computed: {},
  data() {
    return {
      columnDefine: [
        { title: '名称', dataIndex: 'name', scopedSlots: { customRender: 'nameSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' }, align: 'center' }
      ],
      newSeriesItem: this.getDefaultSeriesItemConfiguration()
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.tableDraggable(
      this.widget.configuration.series,
      this.$el.querySelector('.widget-chart-series-table tbody'),
      '.drag-column-handler'
    );
  },
  methods: {
    getDefaultSeriesItemConfiguration() {
      let conf = {
        type: this.widget.wtype == 'WidgetLineChart' ? 'line' : 'bar',
        name: undefined, // 名称默认取维度

        /**
         * 当使用 dataset 时，seriesLayoutBy 指定了 dataset 中用行还是列对应到系列上，也就是说，系列“排布”到 dataset 的行还是列上。可取值：
         * 'column'：默认，dataset 的列对应于系列，从而 dataset 中每一列是一个维度（dimension）。
         * 'row'：dataset 的行对应于系列，从而 dataset 中每一行是一个维度（dimension）
         */
        seriesLayoutBy: 'column',
        colorBy: 'series',
        xAxisIndex: 0,
        yAxisIndex: 0,
        datasetIndex: 0,
        encode: {
          x: [],
          y: []
        },
        dataFrom: {
          type: 'dataset',
          service: undefined,
          from: 'static'
        },
        stack: null,
        barWidth: undefined,
        barWidthUnit: 'px',
        itemStyle: { color: undefined, borderRadius: 0 },
        label: {
          show: false,
          formatter: undefined
        }
      };
      if (this.widget.wtype == 'WidgetLineChart') {
        conf.areaStyle = {
          enable: false
        };
      }
      return conf;
    },

    onConfirmOk(close) {
      this.$refs.seriesConfig.save().then(() => {
        let xAxisIndex = this.newSeriesItem.xAxisIndex,
          item = JSON.parse(JSON.stringify(this.newSeriesItem));
        for (let s of this.widget.configuration.series) {
          if (s.type == 'bar' && xAxisIndex == s.xAxisIndex && s.barWidth != undefined) {
            item.barWidth = s.barWidth;
            item.barWidthUnit = s.barWidthUnit;
            break;
          }
        }
        this.widget.configuration.series.push(item);
        this.newSeriesItem = this.getDefaultSeriesItemConfiguration();
        close();
      });
    }
  }
};
</script>
