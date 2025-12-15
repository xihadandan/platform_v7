<template>
  <div>
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

    <a-form-model-item>
      <template slot="label">
        <a-popover title="提示" placement="left" :mouseEnterDelay="0.5">
          <template slot="content">
            <ol>
              <li>有无选项或两个可选项时生效</li>
              <li>有两个可选项时，选中值为可选项的第一个值，未选中值为可选项的第二个值</li>
              <li>无选项时，选中值为true，未选中值为false</li>
            </ol>
          </template>
          <label>
            开关选择
            <a-icon type="info-circle" style="line-height: inherit; padding-top: 2px" />
          </label>
        </a-popover>
      </template>
      <a-switch v-model="options.switchStyle" />
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
import OptionSourceConfigration from './option-source-configuration.vue';

export default {
  name: 'radioSearchConfig',
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
      return { defineOptions: [], dataSourceLabelColumn: '', dataSourceValueColumn: '', switchStyle: false };
    }
  },
  mounted() {}
};
</script>
