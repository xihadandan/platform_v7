<template>
  <HtmlWrapper :title="(itemData && itemData.title) || '事项办理单据'">
    <BizProcessItemInstanceView :itemData="itemData"></BizProcessItemInstanceView>
  </HtmlWrapper>
</template>

<script>
import BizProcessItemInstanceView from '../../template/biz-process/biz-process-item-instance-view.vue';
export default {
  components: { BizProcessItemInstanceView }
};
</script>

<style></style>
