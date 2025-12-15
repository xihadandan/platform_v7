<template>
  <div>
    <flow-report-head title="流程逾期分析" @onSearchClick="onSearchClick" @onResetClick="onResetClick">
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
      <flow-report-summary-item title="流程总数" :summary="summary.startCount" />
      <flow-report-summary-item
        icon="iconfont icon-ptkj-daoqidaoqijian"
        color="#E60012"
        bgColor="#FEF2F3"
        title="逾期流程数"
        :summary="summary.overdueCount"
      />
      <flow-report-summary-item icon="iconfont icon-ptkj-bingtu-01" color="#E60012" bgColor="#FEF2F3">
        <template slot="titleAndSummary">
          <div class="_title">逾期流程占比</div>
          <div style="color: #e60012; font-size: 20px; font-weight: bold">{{ `${summary.overduePercent}%` }}</div>
        </template>
      </flow-report-summary-item>
      <flow-report-summary-item icon="iconfont icon-ptkj-gongzuoshijianfangan" title="最大逾期时长" :summary="maxOverdueDuration" />
      <flow-report-summary-item icon="ant-iconfont dashboard" title="平均逾期时长" :summary="avgOverdueDuration" />
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
      startTimeRange: [],
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
        startCount: 0,
        overdueCount: 0,
        overduePercent: 0,
        maxOverdueDay: 0,
        maxOverdueHour: 0,
        maxOverdueMinute: 0,
        avgOverdueDay: 0,
        avgOverdueHour: 0,
        avgOverdueMinute: 0
      }
    };
  },
  computed: {
    // 最大逾期时长
    maxOverdueDuration() {
      let time = '';
      if (this.summary.maxOverdueDay) {
        time = time + `${this.summary.maxOverdueDay}天`;
      }
      if (this.summary.maxOverdueHour) {
        time = time + `${this.summary.maxOverdueHour}小时`;
      }
      time = time + `${this.summary.maxOverdueMinute}分钟`;
      return time;
    },
    // 平均逾期时长
    avgOverdueDuration() {
      let time = '';
      if (this.summary.avgOverdueDay) {
        time = time + `${this.summary.avgOverdueDay}天`;
      }
      if (this.summary.avgOverdueHour) {
        time = time + `${this.summary.avgOverdueHour}小时`;
      }
      time = time + `${this.summary.avgOverdueMinute}分钟`;
      return time;
    }
  },
  beforeMount() {
    getSetting().then(setting => {
      this.initStartTimeRange(setting);
    });
    this.loadDataset();
  },
  beforeDestroy() {
    this.pageContext.offEvent('searchFlowOverdue');
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
        startCount: 0,
        overdueCount: 0,
        overduePercent: 0,
        maxOverdueDay: 0,
        maxOverdueHour: 0,
        maxOverdueMinute: 0,
        avgOverdueDay: 0,
        avgOverdueHour: 0,
        avgOverdueMinute: 0
      };
      this.$loading();
      $axios
        .post('/json/data/services', {
          serviceName: 'reportFacadeService',
          methodName: 'loadDataset',
          args: JSON.stringify(['com.wellsoft.pt.app.workflow.report.dataset.FlowOverdueSummaryEchartDatasetLoad', { ..._this.searchForm }])
        })
        .then(({ data: result }) => {
          this.$loading(false);
          if (result.data) {
            result.data.forEach(item => {
              item.source &&
                item.source.forEach(sourceItem => {
                  if (sourceItem.hasOwnProperty('startCount')) {
                    _this.summary.startCount = sourceItem.startCount;
                  } else if (sourceItem.hasOwnProperty('flowOverdueCount')) {
                    _this.summary.overdueCount = sourceItem.flowOverdueCount;
                  } else if (sourceItem.hasOwnProperty('overduePercent')) {
                    _this.summary.overduePercent = sourceItem.overduePercent;
                  } else if (sourceItem.hasOwnProperty('maxOverdueTimeInMinute')) {
                    let maxOverdueTimeInMinute = parseFloat(sourceItem.maxOverdueTimeInMinute);
                    if (maxOverdueTimeInMinute >= 60 * 24) {
                      _this.summary.maxOverdueDay = parseInt(maxOverdueTimeInMinute / (60 * 24));
                    }
                    if (maxOverdueTimeInMinute >= 60) {
                      _this.summary.maxOverdueHour = parseInt((maxOverdueTimeInMinute - _this.summary.maxOverdueDay * 60 * 24) / 60);
                    }
                    _this.summary.maxOverdueMinute = maxOverdueTimeInMinute % 60;
                  } else if (sourceItem.hasOwnProperty('avgOverdueTimeInMinute')) {
                    let avgOverdueTimeInMinute = parseFloat(sourceItem.avgOverdueTimeInMinute);
                    if (avgOverdueTimeInMinute >= 60 * 24) {
                      _this.summary.avgOverdueDay = parseInt(avgOverdueTimeInMinute / (60 * 24));
                    }
                    if (avgOverdueTimeInMinute >= 60) {
                      _this.summary.avgOverdueHour = parseInt((avgOverdueTimeInMinute - _this.summary.avgOverdueDay * 60 * 24) / 60);
                    }
                    _this.summary.avgOverdueMinute = avgOverdueTimeInMinute % 60;
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
      this.pageContext.emitEvent('searchFlowOverdue', this.searchForm);
    },
    onResetClick() {
      this.searchForm = deepClone(this.initSearchForm);
      this.loadDataset();
      this.pageContext.emitEvent('searchFlowOverdue', this.searchForm);
    },
    hideFilter() {
      this.showFilter = false;
      // this.searchForm = deepClone(this.initSearchForm);
      // this.loadDataset();
      // this.pageContext.emitEvent('searchFlowOverdue', this.searchForm);
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
div[w-id='page_flow_overdue_report'] {
  .widget-vpage-content {
    background-color: var(--w-widget-page-layout-bg-color) !important;

    div[w-id='dppPmZEkXtyLXLkkFyHYQcVJYRHCvLSl'],
    div[w-id='zavKVXsysAGeCwPspIYSzzqmHOqqWwfS'] {
      border-radius: 2px;
    }
  }
}
</style>
