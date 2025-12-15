<template>
  <!-- 子流程-前置关系 -->
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
        <div>子流程：{{ record.newFlowName }}</div>
        <div>环节：{{ record.taskName }}</div>
        <div>前置子流程：{{ record.frontNewFlowName }}</div>
        <div>前置环节：{{ record.frontTaskName }}</div>
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button type="link" size="small" icon="setting" @click="setItem(record, index)" />
        <!-- 删除 -->
        <a-button type="link" size="small" icon="delete" @click="delItem(index)" />
      </template>
      <template slot="footer">
        <div class="timers-table-footer">
          <span>
            <a-button type="link" @click="addItem" icon="plus">添加前置关系</a-button>
          </span>
        </div>
      </template>
    </a-table>
    <modal
      title="前置流程关系"
      v-model="visible"
      :container="getContainer"
      :ok="saveItme"
      okText="保存"
      :width="600"
      wrapperClass="flow-timer-modal-wrap"
    >
      <template slot="content">
        <relation-info ref="refItem" v-if="currentItem" :formData="currentItem" />
      </template>
    </modal>
  </div>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import RelationInfo from './relation-info.vue';

export default {
  name: 'RelationTable',
  props: {
    dataSource: {
      type: Array,
      default: () => []
    }
  },
  components: {
    Modal,
    RelationInfo
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
      visible: false,
      createItem: () => {
        return {
          id: generateId(),
          newFlowName: '',
          newFlowId: '',
          taskName: '',
          taskId: '',
          frontNewFlowName: '',
          frontNewFlowId: '',
          frontTaskName: '',
          frontTaskId: ''
        };
      }
    };
  },
  created() {
    this.dataSource.forEach(item => {
      item.id = generateId();
    });
  },
  methods: {
    setItem(record, index) {
      this.currentIndex = index;
      this.currentItem = JSON.parse(JSON.stringify(record));
      this.visible = true;
    },
    delItem(index) {
      this.dataSource.splice(index, 1);
    },
    addItem() {
      this.currentItem = this.createItem();
      this.visible = true;
    },
    saveItme(callback) {
      this.$refs.refItem.validate(({ valid, error, data }) => {
        if (valid) {
          const findIndex = this.dataSource.findIndex(item => {
            return item.id === this.currentItem.id;
          });

          if (findIndex === -1) {
            this.dataSource.push(this.currentItem);
          } else {
            this.dataSource.splice(findIndex, 1, this.currentItem);
          }
          callback(true);
        }
      });
    },
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    }
  }
};
</script>
