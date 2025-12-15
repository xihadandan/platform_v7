<template>
  <div>
    <a-form-model>
      <a-form-model-item label="结构">
        <a-radio-group size="small" v-model="widget.configuration.itemStyle.displayStyle">
          <a-radio value="iconLeft_textRight">图标左、文字右</a-radio>
          <a-radio value="textLeft_IconRight">文字左、图标右</a-radio>
          <a-radio value="iconTop_textBottom">图标上、文字下（居中）</a-radio>
          <a-radio value="textTop_iconBottom">文字上居左、图标下居右</a-radio>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          圆角
          <a-tooltip title="设置四个角圆角">
            <a-checkbox v-model="borderRadiusSetFourCorner" @change="onChangeBorderRadiusSetFourCorner" />
          </a-tooltip>
        </template>
        <a-row v-if="!borderRadiusSetFourCorner">
          <a-col :span="12">
            <a-slider v-model="widget.configuration.itemStyle.borderRadius" :min="0" :max="24" />
          </a-col>
          <a-col :span="12">
            <a-input-group compact>
              <a-input-number
                v-model="widget.configuration.itemStyle.borderRadius"
                :min="0"
                :max="24"
                style="margin-left: 12px"
                allowClear
              />
              <a-button>px</a-button>
            </a-input-group>
          </a-col>
        </a-row>
        <div v-else style="display: flex; justify-content: space-between">
          <template
            v-for="(corner, i) in [
              { name: '左上角', key: 'topLeft' },
              { name: '右上角', key: 'topRight' },
              { name: '右下角', key: 'bottomRight' },
              { name: '左下角', key: 'bottomLeft' }
            ]"
          >
            <div :key="'setCorner_' + i">
              <a-tooltip :title="corner.name" placement="top">
                <a-input-number v-model="widget.configuration.itemStyle.borderRadius[i]" :min="0" :max="10" allowClear />
              </a-tooltip>
            </div>
          </template>
        </div>
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          边框颜色
          <a-tooltip title="使用内置颜色变量">
            <a-checkbox v-model="borderColorUseVar" @change="widget.configuration.itemStyle.borderColor = undefined" />
          </a-tooltip>
        </template>
        <StyleColorTreeSelect
          v-if="borderColorUseVar"
          :colorConfig="colorConfig"
          key="borderStyleColorTreeSelect"
          style="width: 100%"
          v-model="widget.configuration.itemStyle.borderColor"
        />
        <ColorPicker v-else v-model="widget.configuration.itemStyle.borderColor" :width="150" :allowClear="true" />
      </a-form-model-item>
      <a-form-model-item label="边框大小">
        <a-input-number v-model="widget.configuration.itemStyle.borderWidth" :min="0" :max="10" allowClear />
      </a-form-model-item>
      <BackgroundSet :backgroundStyle="widget.configuration.itemStyle.backgroundStyle"></BackgroundSet>
      <a-form-model-item label="内边距">
        <a-input v-model="widget.configuration.itemStyle.padding" :allowClear="true"></a-input>
      </a-form-model-item>
      <a-form-model-item label="悬停手势光标">
        <a-switch v-model="widget.configuration.itemStyle.cursorPointer"></a-switch>
      </a-form-model-item>
      <a-form-model-item label="悬停效果">
        <a-checkbox-group v-model="widget.configuration.itemStyle.hoverStyle.type" name="hoverCheckboxgroup" :options="hoverTypeOptions" />
      </a-form-model-item>
      <template v-if="widget.configuration.itemStyle.hoverStyle.type.indexOf('backgroundColor') > -1">
        <ColorSelectConfiguration
          label="悬停背景颜色"
          v-model="widget.configuration.itemStyle.hoverStyle"
          onlyValue
          colorField="backgroundColor"
          radioSize="small"
          :popupContainer="popupContainer"
        ></ColorSelectConfiguration>
      </template>
      <template v-if="widget.configuration.itemStyle.hoverStyle.type.indexOf('borderColor') > -1">
        <ColorSelectConfiguration
          label="悬停边框颜色"
          v-model="widget.configuration.itemStyle.hoverStyle"
          onlyValue
          colorField="borderColor"
          radioSize="small"
          :popupContainer="popupContainer"
        ></ColorSelectConfiguration>
      </template>
      <template v-if="widget.configuration.itemStyle.hoverStyle.type.indexOf('boxShadow') > -1">
        <a-form-model-item label="悬停阴影效果">
          <a-input v-model="widget.configuration.itemStyle.hoverStyle.boxShadow" allowClear></a-input>
        </a-form-model-item>
      </template>
    </a-form-model>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import StyleColorTreeSelect from '../../../page/theme-designer/component/design/lib/style-color-tree-select.vue';
