var goWorkFlow = new Object();
goWorkFlow.cursorX = 0;
goWorkFlow.cursorY = 0; // 鼠标坐标；
goWorkFlow.curAddObject = null; // 当前要创建对象的类型；
goWorkFlow.curObjectName = null; // 当前选中对象名称；
goWorkFlow.tasks = new Array(); // 对象节点和文字标签的对象数组；
goWorkFlow.lines = new Array(); // 线条数组；
goWorkFlow.eWFJG = null; // 当前工作的画板；
goWorkFlow.fromObject = null; // 画线条时开始选中的对象；
goWorkFlow.curOverObjName = null; // 当前鼠标MouseOver的对象名称；
goWorkFlow.selectTasks = new Array(); // 被选中的多个对象节点；
goWorkFlow.selectObjects = new Array(); // 被选中的多个对象；
goWorkFlow.copyObject = null; // 剪切、拷贝对象的参数存储对象；
goWorkFlow.curStatus = null; // 当前流程事件状态，如导入、保存、检查错误、显示流转历史等；
goWorkFlow.isShowLineName = true; // 是否显示线条名称；
goWorkFlow.isShowLabel = true; // 是否显示注释标签；
goWorkFlow.dynamicTask = new Array(); // 动态播放任务对象元素：Object.obj/Object.status/Object.msg;
goWorkFlow.intervalName = null; // 时间间隔句柄；
goWorkFlow.intervalIndex = 0; // 间隔执行的任务序号；
goWorkFlow.selected = false; // 当前是否有选中状态；
goWorkFlow.saveSetting = false; // 当前是否在保存流程定义；
goWorkFlow.newTaskAndLines = new Array(); //保存所有新增的环节及线条
goWorkFlow.resetTaskAndLineStack = new Array(); //删除的环节及线条
goWorkFlow.initNodes = new Object(); // 初始所有环节对象
goWorkFlow.changeNodeIds = new Object();
goWorkFlow.xmlDOM = null; // xmlDOM对象
goWorkFlow.dictionXML = null; // 流程字典数据XML
goWorkFlow.flowXML = null; // 当前流程定义数据XML
goWorkFlow.flowTreeXML = null; // 流程树XML
goWorkFlow.equalFlowID = null; // 当前表单设置的等价流程ID
goWorkFlow.equalFlowXML = null; // 当前流程等价流程定义数据XML
goWorkFlow.historyXML = null; // 当前工作流程历史XML
goWorkFlow.flowHandingInfo = null; // 当前工作流程的办理信息
parent.goWorkFlow = goWorkFlow;

var onlyGraphics = Browser.getQueryString('onlyGraphics')

function checkKey() {
  return true;
}

function loadEvent() {
  // 工作区的Document Load事件
  goWorkFlow.eWFJG = new jsGraphics('ID_WORKAROUND');
  goWorkFlow.xmlDOM = oGetXMLDocument_();

  var _id = onlyGraphics ? Browser.getQueryString('id') : parent.Browser.getQueryString('id');

  var lsID = '<NEW>';
  if (_id) {
    lsID = decodeURI(_id);
  }
  var flowInstUuid = onlyGraphics ? undefined : parent.Browser.getQueryString('flowInstUuid');
  var moduleId = onlyGraphics ? undefined : parent.Browser.getQueryString('moduleId');
  if (lsID !== '<NEW>') {
  } else {
    goWorkFlow.isNewFlow = true;
    top.document.title = '流程设计器';
  }
  onlyGraphics ? '' : top.appModal.showMask();
  $.ajax({
    url: '/workflow/scheme/diction/xml.action?open&id=' + lsID + '&moduleId=' + moduleId,
    type: 'GET',
    dataType: 'xml',
    cache: false,
    error: function (data) { },
    success: function (data) {
      // dictionXML转化为jQuery的XML对象
      goWorkFlow.dictionXML = $(data).selectSingleNode('diction');
      if (lsID !== '<NEW>' && !parent.clearObject) {
        $.ajax({
          url: '/workflow/scheme/flow/xml.action?open&id=' + lsID,
          type: 'GET',
          dataType: 'xml',
          cache: false,
          error: function (data) { },
          success: function (data) {
            // flowXML转化为jQuery的XML对象
            var flow = $(data).selectSingleNode('flow');
            // 创建节点
            var nodes = ['timers', 'tasks', 'directions', 'labels', 'deletes'];
            for (let i = 0, len = nodes.length; i < len; i++) {
              let n = nodes[i];
              if (flow.selectSingleNode(n) == null) {
                flow.appendChild(goWorkFlow.xmlDOM.createElement(n));
              }
            }

            goWorkFlow.flowXML = flow;
            if (goWorkFlow.flowXML != null) {
              goWorkFlow.flowXMLStr = goWorkFlow.flowXML.context.outerHTML;
              var equalFlow = goWorkFlow.flowXML.selectSingleNode('./property/equalFlow/name').text();
              bLoadObject(flowInstUuid);
              initOtherInfo();
              if (onlyGraphics) {
                initMoveCenter();
                return;
              }
              var designer = $('#newWorkFlowDesigner', top.document)[0].contentWindow;
              designer.initMoveCenter();
              if (equalFlow) {
                goWorkFlow.equalStatus = true;
                var designerElement = $(
                  '.img_line,.img_task,.img_begin,.img_end,.img_subflow,.img_condition,span[type="BEELINE"],span[type="CURVE"]',
                  designer.document
                );
                designerElement.hide();
              }
            } else {
              console.log(parent.sGetLang('FLOW_WF_ERRORDOWNLOAD'));
            }

            // 查阅模式 - 仅可以查看流程画布，不能查看流程属性
            var onlyView = parent.Browser.getQueryString('onlyView');
            if (onlyView) {
              goWorkFlow.onlyView = true;
              onlyViewMode();
              return;
            }

            // 只读模式 - 可以查看流程属性、环节属性、流向属性等
            var readonly = parent.Browser.getQueryString('readOnly');
            if (readonly) {
              goWorkFlow.readonlyMode = true;
              readonlyMode();
              return;
            }

            // 加载右边流程属性
            $(document).trigger('dblclick');
          }
        });
      } else {
        showOrHideEMpty('show');
        var txt = '<flow><property></property><timers /><tasks></tasks><directions></directions><labels /><deletes /></flow>';
        goWorkFlow.xmlDOM = $.parseXML(txt);
        goWorkFlow.flowXML = $(goWorkFlow.xmlDOM).selectSingleNode('flow');
        if (parent.clearObjectUuid != null) {
          goWorkFlow.flowXML.setAttribute('uuid', parent.clearObjectUuid);
        }
        // 加载右边流程属性
        $(document).trigger('dblclick');
        top.appModal.hideMask();
      }
    }
  });
}

function mouseOver(event) {
  // 工作区的MouseOver事件
  getSelectObject();
  if (goWorkFlow.curAddObject === 'CURVE' && goWorkFlow.fromObject != null && goWorkFlow.fromObject.cursorWay == null) {
    goWorkFlow.fromObject = null;
  }
  return true;
}

function mouseOut(event) {
  // 工作区的MouseOut事件
  if (typeof ID_BROKENCURVE === 'object') {
    $('#ID_BROKENCURVE').remove();
  }
  if (typeof ID_BROKENBEELINE === 'object') {
    $('#ID_BROKENBEELINE').remove();
  }
  return true;
}

function mouseDown(e) {
  // 工作区的MouseDown事件
  if (goWorkFlow.equalStatus) {
    return;
  }
  if (e.button === 2) {
    //鼠标右键
    if ($.inArray(goWorkFlow.curAddObject, ['BEELINE', 'CURVE']) > -1) {
      $('.designer-controls-tools .active', top.document).trigger('click');
      goWorkFlow.fromObject = null;
      $(ID_WORKAROUND).css('cursor', 'auto');
      return;
    }
  }

  if (!goWorkFlow.selected) {
    bSetObjectStatus(goWorkFlow.curObjectName, false);
  } else {
    goWorkFlow.selected = true;
    return;
  }
  bShowTasks(null, false);
  bShowErrorObject(false);
  goWorkFlow.allowDeleteCurObject = true;
}

function mouseUp(event) {
  // 工作区的MouseUp事件
}

function mouseMove(event) {
  // 工作区的MouseMove事件
  goWorkFlow.offsetTime = 1;
  if (goWorkFlow.curOverObjName != null && (goWorkFlow.curAddObject === 'BEELINE' || goWorkFlow.curAddObject === 'CURVE')) {
    var loObject = oGetTaskObject(goWorkFlow.curOverObjName);
    if (loObject != null && loObject.Type !== 'LABEL') {
      getCursorPosition(event);
      var loImg = loObject.imgObject;
      if (
        goWorkFlow.cursorX >= loImg.x &&
        goWorkFlow.cursorX <= loImg.x + loImg.w &&
        goWorkFlow.cursorY >= loImg.y &&
        goWorkFlow.cursorY <= loImg.y + loImg.h
      ) {
        if (goWorkFlow.curAddObject === 'CURVE') {
          var lsWay = sGetTaskCurveWay(loObject, goWorkFlow.cursorX, goWorkFlow.cursorY);
          loObject.cursorWay = lsWay;
          if (goWorkFlow.fromObject == null) {
            loImg.setCursor('themes/images/workflow/cur_' + lsWay + 'From.cur');
          } else {
            loImg.setCursor('themes/images/workflow/cur_' + lsWay + 'To.cur');
          }
        } else {
          if (goWorkFlow.fromObject == null) {
            loImg.setCursor('themes/images/workflow/cur_BeelineFrom.cur');
          } else {
            loImg.setCursor('themes/images/workflow/cur_BeelineTo.cur');
          }
        }
      }
    }
  }

  if (goWorkFlow.fromObject != null && (goWorkFlow.curAddObject === 'BEELINE' || goWorkFlow.curAddObject === 'CURVE')) {
    getCursorPosition(event);
    if (goWorkFlow.curAddObject === 'BEELINE') {
      bDrawBrokenBeeline();
    } else {
      bDrawBrokenCurve();
    }
  }

  $(document)
    .off('keydown')
    .on('keydown', function () {
      goWorkFlow.cursorX = event.pageX;
      goWorkFlow.cursorY = event.pageY;
    });
  return true;
}

function mouseClick(event) {
  // 工作区的Mouse Click事件
  removeAllFocus();
  if (goWorkFlow.equalStatus) {
    $.each(goWorkFlow.lines, function (i, item) {
      $(item.htmlObject).hide();
    });
    if (
      goWorkFlow.curAddObject != null &&
      goWorkFlow.curAddObject !== 'DEFAULT' &&
      goWorkFlow.curAddObject !== 'BEELINE' &&
      goWorkFlow.curAddObject !== 'CURVE'
    ) {
      if (goWorkFlow.equalStatus) {
        top.appModal.error('已启用等价流程，无法添加节点。');
        return;
      }
    }
    return;
  }
  // button: 0->左键 1->中键 2->右键
  if (event.button === 2) {
    bSetObjectStatus(goWorkFlow.curObjectName, false);
    return true;
  }
  if (goWorkFlow.selected) {
    goWorkFlow.selected = false;
    return;
  } else {
    bSetObjectStatus(goWorkFlow.curObjectName, false);
  }

  if (bSetLabelHTML() == true) {
    return;
  }
  showCursorPosition(event);
  if (goWorkFlow.curOverObjName != null) {
    return;
  }
  if (
    goWorkFlow.curAddObject != null &&
    goWorkFlow.curAddObject !== 'DEFAULT' &&
    goWorkFlow.curAddObject !== 'BEELINE' &&
    goWorkFlow.curAddObject !== 'CURVE'
  ) {
    if (goWorkFlow.equalStatus) {
      top.appModal.error('已启用等价流程，无法添加节点。');
      return;
    }
    var loObject = oCreateTask(goWorkFlow.curAddObject, goWorkFlow.cursorX, goWorkFlow.cursorY);
    if (goWorkFlow.curAddObject !== 'LABEL') {
      bAdjustObject(loObject);
    }
    var ele = oCreateXMLNode(loObject);
    bSetObjectText(loObject);
  }
}

function mouseDblClick(event) {
  // 工作区的Mouse Double Click事件
  if (goWorkFlow.equalStatus || goWorkFlow.onlyView) {
    return;
  }
  var loObj = event.target;
  temporarySaveProperty(false, undefined, loObj); //临时存储配置信息
  if (loObj.tagName === 'DIV' && loObj.Type === 'LINE' && loObj.parentElement != null && loObj.parentElement.tagName === 'SPAN') {
    setDirectionProperty();
    top.appModal.hideMask();
    return false;
  }
  while (loObj != null) {
    var objType = loObj.getAttribute('objtype');
    var objName = loObj.getAttribute('objname');
    if ($.inArray(objType, ['TASK', 'SUBFLOW', 'CONDITION']) > -1) {
      loObj = oGetTaskObject(objName).htmlObject;
    } else if (objType === 'LINE') {
      loObj = oGetLineObjectByLabel(objName).labelObject.htmlObject;
    }
    if (loObj.id != null && loObj.id.indexOf('img_') === 0 && loObj.id.indexOf('div') !== -1) {
      break;
    }
    loObj = loObj.parentElement;
  }
  if (loObj != null) {
    if (loObj.id.indexOf('img_label') !== -1) {
      bShowLabelInput();
    } else {
      if (loObj.id.indexOf('img_line') !== -1) {
        if (goWorkFlow.curObjectName && goWorkFlow.curObjectName.indexOf('img_line') !== -1) {
          var loObject = oGetLineObjectByLabel(goWorkFlow.curObjectName);
          if (loObject != null) {
            goWorkFlow.curObjectName = loObject.name;
          }
        } else {
          goWorkFlow.curObjectName = oGetLineObjectByLabel(objName).name;
          bSetObjectStatus(goWorkFlow.curObjectName, true);
        }
        setDirectionProperty();
      } else {
        setTaskProperty();
      }
    }
    return false;
  }
  setFlowProperty();
  top.appModal.hideMask();
}

function contextMenu(event) {
  // 工作区的右键事件
  bSetObjectStatus(goWorkFlow.curObjectName, false);

  var loObj = event.target;
  if (loObj.tagName == 'DIV' && loObj.Type == 'LINE' && loObj.parentElement != null && loObj.parentElement.tagName == 'SPAN') {
    goWorkFlow.curObjectName = loObj.parentElement.id;
    bSetObjectStatus(goWorkFlow.curObjectName, true);
    if (loObj.parentElement.Type != null) {
      var loObject = oGetLineObject(goWorkFlow.curObjectName);
      var oMenus = parent.document.getElementsByName('ID_MENU_DIRECTION');
      for (var i = 0; i < oMenus.length; i++) {
        if (oMenus[i].outerHTML.toLowerCase().indexOf(loObj.parentElement.Type.toLowerCase()) != -1) {
          oMenus[i].disabled = true;
        } else {
          if (oMenus[i].outerHTML.toLowerCase().indexOf('showlinename') != -1) {
            if (oMenus[i].outerHTML.toLowerCase().indexOf('(' + loObject.isShowName + ',') != -1) {
              oMenus[i].disabled = true;
            } else {
              oMenus[i].disabled = false;
            }
          } else {
            if (oMenus[i].status != null && oMenus[i].status == 'disable') {
              oMenus[i].disabled = true;
            } else {
              oMenus[i].disabled = false;
            }
          }
        }
      }
    }
    // showMenu("ID_MENU_DIRECTION", 80, "parent", true);
    return false;
  }
  while (loObj != null) {
    if (loObj.id != null && loObj.id.indexOf('img_') == 0 && loObj.id.indexOf('div') != -1) {
      break;
    }
    loObj = loObj.parentElement;
  }
  if (loObj != null) {
    var lsName = loObj.id.substr(0, loObj.id.indexOf('div'));
    goWorkFlow.curObjectName = lsName;
    bSetObjectStatus(goWorkFlow.curObjectName, true);
    if (lsName.indexOf('img_label') != -1) {
      // showMenu("ID_MENU_LABEL", 80, "parent", true);
    } else {
      if (lsName.indexOf('img_line') != -1) {
        var loObject = oGetLineObjectByLabel(lsName);
        goWorkFlow.curObjectName = loObject.name;
        bSetObjectStatus(goWorkFlow.curObjectName, true);
        var oMenus = parent.document.getElementsByName('ID_MENU_DIRECTION');
        for (i = 0; i < oMenus.length; i++) {
          if (oMenus[i].outerHTML.toLowerCase().indexOf(loObject.Type.toLowerCase()) != -1) {
            oMenus[i].disabled = true;
          } else {
            if (oMenus[i].outerHTML.toLowerCase().indexOf('showlinename') != -1) {
              if (oMenus[i].outerHTML.toLowerCase().indexOf('(' + loObject.isShowName + ',') != -1) {
                oMenus[i].disabled = true;
              } else {
                oMenus[i].disabled = false;
              }
            } else {
              if (oMenus[i].status != null && oMenus[i].status == 'disable') {
                oMenus[i].disabled = true;
              } else {
                oMenus[i].disabled = false;
              }
            }
          }
        }
        // showMenu("ID_MENU_DIRECTION", 80, "parent", true);
      } else {
        var oMenus = parent.document.getElementsByName('ID_MENU_TASK');
        for (i = 0; i < oMenus.length; i++) {
          if (oMenus[i].outerHTML.toLowerCase().indexOf('settaskproperty') != -1) {
            if (lsName.indexOf('img_begin') != -1 || lsName.indexOf('img_end') != -1) {
              oMenus[i].disabled = true;
            } else {
              oMenus[i].disabled = false;
            }
          } else {
            if (oMenus[i].outerHTML.toLowerCase().indexOf('before') != -1) {
              if (lsName.indexOf('img_begin') != -1) {
                oMenus[i].disabled = true;
              } else {
                oMenus[i].disabled = false;
              }
            } else {
              if (oMenus[i].outerHTML.toLowerCase().indexOf('after') != -1) {
                if (lsName.indexOf('img_end') != -1) {
                  oMenus[i].disabled = true;
                } else {
                  oMenus[i].disabled = false;
                }
              }
            }
          }
        }
        // showMenu("ID_MENU_TASK", 80, "parent", true);
      }
    }
    return false;
  }
  if (goWorkFlow.curAddObject != null && goWorkFlow.curAddObject != 'DEFAULT') {
    if (goWorkFlow.fromObject != null && (goWorkFlow.curAddObject == 'BEELINE' || goWorkFlow.curAddObject == 'CURVE')) {
      goWorkFlow.fromObject = null;
      if (typeof ID_BROKENBEELINE == 'object') {
        document.all.ID_BROKENBEELINE.outerHTML = '';
      }
      if (typeof ID_BROKENCURVE == 'object') {
        document.all.ID_BROKENCURVE.outerHTML = '';
      }
      return false;
    }
    setSelectObject('DEFAULT');
    getSelectObject();
  } else {
    var oMenus = $('#ID_MENU_FLOW', parent.document);
    for (i = 0; i < oMenus.length; i++) {
      if (oMenus[i].outerHTML.toLowerCase().indexOf('paste') != -1) {
        if (goWorkFlow.copyObject == null) {
          oMenus[i].disabled = true;
        } else {
          oMenus[i].disabled = false;
        }
        break;
      }
    }
    // showMenu("ID_MENU_FLOW", 80, "parent", true);
  }
  return false;
}

function my_PickFunc() {
  // 活动对象或者文字标签的选中事件
  if (goWorkFlow.equalStatus) {
    return;
  }
  bSetObjectStatus(goWorkFlow.curObjectName, false);
  bShowTasks(null, false);
  bShowErrorObject(false);
  bSetLabelHTML();
  var lsName = dd.obj.name;
  if (lsName.indexOf('img_line') != -1) {
    var loObject = oGetLineObjectByLabel(lsName);
    setErrorLineStatus(loObject, false);
    if (loObject != null) {
      lsName = loObject.name;
    }
  }
  bSetObjectStatus(lsName, true);
  if (
    dd.obj.name.indexOf('img_label') == -1 &&
    dd.obj.name.indexOf('img_line') == -1 &&
    (goWorkFlow.curAddObject == 'BEELINE' || goWorkFlow.curAddObject == 'CURVE')
  ) {
    if (goWorkFlow.fromObject == null) {
      goWorkFlow.fromObject = oGetTaskObject(dd.obj.name);
    } else {
      var loTo = oGetTaskObject(dd.obj.name);
      if (goWorkFlow.curAddObject == 'BEELINE') {
        if (typeof ID_BROKENBEELINE == 'object') {
          document.all.ID_BROKENBEELINE.outerHTML = '';
        }
        var loObject = oCreateBeeline(goWorkFlow.fromObject, loTo);
      } else {
        if (typeof ID_BROKENCURVE == 'object') {
          document.all.ID_BROKENCURVE.outerHTML = '';
        }
        var loObject = oCreateCurve(goWorkFlow.fromObject, loTo);
      }
      var ele = oCreateXMLNode(loObject);
      if (ele != null) {
        bSetObjectText(loObject.labelObject);
      }
      goWorkFlow.fromObject = null;
    }
  }
}

function my_DragFunc() {
  // 活动对象或者文字标签的拖动事件
  if (goWorkFlow.equalStatus) {
    return;
  }
  if (dd.obj.name.indexOf('img_label') == -1 && dd.obj.name.indexOf('img_line') == -1) {
    var loObject = oGetTaskObject(dd.obj.name);
    for (var i = 0; i < loObject.outLines.length; i++) {
      if (loObject.outLines[i] == null) {
        continue;
      }
      if (loObject.outLines[i].Type == 'BEELINE') {
        oCreateBeeline(loObject, loObject.outLines[i].toTask, null, loObject.outLines[i]);
      } else {
        oCreateCurve(loObject, loObject.outLines[i].toTask, null, loObject.outLines[i]);
      }
    }
    for (var i = 0; i < loObject.inLines.length; i++) {
      if (loObject.inLines[i] == null) {
        continue;
      }
      if (loObject.inLines[i].Type == 'BEELINE') {
        oCreateBeeline(loObject.inLines[i].fromTask, loObject, null, loObject.inLines[i]);
      } else {
        oCreateCurve(loObject.inLines[i].fromTask, loObject, null, loObject.inLines[i]);
      }
    }
  }
}

function my_ResizeFunc() {
  // 活动对象或者文字标签的放大缩小事件
}

function my_DropFunc() {
  // 活动对象或者文字标签的放下事件
  if (goWorkFlow.equalStatus) {
    return;
  }
  if (dd.obj.name.indexOf('img_label') == -1 && dd.obj.name.indexOf('img_line') == -1) {
    var loObject = oGetTaskObject(dd.obj.name);
    bAdjustObject(loObject);
    for (var i = 0; i < loObject.outLines.length; i++) {
      if (loObject.outLines[i] == null) {
        continue;
      }
      if (loObject.outLines[i].Type == 'BEELINE') {
        oCreateBeeline(loObject, loObject.outLines[i].toTask, null, loObject.outLines[i]);
      } else {
        oCreateCurve(loObject, loObject.outLines[i].toTask, null, loObject.outLines[i]);
      }
    }
    for (var i = 0; i < loObject.inLines.length; i++) {
      if (loObject.inLines[i] == null) {
        continue;
      }
      if (loObject.inLines[i].Type == 'BEELINE') {
        oCreateBeeline(loObject.inLines[i].fromTask, loObject, null, loObject.inLines[i]);
      } else {
        oCreateCurve(loObject.inLines[i].fromTask, loObject, null, loObject.inLines[i]);
      }
    }
  }
}

