<template>
  <div
    class="simulation-node-collab"
    :class="nodeClassName"
    @mouseenter="event => onMouseenter(event)"
    @mouseleave="onMouseleave"
    @mousemove="onMouseMove($event)"
  >
    <div class="node-collab-left-rect"><i class="iconfont icon-xiezuo-01"></i></div>
    <div class="node-collab-name">{{ nodeData.name }}</div>
    <div class="node-timer-icon" v-if="showTimer">
      <i class="iconfont icon-naozhong-01" />
    </div>
    <div v-if="nodeData.state == 'todo'" class="tip-info" :title="tipInfo">
      {{ tipInfo }}
    </div>
    <div v-if="showAction && isPause" class="actions">
      <a-button size="small" @click.stop="transfer">转办</a-button>
      <a-button size="small" @click.stop="counterSign">会签</a-button>
      <a-button size="small" @click.stop="gotoTask">跳转</a-button>
      <a-button size="small" @click.stop="viewForm">查看</a-button>
    </div>
  </div>
</template>

<script>
import SimulationNodeTask from './simulation-node-task.vue';
export default {
  name: 'NodeCollab',
  extends: SimulationNodeTask,
  inject: ['getNode', 'graphInstance', 'simulation'],
  computed: {
    nodeClassName() {
      let className = {
        'node-collab-container': true
      };
      if (this.nodeData.state) {
        className[`node-task-${this.nodeData.state}`] = true;
      }
      return className;
    }
  }
};
</script>

<style lang="less" scoped>
.simulation-node-collab {
  position: relative;
  overflow: visible;

  &.node-collab-container .node-collab-left-rect {
    left: -32px;
  }

  .tip-info {
    position: absolute;
    bottom: 0;
    width: 138px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    text-align: center;
  }
  .actions {
    position: absolute;
    width: 200px;
    left: -33px;
    bottom: -12px;
  }
}
</style>
