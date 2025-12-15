define(['jquery', 'commons', 'constant', 'server', 'appContext', 'appModal'], function (
  $,
  commons,
  constant,
  server,
  appContext,
  appModal
) {
  var StringUtils = commons.StringUtils;

  function createFeedbackDialog(docExcRecordUuid, title) {
    var _self = this;
    var fileupload = new WellFileUpload('feedbackFile');
    var content = createFeedbackHtml();

    var $dialog = popDialog(
      content,
      title,
      {
        confirm: {
          label: '确定',
          className: 'btn-primary',
          callback: function () {
            if (!$('#feedbackContent')) {
              appModal.info('请输入反馈内容');
              return false;
            }

            var feedbackData = {
              content: $('#feedbackContent').val(),
              fileUuids: '',
              fileNames: ''
            };

            for (var i = 0, len = fileupload.files.length; i < len; i++) {
              feedbackData.fileUuids += fileupload.files[i].fileID;
              feedbackData.fileNames += fileupload.files[i].fileName;
              if (i != len - 1) {
                feedbackData.fileUuids += '/';
                feedbackData.fileNames += '/';
              }
            }

            server.JDS.call({
              service: 'dmsDocExchangeRecordService.feedbackDocExchange',
              data: [docExcRecordUuid, feedbackData],
              success: function (res) {
                appModal.success('反馈成功！');
              },
              error: function (jqXHR) {},
              async: false
            });
          }
        },
        cancel: {
          label: '取消',
          className: 'btn-default',
          callback: function () {}
        }
      },
      function () {
        fileupload.init(false, $('#fileUploadDiv'), false, true, []);
      }
    );
  }

  //生成反馈的HTML
  function createFeedbackHtml() {
    var $form = $('<form>', { id: 'feedbackContainer', class: 'dyform' });
    var $table = $('<table>', { id: 'feedbackTable', class: 'table' });

    $table.append(
      $('<tr>').append(
        $('<td>', { class: 'label-td' }).text('反馈内容'),
        $('<td>').append(
          $('<textarea>', {
            id: 'feedbackContent',
            style: 'resize:vertical;height:100px;width:100%;',
            maxlength: 150,
            class: 'editableClass',
            placeholder: '请输入反馈内容'
          })
        )
      ),
      $('<tr>').append(
        $('<td>', { class: 'label-td' }).text('附件'),
        $('<td>').append($('<div>', { id: 'fileUploadDiv' }), $('<input>', { id: 'fileUuid', type: 'hidden' }))
      )
    );

    return $form.append($table)[0].outerHTML;
  }

  /**
   * 弹窗
   * @param html  弹窗内容html
   * @param title  标题
   * @param buttons 按钮配置
   * @param shownCallback 弹窗展示后的回调函数
   */
  function popDialog(html, title, buttons, shownCallback) {
    return appModal.dialog({
      title: title,
      message: html,
      size: 'large',
      buttons: buttons,
      shown: function () {
        if ($.isFunction(shownCallback)) {
          shownCallback();
        }
      }
    });
  }

  return function showDialog(options) {
    var docExcRecordUuid = options.docExcRecordUuid;
    var title = options.title || '反馈意见';
    createFeedbackDialog(docExcRecordUuid, title);
  };
});
