<template>
  <div :class="nodeClassName">
    <div class="node-circle-content">{{ nodeData.name }}</div>
  </div>
</template>

<script>
export default {
  name: 'NodeCircle',
  inject: ['getNode', 'getGraph', 'designer'],
  data() {
    return {
      nodeData: undefined
    };
  },
  computed: {
    nodeClassName() {
      let className = {
        'node-circle-container': true
      };
      if (this.nodeData.state) {
        className[`node-circle-${this.nodeData.state}`] = true;
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
