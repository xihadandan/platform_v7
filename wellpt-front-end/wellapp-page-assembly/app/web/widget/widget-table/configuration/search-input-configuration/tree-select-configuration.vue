<template>
  <div>
    <OptionSourceConfigration
      :widget="widget"
      :designer="designer"
      :options="options"
      :column="column"
      :allowOptionSrc="['dataSource']"
      :data-source-field="[
        {
          label: '展示字段',
          value: 'dataSourceLabelColumn'
        },
        {
          label: '值字段',
          value: 'dataSourceValueColumn'
        },
        {
          label: '唯一值字段',
          value: 'dataSourceKeyColumn'
        },
        {
          label: '父级字段',
          value: 'dataSourceParentColumn'
        }
      ]"
    ></OptionSourceConfigration>

    <a-form-model-item label="启用多选">
      <a-switch v-model="options.multiSelect" />
    </a-form-model-item>

    <a-form-model-item label="启用搜索">
      <a-switch v-model="options.searchable" />
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
import OptionSourceConfigration from './option-source-configuration.vue';
export default {
  name: 'treeSelectSearchConfig',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    options: Object,
    column: Object
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
