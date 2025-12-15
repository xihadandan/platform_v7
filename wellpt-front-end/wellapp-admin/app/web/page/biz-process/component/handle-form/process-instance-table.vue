<template>
  <a-card size="small" :title="title" :bordered="false">
    <template slot="extra">
      <a-button icon="arrow-right" @click="onStartItemClick" size="small" type="link">发起办件</a-button>
    </template>
    <PerfectScrollbar style="height: calc(100vh - 120px)">
      <WidgetTable v-if="tableWidget" ref="tableWidget" :widget="tableWidget" @beforeLoadData="beforeLoadData"></WidgetTable>
    </PerfectScrollbar>
  </a-card>
</template>

<script>
import BizProcessStartItem from '@admin/app/web/widget/@develop/biz-process/BizProcessStartItem.js';
import WidgetTable from '@pageAssembly/app/web/widget/widget-table/widget-table.vue';
import WidgetTableSearchForm from '@pageAssembly/app/web/widget/widget-table/widget-table-search-form.vue';
export default {
  components: { WidgetTable },
  inject: ['assemble'],
  provide() {
    return {
      namespace: 'process-instance-table',
      vPageState: this.vPageState || '',
      $pageJsInstance: {},
      designer: null,
      designMode: false,
      draggableConfig: {},
      designWidgetTypes: [],
      parentLayContentId: ''
    };
  },
  data() {
    return { title: '业务流程办件', widgetTableId: 'RJlQaBeQaHjWekrUSGoAhVOaagTlzaxK', tableWidget: undefined };
  },
  created() {
    if (Vue) {
      Vue.component(WidgetTableSearchForm.name, WidgetTableSearchForm);

      this.getTableDefinition().then(tableDefinition => {
        this.beforeMountTable(tableDefinition);
        this.tableWidget = tableDefinition;
      });
    }
  },
  methods: {
    getTableDefinition() {
      // 取表格定义
      let widgetTableId = this.widgetTableId;
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'appContextService',
            methodName: 'getAppWidgetDefinitionById',
            args: JSON.stringify([widgetTableId, false])
          })
          .then(({ data: { data = {} } }) => {
            if (data.definitionJson) {
              let tableWidgetDefinition = JSON.parse(data.definitionJson);
              resolve(tableWidgetDefinition);
            } else {
              reject(data);
            }
          })
          .catch(res => {
            reject(res);
          });
      });
    },
    beforeMountTable() {},
    beforeLoadData(params) {
      params.criterions = [
        ...(params.criterions || []),
        { columnIndex: 'processDefUuid', type: 'eq', value: this.assemble.processDefinition.uuid }
      ];
    },
    onStartItemClick() {
      let bizProcessStartItem = new BizProcessStartItem(this);
      bizProcessStartItem.startItem({
        processDefId: this.assemble.processDefinition.id
      });
    }
  }
};
</script>

<style></style>
