var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.initProperty = function (field) {
  //初始化数据字典树.
  this.initDictCode();
  var _this = this;

  $("input[name='optionDataSource']").change(function () {
    var checkvalue = $("input[name='optionDataSource']:checked").val();
    if (checkvalue == dyDataSourceType.dataConstant) {
      //常量
      $('.tr_dicCode').hide();
      $('.tr_dataSource').hide();
      $('.tr_data').show();
      $("input[name='dictCode']").val();
      $("input[name='dictName']").val();
      var checked1 = $('#optionDataAutoSet1').prop('checked');
      $('.dataAutoSet1')[checked1 ? 'show' : 'hide']();
      $('.dataAutoSet2')[checked1 ? 'hide' : 'show']();
    } else if (checkvalue == dyDataSourceType.dataSource) {
      //数据仓库
      $('.tr_dicCode').hide();
      $('.tr_data').hide();
      $('.tr_dataSource').show();
      $("input[name='dataSourceText']").val();
      $("input[name='dataSourceId']").val();
      var checked3 = $('#optionDataAutoSet3').prop('checked');
      $('.sourceAutoSet1')[checked3 ? 'show' : 'hide']();
    } else {
      //数据字典
      $('.tr_dicCode').show();
      $('.tr_dataSource').hide();
      $('.tr_data').hide();
      $('#optdata').val();
      var checked2 = $('#optionDataAutoSet2').prop('checked');
      $('.dicAutoSet1')[checked2 ? 'show' : 'hide']();
      $('.dicAutoSet2')[checked2 ? 'hide' : 'show']();
    }
  });

  $("input[name='optionDataAutoSet1']").change(function () {
    var checked = $("input[name='optionDataAutoSet1']").prop('checked');
    if (checked) {
      $('.dataAutoSet1').show();
      $('.dataAutoSet2').hide();
    } else {
      $('.dataAutoSet1').hide();
      $('.dataAutoSet2').show();
    }
  });
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

  $('#showRightBtn').change(function () {
    var checked = $(this).attr('checked');
    if (checked) {
      $('.showRightBtn').show();
    } else {
      $('.showRightBtn').hide();
    }
  });

  $('#selectCheckAll').change(function () {
    var checked = $(this).attr('checked');
    if (checked) {
      $('.maxSelectionLength').hide();
    } else {
      $('.maxSelectionLength').show();
    }
  });

  $('#reloadOption').change(function () {
    var checked = $(this).attr('checked');
    if (checked) {
      $('.reloadOptionTip').show();
    } else {
      $('.reloadOptionTip').hide();
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

  $('#dataOptsList')
    .off('click', '.plusOptsSet')
    .on('click', '.plusOptsSet', function () {
      var html =
        '<tr>' +
        '<td>' +
        '<select name="conditionOperate" id="">' +
        '<option value="eq" selected>=</option>' +
        '<option value="ne">!=</option>' +
        '<option value="lt">&lt;</option>' +
        '<option value="gt">&gt;</option>' +
        '<option value="le">&le;</option>' +
        '<option value="ge">&ge;</option>' +
        '<option value="like">包含</option>' +
        '<option value="nlike">不包含</option>' +
        '</select>' +
        '<input type="text" name="conditionContent" style="width: 180px" />' +
        '</td>' +
        '<td>' +
        '<input type="text" name="conditionValue" style="width: 180px" />' +
        '</td>' +
        '<td>' +
        '<i class="iconfont icon-ptkj-jiahao plusOptsSet"></i>' +
        '<i class="iconfont icon-ptkj-jianhao minusOptsSet"></i>' +
        '</td>' +
        '</tr>';
      $(this).parents('tr').first().after(html);
      $(this).next().show();
    });

  $('#dataOptsList')
    .off('click', '.minusOptsSet')
    .on('click', '.minusOptsSet', function () {
      $(this).parents('tr').first().remove();
      if ($('#dataOptsList tbody').find('tr').length == 1) {
        $('#dataOptsList tbody').find('.minusOptsSet').hide();
      }
    });

  if (field == null || typeof field == 'undefined') {
    field = new WSelectClass();
    field.valueCreateMethod = '1';
    field.inputMode = dyFormInputMode.select;
    field.searchable = true;
    field.clearAll = true;
  } else {
    //$("#name").attr("readonly", "readonly");//在编辑的状态下时则时字段名为只读
  }

  this.initDataSource2(field.dataSourceId, field.dataSourceText, function () {
    if (field.dataSourceId != '' && field.dataSourceId != undefined) {
      _this.$('#dataSourceId').val(field.dataSourceId);
      _this.$('#dataSourceText').val(field.dataSourceText);
      _this.$('#dataSourceFieldName').val(field.dataSourceFieldName);
      _this.$('#dataSourceDisplayName').val(field.dataSourceDisplayName);
    }

    _this.initDataSourceFieldMap(field);
  });

  //控件属性初始化公共设置.
  this.ctlPropertyComInitSet(field);

  //私有属性
  if (field.optionDataSource == dyDataSourceType.dataDictionary) {
    this.$("input[name='optionDataSource'][value='" + dyDataSourceType.dataDictionary + "']").attr('checked', true);
    this.$('.tr_dicCode').show();
    this.$('.tr_data').hide();
    this.$('.tr_dataSource').hide();
    $('#optionDataAutoSet2').prop('checked', field.optionDataAutoSet).trigger('change');
    $('#relateField2').val(field.relateField).trigger('change');
  } else if (field.optionDataSource == dyDataSourceType.dataConstant) {
    this.$("input[name='optionDataSource'][value='" + dyDataSourceType.dataConstant + "']").attr('checked', true);
    this.$('.tr_dicCode').hide();
    this.$('.tr_dataSource').hide();
    this.$('.tr_data').show();
    $('#optionDataAutoSet1').prop('checked', field.optionDataAutoSet).trigger('change');
    $('#relateField1').val(field.relateField).trigger('change');
    if (field.optionDataAutoSet) {
      var html = '';
      $.each(field.dataOptsList, function (index, item) {
        html +=
          '<tr>' +
          '<td>' +
          "<select name='conditionOperate' id=''>" +
          "<option value='eq' " +
          (item.operate == 'eq' ? 'selected' : '') +
          '>=</option>' +
          "<option value='ne' " +
          (item.operate == 'ne' ? 'selected' : '') +
          '>!=</option>' +
          "<option value='lt' " +
          (item.operate == 'lt' ? 'selected' : '') +
          '>&lt;</option>' +
          "<option value='gt' " +
          (item.operate == 'gt' ? 'selected' : '') +
          '>&gt;</option>' +
          "<option value='le' " +
          (item.operate == 'le' ? 'selected' : '') +
          '>&le;</option>' +
          "<option value='ge' " +
          (item.operate == 'ge' ? 'selected' : '') +
          '>&ge;</option>' +
          "<option value='like' " +
          (item.operate == 'like' ? 'selected' : '') +
          '>包含</option>' +
          "<option value='nlike' " +
          (item.operate == 'nlike' ? 'selected' : '') +
          '>不包含</option>' +
          '</select>' +
          "<input type='text' name='conditionContent' style='width: 180px' value='" +
          item.content +
          "'/>" +
          '</td>' +
          '<td>' +
          "<input type='text' name='conditionValue' style='width: 180px'  value='" +
          item.value +
          "'/>" +
          '</td>' +
          '<td>' +
          "<i class='iconfont icon-ptkj-jiahao plusOptsSet'></i>" +
          "<i class='iconfont icon-ptkj-jianhao minusOptsSet' style='" +
          (field.dataOptsList.length == 1 ? 'display:none' : '') +
          "'></i>" +
          '</td>' +
          '</tr>';
      });
      $('#dataOptsList tbody').html(html);
    }
  } else {
    this.$("input[name='optionDataSource'][value='" + dyDataSourceType.dataSource + "']").attr('checked', true);
    this.$('.tr_dicCode').hide();
    this.$('.tr_dataSource').show();
    this.$('.tr_data').hide();
    $('#optionDataAutoSet3').prop('checked', field.optionDataAutoSet).trigger('change');
    $('#relateField3').val(field.relateField).trigger('change');
  }

  if (field.dictCode != '' && field.dictCode != undefined) {
    this.$('#dictCode').val(field.dictCode);
    this.$('#dictUuid').val(field.dictUuid);
    var dictcodearray = field.dictCode.split(':');
    this.$('#dictName').val(dictcodearray[1]);
  }
  this.$('#showRightBtn').attr('checked', field.showRightBtn);
  if (field.showRightBtn) {
    this.$('.showRightBtn').show();
  }
  this.$('#dicCodeAddBtn').attr('checked', field.dicCodeAddBtn);
  this.$('#dicCodeMoveUp').attr('checked', field.dicCodeMoveUp);
  this.$('#dicCodeMoveDown').attr('checked', field.dicCodeMoveDown);
  this.$('#dicCodeDelBtn').attr('checked', field.dicCodeDelBtn);

  if (JSON.cStringify(field.optionSet) != '[]') {
    this.$('#optdata').val(JSON.cStringify(field.optionSet));
  }

  //add by wujx 2015-12-10
  if (field.searchable == true || typeof field.searchable == 'undefined') {
    this.$('#searchable').attr('checked', true);
  }

  if (field.clearAll == true || typeof field.clearAll == 'undefined') {
    this.$('#clearAll').attr('checked', true);
  } else {
    this.$('#clearAll').attr('checked', false);
  }

  if (field.selectMultiple) {
    this.$('#selectMultiple').attr('checked', true);
  }

  if (field.selectCheckAll) {
    this.$('#selectCheckAll').attr('checked', true);
    this.$('.maxSelectionLength').hide();
  }

  if (field.maxSelectionLength) {
    this.$('#maxSelectionLength').val(field.maxSelectionLength);
  }

  if (field.reloadOption) {
    this.$('#reloadOption').attr('checked', true);
    $('.reloadOptionTip').show();
  }

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

  if (field.defaultCondition) {
    editor.setValue(field.defaultCondition);
  }
  $('#defaultCondition').data('codeEditor', editor);

  $('#unitUnique').val(field.unitUnique);
  if (field.unitUnique == 'true') {
    $('#checkRule_6').attr('checked', true);
    $('#checkRule_5').attr('checked', false);
  } else if (field.unitUnique == 'false') {
    $('#checkRule_5').attr('checked', true);
    $('#checkRule_6').attr('checked', false);
  }

  $('#relateField1').wellSelect({
    data: relateFieldData,
    valueField: 'relateField1'
  });
  $('#relateField2').wellSelect({
    data: relateFieldData,
    valueField: 'relateField2'
  });
  $('#relateField3').wellSelect({
    data: relateFieldData,
    multiple: true,
    valueField: 'relateField3'
  });
  $('#dataOptionTip').popover({
    html: true,
    placement: 'bottom',
    container: 'body',
    trigger: 'hover',
    template:
      '<div class="popover" role="tooltip" style="z-index: 99999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
    content: function () {
      return (
        '<span>格式示例：</span><br>' +
        '<span>非数组的格式：{"1":"笔记本","2":"台式"}</span><br>' +
        '<span>数组的格式：[{value:"1",name:"笔记本"},{value:"2",name:"台式"}]</span>'
      );
    }
  });
  $('#dataOptionTip1').popover({
    html: true,
    placement: 'bottom',
    container: 'body',
    trigger: 'hover',
    template:
      '<div class="popover" role="tooltip" style="z-index: 99999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
    content: function () {
      return (
        '<span>根据【联动字段的值】满足的动态条件，来获取条件对应的备选项</span><br>' +
        '<span>1、有满足的条件（≥1个条件）：备选项即为这些条件对应的备选项（去除重复的选项）</span><br>' +
        '<span>2、无满足的条件：备选项为空</span>'
      );
    }
  });
  $('#optionDataSourceTip').popover({
    html: true,
    placement: 'bottom',
    container: 'body',
    trigger: 'hover',
    template:
      '<div class="popover" role="tooltip" style="z-index: 99999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
    content: function () {
      return (
        '<span>当备选项来源选择【取字典】时，请确保字典已按以下内容正确配置，否则将导致字段备选项为空，因为备选项是获取字典下的字典列表内容：</span><br>' +
        '<span>1、数据字典中存在相应的字典</span><br>' +
        '<span>2、字典下的字典列表不能为空</span>'
      );
    }
  });
  $('#optionDictTip').popover({
    html: true,
    placement: 'bottom',
    container: 'body',
    trigger: 'hover',
    template:
      '<div class="popover" role="tooltip" style="z-index: 99999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
    content: function () {
      return (
        '<span>存在前端显示与配置不一致的2个场景：</span><br>' +
        '<span>1、字典不存在时：前端无右侧更多操作，因为此时找不到新增备选项归属的数据字典，且备选项为空</span><br>' +
        '<span>2、字典存在多个时：前端无新增备选项，因为此时无法明确新增备选项归属的数据字典是哪一个</span>'
      );
    }
  });
  $('#relateFieldTip').popover({
    html: true,
    placement: 'right',
    container: 'body',
    trigger: 'hover',
    template:
      '<div class="popover" role="tooltip" style="z-index: 99999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
    content: function () {
      return '<span>备选项来源为【取字典】时，不支持联动字段的值多选（此时备选项为空），如需要支持联动字段的值多选，请使用备选项来源【数据仓库】</span>';
    }
  });
  $('#clearAllTip').popover({
    html: true,
    placement: 'bottom',
    container: 'body',
    trigger: 'hover',
    template:
      '<div class="popover" role="tooltip" style="z-index: 99999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
    content: function () {
      return '<span>1、控件选项值允许为空时，可快捷清空已选项</span><br>' + '<span>2、控件多选时，可通过清空已选项快捷重新选择</span>';
    }
  });
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
            return [
              {
                id: '',
                text: ''
              }
            ].concat(dataSourceColumnData);
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
  var field = new WSelectClass();
  field.inputMode = dyFormInputMode.select;
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
    field.optionDataAutoSet = $('#optionDataAutoSet1').prop('checked');
    if ($('#optionDataAutoSet1').prop('checked')) {
      field.relateField = $('#relateField1').val();
      if (field.relateField == '' || field.relateField == undefined) {
        alert('联动字段不能为空！');
        return false;
      }
      var trs = $('#dataOptsList tbody').find('tr');
      var dataOptsList = [];
      var isNull = false;
      for (var i = 0; i < trs.length; i++) {
        var operate = $(trs[i]).find("select[name='conditionOperate']").val();
        var content = $(trs[i]).find("input[name='conditionContent']").val();
        var value = $(trs[i]).find("input[name='conditionValue']").val();
        if (content == '' || value == '') {
          alert('备选项不能为空');
          isNull = true;
          break;
        }

        dataOptsList.push({
          operate: operate,
          content: content,
          value: value
        });
      }
      if (isNull) {
        return false;
      }
      field.dataOptsList = dataOptsList;
    } else {
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
    }

    // add by wujx 20160711 end
  } else if (field.optionDataSource == dyDataSourceType.dataSource) {
    field.optionDataAutoSet = $('#optionDataAutoSet3').prop('checked');
    if ($('#optionDataAutoSet3').prop('checked')) {
      field.relateField = $('#relateField3').val();
      if (field.relateField == '' || field.relateField == undefined) {
        alert('联动字段不能为空！');
        return false;
      }
    }
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
    // field.defaultCondition = this.$('#defaultCondition').val();

    field.defaultCondition = $('#defaultCondition').data('codeEditor').getValue();

    field.reloadOption = $('#reloadOption')[0].checked;
    if (field.reloadOption && !field.defaultCondition) {
      appModal.alert('默认条件不能为空');
      return false;
    }
  } else {
    //数据字典
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
  }

  //add by wujx 2015-12-10

  field.searchable = $('#searchable')[0].checked;
  field.clearAll = $('#clearAll').prop('checked');

  field.selectMultiple = $('#selectMultiple')[0].checked;

  field.selectCheckAll = $('#selectCheckAll')[0].checked;

  field.maxSelectionLength = $('#maxSelectionLength').val();

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

controlConfig.pluginName = CkPlugin.SELECTCTL;
addPlugin(controlConfig.pluginName, '下拉框', '下拉框控件属性设置', controlConfig);
