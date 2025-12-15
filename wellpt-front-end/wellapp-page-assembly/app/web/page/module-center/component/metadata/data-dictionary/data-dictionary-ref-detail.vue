<template>
  <div>
    <a-row style="margin-bottom: 12px">
      <a-col span="12">字典名称：{{ dataDictionary.name }}</a-col>
      <a-col span="12">字典编码：{{ dataDictionary.code }}</a-col>
    </a-row>
    <a-row>
      <a-table rowKey="uuid" :columns="refColumns" :data-source="rows" :pagination="pagination" @change="onTableChange" class="pt-table">
        <template slot="functionTypeSlot" slot-scope="text, record">
          <span v-if="record.functionType == 'appPageDefinition'">页面</span>
          <span v-if="record.functionType == 'formDefinition'">表单</span>
        </template>
        <template slot="functionNameSlot" slot-scope="text, record">
          {{ getFunctionLocationName(record) }}
        </template>
        <template slot="operationSlot" slot-scope="text, record">
          <a-button @click="viewRefFunctionDesign(record)" type="link">
            <Icon type="pticon iconfont icon-szgy-zonghechaxun"></Icon>
            查看设计
          </a-button>
        </template>
      </a-table>
    </a-row>
  </div>
</template>

<script>
export default {
  props: {
    dataDictionary: Object
  },
  inject: ['currentModule'],
  data() {
    return {
      refColumns: [
        { title: '使用功能', dataIndex: 'functionType', scopedSlots: { customRender: 'functionTypeSlot' } },
        { title: '使用位置', dataIndex: 'functionName', scopedSlots: { customRender: 'functionNameSlot' } },
        { title: '操作', width: 160, dataIndex: '__operation', scopedSlots: { customRender: 'operationSlot' } }
      ],
      rows: [],
      pagination: { pageSize: 10, current: 1, totalPages: 0, totalCount: 0 }
    };
  },
  created() {
    this.loadData();
  },
  methods: {
    onTableChange(pagination) {
      this.pagination = pagination;
      this.loadData();
    },
    loadData() {
      let criterions = [
        {
          columnIndex: 'code',
          value: this.dataDictionary.code,
          type: 'eq'
        }
      ];
      $axios
        .post(`/proxy/api/datastore/loadData`, {
          dataStoreId: 'CD_DS_DATA_DICTIONARY_REF',
          pagingInfo: { currentPage: this.pagination.current, pageSize: this.pagination.pageSize },
          criterions,
          params: {
            moduleId: this.currentModule && this.currentModule.id
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
    getFunctionLocationName(record) {
      let paths = [];
      // 当前模块的引用位置
      paths.push(this.currentModule.name);
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
