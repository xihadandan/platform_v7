<template>
  <div>
    <!-- <a-alert type="info" show-icon message="您可以检测当前模块所有字典的使用情况，以便维护数据字典"></a-alert> -->
    <a-row>
      <a-col span="6">
        <a-input-search placeholder="关键字" v-model="searchText" allow-clear @search="onSearch" />
      </a-col>
      <a-col span="12">
        <a-button type="primary" @click="onSearch" style="margin-left: 8px">确定</a-button>
      </a-col>
    </a-row>
    <p />
    <a-row>
      <a-table
        :rowKey="record => record.rowId"
        :columns="dictionaryColumns"
        :data-source="rows"
        :pagination="pagination"
        @change="onTableChange"
        class="pt-table"
      >
        <template slot="orderSlot" slot-scope="text, record, index">
          {{ getOrder(index) }}
        </template>
        <template slot="functionTypeSlot" slot-scope="text, record">
          <template v-if="record.functionUuid">
            <span v-if="record.functionType == 'appPageDefinition'">页面</span>
            <span v-if="record.functionType == 'formDefinition'">表单</span>
          </template>
        </template>
        <template slot="functionNameSlot" slot-scope="text, record">
          <template v-if="record.functionUuid">
            {{ getFunctionLocationName(record) }}
          </template>
        </template>
        <template slot="operationSlot" slot-scope="text, record">
          <template v-if="record.functionUuid">
            <a-button @click="viewRefFunctionDesign(record)" type="link">查看设计</a-button>
          </template>
        </template>
      </a-table>
    </a-row>
  </div>
</template>

<script>
export default {
  props: {
    categoryType: {
      String,
      default: 'module' // 字典分类类型，buildIn、module
    }
  },
  inject: ['currentModule'],
  data() {
    const _this = this;
    return {
      searchText: '',
      dictionaryColumns: [
        { title: '序号', width: 60, dataIndex: 'order', scopedSlots: { customRender: 'orderSlot' } },
        { title: '字典名称', dataIndex: 'name' },
        { title: '编码', dataIndex: 'code' },
        { title: '使用功能', width: 90, dataIndex: 'functionType', scopedSlots: { customRender: 'functionTypeSlot' } },
        { title: '使用位置', dataIndex: 'functionName', scopedSlots: { customRender: 'functionNameSlot' } },
        { title: '操作', width: 160, dataIndex: '__operation', scopedSlots: { customRender: 'operationSlot' } }
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
      }
    };
  },
  created() {
    this.loadData();
  },
  methods: {
    onSearch() {
      this.pagination.current = 1;
      this.loadData();
    },
    onTableChange(pagination) {
      this.pagination = pagination;
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
      if (this.categoryType == 'buildIn') {
        criterions.push({
          columnIndex: 'categoryType',
          value: 0,
          type: 'eq'
        });
      } else if (this.categoryType == 'module') {
        criterions.push({
          type: 'or',
          conditions: [
            {
              columnIndex: 'categoryType',
              value: 1,
              type: 'eq'
            },
            {
              columnIndex: 'categoryType',
              type: 'is null'
            }
          ]
        });
      }
      let params = {};
      if (this.currentModule && this.currentModule.id) {
        params.moduleId = this.currentModule && this.currentModule.id;
      }
      $axios
        .post(`/proxy/api/datastore/loadData`, {
          dataStoreId: 'CD_DS_DATA_DICTIONARY_USED',
          pagingInfo: { currentPage: this.pagination.current, pageSize: this.pagination.pageSize },
          criterions,
          params
        })
        .then(({ data }) => {
          let rows = data.data.data;
          rows.forEach((item, index) => {
            item.rowId = `${item.uuid}_${item.functionUuid || 'none'}_${index}`;
          });
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
    getFunctionLocationName(record) {
      let paths = [];
      // 当前模块的引用位置
      if (record.functionModuleName) {
        paths.push(record.functionModuleName);
      }
      paths.push(record.functionName);
      if (record.functionItemName) {
        paths.push(record.functionItemName);
      }
      return paths.join('\\');
    },
    viewRefFunctionDesign(record) {
      let url = '';
      if (record.functionType == 'appPageDefinition') {
        url = `/page-designer/index?uuid=${record.functionUuid}`;
      } else if (record.functionType == 'formDefinition') {
        url = `/dyform-designer/index?uuid=${record.functionUuid}`;
      }
      if (url) {
        window.open(url);
      }
    }
  }
};
</script>

<style></style>
