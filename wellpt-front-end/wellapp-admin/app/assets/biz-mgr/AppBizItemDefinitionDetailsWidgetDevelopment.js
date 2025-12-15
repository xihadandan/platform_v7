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
  var StringUtils = commons.StringUtils;
  // 平台管理_业务流程管理_业务事项定义详情_HTML组件二开
  var AppBizItemDefinitionDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppBizItemDefinitionDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      // 验证器
      validator = server.Validation.validate({
        beanName: 'bizItemDefinitionEntity',
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

      // 所属业务
      $('#businessName', $container).wCommonComboTree({
        service: 'bizBusinessFacadeService.getBusinessTree',
        multiSelect: false, // 是否多选
        parentSelect: false, // 父节点选择有效，默认无效
        onAfterSetValue: function (event, self, value) {
          $('#businessId', $container).val(value);
        },
        beforeTreeExpand: function (treeId, treeNode) {
          // 非业务节点，不可选择
          $.each(treeNode.children, function (i, child) {
            if (child.type != 'business') {
              child.nocheck = true;
            }
          });
        }
      });

      // 使用定义单
      $('#formId', $container).wSelect2({
        serviceName: 'bizItemDefinitionFacadeService',
        queryMethod: 'listDyFormDefinitionSelectData',
        selectionMethod: 'getDyFormDefinitionSelectDataByIds',
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
        type: null,
        businessId: null,
        formId: null,
        itemNameField: null,
        itemCodeField: null,
        timeLimitSubformId: null,
        timeLimitField: null,
        materialSubformId: null,
        materialNameField: null,
        materialCodeField: null,
        materialRequiredField: null,
        includeItemSubformId: null,
        includeItemNameField: null,
        includeItemCodeField: null,
        frontItemCodeField: null,
        mutexItemSubformId: null,
        mutexItemCodeField: null,
        relateItemSubformId: null,
        relateItemCodeField: null,
        remark: null
      };
    },

    _saveBizItemDefinition: function () {
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
        url: '/proxy/api/biz/item/definition/save',
        data: bean,
        success: function (result) {
          appModal.success('保存成功！', function () {
            // 保存成功刷新列表
            listView.trigger('AppBizItemDefinitionListView.refresh');
            // 清空表单
            AppPtMgrCommons.clearForm({
              container: $container,
              includeHidden: true
            });
          });
        }
      });
    },

    getSaveUrl: function (type) {
      var typeCode = 'single';
      if (type == '20') {
        typeCode = 'series';
      } else if (type == '30') {
        typeCode = 'parallel';
      }
      return '/proxy/api/biz/' + typeCode + 'item/definition/save';
    },

    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var pageContainer = _self.getPageContainer();

      // 使用定义单选择事件
      $('#formId', $container).on('change', function () {
        var formId = $(this).val();
        if (StringUtils.isNotBlank(formId)) {
          JDS.restfulGet({
            url: `/proxy/api/biz/item/definition/getFormDefinitionByFormId/${formId}`,
            success: function (result) {
              var formDefinition = JSON.parse(result.data);
              _self.initFormFields(formDefinition);
            }
          });
        } else {
          _self.initFormFields({ fields: {}, subforms: {} });
        }
      });

      // 新增
      pageContainer.off('AppBizItemDefinitionListView.editRow');
      pageContainer.on('AppBizItemDefinitionListView.editRow', function (e) {
        var type = e.detail.type || '10';
        $('.item', $container).hide();
        $('.item-' + type, $container).show();

        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });

        // 设置事项类型
        $('#type', $container).val(type);

        if (!e.detail.rowData) {
          //新增
          // 生成ID
          AppPtMgrCommons.idGenerator.generate($container.find('#id'), 'BIZ_ITEM_');
          $('#code', $container).val($('#id', $container).val().replace('BIZ_ITEM_', ''));
          // ID可编辑
          $('#id', $container).prop('readonly', false);
          // 重置表单字段信息
          $('#formId', $container).trigger('change');
        } else {
          //编辑
          JDS.restfulGet({
            url: '/proxy/api/biz/item/definition/get',
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
              $('#businessName', $container).wCommonComboTree('setValue', bean.businessId);
              // ID只读
              $('#id', $container).prop('readonly', true);
              // 重置表单字段信息
              $('#formId', $container).trigger('change');
              validator.form();
            }
          });
        }

        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');

        listView = e.detail.ui;
      });

      $('#btn_biz_item_definition_save', $container).on('click', function () {
        _self._saveBizItemDefinition();
        return false;
      });
    },

    initFormFields: function (formDefinition) {
      var _self = this;
      var $container = _self.getWidgetElement();
      var fieldSelectData = _self.getFieldSelectData(formDefinition);
      var subformSelectData = _self.getSubformSelectData(formDefinition);

      // 事项名称字段
      $('#itemNameField', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: fieldSelectData,
        width: '100%',
        height: 250
      });
      // 事项编码字段
      $('#itemCodeField', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: fieldSelectData,
        width: '100%',
        height: 250
      });
      // 时限从表
      $('#timeLimitSubformId', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: subformSelectData,
        width: '100%',
        height: 250
      });
      // 时限从表变更事件
      $('#timeLimitSubformId', $container)
        .on('change', function () {
          // 时限字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'timeLimitField');
        })
        .trigger('change');
      // 材料从表
      $('#materialSubformId', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: subformSelectData,
        width: '100%',
        height: 250
      });
      // 材料从表变更事件
      $('#materialSubformId', $container)
        .on('change', function () {
          // 材料名称字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'materialNameField');
          // 材料编码字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'materialCodeField');
          // 材料是否必填字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'materialRequiredField');
        })
        .trigger('change');
      // 包含事项从表
      $('#includeItemSubformId', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: subformSelectData,
        width: '100%',
        height: 250
      });
      // 包含事项从表变更事件
      $('#includeItemSubformId', $container)
        .on('change', function () {
          // 包含事项名称字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'includeItemNameField');
          // 包含事项编码字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'includeItemCodeField');
          // 前置事项编码
          _self.onSubformIdChange(formDefinition, $(this).val(), 'frontItemCodeField');
        })
        .trigger('change');
      // 互斥事项从表
      $('#mutexItemSubformId', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: subformSelectData,
        width: '100%',
        height: 250
      });
      // 互斥事项从表变更事件
      $('#mutexItemSubformId', $container)
        .on('change', function () {
          // 互斥事项编码字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'mutexItemCodeField');
        })
        .trigger('change');
      // 关联事项从表
      $('#relateItemSubformId', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: subformSelectData,
        width: '100%',
        height: 250
      });
      // 关联事项从表变更事件
      $('#relateItemSubformId', $container)
        .on('change', function () {
          // 关联事项编码字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'relateItemCodeField');
        })
        .trigger('change');
    },

    onSubformIdChange: function (formDefinition, subformId, field) {
      var _self = this;
      var $container = _self.getWidgetElement();
      var subforms = formDefinition.subforms || {};
      var resetSelect2 = false;
      $.each(subforms, function (i, subform) {
        if (subform.outerId == subformId) {
          var fields = _self.getFieldSelectData(subform);
          $('#' + field, $container).wSelect2({
            defaultBlank: true, // 默认空选项
            remoteSearch: false,
            data: fields,
            width: '100%',
            height: 250
          });
          resetSelect2 = true;
        }
      });
      // 从表不存在，对应字段下拉置空
      if (!resetSelect2) {
        $('#' + field, $container).wSelect2({
          defaultBlank: true, // 默认空选项
          remoteSearch: false,
          data: [],
          width: '100%',
          height: 250
        });
      }
    },

    getFieldSelectData: function (formDefinition) {
      var selectData = [];
      var fields = formDefinition.fields || {};
      $.each(fields, function (i, field) {
        selectData.push({ id: field.name, text: field.displayName });
      });
      return selectData;
    },

    getSubformSelectData(formDefinition) {
      var selectData = [];
      var subforms = formDefinition.subforms || {};
      $.each(subforms, function (i, subform) {
        selectData.push({ id: subform.outerId, text: subform.displayName });
      });
      return selectData;
    }
  });
  return AppBizItemDefinitionDetailsWidgetDevelopment;
});
