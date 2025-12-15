<template>
  <a-form-model
    :model="widget.configuration"
    :rules="rules"
    labelAlign="left"
    :wrapper-col="{ style: { textAlign: 'right' } }"
    :colon="false"
  >
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <FieldNameInput :widget="widget" />
        <FieldCodeInput :widget="widget" />
        <a-form-model-item label="组件类型">
          <a-select
            :options="typeOptions"
            v-model="widget.configuration.type"
            :getPopupContainer="getPopupContainerByPs()"
            :dropdownClassName="getDropdownClassName()"
          ></a-select>
        </a-form-model-item>
        <FieldDefaultValue :configuration="widget.configuration" :variables="[]" dataType="number" />
        <a-form-model-item label="默认状态" class="item-lh">
          <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState" button-style="solid">
            <a-radio-button value="edit">可编辑</a-radio-button>
            <a-radio-button value="unedit">不可编辑</a-radio-button>
            <a-radio-button value="hidden">隐藏</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="不可编辑状态" class="item-lh">
          <a-radio-group size="small" v-model="widget.configuration.uneditableDisplayState" button-style="solid">
            <a-radio-button value="label">纯文本</a-radio-button>
            <a-radio-button value="readonly">只读(显示组件样式)</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <template v-if="widget.configuration.type == 'progress'">
          <WidgetFormNumericalBarProgressConfiguration :widget="widget" :designer="designer"></WidgetFormNumericalBarProgressConfiguration>
        </template>
        <template v-else-if="widget.configuration.type == 'rate'">
          <WidgetFormNumericalBarRateConfiguration :widget="widget" :designer="designer"></WidgetFormNumericalBarRateConfiguration>
        </template>
      </a-tab-pane>
      <a-tab-pane key="2" tab="校验规则" :forceRender="true">
        <ValidateRuleConfiguration
          ref="validateRef"
          :widget="widget"
          :trigger="true"
          :required="true"
          :unique="true"
          :validatorFunction="true"
        />
      </a-tab-pane>
      <a-tab-pane key="3" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
import WidgetFormNumericalBarProgressConfiguration from './progress.vue';
import WidgetFormNumericalBarRateConfiguration from './rate.vue';
import formConfigureMixin from '../../mixin/formConfigure.mixin';
export default {
  name: 'WidgetFormNumericalBarConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  components: { WidgetFormNumericalBarProgressConfiguration, WidgetFormNumericalBarRateConfiguration },
  provide() {
    return {
      designer: this.designer,
      widget: this.widget
    };
  },
  data() {
    return {
      typeOptions: [
        { label: '进度条', value: 'progress' },
        { label: '评分条', value: 'rate' }
      ],
      rules: {
        name: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true },
        code: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true }
      }
    };
  },
  methods: {}
};
</script>
