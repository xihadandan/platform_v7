$(function () {
  var impBtn = "#B004027";
  var expBtn = "#B004028";
  if ($(impBtn)) {
    $(impBtn).show();
    $(impBtn).click(function () {
      excelImp4MainForm();
    });
  }
  if ($(expBtn)) {
    $(expBtn).show();
    $(expBtn).click(function () {
      excelExp4MainFormEx();
    });
  }
  if ($("#uploadExcelFileDiv")) {
    $("#uploadExcelFileDiv").remove();//如果移除文件节点
  };
  var filediv = $('<div id="uploadExcelFileDiv" title="" style="display: none">'
    + '<form action="" id="excel_import_form" name="excel_import_form" enctype="multipart/form-data" method="post">'
    + '<table><tr><td><label>选择XLS文件：</label></td><td><div>'
    + '<input type="file" name="upload" id="uploadexcelfile" />'
    + '</div></td></tr></table></form></div>');
  filediv.appendTo(document.body);
});

var FORM_UUID = "formUuid";
var FORM_DATAS = "formDatas";
var DELETED_FORM_DATAS = "deletedFormDatas";
function getFormUuid(formData) {
  return formData[FORM_UUID];
};
function getFormDatas(formData) {
  return formData[FORM_DATAS];
};
function getMainFormDatas(formData) {
  return getFormDatas(formData)[getFormUuid(formData)];
};
function getMainFormData(formData) {
  return getMainFormDatas(formData)[0];
};

function closeUploadDialog() {
  $("#uploadExcelFileDiv").dialog("close");
}

function get$FormEl(formEl) {
  if (typeof formEl == "undefined" || formEl == null || $("#" + formEl).length == 0) {
    return $(".dyform");
  } else {
    return $("#" + formEl);
  }

}

/**
 * formEl元素ID,表单为工作流dyform
 */
function excelImp4MainForm(formEl) {
  var $formEl = get$FormEl(formEl);




  $("#uploadExcelFileDiv").dialog({
    autoOpen: true,
    height: 200,
    width: 400,
    modal: true,
    buttons: {
      "确定": function () {
        var file = $("#uploadexcelfile").val();
        if (file == '') {
          oAlert2("请选择XLS或XLSX文件");
          return;
        }
        if (file.indexOf(".") < 0) {
          return;
        }
        var fileType = file.substring(file.lastIndexOf("."), file.length).toLowerCase();
        if (fileType == ".xls" || fileType == ".xlsx") {
          $.ajaxFileUpload({
            url: ctx + "/basicdata/excel/parseJSONS",// 链接到服务器的地址
            secureuri: false,
            fileElementId: 'uploadexcelfile',// 文件选择框的ID属性
            dataType: 'json', // 服务器返回的数据格式(不设置会自动处理)
            success: function (data, status) {
              if (data != undefined) {
                data = jQuery.parseJSON(jQuery(data).text().replace(/\n/g, "<br>"));//处理黏贴\n的特殊字符,"&ltbr&gt"
                var formId = data.formUuid;
                try {
                  $formEl.dyform("fillFormDisplayDatas", data.formDatas, formId);
                } catch (e) {
                  oAlert2("写入失败,请检查需要导入数据是否正确.");
                }
              };
              closeUploadDialog();
            },
            error: function (data) {
              oAlert2("导入失败,文件模板不匹配");
            }
          });
        } else {
          oAlert2("请选择XLS或XLSX文件");
          return;
        };
      },
      "取消": function () {
        $(this).dialog("close");
      },
    }
  });
};
/**
 * formEl元素ID,表单为工作流dyform(建议使用Ex)
 */