function my_OverFunc(psName) {
  // 活动对象或者文字标签的MouseOver事件
  if (goWorkFlow.equalStatus) {
    return;
  }
  goWorkFlow.curOverObjName = psName;
}

function my_OutFunc(psName) {
  // 活动对象或者文字标签的MouseOut事件
  goWorkFlow.curOverObjName = null;
  var loObject = oGetTaskObject(psName);
  if (loObject != null) {
    loObject.imgObject.setCursor('themes/images/workflow/cursor_move.cur');
  }
}

function getCursorPosition(event) {
  // 获取鼠标x,y
  goWorkFlow.cursorX = event.pageX; // + $(document.body).scrollLeft();
  goWorkFlow.cursorY = event.pageY; // + $(document.body).scrollTop();
}

function getSelectObject() {
  // 获取当前需要创建对象或者表示选择；
  if (parent.goSelectedTool != null) {
    goWorkFlow.curAddObject = parent.goSelectedTool.Type;
  } else {
    goWorkFlow.curAddObject = null;
  }
  switch (goWorkFlow.curAddObject) {
    case 'DEFAULT':
      $(ID_WORKAROUND).css('cursor', 'auto');
      break;
    case 'BEELINE':
      $(ID_WORKAROUND).css('cursor', 'url(/static/themes/images/workflow/cursor_zx.cur) 32 32,auto');
      break;
    case 'CURVE':
      $(ID_WORKAROUND).css('cursor', 'url(/static/themes/images/workflow/cursor_qx.cur) 32 32,auto');
      break;
    case 'BEGIN':
      $(ID_WORKAROUND).css('cursor', 'url(/static/themes/images/workflow/cursor_begin.cur) 32 32,auto');
      break;
    case 'TASK':
      $(ID_WORKAROUND).css('cursor', 'url(/static/themes/images/workflow/cursor_task.cur) 32 32,auto');
      break;
    case 'CONDITION':
      $(ID_WORKAROUND).css('cursor', 'url(/static/themes/images/workflow/cursor_condition.cur) 32 32,auto');
      break;
    case 'END':
      $(ID_WORKAROUND).css('cursor', 'url(/static/themes/images/workflow/cursor_end.cur) 32 32,auto');
      break;
    case 'SUBFLOW':
      $(ID_WORKAROUND).css('cursor', 'url(/static/themes/images/workflow/cursor_subflow.cur) 32 32,auto');
      break;
    case 'H_SWIMLANE':
      $(ID_WORKAROUND).css('cursor', 'url(/static/themes/images/workflow/cursor_hxyd.cur) 32 32,auto');
      break;
    case 'V_SWIMLANE':
      $(ID_WORKAROUND).css('cursor', 'url(/static/themes/images/workflow/cursor_zxyd.cur) 32 32,auto');
      break;
    case 'LABEL':
      $(ID_WORKAROUND).css('cursor', 'url(/static/themes/images/workflow/cursor_label.cur) 32 32,auto');
      break;
    default:
      $(ID_WORKAROUND).css('cursor', 'auto');
  }
}

function setSelectObject(psType) {
  // 设置选择创建对象的表示值；
  goWorkFlow.curAddObject = psType;
  if (parent.goSelectedTool != null && psType != null) {
    for (var i = 0; i < parent.gaTools.length; i++) {
      if (parent.gaTools[i].innerHTML.indexOf('_' + psType.toLowerCase() + '.') != -1) {
        parent.goSelectedTool = parent.gaTools[i];
        parent.goSelectedTool.Type = psType;
        parent.gaTools[i].className = 'ToolInset';
      } else {
        parent.gaTools[i].className = 'ToolBar';
      }
    }
    parent.showToolMsg();
  }
}

function showCursorPosition(event) {
  // 显示当前鼠标位置
  getCursorPosition(event);
  var innerText = goWorkFlow.cursorX + 'px,' + goWorkFlow.cursorY + 'px';
  $('#ID_Position', parent.document).text(innerText);
}

function showMsg(psMsg, pbError) {
  if (onlyGraphics) {
    return;
  }
  // 显示消息；
  if (pbError) {
    top.appModal.error(psMsg);
  } else {
    console.log(psMsg);
  }
  top.appModal.hideMask();
}

function parentPromptDoing_(psMsg, pbError) {
  // 父窗口显示消息；
  showMsg(psMsg);
  var lsHTML =
    '<div id="hCover" style="position:absolute; top:0; left:0; z-index:9; visibility:visible;"><TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0 style="width:100%;height:1000;background-color: transparent; "><TR><TD style="background-color: transparent; "></td></tr></table></div>';
  $('#ID_PromptDoing', parent.window.document).html(lsHTML);
  $('#ID_PromptDoing', parent.window.document).show();
}

function getAdjustXY(piValue) {
  // 调整点坐标值为方格位置
  var liCeil = Math.ceil(piValue / 48);
  if (liCeil * 48 == piValue) {
    var liReturn = piValue;
  } else {
    if (piValue <= (liCeil - 1) * 48 + 24) {
      var liReturn = (liCeil - 1) * 48;
    } else {
      var liReturn = liCeil * 48;
    }
  }
  return liReturn;
}

function bAdjustObject(poObject) {
  // 调整对象坐标为方格线居中
  // var lix = getAdjustXY(poObject.imgObject.x);
  // var liy = getAdjustXY(poObject.imgObject.y);
  var lix = poObject.imgObject.x;
  var liy = poObject.imgObject.y;
  var liX_mo = lix % 24;
  if (liX_mo > 12) {
    lix += 24 - liX_mo;
  } else {
    lix -= liX_mo;
  }
  var liY_mo = liy % 24;
  if (liY_mo > 12) {
    liy += 24 - liY_mo;
  } else {
    liy -= liY_mo;
  }
  poObject.imgObject.moveTo(lix, liy);
}

function oGetTaskObject(psName) {
  // 根据名称从活动对象或者标签的对象数组里面获取对象
  for (var i = 0; i < goWorkFlow.tasks.length; i++) {
    if (goWorkFlow.tasks[i] != null && goWorkFlow.tasks[i].name == psName) {
      return goWorkFlow.tasks[i];
    }
  }
  return null;
}

function oGetTaskObjectByPropertyName(psName) {
  // 根据环节名称从对象数组里面获取对象
  for (var i = 0; i < goWorkFlow.tasks.length; i++) {
    if (
      goWorkFlow.tasks[i] != null &&
      goWorkFlow.tasks[i].Type != 'LABEL' &&
      goWorkFlow.tasks[i].xmlObject != null &&
      goWorkFlow.tasks[i].xmlObject.getAttribute('name') == psName
    ) {
      return goWorkFlow.tasks[i];
    }
  }
  return null;
}

