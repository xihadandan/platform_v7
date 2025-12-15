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
        <ProcessDesignDrawer :id="drawerEditId" :title="'阶段办理单' + (isRefNode ? '(引用)' : '')">
          <a-button size="small" type="link">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            表单设置
          </a-button>
          <template slot="content">
            <div v-if="isRefNode" style="width: 100%; height: 100%; display: block; position: absolute; z-index: 1"></div>
            <NodeFormSettings
              v-if="formConfig"
              :processDefinition="assemble.getProcessDesigner().processDefinition"
              :formConfig="formConfig"
            ></NodeFormSettings>
          </template>
        </ProcessDesignDrawer>
      </template>
    </BizEntityDyform>
  </a-form-model>
</template>

<script>
import BizEntityDyform from '../biz-entity/biz-entity-dyform.vue';
import ProcessDesignDrawer from '../../../process-designer/component/process-design-drawer.vue';
import NodeFormSettings from '../../../process-designer/component/node-configuration/node-form-settings.vue';
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
  components: { BizEntityDyform, ProcessDesignDrawer, NodeFormSettings },
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
      drawerEditId: 'form_setting_' + this.entity.nodeId,
      drawerContainer: '.designer-component-container .form-container .ant-card-body'
    };
  },
  computed: {
    formConfig() {
      let designer = this.assemble.getProcessDesigner();
      let stageNode = designer.getStageNodeByNodeId(this.entity.nodeId);
      if (!stageNode) {
        return null;
      }
      let nodeData = stageNode.getData();
      if (!nodeData.configuration.formConfig) {
        this.$set(nodeData.configuration, 'formConfig', { configType: '2' });
      }
      return nodeData.configuration.formConfig;
    },
    isRefNode() {
      let designer = this.assemble.getProcessDesigner();
      let stageNode = designer.getStageNodeByNodeId(this.entity.nodeId);
      if (!stageNode) {
        return false;
      }
      return designer.isRefNode(stageNode.id);
    }
  }
};
</script>

<style></style>
