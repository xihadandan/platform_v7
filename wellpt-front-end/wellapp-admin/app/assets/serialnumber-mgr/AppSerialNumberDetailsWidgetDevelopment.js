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
  // 平台管理_产品集成_流水号定义详情_HTML组件二开
  var AppSerialNumberDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppSerialNumberDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      // 验证器
      validator = server.Validation.validate({
        beanName: 'serialNumber',
        container: this.widget.element,
        wrapperForm: true
      });

      _self._formInputRender();

      // 绑定事件
      _self._bindEvents();

      _self._initSerialNumberMaintainTable();
    },

    _formInputRender: function () {
      var _self = this;
      var widget = _self.getWidget();
      //可编辑
      $('#isEditor', this.widget.element).on('change', function () {
        if ($(this).prop('checked')) {
          $('.owner').show();
        } else {
          $('.owner').hide();
        }
      });

      // 选择流水号定义使用人
      $('#ownerNames').click(function () {
        $.unit2.open({
          valueField: 'ownerIds',
          labelField: 'ownerNames',
          title: '选择成员',
          type: 'all',
          unitId: server.SpringSecurityUtils.getCurrentUserUnitId(),
          multiple: true,
          selectTypes: 'all',
          valueFormat: 'justId',
          callback: function () {}
        });
      });

      formBuilder.buildDatetimepicker({
        container: '#startDate_datatimepicker',
        label: '新年度开始时间',
        labelColSpan: 2,
        controlColSpan: 10,
        name: 'startDate',
        timePicker: {
          format: 'MM-DD'
        }
      });
    },

    buildDataProvider: function (table) {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      return new DataStore({
        dataStoreId: 'PT_PI_SERIAL_NUMBER_MAINTAIN',
        onDataChange: function (data, count, params) {
          var dataProvider = table.data('_dataProvider');
          var data = dataProvider.getData();
          var total = dataProvider.getCount();
          params.loadSuccess({
            rows: data,
            total: total
          });

          if (total > 0 && data.length == 0) {
            table.bootstrapTable('selectPage', 1);
          }
        },
        /*defaultCriterions: [{
                        sql: " 1=1 "
                    }],*/
        pageSize: 25
      });
    },

    _moduleId: function () {
      return this.getWidgetParams().moduleId;
    },

    _bean: function () {
      return {
        uuid: null,
        type: null,
        name: null,
        id: null,
        moduleId: this._moduleId(),
        code: null,
        keyPart: null,
        headPart: null,
        initialValue: null,
        isFillPosition: null,
        incremental: null,
        lastPart: null,
        isFillNumber: null,
        startDate: null,
        isEditor: null,
        remark: null,
        systemUnitId: null,
        ownerIds: null,
        ownerNames: ''
      };
    },

    _saveSerialNumber: function () {
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
      server.JDS.call({
        service: 'serialNumberService.saveBean',
        data: [bean],
        version: '',
        success: function (result) {
          if (result.success) {
            appModal.success('保存成功！', function () {
              // 保存成功刷新列表
              listView.trigger('AppSerialNumberListView.refresh');
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

    _initSerialNumberMaintainTable: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var $serialNumberMtTable = $('#table_sn_maintain_info', $container);
      var _dataProvider = this.buildDataProvider($serialNumberMtTable);
      $serialNumberMtTable.data('_dataProvider', _dataProvider);
      var loadedData = {};
      $serialNumberMtTable.bootstrapTable('destroy').bootstrapTable({
        data: [],
        idField: 'uuid',
        uniqueId: 'uuid',
        pagination: false,
        striped: false,
        showColumns: false,
        toolbar: $('#div_sn_maintain_toolbar', $container),
        sidePagination: 'server',
        onEditableHidden: function (field, row, $el, reason) {
          $el.closest('table').bootstrapTable('resetView');
          var $tr = $el.closest('tr');
          var dataIndex = parseInt($tr.attr('data-index'));
          var orginalData = loadedData.rows[dataIndex];
          //比较值是否有变更
          $tr
            .find('.btn-save-mt')
            .prop(
              'disabled',
              !(
                commons.StringUtils.defaultIfBlank(orginalData.code, '') !== commons.StringUtils.defaultIfBlank(row.code, '') ||
                commons.StringUtils.defaultIfBlank(orginalData.keyPart, '') !== commons.StringUtils.defaultIfBlank(row.keyPart, '') ||
                commons.StringUtils.defaultIfBlank(orginalData.pointer, '') !== commons.StringUtils.defaultIfBlank(row.pointer, '')
              )
            );
        },
        width: 500,
        pageSize: 25,
        search: true,
        ajax: function (request) {
          _dataProvider.load(request.data, {
            loadSuccess: request.success,
            notifyChange: request.data.notifyChange
          });
        },
        onPostBody: function () {
          $serialNumberMtTable.find('tr>td').each(function () {
            var $cell = $(this);
            if ($cell.find('a').length <= 0 && $cell.text() != '-') {
              $cell.attr('title', $cell.text());
            }
          });
        },
        onLoadSuccess: function (data) {
          $.extend(true, loadedData, data);
        },
        queryParams: function (params) {
          var newParams = {
            pagination: {
              pageSize: params.pageSize,
              currentPage: params.pageNumber
            },
            criterions: _self._collectSerialNumberMaintainCriterion()
          };

          newParams.orders = [
            {
              sortName: 'code',
              sortOrder: 'asc'
            }
          ];

          return newParams;
        },
        columns: [
          {
            field: 'checked',
            checkbox: true
          },
          {
            field: 'uuid',
            title: 'UUID',
            visible: false
          },
          {
            field: 'name',
            title: '名称',
            visible: false
          },
          {
            field: 'id',
            title: 'ID',
            visible: false
          },
          {
            field: 'code',
            title: '编号',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          },
          {
            field: 'keyPart',
            title: '关键字',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          },
          {
            field: 'pointer',
            title: '指针',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true,
              validate: function (value) {
                if (value == '') {
                  return '请输入指针!';
                }
              }
            }
          },
          {
            field: 'headPart',
            title: '头部'
          },
          {
            field: 'lastPart',
            title: '尾部'
          },
          {
            field: 'moduleId',
            title: '模块ID',
            visible: false
          },
          {
            field: 'operation',
            title: '操作',
            formatter: function (value, row, index) {
              var $btn = $('<button>', {
                class: 'btn btn-sm btn-primary btn-save-mt',
                'data-uuid': row.uuid,
                disabled: ''
              }).text('保存');
              return $btn[0].outerHTML;
            }
          }
        ]
      });

      $serialNumberMtTable.on('click', '.btn-save-mt', function () {
        var uuid = $(this).attr('data-uuid');
        var data = $serialNumberMtTable.bootstrapTable('getRowByUniqueId', uuid);
        appModal.confirm('确定要修改流水号维护吗？', function (result) {
          server.JDS.call({
            service: 'serialNumberMaintainService.saveBean',
            data: [data],
            version: '',
            success: function (result) {
              appModal.success('保存成功！');
              $serialNumberMtTable.bootstrapTable('refresh');
            }
          });
        });
        return false;
      });

      //删除流水号维护
      $('#btn_delete_sn_maintain', $container).on('click', function () {
        var selects = $serialNumberMtTable.bootstrapTable('getSelections');
        if (selects.length > 0) {
          var uuids = [];
          for (var i = 0, len = selects.length; i < len; i++) {
            uuids.push(selects[i].uuid);
          }
          appModal.confirm('确定要删除流水号维护吗？', function (result) {
            if (result) {
              server.JDS.call({
                service: 'serialNumberMaintainService.deleteAllByUuids',
                data: [uuids],
                version: '',
                success: function (result) {
                  appModal.success('删除成功!');
                  $serialNumberMtTable.bootstrapTable('refresh');
                }
              });
            }
          });
        } else {
          appModal.error('请选择记录！');
        }
        return false;
      });
    },

    _collectSerialNumberMaintainCriterion: function () {
      var criterions = [];
      var dataProvider = $('#table_sn_maintain_info').data('_dataProvider');
      var text = $.trim($('#serial_number_maintain', this.widget.element).find('.fixed-table-toolbar .search input').val());
      // 特殊字符%模糊查询转义
      if (commons.StringUtils.contains(text, '%')) {
        text = text.replace(new RegExp(/%/g), '/%');
      }
      if (text != '') {
        dataProvider.setKeyword(text);
        var orcriterion = {
          conditions: [
            {
              columnIndex: 'keyPart',
              value: text,
              type: 'like'
            },
            {
              columnIndex: 'code',
              value: text,
              type: 'like'
            }
          ],
          type: 'or'
        };
        criterions.push(orcriterion);
      }

      dataProvider.addParam('id', $('#id', this.widget.element).val());
      return criterions;
    },

    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var pageContainer = _self.getPageContainer();

      // 新增
      pageContainer.off('AppSerialNumberListView.editRow');
      pageContainer.on('AppSerialNumberListView.editRow', function (e) {
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });

        _self._uneditableForm(false);

        $('#moduleId', $container).val(_self._moduleId());
        $('#moduleId', $container).prop('readonly', $('#moduleId', $container).val() != '');

        if (!e.detail.rowData) {
          //新增
          // 生成ID
          AppPtMgrCommons.idGenerator.generate($container.find('#id'), 'SNO_');
          $('#code', $container).val($('#id', $container).val().replace('SNO_', ''));
          // ID可编辑
          $('#id', $container).prop('readonly', false);
          $('#serial_number_maintain_li', $container).hide();
          $('#isEditor', $container).trigger('change');
          $('#moduleId', $container).trigger('change');
        } else {
          $('#serial_number_maintain_li', $container).show();
          //编辑
          server.JDS.call({
            service: 'serialNumberService.getBeanByUuid',
            data: [e.detail.rowData.uuid],
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
                // ID只读
                $('#id', $container).prop('readonly', true);
                validator.form();

                $('#moduleId', $container).trigger('change');
                $('#moduleId', $container).prop('readonly', bean.moduleId != null);
                $('#isEditor', $container).trigger('change');

                $('#table_sn_maintain_info', $container).bootstrapTable('refresh');
                $('#table_sn_maintain_info', $container).bootstrapTable('resetView');
              }
            }
          });
        }

        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');

        listView = e.detail.ui;
      });

      $('#moduleId', $container).val(_self._moduleId());
      $('#moduleId', $container).prop('readonly', $('#moduleId').val() != '');
      //模块选择
      $('#moduleId', $container).wSelect2({
        valueField: 'moduleId',
        remoteSearch: false,
        serviceName: 'appModuleMgr',
        queryMethod: 'loadSelectData'
      });

      $('#btn_save_serialnumber', $container).on('click', function () {
        _self._saveSerialNumber();
        return false;
      });

      $('a[data-toggle="tab"]', $container).on('shown.bs.tab', function (e) {
        if ($(e.target).attr('aria-controls') == 'serial_number_maintain') {
          $('#table_sn_maintain_info', _self.widget.element).bootstrapTable('resetView');
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

      // //表格不可编辑
      // $("#div_print_template_toolbar", this.widget.element).show();
      // if (uneditable) {
      //     $("#div_print_template_toolbar", this.widget.element).hide();
      // }

      // $("#table_print_template_info").each(function () {
      //     $(this).bootstrapTable('refreshOptions', {
      //         editable: !uneditable
      //     })
      // });

      //保存按钮不可用
      $('#btn_save_serialnumber', this.widget.element).prop('disabled', uneditable);

      $('#moduleId', this.widget.element).prop('readonly', $('#moduleId').val() != '');
    }
  });
  return AppSerialNumberDetailsWidgetDevelopment;
});
