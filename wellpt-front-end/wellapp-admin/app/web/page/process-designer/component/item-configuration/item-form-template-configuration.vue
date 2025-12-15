<template>
  <ProcessFormTemplateConfiguration
    ref="processFormTemplateConfiguration"
    :formData="formData"
    :formConfig="formConfig"
    :templateUuid="templateUuid"
    :templateType="templateType"
  >
    <template slot="processFormInfo">
      <ItemFormInfo ref="processFormInfo" :itemDefinition="itemDefinition" :formConfig="formConfig"></ItemFormInfo>
    </template>
  </ProcessFormTemplateConfiguration>
</template>

<script>
import ProcessFormTemplateConfiguration from '../process-configuration/process-form-template-configuration.vue';
import ItemFormInfo from './item-form-info.vue';
export default {
  name: 'ItemFormTemplateConfiguration',
  extends: ProcessFormTemplateConfiguration,
  components: { ProcessFormTemplateConfiguration, ItemFormInfo },
  props: {
    itemDefinition: Object
  },
  mounted() {
    // 设置当前组件继承的组件内部引用
    let processFormTemplateConfiguration = this.$refs.processFormTemplateConfiguration;
    Object.assign(this.$refs, processFormTemplateConfiguration.$refs);
  }
};
</script>
