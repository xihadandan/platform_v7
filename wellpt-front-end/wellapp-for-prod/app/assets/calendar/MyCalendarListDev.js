define(['jquery', 'commons', 'constant', 'server', 'ListViewWidgetDevelopment', 'appModal', 'wSelect2Group'], function (
  $,
  commons,
  constant,
  server,
  ListViewWidgetDevelopment,
  appModal
) {
  var JDS = server.JDS;
  var currUser = server.SpringSecurityUtils.getUserDetails();

  var MyCalendarListDev = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(MyCalendarListDev, ListViewWidgetDevelopment, {
    init: function () {
      console.log('MyCalendarListDev.init');
      //检查头部是否存在新建日历本的按钮，如果存在，则触表格中配置的新建按钮的事件
      $('#btn_new_calendar').click(function () {
        $('.btn_class_btn_add_calendar').trigger('click');
      });
    }
  });
  return MyCalendarListDev;
});
