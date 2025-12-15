define([
  'constant',
  'commons',
  'server',
  'appModal',
  'AppPtMgrDetailsWidgetDevelopment',
  'AppPtMgrCommons',
  'dataStoreBase',
  'multiOrg',
  'formBuilder'
], function (constant, commons, server, appModal, AppPtMgrDetailsWidgetDevelopment, AppPtMgrCommons, DataStore, multiOrg, formBuilder) {
  var validator;
  var listView;
  var JDS = server.JDS;
  // 平台管理_业务流程管理_业务标签定义详情_HTML组件二开
  var AppBizTagDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppBizTagDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      // 验证器
      validator = server.Validation.validate({
        beanName: 'bizTagEntity',
        container: this.widget.element,
        wrapperForm: true
      });

      _self._formInputRender();

      // 绑定事件
      _self._bindEvents();
    },

    _formInputRender: function () {
      var _self = this;
      var $container = _self.getWidgetElement();

      // 作用范围
      $('#scope', $container).wSelect2({
        serviceName: 'dataDictionaryService',
        params: {
          type: 'BIZ_TAG_SCOPE'
        },
        remoteSearch: false,
        width: '100%',
        height: 250
      });
    },

    _bean: function () {
      return {
        uuid: null,
        recVer: null,
        name: null,
        id: null,
        code: null,
        scope: null,
        remark: null
      };
    },

    _saveBizTag: function () {
      var _self = this;
      var $container = $(this.widget.element);
      var bean = _self._bean();
      AppPtMgrCommons.form2json({
        json: bean,
        container: $container
      });
      if (!validator.form()) {
        return false;
      }
      // bean.systemUnitId = server.SpringSecurityUtils.getCurrentUserUnitId();
      JDS.restfulPost({
        url: '/proxy/api/biz/tag/save',
        data: bean,
        success: function (result) {
          appModal.success('保存成功！', function () {
            // 保存成功刷新列表
            listView.trigger('AppBizTagListView.refresh');
            // 清空表单
            AppPtMgrCommons.clearForm({
              container: $container,
              includeHidden: true
            });
          });
        }
      });
    },

    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var pageContainer = _self.getPageContainer();

      // 新增
      pageContainer.off('AppBizTagListView.editRow');
      pageContainer.on('AppBizTagListView.editRow', function (e) {
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });

        if (!e.detail.rowData) {
          //新增
          // 生成ID
          AppPtMgrCommons.idGenerator.generate($container.find('#id'), 'BIZ_TAG_');
          $('#code', $container).val($('#id', $container).val().replace('BIZ_TAG_', ''));
          // ID可编辑
          $('#id', $container).prop('readonly', false);
        } else {
          //编辑
          JDS.restfulGet({
            url: '/proxy/api/biz/tag/get',
            data: {
              uuid: e.detail.rowData.uuid
            },
            contentType: 'application/x-www-form-urlencoded',
            success: function (result) {
              var bean = _self._bean();
              $.extend(bean, result.data);
              bean = result.data;

              AppPtMgrCommons.json2form({
                json: bean,
                container: $container
              });
              $('#scope', $container).trigger('change');
              // ID只读
              $('#id', $container).prop('readonly', true);
              validator.form();
            }
          });
        }

        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');

        listView = e.detail.ui;
      });

      $('#btn_biz_tag_save', $container).on('click', function () {
        _self._saveBizTag();
        return false;
      });
    }
  });
  return AppBizTagDetailsWidgetDevelopment;
});
