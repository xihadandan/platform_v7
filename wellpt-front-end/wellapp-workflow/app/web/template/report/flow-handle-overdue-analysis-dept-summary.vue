<template>
  <div>
    <flow-report-summary>
      <flow-report-summary-item title="统计部门数" icon="iconfont icon-wsbs-xuanzebumen" :summary="summary.deptCount" />
      <flow-report-summary-item
        icon="iconfont icon-wsbs-xuanzebumen"
        color="#E60012"
        bgColor="#FEF2F3"
        title="逾期部门数"
        :summary="summary.overdueDeptCount"
      />
      <flow-report-summary-item
        icon="iconfont icon-ptkj-daoqidaoqijian"
        color="#FA8C16"
        bgColor="#FFF9F3"
        title="部门平均逾期时长"
        :summary="avgOverdueDuration"
      />
      <flow-report-summary-item
        icon="iconfont icon-ptkj-daoqidaoqijian"
        color="#E60012"
        bgColor="#FEF2F3"
        title="部门平均逾期次数"
        :summary="summary.avgOverdueCount"
      />
      <flow-report-summary-item
        icon="iconfont icon-naozhong-01"
        color="#FA8C16"
        bgColor="#FFF9F3"
        title="平均逾期时长最长流程"
        :summary="summary.flowNameOfAvgMaxOverdueTime"
      />
    </flow-report-summary>
  </div>
</template>

<script>
import { deepClone } from '@framework/vue/utils/util';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import FlowSelect from '@workflow/app/web/lib/flow-select.vue';
import FlowReportSummary from './flow-report-summary.vue';
import FlowReportSummaryItem from './flow-report-summary-item.vue';

export default {
  components: {
    OrgSelect,
    FlowSelect,
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
        deptCount: 0,
        overdueDeptCount: 0,
        avgOverdueDay: 0,
        avgOverdueHour: 0,
        avgOverdueMinute: 0,
        avgOverdueCount: 0,
        flowNameOfAvgMaxOverdueTime: ''
      }
    };
  },
  computed: {
    // 部门平均逾期时长
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
    this.pageContext.handleEvent('searchDeptFlowHandleOverdue', searchForm => {
      this.loadDataset(searchForm);
    });
  },
  methods: {
    loadDataset(searchForm = {}) {
      const _this = this;
      _this.summary = {
        deptCount: 0,
        overdueDeptCount: 0,
        avgOverdueDay: 0,
        avgOverdueHour: 0,
        avgOverdueMinute: 0,
        flowNameOfAvgMaxOverdueTime: ''
      };
      this.$loading();
      $axios
        .post('/json/data/services', {
          serviceName: 'reportFacadeService',
          methodName: 'loadDataset',
          args: JSON.stringify([
            'com.wellsoft.pt.app.workflow.report.dataset.FlowHandleOverdueByDeptSummaryEchartDatasetLoad',
            { ...searchForm }
          ])
        })
        .then(({ data: result }) => {
          this.$loading(false);
          if (result.data) {
            result.data.forEach(item => {
              item.source &&
                item.source.forEach(sourceItem => {
                  if (sourceItem.hasOwnProperty('deptCount')) {
                    _this.summary.deptCount = sourceItem.deptCount;
                  } else if (sourceItem.hasOwnProperty('overdueDeptCount')) {
                    _this.summary.overdueDeptCount = sourceItem.overdueDeptCount;
                  } else if (sourceItem.hasOwnProperty('avgOverdueTimeInMinute')) {
                    let avgOverdueTimeInMinute = parseFloat(sourceItem.avgOverdueTimeInMinute);
                    if (avgOverdueTimeInMinute >= 60 * 24) {
                      _this.summary.avgOverdueDay = parseInt(avgOverdueTimeInMinute / (60 * 24));
                    }
                    if (avgOverdueTimeInMinute >= 60) {
                      _this.summary.avgOverdueHour = parseInt((avgOverdueTimeInMinute - _this.summary.avgOverdueDay * 60 * 24) / 60);
                    }
                    _this.summary.avgOverdueMinute = avgOverdueTimeInMinute % 60;
                  } else if (sourceItem.hasOwnProperty('avgOverdueCount')) {
                    _this.summary.avgOverdueCount = sourceItem.avgOverdueCount;
                  } else if (sourceItem.hasOwnProperty('avgMaxOverdueTimeInMinute')) {
                    _this.summary.flowNameOfAvgMaxOverdueTime = sourceItem.name;
                  }
                });
            });
          }
        })
        .catch(error => {
          this.$loading(false);
        });
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
