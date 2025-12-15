<template>
  <!-- 子流程表格 -->
  <div>
    <a-table
      rowKey="id"
      :showHeader="false"
      :pagination="false"
      size="small"
      :bordered="false"
      :columns="columns"
      :locale="locale"
      :data-source="dataSource"
      class="timers-table no-border"
    >
      <template slot="nameSlot" slot-scope="text, record">
        <div>流程名称：{{ record.name }}</div>
        <div>标签：{{ record.label }}</div>
        <div>提交环节：{{ record.toTaskName }}</div>
        <div>角色：{{ record.isMajor === '1' ? '主办' : '协办' }}</div>
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button type="link" size="small" icon="setting" @click="setItem(record, index)" />
        <!-- 删除 -->
        <a-button type="link" size="small" icon="delete" @click="delItem(index, record)" />
      </template>
      <template slot="footer">
        <div class="timers-table-footer">
          <span>
            <a-button type="link" @click="addItem" icon="plus">添加子流程</a-button>
          </span>
        </div>
      </template>
    </a-table>
    <modal
      title="子流程"
      v-model="visible"
      :container="getContainer"
      :ok="saveItme"
      okText="保存"
      :width="750"
      wrapperClass="flow-timer-modal-wrap"
    >
      <template slot="content">
        <node-subflow-flow-info ref="refItem" v-if="visible" :formData="currentItem" />
      </template>
    </modal>
  </div>
</template>

<script>
import SubFlow from './SubFlow';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import NodeSubflowFlowInfo from './new-flow-info.vue';
import { granularityOptions } from '../designer/constant';

export default {
  name: 'NodeSubflowNewFlows',
  inject: ['designer', 'subflowData', 'unChangedGranularityIds'],
  props: {
    dataSource: {
      type: Array,
      default: () => []
    }
  },
  components: {
    Modal,
    NodeSubflowFlowInfo
  },
  data() {
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      columns: [
        { title: '名称', dataIndex: 'name', scopedSlots: { customRender: 'nameSlot' } },
        { title: '操作', dataIndex: 'operation', width: 70, scopedSlots: { customRender: 'operationSlot' }, align: 'center' }
      ],
      currentIndex: 0,
      currentItem: undefined,
      visible: false
    };
  },
  methods: {
    setItem(record, index) {
      this.currentIndex = index;
      this.currentItem = JSON.parse(JSON.stringify(record));
      this.visible = true;
    },
    delItem(index, record) {
      this.dataSource.splice(index, 1);
      this.setGranularity(record);
    },
    addItem() {
      this.currentItem = new SubFlow();
      this.visible = true;
    },
    saveItme(callback) {
      this.$refs.refItem.validate(({ valid, error, data }) => {
        if (valid) {
          this.setGranularity();

          const findIndex = this.dataSource.findIndex(item => {
            return item.id === this.currentItem.id;
          });

          if (findIndex === -1) {
            this.dataSource.push(this.currentItem);
          } else {
            this.dataSource.splice(findIndex, 1, this.currentItem);
          }
          this.$refs.refItem.createInstanceFormId = undefined;
          callback(true);
        }
      });
    },
    setGranularity(item = this.currentItem) {
      const flowIdIndex = this.unChangedGranularityIds.value.findIndex(id => id === item.id);
      if (flowIdIndex !== -1) {
        this.unChangedGranularityIds.value.splice(flowIdIndex, 1);
      }
      if (!this.unChangedGranularityIds.value.length) {
        let businessTypes = this.designer.subflowChangedBusinessType;
        const hasIndex = businessTypes.findIndex(b => b === this.subflowData.id);
        if (hasIndex !== -1) {
          businessTypes.splice(hasIndex, 1);
        }
      }
    },
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    }
  }
};
</script>
