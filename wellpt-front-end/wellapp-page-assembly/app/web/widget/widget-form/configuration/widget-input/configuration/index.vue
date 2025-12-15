<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
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
        <a-form-model-item label="最大长度">
          <a-input-number v-model="widget.configuration.maxLength" :min="1" />
        </a-form-model-item>
        <!-- <a-form-model-item label="国际化">
          <a-switch v-model="widget.configuration.i18nVisible" />
        </a-form-model-item>
        <a-form-model-item label="国际化编码" v-show="widget.configuration.i18nVisible">
          <a-select :options="formVarOptions" v-model="widget.configuration.i18nCode" />
        </a-form-model-item> -->
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
        <a-form-model-item label="类型" prop="type">
          <a-select v-model="widget.configuration.type" :options="typeOptions" :style="{ width: '100%' }"></a-select>
        </a-form-model-item>
        <a-form-model-item label="格式化">
          <a-select
            :options="[
              { label: '大写', value: 'toUpperCase' },
              { label: '小写', value: 'toLowerCase' }
            ]"
            style="width: 100%"
            v-model="widget.configuration.formatType"
            allow-clear
          />
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
      <a-tab-pane key="2" tab="校验规则" :forceRender="true">
        <ValidateRuleConfiguration
          ref="validateRef"
          :widget="widget"
          :trigger="true"
          :required="true"
          :unique="true"
          :regExp="true"
          :validatorFunction="true"
        ></ValidateRuleConfiguration>
      </a-tab-pane>
      <a-tab-pane key="3" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>
<style></style>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import ValidateRuleConfiguration from '@dyformWidget/commons/validate-rule-configuration.vue';
import editFormElementConfigureMixin from '../../editFormElementConfigure.mixin';

export default {
  name: 'WidgetInputConfiguration',
  mixins: [editFormElementConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      typeOptions: [
        { label: '输入框', value: 'input' },
        { label: '文本域', value: 'textarea' }
      ]
    };
  },

  beforeCreate() {},
  components: { ValidateRuleConfiguration },

  created() {},
  methods: {},
  beforeMount() {},
  mounted() {},
  configuration() {
    return {
      title: '单行输入',
      type: 'input',
      code: '',
      formatType: undefined,
      defaultDisplayState: 'edit',
      uneditableDisplayState: 'label'
    };
  }
};
</script>
