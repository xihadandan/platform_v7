<template>
  <div>
    <a-table
      rowKey="id"
      :showHeader="false"
      size="small"
      :pagination="false"
      :bordered="false"
      :columns="entityTimersTableColumn"
      :locale="locale"
      :data-source="entityDefinition.timers"
      :class="['widget-table-entity-timer-table no-border']"
    >
      <template slot="titleSlot" slot-scope="text">
        <span :title="text">{{ text }}</span>
      </template>

      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button type="link" size="small" title="删除" @click="deleteTimerEvent(index)">
          <Icon type="pticon iconfont icon-ptkj-shanchu" />
        </a-button>
        <ProcessDesignDrawer :id="'entity-timer-edit_' + record.id" title="编辑状态计时定义">
          <a-button type="link" size="small" title="配置"><Icon type="pticon iconfont icon-ptkj-shezhi" /></a-button>
          <template slot="content">
            <EntityTimerConfiguration :entityDefinition="entityDefinition" :timer="record"></EntityTimerConfiguration>
          </template>
        </ProcessDesignDrawer>
      </template>

      <template slot="footer">
        <ProcessDesignDrawer :id="drawerAddId" title="添加业务主体状态计时器">
          <a-button type="link" :style="{ paddingLeft: '7px' }">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            添加
          </a-button>
          <template slot="content">
            <EntityTimerConfiguration ref="addEntityTimerConfiguration" :entityDefinition="entityDefinition"></EntityTimerConfiguration>
          </template>
          <template slot="footer">
            <a-button size="small" type="primary" @click.stop="onConfirmOk">确定</a-button>
          </template>
        </ProcessDesignDrawer>
      </template>
    </a-table>
  </div>
</template>

<script>
import ProcessDesignDrawer from '../process-design-drawer.vue';
import EntityTimerConfiguration from './entity-timer-configuration.vue';
import draggable from '@framework/vue/designer/draggable';
export default {
  mixins: [draggable],
  props: {
    entityDefinition: Object
  },
  components: { ProcessDesignDrawer, EntityTimerConfiguration },
  inject: ['pageContext'],
  data() {
    if (!this.entityDefinition.timers) {
      this.$set(this.entityDefinition, 'timers', []);
    }
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      entityTimersTableColumn: [
        { title: '名称', dataIndex: 'name', ellipsis: true, scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ],
      drawerAddId: 'entity-timer-add_' + new Date().getTime()
    };
  },
  mounted() {
    this.tableDraggable(
      this.entityDefinition.timers,
      this.$el.querySelector('.widget-table-entity-timer-table tbody'),
      '.drag-btn-handler'
    );
  },
  methods: {
    deleteTimerEvent(index) {
      this.entityDefinition.timers.splice(index, 1);
    },
    onConfirmOk() {
      this.$refs.addEntityTimerConfiguration.collect().then(data => {
        if (data) {
          this.entityDefinition.timers.push(data);
          this.pageContext.emitEvent('closeDrawer:' + this.drawerAddId);
        }
      });
    }
  }
};
</script>

<style></style>
