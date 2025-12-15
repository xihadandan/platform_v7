<template>
  <div>
    <a-form-model-item label="枚举类">
      <a-select
        :options="enumOptions"
        show-search
        :filter-option="filterOption"
        :style="{ width: '100%' }"
        v-model="options.enumClass"
        @change="onChangeEnumSelect"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="值字段">
      <a-select :options="fieldOptions" :style="{ width: '100%' }" v-model="options.valueField"></a-select>
    </a-form-model-item>
    <a-form-model-item label="展示字段">
      <a-select :options="fieldOptions" :style="{ width: '100%' }" v-model="options.textField"></a-select>
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'enumClassRendererConfig',
  mixins: [],
  props: {
    options: Object
  },
  data() {
    return {
      enumOptions: [],
      fieldOptions: []
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    onChangeEnumSelect() {
      this.fieldOptions.splice(0, this.fieldOptions.length);
      let opts = {
        serviceName: 'enumClassSelect2QueryService',
        queryMethod: 'loadEnumClassFieldsSelectData',
        pageSize: 100,
        pageNo: 1
      };
      if (this.options.enumClass) {
        opts.enumClass = this.options.enumClass;
        this.fetchSelectOptions(opts, this.fieldOptions);
      }
    },
    filterOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
      );
    },
    fetchSelectOptions(param, options) {
      $axios
        .get(`/proxy/common/select2/query`, { params: param })
        .then(({ data }) => {
          options.splice(0, options.length);
          if (data.results) {
            for (let r of data.results) {
              options.push({
                label: r.text,
                value: r.id
              });
            }
          }
        })
        .catch(error => {});
    }
  },
  mounted() {
    this.fetchSelectOptions(
      {
        serviceName: 'enumClassSelect2QueryService',
        queryMethod: 'queryAll4SelectOptions'
      },
      this.enumOptions
    );
    if (this.options.enumClass) {
      this.fetchSelectOptions(
        {
          serviceName: 'enumClassSelect2QueryService',
          queryMethod: 'loadEnumClassFieldsSelectData',
          pageSize: 100,
          pageNo: 1,
          enumClass: this.options.enumClass
        },
        this.fieldOptions
      );
    }
  }
};
</script>
