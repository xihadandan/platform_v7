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
    <a-form-model-item v-else>
      <template slot="label">
        模板内容
        <WI18nInput :widget="widget" :target="options" :designer="designer" />
      </template>
      <a-alert :description="options.type" type="info" show-icon banner>
        <span slot="message">
          支持参数:
          <template v-for="(p, i) in supportParameters">
            <span :key="i">
              <a-tooltip>
                <template slot="title">
                  {{ p.remark }}
                </template>
                <a-tag>{{ p.code }}</a-tag>
              </a-tooltip>
            </span>
          </template>
        </span>
      </a-alert>
      <!-- <a-textarea v-model="options.template" :auto-size="{ minRows: 6, maxRows: 10 }" /> -->
      <WidgetCodeEditor v-model="options.template" lang="html" width="auto" height="120px" :hideError="true"></WidgetCodeEditor>
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
import WidgetCodeEditor from '@pageWidget/commons/widget-code-editor.vue';
export default {
  name: 'vueTemplateDataRenderConfig',

  mixins: [],
  props: {
    widget: Object,
    column: Object,
    options: Object,
    designer: Object
  },
  data() {
    return {
      vueTemplateOptions: [],
      srcOptions: [
        { label: '代码编辑器', value: 'codeEditor' },
        { label: '项目代码', value: 'projectCode' }
      ],
      supportParameters: [
        {
          code: 'row',
          remark: '当前行数据'
        },
        {
          code: 'text',
          remark: '当前列值'
        }
      ]
    };
  },

  beforeCreate() {},
  components: { WidgetCodeEditor, WI18nInput },
  computed: {},
  created() {},
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
