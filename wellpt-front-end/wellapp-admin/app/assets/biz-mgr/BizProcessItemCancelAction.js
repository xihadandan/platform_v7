define(['constant', 'commons', 'server', 'appContext', 'appModal'], function (constant, commons, server, appContext, appModal) {
  var StringUtils = commons.StringUtils;
  var UUID = commons.UUID;
  var JDS = server.JDS;

  var ProcessItemCancelAction = function () {};

  $.extend(ProcessItemCancelAction.prototype, {
    process_item_cancel: function (options) {
      var _self = this;
      _self.options = options;
      _self.cancel();
    },
    process_item_cancel_other: function (options) {
      var _self = this;
      _self.options = options;
      _self.cancelOther = true;
      _self.cancel();
    },
    cancel: function () {
      var _self = this;
      // 避免请求的遮罩被隐藏
      if (window.hideMaskTimer) {
        clearTimeout(window.hideMaskTimer);
      }
      var options = _self.options;
      if (options.workView) {
        _self.cancelWorkView(options);
      } else {
        _self.cancelBizProcessItem(options);
      }
    },
    // 从流程撤回
    cancelWorkView: function (options) {
      const _self = this;
      var params = options.params || {};
      var workView = options.workView;
      workView.opinionToWorkData();
      var workData = workView.getWorkData();

      var doCancel = function () {
        workView.getDyformData(function (dyformData) {
          workData.dyFormData = dyformData;
          _self.doCancelByWorkData(workData);
        });
      };

      // 撤回意见必填
      if (params.requiredCancelOpinion == 'true') {
        if (StringUtils.isBlank(workData.opinionText)) {
          workView.getOpinionEditor().show();
          appModal.warning('请先签署意见!');
          return;
        }
      }
      if (params.confirmMsg) {
        appModal.confirm(params.confirmMsg, function (result) {
          if (result) {
            doCancel();
          }
        });
      } else {
        doCancel();
      }
    },
    doCancelByWorkData: function (workData) {
      var _self = this;
      var options = _self.options;
      var cancelUrl = '/proxy/api/biz/process/item/instance/cancelByWorkData';
      if (_self.cancelOther) {
        cancelUrl = '/proxy/api/biz/process/item/instance/cancelOtherByWorkData';
      }
      JDS.restfulPost({
        url: cancelUrl,
        data: workData,
        mask: true,
        success: function (result) {
          options.workView.allowUnloadWorkData = true;
          appModal.success({
            message: options.successMsg || '撤回成功！',
            resultCode: 0
          });
        }
      });
    },
    // 撤回业务事项
    cancelBizProcessItem: function (options) {
      var _self = this;
      var bizData = options.bizData;
      bizData.dyFormData = options.data.dyFormData;
      JDS.restfulPost({
        url: '/proxy/api/biz/process/item/instance/cancel',
        data: bizData,
        success: function (result) {
          appModal.success({
            message: '撤回成功！',
            resultCode: 0
          });
        }
      });
    }
  });

  return ProcessItemCancelAction;
});
