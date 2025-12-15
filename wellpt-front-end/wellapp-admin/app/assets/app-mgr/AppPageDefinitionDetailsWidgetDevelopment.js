define([
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'formBuilder',
  'AppPtMgrDetailsWidgetDevelopment',
  'AppPtMgrCommons'
], function (constant, commons, server, appContext, appModal, formBuilder, AppPtMgrDetailsWidgetDevelopment, AppPtMgrCommons) {
  var JDS = server.JDS;
  var UUID = commons.UUID;
  var Browser = commons.Browser;
  var StringUtils = commons.StringUtils;
  var Validation = server.Validation;
  var SecurityUtils = server.SpringSecurityUtils;

  // 页面视图列表
  var listView = null;
  // 页面资源视图ID
  var pageResourceListWidgetDefId = 'wBootstrapTable_C87ACC5519C00001701D17802BB2158F';
  // 表单验证器
  var validator = null;

  // 平台管理_产品集成_页面详情_HTML组件二开
  var AppPageDefinitionDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
    // AppPageDefinition的VO类
    this.bean = this.newBean();
  };

  // 接口方法
  commons.inherit(AppPageDefinitionDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      var widget = _self.getWidget();
      // 获取传给组件的参数
      var params = _self.getWidgetParams();
      // 验证器
      validator = Validation.validate({
        beanName: 'appApplication',
        container: widget.element,
        wrapperForm: true
      });
      // 产品集成信息-树设置
      $('#appPiName', widget.element).wCommonComboTree({
        service: 'appProductIntegrationMgr.getTreeByProductUuidAndDataType',
        serviceParams: [params.appProductUuid, ['1', '2', '3']],
        multiSelect: false, // 是否多选
        parentSelect: true, // 父节点选择有效，默认无效
        expandRootNode: true, // 展开根结点
        onAfterSetValue: function (event, self, value) {
          $('#appPiUuid', widget.element).val(value);
        }
      });

      if (params.appProdIntegrateType == '系统') {
        var html = '';
        html +=
          "<div class='workBench-set w-tooltip w-tooltip-br'>" +
          "<i class='iconfont icon-ptkj-tishishuoming'></i>" +
          "<div class='workbench-set-tips w-tooltip-content '>您需要成为工作台的使用者，才可以预览。<br/>请在工作台的使用者管理中设置，设置后需要退出重新登录</div>" +
          '</div>';
        $('#btn_preview').parent().append(html);

        //pc端状态
        $('label[for="isPc"]').parent().show();
        var $tipHtml =
          '<div class="w-tooltip">' +
          '<i class="iconfont icon-ptkj-tishishuoming"></i>' +
          '<div class="w-tooltip-content">1、只有启用时，才作为用户工作台在pc端显示<br />2、手机端工作台的PC端状态为【禁用】，不支持更改</div>' +
          '</div>';
        $('label[for="isPc"]').append($tipHtml);
      }

      // 绑定事件
      _self._bindEvents();
    },
    _systemId: function () {
      return this.getWidgetParams().systemId;
    },
    renderPageResourceTable: function () {
      var _self = this;
      var parmas = $.extend(
        true,
        {
          page: _self.bean
        },
        _self.getWidgetParams()
      );
      appContext.renderWidget({
        renderTo: '#page_resource_table',
        widgetDefId: pageResourceListWidgetDefId,
        params: parmas,
        callback: function () {},
        onPrepare: function () {}
      });
    },
    // 获取页面资源数据
    getPageResourceData: function () {
      var pageResourceListView = appContext.getWidgetById(pageResourceListWidgetDefId);
      if (pageResourceListView == null) {
        return [];
      }
      var dataProvider = pageResourceListView.getDataProvider();
      return dataProvider.dataArray || dataProvider.data;
    },
    // 清空页面资源数据
    clearPageResourceData: function () {
      var pageResourceListView = appContext.getWidgetById(pageResourceListWidgetDefId);
      if (pageResourceListView == null) {
        return;
      }
      var dataProvider = pageResourceListView.getDataProvider();
      if (dataProvider.dataArray) {
        dataProvider.dataArray = [];
      }
      if (dataProvider.data) {
        dataProvider.data = [];
      }
      pageResourceListView.refresh();
    },
    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      // 获取传给组件的参数
      var params = _self.getWidgetParams();
      var $container = $(widget.element);
      var parentWidget = _self.getParentWidget();

      // 模块主页去掉页面类型
      // if(!_self._systemId()){
      $('#isDefault').parents('.form-group').hide();
      // }

      // 监听页面列表事件
      // 新增
      parentWidget.on('AppPageDefinitionListView.addRow', function (e, ui) {
        // 使用原归属
        var appPiUuid = _self.bean.appPiUuid || params.appPiUuid;
        _self.isFirst = e.detail.rowNum ? false : true;
        listView = e.detail.ui;
        // 清空JSON
        _self.bean = _self.newBean();
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });
        AppPtMgrCommons.uneditableForm(
          {
            json: _self.bean,
            container: $container
          },
          false
        );
        // 清空页面资源
        _self.clearPageResourceData();
        // $("#appPiName", $container).wCommonComboTree({
        // value : appPiUuid
        // });
        $('#appPiName', $container).wCommonComboTree('setValue', appPiUuid);
        $('#appPiName', $container).attr('disabled', 'disabled');
        // 生成ID
        AppPtMgrCommons.idGenerator.generate($('#id', $container), 'page_');
        // ID可编辑
        $('#id', $container).prop('readonly', '');
        // 隐藏版本
        $('#version', $container).closest('.form-group').hide();
        // 隐藏保存为新版本按钮
        $("button[id='btn_save']", $container).show();
        $("button[id='btn_save_new_version']", $container).hide();
        $("button[id='btn_config']", $container).show();
        $('#isDefault', $container).removeAttr('disabled');
        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');
        $("input[name='isPc']", $container).removeAttr('disabled');
        $('#isPc1', $container).prop('checked', true);
      });
      // 删除行
      parentWidget.on('AppPageDefinitionListView.deleteRow', function (e, ui) {
        AppPtMgrCommons.clearForm({
          includeHidden: true,
          container: $container
        });
      });
      // 行点击
      parentWidget.on('AppPageDefinitionListView.clickRow', function (e, ui) {
        _self.bean = $.extend(true, {}, e.detail.rowData);
        AppPtMgrCommons.json2form({
          json: _self.bean,
          container: $container
        });
        listView = ui;
        // 引用页面不可配置
        if (_self.bean.isRef == 1) {
          AppPtMgrCommons.uneditableForm(
            {
              json: _self.bean,
              container: $container
            },
            true
          );
          $('#isDefault', $container).attr('disabled', 'disabled');
          $('#appPiName', $container).wCommonComboTree({
            disabled: true
          });
          $("button[id='btn_save']", $container).hide();
          $("button[id='btn_save_new_version']", $container).hide();
          $("button[id='btn_config']", $container).hide();
          setTimeout(function () {
            $('#page_resource_table', $container).find('button').hide();
          }, 500);
        } else {
          AppPtMgrCommons.uneditableForm(
            {
              json: _self.bean,
              container: $container
            },
            false
          );
          $("button[id='btn_save']", $container).show();
          $("button[id='btn_save_new_version']", $container).show();
          $("button[id='btn_config']", $container).show();
          $('#page_resource_table', $container).find('button').show();
          $('#isDefault', $container).removeAttr('disabled');
          $('#appPiName', $container).wCommonComboTree({
            disabled: false
          });
        }

        if (_self.bean.isDefault) {
          $('#isDefault', $container).attr('disabled', 'disabled');
        }
        $('input[name="isPc"][value="' + (_self.bean.isPc || 1) + '"]', $container).prop('checked', true);
        if (_self.bean.wtype == 'wMobilePage') {
          $('input[name="isPc"]', $container).attr('disabled', 'disabled');
        } else {
          $('input[name="isPc"]', $container).removeAttr('disabled');
        }

        // 非根结点不可编辑归属属性
        if (_self.bean._treeNodeLevel === 0) {
          $('#appPiName', $container).removeAttr('disabled');
        } else {
          $('#appPiName', $container).attr('disabled', 'disabled');
        }
        // $("#appPiName", $container).wCommonComboTree({
        // value : _self.bean.appPiUuid
        // });
        $('#appPiName', $container).wCommonComboTree('setValue', _self.bean.appPiUuid);
        // ID只读
        $('#id', $container).prop('readonly', 'readonly');
        // 显示版本
        $('#version', $container).closest('.form-group').show();
        $('.version-label', $container).text(_self.bean.version || '');
        validator.form();
        // 渲染页面资源
        _self.renderPageResourceTable();
      });

      // 保存
      $('#btn_save', $container).on('click', function () {
        if (!validator.form()) {
          return false;
        }
        AppPtMgrCommons.form2json({
          json: _self.bean,
          container: $container
        });

        if (_self.getWidgetParams().appProdIntegrateType == '系统') {
          _self.bean.isPc = $("input[name='isPc']:checked", $container).val();
        }
        // 收集页面资源
        _self.bean.pageResources = _self.getPageResourceData();
        // if (_self.isFirst || $('#isDefault', $container).attr('checked')) {
        //   _self.bean.isDefault = 1;
        // }
        // JDS.call({
        //   service: 'appPageDefinitionManager.saveDto',
        //   data: [_self.bean],
        //   version: '',
        //   success: function (result) {
        server.JDS.restfulPost({
          url: '/proxy/api/app/pagemanager/savePage',
          data: _self.bean,
          success: function (result) {
            appModal.success('保存成功！');
            // 保存成功刷新列表
            // if (_self.bean.isDefault) {
            //   $('#isDefault', $container).attr({ checked: true, disabled: 'disabled' });
            // }
            // _self.isFirst = false;
            _self.refresh(listView);
          }
        });
      });
      // 保存新版本
      $('#btn_save_new_version', $container).on('click', function () {
        if (!validator.form()) {
          return false;
        }
        AppPtMgrCommons.form2json({
          json: _self.bean,
          container: $container
        });
        // 收集页面资源
        _self.bean.pageResources = _self.getPageResourceData();
        JDS.call({
          service: 'appPageDefinitionManager.saveNewVersion',
          data: [_self.bean],
          version: '',
          success: function (result) {
            appModal.success('保存成功！');
            // 保存成功刷新列表
            _self.refresh(listView);
          }
        });
      });
      // 配置
      $('#btn_config', $container).on('click', function () {
        _self.refreshAppPageDefinitionBean();
        var piUuid = params.appPiUuid;
        var pageUuid = _self.bean.uuid;
        if (StringUtils.isBlank(pageUuid)) {
          appModal.error('请先保存新页面或选择要配置的页面！');
          return;
        }
        var configUrl = ctx + '/web/app/page/config/' + piUuid + '?pageUuid=' + pageUuid + '&prlvId=' + pageResourceListWidgetDefId;
        var isRef = _self.bean.isRef;
        // 引用页面配置
        if (isRef == 1) {
          appModal.confirm('该页面为引用页面，是否对引用的目标页面进行配置？', function (result) {
            if (result) {
              // window.open(configUrl, piUuid + pageUuid
              // +
              // "_config");
              var winOptions = {
                url: configUrl,
                name: piUuid + pageUuid + '_config',
                ui: listView || widget
              };
              appContext.getWindowManager().open(winOptions);
            }
          });
        } else if (StringUtils.isBlank(_self.bean.wtype)) {
          // 选择页面设计器
          _self.choosePageDesigner(function (pageWtype) {
            configUrl += '&pageWtype=' + pageWtype;
            _self.bean.wtype = pageWtype;
            $('#wtype', $container).val(pageWtype);
            // window.open(configUrl, piUuid + pageUuid +
            // "_config");
            var winOptions = {
              url: configUrl,
              name: piUuid + pageUuid + '_config',
              ui: listView || widget
            };
            appContext.getWindowManager().open(winOptions);
          });
        } else {
          // window.open(configUrl, piUuid + pageUuid +
          // "_config");
          var winOptions = {
            url: configUrl,
            name: piUuid + pageUuid + '_config',
            ui: listView || widget
          };
          appContext.getWindowManager().open(winOptions);
        }
      });
      // 预览
      $('#btn_preview', $container).on('click', function () {
        _self.refreshAppPageDefinitionBean();
        var piUuid = params.appPiUuid;
        var pageUuid = _self.bean.uuid;
        if (StringUtils.isBlank(pageUuid)) {
          appModal.error('请选择要预览的页面！');
          return;
        }
        var previewUrl = ctx + '/web/app/page/preview/' + piUuid + '?pageUuid=' + pageUuid;
        if (StringUtils.isBlank(_self.bean.wtype)) {
          appModal.error('页面未配置，请先配置页面！');
          return;
        }
        // 是否启用uni-app页面预览 uni-app.page-desinger.preview.enabled
        // uni-app页面预览url uni-app.page-desinger.preview.url
        if (_self.bean.wtype == 'wMobilePage') {
          var uniAppPreviewEnabled = SystemParams.getValue('uni-app.page-desinger.preview.enabled');
          var uniAppPreviewUrl = SystemParams.getValue('uni-app.page-desinger.preview.url');
          if (uniAppPreviewEnabled == 'true' && StringUtils.isNotBlank(uniAppPreviewUrl)) {
            var piItem = appContext.getPiItem(piUuid);
            var accessToken = getCookie('jwt');
            uniAppPreviewUrl += `#/uni_modules/w-app/pages/app/preview?appPiPath=${piItem.path}&pageUuid=${pageUuid}&accessToken=${accessToken}`;
            window.open(uniAppPreviewUrl, piUuid + pageUuid + '_preview');
          } else {
            window.open(previewUrl, piUuid + pageUuid + '_preview');
          }
        } else {
          window.open(previewUrl, piUuid + pageUuid + '_preview');
        }
      });
    },
    // 选择页面设计器
    choosePageDesigner: function (callback) {
      var _self = this;
      var dlgId = UUID.createUUID();
      var title = '选择页面设计器';
      var message = "<div id='" + dlgId + "'></div>";
      var dlgOptions = {
        title: title,
        message: message,
        templateId: '',
        size: 'middle',
        shown: function () {
          formBuilder.buildSelect2({
            select2: {
              data: _self.getSelect2DataOfPageDesigners(),
              multiple: false
            },
            container: $('#' + dlgId),
            name: 'pageWtype',
            diaplay: 'pageWtypeName',
            label: '页面设计器',
            labelColSpan: '3',
            controlColSpan: '9'
          });
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var pageWtype = $('#pageWtype', $('#' + dlgId)).val();
              if (StringUtils.isBlank(pageWtype)) {
                appModal.error('请选择页面设计器！');
                return false;
              }
              if (pageWtype == 'wMobilePage') {
                $('#isPc2').prop('checked', true);
                $("input[name='isPc']").attr('disabled', true);
              }
              callback.call(this, pageWtype);
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
    },
    // 获取select2的页面设计器数据
    getSelect2DataOfPageDesigners: function () {
      var dataList = [];
      // JDS.call({
      //   service: 'appPageDefinitionManager.getPageDesignerList',
      //   async: false,
      //   version: '',
      //   success: function (result) {
      server.JDS.restfulGet({
        url: `/page-designer/pageDesignerType`,
        async: false,
        success: function (result) {
          dataList = result;
        }
      });

      return dataList;
    },
    // 刷新bean信息
    refreshAppPageDefinitionBean: function () {
      var _self = this;
      if (StringUtils.isBlank(_self.bean.uuid)) {
        return;
      }
      // JDS.call({
      //   service: 'appPageDefinitionManager.getDto',
      //   data: [_self.bean.uuid],
      //   async: false,
      //   version: '',
      //   success: function (result) {
      server.JDS.restfulGet({
        url: `/proxy/api/app/pagemanager/getPageDefinition/${_self.bean.uuid}`,
        async: false,
        success: function (result) {
          _self.bean = result.data;
        }
      });
    },
    newBean: function () {
      return {
        uuid: null, // UUID
        recVer: null, // 版本号
        name: null, // 名称
        title: null, // 标题
        id: null, // ID
        code: null, // 编号
        wtype: null, // 页面容器类型
        title: null, // 标题
        remark: null, // 备注
        systemUnitId: null, // 归属系统单位ID
        appPiUuid: null
        // 归属产品集成UUID
      };
    }
  });
  return AppPageDefinitionDetailsWidgetDevelopment;
});
