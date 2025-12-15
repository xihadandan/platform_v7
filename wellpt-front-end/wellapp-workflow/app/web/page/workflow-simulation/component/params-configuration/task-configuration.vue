<template>
  <a-form-model ref="form" :model="task" labelAlign="left" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }" :colon="false">
    <a-form-model-item label="名称">
      {{ task.name }}
    </a-form-model-item>
    <a-form-model-item label="ID">
      {{ task.id }}
    </a-form-model-item>
    <template v-if="isUserTask">
      <a-form-model-item label="办理人">
        <span v-if="!task.simulationTaskUserId">
          {{ getTaskUserLabel() }}
        </span>
        <OrgSelect
          v-show="false"
          ref="simulationTaskUser"
          v-model="task.simulationTaskUserId"
          :checkableTypes="['user']"
          @change="onSimulationTaskUserChange"
        />
        <a-badge>
          <a-icon
            v-if="task.simulationTaskUserId"
            slot="count"
            type="close-circle"
            style="color: #f5222d"
            theme="filled"
            @click.stop="task.simulationTaskUserId = undefined"
            title="删除仿真办理人"
          />
          <a-button size="small" shape="round" @click="onSimulationTaskUserClick">
            {{ task.simulationTaskUserId ? '仿真办理人: ' + task.simulationTaskUserName : '设置仿真办理人' }}
          </a-button>
        </a-badge>
      </a-form-model-item>
      <a-form-model-item v-if="isCollaborationTask" label="决策人">
        <span v-if="!task.simulationDecisionMakerId">
          {{ getTaskDecisionMakerLabel() }}
        </span>
        <OrgSelect
          v-show="false"
          ref="simulationDecisionMaker"
          v-model="task.simulationDecisionMakerId"
          :checkableTypes="['user']"
          @change="onSimulationDecisionMakerChange"
        />
        <a-badge>
          <a-icon
            v-if="task.simulationDecisionMakerId"
            slot="count"
            type="close-circle"
            style="color: #f5222d"
            theme="filled"
            @click.stop="task.simulationDecisionMakerId = undefined"
            title="删除仿真决策人"
          />
          <a-button size="small" shape="round" @click="onSimulationDecisionMakerClick">
            {{ task.simulationDecisionMakerId ? '仿真决策人: ' + task.simulationDecisionMakerName : '设置仿真决策人' }}
          </a-button>
        </a-badge>
      </a-form-model-item>
      <div>
        <a-form-model-item label="办理意见"></a-form-model-item>
        <a-table
          ref="opinionTable"
          rowKey="id"
          size="small"
          :pagination="false"
          :bordered="false"
          :columns="opinionColumns"
          :data-source="task.opinions"
          :locale="locale"
          :scroll="{ x: 700 }"
          :class="['task-opinion-table no-border']"
        >
          <template slot="taskUserIdSlot" slot-scope="text, record, index">
            <OrgSelect v-model="record.taskUserId" :checkableTypes="['user']" @change="opinionTaskUserChange" />
          </template>
          <template slot="opinionTextSlot" slot-scope="text, record, index">
            <a-input v-model="record.opinionText"></a-input>
          </template>
          <template slot="opinionValueSlot" slot-scope="text, record, index">
            <a-radio-group v-model="record.opinionValue" @change="opinionValueChange(record)">
              <a-radio v-for="item in getOpinionValueOptions()" :key="item.value" :value="item.value" :style="{ display: 'block' }">
                {{ item.label }}
              </a-radio>
            </a-radio-group>
          </template>
          <template slot="opinionFilesSlot" slot-scope="text, record, index">
            <div style="overflow: auto">
              <UploadSimpleComponent
                :fileIds="record.opinionFiles"
                @change="fileList => opinionFileListChange(fileList, record, 'opinionFiles')"
              ></UploadSimpleComponent>
            </div>
          </template>
          <template slot="operationSlot" slot-scope="text, record, index">
            <a-button type="link" size="small" title="删除" @click="deleteOpinion(index)">
              <Icon type="pticon iconfont icon-ptkj-shanchu" />
            </a-button>
          </template>
          <template slot="footer">
            <a-button type="link" :style="{ paddingLeft: '7px' }" @click="addOpinion">
              <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
              添加
            </a-button>
          </template>
        </a-table>
      </div>
      <div>
        <a-form-model-item label="表单设值"></a-form-model-item>
        <a-table
          rowKey="id"
          size="small"
          :pagination="false"
          :bordered="false"
          :columns="formFieldColumns"
          :data-source="task.formFields"
          :locale="locale"
          :class="['form-field-table no-border']"
        >
          <template slot="nameSlot" slot-scope="text, record, index">
            <a-select
              v-model="record.name"
              allow-clear
              show-search
              style="width: 100%"
              :filter-option="filterSelectOption"
              @change="value => fieldNameChange(value, record)"
            >
              <a-select-option v-for="d in formFieldOptions" :key="d.name">
                {{ d.displayName }}
              </a-select-option>
            </a-select>
          </template>
          <template slot="valueSlot" slot-scope="text, record, index">
            <a-input-number v-if="record.type == 'input-number'" v-model="record.value" style="width: 100%"></a-input-number>
            <a-date-picker
              v-else-if="record.type == 'date'"
              v-model="record.value"
              show-time
              :valueFormat="record.dateFormat"
              :format="record.dateFormat"
            ></a-date-picker>
            <UploadSimpleComponent
              v-else-if="record.type == 'file'"
              :fileIds="record.value"
              @change="fileList => fileListChange(fileList, record, 'value')"
            ></UploadSimpleComponent>
            <a-input v-else v-model="record.value"></a-input>
          </template>
          <template slot="conditionConfigSlot" slot-scope="text, record, index">
            <template v-if="record.conditionConfig.match && record.conditionConfig.conditions.length">
              <span v-if="record.conditionConfig.match == 'all'">
                满足全部条件时设值
                <div v-html="getConditionConfigLabel(record.conditionConfig)"></div>
              </span>
              <span v-else>
                满足任一条件时设值
                <div v-html="getConditionConfigLabel(record.conditionConfig)"></div>
              </span>
            </template>
            <a-button type="link" size="small" title="配置" @click="onConfigConditionClick(record)">
              <Icon type="pticon iconfont icon-ptkj-shezhi" />
            </a-button>
          </template>
          <template slot="operationSlot" slot-scope="text, record, index">
            <a-button type="link" size="small" title="删除" @click="deleteFormField(index)">
              <Icon type="pticon iconfont icon-ptkj-shanchu" />
            </a-button>
          </template>
          <template slot="footer">
            <a-button type="link" :style="{ paddingLeft: '7px' }" @click="addFormField">
              <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
              添加
            </a-button>
          </template>
        </a-table>
      </div>
    </template>
    <a-modal title="环节设值条件" :visible="conditionVisible" @ok="handleConditionOk" @cancel="handleConditionCancel">
      <TaskFormFieldConditionConfiguration
        v-if="conditionConfig"
        :configuration="conditionConfig"
        :formFieldOptions="formFieldOptions"
      ></TaskFormFieldConditionConfiguration>
    </a-modal>
  </a-form-model>
