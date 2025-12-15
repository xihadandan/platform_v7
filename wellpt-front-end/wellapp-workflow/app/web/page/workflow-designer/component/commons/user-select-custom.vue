<template>
  <div class="user-select-custom-component">
    <a-textarea :auto-size="{ minRows: 2, maxRows: 6 }" v-model="argValue" :readOnly="true" @click="openModal" />
    <a-modal
      class="wf-users-select-custom-modal"
      :maskClosable="false"
      :title="title"
      :visible="visible"
      :okText="okText"
      cancelText="取消"
      :width="800"
      :bodyStyle="{ padding: '12px 20px', height: '600px', 'overflow-y': 'auto' }"
      @cancel="onCancelModal"
      @ok="onOkModal"
      ref="modal"
    >
      <a-form-model
        v-if="formData"
        ref="form"
        :model="formData"
        :colon="false"
        labelAlign="left"
        :label-col="{ flex: '100px' }"
        :wrapper-col="{ flex: 'auto' }"
      >
        <a-form-model-item label="组合关系">
          <w-select
            :options="optionsList.setOperation"
            v-model="formData.setOperation"
            style="width: 150px"
            @change="setOperationChange"
          ></w-select>
        </a-form-model-item>
        <a-form-model-item label="承办人">
          <w-select :options="optionsList.leftBracket_1" v-model="formData.leftBracket" style="width: 150px"></w-select>
          人员选择
          <w-select :options="optionsList.userType" v-model="formData.userType" style="width: 150px" @change="userTypeChange"></w-select>
          组织版本
          <w-select
            :options="optionsList.orgVersionType"
            v-model="formData.orgVersionType"
            style="width: 150px"
            @change="orgVersionTypeChange"
          ></w-select>
          <w-select
            :options="orgVersionIdOptions"
            v-model="formData.orgVersionId"
            :formData="formData"
            formDataFieldName="orgVersionName"
            style="width: 150px"
          ></w-select>
          <w-select :options="optionsList.optionOf" v-model="formData.optionOf" style="width: 150px" @change="optionOfChange"></w-select>
          <w-select :options="optionsList.rightBracket_1" v-model="formData.rightBracket" style="width: 150px"></w-select>
          <!-- 组织机构 -->
          <org-select
            v-if="formData.userType == 'Unit' && orgVersionId"
            :orgVersionId="orgVersionId"
            :orgVersionIds="orgVersionIds"
            v-model="formData.userValue"
            :orgType="['MyOrg', 'MyLeader', 'PublicGroup']"
            @change="changeOrg"
          />
          <!-- 表单字段 -->
          <dyform-fields-tree-select
            v-else-if="formData.userType == 'FormField'"
            v-model="formData.userValue"
            :formData="formData"
            prop="userValue"
            @change="changeFields"
          ></dyform-fields-tree-select>
          <!-- 环节 -->
          <node-task-select
            v-else-if="formData.userType == 'TaskHistory'"
            mode="multiple"
            v-model="formData.userValue"
            @change="changeTask"
          ></node-task-select>
          <!-- 可选项 -->
          <w-tree-select
            v-else-if="formData.userType == 'Option'"
            :treeData="optionsList.optionTreeData"
            v-model="formData.userValue"
            :treeCheckable="true"
            :formData="formData"
            formDataFieldName="userName"
            :replaceFields="{
              children: 'children',
              title: 'name',
              key: 'id',
              value: 'id'
            }"
          />
          <!-- 接口实现 -->
          <w-tree-select
            v-else-if="formData.userType == 'Interface'"
            :treeData="optionsList.interfaceTreeData"
            v-model="formData.userValue"
            :treeCheckable="true"
            :formData="formData"
            formDataFieldName="userName"
            :replaceFields="{
              children: 'children',
              title: 'name',
              key: 'id',
              value: 'id'
            }"
          />
        </a-form-model-item>
        <a-form-model-item label=" " :colon="false">
          <div class="btn_has_space" style="margin-top: 4px">
            <a-button type="primary" @click="add">添加</a-button>
            <a-button type="primary" :disabled="!isUpdate" @click="update">更新</a-button>
            <a-button @click="del">删除</a-button>
          </div>
        </a-form-model-item>
        <a-form-model-item label=" " :colon="false">
          <div class="expressionSelect">
            <div
              v-for="(item, index) in valueNodes"
              :key="'node_' + index"
              :class="[selectId == item.id ? 'active' : '', 'expression-item']"
              @click="expressionSelect(item)"
            >
              {{ item.displayName }}
            </div>
          </div>
        </a-form-model-item>
        <a-form-model-item label="表达式">
          <a-textarea :auto-size="{ minRows: 2, maxRows: 6 }" v-model="setExpression" :readOnly="true" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script type="text/babel">
