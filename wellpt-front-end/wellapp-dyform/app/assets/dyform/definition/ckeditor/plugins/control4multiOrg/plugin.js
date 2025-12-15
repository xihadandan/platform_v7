var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.setDbDataType = function (dbDataType) {
  var _this = this;

  if (this.$('#dbDataType').size() == 0) {
    return;
  }
  if (this.$('#dbDataType').find('option').length > 0) {
    this.$('#dbDataType').val(dbDataType);
    return;
  }
  var dyFormInputTypeObj = {
    _text: {
      code: dyFormInputType._text,
      name: '文本'
    },
    _clob: {
      code: dyFormInputType._clob,
      name: '大字段'
    }
  };
  for (var type in dyFormInputTypeObj) {
    var obj = dyFormInputTypeObj[type];
    this.$('#dbDataType').append("<option value='" + obj.code + "'>" + obj.name + '</option>');
  }
  this.$('#dbDataType').change(function () {
    if ($(this).val() == dyFormInputType._clob) {
      //大字段,不需要设置长度
      _this.$('#length').parents('tr').first().hide();
    } else {
      _this.$('#length').parents('tr').first().show();
    }
  });
  if (typeof dbDataType == 'undefined') {
    return;
  }
  this.$('#dbDataType').val(dbDataType);
  this.$('#dbDataType').trigger('change');
};

controlConfig.originalField = {};

