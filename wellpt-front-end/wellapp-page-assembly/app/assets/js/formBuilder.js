(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'constant', 'commons', 'moment', 'server', 'lodash', 'layDate'], factory);
  } else {
    // Browser globals
    factory(window.jQuery, window.Constant, window.commons, window.moment, window.server, window._, window.laydate, true);
  }
})(function ($, constant, commons, moment, server, _, laydate, isGlobal) {
  var UUID = commons.UUID;
  var ArrayUtils = commons.ArrayUtils;
  var StringUtils = commons.StringUtils;
  var DateUtils = commons.DateUtils;
  var JDS = {};
  JDS.call = function (options) {
    options.version = '';
    return server.JDS.call(options);
  };
  var colPrefix = 'col-xs-';
  var defaultOptions = {
    container: '', // 容器
    labelColSpan: '2', // label占比(总12份)
    controlColSpan: '10', // 控件占比(总12份)
    labelClass: '', // label要添加的class
    controlClass: '', // 控件要添加的class
    divClass: 'form-group', // 包含块DIV的样式
    label: '', // label
    name: '', // name
    value: '', // value
    inputClass: '', // 输入控件的class
    events: null,
    help: '', //帮助文本
    controlOption: {} //组件的定义参数
    // 事件Map
  };
  var defaultDatetimepickerOptions = {
    showClose: true,
    showClear: true,
    showTodayButton: true,
    format: 'YYYY-MM-DD HH:mm:ss', // 日期格式
    locale: 'zh-cn' // 本地化
  };
  var initOptions = function (specialDefault, options) {
    var specialDefault = $.extend(specialDefault, defaultOptions);
    specialDefault = $.extend(specialDefault, options);
    return specialDefault;
  };
  var formBuilder = {};
  formBuilder.bootstrapTable = {};
  var defaultBootstrapTableOptions = {
    name: '', //表格ID
    container: '', //容器
    toolbar: true, //是否创建新增、删除、上移、下移按钮
    addDefaultBean: {}, //新增时默认的Row对象
    ediableNest: false, //该表格如果有Ediable，且嵌套在另一个表格的行编辑内，设置为True
    copyData: true //表格的初始数据，是否拷贝一副副本再使用。
  };
  /**
   * 创建一个bootstrapTable
   */
  formBuilder.bootstrapTable.build = function (options) {
    var options = $.extend({}, defaultBootstrapTableOptions, options);
    var fieldHtml = new commons.StringBuilder();
    $('#div_' + options.name + '_info').remove();
    fieldHtml.appendFormat("<div id='div_{0}_info' class='form-group div-bootstraptable-formbuilder' >", options.name);
    if (options.toolbar) {
      fieldHtml.appendFormat("	<div id='div_{0}_toolbar' class='btn-group btn_field_search_info'>", options.name);
      fieldHtml.appendFormat("		<button id='btn_add_{0}' type='button' class='btn btn-default'>", options.name);
      fieldHtml.appendFormat("			<span class='glyphicon glyphicon-plus' aria-hidden='true'></span>");
      fieldHtml.appendFormat('		</button>');
      fieldHtml.appendFormat("		<button id='btn_delete_{0}' type='button' class='btn btn-default'>", options.name);
      fieldHtml.appendFormat("			<span class='glyphicon glyphicon-remove' aria-hidden='true'></span>");
      fieldHtml.appendFormat('		</button>');
      fieldHtml.appendFormat("		<button id='btn_row_up_{0}' type='button' class='btn btn-default'>", options.name);
      fieldHtml.appendFormat("			<span class='glyphicon glyphicon-arrow-up' aria-hidden='true'></span>");
      fieldHtml.appendFormat('		</button>');
      fieldHtml.appendFormat("		<button id='btn_row_down_{0}' type='button' class='btn btn-default'>", options.name);
      fieldHtml.appendFormat("			<span class='glyphicon glyphicon-arrow-down' aria-hidden='true'></span>");
      fieldHtml.appendFormat('		</button>');
      if (options.help) {
        fieldHtml.appendFormat("		<button id='btn_table_help_{0}' type='button' class='btn btn-default' title='使用说明'>", options.name);
        fieldHtml.appendFormat("			<span class='glyphicon glyphicon-question-sign' aria-hidden='true' title='使用说明'></span>");
        fieldHtml.appendFormat('		</button>');
      }
      fieldHtml.appendFormat('	</div>');
    }
    fieldHtml.appendFormat("	<table id='table_{0}_info' data-height='{1}'></table>", options.name, options.height);
    fieldHtml.appendFormat('</div>');
    var $container = $(options.container);
    $container.append(fieldHtml.toString());
    var $tableElement = $container.find('#table_' + options.name + '_info');
    if (options.toolbar) {
      if ($.isEmptyObject(options.addDefaultBean)) {
        options.addDefaultBean = {};
        $.each(options.table.columns, function (index, column) {
          options.addDefaultBean[column.field] = '';
        });
      }
      this.addAddRowButtonClickEvent({
        button: '#btn_add_' + options.name,
        tableElement: $tableElement,
        bean: options.addDefaultBean,
        idField: options.table.idField || 'uuid',
        generateKey: options.generateKey === false ? false : true
      });
      this.addDeleteRowButtonClickEvent({
        button: '#btn_delete_' + options.name,
        tableElement: $tableElement,
        idField: options.table.idField || 'uuid'
      });
      this.addRowUpButtonClickEvent({
        button: '#btn_row_up_' + options.name,
        tableElement: $tableElement,
        idField: options.table.idField || 'uuid'
      });
      this.addRowDownButtonClickEvent({
        button: '#btn_row_down_' + options.name,
        tableElement: $tableElement,
        idField: options.table.idField || 'uuid'
      });
      this.addHelpButtonClickEvent({
        button: '#btn_table_help_' + options.name,
        help: options.help
      });

      options.table.toolbar = '#div_' + options.name + '_toolbar';
    }
    if (options.table.data) {
      options.table.data = $.extend(true, [], options.table.data);
    }
    $tableElement.bootstrapTable('destroy').bootstrapTable(options.table);
    if (options.ediableNest) {
      this.fixEdiableNestEx({
        container: $container,
        tableElement: $tableElement
      });
    }
  };
  /**
   * 修复Ediable 嵌套 Ediable失去焦点时间判断错误
   */
  formBuilder.bootstrapTable.fixEdiableNestEx = function (options) {
    $(options.container)
      .closest('form')
      .on('click.editable', options, function (e) {
        var options = e.data;
        var $target = $(e.target),
          i,
          exclude_classes = [
            '.editable-container',
            '.ui-datepicker-header',
            '.datepicker',
            '.modal-backdrop',
            '.bootstrap-wysihtml5-insert-image-modal',
            '.bootstrap-wysihtml5-insert-link-modal'
          ];
        var $openA = $(options.tableElement).find('a.editable.editable-open');
        if (!$openA.length) {
          return;
        }
        if ($target.is($openA)) {
          $openA.each(function () {
            if (this != $target[0]) {
              $(this).next('.editable-container').find('form').submit();
            }
          });
          return false;
        }
        if (!$.contains(this, e.target)) {
          return;
        }
        // if click inside one of exclude classes --> no nothing
        for (i = 0; i < exclude_classes.length; i++) {
          if ($target.is(exclude_classes[i])) {
            return;
          }
          var tag = false;
          $target.parents(exclude_classes[i]).each(function () {
            if ($.contains($(options.container)[0], this)) {
              tag = true;
            }
          });
          if (tag) {
            return;
          }
        }
        $openA.next('.editable-container').find('form').submit();
      });
  };
  /**
   * 给一个按钮添加事件，逻辑为往bootstrapTable添加一行数据
   */
  formBuilder.bootstrapTable.addAddRowButtonClickEvent = function (options) {
    var options = $.extend(
      {
        tableElement: null,
        button: null,
        generateKey: true,
        bean: {},
        idField: 'uuid'
      },
      options
    );
    var $button = top.$(document).find(options.button).length > 0 ? top.$(document).find(options.button) : top.$(options.button);
    $button.off('click').on('click', options, function (event) {
      var options = event.data;
      var $targetTable = $(options.tableElement);
      // 添加行前先保存修改的内容 [#40997](http://zen.well-soft.com:81/zentao/bug-view-40997.html)
      $targetTable.find('a.editable.editable-open').next('.editable-container').find('form').submit();
      if (options.generateKey) {
        options.bean[options.idField] = commons.UUID.createUUID();
      }
      var allData = $targetTable.bootstrapTable('getData');
      $targetTable.bootstrapTable('insertRow', {
        index: allData.length + 1,
        row: $.extend({}, options.bean),
        field: options.idField
      });
      $targetTable.bootstrapTable('resetView');
    });
  };
  /**
   * 给一个按钮添加事件，逻辑为往bootstrapTable删除一行数据
   */
  formBuilder.bootstrapTable.addDeleteRowButtonClickEvent = function (options) {
    var options = $.extend(
      {
        tableElement: null,
        button: null,
        idField: 'uuid'
      },
      options
    );
    var $button = top.$(document).find(options.button).length > 0 ? top.$(document).find(options.button) : top.$(options.button);
    $button.off('click').on('click', options, function (event) {
      var options = event.data;
      var $targetTable = $(options.tableElement);

      var shouldAlwaysRetainFieldsCount = 0;
      var uuids = $.map($targetTable.bootstrapTable('getSelections'), function (row) {
        if (row.shouldAlwaysRetain) shouldAlwaysRetainFieldsCount++;
        return row[options.idField];
      });
      var _info = window.top.appModal ? window.top.appModal.error : appModal ? appModal.error : null;
      if (_info == null) {
        _info = window.top.$ && window.top.$.WCommonAlert ? window.top.$.WCommonAlert : $.WCommonAlert ? $.WCommonAlert : alert;
      }
      if (uuids.length == 0) {
        _info('请选择删除数据!');
        return;
      }

      // 不允许删除默认项
      if (shouldAlwaysRetainFieldsCount > 0) {
        _info('您选择了不能删除的默认项，请重新选择数据!');
        return;
      }

      var _confirm = window.top.appModal ? window.top.appModal.confirm : appModal ? appModal.confirm : null;
      if (_confirm == null) {
        _confirm =
          window.top.$ && window.top.$.WCommonConfirm ? window.top.$.WCommonConfirm : $.WCommonConfirm ? $.WCommonConfirm : confirm;
      }
      var deleRows = function ($table, field, _uuids) {
        $table.bootstrapTable('remove', {
          field: field,
          values: _uuids
        });
        window.top.appModal ? window.top.appModal.success('删除成功！') : appModal.success('删除成功！');
      };
      if (_confirm == confirm && _confirm('确认删除选择的数据?')) {
        var rows = $targetTable.bootstrapTable('getSelections');
        deleRows($targetTable, options.idField, uuids);
        if ($.isFunction(options.callback)) {
          options.callback('delete', rows);
        }
      } else {
        _confirm('确认删除选择的数据?', function (result) {
          if (!result) return true; // 返回 `true` 表示关闭模态框

          var rows = $targetTable.bootstrapTable('getSelections');
          deleRows($targetTable, options.idField, uuids);
          if ($.isFunction(options.callback)) {
            options.callback('delete', rows);
          }
        });
      }
    });
  };
  /**
   * 给一个按钮添加事件，逻辑为bootstrapTable的行下移一格
   */
  formBuilder.bootstrapTable.addRowDownButtonClickEvent = function (options) {
    var options = $.extend(
      {
        tableElement: null,
        button: null,
        idField: 'uuid'
      },
      options
    );
    var $button = top.$(document).find(options.button).length > 0 ? top.$(document).find(options.button) : top.$(options.button);
    $button.off('click').on('click', options, function (event) {
      var options = event.data;
      var $targetTable = $(options.tableElement);
      var allData = $targetTable.bootstrapTable('getData');
      var size = allData.length;
      var selections = $targetTable.bootstrapTable('getSelections');

      for (var i = selections.length - 1; i >= 0; i--) {
        var row = selections[i];
        for (var j = 0; j < allData.length; j++) {
          var row2 = allData[j];
          if (row[options.idField] === row2[options.idField] && j < size - 1) {
            allData[j] = allData[j + 1];
            allData[j + 1] = row;
            return $targetTable.bootstrapTable('load', allData);
          }
        }
      }
    });
  };
  /**
   * 给一个按钮添加事件，逻辑为bootstrapTable的行上移一格
   */
  formBuilder.bootstrapTable.addRowUpButtonClickEvent = function (options) {
    var options = $.extend(
      {
        tableElement: null,
        button: null,
        idField: 'uuid'
      },
      options
    );
    var $button = top.$(document).find(options.button).length > 0 ? top.$(document).find(options.button) : top.$(options.button);
    $button.off('click').on('click', options, function (event) {
      var options = event.data;
      var $targetTable = $(options.tableElement);
      var allData = $targetTable.bootstrapTable('getData');
      var selections = $targetTable.bootstrapTable('getSelections');

      for (var i = 0; i < selections.length; i++) {
        var row = selections[i];
        $.each(allData, function (j, row2) {
          if (row[options.idField] === row2[options.idField] && j > 0) {
            allData[j] = allData[j - 1];
            allData[j - 1] = row;
            return $targetTable.bootstrapTable('load', allData);
          }
        });
      }
    });
  };

  formBuilder.bootstrapTable.addHelpButtonClickEvent = function (options) {
    $(options.button).popover({
      content: '<div style="width: 256px;">' + options.help + '</div>',
      placement: 'bottom',
      html: true
    });
    // $(options.button).off("click").on('click', options, function (event) {
    //     appModal.alert(options.help);
    // });
  };

  //定义表格头部的添加，删除，上移，下移4个按钮事件
  formBuilder.bootstrapTable.initTableTopButtonToolbar = function (
    tableId,
    btnSuffix,
    $container,
    columnRowBean,
    idField,
    callback,
    generateKey
  ) {
    // 列定义
    var $columnInfoTable = $('#' + tableId, $container);
    // 列定义上移事件
    formBuilder.bootstrapTable.addRowUpButtonClickEvent({
      tableElement: $columnInfoTable,
      button: $('#btn_row_up_' + btnSuffix, $container),
      idField: idField
    });
    // 列定义下移事件
    formBuilder.bootstrapTable.addRowDownButtonClickEvent({
      tableElement: $columnInfoTable,
      button: $('#btn_row_down_' + btnSuffix, $container),
      idField: idField
    });
    // 列定义添加一行事件
    formBuilder.bootstrapTable.addAddRowButtonClickEvent({
      tableElement: $columnInfoTable,
      button: $('#btn_add_' + btnSuffix, $container),
      bean: columnRowBean,
      idField: idField,
      generateKey: generateKey === false ? false : true
    });
    // 列定义删除一行事件
    formBuilder.bootstrapTable.addDeleteRowButtonClickEvent({
      tableElement: $columnInfoTable,
      button: $('#btn_delete_' + btnSuffix, $container),
      idField: idField,
      callback: callback
    });
  };

  /**
   * 创建一个包含lable的input
   */
  formBuilder.buildInput = function (options) {
    var options = initOptions({}, options);
    var fieldHtml = new commons.StringBuilder();
    var readOnly = options.readOnly ? " readonly='readonly' " : '';
    fieldHtml.appendFormat("<div class='form-group formbuilder clear {0}'>", options.divClass);
    fieldHtml.appendFormat(
      "<label class='{0}{1} control-label control-label-{3} {2} label-formbuilder' for='{3}' >{4}{5}</label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.name,
      options.label,
      options.isRequired ? '<font color="red" size="2">*</font>' : ''
    );
    fieldHtml.appendFormat("<div class='{0}{1} controls'>", colPrefix, options.controlColSpan);

    if (options.type !== 'numberInterval') {
      fieldHtml.appendFormat(
        "<input type='{5}' style='width:100%' class='form-control form-control-{1} {0}' name='{1}' id='{1}' value='{2}' placeholder='{3}' {4}>",
        options.inputClass,
        options.name,
        options.value,
        options.placeholder != undefined ? options.placeholder : '',
        readOnly,
        options.type == 'password' ? 'password' : 'text'
      );
    } else {
      //文本区间(限制type为number)
      fieldHtml.appendFormat(
        "<input type='number' style='float: left;width: calc(50% - 20px)' class='form-control form-control-{1} {0}' name='{1}' id='{1}' value='{2}' placeholder='{3}' >",
        options.inputClass,
        options.beginName,
        '',
        options.placeholder != undefined ? options.placeholder : ''
      );
      fieldHtml.appendFormat('<div class="numberInterval">-</div>');
      fieldHtml.appendFormat(
        "<input type='number' style='float: left;width: calc(50% - 20px)' class='form-control form-control-{1} {0}' name='{1}' id='{1}' value='{2}' placeholder='{3}' >",
        options.inputClass,
        options.endName,
        '',
        options.placeholder != undefined ? options.placeholder : ''
      );
    }

    fieldHtml.appendFormat('</div></div>');
    $(options.container).append(fieldHtml.toString());

    if (options.type === 'numberInterval') {
      var $beginName = $('#' + options.beginName, options.container);
      var $endName = $('#' + options.endName, options.container);
      $beginName.on('change', function () {
        var $beginNameV = parseFloat($beginName.val());
        var $endNameV = parseFloat($endName.val());
        if (!!$beginNameV && !!$endNameV && $beginNameV > $endNameV) {
          appModal.error(options.label + '区间开始值不能大于区间结束值！');
          $beginName.val('');
        }
      });
      $endName.on('change', function () {
        var $beginNameV = parseFloat($beginName.val());
        var $endNameV = parseFloat($endName.val());
        if (!!$beginNameV && !!$endNameV && $beginNameV > $endNameV) {
          appModal.error(options.label + '区间结束值不能小于区间开始值！');
          $endName.val('');
        }
      });
    }

    if ($.isPlainObject(options.events)) {
      $('#' + options.name, options.container).on(options.events);
    }
  };

  /**
   * 创建一个lable,用来显示查看状态的数据
   */
  formBuilder.buildLabel = function (options) {
    var options = initOptions({}, options);
    var fieldHtml = new commons.StringBuilder();
    var _text = options.text ? options.text.replace(/\r\n/g, '<br/>').replace(/\n/g, '<br/>').replace(/\s/g, ' ') : '';
    var $fieldHtml = $('<div class="form-group formbuilder clear ' + options.divClass + '"></div>');
    fieldHtml.appendFormat(
      "	<label class='{0}{1} control-label control-label-{3} {2} label-formbuilder' for='{3}' ><span class='label-span'>{4}</span></label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.name,
      options.label,
      options.isRequired ? '<font color="red" size="2">*</font>' : ''
    );
    fieldHtml.appendFormat("	<div class='{0}{1} controls'>", colPrefix, options.controlColSpan);
    fieldHtml.appendFormat(
      "    	<span class='form-control-{1} {0}'  name='{1}' id='{1}' >{2}</span>",
      options.inputClass,
      options.name,
      _text
    );
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat('</div>');
    $fieldHtml.html(fieldHtml.toString());
    $(options.container).append($fieldHtml);
    if ($.isPlainObject(options.events)) {
      $('#' + options.name, options.container).on(options.events);
    }
  };

  /**
   * 创建一个包含lable的带颜色选择器控件
   */
  formBuilder.buildColorsInput = function (options) {
    formBuilder.buildInput(options);
    $('#' + options.name, options.container).minicolors('create', options.minicolors || {});
  };
  /**
   * 创建一个包含lable的WCommonComboTree
   */
  formBuilder.buildWCommonComboTree = function (options) {
    var options = initOptions({}, options);
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat("<div class='form-group formbuilder clear {0}'>", options.divClass);
    fieldHtml.appendFormat(
      "	<label class='{0}{1} control-label {2} label-formbuilder' for='{3}' >{4}{5}</label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.name,
      options.label,
      options.isRequired ? '<font color="red" size="2">*</font>' : ''
    );
    fieldHtml.appendFormat("	<div class='{0}{1} controls'>", colPrefix, options.controlColSpan);
    fieldHtml.appendFormat(
      "    	<input type='text' class='form-control {0}'  name='{1}' id='{1}' value='{2}' autocomplete='{3}'>",
      options.inputClass,
      options.name,
      options.value,
      options.autocomplete || 'off'
    );
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat('</div>');
    $(options.container).append(fieldHtml.toString());
    $('#' + options.name).wCommonComboTree(options.wCommonComboTree);
    if ($.isPlainObject(options.events)) {
      $('#' + options.name, options.container).on(options.events);
    }
  };
  /**
   * 创建一个包含lable的Textarea
   */
  formBuilder.buildTextarea = function (options) {
    var options = initOptions(
      {
        rows: 3
      },
      options
    );
    var fieldHtml = new commons.StringBuilder();
    var readOnly = options.readOnly ? " readonly='readonly' " : '';
    fieldHtml.appendFormat("<div class='form-group formbuilder clear {0}'>", options.divClass);
    fieldHtml.appendFormat(
      "	<label class='{0}{1} control-label {2} label-formbuilder' for='{3}' >{4}{5}</label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.name,
      options.label,
      options.isRequired ? '<font color="red" size="2">*</font>' : ''
    );
    fieldHtml.appendFormat("	<div class='{0}{1} controls'>", colPrefix, options.controlColSpan);
    fieldHtml.appendFormat(
      "		<textarea class='form-control {0}' style='width:100%'  name='{1}' id='{1}' rows='{3}' placeholder='{4}' {5}>{2}</textarea> ",
      options.inputClass,
      options.name,
      options.value,
      options.rows,
      options.placeholder,
      readOnly
    );
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat('</div>');
    $(options.container).append(fieldHtml.toString());
    if ($.isPlainObject(options.events)) {
      $('#' + options.name, options.container).on(options.events);
    }
  };

  formBuilder.buildSwitchRadio = function (options) {
    var opt = $.extend({}, defaultOptions, options || {});
    var uid = commons.UUID.createUUID();
    var openValue = options.items[0].id;
    var closeValue = options.items[1].id;
    var openText = options.items[0].text;
    var closeText = options.items[1].text;
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat("<div class='form-group formbuilder clear {0}'>", options.divClass);
    fieldHtml.appendFormat(
      "	<label class='{0}{1} control-label {2} label-formbuilder' for='{3}' >{4}{5}</label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.name,
      options.label,
      options.isRequired ? '<font color="red" size="2">*</font>' : ''
    );
    fieldHtml.appendFormat("	<div class='{0}{1} controls'>", colPrefix, options.controlColSpan);
    var switchHtml = new commons.StringBuilder();
    switchHtml.appendFormat("<div class='switch-wrap' id='{0}'>", uid);
    switchHtml.appendFormat(
      '<span class="switch-text switch-open" style="{0}">{1}</span>',
      opt.value === openValue ? '' : 'display:none;',
      openText
    );
    switchHtml.appendFormat('<span class="switch-radio" data-status="0"></span>');
    switchHtml.appendFormat(
      '<span class="switch-text switch-close" style="{0}">{1}</span>',
      opt.value == undefined || opt.value == '' || opt.value === closeValue ? '' : 'display:none;',
      closeText
    );
    switchHtml.appendFormat(
      "	<input value='{0}' style='display: none;' class='{1}' " + " {2} name='{3}' type='radio' id='{4}'>",
      openValue,
      options.inputClass,
      opt.value === openValue,
      options.name,
      uid + 'radio1'
    );
    switchHtml.appendFormat(
      "	<input value='{0}' style='display: none;' class='{1}' " + " {2} name='{3}' type='radio' id='{4}'>",
      closeValue,
      options.inputClass,
      opt.value === closeValue,
      options.name,
      uid + 'radio1'
    );
    switchHtml.appendFormat('</div>');
    fieldHtml.appendFormat(switchHtml.toString());
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat('</div>');
    if (opt.container) {
      $(opt.container).append(fieldHtml.toString());
      $('#' + uid).on('click', function () {
        var isOpen = $(this).hasClass('active'); //当前是否开启状态
        if (isOpen) {
          $(this).removeClass('active');
          //关闭
          $(this).find('.switch-open').hide();
          $(this).find('.switch-close').show();
          $("input[type='radio']:eq(1)", '#' + uid).prop('checked', true);
        } else {
          $(this).addClass('active');
          //打开
          $(this).find('.switch-open').show();
          $(this).find('.switch-close').hide();
          $("input[type='radio']:eq(0)", '#' + uid).prop('checked', true);
        }
      });
      if ($.isPlainObject(options.events)) {
        $container.find("input[name='" + options.name + "']").on(options.events);
      }
    }
  };

  /**
   * 创建一个包含lable的pre
   */
  formBuilder.buildPre = function (options) {
    var options = initOptions(
      {
        rows: 3
      },
      options
    );
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat("<div class='form-group formbuilder clear {0}'>", options.divClass);
    fieldHtml.appendFormat(
      "	<label class='{0}{1} control-label {2} label-formbuilder' for='{3}' >{4}</label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.name,
      options.label
    );
    fieldHtml.appendFormat("	<div class='{0}{1} controls'>", colPrefix, options.controlColSpan);
    fieldHtml.appendFormat(
      "		<pre class='form-control {0}' style='width:100%;min-height:200px;' name='{1}' id='{1}' rows='{3}' placeholder='{4}' >{2}</pre> ",
      options.inputClass,
      options.name,
      options.value,
      options.rows,
      options.placeholder
    );
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat('</div>');
    $(options.container).append(fieldHtml.toString());
    if ($.isPlainObject(options.events)) {
      $('#' + options.name, options.container).on(options.events);
    }
  };

  /**
   * 创建一个包含lable的时间选择控件
   */
  formBuilder.buildDatetimepicker = function (options) {
    var options = initOptions(
      {
        dataIcon: 'iconfont icon-ptkj-rilixuanzeriqi'
      },
      options
    );

    if (!_.isEmpty(options.controlOption)) {
      if (options.timePicker) {
        options.minDate = options.controlOption.minDate;
        if (options.timePicker.format.indexOf('time') > 0) {
          options.dataIcon = 'iconfont icon-ptkj-shizhongxuanzeshijian';
        }
      } else {
        options.timePicker = {
          format: options.controlOption.format,
          minDate: options.controlOption.minDate
        };
      }
    }

    var timePicker = $.extend({}, defaultDatetimepickerOptions, options.timePicker);
    var $datePicker;
    var controlName = options.name + UUID.createUUID();
    var _format = timePicker.format.split('|');
    var dateIconClass = 'input-group-addon';
    if (_format.length > 1) {
      timePicker.type = _format[0];
      timePicker.format = _format[1];
      dateIconClass = 'date-icon';
    }

    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat("<div class='form-group formbuilder clear {0}'>", options.divClass);
    fieldHtml.appendFormat(
      "	<label class='{0}{1} control-label {2} label-formbuilder' >{4}{5}</label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.name,
      options.label,
      options.isRequired ? '<font color="red" size="2">*</font>' : ''
    );
    fieldHtml.appendFormat("	<div class='{0}{1} controls'>", colPrefix, options.controlColSpan);
    fieldHtml.appendFormat("	<div class='input-group date' id='div_{0}' style='width: 100%'> ", options.name);
    fieldHtml.appendFormat(
      "    	<input type='hidden' class='form-control {0}'  name='{1}' id='{1}'  value='{2}'>",
      options.inputClass,
      options.name,
      options.value
    );
    fieldHtml.appendFormat(
      "    	<input type='text' readonly class='form-control {0}'  name='{1}' id='{1}'  value='{2}' style='background: #fff!important'>",
      options.inputClass,
      controlName,
      options.value
    );
    fieldHtml.appendFormat("    	<span class='{0}'><i class='{1}'></i></span>", dateIconClass, options.dataIcon);
    // fieldHtml.appendFormat("    	<span class='input-group-addon'><i class='glyphicon {0}'></i></span>", options.dataIcon);
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat('</div>');
    $(options.container).append(fieldHtml.toString());

    var timeTypeArr = null;
    var _params = {
      elem: '#' + controlName,
      format: timePicker.format,
      trigger: 'click',
      done: function (value) {
        $(options.container)
          .find('#' + options.name)
          .val(value)
          .trigger('change');
      }
    };
    var newDate = new Date();

    if (options.controlOption && options.controlOption.range && options.controlOption.range == '1') {
      _params.min = newDate.format('yyyy-MM-dd ') + '23:59:59';
    } else if (options.controlOption && options.controlOption.range && options.controlOption.range == '2') {
      var h = newDate.getHours();
      var m = newDate.getMinutes();
      var s = newDate.getSeconds();
      _params.min = '1900-01-01 00:00:00';
      _params.max = (h > 9 ? h : '0' + h) + ':' + (m > 9 ? m : '0' + m) + ':' + (s > 9 ? s : '0' + s);
    }
    if (timePicker.type) {
      timeTypeArr = timePicker.type.split('-');
      _params.type = timeTypeArr[0];
      if (timeTypeArr[1] && timeTypeArr[1] === 'range') {
        _params.range = '至';
      }
    }
    if (options.controlOption.change) {
      _params.change = options.controlOption.change;
    }

    if (timeTypeArr) {
      try {
        laydate.render(_params);
      } catch (error) {
        console.log(error);
      }
      return;
    }

    if ($(options.container).length != 0) {
      $datePicker = $(options.container)
        .find('#div_' + options.name)
        .datetimepicker(timePicker);
    } else {
      $datePicker = $('#div_' + options.name).datetimepicker(timePicker);
    }
    if ($.isPlainObject(options.events)) {
      if ($(options.container).length != 0) {
        $(options.container)
          .find('#' + options.name)
          .on(options.events);
      } else {
        $('#' + options.name).on(options.events);
      }
    }
  };
  /**
   * 创建一个包含lable的时间选择控件（时间输入域为一个区间）
   */
  formBuilder.buildBetweenDatetimepicker = function (options) {
    var options = initOptions(
      {
        beginName: '',
        beginValue: '',
        endName: '',
        endValue: '',
        dataIcon: 'glyphicon-calendar'
      },
      options
    );
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat("<div class='form-group formbuilder clear {0}'>", options.divClass);
    fieldHtml.appendFormat(
      "	<label class='{0}{1} control-label {2} label-formbuilder' for='{3}' >{4}{5}</label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.beginName,
      options.label,
      options.isRequired ? '<font color="red" size="2">*</font>' : ''
    );
    fieldHtml.appendFormat("	<div class='{0}{1} controls'>", colPrefix, options.controlColSpan);
    fieldHtml.appendFormat("	<div class='row' >");
    fieldHtml.appendFormat(
      "	<div class='input-group date ' id='div_{0}' style='padding-left: 15px;width:45%;float: left;'> ",
      options.beginName
    );
    fieldHtml.appendFormat(
      "    	<input type='text' class='form-control {0}'  name='{1}' id='{1}' value='{2}' >",
      options.inputClass,
      options.beginName,
      options.beginValue
    );
    fieldHtml.appendFormat("    	<span class='input-group-addon'><i class='glyphicon {0}'></i></span>", options.dataIcon);
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat("	<span style='margin-top:7px;float: left;padding-right: 5px;padding-left: 5px;'>至</span>");
    fieldHtml.appendFormat(
      "	<div class='input-group date' id='div_{0}' style='padding-right: 15px;width:45%;float: right;'> ",
      options.endName
    );
    fieldHtml.appendFormat(
      "    	<input type='text' class='form-control {0}'  name='{1}' id='{1}' value='{2}' >",
      options.inputClass,
      options.endName,
      options.endValue
    );
    fieldHtml.appendFormat("    	<span class='input-group-addon'><i class='glyphicon {0}'></i></span>", options.dataIcon);
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat('</div>');
    $(options.container).append(fieldHtml.toString());
    var timePicker = $.extend({}, defaultDatetimepickerOptions, options.timePicker);
    var dateChangeEvent = function (event) {
      if (event.date && event.date.format) {
        $('input.form-control', this).attr('title', event.date.format(options.timePicker.format));
        var setDateFunction = 'maxDate';
        var $targetDt = $(options.container).find('#div_' + options.beginName);
        if (this.id == 'div_' + options.beginName) {
          setDateFunction = 'minDate';
          $targetDt = $(options.container).find('#div_' + options.endName);
        }
        $targetDt.data('DateTimePicker')[setDateFunction].call(this, event.date);
      }
    };
    if (timePicker.format.indexOf('|') > -1) {
      timePicker.format = timePicker.format.split('|')[1];
    }
    timePicker.format = timePicker.format.replace(/y/g, 'Y');
    timePicker.format = timePicker.format.replace(/d/g, 'D');
    $(options.container)
      .find('#div_' + options.beginName)
      .css('width', '46%')
      .on('dp.change', dateChangeEvent)
      .datetimepicker(timePicker);
    $(options.container)
      .find('#div_' + options.endName)
      .css('width', '46%')
      .on('dp.change', dateChangeEvent)
      .datetimepicker(
        $.extend(
          {
            useCurrent: false
          },
          timePicker
        )
      );

    //		var $fEnd = $("#div_" + options.endName + ">input.form-control");
    //		var $fBegin = $("#div_" + options.beginName + ">input.form-control");
    //		$.each([$fBegin, $fEnd], function(index, $field){
    //			$field.on("focus", function() {
    //				$(this).parent().animate({"width":"65%"});
    //				($fBegin.is(this) ? $fEnd : $fBegin).parent().animate({"width":"25%"});
    //			}).on("blur", function() {
    //				$fEnd.parent().animate({"width":"45%"});
    //				$fBegin.parent().animate({"width":"45%"});
    //			});
    //		});
    if ($.isPlainObject(options.events)) {
      $(options.container)
        .find('#' + options.beginName)
        .on(options.events);
      $(options.container)
        .find('#' + options.endName)
        .on(options.events);
    }
  };
  /**
   * 创建一个包含lable的Checkbox
   */
  formBuilder.buildCheckbox = function (options) {
    var options = initOptions(
      {
        items: []
      },
      options
    );
    if (options.items.length == 0 && options.controlOption) {
      options.items = this._getFieldOptionValue(options.controlOption);
    }
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat("<div class='form-group formbuilder clear {0}'>", options.divClass);
    fieldHtml.appendFormat(
      "	<label class='{0}{1} control-label {2} label-formbuilder' for='{3}' >{4}{5}</label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.name,
      options.label,
      options.isRequired ? '<font color="red" size="2">*</font>' : ''
    );
    fieldHtml.appendFormat("	<div class='{0}{1} controls'>", colPrefix, options.controlColSpan);
    $.each(options.items, function (index, item) {
      var uid = commons.UUID.createUUID();
      var checked = $.inArray(item.id, options.value) > -1 ? 'checked' : '';
      if (options.checked) {
        checked = options.checked ? 'checked' : '';
      }
      fieldHtml.appendFormat(
        "			<input value='{0}' class='{1}'" + " {2} name='{3}' type='checkbox' id='{4}'>",
        item.id,
        options.inputClass,
        checked,
        options.name,
        uid
      );
      fieldHtml.appendFormat("		<label class='checkbox-inline label-formbuilder' for='{0}'>&nbsp;{1}</label>", uid, item.text);
    });
    var $container = $(options.container);
    $container.append(fieldHtml.toString());
    if (options.events) {
      $container.find("input[name='" + options.name + "']").on(options.events);
    }
  };
  /**
   * 创建一个包含lable的Radio
   */
  formBuilder.buildRadio = function (options) {
    var options = initOptions(
      {
        items: []
      },
      options
    );
    if (options.items.length == 0 && options.controlOption) {
      options.items = this._getFieldOptionValue(options.controlOption);
    }
    if (options.controlOption && options.controlOption.useSwitchOpenStyle == '1' && options.items && options.items.length == 2) {
      //开关式的选项
      formBuilder.buildSwitchRadio(options);
      return;
    }
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat("<div class='form-group formbuilder clear {0}'>", options.divClass);
    fieldHtml.appendFormat(
      "	<label class='{0}{1} control-label {2} label-formbuilder' for='{3}' >{4}{5}</label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.name,
      options.label,
      options.isRequired ? '<font color="red" size="2">*</font>' : ''
    );
    fieldHtml.appendFormat("	<div class='{0}{1} {2} controls'>", colPrefix, options.controlColSpan, options.controlClass);
    $.each(options.items, function (index, item) {
      var checked = item.id == options.value ? 'checked' : '';
      var uid = commons.UUID.createUUID();
      fieldHtml.appendFormat(
        "	<input value='{0}' class='{1}' " + " {2} name='{3}' type='radio' id='{4}'>",
        item.id,
        options.inputClass,
        checked,
        options.name,
        uid
      );
      var labelStyle = options.isBlock ? "style='display:block;'" : '';
      fieldHtml.appendFormat("<label {2} class='radio-inline label-formbuilder' for='{0}'>&nbsp;{1}</label>", uid, item.text, labelStyle);
    });
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat('</div>');
    var $container = $(options.container);
    $container.append(fieldHtml.toString());
    if ($.isPlainObject(options.events)) {
      $container.find("input[name='" + options.name + "']").on(options.events);
    }
  };
  /**
   * 创建一个包含lable的Select
   */
  formBuilder.buildSelect = function (options) {
    options = initOptions(
      {
        defaultBlank: false,
        items: []
      },
      options
    );
    if (options.items.length == 0 && options.controlOption) {
      options.items = this._getFieldOptionValue(options.controlOption);
    }
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat("<div class='form-group formbuilder clear {0}'>", options.divClass);
    fieldHtml.appendFormat(
      "	<label class='{0}{1} control-label {2} label-formbuilder' for='{3}' >{4}{5}</label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.name,
      options.label,
      options.isRequired ? '<font color="red" size="2">*</font>' : ''
    );
    fieldHtml.appendFormat("	<div class='{0}{1} controls'>", colPrefix, options.controlColSpan);
    fieldHtml.appendFormat("		<select class='form-control {0}' name='{1}' id='{1}'> ", options.inputClass, options.name);
    if (options.defaultBlank) {
      options.items.unshift({
        id: '',
        text: ''
      });
    }
    $.each(options.items, function (index, item) {
      var checked = item.id == options.value ? 'selected' : '';
      fieldHtml.appendFormat("<option value='{0}' {1}>{2}</opton>", item.id, checked, item.text);
    });
    fieldHtml.appendFormat('		</select>');
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat('</div>');
    $(options.container).append(fieldHtml.toString());
    if ($.isPlainObject(options.events)) {
      $('#' + options.name, options.container).on(options.events);
    }
  };
  /**
   * 创建一个包含lable的Select2
   */
  var defaultSelect2Options = {};
  formBuilder.buildSelect2 = function (options) {
    options = initOptions(
      {
        defaultBlank: false,
        display: '',
        displayValue: '',
        select2: null
      },
      options
    );
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat("<div class='form-group formbuilder clear {0}'>", options.divClass);
    fieldHtml.appendFormat(
      "	<label class='{0}{1} control-label {2} label-formbuilder' for='{3}' >{4}{5}</label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.name,
      options.label,
      options.isRequired ? '<font color="red" size="2">*</font>' : ''
    );
    fieldHtml.appendFormat("	<div class='{0}{1} controls'>", colPrefix, options.controlColSpan);
    if (StringUtils.isNotBlank(options.display)) {
      fieldHtml.appendFormat(
        "    	<input type='hidden' class='form-control {0}'  name='{1}' id='{1}' value='{2}' >",
        options.inputClass,
        options.display,
        options.displayValue
      );
    }
    fieldHtml.appendFormat(
      "    	<input type='text' style='width:100%' class='form-control {0}' name='{1}' id='{1}' value='{2}' >",
      options.inputClass,
      options.name,
      options.value
    );
    fieldHtml.appendFormat('	</div>');
    if (options.help) {
      fieldHtml.appendFormat(
        "<span class='glyphicon glyphicon-question-sign form-field-help' style='float: right;top: -22px;' title='{0}'></span>",
        options.help
      );
    }
    fieldHtml.appendFormat('</div>');
    var $container = $(options.container);
    $container.append(fieldHtml.toString());
    var $select2 = $container.find('#' + options.name);
    //没有传递select2参数，则检查 controlOption，重新获取
    if (options.select2 == null) {
      if (options.controlOption) {
        options.select2 = formBuilder.getSelect2OptionValue(options.controlOption);
      }
    }

    if (options.controlOption) {
      // 是否多选
      options.select2.multiple = options.select2.multiple ? options.select2.multiple : options.controlOption.multiple === '1';
      // 是否全选
      options.select2.chooseAll = options.select2.chooseAll ? options.select2.chooseAll : options.controlOption.chooseAll === '1';
    }

    var select2 = $.extend({}, defaultSelect2Options, options.select2);
    if (StringUtils.isNotBlank(options.display)) {
      select2.labelField = options.display;
    }
    select2.labelField = options.display;
    select2.valueField = options.name;
    if (options.controlOption.defaultBlankId && options.controlOption.defaultBlankText) {
      select2.defaultBlank = true;
      select2.defaultBlankText = options.controlOption.defaultBlankText;
      select2.defaultBlankValue = options.controlOption.defaultBlankId;
    }
    select2.container = $container;
    // select2.defaultBlank = select2.defaultBlank || options.defaultBlank;
    $select2.wSelect2(select2);
    $container.find('.select2-container').css('padding', '0');
    $container.find('.select2-choice').css('height', '35px').find('span').css('line-height', '35px');
    //$select2.select2("val", options.value); 已经在fieldHtml中赋值过了，不需要再次赋值了
    if ($.isPlainObject(options.events)) {
      $select2.on(options.events);
    }
    this.setEvent(options);
  };

  /**
   * 创建一个带label的分组下来框
   */
  var defaultSelect2GroupOptions = {};
  formBuilder.buildSelect2Group = function (options) {
    options = initOptions(
      {
        defaultBlank: false,
        display: '',
        displayValue: '',
        select2Group: null
      },
      options
    );
    var fieldHtml = new commons.StringBuilder();
    fieldHtml.appendFormat("<div class='form-group formbuilder clear {0}'>", options.divClass);
    fieldHtml.appendFormat(
      "	<label class='{0}{1} control-label {2} label-formbuilder' for='{3}' >{4}{5}</label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.name,
      options.label,
      options.isRequired ? '<font color="red" size="2">*</font>' : ''
    );
    fieldHtml.appendFormat("	<div class='{0}{1} controls'>", colPrefix, options.controlColSpan);
    if (StringUtils.isNotBlank(options.display)) {
      fieldHtml.appendFormat(
        "    	<input type='hidden' class='form-control {0}'  name='{1}' id='{1}' value='{2}' >",
        options.inputClass,
        options.display,
        options.displayValue
      );
    }
    fieldHtml.appendFormat(
      "    	<input type='text' style='width:100%' class='form-control {0}'  name='{1}' id='{1}' value='{2}' >",
      options.inputClass,
      options.name,
      options.value
    );
    fieldHtml.appendFormat('	</div>');
    if (options.help) {
      fieldHtml.appendFormat(
        "<span class='glyphicon glyphicon-question-sign form-field-help' style='float: right;top: -22px;' title='{0}'></span>",
        options.help
      );
    }
    fieldHtml.appendFormat('</div>');
    var $container = $(options.container);
    $container.append(fieldHtml.toString());
    var $select2Group = $('#' + options.name);
    //没有传递select2Group，则判断是否有传递 controlOption， 有则从定义中加载
    if (options.select2Group == null || options.select2Group.length == 0) {
      if (options.controlOption) {
        var serviceAndMethod = options.controlOption.serviceUrl.split('.');
        options.select2Group = {
          serviceName: serviceAndMethod[0],
          queryMethod: serviceAndMethod[1],
          params: {
            selected: options.value
          },
          valueField: options.name,
          placeholder: '请选择',
          multiple: false,
          remoteSearch: false,
          width: '100%',
          height: 250
        };
      }
    }
    var select2Group = $.extend({}, defaultSelect2GroupOptions, options.select2Group);
    if (StringUtils.isNotBlank(options.display)) {
      select2Group.labelField = options.display;
    }
    select2Group.defaultBlank = select2Group.defaultBlank || options.defaultBlank;
    $select2Group.wSelect2Group(select2Group);
    $container.find('.select2-container').css('padding', '0');
    $container.find('.select2-choice').css('height', '35px').find('span').css('line-height', '35px');
    //$select2.select2("val", options.value); 已经在fieldHtml中赋值过了，不需要再次赋值了
    if ($.isPlainObject(options.events)) {
      $select2Group.on(options.events);
    }
    //select2会对input生成title属性，导致validate的错误提示语会出错，所以需要把title属性移除掉
    $select2Group.removeAttr('title');
  };

  /**
   * 创建一个包含lable的Select2
   */
  var defaultContentOptions = {
    multiLine: false,
    rowClass: '',
    container: '',
    labelColSpan: '2',
    controlColSpan: '10',
    divClass: '',
    inputClass: '',
    contentItems: []
  };
  var copyProperties = ['labelColSpan', 'controlColSpan', 'divClass', 'inputClass'];
  var copyProperty = function (source, target, properties) {
    $.each(properties, function (index, property) {
      if (StringUtils.isBlank(target[property])) {
        target[property] = source[property];
      }
    });
  };

  /**
   * 创建树形的选择下拉
   * @param options
   */
  formBuilder.buildTreeSelect = function (options) {
    formBuilder.buildWCommonComboTree({
      divClass: options.divClass,
      container: options.container,
      label: options.label,
      name: options.name,
      value: options.value,
      isRequired: options.isRequired,
      labelColSpan: options.labelColSpan,
      labelClass: options.labelClass,
      controlColSpan: options.controlColSpan,
      wCommonComboTree: {
        service: 'cdDataStoreService.loadTreeNodes',
        serviceParams: [
          {
            dataStoreId: options.controlOption.dataStore,
            valueColumn: options.controlOption.valueColumn,
            uniqueColumn: options.controlOption.uniqueColumn,
            parentColumn: options.controlOption.parentColumn,
            displayColumn: options.controlOption.textColumn,
            defaultCondition: options.controlOption.defaultCondition,
            noCheckLevel: options.controlOption.noCheckLevel != undefined ? options.controlOption.noCheckLevel.split(',') : []
          }
        ],
        multiSelect: options.controlOption.multiple === '1', // 是否多选
        parentSelect: true,
        showCheckAll: false,
        showIcon: options.controlOption.showIcon === '1',
        async: true,
        readonly: true,
        value: null,
        onAfterSetValue: function (e, self) {
          self.$element.data('value', self.getValueNodes());
          self.$element.trigger('onTreeSelectChange');
        }
      }
    });
  };

  /**
   * 创建一个包含lable的组织选择框
   */
  formBuilder.buildUnit = function (options) {
    var _self = this;
    var options = initOptions({}, options);
    var fieldHtml = new commons.StringBuilder();
    if (!options.display) {
      options.display = options.name + '_display';
    }
    //控件真实的valueField 防止相同命名串数据
    var controlName = options.name + UUID.createUUID();
    // 默认展现带图标
    options.controlOption.orgStyle = options.controlOption.orgStyle || 'org-style3';
    //有设置值，但是没有设置对应text,则需要去服务端查找对应的名称
    if (options.value && !options.text) {
      var values = options.value.split(';');
      options.text = _self.getOrgName(values);
    }
    fieldHtml.appendFormat("<div class='form-group formbuilder clear {0}'>", options.divClass);
    fieldHtml.appendFormat(
      "	<label class='{0}{1} control-label {2} label-formbuilder' >{4}{5}</label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.name,
      options.label,
      options.isRequired ? '<font color="red" size="2">*</font>' : ''
    );
    fieldHtml.appendFormat("	<div class='{0}{1} controls'>", colPrefix, options.controlColSpan);
    fieldHtml.appendFormat(
      "    	<input type='text' class='form-control {0}'  name='{1}' id='{1}' value='{2}' readonly >",
      options.inputClass,
      options.display,
      options.text ? options.text : ''
    );
    fieldHtml.appendFormat(
      "    	<input type='hidden' class='form-control {0}'  name='{1}' id='{1}' value='{2}' >",
      options.inputClass,
      options.name,
      options.value
    );
    fieldHtml.appendFormat(
      "    	<input type='hidden' class='form-control {0}'  name='{1}' id='{1}' value='{2}' >",
      options.inputClass,
      controlName,
      options.value
    );
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat('</div>');
    $(options.container).append(fieldHtml.toString());
    var orgTypes = options.controlOption.orgTypes;
    if (StringUtils.isBlank(orgTypes) || orgTypes.indexOf('all') >= 0) {
      orgTypes = 'all';
    }
    var nodeTypes = options.controlOption.nodeTypes;
    if (StringUtils.isBlank(nodeTypes) || nodeTypes.indexOf('all') >= 0) {
      nodeTypes = 'all';
    }
    var orgOptions = {
      labelField: options.display,
      valueField: controlName,
      callback: function () {
        if (options.callback) {
          options.callback.apply(this, arguments);
        } else {
          $(options.container)
            .find('#' + options.name)
            .val(arguments[0].join(options.separator || ';'));
          $(options.container)
            .find('#' + options.display)
            .val(arguments[1].join(options.separator || ';'));
        }
        $('#' + options.name).trigger('change');
      },
      type: orgTypes,
      selectTypes: nodeTypes,
      defaultType: options.controlOption.defaultType,
      multiple: '1' == options.controlOption.multiple ? true : false,
      valueFormat: options.controlOption.valueFormat ? options.controlOption.valueFormat : 'justId'
    };
    orgOptions.otherParams = {
      selectTypes: nodeTypes
    };
    if (options.controlOption.jsonParams) {
      try {
        orgOptions.otherParams = $.extend({}, orgOptions.otherParams, JSON.parse(options.controlOption.jsonParams));
      } catch (e) {
        throw new Error('组织选择项不合法的参数格式');
      }
    }
    var events = {
      click: function () {
        appContext.require(['multiOrg'], function (unit2) {
          unit2.open(orgOptions);
        });
      }
    };
    events = options.events || events;
    if ($.isPlainObject(events)) {
      var $editElemtnt = $('#' + options.display, options.container);
      if (options.controlOption.orgStyle) {
        appContext.require(['multiOrg'], function (unit2) {
          $editElemtnt.orgSelect({
            orgOptions: orgOptions,
            trigger: options.trigger || 'click',
            orgStyle: options.controlOption.orgStyle
          });
        });
      }
      $editElemtnt.on(events);
    }
  };
  /**
   * 创建一个包含lable的附件上传
   */
  formBuilder.buildFileUpload = function (options, isAsLabel) {
    options = initOptions({}, options);
    var controlOption = options.controlOption || {};
    var fieldHtml = new commons.StringBuilder();
    var fileUploadId = UUID.createUUID();

    var addFileText = controlOption.addFileText || '添加文件';
    var singleFile =
      options.singleFile == '1' ||
      options.singleFile === true ||
      controlOption.singleFile == '1' ||
      controlOption.singleFile === true ||
      false; //单文件上传
    var isAsLabel = isAsLabel != undefined ? isAsLabel : options.isAsLabel || false;

    fieldHtml.appendFormat("<div class='form-group formbuilder clear {0}'>", options.divClass);
    fieldHtml.appendFormat(
      "	<label class='{0}{1} control-label {2} label-formbuilder label-for-upload' for='{3}' >{4}{5}</label>",
      colPrefix,
      options.labelColSpan,
      options.labelClass,
      options.name,
      options.label,
      options.isRequired ? '<font color="red" size="2">*</font>' : ''
    );
    fieldHtml.appendFormat(
      "	<div class='{0}{1} controls controls-upload' style='height: auto;min-height: 56px;'>",
      colPrefix,
      options.controlColSpan
    );
    fieldHtml.appendFormat("<div id='{0}'></div>", fileUploadId);
    fieldHtml.appendFormat(
      "<input name='{1}' class='form-control {0}' type='hidden' value='{2}'/>",
      options.inputClass,
      options.name,
      options.value || ''
    );
    fieldHtml.appendFormat("<input name='{1}_fileNames' class='form-control {0}' type='hidden' />", options.inputClass, options.name);
    fieldHtml.appendFormat('	</div>');
    fieldHtml.appendFormat('</div>');

    var $container = $(options.container);
    $container.append(fieldHtml.toString());
    var fuleuploadCtlId = UUID.createUUID();
    var fileupload = new WellFileUpload(fuleuploadCtlId, {
      uploadButtonLable: addFileText,
      events: options.events
    });
    fileupload.initAllowUploadDeleteDownload(true, true, true);
    var fileIdInput = $container.find("[name='" + options.name + "']");
    var fileNameInput = $container.find("[name='" + options.name + "_fileNames']");
    fileupload.uploadOkCallback = function (fileInfo) {
      var fids = [];
      var fnames = [];
      for (var f = 0; f < this.files.length; f++) {
        fids.push(this.files[f].fileID);
        fnames.push(this.files[f].fileName);
      }
      fileIdInput.val(fids.join(';'));
      fileNameInput.val(fnames.join(';'));
      var $div = fileIdInput.parent();
      var fileHeight = $div.outerHeight();
      if (fileHeight != 0) {
        $div.prev().css('height', fids.length ? fileHeight : 'auto');
      }
    };

    fileupload.deleteOkCallback = fileupload.uploadOkCallback;
    fileupload.initFileUploadExtraParam(
      controlOption.isShowFileFormatIcon,
      controlOption.isShowFileSourceIcon,
      controlOption.secDevBtnIdStr,
      controlOption.fileSourceIdStr,
      controlOption.flowSecDevBtnIdStr
    );
    fileupload.init(
      isAsLabel,
      $('#' + fileUploadId),
      false,
      !singleFile,
      (function () {
        var initFiles = [];
        var fids = fileIdInput.val();
        if (fids) {
          var fidsArr = fids.split(';');
          for (var i = 0; i < fidsArr.length; i++) {
            var f = WellFileUpload.loadFileFromDb(fidsArr[i]);
            if (f) {
              initFiles.push(f);
            }
          }
        }
        return initFiles;
      })()
    );
    if (options.ext) {
      fileupload.setFileExt(options.ext);
    }
  };

  /**
   * 构造输入域的内容，可以支持一行一列或者一行两列的布局
   */
  formBuilder.buildContent = function (options) {
    var options = $.extend({}, defaultContentOptions, options);
    var _self = this;

    // var columnNum = options.multiLine ? 2 : 1;
    // options.divClass = colPrefix + (options.multiLine ? "6" : "12") + " " + options.divClass;
    var multiLine = typeof options.multiLine === 'boolean' ? (options.multiLine ? 2 : 1) : options.multiLine;
    var columnNum = 12 / multiLine;
    options.divClass = colPrefix + columnNum + ' ' + options.divClass;
    var $container = $(options.container);
    var $containerElement = null;
    $.each(options.contentItems, function (index, itemOptions) {
      if (index % multiLine === 0) {
        $containerElement = $("<div class='row " + options.rowClass + "'></div>");
        $container.append($containerElement);
      }
      itemOptions.container = $containerElement;
      copyProperty(options, itemOptions, copyProperties);
      switch (itemOptions.type) {
        case 'text':
          _self.buildInput(itemOptions);
          break;
        case 'textarea':
          _self.buildTextarea(itemOptions);
          break;
        case 'timeInterval':
          _self.buildBetweenDatetimepicker(itemOptions);
          break;
        case 'date':
          _self.buildDatetimepicker(itemOptions);
          break;
        case 'radio':
          _self.buildRadio(itemOptions);
          break;
        case 'select':
          _self.buildSelect(itemOptions);
          break;
        case 'select2':
          _self.buildSelect2(itemOptions);
          break;
        case 'select2Group': //分组下拉框
          _self.buildSelect2Group(itemOptions);
          break;
        case 'checkbox':
          _self.buildCheckbox(itemOptions);
          break;
        case 'unit':
          _self.buildUnit(itemOptions);
          break;
        case 'fileUpload':
          _self.buildFileUpload(itemOptions, false);
          break;
        case 'label':
          _self.buildLabel(itemOptions);
          break;
        case 'treeSelect':
          _self.buildTreeSelect(itemOptions);
          break;
        default:
          _self.buildInput(itemOptions);
      }
    });
  };

  //表单数据，以label形态展示，只读状态
  formBuilder.buildContentAsLabel = function (options) {
    var options = $.extend({}, defaultContentOptions, options);
    var _self = this;
    // var columnNum = options.multiLine ? 2 : 1;
    // options.divClass = colPrefix + (options.multiLine ? "6" : "12") + " " + options.divClass;
    var multiLine = typeof options.multiLine === 'boolean' ? (options.multiLine ? 2 : 1) : options.multiLine;
    var columnNum = 12 / multiLine;
    options.divClass = colPrefix + columnNum + ' ' + options.divClass;
    var $container = $(options.container);
    var $containerElement = null;
    $.each(options.contentItems, function (index, itemOptions) {
      if (index % columnNum == 0) {
        $containerElement = $("<div class='row " + options.rowClass + "'></div>");
        $container.append($containerElement);
      }
      itemOptions.container = $containerElement;
      copyProperty(options, itemOptions, copyProperties);
      itemOptions.text = itemOptions.text || itemOptions.value;
      switch (itemOptions.type) {
        case 'date':
          //按定义的格式进行转化
          var format = itemOptions.controlOption.format;
          if (format) {
            if (format.indexOf('|') > -1) {
              format = format.split('|')[1];
            }
            //换成moment的格式
            format = format.replace(new RegExp('y', 'gm'), 'Y');
            format = format.replace(new RegExp('d', 'gm'), 'D');
            var text = moment(itemOptions.value).format(format);
            itemOptions.text = text;
          }
          _self.buildLabel(itemOptions);
          break;
        case 'radio':
        case 'checkbox':
        case 'select':
          if (itemOptions.value) {
            var optionDatas = _self._getFieldOptionValue(itemOptions.controlOption);
            itemOptions.text = _self._getSelectText(optionDatas, itemOptions.value);
          } else {
            itemOptions.text = '';
          }
          _self.buildLabel(itemOptions);
          break;
        case 'select2':
          if (itemOptions.value) {
            var selectData = _self.getSelect2OptionValue(itemOptions.controlOption);
            itemOptions.text = _self._getSelectText(selectData.data, itemOptions.value);
          } else {
            itemOptions.text = '';
          }
          _self.buildLabel(itemOptions);
          break;
        case 'select2Group':
          if (itemOptions.value) {
            var selectData = _self.getSelect2GroupOptionValue(itemOptions.controlOption, itemOptions.value);
            itemOptions.text = selectData[itemOptions.value];
          } else {
            itemOptions.text = '';
          }
          _self.buildLabel(itemOptions);
          break;
        case 'unit':
          if (itemOptions.value) {
            var values = itemOptions.value.split(';');
            itemOptions.text = _self.getOrgName(values);
          }
          _self.buildLabel(itemOptions);
          break;
        case 'fileUpload':
          _self.buildFileUpload(itemOptions, true);
          break;
        default:
          _self.buildLabel(itemOptions);
      }
    });
  };

  formBuilder.getOrgName = function (ids) {
    var texts = [];
    $.ajax({
      type: 'POST',
      url: ctx + '/api/org/facade/getNameByOrgEleIds',
      dataType: 'json',
      data: {
        orgIds: ids
      },
      async: false,
      success: function (result) {
        if (result.success) {
          for (var i = 0; i < ids.length; i++) {
            result.data[ids[i]] && texts.push(result.data[ids[i]]);
          }
        }
      }
    });
    return texts.join(';');
  };

  formBuilder._getSelectText = function (optionDatas, selectValues) {
    var values = typeof selectValues == 'string' ? selectValues.split(';') : selectValues;
    var texts = [];
    $.each(optionDatas, function () {
      if ($.inArray(this.id, values) >= 0) {
        texts.push(this.text);
      }
    });
    return texts.join(';');
  };

  //获取select2控件对应的数据源
  formBuilder.getSelect2OptionValue = function (options) {
    var select2 = {};
    if (options.optionType != 3) {
      select2.defaultBlank = false;
      select2.data = this._getFieldOptionValue(options);
    } else {
      select2.serviceName = 'select2DataStoreQueryService';
      select2.queryMethod = 'loadSelectData';
      select2.selectionMethod = 'loadSelectDataByIds';
      select2.remoteSearch = true;
      select2.defaultBlank = true;
      select2.defaultBlankText = '';
      select2.multiple = true;
      select2.chooseAll = false;
      select2.params = {
        dataStoreId: options.dataStore,
        idColumnIndex: options.valueColumn,
        textColumnIndex: options.textColumn
      };
    }
    return select2;
  };

  formBuilder._getFieldOptionValue = function (options) {
    var optionValue = [];
    switch (options.optionType) {
      case '1': //常量
        return options.optionValue;
      case '2': //数据字典
        server.JDS.call({
          service: 'dataDictionaryMaintain.getDataDictionariesByParentUuid',
          data: [options.dataDic],
          async: false,
          success: function (result) {
            if (result.msg == 'success') {
              optionValue = $.map(result.data, function (data) {
                return {
                  id: data.code,
                  text: data.name
                };
              });
            }
          }
        });
        return optionValue;
      case '3': //数据仓库
        server.JDS.call({
          service: 'viewComponentService.loadAllData',
          data: [options.dataStore],
          async: false,
          success: function (result) {
            if (result.msg == 'success') {
              optionValue = $.map(result.data.data, function (data) {
                return {
                  id: data[options.valueColumn],
                  text: data[options.textColumn]
                };
              });
            }
          }
        });
        return optionValue;
      case '4':
        return optionValue;
      default:
        return optionValue;
    }
  };

  //获取select2group控件对应的数据源
  formBuilder.getSelect2GroupOptionValue = function (options, selected) {
    var optionValue = {};
    server.JDS.call({
      service: options.serviceUrl,
      data: {
        params: {
          id: selected
        }
      },
      async: false,
      success: function (result) {
        if (result.msg == 'success') {
          $.each(result.data.results, function (i) {
            $.each(result.data.results[i].children, function (j) {
              optionValue[this.id] = this.text;
            });
          });
        }
      }
    });
    return optionValue;
  };

  formBuilder.setEvent = function (options) {
    if (options.container) {
      //帮助文本展示
      $('.form-field-help', $(options.container)).tooltip();
    }
  };

  if (isGlobal === true) {
    window.formBuilder = formBuilder;
  }

  window.formBuilder = formBuilder;

  return formBuilder;
});
