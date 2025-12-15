define(['constant', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'AppPtMgrCommons'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  AppPtMgrCommons
) {
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;
  var Validation = server.Validation;
  var SelectiveDatas = server.SelectiveDatas;
  var templateId = 'biz-dispense-form-config';
  // 平台管理_业务流程管理_业务流程定义_业务事项表单分发配置
  var AppBizProcessDefinitionDispenseFormConfigDialog = {};

  AppBizProcessDefinitionDispenseFormConfigDialog.show = function (itemConfig) {
    var _self = this;
    var title = '表单分发配置';
    var dlgId = UUID.createUUID();
    var dlgSelector = '#' + dlgId;
    var message = "<div id='" + dlgId + "'></div>";
    var dlgOptions = {
      title: title,
      size: 'large',
      message: message,
      shown: function () {
        _self.init($(dlgSelector), itemConfig);
      },
      buttons: {
        confirm: {
          label: '确定',
          className: 'btn-primary',
          callback: function () {
            var $trList = $('tbody tr', $(dlgSelector));
            var dispenseFormConfigs = [];
            $.each($trList, function () {
              var $tr = $(this);
              dispenseFormConfigs.push({
                itemDefName: $("input[name='itemDefName']", $tr).val(),
                itemDefId: $("input[name='itemDefId']", $tr).val(),
                itemType: $("input[name='itemType']", $tr).val(),
                itemName: $("input[name='itemName']", $tr).val(),
                itemCode: $("input[name='itemCode']", $tr).val(),
                type: $("select[name='type']", $tr).val(),
                botId: $("input[name='botId']", $tr).val(),
                botName: $("input[name='botName']", $tr).val(),
                formUuid: $("input[name='formUuid']", $tr).val(),
                formName: $("input[name='formName']", $tr).val(),
                entityNameField: $("input[name='entityNameField']", $tr).val(),
                entityIdField: $("input[name='entityIdField']", $tr).val(),
                timeLimitField: $("input[name='timeLimitField']", $tr).val()
              });
            });
            itemConfig.dispenseFormConfigs = dispenseFormConfigs;
            return true;
          }
        },
        cancel: {
          label: '取消',
          className: 'btn-default'
        }
      }
    };
    appModal.dialog(dlgOptions);
  };
  AppBizProcessDefinitionDispenseFormConfigDialog.init = function ($container, itemConfig) {
    var _self = this;

    var itemDefId = itemConfig.itemDefId;
    var itemCode = itemConfig.itemCode;
    JDS.restfulGet({
      url: `/proxy/api/biz/item/definition/listIncludeItemDataByItemCode?id=${itemDefId}&itemCode=${itemCode}`,
      success: function (result) {
        var templateEngine = appContext.getJavaScriptTemplateEngine();
        var html = templateEngine.renderById(templateId, { includeItems: result.data });
        $container.html(html);
        _self.initValue($container, itemConfig);
        _self.initBot($container, result.data);
        _self.bindEvent($container, itemConfig);
      }
    });
  };
  AppBizProcessDefinitionDispenseFormConfigDialog.initValue = function ($container, itemConfig) {
    var dispenseFormConfigs = itemConfig.dispenseFormConfigs || [];
    $.each(dispenseFormConfigs, function (i, dispenseFormConfig) {
      var $tr = $('.tr-' + i, $container);
      if ($("input[name='itemCode']", $tr).val() != dispenseFormConfig.itemCode) {
        return;
      }
      $("input[name='itemDefName']", $tr).val(dispenseFormConfig.itemDefName);
      $("input[name='itemDefId']", $tr).val(dispenseFormConfig.itemDefId);
      $("input[name='itemType']", $tr).val(dispenseFormConfig.itemType);
      $("input[name='itemName']", $tr).val(dispenseFormConfig.itemName);
      $("input[name='itemCode']", $tr).val(dispenseFormConfig.itemCode);
      $("select[name='type']", $tr).val(dispenseFormConfig.type);
      $("input[name='botId']", $tr).val(dispenseFormConfig.botId);
      $("input[name='botName']", $tr).val(dispenseFormConfig.botName);
      $("input[name='formUuid']", $tr).val(dispenseFormConfig.formUuid);
      $("input[name='formName']", $tr).val(dispenseFormConfig.formName);
      $("input[name='entityNameField']", $tr).val(dispenseFormConfig.entityNameField);
      $("input[name='entityIdField']", $tr).val(dispenseFormConfig.entityIdField);
      $("input[name='timeLimitField']", $tr).val(dispenseFormConfig.timeLimitField);
    });
  };
  AppBizProcessDefinitionDispenseFormConfigDialog.initBot = function ($container, includeItems) {
    var _self = this;
    $.each(includeItems, function (i, includeItem) {
      var $tr = $('.tr-' + i, $container);
      // 拷贝信息转换规则
      $(`#botId_${i}`, $tr).wSelect2({
        serviceName: 'botRuleConfFacadeService',
        queryMethod: 'loadSelectData',
        labelField: `botName_${i}`,
        valueField: `botId_${i}`,
        placeholder: '请选择',
        multiple: false,
        remoteSearch: false
      });
      $(`#botId_${i}`, $tr)
        .on('change', function () {
          var botId = $(`#botId_${i}`, $container).val();
          if (StringUtils.isNotBlank(botId)) {
            JDS.call({
              service: 'botRuleConfService.getById',
              data: [botId],
              success: function (result) {
                var targetFormUuid = result.data.targetObjId;
                $("input[name='formUuid']", $tr).val(targetFormUuid);
                _self.loadFormFields(targetFormUuid, $tr);
              }
            });
          } else {
            _self.initFormFields({ fields: {} }, $tr);
          }
        })
        .trigger('change');
    });
  };
  AppBizProcessDefinitionDispenseFormConfigDialog.loadFormFields = function (targetFormUuid, $container) {
    var _self = this;
    JDS.restfulGet({
      url: `/proxy/api/biz/process/definition/getFormDefinitionByFormUuid/${targetFormUuid}`,
      success: function (result) {
        var formDefinition = JSON.parse(result.data);
        _self.initFormFields(formDefinition, $container);
      }
    });
  };
  AppBizProcessDefinitionDispenseFormConfigDialog.initFormFields = function (formDefinition, $container) {
    var _self = this;
    var getFieldSelectData = function (formDefinition) {
      var selectData = [];
      var fields = formDefinition.fields || {};
      $.each(fields, function (i, field) {
        selectData.push({ id: field.name, text: field.displayName });
      });
      return selectData;
    };

    var fieldSelectData = getFieldSelectData(formDefinition);
    // 业务主体名称字段
    $('input[name="entityNameField"]', $container).wSelect2({
      defaultBlank: true, // 默认空选项
      remoteSearch: false,
      data: fieldSelectData,
      width: '100%',
      height: 250
    });
    // 业务主体ID字段
    $('input[name="entityIdField"]', $container).wSelect2({
      defaultBlank: true, // 默认空选项
      remoteSearch: false,
      data: fieldSelectData,
      width: '100%',
      height: 250
    });
    // 事项办理信息显示位置
    $('input[name="timeLimitField"]', $container).wSelect2({
      defaultBlank: true, // 默认空选项
      remoteSearch: false,
      data: fieldSelectData,
      width: '100%',
      height: 250
    });
  };
  AppBizProcessDefinitionDispenseFormConfigDialog.bindEvent = function ($container, itemConfig) {
    // 类型变更
    $($container).on('change', "select[name='type']", function () {
      var type = $(this).val();
      var $tr = $(this).closest('tr');
      if (type == '1') {
        $('.type-1', $tr).hide();
        $('.form-name', $tr).text(itemConfig.formName);
        $("input[name='formName']", $tr).val(itemConfig.formName);
        $("input[name='formUuid']", $tr).val(itemConfig.formUuid);
      } else {
        $('.type-1', $tr).show();
      }
    });
    $("select[name='type']", $container).trigger('change');
  };
  return AppBizProcessDefinitionDispenseFormConfigDialog;
});
