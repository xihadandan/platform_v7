<template>
  <div class="simulation-report-viewer">
    <PerfectScrollbar
      :style="{
        height: 'calc(100vh - 80px)'
      }"
    >
      <a-descriptions v-if="record" bordered :column="3">
        <a-descriptions-item label="仿真人">{{ record.operatorName }}</a-descriptions-item>
        <a-descriptions-item label="仿真时间">{{ record.operatorTime }}</a-descriptions-item>
        <a-descriptions-item label="发起人">{{ record.startUserName }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <span v-if="record.state == 'running'">仿真中</span>
          <span v-else-if="record.state == 'pause'">已暂停</span>
          <span v-else-if="record.state == 'success'">成功</span>
          <span v-else-if="record.state == 'error'">失败</span>
          <span v-else>未知</span>
        </a-descriptions-item>
        <a-descriptions-item label="是否生成流水号">{{ recordParams.generateSerialNumber ? '是' : '否' }}</a-descriptions-item>
        <a-descriptions-item label="是否发送消息">{{ recordParams.sendMsg ? '是' : '否' }}</a-descriptions-item>
        <a-descriptions-item label="是否归档">{{ recordParams.archive ? '是' : '否' }}</a-descriptions-item>
      </a-descriptions>
      <a-empty v-else></a-empty>
      <template v-if="record">
        <p />
        <div>人员解析</div>
        <a-table
          rowKey="uuid"
          size="small"
          :pagination="false"
          :bordered="false"
          :columns="recordItemTableColumns"
          :locale="locale"
          :data-source="record.items"
          :class="['record-item-table no-border']"
        >
          <template slot="sortOrderSlot" slot-scope="text, record, index">
            {{ record.sortOrder || index + 1 }}
          </template>
          <template slot="isSetUserSlot" slot-scope="text, record">
            <template v-if="record.details.taskType == '1' || record.details.taskType == '3'">
              <template v-if="record.details.simulationTaskUserId || record.details.simulationDefaultTaskUserId">
                <div v-if="record.details.simulationTaskUserId">仿真办理人: {{ record.details.simulationTaskUserName }}</div>
                <div v-else-if="record.details.simulationDefaultTaskUserId">
                  默认仿真办理人: {{ record.details.simulationDefaultTaskUserName }}
                </div>
              </template>
              <span v-else>{{ getTaskUserLabel(record.details) }}</span>
              <div v-if="record.details.taskType == '3'">
                <span v-if="record.details.simulationDecisionMakerId">仿真决策人: {{ record.details.simulationDecisionMakerName }}</span>
                <span v-else>{{ getTaskDecisionMakerLabel(record.details) }}</span>
              </div>
            </template>
          </template>
          <template slot="isSetUserResultSlot" slot-scope="text, record">
            <template v-if="record.details.taskType == '1' || record.details.taskType == '3'">
              <div v-if="record.details.startFlow">启动人: {{ record.details.taskUserNames }}</div>
              <div v-else>
                <template v-if="record.details.simulationTaskUserFlag || record.details.simulationDefaultTaskUserFlag">
                  <span v-if="record.details.simulationTaskUserFlag">仿真办理人:</span>
                  <span v-if="record.details.simulationDefaultTaskUserFlag">默认仿真办理人:</span>
                </template>
                <span v-else-if="record.details.taskUserNoFound == 'current'">仿真全局设置取当前用户:</span>
                <span v-else-if="record.details.taskUserNoFound == 'assign'">仿真全局设置取指定办理人:</span>
                <template v-else-if="record.details.taskUserNoFound == 'modal'">
                  <span v-if="record.details.isSetUser == '2'">由前一环节办理人指定:</span>
                  <span v-else>仿真全局设置弹窗由用户选择:</span>
                </template>
                <span v-else>办理人:</span>
                {{ record.details.candidateUserNames || record.details.taskUserNames }}
                <div v-if="record.details.chooseOneUser">
                  <span v-if="record.details.chooseOneUser == 'first'">仿真全局设置多个办理人选择一个自动取第一个办理人:</span>
                  <span v-else-if="record.details.chooseOneUser == 'random'">仿真全局设置多个办理人选择一个自动随机取一个办理人:</span>
                  <span v-else-if="record.details.chooseOneUser == 'last'">仿真全局设置多个办理人选择一个自动取最后一个办理人:</span>
                  <span v-else>仿真全局设置多个办理人选择一个弹窗由用户选择:</span>
                  {{ record.details.taskUserNames }}
                </div>
                <div v-else-if="record.details.chooseMultiUser">
                  <span v-if="record.details.chooseMultiUser == 'all'">仿真全局设置多个办理人选择多个取全部办理人:</span>
                  <span v-else-if="record.details.chooseMultiUser == 'random'">
                    仿真全局设置多个办理人选择多个随机取{{ record.details.randomUserCount }}个办理人:
                  </span>
                  <span v-else>仿真全局设置多个办理人选择多个弹窗由用户选择:</span>
                  {{ record.details.taskUserNames }}
                </div>
              </div>
              <div v-if="record.details.taskType == '3'">
                <span v-if="record.details.simulationDecisionMakerFlag && record.details.taskDecisionMakerNames">仿真决策人:</span>
                <span v-else-if="record.details.taskDecisionMakerNoFound == 'current'">仿真全局设置取当前用户:</span>
                <span v-else-if="record.details.taskDecisionMakerNoFound == 'assign'">仿真全局设置取指定决策人:</span>
                <span v-else-if="record.details.taskDecisionMakerNoFound == 'modal'">仿真全局设置弹窗由用户选择:</span>
                <span v-else>决策人:</span>
                {{ record.details.taskDecisionMakerNames }}
              </div>
            </template>
          </template>
          <template slot="isSetCopyUserSlot" slot-scope="text, record">
            <template v-if="record.details.taskType == '1' || record.details.taskType == '3'">
              <span>{{ getTaskCopyUserLabel(record.details) }}</span>
            </template>
          </template>
          <template slot="isSetCopyUserResultSlot" slot-scope="text, record">
            <template v-if="record.details.taskType == '1' || record.details.taskType == '3'">
              <span v-if="record.details.taskCopyUserNoFound == 'current'">仿真全局设置取当前用户:</span>
              <span v-else-if="record.details.taskCopyUserNoFound == 'assign'">仿真全局设置取指定抄送人:</span>
              <span v-else-if="record.details.taskCopyUserNoFound == 'modal'">仿真全局设置弹窗由用户选择:</span>
              <span v-else-if="record.details.taskCopyUserNames">抄送人:</span>
              {{ record.details.taskCopyUserNames }}
            </template>
          </template>
          <template slot="isSetMonitorSlot" slot-scope="text, record">
            <template v-if="record.details.taskType == '1' || record.details.taskType == '3'">
              <span>{{ getTaskMonitorUserLabel(record.details) }}</span>
            </template>
          </template>
          <template slot="isSetMonitorResultSlot" slot-scope="text, record">
            <template v-if="record.details.taskType == '1' || record.details.taskType == '3'">
              <span v-if="record.details.taskSuperviseUserNoFound == 'current'">仿真全局设置取当前用户:</span>
              <span v-else-if="record.details.taskSuperviseUserNoFound == 'assign'">仿真全局设置取指定督办人:</span>
              <span v-else-if="record.details.taskSuperviseUserNoFound == 'modal'">仿真全局设置弹窗由用户选择:</span>
              <span v-else-if="record.details.taskMonitorUserNames">督办人:</span>
              {{ record.details.taskMonitorUserNames }}
            </template>
          </template>
        </a-table>
        <p />
        <div style="position: relative">
          <div style="position: relative; top: 12px">办理过程</div>
          <WidgetWorkProcess v-if="processLoaded" ref="processWidget" :widget="processWidget"></WidgetWorkProcess>
        </div>
      </template>
    </PerfectScrollbar>
  </div>
</template>

<script>
import {
  getTaskUserLabel,
  getTaskDecisionMakerLabel,
  getTaskCopyUserLabel,
  getTaskMonitorUserLabel,
  addSystemPrefix
} from '../simulation/utils.js';
import WorkProcess from '../../workflow-work/component/WorkProcess.js';
import WidgetWorkProcess from '../../../widget/widget-work-process/widget-work-process.vue';
export default {
  components: { WidgetWorkProcess },
  inject: ['simulation'],
  data() {
    return {
      record: null,
      recordParams: null,
      locale: {
        emptyText: <span>暂无数据</span>
      },
      recordItemTableColumns: [
        { title: '序号', dataIndex: 'sortOrder', width: 50, align: 'center', scopedSlots: { customRender: 'sortOrderSlot' } },
        { title: '环节', dataIndex: 'taskName', width: 120 },
        { title: '办理人设置', dataIndex: 'isSetUser', scopedSlots: { customRender: 'isSetUserSlot' } },
        { title: '办理人解析结果', dataIndex: 'isSetUserResult', scopedSlots: { customRender: 'isSetUserResultSlot' } },
        // { title: '可多人办理', dataIndex: 'alowMultiUser', scopedSlots: { customRender: 'alowMultiUserSlot' } },
        { title: '抄送人设置', dataIndex: 'isSetCopyUser', scopedSlots: { customRender: 'isSetCopyUserSlot' } },
        { title: '抄送人解析结果', dataIndex: 'isSetCopyUserResult', scopedSlots: { customRender: 'isSetCopyUserResultSlot' } },
        { title: '督办人设置', dataIndex: 'isSetMonitor', scopedSlots: { customRender: 'isSetMonitorSlot' } },
        { title: '督办人解析结果', dataIndex: 'isSetMonitorResult', scopedSlots: { customRender: 'isSetMonitorResultSlot' } }
      ],
      processLoaded: false
    };
  },
  watch: {
    'simulation.simulationData': {
      handler(newVal, oldVal) {
        if (this.$el.offsetParent) {
          if (newVal && newVal.recordUuid) {
            this.loadRecord(newVal.recordUuid);
          } else {
            this.record = null;
          }
        }
      }
    },
    'simulation.activeKey': function (newVal, oldVal) {
      if (newVal == 'report' && this.simulation.simulationData && this.simulation.simulationData.recordUuid) {
        this.loadRecord(this.simulation.simulationData.recordUuid);
      }
    }
  },
  computed: {
    processWidget() {
      let widget = this.workProcess.getWidgetConfiguration();
      widget.configuration.allowSwitchDisplayStyle = false;
      widget.configuration.displayStyle = 'table';
      widget.configuration.height = 'auto';
      widget.configuration.style.height = 'auto';
      widget.configuration.search.keywordSearchEnable = false;
      widget.configuration.search.locateCurrentUserRecord = false;
      widget.configuration.search.locateCurrentTaskRecord = false;
      widget.configuration.enabledSort = false;
      return widget;
    }
  },
  mounted() {
    if (this.simulation.simulationData && this.simulation.simulationData.recordUuid) {
      this.loadRecord(this.simulation.simulationData.recordUuid);
    }
  },
  methods: {
    loadRecord(recordUuid) {
      this.processLoaded = false;
      $axios.get(`/proxy/api/workflow/simulation/record/get?uuid=${recordUuid}`).then(({ data: result }) => {
        if (result.data) {
          let record = result.data;
          let items = record.items || [];
          items.forEach(item => (item.details = JSON.parse(item.details)));
          record.items = this.buildTreeItems(items, record.flowInstUuid);
          this.record = record;
          this.recordParams = JSON.parse(this.record.contentJson).params || {};
          this.workProcess = new WorkProcess(record, this);
          this.processLoaded = true;
          this.workProcess.getWorkProcess().then(workProcess => {
            this.$refs.processWidget && (this.$refs.processWidget.workProcess = workProcess);
          });
        }
      });
    },
    getTaskUserLabel(details) {
      return getTaskUserLabel(details);
    },
    getTaskDecisionMakerLabel(details) {
      return getTaskDecisionMakerLabel(details);
    },
    getTaskCopyUserLabel(details) {
      return getTaskCopyUserLabel(details);
    },
    getTaskMonitorUserLabel(details) {
      return getTaskMonitorUserLabel(details);
    },
    buildTreeItems(items, flowInstUuid) {
      let treeItems = [];
      let allItems = items.filter(item => item.flowInstUuid == flowInstUuid);
      let getChildItems = item => {
        let childItems = items.filter(child => child.preTaskInstUuid == item.taskInstUuid);
        return childItems;
      };
      let parentItemMap = {};
      let sortOrder = 1;
      allItems.forEach(item => {
        let childItems = getChildItems(item);
        if (childItems.length > 1) {
          item.sortOrder = sortOrder++;
          treeItems.push(item);
          childItems.forEach(childItem => {
            childItem.sortOrder = sortOrder++;
            treeItems.push(childItem);
            parentItemMap[childItem.taskInstUuid] = childItem;
          });
        } else {
          if (parentItemMap[item.taskInstUuid]) {
          } else {
            let parentItem = parentItemMap[item.preTaskInstUuid];
            if (parentItem) {
              let children = parentItem.children || [];
              item.sortOrder = parentItem.sortOrder * 100 + children.length + 1;
              children.push(item);
              parentItemMap[item.taskInstUuid] = parentItem;
              parentItem.children = children;
            } else {
              item.sortOrder = sortOrder++;
              treeItems.push(item);
            }
          }
        }
      });
      return treeItems;
    }
  }
};
</script>

<style lang="less" scoped>
.simulation-report-viewer {
  padding: var(--w-padding-md);
  ::v-deep .widget-table .ant-table-tbody > tr > td,
  ::v-deep .widget-table .ant-table-thead > tr > th {
    border-color: var(--w-border-color-light);
    background: #fff;
  }
}
</style>
