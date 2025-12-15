define(['jquery', 'constant', 'commons', 'server', 'appContext', 'appModal'], function (
  $,
  constant,
  commons,
  server,
  appContext,
  appModal
) {
  // 操作服务
  var contextPath = ctx;
  var dot = constant.Separator.Dot;
  var services = {
    loadFormDefinition: contextPath + '/pt/dyform/definition/getFormDefinition',
    loadFormDefinitions: contextPath + '/pt/dyform/definition/getFormDefinitions',
    loadFormDefinitionsAndDefaultFormData: contextPath + '/pt/dyform/definition/getFormDefinitions',
    loadFormData: contextPath + '/dyformdata/getFormData',
    loadFormDefinitionData: contextPath + '/pt/dyform/data/getFormDefinitionData',
    loadFormDefinitionDataByFormId: contextPath + '/pt/dyform/data/getFormDefinitionDataByFormId'
  };
  // JSON服务
  var jds = server.JDS;
  var DyformFunction = {
    loadFormDefinition: function (uuid, successCallback, failureCallback) {
      if (uuid == '' || uuid == null || typeof uuid == 'undefined' || uuid == 'undefined') {
        return new MainFormClass();
      }
      var definitionObj = null;
      var time1 = new Date().getTime();
      $.ajax({
        url: services.loadFormDefinition,
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
          if (definitionObj != null) {
            $.extend(definitionObj, formDefinitionMethod);
          }
        },
        error: function () {
          // 加载定义失败
        }
      });
      var time2 = new Date().getTime();
      console.log('加载定义所用时间:' + (time2 - time1) / 1000.0 + 's');
      return definitionObj;
    },

    loadFormDefinitions: function (formUuids, successCallback, failureCallback) {
      if (formUuids == '' || formUuids == null || typeof formUuids == 'undefined' || formUuids == 'undefined' || formUuids.length == 0) {
        return {};
      }
      var definitionMap = [];
      var time1 = new Date().getTime();

      $.ajax({
        url: services.loadFormDefinitions,
        cache: false,
        async: false, // 同步完成
        type: 'POST',
        data: {
          formUuids: JSON.cStringify(formUuids)
        },
        dataType: 'json',
        success: function (data) {
          var definitionObjs = data;
          if (definitionObjs != null && definitionObjs.length > 0) {
            for (var i = 0; i < definitionObjs.length; i++) {
              var definitionObj = eval('(' + definitionObjs[i] + ')');
              $.extend(definitionObj, formDefinitionMethod);
              definitionMap[definitionObj.uuid] = definitionObj;
            }
          }
        },
        error: function () {
          // 加载定义失败
        }
      });

      var time2 = new Date().getTime();
      console.log('加载定义所用时间:' + (time2 - time1) / 1000.0 + 's');
      return definitionMap;
    },

    loadFormDefinitionsAndDefaultFormData: function (formUuids, formUuid, successCallback, failureCallback) {
      var datas = {};
      $.ajax({
        url: services.loadFormDefinitionsAndDefaultFormData,
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
        }
      });

      return datas;
    },

    loadFormData: function (formUuid, dataUuid, successCallback, failureCallback) {
      if (formUuid == '' || formUuid == null || typeof dataUuid == 'undefined' || dataUuid == 'undefined') {
        // 未初始化
        throw new Error('formUuid or dataUuid is not initialized');
      }

      var formDatas = null;
      $.ajax({
        url: services.loadFormData,
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
            appModal.alert(result.data);
          }
        },
        error: function (xhr) {
          // 加载定义失败
          appModal.alert(JSON.parse(xhr.responseText).data || '加载定义失败');
        }
      });
      return formDatas;
    },

    loadFormDefinitionData: function (formUuid, dataUuid, successCallback, failureCallback) {
      if (typeof dataUuid == 'undefined' || dataUuid == 'undefined') {
        // 未初始化
        dataUuid = '';
      }

      var formDatas = null;
      $.ajax({
        url: services.loadFormDefinitionData,
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
            appModal.alert(result.data);
          }
        },
        error: function (xhr) {
          // 加载定义失败
          appModal.alert(JSON.parse(xhr.responseText).data || '加载定义失败');
        }
      });
      return formDatas;
    },

    /**
     * 根据指定的dataUuid从指定的formId表单中获取表单数据
     */
    loadFormDefinitionDataByFormId: function (formId, dataUuid, successCallback, failureCallback) {
      if (typeof dataUuid == 'undefined' || dataUuid == 'undefined') {
        // 未初始化
        dataUuid = '';
      }

      var formDatas = null;
      $.ajax({
        url: services.loadFormDefinitionDataByFormId,
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
            appModal.alert(result.data);
          }
        },
        error: function (xhr) {
          // 加载定义失败
          appModal.alert(JSON.parse(xhr.responseText).data || '加载定义失败');
        }
      });
      return formDatas;
    }
  };
  return DyformFunction;
});
