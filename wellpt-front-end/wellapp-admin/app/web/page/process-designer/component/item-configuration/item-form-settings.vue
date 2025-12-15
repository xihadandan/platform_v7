<template>
  <ProcessFormSettings ref="processFormSettings" :processDefinition="processDefinition" :formConfig="formConfig" templateType="30">
    <template slot="processFormTemplateAdd">
      <ItemFormTemplateConfiguration
        ref="processFormTemplateAdd"
        :itemDefinition="itemDefinition"
        templateType="30"
      ></ItemFormTemplateConfiguration>
    </template>
    <template slot="processFormTemplateEdit">
      <ItemFormTemplateConfiguration
        ref="processFormTemplateEdit"
        :itemDefinition="itemDefinition"
        :templateUuid="formConfig.templateUuid"
        templateType="30"
      ></ItemFormTemplateConfiguration>
    </template>
    <template slot="processFormInfo">
      <ItemFormInfo ref="processFormInfo" :itemDefinition="itemDefinition" :formConfig="formConfig">
        <!-- <template v-if="itemDefinition.itemType == '20' || itemDefinition.itemType == '30'" slot-scope="{ formBlockOptions }">
          <ItemDispenseConfiguration :itemDefinition="itemDefinition" :formBlockOptions="formBlockOptions"></ItemDispenseConfiguration>
        </template> -->
      </ItemFormInfo>
    </template>
  </ProcessFormSettings>
</template>

<script>
import ProcessFormSettings from '../process-configuration/process-form-settings.vue';
import ItemFormTemplateConfiguration from './item-form-template-configuration.vue';
// import ItemDispenseConfiguration from './item-dispense-configuration.vue';
import ItemFormInfo from './item-form-info.vue';
export default {
  props: {
    processDefinition: Object,
    itemDefinition: Object,
    formConfig: Object
  },
  inject: ['designer'],
  components: { ProcessFormSettings, ItemFormTemplateConfiguration, ItemFormInfo },
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
