<template>
  <HtmlWrapper :title="(processData && processData.title) || '业务流程办理单据'">
    <BizProcessInstanceView :processData="processData"></BizProcessInstanceView>
  </HtmlWrapper>
</template>

<script>
import BizProcessInstanceView from '../../template/biz-process/biz-process-instance-view.vue';
export default {
  components: { BizProcessInstanceView }
};
</script>

<style></style>
