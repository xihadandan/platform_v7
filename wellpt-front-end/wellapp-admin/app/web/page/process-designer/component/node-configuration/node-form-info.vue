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
    <a-form-model-item label="阶段办理过程显示位置" prop="itemPlaceHolder">
      <a-select
        v-model="formConfig.itemPlaceHolder"
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
import ProcessFormInfo from '../process-configuration/process-form-info.vue';
import StateConfiguration from '../state-configuration';
import DyformSettingConfiguration from '../dyform-setting-configuration/node-dyform-setting-configuration.vue';
export default {
  name: 'NodeFormInfo',
  extends: ProcessFormInfo,
  components: { StateConfiguration, DyformSettingConfiguration }
};
</script>
