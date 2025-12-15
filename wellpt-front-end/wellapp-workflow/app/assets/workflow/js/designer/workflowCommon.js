var handlerList = [
  {
    type: 'PriorUser',
    list: [
      {
        id: 'useroptions_1',
        name: '前办理人',
        value: 'PriorUser'
      },
      {
        id: 'useroptions_21',
        name: '前办理人的直接汇报人',
        value: 'DirectLeaderOfPriorUser'
      },
      {
        id: 'useroptions_23',
        name: '前办理人的部门领导',
        value: 'DeptLeaderOfPriorUser'
      },
      {
        id: 'useroptions_2',
        name: '前办理人的上级领导',
        value: 'LeaderOfPriorUser'
      },
      {
        id: 'useroptions_27',
        name: '前办理人的分管领导',
        value: 'BranchedLeaderOfPriorUser'
      },
      {
        id: 'useroptions_3',
        name: '前办理人的所有上级领导',
        value: 'AllLeaderOfPriorUser'
      },
      {
        id: 'useroptions_8',
        name: '前办理人的部门人员',
        value: 'DeptOfPriorUser'
      },
      {
        id: 'useroptions_9',
        name: '前办理人的上级部门人员',
        value: 'ParentDeptOfPriorUser'
      },
      {
        id: 'useroptions_10',
        name: '前办理人的根部门人员',
        value: 'RootDeptOfPriorUser'
      },
      {
        id: 'useroptions_90',
        name: '前办理人的根节点人员',
        value: 'RootNodeOfPriorUser'
      },
      {
        id: 'useroptions_29',
        name: '前办理人的单位人员',
        value: 'BizUnitOfPriorUser'
      },
      {
        id: 'useroptions_52',
        name: '前办理人直接下属',
        value: 'SubordinateOfPriorUser'
      },
      {
        id: 'useroptions_53',
        name: '前办理人的所有下属',
        value: 'AllSubordinateOfPriorUser'
      },
      {
        id: 'useroptions_91',
        name: '前办理人的同业务角色人员',
        value: 'SameBizRoleOfPriorUser'
      },
      {
        id: 'useroptions_7',
        name: '前一个环节办理人',
        value: 'PriorTaskUser'
      }
    ]
  },
  {
    type: 'Creator',
    list: [
      {
        id: 'useroptions_4',
        name: '申请人',
        value: 'Creator'
      },
      {
        id: 'useroptions_51',
        name: '申请人的直接汇报人',
        value: 'DirectLeaderOfCreator'
      },
      {
        id: 'useroptions_24',
        name: '申请人的部门领导',
        value: 'DeptLeaderOfCreator'
      },
      {
        id: 'useroptions_5',
        name: '申请人的上级领导',
        value: 'LeaderOfCreator'
      },
      {
        id: 'useroptions_28',
        name: '申请人的分管领导',
        value: 'BranchedLeaderOfCreator'
      },
      {
        id: 'useroptions_6',
        name: '申请人的所有上级领导',
        value: 'AllLeaderOfCreator'
      },
      {
        id: 'useroptions_11',
        name: '申请人的部门人员',
        value: 'DeptOfCreator'
      },
      {
        id: 'useroptions_12',
        name: '申请人的上级部门人员',
        value: 'ParentDeptOfCreator'
      },
      {
        id: 'useroptions_13',
        name: '申请人的根部门人员',
        value: 'RootDeptOfCreator'
      },
      {
        id: 'useroptions_14',
        name: '申请人的根节点人员',
        value: 'RootNodeOfCreator'
      },
      {
        id: 'useroptions_30',
        name: '申请人的单位人员',
        value: 'BizUnitOfCreator'
      },
      {
        id: 'useroptions_92',
        name: '申请人的同业务角色人员',
        value: 'SameBizRoleOfCreator'
      }
    ]
  }
];
var handlerFilterList = [
  {
    type: 'PriorUser',
    list: [
      {
        id: 'useroptions_15',
        name: '限于前办理人的同一部门人员',
        value: 'SameDeptAsPrior'
      },
      {
        id: 'useroptions_16',
        name: '限于前办理人的同一根部门人员',
        value: 'SameRootDeptAsPrior'
      },
      {
        id: 'useroptions_80',
        name: '限于前办理人的同一根节点人员',
        value: 'SameRootNodeAsPrior'
      },
      {
        id: 'useroptions_31',
        name: '限于前办理人的同一单位人员',
        value: 'SameBizUnitAsPrior'
      },
      {
        id: 'useroptions_39',
        name: '限于前办理人的直接汇报人',
        value: 'SameDirectLeaderAsPrior'
      },
      {
        id: 'useroptions_34',
        name: '限于前办理人的部门领导',
        value: 'SameDeptLeaderAsPrior'
      },
      {
        id: 'useroptions_33',
        name: '限于前办理人的上级领导',
        value: 'SameLeaderAsPrior'
      },
      {
        id: 'useroptions_35',
        name: '限于前办理人的分管领导',
        value: 'SameBranchLeaderAsPrior'
      },
      {
        id: 'useroptions_17',
        name: '限于前办理人的所有上级领导',
        value: 'SameAllLeaderAsPrior'
      },
      {
        id: 'useroptions_54',
        name: '限于前办理人的直接下属',
        value: 'SameSubordinateOfPrior'
      },
      {
        id: 'useroptions_55',
        name: '限于前办理人的所有下属',
        value: 'SameAllSubordinateOfPrior'
      },
      {
        id: 'useroptions_83',
        name: '限于前办理人的同业务角色人员',
        value: 'SameBizRoleAsPrior'
      }
    ]
  },
  {
    type: 'Creator',
    list: [
      {
        id: 'useroptions_18',
        name: '限于申请人的同一部门人员',
        value: 'SameDeptAsCreator'
      },
      {
        id: 'useroptions_19',
        name: '限于申请人的同一根部门人员',
        value: 'SameRootDeptAsCreator'
      },
      {
        id: 'useroptions_81',
        name: '限于申请人的同一根节点人员',
        value: 'SameRootNodeAsCreator'
      },
      {
        id: 'useroptions_32',
        name: '限于申请人的同一单位人员',
        value: 'SameBizUnitAsCreator'
      },
      {
        id: 'useroptions_40',
        name: '限于申请人的直接汇报人',
        value: 'SameDirectLeaderAsCreator'
      },
      {
        id: 'useroptions_37',
        name: '限于申请人的部门领导',
        value: 'SameDeptLeaderAsCreator'
      },
      {
        id: 'useroptions_36',
        name: '限于申请人的上级领导',
        value: 'SameLeaderAsCreator'
      },
      {
        id: 'useroptions_38',
        name: '限于申请人的分管领导',
        value: 'SameBranchLeaderAsCreator'
      },
      {
        id: 'useroptions_20',
        name: '限于申请人的所有上级领导',
        value: 'SameAllLeaderAsCreator'
      },
      {
        id: 'useroptions_82',
        name: '限于申请人的同业务角色人员',
        value: 'SameBizRoleAsCreator'
      }
    ]
  }
];

var handlerListAll = {};
for (var i = 0; i < handlerList.length; i++) {
  for (var j = 0; j < handlerList[i].list.length; j++) {
    var item = handlerList[i].list[j];
    handlerListAll[item.value] = item.name;
  }
}
for (var i = 0; i < handlerFilterList.length; i++) {
  for (var j = 0; j < handlerFilterList[i].list.length; j++) {
    var item = handlerFilterList[i].list[j];
    handlerListAll[item.value] = item.name;
  }
}

function switchFun(id, $dialog, callback) {
  $($dialog || document).on('click', '#' + id, function () {
    var _val = $('input[name="' + id + '"]:checked', $dialog || document).val();
    var $this = $(this);
    var isOpen = $(this).hasClass('active'); //当前是否开启状态
    if (isOpen) {
      $this.removeClass('active');
      //关闭
      $this.find('.switch-open').hide();
      $this.find('.switch-close').show();

      $this.find("input[type='radio']:eq(1)").prop('checked', true).trigger('change');
    } else {
      $this.addClass('active');
      //打开
      $this.find('.switch-open').show();
      $this.find('.switch-close').hide();
      $this.find("input[type='radio']:eq(0)").prop('checked', true).trigger('change');
    }
    if (callback) {
      var _val = $('input[name="' + id + '"]:checked', $dialog || document).val();
      callback(_val);
    }
  });

  $($dialog || document).on('change', 'input[name="' + id + '"]', function () {
    var _val = $('input[name="' + id + '"]:checked', $dialog || document).val();
    var $switch = $('#' + id);
    if (_val === '1') {
      $switch.addClass('active');
      $switch.find('.switch-open').show();
      $switch.find('.switch-close').hide();
    } else {
      $switch.removeClass('active');
      $switch.find('.switch-open').hide();
      $switch.find('.switch-close').show();
    }
    // if(callback) {
    //     var _val = $('input[name="' + id + '"]:checked',$dialog || document).val();
    //     callback(_val);
    // }
  });
}

//设置input类型表单字段值
function setInputXMLAttrValue(goForm, arr) {
  $.each(arr, function (i, item) {
    goForm[item].value = goWorkFlow.flowXML.getAttribute(item) == null ? '' : goWorkFlow.flowXML.getAttribute(item);
  });
}

// 从xml获取数据填充到input元素
function setInputXMLValue(goForm, dataObj, obj) {
  $.each(obj, function (i, item) {
    if (item == 'sn') {
      goForm[item].value =
        dataObj.getAttribute('code') == null
          ? dataObj.selectSingleNode('code') == null
            ? ''
            : dataObj.selectSingleNode('code').text()
          : dataObj.getAttribute('code');
    } else {
      goForm[item].value =
        dataObj.getAttribute(item) == null
          ? dataObj.selectSingleNode(item) == null
            ? ''
            : dataObj.selectSingleNode(item).text()
          : dataObj.getAttribute(item);
    }
  });
}

//设置select类型表单字段值
function setSelectXMLValue(goForm, flowProperty, arr, setDefault) {
  $.each(arr, function (i, item) {
    var _nodes = goWorkFlow.dictionXML.selectNodes(item.nodes);
    var data = [];
    if (_nodes != null && _nodes.length > -1) {
      var _node1 = flowProperty.selectSingleNode(item.node1) != null ? flowProperty.selectSingleNode(item.node1).text() : null;
      $('#' + item.eleId).val(_node1);
      _nodes.each(function () {
        var $this = $(this);
        var _node2 = $this.selectSingleNode(item.node2).text();
        data.push({
          id: _node2,
          text: $this.selectSingleNode('name').text()
        });
      });
    }
    $('#' + item.eleId)
      .wellSelect({
        data: data
      })
      .trigger('change');
  });
}

//设置表单控件值
function setSelectSingleNode(goForm, flowProperty, arr) {
  $.each(arr, function (i, item) {
    var _node = getSelectSingleNode(flowProperty, item);
    if (_node != null && _node !== '') {
      bSetFormFieldValue(goForm, item, _node);
    }
  });
}

//获取xml表单控件值
function getSelectSingleNode(flowProperty, item) {
  var _node = flowProperty.selectSingleNode(item) != null ? flowProperty.selectSingleNode(item).text() : null;
  return _node;
}

//设置switch类型字段值
function setSwitchFieldValue(goForm, flowProperty, arr) {
  $.each(arr, function (i, item) {
    var _value = flowProperty.selectSingleNode(item) != null ? flowProperty.selectSingleNode(item).text() : null;
    if (_value) {
      goForm[item].value = _value;
      $('input[name="' + item + '"]', goForm).trigger('change');
    }
  });
}

function setSingleSwitchFieldValue(goForm, field, value) {
  if (value) {
    goForm[field].value = value;
    $('input[name="' + field + '"]:checked', goForm).trigger('change');
  }
}

function bAddEntry(poField, psText, psValue, pbNoSelect, setDefault) {
  setDefault = setDefault !== undefined ? setDefault : true;
  console.log(setDefault, 'setDefault');
  if (poField == null || psText == null || psValue == null) {
    return false;
  }
  var loEntry = new Option(psText, psValue);
  if (1 == poField.options.length && poField.options[0].text == '') {
    poField.options[0] = loEntry;
  } else {
    poField.options[poField.options.length] = loEntry;
  }
  if (!pbNoSelect && setDefault) {
    poField.selectedIndex = poField.options.length - 1;
  }
  return true;
}

function bInsertEntry(poField, psText, psValue) {
  if (poField == null || psText == null || psValue == null) {
    return false;
  }
  if (poField.selectedIndex == -1) {
    return bAddEntry(poField, psText, psValue);
  }
  var liLength = poField.options.length;
  var liIndex = poField.selectedIndex;
  poField.options[liLength] = new Option('', '');
  for (var i = liLength; i > liIndex; i--) {
    poField.options[i].text = poField.options[i - 1].text;
    poField.options[i].value = poField.options[i - 1].value;
  }
  poField.options[i].text = psText;
  poField.options[i].value = psValue;
  return true;
}

function bEditEntry(poField, psText, psValue) {
  if (poField == null || psText == null || psValue == null) {
    return false;
  }
  if (poField.length == 0) {
    return false;
  }
  if (poField.selectedIndex == -1) {
    return false;
  }
  var loEntry = new Option(psText, psValue);
  var liIndex = poField.selectedIndex;
  poField.options[liIndex].text = psText;
  poField.options[liIndex].value = psValue;
  poField.selectedIndex = liIndex;
  return true;
}

function SetUnit(target, title, goForm, ctlField, labelField, valueField) {
  //替换成新的组织弹出框
  var valueFieldName = valueField || target + '1';
  var labelFieldName = labelField || 'D' + target + '1';
  var initValues = goForm[valueFieldName].value;
  var initLabels = goForm[labelFieldName].value;
  top.$.unit2.open({
    valueField: valueFieldName,
    labelField: labelFieldName,
    title: title,
    type: 'all',
    multiple: true,
    selectTypes: 'all',
    valueFormat: 'justId',
    initValues: initValues,
    initLabels: initLabels,
    orgVersionId: goForm.orgId.value,
    v: "7.0",
    orgVersionId: getOrgVersionId(),
    callback: function (values, labels) {
      var $target = ctlField ? $('#' + ctlField) : $('#D' + target + 's');
      $target.find('.org-select').empty();
      $.each(labels, function (i, item) {
        $target.find('.org-select').append('<li class="org-entity ' + values[i][0] + '"><span class="org-label">' + item + '</span></li>');
      });
      var liLength = $target.find('.org-select li').length;
      if (liLength) {
        $target.removeClass('org-select-placeholder');
      } else {
        $target.addClass('org-select-placeholder');
      }
      goForm[labelFieldName].value = labels.join(';');
      goForm[valueFieldName].value = values.join(';');
    }
  });
}

function bFlowActions(piIndex, event, goForm, callback) {
  switch (piIndex) {
    case 1:
      SetUnit('creator', event.target.title, goForm);
      break;
    case 2:
      SetUnit('user', event.target.title, goForm);
      break;
    case 3:
      SelectUsers('unit/field/option', 'monitor', event.target.title, null, goForm);
      break;
    case 4:
      SelectUsers('unit/field/option', 'admin', event.target.title, null, goForm);
      break;
    case 5:
      bSetField('dueTime', false);
      break;
    case 6:
      SelectUsers('direction', 'beginDirections', event.target.title, null, goForm);
      break;
    case 7:
      SelectUsers('direction', 'endDirections', event.target.title, null, goForm);
      break;
    case 8:
      SelectUsers('', 'fileRecipient', event.target.title, null, goForm);
      break;
    case 9:
      SelectUsers('', 'msgRecipient', event.target.title, null, goForm);
      break;
    case 10:
      var reValCallback = function (laValue) {
        if (laValue != null && laValue.length > -1) {
          goForm.EQFlowName.value = laValue[0];
          goForm.EQFlowID.value = laValue[1];
          goWorkFlow.equalFlowID = goForm.EQFlowID.value;
        }
      };
      aSelectFlow2(event.target.name, false, event.target.title, goForm.EQFlowID.value, reValCallback);
      // aSelectFlow(false, event.target.title, goForm.EQFlowID.value,
      // reValCallback);
      break;
    case 11:
      // lsValue = sGetTimer(lsValue);
      var getTimerCallBack = function (lsValue) {
        if (lsValue == null || lsValue == '') {
          return;
        }
        bAddEntry(goForm.DTimer, lsValue, lsValue);
      };
      sGetTimer('', getTimerCallBack, goForm);
      break;
    case 12:
      var lsValue = sGetAllEntryValue(goForm.DTimer, true);
      if (lsValue == null || lsValue == '') {
        return false;
      }
      // lsValue = sGetTimer(lsValue);
      var getTimerCallBack = function (lsValue) {
        if (lsValue == null || lsValue == '') {
          return;
        }
        bEditEntry(goForm.DTimer, lsValue, lsValue);
      };
      sGetTimer(lsValue, getTimerCallBack, goForm);
      break;
    case 13:
      var lsValue = sGetAllEntryValue(goForm.DTimer, true);
      if (lsValue == null || lsValue == '') {
        return false;
      }
      bMoveEntry(goForm.DTimer);
      loNode = goWorkFlow.flowXML.selectSingleNode("./timers/timer[name='" + lsValue + "']");
      if (loNode != null) {
        loNode.remove();
      }
      break;
    case 14:
      var reValCallback = function (laValue) {
        if (laValue == null || laValue.length == 0) {
          return;
        }
        bAddEntry(goForm.DBackUser, laValue[0] + '->' + laValue[2], laValue[1] + '->' + laValue[3]);
      };
      aGetBackUser(-1, reValCallback, goForm);
      break;
    case 15:
      var lsValue = sGetAllEntryValue(goForm.DBackUser, true);
      if (lsValue == null || lsValue == '') {
        return false;
      }
      var reValCallback = function (laValue) {
        if (laValue == null || laValue.length == 0) {
          return;
        }
        bEditEntry(goForm.DBackUser, laValue[0] + '->' + laValue[2], laValue[1] + '->' + laValue[3]);
      };
      aGetBackUser(goForm.DBackUser.selectedIndex, reValCallback, goForm);
      break;
    case 16:
      bMoveEntry(goForm.DBackUser);
      break;
    case 17:
      var reValCallback = function (lsReturn) {
        if (lsReturn == null) {
          return;
        }
        /*modified by huanglinchuan 2014.10.21 begin*/
        var lsText = lsReturn.typeName + '|' + lsReturn.name + '|' + (lsReturn.isSendMsg == '1' ? '启用' : '关闭') + '|';
        if (lsReturn.condition != null && $.trim(lsReturn.condition) != '') {
          lsText = lsText + lsReturn.condition + ' | ';
        } else {
          lsText += '无  | ';
        }
        if (lsReturn.extraMsgRecipients != null && $.trim(lsReturn.extraMsgRecipients) != '') {
          lsText = lsText + lsReturn.extraMsgRecipients;
        } else {
          lsText += '无';
        }
        var $option = $('<option>' + lsText + '</option>');
        $option.data('value', lsReturn);
        $(goForm.DMessageTemplate, goForm).append($option);
        /*modified by huanglinchuan 2014.10.21 end*/
      };
      sGetMessageTemplate({}, reValCallback);
      break;
    case 18:
      var $option = $('#DMessageTemplate > option:selected', goForm);
      if ($option.length == 0) {
        return false;
      }
      // 转化为对象参数
      var lsValue = $option.data('value') || {};
      var reValCallback = function (lsReturn) {
        if (lsReturn == null) {
          return;
        }
        /*modified by huanglinchuan 2014.10.21 begin*/
        var lsText = lsReturn.typeName + '|' + lsReturn.name + '|' + (lsReturn.isSendMsg == '1' ? '启用' : '关闭') + '|';
        if (lsReturn.condition != null && $.trim(lsReturn.condition) != '') {
          lsText = lsText + lsReturn.condition + ' | ';
        } else {
          lsText += '无 | ';
        }
        if (lsReturn.extraMsgRecipients != null && $.trim(lsReturn.extraMsgRecipients) != '') {
          lsText = lsText + lsReturn.extraMsgRecipients;
        } else {
          lsText += '无';
        }
        $option.text(lsText);
        $option.data('value', lsReturn);
        /*modified by huanglinchuan 2014.10.21 end*/
      };
      sGetMessageTemplate(lsValue, reValCallback);
      break;
    case 19:
      bMoveEntry(goForm.DMessageTemplate);
      break;
    //add by huanglinchuan 2014.10.21 begin
    case 20:
      SelectUsers('', 'extraMsgRecipient', event.target.title, null, goForm);
      break;
    //add by huanglinchuan 2014.10.21 end
    /*add by huanglinchuan 2014.10.28 begin*/
    case 21:
      SetUnit('viewer', event.target.title, goForm);
      break;
    /*add by huanglinchuan 2014.10.28 end*/

    /* lmw 2015-4-23 begin */
    case 22:
      sGetRemark(
        {
          name: ''
        },
        function (lsReturn) {
          if (lsReturn == null) {
            return;
          }
          // var laValue = lsReturn.split("|");
          // bAddEntry(goForm.DRemark, laValue[1], lsReturn);
          var $option = $('<option>' + lsReturn.name + '</option>');
          $option.data('value', lsReturn);
          $(goForm.DRemark, goForm).append($option);
        }
      );
      break;
    case 23:
      var $option = $('#DRemark > option:selected', goForm);
      if ($option.length == 0) {
        return false;
      }
      // 转化为对象参数
      var lsValue = $option.data('value') || {};
      var reValCallback = function (lsReturn) {
        if (lsReturn == null) {
          return;
        }
        var lsText = lsReturn.name;
        // var laValue = lsReturn.split("|");
        // bEditEntry(goForm.DRemark, laValue[1], lsReturn);
        $option.text(lsText);
        $option.data('value', lsReturn);
      };
      sGetRemark(lsValue, reValCallback);
      break;
    case 24:
      bMoveEntry(goForm.DRemark);
      break;
    /* lmw 2015-4-23 end */
    case 25:
      // 编辑流程状态
      editFlowState(goForm);
      break;
    case 26:
      SelectUsers('', 'user', event.target.title, null, goForm, callback);
      break;
    case 27:
      SelectUsers('unit/task/field/option', 'monitor', event.target.title, null, goForm, callback);
      break;
    case 28:
      SelectUsers('', 'copyUser', event.target.title, null, goForm, callback);
      break;
    case 29:
      SelectUsers('unit/task/field/option', 'user', event.target.title, null, goForm);
      break;
    case 30:
      SelectUsers('unit/task/field/option', 'copyUser', event.target.title, null, goForm);
      break;
    case 31:
      SelectUsers('unit/task/field/option', 'branchTaskMonitor', event.target.title, null, goForm);
      break;
    case 32:
      SelectUsers('unit/task/field/option', 'Group', event.target.title, null, goForm);
      break;
    case 33:
      SelectUsers('', 'transferUser', event.target.title, null, goForm, callback);
      break;
  }
}

