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
  var templateId = 'biz-item-workflow-bi-milestone-config';
  // 平台管理_业务流程管理_业务流程定义_业务事项工作流集成里程碑配置
  var AppBizProcessDefinitionItemWorkflowBiMilestoneConfig = {};

  AppBizProcessDefinitionItemWorkflowBiMilestoneConfig.show = function ($container, milestoneConfigs, selectData) {
    var _self = this;
    $container.data('milestoneConfigs', milestoneConfigs);
    $container.data('selectData', selectData);
    $container.html('');
    $container.show();
    _self.init($container, milestoneConfigs, selectData);
  };
  AppBizProcessDefinitionItemWorkflowBiMilestoneConfig.hide = function ($container) {
    $container.hide();
  };
  AppBizProcessDefinitionItemWorkflowBiMilestoneConfig.collect = function ($container) {
    var $trs = $('tbody tr:visible', $container);
    var milestoneConfigs = [];
    $.each($trs, function (i) {
      var $tr = $(this);
      milestoneConfigs.push({
        name: $("input[name='name']", $tr).val(),
        triggerType: $("select[name='triggerType']", $tr).val(),
        taskId: $("input[name='taskId']", $tr).val(),
        directionId: $("input[name='directionId']", $tr).val(),
        resultField: $("input[name='resultField']", $tr).val()
      });
    });
    return milestoneConfigs;
  };
  AppBizProcessDefinitionItemWorkflowBiMilestoneConfig.init = function ($container, milestoneConfigs) {
    var _self = this;
    var templateEngine = appContext.getJavaScriptTemplateEngine();
    var html = templateEngine.renderById(templateId, {});
    $container.html(html);

    _self.initTable($container, milestoneConfigs);
    _self.bindEvent($container, milestoneConfigs);
  };
  AppBizProcessDefinitionItemWorkflowBiMilestoneConfig.initTable = function ($container, milestoneConfigs) {
    var _self = this;
    $.each(milestoneConfigs, function (i, milestoneConfig) {
      _self.addRow($container, milestoneConfig);
    });
  };
  AppBizProcessDefinitionItemWorkflowBiMilestoneConfig.bindEvent = function ($container) {
    var _self = this;
    // 新增
    $('#btn_milestone_add', $container).on('click', function () {
      _self.addRow($container, {});
    });

    // 删除
    $('#btn_milestone_delete', $container).on('click', function () {
      var $selectedRows = $('table tbody tr.selected', $container);
      if ($selectedRows.length == 0) {
        appModal.error('请选择要删除的里程碑配置！');
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
  AppBizProcessDefinitionItemWorkflowBiMilestoneConfig.addRow = function ($container, rowData) {
    var _self = this;
    var $tr = $('tbody tr.template', $container).clone().removeClass('hidden').removeClass('template');
    $('tbody', $container).append($tr);
    var selectData = $container.data('selectData') || {};
    if (!$.isEmptyObject(rowData)) {
      $("input[name='name']", $tr).val(rowData.name);
      $("select[name='triggerType']", $tr).val(rowData.triggerType);
      $("input[name='taskId']", $tr).val(rowData.taskId);
      $("input[name='directionId']", $tr).val(rowData.directionId);
      $("input[name='resultField']", $tr).val(rowData.resultField);
    }
    // 序号
    var rows = $('tbody tr', $container).length - 1;
    $('td.row-num', $tr).text(rows);

    // 触发类型
    $("select[name='triggerType']", $tr)
      .on('change', function () {
        var triggerType = $(this).val();
        $('.trigger-type', $tr).hide();
        $('.trigger-type-' + triggerType, $tr).show();
      })
      .trigger('change');

    // 环节
    $('input[name="taskId"]', $tr).wSelect2({
      defaultBlank: true, // 默认空选项
      remoteSearch: false,
      data: selectData.taskIds || [],
      width: '100%',
      height: 250
    });
    // 流向
    $('input[name="directionId"]', $tr).wSelect2({
      defaultBlank: true, // 默认空选项
      remoteSearch: false,
      data: selectData.directions || [],
      width: '100%',
      height: 250
    });
    // 交付物
    $('input[name="resultField"]', $tr).wSelect2({
      defaultBlank: true, // 默认空选项
      remoteSearch: false,
      multiple: true,
      data: selectData.formFields || [],
      width: '100%',
      height: 250
    });
  };
  return AppBizProcessDefinitionItemWorkflowBiMilestoneConfig;
});
