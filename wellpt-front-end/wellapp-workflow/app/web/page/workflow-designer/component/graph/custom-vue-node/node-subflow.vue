<template>
  <div :class="nodeClassName">
    <div class="node-subflow-circle"><i class="iconfont icon-oa-banliguocheng"></i></div>
    <div class="node-subflow-name">{{ nodeData.name }}</div>
    <div class="node-timer-icon" v-if="showTimer">
      <i class="iconfont icon-naozhong-01" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'NodeSubflow',
  inject: ['getNode', 'getGraph', 'designer'],
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
        'node-subflow-container': true
      };
      if (this.nodeData.state) {
        className[`node-subflow-${this.nodeData.state}`] = true;
      }
      return className;
    }
  },
  created() {
    if (!this.nodeData.hasOwnProperty('timerId')) {
      this.$set(this.nodeData, 'timerId', '');
    }
    this.node.on('change:data', ({ current }) => {
      console.log(current);
    });
  }
};
</script>
