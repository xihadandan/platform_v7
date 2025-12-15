define(['jquery', 'commons', 'constant', 'server', 'appModal'], function ($, commons, constant, server, appModal) {
  var _self = this;
  var BtnFetchEmail = function (options) {
    //通过trigger来刷新对应的徽章数量
    refreshBadgeNum = function () {
      this.appContext.pageContainer.trigger('AppEmail.Change');
    };
    var sjx = null;
    for (var key in options.ui.menuItemMap) {
      if (options.ui.menuItemMap[key].name == '收件箱') {
        sjx = options.ui.menuItemMap[key];
        break;
      }
    }

    appModal.showMask('收信中...');
    $.ajax({
      url: ctx + '/webmail/refush', // 服务器的地址
      type: 'POST', // 请求类型
      success: function (result) {
        appModal.hideMask();
        refreshBadgeNum();
        if (result.data.receiveFailCount) {
          appModal.alert({
            title: '收信失败',
            message: '邮件空间已满，有' + result.data.receiveFailCount + '封待接收，请彻底删除部分邮件后，再点击”收邮件“'
          });
        } else {
          if (result.data.unReadCount > 0) {
            appModal.success({ title: '收信成功', message: '目前还有' + result.data.unReadCount + '封未读邮件！' });
          } else {
            appModal.success({ title: '收信成功', message: '暂无新邮件！' });
          }
        }
        if (result.data.receiveMailAction == 0) {
          var sjxdm = $(".metismenu ul li[menuid='" + sjx.uuid + "']");
          if (sjxdm) {
            var syj = $('.metismenu ul li .nav-menu-item .main-nav-menu-item .nav-menu-active');
            syj.removeClass('nav-menu-active');
            sjxdm.addClass('nav-menu-active');
            sjxdm.children('a').click().focus();
          }
        }
      },
      error: function (jqXHR) {
        appModal.alert('收信失败，请联系管理员');
        appModal.hideMask();
      }
    });
  };

  return BtnFetchEmail;
});
