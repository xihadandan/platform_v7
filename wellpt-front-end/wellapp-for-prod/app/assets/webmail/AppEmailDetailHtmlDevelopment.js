define([
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'HtmlWidgetDevelopment',
  'dataStoreBase',
  'lodash',
  'AppEmailListDevelopment',
  'AppEmailTagDevelopment'
], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment,
  dataStoreBase,
  _,
  AppEmailListDevelopment,
  AppEmailTagDevelopment
) {
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;

  var AppEmailDetailHtmlDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppEmailDetailHtmlDevelopment, HtmlWidgetDevelopment, {
    prepare: function () {
      this.mailboxUuid = commons.Browser.getQueryString('mailboxUuid');
      this.ctlId = 'webmail_file_upload';
      this.fileElSelector = '#webmail_file_upload_el';
      this.fileupload = new WellFileUpload(this.ctlId);
      var secDevBtnIdStr = '00b13afb-8afc-4a9e-b1e2-28f321f48924;5f82f10a-9450-4a18-8c8d-e38e3767b466;4ee25050-f1e5-49d2-a635-9c19ef1785dc';
      this.fileupload.initFileUploadExtraParam(true, false, secDevBtnIdStr);
      this.$mailListDataProvider = null;
      this.emailListDevelopment = new AppEmailListDevelopment();
      this.appEmailTagDevelopment = new AppEmailTagDevelopment();
      appModal.showMask('邮件加载中...');
    },

    init: function () {
      var _this = this;
      _this.loadCssLink();
      _this.loadMail();
    },

    bindEvent: function () {
      var _this = this;
      var $container = this.widget.element;
      // 删除
      $('.btn_delete', $container).on('click', function () {
        appModal.confirm('确定要删除吗?', function (yes) {
          if (yes) {
            JDS.restfulPost({
              url: ctx + '/proxy/api/mail/manager/delete',
              contentType: 'application/x-www-form-urlencoded',
              data: {
                mailboxUuids: _this.bean.mailboxUuid
              },
              success: function (result) {
                if (result.code == 0) {
                  appModal.alert({
                    message: '删除成功',
                    callback: function () {
                      _this.refreshParentWindowBadgeNum();
                      _this.refreshParentWindowEmailList();
                      returnWindow();
                      window.close();
                    }
                  });
                } else {
                  // 删除失败
                  appModal.error('删除失败');
                }
              }
            });
          }
        });
      });

      $('#fetchStatusBtn', $container).on('click', function () {
        appModal.showMask();
        _this.popFetchStatusDetailDialog();
      });
      $('#fetchStatusBtn1', $container).on('click', function () {
        appModal.showMask();
        _this.popFetchStatusDetailDialog();
      });
      $('#fetchStatusBtn2', $container).on('click', function () {
        appModal.showMask();
        _this.popFetchStatusDetailDialog();
      });

      $('.sendEmailReceipt')
        .off()
        .on('click', function () {
          // 是否发送回执
          changeReceiptStatus(_this.bean.mailboxUuid, '2');
        });

      $('.notSndEmailReceipt')
        .off()
        .on('click', function () {
          changeReceiptStatus(_this.bean.mailboxUuid, '3');
        });

      function changeReceiptStatus(mailboxUuid, status) {
        JDS.call({
          service: 'wmWebmailService.receipt',
          data: [mailboxUuid, status],
          version: '',
          success: function (result) {
            if (result.success) {
              $('.email-receipt').hide();
              if (status === '3') return;
              var mailboxUuid = (result.data && result.data.mailboxUuid) || '';
              var $dialog = appModal.dialog({
                message: '阅读回执已发送，您可在发件箱中查看收件状态！',
                title: '阅读回执已发送',
                size: 'small',
                callback: function () {
                  _this.refreshParentWindowBadgeNum();
                  _this.refreshParentWindowEmailList();
                },
                shown: function () {
                  $dialog.find('.modal-dialog').css({
                    'min-width': '400px'
                  });
                  $dialog.find('.bootbox-body').css({
                    display: 'flex',
                    'align-items': 'center',
                    'justify-content': 'center'
                  });
                },
                buttons: {
                  check: {
                    label: '查看收件状态',
                    className: 'well-btn w-btn-primary',
                    callback: function () {
                      window.open(
                        ctx +
                          '/web/app/pt-app/pt-webmail/pt-webmail-openmail.html?pageUuid=9a6037a5-52fe-4d82-86c0-1d068a6c0b51&mailboxUuid=' +
                          mailboxUuid,
                        '_self'
                      );
                    }
                  },
                  close: {
                    label: '关闭',
                    className: 'btn btn-default'
                  }
                }
              });
            }
          }
        });
      }

      $('.btn_revoke', $container).on('click', function () {
        appModal.confirm('确定要撤回邮件吗？', function (res) {
          if (res) {
            appModal.showMask('撤回邮件中...');
            JDS.restfulPost({
              mask: true,
              async: false,
              url: ctx + '/proxy/api/mail/manager/revokeMail',
              contentType: 'application/x-www-form-urlencoded',
              data: {
                mailUuid: _this.bean.mailboxUuid
              },
              success: function (result) {
                if (result.code == 0) {
                  appModal.hideMask();
                  $('#btn_revoke', $container).remove();
                  var data = result.data;
                  if (data) {
                    var options = {
                      title: '撤回详情',
                      message: _this.revokeResultTableHtml(data),
                      size: 'middle'
                    };
                    appModal.dialog(options);
                  }
                } else {
                  appModal.hideMask();
                  appModal.error('撤回失败!');
                }
              }
            });
          }
        });
      });

      $('.btn_resend', $container).on('click', function () {
        var msgtip =
          "<div style='text-align: center;padding-top: 30px;'><p><b>确认要将发送内容重新发送吗？</b></p><p>（发送到未接到邮件的收件人手中）</p></div>";
        var options = {
          title: '提示',
          message: msgtip,
          size: 'middle',
          buttons: {
            confirm: {
              label: '确定重新发送',
              className: 'btn-primary',
              callback: function (result) {
                JDS.restfulPost({
                  url: ctx + '/proxy/api/mail/manager/resend',
                  contentType: 'application/x-www-form-urlencoded',
                  data: {
                    mailboxUuid: _this.bean.mailboxUuid
                  },
                  success: function (result) {
                    window.location.reload();
                  }
                });
              }
            },
            cancel: {
              label: '取消',
              className: 'btn-default',
              callback: function (result) {}
            }
          }
        };
        appModal.dialog(options);
      });

      // 转发
      $('.btn_transfer', $container).on('click', function () {
        window.location = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-write.html?transfer=true&mailboxUuid=' + _this.bean.mailboxUuid;
      });

      // 回复
      $('.btn_reply', $container).on('click', function () {
        window.location = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-write.html?reply=true&mailboxUuid=' + _this.bean.mailboxUuid;
      });

      // 回复全部
      $('.btn_reply_all', $container).on('click', function () {
        window.location = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-write.html?replyAll=true&mailboxUuid=' + _this.bean.mailboxUuid;
      });

      // 再次编辑
      $('.btn_edit_again', $container).on('click', function () {
        window.location = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-write.html?editAgain=true&mailboxUuid=' + _this.bean.mailboxUuid;
      });

      _this.initPrevAndNextButtonEvent();
    },

    revokeResultTableHtml: function (result) {
      var $table = $('<table>', { class: 'table table-striped' });
      var $thead = $('<thead>').append($('<tr>').append($('<th>').text('收件'), $('<th>').text('撤回结果')));
      $table.append($thead);
      for (var address in result) {
        if (address == 'toMailAddressNull') {
          var $tr = $('<tr>').append($('<td>').text('解析后无接收人，无需撤回'));
          $table.append($tr);
        } else if (address == 'all') {
          var $tr = $('<tr>').append($('<td>').text('邮件未发送已全部撤回'));
          $table.append($tr);
        } else {
          var tip = result[address] ? '已撤回邮件' : '撤回失败';
          var iconClass = 'glyphicon ' + (result[address] ? 'glyphicon-ok-circle' : 'glyphicon-remove-circle');
          var iconStyle = 'margin-right: 2px;' + (result[address] ? 'color: #5cb85c;' : '');
          var replaceAddress = address.replace(/"/g, '');
          var receiverText = replaceAddress;
          if (replaceAddress.indexOf('<') > -1) {
            receiverText = replaceAddress.substring(0, replaceAddress.indexOf('<'));
          }
          if (receiverText.length > 8) {
            receiverText = receiverText.substring(0, 8) + '...';
          }
          var $tr = $('<tr>').append(
            $('<td>', { title: replaceAddress }).text(receiverText),
            $('<td>').append($('<span>', { class: iconClass, style: iconStyle }).html(''), $('<span>').text(tip))
          );
          $table.append($tr);
        }
      }
      return $table[0].outerHTML;
    },

    statusTableHtml: function ($table, revokeStatusAll, mails, type) {
      var $tra = $('<tr>').append($('<td>').text(type), $('<td>').text('投递状态'), $('<td>').text('是否已读'));
      $table.append($tra);
      for (var address in mails) {
        var statusAll = mails[address];
        var status = statusAll[0];
        var isRead = statusAll[1];
        var revokeStatus = statusAll[2];
        var readTime = statusAll[3];
        var tip = '';

        if (status == -1) {
          tip = '收件人邮箱空间不足';
        } else if (status == 0) {
          tip = '投递中';
        } else if (status == 1 || status == 2) {
          tip = '投递成功';
        } else if (status == 3) {
          tip = '地址不存在';
        } else if (status == 4) {
          tip = '未开启公网邮箱';
        } else if (status == 5) {
          tip = '无效邮件地址';
        } else if (status == 6) {
          tip = '邮件服务异常';
        }
        var iconClass = 'mail_icon ';
        var iconStyle = 'margin-right: 2px;';
        if (status == -1) {
          iconClass = 'iconfont icon-ptkj-weixianjinggaotishiyuqi';
          iconStyle = iconStyle + 'color:red;';
        } else if (status == 0) {
          iconClass = iconClass + 'mail_send_process';
        } else if (status == 1 || status == 2) {
          iconClass = iconClass + 'mail_send_success';
        } else {
          iconClass = iconClass + 'mail_send_fail';
        }

        var replaceAddress = address.replace(/"/g, '');
        var receiverText = replaceAddress;
        if (replaceAddress.indexOf('<') > -1) {
          receiverText = replaceAddress.substring(0, replaceAddress.indexOf('<'));
        }
        if (receiverText.length > 8) {
          receiverText = receiverText.substring(0, 8) + '...';
        }
        var tdstats = $('<td>').append($('<span>', { class: iconClass, style: iconStyle }).html(''), $('<span>').text(tip));
        if (revokeStatusAll != null && revokeStatusAll > 0) {
          var color = '#ef4444';
          var text = '';
          if (revokeStatus == 1) {
            color = '#0ea006';
            text = '[撤回成功]';
          } else if (revokeStatus == 0) {
            text = '[撤回失败]</br>对方已读';
          } else if (revokeStatus == 2) {
            text = '[撤回失败]</br>外部邮箱无法撤回';
          }
          tdstats.append($('<span>', { style: 'color:' + color }).html(text));
        }
        var isReadIconClass = '';
        if (isRead == 1) {
          isReadIconClass = 'mail_readed';
        } else if (isRead == 0) {
          isReadIconClass = 'mail_not_read';
        }
        var readIcon = $('<span>', {
          class: 'mail_icon ' + isReadIconClass,
          style: 'margin-top:2px;'
        });
        var readText = '';
        if (isRead == 1) {
          readText = '已读';
          if (readTime) {
            readText = readText + '(' + readTime + ')';
          }
        } else if (isRead == 0) {
          readText = '未读';
        }
        var $tr = $('<tr>').append(
          $('<td>', { title: replaceAddress }).text(receiverText),
          tdstats,
          $('<td>').append(readIcon, $('<span>').text(readText))
        );
        $table.append($tr);
      }
    },

    popFetchStatusDetailDialog: function () {
      var _this = this;
      JDS.call({
        mask: true,
        async: true,
        service: 'wmWebmailOutboxService.querySendStatus',
        data: [this.bean.mailboxUuid, false],
        version: '',
        success: function (result) {
          var data = result.data;
          var tableHtml = '';
          if (data.noToMailAddress) {
            if (data.msg) {
              tableHtml = '<span>' + data.msg + '</span>';
            } else {
              tableHtml = '<span>未找到具体收件人</span>';
            }
            var options = { title: '投递详情', message: tableHtml, size: 'middle' };
            appModal.dialog(options);
            return;
          }
          if (data.revokeStatus == 1 && data.sendStatus == 0) {
            tableHtml = '<span>未发送，已全部撤回</span>';
            var options = { title: '投递详情', message: tableHtml, size: 'middle' };
            appModal.dialog(options);
          } else {
            var toMails = data['To'];
            var bccMails = data['Bcc'];
            var ccMails = data['Cc'];
            var $table = $('<table>', { class: 'table table-striped fetchMailTable' });
            if (!$.isEmptyObject(toMails)) {
              _this.statusTableHtml($table, data.revokeStatus, toMails, '收件');
            }
            if (!$.isEmptyObject(ccMails)) {
              _this.statusTableHtml($table, data.revokeStatus, ccMails, '抄送收件');
            }
            if (!$.isEmptyObject(bccMails)) {
              _this.statusTableHtml($table, data.revokeStatus, bccMails, '密送收件');
            }
            tableHtml = $table[0].outerHTML;
            var message = tableHtml ? tableHtml : '<span>暂无信息</span>';
            var options = { title: '投递详情', message: message, size: 'middle' };
            appModal.dialog(options);
          }
        },
        error: function (jqXHR) {}
      });
    },
    loadMail: function () {
      var _this = this;
      JDS.restfulGet({
        url: ctx + '/proxy/api/mail/manager/get',
        data: { mailboxUuid: this.mailboxUuid },
        contentType: 'application/x-www-form-urlencoded',
        success: function (result) {
          appModal.hideMask();
          if (result.code == 0) {
            var res = result.data;
            $('#mailbody', _this.widget.element).removeClass('hidden');
            _this.bean = res;
            _this.loadButtons();
            _this.markReaded();
            _this.loadMarkButtons();
            _this.loadMoveButtonAction();

            _this.bindEvent();
            // 初使化邮件数据
            _this.onGetMailData(_this.bean);
          } else {
            window.location = ctx + '/pt/common/404.jsp';
          }
        }
      });
    },

    markReaded: function () {
      if (this.bean.isRead !== '1') {
        var uid = this.bean.mailboxUuid;
        JDS.call({
          service: 'wmWebmailService.markRead',
          data: [uid, null],
          version: ''
        });
      }
    },

    loadCssLink: function () {
      // $('head').append(
      //   $('<link>', {
      //     href: staticPrefix + '/js/pt/css/webmail/wm_webmail.css',
      //     rel: 'stylesheet'
      //   }),
      //   $('<link>', {
      //     href: staticPrefix + '/js/pt/css/webmail/wm_webmail_v2.css',
      //     rel: 'stylesheet'
      //   }),
      //   $('<link>', {
      //     href: staticPrefix + '/js/jquery.tag-editor/jquery.tag-editor.css',
      //     rel: 'stylesheet'
      //   })
      // );
    },

    buttons: {
      btn_delete: {
        class: 'well-btn w-btn-danger btn-minier smaller iconfont icon-ptkj-shanchu btn_delete',
        name: '删除'
      },
      btn_edit_again: {
        class: 'well-btn w-btn-primary btn-minier smaller iconfont icon-oa-zhuanban btn_edit_again',
        name: '再次编辑'
      },
      btn_transfer: {
        class: 'well-btn w-btn-primary btn-minier smaller iconfont icon-oa-zhuanban btn_transfer',
        name: '转发'
      },
      btn_reply: {
        class: 'well-btn w-btn-primary btn-minier smaller iconfont icon-ptkj-xinjianzhengwen btn_reply',
        name: '回复'
      },
      btn_reply_all: {
        class: 'well-btn w-btn-primary btn-minier smaller iconfont icon-ptkj-xinjianzhengwen btn_reply_all',
        name: '回复全部'
      },
      btn_revoke: {
        class: 'well-btn w-btn-primary btn-minier smaller iconfont icon-oa-chehui btn_revoke',
        name: '撤回'
      },
      btn_resend: {
        class: 'well-btn w-btn-primary btn-minier smaller iconfont icon-ptkj-tijiaofabufasong btn_resend',
        name: '重新发送'
      }
    },

    loadButtons: function () {
      var _this = this;
      var buttons = this.buttons;
      var _buttons = [];
      if (this.bean.status == 0 && this.bean.sendTime != null && this.bean.sendStatus == 0) {
        var $btn = $('<button>', {
          class: buttons.btn_delete.class,
          name: buttons.btn_delete.name
        }).text('  ' + buttons.btn_delete.name);
        $('#mail_buttons').html($btn);
        $('#mail_buttons').show();
      } else {
        if (this.bean.mailboxName === 'INBOX') {
          _buttons = [buttons.btn_transfer, buttons.btn_reply, buttons.btn_reply_all, buttons.btn_delete];
        } else if (this.bean.mailboxName === 'OUTBOX') {
          if (this.bean.revokeStatus == null && this.bean.sendStatus == 3) {
            _buttons.push(buttons.btn_resend);
          }
          _buttons.push(buttons.btn_edit_again, buttons.btn_transfer, buttons.btn_reply, buttons.btn_reply_all, buttons.btn_delete);
          if (this.bean.revokeStatus == null && this.bean.status != -1) {
            _buttons.push(buttons.btn_revoke);
          }
        } else {
          _buttons = [buttons.btn_transfer, buttons.btn_reply, buttons.btn_reply_all, buttons.btn_delete];
        }
        for (var i = 0; i < _buttons.length; i++) {
          var $btn = $('<button>', {
            class: _buttons[i].class,
            name: _buttons[i].name
          }).text('  ' + _buttons[i].name);
          $btn.insertBefore($('#prevMail', _this.widget.element));
        }
        $('#mail_buttons').show();
      }
    },

    //禁止双击按钮
    forbidButtonDoubleClick: function () {
      var $container = this.widget.element;
      $('#mail_button_group button', $container).prop('disabled', true);
      window.setTimeout(function () {
        $('#mail_button_group button', $container).prop('disabled', false);
      }, 300);
    },

    refreshParentWindowBadgeNum: function () {
      window.opener.appContext.pageContainer.trigger('AppEmail.Change');
    },

    //刷新父窗口页面的右侧邮件列表
    refreshParentWindowEmailList: function () {
      window.opener.appContext.pageContainer.trigger('AppEmailList.Refresh');
    },

    fixnumber: function (num, n) {
      return (Array(n).join(0) + num).slice(-n);
    },

    onGetMailData: function (data) {
      var $container = this.widget.element;
      for (var key in data) {
        $('#' + key, $container).text(data[key]);
      }
      window.document.title = data.subject;

      if (data.ccUserName) {
        $('#ccUserName', $container).parent().parent().removeClass('hidden');
      }

      if (data.bccUserName && data.mid != null && data.mid != '' && data.sendTime != null) {
        $('#bccUserName', $container).parent().parent().removeClass('hidden');
      } else {
        $('#bccUserName', $container).parent().parent().remove();
      }
      if (data.mailboxName == 'OUTBOX' && data.mid == null && data.status != 0) {
        $('.mailSendstatus', $container).parent().parent().removeClass('hidden');
        if (data.sendStatus == null) {
          data.sendStatus = 1;
        }
        if (data.sendStatus > 1) {
          data.sendStatus = 2;
        }
        if (data.revokeStatus != null && data.revokeStatus != 0) {
          data.sendStatus = -1;
        }
        $('.mailSendstatus[data-sendstatus="' + data.sendStatus + '"]', $container).show();
      }

      if (data.sendTime != null) {
        var date = new Date(data.sendTime);
        var day = ['日', '一', '二', '三', '四', '五', '六'][date.getDay()];
        var text =
          date.getFullYear() +
          '年' +
          this.fixnumber(date.getMonth() + 1, 2) +
          '月' +
          this.fixnumber(date.getDate(), 2) +
          '日 ' +
          this.fixnumber(date.getHours(), 2) +
          ':' +
          this.fixnumber(date.getMinutes(), 2) +
          ' (星期' +
          day +
          ')';
        if (data.status == 0) {
          text = '此邮件是定时邮件，将在【' + text + "】发出 。 <a id = 'updateTime'>修改时间</a>";
        }
        $('#sendTime', $container).html(text);
        var _this = this;
        $('#updateTime').bind('click', function () {
          _this.dialogTimeSend(_this);
        });
      }

      //收件邮件被撤回，不需要加载附件了
      if (!(data.mailboxName == 'INBOX' && data.revokeStatus == '1')) {
        this.fileupload.setBtnShowType('2');
        var dbFiles = data.repoFiles;
        this.fileupload.initAllowUploadDeleteDownload(false, false, true); //已发送的邮件，附件不允许上传删除
        this.fileupload.init(true, $(this.fileElSelector), false, true, dbFiles);
      }

      $('.inbox-content').append(
        $('<iframe>', {
          id: 'mailContentIfm',
          name: 'mailContentIfm',
          /*"scrolling": "no",*/
          style: 'width:100%;border:0px;'
        })
      );
      var _this = this;
      setTimeout(function () {
        if (data.content.indexOf('!DOCTYPE') != -1) {
          $('html', window.frames['mailContentIfm'].document).html(data.content);
        } else {
          //纯文本内容
          $('html', window.frames['mailContentIfm'].document).html(
            $('<pre>', { style: 'word-wrap: break-word;white-space: pre-wrap;' }).html(data.content)[0].outerHTML
          );
        }
      }, 16);

      setTimeout(function () {
        var ifm = $('#mailContentIfm', _this.widget.element);
        var subWeb = window.frames['mailContentIfm'].document;
        if (subWeb != null) {
          ifm.height(subWeb.documentElement.clientHeight);
          $('<base>', { target: '_blank' }).insertBefore($($(subWeb.documentElement).children()[0])); //iframe内的超链接默认点击新开窗口
          $('#mailContentIfm').attr('scrolling', 'no');
          var aa = window.setInterval(function () {
            //调整iframe高度
            if (subWeb.documentElement.clientHeight != ifm.height()) {
              ifm.height(subWeb.documentElement.clientHeight);
              clearInterval(aa);
            }
          }, 200);
        }
        if (_this.bean.sendStatus == null && _this.bean.readReceiptStatus == '1' && _this.bean.revokeStatus != '1') {
          $('.email-receipt').show();
        }
      }, 16);
    },

    dialogTimeSend: function (_this) {
      var title = '修改时间';
      var dlgId = 'dlgTimingSend';
      var message = "</br></br><div id='" + dlgId + "'></div>";
      var nowDate = new Date(_this.bean.sendTime);
      nowDate = nowDate.format('yyyy-MM-dd HH:mm');
      var dlgOptions = {
        title: title,
        message: message,
        templateId: '',
        size: 'middle',
        shown: function () {
          formBuilder.buildDatetimepicker({
            container: $('#' + dlgId),
            label: '选择定时发送的时间',
            labelColSpan: 5,
            controlColSpan: 5,
            isRequired: true,
            name: 'sendTime',
            value: nowDate,
            events: 'change',
            controlOption: {
              change: function () {
                $('#div_sendTime .error').hide();
                var sendTime = $('#sendTime', $('#' + dlgId)).val();
                if (StringUtils.isBlank(sendTime)) {
                  $('#div_sendTime .error').show();
                }
              }
            },
            timePicker: {
              format: 'datetime|yyyy-MM-dd HH:mm'
            }
          });
          $('#div_sendTime').append('<label class="error" style="text-align: left;display:none;">不能为空！</label>');
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              $('#div_sendTime .error').hide();
              var sendTime = $('#sendTime', $('#' + dlgId)).val();
              if (StringUtils.isBlank(sendTime)) {
                $('#div_sendTime .error').show();
                return false;
              }
              if (new Date(sendTime) <= new Date()) {
                appModal.error('您设置的定时时间已过期！');
                return false;
              }
              //保存草稿 定时发送
              _this.bean.sendTime = sendTime + ':00';
              JDS.call({
                service: 'wmWebmailService.updateTime',
                data: [_this.bean],
                async: false,
                success: function (result) {
                  appModal.success('保存成功！', function () {
                    var url = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-openmail.html';
                    var mailboxUuid = result.data.mailboxUuid;
                    _this.refreshParentWindowBadgeNum();
                    if (!_this.quickSendMailFrame) {
                      _this.refreshParentWindowEmailList();
                    }
                    window.location = url + '?mailboxUuid=' + mailboxUuid + '&boxname=DRAFT';
                  });
                },
                error: function (jqXHR) {
                  appModal.hideMask();
                  var msg = '';
                  if (jqXHR.responseJSON && jqXHR.responseJSON.msg) {
                    msg = jqXHR.responseJSON.msg;
                  }
                  appModal.alert('保存失败!</br>' + msg);
                }
              });

              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default'
          }
        }
      };
      appModal.dialog(dlgOptions);
    },

    getMailListDataProvider: function () {
      var providerStr = window.sessionStorage.getItem('mailListDataProvider');
      var _this = this;
      if (providerStr) {
        var cacheProvider = JSON.parse(providerStr);
        cacheProvider.options.onDataChange = _this.pageDataChange;
        _this.$mailListDataProvider = new dataStoreBase(cacheProvider.options);
        _this.$mailListDataProvider.data = cacheProvider.data;
        _this.$mailListDataProvider.totalCount = cacheProvider.totalCount;
        _this.$mailListDataProvider.loaded = cacheProvider.loaded;
        return;
      }
      if (window.opener) {
        //获取邮件列表的数据源
        var $divBtable = $(window.opener.document).find('.ui-wBootstrapTable');
        if ($divBtable.length > 0) {
          for (var i = 0, len = $divBtable.length; i < len; i++) {
            var widgetId = $($divBtable[i]).attr('id');
            var widget = window.opener.appContext.getWidgetById(widgetId);
            if (widget && widget._dataProvider.options.dataStoreId == 'CD_DS_20170525151730') {
              //平台邮件数据源
              _this.$mailListDataProvider = $.extend(true, {}, widget._dataProvider);
              //翻页加载数据成功后的执行函数
              _this.$mailListDataProvider.options.onDataChange = _this.pageDataChange;
              break;
            }
          }
        }
      }
    },

    pageDataChange: function (data, totalCount, params) {
      var _this = this;
      var $container = this.widget.element;
      if (params.prevPage) {
        var prevPageDataLastOne = _.last(data);
        if (prevPageDataLastOne) {
          $('#prevMail', $container).attr({
            uuid: prevPageDataLastOne.UUID,
            title: prevPageDataLastOne.SUBJECT,
            jumpPage: true
          });
          $('#prevMail', $container).removeClass('w-disable-btn');
        } else {
          $('#prevMail', $container).prop('disabled', true);
          $('#prevMail', $container).addClass('w-disable-btn');
        }
      } else if (params.nextPage) {
        var nextPageDataLastOne = _.first(data);
        if (nextPageDataLastOne) {
          $('#nextMail', $container).attr({
            uuid: nextPageDataLastOne.UUID,
            title: nextPageDataLastOne.SUBJECT,
            jumpPage: true
          });
          $('#nextMail', $container).removeClass('w-disable-btn');
        } else {
          $('#nextMail', $container).prop('disabled', true);
          $('#nextMail', $container).addClass('w-disable-btn');
        }
      }
    },

    //初始化上一封、下一封邮件事件
    initPrevAndNextButtonEvent: function () {
      this.getMailListDataProvider();
      var _this = this;
      var $container = this.widget.element;
      if (_this.$mailListDataProvider) {
        var index = _.findIndex(_this.$mailListDataProvider.data, { UUID: _this.bean.mailboxUuid });
        var prevUuid = index == 0 || index == -1 ? null : _this.$mailListDataProvider.data[index - 1].UUID;
        var nextUuid = index == _this.$mailListDataProvider.data.length - 1 ? null : _this.$mailListDataProvider.data[index + 1].UUID;
        var currentPageMailData = [].concat(_this.$mailListDataProvider.data);
        var currentPageMailOptions = $.extend(true, {}, _this.$mailListDataProvider.options);
        var currentPage = _this.$mailListDataProvider.options.currentPage;
        var currentTotalCount = _this.$mailListDataProvider.totalCount;
        if (prevUuid) {
          $('#prevMail', $container).attr({
            uuid: prevUuid,
            title: currentPageMailData[index - 1].SUBJECT
          });
          $('#prevMail', $container).removeClass('w-disable-btn');
        } else if (_this.$mailListDataProvider.hasPrevious()) {
          //没有上一封，但是有上一页数据
          _this.$mailListDataProvider.previousPage({ prevPage: true }); //触发加载上一页数据
        } else {
          $('#prevMail', $container).prop('disabled', true); //没有上一封且上一页数据，则按钮不可点击
          $('#prevMail', $container).addClass('w-disable-btn');
        }

        if (nextUuid) {
          $('#nextMail').attr({
            uuid: nextUuid,
            title: currentPageMailData[index + 1].SUBJECT
          });
          $('#nextMail').removeClass('w-disable-btn');
        } else if (_this.$mailListDataProvider.hasNext()) {
          //没有下一封数据，但有下一页数据
          _this.$mailListDataProvider.nextPage({ nextPage: true }); //触发加载下一页数据
        } else {
          $('#nextMail').prop('disabled', true); //没有下一封且下一页数据，则按钮不可点击
          $('#nextMail').addClass('w-disable-btn');
        }

        $('.prevOrNext').on('click', function () {
          var uuid = $(this).attr('uuid');
          if (uuid) {
            //设置缓存数据源
            var cacheDataProvider = {
              options: currentPageMailOptions,
              data: currentPageMailData,
              loaded: true,
              totalCount: currentTotalCount
            };
            var isJumpPage = $(this).attr('jumpPage');
            if (isJumpPage) {
              //翻页的话，要更新缓存的数据到指定页的数据
              cacheDataProvider.data = _this.$mailListDataProvider.data;
              cacheDataProvider.totalCount = _this.$mailListDataProvider.totalCount;
              cacheDataProvider.options.currentPage = _this.$mailListDataProvider.options.currentPage;
            }
            delete cacheDataProvider.options['onDataChange'];
            delete cacheDataProvider.options['receiver'];
            window.sessionStorage.setItem('mailListDataProvider', JSON.stringify(cacheDataProvider));
            window.location =
              '/web/app/pt-app/pt-webmail/pt-webmail-openmail.html?pageUuid=9a6037a5-52fe-4d82-86c0-1d068a6c0b51&mailboxUuid=' + uuid;
          }
        });
        //绑定方向建的左右对应上下封信
        $(window).on('keyup', function (e) {
          var currKey = 0,
            e = e || event;
          currKey = e.keyCode || e.which || e.charCode;
          if (currKey == 39) {
            if ($('#nextMail').is(':enabled')) {
              $('#nextMail').trigger('click');
            }
          }
          if (currKey == 37) {
            if ($('#prevMail').is(':enabled')) {
              $('#prevMail').trigger('click');
            }
          }
        });
      }
    },

    loadMarkButtons: function () {
      var _this = this;
      //加载标记按钮
      this.emailListDevelopment.loadTagMailButtonGroup();
      var $binder = $('.email-container', this.widget.element);

      //标记邮件
      $binder.off('click', '.js-markMailTag').on('click', '.js-markMailTag', function () {
        _this.emailListDevelopment.markMailByTag([_this.bean.mailboxUuid], $(this).attr('tagUuid'), function () {
          $binder.trigger('EmailTagChange');
          appModal.toast({ message: '标记成功', type: 'success' });
        });

        _this.refreshParentWindowEmailList();
      });

      //标记标签hover展示 [x]关闭
      $binder.on('hover', '.emailTrTagDiv', function (e) {
        if (e.type == 'mouseenter') {
          $(this).addClass('emailTrTagHover');
        } else {
          $(this).removeClass('emailTrTagHover');
        }
      });

      //标签[x] 移除标记
      $binder.on('click', '.emailTrTagClose', function (e) {
        var $parentDiv = $(this).parents('.emailTrTagDiv');
        _this.emailListDevelopment.removeMailTags([_this.bean.mailboxUuid], $parentDiv.attr('tagUuid'), function () {
          $binder.trigger('EmailTagChange');
          appModal.toast({ message: '取消标记成功', type: 'success' });
        });
        _this.refreshParentWindowEmailList();
      });
      //取消所有标签
      $binder.on('click', '.li_class_removeSelectedMailTags', function (e) {
        _this.emailListDevelopment.removeMailTags([_this.bean.mailboxUuid], null, function () {
          $binder.trigger('EmailTagChange');
          appModal.toast({ message: '取消所以标签成功', type: 'success' });
        });
        _this.refreshParentWindowEmailList();
      });

      //展示标签二级导航
      var hideTimeout = null;
      $binder.on('hover', '.li_class_tags', function (e) {
        if (e.type == 'mouseenter') {
          window.clearTimeout(hideTimeout);
          $('.jsTagButtonsDiv').show();
        } else {
          hideTimeout = window.setTimeout(function () {
            $('.jsTagButtonsDiv').hide();
          }, 300);
        }
      });

      //新建标签并标记
      $binder.off('click', '.li_class_addTagAndMark').on('click', '.li_class_addTagAndMark', function (e) {
        _this.appEmailTagDevelopment.popDialog(null, { title: '新建标签并标记' }, function (tagUuid) {
          _this.emailListDevelopment.markMailByTag([_this.bean.mailboxUuid], tagUuid, function () {
            $binder.trigger('EmailTagChange');
          });
          _this.appEmailTagDevelopment.refreshLeftSideMenus({ expand: false });
          _this.emailListDevelopment.loadTagMailButtonGroup(true);
          _this.refreshParentWindowEmailList();
        });
      });

      //查询最新标签并渲染
      $binder.on('EmailTagChange', function () {
        // tagDiv
        JDS.call({
          service: 'wmMailTagFacadeService.queryMailRelaTag',
          data: [[_this.bean.mailboxUuid]],
          success: function (result) {
            var $tagDiv = $('#tagDiv');
            $tagDiv.find('.emailTrTagDiv').remove();
            if (result.data[_this.bean.mailboxUuid]) {
              var tags = result.data[_this.bean.mailboxUuid];
              for (var j in tags) {
                var $textSpan = $('<span>', {
                  class: 'emailTrTagTextSpan',
                  style: 'background:' + tags[j].tagColor + ''
                }).text(tags[j].tagName);
                var $closeSpan = $('<span>', {
                  class: 'emailTrTagClose jsTagDelete',
                  style: 'background:' + tags[j].tagColor + ';'
                }).append($('<b>', { class: 'emailTrTagCloseIcon jsTagDelete' }).text('x'));
                var color = _this.emailListDevelopment.getAdjustFontColor(tags[j].tagColor);
                if (color) {
                  $textSpan.css('color', color);
                  $closeSpan.css('color', color);
                }
                var $div = $('<div>', {
                  class: 'emailTrTagDiv',
                  tagUuid: tags[j].uuid,
                  emailUuid: _this.bean.mailboxUuid
                }).append($textSpan, $closeSpan);

                $tagDiv.append($div);
              }
            }
          },
          error: function (jqXHR) {}
        });
      });
      $binder.trigger('EmailTagChange');
    },

    loadMoveButtonAction: function () {
      var _this = this;
      //加载文件按钮
      _this.emailListDevelopment.loadMoveFolderButtonGroup();
      var $binder = $('.email-container');

      //移动邮件到指定文件夹点击事件
      $binder
        .off('click', '.js-moveToOwnFolder,.li_class_moveToInBox')
        .on('click', '.js-moveToOwnFolder,.li_class_moveToInBox', function () {
          var folder;
          if ($(this).is('.li_class_moveToInBox')) {
            folder = 'INBOX';
          } else {
            folder = $(this).attr('folderCode');
          }
          var folderName = $(this).find('a').text();
          _this.emailListDevelopment.moveEmailToFolder(folder, [_this.bean.mailboxUuid], function () {
            appModal.info('移动邮件成功');
            _this.refreshParentWindowEmailList();
          });
        });

      $binder.off('click', '.li_class_addFolderAndMoveIn').on('click', '.li_class_addFolderAndMoveIn', function () {
        _this.emailListDevelopment.popAddFolderDialog([_this.bean.mailboxUuid]);
      });
    }
  });
  return AppEmailDetailHtmlDevelopment;
});
