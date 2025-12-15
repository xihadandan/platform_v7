<template>
  <a-form-model
    class="basic-info"
    :model="nodeDefinition"
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
        <a-form-model-item label="名称" prop="name">
          <a-input v-model="nodeDefinition.name" />
        </a-form-model-item>
        <a-form-model-item label="ID" prop="id">
          <a-input v-model="nodeDefinition.id" readOnly />
        </a-form-model-item>
        <a-form-model-item label="编码" prop="code">
          <a-input v-model="nodeDefinition.code" />
        </a-form-model-item>
        <a-form-model-item label="时限" prop="timeLimit">
          <a-row :gutter="12">
            <a-col span="12">
              <a-input-number style="width: 100%" v-model="nodeDefinition.timeLimit" />
            </a-col>
            <a-col span="12">
              <a-select v-model="nodeDefinition.timeLimitType">
                <a-select-option :value="1">工作日</a-select-option>
                <a-select-option :value="2">自然日</a-select-option>
              </a-select>
            </a-col>
          </a-row>
        </a-form-model-item>
        <a-form-model-item label="里程碑" prop="milestone">
          <a-switch v-model="nodeDefinition.milestone" checked-children="是" un-checked-children="否" />
        </a-form-model-item>
        <a-form-model-item label="状态条件">
          <NodeStateConditionConfiguration :nodeDefinition="nodeDefinition"></NodeStateConditionConfiguration>
        </a-form-model-item>
        <a-form-model-item label="事件监听" prop="listener">
          <a-select v-model="listeners" mode="multiple" show-search style="width: 100%" :filter-option="filterSelectOption">
            <a-select-option v-for="d in listenerOptions" :key="d.id">
              {{ d.text }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="业务标签" prop="tagId">
          <a-select v-model="nodeDefinition.tagId" show-search style="width: 100%" :filter-option="filterSelectOption">
            <a-select-option v-for="d in bizTagOptions" :key="d.id">
              {{ d.text }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="备注" prop="remark">
          <a-textarea v-model="nodeDefinition.remark" />
        </a-form-model-item>
      </a-collapse-panel>
      <a-collapse-panel v-if="false" key="formInfo" header="阶段办理单">
        <div v-if="isRefNode" style="width: 100%; height: 100%; display: block; position: absolute; z-index: 10"></div>
        <NodeFormSettings :processDefinition="designer.processDefinition" :formConfig="nodeDefinition.formConfig"></NodeFormSettings>
      </a-collapse-panel>
    </a-collapse>
  </a-form-model>
</template>

<script>
import NodeFormSettings from './node-form-settings.vue';
import NodeStateConditionConfiguration from './node-state-condition-configuration.vue';
import { Node } from '@antv/x6';
export default {
  props: {
    designer: Object,
    node: Node
  },
  name: 'NodeConfiguration',
  components: { NodeFormSettings, NodeStateConditionConfiguration },
  inject: ['filterSelectOption', 'getCacheData'],
  data() {
    const _this = this;
    let node = _this.node;
    let nodeData = node.getData();
    _this.node = node;
    if (nodeData.configuration.formConfig == null) {
      _this.$set(nodeData.configuration, 'formConfig', { configType: '2' });
    }
    return {
      nodeDefinition: nodeData.configuration,
      rules: {},
      listeners: nodeData.configuration.listener ? nodeData.configuration.listener.split(';') : [],
      listenerOptions: [],
      bizTagOptions: [],
      nodeRefInfo: _this.designer.getNodeRefInfo(node)
    };
  },
  watch: {
    listeners: function (val) {
      this.nodeDefinition.listener = val.join(';');
    }
  },
  computed: {
    isRefNode() {
      return this.nodeRefInfo.ref;
    }
  },
  created() {
    this.handleListenerSearch();
    this.handleBizTagSearch();
  },
  methods: {
    handleListenerSearch(value = '') {
      let _this = this;
      _this
        .getCacheData('nodeListenerOptions', (resolve, reject) => {
          this.$axios
            .post('/common/select2/query', {
              serviceName: 'bizProcessDefinitionFacadeService',
              queryMethod: 'listBizProcessNodeListenerSelectData',
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
    },
    handleBizTagSearch(value = '') {
      let _this = this;
      _this
        .getCacheData('bizTagOptions', (resolve, reject) => {
          this.$axios
            .post('/common/select2/query', {
              serviceName: 'bizTagFacadeService',
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
          _this.bizTagOptions = options;
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
