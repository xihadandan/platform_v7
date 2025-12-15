define(['constant', 'commons', 'server', 'appModal', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appModal,
  ListViewWidgetDevelopment
) {
  var AppManageLogListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppManageLogListDevelopment, ListViewWidgetDevelopment, {
    beforeRender: function (options, configuration) {
      $.each(configuration.query.fields, function (index, item) {
        if (item.name == 'createTime') {
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
    afterRender: function (options, configuration) {
      this.defaultTime();
      $("button[name='query']").trigger('click');
    },
    onLoadSuccess: function () {
      var $element = this.widget.element;
      $element.find('.fixed-table-body tbody tr').css({
        cursor: 'text'
      });
    },
    defaultTime: function () {
      var $label = $("input[name='createTime']").parents('.formbuilder').first().find('.label-formbuilder');
      $label.append(
        "<div class='search-tooltip'><i class='iconfont icon-ptkj-tishishuoming'></i><div class='search-tooltip-content'>1、默认显示最近1个月<br>2、不支持跨年度的时间范围查询</div></div>"
      );
      var val = getLastMonth();
      $("input[name='createTime']").val(val);
      $("input[name='createTime']").next('input').attr('autocomplete', 'off').val(val);

      function getLastMonth() {
        var now = new Date();
        var year = now.getFullYear();
        var month = now.getMonth() + 1; //0-11表示1-12月
        var day = now.getDate();
        var hour = getDate(now.getHours());
        var minutes = getDate(now.getMinutes());
        var dateObj = {};
        dateObj.now = year + '-' + getDate(month) + '-' + getDate(day) + ' ' + hour + ':' + minutes;
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
              dateObj.last = year + '-' + getDate(month - 1) + '-' + getDate(lastMonthDay - (nowMonthDay - day));
            } else {
              dateObj.last = year + '-' + getDate(month - 1) + '-' + getDate(lastMonthDay);
            }
          } else {
            dateObj.last = year + '-' + getDate(month - 1) + '-' + getDate(day);
          }
        }
        return dateObj.last + ' 00:00 至 ' + dateObj.now;
      }

      function getDate(d) {
        return d - 9 > 0 ? d : '0' + d;
      }
    },
    onResetQueryFields: function () {
      this.defaultTime();
    },

    lineEnderButtonHtmlFormat: function (format, row, index) {
      var $html = $(format.before);
      var isShow =
        (row.dataTypeId == '1' &&
          (row['operationId'] == 'add' ||
            row['operationId'] == 'delete' ||
            (row['operationId'] == 'edit' && row['isShowDetailToEntityButton'] == 0))) ||
        (row.dataTypeId == '2' && row['operationId'] == 'flowListExport');
      $html.find(isShow ? '.btn_class_btn_check' : '').remove();
      format.after = $html[0].outerHTML;
    }
  });
  return AppManageLogListDevelopment;
});
