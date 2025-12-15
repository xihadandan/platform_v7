<template>
  <div>
    <a-form-model>
      <a-form-model-item label="徽标风格">
        <a-radio-group v-model="widget.configuration.badgeStyle.badgeDisplayType" button-style="solid">
          <a-radio-button value="text">普通文本</a-radio-button>
          <a-radio-button value="badge">胶囊</a-radio-button>
          <a-radio-button value="dot">红点</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <div
        v-show="widget.configuration.badgeStyle.badgeDisplayType != 'dot'"
        :style="{
          lineHeight: '30px'
        }"
      >
        <a-form-model-item label="徽标封顶数字">
          <a-radio-group v-model="widget.configuration.badgeStyle.badgeOverflowCountShowType" button-style="solid">
            <a-radio-button value="systemDefault">系统默认</a-radio-button>
            <a-radio-button value="limitless">无封顶</a-radio-button>
            <a-radio-button value="customize">自定义</a-radio-button>
          </a-radio-group>
          <a-input-number
            v-model="widget.configuration.badgeStyle.badgeOverflowCount"
            :min="1"
            v-show="widget.configuration.badgeStyle.badgeOverflowCountShowType == 'customize'"
            allowClear
          />
        </a-form-model-item>
        <a-form-model-item label="徽标数值为0时">
          <a-radio-group v-model="widget.configuration.badgeStyle.badgeShowZero" button-style="solid">
            <a-radio-button value="systemDefault">系统默认</a-radio-button>
            <a-radio-button value="yes">显示</a-radio-button>
            <a-radio-button value="no">不显示</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
      </div>
      <a-form-model-item label="文本颜色" v-if="widget.configuration.badgeStyle.badgeDisplayType == 'text'">
        <a-radio-group v-model="widget.configuration.badgeStyle.colorType" button-style="solid">
          <a-radio-button value="default">默认</a-radio-button>
          <a-radio-button value="value">
            指定颜色
            <a-tooltip title="使用内置颜色变量">
              <a-checkbox v-model="fontColorUseBar" @change="widget.configuration.badgeStyle.color = undefined" />
            </a-tooltip>
          </a-radio-button>
          <a-popover>
            <template slot="content">
              <div>支持使用函数返回颜色值, 入参传入的是 params 参数对象，对象上包括数据项 item , count 参数</div>
              <div>例如: return count > 100 ? 'red' : undefined ;</div>
            </template>
            <a-radio-button value="function">运行时函数判断</a-radio-button>
          </a-popover>
        </a-radio-group>
        <template v-if="widget.configuration.badgeStyle.colorType == 'value'">
          <StyleColorTreeSelect
            v-if="fontColorUseBar"
            :colorConfig="colorConfig"
            :popupContainer="getStyleColorTreeSelectPopupContainer"
            style="width: 100%"
            v-model="widget.configuration.badgeStyle.color"
          />
          <div v-else>
            <ColorPicker
              v-model="widget.configuration.badgeStyle.color"
              :width="150"
              :allowClear="true"
              :popupContainer="colorPickerPopupContainer"
            />
          </div>
        </template>
        <WidgetCodeEditor
          v-if="widget.configuration.badgeStyle.colorType == 'function'"
          v-model="widget.configuration.badgeStyle.color"
          width="auto"
          height="100px"
          lang="js"
          :hideError="true"
        ></WidgetCodeEditor>
      </a-form-model-item>
      <a-form-model-item label="徽标位置">
        <div style="display: flex; flex-wrap: wrap; justify-content: space-between">
          <template v-for="(opt, i) in positionDist">
            <a-form-model-item
              :label="opt.label"
              :key="'postDist_' + i"
              :label-col="{ style: { width: '115px' } }"
              :wrapper-col="{ style: { width: '160px' } }"
              style="display: flex"
            >
              <a-input-group compact style="width: 100%">
                <a-input-number
                  allowClear
                  v-model="widget.configuration.badgeStyle[opt.value]"
                  :min="0"
                  :max="widget.configuration.badgeStyle[opt.value + 'Unit'] == '%' ? 100 : 3000"
                />
                <a-select v-model="widget.configuration.badgeStyle[opt.value + 'Unit']" :style="{ width: '70px' }" defaultValue="px">
                  <a-select-option value="px">px</a-select-option>
                  <a-select-option value="%">%</a-select-option>
                </a-select>
              </a-input-group>
            </a-form-model-item>
          </template>
        </div>
      </a-form-model-item>
    </a-form-model>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import StyleColorTreeSelect from '../../../page/theme-designer/component/design/lib/style-color-tree-select.vue';

export default {
  name: 'BadgeStyleConfiguration',
  props: { widget: Object, designer: Object, colorConfig: Object },
  components: { StyleColorTreeSelect },
  computed: {},
  data() {
    return {
      positionDist: [
        { label: '离左侧距离', value: 'left' },
        { label: '离上侧距离', value: 'top' },
        { label: '离右侧距离', value: 'right' },
        { label: '离下侧距离', value: 'bottom' }
      ],
      fontColorUseBar:
        this.widget.configuration.badgeStyle != undefined &&
        this.widget.configuration.badgeStyle.colorType == 'value' &&
        this.widget.configuration.badgeStyle.color != undefined &&
        this.widget.configuration.badgeStyle.color.startsWith('#')
    };
  },
  beforeCreate() {},
  created() {
    if (!this.widget.configuration.badgeStyle.hasOwnProperty('badgeDisplayType')) {
      this.$set(this.widget.configuration, 'badgeStyle', {
        color: undefined,
        colorType: 'default',
        badgeDisplayType: 'text',
        badgeOverflowCountShowType: 'limitless',
        badgeOverflowCount: undefined,
        badgeShowZero: 'no',
        left: null,
        top: null,
        right: 12,
        bottom: null,
        leftUnit: 'px',
        topUnit: 'px',
        bottomUnit: 'px',
        rightUnit: 'px'
      });
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    getStyleColorTreeSelectPopupContainer(n) {
      return document.body;
    },
    colorPickerPopupContainer() {
      return triggerNode => {
        return document.body;
      };
    }
  }
};
</script>
