<template>
  <a-tabs default-active-key="state-manager">
    <a-tab-pane key="state-definition" tab="状态定义" forceRender>
      <StateTree ref="stateTree" @mounted="treeData => (stateTreeLoaded = true)"></StateTree>
    </a-tab-pane>
    <a-tab-pane key="state-manager" tab="状态回写">
      <a-form-model
        class="basic-info"
        :model="stateDefinition"
        labelAlign="left"
        ref="basicForm"
        :rules="rules"
        :label-col="{ span: 7 }"
        :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
        :colon="false"
      >
        <a-form-model-item label="状态类型名称" prop="stateTypeName">
          <a-input v-model="stateDefinition.stateTypeName" />
        </a-form-model-item>
        <a-form-model-item label="状态名称写入字段">
          <a-select
            v-model="stateDefinition.stateNameField"
            show-search
            style="width: 100%"
            :options="formFieldOptions"
            :filter-option="filterSelectOption"
          ></a-select>
        </a-form-model-item>
        <a-form-model-item label="状态代码写入字段">
          <a-select
            v-model="stateDefinition.stateCodeField"
            show-search
            style="width: 100%"
            :options="formFieldOptions"
            :filter-option="filterSelectOption"
          ></a-select>
        </a-form-model-item>
        <a-form-model-item label="状态列表" class="btn_has_space">
          <a-button @click="addStateConfig()">新增</a-button>
          <a-button @click="deleteStateConfig()">删除</a-button>
        </a-form-model-item>
        <a-table
          class="pt-table"
          rowKey="id"
          :columns="stateConfigColumns"
          :data-source="stateDefinition.stateConfigs"
          :row-selection="{ selectedRowKeys: stateConfigSelectedRowKeys, onChange: onStateConfigSelectChange }"
        >
          <template slot="stateNameSlot" slot-scope="text, record">
            <a-input v-model="record.stateNameValue" readOnly @click="onStateNameValueClick(record)" />
          </template>
          <template slot="stateCodeSlot" slot-scope="text, record">
            <a-input v-model="record.stateCodeValue" readOnly @click="onStateCodeValueClick(record)" />
          </template>
          <template slot="triggerTypeSlot" slot-scope="text, record">
            <a-select
              v-model="record.triggerType"
              show-search
              style="width: 100%"
              :filter-option="filterSelectOption"
              :options="triggerTypeOptions"
            ></a-select>
          </template>
          <template slot="triggerOptionSlot" slot-scope="text, record">
            <template v-if="record.triggerType == 'PROCESS_STATE_CHANGED'">
              <a-select
                v-model="record.processState"
                show-search
                style="width: 100%"
                :filter-option="filterSelectOption"
                :options="processStateOptions"
              ></a-select>
            </template>
            <template v-if="record.triggerType == 'PROCESS_NODE_STATE_CHANGED'">
              <a-row>
                <a-col span="14">
                  <a-select
                    v-model="record.processNodeCode"
                    show-search
                    style="width: 100%"
                    :filter-option="filterSelectOption"
                    :options="processNodeOptions"
                    @change="onProcessNodeChange(record)"
                  ></a-select>
                </a-col>
                <a-col span="10">
                  <a-select
                    v-model="record.processNodeState"
                    show-search
                    style="width: 100%"
                    :filter-option="filterSelectOption"
                    :options="processNodeStateOptions"
                  ></a-select>
                </a-col>
              </a-row>
            </template>
            <template v-if="record.triggerType == 'ITEM_STATE_CHANGED'">
              <a-row>
                <a-col span="14">
                  <a-select
                    v-model="record.itemCode"
                    show-search
                    style="width: 100%"
                    :filter-option="filterSelectOption"
                    :options="itemCodeOptions"
                    @change="onItemCodeChange(record)"
                  ></a-select>
                </a-col>
                <a-col span="10">
                  <a-select
                    v-model="record.itemState"
                    show-search
                    style="width: 100%"
                    :filter-option="filterSelectOption"
                    :options="itemStateOptions"
                  ></a-select>
                </a-col>
              </a-row>
            </template>
            <template v-if="record.triggerType == 'ITEM_EVENT_PUBLISHED'">
              <a-row>
                <a-col span="14">
                  <a-select
                    v-model="record.itemCode"
                    show-search
                    style="width: 100%"
                    :filter-option="filterSelectOption"
                    :options="itemCodeOptions"
                    @change="onItemCodeChange(record)"
                  ></a-select>
                </a-col>
                <a-col span="10">
                  <a-select v-model="record.itemEventId" show-search style="width: 100%" :filter-option="filterSelectOption">
                    <a-select-option v-for="d in getItemEventOptions(record.itemCode)" :key="d.value" :title="d.label">
                      {{ d.label }}
                    </a-select-option>
                  </a-select>
                </a-col>
              </a-row>
            </template>
            <template v-if="record.triggerType == 'PROCESS_ENTITY_TIMER_EVENT_PUBLISHED'">
              <a-select
                v-model="record.timerEventId"
                show-search
                style="width: 100%"
                :filter-option="filterSelectOption"
                :options="entityTimerEventOptions"
              ></a-select>
            </template>
          </template>
        </a-table>
      </a-form-model>
      <StateExpressionModal
        v-if="stateTreeLoaded"
        title="状态名称配置"
        :visible="stateNameValueModalVisible"
        :state-tree-data="stateTreeData"
        :data="modalData"
        @ok="handleStateNameValueOk"
        @cancel="handleStateNameValueCancel"
      ></StateExpressionModal>
      <StateExpressionModal
        v-if="stateTreeLoaded"
        title="状态代码配置"
        :visible="stateCodeValueModalVisible"
        :state-tree-data="stateTreeData"
        :data="modalData"
        @ok="handleStateCodeValueOk"
        @cancel="handleStateCodeValueCancel"
      ></StateExpressionModal>
    </a-tab-pane>
  </a-tabs>
