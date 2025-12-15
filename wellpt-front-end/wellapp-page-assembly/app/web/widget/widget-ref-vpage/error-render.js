export default {
  name: 'ErrorRender',
  inject: ['pageContext'],
  mixins: [],
  props: {
    templateConfig: Object,
    widget: Object
  },
  data() {
    return {};
  },
  computed: {
    component() {
      if (this.templateConfig.sourceType == 'projectCode' && this.templateConfig.templateName) {
        return this.templateConfig.templateName;
      }
      if (this.templateConfig.template) {
        let _this = this;
        return {
          template: `<div>${this.templateConfig.template}</div>`, //
          props: ['pageContext', 'invokeJsFunction', 'invokeDevelopmentMethod'],
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

      return 'div';
    }
  },

  render(h) {
    let propsData = {
      invokeJsFunction: this.invokeJsFunction,
      invokeDevelopmentMethod: this.invokeDevelopmentMethod,
      widget: this.widget
    };

    const { component } = this;

    if (typeof component === 'string') {
      let scopedSlots = {};
      for (let i in this.$slots) {
        scopedSlots[i] = () => {
          return this.$slots[i];
        };
      }
      return h(component, {
        props: propsData, // 绑定属性
        scopedSlots,
        ref: 'errorPageRef'
      });
    } else {
      this.$options.components.component = component;
      const entries = Object.entries(this.$slots);
      return (
        <component
          {...{
            props: propsData
          }}
        >
          {entries.map(([key, value]) => (
            <template slot={key}>{value}</template>
          ))}
        </component>
      );
    }
  },

  methods: {
    emitEvent() {
      this.pageContext.emitEvent.apply(this.pageContext, Array.from(arguments));
    }
  },

  beforeUpdate() {}
};
