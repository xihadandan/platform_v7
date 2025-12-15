define([
  'mui',
  'commons',
  'constant',
  'server',
  'formBuilder',
  'mui-WorkFlowMobileListViewDevelopmentBase',
  'WorkView',
  'WorkViewProxy',
  'formBuilder'
], function ($, commons, constant, server, formBuilder, WorkFlowMobileListViewDevelopmentBase, WorkView, workView, formBuilder) {
  // 工作流程_抄送
  var WorkFlowCCMobileListViewDevelopment = function () {
    WorkFlowMobileListViewDevelopmentBase.apply(this, arguments);
  };
  commons.inherit(WorkFlowCCMobileListViewDevelopment, WorkFlowMobileListViewDevelopmentBase, {
    getWorkService: function () {
      return 'mobileWorkService.getRead';
    },
    getWorkServiceParams: function (data) {
      var params = this._superApply(arguments);
      params.push(true);
      return params;
    },
    onAddLiElement: function (index, data, $element) {
      if ($element.classList.contains('unread')) {
        $element.childNodes[0].childNodes[1].remove();
      } else {
        $element.childNodes[0].childNodes[0].remove();
      }
    }
  });

  return WorkFlowCCMobileListViewDevelopment;
});
