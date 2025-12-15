<template>
  <a-radio-group v-model="value" class="wf-next-select-group">
    <a-radio v-for="(type, index) in typeOptions" :style="radioStyle" :value="type.id" :key="index">
      {{ type.text }}
    </a-radio>
  </a-radio-group>
</template>

<script>
import { isEmpty } from 'lodash';

export default {
  props: {
    values: Array,
    evtWidget: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    let typeOptions = [
      {
        id: '1',
        text: this.evtWidget.$t('WorkflowWork.sourceApproval') || '源文送审批'
      },
      {
        id: '2',
        text: this.evtWidget.$t('WorkflowWork.copySourceApproval') || '复制源文送审批'
      },
      {
        id: '3',
        text: this.evtWidget.$t('WorkflowWork.textLinkApproval') || '原文作为链接送审批'
      }
    ];
    if (this.values && this.values.length) {
      typeOptions = typeOptions.filter(type => this.values.includes(type.id));
    }
    return {
      value: '',
      typeOptions
    };
  },
  methods: {
    collectContent() {
      if (isEmpty(this.value)) {
        return Promise.reject({ type: this.value });
      } else {
        return Promise.resolve({ type: this.value });
      }
    }
  }
};
</script>

<style></style>
