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
        <ProcessDesignDrawer :id="drawerEditId" :title="'事项办理单' + (isRefNode ? '(引用)' : '')">
          <a-button size="small" type="link">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            表单设置
          </a-button>
          <template slot="content">
            <div v-if="isRefNode" style="width: 100%; height: 100%; display: block; position: absolute; z-index: 1"></div>
            <ItemFormSettings
              v-if="itemDefinition"
              :processDefinition="assemble.getProcessDesigner().processDefinition"
              :itemDefinition="itemDefinition"
              :formConfig="itemDefinition.formConfig"
            ></ItemFormSettings>
          </template>
        </ProcessDesignDrawer>
      </template>
    </BizEntityDyform>
  </a-form-model>
</template>

<script>
import BizEntityDyform from '../biz-entity/biz-entity-dyform.vue';
import ProcessDesignDrawer from '../../../process-designer/component/process-design-drawer.vue';
import ItemFormSettings from '../../../process-designer/component/item-configuration/item-form-settings.vue';
import { filterSelectOption, getCacheData } from '../../../process-designer/designer/utils';
export default {
  props: {
    entity: Object,
    defaultTitle: {
      type: String,
      default: '事项办理单'
    },
    formStateFunction: Function
  },
  components: { BizEntityDyform, ProcessDesignDrawer, ItemFormSettings },
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
      drawerEditId: 'form_setting_' + this.entity.itemId,
      drawerContainer: '.designer-component-container .form-container .ant-card-body'
    };
  },
  computed: {
    itemDefinition() {
      let designer = this.assemble.getProcessDesigner();
      let itemNode = designer.getItemNodeByItemId(this.entity.itemId);
      if (!itemNode) {
        return null;
      }
      let itemData = itemNode.getData();
      if (!itemData.configuration.formConfig) {
        this.$set(itemData.configuration, 'formConfig', { configType: '2' });
      }
      return itemData.configuration;
    },
    isRefNode() {
      let designer = this.assemble.getProcessDesigner();
      let itemNode = designer.getItemNodeByItemId(this.entity.itemId);
      if (!itemNode) {
        return false;
      }
      return designer.isRefNode(itemNode.id);
    }
  }
};
</script>

<style></style>
