import widgetMixin from '@framework/vue/mixin/widgetMixin';
import '@modules/.webpack.runtime.wtemplate.js';

export default {
  name: 'WidgetTemplate',
  mixins: [widgetMixin],
  components: {},
  inject: ['locale'],

  data() {
    return {
      loading: this.widget.configuration.type === 'html' && this.widget.configuration.sourceType === 'projectCode',
      description: this.designMode ? '暂无模板' : undefined,
      pageNamespace: this.pageNamespace,
      vHtml:
        this.widget.configuration.type === 'html' && this.widget.configuration.sourceType === 'codeEditor'
          ? this.widget.configuration.templateContent
          : '' // 等待请求获取
    };
  },
  computed: {
    componentName() {
      if (
        // 设计模式下，无代码或者无模板，展示为空样式
        this.designMode &&
        ((this.widget.configuration.sourceType === 'projectCode' && !this.widget.configuration.templateName) ||
          (this.widget.configuration.sourceType === 'codeEditor' && !this.widget.configuration.templateContent))
      ) {
        return 'AEmpty';
      }

      if (this.widget.configuration.type === 'vue' && this.widget.configuration.sourceType === 'projectCode') {
        return this.widget.configuration.templateName || 'span';
      }

      return 'span';
    },

    component() {
      if (
        this.widget.configuration.sourceType === 'codeEditor' &&
        this.widget.configuration.type === 'vue' &&
        this.widget.configuration.templateContent
      ) {
        let _this = this;
        let props = ['widget', 'parent', 'locale'].concat(Object.keys(this.$attrs));

        let templateMethods = {},
          templateData = this.widget.configuration.templateData ? JSON.parse(this.widget.configuration.templateData) : {};

        if (this.widget.configuration.templateMethods != undefined) {
          for (let method of this.widget.configuration.templateMethods) {
            if (method.name && method.content) {
              // templateMethods[method.name] = function () {
              //   let func = new Function(method.content);
              //   func.apply(_this, Array.from(arguments));
              // }
              templateMethods[method.name] = new Function(method.content);
            }
          }
        }

        return {
          template: `<div style="${this.widget.useScope == 'bigScreen' ? 'width:100%;height:100%;' : ''}">${
            this.widget.configuration.templateContent
          }</div>`, //
          props,
          inject: {
            vPageState: {
              default: this.vPageState
            },
            pageContext: {
              default: this.pageContext
            }
          },

          data: function () {
            return { ...templateData }; // 模板变量
          },
          methods: {
            ...templateMethods,
            // ...getMethods,
            // 通过该方法调用二开脚本方法
            invokeDevelopmentMethod() {
              _this.invokeDevelopmentMethod.apply(_this, Array.from(arguments));
            },
            invokePageDevelopmentMethod() {
              if (_this.$pageJsInstance != undefined) {
                let args = Array.from(arguments),
                  method = args.shift(0); // 第一个参数是访问方法，移出后剩余的参数组就是方法入参
                if (typeof _this.$pageJsInstance[method] === 'function') {
                  _this.$pageJsInstance[method].apply(_this.$pageJsInstance, args);
                }
              }
            }
          }
        };
      }
      if (this.widget.configuration.type === 'html') {
        return {
          template: `<a-spin v-if="loading">
                      <a-icon slot="indicator" type="loading" style="font-size: 24px" spin />
                    </a-spin>
                    <div v-else  v-html="vHtml"></div>`,
          props: ['vHtml', 'loading']
        };
      }

      return null;
    }
  },
  methods: {},
  render(h) {
    let propsData = {
      widget: this.widget,
      parent: this.parent,
      locale: this.locale,
      vHtml: this.vHtml,
      loading: this.loading,
      ...this.$attrs
    };

    const { component } = this;
    this.$options.components.component = component;
    if (component != null) {
      return (
        <component
          ref="wTemplate"
          {...{
            props: propsData
          }}
        />
      );
    }

    return h(
      this.componentName,
      {
        ref: 'wTemplate',
        attrs: {
          'w-id': this.widget.id
        },
        style: this.widget.useScope == 'bigScreen' ? { width: '100%', height: '100%' } : {},
        props: propsData // 绑定属性
      },
      [this.componentName === 'span' ? h('span') : '']
    );
  },
  created() {
    this.wTemplate = null;
  },
  beforeMount() {
    if (this.widget.configuration.sourceType === 'projectCode' && this.widget.configuration.type === 'html') {
      // 请求html文件
      $axios.get(`/html${this.widget.configuration.templateName}`).then(({ data }) => {
        this.loading = false;
        this.vHtml = data;
      });
    }
  },

  mounted() {
    const _this = this;
    if (_this.$refs.wTemplate) {
      _this.wTemplate = _this.$refs.wTemplate;
    } else {
      const proxyRefs = new Proxy(_this.$refs, {
        set(object, prop, value) {
          if (prop == 'wTemplate') {
            object.wTemplate = value;
            _this.wTemplate = value;
            if (_this.templateWidgetMounted) {
              _this.templateWidgetMounted.apply(_this, [value]);
            }
          }
          return true;
        }
      });
      _this.$refs = proxyRefs;
    }
  },

  updated() {}
};
