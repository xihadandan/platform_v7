define([
  'constant',
  'commons',
  'server',
  'appModal',
  'AppPtMgrDetailsWidgetDevelopment',
  'AppPtMgrCommons',
  'dataStoreBase',
  'multiOrg'
], function (constant, commons, server, appModal, AppPtMgrDetailsWidgetDevelopment, AppPtMgrCommons, DataStore, multiOrg) {
  var validator;
  var listView;
  // 平台管理_产品集成_数据仓库详情_HTML组件二开
  var AppDatastoreDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppDatastoreDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      // 验证器
      validator = server.Validation.validate({
        beanName: 'cdDataStoreDefinition',
        container: this.widget.element,
        wrapperForm: true
      });

      _self._formInputRender();
      _self._initColumnTable();
      // 绑定事件
      _self._bindEvents();
    },
    _refalshResource: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      $('#tableName', $container).wSelect2({
        serviceName: 'cdDataStoreDefinitionService',
        queryMethod: 'loadSelectDataByTable',
        remoteSearch: false
      });

      //模块选择
      $('#moduleId', $container).wSelect2({
        valueField: 'moduleId',
        remoteSearch: false,
        serviceName: 'appModuleMgr',
        queryMethod: 'loadSelectData',
        params: {
          systemUnitId: server.SpringSecurityUtils.getCurrentUserUnitId()
          //excludeIds: _self._moduleId()
        }
      });
      $('#viewName', $container).wSelect2({
        serviceName: 'cdDataStoreDefinitionService',
        queryMethod: 'loadSelectDataByView',
        remoteSearch: false
      });
      $('#entityName', $container).wSelect2({
        serviceName: 'cdDataStoreDefinitionService',
        queryMethod: 'loadSelectDataByEntity',
        remoteSearch: false
      });
      $('#sqlName', $container).wSelect2({
        serviceName: 'cdDataStoreDefinitionService',
        queryMethod: 'loadSelectDataBySqlName',
        remoteSearch: false
      });
      $('#dataInterfaceName', $container).wSelect2({
        serviceName: 'cdDataStoreDefinitionService',
        queryMethod: 'loadSelectDataByDataInterface',
        remoteSearch: false
      });
      //类型选择
      $('#type', $container).wSelect2({
        serviceName: 'dataDictionaryService',
        params: {
          type: 'APP_PT_DATA_STORE_TYPE'
        },
        remoteSearch: false
      });
    },
    _formInputRender: function () {
      var _self = this;
      var widget = _self.getWidget();

      var $container = $(widget.element);

      $('#tableName', $container).on('change', function (event, noLoadColumn) {
        if (!noLoadColumn) {
          var tableName = $('#tableName', $container).val(),
            camelColumnIndex = $('#camelColumnIndex', $container)[0].checked;
          _self.loadColumns({
            //service: 'cdDataStoreDefinitionService.loadTableColumns',
            url: `/proxy/api/datastore/loadTableColumns/${tableName}?camelColumnIndex=${camelColumnIndex}`
            //data: [, ]
          });
        }
      });

      $('#viewName', $container).on('change', function (event, noLoadColumn) {
        if (!noLoadColumn) {
          // _self.loadColumns({
          //   service: 'cdDataStoreDefinitionService.loadViewColumns',
          //   data: [$('#viewName', $container).val(), $('#camelColumnIndex', $container)[0].checked]
          // });
          var viewName = $('#viewName', $container).val(),
            camelColumnIndex = $('#camelColumnIndex', $container)[0].checked;
          _self.loadColumns({
            url: `/proxy/api/datastore/loadViewColumns/${viewName}?camelColumnIndex=${camelColumnIndex}`
          });
        }
      });

      $('#entityName', $container).on('change', function (event, noLoadColumn) {
        if (!noLoadColumn) {
          // _self.loadColumns({
          //   service: 'cdDataStoreDefinitionService.loadEntityColumns',
          //   data: [$('#entityName', $container).val()]
          // });
          var entityName = $('#entityName', $container).val();
          _self.loadColumns({
            url: `/proxy/api/datastore/loadEntityColumns?entityName=${entityName}`
          });
        }
      });
      $('#sqlStatement', $container).on('change', function (event, noLoadColumn) {
        if (!noLoadColumn) {
          // _self.loadColumns({
          //   service: 'cdDataStoreDefinitionService.loadSqlColumns',
          //   data: [$('#sqlStatement', $container).val(), $('#camelColumnIndex', $container)[0].checked]
          // });

          var sqlStatement = $('#sqlStatement', $container).val(),
            camelColumnIndex = $('#camelColumnIndex', $container)[0].checked;
          _self.loadColumns({
            url: `/proxy/api/datastore/loadSqlColumns?sqlStatement=${sqlStatement}&camelColumnIndex=${camelColumnIndex}`
          });
        }
      });

      $('#sqlName', $container).on('change', function (event, noLoadColumn) {
        if (!noLoadColumn) {
          // _self.loadColumns({
          //   service: 'cdDataStoreDefinitionService.loadSqlNameColumns',
          //   data: [$('#sqlName', $container).val(), $('#camelColumnIndex', $container)[0].checked]
          // });

          var sqlName = $('#sqlName', $container).val(),
            camelColumnIndex = $('#camelColumnIndex', $container)[0].checked;
          _self.loadColumns({
            url: `/proxy/api/datastore/loadSqlNameColumns/${sqlName}?camelColumnIndex=${camelColumnIndex}`
          });
        }
      });
      $('#dataInterfaceName', $container).on('change', function (event, noLoadColumn) {
        var interfaceName = $('#dataInterfaceName', $container).val();
        var interfaceParam = $('#dataInterfaceParam', $container).val();
        //隐藏“数据接口”与“接口使用说明”（旧的数据接口参数模式）
        // $('.DATA_STORE_TYPE_DATA_INTERFACE:eq(1),.DATA_STORE_TYPE_DATA_INTERFACE:eq(2)').hide();
        // $('.DATA_STORE_TYPE_DATA_INTERFACE').filter('.PARAMS').remove();

        if (!noLoadColumn) {
          // _self.loadColumns({
          //   service: 'cdDataStoreDefinitionService.loadDataInterfaceColumns',
          //   data: [interfaceName, interfaceParam]
          // });
          _self.loadColumns({
            url: `/proxy/api/datastore/loadDataInterfaceColumns?interfaceName=${interfaceName}&interfaceParam=${interfaceParam}`
          });
        }

        // server.JDS.call({
        //   service: 'cdDataStoreService.getQueryInterfaceParams',
        //   data: [interfaceName],
        //   version: '',
        //   async: false,
        //   success: function (result) {
        server.JDS.restfulGet({
          url: `/proxy/api/datastore/getQueryInterfaceParams?interfaceName=${interfaceName}`,
          success: function (result) {
            //隐藏“数据接口”与“接口使用说明”（旧的数据接口参数模式）
            $('.DATA_STORE_TYPE_DATA_INTERFACE').filter('.PARAMS').remove();
            if (result.data) {
              $('.DATA_STORE_TYPE_DATA_INTERFACE:eq(1),.DATA_STORE_TYPE_DATA_INTERFACE:eq(2)').hide();
              //接口参数
              _self.renderQueryInterfaceParams(result.data, interfaceParam);
            } else {
              $('.DATA_STORE_TYPE_DATA_INTERFACE:eq(1),.DATA_STORE_TYPE_DATA_INTERFACE:eq(2)').show();
              // server.JDS.call({
              //   service: 'cdDataStoreDefinitionService.getInterfaceDesc',
              //   data: [interfaceName],
              //   version: '',
              //   success: function (result) {
              server.JDS.restfulGet({
                url: `/proxy/api/datastore/getInterfaceDesc?interfaceName=${interfaceName}`,
                success: function (result) {
                  $('#interfaceDesc').html(result.data);
                }
              });
            }
          }
        });
      });

      $('#type', $container)
        .on('change', function () {
          $('.DATA_STORE_TYPE', $container).hide();
          var isNew = !$('#uuid', $container).val();
          var type = $(this).val();
          _self._refalshResource();
          if (type) {
            //显示类型对应的表单输入域
            // $('.' + type, $container).each(function (i, item) {
            //   $(item).show();
            //   _self._columnTableDataRefresh([]);
            //   $('.' + type, $container)
            //     .find('input')
            //     .trigger('change', true);
            // });
            $('.' + type, $container).show();
            _self._columnTableDataRefresh([]);
            $('.' + type, $container)
              .find('input')
              .trigger('change', true);

            if (isNew) {
              // 显示隐藏驼峰列配置
              if (type == 'DATA_STORE_TYPE_ENTITY' || type == 'DATA_STORE_TYPE_DATA_INTERFACE') {
                $('#camelColumnIndexForm', $container).hide();
              } else {
                $('#camelColumnIndexForm', $container).show();
              }
            }
          }

          // 数据接口、SQL命名查询不可设置默认排序
          if (type == 'DATA_STORE_TYPE_NAME_QUERY' || type == 'DATA_STORE_TYPE_DATA_INTERFACE') {
            $('.tr-default-order', $container).hide();
            $('#defaultOrder', $container).val('');
          } else {
            $('.tr-default-order', $container).show();
          }
        })
        .trigger('change');

      //保存
      $('#btn_save_ds', $container).on('click', function () {
        appModal.showMask();
        _self._saveDatastoreDefinition();
        _self._refalshResource();
        return false;
      });

      //预览
      $('#btn_preview_ds', $container).on('click', function () {
        _self._previewDatastore();
        return false;
      });

      // 使用驼峰风格变更时更新列定义
      $('#camelColumnIndex', $container).on('change', function () {
        var _input = $('.' + $('#type', $container).val() + ' input', $container);
        if (_input.length == 0) {
          _input = $('.' + $('#type', $container).val() + ' textarea', $container);
        }
        _input.trigger('change', false);
      });

      //更新列定义
      $('#btn_update_ds_col', $container).on('click', function () {
        var $container = $(widget.element);
        // 显示列定义tab内容
        $('.nav-tabs>li>a:eq(1)', $container).tab('show');
        var _input = $('.' + $('#type', $container).val() + ' input', $container);
        if (_input.length == 0) {
          _input = $('.' + $('#type', $container).val() + ' textarea', $container);
        }
        _input.trigger('change', false);
        return false;
      });
    },
    _reloadInterfaceColumnData: function (interfaceParam) {
      var $container = $(this.getWidget().element);
      var type = $('#type', $container).val();
      var interfaceName = $('#dataInterfaceName', $container).val();
      var interfaceParam = interfaceParam || $('#dataInterfaceParam', $container).val();
      if (type == 'DATA_STORE_TYPE_DATA_INTERFACE') {
        if (interfaceName && interfaceParam) {
          this.loadColumns({
            service: 'cdDataStoreDefinitionService.loadDataInterfaceColumns',
            data: [interfaceName, interfaceParam]
          });
        }
      }
    },

    renderQueryInterfaceParams: function (paramOptions, paramsJson) {
      var $last = $('.DATA_STORE_TYPE_DATA_INTERFACE:last');
      var initParams = {};
      if (paramsJson) {
        try {
          initParams = JSON.parse(paramsJson);
        } catch (e) {
          //非json的情况下，未旧的数据接口参数（字符串），显示旧的输入框
          $('.DATA_STORE_TYPE_DATA_INTERFACE:eq(1),.DATA_STORE_TYPE_DATA_INTERFACE:eq(2)').show();
        }
      }
      for (var i = 0; i < paramOptions.length; i++) {
        var op = paramOptions[i];
        var $div = $('<div>', {
          class: 'form-group DATA_STORE_TYPE DATA_STORE_TYPE_DATA_INTERFACE PARAMS',
          PARAM_KEY: op.id
        });
        $div.append($('<label>', { class: 'col-sm-2 control-label' }).text(op.name), $('<div>', { class: 'col-sm-10' }));
        $div.insertAfter($last);
        var $elementContainer = $div.find('div');
        if (op.domType == 'INPUT') {
          $elementContainer.append(
            $('<input>', {
              type: 'text',
              class: 'form-control',
              name: op.id,
              placeholder: op.placeholder,
              value: initParams[op.id]
            })
          );
        } else if (op.domType == 'SELECT' || op.domType == 'MULTI_SELECT') {
          $elementContainer.append(
            $('<input>', {
              type: 'hidden',
              class: 'form-control',
              name: op.id,
              value: initParams[op.id]
            })
          );
          var select2options = {
            remoteSearch: false,
            multiple: op.domType == 'MULTI_SELECT'
          };
          if (op.dataJSON) {
            var dataJSON = JSON.parse(op.dataJSON);
            select2options.data = [];
            for (var opd in dataJSON) {
              select2options.data.push({ id: dataJSON[opd], text: opd });
            }
          } else if (op.service) {
            select2options.serviceName = op.service.split('.')[0];
            select2options.queryMethod = op.service.split('.')[1];
          }
          $elementContainer.find('input').wSelect2(select2options);
        } else if (op.domType == 'RADIO' || op.domType == 'CHECKBOX') {
          if (op.dataJSON) {
            var domprefix = op.domType == 'CHECKBOX' ? 'checkbox' : 'radio';
            var dataJSON = JSON.parse(op.dataJSON);
            for (var d in dataJSON) {
              $elementContainer.append(
                $('<label>', { class: domprefix + '-inline' }).append(
                  $('<input>', {
                    type: domprefix,
                    name: op.id,
                    value: dataJSON[d],
                    checked: $.inArray(dataJSON[d] + '', initParams[op.id] + '') != -1
                  }),
                  d
                )
              );
            }
          }
        } else if (op.domType == 'ORG_SELECT') {
          //TODO: 组织选择
        } else if (op.domType == 'DATA_DIC_SELECT') {
          //TODO: 数据字典选择
        }
      }
    },

    buildDataProvider: function (id, $previewtable) {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      return new DataStore({
        dataStoreId: id,
        onDataChange: function (data, count, params) {
          var dataProvider = $previewtable.data('_dataProvider');
          var data = dataProvider.getData();
          var total = dataProvider.getCount();
          params.loadSuccess({
            rows: data,
            total: total
          });

          if (total > 0 && data.length == 0) {
            $previewtable.bootstrapTable('selectPage', 1);
          }
        },
        defaultCriterions: [
          {
            sql: $('#defaultCondition', $container).val() || ' 1=1 '
          }
        ],
        pageSize: 25
      });
    },

    _previewDatastore: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var datastoreId = $('#id', $container).val();
      var $dialog;
      var dialogOpts = {
        title: '预览数据仓库',
        message: '<div class="container-fluid" style="max-height: 500px;overflow-y: auto;"><table id="table_preview_info"></table></div>',
        shown: function () {
          var $previewtable = $('#table_preview_info', $dialog);
          var _dataProvider = _self.buildDataProvider(datastoreId, $previewtable);
          $previewtable.data('_dataProvider', _dataProvider);
          // server.JDS.call({
          //   service: 'cdDataStoreDefinitionService.getBeanById',
          //   data: [datastoreId],
          //   version: '',
          //   success: function (result) {
          server.JDS.restfulGet({
            url: `/proxy/api/datastore/getBeanById/${datastoreId}`,
            success: function (result) {
              if (result.code == 0) {
                var columns = JSON.parse(result.data.columnsDefinition);
                var colOptions = [];
                for (var i = 0, len = columns.length; i < len; i++) {
                  colOptions.push({
                    field: columns[i].columnIndex,
                    title: columns[i].columnIndex
                  });
                }
                $previewtable.bootstrapTable('destroy').bootstrapTable({
                  data: [],
                  idField: 'UUID',
                  pagination: true,
                  striped: false,
                  columns: colOptions,
                  sidePagination: 'server',
                  showColumns: false,
                  horizontalScroll: true,
                  fixedColumns: false,
                  pageSize: 25,
                  ajax: function (request) {
                    _dataProvider.load(request.data, {
                      loadSuccess: request.success,
                      notifyChange: request.data.notifyChange
                    });
                  },
                  width: 500,
                  onLoadSuccess: function () {
                    $previewtable
                      .css({
                        'table-layout': 'initial'
                      })
                      .find('td')
                      .css({
                        'max-width': '300px'
                      });
                    $.each($previewtable.find('td'), function (index, item) {
                      $(item).attr('title', $(item).text());
                    });
                  }
                });
              }
            }
          });
        },
        size: 'large'
      };
      $dialog = appModal.dialog(dialogOpts);
    },

    _moduleId: function () {
      return $('#moduleId', this.widget.element).length ? $('#moduleId', this.widget.element).val() : this.getWidgetParams().moduleId;
    },
    _bean: function () {
      return {
        uuid: null,
        name: null,
        id: null,
        type: null,
        code: null,
        tableName: null,
        viewName: null,
        entityName: null,
        sqlStatement: null,
        dataInterfaceName: null,
        dataInterfaceParam: null,
        sqlName: null,
        camelColumnIndex: null,
        defaultCondition: null,
        defaultOrder: null,
        moduleId: this._moduleId()
      };
    },

    _collectionInterfaceParams: function () {
      //收集数据接口的参数
      var $params = $('.DATA_STORE_TYPE_DATA_INTERFACE', this.widget.element).filter('.PARAMS');
      if ($params.length > 0) {
        var params = {};
        $params.each(function () {
          var key = $(this).attr('PARAM_KEY');
          var $input = $(this).find(":input[name='" + key + "']");
          if ($input.is("[type='text']") || $input.is("[type='hidden']")) {
            params[key] = $input.val();
          } else if ($input.is("[type='radio']")) {
            params[key] = $input.filter(':checked').val();
          } else if ($input.is("[type='checkbox']")) {
            params[key] = [];
            $input.filter(':checked').each(function () {
              params[key].push($(this).val());
            });
          }
        });
        if (!$.isEmptyObject(params)) {
          return params;
        }
      }
      return null;
    },

    _saveDatastoreDefinition: function (args) {
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
      var params = this._collectionInterfaceParams();
      if (params) {
        //参数化
        bean.dataInterfaceParam = JSON.stringify(params);
      }
      _self._reloadInterfaceColumnData(bean.dataInterfaceParam);

      var columnsDefinition = $('#datastoreColumnTable', $container).bootstrapTable('getData');
      if (columnsDefinition.length == 0) {
        if (args === undefined || args < 10) {
          //延迟1秒再次请求
          setTimeout(function () {
            _self._saveDatastoreDefinition(args ? args + 1 : 1);
          }, 1000);
          return;
        }
        appModal.hideMask();
        appModal.info('列定义不允许为空!');
        return false;
      }
      bean['columnsDefinition'] = JSON.stringify(columnsDefinition);
      bean.systemUnitId = server.SpringSecurityUtils.getCurrentUserUnitId();
      bean.moduleId = _self._moduleId();
      if (!validator.form()) {
        return false;
      }

      // server.JDS.call({
      //   service: 'cdDataStoreDefinitionService.saveBean',
      //   data: [bean],
      //   version: '',
      //   success: function (result) {
      //     if (result.success) {
      server.JDS.restfulPost({
        url: '/proxy/api/datastore/saveBean',
        data: bean,
        success: function (result) {
          appModal.hideMask();
          if (result.code == 0) {
            //_self._convert2FunctionProductIntegration(result.data);

            appModal.success('保存成功！', function () {
              // 保存成功刷新列表
              listView.trigger('AppDatastoreListView.refresh');
              // 清空表单
              AppPtMgrCommons.clearForm({
                container: $container,
                includeHidden: true
              });
              $('#type', $container).trigger('change');
            });
          }
        }
      });
    },

    _convert2FunctionProductIntegration: function (uuid) {
      var _self = this;
      server.JDS.call({
        service: 'appFunctionService.syncAppFunctionSource',
        data: [uuid, 'dataStoreDefinition'],
        version: '',
        success: function (result) {
          if (result.success) {
            server.JDS.call({
              service: 'appProductIntegrationService.addAppFunction',
              data: [_self.getWidgetParams().appPiUuid, result.data[0]],
              version: '',
              success: function (result) {},
              async: false
            });
          }
        },
        async: false
      });
    },

    loadColumns: function (options) {
      var _self = this;
      if (options.url) {
        // server.JDS.call(
        //   $.extend(
        //     {
        //       version: '',
        //       async: false,
        //       success: function (result) {
        //         if (result.msg == 'success') {
        //           _self._columnTableDataRefresh(result.data);
        //         }
        //       }
        //     },
        //     options
        //   )
        // );
        server.JDS.restfulGet({
          url: options.url,
          success: function (result) {
            if (result.code == 0) {
              _self._columnTableDataRefresh(result.data);
            }
          }
        });
      }
    },

    _columnTableDataRefresh: function (rows) {
      $('#datastoreColumnTable', $(this.widget.element)).bootstrapTable('load', rows);
      $('#datastoreColumnTable', this.widget.element).bootstrapTable('resetView');
    },

    _initColumnTable: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var $table = $("<table id='datastoreColumnTable' data-height='430'></table>");
      $('#col_def', $container).html($table);
      var $columnTable = $('#datastoreColumnTable', $container);
      $columnTable.bootstrapTable('destroy').bootstrapTable({
        data: [],
        idField: 'uuid',
        pagination: false,
        striped: false,
        showColumns: false,
        onEditableHidden: function (field, row, $el, reason) {
          $el.closest('table').bootstrapTable('resetView');
        },
        width: 500,
        columns: [
          {
            field: 'uuid',
            title: 'UUID',
            visible: false
          },
          {
            field: 'title',
            title: '标题',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true,
              validate: function (value) {
                if (value == '') {
                  return '请输入标题!';
                }
              }
            }
          },
          {
            field: 'columnName',
            title: '列名'
          },
          {
            field: 'columnIndex',
            title: '列索引'
          },
          {
            field: 'dataType',
            title: '数据类型'
          },
          {
            field: 'columnType',
            title: '列类型',
            visible: false
          }
        ]
      });
    },

    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var pageContainer = _self.getPageContainer();

      // 新增
      pageContainer.off('AppDatastoreListView.editRow');
      pageContainer.on('AppDatastoreListView.editRow', function (e) {
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });
        _self._columnTableDataRefresh([]);
        _self._uneditableForm(false);
        if (e.detail.rowData) {
          if (e.detail.rowData.isRef == 1) {
            _self._uneditableForm();
          }
          //编辑
          $('#btn_update_ds_col,#btn_preview_ds', $container).show();
          // server.JDS.call({
          //   service: 'cdDataStoreDefinitionService.getBeanById',
          //   data: [e.detail.rowData.id],
          //   version: '',
          //   success: function (result) {
          server.JDS.restfulGet({
            url: `/proxy/api/datastore/getBeanById/${e.detail.rowData.id}`,
            success: function (result) {
              if (result.code == 0) {
                var bean = _self._bean();
                $.extend(bean, result.data);
                bean = result.data;

                AppPtMgrCommons.json2form({
                  json: bean,
                  container: $container
                });
                // ID只读
                $('#id', $container).prop('readonly', 'readonly');
                $('#type', $container).trigger('change');
                $('#moduleId', $container).val(bean.moduleId).trigger('change');
                _self._columnTableDataRefresh(JSON.parse(bean.columnsDefinition));
                //$("#dataInterfaceName", $container).trigger('change');
                validator.form();
              }
            }
          });
        } else {
          //新增
          $('#btn_update_ds_col,#btn_preview_ds', $container).hide();
          // 生成ID
          AppPtMgrCommons.idGenerator.generate($container.find('#id'), 'CD_DS_');
          $('#code', $container).val($('#id', $container).val().replace('CD_DS_', ''));

          // ID可编辑
          $('#id', $container).prop('readonly', '');
          $('#type', $container).trigger('change');
        }

        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');

        listView = e.detail.ui;
      });

      $('a[data-toggle="tab"]', $container).on('shown.bs.tab', function (e) {
        if ($(e.target).attr('aria-controls') == 'col_def') {
          $('#datastoreColumnTable', _self.widget.element).bootstrapTable('resetView');
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

      $('#moduleId', this.widget.element).trigger('change');

      $('#datastoreColumnTable').each(function () {
        $(this).bootstrapTable('refreshOptions', {
          editable: !uneditable
        });
      });

      //保存按钮不可用
      $('#btn_save_ds,#btn_preview_ds,#btn_update_ds_col', this.widget.element).prop('disabled', uneditable);
    }
  });
  return AppDatastoreDetailsWidgetDevelopment;
});
