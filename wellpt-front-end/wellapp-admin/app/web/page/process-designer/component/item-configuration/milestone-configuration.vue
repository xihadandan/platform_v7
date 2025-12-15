<template>
  <a-form-model
    class="basic-info"
    :model="milestoneConfig"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 7 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
    :colon="false"
  >
    <a-form-model-item label="里程碑事件" prop="name">
      <a-input v-model="milestoneConfig.name" />
    </a-form-model-item>
    <a-form-model-item label="触发类型" prop="triggerType">
      <a-select
        v-model="milestoneConfig.triggerType"
        show-search
        style="width: 100%"
        :filter-option="false"
        :options="triggerTypeOptions"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item v-show="milestoneConfig.triggerType == '30' || milestoneConfig.triggerType == '40'" label="触发环节" prop="taskId">
      <a-select v-model="milestoneConfig.taskId" show-search style="width: 100%" :filter-option="false">
        <a-select-option v-for="d in flowOptionData.taskIds" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item v-show="milestoneConfig.triggerType == '50'" label="触发流向" prop="directionId">
      <a-select v-model="milestoneConfig.directionId" show-search style="width: 100%" :filter-option="false">
        <a-select-option v-for="d in flowOptionData.directions" :key="d.id">{{ d.text }} ({{ d.id }})</a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="交付物字段" prop="resultField">
      <a-select v-model="milestoneConfig.resultField" show-search style="width: 100%" :filter-option="false">
        <a-select-option v-for="d in flowOptionData.formFields" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import { generateId } from '../../designer/utils';
export default {
  props: {
    milestoneConfig: {
      type: Object,
      default() {
        return {
          id: generateId()
        };
      }
    }
  },
  inject: ['flowOptionData'],
  data() {
    return {
      triggerTypeOptions: [
        { value: '30', label: '环节创建' },
        { value: '40', label: '环节完成' },
        { value: '50', label: '流向流转' }
      ],
      rules: {
        name: [{ required: true, message: '名称不能为空！', trigger: 'blur' }]
      }
    };
  },
  methods: {
    collect() {
      return this.$refs.basicForm
        .validate()
        .then(valid => {
          if (valid) {
            return this.milestoneConfig;
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

<style></style>