controlConfig.initProperty = function (field) {
  if (field == null || typeof field == 'undefined') {
    field = new WUnitClass();
    field.dbDataType = dyFormInputType._text;
    field.valueCreateMethod = '1';
    field.inputMode = dyFormInputMode.orgSelect2;
    this.originalField = {};
    field.nameDisplayMethod = '1';
  } else {
    this.originalField = JSON.parse(JSON.stringify(field));

    // 兼容历史数据, 之前关联角色为多选
    if (field.relativeRoleUuids) {
      field.relativeRoleUuids = field.relativeRoleUuids.split(';')[0];
      field.relativeRoleNames = field.relativeRoleNames.split(';')[0];
    }
  }
  //控件属性初始化公共设置.
  this.ctlPropertyComInitSet(field);

  function isJSON(str) {
    if (typeof str == 'string') {
      try {
        var obj = JSON.parse(str);
        if (typeof obj == 'object' && obj) {
          return true;
        } else {
          return false;
        }
      } catch (e) {
        console.log('error：' + str + '!!!' + e);
        return false;
      }
    }
    console.log('It is not a string!');
  }

  $('.more-split').on('click', function () {
    $(this).toggleClass('up');
  });
  var html =
    '<div class="dyform article_dyform">' +
    '<div class="label-title">过滤方式</div> ' +
    '<div><p>分为“过滤”和“仅显示”两类。过滤即组织弹出框中过滤不显示指定的节点，其他节点全部显示；仅显示即组织弹出框中仅显示指定的节点，其他节点不显示，具体参数说明如下</p></div>' +
    '<div class="label-title">组织节点过滤说明</div> ' +
    '<div><p>1 过滤</p>' +
    '<p class="level_1">参数样例：D0000003681;D0000003682</p>' +
    '<p class="level_1">参数说明：用于指定需过滤的组织节点ID，多值用英文分号间隔。支持单位、部门、职位等节点类型，仅适用于“我的单位”组织选择项。</p>' +
    '<p>2 仅显示</p>' +
    '<p class="level_1">参数样例：otherParams://{"eleIdPath":"V0000000420/B0000000187/D0000003681/J0000013024;V0000000420/B0000000187/D0000003682;V0000000421/B0000000188"}</p>' +
    '<p class="level_1">参数说明：用于指定仅显示的组织节点ID，必填，需要填写节点全路径，多值用英文分号间隔。支持单位、部门、职位等节点类型，仅适用于“我的单位”组织选择项。</p>' +
    '</div>' +
    '<div class="label-title">群组过滤说明</div> ' +
    '<div><p>1 过滤</p>' +
    '<p class="level_1">参数样例：DU0000000036;DU0000000037</p>' +
    '<p class="level_1">参数说明：用于指定需过滤的群组ID，多值用英文分号间隔。支持个人群组、公共群组、职务群组的过滤，仅适用于“我的群组”、“公共群组”、“职务群组”组织选择项。</p>' +
    '<p>2 仅显示</p>' +
    '<p class="level_1">参数样例：otherParams://{"groupId":"G0000000027;G0000000028","dutyId":"DU0000000018;DU0000000019"}</p>' +
    '<p class="level_1">参数说明：用于指定仅显示的群组ID，包括群组类型和群组ID两部分，多值用英文分号间隔。\
  群组类型：个人群组、公共群组的类型为groupId，职务群组的类型为dutyId；\
  群组ID：输入群组ID，必填，多值用英文分号间隔；\
  支持个人群组、公共群组、职务群组的过滤，仅适用于“我的群组”、“公共群组”、“职务群组”组织选择项。</p>' +
    '</div>' +
    '<div class="label-title">业务通讯录过滤说明</div>' +
    '<div><p>1 仅显示</p>' +
    '<p class="level_1">参数样例：otherParams://{categoryId:"SW_BW,YW",operator:"in",ids:"E0000000001,C0000000002",showOrgUser:true}</p>' +
    '<p class="level_1">参数说明：categoryId ： 必填，输入指定的业务分类ID，用于指定业务通讯录加载对应分类的业务通讯录，多值用 ,英文逗号\
  间隔；\
  operator：操作符，取值范围： in  或者 not in ，用于筛选数据。in代表仅展示对应参数ids的里面节点ID的节点，\
  not in 代表会过滤掉对应参数ids的里面节点ID的节点；\
  ids：需显示的节点id，多值用英文逗号间隔；\
  showOrgUser：是否展示用户节点；\
  支持业务通讯录节点的显示约束，仅适用于“业务通讯录”组织选择项。</p>' +
    '</div>' +
    '</div>';
  $('#filterConditionTip').on('click', function () {
    appModal.dialog({
      message: html,
      title: '过滤ID参数说明',
      buttons: {
        cancel: {
          label: '关闭',
          className: 'btn-default'
        }
      },
      width: '1000px'
      // height: '600px'
    });
  });
  if (field.nameDisplayMethod) {
    $("input[name='nameDisplayMethod'][value='" + field.nameDisplayMethod + "']").attr('checked', true);
  }

  //默认值转换
  $('#defaultValue').val(this.reaplaceInitDefaultValue(field.defaultValue));
  //表单字段值获得焦点时下拉宏一定选项
  $('#defaultValue')
    .focus(function () {
      if (!$("input[name='defaultSelectIds']").val()) {
        $('.hongdy').show();
      }
    })
    .blur(function () {
      window.setTimeout(function () {
        $('.hongdy').hide();
      }, 128); // 在.isChose的click事件之后
    });
  // 有设置默认选中的话，则默认值就不能编辑
  if (field.defaultSelectIds) {
    $('#defaultValue').attr('readonly', 'readonly').val(field.defaultSelectIds);
  }

  // 只显示当前用户和创建者用户选择项
  $('.isChose').each(function () {
    var name = $(this).attr('name');
    if (['currUserId', 'currUserJobId', 'currUserDeptId', 'currUserBizUnitId'].indexOf(name) > -1) {
      $(this).show();
    } else {
      $(this).hide();
    }
  });
  $('.noChose').hide();
  $('.isChose').click(function (event) {
    var dv = $('#defaultValue').val();
    $('#defaultValue').val(dv + ($.trim(dv).length ? ';' : '') + '{' + $(this).html() + '}');
  });

  $('#unitUnique').val(field.unitUnique);
  if (field.unitUnique == 'true') {
    $('#checkRule_6').attr('checked', true);
    $('#checkRule_5').attr('checked', false);
  } else if (field.unitUnique == 'false') {
    $('#checkRule_5').attr('checked', true);
    $('#checkRule_6').attr('checked', false);
  }

  //私有属性
  $('#serviceName').val(field.serviceName);

  $('#filterCondition').val(field.filterCondition);
  //单选
  $("input[name='mutiSelect'][value='" + field.mutiSelect + "']").attr('checked', true);

  $("input[name='valueFormat'][value='" + field.valueFormat + "']").attr('checked', true);

  // 默认选中, 如果同时设置默认值和默认选中，则默认选中优先级比较高
  $("input[name='defaultSelectIds']").val(field.defaultSelectIds);
  $("input[name='defaultSelectNames']").val(field.defaultSelectNames);
  $("input[name='defaultSelectNames']").bind('click', function () {
    $.unit2.open({
      valueField: 'defaultSelectIds',
      labelField: 'defaultSelectNames',
      title: '默认选中',
      type: 'all',
      multiple: true,
      selectTypes: 'all',
      valueFormat: 'justId',
      zIndex: '100000',
      callback: function (values, labels, treeNodes) {
        if (treeNodes && treeNodes.length > 0) {
          var map = {};
          var _ids = [];
          $.each(treeNodes, function (i, node) {
            map[node.id] = node.name;
            _ids.push(node.id);
          });
          $('#defaultValue').val(_ids.join(';'));
          $('#defaultValue').attr('readonly', 'readonly');
        } else {
          $('#defaultValue').val(null);
          $('#defaultValue').removeAttr('readonly');
        }
      }
    });
  });

  this.$('#orgStyle').val(field.orgStyle);

  // 关联角色
  $('#relativeRoleNames').wSelect2Group({
    serviceName: 'securityApiFacade',
    queryMethod: 'queryRoleListByUnitForSelect2Group',
    valueField: 'relativeRoleUuids',
    labelField: 'relativeRoleNames',
    placeholder: '请选择',
    multiple: false,
    remoteSearch: false,
    defaultBlank: true, // 默认空选项
    width: '100%',
    height: 250
  });
  $('#relativeRoleUuids').val(field.relativeRoleUuids);
  $('#relativeRoleNames').val(field.relativeRoleNames).trigger('change');

  this.initSelectTypeList(field);
  this.creatTypeListCheckboxs(field);
  this.initViewStyle(field);
  this.initMappingValues(field);
  this.initRelativeRoleNamesSwitcher(field);
  this.bindEvent(field);

  this.$("[data-toggle='popover']").popover();
};