import { filter, map, each, findIndex } from 'lodash';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import { deepClone, generateId } from '@framework/vue/utils/util';
import constant, { selectDataList } from '../designer/constant';
import DyformFieldsTreeSelect from './dyform-fields-tree-select.vue';
import WSelect from '../components/w-select';
import WTreeSelect from '../components/w-tree-select';
import NodeTaskSelect from './node-task-select';

export default {
  name: 'UserSelectCustom',
  inject: ['designer', 'workFlowData'],
  components: {
    OrgSelect,
    WSelect,
    DyformFieldsTreeSelect,
    NodeTaskSelect,
    WTreeSelect
  },
  props: {
    title: {
      type: String,
      default: function () {
        return '自定义';
      }
    },
    value: {
      type: [String, Array]
    },
    data: {
      type: Array
    },
    orgVersionId: {
      type: String
    },
    orgVersionIds: {
      type: Array
    },
    okText: {
      type: String,
      default: '确定'
    }
  },
  data() {
    let initformData = {
      leftBracket: '',
      userTypeName: '',
      userType: '',
      userName: '',
      userValue: '',
      valuePath: '',
      orgVersionType: '',
      orgVersionName: '',
      orgVersionId: '',
      optionOfName: '',
      optionOf: '',
      setOperationName: '',
      setOperation: '',
      rightBracket: '',
      order: 0
    };
    let expressionConfigs = { expressionConfigs: [] };
    if (this.value) {
      if (Array.isArray(this.value) && this.value.length > 0) {
        expressionConfigs = JSON.parse(this.value[0]);
      } else if (typeof this.value === 'string') {
        expressionConfigs = JSON.parse(this.value);
      }
    }
    let valueNodes = [],
      setExpression = '';
    if (expressionConfigs.expressionConfigs.length) {
      valueNodes = map(expressionConfigs.expressionConfigs, (item, index) => {
        let displayName = this.expDataToDisplayName(item);
        return {
          id: generateId(),
          displayName,
          config: item
        };
      });
    }
    return {
      initformData,
      formData: deepClone(initformData),
      visible: false,
      valueNodes, // 所有值
      setExpression, //表达式
      selectId: ''
    };
  },
  filters: {},
  computed: {
    // 显示值
    argValue() {
      return this.data && this.data.length ? this.data[0].argValue : '';
    },
    isUpdate() {
      return this.selectId;
    },
    optionsList() {
      let optionsList = {};
      each(selectDataList, item => {
        optionsList[item.valueField] = item.data;
      });
      return optionsList;
    },
    orgVersionIdOptions() {
      if (this.formData.orgVersionType == 1) {
        return this.optionsList.orgVersionId_1;
      } else if (this.formData.orgVersionType == 2) {
        return this.optionsList.orgVersionId_2;
      }
      return [];
    }
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    setArgValue() {},
    openModal() {
      let _this = this;
      this.visible = true;
      this.formData = deepClone(this.initformData);
      this.getCurrentUserUnitOrgVersions();
      this.getFormFields();
      this.getTaskUserOptionUsers();
      this.getTaskUserCustomInterfaces();
      this.displaySetExpression();
    },
    clearAllInput() {
      this.emitValueChange();
    },
    clearAll() {},
    emitValueChange() {
      let argValue = map(this.valueNodes, 'displayName').join(' ');
      let expressionConfigs = this.getExpressionConfigs();
      var configValue = JSON.stringify({ expressionConfigs: expressionConfigs });
      if (typeof this.value === 'array') {
        configValue = [configValue];
      }
      this.$emit('input', configValue);
      this.$emit('change', {
        value: configValue,
        label: argValue
      });
    },
    onCancelModal() {
      let _this = this;
      this.visible = false;
      _this.$emit('cancel');
    },
    onOkModal() {
      this.checkUserCustomExpression(configValue => {
        this.visible = false;
        this.emitValueChange();
      });
    },
    // 生成显示值
    expDataToDisplayName(json) {
      var expStr = '';
      if (json.setOperationName) {
        expStr = (json.setOperationName || '') + ' ';
      }
      expStr += json.leftBracket || '';
      if (json.orgVersionName) {
        expStr += json.orgVersionName + '的';
      }
      if (json.userName) {
        expStr += json.userTypeName + '(' + json.userName + ')';
      }
      if (json.optionOfName) {
        expStr += '的' + json.optionOfName;
      }
      expStr += json.rightBracket || '';
      return expStr;
    },
    getExpressionConfigs() {
      return map(this.valueNodes, (item, index) => {
        item.config.order = index;
        return item.config;
      });
    },
    // 显示集合表达式
    displaySetExpression(callback) {
      let expressionConfigs = this.getExpressionConfigs();
      var configValue = JSON.stringify({ expressionConfigs: expressionConfigs });
      $axios
        .get(`/api/workflow/definition/getUserCustomExpression`, { params: { expressionConfig: configValue } })
        .then(({ data: result }) => {
          // if (result.data) {
          this.setExpression = result.data;
          if (typeof callback == 'function') {
            callback(result.data);
          }
          // }
        })
        .catch(error => {
          this.$message.error('检测表达式失败!');
        });
    },
    // 检测表达式
    checkUserCustomExpression(callback) {
      let expressionConfigs = this.getExpressionConfigs();
      var configValue = JSON.stringify({ expressionConfigs: expressionConfigs });
      // 检测表达式
      $axios
        .get(`/api/workflow/definition/checkUserCustomExpression`, { params: { expressionConfig: configValue } })
        .then(({ data: result }) => {
          let hasError = result.data;
          if (!hasError) {
            this.$message.error('表达式语法有误!');
          } else if (typeof callback == 'function') {
            callback(configValue);
          }
        });
    },
    // 组织版本现在确定
    getCurrentUserUnitOrgVersions() {
      if (this.optionsList.orgVersionId_1 && !this.optionsList.orgVersionId_1.length) {
        $axios
          .get(`/api/workflow/definition/getCurrentUserUnitOrgVersions`, { params: {} })
          .then(({ data: result }) => {
            if (result.data) {
              this.optionsList.orgVersionId_1 = map(result.data, item => {
                return {
                  id: item.id,
                  text: item.name
                };
              });
            }
          })
          .catch(error => {});
      }
    },
    // 组织版本文档域
    getFormFields() {
      if (
        this.optionsList.orgVersionId_2 &&
        !this.optionsList.orgVersionId_2.length &&
        this.workFlowData &&
        this.workFlowData.property.formID
      ) {
        $axios
          .get(`/api/workflow/definition/getFormFields`, { params: { formUuid: this.workFlowData.property.formID } })
          .then(({ data: result }) => {
            if (result.data) {
              this.optionsList.orgVersionId_2 = map(result.data, item => {
                return {
                  id: item.id,
                  text: item.name
                };
              });
            }
          })
          .catch(error => {});
      }
    },
    // 可选项
    getTaskUserOptionUsers() {
      if (this.optionsList && (!this.optionsList.optionTreeData || !this.optionsList.optionTreeData.length)) {
        $axios
          .post('/json/data/services', {
            argTypes: [],
            serviceName: 'flowSchemeService',
            methodName: 'getTaskUserOptionUsers',
            args: JSON.stringify([-1]),
            validate: false,
            version: ''
          })
          .then(({ data }) => {
            if (data.code == 0) {
              this.optionsList.optionTreeData = data.data;
            }
          })
          .catch(error => {});
      }
    },
    // 接口实现
    getTaskUserCustomInterfaces() {
      if (this.optionsList && (!this.optionsList.interfaceTreeData || !this.optionsList.interfaceTreeData.length)) {
        $axios
          .post('/json/data/services', {
            argTypes: [],
            serviceName: 'flowSchemeService',
            methodName: 'getTaskUserCustomInterfaces',
            args: JSON.stringify([-1]),
            validate: false,
            version: ''
          })
          .then(({ data }) => {
            if (data.code == 0) {
              this.optionsList.interfaceTreeData = data.data;
            }
          })
          .catch(error => {});
      }
    },
    expressionSelect(data) {
      this.selectId = data.id;
      this.$set(this, 'formData', data.config);
    },
    add() {
      let displayName = this.expDataToDisplayName(this.formData);
      if (displayName) {
        this.valueNodes.push({
          id: generateId(),
          displayName,
          config: deepClone(this.formData)
        });
        this.displaySetExpression();
      } else {
        this.$message.warning('空表达式!');
      }
    },
    update() {
      let displayName = this.expDataToDisplayName(this.formData);
      let index = findIndex(this.valueNodes, { id: this.selectId });
      if (index > -1) {
        this.$set(this.valueNodes[index], 'config', deepClone(this.formData));
        this.$set(this.valueNodes[index], 'displayName', displayName);
      }
      this.displaySetExpression();
    },
    del() {
      let index = findIndex(this.valueNodes, { id: this.selectId });
      if (index > -1) {
        this.valueNodes.splice(index, 1);
        this.selectId = '';
      }
      this.displaySetExpression();
    },
    setOperationChange(value, option) {
      if (value) {
        let data = option.data.props;
        this.formData.setOperationName = data.title;
      } else {
        this.formData.setOperationName = '';
      }
    },
    optionOfChange(value, option) {
      if (value) {
        let data = option.data.props;
        this.formData.optionOfName = data.title;
      } else {
        this.formData.optionOfName = '';
      }
    },
    // 组织版本变化
    orgVersionTypeChange(value, option) {
      this.formData.orgVersionId = '';
      this.formData.orgVersionName = '';
    },
    userTypeChange(value, option) {
      if (value) {
        let data = option.data.props;
        this.formData.userTypeName = data.title;
      } else {
        this.formData.userTypeName = '';
      }
      this.formData.userValue = '';
      this.formData.userName = '';
    },
    // 下拉框变化事件
    changeSelect(value, option) {
      if (value) {
        let data = option.data.props;
        this.$set(this.formData, 'userName', data.title);
      } else {
        this.$set(this.formData, 'userName', '');
      }
    },
    // 树下拉框变化
    changeOrg({ value, label, nodes }) {
      this.$set(this.formData, 'userName', label);
      this.$set(this.formData, 'valuePath', nodes.map(node => node.keyPath).join(';'));
    },
    changeFields({ value, label, extra }) {
      if (typeof label == 'object') {
        label = label.join(';');
      }
      this.$set(this.formData, 'userName', label);
    },
    // 树下拉框变化
    changeTreeSelect(value, label, extra) {
      if (typeof label == 'object') {
        label = label.join(';');
      }
      this.$set(this.formData, 'userName', label);
    },
    // 环节字段
    changeTask(value, option) {
      let label = map(option, item => {
        let data = item.data.props;
        return data.title;
      });
      this.$set(this.formData, 'userName', label.join(';'));
    }
  },

  watch: {
    // formData: {
    //   deep: true,
    //   handler(v) {
    //     console.log(JSON.stringify(v));
    //   }
    // }
  }
};
</script>
<style lang="less">
.wf-users-select-custom-modal {
  .ant-row {
    margin-bottom: 4px;
    display: flex;
    .ant-form-item-control-wrapper {
      flex: 1 1 auto;

      .ant-select {
        margin-right: var(--w-margin-3xs);
      }

      .expressionSelect {
        min-height: 60px;
        border: var(--w-border-color-base) 1px solid;
        border-radius: var(--w-border-radius-2);
        padding: var(--w-padding-3xs);
        .expression-item {
          color: var(--w-text-color-dark);
          padding: var(--w-padding-3xs);
          cursor: pointer;
          line-height: var(--w-line-height);
          &.active {
            background-color: var(--w-primary-color-2);
            border-radius: var(--w-border-radius-2);
          }
        }
      }
    }
  }
}
</style>
