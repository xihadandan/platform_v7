define(['jquery', 'commons', 'constant', 'server', 'appModal', 'layer', 'ckeditor', 'multiOrg', 'wellfileupload'], function (
  $,
  commons,
  constant,
  server,
  appModal,
  layer,
  ckeditor,
  multiOrg,
  wellfileupload
) {
  $('head').append(
    $('<link>', {
      href: '/static/pt/dyform/css/explain/dyform.css',
      rel: 'stylesheet'
    }),
    $('<link>', { href: '/static/js/fileupload/fileupload.css', rel: 'stylesheet' })
  );

  function collectionMsgAndSend($dialog) {
    var sendResult = true;
    appModal.showMask('发送中...');
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
      appModal.hideMask();
      appModal.alert('收件人不能为空！');
      return false;
    } else if (body == null || body == '') {
      appModal.alert('消息内容不能为空！');
      appModal.hideMask();
      return false;
    } else if (subject == null || subject == '') {
      appModal.alert('主题不能为空！');
      appModal.hideMask();
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
          appModal.hideMask();
        },
        error: function () {
          appModal.hideMask();
          appModal.alert('发送失败！');
          sendResult = false;
        }
      });
    }
    return sendResult;
  }

  //消息弹窗html
  function messageHtml(options) {
    var $containDiv = $('<div>', { class: 'container-fluid', style: '' });
    var $divRow = $('<div>', { class: 'row' });
    var $divBox = $('<div>', { class: 'widget-box widget-color-blue' });
    var $divBody = $('<div>', { class: 'widget-body', style: 'padding:0px;' });
    var $divMain = $('<div>', { class: 'widget-main ace-scroll scroll-disabled' });
    var $form = $('<form>', { class: 'dyform' });
    var $table = $('<table>');
    var $receiverTr = $('<tr>', { class: 'field' }).append(
      $('<td>', { class: 'Label', style: 'width:10%;text-align:center;' }).append('收件人'),
      $('<td>').append(
        $('<input>', {
          type: 'hidden',
          id: 'userId',
          name: 'userId',
          value: options && options.toUserIds ? options.toUserIds : ''
        }),
        $('<input>', {
          type: 'text',
          id: 'showUser',
          name: 'showUser',
          class: 'full-width',
          readonly: 'readonly',
          value: options && options.toUserNames ? options.toUserNames : ''
        })
      )
    );
    $table.append($receiverTr);

    var $subjectTr = $('<tr>', { class: 'field' }).append(
      $('<td>', { class: 'Label', style: 'width:10%;text-align:center;' }).append('主题'),
      $('<td>').append(
        $('<input>', {
          type: 'text',
          id: 'subject',
          name: 'subject',
          class: 'full-width',
          style: 'width:91%',
          maxlength: '40'
        }),
        $('<input>', {
          type: 'checkbox',
          id: 'markflag',
          markflag: 'subject',
          value: '1',
          style: 'margin-left:5px;'
        }),
        $('<label>', { for: 'markflag' }).text('重要')
      )
    );
    $table.append($subjectTr);

    var $contentTr = $('<tr>', { class: 'field' }).append(
      $('<td>', { class: 'Label', style: 'width:10%;text-align:center;' }).append('内容'),
      $('<td>').append(
        $('<textarea>', {
          id: 'mesg_body',
          name: 'mesg_body',
          class: 'ckeditor',
          style: 'width:650px; height:215px;'
        })
      )
    );
    $table.append($contentTr);

    var $attachTr = $('<tr>', { class: 'field' }).append(
      $('<td>', { class: 'Label', style: 'width:10%;text-align:center;' }).append('附件'),
      $('<td>').append($('<div>', { id: 'message_fileupload' }))
    );
    $table.append($attachTr);

    $form.append($table);
    $divMain.append($form);
    $divBody.append($divMain);
    $divBox.append($divBody);
    $divRow.append($divBox);
    $containDiv.append($divRow);
    return $containDiv[0].outerHTML;
  }

  //初始化富文本框
  function initCKEditor(isReadOnly) {
    //ckeditor配置路径
    var customCkeditorPath = ctx + '/resources/pt/js/dyform/explain/ckeditor'; // 自定义ckeditor相关配置的路径
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

  //初始化附件上传组件
  function initFileUpload() {
    var fileupload = new WellFileUpload('message_fileupload');
    fileupload.init(false, $('#message_fileupload'), false, true, []);
  }

  //初始化组织人员选择弹出框组件
  function initOrgUserSelect($dialog) {
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

  var QuickSendMsgDialog = {
    /**
     * options:
     *   {'toUserNames':'用户名1;用户名2','toUserIds':'U0000000011;U0000000022','successCallback':function(){}}
     */
    open: function (options) {
      var defaultConfig = {
        title: '新建消息', //标题
        maxmin: true, //开启最大化最小化按钮
        zIndex: 10000,
        message: messageHtml(options),
        size: 'large',
        shown: function () {
          initOrgUserSelect($('.modal'));
          initFileUpload();
          initCKEditor();
          $.fn.modal.Constructor.prototype.enforceFocus = function () {};
        },
        buttons: {
          confirm: {
            label: '发送',
            className: 'well-btn w-btn-primary',
            callback: function () {
              if (collectionMsgAndSend($('.modal'))) {
                if (options && $.isFunction(options.successCallback)) {
                  options.successCallback(
                    (function () {
                      return {
                        to: {
                          id: [].concat($('#userId').val().split(';')),
                          text: [].concat($('#showUser').val().split(';'))
                        }
                      };
                    })()
                  );
                }
              }
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default'
          }
        }
      };
      if (options) {
        defaultConfig = $.extend(defaultConfig, options.config);
      }
      appModal.dialog(defaultConfig);
    }
  };

  return QuickSendMsgDialog;
});
