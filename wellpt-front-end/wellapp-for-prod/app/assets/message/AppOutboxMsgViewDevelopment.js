define([
  'jquery',
  'commons',
  'constant',
  'server',
  'ViewDevelopmentBase',
  'appModal',
  'multiOrg',
  'ckeditor',
  'alerts',
  'appContext'
], function ($, commons, constant, server, ViewDevelopmentBase, appModal, multiOrg, appContext) {
  var JDS = server.JDS;
  var OutboxMsgModule = function () {
    ViewDevelopmentBase.apply(this, arguments);
  };

  // 预设按钮信息
  var presetEventsInfo = {
    forward: {
      type: 'preset',
      text: '转发',
      group: '',
      code: 'btnForwardMsg',
      eventManger: {},
      uuid: 'C78EA36C02400001A8DA914010C01D4B',
      position: ['5-1'],
      displayLocation: ['message-detail', 'message-list'],
      btnLib: {
        btnColor: 'w-btn-primary',
        type: 'primary',
        btnInfo: {
          class: 'w-line-btn',
          type: 'line',
          type_name: '线框按钮',
          status: [
            {
              class: '',
              text: '普通状态'
            },
            {
              class: 'hover',
              text: '鼠标移入状态'
            },
            {
              class: 'active',
              text: '点击状态'
            },
            {
              class: 'w-disable-btn',
              text: '禁用状态'
            }
          ]
        },
        btnSize: '',
        iconInfo: {
          fileIDs: 'iconfont icon-oa-zhuanban',
          filePaths: 'iconfont icon-oa-zhuanban',
          fileType: 3
        }
      }
    },
    reply: {
      type: 'preset',
      text: '回复',
      group: '',
      code: 'btnReplyMsg',
      eventManger: {},
      displayLocation: [],
      position: ['5-1'],
      btnLib: {
        btnColor: 'w-btn-primary',
        type: 'primary',
        btnInfo: {
          class: 'w-line-btn',
          type: 'line',
          type_name: '线框按钮',
          status: [
            {
              class: '',
              text: '普通状态'
            },
            {
              class: 'hover',
              text: '鼠标移入状态'
            },
            {
              class: 'active',
              text: '点击状态'
            },
            {
              class: 'w-disable-btn',
              text: '禁用状态'
            }
          ]
        },
        iconInfo: {
          fileIDs: 'iconfont icon-ptkj-wentifankui',
          filePaths: 'iconfont icon-ptkj-wentifankui',
          fileType: 3
        }
      }
    },
    delete: {
      type: 'preset',
      text: '删除',
      group: '',
      code: 'btnDelMsg',
      displayLocation: ['message-list'],
      eventManger: {},
      uuid: 'C78EA36D0BC000018859CF707CF5DF60',
      position: ['5-1'],
      btnLib: {
        btnColor: 'w-btn-primary',
        type: 'primary',
        btnInfo: {
          class: 'w-line-btn',
          type: 'line',
          type_name: '线框按钮',
          status: [
            {
              class: '',
              text: '普通状态'
            },
            {
              class: 'hover',
              text: '鼠标移入状态'
            },
            {
              class: 'active',
              text: '点击状态'
            },
            {
              class: 'w-disable-btn',
              text: '禁用状态'
            }
          ]
        },
        btnSize: '',
        iconInfo: {
          fileIDs: 'iconfont icon-ptkj-shanchu',
          filePaths: 'iconfont icon-ptkj-shanchu',
          fileType: 3
        }
      }
    }
  };

  //初始化富文本框
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
        paste: function (evt) {
          handleCkeditorPaste(evt);
        },
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

  //初始化组织弹出框
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

  //初始化附件上传
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

  commons.inherit(OutboxMsgModule, ViewDevelopmentBase, {
    //通过trigger来刷新对应的徽章数量
    refreshBadgeNum: function () {
      this.getPageContainer().trigger('AppMsg.Change');
      var _top_widgets = top.appContext.widgetMap;
      $.each(_top_widgets, function (i, item) {
        if (i.indexOf('wNewHeader_') > -1) {
          top.appContext.getWidgetById(i).refreshBadge();
        }
      });
    },

    init: function () {
      var height = $(window.top.document).find('.modal-body').height();
      $(this.widget.element).parents('.web-app-container.container-fluid').css({
        paddingBottom: 0
      });

      $(this.widget.element)
        .parent('.panel-body')
        .css({
          maxHeight: height - 90 + 'px',
          'overflow-y': 'auto'
        });
    },

    currentRowNum: null,
    onClickRow: function (rowNum, row, $element, field, e) {
      this.currentRowNum = rowNum;
      if ($('.onlySelectItem').size() > 0) {
        if ($element.find('.underPadding').hasClass('checked')) {
          $element.find('.underPadding').removeClass('checked');
        } else {
          $element.find('.underPadding').addClass('checked');
        }
      } else if (!$(e.target).hasClass('well-btn')) {
        var _self = this;
        var title = '发件消息   ' + (row['SENT_TIME'] || row['sendTime'] || row['sentTime']).substr(0, 19);
        var outboxUuid = row['UUID'];
        var buttons = {
          forwardMsg: {
            label: '转发',
            className: 'well-btn w-btn-primary',
            callback: function () {
              //必须先删除旧的dialog，才能起新的DIALOG,不能会有ID冲突，导致富文本框和附件上传控件出不来的问题
              $dialog.remove();
              $('.modal-backdrop').remove();
              _self.showForwardDialog(row);
            }
          },
          retractMsg: {
            label: '撤回',
            className: 'well-btn w-btn-primary',
            callback: function () {
              $.ajax({
                type: 'POST',
                url: ctx + '/message/content/retractMessage',
                data: 'uuids=' + row['UUID'],
                dataType: 'text',
                success: function () {
                  appModal.alert('撤回成功！');
                }
              });
            }
          }
        };
        var $dialog = this.viewMsg(row, buttons, title, outboxUuid);
      }
    },

    //新建消息按钮对应的事件
    btnNewMsg: function (args) {
      var dialogHtml = this.getDialogHtml('', '', '', '', '', '', '');
      var $newDialog = this.showSendMsgDialog(dialogHtml, '新建消息', null);
    },

    //转发按钮对应的事件
    btnForwardMsg: function (args) {
      args.stopPropagation();
      args.preventDefault();

      var index = this.currentRowNum;
      var data = this.currentViewRow || this.getData()[index];
      var uuids = [data.UUID];
      if (uuids.length == 0) {
        appModal.alert('请选择记录！');
      } else if (uuids.length != 1) {
        appModal.alert('一次只能选择一条记录！');
      } else {
        this.showForwardDialog(data);
      }
    },

    //弹出转发对话框
    showForwardDialog: function (row) {
      var dialogHtml = this.getForwardDialogHtml(row);
      var outboxUuid = row['UUID'] || row['uuid'];
      var $forwardDialog = this.showSendMsgDialog(dialogHtml, '转发消息', outboxUuid);
    },

    //删除按钮对应的事件
    btnDelMsg: function (args, $dialog) {
      args.stopPropagation();
      args.preventDefault();

      var index = this.currentRowNum;
      var data = this.currentViewRow || this.getData()[index];
      var uuids = [data.UUID];
      if (uuids.length == 0) {
        appModal.alert('请选择记录！');
      } else {
        var _self = this;
        console.log(uuids);
        appModal.confirm({
          message: '确认要删除吗?',
          shown: function () {
            _self.getMask();
          },
          callback: function (result) {
            if (result) {
              $.ajax({
                type: 'POST',
                url: ctx + '/message/content/deleteOutboxMessage',
                data: 'uuids=' + uuids,
                dataType: 'text',
                success: function () {
                  if (_self.getWidget()) {
                    _self.refresh();
                  }
                  if ($dialog) {
                    $dialog.next('.modal-backdrop').remove();
                    $dialog.remove();
                  }
                }
              });
            }
            _self.cancelMask();
          }
        });
      }
    },

    //回复按钮对应的事件
    btnReplyMsg: function (args) {
      // var selections = this.getSelections();
      // var uuids = this.getSelectedRowIds(selections);
      args.stopPropagation();
      args.preventDefault();

      var index = this.currentRowNum;
      var data = this.currentViewRow || this.getData()[index];
      var uuids = [data.UUID];
      if (uuids.length == 0) {
        appModal.error('请选择记录！');
      } else if (uuids.length != 1) {
        appModal.error('一次只能选择一条记录！');
      } else {
        this.showReplyDialog(data);
      }
    },

    //展示回复对话框
    showReplyDialog: function (row) {
      var _self = this;
      var dialogHtml = this.getReplyDialogHtml(row);
      var outboxUuid = row['MESSAGE_OUTBOX_UUID'] || row['messageOutboxUuid'];
      var $replyDialog = this.showSendMsgDialog(dialogHtml, '回复消息', outboxUuid, function () {
        var index = _self.getSelectionIndexes()[0];
        _self.widget.$tableElement.find('tr[data-index=' + index + ']').removeClass('unread');
        if (row['ISREAD'] == 0 || row['isRead'] == 0) {
          _self.readMsgService([row['UUID' || uuid]]);
        }
      });
    },

    getReplyDialogHtml: function (row) {
      var senderName = row['SENDER_NAME'] || row['senderName'];
      var subject = row['SUBJECT'] || row['subject'];
      var body = row['BODY'] || row['body'];
      var senderTime = row['SENT_TIME'] || row['sendTime'] || row['sentTime'];
      var newSubject = 'Re:' + subject;

      var newBody = this.getReplybody(senderName, subject, body, senderTime);
      var relatedUrl = row['RELATED_URL'] || row['relatedUrl'];
      var relatedTitle = row['RELATED_TITLE'] || row['relatedTitle'];
      var userId = row['SENDER'] || row['sender'];
      var userName = row['SENDER_NAME'] || row['senderName'];
      var dialogHtml = this.getDialogHtml(userId, userName, newSubject, newBody, '', relatedUrl, relatedTitle);
      return dialogHtml;
    },

    /**
     * 已读服务
     * @param uuids
     * @param successCallback
     */
    readMsgService: function (uuids, successCallback) {
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
    },

    //收件箱标星按钮对应的事件
    btnMarkStar: function (args) {
      var uuids = this.getSelectedRowIds(this.getSelections());
      if (uuids.length == 0) {
        appModal.alert('请选择记录！');
      } else {
        var _self = this;
        $.ajax({
          type: 'POST',
          url: ctx + '/message/content/markoutboxflag',
          data: 'uuids=' + uuids + '&markflag=1',
          dataType: 'text',
          success: function () {
            appModal.success('标星成功！');
            _self.refresh();
          }
        });
      }
    },

    //收件箱取消标星按钮对应的事件
    btnCancelMarkStar: function (args) {
      var uuids = this.getSelectedRowIds(this.getSelections());
      if (uuids.length == 0) {
        appModal.alert('请选择记录！');
      } else {
        var _self = this;
        $.ajax({
          type: 'POST',
          url: ctx + '/message/content/markoutboxflag',
          data: 'uuids=' + uuids + '&markflag=0',
          dataType: 'text',
          success: function () {
            appModal.success('取消标星成功！');
            _self.refresh();
          }
        });
      }
    },

    btnBatchMsg: function (args) {
      var _self = this;
      var $toolbar = $(args.target).parents('.fixed-table-toolbar');
      $toolbar.find('.bs-bars').hide();
      var html =
        "<div class='msg-batch-buttons'>" +
        "<button type='button' class='well-btn w-btn-primary w-btn-minor  btn-delete-msg'><i class='iconfont icon-ptkj-shanchu'></i>删除</button>" +
        "<i title='退出批量操作' class='msg-batch-close iconfont icon-ptkj-huanyuanhuifu'></i>" +
        '</div>';
      $toolbar.append(html);
      $toolbar.siblings('.fixed-table-container').addClass('onlySelectItem');

      $toolbar.on('click', '.msg-batch-close', function () {
        $(this).parent('.msg-batch-buttons').hide();
        $toolbar.find('.bs-bars').show();
        $toolbar.siblings('.fixed-table-container').removeClass('onlySelectItem');
        $toolbar.siblings('.fixed-table-container').find('.underPadding').removeClass('checked');
      });
      $toolbar.off('click', '.btn-delete-msg').on('click', '.btn-delete-msg', function () {
        var datas = _self.getData();
        var id = $toolbar.parents('.ui-wBootstrapTable').attr('id');
        var chooseItem = $('#' + id + '_table').find('tr .checked');
        var uuids = [];
        $.each(chooseItem, function (index, item) {
          var trIndex = $(item).parent('tr').data('index');
          uuids.push(datas[trIndex].UUID);
        });
        if (uuids.length > 0) {
          appModal.confirm({
            message: '确认要删除吗?',
            className: 'confirmDialogs',
            shown: function () {
              _self.getMask();
            },
            callback: function (result) {
              _self.cancelMask();
              if (result) {
                $.ajax({
                  type: 'POST',
                  url: ctx + '/message/content/deleteOutboxMessage',
                  data: 'uuids=' + uuids,
                  dataType: 'text',
                  success: function () {
                    _self.refresh();
                  }
                });
              }
            }
          });
        } else {
          appModal.error({
            message: '请先选择要删除的记录！',
            className: 'deleteDialogs',
            shown: function () {
              _self.getMask();
            },
            callback: function () {
              _self.cancelMask();
            }
          });
        }
      });
    },

    getSelectedRowIds: function (selections) {
      var uuids = [];
      if (selections != null) {
        for (var i = 0; i < selections.length; i++) {
          uuids.push(selections[i]['UUID']);
        }
      }
      return uuids;
    },

    getDialogHtml: function (userid, userName, subject, body, checked, relatedUrl, relatedTitle) {
      var relatedRow = '';
      if (relatedUrl != '' && relatedTitle != '' && relatedUrl != null && relatedTitle != null) {
        relatedRow =
          '<tr>' +
          "<td class='Label' width='100' align='center'></td>" +
          "<td class='value'>" +
          "<div class='td_class'>" +
          "<a target='_blank' style='float:right;' href='" +
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
        "<table class='table table-hover JColResizer'>" +
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
        '/><label for="markflag">重要</label>' +
        '</td>' +
        '<td></td>' +
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
    },

    $openedDialog: null,
    currentViewRow: null,
    viewMsg: function (row, buttons, title, outboxUuId, shownCallback) {
      var _self = this;
      _self.currentViewRow = row;
      if (row['MESSAGE_PARM'] || row.messageParm) {
        var messageParm = row['MESSAGE_PARM'] || row.messageParm;
        var param = JSON.parse(messageParm);
        var relatedUrl = param.relatedUrl;
        var relatedTitle = param.relatedTitle;
      } else {
        var relatedUrl = row['RELATED_URL'];
        var relatedTitle = row['RELATED_TITLE'];
      }

      var userId = row['RECIPIENT'] || row['recipient'];
      var userName = row['RECIPIENT_NAME'] || row['recipientName'];
      var subject = row['SUBJECT'] || row['subject'];
      var body = row['BODY'] || row['body'];
      var checked = (row['MARK_FLAG'] || row['markFlag']) == '1' ? 'checked' : '';

      var dialogHtml = this.getDialogHtml(userId, userName, subject, body, checked, relatedUrl, relatedTitle);

      var $dialog = appModal.dialog({
        title: title,
        size: 'large',
        message: dialogHtml,
        buttons: buttons,
        zIndex: 9994,
        shown: function () {
          initCKEditor(true);
          initFileuploadByViewMsg(outboxUuId, true);
          $dialog.find('#showUser').attr('disabled', 'disabled');
          $dialog.find('#subject').attr('disabled', 'disabled');
          $dialog.find('#markflag').attr('disabled', 'disabled');
          if ($.isFunction(shownCallback)) {
            shownCallback();
          }
          $dialog.find('.modal-footer button').hide();

          if (row.MESSAGE_PARM || row.messageParm) {
            var messageParm = row.MESSAGE_PARM || row.messageParm;
            var param = JSON.parse(messageParm);
            if (param.callbackJson) {
              var callbackJson = JSON.parse(param.callbackJson);
              var btns = _self.getBtn(callbackJson, 'message-detail');
              $dialog.find('.modal-footer').append(btns);
            }
          }

          _self.getMask($dialog);
          $($dialog)
            .off('click', '.modal-footer .well-btn-callback')
            .on('click', '.modal-footer .well-btn-callback', function (e) {
              var $this = $(this);
              e.stopPropagation();
              e.preventDefault();
              if ($this.data('target') && $(this).data('event')) {
                // 默认转发按钮事件
                if ($this.hasClass('btnForwardMsg') || $this.parent('li').hasClass('btnForwardMsg')) {
                  _self.btnForwardMsg(e);
                  return;
                }

                // 默认删除按钮事件
                if ($this.hasClass('btnDelMsg') || $this.parent('li').hasClass('btnDelMsg')) {
                  _self.btnDelMsg(e, $dialog);
                  return;
                }

                // 默认回复按钮事件
                if ($this.hasClass('btnReplyMsg') || $this.parent('li').hasClass('btnReplyMsg')) {
                  _self.btnReplyMsg(e);
                  return;
                }

                var target = $this.data('target');
                var eventManger = $this.data('event').eventHandler;
                var eventParam = $this.data('event').params;
                if ($this.hasClass('btnExpDetail')) {
                  var url = '/web/app/page/preview/6001a4908f9afe87a4b4b9a278bff001?pageUuid=' + eventManger.id.split('_')[2];

                  var dataUuid = '';
                  if (_self.currentViewRow.messageParm) {
                    dataUuid = JSON.parse(_self.currentViewRow.messageParm).forwardDataUuid;
                  } else if (_self.currentViewRow.MESSAGE_PARM) {
                    dataUuid = JSON.parse(_self.currentViewRow.MESSAGE_PARM).forwardDataUuid;
                  }

                  url = url + '&uuid=' + dataUuid;
                  window.open(ctx + url);
                  return;
                }

                if ($this.hasClass('btnImpDetail')) {
                  var url = '/web/app/page/preview/56111a4071fa57436e77120e3d55eb67?pageUuid=' + eventManger.id.split('_')[2];

                  var dataUuid = '';
                  if (_self.currentViewRow.messageParm) {
                    dataUuid = JSON.parse(_self.currentViewRow.messageParm).forwardDataUuid;
                  } else if (_self.currentViewRow.MESSAGE_PARM) {
                    dataUuid = JSON.parse(_self.currentViewRow.MESSAGE_PARM).forwardDataUuid;
                  }

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
                  appData: top.appContext.getCurrentUserAppData().appData,
                  params: eventParam
                };
                top.appContext.pageContainer.startApp(opt);
              }
              return;
            });
        },
        callback: function () {
          _self.cancelMask();
        },
        onEscape: function () {
          console.log('onEscape================= ');
        }
      });
      _self.$openedDialog = $dialog;
      return $dialog;
    },

    getReplybody: function (senderName, subject, body, sendtime) {
      var replybody =
        '<br/>--------------' + sendtime + '  ' + senderName + ' 在源消息中写道------<br/>主题:' + subject + '<br/>内容:<br/>' + body;
      return replybody;
    },

    //生成转发对话框的html
    getForwardDialogHtml: function (row) {
      var newSubject = 'Fw:' + (row['SUBJECT'] || row['subject']);
      var senderName = row['SENDER_NAME'] || row['senderName'];
      var subject = row['SUBJECT'] || row['subject'];
      var body = row['BODY'] || row['body'];
      var sentTime = row['SENT_TIME'] || row['sendTime'] || row['sentTime'];
      var newBody = this.getReplybody(senderName, subject, body, sentTime);
      var relatedUrl = row['RELATED_URL'] || row['relatedUrl'];
      var relatedTitle = row['RELATED_TITLE'] || row['relatedTitle'];
      var dialogHtml = this.getDialogHtml('', '', newSubject, newBody, '', relatedUrl, relatedTitle);
      return dialogHtml;
    },

    //弹出发送消息的对话框, 如果outboxUuid != null ,代表是回复和转发
    showSendMsgDialog: function (dialogHtml, dialogTitle, outboxUuid, shownCallback) {
      var _self = this;
      _self.$openedDialog && _self.$openedDialog.remove();
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
          cancel: {
            label: '取消',
            className: 'btn-default'
          }
        },
        shown: function () {
          popupUser($dialog);
          initCKEditor(false);
          $.fn.modal.Constructor.prototype.enforceFocus = function () {};
          if (outboxUuid == null) {
            initFileupload();
          } else {
            initFileuploadByViewMsg(outboxUuid, false);
          }
          if ($.isFunction(shownCallback)) {
            shownCallback();
          }

          _self.getMask();
        },
        callback: function () {
          _self.cancelMask();
        }
      });

      _self.$openedDialog = $dialog;
      return $dialog;
    },

    adjustMsgEventsJSON: function (eventsObj) {
      var _this = this;
      if (typeof eventsObj === 'object' && eventsObj.adjusted) {
        return eventsObj;
      }

      var newEventsObj = {
        /** 用于识别对象是否调整为新的数据格式 */
        adjusted: true,

        /** 回调事件数组 */
        events: []
      };

      var oldEventsObj = eventsObj;

      // 预置转发/删除事件
      var forwardEvent = $.extend({}, presetEventsInfo.forward);
      var deleteEvent = $.extend({}, presetEventsInfo.delete);
      newEventsObj.events.push(forwardEvent, deleteEvent);

      // 原配置事件
      $.each(oldEventsObj, function (idx, event) {
        event.type = 'customize';
        event.displayLocation = ['message-modal', 'message-list', 'message-detail'];
        newEventsObj.events.push(event);
      });

      return newEventsObj;
    },

    getBtn: function (callbackJson, position) {
      var _this = this;
      var obj = {};
      var html = '';

      var events = _this.adjustMsgEventsJSON(callbackJson).events;

      $.each(events, function (bIndex, bItem) {
        if (position && $.inArray(position, bItem.displayLocation) < 0) {
          return;
        }

        var className = bItem.btnLib && bItem.btnLib.btnColor ? bItem.btnLib.btnColor : '';

        if (bItem.group) {
          if (!obj[bItem.group]) {
            obj[bItem.group] = new Array();
          }
          obj[bItem.group].push(
            "<li class='" +
              bItem.code +
              "'><span class='well-btn-callback well-btn' data-target='" +
              JSON.stringify(bItem.target) +
              "' data-event='" +
              JSON.stringify(bItem.eventManger) +
              "'>" +
              bItem.text +
              '</span></li>'
          );
        } else {
          className += ' ';
          className += bItem.btnLib && bItem.btnLib.btnInfo && bItem.btnLib.btnInfo.class ? bItem.btnLib.btnInfo.class : '';

          html +=
            "<button type='button' class='well-btn-callback well-btn " +
            bItem.code +
            ' ' +
            className +
            "'  data-target='" +
            JSON.stringify(bItem.target) +
            "' data-event='" +
            JSON.stringify(bItem.eventManger) +
            "'>";
          if (bItem.btnLib && bItem.btnLib.iconInfo) {
            html += "<i class='" + bItem.btnLib.iconInfo.fileIDs + "'></i>";
          }
          html += bItem.text + '</button>';
        }
      });
      if (Object.keys(obj).length > 0) {
        for (var k in obj) {
          var html2 = obj[k].join('');
          html +=
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

      return html;
    },

    getMask: function () {
      if (this.widget) {
        var $element = this.widget.element;
        $element.parents('body').css({
          background: 'transparent'
        });
        // $element.parents('body').find('.web-app-container').css({
        //   paddingLeft: '60px'
        // });
        top.$('.bootbox-close-wrap').css({
          zIndex: 0
        });
        parent.$('.wellpt-msg-wrapper').children('.panel-body').children('.tab-content').css({
          position: 'absolute',
          top: 0,
          left: 0,
          width: '100%',
          zIndex: 2
        });
        $element.parents('body').find('.msg-receive-content').css({
          background: 'transparent'
        });
        setTimeout(function () {
          $element.parents('body').find('.web-app-container').css({
            background: 'transparent'
          });
        }, 160);
      }
    },

    cancelMask: function () {
      if (this.widget) {
        var $element = this.widget.element;
        $element.parents('body').css({
          background: 'transparent'
        });
        $element.parents('body').find('.web-app-container').css({
          background: 'transparent',
          paddingLeft: 0
        });
        top.$('.bootbox-close-wrap').css({
          zIndex: 100
        });
        parent.$('.wellpt-msg-wrapper').children('.panel-body').children('.tab-content').css({
          position: 'initial',
          top: 'initial',
          left: 'initial',
          width: 'initial'
        });
        $('.msg-receive-content').parent().css({
          background: 'transparent'
        });
      }
    }
  });

  return OutboxMsgModule;
});
