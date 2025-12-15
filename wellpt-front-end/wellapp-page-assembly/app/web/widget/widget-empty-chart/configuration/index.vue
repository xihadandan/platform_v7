<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <EchartsVersion />
        <a-form-model-item label="名称">
          <a-input v-model="widget.title" />
        </a-form-model-item>
        <a-form-model-item label="JS模块">
          <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetChartDevelopment" />
        </a-form-model-item>

        <a-form-model-item label="图表配置">
          <a-radio-group v-model="widget.configuration.optionSourceType" button-style="solid" size="small">
            <a-radio-button value="static">静态配置</a-radio-button>
            <a-radio-button value="jsModule">JS模块方法返回</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <div v-show="widget.configuration.optionSourceType === 'static'">
          <a-form-model-item label="静态配置">
            <WidgetDesignDrawer title="静态配置" :id="'WidgetEmptyChartStaticOptionJsonDrawer' + widget.id" :designer="designer">
              <a-button size="small" type="link" icon="setting">设置</a-button>
              <template slot="content">
                <WidgetCodeEditor
                  v-model="widget.configuration.optionJson"
                  width="auto"
                  height="600px"
                  lang="json"
                  :hideError="true"
                ></WidgetCodeEditor>
              </template>
            </WidgetDesignDrawer>
          </a-form-model-item>
        </div>

        <div v-show="widget.configuration.optionSourceType === 'jsModule'">
          <a-form-model-item>
            <template slot="label">
              <a-popover placement="topRight">
                <template slot="content">通过二开方法 return 返回配置数据, 也支持通过返回 Promise 对象异步返回配置</template>
                方法
                <a-icon type="question-circle-o" />
              </a-popover>
            </template>
            <JsHookSelect :designer="designer" :widget="widget" v-model="widget.configuration.optionJsFunction" />
          </a-form-model-item>
        </div>
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
        <a-form-model-item label="高度">
          <a-input-group compact>
            <a-input-number v-model="widget.configuration.style.height" style="width: 100px" :min="1" />
            <a-button>px</a-button>
          </a-input-group>
        </a-form-model-item>
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
import { generateId } from '@framework/vue/utils/util';
import EchartsVersion from '../../widget-bar-chart/configuration/echarts-version.vue';
export default {
  name: 'WidgetEmptyChartConfiguration',
  props: { widget: Object, designer: Object },
  components: { EchartsVersion },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {},

  configuration(widget) {
    let conf = {
      jsModules: [],
      style: {
        height: 300
      },
      optionSourceType: 'static',
      optionJson: undefined,
      optionJsFunction: undefined,
      autoRefresh: false,
      refreshWaitSeconds: 3,
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
    }
    return conf;
  }
};
</script>
