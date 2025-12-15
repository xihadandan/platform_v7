require(['constant', 'commons', 'appContext', 'appModal', 'DyformExplain', 'DyformFunction', 'global'], function (
  constant,
  commons,
  appContext,
  appModal,
  DyformExplain,
  DyformFunction
) {
  // 流程二开测试
  var FORM_UUID = 'formUuid';
  var FORM_DATAS = 'formDatas';
  var DELETED_FORM_DATAS = 'deletedFormDatas';

  function getFormUuid(formData) {
    return formData[FORM_UUID];
  }

  function getFormDatas(formData) {
    return formData[FORM_DATAS];
  }

  function getMainFormDatas(formData) {
    return getFormDatas(formData)[getFormUuid(formData)];
  }

  function getMainFormData(formData) {
    return getMainFormDatas(formData)[0];
  }

  //保存前后事件处理
  function saveEventHandler(event_type) {
    var _self = this;
    var result;
    var formDefinition = dyformData.formDefinition;
    if (formDefinition.events && formDefinition.events != '') {
      var dyformEvents = eval('(' + formDefinition.events + ')');
      if (dyformEvents[event_type]) {
        appContext.eval(dyformEvents[event_type], this, { data: formData.formDatas[dyformData.formUuid][0] }, function (v) {
          result = v;
        });
      }
    }
    return result;
  }
  var getAccessor = function (obj, expr) {
    var ret,
      p,
      prm = [],
      i;
    if (typeof expr === 'function') {
      return expr(obj);
    }
    ret = obj[expr];
    if (ret === undefined) {
      try {
        if (typeof expr === 'string') {
          prm = expr.split('.');
        }
        i = prm.length;
        if (i) {
          ret = obj;
          while (ret && i--) {
            p = prm.shift();
            ret = ret[p];
          }
        }
      } catch (e) {}
    }
    return ret;
  };

  // 表单定义页面中的预览功能
  if (formDefinitionFromDefinitionModule) {
    // 表单定义预览功能

    var fo = formDefinitionFromDefinitionModule;
    fo.enableSignature = signature.disable;
    try {
      $('#dyform_title').text(fo.name + '(解析)');
    } catch (e) {}

    $('#abc').dyform({
      formData: {
        formDefinition: fo,
        formDatas: {}
      },
      displayAsFormModel: fo.displayAsFormModel,
      optional: {
        isFirst: true
      },
      success: function () {
        console.log('表单解析完毕');
      },
      error: function (msg) {
        oAlert(msg);
      },
      afterSetData: function () {
        console.log('afterSetData======>');
      }
    });

    $('#demoBtn').hide();
    return;
  }

  console.log('dyform_demo from here');
  var time1 = new Date().getTime();
  var dataUuid = $('#dataUuid').val();
  var formuuid = $('#formUuid').val();
  dyformData = DyformFunction.loadFormDefinitionData(formuuid, dataUuid);
  var time2 = new Date().getTime();
  console.log('loadFormDefinitionData:' + (time2 - time1) / 1000.0 + 's');

  if (typeof dyformData == 'string') {
    dyformData = eval('(' + formDatas + ')');
  }
  var titleElem = document.getElementsByTagName('dyform_title').item(0);
  var formDefinition = eval('(' + dyformData.formDefinition + ')');
  var text = formDefinition.displayName + '(解析)';

  try {
    $(titleElem).text(text); // ie8不兼容
  } catch (e) {}
  var time3 = new Date().getTime();
  console.log('获取数据:' + (time3 - time1) / 1000.0 + 's');

  var DyformExplain = require('DyformExplain');

  var dyformExplain1 = new DyformExplain('#abc', {
    // var dyformExplain1 = DyformExplain({
    // var dyformExplain1 = $("#abc").DyformExplain({
    // var dyformExplain1 = new DyformExplain({
    renderTo: '#abc',
    namespace: 'ns1',
    formData: dyformData,
    displayAsLabel: formDefinition.editForm === false, // 显示为文本
    async: false, // false为同步,true为异步,默认为false
    optional: {
      isFirst: true
    },
    displayAsFormModel: false, // displayAsLabel为true的前提下该参数才有效,默认为true
    // false:表示不用显示单据,true:使用显示单据,这时该若找不到对应的显示单据，则直接以该表单的模板做为显示单据

    success: function () {
      console.log('表单解析完毕');
      var formUuid2, dataUuid2;
      if (window.Browser && (formUuid2 = Browser.getQueryString('formUuid2'))) {
        $('#abc').append('<div class="dyform" id="f2"></div>');

        // $("#demoBtn #save").after("<input id=\"save2\" type=\"button\" value=\"保存2\" style=\"margin-left: 8px;\">");
        $('#demoBtn #save').after('<input id="collectFormData2" type="button" value="收集数据2" style="margin-left: 8px;">');
        $('#demoBtn #save').after('<input id="validate2" type="button" value="校验2" style="margin-left: 30px;">');

        dataUuid2 = Browser.getQueryString('dataUuid2');
        var dyformData2 = DyformFunction.loadFormDefinitionData(formUuid2, dataUuid2);
        window.dyformExplain2 = new DyformExplain('.dyform#f2', {
          renderTo: '.dyform#f2',
          namespace: 'ns2',
          formData: dyformData2,
          displayAsLabel: false, // 显示为文本
          displayAsFormModel: false, // displayAsLabel为true的前提下该参数才有效,默认为true
          success: function () {
            console.log('表单解析完毕');
          },
          error: function (msg) {
            oAlert(msg);
          }
        });
        var validateForm2 = undefined;
        $('#validate2').bind('click', function () {
          validateForm2 = dyformExplain2.validateForm();
        });
        var formData2 = undefined;
        $('#collectFormData2').click(function () {
          if (validateForm2 == undefined) {
            alert('请先验证数据');
            return;
          } else if (validateForm2 == false) {
            alert('验证失败');
            return;
          }
          dyformExplain2.collectFormData(
            function (formDatas) {
              formData2 = formDatas;
              if (typeof formDatas != 'undefined') {
                console.log('demo:\n' + JSON.cStringify(formDatas));
              }
            },
            function (errorInfo) {
              console.log(errorInfo);
            }
          );
        });
      }
    },
    error: function (msg) {
      oAlert(msg);
    },
    afterSetData: function () {
      // console.log("afterSetData======>");
      /*
       * var iconFileCtl = this.getControl("icon_file");
       * console.log(iconFileCtl.getCtlName());
       * console.table(iconFileCtl); var textInputCtl =
       * this.getControl("text_input"); console.table(textInputCtl);
       */
      /*
       * iconFileCtl.addCustomValidate(function(value, _this) { //
       * 校验规则。。。。 //...... // 返回值 console.log(value); return { valid :
       * false, tipMsg : "就是不让你提交" + new Date().format("yyyy-MM-dd
       * HH:mm:ss") } });
       */
    }
  });

  // $("#abc").dyform({
  //
  // formData : dyformData,
  // displayAsLabel : false,// 显示为文本
  // async : false,// false为同步,true为异步,默认为false
  // optional : {
  // isFirst : true
  // },
  // displayAsFormModel : false,// displayAsLabel为true的前提下该参数才有效,默认为true
  // // false:表示不用显示单据,true:使用显示单据,这时该若找不到对应的显示单据，则直接以该表单的模板做为显示单据
  //
  // success : function() {
  // console.log("表单解析完毕");
  // // SQRMC
  // /*
  // * var formDatas = dyformData.formDatas; for(var i in formDatas){
  // * var formDatasOfOneForm = formDatas[i]; for(var j in
  // * formDatasOfOneForm){ var formData = formDatasOfOneForm[j];
  // * for(var k in formData){ if(k == "SJZZSL"){ //console.log(k);
  // * formData[k] = "2"; } } } }
  // *
  // * $("#abc").dyform("refreshFormDatas", dyformData, function(){
  // * console.log("刷新完毕"); });
  // */
  // },
  // error : function(msg) {
  // oAlert(msg);
  // }
  // });

  // $("#abc").dyform("setColumnCtl", "ky", "wdjs1");
  // setColumnCtl:function(formId, mappingName, controlable)
  // $("#abc").dyform("setColumnCtl", formId, mappingName);

  // 在这里直接用parseForm接口代码

  // console.log("开始填充数据");
  /*
   * var dataUuid = $("#dataUuid").val(); if(typeof dataUuid != "undefinition" &&
   * dataUuid.length > 0){ formDatas = loadFormData($("#formUuid").val(),
   * dataUuid); $("#abc").dyform("fillFormData", formDatas, function(){
   * console.log("数据填充完成"); }); }
   */

  var validateForm = undefined;
  $('#validate').bind('click', function () {
    // DyformExplain.getAccessor
    validateForm = dyformExplain1.validateForm();
  });

  // 清空附件测试
  $('#clearAllFile').click(function () {
    var files = [];
    dyformExplain1.setFieldValue('testattach', files);
    $('#abc').dyform('setFieldValue', 'xq_mc', 'aaaa');
  });

  var formData = undefined;
  var formDisplayData = undefined;

  $('#collectFormData').click(function () {
    if (validateForm == undefined) {
      alert('请先验证数据');
      return;
    } else if (validateForm == false) {
      alert('验证失败');
      return;
    }

    dyformExplain1.collectFormData(
      function (formDatas) {
        formData = formDatas;
        if (typeof formDatas != 'undefined') {
          console.log('demo:\n', formDatas);
        }
      },
      function (errorInfo) {
        console.log(errorInfo);
      }
    );

    // formDisplayData = $("#abc").dyform("collectFormDisplayData");
    // if(typeof formDisplayData != "undefined"){
    // console.log(JSON.cStringify(formDisplayData));
    // }
  });

  $('#save').click(function () {
    if (validateForm == undefined) {
      alert('请先验证数据');
      return;
    } else if (validateForm == false) {
      alert('验证失败');
      return;
    } else if (formData == undefined) {
      alert('请先收集数据');
      return;
    }
    var result = saveEventHandler('beforeSave_event');
    if (result == false) {
      return;
    }
    var url = ctx + '/pt/dyform/data/saveFormData';
    // console.log( JSON.cStringify(formData));
    function saveAction(formData) {
      $.ajax({
        url: url,
        type: 'POST',
        data: JSON.cStringify(formData),
        dataType: 'json',
        contentType: 'application/json',
        success: function (result) {
          if (result.success == 'true' || result.success == true) {
            alert('数据保存成功dataUuid=' + result.data);
            var dataUuid = result.data;
            var formUuid = $('#formUuid').val();
            var url = ctx + '/pt/dyform/definition/open?formUuid=' + formUuid + '&dataUuid=' + dataUuid;
            window.location.href = url;
          } else {
            alert('数据保存失败');
          }
        },
        error: function (jqXHR) {
          var options = {
            formData: formData,
            dyformDataOptions: function () {
              formData.dyformDataOptions = formData.dyformDataOptions || {};
              return {
                getOptions: function (key) {
                  return formData.dyformDataOptions[key];
                },
                putOptions: function (key, value) {
                  return (formData.dyformDataOptions[key] = value);
                }
              };
            },
            callback: function () {
              saveAction(formData);
            }
          };
          if (dyformExplain1.handleError(jqXHR.responseText, options)) {
            // return;
          }
          // alert("数据保存失败");
        }
      });
    }
    saveAction(formData);
    var afterSaveResult = saveEventHandler('afterSave_event');
    if (afterSaveResult == false) {
      return;
    }
  });

  $('#validateFormData').click(function () {
    dyformExplain1.collectFormData(
      function (formDatas) {
        if (typeof formDatas != 'undefined') {
          console.log('demo:\n' + JSON.cStringify(formDatas));
        }
        var validateFormData = formDatas;
        var url = ctx + '/pt/dyform/data/validateFormData';
        $.ajax({
          url: url,
          type: 'POST',
          data: JSON.cStringify(validateFormData),
          dataType: 'json',
          contentType: 'application/json',
          success: function (result) {
            if (result.success == 'true' || result.success == true) {
              console.log(result.data);
            } else {
              alert('数据校验失败');
            }
          },
          error: function (data) {
            alert('数据校验失败');
          }
        });
      },
      function (errorInfo) {
        console.log(errorInfo);
      }
    );
  });

  //
  $('#excelImp').click(function () {
    excelImp4MainForm('abc');
    return false;

    $('#uploadFileDiv').dialog({
      autoOpen: true,
      height: 200,
      width: 400,
      modal: true,
      buttons: {
        确定: function () {
          var file = $('#uploadfile').val();
          if (file == '') {
            alert('请选择xls文件');
            return;
          }
          if (file.indexOf('.') < 0) {
            return;
          }
          var fileType = file.substring(file.lastIndexOf('.'), file.length);
          if (fileType == '.xls' || fileType == '.xlsx') {
            $.ajaxFileUpload({
              url: ctx + '/basicdata/excel/parseJSONS', // 链接到服务器的地址
              secureuri: false,
              fileElementId: 'uploadfile', // 文件选择框的ID属性
              // dataType : 'json', // 服务器返回的数据格式(不设置会自动处理)
              success: function (data, status) {
                if (data != undefined) {
                  data = jQuery.parseJSON(jQuery(data).text());
                  // alert(JSON.cStringify(data));
                  // var formDatas = data.formDatas;
                  // $("#abc").dyform("fillFormDatas",formDatas);
                  // $("#abc").dyform("fillFormDataOfMainform",getMainFormData(data));//主表单独设置
                  // $("#abc").dyform("fillFormDatas",getFormDatas(data));

                  // var formId =
                  // $("#abc").dyform("getFormId");
                  var formId = data.formUuid;
                  $('#abc').dyform('fillFormDisplayDatas', data.formDatas, formId);
                }
              },
              error: function (data) {
                alert('导入失败');
              }
            });
          } else {
            alert('请选择xls文件');
            return;
          }
        },
        取消: function () {
          $(this).dialog('close');
        }
      }
    });
  });

  $('#excelExp').click(function () {
    excelExp4MainFormEx('abc');
    return false;
    if (validateForm == undefined) {
      alert('请先验证数据');
      return;
    } else if (validateForm == false) {
      alert('验证失败');
      return;
    } else if (formDisplayData == undefined) {
      alert('请先收集数据');
      return;
    }
    $('#uploadFileDiv').dialog({
      autoOpen: true,
      height: 200,
      width: 400,
      modal: true,
      buttons: {
        确定: function () {
          var file = $('#uploadfile').val();
          if (file == '') {
            alert('请选择xls文件');
            return;
          }
          if (file.indexOf('.') < 0) {
            return;
          }
          var fileType = file.substring(file.lastIndexOf('.'), file.length);
          if (fileType == '.xls' || fileType == '.xlsx') {
            $.ajaxFileUpload({
              url: ctx + '/basicdata/excel/parseExcel', // 链接到服务器的地址
              type: 'post',
              secureuri: false,
              fileElementId: 'uploadfile', // 文件选择框的ID属性
              data: {
                paramObject: JSON.cStringify(formDisplayData)
              },
              dataType: 'json', // 服务器返回的数据格式
              success: function (data) {
                // data = jQuery.parseJSON(jQuery(data).text());
                alert(jQuery(data).text());
              },
              error: function (data, status, e) {
                alert('导入失败');
              }
            });
          } else {
            alert('请选择xls文件');
            return;
          }
        },
        取消: function () {
          $(this).dialog('close');
        }
      }
    });
  });

  $('#showAsLabel').click(function () {
    $('#abc').dyform('showAsLabel');
  });
  $('#setEditable').click(function () {
    $('#abc').dyform('setEditable');
  });
  $('#setAllowDownload').click(function () {
    $('#abc').dyform('setAllowDownload');
  });
  $('#setNoAllowDownload').click(function () {
    $('#abc').dyform('setNoAllowDownload');
  });
  $('#setReadOnly').click(function () {
    $('#abc').dyform('setReadOnly');
  });
  $('#setTextFile2SWF').click(function () {
    var val = $(this).attr('flag');
    if (typeof val == 'undefined' || val == 'false') {
      $(this).attr('flag', 'true');
      $('#abc').dyform('setTextFile2SWF', true);
    } else {
      $(this).attr('flag', 'false');
      $('#abc').dyform('setTextFile2SWF', false);
    }
  });
  $('#enableSignature').click(function () {
    var val = $(this).attr('flag');
    // console.log("ttt-->" + val);
    if (typeof val == 'undefined' || val == 'false') {
      $(this).attr('flag', 'true');
      $('#abc').dyform('enableSignature', true);
    } else {
      $(this).attr('flag', 'false');
      $('#abc').dyform('enableSignature', false);
    }
  });

  $('#addRowData').click(function () {
    $('#abc').dyform('getSubformControl', 'bfa0c271-0412-4c89-ba61-884d5c6f3fa6').addRowData({});
    // $("#abc").dyform("getSubformControl","cf00247d-fed2-451c-bbeb-eb5e67c4a94d"
    // ).addRowData({});
    // $("#abc").dyform("addRowData", "userform_receipt", {});
    // $("#abc").dyform("hideSubform", "userform_receipt");
  });

  $('#hideSubform').click(function () {
    // $("#abc").dyform("getSubformControl","cf00247d-fed2-451c-bbeb-eb5e67c4a94d"
    // ).addRowData({});
    // $("#abc").dyform("getSubformControl","cf00247d-fed2-451c-bbeb-eb5e67c4a94d"
    // ).addRowData({});
    // $("#abc").dyform("addRowData", "userform_receipt", {});
    $('#abc').dyform('hideSubFormByFormUuid', 'cf00247d-fed2-451c-bbeb-eb5e67c4a94d');

    $('#abc').dyform('hideSubForm', 'userform_receipt');
  });

  $('#setFieldAsHide').click(function () {
    $('#abc').dyform('setFieldAsHide', 'dy_work_task_apply_name');
  });

  $('#setFieldAsShow').click(function () {
    $('#abc').dyform('setFieldAsShow', 'dy_work_task_apply_name');
  });

  $('#setFieldReadOnly').click(function () {
    $('#abc').dyform('setFieldReadOnly', 'dy_work_task_apply_name');
  });

  $('#setFieldEditable').click(function () {
    $('#abc').dyform('setFieldEditable', 'dy_work_task_apply_name');
  });

  $('#setFieldAsLabel').click(function () {
    $('#abc').dyform('setFieldAsLabel', 'dy_work_task_apply_name');
  });

  $('#groupSubform').click(function () {
    $('#abc').dyform('group', 'uf_test_20140717');
  });

  $('#clickEvent').click(function () {
    $('#abc').dyform('setFieldValue', 'FILE_APPROVAL_OPINION', '这里有点击事件');
    $('#abc').dyform('bind2Control', {
      type: 'click',
      mappingName: 'FILE_APPROVAL_OPINION',
      // dataUuid: ""
      callback: function () {
        alert('FILE_APPROVAL_OPINION被点击了');
      }
    });
  });

  $('#afterSelect').click(function () {
    $('#abc').dyform('bind2Control', {
      type: 'afterDialogSelect',
      mappingName: 'dy_work_task_apply_begin_time',
      // dataUuid: ""
      callback: function (paramsId, paramsObj) {
        console.log('paramsId=' + JSON.cStringify(paramsId));
        console.log('paramsObj=' + JSON.cStringify(paramsObj));
      }
    });
  });

  $('#hideColumn').click(function () {
    $('#abc').dyform('hideColumn', 'uf_xzsp_material_def', 'File_title');
  });

  $('#showColumn').click(function () {
    $('#abc').dyform('showColumn', 'uf_xzsp_material_def', 'File_title');
  });

  $('#columnReadonly').click(function () {
    $('#abc').dyform('setColumnReadOnly', 'uf_xzsp_material_def', 'File_title');
  });

  $('#columnEditable').click(function () {
    $('#abc').dyform('setColumnEditable', 'uf_xzsp_material_def', 'File_title');
  });
});
