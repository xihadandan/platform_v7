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
        <a-form-model-item label="仅选择图标">
          <a-switch v-model="widget.configuration.onlyIconClass" />
        </a-form-model-item>
        <a-form-model-item label="默认状态">
          <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState" button-style="solid">
            <a-radio-button value="edit">可编辑</a-radio-button>
            <a-radio-button value="unedit">不可编辑</a-radio-button>
            <!-- <a-radio-button value="hidden">隐藏</a-radio-button> -->
          </a-radio-group>
        </a-form-model-item>
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
  name: 'WidgetSetIconConfiguration',
  mixins: [editFormElementConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: { ValidateRuleConfiguration },

  created() {},
  methods: {},
  beforeMount() {},
  mounted() {},
  configuration() {
    return {
      title: '选择图标',
      code: '',
      onlyIconClass: false,
      defaultDisplayState: 'edit',
      uneditableDisplayState: 'label'
    };
  }
};
</script>