function excelExp4MainForm(formEl, formId) {
  // oAlert2(excelExp4MainForm);
  var $formEl = get$FormEl(formEl);


  if (typeof formId == "undefined" || formId == null) {
    formId = $formEl.dyform("getFormId");
  };
  /**
  var validateForm = $("#"+formEl).dyform("validateForm");
  if(validateForm == false){
    oAlert2("验证失败");
    return;
  };
  */
  var formDisplayData = $formEl.dyform("collectFormDisplayData");
  formDisplayData["formId"] = formId;//用户导出模版 ;//
  $("#uploadExcelFileDiv").dialog({
    autoOpen: true,
    height: 200,
    width: 400,
    modal: true,
    buttons: {
      "确定": function () {
        var file = $("#uploadexcelfile").val();
        if (file == '') {
          oAlert2("请选择XLS或XLSX文件");
          return;
        }
        if (file.indexOf(".") < 0) {
          return;
        }
        var fileType = file.substring(file.lastIndexOf("."), file.length).toLowerCase();
        if (fileType == ".xls" || fileType == ".xlsx") {
          $.ajaxFileUpload({
            url: ctx + "/basicdata/excel/parseExcel",// 链接到服务器的地址
            type: "post",
            secureuri: false,
            fileElementId: 'uploadexcelfile',// 文件选择框的ID属性
            data: { excelJsonObejct: JSON.cStringify(formDisplayData) },
            dataType: 'json', // 服务器返回的数据格式
            success: function (data) {
              // data = jQuery.parseJSON(jQuery(data).text());
              oAlert2(jQuery(data).text());
              closeUploadDialog();
            },
            error: function (data, status, e) {
              oAlert2("导出失败,文件模板不匹配");
            }
          });
        } else {
          oAlert2("请选择XLS或XLSX文件");
          return;
        }
      },
      "取消": function () {
        $(this).dialog("close");
      }
    }
  });
};

function excelExp4MainFormEx(formEl, formId) {
  var $formEl = get$FormEl(formEl);


  if (typeof formId == "undefined" || formId == null) {
    formId = $formEl.dyform("getFormId");
  };
  /** 不校验
  var validateForm = $("#"+formEl).dyform("validateForm");
  if(validateForm == false){
    oAlert2("验证失败");
    return;
  };
  */
  var formDisplayData = $formEl.dyform("collectFormDisplayData");
  formDisplayData["formId"] = formId;//用户导出模版 ;//
  var excelExpForm = $("<form>");//定义一个form表单
  excelExpForm.attr("style", "display:none");
  excelExpForm.attr("target", "_black");
  excelExpForm.attr("method", "post");
  var url = getBackendUrl() + "/basicdata/excel/parseExcelEx";
  var _auth = getCookie('_auth');
  if (_auth) {
    url += '?' + _auth + '=' + getCookie(_auth);
  }
  excelExpForm.attr("action", url);
  var paramInput = $("<input>");
  paramInput.attr("type", "hidden");
  paramInput.attr("name", "excelJsonObejct");
  paramInput.attr("value", JSON.cStringify(formDisplayData));//.replace(/<br>/g,"\n")
  $("body").append(excelExpForm);//将表单放置在web中
  excelExpForm.append(paramInput);
  excelExpForm.submit();//表单提交
  paramInput.remove();
  excelExpForm.remove();
};

function downExcelTemplate(formId) {
  if (typeof formId == "undefined" || formId == null) {
    return null;
  };
  var excelExpForm = $("<form>");//定义一个form表单
  excelExpForm.attr("style", "display:none");
  excelExpForm.attr("target", "_black");
  excelExpForm.attr("method", "post");
  var url = getBackendUrl() + "/basicdata/excel/downTemplate?formId=" + formId;
  var _auth = getCookie('_auth');
  if (_auth) {
    url += '?' + _auth + '=' + getCookie(_auth);
  }
  excelExpForm.attr("action", url);
  $("body").append(excelExpForm);//将表单放置在web中
  excelExpForm.submit();//表单提交
  excelExpForm.remove();
}


/**
 *
 * @param formId 从表单UUID
 */
function excelImp4SubForm(formId) {
  excelImp4MainForm();
};

/**
 *
 * @param formEl 从表单UUID
 */
function excelExp4SubForm(formId) {
  excelExp4MainForm("dyform", formId);
};

