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
    <a-form-model-item label="事项名称字段" prop="itemNameField">
      <a-select
        v-model="formConfig.itemNameField"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="formFieldOptions"
        :disabled="selectDisabled"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="事项编码字段" prop="itemCodeField">
      <a-select
        v-model="formConfig.itemCodeField"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="formFieldOptions"
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
    <a-form-model-item label="时限字段" prop="timeLimitField">
      <a-select
        v-model="formConfig.timeLimitField"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="formFieldOptions"
        :disabled="selectDisabled"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="其他字段回填">
      <ItemFormFieldMapping
        :itemDefinition="itemDefinition"
        :formConfig="formConfig"
        :disabled="selectDisabled"
        :closeOpenDrawer="false"
      ></ItemFormFieldMapping>
    </a-form-model-item>
    <a-form-model-item label="材料从表" prop="materialSubformId">
      <a-select
        v-model="formConfig.materialSubformId"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="subformOptions"
        @change="handleSubformChange"
        :disabled="selectDisabled"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="材料名称字段" prop="materialNameField">
      <a-select
        v-model="formConfig.materialNameField"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="subformFieldOptions"
        :disabled="selectDisabled"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="材料编码字段" prop="materialCodeField">
      <a-select
        v-model="formConfig.materialCodeField"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="subformFieldOptions"
        :disabled="selectDisabled"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="材料是否必填字段" prop="materialRequiredField">
      <a-select
        v-model="formConfig.materialRequiredField"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="subformFieldOptions"
        :disabled="selectDisabled"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="材料附件字段" prop="materialFileField">
      <a-select
        v-model="formConfig.materialFileField"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="subformFieldOptions"
        :disabled="selectDisabled"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="材料其他字段回填">
      <ItemFormMaterialFieldMapping
        :itemDefinition="itemDefinition"
        :formConfig="formConfig"
        :disabled="selectDisabled"
        :closeOpenDrawer="false"
      ></ItemFormMaterialFieldMapping>
    </a-form-model-item>
    <a-form-model-item v-show="itemDefinition.itemType == '20'" label="包含事项办理显示位置" prop="includeItemPlaceHolder">
      <a-select
        v-model="formConfig.includeItemPlaceHolder"
        allow-clear
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        :options="formBlockOptions"
        :disabled="selectDisabled"
      ></a-select>
    </a-form-model-item>
    <slot :formBlockOptions="formBlockOptions" />
    <a-form-model-item label="状态管理">
      <StateConfiguration :definition="formConfig" :disabled="selectDisabled" :closeOpenDrawer="false"></StateConfiguration>
    </a-form-model-item>
    <DyformSettingConfiguration :definition="formConfig" :disabled="selectDisabled" :closeOpenDrawer="false"></DyformSettingConfiguration>
  </div>
</template>

<script>
import ProcessFormInfo from '../process-configuration/process-form-info.vue';
import StateConfiguration from '../state-configuration';
import DyformSettingConfiguration from '../dyform-setting-configuration/item-dyform-setting-configuration.vue';
import ItemFormFieldMapping from './item-form-field-mapping.vue';
import ItemFormMaterialFieldMapping from './item-form-material-field-mapping.vue';
export default {
  name: 'ItemFormInfo',
  extends: ProcessFormInfo,
  components: { StateConfiguration, DyformSettingConfiguration, ItemFormFieldMapping, ItemFormMaterialFieldMapping },
  props: {
    itemDefinition: Object
  }
};
</script>
