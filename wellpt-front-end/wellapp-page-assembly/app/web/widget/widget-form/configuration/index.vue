<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model-item label="名称">
          <a-input v-model="widget.title" />
        </a-form-model-item>
        <a-form-model-item label="JS模块">
          <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetFormDevelopment" />
        </a-form-model-item>
        <a-form-model-item label="表单数据来源设置">
          <a-select v-model="widget.configuration.formDataFrom" style="width: 100%" allow-clear>
            <a-select-option value="eventMetaData">事件元数据</a-select-option>
            <a-select-option value="jsMethod">脚本代码</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-if="widget.configuration.formDataFrom == 'eventMetaData'">
          <template slot="label">
            <a-checkbox v-model="widget.configuration.explainFormDataFromEventParams">
              解析
              <a-tag class="primary-color">formData.属性名</a-tag>
              的事件参数为表单数据
            </a-checkbox>
          </template>
        </a-form-model-item>

        <a-form-model-item label="表单数据来源设置" v-if="widget.configuration.formDataFrom == 'jsMethod'">
          <JsHookSelect :designer="designer" :widget="widget" v-model="widget.configuration.formDataFromJsFunction" />
        </a-form-model-item>
        <!-- <a-form-model-item label="表单数据">
          <a-select :options="pageVarOptions" allowClear v-model="widget.configuration.bindFormDataVar" />
        </a-form-model-item> -->
        <a-form-model-item label="标签文本对齐方式">
          <a-radio-group v-model="widget.configuration.layout" button-style="solid" size="small">
            <a-radio-button value="horizontal">水平</a-radio-button>
            <a-radio-button value="vertical">垂直</a-radio-button>
          </a-radio-group>
        </a-form-model-item>

        <a-form-model-item label="标签宽度">
          <a-input-number v-model="labelWidth" @change="onChangeLabelWidth" />
        </a-form-model-item>
        <a-form-model-item label="展示标签冒号" v-show="widget.configuration.layout == 'horizontal'">
          <a-switch v-model="widget.configuration.colon" />
        </a-form-model-item>
        <a-form-model-item label="提交方式">
          <a-radio-group v-model="widget.configuration.submitAction.submitType" button-style="solid" size="small">
            <a-radio-button value="remote">远程提交</a-radio-button>
            <a-radio-button value="local">自定义提交</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <div v-show="widget.configuration.submitAction.submitType == 'remote'">
          <a-form-model-item label="提交地址">
            <a-input v-model="widget.configuration.submitAction.url" />
          </a-form-model-item>
          <a-form-model-item label="提交显示遮罩">
            <a-switch v-model="widget.configuration.submitAction.mask" />
          </a-form-model-item>
          <a-form-model-item label="提交前">
            <WidgetDesignDrawer id="widgetFormSubmitBeforeDefineDrawer" title="提交前" :designer="designer" :zIndex="1010">
              <a-button title="自定义" type="link" icon="code"></a-button>
              <template slot="content">
                <a-form-model :label-col="{ span: 6 }" :wrapper-col="{ span: 15 }">
                  <a-form-model-item label="执行逻辑">
                    <a-radio-group v-model="widget.configuration.submitAction.beforeSubmit.type" button-style="solid">
                      <a-radio-button value="codeEditor">代码</a-radio-button>
                      <a-radio-button value="developJsFileCode">JS模块</a-radio-button>
                      <a-radio-button value="widgetEvent">触发组件事件</a-radio-button>
                    </a-radio-group>
                  </a-form-model-item>
                  <a-form-model-item label="编写代码" v-show="widget.configuration.submitAction.beforeSubmit.type === 'codeEditor'">
                    <WidgetCodeEditor v-model="widget.configuration.submitAction.beforeSubmit.customScript" width="auto" height="200px" />
                  </a-form-model-item>
                  <a-form-model-item label="脚本代码" v-show="widget.configuration.submitAction.beforeSubmit.type === 'developJsFileCode'">
                    <JsHookSelect
                      :designer="designer"
                      :widget="widget"
                      v-model="widget.configuration.submitAction.beforeSubmit.jsFunction"
                    />
                  </a-form-model-item>
                  <a-form-model-item label="选择组件" v-show="widget.configuration.submitAction.beforeSubmit.type === 'widgetEvent'">
                    <a-select
                      :style="{ width: '100%' }"
                      :options="vWidgetOptions"
                      v-model="widget.configuration.submitAction.beforeSubmit.widgetId"
                      @change="e => onChangeWidgetEvent(widget.configuration.submitAction.beforeSubmit)"
                      show-search
                      :filter-option="filterOption"
                      :getPopupContainer="getSelectPopupContainer"
                    ></a-select>
                  </a-form-model-item>
                  <a-form-model-item label="选择组件事件" v-show="widget.configuration.submitAction.beforeSubmit.type === 'widgetEvent'">
                    <a-select
                      :key="widget.configuration.submitAction.beforeSubmit.widgetId"
                      :style="{ width: '100%' }"
                      :options="getWidgetCustomEvents(widget.configuration.submitAction.beforeSubmit.widgetId)"
                      v-model="widget.configuration.submitAction.beforeSubmit.widgetEventId"
                      @change="e => onChangeWidgetEvent(widget.configuration.submitAction.beforeSubmit)"
                    ></a-select>
                  </a-form-model-item>
                </a-form-model>
              </template>
            </WidgetDesignDrawer>
          </a-form-model-item>
          <a-form-model-item label="提交后">
            <WidgetDesignDrawer id="widgetFormSubmitAfterDefineDrawer" title="提交后" :designer="designer" :zIndex="1010">
              <a-button title="自定义" type="link" icon="code"></a-button>
              <template slot="content">
                <a-form-model :label-col="{ span: 6 }" :wrapper-col="{ span: 15 }">
                  <a-form-model-item label="执行逻辑">
                    <a-radio-group v-model="widget.configuration.submitAction.afterSubmit.type" button-style="solid">
                      <a-radio-button value="codeEditor">代码</a-radio-button>
                      <a-radio-button value="developJsFileCode">JS模块</a-radio-button>
                      <a-radio-button value="widgetEvent">触发组件事件</a-radio-button>
                    </a-radio-group>
                  </a-form-model-item>
                  <a-form-model-item label="编写代码" v-show="widget.configuration.submitAction.afterSubmit.type === 'codeEditor'">
                    <WidgetCodeEditor v-model="widget.configuration.submitAction.afterSubmit.customScript" width="auto" height="200px" />
                  </a-form-model-item>
                  <a-form-model-item label="脚本代码" v-show="widget.configuration.submitAction.afterSubmit.type === 'developJsFileCode'">
                    <JsHookSelect
                      :designer="designer"
                      :widget="widget"
                      v-model="widget.configuration.submitAction.afterSubmit.jsFunction"
                    />
                  </a-form-model-item>
                  <a-form-model-item label="选择组件" v-show="widget.configuration.submitAction.afterSubmit.type === 'widgetEvent'">
                    <a-select
                      :style="{ width: '100%' }"
                      :options="vWidgetOptions"
                      v-model="widget.configuration.submitAction.afterSubmit.widgetId"
                      @change="e => onChangeWidgetEvent(widget.configuration.submitAction.afterSubmit)"
                      show-search
                      :filter-option="filterOption"
                      :getPopupContainer="getSelectPopupContainer"
                    ></a-select>
                  </a-form-model-item>
                  <a-form-model-item label="选择组件事件" v-show="widget.configuration.submitAction.afterSubmit.type === 'widgetEvent'">
                    <a-select
                      :key="widget.configuration.submitAction.afterSubmit.widgetId"
                      :style="{ width: '100%' }"
                      :options="getWidgetCustomEvents(widget.configuration.submitAction.afterSubmit.widgetId)"
                      v-model="widget.configuration.submitAction.afterSubmit.widgetEventId"
                      @change="e => onChangeWidgetEvent(widget.configuration.submitAction.afterSubmit)"
                    ></a-select>
                  </a-form-model-item>
                </a-form-model>
              </template>
            </WidgetDesignDrawer>
          </a-form-model-item>
        </div>
        <div v-show="widget.configuration.submitAction.submitType == 'local'">
          <a-form-model-item label="自定义提交">
            <!-- 自定义代码 -->
            <WidgetDesignDrawer id="widgetFormSubmitDefineDrawer" title="自定义提交" :designer="designer" :zIndex="1010">
              <a-button title="自定义" type="link" icon="code"></a-button>
              <template slot="content">
                <a-form-model :label-col="{ span: 6 }" :wrapper-col="{ span: 15 }">
                  <a-form-model-item label="事件代码来源">
                    <a-radio-group v-model="widget.configuration.submitAction.codeSource" button-style="solid">
                      <a-radio-button value="codeEditor">自定义代码</a-radio-button>
                      <a-radio-button value="developJsFileCode">JS模块</a-radio-button>
                    </a-radio-group>
                  </a-form-model-item>
                  <a-form-model-item label="编写代码" v-show="widget.configuration.submitAction.codeSource === 'codeEditor'">
                    <WidgetCodeEditor v-model="widget.configuration.submitAction.customScript" width="auto" height="200px" />
                  </a-form-model-item>
                  <a-form-model-item label="脚本代码" v-show="widget.configuration.submitAction.codeSource === 'developJsFileCode'">
                    <JsHookSelect :designer="designer" :widget="widget" v-model="widget.configuration.submitAction.jsFunction" />
                  </a-form-model-item>
                </a-form-model>
              </template>
            </WidgetDesignDrawer>
          </a-form-model-item>
        </div>

        <!-- <StyleConfiguration :widget="widget" :setWidthHeight="false" :editBlock="false" /> -->
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>
<style></style>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';

