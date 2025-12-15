<template>
  <div
    class="simulation-node-subflow"
    :class="nodeClassName"
    @mouseenter="event => onMouseenter(event)"
    @mouseleave="onMouseleave"
    @mousemove="onMouseMove($event)"
  >
    <div class="node-subflow-circle"><i class="iconfont icon-oa-banliguocheng"></i></div>
    <div class="node-subflow-name">{{ nodeData.name }}</div>
    <div class="node-timer-icon" v-if="showTimer">
      <i class="iconfont icon-naozhong-01" />
    </div>
    <div v-if="nodeData.state == 'todo'" class="tip-info" :title="tipInfo">
      {{ tipInfo }}
    </div>
  </div>
</template>

<script>
import SimulationNodeTask from './simulation-node-task.vue';
export default {
  name: 'NodeSubflow',
  extends: SimulationNodeTask,
  inject: ['getNode', 'graphInstance', 'simulation'],
  computed: {
    nodeClassName() {
      let className = {
        'node-subflow-container': true
      };
      if (this.nodeData.state) {
        className[`node-subflow-${this.nodeData.state}`] = true;
      }
      return className;
    }
  }
};
</script>

<style lang="less" scoped>
.simulation-node-subflow {
  position: relative;
  overflow: visible;

  &.node-collab-container .node-subflow-circle {
    left: -32px;
  }

  .tip-info {
    position: absolute;
    left: 58px;
    bottom: 0;
    width: 300px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }
  .actions {
    position: absolute;
    width: 200px;
    left: -33px;
    bottom: -12px;
  }
}
</style>
