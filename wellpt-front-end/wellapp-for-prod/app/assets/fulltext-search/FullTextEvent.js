(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'layDate', 'niceScroll'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, layDate, niceScroll) {
  var FullTextEvent = {
    renderSearchResult: function (keyword, newHeader_options) {
      var self = this;
      var html = '';
      html +=
        "<div class='ui-wSearchContainer'>" +
        "<div class='ui-wSearchContainer-title'>全局搜索 <i class='iconfont icon-ptkj-dacha'></i></div>" +
        "<div class='ui-wSearchContainer-content'>" +
        "<div class='ui-wSearchContainer-input'>" +
        "<i class='iconfont icon-ptkj-sousuochaxun'></i>" +
        "<input type='text' class='form-control' id='keywords' name='keywords' value='" +
        keyword +
        "'>" +
        "<i class='iconfont icon-ptkj-dacha'></i>" +
        "<div class='searchBtn well-btn w-btn-primary'>查询</div>" +
        '</div>' +
        "<div class='ui-wSearchContainer-result'>" +
        "<span class='ui-wSearchContainer-num'>为您找到相关结果约<span id='resultNum'></span>个</span>" +
        "<div class='ui-wSearchContainer-times'>" +
        "<div class='ui-wSearchContainer-timesWrap well-select'><span>时间不限</span> <i class='iconfont icon-ptkj-xianmiaojiantou-xia well-select-arrow'></i></div>" +
        "<input type='hidden' class='form-control' id='timeDetail' name='timeDetail' value=''>" +
        "<div class='ui-wSearchContainer-detail'>" +
        "<ul class='ui-wSearchContainer-default'>" +
        "<li class='active' data-val=''>时间不限</li>" +
        "<li data-val='ONE_DAY'>一天内</li>" +
        "<li data-val='ONE_WEEK'>一周内</li>" +
        "<li data-val='ONE_MONTH'>一月内</li>" +
        "<li data-val='ONE_YEAR'>一年内</li>" +
        '</ul>' +
        "<div class='ui-wSearchContainer-custom'>" +
        "<div class='ui-wSearchContainer-custom-title'>自定义</div>" +
        "<div class='ui-wSearchContainer-custom-time'>从<input class='form-control' id='startTime' placeholder='请选择日期'/> <i class='iconfont icon-ptkj-rilixuanzeriqi'></i></div>" +
        "<div class='ui-wSearchContainer-custom-time'>至<input class='form-control' id='endTime' placeholder='请选择日期'/> <i class='iconfont icon-ptkj-rilixuanzeriqi'></i></div>" +
        "<div class='ui-wSearchContainer-custom-confirm'><div class='well-btn w-btn-primary'>确定</div></div>" +
        '</div>' +
        '</div>' +
        '</div>' +
        "<div class='ui-wSearchContainer-filter'><input type='text' class='form-control' id='timeType' name='timeType'></div>" +
        '</div>' +
        "<ul class='full-text-retrieval'></ul>" +
        "<div class='full-text-pagination'></div>" +
        '</div>' +
        '</div>';

      top.$('body').append(html);
      self.computedHeight();
      if ($('#timeType').val() == '') {
        $('#timeType').val(' ');
      }
      $('#timeType').wellSelect({
        data: [
          {
            text: '全部',
            id: ' '
          },
          {
            text: '创建时间',
            id: 'createTime'
          },
          {
            text: '修改时间',
            id: 'modifyTime'
          }
        ],
        value: 'timeType',
        searchable: false
      });
      FullTextEvent.getResultData();
      FullTextEvent.defaultSearchEvent();
      var startApp = appContext.startApp;
      appContext.startApp = function (ui, options) {
        if (options.target === '_targetWidget' && top.$('body').find('.ui-wSearchContainer:visible').length > 0) {
          top.$('body').find('.ui-wSearchContainer').remove();
        }
        startApp.apply(this, arguments);
      };
      self.categoryList = {};
      self.getCategory();
    },
    getCategory() {
      var self = this;
      JDS.call({
        service: 'fulltextDocumentIndexService.getDataDictionaryList',
        version: '',
        //data: data,
        success: function (res) {
          if (res.success) {
            $.each(res.data, function (i, item) {
              self.categoryList[item.code] = item.name;
            });
          }
        }
      });
    },
    computedHeight: function () {
      var windowInnerHeight = window.innerHeight;
      var retrievalOffSetHeight = $('.full-text-retrieval').offset().top;
      $('.full-text-retrieval').css('height', windowInnerHeight - retrievalOffSetHeight - 70 + 'px');
    },
    defaultSearchEvent: function () {
      $('.ui-wSearchContainer .full-text-retrieval').niceScroll({
        cursorcolor: '#ccc'
      });
      $('.ui-wSearchContainer-title .icon-ptkj-dacha', '.ui-wSearchContainer').on('click', function () {
        $('.ui-wSearchContainer').remove();
        top.$('body').removeClass('fullFixedBody');
      });

      $('.ui-wSearchContainer-input .icon-ptkj-dacha', '.ui-wSearchContainer').on('click', function () {
        $('.ui-wSearchContainer').find('#keywords').val('');
        $(this).hide();
      });

      $('.ui-wSearchContainer #keywords').on('keyup', function (e) {
        if (e.keyCode === 13) {
          FullTextEvent.getResultData();
        }
        if ($(this).val() != '') {
          $('.ui-wSearchContainer-input .icon-ptkj-dacha').show();
        }
      });
      $('.searchBtn', '.ui-wSearchContainer').on('click', function (e) {
        FullTextEvent.getResultData();
      });

      $('.ui-wSearchContainer-timesWrap', '.ui-wSearchContainer').on('click', function (e) {
        e.stopPropagation();
        $('.ui-wSearchContainer').find('.ui-wSearchContainer-detail').addClass('showSearchDetail');
        $('.ui-wSearchContainer-timesWrap').addClass('well-select-visible');
      });

      $('.ui-wSearchContainer-default').on('click', 'li', function () {
        $(this).addClass('active').siblings().removeClass('active');
        $('.ui-wSearchContainer').find('.ui-wSearchContainer-detail').removeClass('showSearchDetail');
        $('.ui-wSearchContainer-timesWrap').removeClass('well-select-visible');
        $('.ui-wSearchContainer-timesWrap').find('span').text($(this).text());
        $('#timeDetail').val($(this).data('val'));
        $('#startTime').val('');
        $('#endTime').val('');
        FullTextEvent.getResultData();
      });

      $('#timeType').on('change', function () {
        FullTextEvent.getResultData();
      });

      layDate.render({
        elem: '#startTime',
        trigger: 'click'
      });
      layDate.render({
        elem: '#endTime',
        trigger: 'click'
      });

      $('.ui-wSearchContainer-custom-confirm .well-btn', '.ui-wSearchContainer-custom').on('click', function () {
        var sTime = $('#startTime', '.ui-wSearchContainer-custom').val();
        var eTime = $('#endTime', '.ui-wSearchContainer-custom').val();
        var startTime = Date.parse(new Date(sTime));
        var endTime = Date.parse(new Date(eTime));

        if (startTime != '' && endTime != '' && startTime > endTime) {
          appModal.error('开始时间不能大于结束时间！');
          return false;
        }
        $('.ui-wSearchContainer-timesWrap').find('span').text('自定义');
        $('.ui-wSearchContainer-timesWrap').removeClass('well-select-visible');
        $('.ui-wSearchContainer').find('.ui-wSearchContainer-detail').removeClass('showSearchDetail');
        $('.ui-wSearchContainer-default').find('li').removeClass('active');
        $('#timeDetail').val('');
        FullTextEvent.getResultData();
      });

      $('.full-text-retrieval').on('click', '.result-title', function (e) {
        e.stopPropagation();
        e.preventDefault();
        var url = $(this).data('url');
        window.open(url.indexOf('http') > -1 ? url : ctx + url, '_blank');
      });

      $('.pagination .page-number a', '.ui-wSearchContainer')
        .off()
        .on('click', function () {
          FullTextEvent.getResultData($(this).text());
        });
      $('.page-list .dropdown-menu li a', '.ui-wSearchContainer')
        .off()
        .on('click', function () {
          FullTextEvent.getResultData();
        });
      $(document).on('click', function (e) {
        // e.stopPropagation()
        if ($('.showSearchDetail')[0] != $(e.target).parents('.ui-wSearchContainer-detail')[0] && $('.showSearchDetail').length > 0) {
          $('.ui-wSearchContainer-detail').removeClass('showSearchDetail');
          $('.ui-wSearchContainer-timesWrap').removeClass('well-select-visible');
        }
      });
    },
    getResultData: function (currPage, pageSize) {
      var self = this;
      var data = {
        keyword: $('#keywords', '.ui-wSearchContainer').val(),
        pagingInfo: {
          currentPage: currPage || 1,
          pageSize: pageSize || 10
        },
        fragmentSize: 250
      };
      if ($('#startTime', '.ui-wSearchContainer').val() != '') {
        data.startTime = $('#startTime', '.ui-wSearchContainer').val() + ' 00:00:00';
      }
      if ($('#endTime', '.ui-wSearchContainer').val() != '') {
        data.endTime = $('#endTime', '.ui-wSearchContainer').val() + ' 23:59:59';
      }
      if ($('#timeType', '.ui-wSearchContainer').val() && $.trim($('#timeType', '.ui-wSearchContainer').val()) != '') {
        data.order = {
          property: $('#timeType', '.ui-wSearchContainer').val()
        };
      }
      if ($('#timeDetail', '.ui-wSearchContainer').val() != '') {
        data.dateRange = $('#timeDetail', '.ui-wSearchContainer').val();
      }
      JDS.call({
        service: 'fulltextDocumentIndexService.query',
        version: '',
        data: data,
        success: function (res) {
          if (res.success) {
            self.renderList(res.data.dataList);
            var pagingInfo = res.data.pagingInfo;
            $('#resultNum').text(pagingInfo.totalCount);
            // if (pagingInfo.totalPages === 1) {
            //   $(".full-text-pagination").empty();
            // } else
            if (pagingInfo.currentPage === 1) {
              self.renderPagination(pagingInfo.currentPage, pagingInfo.pageSize, pagingInfo.totalCount);
            }
          }
        }
      });
    },
    renderList: function (list) {
      var self = this;
      if (self.categoryList === undefined || Object.keys(self.categoryList).length == 0) {
        self.categoryList = {};
        self.getCategory();
      }

      $('.full-text-retrieval').empty();
      if (list == null || list.length == 0) {
        var $li = "<li class='full-no-data'><div class='well-table-no-data'></div><span>没有找到匹配的记录</span></li>";
        $('.full-text-retrieval').append($li);
      } else {
        $.each(list, function (i, item) {
          var $li =
            '<li>' +
            "<div class='result-title' data-url='" +
            item.url +
            "'>" +
            item.title +
            '</div>' +
            "<div class='result-body'>" +
            item.content +
            (item.fileNames == null ? '' : item.fileNames) +
            '</div>' +
            "<div class='result-footer'><span>类型：" +
            self.categoryList[item.categoryCode] +
            '</span>' +
            (item.router == null ? '' : '<span>路径：' + item.router + '</span>') +
            '<span>创建人：' +
            item.creator +
            '</span><span>创建时间：' +
            item.createTime +
            '</span><span>修改时间：' +
            item.modifyTime +
            '</span></div>' +
            '</li>';
          $('.full-text-retrieval').append($li);
        });
        $('.full-text-retrieval').scrollTop(0);
      }
    },
    renderPagination: function (page, pageSize, total) {
      var self = this;
      $('.full-text-pagination').wellPagination({
        showSkip: true,
        page: page,
        pageSize: pageSize,
        total: total,
        showTotal: true,
        showPageSizeList: true,
        backFun: function (page, pageSize) {
          self.getResultData(page, pageSize || 10);
        }
      });
    }
  };
  window.FullTextEvent = FullTextEvent;
  return FullTextEvent;
});
