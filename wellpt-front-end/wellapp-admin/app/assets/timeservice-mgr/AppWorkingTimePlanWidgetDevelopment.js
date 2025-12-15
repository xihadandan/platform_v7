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

  // 平台应用_基础数据管理_工作时间方案_详情二开
  var AppWorkingTimePlanWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppWorkingTimePlanWidgetDevelopment, HtmlWidgetDevelopment, {
    // 初始化回调
    init: function () {
      var self = this;
      var $element = this.widget.element;
      $element.parent().css({
        padding: 0
      });
      var bean = {
        uuid: null,
        name: null,
        code: null,
        holidaySchedule: {},
        workTimeSchedule: {}
      };
      var periodType = { 1: '全年', 2: '指定时间周期' };
      var workTimeType = { 1: '固定工时', 2: '单双周', 3: '弹性工时' };
      var workDay = { MON: '周一', TUE: '周二', WED: '周三', THU: '周四', FRI: '周五', SAT: '周六', SUN: '周日' };

      var uuid = GetRequestParam().uuid || this.getWidgetParams().uuid;
      var isCheck = GetRequestParam().isCheck || this.getWidgetParams().isCheck;
      var isHistory = GetRequestParam().isHistory || this.getWidgetParams().isHistory;
      if (uuid) {
        // var utl = '/api/ts/work/time/plan/history/get';
        getWorkPlanDetail(uuid, self, isHistory);
      } else {
        $('.work-header-text', $element).html('新建工作时间方案');
        $('.btn-save-plan-version', $element).hide();
        $('.btn-check-version', $element).hide();
        $('.btn-history-time', $element).hide();
      }

      // 获取当前年的节假日安排
      $.ajax({
        url: ctx + '/api/ts/holiday/schedule/listByYear',
        type: 'get',
        dataType: 'json',
        data: { year: new Date().getFullYear() },
        async: false,
        success: function (res) {
          if (res.code == 0) {
            self.yearHolidaySchedule = _.sortBy(res.data, function (item) {
              return item.holidayInstanceDate;
            });
          }
        }
      });

      // 添加工作时间安排
      $('.addWorkTimePlan', $element)
        .off()
        .on('click', function () {
          addWorkTimePlan('添加工作时间安排', 'add');
        });

      // 设置工作时间安排
      $element.off('click', '.btn-plan-set').on('click', '.btn-plan-set', function () {
        var obj = $(this).parents('.work-time-item').data('obj');
        var index = $(this).parents('.work-time-item').data('index');
        addWorkTimePlan('工作时间安排', 'set', obj, index);
      });

      // 删除工作时间安排
      $element.off('click', '.btn-plan-del').on('click', '.btn-plan-del', function () {
        $(this).parents('.work-time-item').remove();
      });

      $element //减去补班
        .off('click', '.cutDate')
        .on('click', '.cutDate', function () {
          if ($(this).hasClass('isDisabled')) {
            return false;
          }
          $(this).parent().next('.error').remove();
          $(this).parent().remove();
        });

      $($element) // 删除
        .off('click', '.holiday-header-btn')
        .on('click', '.holiday-header-btn', function () {
          var uuid = $(this).parents('.holiday-content-item').first().data('uuid');
          $(this).parents('.holiday-content-item').remove();
        });

      $element // 添加补班
        .off('click', '.addDate')
        .on('click', '.addDate', function () {
          HolidayArragement.addDate($(this));
        });

      $element // 展开、折叠
        .off('click', '.holiday-header-collpase')
        .on('click', '.holiday-header-collpase', function () {
          HolidayArragement.holidayCollpase($(this));
        });

      $('.addHoliday', $element) //  添加节假日
        .off()
        .on('click', function () {
          var year = new Date().getFullYear();
          HolidayArragement.getHolidays($element, year, true, self.yearHolidaySchedule);
        });

      // 保存
      $('.btn-save-plan', $element)
        .off()
        .on('click', function () {
          if (checkHolidays()) {
            $.ajax({
              type: 'post',
              url: ctx + '/api/ts/work/time/plan/save',
              dataType: 'json',
              contentType: 'application/json',
              data: JSON.stringify(bean),
              success: function (res) {
                if (res.code == 0) {
                  appModal.success('保存成功！', function () {
                    if (!bean.uuid) {
                      appContext.getWindowManager().refreshParent();
                      appContext.getWindowManager().close();
                    } else {
                      window.location.reload();
                    }
                  });
                } else {
                  appModal.error(res.msg);
                }
              }
            });
          }
        });

      // 保存新版本
      $('.btn-save-plan-version', $element)
        .off()
        .on('click', function () {
          if (checkHolidays()) {
            var html = getNewVersionHtml();
            var $newVersionDialog = appModal.dialog({
              title: '保存新版本',
              message: html,
              shown: function () {
                $.ajax({
                  url: ctx + '/api/ts/work/time/plan/getMaxVersionByUuid',
                  type: 'get',
                  data: { uuid: bean.uuid },
                  success: function (res) {
                    var version = (Number(res.data) + 0.1).toFixed(1);
                    $('.work-version', $newVersionDialog).text(version);
                  }
                });

                $("input[name='activeRightNow']", $newVersionDialog)
                  .off()
                  .on('click', function () {
                    var val = $(this).val();
                    $('.activeTimeBox', $newVersionDialog)[val == '1' ? 'hide' : 'show']();
                    if (val == '2') {
                      $.ajax({
                        type: 'get',
                        url: ctx + '/api/ts/work/time/plan/getSysDate',
                        dataType: 'json',
                        success: function (res) {
                          if (res.code == 0) {
                            $('.activeTimeBox div', $newVersionDialog).empty();
                            $('.activeTimeBox div', $newVersionDialog).append(
                              '<input type="text" class="form-control" id="activeTime" name="activeTime" placeholder="请选择" style="width:300px;">'
                            );
                            laydate.render({
                              elem: '#activeTime',
                              format: 'yyyy-MM-dd HH:mm',
                              trigger: 'click',
                              type: 'datetime',
                              min: res.data
                            });
                          }
                        }
                      });
                    }
                  });
              },
              buttons: {
                ok: {
                  label: '确定',
                  className: 'well-btn w-btn-primary',
                  callback: function () {
                    bean.activeRightNow = $("input[name='activeRightNow']:checked").val() == '2' ? false : true;
                    bean.version = $('.work-version', $newVersionDialog).text();
                    if (!bean.activeRightNow) {
                      var activeTime = $('#activeTime', $newVersionDialog).val();
                      if (activeTime == '') {
                        appModal.error('生效时间不能为空！');
                        return false;
                      }
                      bean.activeTime = activeTime + ':00';
                    } else {
                      var date = new Date();
                      var year = date.getFullYear();
                      var mon = date.getMonth() + 1;
                      var day = date.getDate();
                      var hh = date.getHours();
                      var min = date.getMinutes();
                      bean.activeTime =
                        year + '-' + changeDate(mon) + '-' + changeDate(day) + ' ' + changeDate(hh) + ':' + changeDate(min) + ':' + '00';
                    }

                    $.ajax({
                      type: 'post',
                      url: ctx + '/api/ts/work/time/plan/saveAsNewVersion',
                      dataType: 'json',
                      contentType: 'application/json',
                      data: JSON.stringify(bean),
                      success: function (res) {
                        if (res.code == 0) {
                          appContext.getWindowManager().refreshParent();
                          appModal.success('保存成功！');

                          location.href =
                            ctx +
                            '/web/app/pt-mgr/pt-basicdata-mgr/app_20210531101501.html?pageUuid=8b45b90c-d728-4144-b922-f4bb8319eb5a&uuid=' +
                            res.data +
                            '&tableBtnEditTag=' +
                            res.data;
                        } else {
                          appModal.error(res.msg);
                        }
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
          }
        });

      // 查看版本历史
      $('.btn-check-version', $element)
        .off()
        .on('click', function () {
          var url =
            ctx + '/web/app/page/preview/288d1201a496d18781ba2f54675dc58b?pageUuid=4cc4cc95-3a64-438e-8f6e-8b78645f4f68&id=' + bean.id;
          var iframe =
            '<div class="embed-responsive embed-responsive-4by3"><iframe src="' +
            url +
            '" class="embed-responsive-item" style="width:100%;"></iframe></div>';

          appModal.dialog({
            title: '版本历史',
            message: iframe,
            size: 'large',
            buttons: {
              close: {
                label: '关闭',
                className: 'btn btn-default'
              }
            }
          });
        });

      // 历史工作时间
      $('.btn-history-time', $element)
        .off()
        .on('click', function () {
          var url =
            ctx + '/web/app/page/preview/288d1201a496d18781ba2f54675dc58b?pageUuid=2a322980-ffc2-45e4-b792-6e78ddec6f0f&uuid=' + bean.uuid;
          var iframe =
            '<div class="embed-responsive embed-responsive-4by3"><iframe src="' +
            url +
            '" class="embed-responsive-item" style="width:100%;"></iframe></div>';

          appModal.dialog({
            title: '历史工作时间',
            message: iframe,
            size: 'large',
            buttons: {
              close: {
                label: '关闭',
                className: 'btn btn-default'
              }
            }
          });
        });

      function changeDate(val) {
        return val > 9 ? val : '0' + val;
      }

      function checkHolidays() {
        $('#workingTimePlan', $element).form2json(bean);
        var checkRes = true;
        var tips = [];
        if (bean.name == '') {
          tips.push('名称不能为空！');
          checkRes = false;
        }
        var workTimeSchedule = [];
        var workList = $('.work-time-item', $element);
        var timeList = [];
        if (workList.length == 0) {
          tips.push('工作时间的应用范围设置不合理，请检查！');
          checkRes = false;
        } else {
          for (var i = 0; i < workList.length; i++) {
            var wItem = workList[i];
            var obj = $(wItem).data('obj');

            if (obj.periodType == '1' && (workList.length > 1 || obj.fromDate != '01-01' || obj.toDate != '12-31')) {
              checkRes = false;
              tips.push('工作时间的应用范围设置不合理，请检查！');
              break;
            } else if (obj.periodType == '2') {
              if (obj.toNextYeay && !_.gt(obj.fromDate, obj.toDate)) {
                checkRes = false;
                tips.push('工作时间的应用范围设置不合理，请检查！');
                timeList = [];
                break;
              }
              timeList.push({
                fromDate: obj.fromDate,
                toDate: obj.toDate,
                toNextYeay: obj.toNextYeay
              });
            }
            $.each(obj.workTimes, function (iIndex, iItem) {
              delete iItem.workDayText;
              delete iItem.timeText;
            });

            workTimeSchedule.push(obj);
          }
          timeList = _.sortBy(timeList, function (o) {
            return o.fromDate; // 按开始时间升序
          });

          if (timeList.length > 1) {
            var year = new Date().getFullYear();
            for (var k = 0; k < timeList.length; k++) {
              var curDate = Date.parse(year + '-' + timeList[k].toDate);
              if (k == timeList.length - 1) {
                if (timeList[k].toDate == '12-31' && timeList[0].fromDate == '01-01') {
                  continue;
                } else {
                  var nextDate = Date.parse(year + '-' + timeList[0].fromDate);
                  if ((nextDate - curDate) / 24 / 60 / 60 / 1000 != 1) {
                    checkRes = false;
                    tips.push('工作时间的应用范围设置不合理，请检查！');
                    break;
                  }
                }
              } else {
                if (timeList[k].toNextYeay) {
                  checkRes = false;
                  tips.push('工作时间的应用范围设置不合理，请检查！');
                  break;
                } else {
                  var nextDate = Date.parse(year + '-' + timeList[k + 1].fromDate);
                  if (timeList[k + 1].toNextYeay) {
                    var date0 = Date.parse(year + '-' + timeList[0].fromDate);
                    var date1 = Date.parse(year + '-' + timeList[k + 1].toDate);
                    var next0 = Date.parse(year + '-' + timeList[k + 1].fromDate);
                    var next1 = Date.parse(year + '-' + timeList[k].toDate);
                    if ((date0 - date1) / 24 / 60 / 60 / 1000 != 1 || (next0 - next1) / 24 / 60 / 60 / 1000 != 1) {
                      checkRes = false;
                      tips.push('工作时间的应用范围设置不合理，请检查！');
                      break;
                    }
                  } else {
                    if ((nextDate - curDate) / 24 / 60 / 60 / 1000 != 1) {
                      checkRes = false;
                      tips.push('工作时间的应用范围设置不合理，请检查！');
                      break;
                    }
                  }
                }
              }
            }
          } else if (timeList.length == 1) {
            var tDate = Date.parse(year + '-' + timeList[0].toDate);
            var fDate = Date.parse(year + '-' + timeList[0].fromDate);
            var type1 = !obj.toNextYeay && (obj.fromDate != '01-01' || obj.toDate != '12-31'); // 不+1,则开始是0101且结束是1231
            var type2 = obj.toNextYeay && (fDate - tDate) / 24 / 60 / 60 / 1000 != 1; // +1，开始等于结束+1天
            if (type1 || type2) {
              checkRes = false;
              tips.push('工作时间的应用范围设置不合理，请检查！');
            }
          }
        }

        var holidaySchedule = [];
        var $items = $element.find('.holiday-content-item');
        var isError = true;
        $.each($items, function (index, item) {
          var obj = {};
          var buDates = []; // 补班时间
          var date = $(item).find("input[name='date']").val();
          obj.holidayInstanceDate = $(item).find('.time-date').text();
          if (obj.holidayInstanceDate.indexOf(new Date().getFullYear()) == -1) {
            obj.holidayInstanceDate = new Date().getFullYear() + '-' + obj.holidayInstanceDate;
          }
          obj.holidayUuid = $(item).data('holiday');
          obj.holidayName = $(item).find('.time-text').text();
          obj.year = new Date().getFullYear();
          if (date != '') {
            obj.fromDate = $.trim(date.split('至')[0]);
            obj.toDate = $.trim(date.split('至')[1]);
            if (Date.parse(obj.toDate) - Date.parse(obj.fromDate) < 0) {
              tips.push('结束日期需大于等于开始日期！');
              isError = false;
            }
          } else {
            tips.push('假期起止日期不能为空！');
            isError = false;
          }
          var $rows = $(item).find('.holiday-row-days');
          $.each($rows, function (rIndex, rItem) {
            var buDate = $(rItem).find("input[name='buDate']").val();
            var buDay = $(rItem).find("input[name='buDay']").val();
            if (buDate == '' || buDay == '') {
              tips.push('补班时间不能为空！');
              isError = false;
            } else {
              buDates.push(buDate + '|' + buDay);
            }
          });
          if (!isError) {
            checkRes = isError;
            return false;
          }
          obj.autoUpdate = $(item).find('.switch-wrap').hasClass('active');
          obj.remark = $(item).find('textarea[name="remark"]').val();
          obj.makeupDate = buDates.join(';');
          holidaySchedule.push(obj);
        });

        if (tips.length > 0) {
          appModal.error(tips.join(''));
        }

        bean.workTimeSchedule = JSON.stringify(workTimeSchedule);
        bean.holidaySchedule = JSON.stringify(holidaySchedule);
        return checkRes;
      }

      //工作时间安排弹窗
      function addWorkTimePlan(title, status, obj, pIndex) {
        var html = getWorkPlanHtml();
        var $addPlanDialog = appModal.dialog({
          title: title,
          message: html,
          size: 'large',
          height: '700',
          width: '1100',
          zIndex: 900,
          shown: function () {
            // 时间周期
            var currYear = new Date().getFullYear();
            laydate.render({
              elem: '#periodDateStart',
              format: 'MM-dd',
              trigger: 'click',
              type: 'date',
              min: currYear + '-01-01',
              max: currYear + '-12-31'
            });
            laydate.render({
              elem: '#periodDateEnd',
              format: 'MM-dd',
              trigger: 'click',
              type: 'date',
              min: currYear + '-01-01',
              max: currYear + '-12-31'
            });

            // 应用时间周期
            $("input[name='periodType']", $addPlanDialog)
              .off()
              .on('change', function () {
                var val = $(this).val();
                $('.showTimeRange', $addPlanDialog)[val == '1' ? 'hide' : 'show']();
              });
            // 类型
            $("input[name='workTimeType']", $addPlanDialog)
              .off()
              .on('change', function () {
                var val = $(this).val();
                $('.workTime', $addPlanDialog).hide();
                $('.workTime' + val, $addPlanDialog).show();
              });

            $('#coreWorkDay', $addPlanDialog)
              .off()
              .on('change', function () {
                var isChecked = $(this).prop('checked');
                $(this).parents('.work-time-flex').next()[isChecked ? 'show' : 'hide']();
              });

            // 添加工作时间
            $('.btn-add-holiday', $addPlanDialog)
              .off()
              .on('click', function () {
                var index = $(this).prev().data('index');
                var obj = {
                  status: 'add',
                  workTimeType: $("input[name='workTimeType']:checked", $addPlanDialog).val()
                };
                addWorkTime($addPlanDialog, '添加工作时间', index, obj);
              });

            // 设置工作时间
            $addPlanDialog.off('click', '.btn-time-set').on('click', '.btn-time-set', function () {
              var index = $(this).parents('.work-time-body').data('index');
              var data = $(this).parents('tr').data('data');
              data.trIndex = $(this).parents('tr').index();
              data.status = 'set';
              data.workTimeType = $("input[name='workTimeType']:checked", $addPlanDialog).val();
              addWorkTime($addPlanDialog, '工作时间', index, data);
            });

            //删除工作时间
            $addPlanDialog.off('click', '.btn-time-del').on('click', '.btn-time-del', function () {
              $(this).parents('tr').remove();
            });

            if (obj) {
              $("input[name='periodType'][value='" + obj.periodType + "']")
                .prop('checked', true)
                .trigger('change');
              // var newDate = obj.fromDate + ' 至 ' + obj.toDate;
              $("input[name='periodDateStart']").val(obj.fromDate);
              $("input[name='periodDateEnd']").val(obj.toDate);
              $("input[name='workTimeType'][value='" + obj.workTimeType + "']")
                .prop('checked', true)
                .trigger('change');

              $('#toNextYeay', $addPlanDialog).prop('checked', obj.toNextYeay).trigger('change');
              $('#coreWorkDay', $addPlanDialog).prop('checked', obj.coreWorkDay).trigger('change');
              if (obj.workTimeType == 3) {
                $("input[name='workHoursPerWeek']", $addPlanDialog).val(obj.workHoursPerWeek);
              }

              var odd = [];
              var even = [];
              var oddHours = 0;
              var evenHours = 0;
              $.each(obj.workTimes, function (wIndex, wItem) {
                var $tr = renderTimeHtml(wItem);
                if (wItem.oddWeeks) {
                  odd.push($tr);
                  oddHours = (Number(oddHours) + wItem.workDay.split(';').length * Number(wItem.workHoursPerDay)).toFixed(2);
                } else {
                  even.push($tr);
                  evenHours = (Number(evenHours) + wItem.workDay.split(';').length * Number(wItem.workHoursPerDay)).toFixed(2);
                }
              });

              if (odd.length > 0) {
                $addPlanDialog
                  .find('.workTime' + obj.workTimeType)
                  .find('.work-time-body:eq(1)')
                  .find('tbody')
                  .append(even.join(''));
                $addPlanDialog
                  .find('.workTime' + obj.workTimeType)
                  .find('.title:eq(1)')
                  .find('span')
                  .text(evenHours);
                $addPlanDialog
                  .find('.workTime' + obj.workTimeType)
                  .find('.work-time-body:eq(0)')
                  .find('tbody')
                  .append(odd.join(''));
                $addPlanDialog
                  .find('.workTime' + obj.workTimeType)
                  .find('.title:eq(0)')
                  .find('span')
                  .text(oddHours);
              } else {
                $addPlanDialog
                  .find('.workTime' + obj.workTimeType)
                  .find('.work-time-body:eq(0)')
                  .find('tbody')
                  .append(even.join(''));
                $addPlanDialog
                  .find('.workTime' + obj.workTimeType)
                  .find('.title:eq(0)')
                  .find('span')
                  .text(evenHours);
              }
            }
          },
          buttons: {
            ok: {
              label: '确定',
              className: 'well-btn w-btn-primary',
              callback: function () {
                var obj = {};
                obj.periodType = $("input[name='periodType']:checked", $addPlanDialog).val();
                obj.toNextYeay = $("input[name='toNextYeay']", $addPlanDialog).prop('checked');
                obj.workTimeType = $("input[name='workTimeType']:checked", $addPlanDialog).val();

                if (obj.periodType == '1') {
                  obj.fromDate = '01-01';
                  obj.toDate = '12-31';
                } else {
                  var fromDate = $("input[name='periodDateStart']", $addPlanDialog).val();
                  var toDate = $("input[name='periodDateEnd']", $addPlanDialog).val();
                  obj.fromDate = fromDate;
                  obj.toDate = toDate;
                  if (fromDate == '' || toDate == '') {
                    appModal.error('时间周期不能为空！');
                    return false;
                  }

                  if (!obj.toNextYeay && _.gt(fromDate, toDate)) {
                    appModal.error('结束时间应大于开始时间！');
                    return false;
                  }
                }

                obj.workHoursPerWeek =
                  obj.workTimeType == '3'
                    ? $("input[name='workHoursPerWeek']", $addPlanDialog).val()
                    : $('.workTime' + obj.workTimeType, $addPlanDialog)
                        .find('.title span')
                        .text();
                obj.coreWorkDay = obj.workTimeType == '3' ? $("input[name='coreWorkDay']", $addPlanDialog).prop('checked') : false;
                var $table = $('.workTime' + obj.workTimeType, $addPlanDialog).find('.work-time-body');
                obj.workTimes = [];

                var totalHour = 0;

                $.each($table, function (index, item) {
                  var $tr = $(item).find('tbody tr');
                  var oddWeeks = false;
                  if (obj.workTimeType == '2' && index == 0) {
                    oddWeeks = true;
                  }
                  $.each($tr, function (rIndex, rItem) {
                    var data = $(rItem).data('data');
                    if (data) {
                      data.oddWeeks = oddWeeks;
                      obj.workTimes.push(data);
                      totalHour = totalHour - 0 + data.workHoursPerDay * data.workDay.split(';').length;
                    }
                  });
                });

                if ((obj.workTimeType != '3' || (obj.workTimeType == '3' && obj.coreWorkDay)) && obj.workTimes.length == 0) {
                  appModal.error('请设置周工作时间！');
                  return false;
                }

                if (obj.workTimeType == '3') {
                  if (obj.workHoursPerWeek == '') {
                    appModal.error('每周工作时长不能为空！');
                    return false;
                  } else if (obj.workHoursPerWeek - 0 < 0) {
                    appModal.error('每周工作时长不能为负数！');
                    return false;
                  }
                  if (obj.coreWorkDay && obj.workHoursPerWeek - totalHour < 0) {
                    appModal.error('核心工作日时长超出每周工作日时长！');
                    return false;
                  }
                }

                if (status == 'add') {
                  var workTimeHtml = renderWorkTimHtml([obj]);
                  $('.holiday_arrangement_list', $element).append(workTimeHtml);
                } else {
                  var workTimeHtml = $(renderWorkTimHtml([obj]));
                  $('.holiday_arrangement_list', $element)
                    .find(".work-time-item[data-index='" + pIndex + "']")
                    .html(workTimeHtml.html())
                    .data('obj', obj);
                }
              }
            },
            cancel: {
              label: '取消',
              className: 'btn-default'
            }
          }
        });
      }

      // 工作时间弹窗
      function addWorkTime($addPlanDialog, title, index, data) {
        var html = getWorkTimeHtml();
        var $addTimeDialog = appModal.dialog({
          title: title,
          message: html,
          size: 'large',
          height: '600',
          zIndex: 1000,
          shown: function () {
            var dateRange = $("input[name='dateRange']", $addTimeDialog);
            $.each(dateRange, function (index, item) {
              var id = $(item).attr('id');
              laydate.render({
                elem: '#' + id,
                format: 'HH:mm',
                trigger: 'click',
                type: 'time',
                range: '至'
              });
            });

            $("input[name='type']", $addTimeDialog)
              .off()
              .on('change', function () {
                var val = $(this).val();
                $('.type', $addTimeDialog).hide();
                $('.type' + val, $addTimeDialog).show();
              });

            $('#coreTime', $addTimeDialog)
              .off()
              .on('change', function () {
                var checked = $(this).prop('checked');
                $('.type3', $addTimeDialog)[checked ? 'show' : 'hide']();
              });

            $('.btn-add-time', $addTimeDialog) // 添加
              .off()
              .on('click', function () {
                var type = $("input[name='type']:checked", $addTimeDialog).val();
                var id = type == '1' ? 'dateRange' : 'dateRanges';
                var index =
                  $('.time-wrapper' + type, $addTimeDialog)
                    .find('.time-content')
                    .last()
                    .data('index') || 0;
                var count = index + 1;
                var timeHtml =
                  '<div class="time-line time-content" data-index="' +
                  count +
                  '">' +
                  '<i class="time-drag iconfont icon-wsbs-liebiaoshitu"></i>' +
                  '<input type="text" name="name" id="name' +
                  count +
                  '" class="time-text-input">' +
                  '<input type="text" name="dateRange" id="' +
                  id +
                  count +
                  '" class="time-date">' +
                  '<div class="time-day"><input type="checkbox" name="toNextDay" id="toNextDay' +
                  count +
                  '"><label for="toNextDay' +
                  count +
                  '">+1天</label></div>' +
                  '<i class="iconfont icon-ptkj-shanchu time-delete"></i>' +
                  '</div>';
                $('.time-wrapper' + type, $addTimeDialog).append(timeHtml);

                laydate.render({
                  elem: '#' + id + count,
                  format: 'HH:mm',
                  trigger: 'click',
                  type: 'time',
                  range: '至'
                });
              });

            $addTimeDialog.off('click', '.time-delete').on('click', '.time-delete', function () {
              // 删除
              $(this).parent().remove();
            });

            setWeekDayDisable($addPlanDialog, $addTimeDialog, data, index, $("input[name='workTimeType']:checked", $addPlanDialog).val());

            if (data && data.status != 'add') {
              var day = data.workDay.split(';');
              $.each(day, function (i, item) {
                $("input[name='workDay'][value='" + item + "']").prop('checked', true);
              });

              $("input[name='type'][value='" + data.type + "']", $addTimeDialog)
                .prop('checked', true)
                .trigger('change');

              var type = data.type;
              $('.time-wrapper' + type, $addTimeDialog).empty();

              $.each(data.timePeriods, function (i, item) {
                var timeHtml = '';
                var id = type == '1' ? 'dateRange' : 'dateRanges';
                var index =
                  $('.time-wrapper' + type, $addTimeDialog)
                    .find('.time-content')
                    .last()
                    .data('index') || 0;
                var count = index + 1;
                var dateRange = item.fromTime + ' 至 ' + item.toTime;
                var toNextDay = item.toNextDay ? 'checked' : '';
                timeHtml +=
                  '<div class="time-line time-content" data-index="' +
                  count +
                  '">' +
                  '<i class="time-drag iconfont icon-wsbs-liebiaoshitu"></i>' +
                  '<input type="text" name="name" id="name' +
                  count +
                  '" class="time-text-input" value="' +
                  item.name +
                  '">' +
                  '<input type="text" name="dateRange" id="' +
                  id +
                  count +
                  '" class="time-date" value="' +
                  dateRange +
                  '">' +
                  '<div class="time-day"><input type="checkbox" name="toNextDay" id="toNextDay' +
                  count +
                  '" ' +
                  toNextDay +
                  '><label for="toNextDay' +
                  count +
                  '">+1天</label></div>' +
                  '<i class="iconfont icon-ptkj-shanchu time-delete"></i>' +
                  '</div>';

                $('.time-wrapper' + type, $addTimeDialog).append(timeHtml);

                laydate.render({
                  elem: '#' + id + count,
                  format: 'HH:mm',
                  trigger: 'click',
                  type: 'time',
                  range: '至'
                });
              });

              if (type == '2') {
                $("input[name='workTimeHours']", $addTimeDialog).val(data.workHoursPerDay);
                $('#coreTime', $addTimeDialog).prop('checked', data.coreWorkPeriod).trigger('change');
              }
            }
          },
          buttons: {
            ok: {
              label: '确定',
              className: 'well-btn w-btn-primary',
              callback: function () {
                var obj = {};
                var $workDay = $("input[name='workDay']", $addTimeDialog);
                var workDay = [];
                var workDayText = [];
                $.each($workDay, function (index, item) {
                  if ($(item).prop('checked')) {
                    workDay.push($(item).val());
                    workDayText.push($(item).next().text());
                  }
                });
                if (workDay.length == 0) {
                  appModal.error('工作日不能为空！');
                  return false;
                }
                obj.workDayText = workDayText.join(';');
                obj.workDay = workDay.join(';');
                obj.type = $("input[name='type']:checked", $addTimeDialog).val();
                obj.coreWorkPeriod = obj.type == '1' ? false : $('#coreTime', $addTimeDialog).prop('checked');
                obj.timePeriods = [];
                var workHoursPerDay = 0;
                var timeText = [];
                if (obj.type == '1' || (obj.type == '2' && obj.coreWorkPeriod)) {
                  var $times = $('.time-wrapper' + obj.type, $addTimeDialog).find('.time-content');
                  var isTrue = true;
                  $.each($times, function (index, item) {
                    var name = $(item).find("input[name='name']").val();
                    var dateRange = $(item).find("input[name='dateRange']").val();
                    var toNextDay = $(item).find("input[name='toNextDay']").prop('checked');
                    if (name == '') {
                      appModal.error('时段名称不能为空！');
                      isTrue = false;
                      return false;
                    }
                    if (dateRange == '') {
                      appModal.error('起止时间不能为空！');
                      isTrue = false;
                      return false;
                    }
                    var fromTime = $.trim(dateRange.split('至')[0]);
                    var toTime = $.trim(dateRange.split('至')[1]);
                    obj.timePeriods.push({
                      name: name,
                      toNextDay: toNextDay,
                      fromTime: fromTime,
                      toTime: toTime
                    });
                    timeText.push(name + '：' + fromTime + '-' + toTime);
                    var newHours = Number(countTime(fromTime, toTime, toNextDay));
                    if (newHours - 0 < 0) {
                      appModal.error('结束时间不能小于开始时间！');
                      isTrue = false;
                      return false;
                    }
                    workHoursPerDay = Number(workHoursPerDay) + newHours;
                  });

                  if (!isTrue) {
                    return false;
                  }

                  var noConflict = isConflict(obj, $addPlanDialog, data, index);
                  if (!noConflict) {
                    appModal.error('工作时段存在冲突，不可重叠！');
                    return false;
                  }
                }

                obj.timeText = timeText;
                obj.workHoursPerDay =
                  obj.type == '2' ? ($("input[name='workTimeHours']", $addTimeDialog).val() - 0).toFixed(2) : workHoursPerDay.toFixed(2);
                if (obj.workHoursPerDay == '' && obj.type == '2') {
                  appModal.error('每日工作时长不能为空！');
                  return false;
                } else if (obj.workHoursPerDay - workHoursPerDay < 0 && obj.type == '2') {
                  appModal.error('核心工作时段不能大于日工作时长！');
                  return false;
                } else if (obj.workHoursPerDay - 0 <= 0) {
                  appModal.error('每日工作时长不能为负数！');
                  return false;
                }

                var $tr = renderTimeHtml(obj);
                if (data.status == 'add') {
                  $addPlanDialog
                    .find('.workTime' + data.workTimeType)
                    .find('.work-time-body:eq(' + (index - 1) + ')')
                    .find('tbody')
                    .append($tr);
                } else {
                  $addPlanDialog
                    .find('.workTime' + data.workTimeType)
                    .find('.work-time-body:eq(' + (index - 1) + ')')
                    .find('tbody tr:eq(' + data.trIndex + ')')
                    .html($($tr).html())
                    .data('data', obj);
                }

                var hours = getWeekHours($addPlanDialog, data.workTimeType, index);
                $addPlanDialog
                  .find('.workTime' + data.workTimeType)
                  .find('.title:eq(' + (index - 1) + ')')
                  .find('span')
                  .text(hours);
              }
            },
            cancel: {
              label: '取消',
              className: 'btn-default'
            }
          }
        });
      }

      function isConflict(obj, $addPlanDialog, data, index) {
        var workDayEn = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];

        var $tr = $addPlanDialog
          .find('.workTime' + data.workTimeType)
          .find(".work-time-body[data-index='" + index + "']")
          .find('tr');

        var noConflict = true;
        var timeObj = [];

        $.each($tr, function (rIndex, rItem) {
          if ($(rItem).data('data')) {
            var newObj = {};
            if (data.trIndex && rIndex == data.trIndex) {
              newObj = _.cloneDeep(obj);
            } else {
              newObj = _.cloneDeep($(rItem).data('data'));
            }
            var workDays = newObj.workDay.split(';');
            $.each(workDays, function (wIndex, wItem) {
              timeObj.push({
                day: wItem,
                timePeriods: newObj.timePeriods
              });
            });
          }
        });

        if (data.status == 'add') {
          var workDays = obj.workDay.split(';');
          $.each(workDays, function (wIndex, wItem) {
            timeObj.push({
              day: wItem,
              timePeriods: obj.timePeriods
            });
          });
        }

        timeObj = _.sortBy(timeObj, function (o) {
          return workDayEn.indexOf(o.day);
        });

        for (var i = 0; i < timeObj.length; i++) {
          // 天

          var timePeriods = _.sortBy(timeObj[i].timePeriods, function (o) {
            return o.fromTime;
          });

          for (var j = 0; j < timePeriods.length; j++) {
            // 时段
            var num = countTime(timePeriods[j].fromTime, timePeriods[j].toTime, timePeriods[j].toNextDay); // 判断时段内数值是否小于0
            if (num < 0) {
              noConflict = false;
              break;
            }
            if (timePeriods[j].toNextDay) {
              // 时间段是否+1
              if (j < timePeriods.length - 1) {
                // +1时间段后有值，必冲突
                noConflict = false;
                break;
              } else if (i < timeObj.length - 1 && timeObj[i + 1].day == workDayEn[i + 1] && timeObj[i + 1].timePeriods.length > 0) {
                // +1 时间段后没有其他时间段
                if (!compareTime(timePeriods[j].toTime, timeObj[i + 1].timePeriods[0].fromTime)) {
                  // 判断第二天的开始时间是否大于第一天的结束时间
                  noConflict = false;
                  break;
                }
              }
            } else if (j < timePeriods.length - 1 && !compareTime(timePeriods[j].toTime, timePeriods[j + 1].fromTime)) {
              noConflict = false;
              break;
            }
          }
        }

        console.log(timeObj);
        return noConflict;
      }

      function compareTime(fromTime, toHours) {
        var from = parseInt(fromTime.split(':')[0]) * 60 + parseInt(fromTime.split(':')[1]);
        var to = parseInt(toHours.split(':')[0]) * 60 + parseInt(toHours.split(':')[1]);
        return to - from > 0;
      }

      function setWeekDayDisable($plan, $time, data, index, type) {
        var $tr = $('.workTime' + type, $plan)
          .find('.work-time-body[data-index="' + index + '"]')
          .find('tr[data-data]');
        var weekDay = [];
        $.each($tr, function (i, item) {
          var obj = $(item).data('data');
          if (data.status == 'add') {
            weekDay = weekDay.concat(obj.workDay.split(';'));
          } else if (i != data.trIndex - 1) {
            weekDay = weekDay.concat(obj.workDay.split(';'));
          }
        });
        $.each(weekDay, function (i, item) {
          $time.find("input[id='" + item + "']").attr('disabled', true);
        });
      }

      function getWeekHours($addPlanDialog, workTimeType, index) {
        var hours = 0;
        var $tr = $addPlanDialog
          .find('.workTime' + workTimeType)
          .find('.work-time-body:eq(' + (index - 1) + ')')
          .find('tr');
        $.each($tr, function (i, item) {
          var obj = $(item).data('data');
          if (obj) {
            var workDay = obj.workDay.split(';').length;
            var perHours = obj.workHoursPerDay;
            hours = perHours * workDay + Number(hours);
          }
        });

        return hours.toFixed(2);
      }

      //计算日小时
      function countTime(fromTime, toHours, toNextDay) {
        var from = parseInt(fromTime.split(':')[0]) * 60 + parseInt(fromTime.split(':')[1]);
        var to = parseInt(toHours.split(':')[0]) * 60 + parseInt(toHours.split(':')[1]);
        if (toNextDay) {
          return ((to - from) / 60).toFixed(2) - 0 + 24;
        } else {
          return ((to - from) / 60).toFixed(2);
        }
      }

      // 获取工作时间安排页面数据
      function getWorkPlanDetail(uuid, self, isHistory) {
        var url = isHistory ? '/api/ts/work/time/plan/history/get' : '/api/ts/work/time/plan/get';
        $.ajax({
          type: 'get',
          url: ctx + url,
          dataType: 'json',
          data: { uuid: uuid },
          success: function (res) {
            if (res.code == 0) {
              bean = res.data;
              $('#workingTimePlan', $element).json2form(bean);
              $('.work-header-text', $element)
                .find('span')
                .append(' v' + bean.version);
              self.version = bean.version;
              if (bean.newVersionTip) {
                $('.work-header-text', $element).find('i').show().attr('title', bean.newVersionTip);
              }
              var holidaySchedule = JSON.parse(bean.holidaySchedule);
              holidaySchedule = _.sortBy(holidaySchedule, function (o) {
                return o.holidayInstanceDate;
              });
              var workTimeSchedule = JSON.parse(bean.workTimeSchedule);
              var workTimeHtml = renderWorkTimHtml(workTimeSchedule);
              $('.holiday_arrangement_list', $element).append(workTimeHtml);
              HolidayArragement.renderHolidayArrangement($element, holidaySchedule, true, true);
              if (isHistory) {
                $('.work-header-text', $element)
                  .find('span')
                  .html('工作时间记录（' + bean.activeTime + '&nbsp;&nbsp;~&nbsp;&nbsp;' + bean.deactiveTime + '）');
              }
              if (isCheck) {
                $('.work-plan-header', $element).find('button').addClass('w-disable-btn').attr('disabled', true);
                $('.work-plan-body', $element).find("button[type='button'],.time-header-btn,.holiday-header-btn,.cutDate").hide();
                $('.work-plan-body', $element).find('input').addClass('isCheck').attr('disabled', true);
                $('.work-plan-body', $element).find('textarea').addClass('isCheck');
                $('.work-plan-body', $element).find('.switch-wrap').addClass('switch-wrap-disabled');
              }
            }
          }
        });
      }
      // 渲染工作时间数据
      function renderTimeHtml(obj) {
        var workDayText = obj.workDayText || [];
        if (workDayText.length == 0) {
          var workDays = obj.workDay.split(';');
          $.each(workDays, function (i, item) {
            workDayText.push(workDay[item]);
          });
          obj.workDayText = workDayText.join(';');
        }
        var timeText = obj.timeText || [];
        if (timeText.length == 0) {
          $.each(obj.timePeriods, function (i, item) {
            timeText.push(item.name + '：' + item.fromTime + '-' + item.toTime);
          });
        }

        var $tr = '';
        $tr +=
          "<tr data-data='" +
          JSON.stringify(obj) +
          "'>" +
          '<td>' +
          obj.workDayText +
          '</td>' +
          '<td>' +
          (obj.type == '1'
            ? '<span class="time-tag">固定工作时段</span>'
            : obj.timePeriods.length > 0
            ? '<span class="time-tag">核心工作时段</span>'
            : '') +
          '' +
          timeText.join(';') +
          '</td>' +
          '<td>' +
          (obj.workHoursPerDay || 0) +
          '小时</td>' +
          '<td>' +
          '<button type="button" class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-time-set"><i class="iconfont icon-ptkj-shezhi"></i>设置</button>' +
          '<button type="button" class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-time-del"><i class="iconfont icon-ptkj-shanchu"></i>删除</button>' +
          '</td>' +
          '</tr>';

        return $tr;
      }

      // 渲染工作时间安排数据
      function renderWorkTimHtml(workTimeSchedule) {
        var workTimeHtml = '';
        var count = $('.holiday_arrangement_list', $element).find('.work-time-item').length;
        $.each(workTimeSchedule, function (index, item) {
          workTimeHtml +=
            "<div class='work-time-item' data-index='" +
            (index + count) +
            "'  data-obj='" +
            JSON.stringify(item) +
            "'>" +
            '<div class="work-time-title"><span><i class="iconfont icon-ptkj-rilixuanzeriqi"></i>' +
            item.fromDate +
            ' 至 ' +
            item.toDate;
          if (item.periodType == '2' && item.toNextYeay) {
            workTimeHtml += '<span class="add-year"><i class="add-year-angle"></i>+1年</span>';
          }

          workTimeHtml +=
            '</span>' +
            '<div class="time-header-btn">' +
            '<span class="btn-plan-set">' +
            '<i class="iconfont icon-ptkj-shezhi"></i>' +
            '设置' +
            '</span>' +
            '<span class="btn-plan-del">' +
            '<i class="iconfont icon-ptkj-shanchu"></i>' +
            '删除' +
            '</span>' +
            '</div>' +
            '</div>';

          var oddHtml = [];
          var evenHtml = [];
          var hasOdd = false;
          var oddHours = 0;
          var evenHours = 0;
          for (var i = 0; i < item.workTimes.length; i++) {
            var work = item.workTimes[i];
            var timeText = [];
            $.each(work.timePeriods, function (tIndex, tItem) {
              timeText.push(tItem.name + '：' + tItem.fromTime + '-' + tItem.toTime);
            });
            var tr =
              '<tr>' +
              '<td>' +
              getWeekDay(work.workDay) +
              '</td>' +
              '<td>' +
              '<span class="time-tag">' +
              (work.type == '1' ? '固定工作时段' : '核心工作时段') +
              '</span>' +
              timeText.join(';') +
              '</td>' +
              '<td>' +
              (work.workHoursPerDay || 0) +
              '小时</td>' +
              '</tr>';
            if (work.oddWeeks) {
              oddHours = Number(oddHours) + Number(work.workHoursPerDay || 0) * work.workDay.split(';').length;
              oddHtml.push(tr);
            } else {
              evenHtml.push(tr);
              evenHours = Number(evenHours) + Number(work.workHoursPerDay || 0) * work.workDay.split(';').length;
            }
          }
          if (oddHtml.length > 0) {
            hasOdd = true;
            workTimeHtml +=
              "<div class='work-time-block'><div class='work-time-header'>" +
              '<div class="time-header-info">' +
              '<span>' +
              '<i class="iconfont icon-ptkj-lishi"></i>单周工时' +
              '（合计' +
              '<span class="time-count">' +
              (oddHours - 0).toFixed(2) +
              '</span>' +
              '小时）' +
              '</span>' +
              '</div>' +
              '</div>' +
              '<table class="work-time-body">' +
              '<tr>' +
              '<th>工作日</th>' +
              '<th>工作时间</th>' +
              '<th>日工作时长</th>' +
              '</tr>' +
              oddHtml.join('');

            workTimeHtml += '</table></div>';
          }
          if (evenHtml.length > 0 || item.workTimeType != '2') {
            workTimeHtml +=
              "<div class='work-time-block'><div class='work-time-header'>" +
              '<div class="time-header-info">' +
              '<span>' +
              '<i class="iconfont icon-ptkj-lishi"></i>' +
              (hasOdd ? '双周工时' : workTimeType[item.workTimeType]) +
              '（合计' +
              '<span class="time-count">' +
              (item.workTimeType == '3' ? item.workHoursPerWeek : (evenHours - 0).toFixed(2)) +
              '</span>' +
              '小时）' +
              '</span>' +
              '</div>' +
              '</div>' +
              '<table class="work-time-body">' +
              '<tr>' +
              '<th>工作日</th>' +
              '<th>工作时间</th>' +
              '<th>日工作时长</th>' +
              '</tr>' +
              evenHtml.join('');

            workTimeHtml += '</table></div>';
          }

          workTimeHtml += '</div>';
        });
        return workTimeHtml;
      }

      // 周时间显示转换
      function getWeekDay(days) {
        var day = days.split(';');
        var newDay = [];
        $.each(day, function (index, item) {
          newDay.push(workDay[item]);
        });
        return newDay.join(';');
      }

      // 工作时间安排弹窗html
      function getWorkPlanHtml() {
        var html = '';
        html +=
          '<div class="work-body-info work-plan-dialog">' +
          '<div class="field">' +
          '<div class="label-td">应用时间周期' +
          '</div>' +
          '<div class="value-td">' +
          '<input id="periodType1" name="periodType" type="radio" class="form-control" checked value="1"/><label for="periodType1">全年</label>' +
          '<input id="periodType2" name="periodType" type="radio" class="form-control" value="2"/><label for="periodType2">指定时间周期</label>' +
          '</div>' +
          '</div>' +
          '<div class="field showTimeRange" style="display:none;">' +
          '<div class="label-td"><font style="color:#f00;">*</font>时间周期</div>' +
          '<div class="value-td">' +
          '<input id="periodDateStart" name="periodDateStart" type="text" class="form-control" autocomplete="off" style="width:200px;margin-right:10px;">至' +
          '<input id="periodDateEnd" name="periodDateEnd" type="text" class="form-control" autocomplete="off" style="width:200px;margin-left:10px;">' +
          '<input type="checkbox" name="toNextYeay" id="toNextYeay"/><label for="toNextYeay">+1年</label>' +
          '</div>' +
          '</div>' +
          '<div class="field">' +
          '<div class="label-td"><font style="color:#f00;">*</font>类型</div>' +
          '<div class="value-td">' +
          '<input id="workTimeType1" name="workTimeType" type="radio" class="form-control" value="1" checked><label for="workTimeType1">固定工时</label>' +
          '<input id="workTimeType2" name="workTimeType" type="radio" class="form-control" value="2"><label for="workTimeType2">单双周</label>' +
          '<input id="workTimeType3" name="workTimeType" type="radio" class="form-control" value="3"><label for="workTimeType3">弹性工时</label>' +
          '</div>' +
          '</div>' +
          '<div class="workTime workTime1">' +
          '<div class="title">固定工时（合计<span>0</span>小时）</div>' +
          '<table class="work-time-body" data-index="1">' +
          '<tr><th>工作日</th><th>工作时间</th><th>日工作时长</th><th>操作</th></tr>' +
          '</table>' +
          '<button type="button" class="well-btn w-btn-primary w-noLine-btn btn-add-holiday" >添加</button>' +
          '</div>' +
          '<div class="workTime workTime2" style="display:none">' +
          '<div  class="title">单周工时（合计<span>0</span>小时）</div>' +
          '<table class="work-time-body" data-index="1">' +
          '<tr><th>工作日</th><th>工作时间</th><th>日工作时长</th><th>操作</th></tr>' +
          '</table>' +
          '<button type="button" class="well-btn w-btn-primary w-noLine-btn btn-add-holiday">添加</button>' +
          '<div  class="title">双周工时（合计<span>0</span>小时）</div>' +
          '<table class="work-time-body"   data-index="2">' +
          '<tr><th>工作日</th><th>工作时间</th><th>日工作时长</th><th>操作</th></tr>' +
          '</table>' +
          '<button type="button" class="well-btn w-btn-primary w-noLine-btn btn-add-holiday">添加</button>' +
          '</div>' +
          '<div class="workTime workTime3" style="display:none;">' +
          '<div class="title">弹性工时</div>' +
          '<div class="work-time-flex">' +
          '<span class="line-title"><font style="color:#f00;">*</font>每周工作时长</span>' +
          '<div class="line-input">' +
          '<input type="number" id="workHoursPerWeek" name="workHoursPerWeek" autocomplete="off">' +
          '<div><input type="checkbox" name="coreWorkDay" id="coreWorkDay"><label for="coreWorkDay">设置核心工作日</label></div>' +
          '</div>' +
          '</div>' +
          '<div style="display:none;"><table class="work-time-body" data-index="1">' +
          '<tr><th>核心工作日</th><th>工作时间</th><th>日工作时长</th><th>操作</th></tr>' +
          '</table><button type="button" class="well-btn w-btn-primary w-noLine-btn btn-add-holiday">添加</button></div>' +
          '</div>' +
          '</div>';
        return html;
      }

      //工作时间弹窗html
      function getWorkTimeHtml() {
        var html = '';
        html +=
          '<div class="dialog-wrapper dialog-work-time">' +
          '<div class="row-line">' +
          '<span class="row-line-label"><font style="color:#f00;">*</font>工作日</span>' +
          '<div>' +
          '<input type="checkbox" name="workDay" id="MON" value="MON"><label for="MON">周一</label>' +
          '<input type="checkbox" name="workDay" id="TUE" value="TUE"><label for="TUE">周二</label>' +
          '<input type="checkbox" name="workDay" id="WED" value="WED"><label for="WED">周三</label>' +
          '<input type="checkbox" name="workDay" id="THU" value="THU"><label for="THU">周四</label>' +
          '<input type="checkbox" name="workDay" id="FRI" value="FRI"><label for="FRI">周五</label>' +
          '<input type="checkbox" name="workDay" id="SAT" value="SAT"><label for="SAT">周六</label>' +
          '<input type="checkbox" name="workDay" id="SUN" value="SUN"><label for="SUN">周日</label>' +
          '</div>' +
          '</div>' +
          '<div class="row-line">' +
          '<span class="row-line-label">类型</span>' +
          '<div>' +
          '<input type="radio" name="type" id="type1" value="1" checked><label for="type1">固定时间</label>' +
          '<input type="radio" name="type" id="type2" value="2"><label for="type2">弹性时间</label>' +
          '</div>' +
          '</div>' +
          '<div class="type type1">' +
          '<div class="time-wrapper time-wrapper1">' +
          '<div class="time-line"><span class="time-text">时段名称</span><span class="time-range">起止时间</span></div>' +
          '<div class="time-line time-content" data-index="1">' +
          '<i class="time-drag iconfont icon-wsbs-liebiaoshitu"></i>' +
          '<input type="text" name="name" id="name1" class="time-text-input" value="上午">' +
          '<input type="text" name="dateRange" id="dateRange1" class="time-date">' +
          '<div class="time-day"><input type="checkbox" name="toNextDay" id="toNextDay1"><label for="toNextDay1">+1天</label></div>' +
          '<i class="iconfont icon-ptkj-shanchu time-delete"></i>' +
          '</div>' +
          '<div class="time-line time-content" data-index="2">' +
          '<i class="time-drag iconfont icon-wsbs-liebiaoshitu"></i>' +
          '<input type="text" name="name" id="name2" class="time-text-input" value="下午">' +
          '<input type="text" name="dateRange" id="dateRange2" class="time-date">' +
          '<div class="time-day"><input type="checkbox" name="toNextDay" id="toNextDay2"><label for="toNextDay2">+1天</label></div>' +
          '<i class="iconfont icon-ptkj-shanchu time-delete"></i>' +
          '</div>' +
          '</div>' +
          '<button type="button" class="mt10 well-btn w-btn-primary w-noLine-btn btn-add-time"><i class="iconfont icon-ptkj-jiahao"></i>添加</button>' +
          '</div>' +
          '<div class="type type2" style="display: none;">' +
          '<div class="row-line">' +
          '<span class="row-line-label">每日工作时长</span>' +
          '<div>' +
          '<input type="number" name="workTimeHours" id="workTimeHours">' +
          '</div>' +
          '</div>' +
          '<div class="row-line">' +
          '<span class="row-line-label"></span>' +
          '<div>' +
          '<div class=""><input type="checkbox" name="coreTime" id="coreTime"><label for="coreTime">设置核心工作时段</label></div>' +
          '</div>' +
          '</div>' +
          '<div class="type3" style="display:none;">' +
          '<div class="time-wrapper time-wrapper2">' +
          '<div class="time-line"><span class="time-text">时段名称</span><span class="time-range">起止时间</span></div>' +
          '<div class="time-line time-content" data-index="1">' +
          '<i class="time-drag iconfont icon-wsbs-liebiaoshitu"></i>' +
          '<input type="text" name="name" id="name1" class="time-text-input" value="上午">' +
          '<input type="text" name="dateRange" id="dateRanges1" class="time-date">' +
          '<div class="time-day"><input type="checkbox" name="toNextDay" id="toNextDay1"><label for="toNextDay1">+1天</label></div>' +
          '<i class="iconfont icon-ptkj-shanchu time-delete"></i>' +
          '</div>' +
          '<div class="time-line time-content" data-index="2">' +
          '<i class="time-drag iconfont icon-wsbs-liebiaoshitu"></i>' +
          '<input type="text" name="name" id="name2" class="time-text-input" value="下午">' +
          '<input type="text" name="dateRange" id="dateRanges2" class="time-date">' +
          '<div class="time-day"><input type="checkbox" name="toNextDay" id="toNextDay2"><label for="toNextDay2">+1天</label></div>' +
          '<i class="iconfont icon-ptkj-shanchu time-delete"></i>' +
          '</div>' +
          '</div>' +
          '<button type="button" class="mt10 well-btn w-btn-primary w-noLine-btn btn-add-time"><i class="iconfont icon-ptkj-jiahao"></i>添加</button>' +
          '</div>' +
          '</div>' +
          '</div>';
        return html;
      }

      // 保存新版本弹窗
      function getNewVersionHtml() {
        var html = '';
        html +=
          '<div class="dialog-wrapper">' +
          '<div class="row-line">' +
          '<span class="row-line-label">新版本</span>' +
          '<div>v<span class="work-version"></span></div>' +
          '</div>' +
          '<div class="row-line">' +
          '<span class="row-line-label"></span>' +
          '<div>' +
          '<input type="radio" name="activeRightNow" id="activeRightNow1" value="1" checked><label for="activeRightNow1">立即生效</label>' +
          '<input type="radio" name="activeRightNow" id="activeRightNow2" value="2"><label for="activeRightNow2">指定时间生效</label>' +
          '</div>' +
          '</div>' +
          '<div class="row-line activeTimeBox" style="display:none;">' +
          '<span class="row-line-label"><font style="color:#f00;">*</font>生效时间</span>' +
          '<div>' +
          '<input class="form-control" type="text" name="activeTime" id="activeTime">' +
          '</div>' +
          '</div>' +
          '</div>';
        return html;
      }
    }
  });
  return AppWorkingTimePlanWidgetDevelopment;
});
