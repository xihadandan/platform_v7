<template>
  <a-form-model
    class="basic-info"
    :model="eventPublishConfig"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 7 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
    :colon="false"
  >
    <a-form-model-item label="触发事件" prop="eventId">
      <a-select
        v-model="eventPublishConfig.eventId"
        show-search
        style="width: 100%"
        :filter-option="false"
        :options="eventOptions"
        @change="handleEventChange"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="触发类型" prop="triggerType">
      <a-select
        v-model="eventPublishConfig.triggerType"
        show-search
        style="width: 100%"
        :filter-option="false"
        :options="triggerTypeOptions"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item
      v-if="
        eventPublishConfig.triggerType == 'TASK_CREATED' ||
        eventPublishConfig.triggerType == 'TASK_COMPLETED' ||
        eventPublishConfig.triggerType == 'TASK_BELONG'
      "
      label="触发环节"
      prop="taskIds"
    >
      <a-select v-model="eventPublishConfig.taskIds" mode="multiple" show-search style="width: 100%" :filter-option="filterSelectOption">
        <a-select-option v-for="d in flowOptionData.taskIds" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item v-if="eventPublishConfig.triggerType == 'TASK_OPERATION'" label="环节操作" prop="taskIds">
      <a-row>
        <a-col span="16">
          <a-select
            v-model="eventPublishConfig.taskIds"
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
          <a-select v-model="eventPublishConfig.actionType" show-search style="width: 100%" :filter-option="filterSelectOption">
            <a-select-option value="Submit">提交</a-select-option>
            <a-select-option value="Rollback">退回</a-select-option>
            <a-select-option value="Cancel">撤回</a-select-option>
          </a-select>
        </a-col>
      </a-row>
    </a-form-model-item>
    <a-form-model-item v-if="eventPublishConfig.triggerType == 'DIRECTION_TRANSITION'" label="触发流向" prop="directionIds">
      <a-select
        v-model="eventPublishConfig.directionIds"
        mode="multiple"
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
      >
        <a-select-option v-for="d in flowOptionData.directions" :key="d.id">{{ d.text }} ({{ d.id }})</a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="附加条件表达式">
      <a-switch v-model="eventPublishConfig.additionalCondition" checked-children="是" un-checked-children="否" />
    </a-form-model-item>
    <template v-if="eventPublishConfig.additionalCondition">
      <a-form-model-item label="脚本类型">
        <a-radio-group v-model="eventPublishConfig.expressionScriptType" default-value="groovy">
          <a-radio value="groovy">groovy脚本</a-radio>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="表达式">
        <a-textarea v-model="eventPublishConfig.conditionExpression"></a-textarea>
        <div class="remark" v-html="groovyScriptRemark"></div>
      </a-form-model-item>
    </template>

    <a-form-model-item v-if="milestone" label="交付物字段" prop="resultField">
      <a-select v-model="eventPublishConfig.resultField" show-search style="width: 100%" :filter-option="filterSelectOption">
        <a-select-option v-for="d in flowOptionData.formFields" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import { generateId } from '../../designer/utils';
import { groovyScriptRemark } from '../../designer/remarks';
import { isEmpty } from 'lodash';
export default {
  props: {
    eventPublishConfig: {
      type: Object,
      default() {
        return {
          id: generateId(),
          triggerConditionType: 'triggerOption'
        };
      }
    }
  },
  inject: ['flowOptionData', 'itemDefinition', 'filterSelectOption'],
  data() {
    return {
      triggerTypeOptions: [
        { label: '流程开始', value: 'FLOW_STARTED' },
        { label: '流程办结', value: 'FLOW_END' },
        { label: '环节创建', value: 'TASK_CREATED' },
        { label: '环节完成', value: 'TASK_COMPLETED' },
        { label: '环节操作', value: 'TASK_OPERATION' },
        { label: '环节归属', value: 'TASK_BELONG' },
        { label: '流向流转', value: 'DIRECTION_TRANSITION' }
      ],
      rules: {
        eventName: [{ required: true, message: '不能为空！', trigger: 'blur' }]
      },
      groovyScriptRemark
    };
  },
  computed: {
    eventOptions() {
      let defineEvents = this.itemDefinition.defineEvents || [];
      let eventOptions = defineEvents
        .filter(event => !event.builtIn)
        .map(event => ({ label: event.name, value: event.id, milestone: event.milestone }));
      return [{ label: '', value: '' }, ...eventOptions];
    },

    milestone() {
      let item = this.eventOptions.find(item => item.value == this.eventPublishConfig.eventId);
      return item && item.milestone;
    }
  },
  methods: {
    handleEventChange(value) {
      if (isEmpty(value)) {
        this.eventPublishConfig.eventName = '';
        this.milestone = false;
      } else {
        let item = this.eventOptions.find(item => item.value == value);
        this.eventPublishConfig.eventName = item.label;
        this.milestone = item.milestone;
      }
    },
    collect() {
      return this.$refs.basicForm
        .validate()
        .then(valid => {
          if (valid) {
            return this.eventPublishConfig;
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
.remark {
  line-height: 1.5;
  font-size: 14px;
}
</style>
