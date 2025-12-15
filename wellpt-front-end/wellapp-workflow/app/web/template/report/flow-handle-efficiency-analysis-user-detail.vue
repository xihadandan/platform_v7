<template>
  <div>
    <flow-report-head title="人员办理时间分析表" @onSearchClick="onSearchClick" @onResetClick="onResetClick">
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
    <a-card :bordered="false">
      <template slot="title"></template>
      <flow-report-detail-handle @expand="expandTable" @collapse="collapseTable">
        <a-tooltip title="刷新">
          <a-button @click="refreshTable(searchForm)">
            <Icon type="iconfont icon-luojizujian-yemianshuaxin" />
          </a-button>
        </a-tooltip>
        <a-tooltip title="导出">
          <a-button @click="exportTable">
            <Icon type="ant-iconfont export" />
          </a-button>
        </a-tooltip>
      </flow-report-detail-handle>
      <div class="widget-table">
        <a-table
          :rowKey="getRowKey"
          :columns="getExpandColumns()"
          :data-source="rows"
          :expandedRowKeys.sync="expandedRowKeys"
          :pagination="false"
          :loading="loading"
          :bordered="true"
          :customRow="customRow"
          :indentSize="0"
          childrenColumnName="details"
          @expand="onExpand"
          class="efficiency-analysis-user-table"
        >
          <template slot="avgHandleTimeSlot" slot-scope="text, record">
            <span v-if="parseFloat(text) >= 60 * 24" :style="getAvgHandleTimeStyle(record)">
              {{ parseInt(parseFloat(text) / (60 * 24)) }}天
              {{ parseInt((parseFloat(text) - parseInt(parseFloat(text) / (60 * 24)) * 60 * 24) / 60) }}小时 {{ parseFloat(text) % 60 }}分钟
            </span>
            <span v-else-if="parseFloat(text) >= 60" :style="getAvgHandleTimeStyle(record)">
              {{ parseInt(parseFloat(text) / 60) }}小时 {{ parseFloat(text) % 60 }}分钟
            </span>
            <span v-else :style="getAvgHandleTimeStyle(record)">{{ parseFloat(text) % 60 }}分钟</span>
          </template>
          <template slot="flowAvgHandleTimeSlot" slot-scope="text, record">
            <template v-if="text">
              <span v-if="parseFloat(text) >= 60 * 24">
                {{ parseInt(parseFloat(text) / (60 * 24)) }}天
                {{ parseInt((parseFloat(text) - parseInt(parseFloat(text) / (60 * 24)) * 60 * 24) / 60) }}小时
                {{ parseFloat(text) % 60 }}分钟
              </span>
              <span v-else-if="parseFloat(text) >= 60">{{ parseInt(parseFloat(text) / 60) }}小时 {{ parseFloat(text) % 60 }}分钟</span>
              <span v-else>{{ parseFloat(text) % 60 }}分钟</span>
            </template>
          </template>
        </a-table>
      </div>
      <!-- <a-button type="link" icon="caret-down" @click="expandTable">展开</a-button>
      <a-button type="link" icon="caret-right" @click="collapseTable">折叠</a-button>
      <Icon @click.stop="exportTable" type="ant-iconfont export" style="color: var(--w-primary-color); border-radius: 20%"></Icon>
      <Icon
        @click.stop="refreshTable(searchForm)"
        type="iconfont icon-luojizujian-yemianshuaxin"
        style="color: var(--w-primary-color); border-radius: 20%"
      ></Icon> -->
    </a-card>
  </div>
</template>

<script>
import moment from 'moment';
import { getSetting } from './utils';
import { debounce } from 'lodash';
import { deepClone } from '@framework/vue/utils/util';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import FlowSelect from '@workflow/app/web/lib/flow-select.vue';
import FlowReportHead from './flow-report-head.vue';
import FlowReportDetailHandle from './flow-report-detail-handle.vue';
import DataSourceBase from '@pageAssembly/app/web/assets/js/commons/dataSource.base.js';

const columns = [
  { title: '人员', dataIndex: 'userName', width: 200, scopedSlots: { customRender: 'userNameSlot' } },
  { title: '流程', dataIndex: 'name', width: 200 },
  { title: '环节', dataIndex: 'taskName', width: 200 },
  { title: '操作次数', dataIndex: 'handleCount', width: 90, scopedSlots: { customRender: 'handleCountSlot' } },
  { title: '平均用时', dataIndex: 'avgHandleTime', width: 200, scopedSlots: { customRender: 'avgHandleTimeSlot' } },
  { title: '流程平均用时', dataIndex: 'flowAvgHandleTime', width: 200, scopedSlots: { customRender: 'flowAvgHandleTimeSlot' } }
];

