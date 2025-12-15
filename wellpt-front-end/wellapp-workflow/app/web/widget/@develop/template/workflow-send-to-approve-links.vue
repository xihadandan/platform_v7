<template>
  <div class="widget-form-layout workflow-send-to-approve-links">
    <div class="layout-header">
      <label class="title">
        <i class="prefix" />
        {{ evtWidget.$t('WorkflowWork.relatedDoc', '相关文档') }}
      </label>
    </div>
    <div class="layout-body">
      <a-row class="form-row" v-for="(link, index) in links" :key="index">
        <a-col class="form-item" flex="auto" @click="onLinkClick(link)">
          <a>{{ link.title }}</a>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script>
import { isEmpty } from 'lodash';
export default {
  props: {
    links: Array,
    flowInstUuid: String,
    evtWidget: {
      type: Object,
      default: () => {}
    }
  },
  methods: {
    onLinkClick(link) {
      const _this = this;
      let linkUrl = link.url;
      if (isEmpty(linkUrl)) {
        console.error('link url is empty', link);
        return;
      }

      if (_this.flowInstUuid) {
        if (linkUrl.indexOf('?') == -1) {
          linkUrl += '?approveFlowInstUuid=' + _this.flowInstUuid;
        } else {
          linkUrl += '&approveFlowInstUuid=' + _this.flowInstUuid;
        }
      }
      window.open(linkUrl, '_blank');
    }
  }
};
</script>

<style lang="less" scoped>
.layout-body {
  padding: 0 10px;
  .form-item {
    padding: 6px 0px;
  }
}
</style>
