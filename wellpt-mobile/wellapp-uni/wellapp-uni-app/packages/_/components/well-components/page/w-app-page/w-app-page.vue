<template>
  <view class="w-app-page">
    <w-page-container :widgetDefinition="appPageDefinition"></w-page-container>
  </view>
</template>

<script>
import { isEmpty, set } from "lodash";
import { mapMutations, mapState } from "vuex";
import { device, storage } from "wellapp-uni-framework";
import { univerifyLogin } from "@/common/univerify.js";
// import wPageContainer from "../w-page-container/w-page-container.vue";

export default {
  props: {
    appPageDefinitionJson: String,
    appPiPath: String,
    pageId: String,
    pageUuid: String,
    widgetDefId: String,
    workbench: {
      // 是否为工作台
      type: Boolean,
      default: false,
    },
    jsModule: String,
    workbenchPath: {
      type: String, // 显示工作台的页面路径
      default: INDEX_PAGE_PATH || "/pages/welldo/index",
    },
    _unauthorizedResource: Array, // 受保护的页面资源，经过后端判断得出的未授权集合
    widgetI18ns: Object,
    navBarTitle: String,
  },
  // components: { wPageContainer },
  data() {
    let namespace = this.pageId || this.pageUuid;
    return {
      namespace,
      appPageDefinition: {},
      pageJsModule: this.jsModule,
      unauthorizedResource: this._unauthorizedResource != undefined ? [...this._unauthorizedResource] : [],
      scrollTop: 0.01,
    };
  },
  provide() {
    return {
      unauthorizedResource: this.unauthorizedResource,
      $pageJsInstance: undefined,
      namespace: this.namespace, // 以页面ID作为命名空间
    };
  },
  computed: {
    ...mapState({
      currentPageOptions: (state) => state.pageOptions,
    }),
    ...mapState(["hasLogin", "forcedLogin", "userName"]),
  },
  created: function () {
    let _self = this;
    if (!this.hasLogin) {
      return false;
    }
    _self.getAppPage(function (data = {}) {
      console.log("getAppPage", data);
      if (data && data.definitionJson) {
        let definition = JSON.parse(data.definitionJson);
        _self.appPageDefinition = definition;
        if (definition.js) {
          _self.pageJsModule = definition.js.key;
        }
        _self.setPageNavBar(definition);
        console.log(JSON.parse(data.definitionJson));
      } else {
        console.log("uni app definition json is null");
        console.log("appPiPath: " + _self.appPiPath);
        console.log("pageUuid: " + _self.pageUuid);
        uni.setStorageSync("loginSuccessRedirect", "/pages/welldo/index");
        this.guideToLogin();
        return false;
      }
      if (_self.appPageDefinition.i18n) {
        let pageI18ns = {
          [this.$i18n.locale]: {
            Page: _self.appPageDefinition.i18n[this.$i18n.locale],
          },
        };
        for (let l in pageI18ns) {
          this.$i18n.mergeLocaleMessage(l, pageI18ns[l]);
        }
      }
      _self.createPageJsInstance();
      // 设置标题栏
      _self.setNavigationBarTitle(data.currentUserAppData);
      // 工作台信息
      if (_self.workbench) {
        _self.setWorkbenchAppPiPath(_self.appPiPath);
        if (_self.appPageDefinition) {
          _self.setWorkbenchPageUuid(_self.appPageDefinition.uuid);
        } else {
          _self.setWorkbenchPageUuid(_self.pageUuid);
        }
        storage.setStorageSync("workbenchPath", _self.workbenchPath);
      }
    });
    if (this.widgetI18ns != undefined) {
      for (let locale in this.widgetI18ns) {
        this.$i18n.mergeLocaleMessage(locale, this.widgetI18ns[locale]);
      }
    }
    this.bindEvents();
  },
  methods: {
    ...mapMutations(["setCustomNavBar", "setNavBarTitle", "setWorkbenchAppPiPath", "setWorkbenchPageUuid"]),
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    setPageNavBar(definition) {
      const execute = (definition) => {
        if (definition.items && definition.items.length) {
          if (definition.items[0].wtype === "WidgetUniNavBar") {
            this.setCustomNavBar(false);
          } else {
            this.setCustomNavBar(true);
          }
        }
      };
      if (this.currentPageOptions) {
        if (this.currentPageOptions.pageId === definition.id || this.currentPageOptions.pageUuid === definition.uuid) {
          execute(definition);
        }
      }
    },
    createPageJsInstance() {
      // 页面二开脚本实例
      if (this.pageJsModule) {
        this.$pageJsInstance = this.appContext.jsInstance(this.pageJsModule);
        if (this.$pageJsInstance) {
          this._provided.$pageJsInstance = this.$pageJsInstance;
          this._provided.$pageJsInstance._JS_META_ = this.pageJsModule;
        }
      }
    },
    goBack() {
      let pages = getCurrentPages(); //获取所有页面栈实例列表
      if (pages.length > 1) {
        uni.navigateBack({
          delta: 1,
        });
      } else if (pages[0] && pages[0].route == "pages/welldo/index") {
        uni.setStorageSync("loginSuccessRedirect", INDEX_PAGE_PATH);
        this.guideToLogin();
      } else {
        uni.reLaunch({
          url: INDEX_PAGE_PATH,
        });
      }
    },
    setNavigationBarTitle: function (appData) {
      const _self = this;
      if (this.navBarTitle) {
        let title = this.navBarTitle;
        uni.setNavigationBarTitle({
          title,
        });
        _self.setNavBarTitle(title);
      } else if (appData == null) {
        // 组件定义加载从组件的name配置或标题获取
        if (!isEmpty(_self.widgetDefId)) {
          let widetConfiguration = _self.appPageDefinition.configuration || {};
          let headerName = widetConfiguration.name || _self.appPageDefinition.title;
          uni.setNavigationBarTitle({
            title: headerName,
          });
          _self.setNavBarTitle(headerName);
        } else if (_self.appPageDefinition.title) {
          let headerName = _self.$t("Widget.title", _self.appPageDefinition.title);
          uni.setNavigationBarTitle({
            title: headerName,
          });
          _self.setNavBarTitle(headerName);
        }
      } else {
        let system = appData.system;
        let module = appData.module;
        let app = appData.application;
        let appId = system.id;
        let headerName = system.name;
        if (app) {
          appId = app.id;
          headerName = app.title || app.name;
        } else if (module) {
          appId = module.id;
          headerName = module.title || module.name;
        }
        uni.setNavigationBarTitle({
          title: headerName,
        });
        _self.setNavBarTitle(headerName);
        console.log("setNavBarTitle", appId, headerName);
      }
    },
    getAppPage: function (callback) {
      var _self = this;
      let key = "app_page_" + _self.appPiPath + "_" + _self.pageUuid;
      // 从本地存储读取页面定义信息
      // try {
      //   let definitionJson = uni.getStorageSync(key);
      //   if(definitionJson != null || definitionJson != "") {
      //     var definition = JSON.parse(definitionJson);
      //     callback.call(_self, definition);
      //     console.log("getAppPage from storage")
      //     return;
      //   }
      // } catch (e) {
      // }
      // 组件调用传入
      if (_self.appPageDefinitionJson) {
        callback.call(_self, {
          definitionJson: _self.appPageDefinitionJson,
        });
      } else if (!isEmpty(_self.widgetDefId)) {
        // 根据组件定义ID获取
        uni.request({
          service: "appContextService.getUniAppWidgetDefinitionById",
          data: [_self.widgetDefId, false],
          success: (res) => {
            if (res.data.code == 403 || res.data.code == -7000) {
              uni.showModal({
                content: this.$t("global.noPermission", "您无权限访问该页面，请联系管理员！"),
                showCancel: false,
                confirmText: this.$t("global.confirm", "确认"),
                success: function (res) {
                  if (res.confirm) {
                    _self.goBack();
                  }
                },
              });
            } else if (res.data.errorCode === "SessionExpired") {
              uni.setStorageSync("loginSuccessRedirect", "/pages/welldo/index");
              this.guideToLogin();
            } else {
              callback.call(_self, res.data.data);
              if (res.data.data) {
                uni.setStorage({
                  key: key,
                  data: JSON.stringify(res.data.data),
                });
              } else {
                uni.showModal({
                  content: this.$t("global.noPermission", "您无权限访问该页面，请联系管理员！"),
                  showCancel: false,
                  confirmText: this.$t("global.confirm", "确认"),
                  success: function (res) {
                    if (res.confirm) {
                      _self.goBack();
                    }
                  },
                });
              }
            }
          },
          fail: (e) => {
            console.error(e);
          },
          complete: () => {},
        });
      } else {
        // 从服务器取页面定义信息
        // 工作台从上次选择的工作台加载
        if (_self.workbench) {
          let workbenchPageUuid = storage.getStorageSync("workbenchPageUuid");
          if (!isEmpty(workbenchPageUuid)) {
            pageUuid = workbenchPageUuid;
          }
        }
        uni.request({
          url: "/webapp/authenticatePage",
          data: {
            id: _self.pageId,
            uuid: _self.pageUuid,
            uniApp: true,
          },
          method: "GET",
          success: (res) => {
            if (res.data.code == 403 || res.data.code == -7000) {
              uni.showModal({
                content: this.$t("global.noPermission", "您无权限访问该页面，请联系管理员！"),
                showCancel: false,
                confirmText: this.$t("global.confirm", "确认"),
                success: function (res) {
                  if (res.confirm) {
                    _self.goBack();
                  }
                },
              });
            } else if (res.data.errorCode === "SessionExpired") {
              uni.setStorageSync("loginSuccessRedirect", "/pages/welldo/index");
              this.guideToLogin();
            } else {
              if (res.data.data) {
                uni.setStorage({
                  key: key,
                  data: JSON.stringify(res.data.data),
                });
                let widgetI18ns = undefined;
                if (res.data.data.i18ns.length) {
                  widgetI18ns = { [this.$i18n.locale]: { Widget: {} } };
                  for (let item of res.data.data.i18ns) {
                    if (item.locale == this.$i18n.locale) {
                      set(widgetI18ns[this.$i18n.locale].Widget, item.code, item.content);
                    }
                  }
                }
                if (widgetI18ns) {
                  for (let l in widgetI18ns) {
                    this.$i18n.mergeLocaleMessage(l, widgetI18ns[l]);
                  }
                }
                callback.call(_self, res.data.data);

                this.$nextTick(() => {
                  this.$emit("rendered");
                });
              } else {
                uni.showModal({
                  content: this.$t("global.noPermission", "您无权限访问该页面，请联系管理员！"),
                  showCancel: false,
                  confirmText: this.$t("global.confirm", "确认"),
                  success: function (res) {
                    if (res.confirm) {
                      _self.goBack();
                    }
                  },
                });
              }
            }
          },
          fail: (e) => {
            console.error(e);
          },
          complete: () => {},
        });
      }
    },
    // 跳转到锚点
    focusWidget: function (event, widget) {
      let _self = this;
      const query = uni.createSelectorQuery().in(this);
      query
        .select(".widget_" + widget.id)
        .boundingClientRect((data) => {
          if (data) {
            _self.getPageRect(data.top);
          }
        })
        .exec();
    },
    getPageRect(top) {
      let _self = this;
      const query = uni.createSelectorQuery().in(this);
      query
        .select(".w-app-page")
        .boundingClientRect((res) => {
          if (res) {
            let toTop = top - res.top - 10;
            uni.pageScrollTo({
              scrollTop: toTop,
            });
          }
        })
        .exec();
    },
    bindEvents: function () {
      var _self = this;
      uni.$off("anchorWidgetInContent");
      uni.$on("anchorWidgetInContent", (res) => {
        if (res.id) {
          this.focusWidget(undefined, res);
        }
      });
    },
    guideToLogin() {
      uni.reLaunch({
        url: LOGIN_PAGE_PATH,
      });
      return false;
    },
  },
  mounted() {
    if (!this.hasLogin) {
      uni.showModal({
        title: this.$t("loginComponent.unLogin", "未登录"),
        content: this.$t("loginComponent.loginAgain", "请重新登录！"),
        showCancel: false,
        success: (res) => {
          if (res.confirm) {
            this.guideToLogin();
          }
        },
      });
    }
  },
};
</script>

<style lang="scss" scoped>
.w-app-page {
  width: 100%;
}
</style>