</template>

<script>
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import UploadSimpleComponent from '@admin/app/web/template/common/upload-simple-component.vue';
import TaskFormFieldConditionConfiguration from './task-form-field-condition-configuration.vue';
import { generateId } from '@framework/vue/utils/util';
import { filterSelectOption } from '@framework/vue/utils/function';
import { operatorOptions } from '@pageAssembly/app/web/widget/commons/constant.js';
import { isEmpty } from 'lodash';
import { deepClone } from '@framework/vue/utils/util';
export default {
  props: {
    task: Object,
    flowDefinition: Object
  },
  components: { OrgSelect, UploadSimpleComponent, TaskFormFieldConditionConfiguration },
  inject: ['simulation'],
  data() {
    let taskConfig = this.flowDefinition.tasks.find(task => task.id == this.task.id);
    let enableOpinionPosition = taskConfig && taskConfig.enableOpinionPosition == '1';
    let opinionColumns = [
      { title: '使用人', dataIndex: 'taskUserId', width: 150, scopedSlots: { customRender: 'taskUserIdSlot' } },
      { title: '办理意见', dataIndex: 'opinionText', width: 120, scopedSlots: { customRender: 'opinionTextSlot' } },
      { title: '意见立场', dataIndex: 'opinionValue', width: 100, scopedSlots: { customRender: 'opinionValueSlot' } },
      { title: '附件', dataIndex: 'opinionFiles', scopedSlots: { customRender: 'opinionFilesSlot' } },
      { title: '操作', dataIndex: 'operation', width: 50, scopedSlots: { customRender: 'operationSlot' }, fixed: 'right' }
    ];
    if (!enableOpinionPosition) {
      opinionColumns = opinionColumns.filter(column => column.dataIndex != 'opinionValue');
    }
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      opinionColumns,
      formFieldColumns: [
        { title: '字段名', dataIndex: 'name', width: '150px', scopedSlots: { customRender: 'nameSlot' } },
        { title: '字段值', dataIndex: 'value', width: '200px', scopedSlots: { customRender: 'valueSlot' } },
        { title: '条件', dataIndex: 'conditionConfig', scopedSlots: { customRender: 'conditionConfigSlot' } },
        { title: '操作', dataIndex: 'operation', width: 50, scopedSlots: { customRender: 'operationSlot' } }
      ],
      formFieldOptions: [],
      conditionVisible: false,
      conditionConfig: null
    };
  },
  computed: {
    isUserTask() {
      return this.task.type == '1' || this.task.type == '3';
    },
    isCollaborationTask() {
      return this.task.type == '3';
    }
  },
  created() {
    this.loadFormFieldOptions();
  },
  mounted() {
    this.$nextTick(() => {
      this.$refs.opinionTable.$forceUpdate();
    });
    setTimeout(() => {
      this.$refs.opinionTable.$forceUpdate();
    }, 500);
  },
  methods: {
    filterSelectOption,
    onSimulationTaskUserClick() {
      this.$refs.simulationTaskUser.openModal();
    },
    onSimulationTaskUserChange({ value, label, nodes }) {
      this.task.simulationTaskUserName = label;
    },
    getTaskUserLabel() {
      let taskId = this.task.id;
      return this.simulation.getTaskUserLabel(taskId);
    },
    onSimulationDecisionMakerClick() {
      this.$refs.simulationDecisionMaker.openModal();
    },
    onSimulationDecisionMakerChange({ value, label, nodes }) {
      this.task.simulationDecisionMakerName = label;
    },
    getTaskDecisionMakerLabel() {
      let taskId = this.task.id;
      return this.simulation.getTaskDecisionMakerLabel(taskId);
    },
    addOpinion() {
      this.task.opinions.push({ id: generateId(), taskId: this.task.id, opinionFiles: [] });
    },
    deleteOpinion(index) {
      this.task.opinions.splice(index, 1);
    },
    getOpinionValueOptions(taskId = this.task.id) {
      if (isEmpty(taskId)) {
        return [];
      }

      let taskConfig = this.flowDefinition.tasks.find(task => task.id == taskId);
      if (!taskConfig || !taskConfig.optNames) {
        return [];
      }

      return taskConfig.optNames.map(item => ({ label: item.argValue, value: item.value }));
    },
    opinionTaskUserChange() {
      this.$refs.opinionTable.$forceUpdate();
    },
    opinionValueChange(record) {
      let opinion = this.getOpinionValueOptions(this.task.id).find(item => item.value == record.opinionValue);
      if (opinion) {
        record.opinionLabel = opinion.label;
      }
    },
    opinionFileListChange(fileList, record, prop) {
      this.fileListChange(fileList, record, prop);
      this.$refs.opinionTable.$forceUpdate();
    },
    fileListChange(fileList, record, prop) {
      record[prop] = fileList.map(file => {
        let dbFile = file.dbFile;
        return {
          uuid: dbFile.fileID,
          name: file.name,
          fileName: file.name,
          fileID: dbFile.fileID,
          status: file.status,
          fileSize: dbFile.fileSize,
          creator: dbFile.creator
        };
      });
    },
    addFormField() {
      this.task.formFields.push({
        id: generateId(),
        type: '',
        name: undefined,
        value: undefined,
        conditionConfig: { match: 'all', conditions: [] }
      });
    },
    deleteFormField(index) {
      this.task.formFields.splice(index, 1);
    },
    loadFormFieldOptions() {
      const _this = this;
      if (_this.flowDefinition.property.formFieldOptions) {
        _this.formFieldOptions = _this.flowDefinition.property.formFieldOptions;
        return;
      }
      let formUuid = _this.flowDefinition.property.formID;
      $axios.post(`/proxy/api/dyform/definition/getFormDefinitionByUuid?formUuid=${formUuid}`).then(({ data: result }) => {
        if (result.definitionJson) {
          let formDefinition = JSON.parse(result.definitionJson);
          let fields = [];
          for (let key in formDefinition.fields) {
            let field = formDefinition.fields[key];
            fields.push(field);
          }
          _this.flowDefinition.property.formFieldOptions = fields;
          _this.formFieldOptions = fields;
        }
      });
    },
    fieldNameChange(value, record) {
      let field = this.formFieldOptions.find(field => field.name == value);
      if (field && field.configuration) {
        if (field.contentFormat) {
          record.type = 'date';
          record.dateFormat = 'YYYY-MM-DD HH:mm:ss';
        } else if (field.inputMode == '6') {
          record.type = 'file';
        } else {
          record.type = field.configuration.type;
        }
      } else if (field && field.inputMode == '30' && field.contentFormat) {
        record.type = 'date';
        record.dateFormat = 'YYYY-MM-DD HH:mm:ss';
      }
      record.value = null;
    },
    getConditionConfigLabel(config) {
      const _this = this;
      let conditions = config.conditions;
      let conditionLabels = [];
      conditions.forEach((condition, index) => {
        let label = index + 1 + '、';
        if (condition.code == 'other.currentRunNum') {
          label += '当前执行次数';
        } else {
          let field = _this.formFieldOptions.find(item => item.name == condition.code);
          label += (field && field.displayName) || '';
        }
        let operator = operatorOptions.find(item => item.value == condition.operator);
        if (operator) {
          label += ' ' + operator.label + ' ';
        }
        if (!['true', 'false'].includes(operator.value)) {
          label += condition.value || '';
        }
        conditionLabels.push(label);
      });
      return conditionLabels.join('</br>');
    },
    onConfigConditionClick(record) {
      this.conditionVisible = true;
      this.conditionConfig = deepClone(record.conditionConfig);
      this.formFieldRecord = record;
    },
    handleConditionOk() {
      this.formFieldRecord.conditionConfig = this.conditionConfig;
      this.conditionConfig = null;
      this.conditionVisible = false;
    },
    handleConditionCancel() {
      this.conditionConfig = null;
      this.conditionVisible = false;
    }
  }
};
</script>

<style lang="less" scoped>
.task-opinion-table,
.form-field-table {
  ::v-deep .ant-table-footer {
    border: none;
    padding: 0px;
    float: right;
    text-align: right;
    width: 100%;
  }
}
</style>
