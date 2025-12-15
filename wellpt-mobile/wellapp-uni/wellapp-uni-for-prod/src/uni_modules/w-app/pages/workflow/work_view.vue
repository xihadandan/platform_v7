<template>
  <uni-w-empty v-if="unauthenticateText" type="jurisdiction" :text="unauthenticateText"></uni-w-empty>
  <w-work-view
    ref="workViewRef"
    :style="theme"
    v-else-if="!workViewUniJsModule && workViewOptions"
    :options="workViewOptions"
    @actionSuccess="onActionSuccess"
  >
    <!-- 进入连续签批 -->
    <template v-if="isContinuousMode" slot="header">
      <w-work-continuous-view
        :options="workViewOptions"
        ref="continueRef"
        @close="close"
        @changeOptions="changeOptions"
      ></w-work-continuous-view>
    </template>
  </w-work-view>
  <view v-else-if="workViewUniJsModule && workViewOptions">
    <!-- #ifdef H5 || APP-PLUS -->
    <component :is="workViewUniJsModule" :workViewOptions="workViewOptions" />
    <!-- #endif -->
    <!-- #ifdef MP -->
    <!-- #endif -->
  </view>
</template>

<script>
import { workFlowUtils } from "wellapp-uni-framework";
import { each, map, assign } from "lodash";

