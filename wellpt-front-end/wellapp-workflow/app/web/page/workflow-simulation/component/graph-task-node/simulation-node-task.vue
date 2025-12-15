<template>
  <div
    class="simulation-node-task"
    :class="nodeClassName"
    @mouseenter="event => onMouseenter(event)"
    @mouseleave="onMouseleave"
    @mousemove="onMouseMove($event)"
  >
    {{ nodeData.name }}
    <div class="node-timer-icon" v-if="showTimer">
      <i class="iconfont icon-naozhong-01" />
    </div>
    <div v-if="nodeData.state == 'todo'" class="tip-info">
      {{ tipInfo }}
    </div>
    <div v-if="showAction && isPause" class="actions" :title="tipInfo">
      <a-button size="small" @click.stop="transfer">转办</a-button>
      <a-button size="small" @click.stop="counterSign">会签</a-button>
      <a-button size="small" @click.stop="gotoTask">跳转</a-button>
      <a-button size="small" @click.stop="viewForm">查看</a-button>
    </div>
  </div>
</template>

<script>
import { isEmpty } from 'lodash';
export default {
  name: 'NodeTask',
  inject: ['getNode', 'getGraph', 'designer', 'simulation'],
  data() {
    let nodeData = {};
    const node = this.getNode();
    if (node) {
      nodeData = node.getData();
    }
    return {
      node,
      nodeData,
      showAction: false
    };
  },
  computed: {
    showTimer() {
      return !!this.nodeData.timerId;
    },
    nodeClassName() {
      let className = {
        'node-task-container': true
      };
      if (this.nodeData.state) {
        className[`node-task-${this.nodeData.state}`] = true;
      }
      return className;
    },
    tipInfo() {
      const _this = this;
      let tip = '';
      let simulation = _this.simulation;
      let stateCode = simulation && simulation.state && simulation.state.code;
      let isRunning = stateCode == 'start' || stateCode == 'running';
      if (!isRunning || !simulation.simulationData) {
        return tip;
      }

      // 存在未完成子流程时取一个子流程信息
      let taskInfo = simulation.simulationData.subTaskInfo || simulation.simulationData;
      let taskName = taskInfo.taskName;
      let todoUserName = taskInfo.todoUserName;
      let superviseUserName = taskInfo.superviseUserName;
      let flowDefName = taskInfo.flowDefName || '';
      if (!isEmpty(taskName) && !isEmpty(todoUserName)) {
        tip = `${flowDefName ? flowDefName + ': ' : ''}${todoUserName} 提交中...`;
      } else if (!isEmpty(taskName) && !isEmpty(superviseUserName)) {
        tip = `${flowDefName ? flowDefName + ': ' : ''}${superviseUserName} 完成中...`;
      }

      if (simulation.isDispatching()) {
        tip = `子流程分发中(${taskInfo.dispatchingCount}/${taskInfo.totalCount})...`;
      } else if (isEmpty(taskInfo.flowInstUuid)) {
        let startUserName = simulation.params.startUserName;
        if (startUserName) {
          let startUserIndex = simulation.startUserIndex;
          let startUserNames = startUserName.split(';');
          if (startUserNames[startUserIndex]) {
            tip = `${startUserNames[startUserIndex]} 发起中...`;
          }
        }
      } else if (simulation.simulationData.taskId != _this.nodeData.id) {
        tip = '';
      }
      return tip;
    },
    isPause() {
      const _this = this;
      let simulationData = _this.simulation.simulationData || {};
      if (simulationData.taskId != _this.nodeData.id) {
        return false;
      }
      return _this.simulation && _this.simulation.state && _this.simulation.state.code == 'pause';
    }
  },
  created() {
    if (!this.nodeData.hasOwnProperty('timerId')) {
      this.$set(this.nodeData, 'timerId', '');
    }
    this.node.on('change:data', ({ current }) => {
      // console.log(current);
    });
  },
  methods: {
    onMouseenter(event) {
      this.showAction = true;
      // console.log('onMouseenter', event);
    },
    onMouseleave(event) {
      this.showAction = false;
      // console.log('onMouseleave', event);
    },
    onMouseMove(event) {
      // console.log(event);
    },
    transfer() {
      this.simulation.transfer();
    },
    counterSign() {
      this.simulation.counterSign();
    },
    gotoTask() {
      this.simulation.gotoTask();
    },
    viewForm() {
      this.simulation.viewForm();
    }
  }
};
</script>

<style lang="less" scoped>
.simulation-node-task {
  position: relative;
  overflow: visible;

  .tip-info {
    position: absolute;
    bottom: 0;
    width: 160px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    text-align: center;
  }
  .actions {
    position: absolute;
    width: 200px;
    left: -9px;
    bottom: -12px;
  }
}
</style>