export default {
  name: 'WidgetFormConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },

  data() {
    return {
      labelWidth: this.widget.configuration.labelCol.style.flex
        ? parseInt(this.widget.configuration.labelCol.style.flex.split(' ')[2])
        : 100
    };
  },
  computed: {
    vWidgetOptions() {
      let options = [];
      if (this.designer) {
        for (let wid in this.designer.widgetIdMap) {
          let w = this.designer.widgetIdMap[wid];
          options.push({ label: w.title, value: w.id });
        }
      }
      return options;
    },

    pageVarOptions() {
      // 页面变量路径
      let paths = this.designer.pageVarKeyPaths(),
        options = [];
      for (let i = 0, len = paths.length; i < len; i++) {
        options.push({ label: paths[i], value: paths[i] });
      }
      return options;
    }
  },
  beforeCreate() {},
  components: {},
  created() {
    // 新配置参数设置
    if (this.widget.configuration.submitAction.afterSubmit == undefined) {
      this.$set(this.widget.configuration.submitAction, 'afterSubmit', {
        type: 'codeEditor',
        customScript: undefined,
        widgetId: undefined,
        widgetEventId: undefined,
        jsFunction: undefined,
        eventParams: []
      });
      if (this.widget.configuration.submitAction.after != undefined) {
        this.widget.configuration.submitAction.afterSubmit.customScript = this.widget.configuration.submitAction.after;
        delete this.widget.configuration.submitAction.after;
      }
    }

    if (this.widget.configuration.submitAction.beforeSubmit == undefined) {
      this.$set(this.widget.configuration.submitAction, 'beforeSubmit', {
        type: 'codeEditor',
        customScript: undefined,
        widgetId: undefined,
        widgetEventId: undefined,
        jsFunction: undefined,
        eventParams: []
      });

      if (this.widget.configuration.submitAction.before != undefined) {
        this.widget.configuration.submitAction.beforeSubmit.customScript = this.widget.configuration.submitAction.before;
        delete this.widget.configuration.submitAction.before;
      }
    }
  },
  methods: {
    getWidgetCustomEvents(wid) {
      let options = [];
      if (this.designer) {
        let w = this.designer.widgetIdMap[wid];
        if (w) {
          if (w.configuration.defineEvents) {
            for (let i = 0, len = w.configuration.defineEvents.length; i < len; i++) {
              if (!['created', 'beforeMount', 'mounted'].includes(w.configuration.defineEvents[i].id)) {
                options.push({ label: w.configuration.defineEvents[i].title, value: w.configuration.defineEvents[i].id });
              }
            }
          }
          let defaultEvents = this.designer.widgetDefaultEvents[w.id];
          if (defaultEvents) {
            for (let i = 0, len = defaultEvents.length; i < len; i++) {
              options.push({ label: defaultEvents[i].title, value: defaultEvents[i].id });
            }
          }
        }
      }
      return options;
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    onChangeLabelWidth() {
      this.widget.configuration.labelCol.style.flex = `0 0 ${this.labelWidth}px`;
    },
    onChangeWidgetEvent(widgetEventConfig) {
      this.getWidgetEventParams(widgetEventConfig);
    },
    getWidgetEventParams(widgetEventConfig) {
      if (widgetEventConfig.widgetId && this.designer) {
        let w = this.designer.widgetIdMap[widgetEventConfig.widgetId];
        let modelParams = widgetEventConfig.eventParams,
          eventParams = [],
          params = {};

        if (w) {
          let defaultEvents = this.designer.widgetDefaultEvents[w.id];
          if (defaultEvents) {
            for (let i = 0, len = defaultEvents.length; i < len; i++) {
              if (defaultEvents[i].id === widgetEventConfig.widgetEventId && defaultEvents[i].eventParams != undefined)
                for (let e = 0, elen = defaultEvents[i].eventParams.length; e < elen; e++) {
                  let p = { id: generateId(), ...defaultEvents[i].eventParams[e] };
                  eventParams.push(p);
                  params[p.paramKey] = p;
                }
            }
          }
          if (w.configuration.defineEvents) {
            for (let i = 0, len = w.configuration.defineEvents.length; i < len; i++) {
              if (
                w.configuration.defineEvents[i].id === widgetEventConfig.widgetEventId &&
                w.configuration.defineEvents[i].eventParams != undefined
              )
                for (let e = 0, elen = w.configuration.defineEvents[i].eventParams.length; e < elen; e++) {
                  let p = { id: generateId(), ...w.configuration.defineEvents[i].eventParams[e] };
                  eventParams.push(p);
                  params[p.paramKey] = p;
                }
            }
          }
        }

        if (modelParams && modelParams.length) {
          for (let i = 0, len = modelParams.length; i < len; i++) {
            let p = modelParams[i];
            if (params[p.paramKey]) {
              params[p.paramKey].paramValue = p.paramValue;
            } else {
              eventParams.push(p);
            }
          }
        }
        widgetEventConfig.eventParams = eventParams;
      }
    }
  },
  beforeMount() {
    if (this.widget.configuration.submitAction.after) {
    }
  },
  mounted() {},
  configuration() {
    return {
      layout: 'horizontal',
      colon: true,
      bindFormDataVar: undefined,
      labelCol: { style: { flex: '0 0 100px' } },
      wrapperCol: { style: { flex: 'auto' } },
      style: {
        margin: [0, 0, 0, 0],
        padding: [10, 10, 10, 10]
      },
      widgets: [],
      submitAction: {
        submitType: 'remote',
        /** 提交动作 */
        mask: true, // 是否显示遮罩
        url: undefined, // 提交地址
        before: undefined, // 提交前
        after: undefined, // 提交后
        afterSubmit: {
          type: 'codeEditor',
          customScript: undefined,
          widgetId: undefined,
          widgetEventId: undefined,
          jsFunction: undefined,
          eventParams: []
        },
        beforeSubmit: {
          type: 'codeEditor',
          customScript: undefined,
          widgetId: undefined,
          widgetEventId: undefined,
          jsFunction: undefined,
          eventParams: []
        },
        method: 'POST',
        codeSource: undefined,
        customScript: undefined,
        jsFunction: undefined
      }
    };
  }
};
</script>
