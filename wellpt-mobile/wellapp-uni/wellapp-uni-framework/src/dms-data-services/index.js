"use strict";

import { isEmpty, isArray, each } from "lodash";

export default {
  validate: function (options) {
    let appFunction = options.appFunction;
    let selection = options.data || [];
    if (selection.length == 0) {
      uni.showToast({
        icon: "none",
        title: appFunction.promptMsg,
      });
      return false;
    } else if (selection.length > 1) {
      // 手机端只能编辑一条记录
      uni.showToast({
        icon: "none",
        title: appFunction.singleSelectPromptMsg,
      });
      return false;
    }
    return true;
  },
  performed: function (options) {
    // 验证
    const _self = this;
    if (!_self.validate(options)) {
      return;
    }

    let appFunction = options.appFunction || {};
    let dmsId = options.dmsId;
    let actionId = options.actionId || appFunction.id;
    let data = options.data;
    let params = options.params;
    // 视图操作
    if (isArray(data)) {
      data = {
        action: appFunction,
        selection: data,
        extras: params,
      };
    }

    let doServiceRequest = function () {
      let url = `/dms/data/services?dms_id=${dmsId}&ac_id=${actionId}`;
      uni.request({
        url,
        method: "POST",
        data,
        success: function (result) {
          let data = result.data || {};
          // 回调处理
          if (options.callback) {
            options.callback.call(options.ui, data);
          } else {
            // 默认处理
            if (data.success == false && data.errorCode == "SaveData") {
              uni.showToast({ icon: "none", title: "表单数据保存失败！" });
            } else {
              _self.onPerformedResult.call(options.ui, data);
            }
          }
        },
      });
    };

    let confirmMsg = appFunction.confirmMsg;
    if (isEmpty(confirmMsg)) {
      doServiceRequest();
    } else {
      uni.showModal({
        title: "提示",
        content: confirmMsg,
        cancelText: "否",
        confirmText: "是",
        success: function (result) {
          if (result.confirm === true) {
            doServiceRequest();
          }
        },
      });
    }
  },
  onPerformedResult: function (result) {
    const _self = this;
    // 关闭窗口
    let close = result.close;
    // 刷新窗口
    let refresh = result.refresh;
    // 刷新父窗口
    let refreshParent = result.refreshParent;
    // 返回的数据
    let resultData = result.data;
    // 要附加的URL参数，存在替换，不存在附加
    let appendUrlParams = result.appendUrlParams;
    // 显示提示信息
    let showMsg = result.showMsg;
    // 操作结果提示
    let msg = result.msg || "操作成功";
    // 操作结果提示类型
    // let msgType = result.msgType;
    // 触发的事件
    let triggerEvents = result.triggerEvents;

    // 提示信息
    if (showMsg) {
      uni.showToast({ title: msg });
    }
    // 刷新父窗口
    if (refreshParent) {
      // 刷新视图列表
      uni.$emit("refresh", result);
    }
    // 关闭当前窗口
    if (close) {
      // 显示提示信息时，延时1秒关闭当前窗口
      setTimeout(
        function () {
          uni.navigateBack({
            delta: 1,
          });
        },
        showMsg ? 1000 : 0
      );
    } else {
      // 刷新当前窗口
      if (refresh && _self.refresh) {
        _self.refresh(appendUrlParams);
      }
    }
    // 触发事件
    if (!isEmpty(triggerEvents)) {
      each(triggerEvents, function (triggerEvent) {
        _self.$emit(triggerEvent, resultData);
      });
    }
  },
};
