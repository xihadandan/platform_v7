import { utils } from "wellapp-uni-framework";
import { compile } from "vue-template-compiler";
import Vue from "vue";

export default {
  inject: ["widgetListContext"],
  props: {
    name: String,
    row: Object,
    text: [String, Number, Object],
    rowIndex: Number,
    slotOption: Object,
    invokeJsFunction: Function,
    invokeDevelopmentMethod: Function,
  },
  data() {
    return {
      templateEngine: null,
    };
  },
  methods: {
    $t() {
      if (this.widgetListContext != undefined) {
        return this.widgetListContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
    compileTemplate(templateString, templateName) {
      try {
        const { render, staticRenderFns } = compile(templateString);

        const DynamicComponent = Vue.extend({
          name: `Dynamic${templateName}`,
          props: ["row", "rowIndex", "text", "slotOption"],
          methods: {},
          render: new Function(render),
          staticRenderFns,
        });

        return DynamicComponent;
      } catch (error) {
        console.error(`编译模板 ${templateName} 失败:`, error);
        return null;
      }
    },
  },
  computed: {
    isComponent() {
      if (
        this.name == "vueTemplateDataRender" &&
        this.slotOption.options.templateFrom == "projectCode" &&
        this.slotOption.options.templateName
      ) {
        return this.slotOption.options.templateName;
      }
      return undefined;
    },
    renderedContent() {
      if (
        this.name == "vueTemplateDataRender" &&
        this.slotOption.options.templateFrom == "codeEditor" &&
        this.slotOption.options.template
      ) {
        return this.compileTemplate(this.slotOption.options.template, "dynamicComponent");
      }
      return undefined;
    },
  },
};
