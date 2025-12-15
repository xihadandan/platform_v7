/**
 * 从数据库中加载表单定义信息
 */
var loadFormDefinition = function (uuid) {
  if (uuid == '' || uuid == null || typeof uuid == 'undefined' || uuid == 'undefined') {
    return new MainFormClass();
  }
  var definitionObj = null;
  var time1 = new Date().getTime();

  $.ajax({
    url: contextPath + '/pt/dyform/definition/getFormDefinition',
    cache: false,
    async: false, // 同步完成
    type: 'POST',
    data: {
      formUuid: uuid,
      justDataAndDef: false
    },
    dataType: 'json',
    success: function (data) {
      definitionObj = data;
    },
    error: function (data) {
      // 加载定义失败
      if (data.status == 404) {
        appModal.info({
          message: '表单不存在:formUuid=' + uuid,
          removeTimer: true
        });
        definitionObj = null;
      } else {
        appModal.info('表单定义加载失败,请重试');
        // window.close();//加载失败时关闭当前窗口,用户需重新点击
        definitionObj = null;
        throw new Error(data);
      }
    }
  });

  if (definitionObj != null) {
    $.extend(definitionObj, formDefinitionMethod);
  }

  var time2 = new Date().getTime();
  console.log('加载定义所用时间:' + (time2 - time1) / 1000.0 + 's');
  return definitionObj;
};

var loadFormDefinitions = function (formUuids) {
  if (formUuids == '' || formUuids == null || typeof formUuids == 'undefined' || formUuids == 'undefined' || formUuids.length == 0) {
    return {};
  }
  var definitionObjs = [];
  var time1 = new Date().getTime();

  $.ajax({
    url: contextPath + '/dyform/getFormDefinitions',
    cache: false,
    async: false, // 同步完成
    type: 'POST',
    data: {
      formUuids: JSON.cStringify(formUuids)
    },
    dataType: 'json',
    success: function (data) {
      definitionObjs = data;
    },
    error: function () {
      // 加载定义失败
      oAlert('表单定义加载失败,请重试');
      // window.close();//加载失败时关闭当前窗口,用户需重新点击
    }
  });

  var definitionMap = [];
  if (definitionObjs != null && definitionObjs.length > 0) {
    for (var i = 0; i < definitionObjs.length; i++) {
      var definitionObj = eval('(' + definitionObjs[i] + ')');
      $.extend(definitionObj, formDefinitionMethod);
      definitionMap[definitionObj.uuid] = definitionObj;
    }
  }

  var time2 = new Date().getTime();
  console.log('加载定义所用时间:' + (time2 - time1) / 1000.0 + 's');
  return definitionMap;
};

var loadFormDefinitionsAndDefaultFormData = function (formUuids, formUuid) {
  /*
   * if(formUuids == "" || formUuids == null || typeof formUuids ==
   * "undefined" || formUuids == "undefined" || formUuids.length == 0){ return
   * {}; }
   */
  var datas = {};
  // var time1 =( new Date()).getTime();

  $.ajax({
    url: contextPath + '/dyform/getFormDefinitions',
    cache: false,
    async: false, // 同步完成
    type: 'POST',
    data: {
      formUuids: JSON.cStringify(formUuids),
      formUuid: formUuid
    },
    dataType: 'json',
    success: function (data) {
      datas = data;
    },
    error: function () {
      // 加载定义失败
      oAlert('表单定义加载失败,请重试');
      // window.close();//加载失败时关闭当前窗口,用户需重新点击
    }
  });

  return datas;
  /*
   * var definitionMap = []; ; if(definitionObjs != null &&
   * definitionObjs.length > 0){ for(var i = 0; i < definitionObjs.length;
   * i++){ var definitionObj = eval("(" + definitionObjs[i] + ")");
   * $.extend(definitionObj, formDefinitionMethod);
   * definitionMap[definitionObj.uuid] = definitionObj; } }
   *
   * var time2 =( new Date()).getTime(); console.log("加载定义所用时间:" + (time2 -
   * time1)/1000.0 + "s"); return definitionMap;
   */
};

/**
 * 根据指定的dataUuid从指定的formUuid表单中获取表单数据
 */
