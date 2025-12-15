<template>
  <a-form-model
    :model="widget.configuration"
    labelAlign="left"
    :label-col="{ span: 7 }"
    :wrapper-col="{ style: { textAlign: 'right' } }"
    :colon="false"
  >
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model-item label="名称">
          <a-input v-model="widget.title" />
        </a-form-model-item>
        <a-form-model-item label="JS模块">
          <JsModuleSelect v-model="configuration.jsModules" dependencyFilter="WidgetStepsDevelopment" width="205px" />
        </a-form-model-item>
        <steps-data-source :options="configuration.options" :configuration="configuration" />
        <!-- <template v-if="designer.terminalType == 'pc'">
          <a-form-model-item label="作为窗口">
            <a-switch v-model="widget.configuration.asWindow" />
          </a-form-model-item>
        </template> -->
        <!-- <a-form-model-item label="类型">
          <a-radio-group v-model="configuration.type" size="small">
            <a-radio-button v-for="item in typeOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-radio-button>
          </a-radio-group>
        </a-form-model-item> -->
        <a-form-model-item label="方向">
          <a-radio-group v-model="configuration.direction" size="small">
            <a-radio-button v-for="item in directionOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="标签位置" v-show="configuration.direction === directionOptions[0]['value']">
          <a-radio-group v-model="configuration.labelPlacement" size="small">
            <a-radio-button v-for="item in labelPlacement" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="尺寸">
          <a-radio-group v-model="configuration.size" size="small">
            <a-radio-button v-for="item in sizeOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-radio-button>
          </a-radio-group>
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer" />
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
import '../css/index.less';
import { typeOptions, directionOptions, sizeOptions } from './constant';
import StepsDataSource from './steps-data-source.vue';

export default {
  name: 'WidgetStepsConfiguration',
  props: {
    widget: Object,
    designer: Object
  },
  components: {
    StepsDataSource
  },
  provide() {
    return {
      designer: this.designer,
      widget: this.widget
    };
  },
  data() {
    const { configuration } = this.widget;
    const labelPlacement = directionOptions.slice(0, 2);
    return {
      configuration,
      typeOptions,
      directionOptions,
      labelPlacement,
      sizeOptions
    };
  },
  methods: {}
};
</script>
