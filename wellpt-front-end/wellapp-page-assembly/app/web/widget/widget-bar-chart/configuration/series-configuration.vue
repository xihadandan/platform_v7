<template>
  <Scroll style="height: calc(100vh - 220px); padding-right: 10px">
    <a-form-model>
      <a-form-model-item>
        <template slot="label">
          <a-tooltip title="用于提示文字的显示, 图例筛选">
            系列名称
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-tooltip>
        </template>
        <a-input v-model="series.name" />
      </a-form-model-item>
      <a-form-model-item label="使用数据">
        <a-radio-group size="small" v-model="series.dataFrom.type" button-style="solid">
          <a-radio-button value="data">当前定义</a-radio-button>
          <a-radio-button value="dataset">数据集</a-radio-button>
        </a-radio-group>
        <span style="margin-left: 8px" v-if="series.dataFrom.type === 'dataset'">
          <label>使用第</label>
          <a-input-number size="small" v-model="datasetIndex" :min="1" @change="onChangeDatasetIndexNum" style="width: 60px" />
          <label>个数据集</label>
        </span>
      </a-form-model-item>
      <div
        v-if="
          (widget.wtype == 'WidgetBarChart' || widget.wtype == 'WidgetLineChart') &&
          ['dataModel', 'dataSource'].includes(widget.configuration.dataSourceType) &&
          series.encode != undefined &&
          series.dataFrom.type == 'dataset'
        "
        style="display: flex; flex-wrap: wrap; justify-content: space-between"
      >
        <a-form-model-item label="X轴数据属性" :label-col="{ style: { width: '115px' } }" :wrapper-col="{ style: { width: '160px' } }">
          <a-select v-model="series.encode.x" :options="datasetColumnOptions"></a-select>
        </a-form-model-item>
        <a-form-model-item label="Y轴数据属性" :label-col="{ style: { width: '115px' } }" :wrapper-col="{ style: { width: '160px' } }">
          <a-select v-model="series.encode.y" :options="datasetColumnOptions"></a-select>
        </a-form-model-item>
      </div>

      <div
        v-if="
          widget.wtype == 'WidgetPieChart' &&
          ['dataModel', 'dataSource'].includes(widget.configuration.dataSourceType) &&
          series.dataFrom.type == 'dataset'
        "
        style="display: flex; flex-wrap: wrap; justify-content: space-between"
      >
        <a-form-model-item label="数据项属性" :label-col="{ style: { width: '115px' } }" :wrapper-col="{ style: { width: '160px' } }">
          <a-select v-model="series.dataNameProp" :options="datasetColumnOptions"></a-select>
        </a-form-model-item>
        <a-form-model-item label="数据值属性" :label-col="{ style: { width: '115px' } }" :wrapper-col="{ style: { width: '160px' } }">
          <a-select v-model="series.dataValueProp" :options="datasetColumnOptions"></a-select>
        </a-form-model-item>
      </div>

      <template v-if="series.dataFrom.type == 'data'">
        <a-form-model-item label="数据源类型">
          <a-radio-group size="small" v-model="series.dataFrom.from" button-style="solid">
            <a-radio-button value="static">静态JSON数据</a-radio-button>
            <a-popover placement="bottomRight" :arrowPointAtCenter="true">
              <template slot="content">
                <div>
                  提供继承于
                  <code>com.wellsoft.pt.report.echart.support.EchartSeriesDataLoad</code>
                  的后端服务类实例
                </div>
              </template>
              <a-radio-button value="service">数据接口</a-radio-button>
            </a-popover>
          </a-radio-group>
          <a-select
            v-if="series.dataFrom.from == 'service'"
            show-search
            allow-clear
            :options="dataServiceOptions"
            v-model="series.dataFrom.service"
            :filter-option="filterSelectOption"
          ></a-select>
          <WidgetCodeEditor
            v-else
            v-model="series.dataFrom.dataJson"
            width="auto"
            height="200px"
            lang="json"
            :hideError="true"
          ></WidgetCodeEditor>
        </a-form-model-item>
      </template>
      <a-form-model-item label="系列排布">
        <a-radio-group size="small" v-model="series.seriesLayoutBy" button-style="solid">
          <a-tooltip title="数据集的列对应于系列, 从而数据集的每一列是一个维度">
            <a-radio-button value="column">列</a-radio-button>
          </a-tooltip>
          <a-tooltip title="数据集的行对应于系列, 从而数据集的每一行是一个维度">
            <a-radio-button value="row">行</a-radio-button>
          </a-tooltip>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="图表类型" v-if="widget.wtype == 'WidgetBarChart' || widget.wtype == 'WidgetLineChart'">
        <a-radio-group size="small" v-model="series.type" button-style="solid" @change="onChangeSeriesType">
          <a-tooltip title="柱状图">
            <a-radio-button value="bar">
              <a-icon type="bar-chart" />
            </a-radio-button>
          </a-tooltip>
          <a-tooltip title="折线图">
            <a-radio-button value="line">
              <a-icon type="line-chart" />
            </a-radio-button>
          </a-tooltip>
        </a-radio-group>
      </a-form-model-item>
      <template v-if="series.type == 'line'">
        <a-form-model-item>
          <template slot="label">
            <a-tooltip title="平滑曲线, 为同一坐标系的曲线图共享配置">
              平滑曲线
              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
            </a-tooltip>
          </template>

          <a-switch v-model="series.smooth" @change="onChangeSmooth" />
        </a-form-model-item>

        <template v-if="series.areaStyle != undefined">
          <a-form-model-item>
            <template slot="label">
              填充效果
              <a-checkbox v-model="series.areaStyle.enable" />
            </template>
          </a-form-model-item>
        </template>
      </template>
      <template v-if="series.type == 'pie'">
        <div style="display: flex; flex-wrap: wrap; justify-content: space-between">
          <template v-for="(opt, i) in piePositionDist">
            <a-form-model-item
              :label="opt.label"
              :key="'postDist_' + i"
              :label-col="{ style: { width: i % 2 == 0 ? '115px' : '60px' } }"
              :wrapper-col="{ style: { width: '160px' } }"
              style="display: flex"
            >
              <a-input-group compact style="width: 100%">
                <a-input-number
                  v-model="series[opt.value][opt.index]"
                  :min="0"
                  :max="series[opt.value + 'Unit'][opt.index] == '%' ? 100 : 3000"
                />
                <a-select v-model="series[opt.value + 'Unit'][opt.index]" :style="{ width: '70px' }">
                  <a-select-option value="px">px</a-select-option>
                  <a-select-option value="%">%</a-select-option>
                </a-select>
              </a-input-group>
            </a-form-model-item>
          </template>
        </div>
        <a-form-model-item label="扇区间隔角度">
          <a-input-number :min="0" :max="360" v-model="series.padAngle" />
        </a-form-model-item>
      </template>
      <LabelConfiguration :widget="widget" :designer="designer" :series="series" />
      <ItemStyleConfiguration :widget="widget" :designer="designer" :series="series" />
      <div v-if="series.type === 'bar'">
        <a-form-model-item>
          <template slot="label">
            <a-tooltip title="柱条的宽度, 不设时自适应。同一坐标系上, 此属性会被多个柱状图系列共享">
              柱条宽度
              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
            </a-tooltip>
          </template>
          <a-input-group compact>
            <a-input-number v-model="series.barWidth" :min="1" @change="onChangeBarWidth" />
            <a-select v-model="series.barWidthUnit" style="width: 60px" @change="onChangeBarWidth">
              <a-select-option value="px">px</a-select-option>
              <a-select-option value="%">%</a-select-option>
            </a-select>
          </a-input-group>
        </a-form-model-item>
      </div>
      <template v-if="series.type !== 'pie'">
        <a-form-model-item label="使用坐标系">
          <a-select v-model="useAxisIndex" @change="onChangeUseAxisIndex">
            <template v-for="(a, i) in widget.configuration.axis">
              <a-select-option :value="i">{{ a.name }}</a-select-option>
            </template>
          </a-select>
        </a-form-model-item>
        <a-form-model-item>
          <template slot="label">
            <a-tooltip title="与其他系列的数据进行堆叠">
              数据堆叠
              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
            </a-tooltip>
          </template>

          <a-select v-model="stack" mode="multiple" allow-clear @change="onChangeStackSelect">
            <template v-for="(s, i) in widget.configuration.series">
              <a-select-option v-if="s.id != series.id" :key="s.id">{{ s.name || '系列 ' + (i + 1) }}</a-select-option>
            </template>
          </a-select>
        </a-form-model-item>
      </template>
    </a-form-model>
  </Scroll>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import ItemStyleConfiguration from './item-style-configuration.vue';
