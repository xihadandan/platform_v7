<template>
  <a-table
    rowKey="id"
    :showHeader="false"
    size="small"
    :pagination="false"
    :bordered="false"
    :columns="stateConditionTableColumn"
    :locale="locale"
    :data-source="nodeDefinition.stateConditions"
    :class="['widget-table-state-condition-table no-border']"
  >
    <template slot="titleSlot" slot-scope="text, record">
      <a-row>
        <a-col span="4">
          <Icon type="pticon iconfont icon-ptkj-tuodong" class="drag-btn-handler" :style="{ cursor: 'move' }" />
        </a-col>
        <a-col span="18">
          <a-input v-model="record.conditionName" size="small" :style="{ width: '120px' }"></a-input>
        </a-col>
      </a-row>
    </template>

    <template slot="operationSlot" slot-scope="text, record, index">
      <a-button type="link" size="small" title="删除" @click="deleteStateCondition(index)">
        <Icon type="pticon iconfont icon-ptkj-shanchu" />
      </a-button>
      <ProcessDesignDrawer :id="'state-condition-edit_' + record.id" title="编辑过程状态变更条件">
        <a-button type="link" size="small" title="配置"><Icon type="pticon iconfont icon-ptkj-shezhi" /></a-button>
        <template slot="content">
          <StateConditionConfiguration :stateCondition="record"></StateConditionConfiguration>
        </template>
      </ProcessDesignDrawer>
    </template>
    <template slot="footer">
      <ProcessDesignDrawer :id="drawerAddId" title="添加阶段状态变更条件">
        <a-button type="link" :style="{ paddingLeft: '7px' }">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          添加
        </a-button>
        <template slot="content">
          <StateConditionConfiguration ref="addStateConditionConfiguration"></StateConditionConfiguration>
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
import StateConditionConfiguration from './state-condition-configuration.vue';
import draggable from '@framework/vue/designer/draggable';
export default {
  mixins: [draggable],
  props: {
    nodeDefinition: Object
  },
  components: { ProcessDesignDrawer, StateConditionConfiguration },
  inject: ['pageContext'],
  data() {
    if (!this.nodeDefinition.stateConditions) {
      this.nodeDefinition.stateConditions = [];
    }
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      stateConditionTableColumn: [
        { title: '名称', dataIndex: 'conditionName', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ],
      drawerAddId: 'state-condition-add_' + new Date().getTime()
    };
  },
  created() {},
  mounted() {
    this.tableDraggable(
      this.nodeDefinition.stateConditions,
      this.$el.querySelector('.widget-table-state-condition-table tbody'),
      '.drag-btn-handler'
    );
  },
  methods: {
    deleteStateCondition(index) {
      this.nodeDefinition.stateConditions.splice(index, 1);
    },
    onConfirmOk() {
      this.$refs.addStateConditionConfiguration.collect().then(data => {
        if (data) {
          this.nodeDefinition.stateConditions.push(data);
          this.pageContext.emitEvent('closeDrawer:' + this.drawerAddId);
        }
      });
    }
  }
};
</script>

<style></style>
