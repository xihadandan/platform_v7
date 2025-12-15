<template>
  <div>
    <a-form-model>
      <a-collapse :defaultActiveKey="['axis_0', 'axis_1']" :bordered="false" expandIconPosition="right"
        style="background: #fff">
        <template v-for="(code, i) in ['x', 'y']">
          <a-collapse-panel :key="'axis_' + i" :header="code + '坐标轴'">
            <a-form-model-item label="显示">
              <a-switch v-model="axis[code].show" />
            </a-form-model-item>
            <a-form-model-item label="坐标轴名称">
              <a-input v-model="axis[code].name" width="100%" />
            </a-form-model-item>
            <a-form-model-item label="坐标轴类型">
              <a-radio-group size="small" v-model="axis[code].type" button-style="solid" @change="onAxisTypeChange(code)">
                <a-radio-button value="category">类目轴</a-radio-button>
                <a-radio-button value="value">数值轴</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="坐标轴线">
              <a-switch v-model="axis[code].axisLine.show" />
            </a-form-model-item>
            <a-form-model-item v-if="axis[code].type == 'value'">
              <template slot="label">
                <span>
                  坐标轴分割间隔
                  <a-popover placement="right">
                    <template slot="content">
                      <p>实际根据策略计算出来的刻度可能无法达到想要的效果, 可通过指定间隔值实现刻度间距调整</p>
                    </template>
                    <a-icon type="question-circle" />
                  </a-popover>
                </span>
              </template>
              <a-input-number v-model="axis[code].interval" :min="0" />
            </a-form-model-item>
            <a-form-model-item label="坐标轴线颜色" v-if="axis[code].axisLine.show">
              <ColorPicker v-model="axis[code].axisLine.lineStyle.color" width="100%"
                :popupContainer="colorPopupContainer" allowClear />
            </a-form-model-item>
            <a-form-model-item label="刻度文字颜色">
              <ColorPicker v-model="axis[code].axisLabel.color" width="100%" :popupContainer="colorPopupContainer"
                allowClear />
            </a-form-model-item>
            <a-form-model-item label="刻度文字倾斜角度">
              <a-input-number v-model="axis[code].axisLabel.rotate" width="100%" />
            </a-form-model-item>
          </a-collapse-panel>
        </template>
        <a-collapse-panel key="gridSetting" header="网格" :style="{ border: 'unset' }">
          <a-form-model-item label="显示">
            <a-switch v-model="axis.grid.show" @change="onChangeGridShow" />
          </a-form-model-item>
          <div style="display: flex; flex-wrap: wrap; justify-content: space-between">
            <template v-for="(opt, i) in positionDist">
              <a-form-model-item :label="opt.label" :key="'postDist_' + i" :label-col="{ style: { width: '115px' } }"
                :wrapper-col="{ style: { width: '160px' } }" style="display: flex">
                <a-input-group compact style="width: 100%">
                  <a-input-number v-model="axis.grid[opt.value]" :min="0" v-if="axis.grid[opt.value + 'Unit'] != 'auto'"
                    :max="axis.grid[opt.value + 'Unit'] == '%' ? 100 : 3000" />
                  <a-select v-model="axis.grid[opt.value + 'Unit']"
                    :style="{ width: axis.grid[opt.value + 'Unit'] == 'auto' ? '100%' : '70px' }" defaultValue="px">
                    <a-select-option value="px">px</a-select-option>
                    <a-select-option value="%">%</a-select-option>
                    <a-select-option value="auto"
                      v-if="opt.value == 'width' || opt.value == 'height'">自适应</a-select-option>
                  </a-select>
                </a-input-group>
              </a-form-model-item>
            </template>
          </div>
        </a-collapse-panel>
      </a-collapse>
    </a-form-model>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';

export default {
  name: 'AxisItemConfiguration',
  props: { widget: Object, designer: Object, axis: Object, axisIndex: Number },
  components: {},
  computed: {},
  data() {
    return {
      positionDist: [
        { label: '宽度', value: 'width' },
        { label: '高度', value: 'height' },
        { label: '离左侧距离', value: 'left' },
        { label: '离上侧距离', value: 'top' },
        { label: '离右侧距离', value: 'right' },
        { label: '离下侧距离', value: 'bottom' }
      ]
    };
  },
  beforeCreate() { },
  created() {
    ['x', 'y'].forEach(key => {
      if (!this.axis[key].hasOwnProperty('axisLine')) {
        this.$set(this.axis[key], 'axisLine', {
          show: key == 'x',
          lineStyle: {
            color: '#333'
          }
        });
      }
    });
  },
  beforeMount() { },
  mounted() { },
  methods: {
    colorPopupContainer() {
      return () => {
        return document.body;
      };
    },
    onChangeGridShow() { },

    onAxisTypeChange(code) {
      let key = code == 'x' ? 'y' : 'x',
        value = this.axis[code].type == 'category' ? 'value' : 'category';
      this.axis[key].type = value;
    }
  }
};
</script>