controlConfig.creatTypeListCheckboxs = function (field) {
  var _this = this;
  var showOptions = this.getShowOption();
  if (!field.typeList) {
    //默认全选
    field.typeList = [];
    $.each(showOptions, function () {
      if (this.isShow == 1) {
        field.typeList.push(this.id);
      }
    });
  }
  field.viewStyle = field.viewStyle || [];
  $.each(showOptions, function (index, item) {
    if (this.id === 'Role' && this.name === '角色') {
      return;
    }
    var viewStyle = this.attach;
    var typeIdx = $.inArray(this.id, field.typeList);
    var bEnableTree = typeof viewStyle === 'string' && viewStyle.indexOf('list') > -1;
    $('#typeListTd').append(
      '<input type="checkbox" name="typeList" value="' +
        this.id +
        '" lableName="' +
        this.name +
        '" ' +
        '" id="' +
        this.id +
        '" ' +
        (typeIdx < 0 ? '' : 'checked="checked"') +
        '/><label class="label" for="' +
        this.id +
        '">' +
        this.name +
        '</label>'
    );
    $('#defaultTypeListTd').append(
      '<input type="radio" name="defaultType" value="' +
        this.id +
        '" lableName="' +
        this.name +
        '" ' +
        ' id="' +
        this.id +
        '_default' +
        '" ' +
        (this.id == field.defaultType ? 'checked="checked"' : '') +
        '/><label class="label" for="' +
        this.id +
        '_default' +
        '" ' +
        '>' +
        this.name +
        '</label>'
    );
    $('#defaultTreeStyle').append(
      '<input type="checkbox" name="defaultTreeStyle" value="' +
        this.id +
        '" lableName="' +
        this.name +
        '" ' +
        ' id="' +
        this.id +
        '_style' +
        '" ' +
        ('list' != field.viewStyle[typeIdx] ? 'checked="checked"' : '') +
        ' ' +
        (bEnableTree ? '' : 'disabled="disabled" data-xdisabled="disabled"') +
        '/><label class="label" for="' +
        this.id +
        '_style' +
        '" ' +
        '>' +
        this.name +
        '</label>'
    );
    $('#defaultListStyle').append(
      '<input type="checkbox" name="defaultListStyle" value="' +
        this.id +
        '" lableName="' +
        this.name +
        '" ' +
        ' id="' +
        this.id +
        '_liststyle' +
        '" ' +
        ('list' == field.viewStyle[typeIdx] ? 'checked="checked"' : '') +
        ' ' +
        (bEnableTree ? '' : 'disabled="disabled" data-xdisabled="disabled"') +
        '/><label class="label" for="' +
        this.id +
        '_liststyle' +
        '" >' +
        this.name +
        '</label>'
    );
  });
};