import { filterSelectOption } from '@framework/vue/utils/function';
import LabelConfiguration from './label-configuration.vue';
export default {
  name: 'SeriesConfiguration',
  inject: ['datasetSourceColumns'],
  props: {
    widget: Object,
    designer: Object,
    series: Object,
    datasetColumns: Array
  },
  components: { ItemStyleConfiguration, LabelConfiguration },
  computed: {
    datasetColumnOptions() {
      let options = [];
      if (this.datasetColumns) {
        for (let d of this.datasetColumns) {
          options.push({
            label: d.title,
            value: d.dataIndex
          });
        }
      }
      return options;
    }
  },
  data() {
    return {
      useAxisIndex: 0,
      stack: [],
      dataServiceOptions: [],
      datasetIndex: this.series.datasetIndex + 1,
      piePositionDist: [
        {
          label: '横坐标',
          value: 'center',
          index: 0
        },
        {
          label: '纵坐标',
          value: 'center',
          index: 1
        },
        {
          label: '内半径',
          value: 'radius',
          index: 0
        },
        {
          label: '外半径',
          value: 'radius',
          index: 1
        }
      ]
    };
  },
  beforeCreate() {},
  created() {
    if (
      (this.widget.wtype == 'WidgetBarChart' || this.widget.wtype == 'WidgetLineChart') &&
      ['dataModel', 'dataSource'].includes(this.widget.configuration.dataSourceType) &&
      !this.series.hasOwnProperty('encode') &&
      this.series.dataFrom.type == 'dataset'
    ) {
      this.$set(this.series, 'encode', { x: [], y: [] });
    }
  },
  beforeMount() {},
  mounted() {
    if (this.series.xAxisIndex == 0) {
      this.useAxisIndex = 0;
    }

    this.lastStack = [];
    if (this.series.id != undefined) {
      this.seriesStackChange();
      this.designer.handleEvent(`${this.widget.id}:series-stack-change`, from => {
        if (from != this.series.id) {
          this.seriesStackChange();
        }
      });
    }

    this.fetchDataServiceOptions();
  },
  methods: {
    fetchDataServiceOptions() {
      this.dataServiceOptions.splice(0, this.dataServiceOptions.length);
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'reportFacadeService',
          queryMethod: 'loadEchartSeriesDataServiceData'
        })
        .then(({ data }) => {
          if (data.results) {
            data.results.forEach(d => {
              this.dataServiceOptions.push({
                label: d.text,
                value: d.id
              });
            });
          }
        });
    },
    onChangeDatasetIndexNum() {
      this.series.datasetIndex = this.datasetIndex - 1;
    },
    onChangeBarWidth() {
      if (this.series.id != undefined) {
        let xAxisIndex = this.series.xAxisIndex;
        this.widget.configuration.series.forEach(s => {
          if (s.type == 'bar' && xAxisIndex == s.xAxisIndex) {
            this.$set(s, 'barWidth', this.series.barWidth);
            this.$set(s, 'barWidthUnit', this.series.barWidthUnit);
          }
        });
      }
    },
    onChangeSeriesType() {
      if (this.series.type == 'line') {
        if (this.series.areaStyle == undefined) {
          this.$set(this.series, 'areaStyle', { enable: false });
        }
      }
    },
    seriesStackChange() {
      this.stack.splice(0, this.stack.length);

      if (this.series.stack != undefined && this.series.id != undefined) {
        let currentStack = this.series.stack;
        this.widget.configuration.series.forEach(s => {
          if (s.stack == currentStack && s.id != this.series.id) {
            this.stack.push(s.id);
          }
        });
        this.lastStack = JSON.parse(JSON.stringify(this.stack));
        // 修改堆叠系列的坐标系为同一个坐标系
        let stackMap = {};
        for (let s of this.widget.configuration.series) {
          if (s.stack != undefined) {
            if (stackMap[s.stack] == undefined) {
              stackMap[s.stack] = [s.xAxisIndex, s.yAxisIndex];
            } else {
              s.xAxisIndex = stackMap[s.stack][0];
              s.yAxisIndex = stackMap[s.stack][1];
            }
          }
        }
      }
    },
    onChangeUseAxisIndex() {
      this.series.xAxisIndex = this.useAxisIndex;
      this.series.yAxisIndex = this.useAxisIndex;
    },
    onChangeSmooth() {
      if (this.series.id != undefined) {
        // 需要修改相同坐标系的折线图同时都为平衡才会保持平滑
        let xAxisIndex = this.series.xAxisIndex;
        this.widget.configuration.series.forEach(s => {
          if (s.xAxisIndex == xAxisIndex && s.type == 'line' && s.id != this.series.id) {
            this.$set(s, 'smooth', this.series.smooth);
          }
        });
      }
    },
    save() {
      return new Promise((resolve, reject) => {
        if (this.series.id == undefined) {
          this.series.id = generateId(6);
          this.onChangeStackSelect();
          // 找到同系列的柱状图，barWidth 设置为同值
          if (this.series.type == 'bar') {
            let xAxisIndex = this.series.xAxisIndex;
            let barWidth = this.series.barWidth,
              barWidthUnit = this.series.barWidthUnit;
            this.widget.configuration.series.forEach(s => {
              if (s.xAxisIndex == xAxisIndex && s.type == 'bar') {
                if (barWidth != undefined) {
                  s.barWidth = barWidth;
                  s.barWidthUnit = barWidthUnit;
                }
              }
            });
          }
          this.stack.splice(0, this.stack.length);
          this.lastStack = [];

          if (this.series.type == 'line') {
            let xAxisIndex = this.series.xAxisIndex,
              smooth = this.series.smooth,
              otherSmooth = false;

            this.widget.configuration.series.forEach(s => {
              if (s.xAxisIndex == xAxisIndex && s.type == 'line') {
                if (!otherSmooth && s.smooth && !smooth) {
                  otherSmooth = true;
                }
                if (smooth) {
                  this.$set(s, 'smooth', this.series.smooth);
                }
              }
            });
            if (otherSmooth && !smooth) {
              this.series.smooth = true;
            }
          }
        }
        resolve();
      });
    },

    onChangeStackSelect() {
      if (this.series.id != undefined) {
        let _stackValue = undefined,
          index = 0;
        for (let i = 0, len = this.widget.configuration.series.length; i < len; i++) {
          let s = this.widget.configuration.series[i];
          if (this.stack.includes(s.id) || s.id == this.series.id) {
            if (i < index) {
              index = i;
            }
          }
        }
        _stackValue = this.widget.configuration.series[index].id;
        this.$set(this.series, 'stack', this.stack.length > 0 ? _stackValue : undefined);
        for (let i = 0, len = this.widget.configuration.series.length; i < len; i++) {
          let s = this.widget.configuration.series[i];
          if (this.stack.includes(s.id)) {
            this.$set(s, 'stack', _stackValue);
          } else if (this.lastStack.includes(this.widget.configuration.series[i].id) && this.series.id != s.id) {
            this.$set(s, 'stack', undefined);
          }
        }
        this.lastStack = JSON.parse(JSON.stringify(this.stack));
        if (this.series.id != undefined) {
          this.designer.emitEvent(`${this.widget.id}:series-stack-change`, this.series.id);
        }
      }
    }
  }
};
</script>
