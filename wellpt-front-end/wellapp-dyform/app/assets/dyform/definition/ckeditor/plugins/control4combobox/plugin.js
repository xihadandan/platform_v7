var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.initProperty = function (field) {
  var _this = this;

  //初始化数据字典树.
  this.initDictCode();

  $("input[name='optionDataSource']").change(function () {
    var checkvalue = $("input[name='optionDataSource']:checked").val();
    if (checkvalue == dyDataSourceType.dataConstant) {
      //常量
      $('#tr_dicCode').hide();
      $('.tr_dataSource').hide();
      $('#tr_data').show();
      $("input[name='dictCode']").val();
      $("input[name='dictName']").val();
    } else if (checkvalue == dyDataSourceType.dataSource) {
      //数据仓库
      $('#tr_dicCode').hide();
      $('#tr_data').hide();
      $('.tr_dataSource').show();
      $("input[name='dataSourceText']").val();
      $("input[name='dataSourceId']").val();
    } else {
      //数据字典
      $('#tr_dicCode').show();
      $('.tr_dataSource').hide();
      $('#tr_data').hide();
      $('#optdata').val();
    }
  });

  if (field == null || typeof field == 'undefined') {
    field = new WComboBoxClass();
    field.valueCreateMethod = '1';
    field.inputMode = dyFormInputMode.selectMutilFase;
    field.searchable = true;
  } else {
    //$("#name").attr("readonly", "readonly");//在编辑的状态下时则时字段名为只读
  }

  this.initDataSource2(field.dataSourceId, field.dataSourceText, function () {
    if (field.dataSourceId != '' && field.dataSourceId != undefined) {
      _this.$('#dataSourceId').val(field.dataSourceId);
      _this.$('#dataSourceText').val(field.dataSourceText);
      _this.$('#dataSourceFieldName').val(field.dataSourceFieldName);
      _this.$('#dataSourceDisplayName').val(field.dataSourceDisplayName);
      _this.$('#defaultCondition').val(field.defaultCondition);
    }
    _this.initDataSourceFieldMap(field);

    if (field.dataSourceId != '' && field.dataSourceId != undefined) {
      _this.$('#dataSourceId').val(field.dataSourceId);
      _this.$('#dataSourceText').val(field.dataSourceText);
      _this.$('#dataSourceFieldName').val(field.dataSourceFieldName);
      _this.$('#dataSourceDisplayName').val(field.dataSourceDisplayName);
      _this.$('#defaultCondition').val(field.defaultCondition);
    }
  });

  //控件属性初始化公共设置.
  this.ctlPropertyComInitSet(field);

  //私有属性
  if (field.optionDataSource == dyDataSourceType.dataDictionary) {
    this.$("input[name='optionDataSource'][value='" + dyDataSourceType.dataDictionary + "']").attr('checked', true);
    this.$('#tr_dicCode').show();
    this.$('#tr_data').hide();
    this.$('.tr_dataSource').hide();
  } else if (field.optionDataSource == dyDataSourceType.dataConstant) {
    this.$("input[name='optionDataSource'][value='" + dyDataSourceType.dataConstant + "']").attr('checked', true);
    this.$('#tr_dicCode').hide();
    this.$('.tr_dataSource').hide();
    this.$('#tr_data').show();
  } else {
    this.$("input[name='optionDataSource'][value='" + dyDataSourceType.dataSource + "']").attr('checked', true);
    this.$('#tr_dicCode').hide();
    this.$('.tr_dataSource').show();
    this.$('#tr_data').hide();
  }

  if (field.dictCode != '' && field.dictCode != undefined) {
    this.$('#dictCode').val(field.dictCode);
    var dictcodearray = field.dictCode.split(':');
    this.$('#dictName').val(dictcodearray[1]);
  }

  if (JSON.cStringify(field.optionSet) != '[]') {
    this.$('#optdata').val(JSON.cStringify(field.optionSet));
  }

  //add by wujx 2015-12-10
  if (field.searchable == true || typeof field.searchable == 'undefined') {
    this.$('#searchable').attr('checked', true);
  }

  if (field.selectMultiple) {
    this.$('#selectMultiple').attr('checked', true);
  }

  if (field.selectCheckAll) {
    this.$('#selectCheckAll').attr('checked', true);
  }

  $('#unitUnique').val(field.unitUnique);
  if (field.unitUnique == 'true') {
    $('#checkRule_6').attr('checked', true);
    $('#checkRule_5').attr('checked', false);
  } else if (field.unitUnique == 'false') {
    $('#checkRule_5').attr('checked', true);
    $('#checkRule_6').attr('checked', false);
  }
};

