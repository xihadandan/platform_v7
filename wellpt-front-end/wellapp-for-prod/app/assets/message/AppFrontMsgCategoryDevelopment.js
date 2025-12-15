define(['constant', 'commons', 'server', 'HtmlWidgetDevelopment'], function (constant, commons, server, HtmlWidgetDevelopment) {
  // 页面组件二开基础
  var AppFrontMsgCategoryDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppFrontMsgCategoryDevelopment, HtmlWidgetDevelopment, {
    // 组件初始化
    init: function () {
      getList();
      var height = $(window.top.document).find('.modal').not('.frontLoginMsgDialog').find('.modal-body').height();

      $('.msg-category-content')
        .parent('.ui-wHtml')
        .css({
          height: height - 47 + 'px'
        });
      $('.msg-category-content').parents('.container-fluid').css({ paddingBottom: 0 });
      $('.msg-category-content')
        .parent('.ui-wHtml')
        .siblings('.ui-wPanel')
        .find('.tab-content')
        .css({
          maxHeight: height - 100 + 'px',
          overflow: 'auto',
          clear: 'both'
        });

      $('#searchMsgCategory').click(function () {
        var val = $('#fullName').val();
        getList(val);
      });

      $('#fullName')
        .keyup(function () {
          if ($(this).val() != '') {
            $('#deleteMsgCategory').show();
          }
        })
        .keypress(function (e) {
          if (e.keyCode == 13) {
            getList($(this).val());
          }
        });

      $('#deleteMsgCategory').click(function () {
        $('#fullName').val('');
        $(this).hide();
        getList();
      });

      $('.msg-category-folder')
        .off()
        .click(function () {
          var width = $(this).parents('.msg-receive-content').width();
          var $content = $(this).parents('.msg-receive-content');
          if ($(this).find('i').hasClass('icon-ptkj-youshouzhan')) {
            $content.find('.ui-wHtml').animate({ width: '231px' }, 300).find('.msg-category-wrap').show();
            $content.find('#msg_category_content').animate({ width: '220px' }, 300);
            $content.find('.panel-default').animate({ width: width - 231 + 'px' }, 300);
            $(this).find('i').removeClass('icon-ptkj-youshouzhan').addClass('icon-ptkj-zuoshouzhan');
          } else {
            $content.find('.ui-wHtml').animate({ width: '11px' }, 300).find('.msg-category-wrap').hide();
            $content.find('#msg_category_content').animate({ width: '0' }, 300);
            $content.find('.panel-default').animate({ width: width - 11 + 'px' }, 300);
            $(this).find('i').addClass('icon-ptkj-youshouzhan').removeClass('icon-ptkj-zuoshouzhan');
          }
        });

      $('#msg_category_tree')
        .off('click', '.msg-category-item')
        .on('click', '.msg-category-item', function (e) {
          e.stopPropagation();
          $(this).addClass('hasSelectCate').siblings().removeClass('hasSelectCate');
          var uuid = $(this).data('uuid');
          var $receiveMsg = $(this).parents('.msg-receive-content');
          var index = $receiveMsg.find('.panel-tab-content.active').data('index');
          if (index == 1) {
            $receiveMsg.find('.panel-tab[data-index=0]').find('a').trigger('click');
          }
          var msgFormateTable = $receiveMsg.find('.panel-tab-content.active .ui-wBootstrapTable').attr('id');
          if (uuid == 'all') {
            $('#' + msgFormateTable)
              .wBootstrapTable('removeParam', 'classifyUuid')
              .wBootstrapTable('refresh');
          } else {
            $('#' + msgFormateTable)
              .wBootstrapTable('addParam', 'classifyUuid', uuid)
              .wBootstrapTable('refresh');
          }
        });

      function getList(val) {
        $.ajax({
          type: 'get',
          async: true,
          data: { name: val || '' },
          url: ctx + '/proxy/api/message/classify/facadeQueryList',
          success: function (result) {
            var res = result;
            if (res.code == 0) {
              var data = res.data;
              var lis = '';
              for (var i = 0; i < data.length; i++) {
                var icon = data[i].icon ? data[i].icon : 'iconfont icon-xmch-wodexiaoxi';
                var background = data[i].iconBg ? data[i].iconBg : '#64B3EA';
                lis +=
                  "<li class='msg-category-item' data-uuid='" +
                  data[i].uuid +
                  "' data-iconbg='" +
                  background +
                  "'>" +
                  "<span class='" +
                  icon +
                  "' style='background:" +
                  background +
                  "'></span>" +
                  "<span class='msg-category-subject'>" +
                  data[i].name +
                  '</span>';
                if (data[i].unReadCount > 0) {
                  lis += "<span class='msg-badge'>" + data[i].unReadCount + '</span>';
                }
                lis += '</li>';
              }
              $('#msg_category_tree').html(lis);
            }
          },
          error: function () {}
        });
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppFrontMsgCategoryDevelopment;
});
