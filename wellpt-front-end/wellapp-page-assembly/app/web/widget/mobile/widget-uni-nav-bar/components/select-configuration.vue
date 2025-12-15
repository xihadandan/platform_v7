<template>
  <div>
    <OptionSourceConfiguration
      :widget="widget"
      :designer="designer"
      :options="widget.configuration.options"
      :dataSourceFieldOptions="currentOptionSourceDatasourceField"
      :optionSources="optionSourceTypes"
    />
  </div>
</template>

<script>
import OptionSourceConfiguration from '../../../widget-form/configuration/widget-select/configuration/option-source-configuration.vue';

export default {
  name: 'SelectConfiguration',
  props: {
    widget: Object,
    designer: Object,
    configuration: Object
  },
  data() {
    let dataSourceFieldOptions = [
      {
        label: '值字段',
        value: 'dataSourceValueColumn'
      },
      {
        label: '展示字段',
        value: 'dataSourceLabelColumn'
      },
      {
        label: '唯一值字段',
        value: 'dataSourceKeyColumn'
      },
      {
        label: '父级字段',
        value: 'dataSourceParentColumn'
      },
      {
        label: '扩展显示内容（辅助列）',
        value: 'dataSourceExtendColumn',
        allowClear: true
      },
      {
        label: '组字段',
        value: 'groupColumn'
      }
    ];
    let optionSourceTypes = [
      { label: '常量', value: 'selfDefine' },
      { label: '数据字典', value: 'dataDictionary' },
      { label: '数据仓库', value: 'dataSource' },
      { label: '数据模型', value: 'dataModel' }
    ];
    return {
      dataSourceFieldOptions,
      optionSourceTypes
    };
  },
  components: {
    OptionSourceConfiguration
  },
  computed: {
    currentOptionSourceDatasourceField() {
      if (this.widget.configuration.type === 'select') {
        return this.dataSourceFieldOptions.slice(0, 2);
      } else if (this.widget.configuration.type === 'select-group') {
        let arr = this.dataSourceFieldOptions.slice(0, 2);
        arr.push(this.dataSourceFieldOptions[4]);
        return arr;
      } else {
        return this.dataSourceFieldOptions.slice(0, 5);
      }
    }
  },
  methods: {
    columnIndexOptionChange() {}
  }
};
</script>