controlConfig.initDataSourceFieldMap = function (field) {
  var _self = this;
  // 默认排序
  var $dsFieldMapTable = $('#table_ds_field_map');
  // 定义添加，删除，上移，下移4按钮事件
  var fieldMapData = {
    dataSourceColumn: null,
    formField: null,
    formater: ''
  };
  formBuilder.bootstrapTable.initTableTopButtonToolbar('table_ds_field_map', 'ds_field_map', $('body'), fieldMapData);

  // 列定义
  $dsFieldMapTable.bootstrapTable('destroy').bootstrapTable({
    data: field && field.dataSourceFieldMapping ? field.dataSourceFieldMapping : [],
    idField: 'uuid',
    striped: true,
    showColumns: false,
    toolbar: $('#div_ds_field_map_info_toolbar', $('body')),
    onEditableHidden: function (field, row, $el, reason) {
      $el.closest('table').bootstrapTable('resetView');
    },
    width: 500,
    height: 150,
    columns: [
      {
        field: 'checked',
        formatter: true,
        checkbox: true
      },
      {
        field: 'uuid',
        title: 'UUID',
        visible: false
      },
      {
        field: 'dataSourceColumn',
        title: '数据仓库字段',
        width: 100,
        editable: {
          type: 'select',
          mode: 'inline',
          showbuttons: false,
          onblur: 'submit',
          emptytext: '请选择',
          source: function () {
            var dataStoreKey = 'data-' + _self.$('#dataSourceId').val();
            var dataSourceColumnData = _self.$('#dataSourceId').data(dataStoreKey);
            return [{ id: '', text: '' }].concat(dataSourceColumnData);
          }
        }
      },
      {
        field: 'formField',
        title: '表单字段',
        width: 100,
        editable: {
          type: 'select',
          mode: 'inline',
          showbuttons: false,
          onblur: 'submit',
          emptytext: '请选择',
          source: function () {
            var formFieldOptions = [];
            for (var f in formDefinition.fields) {
              formFieldOptions.push({
                value: f,
                text: formDefinition.getField(f).displayName,
                title: formDefinition.getField(f).displayName,
                id: f
              });
            }
            return formFieldOptions;
          }
        }
      },
      {
        field: 'formater',
        title: '格式化',
        width: 100,
        editable: {
          type: 'text',
          mode: 'inline',
          showbuttons: false,
          onblur: 'submit',
          savenochange: true
        }
      }
    ]
  });

  $dsFieldMapTable.bootstrapTable('resetWidth');
};

controlConfig.collectFormAndFillCkeditor = function () {
  var field = new WComboBoxClass();
  field.inputMode = dyFormInputMode.selectMutilFase;
  field.fieldCheckRules = [];

  field.optionDataSource = $("input[name='optionDataSource']:checked").val();
  //added by linxr
  field.noNullValidateReminder = $('#noNullValidateReminder').val();
  field.uniqueValidateReminder = $('#uniqueValidateReminder').val();
  field.unitUnique = $('#unitUnique').val();
  //end
  //控件公共属性收集
  var checkpass = this.collectFormCtlComProperty(field);
  if (!checkpass) {
    return false;
  }
  if (field.optionDataSource == dyDataSourceType.dataConstant) {
    //常量
    if (this.$('#optdata').val() == '' || this.$('#optdata').val() == undefined) {
      alert('备选项设置不能为空！');
      return false;
    }
    // add by wujx 20160711 begin
    field.optionSet = this.evalJSON(this.$('#optdata').val()); //eval("("+this.$("#optdata").val()+")");
    try {
      // modify by wujx 20160803 begin
      if (field.optionSet instanceof Array) {
        var optionSetArray = field.optionSet;
        if (optionSetArray.length == 0) {
          alert('备选项设置不能为空！');
          return false;
        }
        for (var i in optionSetArray) {
          var optionSet = optionSetArray[i];
          if (!(optionSet.hasOwnProperty('name') && optionSet.hasOwnProperty('value'))) {
            throw new error('格式有误');
          }
        }
      }
      // modify by wujx 20160803 end
    } catch (e) {
      alert(
        '备选项设置值格式有误，必须符合规范，例如：{"1":"笔记本","2":"台式"}（非数组）或[{value:"1", name:"笔记本"},{value:"2", name:"台式"}]（数组）！'
      );
      return false;
    }
    // add by wujx 20160711 end
  } else if (field.optionDataSource == dyDataSourceType.dataSource) {
    //数据仓库
    if (this.$('#dataSourceId').val() == '' || this.$('#dataSourceId').val() == undefined) {
      alert('数据仓库不能为空！');
      return false;
    }
    if (this.$('#dataSourceFieldName').val() == '' || this.$('#dataSourceFieldName').val() == undefined) {
      alert('真实值不能为空！');
      return false;
    }
    if (this.$('#dataSourceDisplayName').val() == '' || this.$('#dataSourceDisplayName').val() == undefined) {
      alert('显示值不能为空！');
      return false;
    }
    field.dataSourceId = this.$('#dataSourceId').val();
    field.dataSourceText = this.$('#dataSourceText').val();
    field.dataSourceFieldName = this.$('#dataSourceFieldName').val();
    field.dataSourceDisplayName = this.$('#dataSourceDisplayName').val();

    //数据仓库与表单字段的映射
    field.dataSourceFieldMapping = this.$('#table_ds_field_map').bootstrapTable('getData');
    field.defaultCondition = this.$('#defaultCondition').val();
  } else {
    //数据字段
    if ($('#dictName').val() == '' || $('#dictName').val() == undefined) {
      alert('字典不能为空！');
      return false;
    }
    field.dictCode = $('#dictCode').val();
  }

  //add by wujx 2015-12-10
  if ($('#searchable')[0].checked) {
    field.searchable = true;
  } else {
    field.searchable = false;
  }

  if ($('#selectMultiple').attr('checked')) {
    field.selectMultiple = true;
  } else {
    field.selectMultiple = false;
  }

  if ($('#selectCheckAll').attr('checked')) {
    field.selectCheckAll = true;
  } else {
    field.selectCheckAll = false;
  }

  //创建控件占位符
  this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);
  //formDefinition.fields[field.name] = field;
  formDefinition.addField(field.name, field);
  return true;
};

controlConfig.getInputEvents = function () {
  return [
    {
      id: 'change',
      chkDisabled: true,
      name: 'change'
    }
  ];
};

if ($.fn.placeholder.input && $.fn.placeholder.textarea) {
} else {
  $('input, textarea').placeholder();
}

controlConfig.pluginName = CkPlugin.COMBOBOXCTL;
addPlugin(controlConfig.pluginName, '下拉框', '下拉框控件属性设置', controlConfig);
