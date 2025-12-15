<template>
  <div>
    <a-form-model-item class="form-item-vertical">
      <template slot="label">
        <template v-if="displayType === 'flow'">
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">设置流程发起人和办理人在多个身份时的流转方式，如果环节属性中已设置，以具体环节设置优先</div>
            <label>
              发起人或办理人有多个身份时
              <a-icon type="exclamation-circle" />
            </label>
          </a-tooltip>
        </template>
        <template v-else>
          {{ isStartTask ? '发起人有多个身份时' : '办理人有多个身份时' }}
        </template>
      </template>
      <w-select v-model="formData.multiJobFlowType" :options="multiJobFlowTypeConfig" @change="changeMultiJobFlowType" />
    </a-form-model-item>
    <template v-if="formData.multiJobFlowType === multiJobFlowTypeConfig[1]['id']">
      <a-form-model-item label="获取不到主身份时">
        <a-radio-group v-model="formData.mainJobNotFoundFlowType" size="small" button-style="solid">
          <a-radio-button v-for="item in notFoundMainIdentityOptions" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="身份选择" v-if="formData.mainJobNotFoundFlowType === notFoundMainIdentityOptions[0]['value']">
        <a-radio-group v-model="formData.selectJobMode" size="small" button-style="solid">
          <a-radio-button v-for="item in identitySelectOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
    </template>
    <template v-if="formData.multiJobFlowType === multiJobFlowTypeConfig[2]['id']">
      <template v-if="isStartTask || displayType === 'flow'">
        <a-form-model-item
          label="发起流程时通过表单字段选择身份"
          :label-col="{ span: 13 }"
          :wrapper-col="{ span: 10, style: { textAlign: 'right' } }"
        >
          <a-switch v-model="formData.selectJobField" />
        </a-form-model-item>
      </template>
      <a-form-model-item label="身份选择字段" v-if="(isStartTask || displayType === 'flow') && formData.selectJobField">
        <w-select v-model="formData.jobField" :options="jobFieldOptions" />
      </a-form-model-item>
      <a-form-model-item label="身份选择" v-else>
        <a-radio-group v-model="formData.selectJobMode" size="small" button-style="solid">
          <a-radio-button v-for="item in identitySelectOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
    </template>
    <!-- <template v-if="formData.multiJobFlowType === multiJobFlowTypeConfig[2]['id']">
      <a-form-model-item class="form-item-vertical" label="职位字段">
        <w-select v-model="formData.jobField" :options="jobFieldOptions" />
      </a-form-model-item>
    </template> -->
  </div>
</template>

<script>
import { fetchFormFieldSelections } from '../api/index';
import { multiJobFlowTypeConfig, notFoundMainIdentityOptions, identitySelectOptions } from '../designer/constant';
import WSelect from '../components/w-select';
import mixins from '../mixins';

export default {
  name: 'MultiJobFlow',
  inject: ['designer'],
  mixins: [mixins],
  props: {
    formData: {
      type: Object,
      default: () => {}
    },
    displayType: {
      type: String,
      default: 'flow',
      validator: function (value) {
        return ['flow', 'task'].includes(value);
      }
    }
  },
  components: {
    WSelect
  },
  data() {
    return {
      multiJobFlowTypeConfig,
      notFoundMainIdentityOptions,
      identitySelectOptions,
      jobFieldOptions: []
    };
  },
  watch: {
    'designer.formDefinition': {
      deep: true,
      immediate: true,
      handler(formDefinition) {
        this.getFormFieldSelections(formDefinition);
      }
    }
  },
  methods: {
    getFormFieldSelections(formDefinition) {
      if (!formDefinition) {
        this.jobFieldOptions = [];
        return;
      }
      fetchFormFieldSelections({
        formUuid: formDefinition.uuid
      }).then(res => {
        this.jobFieldOptions = res;
      });
    },
    changeMultiJobFlowType(value) {
      if (value === this.multiJobFlowTypeConfig[2]['id']) {
      }
    }
  }
};
</script>