controlConfig.bindEvent = function (field) {
  var _this = this;
  //可选的组织类型 全选按钮
  $('#typeListSelectAll').bind('click', function () {
    $('#typeListTd').find('input[name="typeList"]').attr('checked', 'checked').trigger('change');
  });
  //可选的组织类型 全取消按钮
  $('#typeListCancelSelectAll').bind('click', function () {
    $('#typeListTd').find('input[name="typeList"]').removeAttr('checked').trigger('change');
  });
  // 组织类型联动
  $('#defaultTreeStyle>input[type="checkbox"]:not([data-xdisabled]),#defaultListStyle>input[type="checkbox"]:not([data-xdisabled])')
    .on('change', function (event) {
      var $this = $(this);
      var value = $this.val();
      var targetName = $this.attr('name') === 'defaultTreeStyle' ? 'defaultListStyle' : 'defaultTreeStyle';
      if ($this.is(':checked')) {
        $this.attr('disabled', 'disabled');
        $('input[name="' + targetName + '"][value="' + value + '"]')
          .prop('checked', false)
          .removeAttr('disabled');
      } else {
        $this.removeAttr('disabled');
        $('input[name="' + targetName + '"][value="' + value + '"]')
          .prop('checked', true)
          .attr('disabled', 'disabled');
      }
    })
    .trigger('change');
  $('#typeListTd>input[type="checkbox"]')
    .on('change', function (event) {
      var $this = $(this);
      var value = $this.val();
      if ($this.is(':checked')) {
        $('#defaultTypeListTd>input[value="' + value + '"]')
          .next('label')
          .show();
        var $tin = $('#defaultTreeStyle>input[value="' + value + '"]').hide();
        var $lin = $('#defaultListStyle>input[value="' + value + '"]').hide();
        $tin.next('label').show();
        $lin.next('label').show();
        if (false === $tin.prop('checked') && false === $lin.prop('checked')) {
          $tin.prop('checked', true).trigger('change'); // 选中一个样式
        }
      } else {
        $('#defaultTypeListTd>input[value="' + value + '"]')
          .hide()
          .prop('checked', false)
          .next('label')
          .hide();

        $('#defaultTreeStyle>input[value="' + value + '"]')
          .hide()
          .prop('checked', false)
          .next('label')
          .hide();
        $('#defaultListStyle>input[value="' + value + '"]')
          .hide()
          .prop('checked', false)
          .next('label')
          .hide();
      }
      var $checked = $('#typeListTd>input:checked');
      if ($checked.length == 1) {
        // 只有一个时，默认选中
        $('#defaultTypeListTd>input[value="' + $checked.val() + '"]').prop('checked', true);
      } else if ($('#defaultTypeListTd>input:checked').length <= 0) {
        $('#defaultTypeListTd>label:visible').filter(':first').prev().prop('checked', true);
      }
    })
    .trigger('change');
  var $defaultTypeChecked = $("input[name='defaultType']:checked");
  if ($defaultTypeChecked && $defaultTypeChecked.length <= 0) {
    // 默认选中第一个
    $("#defaultTypeListTd>input[name='defaultType']").each(function () {
      var $this = $(this);
      // 初始化时，页签隐藏，不能用:visible
      var css = $(this).attr('css') || '';
      var cssList = css.split(';');
      var dIndex = 0;
      $.each(cssList, function (index, item) {
        if (item.split(':')[0] == 'display') {
          dIndex = index;
          return false;
        }
      });
      if (cssList[dIndex].split(':')[1] === 'none') {
        return true;
      }
      $this.click();
      return false;
    });
  }
  $('#mappingValues')
    .on('change', function (event) {
      if ($(this).is(':checked')) {
        $('#mappingValuesTr').show();
        if (false === $.isPlainObject(field.mappingValues)) {
          $('select#mappingDeptName').closest('.mapping-group-name').find('input.mapping-enable').prop('checked', true).trigger('change');
        }
        $('#selectTypeListTd>input[type="checkbox"]')
          .attr('disabled', 'disabled')
          .prop('checked', false)
          .filter('[value="U"]')
          .prop('checked', true)
          .trigger('change');
        $("input[name='mutiSelect']").attr('disabled', 'disabled').filter("[value='false']").prop('checked', true);
        // 开启时，提示注意事项
        event.isTrigger ||
          alert(
            '特别注意：\n1、针对已产生用户操作数据的表单，请在开启后进行数据升级，以保障已有的人员数据，在用户操作后可以获取组织选择项（从而获取人员映射信息），否则系统将报错提示\n2、如果组织选择项的组织树上无职位节点，则用户无法选择该组织选择项下的人员，系统将报错提示'
          );
      } else {
        $('#mappingValuesTr').hide();
        $('#selectTypeListTd>input[type="checkbox"]').removeAttr('disabled');
        $("input[name='mutiSelect']").removeAttr('disabled');
      }
    })
    .prop('checked', $.isPlainObject(field.mappingValues))
    .trigger('change');

  var _switchStatusCache;
  $('#relativeRoleNamesSwitcher').on('change', function (event) {
    if ($(this).is(':checked')) {
      $('#relativeRoleNamesInput').show();
      $('#typeListTd input[type="checkbox"]').attr('disabled', 'disabled');
      $('#typeListButtons').hide();

      // 保存当前状态，用于随时切换
      _switchStatusCache = _createSwitchCache();

      $.each($('#typeListTd input'), function (i, input) {
        var $input = $(input);
        var inputValue = $input.val();
        if ($.inArray(inputValue, ['MyUnit', 'PublicGroup']) > -1) {
          $input.attr('checked', true).trigger('change');
        } else {
          $input.removeAttr('checked').trigger('change');
        }
      });
    } else {
      $('#relativeRoleNamesInput').hide();
      $('#typeListTd input[type="checkbox"]').removeAttr('disabled');
      $('#typeListButtons').show();

      $.each($('#typeListTd input'), function (i, input) {
        var $input = $(input);
        var inputValue = $input.val();
        // 如果有缓存值，则使用缓存值
        var typeList = _switchStatusCache ? _switchStatusCache.typeList : field.typeList;
        if ($.inArray(inputValue, typeList) > -1) {
          $input.attr('checked', true).trigger('change');
        } else {
          $input.removeAttr('checked').trigger('change');
        }

        // 更改默认选择项
        var defaultType = _switchStatusCache ? _switchStatusCache.defaultType : field.defaultType;
        $.each($("input[name='defaultType']"), function (i, input) {
          var $input = $(input);
          if ($input.val() === defaultType) {
            $input.prop('checked', true);
          } else {
            $input.prop('checked', false);
          }
        });
      });
    }
  });

  function _createSwitchCache() {
    var cache = {};
    cache.defaultType = $("input[name='defaultType']:checked").val();
    cache.typeList = [];
    $.each($('#typeListTd').find('input[name="typeList"]:checked'), function () {
      cache.typeList.push($(this).val());
    });
    return cache;
  }

  $('#mappingValuesTr input[type="checkbox"].mapping-enable')
    .on('change', function (event) {
      var $this = $(this);
      if ($this.is(':checked')) {
        // $this.next("select").show();
        $this.siblings('span.mapping-name,span.mapping-id').show();
      } else {
        // $this.next("select").val("").hide();
        $this.siblings('span.mapping-name,span.mapping-id').hide().find('select').val('');
      }
    })
    .trigger('change');

  $('#selectTypeListTd>input[value="U"]')
    .on('change', function (event) {
      var $this = $(this);
      if ($this.is(':checked')) {
        $('#defaultListStyle').closest('div').show();
      } else {
        $('#defaultListStyle').closest('div').hide();
        event.isTrigger || $('#defaultTreeStyle>input[type="checkbox"]:visible').prop('checked', true).trigger('change'); // 全部选中树（取消列表）
      }
    })
    .trigger('change');
};

