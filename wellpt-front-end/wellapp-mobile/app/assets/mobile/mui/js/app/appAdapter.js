/**
 * 发布文件更新注意: var webpageUrl =
 * "http://172.16.26.208:8080/yunZhiJiaServlet?dotype=share"; 替换 var webpageUrl =
 * "http://m.leedarson.com:8888/yunZhiJiaServlet?dotype=share";
 */
var flowThumbData =
  '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';
(function (factory) {
  // 配置requirejs
  requirejs.config({
    paths: {
      wxjs: ctx + '/static/mobile/mui/js/jsapi/jweixin',
      qingjs: ctx + '/static/mobile/mui/js/jsapi/qingjs',
      dingtalk: ctx + '/static/mobile/mui/js/jsapi/dingtalk.open',
      filepreview: ctx + '/static/mobile/mui/js/common/mui.filepreview'
    }
  });
  var adapter = {
    wx: false,
    qing: false,
    dingtalk: false
  };
  var requireJsArr = ['mui', 'server', 'appModal'];
  var userAgent = navigator.userAgent;
  // userAgent = "Qing"; // 模拟云之家环境
  if (userAgent && userAgent.indexOf('Qing') > -1) {
    adapter.qing = true;
    requireJsArr.push('qingjs');
  } else if (userAgent && (userAgent.indexOf('Wx') > -1 || userAgent.indexOf('wechat') > -1)) {
    adapter.wx = true;
    requireJsArr.push('wxjs');
  } else if (userAgent && (userAgent.indexOf('DingTalk') > -1 || userAgent.indexOf('AliApp') > -1)) {
    adapter.dingtalk = true;
    requireJsArr.push('dingtalk');
  }
  // 加载
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(requireJsArr, factory);
  }
  window.WellAdapter = adapter;
})(function ($, server, appModal, formBuilder, jssdk) {
  window.jssdk = window.jssdk || jssdk;
  var adapter = window.WellAdapter;
  if (adapter.qing && !adapter.inited) {
    if ($.os.android && window.history.length <= 1) {
      // $.alert("jump..."); // 处理云之家APP首次加载,退回绑定无效问题
      var href = location.href;
      return (location.href = href + (href.indexOf('?') < 0 ? '?' : '&') + '_whl=' + window.history.length);
    }
    document.addEventListener('history.change', function (event) {
      var whl = window.history.length;
      // $.alert(whl + "");
      if (whl === 1 || ($.os.android && whl === 2)) {
        // 处理云之家APP在andriod多一次跳转
        XuntongJSBridge.call('closeWebView', {});
      }
    });
    XuntongJSBridge.call('defback', {}, function (result) {
      $.ui.goBack(); // $.back();
    });
    document.addEventListener('title.change', function (event) {
      var detail = event.detail;
      if (detail && detail.title) {
        XuntongJSBridge.call('setWebViewTitle', {
          title: detail.title
        }); // 设置页面标题并显示
      } else {
        XuntongJSBridge.call('hideWebViewTitle', {}); // 隐藏标题
      }
    });
    document.addEventListener('webview.titleBar.hide', function (event) {
      XuntongJSBridge.call('setWebViewTitleBar', {
        isShow: false
      });
    });
    document.addEventListener('webview.close', function (event) {
      var detail = event.detail;
      if (detail && detail.confirm) {
        $.confirm('确认退出', function (data) {
          XuntongJSBridge.call('closeWebView', {});
        });
      } else {
        XuntongJSBridge.call('closeWebView', {});
      }
    });
    document.addEventListener('optionmenu.change', function (event) {
      var detail = event.detail;
      var target = event.target || document.body;
      if (detail && detail.showOptionMenu) {
        XuntongJSBridge.call('showOptionMenu', {});
      }
      if (detail && detail.customOptionMenu) {
        var params = {};
        params.popTitle = detail.title || '';
        params.popCallback = detail.callback;
        params.items = detail.itemList || [];
        params.menuList = detail.menuList || [];
        params.popTitleCallBackId = 'popCallBackId';
        XuntongJSBridge.call('createPop', params, function (result) {
          if (result.success == true || result.success === 'true') {
            var callBack = params['popCallback'];
            var callBackId = result.data ? result.data.callBackId : '';
            if (callBackId == 'popCallBackId' && typeof callBack === 'function') {
              callBack.call(target, result);
            }
            $.trigger(target, 'optionmenu.callback', {
              result: result
            });
          }
        });
      } else {
        XuntongJSBridge.call('hideOptionMenu', {});
      }
    });
    document.addEventListener('rotateUI', function (event) {
      var detail = event.detail || {
        orientation: 'portrait'
      };
      // appModal.toast("开始调用轻应用控制屏幕横竖屏接口");
      XuntongJSBridge.call(
        'rotateUI',
        {
          orientation: detail.orientation
        },
        function (result) {}
      );
      if (detail.orientation == 'portrait') {
        XuntongJSBridge.call('setWebViewTitleBar', {
          isShow: true
        });
      }
      // appModal.toast("调用轻应用控制屏幕横竖屏接口：XuntongJSBridge.call('rotateUI',
      // {orientation: '" + detail.orientation + "'},
      // function(result){})");
    });
    // appModal.toast("监听轻应用控制屏幕横竖屏事件");
    document.addEventListener('file.show', function (event) {
      var detail = event.detail;
      if (detail && detail.fileObj) {
        var fileObj = detail.fileObj;
        return require(['filepreview'], function (FileViewer) {
          FileViewer.preview(fileObj);
        });
        if (fileObj.image || fileObj.fileId == null || fileObj.fileId == '') {
          XuntongJSBridge.call(
            'showFile',
            {
              fileExt: fileObj.fileExt,
              fileSize: fileObj.fileSize,
              fileName: fileObj.fileName,
              fileTime: '2000-01-01 01:01',
              fileDownloadUrl: fileObj.fileUrl + '&isWeixin=isWeixin'
            },
            function (result) {
              if ((result.success == false || result.success == 'false') && result.error) {
                $.alert(result.error);
              }
            }
          );
        } else {
          window.location.href = fileObj.fileUrl;
        }
      } else if (detail && detail.fileUrl) {
        location.href = detail.fileUrl + '&isWeixin=isWeixin';
      }
    });
    document.addEventListener('sms.send', function (event) {
      // {to:";",body:""}
    });
    document.addEventListener('email.send', function (event) {
      // {to:";",body:""}
    });
    document.addEventListener('phone.call', function (event) {
      // {phoneNum:""}
    });
    document.addEventListener('workflow.share', function (event) {
      var detail = event.detail;
      if (detail && detail.workData) {
        var workData = detail.workData;
        var flowInstUuid = workData.flowInstUuid;
        var taskInstUuid = workData.taskInstUuid;
        // TODO soft code
        var webpageUrl = 'http://172.16.26.208:8080/yunZhiJiaServlet?dotype=share';
        webpageUrl += '&flowInstUuid=' + flowInstUuid + '&taskInstUuid=' + taskInstUuid;
        return XuntongJSBridge.call('getPersonInfo', {}, function (result) {
          if ((result.success == 'true' || result.success == true) && result.data) {
            // return $.alert(JSON.stringify(personInfo));
            var personInfo = result.data;
            var userName = personInfo.userName || personInfo.name;
            webpageUrl += '&user=' + userName;
            // return $.alert(webpageUrl);
            XuntongJSBridge.call(
              'share',
              {
                shareType: '4',
                appId: '10113', // ParamesAPI.workflowAppId
                appName: '流程',
                // "lightAppId" : "XXX",
                title: workData.title,
                webpageUrl: webpageUrl,
                thumbData: flowThumbData,
                content: workData.taskName,
                cellContent: workData.taskName,
                sharedObject: 'all'
              },
              function (result) {}
            );
          } else {
            $.alert('获取授权信息失败');
          }
        });
      }
    });
    $(document.body).on('tap', 'p.assignee[assigneeid]', function (event) {
      var target = event.target;
      var assigneeId = target.getAttribute('assigneeid');
      // $.alert("展示:" + assigneeId)
      $.ajax({
        url: ctx + '/ldxApp/yunzhijia/getPersonTicket',
        data: {
          userId: assigneeId
        },
        async: false,
        dataType: 'json',
        success: function (result) {
          var userinfo = result;
          if (userinfo && userinfo.data && userinfo.data.length > 0) {
            XuntongJSBridge.call(
              'personInfo',
              {
                openId: userinfo.data[0].openId
              },
              function (result) {
                // alert("结果："+JSON.stringify(result));
              }
            );
          } else {
            $.alert('用户未同步或用户不存在');
          }
        },
        error: function () {
          $.alert('用户未同步或用户不存在');
        }
      });
    });
  } else if (adapter.wx && !adapter.inited) {
    document.addEventListener('file.show', function (event) {
      var detail = event.detail;
      if (detail && detail.fileObj) {
        var fileObj = detail.fileObj;
        // if (fileObj.image && fileObj.fileUrl) {
        return require(['filepreview'], function (FileViewer) {
          FileViewer.preview(fileObj);
        });
        // }
        location.href = fileObj.fileUrl + '&isWeixin=isWeixin';
      } else if (detail && detail.fileUrl) {
        location.href = detail.fileUrl + '&isWeixin=isWeixin';
      }
    });
    $(document.body).on('tap', 'p.assignee[assigneeid]', function (event) {
      var target = event.target;
      var assigneeId = target.getAttribute('assigneeid');
      appContext.require(['mui-ShowUserInfo'], function (showUserInfo) {
        showUserInfo({
          userId: assigneeId
        });
      });
    });
    document.addEventListener('workflow.share', function (event) {
      $.alert('TODO:流程分享');
    });
    document.addEventListener('webview.close', function (event) {
      var detail = event.detail;
      if (detail && detail.confirm) {
        $.confirm('确认退出', function (data) {
          window.close();
        });
      } else {
        window.close();
      }
    });
  } else if (adapter.dingtalk && !adapter.inited) {
    var dd = jssdk;
    document.addEventListener('backbutton', function (event) {
      // 在这里处理你的业务逻辑
      // event.preventDefault();
      // //backbutton事件的默认行为是回退历史记录，如果你想阻止默认的回退行为，那么可以通过preventDefault()实现
      // $.ui.goBack();
    });
    document.addEventListener('file.show', function (event) {
      var detail = event.detail;
      if (detail && detail.fileObj) {
        var fileObj = detail.fileObj;
        return require(['filepreview'], function (FileViewer) {
          FileViewer.preview(fileObj);
        });
      }
    });
    document.addEventListener('phone.call', function (event) {
      if (jssdk.pc) {
        return appModal.toast('请在手机端操作');
      }
      // {phoneNum:""}
      var detail = event.detail;
      if (!detail || !detail.phoneNum) {
        appModal.toast('参数错误,phoneNum必填');
      } else {
        jssdk.biz.telephone.showCallMenu({
          phoneNumber: detail.phoneNum, // 期望拨打的电话号码
          code: '+86', // 国家代号，中国是+86
          // showDingCall: true, // 是否显示钉钉电话
          onSuccess: function () {},
          onFail: function () {}
        });
      }
    });
    $(document.body).on('tap', 'p.assignee[assigneeid]', function (event) {
      var target = event.target;
      var assigneeId = target.getAttribute('assigneeid');
      appContext.require(['mui-ShowUserInfo'], function (showUserInfo) {
        showUserInfo({
          userId: assigneeId
        });
      });
    });
    document.addEventListener('title.change', function (event) {
      if (jssdk.pc) {
        return; // appModal.toast("");
      }
      var detail = event.detail,
        title;
      if (detail && detail.title) {
        var $d = document.createElement('div');
        $d.innerHTML = detail.title;
        title = $d.innerText;
      } else {
        title = '';
      }
      jssdk.biz.navigation.setTitle({
        title: title,
        onSuccess: function (result) {
          /*
           * 结构 { }
           */
        },
        onFail: function (err) {}
      });
    });
    document.addEventListener('workflow.share', function (event) {
      var detail = event.detail;
      if (detail && detail.workData) {
        var workData = detail.workData;
        var flowInstUuid = workData.flowInstUuid;
        var taskInstUuid = workData.taskInstUuid;
        var webpageUrl = location.origin + ctx + '/mobile/pt/dingtalk/start';
        webpageUrl =
          webpageUrl +
          '?uri=' +
          encodeURIComponent('/workflow/mobile/work/view/share?' + ('flowInstUuid=' + flowInstUuid + '&taskInstUuid=' + taskInstUuid));
        jssdk.biz.util.share({
          type: 1, // 分享类型，0:全部组件 默认；1:只能分享到钉钉；2:不能分享，只有刷新按钮
          url: webpageUrl,
          title: workData.title,
          content: workData.taskName,
          image: '', //
          onSuccess: function () {
            // onSuccess将在调起分享组件成功之后回调
            /**/
          },
          onFail: function (err) {}
        });
      }
    });
    document.addEventListener('webview.close', function (event) {
      var detail = event.detail;
      var closeWebview = jssdk.pc ? jssdk.biz.navigation.quit : jssdk.biz.navigation.close;
      if (detail && detail.confirm) {
        $.confirm('确认退出', function (data) {
          closeWebview({
            onSuccess: function (result) {},
            onFail: function (err) {}
          });
        });
      } else {
        closeWebview({
          onSuccess: function (result) {},
          onFail: function (err) {}
        });
      }
    });
    var getDingIdsByPtIds = function (userIds) {
      var dingUserIds;
      $.ajax({
        url: ctx + '/pt/dingtalk/getDingIdsByPtIds',
        data: {
          userIds: JSON.stringify(userIds)
        },
        async: false,
        dataType: 'json',
        success: function (result) {
          dingUserIds = result.data;
        },
        error: function () {
          $.alert('用户未同步或用户不存在');
        }
      });
      return dingUserIds;
    };
    adapter.getUserIds = getDingIdsByPtIds;
    // 需要授权的API
    $.getJSON(ctx + '/mobile/pt/dingtalk/getJsApiConfig', { signedUrl: location.href }, function (jsApiConfig) {
      console.log('jsApiConfig:', jsApiConfig);
      dd.error(function (error) {
        // alert('dd error: ' + JSON.stringify(error));
        console.error('dd error: ', error);
      });
      dd.config({
        agentId: jsApiConfig.agentId, // 必填，微应用ID
        corpId: jsApiConfig.corpId, // 必填，企业ID
        timeStamp: jsApiConfig.timeStamp, // 必填，生成签名的时间戳
        nonceStr: jsApiConfig.nonceStr, // 必填，生成签名的随机串
        signature: jsApiConfig.signature, // 必填，签名
        type: 0, // 选填。0表示微应用的jsapi,1表示服务窗的jsapi；不填默认为0。该参数从dingtalk.js的0.8.3版本开始支持
        jsApiList: [
          'runtime.info',
          'biz.contact.choose',
          'device.notification.confirm',
          'device.notification.alert',
          'device.notification.prompt',
          'biz.ding.post',
          'biz.util.openLink',
          'biz.contact.createGroup',
          'biz.chat.pickConversation',
          'biz.chat.chooseConversationByCorpId',
          'biz.chat.toConversation',
          'biz.chat.openSingleChat'
        ]
      });
      dd.ready(function () {
        // 1、创建企业群聊天
        document.addEventListener('createGroup', function (event) {
          var detail = event.detail,
            userIds;
          if (detail.userIds && (userIds = getDingIdsByPtIds(detail.userIds))) {
            dd.biz.contact.createGroup({
              corpId: jsApiConfig.corpId,
              users: userIds, // 默认选中的用户工号列表，可选；使用此参数必须指定corpId
              onSuccess: function (result) {
                /*
                 * { id: 123 //企业群id }
                 */
              },
              onFail: function (err) {}
            });
          }
        });
        // 2、获取会话信息
        document.addEventListener('pickConversation', function (event) {
          dd.biz.chat.pickConversation({
            corpId: jsApiConfig.corpId, // 企业id,必须是用户所属的企业的corpid
            isConfirm: false, // 是否弹出确认窗口，默认为true
            onSuccess: function () {
              console.log('pickConversation:', arguments);
              // onSuccess将在选择结束之后调用
              // 该cid和服务端开发文档-普通会话消息接口配合使用，只能使用一次
              /*
               * { cid: 'xxxx', title:'xxx' }
               */
            },
            onFail: function () {}
          });
        });
        // 3、根据corpid选择会话
        document.addEventListener('chooseConversationByCorpId', function (event) {
          dd.biz.chat.chooseConversationByCorpId({
            corpId: jsApiConfig.corpId, // 企业id,必须是用户所属的企业的corpid
            isAllowCreateGroup: true,
            filterNotOwnerGroup: false,
            onSuccess: function (result) {
              console.log('chooseConversationByCorpId:', arguments);
              var chatId = result.chatId;
              var chatTitle = result.title;
              // 4、根据chatId跳转到对应会话
              dd.biz.chat.toConversation({
                corpId: jsApiConfig.corpId, // //企业id,必须是用户所属的企业的corpid
                chatId: result.chatId, // 会话Id
                onSuccess: function () {
                  console.log('toConversation:', arguments);
                },
                onFail: function () {}
              });
              // onSuccess将在选择结束之后调用
              /*
               * { chatId: 'xxxx', title:'xxx' }
               */
            },
            onFail: function () {}
          });
        });
        // 4、根据chatId跳转到对应会话
        document.addEventListener('toConversation', function (event) {
          var detail = event.detail;
          if (detail.chatId) {
            dd.biz.chat.toConversation({
              corpId: jsApiConfig.corpId, // //企业id,必须是用户所属的企业的corpid
              chatId: detail.chatId, // 会话Id
              onSuccess: function () {},
              onFail: function () {}
            });
          }
        });
        // 5、打开与某个用户的单聊会话
        document.addEventListener('openSingleChat', function (event) {
          var detail = event.detail,
            userIds;
          if (detail.userId && (userIds = getDingIdsByPtIds([detail.userId]))) {
            dd.biz.chat.openSingleChat({
              corpId: jsApiConfig.corpId, // 企业id,必须是用户所属的企业的corpid
              userId: userIds[0], // 用户的工号
              onSuccess: function () {},
              onFail: function () {}
            });
          }
        });
      });
    });
  } else {
    document.addEventListener('file.show', function (event) {
      var detail = event.detail;
      if (detail && detail.fileObj) {
        var fileObj = detail.fileObj;
        // if (fileObj.image && fileObj.fileUrl) {
        return require(['filepreview'], function (FileViewer) {
          FileViewer.preview(fileObj);
        });
        // }
        location.href = fileObj.fileUrl + '&isWeixin=isWeixin';
      } else if (detail && detail.fileUrl) {
        $.toast('不支持下载,请到PC端操作');
        // location.href = detail.fileUrl + "&isWeixin=isWeixin";
      }
    });
    $(document.body).on('tap', 'p.assignee[assigneeid]', function (event) {
      var target = event.target;
      var assigneeId = target.getAttribute('assigneeid');
      // 先关闭offCanvas
      var offCanvas = document.querySelector('.mui-off-canvas-wrap.mui-active'),
        offCanvasApi;
      if (offCanvas && (offCanvasApi = $(offCanvas).offCanvas('close'))) {
        offCanvasApi.close();
      }
      appContext.require(['mui-ShowUserInfo'], function (showUserInfo) {
        showUserInfo({
          userId: assigneeId
        });
      });
    });
    document.addEventListener('workflow.share', function (event) {
      $.alert('TODO:流程分享');
    });
    document.addEventListener('sms.send', function (event) {
      // {to:";",body:""}
      var detail = event.detail;
      if (!detail || !detail.to) {
        appModal.toast('发送短信参数错误,to必填');
      } else if (window.plus) {
        var msg = plus.messaging.createMessage(plus.messaging.TYPE_SMS);
        msg.to = detail.to.split(';');
        msg.body = ''; // 'This is HTML5 Plus example test message';
        plus.messaging.sendMessage(msg);
      } else {
        appModal.toast('设备无法发送短信,请使用app登陆');
      }
    });
    document.addEventListener('email.send', function (event) {
      // {to:";",body:""}
      var detail = event.detail;
      if (!detail || !detail.to) {
        appModal.toast('发送邮件参数错误,to必填');
      } else {
        appContext.require(['mui-webmailBox'], function (webmailBox) {
          var options = {
            newEmail: true,
            toMailAddress: detail.to,
            toUserName: detail.toName,
            ccMailAddress: detail.ccMailAddress,
            bccMailAddress: detail.bccMailAddress,
            ccUserName: detail.ccUserName,
            bccUserName: detail.bccUserName,
            afterDeleteSuccess: detail.afterDeleteSuccess,
            afterSaveSuccess: detail.afterSaveSuccess,
            afterSendSuccess: detail.afterSendSuccess
          };
          return webmailBox.editEmail.apply(event, [options]);
        });
      }
    });
    document.addEventListener('phone.call', function (event) {
      // {phoneNum:""}
      var detail = event.detail;
      if (!detail || !detail.phoneNum) {
        appModal.toast('参数错误,phoneNum必填');
      } else if (window.plus) {
        plus.device.dial(detail.phoneNum, true);
      } else {
        appModal.toast('设备无法打电话,请使用app登陆');
      }
    });
    document.addEventListener('webview.close', function (event) {
      var detail = event.detail;
      if (detail && detail.confirm) {
        $.confirm('确认退出', function (data) {
          window.close();
        });
      } else {
        window.close();
      }
    });
  }
  adapter.inited = true;
  return adapter;
});
