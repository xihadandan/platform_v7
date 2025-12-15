var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.initProperty = function (field) {
  var _this = this;
  //初始化数据字典树.
  this.initDictCode();

  $("input[name='optionDataSource']").change(function () {
    var checkvalue = $("input[name='optionDataSource']:checked").val();
    if (checkvalue == dyDataSourceType.dataSource) {
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
      var checked2 = $('#optionDataAutoSet2').prop('checked');
      $('.dicAutoSet1')[checked2 ? 'show' : 'hide']();
      $('.dicAutoSet2')[checked2 ? 'hide' : 'show']();
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

  var $tbody = this.$('.definitiontrtable>tbody');
  // 提取模板
  var rowTemplate = $tbody.html();
  $tbody.empty();
  function addRow(rowData) {
    var $tr = $(rowTemplate);
    $tbody.append($tr);
    // 数据源
    var $dataSourceTitle = $tr.find('input[name=dataSourceTitle]');
    var $dataSourceSql = $tr.find('input[name=dsWhereSql]');
    var $dataSourceId = $tr.find('input[name=dataSourceId]');
    var $dataSourceRel = $tr.find('select[name=dataSourceRel]');
    var $dataSourceFieldName = $tr.find('select[name=dataSourceFieldName]');
    var $dataSourceDisplayName = $tr.find('select[name=dataSourceDisplayName]');
    function loadDataSource2Fields(dataSourceId) {
      var dataStoreId = dataSourceId;
      var dataStoreIdKey = 'data-' + dataStoreId;
      var nameSource = $tbody.data(dataStoreIdKey);
      if (nameSource == null || typeof nameSource === 'undefined') {
        JDS.call({
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
        $tbody.data(dataStoreIdKey, nameSource);
      }
      var data = nameSource || [];
      var optionHtml = '<option></option>';
      for (var i = 0; i < data.length; i++) {
        if (data[i].title == null) {
          console.log('ID为[' + dataSourceId + ']的数据源的列的title属性为null,请确认数据源的配置是否有问题!');
          continue;
        }
        optionHtml += "<option value='" + data[i].id + "'>" + data[i].title + '</option>';
      }
      $dataSourceRel.html(optionHtml);
      $dataSourceFieldName.html(optionHtml);
      $dataSourceDisplayName.html(optionHtml);
      // 设值
      $dataSourceRel.val(rowData.dataSourceRel);
      $dataSourceFieldName.val(rowData.dataSourceFieldName);
      $dataSourceDisplayName.val(rowData.dataSourceDisplayName);
      return nameSource;
    }
    $dataSourceId
      .wSelect2({
        serviceName: 'viewComponentService',
        queryMethod: 'loadSelectData',
        remoteSearch: false
      })
      .change(function () {
        var dataStoreId = $dataSourceId.val();
        loadDataSource2Fields(dataStoreId);
      })
      .val(rowData.dataSourceId)
      .trigger('change');
    // 默认条件
    $dataSourceSql.val(rowData.dataSourceSql);
    // 层级标题
    $dataSourceTitle.val(rowData.dataSourceTitle);
  }
  // 选中
  $tbody.on('focus', 'tr', function (event) {
    $tbody.find('.row-selected').removeClass('row-selected');
    $(this).addClass('row-selected');
  });

  this.$('#btn-add').click(function (event) {
    addRow({});
  });

  this.$('#btn-upRow').click(function (event) {
    var $selected = $tbody.find('.row-selected');
    var $prev = $selected.prev();
    if ($prev.length) {
      $selected.after($prev);
      // $prev = $selected.prev(); // 置顶
    }
  });

  this.$('#btn-downRow').click(function (event) {
    var $selected = $tbody.find('.row-selected');
    var $next = $selected.next();
    if ($next.length) {
      $selected.before($next);
      // $next = $selected.next();// 置底
    }
  });

  this.$('#btn-del').click(function (event) {
    var $selected = $tbody.find('.row-selected');
    if ($selected.length && confirm('确认删除？')) {
      $selected.remove();
    }
  });

  if (field == null || typeof field == 'undefined') {
    field = new WRadioClass();
    field.inputMode = dyFormInputMode.chained;
    field.valueCreateMethod = '1';
  } else {
    //$("#name").attr("readonly", "readonly");//在编辑的状态下时则时字段名为只读
  }

  this.initDataSource2(field.dataSourceId, null, function () {
    //控件属性初始化公共设置.
    // _this.ctlPropertyComInitSet(field);
  });

  _this.ctlPropertyComInitSet(field);

  //私有属性
  if (field.optionDataSource == dyDataSourceType.dataDictionary) {
    this.$("input[name='optionDataSource'][value='" + dyDataSourceType.dataDictionary + "']").attr('checked', true);
    this.$('.tr_dicCode').show();
    this.$('.tr_data').hide();
    this.$('.tr_dataSource').hide();
    $('#optionDataAutoSet2').prop('checked', field.optionDataAutoSet).trigger('change');
    $('#relateField2').val(field.relateField).trigger('change');
  } else {
    this.$("input[name='optionDataSource'][value='" + dyDataSourceType.dataSource + "']").attr('checked', true);
    this.$('.tr_dicCode').hide();
    this.$('.tr_dataSource').show();
    this.$('.tr_data').hide();
    $('#optionDataAutoSet3').prop('checked', field.optionDataAutoSet).trigger('change');
    $('#relateField3').val(field.relateField).trigger('change');
  }

  if (field.dictCode != '' && field.dictCode != undefined) {
    $('#dictCode').val(field.dictCode);
    var dictcodearray = field.dictCode.split(':');
    $('#dictName').val(dictcodearray[1]);
  }
  $('#dicTitles').val(field.dicTitles);

  if ($.isArray(field.dataSources)) {
    $.each(field.dataSources, function (idx, rowData) {
      addRow(rowData);
    });
  }

  $("input[name='showType'][value='" + field.showType + "']").each(function () {
    this.checked = true;
  });

  $('#unitUnique').val(field.unitUnique);
  if (field.unitUnique == 'true') {
    $('#checkRule_6').attr('checked', true);
    $('#checkRule_5').attr('checked', false);
  } else if (field.unitUnique == 'false') {
    $('#checkRule_5').attr('checked', true);
    $('#checkRule_6').attr('checked', false);
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

controlConfig.collectFormAndFillCkeditor = function () {
  var field = new WRadioClass();
  field.fieldCheckRules = [];
  field.inputMode = dyFormInputMode.chained;

  field.optionDataSource = $("input[name='optionDataSource']:checked").val();
  //added by linxr
  field.noNullValidateReminder = $('#noNullValidateReminder').val();
  field.uniqueValidateReminder = $('#uniqueValidateReminder').val();
  field.unitUnique = $('#unitUnique').val();
  //控件公共属性收集
  var checkpass = this.collectFormCtlComProperty(field);
  if (!checkpass) {
    return false;
  }
  if (field.optionDataSource == dyDataSourceType.dataSource) {
    //数据仓库
    field.optionDataAutoSet = $('#optionDataAutoSet3').prop('checked');
    if ($('#optionDataAutoSet3').prop('checked')) {
      field.relateField = $('#relateField3').val();
      if (field.relateField == '' || field.relateField == undefined) {
        alert('联动字段不能为空！');
        return false;
      }
    }
    var $trs = this.$('.definitiontrtable>tbody>tr');
    if (!$trs.length) {
      alert('至少一级数据仓库,点击“添加”进行');
      return false;
    }
    function getRow($tr) {
      var rowData = {};
      rowData.dataSourceTitle = $tr.find('input[name=dataSourceTitle]').val();
      rowData.dataSourceSql = $tr.find('input[name=dsWhereSql]').val();
      rowData.dataSourceId = $tr.find('input[name=dataSourceId]').val();
      rowData.dataSourceRel = $tr.find('select[name=dataSourceRel]').val();
      rowData.dataSourceFieldName = $tr.find('select[name=dataSourceFieldName]').val();
      rowData.dataSourceDisplayName = $tr.find('select[name=dataSourceDisplayName]').val();
      return rowData;
    }
    var dataSources = [];
    for (var i = 0; i < $trs.length; i++) {
      var rowData = getRow($($trs[i]));
      if (rowData.dataSourceId == '' || rowData.dataSourceId == undefined) {
        alert('数据仓库不能为空！');
        return false;
      }
      // 第一个数据源只关注条件（可选）
      if ((i > 0 && rowData.dataSourceRel == '') || rowData.dataSourceRel == undefined) {
        alert('条件不能为空！');
        return false;
      }
      if (rowData.dataSourceDisplayName == '' || rowData.dataSourceDisplayName == undefined) {
        alert('显示值不能为空！');
        return false;
      }
      if (rowData.dataSourceDisplayName == '' || rowData.dataSourceDisplayName == undefined) {
        alert('显示值不能为空！');
        return false;
      }
      dataSources.push(rowData);
    }
    field.dataSources = dataSources;
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
    }

    field.dicTitles = $('#dicTitles').val();
    if ($.trim(field.dicTitles).length <= 0) {
      alert('层级标题不能为空');
      return false;
    }
  }
  //创建控件占位符
  this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);

  formDefinition.addField(field.name, field);
  return true;
};

controlConfig.getInputEvents = function () {
  return [
    {
      id: 'click',
      chkDisabled: true,
      name: 'click'
    },
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

controlConfig.pluginName = CkPlugin.CHAINED;
addPlugin(controlConfig.pluginName, '级联控件', '级联控件属性设置', controlConfig);
