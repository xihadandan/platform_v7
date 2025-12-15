<template>
  <ProcessFormTemplateConfiguration
    ref="processFormTemplateConfiguration"
    :formData="formData"
    :formConfig="formConfig"
    :templateUuid="templateUuid"
    :templateType="templateType"
  >
    <template slot="processFormInfo">
      <NodeFormInfo ref="processFormInfo" :formConfig="formConfig" :closeOpenDrawer="false"></NodeFormInfo>
    </template>
  </ProcessFormTemplateConfiguration>
</template>

<script>
import ProcessFormTemplateConfiguration from '../process-configuration/process-form-template-configuration.vue';
import NodeFormInfo from './node-form-info.vue';
export default {
  name: 'NodeFormTemplateConfiguration',
  extends: ProcessFormTemplateConfiguration,
  components: { ProcessFormTemplateConfiguration, NodeFormInfo },
  mounted() {
    // 设置当前组件继承的组件内部引用
    let processFormTemplateConfiguration = this.$refs.processFormTemplateConfiguration;
    Object.assign(this.$refs, processFormTemplateConfiguration.$refs);
  }
};
</script>