import BackgroundSet from '../../mobile/commons/background-set.vue';
import ColorSelectConfiguration from '@pageAssembly/app/web/widget/commons/color-select-configuration.vue';

export default {
  name: 'ItemStyleConfiguration',
  props: {
    widget: Object,
    designer: Object,
    colorConfig: Object
  },
  components: { StyleColorTreeSelect, BackgroundSet, ColorSelectConfiguration },
  computed: {},
  data() {
    return {
      bgColorUseVar:
        this.widget.configuration.itemStyle.backgroundColor != undefined &&
        this.widget.configuration.itemStyle.backgroundColor.startsWith('--w-'),
      borderColorUseVar:
        this.widget.configuration.itemStyle.borderColor != undefined && this.widget.configuration.itemStyle.borderColor.startsWith('--w-'),
      borderRadiusSetFourCorner: Array.isArray(this.widget.configuration.itemStyle.borderRadius),
      hoverTypeOptions: [
        { label: '背景颜色', value: 'backgroundColor' },
        { label: '边框颜色', value: 'borderColor' },
        { label: '阴影', value: 'boxShadow' }
      ]
    };
  },
  beforeCreate() {},
  created() {
    if (!this.widget.configuration.itemStyle.hasOwnProperty('borderColor')) {
      this.$set(this.widget.configuration.itemStyle, 'borderColor', undefined);
    }
    if (!this.widget.configuration.itemStyle.hasOwnProperty('displayStyle')) {
      this.$set(this.widget.configuration.itemStyle, 'displayStyle', 'iconLeft_textRight');
    }
    if (!this.widget.configuration.itemStyle.hasOwnProperty('cursorPointer')) {
      this.$set(this.widget.configuration.itemStyle, 'cursorPointer', true);
    }
    if (!this.widget.configuration.itemStyle.hasOwnProperty('backgroundStyle')) {
      this.$set(this.widget.configuration.itemStyle, 'backgroundStyle', {
        backgroundColor: '', // 白底
        backgroundImage: undefined,
        backgroundImageInput: undefined,
        bgImageUseInput: false,
        backgroundRepeat: undefined,
        backgroundPosition: undefined
      });
    }
    if (!this.widget.configuration.itemStyle.hasOwnProperty('hoverStyle')) {
      this.$set(this.widget.configuration.itemStyle, 'hoverStyle', {
        backgroundColor: '',
        borderColor: undefined,
        boxShadow: '0px 0px 4px 1px #d9d9d9',
        type: ['boxShadow']
      });
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    onChangeUseBgColorVar() {
      this.widget.configuration.itemStyle.backgroundColor = undefined;
    },
    onChangeBorderRadiusSetFourCorner() {
      if (this.borderRadiusSetFourCorner) {
        this.$set(
          this.widget.configuration.itemStyle,
          'borderRadius',
          Array.from({ length: 4 }, () => {
            return this.widget.configuration.itemStyle.borderRadius || 0;
          })
        );
      } else {
        this.$set(this.widget.configuration.itemStyle, 'borderRadius', this.widget.configuration.itemStyle.borderRadius[0] || 0);
      }
    }
  }
};
</script>
