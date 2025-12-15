define(['constant', 'commons', 'server', 'appModal', 'AppPtMgrDetailsWidgetDevelopment', 'AppPtMgrCommons'], function (
  constant,
  commons,
  server,
  appModal,
  AppPtMgrDetailsWidgetDevelopment,
  AppPtMgrCommons
) {
  var JDS = server.JDS;
  var Browser = commons.Browser;
  var StringUtils = commons.StringUtils;
  var Validation = server.Validation;
  var SecurityUtils = server.SpringSecurityUtils;

  // 系统视图列表
  var listView = null;
  // 表单验证器
  var validator = null;

  // 平台管理_产品集成_模块详情_HTML组件二开
  var AppModuleDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
    // AppModule的VO类
    this.bean = {
      uuid: null, // UUID
      recVer: null, // 版本号
      code: null, // 编号
      enabled: null, // 是否启用
      id: null, // ID
      name: null, // 名称
      title: null, // 标题
      remark: null, // 备注
      jsModule: null, // 加载自定义JavaScript模块，以模块化的形式开发JavaScript，多个以逗号隔开
      systemUnitId: null, // 归属系统单位ID
      belongToAppSystemUuid: null, // 归属系统UUID
      appPiUuid: null
      // 集成信息UUID
    };
  };

  // 接口方法
  commons.inherit(AppModuleDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      var widget = _self.getWidget();
      // 获取传给组件的参数
      var params = _self.getWidgetParams();
      // 验证器
      validator = Validation.validate({
        beanName: 'appModule',
        container: widget.element,
        wrapperForm: true
      });
      // 归属系统
      $('#belongToAppSystemName').wSelect2({
        serviceName: 'appSystemManager',
        labelField: 'belongToAppSystemName',
        valueField: 'belongToAppSystemUuid',
        params: {
          appProductUuid: params.appProductUuid
        },
        defaultBlank: true,
        remoteSearch: false,
        width: '100%',
        height: 250
      });
      // 绑定事件
      _self._bindEvents();
    },
    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      // 获取传给组件的参数
      var params = _self.getWidgetParams();
      var $container = $(widget.element);
      var parentWidget = _self.getParentWidget();
      // 监听系统列表事件
      // 新增
      parentWidget.on('AppModuleListView.addRow', function (e, ui) {
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });
        listView = e.detail.ui;
        // 归属系统——从全部模块导航新增的可为空，从产品模块配置新增的不可编辑
        _self.bean.belongToAppPiUuid = params.appPiUuid;
        if (StringUtils.isNotBlank(params.appSystemUuid)) {
          $('#belongToAppSystemUuid', $container).val(params.appSystemUuid);
          $('#belongToAppSystemName', $container).select2('disable');
        } else {
          $('#belongToAppSystemUuid', $container).val(_self.bean.appSystemUuid);
        }
        $('#belongToAppSystemName', $container).trigger('change');
        // 生成ID
        AppPtMgrCommons.idGenerator.generate($('#id', $container), 'mod_');
        // ID可编辑
        $('#id', $container).prop('readonly', '');
        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');
      });
      // 新增子模块
      parentWidget.on('AppModuleListView.addSubModule', function (e, ui) {
        if (_self.bean == null) {
          appModal.error('请选择上级模块！');
          return;
        }
        listView = e.detail.ui;
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });
        // 子模块归属的产品集成信息
        _self.bean.belongToAppPiUuid = _self.bean.appPiUuid;
        // 归属系统不可编辑
        $('#belongToAppSystemUuid', $container).val(params.appSystemUuid || _self.bean.appSystemUuid);
        $('#belongToAppSystemName', $container).trigger('change');
        $('#belongToAppSystemName', $container).select2('disable');
        // 生成ID
        AppPtMgrCommons.idGenerator.generate($('#id', $container), 'mod_');
        // ID可编辑
        $('#id', $container).prop('readonly', '');
        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');
      });
      // 删除行
      parentWidget.on('AppModuleListView.deleteRow', function (e, ui) {
        AppPtMgrCommons.clearForm({
          includeHidden: true,
          container: $container
        });
      });
      // 行点击
      parentWidget.on('AppModuleListView.clickRow', function (e, ui) {
        _self.bean = $.extend(true, {}, e.detail.rowData);
        // 归属集成信息UUID
        _self.bean.belongToAppSystemUuid = _self.bean.appSystemUuid;
        AppPtMgrCommons.json2form({
          json: _self.bean,
          container: $container
        });
        listView = ui;
        $('#belongToAppSystemName', $container).trigger('change');
        // 子模块不可变更归属系统
        if (_self.bean._treeNodeLevel === 0) {
          $('#belongToAppSystemName', $container).select2('enable');
        } else {
          $('#belongToAppSystemName', $container).select2('disable');
        }
        // ID只读
        $('#id', $container).prop('readonly', 'readonly');
        validator.form();
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
        // 归属产品集成UUID
        _self.bean.belongToAppPiUuid = _self.bean.belongToAppPiUuid || params.appPiUuid;
        JDS.call({
          service: 'appModuleManager.saveDto',
          data: [_self.bean],
          version: '',
          success: function (result) {
            appModal.success('保存成功！');
            // 保存成功刷新列表
            _self.refresh(listView);
          }
        });
      });
    }
  });
  return AppModuleDetailsWidgetDevelopment;
});
