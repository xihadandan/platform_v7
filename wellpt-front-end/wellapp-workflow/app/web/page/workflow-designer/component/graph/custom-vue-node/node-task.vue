<template>
  <div :class="nodeClassName" @mouseenter="event => onMouseenter(event)" @mouseleave="onMouseleave" @mousemove="onMouseMove($event)">
    {{ nodeData.name }}
    <div class="node-timer-icon" v-if="showTimer">
      <i class="iconfont icon-naozhong-01" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'NodeTask',
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
        'node-task-container': true
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
    this.node.on('change:data', ({ current }) => {
      // console.log(current);
    });
  },
  methods: {
    onMouseenter(event) {
      // console.log('onMouseenter', event);
    },
    onMouseleave(event) {
      // console.log('onMouseleave', event);
    },
    onMouseMove(event) {
      // console.log(event);
    }
  }
};
</script>
