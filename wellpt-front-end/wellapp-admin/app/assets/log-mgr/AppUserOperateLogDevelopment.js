define(['constant', 'commons', 'server', 'appModal', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appModal,
  ListViewWidgetDevelopment
) {
  var AppUserOperateLogDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppUserOperateLogDevelopment, ListViewWidgetDevelopment, {
    beforeRender: function (options, configuration) {
      this.isFirst = true;
      $.each(configuration.query.fields, function (index, item) {
        if (item.name == '操作时间') {
          item.queryOptions.change = function (value) {
            var year = value.split(' 至 ');
            var last = new Date(year[0]).getFullYear();
            var curr = new Date(year[1]).getFullYear();
            if (last != curr) {
              setTimeout(function () {
                $('.laydate-btns-confirm').addClass('laydate-disabled').addClass('laydate-not-same');
                $('.layui-laydate').append('<div class="layui-laydate-hint">不支持跨年度的时间范围查询</div>').show();
              }, 100);
              setTimeout(function () {
                $('.layui-laydate').find('.layui-laydate-hint').remove();
              }, 3000);
            } else {
              $('.laydate-btns-confirm').removeClass('laydate-not-same');
            }
          };
        }
      });
    },

    beforeLoadData: function (options, configuration) {
      if (this.isFirst) {
        var date = this.getLastMonth();
        this.addOtherConditions([
          {
            columnIndex: '操作时间',
            value: date.split(' 至 '),
            type: 'between'
          }
        ]);
        this.isFirst = false;
      } else {
        this.clearOtherConditions();
      }
    },
    afterRender: function () {
      this.defaultTime();
    },
    onLoadSuccess: function () {
      var $element = this.widget.element;
      $element.find('.fixed-table-body tbody tr').css({
        cursor: 'text'
      });
    },
    defaultTime: function () {
      var $label = $("input[name='操作时间']").parents('.formbuilder').first().find('.label-formbuilder');
      $label.append(
        "<div class='search-tooltip'><i class='iconfont icon-ptkj-tishishuoming'></i><div class='search-tooltip-content'>1、默认显示最近1个月<br>2、不支持跨年度的时间范围查询</div></div>"
      );
      var val = this.getLastMonth();
      $("input[name='操作时间']").val(val);
      $("input[name='操作时间']").next('input').attr('autocomplete', 'off').val(val);
    },
    getLastMonth: function () {
      var now = new Date();
      var year = now.getFullYear();
      var month = now.getMonth() + 1; //0-11表示1-12月
      var day = now.getDate();
      var hour = this.getDate(now.getHours());
      var minutes = this.getDate(now.getMinutes());
      var dateObj = {};
      dateObj.now = year + '-' + this.getDate(month) + '-' + this.getDate(day) + ' ' + hour + ':' + minutes;
      var nowMonthDay = new Date(year, month, 0).getDate(); //当前月的总天数
      if (month - 1 <= 0) {
        //如果是1月，年数往前推一年<br>
        dateObj.last = year + '-01-01';
      } else {
        var lastMonthDay = new Date(year, parseInt(month) - 1, 0).getDate();
        if (lastMonthDay < day) {
          //1个月前所在月的总天数小于现在的天日期
          if (day < nowMonthDay) {
            //当前天日期小于当前月总天数
            dateObj.last = year + '-' + this.getDate(month - 1) + '-' + this.getDate(lastMonthDay - (nowMonthDay - day));
          } else {
            dateObj.last = year + '-' + this.getDate(month - 1) + '-' + this.getDate(lastMonthDay);
          }
        } else {
          dateObj.last = year + '-' + this.getDate(month - 1) + '-' + this.getDate(day);
        }
      }
      return dateObj.last + ' 00:00 至 ' + dateObj.now;
    },
    getDate: function (d) {
      return d - 9 > 0 ? d : '0' + d;
    },
    onResetQueryFields: function () {
      this.defaultTime();
    },
    // changeToFieldSearch: function () {
    //   this.defaultTime();
    // },
    lineEnderButtonHtmlFormat: function (format, row, index) {
      var $html = $(format.before);
      $html.find(row['DETAILS_CNT'] != 0 ? '' : '.btn_class_log_detail').remove();
      format.after = $html[0].outerHTML;
    }
  });
  return AppUserOperateLogDevelopment;
});
