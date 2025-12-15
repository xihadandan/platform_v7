<template>
  <div>
    委托满足以下
    <a-select
      size="small"
      :options="[
        { label: '全部', value: 'all' },
        { label: '任一', value: 'any' }
      ]"
      style="width: 65px"
      v-model="configuration.match"
    />
    条件的数据
    <a-list class="condition-list" item-layout="horizontal" :data-source="configuration.conditions">
      <a-list-item slot="renderItem" slot-scope="item" class="condition-item">
        <a-button type="link" slot="actions" @click="deleteCondition(item)">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        </a-button>
        <a-select
          v-model="item.type"
          :options="conditionTypeOptions"
          @change="conditionTypeChange(item)"
          :style="{ width: '160px' }"
        ></a-select>
        &nbsp;
        <template v-if="item.type == 'formField'">
          <a-tree-select
            v-model="item.flowDefId"
            allow-clear
            style="width: 250px"
            :dropdown-style="{ maxHeight: '300px', overflow: 'auto' }"
            :tree-data="flowTreeData"
            :replaceFields="{ title: 'name', key: 'data', value: 'data' }"
            placeholder="请选择"
            @select="(value, node, extra) => onFlowSelect(value, node, extra, item)"
          ></a-tree-select>
          &nbsp;
          <a-select
            show-search
            v-model="item.formField"
            :options="getFormFieldOptions(item.flowDefId)"
            :filter-option="filterSelectOption"
            :style="{ width: '120px' }"
          />
          &nbsp;
        </template>
        <template v-if="item.type == 'workflowField'">
          <a-select
            show-search
            option-filter-prop="children"
            v-model="item.workflowField"
            :options="workflowFieldOptions"
            :filter-option="filterSelectOption"
            :style="{ width: '200px' }"
          />
          &nbsp;
        </template>
        <a-select v-model="item.operator" :options="operatorOptions" :style="{ width: '120px' }" />
        &nbsp;
        <!-- <a-input v-model="item.value" :style="{ width: '50%' }" /> -->
        <DelegationConditionVariableDefine v-model="item.value" :formData="formData"></DelegationConditionVariableDefine>
      </a-list-item>
    </a-list>
    <a-button
      size="small"
      icon="plus"
      type="link"
      @click="
        () => {
          configuration.conditions.push({
            type: undefined,
            value: undefined,
            operator: undefined
          });
        }
      "
    >
      添加
    </a-button>
  </div>
</template>

<script>
import { deepClone } from '@framework/vue/utils/util';
import { filterSelectOption } from '@framework/vue/utils/function';
import DelegationConditionVariableDefine from './delegation-condition-variable-define.vue';
export default {
  props: {
    formData: Object,
    configuration: Object
  },
  components: { DelegationConditionVariableDefine },
  data() {
    return {
      conditionTypeOptions: [
        { label: '表单字段', value: 'formField' },
        { label: '工作流字段', value: 'workflowField' }
      ],
      flowTreeData: [],
      formFieldOptionMap: {},
      workflowFieldOptions: [
        { label: '流程名称', value: 'flowDefName' },
        { label: '流程ID', value: 'flowDefId' },
        { label: '流程编号', value: 'flowCode' },
        { label: '发起人姓名', value: 'startUserName' },
        { label: '发起人所在部门名称', value: 'startUserDepartmentName' },
        { label: '发起人所在部门名称全路径', value: 'startUserDepartmentPathName' },
        { label: '当前环节名称', value: 'taskName' },
        { label: '当前环节ID', value: 'taskId' }
      ],
      operatorOptions: [
        { label: '等于', value: '==' },
        { label: '不等于', value: '!=' },
        { label: '大于', value: '>' },
        { label: '大于等于', value: '>=' },
        { label: '小于', value: '<' },
        { label: '小于等于', value: '<=' },
        { label: '包含', value: 'contains' },
        { label: '不包含', value: 'not contains' }
      ]
    };
  },
  watch: {
    'formData.contentConfig': {
      deep: true,
      handler(newVal, oldVal) {
        this.filterFlowTreeData();
      }
    }
  },
  created() {
    this.loadFlowTreeData();
    let conditions = this.configuration.conditions || [];
    conditions.forEach(condition => {
      if (condition.type == 'formField' && condition.flowDefId) {
        this.onFlowSelect(condition.flowDefId);
      }
    });
  },
  methods: {
    filterSelectOption,
    filterFlowTreeData() {
      const _this = this;
      let flowTreeData = deepClone(_this.initTreeData);
      flowTreeData.forEach(category => {
        category.selectable = false;
        category.style = {};
        let categorySelected = _this.isFlowInContent(category);
        let children = category.children || [];
        let hasChildSelected = false;
        children.forEach(item => {
          item.style = {};
          if (!categorySelected && !_this.isFlowInContent(item)) {
            item.style = { display: 'none' };
          } else {
            hasChildSelected = true;
          }
        });
        if (!categorySelected && !hasChildSelected) {
          category.style = { display: 'none' };
        }
      });
      _this.flowTreeData = [...flowTreeData];
    },
    isFlowInContent(nodeData) {
      let _this = this;
      let formData = _this.formData;
      let contentConfig = formData.contentConfig;
      if (!contentConfig) {
        return false;
      }
      let values = contentConfig.values || [];
      if (contentConfig.type == 'all') {
        return true;
      }
      if (contentConfig.type == 'flow') {
        return values.includes(nodeData.data);
      }
      if (contentConfig.type == 'task') {
        for (let index = 0; index < values.length; index++) {
          let value = values[index];
          if (value && value.startsWith(nodeData.data + ':')) {
            return true;
          }
        }
      }
      return false;
    },
    loadFlowTreeData() {
      let _this = this;
      this.$axios
        .post('/json/data/services', {
          serviceName: 'workflowDelegationSettiongsService',
          methodName: 'getContentAsTreeAsync',
          args: JSON.stringify(['-1'])
        })
        .then(({ data: result }) => {
          if (result.data) {
            _this.initTreeData = deepClone(result.data);
            _this.flowTreeData = result.data;
            _this.filterFlowTreeData();
          }
        });
    },
    conditionTypeChange(condition) {
      let operatorOptions = this.operatorOptions;
      let operator = operatorOptions.find(item => item.value == condition.operator);
      if (!operator) {
        condition.operator = null;
      }
    },
    getFormFieldOptions(flowDefId) {
      return this.formFieldOptionMap[flowDefId] || [];
    },
    onFlowSelect(value, node, extra, item) {
      const _this = this;
      if (!value || _this.formFieldOptionMap[value]) {
        return;
      }

      if (item && item.formField) {
        item.formField = null;
      }

      _this.$axios.get(`/proxy/api/workflow/definition/getFormFieldsByFlowDefId?flowDefId=${value}`).then(({ data: result }) => {
        if (result.data) {
          let formFieldOptions = [];
          result.data.forEach(item => {
            if (item.children && item.children.length) {
              //从表不要
            } else {
              formFieldOptions.push({
                value: item.id,
                label: item.name,
                title: item.name
              });
            }
          });
          _this.$set(_this.formFieldOptionMap, value, formFieldOptions);
        }
      });
    },
    deleteCondition(condition) {
      let deleteIndex = this.configuration.conditions.indexOf(condition);
      if (deleteIndex != -1) {
        this.configuration.conditions.splice(deleteIndex, 1);
      }
      if (this.configuration.conditions.length == 0) {
        this.formData.conditionConfig = null;
      }
    }
  }
};
</script>

<style lang="less" scoped>
.condition-item {
  ::v-deep .ant-list-item-action {
    margin-left: 6px;
  }
}
</style>
