var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.initProperty = function (field) {
  var _this = this;

  //初始化数据字典树.
  this.initDictCode();

  $("input[name='selectMode']").change(function () {
    var checkvalue = $("input[name='optionDataSource']:checked").val();
    if (checkvalue == '1') {
      $('.tr_dicCode').hide();
      $('.tr_data').show();
      $('.tr_dataSource').hide();
      $("input[name='dictCode']").val();
      $("input[name='dictName']").val();
      $("input[name='dataSourceText']").val();
      $("input[name='dataSourceId']").val();
    } else if (checkvalue == '2') {
      $('.tr_dicCode').show();
      $('.tr_data').hide();
      $('.tr_dataSource').hide();
      $("input[name='dataSourceText']").val();
      $("input[name='dataSourceId']").val();
      $('#optdata').val();
    } else {
      $('.tr_dicCode').hide();
      $('.tr_data').hide();
      $('.tr_dataSource').show();
      $("input[name='dictCode']").val();
      $("input[name='dictName']").val();
      $('#optdata').val();
    }

    var selectmode = $("input[name='selectMode']:checked").val();
    if (selectmode == '1') {
      $('#tr_optionDataSource').hide();
      $('#tr_singleChecksrc').show();
      $('#tr_ctrField').show();
      $('.tr_data').hide();
      $('.tr_dicCode').hide();
      $('.tr_dataSource').hide();
      $('#tr_all').hide();
      $('#tr_number').hide();
      $('#tr_layout').hide();
    } else {
      $('#tr_optionDataSource').show();
      $('#tr_singleChecksrc').hide();
      $('#tr_ctrField').hide();
      $('#tr_all').show();
      $('#tr_number').show();
      $('#tr_layout').show();
    }
  });

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

  $("input[name='selectDirection']").change(function () {
    var alignValue = $("input[name='selectDirection']:checked").val();
    if (alignValue == '0') {
      $('#tr_align').show();
    } else {
      $('#tr_align').hide();
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

  if (field == null || typeof field == undefined) {
    field = new WCheckBoxClass();
    field.inputMode = dyFormInputMode.checkbox;
    field.valueCreateMethod = '1';
    field.selectMode = '2';
  }

  this.initDataSource2(field.dataSourceId, null, function () {
    _this.$('#dataSourceId').val(field.dataSourceId);
    _this.$('#dataSourceText').val(field.dataSourceText);
    _this.$('#dataSourceFieldName').val(field.dataSourceFieldName);
    _this.$('#dataSourceDisplayName').val(field.dataSourceDisplayName);
  });

  //控件属性初始化公共设置.
  _this.ctlPropertyComInitSet(field);

  //私有属性
  $('#singleCheck').val(field.singleCheckContent);
  $('#singleUnCheck').val(field.singleUnCheckContent);

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

  if (field.selectMode == '2') {
    $("input[name='selectMode'][value='" + 2 + "']").attr('checked', true);
    $('#tr_optionDataSource').show();
    $('#tr_singleChecksrc').hide();
    $('#tr_ctrField').hide();
    $('#tr_all').show();
    $('#tr_number').show();
  } else {
    $("input[name='selectMode'][value='" + 1 + "']").attr('checked', true);
    $('#tr_optionDataSource').hide();
    $('#tr_singleChecksrc').show();
    $('#tr_ctrField').show();
    $('.tr_dicCode').hide();
    $('.tr_data').hide();
    $('#tr_all').hide();
    $('#tr_number').hide();
  }

  if (field.selectDirection == '0') {
    $('#tr_align').show();
  } else {
    $('#tr_align').hide();
  }
  $('#unitUnique').val(field.unitUnique);
  if (field.unitUnique == 'true') {
    $('#checkRule_6').attr('checked', true);
    $('#checkRule_5').attr('checked', false);
  } else if (field.unitUnique == 'false') {
    $('#checkRule_5').attr('checked', true);
    $('#checkRule_6').attr('checked', false);
  }
  //$("#checkboxAttrCfgDiv_ctrlField").val(field.ctrlField);
  // $("#checkboxAttrCfgDiv_ctrlFieldId").val(field.ctrlField.split(",")[0]);
  // $("#checkboxAttrCfgDiv_ctrlFieldName").val(field.ctrlField.split(",")[1]);
  if (field.dictCode != '' && field.dictCode != 'undefined') {
    $('#dictCode').val(field.dictCode);
    var dictcodearray = field.dictCode.split(':');
    $('#dictName').val(dictcodearray[1]);
  }

  if (field.dataSourceId != '' && field.dataSourceId != undefined) {
    this.$('#dataSourceId').val(field.dataSourceId);
    this.$('#dataSourceText').val(field.dataSourceText);
  }

  getCheckboxVal(field, 'selectDirection');
  getCheckboxVal(field, 'checkboxAll');
  getCheckboxVal(field, 'selectAlign');
  this.$('#checkboxMin').val(field.checkboxMin);
  this.$('#checkboxMax').val(field.checkboxMax);

  if (JSON.cStringify(field.optionSet) != '[]') {
    $('#optdata').val(JSON.cStringify(field.optionSet));
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
};

function getCheckboxVal(field, name) {
  if (field[name] == '0') {
    $("input[name='" + name + "'][value='" + 0 + "']").attr('checked', true);
  } else {
    $("input[name='" + name + "'][value='" + 1 + "']").attr('checked', true);
  }
}

controlConfig.collectFormAndFillCkeditor = function () {
  var field = new WCheckBoxClass();
  //field.ctrlField=$("#checkboxAttrCfgDiv_ctrlField").val();
  field.inputMode = dyFormInputMode.checkbox;
  field.fieldCheckRules = [];

  field.optionDataSource = $("input[name='optionDataSource']:checked").val();
  field.selectMode = $("input[name='selectMode']:checked").val();

  field.singleCheckContent = $('#singleCheck').val();
  field.singleUnCheckContent = $('#singleUnCheck').val();
  //added by linxr
  field.noNullValidateReminder = $('#noNullValidateReminder').val();
  field.uniqueValidateReminder = $('#uniqueValidateReminder').val();
  field.checkboxStatus = $("input[name='checkboxStatus']:checked").val();
  field.selectDirection = $("input[name='selectDirection']:checked").val();
  field.selectAlign = $("input[name='selectAlign']:checked").val();
  field.checkboxShape = $("input[name='checkboxShape']:checked").val();
  field.checkboxAll = $("input[name='checkboxAll']:checked").val();
  field.checkboxMin = $('#checkboxMin').val();
  field.checkboxMax = $('#checkboxMax').val();
  field.unitUnique = $('#unitUnique').val();
  //end
  //控件公共属性收集
  var checkpass = this.collectFormCtlComProperty(field);
  if (!checkpass) {
    return false;
  }
  if (field.selectMode == '2') {
    if (field.optionDataSource == dyDataSourceType.dataConstant) {
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
        if ($('#optdata').val() == '' || $('#optdata').val() == undefined) {
          alert('备选项设置不能为空！');
          return false;
        }
        field.optionSet = this.evalJSON(this.$('#optdata').val()); //eval("("+this.$("#optdata").val()+")");
      }
    } else if (field.optionDataSource == dyDataSourceType.dataSource) {
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
    } else {
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
        //编码跟name一样，收集时取name为了支持手录.
        field.dictCode = $('#dictCode').val();
      }
    }

    if (
      $('#checkboxMin').val() != '' &&
      $('#checkboxMax').val() != '' &&
      parseInt($('#checkboxMin').val()) - parseInt($('#checkboxMax').val()) > 0
    ) {
      alert('最小值不能大于最大值！');
      return false;
    }

    //多选的清空单选内容
    field.singleCheckContent = '';
    field.singleUnCheckContent = '';
  } else {
    if ($('#singleCheck').val() == '' || $('#singleCheck').val() == undefined) {
      alert('[单选设置/选中]不能为空！');
      return false;
    }
    try {
      eval('(' + $('#singleCheck').val() + ')');
    } catch (e) {
      alert('[单选设置/选中]不符合格式要求,无效设置！');
      return false;
    }
    if ($('#singleUnCheck').val() == '' || $('#singleUnCheck').val() == undefined) {
      alert('[单选设置/取消]不能为空！');
      return false;
    }
    try {
      eval('(' + $('#singleUnCheck').val() + ')');
    } catch (e) {
      alert('[单选设置/取消]不符合格式要求,无效设置！');
      return false;
    }
    //单选的清空多选内容

    field.optionSet = $.extend({}, this.evalJSON(this.$('#singleCheck').val()), this.evalJSON(this.$('#singleUnCheck').val()));
    field.dictCode = '';
    field.checkboxMin = '';
    field.checkboxMax = '';
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

controlConfig.pluginName = CkPlugin.CHECKBOXCTL;
addPlugin(controlConfig.pluginName, '复选框', '复选框控件属性设置', controlConfig);