controlConfig.getShowOption = function () {
  var opts = null;
  JDS.call({
    mask: false,
    service: 'multiOrgService.queryOrgOptionListBySystemUnitIdAndOptionOfPT',
    data: [SpringSecurityUtils.getCurrentUserUnitId(), false],
    validate: true,
    async: false,
    success: function (result) {
      opts = result.data;
    }
  });
  return opts;
};

controlConfig.initSelectTypeList = function (field) {
  var _this = this;
  if (!field.selectTypeList) {
    //默认全选
    field.selectTypeList = [];
    $.each(ORG_SELECT_TYPES, function () {
      field.selectTypeList.push(this.code);
    });
  }

  $.each(ORG_SELECT_TYPES, function () {
    if (!field.selectTypeObj) {
      if (field.selectTypeList.indexOf('E') > -1) {
        field.selectTypeList.push('C');
      }
    }
    $('#selectTypeListTd').append(
      '<input type="checkbox" name="selectTypeList" value="' +
        this.code +
        '" lableName="' +
        this.name +
        '" ' +
        '" id="' +
        this.code +
        '" ' +
        ($.inArray(this.code, field.selectTypeList) < 0 ? '' : 'checked="checked"') +
        '/>' +
        '<label for="' +
        this.code +
        '" >' +
        this.name +
        '</label>'
    );
  });
};

