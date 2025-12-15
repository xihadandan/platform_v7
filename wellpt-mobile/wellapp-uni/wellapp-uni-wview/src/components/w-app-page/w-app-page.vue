<template>
  <view class="w-app-page">
    <w-page-container :widgetDefinition="appPageDefinition"></w-page-container>
  </view>
</template>

<script>
import { isEmpty } from "lodash";
import { mapMutations } from "vuex";
import { device, storage } from "wellapp-uni-framework";
import wPageContainer from "../w-page-container/w-page-container.vue";

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
      default: "/pages/main/main",
    },
    _unauthorizedResource: Array, // 受保护的页面资源，经过后端判断得出的未授权集合
  },
  components: { wPageContainer },
  data() {
    let namespace = this.pageId || this.pageUuid;
    return {
      namespace,
      appPageDefinition: {},
      pageJsModule: this.jsModule,
      unauthorizedResource: this._unauthorizedResource != undefined ? [...this._unauthorizedResource] : [],
    };
  },
  provide() {
    return {
      unauthorizedResource: this.unauthorizedResource,
      $pageJsInstance: undefined,
      namespace: this.namespace, // 以页面ID作为命名空间
    };
  },

  created: function () {
    let _self = this;
    _self.getAppPage(function (data = {}) {
      if (data.definitionJson) {
        let definition = JSON.parse(data.definitionJson);
        _self.appPageDefinition = definition;
        if (definition.js) {
          _self.pageJsModule = definition.js.key;
        }
        console.log(JSON.parse(data.definitionJson));
      } else {
        console.log("uni app definition json is null");
        console.log("appPiPath: " + _self.appPiPath);
        console.log("pageUuid: " + _self.pageUuid);
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
  },
  methods: {
    ...mapMutations(["setNavBarTitle", "setWorkbenchAppPiPath", "setWorkbenchPageUuid"]),

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
      uni.navigateBack({
        delta: 1,
      });
    },
    setNavigationBarTitle: function (appData) {
      const _self = this;
      if (appData == null) {
        // 组件定义加载从组件的name配置或标题获取
        if (!isEmpty(_self.widgetDefId)) {
          let widetConfiguration = _self.appPageDefinition.configuration || {};
          let headerName = widetConfiguration.name || _self.appPageDefinition.title;
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
            callback.call(_self, res.data.data);
            if (res.data.data) {
              uni.setStorage({
                key: key,
                data: JSON.stringify(res.data.data),
              });
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
            callback.call(_self, res.data.data);
            if (res.data.data) {
              uni.setStorage({
                key: key,
                data: JSON.stringify(res.data.data),
              });
            }
          },
          fail: (e) => {
            console.error(e);
          },
          complete: () => {},
        });
      }
    },
  },
};
</script>

<style scoped>
.w-app-page {
  width: 100%;
}
</style>
