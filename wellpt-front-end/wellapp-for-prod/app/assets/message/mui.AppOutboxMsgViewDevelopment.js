define([
  'jquery',
  'commons',
  'constant',
  'server',
  'appContext',
  'multiOrg',
  'MobileListDevelopmentBase',
  'appModal',
  'formBuilder',
  'mui-fileupload',
  'mui-filepreview'
], function ($, commons, constant, server, appContext, unit, ListViewWidgetDevelopment, appModal, formBuilder, muiFileupload, FileViewer) {
  window.JDS = server.JDS;
  var OutboxMsgModule = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  var fileUpload;
  var FileUpLoad = $.MuiFileUpload.extend({
    init: function (holder, options) {
      var self = this;
      self._super.apply(this, arguments);
      self.on('afterSetValue', function () {
        var fileNum = self.files ? self.files.length : 0;
        var mailWrapper = $(self.holder).closest('.msg-wrapper');
        if (fileNum !== 0) {
          $('.file-num', mailWrapper)[0].innerHTML = fileNum;
        } else {
          $('.file-num', mailWrapper)[0].innerHTML = '';
        }
        // 触发输入
        $.trigger($('#subject', mailWrapper)[0], 'input');
      });
    },
    renderItems: function (files, append) {
      var self = this;
      var mailWrapper = $(self.holder).closest('.msg-wrapper');
      if (!append) {
        $('.file-list', mailWrapper)[0].innerHTML = '';
      }
      var fileListWrapper = $('.file-list', mailWrapper)[0];
      files.forEach(function (n) {
        var fileType = n.fileName.substring(n.fileName.indexOf('.') + 1);
        var iconSpan;
        switch (fileType) {
          case 'doc':
          case 'docx':
            iconSpan = 'file-word-o';
            break;
          case 'xls':
          case 'xlsx':
            iconSpan = 'file-excel-o';
            break;
          case 'ppt':
          case 'pptx':
            iconSpan = 'file-powerpoint-o';
            break;
          case 'rar':
          case 'zip':
            iconSpan = 'file-archive-o';
            break;
          case 'jpg':
          case 'png':
          case 'git':
            iconSpan = 'file-image-o';
            break;
          default:
            iconSpan = 'file-text-o';
        }
        var li = document.createElement('li');
        li.setAttribute('data-fileID', n.fileID);
        li.className = 'mui-table-view-cell';
        var span = document.createElement('span');
        span.className = 'fa fa-' + iconSpan;
        var fileName = document.createElement('span');
        fileName.className = 'mui-ellipsis';
        fileName.innerHTML = n.fileName;
        li.appendChild(span);
        li.appendChild(fileName);
        fileListWrapper.appendChild(li);
      });
      //初始化弹框
      var popoverBox = $('#popup', mailWrapper)[0];
      if (!popoverBox) {
        var popup = document.createElement('div');
        popup.id = 'popup';
        mailWrapper.appendChild(popup);
        formBuilder.buildActionSheet({
          container: popup,
          data: [
            { id: 'delete', name: 'delete', text: '删除' },
            { id: 'preview', name: 'preview', text: '预览' }
          ]
        });
      }
      //点击事件
      if ($('.file-list', mailWrapper)[0].innerHTML !== '') {
        var fileId;
        $('.file-list', mailWrapper).on('tap', 'li', function () {
          var _this = this;
          $('#popup #action_sheet_', mailWrapper).popover('show');
          fileId = $(_this)[0].getAttribute('data-fileid');
        });
        $('#popup #action_sheet_', mailWrapper)
          .off('tap', 'li')
          .on('tap', 'li', function () {
            if (this.id == 'delete') {
              self.deleteItem(fileId);
              // self.renderAfterDel();
              $('#popup #action_sheet_', mailWrapper).popover('hide');
            } else if (this.id == 'preview') {
              self.previewItem(fileId);
              $('#popup #action_sheet_', mailWrapper).popover('hide');
            }
          });
      }
    },
    renderAfterDel: function () {
      var self = this;
      var mailWrapper = $(self.holder).closest('.msg-wrapper');
      $('.file-list', mailWrapper)[0].innerHTML = '';
      self.renderItems(self.files);
    },
    deleteItem: function (fileId) {
      var self = this;
      var delItem = self.getFile(fileId);
      // console.log(self.files);
      self.deleteFileItem(delItem);
      self.setItems(self.files, false); // 刷新界面
    },
    previewItem: function (fileId) {
      var self = this;
      var previewItem = self.getFile(fileId);
      var previewObj = self.getPreviewObj(previewItem);
      FileViewer.preview(previewObj);
    }
  });
  //发送消息
  function sendMessage($dialog) {
    var sendResult = true;
    //$(form_selector).form2json(bean);
    var userId = $('#msg_editbox #userId')[0].value;
    var showUser = $('#msg_editbox #showUser')[0].value;
    var subject = $('#msg_editbox #subject')[0].value;
    var body = $('#msg_editbox #mesg_body')[0].innerHTML;
    var markflag = $('#msg_editbox .mui-switch')[0].classList.contains('mui-active') ? '1' : '0';
    var relatedUrl = null;
    var relatedTitle = null;
    if ($('#msg_editbox #relatedUrl')[0] != undefined) {
      relatedUrl = $('#msg_editbox #relatedUrl')[0].value;
    }
    if ($('#msg_editbox #relatedTitle')[0] != undefined) {
      relatedTitle = $('#msg_editbox #relatedTitle')[0].value;
    }

    var messageAttachId = [];
    var messageAttach = fileUpload.getItems();
    //        var messageAttach = WellFileUpload.files["message_fileupload"];
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
  commons.inherit(OutboxMsgModule, ListViewWidgetDevelopment, {
    //通过trigger来刷新对应的徽章数量
    refreshBadgeNum: function () {
      //            this.getPageContainer().trigger("AppMsg.Change");
    },
    onClickRow: function (rowNum, row, $element, field) {
      var _self = this;
      var title = '发件消息   ' + row['SENT_TIME'].substr(0, 19);
      var outboxUuid = row['UUID'];
      var buttons = [
        {
          id: 'forwardMsg',
          name: '转发',
          callback: function () {
            _self.showForwardDialog(row);
          }
        },
        {
          id: 'retractMsg',
          name: '撤回',
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
      ];

      var $dialog = this.viewMsg(row, buttons, title, outboxUuid);
    },
    //新建消息按钮对应的事件
    btnNewMsg: function (args) {
      var dialogHtml = this.getDialogHtml('', '', '', '', '', '', '');
      var $newDialog = this.SendOutPanel(dialogHtml, '新建消息', null, null);
    },
    //如果outboxUuid != null ,代表是回复和转发
    SendOutPanel: function (pageHtml, title, outboxUuid, shownCallback) {
      var containerSelector = '#msg_editbox';
      var $formPanel = formBuilder.showPanel({
        title: title,

        optButton: {
          id: 'sendOutBtn',

          label: '发送',
          callback: function () {
            return sendMessage();
          }
        },
        moreNum: 5,
        content: pageHtml,
        container: containerSelector
      });
      if ($.isFunction(shownCallback)) {
        shownCallback();
      }
      fileUpload = new FileUpLoad($('#msg_editbox #upload-input')[0], {
        autoUpload: true,
        multiple: true,
        lableText: '',
        initXHRData: {
          folderID: outboxUuid,
          purpose: 'messageAttach'
        }
      });
      this.showReceiver(containerSelector);
      $('#msg_editbox .mui-switch')['switch']();
      $('#msg_editbox #mesg_body')[0].style.background = '#fff';
      $('#msg_editbox .body-editor')[0].setAttribute('contenteditable', true);
    },

    //转发按钮对应的事件
    btnForwardMsg: function (args) {
      var selections = this.getSelections();
      var uuids = this.getSelectedRowIds(selections);
      if (uuids.length == 0) {
        appModal.alert('请选择记录！');
      } else if (uuids.length != 1) {
        appModal.alert('一次只能选择一条记录！');
      } else {
        this.showForwardDialog(selections[0]);
      }
    },
    showReceiver: function (containerSelector) {
      $(containerSelector + ' #showUser')[0].addEventListener('tap', function (event) {
        var self = this;
        unit.open({
          type: 'MyUnit;DutyGroup;MyGroup;PublicGroup',
          selectType: '1',
          labelField: containerSelector + ' #showUser',
          valueField: containerSelector + ' #userId',
          title: '添加收件人',
          afterSelect: function () {
            $.trigger(self, 'input');
          }
        });
      });
    },
    //弹出转发对话框
    showForwardDialog: function (row) {
      var dialogHtml = this.getForwardDialogHtml(row);
      var outboxUuid = row['UUID'];
      var $forwardDialog = this.SendOutPanel(dialogHtml, '转发消息', outboxUuid);
    },
    //删除按钮对应的事件
    btnDelMsg: function (args) {
      var uuids = this.getSelectedRowIds(this.getSelections());
      if (uuids.length == 0) {
        appModal.alert('请选择记录！');
      } else {
        var _self = this;
        console.log(uuids);
        appModal.confirm('确认要删除吗?', function (result) {
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
        });
      }
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
          "<td class='Label' width='100' align='center'>相关链接</td>" +
          "<td class='value'>" +
          "<div class='td_class'>" +
          "<a target='_blank' style='color:blue' href='" +
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
        "<div id='dialog_form_content' class='msg-wrapper' style='margin:15px;'>" +
        "<label style='display:block'>收件人</label>" +
        "<input type='hidden' id='userId' name='userId' value='" +
        userid +
        "'/>" +
        "<textarea class='ckeditor'  id='showUser' name='showUser'/>" +
        userName +
        '</textarea>' +
        "<label style='display:block'  >主题</label>" +
        "<input type='text' id='subject' name='subject'  value='" +
        subject +
        "'/>" +
        "<div style='text-align:right'>" +
        //                + "<input id='markflag' name='markflag' type='checkbox' value='1' " + checked + "/>"
        //                + "<label>重要</label>"
        "<div class='mui-switch mui-switch-mini " +
        (checked == 0 ? '' : 'mui-active') +
        "' id='markflag' style='display:inline-block;vertical-align:middle;margin-right:10px'>" +
        " <div class='mui-switch-handle'></div>" +
        '</div>' +
        '<label>重要</label>' +
        '</div>' +
        "<label style='display:block'>内容</label>" +
        "<div  id='mesg_body' class='ckeditor body-editor'  style=' height:215px; padding:10px;overflow-y:auto; border:1px solid #ccc' name='mesg_body'>" +
        body +
        '</div>' +
        "<tr><td style='padding-left: 20px'>" +
        "<button class='fileUpload-btn'>" +
        "<span class='mui-icon mui-icon-paperclip'></span>" +
        "<span class='file-num'></span>" +
        "<input type='file' id='upload-input'>" +
        '</button>' +
        " <div class='file-list-wrapper'>" +
        '<p>附件</p>' +
        "<ul class='file-list mui-table-view'>	" +
        '</ul>' +
        ' </div>' +
        '</div>';
      // content += '<link href="/resources/fileupload/fileupload.css?v=5.3.1.070747" rel="stylesheet">';
      return content;
    },

    viewMsg: function (row, buttons, title, outboxUuId, shownCallback) {
      var userId = row['RECIPIENT'];
      var userName = row['RECIPIENT_NAME'];
      var subject = row['SUBJECT'];
      var body = row['BODY'];
      //            body = body.replace(/<br\/>/g,"\r");
      //            var regex = /(<([^>]+)>)/ig;
      //            body = body.replace(regex, "");
      var checked = row['MARK_FLAG'] == '1' ? 1 : 0;
      var relatedUrl = row['RELATED_URL'];
      var relatedTitle = row['RELATED_TITLE'];
      var dialogHtml = this.getDialogHtml(userId, userName, subject, body, checked, relatedUrl, relatedTitle);
      var containerSelector = '#msg_viewbox';
      var $formPanel = formBuilder.showPanel({
        title: title,
        actionBack: {
          showNavTitle: true
        },
        moreNum: 5,
        actions: buttons,
        content: dialogHtml,
        container: containerSelector
      });
      $('#msg_viewbox .mui-switch')['switch']();
      $('#msg_viewbox #showUser')[0].setAttribute('disabled', 'disabled');
      $('#msg_viewbox #subject')[0].setAttribute('disabled', 'disabled');
      $('#msg_viewbox #mesg_body')[0].style.background = '#eee';
      $('#msg_viewbox #markflag')[0].classList.add('mui-disabled');
      if ($.isFunction(shownCallback)) {
        shownCallback();
      }
      new FileUpLoad($('#msg_viewbox #upload-input')[0], {
        autoUpload: true,
        multiple: true,
        lableText: '',
        initXHRData: {
          folderID: outboxUuId,
          purpose: 'messageAttach'
        }
      });
      $('#msg_viewbox .fileUpload-btn')[0].style.display = 'none';
      this.showReceiver(containerSelector);
      return $formPanel;
    },

    getReplybody: function (senderName, subject, body, sendtime) {
      var replybody = '<p>-------' + sendtime + '  ' + senderName + ' 在源消息中写道------<br/>主题:' + subject + '<br/>内容:</p>' + body;
      return replybody;
    },

    //生成转发对话框的html
    getForwardDialogHtml: function (row) {
      var newSubject = 'Fw:' + row['SUBJECT'];
      var body = row['BODY'];
      //            body = body.replace(/<br\/>/g,"\r");
      var newBody = this.getReplybody(row['SENDER_NAME'], row['SUBJECT'], body, row['SENT_TIME']);
      var relatedUrl = row['RELATED_URL'];
      var relatedTitle = row['RELATED_TITLE'];
      var dialogHtml = this.getDialogHtml('', '', newSubject, newBody, '', relatedUrl, relatedTitle);
      return dialogHtml;
    }
  });

  return OutboxMsgModule;
});
