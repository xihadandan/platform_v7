(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'layDate'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, laydate) {
  var HolidayArragement = {
    renderHolidayArrangement: function ($element, list, isEdit, autoUpdate) {
      // 渲染节假日安排
      var self = this;
      var $content = $('.holiday-content', $element);
      var html = '';
      $.each(list, function (index, item) {
        var isDisabled = isEdit ? '' : 'disabled';
        var isHide = isEdit ? '' : 'hide';
        var isThisYear = item.holidayInstanceDate.indexOf(new Date().getFullYear()) > -1;

        html +=
          '<div class="holiday-content-item" data-uuid="' +
          item.uuid +
          '" data-holiday="' +
          item.holidayUuid +
          '">' +
          '<div class="holiday-item-time">' +
          '<i class="time-yuandian"></i>' +
          '<span class="time-date">' +
          (isThisYear ? item.holidayInstanceDate.substring(5) : item.holidayInstanceDate) +
          '</span>' +
          '<span class="time-text ' +
          (isThisYear ? '' : 'otherYear') +
          '">' +
          item.holidayName +
          '</span>' +
          '</div>' +
          '<div class="holiday-item-detail">' +
          '<div class="holiday-detail-header">' +
          '<span class="holiday-header-text">' +
          '<i class="iconfont icon-ptkj-shixinjiantou-xia holiday-header-collpase"></i><span class="holiday-date">' +
          item.fromDate +
          '至' +
          item.toDate +
          '</span>放假，共<span class="holiday-count">' +
          self.getHolidayCount(item.fromDate, item.toDate) +
          '</span>天。';
        if (item.makeupDate != null && item.makeupDate != '') {
          var makeupDate = item.makeupDate.split(';');
          var buTitle = [];
          $.each(makeupDate, function (dIndex, dItem) {
            buTitle.push(dItem.split('|')[0]);
          });
          html += buTitle.join('、') + '补班。';
        }
        html +=
          '</span>' +
          '<span class="holiday-header-btn ' +
          isHide +
          '">' +
          '<i class="iconfont icon-ptkj-shanchu"></i>删除' +
          '</span>' +
          '</div>' +
          '<div class="holiday-detail-body">';
        if (autoUpdate) {
          var active = item.autoUpdate ? 'active' : '';
          html +=
            '<div class="holiday-body-row">' +
            '<span class="holiday-row-label">自动更新假期</span>' +
            '<div class="holiday-row-content">' +
            '<div class="autoUpdate switch-wrap ' +
            active +
            '"><span class="switch-radio"></span></div>' +
            '</div>' +
            '</div>';
        }
        var isActive = autoUpdate && item.autoUpdate ? ' disabled' : '';
        var isDisabledClass = autoUpdate && item.autoUpdate ? ' isDisabled' : '';

        html +=
          '<div class="holiday-body-row">' +
          '<span class="holiday-row-label">' +
          '<font style="color:#f00;">*</font>假期起止日期：</span>' +
          '<div class="holiday-row-content">' +
          '<input class="form-control" type="text" name="date" id="date' +
          item.holidayUuid +
          '" value="' +
          (item.fromDate + ' 至 ' + item.toDate) +
          '" autocomplete="off" ' +
          isDisabled +
          isActive +
          '>' +
          '</div>' +
          '</div>' +
          '<div class="holiday-body-row">' +
          '<span class="holiday-row-label">补班日期：</span>' +
          '<div class="holiday-row-content">';
        if (item.makeupDate != '' && item.makeupDate != null) {
          var makeupDate = item.makeupDate.split(';');
          $.each(makeupDate, function (mIndex, mItem) {
            html +=
              '<div class="holiday-row-days" data-index="' +
              mIndex +
              '">' +
              '<input class="form-control" type="text" name="buDate" id="buDate' +
              item.holidayUuid +
              mIndex +
              '" autocomplete="off" value="' +
              mItem.split('|')[0] +
              '" ' +
              isDisabled +
              isActive +
              '>' +
              '补&nbsp;&nbsp;&nbsp;<input class="form-control" type="text" name="buDay" id="buDay' +
              item.holidayUuid +
              mIndex +
              '" autocomplete="off" value="' +
              mItem.split('|')[1] +
              '" ' +
              isDisabled +
              isActive +
              '>' +
              '<i class="iconfont icon-ptkj-jianhao cutDate ' +
              isHide +
              ' ' +
              isDisabledClass +
              '" ></i>' +
              '</div>';
          });
        } else {
          // html +=
          //   '<div class="holiday-row-days" data-index="1">' +
          //   '<input class="form-control" type="text" name="buDate" id="buDate' +
          //   item.holidayUuid +
          //   '1" autocomplete="off" ' +
          //   isDisabled +
          //   isActive +
          //   '>' +
          //   '补&nbsp;&nbsp;&nbsp;<input type="text" class="form-control" name="buDay" id="buDay' +
          //   item.holidayUuid +
          //   '1" autocomplete="off" ' +
          //   isDisabled +
          //   isActive +
          //   '>' +
          //   '<i class="iconfont icon-ptkj-jianhao cutDate ' +
          //   isHide +
          //   ' ' +
          //   isDisabledClass +
          //   '"></i>' +
          //   '</div>';
        }

        html +=
          '<div class="mt10">' +
          '<button type="button" class="well-btn w-btn-primary well-btn-sm w-noLine-btn addDate ' +
          isHide +
          '" ' +
          isActive +
          '>' +
          '<i class="iconfont icon-ptkj-jiahao"></i>添加补班日期</button>' +
          '</div>' +
          '</div>' +
          '</div>' +
          '<div class="holiday-body-row">' +
          '<span class="holiday-row-label">备注：</span>' +
          '<div class="holiday-row-content">' +
          '<textarea class="form-control" type="text" name="remark" ' +
          isDisabled +
          isActive +
          '>' +
          (item.remark || '') +
          '</textarea>' +
          '</div>' +
          '</div>' +
          '</div>' +
          '</div>' +
          '</div>';
      });
      $content.html(html);
      var date = $("input[name='date']", $element);
      var buDate = $("input[name='buDate']", $element);
      var buDay = $("input[name='buDay']", $element);
      $.each(date, function (index, item) {
        // 假期起止日期
        var id = $(item).attr('id');
        laydate.render({
          elem: '#' + id,
          format: 'yyyy-MM-dd',
          trigger: 'click',
          range: '至',
          type: 'date',
          done: function (value) {
            $('#' + id)
              .parents('.holiday-item-detail')
              .find('.holiday-date')
              .text(value);
            if (value != '') {
              var d1 = $.trim(value.split('至')[0]);
              var d2 = $.trim(value.split('至')[1]);
              var count = self.getHolidayCount(d1, d2);
              $('#' + id)
                .parents('.holiday-item-detail')
                .find('.holiday-count')
                .text(count);
            }
          }
        });
      });

      $.each(buDate, function (index, item) {
        // 补班日期
        var id = $(item).attr('id');
        laydate.render({
          elem: '#' + id,
          format: 'yyyy-MM-dd',
          trigger: 'click',
          type: 'date'
        });
      });

      $.each(buDay, function (index, item) {
        // 补
        var id = $(item).attr('id');
        laydate.render({
          elem: '#' + id,
          format: 'yyyy-MM-dd',
          trigger: 'click',
          type: 'date'
        });
      });

      $element.off('click', '.autoUpdate').on('click', '.autoUpdate', function () {
        var hasActive = $(this).hasClass('active');
        $(this)[hasActive ? 'removeClass' : 'addClass']('active');
        if (hasActive) {
          $(this).parents('.holiday-content-item').find('input').removeAttr('disabled');
          $(this).parents('.holiday-content-item').find('textarea').removeAttr('disabled');
          $(this).parents('.holiday-content-item').find('button').removeAttr('disabled');
          $(this).parents('.holiday-content-item').find('.cutDate').removeClass('isDisabled');
        } else {
          $(this).parents('.holiday-content-item').find('input').attr('disabled', true);
          $(this).parents('.holiday-content-item').find('textarea').attr('disabled', true);
          $(this).parents('.holiday-content-item').find('button').attr('disabled', true);
          $(this).parents('.holiday-content-item').find('.cutDate').addClass('isDisabled');
        }
      });
    },
    getHolidayCount: function (formDate, toDate) {
      var sDate1 = Date.parse(formDate);
      var sDate2 = Date.parse(toDate);
      var dateSpan = sDate2 - sDate1;
      var days = Math.floor(dateSpan / (24 * 3600 * 1000)) + 1;
      return days > 0 ? days : 0;
    },
    getHolidays: function ($element, curYear, autoUpdate, yearHolidaySchedule) {
      var self = this;
      var html = this.getHolidayHtml();
      var holidayUuids = [];
      var choseUuids = [];
      var $holiday = $element.find('.holiday-content-item');
      $.each($holiday, function (hIndex, hItem) {
        holidayUuids.push($(hItem).data('holiday'));
      });
      var $holidayArragementDialog = appModal.dialog({
        title: '添加节假日',
        message: html,
        size: 'large',
        width: '850',
        height: '600',
        shown: function () {
          getAllBySystemUnitIdsLikeFields(true);
          renderTagOptionSet();
          $('#holidayTable', $holidayArragementDialog).bootstrapTable({
            data: [],
            idField: 'id',
            striped: false,
            showColumns: false,
            sortable: true,
            undefinedText: '',
            clickToSelect: true,
            columns: [
              {
                checkbox: true
              },
              {
                field: 'uuid',
                title: 'uuid',
                width: 0
              },
              {
                field: 'name',
                title: '名称',
                width: '30%',
                formatter: function (v, row, i) {
                  return "<div title='" + v + "'>" + v + '</div>';
                }
              },
              {
                field: 'holidayDateName',
                title: '日期',
                width: '30%'
              },
              {
                field: 'tag',
                title: '',
                width: '30%',
                formatter: function (v, row, i) {
                  var html = '';
                  if (v != '' && v != null) {
                    var val = v.split(';');
                    $.each(val, function (index, item) {
                      html += "<span class='holiday-tag'>" + item + '</span>';
                    });
                  }
                  return html;
                }
              }
            ],
            onPostBody: function () {
              $('#holidayTable', $holidayArragementDialog).css({
                // 'table-layout': 'auto'
              });
              $('#holidayTable', $holidayArragementDialog).find("td[data-field='tag']").css({
                'white-space': 'normal',
                'line-height': '1'
              });
              $('#holidayTable', $holidayArragementDialog).find("td[data-field='uuid']").hide();
              $('#holidayTable', $holidayArragementDialog).find("th[data-field='uuid']").hide();
            }
          });

          $('#holidayTable', $holidayArragementDialog).parents('.bootstrap-table').addClass('ui-wBootstrapTable');

          function getAllBySystemUnitIdsLikeFields(flag) {
            // 获取表格数据
            var $tags = $('.tag-list', $holidayArragementDialog).find('span');
            var tags = [];
            $.each($tags, function (index, item) {
              tags.push($(item).data('name'));
            });
            var keyword = $('#name', $holidayArragementDialog).val();

            $('#holidayTable', $holidayArragementDialog).bootstrapTable('removeAll');
            $.ajax({
              url: ctx + '/api/ts/holiday/getAllBySystemUnitIdsLikeFields',
              type: 'post',
              data: { keyword: keyword || '', tags: tags.join(';') },
              success: function (res) {
                if (res.code == 0) {
                  if (res.data.length > 0) {
                    for (var i = 0; i < res.data.length; i++) {
                      $('#holidayTable', $holidayArragementDialog).bootstrapTable('insertRow', { index: i, row: res.data[i] });
                    }
                    var $tr = $('#holidayTable', $holidayArragementDialog).find('tbody tr');
                    $.each($tr, function (tIndex, tItem) {
                      if (holidayUuids.indexOf(res.data[tIndex].uuid) > -1 && flag) {
                        $(tItem).find("input[name='btSelectItem']").trigger('click');
                      } else if (choseUuids.indexOf(res.data[tIndex].uuid) > -1 && !flag) {
                        $(tItem).find("input[name='btSelectItem']").prop('checked', true);
                      }
                    });
                  }
                }
              }
            });
          }

          $($holidayArragementDialog) // 全选
            .off('click', "input[name='btSelectAll']")
            .on('click', "input[name='btSelectAll']", function (e) {
              e.stopPropagation();
              choseUuids = [];
              if ($(this).prop('checked')) {
                var $tr = $('#holidayTable', $holidayArragementDialog).find('tbody tr');
                var html = '';
                $.each($tr, function (index, item) {
                  var uuid = $(item).find("td[data-field='uuid']").text();
                  var name = $(item).find("td[data-field='name']").text();
                  html +=
                    '<div class="holiday-right-item" data-uuid="' +
                    uuid +
                    '"><span>' +
                    name +
                    '</span><i class="iconfont icon-ptkj-dacha-xiao"></i></div>';
                  choseUuids.push(uuid);
                });

                $('.holiday-right-list', $holidayArragementDialog).html(html);
              } else {
                $('.holiday-right-list', $holidayArragementDialog).empty();
              }
              var len = $('.holiday-right-list', $holidayArragementDialog).find('.holiday-right-item').length;
              $('.holidayCount', $holidayArragementDialog).text(len);
            });

          $($holidayArragementDialog) // 单选
            .off('click', "input[name='btSelectItem']")
            .on('click', "input[name='btSelectItem']", function (e) {
              e.stopPropagation();
              var $span = $('.holiday-right-list', $holidayArragementDialog).find('.holiday-right-item');
              var $tr = $(this).parents('tr').first();
              var uuid = $tr.find("td[data-field='uuid']").text();
              var name = $tr.find("td[data-field='name']").text();
              if ($(this).prop('checked')) {
                $('.holiday-right-list', $holidayArragementDialog).append(
                  '<div class="holiday-right-item" data-uuid="' +
                    uuid +
                    '"><span>' +
                    name +
                    '</span><i class="iconfont icon-ptkj-dacha-xiao"></i></div>'
                );
                choseUuids.push(uuid);
              } else {
                $.each($span, function (index, item) {
                  if ($(item).data('uuid') == uuid) {
                    var cIndex = choseUuids.indexOf(uuid);
                    choseUuids.splice(cIndex, 1);
                    $(item).remove();
                  }
                });
              }
              var len = $('.holiday-right-list', $holidayArragementDialog).find('.holiday-right-item').length;
              $('.holidayCount', $holidayArragementDialog).text(len);
            });

          $($holidayArragementDialog) //  删除选择的节日
            .off('click', '.holiday-right-item i')
            .on('click', '.holiday-right-item i', function () {
              var uuid = $(this).parent().data('uuid');
              var $tr = $('#holidayTable', $holidayArragementDialog).find('tbody tr');
              $.each($tr, function (index, item) {
                var trUuid = $(item).find("td[data-field='uuid']").text();
                if (trUuid == uuid) {
                  $(item).find("input[name='btSelectItem']").trigger('click');
                }
              });
            });

          $('#name', $holidayArragementDialog) // 搜索
            .off()
            .on('keyup', function (e) {
              var val = $(this).val();
              if (val != '') {
                $(this).next().show();
              } else {
                $(this).next().hide();
              }
              if (e.keyCode == '13') {
                getAllBySystemUnitIdsLikeFields();
              }
            });

          $('.input-close', $holidayArragementDialog) // 搜索
            .off()
            .on('click', function (e) {
              $('#name', $holidayArragementDialog).val('');
              getAllBySystemUnitIdsLikeFields();
              $(this).hide();
            });

          $('.input-search', $holidayArragementDialog) // 搜索
            .off()
            .on('click', function (e) {
              getAllBySystemUnitIdsLikeFields();
            });

          $('.tag-icon', $holidayArragementDialog) // 打开标签弹窗
            .off()
            .on('click', function () {
              $('.holiday-type-box', $holidayArragementDialog).show();
              var tags = $('.holiday-type-box', $holidayArragementDialog).data('tags');
              if (tags != undefined) {
                var $span = $('.list', $holidayArragementDialog).find('span');
                $.each($span, function (index, item) {
                  var name = $(item).data('name');
                  if (tags.indexOf(name) > -1) {
                    $(item).addClass('active');
                  }
                });
              }
            });

          $('.list span', $holidayArragementDialog) // 选择或取消标签
            .off()
            .on('click', function () {
              if ($(this).hasClass('active')) {
                $(this).removeClass('active');
              } else {
                $(this).addClass('active');
              }
            });

          $('.btn-sure', $holidayArragementDialog)
            .off()
            .on('click', function () {
              var $span = $('.list', $holidayArragementDialog).find('span');
              var tags = [];
              var html = '';
              $.each($span, function (index, item) {
                if ($(item).hasClass('active')) {
                  var name = $(item).data('name');
                  tags.push(name);
                  html +=
                    '<span class="tag-item" data-name="' +
                    name +
                    '">' +
                    name +
                    '<i class="iconfont icon-ptkj-dacha-xiao tag-close"></i></span>';
                }
              });
              $('.holiday-type-box', $holidayArragementDialog).data('tags', tags).hide();
              $span.removeClass('active');
              $('.tag-list', $holidayArragementDialog).html(html);
              getAllBySystemUnitIdsLikeFields();
            });

          $('.btn-cancel', $holidayArragementDialog)
            .off()
            .on('click', function () {
              $('.list', $holidayArragementDialog).find('span').removeClass('active');
              $('.holiday-type-box', $holidayArragementDialog).hide();
            });

          $('.btn-reset', $holidayArragementDialog)
            .off()
            .on('click', function () {
              $('.list', $holidayArragementDialog).find('span').removeClass('active');
            });

          $($holidayArragementDialog) // 删除标签
            .off('click', '.tag-close')
            .on('click', '.tag-close', function () {
              $(this).parent().remove();
              var $span = $('.tag-list', $holidayArragementDialog).find('span');
              var tags = [];
              $.each($span, function (index, item) {
                var name = $(item).data('name');
                tags.push(name);
              });
              $('.holiday-type-box', $holidayArragementDialog).data('tags', tags);
              getAllBySystemUnitIdsLikeFields();
            });

          function renderTagOptionSet() {
            // 获取标签
            JDS.call({
              service: 'dataDictionaryService.getDataDictionariesByTypeCode',
              data: ['computer_rank', ''],
              version: '',
              async: false,
              success: function (result) {
                var data = result.data;
                if (data.length > 0) {
                  var html = '';
                  $.each(data, function (index, item) {
                    html += "<span data-name='" + item.name + "'>" + item.name + '</span>';
                  });
                  $('.list', $holidayArragementDialog).append(html);
                }
              }
            });
          }
        },
        buttons: {
          ok: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var $items = $('.holiday-right-list', $holidayArragementDialog).find('.holiday-right-item');
              if ($items.length == 0) {
                appModal.error('请选择节假日!');
                return false;
              }
              // 弹窗中取消勾选的要在列表中删掉
              $.each(holidayUuids, function (uIndex, uItem) {
                if (choseUuids.indexOf(uItem) == -1) {
                  $element.find(".holiday-content-item[data-holiday='" + uItem + "']").remove();
                }
              });

              // 添加弹窗中勾选的但是列表没有的节假日
              var html = '';
              $.each($items, function (index, item) {
                var uuid = $(item).data('uuid');
                var hName = $(item).find('span').text(); // 节日名称
                // 判断是否存在列表中
                if (holidayUuids.indexOf(uuid) == -1) {
                  var date = '';
                  $.ajax({
                    url: ctx + '/api/ts/holiday/getHolidayInstanceDate',
                    type: 'get',
                    dataType: 'json',
                    data: { uuid: uuid, year: curYear },
                    async: false,
                    success: function (res) {
                      if (res.code == 0) {
                        date = res.data.indexOf(thisYear) > -1 ? res.data.substr(5) : res.data;
                      } else {
                        date = '';
                      }
                    }
                  });
                  var holidaySchedule = {};
                  if (yearHolidaySchedule && yearHolidaySchedule.length > 0 && autoUpdate) {
                    for (var i = 0; i < yearHolidaySchedule.length; i++) {
                      if (uuid == yearHolidaySchedule[i].holidayUuid) {
                        holidaySchedule = yearHolidaySchedule[i];
                        break;
                      }
                    }
                  }
                  var count = 0;
                  var name = hName;
                  if (autoUpdate && holidaySchedule.holidayUuid != undefined) {
                    name = holidaySchedule.fromDate + '至' + holidaySchedule.toDate;
                    count = self.getHolidayCount(holidaySchedule.fromDate, holidaySchedule.toDate);
                  }
                  var thisYear = new Date().getFullYear();
                  var isThisYear = date.indexOf(thisYear) > -1;
                  html +=
                    '<div class="holiday-content-item" data-uuid="" data-holiday="' +
                    uuid +
                    '">' +
                    '<div class="holiday-item-time">' +
                    '<i class="time-yuandian"></i>' +
                    '<span class="time-date">' +
                    (isThisYear ? date.substr(5) : date) +
                    '</span>' +
                    '<span class="time-text ' +
                    (isThisYear ? '' : 'otherYear') +
                    '">' +
                    hName +
                    '</span>' +
                    '</div>' +
                    '<div class="holiday-item-detail">' +
                    '<div class="holiday-detail-header">' +
                    '<span class="holiday-header-text">' +
                    '<i class="iconfont icon-ptkj-shixinjiantou-xia holiday-header-collpase"></i><span class="holiday-date">' +
                    name +
                    '</span>放假，共<span class="holiday-count">' +
                    count +
                    '</span>天。';
                  if (holidaySchedule.makeupDate != null && holidaySchedule.makeupDate != '') {
                    var makeupDate = holidaySchedule.makeupDate.split(';');
                    var buTitle = [];
                    $.each(makeupDate, function (dIndex, dItem) {
                      buTitle.push(dItem.split('|')[0]);
                    });
                    html += buTitle.join('、') + '补班。';
                  }
                  html +=
                    '</span>' +
                    '<span class="holiday-header-btn">' +
                    '<i class="iconfont icon-ptkj-shanchu"></i>删除' +
                    '</span>' +
                    '</div>' +
                    '<div class="holiday-detail-body">';
                  if (autoUpdate) {
                    html +=
                      '<div class="holiday-body-row">' +
                      '<span class="holiday-row-label">自动更新假期</span>' +
                      '<div class="holiday-row-content">' +
                      '<div class="autoUpdate switch-wrap ' +
                      (holidaySchedule.holidayUuid != undefined ? 'active' : '') +
                      '"><span class="switch-radio"></span></div>' +
                      '</div>' +
                      '</div>';
                  }
                  var isActive = autoUpdate && holidaySchedule.holidayUuid != undefined ? 'disabled' : '';
                  var hDay =
                    autoUpdate && holidaySchedule.fromDate != undefined ? holidaySchedule.fromDate + ' 至 ' + holidaySchedule.toDate : ''; // 假期起止日
                  var remark = autoUpdate && holidaySchedule.remark != undefined ? holidaySchedule.remark : ''; // beizhu
                  var disableClass = autoUpdate && holidaySchedule.holidayUuid != undefined ? 'isDisabled' : '';
                  html +=
                    '<div class="holiday-body-row">' +
                    '<span class="holiday-row-label">' +
                    '<font style="color:#f00;">*</font>假期起止日期：</span>' +
                    '<div class="holiday-row-content">' +
                    '<input type="text" name="date" id="date' +
                    uuid +
                    '" value="' +
                    hDay +
                    '" autocomplete="off" ' +
                    isActive +
                    '>' +
                    '</div>' +
                    '</div>' +
                    '<div class="holiday-body-row">' +
                    '<span class="holiday-row-label">补班日期：</span>' +
                    '<div class="holiday-row-content">';
                  if (autoUpdate && holidaySchedule.makeupDate != null && holidaySchedule.makeupDate != undefined) {
                    var makeupDate = holidaySchedule.makeupDate.split(';');
                    $.each(makeupDate, function (mIndex, mItem) {
                      var buDate = mItem.split('|')[0];
                      var buDay = mItem.split('|')[1];
                      html +=
                        '<div class="holiday-row-days" data-index="' +
                        mIndex +
                        '">' +
                        '<input class="form-control" type="text" name="buDate" id="buDate' +
                        uuid +
                        mIndex +
                        '" autocomplete="off" ' +
                        isActive +
                        ' value="' +
                        buDate +
                        '">' +
                        '补&nbsp;&nbsp;&nbsp;<input type="text" name="buDay" id="buDay' +
                        uuid +
                        mIndex +
                        '" autocomplete="off" ' +
                        isActive +
                        ' value="' +
                        buDay +
                        '">' +
                        '<i class="iconfont icon-ptkj-jianhao cutDate ' +
                        disableClass +
                        '" ' +
                        isActive +
                        '></i>' +
                        '</div>';
                    });
                  } else if (holidaySchedule.makeupDate != null && holidaySchedule.makeupDate != undefined) {
                    html +=
                      '<div class="holiday-row-days" data-index="1">' +
                      '<input class="form-control" type="text" name="buDate" id="buDate' +
                      uuid +
                      '1" autocomplete="off" ' +
                      isActive +
                      '>' +
                      '补&nbsp;&nbsp;&nbsp;<input type="text" name="buDay" id="buDay' +
                      uuid +
                      '1" autocomplete="off" ' +
                      isActive +
                      '>' +
                      '<i class="iconfont icon-ptkj-jianhao cutDate" ' +
                      isActive +
                      '></i>' +
                      '</div>';
                  }
                  html +=
                    '<div class="mt10">' +
                    '<button type="button" class="well-btn w-btn-primary well-btn-sm w-noLine-btn addDate" ' +
                    isActive +
                    '>' +
                    '<i class="iconfont icon-ptkj-jiahao"></i>添加补班日期</button>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '<div class="holiday-body-row">' +
                    '<span class="holiday-row-label">备注：</span>' +
                    '<div class="holiday-row-content">' +
                    '<textarea type="text" name="remark" ' +
                    isActive +
                    '>' +
                    remark +
                    '</textarea>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '</div>';
                }
              });
              $('.holiday-content', $element).append(html);
              var date = $("input[name='date']", $element);
              var buDate = $("input[name='buDate']", $element);
              var buDay = $("input[name='buDay']", $element);
              $.each(date, function (index, item) {
                // 假期起止日期
                var id = $(item).attr('id');
                laydate.render({
                  elem: '#' + id,
                  format: 'yyyy-MM-dd',
                  trigger: 'click',
                  range: '至',
                  type: 'date',
                  done: function (value) {
                    $('#' + id)
                      .parents('.holiday-item-detail')
                      .find('.holiday-date')
                      .text(value);
                    if (value != '') {
                      var d1 = $.trim(value.split('至')[0]);
                      var d2 = $.trim(value.split('至')[1]);
                      var count = self.getHolidayCount(d1, d2);
                      $('#' + id)
                        .parents('.holiday-item-detail')
                        .find('.holiday-count')
                        .text(count);
                    }
                  }
                });
              });

              $.each(buDate, function (index, item) {
                // 补班日期
                var id = $(item).attr('id');
                laydate.render({
                  elem: '#' + id,
                  format: 'yyyy-MM-dd',
                  trigger: 'click',
                  type: 'date'
                });
              });

              $.each(buDay, function (index, item) {
                // 补
                var id = $(item).attr('id');
                laydate.render({
                  elem: '#' + id,
                  format: 'yyyy-MM-dd',
                  trigger: 'click',
                  type: 'date'
                });
              });

              $element.off('click', '.autoUpdate').on('click', '.autoUpdate', function () {
                var hasActive = $(this).hasClass('active');
                $(this)[hasActive ? 'removeClass' : 'addClass']('active');
                if (hasActive) {
                  $(this).parents('.holiday-content-item').find('input').removeAttr('disabled');
                  $(this).parents('.holiday-content-item').find('textarea').removeAttr('disabled');
                  $(this).parents('.holiday-content-item').find('button').removeAttr('disabled');
                  $(this).parents('.holiday-content-item').find('.cutDate').removeClass('isDisabled');
                } else {
                  $(this).parents('.holiday-content-item').find('input').attr('disabled', true);
                  $(this).parents('.holiday-content-item').find('textarea').attr('disabled', true);
                  $(this).parents('.holiday-content-item').find('button').attr('disabled', true);
                  $(this).parents('.holiday-content-item').find('.cutDate').addClass('isDisabled');
                }
              });
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default'
          }
        }
      });
    },
    getHolidayHtml: function () {
      var html = '';
      html +=
        '<div class="holiday-wrapper">' +
        '<div class="holiday-wrapper-left">' +
        '<div class="holiday-left-search"><div><input type="text" id="name" name="name"><i class="iconfont icon-ptkj-dacha input-close"></i><i class="iconfont icon-ptkj-sousuochaxun input-search"></i></div></div>' + // 搜索
        '<div class="holiday-left-tag">' +
        '<div class="tag-list"></div>' +
        '<i class="iconfont icon-ptkj-shaixuan tag-icon"></i>' +
        '<div class="holiday-type-box">' +
        '<div class="holiday-type-list">' +
        '<div class="holiday-type-item">' +
        '<div class="title">节假日类型</div>' +
        '<div class="list"></div>' +
        '</div>' +
        '</div>' +
        '<div class="holiday-type-btn">' +
        '<button type="button" class="well-btn w-btn-primary btn-sure">确定</button>' +
        '<button type="button" class="btn btn-default btn-cancel" style="margin-right:10px;">取消</button>' +
        '<button type="button" class="btn btn-default btn-reset">重置</button>' +
        '</div>' +
        '</div>' +
        '</div>' + // 标签
        '<div class="holiday-left-table"><table id="holidayTable"></table></div>' + // 表格
        '</div>' +
        '<div class="holiday-wrapper-right">' +
        '<div class="holiday-right-title">已选择<span class="holidayCount">0</span>项</div>' + // 标题
        '<div class="holiday-right-list">' +
        '</div>' + // 已选择的
        '</div>' +
        '</div>';
      return html;
    },
    addDate: function ($this) {
      var days = $this.parents('.holiday-row-content').first().find('.holiday-row-days');
      var uuid = $this.parents('.holiday-content-item').data('holiday');
      var idIndex = 0;
      $.each(days, function (index, item) {
        var dataIndex = $(item).data('index');
        idIndex = dataIndex - idIndex > 0 ? dataIndex : idIndex;
      });
      ++idIndex;
      var html =
        '<div class="holiday-row-days" data-index="' +
        idIndex +
        '">' +
        '<input class="form-control" type="text" name="buDate" id="buDate' +
        uuid +
        idIndex +
        '" autocomplete="off">' +
        '补&nbsp;&nbsp;&nbsp;<input type="text" name="buDay" id="buDay' +
        uuid +
        idIndex +
        '" autocomplete="off">' +
        '<i class="iconfont icon-ptkj-jianhao cutDate"></i>' +
        '</div>';
      $this.parent().before(html);

      laydate.render({
        elem: '#buDate' + uuid + idIndex,
        format: 'yyyy-MM-dd',
        trigger: 'click',
        type: 'date'
      });

      laydate.render({
        elem: '#buDay' + uuid + idIndex,
        format: 'yyyy-MM-dd',
        trigger: 'click',
        type: 'date'
      });
    },
    holidayCollpase: function ($this) {
      if ($this.hasClass('icon-ptkj-shixinjiantou-you')) {
        $this.removeClass('icon-ptkj-shixinjiantou-you').addClass('icon-ptkj-shixinjiantou-xia');
        $this.parents('.holiday-detail-header').next().slideDown();
      } else {
        $this.removeClass('icon-ptkj-shixinjiantou-xia').addClass('icon-ptkj-shixinjiantou-you');
        $this.parents('.holiday-detail-header').next().slideUp();
      }
    }
  };
  window.HolidayArragement = HolidayArragement;
  return HolidayArragement;
});
