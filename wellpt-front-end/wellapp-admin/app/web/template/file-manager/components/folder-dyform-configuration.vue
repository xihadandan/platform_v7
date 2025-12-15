<template>
  <div>
    <a-form-model-item>
      <template slot="label">
        <a-space>
          动态表单
          <a-popover>
            <template slot="content">设置文件库的默认动态表单，创建表单数据时默认使用该表单</template>
            <a-icon type="info-circle" />
          </a-popover>
        </a-space>
      </template>
      <a-select
        v-model="configuration.formUuid"
        showSearch
        allow-clear
        :options="formOptions"
        :filter-option="filterSelectOption"
        @search="onFormSearch"
        @change="onFormChange"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item>
      <template slot="label">
        <a-space>
          文档标题
          <a-popover>
            <template slot="content">引用表单标题</template>
            <a-icon type="info-circle" />
          </a-popover>
        </a-space>
      </template>
      {{ titleContent }}
      <!-- <a-select
        v-model="configuration.fileNameField"
        showSearch
        allow-clear
        :options="formFieldOptions"
        :filter-option="filterSelectOption"
        @change="value => updateFormFieldConfigName(value, 'fileNameFieldName')"
      ></a-select> -->
    </a-form-model-item>
    <a-form-model-item>
      <template slot="label">
        <a-space>
          文档状态字段
          <a-popover>
            <template slot="content">将文档状态回写至表单字段</template>
            <a-icon type="info-circle" />
          </a-popover>
        </a-space>
      </template>
      <a-select
        v-model="configuration.fileStatusField"
        showSearch
        allow-clear
        :options="formFieldOptions"
        :filter-option="filterSelectOption"
        @change="value => updateFormFieldConfigName(value, 'fileStatusFieldName')"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="阅读人员字段">
      <a-select
        v-model="configuration.readFileField"
        showSearch
        allow-clear
        :options="formFieldOptions"
        :filter-option="filterSelectOption"
        @change="value => updateFormFieldConfigName(value, 'readFileFieldName')"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="编辑人员字段">
      <a-select
        v-model="configuration.editFileField"
        showSearch
        allow-clear
        :options="formFieldOptions"
        :filter-option="filterSelectOption"
        @change="value => updateFormFieldConfigName(value, 'editFileFieldName')"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item>
      <template slot="label">
        <a-space>
          展示表单
          <a-popover>
            <template slot="content">设置文件库的默认展示表单，打开表单数据时默认使用该表单</template>
            <a-icon type="info-circle" />
          </a-popover>
        </a-space>
      </template>
      <a-select
        v-model="configuration.displayFormUuid"
        showSearch
        allow-clear
        :options="formOptions"
        :filter-option="filterSelectOption"
        @change="onDisplayFormChange"
      ></a-select>
    </a-form-model-item>
  </div>
</template>

<script>
import { filterSelectOption } from '@framework/vue/utils/function';
export default {
  props: {
    configuration: Object
  },
  data() {
    return {
      formOptions: [],
      formFieldOptions: [],
      displayFormOptions: [],
      titleContent: ''
    };
  },
  created() {
    const _this = this;
    _this.loadFormOptions();
    // _this.loadDisplayFormOptions();
    if (_this.configuration.formUuid) {
      _this.onFormChange(false);
    }
  },
  methods: {
    filterSelectOption,
    loadFormOptions(searchValue = '') {
      const _this = this;
      $axios
        .post('/common/select2/query', {
          serviceName: 'dyFormFacade',
          queryMethod: 'queryAllPforms',
          searchValue,
          pageSize: 20,
          includeSuperAdmin: true
        })
        .then(({ data: result }) => {
          if (result.results) {
            _this.formOptions = result.results.map(item => ({ label: item.text, value: item.id }));
            // 选择的表单选项
            if (
              !searchValue &&
              _this.configuration.formUuid &&
              _this.formOptions.find(item => item.value == _this.configuration.formUuid) == null
            ) {
              _this.formOptions = [{ label: _this.configuration.formName, value: _this.configuration.formUuid }].concat(_this.formOptions);
            }
          }
        });
    },
    onFormSearch(searchValue) {
      this.loadFormOptions(searchValue);
    },
    onFormChange(value) {
      const _this = this;
      let formUuid = _this.configuration.formUuid;
      if (value) {
        let formOption = _this.formOptions.find(item => item.value == value);
        _this.configuration.formName = (formOption && formOption.label) || '';
      }

      if (!formUuid) {
        return;
      }

      _this.loadFormDefinition(formUuid);
    },
    onDisplayFormChange(value) {
      const _this = this;
      if (value) {
        let formOption = _this.formOptions.find(item => item.value == value);
        _this.configuration.displayFormName = (formOption && formOption.label) || '';
      } else {
        _this.configuration.displayFormName = '';
      }
    },
    updateFormFieldConfigName(field, nameProp) {
      const _this = this;
      let fieldOption = _this.formFieldOptions.find(item => item.value == field);
      _this.configuration[nameProp] = (fieldOption && fieldOption.label) || '';
    },
    loadDisplayFormOptions(searchValue = '') {
      const _this = this;
      $axios
        .post('/common/select2/query', {
          serviceName: 'dyFormFacade',
          queryMethod: 'queryAllVforms',
          searchValue,
          pageSize: 20,
          includeSuperAdmin: true
        })
        .then(({ data: result }) => {
          if (result.results) {
            _this.formOptions = result.results.map(item => ({ label: item.text, value: item.id }));
          }
        });
    },
    onDisplayFormSearch(searchValue) {
      this.loadDisplayFormOptions(searchValue);
    },
    loadFormDefinition(formUuid) {
      const _this = this;
      $axios
        .post(`/proxy/api/dyform/definition/getFormDefinitionByUuid?formUuid=${formUuid}`, {})
        .then(({ data: result }) => {
          if (result.definitionJson) {
            let formDefinition = JSON.parse(result.definitionJson);
            let fields = formDefinition.fields || {};
            let fieldOptions = [];
            // 字段
            for (let fieldName in fields) {
              let field = fields[fieldName];
              fieldOptions.push({ value: field.name, label: field.displayName });
            }
            _this.formFieldOptions = fieldOptions;
            _this.titleContent = formDefinition.titleContent || '${表单标题}';
          }
        })
        .catch(error => {});
    }
  }
};
</script>

<style></style>
