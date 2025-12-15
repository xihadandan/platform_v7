define(['jquery', 'commons', 'constant', 'server', 'appModal', 'layer'], function ($, commons, constant, server, appModal, layer) {
  var quickSendMailDialog = {};

  /**
   * options:
   *        {'toUserNames':'用户名1;用户名2','toUserIds':'U0000000011;U0000000022','successCallback':function(){}}
   */
  quickSendMailDialog.open = function (options) {
    layer.ready(function () {
      var uid = commons.UUID.createUUID();

      var defaultConfig = {
        type: 2, //iframe弹窗类型
        title: ['写邮件'], //标题
        shade: 0.5,
        maxmin: true, //开启最大化最小化按钮
        zIndex: 1,
        area: ['1093px', '800px'],
        content: ctx + '/web/app/pt-app/pt-webmail/pt-webmail-write.html?uid=' + uid,
        cancel: function (index, layero) {
          close(index);
        },
        success: function (layero, index) {
          if (!window.mailEvent) {
            window.mailEvent = {};
          }
          window.mailEvent[uid] = {
            ready: function ($body) {
              // var body = layer.getChildFrame('body', index);
              if (options) {
                if (options.toUserNames) {
                  var names = options.toUserNames.split(';');
                  var ids = options.toUserIds.split(';');
                  var $input = $body.find('#toUserNameTagInput');
                  $input.data('validateInput', false);
                  for (var n = 0, nlen = names.length; n < nlen; n++) {
                    $input.tagEditor('addTag', names[n], true, false, { value: ids[n] });
                  }
                  $input.data('validateInput', true);
                }
              }
            },

            beforeSend: function ($body, mailData) {
              if (options && $.isFunction(options.beforeSendCallback)) {
                return options.beforeSendCallback(mailData);
              }
              return true;
            },
            afterSend: function ($body, mailData) {
              if (options && $.isFunction(options.successCallback)) {
                options.successCallback(
                  //返回收件人数组
                  (function () {
                    var receiver = { to: [], bcc: [], cc: [] };
                    var inputFieldMapper = { toUserNameTagInput: 'to', ccUserNameTagInput: 'cc', bccUserNameTagInput: 'bcc' };
                    //收件人
                    $('#toUserNameTagInput,#ccUserNameTagInput,#bccUserNameTagInput', $body).each(function () {
                      var text = [];
                      var value = [];
                      var id = $(this).attr('id');
                      var datas = $('#' + id, $body).tagEditor('getTagDatas');
                      var tags = $('#' + id, $body).tagEditor('getTags')[0].tags;
                      if (datas.length) {
                        for (var i = 0, len = datas.length; i < len; i++) {
                          text.push(tags[i]);
                          value.push(datas[i] ? datas[i].value : tags[i]);
                        }
                      }
                      receiver[inputFieldMapper[id]] = {
                        id: value,
                        text: text
                      };
                    });
                    return receiver;
                  })()
                );
              }

              close(index);
            }
          };
        },
        btn: ['发送', '存草稿'],
        yes: function (index, layero) {
          var body = layer.getChildFrame('body', index);
          $('.btn_sent', $(body)).trigger('click');
          return false;
        },
        btn2: function (index, layero) {
          var body = layer.getChildFrame('body', index);
          $('.btn_save', $(body)).trigger('click');
          return false;
        }
      };

      if (options) {
        defaultConfig = $.extend(defaultConfig, options.config);
      }
      layer.open(defaultConfig);

      function close(index) {
        try {
          layer.close(index);
        } catch (e) {
          //解决部分浏览器关闭会滚动的现象，不去使用layer的自带的关闭
          $('#layui-layer' + index).remove();
          $('#layui-layer-shade' + index).remove();
        }
      }
    });
  };

  return quickSendMailDialog;
});
