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
        <a-form-model-item label="状态列表">
          <a-button @click="addStateConfig()">新增</a-button>
          <a-button @click="deleteStateConfig()">删除</a-button>
        </a-form-model-item>
        <a-table
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
          <template slot="actionTypeSlot" slot-scope="text, record">
            <a-input
              v-show="record.triggerType && record.triggerType != 'FLOW_STARTED' && record.triggerType != 'FLOW_END'"
              :value="getTriggerCondition(record)"
              readOnly
              @click="onTriggerConditionClick(record)"
            />
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
      <a-modal
        title="触发条件"
        width="650px"
        :visible="stateConditionModalVisible"
        :destroyOnClose="true"
        @ok="handleTriggerConditionOk"
        @cancel="handleTriggerConditionCancel"
        :footer="null"
      >
        <a-form-model v-if="currentRecord" :label-col="{ span: 5 }" :wrapper-col="{ span: 19 }">
          <a-form-model-item
            v-if="
              currentRecord.triggerType == 'TASK_CREATED' ||
              currentRecord.triggerType == 'TASK_COMPLETED' ||
              currentRecord.triggerType == 'TASK_BELONG'
            "
            label="触发环节"
            prop="taskIds"
          >
            <a-row>
              <a-col span="19">
                <a-select
                  v-model="currentRecord.taskIds"
                  mode="multiple"
                  show-search
                  style="width: 100%"
                  :filter-option="filterSelectOption"
                >
                  <a-select-option v-for="d in flowOptionData.taskIds" :key="d.id">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
              </a-col>
            </a-row>
          </a-form-model-item>
          <a-form-model-item v-if="currentRecord.triggerType == 'TASK_OPERATION'" label="环节操作" prop="taskIds">
            <a-row>
              <a-col span="11">
                <a-select
                  v-model="currentRecord.taskIds"
                  mode="multiple"
                  show-search
                  style="width: 100%"
                  :filter-option="filterSelectOption"
                >
                  <a-select-option v-for="d in flowOptionData.taskIds" :key="d.id">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
              </a-col>
              <a-col span="8">
                <a-select
                  v-model="currentRecord.actionType"
                  show-search
                  style="width: 100%"
                  :options="actionTypeOptions"
                  :filter-option="filterSelectOption"
                ></a-select>
              </a-col>
            </a-row>
          </a-form-model-item>
          <a-form-model-item v-if="currentRecord.triggerType == 'DIRECTION_TRANSITION'" label="触发流向" prop="directionIds">
            <a-row>
              <a-col span="19">
                <a-select
                  v-model="currentRecord.directionIds"
                  mode="multiple"
                  show-search
                  style="width: 100%"
                  :filter-option="filterSelectOption"
                >
                  <a-select-option v-for="d in flowOptionData.directions" :key="d.id">{{ d.text }} ({{ d.id }})</a-select-option>
                </a-select>
              </a-col>
            </a-row>
          </a-form-model-item>
          <a-form-model-item label="附加条件表达式">
            <a-switch v-model="currentRecord.additionalCondition" checked-children="是" un-checked-children="否" />
          </a-form-model-item>
          <template v-if="currentRecord.additionalCondition">
            <a-form-model-item label="脚本类型">
              <a-radio-group v-model="currentRecord.expressionScriptType" default-value="groovy">
                <a-radio value="groovy">groovy脚本</a-radio>
              </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="表达式">
              <a-row>
                <a-col span="19">
                  <a-textarea v-model="currentRecord.conditionExpression"></a-textarea>
                  <div class="remark" v-html="groovyScriptRemark"></div>
                </a-col>
              </a-row>
            </a-form-model-item>
          </template>
        </a-form-model>
      </a-modal>
    </a-tab-pane>
  </a-tabs>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import { groovyScriptRemark } from '../../designer/remarks.js';
