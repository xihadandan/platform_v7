<template>
  <div>
    <flow-report-summary>
      <flow-report-summary-item title="统计部门数" icon="iconfont icon-wsbs-xuanzebumen" :summary="summary.deptCount" />
      <flow-report-summary-item
        icon="iconfont icon-oa-qingxiujiatongji"
        color="#3DC255"
        bgColor="#F5FCF7"
        title="部门平均操作用时"
        :summary="avgHandleDuration"
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
        avgHandleDay: 0,
        avgHandleHour: 0,
        avgHandleMinute: 0
      }
    };
  },
  computed: {
    // 部门平均操作用时
    avgHandleDuration() {
      let time = '';
      if (this.summary.avgHandleDay) {
        time = time + `${this.summary.avgHandleDay}天`;
      }
      if (this.summary.avgHandleHour) {
        time = time + `${this.summary.avgHandleHour}小时`;
      }
      time = time + `${this.summary.avgHandleMinute}分钟`;
      return time;
    }
  },
  beforeMount() {
    this.pageContext.handleEvent('searchDeptFlowHandleEfficiency', searchForm => {
      this.loadDataset(searchForm);
    });
  },
  methods: {
    loadDataset(searchForm = {}) {
      const _this = this;
      _this.summary = {
        deptCount: 0,
        avgHandleDay: 0,
        avgHandleHour: 0,
        avgHandleMinute: 0
      };
      this.$loading();
      $axios
        .post('/json/data/services', {
          serviceName: 'reportFacadeService',
          methodName: 'loadDataset',
          args: JSON.stringify([
            'com.wellsoft.pt.app.workflow.report.dataset.FlowHandleEfficiencyByDeptSummaryEchartDatasetLoad',
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
                  } else if (sourceItem.hasOwnProperty('avgHandleTimeInMinute')) {
                    let avgHandleTimeInMinute = parseFloat(sourceItem.avgHandleTimeInMinute);
                    if (avgHandleTimeInMinute >= 60 * 24) {
                      _this.summary.avgHandleDay = parseInt(avgHandleTimeInMinute / (60 * 24));
                    }
                    if (avgHandleTimeInMinute >= 60) {
                      _this.summary.avgHandleHour = parseInt((avgHandleTimeInMinute - _this.summary.avgHandleDay * 60 * 24) / 60);
                    }
                    _this.summary.avgHandleMinute = avgHandleTimeInMinute % 60;
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