function bSetFormFieldValue(poForm, psName, psValues) {
  var lsValueString = ';' + psValues + ';';
  for (var i = 0; i < poForm.elements.length; i++) {
    if (poForm.elements[i].name == null || poForm.elements[i].name != psName) {
      continue;
    }
    switch (poForm.elements[i].type) {
      case 'radio':
        if (lsValueString.indexOf(';' + poForm.elements[i].value + ';') > -1) {
          poForm.elements[i].checked = true;
          if (poForm.elements[i].onclick) {
            poForm.elements[i].onclick();
          }
        }
        break;
      case 'checkbox':
        if (lsValueString.indexOf(';' + poForm.elements[i].value + ';') > -1) {
          poForm.elements[i].checked = true;
          if (poForm.elements[i].onclick) {
            poForm.elements[i].onclick();
          }
        } else {
          poForm.elements[i].checked = false;
        }
        break;
      case 'select-one':
      case 'select-multiple':
        for (var j = 0; j < poForm.elements[i].options.length; j++) {
          if (
            lsValueString.indexOf(';' + poForm.elements[i].options[j].value + ';') > -1 ||
            lsValueString.indexOf(';' + poForm.elements[i].options[j].text + ';') > -1
          ) {
            poForm.elements[i].options[j].selected = true;
          } else {
            poForm.elements[i].options[j].selected = false;
          }
        }
        if (poForm.elements[i].onchange) {
          poForm.elements[i].onchange();
        }
        break;
      case 'hidden':
      case 'text':
      case 'textarea':
        poForm.elements[i].value = psValues;
        break;
    }
  }
  return true;
}

function aGetXMLFieldValue(poXML, psFieldName) {
  var loNode = poXML.selectSingleNode(psFieldName);
  if (loNode == null) {
    return null;
  }
  var laValue = new Array();
  var laUnit = loNode.selectNodes('unit');
  if (laUnit == null) {
    return laValue;
  }
  laUnit.each(function (index) {
    laValue.push($(this).text());
  });

  if (laValue.length > 0) {
    return laValue;
  } else {
    return null;
  }
}

function sGetFormFieldValue(poForm, psName) {
  var lsValue = '';
  for (var i = 0; i < poForm.elements.length; i++) {
    if (poForm.elements[i].name == null || poForm.elements[i].name != psName) {
      continue;
    }
    switch (poForm.elements[i].type) {
      case 'radio':
        if (poForm.elements[i].checked == true) {
          lsValue = poForm.elements[i].value;
          return lsValue;
        }
        break;
      case 'checkbox':
        if (poForm.elements[i].checked == true) {
          if (lsValue == '') {
            lsValue = poForm.elements[i].value;
          } else {
            lsValue += ';' + poForm.elements[i].value;
          }
        }
        break;
      case 'select-one':
      case 'select-multiple':
        for (var j = 0; j < poForm.elements[i].options.length; j++) {
          if (poForm.elements[i].options[j].selected == true) {
            if (poForm.elements[i].options[j].value != null && poForm.elements[i].options[j].value != '') {
              if (lsValue == '') {
                lsValue = poForm.elements[i].options[j].value;
              } else {
                lsValue += ';' + poForm.elements[i].options[j].value;
              }
            } else {
              if (lsValue == '') {
                lsValue = poForm.elements[i].options[j].text;
              } else {
                lsValue += ';' + poForm.elements[i].options[j].text;
              }
            }
          }
        }
        return lsValue;
        break;
      case 'hidden':
      case 'text':
      case 'textarea':
      case 'number':
        lsValue = poForm.elements[i].value;
        return lsValue;
        break;
    }
  }
  return lsValue;
}

function setUnitXMLFieldValue(goForm, flowProperty, data) {
  $.each(data, function (i, item) {
    bGetUnitXMLToField(flowProperty, goForm, item);
  });
}

function bSetUnitFieldToXML(poXML, poForm, psFieldName, formField) {
  var _formField = formField || psFieldName;
  var loNode = poXML.selectSingleNode(psFieldName + 's');
  if (loNode != null && loNode.length) {
    poXML.removeChild(loNode);
  }
  loNode = oAddElement(poXML, psFieldName + 's');
  var loDField = poForm['D' + _formField + '1'];
  var loField = poForm[_formField + '1'];
  if (loDField != null && loDField.value !== '' && loField != null && loField.value !== '') {
    var laValue1 = loDField.value.split(';');
    var laValue2 = loField.value.split(';');
    for (var i = 0; i < laValue1.length; i++) {
      var loUnit = oAddElement(loNode, 'unit');
      loUnit.setAttribute('type', '1');
      oAddElement(loUnit, 'value', laValue2[i]);
      oAddElement(loUnit, 'argValue', laValue1[i]);
    }
  }
  var laFlag = ['2', '4', '8'];
  for (var j = 0; j < laFlag.length; j++) {
    loField = poForm[_formField + laFlag[j]];
    if (loField != null && loField.value !== '') {
      var laValue = loField.value.split(';');
      for (var i = 0; i < laValue.length; i++) {
        var loUnit = oAddElement(loNode, 'unit');
        loUnit.setAttribute('type', laFlag[j]);
        oAddElement(loUnit, 'value', laValue[i]);
      }
    }
  }
  loDField = poForm['D' + _formField + '16'];
  loField = poForm[_formField + '16'];
  if (loDField != null && loDField.value !== '' && loField != null && loField.value !== '') {
    var loUnit = oAddElement(loNode, 'unit');
    loUnit.setAttribute('type', '16');
    oAddElement(loUnit, 'value', loField.value);
    oAddElement(loUnit, 'argValue', loDField.value);
  }
  return true;
}

function bGetUnitXMLToField(poXML, poForm, psFieldName) {
  var loNode = poXML.selectSingleNode(psFieldName + 's');
  if (loNode == null) {
    return false;
  }
  var laUnit = loNode.selectNodes('unit');
  if (laUnit == null || laUnit.length === 0) {
    return false;
  }
  var laAllValue = new Array();
  var laDValue1 = new Array();
  var laValue1 = new Array();
  var laValue2 = new Array();
  var laValue4 = new Array();
  var laValue8 = new Array();
  var laDValue16 = new Array();
  var laValue16 = new Array();
  for (var i = 0; i < laUnit.length; i++) {
    switch (laUnit[i].getAttribute('type')) {
      case '1':
        laDValue1.push($(laUnit[i]).selectSingleNode('argValue').text());
        laValue1.push($(laUnit[i]).selectSingleNode('value').text());
        laAllValue.push($(laUnit[i]).selectSingleNode('argValue').text());
        break;
      case '2':
        var lsValue = $(laUnit[i]).selectSingleNode('value').text();
        laValue2.push(lsValue);
        laAllValue.push('{' + lsValue + '}');
        break;
      case '4':
        var lsValue = $(laUnit[i]).selectSingleNode('value').text();
        laValue4.push(lsValue);
        lsValue = sGetTaskNameByID(lsValue);
        laAllValue.push('<' + lsValue + '>');
        break;
      case '8':
        var lsValue = $(laUnit[i]).selectSingleNode('value').text();
        laValue8.push(lsValue);
        laAllValue.push('[' + lsValue + ']');
        break;
      case '16':
        laDValue16.push($(laUnit[i]).selectSingleNode('argValue').text());
        laValue16.push($(laUnit[i]).selectSingleNode('value').text());
        laAllValue.push($(laUnit[i]).selectSingleNode('argValue').text());
        break;
    }
  }

  var unitAllData = {
    unitLabel: laDValue1,
    unitValue: laValue1,
    formField: laValue2,
    tasks: laValue4,
    options: laValue8,
    userCustom: laValue16
  };
  renderUnitField(unitAllData, poForm, psFieldName);
  return true;
}

function renderUnitField(unitAllData, poForm, psFieldName) {
  var allValueText = [];
  $('#D' + psFieldName + 's', poForm)
    .find('.org-select')
    .empty();
  if (unitAllData.unitValue.length) {
    $('#' + psFieldName + '_custom', poForm)
      .attr('checked', 'checked')
      .trigger('change');
  } else {
    $('#' + psFieldName + '_default', poForm)
      .attr('checked', 'checked')
      .trigger('change');
  }

  var loField = poForm['D' + psFieldName + '1'];
  if (loField != null) {
    loField.value = unitAllData.unitLabel.join(';');
    $.each(unitAllData.unitLabel, function (i, item) {
      allValueText.push(item);
    });
  }
  loField = poForm[psFieldName + '1'];
  if (loField != null) {
    loField.value = unitAllData.unitValue.join(';');
  }
  //组织机构
  $.each(unitAllData.unitLabel, function (i, item) {
    $('#D' + psFieldName + 's', poForm)
      .find('.org-select')
      .append('<li class="org-entity ' + unitAllData.unitValue[i][0] + '"><span class="org-label">' + item + '</span></li>');
  });
  loField = poForm[psFieldName + '2'];
  if (loField != null) {
    loField.value = unitAllData.formField.join(';');
    $.each(unitAllData.formField, function (i, item) {
      allValueText.push('{' + item + '}');
    });
  }
  //表单字段
  $.each(unitAllData.formField, function (i, item) {
    var itemArr = item.split(':');
    var showName = '';
    if (itemArr.length === 1) {
      showName = formFieldFormat(itemArr[0]);
    } else {
      showName = formFieldFormat(itemArr[1], itemArr[0]);
    }
    if (showName) {
      $('#D' + psFieldName + 's', poForm)
        .find('.org-select')
        .append('<li class="org-entity no-icon"><span class="org-label">' + showName + '</span></li>');
    }
  });

  loField = poForm[psFieldName + '4'];
  if (loField != null) {
    loField.value = unitAllData.tasks.join(';');
    $.each(unitAllData.tasks, function (i, item) {
      allValueText.push('<' + item + '>');
    });
  }
  //环节
  $.each(unitAllData.tasks, function (i, item) {
    $.each(goWorkFlow.tasks, function (i, item2) {
      if (item2.xmlObject.context.id === item) {
        $('#D' + psFieldName + 's', poForm)
          .find('.org-select')
          .append('<li class="org-entity no-icon"><span class="org-label">' + item2.htmlObject.textContent + '</span></li>');
      }
    });
  });

  loField = poForm[psFieldName + '8'];
  if (loField != null) {
    loField.value = unitAllData.options.join(';');
    $.each(unitAllData.options, function (i, item) {
      allValueText.push('[' + item + ']');
    });
  }
  //选项
  $.each(unitAllData.options, function (i, item) {
    $('#D' + psFieldName + 's', poForm)
      .find('.org-select')
      .append('<li class="org-entity no-icon"><span class="org-label">' + handlerListAll[item] + '</span></li>');
  });

  $('#D' + psFieldName + 's', poForm).data('allValueText', allValueText.join(';'));

  //自定义
  loField = poForm['D' + psFieldName + '16'];
  if (loField != null) {
    loField.value = unitAllData.userCustom && unitAllData.userCustom.length > 0 ? unitAllData.userCustom[0] : '';
    if (loField.value !== '') {
      $('#D' + psFieldName + 's', poForm)
        .find('.org-select')
        .append('<li class="org-entity no-icon"><span class="org-label">' + loField.value + '</span></li>');
    }
  }
  loField = poForm[psFieldName + '16'];
  if (loField != null) {
    loField.value = unitAllData.userCustom && unitAllData.userCustom.length > 0 ? unitAllData.userCustom[1] : '';
  }

  var liLength = $('#D' + psFieldName + 's', poForm).find('.org-select li').length;
  if (liLength) {
    $('#D' + psFieldName + 's', poForm).removeClass('org-select-placeholder');
    $('#D' + psFieldName + 's', poForm)
      .find('.org-select')
      .removeClass('control-error');
  } else {
    $('#D' + psFieldName + 's', poForm).addClass('org-select-placeholder');
  }
}

function showHandlerDialog(title, customTitle, data, psTarget, goForm, callback) {
  var html = initHandleDialog();
  var $Dialog = top.appModal.dialog({
    title: title,
    message: html,
    size: 'large',
    height: '600',
    shown: function () {
      SelectUserLoadEvent($Dialog, data);
      SelectUserInitEvent($Dialog, data, customTitle);
    },
    buttons: {
      ok: {
        label: '确定',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var laReturn = SelectUserOKEvent($('#userSelectForm', $Dialog)[0], data);
          renderUnitField(laReturn, goForm, psTarget);
          if (callback) {
            callback();
          }
        }
      },
      clear: {
        label: '清空',
        className: 'well-btn w-btn-primary',
        callback: function () {
          $('#DUnits', $Dialog).val('');
          $('#Units', $Dialog).val('');
          $('.org-select', $Dialog).empty();
          // $("#D"+psTarget + "s").empty().removeClass("work-flow-handler-list");
          var ztree = top.$.fn.zTree.getZTreeObj('FieldLabels_ztree');
          if (ztree) {
            $.each(ztree.getCheckedNodes(), function (i, item) {
              ztree.checkNode(item, false, true, true);
            });
          }
          $('#FieldLabels', $Dialog).val('');
          $('#Fields', $Dialog).val('');
          $('#display_FieldLabels .well-tag-list', $Dialog).empty().next().show();
          $('#TasksValue', $Dialog).wellSelect('val', '');
          $('#DUserCustoms', $Dialog).val('');
          $('#UserCustoms', $Dialog).val('');
          $('[name="UserOptions"]', $Dialog).attr('checked', false);
          return false;
        }
      },
      cancel: {
        label: '取消',
        className: 'btn-default',
        callback: function () { }
      }
    }
  });
}

function initHandleDialog() {
  var html = '';
  var handlerListHtml = '';
  $.each(handlerList, function (i, item) {
    var listHtml = '<div class="list-vertical">';
    $.each(item.list, function (j, li) {
      listHtml +=
        "<input type='checkbox' id='" +
        li.id +
        "' name='UserOptions' value='" +
        li.value +
        "'><label for='" +
        li.id +
        "'>" +
        li.name +
        '</label>';
    });
    listHtml += '</div>';
    handlerListHtml += listHtml;
  });

  var handlerFilterListHtml = '';
  $.each(handlerFilterList, function (i, item) {
    var listHtml = '<div class="list-vertical">';
    $.each(item.list, function (j, li) {
      listHtml +=
        "<input type='checkbox' id='" +
        li.id +
        "' name='UserOptions' value='" +
        li.value +
        "'><label for='" +
        li.id +
        "'>" +
        li.name +
        '</label>';
    });
    listHtml += '</div>';
    handlerFilterListHtml += listHtml;
  });

  html +=
    '<form id="userSelectForm" name="userSelectForm" class="workflow-popup form-widget">' +
    '<div class="well-form form-horizontal">' +
    '<div id="ID_Unit" class="form-group">' +
    '<label for="ruleName" class="well-form-label control-label">组织机构</label>' +
    '<div class="well-form-control">' +
    '<input type="hidden" class="form-control" id="DUnits" name="DUnits">' +
    '<input type="hidden" class="form-control" id="Units" name="Units">' +
    '<div class="org-select-container org-style3" id="Units_control">' +
    '<ul class="org-select"></ul>' +
    '<i class="icon iconfont icon-ptkj-zuzhixuanze"></i>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div id="ID_Field" class="form-group">' +
    '<label for="fieldName" class="well-form-label control-label">表单字段</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="FieldLabels" name="FieldLabels">' +
    '<input type="hidden" id="Fields" name="Fields">' +
    '</div>' +
    '</div>' +
    '<div id="ID_Task" class="form-group">' +
    '<label for="operator" class="well-form-label control-label">办理环节</label>' +
    '<div class="well-form-control">' +
    '<input type="text" id="TasksValue" name="TasksValue"/>' +
    '<input type="hidden" id="TasksLabel" name="TasksLabel"/>' +
    '</div>' +
    '</div>' +
    // '<div id="ID_Direction" class="form-group">' +
    // '<label for="fieldName" class="well-form-label control-label">工作流向</label>' +
    // '<div class="well-form-control">' +
    // '<input type="text" class="form-control" id="FieldLabels" name="FieldLabels">' +
    // '<input type="hidden" id="Fields" name="Fields">' +
    // '</div>' +
    // '</div>' +
    '<div id="ID_Option_0" class="form-group">' +
    '<label for="fieldValue" class="well-form-label control-label">人员选项</label>' +
    '<div class="well-form-control">' +
    handlerListHtml +
    '</div>' +
    '</div>' +
    '<div id="ID_Option_1" class="form-group">' +
    '<label for="ruleConnector" class="well-form-label control-label">人员过滤</label>' +
    '<div class="well-form-control">' +
    handlerFilterListHtml +
    '</div>' +
    '</div>' +
    '<div id="ID_User_Custom" class="form-group">' +
    '<label for="DUserCustoms" class="well-form-label control-label">自定义</label>' +
    '<div class="well-form-control">' +
    '<textarea name="DUserCustoms" id="DUserCustoms" type="text" class="form-control"></textarea>' +
    '<input name="UserCustoms" id="UserCustoms" type="hidden" class="form-control"/>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</form>';
  return html;
}

