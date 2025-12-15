<template>
  <a-table
    rowKey="id"
    :showHeader="false"
    size="small"
    :pagination="false"
    :bordered="false"
    :columns="eventPublishTableColumn"
    :locale="locale"
    :data-source="eventPublishConfigs"
    :class="['widget-table-item-event-publish-table no-border']"
  >
    <template slot="nameSlot" slot-scope="text, record">
      <Icon type="pticon iconfont icon-ptkj-tuodong" class="drag-btn-handler" :style="{ cursor: 'move' }" :disabled="disabled" />
      <span :title="record.eventName">{{ record.eventName }}</span>
    </template>

    <template v-if="!disabled" slot="operationSlot" slot-scope="text, record, index">
      <a-button type="link" size="small" title="删除" @click="deleteEventPublish(index)">
        <Icon type="pticon iconfont icon-ptkj-shanchu" />
      </a-button>
      <ProcessDesignDrawer :id="'item-event-publish-edit_' + record.id" :closeOpenDrawer="closeOpenDrawer" title="编辑事件发布">
        <a-button type="link" size="small" title="配置"><Icon type="pticon iconfont icon-ptkj-shezhi" /></a-button>
        <template slot="content">
          <WorkflowEventPublishConfiguration :eventPublishConfig="record"></WorkflowEventPublishConfiguration>
        </template>
      </ProcessDesignDrawer>
    </template>
    <template v-if="!disabled" slot="footer">
      <ProcessDesignDrawer :id="drawerAddId" :closeOpenDrawer="closeOpenDrawer" title="添加事件发布">
        <a-button type="link" :style="{ paddingLeft: '7px' }">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          添加
        </a-button>
        <template slot="content">
          <WorkflowEventPublishConfiguration ref="addEventPublishConfiguration"></WorkflowEventPublishConfiguration>
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
import WorkflowEventPublishConfiguration from './workflow-event-publish-configuration.vue';
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
  components: { ProcessDesignDrawer, WorkflowEventPublishConfiguration },
  inject: ['pageContext', 'itemDefinition'],
  data() {
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      eventPublishTableColumn: [
        { title: '名称', dataIndex: 'eventName', ellipsis: true, scopedSlots: { customRender: 'nameSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ],
      drawerAddId: 'item-event-publish-add_' + new Date().getTime()
    };
  },
  computed: {
    eventPublishConfigs() {
      let eventPublishConfigs = this.workflowIntegration.eventPublishConfigs;
      if (!this.workflowIntegration.eventPublishConfigs) {
        return [];
      }
      let defineEvents = this.itemDefinition.defineEvents || [];
      eventPublishConfigs.forEach(item => {
        let defineEvent = defineEvents.find(event => event.id == item.eventId);
        if (defineEvent) {
          item.eventName = defineEvent.name;
        }
      });
      return eventPublishConfigs;
    }
  },
  mounted() {
    this.tableDraggable(
      this.workflowIntegration.eventPublishConfigs,
      this.$el.querySelector('.widget-table-item-event-publish-table tbody'),
      '.drag-btn-handler'
    );
  },
  methods: {
    deleteEventPublish(index) {
      this.workflowIntegration.eventPublishConfigs.splice(index, 1);
    },
    onConfirmOk() {
      this.$refs.addEventPublishConfiguration.collect().then(data => {
        if (data) {
          // 旧的模板数据兼容处理
          if (!this.workflowIntegration.eventPublishConfigs) {
            this.$set(this.workflowIntegration, 'eventPublishConfigs', []);
          }
          this.workflowIntegration.eventPublishConfigs.push(data);
          this.pageContext.emitEvent('closeDrawer:' + this.drawerAddId);
        }
      });
    }
  }
};
</script>

<style></style>
