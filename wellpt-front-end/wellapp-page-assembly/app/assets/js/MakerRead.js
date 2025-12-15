(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'ListViewWidgetDevelopment'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, ListViewWidgetDevelopment) {
  var MarkerRead = {
    defaultEvent: function () {
      debugger;
      var self = this;
      var data = this.getSelections();
      var getConfiguration = this.getConfiguration();
      if (data.length > 0) {
        var tagDataKeys = [];
        $.each(data, function (index, item) {
          tagDataKeys.push(item[getConfiguration['tagDataKey']]);
        });
        $.ajax({
          type: 'post',
          url: ctx + '/api/readMarker/add',
          dataType: 'json',
          data: { tagDataKeys: tagDataKeys.join(',') },
          success: function (result) {
            if (result.msg == '成功') {
              appModal.success('批量标记已读成功');
              self.refresh();
            }
          }
        });
      } else {
        appModal.error('请选择记录！');
      }
    }
  };
  window.MarkerRead = MarkerRead;
  return MarkerRead;
});