</template>

<script>
import { generateId, deepClone } from '@framework/vue/utils/util';
import { isEmpty } from 'lodash';
import StateTree from './state-tree.vue';
import StateExpressionModal from './state-expression-modal.vue';
export default {
  props: {
    stateDefinition: {
      type: Object,
      default() {
        return { id: generateId('SF'), stateTypeName: '', stateNameField: '', stateCodeField: '', stateConfigs: [] };
      }
    },
    formUuid: String
  },
  components: { StateTree, StateExpressionModal },
  inject: ['designer', 'getCacheData', 'filterSelectOption'],
  data() {
    return {
      stateTreeLoaded: false,
      rules: {
        stateTypeName: [{ required: true, message: '不能为空！', trigger: 'blur' }]
      },
      formFieldOptions: [],
      stateConfigColumns: [
        { title: '状态名称', dataIndex: 'stateNameValue', scopedSlots: { customRender: 'stateNameSlot' } },
        { title: '状态代码', dataIndex: 'stateCodeValue', scopedSlots: { customRender: 'stateCodeSlot' } },
        { title: '触发类型', dataIndex: 'triggerType', width: '190px', scopedSlots: { customRender: 'triggerTypeSlot' } },
        { title: '触发选项', dataIndex: 'taskId', width: '260px', scopedSlots: { customRender: 'triggerOptionSlot' } }
      ],
      stateConfigSelectedRowKeys: [],
      triggerTypeOptions: [
        { label: '业务流程状态变更', value: 'PROCESS_STATE_CHANGED' },
        { label: '阶段状态变更', value: 'PROCESS_NODE_STATE_CHANGED' },
        { label: '业务事项状态变更', value: 'ITEM_STATE_CHANGED' },
        { label: '业务事项事件发生', value: 'ITEM_EVENT_PUBLISHED' },
        { label: '业务主体状态计时事件发生', value: 'PROCESS_ENTITY_TIMER_EVENT_PUBLISHED' }
      ],
      modalData: {
        type: '1',
        displayValue: '',
        value: ''
      },
      stateNameValueModalVisible: false,
      stateCodeValueModalVisible: false,
      processStateOptions: [
        { value: '00', label: '创建' },
        { value: '10', label: '办理中' },
        { value: '30', label: '完成' },
        { value: '40', label: '取消' }
      ],
      processNodeOptions: [],
      processNodeStateOptions: [
        { value: '00', label: '创建' },
        { value: '10', label: '办理中' },
        { value: '30', label: '完成' },
        { value: '40', label: '取消' }
      ],
      itemCodeOptions: [],
      itemStateOptions: [
        { value: '00', label: '创建' },
        { value: '10', label: '办理中' },
        { value: '20', label: '挂起' },
        { value: '30', label: '完成' },
        { value: '40', label: '取消' }
      ],
      itemEventOptions: [],
      entityTimerEventOptions: [
        { value: 'due', label: '计时到期' },
        { value: 'overdue', label: '计时逾期' }
      ]
    };
  },
  computed: {
    stateTreeData() {
      return this.$refs.stateTree.treeData;
    }
  },
  created() {
    if (this.formUuid) {
      this.loadFormFieldOptions(this.formUuid);
    }
    this.initStateConfigOptions();
  },
  methods: {
    initStateConfigOptions() {
      const _this = this;
      _this.designer.updateTree();
      let nodeDataList = _this.designer.processTree.getTreeDataList('node');
      let processNodeOptions = [];
      let processNodeMap = new Map();
      nodeDataList.forEach(node => {
        if (node.data && node.data.configuration) {
          let configuration = node.data.configuration;
          processNodeOptions.push({ value: configuration.code, label: configuration.name });
          processNodeMap.set(configuration.code, configuration.name);
        }
      });
      _this.processNodeOptions = processNodeOptions;
      _this.processNodeMap = processNodeMap;

      let itemDataList = _this.designer.processTree.getTreeDataList('item');
      let itemCodeOptions = [];
      let itemCodeMap = new Map();
      let itemEventMap = new Map();
      itemDataList.forEach(item => {
        if (item.data && item.data.configuration) {
          let configuration = item.data.configuration;
          itemCodeOptions.push({ value: configuration.itemCode, label: configuration.itemName });
          itemCodeMap.set(configuration.itemCode, configuration.itemName);
          itemEventMap.set(configuration.itemCode, configuration.defineEvents);
        }
      });
      _this.itemCodeOptions = itemCodeOptions;
      _this.itemCodeMap = itemCodeMap;
      _this.itemEventMap = itemEventMap;
    },
    loadFormFieldOptions(formUuid) {
      const _this = this;
      _this
        .getCacheData(`formDefinition_${formUuid}`, (resolve, reject) => {
          if (!formUuid) {
            resolve({});
            return;
          }
          _this.$axios.get(`/proxy/api/biz/process/definition/getFormDefinitionByFormUuid/${formUuid}`).then(({ data }) => {
            if (!data.data) {
              console.error('form definition is null', formUuid);
              resolve({});
            } else {
              let formDefinition = JSON.parse(data.data);
              resolve(formDefinition);
            }
          });
        })
        .then(formDefinition => {
          let fieldSelectData = [{ value: 'uuid', label: 'UUID' }];
          let fields = formDefinition.fields || {};
          // 字段
          for (let fieldName in fields) {
            let field = fields[fieldName];
            fieldSelectData.push({ value: field.name, label: field.displayName });
          }
          _this.formFieldOptions = fieldSelectData;
        });
    },
    addStateConfig() {
      const _this = this;
      _this.stateDefinition.stateConfigs.push({
        id: generateId('SF'),
        stateNameScriptType: 'constant',
        stateCodeScriptType: 'constant'
      });
    },
    deleteStateConfig() {
      const _this = this;
      if (_this.stateConfigSelectedRowKeys.length == 0) {
        _this.$message.error('请选择记录！');
        return;
      }
      _this.stateConfigSelectedRowKeys.forEach(rowKey => {
        let index = _this.stateDefinition.stateConfigs.findIndex(item => item.id == rowKey);
        if (index != -1) {
          _this.stateDefinition.stateConfigs.splice(index, 1);
        }
      });
      _this.stateConfigSelectedRowKeys = [];
    },
    onStateConfigSelectChange(selectedRowKeys) {
      this.stateConfigSelectedRowKeys = selectedRowKeys;
    },
    onStateNameValueClick(record) {
      this.currentRecord = record;
      this.modalData.type = record.stateNameScriptType;
      this.modalData.value = record.stateNameValue;
      this.modalData.stateName = record.stateNameValue;
      this.modalData.stateCode = record.stateCodeValue;
      this.stateNameValueModalVisible = true;
    },
    handleStateNameValueOk(data) {
      this.currentRecord.stateNameScriptType = data.type;
      if (data.type == 'constant') {
        this.currentRecord.stateNameValue = data.displayValue;
        this.currentRecord.stateCodeValue = data.value;
      } else {
        this.currentRecord.stateNameValue = data.value;
      }
      this.stateNameValueModalVisible = false;
    },
    handleStateNameValueCancel() {
      this.currentRecord = null;
      this.stateNameValueModalVisible = false;
    },
    onStateCodeValueClick(record) {
      this.currentRecord = record;
      this.modalData.type = record.stateCodeScriptType;
      this.modalData.value = record.stateCodeValue;
      this.modalData.stateName = record.stateNameValue;
      this.modalData.stateCode = record.stateCodeValue;
      this.stateCodeValueModalVisible = true;
    },
    handleStateCodeValueOk(data) {
      this.currentRecord.stateCodeScriptType = data.type;
      this.currentRecord.stateCodeValue = data.value;
      if (data.type == 'constant') {
        this.currentRecord.stateNameValue = data.displayValue;
      }
      this.stateCodeValueModalVisible = false;
    },
    handleStateCodeValueCancel() {
      this.currentRecord = null;
      this.stateCodeValueModalVisible = false;
    },
    onProcessNodeChange(record) {
      record.processNodeName = this.processNodeMap.get(record.processNodeCode);
    },
    onItemCodeChange(record) {
      record.itemName = this.itemCodeMap.get(record.itemCode);
    },
    getItemEventOptions(itemCode) {
      if (isEmpty(itemCode)) {
        return [];
      }
      let defineEvents = this.itemEventMap.get(itemCode) || [];
      return defineEvents.map(item => ({ label: item.name, value: item.id }));
    },
    collect() {
      return this.$refs.basicForm
        .validate()
        .then(valid => {
          if (valid) {
            return this.stateDefinition;
          }
          return null;
        })
        .catch(valid => {
          console.log('valid ', valid);
        });
    }
  }
};
</script>

<style lang="less" scoped>
.tree-more-operations {
  li .ant-tree-node-content-wrapper {
    display: inline;
    line-height: 24px;
  }
}
</style>
