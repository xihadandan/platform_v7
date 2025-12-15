<template>
  <HtmlWrapper :title="(nodeData && nodeData.title) || '阶段办理单据'">
    <BizProcessNodeInstanceView :nodeData="nodeData"></BizProcessNodeInstanceView>
  </HtmlWrapper>
</template>

<script>
import BizProcessNodeInstanceView from '../../template/biz-process/biz-process-node-instance-view.vue';
export default {
  components: { BizProcessNodeInstanceView }
};
</script>

<style></style>
