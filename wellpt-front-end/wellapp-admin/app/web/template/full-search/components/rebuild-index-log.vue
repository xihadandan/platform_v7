<template>
  <div>
    <a-table
      :rowKey="rowKey"
      :bordered="false"
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      @change="handleTableChange"
    >
      <!-- <template slot="title">
        <a-input-search
          v-model.trim="keyword"
          :allowClear="true"
          enterButton="查询"
          placeholder="关键字"
          @search="onSearch"
          @change="changeKeyword"
          style="width: 370px"
        />
      </template> -->
      <template slot="originalIndexCountSlot" slot-scope="text, record, index">
        {{ text | formatCount }}
      </template>
      <template slot="rebuildIndexCountSlot" slot-scope="text, record, index">
        {{ text | formatCount }}
      </template>
      <template slot="elapsedTimeInSecondSlot" slot-scope="text, record, index">
        {{ text ? `${Number(text)}(秒)` : '' }}
      </template>
      <template slot="executeStateSlot" slot-scope="text, record, index">
        <span :style="`${text === '1' ? '' : text === '3' ? 'color:var(--w-danger-color)' : 'color:var(--w-success-color)'}`">
          {{ executeStateMap[text] }}
        </span>
      </template>
    </a-table>
  </div>
</template>

<script>
import { fetchRebuildLog } from '../api';

export default {
  name: 'RebuildIndexLog',
  inject: ['settingProvide'],
  data() {
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      dataSource: [],
      columns: [
        { title: '开始时间', dataIndex: 'startTime', width: '180px' },
        { title: '结束时间', dataIndex: 'endTime', width: '180px' },
        { title: '耗时', dataIndex: 'elapsedTimeInSecond', scopedSlots: { customRender: 'elapsedTimeInSecondSlot' } },
        { title: '原索引数', dataIndex: 'originalIndexCount', scopedSlots: { customRender: 'originalIndexCountSlot' } },
        { title: '重建索引数', dataIndex: 'rebuildIndexCount', scopedSlots: { customRender: 'rebuildIndexCountSlot' } },
        { title: '执行状态', dataIndex: 'executeState', width: '90px', scopedSlots: { customRender: 'executeStateSlot' } }
      ],
      keyword: '',
      loading: false,
      rowKey: 'uuid',
      selectedRowKeys: [],
      selectedRows: [],
      pagination: {
        showQuickJumper: true,
        showSizeChanger: true,
        current: 1,
        total: 0,
        pageSize: 10,
        showTotal: function (total, range) {
          const totalPages = total % this.pageSize === 0 ? parseInt(total / this.pageSize) : parseInt(total / this.pageSize + 1);
          return `共 ${totalPages} 页/ ${total} 条记录`;
        }
      },
      executeStateMap: {
        1: '执行中',
        2: '成功',
        3: '失败'
      }
    };
  },
  created() {
    if (this.settingProvide && this.settingProvide.uuid) {
      this.getRebuildIndexLog();
    }
  },
  filters: {
    formatCount: function (num) {
      if (!num) return '';
      return String(num).replace(/(?!^)(?=((\d{3})+)$)/g, ',');
    }
  },
  methods: {
    getRebuildIndexLog() {
      this.loading = true;
      fetchRebuildLog({
        settingUuid: this.settingProvide.uuid
      }).then(res => {
        this.dataSource = res;

        this.loading = false;
      });
    },
    onSearch() {
      this.pagination.current = 1;
      this.getRebuildIndexLog();
    },
    changeKeyword(event) {
      const keyword = event.target.value;
      if (keyword === '') {
        this.pagination.current = 1;
        this.getRebuildIndexLog();
      }
    },
    handleTableChange(pagination, filters, sorter, { currentDataSource }) {
      this.pagination.pageSize = pagination.pageSize;
      this.pagination.current = pagination.current;
      this.getRebuildIndexLog();
    }
  }
};
</script>
