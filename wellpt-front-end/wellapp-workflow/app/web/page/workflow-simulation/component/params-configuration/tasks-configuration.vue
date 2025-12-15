<template>
  <div>
    <a-table
      rowKey="id"
      :showHeader="false"
      size="small"
      :pagination="false"
      :bordered="false"
      :columns="taskTableColumn"
      :locale="locale"
      :data-source="params.tasks"
      :class="['simulation-task-table no-border']"
    >
      <template slot="taskUsersSlot" slot-scope="text, record">
        <template v-if="record.type == '1' || record.type == '3'">
          <span v-if="record.simulationTaskUserId">仿真办理人: {{ record.simulationTaskUserName }}</span>
          <span v-else>{{ simulation.getTaskUserLabel(record.id) }}</span>
          <div v-if="record.type == '3'">
            <span v-if="record.simulationDecisionMakerId">仿真决策人: {{ record.simulationDecisionMakerName }}</span>
            <span v-else>{{ simulation.getTaskDecisionMakerLabel(record.id) }}</span>
          </div>
        </template>
      </template>

      <template slot="operationSlot" slot-scope="text, record, index">
        <drawer
          :ref="'drawer_' + record.id"
          title="仿真环节配置"
          :width="650"
          :container="getDrawerContainer"
          :wrapStyle="{ position: 'absolute' }"
          wrapClassName="task-configuration-drawer-wrap"
          :afterVisibleChange="getAfterDrawerVisibleChangeFunction()"
        >
          <a-button v-if="record.type == '1' || record.type == '3'" type="link" size="small" title="配置" @click="openDrawer(record)">
            <Icon type="pticon iconfont icon-ptkj-shezhi" />
          </a-button>
          <template slot="content">
            <TaskConfiguration :task="record" :flowDefinition="flowDefinition"></TaskConfiguration>
          </template>
        </drawer>
      </template>
    </a-table>
  </div>
</template>

<script>
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import TaskConfiguration from './task-configuration.vue';
export default {
  props: {
    params: Object,
    flowDefinition: Object
  },
  components: { Drawer, TaskConfiguration },
  inject: ['simulation'],
  data() {
    // 同步环节信息
    this.syncTasks();

    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      taskTableColumn: [
        { title: '环节', dataIndex: 'name', width: 110, ellipsis: true },
        { title: '办理人', dataIndex: 'taskUsers', scopedSlots: { customRender: 'taskUsersSlot' } },
        { title: '操作', dataIndex: 'operation', width: 40, scopedSlots: { customRender: 'operationSlot' } }
      ]
    };
  },
  methods: {
    syncTasks() {
      const _this = this;
      let tasks = _this.params.tasks || [];
      let taskMap = {};
      tasks.forEach(task => {
        taskMap[task.id] = task;
      });
      let newTasks = [];
      _this.flowDefinition.tasks.forEach(task => {
        let taskConfig = {};
        if (taskMap[task.id]) {
          taskConfig = taskMap[task.id];
          taskConfig.name = task.name;
          taskConfig.id = task.id;
          taskConfig.type = task.type;
        } else {
          taskConfig = {
            name: task.name,
            id: task.id,
            type: task.type,
            opinions: [],
            formFields: []
          };
        }
        newTasks.push(taskConfig);
      });
      _this.params.tasks = newTasks;
    },
    getDrawerContainer() {
      return document.querySelector('.configuration-drawer-container');
    },
    getAfterDrawerVisibleChangeFunction() {
      const _this = this;
      return function (visible) {
        if (visible) {
          let drawers = _this.$refs;
          for (let key in drawers) {
            if (drawers[key] != this && drawers[key].visible && drawers[key].hide) {
              drawers[key].hide();
            }
          }
        }
      };
    },
    openDrawer(record) {
      document.querySelector('.configuration-drawer-container').style.display = 'none';
      setTimeout(() => {
        document.querySelector('.configuration-drawer-container').style.display = '';
        let drawer = this.$refs['drawer_' + record.id];
        if (drawer && drawer.visibleChange) {
          drawer.visibleChange(true);
        }
      }, 0);
    }
  }
};
</script>

<style></style>
