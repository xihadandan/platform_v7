<template>
  <div class="item-flow-designer-node flex" :class="{ selected: selected, error: itemDefinition.error }">
    <div :class="['f_s_0 icon']" style="width: 36px">
      <Icon v-if="itemDefinition.error" type="pticon iconfont icon-ptkj-weixianjinggaotishiyuqi"></Icon>
      <Icon v-else type="pticon iconfont icon-ptkj-xiangmuguanli"></Icon>
    </div>
    <div class="text f_g_1" :title="itemDefinition.itemName">
      {{ itemDefinition.itemName }}
    </div>
  </div>
</template>

<script>
import { createNodeTemplate, getNodeChildrenSize, moveNodeToViewportIfRequired } from '../designer/utils';
import { findIndex } from 'lodash';
export default {
  name: 'ItemFlowNode',
  components: {},
  inject: ['getNode', 'getGraph', 'designer'],
  data() {
    return {
      selected: false
    };
  },
  computed: {},
  created() {
    const _this = this;
    let node = _this.getNode();
    if (!node.vm) {
      node.vm = _this;
    }
    _this.nodeData = node.getData();
    _this.itemDefinition = _this.nodeData.configuration || _this.nodeData;
  },
  mounted() {
    if (this.designer.selectedNodeId == this.getNode().id) {
      this.select();
    }
  },
  methods: {
    select() {
      this.selected = true;
      moveNodeToViewportIfRequired(this.getNode(), this.getGraph());
    },
    unselect() {
      this.selected = false;
    }
  }
};
</script>

<style lang="less">
.item-flow-designer-node {
  position: relative;
  --w-item-flow-designer-item-border-color: var(--w-primary-color);
  --w-item-flow-designer-item-text-color: var(--w-font-size-base);
  --w-item-flow-designer-item-border-color-selected: var(--w-primary-color-3);

  border-radius: 4px;
  border: 2px solid var(--w-item-flow-designer-item-border-color);
  background-color: #ffffff;
  cursor: pointer;

  &.error {
    --w-item-flow-designer-item-border-color: var(--w-danger-color);
    --w-item-flow-designer-item-border-color-selected: var(--w-danger-color-3);
  }

  &.selected {
    box-shadow: 0 0 0 6px var(--w-item-flow-designer-item-border-color-selected);
  }

  .icon {
    line-height: 36px;
    text-align: center;
    background-color: var(--w-item-flow-designer-item-border-color);
    color: #ffffff;
    i {
      font-size: 20px;
    }
  }

  .text {
    font-size: var(--w-font-size-base);
    color: var(--w-item-flow-designer-item-text-color);
    line-height: 36px;
    padding-left: 12px;
    padding-right: 12px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }
}
</style>
