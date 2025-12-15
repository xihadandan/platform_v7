<template>
  <div class="identity-replace-result">
    <template v-if="displayState == 'label'">
      <div class="row-header-label">
        <span>批量查找替换</span>
      </div>
    </template>
    <div class="identity-replace-result-message">
      {{ modifyMode }}完成！{{ modifyMode }}流程数据
      <span class="total">{{ totalCount }}</span>
      条，{{ modifyMode }}成功环节
      <span class="success">{{ successCount }}</span>
      个，{{ modifyMode }}失败环节
      <span class="error">{{ errorCount }}</span>
      个
      <span v-if="log">| {{ log.userName }} | {{ log.createTime }}</span>
    </div>
    <div v-if="displayState == 'label'" class="identity-replace-result-card" style="margin-bottom: 12px">
      <div class="row-header-label">
        <Icon type="iconfont icon-ptkj-wenjian" style="margin-right: 4px; font-weight: normal" />
        <span>查找内容</span>
      </div>
      <div class="row-header-content">
        <IdentityReplaceSearchForm :displayState="displayState" :searchForm="searchForm"></IdentityReplaceSearchForm>
      </div>
    </div>
    <div class="identity-replace-result-card">
      <div class="row-header-label">
        <Icon type="iconfont icon-ptkj_tihuan" style="margin-right: 4px; font-weight: normal" />
        <span>修改明细</span>
      </div>
      <div class="row-header-content">
        <a-tabs default-active-key="1" class="radio-group-style">
          <a-tab-pane key="1">
            <template slot="tab">
              成功
              <span class="success">{{ successCount }}</span>
            </template>
            <WidgetTable
              v-if="successTableWidget"
              ref="successTableWidget"
              :widget="successTableWidget"
              @beforeLoadData="beforeLoadSucessData"
              @tbodyRendered="onSuccessTbodyRendered"
            ></WidgetTable>
          </a-tab-pane>
          <a-tab-pane key="2">
            <template slot="tab">
              失败
              <span class="error">{{ errorCount }}</span>
            </template>
            <WidgetTable
              v-if="errorTableWidget"
              ref="errorTableWidget"
              :widget="errorTableWidget"
              @beforeLoadData="beforeLoadErrorData"
            ></WidgetTable>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
  </div>
</template>

