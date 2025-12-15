<template>
  <div>
    <flow-report-head title="待办流程统计表" @onSearchClick="onSearchClick" @onResetClick="onResetClick">
      <template slot="filter">
        <a-form-model :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }" :model="searchForm" :colon="false" ref="searchForm">
          <template v-if="showUserTodo">
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
          </template>
          <template v-else>
            <a-row>
              <a-col :span="8">
                <a-form-model-item label="部门范围" prop="deptRange">
                  <OrgSelect v-model="searchForm.deptRange" :uncheckableTypes="['user', 'job']" :key="deptRange" />
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
import reportMixin from './report.mixin';
import { deepClone } from '@framework/vue/utils/util';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import FlowSelect from '@workflow/app/web/lib/flow-select.vue';
import FlowReportHead from './flow-report-head.vue';

export default {
  mixins: [reportMixin],
  components: {
    OrgSelect,
    FlowSelect,
    FlowReportHead
  },
  inject: ['pageContext'],
  data() {
    let userRange = this.getCurrentUserDeptId();
    let deptRange = userRange;
    let initSearchForm = {
      deptLevel: 'all',
      userRange,
      deptRange,
      startTimeRange: [],
      flowRange: null,
      includeDisabledFlow: true
    };
    return {
      showFilter: false,
      initSearchForm,
      searchForm: deepClone(initSearchForm),
      defaultPattern: 'yyyy-MM-DD',
      showUserTodo: true
    };
  },
  beforeMount() {
    getSetting().then(setting => {
      this.initStartTimeRange(setting);
    });
    this.pageContext.handleEvent('showUserCustomSearchOfFlowTodo', () => {
      this.showUserTodo = true;
    });
    this.loadedDeptCustomSearchOfFlowTodo = false;
    this.pageContext.handleEvent('showDeptCustomSearchOfFlowTodo', () => {
      this.showUserTodo = false;
      if (!this.loadedDeptCustomSearchOfFlowTodo) {
        this.refreshTable(this.searchForm);
        this.loadedDeptCustomSearchOfFlowTodo = true;
      }
    });
  },
  mounted() {
    setTimeout(() => {
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
      this.refreshTable(this.searchForm);
    },
    onResetClick() {
      this.searchForm = deepClone(this.initSearchForm);
      this.refreshTable(this.searchForm);
    },
    hideFilter() {
      this.showFilter = false;
    },
    refreshTable(searchForm) {
      const _this = this;
      let tableWidget = _this.pageContext.getVueWidgetById('QbMPIlFpobCTQiDiNEEmxOZTFJiyXsPa');
      if (!this.showUserTodo) {
        tableWidget = _this.pageContext.getVueWidgetById('IFNeymaJgaZLzNkoKUEmmGPpxtHVnbzd');
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
div[w-id='page_flow_todo_detail_report'] {
  .widget-vpage-content {
    background-color: var(--w-widget-page-layout-bg-color) !important;
    .ant-table-row-expand-icon {
      font-family: 'ant-iconfont';
      border: none;
      background: transparent;
    }
    .ant-table-row-collapsed {
      &::after {
        content: '\e7c3';
      }
    }
    .ant-table-row-expanded {
      &::after {
        content: '\e7c7';
      }
    }
  }
}
</style>
