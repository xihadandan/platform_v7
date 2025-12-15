<template>
  <div class="flow-report-summary">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: 'FlowReportSummary'
};
</script>

<style lang="less">
.flow-report-summary {
  margin-bottom: 12px;
  display: flex;
  > ._summary-item {
    flex: 1;
    margin-left: 6px;
    margin-right: 6px;
    &:first-child {
      margin-left: 0;
    }
    &:last-child {
      margin-right: 0;
    }
  }
}
</style>
