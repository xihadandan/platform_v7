<template>
  <div class="data-dictionary-list">
    <!-- <template slot="title"> -->
    <a-alert type="info">
      <span slot="message">
        当前引用
        <span style="color: var(--w-primary-color)">{{ refCount }}</span>
        个字典，引用的字典无法编辑
      </span>
    </a-alert>
    <div style="margin: 12px 0">
      <a-input-search placeholder="关键字" v-model="searchText" allow-clear @search="onSearch" style="width: 260px; margin-right: 8px" />
      <a-button type="primary" @click="onSearch">确定</a-button>
    </div>
    <!-- </template> -->
    <a-table
      rowKey="uuid"
      :columns="dictionaryColumns"
      :data-source="rows"
      :pagination="pagination"
      @change="onTableChange"
      :row-selection="rowSelection"
      class="pt-table data-dictionary-ref-list-table"
    >
      <template slot="orderSlot" slot-scope="text, record, index">
        {{ getOrder(index) }}
      </template>
      <template slot="sourceSlot" slot-scope="text, record, index">
        <span v-if="record.moduleId && record.moduleName">
          {{ record.moduleName }}
        </span>
        <a-tag v-else>通用字典</a-tag>
      </template>
      <template slot="operationSlot" slot-scope="text, record">
        <Drawer
          title="查看字典"
          ref="drawer"
          :width="800"
          :mask="true"
          :maskClosable="true"
          :container="getDrawerContainer"
          okText="确定"
          :ok="e => e(true)"
          :destroyOnClose="true"
          drawerClass="pt-drawer"
        >
          <a-button type="link" size="small">
            <Icon type="pticon iconfont icon-szgy-zonghechaxun"></Icon>
            查看
          </a-button>
          <template slot="content">
            <DataDictionaryDetail
              :moduleSource="supportsGeneric && !record.moduleId ? 'none' : 'all'"
              displayState="readonly"
              :dataDictionary="record"
              :categories="categories"
              :ref="'editData_' + record.uuid"
            />
          </template>
        </Drawer>
        <Modal title="查看引用位置" :width="700" :ok="e => e(true)" :destroyOnClose="true" dialogClass="pt-modal">
          <a-button type="link" size="small">
            <Icon type="pticon iconfont icon-ptkj-yinyong"></Icon>
            引用位置
          </a-button>
          <template slot="content">
            <DataDictionaryRefDetail :dataDictionary="record"></DataDictionaryRefDetail>
          </template>
        </Modal>
      </template>
    </a-table>
    <div style="text-align: right; margin-top: 20px">
      <a-pagination v-model="pagination.current" :pageSize="pagination.pageSize" @change="onChangePagination" :total="pagination.total" />
    </div>
  </div>
</template>

<script>
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import DataDictionaryDetail from './data-dictionary-detail.vue';
import DataDictionaryRefDetail from './data-dictionary-ref-detail.vue';
export default {
  props: {
    categories: Array,
    // 是否支持通用字典
    supportsGeneric: {
      type: Boolean,
      default: false
    },
    // 模块来源，'current'当前模块，'all'系统所有模块,‘none’没有模块归属
    moduleSource: {
      type: String,
      default: 'current'
    },
    refCount: {
      type: Number,
      default: 0
    }
  },
  inject: ['currentModule'],
  components: { Drawer, Modal, DataDictionaryDetail, DataDictionaryRefDetail },
  data() {
    const _this = this;
    return {
      searchText: '',
      dictionaryColumns: [
        { title: '序号', dataIndex: 'order', scopedSlots: { customRender: 'orderSlot' } },
        { title: '字典名称', dataIndex: 'name' },
        { title: '编码', dataIndex: 'code' },
        { title: '分类', dataIndex: 'categoryName' },
        { title: '来源', dataIndex: 'moduleName', scopedSlots: { customRender: 'sourceSlot' } },
        { title: '操作', width: 200, dataIndex: '__operation', scopedSlots: { customRender: 'operationSlot' } }
      ],
      rows: [],
      pagination: {
        pageSize: 10,
        current: 1,
        totalPages: 0,
        totalCount: 0,
        showTotal(total, range) {
          return `共${_this.pagination.totalPages}页/${total}条记录`;
        }
      },
      selectedRowKeys: [],
      selectedRows: [],
      rowSelection: {
        onChange: (selectedRowKeys, selectedRows) => {
          this.selectedRowKeys = selectedRowKeys;
          this.selectedRows = selectedRows;
        }
      },
      modifierMap: {},
      formData: {
        moduleId: undefined
      }
    };
  },
  created() {
    this.loadData();
  },
  methods: {
    refresh() {
      this.loadData();
    },
    onSearch() {
      this.pagination.current = 1;
      this.loadData();
    },
    onTableChange(pagination) {
      if (pagination) {
        this.pagination = pagination;
      }
      this.loadData();
    },
    loadData() {
      let criterions = [];
      if (this.searchText) {
        criterions.push({
          type: 'or',
          conditions: [
            {
              columnIndex: 'name',
              value: this.searchText,
              type: 'like'
            },
            {
              columnIndex: 'code',
              value: this.searchText,
              type: 'like'
            }
          ]
        });
      }
      $axios
        .post(`/proxy/api/datastore/loadData`, {
          dataStoreId: 'CD_DS_DATA_DICTIONARY_REF',
          pagingInfo: { currentPage: this.pagination.current, pageSize: this.pagination.pageSize },
          criterions,
          params: {
            moduleId: this.currentModule && this.currentModule.id,
            distinct: true
          }
        })
        .then(({ data }) => {
          let rows = data.data.data;
          this.pagination.total = data.data.pagination.totalCount;
          this.pagination.current = data.data.pagination.currentPage;
          this.pagination.totalPages = data.data.pagination.totalPages;
          this.pagination.totalCount = data.data.pagination.totalCount;
          this.rows = rows;
        });
    },
    getOrder(index) {
      if (this.pagination.current <= 0) {
        return index + 1;
      }
      return this.pagination.pageSize * (this.pagination.current - 1) + index + 1;
    },
    getDrawerContainer() {
      return document.body; //this.$el; // document.querySelector('.data-dictionary-list');
    },
    onChangePagination() {
      this.onTableChange();
    }
  }
};
</script>
