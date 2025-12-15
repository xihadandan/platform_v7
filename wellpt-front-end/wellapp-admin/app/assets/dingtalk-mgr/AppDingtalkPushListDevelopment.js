define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment'], function (constant, commons, server, ListViewWidgetDevelopment) {
  var AppDingtalkPushListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };

  commons.inherit(AppDingtalkPushListDevelopment, ListViewWidgetDevelopment, {
    afterRender: function () {
      var _self = this;
      $(_self.widget.element).prepend(
        "<div style='line-height:1;margin: 10px;color:#666;'><i class='iconfont icon-ptkj-xinxiwenxintishi' style='margin-right: 5px;font-size:20px;vertical-align: middle;'></i>开启后，才推送待办消息，1个用户的1个待办消息为1条记录，根据动作流转，更新记录的数据，待办处理完成且推送钉钉成功时，将从列表记录中删除</div>"
      );
      var $push = $("<div>消息推送：<input type='checkbox' id='push' class='switch'><label for='push' style='margin:0;'></label></div>");
      $(_self.widget.element).prepend($push);
      $.ajax({
        url: ctx + '/pt/dingtalk/getConfig',
        type: 'GET',
        dataType: 'json',
        success: function (result) {
          var bean = result.data || {};
          var checked = bean['push.mode'] === 'sync' || bean['push.mode'] === 'async';

          $push
            .find('#push')
            .prop('checked', checked)
            .trigger('change')
            .on('change', function (event) {
              bean['push.mode'] = this.checked ? 'async' : 'off';
              var that = this;
              var title = this.checked ? '开启' : '关闭';
              appModal.confirm('确定要' + title + '消息推送吗？', function (result) {
                if (result) {
                  $.ajax({
                    url: ctx + '/pt/dingtalk/saveConfig',
                    type: 'POST',
                    dataType: 'json',
                    data: {
                      jsonParams: JSON.stringify(bean)
                    },
                    success: function (result) {
                      // _self.refresh();
                      appModal.success('保存成功');
                    },
                    error: function (jqXHR) {
                      appModal.error('保存失败');
                    }
                  });
                } else {
                  $(that).prop('checked', !that.checked);
                }
              });
            });
        },
        error: function (jqXHR) {}
      });
    }
  });
  return AppDingtalkPushListDevelopment;
});