var loadFormData = function (formUuid, dataUuid) {
  if (formUuid == '' || formUuid == null || typeof dataUuid == 'undefined' || dataUuid == 'undefined') {
    // 未初始化
    throw new Error('formUuid or dataUuid is not initialized');
  }

  var formDatas = null;
  $.ajax({
    url: contextPath + '/dyformdata/getFormData',
    cache: false,
    async: false, // 同步完成
    type: 'POST',
    data: {
      formUuid: formUuid,
      dataUuid: dataUuid
    },
    dataType: 'json',
    success: function (result) {
      if (result.success == 'true' || result.success == true) {
        formDatas = result.data;
      } else {
        alert('数据获取失败');
      }
    },
    error: function (re) {
      // 加载定义失败
      alert('数据获取失败');
      alert(JSON.cStringify(re));
      formDatas = null;
    }
  });
  return formDatas;
};

/**
 * 根据指定的dataUuid从指定的formUuid表单中获取表单数据
 */
var loadFormDefinitionData = function (formUuid, dataUuid) {
  if (typeof dataUuid == 'undefined' || dataUuid == 'undefined') {
    // 未初始化
    dataUuid = '';
  }

  var formDatas = null;
  $.ajax({
    url: contextPath + '/dyformdata/getFormDefinitionData',
    cache: false,
    async: false, // 同步完成
    type: 'POST',
    data: {
      formUuid: formUuid,
      dataUuid: dataUuid
    },
    dataType: 'json',
    success: function (result) {
      if (result.success == 'true' || result.success == true) {
        formDatas = result.data;
      } else {
        alert('数据获取失败');
      }
    },
    error: function () {
      // 加载定义失败
      alert('数据获取失败');
      formDatas = null;
    }
  });
  return formDatas;
};

/**
 * 根据指定的dataUuid从指定的formId表单中获取表单数据
 */
var loadFormDefinitionDataByFormId = function (formId, dataUuid) {
  if (typeof dataUuid == 'undefined' || dataUuid == 'undefined') {
    // 未初始化
    dataUuid = '';
  }

  var formDatas = null;
  $.ajax({
    url: contextPath + '/dyformdata/getFormDefinitionDataByFormId',
    cache: false,
    async: false, // 同步完成
    type: 'POST',
    data: {
      formId: formId,
      dataUuid: dataUuid
    },
    dataType: 'json',
    success: function (result) {
      if (result.success == 'true' || result.success == true) {
        formDatas = result.data;
      } else {
        alert('数据获取失败');
      }
    },
    error: function () {
      // 加载定义失败
      alert('数据获取失败');
      formDatas = null;
    }
  });
  return formDatas;
};

function loadDisplayModelDefintion(uuid) {
  var model = null;
  var url = contextPath + '/dyformmodel/getDisplayModel';
  $.ajax({
    url: url,
    type: 'POST',
    data: {
      uuid: uuid
    },
    dataType: 'json',
    async: false,
    timeout: 120000,
    contentType: 'application/x-www-form-urlencoded',
    beforeSend: function () {
      pageLock('show');
    },
    complete: function () {
      pageLock('hide');
    },
    success: function (result) {
      if (result.success == 'true' || result.success == true) {
        // $("#"+$(window.opener.document.getElementById("tt")).attr("id")).trigger('reloadGrid');
        // window.opener.location.reload();//刷新父窗口页面
        // window.close();
        // window.location.href = contextPath +
        // "/dyformmodel/openDisplayModel?uuid=" + result.data + "";
        model = result.data;
      } else {
        alert('获取显示单据信息失败');
      }
    },
    error: function (result) {
      var responseText = result.responseText;
      try {
        var errorObj = eval('(' + responseText + ')');
        alert('保存失败\n' + errorObj.data);
      } catch (e) {
        alert(JSON.cStringify(result));
      }

      // console.log(JSON.cStringify(data));
    }
  });
  return model;
}

