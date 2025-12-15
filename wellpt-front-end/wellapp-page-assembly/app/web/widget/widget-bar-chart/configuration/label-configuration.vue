<template>
  <div>
    <a-form-model-item label="是否显示标签">
      <a-switch v-model="labelConfig.show" />
    </a-form-model-item>
    <template v-if="labelConfig.show">
      <a-form-model-item label="标签颜色">
        <ColorPicker v-model="labelConfig.color" :width="150" allowClear />
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          <a-popover placement="left" title="说明">
            <template slot="content">
              <p>入参 params 为参数对象, 可通过 return 返回格式化后的文本, 参数对象属性如下:</p>
              <pre>
  {
      componentType: 'series', // 组件类型
      seriesType: string, // 系列类型
      seriesIndex: number, // 系列下标
      seriesName: string, // 系列名称
      name: string, // 数据名，类目名
      dataIndex: number,
      data: Object, // 传入的原始数据项
      value: number|Array|Object, //传入的数据值
      color: string // 数据图形的颜色
  } </pre
              >
            </template>
            文本格式函数
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-popover>
        </template>

        <WidgetCodeEditor lang="javascript" v-model="labelConfig.formatter" width="auto" height="100px"></WidgetCodeEditor>
      </a-form-model-item>
    </template>
    <template v-if="series.type == 'pie'">
      <a-form-model-item label="标签位置">
        <a-radio-group size="small" v-model="labelConfig.position" button-style="solid">
          <a-radio-button value="outside">外侧</a-radio-button>
          <a-radio-button value="inside">内侧</a-radio-button>
          <a-radio-button value="center">中心</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
    </template>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'LabelConfiguration',
  props: {
    widget: Object,
    designer: Object,
    series: Object
  },
  components: {},
  computed: {
    labelConfig() {
      return this.series.label;
    }
  },
  data() {
    return {
      colorType: undefined
    };
  },
  beforeCreate() {},
  created() {
    if (this.series.label.color) {
      this.colorType = this.series.label.color.startsWith('#') ? 'selectColor' : 'function';
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    colorPickerPopupContainer() {
      return triggerNode => {
        return document.body;
      };
    },
    onChangeColorType() {
      this.series.label.color = undefined;
    }
  }
};
</script>
