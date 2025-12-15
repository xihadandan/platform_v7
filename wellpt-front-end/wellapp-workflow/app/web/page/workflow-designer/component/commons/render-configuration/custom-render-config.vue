<template>
  <div>
    <a-form-model-item label="选择渲染器">
      <a-select
        :options="customServerRenderOptions"
        show-search
        :filter-option="filterOption"
        :style="{ width: '100%' }"
        v-model="options.customType"
        @change="onChangeEnumSelect"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="扩展参数">
      <a-textarea v-model="options.params" :auto-size="{ minRows: 6, maxRows: 10 }" />
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'customRenderConfig',
  mixins: [],
  props: {
    options: Object
  },
  data() {
    return {
      customServerRenderOptions: []
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
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
        serviceName: 'viewComponentService',
        queryMethod: 'loadRendererSelectData'
      },
      this.customServerRenderOptions
    );
  }
};
</script>
