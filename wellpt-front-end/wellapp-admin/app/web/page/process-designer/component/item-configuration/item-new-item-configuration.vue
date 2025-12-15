<template>
  <a-table
    rowKey="id"
    :showHeader="false"
    size="small"
    :pagination="false"
    :bordered="false"
    :columns="newItemTableColumn"
    :locale="locale"
    :data-source="workflowIntegration.newItemConfigs"
    :class="['widget-table-new-item-table no-border']"
  >
    <template slot="titleSlot" slot-scope="text, record">
      <a-row>
        <a-col span="4">
          <Icon type="pticon iconfont icon-ptkj-tuodong" class="drag-btn-handler" :style="{ cursor: 'move' }" :disabled="disabled" />
        </a-col>
        <a-col span="18">
          <a-input v-model="record.title" size="small" :style="{ width: '180px' }" :disabled="disabled"></a-input>
        </a-col>
      </a-row>
    </template>

    <template v-if="!disabled" slot="operationSlot" slot-scope="text, record, index">
      <a-button type="link" size="small" title="删除" @click="deleteNewItem(index)">
        <Icon type="pticon iconfont icon-ptkj-shanchu" />
      </a-button>
      <ProcessDesignDrawer :id="'new-item-edit_' + record.id" :closeOpenDrawer="closeOpenDrawer" title="编辑发起事项">
        <a-button type="link" size="small" title="配置"><Icon type="pticon iconfont icon-ptkj-shezhi" /></a-button>
        <template slot="content">
          <NewItemConfiguration :newItemConfig="record"></NewItemConfiguration>
        </template>
      </ProcessDesignDrawer>
    </template>
    <template v-if="!disabled" slot="footer">
      <ProcessDesignDrawer :id="drawerAddId" :closeOpenDrawer="closeOpenDrawer" title="添加发起事项">
        <a-button type="link" :style="{ paddingLeft: '7px' }">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          添加
        </a-button>
        <template slot="content">
          <NewItemConfiguration ref="addNewItemConfiguration"></NewItemConfiguration>
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
import NewItemConfiguration from './new-item-configuration.vue';
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
  components: { ProcessDesignDrawer, NewItemConfiguration },
  inject: ['pageContext'],
  data() {
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      newItemTableColumn: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ],
      drawerAddId: 'new-item-add_' + new Date().getTime()
    };
  },
  created() {},
  mounted() {
    this.tableDraggable(
      this.workflowIntegration.newItemConfigs,
      this.$el.querySelector('.widget-table-new-item-table tbody'),
      '.drag-btn-handler'
    );
  },
  methods: {
    deleteNewItem(index) {
      this.workflowIntegration.newItemConfigs.splice(index, 1);
    },
    onConfirmOk() {
      this.$refs.addNewItemConfiguration.collect().then(data => {
        if (data) {
          this.workflowIntegration.newItemConfigs.push(data);
          this.pageContext.emitEvent('closeDrawer:' + this.drawerAddId);
        }
      });
    }
  }
};
</script>

<style></style>
