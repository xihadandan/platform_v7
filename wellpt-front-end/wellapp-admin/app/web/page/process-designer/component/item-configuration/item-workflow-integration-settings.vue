<template>
  <div>
    <a-form-model-item label="工作流集成" prop="configType">
      <a-radio-group size="small" v-model="workflowIntegration.configType">
        <a-radio-button v-show="designer.processDefinition.uuid" value="1">引用模板</a-radio-button>
        <a-radio-button value="2">自定义</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <template v-if="workflowIntegration.configType == '1'">
      <a-form-model-item label="引用模板" prop="templateUuid">
        <ProcessDesignDrawer :id="drawerAddId" title="添加模板">
          <a-button type="link" :style="{ paddingLeft: '7px' }">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            添加
          </a-button>
          <template slot="content">
            <ItemWorkflowIntegrationTemplateConfiguration
              ref="workflowIntegrationTemplateAdd"
            ></ItemWorkflowIntegrationTemplateConfiguration>
          </template>
          <template slot="footer">
            <a-button size="small" type="primary" @click.stop="onAddTemplateConfirmOk">确定</a-button>
          </template>
        </ProcessDesignDrawer>
        <ProcessDesignDrawer :id="drawerEditId" title="配置模板">
          <a-button type="link" :style="{ paddingLeft: '7px' }">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            配置
          </a-button>
          <template slot="content">
            <ItemWorkflowIntegrationTemplateConfiguration
              ref="workflowIntegrationTemplateEdit"
              :templateUuid="workflowIntegration.templateUuid"
            ></ItemWorkflowIntegrationTemplateConfiguration>
          </template>
          <template slot="footer">
            <a-button size="small" type="primary" @click.stop="onEditTemplateConfirmOk">确定</a-button>
          </template>
        </ProcessDesignDrawer>
        <a-select
          v-model="workflowIntegration.templateUuid"
          show-search
          style="width: 100%"
          :filter-option="false"
          @search="handleTemplateSearch"
          @change="handleTemplateChange"
        >
          <a-select-option v-for="d in templateOptions" :key="d.id">
            {{ d.text }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
    </template>
    <ItemWorkflowIntegrationInfo ref="itemWorkflowIntegrationInfo" :workflowIntegration="workflowIntegration"></ItemWorkflowIntegrationInfo>
  </div>
</template>

<script>
import ItemWorkflowIntegrationTemplateConfiguration from './item-workflow-integration-template-configuration.vue';
import ItemWorkflowIntegrationInfo from './item-workflow-integration-info.vue';
import ProcessDesignDrawer from '../process-design-drawer.vue';
export default {
  props: {
    workflowIntegration: Object
  },
  components: { ItemWorkflowIntegrationTemplateConfiguration, ItemWorkflowIntegrationInfo, ProcessDesignDrawer },
  inject: ['designer', 'pageContext'],
  data() {
    return {
      templateOptions: [],
      drawerAddId: 'item-workflow-integration-template-add_' + new Date().getTime(),
      drawerEditId: 'item-workflow-integration-template-edit_' + new Date().getTime()
    };
  },
  mounted() {
    this.handleTemplateSearch();
    if (this.workflowIntegration.templateUuid && this.workflowIntegration.configType == '1') {
      this.handleTemplateChange(this.workflowIntegration.templateUuid);
    }
  },
  methods: {
    handleTemplateSearch(value = '') {
      let _this = this;
      _this.$axios
        .post('/common/select2/query', {
          serviceName: 'bizDefinitionTemplateFacadeService',
          searchValue: value,
          pageSize: 1000,
          pageNo: 1,
          processDefUuid: _this.designer.processDefinition.uuid,
          templateType: '40',
          selectedTemplateUuid: this.workflowIntegration.templateUuid
        })
        .then(({ data }) => {
          if (data.results) {
            _this.templateOptions = data.results;
          }
        });
    },
    handleTemplateChange(templateUuid) {
      if (templateUuid && this.workflowIntegration.configType == '1') {
        $axios.get(`/proxy/api/biz/definition/template/get/${templateUuid}`).then(({ data }) => {
          if (data.data && data.data.definitionJson) {
            this.workflowIntegration = Object.assign(this.workflowIntegration, JSON.parse(data.data.definitionJson));
            this.$nextTick(() => {
              this.$refs.itemWorkflowIntegrationInfo.refresh();
            });
          }
        });
      } else {
        this.$refs.itemWorkflowIntegrationInfo.reset();
      }
    },
    onAddTemplateConfirmOk() {
      this.$refs.workflowIntegrationTemplateAdd.save(() => {
        this.$message.success('设置成功！');
        this.handleTemplateSearch();
        this.pageContext.emitEvent('closeDrawer:' + this.drawerAddId);
      });
    },
    onEditTemplateConfirmOk() {
      this.$refs.workflowIntegrationTemplateEdit.save(() => {
        this.$message.success('设置成功！');
        this.handleTemplateSearch();
        this.handleTemplateChange(this.workflowIntegration.templateUuid);
        this.pageContext.emitEvent('closeDrawer:' + this.drawerEditId);
      });
    }
  }
};
</script>

<style></style>
