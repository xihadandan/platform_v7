<template>
  <a-card :title="title" class="subflow-distribute-operation">
    <a-row class="keyword-query-container">
      <a-col span="6">
        <a-input-search
          v-model="keyword"
          :placeholder="$t('WorkflowWork.keywordPlaceholder', '关键字')"
          :enter-button="$t('WorkflowWork.operation.searchBtnText', '查询')"
          :allowClear="true"
          @search="onSearch"
        />
      </a-col>
    </a-row>
    <a-table
      :columns="getTableColumns()"
      :row-key="record => record.uuid"
      :data-source="dataList"
      :pagination="pagination"
      :loading="loading"
      @change="onTableChange"
      class="pt-table"
    >
      <template slot="_order" slot-scope="text, record, index">
        {{ index + 1 }}
      </template>
      <template slot="actionSlot" slot-scope="text, record">
        {{ $t('WorkflowWork.operation.' + record.actionType, record.action) }}
      </template>
    </a-table>
  </a-card>
</template>

<script>
import { each as forEach, isEmpty, findIndex, trim as stringTrim } from 'lodash';
export default {
  name: 'WorkflowSubflowRelateOperation',
  props: {
    workView: Object,
    subTaskData: Object,
    relateOperation: Object,
    index: Number
  },
  data() {
    return {
      keyword: '',
      loading: false,
      dataList: [],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0
      }
    };
  },
  created() {
    const _self = this;
    _self.dataList = _self.relateOperation.operations || [];
    _self.pagination.total = _self.dataList.length;
  },
  methods: {
    onSearch() {
      this.loadDistributeInfosByPage(this.keyword);
    },
    getTableColumns() {
      const serialColumn = {
        title: this.$t('WorkflowWork.subFlowRelateOperation.column.serialNumber', '序号'),
        dataIndex: '_order',
        width: '60px',
        align: 'center',
        scopedSlots: { customRender: '_order' }
      };
      if (this.$i18n.locale.startsWith('en')) {
        serialColumn.width = '130px';
      }
      let columns = [
        serialColumn,
        { title: this.$t('WorkflowWork.subFlowRelateOperation.column.assigneeName', '操作人员'), dataIndex: 'assigneeName' },
        {
          title: this.$t('WorkflowWork.subFlowRelateOperation.column.actionName', '操作名称'),
          dataIndex: 'action',
          scopedSlots: { customRender: 'actionSlot' }
        },
        { title: this.$t('WorkflowWork.subFlowRelateOperation.column.userName', '操作对象'), dataIndex: 'userName' },
        { title: this.$t('WorkflowWork.subFlowRelateOperation.column.createTime', '操作时间'), dataIndex: 'createTime' },
        { title: this.$t('WorkflowWork.subFlowRelateOperation.column.detail', '详情'), dataIndex: 'opinionText' }
      ];
      return columns;
    },
    onTableChange(pagination) {
      this.loadDistributeInfosByPage(this.keyword, pagination);
    },
    loadDistributeInfosByPage(keyword = '', pagination = {}) {
      const _self = this;
      let subTaskData = _self.subTaskData;
      let page = pagination.current || 1;
      let pageSize = pagination.pageSize || 10;
      _self.loading = true;
      $axios
        .post('/api/workflow/work/loadRelateOperationByPage', {
          keyword: keyword,
          pageNum: page,
          pageSize: pageSize,
          flowInstUuid: _self.relateOperation.belongToFlowInstUuid || subTaskData.flowInstUuid
        })
        .then(result => {
          let data = result.data.data;
          let dataList = data.result;
          if (dataList && dataList.length > 0) {
            _self.relateOperationResult2Table(dataList[0], { current: data.pageNo, pageSize: 10, total: data.totalCount });
          } else {
            _self.relateOperationResult2Table({}, { current: 1, pageSize: 0, total: 0 });
          }
          _self.loading = false;
        });
    },
    relateOperationResult2Table(relateOperation, pagination) {
      const _self = this;
      // 承办数据
      _self.dataList = relateOperation.operations || [];
      // 分页信息
      _self.pagination = pagination;
    },
    onDownAllFilesClick(event, logicFileInfos) {
      let fileIds = [];
      forEach(logicFileInfos, function (fileInfo) {
        fileIds.push(fileInfo.fileID);
      });
      this.workView.downloadFilesByFileIds(fileIds);
    }
  },
  computed: {
    title() {
      const _self = this;
      if (!isEmpty(_self.relateOperation.title)) {
        return _self.relateOperation.title;
      }
      return this.$t('WorkflowWork.subFlowRelateOperation.title', '操作记录') + (_self.index + 1);
    }
  }
};
</script>

<style scoped></style>
