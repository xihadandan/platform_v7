<template>
  <div>
    <flow-report-head title="流程用时详细统计表" :formData="searchForm" @onSearchClick="onSearchClick" @onResetClick="onResetClick">
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
              <a-form-model-item label="流程范围" prop="flowRange">
                <FlowSelect v-model="searchForm.flowRange" style="width: 100%"></FlowSelect>
              </a-form-model-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col :span="8">
              <a-form-model-item label="流程状态" prop="flowState">
                <a-radio-group v-model="searchForm.flowState">
                  <a-radio-button value="all">全部</a-radio-button>
                  <a-radio-button value="completed">已结束</a-radio-button>
                  <a-radio-button value="uncompleted">流转中</a-radio-button>
                </a-radio-group>
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
      <flow-report-summary-item title="发起的流程总数" :summary="summary.startCount" />
      <flow-report-summary-item icon="ant-iconfont dashboard" title="流程平均用时" :summary="avgCompletedDuration" />
      <flow-report-summary-item
        icon="ant-iconfont deployment-unit"
        color="#FA8C16"
        bgColor="#FFF9F3"
        title="流转环节总数"
        :summary="summary.taskCompletedCount"
      />
      <flow-report-summary-item icon="iconfont icon-ptkj-gongzuoshijianfangan" title="环节平均用时" :summary="avgTaskDuration" />
      <flow-report-summary-item
        icon="iconfont icon-naozhong-01"
        color="#E60012"
        bgColor="#FEF2F3"
        title="平均用时最长流程"
        :summary="summary.flowNameOfMaxAvgCompletedTime"
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
      flowRange: null,
      flowState: 'all',
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
        uncompletedCount: 0,
        avgCompletedDay: 0,
        avgCompletedHour: 0,
        avgCompletedMinute: 0,
        avgTaskDurationDay: 0,
        avgTaskDurationHour: 0,
        avgTaskDurationMinute: 0,
        taskCompletedCount: 0,
        flowNameOfMaxAvgCompletedTime: ''
      },
      showFlowDetailTable: true
    };
  },
  computed: {
    // 流程平均用时
    avgCompletedDuration() {
      let time = '';
      if (this.summary.avgCompletedDay) {
        time = time + `${this.summary.avgCompletedDay}天`;
      }
      if (this.summary.avgCompletedHour) {
        time = time + `${this.summary.avgCompletedHour}小时`;
      }
      time = time + `${this.summary.avgCompletedMinute}分钟`;
      return time;
    },
    // 环节平均用时
    avgTaskDuration() {
      let time = '';
      if (this.summary.avgTaskDurationDay) {
        time = time + `${this.summary.avgTaskDurationDay}天`;
      }
      if (this.summary.avgTaskDurationHour) {
        time = time + `${this.summary.avgTaskDurationHour}小时`;
      }
      time = time + `${this.summary.avgTaskDurationMinute}分钟`;
      return time;
    }
  },
  beforeMount() {
    getSetting().then(setting => {
      this.initStartTimeRange(setting);
    });
    this.loadDataset();
    this.pageContext.handleEvent('showFlowDetailTableOfFlowEfficienty', () => {
      this.showFlowDetailTable = true;
    });
    this.pageContext.handleEvent('showTaskDetailTableOfFlowEfficienty', () => {
      this.showFlowDetailTable = false;
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
    loadDataset() {
      const _this = this;
      _this.summary = {
        startCount: 0,
        completedCount: 0,
        uncompletedCount: 0,
        avgCompletedDay: 0,
        avgCompletedHour: 0,
        avgCompletedMinute: 0,
        avgTaskDurationDay: 0,
        avgTaskDurationHour: 0,
        avgTaskDurationMinute: 0,
        taskCompletedCount: 0,
        flowNameOfMaxAvgCompletedTime: ''
      };
      this.$loading();
      $axios
        .post('/json/data/services', {
          serviceName: 'reportFacadeService',
          methodName: 'loadDataset',
          args: JSON.stringify([
            'com.wellsoft.pt.app.workflow.report.dataset.FlowEfficiencyDetailSummaryEchartDatasetLoad',
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
                  } else if (sourceItem.hasOwnProperty('avgCompletedTimeInMinute')) {
                    let avgCompletedTimeInMinute = parseFloat(sourceItem.avgCompletedTimeInMinute);
                    if (avgCompletedTimeInMinute >= 60 * 24) {
                      _this.summary.avgCompletedDay = parseInt(avgCompletedTimeInMinute / (60 * 24));
                    }
                    if (avgCompletedTimeInMinute >= 60) {
                      _this.summary.avgCompletedHour = parseInt((avgCompletedTimeInMinute - _this.summary.avgCompletedDay * 60 * 24) / 60);
                    }
                    _this.summary.avgCompletedMinute = avgCompletedTimeInMinute % 60;
                  } else if (sourceItem.hasOwnProperty('avgTaskDurationInMinute')) {
                    let avgTaskDurationInMinute = parseFloat(sourceItem.avgTaskDurationInMinute);
                    if (avgTaskDurationInMinute >= 60 * 24) {
                      _this.summary.avgTaskDurationDay = parseInt(avgTaskDurationInMinute / (60 * 24));
                    }
                    if (avgTaskDurationInMinute >= 60) {
                      _this.summary.avgTaskDurationHour = parseInt(
                        (avgTaskDurationInMinute - _this.summary.avgTaskDurationDay * 60 * 24) / 60
                      );
                    }
                    _this.summary.avgTaskDurationMinute = avgTaskDurationInMinute % 60;
                  } else if (sourceItem.hasOwnProperty('taskCompletedCount')) {
                    _this.summary.taskCompletedCount = sourceItem.taskCompletedCount;
                  } else if (sourceItem.hasOwnProperty('maxAvgCompletedTimeInMinute')) {
                    _this.summary.flowNameOfMaxAvgCompletedTime = sourceItem.name;
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
      let flowTableWidget = _this.pageContext.getVueWidgetById('jyOdGjrwdFtspEShTTMECzFaopEzjWhh');
      let taskTableWidget = _this.pageContext.getVueWidgetById('DMvilBorMuHcFouJncDcrdoRcBpNxyvw');
      for (let key in searchForm) {
        if (searchForm[key] != null) {
          flowTableWidget.addDataSourceParams({ [key]: searchForm[key] });
          taskTableWidget.addDataSourceParams({ [key]: searchForm[key] });
        } else {
          flowTableWidget.deleteDataSourceParams([key]);
          taskTableWidget.deleteDataSourceParams([key]);
        }
      }
      flowTableWidget.refetch(true);
      taskTableWidget.refetch(true);
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
    top: 12px;
    display: inline-block;
    text-align: left;
    padding-top: 8px;
    .count {
      font-size: 24px;
      font-weight: bold;
      display: inline-block;
      max-width: 220px;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
    }
  }
}
</style>

<style lang="less">
div[w-id='page_flow_efficiency_detail_report'] {
  .widget-vpage-content {
    background-color: var(--w-widget-page-layout-bg-color) !important;

    div[w-id='jULHdCpqDmtywXlRQgeSPzBLlAcvkOls'] {
      border-radius: 2px;
    }
  }
}
</style>
