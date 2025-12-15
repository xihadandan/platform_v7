<template>
  <OptionSourceConfigration
    :widget="widget"
    :designer="designer"
    :options="options"
    :column="column"
    :data-source-field="[
      {
        label: '展示字段',
        value: 'dataSourceLabelColumn'
      },
      {
        label: '值字段',
        value: 'dataSourceValueColumn'
      }
    ]"
  ></OptionSourceConfigration>
</template>
<style></style>
<script type="text/babel">
import OptionSourceConfigration from './option-source-configuration.vue';

export default {
  name: 'checkboxSearchConfig',
  mixins: [],
  props: {
    options: Object,
    column: Object,
    widget: Object,
    designer: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: { OptionSourceConfigration },
  computed: {},
  created() {},
  methods: {
    initOptions(options) {
      //初始化参数
      return { defineOptions: [], dataSourceLabelColumn: '', dataSourceValueColumn: '' };
    }
  },
  mounted() {}
};
</script>
