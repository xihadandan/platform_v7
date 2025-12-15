<template>
  <a-table
    rowKey="id"
    :showHeader="false"
    size="small"
    :pagination="false"
    :bordered="false"
    :columns="milestoneTableColumn"
    :locale="locale"
    :data-source="workflowIntegration.milestoneConfigs"
    :class="['widget-table-item-milestone-table no-border']"
  >
    <template slot="nameSlot" slot-scope="text, record">
      <a-row>
        <a-col span="4">
          <Icon type="pticon iconfont icon-ptkj-tuodong" class="drag-btn-handler" :style="{ cursor: 'move' }" :disabled="disabled" />
        </a-col>
        <a-col span="18">
          <a-input v-model="record.name" size="small" :style="{ width: '180px' }" :disabled="disabled"></a-input>
        </a-col>
      </a-row>
    </template>

    <template v-if="!disabled" slot="operationSlot" slot-scope="text, record, index">
      <a-button type="link" size="small" title="删除" @click="deleteMilestone(index)">
        <Icon type="pticon iconfont icon-ptkj-shanchu" />
      </a-button>
      <ProcessDesignDrawer :id="'item-milestone-edit_' + record.id" :closeOpenDrawer="closeOpenDrawer" title="编辑里程碑">
        <a-button type="link" size="small" title="配置"><Icon type="pticon iconfont icon-ptkj-shezhi" /></a-button>
        <template slot="content">
          <MilestoneConfiguration :milestoneConfig="record"></MilestoneConfiguration>
        </template>
      </ProcessDesignDrawer>
    </template>
    <template v-if="!disabled" slot="footer">
      <ProcessDesignDrawer :id="drawerAddId" :closeOpenDrawer="closeOpenDrawer" title="添加里程碑">
        <a-button type="link" :style="{ paddingLeft: '7px' }">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          添加
        </a-button>
        <template slot="content">
          <MilestoneConfiguration ref="addMilestoneConfiguration"></MilestoneConfiguration>
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
import MilestoneConfiguration from './milestone-configuration.vue';
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
  components: { ProcessDesignDrawer, MilestoneConfiguration },
  inject: ['pageContext'],
  data() {
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      milestoneTableColumn: [
        { title: '名称', dataIndex: 'name', scopedSlots: { customRender: 'nameSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ],
      drawerAddId: 'item-milestone-add_' + new Date().getTime()
    };
  },
  created() {},
  mounted() {
    this.tableDraggable(
      this.workflowIntegration.milestoneConfigs,
      this.$el.querySelector('.widget-table-item-milestone-table tbody'),
      '.drag-btn-handler'
    );
  },
  methods: {
    deleteMilestone(index) {
      this.milestoneConfigs.splice(index, 1);
    },
    onConfirmOk() {
      this.$refs.addMilestoneConfiguration.collect().then(data => {
        if (data) {
          this.workflowIntegration.milestoneConfigs.push(data);
          this.pageContext.emitEvent('closeDrawer:' + this.drawerAddId);
        }
      });
    }
  }
};
</script>

<style></style>
