<template>
  <a-form-model
    class="basic-info"
    labelAlign="left"
    ref="basicForm"
    :label-col="{ span: 8 }"
    :wrapper-col="{ span: 16, style: { textAlign: 'right' } }"
    :colon="false"
  >
    <a-form-model-item label="名称">
      <a-input v-model="itemFlow.itemName" />
    </a-form-model-item>
    <a-form-model-item label="ID">
      <a-input v-model="itemFlow.itemId" disabled />
    </a-form-model-item>
    <a-form-model-item label="编码">
      <a-input v-model="itemFlow.itemCode" disabled />
    </a-form-model-item>
    <a-form-model-item label="备注">
      <a-textarea v-model="itemFlow.remark" />
    </a-form-model-item>
  </a-form-model>
</template>

<script>
export default {
  props: {
    designer: Object
  },
  data() {
    let itemFlow = this.designer.currentItemFlow || {};
    itemFlow.name = itemFlow.itemName || '';
    return {
      itemFlow
    };
  },
  watch: {
    'designer.currentItemFlow': {
      deep: false,
      handler(newVal) {
        this.itemFlow = newVal || {};
        this.updateItemFlowInfo();
      }
    }
  },
  mounted() {
    this.updateItemFlowInfo();
  },
  methods: {
    updateItemFlowInfo() {
      const _this = this;
      if (_this.itemFlow && _this.itemFlow.itemId) {
        let itemNode = _this.designer.getItemNodeByItemId(_this.itemFlow.itemId);
        if (itemNode) {
          let itemData = itemNode.getData();
          if (itemData && itemData.configuration) {
            _this.itemFlow.itemName = itemData.configuration.itemName;
            _this.itemFlow.itemCode = itemData.configuration.itemCode;
          }
        }
      }
    }
  }
};
</script>

<style></style>
