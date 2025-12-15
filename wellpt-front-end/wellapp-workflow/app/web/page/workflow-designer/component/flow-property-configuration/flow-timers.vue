<template>
  <!-- 流程属性-流程计时 -->
  <div>
    <a-form-model-item class="form-item-vertical" label="计时设置">
      <a-table
        rowKey="timerId"
        :showHeader="false"
        :pagination="false"
        size="small"
        :bordered="false"
        :columns="columnDefine"
        :locale="locale"
        :data-source="workFlowData.timers"
        class="timers-table no-border"
      >
        <!-- <template slot="title">
        <i class="line" />
        计时设置
      </template> -->
        <template slot="nameSlot" slot-scope="text">
          {{ text }}
        </template>
        <template slot="operationSlot" slot-scope="text, record, index">
          <a-button type="link" size="small" @click="setTimer(record, index)">
            <Icon type="pticon iconfont icon-ptkj-shezhi" />
          </a-button>
          <!-- 删除 -->
          <a-button type="link" size="small" @click="delTimer(record, index)">
            <Icon type="pticon iconfont icon-ptkj-shanchu" />
          </a-button>
        </template>
        <template slot="footer">
          <div class="timers-table-footer">
            <span>
              <a-button type="link" @click="addTimer" icon="plus">添加计时器</a-button>
            </span>
          </div>
        </template>
      </a-table>
    </a-form-model-item>
    <modal
      title="计时器"
      v-model="visible"
      :container="getContainer"
      :ok="saveTimer"
      okText="保存"
      :width="880"
      wrapperClass="flow-timer-modal-wrap"
    >
      <template slot="content">
        <flow-timer-info ref="refTimer" v-if="visible" :formData="currentTimer" :graphItem="graphItem" />
      </template>
    </modal>
    <more-show-component position="bottom">
      <a-form-model-item class="form-item-vertical" label="事件监听">
        <a-tree-select
          class="workflow-tree-select"
          v-model="timerListenerId"
          :treeData="timerListeners"
          :allowClear="true"
          :showSearch="true"
          :replaceFields="{
            children: 'children',
            title: 'name',
            key: 'id',
            value: 'id'
          }"
          @change="changeTimerListenerId"
        />
      </a-form-model-item>
    </more-show-component>
  </div>
</template>

<script>
import FlowTimer from '../designer/FlowTimer';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import FlowTimerInfo from './flow-timer-info.vue';
import MoreShowComponent from '../commons/more-show-component.vue';

export default {
  name: 'FlowPropertyFlowTimers',
  inject: ['workFlowData', 'graph'],
  props: {
    graphItem: {
      type: Object,
      default: () => {}
    },
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    Modal,
    FlowTimerInfo,
    MoreShowComponent
  },
  data() {
    let timerListenerId = [];
    if (this.formData.timerListener) {
      const timerListener = this.formData.timerListener;
      if (typeof timerListener === 'string') {
        if (timerListener.indexOf(';')) {
          timerListenerId = timerListener.split(';');
        } else {
          timerListenerId = [timerListener];
        }
      }
    }
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      columnDefine: [
        { title: '名称', dataIndex: 'name', scopedSlots: { customRender: 'nameSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' }, align: 'center' }
      ],
      currentIndex: 0,
      currentTimer: undefined,
      visible: false,
      timerListenerId,
      timerListeners: []
    };
  },
  computed: {
    // 环节数据
    tasksData() {
      let tasksData = [];
      if (this.graph.instance) {
        tasksData = this.graph.instance.tasksData;
      }
      return tasksData;
    },
    // 子流程数据
    subflowsData() {
      let tasksData = [];
      if (this.graph.instance) {
        tasksData = this.graph.instance.subflowsData;
      }
      return tasksData;
    }
  },
  created() {
    this.getTimerListeners();
  },
  methods: {
    setTimer(record, index) {
      this.currentIndex = index;
      this.currentTimer = JSON.parse(JSON.stringify(record));
      this.visible = true;
    },
    delTimer(record, index) {
      this.delTasksTimerId(record.tasks);
      if (record.subTasks.length) {
        this.delSubTasksTimerId(record.subTasks);
      }
      this.workFlowData.timers.splice(index, 1);
    },
    addTimer() {
      this.currentTimer = new FlowTimer();
      this.visible = true;
    },
    saveTimer(callback) {
      this.$refs.refTimer.validate(({ valid, error, data }) => {
        if (valid) {
          const timerIndex = this.workFlowData.timers.findIndex(item => {
            return item.timerId === this.currentTimer.timerId;
          });

          if (timerIndex === -1) {
            this.workFlowData.timers.push(this.currentTimer);
          } else {
            const beforeTasks = this.workFlowData.timers[timerIndex]['tasks'];
            this.delTasksTimerId(beforeTasks);
            const beforeSubTasks = this.workFlowData.timers[timerIndex]['subTasks'];
            if (beforeSubTasks.length) {
              this.delSubTasksTimerId(beforeSubTasks);
            }
            this.workFlowData.timers.splice(timerIndex, 1, this.currentTimer);
          }
          this.setTasksTimerId(this.currentTimer.tasks);
          if (this.currentTimer.subTasks.length) {
            // 子流程计时不是必填
            this.setSubTasksTimerId(this.currentTimer.subTasks);
          }
          callback(true);
        }
      });
    },
    // 设置环节计时id
    setTasksTimerId(tasks) {
      let taskIds = [];
      tasks.forEach(item => {
        taskIds.push(item.value);
      });
      this.tasksData.forEach(data => {
        if (taskIds.includes(data.id)) {
          data.timerId = this.currentTimer.timerId;
        }
      });
    },
    // 设置子流程计时id
    setSubTasksTimerId(tasks) {
      let taskIds = [];
      tasks.forEach(item => {
        taskIds.push(item.taskId);
      });
      this.subflowsData.forEach(data => {
        if (taskIds.includes(data.id)) {
          data.timerId = this.currentTimer.timerId;
        }
      });
    },
    // 删除环节计时id
    delTasksTimerId(tasks) {
      tasks.forEach(item => {
        this.tasksData.forEach(data => {
          if (data.id === item.value) {
            data.timerId = '';
          }
        });
      });
    },
    // 删除子流程计时id
    delSubTasksTimerId(tasks) {
      tasks.forEach(item => {
        this.subflowsData.forEach(data => {
          if (data.id === item.taskId) {
            data.timerId = '';
          }
        });
      });
    },
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    },
    // 改变计时监听器
    changeTimerListenerId(value, label, extra) {
      this.formData.timerListener = value;
    },
    // 获取计时事件监听
    getTimerListeners() {
      const params = {
        args: JSON.stringify([-1]),
        serviceName: 'flowSchemeService',
        methodName: 'getTimerListeners'
      };
      this.$axios
        .post('/json/data/services', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              this.timerListeners = data;
            }
          }
        });
    }
  }
};
</script>
