<template>
  <a-form-model
    class="basic-info"
    :model="dispenseFormConfig"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 7 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
    :colon="false"
  >
    <a-form-model-item label="事项名称" prop="itemName">
      <a-input v-model="dispenseFormConfig.itemName" readOnly />
    </a-form-model-item>
    <a-form-model-item label="表单分发类型" prop="type">
      <a-select v-model="dispenseFormConfig.type" style="width: 100%" :filter-option="false">
        <a-select-option value="1">使用主表单</a-select-option>
        <a-select-option value="2">使用单据转换</a-select-option>
      </a-select>
    </a-form-model-item>
    <keep-alive>
      <template v-if="dispenseFormConfig.type == '2'">
        <div>
          <a-form-model-item label="单据转换" prop="botId">
            <a-select
              v-model="dispenseFormConfig.botId"
              style="width: 100%"
              show-search
              :filter-option="filterSelectOption"
              @change="handleBotRuleChange"
            >
              <a-select-option v-for="d in botRuleOptions" :key="d.id">
                {{ d.text }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="使用表单" prop="formName">
            <a-input v-model="dispenseFormConfig.formName" readOnly />
          </a-form-model-item>
          <a-form-model-item label="业务主体名称字段" prop="entityNameField">
            <a-select
              v-model="dispenseFormConfig.entityNameField"
              style="width: 100%"
              show-search
              :filter-option="filterSelectOption"
              :options="formFieldOptions"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="业务主体ID字段" prop="entityIdField">
            <a-select
              v-model="dispenseFormConfig.entityIdField"
              style="width: 100%"
              show-search
              :filter-option="filterSelectOption"
              :options="formFieldOptions"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="办理时限字段" prop="timeLimitField">
            <a-select
              v-model="dispenseFormConfig.timeLimitField"
              style="width: 100%"
              show-search
              :filter-option="filterSelectOption"
              :options="formFieldOptions"
            ></a-select>
          </a-form-model-item>
        </div>
      </template>
    </keep-alive>
  </a-form-model>
</template>

<script>
export default {
  props: {
    dispenseFormConfig: Object
  },
  inject: ['designer', 'filterSelectOption', 'getCacheData'],
  data() {
    return {
      rules: {},
      botRuleOptions: [],
      formFieldOptions: []
    };
  },
  created() {
    this.handleBotRuleSearch();
  },
  methods: {
    handleBotRuleSearch(value = '') {
      let _this = this;
      _this
        .getCacheData('botRuleOptions', (resolve, reject) => {
          _this.$axios
            .post('/common/select2/query', {
              serviceName: 'botRuleConfFacadeService',
              queryMethod: 'loadSelectData',
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
        .then(botRuleOptions => {
          _this.botRuleOptions = botRuleOptions;
        });
    },
    handleBotRuleChange(botRuleId) {
      $axios
        .post('/json/data/services', {
          serviceName: 'botRuleConfService',
          methodName: 'getById',
          args: JSON.stringify([botRuleId])
        })
        .then(({ data }) => {
          if (data.data) {
            let botRule = data.data;
            this.dispenseFormConfig.formUuid = botRule.targetObjId;
            this.loadFormFields(botRule.targetObjId);
          }
        });
    },
    loadFormFields(formUuid) {
      let _this = this;
      _this
        .getCacheData(`formDefinition_${formUuid}`, (resolve, reject) => {
          _this.$axios
            .get(`/proxy/api/biz/process/definition/getFormDefinitionByFormUuid/${formUuid}`)
            .then(({ data }) => {
              if (!data.data) {
                console.error('form definition is null', formUuid);
                reject({});
              } else {
                let formDefinition = JSON.parse(data.data);
                resolve(formDefinition);
              }
            })
            .catch(err => reject({}));
        })
        .then(formDefinition => {
          _this.dispenseFormConfig.formName = formDefinition.name;

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
