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
      <a-input v-model="configuration.name" readonly />
    </a-form-model-item>
    <a-form-model-item label="ID">
      <a-input v-model="node.id" readonly />
    </a-form-model-item>
    <a-form-model-item label="备注">
      <a-textarea v-model="configuration.remark" />
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import { Node } from '@antv/x6';
export default {
  props: {
    node: Node
  },
  data() {
    const _this = this;
    let nodeData = _this.node.getData();
    let configuration = nodeData.configuration;
    return {
      configuration
    };
  }
};
</script>

<style></style>
