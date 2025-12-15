define(['jquery', 'commons', 'constant', 'appContext', 'appModal', 'WorkView', 'jquery-workView'], function (
  $,
  commons,
  constant,
  appContext,
  appModal,
  WorkView,
  jqueryWorkView
) {
  // 签署意见模式
  var signOpinionModel = $('#wf_signOpinionModel').val();
  var wfSignOpinionModel = $('#ep_wf_sign_opinion_model').val();
  var StringUtils = commons.StringUtils;
  if (StringUtils.isNotBlank(wfSignOpinionModel)) {
    signOpinionModel = wfSignOpinionModel;
  }
  if (StringUtils.isBlank(signOpinionModel) || !(signOpinionModel === '1' || signOpinionModel === '2')) {
    signOpinionModel = '1';
  }

  var WorkFlow = {};
  var bean = {};

  // 判断字符串不为undefined、null、空串、空格串
  var isNotBlank = StringUtils.isNotBlank;
  // 判断字符串为undefined、null、空串、空格串
  var isBlank = StringUtils.isBlank;

  var dyformSelector = '#dyform';
  bean.flowDefUuid = $('#wf_flowDefUuid').val();
  bean.flowDefId = $('#wf_flowDefId').val();
  bean.flowInstUuid = $('#wf_flowInstUuid').val();
  bean.taskInstUuid = $('#wf_taskInstUuid').val();
  bean.taskId = $('#wf_taskId').val();
  bean.taskName = $('#wf_taskName').val();
  bean.taskIdentityUuid = $('#wf_taskIdentityUuid').val();
  bean.formUuid = $('#wf_formUuid').val();
  bean.defaultVFormUuid = $('#wf_defaultVFormUuid').val();
  bean.defaultMFormUuid = $('#wf_defaultMFormUuid').val();
  bean.dataUuid = $('#wf_dataUuid').val();
  bean.title = $('#wf_title').val();
  bean.aclRole = $('#wf_aclRole').val();
  bean.serialNoDefId = $('#wf_serialNoDefId').val();
  bean.suspensionState = $('#wf_suspensionState').val();
  bean.isFirstTaskNode = $('#wf_isFirstTaskNode').val();

  // 保留自定义运行时参数
  bean.extraParams = {};
  var $eps = $('input[name^=custom_rt_]', '#wf_form');
  $eps.each(function () {
    bean.extraParams[$(this).attr('name')] = $(this).val();
  });
  $eps = $('input[name^=ep_]', '#wf_form');
  $eps.each(function () {
    bean.extraParams[$(this).attr('name')] = $(this).val();
  });

  // 如果流程ID或流程定义UUID都为空，则提示错误并返回
  if (isBlank(bean.flowDefId) && isBlank(bean.flowDefUuid)) {
    oAlert('流程定义加载出错，没有指定流程或流程不存在!');
    return;
  }

  // JQuery UI按钮
  var workProcess = null;
  var workDataDisplayAsLabel = false;

  // 自动提交
  var autoSubmit = $('input[name=auto_submit]', '#wf_form').val();
  // 输入的办理意见
  var opinionLabel = $('#ep_wf_opinion_label').val();
  var opinionValue = $('#ep_wf_opinion_value').val();
  var opinionText = $('#ep_wf_opinion_text').val();
  var wf_opinion_label = $('#wf_opinionLabel').val();
  var wf_opinion_value = $('#wf_opinionValue').val();
  var wf_opinion_text = $('#wf_opinionText').val();
  if (StringUtils.isBlank(opinionLabel)) {
    opinionLabel = wf_opinion_label;
  }
  if (StringUtils.isBlank(opinionValue)) {
    opinionValue = wf_opinion_value;
  }
  if (StringUtils.isBlank(opinionText)) {
    opinionText = wf_opinion_text;
  }

  var toolbarModule = require('WorkFlowToolbar');
  var processViewerModule = null;
  var opinionEditorModule = null;
  var lxqpModule = require('WorkFlowLXQP');
  // 右侧签署意见
  if (signOpinionModel == '1') {
    // 侧边tab办理过程
    processViewerModule = require('WorkFlowSidebarTabWorkProcessViewer');
    // 侧边tab签署意见
    opinionEditorModule = require('WorkFlowSidebarTabOpinionEditor');
  } else {
    // 侧边独立办理过程
    processViewerModule = require('WorkFlowWorkProcessViewer');
    // 底部签署意见
    opinionEditorModule = require('WorkFlowOpinionEditor');
  }
  var errorHandlerModule = require('WorkFlowErrorHandler');
  // workView实例化回调
  var workViewInstantiateCallback = function (workViewModule) {
    // 是否可编辑动态表单，如果可编辑按流程设置处理，不可编辑设置有只读
    var options = {
      workViewModule: workViewModule,
      toolbarModule: toolbarModule,
      lxqpModule: lxqpModule,
      processViewerModule: processViewerModule,
      opinionEditorModule: opinionEditorModule,
      errorHandlerModule: errorHandlerModule,
      opinionEditor: {
        opinionLabel: opinionLabel,
        opinionValue: opinionValue,
        opinionText: opinionText
      },
      workData: bean,
      autoSubmit: autoSubmit,
      signOpinionModel: signOpinionModel,
      dyformSelector: dyformSelector,
      beforeServiceCallback: function () {
        // appModal.showMask();
        //$('.btn', options.toolbarPlaceholder).attr('disabled', 'disabled');
      },
      afterServiceCallback: function () {
        // appModal.hideMask();
        //$('.btn', options.toolbarPlaceholder).removeAttr('disabled');
      }
    };
    $('body').workView(options);

    $(dyformSelector).one('DyformCreationComplete', function (event) {
      $(dyformSelector).dyform('fixedSubformsHeader', window, 'div.widget-box>.widget-header');
    });
    // 表头固定
    // 发布事件
    $(document).trigger(constant.WIDGET_EVENT.PageContainerCreationComplete);
  };

  // 二开的流程JS模块
  var workViewModule = WorkView;
  var wf_customJsModule = $('#wf_customJsModule').val();
  if (StringUtils.isNotBlank(wf_customJsModule)) {
    workViewModule = require(wf_customJsModule);
  }
  // 外部传入的二开片段模块
  var ep_workViewFragmentModule = $('#ep_workViewFragment').val();
  // 流程环节二开片段模块
  var wf_customJsFragmentModule = $('#wf_customJsFragmentModule').val();
  // 动态的二开片段模块
  var custom_rt_workViewFragment = $('#custom_rt_workViewFragment').val();
  var workViewFragmentModules = [];
  if (StringUtils.isNotBlank(custom_rt_workViewFragment)) {
    workViewFragmentModules.push(custom_rt_workViewFragment);
  }
  if (StringUtils.isNotBlank(wf_customJsFragmentModule)) {
    workViewFragmentModules.push(wf_customJsFragmentModule);
  }
  if (StringUtils.isNotBlank(ep_workViewFragmentModule)) {
    workViewFragmentModules.push(ep_workViewFragmentModule);
  }
  if (workViewFragmentModules.length > 0) {
    appContext.require(workViewFragmentModules, function () {
      var SuperWorkViewModule = workViewModule;
      for (var index = 0; index < arguments.length; index++) {
        var workViewFragmentModule = arguments[index];
        if ($.isFunction(workViewFragmentModule)) {
          workViewFragmentModuleObject = new workViewFragmentModule();
        }
        var InnerWorkView = function ($element) {
          workViewModule.call(this, $element);
          this.$element = $element;
        };
        commons.inherit(InnerWorkView, SuperWorkViewModule, workViewFragmentModule);
        SuperWorkViewModule = InnerWorkView;
      }
      workViewInstantiateCallback(SuperWorkViewModule);
    });
  } else {
    workViewInstantiateCallback(workViewModule);
  }

  // 窗口大小调整事件
  $(window).bind('resize', function (e) {
    // 调整自适应表单宽度
    adjustWidthToForm();
  });

  // 底部高度调整事件
  $('div.widget-box').on('footer.resize', function (e) {
    $('.widget-body', this).css('padding-bottom', $('.footer', this).height() + 20);
  });
  // 调整自适应表单宽度
  function adjustWidthToForm() {
    // $(document).scrollTop("0"); // zhongzh comment:手动触发的resize事件导致表单滚动到头部

    var div_body_width = $(window).width() * 0.95;
    $('.form_header').css('width', div_body_width - 5);
    $('.div_body').css('width', div_body_width);

    $('.form_content').css('width', div_body_width - 44);
    $('#process').css('width', div_body_width - 45);

    // 调整子过程办理过程宽度
    $('.share_flow_content').css('width', div_body_width - 45);
  }

  /*
   * add by huanglinchuan 2014.10.18 begin window.onscroll = function() { var
   * $widgetHeader = $(".widget-header"); if ($(document).scrollTop() > 0) {
   * var width = $widgetHeader.width(); $widgetHeader.addClass("fixed");
   * $widgetHeader.css("width", width); $widgetHeader.css("top", "4px"); }
   * else { $widgetHeader.removeClass("fixed");
   * $widgetHeader.removeAttr("style"); } };
   */
  $('.sidebar-container').slimScroll({
    height: '100%',
    wheelStep: navigator.userAgent.indexOf('Firefox') > -1 ? 1 : 10
  });
  /* add by huanglinchuan 2014.10.18 end */
});