<script>
import IdentityReplaceSearchForm from './identity-replace-search-form.vue';
import { deepClone } from '@framework/vue/utils/util';
export default {
  props: {
    displayState: String,
    searchForm: {
      type: Object,
      default() {
        return {};
      }
    },
    records: {
      type: Array,
      default() {
        return [];
      }
    },
    log: Object
  },
  components: { IdentityReplaceSearchForm },
  data() {
    return {
      successTableWidget: null,
      errorTableWidget: null
    };
  },
  computed: {
    totalCount() {
      return this.records.length;
    },
    successCount() {
      return this.records.filter(item => item.status == 1).length;
    },
    errorCount() {
      return this.records.filter(item => item.status == 2).length;
    },
    successRecords() {
      return this.records.filter(item => item.status == 1);
    },
    errorRecords() {
      return this.records.filter(item => item.status == 2);
    },
    modifyMode() {
      return this.searchForm.modifyMode == 'replace' ? '替换' : this.searchForm.modifyMode == 'add' ? '增加' : '删除';
    }
  },
  created() {
    this.getTableDefinition().then(tableWidget => {
      this.successTableWidget = tableWidget;
      this.errorTableWidget = deepClone(tableWidget);
      let resultMsgColumn = this.errorTableWidget.configuration.columns.find(column => column.dataIndex == 'remark');
      if (resultMsgColumn) {
        resultMsgColumn.hidden = false;
      }
    });
  },
  methods: {
    getTableDefinition() {
      // 取表格定义
      let widgetTableId = 'eQwOWciFRYimihLJciXrPTRGPNevSqqb';
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'appContextService',
            methodName: 'getAppWidgetDefinitionById',
            args: JSON.stringify([widgetTableId, false])
          })
          .then(({ data: { data = {} } }) => {
            if (data.definitionJson) {
              let tableWidgetDefinition = JSON.parse(data.definitionJson);
              resolve(tableWidgetDefinition);
            } else {
              reject(data);
            }
          })
          .catch(res => {
            reject(res);
          });
      });
    },
    beforeLoadSucessData() {
      const _this = this;
      if (!_this.successDataSourceChanged && _this.$refs.successTableWidget && _this.$refs.successTableWidget.dataSourceProvider) {
        _this.changeDataSource(_this.$refs.successTableWidget.dataSourceProvider, _this.successRecords);
        _this.successDataSourceChanged = true;
        _this.$emit('changeTableStyle', _this.$refs.successTableWidget.$el.querySelector('.ant-table-content'));
      }
    },
    onSuccessTbodyRendered({ rows = [] }) {
      const _this = this;
      let oldUserName = _this.searchForm.oldUserName;
      let newUserName = _this.searchForm.newUserName || '';
      if (oldUserName) {
        rows.forEach(row => {
          if (row.userArgValue) {
            let searchText1 = `<span style="color: var(--w-primary-color); font-weight: bold">${oldUserName}</span>|user`;
            let searchText = `<span style="color: var(--w-primary-color); font-weight: bold">${oldUserName}</span>`;
            let replaceText = `<span style="color: var(--w-primary-color); font-weight: bold">${newUserName}</span>`;
            row.userArgValue = row.userArgValue.replaceAll(searchText1, replaceText);
            row.userArgValue = row.userArgValue.replaceAll(searchText, replaceText);
          }
        });
      }
    },
    beforeLoadErrorData() {
      const _this = this;
      if (!_this.errorDataSourceChanged && _this.$refs.errorTableWidget && _this.$refs.errorTableWidget.dataSourceProvider) {
        _this.changeDataSource(_this.$refs.errorTableWidget.dataSourceProvider, _this.errorRecords);
        _this.errorDataSourceChanged = true;
        _this.$emit('changeTableStyle', _this.$refs.errorTableWidget.$el.querySelector('.ant-table-content'));
      }
    },
    changeDataSource(dataSourceProvider, records = []) {
      const _this = this;
      dataSourceProvider._loadData = function (params) {
        this.data = _this.getPageData(this.getParams(), records);
        this.totalCount = records.length;
        this.loaded = true;
        this.notifyChange(params);
        return Promise.resolve(this.data);
      };
      dataSourceProvider._loadCount = function (params) {
        this.totalCount = records.length;
        this.loaded = true;
        this.notifyChange(params);
      };
    },
    getPageData(params, records) {
      const _this = this;
      let pagingInfo = params.pagingInfo;
      let currentPage = pagingInfo.currentPage;
      let pageSize = pagingInfo.pageSize;
      let startIndex = (currentPage - 1) * pageSize;
      let dataList = [];
      for (let index = startIndex; index < startIndex + pageSize; index++) {
        let rowData = records[index];
        if (rowData) {
          dataList.push(rowData);
        }
      }
      pagingInfo.totalCount = records.length;
      pagingInfo.totalPages = Math.ceil(pagingInfo.totalCount / pageSize);
      return {
        data: dataList,
        pagination: pagingInfo
      };
    }
  }
};
</script>

<style lang="less" scoped>
.identity-replace-result {
  .row-header-label {
    font-size: var(--w-font-size-base);
    font-weight: bold;
    color: var(--w-text-color-dark);
    line-height: 32px;
  }
  .identity-replace-result-message {
    margin-bottom: 12px;
    font-size: var(--w-font-size-base);
    color: var(--w-text-color-light);
    .total {
      color: var(--w-primary-color);
      font-weight: bold;
    }
    .success {
      color: var(--w-success-color);
      font-weight: bold;
    }
    .error {
      color: var(--w-danger-color);
      font-weight: bold;
    }
  }
  .identity-replace-result-card {
    background: #fafafa;
    border-radius: 4px;
    .row-header-label {
      padding: 4px 12px;
      border-bottom: 1px solid var(--w-border-color-light);
      > i {
        color: var(--w-primary-color);
      }
    }
    .row-header-content {
      padding: 12px;
      .search-form {
        --search-form-container-row-content-flag-background: #ffffff;
      }
    }
    .radio-group-style {
      --w-tabs-radio-tab-bar-nav-width: unset;
      .ant-tabs-tab:not(.ant-tabs-tab-active) {
        .success {
          color: var(--w-success-color);
        }
        .error {
          color: var(--w-danger-color);
        }
      }
    }
  }
}
</style>