// 设置自定义弹窗
function showUserCustomDialog($handlerDialog, data, title) {
  var html = initUserCustomDialogHtml();
  var $userCustomDialog = top.appModal.dialog({
    title: title,
    message: html,
    size: 'large',
    height: '600',
    shown: function () {
      iniWellSelect($userCustomDialog, selectDataList);
      initUserCustomEvent($userCustomDialog, data);
    },
    buttons: {
      ok: {
        label: '确定',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var expressionList = $('#expressionSelect', $userCustomDialog).find('div');
          var setExpressionList = [];
          var DUserCustoms = [];
          $.each(expressionList, function (index, item) {
            DUserCustoms.push($(item).text());
            var data = $(item).data('data');
            data.order = index;
            setExpressionList.push(data);
          });
          var expressionObject = {};
          expressionObject.expressionConfigs = setExpressionList;
          var configValue = JSON.stringify(expressionObject);
          var hasError = false;
          // 检测表达式
          $.get({
            url: ctx + '/api/workflow/definition/checkUserCustomExpression',
            data: {
              expressionConfig: configValue
            },
            async: false,
            success: function (result) {
              hasError = result.data;
            }
          });
          if (!hasError) {
            top.appModal.error('表达式语法有误!');
            return false;
          }
          $('#DUserCustoms', $handlerDialog).val(DUserCustoms.join(' '));
          $('#UserCustoms', $handlerDialog).val(JSON.stringify(expressionObject));
        }
      },
      cancel: {
        label: '取消',
        className: 'btn-default',
        callback: function () { }
      }
    }
  });
}

function iniWellSelect($userCustomDialog, selectDataList) {
  $.each(selectDataList, function (i, item) {
    $('#' + item.valueField, $userCustomDialog).wellSelect(item);
  });
}

var selectDataList = [
  {
    valueField: 'setOperation',
    data: [
      {
        id: '∪',
        text: '并'
      },
      {
        id: '∩',
        text: '交'
      }
    ],
    searchable: false
  },
  {
    valueField: 'leftBracket_1',
    data: [
      {
        id: '(',
        text: '('
      }
    ],
    searchable: false
  },
  {
    valueField: 'userType',
    data: [
      {
        id: 'Unit',
        text: '组织机构'
      },
      {
        id: 'FormField',
        text: '文档域'
      },
      {
        id: 'TaskHistory',
        text: '办理环节'
      },
      {
        id: 'Option',
        text: '可选项'
      },
      {
        id: 'Interface',
        text: '接口实现'
      }
    ],
    searchable: false
  },
  {
    valueField: 'orgVersionType',
    data: [
      {
        id: '1',
        text: '现在确定'
      },
      {
        id: '2',
        text: '取文档域'
      }
    ],
    searchable: false
  },
  {
    valueField: 'orgVersionId_1',
    data: [],
    searchable: false
  },
  {
    valueField: 'orgVersionId_2',
    data: [],
    searchable: false
  },
  {
    valueField: 'optionOf',
    data: [
      {
        id: 'LeaderOf',
        text: '直接领导'
      },
      {
        id: 'DeptLeaderOf',
        text: '部门领导'
      },
      {
        id: 'BranchedLeaderOf',
        text: '分管领导'
      },
      {
        id: 'AllLeaderOf',
        text: '所有领导'
      },
      {
        id: 'DeptOf',
        text: '部门'
      },
      {
        id: 'ParentDeptOf',
        text: '上级部门'
      },
      {
        id: 'RootDeptOf',
        text: '根部门'
      },
      {
        id: 'SameDeptOf',
        text: '同一直接部门人员'
      },
      {
        id: 'SameRootDeptOf',
        text: '同一根部门人员'
      }
    ],
    searchable: false
  },
  {
    valueField: 'rightBracket_1',
    data: [
      {
        id: ')',
        text: ')'
      }
    ],
    searchable: false
  }
];

// 设置自定义弹窗 html
function initUserCustomDialogHtml() {
  var html = '';
  html +=
    '<form id="workFlowHandler" class="workflow-popup form-widget">' +
    '<div class="well-form form-horizontal">' +
    '<div class="form-group">' +
    '<label for="setOperation" class="well-form-label control-label">组合关系</label>' +
    '<div class="well-form-control" style="width:150px;">' +
    '<input class="form-control" id="setOperation" name="setOperation" type="text"/>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="fieldName" class="well-form-label control-label">承办人</label>' +
    '<div class="well-form-control">' +
    '<div class="combineSelect">' +
    '<input class="form-control" id="leftBracket_1" name="leftBracket_1" type="text"/>' +
    '</div>' +
    '<label style="font-size: 14px;font-weight: 400;color: #666; margin-right: 8px">人员选择</label>' +
    '<div class="combineSelect">' +
    '<input id="userType" name="userType" type="text"/>' +
    '</div>' +
    '<label style="font-size: 14px;font-weight: 400;color: #666; margin-right: 8px">组织版本</label>' +
    '<div class="combineSelect">' +
    '<input id="orgVersionType" name="orgVersionType" type="text"/>' +
    '</div>' +
    '<div  class="combineSelect">' +
    '<input id="orgVersionId_1" type="text" name="orgVersionId_1"/>' +
    '</div>' +
    '<div class="combineSelect" style="display: none;">' +
    '<input id="orgVersionId_2" type="text" name="orgVersionId_2" />' +
    '</div>' +
    '<div  class="combineSelect">' +
    '<input id="optionOf" name="optionOf" type="text"/>' +
    '</div>' +
    '<div class="combineSelect">' +
    '<input id="rightBracket_1" type="text" name="rightBracket_1" />' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group empty_block userType-item userType_">' +
    '<label for="emptyBlock" class="well-form-label control-label no-suffix"></label>' +
    '<div class="well-form-control">' +
    '<textarea type="text" id="emptyBlock" name="emptyBlock" class="form-control"></textarea>' +
    '</div>' +
    '</div>' +
    '<div class="form-group tr_unit userType-item userType_Unit" style="display: none">' +
    '<label for="unitName" class="well-form-label control-label no-suffix"></label>' +
    '<div class="well-form-control">' +
    '<textarea type="text" id="unitName" name="unitName" class="form-control"></textarea>' +
    '<input type="hidden" id="unitId" name="unitId"/>' +
    '</div>' +
    '</div>' +
    '<div class="form-group tr_form_field userType-item userType_FormField" style="display: none">' +
    '<label for="formFieldName" class="well-form-label control-label no-suffix"></label>' +
    '<div class="well-form-control">' +
    '<textarea type="text" id="formFieldName" name="formFieldName"></textarea>' +
    '<input type="hidden" id="formFieldId" name="formFieldId"/>' +
    '</div>' +
    '</div>' +
    '<div class="form-group tr_task_history userType-item userType_TaskHistory" style="display: none">' +
    '<label for="taskHistoryName" class="well-form-label control-label no-suffix"></label>' +
    '<div class="well-form-control">' +
    '<textarea type="text" id="taskHistoryName" name="taskHistoryName"></textarea>' +
    '<input type="hidden" id="taskHistory" name="taskHistory"/>' +
    '</div>' +
    '</div>' +
    '<div class="form-group tr_option userType-item userType_Option" style="display: none">' +
    '<label for="optionUserName" class="well-form-label control-label no-suffix"></label>' +
    '<div class="well-form-control">' +
    '<textarea type="text" id="optionUserName" name="optionUserName"></textarea>' +
    '<input type="hidden" id="optionUser" name="optionUser"/>' +
    '</div>' +
    '</div>' +
    '<div class="form-group tr_interface userType-item userType_Interface" style="display: none">' +
    '<label for="emptyBlock" class="well-form-label control-label no-suffix"></label>' +
    '<div class="well-form-control">' +
    '<textarea type="text" id="interfaceName" name="interfaceName"></textarea>' +
    '<input type="hidden" id="interfaceValue" name="interfaceValue"/>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="fieldValue" class="well-form-label control-label no-suffix"></label>' +
    '<div class="well-form-control">' +
    '<button id="btn_expression_add" type="button" class="well-btn w-btn-primary">添加</button>' +
    '<button id="btn_expression_update" type="button" class="well-btn w-btn-primary w-disable-btn">更新</button>' +
    '<button id="btn_expression_del" type="button" class="well-btn w-btn-danger">删除</button>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label no-suffix"></label>' +
    '<div class="well-form-control" >' +
    '<div class="expressionSelect" id="expressionSelect"></div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="set_expression" class="well-form-label control-label">表达式</label>' +
    '<div class="well-form-control">' +
    '<textarea name="set_expression" id="set_expression" type="text" class="form-control" readonly></textarea>' +
    '<div>多个表达式用组合关系连接。</div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</form>';
  return html;
}

function initUserCustomEvent($userCustomDialog, data) {
  var UserCustom = UserCustom || {};
  UserCustom.init = function (configName, configValue) {
    if (StringUtils.isNotBlank(configValue)) {
      var expressionObject = JSON.parse(configValue);
      if (expressionObject.expressionConfigs) {
        $.each(expressionObject.expressionConfigs, function (index) {
          var expStr = expDataToDisplayName(this);
          var option = $("<div data-value='" + expStr + "'>" + expStr + '</div>');
          $(option).attr('data-data', JSON.stringify(this));
          $('#expressionSelect', $userCustomDialog).append(option);
        });
      }
      // 显示集合表达式
      displaySetExpression($userCustomDialog);
    }
  };
  UserCustom.init(data[0], data[1]);

  $('#userType', $userCustomDialog)
    .off()
    .on('change', function () {
      var type = $(this).val();
      $('.userType-item', $userCustomDialog).hide();
      $('.userType_' + type, $userCustomDialog).show();
    });

  $('#orgVersionType', $userCustomDialog)
    .off()
    .on('change', function () {
      var orgVersionType = $(this).val();
      $('#orgVersionId_1', $userCustomDialog).parent().hide();
      $('#orgVersionId_2', $userCustomDialog).parent().hide();
      if (orgVersionType === '1') {
        $('#orgVersionId_1', $userCustomDialog).parent().css({
          display: 'inline-block'
        });
      } else if (orgVersionType === '2') {
        $('#orgVersionId_2', $userCustomDialog).parent().css({
          display: 'inline-block'
        });
      }
    });
  // 组织版本现在确定
  $.get({
    url: ctx + '/api/workflow/definition/getCurrentUserUnitOrgVersions',
    success: function (result) {
      var data = result.data;
      var wellData = [];
      $.each(data, function () {
        wellData.push({
          id: this.id,
          text: this.name
        });
      });
      $('#orgVersionId_1', $userCustomDialog).wellSelect({
        data: wellData,
        valueField: 'orgVersionId_1',
        searchable: false
      });
    }
  });
  // 组织版本文档域
  $.get({
    url: ctx + '/api/workflow/definition/getFormFields',
    data: {
      formUuid: bGetForm()
    },
    success: function (result) {
      var data = result.data;
      var wellData = [];
      $.each(data, function () {
        wellData.push({
          id: this.id,
          text: this.name
        });
      });
      $('#orgVersionId_2', $userCustomDialog).wellSelect({
        data: wellData,
        valueField: 'orgVersionId_2',
        searchable: false
      });
    }
  });
  // 组织机构
  $('#unitName', $userCustomDialog)
    .off()
    .on('click', function () {
      top.$.unit2.open({
        valueField: 'unitId',
        labelField: 'unitName',
        title: '人员选择',
        type: 'all',
        multiple: true,
        selectTypes: 'all',
        v: "7.0",
        orgVersionId: getOrgVersionId(),
      });
    });
  // 表单域
  var formFieldSetting = {
    async: {
      otherParam: {
        serviceName: 'flowSchemeService',
        methodName: 'getFormFields',
        data: ['-1', bGetForm()]
      }
    }
  };
  $('#formFieldName', $userCustomDialog).comboTree({
    labelField: 'formFieldName',
    valueField: 'formFieldId',
    initService: 'flowSchemeService.getFormFieldByFieldNames',
    initServiceParam: [bGetForm()],
    treeSetting: formFieldSetting,
    width: 520,
    height: 220
  });
  var hisTasks = aGetTasks('TASK', null, false);
  var hisTasksMap = {};
  if (hisTasks != null && hisTasks.length > 0) {
    for (var index = 0; index < hisTasks.length; index++) {
      var hisTask = hisTasks[index];
      if (hisTask != null) {
        var tasks = hisTask.split('|');
        if (tasks.length == 2) {
          hisTasksMap[tasks[1]] = tasks[0];
        }
      }
    }
  }
  var hisTaskSetting = {
    async: {
      otherParam: {
        serviceName: 'flowSchemeService',
        methodName: 'getTaskUserTaskHistorys',
        data: [null, hisTasksMap]
      }
    }
  };
  $('#taskHistoryName', $userCustomDialog).comboTree({
    labelField: 'taskHistoryName',
    valueField: 'taskHistory',
    treeSetting: hisTaskSetting,
    width: 520,
    height: 220
  });
  // 人员可选项
  var optionUserSetting = {
    async: {
      otherParam: {
        serviceName: 'flowSchemeService',
        methodName: 'getTaskUserOptionUsers'
      }
    }
  };
  $('#optionUserName', $userCustomDialog).comboTree({
    labelField: 'optionUserName',
    valueField: 'optionUser',
    treeSetting: optionUserSetting,
    width: 520,
    height: 220
  });
  // 接口实现
  var interfaceSetting = {
    async: {
      otherParam: {
        serviceName: 'flowSchemeService',
        methodName: 'getTaskUserCustomInterfaces'
      }
    }
  };
  $('#interfaceName', $userCustomDialog).comboTree({
    labelField: 'interfaceName',
    valueField: 'interfaceValue',
    treeSetting: interfaceSetting,
    width: 520,
    height: 220
  });
  $('#btn_expression_add', $userCustomDialog)
    .off()
    .on('click', function (e) {
      addOrUpdate(e);
    });

  // 添加
  function addOrUpdate(e, $updateOption) {
    var typeName = $('#userType', $userCustomDialog).wellSelect('data').text;
    var type = $('#userType', $userCustomDialog).val();
    var name = null;
    var value = null;
    if (type === 'Unit') {
      name = $('textarea[name=unitName]', $userCustomDialog).val();
      value = $('input[name=unitId]', $userCustomDialog).val();
    } else if (type === 'FormField') {
      name = $('textarea[name=formFieldName]', $userCustomDialog).val();
      value = $('input[name=formFieldId]', $userCustomDialog).val();
    } else if (type === 'TaskHistory') {
      name = $('#taskHistoryName', $userCustomDialog).val();
      value = $('#taskHistory', $userCustomDialog).val();
    } else if (type === 'Option') {
      name = $('#optionUserName', $userCustomDialog).val();
      value = $('#optionUser', $userCustomDialog).val();
    } else if (type === 'Interface') {
      name = $('#interfaceName', $userCustomDialog).val();
      value = $('#interfaceValue', $userCustomDialog).val();
    }
    var leftBracket = $('#leftBracket_1', $userCustomDialog).val();
    var userTypeName = typeName;
    var userType = type;
    var userName = name;
    var userValue = value;
    var orgVersionType = $('#orgVersionType', $userCustomDialog).val();
    var orgVersionId = '';
    var orgVersionName = '';
    if (orgVersionType == '1') {
      orgVersionId = $('#orgVersionId_1', $userCustomDialog).val();
      orgVersionName = $('#orgVersionId_1', $userCustomDialog).wellSelect('data').text;
    } else if (orgVersionType == '2') {
      orgVersionId = $('#orgVersionId_2', $userCustomDialog).val();
      orgVersionName = '文档域' + $('#orgVersionId_2', $userCustomDialog).wellSelect('data').text;
    }
    var optionOfName = $('#optionOf', $userCustomDialog).wellSelect('data').text;
    var optionOf = $('#optionOf', $userCustomDialog).val();
    var setOperationName = $('#setOperation', $userCustomDialog).wellSelect('data').text;
    var setOperation = $('#setOperation', $userCustomDialog).val();
    var rightBracket = $('#rightBracket_1', $userCustomDialog).val();

    // 生成显示值
    var expData = {};
    expData.leftBracket = leftBracket;
    expData.userTypeName = userTypeName;
    expData.userType = userType;
    expData.userName = userName;
    expData.userValue = userValue;
    expData.orgVersionType = orgVersionType;
    expData.orgVersionName = orgVersionName;
    expData.orgVersionId = orgVersionId;
    expData.optionOfName = optionOfName;
    expData.optionOf = optionOf;
    expData.setOperationName = setOperationName;
    expData.setOperation = setOperation;
    expData.rightBracket = rightBracket;
    var expStr = expDataToDisplayName(expData);

    // 空表达式
    if ($.trim(expStr) === '') {
      appModal.error('空表达式!');
      return;
    }

    if ($updateOption != null) {
      $updateOption.val(expStr);
      $updateOption.text(expStr);
      $('#expressionSelect', $userCustomDialog).find('div.active').removeAttr('data-data');
      $('#expressionSelect', $userCustomDialog).find('div.active').attr('data-data', JSON.stringify(expData));
    } else {
      var option = $("<div data-value='" + expStr + "' >" + expStr + '</div>');
      $(option).attr('data-data', JSON.stringify(expData));
      if ($('#expressionSelect', $userCustomDialog).find('div.active').length > 0) {
        $('#expressionSelect', $userCustomDialog).find('div.active').after(option);
      } else {
        $('#expressionSelect', $userCustomDialog).append(option);
      }
    }

    // 显示集合表达式
    displaySetExpression($userCustomDialog);
  }

  // 显示集合表达式
  function displaySetExpression($userCustomDialog) {
    var expressions = [];
    var newOptions = $('#expressionSelect', $userCustomDialog).find('div');
    $.each(newOptions, function (index, item) {
      var data = JSON.parse($(item).attr('data-data'));
      data.order = index;
      expressions.push(data);
    });
    var newExpressionObject = {};
    newExpressionObject.expressionConfigs = expressions;
    var configValue = JSON.stringify(newExpressionObject);
    // 检测表达式
    $.get({
      url: ctx + '/api/workflow/definition/getUserCustomExpression',
      data: {
        expressionConfig: configValue
      },
      async: false,
      success: function (result) {
        $('#set_expression', $userCustomDialog).val(result.data);
      }
    });
  }

  // 编辑表达式
  $($userCustomDialog)
    .off()
    .on('click', '#expressionSelect div', function () {
      var $option = $(this);
      $(this).addClass('active').siblings().removeClass('active');
      if ($option.length > 1) {
        top.appModal.error('只能选择一条记录!');
        $('#btn_expression_update', $userCustomDialog).prop('disabled', 'disabled');
        return false;
      }
      $('#btn_expression_update', $userCustomDialog).removeClass('w-disable-btn');
      $('#btn_expression_update', $userCustomDialog).prop('disabled', '');

      var expData = JSON.parse($option.attr('data-data'));
      $('#leftBracket_1', $userCustomDialog).wellSelect('val', expData.leftBracket).trigger('change');
      $('#userType', $userCustomDialog).wellSelect('val', expData.userType).trigger('change');
      $('#orgVersionType', $userCustomDialog).wellSelect('val', expData.orgVersionType).trigger('change');
      if (expData.orgVersionType == '1') {
        $('#orgVersionId_1', $userCustomDialog).wellSelect('val', expData.orgVersionId).trigger('change');
      } else if (expData.orgVersionType == '2') {
        $('#orgVersionId_2', $userCustomDialog).wellSelect('val', expData.orgVersionId).trigger('change');
      }
      $('#orgVersionType', $userCustomDialog).wellSelect('val', expData.orgVersionType).trigger('change');
      $('#optionOf', $userCustomDialog).wellSelect('val', expData.optionOf).trigger('change');
      $('#rightBracket_1', $userCustomDialog).wellSelect('val', expData.rightBracket).trigger('change');
      $('#setOperation', $userCustomDialog).wellSelect('val', expData.setOperation).trigger('change');

      var type = expData.userType;
      var name = expData.userName;
      var value = expData.userValue;
      if (type === 'Unit') {
        $('textarea[name=unitName]', $userCustomDialog).val(name).trigger('change');
        $('input[name=unitId]', $userCustomDialog).val(value).trigger('change');
      } else if (type === 'FormField') {
        $('textarea[name=formFieldName]', $userCustomDialog).val(name).trigger('change');
        $('input[name=formFieldId]', $userCustomDialog).val(value).trigger('change');
      } else if (type === 'TaskHistory') {
        $('#taskHistoryName', $userCustomDialog).val(name).trigger('change');
        $('#taskHistory', $userCustomDialog).val(value).trigger('change');
      } else if (type === 'Option') {
        $('#optionUserName', $userCustomDialog).val(name).trigger('change');
        $('#optionUser', $userCustomDialog).val(value).trigger('change');
      } else if (type === 'Interface') {
        $('#interfaceName', $userCustomDialog).val(name).trigger('change');
        $('#interfaceValue', $userCustomDialog).val(value).trigger('change');
      }

      $('#userType', $userCustomDialog).trigger('change');
    });
  // 更新表达式
  $('#btn_expression_update', $userCustomDialog)
    .off()
    .on('click', function (e) {
      var $option = $('#expressionSelect', $userCustomDialog).find('div.active');
      if ($option.length != 1) {
        return false;
      }
      addOrUpdate(e, $option);
    });
  // 删除表达式
  $('#btn_expression_del', $userCustomDialog).on('click', function () {
    if ($('#expressionSelect', $userCustomDialog).find('div.active').length == 0) {
      top.appModal.error('请先选择一条记录！');
    }
    $('#expressionSelect', $userCustomDialog).find('div.active').remove();

    // 显示集合表达式
    displaySetExpression($userCustomDialog);
  });

  function isMultiExp() {
    var $options = $('#expressionSelect').find('option');
    for (var i = 0; i < $options.length; i++) {
      if ($.trim($($options[i]).text()).length > 1) {
        return true;
      }
    }

    return false;
  }

  function expDataToDisplayName(json) {
    var leftBracket = json.leftBracket;
    var userTypeName = json.userTypeName;
    var userType = json.userType;
    var userName = json.userName;
    var userValue = json.userValue;
    var orgVersionType = json.orgVersionType;
    var orgVersionId = json.orgVersionId;
    var orgVersionName = json.orgVersionName;
    var optionOfName = json.optionOfName;
    var optionOf = json.optionOf;
    var setOperationName = json.setOperationName;
    var rightBracket = json.rightBracket;
    var expStr = '';
    expStr = setOperationName + ' ' + leftBracket;
    if (orgVersionName != null && $.trim(orgVersionName) != '') {
      expStr += orgVersionName + '的';
    }
    if (userName != null && $.trim(userName) != '') {
      expStr += userTypeName + '(' + userName + ')';
    }
    if (optionOfName != null && $.trim(optionOfName) != '') {
      expStr += '的' + optionOfName;
    }
    expStr += rightBracket;
    return expStr;
  }
}

