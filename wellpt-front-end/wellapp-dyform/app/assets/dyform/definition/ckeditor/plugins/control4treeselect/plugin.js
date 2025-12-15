var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.initProperty = function (field) {
  var _this = this;
  this.initDictCode();
  if (field == null || typeof field == 'undefined') {
    field = new WComboTreeClass();
    field.dbDataType = dyFormInputType._text;
    field.valueCreateMethod = '1';
    field.inputMode = dyFormInputMode.treeSelect;
    field.allPath = 'false';
    field.clearAll = 'true';
    field.dictValueColumn = 'uuid';
  } else {
    field.allPath = field.allPath || 'true';
  }

  this.initDataSource2(field.dataSourceId, field.dataSourceText, function () {
    _this.initDataSourceTreeColumn(field);
    _this.$('#dataSourceId').val(field.dataSourceId).trigger('change');
  });

  //控件属性初始化公共设置.
  this.ctlPropertyComInitSet(field);
  //私有属性
  $('#serviceName').val(field.serviceName);

  $('#newServiceName').val(field.newServiceName).wSelect2({
    serviceName: 'selectiveDataService',
    queryMethod: 'getTreeNodeDataProviderSelect',
    valueField: 'newServiceName',
    placeholder: '请选择',
    params: {},
    multiple: false,
    remoteSearch: false,
    width: '100%'
  });

  $('#treeWidth').val(field.treeWidth);
  $('#treeHeight').val(field.treeHeight);
  if (field.selectParent) {
    $('#selectParent').prop('checked', true);
  }

  if (field.showCheckAll) {
    $('#showCheckAll').prop('checked', true);
  }

  if (field.expandAndCollapse) {
    $('#expandAndCollapse').prop('checked', true);
  }

  if (field.clearAll) {
    $('#clearAll').prop('checked', true);
  } else {
    $('#clearAll').prop('checked', false);
  }

  $('#showRightBtn').change(function () {
    var checked = $(this).attr('checked');
    if (checked) {
      $('.showRightBtn').show();
    } else {
      $('.showRightBtn').hide();
    }
  });

  if (field.dictCode != '' && field.dictCode != undefined) {
    this.$('#dictCode').val(field.dictCode);
    var dictcodearray = field.dictCode.split(':');
    this.$('#dictName').val(dictcodearray[1]);
    this.$('#display_dictName').find('.well-select-selected-value').text(dictcodearray[1]).show();
    this.$('#display_dictName').find('.well-select-placeholder').hide();
    this.$('#dictUuid').val(field.dictUuid);

    this.$('#showRightBtn').attr('checked', field.showRightBtn);
    if (field.showRightBtn) {
      this.$('.showRightBtn').show();
    }
    this.$('#dicCodeAddBtn').attr('checked', field.dicCodeAddBtn);
    this.$('#dicCodeMoveUp').attr('checked', field.dicCodeMoveUp);
    this.$('#dicCodeMoveDown').attr('checked', field.dicCodeMoveDown);
    this.$('#dicCodeDelBtn').attr('checked', field.dicCodeDelBtn);
  }

  // $('#defaultCondition').val(field.defaultCondition);

  var editor = ace.edit('defaultCondition');
  editor.setTheme('ace/theme/clouds');
  editor.session.setMode('ace/mode/sql');
  //启用提示菜单
  ace.require('ace/ext/language_tools');
  editor.setOptions({
    enableBasicAutocompletion: true,
    enableSnippets: true,
    enableLiveAutocompletion: true,
    showPrintMargin: false,
    enableVarSnippets: {
      value: 'wComponentDataStoreCondition',
      showSnippetsTabs: ['内置变量', '常用代码逻辑'],
      scope: ['sql']
    },
    enableCodeHis: {
      relaBusizUuid: '111',
      codeType: 'wBootstrapTable.defaultCondition',
      enable: true
    }
  });

  if (field.allPath) {
    $('input[name="allPath"][value="' + field.allPath + '"]').attr('checked', true);
  }

  if (field.defaultCondition) {
    editor.setValue(field.defaultCondition);
  }
  $('#defaultCondition').data('codeEditor', editor);
  $('#dictValueColumn')
    .val(field.dictValueColumn || 'uuid')
    .trigger('change');
  $("input[name='mutiSelect'][value='" + field.mutiSelect + "']").attr('checked', true);
  $("input[name='optionDataSource'][value='" + field.optionDataSource + "']").prop('checked', true);
  if (field.optionDataSource == '3') {
    $('#optionDataAutoSet2').prop('checked', field.optionDataAutoSet).trigger('change');
    $('#relateField2').val(field.relateField).trigger('change');
  } else if (field.optionDataSource == '2') {
    $('#optionDataAutoSet3').prop('checked', field.optionDataAutoSet).trigger('change');
    $('#relateField3').val(field.relateField).trigger('change');
  }

  $("input[name='optionDataSource']")
    .on('change', function () {
      var val = $("input[name='optionDataSource']:checked").val();
      if (val == 1) {
        $('.tr_serviceName').show();
        $('.tr_dataSource').hide();
        $('.tr_dicCode').hide();
      } else if (val == 2) {
        $('.tr_serviceName').hide();
        $('.tr_dataSource').show();
        $('.tr_dicCode').hide();
        var checked3 = $('#optionDataAutoSet3').prop('checked');
        $('.sourceAutoSet1')[checked3 ? 'show' : 'hide']();
      } else if (val == 3) {
        $('.tr_dicCode').show();
        $('.tr_serviceName').hide();
        $('.tr_dataSource').hide();
        var checked2 = $('#optionDataAutoSet2').prop('checked');
        $('.dicAutoSet1')[checked2 ? 'show' : 'hide']();
        $('.dicAutoSet2')[checked2 ? 'hide' : 'show']();
      }
    })
    .trigger('change');

  $('#unitUnique').val(field.unitUnique);
  if (field.unitUnique == 'true') {
    $('#checkRule_6').attr('checked', true);
    $('#checkRule_5').attr('checked', false);
  } else if (field.unitUnique == 'false') {
    $('#checkRule_5').attr('checked', true);
    $('#checkRule_6').attr('checked', false);
  }

  $("input[name='optionDataAutoSet2']").change(function () {
    var checked = $("input[name='optionDataAutoSet2']").prop('checked');
    if (checked) {
      $('.dicAutoSet1').show();
      $('.dicAutoSet2').hide();
    } else {
      $('.dicAutoSet1').hide();
      $('.dicAutoSet2').show();
    }
  });
  $("input[name='optionDataAutoSet3']").change(function () {
    var checked = $("input[name='optionDataAutoSet3']").prop('checked');
    if (checked) {
      $('.sourceAutoSet1').show();
    } else {
      $('.sourceAutoSet1').hide();
    }
  });

  var relateFieldData = [];

  for (var i in formDefinition.fields) {
    var fieldt = formDefinition.getField(i);

    if (!formDefinition.isCustomField(fieldt.name) || (field != null && fieldt.name == field.name)) {
      continue;
    }
    relateFieldData.push({
      id: fieldt.name,
      text: fieldt.displayName
    });
  }

  $('#relateField2').wellSelect({
    data: relateFieldData,
    valueField: 'relateField2'
  });
  $('#relateField3').wellSelect({
    data: relateFieldData,
    multiple: true,
    valueField: 'relateField3'
  });

  $('#dictValueColumn').wellSelect({
    data: [
      {
        id: 'uuid',
        text: 'uuid'
      },
      {
        id: 'code',
        text: 'code'
      }
    ],
    showEmpty: false,
    searchable: false,
    valueField: 'dictValueColumn'
  });

  var content1 =
    '<span>当备选项来源选择【取字典】时，请确保字典已按以下内容正确配置，否则将导致字段备选项为空，因为备选项是获取字典下的字典列表内容：</span><br>' +
    '<span>1、数据字典中存在相应的字典</span><br>' +
    '<span>2、字典下的字典列表不能为空</span>';
  toPopover('#optionDataSourceTip', content1);

  var content2 =
    '<span>存在前端显示与配置不一致的2个场景：</span><br>' +
    '<span>1、字典不存在时：前端无右侧更多操作，因为此时找不到新增备选项归属的数据字典，且备选项为空</span><br>' +
    '<span>2、字典存在多个时：前端无新增备选项，因为此时无法明确新增备选项归属的数据字典是哪一个</span>';
  toPopover('#optionDictTip', content2);

  var content3 =
    '<span>备选项来源为【取字典】时，不支持联动字段的值多选（此时备选项为空），如需要支持联动字段的值多选，请使用备选项来源【数据仓库】</span>';
  toPopover('#relateFieldTip', content3);

  var content4 = '<span>1、控件选项值允许为空时，可快捷清空已选项</span><br>' + '<span>2、控件多选时，可通过清空已选项快捷重新选择</span>';
  toPopover('#clearAllTip', content4);

  var content5 =
    '<span>更改真实值字段后，会清空已保存表单的已选项，可通过以下任意方式处理：</span><br>' +
    '<span>1、用户在页面上重新选择备选项</span><br><span>2、对已保存表单的数据，进行数据库刷新</span>';
  toPopover('#valueTip1,#valueTip2', content5);

  function toPopover(id, content, placement) {
    $(id).popover({
      html: true,
      placement: placement || 'bottom',
      container: 'body',
      trigger: 'hover',
      template:
        '<div class="popover" role="tooltip" style="z-index: 99999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
      content: function () {
        return content;
      }
    });
  }
};

