<template>
  <div>
    <a-form-model-item label="内容来源">
      <a-select :options="srcOptions" :style="{ width: '100%' }" v-model="options.templateFrom"></a-select>
    </a-form-model-item>

    <div v-if="options.templateFrom === 'projectCode'">
      <a-form-model-item label="项目代码">
        <a-select
          :options="vueTemplateOptions"
          :style="{ width: '100%' }"
          v-model="options.templateName"
          :filter-option="filterOption"
          :showSearch="true"
          allowClear
        ></a-select>
      </a-form-model-item>
    </div>
    <a-form-model-item label="模板内容" v-else>
      <WidgetCodeEditor v-model="options.template" lang="html" width="auto" height="120px" :hideError="true"></WidgetCodeEditor>
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'TemplateSelect',
  mixins: [],
  props: {
    options: Object
  },
  data() {
    return {
      vueTemplateOptions: [],
      srcOptions: [
        { label: '代码编辑器', value: 'codeEditor' },
        { label: '项目代码', value: 'projectCode' }
      ]
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    if (this.options.templateFrom == undefined) {
      this.$set(this.options, 'templateFrom', 'projectCode');
    }
  },
  methods: {
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    }
  },
  beforeMount() {
    // 获取vue模板实例
    let regExp = /wellapp[\w|-]+\/app\/web\/template.+\.vue$/;
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
  mounted() {}
};
</script>
