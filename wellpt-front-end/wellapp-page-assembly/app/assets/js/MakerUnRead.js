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
    define(['jquery'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($) {
  var MarkerUnRead = {
    defaultEvent: function () {
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
          url: ctx + '/api/readMarker/del',
          dataType: 'json',
          data: { tagDataKeys: tagDataKeys.join(',') },
          success: function (result) {
            if (result.msg == '成功') {
              appModal.success('批量标记未读成功');
              self.refresh();
            }
          }
        });
      } else {
        appModal.error('请选择记录！');
      }
    }
  };
  window.MarkerUnRead = MarkerUnRead;
  return MarkerUnRead;
});
