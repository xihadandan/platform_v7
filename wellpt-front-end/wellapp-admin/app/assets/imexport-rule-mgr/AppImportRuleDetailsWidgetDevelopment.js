define([
  'constant',
  'commons',
  'server',
  'appModal',
  'AppPtMgrDetailsWidgetDevelopment',
  'AppPtMgrCommons',
  'dataStoreBase',
  'formBuilder'
], function (constant, commons, server, appModal, AppPtMgrDetailsWidgetDevelopment, AppPtMgrCommons, DataStore, formBuilder) {
  var validator;
  var listView;
  // 平台管理_产品集成_导入规则定义详情_HTML组件二开
  var AppImportRuleDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  var entityColumns = {};
  var orginalColumnData = [];

  // 接口方法
  commons.inherit(AppImportRuleDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      // 验证器
      validator = server.Validation.validate({
        beanName: 'excelImportRule',
        container: this.widget.element,
        wrapperForm: true
      });

      _self._formInputRender();

      // 绑定事件
      _self._bindEvents();

      _self._initColumnDefineTable();

      _self._initUploadTemplateFile();
    },

    _initUploadTemplateFile: function (value) {
      var $container = this.widget.element;
      $('#templateUploadDiv', $container).empty();
      //上传模板文件
      formBuilder.buildFileUpload({
        container: $('#templateUploadDiv', $container),
        labelColSpan: '2',
        label: '模板',
        name: 'fileUuid',
        controlOption: {
          addFileText: '选择模板文件'
        },
        value: value,
        singleFile: true
      });
    },

    _formInputRender: function () {
      var _self = this;
      var $container = this.widget.element;

      $('#type', $container).wSelect2({
        valueField: 'type',
        remoteSearch: false,
        container: $container,
        data: [
          { id: 'entity', text: '实体' },
          { id: 'formdefinition', text: '表单' }
        ]
      });

      $('#type', $container).on('change', function () {
        var data = $(this).select2('data');
        $('.entity', $container).hide();
        if (data.id) {
          $('.entity', $container).show();
        }
        $("label[for='entity']").text(data.text);
        if (data.id == 'entity') {
          _self._initSystemEntityClassSelect();
        }
        if (data.id == 'formdefinition') {
          _self._initFormDefinitionSelect();
        }
      });

      $('#moduleId', $container).val(_self._moduleId());
      $('#moduleId', $container).prop('readonly', $('#moduleId', $container).val() != '');
      //模块选择
      $('#moduleId', $container).wSelect2({
        valueField: 'moduleId',
        remoteSearch: false,
        serviceName: 'appModuleMgr',
        queryMethod: 'loadSelectData'
      });

      $('#entity', $container).wSelect2({
        valueField: 'entity',
        remoteSearch: false,
        data: []
      });

      $('#entity', $container).on('change', function () {
        var val = $(this).val();
        if (val) {
          //加载列定义
        }
      });
    },

    _initSystemEntityClassSelect: function () {
      $('#entity').wSelect2({
        valueField: 'entity',
        remoteSearch: false,
        serviceName: 'systemTableService',
        queryMethod: 'loadSelectionByModule',
        params: {
          moduleId: this._moduleId(),
          idProperty: 'fullEntityName'
        }
      });
    },

    _initFormDefinitionSelect: function () {
      $('#entity').wSelect2({
        valueField: 'entity',
        remoteSearch: false,
        serviceName: 'formDefinitionService',
        queryMethod: 'loadSelectDataFromFormDefinition',
        params: {
          moduleId: this._moduleId(),
          idProperty: 'uuid'
        }
      });
    },

    _moduleId: function () {
      return this.getWidgetParams().moduleId;
    },

    _bean: function () {
      return {
        moduleId: this._moduleId(),
        uuid: null,
        name: null,
        sortOrder: null,
        code: null,
        id: null,
        startRow: null,
        entity: null,
        entityName: null,
        type: null,
        fileUuid: null,
        excelColumnDefinitions: [],
        changeColumnDefinitions: [],
        deletedExcelRows: []
      };
    },

    _saveImportRule: function () {
      var _self = this;
      var $container = $(this.widget.element);
      var bean = _self._bean();
      AppPtMgrCommons.form2json({
        json: bean,
        container: $container
      });

      bean.systemUnitId = server.SpringSecurityUtils.getCurrentUserUnitId();
      bean.changeColumnDefinitions = $('#table_column_info').bootstrapTable('getData');
      for (var i = 0, len = orginalColumnData.length; i < len; i++) {
        if (_.findIndex(bean.changeColumnDefinitions, { uuid: orginalColumnData[i].uuid }) == -1) {
          bean.deletedExcelRows.push(orginalColumnData[i]);
        }
      }
      if (!validator.form()) {
        return false;
      }
      server.JDS.call({
        service: 'excelImportRuleService.saveBean',
        data: [bean],
        version: '',
        success: function (result) {
          if (result.success) {
            appModal.success('保存成功！', function () {
              // 保存成功刷新列表
              listView.trigger('AppImportRuleListView.refresh');
              // 清空表单
              AppPtMgrCommons.clearForm({
                container: $container,
                includeHidden: true
              });
            });
          }
        }
      });
    },

    _refreshColumnDefineTableData: function (rows) {
      $('#table_column_info', this.widget.element).bootstrapTable('load', rows);
      $('#table_column_info', this.widget.element).bootstrapTable('resetView');
    },

    _initColumnDefineTable: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var $columnTable = $('#table_column_info', $container);

      formBuilder.bootstrapTable.initTableTopButtonToolbar(
        'table_column_info',
        'column',
        $container,
        {
          columnNum: '',
          attributeName: ''
        },
        'guuid'
      );

      $columnTable.bootstrapTable('destroy').bootstrapTable({
        data: [],
        idField: 'guuid',
        pagination: false,
        striped: false,
        showColumns: false,
        toolbar: $('#div_column_toolbar', $container),
        onEditableHidden: function (field, row, $el, reason) {
          $el.closest('table').bootstrapTable('resetView');
        },
        width: 500,
        columns: [
          {
            field: 'checked',
            formatter: function (value, row, index) {
              if (value) {
                return true;
              }
              return false;
            },
            checkbox: true
          },
          {
            field: 'guuid',
            title: 'GUUID',
            visible: false
          },
          {
            field: 'uuid',
            title: 'UUID',
            visible: false
          },
          {
            field: 'columnNum',
            title: '第几列',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          },
          {
            field: 'attributeName',
            title: '属性名',
            editable: {
              type: 'select',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              emptytext: '请选择',
              source: function () {
                return _self.attrbuteNameSource();
              }
            }
          }
        ]
      });
    },

    attrbuteNameSource: function () {
      var $container = this.widget.element;
      var type = $('#type', $container).val();
      var entity = $('#entity', $container).val();
      if (entityColumns[entity]) {
        return entityColumns[entity];
      }
      var method = '';
      if (type === 'entity') {
        method = 'getSelectEntityColumn';
      }
      if (type === 'formdefinition') {
        method = 'getSelectFormColumn';
      }

      server.JDS.call({
        service: 'basicDataApiFacade.' + method,
        data: [entity],
        version: '',
        async: false,
        success: function (result) {
          if (result.success) {
            var rs = result.data.data;
            var selects = [];
            for (var c = 0; c < rs.length; c++) {
              selects.push({
                value: rs[c].columnName,
                text: rs[c].columnName
              });
            }
            entityColumns[entity] = selects;
          }
        }
      });

      return entityColumns[entity];
    },

    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var pageContainer = _self.getPageContainer();

      // 新增
      pageContainer.off('AppImportRuleListView.editRow');
      pageContainer.on('AppImportRuleListView.editRow', function (e) {
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });

        _self._uneditableForm(false);
        _self._refreshColumnDefineTableData([]);

        $('#moduleId', $container).val(_self._moduleId());
        $('#moduleId', $container).prop('readonly', $('#moduleId', $container).val() != '');

        if (!e.detail.rowData) {
          //新增
          // 生成ID
          AppPtMgrCommons.idGenerator.generate($container.find('#id'), 'IMP_RULE_');
          $('#code', $container).val($('#id', $container).val().replace('IMP_RULE_', ''));
          // ID可编辑
          $('#id', $container).prop('readonly', false);
          $('#moduleId', $container).trigger('change');
          $('#type', $container).trigger('change');
          _self._initUploadTemplateFile();
        } else {
          //编辑
          server.JDS.call({
            service: 'excelImportRuleService.getBeanByUuid',
            data: [e.detail.rowData.uuid],
            version: '',
            success: function (result) {
              if (result.success) {
                orginalColumnData = [];
                var bean = _self._bean();
                $.extend(bean, result.data);
                bean = result.data;

                AppPtMgrCommons.json2form({
                  json: bean,
                  container: $container
                });
                // ID只读
                $('#id', $container).prop('readonly', true);
                validator.form();
                _self._initUploadTemplateFile(bean.fileUuid);
                $('#moduleId', $container).trigger('change');
                $('#type', $container).trigger('change');
                $('#moduleId', $container).prop('readonly', bean.moduleId != null);
                for (var i = 0, len = bean.excelColumnDefinitions.length; i < len; i++) {
                  bean.excelColumnDefinitions[i].guuid = bean.excelColumnDefinitions[i].uuid;
                }
                $.extend(true, orginalColumnData, bean.excelColumnDefinitions);
                _self._refreshColumnDefineTableData(bean.excelColumnDefinitions);
              }
            }
          });
        }

        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');

        listView = e.detail.ui;
      });

      $('#btn_save_import_rule', $container).on('click', function () {
        _self._saveImportRule();
        return false;
      });

      $('a[data-toggle="tab"]', $container).on('shown.bs.tab', function (e) {
        if ($(e.target).attr('aria-controls') == 'import_rule_col_info') {
          $('#table_column_info', _self.widget.element).bootstrapTable('resetView');
        }
        return true;
      });
    },
    _uneditableForm: function (uneditable) {
      uneditable = uneditable == undefined ? true : Boolean(uneditable);
      AppPtMgrCommons.uneditableForm(
        {
          container: this.widget.element
        },
        uneditable
      );

      $('#moduleId', this.widget.element).prop('readonly', $('#moduleId').val() != '');
    }
  });
  return AppImportRuleDetailsWidgetDevelopment;
});
