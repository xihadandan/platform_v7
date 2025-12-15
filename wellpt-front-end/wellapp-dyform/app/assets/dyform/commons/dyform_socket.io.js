(function (root, factory) {
  'use strict';
  var ctx = ctx != null && ctx != undefined ? ctx : '';
  if (typeof define === 'function' && define.amd) {
    define(['jquery', 'socket.io', 'appModal'], factory);
  } else if (typeof exports === 'object') {
    module.exports = factory(require('jquery'), require('socket.io'));
  } else {
    $.ajax({ url: '/static/js/socket.io/socket.io.slim.min.js', async: false, dataType: 'script' });
    factory(root.jQuery);
  }
})(this, function init($, io, appModal) {
  'use strict';
  function getCookie(name) {
    var reg = new RegExp('(^| )' + name + '=([^;]*)(;|$)');
    var arr = document.cookie.match(reg);
    return arr ? unescape(arr[2]) : null;
  }
  var $div = $('.multi-user-operation-container');
  var $badge = $div.find('.badge');
  var $tip = $div.find('.tip');
  var $multiUl = $div.find('.mutli-user-operation-list');
  var isWorkflow = $('#wf_taskInstUuid').length > 0;
  if (
    $multiUl.length == 0 ||
    (isWorkflow && ['TODO'].indexOf($('#wf_aclRole').val()) == -1) // 流程待办、监控情况下
  ) {
    return;
  }

  // cookie.current.userId
  var userid = getCookie('cookie.current.userId'),
    user = [];

  var operationName = $('#wf_canEditForm').val() == 'true' ? '编辑' : '查阅';
  if ($('#wf_aclRole').val() === 'MONITOR') {
    // 监控者只能查阅
    operationName = '查阅';
  }
  var multiSubmitType = $('#wf_multiSubmitType').val();
  $div.on('click', function (e) {
    if ($(e.target).parents('.tip').length > 0) {
      $multiUl.hide();
      e.stopPropagation();
      return;
    }
    if ($multiUl.is(':visible')) {
      e.stopPropagation();
      return;
    }
    $(document).one('click', function (e) {
      $multiUl.hide();
    });
    $multiUl.css('right', $badge.css('right'));
    $multiUl.show();
    $multiUl.empty();
    var $toplabelli = $('<li>').append($('<label>', { class: 'first-li-label' }).text('当前多人同时' + operationName));
    if (multiSubmitType === 'isAnyone') {
      $toplabelli.append($('<br>'), $('<label>', { class: 'first-li-label' }).text('只需一人办理'));
    }
    $multiUl.append($toplabelli);
    for (var i = 0, len = user.length; i < len; i++) {
      if (user[i].photoUuid) {
        $multiUl.append(
          $('<li>', { title: user[i].mainJobPath }).append(
            $('<img>', {
              src: '/org/user/view/photo/' + user[i].photoUuid
            }),
            $('<label>', { class: 'user-name-label' }).text(user[i].userName + (user[i].userId == userid ? ' (我)' : ''))
          )
        );
      } else {
        $multiUl.append(
          $('<li>', { title: user[i].mainJobPath }).append(
            $('<div>', { class: 'default-avatar' }).append(
              $('<i>', {
                class: 'ui-wIcon iconfont icon-wsbs-dengluyonghumingzhanghao'
              })
            ),
            $('<label>', { class: 'user-name-label' }).text(user[i].userName + (user[i].userId == userid ? ' (我)' : ''))
          )
        );
      }
    }
    e.stopPropagation();
  });

  $('.icon-del').on('click', function () {
    $tip.hide();
  });
  var startSocketMonitor = function () {
    var _socketid = null;
    var _userid = null;
    var socket = io('/wellapp-dyform', {
      transports: ['websocket', 'polling'],
      query: {
        userId: userid,
        dataUuid: $('#wf_taskInstUuid').val() || $('#dms_dataUuid').val()
      }
    }); //ip
    var connectFlg = false;
    socket.on('connect', function () {
      socket.on('socketid', function (res) {
        _socketid = res.socketid;
        _userid = res.userid;
      });
      // 连接上
      console.log('wellpt-dyform socket.io connected!');
      connectFlg = true;
      var showMultiUserOperation = false;
      var noTip = false;
      // 接收离线消息数目的通知
      socket.on('updateUserEditDyform', function (res) {
        // 是否发生数量变更
        console.log(res);
        user = res.user;
        if (user.length <= 1 && !showMultiUserOperation) {
          $div.hide();
          noTip = true;
          return;
        }
        $div.show();
        $badge.text(user.length);
        if (isWorkflow && user.length > 1 && !noTip) {
          if (multiSubmitType === 'isAnyone') {
            appModal.warning({ message: `当前${user.length}人同时${operationName}，只需一个办理`, autoClose: false });
          } else if (multiSubmitType === 'isMultiSubmit') {
            appModal.warning({ message: `当前${user.length}人同时${operationName}`, autoClose: false });
          }
          noTip = true;
        }
        showMultiUserOperation = true; // 一旦显示图标后，各个用户的待办流程详情中一直显示该图标
        var maxCnt = 3,
          userAppended = [];
        $div.find('[userid]').remove();
        if (userAppended.length < maxCnt) {
          user = _.sortByOrder(user, ['issued'], ['desc']);
          for (var i = 0, len = user.length; i < len; i++) {
            if (userAppended.indexOf(user[i].userId) == -1 && userAppended.length < maxCnt) {
              userAppended.push(user[i].userId);
              // 计算偏移量
              var left = -23 * (userAppended.length - 1) + 'px';
              var zindex = -1 * (userAppended.length - 1);
              if (user[i].photoUuid) {
                $('<img>', {
                  class: 'toolbar-user-icon',
                  src: '/org/user/view/photo/' + user[i].photoUuid,
                  userid: user[i].userId,
                  style: 'left:' + left + ';z-index:' + zindex
                }).insertBefore($badge);
              } else {
                $('<div>', {
                  class: 'default-avatar toolbar-user-icon',
                  userid: user[i].userId,
                  style: 'left:' + left + ';z-index:' + zindex
                })
                  .append(
                    $('<i>', {
                      class: 'ui-wIcon iconfont icon-wsbs-dengluyonghumingzhanghao'
                    })
                  )
                  .insertBefore($badge);
              }
            }
          }
        }
        $badge.show();
        if ($multiUl.is(':visible')) {
          $multiUl.hide();
          $badge.trigger('click');
        }
        if ($tip.is(':hidden') && user.length > 1) {
          if (connectFlg) {
            connectFlg = false;
          } else {
            var $span = $tip.find('span');
            $tip.css('right', $badge.css('right'));
            $span.html('新的办理人员');
            $tip.show();
          }
        }
      });

      socket.on('dyformDataChanged', function (res) {
        if (res.userId != _userid) {
          if (res.tipType === 'alert') {
            appModal.alert(res.operationTip);
          } else {
            var $span = $tip.find('span');
            $tip.css('right', $badge.css('right'));
            $span.html(res.operationTip);
            $tip.show();
          }
          $multiUl.parents('.widget-toolbar').find('button').prop('disabled', true).addClass('w-disable-btn');
          if (isWorkflow) {
            //签署意见的按钮
            $('#wf_sign_opinion_content button').prop('disabled', true).addClass('w-disable-btn');
          }
        }
      });
    });
  };
  setTimeout(function () {
    startSocketMonitor();
  }, 3000);
});
