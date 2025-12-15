<template>
  <ProcessFormSettings ref="processFormSettings" :processDefinition="processDefinition" :formConfig="formConfig" templateType="20">
    <template slot="processFormTemplateAdd">
      <NodeFormTemplateConfiguration ref="processFormTemplateAdd" templateType="20"></NodeFormTemplateConfiguration>
    </template>
    <template slot="processFormTemplateEdit">
      <NodeFormTemplateConfiguration
        ref="processFormTemplateEdit"
        :templateUuid="formConfig.templateUuid"
        templateType="20"
      ></NodeFormTemplateConfiguration>
    </template>
    <template slot="processFormInfo">
      <NodeFormInfo ref="processFormInfo" :formConfig="formConfig"></NodeFormInfo>
    </template>
  </ProcessFormSettings>
</template>

<script>
import ProcessFormSettings from '../process-configuration/process-form-settings.vue';
import NodeFormTemplateConfiguration from './node-form-template-configuration.vue';
import NodeFormInfo from './node-form-info.vue';
export default {
  props: {
    processDefinition: Object,
    formConfig: Object
  },
  inject: ['designer'],
  components: { ProcessFormSettings, NodeFormTemplateConfiguration, NodeFormInfo },
  mounted() {
    const _self = this;
    let processFormSettings = _self.$refs.processFormSettings;
    // 设置引用的ProcessFormSettings组件内部引用
    processFormSettings.$refs.processFormInfo = _self.$refs.processFormInfo;
  },
  watch: {
    'designer.drawerVisibleKey': function () {
      const _self = this;
      setTimeout(() => {
        let processFormSettings = _self.$refs.processFormSettings;
        // 设置引用的ProcessFormSettings组件内部引用
        if (_self.$refs.processFormTemplateAdd) {
          processFormSettings.$refs.processFormTemplateAdd = _self.$refs.processFormTemplateAdd;
        }
        if (_self.$refs.processFormTemplateEdit) {
          processFormSettings.$refs.processFormTemplateEdit = _self.$refs.processFormTemplateEdit;
        }
      }, 500);
    }
  }
};
</script>

<style></style>