function SelectUsers(psStyle, psTarget, psTitle, pbSingle, goForm, checkAfterSelect, customTitle) {
  //弹窗包含的字段
  var lsStyle = psStyle || 'unit/task/field/option/custom';
  var laArg = [];
  laArg.Style = lsStyle;
  laArg.Title = psTitle;
  var laExistTask = null;
  if (lsStyle.indexOf('task') > -1) {
    var lsID = goForm.id ? goForm.id.value : '';
    laExistTask = aGetTasks(
      lsStyle.indexOf('alltask') > -1 ? 'TASK/SUBFLOW' : '',
      lsStyle.indexOf('nocurtask') > -1 ? lsID : null,
      lsStyle.indexOf('alltask') > -1 ? true : null
    );
    laArg.Tasks = laExistTask;
    laArg.MulTask = pbSingle ? false : true;
  }
  var laDirection = null;
  if (lsStyle.indexOf('direction') > -1) {
    laDirection = aGetDirections();
    laArg.Directions = laDirection;
  }
  //收集表单组织机构值
  if (lsStyle.indexOf('unit') > -1) {
    var laValue = [];
    var loField = goForm['D' + psTarget + '1'];
    laValue.push(loField.value || '');
    loField = goForm[psTarget + '1'];
    laValue.push(loField.value || '');
    laArg.unit = laValue;
  }
  //收集表单字段值
  if (lsStyle.indexOf('field') > -1) {
    var loField = goForm[psTarget + '2'];
    laArg.field = loField.value || '';
  }
  //收集表单办理环节值
  if (lsStyle.indexOf('task') > -1) {
    var loField = goForm[psTarget + (lsStyle.indexOf('unit') > -1 ? '4' : '')];
    laArg.task = loField.value || '';
  }
  //收集表单选项值
  if (lsStyle.indexOf('option') > -1) {
    var loField = goForm[psTarget + '8'];
    laArg.option = loField.value || '';
  }
  //收集表单自定义值
  if (lsStyle.indexOf('custom') > -1) {
    var laValue = [];
    var loField = goForm['D' + psTarget + '16'];
    laValue.push(loField.value || '');
    loField = goForm[psTarget + '16'];
    laValue.push(loField.value || '');
    laArg.userCustom = laValue;
  }
  //收集表单流向值
  if (lsStyle.indexOf('direction') > -1) {
    var loField = goForm[psTarget];
    laArg.direction = loField.value || '';
  }

  //回填表单各字段值
  var reValCallback = function (laValue) {
    if (laValue == null) {
      return;
    }
    var laDValue = [];
    if (lsStyle.indexOf('unit') > -1) {
      var loField = goForm['D' + psTarget + '1'];
      if (loField) {
        loField.value = laValue.unit[0];
      }
      loField = goForm[psTarget + '1'];
      if (loField) {
        loField.value = laValue.unit[1];
      }
      if (laValue.unit[0] > -1) {
        laDValue.push(laValue.unit[0]);
      }
    }
    if (lsStyle.indexOf('field') > -1) {
      var loField = goForm[psTarget + '2'];
      if (loField) {
        loField.value = laValue.field;
      }
      if (laValue.field) {
        var laTemp = laValue.field.split(';');
        for (var i = 0; i < laTemp.length; i++) {
          laTemp[i] = '{' + laTemp[i] + '}';
        }
        laDValue.push(laTemp.join(';'));
      }
    }
    if (lsStyle.indexOf('task') > -1 && (!checkAfterSelect || checkAfterSelect.call(this, laArg, laValue) === true)) {
      var loField = goForm[psTarget + (lsStyle.indexOf('unit') > -1 ? '4' : '')];
      if (loField) {
        loField.value = laValue.task;
      }
      if (laValue.task && laExistTask) {
        var laTemp = laValue.task.split(';');
        for (var i = 0; i < laTemp.length; i++) {
          for (var j = 0; j < laExistTask.length; j++) {
            if (laExistTask[j].split('|')[1] === laTemp[i]) {
              break;
            }
          }
          if (j < laExistTask.length) {
            laTemp[i] = '<' + laExistTask[j].split('|')[0] + '>';
          }
        }
        if (lsStyle.indexOf('unit') < 0) {
          loField = goForm['D' + psTarget];
          if (loField != null) {
            loField.value = laTemp.join(';');
          }
        } else {
          laDValue.push(laTemp.join(';'));
        }
      } else {
        if (lsStyle.indexOf('unit') < 0) {
          loField = goForm['D' + psTarget];
          if (loField != null) {
            loField.value = '';
          }
        }
      }
    }

    if (lsStyle.indexOf('option') > -1) {
      var loField = goForm[psTarget + '8'];
      if (loField) {
        loField.value = laValue.option;
      }
      if (laValue.option) {
        var laTemp = laValue.option.split(';');
        for (var i = 0; i < laTemp.length; i++) {
          laTemp[i] = '[' + laTemp[i] + ']';
        }
        laDValue.push(laTemp.join(';'));
      }
    }
    if (lsStyle.indexOf('custom') > -1) {
      var loField = goForm['D' + psTarget + '16'];
      if (loField) {
        loField.value = laValue.userCustom[0];
      }
      loField = goForm[psTarget + '16'];
      if (loField) {
        loField.value = laValue.userCustom[1];
      }
      if (laValue.userCustom[0]) {
        laDValue.push(laValue.userCustom[0]);
      }
    }
    if (lsStyle.indexOf('unit') > -1) {
      var loField = goForm['D' + psTarget + 's'];
      if (loField) {
        loField.value = laDValue.join(';');
      }
    }
    if (lsStyle.indexOf('direction') > -1) {
      var loField = goForm[psTarget];
      if (loField) {
        loField.value = laValue.direction;
      }
      loField = goForm['D' + psTarget];
      if (laValue.direction && laDirection) {
        var laTemp = laValue.direction.split(';');
        for (var i = 0; i < laTemp.length; i++) {
          for (var j = 0; j < laDirection.length; j++) {
            var laDTemp = laDirection[j].split('|');
            if (laDTemp[3] + '|' + laDTemp[5] === laTemp[i]) {
              laTemp[i] = laDTemp[0] + '(' + laDTemp[2] + '-' + laDTemp[4] + ')';
              break;
            }
          }
        }
        if (loField) {
          loField.value = laTemp.join('\r');
        }
      } else {
        if (loField) {
          loField.value = '';
        }
      }
    }
  };
  showHandlerDialog(psTitle, customTitle, laArg, psTarget, goForm, checkAfterSelect);
}

function SelectUserLoadEvent($handlerDialog, laArg) {
  var goForm = $('#userSelectForm', $handlerDialog)[0];
  var lsStyle = laArg.Style;
  if (lsStyle.indexOf('unit') > -1) {
    $('#ID_Unit', $handlerDialog).show();
  } else {
    $('#ID_Unit', $handlerDialog).hide();
  }
  if (lsStyle.indexOf('field') > -1) {
    $('#ID_Field', $handlerDialog).show();
  } else {
    $('#ID_Field', $handlerDialog).hide();
  }
  if (lsStyle.indexOf('task') > -1) {
    $('#ID_Task', $handlerDialog).show();
  } else {
    $('#ID_Task', $handlerDialog).hide();
  }
  if (lsStyle.indexOf('direction') > -1) {
    $('#ID_Direction', $handlerDialog).show();
  } else {
    $('#ID_Direction', $handlerDialog).hide();
  }
  if (lsStyle.indexOf('option') > -1) {
    $('#ID_Option_0', $handlerDialog).show();
  } else {
    $('#ID_Option_0', $handlerDialog).hide();
  }
  if (lsStyle.indexOf('option') > -1) {
    $('#ID_Option_1', $handlerDialog).show();
  } else {
    $('#ID_Option_1', $handlerDialog).hide();
  }
  if (lsStyle.indexOf('custom') > -1) {
    $('#ID_User_Custom', $handlerDialog).show();
  } else {
    $('#ID_User_Custom', $handlerDialog).hide();
  }
  var laValue = laArg.unit;
  if (laValue != null) {
    $('#DUnits', $handlerDialog).val(laValue[0]);
    $('#Units', $handlerDialog).val(laValue[1]);
    var values = laValue[1].split(';');
    var labels = laValue[0].split(';');
    $.each(labels, function (i, item) {
      if (item) {
        $('#Units_control', $handlerDialog)
          .find('.org-select')
          .append('<li class="org-entity ' + values[i][0] + '"><span class="org-label">' + item + '</span></li>');
      }
    });
  }
  var lsValue = laArg.field;
  goForm.Fields.value = lsValue;
  lsValue = laArg.task;
  if (goForm.TasksValue && lsValue != null) {
    bSetFormFieldValue(goForm, 'TasksValue', lsValue);
  }
  lsValue = laArg.option;
  if (lsValue != null) {
    bSetFormFieldValue(goForm, 'UserOptions', lsValue);
  }
  var userCustom = laArg.userCustom;
  if (laValue != null && userCustom != null) {
    bSetFormFieldValue(goForm, 'DUserCustoms', userCustom[0]);
    bSetFormFieldValue(goForm, 'UserCustoms', userCustom[1]);
  }
  lsValue = laArg.direction;
  if (goForm.Directions && lsValue != null) {
    bSetFormFieldValue(goForm, 'Directions', lsValue);
  }
}

function SelectUserInitEvent($handlerDialog, laArg, title) {
  var goForm = $('#userSelectForm', $handlerDialog)[0];
  // 组织机构
  $('#Units_control', $handlerDialog).on('click', function () {
    var userDetail = SpringSecurityUtils.getUserDetails();
    var unitId = userDetail['systemUnitId'];
    var initValues = goForm.Units.value;
    var initLabels = goForm.DUnits.value;
    top.$.unit2.open({
      valueField: 'Units',
      labelField: 'DUnits',
      title: '人员选择',
      type: unitId === 'S0000000000' ? 'MyUnit' : 'all',
      multiple: true,
      selectTypes: 'all',
      initValues: initValues,
      initLabels: initLabels,
      v: "7.0",
      orgVersionId: getOrgVersionId(),
      callback: function (values, labels) {
        $('#Units_control', $handlerDialog).find('.org-select').empty();
        $.each(labels, function (i, item) {
          $('#Units_control', $handlerDialog)
            .find('.org-select')
            .append('<li class="org-entity ' + values[i][0] + '"><span class="org-label">' + item + '</span></li>');
        });
        goForm.DUnits.value = labels.join(';');
        goForm.Units.value = values.join(';');
      }
    });
  });

  // 表单字段
  var setting = {
    view: {
      showIcon: true,
      showLine: false
    },
    check: {
      enable: true,
      chkStyle: 'checkbox',
      radioType: 'all'
    },
    async: {
      otherParam: {
        serviceName: 'flowSchemeService',
        methodName: 'getFormFields',
        data: [-1, bGetForm(), '1;17;18;41;43;199']
      }
    }
  };
  $('#FieldLabels', $handlerDialog).comboTree({
    labelField: 'FieldLabels',
    valueField: 'Fields',
    initService: 'flowSchemeService.getFormFieldByFieldNames',
    initServiceParam: [bGetForm()],
    treeSetting: setting,
    width: 488,
    height: 220,
    showSelect: true,
    showCheckAll: true,
    readonly: true,
    mutiSelect: true
  });

  //环节
  var laTask = laArg.Tasks;
  if (laTask) {
    var lsHTML = '';
    var tasksArr = $.map(laTask, function (item) {
      return {
        id: item.split('|')[1],
        text: item.split('|')[0]
      };
    });

    $('#TasksValue', $handlerDialog).wellSelect({
      multiple: laArg.MulTask ? true : false,
      data: tasksArr,
      valueField: 'TasksValue',
      labelField: 'TasksLabel'
    });
  }
  var laDirection = laArg.Directions;
  if (laDirection != null) {
    var lsHTML = '';
    for (var i = 0; i < laDirection.length; i++) {
      var laValue = laDirection[i].split('|');
      var lsText = laValue[0] + '(' + laValue[2] + '-' + laValue[4] + ')';
      var lsValue = laValue[3] + '|' + laValue[5];
      lsHTML +=
        '<INPUT TYPE=checkbox class="AutoSize" NAME="Directions" VALUE="' +
        lsValue +
        '" id="directions_' +
        i +
        '"><label for="directions_' +
        i +
        '">' +
        lsText +
        '</label><br>';
    }
    $('#ID_DirectionList', $handlerDialog).html(lsHTML);
  }

  // 自定义
  $('#DUserCustoms', $handlerDialog)
    .off()
    .on('click', function () {
      showUserCustomDialog($handlerDialog, [$(this).val(), $('#UserCustoms', $handlerDialog).val()], title);
    });

  function showUserCustomDialog1(data) {
    var dlg_selector = '#dlg_select_user_custom';

    // 设置弹出框内容
    $(dlg_selector).html(data);

    // 初始化下一流程选择框
    var options = {
      title: '设置承办人',
      autoOpen: true,
      width: 680,
      height: 580,
      resizable: false,
      modal: true,
      open: function () {
        // 弹出框显示后处理
        var configName = $('#DUserCustoms').val();
        var configValue = $('#UserCustoms').val();
        UserCustom.init(configName, configValue);
      },
      buttons: {
        //				"测试表达式" : function(e) {
        //					var setExpression = $("#set_expression").val();
        //					// 检测表达式
        //					JDS.call({
        //						helper : "flowSchemeService.evaluateUserCustomExpression",
        //						data : [ setExpression ],
        //						async : false,
        //						success : function(result) {
        //							alert(result.data);
        //						},
        //						error : function(e){
        //							var tip = "测试失败，请先确保表达式可测试！";
        //							alert(tip);
        //						}
        //					});
        //				},
        确定: function (e) {
          var configNames = [];
          var expressions = [];
          var options = $('#expressionSelect').find('option');
          $.each(options, function (index) {
            configNames.push($(this).text());
            var data = $(this).data('data');
            data.order = index;
            expressions.push(data);
          });
          var expressionObject = {};
          expressionObject.expressionConfigs = expressions;
          var configName = configNames.join(' ');
          var configValue = JSON.stringify(expressionObject);
          var hasError = false;
          // 检测表达式
          $.get({
            url: ctx + '/api/workflow/definition/checkUserCustomExpression',
            data: {
              expressionConfig: configValue
            },
            async: false,
            success: function (result) {
              hasError = result.data;
            }
          });
          if (hasError === false) {
            alert('表达式语法有误!');
            return;
          }

          $('#DUserCustoms').val(configName);
          $('#UserCustoms').val(configValue);
          $(this).dialog('close');
        },
        取消: function (e) {
          $(this).dialog('close');
        }
      },
      close: function () {
        $(dlg_selector).html('');
        $(this).dialog('destroy');
      }
    };
    // 显示弹出框
    $(dlg_selector).dialog(options);
  }
}

function SelectUserOKEvent(goForm, laArg) {
  var laReturn = {};
  var lsStyle = laArg.Style;
  if (lsStyle.indexOf('unit') > -1) {
    laReturn.unitLabel = goForm.DUnits.value ? goForm.DUnits.value.split(';') : [];
    laReturn.unitValue = goForm.Units.value ? goForm.Units.value.split(';') : [];
  }
  if (lsStyle.indexOf('field') > -1) {
    laReturn.formField = goForm.Fields.value ? goForm.Fields.value.split(';') : [];
  }
  if (lsStyle.indexOf('task') > -1) {
    var _tasks = sGetFormFieldValue(goForm, 'TasksValue');
    laReturn.tasks = _tasks ? _tasks.split(';') : [];
  }
  if (lsStyle.indexOf('option') > -1) {
    var _options = sGetFormFieldValue(goForm, 'UserOptions');
    laReturn.options = _options ? _options.split(';') : [];
  }
  //todo 自己改
  if (lsStyle.indexOf('custom') > -1) {
    var laValue = new Array();
    laValue.push(goForm.DUserCustoms.value);
    laValue.push(goForm.UserCustoms.value);
    laReturn.userCustom = laValue;
  }
  if (lsStyle.indexOf('direction') > -1) {
    laReturn.direction = sGetFormFieldValue(goForm, 'Directions');
  }
  return laReturn;
}