export default {
  components: {
    OrgSelect,
    FlowSelect,
    FlowReportHead,
    FlowReportDetailHandle
  },
  inject: ['pageContext'],
  data() {
    let userRange = this.getCurrentUserDeptId();
    let initSearchForm = {
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
      columns,
      rows: [],
      expandedRowKeys: [],
      pagination: {
        current: 1, //当前页
        pageSize: -1, //每页条数
        total: 0 //总条数
      },
      loading: false
    };
  },
  beforeMount() {
    getSetting().then(setting => {
      this.initStartTimeRange(setting);
    });
  },
  mounted() {
    setTimeout(() => {
      this.loadUserHandleEfficiencyData(this.searchForm, data => {
        this.expandedRowKeys.push(data[0]['uuid']);
        this.$nextTick(() => {
          this.onExpand(true, data[0]);
        });
      });
    }, 500);
  },
  methods: {
    customRow(row, index) {
      return {
        props: {
          style: {}
        },
        on: {
          // 事件
          click: event => {}
        }
      };
    },
    getRowKey(record) {
      if (record.uuid) {
        return record.uuid;
      }
      return record.userId + '-' + record.id + '-' + record.taskId;
    },
    onExpand(expanded, record) {
      const { uuid } = record;
      const rowEl = this.$el.querySelector(`[data-row-key="${uuid}"]`);
      if (expanded) {
        rowEl.style.cssText += `;display: none`;
      } else {
        // rowEl.style.cssText += `;display: block`;
      }
    },
    expandTable() {
      this.expandedRowKeys = this.rows.map(item => {
        const uuid = item.uuid;
        const rowEl = this.$el.querySelector(`[data-row-key="${uuid}"]`);
        rowEl.style.cssText += `;display: none`;
        return uuid;
      });
    },
    collapseTable() {
      this.expandedRowKeys = [];
      this.rows.map(item => {
        const uuid = item.uuid;
        const rowEl = this.$el.querySelector(`[data-row-key="${uuid}"]`);
        rowEl.removeAttribute('style');
      });
    },
    removeExpandedRowKeys(row) {
      let uuid;
      this.rows.map(item => {
        const rowUuid = item.uuid;
        if (item.details) {
          item.details.map(d => {
            if (d.userId === row.userId && d.id === row.id && d.taskId === row.taskId) {
              uuid = rowUuid;
            }
          });
        }
      });
      if (uuid) {
        const findIndex = this.expandedRowKeys.findIndex(item => item === uuid);
        if (findIndex > -1) {
          this.expandedRowKeys.splice(findIndex, 1);

          const rowEl = this.$el.querySelector(`[data-row-key="${uuid}"]`);
          // rowEl.style.cssText += `;display: table-row`;
          rowEl.removeAttribute('style');
        }
      }
    },
    getExpandColumns() {
      const _this = this;
      let expandColumns = JSON.parse(JSON.stringify(columns));
      expandColumns.forEach(column => {
        if (column.dataIndex == 'userName') {
          column.customRender = (value, row, index) => {
            const obj = {
              attrs: {},
              style: {
                verticalAlign: 'top'
              }
            };
            if (row.uuid) {
              obj.children = value;
              return obj;
            }

            const className = 'ant-table-row-expand-icon ant-table-row-spaced show';
            obj.children = [
              // <icon type=""/>
              <span class={className} onClick={$event => this.removeExpandedRowKeys(row)}></span>,
              <span>{value}</span>
            ];

            let userRow = _this.rows.find(item => item.userId == row.userId);
            if (index == 0) {
              obj.attrs.rowSpan = userRow.details.length;
            } else {
              obj.attrs.rowSpan = 0;
            }
            return obj;
          };
        }

        if (column.dataIndex == 'name') {
          column.customRender = (value, row, index) => {
            const obj = {
              children: value,
              attrs: {}
            };
            if (row.uuid) {
              return obj;
            }
            let userRow = _this.rows.find(item => item.userId == row.userId);
            let details = userRow.details;
            for (let i = 0; i < details.length; i++) {
              let detail = details[i];
              if (detail == row) {
                if (index == 0) {
                  obj.attrs.rowSpan = details.filter(item => item.id == row.id).length;
                } else {
                  let prevDetail = details[i - 1];
                  if (prevDetail.id == row.id) {
                    obj.attrs.rowSpan = 0;
                  } else {
                    obj.attrs.rowSpan = details.filter(item => item.id == row.id).length;
                  }
                }
              }
            }
            return obj;
          };
        }
      });
      return expandColumns;
    },
    getCurrentUserDeptId() {
      let userSystemOrgDetails = this._$USER.userSystemOrgDetails || {};
      let details = userSystemOrgDetails.details || [];
      for (let i = 0; i < details.length; i++) {
        let detail = details[i];
        if (detail.mainDept && detail.mainDept.deptId) {
          return detail.mainDept.deptId;
        }
        if (detail.mainJob && detail.mainJob.deptId) {
          return detail.mainDept.deptId;
        }

        let otherDepts = detail.otherDepts || [];
        for (let j = 0; j < otherDepts.length; j++) {
          let otherDept = otherDepts[j];
          if (otherDept.deptId) {
            return otherDept.deptId;
          }
        }
        let otherJobs = detail.otherJobs || [];
        for (let j = 0; j < otherJobs.length; j++) {
          let otherJob = otherJobs[j];
          if (otherJob.deptId) {
            return otherJob.deptId;
          }
        }
      }
      return null;
    },
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
      this.loadUserHandleEfficiencyData(searchForm);
    },
    exportTable: debounce(function () {
      this.$message.info(this.$t('WidgetTable.exportModal.exportingTip', '数据正在导出'));
      let exportColumns = JSON.parse(JSON.stringify(columns));
      exportColumns.forEach(column => {
        column.columnIndex = column.dataIndex;
        if (column.dataIndex == 'avgHandleTime' || column.dataIndex == 'flowAvgHandleTime') {
          column.renderer = {
            rendererType: 'freemarkerTemplateRenderer',
            template:
              '<if value gte (60 * 24)>\
                ${(value / (60 * 24))?floor}天${((value - ((value / (60 * 24))?floor) * 60 * 24) / 60)?floor}小时${value % 60}分钟\
              <elseif value gte 60>\
                ${((value - ((value / (60 * 24))?floor) * 60 * 24) / 60)?floor}小时${value % 60}分钟\
              <else>\
                ${value % 60}分钟\
              </if>'
          };
        }
      });
      new DataSourceBase({
        onDataChange: function (data, count, params) {
          data.data.forEach(item => (item.expanded = false));
          _this.rows = data.data;
          _this.pagination.total = count;
          _this.pagination.current = data.pagination.currentPage;
          _this.pagination.totalPages = data.pagination.totalPages;
          _this.loading = false;
        },
        receiver: this,
        dataStoreId: 'CD_DS_172036954129433798',
        params: {
          ...this.searchForm,
          export: true
        },
        pageSize: -1
      }).exportData({
        pagination: this.pagination,
        extras: {},
        fileName: '人员办理时间分析表' + '_' + moment().format('yyyyMMDDHHmmss'),
        exportColumns,
        type: 'excel_ooxml_column'
      });
    }, 300),
    loadUserHandleEfficiencyData(searchForm = this.searchForm, callback) {
      let _this = this;
      if (_this.loading) {
        return;
      }
      _this.loading = true;
      _this.rows.splice(0, _this.rows.length);
      new DataSourceBase({
        onDataChange: function (data, count, params) {
          data.data.forEach(item => (item.expanded = false));
          _this.rows = data.data;
          _this.pagination.total = count;
          _this.pagination.current = data.pagination.currentPage;
          _this.pagination.totalPages = data.pagination.totalPages;
          _this.loading = false;
          if (callback && typeof callback === 'function') {
            callback(data.data);
          }
        },
        receiver: this,
        dataStoreId: 'CD_DS_172036954129433798',
        params: {
          ...searchForm
        },
        pageSize: -1
      }).load({
        pagination: {
          currentPage: this.pagination.current,
          pageSize: this.pagination.pageSize
        }
      });
    },
    getAvgHandleTimeStyle(record) {
      let avgHandleTime = record.avgHandleTime;
      let flowAvgHandleTime = record.flowAvgHandleTime;
      if (!avgHandleTime || !flowAvgHandleTime) {
        return {};
      }

      if (parseInt(avgHandleTime) > parseInt(flowAvgHandleTime) * 1.5) {
        return {
          color: 'red'
        };
      } else if (parseInt(avgHandleTime) > parseInt(flowAvgHandleTime)) {
        return {
          color: 'orange'
        };
      }
      return {};
    }
  }
};
</script>

<style lang="less">
div[w-id='page_flow_handle_efficiency_detail_report'] {
  .widget-vpage-content {
    background-color: var(--w-widget-page-layout-bg-color) !important;
    .ant-table-row-expand-icon {
      font-family: 'ant-iconfont';
      border: none;
      background: transparent;
      &.ant-table-row-spaced {
        display: none;
        &.show {
          display: inline-block;
          margin-right: 8px;
          &::after {
            content: '\e7c7';
            visibility: visible;
          }
        }
      }
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

    .efficiency-analysis-user-table {
      .ant-table-body {
        > table {
          border-left: none;
        }
      }
      .ant-table-thead > tr > th {
        border-right: none;
      }
      .ant-table-row-level-0 {
        td {
          border-right: none;
        }
      }
      .ant-table-row-level-1 {
        td {
          &:last-child {
            border-right: none;
          }
        }
      }
    }
  }
}
</style>
