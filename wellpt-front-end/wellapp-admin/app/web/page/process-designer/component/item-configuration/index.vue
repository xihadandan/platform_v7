<template>
  <a-form-model
    class="basic-info"
    :model="itemDefinition"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 8 }"
    :wrapper-col="{ span: 16, style: { textAlign: 'right' } }"
    :colon="false"
  >
    <a-collapse accordion :bordered="false" expandIconPosition="right" defaultActiveKey="basicInfo">
      <a-collapse-panel key="basicInfo" header="基本信息">
        <div v-if="isRefNode" style="width: 100%; height: 100%; display: block; position: absolute; z-index: 10"></div>
        <a-form-model-item label="名称" prop="itemName">
          <a-input v-model="itemDefinition.itemName" />
        </a-form-model-item>
        <a-form-model-item label="ID" prop="id">
          <a-input v-model="itemDefinition.id" readOnly />
        </a-form-model-item>
        <a-form-model-item label="编码" prop="itemCode">
          <div>
            <a-row>
              <a-col span="22">
                <a-input v-model="itemDefinition.itemCode" readOnly />
              </a-col>
              <a-col span="2">
                <ProcessDesignDrawer :id="chooseItemDrawerId" title="选择事项" :disabled="itemDefinition.itemChangable === false">
                  <a-button icon="select" type="link" title="选择事项" @click="onItemCodeClick"></a-button>
                  <template slot="content">
                    <WidgetTable
                      v-if="itemWidgetTableDefinition"
                      ref="itemWidgetTable"
                      :widget="itemWidgetTableDefinition"
                      @beforeLoadData="beforeItemTableLoadData"
                    ></WidgetTable>
                  </template>
                  <template slot="footer">
                    <a-button size="small" type="primary" @click.stop="onSelectItemConfirmOk">确定</a-button>
                  </template>
                </ProcessDesignDrawer>
              </a-col>
            </a-row>
          </div>
        </a-form-model-item>
        <a-form-model-item label="时限类型" prop="itemCode">
          <a-select v-model="itemDefinition.timeLimitType">
            <a-select-option :value="1">工作日</a-select-option>
            <a-select-option :value="2">自然日</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="事件监听" prop="listener">
          <a-select v-model="listeners" mode="multiple" show-search style="width: 100%" :filter-option="filterSelectOption">
            <a-select-option v-for="d in listenerOptions" :key="d.id">
              {{ d.text }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="是否必办件" prop="mandatory">
          <a-switch v-model="itemDefinition.mandatory" checked-children="是" un-checked-children="否" />
        </a-form-model-item>
        <a-form-model-item label="里程碑" prop="milestone">
          <a-switch v-model="itemDefinition.milestone" checked-children="是" un-checked-children="否" />
        </a-form-model-item>
        <ItemSituationConfiguration :itemDefinition="itemDefinition" :closeOpenDrawer="false"></ItemSituationConfiguration>
      </a-collapse-panel>
      <a-collapse-panel v-if="false" key="formInfo" header="事项办理单">
        <div v-if="isRefNode" style="width: 100%; height: 100%; display: block; position: absolute; z-index: 10"></div>
        <ItemFormSettings
          :processDefinition="designer.processDefinition"
          :itemDefinition="itemDefinition"
          :formConfig="itemDefinition.formConfig"
        ></ItemFormSettings>
      </a-collapse-panel>
      <a-collapse-panel v-if="itemDefinition.itemType == '20'" key="includeItems" header="包含事项">
        <div v-if="isRefNode" style="width: 100%; height: 100%; display: block; position: absolute; z-index: 10"></div>
        <ItemCombinedConfiguration :itemDefinition="itemDefinition"></ItemCombinedConfiguration>
      </a-collapse-panel>
      <a-collapse-panel key="itemFlowInfo" header="事项流信息">
        <div v-if="isRefNode" style="width: 100%; height: 100%; display: block; position: absolute; z-index: 10"></div>
        <ItemFlowInfo :itemDefinition="itemDefinition" />
      </a-collapse-panel>
      <a-collapse-panel key="bizIsInfo" header="业务集成">
        <div v-if="isRefNode" style="width: 100%; height: 100%; display: block; position: absolute; z-index: 10"></div>
        <ItemBusinessIntegrationConfigs
          :processDefinition="designer.processDefinition"
          :itemDefinition="itemDefinition"
        ></ItemBusinessIntegrationConfigs>
      </a-collapse-panel>
      <a-collapse-panel key="defineEvents" header="事件设置">
        <div v-if="isRefNode" style="width: 100%; height: 100%; display: block; position: absolute; z-index: 10"></div>
        <ItemDefineEvents :processDefinition="designer.processDefinition" :itemDefinition="itemDefinition"></ItemDefineEvents>
      </a-collapse-panel>
    </a-collapse>
  </a-form-model>
</template>

<script>
import ProcessDesignDrawer from '../process-design-drawer.vue';
import ItemSituationConfiguration from './item-situation-configuration.vue';
import ItemFormSettings from './item-form-settings.vue';
import ItemDefineEvents from './item-define-events.vue';
import ItemCombinedConfiguration from './item-combined-configuration.vue';
import ItemFlowInfo from './item-flow-info.vue';
import ItemBusinessIntegrationConfigs from './item-business-integration-configs.vue';
import { Node } from '@antv/x6';
import WidgetTable from '@pageAssembly/app/web/widget/widget-table/widget-table.vue';
import WidgetTableSearchForm from '@pageAssembly/app/web/widget/widget-table/widget-table-search-form.vue';
export default {
  props: {
    designer: Object,
    node: Node
  },
  name: 'ItemConfiguration',
  components: {
    ProcessDesignDrawer,
    ItemSituationConfiguration,
    ItemFormSettings,
    ItemDefineEvents,
    ItemCombinedConfiguration,
    ItemFlowInfo,
    ItemBusinessIntegrationConfigs,
    WidgetTable
  },
  inject: ['pageContext', 'filterSelectOption', 'getCacheData'],
  data() {
    const _this = this;
    // let node = _this.$attrs.node;
    let nodeData = _this.node.getData();
    // _this.node = node;
    if (nodeData.configuration.formConfig == null) {
      nodeData.configuration.timeLimitType = 1;
      _this.$set(nodeData.configuration, 'formConfig', { configType: '2' });
    }
    if (!nodeData.configuration.defineEvents || nodeData.configuration.defineEvents.length == 0) {
      let defaultEvents = [
        { name: '创建', id: 'created', builtIn: true },
        { name: '开始', id: 'started', builtIn: true },
        { name: '挂起', id: 'suspended', builtIn: true },
        { name: '恢复', id: 'resumed', builtIn: true },
        { name: '完成', id: 'completed', builtIn: true },
        { name: '取消', id: 'cancelled', builtIn: true }
      ];
      this.$set(nodeData.configuration, 'defineEvents', defaultEvents);
      this.$set(nodeData.configuration, 'eventPublishConfigs', []);
    }
    return {
      itemDefinition: nodeData.configuration,
      rules: {},
      itemWidgetTableDefinition: null,
      chooseItemDrawerId: 'chooseItemDrawerId_' + new Date().getTime(),
      listeners: nodeData.configuration.listener ? nodeData.configuration.listener.split(';') : [],
      listenerOptions: [],
      nodeRefInfo: _this.designer.getNodeRefInfo(_this.node)
    };
  },
  watch: {
    listeners: function (val) {
      this.itemDefinition.listener = val.join(';');
    },
    'itemDefinition.itemName': function (newVal) {
      this.node.setAttrs({ label: { text: newVal } });
    }
  },
  computed: {
    isRefNode() {
      return this.nodeRefInfo.ref;
    }
  },
  created() {
    this.handleListenerSearch();
  },
  methods: {
    onItemCodeClick() {
      // 事项流中组合事项包含事项的事项编码不可变更
      if (this.itemDefinition.itemChangable === false) {
        return false;
      }
      if (Vue) {
        Vue.component(WidgetTableSearchForm.name, WidgetTableSearchForm);
        this.getItemWidgetTableDefinition().then(widgetDefinition => {
          this.itemWidgetTableDefinition = widgetDefinition;
        });
      }
    },
    getItemWidgetTableDefinition() {
      // 无法直接取表格定义，从卡片定义下的组件取
      let widgetCardId = 'KJIxeKykSbIjZnxBRSDosFDqfurGesOG';
      return this.getCacheData(`widgetDefinition_${widgetCardId}`, (resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'appContextService',
            methodName: 'getAppWidgetDefinitionById',
            args: JSON.stringify([widgetCardId, false])
          })
          .then(({ data: { data = {} } }) => {
            if (data.definitionJson) {
              let cardWidgetDefinition = JSON.parse(data.definitionJson);
              if (cardWidgetDefinition.configuration.widgets.length > 0) {
                resolve(cardWidgetDefinition.configuration.widgets[0]);
              }
            } else {
              reject(data);
            }
          })
          .catch(res => {
            reject(res);
          });
      });
    },
    beforeItemTableLoadData(parems) {
      let tableWidget = this.$refs.itemWidgetTable;
      let dataSource = tableWidget.getDataSourceProvider();
      dataSource.addParam('processDefUuid', this.designer.processDefinition.uuid);
    },
    onSelectItemConfirmOk() {
      let selectedRows = this.$refs.itemWidgetTable.getSelectedRows();
      if (selectedRows.length != 1) {
        this.$message.error('请选择一条记录！');
        return;
      }

      let selectedRow = selectedRows[0];
      this.$set(this.itemDefinition, 'itemDefName', selectedRow.itemDefName);
      this.$set(this.itemDefinition, 'itemDefId', selectedRow.itemDefId);
      this.$set(this.itemDefinition, 'itemName', selectedRow.itemName || '');
      this.$set(this.itemDefinition, 'itemCode', selectedRow.itemCode);
      this.$set(this.itemDefinition, 'itemType', selectedRow.itemType);

      if (selectedRow.itemType == '10') {
        this.itemDefinition.includeItems = [];
        this.itemDefinition.mutexItems = [];
        this.itemDefinition.relateItems = [];
      }

      this.pageContext.emitEvent('closeDrawer:' + this.chooseItemDrawerId);
    },
    handleListenerSearch(value = '') {
      let _this = this;
      _this
        .getCacheData('itemListenerOptions', (resolve, reject) => {
          this.$axios
            .post('/common/select2/query', {
              serviceName: 'bizProcessDefinitionFacadeService',
              queryMethod: 'listBizProcessItemListenerSelectData',
              searchValue: value
            })
            .then(({ data }) => {
              if (data.results) {
                resolve(data.results);
              }
            })
            .catch(err => reject(err));
        })
        .then(options => {
          _this.listenerOptions = options;
        });
    }
  }
};
</script>

<style lang="less" scoped>
.basic-info {
  ::v-deep .ant-collapse-content-active {
    position: relative;
  }
}
</style>
