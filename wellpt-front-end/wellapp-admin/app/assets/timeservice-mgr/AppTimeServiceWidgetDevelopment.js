define(['constant', 'commons', 'server', 'appContext', 'appModal', 'layDate', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  laydate,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  // 平台应用_基础数据管理_计时服务_详情二开
  var AppTimeServiceWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppTimeServiceWidgetDevelopment, HtmlWidgetDevelopment, {
    // 初始化回调
    init: function () {
      var $container = this.widget.element;
      $container.parents('.container-fluid').css({
        background: '#fff'
      });
      var bean = {
        uuid: '',
        name: '',
        id: '',
        code: '',
        categoryUuid: '',
        timingMode: '',
        includeStartTimePoint: '',
        autoDelay: '',
        timeLimitType: '',
        timeLimit: '',
        timeLimitUnit: '',
        remark: ''
      };

      parent
        .$('iframe[src^="/web/app/page/preview/1526fca2143e81de76c3e0b7569fbd3f?pageUuid=6606477d-e115-4452-83b0-b5dc655d77dd"]')
        .parent()
        .css({
          paddingBottom: '61%'
        });

      var uuid = GetRequestParam().uuid || this.getWidgetParams().uuid;
      var from = GetRequestParam().from || this.getWidgetParams().from;
      var editable = GetRequestParam().editable || this.getWidgetParams().editable;

      // 计时服务分类
      $('#categoryUuid', $container).wellSelect({
        data: [],
        valueField: 'categoryUuid'
      });

      // 获取计时服务列表数据
      getSelectdata();
      if (uuid) {
        getTimeConfig(uuid, bean);
      } else {
        var categoryUuid = localStorage.getItem('ts-categoryUuid');
        if (categoryUuid != 'undefined') {
          $('#categoryUuid', $container).wellSelect('val', categoryUuid).trigger('change');
        }
      }
      if (from == 'workflow') {
        $('#btn_save_time_service', $container).parents('.row').first().hide();
      }

      var timingMode = [
        [3, 2, 1],
        [13, 12, 11],
        [86400, 3600, 60]
      ];

      var mode1 = [
        { id: '3', text: '工作日' },
        { id: '2', text: '工作小时' },
        { id: '1', text: '工作分钟' }
      ];
      var mode2 = [
        { id: '13', text: '工作日' },
        { id: '12', text: '小时（工作日）' },
        { id: '11', text: '分钟（工作日）' }
      ];
      var mode3 = [
        { id: '86400', text: '天' },
        { id: '3600', text: '小时' },
        { id: '60', text: '分钟' }
      ];

      // 计时方式
      $('#timingMode1', $container)
        .wellSelect({
          data: [
            { id: '1', text: '工作日' },
            { id: '2', text: '工作日（一天24小时）' },
            { id: '3', text: '自然日' }
          ],
          searchable: false,
          showEmpty: false,
          valueField: 'timingMode1'
        })
        .off()
        .on('change', function () {
          var val = $(this).val();
          $('.autoDelay', $container)[val == '3' ? 'hide' : 'show']();
          var data = val == '1' ? mode1 : val == '2' ? mode2 : mode3;
          $('#timingMode2', $container).wellSelect('destroy').wellSelect({
            data: data,
            searchable: false,
            showEmpty: false,
            valueField: 'timingMode2'
          });
          $('#timingMode2', $container).wellSelect('val', data[0].id);
        });

      var timeLimitUnit1 = [
        { id: '1', text: '天' },
        { id: '2', text: '小时' },
        { id: '3', text: '分钟' }
      ];

      var timeLimitUnit2 = [
        { id: '1', text: '天（日期 2000-01-01）' },
        { id: '2', text: '小时（日期到时 2000-01-01 12）' },
        { id: '3', text: '分钟（日期到分 2000-01-01 12:00）' }
      ];

      //计时单位
      $('#timingMode2', $container)
        .wellSelect({
          data: [],
          searchable: false,
          showEmpty: false,
          valueField: 'timingMode2'
        })
        .off()
        .on('change', function () {
          var includeStartTimePoint = $("input[name='includeStartTimePoint']:checked", $container).val();
          if (includeStartTimePoint == 0) {
            var val = $(this).val();
            var num = val == '3' || val == '13' || val == '86400' ? 2 : val == '2' || val == '12' || val == '3600' ? 1 : 0;
            $('#timeLimitUnit1', $container)
              .val('')
              .wellSelect('reRenderOption', {
                data: _.dropRight(timeLimitUnit1, num)
              });
            $('#timeLimitUnit2', $container)
              .val('')
              .wellSelect('reRenderOption', {
                data: _.dropRight(timeLimitUnit2, num)
              });
          }
        });

      $("input[name='includeStartTimePoint']", $container)
        .off()
        .on('change', function () {
          var includeStartTimePoint = $(this).val();
          var val = $('#timingMode2', $container).val();
          var num =
            includeStartTimePoint == '0'
              ? val == '3' || val == '13' || val == '86400'
                ? 2
                : val == '2' || val == '12' || val == '3600'
                ? 1
                : 0
              : 0;
          $('#timeLimitUnit1', $container)
            .val('')
            .wellSelect('reRenderOption', {
              data: _.dropRight(timeLimitUnit1, num)
            });
          $('#timeLimitUnit2', $container)
            .val('')
            .wellSelect('reRenderOption', {
              data: _.dropRight(timeLimitUnit2, num)
            });
        });

      //时限类型
      $('#timeLimitType', $container)
        .wellSelect({
          data: [
            { id: '10', text: '固定时限' },
            { id: '20', text: '固定截止时间' },
            { id: '30', text: '动态时限' },
            { id: '40', text: '动态截止时间' }
          ],
          searchable: false,
          showEmpty: false,
          valueField: 'timeLimitType'
        })
        .off()
        .on('change', function () {
          var val = $(this).val();
          $('.timeLimitType', $container).hide();
          $('.timeLimitType' + val, $container).show();
        });

      //时限单位-固定时限
      $('#timeLimitUnit1', $container).wellSelect({
        data: timeLimitUnit1,
        searchable: false,
        showEmpty: false,
        valueField: 'timeLimitUnit1'
      });

      //时限单位-固定截止时间
      $('#timeLimitUnit2', $container)
        .wellSelect({
          data: timeLimitUnit2,
          searchable: false,
          showEmpty: false,
          valueField: 'timeLimitUnit2'
        })
        .off()
        .on('change', function () {
          var val = $(this).val();
          $('#timeLimit2', $container).remove();
          $('.timeLimit2').append(
            '<input type="text" class="form-control" id="timeLimit2" name="timeLimit2" placeholder="请选择" style="width:350px;">'
          );
          // 截止时间
          if (val == '1') {
            laydate.render({
              elem: '#timeLimit2',
              format: 'yyyy-MM-dd',
              trigger: 'click',
              type: 'date'
            });
          } else if (val == '2') {
            laydate.render({
              elem: '#timeLimit2',
              format: 'yyyy-MM-dd HH',
              trigger: 'click',
              type: 'datetime'
            });
          } else if (val == '3') {
            laydate.render({
              elem: '#timeLimit2',
              format: 'yyyy-MM-dd HH:mm',
              trigger: 'click',
              type: 'datetime'
            });
          }
        });

      // 保存
      $('#btn_save_time_service', $container)
        .off()
        .on('click', function () {
          $('#time_service_form', this.$container).form2json(bean);
          var emptyStr = [];
          var numFormat = '';
          if (bean.name == '') {
            emptyStr.push('名称');
          }
          if (bean.id == '') {
            emptyStr.push('ID');
          }
          var timingMode1 = $('#timingMode1', $container).val();
          var timingMode2 = $('#timingMode2', $container).val();
          if (timingMode1 == '' || timingMode2 == '') {
            emptyStr.push('计时方式');
          } else {
            bean.timingMode = timingMode2;
          }

          if (bean.timeLimitType == '') {
            emptyStr.push('时限类型');
          } else if (bean.timeLimitType == '10') {
            bean.timeLimitUnit = $('#timeLimitUnit1', $container).val();
            bean.timeLimit = $('#timeLimit1', $container).val();

            if (bean.timeLimitUnit == '') {
              emptyStr.push('时限单位');
            }
            if (bean.timeLimit == '') {
              emptyStr.push('时限');
            } else if (bean.timeLimit - 0 <= 0) {
              numFormat = '时限只能输入正数！';
            }
          } else if (bean.timeLimitType == '20') {
            bean.timeLimitUnit = $('#timeLimitUnit2', $container).val();
            bean.timeLimit = $('#timeLimit2', $container).val();

            if (bean.timeLimitUnit == '') {
              emptyStr.push('时限单位');
            }

            if (bean.timeLimit == '') {
              emptyStr.push('截止时间');
            }
          }

          if (emptyStr.length > 0 || numFormat != '') {
            var tips = emptyStr.length > 0 ? emptyStr.join('、') + '不能为空！' + numFormat : numFormat;
            appModal.error(tips);
            return false;
          }
          bean.includeStartTimePoint = $("input[name='includeStartTimePoint']:checked", $container).val() == 1 ? true : false;
          bean.autoDelay = $('#autoDelay', $container).prop('checked');
          $.ajax({
            type: 'post',
            url: ctx + '/api/ts/timer/config/save',
            data: bean,
            dataType: 'json',
            success: function (res) {
              if (res.code == 0) {
                appModal.success('保存成功！', function () {
                  appContext.getNavTabWidget().closeTab();
                });
              } else {
                appModal.error(res.msg);
              }
            }
          });
        });

      function getTimeConfig(uuid, bean) {
        // 计时服务数据
        $.ajax({
          type: 'get',
          url: ctx + '/api/ts/timer/config/get',
          data: { uuid: uuid },
          success: function (res) {
            if (res.code == 0) {
              bean = res.data;
              // 查看详情- 设置只读
              if (editable == 'false') {
                $('#name', $container).attr('readonly', true); // 名称
                $('#code', $container).attr('readonly', true); // 编号
                $('#categoryUuid', $container).wellSelect('disable', true);

                $('#autoDelay', $container).attr('disabled', true); // 时限
                $('#includeStartTimePoint1', $container).attr('disabled', true); // 计时方式
                $('#includeStartTimePoint2', $container).attr('disabled', true);
                $('#timeLimitType', $container).wellSelect('disable', true);
                $('#timeLimit1', $container).attr('readonly', true);
                $('#timingMode1', $container).wellSelect('disable', true);
                $('#timingMode2', $container).wellSelect('disable', true);
                $('#remark', $container).attr('readonly', true); // 备注
                $('#timeLimitUnit2', $container).wellSelect('disable', true);
                $('#timeLimitUnit1', $container).wellSelect('disable', true);
              }

              $('#time_service_form', this.$container).json2form(bean);
              $('#id', $container).attr('readonly', true);
              $('#categoryUuid', $container).wellSelect('val', bean.categoryUuid).trigger('change');
              setTimingMode(bean.timingMode);
              $("input[name='includeStartTimePoint'][value='" + bean.includeStartTimePoint + "']", $container)
                .prop('checked', true)
                .trigger('change');
              $('#autoDelay', $container).prop('checked', bean.autoDelay == '1' ? true : false);
              $('.timeLimitType' + bean.timeLimitType, $container).show();
              $('#timeLimitType', $container).wellSelect('val', bean.timeLimitType).trigger('change');
              if (bean.timeLimitType == '10') {
                $('#timeLimitUnit1', $container).wellSelect('val', bean.timeLimitUnit).trigger('change');
                $('#timeLimit1', $container).val(bean.timeLimit);
              } else if (bean.timeLimitType == '20') {
                $('#timeLimitUnit2', $container).wellSelect('val', bean.timeLimitUnit).trigger('change');
                $('#timeLimit2', $container).val(bean.timeLimit);
                if (editable == 'false') {
                  $('#timeLimit2', $container).attr('disabled', true);
                }
              }
            }
          }
        });
      }

      function getSelectdata() {
        // 获取计时分类列表
        $.ajax({
          type: 'post',
          url: ctx + '/api/ts/timer/category/getAllBySystemUnitIdsLikeName',
          data: { name: '' },
          success: function (res) {
            if (res.code == 0) {
              var data = [];
              $.each(res.data, function (index, item) {
                data.push({
                  id: item.uuid,
                  text: item.name
                });
              });
              $('#categoryUuid', $container).wellSelect('destroy').wellSelect({
                data: data,
                valueField: 'categoryUuid'
              });
            }
          }
        });
      }

      function setTimingMode(mode) {
        for (var i = 0; i < timingMode.length; i++) {
          for (var j = 0; j < timingMode[i].length; j++) {
            if (mode == timingMode[i][j]) {
              $('#timingMode1', $container)
                .wellSelect('val', (i + 1).toString())
                .trigger('change');

              $('#timingMode2', $container).wellSelect('val', mode).trigger('change');

              break;
            }
          }
        }
      }
    }
  });
  return AppTimeServiceWidgetDevelopment;
});
