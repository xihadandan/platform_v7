<template>
  <a-table
    rowKey="id"
    :showHeader="false"
    size="small"
    :pagination="false"
    :bordered="false"
    :columns="stateTableColumn"
    :locale="locale"
    :data-source="workflowIntegration.states"
    :class="['widget-table-state-config-table no-border']"
  >
    <template slot="titleSlot" slot-scope="text, record">
      <a-row>
        <a-col span="4">
          <Icon type="pticon iconfont icon-ptkj-tuodong" class="drag-btn-handler" :style="{ cursor: 'move' }" :disabled="disabled" />
        </a-col>
        <a-col span="18">
          <a-input v-model="record.stateTypeName" size="small" :style="{ width: '120px' }" :disabled="disabled"></a-input>
        </a-col>
      </a-row>
    </template>

    <template v-if="!disabled" slot="operationSlot" slot-scope="text, record, index">
      <a-button type="link" size="small" title="删除" @click="deleteStateConfig(index)">
        <Icon type="pticon iconfont icon-ptkj-shanchu" />
      </a-button>
      <ProcessDesignDrawer :id="'state-config-edit_' + record.id" :closeOpenDrawer="closeOpenDrawer" :width="800" title="编辑状态配置">
        <a-button type="link" size="small" title="配置"><Icon type="pticon iconfont icon-ptkj-shezhi" /></a-button>
        <template slot="content">
          <WorkflowStateDefinition :stateDefinition="record" :formUuid="workflowIntegration.formUuid"></WorkflowStateDefinition>
        </template>
      </ProcessDesignDrawer>
    </template>
    <template v-if="!disabled" slot="footer">
      <ProcessDesignDrawer :id="drawerAddId" :closeOpenDrawer="closeOpenDrawer" :width="800" title="添加状态配置">
        <a-button type="link" :style="{ paddingLeft: '7px' }">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          添加
        </a-button>
        <template slot="content">
          <WorkflowStateDefinition ref="addStateDefinition" :formUuid="workflowIntegration.formUuid"></WorkflowStateDefinition>
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
import WorkflowStateDefinition from './workflow-state-configuration.vue';
import draggable from '@framework/vue/designer/draggable';
export default {
  mixins: [draggable],
  props: {
    workflowIntegration: Object,
    disabled: {
      type: Boolean,
      default: false
    },
    closeOpenDrawer: {
      type: Boolean,
      default: true
    }
  },
  components: { ProcessDesignDrawer, WorkflowStateDefinition },
  inject: ['pageContext'],
  data() {
    if (!this.workflowIntegration.states) {
      this.$set(this.workflowIntegration, 'states', []);
    }
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      stateTableColumn: [
        { title: '名称', dataIndex: 'stateTypeName', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ],
      drawerAddId: 'state-config-add_' + new Date().getTime()
    };
  },
  created() {},
  mounted() {
    this.tableDraggable(
      this.workflowIntegration.states,
      this.$el.querySelector('.widget-table-state-config-table tbody'),
      '.drag-btn-handler'
    );
  },
  methods: {
    deleteStateConfig(index) {
      this.workflowIntegration.states.splice(index, 1);
    },
    onConfirmOk() {
      this.$refs.addStateDefinition.collect().then(data => {
        if (data) {
          // 旧的模板数据兼容处理
          if (!this.workflowIntegration.states) {
            this.$set(this.workflowIntegration, 'states', []);
          }
          this.workflowIntegration.states.push(data);
          this.pageContext.emitEvent('closeDrawer:' + this.drawerAddId);
        }
      });
    }
  }
};
</script>

<style></style>
