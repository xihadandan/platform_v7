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
    <a-form-model-item v-if="eventPublishConfig.triggerType == 'ITEM_STATE_CHANGED'" label="事项状态变更为" prop="itemState">
      <a-select
        v-model="eventPublishConfig.itemState"
        show-search
        style="width: 100%"
        :options="itemStateOptions"
        :filter-option="filterSelectOption"
      ></a-select>
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
        <div class="remark" v-html="groovyItemEventScriptRemark"></div>
      </a-form-model-item>
    </template>
    <a-form-model-item v-if="milestone" label="交付物字段" prop="resultField">
      <a-select
        v-model="eventPublishConfig.resultField"
        show-search
        style="width: 100%"
        :options="formFieldOptions"
        :filter-option="filterSelectOption"
      ></a-select>
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import { generateId } from '../../designer/utils';
import { groovyItemEventScriptRemark } from '../../designer/remarks';
import { isEmpty } from 'lodash';
export default {
  props: {
    eventPublishConfig: {
      type: Object,
      default() {
        return {
          id: generateId(),
          expressionScriptType: 'groovy'
        };
      }
    },
    itemDefinition: Object
  },
  inject: ['filterSelectOption', 'getCacheData'],
  data() {
    return {
      triggerTypeOptions: [{ label: '业务事项状态变更', value: 'ITEM_STATE_CHANGED' }],
      itemStateOptions: [
        { value: '00', label: '草稿' },
        { value: '10', label: '办理中' },
        { value: '20', label: '暂停' },
        { value: '30', label: '办结' },
        { value: '40', label: '取消' }
      ],
      formFieldOptions: [],
      rules: {
        eventName: [{ required: true, message: '不能为空！', trigger: 'blur' }]
      },
      groovyItemEventScriptRemark
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
  created() {
    if (this.itemDefinition && this.itemDefinition.formConfig && this.itemDefinition.formConfig.formUuid) {
      this.loadFormFieldOptions(this.itemDefinition.formConfig.formUuid);
    }
  },
  methods: {
    handleEventChange(value) {
      if (isEmpty(value)) {
        this.eventPublishConfig.eventName = '';
      } else {
        let item = this.eventOptions.find(item => item.value == value);
        this.eventPublishConfig.eventName = item.label;
      }
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
