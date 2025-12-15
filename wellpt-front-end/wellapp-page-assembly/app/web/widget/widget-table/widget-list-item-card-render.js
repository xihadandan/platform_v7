
export default {
  name: 'WidgetListItemCardRender',
  inject: ['pageContext'],
  mixins: [],
  props: {
    row: Object,
    rowIndex: Number,
    invokeJsFunction: Function,
    invokeDevelopmentMethod: Function,
    cardTemplateConfig: Object,
    eventWidget: Object | Function
  },
  data() {
    return {};
  },
  computed: {
    component() {
      if (this.cardTemplateConfig.templateFrom == 'projectCode' && this.cardTemplateConfig.templateName) {
        return this.cardTemplateConfig.templateName;
      }
      if (this.cardTemplateConfig.template) {
        let _this = this;
        return {
          template: `<div>${this.cardTemplateConfig.template}</div>`, //
          props: ['row', 'pageContext', 'rowIndex', 'invokeJsFunction', 'invokeDevelopmentMethod'],
          data: function () {
            return {};
          },
          render: undefined,
          methods: {
            emitEvent() {
              _this.emitEvent.apply(_this, Array.from(arguments));
            }
          }
        };
      }

      return 'div'

    },
  },

  render(h) {
    let propsData = {
      row: this.row,
      rowIndex: this.rowIndex,
      invokeJsFunction: this.invokeJsFunction,
      invokeDevelopmentMethod: this.invokeDevelopmentMethod,
      eventWidget: this.eventWidget
    }

    const { component } = this;

    if (typeof component === 'string') {
      let scopedSlots = {};
      for (let i in this.$slots) {
        scopedSlots[i] = () => {
          return this.$slots[i];
        }
      }
      return h(
        component,
        {
          props: propsData,// 绑定属性
          scopedSlots,
          ref: 'itemCardTemRef'
        },
      );
    } else {
      this.$options.components.component = component;
      const entries = Object.entries(this.$slots);
      return < component {...{
        props: propsData
      }}  >
        {entries.map(([key, value]) => (
          <template slot={key} >
            {value}
          </template>
        ))}
      </component>
    }

  },

  methods: {

    emitEvent() {
      this.pageContext.emitEvent.apply(this.pageContext, Array.from(arguments));
    },

  },

  beforeUpdate() { },


};
