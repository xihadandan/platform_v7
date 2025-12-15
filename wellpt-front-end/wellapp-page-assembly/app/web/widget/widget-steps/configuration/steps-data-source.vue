<template>
  <div>
    <a-form-model-item label="数据来源">
      <a-radio-group size="small" v-model="options.type" @change="e => changeSource(e.target.value)">
        <a-radio-button :value="item.value" v-for="(item, i) in sourceTypes" :key="i">
          {{ item.label }}
        </a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <steps-list v-show="options.type === 'selfDefine'" :dataSource="configuration.steps" />
    <step-data-store v-show="options.type === 'dataStore'" :options="options" />
    <step-data-model v-show="options.type === 'dataModel'" :formData="configuration.dataModel" />
  </div>
</template>

<script>
import StepsList from './steps-list.vue';
import StepDataStore from './step-data-store.vue';
import StepDataModel from './step-data-model.vue';
import { sourceTypes } from './constant';
export default {
  name: 'StepsDataSource',
  props: {
    configuration: Object,
    options: Object
  },
  components: {
    StepsList,
    StepDataStore,
    StepDataModel
  },
  data() {
    if (!this.configuration.hasOwnProperty('dataModel')) {
      this.$set(this.configuration, 'dataModel', {});
    }
    return {
      sourceTypes
    };
  },
  methods: {
    changeSource() {}
  }
};
</script>
