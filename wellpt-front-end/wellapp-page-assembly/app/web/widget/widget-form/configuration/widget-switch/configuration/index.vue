<template>
  <div>
    <a-form-model ref="form" :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }">
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <a-form-model-item label="名称">
            <a-input v-model="widget.configuration.title" @change="widget.title = widget.configuration.title">
              <template slot="addonAfter">
                <a-switch :checked="widget.configuration.titleHidden !== true" @change="onChangeTitleHidden" title="显示名称"></a-switch>
                <WI18nInput
                  v-show="widget.configuration.titleHidden !== true"
                  :widget="widget"
                  :designer="designer"
                  :code="widget.id"
                  v-model="widget.configuration.title"
                />
              </template>
            </a-input>
          </a-form-model-item>
          <a-form-model-item label="编码">
            <a-input v-model="widget.configuration.code" />
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              文字提示
              <a-checkbox v-model="widget.configuration.enableTooltip" />
            </template>
            <template v-if="widget.configuration.enableTooltip">
              <a-radio-group size="small" v-model="widget.configuration.tooltipDisplayType" button-style="solid">
                <a-radio-button value="popover">气泡卡片</a-radio-button>
                <a-radio-button value="tooltip">气泡浮层</a-radio-button>
              </a-radio-group>
              <a-input placeholder="文字提示内容" style="width: 100%; float: right" v-model="widget.configuration.tooltip" allow-clear>
                <template slot="addonAfter">
                  <WI18nInput :widget="widget" :designer="designer" :code="widget.id + '_tooltip'" v-model="widget.configuration.tooltip" />
                </template>
              </a-input>
            </template>
          </a-form-model-item>

          <a-form-model-item label="开关风格">
            <a-radio-group size="small" v-model="widget.configuration.switchStyle">
              <a-radio-button v-for="item in styleOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="开启文字" v-show="widget.configuration.switchStyle == 'label'">
            <a-input v-model="widget.configuration.checkedLabel">
              <template slot="addonAfter">
                <WI18nInput :widget="widget" :designer="designer" code="checkedLabel" v-model="widget.configuration.checkedLabel" />
              </template>
            </a-input>
          </a-form-model-item>
          <a-form-model-item label="关闭文字" v-show="widget.configuration.switchStyle == 'label'">
            <a-input v-model="widget.configuration.uncheckedLabel">
              <template slot="addonAfter">
                <WI18nInput :widget="widget" :designer="designer" code="uncheckedLabel" v-model="widget.configuration.uncheckedLabel" />
              </template>
            </a-input>
          </a-form-model-item>
          <a-form-model-item label="开启真实值">
            <a-input v-model="widget.configuration.checkedValue" />
          </a-form-model-item>
          <a-form-model-item label="关闭真实值">
            <a-input v-model="widget.configuration.uncheckedValue" />
          </a-form-model-item>

          <a-form-model-item label="默认状态">
            <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState" button-style="solid">
              <a-radio-button value="edit">可编辑</a-radio-button>
              <a-radio-button value="unedit">不可编辑</a-radio-button>
              <!-- <a-radio-button value="hidden">隐藏</a-radio-button> -->
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="不可编辑状态" class="item-lh" v-if="widget.configuration.defaultDisplayState == 'unedit'">
            <a-radio-group size="small" v-model="widget.configuration.uneditableDisplayState" button-style="solid">
              <a-radio-button value="label">纯文本</a-radio-button>
              <a-radio-button value="readonly">只读(显示组件样式)</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <DefaultVisibleConfiguration compact :designer="designer" :configuration="widget.configuration" :widget="widget">
            <template slot="extraAutoCompleteSelectGroup">
              <a-select-opt-group>
                <span slot="label">
                  <a-icon type="code" />
                  表单数据
                </span>
                <a-select-option v-for="opt in formVarOptions" :key="opt.value" :title="opt.label">{{ opt.label }}</a-select-option>
              </a-select-opt-group>
            </template>
          </DefaultVisibleConfiguration>
        </a-tab-pane>

        <a-tab-pane key="3" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import editFormElementConfigureMixin from '../../editFormElementConfigure.mixin';

export default {
  name: 'WidgetSwitchConfiguration',
  mixins: [editFormElementConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },

  data() {
    return {
      styleOptions: [
        { label: '默认', value: '' },
        { label: '显示文字', value: 'label' }
        // { label: '显示图标', value: 'icon' }
      ]
    };
  },
  filters: {},
  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {},
  mounted() {},
  updated() {},
  configuration() {
    return {
      title: '开关',
      code: '',
      checkedValue: 'true',
      uncheckedValue: 'false',
      checkedLabel: '',
      uncheckedLabel: '',
      switchStyle: '',
      defaultDisplayState: 'edit',
      uneditableDisplayState: 'label'
    };
  }
};
</script>
