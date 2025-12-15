<template>
  <div class="widget-vpage" :namespace="namespace" :w-id="widget != undefined ? widget.id : undefined" :style="vpageStyle">
    <Error :error-code="errorCode" v-if="errorCode && !errorRenderDisabled" />
    <div v-else :style="{ ...vHeight, ...pageStyle }" class="widget-vpage-content">
      <div class="spin-center">
        <a-spin :spinning="loading" />
      </div>

      <template v-for="(wgt, index) in items">
        <template v-if="pageAbsolute">
          <!-- 绝对定义页面，组件外套一层相对定位 -->
          <div :key="'position_' + index" v-if="wgt.forceRender !== false" :style="getWidgetPositionWrapStyle(wgt)">
            <component
              :is="resolveWidgetType(wgt)"
              :widget="wgt"
              :key="wgt.id"
              :index="index"
              :widgetsOfParent="items"
              :parent="{ id: namespace }"
            ></component>
          </div>
        </template>
        <template v-else>
          <component
            v-if="wgt.forceRender !== false"
            :is="resolveWidgetType(wgt)"
            :widget="wgt"
            :key="wgt.id"
            :index="index"
            :widgetsOfParent="items"
            :parent="{ id: namespace }"
          ></component>
        </template>
      </template>
    </div>
    <slot></slot>
  </div>
</template>
<style lang="less">
.widget-vpage {
  min-height: 100%;
  height: 100%;
}
.widget-vpage-content {
  // 直接组件的最后一个不要有下底间距，避免布局内滚动
  > .widget:last-child {
    margin-bottom: 0px !important;
  }
  border-radius: var(--w-widget-page-block-border-radius);

  > .widget-table,
  > .widget-button,
  > div > .widget-tree,
  > .widget-data-manager-view > .ant-layout,
  > .widget-data-manager-view > .widget-table {
    // 页面直接套表格，表格增加内边距
    padding: var(--w-padding-xs) var(--w-padding-md);
  }
  > .widget-anchor-container {
    // 左侧锚点，不加左边距
    > .flex-direction-row {
      > .ant-col:not(.anchor-col) {
        > .widget-table,
        > .widget-button,
        > div > .widget-tree,
        > .widget-data-manager-view > .ant-layout,
        > .widget-data-manager-view > .widget-table {
          // 页面直接套表格，表格增加内边距
          padding: var(--w-padding-xs) var(--w-padding-md) var(--w-padding-xs) 0;
        }
      }
    }
    // 右侧锚点，不加右边距
    > .flex-direction-row-reverse {
      > .ant-col:not(.anchor-col) {
        > .widget-table,
        > .widget-button,
        > div > .widget-tree,
        > .widget-data-manager-view > .ant-layout,
        > .widget-data-manager-view > .widget-table {
          // 页面直接套表格，表格增加内边距
          padding: var(--w-padding-xs) 0 var(--w-padding-xs) var(--w-padding-md);
        }
      }
    }
    > .flex-direction-column {
      > .ant-col:not(.anchor-col) {
        > .widget-table,
        > .widget-button,
        > div > .widget-tree,
        > .widget-data-manager-view > .ant-layout,
        > .widget-data-manager-view > .widget-table {
          // 页面直接套表格，表格增加内边距
          padding: var(--w-padding-xs) var(--w-padding-md);
        }
      }
    }
  }
  > .widget-refrence {
    > .widget-table,
    > .widget-button,
    > div > .widget-tree,
    > .widget-data-manager-view > .ant-layout,
    > .widget-data-manager-view > .widget-table {
      // 页面直接套表格，表格增加内边距
      padding: var(--w-padding-xs) var(--w-padding-md);
    }
  }
}
</style>
<script type="text/babel">
import extend from 'extend';
import { camelCase, set } from 'lodash';
import { createLocalforage, dropLocalforage } from '@framework/vue/utils/util';

