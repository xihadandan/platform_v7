<template>
  <view class="dms-dyform-document-view">
    <slot name="header"></slot>
    <slot name="dyform" :dyformOptions="dyformOptions">
      <w-dyform v-if="dyformOptions != null" :options="dyformOptions" @initSuccess="onDyformInitSuccess"></w-dyform>
    </slot>
    <slot name="toolbar" :actions="actions">
      <view class="dms-dyform-toolbar" v-if="actions && actions.length > 0">
        <view class="btn-group">
          <view v-for="(action, index) in actions" :key="index" class="btn" :class="'btn-' + action.id">
            <button :type="'default'" :class="action.cssClass" @tap="actionTap($event, action)">
              {{ action.name }}
            </button>
          </view>
        </view>
      </view>
    </slot>
  </view>
</template>

<script>
import { each as forEach, isEmpty } from "lodash";
import { appContext, dmsDataServices } from "wellapp-uni-framework";
export default {
  props: {
    dmsId: String,
    configuration: Object,
    item: Object,
    extraParams: Object,
    loadDataActionId: {
      type: String, // 加载表单数据的action_id
      default: "btn_dyform_get",
    },
  },
  data() {
    let store = this.configuration.store || {};
    let item = this.item || { formUuid: store.mFormUuid || store.formUuid, dataUuid: "" };
    return {
      documentData: null,
      formUuid: item.mFormUuid || item.formUuid,
      dataUuid: item.uuid,
      dyformOptions: null,
      actions: [],
      moreActions: [],
      buttonRect: {},
    };
  },
  created() {
    const _self = this;
    _self.loadData(function (documentData) {
      // 初始化文档数据
      _self.initDocument(documentData);
      // 创建文档操作
      _self.createActionSheetIfRequried();
    });
  },
  methods: {
    loadData: function (callback) {
      const _self = this;
      let dmsId = _self.dmsId;
      let formUuid = _self.formUuid;
      let dataUuid = _self.dataUuid;
      let extras = _self.getExtras();
      dmsDataServices.performed({
        ui: _self,
        dmsId,
        actionId: _self.loadDataActionId,
        data: { dmsId, formUuid, dataUuid, extras },
        callback: function (documentData) {
          callback.call(_self, documentData);
        },
      });
    },
    initDocument: function (documentData) {
      const _self = this;
      let dyFormData = documentData.dyFormData;
      let displayAsLabel = documentData.displayAsLabel;
      var dyformOptions = {
        formData: dyFormData,
        recordInitFormDatas: true,
        displayAsLabel,
        optional: {
          title: documentData.title,
        },
      };
      _self.dyformOptions = dyformOptions;
      _self.documentData = documentData;
      uni.setNavigationBarTitle({
        title: documentData.title,
      });
    },
    onDyformInitSuccess: function (dyform) {
      this.dyform = dyform;
      this.$emit("dyformInitSuccess", dyform);
    },
    createActionSheetIfRequried: function () {
      const _self = this;
      let showBtnCount = 3;
      let actions = _self._wrapperActions(_self.documentData.actions || []);
      if (actions.length <= showBtnCount) {
        _self.actions = actions;
      } else {
        let newActions = [];
        let newMoreActions = [];
        for (var i = 0; i < actions.length; i++) {
          let action = actions[i];
          if (i < showBtnCount - 1) {
            newActions.push(action);
          } else if (i == showBtnCount - 1) {
            newActions.push({
              id: "more",
              name: "更多",
            });
            newMoreActions.push(action);
          } else {
            newMoreActions.push(action);
          }
        }
        _self.actions = newActions;
        _self.moreActions = newMoreActions;
      }
    },
    // 包装操作对象，将properties属性的key、value合并到action中
    _wrapperActions: function (actions) {
      if (!actions) {
        return [];
      }
      let retActions = [];
      forEach(actions, function (action) {
        let properties = action.properties;
        for (let p in properties) {
          // 名称不复制
          if (p === "name") {
            continue;
          }
          action[p] = properties[p];
        }
        // 操作隐藏
        if (action.hidden == "1") {
          return;
        }
        // iconfont替换为uniui-iconfont
        action.cssClass =
          action.cssClass + " " + ((action.icon && action.icon.className) || "").replace(/\b(?=iconfont)\b/, "uniui-");
        retActions.push(action);
      });
      return retActions;
    },
    actionTap(e, action) {
      const _self = this;
      // 更多
      if (action.id == "more") {
        _self.actionSheetTap();
      } else {
        // 执行操作
        _self.performed(action);
      }
    },
    actionSheetTap() {
      const _self = this;
      let itemList = [];
      for (let i = 0; i < _self.moreActions.length; i++) {
        itemList.push(_self.moreActions[i].name);
      }
      uni.showActionSheet({
        itemList: itemList,
        popover: {
          // 104: navbar + topwindow 高度，暂时 fix createSelectorQuery 在 pc 上获取 top 不准确的 bug
          top: _self.buttonRect.top + 104 + _self.buttonRect.height,
          left: _self.buttonRect.left + _self.buttonRect.width / 2,
        },
        success: (e) => {
          console.log(e.tapIndex);
          _self.actionTap(e, _self.moreActions[e.tapIndex]);
        },
      });
    },
    // 验证表单数据
    validateDyformData: function () {
      return this.dyform.validateForm();
    },
    // 执行验证
    validate: function () {
      const _self = this;
      let result = false;
      try {
        result = _self.validateDyformData();
      } catch (e) {
        console.error(e);
        uni.showToast({ title: "表单数据验证出错：" + e });
        throw e;
      }
      return result;
    },
    // 执行操作
    performed: function (action) {
      const _self = this;
      let extras = _self.getExtras();
      // 获取表单数据
      let dyFormData = _self.dyform.collectFormData();
      // 验证处理
      if (action.validate === true && !_self.validate(action)) {
        return;
      }
      let rawParams = action.params || {};
      let params = Object.assign({}, rawParams);
      let data = {
        action,
        extras: Object.assign(extras, params),
        formUuid: _self.formUuid,
        dataUuid: _self.dataUuid,
        dyFormData: dyFormData,
      };
      // 执行JS模块，触发事件
      // if (action.executeJsModule) {
      //   console.log("executeUniJsModule", action);
      // _self.$emit("executeUniJsModule", action.executeJsModule, data);
      // 数据管理的操作标记分类为DmsAction
      if (isEmpty(action.appPath) && isEmpty(action.category)) {
        action.category = "DmsAction";
      }
      appContext.startApp({
        ui: _self,
        dmsId: _self.dmsId,
        appFunction: action,
        data,
      });
      // } else {
      //   // 执行后端请求
      //   _self.dmsServiceRequest(action.id, data, function (data) {
      //     _self.onPerformedResult(data);
      //   });
      // }
    },
    refresh: function (urlParams) {
      const _self = this;
      _self.setPageParameter("dmsId", _self.dmsId);
      _self.setPageParameter("configuration", _self.configuration);
      _self.setPageParameter("item", _self.item);
      _self.setPageParameter("extraParams", Object.assign(_self.getExtras(), urlParams || {}));
      uni.redirectTo({
        url: "/packages/_/pages/dms/dms_dyform_view",
      });
    },
    // 获取文档数据
    getDocumentData() {
      const _self = this;
      if (!_self.documentData) {
        _self.documentData = {};
      }
      return _self.documentData;
    },
    // 获取文档额外数据
    getExtras: function () {
      const _self = this;
      let docData = _self.getDocumentData();
      let extras = docData.extras;
      if (extras == null) {
        extras = {};
        let extraParams = _self.extraParams || {};
        for (let p in extraParams) {
          extras[p] = extraParams[p];
        }
        docData.extras = extras;
      }
      return extras;
    },
    // 获取文档额外数据
    getExtra: function (key) {
      return this.getExtras()[key];
    },
    // 设置文档额外数据
    setExtra: function (key, value) {
      this.getExtras()[key] = value;
    },
  },
};
</script>