function loadDisplayModelDefintionByModelId(modelId) {
  var model = null;
  var url = contextPath + '/dyformmodel/getDisplayModelByModelId';
  $.ajax({
    url: url,
    type: 'POST',
    data: {
      modelId: modelId
    },
    dataType: 'json',
    async: false,
    timeout: 120000,
    contentType: 'application/x-www-form-urlencoded',
    beforeSend: function () {
      pageLock('show');
    },
    complete: function () {
      pageLock('hide');
    },
    success: function (result) {
      if (result.success == 'true' || result.success == true) {
        // $("#"+$(window.opener.document.getElementById("tt")).attr("id")).trigger('reloadGrid');
        // window.opener.location.reload();//刷新父窗口页面
        // window.close();
        // window.location.href = contextPath +
        // "/dyformmodel/openDisplayModel?uuid=" + result.data + "";
        model = result.data;
      } else {
        alert('获取显示单据信息失败');
      }
    },
    error: function (result) {
      var responseText = result.responseText;
      try {
        var errorObj = eval('(' + responseText + ')');
        alert('保存失败\n' + errorObj.data);
      } catch (e) {
        alert(JSON.cStringify(result));
      }

      // console.log(JSON.cStringify(data));
    }
  });
  return model;
}

function formPreview(formDef) {
  if (formDef.uuid == '') {
    formDef.uuid = 'demouuid';
  }

  var str = JSON.cStringify(formDef);
  openPostWindow(ctx + '/pt/dyform/definition/open', str, '_blank');
}

function openPostWindow(url, data, name) {
  var tempForm = document.createElement('form');
  tempForm.id = 'tempForm1';
  tempForm.method = 'post';
  tempForm.action = url;
  tempForm.target = name;

  var hideInput = document.createElement('input');
  hideInput.type = 'hidden';
  hideInput.name = 'formDefinition';
  hideInput.value = data;
  tempForm.appendChild(hideInput);
  tempForm.onsubmit = function () {
    openWindow(name);
  };
  document.body.appendChild(tempForm);

  var csrfToken = document.createElement('input');
  csrfToken.type = 'hidden';
  csrfToken.name = '_csrf';
  csrfToken.value = getCookie('_csrfToken');
  tempForm.appendChild(csrfToken);
  // tempForm.fireEvent("onsubmit");
  tempForm.submit();
  document.body.removeChild(tempForm);
}

function openWindow(name) {
  window.open(
    'about:blank',
    name,
    'height=400, width=400, top=0, left=0, toolbar=yes, menubar=yes, scrollbars=yes, resizable=yes,location=yes, status=yes'
  );
}

/**
 * 控件参数继承时需要将父类属性显示. eg. this.toJSON = toJSON;
 */
var toJSON = function () {
  /*
   * var tmp = {};
   *
   * for(var key in this) { if(typeof this[key] !== 'function') tmp[key] =
   * this[key]; } return tmp;
   */
  // alert(Object.create);
  if (typeof Object.create !== 'function') {
    // ie8下面没有该函数,所以在这里被定义
    Object.create = function (o) {
      function F() {}
      F.prototype = o;
      return new F();
    };
  }
  var result = Object.create(this);
  for (var key in result) {
    result[key] = result[key];
  }
  return result;
};

var getDbDataTypeName = function (dbDataType) {
  for (var i in dyFormDataType) {
    if (dyFormDataType[i] == dbDataType) {
      return i;
    }
  }
  return dbDataType;
};