function bGetForm() {
  return goWorkFlow.DformDefinition ? goWorkFlow.DformDefinition.uuid : '';
}

function aGetTasks(psTypes, psCurTaskID, pbEnd) {
  var laTask = [];
  var lsTypes = psTypes || 'TASK';
  var lsID = psCurTaskID || '';
  if (bGetEQFlowXML()) {
    var laNode = goWorkFlow.equalFlowXML.selectNodes('./tasks/task');
    if (laNode != null) {
      for (var i = 0; i < laNode.length; i++) {
        var lsType = laNode[i].getAttribute('type') === '1' ? 'TASK' : 'SUBFLOW';
        if (lsTypes.indexOf(lsType) == -1) {
          continue;
        }
        if (laNode[i].getAttribute('name') == null || laNode[i].getAttribute('name') === '') {
          continue;
        }
        if (laNode[i].getAttribute('id') == null || laNode[i].getAttribute('id') === '') {
          continue;
        }
        if (lsID === laNode[i].getAttribute('id')) {
          continue;
        }
        laTask.push(laNode[i].getAttribute('name') + '|' + laNode[i].getAttribute('id'));
      }
    }
  } else {
    for (var i = 0; i < goWorkFlow.tasks.length; i++) {
      if (goWorkFlow.tasks[i] == null || goWorkFlow.tasks[i].xmlObject == null) {
        continue;
      }
      if (lsTypes.indexOf(goWorkFlow.tasks[i].Type) === -1) {
        continue;
      }
      if (goWorkFlow.tasks[i].xmlObject.getAttribute('name') == null || goWorkFlow.tasks[i].xmlObject.getAttribute('name') === '') {
        continue;
      }
      if (goWorkFlow.tasks[i].xmlObject.getAttribute('id') == null || goWorkFlow.tasks[i].xmlObject.getAttribute('id') === '') {
        continue;
      }
      if (goWorkFlow.tasks[i].xmlObject.getAttribute('id') === lsID) {
        continue;
      }
      laTask.push(goWorkFlow.tasks[i].xmlObject.getAttribute('name') + '|' + goWorkFlow.tasks[i].xmlObject.getAttribute('id'));
    }
  }
  if (pbEnd != null && pbEnd == true) {
    laTask.push(parent.sGetLang('FLOW_WF_ENDTASKNAME') + '|<EndFlow>');
  }
  return laTask;
}

function bGetEQFlowXML() {
  var lsID = null;
  if (goWorkFlow.equalFlowID) {
    lsID = goWorkFlow.equalFlowID;
  } else {
    if (goWorkFlow.flowXML == null) {
      return false;
    }
    var loNode = goWorkFlow.flowXML.selectSingleNode('./property/equalFlow/id');
    if (loNode != null) {
      lsID = loNode.text();
    }
  }
  if (!lsID) {
    return false;
  }
  if (goWorkFlow.equalFlowXML == null || lsID !== $(goWorkFlow.equalFlowXML).getAttribute('id')) {
    $.ajax({
      url: 'scheme/flow/xml/id?open&id=' + lsID,
      type: 'GET',
      dataType: 'xml',
      cache: false,
      async: false,
      error: function (data) { },
      success: function (data) {
        goWorkFlow.equalFlowXML = $(data).selectSingleNode('flow');
      }
    });
  }
  if (goWorkFlow.equalFlowXML == null) {
    return false;
  }
  return true;
}

//表单字段回显转化为name
function formFieldFormat(code, subformId) {
  if (!code) {
    return null;
  }
  var DformDefinition = goWorkFlow.DformDefinition;
  if (!subformId) {
    var _field = DformDefinition.fields[code];
    if (_field) {
      return _field.displayName;
    }
  }
  var _subform = DformDefinition.subforms[subformId];
  if (_subform) {
    return _subform.displayName + ':' + _subform.fields[code].displayName;
  }
  return '';
}

function getDirections() {
  var loNodes = goWorkFlow.flowXML.selectNodes('./directions/direction');
  var directions = [];
  if (loNodes != null) {
    for (var i = 0; i < loNodes.length; i++) {
      var loNode = loNodes[i];
      var id = $(loNode).getAttribute('id');
      var name = $(loNode).getAttribute('name');
      if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(name)) {
        directions.push({
          id: id,
          text: name + ' | ' + id
        });
      }
    }
  }
  return directions;
}

function getEndDirection() {
  var loNodes = goWorkFlow.flowXML.selectNodes('./directions/direction');
  var endDirection;
  if (loNodes != null) {
    for (var i = 0; i < loNodes.length; i++) {
      var loNode = loNodes[i];
      var id = $(loNode).getAttribute('id');
      var name = $(loNode).getAttribute('name');
      var toID = $(loNode).getAttribute('toID');
      if (toID !== '<EndFlow>') continue;
      endDirection = {
        id: id,
        text: name,
        desc: id
      };
    }
  }
  return endDirection;
}

function sGetTaskNameByID(psID) {
  var lsName = null;
  var laTask = aGetTasks('TASK/SUBFLOW', null, true);
  for (var i = 0; i < laTask.length; i++) {
    var laValue = laTask[i].split('|');
    if (laValue[1] == psID) {
      lsName = laValue[0];
      break;
    }
  }
  return lsName;
}

function oAddElement(parentElement, name, value) {
  if (goWorkFlow.xmlDOM == null) {
    goWorkFlow.xmlDOM = oGetXMLDocument_();
  }
  var element = $(goWorkFlow.xmlDOM.createElement(name));
  if (value != null) {
    element.appendChild($(goWorkFlow.xmlDOM.createTextNode(value)));
  }
  parentElement.appendChild(element);
  return element;
}

function oSetElement(parentElement, name, value) {
  // 设置xml节点值
  // parentElement为jQuery的XML对象
  if (goWorkFlow.xmlDOM == null) {
    goWorkFlow.xmlDOM = oGetXMLDocument_();
  }
  var element = parentElement.selectSingleNode(name);
  if (element == null || element.length == 0) {
    // 转换为jQuery对象
    element = $(goWorkFlow.xmlDOM.createElement(name));
    parentElement.appendChild(element);
  }
  if (value != null) {
    // 使用XML对象接口判断
    if (element.get(0).childNodes.length > 0) {
      // element.children[0].val(value);
      element.get(0).childNodes[0].nodeValue = value;
    } else {
      element.append($(goWorkFlow.xmlDOM.createTextNode(value)));
    }
  }
  return element;
}

function bSetXMLFieldValue(poXML, psFieldName, paValue) {
  var loNode = poXML.selectSingleNode(psFieldName);
  if (loNode != null) {
    poXML.removeChild(loNode);
  }
  loNode = oAddElement(poXML, psFieldName);
  if (paValue != null) {
    for (var i = 0; i < paValue.length; i++) {
      var loUnit = oAddElement(loNode, 'unit', paValue[i]);
      loUnit.setAttribute('type', '32');
    }
  }
  return true;
}

function getFormID() {
  var loNode = goWorkFlow.flowXML.selectSingleNode('./property/formID');
  var formUuid = loNode != null ? loNode.text() : null;
  return formUuid;
}

function getOrgVersionId() {
  var orgVersionId = null;
  var loNode = goWorkFlow.flowXML.selectSingleNode('./property/orgId');
  var orgId = loNode != null ? loNode.text() : null;
  if (orgId) {
    $.get({
      url: `/proxy/api/org/organization/version/getOrgVersionIdByOrgId?orgId=${orgId}`,
      async: false,
      success: function (result) {
        orgVersionId = result.data;
      }
    })
  }
  return orgVersionId;
}

function setUnitFieldValue(ele, goForm, data) {
  var ids = data.ids.split(';');
  var texts = data.texts.split(';');
  $.each(ids, function (i, item) {
    $('#' + ele, goForm)
      .find('.org-select')
      .append('<li class="org-entity ' + item[0] + '"><span class="org-label">' + texts[i] + '</span></li>');
  });
}

function openTitleExpressionDialog(goForm, data, container, title, tips) {
  var _html = get_title_expression(tips);
  var $dialog = top.appModal.dialog({
    title: title || '流程标题设置',
    message: _html,
    size: 'large',
    shown: function () {
      $('textarea', $dialog).val($('#' + container).text());
      setTitle_expressionSelect($dialog, 'defaultField', true, [
        '流程名称',
        '流程ID',
        '流程编号',
        '发起人姓名',
        '发起人所在部门名称',
        '发起人所在部门名称全路径',
        '年',
        '简年',
        '月',
        '日',
        '时',
        '分',
        '秒',
        '发起年',
        '发起简年',
        '发起月',
        '发起日',
        '发起时',
        '发起分',
        '发起秒'
      ]);
      var formDefinition = goWorkFlow.DformDefinition || loadFormDefinition($('#DForm').val());
      var formFieldData = [];
      var field = {};
      field.label = formDefinition.name;
      field.labelID = '';
      field.optgroup = [];
      $.each(formDefinition.fields, function (i, v) {
        field.optgroup.push({
          value: i,
          name: v.displayName,
          data: v
        });
      });
      formFieldData.push(field);
      $.each(formDefinition.subforms, function (i, item) {
        var field = {};
        field.label = item.name;
        field.labelID = item.outerId;
        field.optgroup = [];
        $.each(item.fields, function (i, v) {
          field.optgroup.push({
            value: i,
            name: v.displayName,
            data: v
          });
        });
        formFieldData.push(field);
      });
      $(top.document)
        .off()
        .on('click', function (e) {
          if ($('.choose-item', $dialog)[0] === $(e.target).parents('.well-select')[0]) return;
          $('.choose-item', $dialog).removeClass('well-select-visible');
        });
      setTitle_expressionSelect($dialog, 'formField', true, formFieldData);
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
            if ($this.data('value').toUpperCase().indexOf(keyword) > -1 || $this.data('name').toUpperCase().indexOf(keyword) > -1) {
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

      $('.well-select-dropdown', $dialog)
        .off()
        .on('click', function (e) {
          e.stopPropagation();
        });
      $('.well-select-item', $dialog)
        .off()
        .on('click', function (e) {
          var $titleControl = $('#titleControl', $dialog)[0];
          var value = $titleControl.value;
          var start = $titleControl.selectionStart;
          var value1 = value.substr(0, start);
          var value2 = value.substr(start);
          var finalValue = value1 + '${' + $(this).attr('data-value') + '}' + value2;
          $('textarea', $dialog).val(finalValue);
          $('.choose-item', $dialog).removeClass('well-select-visible');
        });
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          $('#' + container).text($('textarea', $dialog).val());
        }
      },
      close: {
        label: '关闭',
        className: 'btn-default',
        callback: function () { }
      }
    }
  });
}

function setTitle_expressionSelect($dialog, id, showSearch, data) {
  $dialog
    .find('#' + id)
    .append(
      '<div class="well-select-dropdown" x-placement="bottom-start"' +
      '    style="position: absolute; left: 0; top: 34px; width: 300px;">' +
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

  if (id === 'defaultField') {
    $.each(data, function (i, item) {
      $dialog
        .find('#defaultField .well-select-dropdown-list')
        .append('<li class="well-select-item" data-name="' + item + '" data-value="' + item + '"><span>' + item + '</span></li>');
    });
  } else {
    $.each(data[0].optgroup, function (i, item) {
      $dialog
        .find('#formField .well-select-dropdown-list')
        .append(
          '<li class="well-select-item" data-name="' + item.name + '" data-value="' + item.value + '"><span>' + item.name + '</span></li>'
        );
    });
  }
}

function get_title_expression(tips) {
  var tipContent = tips ? tips : '在下方编辑流程标题表达式，可插入流程内置变量、表单字段和文本。';
  return (
    '<div class="title_expression_wrap">' +
    '<div class="tip">' +
    '<span>' +
    '<i class="iconfont icon-ptkj-xinxiwenxintishi"></i>' +
    tipContent +
    '</span>' +
    '</div>' +
    '<div class="content">' +
    '<div class="choose-btns clear">' +
    '<div id="defaultField" class="choose-item well-select"><span>插入流程内置变量</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia well-select-arrow"></i></div>' +
    '<div id="formField" class="choose-item well-select"><span>插入表单字段</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia well-select-arrow"></i></div></div>' +
    '<textarea id="titleControl" class="form-control" rows="10"></textarea>' +
    '</div>' +
    '<div class="bootom-tip">样例：${流程名称}_${发起人姓名}(${发起年}${发起月}${发起日})</div>' +
    '</div>'
  );
}

function getEventScriptHtml(pointcut) {
  var html = '<form id="eventScriptForm" class="workflow-popup form-widget">' + '<div class="well-form form-horizontal">';
  if (pointcut != 'direction') {
    html +=
      '<div class="form-group">' +
      '<label class="well-form-label control-label required">事件节点</label>' +
      '<div class="well-form-control">' +
      '<input type="text" class="form-control" id="pointcut" name="pointcut">' +
      '</div>' +
      '</div>';
  }

  html +=
    '<div class="form-group">' +
    '<label class="well-form-label control-label">脚本类型</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="type" name="type">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">脚本内容</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="contentType" name="contentType">' +
    '</div>' +
    '</div>' +
    '<div class="form-group contentType1" style="display: none">' +
    '<label class="well-form-label control-label">脚本定义</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="createdScriptDefinition" name="createdScriptDefinition">' +
    '</div>' +
    '</div>' +
    '<div class="form-group contentType2" style="display:none;">' +
    '<label class="well-form-label control-label">自定义</label>' +
    '<div class="well-form-control">' +
    '<div id="customScriptDefinitionWrap">' +
    '<textarea id="customScriptDefinition" name="customScriptDefinition" rows="5" class="form-control"></textarea>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">支持的脚本变量</label>' +
    '<div class="well-form-control">' +
    '<div class="list-vertical" style="width: 60%">' +
    '<div class="tip-text">applicationContext：spring应用上下文</div>' +
    '<div class="tip-text">event：流程相关事件信息</div>' +
    '<div class="tip-text">taskInstUuid：环节实例UUID</div>' +
    '<div class="tip-text">formUuid：表单定义UUID</div>' +
    '<div class="tip-text">dyFormData：表单数据</div>' +
    '<div class="tip-text">actionType：操作类型，如Submit、Rollback等</div>' +
    '</div>' +
    '<div class="list-vertical" style="width: 40%;">' +
    '<div class="tip-text">currentUser：当前用户信息</div>' +
    '<div class="tip-text">flowInstUuid：流程实例UUID</div>' +
    '<div class="tip-text">taskData：环节数据</div>' +
    '<div class="tip-text">dataUuid：表单数据UUID</div>' +
    '<div class="tip-text">dyFormFacade：表单接口</div>' +
    '<div class="tip-text">opinionText：办理意见</div>' +
    '</div>' +
    '<div class="tip-text">resultMessage：事件脚本执行结果，调用resultMessage.isSuccess()方法返回true，通过resultMessage.setSuccess(true/false)设置脚本执行是否成功</div>' +
    '</div>' +
    '</div>' +
    '</form>';
  return html;
}

function loadEventScript(goForm, flowProperty, $dialog, data, showPointcut) {
  if (data) {
    initEventScript(goForm, flowProperty, $dialog, data, showPointcut);
  }
  var pointcut = [
    {
      id: 'created',
      text: '流程创建'
    },
    {
      id: 'started',
      text: '流程启动'
    },
    {
      id: 'end',
      text: '流程结束'
    },
    {
      id: 'deleted',
      text: '流程删除'
    }
  ];
  var customPointCut = [
    {
      id: 'created',
      text: '环节创建'
    },
    {
      id: 'completed',
      text: '环节完成'
    }
  ];
  var pointcutObj = {
    created: '流程创建',
    started: '流程启动',
    end: '流程结束',
    deleted: '流程删除'
  };
  var pointcutObj1 = {
    created: '环节创建',
    completed: '环节完成'
  };
  var type = [
    {
      id: '',
      text: ''
    },
    {
      id: 'groovy',
      text: 'Groovy'
    }
  ];
  var contentType = [
    {
      id: '1',
      text: '脚本定义'
    },
    {
      id: '2',
      text: '自定义'
    }
  ];

  var exitScripts = [];
  $('#eventScripts li', goForm).each(function () {
    var $this = $(this);
    exitScripts.push($this.attr('data-name'));
  });

  $('#pointcut', $dialog)
    .wellSelect({
      data: showPointcut == 'task' ? customPointCut : pointcut,
      searchable: false
    })
    .on('change', function () {
      var val = $(this).val();
      if (!data && exitScripts.indexOf(val) > -1) {
        top.appModal.error('[' + showPointcut == 'task' ? pointcutObj1[val] : pointcutObj[val] + ']事件脚本已配置，不能重复配置');
        $('#pointcut', $dialog).wellSelect('val', '');
      }
    });
  if (data) {
    $('#pointcut', $dialog).wellSelect('disable', true);
  }
  $('#type', $dialog)
    .wellSelect({
      data: type,
      searchable: false
    })
    .on('change', function () {
      getCreatedScriptDefinition($dialog, $('#type', $dialog).val());
      createAceCodeEditor(
        $('#type', $dialog).val(),
        $('#contentType', $dialog).val(),
        $('#customScriptDefinition', $dialog),
        'customScriptDefinitionWrap',
        'workflow.' + $('#pointcut', $dialog).val() + 'GroovyScript'
      );
    });
  $('#contentType', $dialog)
    .wellSelect({
      data: contentType,
      searchable: false
    })
    .on('change', function () {
      var val = $(this).val();
      if (val === '1') {
        $('.contentType1', $dialog).show();
        $('.contentType2', $dialog).hide();
        getCreatedScriptDefinition($dialog, $('#type', $dialog).val());
      } else {
        $('.contentType1', $dialog).hide();
        $('.contentType2', $dialog).show();
        createAceCodeEditor(
          $('#type', $dialog).val(),
          $('#contentType', $dialog).val(),
          $('#customScriptDefinition', $dialog),
          'customScriptDefinitionWrap',
          'workflow.' + $('#pointcut', $dialog).val() + 'GroovyScript'
        );
      }
    })
    .trigger('change');

  createAceCodeEditor(
    $('#type', $dialog).val(),
    $('#contentType', $dialog).val(),
    $('#customScriptDefinition', $dialog),
    $('#customScriptDefinitionWrap', $dialog),
    'workflow.' + $('#pointcut', $dialog).val() + 'GroovyScript'
  );
}

function getCreatedScriptDefinition($dialog, type) {
  $('#createdScriptDefinition', $dialog).wSelect2({
    serviceName: 'cdScriptDefinitionFacadeService',
    queryMethod: 'loadSelectData',
    valueField: 'createdScriptDefinition',
    params: {
      type: type
    },
    multiple: false,
    remoteSearch: false
  });
}

function initEventScript(goForm, flowProperty, $dialog, data, showPointcut) {
  var poForm = $('#eventScriptForm', $dialog)[0];
  if (showPointcut != 'direction') {
    poForm.pointcut.value = data.pointcut;
  }
  poForm.type.value = data.type;
  poForm.contentType.value = data.contentType;
  if (data.contentType === '1') {
    poForm.createdScriptDefinition.value = data.text;
  } else {
    poForm.customScriptDefinition.value = data.text;
  }
}

function addEventScript(goForm, flowProperty, data, type) {
  var point = {
    created: '流程创建',
    started: '流程启动',
    end: '流程结束',
    deleted: '流程删除'
  };
  var customPoint = {
    created: '环节创建',
    completed: '环节完成'
  };
  var pointcut = type == 'task' ? customPoint : point;
  var name = data.pointcut ? pointcut[data.pointcut] : '脚本定义';
  var $li = $('<li data-name="' + (data.pointcut || '') + '"></li>');
  $li.data('script', data);
  var html =
    '<div class="name">' +
    name +
    '</div>' +
    '<div class="btn-group">' +
    '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-set" title="设置">' +
    '<i class="iconfont icon-ptkj-shezhi"></i>' +
    '</div>' +
    '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-remove" title="删除">' +
    '<i class="iconfont icon-ptkj-shanchu"></i>' +
    '</div>' +
    '</div>';
  $li.append(html);
  if (data.pointcut) {
    switch (data.pointcut) {
      case 'created':
        $('#eventScripts', goForm).prepend($li);
        break;
      case 'started':
        var $firstLi = $('#eventScripts li:first', goForm);
        if (!$firstLi.length || $firstLi.attr('data-name') !== 'created') {
          $('#eventScripts', goForm).prepend($li);
        } else {
          $firstLi.after($li);
        }
        break;
      case 'end':
        var $lastLi = $('#eventScripts li:last', goForm);
        if (!$lastLi.length || $lastLi.attr('data-name') !== 'deleted') {
          $('#eventScripts', goForm).append($li);
        } else {
          $lastLi.before($li);
        }
        break;
      case 'deleted':
      case 'completed':
        $('#eventScripts', goForm).append($li);
        break;
    }
  } else {
    $('#eventScripts', goForm).append($li);
  }

  if ($('#eventScripts li', goForm).length === 4) {
    $('#addEventScript', goForm).hide();
  }
}

function openEventScript(goForm, flowProperty, data, pointcut, liIndex) {
  var _html = getEventScriptHtml(pointcut);
  var $dialog = top.appModal.dialog({
    title: '事件脚本',
    message: _html,
    size: 'large',
    shown: function () {
      loadEventScript(goForm, flowProperty, $dialog, data, pointcut);
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var contentType = $('#contentType', $dialog).val();
          var content = '';
          if (contentType === '1') {
            content = $('#createdScriptDefinition', $dialog).val();
          } else {
            content = $('#customScriptDefinition', $dialog).val();
          }
          if (content) {
            var script = {
              type: $('#type', $dialog).val(),
              contentType: contentType,
              text: content
            };
            if (pointcut != 'direction') {
              script.pointcut = $('#pointcut', $dialog).val();
            }
            if (data) {
              if (pointcut != 'direction') {
                $('#eventScripts li[data-name="' + data.pointcut + '"]', goForm).data('script', script);
              } else {
                $('#eventScripts li:eq(' + liIndex + ')', goForm).data('script', script);
              }
            } else {
              addEventScript(goForm, flowProperty, script, pointcut);
            }
          }
        }
      },
      close: {
        label: '关闭',
        className: 'btn-default',
        callback: function () { }
      }
    }
  });
}

