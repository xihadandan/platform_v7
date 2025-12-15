<template>
  <div>
    <a-form-model-item label="图形颜色">
      <!-- <template slot="label">
        <a-tooltip title="默认从全局调色盘获取颜色">
          图形颜色
          <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"  />
        </a-tooltip>
      </template> -->
      <a-radio-group size="small" v-model="colorType" button-style="solid" @change="onChangeColorType">
        <a-radio-button :value="undefined">默认</a-radio-button>
        <a-radio-button value="selectColor">指定颜色</a-radio-button>
        <a-popover>
          <template slot="content">
            <div>支持使用函数返回颜色值, 入参传入的是 params 参数对象，对象上包括数据项 seriesIndex, dataIndex, data, value 等各个参数</div>
          </template>
          <a-radio-button value="function">运行时函数判断</a-radio-button>
        </a-popover>
      </a-radio-group>

      <div>
        <ColorPicker
          v-if="colorType == 'selectColor'"
          v-model="series.itemStyle.color"
          :width="150"
          allowClear
          @onChangeColor="color => onChangeColor(color)"
        />
      </div>
      <WidgetCodeEditor
        v-if="colorType == 'function'"
        v-model="series.itemStyle.color"
        width="auto"
        height="100px"
        lang="js"
        :hideError="true"
      ></WidgetCodeEditor>
    </a-form-model-item>
    <template v-if="series.type == 'bar' || series.type == 'pie'">
      <a-form-model-item>
        <template slot="label">
          圆角半径
          <a-tooltip title="分别设置圆角半径">
            <a-checkbox :checked="vBorderRadiusChecked" @change="onChangeBorderRadius" />
          </a-tooltip>
        </template>
        <template v-if="vBorderRadiusChecked">
          <template v-for="(arrItem, a) in series.itemStyle.borderRadius">
            <label style="margin-right: 5px">{{ ['左上', '右上', '右下', '左下'][a] }}</label>
            <a-input-number
              v-model="series.itemStyle.borderRadius[a]"
              :min="0"
              :key="'borderRadius' + a"
              style="width: 75px; margin-right: 5px"
            />
          </template>
        </template>
        <a-input-number v-else v-model="series.itemStyle.borderRadius" :min="0" style="width: 75px" />
      </a-form-model-item>
      <div style="display: flex; flex-wrap: wrap; justify-content: space-between">
        <a-form-model-item label="边框颜色" :label-col="{ style: { width: '115px' } }" :wrapper-col="{ style: { width: '160px' } }">
          <ColorPicker :width="120" v-model="series.itemStyle.borderColor" allowClear :popupContainer="colorPickerPopupContainer" />
        </a-form-model-item>
        <a-form-model-item label="边框宽度" :label-col="{ style: { width: '70px' } }" :wrapper-col="{ style: { width: '160px' } }">
          <a-input-group compact>
            <a-input-number v-model="series.itemStyle.borderWidth" :min="0" />
            <a-button>px</a-button>
          </a-input-group>
        </a-form-model-item>
      </div>
    </template>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';

export default {
  name: 'ItemStyleConfiguration',
  props: {
    widget: Object,
    designer: Object,
    series: Object
  },
  components: { ColorPicker },
  computed: {
    vBorderRadiusChecked() {
      return Array.isArray(this.series.itemStyle.borderRadius) && this.series.itemStyle.borderRadius.length === 4;
    }
  },
  data() {
    return {
      colorType: undefined
    };
  },
  beforeCreate() {},
  created() {
    if (this.series.itemStyle.color) {
      this.colorType = this.series.itemStyle.color.startsWith('#') ? 'selectColor' : 'function';
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
    onChangeBorderRadius(e) {
      if (e.target.checked) {
        this.$set(
          this.series.itemStyle,
          'borderRadius',
          Array.from({ length: 4 }, () => {
            return this.series.itemStyle.borderRadius || 0;
          })
        );
      } else {
        this.$set(this.series.itemStyle, 'borderRadius', this.series.itemStyle.borderRadius[0] || 0);
      }
    },
    onChangeColor(clr) {
      console.log(clr);
    },
    onChangeColorType() {
      this.series.itemStyle.color = undefined;
    }
  }
};
</script>
