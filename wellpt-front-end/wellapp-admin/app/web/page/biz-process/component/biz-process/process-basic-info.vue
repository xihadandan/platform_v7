<template>
  <a-form-model
    class="basic-info"
    :model="processDefinition"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 7 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
    :colon="false"
  >
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="processDefinition.name" />
    </a-form-model-item>
    <a-form-model-item label="ID" prop="id">
      <a-input v-model="processDefinition.id" :readOnly="processDefinition.uuid != null" />
    </a-form-model-item>
    <a-form-model-item label="编号" prop="code">
      <a-input v-model="processDefinition.code" />
    </a-form-model-item>
    <a-form-model-item label="状态" prop="enabled">
      <a-switch v-model="processDefinition.enabled" checked-children="启用" un-checked-children="禁用" />
    </a-form-model-item>
    <a-form-model-item prop="businessId">
      <template slot="label">
        <a-space>
          所属业务
          <a-popover>
            <template slot="content">
              用于关联相同业务的业务事项定义，关联后业务
              <br />
              事项定义表单的数据才能在当前业务流程定义中引用
            </template>
            <a-icon type="info-circle" />
          </a-popover>
        </a-space>
      </template>
      <a-tree-select
        v-model="processDefinition.businessId"
        style="width: 100%"
        :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
        :tree-data="businessTreeData"
        placeholder="请选择"
        tree-default-expand-all
      ></a-tree-select>
    </a-form-model-item>
    <a-form-model-item label="事件监听" prop="listener">
      <a-select v-model="processDefinition.listener" mode="multiple" show-search style="width: 100%" :filter-option="filterSelectOption">
        <a-select-option v-for="d in listenerOptions" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="业务标签" prop="tagId">
      <a-select v-model="processDefinition.tagId" show-search style="width: 100%" :filter-option="filterSelectOption">
        <a-select-option v-for="d in bizTagOptions" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="备注" prop="remark">
      <a-textarea v-model="processDefinition.remark" />
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import ProcessBasicInfo from '../../../process-designer/component/process-configuration/process-basic-info.vue';
export default {
  extends: ProcessBasicInfo
};
</script>

<style></style>
