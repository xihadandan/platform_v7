;
(function ($, window, document, undefined) {
  'use strict';
  var defaults = {
    page: 1, //当前页
    pageSize: 10, //每页显示多少条
    total: 0, //数据总条数
    showTotal: false, //是否显示总条数
    totalTxt: "共{total}条记录", //数据总条数文字描述
    totalPage: "共{totalpage}页", //数据总页数文字描述
    noData: false, //没有数据时是否显示分页，默认false不显示，true显示第一页
    showSkip: false, //是否显示跳页
    showPN: true, //是否显示上下翻页
    prevPage: "&lt;", //上翻页按钮文字
    nextPage: "&gt;", //下翻页按钮文字
    fastForward: 0, //快进快退，默认0页
    showPageSizeList: false, //是否显示每页条数选择
    backFun: function (page) {} //点击分页按钮回调函数，返回当前页码
  };

  function Plugin(element, options) {
    this.element = $(element);
    this.settings = $.extend({}, defaults, options);
    this.pageNum = 1, //记录当前页码
      this.pageList = [], //页码集合
      this.pageTatol = 0; //记录总页数
    this.pageSizeList = [10, 20, 50, 100, 200]; //每页条数，默认10
    if (this.settings.pageSize && this.pageSizeList.indexOf(Number(this.settings.pageSize)) == -1) {
      this.pageSizeList.push(this.settings.pageSize)
      var pageSizeList = this.minSort(this.pageSizeList) //从大到小排序
      this.pageSizeList = Array.from(new Set(pageSizeList)) //去重
    }
    this.init();
  }
  $.extend(Plugin.prototype, {
    init: function () {
      this.element.empty();
      this.viewHtml();
    },
    creatHtml: function (i) {
      i == this.settings.page ? this.pageList.push('<button type="button" class="active" data-page=' + i + '>' + i + '</button>') :
        this.pageList.push('<button type="button" data-page=' + i + '>' + i + '</button>');
    },
    viewHtml: function () {
      var settings = this.settings;
      var pageTatol = 0;
      var pageArr = []; //分页元素集合，减少dom重绘次数
      if (settings.total > 0) {
        pageTatol = Math.ceil(settings.total / settings.pageSize);
      } else {
        if (settings.noData) {
          pageTatol = 1;
          settings.page = 1;
          settings.total = 0;
        } else {
          return;
        }
      }
      this.pageTatol = pageTatol;
      this.pageNum = settings.page;
      //页数选择和总条数放左边
      if (settings.showPageSizeList || settings.showTotal) {
        pageArr.push('<span class="page-list pull-left">')
      }
      if (settings.showPageSizeList) { //选择每页条数
        var defalutSize = settings.pageSize
        var pageSizeList = this.pageSizeList
        pageArr.push('<span class="btn-group dropup" style="margin-right: 10px;vertical-align: middle;"><button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false" data-pageSize="10"><span class="page-size">' + defalutSize + '条/页</span><i class="fa fa-angle-up"></i></button><ul class="dropdown-menu" role="menu" style="min-width:80px;">')
        for (var i = 0; i < pageSizeList.length; i++) {
          if (defalutSize == pageSizeList[i]) {
            pageArr.push('<li><a href="javascript:void(0)" class="active" style="text-align:center">' + pageSizeList[i] + '</a></li>')
          } else {
            pageArr.push('<li><a href="javascript:void(0)" style="text-align:center">' + pageSizeList[i] + '</a></li>')
          }
          if (pageSizeList[i] >= settings.total) {
            break;
          }
        }
        pageArr.push('</ul></span></div>')
      }
      if (settings.showTotal) {
        pageArr.push('<div class="spage-total">' + settings.totalPage.replace(/\{(\w+)\}/gi, this.pageTatol) + '/' + settings.totalTxt.replace(/\{(\w+)\}/gi, settings.total) + '</div>');
      }
      if (settings.showPageSizeList || settings.showTotal) {
        pageArr.push('</span>')
      } else {
        pageArr.push('<div class="spage-total">' + settings.pageSize + ' 条/页</div>');
      }
      pageArr.push('<span class="page-list pull-right"><div class="spage-number">');
      this.pageList = []; //页码元素集合，包括上下页
      if (settings.showPN) {
        settings.page == 1 ? this.pageList.push('<button type="button" class="button-disabled" data-page="prev">' + settings.prevPage + '</button>') :
          this.pageList.push('<button type="button" data-page="prev">' + settings.prevPage + '</button>');
      }
      if (pageTatol <= 6) {
        for (var i = 1; i < pageTatol + 1; i++) {
          this.creatHtml(i);
        }
      } else {
        if (settings.page < 5) {
          for (var i = 1; i <= 5; i++) {
            this.creatHtml(i);
          }
          this.pageList.push('<button type="button" data-page="after" class="spage-after">...</button><button type="button" data-page=' + pageTatol + '>' + pageTatol + '</button>');
        } else if (settings.page > pageTatol - 4) {
          this.pageList.push('<button type="button" data-page="1">1</button><button type="button" data-page="before" class="spage-before">...</button>');
          for (var i = pageTatol - 4; i <= pageTatol; i++) {
            this.creatHtml(i);
          }
        } else {
          this.pageList.push('<button type="button" data-page="1">1</button><button type="button" data-page="before" class="spage-before">...</button>');
          for (var i = settings.page - 2; i <= Number(settings.page) + 2; i++) {
            this.creatHtml(i);
          }
          this.pageList.push('<button type="button" data-page="after" class="spage-after">...</button><button type="button" data-page=' + pageTatol + '>' + pageTatol + '</button>');
        }
      }
      if (settings.showPN) {
        settings.page == pageTatol ? this.pageList.push('<button type="button" class="button-disabled" data-page="next">' + settings.nextPage + '</button>') :
          this.pageList.push('<button type="button" data-page="next">' + settings.nextPage + '</button>');
      }
      pageArr.push(this.pageList.join(''));
      pageArr.push('</div>');
      if (settings.showSkip) {
        pageArr.push('<div class="spage-skip">跳至&nbsp;<input type="text" value=""/>&nbsp;页&nbsp;&nbsp;<button type="button" data-page="go">跳转</button></div></span>');
      }
      this.element.html(pageArr.join(''));
      this.clickBtn();
      if (settings.total <= 10) {
        this.element.find(".btn-group").hide();
        this.element.find(".page-list.pull-right").hide();
      }
    },
    clickBtn: function () {
      var that = this;
      var settings = this.settings;
      var ele = this.element;
      var pageTatol = this.pageTatol;
      this.element.off("click", "button");
      this.element.on("click", "button", function () {
        if ($(this).data("pagesize")) {
          // $(this).next(".dropdown-menu").addClass("open")
        } else {
          var pageText = $(this).data("page");
          switch (pageText) {
            case "prev":
              settings.page = settings.page - 1 >= 1 ? settings.page - 1 : 1;
              pageText = settings.page;
              break;
            case "next":
              settings.page = Number(settings.page) + 1 <= pageTatol ? Number(settings.page) + 1 : pageTatol;
              pageText = settings.page;
              break;
            case "before":
              settings.page = settings.page - settings.fastForward >= 1 ? settings.page - settings.fastForward : 1;
              pageText = settings.page;
              break;
            case "after":
              settings.page = Number(settings.page) + Number(settings.fastForward) <= pageTatol ? Number(settings.page) + Number(settings.fastForward) : pageTatol;
              pageText = settings.page;
              break;
            case "go":
              var p = parseInt(ele.find("input").val());
              if (/^[0-9]*$/.test(p) && p >= 1 && p <= pageTatol) {
                settings.page = p;
                pageText = p;
              } else {
                return;
              }
              break;
            default:
              settings.page = pageText;
          }
          // 点击或跳转当前页码不执行任何操作
          if (pageText == that.pageNum) {
            return;
          }
          that.pageNum = settings.page;
          that.viewHtml();
          settings.backFun(pageText, that.settings.pageSize || 10);
        }
        $(document).trigger("pageSizeOnChange");
      });
      this.element.off("keyup", "input");
      this.element.on("keyup", "input", function (event) {
        if (event.keyCode == 13) {
          var p = parseInt(ele.find("input").val());
          if (/^[0-9]*$/.test(p) && p >= 1 && p <= pageTatol && p != that.pageNum) {
            settings.page = p;
            that.pageNum = p;
            that.viewHtml();
            settings.backFun(p, that.settings.pageSize || 10);
          } else {
            return;
          }
        }
      });
      if (settings.fastForward > 0) {
        ele.find(".spage-after").hover(function () {
          $(this).html("&raquo;");
        }, function () {
          $(this).html("...");
        });
        ele.find(".spage-before").hover(function () {
          $(this).html("&laquo;");
        }, function () {
          $(this).html("...");
        });
      }
      if (settings.showPageSizeList) {
        ele.find(".dropdown-menu li").off("click")
        ele.find(".dropdown-menu li").on("click", function () {
          var size = $(this).find("a").html()
          that.settings.pageSize = size;
          that.settings.page = 1;
          that.viewHtml();
          settings.backFun(that.settings.page, size);
          $(document).trigger("pageSizeOnChange");
        })
      }
    },
    minSort: function (arr) {
      var min
      for (var i = 0; i < arr.length; i++) {
        for (var j = i; j < arr.length; j++) {
          if (arr[i] > arr[j]) {
            min = arr[j]
            arr[j] = arr[i]
            arr[i] = min
          }
        }
      }
      return arr
    }
  });
  $.fn.wellPagination = function (options) {
    return this.each(function () {
      new Plugin(this, options);
    });
  }
})(jQuery, window, document);
