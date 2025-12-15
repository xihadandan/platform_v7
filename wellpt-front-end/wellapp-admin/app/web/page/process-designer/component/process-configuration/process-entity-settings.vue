<template>
  <div>
    <a-form-model-item label="业务主体表单" prop="formUuid">
      <a-select
        v-model="entityConfig.formUuid"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        @change="handleFormChange"
      >
        <a-select-option v-for="d in formOptions" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="业务主体ID字段" prop="entityIdField">
      <a-select
        v-model="entityConfig.entityIdField"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="formFieldOptions"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="状态管理">
      <StateConfiguration :definition="entityConfig"></StateConfiguration>
    </a-form-model-item>
    <a-form-model-item label="状态计时">
      <ProcessEntityTimers :entityDefinition="entityConfig"></ProcessEntityTimers>
    </a-form-model-item>
  </div>
</template>

<script>
import StateConfiguration from '../state-configuration';
import ProcessEntityTimers from './process-entity-timers.vue';
export default {
  components: { StateConfiguration, ProcessEntityTimers },
  props: {
    entityConfig: Object
  },
  inject: ['designer', 'filterSelectOption', 'getCacheData'],
  data() {
    this.subformMap = new Map();
    return {
      formOptions: [],
      formFieldOptions: []
    };
  },
  mounted() {
    this.handleFormSearch();
    if (this.entityConfig.formUuid) {
      this.handleFormChange(this.entityConfig.formUuid);
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
          _this.entityConfig.formName = formDefinition.name;

          let fieldSelectData = [{ value: 'uuid', label: 'UUID' }];
          let fields = formDefinition.fields || {};
          // 字段
          for (let fieldName in fields) {
            let field = fields[fieldName];
            fieldSelectData.push({ value: field.name, label: field.displayName });
          }
          _this.formFieldOptions = fieldSelectData;
        });
    }
  }
};
</script>

<style></style>
