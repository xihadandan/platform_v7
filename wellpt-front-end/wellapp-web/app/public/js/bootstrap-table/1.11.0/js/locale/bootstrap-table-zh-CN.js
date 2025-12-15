/**
 * Bootstrap Table Chinese translation
 * Author: Zhixin Wen<wenzhixin2010@gmail.com>
 */
(function ($) {
  'use strict';

  $.fn.bootstrapTable.locales['zh-CN'] = {
    formatLoadingMessage: function () {
      return '数据加载中，请稍候……';
    },
    formatRecordsPerPage: function (pageNumber) {
      return /*'每页显示 ' + */ pageNumber /*+ ' 条记录'*/;
    },
    formatShowingRows: function (pageFrom, pageTo, totalRows, totalPages) {
      return '共' + totalPages + '页/' + totalRows + '条记录';
      //return /*'显示第 ' + pageFrom + ' 到第 ' + pageTo + ' 条记录，*/'总共 ' + totalRows + ' 条记录';
    },
    formatSearch: function () {
      return '搜索';
    },
    formatNoMatches: function () {
      return '没有找到匹配的记录';
    },
    formatPaginationSwitch: function () {
      return '隐藏/显示分页';
    },
    formatRefresh: function () {
      return '刷新';
    },
    formatToggle: function () {
      return '切换';
    },
    formatColumns: function () {
      return '列';
    },
    formatExport: function () {
      return '导出数据';
    },
    formatClearFilters: function () {
      return '清空过滤';
    },
    formatePageSize: function (pageSize) {
      return pageSize + '条/页';
    }
  };

  $.extend($.fn.bootstrapTable.defaults, $.fn.bootstrapTable.locales['zh-CN']);
})(jQuery);
