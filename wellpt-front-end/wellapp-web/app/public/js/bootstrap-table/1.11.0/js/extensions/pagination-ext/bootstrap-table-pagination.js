/**
 * 表格分页功能的扩展
 * 1.默认分页
 * 2.瀑布式分页
 * 3.翻页式分页
 */
(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'commons', 'server', 'appModal', 'lodash'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function (jquery, commons, server, appModal, lodash) {
  'use strict';

  // 调用bootstrapTable组件的构造器得到对象
  var BootstrapTable = $.fn.bootstrapTable.Constructor;

  BootstrapTable.prototype.initDefaultPagination = BootstrapTable.prototype.initPagination;

  /**
   * 重写bootstrapTable的分页
   * @param fixedScroll
   */
  BootstrapTable.prototype.initPagination = function () {
    if (this.options.treeView && this.options.allPageDataTreeFormate) {
      if (this.options.pagination && this.$pagination.length > 0) {
        this.$pagination.hide();
      }
      return;
    }
    if (!this.options.pagination) {
      this.$pagination.hide();
      return;
    } else {
      if (this.options.pageStyleOption.pageStyle === 'default' || !this.options.pageStyleOption.pageStyle) {
        //默认的分页类型
        this.options.jumpPage = this.options.pageStyleOption.jumpPage; //允许跳页
        this.initDefaultPagination();
        this.$pagination.find('.page-pre a').html('').addClass('iconfont icon-ptkj-xianmiaojiantou-zuo').css('fontSize', '12px');
        this.$pagination.find('.page-next a').html('').addClass('iconfont icon-ptkj-xianmiaojiantou-you').css('fontSize', '12px');
        return;
      }
      this.$pagination.show();
    }

    if (this.options.pageStyleOption.pageStyle === 'waterfall') {
      this.waterfallPagination();
      return;
    }
    if (this.options.pageStyleOption.pageStyle === 'turn') {
      this.turnPagination();
      return;
    }
  };

  /**
   * 默认带跳页的分页
   */
  BootstrapTable.prototype.initDefaultPaginationWithJumpPage = function () {};

  /**
   * 瀑布式分页
   */
  BootstrapTable.prototype.waterfallPagination = function () {
    var that = this,
      html = [];

    this.calculateTotlaPage();

    var pageMoreText =
      (this.options.pageStyleOption.waterfallPageLabel || $.fn.bootstrapTable.defaults.pageStyleOption.waterfallPageLabel) +
      '<i class="fa fa-long-arrow-down"></i>';

    if (this.options.pageNumber === this.options.totalPages) {
      pageMoreText = '无更多数据';
      html.push('<div class="pull-center pagination-detail">', '<span class="pagination-info">', pageMoreText, '</span>', '</div>');
    } else {
      html.push(
        '<div class="pull-center pagination-detail">',
        '<span class="pagination-info">',
        '<a href="javascript:void(0);" class="page-more">',
        pageMoreText,
        '</a>',
        '</span>',
        '</div>'
      );
    }

    this.$pagination.html(html.join(''));

    var $pageMore = this.$pagination.find('.page-more');
    $pageMore.off('click').on('click', $.proxy(this.waterfallOnPageNext, this));
  };

  BootstrapTable.prototype.calculateTotlaPage = function () {
    if (this.options.totalRows) {
      if (this.options.pageSize === this.options.formatAllRows()) {
        this.options.pageSize = this.options.totalRows;
      } else if (this.options.pageSize === this.options.totalRows) {
        var pageLst =
          typeof this.options.pageList === 'string'
            ? this.options.pageList.replace('[', '').replace(']', '').replace(/ /g, '').toLowerCase().split(',')
            : this.options.pageList;
        if ($.inArray(this.options.formatAllRows().toLowerCase(), pageLst) > -1) {
        }
      }
      this.totalPages = ~~((this.options.totalRows - 1) / this.options.pageSize) + 1;
      this.options.totalPages = this.totalPages;
    }
  };

  /**
   * 翻页式分页
   */
  BootstrapTable.prototype.turnPagination = function () {
    var that = this,
      html = [];

    this.calculateTotlaPage();
    html.push(
      '<div class="pull-center pagination-detail">',
      '<nav>',
      '<ul class="pagination">',
      '<li class="' + (this.options.pageNumber === 1 ? 'disabled' : 'active') + '"><a href="#" class="page-pre">',
      this.options.pageStyleOption.prePageLabel || $.fn.bootstrapTable.defaults.pageStyleOption.prePageLabel,
      '</a></li>',
      '<li class="' + (this.options.pageNumber === this.options.totalPages ? 'disabled' : 'active') + '"><a href="#" class="page-next">',
      this.options.pageStyleOption.nextPageLabel || $.fn.bootstrapTable.defaults.pageStyleOption.nextPageLabel,
      '</a></li>',
      '</ul>',
      '</nav>'
    );

    this.$pagination.html(html.join(''));
    var $pre = this.$pagination.find('.page-pre');
    var $next = this.$pagination.find('.page-next');
    $pre.off('click').on('click', $.proxy(this.turnOnPagePre, this));
    $next.off('click').on('click', $.proxy(this.turnOnPageNext, this));
  };

  BootstrapTable.prototype.turnOnPagePre = function () {
    if (this.options.pageNumber === 1) {
      return;
    }
    if (this.options.pageNumber - 1 === 1) {
      this.$pagination.find('.page-pre').parent().addClass('disabled').removeClass('active');
    } else {
      this.$pagination.find('.page-pre').parent().addClass('active').removeClass('disabled');
    }
    this.options.pageNumber--;
    this.updatePagination(event);
  };

  BootstrapTable.prototype.turnOnPageNext = function () {
    if (this.options.pageNumber === this.options.totalPages) {
      return;
    }
    if (this.options.pageNumber + 1 === this.options.totalPages) {
      this.$pagination.find('.page-pre').parent().addClass('disabled').removeClass('active');
    } else {
      this.$pagination.find('.page-pre').parent().addClass('active').removeClass('disabled');
    }
    this.options.pageNumber++;
    this.updatePagination(event);
  };

  BootstrapTable.prototype.waterfallOnPageNext = function () {
    if (this.options.pageNumber == this.options.totalPages || (event && $(event.currentTarget).hasClass('disabled'))) {
      return;
    }

    var that = this;
    var _responseHandler = this.options.responseHandler;
    this.options.responseHandler = function (res) {
      res.rows = that.data.concat(res.rows);
      that.options.responseHandler = _responseHandler;
      return res;
    };
    this.options.pageNumber++;

    if (!this.options.maintainSelected) {
      this.resetRows();
    }

    this.initPagination();

    var $papgeMore = this.$pagination.find('.page-more');
    this.$tableLoading.css('top', $papgeMore.offset().top - 10);

    this.initServer();

    this.trigger('page-change', this.options.pageNumber, this.options.pageSize);
  };

  $.extend($.fn.bootstrapTable.defaults, {
    pageStyleOption: {
      pageStyle: '', // default / waterfall /turn
      waterfallPageLabel: '加载更多',
      prePageLabel: '上一页',
      nextPageLabel: '下一页'
    }
  });
});
