<template>
  <div>
    <flow-report-head title="人员逾期率统计表" @onSearchClick="onSearchClick" @onResetClick="onResetClick">
      <template slot="filter">
        <a-form-model :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }" :model="searchForm" :colon="false" ref="searchForm">
          <a-row>
            <a-col :span="8">
              <a-form-model-item label="人员范围" prop="userRange">
                <OrgSelect v-model="searchForm.userRange" />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="发起时间范围" prop="startTimeRange">
                <a-range-picker :format="defaultPattern" :valueFormat="defaultPattern" v-model="searchForm.startTimeRange" />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="接收时间范围" prop="startTimeRange">
                <a-range-picker :format="defaultPattern" :valueFormat="defaultPattern" v-model="searchForm.arriveTimeRange" />
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
              <a-form-model-item label="包含已停用流程数据" prop="includeDisabledFlow">
                <a-switch v-model="searchForm.includeDisabledFlow" />
              </a-form-model-item>
            </a-col>
          </a-row>
        </a-form-model>
      </template>
    </flow-report-head>
    <flow-report-summary>
      <flow-report-summary-item title="统计人数" icon="iconfont icon-luojizujian-huoquyonghuliebiao" :summary="summary.userCount" />
      <flow-report-summary-item
        icon="iconfont icon-luojizujian-huoquyonghuliebiao"
        color="#E60012"
        bgColor="#FEF2F3"
        title="逾期人数"
        :summary="summary.overdueUserCount"
      />
      <flow-report-summary-item
        icon="iconfont icon-ptkj-daoqidaoqijian"
        color="#FA8C16"
        bgColor="#FFF9F3"
        title="人员平均逾期时长"
        :summary="avgOverdueDuration"
      />
      <flow-report-summary-item
        icon="iconfont icon-ptkj-daoqidaoqijian"
        color="#E60012"
        bgColor="#FEF2F3"
        title="人员平均逾期次数"
        :summary="summary.avgOverdueCount"
      />
    </flow-report-summary>
  </div>
</template>

<script>
import moment from 'moment';
import { getSetting } from './utils';
import reportMixin from './report.mixin';
import { deepClone } from '@framework/vue/utils/util';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import FlowSelect from '@workflow/app/web/lib/flow-select.vue';
import FlowReportHead from './flow-report-head.vue';
import FlowReportSummary from './flow-report-summary.vue';
import FlowReportSummaryItem from './flow-report-summary-item.vue';

export default {
  mixins: [reportMixin],
  components: {
    OrgSelect,
    FlowSelect,
    FlowReportHead,
    FlowReportSummary,
    FlowReportSummaryItem
  },
  inject: ['pageContext'],
  data() {
    let userRange = this.getCurrentUserDeptId();
    let initSearchForm = {
      deptLevel: 'all',
      userRange,
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
        userCount: 0,
        overdueUserCount: 0,
        avgOverdueDay: 0,
        avgOverdueHour: 0,
        avgOverdueMinute: 0,
        avgOverdueCount: 0,
        flowNameOfAvgMaxOverdueTime: ''
      }
    };
  },
  computed: {
    // 人员平均逾期时长
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
  },
  mounted() {
    setTimeout(() => {
      this.loadDataset(this.searchForm);
      this.refreshTable(this.searchForm);
    }, 500);
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
      this.loadDataset(this.searchForm);
      this.refreshTable(this.searchForm);
    },
    onResetClick() {
      this.searchForm = deepClone(this.initSearchForm);
      this.loadDataset(this.searchForm);
      this.refreshTable(this.searchForm);
    },
    hideFilter() {
      this.showFilter = false;
    },
    refreshTable(searchForm) {
      const _this = this;
      let tableWidget = _this.pageContext.getVueWidgetById('lPqKaRvGmbsiMCRGRGYPtNpHOhgNmpOQ');
      for (let key in searchForm) {
        if (searchForm[key] != null) {
          tableWidget.addDataSourceParams({ [key]: searchForm[key] });
        } else {
          tableWidget.deleteDataSourceParams([key]);
        }
      }
      tableWidget.refetch(true);
    },
    loadDataset(searchForm = {}) {
      const _this = this;
      _this.summary = {
        userCount: 0,
        overdueUserCount: 0,
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
            'com.wellsoft.pt.app.workflow.report.dataset.FlowHandleOverdueByUserSummaryEchartDatasetLoad',
            { ...searchForm }
          ])
        })
        .then(({ data: result }) => {
          this.$loading(false);
          if (result.data) {
            result.data.forEach(item => {
              item.source &&
                item.source.forEach(sourceItem => {
                  if (sourceItem.hasOwnProperty('userCount')) {
                    _this.summary.userCount = sourceItem.userCount;
                  } else if (sourceItem.hasOwnProperty('overdueUserCount')) {
                    _this.summary.overdueUserCount = sourceItem.overdueUserCount;
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
div[w-id='page_flow_user_overdue_rate_report'] {
  .widget-vpage-content {
    background-color: var(--w-widget-page-layout-bg-color) !important;
  }
}
</style>
