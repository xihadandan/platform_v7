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
  var copy;
  var StringBuilder = commons.StringBuilder;
  var datastoreColumns = {};
  var processorClass = {};
  var AppDataIntegrationConfigDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppDataIntegrationConfigDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      // 验证器
      validator = server.Validation.validate({
        beanName: 'diConfigEntity',
        container: this.widget.element,
        wrapperForm: true
      });
      _self._formInputRender();

      _self._loadProcessorClass();

      _self._initProcessorTable();

      _self._initDynamicEntityDefineTable();

      _self._initHibernatePropertiesTable();

      // 绑定事件
      _self._bindEvents();
    },

    clear: function () {
      // 清空表单
      AppPtMgrCommons.clearForm({
        container: this.widget.element,
        includeHidden: true
      });
      $('#diRedeliveryTypeSelect', this.widget.element).find('option:eq(0)').prop('selected', true);
      $('#timeIntervalType', this.widget.element).prop('checked', true);
      $(':input', this.widget.element).trigger('change');

      $('table', this.widget.element).each(function () {
        if ($(this).bootstrapTable) {
          $(this).bootstrapTable('removeAll');
        }
      });
    },

    _loadProcessorClass: function () {
      server.JDS.call({
        service: 'diConfigFacadeService.processorSelections',
        data: [{}],
        version: '',
        async: false,
        success: function (result) {
          if (result.msg == 'success') {
            for (var i = 0; i < result.data.results.length; i++) {
              processorClass[result.data.results[i].id] = result.data.results[i].text;
            }
          }
        }
      });
    },

    _initProcessorTable: function () {
      var _self = this;
      var $processorTable = $('#table_processor_def_info', this.widget.element);
      // 定义添加，删除，上移，下移4按钮事件
      formBuilder.bootstrapTable.initTableTopButtonToolbar(
        'table_processor_def_info',
        'processor_def',
        this.widget.element,
        {
          uuid: '',
          type: '',
          processor: '',
          processorParameterSet: ''
        },
        'guuid'
      );

      // 列定义
      $processorTable.bootstrapTable('destroy').bootstrapTable({
        data: [],
        idField: 'guuid',
        // uniqueKey: "guuid",
        striped: true,
        showColumns: false,
        toolbar: $('#div_processor_def_toolbar', this.widget.element),
        width: 500,
        columns: [
          {
            field: 'checked',
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
            field: 'type',
            title: '类型',
            editable: {
              type: 'select',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              emptytext: '请选择',
              source: [
                {
                  value: '0',
                  text: '数据转换器'
                },
                {
                  value: '1',
                  text: '数据处理器'
                }
              ]
            }
          },
          {
            field: 'processor',
            title: '处理过程类',
            editable: {
              onblur: 'ignore',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              value2input: function (v) {
                var $input = this.$input;
                var type = $processorTable.bootstrapTable('getData')[parseInt($input.parents('tr').attr('data-index'))].type;
                if (type != 0 && type != 1) {
                  appModal.info('请先选择类型');
                  return null;
                }
                $input.closest('form').removeClass('form-inline');
                $input.css('width', '400');
                $input.empty();
                v = v || {};
                formBuilder.buildSelect2({
                  container: $input,
                  label: '',
                  name: 'processor',
                  value: v.class,
                  inputClass: 'w-custom-collect',
                  labelColSpan: '0',
                  controlColSpan: '12',
                  select2: {
                    serviceName: 'diConfigFacadeService',
                    queryMethod: 'processorSelections',
                    params: {
                      type: type
                    },
                    remoteSearch: false
                  }
                });
              },
              input2value: function (v) {
                var $input = this.$input;
                var $processor = $input.find('#processor');
                return {
                  class: $processor.select2('data').id,
                  name: $processor.select2('data').text
                };
              },
              value2display: function (v) {
                return v ? v.name : '';
              },
              value2html: function (v, element) {
                return $(element).html('<code>' + (v.name ? v.name : '') + '</code>');
              }
            }
          },
          {
            field: 'processorParameterSet',
            title: '参数',
            width: 100,
            formatter: function (value, row, index) {
              return '<a class="di_processor_param_set" data-index="' + index + '">参数设置</a>';
            }
          }
        ]
      });

      $processorTable.on('click', '.di_processor_param_set', function () {
        var data = $processorTable.bootstrapTable('getData')[parseInt($(this).attr('data-index'))];
        _self.showProcessorParameterDialog(data);
      });
    },

    _initDynamicEntityDefineTable: function () {
      var _self = this;
      var $dynamicEntityTable = $('#table_dynamic_entity_info', this.widget.element);
      // 定义添加，删除，上移，下移4按钮事件
      formBuilder.bootstrapTable.initTableTopButtonToolbar(
        'table_dynamic_entity_info',
        'dynamic_entity',
        this.widget.element,
        {
          uuid: '',
          type: '',
          name: '',
          column: ''
        },
        'guuid'
      );

      // 列定义
      $dynamicEntityTable.bootstrapTable('destroy').bootstrapTable({
        data: [],
        idField: 'guuid',
        striped: true,
        showColumns: false,
        toolbar: $('#div_dynamic_entity_toolbar', this.widget.element),
        width: 500,
        onEditableSave: function (field, row, oldValue, $el) {
          if (field == 'primary' && row[field] == '1') {
            var data = $dynamicEntityTable.bootstrapTable('getData');
            $.each(data, function (index, rowData) {
              if (row != rowData) {
                rowData.primary = 0;
                $dynamicEntityTable.bootstrapTable('updateRow', index, rowData);
              }
            });
          }
        },
        columns: [
          {
            field: 'checked',
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
            field: 'name',
            title: '实体属性',
            width: 100,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          },
          {
            field: 'column',
            title: '表字段',
            width: 100,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          },
          {
            field: 'type',
            title: '类型',
            editable: {
              type: 'select',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              emptytext: '请选择',
              source: [
                {
                  value: 'java.lang.String',
                  text: '字符串'
                },
                {
                  value: 'java.util.Date',
                  text: '日期'
                },
                {
                  value: 'java.lang.Long',
                  text: '长整型'
                },
                {
                  value: 'java.lang.Integer',
                  text: '整型'
                },
                {
                  value: 'java.lang.Double',
                  text: '双精度数值'
                },
                {
                  value: 'java.sql.Blob',
                  text: '大字段'
                }
              ]
            }
          },
          {
            field: 'primary',
            title: '是否主键',
            editable: {
              type: 'select',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              emptytext: '请选择',
              source: [
                {
                  value: '1',
                  text: '是'
                },
                {
                  value: '0',
                  text: '否'
                }
              ]
            }
          }
        ]
      });
    },

    _initHibernatePropertiesTable: function () {
      var _self = this;
      var $hibernatePropertiesTable = $('#table_hibernate_properties_info', this.widget.element);
      // 定义添加，删除，上移，下移4按钮事件
      formBuilder.bootstrapTable.initTableTopButtonToolbar(
        'table_hibernate_properties_info',
        'hibernate_properties',
        this.widget.element,
        {
          uuid: '',
          key: '',
          value: '',
          remark: ''
        },
        'guuid'
      );

      $hibernatePropertiesTable.bootstrapTable('destroy').bootstrapTable({
        data: [],
        idField: 'guuid',
        striped: true,
        showColumns: false,
        toolbar: $('#div_hibernate_properties_toolbar', this.widget.element),
        width: 500,
        columns: [
          {
            field: 'checked',
            checkbox: true
          },
          {
            field: 'guuid',
            title: 'GUUID',
            visible: false
          },
          {
            field: 'key',
            title: '属性',
            width: 100,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          },
          {
            field: 'value',
            title: '值',
            width: 100,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          },
          {
            field: 'remark',
            title: '描述',
            width: 100,
            editable: {
              type: 'textarea',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          }
        ]
      });
    },

    showProcessorParameterDialog: function (data) {
      var $dialog,
        dialogOpts = {
          title: '参数设置',
          message:
            '<div style="max-height:500px;overflow-y: auto;overflow-x: hidden;" class="form-horizontal">' +
            this.processorParameterDialogHtml(data) +
            '</div>',
          buttons: {
            confirm: {
              label: '确定',
              className: 'btn-primary',
              callback: function (result) {
                var param = {};
                $dialog.find(':input').each(function () {
                  var k = $(this).attr('name') || $(this).attr('id');
                  if ($(this).is('[type="checkbox"]')) {
                    var $chk = $dialog.find('input[name="' + k + '"]:checked');
                    if ($chk.length > 0) {
                      var vs = [];
                      $chk.each(function () {
                        vs.push($(this).val());
                      });
                      param[k] = vs.join(',');
                    }
                  } else if ($(this).is('[type="radio"')) {
                    var $rdo = $dialog.find('input[name="' + k + '"]:checked');
                    if ($rdo.length > 0) {
                      param[k] = $rdo.val();
                    }
                  } else {
                    param[k] = $(this).val();
                  }
                });
                data.processorParameter = JSON.stringify(param);
              }
            },

            cancel: {
              label: '取消',
              className: 'btn-default',
              callback: function (result) {}
            }
          },
          shown: function () {
            if (data && data.processorParameter) {
              var p = JSON.parse(data.processorParameter);
              for (var k in p) {
                var $input = $dialog.find(":input[name='" + k + "']");
                if ($input.is('select')) {
                  $input.find('option[value="' + p[k] + '"]').prop('selected', true);
                } else if ($input.length > 1 || $input.is('input[type="checkbox"]') || $input.is('input[type="radio"]')) {
                  var vs = p[k].split(',');
                  for (var i = 0; i < vs.length; i++) {
                    $dialog.find(":input[name='" + k + "'][value='" + vs[i] + "']").prop('checked', true);
                  }
                } else {
                  $input.val(p[k]);
                }
              }
            }
          },
          size: 'middle'
        };
      $dialog = appModal.dialog(dialogOpts);
    },

    processorParameterDialogHtml: function (data) {
      var _self = this;
      var Strings = new StringBuilder();
      if (data.processor) {
        server.JDS.call({
          service: 'diConfigFacadeService.getProcessorSupportParameters',
          data: [data.processor.class],
          async: false,
          version: '',
          success: function (result) {
            if (result.msg == 'success' && result.data) {
              for (var i = 0; i < result.data.length; i++) {
                Strings.append('<div class="form-group" style="margin-right:0px;margin-left:0px;">');
                Strings.appendFormat('<label class="col-sm-2 control-label">{0}</label>', result.data[i].name);
                Strings.append('<div class="col-sm-10">');
                Strings.append(_self.parameterInputDomString(result.data[i].id, result.data[i].domType, result.data[i].dataJSON));
                Strings.append('</div></div>');
              }
            }
          }
        });
      }

      return Strings.toString();
    },

    parameterInputDomString: function (id, type, dataJSON) {
      dataJSON = dataJSON || '{}';
      var data = JSON.parse(dataJSON);
      var Strings = new StringBuilder();
      if (type == 'INPUT') {
        return '<input type="text" name="' + id + '" class="form-control" edpp>';
      } else if (type == 'TEXTAREA') {
        return '<textarea rows="20" class="form-control" name="' + id + '" edpp></textarea>';
      } else if (type == 'CHECKBOX_BOOLEAN') {
        return '<input type="checkbox" name="' + id + '" class="form-control" value="true" edpp />';
      } else if (type == 'CHECKBOX') {
        for (var k in data) {
          Strings.appendFormat(
            '<label class="checkbox-inline">' + '<input type="checkbox" name="{0}" value="{1}" edpp>{2}' + '</label>',
            id,
            data[k],
            k
          );
        }
        return Strings.toString();
      } else if (type == 'SELECT') {
        Strings.appendFormat('<select class="form-control" name="{0}" edpp>', id);
        for (var k in data) {
          Strings.appendFormat('<option value="{0}">{1}</option>', data[k], k);
        }
        Strings.append('</select>');
        return Strings.toString();
      } else if (type == 'RADIO') {
        var i = 0;
        for (var k in data) {
          Strings.appendFormat(
            '<label class="radio-inline">\n' + '  <input type="radio" name="{0}" id="{1}" value="{2}" edpp>{3}\n' + '</label>',
            id,
            id + '_' + ++i,
            data[k],
            k
          );
        }
        return Strings.toString();
      }
    },

    _formInputRender: function () {
      var _self = this;
      var widget = _self.getWidget();

      $('#jobUuid', widget.element).wSelect2({
        valueField: 'jobUuid',
        remoteSearch: false,
        serviceName: 'diConfigFacadeService',
        queryMethod: 'diJobSelections'
      });

      $("input[name='executeType']", widget.element).on('change', function () {
        $('.executeType', widget.element).hide();
        $('.' + $("input[name='executeType']:checked", widget.element).val(), widget.element).show();
      });

      $('#diRedeliveryTypeSelect', widget.element)
        .on('change', function () {
          $('.redeliveryType', widget.element).hide();
          $('.' + $(this).val(), widget.element).show();
        })
        .trigger('change');

      $('#sourceType', widget.element).wSelect2({
        valueField: 'sourceType',
        remoteSearch: false,
        serviceName: 'diConfigFacadeService',
        queryMethod: 'endpointSelections',
        params: {
          type: 'consumer'
        }
      });

      $('#sourceType', widget.element).on('change', function () {
        var v = $(this).val();
        $('#DIC_sourceDefinition', widget.element).find('div[src-type]').hide();
        var $srcType = $('#DIC_sourceDefinition', widget.element).find("div[src-type='" + v + "']");
        if ($srcType.length > 0) {
          $srcType.show();
        } else if (v) {
          //加载类型对应的配置数据元素渲染
          var str = _self.renderCustomEndpointParameters(v, 'CONSUMER');
          $('#DIC_sourceDefinition', widget.element).append($(str));
          $('#DIC_sourceDefinition', widget.element)
            .find("div[src-type='" + v + "']")
            .show();
        }
      });

      _self.renderDatastoreEndpointDefinition();

      $('#targetType', widget.element).wSelect2({
        valueField: 'targetType',
        remoteSearch: false,
        serviceName: 'diConfigFacadeService',
        queryMethod: 'endpointSelections',
        params: {
          type: 'producer'
        }
      });

      $('#targetType', widget.element).on('change', function () {
        //TODO:加载类型对应的配置数据元素渲染
        var v = $(this).val();
        $('#DIC_targetDefinition', widget.element).find('div[tar-type]').hide();
        var $targetType = $('#DIC_targetDefinition', widget.element).find("div[tar-type='" + v + "']");
        if ($targetType.length > 0) {
          $targetType.show();
        } else if (v) {
          //加载类型对应的配置数据元素渲染
          var str = _self.renderCustomEndpointParameters(v, 'PRODUCER');
          $('#DIC_targetDefinition', widget.element).append($(str));
          $('#DIC_targetDefinition', widget.element)
            .find("div[tar-type='" + v + "']")
            .show();
        }
      });

      $('#callbackClass', widget.element).wSelect2({
        valueField: 'callbackClass',
        remoteSearch: false,
        serviceName: 'diConfigFacadeService',
        queryMethod: 'diCallbackSelections'
      });

      $('#btn_save_di_config', widget.element).on('click', function () {
        _self.savaDataIntegrationConfig();
        return false;
      });

      $('#tableName', widget.element).wSelect2({
        serviceName: 'cdDataStoreDefinitionService',
        queryMethod: 'loadSelectDataByTable',
        valueField: 'tableName',
        remoteSearch: false,
        multiple: true
      });
    },

    renderCustomEndpointParameters: function (t, type) {
      var _self = this;
      var Strings = new StringBuilder();
      server.JDS.call({
        service: 'diConfigFacadeService.getEndpointSupportParameters',
        data: [t, type],
        async: false,
        version: '',
        success: function (result) {
          if (result.msg == 'success' && result.data) {
            for (var i = 0; i < result.data.length; i++) {
              Strings.appendFormat(
                '<div style="display: none;" {0}="{1}"><div class="form-group">',
                type == 'CONSUMER' ? 'src-type' : 'tar-type',
                t
              );
              Strings.appendFormat('<label class="col-sm-2 control-label">{0}</label>', result.data[i].name);
              Strings.append('<div class="col-sm-10">');
              Strings.append(_self.parameterInputDomString(result.data[i].id, result.data[i].domType, result.data[i].dataJSON));
              Strings.append('</div></div></div>');
            }
          }
        }
      });

      return Strings.toString();
    },

    renderFtpEndpointDefinition: function (data) {},

    renderDatastoreEndpointDefinition: function (data) {
      var _self = this;

      $('#dataStoreId', this.widget.element).wSelect2({
        serviceName: 'viewComponentService',
        queryMethod: 'loadSelectData',
        valueField: 'dataStoreId',
        remoteSearch: false,
        params: {
          //piUuid: _self.component.pageDesigner.getPiUuid()
        }
      });

      $('#dataStoreId', this.widget.element).on('change', function (e, arg) {
        $('#uuidColumn', _self.widget.element)
          .val(arg && arg.uuidColumn ? arg.uuidColumn : '')
          .wSelect2({
            valueField: 'uuidColumn',
            data: _self._loadDatastoreColumns($(this).val())
          });
        $('#timeColumn', _self.widget.element)
          .val(arg && arg.timeColumn ? arg.timeColumn : '')
          .wSelect2({
            valueField: 'timeColumn',
            data: _self._loadDatastoreColumns($(this).val())
          });
      });
    },

    _loadDatastoreColumns: function (dataStoreId) {
      var colSource = [];
      if (datastoreColumns[dataStoreId]) {
        return datastoreColumns[dataStoreId];
      }
      server.JDS.call({
        service: 'viewComponentService.getColumnsById',
        data: [dataStoreId],
        async: false,
        version: '',
        success: function (result) {
          if (result.msg == 'success') {
            colSource = $.map(result.data, function (data) {
              return {
                value: data.columnIndex,
                text: data.columnIndex,
                dataType: data.dataType,
                title: data.title,
                id: data.columnIndex
              };
            });
          }
        }
      });
      datastoreColumns[dataStoreId] = colSource;
      return colSource;
    },

    renderFileEndpointDefinition: function (data) {},

    _bean: function () {
      return {
        uuid: null,
        name: null,
        id: null,
        jobUuid: null,
        redeliveryMaximum: null,
        redeliveryRulePattern: null,
        isEnable: null,
        redeliveryInterval: null,
        timeInterval: null,
        consumerEndpoint: null,
        producerEndpoint: null,
        processorDtos: null,
        systemUnitId: null,
        callbackClass: null,
        sourceType: null,
        targetType: null,
        isAsyncCallback: null
      };
    },

    savaDataIntegrationConfig: function () {
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
      bean.systemUnitId = server.SpringSecurityUtils.getCurrentUserUnitId();
      bean.isEnable;
      var executeType = $("input[name='executeType']:checked", $container).val();
      if (executeType == 'executeType_timeInterval') {
        bean.jobUuid = null;
      } else {
        bean.timeInterval = null;
      }
      var redeliveryType = $('#diRedeliveryTypeSelect', $container).val();
      if (redeliveryType == 'redeliveryType_timeInterval') {
        bean.redeliveryRulePattern = null;
      } else {
        bean.redeliveryMaximum = bean.redeliveryInterval = null;
      }

      //源端点定义
      bean.consumerEndpoint = {
        uuid: $('#sourceUuid', $container).val(),
        edpType: bean.sourceType,
        definition: (function () {
          var $srcContainer = $('#DIC_sourceDefinition', $container).find("div[src-type='" + bean.sourceType + "']");
          var def = {};
          $srcContainer.find(':input[edpp]').each(function () {
            var k = $(this).attr('name') || $(this).attr('id');
            def[k] = $(this).val();
          });
          return JSON.stringify(def);
        })()
      };

      var $table = $('#table_processor_def_info', $container);
      var processorDatas = $table.bootstrapTable('getData');
      var processor = [];
      for (var i = 0; i < processorDatas.length; i++) {
        processor.push({
          guuid: processorDatas[i].guuid || i,
          seq: i + 1,
          processorClass: processorDatas[i].processor.class,
          processorParameter: processorDatas[i].processorParameter,
          type: processorDatas[i].type,
          uuid: processorDatas[i].uuid
        });
      }
      bean.processorDtos = processor;

      bean.producerEndpoint = {
        uuid: $('#targetUuid', $container).val(),
        edpType: bean.targetType,
        definition: (function () {
          var $srcContainer = $('#DIC_targetDefinition', $container).find("div[tar-type='" + bean.targetType + "']");
          var def = {};
          $srcContainer.find(':input[edpp]').each(function () {
            if ($(this).is('[type=checkbox]')) {
              if ($(this).prop('checked')) {
                def[$(this).attr('name')] = $(this).val();
              }
            } else {
              def[$(this).attr('name')] = $(this).val();
            }
          });

          if (bean.targetType == 'hb-save') {
            def.mappingEntityProperties = $('#table_dynamic_entity_info', $container).bootstrapTable('getData');
            def.hibernateProperties = $('#table_hibernate_properties_info', $container).bootstrapTable('getData');
          }

          return JSON.stringify(def);
        })(),
        callbackClass: bean.callbackClass,
        isAsyncCallback: bean.isAsyncCallback
      };

      server.JDS.call({
        service: 'diConfigFacadeService.saveConfig',
        data: [bean],
        version: '',
        success: function (result) {
          if (result.success) {
            appModal.success('保存成功！', function () {
              // 保存成功刷新列表
              listView.trigger('AppDIConfigListView.refresh');
              // 清空表单
              AppPtMgrCommons.clearForm({
                container: $container,
                includeHidden: true
              });

              server.JDS.call({
                service: 'diConfigFacadeService.startOrStopRouteByConfigUuid',
                data: [result.data],
                version: ''
              });
            });
          }
        }
      });
    },

    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var pageContainer = _self.getPageContainer();

      // 新增
      pageContainer.off('AppDIConfigListView.editRow');
      pageContainer.on('AppDIConfigListView.editRow', function (e) {
        _self.clear();
        copy = false;
        var isCopy = !!e.detail.copy;
        if (!e.detail.rowData) {
          //新增
          // 生成ID
          AppPtMgrCommons.idGenerator.generate($container.find('#id'), 'DIC_');
          // ID可编辑
          $('#id', $container).prop('readonly', false);
        } else {
          //编辑
          server.JDS.call({
            service: 'diConfigFacadeService.getDetails',
            data: [e.detail.rowData.UUID],
            version: '',
            success: function (result) {
              if (result.success) {
                var bean = _self._bean();
                $.extend(bean, result.data);
                bean = result.data;

                AppPtMgrCommons.json2form({
                  json: bean,
                  container: $container
                });
                $('#id', $container).prop('readonly', !isCopy);
                if (isCopy) {
                  AppPtMgrCommons.idGenerator.generate($container.find('#id'), 'DIC_');
                  $container.find('#name,#uuid').val('');
                  $container.find('#isEnable').prop('checked', false);
                }

                if (bean.jobUuid) {
                  $('#jobType', $container).prop('checked', true).trigger('change');
                } else {
                  $('#timeIntervalType', $container).prop('checked', true).trigger('change');
                }
                $('#diRedeliveryTypeSelect', $container)
                  .find("option[value='" + (!!bean.redeliveryRulePattern ? 'redeliveryType_pattern' : 'redeliveryType_timeInterval') + "']")
                  .prop('selected', true);
                $('#diRedeliveryTypeSelect', $container).trigger('change');

                if (bean.consumerEndpoint) {
                  $('#sourceUuid', $container).val(isCopy ? '' : bean.consumerEndpoint.uuid);
                  $('#sourceType', $container).val(bean.consumerEndpoint.edpType).trigger('change');
                  var def = JSON.parse(bean.consumerEndpoint.definition);
                  if (bean.consumerEndpoint.edpType == 'datastore') {
                    $('#dataStoreId', $container)
                      .val(def.dataStoreId)
                      .trigger('change', { uuidColumn: def.uuidColumn, timeColumn: def.timeColumn });
                    // $('#uuidColumn', $container).val(def.uuidColumn).trigger('change');
                    // $('#timeColumn', $container).val(def.timeColumn).trigger('change');
                    $('#limit', $container).val(def.limit);
                  } else {
                    for (var k in def) {
                      var $input = $(
                        ":input[name='" + k + "'][edpp]",
                        $container.find("div[src-type='" + bean.consumerEndpoint.edpType + "']")
                      );
                      if ($input.is('select')) {
                        $input.find('option[value="' + def[k] + '"]').prop('selected', true);
                      } else if ($input.length > 1 || $input.is('input[type="checkbox"]') || $input.is('input[type="radio"]')) {
                        var vs = def[k].split(',');
                        for (var i = 0; i < vs.length; i++) {
                          $dialog.find(":input[name='" + k + "'][value='" + vs[i] + "']").prop('checked', true);
                        }
                      } else {
                        $input.val(def[k]);
                      }
                    }
                  }
                  $('#tableName', $container).trigger('change');
                }

                if (bean.producerEndpoint) {
                  $('#targetUuid', $container).val(isCopy ? '' : bean.producerEndpoint.uuid);
                  $('#targetType', $container).val(bean.producerEndpoint.edpType).trigger('change');
                  $('#callbackClass', $container).val(bean.producerEndpoint.callbackClass).trigger('change');
                  $('#isAsyncCallback', $container).prop('checked', bean.producerEndpoint.isAsyncCallback);
                  var def = JSON.parse(bean.producerEndpoint.definition);
                  if (bean.producerEndpoint.edpType) {
                    for (var k in def) {
                      var $input = $(
                        ":input[name='" + k + "'][edpp]",
                        $container.find("div[tar-type='" + bean.producerEndpoint.edpType + "']")
                      );
                      if ($input.is('select')) {
                        $input.find('option[value="' + def[k] + '"]').prop('selected', true);
                      } else if ($input.length > 1 || $input.is('input[type="checkbox"]') || $input.is('input[type="radio"]')) {
                        var vs = def[k].split(',');
                        for (var i = 0; i < vs.length; i++) {
                          $dialog.find(":input[name='" + k + "'][value='" + vs[i] + "']").prop('checked', true);
                        }
                      } else {
                        $input.val(def[k]);
                      }
                    }
                  }

                  if (def.mappingEntityProperties) {
                    $('#table_dynamic_entity_info').bootstrapTable('load', def.mappingEntityProperties);
                    $('#table_hibernate_properties_info').bootstrapTable('load', def.hibernateProperties);
                  }
                }

                if (bean.processorDtos) {
                  $('#table_processor_def_info', $container).bootstrapTable(
                    'load',
                    (function () {
                      var data = [];
                      for (var i = 0; i < bean.processorDtos.length; i++) {
                        if (isCopy) {
                          bean.processorDtos[i].uuid = null;
                        }
                        bean.processorDtos[i].processor = {
                          class: bean.processorDtos[i].processorClass,
                          name: processorClass[bean.processorDtos[i].processorClass]
                        };
                        data.push($.extend({ guuid: i }, bean.processorDtos[i]));
                      }
                      return data;
                    })()
                  );
                }
              }
            }
          });
        }

        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');

        listView = e.detail.ui;
      });
    }
  });
  return AppDataIntegrationConfigDetailsWidgetDevelopment;
});