controlConfig.initViewStyle = function (field) {
  field.viewStyle = field.viewStyle || [];
  // see @creatTypeListCheckboxs
};

controlConfig.initMappingValues = function (field) {
  var mappingValues = field.mappingValues;
  if ($.isPlainObject(mappingValues)) {
    $('#mappingValues').prop('checked', true).trigger('change');
    $.each(mappingValues, function (mappingName, mappingObj) {
      var $select = $('select[name="mapping' + mappingName + '"]');
      $select.val(mappingObj.fieldName);
      $select.next('input[type="checkbox"]').prop('checked', mappingObj.path);
      $select.parent('span').siblings('input[type="checkbox"]').prop('checked', true).trigger('change');
    });
  } else {
    $('#mappingValues').prop('checked', false).trigger('change');
  }
};

controlConfig.initRelativeRoleNamesSwitcher = function (field) {
  var useRelativeRole = 'useRelativeRole' in field ? field.useRelativeRole : field.relativeRoleNames;
  if (useRelativeRole) {
    $('#relativeRoleNamesSwitcher').prop('checked', true).trigger('change');
    $('#relativeRoleNamesInput').show();
    $('#typeListTd input[type="checkbox"]').attr('disabled', 'disabled');
    $('#typeListButtons').hide();

    $.each($('#typeListTd input'), function (i, input) {
      var $input = $(input);
      var inputValue = $input.val();
      if ($.inArray(inputValue, ['MyUnit', 'PublicGroup']) > -1) {
        $input.attr('checked', true).trigger('change');
      } else {
        $input.removeAttr('checked').trigger('change');
      }
    });
  } else {
    $('#relativeRoleNamesSwitcher').prop('checked', false).trigger('change');
    $('#relativeRoleNamesInput').hide();
    $('#typeListTd input[type="checkbox"]').removeAttr('disabled');
    $('#typeListButtons').show();

    $.each($('#typeListTd input'), function (i, input) {
      var $input = $(input);
      var inputValue = $input.val();
      if ($.inArray(inputValue, field.typeList) > -1) {
        $input.attr('checked', true).trigger('change');
      } else {
        $input.removeAttr('checked').trigger('change');
      }
    });
  }
};