import StateTree from '../state-configuration/state-tree.vue';
import StateExpressionModal from '../state-configuration/state-expression-modal.vue';
export default {
  props: {
    stateDefinition: {
      type: Object,
      default() {
        return { id: generateId('SF'), stateTypeName: '', stateNameField: '', stateCodeField: '', stateConfigs: [] };
      }
    }
  },
  components: { StateTree, StateExpressionModal },
  inject: ['designer', 'flowOptionData', 'getCacheData', 'filterSelectOption'],
  data() {
    let formFields = this.flowOptionData.formFields || [];
    let formFieldOptions = formFields.map(field => ({ label: field.text, value: field.id }));
    return {
      groovyScriptRemark,
      stateTreeLoaded: false,
      rules: {
        stateTypeName: [{ required: true, message: '不能为空！', trigger: 'blur' }]
      },
      formFieldOptions,
      stateConfigColumns: [
        { title: '状态名称', dataIndex: 'stateNameValue', scopedSlots: { customRender: 'stateNameSlot' } },
        { title: '状态代码', dataIndex: 'stateCodeValue', scopedSlots: { customRender: 'stateCodeSlot' } },
        { title: '触发类型', dataIndex: 'triggerType', width: '160px', scopedSlots: { customRender: 'triggerTypeSlot' } },
        { title: '触发条件', dataIndex: 'actionType', width: '260px', scopedSlots: { customRender: 'actionTypeSlot' } }
      ],
      stateConfigSelectedRowKeys: [],
      triggerTypeOptions: [
        { label: '流程开始', value: 'FLOW_STARTED' },
        { label: '流程办结', value: 'FLOW_END' },
        { label: '环节创建', value: 'TASK_CREATED' },
        { label: '环节完成', value: 'TASK_COMPLETED' },
        { label: '环节操作', value: 'TASK_OPERATION' },
        { label: '环节归属', value: 'TASK_BELONG' },
        { label: '流向流转', value: 'DIRECTION_TRANSITION' }
      ],
      actionTypeOptions: [
        { label: '提交', value: 'Submit' },
        { label: '退回', value: 'Rollback' },
        { label: '撤回', value: 'Cancel' }
      ],
      modalData: {
        type: '1',
        displayValue: '',
        value: ''
      },
      stateNameValueModalVisible: false,
      stateCodeValueModalVisible: false,
      stateConditionModalVisible: false,
      currentRecord: undefined
    };
  },
  computed: {
    stateTreeData() {
      return this.$refs.stateTree.treeData;
    }
  },
  created() {},
  methods: {
    addStateConfig() {
      const _this = this;
      _this.stateDefinition.stateConfigs.push({
        id: generateId('SF'),
        stateNameScriptType: 'constant',
        stateCodeScriptType: 'constant',
        expressionScriptType: 'groovy'
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
      this.currentRecord = null;
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
      this.currentRecord = null;
      this.stateCodeValueModalVisible = false;
    },
    handleStateCodeValueCancel() {
      this.currentRecord = null;
      this.stateCodeValueModalVisible = false;
    },
    getTriggerCondition(record) {
      const _this = this;
      let triggerCondition = '';
      let triggerType = record.triggerType;
      let flowOptionData = _this.flowOptionData;
      let actionTypeOptions = _this.actionTypeOptions;
      let taskIds = record.taskIds || [];
      let directionIds = record.directionIds || [];
      let actionType = record.actionType;
      if (triggerType == 'TASK_CREATED' || triggerType == 'TASK_COMPLETED' || triggerType == 'TASK_BELONG') {
        triggerCondition = flowOptionData.taskIds
          .filter(item => taskIds.indexOf(item.id) != -1)
          .map(item => item.text)
          .join(';');
      } else if (triggerType == 'TASK_OPERATION') {
        triggerCondition =
          flowOptionData.taskIds
            .filter(item => taskIds.indexOf(item.id) != -1)
            .map(item => item.text)
            .join(';') +
          ' ' +
          actionTypeOptions
            .filter(item => item.value == actionType)
            .map(item => item.label)
            .join(';');
      } else if (triggerType == 'DIRECTION_TRANSITION') {
        triggerCondition = flowOptionData.directions
          .filter(item => directionIds.indexOf(item.id) != -1)
          .map(item => item.text)
          .join(';');
      }
      return triggerCondition;
    },
    onTriggerConditionClick(record) {
      this.currentRecord = record;
      this.stateConditionModalVisible = true;
    },
    handleTriggerConditionOk() {
      this.currentRecord = null;
      this.stateConditionModalVisible = false;
    },
    handleTriggerConditionCancel() {
      this.currentRecord = null;
      this.stateConditionModalVisible = false;
    },
    onProcessNodeChange(record) {
      record.processNodeName = this.processNodeMap.get(record.processNodeId);
    },
    onItemCodeChange(record) {
      record.itemName = this.itemCodeMap.get(record.itemCode);
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
.remark {
  line-height: 1.5;
  font-size: 14px;
}
</style>
