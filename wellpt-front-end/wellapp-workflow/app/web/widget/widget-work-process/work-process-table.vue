<template>
  <div class="work-process-table">
    <WorkProcessHeader
      :widget="widget"
      :bodyWidget="bodyWidget"
      :allowLocate="false"
      @searchWorkProcess="onSearchWorkProcess"
    ></WorkProcessHeader>
    <p />
    <PerfectScrollbar :style="{ height: bodyHeight }">
      <WidgetTable
        v-if="tableWidget"
        ref="tableWidget"
        :widget="tableWidget"
        :widgetsOfParent="[]"
        @beforeLoadData="beforeLoadData"
      ></WidgetTable>
    </PerfectScrollbar>
  </div>
</template>

<script>
import WidgetTable from '@pageAssembly/app/web/widget/widget-table/widget-table.vue';
import WorkProcessHeader from './work-process-header.vue';
import WorkProcessStandard from './work-process-standard.vue';
import { deepClone, generateId } from '@framework/vue/utils/util';

export default {
  name: 'WorkProcessTable',
  extends: WorkProcessStandard,
  components: { WidgetTable, WorkProcessHeader },
  computed: {
    tableWidget() {
      let tableWidget = deepClone(this.widget);
      tableWidget.configuration.columns.forEach(column => (column.primaryKey = false));
      tableWidget.configuration.columns.push({
        id: generateId(),
        title: 'ID',
        dataIndex: 'id',
        primaryKey: true,
        hidden: true,
        sortable: false,
        showTip: false,
        titleAlign: 'left',
        contentAlign: 'left',
        renderFunction: { options: {} },
        clickEvent: { enable: false, eventHandler: {} },
        exportFunction: { options: {} }
      });

      Object.assign(tableWidget.configuration, {
        bordered: false,
        columnTitleHidden: false,
        rowDataFrom: 'dataSource',
        dataModelUuid: undefined,
        dataModelType: 'TABLE',
        dataSourceId: 'workProcess',
        dataSourceName: null,
        rowSelectAble: true,
        rowSelectType: 'no',
        displayCardList: false,
        cardColumnNum: 3,
        toggleDisplay: true,
        defaultDisplayState: 'table',
        enableDefaultCondition: false,
        defaultCondition: undefined,
        enableCustomTable: false,
        rowClickEvent: {
          enable: false,
          eventHandlers: []
        },
        headerButton: {
          enable: false,
          buttons: [],
          buttonGroup: {
            type: 'notGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
            groups: [
              // 固定分组
              // {name:,buttonIds:[]}
            ],
            style: { textHidden: false, type: 'default', icon: undefined, rightDownIconVisible: false },
            dynamicGroupName: '更多', //动态分组名称
            dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
          }
        },
        rowButton: {
          enable: false,
          buttons: [],
          buttonGroup: {
            type: 'notGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
            groups: [
              // 固定分组
              // {name:,buttonIds:[]}
            ],
            style: { textHidden: false, type: 'default', icon: undefined, rightDownIconVisible: false },
            dynamicGroupName: '更多', //动态分组名称
            dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
          }
        },
        search: {
          type: 'keywordAdvanceSearch',
          keywordSearchEnable: false,
          keywordSearchColumns: [],
          columnSearchGroupEnable: false,
          advanceSearchEnable: false,
          columnSearchGroup: [],
          columnAdvanceSearchGroup: [],
          advanceSearchPerRowColNumber: 3
        },
        defaultSort: [] // 默认排序
      });
      return tableWidget;
    },
    tableDataSource() {
      const _this = this;
      let dataSource = _this.dataSource || [];
      dataSource.forEach(detail => {
        detail.actionInfo = _this.getActionInfo(detail);
      });
      return dataSource;
    }
  },
  watch: {
    dataSource: {
      deep: false,
      handler(newVal, oldVal) {
        this.$refs.tableWidget.refetch();
      }
    }
  },
  // created() {
  //   const _this = this;
  //   let dataSource = _this.dataSource || [];
  //   dataSource.forEach(detail => {
  //     detail.actionInfo = _this.getActionInfo(detail);
  //   });
  // },
  methods: {
    getActionInfo(detail) {
      let actionInfo = '';
      let actionName = detail.actionName || this.$t('WorkflowWork.operation.Submit', '提交');
      let _actionName = detail.matchActionName || this.$t('WorkflowWork.operation.' + detail.actionType, null);
      if (_actionName) {
        actionName = _actionName;
      }
      if (detail.actionCode == 35) {
        detail.actionName += ' (' + this.$t('WidgetWorkProcess.approveAuto', '自动审批') + ')';
      } else if (detail.actionCode == 36) {
        detail.actionName = this.$t('WidgetWorkProcess.autoJumpRepeatTask', '重复自动跳过');
      }
      let actionType = detail.actionType || 'Submit';
      // if (!actionType || !['Transfer', 'CounterSign', 'AddSign', 'Delegation', 'CopyTo', 'GotoTask', 'HandOver'].includes(actionType)) {
      //   return actionInfo;
      // }

      let actionObjects = '';
      if (actionType == 'GotoTask') {
        let fromTaskName = detail.suspensionState == 2 ? this.$t('WidgetWorkProcess.endTask', '结束') : detail.taskName;
        let toTaskName = detail.gotoTaskName;
        let gotoTaskActionName = _actionName
          ? _actionName + this.$t('WidgetWorkProcess.toText', '至')
          : this.$t('WidgetWorkProcess.gotoTaskToText', '跳转至');
        actionInfo = `<span class="action-goto-task-info">
            ${this.$t('WidgetWorkProcess.gotoTaskFromText', '将流程从')}<span class="action-task-name">&lt;${fromTaskName}&gt;</span>
             ${gotoTaskActionName}<span class="action-task-name">&lt;${toTaskName}&gt;</span>
          </span>`;
      } else if (actionType == 'HandOver') {
        let formerHandler = detail.formerHandler;
        let toUser = detail.toUser;
        let handOverActionName = _actionName
          ? _actionName + this.$t('WidgetWorkProcess.toText', '至')
          : this.$t('WidgetWorkProcess.handoverToText', '移交至');
        actionInfo = `<span class="action-hand-over-info">
            ${this.$t('WidgetWorkProcess.gotoTaskFromText', '将流程从')}<span class="action-task-name">&lt;${formerHandler}&gt;</span>
            ${handOverActionName}<span class="action-task-name">&lt;${toUser}&gt;</span>
          </span>`;
      } else if (
        detail.toUser &&
        (actionType == 'Transfer' || actionType == 'CounterSign' || actionType == 'AddSign' || actionType == 'Delegation')
      ) {
        actionObjects = '<span class="action-objects">' + detail.toUser + '</span>';
        actionName += ' ' + this.$t('WidgetWorkProcess.To', '给');
        actionInfo = actionName + ' ' + actionObjects;
      } else if (detail.copyUser && actionType == 'CopyTo') {
        actionObjects = '<span class="action-objects">' + detail.copyUser + '</span>';
        actionName = actionName + ' ' + this.$t('WidgetWorkProcess.Target', '对象');
        actionInfo = actionName + ' ' + actionObjects;
      } else if (detail.toUser && detail.copyUser) {
        actionInfo = `<div class="action-with-copyto">${this.$t(
          'WidgetWorkProcess.copyToMeanwhile',
          '同时抄送'
        )} <span class="action-objects">${detail.copyUser}</span></div>`;
      }
      return actionInfo;
    },
    onSearchWorkProcess() {
      this.$refs.tableWidget.pagination.current = 1;
    },
    beforeLoadData() {
      const _this = this;
      if (!_this.changeDataSource && _this.$refs.tableWidget && _this.$refs.tableWidget.dataSourceProvider) {
        _this.changeDataSourceForm(_this.$refs.tableWidget.dataSourceProvider);
      }
    },
    changeDataSourceForm(dataSourceProvider) {
      const _this = this;
      _this.changeDataSource = true;
      dataSourceProvider._loadData = function (params) {
        this.data = _this.getPageData(this.getParams());
        this.totalCount = _this.tableDataSource.length;
        this.loaded = true;
        this.notifyChange(params);
        return Promise.resolve(this.data);
      };
      dataSourceProvider._loadCount = function (params) {
        this.totalCount = _this.tableDataSource.length;
        this.loaded = true;
        this.notifyChange(params);
      };
    },
    getPageData(params) {
      const _this = this;
      let pagingInfo = params.pagingInfo;
      let currentPage = pagingInfo.currentPage;
      let pageSize = pagingInfo.pageSize;
      let startIndex = (currentPage - 1) * pageSize;
      let dataList = [];
      for (let index = startIndex; index < startIndex + pageSize; index++) {
        let rowData = _this.tableDataSource[index];
        if (rowData) {
          if (!rowData.id) {
            rowData.id = generateId('SF');
          }
          // 撤回的意见立场、附件不显示
          if (rowData.canceled) {
            rowData = deepClone(rowData);
            rowData.opinionLabel = '';
            rowData.opinionValue = '';
            rowData.opinionFiles = [];
          }
          if (rowData.supplemented) {
            rowData.taskName =
              this.$t('WorkflowView.' + rowData.taskId + '.taskName', rowData.taskName) +
              ' (' +
              this.$t('WorkflowWork.extraApproveExtraTask', '补审补办') +
              ')';
          } else if (rowData.taskId) {
            rowData.taskName = _this.$t('WorkflowView.' + rowData.taskId + '.taskName', rowData.taskName);
          }
          if (rowData.actionType) {
            if (![35, 36].includes(rowData.actionCode)) {
              rowData.actionName = _this.$t('WorkflowWork.operation.' + rowData.actionType, rowData.actionName);
            }
          }
          dataList.push(rowData);
        }
      }
      pagingInfo.totalCount = _this.tableDataSource.length;
      pagingInfo.totalPages = Math.ceil(pagingInfo.totalCount / pageSize);
      return {
        data: dataList,
        pagination: pagingInfo
      };
    }
  }
};
</script>

<style lang="less" scoped>
.work-process-table {
  ::v-deep .action-objects {
    color: var(--w-primary-color);
  }
  ::v-deep .action-user-name {
    font-weight: bold;
    color: #222;
  }
  ::v-deep .action-task-name {
    font-weight: bold;
    color: var(--w-primary-color);
  }
  ::v-deep .ant-tag.current-user-tag {
    border-radius: 12px;
    --w-tag-text-size: var(--w-font-size-base);
    --w-tag-height: 22px;
  }
}
</style>
