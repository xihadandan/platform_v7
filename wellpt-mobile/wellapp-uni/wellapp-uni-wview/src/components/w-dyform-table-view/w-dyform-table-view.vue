<template>
  <view class="dyform-table-view">
    <w-section
      :title="tableViewDefinition.displayName"
      type="line"
      iconType="paperplane"
      :extraIcon="tableViewDefinition.icon ? { type: tableViewDefinition.icon } : null"
      :collapsable="true"
    >
      <w-list-view v-if="widget" :widget="widget"></w-list-view>
    </w-section>
  </view>
</template>

<script>
import { isEmpty } from "lodash";
export default {
  props: {
    tableViewDefinition: {
      type: Object,
      required: true,
    },
    formScope: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      widget: null,
    };
  },
  created() {
    const _self = this;
    let tableViewWidgetId = _self.tableViewDefinition.tableViewWidgetId;
    if (isEmpty(tableViewWidgetId)) {
      return;
    }
    uni.request({
      service: "appContextService.getUniAppWidgetDefinitionById",
      data: [tableViewWidgetId],
      success: (res) => {
        let data = res.data.data;
        if (data && data.definitionJson) {
          let definitionJson = JSON.parse(data.definitionJson);
          _self.widget = definitionJson;
        }
      },
      fail: (e) => {
        console.error(e);
      },
      complete: () => {},
    });
  },
};
</script>

<style lang="scss" scoped>
.dyform-table-view {
  width: 100%;
  // height: 100%;
  overflow: auto;
}
</style>
