<template>
  <div>
    <flow-report-summary>
      <flow-report-summary-item title="统计人数" icon="iconfont icon-luojizujian-huoquyonghuliebiao" :summary="summary.userCount" />
      <flow-report-summary-item
        icon="iconfont icon-xmch-daibangongzuo"
        color="#3DC255"
        bgColor="#F5FCF7"
        title="待办数量"
        :summary="summary.todoCount"
      />
      <flow-report-summary-item title="待办最多流程" :summary="summary.flowNameOfMaxTodoCount" />
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
        userCount: 0,
        todoCount: 0,
        flowNameOfMaxTodoCount: ''
      }
    };
  },
  beforeMount() {
    this.pageContext.handleEvent('searchUserFlowTodo', searchForm => {
      this.loadDataset(searchForm);
    });
  },
  methods: {
    loadDataset(searchForm = {}) {
      const _this = this;
      _this.summary = {
        userCount: 0,
        todoCount: 0,
        flowNameOfMaxTodoCount: ''
      };
      this.$loading();
      $axios
        .post('/json/data/services', {
          serviceName: 'reportFacadeService',
          methodName: 'loadDataset',
          args: JSON.stringify(['com.wellsoft.pt.app.workflow.report.dataset.FlowTodoByUserSummaryEchartDatasetLoad', { ...searchForm }])
        })
        .then(({ data: result }) => {
          this.$loading(false);
          if (result.data) {
            result.data.forEach(item => {
              item.source &&
                item.source.forEach(sourceItem => {
                  if (sourceItem.hasOwnProperty('userCount')) {
                    _this.summary.userCount = sourceItem.userCount;
                  } else if (sourceItem.hasOwnProperty('todoCount')) {
                    _this.summary.todoCount = sourceItem.todoCount;
                  } else if (sourceItem.hasOwnProperty('maxFlowTodoCountByUserGrouyByFlow')) {
                    _this.summary.flowNameOfMaxTodoCount = sourceItem.name;
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
