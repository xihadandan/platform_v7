var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.initProperty = function (field) {
  var _this = this;

  //初始化数据字典树.
  this.initDictCode();

  function initColor($container, id, defaultValue, opacity, position, opacityValue, changeCallback) {
    //调色盘
    $('#' + id, $container).minicolors({
      control: 'hue',
      format: 'hex',
      letterCase: 'lowercase',
      opacity: false,
      position: 'top left',
      theme: 'bootstrap',
      change: function (value, opacity) {
        var _this = $('#' + id, $container);
        _this.data('rgbObject', _this.minicolors('rgbObject'));
        _this.data('rgbaString', _this.minicolors('rgbaString'));

        if ($.isFunction(changeCallback)) {
          changeCallback(id, value);
        }
      }
    });
    if (defaultValue != null) {
      $('#' + id, $container).minicolors('value', defaultValue); //设置值触发change事件
    }

    if (opacity === true) {
      //设置透明度
      if (opacityValue != undefined) {
        $('#' + id, $container).minicolors('opacity', parseFloat(opacityValue));
      }
    }
  }

  function initColorEvent(ele, parentEle, removeEle, colorsStr, num) {
    $(ele, parentEle).on('click', function (e, colors) {
      var _id = 'color_' + num++;
      colors = colors || colorsStr;
      var $input = $('<input>', {
        type: 'text',
        class: 'addColors',
        id: _id,
        name: 'color',
        style: 'width:120px'
      });
      var $div = $("<div class='left'>").append($input);
      $div.insertBefore($(this));
      initColor($(parentEle + ' .value'), 'color_' + (num - 1), colors, true, 'top left', '0.5');
      $input.focus();
    });

    $(removeEle, parentEle).on('click', function () {
      $(parentEle + ' .value .left:last').remove();
    });
  }

  var fontColorNum = 0;
  var bgColorNum = 0;
  var borderColorNum = 0;

  initColorEvent('#addFontColor', '#tr_color', '#removeFontColor', '#666', fontColorNum);
  initColorEvent('#addBgColor', '#tr_bg', '#removeBgColor', '#f6f6f6', bgColorNum);
  initColorEvent('#addBorderColor', '#tr_border', '#removeBorderColor', '#ddd', borderColorNum);
  $("input[name='showType']").on('change', function () {
    var checkvalue = $("input[name='optionDataSource']:checked").val();
    if (checkvalue == '1') {
      $('.tr_dicCode').hide();
      $('.tr_data').show();
      $('.tr_dataSource').hide();
      $("input[name='dictCode']").val();
      $("input[name='dictName']").val();
      var checked1 = $('#optionDataAutoSet1').prop('checked');
      $('.dataAutoSet1')[checked1 ? 'show' : 'hide']();
      $('.dataAutoSet2')[checked1 ? 'hide' : 'show']();
    } else if (checkvalue == '2') {
      $('.tr_dicCode').show();
      $('.tr_data').hide();
      $('.tr_dataSource').hide();
      $('#optdata').val();
      var checked2 = $('#optionDataAutoSet2').prop('checked');
      $('.dicAutoSet1')[checked2 ? 'show' : 'hide']();
      $('.dicAutoSet2')[checked2 ? 'hide' : 'show']();
    } else if (checkvalue == '4') {
      $('.tr_dicCode').hide();
      $('.tr_data').hide();
      $('.tr_dataSource').show();
      var checked3 = $('#optionDataAutoSet3').prop('checked');
      $('.sourceAutoSet1')[checked3 ? 'show' : 'hide']();
    }

    var editmode = $("input[name='showType']:checked").val();
    if (editmode == '2') {
      $('#tr_optionDataSource').show();
    } else if (editmode == '1') {
      $('#tr_optionDataSource').hide();
      $('.tr_data').hide();
      $('.tr_dicCode').hide();
      $('.tr_dataSource').hide();
    }
  });

  $("input[name='selectMode']").on('change', function () {
    // 选择模式
    var selectmode = $("input[name='selectMode']:checked").val();
    if (selectmode == '1') {
      $('#tr_selectMore').hide();
    } else {
      $('#tr_selectMore').show();
    }
  });

  $("input[name='optionDataSource']").on('change', function () {
    // 数据来源
    var checkvalue = $("input[name='optionDataSource']:checked").val();
    if (checkvalue == dyDataSourceType.dataConstant) {
      //常量
      $('.tr_dicCode').hide();
      $('.tr_dataSource').hide();
      $('.tr_data').show();
      $('#optdata').val();
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
      $("input[name='dictCode']").val();
      $("input[name='dictName']").val();
      var checked2 = $('#optionDataAutoSet2').prop('checked');
      $('.dicAutoSet1')[checked2 ? 'show' : 'hide']();
      $('.dicAutoSet2')[checked2 ? 'hide' : 'show']();
    }
  });

  $("input[name='tagColor']").on('change', function () {
    // 颜色选择
    var tagColor = $("input[name='tagColor']:checked").val();
    if (tagColor == '1') {
      $('#tr_color').hide();
      $('#tr_bg').hide();
      $('#tr_border').hide();
    } else {
      $('#tr_color').show();
      $('#tr_bg').show();
      $('#tr_border').show();
    }
  });

  $("input[name='optionDataAutoSet1']").on('change', function () {
    var checked = $("input[name='optionDataAutoSet1']").prop('checked');
    if (checked) {
      $('.dataAutoSet1').show();
      $('.dataAutoSet2').hide();
    } else {
      $('.dataAutoSet1').hide();
      $('.dataAutoSet2').show();
    }
  });
  $("input[name='optionDataAutoSet2']").on('change', function () {
    var checked = $("input[name='optionDataAutoSet2']").prop('checked');
    if (checked) {
      $('.dicAutoSet1').show();
      $('.dicAutoSet2').hide();
    } else {
      $('.dicAutoSet1').hide();
      $('.dicAutoSet2').show();
    }
  });
  $("input[name='optionDataAutoSet3']").on('change', function () {
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
    field = new WTagGroupClass();
    field.inputMode = dyFormInputMode.taggroup;
    field.valueCreateMethod = '1';
    field.selectMode = '2';
  }
  if (field.tagEditable === '1') {
    field.showType = '2';
  } else if (field.tagEditable === '2') {
    field.showType = '1';
  }
  delete field.tagEditable;
  this.initDataSource2(field.dataSourceId, field.dataSourceText, function () {
    //控件属性初始化公共设置.
    if (field.dataSourceId != '' && field.dataSourceId != undefined) {
      _this.$('#dataSourceId').val(field.dataSourceId);
      _this.$('#dataSourceText').val(field.dataSourceText);
      _this.$('#dataSourceFieldName').val(field.dataSourceFieldName);
      _this.$('#dataSourceDisplayName').val(field.dataSourceDisplayName);
    }
  });
  _this.ctlPropertyComInitSet(field);

  //私有属性
  if (field.optionDataSource == dyDataSourceType.dataDictionary) {
    this.$("input[name='optionDataSource'][value='" + dyDataSourceType.dataDictionary + "']")
      .attr('checked', true)
      .trigger('change');
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
    $("input[name='selectMode'][value='" + 2 + "']")
      .attr('checked', true)
      .trigger('change');
    $('#tr_selectMore').show();
    $('#select_min').val(field.selectMinContent);
    $('#select_max').val(field.selectMaxContent);
  } else {
    $("input[name='selectMode'][value='" + 1 + "']")
      .attr('checked', true)
      .trigger('change');
    $('#tr_selectMore').hide();
  }

  if (field.tagColor == '1') {
    $("input[name='tagColor'][value='" + 1 + "']").attr('checked', true);
    $('#tr_color').hide();
    $('#tr_bg').hide();
    $('#tr_border').hide();
  } else {
    $("input[name='tagColor'][value='" + 2 + "']").attr('checked', true);
    $('#tr_color').show();
    $('#tr_bg').show();
    $('#tr_border').show();

    triggerColor(field.tagFontColor, '#addFontColor', '#tr_color');
    triggerColor(field.tagBgColor, '#addBgColor', '#tr_bg');
    triggerColor(field.tagBorderColor, '#addBorderColor', '#tr_border');
  }

  function triggerColor(colors, ele, parentEle) {
    if (colors && colors.length > 0) {
      for (var i = 0, len = colors.length; i < len; i++) {
        (function (index) {
          $(ele, parentEle).trigger('click', colors[index]);
        })(i);
      }
    }
  }

  if (field.tagShape == '1') {
    // 标签形状
    $("input[name='tagShape'][value='" + 1 + "']").attr('checked', true);
  } else {
    $("input[name='tagShape'][value='" + 2 + "']").attr('checked', true);
  }

  if (field.showType == '2') {
    // 是否可编辑
    // $("input[name='tagEditable'][value='" + 1 + "']").attr('checked', true);
    $('#tr_optionDataSource').show();
  } else if (field.showType == '1') {
    // $("input[name='tagEditable'][value='" + 2 + "']").attr('checked', true);
    $('#tr_optionDataSource').hide();
    $('.tr_data').hide();
    $('.tr_dicCode').hide();
    $('.tr_dataSource').hide();
  }

  if (field.dictCode != '' && field.dictCode != 'undefined') {
    $('#dictCode').val(field.dictCode);
    var dictcodearray = field.dictCode.split(':');
    $('#dictName').val(dictcodearray[1]);
  }

  if (field.dataSourceId != '' && field.dataSourceId != undefined) {
    this.$('#dataSourceId').val(field.dataSourceId);
    this.$('#dataSourceText').val(field.dataSourceText);
  }

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

controlConfig.exitDialog = function () {
  this.editor.focusedDom = null;
};

controlConfig.collectFormAndFillCkeditor = function () {
  var field = new WTagGroupClass();
  field.inputMode = dyFormInputMode.taggroup;
  field.fieldCheckRules = [];

  field.optionDataSource = $("input[name='optionDataSource']:checked").val();
  field.selectMode = $("input[name='selectMode']:checked").val();

  field.selectMinContent = $('#select_min').val();
  field.selectMaxContent = $('#select_max').val();

  field.tagColor = $("input[name='tagColor']:checked").val();
  field.tagShape = $("input[name='tagShape']:checked").val();
  // field.tagEditable = $("input[name='tagEditable']:checked").val();

  if (field.tagColor == '2') {
    setColor('#tr_color', '.addColors', 'tagFontColor');
    setColor('#tr_bg', '.addColors', 'tagBgColor');
    setColor('#tr_border', '.addColors', 'tagBorderColor');
  }

  function setColor(ele, selectEle, fields) {
    if ($(ele).find(selectEle).length > 0) {
      var tagColor = $(ele).find(selectEle);
      field[fields] = [];
      tagColor.each(function () {
        if ($(this).val() != '') {
          field[fields].push($(this).val());
        }
      });
    }
  }

  field.noNullValidateReminder = $('#noNullValidateReminder').val();
  //end
  //控件公共属性收集
  var checkpass = this.collectFormCtlComProperty(field);

  if (!checkpass) {
    return false;
  }
  if (field.showType == '2') {
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
  }

  if (field.selectMode == '2') {
    if (parseInt($('#select_max').val()) - parseInt($('#select_min').val()) < 0) {
      alert('最大值不能小于最小值！');
      return false;
    }
  } else {
    //单选的清空多选内容
    field.selectMinContent = '';
    field.selectMaxContent = '';
  }

  //创建控件占位符
  this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);
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

controlConfig.pluginName = CkPlugin.TAGGROUP;
addPlugin(controlConfig.pluginName, '标签组', '标签组控件属性设置', controlConfig);
