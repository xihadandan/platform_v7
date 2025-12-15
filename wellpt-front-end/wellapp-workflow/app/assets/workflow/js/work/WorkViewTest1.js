define(['constant', 'commons', 'appContext', 'appModal', 'WorkView'], function (constant, commons, appContext, appModal, WorkView) {
  // 流程二开测试
  var WorkViewTest = function () {};
  commons.inherit(WorkViewTest, WorkView, {
    // 流程初始化
    init: function (options) {
      var _self = this;
      _self._superApply(arguments);
    },
    // 准备初始化表单
    prepareInitDyform: function (dyformOptions) {
      var _self = this;
      // 调用父类方法
      _self._superApply(arguments);
    },
    // 表单初始化成功
    onDyformInitSuccess: function () {
      var _self = this;
      // 调用父类方法
      _self._superApply(arguments);
    },
    // 重写提交方法
    submit: function () {
      var _self = this;
      var workData = _self.getWorkData();
      // 调用父类提交方法
      _self._superApply(arguments);
    }
  });
  return WorkViewTest;
});
