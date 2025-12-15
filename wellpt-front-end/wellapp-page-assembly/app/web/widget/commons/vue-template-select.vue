<template>
  <a-select
    :options="vueTemplateOptions"
    :style="{ width: '100%' }"
    v-model="selectedValue"
    :filter-option="filterSelectOption"
    :showSearch="true"
    @change="onChange"
    allowClear
  ></a-select>
</template>
<style lang="less"></style>
<script type="text/babel">
import { filterSelectOption } from '@framework/vue/utils/function';
export default {
  name: 'VueTemplateSelect',
  props: {
    value: String
  },
  components: {},
  computed: {},
  data() {
    return { vueTemplateOptions: [], selectedValue: this.value };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    // 获取vue模板实例
    let regExp = /wellapp[\w|-]+\/app\/web\/template\/.+\.vue$/;
    for (let key in window.Vue.options.components) {
      let comp = window.Vue.options.components[key];
      let META = comp.META;
      if (META) {
        this.vueTemplateOptions.push({
          label: META.fileName.replace('./', ''),
          value: key
        });
        continue;
      } else if (comp.options && comp.options.__file && regExp.test(comp.options.__file)) {
        this.vueTemplateOptions.push({
          label: comp.options.__file.substr(comp.options.__file.lastIndexOf('/') + 1),
          value: key
        });
      }
    }
  },
  mounted() {},
  methods: {
    filterSelectOption,
    onChange(value, option) {
      this.$emit('input', value);
    }
  }
};
</script>
