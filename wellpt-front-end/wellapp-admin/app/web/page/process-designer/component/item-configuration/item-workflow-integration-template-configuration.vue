<template>
  <a-form-model
    class="basic-info"
    :model="formData"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 7 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
    :colon="false"
  >
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="formData.name" />
    </a-form-model-item>
    <ItemWorkflowIntegrationInfo
      :closeOpenDrawer="false"
      ref="itemWorkflowIntegrationInfo"
      :workflowIntegration="workflowIntegration"
    ></ItemWorkflowIntegrationInfo>
  </a-form-model>
</template>

<script>
import ItemWorkflowIntegrationInfo from './item-workflow-integration-info.vue';
export default {
  props: {
    templateUuid: String
  },
  components: { ItemWorkflowIntegrationInfo },
  inject: ['designer'],
  data() {
    return {
      formData: {
        name: '',
        type: '40',
        processDefUuid: this.designer.processDefinition.uuid
      },
      workflowIntegration: {
        formDataType: '1',
        milestoneConfigs: [],
        newItemConfigs: []
      },
      rules: {
        name: [{ required: true, message: '模板名称不能为空！', trigger: 'blur' }]
      }
    };
  },
  mounted() {
    if (this.templateUuid) {
      $axios.get(`/proxy/api/biz/definition/template/get/${this.templateUuid}`).then(({ data }) => {
        if (data.data && data.data.definitionJson) {
          this.formData = data.data;
          this.workflowIntegration = JSON.parse(data.data.definitionJson);
          if (this.$refs.itemWorkflowIntegrationInfo) {
            this.$nextTick(() => {
              this.$refs.itemWorkflowIntegrationInfo.refresh();
            });
          }
        }
      });
    }
  },
  methods: {
    save(successCallback) {
      this.$refs.basicForm.validate().then(valid => {
        if (valid) {
          this.formData.definitionJson = JSON.stringify(this.workflowIntegration);
          $axios.post('/proxy/api/biz/definition/template/save', this.formData).then(result => {
            successCallback.call(this, result);
          });
        }
      });
    }
  }
};
</script>

<style></style>
