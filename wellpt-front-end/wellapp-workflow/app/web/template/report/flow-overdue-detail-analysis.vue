<template>
  <div>
    <flow-report-head title="流程逾期详细统计表" @onSearchClick="onSearchClick" @onResetClick="onResetClick">
      <template slot="filter">
        <a-form-model :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }" :model="searchForm" :colon="false" ref="searchForm">
          <a-row>
            <a-col :span="8">
              <a-form-model-item label="发起人范围" prop="startUserRange">
                <OrgSelect v-model="searchForm.startUserRange" />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="发起时间范围" prop="startTimeRange">
                <a-range-picker :format="defaultPattern" :valueFormat="defaultPattern" v-model="searchForm.startTimeRange" />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="结束时间范围" prop="endTimeRange">
                <a-range-picker :format="defaultPattern" :valueFormat="defaultPattern" v-model="searchForm.endTimeRange" />
              </a-form-model-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col :span="8">
              <a-form-model-item label="流程范围" prop="flowRange">
                <FlowSelect v-model="searchForm.flowRange" style="width: 100%"></FlowSelect>
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="仅统计计时流程" prop="onlyIncludeTimingFlow">
                <a-switch v-model="searchForm.onlyIncludeTimingFlow" />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="包含已停用流程数据" prop="includeDisabledFlow">
                <a-switch v-model="searchForm.includeDisabledFlow" />
              </a-form-model-item>
            </a-col>
          </a-row>
        </a-form-model>
      </template>
    </flow-report-head>
    <flow-report-summary>
      <flow-report-summary-item title="逾期流程" :summary="summary.overdueCount" />
      <flow-report-summary-item
        icon="iconfont icon-ptkj-daoqidaoqijian"
        color="#E60012"
        bgColor="#FEF2F3"
        title="逾期最多流程"
        :summary="summary.flowNameOfMaxOverdueCount"
      />
      <flow-report-summary-item
        icon="iconfont icon-naozhong-01"
        color="#FA8C16"
        bgColor="#FFF9F3"
        title="逾期最长流程"
        :summary="summary.flowNameOfMaxOverdueTime"
      />
    </flow-report-summary>
  </div>
</template>

<script>
import moment from 'moment';
import { getSetting } from './utils';
import { deepClone } from '@framework/vue/utils/util';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import FlowSelect from '@workflow/app/web/lib/flow-select.vue';
import FlowReportHead from './flow-report-head.vue';
import FlowReportSummary from './flow-report-summary.vue';
import FlowReportSummaryItem from './flow-report-summary-item.vue';

export default {
  components: {
    OrgSelect,
    FlowSelect,
    FlowReportHead,
    FlowReportSummary,
    FlowReportSummaryItem
  },
  inject: ['pageContext'],
  data() {
    let initSearchForm = {
      startUserRange: null,
      startTimeRange: [],
      endTimeRange: [],
      flowRange: null,
      onlyIncludeTimingFlow: true,
      includeDisabledFlow: true
    };
    return {
      showFilter: false,
      initSearchForm,
      searchForm: deepClone(initSearchForm),
      defaultPattern: 'yyyy-MM-DD',
      summary: {
        overdueCount: 0,
        flowNameOfMaxOverdueCount: '',
        flowNameOfMaxOverdueTime: ''
      }
    };
  },
  beforeMount() {
    getSetting().then(setting => {
      this.initStartTimeRange(setting);
    });
    this.loadDataset();
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
    loadDataset() {
      const _this = this;
      _this.summary = {
        overdueCount: 0,
        flowNameOfMaxOverdueCount: '',
        flowNameOfMaxOverdueTime: ''
      };
      this.$loading();
      $axios
        .post('/json/data/services', {
          serviceName: 'reportFacadeService',
          methodName: 'loadDataset',
          args: JSON.stringify([
            'com.wellsoft.pt.app.workflow.report.dataset.FlowOverdueDetailSummaryEchartDatasetLoad',
            { ..._this.searchForm }
          ])
        })
        .then(({ data: result }) => {
          this.$loading(false);
          if (result.data) {
            result.data.forEach(item => {
              item.source &&
                item.source.forEach(sourceItem => {
                  if (sourceItem.hasOwnProperty('flowOverdueCount')) {
                    _this.summary.overdueCount = sourceItem.flowOverdueCount;
                  } else if (sourceItem.hasOwnProperty('maxOverdueCountByDefinition')) {
                    _this.summary.flowNameOfMaxOverdueCount = sourceItem.name;
                  } else if (sourceItem.hasOwnProperty('maxOverdueTimeInMinute')) {
                    _this.summary.flowNameOfMaxOverdueTime = sourceItem.name;
                  }
                });
            });
          }
        })
        .catch(error => {
          this.$loading(false);
        });
    },
    onSearchClick() {
      this.loadDataset();
      this.refreshTable(this.searchForm);
    },
    onResetClick() {
      this.searchForm = deepClone(this.initSearchForm);
      this.loadDataset();
      this.refreshTable(this.searchForm);
    },
    hideFilter() {
      this.showFilter = false;
      // this.searchForm = deepClone(this.initSearchForm);
      // this.loadDataset();
      // this.refreshTable(this.searchForm);
    },
    refreshTable(searchForm) {
      const _this = this;
      let tableWidget = _this.pageContext.getVueWidgetById('pmvzFZRXtnpXCAfTckfSrdiXykrqcSKS');
      for (let key in searchForm) {
        if (searchForm[key] != null) {
          tableWidget.addDataSourceParams({ [key]: searchForm[key] });
        } else {
          tableWidget.deleteDataSourceParams([key]);
        }
      }
      tableWidget.refetch(true);
    }
  }
};
</script>

<style lang="less" scoped>
.card-item {
  padding: 12px;
  text-align: center;
  position: relative;

  .icon {
    margin-right: 8px;
  }
  .label-container {
    position: relative;
    top: 6px;
    display: inline-block;
    text-align: left;
    padding-top: 8px;
    .count {
      font-size: 24px;
      font-weight: bold;
    }
  }
}
</style>

<style lang="less">
div[w-id='page_flow_overdue_detail_report'] {
  .widget-vpage-content {
    background-color: var(--w-widget-page-layout-bg-color) !important;
  }
}
</style>
