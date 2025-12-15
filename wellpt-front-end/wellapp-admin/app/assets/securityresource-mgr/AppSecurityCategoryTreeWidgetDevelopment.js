define(['constant', 'commons', 'server', 'appModal', 'AppPtMgrDetailsWidgetDevelopment', 'AppPtMgrCommons', 'dataStoreBase'], function (
  constant,
  commons,
  server,
  appModal,
  AppPtMgrDetailsWidgetDevelopment,
  AppPtMgrCommons,
  DataStore
) {
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

    _formInputRender: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);

      $('#tableName', $container).wSelect2({
        serviceName: 'cdDataStoreDefinitionService',
        queryMethod: 'loadSelectDataByTable',
        remoteSearch: false
      });
      $('#tableName', $container).on('change', function (event, noLoadColumn) {
        if (!noLoadColumn) {
          // _self.loadColumns({
          //   service: 'cdDataStoreDefinitionService.loadTableColumns',
          //   data: [$('#tableName').val(), $('#camelColumnIndex')[0].checked]
          // });
          var tableName = $('#tableName', $container).val(),
            camelColumnIndex = $('#camelColumnIndex', $container)[0].checked;
          _self.loadColumns({
            url: `/proxy/api/datastore/loadTableColumns/${tableName}?camelColumnIndex=${camelColumnIndex}`
          });
        }
      });
      $('#viewName', $container).wSelect2({
        serviceName: 'cdDataStoreDefinitionService',
        queryMethod: 'loadSelectDataByView',
        remoteSearch: false
      });
      $('#viewName', $container).on('change', function (event, noLoadColumn) {
        if (!noLoadColumn) {
          // _self.loadColumns({
          //   service: 'cdDataStoreDefinitionService.loadViewColumns',
          //   data: [$('#viewName').val(), $('#camelColumnIndex')[0].checked]
          // });
          var viewName = $('#viewName', $container).val(),
            camelColumnIndex = $('#camelColumnIndex', $container)[0].checked;
          _self.loadColumns({
            url: `/proxy/api/datastore/loadViewColumns/${viewName}?camelColumnIndex=${camelColumnIndex}`
          });
        }
      });
      $('#entityName', $container).wSelect2({
        serviceName: 'cdDataStoreDefinitionService',
        queryMethod: 'loadSelectDataByEntity',
        remoteSearch: false
      });
      $('#entityName', $container).on('change', function (event, noLoadColumn) {
        if (!noLoadColumn) {
          // _self.loadColumns({
          //   service: 'cdDataStoreDefinitionService.loadEntityColumns',
          //   data: [$('#entityName').val()]
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
          //   data: [$('#sqlStatement').val(), $('#camelColumnIndex')[0].checked]
          // });
          var sqlStatement = $('#sqlStatement', $container).val(),
            camelColumnIndex = $('#camelColumnIndex', $container)[0].checked;
          _self.loadColumns({
            url: `/proxy/api/datastore/loadSqlColumns?sqlStatement=${sqlStatement}&camelColumnIndex=${camelColumnIndex}`
          });
        }
      });
      $('#sqlName', $container).wSelect2({
        serviceName: 'cdDataStoreDefinitionService',
        queryMethod: 'loadSelectDataBySqlName',
        remoteSearch: false
      });
      $('#sqlName', $container).on('change', function (event, noLoadColumn) {
        if (!noLoadColumn) {
          // _self.loadColumns({
          //   service: 'cdDataStoreDefinitionService.loadSqlNameColumns',
          //   data: [$('#sqlName').val(), $('#camelColumnIndex')[0].checked]
          // });
          var sqlName = $('#sqlName', $container).val(),
            camelColumnIndex = $('#camelColumnIndex', $container)[0].checked;
          _self.loadColumns({
            url: `/proxy/api/datastore/loadSqlNameColumns/${sqlName}?camelColumnIndex=${camelColumnIndex}`
          });
        }
      });
      $('#dataInterfaceName', $container).wSelect2({
        serviceName: 'cdDataStoreDefinitionService',
        queryMethod: 'loadSelectDataByDataInterface',
        remoteSearch: false
      });
      $('#dataInterfaceName', $container).on('change', function (event, noLoadColumn) {
        var interfaceName = $('#dataInterfaceName').val();
        var interfaceParam = $('#dataInterfaceParam').val();
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
      });

      $('#dataInterfaceParam', $container).on('input', function (event, noLoadColumn) {
        var type = $('#type').val();
        var interfaceName = $('#dataInterfaceName').val();
        var interfaceParam = $('#dataInterfaceParam').val();
        if (type == 'DATA_STORE_TYPE_DATA_INTERFACE') {
          if (interfaceName && interfaceParam && !noLoadColumn) {
            _self.loadColumns({
              service: 'cdDataStoreDefinitionService.loadDataInterfaceColumns',
              data: [interfaceName, interfaceParam]
            });
          }
        }
      });

      //类型选择
      $('#type', $container).wSelect2({
        serviceName: 'dataDictionaryService',
        params: {
          type: 'APP_PT_DATA_STORE_TYPE'
        },
        remoteSearch: false
      });

      $('#type', $container)
        .on('change', function () {
          $('.DATA_STORE_TYPE', $container).hide();
          var isNew = !$('#uuid', $container).val();
          var type = $(this).val();

          if (type) {
            //显示类型对应的表单输入域
            $('.' + type).each(function (i, item) {
              $(item).show();
              _self._columnTableDataRefresh([]);
              $(item).find('input').trigger('change', true);
            });

            if (isNew) {
              // 显示隐藏驼峰列配置
              if (type == 'DATA_STORE_TYPE_ENTITY' || type == 'DATA_STORE_TYPE_DATA_INTERFACE') {
                $('#camelColumnIndexForm').hide();
              }
            }
          }
        })
        .trigger('change');

      //保存
      $('#btn_save_ds', $container).on('click', function () {
        _self._saveDatastoreDefinition();
        return false;
      });

      //预览
      $('#btn_preview_ds', $container).on('click', function () {
        _self._previewDatastore();
        return false;
      });

      //更新列定义
      $('#btn_update_ds_col', $container).on('click', function () {
        var $container = $(widget.element);
        // 显示列定义tab内容
        $('.nav-tabs>li>a:eq(1)', $container).tab('show');
        $('.' + $('#type').val() + ' input', $container).trigger('change', false);
        return false;
      });
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
          //     service: "cdDataStoreDefinitionService.getBeanById",
          //     data: [datastoreId],
          //     version: '',
          //     success: function (result) {
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
                  pageSize: 25,
                  ajax: function (request) {
                    _dataProvider.load(request.data, {
                      loadSuccess: request.success,
                      notifyChange: request.data.notifyChange
                    });
                  },
                  width: 500
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
      return this.getWidgetParams().moduleId;
    },
    _bean: function () {
      return {
        uuid: null,
        name: null,
        id: null,
        type: null,
        code: null,
        tableName: null,
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

    _saveDatastoreDefinition: function (args) {
      var _self = this;
      var $container = $(this.widget.element);
      var bean = _self._bean();
      AppPtMgrCommons.form2json({
        json: bean,
        container: $container
      });

      var columnsDefinition = $('#datastoreColumnTable', $container).bootstrapTable('getData');
      if (columnsDefinition.length == 0) {
        if (args === undefined) {
          //延迟0.5秒再次请求
          setTimeout(_self._saveDatastoreDefinition(1), 500);
          return;
        }
        appModal.info('列定义不允许为空!');
        return false;
      }

      bean['columnsDefinition'] = JSON.stringify(columnsDefinition);

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
          if (result.code == 0) {
            appModal.success('保存成功！', function () {
              // 保存成功刷新列表
              _self.$dataStoreTableView().refresh();
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

    $dataStoreTableView: function () {
      return $('#wBootstrapTable_C8794FCD08C000018EFFB42015E0F070', this.getPageContainer().element).data('uiWBootstrapTable');
    },

    loadColumns: function (options) {
      var _self = this;
      if (options.url) {
        // server.JDS.call(
        //   $.extend(
        //     {
        //       version: '',
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
          //url: `/proxy/api/datastore/loadTableColumns/${tableName}`,
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
    },

    _initColumnTable: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
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
      pageContainer.off('AppDatastoreListView.addRow');
      pageContainer.on('AppDatastoreListView.addRow', function (e) {
        $('#btn_update_ds_col,#btn_preview_ds').hide();
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });
        // 生成ID
        AppPtMgrCommons.idGenerator.generate($container.find('#id'), 'CD_DS_');
        $('#code', $container).val($('#id', $container).val().replace('CD_DS_', ''));

        // ID可编辑
        $('#id', $container).prop('readonly', '');
        $('#type', $container).trigger('change');
        _self._columnTableDataRefresh([]);
        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');
        listView = e.detail.ui;
      });

      pageContainer.off('AppDatastoreListView.clickRow');
      pageContainer.on('AppDatastoreListView.clickRow', function (e) {
        var bean = _self._bean();

        $('#btn_update_ds_col,#btn_preview_ds').show();

        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });
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
              validator.form();
              _self._columnTableDataRefresh(JSON.parse(bean.columnsDefinition));
            }
          }
        });

        listView = e.detail.ui;
        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');
      });
    }
  });
  return AppDatastoreDetailsWidgetDevelopment;
});
