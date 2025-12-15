<template>
  <div>
    <a-form-model-item label="使用表单" prop="formUuid">
      <a-select
        v-model="formConfig.formUuid"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        @change="handleFormChange"
        :disabled="selectDisabled"
      >
        <a-select-option v-for="d in formOptions" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="业务主体办理过程显示位置" prop="processNodePlaceHolder">
      <a-select
        v-model="formConfig.processNodePlaceHolder"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="formBlockOptions"
        :disabled="selectDisabled"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="业务主体名称字段" prop="entityNameField">
      <a-select
        v-model="formConfig.entityNameField"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="formFieldOptions"
        :disabled="selectDisabled"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="业务主体ID字段" prop="entityIdField">
      <a-select
        v-model="formConfig.entityIdField"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="formFieldOptions"
        :disabled="selectDisabled"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="状态管理">
      <StateConfiguration :definition="formConfig" :disabled="selectDisabled" :closeOpenDrawer="false"></StateConfiguration>
    </a-form-model-item>
    <DyformSettingConfiguration :definition="formConfig" :disabled="selectDisabled" :closeOpenDrawer="false"></DyformSettingConfiguration>
  </div>
</template>

<script>
import StateConfiguration from '../state-configuration';
import DyformSettingConfiguration from '../dyform-setting-configuration/process-dyform-setting-configuration.vue';
export default {
  components: { StateConfiguration, DyformSettingConfiguration },
  props: {
    formConfig: Object,
    // 展示状态
    displayState: {
      type: String,
      default: 'edit',
      validator: function (v) {
        return ['edit', 'readonly', 'disable', 'label'].indexOf(v) !== -1;
      }
    },
    closeOpenDrawer: {
      type: Boolean,
      default: true
    }
  },
  inject: ['designer', 'filterSelectOption', 'getCacheData'],
  data() {
    this.subformMap = new Map();
    return {
      formOptions: [],
      formFieldOptions: [],
      formBlockOptions: [],
      subformOptions: [],
      subformFieldOptions: []
    };
  },
  computed: {
    selectDisabled() {
      return this.displayState == 'disable' || this.formConfig.configType == '1';
    }
  },
  mounted() {
    this.handleFormSearch();
    if (this.formConfig.formUuid) {
      this.handleFormChange(this.formConfig.formUuid);
    }
  },
  methods: {
    handleFormSearch(value = '') {
      let _this = this;
      _this
        .getCacheData('formOptions', (resolve, reject) => {
          _this.$axios
            .post('/common/select2/query', {
              serviceName: 'bizProcessDefinitionFacadeService',
              queryMethod: 'listDyFormDefinitionSelectData',
              searchValue: value,
              pageSize: 1000,
              pageNo: 1
            })
            .then(({ data }) => {
              if (data.results) {
                resolve(data.results);
                // _this.formOptions = data.results;
              }
            })
            .catch(err => reject(err));
        })
        .then(formOptions => {
          _this.formOptions = formOptions;
        });
    },
    handleFormChange(formUuid) {
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
          _this.formConfig.formName = formDefinition.name;

          let fieldSelectData = [{ value: 'uuid', label: 'UUID' }];
          let fields = formDefinition.fields || {};
          // 字段
          for (let fieldName in fields) {
            let field = fields[fieldName];
            fieldSelectData.push({ value: field.name, label: field.displayName });
          }
          _this.formFieldOptions = fieldSelectData;

          // 区块
          let blockSelectData = [];
          let blocks = formDefinition.blocks || {};
          for (let blockCode in blocks) {
            let block = blocks[blockCode];
            blockSelectData.push({ value: block.blockCode + '', label: block.blockTitle });
          }
          _this.formBlockOptions = blockSelectData;

          // 从表
          let subformSelectData = [];
          let subforms = formDefinition.subforms || {};
          for (let subformUuid in subforms) {
            let subform = subforms[subformUuid];
            subformSelectData.push({ value: subform.outerId, label: subform.displayName || subform.name });
            _this.subformMap.set(subform.outerId, subform);
          }
          _this.subformOptions = subformSelectData;

          // 触发从表变更
          if (_this.formConfig.materialSubformId) {
            _this.handleSubformChange(_this.formConfig.materialSubformId);
          }
        });
    },
    handleSubformChange(subformId) {
      const _this = this;
      if (!subformId) {
        _this.subformFieldOptions = [];
        return;
      }
      let subform = _this.subformMap.get(subformId);
      if (!subform) {
        _this.subformFieldOptions = [];
        return;
      }
      let fieldSelectData = [{ value: 'uuid', label: 'UUID' }];
      let fields = subform.fields || {};
      for (let fieldName in fields) {
        let field = fields[fieldName];
        fieldSelectData.push({ value: field.name, label: field.displayName });
      }
      _this.subformFieldOptions = fieldSelectData;
    }
  }
};
</script>

<style></style>
