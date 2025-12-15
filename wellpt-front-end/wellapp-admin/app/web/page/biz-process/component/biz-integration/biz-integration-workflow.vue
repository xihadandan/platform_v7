<template>
  <a-card size="small" :bordered="false">
    <template slot="title">
      <template v-if="flowDefUuid">
        {{ flowDefinition.name }}
        <label v-if="flowDefinition.version" style="color: rgba(0, 0, 0, 0.45)">v{{ flowDefinition.version }}</label>
      </template>
      <span v-else>
        流程集成
        <span v-if="!workflowIntegration.flowDefId" style="color: red">
          (流程定义未指定！)
          <a type="link" @click="openBizProcessDesigner">前往配置</a>
        </span>
      </span>
    </template>
    <template v-if="flowDefUuid">
      <template slot="extra">
        <a-button @click="onClickDesignWorkflow" size="small" type="link">
          <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
          编辑流程
        </a-button>
      </template>
      <iframe
        :src="viewerUrl"
        id="workflowViewFrame"
        :style="{
          border: 'none',
          width: '100%',
          height: 'calc(100vh - 120px)'
        }"
      ></iframe>
    </template>
  </a-card>
</template>

<script>
export default {
  props: {
    itemDefinition: Object
  },
  inject: ['assemble'],
  data() {
    let configs = this.itemDefinition.businessIntegrationConfigs || [];
    let workflowIntegration = configs.find(item => item.type == '1');
    return {
      workflowIntegration,
      flowDefinition: {}
    };
  },
  computed: {
    flowDefUuid() {
      return this.flowDefinition.uuid;
    },
    viewerUrl() {
      let url = `/html/newWorkFlowDesignerView.html?onlyGraphics=true&id=${this.flowDefUuid}`;
      if (this.flowDefinition.xmlDefinition === false) {
        url = `/workflow-viewer/index?uuid=${this.flowDefUuid}&showHeader=false`;
      }
      return url;
    }
  },
  created() {
    if (this.workflowIntegration && this.workflowIntegration.flowDefId) {
      this.loadFlowDefinition(this.workflowIntegration.flowDefId);
    }
  },
  methods: {
    loadFlowDefinition(flowDefId) {
      $axios.get(`/proxy/api/workflow/definition/getById?flowDefId=${flowDefId}`).then(({ data: result }) => {
        if (result.data) {
          this.flowDefinition = result.data;
        }
      });
    },
    openBizProcessDesigner() {
      this.assemble.openBizProcessDesigner();
      // const _this = this;
      // let processDefUuid = _this.assemble.processDefinition.uuid;
      // let url = `/web/app/pt-mgr/mod_biz_mgr/app_biz_process_def.html?pageUuid=5064e358-b070-4edb-8a39-e2359050bae5&processDefUuid=${processDefUuid}`;
      // window.open(url);
    },
    onClickDesignWorkflow() {
      let url = `/web/app/pt-mgr/pt-wf-mgr/pt-wf-designer.html?id=${this.flowDefUuid}`;
      const newUrl = `/workflow-designer/index?uuid=${this.flowDefUuid}`;
      if (this.flowDefinition.xmlDefinition === false) {
        url = newUrl;
      }
      window.open(url, '_blank');
    }
  }
};
</script>

<style></style>