controlConfig.initDataSourceTreeColumn = function (field) {
  var _this = this;
  this.$('#dataSourceId')
    .on('change', function () {
      var dataStoreId = _this.$('#dataSourceId').val();
      if (!dataStoreId) {
        if (field.dataSourceId) {
          _this.$('#dataSourceId').val(field.dataSourceId);
          _this.$('#dataSourceText').val(field.dataSourceText);
        }
      } else {
        var dataStoreColumnData = _this.$('#dataSourceId').data('data-' + dataStoreId);
        var $opt = [
          $('<option>', {
            value: ''
          }).text('')
        ];
        for (var i = 0, len = dataStoreColumnData.length; i < len; i++) {
          $opt.push(
            $('<option>', {
              value: dataStoreColumnData[i].id
            }).text(dataStoreColumnData[i].title)
          );
        }
        var optionHtml = '';
        $('.dataSourceColumnSelect').each(function () {
          $(this).empty();
          if (optionHtml) {
            $(this).html(optionHtml);
          } else {
            $(this).append($opt);
            optionHtml = $(this).html();
          }
        });
        $('.dataSourceColumnSelect').each(function () {
          var id = $(this).attr('id');
          if (field[id]) {
            $(this)
              .find("option[value='" + field[id] + "']")
              .prop('selected', true);
          }
        });
      }
    })
    .trigger('change');
};

