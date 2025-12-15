<template>
  <div>
    <flow-report-head title="流程行为统计表" @onSearchClick="onSearchClick" @onResetClick="onResetClick">
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
                <a-checkbox-group v-model="searchForm.operationTypes" :options="operationTypeOptions" />
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
    <div class="flow-operation-detail-summary">
      <div class="_summary-item _total">
        <div class="_title">发起的流程总数</div>
        <div class="_summary">{{ summary.startCount }}</div>
      </div>
      <template v-if="Object.keys(operationTypeMap).length">
        <div
          class="_summary-item"
          v-for="(item, index) in searchForm.operationTypes"
          :key="index"
          :style="`background-color: ${operationTypeMap[item]['bgColor']};`"
        >
          <div class="_title">{{ operationTypeMap[item]['label'] }}次数</div>
          <div class="_summary" :style="`color: ${operationTypeMap[item]['color']};`">
            <!-- {{ summary[`${lowerFirst(item)}Count`] }} -->
            {{ summary[`${item.replace(/^./, match => match.toLowerCase())}Count`] }}
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import moment from 'moment';
import { lowerFirst } from 'lodash';
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
        rollbackCount: 0,
        transferCount: 0,
        counterSignCount: 0,
        addSignCount: 0,
        handOverCount: 0,
        gotoTaskCount: 0
      },
      operationTypeOptions: [
        { label: '退回', value: 'Rollback', bgColor: 'rgba(230,0,18,0.05)', color: '#E60012', count: 'rollbackCount' },
        { label: '转办', value: 'Transfer', bgColor: 'rgba(22,119,255,0.05)', color: '#1677FF' },
        { label: '会签', value: 'CounterSign', bgColor: 'rgba(34,181,175,0.05)', color: '#22B5AF' },
        { label: '加签', value: 'AddSign', bgColor: 'rgba(103,50,243,0.05)', color: '#6732F3' },
        { label: '移交', value: 'HandOver', bgColor: 'rgba(250,140,22,0.05)', color: '#FA8C16' },
        { label: '跳转', value: 'GotoTask', bgColor: 'rgba(61,194,85,0.05)', color: '#3DC255' }
      ],
      operationTypeMap: {}
    };
  },
  beforeMount() {
    this.operationTypeOptions.map(item => {
      this.operationTypeMap[item.value] = item;
    });
    getSetting().then(setting => {
      this.initStartTimeRange(setting);
    });
    this.loadDataset();
  },
  methods: {
    lowerFirst,
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
        rollbackCount: 0,
        transferCount: 0,
        counterSignCount: 0,
        addSignCount: 0,
        handOverCount: 0,
        gotoTaskCount: 0
      };
      this.$loading();
      $axios
        .post('/json/data/services', {
          serviceName: 'reportFacadeService',
          methodName: 'loadDataset',
          args: JSON.stringify([
            'com.wellsoft.pt.app.workflow.report.dataset.FlowOperationDetailSummaryEchartDatasetLoad',
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
                  } else if (sourceItem.hasOwnProperty('rollbackCount')) {
                    _this.summary.rollbackCount = sourceItem.rollbackCount;
                  } else if (sourceItem.hasOwnProperty('transferCount')) {
                    _this.summary.transferCount = sourceItem.transferCount;
                  } else if (sourceItem.hasOwnProperty('counterSignCount')) {
                    _this.summary.counterSignCount = sourceItem.counterSignCount;
                  } else if (sourceItem.hasOwnProperty('addSignCount')) {
                    _this.summary.addSignCount = sourceItem.addSignCount;
                  } else if (sourceItem.hasOwnProperty('handOverCount')) {
                    _this.summary.handOverCount = sourceItem.handOverCount;
                  } else if (sourceItem.hasOwnProperty('gotoTaskCount')) {
                    _this.summary.gotoTaskCount = sourceItem.gotoTaskCount;
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
      // this.pageContext.emitEvent('searchFlowOperation', this.searchForm);
      this.refreshTable(this.searchForm);
    },
    onResetClick() {
      this.searchForm = deepClone(this.initSearchForm);
      this.loadDataset();
      // this.pageContext.emitEvent('searchFlowOperation', this.searchForm);
      this.refreshTable(this.searchForm);
    },
    hideFilter() {
      this.showFilter = false;
      // this.searchForm = deepClone(this.initSearchForm);
      // this.loadDataset();
      // this.pageContext.emitEvent('searchFlowOperation', this.searchForm);
    },
    refreshTable(searchForm) {
      const _this = this;
      let tableWidget = _this.pageContext.getVueWidgetById('FuiBDQcMLOeZyPKRXXBNSVArjnXYOvMW');
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
div[w-id='page_flow_operation_detail_report'] {
  .widget-vpage-content {
    background-color: var(--w-widget-page-layout-bg-color) !important;
  }
}

.flow-operation-detail-summary {
  display: flex;
  padding: 12px 6px;
  margin-bottom: 12px;
  border-radius: 2px;
  background-color: var(--w-color-white);

  > ._summary-item {
    flex: 1;
    // max-width: 223px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 66px;
    margin: 0 6px;
    padding: 12px;
    border-radius: 4px;

    > ._title {
      color: var(--w-text-color-darker);
    }
    > ._summary {
      font-size: 24px;
      font-weight: bold;
    }

    &._total {
      flex-direction: column;
      justify-content: center;
      > ._title {
        color: var(--w-text-color-light);
      }
      > ._summary {
        font-size: 32px;
        line-height: 32px;
        color: var(--w-text-color-darker);
      }
    }
  }
}
</style>
