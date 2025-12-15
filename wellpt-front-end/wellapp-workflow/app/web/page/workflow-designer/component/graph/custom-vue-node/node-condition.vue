<template>
  <div :class="nodeClassName">
    <div class="node-diamond-container"></div>
    <div class="node-condition-name">{{ nodeData.name }}</div>
  </div>
</template>

<script>
export default {
  name: 'NodeCondition',
  inject: ['getNode', 'getGraph', 'designer'],
  data() {
    return {
      nodeData: undefined
    };
  },
  computed: {
    nodeClassName() {
      let className = {
        'node-condition-container': true
      };
      if (this.nodeData.state) {
        className[`node-condition-${this.nodeData.state}`] = true;
      }
      return className;
    }
  },
  created() {
    const node = this.getNode();
    this.nodeData = node.getData();
    node.on('change:data', ({ current }) => {
      console.log(current);
    });
  }
};
</script>