controlConfig.collectViewStyle = function (field) {
  var viewStyle = [];
  $('#defaultTreeStyle>input[type="checkbox"]:visible').each(function (idx, element) {
    var $element = $(element);
    if ($element.is(':checked')) {
      viewStyle.push('tree');
    } else {
      viewStyle.push('');
    }
  });
  $('#defaultListStyle>input[type="checkbox"]').each(function (idx, element) {
    var $element = $(element);
    if ($element.is(':checked')) {
      viewStyle[idx] = 'list';
    }
  });
  field.viewStyle = viewStyle;
};

controlConfig.collectMappingValues = function (field) {
  // 校验勾起必选
  if (false == $('#mappingValues').prop('checked')) {
    delete field.mappingValues;
    return true;
  }
  var $checked = $('#mappingValuesTr input.mapping-enable:checked');
  if ($checked.length <= 0) {
    alert('请选择人员信息映射');
    return;
  }
  var $inValid = $checked.filter(function (idx, element) {
    var val = $(this).siblings('span.mapping-name,span.mapping-id').find('>select').val();
    return $.trim(val).length <= 0;
  });
  if ($inValid.length > 0) {
    var fileNames = $.map($inValid, function (element, idx) {
      var $element = $(element);
      var v1 = $element.closest('div.mapping-group').find('>label.mapping-label').text();
      var v2 = $element.next('label.mapping-label').text();
      return v1 + v2;
    });
    alert('人员信息映射的字段不允许为空，以下字段为空：' + fileNames.join('、'));
    $inValid.next('span').find('>select').focus();
    return;
  }
  $inValid = $checked.filter(function (idx, element) {
    var $select = $(this).siblings('span.mapping-name,span.mapping-id').find('>select');
    var $sameField = $checked.filter(function (idx, element) {
      var $select2 = $(this).siblings('span.mapping-name,span.mapping-id').find('>select');
      return $select.is($select2) === false && $select.val() === $select2.val();
    });
    return $sameField.length > 0;
  });
  if ($inValid.length > 0) {
    var fileNames = $.map($inValid, function (element, idx) {
      var $element = $(element);
      var v1 = $element.closest('div.mapping-group').find('>label.mapping-label').text();
      var v2 = $element.next('label.mapping-label').text();
      return v1 + v2;
    });
    alert('人员信息映射的字段不允许重复，以下字段重复：' + fileNames.join('、'));
    $inValid.next('span').find('>select').focus();
    return;
  }
  // 校验勾起必选 end

  var mappingValues = {};
  $('#mappingValuesTr select.mappingFields').each(function (idx, element) {
    var $element = $(element);
    var fieldName = $element.val();
    var mappingName = $element.attr('name').replace('mapping', '');
    if ($.trim(fieldName).length > 0 && $.trim(mappingName).length > 0) {
      var mappingPath = $element.next('input[type="checkbox"]').is(':checked');
      mappingValues[mappingName] = {
        path: mappingPath,
        fieldName: fieldName
      };
    }
  });
  if ($.isEmptyObject(mappingValues)) {
    delete field.mappingValues;
  } else {
    field.mappingValues = mappingValues;
  }
  return true;
};