var getControlName = function (inputMode) {
  if (inputMode == dyFormInputMode.text) {
    return '文本输入';
  } else if (inputMode == dyFormInputMode.ckedit) {
    return '富文本编辑';
  } else if (inputMode == dyFormInputMode.accessory1) {
    return '图标式附件';
  } else if (inputMode == dyFormInputMode.accessory3) {
    return '列表式附件';
  } else if (inputMode == dyFormInputMode.accessoryImg) {
    return '图片附件	';
  } else if (inputMode == dyFormInputMode.serialNumber) {
    return '可编辑流水号';
  } else if (inputMode == dyFormInputMode.unEditSerialNumber) {
    return '不可编辑流水号';
  } else if (inputMode == dyFormInputMode.orgSelect) {
    return '组织选择控件';
  } else if (inputMode == dyFormInputMode.orgSelectStaff) {
    return '组织选择框（人员）';
  } else if (inputMode == dyFormInputMode.orgSelectDepartment) {
    return '组织选择框（部门）';
  } else if (inputMode == dyFormInputMode.orgSelectStaDep) {
    return '组织选择框（部门+人员）';
  } else if (inputMode == dyFormInputMode.orgSelectAddress) {
    return '组织选择框 (单位通讯录)';
  } else if (inputMode == dyFormInputMode.timeEmploy) {
    return '资源选择';
  } else if (inputMode == dyFormInputMode.timeEmployForMeet) {
    return '资源选择（会议）';
  } else if (inputMode == dyFormInputMode.timeEmployForCar) {
    return '资源选择（车辆）';
  } else if (inputMode == dyFormInputMode.timeEmployForDriver) {
    return '资源选择（司机）';
  } else if (inputMode == dyFormInputMode.treeSelect) {
    return '树形下拉框';
  } else if (inputMode == dyFormInputMode.radio) {
    return 'radio';
  } else if (inputMode == dyFormInputMode.checkbox) {
    return 'checkbox';
  } else if (inputMode == dyFormInputMode.selectMutilFase) {
    return '下拉单选框';
  } else if (inputMode == dyFormInputMode.textArea) {
    return '文本域输入';
  } else if (inputMode == dyFormInputMode.textBody) {
    return '正文 ';
  } else if (inputMode == dyFormInputMode.dialog) {
    return '弹出框';
  } else if (inputMode == dyFormInputMode.xml) {
    return 'XML';
  } else if (inputMode == dyFormInputMode.date) {
    return '日期';
  } else if (inputMode == dyFormInputMode.number) {
    return '数字控件';
  } else if (inputMode == dyFormInputMode.viewdisplay) {
    return '视图展示';
  } else if (inputMode == dyFormInputMode.taggroup) {
    return 'taggroup';
  } else if (inputMode == dyFormInputMode.orgSelect2) {
    return '组织选择框';
  } else if (inputMode == dyFormInputMode.select) {
    return '下拉框';
  } else if (inputMode == dyFormInputMode.text) {
    return '';
  }
};

var getApplyToName = function (applayTo) {
  if (applayTo == undefined || applayTo.length == 0) {
    return '';
  }
  var name = '';
  JDS.call({
    service: 'dataDictionaryService.getKeyValuePair',
    data: ['DY_FORM_FIELD_MAPPING', applayTo],
    async: false,
    success: function (result) {
      name = result.data.label;
    },
    error: function (jqXHR) {}
  });
  return name;
};

var getValueCreateMethod = function (valueCreateMethod) {
  if (valueCreateMethod == dyFormInputValue.userImport) {
    return '用户输入';
  } else if (valueCreateMethod == dyFormInputValue.jsEquation) {
    return 'JS公式';
  } else if (valueCreateMethod == dyFormInputValue.creatOperation) {
    return '由后台创建时计算';
  } else if (valueCreateMethod == dyFormInputValue.showOperation) {
    return '由前台显示时计算';
  } else if (valueCreateMethod == dyFormInputValue.twoDimensionCode) {
    return '二维码';
  } else if (valueCreateMethod == dyFormInputValue.shapeCod) {
    return '条形码';
  } else if (valueCreateMethod == dyFormInputValue.relationDoc) {
    return '关联文档';
  }
};

var getShowType = function (showType) {
  if (showType == dyshowType.edit) {
    return '可编辑';
  } else if (showType == dyshowType.showAsLabel) {
    return '显示文本  ';
  } else if (showType == dyshowType.readonly) {
    return '只读 ';
  } else if (showType == dyshowType.disabled) {
    return '禁用 ';
  } else if (showType == dyshowType.hide) {
    return '隐藏';
  }
};

var sortASCBy = function (arr, prop) {
  // var props=[],
  // ret=[],

  len = arr.length;

  if (typeof prop == 'string') {
    // 冒泡排序法
    for (var j = 0; j < len - 1; j++) {
      for (var i = 0; i < len - (j + 1); i++) {
        var oI = arr[i];
        var oIPlus = arr[i + 1];

        if (!oI[prop]) {
          oI[prop] = 0;
        }

        if (!oIPlus[prop]) {
          oIPlus[prop] = 0;
        }
        if (oI[prop] > oIPlus[prop]) {
          var tmp = oIPlus;
          arr[i + 1] = oI;
          arr[i] = tmp;
        }
      }
    }
  } else {
    throw '参数类型错误';
  }

  return arr;
};