function createAceCodeEditor(scriptType, contentType, $textarea, container, codeType) {
  //groovy脚本代码编辑框
  var flowId = goWorkFlow.flowXML.attr('id') || '';
  if (scriptType === 'groovy' && contentType === '2' && flowId) {
    $textarea.hide();
    if ($textarea.data('codeEditor')) {
      $textarea.data('codeEditor').getContainer().show();
      return;
    }
    var version = goWorkFlow.flowXML.attr('version') || '1.0';
    //创建代码编辑框
    $('<div>', {
      class: container
    }).insertAfter($textarea);
    var $ace = top.$.fn.aceBinder({
      id: container + 'Content',
      container: '.' + container,
      iframeId: container + 'WfScriptCoderFrame',
      mode: 'groovy',
      varSnippets: 'wf.workflowDefineGroovy',
      value: $textarea.val(),
      valueChange: function (v) {
        $textarea.val(v);
      },
      codeHis: {
        enable: true,
        relaBusizUuid: 'workflow_' + flowId + '_version_' + version, //流程ID
        codeType: codeType
      }
    });
    $textarea.data('codeEditor', $ace);
  } else {
    $textarea.show();
    $textarea.data('codeEditor') && $textarea.data('codeEditor').getContainer().hide();
  }
}

function XMLtoString(elem) {
  var serialized;
  try {
    var serializer = new XMLSerializer();
    serialized = serializer.serializeToString(elem);
  } catch (e) {
    serialized = elem.xml;
  }
  return serialized;
}

function bSetUnitFieldDataToXML(xmlObj, data) {
  $.each(data.unitValue, function (i, item) {
    var loUnit = oAddElement(xmlObj, 'unit');
    loUnit.setAttribute('type', '1');
    oAddElement(loUnit, 'value', item);
    oAddElement(loUnit, 'argValue', data.unitLabel[i]);
  });
  $.each(data.formField, function (i, item) {
    var loUnit = oAddElement(xmlObj, 'unit');
    loUnit.setAttribute('type', '2');
    oAddElement(loUnit, 'value', item);
  });
  $.each(data.tasks, function (i, item) {
    var loUnit = oAddElement(xmlObj, 'unit');
    loUnit.setAttribute('type', '4');
    oAddElement(loUnit, 'value', item);
  });
  $.each(data.options, function (i, item) {
    var loUnit = oAddElement(xmlObj, 'unit');
    loUnit.setAttribute('type', '8');
    oAddElement(loUnit, 'value', item);
  });
  if (data.userCustom && $.isArray(data.userCustom)) {
    if (data.userCustom[0] && data.userCustom[1]) {
      var loUnit = oAddElement(xmlObj, 'unit');
      loUnit.setAttribute('type', '16');
      oAddElement(loUnit, 'value', data.userCustom[1] || '');
      oAddElement(loUnit, 'argValue', data.userCustom[0] || '');
    }
  }
}

// 通用判断是否为json
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
      return false;
    }
  }
}

function formatUnitXMLData(data) {
  var result = {
    userCustom: [],
    formField: [],
    options: [],
    tasks: [],
    unitLabel: [],
    unitValue: []
  };
  if (!$.isArray(data)) {
    data = [data];
  }
  $.each(data, function (i, item) {
    switch (item._type) {
      case '1':
        result.unitValue.push(item.value);
        result.unitLabel.push(item.argValue);
        break;
      case '2':
        result.formField.push(item.value);
        break;
      case '4':
        result.tasks.push(item.value);
        break;
      case '8':
        result.options.push(item.value);
        break;
      case '16':
        result.userCustom = [item.argValue, item.value];
        break;
    }
  });
  return result;
}

// 流转设置 -- 显示位置
function setPlaceholder(obj, goForm) {
  var formUuid = bGetForm();
  $.get({
    url: ctx + '/api/workflow/definition/getFormBlocks',
    data: {
      formUuid: formUuid
    },
    success: function (result) {
      var data = [];
      for (var i = 0; i < result.data.length; i++) {
        data.push({
          id: result.data[i].data,
          text: result.data[i].name
        });
      }
      $.each(obj, function (index, item) {
        $('#' + item, goForm).wellSelect({
          data: data
        });
      });
    }
  });
}

function setFrameReadOnly(frameWindow) {
  var $ = frameWindow ? frameWindow.$ : $;

  // 1. 禁用文本框、单选框、复选框
  $('input').attr('disabled', 'disabled');

  // 2. 禁用多行文本框
  $('textarea').attr('disabled', 'disabled');

  // 3. 禁用下拉框、wellselect
  $('select').attr('disabled', 'disabled');
  $('.well-select').addClass('wellSelect-container-disabled');

  // 4. 禁用按钮
  $('.well-btn').addClass('w-btn-disabled').css({
    'pointer-events': 'none',
    color: '#ccc'
  });

  // 5. 禁用开关
  $('.switch-wrap').addClass('switch-wrap-disabled').css({
    'pointer-events': 'none'
  });

  // 6. 禁用组织选择框
  $('.org-select-container').attr('readonly', 'readonly').css({
    'pointer-events': 'none'
  });
}

// 判断条件弹窗
function showConditionDialog(options) {
  var optionData = options.optionData;
  var data = options.data;
  var iIndex = options.index;
  var types = options.types;
  var $listContainer = options.listContainer;
  var title = options.title || '判断条件';
  var html = initConditionDialog(types);
  var $conditionDialog = top.appModal.dialog({
    title: title,
    message: html,
    size: 'large',
    with: '1000',
    height: '600',
    shown: function () {
      // 隐藏不匹配的逻辑条件类型
      var logicTypes = ['1', '2', '3', '4', '5', '6'];
      var dutyData = [
        {
          text: '申请人职等',
          id: 'A1'
        },
        {
          text: '申请人职级',
          id: 'A2'
        },
        {
          text: '当前办理人职等',
          id: 'A3'
        },
        {
          text: '当前办理人职级',
          id: 'A4'
        }
      ];
      if (types) {
        for (var i = 0; i < logicTypes.length; i++) {
          if ($.inArray(logicTypes[i], types) < 0) {
            $('#LogicType_' + logicTypes[i], $conditionDialog).hide();
            $('label[for=LogicType_' + logicTypes[i] + ']', $conditionDialog).hide();
          }
        }
      }
      var isHideRank = SystemParams.getValue('app.hide.rank') && SystemParams.getValue('app.hide.rank') == 'show';
      if (isHideRank) {
        $('#LogicType_7', $conditionDialog).next().show();
      }

      var loNode = goWorkFlow.flowXML.selectSingleNode('./property/formID');
      var formUuid = loNode != null ? loNode.text() : '';

      $("input[name='LogicType']", $conditionDialog)
        .off()
        .on('change', function () {
          var val = $(this).val();
          $(
            '.Logic_ID_Field,.Logic_ID_Vote,.Logic_ID_Group,.Logic_ID_Expression,.Logic_ID_Opinion_Position,.Logic_ID_Duty',
            $conditionDialog
          ).hide();
          if (val == '1') {
            $('.Logic_ID_Field', $conditionDialog).show();
          } else if (val == '2') {
            $('.Logic_ID_Vote', $conditionDialog).show();
          } else if (val == '3') {
            $('.Logic_ID_Group', $conditionDialog).show();
          } else if (val == '5') {
            $('.Logic_ID_Expression', $conditionDialog).show();
          } else if (val == '6') {
            $('.Logic_ID_Opinion_Position', $conditionDialog).show();
          } else if (val == '7') {
            $('.Logic_ID_Duty', $conditionDialog).show();
          }
        });

      $('#formValueType', $conditionDialog).on('change', function () {
        if ($(this).val() == '1') {
          $('#Value', $conditionDialog).closest('.inputAndSelect').show();
          $('#Value2', $conditionDialog).closest('.inputAndSelect').hide();
        } else {
          $('#Value', $conditionDialog).closest('.inputAndSelect').hide();
          $('#Value2', $conditionDialog).closest('.inputAndSelect').show();
        }
      });

      $('#voteValueType', $conditionDialog).on('change', function () {
        if ($(this).val() == '1') {
          $('#vValue', $conditionDialog).closest('.inputAndSelect').show();
          $('#vValue2', $conditionDialog).closest('.inputAndSelect').hide();
        } else {
          $('#vValue', $conditionDialog).closest('.inputAndSelect').hide();
          $('#vValue2', $conditionDialog).closest('.inputAndSelect').show();
        }
      });
      $('#orgValueType', $conditionDialog).on('change', function () {
        if ($(this).val() == '1') {
          $('#Groups', $conditionDialog).closest('.inputAndSelect').show();
          $('#orgValue2', $conditionDialog).closest('.inputAndSelect').hide();
        } else {
          $('#Groups', $conditionDialog).closest('.inputAndSelect').hide();
          $('#orgValue2', $conditionDialog).closest('.inputAndSelect').show();
        }
      });

      $('#dValueType', $conditionDialog).on('change', function () {
        var DutyId = $('#DutyId', $conditionDialog).val();
        var val = $(this).val();

        changeDutyAndType($conditionDialog, val, DutyId);
      });

      var dSetting = {
        check: {
          radioType: 'level',
          chkStyle: 'radio',
          enable: true
        },
        view: {
          autoCancelSelected: true
        },
        async: {
          otherParam: {
            serviceName: 'orgDutySeqService',
            methodName: 'queryJobRankSelect',
            data: ['']
          }
        },
        callback: {
          onNodeCreated: function (event, treeId, treeNode) { },
          onClick: null,
          beforeClick: function (treeId, treeNode) { }
        }
      };

      $('#dValueName2', $conditionDialog)
        .comboTree({
          labelField: 'dValueName2',
          valueField: 'dValue2',
          treeSetting: dSetting,
          width: 220,
          height: 220,
          multiple: true,
          autoInitValue: false,
          autoCheckByValue: true,
          labelBy: 'name'
        })
        .trigger('change');

      if (data) {
        data.optnames = optionData;
        var logic = false;
        if (data.value == null) {
          logic = true;
        } else {
          logic = $.trim(data.value).indexOf('&') != -1 || $.trim(data.value).indexOf('|') != -1;
        }
        data.logic = logic;
        bInitLogicValue(data, $conditionDialog);
      }

      $('#DutyId', $conditionDialog)
        .wellSelect({
          searchable: false,
          data: dutyData
        })
        .on('change', function () {
          var val = $(this).val();
          var dOperatorValue = $('#dOperator', $conditionDialog).val();
          if (val == 'A1' || val == 'A3') {
            //职等-渲染操作符
            var curIndex = _.findIndex(gradeOperateor, function (o) {
              return o.id == dOperatorValue;
            });
            if (curIndex == -1) {
              $('#dOperator', $conditionDialog).val('');
            }
            $('#dOperator', $conditionDialog).wellSelect({
              data: gradeOperateor,
              searchable: false,
              placeholder: '请选择操作符'
            });
          } else {
            var curIndex = _.findIndex(dutyOperateor, function (o) {
              return o.id == dOperatorValue;
            });
            if (curIndex == -1) {
              $('#dOperator', $conditionDialog).val('');
            }
            //职级-渲染操作符
            $('#dOperator', $conditionDialog).wellSelect({
              data: dutyOperateor,
              searchable: false,
              placeholder: '请选择操作符'
            });
          }
          var dValueType = $('#dValueType', $conditionDialog).val();
          changeDutyAndType($conditionDialog, dValueType, val);
        });

      $('#AndOr', $conditionDialog).wellSelect({
        searchable: false,
        data: [
          {
            id: '',
            text: ''
          },
          {
            id: '&',
            text: '并且'
          },
          {
            id: '|',
            text: '或者'
          }
        ],
        placeholder: '请选择逻辑关系'
      });

      var lBracket = [
        {
          id: '',
          text: ''
        },
        {
          id: '(',
          text: '('
        }
      ];

      var rBracket = [
        {
          id: '',
          text: ''
        },
        {
          id: ')',
          text: ')'
        }
      ];

      var bracket = [
        'RBracket',
        'vRBracket',
        'pRBracket',
        'gRBracket',
        'dRBracket',
        'LBracket',
        'vLBracket',
        'pLBracket',
        'gLBracket',
        'eLBracket',
        'dLBracket'
      ];
      $.each(bracket, function (index, item) {
        var bracketData = item.indexOf('RBracket') > -1 ? rBracket : lBracket;
        $('#' + item, $conditionDialog).wellSelect({
          data: bracketData,
          searchable: false
        });
      });

      var voteOptionData = [];
      var positionOptionData = [];
      if (optionData != '') {
        var newData = optionData.split(';');
        $.each(newData, function (index, item) {
          voteOptionData.push({
            id: '[VOTE=' + item.split('|')[1] + ']',
            text: item
          });
          positionOptionData.push({
            id: item.split('|')[1],
            text: item
          });
        });
      }
      $('#VoteOption', $conditionDialog).wellSelect({
        searchable: false,
        data: voteOptionData,
        placeholder: '请选择投票意见'
      });
      $('#positionOption', $conditionDialog).wellSelect({
        searchable: false,
        data: positionOptionData,
        placeholder: '请选择'
      });
      var operateor = [
        {
          id: '',
          text: ''
        },
        {
          id: '>',
          text: '大于'
        },
        {
          id: '>=',
          text: '大于等于'
        },
        {
          id: '<',
          text: '小于'
        },
        {
          id: '<=',
          text: '小于等于'
        },
        {
          id: '==',
          text: '等于'
        },
        {
          id: '!=',
          text: '不等于'
        }
      ];
      //职等对应的操作符
      var gradeOperateor = [
        {
          id: 'equal',
          text: '等于'
        },
        {
          id: 'notEqual',
          text: '不等于'
        },
        {
          id: 'overTop',
          text: '高于'
        },
        {
          id: 'overTopEqual',
          text: '高于等于'
        },
        {
          id: 'lowerThan',
          text: '低于'
        },
        {
          id: 'lowerThanEqual',
          text: '低于等于'
        }
      ];
      //职级对应的操作符
      var dutyOperateor = [
        {
          id: 'equal',
          text: '等于'
        },
        {
          id: 'notEqual',
          text: '不等于'
        },
        {
          id: 'overTop',
          text: '高于'
        },
        {
          id: 'overTopEqual',
          text: '高于等于'
        },
        {
          id: 'lowerThan',
          text: '低于'
        },
        {
          id: 'lowerThanEqual',
          text: '低于等于'
        }
      ];
      $('#Operator', $conditionDialog).wellSelect({
        data: operateor.concat([
          {
            id: 'like',
            text: '包含'
          },
          {
            id: 'notlike',
            text: '不包含'
          }
        ]),
        searchable: false,
        placeholder: '请选择操作符'
      });
      $('#vOperator', $conditionDialog).wellSelect({
        data: operateor,
        searchable: false,
        placeholder: '请选择操作符'
      });
      var pOperateor = [
        {
          id: '==',
          text: '等于'
        },
        {
          id: '!=',
          text: '不等于'
        }
      ];
      $('#positionValue', $conditionDialog).wellSelect({
        data: [
          {
            id: 'PositionValue',
            text: '意见立场值'
          }
        ],
        searchable: false,
        defaultBlank: false,
        defaultValue: options.action == 'add' ? 'PositionValue' : null
      });
      $('#pOperator', $conditionDialog).wellSelect({
        data: pOperateor,
        searchable: false,
        placeholder: '请选择操作符'
      });
      $('#dOperator', $conditionDialog).wellSelect({
        data: [],
        searchable: false,
        placeholder: '请选择操作符'
      });
      $('#GroupType', $conditionDialog)
        .wellSelect({
          data: [
            {
              id: '1',
              text: '当前办理人为所选人员之一'
            },
            {
              id: '0',
              text: '表单字段所选人员为所选人员之一'
            }
          ],
          searchable: false,
          placeholder: '请选择成员'
        })
        .on('change', function () {
          if ($(this).val() == '0') {
            $('#ID_UserField', $conditionDialog).show();
          } else {
            $('#ID_UserField', $conditionDialog).hide();
          }
        });

      $.get({
        url: ctx + '/api/workflow/definition/getFormFields',
        data: {
          formUuid: formUuid
        },
        success: function (result) {
          var data = result.data;
          var newData = [];
          // 提取从表也作为字段列表
          $.each(data, function (i, field) {
            // 去掉从表字段，bug#47586
            if (field.children && field.children.length > 0) {
              //              var children = field.children;
              //              $.each(children, function (j, subformField) {
              //                // 过滤掉添加删除行
              //                if (subformField.name == '添加删除行') {
              //                  return;
              //                }
              //                subformField.name = field.name + '/' + subformField.name;
              //                newData.push(subformField);
              //              });
            } else {
              newData.push(field);
            }
          });
          var fieldData = [];
          $.each(newData, function (index, item) {
            fieldData.push({
              id: item.data,
              text: item.name
            });
          });
          $('#FieldName', $conditionDialog).wellSelect({
            searchable: false,
            data: fieldData,
            valueField: 'FieldName',
            labelField: 'FieldNameLabel',
            placeholder: '请选择字段'
          });
          if ($('#FieldName', $conditionDialog).val() == '') {
            //如果是新增FieldName为空，则默认为FieldNameLabel默认选择的值
            $('#FieldName', $conditionDialog).val($('#FieldNameLabel', $conditionDialog).val());
          } else {
            $('#FieldNameLabel', $conditionDialog).val($('#FieldName', $conditionDialog).val());
          }

          var value2 = ['Value2', 'vValue2', 'orgValue2', 'dValue3'];
          $.each(value2, function (index, item) {
            $('#' + item, $conditionDialog).wellSelect({
              searchable: false,
              data: fieldData,
              valueField: item,
              placeholder: '请选择字段'
            });
          });
        }
      });

      $('.num-arrows-up', $conditionDialog)
        .off('click')
        .on('click', function () {
          var $input = $(this).parent().prev();
          if ($input.val() == '') {
            $input.val(0);
          }
          $input.val($input.val() - 0 + 1);
        });
      $('.num-arrows-down', $conditionDialog)
        .off('click')
        .on('click', function () {
          var $input = $(this).parent().prev();
          if ($input.val() == '' || $input.val() == '0') {
            $input.val(0);
            return false;
          }
          $input.val($input.val() - 1);
        });

      $.get({
        url: ctx + '/api/workflow/definition/getFormFields',
        data: {
          formUuid: formUuid
        },
        success: function (result) {
          var data = result.data;
          var userData = [];
          $.each(data, function (i, k) {
            // 去掉从表字段，bug#57365
            if (k.children && k.children.length > 0) {
            } else {
              userData.push({
                id: k.data,
                text: k.name
              });
            }
          });
          $('#UserField', $conditionDialog).wellSelect({
            searchable: false,
            data: userData,
            valueField: 'UserField',
            labelField: 'UserFieldLabel',
            placeholder: '请选择用户字段'
          });
        }
      });

      // 选择人员
      $('#DGroups', $conditionDialog)
        .off()
        .on('click', function (e) {
          e.target.title = '选择人员';
          bFlowActions(32, e, $('#logicForm', $conditionDialog)[0]);
        });
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          if (!aLogicCheckValue($conditionDialog)) {
            return false;
          }
          var returnValue = aLogicBuildValue($conditionDialog);
          if (data) {
            $($listContainer)
              .find('li:eq(' + iIndex + ')')
              .data('obj', returnValue[1])
              .attr('logicType', returnValue[2])
              .find('div')
              .text(returnValue[0]);
          } else {
            var html =
              ' <li class="work-flow-other-item" style="padding: 0px;" data-obj="' +
              encodeURIComponent(returnValue[1]) +
              '" logicType="' +
              returnValue[2] +
              '">' +
              '<div>' +
              returnValue[0] +
              '</div>' +
              "<button type='button' class='editCondition'><i class='iconfont icon-ptkj-shezhi' title='设置'></i></button>" +
              "<button type='button' class='clearCondition'><i class='iconfont icon-ptkj-shanchu' title='删除'></i></button>" +
              '</li>';
            $($listContainer).append(html);
          }
        }
      },
      cancel: {
        label: '取消',
        className: 'btn-default',
        callback: function () { }
      }
    }
  });
}

