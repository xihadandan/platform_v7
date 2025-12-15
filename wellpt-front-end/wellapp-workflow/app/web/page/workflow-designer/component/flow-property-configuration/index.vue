<template>
  <PerfectScrollbar ref="scroll">
    <a-form-model
      ref="form"
      :model="formData"
      :rules="rules"
      :colon="false"
      labelAlign="left"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
    >
      <a-tabs v-model="tabKey" size="small" class="flex-card-tabs flow-flex-card-tabs">
        <a-tab-pane key="0" tab="基本信息">
          <flow-property-base-info :formData="formData" :rules="rules" />
        </a-tab-pane>
        <a-tab-pane key="1" tab="流程权限">
          <flow-property-permission-settings :formData="formData" />
        </a-tab-pane>
        <a-tab-pane key="2" tab="流程计时">
          <slot name="flowTimers"></slot>
        </a-tab-pane>
        <a-tab-pane key="3" tab="高级设置">
          <flow-property-advanced-settings :formData="formData" />
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </PerfectScrollbar>
</template>

<script>
import { flowPropertyRules as rules, getCustomRules } from '../designer/constant';
import FlowPropertyBaseInfo from './base-info.vue';
import FlowPropertyPermissionSettings from './permission-settings.vue';
import FlowPropertyAdvancedSettings from './advanced-settings.vue';

export default {
  name: 'FlowPropertyConfiguration',
  inject: ['pageContext'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    FlowPropertyBaseInfo,
    FlowPropertyPermissionSettings,
    FlowPropertyAdvancedSettings
  },
  data() {
    if (!this.formData.hasOwnProperty('orgVersionId')) {
      this.$set(this.formData, 'orgVersionId', '');
    }
    const rulesCustom = getCustomRules(rules);
    return {
      tabKey: '0',
      rules: rulesCustom
    };
  },
  created() {
    this.pageContext.handleEvent('collectFlowProperty', this.saveOrUpdate);
  },
  mounted() {
    this.pageContext.emitEvent('flowPropertyMounted', this);
  },
  methods: {
    saveOrUpdate(callback) {
      this.$refs.form.validate((valid, error) => {
        callback({ valid, error, data: this.formData });
      });
    }
  }
};
</script>
