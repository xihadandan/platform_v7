(function ($) {
  /**
   * 检查文字长度是否溢出，如果是则将溢出部分省略
   *
   *   注意：
   *   文字本身容器需设置line-height
   *   父物体容器需设置height和overflow-wrap: break-word（目前兼容性比较好的溢出换行样式）
   */
  $.fn.checkOverflow = function (parentClass) {
    $(this).each(function (id, el) {
      var $this = $(el);
      var $parent = $this.closest(parentClass);
      if ($this.height() > $parent.height()) {
        var parentHeight = $parent[0].offsetHeight;
        var thisText = $this.text();
        for (var i = 0; i <= thisText.length; i++) {
          $this.text(thisText.slice(0, i));
          if (parentHeight < $this[0].scrollHeight) {
            var str_all_cn = true;
            // 判断省略号取代的三个字符是否全为中文字符
            thisText
              .slice(i - 2)
              .split('')
              .forEach(function (c, id) {
                if (thisText.slice(i - 2).charCodeAt(id) <= 255) {
                  str_all_cn = false;
                }
              });
            if (str_all_cn) {
              $this.text(thisText.slice(0, i - 2) + '...');
            } else {
              $this.text(thisText.slice(0, i - 3) + '...');
            }
            break;
          }
        }
      }
    });
  };
})(jQuery);