function changeDutyAndType($conditionDialog, val, DutyId) {
  if (val == '2') {
    $('#dValue2', $conditionDialog).closest('.inputAndSelect').hide();
    $('#dValue1', $conditionDialog).closest('.inputAndSelect').hide();
    $('#dValue3', $conditionDialog).closest('.inputAndSelect').show();
  } else {
    if (DutyId == 'A2' || DutyId == 'A4') {
      $('#dValue2', $conditionDialog).closest('.inputAndSelect').show();
      $('#dValue1', $conditionDialog).closest('.inputAndSelect').hide();
    } else {
      $('#dValue2', $conditionDialog).closest('.inputAndSelect').hide();
      $('#dValue1', $conditionDialog).closest('.inputAndSelect').show();
    }
    $('#dValue3', $conditionDialog).closest('.inputAndSelect').hide();
  }
}

function initConditionDialog() {
  var html = '';
  html +=
    '<form id="logicForm" class="workflow-popup form-widget">' +
    '<div class="well-form form-horizontal">' +
    '<div class="form-group logic-type">' +
    '<label for="ruleName" class="well-form-label control-label">条件类型</label>' +
    '<div class="well-form-control">' +
    '<input type="radio" class="form-control" id="LogicType_1" name="LogicType" value="1" checked>' +
    '<label for="LogicType_1">通过字段值比较</label>' +
    '<input type="radio" class="form-control" id="LogicType_2" name="LogicType" value="2">' +
    '<label for="LogicType_2">通过投票比例设置条件</label>' +
    '<input type="radio" class="form-control" id="LogicType_6" name="LogicType" value="6">' +
    '<label for="LogicType_6">通过意见立场判断</label>' +
    '<input type="radio" class="form-control" id="LogicType_3" name="LogicType" value="3">' +
    '<label for="LogicType_3">通过办理人归属判断</label>' +
    '<input type="radio" class="form-control" id="LogicType_7" name="LogicType" value="7">' +
    '<label for="LogicType_7">通过职等职级判断</label>' +
    '<input type="radio" name="LogicType" id="LogicType_5" value="5">' +
    '<label class="radio-inline" for="LogicType_5">自定义条件</label>' +
    '<input type="radio" class="form-control" id="LogicType_4" name="LogicType" value="4">' +
    '<label for="LogicType_4">逻辑条件</label>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="fieldName" class="well-form-label control-label" style="padding-right:10px;">与前一个条件的逻辑关系</label>' +
    '<div class="well-form-control" style="width: 200px;">' +
    '<input type="text" class="form-control" id="AndOr" name="AndOr" >' +
    '</div>' +
    '</div>' +
    '<div class="form-group Logic_ID_Field">' +
    '<div class="well-form-control" style="width: 100%;">' +
    '<div class="inputAndSelect" style="width: 75px;">' +
    '<input type="text" name="LBracket" id="LBracket">' +
    '</div>' +
    '<div class="inputAndSelect">' +
    '<input type="hidden" name="FieldNameLabel" id="FieldNameLabel">' +
    '<input type="hidden" name="FieldName" id="FieldName">' +
    '</div>' +
    '<span style="line-height: 35px;">&nbsp;&nbsp;</span>' +
    '<div class="inputAndSelect" style="width: 114px;">' +
    '<input type="text" name="Operator" id="Operator">' +
    '</div>' +
    '<div class="inputAndSelect">' +
    '<input type="text" class="form-control" name="Value" id="Value" title="支持文本/数字类型的数据,文本要用单或双引号括起来,引号不能跟带有单或双引号的文本冲突."/>' +
    '</div>' +
    '<div class="inputAndSelect" style="display:none;">' +
    '<input type="text" class="form-control" name="Value2" id="Value2"/>' +
    '</div>' +
    '<div class="inputAndSelect" style="width: 95px;">' +
    '<select type="text" id="formValueType" name="formValueType">' +
    '<option value="1">常量</option>' +
    '<option value="2">字段值</option>' +
    '</select>' +
    '</div>' +
    '<div class="inputAndSelect"  style="width: 75px;">' +
    '<input type="text" id="RBracket" name="RBracket">' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group Logic_ID_Vote" style="display: none;">' +
    '<div class="well-form-control" style="width: 100%;">' +
    '<div class="inputAndSelect"  style="width: 75px;">' +
    '<input type="text" name="vLBracket" id="vLBracket">' +
    '</div>' +
    '<div class="inputAndSelect">' +
    '<input type="text" name="VoteOption" id="VoteOption">' +
    '</div>' +
    '<div class="inputAndSelect" style="width: 95px;">' +
    '<input type="text" name="vOperator" id="vOperator">' +
    '</div>' +
    '<div class="inputAndSelect">' +
    '<input type="text" name="vValue" id="vValue" placeholder="请输入所占百分比！">' +
    '</div>' +
    '<div class="inputAndSelect" style="display:none;">' +
    '<input type="text" class="form-control" name="vValue2" id="vValue2"/>' +
    '</div>' +
    '<div class="inputAndSelect" style="width: 95px;">' +
    '<select type="text" id="voteValueType" name="voteValueType">' +
    '<option value="1">常量</option>' +
    '<option value="2">字段值</option>' +
    '</select>' +
    '</div>' +
    '<div class="inputAndSelect"  style="width: 75px;">' +
    '<input type="text" id="vRBracket" name="vRBracket">' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group Logic_ID_Opinion_Position" style="display: none;">' +
    '	<div class="well-form-control" style="width: 100%;">' +
    '		<div class="inputAndSelect"  style="width: 75px;">' +
    '			<input type="text" name="pLBracket" id="pLBracket">' +
    '		</div>' +
    '	<div class="inputAndSelect" style="width: 200px;">' +
    '		<input type="text" name="positionValue" id="positionValue">' +
    '	</div>' +
    '	<div class="inputAndSelect" style="width: 114px;">' +
    '		<input type="text" name="pOperator" id="pOperator">' +
    '	</div>' +
    '	<div class="inputAndSelect">' +
    '		<input type="text" name="positionOption" id="positionOption">' +
    '	</div>' +
    '	<div class="inputAndSelect"  style="width: 75px;">' +
    '		<input type="text" id="pRBracket" name="pRBracket">' +
    '	</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group Logic_ID_Group"  style="display: none;">' +
    '<div class="well-form-control" style="width: 100%;">' +
    '<div class="inputAndSelect"  style="width: 75px;">' +
    '<input type="text" name="gLBracket" id="gLBracket">' +
    '</div>' +
    '<div class="inputAndSelect">' +
    '<input type="text" name="GroupType" id="GroupType">' +
    '</div>' +
    '<div class="inputAndSelect" id="ID_UserField" style="display: none;">' +
    '<input type="hidden" name="UserFieldLabel" id="UserFieldLabel">' +
    '<input type="hidden" name="UserField" id="UserField">' +
    '</div>' +
    '<span style="line-height: 35px;">归属于&nbsp;&nbsp;</span>' +
    '<div class="inputAndSelect" style="width:250px;">' +
    '<input type="hidden" name="Groups" id="Groups" >' +
    '<div id="DGroups" name="DGroups" class="well-form-control org-select-container org-style3 org-select-placeholder" style="margin-bottom: 10px;width:100%;">' +
    '<input type="hidden" name="DGroup1" id="DGroup1" >' +
    '<input type="hidden" name="Group1" id="Group1" >' +
    '<input type="hidden" name="Group2" id="Group2" >' +
    '<input type="hidden" name="Group4" id="Group4" >' +
    '<input type="hidden" name="Group8" id="Group8" >' +
    '<ul class="org-select"></ul>' +
    '<i class="icon iconfont icon-ptkj-zuzhixuanze"></i>' +
    '</div>' +
    '</div>' +
    '<div class="inputAndSelect" style="display:none;">' +
    '<input type="text" class="form-control" name="orgValue2" id="orgValue2"/>' +
    '</div>' +
    '<div class="inputAndSelect" style="width: 95px;">' +
    '<select type="text" id="orgValueType" name="orgValueType">' +
    '<option value="1">组织</option>' +
    '<option value="2">字段值</option>' +
    '</select>' +
    '</div>' +
    '<div class="inputAndSelect"  style="width: 75px;">' +
    '<input type="hidden" id="gRBracket" name="gRBracket">' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group Logic_ID_Expression" style="display: none;">' +
    '	<div class="well-form-control" style="width: 100%;">' +
    '		<div class="inputAndSelect" style="width: 75px;">' +
    '			<input type="text" name="eLBracket" id="eLBracket">' +
    '		</div>' +
    '		<div class="inputAndSelect LogicValue_Expression" style="width: calc(100% - 190px); margin-right: 5px;">' +
    '			<textarea rows="5" class="form-control" id="expression" name="expression" placeholder="请输入自定义条件"></textarea>' +
    '		</div>' +
    '		<div class="inputAndSelect"  style="width: 75px;">' +
    '			<input type="hidden" id="eRBracket" name="eRBracket">' +
    '		</div>' +
    '	</div>' +
    '	<div class="inputAndSelect LogicValue_Expression tip-text mt10">动态表单变量使用${dyform.表单字段}。变量支持>、>=、<、<=、==、!=、contains、not contains等操作。字符串常量用单引号括起来，多个条件用逻辑与&&、逻辑或||、左右括号等组合。</div>' +
    '</div>' +
    '<div class="form-group Logic_ID_Duty" style="display: none;">' +
    '	<div class="well-form-control" style="width: 100%;">' +
    '		<div class="inputAndSelect" style="width: 75px;">' +
    '			<input type="text" name="dLBracket" id="dLBracket">' +
    '		</div>' +
    '   <div class="inputAndSelect">' +
    '     <input type="text" name="DutyId" id="DutyId">' +
    '   </div>' +
    '   <div class="inputAndSelect" style="width: 114px;">' +
    '     <input type="text" name="dOperator" id="dOperator">' +
    '   </div>' +
    '   <div class="inputAndSelect" style="width:95px;">' +
    '     <div class="level-num-wrap">' +
    '       <input type="text" class="form-control" name="dValue1" id="dValue1"/>' +
    '       <div class="level-num-arrows">' +
    '         <span class="num-arrows-up"><i class="iconfont icon-ptkj-xianmiaojiantou-shang"></i></span>' +
    '         <span class="num-arrows-down"><i class="iconfont icon-ptkj-xianmiaojiantou-xia"></i></span>' +
    '       </div>' +
    '     </div>' +
    '   </div>' +
    '   <div class="inputAndSelect" style="display: none;">' +
    '     <input type="hidden" class="form-control" name="dValue2" id="dValue2"/>' +
    '     <input type="text" class="form-control" name="dValueName2" id="dValueName2"/>' +
    '   </div>' +
    '   <div class="inputAndSelect" style="display: none;">' +
    '     <input type="text" class="form-control" name="dValue3" id="dValue3"/>' +
    '   </div>' +
    '   <div class="inputAndSelect" style="width:95px;">' +
    '     <select type="text" id="dValueType" name="dValueType">' +
    '       <option value="1">常量</option>' +
    '       <option value="2">字段值</option>' +
    '     </select>' +
    '   </div>' +
    '		<div class="inputAndSelect"  style="width: 75px;">' +
    '			<input type="hidden" id="dRBracket" name="dRBracket">' +
    '		</div>' +
    '	</div>' +
    '</div>' +
    '</div>' +
    '</form>';
  return html;
}