function getSortFun(order, sortBy) {
  var ordAlpah = order == 'asc' ? '>' : '<';
  var sortFun = new Function('a', 'b', 'return a.' + sortBy + ordAlpah + 'b.' + sortBy + '?1:-1');
  return sortFun;
}

function loadScript(url, cache) {
  $.ajax({
    url: url,
    dataType: 'script',
    async: false,
    cache: cache
  });
}

function parseNumber(val) {
  if (typeof val == 'undefined' || val == null || $.trim(val).length == 0) {
    val = '0';
  }
  return parseFloat(val);
}

/**
 * 所有列 column的格式为"${formId:fieldName}"
 *
 * @param column
 *            从表及列信息
 * @param $form
 *            表单form元素
 */
function sumAllColumn(column, $form) {
  if ($form) {
  } else {
    if (!arguments.callee.caller == null) {
      return 0;
    }

    if (!arguments.callee.caller.$form) {
      return 0;
    }

    $form = arguments.callee.caller.$form;
  }

  var res = /\${([^}]*)}/g.exec(column);
  var subformColumn = res[1].split(':');
  if (subformColumn.length == 0) {
    return;
  }

  var formId = subformColumn[0];
  var fieldName = subformColumn[1];

  // 获取所有的行数据
  var formdata = $form.dyform('collectSubformData', formId);

  if (typeof formdata == 'undefined' || formdata.length == 0) {
    return 0;
  }

  var sum = 0;
  for (var i = 0; i < formdata.length; i++) {
    var id = formdata[i]['uuid'];
    var control = $.ControlManager.getCtl($.dyform.getCellId(id, fieldName));
    sum += parseNumber(control.getValue());
  }
  return sum;
}

/**
 * 添加url点击事件
 */
var urlClickEvent = function (event) {
  // var reg = new RegExp(/\$\{([^\}]*)\}/g);
  var url = event.data.url;
  var formData = {};
  var dyform = event.data.dyform;
  dyform.collectMainformData(function (result) {
    formData = result || {};
    var returnParams = appContext.resolveParams(
      {
        result: url
      },
      formData
    );
    window.open(returnParams.result);
  }, $.noop);
  // var tempValueArray = url.match(reg);
  // if (typeof tempValueArray != "undefined" && tempValueArray != null) {
  // for (var k = 0; k < tempValueArray.length; k++) {
  // var tempvariable = tempValueArray[k].replace("${", "").replace("}", "");
  // var control = $.ControlManager.getCtl(tempvariable);
  // if (control != undefined) {
  // url = url.replace(tempValueArray[k], control.getValue());
  // } else if (tempvariable == 'datauuid') {
  //
  // } else if (tempvariable.indexOf('sys') > -1) {// 系统一些变量
  // }
  // }
  // }
};

var loadPersonInfo = function (personId) {
  var res = {};
  var model = null;
  var url = contextPath + '/dyform/loadOrg';
  $.ajax({
    url: url,
    type: 'POST',
    data: {
      userId: personId
    },
    dataType: 'json',
    async: false,
    timeout: 120000,
    // contentType:'',
    beforeSend: function () {
      pageLock('show');
    },
    complete: function () {
      pageLock('hide');
    },
    success: function (result) {
      /*
       * if(result.success == "true" || result.success == true){ //
       * $("#"+$(window.opener.document.getElementById("tt")).attr("id")).trigger('reloadGrid');
       * //window.opener.location.reload();//刷新父窗口页面 //window.close();
       * //window.location.href = contextPath +
       * "/dyformmodel/openDisplayModel?uuid=" + result.data + ""; model =
       * result.data ; }else{ alert("获取显示单据信息失败"); }
       */
      // console.log(JSON.cStringify(result));
      res = result;
    },
    error: function (result) {
      var responseText = result.responseText;
      try {
        // var errorObj = eval("(" + responseText + ")");
        // alert("保存失败\n" +errorObj.data);
        console.error('加载个人信息失败');
      } catch (e) {
        // alert(JSON.cStringify(result));
      }

      // console.log(JSON.cStringify(data));
    }
  });
  return res;
};

