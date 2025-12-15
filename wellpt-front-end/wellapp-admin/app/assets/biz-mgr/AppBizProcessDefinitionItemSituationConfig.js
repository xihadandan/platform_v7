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
  var templateId = 'biz-item-situation-config';
  // 平台管理_业务流程管理_业务流程定义_业务事项办理情形配置
  var AppBizProcessDefinitionItemSituationConfig = {};

  AppBizProcessDefinitionItemSituationConfig.show = function ($container, itemConfig) {
    var _self = this;
    var situationConfigs = itemConfig.situationConfigs || [];
    $container.data('situationConfigs', situationConfigs);
    $container.html('');
    $container.show();
    _self.init($container, itemConfig);
  };
  AppBizProcessDefinitionItemSituationConfig.hide = function ($container) {
    $container.hide();
  };
  AppBizProcessDefinitionItemSituationConfig.collect = function ($container) {
    var $trs = $('tbody tr:visible', $container);
    var situationConfigs = [];
    $.each($trs, function (i) {
      var $tr = $(this);
      situationConfigs.push({
        situationName: $("input[name='situationName']", $tr).val(),
        itemWorkday: $("input[name='itemWorkday']", $tr).val(),
        itemMaterialCodes: $("input[name='itemMaterialCodes']", $tr).val(),
        conditionName: $("input[name='conditionName']", $tr).val(),
        conditionConfigs: $("input[name='conditionName']", $tr).data('conditionConfigs') || []
      });
    });
    return situationConfigs;
  };
  AppBizProcessDefinitionItemSituationConfig.init = function ($container, itemConfig) {
    var _self = this;
    var templateEngine = appContext.getJavaScriptTemplateEngine();
    var html = templateEngine.renderById(templateId, {});
    $container.html(html);

    var itemDefId = itemConfig.itemDefId;
    var itemCode = itemConfig.itemCode;
    JDS.restfulGet({
      url: `/proxy/api/biz/item/definition/listTimeLimitAndMaterialByItemCode?id=${itemDefId}&itemCode=${itemCode}`,
      success: function (result) {
        var data = result.data;
        $container.data('timeLimitSelectData', _self.getTimeLimitSelectData(data, itemConfig));
        $container.data('materialSelectData', _self.getMaterialSelectData(data, itemConfig));
        _self.initTable($container, itemConfig);
        _self.bindEvent($container, itemConfig);
      }
    });
  };
  AppBizProcessDefinitionItemSituationConfig.getTimeLimitSelectData = function (data, itemConfig) {
    var selectData = [];
    var timeLimits = data.timeLimits || [];
    $.each(timeLimits, function (i, timeLimit) {
      var text = timeLimit.timeLimit + '个' + (itemConfig.timeLimitType == '1' ? '工作日' : '自然日');
      selectData.push({ id: timeLimit.timeLimit, text: text });
    });
    return selectData;
  };
  AppBizProcessDefinitionItemSituationConfig.getMaterialSelectData = function (data, itemConfig) {
    var selectData = [];
    var materials = data.materials || [];
    $.each(materials, function (i, material) {
      selectData.push({ id: material.materialCode, text: material.materialName });
    });
    return selectData;
  };
  AppBizProcessDefinitionItemSituationConfig.initTable = function ($container, itemConfig) {
    var _self = this;
    var situationConfigs = itemConfig.situationConfigs || [];
    $.each(situationConfigs, function (i, situationConfig) {
      _self.addRow($container, situationConfig);
    });
  };
  AppBizProcessDefinitionItemSituationConfig.bindEvent = function ($container, itemConfig) {
    var _self = this;
    // 新增
    $('#btn_item_situation_add', $container).on('click', function () {
      _self.addRow($container, {});
    });

    // 删除
    $('#btn_item_situation_delete', $container).on('click', function () {
      var $selectedRows = $('table tbody tr.selected', $container);
      if ($selectedRows.length == 0) {
        appModal.error('请选择要删除的办理情形配置！');
        return;
      }
      var $nextRow = $selectedRows.next();
      while ($nextRow.is('tr')) {
        var $rowNum = $nextRow.children('td.row-num');
        var rowNum = $rowNum.text();
        $rowNum.text(parseInt(rowNum) - 1);
        $nextRow = $nextRow.next();
      }
      $selectedRows.remove();
    });

    // 行点击选中
    $container.on('click', 'table tbody tr', function () {
      $(this).closest('tbody').find('tr').removeClass('selected');
      $(this).addClass('selected');
    });
  };
  AppBizProcessDefinitionItemSituationConfig.addRow = function ($container, rowData) {
    var _self = this;
    var $tr = $('tbody tr.template', $container).clone().removeClass('hidden').removeClass('template');
    $('tbody', $container).append($tr);
    var timeLimitSelectData = $container.data('timeLimitSelectData') || [];
    var materialSelectData = $container.data('materialSelectData') || [];
    if (!$.isEmptyObject(rowData)) {
      $("input[name='situationName']", $tr).val(rowData.situationName);
      $("input[name='itemWorkday']", $tr).val(rowData.itemWorkday);
      $("input[name='itemMaterialCodes']", $tr).val(rowData.itemMaterialCodes);
      $("input[name='conditionName']", $tr).val(rowData.conditionName);
      $("input[name='conditionName']", $tr).data('conditionConfigs', rowData.conditionConfigs || []);
    }
    // 序号
    var rows = $('tbody tr', $container).length - 1;
    $('td.row-num', $tr).text(rows);

    // 工作日
    $('input[name="itemWorkday"]', $tr).wSelect2({
      defaultBlank: true, // 默认空选项
      remoteSearch: false,
      data: timeLimitSelectData,
      width: '100%',
      height: 250
    });
    // 办理材料
    $('input[name="itemMaterialCodes"]', $tr).wSelect2({
      defaultBlank: true, // 默认空选项
      remoteSearch: false,
      multiple: true,
      data: materialSelectData,
      width: '100%',
      height: 250
    });

    $("button[name='btn_condition_config']", $tr).on('click', function () {
      var conditionName = $("input[name='conditionName']", $tr).val();
      var conditionConfigs = $("input[name='conditionName']", $tr).data('conditionConfigs');
      _self.showConditionDialog({
        conditionName,
        conditionConfigs,
        onOk: function (situationCondition) {
          $("input[name='conditionName']", $tr).val(situationCondition.conditionName);
          $("input[name='conditionName']", $tr).data('conditionConfigs', situationCondition.conditionConfigs);
        }
      });
    });
  };
  AppBizProcessDefinitionItemSituationConfig.showConditionDialog = function (options) {
    var _self = this;
    var title = '办理情形条件条件';
    var dlgOptions = {
      title: title,
      templateId: 'biz-item-situation-config-condition',
      size: 'large',
      shown: function () {
        var $container = $('.item-situation-config-condition');
        _self.initDialog($container, options);
        _self.bindDialogEvent($container);
      },
      buttons: {
        confirm: {
          label: '确定',
          className: 'btn-primary',
          callback: function () {
            var $container = $('.item-situation-config-condition');
            var $conditions = $("select[name='condition']", $container).find('option');
            var conditions = [];
            var conditionNames = [];
            $.each($conditions, function () {
              var condition = $(this).data('condition');
              conditions.push(condition);
              conditionNames.push(condition.name);
            });
            if ($.isFunction(options.onOk)) {
              options.onOk.call(_self, {
                conditionName: conditionNames.join(' '),
                conditionConfigs: conditions
              });
            }
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
  AppBizProcessDefinitionItemSituationConfig.initDialog = function ($container, options) {
    var _self = this;

    // 使用定义单
    $('#condition_formName', $container).wSelect2({
      labelField: 'condition_formName',
      valueField: 'condition_formUuid',
      serviceName: 'bizProcessDefinitionFacadeService',
      queryMethod: 'listDyFormDefinitionSelectData',
      selectionMethod: 'getDyFormDefinitionSelectDataByUuids',
      placeholder: '请选择表单',
      defaultBlank: true, // 默认空选项
      remoteSearch: false,
      width: '100%',
      height: 250
    });
    $('#condition_formName', $container)
      .on('change', function () {
        var formUuid = $('#condition_formUuid', $container).val();
        if (StringUtils.isNotBlank(formUuid)) {
          JDS.restfulGet({
            url: `/proxy/api/biz/process/definition/getFormDefinitionByFormUuid/${formUuid}`,
            success: function (result) {
              var formDefinition = JSON.parse(result.data);
              _self.initDialogFormFields(formDefinition, $container);
            }
          });
        } else {
          _self.initDialogFormFields({ fields: {}, subforms: {} }, $container);
        }
      })
      .trigger('change');

    // 操作符
    var opeatorSelectData = [
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
      },
      {
        id: 'like',
        text: '包含'
      },
      {
        id: 'notlike',
        text: '不包含'
      }
    ];
    $('#condition_operatorName', $container).wSelect2({
      labelField: 'condition_operatorName',
      valueField: 'condition_operator',
      placeholder: '请选择操作符',
      defaultBlank: true, // 默认空选项
      remoteSearch: false,
      data: opeatorSelectData,
      width: '100%',
      height: 250
    });

    if (options.conditionConfigs) {
      $.each(options.conditionConfigs, function (i, condition) {
        var $option = $('<option/>').text(condition.name).data('condition', condition);
        $("select[name='condition']", $container).append($option);
      });
    }
  };
  AppBizProcessDefinitionItemSituationConfig.initDialogFormFields = function (formDefinition, $container) {
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
    // 字段名
    $('#condition_fieldName', $container).wSelect2({
      placeholder: '请选择字段',
      defaultBlank: true, // 默认空选项
      remoteSearch: false,
      data: fieldSelectData,
      width: '100%',
      height: 250
    });
  };
  AppBizProcessDefinitionItemSituationConfig.bindDialogEvent = function ($container) {
    // 类型变更
    $("input[name='type']", $container)
      .on('change', function () {
        var type = $("input[name='type']:checked", $container).val();
        $('.condition-type', $container).hide();
        $('.condition-type-' + type, $container).show();
      })
      .trigger('change');
    var collecCondition = function () {
      var type = $("input[name='type']:checked", $container).val();
      var connector = $("select[name='connector']", $container).val();
      var connectorName = $("select[name='connector']", $container).find('option:selected').text();
      var leftBracket = $("select[name='leftBracket']:visible", $container).val();
      var formUuid = $("input[name='formUuid']", $container).val();
      var formName = $("input[name='formName']", $container).val();
      var fieldName = $("input[name='fieldName']", $container).val();
      var fieldDisplayName = $("input[name='fieldName']", $container).parent().find('.well-select-selected-value').text();
      var operator = $("input[name='operator']", $container).val();
      var operatorName = $("input[name='operatorName']", $container).val();
      var value = $("input[name='value']", $container).val();
      var rightBracket = $("select[name='rightBracket']:visible", $container).val();
      var conditionName = '';
      if (type == '3') {
        conditionName = leftBracket + formName + ':' + fieldDisplayName + ' ' + operatorName + ' ' + value + rightBracket;
      } else if (type == '9') {
        conditionName = connectorName;
      }
      return {
        name: conditionName,
        type,
        connector,
        leftBracket,
        formUuid,
        fieldName,
        operator,
        value,
        rightBracket
      };
    };
    var condition2Form = function (condition) {
      $('.condition-type', $container).hide();
      $('.condition-type-' + condition.type, $container).show();

      $("input[name='type']:checked", $container).val(condition.type);
      $("select[name='connector']", $container).val(condition.connector);
      $("select[name='leftBracket']:visible", $container).val(condition.leftBracket);
      $("input[name='formUuid']", $container).val(condition.formUuid);
      $("input[name='formName']", $container).trigger('change');
      $("input[name='fieldName']", $container).val(condition.fieldName).trigger('change');
      $("input[name='operator']", $container).val(condition.operator);
      $("input[name='operatorName']", $container).trigger('change');
      $("input[name='value']", $container).val(condition.value);
      $("select[name='rightBracket']:visible", $container).val(condition.rightBracket);
    };
    // 添加
    $('button[name="btn_add"]', $container).on('click', function () {
      var condition = collecCondition();
      var $option = $('<option/>').text(condition.name).data('condition', condition);
      $("select[name='condition']", $container).append($option);
    });

    // 更新
    $('button[name="btn_update"]', $container).on('click', function () {
      var condition = collecCondition();
      $("select[name='condition']", $container).find('option:selected').text(condition.name).data('condition', condition);
    });
    // 删除
    $('button[name="btn_delete"]', $container).on('click', function () {
      $("select[name='condition']", $container).find('option:selected').remove();
    });
    $("select[name='condition']", $container).on('click', 'option', function () {
      var condition = $(this).data('condition');
      condition2Form(condition);
    });
  };
  return AppBizProcessDefinitionItemSituationConfig;
});
