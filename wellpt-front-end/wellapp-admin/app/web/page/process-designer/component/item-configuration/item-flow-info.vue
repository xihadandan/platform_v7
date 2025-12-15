<template>
  <div>
    <a-form-model-item label="默认事项流">
      <div style="text-align: left">
        {{ itemDefinition.itemName }}
      </div>
    </a-form-model-item>
    <a-form-model-item label="参与的事项流">
      <div v-if="joinItems.length > 0" style="text-align: left">
        {{ joinItems.map(item => item.name).join('、') }}
      </div>
      <div v-else style="text-align: left">
        <a-icon type="search"></a-icon>
        暂无数据
      </div>
    </a-form-model-item>
  </div>
</template>

<script>
export default {
  props: {
    itemDefinition: Object
  },
  inject: ['itemFlowDesigner'],
  computed: {
    joinItems() {
      const _this = this;
      let joinItems = [];
      let itemFlows = _this.itemFlowDesigner.itemFlows || {};
      for (let itemId in itemFlows) {
        let itemFlow = itemFlows[itemId];
        if (_this.itemDefinition.id == itemId || !itemFlow.graphData || !itemFlow.graphData.cells) {
          continue;
        }
        if (_this.isInItemFlow(itemFlow)) {
          joinItems.push({
            name: itemFlow.name || itemFlow.itemName,
            id: itemFlow.itemId
          });
        }
      }
      return joinItems;
    }
  },
  methods: {
    isInItemFlow(itemFlow) {
      const _this = this;
      let cells = itemFlow.graphData.cells || [];
      let itemNodes = cells.filter(cell => cell.data && cell.data.type == 'item');
      for (let index = 0; index < itemNodes.length; index++) {
        let itemNode = itemNodes[index];
        if (itemNode.data.itemId == _this.itemDefinition.id) {
          return true;
        }
      }
      return false;
    }
  }
};
</script>

<style></style>