function isIE8() {
  if (typeof $ != 'undefined' && $.browser && $.browser.msie && $.browser.version < 9) {
    return true;
  }
  if (typeof navigator == 'undefined') {
    return false;
  }
  var browser = navigator.appName;
  var b_version = navigator.appVersion;
  var version = b_version.split(';');
  if (version.length < 2) {
    return false;
  }
  var trim_Version = version[1].replace(/[ ]/g, '');
  if (browser == 'Microsoft Internet Explorer' && trim_Version == 'MSIE6.0') {
    return false;
  } else if (browser == 'Microsoft Internet Explorer' && trim_Version == 'MSIE7.0') {
    return false;
  } else if (browser == 'Microsoft Internet Explorer' && trim_Version == 'MSIE8.0') {
    return true;
  } else if (browser == 'Microsoft Internet Explorer' && trim_Version == 'MSIE9.0') {
    return false;
  }
  return false;
}

JSON.cStringify = function (obj) {
  if (typeof obj != 'object') {
    return obj;
  }
  var ie8 = false;

  ie8 = isIE8();

  if (ie8) {
    // ie8
    // eval("var str = '" + JSON.stringify(obj) + "';");

    return JSONUtil.encode(obj);
  } else {
    return JSON.stringify(obj);
  }
};

if (isIE8()) {
  // 对于ie8重写JSON.stringify
  JSON.stringify = JSON.cStringify;
}

/**
 * JSON 解析类
 */

var JSONUtil;
if (!JSONUtil) {
  JSONUtil = {};
}
JSONUtil.decode = function (json) {
  try {
    return eval('\u0028' + json + '\u0029');
  } catch (exception) {
    return eval('\u0075\u006e\u0064\u0065\u0066\u0069\u006e\u0065\u0064');
  }
};