controlConfig.collectFormAndFillCkeditor = function () {
  var field = new WUnitClass();
  field.fieldCheckRules = [];
  field.inputMode = dyFormInputMode.orgSelect2;
  //added by linxr
  field.noNullValidateReminder = $('#noNullValidateReminder').val();
  field.uniqueValidateReminder = $('#uniqueValidateReminder').val();
  field.unitUnique = $('#unitUnique').val();
  //控件公共属性收集
  var checkpass = this.collectFormCtlComProperty(field);
  if (!checkpass) {
    return false;
  }

  field.serviceName = $('#serviceName').val();
  field.filterCondition = $('#filterCondition').val();
  //	field.orgVersionId = $("#orgVersionId").val();
  field.valueFormat = $("input[name='valueFormat']:checked").val();
  field.defaultType = $("input[name='defaultType']:checked").val();

  //默认选中的值
  field.defaultSelectIds = $('#defaultSelectIds').val();
  field.defaultSelectNames = $('#defaultSelectNames').val();
  // 默认选中的优先级大于默认值
  if (field.defaultSelectIds == undefined || field.defaultSelectIds == '') {
    //默认值转换
    field.defaultValue = this.reaplaceDefaultValue($('#defaultValue').val());
  }
  field.orgStyle = this.$('#orgStyle').val();
  //关联角色
  field.relativeRoleUuids = $('#relativeRoleUuids').val();
  field.relativeRoleNames = $('#relativeRoleNames').val();
  field.useRelativeRole = $('input[name="relativeRoleNamesSwitcher"]:checked').val() === '1';
  if (field.useRelativeRole && !field.relativeRoleNames) {
    alert('开启关联角色后，必须从后台角色中选择1个！');
    return;
  }

  var mutiselect = $("input[name='mutiSelect']:checked").val();
  if (mutiselect == 'true') {
    field.mutiSelect = true;
  } else {
    field.mutiSelect = false;
  }
  field.nameDisplayMethod = $("input[name='nameDisplayMethod']:checked").val();

  try {
    this.collectTypeList(field);
    this.collectSelectTypeList(field);
    this.collectViewStyle(field);
    if (!this.collectMappingValues(field)) {
      return;
    }
  } catch (e) {
    return false;
  }

  //创建控件占位符
  this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);

  formDefinition.addField(field.name, field);

  return true;
};

controlConfig.collectTypeList = function (field) {
  if (field.useRelativeRole) {
    field.typeList = this.originalField.typeList;
    return;
  }

  var typeList = [];
  $.each($('#typeListTd').find('input[name="typeList"]:checked'), function () {
    typeList.push($(this).val());
  });
  if (typeList.length < 1) {
    alert('请选择可选的组织选择项');
    throw new exception('请选择可选的组织选择项');
  }
  field.typeList = typeList;
};

controlConfig.collectSelectTypeList = function (field) {
  // if (field.useRelativeRole){
  // 	field.selectTypeList = this.originalField.selectTypeList;
  // 	return;
  // }

  var selectTypeList = [];
  var selectTypeObj = {};
  $.each($('#selectTypeListTd').find('input[name="selectTypeList"]:checked'), function () {
    selectTypeList.push($(this).val());
    selectTypeObj[$(this).val()] = true;
  });
  if (selectTypeList.length < 1) {
    alert('请选择可选的组织节点类型');
    throw new exception('请选择可选的组织节点类型');
  }
  field.selectTypeList = selectTypeList;
  field.selectTypeObj = selectTypeObj;
};

controlConfig.getInputEvents = function () {
  return [
    {
      id: 'click',
      chkDisabled: true,
      name: 'click'
    },
    {
      id: 'focus',
      chkDisabled: true,
      name: 'focus'
    },
    {
      id: 'blur',
      chkDisabled: true,
      name: 'blur'
    }
  ];
};

controlConfig.pluginName = CkPlugin.MULTIORG;
addPlugin(controlConfig.pluginName, '组织选择控件', '组织选择控件属性设置', controlConfig);
