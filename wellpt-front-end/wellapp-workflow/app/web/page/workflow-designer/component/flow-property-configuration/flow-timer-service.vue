<template>
  <div>
    <a-table
      :rowKey="rowKey"
      :bordered="false"
      :columns="columns"
      :locale="locale"
      :data-source="dataSource"
      :pagination="pagination"
      :customRow="customRow"
      :loading="loading"
      :rowSelection="rowSelection"
      @change="handleTableChange"
      class="timers-table no-border timers-service-table"
    >
      <template slot="title">
        <a-input-search
          v-model.trim="keyword"
          :allowClear="true"
          enterButton="查询"
          placeholder="关键字"
          @search="onSearch"
          @change="changeKeyword"
          style="width: 370px"
        />
      </template>
    </a-table>
    <modal :title="title" :width="1100" v-model="visible">
      <template slot="content">
        <timer-config-details v-if="visible" ref="timerDetails" :disabled="true" />
      </template>
    </modal>
  </div>
</template>

<script>
/* 
计时服务列表接口
/proxy/api/datastore/loadData
post
{
    "dataStoreId": "CD_DS_TS_TIMER_CONFIG",
    "proxy": {
        "storeId": "CD_DS_TS_TIMER_CONFIG"
    },
    "pagingInfo": {
        "pageSize": 10,
        "currentPage": 3,
        "autoCount": true
    },
    "params": {},
    "criterions": [],
    "renderers": [],
    "orders": [
        {
            "sortName": "code",
            "sortOrder": "asc"
        },
        {
            "sortName": "categoryName",
            "sortOrder": "asc"
        }
    ]
}


旧的
/json/data/services
post

argTypes: [],
args: JSON.stringify([]),
methodName: "loadData",
serviceName: "cdDataStoreService",
validate: false

{
    "dataStoreId": "CD_DS_TS_TIMER_CONFIG",
    "proxy": {
        "storeId": "CD_DS_TS_TIMER_CONFIG"
    },
    "pagingInfo": {
        "pageSize": "10",
        "currentPage": 1,
        "autoCount": true
    },
    "params": {
        "keyword": ""
    },
    "criterions": [
        {
            "sql": "(system_unit_id = :currentUserUnitId or system_unit_id = 'S0000000000') and category_uuid in (select c.uuid from ts_timer_category c where c.id = 'flowTiming')"
        }
    ],
    "renderers": [],
    "orders": [
        {
            "sortName": "code",
            "sortOrder": "asc"
        }
    ]
}
*/

import { fetchTimerService, fetchTimerItem } from '../api/index';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import TimerConfigDetails from '@admin/app/web/template/timeservice-manage/timer-config-details.vue';
export default {
  name: 'FlowTimerService',
  props: {
    uuid: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      columns: [
        { title: '名称', dataIndex: 'name', ellipsis: true, scopedSlots: { customRender: 'nameSlot' } },
        { title: 'ID', dataIndex: 'id', ellipsis: true },
        { title: '编号', dataIndex: 'code', ellipsis: true },
        { title: '计时方式', dataIndex: 'timingModeTypeName', ellipsis: true },
        { title: '时限类型', dataIndex: 'timeLimitTypeName', ellipsis: true }
      ],
      dataSource: [],
      visible: false,
      title: '',
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
      event: {
        meta: undefined
      }
    };
  },
  components: {
    Modal,
    TimerConfigDetails
  },
  provide() {
    return {
      $event: this.event,
      vPageState: {}
    };
  },
  computed: {
    rowSelection() {
      return {
        type: 'radio',
        selectedRowKeys: this.selectedRowKeys,
        onChange: this.selectRowChange,
        getCheckboxProps: this.getCheckboxProps,
        columnWidth: 37
      };
    }
  },
  created() {
    this.getTimerService();
  },
  methods: {
    selectRowChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys;
      this.selectedRows = selectedRows;
    },
    getTimerService() {
      this.loading = true;
      fetchTimerService({
        pageSize: this.pagination.pageSize,
        currentPage: this.pagination.current,
        keyword: this.keyword
      }).then(res => {
        this.loading = false;
        const { data, pagination } = res;
        this.dataSource = data;
        this.pagination.total = pagination.totalCount;
      });
    },
    onSearch() {
      this.pagination.current = 1;
      this.getTimerService();
    },
    changeKeyword(event) {
      const keyword = event.target.value;
      if (keyword === '') {
        this.pagination.current = 1;
        this.getTimerService();
      }
    },
    handleTableChange(pagination, filters, sorter, { currentDataSource }) {
      this.pagination.pageSize = pagination.pageSize;
      this.pagination.current = pagination.current;
      this.getTimerService();
    },
    getCheckboxProps(row) {
      return {
        props: {
          checked: row[this.rowKey] === this.uuid
        },
        on: {
          click: event => {
            // 单选反选取消选中效果
            /* if (this.selectedRowKeys.includes(row[this.rowKey])) {
              this.selectedRowKeys.splice(0, this.selectedRowKeys.length);
              this.selectedRows.splice(0, this.selectedRows.length);
            } */
          }
        }
      };
    },
    // 点击行设置获取单个计时服务后设置title
    customRow(row, index) {
      return {
        on: {
          // 事件
          click: event => {
            this.event.meta = row;
            this.title = row.name;
            this.visible = true;
            // fetchTimerItem(row.uuid).then(res => {
            //   this.event.meta = res;
            //   this.title = res.name;
            //   this.visible = true;
            //   this.$nextTick(() => {
            //     this.$refs.timerDetails.loading = false;
            //     console.log(this.$refs.timerDetails);
            //     // .querySelectorAll('')
            //   });
            // });
          }
        }
      };
    }
  }
};
</script>
