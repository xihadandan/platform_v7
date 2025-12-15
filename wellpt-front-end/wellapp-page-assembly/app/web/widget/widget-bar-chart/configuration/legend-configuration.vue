<template>
  <div>
    <a-form-model-item label="显示">
      <a-switch v-model="legend.show" />
    </a-form-model-item>
    <a-form-model-item label="图例选择模式">
      <a-radio-group size="small" v-model="legend.selectedMode" button-style="solid">
        <a-radio-button value="false">不可选</a-radio-button>
        <a-radio-button value="single">单选</a-radio-button>
        <a-radio-button value="multiple">多选</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <div v-show="legend.show">
      <a-form-model-item label="图例类型">
        <a-input-group compact>
          <a-select v-model="legend.orient" style="width: 80px">
            <a-select-option value="horizontal">水平</a-select-option>
            <a-select-option value="vertical">垂直</a-select-option>
          </a-select>
          <a-select v-model="legend.type" style="width: 120px">
            <a-select-option value="plain">普通图例</a-select-option>
            <a-select-option value="scroll">滚动图例</a-select-option>
          </a-select>
        </a-input-group>
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          <a-tooltip title="图例标记和文本的对齐">
            对齐方式
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-tooltip>
        </template>
        <a-radio-group size="small" v-model="legend.align" button-style="solid">
          <a-tooltip title="根据组件的布局决定自适应对齐">
            <a-radio-button value="auto">自动对齐</a-radio-button>
          </a-tooltip>
          <a-radio-button value="left">左对齐</a-radio-button>
          <a-radio-button value="right">右对齐</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="图例文本样式">
        <WidgetDesignDrawer :id="'chartLegendTextConfig' + widget.id" title="图例文本样式" :designer="designer">
          <a-button size="small" type="link">设置</a-button>
          <template slot="content">
            <TextStyleConfiguration :textStyle="legend.textStyle" :widget="widget" :designer="designer" />
          </template>
        </WidgetDesignDrawer>
      </a-form-model-item>
      <template v-for="(opt, i) in positionDist">
        <a-form-model-item :label="opt.label" :key="'postDist_' + i">
          <a-input-group compact style="width: 100%">
            <a-input-number
              v-model="legend[opt.value]"
              :min="0"
              v-if="legend[opt.value + 'Unit'] != 'auto'"
              :max="legend[opt.value + 'Unit'] == '%' ? 100 : 3000"
            />
            <a-select
              v-model="legend[opt.value + 'Unit']"
              :style="{ width: legend[opt.value + 'Unit'] == 'auto' ? '100%' : '70px' }"
              defaultValue="px"
            >
              <a-select-option value="px">px</a-select-option>
              <a-select-option value="%">%</a-select-option>
              <a-select-option value="auto">自适应</a-select-option>
            </a-select>
          </a-input-group>
        </a-form-model-item>
      </template>

      <a-form-model-item label="图例间距">
        <a-input-number :min="0" v-model="legend.itemGap" />
      </a-form-model-item>

      <a-form-model-item label="图例宽度">
        <a-input-number :min="0" v-model="legend.itemWidth" />
      </a-form-model-item>

      <a-form-model-item label="图例高度">
        <a-input-number :min="0" v-model="legend.itemHeight" />
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          <a-popover placement="left" title="说明">
            <template slot="content">
              <p>入参 name 为图例名称, 可通过 return 返回格式化后的文本, 例如: return '图例 '+ name;</p>
            </template>
            图例文本格式化函数
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-popover>
        </template>

        <WidgetCodeEditor lang="javascript" v-model="legend.formatter">
          <a-button icon="code" type="link" size="small">编写代码</a-button>
        </WidgetCodeEditor>
      </a-form-model-item>
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import TextStyleConfiguration from './text-style-configuration.vue';
export default {
  name: 'LegendConfiguration',
  props: { widget: Object, designer: Object, legend: Object },
  components: { TextStyleConfiguration },
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
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