(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'AppOutboxMsgViewDevelopment', 'AppInboxMsgViewDevelopment'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, AppOutboxMsgViewDevelopment, AppInboxMsgViewDevelopment) {
  var UserMsgEvent = {
    myMsgEvent: function () {
      var _self = this;
      $("li[menuid='myMsg']").find('.myMsgWrap').remove();

      var color = getUserTheme().color;

      var $myMsgWrap = $(
        '<div class="myMsgWrap animated" style="display: none"><div class="myMsgSet"><i id="msgSetIcon" title="消息通知设置" class="iconfont icon-ptkj-shezhi"></i></div><ul></ul><div class="msg-tip"><img src="/static/js/pt/theme/default/images/no-msg.' +
          color +
          '.png" alt=""><div>没有新消息！</div></div><div class="myMsg-btns clearfix"><div class="myMsg-read fl">全部标记为已读</div><div class="myMsg-more fr">查看全部<i class="iconfont icon-ptkj-xianmiaoshuangjiantou-you"></i></div></div></div>'
      );
      $.ajax({
        type: 'get',
        async: false,
        dataType: 'text',
        contentType: 'application/json',
        url: contextPath + '/message/inbox/queryRecentTenLists',
        success: function (result) {
          var res = JSON.parse(result);
          if (res.code == 0) {
            var data = res.data;
            if (data.length > 0) {
              $myMsgWrap.removeClass('noMsg');
              $myMsgWrap.find('ul').empty();
              $.each(data, function (i, item) {
                if (i < 4) {
                  $myMsgWrap
                    .find('ul')
                    .append(
                      '<li data-index="' +
                        i +
                        '" data-cateuuid="' +
                        item.classifyUuid +
                        '"><div class="clearfix"><div class="tit fl"><i class="iconfont icon-ptkj-xiaoxitongzhibiaoti"></i>' +
                        (item.classifyName || item.senderName) +
                        '</div><div class="time fr">' +
                        item.receivedTime +
                        '</div></div><div class="msgSubject">' +
                        item.subject +
                        '</div><div class="content"><div class="text">' +
                        item.body +
                        '</div></div></li>'
                    );
                }
              });
            } else {
              $myMsgWrap.addClass('noMsg');
            }
            $("li[menuid='myMsg']").append($myMsgWrap);
            UserMsgEvent.getMsgIcons($myMsgWrap);
            // 消息弹窗

            var outMsgs = new AppOutboxMsgViewDevelopment();
            var inMsgs = new AppInboxMsgViewDevelopment();

            $('.myMsgWrap')
              .find('li')
              .click(function () {
                var index = $(this).data('index');
                var row = data[index];
                var title = '来自 ' + row['senderName'] + '(' + row['receivedTime'] + ')';
                var buttons = {
                  forwardMsg: {
                    label: '转发',
                    className: 'well-btn w-btn-primary',
                    callback: function () {
                      //必须先删除旧的dialog，才能起新的DIALOG,不能会有ID冲突，导致富文本框和附件上传控件出不来的问题
                      $dialog.remove();
                      $('.modal-backdrop').remove();
                      outMsgs.showForwardDialog(row);
                    }
                  },
                  retractMsg: {
                    label: '回复',
                    className: 'well-btn w-btn-primary',
                    callback: function () {
                      //必须先删除旧的dialog，才能起新的DIALOG,不能会有ID冲突，导致富文本框和附件上传控件出不来的问题
                      $dialog.remove();
                      $('.modal-backdrop').remove();
                      inMsgs.showReplyDialog(row);
                    }
                  }
                };

                var $dialog = outMsgs.viewMsg(row, buttons, title, row.messageOutboxUuid, function () {
                  $dialog.find("button[data-bb-handler='forwardMsg']").hide();
                  $dialog.find("button[data-bb-handler='retractMsg']").hide();
                  if (!row['isread']) {
                    inMsgs.readMsgService([row['uuid']], function () {
                      appContext.getWidgetByCssSelector('.ui-wNewHeader').refreshBadge();
                    });
                  }
                });
              });
          }
        }
      });
    },
    getMsgIcons: function ($msg) {
      var items = $msg.find('ul li');
      if (items.length > 0) {
        $.ajax({
          type: 'get',
          async: true,
          url: ctx + '/proxy/api/message/classify/queryList',
          data: { name: '', message: '' },
          success: function (result) {
            var res = result;
            if (res.code == 0) {
              var data = res.data;
              if (data.length > 0) {
                for (var i = 0; i < items.length; i++) {
                  var icon = 'iconfont icon-ptkj-xiaoxitongzhibiaoti';
                  var color = '#488cee';
                  var cateUuid = $(items[i]).data('cateuuid');
                  for (var j = 0; j < data.length; j++) {
                    if (cateUuid == data[j].uuid) {
                      icon = data[j].icon || icon;
                      color = data[j].iconBg || color;
                      $(items[i]).find('i').attr('class', icon).css({
                        background: color,
                        color: '#fff',
                        fontSize: '14px'
                      });
                    }
                  }
                }
              }
            }
          }
        });
      }
    },
    defaultEvent: function () {
      var _self = this;
      $(document).on('click', function (e) {
        if ($(e.target).closest('.myMsgWrap').length || $(e.target).closest('li[menuid="myMsg"]').length) {
          return;
        } else if ($('.myMsgWrap:visible')) {
          $('.myMsgWrap').hide();
        }
      });
      $(_self.element)
        .on('mouseenter', 'li[menuid="myMsg"]', function () {
          var $this = $(this);
          UserMsgEvent.myMsgEvent();
          $this.find('.myMsgWrap').show();
          $this.find('.myMsgWrap .content .text').checkOverflow('.content');
        })
        .on('mouseleave', 'li[menuid="myMsg"]', function () {
          $('.myMsgWrap').hide();
        });

      $(_self.element).on('click', '.myMsgWrap', function (e) {
        e.stopPropagation();
      });

      // 消息弹窗自定义
      $(_self.element).on('click', '#msgSetIcon', function (e) {
        e.stopPropagation();

        $('.myMsgWrap').hide();
        var list = [];
        $.ajax({
          type: 'get',
          async: false,
          dataType: 'text',
          contentType: 'application/json',
          url: ctx + '/userPersonalise/queryList',
          success: function (result) {
            var res = JSON.parse(result);
            if (res.code == 0) {
              list = res.data;
              var message = UserMsgEvent._initMsgSetDialog(list);
              UserMsgEvent._openMsgSetDialog(message, list);
            }
          }
        });
      });

      //消息查看更多
      $(_self.element).on('click', '.myMsg-more', function (e) {
        e.stopPropagation();
        $('.myMsgWrap').hide();
        var menuId = $('li.nav-menu-item[menuid="myMsg"]').attr('menuId');
        var menuItem = _self.menuItemMap[menuId];
        var handler = {
          id: 'page_e05952eb67dded440e2c0dc18f25a361_a6bd713e-c3f7-4b93-9336-d99194b20ce5',
          name: '页面:默认页面容器-版本1.0',
          path: '/pt-app/pt-message/wPage_C78EA19C92E0000183EF102217B03D50',
          type: 4
        };
        var eventParams = menuItem.eventParams || {};
        var target = menuItem.target;
        var opt = {
          target: target.position || '_dialog',
          targetWidgetId: target.widgetId,
          refreshIfExists: target.refreshIfExists,
          appType: handler.type,
          appPath: handler.path,
          params: { id: '246174de446c31abe74fb2285e1ffcd9' },
          event: event,
          selection: menuId,
          targetDetail: target
        };
        _self.startApp(opt);
      });

      //消息全部已读
      $(_self.element).on('click', '.myMsg-read', function (e) {
        e.stopPropagation();
        if ($('.myMsgWrap').hasClass('noMsg')) {
          return false;
        }
        $.ajax({
          type: 'put',
          async: false,
          dataType: 'json',
          contentType: 'application/json',
          url: ctx + '/message/inbox/updateToReadState',
          success: function (result) {
            if (result.code == 0) {
              appModal.success('标记成功！');
              _self.refreshBadge();
              $('.myMsgWrap').hide();
            }
          }
        });
      });
    },
    _openMsgSetDialog: function (html, list) {
      var $msgSetDialog = appModal.dialog({
        title: '消息通知设置',
        message: html,
        size: 'middle',
        height: 552,
        width: 900,
        buttons: {
          ok: {
            label: '保存',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var userPer = list.userPerClassifys;
              var templateIds = [];
              var isPopups = [];
              for (var i = 0; i < userPer.length; i++) {
                if (userPer[i].userPersonalises && userPer[i].userPersonalises.length > 0) {
                  for (var j = 0; j < userPer[i].userPersonalises.length; j++) {
                    templateIds.push(userPer[i].userPersonalises[j].templateId);
                    isPopups.push(userPer[i].userPersonalises[j].isPopup);
                  }
                }
              }

              // $.ajax({
              //   url: ctx + '/api/user/preferences/save',
              //   type: 'POST',
              //   data: {
              //     dataKey: 'MSG',
              //     dataValue: JSON.stringify({ mainSwitch: list.mainSwitch, templateIds: templateIds, isPopups: isPopups }),
              //     moduleId: 'MSG',
              //     remark: '消息通知设置'
              //   },
              //   dataType: 'json',
              //   success: function (result) {
              //     if (result.code == 0) {
              //       appModal.success('保存成功');
              //     }
              //   },
              //   error: function () {}
              // });

              $.ajax({
                type: 'post',
                async: true,
                dataType: 'json',
                contentType: 'application/json',
                url: ctx + '/userPersonalise/saveUserPersonalise',
                data: JSON.stringify({ mainSwitch: list.mainSwitch, templateIds: templateIds, isPopups: isPopups }),
                success: function (result) {
                  var res = JSON.parse(result);
                  if (res.code == 0) {
                    appModal.success('保存成功');
                  }
                }
              });
            }
          },
          setDefault: {
            label: '恢复默认',
            className: 'well-btn btn-default',
            callback: function () {
              $.ajax({
                type: 'put',
                async: true,
                dataType: 'json',
                contentType: 'application/json',
                url: ctx + '/userPersonalise/reset',
                success: function (result) {
                  var res = JSON.parse(result);
                  if (res.code == 0) {
                    appModal.success('重置成功');
                  }
                }
              });
            }
          },
          cancel: {
            label: '取消',
            className: 'well-btn btn-default',
            callback: function () {}
          }
        },
        shown: function () {
          $msgSetDialog.find('.modal-dialog').css({
            margin: '50vh auto 0px',
            transform: 'translateY(-50%)'
          });

          $('.bootbox-body').on('click', '.switch-wrap', function () {
            if ($(this).hasClass('msg-no-operate')) {
              return;
            }

            var type = $(this).data('type');
            var index = $(this).data('index');
            var userIndex = $(this).data('user-index');

            if ($(this).hasClass('active')) {
              $(this).data('value', 0).removeClass('active');
            } else {
              $(this).data('value', 1).addClass('active');
            }

            var value = $(this).data('value');
            var $title = $(this).parents('.msg-set-title');
            if (type == 'userPersonalises') {
              list.userPerClassifys[index].userPersonalises[userIndex].isPopup = value;
            } else if ($title.size() > 0 && value == 1) {
              $title.siblings().find('.switch-wrap').removeClass('msg-no-operate');
              list.mainSwitch = value;
            } else if ($title.size() > 0 && value == 0) {
              $title.siblings().find('.switch-wrap').addClass('msg-no-operate');
              list.mainSwitch = value;
            }
          });

          if (list.mainSwitch == 0) {
            $('.msg-set-title').siblings().find('.switch-wrap').addClass('msg-no-operate');
          }

          $('.msg-set-wrap').on('click', '.msg-set-folder', function () {
            if ($(this).hasClass('icon-ptkj-shixinjiantou-you')) {
              $(this).addClass('icon-ptkj-shixinjiantou-xia').removeClass('icon-ptkj-shixinjiantou-you');
              $(this).parents('li').find('.hasClassify').slideDown(100);
            } else {
              $(this).addClass('icon-ptkj-shixinjiantou-you').removeClass('icon-ptkj-shixinjiantou-xia');
              $(this).parents('li').find('.hasClassify').slideUp(100);
            }
          });
        }
      });
    },
    _initMsgSetDialog: function (obj) {
      var html = '';
      var list = obj.userPerClassifys;
      var isSwitchActive = obj.mainSwitch == 0 ? '' : 'active';
      html +=
        "<ul class='msg-set-wrap'>" +
        "<li class='msg-set-title'>" +
        '<div>' +
        "新消息弹出提醒<span class='new-msg-tip'>（开启后收到指定类型的消息时将会在右下角弹出提醒）</span>" +
        "<input type='hidden' name='isEnable' id='isEnable' value='" +
        (obj.mainSwitch || 1) +
        "'/>" +
        "<div class='switch-wrap " +
        isSwitchActive +
        "'><span class='switch-radio'></span></div>" +
        '</div>' +
        '</li>';
      if (list.length > 0) {
        for (var i = 0; i < list.length; i++) {
          var classify = list[i].classify;
          var userPersonal = list[i].userPersonalises;
          if (classify && userPersonal) {
            var background = classify.iconBg ? classify.iconBg : '#64B3EA';
            var icon = classify.icon ? classify.icon : 'iconfont icon-xmch-wodexiaoxi';
            html +=
              "<li class=''>" +
              '<div>' +
              "<i class='msg-set-folder iconfont icon-ptkj-shixinjiantou-you'></i><i class='" +
              icon +
              "' style='background: " +
              background +
              "'></i>" +
              classify.name +
              '</div>';

            if (userPersonal && userPersonal.length > 0) {
              html += "<ul class='hasClassify' style='display: none'>";
              for (var j = 0; j < userPersonal.length; j++) {
                var isPopup = userPersonal[j].isPopup == 1 ? 'active' : '';
                html +=
                  '<li>' +
                  '<div>' +
                  userPersonal[j].templateName +
                  "<div class='switch-wrap " +
                  isPopup +
                  "' data-is-popup='" +
                  userPersonal[j].isPopup +
                  "'  data-index='" +
                  i +
                  "' data-type='userPersonalises' data-value='" +
                  userPersonal[j].isPopup +
                  "' data-user-index='" +
                  j +
                  "'><span class='switch-radio'></span></div>" +
                  '</div>' +
                  '</li>';
              }
              html += '</ul>';
            }
            html += '</li>';
          } else if (userPersonal) {
            for (var j = 0; j < userPersonal.length; j++) {
              var isPopup = userPersonal[j].isPopup == 1 ? 'active' : '';
              html +=
                '<li>' +
                '<div>' +
                userPersonal[j].templateName +
                "<div class='switch-wrap " +
                isPopup +
                "' data-is-popup='" +
                userPersonal[j].isPopup +
                "'  data-index='" +
                i +
                "' data-type='userPersonalises' data-value='" +
                userPersonal[j].isPopup +
                "' data-user-index='" +
                j +
                "'><span class='switch-radio'></span></div>" +
                '</div>' +
                '</li>';
            }
          }
        }
      }
      html += '</ul>';
      return html;
    }
  };
  window.UserMsgEvent = UserMsgEvent;
  return UserMsgEvent;
});