function oGetTaskObjectByPropertyID(psID) {
  for (var i = 0; i < goWorkFlow.tasks.length; i++) {
    if (
      goWorkFlow.tasks[i] != null &&
      goWorkFlow.tasks[i].Type != 'LABEL' &&
      goWorkFlow.tasks[i].xmlObject != null &&
      goWorkFlow.tasks[i].xmlObject.getAttribute('id') == psID
    ) {
      return goWorkFlow.tasks[i];
    }
  }
  return null;
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

function oGetConditionObjectByFromTaskName(psName) {
  var loTask = oGetTaskObjectByPropertyName(psName);
  if (loTask == null) {
    return null;
  }
  for (var j = 0; j < loTask.outLines.length; j++) {
    if (loTask.outLines[j] != null && loTask.outLines[j].toTask != null && loTask.outLines[j].toTask.Type == 'CONDITION') {
      return loTask.outLines[j].toTask;
    }
  }
  return null;
}

function oGetLineObject(psName) {
  for (var i = 0; i < goWorkFlow.lines.length; i++) {
    if (goWorkFlow.lines[i] != null && goWorkFlow.lines[i].name == psName) {
      return goWorkFlow.lines[i];
    }
  }
  return null;
}

function oGetLineObjectByLabel(psName) {
  for (var i = 0; i < goWorkFlow.lines.length; i++) {
    if (goWorkFlow.lines[i] != null && goWorkFlow.lines[i].labelObject.name == psName) {
      return goWorkFlow.lines[i];
    }
  }
  return null;
}

function oGetLineObjectByPoint(poFrom, poTo) {
  if (poFrom == null || poTo == null) {
    return null;
  }
  for (var i = 0; i < goWorkFlow.lines.length; i++) {
    if (goWorkFlow.lines[i] == null) {
      continue;
    }
    if (goWorkFlow.lines[i].fromTask.name != poFrom.name || goWorkFlow.lines[i].toTask.name != poTo.name) {
      continue;
    }
    return goWorkFlow.lines[i];
  }
  return null;
}

function oGetConditionObjectByFromTask(poTask) {
  var loTask = poTask;
  if (loTask == null) {
    return null;
  }
  for (var j = 0; j < loTask.outLines.length; j++) {
    if (loTask.outLines[j] != null && loTask.outLines[j].toTask != null && loTask.outLines[j].toTask.Type == 'CONDITION') {
      return loTask.outLines[j].toTask;
    }
  }
  return null;
}

function oGetEndTaskFromTask(poTask) {
  var loTask = poTask;
  if (loTask == null) {
    return null;
  }
  for (var j = 0; j < loTask.outLines.length; j++) {
    if (loTask.outLines[j] != null && loTask.outLines[j].toTask != null && loTask.outLines[j].toTask.Type == 'END') {
      return loTask.outLines[j].toTask;
    }
  }
  return null;
}

function sGetRandom(piLength) {
  var lsOne,
    lsResult = '';
  for (var i = 0; i < piLength; i++) {
    lsOne = Math.random() * 10 + '';
    lsResult += lsOne.substring(0, 1);
  }
  return lsResult;
}

function sGetNewName(poObject) {
  var lsName = sGetLang('FLOW_WF_' + poObject.Type + '_NAME');
  var lsNewName = lsName;
  if (poObject.Type === 'TASK' || poObject.Type === 'SUBFLOW' || poObject.Type === 'CONDITION') {
    var liIndex = 1;
    do {
      var lsNewName = lsName + liIndex;
      var _len = goWorkFlow.tasks.length;

      for (var i = 0; i < _len; i++) {
        if (
          goWorkFlow.tasks[i] != null &&
          goWorkFlow.tasks[i].xmlObject != null &&
          goWorkFlow.tasks[i].xmlObject.getAttribute('name') === lsNewName
        ) {
          break;
        }
      }

      if (i >= _len - 1) {
        break;
      }
      liIndex++;
    } while (true);
  }
  return lsNewName;
}

function sGetNewFlowID() {
  var lsID, lsURL;
  do {
    lsID = 'F' + sGetRandom(3);
    return lsID;
  } while (true);
}

function sGetNewTaskID(poObject) {
  var lsID;
  var lsHead = poObject.Type == 'TASK' ? 'T' : 'S';
  do {
    lsID = lsHead + sGetRandom(3);
    for (var i = 0; i < goWorkFlow.tasks.length; i++) {
      if (
        goWorkFlow.tasks[i] != null &&
        goWorkFlow.tasks[i].xmlObject != null &&
        goWorkFlow.tasks[i].xmlObject.getAttribute('id') == lsID
      ) {
        break;
      }
    }
    if (i >= goWorkFlow.tasks.length) {
      return lsID;
    }
  } while (true);
}

function sGetNewDirectionID() {
  var lsID;
  do {
    lsID = 'D' + sGetRandom(3);
    for (var i = 0; i < goWorkFlow.lines.length; i++) {
      if (
        goWorkFlow.lines[i] != null &&
        goWorkFlow.lines[i].xmlObject != null &&
        goWorkFlow.lines[i].xmlObject.getAttribute('id') == lsID
      ) {
        break;
      }
    }
    if (i >= goWorkFlow.lines.length) {
      return lsID;
    }
  } while (true);
}

function aGetArrow(x1, y1, x2, y2) {
  var x3, y3, x4, y4;
  var const_ArrowLength = 10;
  var liAngle = Math.atan(Math.abs(y2 - y1) / Math.abs(x2 - x1));
  var liX1 = Math.round(const_ArrowLength * Math.cos(Math.abs(Math.PI / 9 - liAngle)));
  var liY1 = Math.round(const_ArrowLength * Math.sin(Math.abs(Math.PI / 9 - liAngle)));
  var liX2 = Math.round(const_ArrowLength * Math.sin(Math.abs((Math.PI * 7) / 18 - liAngle)));
  var liY2 = Math.round(const_ArrowLength * Math.cos(Math.abs((Math.PI * 7) / 18 - liAngle)));
  if (x2 >= x1 && y2 >= y1) {
    x3 = x2 - liX1;
    y3 = liAngle <= Math.PI / 9 ? y2 + liY1 : y2 - liY1;
    x4 = liAngle <= (Math.PI * 7) / 18 ? x2 - liX2 : x2 + liX2;
    y4 = y2 - liY2;
  } else {
    if (x2 < x1 && y2 >= y1) {
      x3 = x2 + liX1;
      y3 = liAngle <= Math.PI / 9 ? y2 + liY1 : y2 - liY1;
      x4 = liAngle <= (Math.PI * 7) / 18 ? x2 + liX2 : x2 - liX2;
      y4 = y2 - liY2;
    } else {
      if (x2 >= x1 && y2 < y1) {
        x3 = x2 - liX1;
        y3 = liAngle <= Math.PI / 9 ? y2 - liY1 : y2 + liY1;
        x4 = liAngle <= (Math.PI * 7) / 18 ? x2 - liX2 : x2 + liX2;
        y4 = y2 + liY2;
      } else {
        if (x2 < x1 && y2 < y1) {
          x3 = x2 + liX1;
          y3 = liAngle <= Math.PI / 9 ? y2 - liY1 : y2 + liY1;
          x4 = liAngle <= (Math.PI * 7) / 18 ? x2 + liX2 : x2 - liX2;
          y4 = y2 + liY2;
        }
      }
    }
  }
  var laReturn = new Array();
  laReturn[0] = x3;
  laReturn[1] = y3;
  laReturn[2] = x4;
  laReturn[3] = y4;
  return laReturn;
}

function aGetLinePoint(x1, y1, x2, y2, piLength) {
  var liAngle = Math.atan(Math.abs(y2 - y1) / Math.abs(x2 - x1));
  var liX = Math.round(piLength * Math.cos(liAngle));
  var liY = Math.round(piLength * Math.sin(liAngle));
  var x3 = x2 == x1 ? x2 : x2 > x1 ? x2 - liX : x2 + liX;
  var y3 = y2 == y1 ? y2 : y2 > y1 ? y2 - liY : y2 + liY;
  var laReturn = new Array();
  laReturn[0] = x3;
  laReturn[1] = y3;
  return laReturn;
}

function aTaskBeelinePoint(poObject, x2, y2) {
  var x1 = poObject.imgObject.x + poObject.imgObject.w / 2;
  var y1 = poObject.imgObject.y + poObject.imgObject.h / 2;
  var liAngle1 = Math.atan(Math.abs(y2 - y1) / Math.abs(x2 - x1));
  var liAngle2 = Math.atan(poObject.imgObject.h / poObject.imgObject.w);
  var liX = liAngle1 < liAngle2 ? poObject.imgObject.w / 2 : (Math.abs(x2 - x1) * poObject.imgObject.h) / 2 / Math.abs(y2 - y1);
  var liY = liAngle1 > liAngle2 ? poObject.imgObject.h / 2 : (Math.abs(y2 - y1) * poObject.imgObject.w) / 2 / Math.abs(x2 - x1);
  var x3 = x2 == x1 ? x1 : x2 > x1 ? x1 + liX : x1 - liX;
  var y3 = y2 == y1 ? y1 : y2 > y1 ? y1 + liY : y1 - liY;
  var laReturn = new Array();
  laReturn[0] = x3;
  laReturn[1] = y3;
  return laReturn;
}

function aTaskCurvePoint(poObject, psWay, poLine, pbNew, x2, y2) {
  var x1 = poObject.imgObject.x;
  var y1 = poObject.imgObject.y;
  var x3, y3, laLine, liInterval, liWayIndex;
  if (poLine == null) {
    switch (psWay) {
      case 'up':
        x3 = x1 + poObject.imgObject.w / 2;
        y3 = y1;
        break;
      case 'down':
        x3 = x1 + poObject.imgObject.w / 2;
        y3 = y1 + poObject.imgObject.h;
        break;
      case 'left':
        x3 = x1;
        y3 = y1 + poObject.imgObject.h / 2;
        break;
      case 'right':
        x3 = x1 + poObject.imgObject.w;
        y3 = y1 + poObject.imgObject.h / 2;
        break;
    }
  } else {
    if (psWay == 'up' || psWay == 'down') {
      if (psWay == 'up') {
        laLine = poObject.upCurves;
        y3 = y1;
      } else {
        laLine = poObject.downCurves;
        y3 = y1 + poObject.imgObject.h;
      }
      liInterval = poObject.imgObject.w / (laLine.length + 1);
      if (Math.ceil(liInterval) > liInterval) {
        liInterval--;
      }
      for (var i = 0; i < laLine.length; i++) {
        if (laLine[i].name == poLine.name) {
          x3 = x1 + liInterval * (i + 1);
          break;
        }
      }
      liWayIndex = i;
      if (pbNew == true) {
        for (var i = 0; i < laLine.length; i++) {
          var loObject = laLine[i];
          if (loObject == null || loObject.name == poLine.name) {
            continue;
          }
          if (poObject.name == loObject.fromTask.name) {
            loObject.x1 = x1 + liInterval * (i + 1);
            loObject.y1 = y3;
          } else {
            loObject.x2 = x1 + liInterval * (i + 1);
            loObject.y2 = y3;
          }
          bReDrawLine(loObject);
        }
      }
    } else {
      if (psWay == 'left' || psWay == 'right') {
        if (psWay == 'left') {
          laLine = poObject.leftCurves;
          x3 = x1;
        } else {
          laLine = poObject.rightCurves;
          x3 = x1 + poObject.imgObject.w;
        }
        liInterval = poObject.imgObject.h / (laLine.length + 1);
        if (Math.ceil(liInterval) > liInterval) {
          liInterval--;
        }
        for (var i = 0; i < laLine.length; i++) {
          if (laLine[i].name == poLine.name) {
            y3 = y1 + liInterval * (i + 1);
            break;
          }
        }
        liWayIndex = i;
        if (pbNew == true) {
          for (var i = 0; i < laLine.length; i++) {
            var loObject = laLine[i];
            if (loObject == null || loObject.name == poLine.name) {
              continue;
            }
            if (poObject.name == loObject.fromTask.name) {
              loObject.x1 = x3;
              loObject.y1 = y1 + liInterval * (i + 1);
            } else {
              loObject.x2 = x3;
              loObject.y2 = y1 + liInterval * (i + 1);
            }
            bReDrawLine(loObject);
          }
        }
      }
    }
  }
  var laReturn = new Array();
  laReturn[0] = x3;
  laReturn[1] = y3;
  laReturn[2] = liWayIndex;
  return laReturn;
}

function aGetCurvePoint(x1, y1, x2, y2, psFromWay, psToWay, piMaxIndex) {
  var MINIZE_LENGTH = 20;
  if (piMaxIndex != null && piMaxIndex > 0) {
    MINIZE_LENGTH += piMaxIndex * 10;
  }
  var laX = new Array();
  var laY = new Array();
  laX[0] = x1;
  laY[0] = y1;
  if (psToWay == null) {
    laX[3] = x2;
    laY[3] = y2;
    switch (psFromWay) {
      case 'up':
        laX[1] = laX[0];
        laY[1] = (laY[0] > laY[3] ? laY[3] : laY[0]) - MINIZE_LENGTH;
        laX[2] = laX[3];
        laY[2] = laY[1];
        break;
      case 'down':
        laX[1] = laX[0];
        laY[1] = (laY[0] < laY[3] ? laY[3] : laY[0]) + MINIZE_LENGTH;
        laX[2] = laX[3];
        laY[2] = laY[1];
        break;
      case 'left':
        laX[1] = (laX[0] > laX[3] ? laX[3] : laX[0]) - MINIZE_LENGTH;
        laY[1] = laY[0];
        laX[2] = laX[1];
        laY[2] = laY[3];
        break;
      case 'right':
        laX[1] = (laX[0] < laX[3] ? laX[3] : laX[0]) + MINIZE_LENGTH;
        laY[1] = laY[0];
        laX[2] = laX[1];
        laY[2] = laY[3];
        break;
    }
  } else {
    switch (psFromWay) {
      case 'up':
        switch (psToWay) {
          case 'up':
            laX[1] = laX[0];
            laY[1] = (laY[0] > y2 ? y2 : laY[0]) - MINIZE_LENGTH;
            laX[2] = x2;
            laY[2] = laY[1];
            break;
          case 'down':
            laX[1] = laX[0];
            laY[1] = laY[0] - MINIZE_LENGTH > (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : laY[0] - MINIZE_LENGTH;
            laX[2] = (laX[0] + x2) / 2;
            laY[2] = laY[1];
            laX[3] = laX[2];
            laY[3] = y2 + MINIZE_LENGTH < (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : y2 + MINIZE_LENGTH;
            laX[4] = x2;
            laY[4] = laY[3];
            break;
          case 'left':
            laX[1] = laX[0];
            laY[1] = laY[0] - MINIZE_LENGTH > (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : laY[0] - MINIZE_LENGTH;
            laX[2] = x2 - MINIZE_LENGTH > (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : x2 - MINIZE_LENGTH;
            laY[1] = laX[2] > laX[1] && laY[0] - MINIZE_LENGTH > y2 ? y2 : laY[1];
            laY[2] = laY[1];
            laX[3] = laX[2];
            laY[3] = y2;
            break;
          case 'right':
            laX[1] = laX[0];
            laY[1] = laY[0] - MINIZE_LENGTH > (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : laY[0] - MINIZE_LENGTH;
            laX[2] = x2 + MINIZE_LENGTH < (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : x2 + MINIZE_LENGTH;
            laY[1] = laX[2] < laX[1] && laY[0] - MINIZE_LENGTH > y2 ? y2 : laY[1];
            laY[2] = laY[1];
            laX[3] = laX[2];
            laY[3] = y2;
            break;
        }
        break;
      case 'down':
        switch (psToWay) {
          case 'up':
            laX[1] = laX[0];
            laY[1] = laY[0] + MINIZE_LENGTH < (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : laY[0] + MINIZE_LENGTH;
            laX[2] = (laX[0] + x2) / 2;
            laY[2] = laY[1];
            laX[3] = laX[2];
            laY[3] = y2 - MINIZE_LENGTH > (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : y2 - MINIZE_LENGTH;
            laX[4] = x2;
            laY[4] = laY[3];
            break;
          case 'down':
            laX[1] = laX[0];
            laY[1] = (laY[0] < y2 ? y2 : laY[0]) + MINIZE_LENGTH;
            laX[2] = x2;
            laY[2] = laY[1];
            break;
          case 'left':
            laX[1] = laX[0];
            laY[1] = laY[0] + MINIZE_LENGTH < (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : laY[0] + MINIZE_LENGTH;
            laX[2] = x2 - MINIZE_LENGTH > (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : x2 - MINIZE_LENGTH;
            laY[1] = laX[2] > laX[1] && laY[0] + MINIZE_LENGTH < y2 ? y2 : laY[1];
            laY[2] = laY[1];
            laX[3] = laX[2];
            laY[3] = y2;
            break;
          case 'right':
            laX[1] = laX[0];
            laY[1] = laY[0] + MINIZE_LENGTH < (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : laY[0] + MINIZE_LENGTH;
            laX[2] = x2 + MINIZE_LENGTH < (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : x2 + MINIZE_LENGTH;
            laY[1] = laX[2] < laX[1] && laY[0] + MINIZE_LENGTH < y2 ? y2 : laY[1];
            laY[2] = laY[1];
            laX[3] = laX[2];
            laY[3] = y2;
            break;
        }
        break;
      case 'left':
        switch (psToWay) {
          case 'up':
            laX[1] = laX[0] - MINIZE_LENGTH > (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : laX[0] - MINIZE_LENGTH;
            laY[1] = laY[0];
            laY[2] = y2 - MINIZE_LENGTH > (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : y2 - MINIZE_LENGTH;
            laX[1] = laY[2] > laY[1] && laX[0] - MINIZE_LENGTH > x2 ? x2 : laX[1];
            laX[2] = laX[1];
            laX[3] = x2;
            laY[3] = laY[2];
            break;
          case 'down':
            laX[1] = laX[0] - MINIZE_LENGTH > (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : laX[0] - MINIZE_LENGTH;
            laY[1] = laY[0];
            laY[2] = y2 + MINIZE_LENGTH < (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : y2 + MINIZE_LENGTH;
            laX[1] = laY[2] < laY[1] && laX[0] - MINIZE_LENGTH > x2 ? x2 : laX[1];
            laX[2] = laX[1];
            laX[3] = x2;
            laY[3] = laY[2];
            break;
          case 'left':
            laX[1] = (laX[0] > x2 ? x2 : laX[0]) - MINIZE_LENGTH;
            laY[1] = laY[0];
            laX[2] = laX[1];
            laY[2] = y2;
            break;
          case 'right':
            laX[1] = laX[0] - MINIZE_LENGTH > (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : laX[0] - MINIZE_LENGTH;
            laY[1] = laY[0];
            laX[2] = laX[1];
            laY[2] = (laY[0] + y2) / 2;
            laX[3] = x2 + MINIZE_LENGTH < (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : x2 + MINIZE_LENGTH;
            laY[3] = laY[2];
            laX[4] = laX[3];
            laY[4] = y2;
            break;
        }
        break;
      case 'right':
        switch (psToWay) {
          case 'up':
            laX[1] = laX[0] + MINIZE_LENGTH < (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : laX[0] + MINIZE_LENGTH;
            laY[1] = laY[0];
            laY[2] = y2 - MINIZE_LENGTH > (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : y2 - MINIZE_LENGTH;
            laX[1] = laY[2] > laY[1] && laX[0] + MINIZE_LENGTH < x2 ? x2 : laX[1];
            laX[2] = laX[1];
            laX[3] = x2;
            laY[3] = laY[2];
            break;
          case 'down':
            laX[1] = laX[0] + MINIZE_LENGTH < (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : laX[0] + MINIZE_LENGTH;
            laY[1] = laY[0];
            laY[2] = y2 + MINIZE_LENGTH < (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : y2 + MINIZE_LENGTH;
            laX[1] = laY[2] < laY[1] && laX[0] + MINIZE_LENGTH < x2 ? x2 : laX[1];
            laX[2] = laX[1];
            laX[3] = x2;
            laY[3] = laY[2];
            break;
          case 'left':
            laX[1] = laX[0] + MINIZE_LENGTH < (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : laX[0] + MINIZE_LENGTH;
            laY[1] = laY[0];
            laX[2] = laX[1];
            laY[2] = (laY[0] + y2) / 2;
            laX[3] = x2 - MINIZE_LENGTH > (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : x2 - MINIZE_LENGTH;
            laY[3] = laY[2];
            laX[4] = laX[3];
            laY[4] = y2;
            break;
          case 'right':
            laX[1] = (laX[0] < x2 ? x2 : laX[0]) + MINIZE_LENGTH;
            laY[1] = laY[0];
            laX[2] = laX[1];
            laY[2] = y2;
            break;
        }
        break;
    }
    laX[laX.length] = x2;
    laY[laY.length] = y2;
  }
  var laReturn = new Array();
  laReturn[0] = laX;
  laReturn[1] = laY;
  return laReturn;
}

function sGetTaskCurveWay(poObject, x2, y2) {
  if (poObject == null) {
    return null;
  }
  var loImg = poObject.imgObject;
  var x1 = loImg.x + loImg.w / 2;
  var y1 = loImg.y + loImg.h / 2;
  var liAngle1 = Math.atan(Math.abs(y2 - y1) / Math.abs(x2 - x1));
  var liAngle2 = Math.atan(loImg.h / loImg.w);
  if (liAngle1 <= liAngle2) {
    if (x2 >= x1) {
      var lsWay = 'right';
    } else {
      var lsWay = 'left';
    }
  } else {
    if (y2 >= y1) {
      var lsWay = 'down';
    } else {
      var lsWay = 'up';
    }
  }
  return lsWay;
}
//画直线
function bDrawBeeline(id, x1, y1, x2, y2, piStroke) {
  var laXY = aGetLinePoint(x1, y1, x2, y2, 5);
  var x3 = laXY[0];
  var y3 = laXY[1];
  laXY = aGetArrow(x1, y1, x3, y3);
  var liStroke = 1;
  if (piStroke != null) {
    liStroke = piStroke;
  }
  goWorkFlow.eWFJG.setStroke(liStroke);
  goWorkFlow.eWFJG.htm += '<span id="' + id + '" type="BEELINE" onclick="bSetObjectStatus(this.id,true);">';
  goWorkFlow.eWFJG.drawLine(x1, y1, x2, y2);
  goWorkFlow.eWFJG.drawLine(laXY[0], laXY[1], x3, y3);
  goWorkFlow.eWFJG.drawLine(laXY[2], laXY[3], x3, y3);
  goWorkFlow.eWFJG.fillPolygon(new Array(x3, laXY[0], laXY[2]), new Array(y3, laXY[1], laXY[3]));
  goWorkFlow.eWFJG.htm += '</span>';
  goWorkFlow.eWFJG.paint();
  var lineObj = oGetLineObject(id);
  if (lineObj && $(lineObj.htmlObject).hasClass('done')) {
    setTimeout(function () {
      setLineDoneColor(lineObj);
    }, 0);
  }
  return true;
}
//画曲线
function bDrawCurve(id, paX, paY, piStroke) {
  var laXY = aGetLinePoint(paX[paX.length - 2], paY[paY.length - 2], paX[paX.length - 1], paY[paY.length - 1], 5);
  var x3 = laXY[0];
  var y3 = laXY[1];
  laXY = aGetArrow(paX[paX.length - 2], paY[paY.length - 2], x3, y3);
  var liStroke = 1;
  if (piStroke != null) {
    liStroke = piStroke;
  }
  goWorkFlow.eWFJG.setStroke(liStroke);
  goWorkFlow.eWFJG.htm += '<span id="' + id + '" type="CURVE" onclick="bSetObjectStatus(this.id,true);">';
  goWorkFlow.eWFJG.drawPolyline(paX, paY);
  goWorkFlow.eWFJG.drawLine(laXY[0], laXY[1], x3, y3);
  goWorkFlow.eWFJG.drawLine(laXY[2], laXY[3], x3, y3);
  goWorkFlow.eWFJG.fillPolygon(new Array(x3, laXY[0], laXY[2]), new Array(y3, laXY[1], laXY[3]));
  goWorkFlow.eWFJG.htm += '</span>';
  goWorkFlow.eWFJG.paint();
  var lineObj = oGetLineObject(id);
  if (lineObj && $(lineObj.htmlObject).hasClass('done')) {
    setTimeout(function () {
      setLineDoneColor(lineObj);
    }, 0);
  }
  return true;
}

function bDrawBrokenBeeline() {
  if (typeof ID_BROKENBEELINE == 'object') {
    $('#ID_BROKENBEELINE').remove();
  }
  var laXY = aTaskBeelinePoint(goWorkFlow.fromObject, goWorkFlow.cursorX, goWorkFlow.cursorY);
  bDrawBeeline('ID_BROKENBEELINE', laXY[0], laXY[1], goWorkFlow.cursorX, goWorkFlow.cursorY);
  return true;
}

function bDrawBrokenCurve() {
  var loTo =
    goWorkFlow.curOverObjName != null && goWorkFlow.curOverObjName !== goWorkFlow.fromObject.name
      ? oGetTaskObject(goWorkFlow.curOverObjName)
      : null;
  var lsFromWay = goWorkFlow.fromObject.cursorWay;
  var lsToWay = loTo != null ? loTo.cursorWay : null;
  var laXY = aTaskCurvePoint(goWorkFlow.fromObject, lsFromWay, null);
  var laX_Y = aGetCurvePoint(laXY[0], laXY[1], goWorkFlow.cursorX, goWorkFlow.cursorY, lsFromWay, lsToWay);
  var laX = laX_Y[0];
  var laY = laX_Y[1];
  if (typeof ID_BROKENCURVE == 'object') {
    $('#ID_BROKENCURVE').remove();
  }
  bDrawCurve('ID_BROKENCURVE', laX, laY);
  return true;
}

function bSetObjectStatus(psName, pbSelected, poObject, event) {
  var loObject = null;
  if (psName == null && poObject == null) {
    return;
  }
  if (poObject != null) {
    loObject = poObject;
  } else {
    goWorkFlow.curObjectName = psName;
    loObject = oGetLineObject(psName);
  }
  if (loObject != null && (loObject.Type === 'BEELINE' || loObject.Type === 'CURVE')) {
    var loSpan = loObject.htmlObject;
    var label = loObject.labelObject.htmlObject;
    if (pbSelected) {
      $(label).addClass('flow-control-shadow');
    } else {
      $(label).removeClass('flow-control-shadow');
    }
    for (var i = 0; i < loSpan.children.length; i++) {
      if (!top.GetRequestParam().onlyView) {
        if (pbSelected) {
          loSpan.children[i].style.backgroundColor = '#076CFF';
        } else {
          loSpan.children[i].style.backgroundColor = '#808799';
        }
      }
    }
  } else {
    if (loObject == null) {
      loObject = oGetTaskObject(psName);
    }
    if (loObject == null) {
      return;
    }
    if (loObject.Type == 'LABEL') {
      var lodiv = loObject.htmlObject;
      if (pbSelected == false) {
        $(lodiv).removeClass('flow-control-shadow');
      } else {
        selectedLoObject(loObject);
      }
    } else {
      var loImg = loObject.imgObject;
      if (loImg.is_image) {
        var lsName = unescape(loImg.src);
        if (lsName.indexOf('~.gif') != -1) {
          lsName = lsName.substr(0, lsName.indexOf('~.gif'));
        } else {
          if (lsName.indexOf('^.gif') != -1) {
            lsName = lsName.substr(0, lsName.indexOf('^.gif'));
          } else {
            if (lsName.indexOf('_.gif') != -1) {
              lsName = lsName.substr(0, lsName.indexOf('_.gif'));
            } else {
              lsName = lsName.substr(0, lsName.indexOf('.gif'));
            }
          }
        }
        if (pbSelected == true) {
          // 选择对象
          $(loObject.htmlObject).removeClass('flow-control-error');
          selectedLoObject(loObject);
          // loImg.swapImage(lsName + "_.gif");
        } else {
          if (pbSelected == -1) {
            // loImg.swapImage(lsName + "~.gif");
            $(loObject.htmlObject).removeClass('flow-control-shadow').addClass('flow-control-error');
          } else {
            if (pbSelected == -2) {
              loImg.swapImage(lsName + '^.gif');
            } else {
              // 取消选择对象
              var loImgdiv = loObject.htmlObject;
              // 查看流程办理状态，图片切换
              if ($(loImgdiv).hasClass('flow-handing-info')) {
                swapFlowHandingInfoImageByName(loObject, $(loImgdiv).attr('imgName'));
              } else {
                // loImg.swapImage(lsName + ".gif");
              }
              unselectedLoObject(loObject);
            }
          }
        }
      }
    }
  }
  if (pbSelected == true) {
    goWorkFlow.selected = true;
    if (event != undefined && event != null) {
      event.cancelBubble = true;
      // event.stopPropagation();
    }
  }
}
// 选择对象
function selectedLoObject(loObject) {
  if (goWorkFlow.equalStatus) {
    return;
  }
  goWorkFlow.curObjectName = loObject.name;
  var lodiv = loObject.htmlObject;
  $(lodiv).addClass('flow-control-shadow');
}
// 取消选择对象
function unselectedLoObject(loObject) {
  goWorkFlow.curObjectName = null;
  var lodiv = loObject.htmlObject;
  $(lodiv).removeClass('flow-control-shadow');
}

function bShowTasks(poObject, pbShow, pbBefore, psName) {
  if (pbShow == true) {
    if (pbBefore == true) {
      for (var i = 0; i < poObject.inLines.length; i++) {
        if (poObject.inLines[i].fromTask.name == psName) {
          continue;
        }
        for (var j = 0; j < goWorkFlow.selectTasks.length; j++) {
          if (goWorkFlow.selectTasks[j].name == poObject.inLines[i].fromTask.name) {
            break;
          }
        }
        if (j < goWorkFlow.selectTasks.length) {
          continue;
        }
        goWorkFlow.selectTasks[goWorkFlow.selectTasks.length] = poObject.inLines[i].fromTask;
        bSetObjectStatus(null, -1, poObject.inLines[i].fromTask);
        bShowTasks(poObject.inLines[i].fromTask, pbShow, pbBefore, psName);
      }
    } else {
      for (var i = 0; i < poObject.outLines.length; i++) {
        if (poObject.outLines[i].toTask.name == psName) {
          continue;
        }
        for (var j = 0; j < goWorkFlow.selectTasks.length; j++) {
          if (goWorkFlow.selectTasks[j].name == poObject.outLines[i].toTask.name) {
            break;
          }
        }
        if (j < goWorkFlow.selectTasks.length) {
          continue;
        }
        goWorkFlow.selectTasks[goWorkFlow.selectTasks.length] = poObject.outLines[i].toTask;
        bSetObjectStatus(null, -1, poObject.outLines[i].toTask);
        bShowTasks(poObject.outLines[i].toTask, pbShow, pbBefore, psName);
      }
    }
  } else {
    if (goWorkFlow.selectTasks != null && goWorkFlow.selectTasks.length > 0) {
      for (var i = 0; i < goWorkFlow.selectTasks.length; i++) {
        bSetObjectStatus(null, false, goWorkFlow.selectTasks[i]);
      }
      goWorkFlow.selectTasks = new Array();
    }
  }
  return true;
}

function oCreateTask(psType, piX, piY) {
  showOrHideEMpty('hide');
  dd.db = ID_WORKAROUND;
  var lsName = 'img_' + psType.toLowerCase();
  dd.elements[lsName].copy();
  var liCopyLength = dd.elements[lsName].copies.length;
  var loObject = new Object();
  loObject.imgObject = dd.elements[lsName].copies[liCopyLength - 1];
  loObject.name = loObject.imgObject.name;
  loObject.Type = psType;
  loObject.imgObject.show();
  var liX = piX > loObject.imgObject.w / 2 ? piX - loObject.imgObject.w / 2 : 0;
  var liY = piY > loObject.imgObject.h / 2 ? piY - loObject.imgObject.h / 2 : 0;
  liX = liX + loObject.imgObject.w > ID_WORKAROUND.offsetWidth ? ID_WORKAROUND.offsetWidth - loObject.imgObject.w : liX;
  liY = liY + loObject.imgObject.h > ID_WORKAROUND.offsetHeight ? ID_WORKAROUND.offsetHeight - loObject.imgObject.h : liY;
  loObject.imgObject.moveTo(liX, liY);
  loObject.imgObject.setCursor('themes/images/workflow/cur_move.cur');
  loObject.htmlObject = dd.getDiv(loObject.name + 'div');
  $(loObject.htmlObject)
    .children('img')
    .addClass('img-' + psType.toLowerCase());
  if (psType == 'LABEL') {
    loObject.htmlObject.style.border = '#000000 1px solid';
  } else {
    if (psType != 'LINE') {
      loObject.outLines = new Array();
      loObject.inLines = new Array();
      loObject.upCurves = new Array();
      loObject.downCurves = new Array();
      loObject.leftCurves = new Array();
      loObject.rightCurves = new Array();
      loObject.cursorWay = null;
    }
  }
  if (psType != 'LINE') {
    goWorkFlow.tasks[goWorkFlow.tasks.length] = loObject;
    if (goWorkFlow.curAddObject) {
      goWorkFlow.newTaskAndLines.push(loObject);
    }
  }
  return loObject;
}

function oCreateXMLNode(poObject) {
  // 创建活动对象的对应jquery的XML节点对象
  var loNode = null;
  var lsName = sGetLang('FLOW_WF_' + poObject.Type + '_NAME');
  var lsNewName = sGetNewName(poObject);
  switch (poObject.Type) {
    case 'TASK':
      loNode = goWorkFlow.xmlDOM.createElement('task');
      loNode.setAttribute('name', lsNewName);
      loNode.setAttribute('id', sGetNewTaskID(poObject));
      loNode.setAttribute('type', '1');
      $(loNode).append(initTaskPropertyContent(poObject));
      var loTasks = goWorkFlow.flowXML.children('tasks');
      loTasks.append($(loNode));
      break;
    case 'SUBFLOW':
      loNode = goWorkFlow.xmlDOM.createElement('task');
      loNode.setAttribute('name', lsNewName);
      loNode.setAttribute('id', sGetNewTaskID(poObject));
      loNode.setAttribute('type', '2');
      var loTasks = goWorkFlow.flowXML.children('tasks');
      loTasks.append($(loNode));
      break;
    case 'BEELINE':
    case 'CURVE':
      if (poObject.toTask.Type == 'CONDITION') {
        break;
      }
      lsName = sGetLang('FLOW_WF_SENDTOTASK') + poObject.toTask.xmlObject.attr('name');
      loNode = goWorkFlow.xmlDOM.createElement('direction');
      loNode.setAttribute('name', lsName);
      loNode.setAttribute('id', sGetNewDirectionID());
      loNode.setAttribute('type', poObject.fromTask.Type == 'CONDITION' ? '2' : '1');
      var loDirections = goWorkFlow.flowXML.find('directions');
      loDirections.append($(loNode));
      break;
    case 'LABEL':
      loNode = goWorkFlow.xmlDOM.createElement('label');
      var ele = goWorkFlow.xmlDOM.createElement('html');
      ele.appendChild(goWorkFlow.xmlDOM.createTextNode(''));
      loNode.appendChild(ele);
      var loLabels = goWorkFlow.flowXML.find('labels');
      loLabels.append($(loNode));
      break;
    case 'CONDITION':
      loNode = goWorkFlow.xmlDOM.createElement('task');
      loNode.setAttribute('name', lsNewName);
      loNode.appendChild(goWorkFlow.xmlDOM.createTextNode(''));
      break;
    case 'BEGIN':
      loNode = goWorkFlow.xmlDOM.createElement('task');
      loNode.setAttribute('name', lsName);
      break;
    case 'END':
      loNode = goWorkFlow.xmlDOM.createElement('task');
      loNode.setAttribute('name', lsName);
      break;
  }
  // xmlObject为jQuery的XML对象
  if (loNode == null) {
    poObject.xmlObject = loNode;
    return loNode;
  }
  poObject.xmlObject = $(loNode);
  return $(loNode);
}
//删除环节
function bDeleteTask(psName) {
  for (var i = 0; i < goWorkFlow.tasks.length; i++) {
    if (goWorkFlow.tasks[i] == null || goWorkFlow.tasks[i].name !== psName) {
      continue;
    }
    removeTaskAndLine(goWorkFlow.tasks[i], i);
    if (goWorkFlow.tasks[i].Type !== 'LABEL') {
      var loObject = goWorkFlow.tasks[i];
      for (var k = 0, n = loObject.outLines.length; k < n; k++) {
        if (loObject.outLines[loObject.outLines.length - 1] != null) {
          bDeleteLine(loObject.outLines[loObject.outLines.length - 1].name, true);
        }
      }
      for (var k = 0, n = loObject.inLines.length; k < n; k++) {
        if (loObject.inLines[loObject.inLines.length - 1] != null) {
          bDeleteLine(loObject.inLines[loObject.inLines.length - 1].name, true);
        }
      }
    }
    goWorkFlow.tasks[i].imgObject.hide();
    if (typeof goWorkFlow.tasks[i].htmlObject === 'object') {
      goWorkFlow.tasks[i].htmlObject.style.display = 'none';
    }

    if (
      goWorkFlow.tasks[i].xmlObject != null &&
      goWorkFlow.tasks[i].xmlObject.getAttribute('uuid') != null &&
      goWorkFlow.tasks[i].xmlObject.getAttribute('uuid') !== ''
    ) {
      var loNode = goWorkFlow.flowXML.selectSingleNode('deletes');
      if (goWorkFlow.tasks[i].Type === 'TASK' || goWorkFlow.tasks[i].Type === 'SUBFLOW') {
        oAddElement(loNode, 'task', goWorkFlow.tasks[i].xmlObject.getAttribute('uuid'));
      } else {
        if (goWorkFlow.tasks[i].Type === 'LABEL') {
          oAddElement(loNode, 'label', goWorkFlow.tasks[i].xmlObject.getAttribute('uuid'));
        }
      }
    }
    if (goWorkFlow.tasks[i].xmlObject != null) {
      goWorkFlow.tasks[i].xmlObject.remove();
    }

    goWorkFlow.tasks.splice(i, 1);
    // goWorkFlow.tasks[i] = null;
    if (goWorkFlow.curAddObject === 'BEELINE') {
      goWorkFlow.curAddObject = null;
      goWorkFlow.fromObject = null;
    }
  }
  return true;
}

function oCreateBeeline(poFrom, poTo, piStroke, poLineObject, event) {
  if (poFrom.name == poTo.name) {
    if (event != undefined && event != null) {
      event.cancelBubble = true;
    }
    return null;
  }
  if (poLineObject != null) {
    var loObject = poLineObject;
    if (loObject.Type == 'CURVE') {
      loObject.Type = 'BEELINE';
      bDeleteWayElement(poFrom, loObject.fromWay + 'Curves', loObject.name);
      bDeleteWayElement(poTo, loObject.toWay + 'Curves', loObject.name);
      loObject.fromWay = null;
      loObject.toWay = null;
    }
  } else {
    var liIndex = goWorkFlow.lines.length;
    var lsName = 'ID_LINE_' + liIndex;
    var loObject = new Object();
    loObject.name = lsName;
    loObject.Type = 'BEELINE';
    loObject.htmlObject = null;
    loObject.fromTask = poFrom;
    loObject.toTask = poTo;
    loObject.stroke = piStroke;
    loObject.isShowName = goWorkFlow.isShowLineName;
    loObject.labelObject = oCreateTask('LINE', 0, 0);
    loObject.labelObject.parent = loObject;
    loObject.labelObject.imgObject.setCursor('default');
    loObject.labelObject.imgObject.hide();
    goWorkFlow.lines[liIndex] = loObject;
    if (goWorkFlow.curAddObject) {
      addTasksAndLines(loObject);
    }
    poFrom.outLines[poFrom.outLines.length] = loObject;
    poTo.inLines[poTo.inLines.length] = loObject;
  }
  loObject.x2 = poTo.imgObject.x + poTo.imgObject.w / 2;
  loObject.y2 = poTo.imgObject.y + poTo.imgObject.h / 2;
  var laXY = aTaskBeelinePoint(poFrom, loObject.x2, loObject.y2);
  loObject.x1 = laXY[0];
  loObject.y1 = laXY[1];
  laXY = aTaskBeelinePoint(poTo, loObject.x1, loObject.y1);
  loObject.x2 = laXY[0];
  loObject.y2 = laXY[1];
  bReDrawLine(loObject);
  // 綁定右鍵菜單
  var lineId = '#' + $(loObject.htmlObject).attr('id');
  bindLineContextMenu(lineId, loObject);
  if (event != undefined && event != null) {
    event.cancelBubble = true;
  }
  return loObject;
}

function bindLineContextMenu(lineId, loObject) {
  $(lineId).contextMenu('ID_MENU_DIRECTION', {
    bindings: {
      directionProperty: function (t) {
        setDirectionProperty();
      },
      arrow: function (t) {
        bSetDirection('arrow');
      },
      curve: function (t) {
        bSetDirection('curve');
      },
      beeline: function (t) {
        bSetDirection('beeline');
      },
      showDLineName: function (t) {
        showLineName(true, true);
      },
      hideDLineName: function (t) {
        showLineName(false, true);
      },
      deleteDirection: function (t) {
        bDealObject('delete');
      }
    },
    onKeyDown: function (e) {
      // 设置选中状态
      try {
        contextMenu(e);
      } catch (e) { }
    },
    onShowMenu: function (e, menu) {
      if (loObject.Type == 'BEELINE') {
        $('#beeline', menu).remove();
      } else if (loObject.Type == 'CURVE') {
        $('#curve', menu).remove();
      }
      return menu;
    }
  });
}

function oCreateCurve(poFrom, poTo, piStroke, poLineObject, event) {
  if (poFrom.name == poTo.name) {
    if (event != undefined && event != null) {
      event.cancelBubble = true;
    }
    return null;
  }
  var lbNew = false;
  if (poLineObject != null) {
    var loObject = poLineObject;
    if (loObject.Type == 'BEELINE') {
      loObject.Type = 'CURVE';
      loObject.fromWay = sGetTaskCurveWay(poFrom, loObject.x1, loObject.y1);
      loObject.toWay = sGetTaskCurveWay(poTo, loObject.x2, loObject.y2);
      // eval("poFrom." + loObject.fromWay + "Curves[poFrom."
      // + loObject.fromWay + "Curves.length]=loObject;");
      // evalLoFrom1(poFrom, loObject);
      setPoObject(poFrom, loObject.fromWay, loObject);
      // eval("poTo." + loObject.toWay + "Curves[poTo." + loObject.toWay
      // + "Curves.length]=loObject;");
      // evalLoTo1(poTo, loObject);
      setPoObject(poTo, loObject.toWay, loObject);
      lbNew = true;
    }
  } else {
    lbNew = true;
    var liIndex = goWorkFlow.lines.length;
    var lsName = 'ID_LINE_' + liIndex;
    var loObject = new Object();
    loObject.fromWay = poFrom.cursorWay;
    loObject.toWay = poTo.cursorWay;
    loObject.name = lsName;
    loObject.Type = 'CURVE';
    loObject.htmlObject = null;
    loObject.fromTask = poFrom;
    loObject.toTask = poTo;
    loObject.stroke = piStroke;
    loObject.isShowName = goWorkFlow.isShowLineName;
    loObject.labelObject = oCreateTask('LINE', 0, 0);
    loObject.labelObject.parent = loObject;
    loObject.labelObject.imgObject.setCursor('default');
    loObject.labelObject.imgObject.hide();
    goWorkFlow.lines[liIndex] = loObject;
    if (goWorkFlow.curAddObject) {
      addTasksAndLines(loObject);
    }
    poFrom.outLines[poFrom.outLines.length] = loObject;
    poTo.inLines[poTo.inLines.length] = loObject;
    // eval("poFrom." + poFrom.cursorWay + "Curves[poFrom." +
    // poFrom.cursorWay
    // + "Curves.length]=loObject;");
    // evalPoFrom(poFrom, loObject);
    setPoObject(poFrom, poFrom.cursorWay, loObject);
    // eval("poTo." + poTo.cursorWay + "Curves[poTo." + poTo.cursorWay
    // + "Curves.length]=loObject;");
    // evalPoTo(poTo, loObject);
    setPoObject(poTo, poTo.cursorWay, loObject);
  }
  loObject.x1 = poFrom.imgObject.x + poFrom.imgObject.w / 2;
  loObject.y1 = poFrom.imgObject.y + poFrom.imgObject.h / 2;
  loObject.x2 = poTo.imgObject.x + poTo.imgObject.w / 2;
  loObject.y2 = poTo.imgObject.y + poTo.imgObject.h / 2;
  var laXY = aTaskCurvePoint(poFrom, loObject.fromWay, loObject, lbNew, loObject.x2, loObject.y2);
  loObject.x1 = laXY[0];
  loObject.y1 = laXY[1];
  var liFromIndex = laXY[2];
  laXY = aTaskCurvePoint(poTo, loObject.toWay, loObject, lbNew, loObject.x1, loObject.y1);
  loObject.x2 = laXY[0];
  loObject.y2 = laXY[1];
  var liToIndex = laXY[2];
  loObject.maxIndex = liFromIndex > liToIndex ? liFromIndex : liToIndex;
  bReDrawLine(loObject);
  if (event != undefined && event != null) {
    event.cancelBubble = true;
  }
  // 綁定右鍵菜單
  var lineId = '#' + $(loObject.htmlObject).attr('id');
  $(lineId).contextMenu('ID_MENU_DIRECTION', {
    bindings: {
      directionProperty: function (t) {
        setDirectionProperty();
      },
      arrow: function (t) {
        bSetDirection('arrow');
      },
      curve: function (t) {
        bSetDirection('curve');
      },
      beeline: function (t) {
        bSetDirection('beeline');
      },
      showDLineName: function (t) {
        showLineName(true, true);
      },
      hideDLineName: function (t) {
        showLineName(false, true);
      },
      deleteDirection: function (t) {
        bDealObject('delete');
      }
    },
    onKeyDown: function (e) {
      // 设置选中状态
      try {
        contextMenu(e);
      } catch (e) { }
    },
    onShowMenu: function (e, menu) {
      if (loObject.Type == 'BEELINE') {
        $('#beeline', menu).remove();
      } else if (loObject.Type == 'CURVE') {
        $('#curve', menu).remove();
      }
      return menu;
    }
  });
  return loObject;
}

function setPoObject(poObject, way, loObject) {
  if (way === 'up') {
    poObject.upCurves[poObject.upCurves.length] = loObject;
  } else if (way === 'down') {
    poObject.downCurves[poObject.downCurves.length] = loObject;
  } else if (way === 'left') {
    poObject.leftCurves[poObject.leftCurves.length] = loObject;
  } else if (way === 'right') {
    poObject.rightCurves[poObject.rightCurves.length] = loObject;
  }
}

function bReDrawLine(poObject) {
  var loObject = poObject;
  if (loObject.Type === 'BEELINE') {
    if (loObject.htmlObject != null && typeof loObject.htmlObject === 'object') {
      loObject.htmlObject.outerHTML = '';
    }
    bDrawBeeline(loObject.name, loObject.x1, loObject.y1, loObject.x2, loObject.y2, loObject.stroke);
    loObject.htmlObject = dd.getDiv(loObject.name);
    var x3 = (loObject.x1 + loObject.x2) / 2;
    var y3 = (loObject.y1 + loObject.y2) / 2;
  } else {
    if (loObject.Type === 'CURVE') {
      if (loObject.htmlObject != null && typeof loObject.htmlObject === 'object') {
        loObject.htmlObject.outerHTML = '';
      }
      var laX_Y = aGetCurvePoint(loObject.x1, loObject.y1, loObject.x2, loObject.y2, loObject.fromWay, loObject.toWay, loObject.maxIndex);
      bDrawCurve(loObject.name, laX_Y[0], laX_Y[1], loObject.stroke);
      loObject.htmlObject = dd.getDiv(loObject.name);
      if (laX_Y[0].length == 6) {
        var x3 = (laX_Y[0][2] + laX_Y[0][3]) / 2;
        var y3 = (laX_Y[1][2] + laX_Y[1][3]) / 2;
      } else {
        var liLength1 = Math.abs(laX_Y[0][1] == laX_Y[0][2] ? laX_Y[1][2] - laX_Y[1][1] : laX_Y[0][2] - laX_Y[0][1]);
        var liLength2 = Math.abs(laX_Y[0][3] == laX_Y[0][2] ? laX_Y[1][2] - laX_Y[1][3] : laX_Y[0][2] - laX_Y[0][3]);
        if (liLength1 > liLength2) {
          var x3 = (laX_Y[0][2] + laX_Y[0][1]) / 2;
          var y3 = (laX_Y[1][2] + laX_Y[1][1]) / 2;
        } else {
          var x3 = (laX_Y[0][2] + laX_Y[0][3]) / 2;
          var y3 = (laX_Y[1][2] + laX_Y[1][3]) / 2;
        }
      }
    }
  }
  if (goWorkFlow.curStatus != 'load') {
    loObject.labelObject.imgObject.moveTo(x3 - loObject.labelObject.imgObject.w / 2, y3 - loObject.labelObject.imgObject.h);
  }
  return true;
}

function bDeleteLine(psName, unPushToStack) {
  for (var i = 0; i < goWorkFlow.lines.length; i++) {
    if (goWorkFlow.lines[i] == null || goWorkFlow.lines[i].name !== psName) {
      continue;
    }
    var loFrom = goWorkFlow.lines[i].fromTask;
    var loTo = goWorkFlow.lines[i].toTask;
    if (!unPushToStack) {
      removeTaskAndLine(goWorkFlow.lines[i], i);
    } else {
      removeTaskAndLine(goWorkFlow.lines[i], i, true);
    }

    if (goWorkFlow.lines[i].Type === 'CURVE') {
      bDeleteWayElement(loFrom, goWorkFlow.lines[i].fromWay + 'Curves', psName);
      bDeleteWayElement(loTo, goWorkFlow.lines[i].toWay + 'Curves', psName);
    }
    if (typeof goWorkFlow.lines[i].htmlObject === 'object') {
      // goWorkFlow.lines[i].htmlObject.outerHTML = "";
      goWorkFlow.lines[i].htmlObject.style.display = 'none';
    }
    bDeleteWayElement(loFrom, 'outLines', psName);
    bDeleteWayElement(loTo, 'inLines', psName);
    goWorkFlow.lines[i].labelObject.imgObject.hide();
    if (typeof goWorkFlow.lines[i].labelObject.htmlObject === 'object') {
      goWorkFlow.lines[i].labelObject.htmlObject.style.display = 'none';
    }
    if (
      goWorkFlow.lines[i].xmlObject != null &&
      goWorkFlow.lines[i].xmlObject.getAttribute('uuid') != null &&
      goWorkFlow.lines[i].xmlObject.getAttribute('uuid') !== ''
    ) {
      var loNode = goWorkFlow.flowXML.selectSingleNode('deletes');
      oAddElement(loNode, 'direction', goWorkFlow.lines[i].xmlObject.getAttribute('uuid'));
    }
    if (goWorkFlow.lines[i].xmlObject != null) {
      goWorkFlow.lines[i].xmlObject.remove();
    }
    goWorkFlow.lines[i] = null;
    break;
  }
  return true;
}

function bDeleteWayElement(poObject, psArrayName, psName) {
  var laSource, laNew;
  laNew = new Array();
  // eval("laSource = poObject." + psArrayName);
  laSource = poObject[psArrayName];
  for (var j = 0; j < laSource.length; j++) {
    if (laSource[j].name != psName) {
      laNew[laNew.length] = laSource[j];
    }
  }
  // eval("poObject." + psArrayName + " = laNew");
  poObject[psArrayName] = laNew;
  return true;
}

function bShowLabelInput(psName) {
  var lsName = psName != null ? psName : goWorkFlow.curObjectName;
  var loObject = oGetTaskObject(lsName);
  if (loObject == null || loObject.Type != 'LABEL') {
    return false;
  }
  top.appModal.hideMask();
  var loInput = dd.getDiv('ID_LABELVALUEDIV');
  loInput.style.display = '';
  loInput.style.zIndex = 10000;
  loInput.style.left = loObject.imgObject.x + 'px';
  loInput.style.top = loObject.imgObject.y + 'px';
  loInput.style.width = loObject.imgObject.w + 'px';
  loInput.style.height = loObject.imgObject.h + 'px';
  var element = loObject.xmlObject.selectSingleNode('html');
  document.all.ID_LABELVALUE.value = element.text() != null ? element.text() : '';
  document.all.ID_LABELVALUE.focus();
  document.onselectstart = Function('return true;');
  return true;
}

function bSetLabelHTML(psName) {
  var lsName = psName != null ? psName : goWorkFlow.curObjectName;
  var loObject = oGetTaskObject(lsName);
  var loInput = dd.getDiv('ID_LABELVALUEDIV');
  if (loInput.style.display === '') {
    document.onselectstart = Function('return false;');
    loInput.style.display = 'none';
    var element = loObject.xmlObject.selectSingleNode('html');
    if (element.length > 0) {
      var _value = document.all.ID_LABELVALUE.value;
      element.get(0).childNodes[0].nodeValue = _value;
      if (_value) {
        $(loObject.htmlObject).removeClass('flow-control-error');
      }
      bSetObjectText(loObject);
    }
    return true;
  }
  return false;
}

function bSetObjectText(poObject) {
  var lsFontWeight = poObject.Type === 'BEGIN' || poObject.Type === 'END' ? Font.BOLD : Font.PLAIN;
  var lsValue =
    poObject.Type === 'LABEL'
      ? poObject.xmlObject.selectSingleNode('html').text()
      : poObject.Type === 'LINE'
        ? poObject.parent.xmlObject.getAttribute('name')
        : poObject.xmlObject.getAttribute('name');
  if (lsValue == null) {
    lsValue = '';
  }
  if (poObject.Type === 'LINE' && lsValue != null && lsValue !== '') {
    var liX = poObject.imgObject.x + poObject.imgObject.w / 2;
    poObject.imgObject.resizeTo(20 + lsValue.length * 12, poObject.imgObject.h);
    poObject.imgObject.moveTo(liX - poObject.imgObject.w / 2, poObject.imgObject.y);
  }
  var lsHTML =
    '<div class="flow-control-text-wrap"><div class="flow-control-table"><div class="flow-control-text" objtype="' +
    poObject.Type +
    '" objname="' +
    poObject.name +
    '" title="' +
    lsValue +
    '">' +
    lsValue +
    '</div></div></div>';
  var $wrap = $(poObject.htmlObject).find('.flow-control-text-wrap');
  if ($wrap.length) {
    $wrap.remove();
  }
  $(poObject.htmlObject).append(lsHTML);

  if (poObject.Type === 'LINE') {
    var lodiv = $(poObject.htmlObject);
    if (lodiv.text() == null || lodiv.text() == '') {
      poObject.imgObject.hide();
    } else {
      if (poObject.parent.isShowName == true) {
        poObject.imgObject.show();
      } else {
        poObject.imgObject.hide();
      }
    }
  } else {
    if (poObject.Type === 'LABEL') {
      var lodiv = $(poObject.htmlObject);
      if (lodiv.text()) {
        $(lodiv).addClass('no-border');
      } else {
        $(lodiv).removeClass('no-border');
      }
      if (goWorkFlow.isShowLabel) {
        poObject.imgObject.show();
      } else {
        poObject.imgObject.hide();
      }
    }
  }
}

function moveCanvas(psWay) {
  for (var i = 0; i < goWorkFlow.tasks.length; i++) {
    if (goWorkFlow.tasks[i] == null) {
      continue;
    }
    var loObject = goWorkFlow.tasks[i];
    switch (psWay) {
      case 'up':
        loObject.imgObject.moveTo(loObject.imgObject.x, loObject.imgObject.y - 40);
        break;
      case 'down':
        loObject.imgObject.moveTo(loObject.imgObject.x, loObject.imgObject.y + 40);
        break;
      case 'left':
        loObject.imgObject.moveTo(loObject.imgObject.x - 40, loObject.imgObject.y);
        break;
      case 'right':
        loObject.imgObject.moveTo(loObject.imgObject.x + 40, loObject.imgObject.y);
        break;
    }
  }
  for (var i = 0; i < goWorkFlow.lines.length; i++) {
    if (goWorkFlow.lines[i] == null) {
      continue;
    }
    if (goWorkFlow.lines[i].Type == 'BEELINE') {
      oCreateBeeline(goWorkFlow.lines[i].fromTask, goWorkFlow.lines[i].toTask, null, goWorkFlow.lines[i]);
    } else {
      oCreateCurve(goWorkFlow.lines[i].fromTask, goWorkFlow.lines[i].toTask, null, goWorkFlow.lines[i]);
    }
  }
}

function showLineName(pbShow, pbSingle) {
  var loObject = null;
  if (goWorkFlow.curObjectName != null && goWorkFlow.curObjectName != '') {
    loObject = oGetLineObject(goWorkFlow.curObjectName);
  }
  if (loObject != null && pbSingle == true) {
    loObject.isShowName = pbShow;
    if (loObject.isShowName == true) {
      loObject.labelObject.imgObject.show();
    } else {
      loObject.labelObject.imgObject.hide();
    }
  } else {
    goWorkFlow.isShowLineName = pbShow;
    for (var i = 0; i < goWorkFlow.lines.length; i++) {
      if (goWorkFlow.lines[i] == null) {
        continue;
      }
      goWorkFlow.lines[i].isShowName = goWorkFlow.isShowLineName;
      if (
        goWorkFlow.lines[i].xmlObject != null &&
        goWorkFlow.lines[i].xmlObject.getAttribute('name') != null &&
        goWorkFlow.lines[i].xmlObject.getAttribute('name') != ''
      ) {
        if (goWorkFlow.lines[i].isShowName == true) {
          goWorkFlow.lines[i].labelObject.imgObject.show();
        } else {
          goWorkFlow.lines[i].labelObject.imgObject.hide();
        }
      } else {
        goWorkFlow.lines[i].labelObject.imgObject.hide();
      }
    }
  }
}

function showLabel(pbShow) {
  goWorkFlow.isShowLabel = pbShow;
  for (var i = 0; i < goWorkFlow.tasks.length; i++) {
    if (goWorkFlow.tasks[i] == null || goWorkFlow.tasks[i].Type != 'LABEL') {
      continue;
    }
    if (goWorkFlow.isShowLabel == true) {
      goWorkFlow.tasks[i].imgObject.show();
    } else {
      goWorkFlow.tasks[i].imgObject.hide();
    }
  }
}

function saveWorkFlow(pbNew) {
  var curTabVerify = temporarySaveProperty(true);
  if (!curTabVerify) {
    return;
  }
  if (!checkWorkFlow(pbNew)) {
    return;
  }
  changeLabelText();
  top.appModal.showMask();
  var lsVersion = goWorkFlow.flowXML.getAttribute('version');
  if (lsVersion == null || lsVersion == '') {
    lsVersion = '1.0';
    goWorkFlow.flowXML.setAttribute('version', '1.0');
  } else {
    if (pbNew) {
      lsVersion = goWorkFlow.flowXML.getAttribute('lastVer');
      if (lsVersion == undefined || lsVersion == null) {
        lsVersion = '1.0';
      }
      lsVersion = lsVersion - 0 + 0.111 + '';
      lsVersion = lsVersion.substring(0, 3);
      goWorkFlow.flowXML.setAttribute('version', lsVersion);
      goWorkFlow.flowXML.setAttribute('uuid', '');
    }
  }
  var loNode = goWorkFlow.flowXML.selectSingleNode('./property/categorySN');
  loNode = goWorkFlow.dictionXML.selectSingleNode("/diction/categorys/category[code='" + loNode.text() + "']");
  if (loNode == null) {
    appModal.error('流程分类不存在！');
    return;
  }
  var lsCategory = loNode.selectSingleNode('name').text();
  var title = goWorkFlow.flowXML.getAttribute('name') + '（' + goWorkFlow.flowXML.getAttribute('version') + '）';
  if (onlyGraphics == undefined) {
    parent.document.title = title;
  }
  $('.work-flow-title', parent.document).text(title);
  console.log(parent.sGetLang('FLOW_WF_FLOWSAVING'));
  for (var i = 0; i < goWorkFlow.tasks.length; i++) {
    if (goWorkFlow.tasks[i] != null && goWorkFlow.tasks[i].xmlObject != null) {
      var loNode = goWorkFlow.tasks[i].xmlObject;
      switch (goWorkFlow.tasks[i].Type) {
        case 'TASK':
        case 'SUBFLOW':
          oSetElement(loNode, 'X', goWorkFlow.tasks[i].imgObject.x + goWorkFlow.tasks[i].imgObject.w / 2 + '');
          oSetElement(loNode, 'Y', goWorkFlow.tasks[i].imgObject.y + goWorkFlow.tasks[i].imgObject.h / 2 + '');
          var loObject = oGetConditionObjectByFromTaskName(loNode.getAttribute('name'));
          if (loObject != null) {
            oSetElement(loNode, 'conditionName', loObject.xmlObject.getAttribute('name'));
            oSetElement(loNode, 'conditionBody', loObject.xmlObject.text());
            oSetElement(loNode, 'conditionX', loObject.imgObject.x + loObject.imgObject.w / 2 + '');
            oSetElement(loNode, 'conditionY', loObject.imgObject.y + loObject.imgObject.h / 2 + '');
            loObject = goWorkFlow.tasks[i].outLines[0];
            if (loObject.Type == 'BEELINE') {
              oSetElement(loNode, 'conditionLine', 'BEELINE');
            } else {
              oSetElement(loNode, 'conditionLine', 'CURVE;' + loObject.fromWay + ';' + loObject.toWay);
            }
          } else {
            oSetElement(loNode, 'conditionName', '');
            oSetElement(loNode, 'conditionBody', '');
            oSetElement(loNode, 'conditionX', '');
            oSetElement(loNode, 'conditionY', '');
            oSetElement(loNode, 'conditionLine', '');
          }
          break;
        case 'LABEL':
          oSetElement(loNode, 'X', goWorkFlow.tasks[i].imgObject.x + goWorkFlow.tasks[i].imgObject.defw / 2 + '');
          oSetElement(loNode, 'Y', goWorkFlow.tasks[i].imgObject.y + goWorkFlow.tasks[i].imgObject.defh / 2 + '');
          oSetElement(loNode, 'W', goWorkFlow.tasks[i].imgObject.w + '');
          oSetElement(loNode, 'H', goWorkFlow.tasks[i].imgObject.h + '');
          break;
      }
    }
  }
  for (var i = 0; i < goWorkFlow.lines.length; i++) {
    if (goWorkFlow.lines[i] != null && goWorkFlow.lines[i].xmlObject != null) {
      var loObject = null;
      var lsSource = '',
        lsTarget = '';
      if (goWorkFlow.lines[i].fromTask.Type == 'BEGIN') {
        loObject = goWorkFlow.lines[i].fromTask;
        lsSource = '<StartFlow>';
      } else {
        if (goWorkFlow.lines[i].fromTask.Type == 'CONDITION') {
          var loLine = goWorkFlow.lines[i].fromTask.inLines[0];
          lsSource = loLine.fromTask.xmlObject.getAttribute('id');
        } else {
          lsSource = goWorkFlow.lines[i].fromTask.xmlObject.getAttribute('id');
        }
      }
      if (goWorkFlow.lines[i].toTask.Type == 'END') {
        loObject = goWorkFlow.lines[i].toTask;
        lsTarget = '<EndFlow>';
      } else {
        lsTarget = goWorkFlow.lines[i].toTask.xmlObject.getAttribute('id');
      }
      var loNode = goWorkFlow.lines[i].xmlObject;
      if (pbNew == true) {
        loNode.setAttribute('uuid', '');
      }
      loNode.setAttribute('fromID', lsSource);
      loNode.setAttribute('toID', lsTarget);
      if (loObject != null) {
        oSetElement(loNode, 'terminalName', loObject.xmlObject.getAttribute('name'));
        oSetElement(loNode, 'terminalType', loObject.Type);
        oSetElement(loNode, 'terminalX', loObject.imgObject.x + loObject.imgObject.w / 2 + '');
        oSetElement(loNode, 'terminalY', loObject.imgObject.y + loObject.imgObject.h / 2 + '');
        oSetElement(loNode, 'terminalBody', loObject.xmlObject.text());
      } else {
        oSetElement(loNode, 'terminalName', '');
        oSetElement(loNode, 'terminalType', '');
        oSetElement(loNode, 'terminalX', '');
        oSetElement(loNode, 'terminalY', '');
        oSetElement(loNode, 'terminalBody', '');
      }
      loObject = goWorkFlow.lines[i];
      oSetElement(loNode, 'lineLabel', loObject.labelObject.imgObject.x + ';' + loObject.labelObject.imgObject.y);
      if (loObject.Type == 'BEELINE') {
        oSetElement(loNode, 'line', 'BEELINE');
      } else {
        oSetElement(loNode, 'line', 'CURVE;' + loObject.fromWay + ';' + loObject.toWay);
      }
      oSetElement(loNode, 'isShowName', loObject.isShowName == true ? '1' : '0');
    }
  }
  var xmlString = XMLtoString(goWorkFlow.flowXML.get(0));
  parent.clearObjectUuid = goWorkFlow.flowXML.getAttribute('uuid');
  $.ajax({
    url: '/workflow/scheme/save.action?pbNew=' + (pbNew == true),
    type: 'POST',
    data: JSON.stringify({
      xmlString: xmlString
    }),
    contentType: 'application/json',
    dataType: 'xml',
    error: function (data) {
      top.appModal.hideMask();
      if (data.status === 417) {
        showMsg(data.responseText, true);
      } else {
        if (data.responseText) {
          showMsg(data.responseText, true);
        } else {
          showMsg(parent.sGetLang('FLOW_WF_SAVEFAILURE'), true);
        }
      }
    },
    success: function (data) {
      top.appModal.hideMask();
      goWorkFlow.flowXML = $(data).selectSingleNode('flow');
      appModal.success(parent.sGetLang('FLOW_WF_SAVESUCCESS'));
      var newUuid = goWorkFlow.flowXML.getAttribute('uuid');
      setTimeout(function () {
        location = '/web/app/pt-mgr/pt-wf-mgr/pt-wf-designer.html?open&id=' + newUuid;
      }, 1000);
    }
  });
}

function checkWorkFlow(pbNew) {
  goWorkFlow.selectObjects = [];
  var loXML = goWorkFlow.flowXML;
  var loFlow = loXML.selectSingleNode('property');
  if (loFlow == null) {
    showMsg(parent.sGetLang('FLOW_WF_FLOWPROPERTYISEMPTY'), true);
    return false;
  }
  if (goWorkFlow.sortOrderError) {
    showMsg('流向排序不正确!', true);
    return false;
  }
  var noEmptyItem = [];
  if (loXML.getAttribute('name') == null || loXML.getAttribute('name') == '') {
    noEmptyItem.push('流程名称');
  }
  if (loXML.getAttribute('id') == null || loXML.getAttribute('id') == '') {
    noEmptyItem.push('流程ID');
  }

  if (loFlow.selectSingleNode('categorySN') == null || loFlow.selectSingleNode('categorySN').text() == '') {
    noEmptyItem.push('流程分类');
  }
  if (loXML.getAttribute('moduleId') == null || loXML.getAttribute('moduleId') === '') {
    noEmptyItem.push('所属模块');
  }
  if (loXML.selectSingleNode('formID') == null || loXML.selectSingleNode('formID').text() === '') {
    noEmptyItem.push('使用表单');
  }
  if (noEmptyItem.length) {
    showMsg('请填写' + noEmptyItem.join('、'), true);
    return false;
  }

  if (loXML.getAttribute('systemUnitId') == null || loXML.getAttribute('systemUnitId') == '') {
    showMsg(parent.sGetLang('FLOW_WF_FLOWSYSTEMUNITIDISEMPTY'), true);
    return false;
  }
  if (/^[a-zA-Z_0-9]+$/.test(loXML.getAttribute('id')) == false) {
    showMsg(parent.sGetLang('FLOW_WF_FLOWALIASISRULE'), true);
    return false;
  }

  if (
    loXML.selectSingleNode('enableAccessPermissionProvider') != null &&
    loXML.selectSingleNode('enableAccessPermissionProvider').text() == '1' &&
    loXML.selectSingleNode('accessPermissionProvider').text() == ''
  ) {
    showMsg('请选择流程数据鉴权接口', true);
    return false;
  }

  // 等价流程
  var loNode = loFlow.selectSingleNode('./property/equalFlow/name');
  if (loNode != null && loNode.text() != '') {
    if (loNode.text() == '-1') {
      showMsg('请选择等价流程！', true);
      return false;
    }
    var lbOther = false;
    for (var i = 0; i < goWorkFlow.tasks.length; i++) {
      if (goWorkFlow.tasks[i] == null || goWorkFlow.tasks[i].Type == 'LABEL') {
        continue;
      }
      lbOther = true;
      break;
    }
    if (lbOther == true) {
      // showMsg(parent.sGetLang("FLOW_WF_EQFLOWMOREOBJECT"), true);
      // return false;
    } else {
      showMsg(parent.sGetLang('FLOW_WF_SETTINGTRUE'));
      return true;
    }
  }

  var isExitID = false;
  if (goWorkFlow.isNewFlow) {
    $.get({
      url: ctx + '/api/workflow/definition/countById',
      data: {
        flowDefId: loXML.getAttribute('id')
      },
      async: false,
      success: function (result) {
        if (parseInt(result.data)) {
          isExitID = true;
        }
      }
    });
  }

  var errorStatus = false;
  if (!pbNew) {
    var xmlString = XMLtoString(goWorkFlow.flowXML.get(0));
    $.ajax({
      url: ctx + '/api/workflow/scheme/checkFlowXmlForUpdate',
      type: 'POST',
      data: JSON.stringify({
        xmlString: xmlString
      }),
      contentType: 'application/json',
      dataType: 'json',
      async: false,
      success: function (result) {
        if (!result.data) {
          if (result.msg) {
            showMsg(result.msg, true);
            errorStatus = true;
          } else {
            var curFormID = goWorkFlow.flowXML.selectSingleNode('formID').text();
            if (goWorkFlow.initFormID && goWorkFlow.initFormID !== curFormID) {
              showMsg('存在未办结的流程，无法直接修改使用表单，请保存新版本！', true);
              errorStatus = true;
              return false;
            }
            if (!$.isEmptyObject(goWorkFlow.changeNodeIds)) {
              $.map(goWorkFlow.changeNodeIds, function (item) {
                setModifyTaskStatusById(item.modifyId, 'error');
              });
              showMsg('存在未办结的流程，无法直接修改节点ID，请保存新版本', true);
              errorStatus = true;
              return false;
            }
            var isFree = goWorkFlow.flowXML.selectSingleNode('isFree').text() || '0';
            var equalFlow_ID = goWorkFlow.flowXML.selectSingleNode('equalFlow/id').text();
            if (
              (goWorkFlow.isFree && isFree != goWorkFlow.isFree) ||
              (goWorkFlow.equalFlow_ID && equalFlow_ID !== goWorkFlow.equalFlow_ID)
            ) {
              showMsg('存在未办结的流程，无法设置等价流程/自由流程，请保存新版本', true);
              errorStatus = true;
              return false;
            }
            if (goWorkFlow.newTaskAndLines.length) {
              $.map(goWorkFlow.newTaskAndLines, function (item) {
                switch (item.Type) {
                  case 'TASK':
                  case 'SUBFLOW':
                    $(item.htmlObject).addClass('flow-control-error');
                    break;
                  case 'BEELINE':
                  case 'CURVE':
                    $(item.htmlObject).find('div').css('background-color', '#e33033');
                    $(item.labelObject.htmlObject).addClass('flow-control-error');
                    break;
                }
              });
              showMsg('存在未办结的流程，无法直接新增或删除节点、流向，请保存新版本！', true);
              errorStatus = true;
              return false;
            }
            if (goWorkFlow.resetTaskAndLineStack.length) {
              showMsg('存在未办结的流程，无法直接新增或删除节点、流向，请保存新版本！', true);
              errorStatus = true;
            }
            if (!errorStatus) {
              showMsg(result.msg, true);
              errorStatus = true;
            }
          }
        }
      },
      error: function (error) {
        showMsg('流程定义XML检验服务出错！', true);
        errorStatus = true;
      }
    });
  }
  if (errorStatus) {
    return false;
  }

  if (isExitID) {
    showMsg(parent.sGetLang('FLOW_WF_FLOWIDISEXIT'), true);
    return false;
  }

  var lsAllTaskNames = ';',
    lsAllLineNames = ';',
    lsAllTaskIDs = ';';
  var loBegin = null,
    liCount = 0;
  var lsErrorMsg = '';
  for (var i = 0; i < goWorkFlow.tasks.length; i++) {
    if (goWorkFlow.tasks[i] == null) {
      continue;
    }
    liCount = 0;
    switch (goWorkFlow.tasks[i].Type) {
      case 'BEGIN':
        if (loBegin == null) {
          loBegin = goWorkFlow.tasks[i];
        } else {
          // goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.tasks[i];
          // bShowErrorObject(true);
          showMsg(parent.sGetLang('FLOW_WF_BEGINISMORE'), true);
          return false;
        }
        break;
      case 'END':
        for (var j = 0; j < goWorkFlow.tasks[i].outLines.length; j++) {
          if (goWorkFlow.tasks[i].outLines[j] != null) {
            break;
          }
        }
        if (j < goWorkFlow.tasks[i].outLines.length) {
          goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.tasks[i];
          if (lsErrorMsg == '') {
            lsErrorMsg = sGetLang('FLOW_WF_ENDOBJECTEXISTOUTLINE');
          }
        } else {
          for (var j = 0; j < goWorkFlow.tasks[i].inLines.length; j++) {
            if (goWorkFlow.tasks[i].inLines[j] != null) {
              liCount++;
              if (liCount > 1) {
                break;
              }
            }
          }
          if (j < goWorkFlow.tasks[i].inLines.length || liCount == 0) {
            goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.tasks[i];
            if (lsErrorMsg == '') {
              lsErrorMsg = sGetLang('FLOW_WF_ENDOBJECTMOREINLINE');
            }
          } else {
            if (
              goWorkFlow.tasks[i].Type == 'SUBFLOW' &&
              (goWorkFlow.tasks[i].xmlObject == null ||
                goWorkFlow.tasks[i].xmlObject.getAttribute('name') == null ||
                goWorkFlow.tasks[i].xmlObject.getAttribute('name') == '')
            ) {
              goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.tasks[i];
              if (lsErrorMsg == '') {
                lsErrorMsg = sGetLang('FLOW_WF_SUBFLOWPROPERTYISEMPTY');
              }
            }
          }
        }
        break;
      case 'LABEL':
        changeLabelText();
        if (
          goWorkFlow.tasks[i].xmlObject == null ||
          goWorkFlow.tasks[i].xmlObject.selectSingleNode('html') == null ||
          goWorkFlow.tasks[i].xmlObject.selectSingleNode('html').text() == ''
        ) {
          goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.tasks[i];
          $(goWorkFlow.tasks[i].htmlObject).addClass('flow-control-error');
          if (lsErrorMsg == '') {
            lsErrorMsg = sGetLang('FLOW_WF_LABELHTMLISEMPTY');
          }
        }
        break;
      case 'TASK':
        var lsName = null,
          lsID = null,
          lbFree = false;
        var loNode = loXML.selectSingleNode('./property/isFree');
        if (loNode != null && loNode.text() == '1') {
          lbFree = true;
        }
        // 指定办理人、抄送人、督办人
        if (goWorkFlow.tasks[i].xmlObject != null) {
          lsName = goWorkFlow.tasks[i].xmlObject.getAttribute('name');
          lsID = goWorkFlow.tasks[i].xmlObject.getAttribute('id');
          var loNode = goWorkFlow.tasks[i].xmlObject.selectSingleNode('isSetUser');
          if (loNode != null && loNode.text() == '1') {
            loNode = goWorkFlow.tasks[i].xmlObject.selectSingleNode('./task/users/unit');
            if (loNode == null) {
              lsErrorMsg = sGetLang('FLOW_WF_TASKPROPERTY_SETUSER_ERROR');
            }
          }
          loNode = goWorkFlow.tasks[i].xmlObject.selectSingleNode('isSetCopyUser');
          if (loNode != null && loNode.text() == '1') {
            loNode = goWorkFlow.tasks[i].xmlObject.selectSingleNode('./task/copyUsers/unit');
            if (loNode == null && lsErrorMsg == '') {
              lsErrorMsg = sGetLang('FLOW_WF_TASKPROPERTY_SETCOPYUSER_ERROR');
            }
          }

          loNode = goWorkFlow.tasks[i].xmlObject.selectSingleNode('isSetTransferUser');
          if (loNode != null && loNode.text() == '1') {
            loNode = goWorkFlow.tasks[i].xmlObject.selectSingleNode('./task/transferUsers/unit');
            if (loNode == null && lsErrorMsg == '') {
              lsErrorMsg = sGetLang('FLOW_WF_TASKPROPERTY_SETTRANSFERUSER_ERROR');
            }
          }

          loNode = goWorkFlow.tasks[i].xmlObject.selectSingleNode('isSetMonitor');
          if (loNode != null && loNode.text() == '1') {
            loNode = goWorkFlow.tasks[i].xmlObject.selectSingleNode('./task/monitors/unit');
            if (loNode == null && lsErrorMsg == '') {
              lsErrorMsg = sGetLang('FLOW_WF_TASKPROPERTY_SETMONITOR_ERROR');
            }
          }
          // 待办意见立场启用必填判断
          loNode = goWorkFlow.tasks[i].xmlObject.selectSingleNode('enableOpinionPosition');
          if (loNode != null && loNode.text() == '1') {
            loNode = goWorkFlow.tasks[i].xmlObject.selectNodes('./task/optNames/unit');
            if (loNode == null || loNode.length == 0) {
              lsErrorMsg = '环节属性存在问题：意见立场不能为空！';
            }
          }

          if (lsErrorMsg != '') {
            showMsg(lsErrorMsg, true);
            return false;
          }
        }
        if (
          goWorkFlow.tasks[i].xmlObject == null ||
          lsName == null ||
          lsName == '' ||
          lsAllTaskNames.indexOf(';' + lsName + ';') != -1 ||
          lsID == null ||
          lsID == '' ||
          lsAllTaskIDs.indexOf(';' + lsID + ';') != -1
        ) {
          goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.tasks[i];
          if (lsErrorMsg == '') {
            lsErrorMsg = sGetLang('FLOW_WF_TASKPROPERTYERROR');
          }
        } else {
          if (lbFree == false && goWorkFlow.tasks[i].outLines.length < 1) {
            goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.tasks[i];
            if (lsErrorMsg == '') {
              lsErrorMsg = sGetLang('FLOW_WF_OBJECTOUTLINEISEMPTY');
            }
          } else {
            var loObject = oGetConditionObjectByFromTaskName(goWorkFlow.tasks[i].xmlObject.getAttribute('name'));
            if (loObject != null && goWorkFlow.tasks[i].outLines.length > 1) {
              goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.tasks[i];
              if (lsErrorMsg == '') {
                lsErrorMsg = sGetLang('FLOW_WF_TASKMOREOUTLINEFORCD');
              }
            } else {
              lsAllTaskNames += lsName + ';';
              lsAllTaskIDs += lsID + ';';
            }
          }
        }
        break;
      case 'SUBFLOW':
        var lsName = null,
          lsID = null,
          lbErrorForSetFlow = false,
          lbFree = false;
        if (goWorkFlow.tasks[i].xmlObject != null) {
          lsName = goWorkFlow.tasks[i].xmlObject.getAttribute('name');
          lsID = goWorkFlow.tasks[i].xmlObject.getAttribute('id');
          var loNode = goWorkFlow.tasks[i].xmlObject.selectSingleNode('isSetFlow');
          if (loNode != null && loNode.text() == '1') {
            loNode = goWorkFlow.tasks[i].xmlObject.selectSingleNode('/task/newFlows/unit');
            if (loNode == null) {
              lbErrorForSetFlow = true;
            }
          }
        }
        if (
          goWorkFlow.tasks[i].xmlObject == null ||
          lsName == null ||
          lsName == '' ||
          lsAllTaskNames.indexOf(';' + lsName + ';') != -1 ||
          lsID == null ||
          lsID == '' ||
          lsAllTaskIDs.indexOf(';' + lsID + ';') != -1 ||
          lbErrorForSetFlow == true
        ) {
          goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.tasks[i];
          if (lsErrorMsg == '') {
            lsErrorMsg = sGetLang('FLOW_WF_SUBFLOWPROPERTYERROR');
          }
        } else {
          if (lbFree == false && goWorkFlow.tasks[i].outLines.length < 1) {
            goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.tasks[i];
            if (lsErrorMsg == '') {
              lsErrorMsg = sGetLang('FLOW_WF_OBJECTOUTLINEISEMPTY');
            }
          } else {
            var loObject = oGetConditionObjectByFromTaskName(goWorkFlow.tasks[i].xmlObject.getAttribute('name'));
            if (loObject != null && goWorkFlow.tasks[i].outLines.length > 1) {
              goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.tasks[i];
              if (lsErrorMsg == '') {
                lsErrorMsg = sGetLang('FLOW_WF_SUBFLOWMOREOUTLINEFORCD');
              }
            } else {
              lsAllTaskNames += lsName + ';';
              lsAllTaskIDs += lsID + ';';
            }
          }
        }
        break;
      case 'CONDITION':
        var lsName = null,
          lbFree = false;
        var loNode = loXML.selectSingleNode('./property/isFree');
        if (loNode != null && loNode.text() == '1') {
          lbFree = true;
        }
        if (goWorkFlow.tasks[i].xmlObject != null) {
          lsName = goWorkFlow.tasks[i].xmlObject.getAttribute('name');
        }
        if (
          goWorkFlow.tasks[i].xmlObject == null ||
          lsName == null ||
          lsName == '' ||
          goWorkFlow.tasks[i].inLines.length != 1 ||
          (goWorkFlow.tasks[i].inLines[0].fromTask.Type != 'TASK' && goWorkFlow.tasks[i].inLines[0].fromTask.Type != 'SUBFLOW')
        ) {
          goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.tasks[i];
          if (lsErrorMsg == '') {
            lsErrorMsg = sGetLang('FLOW_WF_CONDICTIONERROR');
          }
        } else {
          if (lbFree == false && goWorkFlow.tasks[i].outLines.length < 1) {
            goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.tasks[i];
            if (lsErrorMsg == '') {
              lsErrorMsg = sGetLang('FLOW_WF_OBJECTOUTLINEISEMPTY');
            }
          }
        }
        break;
    }
  }
  for (var i = 0; i < goWorkFlow.lines.length; i++) {
    if (goWorkFlow.lines[i] == null) {
      continue;
    }
    setErrorLineStatus(goWorkFlow.lines[i], false);
    if (
      goWorkFlow.lines[i].toTask.Type != 'CONDITION' &&
      (goWorkFlow.lines[i].xmlObject == null ||
        goWorkFlow.lines[i].xmlObject.getAttribute('name') == null ||
        goWorkFlow.lines[i].xmlObject.getAttribute('name') == '')
    ) {
      goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.lines[i];
      if (lsErrorMsg == '') {
        lsErrorMsg = sGetLang('FLOW_WF_LINEPROPERTYERROR');
      }
    } else {
      if (
        goWorkFlow.lines[i].fromTask.Type == 'BEGIN' &&
        (goWorkFlow.lines[i].toTask.Type == 'END' ||
          goWorkFlow.lines[i].toTask.Type == 'SUBFLOW' ||
          goWorkFlow.lines[i].toTask.Type == 'CONDITION')
      ) {
        goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.lines[i];
        if (lsErrorMsg == '') {
          lsErrorMsg = sGetLang('FLOW_WF_LINETOTASKERRORFORBEGIN');
        }
      } else {
        if (goWorkFlow.lines[i].xmlObject != null && goWorkFlow.lines[i].xmlObject.getAttribute('name') != null) {
          lsAllLineNames += goWorkFlow.lines[i].xmlObject.getAttribute('name') + ';';
        }
      }
    }
    if (
      goWorkFlow.lines[i].xmlObject &&
      goWorkFlow.lines[i].xmlObject.selectSingleNode('useAsButton') &&
      goWorkFlow.lines[i].xmlObject.selectSingleNode('useAsButton').text() === '1'
    ) {
      var buttonOrder = goWorkFlow.lines[i].xmlObject.selectSingleNode('buttonOrder').text();
      var buttonClassName = goWorkFlow.lines[i].xmlObject.selectSingleNode('buttonClassName').text();
      if (buttonOrder == '' || buttonOrder == null) {
        goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.lines[i];
        setErrorLineStatus(goWorkFlow.lines[i], true);
        if (lsErrorMsg == '') {
          lsErrorMsg = sGetLang('FLOW_WF_LINEBTNORDEREMPTY');
        }
      }
      if (buttonClassName == '' || buttonClassName == null) {
        goWorkFlow.selectObjects[goWorkFlow.selectObjects.length] = goWorkFlow.lines[i];
        if (lsErrorMsg == '') {
          lsErrorMsg = sGetLang('FLOW_WF_LINEBTNCLASSNAMEEMPTY');
        }
      }
    }
  }
  if (loBegin == null) {
    showMsg(parent.sGetLang('FLOW_WF_NOBEGIN'), true);
    return false;
  }
  liCount = 0;
  for (var i = 0; i < loBegin.outLines.length; i++) {
    if (loBegin.outLines[i] != null) {
      liCount++;
      if (liCount > 1) {
        showMsg(parent.sGetLang('FLOW_WF_BEGINOUTLINEISMORE'), true);
        return false;
      }
    }
  }
  if (liCount == 0) {
    bShowErrorObject(true);
    showMsg(parent.sGetLang('FLOW_WF_NOBEGINOUTLINE'), true);
    return false;
  }
  for (var i = 0; i < loBegin.inLines.length; i++) {
    if (loBegin.inLines[i] != null) {
      showMsg(parent.sGetLang('FLOW_WF_BEGINLINEERROR'), true);
      return false;
    }
  }
  if (goWorkFlow.selectObjects.length > 0) {
    bShowErrorObject(true);
    if (lsErrorMsg == '') {
      showMsg(parent.sGetLang('FLOW_WF_SETTINGERROR'), true);
    } else {
      showMsg(lsErrorMsg, true);
    }
    return false;
  } else {
    showMsg(parent.sGetLang('FLOW_WF_SETTINGTRUE'));
  }
  return true;
}

function bShowErrorObject(pbShow) {
  var lbSelect = pbShow == true ? -1 : false;
  for (var i = 0; i < goWorkFlow.selectObjects.length; i++) {
    bSetObjectStatus(null, lbSelect, goWorkFlow.selectObjects[i]);
  }
  if (pbShow != true && goWorkFlow.selectObjects.length > 0) {
    goWorkFlow.selectObjects = new Array();
  }
  return true;
}

function bLoadObject(flowInstUuid) {
  goWorkFlow.curStatus = 'load';
  var loNode = goWorkFlow.flowXML.selectSingleNode('./property/categorySN');
  loNode = goWorkFlow.dictionXML.selectSingleNode("/diction/categorys/category[code='" + loNode.text() + "']");
  var lsCategory = '';
  if (loNode != null) {
    lsCategory = loNode.selectSingleNode('name').text();
  }
  var title = goWorkFlow.flowXML.getAttribute('name') + '（' + goWorkFlow.flowXML.getAttribute('version') + '）';
  if (onlyGraphics == undefined) {
    parent.document.title = title;
  }
  $('.work-flow-title', parent.document).text(title);
  var laNode = goWorkFlow.flowXML.selectNodes('./tasks/task');
  if (laNode != null) {
    laNode.each(function (index) {
      var taskNode = $(this);
      var loObject = oCreateTask(
        taskNode.getAttribute('type') == '1' ? 'TASK' : 'SUBFLOW',
        taskNode.selectSingleNode('X').text() - 0,
        taskNode.selectSingleNode('Y').text() - 0
      );
      bAdjustObject(loObject);
      loObject.xmlObject = taskNode;
      bSetObjectText(loObject);
      // 计时图标切换
      switchTaskImageWithTimerIfRequried(loObject);
      // 根据流程办理信息显示图标
      switchLoObjectImageWithFlowHandingInfoIfRequried(loObject, flowInstUuid);
      var lsCName = taskNode.selectSingleNode('conditionName') != null ? taskNode.selectSingleNode('conditionName').text() : '';
      var lsCLine = taskNode.selectSingleNode('conditionLine') != null ? taskNode.selectSingleNode('conditionLine').text() : '';
      if (lsCName != '' && lsCLine != '') {
        var loFromTask = loObject;
        var loObject = oCreateTask(
          'CONDITION',
          taskNode.selectSingleNode('conditionX').text() - 0,
          taskNode.selectSingleNode('conditionY').text() - 0
        );
        bAdjustObject(loObject);
        var loNode = oCreateXMLNode(loObject);
        loNode.setAttribute('name', lsCName);
        loNode.text(taskNode.selectSingleNode('conditionBody') != null ? taskNode.selectSingleNode('conditionBody').text() : '');
        bSetObjectText(loObject);
        var loToTask = loObject;
        if (loFromTask != null && loToTask != null) {
          if (lsCLine.indexOf('BEELINE') == 0) {
            var loObject = oCreateBeeline(loFromTask, loToTask);
          } else {
            var laTemp = lsCLine.split(';');
            loFromTask.cursorWay = laTemp[1];
            loToTask.cursorWay = laTemp[2];
            var loObject = oCreateCurve(loFromTask, loToTask);
          }
        }
      }
    });
  }
  var laNode = goWorkFlow.flowXML.selectNodes('./directions/direction');
  if (laNode != null) {
    laNode.each(function (index) {
      var directionNode = $(this);
      var lsSource = directionNode.getAttribute('fromID');
      var loStartEndObject = null;
      if (lsSource == '<StartFlow>') {
        var loObject = oCreateTask(
          directionNode.selectSingleNode('terminalType').text(),
          directionNode.selectSingleNode('terminalX').text() - 0,
          directionNode.selectSingleNode('terminalY').text() - 0
        );
        bAdjustObject(loObject);
        var loNode = oCreateXMLNode(loObject);
        loNode.setAttribute('name', directionNode.selectSingleNode('terminalName').text());
        bSetObjectText(loObject);
        var loFromTask = loObject;
        loStartEndObject = loObject;
      } else {
        if (directionNode.getAttribute('type') == '2') {
          loTask = oGetTaskObjectByPropertyID(lsSource);
          var loFromTask = oGetConditionObjectByFromTask(loTask);
        } else {
          var loFromTask = oGetTaskObjectByPropertyID(lsSource);
        }
      }
      var lsTarget = directionNode.getAttribute('toID');
      if (lsTarget == '<EndFlow>') {
        var loObject = oCreateTask(
          directionNode.selectSingleNode('terminalType').text(),
          directionNode.selectSingleNode('terminalX').text() - 0,
          directionNode.selectSingleNode('terminalY').text() - 0
        );
        bAdjustObject(loObject);
        var loNode = oCreateXMLNode(loObject);
        loNode.setAttribute('name', directionNode.selectSingleNode('terminalName').text());
        bSetObjectText(loObject);
        var loToTask = loObject;
        loStartEndObject = loObject;
      } else {
        var loToTask = oGetTaskObjectByPropertyID(lsTarget);
      }
      if (loFromTask != null && loToTask != null) {
        var lsLine = directionNode.selectSingleNode('line').text();
        if (lsLine.indexOf('BEELINE') == 0) {
          var loObject = oCreateBeeline(loFromTask, loToTask);
        } else {
          var laTemp = lsLine.split(';');
          loFromTask.cursorWay = laTemp[1];
          loToTask.cursorWay = laTemp[2];
          var loObject = oCreateCurve(loFromTask, loToTask);
        }
        if (directionNode.selectSingleNode('isShowName').text() == '0') {
          loObject.isShowName = false;
        } else {
          loObject.isShowName = true;
        }
        loObject.xmlObject = directionNode;
        bSetObjectText(loObject.labelObject);
        var laTemp = directionNode.selectSingleNode('lineLabel').text().split(';');
        loObject.labelObject.imgObject.moveTo(laTemp[0] - 0, laTemp[1] - 0);
      }
      if (lsSource == '<StartFlow>' || lsTarget == '<EndFlow>') {
        // 根据流程办理信息显示图标
        switchLoObjectImageWithFlowHandingInfoIfRequried(loStartEndObject, flowInstUuid);
      }
    });
  }
  // 所有结点处理结束，处理判断点图标
  switchAllConditionImageWithFlowHandingInfoIfRequried(goWorkFlow.tasks, flowInstUuid);
  var laNode = goWorkFlow.flowXML.selectNodes('./labels/label');
  if (laNode != null) {
    laNode.each(function (index) {
      var labelNode = $(this);
      var loObject = oCreateTask('LABEL', labelNode.selectSingleNode('X').text() - 0, labelNode.selectSingleNode('Y').text() - 0);
      loObject.imgObject.resizeTo(labelNode.selectSingleNode('W').text() - 0, labelNode.selectSingleNode('H').text() - 0);
      loObject.xmlObject = labelNode;
      bSetObjectText(loObject);
    });
  }
  showMsg(lsCategory + '/' + goWorkFlow.flowXML.getAttribute('name') + sGetLang('FLOW_WF_LOADFINISH'));
  goWorkFlow.curStatus = null;
  return true;
}
// 计时环节切换为计时图片
function switchTaskImageWithTimerIfRequried(loObject) {
  // 计时图标暂不处理
  // TODO
  if (true) {
    return;
  }
  var taskNode = loObject.xmlObject;
  if (!taskNode || taskNode.getAttribute('type') != 1) {
    return;
  }
  var loImg = loObject.imgObject;
  var lsName = unescape(loImg.src);
  lsName = lsName.substr(0, lsName.indexOf('.gif'));
  var isTimerImg = lsName.indexOf('_timer') != -1;
  if (isTimerTaskNode(taskNode.getAttribute('id'))) {
    // 计时环节时，切换为计时图片
    if (!isTimerImg) {
      loImg.swapImage(lsName + '_timer.gif');
    }
  } else if (isTimerImg) {
    // 非计时环节时，切换为非计时图片
    if (isTimerImg) {
      lsName = lsName.substr(0, lsName.indexOf('_timer'));
      loImg.swapImage(lsName + '.gif');
    }
  }
}

function switchTaskWithTimerIfRequried(loObject, tasks) {
  // return true;
  var taskId = loObject.xmlObject.attr('id');
  var $taskHtml = $(loObject.htmlObject);
  if (tasks[taskId].isOverDue) {
    $taskHtml.find('.flow-control-text-wrap').append('<div class="task-timer isOverDue"></div>');
  } else if (tasks[taskId].isAlarm) {
    $taskHtml.find('.flow-control-text-wrap').append('<div class="task-timer isAlarm"></div>');
  } else if (tasks[taskId].isTimming) {
    $taskHtml.find('.flow-control-text-wrap').append('<div class="task-timer isTimming"></div>');
  } else {
    if (isTimer(taskId)) {
      if (tasks[taskId].state === 'done') {
        $taskHtml.find('.flow-control-text-wrap').append('<div class="task-timer isTimming"></div>');
      } else {
        $taskHtml.find('.flow-control-text-wrap').append('<div class="task-timer timeDefault"></div>');
      }
    }
  }
}

function getAllTimerId() {
  var tasks = goWorkFlow.flowXML.selectNodes('./timers/timer/tasks/unit') || [];
  var tasksTimerIds = $.map(tasks, function (item) {
    return item.textContent;
  });
  var subflows = goWorkFlow.flowXML.selectNodes('./timers/timer/subTasks/subTask') || [];
  var subflowsTimerIds = $.map(subflows, function (item) {
    return $(item).selectSingleNode('taskId').text();
  });
  goWorkFlow.timers = tasksTimerIds.concat(subflowsTimerIds);
}

function isTimer(id) {
  if (!goWorkFlow.timers) {
    getAllTimerId();
  }
  return goWorkFlow.timers.indexOf(id) > -1 ? true : false;
}

// 查看流程图切换为流程办理状态图
function switchLoObjectImageWithFlowHandingInfoIfRequried(loObject, flowInstUuid) {
  if (loObject == null || flowInstUuid == null || $.trim(flowInstUuid) == '') {
    return;
  }
  var flowHandingInfo = getFlowHandingInfo(flowInstUuid);
  var loType = loObject.Type;
  switch (loType) {
    case 'BEGIN':
      switchBeginImageWithFlowHandingInfoIfRequried(loObject, flowHandingInfo.start);
      break;
    case 'TASK':
      switchTaskImageWithFlowHandingInfoIfRequried(loObject, flowHandingInfo.tasks);
      switchTaskWithTimerIfRequried(loObject, flowHandingInfo.tasks);
      break;
    case 'SUBFLOW':
      switchSubflowImageWithFlowHandingInfoIfRequried(loObject, flowHandingInfo.subflows);
      switchTaskWithTimerIfRequried(loObject, flowHandingInfo.subflows);
      break;
    case 'CONDITION':
      console.log('switchLoObjectImageWithFlowHandingInfoIfRequried not support for CONDITION');
      break;
    case 'END':
      switchEndImageWithFlowHandingInfoIfRequried(loObject, flowHandingInfo.ends);
      break;
  }
}
// 查看流程图切换开始结点办理状态
function switchBeginImageWithFlowHandingInfoIfRequried(loObject, start) {
  // 流程已开始
  if (start && start.started) {
    swapFlowHandingInfoImageByName(loObject, 'wl_begin.gif');
  } else {
    // 流程未开始
    swapFlowHandingInfoImageByName(loObject, 'wl_begin_.gif');
  }
}
var taskImgNames = {
  undo: 'wl_task_',
  todo: 'wl_task',
  done: 'wl_task'
};
// 查看流程图切换环节结点办理状态
function switchTaskImageWithFlowHandingInfoIfRequried(loObject, tasks) {
  var taskNode = loObject.xmlObject;
  var taskId = taskNode.getAttribute('id');
  var taskHandingInfo = tasks[taskId];
  if (taskHandingInfo == null) {
    return;
  }
  var imgName = '';
  var lodiv = loObject.htmlObject;
  // 已办
  if (taskHandingInfo.state == 'done') {
    imgName = getTaskImgName('done');
    $(lodiv).addClass('task-done');
    setTimeout(function () {
      changeLineDone(loObject.inLines, taskHandingInfo.preTaskIds);
    }, 0);
  } else if (taskHandingInfo.state == 'todo') {
    // 待办
    imgName = getTaskImgName('todo');
    setTimeout(function () {
      changeLineDone(loObject.inLines, taskHandingInfo.preTaskIds);
    }, 0);
    // 添加待办样式
    $(lodiv).addClass('task-todo').children('img').addClass('task-todo');
  } else {
    // 未办
    imgName = getTaskImgName('undo');
    $(lodiv).addClass('task-undo');
  }
  // 切换图片
  swapFlowHandingInfoImageByName(loObject, imgName + '.gif');
}

function changeLineDone(inLines, preTaskIds) {
  $.each(inLines, function (i, item) {
    var fromTask = item.fromTask;
    if ($(fromTask.htmlObject).hasClass('task-done') || fromTask.Type === 'BEGIN') {
      setLineDoneColor(item);
    }
    if (fromTask.Type === 'CONDITION') {
      var CONDITION_fromTask = fromTask.inLines[0].fromTask;
      var CONDITION_fromTask_id = CONDITION_fromTask.xmlObject.attr('id');
      if ($(CONDITION_fromTask.htmlObject).hasClass('task-done')) {
        changeLineDone(fromTask.inLines);
      }
      if (preTaskIds.indexOf(CONDITION_fromTask_id) > -1) {
        setLineDoneColor(item);
      }
    }
  });
}

function setLineDoneColor(item) {
  $(item.htmlObject).addClass('done').find('div').css('background-color', '#4BB633');
}

function getTaskImgName(state) {
  return taskImgNames[state];
}

function isTaskUndoByTaskId(taskId, tasks) {
  var taskHandingInfo = tasks[taskId];
  if (taskHandingInfo == null) {
    return true;
  }
  return taskHandingInfo.state != 'done';
}
// 查看流程图切换子流程结点办理状态
function switchSubflowImageWithFlowHandingInfoIfRequried(loObject, subflows) {
  switchTaskImageWithFlowHandingInfoIfRequried(loObject, subflows);
}
// 查看流程图切换所有条件结点办理状态
function switchAllConditionImageWithFlowHandingInfoIfRequried(tasks, flowInstUuid) {
  if (tasks == null || flowInstUuid == null || $.trim(flowInstUuid) == '') {
    return;
  }
  var flowHandingInfo = getFlowHandingInfo(flowInstUuid);
  $.each(tasks, function (i, loObject) {
    if (loObject.Type == 'CONDITION') {
      switchConditionImageWithFlowHandingInfoIfRequried(loObject, flowHandingInfo.conditions, flowHandingInfo);
    }
  });
}

function switchConditionImageWithFlowHandingInfoIfRequried(loObject, conditions, flowHandingInfo) {
  var inLines = loObject.inLines;
  var outLines = loObject.outLines;
  var pathStates = [];
  var isHandle = false;
  // 提取路径完成状态信息
  for (var i = 0; i < inLines.length; i++) {
    if (isHandle) {
      break;
    }
    var inLine = inLines[i];
    var fromId = getTaskId(inLine.fromTask);
    var fromTaskUndo = isTaskUndoByTaskId(fromId, flowHandingInfo.tasks);
    for (var j = 0; j < outLines.length; j++) {
      var outLine = outLines[j];
      var toId = getTaskId(outLine.toTask);
      var completed = !(fromTaskUndo && isTaskUndoByTaskId(toId, flowHandingInfo.tasks));
      // 标记完成状态
      if (completed) {
        // 条件已流转
        swapFlowHandingInfoImageByName(loObject, 'wl_condition.gif');
        isHandle = true;
        break;
      }
    }
  }
  // 条件未流转
  if (!isHandle) {
    swapFlowHandingInfoImageByName(loObject, 'wl_condition_.gif');
  }
}
// 查看流程图切换结束结点办理状态
function switchEndImageWithFlowHandingInfoIfRequried(loObject, ends) {
  var fromId = getFromTaskId(loObject);
  var lodiv = loObject.htmlObject;
  for (var i = 0; i < ends.length; i++) {
    var end = ends[i];
    if (end.fromId == fromId && end.completed) {
      // 流程已结束
      swapFlowHandingInfoImageByName(loObject, 'wl_end.gif');
      return;
    }
  }
  // 流程未结束
  $(lodiv).addClass('undo');
  swapFlowHandingInfoImageByName(loObject, 'wl_end_.gif');
}

function getFromTaskId(loObject) {
  var inLines = loObject.inLines;
  if (inLines != null) {
    for (var i = 0; i < inLines.length; i++) {
      var inLine = inLines[i];
      if (inLine.fromTask && inLine.fromTask) {
        return getTaskId(inLine.fromTask);
      }
    }
  }
  return null;
}

function getTaskId(loObject) {
  return $(loObject.xmlObject).getAttribute('id');
}

function swapFlowHandingInfoImageByName(loObject, imgName) {
  var loImg = loObject.imgObject;
  var lsName = unescape(loImg.src);
  lsName = lsName.substring(0, lsName.lastIndexOf('/') + 1);
  loImg.swapImage(lsName + imgName);
  // 添加流程办理信息样式
  var loImgDiv = loObject.htmlObject;
  $(loImgDiv).addClass('flow-handing-info');
  $(loImgDiv).attr('imgName', imgName);
}

function getFlowHandingInfo(flowInstUuid) {
  if (goWorkFlow.flowHandingInfo) {
    return goWorkFlow.flowHandingInfo;
  }
  var flowHandingInfo = {};
  $.ajax({
    type: 'get',
    url: '/api/workflow/definition/getFlowHandingStateInfo',
    data: {
      flowInstUuid: flowInstUuid
    },
    async: false,
    success: function (result) {
      flowHandingInfo = result.data;
    }
  });
  goWorkFlow.flowHandingInfo = flowHandingInfo;
  return flowHandingInfo;
}
// 判断是否为计时环节
function isTimerTaskNode(taskId) {
  var timers = goWorkFlow.flowXML.selectNodes('./timers/timer');
  if (timers == null || timers.length == 0) {
    return false;
  }
  for (var i = 0; i < timers.length; i++) {
    var timer = timers[i];
    var timerTasks = $(timer).selectNodes('tasks/unit');
    if (timerTasks != null) {
      for (var j = 0; j < timerTasks.length; j++) {
        if ($(timerTasks[j]).text() == taskId) {
          return true;
        }
      }
    }
  }
  return false;
}
// 根据计时器配置，更新所有环节相应的图标
var updateAllTaskImageWithTimers = function () {
  var loObjects = goWorkFlow.tasks;
  $.each(loObjects, function (i, loObject) {
    if (loObject.Type == 'TASK') {
      switchTaskImageWithTimerIfRequried(loObject);
    }
  });
};
goWorkFlow.updateAllTaskImageWithTimers = updateAllTaskImageWithTimers;

function bGetEQFlowXML() {
  var lsID = null;
  if (goWorkFlow.equalFlowID != null && goWorkFlow.equalFlowID != '') {
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
  if (lsID == null || lsID == '') {
    return false;
  }
  if (goWorkFlow.equalFlowXML == null || lsID != $(goWorkFlow.equalFlowXML).getAttribute('id')) {
    $.ajax({
      url: 'scheme/flow/xml/id?open&id=' + lsID,
      type: 'GET',
      dataType: 'xml',
      cache: false,
      async: false,
      error: function (data) { },
      success: function (data) {
        // flowXML转化为jQuery的XML对象
        goWorkFlow.equalFlowXML = $(data).selectSingleNode('flow');
      }
    });
    // var xmlhttp = oGetXmlHttpRequest_();
    // if (xmlhttp == null) {
    // return false;
    // }
    // xmlhttp.open("GET", "scheme/flow/xml.action?open&id=" + lsID + "&t="
    // + Math.random(), false);
    // xmlhttp.send("");
    // goWorkFlow.equalFlowXML =
    // xmlhttp.responseXML.selectSingleNode("flow");
    // delete xmlhttp;
    // xmlhttp = null;
  }
  if (goWorkFlow.equalFlowXML == null) {
    return false;
  }
  return true;
}

function aGetTasks(psTypes, psCurTaskID, pbEnd) {
  var laTask = new Array();
  var lsTypes = psTypes != null && psTypes != '' ? psTypes : 'TASK';
  var lsID = psCurTaskID != null ? psCurTaskID : '';
  if (bGetEQFlowXML() == true) {
    var laNode = goWorkFlow.equalFlowXML.selectNodes('./tasks/task');
    if (laNode != null) {
      for (var i = 0; i < laNode.length; i++) {
        var lsType = laNode[i].getAttribute('type') == '1' ? 'TASK' : 'SUBFLOW';
        if (lsTypes.indexOf(lsType) == -1) {
          continue;
        }
        if (laNode[i].getAttribute('name') == null || laNode[i].getAttribute('name') == '') {
          continue;
        }
        if (laNode[i].getAttribute('id') == null || laNode[i].getAttribute('id') == '') {
          continue;
        }
        if (lsID == laNode[i].getAttribute('id')) {
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
      if (lsTypes.indexOf(goWorkFlow.tasks[i].Type) == -1) {
        continue;
      }
      if (goWorkFlow.tasks[i].xmlObject.getAttribute('name') == null || goWorkFlow.tasks[i].xmlObject.getAttribute('name') == '') {
        continue;
      }
      if (goWorkFlow.tasks[i].xmlObject.getAttribute('id') == null || goWorkFlow.tasks[i].xmlObject.getAttribute('id') == '') {
        continue;
      }
      if (lsID == goWorkFlow.tasks[i].xmlObject.getAttribute('id')) {
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

function aGetDirections() {
  var laDirection = new Array();
  var lsSource, lsTarget;
  if (bGetEQFlowXML() == true) {
    var laNode = goWorkFlow.equalFlowXML.selectNodes('./directions/direction');
    if (laNode != null) {
      for (var i = 0; i < laNode.length; i++) {
        lsSource = null;
        lsTarget = null;
        if (laNode[i].getAttribute('fromID') == '<StartFlow>') {
          lsSource = sGetLang('FLOW_WF_BEGINTASKNAME') + '|<StartFlow>';
        } else {
          var loNode = goWorkFlow.equalFlowXML.selectSingleNode("./tasks/task[@id='" + laNode[i].getAttribute('fromID') + "']");
          if (loNode != null) {
            lsSource = loNode.getAttribute('name') + '|' + loNode.getAttribute('id');
          }
        }
        if (laNode[i].getAttribute('toID') == '<EndFlow>') {
          lsTarget = sGetLang('FLOW_WF_ENDTASKNAME') + '|<EndFlow>';
        } else {
          var loNode = goWorkFlow.equalFlowXML.selectSingleNode("./tasks/task[@id='" + laNode[i].getAttribute('toID') + "']");
          if (loNode != null) {
            lsTarget = loNode.getAttribute('name') + '|' + loNode.getAttribute('id');
          }
        }
        if (lsSource != null && lsSource != '' && lsTarget != null && lsTarget != '') {
          laDirection.push(laNode[i].getAttribute('name') + '|' + laNode[i].getAttribute('id') + '|' + lsSource + '|' + lsTarget);
        }
      }
    }
  } else {
    for (var i = 0; i < goWorkFlow.lines.length; i++) {
      lsSource = null;
      lsTarget = null;
      if (
        goWorkFlow.lines[i] == null ||
        goWorkFlow.lines[i].xmlObject == null ||
        goWorkFlow.lines[i].xmlObject.getAttribute('name') == null ||
        goWorkFlow.lines[i].toTask.Type == 'CONDITION'
      ) {
        continue;
      }
      if (goWorkFlow.lines[i].fromTask.Type == 'BEGIN') {
        lsSource = sGetLang('FLOW_WF_BEGINTASKNAME') + '|<StartFlow>';
      } else {
        if (goWorkFlow.lines[i].fromTask.Type == 'CONDITION') {
          var loLine = goWorkFlow.lines[i].fromTask.inLines[0];
          if (loLine == null) {
            continue;
          }
          if (loLine.fromTask.xmlObject != null && loLine.fromTask.xmlObject.getAttribute('id') != null) {
            lsSource = loLine.fromTask.xmlObject.getAttribute('name') + '|' + loLine.fromTask.xmlObject.getAttribute('id');
          }
        } else {
          if (goWorkFlow.lines[i].fromTask.xmlObject != null && goWorkFlow.lines[i].fromTask.xmlObject.getAttribute('id') != null) {
            lsSource =
              goWorkFlow.lines[i].fromTask.xmlObject.getAttribute('name') + '|' + goWorkFlow.lines[i].fromTask.xmlObject.getAttribute('id');
          }
        }
      }
      if (goWorkFlow.lines[i].toTask.Type == 'END') {
        lsTarget = sGetLang('FLOW_WF_ENDTASKNAME') + '|<EndFlow>';
      } else {
        if (goWorkFlow.lines[i].toTask.xmlObject != null && goWorkFlow.lines[i].toTask.xmlObject.getAttribute('id') != null) {
          lsTarget =
            goWorkFlow.lines[i].toTask.xmlObject.getAttribute('name') + '|' + goWorkFlow.lines[i].toTask.xmlObject.getAttribute('id');
        }
      }
      if (lsSource != null && lsSource != '' && lsTarget != null && lsTarget != '') {
        laDirection.push(
          goWorkFlow.lines[i].xmlObject.getAttribute('name') +
          '|' +
          goWorkFlow.lines[i].xmlObject.getAttribute('id') +
          '|' +
          lsSource +
          '|' +
          lsTarget
        );
      }
    }
  }
  return laDirection;
}

function setFlowProperty() {
  $('.direct-tab', parent.document).hide();
  $('.task-tab', parent.document).hide();
  $('.subflow-tab', parent.document).hide();
  $('.condition-tab', parent.document).hide();
  top.switchPropertyTab('workflowProperties');
  goWorkFlow.curObjectName = null;
}

function setTaskProperty() {
  var loTask = oGetTaskObject(goWorkFlow.curObjectName);
  if (loTask == null) {
    return false;
  }
  if (loTask.Type === 'BEGIN' || loTask.Type === 'END') {
    return;
  }
  goWorkFlow.curTaskObj = loTask;
  $('.direct-tab', parent.document).hide();
  if (loTask.Type === 'TASK') {
    $('.task-tab', parent.document).show();
    $('.subflow-tab', parent.document).hide();
    $('.condition-tab', parent.document).hide();
    top.switchPropertyTab('nodeProperties');
    var Frame = parent.document.getElementById('nodeFrame');
  } else if (loTask.Type === 'SUBFLOW') {
    goWorkFlow.curSubflow = loTask;
    $('.task-tab', parent.document).hide();
    $('.subflow-tab', parent.document).show();
    $('.condition-tab', parent.document).hide();
    top.switchPropertyTab('subFlowProperties');
    var Frame = parent.document.getElementById('subFlowFrame');
  } else if (loTask.Type === 'CONDITION') {
    $('.task-tab', parent.document).hide();
    $('.subflow-tab', parent.document).hide();
    $('.condition-tab', parent.document).show();
    top.switchPropertyTab('conditionProperties');
    var Frame = parent.document.getElementById('conditionFrame');
  }
  if (!Frame.src) {
    Frame.setAttribute('curObject', goWorkFlow.curObjectName);
    Frame.src = $(Frame).attr('data-src');
  } else {
    var curTask = Frame.getAttribute('curObject');
    if (curTask !== goWorkFlow.curObjectName) {
      Frame.setAttribute('curObject', goWorkFlow.curObjectName);
      Frame.contentWindow.location.reload();
    } else {
      top.appModal.hideMask();
    }
  }
}

function setDirectionProperty() {
  var loLine = oGetLineObject(goWorkFlow.curObjectName);
  if (loLine == null || loLine.xmlObject == null) {
    return false;
  }
  $('.task-tab', parent.document).hide();
  $('.subflow-tab', parent.document).hide();
  $('.direct-tab', parent.document).show();
  goWorkFlow.currloLine = loLine;
  top.switchPropertyTab('flowDirectProperties');
  var Frame = parent.document.getElementById('directionFrame');
  if (!Frame.src) {
    Frame.setAttribute('curObject', goWorkFlow.curObjectName);
    Frame.src = $(Frame).attr('data-src');
  } else {
    var curTask = Frame.getAttribute('curObject');
    if (curTask !== goWorkFlow.curObjectName) {
      Frame.setAttribute('curObject', goWorkFlow.curObjectName);
      Frame.contentWindow.location.reload();
    } else {
      top.appModal.hideMask();
    }
  }
}

function showTasks(psArrow) {
  var loObject = oGetTaskObject(goWorkFlow.curObjectName);
  if (loObject != null) {
    bShowTasks(loObject, true, psArrow == 'before' ? true : false, goWorkFlow.curObjectName);
  }
}

function bSetDirection(psType) {
  var loObject = oGetLineObject(goWorkFlow.curObjectName);
  if (loObject == null) {
    return;
  }
  switch (psType) {
    case 'arrow':
      var loFrom = loObject.fromTask;
      var loTo = loObject.toTask;
      if (loObject.Type == 'CURVE') {
        bDeleteWayElement(loFrom, loObject.fromWay + 'Curves', loObject.name);
        bDeleteWayElement(loTo, loObject.toWay + 'Curves', loObject.name);
        var lsWay = loObject.fromWay;
        loObject.fromWay = loObject.toWay;
        loObject.toWay = lsWay;
        // eval("loTo." + loObject.fromWay + "Curves[loTo." +
        // loObject.fromWay
        // + "Curves.length]=loObject;");
        // evalLoTo2(loTo, loObject);
        setPoObject(loTo, loObject.fromWay, loObject);
        // eval("loFrom." + loObject.toWay + "Curves[loFrom." +
        // loObject.toWay
        // + "Curves.length]=loObject;");
        // evalLoFrom2(loFrom, loObject);
        setPoObject(loFrom, loObject.toWay, loObject);
      }
      bDeleteWayElement(loFrom, 'outLines', loObject.name);
      bDeleteWayElement(loTo, 'inLines', loObject.name);
      loObject.fromTask = loTo;
      loObject.toTask = loFrom;
      loObject.fromTask.outLines[loObject.fromTask.outLines.length] = loObject;
      loObject.toTask.inLines[loObject.toTask.inLines.length] = loObject;
      if (loObject.Type == 'CURVE') {
        oCreateCurve(loObject.fromTask, loObject.toTask, null, loObject);
      } else {
        oCreateBeeline(loObject.fromTask, loObject.toTask, null, loObject);
      }
      break;
    case 'beeline':
      oCreateBeeline(loObject.fromTask, loObject.toTask, null, loObject);
      break;
    case 'curve':
      oCreateCurve(loObject.fromTask, loObject.toTask, null, loObject);
      break;
  }
}

function bDealObject(psAction) {
  if (psAction === 'cut' || psAction === 'delete') {
    var curTab = getCurrentTab();
    if (curTab !== 'workflowProperties') {
      if (curTab === 'flowDirectProperties') {
        if (goWorkFlow.currloLine.name === goWorkFlow.curObjectName) {
          setFlowProperty();
        }
      } else {
        if (goWorkFlow.curTaskObj.name === goWorkFlow.curObjectName) {
          setFlowProperty();
        }
      }
    }
  }
  if (psAction === 'cut' || psAction === 'copy') {
    var loObject = oGetTaskObject(goWorkFlow.curObjectName);
    goWorkFlow.copyObject = new Object();
    if (loObject.xmlObject != null) {
      goWorkFlow.copyObject.xmlObject = loObject.xmlObject.cloneNode(true);
    } else {
      goWorkFlow.copyObject.xmlObject = null;
    }
    goWorkFlow.copyObject.x = loObject.imgObject.x + loObject.imgObject.w / 2;
    goWorkFlow.copyObject.y = loObject.imgObject.y + loObject.imgObject.h / 2;
    goWorkFlow.copyObject.Type = loObject.Type;
    if (psAction === 'cut') {
      goWorkFlow.copyObject = loObject;
      bDeleteTask(loObject.name);
    }
  } else {
    if (psAction === 'paste') {
      if (goWorkFlow.copyObject == null) {
        return;
      }
      goWorkFlow.offsetTime = goWorkFlow.offsetTime || 1;
      var offsetX = goWorkFlow.cursorX + goWorkFlow.offsetTime * 24;
      var offsetY = goWorkFlow.cursorY + goWorkFlow.offsetTime * 24;
      goWorkFlow.offsetTime++;
      var loObject = oCreateTask(goWorkFlow.copyObject.Type, offsetX, offsetY);
      bAdjustObject(loObject);
      loObject.imgObject.maximizeZ();
      if (goWorkFlow.copyObject.xmlObject != null) {
        loObject.xmlObject = goWorkFlow.copyObject.xmlObject.cloneNode(true);
        if (loObject.Type !== 'LABEL') {
          loObject.xmlObject.setAttribute('name', sGetNewName(loObject));
        }
        if (loObject.Type === 'TASK' || loObject.Type === 'SUBFLOW') {
          loObject.xmlObject.setAttribute('id', sGetNewTaskID(loObject));
          var loNode = goWorkFlow.flowXML.selectSingleNode('tasks');
          loNode.appendChild(loObject.xmlObject);
        } else {
          if (loObject.Type === 'LABEL') {
            var loNode = goWorkFlow.flowXML.selectSingleNode('labels');
            loNode.appendChild(loObject.xmlObject);
          }
        }
        bSetObjectText(loObject);
      }
    } else {
      if (psAction === 'delete') {
        bDeleteTask(goWorkFlow.curObjectName || (goWorkFlow.curTaskObj && goWorkFlow.curTaskObj.name));
        bDeleteLine(goWorkFlow.curObjectName || (goWorkFlow.currloLine && goWorkFlow.currloLine.name));
      }
    }
  }
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

function bSetFormFieldValue(poForm, psName, psValues) {
  var lsValueString = ';' + psValues + ';';
  for (var i = 0; i < poForm.elements.length; i++) {
    if (poForm.elements[i].name == null || poForm.elements[i].name != psName) {
      continue;
    }
    switch (poForm.elements[i].type) {
      case 'radio':
        if (lsValueString.indexOf(';' + poForm.elements[i].value + ';') != -1) {
          poForm.elements[i].checked = true;
          if (poForm.elements[i].onclick) {
            poForm.elements[i].onclick();
          }
        }
        break;
      case 'checkbox':
        if (lsValueString.indexOf(';' + poForm.elements[i].value + ';') != -1) {
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
            lsValueString.indexOf(';' + poForm.elements[i].options[j].value + ';') != -1 ||
            lsValueString.indexOf(';' + poForm.elements[i].options[j].text + ';') != -1
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
        lsValue = poForm.elements[i].value;
        return lsValue;
        break;
    }
  }
  return lsValue;
}

function bSetVersionInfo(psVersion, poWindow) {
  if (psVersion == null || psVersion == '') {
    return;
  }
  var objs = poWindow.document.getElementsByTagName('SPAN');
  if (objs != null) {
    for (var i = 0; i < objs.length; i++) {
      if (objs[i].Version == null) {
        continue;
      }
      if (objs[i].Version.indexOf(psVersion) != -1) {
        if (objs[i].id == 'ID_PageContent') {
          objs[i].id = '';
        }
        objs[i].style.display = 'none';
      } else {
        objs[i].style.display = '';
      }
    }
  }
  var objs = poWindow.document.getElementsByTagName('TR');
  if (objs != null) {
    for (var i = 0; i < objs.length; i++) {
      if (objs[i].Version == null) {
        continue;
      }
      if (objs[i].Version.indexOf(psVersion) != -1) {
        objs[i].style.display = 'none';
      } else {
        objs[i].style.display = '';
      }
    }
  }
  var objs = poWindow.document.getElementsByTagName('TD');
  if (objs != null) {
    for (var i = 0; i < objs.length; i++) {
      if (objs[i].Version == null) {
        continue;
      }
      if (objs[i].Version.indexOf(psVersion) != -1) {
        objs[i].style.display = 'none';
      } else {
        objs[i].style.display = '';
      }
    }
  }
  return true;
}

function vOpenModal_(psURL, pvArg, piWidth, piHeight, psStyle) {
  var liWidth = piWidth <= 1 ? screen.width * piWidth : piWidth;
  var liHeight = piHeight <= 1 ? screen.height * piHeight : piHeight;
  if (psStyle == null) {
    psStyle = '';
  }
  var lsModal = psStyle + ' dialogWidth:' + liWidth + 'px;dialogHeight:' + liHeight + 'px;center:yes;status:no;help:no';
  var rvArg = showModalDialog(psURL, pvArg, lsModal);
  return rvArg;
}

function oGetXmlHttpRequest_() {
  if (window.XMLHttpRequest) {
    return new XMLHttpRequest();
  } else {
    if (window.ActiveXObject) {
      try {
        return new ActiveXObject('Msxml2.XMLHTTP');
      } catch (e) {
        try {
          return new ActiveXObject('Microsoft.XMLHTTP');
        } catch (E) {
          return null;
        }
      }
    } else {
      return null;
    }
  }
}

function XMLtoString(elem) {
  var serialized;
  try {
    // XMLSerializer exists in current Mozilla browsers
    serializer = new XMLSerializer();
    serialized = serializer.serializeToString(elem);
  } catch (e) {
    // Internet Explorer has a different approach to serializing XML
    serialized = elem.xml;
  }
  return serialized;
}

function oGetXMLDocument_() {
  return $.parseXML('<flow/>');
}
$('#ID_WORKAROUND').mousedown(function (event) {
  if (event.target.tagName !== 'BODY') {
    mouseDown(event);
    goWorkFlow.allowDeleteCurObject = false;
  }
  return true;
});
/* lmw 2015-4-28 16:28 begin */
$('#ID_WORKAROUND').keydown(function (event) {
  if (event.target.tagName !== 'BODY') {
    return true;
  }
  var code = event.keyCode;
  if (code === 46 && goWorkFlow.allowDeleteCurObject === true) {
    var name = goWorkFlow.curObjectName;
    if (isFlowObject(name, 'line', true)) {
      bDeleteLine(goWorkFlow.curObjectName);
    } else if (
      isFlowObject(name, 'task', true) ||
      isFlowObject(name, 'subflow', true) ||
      isFlowObject(name, 'begin', true) ||
      isFlowObject(name, 'end', true)
    ) {
      bDeleteTask(goWorkFlow.curObjectName);
    }
  }
});

function isFlowObject(curObjectName, name, ignoreCase) {
  if (curObjectName == undefined || curObjectName == null || !name) {
    return false;
  } else {
    if (ignoreCase === true) {
      curObjectName = curObjectName.toLowerCase();
      name = name.toLowerCase();
    }
    return curObjectName.indexOf(name) >= 0;
  }
}
/* lmw 2015-4-28 16:28 begin */

function addTasksAndLines(obj) {
  $('.workflow-designer-tools-bar .wfd-tool[data-type="backout"]').removeClass('disabled');
  goWorkFlow.newTaskAndLines.push(obj);
}
//撤销
function cancelTaskAndLineStack() {
  var obj = goWorkFlow.newTaskAndLines.pop();
  if (!obj) {
    $('.workflow-designer-tools-bar .wfd-tool[data-type="backout"]').addClass('disabled');
    return false;
  }
  goWorkFlow.resetTaskAndLineStack.push(obj);
  if ($.inArray(obj.Type, ['CURVE', 'BEELINE']) > -1) {
    bDeleteLine(obj.name);
  } else {
    bDeleteTask(obj.name);
  }
}

function resetTask(taskNode) {
  console.log(taskNode.selectSingleNode('X').text(), taskNode.selectSingleNode('Y').text());
  var loObject = oCreateTask(
    taskNode.getAttribute('type') == '1' ? 'TASK' : 'SUBFLOW',
    taskNode.selectSingleNode('X').text() - 0,
    taskNode.selectSingleNode('Y').text() - 0
  );
  bAdjustObject(loObject);
  loObject.xmlObject = taskNode;
  bSetObjectText(loObject);
}

function resetCondition(conditionNode) {
  var loFromTask = conditionNode;
  var loObject = oCreateTask(
    'CONDITION',
    taskNode.selectSingleNode('conditionX').text() - 0,
    taskNode.selectSingleNode('conditionY').text() - 0
  );
  bAdjustObject(loObject);
  var loNode = oCreateXMLNode(loObject);
  loNode.setAttribute('name', lsCName);
  loNode.text(taskNode.selectSingleNode('conditionBody') != null ? taskNode.selectSingleNode('conditionBody').text() : '');
  bSetObjectText(loObject);
  var loToTask = loObject;
  if (loFromTask != null && loToTask != null) {
    if (lsCLine.indexOf('BEELINE') == 0) {
      var loObject = oCreateBeeline(loFromTask, loToTask);
    } else {
      var laTemp = lsCLine.split(';');
      loFromTask.cursorWay = laTemp[1];
      loToTask.cursorWay = laTemp[2];
      var loObject = oCreateCurve(loFromTask, loToTask);
    }
  }
}

function resetDirection(directionNode) {
  var lsSource = directionNode.getAttribute('fromID');
  var loStartEndObject = null;
  if (lsSource == '<StartFlow>') {
    var loFromTask = oGetTaskObjectByPropertyName('开始');
    loStartEndObject = loObject;
  } else {
    if (directionNode.getAttribute('type') == '2') {
      var loTask = oGetTaskObjectByPropertyID(lsSource);
      var loFromTask = oGetConditionObjectByFromTask(loTask);
    } else {
      var loFromTask = oGetTaskObjectByPropertyID(lsSource);
    }
  }
  var lsTarget = directionNode.getAttribute('toID');
  if (lsTarget == '<EndFlow>') {
    var loToTask = oGetTaskObjectByPropertyName('结束');
    loStartEndObject = loObject;
  } else {
    var loToTask = oGetTaskObjectByPropertyID(lsTarget);
  }
  if (loFromTask != null && loToTask != null) {
    var lsLine = directionNode.selectSingleNode('line').text();
    if (lsLine.indexOf('BEELINE') == 0) {
      var loObject = oCreateBeeline(loFromTask, loToTask);
    } else {
      var laTemp = lsLine.split(';');
      loFromTask.cursorWay = laTemp[1];
      loToTask.cursorWay = laTemp[2];
      var loObject = oCreateCurve(loFromTask, loToTask);
    }
    if (directionNode.selectSingleNode('isShowName').text() == '0') {
      loObject.isShowName = false;
    } else {
      loObject.isShowName = true;
    }
    loObject.xmlObject = directionNode;
    bSetObjectText(loObject.labelObject);
    var laTemp = directionNode.selectSingleNode('lineLabel').text().split(';');
    loObject.labelObject.imgObject.moveTo(laTemp[0] - 0, laTemp[1] - 0);
  }
  return loObject;
}

//重做
function resetTaskAndLineStack() {
  var obj = goWorkFlow.resetTaskAndLineStack.pop();
  goWorkFlow.newTaskAndLines.push(obj);
  console.log(obj);
  if (obj.obj.Type === 'CONDITION') {
  } else {
    var taskNode = obj.obj.xmlObject;
    resetTask(taskNode);
    goWorkFlow.flowXML.children('tasks').appendChild(taskNode);
  }
  if (obj.lines) {
    $.each(obj.lines, function (i, item) {
      var directionNode = item.obj.xmlObject;
      if (directionNode) {
        resetDirection(directionNode);
        goWorkFlow.flowXML.children('directions').appendChild(directionNode);
      }
    });
  }
}

//恢复线条
function resetLine(obj) {
  $('#' + obj.name).show();
  $('#' + obj.labelObject.name + 'div')
    .show()
    .css('visibility', 'visible');
  $.each(goWorkFlow.tasks, function (i, item) {
    if (item.name === obj.fromTask.name) {
      item.outLines.push(obj);
      return true;
    } else if (item.name === obj.toTask.name) {
      item.inLines.push(obj);
      return true;
    }
  });
}

/*
 * 删除环节/流向等时加入需要撤销重做栈中
 * obj 删除对象
 * index 删除对象所在数组下标
 * append true: 删除环节连带流向加入环节对象，不独立存在栈中
 * */
function removeTaskAndLine(obj, index, append) {
  if (append) {
    var lines = goWorkFlow.resetTaskAndLineStack[goWorkFlow.resetTaskAndLineStack.length - 1].lines || [];
    lines.push({
      obj: obj,
      index: index
    });
    goWorkFlow.resetTaskAndLineStack[goWorkFlow.resetTaskAndLineStack.length - 1].lines = lines;
  } else {
    goWorkFlow.resetTaskAndLineStack.push({
      obj: obj,
      index: index
    });
  }
}

function toPng(element, picName) {
  top.appModal.showMask('流程图生成中...');
  var position = getTaskArea();
  var wrap = document.createElement('div');
  var width = position.right - position.left + 300;
  var height = position.bottom - position.top + 300;
  wrap.style.width = width + 'px';
  wrap.style.height = height + 'px';
  wrap.style.overflow = 'hidden';
  domtoimage.toPng(element).then(function (dataUrl) {
    var img = document.createElement('img');
    img.src = dataUrl;
    img.style.marginTop = '-' + (position.top - 100) + 'px';
    img.style.marginLeft = '-' + (position.left - 100) + 'px';
    wrap.appendChild(img);
    document.body.appendChild(wrap);
    domtoimage
      .toPng(wrap, {
        width: width,
        height: height
      })
      .then(function (dataUrl2) {
        var a = document.createElement('a');
        a.href = dataUrl2;
        a.download = picName || '';
        top.appModal.hideMask();
        a.click();
        a.remove();
      });
  });
}

function setToolsBarStatus() {
  var _timer = setInterval(function () {
    if (goWorkFlow.curObjectName) {
      $('.workflow-designer-tools-bar .wfd-tool[data-type="copy"]').removeClass('disabled');
      $('.workflow-designer-tools-bar .wfd-tool[data-type="cut"]').removeClass('disabled');
      $('.workflow-designer-tools-bar .wfd-tool[data-type="delete"]').removeClass('disabled');
    } else {
      $('.workflow-designer-tools-bar .wfd-tool[data-type="copy"]').addClass('disabled');
      $('.workflow-designer-tools-bar .wfd-tool[data-type="cut"]').addClass('disabled');
      $('.workflow-designer-tools-bar .wfd-tool[data-type="delete"]').addClass('disabled');
    }
    if (goWorkFlow.copyObject) {
      $('.workflow-designer-tools-bar .wfd-tool[data-type="paste"]').removeClass('disabled');
    } else {
      $('.workflow-designer-tools-bar .wfd-tool[data-type="paste"]').addClass('disabled');
    }
    if (goWorkFlow.newTaskAndLines.length) {
      $('.workflow-designer-tools-bar .wfd-tool[data-type="backout"]').removeClass('disabled');
    } else {
      $('.workflow-designer-tools-bar .wfd-tool[data-type="backout"]').addClass('disabled');
    }
    if (goWorkFlow.resetTaskAndLineStack.length) {
      $('.workflow-designer-tools-bar .wfd-tool[data-type="reset"]').removeClass('disabled');
    } else {
      $('.workflow-designer-tools-bar .wfd-tool[data-type="reset"]').addClass('disabled');
    }
  }, 1000);
}

$(document).ready(function () {
  var initTimer = setInterval(function () {
    if (goWorkFlow) {
      clearInterval(initTimer);
      setToolsBarStatus();
    }
  }, 50);
  $('.workflow-designer-tools-bar .wfd-tool')
    .on('click', function (e) {
      var $this = $(this);
      var type = $this.attr('data-type');
      if ($this.hasClass('disabled')) {
        return false;
      }
      switch (type) {
        case 'backout':
          cancelTaskAndLineStack();
          break;
        case 'reset':
          resetTaskAndLineStack();
          break;
        case 'clear':
          parent.clearObjectUuid = goWorkFlow.flowXML.getAttribute('uuid');
          parent.clearObject = true;
          $("td[id^='menu_']", parent.document).off('click');
          $('#workFlowMainTab li', parent.document).hide();
          $('#workFlowMainTab li', parent.document).eq(0).show();
          top.switchPropertyTab('workflowProperties');
          location.reload();
          e.stopPropagation();
          break;
        case 'delete':
          bDealObject('delete');
          break;
        case 'copy':
          bDealObject('copy');
          break;
        case 'cut':
          bDealObject('cut');
          break;
        case 'paste':
          bDealObject('paste');
          break;
        case 'grid':
          $('#ID_WORKAROUND').toggleClass('no-bg');
          break;
        case 'fullScreen':
          parent.fullScreen();
          break;
        case 'checkError':
          temporarySaveProperty();
          if (checkWorkFlow()) {
            top.appModal.success('流程设计检查通过！');
            top.appModal.hideMask();
          } else {
            console.log(false);
          }
          break;
        case 'downloadWorkflow':
          var ele = document.getElementById('ID_WORKAROUND');
          var saveStatus = isSave();
          if (saveStatus) {
            var _now = new Date().format('yyyy-MM-dd HH:mm:ss');
            toPng(ele, goWorkFlow.flowXML.attr('name') + '(' + _now + ')');
          }
          break;
      }
    })
    .on('mouseenter', function () {
      var $this = $(this);
      var type = $this.attr('data-type');
      var handleText = '';
      var tipText = '';

      switch (type) {
        case 'backout':
          handleText = '撤销（CTRL+Z）';
          tipText = '撤销$(上一操作)';
          break;
        case 'reset':
          handleText = '重做（CTRL+Y）';
          tipText = '重做$(上一撤销操作)';
          break;
        case 'clear':
          handleText = '清空（CTRL+D）';
          tipText = '清空流程设计，重新绘制';
          break;
        case 'delete':
          handleText = '删除（Delete）';
          tipText = '删除选中的内容';
          break;
        case 'copy':
          handleText = '复制（CTRL+C）';
          tipText = '复制选中的内容到剪贴板';
          break;
        case 'cut':
          handleText = '剪切（CTRL+X）';
          tipText = '删除选中的内容到剪贴板';
          break;
        case 'paste':
          handleText = '粘贴（CTRL+V）';
          tipText = '将剪贴板内容粘贴到画布';
          break;
        case 'grid':
          handleText = '网格';
          tipText = '切换网格的显示和隐藏';
          break;
        case 'fullScreen':
          handleText = '全屏';
          tipText = '进入全屏编辑模式';
          break;
        case 'checkError':
          handleText = '错误检查';
          tipText = '检查流程图是否存在错误';
          break;
        case 'downloadWorkflow':
          handleText = '下载流程图';
          tipText = '将流程图以图片形式保存';
          break;
      }

      var popover =
        '<div class="wfd-popover">' + '<div class="handle">' + handleText + '</div>' + '<div class="tip">' + tipText + '</div>' + '</div>';
      $this.append(popover);
    })
    .on('mouseleave', function () {
      var $this = $(this);
      $this.find('.wfd-popover').remove();
    });

  $(document).on('keydown', function (e) {
    var newWorkFlowDesigner = $('#newWorkFlowDesigner', top.document)[0].contentWindow;
    switch (e.keyCode) {
      case 37:
      case 38:
      case 39:
      case 40:
        if (goWorkFlow.curObjectName && !$(top.document).find('.bootbox.modal').length) {
          e.preventDefault();
          newWorkFlowDesigner.keyboardMove(e.keyCode);
          return false;
        }
        break;
      case 46:
        if (goWorkFlow.curObjectName) {
          e.preventDefault();
          var designer = $('#newWorkFlowDesigner', top.document)[0].contentWindow;
          $('.workflow-designer-tools-bar .wfd-tool[data-type="delete"]', designer.document).trigger('click');
          return false;
        }
        break;
    }
  });

  $('#WORKBODY').on('keydown', function (e) {
    var isMac = /macintosh|mac os x/i.test(navigator.userAgent);
    if ((isMac && e.metaKey && e.keyCode !== 91) || e.ctrlKey) {
      switch (e.keyCode) {
        case 90:
          $('.workflow-designer-tools-bar .wfd-tool[data-type="backout"]').trigger('click');
          break;
        case 89:
          $('.workflow-designer-tools-bar .wfd-tool[data-type="reset"]').trigger('click');
          break;
        case 68:
          $('.workflow-designer-tools-bar .wfd-tool[data-type="clear"]').trigger('click');
          e.preventDefault();
          return false;
          break;
        case 67:
          $('.workflow-designer-tools-bar .wfd-tool[data-type="copy"]').trigger('click');
          break;
        case 88:
          $('.workflow-designer-tools-bar .wfd-tool[data-type="cut"]').trigger('click');
          break;
        case 86:
          $('.workflow-designer-tools-bar .wfd-tool[data-type="paste"]').trigger('click');
          break;
      }
    } else {
      switch (e.keyCode) {
        case 37:
        case 38:
        case 39:
        case 40:
          if (goWorkFlow.curObjectName && !$(top.document).find('.bootbox.modal').length) {
            e.preventDefault();
            keyboardMove(e.keyCode);
            return false;
          }
          break;
        case 46:
          $('.workflow-designer-tools-bar .wfd-tool[data-type="delete"]').trigger('click');
          break;
      }
    }
  });

  $('#workFlowSave').on('click', function () {
    saveWorkFlow(false);
  });

  $('#workFlowSaveNew').on('click', function () {
    saveWorkFlow(true);
  });
});

function getCurrentTab() {
  return $('#workFlowMainTab', top.document).find('li.active').attr('data-name');
}

function temporarySaveProperty(check, tabName, target) {
  var curTab = tabName || getCurrentTab();
  var verifyStatus;

  var classList = target ? target.className.split(' ') : [];

  if (!tabName && classList.indexOf('img_begin') == -1 && classList.indexOf('img_end') == -1) {
    top.appModal.showMask();
  }
  switch (curTab) {
    case 'workflowProperties':
      var propertiesFrame = $('#propertiesFrame', top.document)[0].contentWindow;
      verifyStatus = propertiesFrame.collectPropertiesData();
      break;
    case 'nodeProperties':
      var nodeFrame = $('#nodeFrame', top.document)[0].contentWindow;
      verifyStatus = nodeFrame.collectPropertiesData(check);
      break;
    case 'flowDirectProperties':
      var directionFrame = $('#directionFrame', top.document)[0].contentWindow;
      verifyStatus = directionFrame.collectPropertiesData(check);
      break;
    case 'subFlowProperties':
      var subFlowFrame = $('#subFlowFrame', top.document)[0].contentWindow;
      verifyStatus = subFlowFrame.collectPropertiesData();
      break;
    case 'conditionProperties':
      var conditionFrame = $('#conditionFrame', top.document)[0].contentWindow;
      verifyStatus = conditionFrame.collectPropertiesData();
      break;
  }
  if (verifyStatus) {
    return goWorkFlow.flowXML.context.outerHTML;
  } else {
    return false;
  }
}

function changeObjText(obj) {
  var newWorkFlowDesigner = $('#newWorkFlowDesigner', top.document)[0].contentWindow;
  newWorkFlowDesigner.bSetObjectText(obj);
}

function changeLabelText() {
  var newWorkFlowDesigner = $('#newWorkFlowDesigner', top.document)[0].contentWindow;
  newWorkFlowDesigner.bSetLabelHTML();
}

function equalSwitch(status) {
  var designer = $('#newWorkFlowDesigner', top.document)[0].contentWindow.document;
  var designerElement = $(
    '.img_line,.img_task,.img_begin,.img_end,.img_subflow,.img_condition,span[type="BEELINE"],span[type="CURVE"]',
    designer
  );
  if (status) {
    goWorkFlow.equalStatus = true;
    designerElement.hide();
  } else {
    goWorkFlow.equalStatus = false;
    designerElement.show();
  }
}

function initTaskPropertyContent(taskProperty) {
  var str = '';
  str += getDefaultRight(taskProperty, 'startRights', '/diction/startRights/right');
  str += getDefaultRight(taskProperty, 'rights', '/diction/rights/right');
  str += getDefaultRight(taskProperty, 'doneRights', '/diction/doneRights/right');
  str += getDefaultRight(taskProperty, 'monitorRights', '/diction/monitorRights/right');
  str += getDefaultRight(taskProperty, 'adminRights', '/diction/adminRights/right');
  return (
    '<isSetUser></isSetUser><users/><isSetUserEmpty>1</isSetUserEmpty><emptyToTask></emptyToTask><emptyNoteDone></emptyNoteDone><isSelectAgain></isSelectAgain><isAnyone></isAnyone><isByOrder></isByOrder><sameUserSubmit/><isSetCopyUser></isSetCopyUser><copyUsers/><copyUserCondition></copyUserCondition><isSetMonitor></isSetMonitor><isAllowApp></isAllowApp>' +
    str +
    '<optNames/><buttons/><granularity></granularity><canEditForm></canEditForm><hideBlocks/><hideTabs/><hideFields/><editFields/><notNullFields/><formID></formID><parallelGateway><forkMode><value>1</value></forkMode><joinMode><value/></joinMode></parallelGateway><records/><printTemplate></printTemplate><printTemplateId></printTemplateId><printTemplateUuid></printTemplateUuid><snName></snName><serialNo></serialNo><customJsModule></customJsModule><listener></listener><listenerName></listenerName><eventScripts/>'
  );
}
// 判断是否为第一个环节
function isFirstTask(taskProperty) {
  var taskId = $(taskProperty).getAttribute('id');
  var directions = aGetDirections();
  for (var i = 0; i < directions.length; i++) {
    var laTemp = directions[i].split('|');
    if (laTemp[3] == '<StartFlow>' && laTemp[5] == taskId) {
      return true;
    }
  }
  return false;
}
//设置默认权限
function getDefaultRight(task, ele, path) {
  var str = '';
  var laNode = goWorkFlow.dictionXML.selectNodes(path);
  if (task && laNode != null && laNode.length > 0) {
    str += '<' + ele + '>';
    for (var j = 0; j < laNode.length; j++) {
      if ($(laNode[j]).selectSingleNode('isDefault').text() == '1') {
        // 发起权限 默认不显示必须签署意见
        if ($(laNode[j]).selectSingleNode('value').text() == 'B004026' && ele == 'startRights') {
          continue;
        }
        // 监督权限 默认不显示抄送、关注、取消关注
        if (
          ($(laNode[j]).selectSingleNode('value').text() == 'B004008' ||
            $(laNode[j]).selectSingleNode('value').text() == 'B004010' ||
            $(laNode[j]).selectSingleNode('value').text() == 'B004012') &&
          ele == 'adminRights'
        ) {
          continue;
        }
        str += "<unit type='32'>" + $(laNode[j]).find('code').text() + '</unit>';
      }
    }
    str += '</' + ele + '>';
  } else {
    str += '<' + ele + '/>';
  }
  return str;
}
//判断流程是否保存
function isSave() {
  var x2js = new X2JS();
  var newFlow1Obj = x2js.xml2js(temporarySaveProperty());
  var newFlow2Obj = x2js.xml2js(goWorkFlow.flowXMLStr);
  newFlow1Obj.flow._systemUnitId = newFlow2Obj.flow._systemUnitId; //打开其他角色建的流程转化相同系统单位再对比
  //移除环节的_initId属性再做对比
  if ($.isArray(newFlow1Obj.flow.tasks.task)) {
    $.each(newFlow1Obj.flow.tasks.task, function (i, item) {
      delete item['_initId'];
    });
  } else {
    delete newFlow1Obj.flow.tasks.task['_initId'];
  }
  if ($.isArray(newFlow2Obj.flow.tasks.task)) {
    $.each(newFlow2Obj.flow.tasks.task, function (i, item) {
      delete item['_initId'];
    });
  } else {
    delete newFlow2Obj.flow.tasks.task['_initId'];
  }
  var uuid = goWorkFlow.flowXML.attr('uuid');
  if (!uuid || !deepCompare(newFlow1Obj, newFlow2Obj)) {
    top.appModal.error('请先保存流程定义！');
    top.appModal.hideMask();
    return false;
  }
  return true;
}
//显示/隐藏空流程图标
function showOrHideEMpty(type) {
  if (onlyGraphics) {
    return;
  }
  var designer = $('#newWorkFlowDesigner', top.document)[0].contentWindow.document;
  if (type === 'show') {
    $('.no-flow', designer).show();
  } else {
    $('.no-flow', designer).hide();
  }
}

// 查阅模式 - 仅可以查看流程画布，不能查看流程属性
function onlyViewMode() {
  $('#designerLeft,.left-view-mode,.designer-right,.right-view-mode,.work-flow-btns', top.document).hide();
  $('.designer-body', top.document).css('padding', 0);
  $('.work-flow-download', top.document).show();
  var designer = $('#newWorkFlowDesigner', top.document)[0].contentWindow.document;
  $('.workflow-designer-tools-bar', designer).hide();
  $('.work-flow-download', top.document).on('click', function () {
    $('.wfd-tool[data-type="downloadWorkflow"]', designer).trigger('click');
  });
}

// 只读模式 - 可以查看流程属性、环节属性、流向属性等
function readonlyMode() {
  $('#designerLeft, .left-view-mode, .work-flow-btns', top.document).hide();
  $('.designer-body', top.document).css('padding', 0);
  $('.work-flow-download', top.document).show();
  var designer = $('#newWorkFlowDesigner', top.document)[0].contentWindow.document;
  $('.workflow-designer-tools-bar', designer).hide();
  $('.work-flow-download', top.document).on('click', function () {
    $('.wfd-tool[data-type="downloadWorkflow"]', designer).trigger('click');
  });
}

//画布初始化到中间
function initMoveCenter() {
  var position = getTaskArea();
  var ID_WORKAROUND = null;
  if (onlyGraphics) {
    ID_WORKAROUND = document.querySelector("#ID_WORKAROUND")
  } else {
    var designer = $('#newWorkFlowDesigner', top.document)[0].contentWindow;
    ID_WORKAROUND = designer.ID_WORKAROUND;
  }
  var taskW = position.right - position.left;
  var taskH = position.bottom - position.top;
  var canvasW = ID_WORKAROUND.offsetWidth;
  var canvasH = ID_WORKAROUND.offsetHeight;

  $.each(goWorkFlow.tasks, function (i, item) {
    var x = item.imgObject.x;
    var y = item.imgObject.y;
    var moveToX = (canvasW - taskW) / 2 + x - position.left;
    var moveToY = (canvasH - taskH) / 2 + y - position.top;
    item.imgObject.moveTo(moveToX, moveToY);
    bAdjustObject(item);
  });

  $.each(goWorkFlow.lines, function (i, item) {
    if (item == null) {
      return true;
    }
    if (item.Type === 'BEELINE') {
      oCreateBeeline(item.fromTask, item.toTask, null, item);
    } else {
      oCreateCurve(item.fromTask, item.toTask, null, item);
    }
  });

  resetScroll();
}

//重置画布的滚动位置
function resetScroll() {
  var position = getTaskArea();
  var ID_WORKAROUND = null;
  if (onlyGraphics) {
    ID_WORKAROUND = document.querySelector("#ID_WORKAROUND")
  } else {
    var designer = $('#newWorkFlowDesigner', top.document)[0].contentWindow;
    ID_WORKAROUND = designer.ID_WORKAROUND;
  }
  var taskW = position.right - position.left + 144;
  var taskH = position.bottom - position.top + 48;
  var canvasW = ID_WORKAROUND.offsetWidth;
  var canvasH = ID_WORKAROUND.offsetHeight;

  if (window.innerWidth < canvasW) {
    if (window.innerWidth > taskW) {
      $(document).scrollLeft((canvasW - window.innerWidth) / 2);
    } else {
      $(document).scrollTop(position.left - 80);
    }
  }
  if (window.innerHeight < canvasH) {
    var realInnerHeight = window.innerHeight - 48;
    if (realInnerHeight > taskH) {
      $(document).scrollTop(Math.ceil((canvasH - realInnerHeight) / 48) * 24 - 48);
    } else {
      $(document).scrollTop(position.top - 48);
    }
  }
}

function getTaskArea() {
  var position = {};
  $.each(goWorkFlow.tasks, function (i, item) {
    var x = item.imgObject.x;
    var y = item.imgObject.y;
    if (position.top || position.top === 0) {
      position.top = position.top < y ? position.top : y;
    } else {
      position.top = y;
    }
    if (position.bottom) {
      position.bottom = position.bottom > y ? position.bottom : y;
    } else {
      position.bottom = y;
    }
    if (position.left || position.left === 0) {
      position.left = position.left < x ? position.left : x;
    } else {
      position.left = x;
    }
    if (position.right) {
      position.right = position.right > x ? position.right : x;
    } else {
      position.right = x;
    }
  });
  return position;
}

function keyboardMove(type) {
  var loObject = oGetTaskObject(goWorkFlow.curObjectName);
  var x = loObject.imgObject.x;
  var y = loObject.imgObject.y;
  var w = loObject.imgObject.w;
  var h = loObject.imgObject.h;
  var designer = $('#newWorkFlowDesigner', top.document)[0].contentWindow;
  var ID_WORKAROUND = designer.ID_WORKAROUND;
  var canvasW = ID_WORKAROUND.offsetWidth;
  var canvasH = ID_WORKAROUND.offsetHeight;
  switch (type) {
    case 37:
      if (x < 24) {
        top.appModal.error('已经在最左端了，无法左移！');
      } else {
        x -= 24;
      }
      break;
    case 38:
      if (y <= 72) {
        top.appModal.error('已经在最顶端了，无法上移！');
      } else {
        y -= 24;
      }
      break;
    case 39:
      if (x + w >= canvasW) {
        top.appModal.error('已经在最右端了，无法右移！');
      } else {
        x += 24;
      }
      break;
    case 40:
      if (y + h >= canvasH) {
        top.appModal.error('已经在最底端了，无法下移！');
      } else {
        y += 24;
      }
      break;
  }
  var scrollLeft = $(designer.document).scrollLeft();
  var scrollTop = $(designer.document).scrollTop();
  if (x < scrollLeft) {
    $(designer.document).scrollLeft(x);
  }
  if (x + w > scrollLeft + designer.innerWidth) {
    $(designer.document).scrollLeft(scrollLeft + 24);
  }
  if (y - 48 < scrollTop) {
    $(designer.document).scrollTop(y - 48);
  }
  if (y + h > scrollTop + designer.innerHeight) {
    $(designer.document).scrollTop(scrollTop + 24);
  }

  loObject.imgObject.moveTo(x, y);
  for (var i = 0; i < loObject.outLines.length; i++) {
    if (loObject.outLines[i] == null) {
      continue;
    }
    if (loObject.outLines[i].Type === 'BEELINE') {
      oCreateBeeline(loObject, loObject.outLines[i].toTask, null, loObject.outLines[i]);
    } else {
      oCreateCurve(loObject, loObject.outLines[i].toTask, null, loObject.outLines[i]);
    }
  }
  for (var i = 0; i < loObject.inLines.length; i++) {
    if (loObject.inLines[i] == null) {
      continue;
    }
    if (loObject.inLines[i].Type === 'BEELINE') {
      oCreateBeeline(loObject.inLines[i].fromTask, loObject, null, loObject.inLines[i]);
    } else {
      oCreateCurve(loObject.inLines[i].fromTask, loObject, null, loObject.inLines[i]);
    }
  }
}

function removeAllFocus() {
  if (onlyGraphics) {
    return;
  }
  var propertiesFrame = $('#propertiesFrame', top.document)[0].contentWindow;
  var nodeFrame = $('#nodeFrame', top.document)[0].contentWindow;
  var directionFrame = $('#directionFrame', top.document)[0].contentWindow;
  var subFlowFrame = $('#subFlowFrame', top.document)[0].contentWindow;
  var conditionFrame = $('#conditionFrame', top.document)[0].contentWindow;
  $('input,textarea', propertiesFrame.document).blur();
  $('input,textarea', nodeFrame.document).blur();
  $('input,textarea', directionFrame.document).blur();
  $('input,textarea', subFlowFrame.document).blur();
  $('input,textarea', conditionFrame.document).blur();
}

//初始化部分待保存时的校验信息及移除环节的xml初始化id
function initOtherInfo() {
  var tasks = goWorkFlow.tasks;
  $.each(tasks, function (i, item) {
    var id = item.xmlObject.attr('id');
    if (id) {
      item.xmlObject.removeAttr('initId');
    }
  });
  goWorkFlow.isFree = goWorkFlow.flowXML.selectSingleNode('isFree').text() || '0';
  goWorkFlow.equalFlow_ID = goWorkFlow.flowXML.selectSingleNode('equalFlow/id').text();
}

//根据环节id设置环节错误状态
function setModifyTaskStatusById(id, status) {
  $(oGetTaskObjectByPropertyID(id).htmlObject)[status === 'error' ? 'addClass' : 'removeClass']('flow-control-error');
}

function setErrorLineStatus(loObject, status) {
  var loSpan = loObject.htmlObject;
  var label = loObject.labelObject.htmlObject;
  $(label)[status ? 'addClass' : 'removeClass']('flow-control-error');
  for (var i = 0; i < loSpan.children.length; i++) {
    $(loSpan.children[i])[status ? 'addClass' : 'removeClass']('flow-line-error');
  }
}