export default {
  name: 'WidgetVpage',
  inject: ['pageContext', 'designMode', 'previewMode'],
  props: {
    errorCode: String,
    pageUuid: String,
    pageId: String,
    pageTitle: String,
    widget: Object,
    security: {
      type: Boolean,
      default: true
    },
    height: {
      type: Number | String
    },
    vpageStyle: {
      type: Object,
      default: function () {
        return {
          backgroundColor: 'var(--w-widget-page-layout-bg-color)'
        };
      }
    },
    parent: Object,
    _event: Object,
    jsModule: String,
    eventParams: Object, // 事件参数，由事件处理传递进来的
    _unauthorizedResource: Array, // 受保护的页面资源，经过后端判断得出的未授权集合
    widgetI18ns: Object,
    errorRenderDisabled: {
      type: Boolean,
      default: false
    },
    urlHashParams: Object
  },

  data() {
    let namespace = this.pageId || this.pageUuid;
    if (namespace == undefined && this.widget != undefined) {
      namespace = this.widget.id;
    }
    return {
      unauthorizedResource: this._unauthorizedResource != undefined ? [...this._unauthorizedResource] : [],
      items: [],
      loading: this.widget == undefined,
      namespace,
      pageJsModule: this.jsModule,
      pageAbsolute: false,
      pageStyle: {},
      title: this.pageTitle
    };
  },
  provide() {
    let pageProvide = {
      unauthorizedResource: this.unauthorizedResource,
      $pageJsInstance: undefined,
      vPageState: {},
      vPage: this,
      $tempStorage: createLocalforage(this.namespace, 'vPage页面级缓存'),
      namespace: this.namespace // 以页面ID作为命名空间
    };
    if (this._event != undefined) {
      pageProvide.$event = this._event;
    }
    if (this.urlHashParams != undefined) {
      pageProvide.urlHashParams = this.urlHashParams;
    }

    return pageProvide;
  },

  beforeCreate() {},
  components: {},
  computed: {
    vHeight() {
      let style = {};
      if (this.height === 'number') {
        style.height = this.height + 'px';
      } else {
        if (this.height) {
          style.height = this.height;
        }
        style.minHeight = '100%';
      }
      return style;
    }
  },
  created() {
    if (EASY_ENV_IS_BROWSER && this.pageContext == undefined && $app != undefined) {
      this._provided.pageContext = $app.pageContext;
    }
    if (EASY_ENV_IS_BROWSER && this.$store == undefined && $app != undefined) {
      this.$store = $app.$store;
    }
    if (this.errorCode == undefined) {
      this.registerPageState({});
      // 通过定义传入的，直接渲染相关配置
      if (this.widget != undefined) {
        this.renderWidget(this.widget);
        this.invokeDevelopmentMethod('created');
      }
    }
    if (this.widgetI18ns != undefined) {
      for (let locale in this.widgetI18ns) {
        this.$i18n.mergeLocaleMessage(locale, this.widgetI18ns[locale]);
      }
    }
  },
  methods: {
    getWidgetPositionWrapStyle(wgt) {
      let style = {};
      if (wgt.configuration.style != undefined && wgt.configuration.style.position != undefined) {
        let px = ['left', 'top', 'bottom', 'right', 'width', 'height'];
        style.position = wgt.configuration.style.position;
        for (let key of px) {
          if (wgt.configuration.style[key] != undefined) {
            if (typeof wgt.configuration.style[key] == 'number') {
              style[key] = `${wgt.configuration.style[key]}px`;
            } else {
              style[key] = wgt.configuration.style[key];
            }
          }
        }
        if (wgt.configuration.style.zIndex) {
          style.zIndex = wgt.configuration.style.zIndex;
        }
      }

      return style;
    },
    invokeDevelopmentMethod() {
      if (this.$pageJsInstance) {
        let args = Array.from(arguments),
          method = args.shift(0); // 第一个参数是访问方法，移出后剩余的参数组就是方法入参
        try {
          this.$pageJsInstance[method].apply(this.$pageJsInstance, args);
        } catch (error) {
          console.error('调用二开脚本方法失败：', error);
        }
      }
    },
    createPageJsInstance() {
      // 页面二开脚本实例
      if (this.pageJsModule && !EASY_ENV_IS_NODE) {
        // 服务端不需要执行二开脚本
        this.$pageJsInstance = new this.__developScript[this.pageJsModule].default(this);
        this._provided.$pageJsInstance = this.$pageJsInstance;
        this._provided.$pageJsInstance._JS_META_ = this.pageJsModule;
      }
    },
    getVueWidgetById(id) {
      return this.pageContext.getVueWidgetById(id);
    },

    // 组件按编码查询，默认仅查询同一命名空间内的
    getVueWidgetByCode(code) {
      return this.pageContext.getVueWidgetByCode(code, this.namespace);
    },

    getPageState() {
      return this.$store.state[this.namespace];
    },

    resolveWidgetType(widget) {
      return widget.wtype;
    },

    commitPageState(state) {
      this.$store.commit(`${this.namespace}/set`, state);
    },
    registerPageState(state) {
      // 以页面UUID作为命名空间，注册页面级的状态存储管理
      if (!this.$store.hasModule(this.namespace) && !EASY_ENV_IS_NODE) {
        console.log(`注册页面 [ ${this.namespace} ] 状态管理`);
        let _this = this;
        this.$store.registerModule(this.namespace, {
          state,
          namespaced: true,
          mutations: {
            // 修改状态
            set(state, payload) {
              if (typeof payload === 'function') {
                payload.call(this, state);
              } else if (typeof payload === 'object') {
                for (let k in payload) {
                  _this.$set(state, k, typeof payload === 'object' ? JSON.parse(JSON.stringify(payload[k])) : payload[k]);
                }
                extend(true, state, payload);
              }
            }
          }
        });
      }
      this._provided.vPageState = this.$store.state[this.namespace];
    },
    setStyle(style) {
      if (style) {
        let {
          enableBackground,
          backgroundColor,
          backgroundImage,
          backgroundImageInput,
          bgImageUseInput,
          backgroundPosition,
          backgroundRepeat,
          position
        } = style;
        // 启用时生效或者未设置时
        if (enableBackground === undefined || enableBackground) {
          if (backgroundColor) {
            this.pageStyle.backgroundColor = backgroundColor;
            // this.pageStyle['--w-widget-page-layout-bg-color'] = backgroundColor;
          }
          let bgImgStyle = bgImageUseInput ? backgroundImageInput : backgroundImage;
          if (bgImgStyle) {
            let isUrl =
              bgImgStyle.startsWith('data:') ||
              bgImgStyle.startsWith('http') ||
              bgImgStyle.startsWith('/') ||
              bgImgStyle.startsWith('../') ||
              bgImgStyle.startsWith('./');
            this.pageStyle.backgroundImage = isUrl ? `url("${bgImgStyle}")` : bgImgStyle;
          }
          if (backgroundPosition) {
            this.pageStyle.backgroundPosition = backgroundPosition;
          }
          if (backgroundRepeat) {
            this.pageStyle.backgroundRepeat = backgroundRepeat;
          }
          if (position) {
            this.pageStyle.position = position;
            this.pageAbsolute = true;
            if (style.width) {
              this.pageStyle.width = typeof style.width == 'number' ? `${style.width}px` : style.width;
            } else {
              this.pageStyle.width = '100%';
            }

            if (style.height) {
              this.pageStyle.height = typeof style.height == 'number' ? `${style.height}px` : style.height;
            } else {
              this.pageStyle.height = '100%';
            }
          }
        } else {
          this.pageStyle = {};
        }
      }
    },
    renderWidget(widget) {
      let items = widget.items,
        vars = widget.vars,
        style = widget.style,
        _this = this;
      this.commitPageState(vars);
      this.setStyle(style);
      if (widget.js) {
        _this.pageJsModule = widget.js.key;
      }
      _this.items = items;
      _this.createPageJsInstance();
    },

    getPageDefinition() {
      // 如果指定了pageUuid，则按uuid加载页面，否则根据页面id取最新版本的页面定义
      $axios
        .get('/proxy/webapp/authenticatePage', {
          params: { id: this.pageId, uuid: this.pageUuid },
          headers: { debug: this.security === false || this.designMode || this.previewMode }
        })
        .then(({ data }) => {
          this.loading = false;
          if (data.code == 0) {
            if (data.data) {
              this.$emit('updatePageTitle', data.data.title);
              this.title = data.data.title;
              this.unauthorizedResource.splice(0, this.unauthorizedResource.length);
              if (data.data.unauthorizedResource && data.data.unauthorizedResource.length > 0) {
                this.unauthorizedResource.push(...data.data.unauthorizedResource);
              }
              this.renderWidget(JSON.parse(data.data.definitionJson));

              let widgetI18ns = undefined;
              if (data.data.i18ns.length) {
                widgetI18ns = { [this.$i18n.locale]: { Widget: {} } };
                for (let item of data.data.i18ns) {
                  set(widgetI18ns[this.$i18n.locale].Widget, item.code, item.content);
                }
              }

              this.$nextTick(() => {
                this.$emit('rendered');
                if (widgetI18ns) {
                  for (let l in widgetI18ns) {
                    this.$i18n.mergeLocaleMessage(l, widgetI18ns[l]);
                  }
                }
              });
            } else {
              this.errorCode = '404';
              this.$emit('pageError', { errorCode: '404' });
            }
          }
          if (data.code == -7000) {
            this.errorCode = '403';
            this.$emit('pageError', { errorCode: '403' });
          }
        })
        .catch(() => {});
    }
  },

  mounted() {
    if (this.errorCode == undefined) {
      let _this = this;
      // 需要等待页面相关脚本全部加载完毕再进行渲染
      if (!this.widget && (this.pageUuid || this.pageId)) {
        this.getPageDefinition();
      } else {
        this.mountedExecuted = true;
        this.invokeDevelopmentMethod('mounted');
        this.$nextTick(() => {
          this.$emit('rendered');
        });
      }

      if (this.parent != undefined) {
        this.pageContext.handleEvent(`${this.parent.id}:viewHeight`, v => {
          _this.pageContext.emitEvent(`${_this.pageId}:viewHeight`, v);
        });
      } else {
        this.pageContext.emitEvent(`${_this.pageId}:viewHeight`, window.innerHeight);
      }
    }
  },
  beforeDestroy() {
    dropLocalforage(this.namespace);
  },

  updated() {
    if (this.errorCode == undefined) {
      if (!this.loading && !this.mountedExecuted) {
        this.invokeDevelopmentMethod('mounted');
        this.mountedExecuted = true;

        if (typeof this.height === 'number') {
          this.pageContext.emitEvent(`${this.pageId}:viewHeight`, this.height);
        }
      }
    }
  }
};
</script>
