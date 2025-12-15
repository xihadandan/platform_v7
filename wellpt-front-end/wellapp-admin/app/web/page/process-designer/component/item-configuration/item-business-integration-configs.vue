<template>
  <div>
    <a-form-model-item label="事项流转/办理方式" prop="workflowIntegrationType" :label-col="{ span: 9 }">
      <a-radio-group v-model="integrationType" style="text-align: left; padding-left: 20px" @change="onChangeIntegrationType">
        <a-radio value="0">内置流转系统</a-radio>
        <a-radio value="1">集成工作流系统</a-radio>
      </a-radio-group>
      <!-- <a-checkbox v-model="workflowIntegration.enabled">集成工作流系统</a-checkbox> -->
    </a-form-model-item>
    <keep-alive>
      <template v-if="workflowIntegration.enabled && workflowIntegration.type == '1'">
        <ItemWorkflowIntegrationSettings :workflowIntegration="workflowIntegration"></ItemWorkflowIntegrationSettings>
      </template>
    </keep-alive>
  </div>
</template>

<script>
import ItemWorkflowIntegrationSettings from './item-workflow-integration-settings.vue';
export default {
  props: {
    processDefinition: Object,
    itemDefinition: Object
  },
  provide() {
    return { itemDefinition: this.itemDefinition };
  },
  components: { ItemWorkflowIntegrationSettings },
  data() {
    if (this.itemDefinition.businessIntegrationConfigs == null) {
      this.itemDefinition.businessIntegrationConfigs = [];
    }
    let businessIntegrationConfigs = this.itemDefinition.businessIntegrationConfigs;
    let workflowIntegration = {
      enabled: false,
      type: '1',
      configType: '2',
      formDataType: '1',
      milestoneConfigs: [],
      eventPublishConfigs: [],
      newItemConfigs: [],
      states: []
    };
    businessIntegrationConfigs.forEach(config => {
      if (config.type == '1') {
        workflowIntegration = config;
        // 旧数据兼容处理
        // if (!config.hasOwnProperty('enabled')) {
        // this.$set(workflowIntegration, 'enabled', true);
        // }
        if (!config.hasOwnProperty('states')) {
          this.$set(workflowIntegration, 'states', []);
        }
        if (!config.hasOwnProperty('eventPublishConfigs')) {
          this.$set(workflowIntegration, 'eventPublishConfigs', []);
        }
      }
    });

    if (businessIntegrationConfigs.length) {
      businessIntegrationConfigs.length = 1;
      workflowIntegration = businessIntegrationConfigs[0];
    } else {
      businessIntegrationConfigs.push(workflowIntegration);
    }
    return {
      integrationType: workflowIntegration.enabled ? '1' : '0',
      businessIntegrationConfigs,
      workflowIntegration
    };
  },
  methods: {
    onChangeIntegrationType() {
      const _this = this;
      if (_this.integrationType == '1') {
        _this.workflowIntegration.enabled = true;
        _this.workflowIntegration.type = '1';
      } else {
        _this.workflowIntegration.enabled = false;
        _this.workflowIntegration.type = '1';
      }
    }
  }
};
</script>

<style></style>
