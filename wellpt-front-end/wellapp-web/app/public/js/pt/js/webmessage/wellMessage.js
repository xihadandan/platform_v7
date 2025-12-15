(function (root, factory) {
  'use strict';
  var ctx = ctx != null && ctx != undefined ? ctx : '';
  if (typeof define === 'function' && define.amd) {
    define(['jquery', 'ckeditor', 'SockJS', 'stomp'], factory);
  } else if (typeof exports === 'object') {
    module.exports = factory(require('jquery'), require('ckeditor'), require('SockJS'), require('stomp'));
  } else {
    $.ajax({ url: '/static/js/websocket/js/sockjs.js', async: false, dataType: 'script' });
    $.ajax({ url: '/static/js/websocket/js/stomp.js', async: false, dataType: 'script' });
    factory(root.jQuery);
  }
})(this, function init($) {
  'use strict';

  if (window !== top.window) {
    return;
  }

  var ctx = ctx != null && ctx != undefined ? ctx : '';
  // 消息接收websocket
  var stompClient, socket;

  var $openedDialog;

  // 回传消息
  function socketCallMsg(headers) {
    // console.log('callBackMsg:', headers);
    if (headers && headers.callBackUrl) {
      /**
       *  1. 第一个参数 url 为服务器 controller中 @MessageMapping 中匹配的URL，字符串，必须参数；
       *  2. headers 为发送信息的header，json格式，JavaScript 对象，
       *     可选参数,可以携带消息头信息，也可以做事务，如果没有，传{}
       *  3. body 为发送信息的 body，字符串，可选参数
       */
      stompClient.send(headers.callBackUrl, {}, headers.callBackJson);
    }
  }

  // websocket 配置
  function websocketConfig() {
    /*
     * 1. 连接url为endpointChat的endpoint,对应后台WebSoccketConfig的配置
     * 2. SockJS 所处理的URL 是 "http://" 或 "https://" 模式，而不是 "ws://" or  "wss://"
     */
    var SockJS = window.requirejs == null ? window.SockJS : requirejs('SockJS');
    function getCookie(name) {
      var reg = new RegExp('(^| )' + name + '=([^;]*)(;|$)');
      var arr = document.cookie.match(reg);
      return arr ? unescape(arr[2]) : null;
    }

    var backendUrl = top.backendUrl || getCookie('backend.url');
    socket = new SockJS(backendUrl + '/wellSocket?jwt=' + getCookie('jwt'));

    // 获取 STOMP 子协议的客户端对象
    stompClient = Stomp.over(socket);
    /*
     * 1. 获取到stomp 子协议后，可以设置心跳连接时间，认证连接，主动断开连接
     * 2，连接心跳有的版本的stomp.js 是默认开启的，这里我们不管版本，手工设置
     * 3. 心跳是双向的，客户端开启心跳，必须要服务端支持心跳才行
     * 4. heartbeat.outgoing 表示客户端给服务端发送心跳的间隔时间
     * 5. 客户端接收服务端心跳的间隔时间，如果为0 表示客户端不接收服务端心跳
     */
    stompClient.heartbeat.outgoing = 10000;
    stompClient.heartbeat.incoming = 10000;
    /*
     * 1. stompClient.connect(headers, connectCallback, errorCallback);
     * 2. headers表示客户端的认证信息,多个参数 json格式存,这里简单用的httpsessionID，可以根据业务场景变更
     *    这里存的信息，在服务端StompHeaderAccessor 对象调用方法可以取到
     * 3. connectCallback 表示连接成功时（服务器响应 CONNECTED 帧）的回调方法；
     *    errorCallback 表示连接失败时（服务器响应 ERROR 帧）的回调方法，非必须；
     */
    var headers = {};

    stompClient.connect(
      headers,
      function (frame) {
        // console.log('Connected111: ' + frame);
        /*
         * 1. 订阅服务，订阅地址为服务器Controller 中的地址
         * 2. 如果订阅为公告，地址为Controller 中@SendTo 注解地址
         * 3. 如果订阅为私信，地址为setUserDestinationPrefix 前缀+@SendToUser注解地址
         *    或者setUserDestinationPrefix 前缀 + controller的convertAndSendToUser地址一致
         * 4. 这里演示为公告信息，所有订阅了的用户都能接受
         */
        stompClient.subscribe('/user/topic/callBack', function (message) {
          socketCallMsg(message.headers);
          var obj = JSON.parse(message.body);
          if (obj.type == 'inboxOnLine') {
            if (obj.data.isOnlinePopup == 'Y') {
              var messageParm = JSON.parse(obj.data.messageParm);
              if (window.requirejs != null && !localStorage.getItem('onlineMsg') && !document.hidden) {
                localStorage.setItem('onlineMsg', 'msg');
                var buttons = {};
                var title = messageParm.subject;
                var html = messageParm.body;
                var btnHtml = '';
                var events = JSON.parse(messageParm.callbackJson);

                if (events.adjusted) {
                  events = events.events;
                }

                if (events && events.length > 0) {
                  var groupBtn = {};
                  for (var i = 0; i < events.length; i++) {
                    var btn = events[i];

                    if (btn.displayLocation && $.inArray('message-modal', btn.displayLocation) < 0) {
                      continue;
                    }

                    var target = btn.target;
                    var eventHandler = btn.eventManger && btn.eventManger.eventHandler;
                    if (btn.group != '') {
                      if (!groupBtn[btn.group]) {
                        groupBtn[btn.group] = new Array();
                      }
                      groupBtn[btn.group].push(
                        "<li class='" +
                          btn.code +
                          "'><span class='well-btn-callback well-btn' data-target='" +
                          JSON.stringify(target) +
                          "' data-event='" +
                          JSON.stringify(eventHandler) +
                          "'>" +
                          btn.text +
                          '</span></li>'
                      );
                    } else {
                      var className = btn.btnLib && btn.btnLib.btnColor ? btn.btnLib.btnColor : '';
                      className += ' ';
                      className += btn.btnLib && btn.btnLib.btnInfo && btn.btnLib.btnInfo.class ? btn.btnLib.btnInfo.class : '';
                      btnHtml +=
                        "<button type='button' class='well-btn-callback well-btn " +
                        className +
                        "'  data-target='" +
                        JSON.stringify(btn.target) +
                        "' data-event='" +
                        JSON.stringify(eventHandler) +
                        "'>";
                      if (btn.btnLib && btn.btnLib.iconInfo) {
                        btnHtml += "<i class='" + btn.btnLib.iconInfo.fileIDs + "'></i>";
                      }
                      btnHtml += btn.text + '</button>';
                    }
                  }
                  if (Object.keys(groupBtn).length > 0) {
                    for (var k in groupBtn) {
                      var html2 = groupBtn[k].join('');
                      btnHtml +=
                        '<div class="btn-group" style="overflow: visible">' +
                        '<a type="button" class="well-btn w-btn-primary w-line-btn dropdown-toggle" data-toggle="dropdown">' +
                        '<span>' +
                        k +
                        '</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia"></i>' +
                        '</a>' +
                        '<ul class="dropdown-menu w-btn-dropMenu" role="menu">' +
                        html2 +
                        '</ul>' +
                        '</div>';
                    }
                  }
                  if (btnHtml != '') {
                    buttons = {
                      cancel: {
                        label: '关闭',
                        className: 'btn btn-default'
                      }
                    };
                  }
                } else if (messageParm.relatedTitle) {
                  buttons = {
                    cancel: {
                      label: '关闭',
                      className: 'btn btn-default'
                    }
                  };
                }
                var width = messageParm.popupSize == 2 ? (messageParm.popupWidth != '' ? messageParm.popupWidth : '') : 340;
                var height = messageParm.popupSize == 2 ? (messageParm.popupHeight != '' ? messageParm.popupHeight : '') : 180;
                var timer = messageParm.autoTimeCloseWin == 1 ? 3000 : '';
                var backdrop = messageParm.displayMask == 1 ? true : false;

                $openedDialog && $openedDialog.remove();
                $('.modal-backdrop').remove();
                var $msgDialog = appModal.dialog({
                  message: html,
                  title: title,
                  className: 'frontMsgDialog',
                  buttons: buttons,
                  // zIndex: 1000000000,
                  width: width,
                  height: height,
                  timer: timer,
                  backdrop: backdrop,
                  shown: function () {
                    if (btnHtml != '') {
                      $msgDialog.find('.modal-footer').html(btnHtml);
                    }
                    if (messageParm.relatedTitle) {
                      if($msgDialog.find('.modal-footer').length == 0){
                         var body = $msgDialog.find(".modal-body");
                         body.after("<div class='modal-footer'></div>");
                      }
                      $msgDialog.find('.modal-footer')
                        .show()
                        .prepend(
                          "<a class='relateUrl' target='_blank' href='" + messageParm.relatedUrl + "'>" + messageParm.relatedTitle + '</a>'
                        );
                    }
                    if (messageParm.popupPosition == 2) {
                      $msgDialog.find('.modal-dialog').css({
                        margin: 0,
                        transform: 'translate(-50%,-50%)',
                        position: 'absolute',
                        top: '50%',
                        left: '50%'
                      });
                      $msgDialog.find('.modal-footer .dropdown-menu').css({
                        bottom: 'initial',
                        top: '100%'
                      });
                    } else {
                      $msgDialog.find('.modal-header').css({
                        background: '#fff'
                      });
                      $msgDialog.find('.modal-title').css({
                        color: '#333'
                      });
                      $msgDialog.find('.close i.icon-ptkj-dacha').css({
                        color: '#999'
                      });
                      $msgDialog.find('.modal-dialog').css({
                        bottom: 0,
                        right: 0,
                        position: 'fixed',
                        margin: 0
                      });
                      if (messageParm.displayMask != 1) {
                        $msgDialog.css({
                          top: 'initial',
                          left: 'initial'
                        });
                      }
                    }

                    $msgDialog
                      .off('click', '.modal-footer button.well-btn-callback,.modal-footer li span.well-btn-callback')
                      .on('click', '.modal-footer button.well-btn-callback,.modal-footer li span.well-btn-callback', function (e) {
                        e.stopPropagation();
                        e.preventDefault();
                        var $this = $(this);
                        if ($(this).data('target') && $(this).data('event')) {
                          // 默认转发按钮事件
                          if ($this.hasClass('btnForwardMsg') || $this.parent('li').hasClass('btnForwardMsg')) {
                            btnForwardMsg(obj.data);
                            return;
                          }

                          // 默认删除按钮事件
                          if ($this.hasClass('btnDelMsg') || $this.parent('li').hasClass('btnDelMsg')) {
                            btnDelMsg(obj.data, $msgDialog);
                            return;
                          }

                          // 默认回复按钮事件
                          if ($this.hasClass('btnReplyMsg') || $this.parent('li').hasClass('btnReplyMsg')) {
                            btnReplyMsg(obj.data);
                            return;
                          }

                          var target = $(this).data('target');
                          var eventManger = $(this).data('event');

                          if ($this.hasClass('btnExpDetail') || obj.data.messageParm.indexOf('数据导出在线消息设置') > -1) {
                            var url = '/web/app/page/preview/6001a4908f9afe87a4b4b9a278bff001?pageUuid=' + eventManger.id.split('_')[2];
                            var dataUuid = JSON.parse(obj.data.messageParm).forwardDataUuid;
                            url = url + '&uuid=' + dataUuid;
                            window.open(ctx + url);
                            return;
                          }

                          if ($this.hasClass('btnImpDetail') || obj.data.messageParm.indexOf('数据导入在线消息设置') > -1) {
                            var url = '/web/app/page/preview/56111a4071fa57436e77120e3d55eb67?pageUuid=' + eventManger.id.split('_')[2];
                            var dataUuid = JSON.parse(obj.data.messageParm).forwardDataUuid;
                            url = url + '&uuid=' + dataUuid;
                            window.open(ctx + url);
                            return;
                          }

                          var opt = {
                            target: target.position,
                            targetWidgetId: target.widgetId,
                            refreshIfExists: target.refreshIfExists,
                            eventTarget: target,
                            appId: eventManger.id,
                            appType: eventManger.type,
                            appPath: eventManger.path,
                            appData: top.appContext.getCurrentUserAppData().appData
                          };
                          top.appContext.pageContainer.startApp(opt);
                        }
                        return;
                      });
                    playAudio();
                  },
                  callback: function () {
                    localStorage.removeItem('onlineMsg');
                  }
                });

                $openedDialog = $msgDialog;
              }
            }

            if (appContext.getWidgetById($('.ui-wNewHeader').attr('id'))) {
              var widget = appContext.getWidgetById($('.ui-wNewHeader').attr('id'));
              widget.refreshBadge();
            }
          } else if (obj.type == 'inboxOffLine') {
            if (window.requirejs != null && !document.hidden) {
              var count = obj.data;
              setTimeout(function () {
                $openedDialog && $openedDialog.remove();
                $('.modal-backdrop').remove();
                var loginMsgDialog = appModal.dialog({
                  message:
                    "<div style='color:#666;'>您有<span style='color:#f00;margin: 0 5px;font-size:16px;'>" +
                    count +
                    '</span>条新消息！</div>',
                  title: '消息通知',
                  className: 'frontLoginMsgDialog',
                  // zIndex: 1000000000,
                  size: 'small',
                  height: 150,
                  width: 260,
                  backdrop: false,
                  buttons: {
                    ok: {
                      label: '查看',
                      className: 'well-btn w-btn-primary',
                      callback: function () {
                        var loginType = SpringSecurityUtils.getUserDetails().loginType;
                        var url = (url = '/web/app/pt-app/pt-message.html?pageUuid=a6bd713e-c3f7-4b93-9336-d99194b20ce5');
                        if (loginType == 7) {
                          url =
                            '/web/app/pt-app/pt-message.html?pageUuid=a6bd713e-c3f7-4b93-9336-d99194b20ce5&id=246174de446c31abe74fb2285e1ffcd9&selection=myMsg';
                        }
                        var iframe =
                          '<div class="embed-responsive embed-responsive-4by3"><iframe src="' +
                          url +
                          '" class="embed-responsive-item"></iframe></div>';

                        appModal.dialog({
                          message: iframe,
                          size: 'large',
                          title: '',
                          width: '1200',
                          buttons: {}
                        });
                      }
                    },
                    cancel: {
                      label: '关闭',
                      className: 'well-btn btn-default'
                    }
                  },
                  shown: function () {
                    loginMsgDialog.removeAttr('tabindex');
                    $('body').removeClass('modal-open');
                    playAudio();
                  }
                });

                $openedDialog = loginMsgDialog;
              }, 200);
            }
          } else if (obj.type == 'countOffLine') {
            $(document).trigger("countOffLine", obj.data);
          }
        });
      },
      function (error) {
        console.log('STOMP: ' + error);
        //重复执行 链接请求
        // setTimeout(websocketConfig, 10000);
        console.log('STOMP: Reconnecting in 10 seconds');
      },
      function (close) {
        //关闭
        console.log('STOMP close: ' + close);
      }
    );

    window.addEventListener('load', function () {
      if (localStorage.getItem('onlineMsg')) {
        localStorage.removeItem('onlineMsg');
      }
    });
    window.addEventListener('beforeunload', function () {
      if (localStorage.getItem('onlineMsg')) {
        localStorage.removeItem('onlineMsg');
      }
    });
  }

  // 删除按钮对应的事件
  function btnDelMsg(msgData, $dialog) {
    appModal.confirm({
      message: '确认要删除吗?',
      callback: function (result) {
        if (result) {
          $.ajax({
            type: 'POST',
            url: ctx + '/message/content/deleteInboxMessage',
            data: 'uuids=' + msgData.uuid,
            dataType: 'text',
            success: function () {
              appModal.success('删除成功！');

              $dialog.next('.modal-backdrop').remove();
              $dialog.remove();
            }
          });
        }
      }
    });
  }

  /**
   * 已读服务
   * @param uuids
   * @param successCallback
   */
  function readMsgService(uuids, successCallback) {
    $.ajax({
      type: 'POST',
      url: ctx + '/message/content/read',
      data: 'uuids=' + uuids,
      dataType: 'text',
      success: function () {
        if ($.isFunction(successCallback)) {
          successCallback();
        }
      }
    });
  }

  // 转发按钮对应的事件
  function btnForwardMsg(msgData) {
    var dialogHtml = getForwardDialogHtml(msgData);
    var uuid = msgData['UUID'] || msgData['uuid'];
    var $forwardDialog = showSendMsgDialog(dialogHtml, '转发消息', uuid, function () {
      readMsgService([uuid]);
    });
  }

  // 回复按钮对应的事件
  function btnReplyMsg(msgData) {
    var dialogHtml = getReplyDialogHtml(msgData);
    var uuid = msgData['uuid'] || msgData['uuid'];
    var $replyDialog = showSendMsgDialog(dialogHtml, '回复消息', uuid, function () {
      readMsgService([uuid]);
    });
  }

  function prefixZero(num) {
    return ('00' + num).slice(-2);
  }

  function formatDate(num) {
    var date = new Date(num);
    var year = date.getFullYear();
    var month = prefixZero(date.getMonth() + 1);
    var day = prefixZero(date.getDate());
    var hour = prefixZero(date.getHours());
    var minute = prefixZero(date.getMinutes());
    var second = prefixZero(date.getSeconds());

    return year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second;
  }

  function getReplyDialogHtml(row) {
    var senderName = row['SENDER_NAME'] || row['senderName'];
    var subject = row['SUBJECT'] || row['subject'];
    var body = row['BODY'] || row['body'];
    var senderTime = row['SENT_TIME'] || row['sendTime'] || row['sentTime'];

    if (typeof senderTime === 'number') {
      senderTime = formatDate(senderTime);
    }
    var newSubject = 'Re:' + subject;

    var newBody = getReplybody(senderName, subject, body, senderTime);
    var relatedUrl = row['RELATED_URL'] || row['relatedUrl'];
    var relatedTitle = row['RELATED_TITLE'] || row['relatedTitle'];
    var userId = row['SENDER'] || row['sender'];
    var userName = row['SENDER_NAME'] || row['senderName'];
    var dialogHtml = getDialogHtml(userId, userName, newSubject, newBody, '', relatedUrl, relatedTitle);
    return dialogHtml;
  }

  function showSendMsgDialog(dialogHtml, dialogTitle, uuid, shownCallback) {
    $openedDialog && $openedDialog.remove();
    $('.modal-backdrop').remove();
    var $dialog = appModal.dialog({
      title: dialogTitle,
      size: 'large',
      message: dialogHtml,
      zIndex: 2000,
      buttons: {
        confirm: {
          label: '发送',
          className: 'well-btn w-btn-primary',
          callback: function () {
            return sendMessage($dialog);
          }
        },
        cancel: { label: '取消', className: 'btn-default' }
      },
      shown: function () {
        popupUser($dialog);
        initCKEditor(false);
        $.fn.modal.Constructor.prototype.enforceFocus = function () {};
        if (uuid == null) {
          initFileupload();
        } else {
          initFileuploadByViewMsg(uuid, false);
        }
        if ($.isFunction(shownCallback)) {
          shownCallback();
        }
      }
    });

    $openedDialog = $dialog;
    return $dialog;
  }

  function getReplybody(senderName, subject, body, sendtime) {
    var replybody =
      '<br/>--------------' + sendtime + '  ' + senderName + ' 在源消息中写道------<br/>主题:' + subject + '<br/>内容:<br/>' + body;
    return replybody;
  }

  // 生成转发对话框的html
  function getForwardDialogHtml(row) {
    var newSubject = 'Fw:' + (row['SUBJECT'] || row['subject']);
    var senderName = row['SENDER_NAME'] || row['senderName'];
    var subject = row['SUBJECT'] || row['subject'];
    var body = row['BODY'] || row['body'];
    var sentTime = row['SENT_TIME'] || row['sentTime'];
    if (typeof sentTime === 'number') {
      sentTime = formatDate(sentTime);
    }

    var newBody = getReplybody(senderName, subject, body, sentTime);
    var relatedUrl = row['RELATED_URL'] || row['relatedUrl'];
    var relatedTitle = row['RELATED_TITLE'] || row['relatedTitle'];
    var dialogHtml = getDialogHtml('', '', newSubject, newBody, '', relatedUrl, relatedTitle);
    return dialogHtml;
  }

  function getDialogHtml(userid, userName, subject, body, checked, relatedUrl, relatedTitle) {
    var relatedRow = '';
    if (relatedUrl != '' && relatedTitle != '' && relatedUrl != null && relatedTitle != null) {
      relatedRow =
        '<tr>' +
        "<td class='Label' width='100' align='center'></td>" +
        "<td class='value'>" +
        "<div class='td_class'>" +
        "<a target='_blank' style='color:#488cee;float:right;' href='" +
        relatedUrl +
        "'>" +
        relatedTitle +
        '</a>' +
        "<input type='hidden' id='relatedUrl' name='relatedUrl' value='" +
        relatedUrl +
        "' >" +
        "<input type='hidden' id='relatedTitle' name='relatedTitle' value='" +
        relatedTitle +
        "' >" +
        '</div>' +
        '</td>' +
        '</tr>';
    }
    var content =
      "<div id='dialog_form_content'>" +
      "<table class='table table-hover table-striped JColResizer'>" +
      '<tbody>' +
      '<tr>' +
      "<td class='Label' width='100' align='center'>收件人</td>" +
      "<td class='value'>" +
      "<div class='td_class'>" +
      "<input type='hidden' id='userId' name='userId' value='" +
      userid +
      "'/>" +
      "<input type='text' id='showUser' name='showUser' style='width:95%;' value='" +
      userName +
      "'/>" +
      '</div>' +
      '</td>' +
      '</tr>' +
      '<tr>' +
      "<td class='Label' width='100' align='center'>主题</td>" +
      '<td>' +
      '<table><tr>' +
      "<td class='value'>" +
      "<div class='td_class'>" +
      "<input type='text' id='subject' name='subject' style='width: 650px;' value='" +
      subject +
      "'/>" +
      '</div>' +
      '</td>' +
      '<td>&nbsp;&nbsp;</td>' +
      "<td style='padding-bottom: 6px;'>" +
      "<input id='markflag' name='markflag' type='checkbox' value='1' " +
      checked +
      '/>' +
      '</td>' +
      '<td>重要</td>' +
      '</tr></table>' +
      '</td>' +
      '</tr>' +
      '<tr>' +
      "<td class='Label' width='100' align='center'>内容</td>" +
      '<td>' +
      "<table style='width: 100%'>" +
      "<tr><td class='value'>" +
      "<div class='td_class'>" +
      "<textarea  id='mesg_body' class='ckeditor' style='width:100%; height:100%;' name='mesg_body'>" +
      body +
      '</textarea>' +
      '</div>' +
      '</td></tr>' +
      "<tr><td style='padding-left: 20px'>" +
      "<div id='message_fileupload'></div>" +
      '</td></tr>' +
      '</table>' +
      '</td>' +
      '</tr>' +
      relatedRow +
      '</tbody>' +
      '</table>' +
      '</div>';
    content += '<link href="/static/js/fileupload/fileupload.css" rel="stylesheet">';
    return content;
  }

  // 初始化富文本框
  function initCKEditor(isReadOnly) {
    //ckeditor配置路径
    var customCkeditorPath = '/static/dyform/explain/ckeditor'; // 自定义ckeditor相关配置的路径
    CKEDITOR.plugins.basePath = customCkeditorPath + '/plugins/'; // 自定义ckeditor的插件路径
    CKEDITOR.replace('mesg_body', {
      allowedContent: true,
      enterMode: CKEDITOR.ENTER_P,
      toolbarStartupExpanded: true,
      toolbarCanCollapse: true,
      readOnly: isReadOnly,
      customConfig: customCkeditorPath + '/dyform_config.js',
      width: '100%',
      height: '100%',
      startupFocus: true,
      toolbar: [
        [
          'Bold',
          'Italic',
          'Underline',
          'NumberedList',
          'BulletedList',
          '-',
          'Outdent',
          'Indent',
          'JustifyLeft',
          'JustifyCenter',
          'JustifyRight',
          'JustifyBlock',
          'Undo',
          'Redo',
          'changeMode',
          'Maximize'
        ],
        ['Font', 'FontSize', 'TextColor', 'BGColor', 'Blockquote', 'Link', 'Image', 'Table', 'Smiley', 'Source']
      ],
      on: {
        instanceReady: function (ev) {
          if (ev.editor.readOnly) {
            //只读情况下，允许超链接点击跳转动作
            var $a = CKEDITOR.instances.mesg_body.document.getBody().getElementsByTag('a');
            var cnt = $a.count();
            for (var i = 0; i < cnt; i++) {
              var href = $a.getItem(i).getAttribute('href');
              if (href && href != '#' && href != 'javascript:void(0);') {
                $a.getItem(i).setAttribute('onclick', "window.open('" + href + "')");
              }
            }
          }
          var _path = CKEDITOR.plugins.registered.changeMode.path;
          var _name = 'mesg_body';
          var iconDown = _path + 'images/iconDown.png';
          var cke_toolbar_lastChild = $('#cke_' + _name + ' .cke_toolbar:last-child');
          var cke_button__changemode_icon = $('#cke_' + _name + ' .cke_button__changemode_icon');
          cke_toolbar_lastChild.hide();
          cke_button__changemode_icon.css('backgroundImage', 'url(' + iconDown + ')');
          $.fn.modal.Constructor.prototype.enforceFocus = function () {};
        },

        loaded: function (ev) {
          //$(".cke_contents").css("height","230px");
          //$(".cke_contents").css("width","640px");
          //$(".cke_contents").css("margin-right","20px");
        }
      }
    });
  }

  // 初始化组织弹出框
  function popupUser($dialog) {
    $dialog.find('#showUser').click(function () {
      var $unit = $.unit2.open({
        valueField: 'userId',
        labelField: 'showUser',
        title: '选择人员',
        type: 'all',
        multiple: true,
        selectTypes: 'all'
      });
    });
  }

  // 初始化附件上传
  function initFileupload() {
    var fileupload = new WellFileUpload('message_fileupload');
    fileupload.init(false, $('#message_fileupload'), false, true, []);
  }

  function initFileuploadByViewMsg(folderID, readonly) {
    var fileupload = new WellFileUpload('message_fileupload');
    fileupload.initWithLoadFilesFromFileSystem(readonly, $('#message_fileupload'), false, true, folderID, 'messageAttach');
  }

  //发送消息
  function sendMessage($dialog) {
    var sendResult = true;
    //$(form_selector).form2json(bean);
    var userId = $dialog.find('#userId').val();
    var showUser = $dialog.find('#showUser').val();
    var subject = $dialog.find('#subject').val();
    var body = CKEDITOR.instances.mesg_body.getData();
    var markflag = $dialog.find('#markflag').attr('checked') ? '1' : '0';
    var relatedUrl = $dialog.find('#relatedUrl').val();
    var relatedTitle = $dialog.find('#relatedTitle').val();

    var messageAttachId = [];
    var messageAttach = WellFileUpload.files['message_fileupload'];
    for (i in messageAttach) {
      messageAttachId.push(messageAttach[i].fileID);
    }

    if (body) {
      body.replace('%', '%'); //将百分号替换为中文型的
    }
    if (subject) {
      subject.replace('%', '%'); //将百分号替换为中文型的
    }

    if (showUser == null || showUser == '') {
      appModal.alert('收件人不能为空！');
      return false;
    } else if (body == null || body == '') {
      appModal.alert('消息内容不能为空！');
      return false;
    } else if (subject == null || subject == '') {
      appModal.alert('主题不能为空！');
      return false;
    } else {
      var bean = {
        userId: userId,
        body: body,
        type: ['ON_LINE'],
        subject: subject,
        markflag: markflag,
        messageAttach: messageAttachId,
        relatedUrl: relatedUrl,
        relatedTitle: relatedTitle,
        showUser: showUser
      };

      $.ajax({
        type: 'post',
        async: false,
        data: JSON.stringify(bean),
        dataType: 'text',
        contentType: 'application/json',
        url: ctx + '/message/content/submitmessage',
        success: function (result) {
          appModal.success('发送成功！');
        },
        error: function () {
          appModal.alert('发送失败！');
          sendResult = false;
        }
      });
    }
    return sendResult;
  }

  // 陈悦要求加的
  function playAudio() {
    var isOpen = SystemParams.getValue('message.sound.mode'); //on未开启、off或其他未开启
    if (isOpen == 'on') {
      var audio = document.createElement('audio');
      var host = ctx || 'http://' + window.location.host;
      audio.src = host + '/static/images/dingdingSound.mp3';
      audio.play();
    }
  }

  setTimeout(function () {
    websocketConfig();
  }, 100);
});
