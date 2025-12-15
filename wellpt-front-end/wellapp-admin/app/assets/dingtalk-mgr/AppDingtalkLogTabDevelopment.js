define(['constant', 'commons', 'server', 'BootstrapTabsWidgetDevelopment'], function (
  constant,
  commons,
  server,
  BootstrapTabsWidgetDevelopment
) {
  'use strict';
  var AppDingtalkLogTabDevelopment = function () {
    BootstrapTabsWidgetDevelopment.apply(this, arguments);
  };
  // 要显示的tab种类组
  var tabActives = [
    ['部门', '0', '1,2'],
    ['人员', '1', '0,2'],
    ['人员和职位的关系', '2', '0,1'],
    ['部门、人员', '0,1', '2'],
    ['部门、人员和职位的关系', '0,2', '1'],
    ['人员、人员和职位的关系', '1,2', '0'],
    ['部门、人员、人员和职位的关系', '0,1,2', '']
  ];
  commons.inherit(AppDingtalkLogTabDevelopment, BootstrapTabsWidgetDevelopment, {
    afterRender: function () {
      var $container = this.widget.element;
      this.getDetail($container);
    },
    // 同步详情
    getDetail: function ($container) {
      var sync_content = null;
      var that = this;
      var params = this.getWidgetParams();
      var api = params.apiType == 'org' ? '/pt/dingtalk/getOrgSyncLogDetail' : '/pt/dingtalk/getEventCallBackDetail';
      $.ajax({
        url: ctx + api,
        type: 'POST',
        data: {
          uuid: params.P_UUID
        },
        async: true,
        success: function (result) {
          sync_content = result.data.syncContent;
          // 判断同步内容，展示tab
          var lis = $container.find('ul li[role="presentation"]');
          var parentElement = $container.closest('.ui-wBootstrapTabs');
          if (!sync_content) {
            $.each(lis, function (index, item) {
              $(item).removeClass('active').hide();
              var href = $(item).find('a').attr('href');
              $(href, parentElement).removeClass('active').hide();
            });
          } else {
            for (var i = 0; i < tabActives.length; i++) {
              if (sync_content == tabActives[i][0]) {
                // lis.eq(tabActives[i][1]).addClass('active').show().siblings().removeClass('active').hide();
                // lis.eq(tabActives[i][1]).find('a').trigger('click');
                var showIndex = that.setTabActive(tabActives[i][1], tabActives[i][2], lis);
                // var href = lis.eq(tabActives[i][1]).find('a').attr('href');
                var $a = lis.eq(showIndex).find('a[role=tab]');
                $a.trigger('click');

                // $(href, parentElement).addClass('active').show().siblings().removeClass('active').hide();
                break;
              }
            }
          }
        }
      });
    },

    // 设置tab-active
    setTabActive: function (showStr, hideStr, lis) {
      var showArr = [];
      var hideArr = [];
      var showIndex = 0;
      if (showStr !== '') {
        showArr = showStr.split(',');
        for (var i = 0; i < showArr.length; i++) {
          lis.eq(showArr[i]).show();
        }

        showIndex = showArr[0];
      }
      if (hideStr !== '') {
        hideArr = hideStr.split(',');
        for (var i = 0; i < hideArr.length; i++) {
          lis.eq(hideArr[i]).hide();
        }
      }

      return showIndex;
    }
  });
  return AppDingtalkLogTabDevelopment;
});
