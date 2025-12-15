define(['constant', 'commons', 'server', 'appContext', 'appModal', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  ListViewWidgetDevelopment
) {
  var JDS = server.JDS;
  var UrlUtils = commons.UrlUtils;
  var StringBuilder = commons.StringBuilder;
  // 流程签署意见_校验规则管理_视图组件二开
  var AppWorkFlowOpinionRuleListViewWidgetDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppWorkFlowOpinionRuleListViewWidgetDevelopment, ListViewWidgetDevelopment, {
    // 获取要附加的自定义参数
    getCustomUrlParams: function (index, row) {
      return {};
    },
    afterRender: function (options, configuration) {
      var _self = this;

      _self.bindEvent();
    },
    bindEvent: function () {
      var _self = this;
      var $pageElement = _self.widget.pageContainer.element;

      $pageElement.on('AppWorkFlowOpinionRuleList.Refresh', function () {
        _self.getWidget().refresh();
      });
    },
    //删除
    btn_opinion_rule_list_view_delete: function (options) {
      var _self = this;
      var selection = _self.getWidget().getSelections();
      var params = options.params || {};
      var ruleUuids = [];
      ruleUuids.push(selection[0].uuid);

      _self._isReferencedHandler(ruleUuids, params.confirmMsg);
    },
    //批量删除
    btn_opinion_rule_list_view_deletes: function (options) {
      var _self = this;
      var selection = _self.getSelection();
      if (selection.length === 0) {
        return;
      }
      var params = options.params || {};

      var ruleUuids = [];
      $.each(selection, function (i, item) {
        ruleUuids.push(item.uuid);
      });
      _self._isReferencedHandler(ruleUuids, params.confirmMsg);
    },
    _deleteRuleList: function (ruleUuids, _self) {
      JDS.restfulPost({
        url: ctx + '/api/opinion/rule/delete',
        data: { uuids: ruleUuids },
        contentType: 'application/x-www-form-urlencoded',
        mask: true,
        success: function (result) {
          appModal.success('删除成功！');
          _self.refresh();
        }
      });
    },
    getSelection: function (multiple) {
      var _self = this;
      var selection = _self.getWidget().getSelections();
      if (selection.length === 0) {
        appModal.error('请选择记录！');
        return [];
      }
      return selection;
    },
    _isReferencedHandler: function (ruleUuids, confirmMsg) {
      var confirmMsg = confirmMsg || '校验规则正被使用，删除后使用的流程将不再按规则进行校验，是否确定？';
      var _self = this;
      JDS.restfulPost({
        url: ctx + '/api/opinion/rule/isReferencedByOpinionRuleUuids',
        data: { opinionRuleUuids: ruleUuids },
        contentType: 'application/x-www-form-urlencoded',
        success: function (result) {
          if (result.data) {
            //存在引用
            appModal.confirm(confirmMsg, function (result) {
              if (result) {
                _self._deleteRuleList(ruleUuids, _self);
              }
            });
          } else {
            appModal.confirm('确定要删除吗？', function (result) {
              if (result) {
                // 执行后端发送操作
                _self._deleteRuleList(ruleUuids, _self);
              }
            });
          }
        }
      });
    }
  });
  return AppWorkFlowOpinionRuleListViewWidgetDevelopment;
});
