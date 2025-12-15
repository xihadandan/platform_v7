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
      <template slot="logicFileInfos" slot-scope="text, record">
        <div v-for="fileInfo in record.logicFileInfos" :key="fileInfo.fileID">
          {{ fileInfo.fileName }}
        </div>
        <div v-if="record.logicFileInfos && record.logicFileInfos.length > 0">
          {{
            $t(
              'WorkflowWork.subFlowDisInfo.countFiles',
              { count: record.logicFileInfos.length },
              '共' + record.logicFileInfos.length + '个附件'
            )
          }}
          <a-button type="link" @click="onDownAllFilesClick($event, record.logicFileInfos)">
            {{ $t('WorkflowWork.opinionManager.operation.downloadAll', '全部下载') }}
          </a-button>
        </div>
      </template>
    </a-table>
  </a-card>
</template>

<script>
import { each as forEach, isEmpty, findIndex, trim as stringTrim } from 'lodash';
export default {
  name: 'WorkflowSubflowDistributeInfo',
  props: {
    workView: Object,
    subTaskData: Object,
    distributeInfo: Object,
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
    _self.dataList = _self.distributeInfo.distributeInfos || [];
    _self.pagination.total = _self.dataList.length;
  },
  methods: {
    onSearch() {
      this.loadDistributeInfosByPage(this.keyword);
    },
    getTableColumns() {
      let serialColumn = {
        title: this.$t('WorkflowWork.subFlowDisInfo.column.serialNumber', '序号'),
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
        { title: this.$t('WorkflowWork.subFlowDisInfo.column.distributor', '分发人'), dataIndex: 'distributorName' },
        { title: this.$t('WorkflowWork.subFlowDisInfo.column.distributeTime', '分发时间'), dataIndex: 'createTime' },
        { title: this.$t('WorkflowWork.subFlowDisInfo.column.distributeContent', '分发内容'), dataIndex: 'content' },
        {
          title: this.$t('WorkflowWork.subFlowDisInfo.column.attachment', '附件'),
          dataIndex: 'logicFileInfos',
          scopedSlots: { customRender: 'logicFileInfos' }
        }
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
        .post('/api/workflow/work/loadDistributeInfosByPage', {
          keyword: keyword,
          pageNum: page,
          pageSize: pageSize,
          parentFlowInstUuid: _self.distributeInfo.belongToFlowInstUuid || subTaskData.parentFlowInstUuid
        })
        .then(result => {
          let data = result.data.data;
          let dataList = data.result;
          if (dataList && dataList.length > 0) {
            _self.distributeInfoResult2Table(dataList[0], { current: data.pageNo, pageSize: 10, total: data.totalCount });
          } else {
            _self.distributeInfoResult2Table({}, { current: 1, pageSize: 0, total: 0 });
          }
          _self.loading = false;
        });
    },
    distributeInfoResult2Table(distributeInfo, pagination) {
      const _self = this;
      // 承办数据
      _self.dataList = distributeInfo.distributeInfos || [];
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
      if (!isEmpty(_self.distributeInfo.title)) {
        return _self.distributeInfo.title;
      }
      return this.$t('WorkflowWork.subFlowDisInfo.title', '信息分发') + (_self.index + 1);
    }
  }
};
</script>

<style scoped></style>