JSONUtil.encode = (function () {
  var $ = !!{}.hasOwnProperty,
    _ = function ($) {
      return $ < 10 ? '0' + $ : $;
    },
    A = {
      '\b': '\\b',
      '\t': '\\t',
      '\n': '\\n',
      '\f': '\\f',
      '\r': '\\r',
      '"': '\\"',
      '\\': '\\\\'
    };
  return function (C) {
    // undefined
    if (typeof C == '\u0075\u006e\u0064\u0065\u0066\u0069\u006e\u0065\u0064' || C === null) return 'null';
    // 数组
    else if (Object.prototype.toString.call(C) === '\u005b\u006f\u0062\u006a\u0065\u0063\u0074\u0020\u0041\u0072\u0072\u0061\u0079\u005d') {
      var B = ['\u005b'],
        G,
        E,
        D = C.length,
        F;
      for (E = 0; E < D; E += 1) {
        F = C[E];
        switch (typeof F) {
          case '\u0075\u006e\u0064\u0065\u0066\u0069\u006e\u0065\u0064':
          case '\u0066\u0075\u006e\u0063\u0074\u0069\u006f\u006e':
          case '\u0075\u006e\u006b\u006e\u006f\u0077\u006e':
            break;
          default:
            if (G) B.push('\u002c');
            B.push(F === null ? 'null' : this.encode(F));
            G = true;
        }
      }
      B.push('\u005d');
      return B.join('');
      // 日期
    } else if (Object.prototype.toString.call(C) === '\u005b\u006f\u0062\u006a\u0065\u0063\u0074\u0020\u0044\u0061\u0074\u0065\u005d')
      return (
        '"' +
        C.getFullYear() +
        '-' +
        _(C.getMonth() + 1) +
        '-' +
        _(C.getDate()) +
        'T' +
        _(C.getHours()) +
        ':' +
        _(C.getMinutes()) +
        ':' +
        _(C.getSeconds()) +
        '"'
      );
    // 字符串
    else if (typeof C == '\u0073\u0074\u0072\u0069\u006e\u0067') {
      if (/["\\\x00-\x1f]/.test(C))
        return (
          '"' +
          C.replace(/([\x00-\x1f\\"])/g, function (B, _) {
            var $ = A[_];
            if ($) return $;
            $ = _.charCodeAt();
            return '\\u00' + Math.floor($ / 16).toString(16) + ($ % 16).toString(16);
          }) +
          '"'
        );
      return '"' + C + '"';
      // number
    } else if (typeof C == '\u006e\u0075\u006d\u0062\u0065\u0072') return isFinite(C) ? String(C) : 'null';
    // boolean
    else if (typeof C == '\u0062\u006f\u006f\u006c\u0065\u0061\u006e') return String(C);
    // object
    else {
      (B = ['\u007b']), G, E, F;
      for (E in C)
        if (!$ || C.hasOwnProperty(E)) {
          F = C[E];
          if (F === null) continue;
          switch (typeof F) {
            case '\u0075\u006e\u0064\u0065\u0066\u0069\u006e\u0065\u0064':
            case '\u0066\u0075\u006e\u0063\u0074\u0069\u006f\u006e':
            case '\u0075\u006e\u006b\u006e\u006f\u0077\u006e':
              break;
            default:
              if (G) B.push('\u002c');
              B.push(this.encode(E), '\u003a', this.encode(F));
              G = true;
          }
        }
      B.push('\u007d');
      return B.join('');
    }
  };
})();
String.prototype.replaceAll = function (s1, s2) {
  return this.replace(new RegExp(s1, 'gm'), s2);
};

window.JSONUtil = JSONUtil;

function openFormDefinition(type, uuid) {
  if (FormUtils.isFormTypeAsMstform(type)) {
    openMstformDefinition(uuid);
  } else if (FormUtils.isFormTypeAsPform(type)) {
    openPformDefinition(uuid);
  } else if (FormUtils.isFormTypeAsCform(type)) {
    openCformDefinition(uuid);
  } else if (FormUtils.isFormTypeAsVform(type)) {
    openVformDefinition(uuid);
  } else if (FormUtils.isFormTypeAsMform(type)) {
    openMformDefinition(uuid);
  } else {
    var errorMsg = '未知单据类型:' + type;
    alert(errorMsg);
    throw new Error(errorMsg);
  }
}

function openPformDefinition(uuid) {
  if (uuid) {
    var url = ctx + '/pt/dyform/definition/pform-designer?uuid=' + uuid;
  } else {
    var url = ctx + '/pt/dyform/definition/pform-designer';
  }
  window.open(url);
}

function openCformDefinition(uuid) {
  if (uuid) {
    var url = ctx + '/pt/dyform/definition/cform-designer?uuid=' + uuid;
  } else {
    var url = ctx + '/pt/dyform/definition/cform-designer';
  }
  window.open(url);
}

function openVformDefinition(uuid) {
  if (uuid) {
    var url = ctx + '/pt/dyform/definition/vform-designer?uuid=' + uuid;
  } else {
    var url = ctx + '/pt/dyform/definition/vform-designer';
  }

  window.open(url);
}

function openMformDefinition(uuid) {
  if (uuid) {
    var url = ctx + '/pt/dyform/definition/mform-designer?uuid=' + uuid;
  } else {
    var url = ctx + '/pt/dyform/definition/mform-designer';
  }

  window.open(url);
}

function openMstformDefinition(uuid) {
  if (uuid) {
    var url = ctx + '/pt/dyform/definition/mstform-designer?uuid=' + uuid;
  } else {
    var url = ctx + '/pt/dyform/definition/mstform-designer';
  }
  window.open(url);
}

var FormUtils = {
  isFormTypeAsPform: function (formType) {
    if ('P'.equalsIgnoreCase(formType)) {
      return true;
    } else {
      return false;
    }
  },
  isFormTypeAsCform: function (formType) {
    if ('C'.equalsIgnoreCase(formType)) {
      return true;
    } else {
      return false;
    }
  },
  isFormTypeAsVform: function (formType) {
    if ('V'.equalsIgnoreCase(formType)) {
      return true;
    } else {
      return false;
    }
  },
  isFormTypeAsMform: function (formType) {
    if ('M'.equalsIgnoreCase(formType)) {
      return true;
    } else {
      return false;
    }
  },
  isFormTypeAsMstform: function (formType) {
    if ('MST'.equalsIgnoreCase(formType)) {
      return true;
    } else {
      return false;
    }
  },
  openFormDefinition: openFormDefinition,
  loadFormDefinition: loadFormDefinition
};
