<template>
  <a-form-model
    ref="form"
    :model="widget.configuration"
    :rules="rules"
    labelAlign="left"
    :label-col="{ span: 8 }"
    :wrapper-col="{ span: 15, style: { textAlign: 'right' } }"
  >
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <FieldNameInput :widget="widget" />
        <FieldCodeInput :widget="widget" />
        <a-form-model-item label="默认值" prop="">
          <WidgetIconLibModal
            v-model="widget.configuration.defaultValue"
            :zIndex="1000"
            :onlyIconClass="widget.configuration.onlyIconClass"
            @change="onDefaultValueChange"
          ></WidgetIconLibModal>
        </a-form-model-item>
        <a-form-model-item label="仅选择图标">
          <a-switch v-model="widget.configuration.onlyIconClass" />
        </a-form-model-item>
        <a-form-model-item label="默认状态">
          <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState">
            <a-radio-button value="edit">可编辑</a-radio-button>
            <a-radio-button value="unedit">不可编辑</a-radio-button>
            <a-radio-button value="hidden">隐藏</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <!-- <a-form-model-item label="显示清空按钮">
          <a-switch v-model="widget.configuration.allowClear" />
        </a-form-model-item> -->
      </a-tab-pane>
      <a-tab-pane key="2" tab="校验规则">
        <ValidateRuleConfiguration :widget="widget"></ValidateRuleConfiguration>
      </a-tab-pane>
      <a-tab-pane key="3" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>
<style></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';

export default {
  name: 'WidgetFormSetIconConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: { WidgetIconLibModal },
  computed: {},
  filters: {},
  created() {},
  methods: {
    getPopupContainerByPs,
    onDefaultValueChange(value) {
      if (value) {
        this.widget.configuration.hasDefaultValue = true;
      } else {
        this.widget.configuration.hasDefaultValue = false;
      }
    }
  },
  mounted() {},
  updated() {}
};
</script>