controlConfig.collectFormAndFillCkeditor = function () {
  var field = new WComboTreeClass();
  field.inputMode = dyFormInputMode.treeSelect;
  field.noNullValidateReminder = $('#noNullValidateReminder').val();
  field.uniqueValidateReminder = $('#uniqueValidateReminder').val();
  field.unitUnique = $('#unitUnique').val();
  field.fieldCheckRules = [];
  //控件公共属性收集
  var checkpass = this.collectFormCtlComProperty(field, this);
  if (!checkpass) {
    return false;
  }

  field.allPath = $('input[name=allPath]:checked').val();
  field.optionDataSource = $('input[name=optionDataSource]:checked').val();

  $('.dataSourceColumnSelect').each(function () {
    var val = $(this).val();
    var id = $(this).attr('id');
    if (val) {
      field[id] = val;
    }
  });

  var mutiselect = $("input[name='mutiSelect']:checked").val();
  if (mutiselect == 'true') {
    field.mutiSelect = true;
  } else {
    field.mutiSelect = false;
  }
  field.selectParent = $('#selectParent').prop('checked');
  field.showCheckAll = $('#showCheckAll').prop('checked');
  field.expandAndCollapse = $('#expandAndCollapse').prop('checked');
  field.clearAll = $('#clearAll').prop('checked');

  field.treeWidth = $('#treeWidth').val();
  field.treeHeight = $('#treeHeight').val();
  // field.defaultCondition = $('#defaultCondition').val();
  field.defaultCondition = $('#defaultCondition').data('codeEditor').getValue();

  if (field.optionDataSource == 3) {
    field.optionDataAutoSet = $('#optionDataAutoSet2').prop('checked');
    if ($('#optionDataAutoSet2').prop('checked')) {
      field.relateField = $('#relateField2').val();
      if (field.relateField == '' || field.relateField == undefined) {
        alert('联动字段不能为空！');
        return false;
      }
    } else {
      if ($('#dictName').val() == '' || $('#dictName').val() == undefined) {
        alert('字典不能为空！');
        return false;
      }
      field.dictCode = $('#dictCode').val();
      field.dictUuid = $('#dictUuid').val();
    }

    field.showRightBtn = $('#showRightBtn')[0].checked;
    field.dicCodeAddBtn = $('#dicCodeAddBtn')[0].checked;
    field.dicCodeMoveUp = $('#dicCodeMoveUp')[0].checked;
    field.dicCodeMoveDown = $('#dicCodeMoveDown')[0].checked;
    field.dicCodeDelBtn = $('#dicCodeDelBtn')[0].checked;
    field.dictValueColumn = $('#dictValueColumn').val();
  } else if (field.optionDataSource == 2) {
    //数据仓库
    field.optionDataAutoSet = $('#optionDataAutoSet3').prop('checked');
    if ($('#optionDataAutoSet3').prop('checked')) {
      field.relateField = $('#relateField3').val();
      if (field.relateField == '' || field.relateField == undefined) {
        alert('联动字段不能为空！');
        return false;
      }
    }
    if (this.$('#dataSourceId').val() == '' || this.$('#dataSourceId').val() == undefined) {
      alert('数据仓库不能为空！');
      return false;
    }
    field.dataSourceId = this.$('#dataSourceId').val();
    field.dataSourceText = this.$('#dataSourceText').val();
  } else {
    if ($('#newServiceName').val() == '') {
      alert('数据服务不能为空！');
      return false;
    }
    field.serviceName = $('#serviceName').val();
    field.newServiceName = $('#newServiceName').val();
  }

  //创建控件占位符
  this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);

  formDefinition.addField(field.name, field);

  return true;
};

controlConfig.getInputEvents = function () {
  return [
    {
      id: 'focus',
      chkDisabled: true,
      name: 'focus'
    },
    {
      id: 'blur',
      chkDisabled: true,
      name: 'blur'
    },
    {
      id: 'change',
      chkDisabled: true,
      name: 'change'
    }
  ];
};

controlConfig.pluginName = CkPlugin.TREESELECTCTL;

addPlugin(controlConfig.pluginName, '树形下拉控件', '树形下拉控件属性设置', controlConfig);
