<template>
  <a-form-model
    class="basic-info"
    :model="fieldMapping"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 7 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'left' } }"
    :colon="false"
  >
    <a-form-model-item label="源字段来源">
      <a-radio-group v-model="fieldMapping.sourceType" @change="sourceTypeChange">
        <a-radio v-for="item in sourceTypeOptions" :key="item.value" :value="item.value" :disabled="item.disabled">
          {{ item.label }}
        </a-radio>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="源字段" prop="sourceField">
      <a-select
        v-model="fieldMapping.sourceField"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="sourceFormFieldOptions"
        @change="onSourceFormFieldChange"
        :disabled="selectDisabled"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="目标字段" prop="targetField">
      <a-select
        v-model="fieldMapping.targetField"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="targetFormFieldOptions"
        @change="onTargetFormFieldChange"
        :disabled="selectDisabled"
      ></a-select>
    </a-form-model-item>
  </a-form-model>
</template>

<script>
export default {
  props: {
    itemDefinition: Object,
    formConfig: Object,
    fieldMapping: {
      type: Object,
      default() {
        return {
          sourceType: 'entity',
          sourceField: undefined,
          sourceFieldName: undefined,
          targetField: undefined,
          targetFieldName: undefined
        };
      }
    }
  },
  inject: ['designer', 'filterSelectOption', 'getCacheData'],
  data() {
    return {
      sourceFormFieldOptions: [],
      targetFormFieldOptions: [],
      rules: {
        sourceField: [{ required: true, message: '不能为空！', trigger: 'blur' }],
        targetField: [{ required: true, message: '不能为空！', trigger: 'blur' }]
      }
    };
  },
  computed: {
    sourceTypeOptions() {
      return [
        { label: '业务主体', value: 'entity' },
        { label: '事项源', value: 'item', disabled: !this.itemDefinition.itemDefId }
      ];
    },
    targetExcludeFields() {
      const _this = this;
      let fields = [];
      if (_this.formConfig.itemNameField) {
        fields.push(_this.formConfig.itemNameField);
      }
      if (_this.formConfig.itemCodeField) {
        fields.push(_this.formConfig.itemCodeField);
      }
      if (_this.formConfig.entityNameField) {
        fields.push(_this.formConfig.entityNameField);
      }
      if (_this.formConfig.entityIdField) {
        fields.push(_this.formConfig.entityIdField);
      }
      if (_this.formConfig.timeLimitField) {
        fields.push(_this.formConfig.timeLimitField);
      }
      return fields;
    }
  },
  created() {
    this.loadFormFieldOptions();
  },
  methods: {
    loadFormFieldOptions() {
      const _this = this;

      _this.sourceTypeChange();

      let targetFormUuid = _this.formConfig.formUuid;
      _this.loadFormDefinition(targetFormUuid).then(formDefinition => {
        _this.targetFormFieldOptions = _this.getFormFieldSelectData(formDefinition, false, _this.targetExcludeFields);
      });
    },
    getFormFieldSelectData(formDefinition, includeUuid = false, excludeFields = []) {
      let fieldSelectData = includeUuid ? [{ value: 'uuid', label: 'UUID' }] : [];
      let fields = formDefinition.fields || {};
      // 字段
      for (let fieldName in fields) {
        let field = fields[fieldName];
        if (excludeFields.length && excludeFields.includes(field.name)) {
          continue;
        }
        fieldSelectData.push({ value: field.name, label: field.displayName });
      }
      return fieldSelectData;
    },
    loadFormDefinition(formUuid, defaultUrl) {
      const _this = this;
      return _this.getCacheData(`formDefinition_${formUuid}`, (resolve, reject) => {
        if (!formUuid) {
          resolve({});
          return;
        }
        let url = defaultUrl ? defaultUrl : `/proxy/api/biz/process/definition/getFormDefinitionByFormUuid/${formUuid}`;
        _this.$axios.get(url).then(({ data }) => {
          if (!data.data) {
            console.error('form definition is null', formUuid);
            resolve({});
          } else {
            let formDefinition = JSON.parse(data.data);
            resolve(formDefinition);
          }
        });
      });
    },
    sourceTypeChange() {
      const _this = this;
      if (_this.fieldMapping.sourceType == 'entity') {
        let processDefinition = _this.designer.getProcessDefinition();
        let entityFormUuid = processDefinition.entityConfig && processDefinition.entityConfig.formUuid;
        _this.loadFormDefinition(entityFormUuid).then(formDefinition => {
          _this.sourceFormFieldOptions = _this.getFormFieldSelectData(formDefinition, true);
        });
      } else {
        let itemDefId = _this.itemDefinition.itemDefId;
        let url = `/proxy/api/biz/process/definition/getFormDefinitionByItemDefId/${itemDefId}`;
        _this.loadFormDefinition(itemDefId, url).then(formDefinition => {
          _this.sourceFormFieldOptions = _this.getFormFieldSelectData(formDefinition, true);
        });
      }
    },
    onSourceFormFieldChange(value, option) {
      let item = this.sourceFormFieldOptions.find(item => item.value == value);
      this.fieldMapping.sourceFieldName = item ? item.label : '';
    },
    onTargetFormFieldChange(value, option) {
      let item = this.targetFormFieldOptions.find(item => item.value == value);
      this.fieldMapping.targetFieldName = item ? item.label : '';
    },
    collect() {
      return this.$refs.basicForm
        .validate()
        .then(valid => {
          if (valid) {
            return this.fieldMapping;
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
