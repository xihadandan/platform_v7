define([
  'appContext',
  'constant',
  'commons',
  'server',
  'bootbox',
  'formBuilder',
  'appModal',
  'design_commons',
  'moment',
  'dataStoreBase',
  'select2',
  'wSelect2'
], function (appContext, constant, commons, server, bootbox, formBuilder, appModal, designCommons, moment, DataStore) {
  var component = $.ui.component.BaseComponent();
  var StringUtils = commons.StringUtils;
  var collectClass = 'w-configurer-option';
  var _minTime = '08:00';
  var _maxTime = '18:00';
  var clearChecked = function (row) {
    row.checked = false;
    return row;
  };

  var prefixZero = function (num) {
    return ('0' + num).slice(-2);
  };

  var isJSON = function (str) {
    try {
      var obj = JSON.parse(str);
      if (typeof obj == 'object') {
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  };
  var checkRequire = function (propertyNames, options, $container, viewName) {
    console.log(propertyNames, options);
    for (var i = 0; i < propertyNames.length; i++) {
      var propertyName = propertyNames[i];
      if (StringUtils.isBlank(options[propertyName])) {
        var title = $("label[for='" + propertyName + "']", $container).text();
        appModal.error((viewName || '基础信息') + '的' + '' + title.replace('*', '') + '不允许为空！');
        return false;
      }
    }
    return true;
  };
  var onEditHidden = function (field, row, $el, reason) {
    $el.closest('table').bootstrapTable('resetView');
  };
  var checkedFormat = function (value, row, index) {
    if (value) {
      return true;
    }
    return false;
  };
  // 列定义bean
  var columnRowBean = {
    checked: false,
    uuid: '',
    title: '',
    name: '',
    dataType: '',
    width: '',
    hidden: '1',
    resourceDetailHidden: '1',
    sortable: '0',
    keywordQuery: '1',
    editable: '0',
    defaultValue: '',
    controlOptions: {
      queryTypeLabel: '文本框',
      queryType: 'text'
    },
    validateRules: {
      validateRuleLabel: '',
      validateRegex: ''
    }
  };

  var clearInputValue = function ($container) {
    $container.find('.w-configurer-option').each(function () {
      var $element = $(this);
      var type = $element.attr('type');
      if (type == 'text' || type == 'hidden') {
        $element.val('');
      } else if (type == 'checkbox' || type == 'radio') {
        $element.prop('checked', false);
      }
      $element.trigger('change');
    });
  };

  var loadColumnNames = function (alwaysLoad, elementId) {
    elementId = elementId || 'dataProviderId';
    var dataProviderId = $('#' + elementId).val();
    var dataProviderIdKey = 'data-' + dataProviderId;
    var columnNames = $('#' + elementId).data(dataProviderIdKey);
    if (columnNames == null || typeof columnNames === 'undefined') {
      server.JDS.call({
        service: 'calendarComponentService.loadColumnsSelectData',
        data: [dataProviderId],
        async: false,
        success: function (result) {
          if (result.msg == 'success') {
            columnNames = $.map(result.data.results, function (data) {
              return {
                value: data.id,
                text: data.text,
                title: data.text,
                id: data.id,
                dataType: '' // 预留
              };
            });
          }
        }
      });
      $('#' + elementId).data(dataProviderIdKey, columnNames);
    }
    // 重复加载数据来源字段时会取到空字段,在这里过滤掉
    var newColumnNames = [];
    $.each(columnNames, function (i, v) {
      if (StringUtils.isNotBlank(v.id)) {
        newColumnNames.push(columnNames[i]);
      }
    });
    return newColumnNames;
  };

  var loadDataStoreColumnNames = function (elementId) {
    elementId = elementId || 'dataProviderId';
    var dataStoreId = $('#' + elementId).val();
    var dataStoreIdKey = 'data-' + dataStoreId;
    var nameSource = $('#' + elementId).data(dataStoreIdKey);
    if (nameSource == null || typeof nameSource === 'undefined') {
      server.JDS.call({
        service: 'viewComponentService.getColumnsById',
        data: [dataStoreId],
        async: false,
        success: function (result) {
          if (result.msg == 'success') {
            nameSource = $.map(result.data, function (data) {
              return {
                value: data.columnIndex,
                text: data.title,
                dataType: data.dataType,
                title: data.title,
                id: data.columnIndex
              };
            });
          }
        }
      });
      $('#' + elementId).data(dataStoreIdKey, nameSource);
    }
    // 必须套一个空数组来合并，不然每次会被 wselect2 初始化的时候给添加一个空字符值尽量
    return [].concat(nameSource);
  };

  function buildDataProvider(id, fieldId, fieldName) {
    var _self = this;
    return new DataStore({
      dataStoreId: id,
      onDataChange: function (data, count, params) {
        params.loadSuccess({
          rows: $.map(data, function (elem, idx) {
            return {
              color: {
                className: '',
                iconColor: '#488cee'
              },
              code: elem[fieldId],
              text: elem[fieldName]
            };
          }),
          total: count
        });
      },
      defaultCriterions: [
        {
          sql: ' 1=1 '
        }
      ],
      pageSize: 25
    });
  }

  var loadOperator = function () {
    var operatorSource = [];
    server.JDS.call({
      service: 'viewComponentService.getQueryOperators',
      async: false,
      success: function (result) {
        if (result.msg == 'success') {
          operatorSource = result.data;
        }
      }
    });
    return operatorSource;
  };

  var configurer = $.ui.component.BaseComponentConfigurer();

  // 初始化'基础信息'页签
  configurer.prototype.initBaseInfo = function (configuration, $container) {
    var _self = this;
    var ignorePorps = ['enableView', 'calendar', 'resource', 'columns', 'query', 'buttons'];
    designCommons.setElementValue(configuration, $('#widget_fullcalendar_tabs_base_info', $container), ignorePorps);
    // 数据来源
    $('#dataProviderId', $container).wSelect2({
      serviceName: 'calendarComponentService',
      queryMethod: 'loadCalendarComponent',
      remoteSearch: false
    });
    $('#dataProviderId', $container)
      .change(function () {
        var dataProviderId = $('#dataProviderId').val();
        var newColumnNames = loadColumnNames(true);
        var columns = $.map(newColumnNames, function (column) {
          var bean = $.extend({}, columnRowBean);
          bean.uuid = commons.UUID.createUUID();
          bean.title = column.title;
          bean.name = column.id;
          return bean;
        });
        var $columnInfoTable = $('#table_column_info', $container);
        $columnInfoTable.bootstrapTable('removeAll').bootstrapTable('load', columns);
        clearInputValue($('#widget_fullcalendar_tabs_query_info', $container));
        var $sortInfoTable = $('#table_sort_info', $container);
        $sortInfoTable.bootstrapTable('removeAll');
        // 资源分组字段
        // $('#resourceGroupId', $container).wSelect2({
        //   data: newColumnNames
        // });
        if ($.trim(dataProviderId).length) {
          $.ajax({
            url: ctx + '/basicdata/calendarcomponent/getProviderInfo',
            type: 'POST',
            data: JSON.stringify({
              dataProviderId: dataProviderId
            }),
            dataType: 'json',
            contentType: 'application/json',
            success: function (result) {
              //
              $('#eventStatusFieldName').val(result.data.statusFieldName);
              $('#eventResourceFieldName').val(result.data.resourceFieldName);
            }
          });
        }
      })
      .trigger('change');
    // 数据来源
    $('#resProviderId', $container).wSelect2({
      serviceName: 'viewComponentService',
      queryMethod: 'loadSelectData',
      remoteSearch: false
    });
    $('#resProviderId', $container)
      .change(function () {
        var newColumnNames = loadDataStoreColumnNames('resProviderId');
        var columns = $.map(newColumnNames, function (column) {
          var bean = $.extend({}, columnRowBean);
          bean.uuid = commons.UUID.createUUID();
          bean.title = column.title;
          bean.name = column.id;
          return bean;
        });
        var $resourceDetails = $('#table_resource_details_info', $container);
        $resourceDetails.bootstrapTable('removeAll'); // .bootstrapTable('load', columns);
        // 资源分组字段
        $('#resourceGroupId,#resourceGroupName', $container).wSelect2({
          data: newColumnNames
        });
      })
      .trigger('change');

    // 加载的JS模块
    $('#jsModule', $container).wSelect2({
      serviceName: 'appJavaScriptModuleMgr',
      params: {
        dependencyFilter: 'CalendarWidgetDevelopment'
      },
      labelField: 'jsmoduleCode',
      valueField: 'jsModule',
      remoteSearch: false,
      multiple: true
    });

    // 可切换视图
    $('#switchView', $container).wSelect2({
      multiple: true
    });
    // 应用组件
    var $calendarType = $('input[name=calendarType]', $container);
    $calendarType
      .on('change', function (event) {
        var calendarType = $calendarType.filter(':checked').val();
        if ('simple' === calendarType) {
          $('#enableCalendarView').removeAttr('disabled').closest('.checkbox-inline').show();
          if (false === $('#enableCalendarView').is(':checked')) {
            $('#enableCalendarView').trigger('click');
          }
          $('#enableCalendarView').next('div').find('input[type=radio]').trigger('click');
          $('#enableResourceView').attr('disabled', 'disabled').prop('checked', false).trigger('change').closest('.checkbox-inline').hide();
        } else if ('panel' === calendarType) {
          $('#enableResourceView').removeAttr('disabled').closest('.checkbox-inline').show();
          if (false === $('#enableResourceView').is(':checked')) {
            $('#enableResourceView').trigger('click');
          }
          $('#enableResourceView').next('div').find('input[type=radio]').trigger('click');
          $('#enableCalendarView').attr('disabled', 'disabled').prop('checked', false).trigger('change').closest('.checkbox-inline').hide();
        } else {
          $('#enableResourceView').removeAttr('disabled').closest('.checkbox-inline').show();
          $('#enableCalendarView').removeAttr('disabled').closest('.checkbox-inline').show();
        }
      })
      .trigger('change');
    // 启用视图
    var $enableView = $('input[name=enableView]', $container);
    configuration.enableView &&
      $enableView.each(function (idx, element) {
        $(element).prop('checked', $.inArray(element.value, configuration.enableView) > -1);
      });
    $enableView
      .on('change', function (event) {
        var $this = $(this),
          $providerId,
          $tabId;
        var $radio = $this.next('div').find('input[type=radio]');
        if ($this.prop('checked')) {
          $radio.removeAttr('disabled');
        } else {
          $radio.attr('disabled', 'disabled');
        }
        if ($this.val() === 'enableCalendarView') {
          $providerId = $('#dataProviderId');
          $tabId = $('a[href=#widget_fullcalendar_tabs_calendar_info]');
        } else {
          $providerId = $('#resProviderId');
          $tabId = $('a[href=#widget_fullcalendar_tabs_resource_info]');
          if ($this.prop('checked')) {
            $tabId.closest('[role=presentation]').show();
            $providerId.closest('.black').show();
          } else {
            $tabId.closest('[role=presentation]').hide();
            $providerId.closest('.black').hide();
          }
        }
        var checkLength = $enableView.filter(':checked').length;
        if (checkLength === 0) {
          $('input[name=defaultEnableView]').prop('checked', false);
        } else if (checkLength === 1) {
          $('input[name=defaultEnableView][value=' + $enableView.filter(':checked').val() + ']').prop('checked', true);
        } else if ($('input[name=defaultEnableView]:checked').length <= 0) {
          $('input[name=defaultEnableView]:first').prop('checked', true);
        }
      })
      .trigger('change');
  };
  // 初始化日历和资源视图
  configurer.prototype.initCalendarAndResource = function (configuration, $container) {
    // 展示系统工作时间
    server.JDS.call({
      service: 'workHourFacadeService.listCurrentUnitWorkHours',
      async: false,
      success: function (result) {
        if (result.success && result.data && result.data.length > 0) {
          var workhour = result.data[0];
          _minTime = workhour.fromTime1;
          _maxTime = workhour.toTime2;
          $('.currentWorkHour', $container).html(_minTime + ' - ' + _maxTime);
        }
      }
    });
    this.initCalendarInfo(configuration.calendar, $container);
    this.initResourceInfo(configuration.resource, $container);
  };

  configurer.prototype.openCalendarStatusColorSettingDialog = function ($eleButton, closeCallback) {
    // 日历状态颜色设置html
    var calendarStatusColorSettingHtml = `<div class="business-box">
    <div class="business-box-left">
        <div class="business-left-list"></div>
        <!--<div class="business-box-btn">
            <button id="addBusinessBtn" type="button" class="well-btn w-btn-primary">添加</button>
        </div>-->
    </div>
    <div class="business-box-right">

        <form action="" class="" style="margin-top: 15px;padding:0 30px;box-sizing: border-box;">

            <div class="form-group formbuilder clear col-xs-12 ">
                <label
                        class="col-xs-2 control-label control-label-module_code label-filed label-formbuilder"
                      style="text-align: center;" for="module_code">模块编码<font color="red" size="2">*</font></label>
                <div class="col-xs-4 controls">
                    <input type="text" style="width:100%"
                           class="form-control form-control-module_code w-itemform-option"
                           name="module_code" id="module_code" value="" placeholder="">
                </div>

                <label
                        class="col-xs-2 control-label control-label-type label-filed label-formbuilder"
                       style="text-align: center;" for="type">类型<font color="red" size="2">*</font></label>
                <div class="col-xs-4 controls">
                    <input type="text" style="width:100%"
                           class="form-control form-control-type w-itemform-option"
                           name="type" id="type" value="" placeholder="">
                </div>
            </div>
            <ul class="form-table">
                <li>
                    <div class="row">
                        <button id="btn_cal_color_add" type="button" data-apply="1"
                                class="well-btn w-btn-primary btn-add-rows"><i class="glyphicon glyphicon-plus"></i>新增
                        </button>
                        <button id="btn_cal_color_del" type="button" class="well-btn w-btn-danger btn-del-rows"><i
                                class="glyphicon glyphicon-trash"></i>删除
                        </button>
                    </div>
                    <table id="calendarStatusColorSettingButtonList"
                           class="calendarStatusColorSettingButtonList"></table>
                </li>
            </ul>
        </form>
        <div class="business-box-btn">
            <button id="btn_cal_color_save" type="button" class="well-btn w-btn-primary">保存</button>
        </div>
    </div>
</div>`;

    // 状态颜色管理 按钮
    $eleButton.off('click').on('click', function () {
      var $businessDialog = appModal.dialog({
        message: calendarStatusColorSettingHtml,
        title: '状态颜色管理',
        size: 'large',
        height: 620,
        width: 980,
        zIndex: 10001,
        shown: function ($dialog) {
          dialogInit();

          function dialogValidAndGet() {
            var error = '';

            var $moduleCode = $dialog.find('input[name="module_code"]');
            var $type = $dialog.find('input[name="type"]');
            var $table = $dialog.find('.calendarStatusColorSettingButtonList');
            var moduleCode = $moduleCode.val();
            var type = $type.val();

            var dataList = $table.bootstrapTable('getData');
            if (!moduleCode) {
              error += '模块编码不为空' + '<br/>';
            }
            if (!type) {
              error += '类型不为空' + '<br/>';
            }

            if (dataList.length === 0) {
              error += '配置不能为空' + '<br/>';
            }

            $.each(dataList, function (i, datum) {
              var rowError = '';

              if (!datum.id) {
                rowError += '编号不能为空；';
              }
              if (!datum.name) {
                rowError += '状态名不能为空；';
              }

              if (!datum.color) {
                rowError += '颜色不能为空；';
              }

              if (rowError) {
                error += '第' + (i + 1) + '行：' + rowError + '<br/>';
              }
            });

            dataList = JSON.parse(JSON.stringify(dataList));

            $.each(dataList, function (i, datum) {
              if (datum.color && datum.color.iconColor) {
                datum.color = datum.color.iconColor;
              }
            });

            return {
              moduleCode: moduleCode,
              type: type,
              valueList: dataList,
              error: error
            };
          }

          function dialogInit() {
            var $table = $dialog.find('.calendarStatusColorSettingButtonList');

            $dialog
              .find('.bootbox-close-button')
              .off('click')
              .on('click', function () {
                if (closeCallback) {
                  closeCallback();
                }
              });

            $dialog
              .find('#btn_cal_color_add')
              .off('click')
              .on('click', function () {
                var bean = {
                  id: null,
                  name: null,
                  color: null,
                  _id: Math.random().toString(16).substring(2)
                };
                var count = $table.bootstrapTable('getData').length;
                $table.bootstrapTable('insertRow', {
                  index: count,
                  row: bean
                });
              });

            $dialog
              .find('#btn_cal_color_del')
              .off('click')
              .on('click', function () {
                // 删除行数据
                var selectionRows = $table.bootstrapTable('getSelections');
                if (selectionRows.length == 0) {
                  appModal.error('请选择记录!');
                  return;
                }

                var fieldsVal = [];
                for (var i = 0; i < selectionRows.length; i++) {
                  selectionRows[i].rowStatus = 'deleted';
                  fieldsVal.push(selectionRows[i]['_id']);
                }

                $table.bootstrapTable('remove', {
                  field: '_id',
                  values: fieldsVal
                });
              });
            $dialog
              .find('#btn_cal_color_save')
              .off('click')
              .on('click', function () {
                var saveData = dialogValidAndGet();
                if (saveData.error) {
                  appModal.error(saveData.error);
                  return;
                }
                $.ajax({
                  url: ctx + '/api/webapp/color/setting/saveBean',
                  type: 'POST',
                  data: JSON.stringify(saveData),
                  dataType: 'json',
                  contentType: 'application/json',
                  success: function (result) {
                    //
                    dialogInitLeftList();
                    appModal.info('保存成功');
                  }
                });
              });

            dialogInitLeftList();
            _dialogSetValueByData('', '', []);
          }

          function _dialogSetValueByData(moduleCode, type, valueList) {
            if (!moduleCode || !type) {
              moduleCode = moduleCode ? moduleCode : '';
              type = type ? type : '';
            }

            var $moduleCode = $dialog.find('input[name="module_code"]');
            var $type = $dialog.find('input[name="type"]');
            $moduleCode.val(moduleCode);
            $type.val(type);
            dialogInitTable(valueList);
          }

          function dialogInitTable(valueList) {
            valueList = valueList && valueList.length ? valueList : [];

            $.each(valueList, function (i, datum) {
              datum._id = Math.random().toString(16).substring(2);
              datum.color = {
                className: '',
                iconColor: datum.color
              };
            });

            var $table = $dialog.find('.calendarStatusColorSettingButtonList');
            $table.bootstrapTable('destroy').bootstrapTable({
              data: valueList,
              idField: 'uuid',
              striped: true,
              width: 500,
              // onEditableHidden: onEditHidden,
              // toolbar: $('#div_calendar_status_toolbar', $container),
              columns: [
                {
                  field: 'checked',
                  checkbox: true,
                  formatter: checkedFormat
                },
                {
                  field: 'name',
                  title: '状态名',
                  editable: {
                    type: 'text',
                    mode: 'inline',
                    showbuttons: false,
                    onblur: 'submit',
                    validate: function (value) {
                      if (StringUtils.isBlank(value)) {
                        return '请输入名称!';
                      }
                    }
                  }
                },
                {
                  field: 'id',
                  title: '编号',
                  editable: {
                    type: 'text',
                    mode: 'inline',
                    showbuttons: false,
                    onblur: 'submit',
                    validate: function (value) {
                      if (StringUtils.isBlank(value)) {
                        return '请输入编号!';
                      }
                    }
                  }
                },
                {
                  field: 'color',
                  title: '颜色',
                  width: 150,
                  editable: {
                    onblur: 'cancel',
                    type: 'wCustomForm',
                    placement: 'left',
                    savenochange: true,
                    validate: function (value) {
                      if (StringUtils.isBlank(value)) {
                        return '请输入颜色!';
                      }
                    },
                    value2input: designCommons.bootstrapTable.color.value2inputFactory([
                      '#488cee',
                      '#bfe7f5',
                      '#e33033',
                      '#ff6000',
                      '#e99f00',
                      '#3aa322'
                    ]),
                    input2value: designCommons.bootstrapTable.color.input2value,
                    value2display: designCommons.bootstrapTable.color.value2display,
                    value2html: designCommons.bootstrapTable.color.value2html
                  }
                }
              ]
            });
          }

          function dialogInitLeftList() {
            var beanValueList = [];
            $.ajax({
              type: 'GET',
              url: '/api/webapp/color/setting/getAllBean',
              dataType: 'json',
              async: false,
              success: function (result) {
                beanValueList = result.data;
              }
            });

            var $businessLeftList = $dialog.find('.business-left-list');
            $businessLeftList.html('');

            var valueHashArr = [];
            var $businessLeftListHtml = '';
            $.each(beanValueList, function (i, datum) {
              var valueHash = datum.moduleCode + '_' + datum.type;
              if (valueHashArr.indexOf(valueHash) >= 0) {
                return;
              }
              valueHashArr.push(valueHash);
              var itemHtml = `<div class="business-left-item " module-code="${datum.moduleCode}" type-value="${datum.type}">
                    <span class="business-left-name">
                        <!--<i class="iconfont icon-ptkj-tuodong"></i>-->
                        <span>${valueHash}</span>
                    </span>
                    <i class="iconfont icon-ptkj-shanchu itemDel"></i>
                </div>`;
              $businessLeftListHtml += itemHtml;
            });
            $businessLeftList.html($businessLeftListHtml);

            $businessDialog.off('click', '.business-left-item').on('click', '.business-left-item', function () {
              $(this).addClass('active').siblings().removeClass('active');
              var moduleCode = $(this).attr('module-code');
              var type = $(this).attr('type-value');
              var valueList = [];
              $.each(beanValueList, function (i, datum) {
                var valueHash = datum.moduleCode + '_' + datum.type;
                if (datum.moduleCode === moduleCode && datum.type === type) {
                  valueList = JSON.parse(JSON.stringify(datum.valueList));
                }
              });
              _dialogSetValueByData(moduleCode, type, valueList);
              // $('.business-table-item', $businessDialog).find('.error').hide();
            });

            // 删除交换业务
            $businessDialog.off('click', '.itemDel').on('click', '.itemDel', function (e) {
              e.stopPropagation();

              var moduleCode = $(this).parent().attr('module-code');
              var type = $(this).parent().attr('type-value');

              appModal.confirm('确定要删除所选记录吗？', function (res) {
                if (res) {
                  // 删除 ajax
                  $.ajax({
                    url: ctx + `/api/webapp/color/setting/deleteBean?moduleCode=${moduleCode}&type=${type}`,
                    type: 'POST',
                    dataType: 'json',
                    contentType: 'application/json',
                    success: function (result) {
                      dialogInitLeftList();

                      appModal.info('删除成功');
                    }
                  });
                }
              });
            });
          }
        },
        buttons: {
          close: {
            label: '关闭',
            className: 'btn btn-default',
            callback: function () {
              if (closeCallback) {
                closeCallback();
              }
            }
          }
        }
      });
    });
  };

  // 初始化日历视图
  configurer.prototype.initCalendarInfo = function (calendar, $container) {
    calendar = calendar || {};
    designCommons.setElementValue(calendar, $('#widget_fullcalendar_tabs_calendar_info', $container), ['enableCalendar']);
    var $enableView = $('input[name=enableCalendar]', $container);
    calendar.enableCalendar &&
      $enableView.each(function (idx, element) {
        $(element).prop('checked', $.inArray(element.value, calendar.enableCalendar) > -1);
      });

    $enableView
      .on('change', function (event) {
        var $this = $(this),
          $default;
        var $radio = $this.next('div').find('input[type=radio]');
        if ($this.prop('checked')) {
          $radio.removeAttr('disabled');
        } else {
          $radio.attr('disabled', 'disabled');
        }
        var checkLength = $enableView.filter(':checked').length;
        if (checkLength === 0) {
          $('input[name=defaultCalendarView]').prop('checked', false);
        } else if (
          ($default = $('input[name=defaultCalendarView]:checked')).length <= 0 ||
          $enableView.filter('[value=' + $default.val() + ']').prop('checked') === false
        ) {
          $('input[name=defaultCalendarView][value=' + $($enableView.filter(':checked')[0]).val() + ']').prop('checked', true);
        }
      })
      .trigger('change');
    $container
      .find('input[name=calendarDayTime]')
      .on('change', function (event) {
        var value = this.value;
        $('.calendarDayTimeCustom,.calendarDayTimeSystem').hide();
        if ('custom' === value) {
          $('.calendarDayTimeCustom').show();
        } else if ('system' === value) {
          $('.calendarDayTimeSystem').show();
        }
      })
      .filter('[value=' + (calendar.calendarDayTime || 'custom') + ']')
      .prop('checked', true)
      .trigger('change');
    $container
      .find('select[name=calendarGradu]')
      .wSelect2({
        width: '100%'
      })
      .val(calendar.calendarGradu || '30')
      .trigger('change');

    function getTimeTextBySliderValue(val) {
      if (val % 2) {
        return [prefixZero((val - 1) / 2), 30].join(':');
      } else {
        return [prefixZero(val / 2), prefixZero(0)].join(':');
      }
    }

    var $calendarAmount = $('#calendarDayTimeArray_amount');
    var $calendarSlider = $('#calendarDayTimeArray_slider');

    var calendarDayTimeArray = calendar.calendarDayTimeArray;
    var beginTime = '8:00'; // 系统默认开始时间
    var endTime = '18:00'; // 系统默认结束时间

    if (typeof calendarDayTimeArray === 'string' && calendarDayTimeArray.indexOf(' - ') > 0) {
      var calendarDayTimeArrays = calendarDayTimeArray.split(' - ');
      beginTime = calendarDayTimeArrays[0];
      endTime = calendarDayTimeArrays[1];
    }

    var beginHour = moment(beginTime, 'mm:ss').format('m');
    var beginMinute = moment(beginTime, 'mm:ss').format('s');
    var endHour = moment(endTime, 'mm:ss').format('m');
    var endMinute = moment(endTime, 'mm:ss').format('s');

    $calendarSlider.slider({
      range: true,
      min: 0,
      max: 48,
      values: [beginHour * 2 + (beginMinute % 29), endHour * 2 + (endMinute % 29)],
      slide: function (event, ui) {
        if (ui.values[0] == ui.values[1]) {
          return false;
        }

        var timeRangeTxt = StringUtils.format('${startTime} - ${endTime}', {
          startTime: getTimeTextBySliderValue(ui.values[0]),
          endTime: getTimeTextBySliderValue(ui.values[1])
        });

        $calendarAmount.val(timeRangeTxt);
      }
    });

    var timeRangeTxt = StringUtils.format('${startTime} - ${endTime}', {
      startTime: [prefixZero(beginHour), prefixZero(beginMinute % 29 ? 30 : 0)].join(':'),
      endTime: [prefixZero(endHour), prefixZero(endMinute % 29 ? 30 : 0)].join(':')
    });

    $calendarAmount.val(timeRangeTxt);

    // 事项标题
    function get_title_expression() {
      return (
        '<div class="title_expression_wrap">' +
        '<div class="tip">' +
        '<i class="iconfont icon-ptkj-xinxiwenxintishi"></i>' +
        '<span>' +
        '在下方编辑事项标题表达式，可插入事项相关变量和文本。' +
        '</span>' +
        '</div>' +
        '<div class="content">' +
        '<div class="choose-btns clear">' +
        '<div id="formField" class="choose-item well-select"><span>插入事项变量</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia well-select-arrow" style="display:none;"></i></div>' +
        '<textarea id="titleControl" class="form-control" rows="10"></textarea>' +
        '</div>' +
        '<div class="bootom-tip">样例：${开始时间}-${结束时间}  ${日程标题}</div>' +
        '</div>'
      );
    }

    function setTitle_expressionSelect($dialog, id, showSearch, data) {
      $dialog
        .find('#' + id)
        .append(
          '<div class="well-select-dropdown" x-placement="bottom-start"' +
            '    style="position: absolute; left: 16px; top: 60px; width: 300px;">' +
            '    <div class="well-select-search" style="display: ' +
            (showSearch ? 'block' : 'none') +
            ';"><input class="well-select-input" placeholder="搜索">' +
            '        <div class="search-icon"><i class="iconfont icon-ptkj-sousuochaxun"></i></div>' +
            '    </div>' +
            '    <ul class="well-select-not-found" style="display: none;">' +
            '        <li>无匹配数据</li>' +
            '    </ul>' +
            '    <ul class="well-select-dropdown-list"></ul>' +
            '</div>'
        );

      if (id == 'formField') {
        $.each(data, function (i, item) {
          $dialog
            .find('#' + id + ' .well-select-dropdown-list')
            .append(
              '<li class="well-select-item" data-name="' +
                item.title +
                '" data-value="' +
                item.id +
                '"><span>' +
                item.title +
                '</span></li>'
            );
        });
      }
    }

    $('#calendarTitleBtn').on('click', function (event) {
      var $dialog = appModal.dialog({
        title: '事项标题设置',
        message: get_title_expression(),
        size: 'large',
        height: 500,
        shown: function () {
          $('textarea', $dialog)
            .off()
            .on('input propertychange', function () {
              var $that = $(this);
              var val = $('textarea', $dialog).val();
              if (val) {
                $('.bootom-tip', $dialog).hide();
              } else {
                $('.bootom-tip', $dialog).show();
              }
            });
          $('textarea', $dialog).val($('#calendarTitle').val());
          if ($('#calendarTitle').val()) {
            $('.bootom-tip', $dialog).hide();
          } else {
            $('.bootom-tip', $dialog).show();
          }
          var columnNames = loadColumnNames();
          setTitle_expressionSelect($dialog, 'formField', true, columnNames);

          $(top.document).on('click', function (e) {
            if ($('.choose-item', $dialog)[0] === $(e.target).parents('.well-select')[0]) return;
            $('.choose-item', $dialog).removeClass('well-select-visible');
          });

          $('.choose-item', $dialog)
            .off()
            .on('click', function (e) {
              var $this = $(this);
              e.stopPropagation();
              $this.toggleClass('well-select-visible');
              $('.choose-item', $dialog).each(function () {
                var _$this = $(this);
                if (!_$this.is($this)) {
                  _$this.removeClass('well-select-visible');
                }
              });
            });

          $('.well-select-input', $dialog)
            .off()
            .on('input propertychange', function () {
              var $that = $(this);
              var keyword = $.trim($that.val()).toUpperCase();
              var $wellSelect = $that.closest('.well-select');
              var $wellSelectItem = $wellSelect.find('.well-select-item');
              var $wellSelectNotFound = $wellSelect.find('.well-select-not-found');
              var showNum = 0;
              $wellSelectItem.each(function () {
                var $this = $(this);
                var value = $this.data('value').toString();
                var name = $this.data('name').toString();
                if (value.toUpperCase().indexOf(keyword) > -1 || name.toUpperCase().indexOf(keyword) > -1) {
                  $this.show();
                  showNum++;
                } else {
                  $this.hide();
                }
              });
              if (showNum) {
                $wellSelectNotFound.hide();
              } else {
                $wellSelectNotFound.show();
              }
            });
          $('.well-select-dropdown', $dialog).on('click', function (e) {
            e.stopPropagation();
          });
          $('.well-select-item', $dialog).on('click', function (e) {
            var $titleControl = $('#titleControl', $dialog)[0];
            var value = $titleControl.value;
            var start = $titleControl.selectionStart;
            var value1 = value.substr(0, start);
            var value2 = value.substr(start);
            var id = $(this).parents('.well-select').attr('id');
            if (id == 'formField') {
              var content = '${' + $(this).attr('data-name') + '(' + $(this).attr('data-value') + ')}';
            }
            var finalValue = value1 + content + value2;
            $('textarea', $dialog).val(finalValue);
            if (finalValue) {
              $('.bootom-tip', $dialog).hide();
            } else {
              $('.bootom-tip', $dialog).show();
            }
            $('.choose-item', $dialog).removeClass('well-select-visible');
          });
        },
        buttons: {
          ok: {
            label: '保存',
            className: 'well-btn w-btn-primary',
            callback: function () {
              $('#calendarTitle').val($('textarea', $dialog).val());
            }
          },
          cancel: {
            label: '关闭',
            className: 'btn-default'
          }
        }
      });
    });

    //事项状态
    // var status = calendar.status;
    // 按钮定义
    var $calendarStatusTable = $('#table_calendar_status_info', $container);
    var $calendarStatusKey = $('#calendarStatusKey', $container);
    var $calendarStatusColorSettingButton = $('#calendar-status-color-setting-button', $container);

    $calendarStatusTable.bootstrapTable('destroy').bootstrapTable({
      data: [],
      idField: 'uuid',
      striped: true,
      width: 500,
      editable: false,
      onEditableHidden: onEditHidden,
      toolbar: $('#div_calendar_status_toolbar', $container),
      columns: [
        {
          field: 'checked',
          checkbox: true,
          formatter: checkedFormat
        },
        {
          field: 'name',
          title: '状态名',
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入名称!';
              }
            }
          }
        },
        {
          field: 'id',
          title: '编号',
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入编号!';
              }
            }
          }
        },
        {
          field: 'color',
          title: '状态颜色',
          width: 150,
          formatter: function (value) {
            return `<span class="" aria-hidden="true" style="width: 18px;height: 18px;display: inline-block;background-color:${value.iconColor};"></span>`;
          }
        }
      ]
    });

    var initCalendarStatusKeySelect = function () {
      var beanValueList = [];
      $.ajax({
        type: 'GET',
        url: '/api/webapp/color/setting/getAllBean',
        dataType: 'json',
        async: false,
        success: function (result) {
          beanValueList = result.data;
        }
      });

      var calendarStatusKeyVals = $.map(beanValueList, function (elem, idx) {
        return '<option class="" value="' + elem.moduleCode + '_' + elem.type + '">' + elem.moduleCode + '_' + elem.type + '</option>';
      });
      if ($calendarStatusKey.val()) {
        calendar.calendarStatusKey = $calendarStatusKey.val();
      }
      $calendarStatusKey.html(calendarStatusKeyVals.join('')).val(calendar.calendarStatusKey).wSelect2('reRenderOption');

      function calendarStatusKeyChange() {
        var colorVals = [];
        var calendarStatusKey = $calendarStatusKey.val();
        $.each(beanValueList, function (i, datum) {
          if (calendarStatusKey === datum.moduleCode + '_' + datum.type) {
            colorVals = datum.valueList;
          }
        });
        colorVals = JSON.parse(JSON.stringify(colorVals));
        $.each(colorVals, function (i, datum) {
          datum._id = Math.random().toString(16).substring(2);
          datum.color = {
            className: '',
            iconColor: datum.color
          };
        });
        $calendarStatusTable.bootstrapTable('removeAll').bootstrapTable('load', colorVals);
      }

      $calendarStatusKey.on('change', calendarStatusKeyChange).wSelect2({
        placeholder: '请选择'
      });

      calendarStatusKeyChange();
    };

    this.openCalendarStatusColorSettingDialog($calendarStatusColorSettingButton, initCalendarStatusKeySelect);

    initCalendarStatusKeySelect();

    //事件设置
    var events = calendar.events
      ? calendar.events
      : [
          {
            code: 'panel.click',
            text: '日历面板单击事件'
          },
          {
            code: 'event.click',
            text: '日历事项查看事件'
          }
        ];
    // 按钮定义
    var $calendarEventsTable = $('#table_calendar_events_info', $container);
    $calendarEventsTable.bootstrapTable('destroy').bootstrapTable({
      data: events,
      idField: 'uuid',
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      columns: [
        {
          field: 'checked',
          checkbox: true,
          formatter: checkedFormat
        },
        {
          field: 'code',
          title: 'code',
          visible: false
        },
        {
          field: 'text',
          title: '名称',
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入名称!';
              }
            }
          }
        },
        {
          field: 'target',
          title: '目标位置',
          width: 100,
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'left',
            savenochange: true,
            value2input: designCommons.bootstrapTable.targePosition.value2input,
            value2display: designCommons.bootstrapTable.targePosition.value2display,
            inputCompleted: designCommons.bootstrapTable.targePosition.inputCompleted
          }
        },
        {
          field: 'eventHandler',
          title: '事件逻辑',
          width: 150,
          editable: {
            mode: 'modal',
            container: 'body',
            onblur: 'ignore',
            type: 'wCustomForm',
            placement: 'left',
            savenochange: true,
            renderParams: {
              defineJs: false
            },
            value2input: designCommons.bootstrapTable.eventManager.value2input,
            input2value: designCommons.bootstrapTable.eventManager.input2value,
            validate: designCommons.bootstrapTable.eventManager.validate,
            value2display: designCommons.bootstrapTable.eventManager.value2display
          }
        }
      ]
    });
  };
  // 初始化资源视图
  configurer.prototype.initResourceInfo = function (resource, $container) {
    resource = resource || {};
    designCommons.setElementValue(resource, $('#widget_fullcalendar_tabs_resource_info', $container), ['enableResource']);
    var $enableView = $('input[name=enableResource]', $container);
    resource.enableResource &&
      $enableView.each(function (idx, element) {
        $(element).prop('checked', $.inArray(element.value, resource.enableResource) > -1);
      });
    $enableView
      .on('change', function (event) {
        var $this = $(this),
          $default;
        var $radio = $this.next('div').find('input[type=radio]');
        if ($this.prop('checked')) {
          $radio.removeAttr('disabled');
        } else {
          $radio.attr('disabled', 'disabled');
        }
        var checkLength = $enableView.filter(':checked').length;
        if (checkLength === 0) {
          $('input[name=defaultResourceView]').prop('checked', false);
        } else if (
          ($default = $('input[name=defaultResourceView]:checked')).length <= 0 ||
          $enableView.filter('[value=' + $default.val() + ']').prop('checked') === false
        ) {
          $('input[name=defaultResourceView][value=' + $($enableView.filter(':checked')[0]).val() + ']').prop('checked', true);
        }
      })
      .trigger('change');
    $container
      .find('input[name=resourceDayTime]')
      .on('change', function (event) {
        var value = this.value;
        $('.resourceDayTimeCustom,.resourceDayTimeSystem').hide();
        if ('custom' === value) {
          $('.resourceDayTimeCustom').show();
        } else if ('system' === value) {
          $('.resourceDayTimeSystem').show();
        }
      })
      .filter('[value=' + (resource.resourceDayTime || 'custom') + ']')
      .prop('checked', true)
      .trigger('change');
    $container
      .find('select[name=resourceGradu]')
      .wSelect2({
        width: '100%'
      })
      .val(resource.resourceGradu || '30')
      .trigger('change');

    function getTimeTextBySliderValue(val) {
      if (val % 2) {
        return [prefixZero((val - 1) / 2), 30].join(':');
      } else {
        return [prefixZero(val / 2), prefixZero(0)].join(':');
      }
    }

    //
    var $resourceAmount = $('#resourceDayTimeArray_amount');
    var $resourceSlider = $('#resourceDayTimeArray_slider');

    var resourceDayTimeArray = resource.resourceDayTimeArray;
    var beginTime = '8:00'; // 系统默认开始时间
    var endTime = '18:00'; // 系统默认结束时间

    if (typeof resourceDayTimeArray === 'string' && resourceDayTimeArray.indexOf(' - ') > 0) {
      var resourceDayTimeArrays = resourceDayTimeArray.split(' - ');
      beginTime = resourceDayTimeArrays[0];
      endTime = resourceDayTimeArrays[1];
    }

    var beginHour = moment(beginTime, 'mm:ss').format('m');
    var beginMinute = moment(beginTime, 'mm:ss').format('s');
    var endHour = moment(endTime, 'mm:ss').format('m');
    var endMinute = moment(endTime, 'mm:ss').format('s');

    $resourceSlider.slider({
      range: true,
      min: 0,
      max: 48,
      values: [beginHour * 2 + (beginMinute % 29), endHour * 2 + (endMinute % 29)],
      slide: function (event, ui) {
        if (ui.values[0] == ui.values[1]) {
          return false;
        }

        var timeRangeTxt = StringUtils.format('${startTime} - ${endTime}', {
          startTime: getTimeTextBySliderValue(ui.values[0]),
          endTime: getTimeTextBySliderValue(ui.values[1])
        });

        $resourceAmount.val(timeRangeTxt);
      }
    });

    var timeRangeTxt = StringUtils.format('${startTime} - ${endTime}', {
      startTime: [prefixZero(beginHour), prefixZero(beginMinute % 29 ? 30 : 0)].join(':'),
      endTime: [prefixZero(endHour), prefixZero(endMinute % 29 ? 30 : 0)].join(':')
    });

    $resourceAmount.val(timeRangeTxt);

    function get_title_expression() {
      return (
        '<div class="title_expression_wrap">' +
        '<div class="tip">' +
        '<i class="iconfont icon-ptkj-xinxiwenxintishi"></i>' +
        '<span>' +
        '在下方编辑事项标题表达式，可插入事项相关变量和文本。' +
        '</span>' +
        '</div>' +
        '<div class="content">' +
        '<div class="choose-btns clear">' +
        '<div id="formField" class="choose-item well-select"><span>插入事项变量</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia well-select-arrow" style="display:none;"></i></div>' +
        '<textarea id="titleControl" class="form-control" rows="10"></textarea>' +
        '</div>' +
        '<div class="bootom-tip">样例：${开始时间}-${结束时间}  ${日程标题}</div>' +
        '</div>'
      );
    }

    function setTitle_expressionSelect($dialog, id, showSearch, data) {
      $dialog
        .find('#' + id)
        .append(
          '<div class="well-select-dropdown" x-placement="bottom-start"' +
            '    style="position: absolute; left: 16px; top: 60px; width: 300px;">' +
            '    <div class="well-select-search" style="display: ' +
            (showSearch ? 'block' : 'none') +
            ';"><input class="well-select-input" placeholder="搜索">' +
            '        <div class="search-icon"><i class="iconfont icon-ptkj-sousuochaxun"></i></div>' +
            '    </div>' +
            '    <ul class="well-select-not-found" style="display: none;">' +
            '        <li>无匹配数据</li>' +
            '    </ul>' +
            '    <ul class="well-select-dropdown-list"></ul>' +
            '</div>'
        );

      if (id == 'formField') {
        $.each(data, function (i, item) {
          $dialog
            .find('#' + id + ' .well-select-dropdown-list')
            .append(
              '<li class="well-select-item" data-name="' +
                item.title +
                '" data-value="' +
                item.id +
                '"><span>' +
                item.title +
                '</span></li>'
            );
        });
      }
    }

    $('#resourceTitleBtn').on('click', function (event) {
      var $dialog = appModal.dialog({
        title: '资源占用标题设置',
        message: get_title_expression(),
        size: 'large',
        height: 500,
        shown: function () {
          $('textarea', $dialog)
            .off()
            .on('input propertychange', function () {
              var $that = $(this);
              var val = $('textarea', $dialog).val();
              if (val) {
                $('.bootom-tip', $dialog).hide();
              } else {
                $('.bootom-tip', $dialog).show();
              }
            });
          $('textarea', $dialog).val($('#resourceTitle').val());
          if ($('#resourceTitle').val()) {
            $('.bootom-tip', $dialog).hide();
          } else {
            $('.bootom-tip', $dialog).show();
          }
          var columnNames = loadDataStoreColumnNames('resProviderId');
          setTitle_expressionSelect($dialog, 'formField', true, columnNames);

          $(top.document).on('click', function (e) {
            if ($('.choose-item', $dialog)[0] === $(e.target).parents('.well-select')[0]) return;
            $('.choose-item', $dialog).removeClass('well-select-visible');
          });

          $('.choose-item', $dialog)
            .off()
            .on('click', function (e) {
              var $this = $(this);
              e.stopPropagation();
              $this.toggleClass('well-select-visible');
              $('.choose-item', $dialog).each(function () {
                var _$this = $(this);
                if (!_$this.is($this)) {
                  _$this.removeClass('well-select-visible');
                }
              });
            });

          $('.well-select-input', $dialog)
            .off()
            .on('input propertychange', function () {
              var $that = $(this);
              var keyword = $.trim($that.val()).toUpperCase();
              var $wellSelect = $that.closest('.well-select');
              var $wellSelectItem = $wellSelect.find('.well-select-item');
              var $wellSelectNotFound = $wellSelect.find('.well-select-not-found');
              var showNum = 0;
              $wellSelectItem.each(function () {
                var $this = $(this);
                var value = $this.data('value').toString();
                var name = $this.data('name').toString();
                if (value.toUpperCase().indexOf(keyword) > -1 || name.toUpperCase().indexOf(keyword) > -1) {
                  $this.show();
                  showNum++;
                } else {
                  $this.hide();
                }
              });
              if (showNum) {
                $wellSelectNotFound.hide();
              } else {
                $wellSelectNotFound.show();
              }
            });
          $('.well-select-dropdown', $dialog).on('click', function (e) {
            e.stopPropagation();
          });
          $('.well-select-item', $dialog).on('click', function (e) {
            var $titleControl = $('#titleControl', $dialog)[0];
            var value = $titleControl.value;
            var start = $titleControl.selectionStart;
            var value1 = value.substr(0, start);
            var value2 = value.substr(start);
            var id = $(this).parents('.well-select').attr('id');
            if (id == 'formField') {
              var content = '${' + $(this).attr('data-name') + '(' + $(this).attr('data-value') + ')}';
            }
            var finalValue = value1 + content + value2;
            $('textarea', $dialog).val(finalValue);
            if (finalValue) {
              $('.bootom-tip', $dialog).hide();
            } else {
              $('.bootom-tip', $dialog).show();
            }
            $('.choose-item', $dialog).removeClass('well-select-visible');
          });
        },
        buttons: {
          ok: {
            label: '保存',
            className: 'well-btn w-btn-primary',
            callback: function () {
              $('#resourceTitle').val($('textarea', $dialog).val());
            }
          },
          cancel: {
            label: '关闭',
            className: 'btn-default'
          }
        }
      });
    });

    //启用资源详情查看
    $('#showResourceDetails')
      .on('change', function (event) {
        var $this = $(this);
        $('#div_resource_details')[$this.prop('checked') ? 'show' : 'hide']();
      })
      .trigger('change');
    var details = resource.detail;
    // 按钮定义
    var $resourceDetailTable = $('#table_resource_details_info', $container);
    var detailsRowBean = {
      text: '',
      icon: ''
    };
    //添加，删除，上移，下移4个按钮
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_resource_details_info', 'resource_details', $container, detailsRowBean);
    $resourceDetailTable.bootstrapTable('destroy').bootstrapTable({
      data: details,
      idField: 'uuid',
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      toolbar: $('#div_resource_details_toolbar', $container),
      columns: [
        {
          field: 'checked',
          checkbox: true,
          formatter: checkedFormat
        },
        {
          field: 'title',
          title: '标题',
          visible: false
        },
        {
          field: 'text',
          title: '资源详情展示信息',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: function () {
              // 资源详情
              return loadDataStoreColumnNames.call(this, 'resProviderId');
            }
          }
        },
        {
          field: 'icon',
          title: '图标',
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            iconSelectTypes: [3],
            value2input: designCommons.bootstrapTable.icon.value2input,
            input2value: designCommons.bootstrapTable.icon.input2value,
            value2display: designCommons.bootstrapTable.icon.value2display,
            value2html: designCommons.bootstrapTable.icon.value2html
          }
        }
      ]
    });
    $resourceDetailTable.closest('.bootstrap-table').css({
      'margin-top': '0px'
    });

    //事项状态
    // var status = resource.status;
    // 按钮定义
    var $resourceStatusTable = $('#table_resource_status_info', $container);
    var $resourceStatusKey = $('#resourceStatusKey', $container);
    var $resourceStatusColorSettingButton = $('#resource-status-color-setting-button', $container);

    $resourceStatusTable.bootstrapTable('destroy').bootstrapTable({
      data: [],
      idField: 'uuid',
      striped: true,
      width: 500,
      editable: false,
      onEditableHidden: onEditHidden,
      toolbar: $('#div_resource_status_toolbar', $container),
      columns: [
        {
          field: 'checked',
          checkbox: true,
          formatter: checkedFormat
        },
        {
          field: 'name',
          title: '状态名',
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入名称!';
              }
            }
          }
        },
        {
          field: 'id',
          title: '编号',
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入编号!';
              }
            }
          }
        },
        {
          field: 'color',
          title: '状态颜色',
          width: 150,
          formatter: function (value) {
            return `<span class="" aria-hidden="true" style="width: 18px;height: 18px;display: inline-block;background-color:${value.iconColor};"></span>`;
          }
        }
      ]
    });

    var initResourceStatusKeySelect = function () {
      var beanValueList = [];
      $.ajax({
        type: 'GET',
        url: '/api/webapp/color/setting/getAllBean',
        dataType: 'json',
        async: false,
        success: function (result) {
          beanValueList = result.data;
        }
      });

      var resourceStatusKeyVals = $.map(beanValueList, function (elem, idx) {
        return '<option class="" value="' + elem.moduleCode + '_' + elem.type + '">' + elem.moduleCode + '_' + elem.type + '</option>';
      });
      if ($resourceStatusKey.val()) {
        resource.resourceStatusKey = $resourceStatusKey.val();
      }
      $resourceStatusKey.html(resourceStatusKeyVals.join('')).val(resource.resourceStatusKey).wSelect2('reRenderOption');

      function resourceStatusKeyChange() {
        var colorVals = [];
        var resourceStatusKey = $resourceStatusKey.val();
        $.each(beanValueList, function (i, datum) {
          if (resourceStatusKey === datum.moduleCode + '_' + datum.type) {
            colorVals = datum.valueList;
          }
        });
        colorVals = JSON.parse(JSON.stringify(colorVals));
        $.each(colorVals, function (i, datum) {
          datum._id = Math.random().toString(16).substring(2);
          datum.color = {
            className: '',
            iconColor: datum.color
          };
        });
        $resourceStatusTable.bootstrapTable('removeAll').bootstrapTable('load', colorVals);
      }

      $resourceStatusKey.on('change', resourceStatusKeyChange).wSelect2({
        placeholder: '请选择'
      });

      resourceStatusKeyChange();
    };

    this.openCalendarStatusColorSettingDialog($resourceStatusColorSettingButton, initResourceStatusKeySelect);

    initResourceStatusKeySelect();

    //事件设置
    var events = resource.events
      ? resource.events
      : [
          {
            code: 'panel.click',
            text: '资源面板单击事件'
          },
          {
            code: 'event.click',
            text: '资源占用查看事件'
          }
        ];
    // 按钮定义
    var $resourceEventsTable = $('#table_resource_events_info', $container);
    $resourceEventsTable.bootstrapTable('destroy').bootstrapTable({
      data: events,
      idField: 'uuid',
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      columns: [
        {
          field: 'checked',
          checkbox: true,
          formatter: checkedFormat
        },
        {
          field: 'code',
          title: 'code',
          visible: false
        },
        {
          field: 'text',
          title: '名称',
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入名称!';
              }
            }
          }
        },
        {
          field: 'target',
          title: '目标位置',
          width: 100,
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'left',
            savenochange: true,
            value2input: designCommons.bootstrapTable.targePosition.value2input,
            value2display: designCommons.bootstrapTable.targePosition.value2display,
            inputCompleted: designCommons.bootstrapTable.targePosition.inputCompleted
          }
        },
        {
          field: 'eventHandler',
          title: '事件逻辑',
          width: 150,
          editable: {
            mode: 'modal',
            onblur: 'ignore',
            type: 'wCustomForm',
            placement: 'left',
            savenochange: true,
            renderParams: {
              defineJs: false
            },
            value2input: designCommons.bootstrapTable.eventManager.value2input,
            input2value: designCommons.bootstrapTable.eventManager.input2value,
            validate: designCommons.bootstrapTable.eventManager.validate,
            value2display: designCommons.bootstrapTable.eventManager.value2display
          }
        }
      ]
    });
  };
  // 初始化'列定义'页签数据
  configurer.prototype.initColumnTable = function (columns, $container) {
    var columnsData = columns ? columns : [];
    var $columnInfoTable = $('#table_column_info', $container);

    $columnInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: columnsData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      onEditableSave: function (field, row, oldValue, $el) {
        if (field == 'idField' && row[field] == '1') {
          var data = $columnInfoTable.bootstrapTable('getData');
          $.each(data, function (index, rowData) {
            if (row != rowData) {
              rowData.idField = 0;
              $columnInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
        if (field == 'name') {
          var rowDatas = $columnInfoTable.bootstrapTable('getData');
          $.each(rowDatas, function (index, rowData) {
            if (row == rowData) {
              $.each(loadColumnNames(), function (index, val) {
                if (val.value == row.name) {
                  rowData.title = val.title;
                  rowData.dataType = val.dataType;
                }
              });
              $columnInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
      },
      toolbar: $('#div_column_toolbar', $container),
      columns: [
        {
          field: 'checked',
          formatter: checkedFormat,
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'title',
          title: '标题',
          editable: {
            type: 'text',
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline'
          }
        },
        {
          field: 'name',
          title: '字段名',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: loadColumnNames
          }
        },
        // {
        // 	field : "width",
        // 	title : "列宽",
        // 	editable : {
        // 		type : "text",
        // 		showbuttons : false,
        // 		onblur : "submit",
        // 		mode : "inline",
        // 		validate : function(value) {
        // 			if (StringUtils.isNotBlank(value)) {
        // 				var regu = "^(([1-9][0-9]*)|([1-9][0-9]*\.[0-9]+)|([0]\.[0-9]+))$";
        // 				var re = new RegExp(regu);
        // 				if (!re.test(value)) {
        // 					return '请输入正确的数字!';
        // 				}
        // 			}
        // 		}
        // 	}
        // },
        {
          field: 'controlOptions',
          title: '控件类型',
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            value2input: designCommons.bootstrapTable.queryFieldType.value2input,
            input2value: designCommons.bootstrapTable.queryFieldType.input2value,
            value2display: designCommons.bootstrapTable.queryFieldType.value2display,
            validate: function (value) {
              if (!value || !value.queryType) {
                return '请选择控件类型!';
              }
            }
          }
        },
        {
          field: 'validateRules',
          title: '校验规则',
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            value2input: designCommons.bootstrapTable.validateRule.value2input,
            input2value: designCommons.bootstrapTable.validateRule.input2value,
            value2display: designCommons.bootstrapTable.validateRule.value2display,
            validate: function (value) {}
          }
        },
        {
          field: 'defaultValue',
          title: '默认值',
          editable: {
            onblur: 'submit',
            type: 'text',
            mode: 'inline',
            showbuttons: false
          }
        },
        {
          field: 'hidden',
          title: '隐藏',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: [
              {
                value: '1',
                text: '是'
              },
              {
                value: '0',
                text: '否'
              }
            ]
          }
        },
        {
          field: 'resourceDetailHidden',
          title: '资源视图详情隐藏',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: [
              {
                value: '1',
                text: '是'
              },
              {
                value: '0',
                text: '否'
              }
            ]
          }
        },
        {
          field: 'keywordQuery',
          title: '参与关键字查询',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: [
              {
                value: '0',
                text: '否'
              },
              {
                value: '1',
                text: '是'
              }
            ]
          }
        }
      ]
    });
  };

  // 初始化列定义页签
  configurer.prototype.initColumnInfo = function (columns, $container) {
    //定义添加，删除，上移，下移4按钮事件
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_column_info', 'column', $container, columnRowBean);
    //初始化列定义表格
    this.initColumnTable(columns);
  };

  // 初始化'查询定义'页签
  configurer.prototype.initQueryInfo = function (queryConfiguration, $container) {
    var fieldData = [];
    if (queryConfiguration) {
      if (queryConfiguration.fields) {
        fieldData = queryConfiguration.fields;
      }
      designCommons.setElementValue(queryConfiguration, $container);
      if (queryConfiguration.fieldRowColumns) {
        $container.find('#fieldRowColumns').val(queryConfiguration.fieldRowColumns);
      }
    }
    // 查询
    // 按字段查询
    var $fieldSearchInfo = $('#div_field_search_info', $container);
    var $fieldSearchInfoTable = $('#table_field_search_info', $container);
    $('#fieldSearch', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $fieldSearchInfo.show();
        } else {
          $fieldSearchInfo.hide();
          $fieldSearchInfoTable.bootstrapTable('removeAll');
        }
      })
      .trigger('change');

    var columnRowBean = {
      checked: false,
      uuid: '',
      name: '',
      label: '',
      defaultValue: '',
      queryOptions: {
        queryTypeLabel: '文本框',
        queryType: 'text'
      },
      operator: 'eq'
    };
    //定义添加，删除，上移，下移4按钮事件
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_field_search_info', 'field', $container, columnRowBean);

    var $columnInfoTable = $('#table_column_info', $container);
    var operatorSource = loadOperator();
    $fieldSearchInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: fieldData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      toolbar: $('#div_field_search_info_toolbar', $container),
      columns: [
        {
          field: 'checked',
          formatter: checkedFormat,
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'label',
          title: '标题',
          editable: {
            type: 'text',
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline'
          }
        },
        {
          field: 'name',
          title: '字段名',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            emptytext: '请选择',
            source: function () {
              var columns = $columnInfoTable.bootstrapTable('getData');
              return $.map(columns, function (column) {
                return {
                  value: column.name,
                  text: column.title
                };
              });
            }
          }
        },
        {
          field: 'queryOptions',
          title: '查询类型',
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            value2input: designCommons.bootstrapTable.queryFieldType.value2input,
            input2value: designCommons.bootstrapTable.queryFieldType.input2value,
            value2display: designCommons.bootstrapTable.queryFieldType.value2display,
            validate: function (value) {
              if (!value || !value.queryType) {
                return '请选择查询类型!';
              }
            }
          }
        },
        {
          field: 'defaultValue',
          title: '默认值',
          editable: {
            onblur: 'submit',
            type: 'text',
            mode: 'inline',
            showbuttons: false
          }
        },
        {
          field: 'operator',
          title: '操作符',
          editable: {
            type: 'select',
            mode: 'inline',
            onblur: 'submit',
            showbuttons: false,
            source: operatorSource
          }
        }
      ]
    });
  };

  // 初始化'按钮定义'页签
  configurer.prototype.initButtonInfo = function (configuration, $container) {
    var buttonData = configuration.buttons ? configuration.buttons : [];
    var piUuid = this.component.pageDesigner.getPiUuid();
    var system = appContext.getCurrentUserAppData().getSystem();
    var productUuid = system.productUuid;
    if (StringUtils.isNotBlank(system.piUuid)) {
      piUuid = system.piUuid;
    }
    // 按钮定义
    var $buttonInfoTable = $('#table_button_info', $container);
    var columnRowBean = {
      checked: false,
      uuid: '',
      code: '',
      text: '',
      //position : [ '1' ],
      group: '',
      cssClass: 'btn-default'
    };
    //添加，删除，上移，下移4个按钮
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_button_info', 'button', $container, columnRowBean);

    $buttonInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: buttonData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      toolbar: $('#div_button_info_toolbar', $container),
      columns: [
        {
          field: 'checked',
          checkbox: true,
          formatter: checkedFormat
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'text',
          title: '名称',
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入名称!';
              }
            }
          }
        },
        {
          field: 'code',
          title: '编码',
          width: 80,
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            savenochange: true,
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入编码!';
              }
            }
          }
        },
        {
          field: 'group',
          title: '组别',
          width: 100,
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit'
          }
        },
        {
          field: 'btnLib',
          title: '按钮库',
          width: 100,
          editable: {
            onblur: 'save',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            value2input: designCommons.bootstrapTable.btnLib.value2input,
            input2value: designCommons.bootstrapTable.btnLib.input2value,
            value2display: designCommons.bootstrapTable.btnLib.value2display,
            value2html: designCommons.bootstrapTable.btnLib.value2html
          }
        },
        {
          field: 'resource',
          title: '资源',
          width: 100,
          editable: {
            type: 'wCommonComboTree',
            mode: 'modal',
            container: 'body',
            placement: 'bottom',
            showbuttons: false,
            onblur: 'submit',
            wCommonComboTree: {
              inlineView: true,
              service: 'appProductIntegrationMgr.getTreeNodeByUuid',
              serviceParams: [piUuid, [], ['BUTTON']],
              multiSelect: false
              // 是否多选
            }
          }
        },
        {
          field: 'target',
          title: '目标位置',
          width: 100,
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'left',
            savenochange: true,
            value2input: designCommons.bootstrapTable.targePosition.value2input,
            value2display: designCommons.bootstrapTable.targePosition.value2display,
            inputCompleted: designCommons.bootstrapTable.targePosition.inputCompleted
          }
        },
        {
          field: 'eventHandler',
          title: '事件逻辑',
          width: 150,
          editable: {
            mode: 'modal',
            onblur: 'ignore',
            type: 'wCustomForm',
            placement: 'left',
            container: 'body',
            savenochange: true,
            renderParams: {
              defineJs: false
            },
            value2input: designCommons.bootstrapTable.eventManager.value2input,
            input2value: designCommons.bootstrapTable.eventManager.input2value,
            validate: designCommons.bootstrapTable.eventManager.validate,
            value2display: designCommons.bootstrapTable.eventManager.value2display
          }
        }
      ]
    });
  };

  // 初始化'高级配置'页签
  configurer.prototype.initSeniorInfo = function (seniorConfiguration, $container) {
    var seniorConfiguration = seniorConfiguration ? seniorConfiguration : [];
    designCommons.setElementValue(seniorConfiguration, $container);
  };

  // 初始化'事项表单模板'页签
  configurer.prototype.initContentInfo = function (formModuleConfiguration, $container) {
    var formModuleConfiguration = formModuleConfiguration ? formModuleConfiguration : [];
    var component = this.component;
    var options = component.options || {};
    designCommons.setElementValue(formModuleConfiguration, $container);

    var content = formModuleConfiguration.formHtmlContent ? formModuleConfiguration.formHtmlContent : StringUtils.EMPTY;

    $('#wHtml_content').summernote({
      height: '300px',
      dialogsInBody: true
    });

    $('#wHtml_content').summernote('code', content);

    var $customHtml = $('#div_wHtml_content', $container);
    $('#enableCustomForm', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $customHtml.show();
        } else {
          $customHtml.hide();
          clearInputValue($customHtml);
        }
      })
      .trigger('change');
  };

  // 组件定义加载
  configurer.prototype.onLoad = function ($container, options) {
    // 初始化页签项
    $('#widget_fullcalendar_tabs ul a', $container).on('click', function (e) {
      e.preventDefault();
      $(this).tab('show');
      var panelId = $(this).attr('href');
      $(panelId + ' .definition_info').bootstrapTable('resetView');
    });
    console.log('初始化数据');
    console.log(options.configuration);
    var configuration = $.extend(true, {}, options.configuration);
    this.initBaseInfo(configuration, $container);
    this.initCalendarAndResource(configuration, $container);
    this.initColumnInfo(configuration.columns, $container);
    this.initQueryInfo(configuration.query, $container);
    this.initButtonInfo(configuration, $container);
    this.initSeniorInfo(configuration.seniorConfiguration, $container);
    this.initContentInfo(configuration.formModuleConfig, $container);
  };

  // 组件定义确认
  configurer.prototype.onOk = function ($container) {
    if (this.component.isReferenceWidget()) {
      return;
    }

    var $columnInfoTable = $('#table_column_info', $container);

    var opt = designCommons.collectConfigurerData($('#widget_fullcalendar_tabs_base_info', $container), collectClass);
    opt.calendar = designCommons.collectConfigurerData($('#widget_fullcalendar_tabs_calendar_info', $container), collectClass);
    var $tablecalendarStatusInfo = $('#table_calendar_status_info', $container);
    var $tablecalendarEventsInfo = $('#table_calendar_events_info', $container);
    opt.calendar.status = $tablecalendarStatusInfo.bootstrapTable('getData');
    opt.calendar.events = $tablecalendarEventsInfo.bootstrapTable('getData');
    if (opt.calendar.calendarDayTime === 'system') {
      opt.calendar.DayTimeArray = _minTime + ' - ' + _maxTime;
    }
    var allowCustomCalendarGradu = opt.calendar.allowCustomCalendarGradu || [];
    opt.calendar.allowCustomGradu = allowCustomCalendarGradu.length > 0 && allowCustomCalendarGradu[0] == 'true';
    var allowCustomCalendarDayTime = opt.calendar.allowCustomCalendarDayTime || [];
    opt.calendar.allowCustomDayTime = allowCustomCalendarDayTime.length > 0 && allowCustomCalendarDayTime[0] == 'true';
    opt.resource = designCommons.collectConfigurerData($('#widget_fullcalendar_tabs_resource_info', $container), collectClass);
    var $resourceDetailTable = $('#table_resource_details_info', $container);
    var $tableResourceStatusInfo = $('#table_resource_status_info', $container);
    var $tableResourceEventsInfo = $('#table_resource_events_info', $container);
    var showResourceDetails = opt.resource.showResourceDetails || [];
    opt.resource.showDetails = showResourceDetails.length > 0 && showResourceDetails[0] == 'true';
    if (opt.resource.showDetails) {
      opt.resource.detail = $resourceDetailTable.bootstrapTable('getData');
      var colnums = loadDataStoreColumnNames('resProviderId');
      $.each(opt.resource.detail, function (idx, item) {
        var colnum = colnums.filter(function (v) {
          return v.id === item.text;
        })[0];
        item.title = colnum.title;
      });
    }
    opt.resource.status = $tableResourceStatusInfo.bootstrapTable('getData');
    opt.resource.events = $tableResourceEventsInfo.bootstrapTable('getData');
    if (opt.resource.resourceDayTime === 'system') {
      opt.resource.DayTimeArray = _minTime + ' - ' + _maxTime;
    }
    var allowCustomResourceGradu = opt.resource.allowCustomResourceGradu || [];
    opt.resource.allowCustomGradu = allowCustomResourceGradu.length > 0 && allowCustomResourceGradu[0] == 'true';
    var allowCustomResourceDayTime = opt.resource.allowCustomResourceDayTime || [];
    opt.resource.allowCustomDayTime = allowCustomResourceDayTime.length > 0 && allowCustomResourceDayTime[0] == 'true';
    opt.resourceGroupId = opt.resource.resourceGroupId;
    opt.resourceGroupName = opt.resource.resourceGroupName;
    // 名称，分类，数据来源
    var requeryFields = ['name'];

    /* ------------------ 插件相关参数 start --------------- */
    // 启用日历/资源视图
    opt.enableView = opt.enableView || [];
    if (opt.enableView.length <= 0) {
      appModal.error('至少启用一种类型的视图！');
      return false;
    }
    var enableCalendarView = $.inArray('enableCalendarView', opt.enableView) > -1;
    var enableResourceView = $.inArray('enableResourceView', opt.enableView) > -1;
    //if (enableCalendarView) { // 资源视图也需要事项标识占用
    requeryFields.push('dataProviderId');
    //}
    if (enableResourceView) {
      requeryFields.push('resProviderId');
    }

    // 默认视图
    opt.defaultEnableView = opt.defaultEnableView;
    // 是否显示周末
    opt.weekends = true; //Boolean(opt.weekends);
    // 其他选项
    opt.multiSelect = Boolean(opt.multiSelect);

    if (!checkRequire(requeryFields, opt, $container)) {
      return false;
    }
    if (enableCalendarView && !checkRequire([], opt.calendar, $container, '日历视图')) {
      return false;
    }
    if (enableResourceView && !checkRequire(['resourceGroupId', 'resourceGroupName'], opt.resource, $container, '资源视图')) {
      return false;
    }
    /* ------------------ 插件相关参数 end --------------- */

    // '宽度' 校验
    if (StringUtils.isNotBlank(opt.width)) {
      var regu = '^(([1-9][0-9]*)|([1-9][0-9]*.[0-9]+)|([0].[0-9]+))$';
      var re = new RegExp(regu);
      if (!re.test(opt.width)) {
        appModal.error('宽度必须为正浮点数!');
        return false;
      }
    }
    // '高度' 校验
    if (StringUtils.isNotBlank(opt.height)) {
      var regu = '^(([1-9][0-9]*)|([1-9][0-9]*.[0-9]+)|([0].[0-9]+))$';
      var re = new RegExp(regu);
      if (!re.test(opt.height)) {
        appModal.error('高度必须为正浮点数!');
        return false;
      }
    }

    var columns = $columnInfoTable.bootstrapTable('getData');
    columns = $.map(columns, clearChecked);
    if (enableCalendarView && columns.length == 0) {
      appModal.error('请配置数据列！');
      return false;
    }
    for (var i = 0; i < columns.length; i++) {
      var column = columns[i];
      if (StringUtils.isBlank(column.title) && column.hidden != '1') {
        appModal.error('展示字段标题不允许为空！');
        return false;
      }
    }
    // '查询定义'页签
    var query = designCommons.collectConfigurerData($('#widget_fullcalendar_tabs_query_info', $container), collectClass);
    var $fieldSearchInfoTable = $('#table_field_search_info', $container);
    var fields = $fieldSearchInfoTable.bootstrapTable('getData');
    query.keyword = Boolean(query.keyword);
    query.fieldSearch = Boolean(query.fieldSearch);
    query.expandFieldSearch = Boolean(query.expandFieldSearch);
    query.allowSaveTemplate = Boolean(query.allowSaveTemplate);
    fields = $.map(fields, clearChecked);
    if (query.fieldSearch && fields.length == 0) {
      appModal.error('请配置字段查询内容！');
      return false;
    }
    for (var i = 0; i < fields.length; i++) {
      var field = fields[i];
      if (StringUtils.isBlank(field.name)) {
        appModal.error('字段查询的字段名不允许为空！');
        return false;
      }
      if (StringUtils.isBlank(field.label)) {
        appModal.error('字段查询的标题不允许为空！');
        return false;
      }
      if (field.operator == 'between' && field.queryOptions.queryType != 'date') {
        appModal.error('字段查询中只有日期才允许配置区间！');
        return false;
      }
    }
    // '按钮定义'页签
    var $tableButtonInfo = $('#table_button_info', $container);
    var buttons = $tableButtonInfo.bootstrapTable('getData');
    buttons = $.map(buttons, clearChecked);
    for (var i = 0; i < buttons.length; i++) {
      var button = buttons[i];
      if (StringUtils.isBlank(button.text)) {
        appModal.error('按钮的名称不允许为空！');
        return false;
      }
      if (StringUtils.isBlank(button.code)) {
        appModal.error('按钮的编码不允许为空！');
        return false;
      }
      /*
       * if (StringUtils.isBlank(button.position)) {
       *	appModal.error("按钮的位置不允许为空！");
       *	return false;
       *}
       **/
    }
    // '高级配置'页签
    var seniorSetting = designCommons.collectConfigurerData($('#widget_fullcalendar_tabs_senior_info', $container), collectClass);
    if (StringUtils.isNotBlank(seniorSetting.customSetting)) {
      if (!isJSON(seniorSetting.customSetting)) {
        appModal.error('高级配置>自定义参数非有效JSON格式！');
        return false;
      }
    }

    // '事项表单模板'页签
    var formModuleConfig = designCommons.collectConfigurerData($('#widget_fullcalendar_tabs_formhtml_info', $container), collectClass);
    formModuleConfig.enableCustomForm = Boolean(formModuleConfig.enableCustomForm);
    opt.enableCustomForm = formModuleConfig.enableCustomForm;
    if (formModuleConfig.enableCustomForm) {
      var content = $('#wHtml_content').summernote('code');
      formModuleConfig.formHtmlContent = content;
    }

    requeryFields = []; // 格式化

    opt.columns = columns; // 列定义配置
    opt.query = query; // 查询定义配置
    opt.query.fields = fields; // 查询字段配置
    opt.buttons = buttons; // 按钮定义配置
    opt.seniorConfiguration = seniorSetting; // 高级配置
    opt.formModuleConfig = formModuleConfig; // 表单模板

    this.component.options.configuration = $.extend({}, opt);
  };

  //
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };

  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    return configurer;
  };

  //
  component.prototype.create = function () {
    $(this.element).find('.widget-body').html(this.options.content);
  };

  //
  component.prototype.getDefinitionJson = function () {
    var options = this.options;
    var id = this.getId();
    options.id = id;
    return options;
  };

  return component;
});