function bInitLogicValue(laArg, $conditionDialog) {
  var logicType = laArg['type'];
  var gsText = laArg['text'];
  var gsValue = laArg['value'];
  var gbIsLogic = laArg['logic'];
  var gsOptNames = laArg['optnames'];
  var goForm = $('#logicForm', $conditionDialog)[0];

  if (gsValue == null || gsValue == '') {
    return false;
  }
  var lsValue = gsValue;
  // 自定义表达式
  if (logicType == 16) {
    $("input[name='LogicType'][value='5']", $conditionDialog).attr('checked', true).trigger('change');
    if (lsValue.indexOf(' & ') == 0) {
      if (gbIsLogic) {
        $('#AndOr', $conditionDialog).val('&').trigger('change');
      }
      lsValue = lsValue.substring(3, lsValue.length);
    } else {
      if (lsValue.indexOf(' | ') == 0) {
        if (gbIsLogic) {
          $('#AndOr', $conditionDialog).val('|').trigger('change');
        }
        lsValue = lsValue.substring(3, lsValue.length);
      }
    }
    if (lsValue.indexOf('(') == 0) {
      $('#eLBracket', $conditionDialog).val('(').trigger('change');
      lsValue = lsValue.substring(1, lsValue.length);
    }
    if (lsValue.lastIndexOf(')') == lsValue.length - 1) {
      $('#eRBracket', $conditionDialog).val(')').trigger('change');
      lsValue = lsValue.substring(0, lsValue.length - 1);
    }
    goForm.expression.value = lsValue;
  } else if (logicType == 32) {
    // 通过意见立场判断
    $("input[name='LogicType'][value='6']", $conditionDialog).attr('checked', true).trigger('change');
    if (lsValue.indexOf(' & ') == 0) {
      if (gbIsLogic) {
        $('#AndOr', $conditionDialog).val('&').trigger('change');
      }
      lsValue = lsValue.substring(3, lsValue.length);
    } else {
      if (lsValue.indexOf(' | ') == 0) {
        if (gbIsLogic) {
          $('#AndOr', $conditionDialog).val('|').trigger('change');
        }
        lsValue = lsValue.substring(3, lsValue.length);
      }
    }
    if (lsValue.indexOf('(') == 0) {
      $('#pLBracket', $conditionDialog).val('(').trigger('change');
      lsValue = lsValue.substring(1, lsValue.length);
    }
    if (lsValue.lastIndexOf(')') == lsValue.length - 1) {
      $('#pRBracket', $conditionDialog).val(')').trigger('change');
      lsValue = lsValue.substring(0, lsValue.length - 1);
    }
    var positionValues = lsValue.split(' ');
    if (positionValues.length == 3) {
      goForm.positionValue.value = positionValues[0];
      $('#positionValue', $conditionDialog).trigger('change');
      goForm.pOperator.value = positionValues[1];
      goForm.positionOption.value = positionValues[2];
    }
  } else if (lsValue.indexOf('@ISMEMBER(') != -1) {
    $("input[name='LogicType'][value='3']", $conditionDialog).attr('checked', true).trigger('change');
    var lsTemp = lsValue.substring(0, lsValue.indexOf('@ISMEMBER('));
    if (gbIsLogic) {
      if (lsTemp.indexOf('&') != -1) {
        $('#AndOr', $conditionDialog).val('&').trigger('change');
      } else {
        if (lsTemp.indexOf('|') != -1) {
          $('#AndOr', $conditionDialog).val('|').trigger('change');
        }
      }
    }
    if (lsTemp.indexOf('(') != -1) {
      $('#gLBracket', $conditionDialog).val('(').trigger('change');
    }
    lsTemp = lsValue.substring(lsValue.lastIndexOf('")') + 2, lsValue.length);
    if (lsTemp.indexOf(')') != -1) {
      $('#gRBracket', $conditionDialog).val(')').trigger('change');
    }
    lsValue = lsValue.substring(lsValue.indexOf('@ISMEMBER("') + 11, lsValue.lastIndexOf('")'));
    var laValue = lsValue.split('","');

    if (laValue[0] != '<CURUSER>') {
      // goForm.GroupType[1].checked = true;
      goForm.GroupType.value = 0;
      $('#ID_UserField', $conditionDialog).show();
      goForm.UserField.value = laValue[0];
    } else if (laValue[0] == '<CURUSER>') {
      goForm.GroupType.value = 1;
    }
    var orgFullValue = laValue[1];
    if (orgFullValue != null && orgFullValue.indexOf(':') != -1) {
      var values = orgFullValue.split(':');
      goForm.orgValueType.value = values[1];
      if (values[1] == '1') {
        goForm.Groups.value = values[0];
      } else {
        goForm.orgValue2.value = values[0];
      }
    } else {
      goForm.orgValueType.value = '1';
      goForm.Groups.value = orgFullValue;
    }
    $('#orgValueType', $conditionDialog).trigger('change');
    if (goForm.Groups.value != null && $.trim(goForm.Groups.value) != '') {
      var lsText = gsText.substring(gsText.indexOf('@ISMEMBER("') + 11, gsText.lastIndexOf('")'));
      var allData = {};
      allData.unitLabel = goForm.Groups.value.split(',')[0] == '' ? [] : goForm.Groups.value.split(',')[0].split(';');
      allData.unitValue = goForm.Groups.value.split(',')[1] == '' ? [] : goForm.Groups.value.split(',')[1].split(';');
      allData.formField = goForm.Groups.value.split(',')[2] == '' ? [] : goForm.Groups.value.split(',')[2].split(';');
      allData.tasks = goForm.Groups.value.split(',')[3] == '' ? [] : goForm.Groups.value.split(',')[3].split(';');
      allData.options = goForm.Groups.value.split(',')[4] == '' ? [] : goForm.Groups.value.split(',')[4].split(';');
      renderUnitField(allData, goForm, 'Group');
    }
  } else if (lsValue.indexOf('@DUTYGRADE(') != -1) {
    $("input[name='LogicType'][value='7']", $conditionDialog).attr('checked', true).trigger('change');
    var lsTemp = lsValue.substring(0, lsValue.indexOf('@DUTYGRADE('));
    if (gbIsLogic) {
      if (lsTemp.indexOf('&') != -1) {
        $('#AndOr', $conditionDialog).val('&').trigger('change');
      } else {
        if (lsTemp.indexOf('|') != -1) {
          $('#AndOr', $conditionDialog).val('|').trigger('change');
        }
      }
    }
    if (lsTemp.indexOf('(') != -1) {
      $('#dLBracket', $conditionDialog).val('(').trigger('change');
    }
    lsTemp = lsValue.substring(lsValue.lastIndexOf('")') + 2, lsValue.length);
    if (lsTemp.indexOf(')') != -1) {
      $('#dRBracket', $conditionDialog).val(')').trigger('change');
    }
    lsValue = lsValue.substring(lsValue.indexOf('@DUTYGRADE("') + 12, lsValue.lastIndexOf('")'));
    var laValue = lsValue.split(' ');
    $('#DutyId', $conditionDialog).val(laValue[0]).trigger('change');
    // bug#59107
    setTimeout(function () {
      $('#DutyId', $conditionDialog).trigger('change');
    }, 100);
    $('#dOperator', $conditionDialog).val(laValue[1]).trigger('change');
    var dValue = laValue[2].split(':');
    $('#dValueType', $conditionDialog).val(dValue[1]).trigger('change');
    if (dValue[1] == '2') {
      $('#dValue3', $conditionDialog).val(dValue[0]).trigger('change');
    } else {
      if (laValue[0] == 'A1' || laValue[0] == 'A3') {
        $('#dValue1', $conditionDialog).val(dValue[0]);
      } else {
        var valueName2 = dValue[0];
        ////参数有List<String>时，要用 values.join(",")
        $.ajax({
          url: ctx + '/proxy//api/org/duty/hierarchy/getMultiOrgJobRankDetail',
          type: 'get',
          data: {
            uuid: valueName2
          },
          contentType: 'application/x-www-form-urlencoded',
          success: function (result) {
            if (result.code == 0) {
              var orgJobRank = result.data;
              //设置真实值和显示值
              $('#dValueName2', $conditionDialog).comboTree('renderTreeLabel', {
                label: orgJobRank.jobRank,
                value: orgJobRank.uuid
              });
            } else {
            }
          },
          error: function (data) { }
        });
      }
    }
  } else {
    if (lsValue.indexOf('[VOTE=') != -1) {
      $("input[name='LogicType'][value='2']", $conditionDialog).attr('checked', true).trigger('change');
      if (lsValue.indexOf(' & ') == 0) {
        if (gbIsLogic) {
          $('#AndOr', $conditionDialog).val('&').trigger('change');
        }
        lsValue = lsValue.substring(3, lsValue.length);
      } else {
        if (lsValue.indexOf(' | ') == 0) {
          if (gbIsLogic) {
            $('#AndOr', $conditionDialog).val('|').trigger('change');
          }
          lsValue = lsValue.substring(3, lsValue.length);
        }
      }
      if (lsValue.indexOf('(') == 0) {
        $('#vLBracket', $conditionDialog).val('(').trigger('change');
        lsValue = lsValue.substring(1, lsValue.length);
      }
      if (lsValue.lastIndexOf(')') == lsValue.length - 1) {
        $('#vRBracket', $conditionDialog).val(')').trigger('change');
        lsValue = lsValue.substring(0, lsValue.length - 1);
      }
      $('#VoteOption', $conditionDialog)
        .val(lsValue.substr(0, lsValue.indexOf(' ')))
        .trigger('change');
      lsValue = lsValue.substr(lsValue.indexOf(' ') + 1);
      $('#vOperator', $conditionDialog)
        .val(lsValue.substr(0, lsValue.indexOf(' ')))
        .trigger('change');
      var vFullValue = lsValue.substr(lsValue.indexOf(' ') + 1);
      if (vFullValue != null && vFullValue.indexOf(':') != -1) {
        var values = vFullValue.split(':');
        goForm.voteValueType.value = values[1];
        if (values[1] == '1') {
          goForm.vValue.value = values[0];
        } else {
          goForm.vValue2.value = values[0];
        }
      } else {
        goForm.voteValueType.value = '1';
        goForm.vValue.value = lsValue.substr(lsValue.indexOf(' ') + 1);
      }
      $('#voteValueType', $conditionDialog).trigger('change');
    } else if ($.trim(lsValue) == '&' || $.trim(lsValue) == '|') {
      // 逻辑条件
      $("input[name='LogicType'][value='4']", $conditionDialog).attr('checked', true).trigger('change');
      if ($.trim(lsValue) == '&') {
        $('#AndOr', $conditionDialog).val('&').trigger('change');
      } else {
        if ($.trim(lsValue) == '|') {
          $('#AndOr', $conditionDialog).val('|').trigger('change');
        }
      }
    } else {
      $("input[name='LogicType'][value='1']", $conditionDialog).attr('checked', true).trigger('change');
      if (lsValue.indexOf(' & ') == 0) {
        if (gbIsLogic) {
          $('#AndOr', $conditionDialog).val('&').trigger('change');
        }
        lsValue = lsValue.substring(3, lsValue.length);
      } else {
        if (lsValue.indexOf(' | ') == 0) {
          if (gbIsLogic) {
            $('#AndOr', $conditionDialog).val('|').trigger('change');
          }
          lsValue = lsValue.substring(3, lsValue.length);
        }
      }
      if (lsValue.indexOf('(') == 0) {
        $('#LBracket', $conditionDialog).val('(').trigger('change');
        lsValue = lsValue.substring(1, lsValue.length);
      }
      if (lsValue.lastIndexOf(')') == lsValue.length - 1) {
        $('#RBracket', $conditionDialog).val(')').trigger('change');
        lsValue = lsValue.substring(0, lsValue.length - 1);
      }
      goForm.FieldName.value = lsValue.substr(0, lsValue.indexOf(' '));
      lsValue = lsValue.substr(lsValue.indexOf(' ') + 1);
      $('#Operator', $conditionDialog)
        .val(lsValue.substr(0, lsValue.indexOf(' ')))
        .trigger('change');
      var fullValue = lsValue.substr(lsValue.indexOf(' ') + 1);
      if (fullValue != null && fullValue.indexOf(':') != -1) {
        var values = fullValue.split(':');
        goForm.formValueType.value = values[1];
        if (values[1] == '1') {
          goForm.Value.value = values[0];
        } else {
          goForm.Value2.value = values[0];
        }
      } else {
        goForm.formValueType.value = '1';
        goForm.Value.value = lsValue.substr(lsValue.indexOf(' ') + 1);
      }
      $('#formValueType', $conditionDialog).trigger('change');
      //goForm.Value.value = lsValue.substr(lsValue.indexOf(' ') + 1);
    }
  }

  return true;
}

function aLogicCheckValue($conditionDialog) {
  var goForm = $('#logicForm', $conditionDialog)[0];
  if ($('#LogicType_1', goForm)[0].checked == true) {
    if (goForm.FieldName.value == '') {
      top.appModal.error('请选择字段!');
      return false;
    }
    if (goForm.Operator.value == '') {
      top.appModal.error('请选择操作符!');
      return false;
    }
    if (goForm.Value2.value == '' && goForm.formValueType.value == '2') {
      top.appModal.error('请选择字段!');
      return false;
    } else if (goForm.Value.value == '' && goForm.formValueType.value == '1') {
      top.appModal.error('请输入常量!');
      return false;
    }
  } else if ($('#LogicType_2', goForm)[0].checked == true) {
    if (goForm.vValue.value == '' && goForm.voteValueType.value == '1') {
      top.appModal.error('请输入所占百分比!');
      return false;
    } else if (goForm.vValue2.value == '' && goForm.voteValueType.value == '2') {
      top.appModal.error('请选择字段!');
      return false;
    } else {
      if (goForm.vValue.value.indexOf('%') != -1) {
        top.appModal.error('不需要在所占百分比中输入百分号“％”!');
        return false;
      }
    }
  } else if ($('#LogicType_3', goForm)[0].checked == true) {
    if (goForm.GroupType.value == '') {
      top.appModal.error('请指定成员!');
      return false;
    }
    if (goForm.GroupType.value == '0' && goForm.UserField.value == '') {
      top.appModal.error('请选择字段!');
      return false;
    }

    if ($('#DGroups .org-entity', $conditionDialog).length <= 0 && goForm.orgValueType.value == '1') {
      top.appModal.error('请选择人员!');
      return false;
    }
    if (goForm.orgValue2.value == '' && goForm.orgValueType.value == '2') {
      top.appModal.error('请选择字段!');
      return false;
    }
  } else if ($('#LogicType_4', goForm)[0].checked == true) {
    if ($.trim($("input[name='AndOr']", $conditionDialog).val()) == '') {
      top.appModal.error('请选择逻辑操作符!');
      return false;
    }
  } else if ($('#LogicType_6', goForm)[0].checked == true) {
    if (goForm.positionValue.value == '') {
      top.appModal.error('请选择意见立场值!');
      return false;
    }
    if (goForm.pOperator.value == '') {
      top.appModal.error('请选择操作符!');
      return false;
    }
    if (goForm.positionOption.value == '') {
      top.appModal.error('请选择意见立场!');
      return false;
    }
  } else if ($('#LogicType_7', goForm)[0].checked == true) {
    if (goForm.DutyId.value == '') {
      top.appModal.error('请选择职等职级!');
      return false;
    }
    if (goForm.dOperator.value == '') {
      top.appModal.error('请选择操作符!');
      return false;
    }
    if (goForm.dValueType.value == '1') {
      if (goForm.DutyId.value == 'A1' || goForm.DutyId.value == 'A3') {
        if (goForm.dValue1.value == '') {
          top.appModal.error('请输入职等!');
          return false;
        } else {
          if (!/(^[0-9]\d*$)/.test(goForm.dValue1.value)) {
            top.appModal.error('输入的不是正整数!');
            return false;
          }
        }
      } else {
        if (goForm.dValue2.value == '') {
          top.appModal.error('请选择职级!');
          return false;
        }
      }
    } else {
      if (goForm.dValue3.value == '') {
        top.appModal.error('请选择字段值!');
        return false;
      }
    }
  }
  return true;
}

function aLogicBuildValue($conditionDialog) {
  var goForm = $('#logicForm', $conditionDialog)[0];
  goForm.Groups.value =
    $("input[name='DGroup1']", goForm).val() +
    ',' +
    $("input[name='Group1']", goForm).val() +
    ',' +
    $("input[name='Group2']", goForm).val() +
    ',' +
    $("input[name='Group4']", goForm).val() +
    ',' +
    $("input[name='Group8']", goForm).val();

  var laReturn = new Array();
  var lsDLogic = '',
    lsLogic = '';
  if (goForm.AndOr.value != '') {
    lsDLogic = $('#AndOr', $conditionDialog).wellSelect('data').text;
    lsLogic = ' ' + goForm.AndOr.value + ' ';
  }
  if ($('#LogicType_1', goForm)[0].checked == true) {
    var lsOperator = goForm.Operator.value;
    var lsDOperator = $('#Operator', $conditionDialog).wellSelect('data').text;
    var lsLBracket = goForm.LBracket.value;
    var lsRBracket = goForm.RBracket.value;
    // 值:值类型1、常量2、字段值
    var cValueType = goForm.formValueType.value;
    var cValue = goForm.Value.value;
    var dValue = cValue;
    if (cValueType == '2') {
      cValue = goForm.Value2.value;
      dValue = $('#Value2', $conditionDialog).wellSelect('data').text;
    }
    var lsDCondition =
      lsDLogic +
      lsLBracket +
      '[' +
      $('#FieldName', $conditionDialog).wellSelect('data').text +
      '] ' +
      lsDOperator +
      ' ' +
      dValue +
      lsRBracket;
    var lsCondition = lsLogic + lsLBracket + goForm.FieldName.value + ' ' + lsOperator + ' ' + cValue + ':' + cValueType + lsRBracket;
    if (goForm.FieldName.value != -1) {
      laReturn.push(lsDCondition);
      laReturn.push(lsCondition);
      laReturn.push(1);
    }
  } else if ($('#LogicType_2', goForm)[0].checked == true) {
    var lsDOperator = $('#vOperator', $conditionDialog).wellSelect('data').text;
    var lsOperator = goForm.vOperator.value;
    var lsLBracket = goForm.vLBracket.value;
    var lsRBracket = goForm.vRBracket.value;
    // 值:值类型1、常量2、字段值
    var cValueType = goForm.voteValueType.value;
    var cValue = goForm.vValue.value;
    var dValue = cValue;
    if (cValueType == '2') {
      cValue = goForm.vValue2.value;
      dValue = $('#vValue2', $conditionDialog).wellSelect('data').text;
    }
    var lsDVoteOption = $('#VoteOption', $conditionDialog).wellSelect('data').text;
    var lsVoteOption = goForm.VoteOption.value;
    var lsDCondition = lsDLogic + lsLBracket + '[' + lsDVoteOption + '] ' + lsDOperator + ' ' + dValue + '%' + lsRBracket;
    var lsCondition = lsLogic + lsLBracket + lsVoteOption + ' ' + lsOperator + ' ' + cValue + ':' + cValueType + lsRBracket;
    laReturn.push(lsDCondition);
    laReturn.push(lsCondition);
    laReturn.push(2);
  } else if ($('#LogicType_3', goForm)[0].checked == true) {
    var lsLBracket = goForm.gLBracket.value;
    var lsRBracket = goForm.gRBracket.value;
    // 值:值类型1、常量2、字段值
    var cValueType = goForm.orgValueType.value;
    var cValue = goForm.Groups.value;
    var dValue = getDGroupsValue($conditionDialog);
    if (cValueType == '2') {
      cValue = goForm.orgValue2.value;
      dValue = $('#orgValue2', $conditionDialog).wellSelect('data').text;
    }
    if (goForm.GroupType.value == '1') {
      var lsField = '<CURUSER>';
      goForm.UserFieldLabel.value = '';
    } else {
      var lsField = goForm.UserField.value;
    }
    var DGroupsValue = dValue;
    var lsDCondition = lsDLogic + lsLBracket + '@ISMEMBER("' + goForm.UserFieldLabel.value + '","' + DGroupsValue + '")' + lsRBracket;
    var lsCondition = lsLogic + lsLBracket + '@ISMEMBER("' + lsField + '","' + cValue + ':' + cValueType + '")' + lsRBracket;
    laReturn.push(lsDCondition);
    laReturn.push(lsCondition);
    laReturn.push(4);
  } else if ($('#LogicType_4', goForm)[0].checked == true) {
    laReturn.push($('#AndOr', $conditionDialog).wellSelect('data').text + ' ');
    laReturn.push(' ' + goForm.AndOr.value + ' ');
    laReturn.push(8);
  } else if ($('#LogicType_5', goForm)[0].checked == true) {
    var eLBracket = goForm.eLBracket.value;
    var eRBracket = goForm.eRBracket.value;
    var expression = goForm.expression.value;
    laReturn.push(lsDLogic + eLBracket + '[自定义条件]' + expression + eRBracket);
    laReturn.push(lsLogic + eLBracket + expression + eRBracket);
    laReturn.push(16);
  } else if ($('#LogicType_6', goForm)[0].checked == true) {
    var pLBracket = goForm.pLBracket.value;
    var pRBracket = goForm.pRBracket.value;
    var positionValue = goForm.positionValue.value;
    var pOperatorText = $('#pOperator', $conditionDialog).wellSelect('data').text;
    var pOperator = goForm.pOperator.value;
    var positionOptionText = $('#positionOption', $conditionDialog).wellSelect('data').text;
    var positionOption = goForm.positionOption.value;
    if (positionOptionText != null && positionOptionText.indexOf('|') != -1) {
      var positionOptionArray = positionOptionText.split('|');
      positionOptionText = positionOptionArray[0] + '(' + positionOption + ')';
    }
    laReturn.push(lsDLogic + pLBracket + '[意见立场]' + ' ' + pOperatorText + ' ' + positionOptionText + pRBracket);
    laReturn.push(lsLogic + pLBracket + positionValue + ' ' + pOperator + ' ' + positionOption + pRBracket);
    laReturn.push(32);
  } else if ($('#LogicType_7', goForm)[0].checked == true) {
    var dLBracket = goForm.dLBracket.value;
    var dRBracket = goForm.dRBracket.value;
    var DutyName = $('#DutyId', $conditionDialog).wellSelect('data').text;
    var DutyId = goForm.DutyId.value;
    var dOperatorText = $('#dOperator', $conditionDialog).wellSelect('data').text;
    var dOperator = goForm.dOperator.value;
    var dValueType = goForm.dValueType.value;
    var dValue = '';
    var dValueText = '';
    if (dValueType == '1') {
      if (DutyId == 'A1' || DutyId == 'A3') {
        dValueText = goForm.dValue1.value;
        dValue = goForm.dValue1.value;
      } else {
        dValueText = goForm.dValueName2.value;
        dValue = goForm.dValue2.value;
      }
    } else {
      dValueText = $('#dValue3', $conditionDialog).wellSelect('data').text;
      dValue = goForm.dValue3.value;
    }

    laReturn.push(lsDLogic + dLBracket + '[' + DutyName + ']' + ' ' + dOperatorText + ' ' + dValueText + dRBracket);
    laReturn.push(lsLogic + dLBracket + '@DUTYGRADE("' + DutyId + ' ' + dOperator + ' ' + dValue + ':' + dValueType + '")' + dRBracket);
    laReturn.push(64);
  }
  return laReturn;
}

function getDGroupsValue($conditionDialog) {
  var value1 = $('#DGroup1', $conditionDialog).val();
  var data = [
    {
      field: 'Group2',
      tag1: '{',
      tag2: '}'
    },
    {
      field: 'Group4',
      tag1: '<',
      tag2: '>'
    },
    {
      field: 'Group8',
      tag1: '[',
      tag2: ']'
    }
  ];
  var value2 = getDGroupData($conditionDialog, data);

  var allValue = [];
  if (value1 != '') {
    allValue.push(value1);
  }
  if (value2.length > 0) {
    allValue = allValue.concat(value2);
  }
  return allValue.join(';');
}

function getDGroupData($conditionDialog, data) {
  var allData = [];
  for (var i = 0; i < data.length; i++) {
    var value = $('#' + data[i].field, $conditionDialog).val();
    if (value != '') {
      var val = [];
      var newValue = value.split(';');
      for (var j = 0; j < newValue.length; j++) {
        if (data[i].field == 'Group2') {
          var itemArr = newValue[j].split(':');
          var showName = '';
          if (itemArr.length === 1) {
            showName = formFieldFormat(itemArr[0]);
          } else {
            showName = formFieldFormat(itemArr[1], itemArr[0]);
          }
          val.push(data[i].tag1 + showName + data[i].tag2);
        } else if (data[i].field == 'Group4') {
          $.each(newValue, function (i, item) {
            $.each(goWorkFlow.tasks, function (i, item2) {
              if (item2.xmlObject.context.id === item) {
                val.push(data[i].tag1 + item2.htmlObject.textContent + data[i].tag2);
              }
            });
          });
        } else if (data[i].field == 'Group8') {
          val.push(data[i].tag1 + handlerListAll[newValue[j]] + data[i].tag2);
        }
      }
      allData.push(val.join(';'));
    }
  }
  return allData;
}
