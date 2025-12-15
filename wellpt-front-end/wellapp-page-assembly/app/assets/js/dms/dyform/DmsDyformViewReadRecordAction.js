define('DmsDyformViewReadRecordAction', [
  'jquery',
  'commons',
  'constant',
  'server',
  'appContext',
  'appModal',
  'DmsDyformActionBase'
], function ($, commons, constant, server, appContext, appModal, DmsDyformActionBase) {
  var StringBuilder = commons.StringBuilder;
  // 表单单据查看阅读记录操作
  var DmsDyformViewReadRecordAction = function () {
    DmsDyformActionBase.apply(this, arguments);
  };
  var tpl = '<div class="record-box"><div class="record-header">已阅人员（{0}人）</div><ul>{1}</ul></div>';
  tpl += '<div class="record-box"><div class="record-header">未阅人员（{2}人）</div><ul>{3}</ul></div>';
  // 显示已阅未阅弹出框
  var showReadRecordDialog = function (callback) {
    var options = {
      title: '阅读记录',
      message: "<div class='view-read-record-content'></div>",
      width: 840,
      height: 600,
      show: callback,
      buttons: [
        {
          className: 'well-btn w-btn-light w-line-btn',
          label: '取消',
          callback: $.noop
        }
      ]
    };
    appModal.dialog(options);
  };
  var fillReadRecords = function (readRecords) {
    var readString = getReadString(readRecords.records);
    var unreadUserName = readRecords.unreadUserName || '';
    var unreadUsers = unreadUserName.split(';');
    var unreadRecords = _.map(unreadUsers, function (item) {
      return {
        userName: item
      };
    });
    var unreadString = getReadString(unreadRecords);
    var unreadLength = unreadUserName ? unreadUsers.length : 0;
    var sb = new StringBuilder();
    sb.appendFormat(tpl, readRecords.records.length, readString, unreadLength, unreadString);
    $('.view-read-record-content')[0].innerHTML = sb.toString();
  };
  // 获取已阅人员字符串
  var getReadString = function (records) {
    if (records == null) {
      return '';
    }
    var sb = new StringBuilder();
    $.each(records, function (i, record) {
      sb.append('<li>');
      sb.append("<div class='record-item'>");
      sb.append("<div class='gray-11'>");
      sb.append(record.userName);
      sb.append('</div>');
      sb.append("<div class='text-right gray-10'>");
      sb.append(getReadTimeString(record.readTime));
      sb.append('</div>');
      sb.append('</div>');
      sb.append('</li>');
    });
    return sb.toString();
  };
  var getReadTimeString = function (readTime) {
    if (!readTime) {
      return '';
    }
    var timestring = new Date(readTime);
    var now = new Date();
    if (now.getFullYear() != timestring.getFullYear()) {
      readTime = timestring.format('yyyy-MM-dd HH:mm'); //往年查阅的，显示年月日时分
    } else {
      readTime = timestring.format('MM-dd HH:mm'); //当年查阅的，仅显示月日时分
      if (now.format('MM-dd') == timestring.format('MM-dd')) {
        readTime = timestring.format('HH:mm'); //当天查阅的，仅显示时分
      }
    }
    return readTime;
  };
  commons.inherit(DmsDyformViewReadRecordAction, DmsDyformActionBase, {
    btn_dyform_view_read_record: function (options) {
      var _self = this;
      showReadRecordDialog(function () {
        options.success = function (result) {
          fillReadRecords(result.data);

          $('.view-read-record-content').parent().css({
            'max-height': '500px'
          });
        };
        // 重新执行操作的服务端处理
        _self.dmsDataServices.performed(options);
      });
    }
  });
  return DmsDyformViewReadRecordAction;
});
