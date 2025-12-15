<template>
  <a-table
    rowKey="id"
    :showHeader="false"
    size="small"
    :pagination="false"
    :bordered="false"
    :columns="fieldMappingTableColumn"
    :locale="locale"
    :data-source="formConfig.fieldMappings"
    :class="['widget-table-field-mapping-table no-border']"
  >
    <template slot="sourceFieldNameSlot" slot-scope="text, record">
      <Icon type="pticon iconfont icon-ptkj-tuodong" class="drag-btn-handler" :style="{ cursor: 'move' }" :disabled="disabled" />
      <span :title="record.sourceFieldName + '->' + record.targetFieldName">
        {{ record.sourceFieldName }} -> {{ record.targetFieldName }}
      </span>
    </template>

    <template v-if="!disabled" slot="operationSlot" slot-scope="text, record, index">
      <a-button type="link" size="small" title="删除" @click="deleteFieldMapping(index)">
        <Icon type="pticon iconfont icon-ptkj-shanchu" />
      </a-button>
      <ProcessDesignDrawer :id="'field-mapping-edit_' + record.id" :closeOpenDrawer="closeOpenDrawer" :width="800" title="编辑字段回填">
        <a-button type="link" size="small" title="配置"><Icon type="pticon iconfont icon-ptkj-shezhi" /></a-button>
        <template slot="content">
          <FieldMappingConfiguration
            :fieldMapping="record"
            :formConfig="formConfig"
            :itemDefinition="itemDefinition"
          ></FieldMappingConfiguration>
        </template>
      </ProcessDesignDrawer>
    </template>
    <template v-if="!disabled" slot="footer">
      <ProcessDesignDrawer :id="drawerAddId" :closeOpenDrawer="closeOpenDrawer" :width="800" title="添加字段回填">
        <a-button type="link" :style="{ paddingLeft: '7px' }">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          添加
        </a-button>
        <template slot="content">
          <FieldMappingConfiguration
            ref="addFieldMapping"
            :formConfig="formConfig"
            :itemDefinition="itemDefinition"
          ></FieldMappingConfiguration>
        </template>
        <template slot="footer">
          <a-button size="small" type="primary" @click.stop="onConfirmOk">确定</a-button>
        </template>
      </ProcessDesignDrawer>
    </template>
  </a-table>
</template>

<script>
import ProcessDesignDrawer from '../process-design-drawer.vue';
import FieldMappingConfiguration from './item-form-field-mapping-configuration.vue';
import draggable from '@framework/vue/designer/draggable';
export default {
  mixins: [draggable],
  props: {
    itemDefinition: Object,
    formConfig: Object,
    disabled: {
      type: Boolean,
      default: false
    },
    closeOpenDrawer: {
      type: Boolean,
      default: true
    }
  },
  components: { ProcessDesignDrawer, FieldMappingConfiguration },
  inject: ['pageContext'],
  data() {
    if (!this.formConfig.fieldMappings) {
      this.$set(this.formConfig, 'fieldMappings', []);
    }
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      fieldMappingTableColumn: [
        { title: '源字段', dataIndex: 'sourceFieldName', ellipsis: true, scopedSlots: { customRender: 'sourceFieldNameSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ],
      drawerAddId: 'field-mapping-add_' + new Date().getTime()
    };
  },
  created() {},
  mounted() {
    this.tableDraggable(
      this.formConfig.fieldMappings,
      this.$el.querySelector('.widget-table-field-mapping-table tbody'),
      '.drag-btn-handler'
    );
  },
  methods: {
    deleteFieldMapping(index) {
      this.formConfig.fieldMappings.splice(index, 1);
    },
    onConfirmOk() {
      this.$refs.addFieldMapping.collect().then(data => {
        if (data) {
          this.formConfig.fieldMappings.push(data);
          this.pageContext.emitEvent('closeDrawer:' + this.drawerAddId);
        }
      });
    }
  }
};
</script>

<style></style>
