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
      <a-input v-model="configuration.name" />
    </a-form-model-item>
    <a-form-model-item label="ID">
      <a-input v-model="node.id" readonly />
    </a-form-model-item>
    <template v-if="(sourceNodeIsSingleItem || sourceNodeIsCombinedItem) && targetNodeIsItem">
      <a-form-model-item label="目标事项(发起方式)" :label-col="{ span: 10 }" :wrapper-col="{ span: 14 }">
        <div style="text-align: left">
          <a-radio-group v-model="configuration.startWay">
            <a-radio v-if="sourceNodeIsSingleItem" value="1" :style="radioStyle">上一事项办结时发起</a-radio>
            <a-radio v-if="sourceNodeIsSingleItem" value="2" :style="radioStyle">上一事项触发事件时发起</a-radio>
            <a-radio v-if="!sourceNodeIsSingleItem && sourceNodeIsCombinedItem" value="20" :style="radioStyle">
              监听组合事项办理事件发生
            </a-radio>
          </a-radio-group>
        </div>
      </a-form-model-item>
      <a-form-model-item v-show="configuration.startWay == '2' || configuration.startWay == '20'" label="选择事项事件" prop="listenEvents">
        <a-select
          v-model="configuration.listenEvents"
          mode="multiple"
          show-search
          style="width: 100%"
          :options="sourceItemEventOptions"
          :filter-option="filterSelectOption"
        ></a-select>
      </a-form-model-item>
      <a-form-model-item label="使用表单">
        <div style="text-align: left">
          <a-radio-group v-model="configuration.formDataType" @change="onFormDataTypeChange">
            <a-radio v-if="sourceNodeIsSingleItem" value="1" :style="radioStyle">直接使用上一事项办理单数据</a-radio>
            <a-radio v-if="sourceNodeIsSingleItem" value="2" :style="radioStyle">通过上一事项单据转换获取</a-radio>
            <a-radio v-if="sourceNodeIsCombinedItem" value="10" :style="radioStyle">直接使用组合事项办理单数据</a-radio>
            <a-radio v-if="sourceNodeIsCombinedItem" value="20" :style="radioStyle">通过组合事项单据转换获取</a-radio>
          </a-radio-group>
        </div>
      </a-form-model-item>
      <template v-if="['2', '20'].indexOf(configuration.formDataType) != -1">
        <a-form-model-item label="单据转换规则" prop="copyBotRuleId">
          <a-select v-model="configuration.copyBotRuleId" show-search style="width: 100%" :filter-option="filterSelectOption">
            <a-select-option v-for="d in botRuleOptions" :key="d.id">
              {{ d.text }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item>
          <template slot="label">
            <a-space>
              事项办结回写
              <a-popover>
                <template slot="content">事项办结时回写数据至上一事项办理单</template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
          <a-switch v-model="configuration.returnWithOver" checked-children="是" un-checked-children="否" @change="onFormDataTypeChange" />
        </a-form-model-item>
        <a-form-model-item>
          <template slot="label">
            <a-space>
              事项事件回写
              <a-popover>
                <template slot="content">事项在触发事件时回写数据至上一事项办理单</template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
          <a-switch v-model="configuration.returnWithEvent" checked-children="是" un-checked-children="否" @change="onFormDataTypeChange" />
        </a-form-model-item>
        <a-form-model-item v-if="configuration.returnWithEvent" label="回写事件">
          <a-select
            v-model="configuration.returnEvents"
            mode="multiple"
            show-search
            style="width: 100%"
            :options="returnItemEventOptions"
            :filter-option="filterSelectOption"
          ></a-select>
        </a-form-model-item>
        <a-form-model-item v-if="configuration.returnWithOver || configuration.returnWithEvent" label="回写规则">
          <a-select v-model="configuration.returnBotRuleId" show-search style="width: 100%" :filter-option="filterSelectOption">
            <a-select-option v-for="d in botRuleOptions" :key="d.id">
              {{ d.text }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
      </template>
      <a-form-model-item v-if="targetWorkflowIntegration" label="提交环节" prop="toTaskId">
        <a-select v-model="configuration.toTaskId" show-search style="width: 100%" :filter-option="filterSelectOption">
          <a-select-option :value="'AUTO_SUBMIT'">自动提交</a-select-option>
          <a-select-option v-for="d in targetFlowOptionData.taskIds" :key="d.id">
            {{ d.text }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
    </template>
    <a-form-model-item label="备注">
      <a-textarea v-model="configuration.remark" />
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import { Node } from '@antv/x6';
export default {
  props: {
    designer: Object,
    node: Node
  },
  inject: ['filterSelectOption', 'getCacheData'],
  data() {
    const _this = this;
    let nodeData = _this.node.getData();
    if (nodeData == null) {
      nodeData = { configuration: {} };
      _this.node.setData(nodeData);
    }
    let configuration = nodeData.configuration;
    return {
      configuration,
      botRuleOptions: [],
      radioStyle: { display: 'block', textAlign: 'left' },
      targetFlowOptionData: {
        directions: [],
        formFields: [],
        taskIds: []
      }
    };
  },
  computed: {
    // 源节点是否单个事项
    sourceNodeIsSingleItem() {
      const _this = this;
      return _this.checkSourceNodeIsSingleItem(_this.node);
    },
    // 源事项是否组合事项
    sourceNodeIsCombinedItem() {
      let sourceNode = this.getSourceNode();
      return sourceNode != null && sourceNode.getParent() != null;
    },
    sourceItemEventOptions() {
      const _this = this;
      let options = [];
      let sourceNode = _this.getSourceItemNode();
      if (sourceNode != null) {
        options = _this.getItemNodeDefineEvents(sourceNode);
      }
      return options;
    },
    // 目标事项是否事项或组合事项
    targetNodeIsItem() {
      const _this = this;
      let targetNode = _this.getTargetNode();
      if (targetNode != null) {
        let nodeData = targetNode.getData();
        if (nodeData && nodeData.type == 'item') {
          return true;
        }
      }
      return false;
    },
    // 目标事项流程集成
    targetWorkflowIntegration() {
      const _this = this;
      let targetNode = _this.getTargetNode();
      let workflowIntegration = _this.getItemNodeWorkflowIntegration(targetNode);
      return workflowIntegration;
    },
    returnItemEventOptions() {
      const _this = this;
      let targetNode = _this.getTargetNode();
      if (targetNode != null) {
        let nodeData = targetNode.getData();
        if (nodeData && nodeData.type == 'item') {
          return _this.getItemNodeDefineEvents(targetNode);
        }
      }
      return [];
    }
  },
  mounted() {
    // 节点数据更新为VUE数据引用
    this.node.getData().configuration = this.configuration;

    this.onFormDataTypeChange();

    if (this.targetWorkflowIntegration) {
      this.loadTargetFlowOptionData(this.targetWorkflowIntegration.flowDefId);
    }
  },
  methods: {
    checkSourceNodeIsSingleItem(edge) {
      const _this = this;
      let sourceNode = _this.getSourceNode(edge);
      if (sourceNode != null) {
        let nodeData = sourceNode.getData();
        if (nodeData && nodeData.type == 'item') {
          return true;
        } else if (nodeData && nodeData.type == 'gateway') {
          let incomingEdges = _this.designer.graph.getIncomingEdges(sourceNode);
          for (let i = 0; i < incomingEdges.length; i++) {
            if (_this.checkSourceNodeIsSingleItem(incomingEdges[i])) {
              return true;
            }
          }
        }
      }
      return false;
    },
    getSourceItemNode(edge = this.node) {
      const _this = this;
      let sourceNode = _this.getSourceNode(edge);
      if (sourceNode != null) {
        let nodeData = sourceNode.getData();
        if (nodeData && nodeData.type == 'item') {
          return sourceNode;
        } else if (nodeData && nodeData.type == 'gateway') {
          let incomingEdges = _this.designer.graph.getIncomingEdges(sourceNode);
          for (let i = 0; i < incomingEdges.length; i++) {
            sourceNode = _this.getSourceItemNode(incomingEdges[i]);
            if (sourceNode != null) {
              return sourceNode;
            }
          }
        } else {
          return sourceNode.getParent();
        }
      }
      return null;
    },
    getSourceNode(edge = this.node) {
      let source = edge.getSource();
      if (source && source.cell) {
        return this.designer.graph.getCellById(source.cell);
      }
      return null;
    },
    getTargetNode(edge = this.node) {
      let target = edge.getTarget();
      if (target && target.cell) {
        return this.designer.graph.getCellById(target.cell);
      }
      return null;
    },
    getItemNodeDefineEvents(itemNode) {
      const _this = this;
      let nodeData = itemNode.getData() || {};
      let itemDefinition = _this.designer.getItemDefinitionByItemData(nodeData) || {};
      return (itemDefinition.defineEvents || []).map(item => ({ label: item.name, value: item.id }));
    },
    getItemNodeWorkflowIntegration(itemNode) {
      const _this = this;
      let nodeData = itemNode.getData() || {};
      let itemDefinition = _this.designer.getItemDefinitionByItemData(nodeData) || {};
      let businessIntegrationConfigs = itemDefinition.businessIntegrationConfigs || [];
      for (let index = 0; index < businessIntegrationConfigs.length; index++) {
        let config = businessIntegrationConfigs[index];
        if (config.enabled && config.type == '1' && config.flowDefId) {
          return config;
        }
      }
      return null;
    },
    onFormDataTypeChange(e) {
      if (
        this.configuration.formDataType == '2' ||
        this.configuration.formDataType == '20' ||
        this.configuration.returnWithOver ||
        this.configuration.returnWithEvent
      ) {
        this.handleBotRuleSearch();
      }
    },
    handleBotRuleSearch(value = '') {
      let _this = this;
      _this
        .getCacheData('botRuleOptions', (resolve, reject) => {
          _this.$axios
            .post('/common/select2/query', {
              serviceName: 'botRuleConfFacadeService',
              queryMethod: 'loadSelectData',
              searchValue: value,
              pageSize: 1000,
              pageNo: 1
            })
            .then(({ data }) => {
              if (data.results) {
                resolve(data.results);
              }
            })
            .catch(err => reject(err));
        })
        .then(botRuleOptions => {
          _this.botRuleOptions = botRuleOptions;
        });
    },
    loadTargetFlowOptionData(flowDefId) {
      this.$axios.get(`/proxy/api/workflow/business/definition/getSelectDataByFlowDefId/${flowDefId}`).then(({ data: result }) => {
        if (result.data) {
          this.targetFlowOptionData = result.data;
        }
      });
    }
  }
};
</script>

<style></style>
