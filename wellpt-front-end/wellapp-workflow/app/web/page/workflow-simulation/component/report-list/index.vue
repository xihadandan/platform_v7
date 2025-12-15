<template>
  <div>
    <p />
    <div>
      <a-row>
        <a-col span="11" offset="13">
          <a-space>
            <a-button type="danger" icon="minus" :disabled="!rowSelection.selectedRowKeys.length" @click="onDeleteRecordClick">
              删除
            </a-button>
            <a-button
              type="danger"
              icon="delete"
              :disabled="!records.length"
              @click="onClearRecordClick"
              :title="`仿真报告${simulation.recordRetainDays}天后自动清除`"
            >
              清空
            </a-button>
          </a-space>
        </a-col>
      </a-row>
    </div>
    <p />
    <PerfectScrollbar
      :style="{
        height: 'calc(100vh - 160px)'
      }"
    >
      <a-table
        rowKey="uuid"
        size="small"
        :pagination="false"
        :bordered="true"
        :columns="recordTableColumns"
        :locale="locale"
        :data-source="records"
        :row-selection="rowSelection"
        :customRow="customRow"
        :class="['record-table no-border']"
      >
        <template slot="stateSlot" slot-scope="text, record">
          <div :title="getDeleteTimeTip(record)">
            <span v-if="text == 'running'">仿真中</span>
            <span v-else-if="text == 'pause'">已暂停</span>
            <span v-else-if="text == 'success'">成功</span>
            <span v-else-if="text == 'error'">失败</span>
            <span v-else>未知</span>
          </div>
        </template>
        <template slot="operationSlot" slot-scope="text, record, index">
          <a-button v-if="record.flowInstUuid" type="link" size="small" title="查看流程" @click.stop="viewWork(record)">
            <Icon type="eye" />
          </a-button>
          <a-button
            v-if="record.state == 'success' || record.state == 'pause' || record.state == 'error'"
            type="link"
            size="small"
            title="重新仿真"
            @click.stop="restart(record)"
          >
            <Icon type="redo" />
          </a-button>
          <a-button
            v-if="record.state == 'pause' || record.state == 'error'"
            type="link"
            size="small"
            title="继续仿真"
            @click.stop="resume(record)"
          >
            <Icon type="control" />
          </a-button>
        </template>
      </a-table>
    </PerfectScrollbar>
  </div>
</template>

<script>
import { addSystemPrefix } from '../../simulation/utils.js';
import moment from 'moment';
export default {
  props: {
    flowDefinition: Object
  },
  inject: ['pageContext', 'simulation'],
  data() {
    const _this = this;
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      recordTableColumns: [
        { title: '仿真人', dataIndex: 'operatorName' },
        { title: '仿真时间', dataIndex: 'operatorTime' },
        { title: '状态', dataIndex: 'state', width: 60, scopedSlots: { customRender: 'stateSlot' } },
        { title: '操作', dataIndex: 'operation', width: 50, scopedSlots: { customRender: 'operationSlot' } }
      ],
      records: [],
      rowSelection: {
        selectedRowKeys: [],
        selectedRows: [],
        onChange(selectedRowKeys, selectedRows) {
          _this.rowSelection.selectedRowKeys = selectedRowKeys;
          _this.rowSelection.selectedRows = selectedRows;
        }
      }
    };
  },
  mounted() {
    const _this = this;
    _this.listRecord();
    _this.pageContext.handleEvent('simulationStart', () => {
      _this.listRecord();
    });
    _this.pageContext.handleEvent('simulationPause', () => {
      _this.listRecord();
    });
    _this.pageContext.handleEvent('simulationUpdate', () => {
      _this.listRecord();
    });
    _this.pageContext.handleEvent('simulationSuccess', () => {
      _this.listRecord();
    });
    _this.pageContext.handleEvent('simulationFailure', () => {
      _this.listRecord();
    });
  },
  methods: {
    listRecord() {
      $axios.get(`/proxy/api/workflow/simulation/record/list?flowDefUuid=${this.flowDefinition.uuid}`).then(({ data: result }) => {
        if (result.data) {
          this.records = result.data;
          this.simulation.records = result.data;
        }
      });
    },
    onDeleteRecordClick() {
      const _this = this;
      let selectedRowKeys = _this.rowSelection.selectedRowKeys;
      _this.deleteDeleteRecord(selectedRowKeys, '确认删除所选记录？');
    },
    onClearRecordClick() {
      const _this = this;
      let selectedRowKeys = _this.records.map(record => record.uuid);
      _this.deleteDeleteRecord(selectedRowKeys, '确认清空所有记录？');
    },
    getDeleteTimeTip(record) {
      let operatorTime = moment(record.operatorTime);
      operatorTime.add(this.simulation.recordRetainDays, 'd');
      return '自动删除时间：' + operatorTime.format('YYYY-MM-DD HH:mm:ss');
    },
    deleteDeleteRecord(selectedRowKeys, configMsg) {
      const _this = this;
      _this.$confirm({
        title: '确认',
        content: `${configMsg}`,
        okText: '确定',
        cancelText: '取消',
        onOk() {
          _this.$loading();
          $axios
            .post(`/proxy/api/workflow/simulation/record/delete?uuids=${selectedRowKeys}`)
            .then(({ data: result }) => {
              _this.$loading(false);
              if (result.code == 0) {
                _this.$message.success('删除成功！');
                _this.listRecord();
                _this.simulation.reset();
                _this.rowSelection.selectedRowKeys = [];
                _this.rowSelection.selectedRows = [];
              } else {
                _this.$message.error(result.msg || '删除失败！');
              }
            })
            .catch(({ response }) => {
              _this.$loading(false);
              _this.$message.error((response && response.data && response.data.msg) || '服务异常！');
            });
        }
      });
    },
    customRow(record) {
      return {
        on: {
          click: event => {
            this.simulation.viewByRecord(record);
            this.rowSelection.selectedRowKeys = [record.uuid];
            this.rowSelection.selectedRows = [record];
          }
        }
      };
    },
    viewWork(record) {
      let workUrl = '/workflow/work/view/work?flowInstUuid=' + record.flowInstUuid;
      window.open(addSystemPrefix(workUrl, this));
    },
    restart(record) {
      this.simulation.restartByRecord(record);
    },
    resume(record) {
      this.simulation.resumeByRecord(record);
    }
  }
};
</script>

<style></style>
