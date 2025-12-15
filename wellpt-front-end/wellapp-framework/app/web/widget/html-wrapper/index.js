import { addWindowResizeHandler, dropLocalforage, queryString } from '@framework/vue/utils/util';

export default {
  name: 'HtmlWrapper',
  inject: ['pageContext', '_CONTEXT_STATE_', 'locale'],
  data() {
    return {};
  },
  provide() {
    let urlHashParams = {};
    if (EASY_ENV_IS_BROWSER) {
      if (location.hash) {
        try {
          urlHashParams = queryString(decodeURIComponent(location.hash.substring(1)), {
            ignoreQueryPrefix: true,
            allowDots: true
          });
        } catch (error) {
          console.error(error);
        }
      }
    }
    return {
      urlHashParams,
      pageContext: this.pageContext,
      currentWindow: EASY_ENV_IS_NODE
        ? {}
        : {
            close: function () {
              window.opener = null;
              window.close();
            }
          }
    };
  },
  props: {
    title: String,
    description: String,
    keywords: String,
    version: String,
    provideVar: Object
  },
  components: {},
  computed: {
    vTitle() {
      return this.title || this.$root.title || '网站';
    },
    vFavicon() {
      return this.$root.favicon || '/favicon.ico';
    },
    vKeywords() {
      return this.$root.keywords || this.keywords || 'wellsoft, vue, webpack, server side render';
    },
    vDescription() {
      return this.$root.description || this.description || 'wellsoft server side render';
    },
    baseClass() {
      let _class = this.$root.baseClass || '';
      if (this._CONTEXT_STATE_.SSR_THEME) {
        _class +=
          (_class.length > 0 ? ' ' : '') +
          this._CONTEXT_STATE_.SSR_THEME.themeClass +
          (this._CONTEXT_STATE_.SSR_THEME.colorClass ? ' ' + this._CONTEXT_STATE_.SSR_THEME.colorClass : '') +
          (this._CONTEXT_STATE_.SSR_THEME.fontSizeClass && this._CONTEXT_STATE_.SSR_THEME.fontSizeClass != 'font-size-1'
            ? ' ' + this._CONTEXT_STATE_.SSR_THEME.fontSizeClass
            : '');
      }
      return _class;
    },

    resourceVersion() {
      if (!EASY_ENV_IS_NODE) {
        let key = 'resourceVersionHash';
        let arr = document.cookie.match(new RegExp('(^| )' + key + '=([^;]*)(;|$)'));
        if (arr) {
          return arr[2];
        }
      }
      return '1.0.0';
    }
  },
  render(h) {
    return EASY_ENV_IS_NODE ? ( // 服务端渲染
      <html>
        <head>
          <title>{this.vTitle} </title>
          <meta name="keywords" content={this.vKeywords} />
          <meta name="description" content={this.vDescription} />
          <meta http-equiv="content-type" content="text/html;charset=utf-8" />
          <meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no, minimal-ui" />
          <link rel="shortcut icon" href={this.vFavicon} type="image/x-icon" />
          <script src="/static/js/less/less.min.js" data-env="production"></script>
          <style type="text/css">/* vue-ssr-inject-style */</style>
        </head>
        <body class={this.baseClass} style="overflow: hidden;">
          <div id="app" data-server-rendered="true">
            <a-config-provider locale={this.locale}>{this.$slots.default}</a-config-provider>
          </div>
        </body>
      </html>
    ) : (
      // 客户端渲染
      <div id="app">
        <a-config-provider locale={this.locale} ref="configProvider">
          {this.$slots.default}
        </a-config-provider>
      </div>
    );
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    // 移除overflow
    document.querySelector('body').style.removeProperty('overflow');
    document.title = this.vTitle;
  },
  mounted() {
    $app._provided.configProvider = this.$refs.configProvider._provided.configProvider;
    addWindowResizeHandler(() => {
      this.$nextTick(() => {
        // 发布窗口大小变动通知
        this.pageContext.emitEvent('Window:Resize', window.innerHeight);
      });
    });
    window.addEventListener('beforeunload', event => {
      dropLocalforage();
    });
  },
  watch: {
    title: {
      handler() {
        document.title = arguments[0];
      }
    },
    '_CONTEXT_STATE_.SSR_THEME': {
      deep: true,
      handler(v) {
        // 主题类变更
        document.body.setAttribute('class', this.baseClass || '');
        let iframe = document.querySelectorAll('iframe');
        if (iframe) {
          for (let i = 0, len = iframe.length; i < len; i++) {
            try {
              iframe[0].contentWindow.document.body.setAttribute('class', this.baseClass || '');
            } catch (error) {}
          }
        }
      }
    }
  }
};
