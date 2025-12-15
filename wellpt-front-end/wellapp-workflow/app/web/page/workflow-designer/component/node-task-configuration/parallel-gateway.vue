<template>
  <!-- 环节属性-流转设置 -->
  <div>
    <a-form-model-item class="form-item-vertical" label="分支模式">
      <a-radio-group v-model="parallelGateway.forkMode" size="small">
        <a-radio v-for="item in forkModeConfig" :key="item.value" :value="item.value">
          {{ item.label }}
        </a-radio>
      </a-radio-group>
      <template v-if="parallelGateway.forkMode === 2">
        <w-checkbox v-model="parallelGateway.chooseForkingDirection" unCheckedValue="">提交时可选择流向</w-checkbox>
      </template>
    </a-form-model-item>
    <template v-if="parallelGateway.forkMode === 2">
      <a-form-model-item class="form-item-vertical" prop="businessType" label="分发组织">
        <use-org-tree-select v-model="parallelGateway.businessType" />
      </a-form-model-item>
      <branch-mode :formData="parallelGateway" />
    </template>
    <a-form-model-item class="form-item-vertical" label="聚合模式">
      <a-radio-group v-model="parallelGateway.joinMode" size="small">
        <a-radio v-for="item in joinModeConfig" :key="item.value" :value="item.value">
          {{ item.label }}
        </a-radio>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="退回设置">
      <w-checkbox v-model="formData.notRollback">本环节不可被退回</w-checkbox>
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="撤回设置">
      <w-checkbox v-model="formData.notCancel">本环节不可被撤回</w-checkbox>
    </a-form-model-item>
    <a-form-model-item label="多身份流转设置">
      <a-switch v-model="formData.enabledJobFlowType" />
    </a-form-model-item>
    <multi-job-flow v-if="formData.enabledJobFlowType" :formData="formData" displayType="task" />
  </div>
</template>

<script>
import { forkModeConfig, joinModeConfig } from '../designer/constant';
import WCheckbox from '../components/w-checkbox';
import UseOrgTreeSelect from '../commons/use-org-tree-select.vue';
import BranchMode from './branch-mode.vue';
import MultiJobFlow from '../commons/multi-job-flow.vue';

export default {
  name: 'TaskPropertyParallelGateway',
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    const { parallelGateway } = this.formData;
    return {
      parallelGateway,
      forkModeConfig,
      ruleOptions: [],
      joinModeConfig
    };
  },
  components: {
    WCheckbox,
    UseOrgTreeSelect,
    BranchMode,
    MultiJobFlow
  }
};
</script>
