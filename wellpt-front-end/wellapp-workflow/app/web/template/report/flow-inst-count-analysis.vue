<template>
  <div class="flow-inst-count-analysis">
    <flow-report-head title="流程发起量分析" :formData="searchForm" @onSearchClick="onSearchClick" @onResetClick="onResetClick" />
    <flow-report-summary>
      <flow-report-summary-item title="发起的流程总数" :summary="summary.startCount" />
      <flow-report-summary-item
        icon="iconfont icon-ptkj-tuichudenglu-01"
        color="#3DC255"
        bgColor="#F5FCF7"
        title="已结束流程数"
        :summary="summary.completedCount"
      />
      <flow-report-summary-item
        icon="ant-iconfont deployment-unit"
        color="#FA8C16"
        bgColor="#FFF9F3"
        title="在办流程数"
        :summary="summary.uncompletedCount"
      />
    </flow-report-summary>
  </div>
</template>

<script>
import moment from 'moment';
import { getSetting } from './utils';
import { deepClone } from '@framework/vue/utils/util';
import FlowReportHead from './flow-report-head.vue';
import FlowReportSummary from './flow-report-summary.vue';
import FlowReportSummaryItem from './flow-report-summary-item.vue';

export default {
  components: {
    FlowReportHead,
    FlowReportSummary,
    FlowReportSummaryItem
  },
  inject: ['pageContext'],
  data() {
    let initSearchForm = {
      startTimeRange: [],
      flowRange: null,
      includeDisabledFlow: true
    };
    return {
      showFilter: false,
      initSearchForm,
      searchForm: deepClone(initSearchForm),
      defaultPattern: 'yyyy-MM-DD',
      summary: {
        startCount: 0,
        completedCount: 0,
        uncompletedCount: 0
      }
    };
  },
  beforeMount() {
    getSetting().then(setting => {
      this.initStartTimeRange(setting);
    });
    this.loadDataset();
  },
  beforeDestroy() {
    this.pageContext.offEvent('searchFlowInstCount');
  },
  methods: {
    loadDataset() {
      const _this = this;
      _this.summary = {
        startCount: 0,
        completedCount: 0,
        uncompletedCount: 0
      };
      this.$loading();
      $axios
        .post('/json/data/services', {
          serviceName: 'reportFacadeService',
          methodName: 'loadDataset',
          args: JSON.stringify([
            'com.wellsoft.pt.app.workflow.report.dataset.FlowEfficiencySummaryEchartDatasetLoad',
            { ..._this.searchForm }
          ])
        })
        .then(({ data: result }) => {
          this.$loading(false);
          if (result.data) {
            result.data.forEach(item => {
              item.source &&
                item.source.forEach(sourceItem => {
                  if (sourceItem.hasOwnProperty('startCount')) {
                    _this.summary.startCount = sourceItem.startCount;
                  } else if (sourceItem.hasOwnProperty('completedCount')) {
                    _this.summary.completedCount = sourceItem.completedCount;
                  } else if (sourceItem.hasOwnProperty('uncompletedCount')) {
                    _this.summary.uncompletedCount = sourceItem.uncompletedCount;
                  }
                });
            });
          }
        })
        .catch(error => {
          this.$loading(false);
        });
    },
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
      this.loadDataset();
      this.pageContext.emitEvent('searchFlowInstCount', this.searchForm);
    },
    onResetClick() {
      this.searchForm = deepClone(this.initSearchForm);
      this.loadDataset();
      this.pageContext.emitEvent('searchFlowInstCount', this.searchForm);
    },
    hideFilter() {
      this.showFilter = false;
      // this.searchForm = deepClone(this.initSearchForm);
      // this.pageContext.emitEvent('searchFlowInstCount', this.searchForm);
    }
  }
};
</script>

<style lang="less" scoped>
.card-item {
  padding: 12px;
  text-align: center;

  .icon {
    margin-right: 8px;
  }
  .label-container {
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
div[w-id='page_flow_inst_count_report'] {
  .widget-vpage-content {
    background-color: var(--w-widget-page-layout-bg-color) !important;

    div[w-id='tAhNwxsevVXeYWqpWhSBgQqfwDAlcVDe'] {
      border-radius: 2px;
    }
  }
}
</style>
