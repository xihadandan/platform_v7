define(['constant', 'commons', 'server', 'appModal', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appModal,
  ListViewWidgetDevelopment
) {
  var AppUserLogDetailDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppUserLogDetailDevelopment, ListViewWidgetDevelopment, {
    beforeRender: function (options, configuration) {
      var uuid = GetRequestParam().uuid;
      var widget = this.getWidget();
      var otherConditions = [];
      var condition = {
        columnIndex: '日志ID',
        value: uuid,
        type: 'eq'
      };
      otherConditions.push(condition);

      widget.addOtherConditions(otherConditions);
    },
    onLoadSuccess: function () {
      var $element = this.widget.element;
      $element.parents('body,.container-fluid').css({
        background: '#fff'
      });
      $element.find('.fixed-table-body tbody tr').css({
        cursor: 'text'
      });
    }
  });
  return AppUserLogDetailDevelopment;
});
