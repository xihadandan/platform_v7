<template>
  <a-card size="small" title="业务集成" :bordered="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="流程集成">
        <a-menu @click="onItemMenuClick">
          <a-menu-item v-for="item in workflowIntegrationItems" :key="item.id">
            {{ item.itemName }}
          </a-menu-item>
        </a-menu>
      </a-tab-pane>
    </a-tabs>
  </a-card>
</template>

<script>
import BizIntegrationWorkflow from './biz-integration-workflow.vue';
export default {
  inject: ['assemble'],
  computed: {
    workflowIntegrationItems() {
      return this.assemble.getAllItems().filter(item => {
        let businessIntegrationConfigs = item.businessIntegrationConfigs || [];
        for (let index = 0; index < businessIntegrationConfigs.length; index++) {
          let businessIntegrationConfig = businessIntegrationConfigs[index];
          if (businessIntegrationConfig.enabled && businessIntegrationConfig.type == '1') {
            return true;
          }
        }
        return false;
      });
    }
  },
  methods: {
    onItemMenuClick({ key }) {
      let itemDefinition = this.workflowIntegrationItems.find(item => item.id == key);
      this.assemble.showContent({
        component: BizIntegrationWorkflow,
        metadata: { itemDefinition }
      });
    }
  }
};
</script>

<style></style>
