import createPageContext from '@pageAssembly/app/web/framework/vue/pageContext';
import { merge, camelCase, set } from 'lodash';

export default {
  install(Vue) {
    Vue.prototype._CreatePageContext = createPageContext;
    Vue.mixin({
      computed: {
        // 是否为浏览器端渲染
        ENV_IS_BROWSER() {
          return EASY_ENV_IS_BROWSER;
        }
      }
    });
    if (EASY_ENV_IS_BROWSER) {
      const loadingFunc = text => {
        if (text === false) {
          window.__VLOADING = false;
          let $l = document.querySelector('.vloading');
          if ($l) {
            $l.remove();
          }
          return;
        }

        if (window.__VLOADING) {
          return;
        }
        window.__VLOADING = true;
        let div = document.createElement('div'),
          container = document.body;
        container.appendChild(div);
        new (Vue.extend({
          render(h) {
            return (
              <div class="vloading" style={this.style}>
                <a-spin tip={text}></a-spin>
              </div>
            );
          },
          data() {
            return {
              style: {
                position: 'fixed',
                top: 0,
                zIndex: 99999999,
                width: '100%',
                height: '100%',
                background: 'rgb(0 0 0 / 15%)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }
            };
          }
        }))().$mount(div);
      };

      Vue.prototype.$loading = loadingFunc;
      window.$LOADING = loadingFunc;

      // 是否存在权限
      Vue.prototype._hasRole = function (role) {
        return role && this._$USER && this._$USER.roles && this._$USER.roles.includes(role);
      };
      // 是否存在任一权限
      Vue.prototype._hasAnyRole = function (roles) {
        let myRoles = this._$USER ? this._$USER.roles || [] : [];
        if (roles) {
          let _roles = Array.isArray(roles) ? roles : [roles];
          for (let i = 0, len = _roles.length; i < len; i++) {
            if (myRoles.includes(_roles[i])) {
              return true;
            }
          }
        }
        return false;
      };

      // 判断角色是否存在于影响该授权页面的生效角色内 ( 用户可能由 N 个角色，但是实际上使用户能访问到该页面的有效角色只是其中的子集 )
      Vue.prototype._hasAnyEffectiveRole = function (roles) {
        if (roles && this._$pageEffectiveRoles) {
          let _roles = Array.isArray(roles) ? roles : [roles];
          for (let i = 0, len = _roles.length; i < len; i++) {
            if (this._$pageEffectiveRoles.includes(_roles[i])) {
              return true;
            }
          }
        }
        return false;
      };

      Vue.prototype._lazyLoadAppCodeI18nMessagesApplyTo = function (applyTo) {
        return new Promise((resolve, reject) => {
          this.$axios
            .get(`/proxy/api/app/codeI18n/getAppCodeI18nByApplyTo`, {
              params: {
                applyTo
              }
            })
            .then(({ data }) => {
              let result = {};
              if (data.data && data.data.length) {
                for (let d of data.data) {
                  set(result, d.code, d[camelCase($app.$i18n.locale)]);
                }
                this.$i18n.mergeLocaleMessage(this.$i18n.locale, result);
              }
              resolve(result);
            })
            .catch(error => {});
        });
      };
    }

    /**
     * 重写翻译方法： 支持返回默认值，针对未做国际化的相关配置
     * @returns
     */
    Vue.prototype.$t = function () {
      let params = {},
        defaultValue = undefined;
      if (arguments.length > 1) {
        if (arguments[1] != undefined) {
          if (typeof arguments[1] !== 'string') {
            // 参数化翻译
            params = arguments[1];
          } else {
            // 默认值
            defaultValue = arguments[1];
          }
        }
        if (arguments[2] != undefined && typeof arguments[2] == 'string') {
          // 默认值
          defaultValue = arguments[2];
        }
      }
      if (this.$te(arguments[0])) {
        return this.$i18n.t.apply(this.$i18n, [arguments[0], params]);
      }
      return defaultValue || '';
    };

    /**
     * 翻译服务调用
     * @param {*} word 字符串或者字符串数组
     * @param {*} from 源文本国家编码，如: zh , en
     * @param {*} to 翻译为目标国家编码
     * @returns 当 word 为数组时候，返回 map 对象，否则返回翻译后的文本
     */
    Vue.prototype.$translate = function (word, from, to) {
      return new Promise((resolve, reject) => {
        this.$axios[typeof word == 'string' ? 'get' : 'post'](
          '/proxy/api/translate/text',
          typeof word == 'string' ? { params: { word: encodeURIComponent(word), from, to } } : { word, from, to }
        )
          .then(({ data }) => {
            if (data.code == 0) {
              resolve(data.data);
            }
          })
          .catch(error => {
            console.error('翻译服务异常:', error);
            reject();
          });
      });
    };
  }
};
