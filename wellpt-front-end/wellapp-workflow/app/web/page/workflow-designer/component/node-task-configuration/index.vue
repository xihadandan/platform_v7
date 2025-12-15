<template>
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
      <a-tab-pane key="0" tab="基本设置">
        <task-property-base-info :graphItem="graphItem" :formData="formData" :rules="rules" />
      </a-tab-pane>
      <a-tab-pane key="1" tab="权限设置">
        <task-property-permission-settings :formData="formData" />
      </a-tab-pane>
      <a-tab-pane key="2" tab="表单设置" :forceRender="true">
        <task-property-form-settings :formData="formData" />
      </a-tab-pane>
      <a-tab-pane key="3" tab="流转设置">
        <task-property-parallel-gateway :formData="formData" />
      </a-tab-pane>
      <a-tab-pane key="4" tab="高级设置">
        <task-property-advanced-settings :formData="formData" />
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
import { taskRules as rules, getCustomRules } from '../designer/constant';
import TaskPropertyBaseInfo from './base-info.vue';
import TaskPropertyPermissionSettings from './permission-settings.vue';
import TaskPropertyFormSettings from './form-settings.vue';
import TaskPropertyParallelGateway from './parallel-gateway.vue';
import TaskPropertyAdvancedSettings from './advanced-settings.vue';

export default {
  name: 'NodeTaskConfiguration',
  props: {
    formData: {
      type: Object,
      default: () => {}
    },
    graphItem: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    TaskPropertyBaseInfo,
    TaskPropertyPermissionSettings,
    TaskPropertyFormSettings,
    TaskPropertyParallelGateway,
    TaskPropertyAdvancedSettings
  },
  data() {
    const rulesCustom = getCustomRules(rules);
    return {
      tabKey: '0',
      rules: rulesCustom
    };
  },
  mounted() {
    this.$emit('mounted', this);
  },
  methods: {
    validate(callback) {
      this.$refs.form.validate((valid, error) => {
        callback({ valid, error, data: this.formData });
      });
    }
  }
};
</script>
