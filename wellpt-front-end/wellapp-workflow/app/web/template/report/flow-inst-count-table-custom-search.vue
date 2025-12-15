<template>
  <div>
    <flow-report-head title="流程分类流程量统计" @onSearchClick="onSearchClick" @onResetClick="onResetClick">
      <template slot="filter">
        <a-form-model :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }" :model="searchForm" :colon="false" ref="searchForm">
          <a-row>
            <a-col :span="12">
              <a-form-model-item label="发起时间范围" prop="startTimeRange">
                <a-range-picker :format="defaultPattern" :valueFormat="defaultPattern" v-model="searchForm.startTimeRange" />
              </a-form-model-item>
            </a-col>
            <a-col :span="12">
              <a-form-model-item label="结束时间范围" prop="endTimeRange">
                <a-range-picker :format="defaultPattern" :valueFormat="defaultPattern" v-model="searchForm.endTimeRange" />
              </a-form-model-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col :span="12">
              <a-form-model-item label="流程范围" prop="flowRange">
                <FlowSelect v-model="searchForm.flowRange" style="width: 100%"></FlowSelect>
              </a-form-model-item>
            </a-col>
            <a-col :span="12">
              <a-form-model-item label="包含已停用流程数据" prop="includeDisabledFlow">
                <a-switch v-model="searchForm.includeDisabledFlow" />
              </a-form-model-item>
            </a-col>
          </a-row>
        </a-form-model>
      </template>
    </flow-report-head>
    <!-- 
     <a-button type="link" icon="caret-down" @click="expandTable">展开</a-button>
        <a-button type="link" icon="caret-right" @click="collapseTable">折叠</a-button>
    -->
  </div>
</template>

<script>
import moment from 'moment';
import { getSetting } from './utils';
import { deepClone } from '@framework/vue/utils/util';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import FlowSelect from '@workflow/app/web/lib/flow-select.vue';
import FlowReportHead from './flow-report-head.vue';

export default {
  components: {
    OrgSelect,
    FlowSelect,
    FlowReportHead
  },
  inject: ['pageContext'],
  data() {
    let initSearchForm = {
      startTimeRange: [],
      endTimeRange: [],
      flowRange: null,
      includeDisabledFlow: true
    };
    return {
      showFilter: false,
      initSearchForm,
      searchForm: deepClone(initSearchForm),
      defaultPattern: 'yyyy-MM-DD'
    };
  },
  beforeMount() {
    getSetting().then(setting => {
      this.initStartTimeRange(setting);
    });
  },
  methods: {
    initStartTimeRange(setting) {
      let recentStartTimeRange = (setting && setting.search && setting.search.recentStartTimeRange) || 30;
      let from = moment();
      from.add(-recentStartTimeRange, 'day');
      let fromTime = from.format(this.defaultPattern);
      let endTime = moment().format(this.defaultPattern);
      this.initSearchForm.startTimeRange = [fromTime, endTime];
      this.searchForm.startTimeRange = [fromTime, endTime];
    },
    onSearchClick() {
      // this.pageContext.emitEvent('searchFlowInstCount', this.searchForm);
      this.refreshTable(this.searchForm);
    },
    onResetClick() {
      this.searchForm = deepClone(this.initSearchForm);
      // this.pageContext.emitEvent('searchFlowInstCount', this.searchForm);
      this.refreshTable(this.searchForm);
    },
    hideFilter() {
      this.showFilter = false;
      // this.searchForm = deepClone(this.initSearchForm);
      // // this.pageContext.emitEvent('searchFlowInstCount', this.searchForm);
      // this.refreshTable(this.searchForm);
    },
    refreshTable(searchForm) {
      const _this = this;
      let tableWidget = _this.pageContext.getVueWidgetById('aBwigyrSHxUUoZdqBcVGucjCTmjsxhjb');
      for (let key in searchForm) {
        if (searchForm[key] != null) {
          tableWidget.addDataSourceParams({ [key]: searchForm[key] });
        } else {
          tableWidget.deleteDataSourceParams([key]);
        }
      }
      tableWidget.refetch(true);
    },
    expandTable() {
      const _this = this;
      let tableWidget = _this.pageContext.getVueWidgetById('aBwigyrSHxUUoZdqBcVGucjCTmjsxhjb');
      let dataSourceProvider = tableWidget.getDataSourceProvider();
      if (dataSourceProvider && dataSourceProvider.data && dataSourceProvider.data.data) {
        tableWidget.expandedRowKeys = dataSourceProvider.data.data.map(item => item.uuid);
      }
    },
    collapseTable() {
      const _this = this;
      let tableWidget = _this.pageContext.getVueWidgetById('aBwigyrSHxUUoZdqBcVGucjCTmjsxhjb');
      tableWidget.expandedRowKeys = [];
    }
  }
};
</script>

<style lang="less">
div[w-id='page_flow_inst_count_by_category_report'] {
  .widget-vpage-content {
    background-color: var(--w-widget-page-layout-bg-color) !important;
    .ant-table-row-expand-icon {
      font-family: 'ant-iconfont';
      border: none;
      background: transparent;
    }
    .ant-table-row-collapsed {
      &::after {
        content: '\e7c3';
      }
    }
    .ant-table-row-expanded {
      &::after {
        content: '\e7c7';
      }
    }
  }
}
</style>
