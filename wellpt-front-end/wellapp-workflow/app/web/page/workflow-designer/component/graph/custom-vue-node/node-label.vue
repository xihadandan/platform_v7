<template>
  <div :class="nodeClassName">
    <!-- <template v-if="displayState === 'label'">
      <div>{{ nodeData.label }}</div>
    </template>
    <template v-else>
      <a-textarea v-model="nodeData.label" :autoFocus="true" />
    </template> -->

    <template v-if="displayState === 'label'">
      <div @click="hadleLabel">{{ nodeData.label }}</div>
    </template>
    <template v-else>
      <a-textarea v-model="nodeData.label" :autoFocus="true" @blur="blurLabel" />
    </template>
  </div>
</template>

<script>
export default {
  name: 'NodeLabel',
  inject: ['getNode', 'graphInstance'],
  data() {
    let nodeData = {};
    const node = this.getNode();
    if (node) {
      nodeData = node.getData();
    }
    const displayState = 'label';
    return {
      node,
      nodeData,
      displayState
    };
  },
  watch: {
    // 'graphInstance.selectedLabelId': {
    //   deep: true,
    //   handler(id) {
    //     if (id === this.node.id) {
    //       this.displayState = 'edit';
    //     } else {
    //       this.displayState = 'label';
    //     }
    //   }
    // }
  },
  computed: {
    nodeClassName() {
      let className = {
        'node-lable-container': true
      };
      if (this.nodeData.label) {
        className[`node-lable-has-label`] = true;
      }
      return className;
    }
  },
  methods: {
    hadleLabel() {
      this.displayState = 'edit';
    },
    blurLabel() {
      this.displayState = 'label';
    }
  }
};
</script>
