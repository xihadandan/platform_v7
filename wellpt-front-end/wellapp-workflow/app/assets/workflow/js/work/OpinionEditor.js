define(['jquery', 'server', 'commons', 'constant', 'appContext', 'appModal'], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var jds = server.JDS;
  var OpinionEditor = function (workView, options) {
    var _self = this;
    _self.workView = workView;
    _self.options = options;
    // 编辑器占位符
    _self.editorPlaceholder = options.editorPlaceholder;
    // 编辑器Input选择器
    _self.editorSelector = '.wf-opinion-summernote';
    // 文本编辑器选择器
    _self.textareaSlector = '.summernote';
    // 签署意见快照占位符
    _self.snapshotPlaceholder = options.snapshotPlaceholder;
    // 签署意见快照Input选择器
    _self.snapshotInputSelector = "input[name='sign_opinion_text']";
    // 签署的意见立场文本label
    // 签署的意见立场值value
    // 签署的意见内容text
    // 初始化传入的意见内容
    _self.opinion = {
      label: options.opinionLabel,
      value: options.opinionValue,
      text: options.opinionText
    };
    // 是否需要意见立场
    _self.isRequiredOpinionValue = false;
    _self.editorCreated = false;
  };
  $.extend(OpinionEditor.prototype, {
    // 初始化
    init: function () {},
    // 显示
    show: function () {},
    // 隐藏
    hide: function () {},
    // 打开签署意见
    openToSignIfRequired: function (options) {},
    // 签署
    sign: function (data) {
      this.opinion = data;
    },
    // 存储签署意见
    store: function () {
      var _self = this;
      var opinion = _self.collectOpinion();
      var storage = commons.StorageUtils.getStorage();
      if (storage) {
        var key = getStorageKey(_self.workView);
        storage.setItem(key, opinion.text);
        storage.setItem('opinionvalue', opinion.value);
      }
    },
    // 还原签署意见
    restore: function () {
      var _self = this;
      var storage = commons.StorageUtils.getStorage();
      var key = getStorageKey(_self.workView);
      var text = storage.getItem(key);
      var opinionvalue = storage.getItem('opinionvalue');
      if (StringUtils.isNotBlank(text)) {
        _self.opinion.text = text;
        _self.restoreText = text;
      }
      if (StringUtils.isNotBlank(opinionvalue)) {
        _self.opinion.value = opinionvalue;
      }
      if (StringUtils.isBlank(_self.opinion.text) || StringUtils.isBlank(_self.opinion.value)) {
        var flowInstUuid = _self.workView.getWorkData().flowInstUuid;
        var taskInstUuid = _self.workView.getWorkData().taskInstUuid;
        $.ajax({
          url: ctx + '/proxy/api/workflow/work/getTaskOperationTemp',
          type: 'GET',
          async: false,
          dataType: 'json',
          data: {
            flowInstUuid: flowInstUuid,
            taskInstUuid: taskInstUuid
          },
          success: function (result) {
            if (result.data) {
              _self.opinion.text = result.data.opinionText ? result.data.opinionText : _self.opinion.text;
              _self.opinion.value = result.data.opinionValue ? result.data.opinionValue : _self.opinion.value;
              _self.opinion.label = result.data.opinionLabel ? result.data.opinionLabel : _self.opinion.label;
            }
          }
        });
      }
    },
    // 清空签署意见
    clear: function () {
      var _self = this;
      var storage = commons.StorageUtils.getStorage();
      var key = getStorageKey(_self.workView);
      storage.removeItem(key);
      storage.removeItem('opinionvalue');
    },
    // 收集签署的意见
    collectOpinion: function () {},
    // 获取签署的意见
    getOpinion: function () {
      return this.opinion;
    },
    // 是否需要签署意见
    isRequiredSignOpinion: function () {
      return true;
    },
    // 是否需要选择意见立场
    isRequiredOpinionPosition: function () {},
    // 是否需要提交签署意见
    isRequiredSubmitOpinion: function () {
      return this._isRequiredOpinion(this.options.required.submit);
    },
    // 是否需要撤回签署意见
    isRequiredCancelOpinion: function () {
      if (this.options.required.cancel === false) {
        return false;
      }
      var _self = this;
      if (_self.workView.isDone() === false) {
        return false;
      }
      if (StringUtils.isBlank(_self.opinion.text)) {
        return true;
      }
      return false;
    },
    // 是否需要转办必填意见
    isRequiredTransferOpinion: function () {
      return this._isRequiredOpinion(this.options.required.transfer);
    },
    // 是否需要会签必填意见
    isRequiredCounterSignOpinion: function () {
      return this._isRequiredOpinion(this.options.required.counterSign);
    },
    // 是否需要退回必填意见
    isRequiredRollbackOpinion: function () {
      return this._isRequiredOpinion(this.options.required.rollback);
    },
    // 是否需要催办必填意见
    isRequiredRemindOpinion: function () {
      if (this.options.required.remind === false) {
        return false;
      }
      var _self = this;
      if (_self.workView.isSupervise() === false) {
        return false;
      }
      if (StringUtils.isBlank(_self.opinion.text)) {
        return true;
      }
      return false;
    },
    // 是否需要特送个人必填意见
    isRequiredHandOverOpinion: function () {
      if (this.options.required.handOver === false) {
        return false;
      }
      var _self = this;
      if (_self.workView.isMonitor() === false) {
        return false;
      }
      if (StringUtils.isBlank(_self.opinion.text)) {
        return true;
      }
      return false;
    },
    // 是否需要特送环节必填意见
    isRequiredGotoTaskOpinion: function () {
      if (this.options.required.gotoTask === false) {
        return false;
      }
      var _self = this;
      if (_self.workView.isMonitor() === false) {
        return false;
      }
      if (StringUtils.isBlank(_self.opinion.text)) {
        return true;
      }
      return false;
    },
    _isRequiredOpinion: function (required) {
      if (required === false) {
        return false;
      }
      var _self = this;
      if (!(_self.workView.isDraft() || _self.workView.isTodo())) {
        return false;
      }
      if (_self.isRequiredOpinionValue && StringUtils.isBlank(_self.opinion.value)) {
        return true;
      }
      if (StringUtils.isBlank(_self.opinion.text)) {
        return true;
      }
      return false;
    },
    getUsedAndCommonOpinion: function () {
      // 获取常用和最近使用意见
      var _self = this;
      var workData = _self.workView.getWorkData();
      var flowDefUuid = workData.flowDefUuid;
      var taskId = workData.taskId;
      $.get({
        url: ctx + '/proxy/api/workflow/work/getCurrentUserOpinion2Sign/' + flowDefUuid + '/' + taskId,
        //        data: {
        //          flowDefUuid: flowDefUuid,
        //          taskId: taskId
        //        },
        async: false,
        success: function (result) {
          var userOpinions = result.data;
          _self.userOpinions = userOpinions;
          _self.isRequiredOpinionValue = _self.userOpinions.enableOpinionPosition && _self.userOpinions.requiredOpinionPosition;
          var userOpinionCategories = _self.userOpinions.userOpinionCategories || [];
          var commonOpinionCategory = null;
          $.each(userOpinionCategories, function (i, userOpinionCategory) {
            if (userOpinionCategory.id == '001') {
              commonOpinionCategory = userOpinionCategory;
            }
          });
          _self.userOpinions.commonOpinions = commonOpinionCategory.opinions;
          _self.userOpinions.commonOpinionCategory = commonOpinionCategory;
        }
      });
    }
  });

  // 获取签署意见存储的key
  function getStorageKey(workView) {
    var workData = workView.getWorkData();
    var flowDefUuid = workData.flowDefUuid;
    var flowInstUuid = workData.flowInstUuid;
    var taskInstUuid = workData.taskInstUuid;
    var userId = workData.userId || '';
    if (workView.isDraft()) {
      return flowDefUuid + userId;
    }
    if (appContext.isMobileApp()) {
      return taskInstUuid + userId;
    }
    return flowInstUuid + userId;
  }
  return OpinionEditor;
});