<style lang="scss" scoped>
.dms-dyform-document-view {
  .dms-dyform-toolbar {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    justify-content: center;
    align-items: center;

    position: fixed;
    left: 0;
    right: 0;
    /* #ifdef H5 */
    left: var(--window-left);
    right: var(--window-right);
    /* #endif */
    bottom: var(--window-bottom, 0);
    z-index: 10;

    background: $uni-bg-color;
    box-shadow: $uni-shadow-base;
    border-top: 3px solid $uni-border-3;
    height: 50px;
    padding: 0 5px;

    .btn-group {
      -webkit-flex: 1;
      flex: 1;
      display: flex;
      flex-direction: row;
      justify-content: center;
      align-items: center;

      .btn {
        -webkit-flex: 1;
        flex: 1;
        margin-right: 10rpx;
      }
      .btn-more {
        -webkit-flex: 0.5;
        flex: 0.5;
      }
    }

    .uniui-iconfont::before {
      margin-right: 2px;
    }

    .btn-inverse {
      background-color: $uni-btn-color-inverse; // #000000; // 黑色
    }
    .btn-default {
      background-color: $uni-btn-color-default; // #d4d4d4; // 灰色
    }
    .btn-primary {
      background-color: $uni-btn-color-primary; // #007aff; // 蓝色
    }
    .btn-success {
      background-color: $uni-btn-color-success; // #3aa322; // 绿色
    }
    .btn-info {
      background-color: $uni-btn-color-info; // #2aaedd; // 浅蓝
    }
    .btn-warning {
      background-color: $uni-btn-color-warning; // #e99f00; // 橙色
    }
    .btn-danger {
      background-color: $uni-btn-color-danger; // #e33033; // 红色
    }
  }
}
</style>
