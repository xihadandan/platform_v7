<template>
  <div>
    <flow-report-head title="流程行为分析" @onSearchClick="onSearchClick" @onResetClick="onResetClick">
      <template slot="filter">
        <a-form-model :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }" :model="searchForm" :colon="false" ref="searchForm">
          <a-row>
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
            <a-col :span="8">
              <a-form-model-item label="流程行为" prop="operationTypes">
                <a-checkbox-group v-model="searchForm.operationTypes" :options="operationTypeOptions" @change="onOperationTypesChange" />
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
    <div class="flow-operation-summary">
      <div class="_left">
        <div class="_type-title">
          低效操作
          <span>
            {{
              operationTypeOptions
                .filter(item => searchForm.operationTypes.includes(item.value))
                .map(item => item.label)
                .join('/')
            }}
          </span>
        </div>
        <div class="_content">
          <flow-report-summary-item icon="iconfont icon-luojizujian-faqiliucheng" title="发起的流程总数" :summary="summary.startCount" />
          <flow-report-summary-item icon="iconfont icon-luojizujian-chuyu" title="低效操作流程数" :summary="summary.inefficientCount" />
          <flow-report-summary-item
            icon="iconfont icon-luojizujian-chuyu"
            title="平均低效操作率"
            :summary="summary.avgInefficientPercent"
          />
          <flow-report-summary-item
            icon="iconfont icon-oa-banliguocheng"
            title="低效操作率最高流程"
            :summary="summary.flowNameOfMaxInefficientCount"
          />
        </div>
      </div>
      <div class="_right">
        <div id="inefficient-percent-chart" style="height: 120px"></div>
      </div>
    </div>
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
      operationTypes: ['Rollback', 'Transfer', 'CounterSign', 'AddSign', 'HandOver', 'GotoTask'],
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
        inefficientCount: 0,
        avgInefficientPercent: 0,
        flowNameOfMaxInefficientCount: ''
      },
      operationTypeOptions: [
        { label: '退回', value: 'Rollback' },
        { label: '转办', value: 'Transfer' },
        { label: '会签', value: 'CounterSign' },
        { label: '加签', value: 'AddSign' },
        { label: '移交', value: 'HandOver' },
        { label: '跳转', value: 'GotoTask' }
      ]
    };
  },
  beforeMount() {
    getSetting().then(setting => {
      this.initStartTimeRange(setting);
    });
    this.loadDataset();
  },
  mounted() {
    import('echarts').then(echarts => {
      this.initInefficientPercentChart(echarts);
    });
  },
  beforeDestroy() {
    this.pageContext.offEvent('searchFlowOperation');
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
        inefficientCount: 0,
        avgInefficientPercent: 0,
        flowNameOfMaxInefficientCount: ''
      };
      this.$loading();
      $axios
        .post('/json/data/services', {
          serviceName: 'reportFacadeService',
          methodName: 'loadDataset',
          args: JSON.stringify([
            'com.wellsoft.pt.app.workflow.report.dataset.FlowOperationSummaryEchartDatasetLoad',
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
                  } else if (sourceItem.hasOwnProperty('inefficientCount')) {
                    _this.summary.inefficientCount = sourceItem.inefficientCount;
                  } else if (sourceItem.hasOwnProperty('avgInefficientPercent')) {
                    _this.summary.avgInefficientPercent = sourceItem.avgInefficientPercent;
                  } else if (sourceItem.hasOwnProperty('maxInefficientCountByDefinition')) {
                    _this.summary.flowNameOfMaxInefficientCount = sourceItem.name;
                  }
                });
            });
          }
        })
        .catch(error => {
          this.$loading(false);
        });
    },
    initInefficientPercentChart(echarts) {
      const _this = this;
      _this.echartLib = echarts;
      if (!_this.echarts) {
        _this.echarts = echarts.init(_this.$el.querySelector('#inefficient-percent-chart'));
      }
      this.$loading();
      $axios
        .post('/json/data/services', {
          serviceName: 'reportFacadeService',
          methodName: 'loadDataset',
          args: JSON.stringify([
            'com.wellsoft.pt.app.workflow.report.dataset.FlowOperationInefficientActionPercentEchartDatasetLoad',
            { ..._this.searchForm }
          ])
        })
        .then(({ data: result }) => {
          this.$loading(false);
          if (result.data) {
            let option = {
              grid: {
                top: 20,
                bottom: 12,
                left: 20,
                right: 20,
                containLabel: true
              },
              legend: { show: false },
              tooltip: {
                valueFormatter(value) {
                  return value + '%';
                },
                formatter(params) {
                  return params.marker + params.data.percentName + "<span style='padding-left:20px' />" + params.data.percent + '%';
                }
              },
              dataset: result.data,
              // 声明一个 X 轴，类目轴（category）。默认情况下，类目轴对应到 dataset 第一列。
              xAxis: { type: 'category', axisLine: { show: false }, axisTick: { show: false } },
              // 声明一个 Y 轴，数值轴。
              yAxis: { show: false },
              // 声明多个 bar 系列，默认情况下，每个系列会自动对应到 dataset 的每一列。
              series: [
                {
                  type: 'bar',
                  barWidth: 16,
                  itemStyle: {
                    color: params => {
                      let color = params.color;
                      let operationType = params.data.operationType;
                      if (operationType === 'Rollback') {
                        color = '#91CC75';
                      } else if (operationType === 'Transfer') {
                        color = '#fac858';
                      } else if (operationType === 'CounterSign') {
                        color = '#ee6666';
                      } else if (operationType === 'AddSign') {
                        color = '#73c0de';
                      } else if (operationType === 'HandOver') {
                        color = '#3ba272';
                      } else if (operationType === 'GotoTask') {
                        color = '#fc8452';
                      }
                      return color;
                    }
                  }
                }
              ]
            };
            option && _this.echarts.setOption(option);
          }
        })
        .catch(error => {
          this.$loading(false);
        });
    },
    onOperationTypesChange() {
      this.echartLib && this.initInefficientPercentChart(this.echartLib);
      this.onSearchClick();
      let vueWidget = this.pageContext.getVueWidgetById('cNwdCPcPkmzeHYyvqlCdokTJikhSawUB');
      if (vueWidget) {
        if (!this.tabs) {
          this.tabs = vueWidget.tabs;
        }
        vueWidget.tabs = this.tabs.filter(tab => {
          let operationTypes = this.searchForm.operationTypes;
          if (operationTypes.includes('Rollback') && tab.id == 'tab-OjCNGkyZOOVKZqtvPOzNWJtDIeCJxFuO') {
            return true;
          }
          if (operationTypes.includes('Transfer') && tab.id == 'tab-pLPMoILPCXiTLoXCprHRKXeScuitCKsD') {
            return true;
          }
          if (operationTypes.includes('CounterSign') && tab.id == 'tab-wTcgsylkDeEFtxCGDIzRNdmMJpMYgoCD') {
            return true;
          }
          if (operationTypes.includes('AddSign') && tab.id == 'tab-hHUMUiEEidGrQUEoTXiAPpDCxsdrMiCx') {
            return true;
          }
          if (operationTypes.includes('HandOver') && tab.id == 'tab-fLxKsSukYAwcCwnhClocMOOYNnQMpLFF') {
            return true;
          }
          if (operationTypes.includes('GotoTask') && tab.id == 'tab-KVRbckMmUlbfDLlkEIlwrTAKpGbJrvdC') {
            return true;
          }
          return false;
        });
        if (vueWidget.tabs.length > 0) {
          vueWidget.activeKey = vueWidget.tabs[0].id;
        }
      }
    },
    onSearchClick() {
      this.loadDataset();
      this.echartLib && this.initInefficientPercentChart(this.echartLib);
      this.pageContext.emitEvent('searchFlowOperation', this.searchForm);
    },
    onResetClick() {
      this.searchForm = deepClone(this.initSearchForm);
      this.loadDataset();
      this.echartLib && this.initInefficientPercentChart(this.echartLib);
      this.pageContext.emitEvent('searchFlowOperation', this.searchForm);
    },
    hideFilter() {
      this.showFilter = false;
      // this.searchForm = deepClone(this.initSearchForm);
      // this.loadDataset();
      // this.pageContext.emitEvent('searchFlowOperation', this.searchForm);
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
div[w-id='page_flow_operation_report'] {
  .widget-vpage-content {
    color: var(--w-text-color-darker);
    background-color: var(--w-widget-page-layout-bg-color) !important;

    div[w-id='BrqwCOPiLQkrRHVDvwkTviPgpQErTHwM'],
    div[w-id='cNwdCPcPkmzeHYyvqlCdokTJikhSawUB'] {
      border-radius: 2px;
    }
  }
}
.flow-operation-summary {
  display: flex;
  margin-bottom: 12px;
  > ._left {
    flex: 1 1 auto;

    margin-right: 6px;
    background-color: var(--w-color-white);
    > ._type-title {
      padding-top: 14px;
      padding-left: 20px;
      font-weight: bold;
      span {
        font-size: 12px;
        font-weight: normal;
      }
    }
    > ._content {
      display: flex;
    }
  }
  > ._right {
    flex: 0 0 405px;
    margin-left: 6px;
    background-color: var(--w-color-white);
    #inefficient-percent-chart {
      width: 405px;
    }
  }
  ._summary-item {
    flex: 1;
    padding: 0;
    margin: 17px 20px;
    border-right: solid 1px #e5e5e5;
    &:last-child {
      border-right: none;
    }
  }
}
</style>
