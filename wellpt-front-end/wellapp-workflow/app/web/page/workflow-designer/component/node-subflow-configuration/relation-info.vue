<template>
  <!-- 前置流程关系表单 -->
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    :colon="false"
    labelAlign="right"
    :label-col="{ span: 5 }"
    :wrapper-col="{ span: 19, style: { textAlign: 'right' } }"
  >
    <a-form-model-item prop="newFlowId" label="子流程">
      <w-select
        v-model="formData.newFlowId"
        formDataFieldName="newFlowName"
        :formData="formData"
        :options="newFlows"
        :getPopupContainer="getPopupContainer"
        @change="(value, option) => changeFlowId({ value, type: 'newFlowId' })"
      />
    </a-form-model-item>
    <a-form-model-item prop="taskId" label="环节">
      <w-select
        v-model="formData.taskId"
        formDataFieldName="taskName"
        :formData="formData"
        :options="taskOptions"
        :replaceFields="replaceFields"
        :getPopupContainer="getPopupContainer"
      />
    </a-form-model-item>
    <a-form-model-item prop="frontNewFlowId" label="前置子流程">
      <w-select
        v-model="formData.frontNewFlowId"
        formDataFieldName="frontNewFlowName"
        :formData="formData"
        :options="frontFlows"
        :getPopupContainer="getPopupContainer"
        @change="(value, option) => changeFlowId({ value, type: 'frontNewFlowId' })"
      />
    </a-form-model-item>
    <a-form-model-item prop="frontTaskId" label="前置环节">
      <w-select
        v-model="formData.frontTaskId"
        formDataFieldName="frontTaskName"
        :formData="formData"
        :options="frontTaskOptions"
        :replaceFields="replaceFields"
        :getPopupContainer="getPopupContainer"
      />
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import WSelect from '../components/w-select';
import { fetchFlowTasksById } from '../api/index';

export default {
  name: 'RelationInfo',
  inject: ['subflowData'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      rules: {
        newFlowId: { required: true, message: '请选择子流程' },
        taskId: { required: true, message: '请选择环节' },
        frontNewFlowId: { required: true, message: '请选择前置子流程' },
        frontTaskId: { required: true, message: '请选择前置环节' }
      },
      replaceFields: {
        title: 'name',
        key: 'id',
        value: 'id'
      },
      taskOptions: [], // 环节选项
      frontTaskOptions: [] // 前置环节选项
    };
  },
  components: {
    WSelect
  },
  computed: {
    // 子流程选项
    newFlows() {
      let newFlows = [];
      if (this.subflowData && this.subflowData.newFlows) {
        this.subflowData.newFlows.forEach(item => {
          if (item.value !== this.formData.frontNewFlowId) {
            newFlows.push({
              label: item.name,
              value: item.value
            });
          }
        });
      }
      return newFlows;
    },
    // 前置子流程选项
    frontFlows() {
      let frontFlows = [];
      if (this.subflowData && this.subflowData.newFlows) {
        this.subflowData.newFlows.forEach(item => {
          if (item.value !== this.formData.newFlowId) {
            frontFlows.push({
              label: item.name,
              value: item.value
            });
          }
        });
      }
      return frontFlows;
    }
  },
  created() {
    if (this.formData.newFlowId) {
      fetchFlowTasksById(this.formData.newFlowId).then(res => {
        this.taskOptions = res;
      });
    }
    if (this.formData.frontNewFlowId) {
      fetchFlowTasksById(this.formData.frontNewFlowId).then(res => {
        this.frontTaskOptions = res;
      });
    }
  },
  methods: {
    changeFlowId({ value, type }) {
      if (type === 'newFlowId') {
        for (const key in this.formData) {
          if (key === 'id' || key === 'newFlowId') {
            continue;
          }
          this.formData[key] = '';
        }
        this.frontTaskOptions = [];
        fetchFlowTasksById(value).then(res => {
          this.taskOptions = res;
        });
      } else {
        this.formData.frontTaskId = '';
        this.formData.frontTaskName = '';
        fetchFlowTasksById(value).then(res => {
          this.frontTaskOptions = res;
        });
      }
    },
    validate(callback) {
      this.$refs.form.validate((valid, error) => {
        callback({ valid, error, data: this.formData });
      });
    },
    getPopupContainer(triggerNode) {
      return document.body;
    }
  }
};
</script>
