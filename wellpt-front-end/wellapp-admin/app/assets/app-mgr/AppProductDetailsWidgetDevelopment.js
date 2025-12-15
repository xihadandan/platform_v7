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

  // 产品视图列表
  var listView = null;
  // 表单验证器
  var validator = null;
  // AppProduct的VO类
  var bean = {
    uuid: null, // UUID
    recVer: null, // 版本号
    name: null, // 名称
    id: null, // ID
    code: null, // 编号
    remark: null
    // 备注
  };

  // JQuery zTree
  var setting = {
    edit: {
      drag: {
        autoExpandTrigger: true,
        isCopy: false,
        isMove: true,
        prev: true,
        inner: true,
        next: true
      },
      enable: true,
      showRemoveBtn: false,
      showRenameBtn: false
    },
    view: {
      autoCancelSelected: true,
      selectedMulti: false,
      txtSelectedEnable: true
    }
  };

  // 根据UUID获取产品集成信息树
  function getAppProductIntegrationTree(productUuid) {
    // 加载资源树，自动选择已选资源
    JDS.call({
      service: 'appProductIntegrationMgr.getTree',
      data: [productUuid],
      async: false,
      success: function (result) {
        var zTree = $.fn.zTree.init($('#pi_tree'), setting, result.data);
        var nodes = zTree.getNodes();
        // 默认展开第一个节点
        if (nodes.length > 0) {
          var node = nodes[0];
          zTree.expandNode(node, true, false, false, true);
        }
      }
    });
  }

  // 平台管理_产品集成_产品详情_HTML组件二开
  var AppProductDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppProductDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      var widget = _self.getWidget();
      // 验证器
      validator = Validation.validate({
        beanName: 'appProduct',
        container: widget.element,
        wrapperForm: true
      });
      // 绑定事件
      _self._bindEvents();
    },
    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var parentWidget = _self.getParentWidget();
      // 监听产品列表事件
      // 新增
      parentWidget.on('AppProductListView.addRow', function (e, ui) {
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });
        listView = e.detail.ui;
        // 清空集成信息树
        var zTree = $.fn.zTree.init($('#pi_tree', $container), setting, []);
        // 生成ID
        AppPtMgrCommons.idGenerator.generate($('#id', $container), 'PRD_');
        // ID可编辑
        $('#id', $container).prop('readonly', '');
        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');
      });
      // 删除行
      parentWidget.on('AppProductListView.deleteRow', function (e, ui) {
        AppPtMgrCommons.clearForm({
          includeHidden: true,
          container: $container
        });
      });
      // 行点击
      parentWidget.on('AppProductListView.clickRow', function (e, ui) {
        bean = $.extend(true, {}, e.detail.rowData);
        AppPtMgrCommons.json2form({
          json: bean,
          container: $container
        });
        getAppProductIntegrationTree(bean.uuid);
        listView = ui;
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
          json: bean,
          container: $container
        });
        JDS.call({
          service: 'appProductMgr.saveBean',
          data: [bean],
          version: '',
          success: function (result) {
            appModal.success('保存成功！');
            // 保存成功刷新列表
            _self.refresh(listView);
          }
        });
      });

      $('a[data-toggle="tab"]', $container).on('shown.bs.tab', function (e) {
        if ($(e.target).attr('aria-controls') == 'template_list') {
          $('#table_print_template_info', _self.widget.element).bootstrapTable('resetView');
        }
      });
    }
  });
  return AppProductDetailsWidgetDevelopment;
});
