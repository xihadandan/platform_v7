define([
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'layDate',
  'bootstrapTable',
  'bootstrapTable_editable',
  'HtmlWidgetDevelopment',
  'HolidayArragement'
], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  laydate,
  bootstrapTable,
  bootstrapTable_editable,
  HtmlWidgetDevelopment,
  HolidayArragement
) {
  var JDS = server.JDS;

  //  平台应用_基础数据管理_节假日安排二开
  var AppHolidayArrangementWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppHolidayArrangementWidgetDevelopment, HtmlWidgetDevelopment, {
    // 初始化回调
    init: function () {
      var self = this;
      var $element = this.widget.element;
      $element.parent().css({
        padding: 0
      });
      getListAllYear();

      $($element) //点击年份
        .off('click', '.year-item')
        .on('click', '.year-item', function () {
          $(this).addClass('active').siblings().removeClass('active');
          self.year = $(this).data('year');
          var isEdit = $(this).parent().attr('id') == 'currYear' ? true : false;
          $('.btn-save-holiday', $element)[isEdit ? 'removeClass' : 'addClass']('hide');
          $('.addHoliday', $element)[isEdit ? 'removeClass' : 'addClass']('hide');
          getHolidayArrangement(isEdit);
        });

      $($element) // 删除
        .off('click', '.holiday-header-btn')
        .on('click', '.holiday-header-btn', function () {
          $(this).parents('.holiday-content-item').remove();
        });

      $($element) //减去补班
        .off('click', '.cutDate')
        .on('click', '.cutDate', function () {
          $(this).parent().next('.error').remove();
          $(this).parent().remove();
        });

      $($element) // 添加补班
        .off('click', '.addDate')
        .on('click', '.addDate', function () {
          HolidayArragement.addDate($(this));
        });

      $($element) // 展开、折叠
        .off('click', '.holiday-header-collpase')
        .on('click', '.holiday-header-collpase', function () {
          HolidayArragement.holidayCollpase($(this));
        });

      $('.addHoliday', $element) //  添加节假日
        .off()
        .on('click', function () {
          HolidayArragement.getHolidays($element, self.year);
        });

      $('.btn-save-holiday button', $element) // 保存
        .off()
        .on('click', function () {
          var list = [];
          var $items = $element.find('.holiday-content-item');
          var isError = false;
          $('.error').remove();
          $.each($items, function (index, item) {
            var obj = {};
            var buDates = []; // 补班时间
            var date = $(item).find("input[name='date']").val();
            obj.holidayInstanceDate = $(item).find('.time-date').text();
            obj.holidayUuid = $(item).data('holiday');
            obj.holidayName = $(item).find('.time-text').text();
            obj.uuid = $(item).data('uuid') || '';
            obj.year = self.year;
            if (date != '') {
              obj.fromDate = $.trim(date.split('至')[0]);
              obj.toDate = $.trim(date.split('至')[1]);
              if (Date.parse(obj.toDate) - Date.parse(obj.fromDate) < 0) {
                $(item).find("input[name='date']").after('<div class="error">结束日期需大于等于开始日期！</div>');
                isError = true;
              }
            } else {
              $(item).find("input[name='date']").after('<div class="error">结束日期需大于等于开始日期！</div>');
              isError = true;
            }
            var $rows = $(item).find('.holiday-row-days');
            $.each($rows, function (rIndex, rItem) {
              var buDate = $(rItem).find("input[name='buDate']").val();
              var buDay = $(rItem).find("input[name='buDay']").val();
              if (buDate == '' || buDay == '') {
                $(rItem).after('<div class="error">补班时间不能为空！</div>');
                isError = true;
              } else {
                buDates.push(buDate + '|' + buDay);
              }
            });

            obj.remark = $(item).find('textarea[name="remark"]').val();
            obj.makeupDate = buDates.join(';');
            list.push(obj);
          });
          if (isError) {
            return false;
          }
          $.ajax({
            url: ctx + '/api/ts/holiday/schedule/saveAll?year=' + self.year,
            type: 'post',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(list),
            success: function (res) {
              if (res.code == 0) {
                appModal.success('保存成功！');
                var activeIndex = $('#currYear', $element).find('.year-item.active').index();
                getListAllYear(activeIndex);
                getHolidayArrangement(true);
              } else {
                appModal.error('保存失败！');
              }
            }
          });
        });

      function getHolidayArrangement(isEdit) {
        // 获取节假日安排
        $.ajax({
          url: ctx + '/api/ts/holiday/schedule/listByYear',
          type: 'get',
          data: { year: self.year },
          success: function (res) {
            var data = _.sortBy(res.data, function (item) {
              return item.holidayInstanceDate;
            });
            HolidayArragement.renderHolidayArrangement($element, data, isEdit);
          }
        });
      }

      function getListAllYear(activeIndex) {
        // 获取年份列表
        $.ajax({
          url: ctx + '/api/ts/holiday/schedule/listAllYear',
          type: 'get',
          success: function (res) {
            var data = res.data;
            renderYearList(data, activeIndex);
          }
        });
      }

      function renderYearList(list, activeIndex) {
        //渲染年份列表
        var currYear = $('#currYear', $element);
        var historyYear = $('#historyYear', $element);
        var currHtml = '';
        var historyHtml = '';
        $.each(list, function (index, item) {
          if (list.length - index <= 5) {
            currHtml +=
              "<div class='year-item' data-year='" + item.year + "'><span>" + item.year + '</span><span>' + item.count + '</span></div>';
          } else {
            historyHtml +=
              "<div class='year-item' data-year='" + item.year + "'><span>" + item.year + '</span><span>' + item.count + '</span></div>';
          }
        });
        currYear.html(currHtml);
        historyYear.html(historyHtml);
        var itemIndex = activeIndex ? activeIndex : 0;
        $('#currYear', $element)
          .find('.year-item:eq(' + itemIndex + ')')
          .trigger('click');
      }
    },
    refresh: function () {
      var _self = this;
      _self.init();
    }
  });
  return AppHolidayArrangementWidgetDevelopment;
});
