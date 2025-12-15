<template>
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    :colon="false"
    labelAlign="left"
    :label-col="{ span: 6 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
  >
    <a-tabs v-model="tabKey" size="small" class="flex-card-tabs flow-flex-card-tabs">
      <a-tab-pane key="0" tab="子流程">
        <node-subflow-base-info :graphItem="graphItem" :formData="formData" :rules="rules" />
      </a-tab-pane>
      <a-tab-pane key="1" tab="办理信息">
        <subflow-info :formData="formData" />
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
import { subflowRules as rules, getCustomRules } from '../designer/constant';
import NodeSubflowBaseInfo from './base-info.vue';
import SubflowInfo from './sub-flow-info.vue';

export default {
  name: 'NodeSubflowConfiguration',
  props: {
    formData: {
      type: Object,
      default: () => {}
    },
    graphItem: {
      type: Object,
      default: () => {}
    }
  },
  provide() {
    return {
      subflowData: this.formData
    };
  },
  components: {
    NodeSubflowBaseInfo,
    SubflowInfo
  },
  data() {
    const rulesCustom = getCustomRules(rules);
    return {
      tabKey: '0',
      rules: rulesCustom
    };
  },
  mounted() {
    this.$emit('mounted', this);
  },
  methods: {
    validate(callback) {
      this.$refs.form.validate((valid, error) => {
        callback({ valid, error, data: this.formData });
      });
    }
  }
};
</script>
