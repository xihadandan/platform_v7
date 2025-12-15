<template>
  <a-form-model
    class="basic-info"
    labelAlign="left"
    ref="basicForm"
    :label-col="{ span: 8 }"
    :wrapper-col="{ span: 16, style: { textAlign: 'right' } }"
    :colon="false"
  >
    <BizEntityDyform ref="form" :entity="entity" :defaultTitle="defaultTitle" :formStateFunction="formStateFunction">
      <template slot="extra">
        <ProcessDesignDrawer :id="drawerEditId" title="业务流程办理单">
          <a-button size="small" type="link">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            表单设置
          </a-button>
          <template slot="content">
            <ProcessFormSettings
              v-if="processDefinition"
              :processDefinition="processDefinition"
              :formConfig="processDefinition.formConfig"
            ></ProcessFormSettings>
          </template>
        </ProcessDesignDrawer>
      </template>
    </BizEntityDyform>
  </a-form-model>
</template>

<script>
import BizEntityDyform from '../biz-entity/biz-entity-dyform.vue';
import ProcessDesignDrawer from '../../../process-designer/component/process-design-drawer.vue';
import ProcessFormSettings from '../../../process-designer/component/process-configuration/process-form-settings.vue';
import { filterSelectOption, getCacheData } from '../../../process-designer/designer/utils';
export default {
  props: {
    entity: Object,
    defaultTitle: {
      type: String,
      default: '阶段办理单'
    },
    formStateFunction: Function
  },
  components: { BizEntityDyform, ProcessDesignDrawer, ProcessFormSettings },
  inject: ['assemble'],
  provide() {
    return {
      designer: this.assemble.getProcessDesigner(),
      filterSelectOption,
      getCacheData,
      drawerContainer: this.drawerContainer
    };
  },
  data() {
    return {
      drawerEditId: 'form_setting_' + this.entity.formUuid,
      drawerContainer: '.designer-component-container .form-container .ant-card-body'
    };
  },
  computed: {
    processDefinition() {
      let designer = this.assemble.getProcessDesigner();
      let definitionJson = designer.processDefinition;
      if (!definitionJson) {
        return null;
      }
      if (!definitionJson.formConfig) {
        this.$set(definitionJson, 'formConfig', { configType: '2' });
      }
      return definitionJson;
    }
  }
};
</script>

<style></style>
