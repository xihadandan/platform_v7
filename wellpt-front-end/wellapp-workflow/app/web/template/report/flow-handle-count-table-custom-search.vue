<template>
  <div>
    <flow-report-head title="流程办理情况统计表" @onSearchClick="onSearchClick" @onResetClick="onResetClick">
      <template slot="filter">
        <a-form-model :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }" :model="searchForm" :colon="false" ref="searchForm">
          <template v-if="showUserSearchForm">
            <a-row>
              <a-col :span="8">
                <a-form-model-item label="发起人" prop="startUserRange">
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
          </template>
          <template v-else>
            <a-row>
              <a-col :span="8">
                <a-form-model-item label="部门" prop="deptRange">
                  <OrgSelect v-model="searchForm.deptRange" :checkableTypes="['unit', 'dept']" :key="deptRange" />
                </a-form-model-item>
              </a-col>
              <a-col :span="8">
                <a-form-model-item label="部门层级" prop="deptLevel">
                  <a-select v-model="searchForm.deptLevel">
                    <a-select-option value="all">统计全部部门</a-select-option>
                    <a-select-option value="top">仅统计顶级部门</a-select-option>
                    <a-select-option value="leaf">仅统计末级部门</a-select-option>
                  </a-select>
                </a-form-model-item>
              </a-col>
              <a-col :span="8">
                <a-form-model-item label="发起时间范围" prop="startTimeRange">
                  <a-range-picker :format="defaultPattern" :valueFormat="defaultPattern" v-model="searchForm.startTimeRange" />
                </a-form-model-item>
              </a-col>
            </a-row>
            <a-row>
              <a-col :span="8">
                <a-form-model-item label="结束时间范围" prop="endTimeRange">
                  <a-range-picker :format="defaultPattern" :valueFormat="defaultPattern" v-model="searchForm.endTimeRange" />
                </a-form-model-item>
              </a-col>
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
            </a-row>
            <a-row>
              <a-col :span="8">
                <a-form-model-item label="包含已停用流程数据" prop="includeDisabledFlow">
                  <a-switch v-model="searchForm.includeDisabledFlow" />
                </a-form-model-item>
              </a-col>
            </a-row>
          </template>
        </a-form-model>
      </template>
    </flow-report-head>
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
      deptRange: null,
      deptLevel: 'all',
      startUserRange: null,
      startTimeRange: [],
      endTimeRange: [],
      flowRange: null,
      flowState: 'all',
      includeDisabledFlow: true
    };
    return {
      showFilter: false,
      showUserSearchForm: true,
      initSearchForm,
      searchForm: deepClone(initSearchForm),
      defaultPattern: 'yyyy-MM-DD'
    };
  },
  beforeMount() {
    getSetting().then(setting => {
      this.initStartTimeRange(setting);
    });
    this.pageContext.handleEvent('showUserCustomSearchOfFlowHandleCount', () => {
      this.showUserSearchForm = true;
    });
    this.pageContext.handleEvent('showDeptCustomSearchOfFlowHandleCount', () => {
      this.showUserSearchForm = false;
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
      let tableWidget = null;
      if (_this.showUserSearchForm) {
        tableWidget = _this.pageContext.getVueWidgetById('FgsSSxGgNosVgUWxCEAHMDzFHrBvayRq');
      } else {
        tableWidget = _this.pageContext.getVueWidgetById('NwBnjxTDXJGMeWWgfJZAGYdXvXypVDtH');
      }
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

<style lang="less">
div[w-id='page_flow_handle_report'] {
  .widget-vpage-content {
    background-color: var(--w-widget-page-layout-bg-color) !important;
  }
}
</style>
