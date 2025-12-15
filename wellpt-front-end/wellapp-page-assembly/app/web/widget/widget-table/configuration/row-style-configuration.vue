<template>
  <a-form-model>
    <a-form-model-item label="行背景样式类型">
      <a-radio-group v-model="widget.configuration.rowStyle.bgStyleType" button-style="solid">
        <a-radio-button value="default">默认样式</a-radio-button>
        <a-radio-button value="stripeStyle">交替行条纹</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <div v-show="widget.configuration.rowStyle.bgStyleType == 'stripeStyle'">
      <a-form-model-item label="条纹颜色">
        <a-radio-group
          v-model="widget.configuration.rowStyle.backgroundColorType"
          @change="widget.configuration.rowStyle.backgroundColor = undefined"
          button-style="solid"
        >
          <a-radio-button value="value">
            指定颜色
            <a-tooltip title="使用内置颜色变量">
              <a-checkbox size="small" v-model="bgColorUseVar" @change="widget.configuration.rowStyle.backgroundColor = undefined" />
            </a-tooltip>
          </a-radio-button>
          <a-popover>
            <template slot="content">
              <div>支持使用函数返回颜色值, 入参传入的是 row , index 参数</div>
              <div>例如: return row.NAME == 'ABC' ? 'gray' : undefined ;</div>
            </template>
            <a-radio-button value="function">运行时函数判断</a-radio-button>
          </a-popover>
        </a-radio-group>
        <template v-if="widget.configuration.rowStyle.backgroundColorType == 'value'">
          <StyleColorTreeSelect
            v-if="bgColorUseVar"
            :colorConfig="designer.themeSpecifyColorConfig"
            style="width: 100%"
            v-model="widget.configuration.rowStyle.backgroundColor"
          />
          <div v-else>
            <ColorPicker v-model="widget.configuration.rowStyle.backgroundColor" :width="150" :allowClear="true" />
          </div>
        </template>
        <WidgetCodeEditor
          v-if="widget.configuration.rowStyle.backgroundColorType == 'function'"
          v-model="widget.configuration.rowStyle.backgroundColor"
          width="auto"
          height="100px"
          lang="js"
          :hideError="true"
        ></WidgetCodeEditor>
      </a-form-model-item>
    </div>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import StyleColorTreeSelect from '../../../page/theme-designer/component/design/lib/style-color-tree-select.vue';

export default {
  name: 'RowStyleConfiguration',
  props: {
    widget: Object,
    designer: Object
  },
  components: { StyleColorTreeSelect },
  computed: {},
  data() {
    return {
      bgColorUseVar:
        this.widget.configuration.rowStyle.backgroundColor != undefined &&
        this.widget.configuration.rowStyle.backgroundColor.startsWith('--w-')
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
