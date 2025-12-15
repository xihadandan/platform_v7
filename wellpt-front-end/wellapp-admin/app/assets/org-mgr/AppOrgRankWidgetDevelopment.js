define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppOrgRankWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOrgRankWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var formBean = {
        uuid: null,
        id: null,
        name: null,
        systemUnitId: null,
        code: null
      };
      $('#org_rank_form').json2form(formBean);
      var validator = $.common.validation.validate('#org_rank_form', 'multiOrgJobRank');
      var uuid = GetRequestParam().uuid;
      if (uuid) {
        showJobRankInfo(uuid);
      }
      $('#org_rank_save').click(function () {
        if (!validator.form()) {
          return false;
        }
        $('#org_rank_form').form2json(formBean);
        formBean.systemUnitId = formBean.systemUnitId || SpringSecurityUtils.getCurrentUserUnitId();

        var url = '/api/org/multi/' + (formBean.uuid ? 'modifyJobRank' : 'addJobRank');
        $.ajax({
          type: 'POST',
          url: url,
          dataType: 'json',
          data: formBean,
          success: function (data) {
            appModal.success('保存成功！');
            appContext.getNavTabWidget().closeTab();
          }
        });
      });

      function showJobRankInfo(uuid) {
        $.ajax({
          url: ctx + '/api/org/multi/getJobRank',
          type: 'get',
          data: {
            uuid: uuid
          },
          success: function (result) {
            formBean = result.data;
            $('#org_rank_form').json2form(formBean);
          }
        });
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppOrgRankWidgetDevelopment;
});
