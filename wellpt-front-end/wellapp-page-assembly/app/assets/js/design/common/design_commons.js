define([
  'jquery',
  'commons',
  'formBuilder',
  'appContext',
  'server',
  'design_app_event',
  'wellBtnLib',
  'ace_code_editor',
  'ace_ext_language_tools',
  'minicolors'
], function ($, commons, formBuilder, appContext, server, design_app_event, wellBtnLib) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var uuidObject = commons.UUID;
  // 设置界面元素值
  var setElementValue = function (key, value, $container) {
    if (typeof key == 'string') {
      $("input[name='" + key + "']", $container).each(function () {
        $element = $(this);
        var type = $element.attr('type');
        if (type == 'text' || type == 'hidden' || type == 'number') {
          $element.val(value);
        } else if (type == 'checkbox') {
          $element.prop('checked', Boolean(value));
        } else if (type == 'radio') {
          var checked = $element.val() == value + '';
          $element.prop('checked', checked);
        }
        if ($("input[name='" + key + "']", $container).data('wellSelect')) {
          $("input[name='" + key + "']", $container).wellSelect('val', value);
        }
      });
      $("select[name='" + key + "']", $container).val(value);
      $("textarea[name='" + key + "']", $container).val(value);
    }
  };
  var defaultColumns = [
    'UUID',
    'CREATOR',
    'CREATE_TIME',
    'MODIFIER',
    'MODIFY_TIME',
    'REC_VER',
    'FORM_UUID',
    'STATUS',
    'SIGNATURE_',
    'VERSION',
    'SYSTEM_UNIT_ID'
  ];
  var getImportTableColumnDatas = function () {
    var columnDatas = [];
    var tableName = $('#dynamicTable').val();
    if (!tableName) {
      return columnDatas;
    }
    // server.JDS.call({
    //   service: 'cdDataStoreDefinitionService.loadTableColumns',
    //   data: [tableName],
    //   version: '',
    //   success: function (result) {
    //     if (result.success && result.data) {
    server.JDS.restfulGet({
      url: `/proxy/api/datastore/loadTableColumns/${tableName}`,
      success: function (result) {
        if (result.code == 0 && result.data) {
          columnDatas = $.map(result.data, function (data) {
            if ($.inArray(data.columnIndex, defaultColumns) < 0) {
              return {
                value: data.columnIndex,
                text: data.columnIndex,
                dataType: data.dataType,
                title: data.title,
                id: data.columnIndex
              };
            }
          });
        }
      },
      async: false
    });
    return [].concat(columnDatas);
  };
  var columnConfigRowBean = {
    checked: false,
    uuid: '',
    name: '',
    cell: '',
    rule: {},
    defaults: {}
  };
  var DesignUtils = {
    // 收集非表格数据
    /**
     * $scope jquery对象，数据范围 className class标识
     */
    collectConfigurerData: function ($scope, className) {
      if (!$scope) {
        return;
      }
      if (!className) {
        className = 'w-configurer-option';
      }
      var data = {};
      $('.' + className, $scope).each(function (key, element) {
        var $element = $(element);
        var name = $element.attr('name');
        if (name) {
          var value = $element.val();
          if ($element.attr('type') == 'checkbox') {
            var valueArray = [];
            $("input[name='" + name + "']:checked", $scope).each(function (key, ck) {
              valueArray.push($(ck).val());
            });
            //统一返回 array格式
            value = valueArray;
            //						if (valueArray.length == 1) {
            //							value = valueArray[0];
            //						} else {
            //							value = valueArray;
            //						}
          } else if ($element.attr('type') == 'radio') {
            value = $("input[name='" + name + "']:checked", $scope).val();
          } else if ($element.hasClass('wCommon')) {
            if ($element.hasClass('wCommonComboTree')) {
              // value = $element.wCommonComboTree("instance").getValue();
              value = $element.val();
            }
          }

          DesignUtils.setPropertyValue(data, name, value);
        }
      });
      return data;
    },

    // 设置属性值
    setPropertyValue: function (data, name, value) {
      if (value == null || value.length == 0) {
        value = '';
      }
      var nameArray = name.split('.');
      var name1 = nameArray[0];
      if (nameArray.length == 1) {
        data[name1] = value;
      } else {
        var name2 = name.substring(name.indexOf('.') + 1, name.length);
        if (!data[name1]) {
          data[name1] = {};
        }
        DesignUtils.setPropertyValue(data[name1], name2, value);
      }
    },

    // 收集表格数据
    /**
     * $scope jquery对象，数据范围 tableClassName table的class标识 elementClassName
     * 控件的class标识
     */
    collectConfigurerTableData: function ($scope, tableClassName, elementClassName) {
      if (!$scope) {
        return;
      }
      if (!tableClassName) {
        tableClassName = 'w-configurer-table';
      }
      if (!elementClassName) {
        elementClassName = 'w-configurer-table-option';
      }
      var dataArray = [];
      $('.' + tableClassName, $scope).each(function (key, tableElement) {
        var datas = [];
        var table = $(tableElement);
        $('tbody tr', $(this)).each(function (i, element) {
          var data = DesignUtils.collectConfigurerData($(this), elementClassName);
          datas.push(data);
        });
        dataArray.push(datas);
      });
      return dataArray;
    },

    // 填充非表格数据
    fillConfigurerData: function (opt, $scope, className) {
      if (StringUtils.isBlank(className)) {
        className = 'w-configurer-option';
      }
      if (!$scope) {
        return;
      }
      var data = {};
      $('.' + className, $scope).each(function (key, element) {
        var $element = $(element);
        var name = $element.attr('name');
        var value = DesignUtils.getPropertyValue(opt, name);
        if ($element.attr('type') == 'checkbox') {
          var valueArray = [];
          if (value instanceof Array) {
            valueArray = value;
          } else {
            valueArray.push(value);
          }
          $("input[name='" + name + "']", $scope).each(function (key, ck) {
            for (var i = 0; i < valueArray.length; i++) {
              if ($(ck).val() == valueArray[i]) {
                $(ck).attr('checked', 'true');
              }
            }
          });
        } else if ($element.attr('type') == 'radio') {
          $("input[name='" + name + "']", $scope).each(function (key, radio) {
            if ($(radio).val() == value) {
              $(radio).attr('checked', 'true');
              $(radio).trigger('change');
            }
          });
        } else if ($element.hasClass('wCommon')) {
          if ($element.hasClass('wCommonComboTree')) {
            $element.wCommonComboTree('instance').setValue(value);
          }
        } else {
          $element.val(value);
        }
      });
    },

    // 获取属性值
    getPropertyValue: function (data, name) {
      if (!data) {
        return null;
      }
      var nameArray = name.split('.');
      var name1 = nameArray[0];
      if (nameArray.length == 1) {
        return data[name1];
      } else {
        var name2 = name.substring(name.indexOf('.') + 1, name.length);
        return DesignUtils.getPropertyValue(data[name1], name2);
      }
    },

    // 填充表格数据
    fillConfigurerTableData: function (opt, $scope, tableClassName, elementClassName) {},
    setElementValue: function (configuration, $container, ignoreProperties) {
      $.each(configuration, function (key, value) {
        if (ignoreProperties != null && ignoreProperties.length > 0) {
          for (var i = 0; i < ignoreProperties.length; i++) {
            if (key == ignoreProperties[i]) {
              return;
            }
          }
        }
        setElementValue(key, value, $container);
      });
    },
    checkedFormat: function (value, row, index) {
      if (value) {
        return true;
      }
      return false;
    },
    // 是否支持应用锚点
    isSupportsAppHashByAppPath: function (appPath) {
      var isSupportsAppHash = false;
      if (StringUtils.isBlank(appPath)) {
        return isSupportsAppHash;
      }
      server.JDS.call({
        service: 'appPageDefinitionMgr.isSupportsAppHashByAppPath',
        data: [appPath],
        async: false,
        version: '',
        success: function (result) {
          isSupportsAppHash = result.data;
        }
      });
      return isSupportsAppHash;
    }
  };

  var bootstrapTable = {};
  // 1、目标位置
  var targetItems = [
    {
      text: '',
      id: ''
    },
    {
      text: '新窗口',
      id: '_blank'
    },
    {
      text: '当前窗口',
      id: '_self'
    },
    {
      text: '当前布局窗口',
      id: '_targetWidget'
    },
    {
      text: '对话框',
      id: '_dialog'
    }
  ];
  var renderContentViewByPosition = function ($container, value) {
    var appPageUuid = window.appPageDesigner.getPageUuid();
    var $containerDiv = $container.find('.div_content_option');
    if ($containerDiv[0]) {
      $containerDiv.empty();
    } else {
      $containerDiv = $("<div class='div_content_option'></div>");
      $container.append($containerDiv);
    }
    var $dialogTitle = "<div class=''></div>";
    $containerDiv.append($dialogTitle);
    if (value.position == '_dialog') {
      formBuilder.buildInput({
        container: $containerDiv,
        label: '弹窗标题',
        name: 'widgetDialogTitle',
        value: value.widgetDialogTitle,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9'
      });
      if (WebApp.containerDefaults.wtype === 'wPage') {
        formBuilder.buildInput({
          container: $containerDiv,
          label: '弹窗宽度',
          name: 'widgetDialogWidth',
          value: value.widgetDialogWidth,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9'
        });
        formBuilder.buildInput({
          container: $containerDiv,
          label: '弹窗高度',
          name: 'widgetDialogHeight',
          value: value.widgetDialogHeight,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9'
        });
      }
    }

    if (value.position == '_targetWidget') {
      formBuilder.buildSelect2({
        container: $containerDiv,
        label: '组件选择器',
        name: 'widgetSelectorType',
        value: value.widgetSelectorType,
        display: 'widgetSelectorTypeName',
        displayValue: value.widgetSelectorTypeName,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9',
        select2: {
          data: [
            {
              id: '1',
              text: '#id选择器'
            },
            {
              id: '2',
              text: '.class选择器'
            }
          ],
          labelField: 'widgetSelectorTypeName',
          valueField: 'widgetSelectorType',
          defaultBlank: true,
          remoteSearch: false
        },
        events: {
          change: function () {
            var widgetSelectorType = $(this).val();
            if (widgetSelectorType == '1') {
              $('#div_widgetId_config_info', $containerDiv).show();
              $('#div_widgetCssClass_config_info', $containerDiv).hide();
            } else if (widgetSelectorType == '2') {
              $('#div_widgetId_config_info', $containerDiv).hide();
              $('#div_widgetCssClass_config_info', $containerDiv).show();
            } else {
              $('#div_widgetId_config_info', $containerDiv).hide();
              $('#div_widgetCssClass_config_info', $containerDiv).hide();
            }
          }
        }
      });
      var $widgetIdConfigDiv = $("<div id='div_widgetId_config_info'/>");
      $containerDiv.append($widgetIdConfigDiv);
      formBuilder.buildSelect2({
        container: $widgetIdConfigDiv,
        label: '目标布局窗口',
        name: 'widgetId',
        value: value.widgetId,
        display: 'widgetName',
        displayValue: value.widgetName,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9',
        select2: {
          serviceName: 'appWidgetDefinitionMgr',
          queryMethod: 'loadLayoutSelectData',
          selectionMethod: 'loadLayoutSelectDataByIds',
          params: {
            appPageUuid: appPageUuid,
            uniqueKey: 'id'
          },
          remoteSearch: false
        }
      });
      var $widgetCssClassConfigDiv = $("<div id='div_widgetCssClass_config_info'/>");
      $containerDiv.append($widgetCssClassConfigDiv);
      formBuilder.buildInput({
        container: $widgetCssClassConfigDiv,
        label: '组件样式类',
        name: 'widgetCssClass',
        value: value.widgetCssClass,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9'
      });
      $('#widgetSelectorType', $containerDiv).trigger('change');
      formBuilder.buildCheckbox({
        container: $containerDiv,
        label: '组件内容',
        name: 'refreshIfExists',
        value: value.refreshIfExists,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9',
        items: [
          {
            id: true,
            text: '组件存在时刷新'
          }
        ]
      });
    }
  };
  bootstrapTable.targePosition = {};
  bootstrapTable.targePosition.value2input = function (value) {
    var $input = this.$input;
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '400');
    $input.empty();
    value = value || {};
    var selectData = [
      {
        text: '当前布局窗口',
        id: '_targetWidget'
      },
      {
        text: '对话框',
        id: '_dialog'
      }
    ];
    if (window.appPageDesigner.configureComponent.options.wtype != 'wMobileTabs') {
      selectData.unshift(
        {
          text: '新窗口',
          id: '_blank'
        },
        {
          text: '当前窗口',
          id: '_self'
        }
      );
    }
    if (WebApp.containerDefaults.wtype === 'wPage') {
      selectData.push({
        text: '导航标签组件',
        id: '_navTab'
      });
    }
    formBuilder.buildSelect2({
      container: $input,
      label: '目标位置',
      name: 'position',
      value: value.position,
      display: 'positionName',
      displayValue: value.positionName,
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      // items: targetItems,
      select2: {
        data: selectData,
        labelField: 'widgetPositionTypeName',
        valueField: 'widgetPositionType',
        defaultBlank: true,
        remoteSearch: false
      },
      events: {
        change: function () {
          var position = $(this).val();
          renderContentViewByPosition($input, {
            position: position
          });
        }
      }
    });
    renderContentViewByPosition($input, value);
  };
  bootstrapTable.targePosition.value2display = function (value) {
    for (var i = 0; i < targetItems.length; i++) {
      var target = targetItems[i];
      var position = target.id;
      var positionName = target.text;
      if (position === '_targetWidget' && position === value.position) {
        if (value.widgetSelectorType == '2') {
          return positionName + '(.class选择器:' + value.widgetCssClass + ')';
        } else {
          return positionName + '(#id选择器:' + value.widgetName + ')';
        }
      } else if (position === value.position) {
        return positionName;
      }
    }
    return value.positionName;
  };
  bootstrapTable.targePosition.inputCompleted = function (value) {
    if (value) {
      value.refreshIfExists = Boolean(value.refreshIfExists);
    }
  };

  // 2、徽章
  bootstrapTable.badge = {};
  var renderContentViewByJsModule = function ($container, value, wtype) {
    var $containerDiv = $container.find('.div_content_option');
    if ($containerDiv[0]) {
      $containerDiv.empty();
    } else {
      $containerDiv = $("<div class='div_content_option countJsRelaTableWidget'></div>");
      $container.append($containerDiv);
    }
    $containerDiv.show();
    if (value.countJsModule == 'BootstrapTableViewGetCount') {
      formBuilder.buildSelect2({
        container: $containerDiv,
        label: '视图组件',
        name: 'widgetDefId',
        value: value.widgetDefId,
        display: 'widgetDefName',
        displayValue: value.widgetDefName,
        inputClass: 'w-custom-collect',
        labelColSpan: '4',
        controlColSpan: '8',
        select2: {
          serviceName: 'appWidgetDefinitionMgr',
          params: {
            wtype: wtype || 'wBootstrapTable',
            uniqueKey: 'id'
          },
          remoteSearch: false
        }
      });
    }
  };
  bootstrapTable.badge.value2input = function (wtype, piUuid) {
    return function (value) {
      var val = value || {};
      var $input = this.$input;
      $input.closest('form').removeClass('form-inline');
      $input.css('width', '500');
      $input.empty();
      //兼容旧代码：没有徽章获取方式的情况时
      if (!val.countWay && val.countJsModule) {
        val.countWay = 'countJs';
      }
      formBuilder.buildSelect2({
        container: $input,
        label: '徽章获取方式',
        name: 'countWay',
        value: val.countWay,
        display: 'countWayName',
        displayValue: val.countWayName,
        inputClass: 'w-custom-collect',
        labelColSpan: '4',
        controlColSpan: '8',
        select2: {
          data: [
            {
              id: 'countJs',
              text: '通过数量统计JS脚本'
            },
            {
              id: 'dataStoreCount',
              text: '通过数据仓库获取数量'
            }
          ],
          labelField: 'countWayName',
          valueField: 'countWay',
          defaultBlank: true,
          remoteSearch: false
        },
        events: {
          change: function () {
            $('.countWay', $input).hide();
            $('.countJsRelaTableWidget', $input).hide();
            var way = $(this).val();
            if (way) {
              $('.countWay-' + way).show();
              $('.countWay-' + way).trigger('change');
              if (way === 'countJs') {
                $('#countJsModule', $input).trigger('change');
              }
            }
          }
        }
      });

      formBuilder.buildSelect2({
        container: $input,
        label: '数量统计的JS脚本',
        name: 'countJsModule',
        value: val.countJsModule,
        display: 'countJsModuleName',
        displayValue: val.countJsModuleName,
        inputClass: 'w-custom-collect',
        divClass: 'countWay countWay-countJs',
        labelColSpan: '4',
        controlColSpan: '8',
        select2: {
          serviceName: 'appJavaScriptModuleMgr',
          params: {
            dependencyFilter: 'GetCountBase'
          },
          labelField: 'countJsModuleName',
          valueField: 'countJsModule',
          defaultBlank: true,
          remoteSearch: false
        },
        events: {
          change: function () {
            var countJsModule = $(this).val();
            $('.countJsRelaTableWidget', $input).hide();
            val.countJsModule = countJsModule;
            if (countJsModule) {
              renderContentViewByJsModule($input, val, wtype);
            }
          }
        }
      });
      renderContentViewByJsModule($input, val, wtype);

      formBuilder.buildSelect2({
        container: $input,
        label: '统计数量的数据仓库',
        name: 'dataStoreCounter',
        value: val.dataStoreCounter,
        display: 'dataStoreCounterName',
        displayValue: val.dataStoreCounterName,
        inputClass: 'w-custom-collect',
        divClass: 'countWay countWay-dataStoreCount',
        labelColSpan: '4',
        controlColSpan: '8',
        select2: {
          serviceName: 'viewComponentService',
          queryMethod: 'loadSelectData',
          labelField: 'dataStoreCounterName',
          valueField: 'dataStoreCounter',
          defaultBlank: true,
          remoteSearch: false,
          params: {
            piUuid: piUuid
          }
        },
        events: {
          change: function () {}
        }
      });

      $('.countWay', $input).hide();
      $('#countWay', $input).trigger('change');
    };
  };
  bootstrapTable.badge.value2display = function (value) {
    var countJsModuleName = value.countJsModuleName;
    var dataStoreCounterName = value.dataStoreCounterName;
    var widgetDefName = value.widgetDefName;
    if (value.countWay == 'countJs' && countJsModuleName && widgetDefName) {
      return '统计[表格视图: ' + widgetDefName + ']的数据';
    }
    if (value.countWay == 'dataStoreCount' && dataStoreCounterName) {
      return '统计[数据仓库: ' + dataStoreCounterName + ']的数据';
    }
    return '';
  };

  // 3、图标
  bootstrapTable.icon = {};
  bootstrapTable.icon.value2input = function (value) {
    var $input = this.$input;
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '400');
    $input.empty();

    $('body').on('click,mousedown', '.wCommonPictureLib', function (event) {
      event.stopPropagation();
    });
    var img = "<img src='{0}' iconPath='{1}' style='margin-left: auto;margin-top: 10px;max-width: 110px;' />";
    var icon = "<span class='icon {0}' className='{1}' aria-hidden='true'></span>";
    var selectTypes = this.options.iconSelectTypes;
    var iconEditable = this.options.iconEditable;
    var fieldHtml = new StringBuilder();
    fieldHtml.append("<div class='form-group formbuilder clear'>");
    fieldHtml.append(
      "	<label class='col-xs-3 control-label label-formbuilder' >" + (selectTypes.indexOf(3) > -1 ? '图标' : '图片') + '</label>'
    );
    fieldHtml.append("	<div class='col-xs-9 controls'>");
    fieldHtml.append("		<div id='image_container_div' style='display: inline;'>");
    if (value && value.iconPath) {
      fieldHtml.appendFormat(img, ctx + value.iconPath, value.iconPath);
    } else if (value && value.className) {
      fieldHtml.appendFormat(icon, value.className, value.className);
    }
    fieldHtml.append('		</div>');
    fieldHtml.append("		<div style='float:right;margin-right: 3%;'>");
    fieldHtml.append("		<button type='button' class='btn btn-primary btn-sm'");
    fieldHtml.append(" 			id='logoFileImageSelectBtn'>选择" + (selectTypes.indexOf(3) > -1 ? '图标' : '图片') + '</button>');
    fieldHtml.append("		<button type='button' class='btn btn-danger btn-sm' id='logoFileImageRemoveBtn'>删除</button>");
    fieldHtml.append('		</div>');
    fieldHtml.append('	</div>');
    fieldHtml.append('</div>');
    if (iconEditable) {
      var iconClassInput = "<input type='text' name='className' value='{0}'></input>";
      fieldHtml.append("<div class='form-group formbuilder clear'>");
      fieldHtml.append("	<label class='col-xs-3 control-label label-formbuilder'>样式类</label>");
      fieldHtml.append("	<div class='col-xs-9 controls'>");
      fieldHtml.appendFormat(iconClassInput, (value && value.className) || '');
      fieldHtml.append('	</div>');
      fieldHtml.append('</div>');
      var iconSizeInput = "<input type='number' name='iconSize' value='{0}'></input>";
      fieldHtml.append("<div class='form-group formbuilder clear'>");
      fieldHtml.append("	<label class='col-xs-3 control-label label-formbuilder'>图标大小</label>");
      fieldHtml.append("	<div class='col-xs-9 controls'>");
      fieldHtml.appendFormat(iconSizeInput, (value && value.iconSize) || '30');
      fieldHtml.append('	</div>');
      fieldHtml.append('</div>');
    }
    $input.append(fieldHtml.toString());

    if (iconEditable) {
      $("input[name='className']", $input).on('keyup', function () {
        var iconClass = $(this).val();
        var imgHtml = new StringBuilder();
        imgHtml.appendFormat(icon, iconClass, iconClass);
        $('#image_container_div', $input).empty().append(imgHtml.toString());
        bootstrapTable.icon.initMiniColors(value, $input);
      });
    }

    $('#logoFileImageSelectBtn', $input).on('click', function () {
      $.WCommonPictureLib.show({
        value: $('#image_container_div').find('span').attr('classname'),
        selectTypes: selectTypes,
        confirm: function (data) {
          var fileType = data.fileType;
          var pictureFilePath = data.filePaths;
          if (StringUtils.isBlank(pictureFilePath)) {
            return;
          }
          var imgHtml = new StringBuilder();
          if (fileType === 3) {
            imgHtml.appendFormat(icon, pictureFilePath, pictureFilePath);
            if (iconEditable) {
              $("input[name='className']", $input).val(pictureFilePath);
              $("input[name='iconSize']", $input).val('30');
            }
          } else {
            imgHtml.appendFormat(img, ctx + pictureFilePath, pictureFilePath);
          }
          $('#image_container_div', $input).empty().append(imgHtml.toString());
          bootstrapTable.icon.initMiniColors(value, $input);
        }
      });
      setTimeout(function () {
        $('.wCommonPictureLib').addClass('editable-container');
      }, 0);
    });
    $('#logoFileImageRemoveBtn', $input).on('click', function () {
      $('#image_container_div', $input)
        .find('img,span.icon')
        .each(function () {
          $(this).remove();
        });
    });
    if (!$.isEmptyObject(value) && $('#image_container_div', $input).find('img,span.icon').length > 0) {
      bootstrapTable.icon.initMiniColors(value, $input);
    }
  };
  bootstrapTable.icon.initMiniColors = function (value, $container) {
    var $input = this.$input || $container;
    $('#colorValue', $input).remove();
    $('#colorSelect', $input).remove();
    $('.minicolors', $input).remove();
    $('#image_container_div', $input).append(
      $('<input>', {
        type: 'hidden',
        id: 'colorValue',
        value: value ? value.iconColor : ''
      })
    );
    var $colorBtn = $('<span>', {
      id: 'colorSelect',
      style: 'display:inline-block;width:30px;height:30px;'
    });

    var $selectBtn = $('#logoFileImageSelectBtn', $input);
    $colorBtn.insertBefore($selectBtn);

    $('#colorValue', $input)
      .on('valueChange', function () {
        var $iconSnap = $('#image_container_div .icon', $input);
        $iconSnap.css({
          color: $(this).val()
        });
      })
      .trigger('valueChange');
    var defaultValue = $('#colorValue', $input).val();
    $('#colorSelect', $input).minicolors({
      control: 'hue',
      defaultValue: defaultValue ? defaultValue : '#404040',
      format: 'hex',
      position: 'bottom left',
      letterCase: 'lowercase',
      swatches: '#90caf9|#ef9a9a|#a5d6a7|#fff59d|#ffcc80|#bcaaa4|#eeeeee|#f44336|#2196f3|#4caf50|#ffeb3b|#ff9800|#795548|#9e9e9e'.split(
        '|'
      ),
      change: function (value, opacity) {
        var $iconColor = $('#colorValue', $input);
        $iconColor.val(value);
        $iconColor.trigger('valueChange');
      },
      hide: function () {},
      theme: 'bootstrap'
    });
    $('#colorSelect', $input).parents('.minicolors').addClass('pull-left');
  };
  bootstrapTable.icon.input2value = function (value) {
    var $input = this.$input;
    var iconEditable = this.options.iconEditable;
    var iconPath = $('#image_container_div img', $input).attr('iconPath');
    var className = $('#image_container_div span.icon', $input).attr('className');
    var iconColor = $('#image_container_div #colorValue', $input).val();
    var iconSize = $('input[name="iconSize"]', $input).val();
    if (StringUtils.isNotBlank(iconPath)) {
      value.iconPath = iconPath;
    }
    if (StringUtils.isNotBlank(className)) {
      value.className = className;
    }
    if (iconColor) {
      value.iconColor = iconColor;
    }
    if (iconEditable) {
      value.iconSize = iconSize;
    }
    return value;
  };
  bootstrapTable.icon.value2display = function (value) {
    return value.iconPath ? value.iconPath : value.className ? value.className : '';
  };
  bootstrapTable.icon.value2html = function (value, element) {
    var _dataValue;
    var dataValueAttrString = $(element).attr('data-value');
    if (!dataValueAttrString || dataValueAttrString === 'undefined' || dataValueAttrString === '{}') {
      _dataValue = undefined;
    } else {
      _dataValue = JSON.parse(dataValueAttrString);
    }
    if (_dataValue && _dataValue.className) {
      $(element).removeClass(_dataValue.className.split(' ')[1]);
    }
    $(element).attr('data-value', JSON.stringify(value));
    if (value.className) {
      var iconHtml = "<span class='hide " + value.className + "' aria-hidden='true' style='color:" + value.iconColor + ";'></span>";
      return $(element).addClass(value.className).html(iconHtml);
    }
    return $(element).text($.trim(value.iconPath));
  };

  // 4、颜色
  bootstrapTable.color = {};

  bootstrapTable.color.value2inputFactory = function (defaultSwatchesColors) {
    var _this = this;
    return function (value) {
      _this.value2input.call(this, value, defaultSwatchesColors);
    };
  };

  bootstrapTable.color.value2input = function (value, defaultSwatchesColors) {
    var defaultColors = '#90caf9|#ef9a9a|#a5d6a7|#fff59d|#ffcc80|#bcaaa4|#eeeeee|#f44336|#2196f3|#4caf50|#ffeb3b|#ff9800|#795548|#9e9e9e';
    var swatches = defaultSwatchesColors || defaultColors.split('|');

    var $input = this.$input;
    $input.closest('form').removeClass('form-inline');
    $input.empty();
    $('body').on('click,mousedown', '.wCommonPictureLib', function (event) {
      event.stopPropagation();
    });
    var defaultValue = value ? value.iconColor : '#488cee';

    var $colorValue = $('<input>', {
      type: 'hidden',
      id: 'colorValue',
      value: defaultValue
    });
    var $colorBtn = $('<div class="currBg" id="colorSelect"><div></div></div>');

    var $defaultColorSelector = $('<div class="bg-choose-list">')
      .append('<ul class="bg-choose-box"></ul>')
      .append('<div class="bg-choose-more">更多<i></i></div>');
    $input.addClass('icon-bg-wrap').append($colorValue).append($colorBtn).append($defaultColorSelector);

    function initBgColor(colors) {
      var items = '';

      // 渲染背景颜色
      $.each(colors, function (index, item) {
        var icon = '<i class="iconfont icon-ptkj-dagou"></i>';
        // prettier-ignore
        items += '<li class="bg-choose-item" data-color="' + item + '" style="background: ' + item + '">' + icon + '</li>';
      });

      return items;
    }

    function setInputColor(color) {
      $input.find('#colorSelect').css('borderColor', color).find('div').css('background', color).data('color', color);
      $colorValue.val(color);
    }

    function bindEvent() {
      // 色块选择
      $('.bg-choose-item', $defaultColorSelector)
        .off()
        .on('click', function () {
          var color = $(this).data('color');
          $(this).addClass('hasChoose').siblings().removeClass('hasChoose');
          setInputColor(color);
        });

      // 关闭色块
      $(document).on('click', function (e) {
        if (!$(e.target).hasClass('icon-bg-wrap') && !$(e.target).parents().hasClass('icon-bg-wrap')) {
          $input.closest('.editable-popup').removeClass('editable-popup-open');
          $('.bg-choose-list').hide();
          $('.minicolors').hide();
        }
      });

      // 更多
      $('.bg-choose-more', $defaultColorSelector)
        .off()
        .on('click', function () {
          $('.bg-choose-list').hide();
          if ($('.minicolors').size() > 0) {
            $('.minicolors').show();
          } else {
            var opacity = false;
            $('#colorValue').minicolors({
              control: 'hue',
              format: 'hex',
              color: '#0070C0',
              letterCase: 'lowercase',
              opacity: opacity,
              position: 'bottom left',
              theme: 'bootstrap',
              change: function (value, opacity) {
                $('#colorSelect').focus();
                setInputColor(value);
              },
              hide: function () {
                $('#color').hide();
                $('.icon-bg-wrap').find('.minicolors').hide();
              },
              show: function () {
                $('#color').focus();
              }
            });
            $('#color').focus();
            if (!opacity) {
              $('.minicolors-input-swatch').hide();
            }
          }
        });

      // 显示色块选择器
      $('#colorSelect', $input)
        .off()
        .on('click', function () {
          var currColor = $(this).find('div').data('color');
          $('.bg-choose-list', $input).css('display', 'inline-block');
          $input.closest('.editable-popup').addClass('editable-popup-open');
          var chooseItem = $('.bg-choose-list', $input).find('.bg-choose-item');
          chooseItem.removeClass('hasChoose');
          $.each(chooseItem, function (index, item) {
            if ($(item).data('color') == currColor) {
              $(item).addClass('hasChoose');
            }
          });
        });
    }

    var defaultItems = initBgColor(swatches);
    $defaultColorSelector.find('.bg-choose-box').append(defaultItems);
    setInputColor(defaultValue);
    bindEvent();
  };

  bootstrapTable.color.input2value = function (value) {
    var $input = this.$input;
    var iconColor = $('#colorValue', $input).val();
    if (StringUtils.isNotBlank(iconColor)) {
      value.iconColor = iconColor;
    }
    return value;
  };
  bootstrapTable.color.value2display = function (value) {
    return value.iconColor || '';
  };
  bootstrapTable.color.value2html = function (value, element) {
    if ($.trim(value).length === 0 || value === 'undefined') {
      value = {
        className: '',
        iconColor: '#488cee'
      };
    } else if (typeof value === 'string') {
      if (/^{(.|\s)*}$/g.test(value)) {
        value = JSON.parse(value);
      } else {
        value = {
          className: '',
          iconColor: value
        };
      }
    }
    value.className = value.className || '';
    $(element).attr('data-value', JSON.stringify(value));
    var iconHtml =
      "<span class='" +
      value.className +
      "' aria-hidden='true' style='width: 18px;height: 18px;display: inline-block;background-color:" +
      value.iconColor +
      ";'></span>";
    return $(element).addClass(value.className).html(iconHtml);
  };

  //按钮库
  bootstrapTable.btnLib = {};
  bootstrapTable.btnLib.value2input = function (value) {
    var $input = this.$input;
    $input.closest('form').removeClass('form-inline').addClass('form-horizontal');
    $input.css('width', '430');
    $input.empty();

    $('body').on('click', '.wCommonBtnLib', function (event) {
      event.stopPropagation();
    });

    var fieldHtml = new StringBuilder();
    fieldHtml.append("<div class='form-group clear'>");
    fieldHtml.append("	<label class='col-xs-2 control-label' >模板</label>");
    fieldHtml.append("	<div class='col-xs-10 controls'>");
    fieldHtml.append("		<div id='btn_container_div' style='display: inline;'>");
    if (value && value.btnInfo) {
      if ($.inArray(value.btnInfo.type, ['primary', 'minor', 'line', 'noLine']) > -1) {
        if (value.iconInfo) {
          fieldHtml.appendFormat(
            '<a class="well-btn {0} {1} {2}"><i class="{3}"></i>按钮</a>',
            value.btnColor,
            value.btnInfo['class'],
            value.btnSize,
            value.iconInfo.fileIDs
          );
        } else {
          fieldHtml.appendFormat('<a class="well-btn {0} {1} {2}">按钮</a>', value.btnColor, value.btnInfo['class'], value.btnSize);
        }
      } else {
        if (value.btnInfo.icon) {
          fieldHtml.appendFormat(
            '<a class="well-btn {0} {1}"><i class="{2}"></i>{3}</a>',
            value.btnInfo['class'],
            value.btnSize,
            value.btnInfo.icon,
            value.btnInfo.text
          );
        } else {
          fieldHtml.appendFormat('<a class="well-btn {0} {1}">{2}</a>', value.btnInfo['class'], value.btnSize, value.btnInfo.text);
        }
      }
    }
    fieldHtml.append('		</div>');
    fieldHtml.append("		<div style='float:right;margin-right: 3%;'>");
    fieldHtml.append("		<button type='button' class='well-btn w-btn-primary'");
    fieldHtml.append(" 			id='btnLibSelectBtn'>选择按钮模板</button>");
    fieldHtml.append("		<button type='button' class='well-btn w-btn-danger' id='btnLibRemoveBtn'>删除</button>");
    fieldHtml.append('		</div>');
    fieldHtml.append('	</div>');
    fieldHtml.append('</div>');
    $input.append(fieldHtml.toString());
    $('#btn_container_div a').data('info', value);

    $('#btnLibSelectBtn', $input).on('click', function () {
      var info = {};
      if ($('#btn_container_div a', $input).length > 0) {
        info = $('#btn_container_div a', $input).data('info');
      }
      $.WCommonBtnLib.show({
        value: info,
        confirm: function (data) {
          var btnHtml = new StringBuilder();
          if ($.inArray(data.btnInfo.type, ['primary', 'minor', 'line', 'noLine']) > -1) {
            if (data.iconInfo) {
              btnHtml.appendFormat(
                '<a class="well-btn {0} {1} {2}"><i class="{3}"></i>按钮</a>',
                data.btnColor,
                data.btnInfo['class'],
                data.btnSize,
                data.iconInfo.fileIDs
              );
            } else {
              btnHtml.appendFormat('<a class="well-btn {0} {1} {2}">按钮</a>', data.btnColor, data.btnInfo['class'], data.btnSize);
            }
          } else {
            if (data.btnInfo.icon) {
              btnHtml.appendFormat(
                '<a class="well-btn {0} {1}"><i class="{2}"></i>{3}</a>',
                data.btnInfo['class'],
                data.btnSize,
                data.btnInfo.icon,
                data.btnInfo.text
              );
            } else {
              btnHtml.appendFormat('<a class="well-btn {0} {1}">{2}</a>', data.btnInfo['class'], data.btnSize, data.btnInfo.text);
            }
          }
          $('#btn_container_div', $input).empty().append(btnHtml.toString());

          $('#btn_container_div a', $input).data('info', data);
        }
      });
    });

    $('#btnLibRemoveBtn', $input).on('click', function () {
      $('#btn_container_div', $input)
        .find('a')
        .each(function () {
          $(this).remove();
        });
    });
  };
  bootstrapTable.btnLib.input2value = function () {
    var $input = this.$input;
    return $('#btn_container_div a', $input).data('info');
  };
  bootstrapTable.btnLib.value2display = function (value) {
    // return value.iconPath ? value.iconPath : (value.className ? value.className : "");
  };
  bootstrapTable.btnLib.value2html = function (value, element) {
    var btnHtml = new StringBuilder();
    if (value && value.btnInfo) {
      if ($.inArray(value.btnInfo.type, ['primary', 'minor', 'line', 'noLine']) > -1) {
        if (value.iconInfo) {
          btnHtml.appendFormat(
            '<a class="well-btn {0} {1} {2}"><i class="{3}"></i>按钮</a>',
            value.btnColor,
            value.btnInfo['class'],
            value.btnSize,
            value.iconInfo.fileIDs
          );
        } else {
          btnHtml.appendFormat('<a class="well-btn {0} {1} {2}">按钮</a>', value.btnColor, value.btnInfo['class'], value.btnSize);
        }
      } else {
        if (value.btnInfo.icon) {
          btnHtml.appendFormat(
            '<a class="well-btn {0} {1}"><i class="{2}"></i>{3}</a>',
            value.btnInfo['class'],
            value.btnSize,
            value.btnInfo.icon,
            value.btnInfo.text
          );
        } else {
          btnHtml.appendFormat('<a class="well-btn {0} {1}">{2}</a>', value.btnInfo.class, value.btnSize, value.btnInfo.text);
        }
      }
      return $(element).html(btnHtml.toString());
    } else {
      return $(element).html('');
    }
  };

  //按钮显示位置
  bootstrapTable.btnPosition = {};
  bootstrapTable.btnPosition.value2input = function (value) {
    var $input = this.$input;
    console.log($input);
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '400');
    $input.empty();

    var _value;
    _value = $.map(value, function (item) {
      return item.split('-')[0];
    });
    console.log(value, _value);
    var positionArr = [
      {
        id: '1',
        text: '表格头部'
      },
      {
        id: '2',
        text: '表格底部'
      },
      {
        id: '3',
        text: '行末'
      },
      {
        id: '4',
        text: '悬浮行上'
      },
      {
        id: '5',
        text: '行下'
      }
    ];
    var fieldHtml = new StringBuilder();
    fieldHtml.append("<div class='formbuilder form-group'>");
    fieldHtml.append("<label class='col-xs-3 control-label label-formbuilder' >展示位置</label>");
    fieldHtml.append("<div class='col-xs-9 controls'>");
    fieldHtml.append("<div id='btn_container_div' style='display: inline;'>");
    if (_value) {
      fieldHtml.append("<input type='hidden' id='btn_position_value' value='" + _value.join(';') + "'>");
    } else {
      fieldHtml.append("<input type='hidden' id='btn_position_value'>");
    }
    fieldHtml.append("<input type='hidden' id='btn_position_label'>");
    fieldHtml.append('</div>');
    fieldHtml.append('</div>');
    fieldHtml.append('</div>');
    fieldHtml.append("<div class='formbuilder form-group'  id='float_btn_column_select' style='display: none;margin-top: 10px'>");
    fieldHtml.append("<label class='col-xs-3 control-label label-formbuilder' >按钮悬浮行列</label>");
    fieldHtml.append("<div class='col-xs-9 controls'>");

    var float_btn_column_value = '';
    var showFloatField = false;
    $.each(value, function (i, item) {
      if (item.split('-').length > 1 && item.split('-')[0] == '4') {
        float_btn_column_value = item.split('-')[1];
        showFloatField = true;
      }
    });

    if (float_btn_column_value) {
      fieldHtml.append("<input type='hidden' id='float_btn_column_value' value='" + float_btn_column_value + "'>");
    } else {
      fieldHtml.append("<input type='hidden' id='float_btn_column_value'>");
    }
    fieldHtml.append("<input type='hidden' id='float_btn_column_label'>");
    fieldHtml.append('</div>');
    fieldHtml.append('</div>');
    fieldHtml.append("<div class='formbuilder form-group'  id='under_btn_column_select' style='display: none;margin-top: 10px'>");
    fieldHtml.append("<label class='col-xs-3 control-label label-formbuilder' >按钮行下列</label>");
    fieldHtml.append("<div class='col-xs-9 controls'>");

    var under_btn_column_value = '';
    var showUnderField = false;
    $.each(value, function (i, item) {
      if (item.split('-').length > 1 && item.split('-')[0] == '5') {
        under_btn_column_value = item.split('-')[1];
        showUnderField = true;
      }
    });

    if (under_btn_column_value) {
      fieldHtml.append("<input type='hidden' id='under_btn_column_value' value='" + under_btn_column_value + "'>");
    } else {
      fieldHtml.append("<input type='hidden' id='under_btn_column_value'>");
    }
    fieldHtml.append("<input type='hidden' id='under_btn_column_label'>");
    fieldHtml.append('</div>');
    fieldHtml.append('</div>');
    fieldHtml.append("<div style='float:right;margin-right: 3%;'>");
    fieldHtml.append('</div>');
    $input.append(fieldHtml.toString());
    $input.find('#btn_position_value').wSelect2({
      data: positionArr,
      multiple: true,
      searchable: false,
      valueField: 'btn_position_value',
      labelField: 'btn_position_label'
    });
    $input.find('#float_btn_column_value').wSelect2({
      data: this.options.renderParams.columnTableData,
      labelField: 'float_btn_column_label',
      valueField: 'float_btn_column_value',
      remoteSearch: false
    });
    $input.find('#under_btn_column_value').wSelect2({
      data: this.options.renderParams.columnTableData,
      labelField: 'under_btn_column_label',
      valueField: 'under_btn_column_value',
      remoteSearch: false
    });
    if (showFloatField) {
      $input.find('#float_btn_column_select').show();
    }
    if (showUnderField) {
      $input.find('#under_btn_column_select').show();
    }
    $input.find('#btn_position_value').on('change', function () {
      var _value = $(this).val().split(';');
      if (_value.indexOf('4') > -1) {
        $input.find('#float_btn_column_select').show();
        $input.find('#under_btn_column_select').hide();
      } else if (_value.indexOf('5') > -1) {
        $input.find('#under_btn_column_select').show();
        $input.find('#float_btn_column_select').hide();
      } else {
        $input.find('#float_btn_column_select').hide();
        $input.find('#under_btn_column_select').hide();
      }
    });
  };
  bootstrapTable.btnPosition.input2value = function () {
    var $input = this.$input;

    var returnData = $input.find('#btn_position_value').val() !== '' ? $input.find('#btn_position_value').val().split(';') : [];
    if (returnData.indexOf('4') > -1) {
      returnData[returnData.indexOf('4')] = '4-' + $input.find('#float_btn_column_value').val();
    }
    if (returnData.indexOf('5') > -1) {
      returnData[returnData.indexOf('5')] = '5-' + $input.find('#under_btn_column_value').val();
    }
    return returnData;
  };
  bootstrapTable.btnPosition.value2display = function () {
    console.log('value2display', arguments);
  };
  bootstrapTable.btnPosition.value2html = function (value, element) {
    console.log('value2html', arguments);
    var positionArr = ['', '表格头部', '表格底部', '行末', '悬浮行上', '行下'];
    var _html = $.map(value, function (item) {
      return '<div>' + positionArr[item.split('-')[0]] + '</div>';
    });
    $(element).html(_html.join(''));
  };

  bootstrapTable.eventHandler = {};
  bootstrapTable.eventHandler.value2input = function (value) {
    var _self = this;
    var $input = this.$input;
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '400');
    $input.empty();

    formBuilder.buildInput({
      container: $input,
      label: '',
      name: 'eventHandlerName',
      value: value ? value.name : '',
      labelColSpan: '0',
      controlColSpan: '12'
    });

    var $eventHanlderContainer = $input;
    $('#eventHandlerName', $input).AppEvent({
      idValue: value ? value.id : '',
      ztree: {
        params: [appContext.getCurrentUserAppData().getSystem().productUuid]
      },
      okCallback: function ($el, data) {
        if (data) {
          // 锚点设置
          $("input[name='eventHashType']", $eventHanlderContainer).removeAttr('checked');
          $("input[name='eventHashType']", $eventHanlderContainer).trigger('change');
          if (DesignUtils.isSupportsAppHashByAppPath(data.data.path)) {
            $("input[name='eventHashType']", $eventHanlderContainer).removeAttr('disabled');
          } else {
            $("input[name='eventHashType']", $eventHanlderContainer).attr('disabled', 'disabled');
          }
          $("input[name='eventHash']", $eventHanlderContainer).val('');
          if (!buildEventHashTree) {
            hashTree(data.path, '');
          }
          $('#eventHashTree', $eventHanlderContainer).wCommonComboTree({
            value: ''
          });
        }
      },
      clearCallback: function ($el) {
        // 锚点设置
        $("input[name='eventHashType']", $eventHanlderContainer).removeAttr('checked');
        $("input[name='eventHashType']", $eventHanlderContainer).trigger('change');
        $("input[name='eventHashType']", $eventHanlderContainer).attr('disabled', 'disabled');
        $("input[name='eventHash']", $eventHanlderContainer).val('');
        $('#eventHashTree', $eventHanlderContainer).wCommonComboTree({
          value: ''
        });
      }
    });
    $input.find('input[name=eventHandlerName]').data('initEventHandler', value || {});

    var buildEventHashTree = null;

    // 锚点设置
    formBuilder.buildRadio({
      container: $eventHanlderContainer,
      label: '锚点设置',
      name: 'eventHashType',
      value: value ? value.hashType : '',
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      divClass: 'eventHashTypeDiv',
      items: [
        {
          id: '1',
          text: '指定锚点'
        },
        {
          id: '2',
          text: '自定义'
        }
      ],
      events: {
        change: function () {
          var eventHashType = $("input[name='" + this.name + "']:checked", $eventHanlderContainer).val();
          $('.eventHashTypeRow', $eventHanlderContainer).hide();
          if (eventHashType == '1') {
            $("input[name='" + this.name + "']:checked", $eventHanlderContainer).data('checkFlag', 1);
            $('.eventHashType' + eventHashType, $eventHanlderContainer).show();
            var appPath = value ? value.path : '';
            var selectEventData = $('#eventHandlerName', $eventHanlderContainer).data('eventData');
            if (selectEventData) {
              appPath = selectEventData.path;
            }
            // 事件处理
            $('#eventHashTree', $eventHanlderContainer).wCommonComboTree({
              service: 'pageDefinitionService.getAppHashTreeByAppPath',
              serviceParams: [appPath],
              multiSelect: false, // 是否多选
              parentSelect: false, // 父节点选择有效，默认无效
              onAfterSetValue: function (event, self, value) {
                $('#eventHash', $eventHanlderContainer).val(value);
                var valueNodes = self.options.valueNodes;
                if (valueNodes && valueNodes.length == 1) {
                  var parantNode = valueNodes[0].getParentNode();
                  if (parantNode) {
                    $(this).val('/' + parantNode.name + '/' + valueNodes[0].name);
                  } else {
                    $(this).val('/' + valueNodes[0].name);
                  }
                }
              }
            });
          } else if (eventHashType == '2') {
            $("input[name='" + this.name + "']:checked", $eventHanlderContainer).data('checkFlag', 1);
            $('.eventHashType' + eventHashType, $eventHanlderContainer).show();
          }
        }
      }
    });
    // 锚点设置单选框取消选中
    $("input[name='eventHashType']", $eventHanlderContainer).on('click', function () {
      var $radio = $(this);
      var checkFlag = $radio.data('checkFlag');
      if ($radio.attr('checked') == 'checked' && checkFlag == null) {
        $radio.data('checkFlag', '1');
      } else {
        if (checkFlag == '1') {
          $radio.removeAttr('checked');
          $radio.trigger('change');
        } else {
          $radio.data('checkFlag', '1');
        }
      }
      $("input[name='eventHashType']:not(:checked)", $eventHanlderContainer).data('checkFlag', '0');
    });
    // 锚点树
    var appPath = value ? value.path : '';
    var selectEventData = $('#eventHandlerName', $eventHanlderContainer).data('eventData');
    if (selectEventData) {
      appPath = selectEventData.path;
    }
    var hashTree = function (_appPath, _hash) {
      buildEventHashTree = true;
      formBuilder.buildWCommonComboTree({
        container: $eventHanlderContainer,
        label: '',
        name: 'eventHashTree',
        value: _hash,
        inputClass: 'w-custom-collect',
        divClass: 'eventHashType1 eventHashTypeRow',
        labelColSpan: '3',
        controlColSpan: '9',
        wCommonComboTree: {
          service: 'pageDefinitionService.getAppHashTreeByAppPath',
          serviceParams: [_appPath],
          width: '292px',
          multiSelect: false, // 是否多选
          parentSelect: true, // 父节点选择有效，默认无效
          onAfterSetValue: function (event, self, value) {
            $('#eventHash', $eventHanlderContainer).val(value);
            var valueNodes = self.options.valueNodes;
            if (valueNodes && valueNodes.length == 1) {
              var parantNode = valueNodes[0].getParentNode();
              if (parantNode) {
                $(this).val('/' + parantNode.name + '/' + valueNodes[0].name);
              } else {
                $(this).val('/' + valueNodes[0].name);
              }
            }
          }
        }
      });
    };
    if (appPath) {
      hashTree(appPath, value ? value.hash : '');
    }
    // 自定义锚点
    formBuilder.buildInput({
      container: $eventHanlderContainer,
      label:
        '<span class="wtooltip icon iconfont icon-ptkj-mianxingwenxintishi"><span class="wtooltiptext">锚点格式：/{组件ID}/{菜单、导航、页签等ID}</span></span>',
      name: 'eventHash',
      value: value ? value.hash : '',
      inputClass: 'w-custom-collect',
      divClass: 'eventHashType2 eventHashTypeRow',
      labelColSpan: '3',
      controlColSpan: '9'
    });
    $("input[name='eventHashType']", $eventHanlderContainer).trigger('change');
    if (DesignUtils.isSupportsAppHashByAppPath(appPath)) {
      $("input[name='eventHashType']", $eventHanlderContainer).removeAttr('disabled');
    } else {
      $("input[name='eventHashType']", $eventHanlderContainer).attr('disabled', 'disabled');
    }
  };
  bootstrapTable.eventHandler.input2value = function (value) {
    var value = $('#eventHandlerName', this.$input).AppEvent('getData');
    var hashType = $("input[name='eventHashType']:checked", this.$input).val();
    var hash = $('#eventHash', this.$input).val();
    if (!value) {
      var initEventHandler = $('#eventHandlerName', this.$input).data('initEventHandler') || {};
      initEventHandler.hashType = hashType;
      initEventHandler.hash = hash;
      return initEventHandler;
    }
    return {
      id: value.id,
      name: value.name,
      path: value.data.path,
      type: value.data.type,
      hashType: hashType,
      hash: hash
    };
  };
  bootstrapTable.eventHandler.validate = function (value) {
    // 锚点设置验证
    if (value && StringUtils.isNotBlank(value.hashType) && StringUtils.isBlank(value.hash)) {
      appModal.error('锚点设置不能为空！');
      setTimeout(function () {
        $('.has-error', '.popover-content').removeClass('has-error');
      }, 50);
      return {
        newValue: value,
        msg: ''
      };
      //return "锚点设置不能为空！";
    }
  };
  bootstrapTable.eventHandler.value2display = function (value) {
    return value ? value.name : null;
  };

  // 4、事件参数
  bootstrapTable.eventParams = {};
  bootstrapTable.eventParams.value2input = function (value) {
    var $input = this.$input;
    this.options.value = value;
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '400');
    $input.empty();

    var $optionDiv = $input.find('.div_event_param_option');
    if ($optionDiv[0]) {
      $optionDiv.empty();
    } else {
      $optionDiv = $("<div class='div_event_param_option'></div>");
      $input.append($optionDiv);
    }

    var $div = $('<div>').attr('id', 'div_event_param_select');
    $optionDiv.append($div);
    var eventParamsAppendParametersConstant = function ($element, value) {
      var parameters = [];
      if (value && value.parameters) {
        parameters = value.parameters;
      }
      formBuilder.bootstrapTable.build({
        container: $element,
        name: 'parameters',
        ediableNest: true,
        help:
          '1、事件处理中附加的参数对象，解析后作为事件处理参数options的params属性对象，通过参数名访问参数值；<br>' +
          '2、参数值支持访问变量，默认支持访问当前组件${ui}、应用上下文${appContext}；<br>' +
          '3、事件处理通过url响应的参数附加到url中，事件处理为组件渲染的传到入组件定义的params参数。',
        table: {
          data: parameters,
          striped: true,
          idField: 'uuid',
          width: 300,
          columns: [
            {
              field: 'checked',
              formatter: DesignUtils.checkedFormat,
              checkbox: true
            },
            {
              field: 'uuid',
              title: 'UUID',
              visible: false,
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'text',
              title: '参数名称',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'name',
              title: '参数名',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'value',
              title: '参数值',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            }
          ]
        }
      });
    };
    eventParamsAppendParametersConstant($div, value);
  };
  bootstrapTable.eventParams.input2value = function (value) {
    var params = {};
    value.parameters = this.$input.find('#table_parameters_info').bootstrapTable('getData');
    $.map(value.parameters, function (option) {
      params[option.name] = option.value;
    });
    value.params = params;
    return value;
  };
  bootstrapTable.eventParams.value2display = function (value) {
    if (value == null) {
      return '';
    }
    var parameters = value.parameters;
    if (parameters == null || parameters.length == 0) {
      return '';
    }
    var sb = new StringBuilder();
    for (var i = 0; i < parameters.length; i++) {
      sb.append(parameters[i].text);
      if (i != parameters.length - 1) {
        sb.append(';');
      }
    }
    return sb.toString();
  };

  // |分隔符 前面为layDate显示类型，后面为日期格式
  var dateFormat = [
    {
      id: 'date|yyyy-MM-dd',
      text: '选择日期(2019-01-01)'
    },
    {
      id: 'date|yyyy-MM-dd|includeEnd',
      text: '选择日期(2019-01-01)(区间时结束时间包含当天)'
    },
    {
      id: 'date|yyyy年MM月dd日',
      text: '选择日期(2019年01月01日)'
    },
    {
      id: 'date|yyyy年MM月dd日|includeEnd',
      text: '选择日期(2019年01月01日)(区间时结束时间包含当天)'
    },
    {
      id: 'date-range|yyyy-MM-dd',
      text: '选择日期范围(2019-01-01 至 2020-01-01)'
    },
    {
      id: 'date-range|yyyy-MM-dd|includeEnd',
      text: '选择日期范围(2019-01-01 至 2020-01-01)(区间时结束时间包含当天)'
    },
    {
      id: 'date-range|yyyy年MM月dd日',
      text: '选择日期范围(2019年01月01日 至 2020年01月01日)'
    },
    {
      id: 'date-range|yyyy年MM月dd日|includeEnd',
      text: '选择日期范围(2019年01月01日 至 2020年01月01日)(区间时结束时间包含当天)'
    },
    {
      id: 'year|yyyy',
      text: '选择年份(2019)'
    },
    {
      id: 'year|yyyy年',
      text: '选择年份(2019年)'
    },
    {
      id: 'month|yyyy-MM',
      text: '选择月份(2019-09)'
    },
    {
      id: 'month|yyyy年MM月',
      text: '选择月份(2019年09月)'
    },
    {
      id: 'month-range|yyyy-MM',
      text: '选择月份范围(2019-01 至 2020-01)'
    },
    {
      id: 'month-range|yyyy年MM月',
      text: '选择月份范围(2019年01月 至 2020年01月)'
    },
    {
      id: 'time|HH:mm:ss',
      text: '选择时间(12:00:00)'
    },
    {
      id: 'time|HH时mm分ss秒',
      text: '选择时间(12时00分00秒)'
    },
    {
      id: 'time|HH:mm',
      text: '选择时间(12:00)'
    },
    {
      id: 'time|HH时mm分',
      text: '选择时间(12时00分)'
    },
    {
      id: 'time|HH',
      text: '选择时间(12)'
    },
    {
      id: 'time|HH时',
      text: '选择时间(12时)'
    },
    {
      id: 'time-range|HH:mm:ss',
      text: '选择时间范围(12:00:00 至 20：00:00)'
    },
    {
      id: 'time-range|HH时mm分ss秒',
      text: '选择时间范围(12时00分00秒 至 20时00分00秒)'
    },
    {
      id: 'time-range|HH:mm',
      text: '选择时间范围(12:00 至 20：00)'
    },
    {
      id: 'time-range|HH时mm分',
      text: '选择时间范围(12时00分 至 20时00分)'
    },
    {
      id: 'time-range|HH',
      text: '选择时间范围(12 至 20)'
    },
    {
      id: 'time-range|HH时',
      text: '选择时间范围(12时 至 20时)'
    },
    {
      id: 'datetime|yyyy-MM-dd HH:mm:ss',
      text: '选择日期时间(2019-09-01 12:00:00)'
    },
    {
      id: 'datetime|yyyy年MM月dd日 HH时mm分ss秒',
      text: '选择日期时间(2019年09月01日 12时00分00秒)'
    },
    {
      id: 'datetime|yyyy-MM-dd HH:mm',
      text: '选择日期时间(2019-09-01 12:00)'
    },
    {
      id: 'datetime|yyyy年MM月dd日 HH时mm分',
      text: '选择日期时间(2019年09月01日 12时00分)'
    },
    {
      id: 'datetime|yyyy-MM-dd HH',
      text: '选择日期时间(2019-09-01 12)'
    },
    {
      id: 'datetime|yyyy年MM月dd日 HH时',
      text: '选择日期时间(2019年09月01日 12时)'
    },
    {
      id: 'datetime-range|yyyy-MM-dd HH:mm:ss',
      text: '选择日期时间范围(2019-09-01 12:00:00 至 2019-09-01 20:00:00)'
    },
    {
      id: 'datetime-range|yyyy年MM月dd日 HH时mm分ss秒',
      text: '选择日期时间范围(2019年09月01日 12时00分00秒 至 2019年09月01日 20时00分00秒)'
    },
    {
      id: 'datetime-range|yyyy-MM-dd HH:mm',
      text: '选择日期时间范围(2019-09-01 12:00 至 2019-09-01 20:00)'
    },
    {
      id: 'datetime-range|yyyy年MM月dd日 HH时mm分',
      text: '选择日期时间范围(2019年09月01日 12时00分 至 2019年09月01日 20时00分)'
    },
    {
      id: 'datetime-range|yyyy-MM-dd HH',
      text: '选择日期时间范围(2019-09-01 12 至 2019-09-01 20)'
    },
    {
      id: 'datetime-range|yyyy年MM月dd日 HH时',
      text: '选择日期时间范围(2019年09月01日 12时 至 2019年09月01日 20时)'
      // }, {
      //     id: 'YYYY-MM-DD',
      //     text: "日期(2000-01-01)"
      // }, {
      //     id: 'YYYY',
      //     text: "年份(2000年)"
      // }, {
      //     id: 'YYYY年MM月',
      //     text: "日期(2000年01月)"
      // }, {
      //     id: 'MM月DD日',
      //     text: "日期(01月01日)"
      // }, {
      //     id: 'DD日',
      //     text: "日期(01日)"
      // }, {
      //     id: 'HH',
      //     text: "时间(12)"
      // }, {
      //     id: 'HH:mm',
      //     text: "时间到分(12:00)"
      // }, {
      //     id: 'HH:mm:ss',
      //     text: "时间到秒(12:00:00)"
      // }, {
      //     id: 'YYYY-MM-DD HH',
      //     text: "日期到时 (2000-01-01 12)"
      // }, {
      //     id: 'YYYY-MM-DD HH:mm',
      //     text: "日期到分 (2000-01-01 12:00)"
      // }, {
      //     id: 'YYYY-MM-DD HH:mm:ss',
      //     text: "日期到秒 (2000-01-01 12:00:00)"
    }
  ];

  var clearChecked = function (row) {
    row.checked = false;
    return row;
  };

  var checkedFormat = function (value, row, index) {
    if (value) {
      return true;
    }
    return false;
  };

  // 查询类型
  bootstrapTable.queryFieldType = {
    // 渲染查询类型的页面
    renderQueryTypeOptionView: function ($input, value) {
      var $optionDiv = $input.find('.div_query_type_option');
      if ($optionDiv[0]) {
        $optionDiv.empty();
      } else {
        $optionDiv = $("<div class='div_query_type_option'></div>");
        $input.append($optionDiv);
      }
      // 渲染日期格式
      this.appendDateQuery($optionDiv, value);
      // 渲染下拉框格式
      this.appendSelectQuery($optionDiv, value);
      // 渲染组织弹出框格式
      this.appendUnitQuery($optionDiv, value);

      this.appendTreeDataQuery($optionDiv, value);

      this.appendFileUploadQuery($optionDiv, value);
    },

    appendFileUploadQuery: function ($element, value) {
      if (value.queryType == 'fileUpload') {
        var $div = $('<div>').attr('id', 'div_query_type_select_tree');
        $element.append($div);
        this.appendCheckboxOption($div, value, [
          {
            label: '仅单文件上传',
            name: 'singleFile',
            items: [
              {
                id: '1',
                text: ' '
              }
            ]
          }
        ]);
      }
    },

    //树形
    appendTreeDataQuery: function ($element, value) {
      if (value.queryType == 'treeSelect') {
        var $div = $('<div>').attr('id', 'div_query_type_select_tree');
        $element.append($div);
        this.createDataSourceColumnOption($div, value, [
          {
            label: '值字段',
            name: 'valueColumn'
          },
          {
            label: '展示字段',
            name: 'textColumn'
          },
          {
            label: '唯一字段',
            name: 'uniqueColumn'
          },
          {
            label: '父级字段',
            name: 'parentColumn'
          }
        ]);
        formBuilder.buildTextarea({
          container: $div,
          label: '默认条件',
          name: 'defaultCondition',
          value: value.defaultCondition,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9'
        });

        formBuilder.buildInput({
          container: $div,
          label: '不能选中的级别',
          name: 'noCheckLevel',
          value: value.noCheckLevel,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          placeholder: '例如: 0,1,2'
        });

        this.appendCheckboxOption($div, value, [
          {
            label: '是否多选',
            name: 'multiple',
            items: [
              {
                id: '1',
                text: '启用多选'
              }
            ]
          },
          {
            label: '是否显示图标',
            name: 'showIcon',
            items: [
              {
                id: '1',
                text: '启用图标'
              }
            ]
          }
        ]);
      }
    },

    appendCheckboxOption: function ($element, value, ckOptions) {
      for (var i = 0, len = ckOptions.length; i < len; i++) {
        formBuilder.buildCheckbox({
          container: $element,
          label: ckOptions[i].label,
          name: ckOptions[i].name,
          value: [value[ckOptions[i].name]],
          inputClass: 'w-custom-collect',
          labelColSpan: ckOptions[i].labelColspan || '3',
          controlColSpan: ckOptions[i].controlColSpan || '9',
          items: ckOptions[i].items
        });
      }
    },

    // 日期格式
    appendDateQuery: function ($element, value) {
      if (value.queryType == 'date') {
        $dateDiv = $('<div>').add('id', 'div_query_type_date');
        $element.append($dateDiv);
        formBuilder.buildSelect2({
          container: $dateDiv,
          label: '日期格式',
          name: 'format',
          value: value.format,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          select2: {
            data: dateFormat
          }
        });
        formBuilder.buildRadio({
          container: $dateDiv,
          label: '日期范围',
          name: 'range',
          value: value.range,
          inputClass: 'w-custom-collect w-data-range',
          labelColSpan: '3',
          controlColSpan: '9',
          items: [
            {
              id: '1',
              text: '今天之前不可选'
            },
            {
              id: '2',
              text: '今天之后不可选'
            }
          ]
        });
      }
    },

    // 组织选择框的配置
    appendUnitQuery: function ($element, value) {
      if (value.queryType == 'unit') {
        $unitDiv = $('<div>').add('id', 'div_query_type_unit');
        $element.append($unitDiv);
        var currUser = server.SpringSecurityUtils.getUserDetails();
        var orgTypes = [];
        $.ajax({
          url: ctx + '/api/org/multi/queryOrgOptionListBySystemUnitIdAndOptionOfPT',
          type: 'get',
          data: {
            systemUnitId: currUser.systemUnitId
          },
          async: false,
          success: function (result) {
            if (result.data) {
              $.each(result.data, function (i, d) {
                orgTypes.push({
                  id: d.id,
                  text: d.name
                });
              });
            }
          }
        });

        //可选的组织类型
        formBuilder.buildSelect2({
          container: $unitDiv,
          label: '组织类型',
          name: 'orgTypes',
          value: value.orgTypes,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          select2: {
            data: [
              {
                id: 'all',
                text: '全部'
              }
            ].concat(orgTypes),
            multiple: true
          }
        });

        // 默认组织类型
        formBuilder.buildSelect2({
          container: $unitDiv,
          label: '默认类型',
          name: 'defaultType',
          value: value.defaultType,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          select2: {
            data: orgTypes
          }
        });

        var ORG_NODE_TYPES = [
          {
            id: 'all',
            text: '全部'
          },
          {
            id: 'O',
            text: '组织'
          },
          {
            id: 'B',
            text: '单位'
          },
          {
            id: 'D',
            text: '部门'
          },
          {
            id: 'J',
            text: '职位'
          },
          {
            id: 'U',
            text: '人员'
          },
          {
            id: 'G',
            text: '群组'
          },
          {
            id: 'DU',
            text: '职务'
          }
        ];

        //可选的节点类型
        formBuilder.buildSelect2({
          container: $unitDiv,
          label: '节点类型',
          name: 'nodeTypes',
          value: value.nodeTypes,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          select2: {
            data: ORG_NODE_TYPES,
            multiple: true
          }
        });

        // 数据格式
        formBuilder.buildRadio({
          container: $unitDiv,
          label: '数据格式',
          name: 'valueFormat',
          value: value.valueFormat ? value.valueFormat : 'justId',
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          items: [
            {
              id: 'all',
              text: '完整格式'
            },
            {
              id: 'justId',
              text: '仅组织ID'
            }
          ]
        });

        //选择方式
        formBuilder.buildRadio({
          container: $unitDiv,
          label: '选择方式',
          name: 'multiple',
          value: value.multiple ? value.multiple : '0',
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          items: [
            {
              id: '0',
              text: '单选'
            },
            {
              id: '1',
              text: '多选'
            }
          ]
        });

        //选择风格
        formBuilder.buildSelect({
          container: $unitDiv,
          label: '选择风格',
          name: 'orgStyle',
          value: value.orgStyle,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          items: [
            {
              id: 'org-style0',
              text: '正常(分号分割)'
            },
            {
              id: 'org-style1',
              text: 'orgStyle1(实体带逗号)'
            },
            {
              id: 'org-style2',
              text: 'orgStyle2(分组带图标)'
            },
            {
              id: 'org-style3',
              text: 'orgStyle3(实体带图标)'
            }
          ]
        });

        formBuilder.buildTextarea({
          container: $unitDiv,
          label: '通讯录Json参数',
          name: 'jsonParams',
          placeholder: '{"key1":1,"ke2":true}',
          value: value.jsonParams,
          inputClass: 'w-custom-collect',
          rows: '3',
          labelColSpan: '3',
          controlColSpan: '9'
        });
      }
    },

    appendSelectQuery: function ($element, value) {
      if (value.queryType == 'radio') {
        this.appendCheckboxOption($element, value, [
          {
            label: '',
            name: 'useSwitchOpenStyle',
            items: [
              {
                id: '1',
                text: '启用开关按钮式（数据来源的数据项有且只要两个）'
              }
            ],
            labelColspan: '0',
            controlColSpan: '12'
          }
        ]);
      }

      if ($.inArray(value.queryType, ['checkbox', 'radio', 'select2']) != -1) {
        var $div = $('<div>').attr('id', 'div_query_type_select');
        $element.append($div);
        var $configDiv = $("<div id='div_optionType_config_info'/>");
        formBuilder.buildRadio({
          container: $div,
          label: '数据来源',
          name: 'optionType',
          value: value.optionType,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          items: [
            {
              id: '1',
              text: '常量'
            },
            {
              id: '2',
              text: '数据字典'
            },
            {
              id: '3',
              text: '数据仓库'
            },
            {
              id: '4',
              text: '未知'
            }
          ],
          events: {
            change: function () {
              var optionType = $(this).val();
              $configDiv.empty();
              bootstrapTable.queryFieldType.appendOptionValueConstant($configDiv, {
                queryType: value.queryType,
                optionType: optionType
              });
            }
          }
        });
        $div.append($configDiv);
        bootstrapTable.queryFieldType.appendOptionValueConstant($configDiv, value);
      }
      // 下拉框select2，配置是否多选
      if (value.queryType == 'select2') {
        this.appendCheckboxOption($div, value, [
          {
            label: '是否全选',
            name: 'chooseAll',
            items: [
              {
                id: '1',
                text: '启用全选'
              }
            ]
          },
          {
            label: '是否多选',
            name: 'multiple',
            items: [
              {
                id: '1',
                text: '启用多选'
              }
            ]
          }
        ]);

        var $searchable = $('<div>').attr('id', 'div_searchable_select');
        $element.append($searchable);
        formBuilder.buildRadio({
          container: $searchable,
          label: '启用搜索',
          name: 'searchable',
          value: value.searchable || '1',
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          items: [
            {
              id: '1',
              text: '是'
            },
            {
              id: '2',
              text: '否'
            }
          ]
        });
      }

      // 分组下拉框
      if (value.queryType == 'select2Group') {
        formBuilder.buildInput({
          container: $element,
          label: '数据接口',
          name: 'serviceUrl',
          value: value.serviceUrl,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9'
        });
      }
    },

    createDataSourceColumnOption: function ($element, value, columnOption) {
      var columnSelect2Options = {
        serviceName: 'viewComponentService',
        queryMethod: 'loadColumnsSelectData',
        params: function () {
          return {
            dataStoreId: $('#dataStore').val()
          };
        },
        remoteSearch: false
      };
      formBuilder.buildSelect2({
        container: $element,
        label: '数据仓库',
        name: 'dataStore',
        value: value.dataStore,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9',
        select2: {
          serviceName: 'viewComponentService',
          queryMethod: 'loadSelectData',
          remoteSearch: false,
          params: {
            piUuid: WebApp.currentUserAppData.piUuid
          }
        },
        events: {
          change: function () {
            for (var i = 0, len = columnOption.length; i < len; i++) {
              $('#' + columnOption[i].name).wSelect2('destroy');
              columnSelect2Options.valueField = columnOption[i].name;
              $('#' + columnOption[i].name).wSelect2(columnSelect2Options);
            }
          }
        }
      });
      for (var i = 0, len = columnOption.length; i < len; i++) {
        formBuilder.buildSelect2({
          container: $element,
          label: columnOption[i].label,
          name: columnOption[i].name,
          value: value[columnOption[i].name],
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          select2: columnSelect2Options
        });
      }
    },

    appendOptionValueConstant: function ($element, value) {
      if (value.optionType == '1') {
        //常量
        formBuilder.bootstrapTable.build({
          container: $element,
          name: 'optionValue',
          ediableNest: true,
          table: {
            data: value.optionValue,
            striped: true,
            idField: 'uuid',
            columns: [
              {
                field: 'checked',
                formatter: checkedFormat,
                checkbox: true
              },
              {
                field: 'uuid',
                title: 'UUID',
                visible: false,
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'id',
                title: '真实值',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'text',
                title: '展示值',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              }
            ]
          }
        });
      } else if (value.optionType == '2') {
        //数据字典
        formBuilder.buildWCommonComboTree({
          container: $element,
          label: '数据字典',
          name: 'dataDic',
          value: value.dataDic,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          wCommonComboTree: {
            service: 'dataDictionaryMaintain.getAllAsTree',
            serviceParams: '-1',
            width: '292px',
            multiSelect: false, // 是否多选
            parentSelect: true, // 父节点选择有效，默认无效
            onAfterSetValue: function (event, self, value) {}
          }
        });
        $('#dataDic').css('width', '100%');
        // 下拉框select2的空值选项
        if (value.queryType == 'select2') {
          formBuilder.buildSelect2({
            container: $element,
            label: '空值选项',
            name: 'defaultBlankId',
            value: value.defaultBlankId,
            display: 'defaultBlankText',
            displayValue: value.defaultBlankText,
            inputClass: 'w-custom-collect',
            labelColSpan: '3',
            controlColSpan: '9',
            remoteSearch: false,
            select2: {
              data: [
                {
                  id: '-1',
                  text: ''
                },
                {
                  id: '全部',
                  text: '全部'
                },
                {
                  id: '请选择',
                  text: '请选择'
                }
              ]
            },
            events: {
              change: function () {
                $('#valueColumn,#textColumn').wSelect2('destroy').wSelect2();
              }
            }
          });
        }
      } else if (value.optionType == '3') {
        //数据仓库
        this.createDataSourceColumnOption($element, value, [
          {
            label: '值字段',
            name: 'valueColumn'
          },
          {
            label: '展示字段',
            name: 'textColumn'
          }
        ]);
      }
    }
  };
  bootstrapTable.queryFieldType.input2value = function (value) {
    if (value.optionType == '1') {
      value.optionValue = this.$input.find('#table_optionValue_info').bootstrapTable('getData');
      value.optionValue = $.map(value.optionValue, clearChecked);
    }
    if (value.optionType == '2') {
      var valueNodes = this.$input.find('#dataDic').wCommonComboTree('getValueNodes');
      if (valueNodes[0]) {
        value.dataDic = valueNodes[0].id;
      }
    }
    return value;
  };

  bootstrapTable.queryFieldType.value2display = function (value) {
    return value.queryTypeLabel;
  };

  bootstrapTable.queryFieldType.value2input = function (value) {
    var $input = this.$input;
    this.options.value = value;
    // var $fieldSearchInfoTable = $("#table_field_search_info",
    // $container);
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '400');
    $input.empty();
    if (this.options.renderParams && this.options.renderParams.property_source) {
      value.queryType = 'select2';
      bootstrapTable.queryFieldType.renderQueryTypeOptionView($input, value);
      return;
    }
    formBuilder.buildSelect2({
      container: $input,
      label: '查询类型',
      name: 'queryType',
      display: 'queryTypeLabel',
      inputClass: 'w-custom-collect',
      value: value.queryType,
      displayValue: value.queryTypeLabel,
      labelColSpan: '3',
      controlColSpan: '9',
      select2: {
        data: [
          {
            id: 'text',
            text: '文本框'
          },
          {
            id: 'date',
            text: '日期框'
          },
          {
            id: 'unit',
            text: '组织弹出框'
          },
          {
            id: 'select2',
            text: '下拉框'
          },
          {
            id: 'select2Group',
            text: '分组下拉框'
          },
          {
            id: 'radio',
            text: '单选框'
          },
          {
            id: 'checkbox',
            text: '复选框'
          },
          {
            id: 'fileUpload',
            text: '上传文件'
          },
          {
            id: 'treeSelect',
            text: '下拉树形'
          }
        ]
      },
      events: {
        change: function () {
          // var queryType = $(this).select2('val');
          var queryType = $(this).wellSelect('val');
          bootstrapTable.queryFieldType.renderQueryTypeOptionView($input, {
            queryType: queryType
          });
        }
      }
    });
    bootstrapTable.queryFieldType.renderQueryTypeOptionView($input, value);
  };

  //校验规则
  bootstrapTable.validateRule = {
    renderValidateRuleOptionView: function ($input, value) {
      var $optionDiv = $input.find('.div_validate_rule_option');
      if ($optionDiv[0]) {
        $optionDiv.empty();
      } else {
        $optionDiv = $("<div class='div_validate_rule_option'></div>");
        $input.append($optionDiv);
      }
    }
  };
  bootstrapTable.validateRule.value2display = function (value) {
    return value.validateRuleLabel;
  };
  bootstrapTable.validateRule.input2value = function (value) {
    return value;
  };
  bootstrapTable.validateRule.value2input = function (value) {
    value.validateRuleLabel = StringUtils.isBlank(value.validateRegex) ? StringUtils.EMPTY : value.validateRuleLabel;
    var $input = this.$input;
    this.options.value = value;
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '400');
    $input.empty();
    formBuilder.buildSelect2({
      container: $input,
      label: '校验规则',
      name: 'validateRegex',
      display: 'validateRuleLabel',
      inputClass: 'w-custom-collect',
      value: value.validateRegex,
      displayValue: value.validateRuleLabel,
      labelColSpan: '3',
      controlColSpan: '9',
      select2: {
        data: [
          {
            id: '',
            text: ''
          },
          {
            id: 'notNull',
            text: '非空'
          },
          {
            id: '^[1-9]\\d*$',
            text: '整数'
          },
          {
            id: '(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]',
            text: 'URL'
          },
          {
            id: "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?",
            text: 'EMAIL'
          },
          {
            id: '^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$',
            text: '身份证'
          },
          {
            id: '^0?(13[0-9]|15[012356789]|18[012356789]|14[012356789]|16[012356789]|17[012356789])[0-9]{8}$',
            text: '手机号(13/14/15/16/17/18开头)'
          },
          {
            id: '^(0\\d{2,3}-?)?\\d{7,8}$',
            text: '电话'
          }
        ]
      },
      events: {
        change: function () {
          var validateRule = $(this).select2('data');
          bootstrapTable.validateRule.renderValidateRuleOptionView($input, {
            validateRegex: validateRule.id,
            validateRuleLabel: validateRule.text
          });
        }
      }
    });
    bootstrapTable.validateRule.renderValidateRuleOptionView($input, value);
  };

  // 3、标记据库表字段与实体类选择
  bootstrapTable.markTableOrEntity = {};
  bootstrapTable.markTableOrEntity.renderDataTypeOptions = function ($container, params) {
    if (params.dataType == 'entity') {
      //实体类选择
      formBuilder.buildSelect2({
        container: $container,
        label: '实体类',
        name: 'entityClasspath',
        value: params.value.entityClasspath,
        display: 'entityClassName',
        displayValue: params.value.entityClassName,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9',
        select2: {
          serviceName: 'select2DataStoreQueryService',
          queryMethod: 'loadEntityData',
          params: {
            superEntityClass: params.renderParams.superEntityClass
          },
          remoteSearch: false
        }
      });
    } else if (params.dataType == 'table') {
      //数据库表选择
      formBuilder.buildSelect2({
        container: $container,
        label: '数据库表',
        labelClass: 'required',
        name: 'databaseTable',
        value: params.value.databaseTable,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9',
        select2: {
          serviceName: 'cdDataStoreDefinitionService',
          queryMethod: 'loadSelectDataByTable',
          remoteSearch: false
        },
        events: {
          change: function () {
            $('.tableColumnSelectDiv', $container).remove();
            var getColumnDatas = function () {
              var columnDatas = [];
              var tableName = $('#databaseTable', $container).val();
              if (!tableName) {
                return columnDatas;
              }
              // server.JDS.call({
              //   service: 'cdDataStoreDefinitionService.loadTableColumns',
              //   data: [tableName],
              //   version: '',
              //   success: function (res) {
              //     if (res.success && res.data) {
              server.JDS.restfulGet({
                url: `/proxy/api/datastore/loadTableColumns/${tableName}`,
                success: function (res) {
                  if (res.code == 0 && res.data) {
                    for (var d = 0, dlen = res.data.length; d < dlen; d++) {
                      columnDatas.push({
                        id: res.data[d].columnIndex,
                        text: res.data[d].columnName
                      });
                    }
                  }
                },
                async: false
              });
              return columnDatas;
            };
            formBuilder.buildSelect2({
              container: $container,
              label: '状态字段',
              labelClass: 'required',
              name: 'statusColumn',
              value: params.value.statusColumn,
              inputClass: 'w-custom-collect',
              divClass: 'tableColumnSelectDiv',
              labelColSpan: '3',
              controlColSpan: '9',
              select2: {
                data: getColumnDatas()
              }
            });

            formBuilder.buildSelect2({
              container: $container,
              label: '更新时间字段',
              name: 'updateTimeColumn',
              value: params.value.updateTimeColumn,
              inputClass: 'w-custom-collect',
              divClass: 'tableColumnSelectDiv',
              labelColSpan: '3',
              controlColSpan: '9',
              select2: {
                data: getColumnDatas()
              }
            });
          }
        }
      });

      if (!$.isEmptyObject(params.value)) {
        $('#databaseTable', $container).trigger('change');
      }
    }
  };
  bootstrapTable.markTableOrEntity.value2input = function (value) {
    var $input = this.$input;
    var renderParams = this.options.renderParams;
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '450');
    $input.empty();
    value = value || {};
    var $dataTypeOptionContainer = $('<div>', {
      id: 'dataTypeOptionContainer'
    });
    var $operationTipContainer = $('<div>', {
      id: 'operationTipContainer'
    });
    formBuilder.buildSelect2({
      container: $input,
      label: '数据类型',
      labelClass: 'required',
      name: 'markType',
      value: value.markType,
      display: 'markTypeName',
      displayValue: value.markTypeName,
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      select2: {
        serviceName: 'dmsDataMarkFacadeService',
        queryMethod: 'loadDataMarkType',
        remoteSearch: false
      },
      events: {
        change: function () {
          $dataTypeOptionContainer.empty();
          $operationTipContainer.empty();
          bootstrapTable.markTableOrEntity.renderDataTypeOptions($dataTypeOptionContainer, {
            renderParams: renderParams,
            dataType: $(this).val(),
            value: value
          });

          formBuilder.buildLabel({
            divClass: 'operationTip',
            container: $operationTipContainer,
            text:
              $(this).val() == 'table'
                ? '状态字段：更新标记状态值的状态字段，取值为1或者0状态值， 1=标记 0=取消标记</br>' + '更新时间字段：更新状态时的时间字段'
                : '实体类必须继承' + renderParams.superEntityClass,
            name: 'tip',
            inputClass: 'text-info',
            labelColSpan: '0',
            controlColSpan: '12'
          });
        }
      }
    });
    $input.append($dataTypeOptionContainer, $operationTipContainer);
    if (!$.isEmptyObject(value)) {
      $('#markType', $input).trigger('change');
    }
  };
  bootstrapTable.markTableOrEntity.input2value = function (value) {
    var $input = this.$input;
    var markType = $('#markType', $input).val();
    if (markType) {
      value.markType = markType;
    }
    var markTypeName = $('#markTypeName', $input).val();
    if (markTypeName) {
      value.markTypeName = markTypeName;
    }
    var databaseTable = $('#databaseTable', $input).val();
    if (databaseTable) {
      value.databaseTable = databaseTable;
    }
    var statusColumn = $('#statusColumn', $input).val();
    if (statusColumn) {
      value.statusColumn = statusColumn;
    }
    var updateTimeColumn = $('#updateTimeColumn', $input).val();
    if (updateTimeColumn) {
      value.updateTimeColumn = updateTimeColumn;
    }
    var entityClassName = $('#entityClassName', $input).val();
    if (entityClassName) {
      value.entityClassName = entityClassName;
    }
    var entityClasspath = $('#entityClasspath', $input).val();
    if (entityClasspath) {
      value.entityClasspath = entityClasspath;
    }
    return value;
  };
  bootstrapTable.markTableOrEntity.value2display = function (value) {
    return value.markTypeName;
  };
  bootstrapTable.markTableOrEntity.value2html = function (value, element) {
    return $(element).html(
      '<code>' + (value.entityClassName ? value.entityClassName : value.databaseTable + '.' + value.statusColumn) + '</code>'
    );
  };

  //表格行样式选择
  bootstrapTable.tableRowStyle = {};
  bootstrapTable.tableRowStyle.value2input = function (value) {
    var $input = this.$input;
    value = value || {};
    bootstrapTable.icon.value2input.call(this, value); //生成图标按钮选择
    formBuilder.buildSelect2({
      container: $input,
      label: '情境文本',
      name: 'textStyleClass',
      value: value.textStyleClass,
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      defaultBlank: true,
      select2: {
        data: (function () {
          var options = [];
          options.push(
            {
              id: 'text-primary',
              text: 'text-primary'
            },
            {
              id: 'text-success',
              text: 'text-success'
            },
            {
              id: 'text-info',
              text: 'text-info'
            },
            {
              id: 'text-warning',
              text: 'text-warning'
            },
            {
              id: 'text-danger',
              text: 'text-danger'
            }
          );
          return options;
        })(),
        remoteSearch: false
      }
    });

    formBuilder.buildSelect2({
      container: $input,
      label: '情境背景',
      name: 'bgStyleClass',
      value: value.bgStyleClass,
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      defaultBlank: true,
      select2: {
        data: (function () {
          var options = [];
          options.push(
            {
              id: 'bg-primary',
              text: 'bg-primary'
            },
            {
              id: 'bg-success',
              text: 'bg-success'
            },
            {
              id: 'bg-info',
              text: 'bg-info'
            },
            {
              id: 'bg-warning',
              text: 'bg-warning'
            },
            {
              id: 'bg-danger',
              text: 'bg-danger'
            }
          );
          return options;
        })(),
        remoteSearch: false
      }
    });

    formBuilder.buildCheckbox({
      container: $input,
      label: ' ',
      name: 'fontBolder',
      value: value.fontBolder,
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      items: [
        {
          id: '1',
          text: '字体加粗'
        }
      ]
    });

    if (!$.isEmptyObject(value)) {
      $('.w-custom-collect', $input).trigger('change');
    }

    $input.css('width', '350');
  };
  bootstrapTable.tableRowStyle.input2value = function (value) {
    var $input = this.$input;
    var textStyleClass = $('#textStyleClass', $input).val();
    if (textStyleClass) {
      value.textStyleClass = textStyleClass;
    }
    var bgStyleClass = $('#bgStyleClass', $input).val();
    if (bgStyleClass) {
      value.bgStyleClass = bgStyleClass;
    }
    var fontBolder = $(':checkbox[name=fontBolder]:checked', $input).val();
    if (fontBolder) {
      value.fontBolder = fontBolder;
    }
    bootstrapTable.icon.input2value.call(this, value);
    return value;
  };
  bootstrapTable.tableRowStyle.value2display = function (value) {
    return value.className + ' ' + value.textStyleClass + ' ' + value.bgStyleClass;
  };
  bootstrapTable.tableRowStyle.value2html = function (value, element) {
    var icon = '';
    if (value.className) {
      icon = "<span class='" + value.className + "' aria-hidden='true' style='color:" + value.iconColor + ";'></span>";
    }
    var text = '';
    if (value.textStyleClass) {
      text = "&nbsp;<span class='" + value.textStyleClass + "'>" + value.textStyleClass + '</span>';
    }
    var bg = '';
    if (value.bgStyleClass) {
      bg = '&nbsp;<span class="' + value.bgStyleClass + '">' + value.bgStyleClass + '</span>';
    }
    var fontBolder = '';
    if (value.fontBolder) {
      fontBolder = '&nbsp;<label style="font-weight: bolder">字体加粗</label>';
    }
    $(element).html(icon + text + bg + fontBolder);
    return icon + text + bg;
  };

  //表格按钮选择
  bootstrapTable.tableButtons = {};
  bootstrapTable.tableButtons.value2input = function (value) {
    var $input = this.$input;
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '500');
    $input.empty();
    value = value || {};

    formBuilder.buildSelect2({
      container: $input,
      label: '操作按钮',
      name: 'buttonClass',
      value: value.buttonClass,
      display: 'buttonName',
      displayValue: value.buttonName,
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      select2: {
        data: (function () {
          var colDatas = [];
          var buttonColumnData = $('#table_button_info').data('bootstrap.table').data;
          for (var i = 0; i < buttonColumnData.length; i++) {
            colDatas.push({
              id: buttonColumnData[i].code,
              text: buttonColumnData[i].text
            });
          }
          return colDatas;
        })(),
        remoteSearch: false
      }
    });
  };
  bootstrapTable.tableButtons.input2value = function (value) {
    var $input = this.$input;
    var buttonClass = $('#buttonClass', $input).val();
    if (buttonClass) {
      value.buttonClass = buttonClass;
    }
    var buttonName = $('#buttonName', $input).val();
    if (buttonName) {
      value.buttonName = buttonName;
    }

    return value;
  };
  bootstrapTable.tableButtons.value2display = function (value) {
    return value.buttonClass;
  };
  bootstrapTable.tableButtons.value2html = function (value, element) {
    return $(element).text(value.buttonName);
  };

  // 3、实体类选择
  bootstrapTable.entityClass = {};
  bootstrapTable.entityClass.value2input = function (value) {
    var $input = this.$input;
    var renderParams = this.options.renderParams;
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '450');
    $input.empty();
    value = value || {};
    formBuilder.buildSelect2({
      container: $input,
      label: '实体类',
      name: 'entityClasspath',
      labelClass: 'required',
      value: value.entityClasspath,
      display: 'entityClassName',
      displayValue: value.entityClassName,
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      select2: {
        serviceName: 'select2DataStoreQueryService',
        queryMethod: 'loadEntityData',
        params: {
          superEntityClass: renderParams.superEntityClass
        },
        remoteSearch: false
      }
    });

    formBuilder.buildLabel({
      divClass: 'operationTip',
      container: $input,
      label: '实体类必须继承' + renderParams.superEntityClass,
      name: 'tip',
      labelClass: 'text-info',
      labelColSpan: '12',
      controlColSpan: '0'
    });
  };
  bootstrapTable.entityClass.input2value = function (value) {
    var $input = this.$input;
    var entityClassName = $('#entityClassName', $input).val();
    if (entityClassName) {
      value.entityClassName = entityClassName;
    }
    var entityClasspath = $('#entityClasspath', $input).val();
    if (entityClasspath) {
      value.entityClasspath = entityClasspath;
    }
    return value;
  };
  bootstrapTable.entityClass.value2display = function (value) {
    return value.entityClassName ? value.entityClassName : '';
  };
  bootstrapTable.entityClass.value2html = function (value, element) {
    if (!$.isEmptyObject(value)) {
      return $(element).html('<code>' + value.entityClassName + '</code>');
    }
  };

  //渲染器
  bootstrapTable.renderOption = {};
  var renderOptionView = function ($input, value) {
    var $optionDiv = $input.find('.div_renderer_type_option');
    if ($optionDiv[0]) {
      $optionDiv.empty();
    } else {
      $optionDiv = $("<div class='div_renderer_type_option'></div>");
      $input.append($optionDiv);
    }
    if (value.rendererType && value.rendererType != '') {
      formBuilder.buildSelect2({
        container: $optionDiv,
        label: '是否延迟加载',
        name: 'loadType',
        value: value.loadType || '0', // 默认值
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9',
        select2: {
          data: [
            {
              id: '0',
              text: '否'
            },
            {
              id: '1',
              text: '是'
            }
          ]
        }
      });
    }
    if (value.rendererType == 'dateRenderer') {
      formBuilder.buildSelect2({
        container: $optionDiv,
        label: '日期格式',
        name: 'format',
        value: value.format || 'yyyy-MM-dd', // 默认值
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9',
        select2: {
          data: [
            {
              id: 'yyyy-MM-dd',
              text: '日期(2000-01-01)'
            },
            {
              id: 'yyyy',
              text: '年份(2000年)'
            },
            {
              id: 'yyyy年MM月',
              text: '日期(2000年01月)'
            },
            {
              id: 'MM月dd日',
              text: '日期(01月01日)'
            },
            {
              id: 'dd日',
              text: '日期(01日)'
            },
            {
              id: 'HH',
              text: '时间(12)'
            },
            {
              id: 'HH:mm',
              text: '时间到分(12:00)'
            },
            {
              id: 'HH:mm:ss',
              text: '时间到秒(12:00:00)'
            },
            {
              id: 'yyyy-MM-dd HH',
              text: '日期到时 (2000-01-01 12)'
            },
            {
              id: 'yyyy-MM-dd HH:mm',
              text: '日期到分 (2000-01-01 12:00)'
            },
            {
              id: 'yyyy-MM-dd HH:mm:ss',
              text: '日期到秒 (2000-01-01 12:00:00)'
            }
          ]
        }
      });
    } else if (value.rendererType == 'deptRenderer') {
      formBuilder.buildRadio({
        container: $optionDiv,
        label: '展示类型',
        name: 'showType',
        value: value.showType,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9',
        items: [
          {
            id: '0',
            text: '部门路径'
          },
          {
            id: '1',
            text: '部门名称'
          }
        ]
      });
    } else if (value.rendererType == 'jobRenderer') {
      formBuilder.buildRadio({
        container: $optionDiv,
        label: '展示类型',
        name: 'showType',
        value: value.showType,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9',
        items: [
          {
            id: '0',
            text: '职位路径'
          },
          {
            id: '1',
            text: '职位名称'
          }
        ]
      });
    } else if (value.rendererType == 'freemarkerTemplateRenderer') {
      formBuilder.buildTextarea({
        container: $optionDiv,
        label: '模板内容',
        name: 'template',
        value: value.template,
        inputClass: 'w-custom-collect',
        rows: '6',
        labelColSpan: '3',
        controlColSpan: '9'
      });
      formBuilder.buildLabel({
        container: $optionDiv,
        label: '模板变量',
        name: 'variable',
        text: '行数据${rowData}，通过${rowData.字段名}访问列值<br/>当前行数据索引${rowData.dataIndex}<br/>当前字段名(列索引)${columnIndex}<br/>当前列值${value}',
        rows: '3',
        labelColSpan: '3',
        controlColSpan: '9'
      });
    } else if (value.rendererType == 'JsonPathRenderer') {
      formBuilder.buildTextarea({
        container: $optionDiv,
        label: 'JsonPath',
        name: 'jsonpath',
        value: value.jsonpath,
        inputClass: 'w-custom-collect',
        placeholder: '请输入jsonpath支持的语法表达式，例如：$..book[0]',
        rows: '6',
        labelColSpan: '3',
        controlColSpan: '9'
      });
      formBuilder.buildLabel({
        container: $optionDiv,
        label: ' ',
        name: 'jsonpath',
        text: '<a href="/resources/jsonpath/jsonpath.html" target="_blank">jsonpath使用说明</a>',
        rows: '6',
        labelColSpan: '3',
        controlColSpan: '9'
      });
    } else if (value.rendererType === 'dataDictionaryRenderer') {
      formBuilder.buildWCommonComboTree({
        container: $optionDiv,
        label: '数据字典',
        name: 'dataDic',
        value: value.dataDic,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9',
        wCommonComboTree: {
          service: 'dataDictionaryMaintain.getAllAsTree',
          serviceParams: '-1',
          width: '292px',
          multiSelect: false, // 是否多选
          parentSelect: true, // 父节点选择有效，默认无效
          onAfterSetValue: function (event, self, value) {}
        }
      });
      $('#dataDic').css('width', '100%');
    } else if (value.rendererType == 'enumClassRenderer') {
      var enumFieldSelect2Options = {
        serviceName: 'enumClassSelect2QueryService',
        queryMethod: 'loadEnumClassFieldsSelectData',
        params: function () {
          return {
            enumClass: $('#enumClass', $optionDiv).val()
          };
        },
        remoteSearch: false
      };
      formBuilder.buildSelect2({
        container: $optionDiv,
        label: '枚举类',
        name: 'enumClass',
        inputClass: 'w-custom-collect',
        value: value.enumClass,
        display: 'enumClassName',
        displayValue: value.enumClassName,
        labelColSpan: '3',
        controlColSpan: '9',
        select2: {
          serviceName: 'enumClassSelect2QueryService',
          queryMethod: 'queryAll4SelectOptions',
          defaultBlank: true,
          remoteSearch: false
        },
        events: {
          change: function () {
            $('#valueColumn,#textColumn', $optionDiv).wSelect2('destroy').wSelect2(enumFieldSelect2Options);
          }
        }
      });
      formBuilder.buildSelect2({
        container: $optionDiv,
        label: '值字段',
        name: 'valueField',
        value: value.valueField,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9',
        select2: enumFieldSelect2Options
      });
      formBuilder.buildSelect2({
        container: $optionDiv,
        label: '展示字段',
        name: 'textField',
        value: value.textField,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9',
        select2: enumFieldSelect2Options
      });
    } else if (value.rendererType == 'customRenderer') {
      formBuilder.buildSelect2({
        container: $optionDiv,
        label: '自定义类型',
        name: 'customType',
        inputClass: 'w-custom-collect',
        value: value.customType,
        display: 'customTypeRenderName',
        displayValue: value.customTypeRenderName,
        labelColSpan: '3',
        controlColSpan: '9',
        select2: {
          serviceName: 'viewComponentService',
          queryMethod: 'loadRendererSelectData',
          params: {
            type: '3'
          },
          defaultBlank: true,
          remoteSearch: false
        }
      });
      formBuilder.buildTextarea({
        container: $optionDiv,
        label: '扩展参数',
        name: 'params',
        value: value.params,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9'
      });
    }
  };

  bootstrapTable.renderOption.value2input = function (value) {
    var $input = this.$input;
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '400');
    $input.empty();
    formBuilder.buildSelect2({
      container: $input,
      label: '渲染器类型',
      name: 'rendererType',
      display: 'rendererName',
      displayValue: value.rendererName,
      inputClass: 'w-custom-collect',
      value: value.rendererType,
      labelColSpan: '3',
      controlColSpan: '9',
      select2: {
        serviceName: 'viewComponentService',
        queryMethod: 'loadRendererSelectData',
        params: {
          type: '2'
        },
        defaultBlank: true,
        remoteSearch: false
      },
      events: {
        change: function () {
          var type = $(this).select2('val');
          renderOptionView($input, {
            rendererType: type
          });
        }
      }
    });
    renderOptionView($input, value);
  };

  //按钮约束
  bootstrapTable.btnConstraint = {};
  var renderConstraintView = function ($input, value) {
    var $optionDiv = $input.find('.div_renderer_type_option');
    if ($optionDiv[0]) {
      $optionDiv.empty();
    } else {
      $optionDiv = $("<div class='div_renderer_type_option'></div>");
      $input.append($optionDiv);
    }

    formBuilder.buildTextarea({
      container: $optionDiv,
      label: '约束条件',
      name: 'template',
      value: value.template || '',
      inputClass: 'w-custom-collect',
      rows: '6',
      labelColSpan: '3',
      controlColSpan: '9'
    });
    formBuilder.buildLabel({
      container: $optionDiv,
      label: '约束条件变量',
      name: 'variable',
      text: "直接通过 row.字段code 获取对应表单字段支持多条件组合(|| / &&)<br>例子：row.field > 10 || row.filed == 'xxx'<br>条件成立时显示按钮",
      rows: '3',
      labelColSpan: '3',
      controlColSpan: '9'
    });
  };

  bootstrapTable.btnConstraint.value2input = function (value) {
    var $input = this.$input;
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '400');
    $input.empty();
    renderConstraintView($input, value);
  };

  bootstrapTable.btnConstraint.value2display = function (value) {
    return value.template;
  };

  bootstrapTable.btnConstraint.value2html = function (value, element) {
    return $(element).html(value.template);
  };

  bootstrapTable.btnConstraint.input2value = function (value) {
    return value;
  };

  bootstrapTable.renderOption.value2display = function (value) {
    if (value.rendererType == 'customRenderer') {
      return value.rendererName + '：' + value.customTypeRenderName;
    }
    return value.rendererName;
  };

  bootstrapTable.renderOption.value2html = function (value, element) {
    if (value.rendererType == 'customRenderer') {
      return $(element).html(value.rendererName + '<br>' + value.customTypeRenderName);
    }
    return $(element).html(value.rendererName);
  };

  bootstrapTable.renderOption.input2value = function (value) {
    // 数据字典取字典UUID
    if (value.rendererType == 'dataDictionaryRenderer') {
      var valueNodes = this.$input.find('#dataDic').wCommonComboTree('getValueNodes');
      if (valueNodes[0]) {
        value.dataDic = valueNodes[0].id;
      }
    }
    return value;
  };

  //按钮多选
  bootstrapTable.multiSelectButtons = {
    value2input: function (value) {
      var $input = this.$input;
      $input.closest('form').removeClass('form-inline');
      $input.css('width', '400');
      $input.empty();
      value = value || {};
      var dataSource = this.options.renderParams.source;
      $input.append(
        $('<input>', {
          type: 'hidden',
          id: 'btnNames',
          name: 'btnNames',
          value: value.btnNames
        })
      );
      formBuilder.buildSelect2Group({
        container: $input,
        label: '选择按钮',
        labelClass: 'required',
        name: 'btnCodes',
        value: value.btnCodes,
        inputClass: 'w-custom-collect',
        labelColSpan: '3',
        controlColSpan: '9',
        select2Group: {
          data: dataSource,
          remoteSearch: false,
          labelField: 'btnNames'
        }
      });
    },
    input2value: function (value) {
      var btnCodes = $('#btnCodes', this.$input).val();
      if (btnCodes) {
        value.btnCodes = btnCodes;
      }
      var btnNames = $('#btnNames', this.$input).val();
      if (btnNames) {
        value.btnNames = btnNames;
      }
      return value;
    },
    value2display: function (value) {
      return value.btnNames;
    },
    value2html: function (value, element) {
      if (!$.isEmptyObject(value)) {
        return $(element).text(value.btnNames ? value.btnNames : '');
      }
    }
  };

  // 3、js代码编辑器
  bootstrapTable.jsCodeEditor = {};
  bootstrapTable.jsCodeEditor.value2input = function (value) {
    var $input = this.$input;
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '500');
    $input.empty();
    value = value || {};
    formBuilder.buildPre({
      container: $input,
      label: '',
      name: 'jsCodeEditor',
      value: value.codes || '',
      inputClass: 'w-custom-collect',
      labelColSpan: '0',
      controlColSpan: '12'
    });
    var editor = ace.edit('jsCodeEditor');
    editor.setTheme('ace/theme/chrome');
    editor.session.setMode('ace/mode/javascript');
    //启用提示菜单
    ace.require('ace/ext/language_tools');
    var _component = appPageDesigner.configureComponent;
    editor.setOptions({
      enableBasicAutocompletion: true,
      enableSnippets: true,
      showPrintMargin: false,
      enableLiveAutocompletion: true,
      enableCodeHis: {
        relaBusizUuid: _component.options.id,
        codeType: value.codeType,
        enable: true
      }
    });

    $input.data('editor', editor);
  };
  bootstrapTable.jsCodeEditor.input2value = function (value) {
    return {
      codes: this.$input.data('editor').getValue()
    };
  };
  bootstrapTable.jsCodeEditor.value2display = function (value) {
    return value.codes;
  };
  bootstrapTable.jsCodeEditor.value2html = function (value, element) {
    if (!$.isEmptyObject(value) && value.codes) {
      $(element).html('<pre style="padding:5px;">' + value.codes + '</pre>');
      $(element).on('click', 'pre', function (event) {
        $(this).parent().click();
        event.stopImmediatePropagation();
      });
    } else {
      $(element).html('');
    }
  };

  // 3、自定义脚本
  bootstrapTable.defineJavascriptEvent = {};
  bootstrapTable.defineJavascriptEvent.value2input = function (value) {
    var $input = this.$input;
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '450');
    $input.empty();
    value = value || {};
    var tablevalue = [];
    var i = 1;
    for (var k in value) {
      tablevalue.push({
        uuid: i++,
        eventType: k,
        eventCode: {
          codes: value[k]
        }
      });
    }
    (function ($this, v) {
      formBuilder.bootstrapTable.build({
        container: $input,
        name: 'jscodes',
        ediableNest: true,
        help: '',
        table: {
          data: v,
          striped: true,
          idField: 'uuid',
          width: 300,
          columns: [
            {
              field: 'checked',
              formatter: DesignUtils.checkedFormat,
              checkbox: true
            },
            {
              field: 'uuid',
              title: 'UUID',
              visible: false,
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'eventType',
              title: '事件类型',
              editable: {
                type: 'select',
                mode: 'inline',
                showbuttons: false,
                source: [
                  {
                    value: 'click',
                    text: '点击事件'
                  },
                  {
                    value: 'mouseenter',
                    text: '鼠标移入事件'
                  },
                  {
                    value: 'mouseover',
                    text: '鼠标移出事件'
                  },
                  {
                    value: 'init',
                    text: '初始化事件'
                  }
                ]
              }
            },
            {
              field: 'eventCode',
              title: '事件脚本',
              editable: {
                type: 'wCustomForm',
                placement: 'bottom',
                savenochange: true,
                value2input: bootstrapTable.jsCodeEditor.value2input,
                input2value: bootstrapTable.jsCodeEditor.input2value,
                value2html: bootstrapTable.jsCodeEditor.value2html
              }
            }
          ]
        }
      });
    })($input, tablevalue);
  };
  bootstrapTable.defineJavascriptEvent.input2value = function (value) {
    var $table = this.$input.find('table');
    var datas = $table.bootstrapTable('getData');
    for (var i = 0, len = datas.length; i < len; i++) {
      value[datas[i].eventType] = datas[i].eventCode.codes;
    }
    return value;
  };
  bootstrapTable.defineJavascriptEvent.value2display = function (value) {
    var displayText = '';
    for (var k in value) {
      displayText += k + ';';
    }
    return displayText;
  };
  bootstrapTable.defineJavascriptEvent.value2html = function (value, element) {
    if (!$.isEmptyObject(value)) {
      var displayText = '';
      for (var k in value) {
        displayText += k + ';';
      }
      return $(element).html('<code>' + displayText + '</code>');
    }
  };

  // 3、定义事件管理
  bootstrapTable.eventManager = {};
  bootstrapTable.eventManager.value2input = function (value) {
    var $input = this.$input;
    var $bootbox = $input.parents('.bootbox');
    $bootbox.addClass('primary-bg');
    if (!$bootbox.hasClass('ui-draggable')) {
      $input.parents('.bootbox ').draggable({
        handle: '.modal-header',
        cursor: 'move',
        refreshPositions: false
      });
    }
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '550');
    $input.empty();
    value = value || {};
    var defineJs = true; //是否自定义脚本事件
    if (this.options.renderParams && this.options.renderParams.defineJs != undefined) {
      defineJs = this.options.renderParams.defineJs;
    }
    var eventUseFor = null; //事件管理用处
    if (this.options.renderParams && this.options.renderParams.eventUseFor != undefined) {
      eventUseFor = this.options.renderParams.eventUseFor;
    }
    var $container = $('<div>', {
      id: 'eventMgrContainer'
    });
    var $ul = $('<ul>', {
      class: 'nav nav-tabs',
      role: 'tablist',
      id: 'eventTab'
    });
    if (defineJs) {
      $ul.append(
        $('<li>', {
          role: 'presentation',
          class: 'active'
        }).append(
          $('<a>', {
            href: '#selfDefineJsEventCode'
          }).html('自定义事件脚本')
        )
      );
    }
    $ul.append(
      $('<li>', {
        role: 'presentation',
        class: !defineJs ? 'active' : ''
      }).append(
        $('<a>', {
          href: '#eventHanlderContainer'
        }).html('引用事件处理')
      )
    );
    $container.append(
      $ul,
      $('<div>', {
        class: 'tab-content',
        style: 'min-height:200px'
      }).append(
        $('<div>', {
          class: defineJs ? 'tab-pane active' : 'tab-pane',
          role: 'tabpanel',
          id: 'selfDefineJsEventCode'
        }),
        $('<div>', {
          class: !defineJs ? 'tab-pane active' : 'tab-pane',
          role: 'tabpanel',
          id: 'eventHanlderContainer'
        })
      )
    );
    $input.append($container);
    $input.find('#eventTab a').click(function (e) {
      e.preventDefault();
      $(this).tab('show');
    });

    //自定义事件脚本
    var $div = $('<div>', {
      class: 'form-group formbuilder clear',
      id: 'defineEventJs'
    });
    $div.append(
      $('<div>', {
        class: 'col-xs-3 controls'
      }),
      $('<div>', {
        class: 'col-xs-9 controls eventCodeInputDiv'
      })
    );
    $input.find('#selfDefineJsEventCode').append($div);
    $input
      .find('#defineEventJs')
      .find('div:eq(0)')
      .append(
        $('<div>', {
          class: 'btn-group-vertical',
          role: 'group',
          id: 'eventBtnGroup'
        }).append(
          $('<button>', {
            type: 'button',
            class: 'btn btn-primary btn-sm',
            _target: 'initEvent'
          }).text('初始化事件'),
          WebApp.containerDefaults && WebApp.containerDefaults.wtype !== 'wPage'
            ? ''
            : ($('<button>', {
                type: 'button',
                class: 'btn btn-sm',
                _target: 'clickEvent'
              }).text('鼠标点击事件'),
              $('<button>', {
                type: 'button',
                class: 'btn btn-sm',
                _target: 'mouseEnterEvent'
              }).text('鼠标移入事件'),
              $('<button>', {
                type: 'button',
                class: 'btn btn-sm',
                _target: 'mouseLeaveEvent'
              }).text('鼠标移出事件'))
        )
      );
    $input
      .find('#defineEventJs')
      .find('.eventCodeInputDiv')
      .append(
        $('<div>', {
          id: 'initEvent',
          class: 'codeEditDiv'
        }).append(
          $('<pre>', {
            style: 'min-height:200px',
            id: 'init'
          })
        ),
        $('<div>', {
          id: 'clickEvent',
          style: 'display:none',
          class: 'codeEditDiv'
        }).append(
          $('<pre>', {
            style: 'min-height:200px',
            id: 'click'
          })
        ),
        $('<div>', {
          id: 'mouseEnterEvent',
          style: 'display:none',
          class: 'codeEditDiv'
        }).append(
          $('<pre>', {
            style: 'min-height:200px',
            id: 'mouseenter'
          })
        ),
        $('<div>', {
          id: 'mouseLeaveEvent',
          style: 'display:none',
          class: 'codeEditDiv'
        }).append(
          $('<pre>', {
            style: 'min-height:200px',
            id: 'mouseleave'
          })
        )
      );
    $input.find('#defineEventJs button').each(function () {
      $(this).on('click', function () {
        $('.eventCodeInputDiv').find('.codeEditDiv').hide();
        $('.eventCodeInputDiv')
          .find('#' + $(this).attr('_target'))
          .show();
        $('#eventBtnGroup .btn-primary').removeClass('btn-primary');
        $(this).addClass('btn-primary');
      });
    });
    //代码输入框初始化
    $input
      .find('#defineEventJs')
      .find('pre')
      .each(function () {
        if (value.defineEventJs && value.defineEventJs[$(this).attr('id')]) {
          $(this).text(value.defineEventJs[$(this).attr('id')]);
        }
        var editor = ace.edit($input.find('#' + $(this).attr('id'))[0]);
        editor.setTheme('ace/theme/chrome');
        editor.session.setMode('ace/mode/javascript');
        //启用提示菜单
        ace.require('ace/ext/language_tools');

        if (window.appPageDesigner) {
          var _component = appPageDesigner.configureComponent;
          var id = _component.options.id;
          var type = _component.options.wtype;
        } else {
          var id = new UUID();
          var type = new UUID();
        }
        editor.setOptions({
          enableBasicAutocompletion: true,
          enableSnippets: true,
          showPrintMargin: false,
          enableLiveAutocompletion: true,
          enableVarSnippets: 'wBootstrapTable.defineButtonEvent',
          enableCodeHis: {
            relaBusizUuid: id,
            codeType: type + '.' + $(this).attr('id'),
            enable: true
          }
        });

        $(this).data('editor', editor);
      });

    var $eventHanlderContainer = $input.find('#eventHanlderContainer');
    formBuilder.buildInput({
      container: $eventHanlderContainer,
      label: '事件处理',
      name: 'eventHandlerName',
      value: value.eventHandler ? value.eventHandler.name : '',
      labelColSpan: '3',
      controlColSpan: '9'
    });

    $input.find('input[name=eventHandlerName]').AppEvent({
      idValue: value.eventHandler ? value.eventHandler.id : '',
      ztree: {
        params: [appContext.getCurrentUserAppData().getSystem().productUuid]
      },
      okCallback: function ($el, data) {
        if (data) {
          // 锚点设置
          $("input[name='eventHashType']", $eventHanlderContainer).removeAttr('checked');
          $("input[name='eventHashType']", $eventHanlderContainer).trigger('change');
          if (DesignUtils.isSupportsAppHashByAppPath(data.data.path)) {
            $("input[name='eventHashType']", $eventHanlderContainer).removeAttr('disabled');
          } else {
            $("input[name='eventHashType']", $eventHanlderContainer).attr('disabled', 'disabled');
          }
          $("input[name='eventHash']", $eventHanlderContainer).val('');
          $('#eventHashTree', $eventHanlderContainer).wCommonComboTree({
            value: ''
          });
        }
      },
      clearCallback: function ($el) {
        // 锚点设置
        $("input[name='eventHashType']", $eventHanlderContainer).removeAttr('checked');
        $("input[name='eventHashType']", $eventHanlderContainer).trigger('change');
        $("input[name='eventHashType']", $eventHanlderContainer).attr('disabled', 'disabled');
        $("input[name='eventHash']", $eventHanlderContainer).val('');
        $('#eventHashTree', $eventHanlderContainer).wCommonComboTree({
          value: ''
        });
      }
    });
    $input.find('input[name=eventHandlerName]').data('initEventHandler', value.eventHandler || {});

    $input.find('input[name=eventHandlerName]').css({
      width: '100%'
    });

    // 锚点设置
    formBuilder.buildRadio({
      container: $eventHanlderContainer,
      label: '锚点设置',
      name: 'eventHashType',
      value: value.eventHandler ? value.eventHandler.hashType : '',
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      divClass: 'eventHashTypeDiv',
      items: [
        {
          id: '1',
          text: '指定锚点'
        },
        {
          id: '2',
          text: '自定义'
        }
      ],
      events: {
        change: function () {
          var eventHashType = $("input[name='" + this.name + "']:checked", $eventHanlderContainer).val();
          $('.eventHashTypeRow', $eventHanlderContainer).hide();
          if (eventHashType == '1') {
            $('.eventHashType' + eventHashType, $eventHanlderContainer).show();
            var appPath = value.eventHandler ? value.eventHandler.path : '';
            var selectEventData = $('#eventHandlerName', $eventHanlderContainer).data('eventData');
            if (selectEventData) {
              appPath = selectEventData.path;
            }
            // 事件处理
            $('#eventHashTree', $eventHanlderContainer).wCommonComboTree({
              service: 'pageDefinitionService.getAppHashTreeByAppPath',
              serviceParams: [appPath],
              multiSelect: false, // 是否多选
              parentSelect: false, // 父节点选择有效，默认无效
              onAfterSetValue: function (event, self, value) {
                $('#eventHash', $eventHanlderContainer).val(value);
                var valueNodes = self.options.valueNodes;
                if (valueNodes && valueNodes.length == 1) {
                  var parantNode = valueNodes[0].getParentNode();
                  if (parantNode) {
                    $(this).val('/' + parantNode.name + '/' + valueNodes[0].name);
                  } else {
                    $(this).val('/' + valueNodes[0].name);
                  }
                }
              }
            });
          } else if (eventHashType == '2') {
            $('.eventHashType' + eventHashType, $eventHanlderContainer).show();
          }
        }
      }
    });
    // 锚点设置单选框取消选中
    $("input[name='eventHashType']", $eventHanlderContainer).on('click', function () {
      var $radio = $(this);
      var checkFlag = $radio.data('checkFlag');
      if ($radio.attr('checked') == 'checked' && checkFlag == null) {
        $radio.data('checkFlag', '1');
      } else {
        if (checkFlag == '1') {
          $radio.removeAttr('checked');
          $radio.trigger('change');
        } else {
          $radio.data('checkFlag', '1');
        }
      }
      $("input[name='eventHashType']:not(:checked)", $eventHanlderContainer).data('checkFlag', '0');
    });
    // 锚点树
    var appPath = value.eventHandler ? value.eventHandler.path : '';
    var selectEventData = $('#eventHandlerName', $eventHanlderContainer).data('eventData');
    if (selectEventData) {
      appPath = selectEventData.path;
    }
    formBuilder.buildWCommonComboTree({
      container: $eventHanlderContainer,
      label: '',
      name: 'eventHashTree',
      value: value.eventHandler ? value.eventHandler.hash : '',
      inputClass: 'w-custom-collect',
      divClass: 'eventHashType1 eventHashTypeRow',
      labelColSpan: '3',
      controlColSpan: '9',
      wCommonComboTree: {
        service: 'pageDefinitionService.getAppHashTreeByAppPath',
        serviceParams: [appPath],
        width: '292px',
        multiSelect: false, // 是否多选
        parentSelect: true, // 父节点选择有效，默认无效
        onAfterSetValue: function (event, self, value) {
          $('#eventHash', $eventHanlderContainer).val(value);
          var valueNodes = self.options.valueNodes;
          if (valueNodes && valueNodes.length == 1) {
            var parantNode = valueNodes[0].getParentNode();
            if (parantNode) {
              $(this).val('/' + parantNode.name + '/' + valueNodes[0].name);
            } else {
              $(this).val('/' + valueNodes[0].name);
            }
          }
        }
      }
    });
    // 自定义锚点
    formBuilder.buildInput({
      container: $eventHanlderContainer,
      label:
        '<span class="wtooltip icon iconfont icon-ptkj-mianxingwenxintishi"><span class="wtooltiptext">锚点格式：/{组件ID}/{菜单、导航、页签等ID}</span></span>',
      name: 'eventHash',
      value: value.eventHandler ? value.eventHandler.hash : '',
      inputClass: 'w-custom-collect',
      divClass: 'eventHashType2 eventHashTypeRow',
      labelColSpan: '3',
      controlColSpan: '9'
    });
    $("input[name='eventHashType']", $eventHanlderContainer).trigger('change');
    if (DesignUtils.isSupportsAppHashByAppPath(appPath)) {
      $("input[name='eventHashType']", $eventHanlderContainer).removeAttr('disabled');
    } else {
      $("input[name='eventHashType']", $eventHanlderContainer).attr('disabled', 'disabled');
    }

    //事件参数
    var $eventParamDiv = $('<div>', {
      style: 'width: 450px;margin-left: 30px;'
    });
    $eventHanlderContainer.append($eventParamDiv);

    var eventParamsAppendParametersConstant = function ($element, value) {
      var parameters = [];
      if (value && value.parameters) {
        parameters = value.parameters;
      }
      formBuilder.bootstrapTable.build({
        container: $element,
        name: 'parameters',
        ediableNest: true,
        help:
          '1、事件处理中附加的参数对象，解析后作为事件处理参数options的params属性对象，通过参数名访问参数值；<br>' +
          '2、参数值支持访问变量，默认支持访问当前组件${ui}、应用上下文${appContext}；<br>' +
          '3、事件处理通过url响应的参数附加到url中，事件处理为组件渲染的传到入组件定义的params参数。',
        table: {
          data: parameters,
          striped: true,
          idField: 'uuid',
          width: 300,
          columns: [
            {
              field: 'checked',
              formatter: DesignUtils.checkedFormat,
              checkbox: true
            },
            {
              field: 'uuid',
              title: 'UUID',
              visible: false,
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'text',
              title: '参数名称',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'name',
              title: '参数名',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'value',
              title: '参数值',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            }
          ]
        }
      });
    };
    eventParamsAppendParametersConstant($eventParamDiv, value);

    //事件处理后
    formBuilder.buildPre({
      container: $eventHanlderContainer,
      label: '事件处理后',
      name: 'afterDealEventCodes',
      value: (value.eventHandler && value.eventHandler.afterEventHandlerCodes) || '',
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9'
    });
    var editor = ace.edit($input.find('#afterDealEventCodes')[0]);
    editor.setTheme('ace/theme/chrome');
    editor.session.setMode('ace/mode/javascript');
    //启用提示菜单
    ace.require('ace/ext/language_tools');
    if (window.appPageDesigner) {
      var _component = appPageDesigner.configureComponent;
      var id = _component.options.id;
      var type = _component.options.wtype;
    } else {
      var id = new UUID();
      var type = new UUID();
    }
    editor.setOptions({
      enableBasicAutocompletion: true,
      enableSnippets: true,
      enableLiveAutocompletion: true,
      showPrintMargin: false,
      enableVarSnippets: 'wDataManagementViewer.afterDealEventCode',
      enableCodeHis: {
        relaBusizUuid: id,
        codeType: type + '.afterDealEventCode',
        enable: true
      }
    });
    $input.find('#afterDealEventCodes').data('editor', editor);
    $eventHanlderContainer.data('editor', editor);
  };
  bootstrapTable.eventManager.input2value = function (value) {
    value = value || {};
    //事件管理参数
    value.eventHandler = {};
    if ($('#eventHandlerName').val()) {
      var selectEventData = $('#eventHandlerName', '#eventHanlderContainer').data('eventData');
      if (selectEventData) {
        value.eventHandler.id = selectEventData.id;
        value.eventHandler.path = selectEventData.path;
        value.eventHandler.type = selectEventData.data.type;
        value.eventHandler.name = selectEventData.name;
      } else {
        var initEventHandler = $('#eventHandlerName', '#eventHanlderContainer').data('initEventHandler');
        value.eventHandler = initEventHandler || {};
      }
    }
    // 事件锚点
    if (value.eventHandler) {
      value.eventHandler.hash = $('#eventHash', '#eventHanlderContainer').val();
      value.eventHandler.hashType = $("input[name='eventHashType']:checked", '#eventHanlderContainer').val();
    }
    try {
      //事件处理后的代码
      value.eventHandler.afterEventHandlerCodes = $('#eventHanlderContainer').data('editor').getValue();
    } catch (e) {
      console.log(e);
    }

    //事件参数
    value.parameters = this.$input.find('#table_parameters_info').bootstrapTable('getData');
    value.params = {};
    $.map(value.parameters, function (option) {
      value.params[option.name] = option.value;
    });

    //兼容旧参数格式
    value.eventParams = {
      params: value.params,
      parameters: value.parameters
    };

    //自定义事件
    value.defineEventJs = {};
    $('#defineEventJs')
      .find('pre')
      .each(function () {
        var jsCodes = $(this).data('editor').getValue();
        if ($.trim(jsCodes).length != 0) {
          value.defineEventJs[$(this).attr('id')] = jsCodes;
        }
      });

    return value;
  };
  bootstrapTable.eventManager.validate = function (value) {
    // 锚点设置验证
    var eventHandler = value && value.eventHandler ? value.eventHandler : {};
    if (StringUtils.isNotBlank(eventHandler.hashType) && StringUtils.isBlank(eventHandler.hash)) {
      appModal.error('锚点设置不能为空！');
      setTimeout(function () {
        $('.has-error', '.popover-content').removeClass('has-error');
      }, 50);
      return {
        newValue: value,
        msg: ''
      };
    }
  };
  bootstrapTable.eventManager.value2display = function (value) {
    var display = '';
    if (!$.isEmptyObject(value.defineEventJs)) {
      display += '自定义事件脚本;';
    }
    if (!$.isEmptyObject(value.eventHandler) && StringUtils.isNotBlank(value.eventHandler.name)) {
      display += value.eventHandler.name;
    }
    return display;
  };
  bootstrapTable.eventManager.value2html = function (value, element) {
    if (!$.isEmptyObject(value)) {
      var display = '';
      if (!$.isEmptyObject(value.defineEventJs)) {
        display += '自定义事件脚本;';
      }
      if (!$.isEmptyObject(value.eventHandler)) {
        display += value.eventHandler.name;
      }
      return $(element).html(display);
    }
  };

  //5. 导入配置按钮
  bootstrapTable.importConfig = {};
  bootstrapTable.importConfig.value2input = function (value) {
    var $input = this.$input;
    var _this = this;
    this.options.value = value;
    value = value || {};
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '500');
    $input.empty();
    //1.导入标题
    formBuilder.buildInput({
      container: $input,
      label: '导入弹窗标题',
      labelClass: 'required',
      name: 'title',
      value: value.title || '',
      inputClass: 'w-custom-collect',
      divClass: 'importType-1 importType-2',
      labelColSpan: '3',
      controlColSpan: '9'
    });

    formBuilder.buildRadio({
      container: $input,
      label: '导入方式',
      labelClass: 'required',
      name: 'importType',
      value: value.importType || '1',
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      divClass: 'importTypeSelectDiv',
      items: [
        {
          id: '1',
          text: '定义表单字段导入规则'
        },
        {
          id: '2',
          text: '自定义导入监听服务类 '
        }
      ],
      events: {
        change: function () {
          $input.find('.form-group').hide();
          $('.importTypeSelectDiv').show();
          if ($(this).val() == '2') {
            $input.find('.form-group').filter('.importType-2').show();
          } else {
            $input.find('.form-group').filter('.importType-1').show();
            $('#div_config_column_info').show();
          }
        }
      }
    });

    formBuilder.buildSelect2({
      container: $input,
      label: ' ',
      name: 'customImportListener',
      value: value.customImportListener || '',
      display: 'customImportListenerName',
      displayValue: value.customImportListenerName || '',
      inputClass: 'w-custom-collect',
      divClass: 'importType-2',
      labelColSpan: '3',
      controlColSpan: '9',
      select2: {
        serviceName: 'excelImportListenerHolder',
        queryMethod: 'getAllExcelImportListenerClass',
        params: {},
        labelField: 'customImportListenerName',
        valueField: 'customImportListener',
        remoteSearch: false,
        defaultBlank: true,
        container: $input
      }
    });
    formBuilder.buildLabel({
      container: $input,
      label: ' ',
      name: 'customImportListenerHelp',
      inputClass: 'help-block',
      divClass: 'importType-2',
      labelColSpan: '3',
      controlColSpan: '9',
      text: '导入监听服务类需要继承<code>AbstractEasyExcelImportListener</code>，且命名规范以<code>ExcelImportListener</code>结尾'
    });

    formBuilder.buildInput({
      container: $input,
      label: '文件格式限制',
      name: 'fileFormateLimit',
      value: value.fileFormateLimit || '',
      inputClass: 'w-custom-collect',
      divClass: 'importType-1 importType-2',
      labelColSpan: '3',
      controlColSpan: '9'
    });
    formBuilder.buildInput({
      container: $input,
      label: '文件格式不匹配提示',
      name: 'fileFormateLimitTip',
      value: value.fileFormateLimitTip || '',
      inputClass: 'w-custom-collect',
      divClass: 'importType-1 importType-2',
      labelColSpan: '3',
      controlColSpan: '9'
    });

    //6.字段配置
    var fieldConfigConstant = function ($input, value) {
      var fieldConfigs = [];
      if (value && value.fieldConfigs) {
        fieldConfigs = value.fieldConfigs;
      }
      value = value || {};
      formBuilder.bootstrapTable.build({
        container: $input,
        name: 'config_column',
        ediableNest: true,
        help: '1、初始化字段配置表格的时候，默认会带出所有表单的字段，第几列置空，校验规则置空。第几列不填的情况，表示字段没有与导入模块有对应的列。',
        table: {
          data: fieldConfigs,
          striped: true,
          idField: 'uuid',
          width: 300,
          columns: [
            {
              field: 'checked',
              formatter: DesignUtils.checkedFormat,
              checkbox: true
            },
            {
              field: 'uuid',
              title: 'UUID',
              visible: false,
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
                source: getImportTableColumnDatas()
              }
            },
            {
              field: 'cell',
              title: '第几列',
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'rule',
              title: '校验规则',
              editable: {
                onblur: 'ignore',
                type: 'wCustomForm',
                placement: 'bottom',
                savenochange: true,
                value2input: bootstrapTable.ruleOption.value2input,
                input2value: bootstrapTable.ruleOption.input2value,
                value2display: bootstrapTable.ruleOption.value2display
              }
            },
            {
              field: 'defaults',
              title: '字段设值',
              editable: {
                onblur: 'ignore',
                type: 'wCustomForm',
                placement: 'left',
                savenochange: true,
                value2input: bootstrapTable.defaultsOption.value2input,
                input2value: bootstrapTable.defaultsOption.input2value,
                value2display: bootstrapTable.defaultsOption.value2display
              }
            }
          ]
        }
      });
    };
    //2.导入类型
    formBuilder.buildSelect2({
      container: $input,
      label: '导入类型',
      labelClass: 'required',
      name: 'typeCode',
      value: value.typeCode || '',
      display: 'typeName',
      displayValue: value.typeName || '',
      divClass: 'importType-1',
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      select2: {
        data: (function () {
          var colDatas = [];
          colDatas.push({
            id: '',
            text: ''
          });
          colDatas.push({
            id: 'dynamic_form',
            text: '动态表单'
          });
          return colDatas;
        })(),
        remoteSearch: false,
        labelField: 'typeName',
        valueField: 'typeCode'
      },
      events: {
        change: function () {
          $('.div_impotrt_dynamic_name_div').empty();
          if ($(this).val() == 'dynamic_form') {
            //3.动态表单
            formBuilder.buildSelect2({
              container: $('.div_impotrt_dynamic_name_div'),
              label: '动态表单',
              labelClass: 'required',
              name: 'dynamicTable',
              value: value.dynamicTable || '',
              display: 'dynamicTableName',
              displayValue: value.dynamicTableName || '',
              divClass: 'importType-1',
              inputClass: 'w-custom-collect',
              labelColSpan: '3',
              controlColSpan: '9',
              select2: {
                serviceName: 'cdDataStoreDefinitionService',
                queryMethod: 'loadSelectDataByTable',
                params: {},
                labelField: 'dynamicTableName',
                valueField: 'dynamicTable',
                remoteSearch: false,
                defaultBlank: true
              },
              events: {
                change: function () {
                  $('.div_impotrt_filed_config_div').empty();
                  var newColumnNames = getImportTableColumnDatas();
                  var fieldConfigs = [];
                  var columns = $.map(newColumnNames, function (column) {
                    var bean = $.extend({}, columnConfigRowBean);
                    bean.uuid = commons.UUID.createUUID();
                    bean.name = column.id;
                    return bean;
                  });
                  value.fieldConfigs = columns;
                  var $columnInfoTable = $('#table_config_column_info', $input);
                  $columnInfoTable.empty();
                  fieldConfigConstant($input, value);
                }
              }
            });
          }
        }
      }
    });
    var $impotrtDynamicNameDiv = $('<div>', {
      class: 'div_impotrt_dynamic_name_div'
    });
    $input.append($impotrtDynamicNameDiv);
    if (!$.isEmptyObject(value)) {
      $('#typeCode', $input).trigger('change');
      //            $("#dynamicTable", $(".div_impotrt_dynamic_name_div")).trigger('change');
    }
    //4.导入模板
    formBuilder.buildFileUpload({
      container: $input,
      label: '导入模板',
      labelClass: 'required',
      name: 'importTemplate',
      value: value.importTemplate,
      divClass: 'importType-1 importType-2',
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      singleFile: true
    });
    //5.导入起始行
    formBuilder.buildInput({
      container: $input,
      label: '导入起始行',
      labelClass: 'required',
      name: 'beginRow',
      value: value.beginRow,
      divClass: 'importType-1',
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9'
    });
    //7.唯一性字段校验
    formBuilder.buildSelect2({
      container: $input,
      label: '唯一性字段校验',
      labelClass: 'text-info',
      name: 'uniquenessField',
      value: value.uniquenessField || '',
      display: 'uniquenessFieldText',
      displayValue: value.uniquenessFieldText || '',
      divClass: 'importType-1',
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      select2: {
        data: getImportTableColumnDatas(),
        multiple: true
      },
      events: {}
    });
    //8.处理策略
    formBuilder.buildSelect2({
      container: $input,
      label: '处理策略',
      labelClass: 'text-info',
      name: 'strategy',
      value: value.strategy || '',
      display: 'strategyText',
      displayValue: value.strategyText || '',
      divClass: 'importType-1',
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      select2: {
        data: [
          {
            id: '',
            text: '请选择!'
          },
          {
            id: 'overlap',
            text: '覆盖操作'
          },
          {
            id: 'error',
            text: '报错操作'
          }
        ]
      },
      events: {}
    });
    fieldConfigConstant($input, value);

    $("input[name='importType']:checked", $input).trigger('change');
  };
  bootstrapTable.importConfig.value2display = function (value) {
    var displayText = '';
    if ($.isEmptyObject(value)) {
      return '';
    }

    if (value.importType == '1' || value.importType == undefined) {
      displayText += '定义表单字段导入规则：' + (value.dynamicTable || '[未指定动态表单]');
    } else {
      displayText += '自定义导入监听服务类：' + (value.customImportListenerName || '[未指定服务类]');
    }
    return displayText;
  };

  bootstrapTable.importConfig.input2value = function (value) {
    value = value || {};
    var $input = this.$input;
    var title = $('#title', $input).val();
    if (title) {
      value.title = title;
    }
    var fileFormateLimit = $('#fileFormateLimit', $input).val();
    if (fileFormateLimit) {
      value.fileFormateLimit = fileFormateLimit;
    }
    var fileFormateLimitTip = $('#fileFormateLimitTip', $input).val();
    if (fileFormateLimitTip) {
      value.fileFormateLimitTip = fileFormateLimitTip;
    }
    var typeCode = $('#typeCode', $input).val();
    if (typeCode) {
      value.typeCode = typeCode;
    }
    var typeName = $('#typeName', $input).val();
    if (typeName) {
      value.typeName = typeName;
    }
    var dynamicTable = $('#dynamicTable', $input).val();
    if (dynamicTable) {
      value.dynamicTable = dynamicTable;
    }
    var dynamicTableName = $('#dynamicTableName', $input).val();
    if (dynamicTableName) {
      value.dynamicTableName = dynamicTableName;
    }
    var importTemplate = $('#importTemplate', $input).val();
    if (importTemplate) {
      value.importTemplate = importTemplate;
    }
    var beginRow = $('#beginRow', $input).val();
    if (beginRow) {
      value.beginRow = beginRow;
    }
    var uniquenessField = $('#uniquenessField', $input).val();
    if (uniquenessField) {
      value.uniquenessField = uniquenessField;
    }
    var uniquenessFieldText = $('#uniquenessFieldText', $input).val();
    if (uniquenessFieldText) {
      value.uniquenessFieldText = uniquenessFieldText;
    }
    var strategy = $('#strategy', $input).val();
    if (strategy) {
      value.strategy = strategy;
    }
    var strategyText = $('#strategyText', $input).val();
    if (strategyText) {
      value.strategyText = strategyText;
    }
    var fieldConfigs = $input.find('#table_config_column_info').bootstrapTable('getData');
    if (fieldConfigs) {
      for (var i = 0; i < fieldConfigs.length; i++) {
        fieldConfigs[i].rule = fieldConfigs[i].rule || {};
        fieldConfigs[i].defaults = fieldConfigs[i].defaults || {};
      }
      value.fieldConfigs = fieldConfigs;
    }

    return value;
  };

  //5.1. 导入校验规则按钮
  bootstrapTable.ruleOption = {};
  bootstrapTable.ruleOption.value2input = function (value) {
    var $input = this.$input;
    var _this = this;
    this.options.value = value;
    value = value || {};
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '400');
    $input.empty();
    $regularOptionDiv = $("<div class='div_regular_option'></div>");
    $groovyOptionDiv = $("<div class='div_groovy_option'></div>");

    //1.必填
    formBuilder.buildCheckbox({
      container: $input,
      label: '必填',
      name: 'requireds',
      value: value.requireds || '',
      labelClass: 'text-info',
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      items: [
        {
          id: '1',
          text: '必填'
        }
      ]
    });
    formBuilder.buildSelect2({
      container: $input,
      label: '校验方式',
      name: 'verification',
      value: value.verification || '',
      display: 'verificationText',
      displayValue: value.verificationText || '',
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      remoteSearch: false,
      select2: {
        data: [
          {
            id: 'other',
            text: ''
          },
          {
            id: 'regular',
            text: '自定义正则表达式'
          },
          {
            id: 'groovy',
            text: '自定义groovy脚本'
          }
        ]
      },
      events: {
        change: function () {
          var verificationId = $(this).select2('val');
          if ('regular' == verificationId) {
            $groovyOptionDiv.empty();
            formBuilder.buildSelect2({
              container: $regularOptionDiv,
              label: '',
              labelClass: 'text-info',
              name: 'regularType',
              value: value.regularType || '',
              display: 'regularTypeText',
              displayValue: value.regularTypeText || '',
              inputClass: 'w-custom-collect',
              divClass: 'tableColumnSelectDiv',
              labelColSpan: '3',
              controlColSpan: '9',
              select2: {
                data: [
                  {
                    id: 'other',
                    text: '其他自定义'
                  },
                  {
                    id: 'email',
                    text: '邮箱地址校验'
                  },
                  {
                    id: 'phone',
                    text: '电话号码校验'
                  },
                  {
                    id: 'identity',
                    text: '身份证号校验'
                  },
                  {
                    id: 'int',
                    text: '正整数校验'
                  },
                  {
                    id: 'china',
                    text: '中文校验'
                  }
                ]
              },
              events: {
                change: function () {
                  var regularType = $(this).select2('val');
                  var regularValue = regularValue || '';
                  if (!$.isEmptyObject(regularType)) {
                    if ('email' == regularType) {
                      regularValue = '\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?';
                    } else if ('phone' == regularType) {
                      regularValue = '(\\+\\d+)?1[3458]\\d{9}$';
                    } else if ('identity' == regularType) {
                      regularValue = '[1-9]\\d{13,16}[a-zA-Z0-9]{1}';
                    } else if ('int' == regularType) {
                      regularValue = '^[1-9]d*$';
                    } else if ('china' == regularType) {
                      regularValue = '^[\u4E00-\u9FA5]+$';
                    }
                  }
                  $('#regularValue').val(regularValue);
                }
              }
            });
            // 自定义正则表达式
            formBuilder.buildInput({
              container: $regularOptionDiv,
              label: '',
              labelClass: 'text-info',
              name: 'regularValue',
              value: value.regularValue || '',
              inputClass: 'w-custom-collect',
              labelColSpan: '3',
              controlColSpan: '9'
            });
          } else if (verificationId == 'groovy') {
            $regularOptionDiv.empty();
            //自定义groovy脚本
            formBuilder.buildPre({
              container: $groovyOptionDiv,
              label: '',
              name: 'groovyValue',
              value: value.groovyValue || '',
              inputClass: 'w-custom-collect',
              labelColSpan: '0',
              controlColSpan: '12'
            });
            var editor = ace.edit('groovyValue');
            editor.setTheme('ace/theme/chrome');
            editor.session.setMode('ace/mode/groovy');
            //启用提示菜单
            ace.require('ace/ext/language_tools');
            var _component = appPageDesigner.configureComponent;
            editor.setOptions({
              enableBasicAutocompletion: true,
              enableSnippets: true,
              showPrintMargin: false,
              enableVarSnippets: 'wBootstrapTable.importColGroovyUse',
              enableLiveAutocompletion: true,
              enableCodeHis: {
                relaBusizUuid: _component.options.id,
                codeType: _component.options.wtype + '.importGroovyValidateCode',
                enable: true
              }
            });
            $groovyOptionDiv.data('editor', editor);
          }
        }
      }
    });
    $input.append($groovyOptionDiv);
    $input.append($regularOptionDiv);
    if (!$.isEmptyObject(value)) {
      $('#verification', $input).trigger('change');
      //           $("#regularValue", $input).trigger('change');
    }
  };
  bootstrapTable.ruleOption.input2value = function (value) {
    value = value || {};
    var $input = this.$input;
    var requireds = $(':checkbox[name=requireds]:checked', $input).val();
    if (requireds) {
      value.requireds = requireds;
    }
    var verification = $('#verification', $input).val();
    if (verification) {
      value.verification = verification;
    }
    var verificationText = $('#verificationText', $input).val();
    if (verificationText) {
      value.verificationText = verificationText;
    }
    var regularType = $('#regularType', $input).val();
    if (regularType) {
      value.regularType = regularType;
    }
    var regularTypeText = $('#regularTypeText', $input).val();
    if (regularTypeText) {
      value.regularTypeText = regularTypeText;
    }

    var regularValue = $('#regularValue', $('.div_regular_option')).val();
    if (regularValue) {
      value.regularValue = regularValue;
    }
    if (!$.isEmptyObject($('.div_groovy_option').data('editor'))) {
      var groovyValue = $('.div_groovy_option').data('editor').getValue();
      if (groovyValue) {
        value.groovyValue = groovyValue;
      }
    }
    return value;
  };
  bootstrapTable.ruleOption.value2display = function (value) {
    var text = '';
    if ('自定义正则表达式' == value.verificationText) {
      text = value.regularTypeText;
    } else {
      text = value.verificationText || '';
    }
    return text;
  };
  //5.1. 导入默认值按钮
  bootstrapTable.defaultsOption = {};
  bootstrapTable.defaultsOption.value2input = function (value) {
    var $input = this.$input;
    value = value || {};
    this.options.value = value;
    $input.closest('form').removeClass('form-inline');
    $input.css('width', '400');
    $input.empty();
    $groovyValueOptionDiv = $("<div class='div_groovy_value_option'></div>");

    // 默认值类型
    formBuilder.buildSelect2({
      container: $input,
      label: '类型',
      name: 'defaultTypeId',
      value: value.defaultTypeId || '',
      display: 'defaultTypeText',
      displayValue: value.defaultTypeText || '',
      inputClass: 'w-custom-collect',
      labelColSpan: '3',
      controlColSpan: '9',
      select2: {
        data: [
          {
            id: 'other',
            text: '自定义文本'
          },
          {
            id: 'freemark',
            text: 'freemark渲染值'
          },
          {
            id: 'groovy',
            text: 'groovy脚本渲染值'
          }
        ]
      },
      events: {
        change: function () {
          var regularType = $(this).select2('val');
          if (!$.isEmptyObject(regularType)) {
            $groovyValueOptionDiv.empty();
            if ('groovy' == regularType) {
              //自定义groovy脚本
              formBuilder.buildPre({
                container: $groovyValueOptionDiv,
                label: '',
                name: 'defaultValue',
                value: value.defaultValue || '',
                inputClass: 'w-custom-collect',
                labelColSpan: '0',
                controlColSpan: '12'
              });
              var editor = ace.edit('defaultValue');
              editor.setTheme('ace/theme/chrome');
              editor.session.setMode('ace/mode/groovy');
              //启用提示菜单
              ace.require('ace/ext/language_tools');
              var _component = appPageDesigner.configureComponent;
              editor.setOptions({
                enableBasicAutocompletion: true,
                enableSnippets: true,
                showPrintMargin: false,
                enableLiveAutocompletion: true,
                enableVarSnippets: 'wBootstrapTable.importColGroovyUse',
                enableCodeHis: {
                  relaBusizUuid: _component.options.id,
                  codeType: _component.options.wtype + '.importGroovyValueCode',
                  enable: true
                }
              });

              $groovyValueOptionDiv.data('editor', editor);
            } else {
              // 文本默认值
              formBuilder.buildTextarea({
                container: $groovyValueOptionDiv,
                label: '',
                name: 'defaultValue',
                value: value.defaultValue || '',
                inputClass: 'w-custom-collect',
                rows: '6',
                labelColSpan: '3',
                controlColSpan: '9'
              });
            }
          }
        }
      }
    });
    $input.append($groovyValueOptionDiv);
    if (!$.isEmptyObject(value)) {
      $('#defaultTypeId', $input).trigger('change');
    }
  };
  bootstrapTable.defaultsOption.input2value = function (value) {
    value = value || {};
    var $input = this.$input;
    var defaultTypeId = $('#defaultTypeId', $input).val();
    if (defaultTypeId) {
      value.defaultTypeId = defaultTypeId;
    }
    var defaultTypeText = $('#defaultTypeText', $input).val();
    if (defaultTypeText) {
      value.defaultTypeText = defaultTypeText;
    }
    var defaultValue = $('#defaultValue', $input).val() || $('.div_groovy_value_option').data('editor').getValue();
    if (defaultValue) {
      value.defaultValue = defaultValue;
    }
    return value;
  };
  bootstrapTable.defaultsOption.value2display = function (value) {
    return value.defaultTypeText || '';
  };

  bootstrapTable.checkbox = {
    formatter: function (value, prop) {
      var uuid = uuidObject.createUUID();
      var html =
        '<label>' +
        '<input type="checkbox" class="w-checkbox" prop="' +
        prop +
        '"' +
        ' id="' +
        uuid +
        '" ' +
        (value == 1 ? 'checked' : '') +
        '  />' +
        '<label for="' +
        uuid +
        '">' +
        '</label></label>';
      return html;
    },
    events: {
      'change :checkbox': function (event, value, row, index) {
        var prop = $(this).attr('prop');
        if (!prop) {
          console.error('请设置checkbox对应的prop信息');
        }
        row[prop] = this.checked ? '1' : '0';
      }
    }
  };

  DesignUtils.bootstrapTable = bootstrapTable;
  DesignUtils.dateFormat = dateFormat;

  return DesignUtils;
});
