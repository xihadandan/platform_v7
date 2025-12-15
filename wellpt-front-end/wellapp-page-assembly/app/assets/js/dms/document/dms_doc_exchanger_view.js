define([
  'jquery',
  'commons',
  'constant',
  'server',
  'appContext',
  'jquery-dmsDocumentView',
  'formBuilder',
  'wUnit2',
  'bootstrap-datetimepicker',
  'DmsActionDispatcher'
], function ($, commons, constant, server, appContext, dmsDocumentView, formBuilder, wUnit2, datetimepicker, DmsActionDispatcher) {
  var StringUtils = commons.StringUtils;
  var options = {};
  var $inputs = $('input', "form[name='dms-data-form']");
  $.each($inputs, function () {
    var name = $(this).attr('name');
    if (StringUtils.isNotBlank(name) && name.length > 4) {
      var subfix = name.substring(0, 4);
      if ('dms_' == subfix) {
        var key = name.substring(4);
        var value = $(this).val();
        options[key] = value;
      }
    }
  });

  var configDto = options.configDto ? JSON.parse(options.configDto) : {};
  var recordDto = options.recordDto ? JSON.parse(options.recordDto) : {};
  options.isReceiver = !!recordDto.fromRecordDetailUuid;
  options.isSender = !options.isReceiver;
  window.isSender = options.isSender;

  /**
   * 加载文档数据
   */
  var loadDocument = function () {
    // 初始化页签项
    $('#docExchangeTabs a').on('click', function (e) {
      e.preventDefault();
      $(this).tab('show');
      var $tab = $(this);
      var $tabZone = $('#' + $(this).attr('href').substr(1));
      if (!$(this).data('load')) {
        //初始化基本信息区块
        appContext.require([$(this).attr('executeJsModule')], function (app) {
          $tab.data('load', true);
          var baseViewOpt = {
            action: 'init', //执行的方法
            $container: $tabZone.find('.doc_exchange_container'), //容器
            dataUuid: options.dataUuid,
            formUuid: options.formUuid,
            recordStatus: options.docExchangeRecordStatus,
            docExchangeRecordUuid: options.docExchangeRecordUuid,
            moduleId: options.extraParams.ep_moduleId,
            dataType: options.dataType,
            displayAsLabel: options.extraParams.ep_displayAsLabel == 'true',
            configuration: {},
            configDto: options.configDto ? JSON.parse(options.configDto) : {}
          };

          $('#docExchangerConf input[field]').each(function (i) {
            if ($(this).is(':checkbox')) {
              baseViewOpt['configuration'][$(this).attr('field')] = $(this).prop('checked');
            }
          });
          appContext.executeJsModule(app, baseViewOpt);
        });
      }
    });
    $("#docExchangeTabs a:contains('基础信息')").trigger('click');
    //对应的目标位置 _dialog ：弹窗,_blank ：新页面
    options.target = $('#target').val();
    // // 数据管理服务ID
    // options.dmsId = $("#dms_dmsId").val();
    // // 业务数据初始化
    // options.formUuid = $("#dms_formUuid").val();
    // options.dataUuid = $("#dms_dataUuid").val();
    // 单据解析JS模块
    // var documentViewModule = $("#dms_documentViewModule").val();
    var documentViewModule = options.documentViewModule;
    if (StringUtils.isNotBlank(documentViewModule)) {
      documentViewModule = require(documentViewModule);
    }
    options.documentViewModule = documentViewModule;
    var app = WebApp || {};
    options.extraParams = app.extraParams || {};
    // 外部传入的二开片段模块
    var epDmsDocumentViewFragmentModule = options.extraParams.ep_dmsDocumentViewFragment;
    if (StringUtils.isNotBlank(epDmsDocumentViewFragmentModule)) {
      appContext.require([epDmsDocumentViewFragmentModule], function (dmsDocumentViewFragmentModule) {
        var InnerDocumentView = function () {
          documentViewModule.apply(this, arguments);
        };
        var dmsDocumentViewFragmentModuleObject = dmsDocumentViewFragmentModule;
        if ($.isFunction(dmsDocumentViewFragmentModule)) {
          dmsDocumentViewFragmentModuleObject = new dmsDocumentViewFragmentModule();
        }
        commons.inherit(InnerDocumentView, documentViewModule, dmsDocumentViewFragmentModuleObject);
        options.documentViewModule = InnerDocumentView;
        $('body').dmsDocumentView(options);
      });
    } else {
      $('body').dmsDocumentView(options);
    }
    // 发布事件
    $(document).trigger(constant.WIDGET_EVENT.PageContainerCreationComplete);

    var dyformSelector = '#dyform';
    $(document.body).one('DyformCreationComplete', function (event) {
      $(dyformSelector).dyform('fixedSubformsHeader', window, 'div.widget-box>.widget-header');
    });
    // 窗口大小调整事件
    $(window).bind('resize', function (e) {
      // 调整自适应表单宽度
      adjustWidthToForm();
    });

    // 调整自适应表单宽度
    function adjustWidthToForm() {
      // $(document).scrollTop("0");
      var div_body_width = $(window).width() * 0.95;
      $('.form_header').css('width', div_body_width - 5);
      $('.div_body').css('width', div_body_width);
      $('.form_content').css('width', div_body_width - 44);
    }

    if (options.docExchangeRecordStatus != 'DRAFT' && options.docExchangeRecordUuid) {
      $('#docExchangeTabs li:gt(0)').show();
    }

    if (options.isSender) {
      $('#forward_info_li').hide();
    } else {
      $('#urge_info_li,#extra_send_info_li').hide();
    }

    if (!recordDto.isNeedSign) {
      $('#sign-tab').hide();
    }

    if (!recordDto.isNeedFeedback) {
      $('#feedback-tab').hide();
    }
  };

  function openSignOrReturnDialog() {
    appContext.require(['DmsDocExchangerReceiverOperateAction'], function (app) {
      var baseViewOpt = {
        action: 'signOrReturn', //执行的方法
        $container: $('#doc_exc_body'), //容器
        docExchangeRecordUuid: options.docExchangeRecordUuid,
        signCallback: function () {
          if (configDto.processView && !recordDto.noReminders) {
            openProcessViewReminderDialog();
          } else {
            loadDocument();
            $('#doc_exc_body').show();
            appContext.getWindowManager().refreshParent();
          }
        },
        returnCallBack: function () {}
      };
      appContext.executeJsModule(app, baseViewOpt);
    });
  }

  function openProcessViewReminderDialog() {
    appContext.require(['DmsDocExchangerReceiverOperateAction'], function (app) {
      var baseViewOpt = {
        action: 'processViewReminder', //执行的方法
        $container: $('#doc_exc_body'), //容器
        docExchangeRecordUuid: options.docExchangeRecordUuid,
        configDto: configDto,
        recordDto: recordDto,
        callback: function () {
          loadDocument();
          $('#doc_exc_body').show();
          appContext.getWindowManager().refreshParent();
        }
      };
      appContext.executeJsModule(app, baseViewOpt);
    });
  }

  if (options.docExchangeRecordStatus == 'WAIT_SIGN') {
    openSignOrReturnDialog();
  } else if (options.isReceiver && configDto.processView && !recordDto.noReminders) {
    openProcessViewReminderDialog();
  } else {
    loadDocument();
  }
});
