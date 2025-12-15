<template>
  <div class="flow-report-detail-handle">
    <slot></slot>
    <a-tooltip :title="`${expanded ? '展开' : '折叠'}`">
      <a-button @click="handleToggleExpand">
        <Icon :type="`iconfont ${expanded ? 'icon-ptkj-zhankai2' : 'icon-ptkj-shouqi2'}`" />
      </a-button>
    </a-tooltip>
  </div>
</template>

<script>
export default {
  name: 'FlowReportDetailHandle',
  inject: ['pageContext'],
  data() {
    return {
      expanded: false // 展开
    };
  },
  methods: {
    handleToggleExpand() {
      const wId = this.$el.nextElementSibling.getAttribute('w-id');
      if (this.expanded) {
        this.$emit('collapse');
        this.collapseTable(wId);
      } else {
        this.$emit('expand');
        this.expandTable(wId);
      }

      this.expanded = !this.expanded;
    },
    expandTable(wId) {
      if (!wId) {
        return;
      }
      const _this = this;
      let tableWidget = _this.pageContext.getVueWidgetById(wId);
      let dataSourceProvider = tableWidget.getDataSourceProvider();
      if (dataSourceProvider && dataSourceProvider.data && dataSourceProvider.data.data) {
        tableWidget.expandedRowKeys = dataSourceProvider.data.data.map(item => item.uuid);
      }
    },
    collapseTable(wId) {
      if (!wId) {
        return;
      }
      const _this = this;
      let tableWidget = _this.pageContext.getVueWidgetById(wId);
      tableWidget.expandedRowKeys = [];
    }
  }
};
</script>

<style lang="less">
.flow-report-detail-handle {
  display: flex;
  flex-flow: row-reverse;
  padding-bottom: 12px;
  > .ant-btn {
    width: 32px;
    padding: 0;
    margin-left: 8px;
    &:hover {
      border-color: var(--w-button-border-color-hover);
      color: var(--w-button-font-color-hover);
      background-color: var(--w-button-background-hover);
    }
  }
}
</style>
