<template>
  <div :class="nodeClassName">
    <div class="node-collab-left-rect"><i class="iconfont icon-xiezuo-01"></i></div>
    <div class="node-collab-name">{{ nodeData.name }}</div>
    <div class="node-timer-icon" v-if="showTimer">
      <i class="iconfont icon-naozhong-01" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'NodeCollab',
  inject: ['getNode', 'graphInstance'],
  data() {
    let nodeData = {};
    const node = this.getNode();
    if (node) {
      nodeData = node.getData();
    }
    return {
      node,
      nodeData
    };
  },
  computed: {
    showTimer() {
      return !!this.nodeData.timerId;
    },
    nodeClassName() {
      let className = {
        'node-collab-container': true
      };
      if (this.nodeData.state) {
        className[`node-task-${this.nodeData.state}`] = true;
      }
      return className;
    }
  },
  created() {
    if (!this.nodeData.hasOwnProperty('timerId')) {
      this.$set(this.nodeData, 'timerId', '');
    }
  }
};
</script>