export default {
  data() {
    return {
      workViewOptions: null,
      workViewUniJsModule: null,
      options: {},
      unauthenticateText: "",
    };
  },
  onLoad: function (options) {
    var _self = this;
    this.unauthenticateText = "";
    uni.showLoading({ title: "加载中" });
    this.options = options;
    if (!this.options.system) {
      this.options.system = this._$SYSTEM_ID;
    }
    const item = _self.getPageParameter("item");
    let toReload = false;
    if (item) {
      if (options.aclRole == "DRAFT") {
        let flowInstUuid = item.uuid || item.flowInstUuid || item.flow_inst_uuid;
        if (!options.flowInstUuid) {
          options.flowInstUuid = flowInstUuid;
          toReload = true;
        }
      } else {
        let flowInstUuid = item.flowInstUuid || item.flow_inst_uuid;
        let taskInstUuid = item.taskInstUuid || item.task_inst_uuid || item.uuid;
        if (!options.flowInstUuid) {
          options.flowInstUuid = flowInstUuid;
          toReload = true;
        }
        if (!options.taskInstUuid) {
          options.taskInstUuid = taskInstUuid;
          toReload = true;
        }
      }
    }
    if (options.uniJsModule) {
      _self.workViewUniJsModule = options.uniJsModule;
    }
    if (toReload) {
      let params = [];
      for (const key in options) {
        if (Object.hasOwnProperty.call(options, key)) {
          params.push(key + "=" + options[key]);
        }
      }
      _self.updateRouteParam(workFlowUtils.pageUrl + "?" + params.join("&"));
    } else {
      this.options.userId = this._$USER.userId;
      this.options.viewVue = this;
      workFlowUtils.initWorkData(options, this.workView);
    }
  },
  computed: {
    // 连续签批状态
    isContinuousMode() {
      return this.options.continuousMode == "1";
    },
  },
  methods: {
    workView(options) {
      if (options.isReLoad) {
        this.updateRouteParam(options.url, options);
      } else if (options.UnauthenticateText) {
        this.unauthenticateText = options.UnauthenticateText;
        uni.hideLoading();
      } else {
        this.workViewOptions = options;
        uni.hideLoading();
      }
    },
    updateRouteParam(url, options) {
      if (this._$SYSTEM_ID && url && url.indexOf("system=prod_") == -1) {
        url += `&system=${this._$SYSTEM_ID}`;
      }
      uni.redirectTo({
        url: url,
      });
    },
    changeOptions(options) {
      this.workView(options);
    },
    // 刷新父窗口
    refreshParent(options) {
      var _self = this;
      try {
        workFlowUtils.onPerformedResult.call(_self, {
          refreshParent: true,
          ...options,
        });
      } catch (e) {
        console.error(e);
      }
    },
    // 刷新父窗口并关闭当前窗口
    refreshParentWithMessageAndClose(options) {
      const _self = this;
      // 刷新父窗口，父窗口提交成功信息
      workFlowUtils.onPerformedResult.call(_self, {
        refreshParent: true,
        close: true,
        ...options,
      });
    },
    showResultMessage(options) {
      const _self = this;
      workFlowUtils.onPerformedResult.call(_self, {
        message: options.message,
        ...options,
      });
    },
    showResultMessageAndReload(options) {
      const _self = this;
      // 提示信息，2秒后重新加载
      workFlowUtils.onPerformedResult.call(_self, {
        message: options.message,
        msgCallback: () => {
          _self.$refs.workViewRef.workView.reload();
        },
      });
    },
    reloadCurrentPage(options = {}) {
      // 页面重载
      const pages = getCurrentPages();
      // 声明一个pages使用getCurrentPages方法
      const curPage = pages[pages.length - 1];
      // 声明一个当前页面
      curPage.onLoad(assign(curPage.options, options)); // 传入参数
      // 执行刷新
    },
    // 关闭当前窗口
    close() {
      const _self = this;
      try {
        workFlowUtils.onPerformedResult.call(_self, {
          close: true,
        });
      } catch (e) {
        console.error(e);
      }
    },
    // 操作成功
    onActionSuccess(options) {
      const _self = this;
      let action = options.action;
      switch (action) {
        case "save": // 保存
          _self.onSaveSuccess(options);
          break;
        case "submit": // 提交
          _self.onSubmitSuccess(options);
          break;
        case "cancel": // 撤回
          _self.refreshParent(options);
          break;
        case "rollback": // 退回
          _self.onRollbackSuccess(options);
          break;
        case "directRollback": // 直接退回
        case "rollbackToMainFlow": // 退回主流程
        case "transfer": // 转办
        case "counterSign": // 会签
        case "addSign": // 加签
        case "remove": // 删除
          _self.refreshParentWithMessageAndClose(options);
          break;
        case "copyTo": // 抄送
        case "print": // 套打
        case "remind": // 催办
          _self.showResultMessage(options);
          break;
        case "attention": // 关注
        case "unfollow": // 取消关注
        case "handOver": // 特送个人
          _self.showResultMessageAndReload(options);
          break;
        case "gotoTask": // 特送环节
          _self.refreshParentWithMessageAndClose(options);
          break;
        case "suspend": // 挂起
        case "resume": // 恢复
          _self.showResultMessageAndReload(options);
          break;
        case "subflowAddSubflow": // 子流程添加承办
        case "subflowAddMajorFlow": // 子流程添加主办
        case "subflowAddMinorFlow": // 子流程添加协办
        case "subflowRemind": // 子流程催办
        case "subflowSendMessage": // 子流程信息分发
        case "subflowLimitTime": // 子流程协办时限
        case "subflowRedo": // 子流程重办
          _self.showResultMessageAndReload(options);
          break;
        case "subflowStop": // 子流程终止
          _self.onSubflowStopSuccess(options);
          break;
        default:
          _self.showResultMessage(options);
      }
    },
    // 保存成功
    onSaveSuccess(options) {
      const _self = this;
      // 提示信息
      _self.showResultMessageAndReload(options);
    },
    // 提交成功
    onSubmitSuccess(options) {
      const _self = this;
      let result = options.result;
      // 与前环节相同自动提交
      let sameUserSubmitInfo = _self.$refs.workViewRef.getSameUserSubmitInfo(result);
      if (sameUserSubmitInfo != null) {
        // 刷新父窗口
        _self.refreshParent(options);
        _self.handleSameUserSubmitInfo(sameUserSubmitInfo);
      } else {
        // 父窗口提交成功信息
        _self.refreshParent({
          ...options,
          msgCallback: () => {
            if (_self.isContinuousMode) {
              _self.$refs.continueRef && _self.$refs.continueRef.toUpdateRecord();
            } else {
              // 关闭当前窗口
              if (result && result.data && result.data.downloadFile) {
                setTimeout(() => {
                  _self.close();
                }, 1000);
              } else {
                _self.close();
              }
            }
          },
        });
      }
    },
    handleSameUserSubmitInfo(sameUserSubmitInfo) {
      const _self = this;
      let sameUserSubmitType = sameUserSubmitInfo.sameUserSubmitType ? sameUserSubmitInfo.sameUserSubmitType : "0";
      let refreshUrl = workFlowUtils.pageUrl + sameUserSubmitInfo.refreshUrl;
      let keepOnCurrentPage = false;
      switch (sameUserSubmitType) {
        case "0": // 自动提交，让办理人确认是否继承上一环节意见
        case "1": // 自动提交，且自动继承意见
          keepOnCurrentPage = true;
          break;
        case "2": // 不自动提交并关闭页面
          break;
        case "3": // 不自动提交并刷新页面
          keepOnCurrentPage = true;
          break;
      }

      // 与前环节相同自动提交
      setTimeout(() => {
        if (_self.isContinuousMode) {
          _self.$refs.continueRef && _self.$refs.continueRef.changeDocumentInfo(sameUserSubmitInfo);
        } else {
          if (keepOnCurrentPage) {
            _self.updateRouteParam(refreshUrl);
          } else {
            _self.close();
          }
        }
      }, 2000);

      return true;
    },
    // 退回成功
    onRollbackSuccess(options) {
      const _self = this;
      let result = options.result;
      // 刷新父窗口
      _self.refreshParent();
      // 退回到自己
      let rollbackToSelfInfo = _self.$refs.workViewRef.getRollbackToSelfInfo(result);
      if (rollbackToSelfInfo && rollbackToSelfInfo.rollbackToSelf) {
        _self.rollbackToSelf(rollbackToSelfInfo);
        // 提示信息
        _self.showResultMessage(options);
      } else {
        if (_self.isContinuousMode) {
          // 进入下一条
          _self.$refs.continueRef && _self.$refs.continueRef.gotoNextRecord();
        } else {
          // 父窗口提交成功信息
          _self.refreshParentWithMessageAndClose(options);
        }
      }
    },
    rollbackToSelf(rollbackToSelfInfo) {
      const _self = this;
      let refreshUrl =
        workFlowUtils.pageUrl +
        `?aclRole=TODO&taskInstUuid=${rollbackToSelfInfo.taskInstUuid}&&flowInstUuid=${rollbackToSelfInfo.flowInstUuid}`;
      setTimeout(() => {
        _self.updateRouteParam(refreshUrl);
      }, 2000);
    },
    // 子流程终止成功处理
    onSubflowStopSuccess(options) {
      const _self = this;
      let stopSelf = options.stopSelf;
      if (stopSelf === true) {
        _self.refreshParentWithMessageAndClose(options);
      } else {
        _self.showResultMessageAndReload(options);
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.work-view-wrapper {
  width: 100%;
}
</style>
