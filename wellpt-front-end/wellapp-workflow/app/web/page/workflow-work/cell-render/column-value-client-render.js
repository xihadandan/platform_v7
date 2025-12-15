import TableRenderMixin from './table.renderMixin';
import { merge } from 'lodash';
export default {
  name: 'ColumnValueClientRender',
  inject: ['pageContext'],
  mixins: [TableRenderMixin],
  props: {
    row: Object,
    rowIndex: Number,
    text: [String, Number, Boolean],
    slotOption: Object,
    invokeJsFunction: Function,
    invokeDevelopmentMethod: Function
  },
  data() {
    return {
    };
  },
  computed: {
    component() {
      if (this.slotOption.renderType == 'vueTemplateDataRender') {
        if (this.slotOption.options.templateFrom == 'projectCode' && this.slotOption.options.templateName) {
          return this.slotOption.options.templateName;
        }
        if (this.slotOption.options.template) {
          let _this = this;
          let messages = this.$i18n.messages[this.$i18n.locale] ?
            JSON.parse(JSON.stringify(this.$i18n.messages[this.$i18n.locale])) : {};
          merge(messages, this.slotOption.options.i18n ? this.slotOption.options.i18n[this.$i18n.locale] || {} : {});
          return {
            template: `<div>${this.slotOption.options.template}</div>`, //
            props: ['row', 'text', 'value', 'pageContext', 'rowIndex', 'slotOption', 'invokeJsFunction', 'invokeDevelopmentMethod'],
            i18n: { locale: this.$i18n.locale, messages: { [this.$i18n.locale]: messages } },
            data: function () {
              return {
              };
            },
            render: undefined,
            methods: {
              emitEvent() {
                _this.emitEvent.apply(_this, Array.from(arguments));
              }
            }
          };
        }
      }

      return null;
    },
  },

  render(h) {
    let propsData = {
      row: this.row,
      text: this.text,
      value: this.text,
      slotOption: this.slotOption,
      rowIndex: this.rowIndex,
      invokeJsFunction: this.invokeJsFunction,
      invokeDevelopmentMethod: this.invokeDevelopmentMethod
    }

    // 拓展字段，当前数据不在列值来源内，直接用空内容渲染
    if (this.slotOption.columnType == '2' && this.slotOption.configuration) {
      let sources = this.slotOption.configuration.sources.split(/[;,]/);
      if (sources.indexOf(this.row.flowDefId) == -1) {
        if (this.slotOption.defaultContentIfNull) {
          let html = this.slotOption.defaultContentIfNull;
          return <div domPropsInnerHTML={html} class="cell-render"></div>;
        }
        return null;
      }
    }


    if (this.isCellRender(this.slotOption.renderType)) {
      return h(
        this.slotOption.renderType,
        {
          attrs: {
          },
          props: propsData // 绑定属性
        }
      );
    }

    // 空内容缺省显示内容
    if (this.slotOption.defaultContentIfNull && (this.text == undefined || this.text == null || this.text.trim && this.text.trim() == '')) {
      let html = this.slotOption.defaultContentIfNull;
      return <div domPropsInnerHTML={html} class="cell-render"></div>;
    }

    const { component } = this;

    if (component != null) {
      if (typeof component === 'string') {
        return h(
          component,
          {
            props: propsData // 绑定属性
          }
        );
      } else {
        this.$options.components.component = component;
        return < component {...{
          props: propsData
        }} />;

      }
    }

    let vhtml = this.invokeRenderMethod(this.slotOption.renderType, this.text, this.row, this.slotOption.options, this.slotOption);
    if (this.$parent && this.$parent.$el && vhtml) {
      this.$parent.$el.setAttribute('title', this.getPlainText(vhtml));
    }
    return <div domPropsInnerHTML={vhtml} class="cell-render"></div>;
  },

  methods: {
    getPlainText(htmlString) {
      const tempDiv = document.createElement('div');
      tempDiv.innerHTML = htmlString;
      return tempDiv.textContent || tempDiv.innerText || "";
    },
    emitEvent() {
      this.pageContext.emitEvent.apply(this.pageContext, Array.from(arguments));
    },

  },

  beforeUpdate() { },


};
