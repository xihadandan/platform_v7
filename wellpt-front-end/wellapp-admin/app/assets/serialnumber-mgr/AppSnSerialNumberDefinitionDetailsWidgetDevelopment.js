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
  var UUID = commons.UUID;
  // 平台管理_产品集成_新版流水号定义详情_HTML组件二开
  var AppSnSerialNumberDefinitionDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppSnSerialNumberDefinitionDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      // 验证器
      validator = server.Validation.validate({
        beanName: 'snSerialNumberDefinition',
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

      // 选择流水号定义使用人
      $('#ownerNames', widget.element).click(function () {
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
        label: '下一年',
        labelColSpan: 2,
        controlColSpan: 10,
        name: 'nextYearStartDate',
        value: '01-01',
        timePicker: {
          format: 'MM-DD'
        }
      });
    },

    switchFun: function (id, callback) {
      var _self = this;
      $(_self.widget.element).on('click', '#' + id, function () {
        var _val = $('input[name="' + id + '"]:checked', _self.widget.element).val();
        var $this = $(this);
        var isOpen = $(this).hasClass('active'); //当前是否开启状态
        if (isOpen) {
          $this.removeClass('active');
          //关闭
          $this.find('.switch-open').hide();
          $this.find('.switch-close').show();

          $this.find("input[type='radio']:eq(1)").prop('checked', true).trigger('change');
        } else {
          $this.addClass('active');
          //打开
          $this.find('.switch-open').show();
          $this.find('.switch-close').hide();
          $this.find("input[type='radio']:eq(0)").prop('checked', true).trigger('change');
        }
        if (callback) {
          var _val = $('input[name="' + id + '"]:checked', _self.widget.element).val();
          callback(_val);
        }
      });

      $(_self.widget.element).on('change', 'input[name="' + id + '"]', function () {
        var _val = $('input[name="' + id + '"]:checked', _self.widget.element).val();
        var $switch = $('#' + id);
        if (_val === '1') {
          $switch.addClass('active');
          $switch.find('.switch-open').show();
          $switch.find('.switch-close').hide();
        } else {
          $switch.removeClass('active');
          $switch.find('.switch-open').hide();
          $switch.find('.switch-close').show();
        }
        if (callback) {
          callback(_val);
        }
      });
    },

    buildDataProvider: function (table) {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      return new DataStore({
        dataStoreId: 'CD_DS_SN_SERIAL_NUMBER_MAINTAIN',
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
        name: null,
        id: null,
        code: null,
        categoryUuid: null,
        moduleId: this._moduleId(),
        ownerIds: null,
        ownerNames: '',
        prefix: null,
        initialValue: null,
        incremental: null,
        defaultDigits: null,
        suffix: null,
        enablePointerReset: null,
        pointerResetType: null,
        pointerResetRule: null,
        nextYearStartDate: null,
        remark: null,
        systemUnitId: null
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
      // 指针默认位数
      var enableDefaultDigits = $('input[name="enableDefaultDigits"]:checked').val();
      bean.defaultDigits = enableDefaultDigits == '1' ? bean.defaultDigits : '';
      // 是否启用指针重置
      bean.enablePointerReset = bean.enablePointerReset == '1';
      // 按周期重置
      if (bean.pointerResetType == '1') {
        bean.pointerResetRule = $("select[name='pointerResetRule1']", $container).val();
      } else if (bean.pointerResetType == '2') {
        // 按变量重置
        bean.pointerResetRule = $("input[name='pointerResetRule2']", $container).val();
      }
      bean.systemUnitId = server.SpringSecurityUtils.getCurrentUserUnitId();
      server.JDS.restfulPost({
        url: ctx + '/proxy/api/sn/serial/number/definition/save',
        data: bean,
        success: function (result) {
          if (result.success || result.code == 0) {
            appModal.success('保存成功！', function () {
              // 保存成功刷新列表
              listView.trigger('AppSnSerialNumberDefinitionListView.refresh');
              // 清空表单
              AppPtMgrCommons.clearForm({
                container: $container,
                includeHidden: true
              });
            });
          } else {
            appModal.error(result.msg);
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
              sortName: 'pointerResetType',
              sortOrder: 'asc'
            },
            {
              sortName: 'pointerResetRule',
              sortOrder: 'asc'
            },
            {
              sortName: 'pointerResetRuleValue',
              sortOrder: 'desc'
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
            field: 'pointerResetType',
            title: '计数重置类型',
            formatter: function (value, row, index) {
              if (value == '1') {
                return '按周期重置';
              } else if (value == '2') {
                return '按变量重置';
              }
              return '不重置';
            }
          },
          {
            field: 'pointerResetRule',
            title: '计数重置规则',
            formatter: function (value, row, index) {
              if (value == '10') {
                return '按年重置';
              } else if (value == '20') {
                return '按月重置';
              } else if (value == '30') {
                return '按周重置';
              } else if (value == '40') {
                return '按日重置';
              }
              return value;
            }
          },
          {
            field: 'pointerResetRuleValue',
            title: '计数重置规则值'
          },
          {
            field: 'pointer',
            title: '计数',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true,
              validate: function (value) {
                if (value == '') {
                  return '请输入计数!';
                }
              }
            }
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
            service: 'snSerialNumberMaintainFacadeService.updatePointerByUuid',
            data: [uuid, data.pointer],
            version: '',
            success: function (result) {
              appModal.success('保存成功！');
              $serialNumberMtTable.bootstrapTable('refresh');
            }
          });
        });
        return false;
      });

      // 流水号记录
      $('#btn_check_sn_record', $container).on('click', function () {
        var serialNumberDefUuid = $('#uuid', $container).val();
        var maintainUuids = [];
        var selects = $serialNumberMtTable.bootstrapTable('getSelections');
        $.each(selects, function (i, row) {
          maintainUuids.push(row.uuid);
        });
        if (maintainUuids.length == 0) {
          appModal.error('请选择流水号维护');
          return;
        }
        var dlgId = UUID.createUUID();
        var title = '流水号记录';
        var message = "<div id='" + dlgId + "'></div>";
        var dlgOptions = {
          title: title,
          message: message,
          size: 'large',
          shown: function () {
            appContext.renderWidget({
              renderTo: '#' + dlgId,
              widgetDefId: 'wBootstrapTable_C9EA738A14600001581940F5140015C0',
              forceRenderIfConflict: true,
              params: {
                serialNumberDefUuid: serialNumberDefUuid
              },
              callback: function (listViewWidget) {
                $('#' + dlgId + ' > .ui-wBootstrapTable').css({
                  overflow: 'auto'
                });
              },
              onPrepare: {
                wBootstrapTable_C9EA738A14600001581940F5140015C0: function () {
                  var listViewWidget = this;
                  listViewWidget.develops.push({
                    beforeLoadData: function () {
                      listViewWidget.addParam('serialNumberDefUuid', serialNumberDefUuid);
                      listViewWidget.addParam('maintainUuids', maintainUuids);
                    }
                  });
                }
              }
            });
          },
          buttons: {
            confirm: {
              label: '关闭',
              className: 'btn-primary',
              callback: function () {
                return true;
              }
            }
          }
        };
        appModal.dialog(dlgOptions);
      });
      // 跳号检测
      var showCheckResultDialog = function (selects, rawSelection, checkResultMap) {
        var sourceInitialValueMap = {};
        $.each(selects, function (i, rowData) {
          sourceInitialValueMap[rowData.uuid] = rowData.initialValue;
          var checkResult = checkResultMap[rowData.uuid];
          if (checkResult) {
            rowData.skipPointers = checkResult.skipPointers.join('、');
            rowData.fillSerialNos = checkResult.fillSerialNos.join('、');
          }
        });
        var dataValidate = true;
        var dlgId = UUID.createUUID();
        var title = '跳号检测结果';
        var message = "<div id='" + dlgId + "'></div>";
        var dlgOptions = {
          title: title,
          message: message,
          size: 'large',
          shown: function () {
            $('#' + dlgId)
              .bootstrapTable('destroy')
              .bootstrapTable({
                data: selects || [],
                striped: true,
                pageNumber: 1,
                pagination: true,
                sidePagination: 'client',
                pageSize: 10,
                hideTotalPageText: true,
                columns: [
                  {
                    field: 'uuid',
                    title: 'UUID',
                    visible: false
                  },
                  {
                    field: 'pointerResetType',
                    title: '计数重置类型',
                    width: '15%',
                    formatter: function (value, row, index) {
                      if (value == '1') {
                        return '按周期重置';
                      } else if (value == '2') {
                        return '按变量重置';
                      }
                      return '不重置';
                    }
                  },
                  {
                    field: 'pointerResetRule',
                    title: '计数重置规则',
                    width: '15%',
                    formatter: function (value, row, index) {
                      if (value == '10') {
                        return '按年重置';
                      } else if (value == '20') {
                        return '按月重置';
                      } else if (value == '30') {
                        return '按周重置';
                      } else if (value == '40') {
                        return '按日重置';
                      }
                      return value;
                    }
                  },
                  {
                    field: 'pointerResetRuleValue',
                    title: '计数重置规则值',
                    width: '15%'
                  },
                  {
                    field: 'pointer',
                    title: '当前计数',
                    width: '10%'
                  },
                  {
                    field: 'skipPointers',
                    title: '跳号',
                    width: '30%',
                    formatter: function (value, row, index) {
                      return '<div style="white-space: nowrap;text-overflow: ellipsis;overflow: hidden;width: 250px;">' + value + '</div>';
                    }
                  },
                  {
                    field: 'initialValue',
                    title: '可补号起始值',
                    width: '15%',
                    editable: {
                      type: 'text',
                      mode: 'inline',
                      showbuttons: false,
                      onblur: 'submit',
                      savenochange: true,
                      validate: function (value) {
                        if (value == '') {
                          dataValidate = false;
                          return '请输入可补号起始值!';
                        }
                        if (!/^\d+$/.test(value)) {
                          dataValidate = false;
                          return '请输入正整数!';
                        }
                        dataValidate = true;
                      }
                    }
                  }
                ]
              });
          },
          buttons: {
            cancel: {
              label: '关闭',
              className: 'btn-default',
              callback: function () {
                return true;
              }
            },
            confirm: {
              label: '保存',
              className: 'btn-primary',
              callback: function () {
                // 数据检验
                if (!dataValidate) {
                  console.log('数据检验', dataValidate);
                  return false;
                }

                $.each(selects, function (i, rowData) {
                  // 更新源数据
                  let selection = rawSelection[i];
                  selection.initialValue = rowData.initialValue;

                  if (rowData.initialValue == sourceInitialValueMap[rowData.uuid]) {
                    delete sourceInitialValueMap[rowData.uuid];
                  } else {
                    sourceInitialValueMap[rowData.uuid] = rowData.initialValue;
                  }
                });
                server.JDS.call({
                  service: 'snSerialNumberMaintainFacadeService.updateInitialValue',
                  data: [sourceInitialValueMap],
                  version: '',
                  success: function (result) {
                    appModal.success('保存成功！');
                  }
                });
                return true;
              }
            }
          }
        };
        appModal.dialog(dlgOptions);
      };
      $('#btn_check_sn_maintain', $container).on('click', function () {
        var serialNumberDefId = $('#id', $container).val();
        var rawSelection = $serialNumberMtTable.bootstrapTable('getSelections');
        var selects = JSON.parse(JSON.stringify(rawSelection));
        var maintainUuids = [];
        for (var i = 0, len = selects.length; i < len; i++) {
          maintainUuids.push(selects[i].uuid);
        }
        if (maintainUuids.length == 0) {
          appModal.error('请选择流水号维护');
          return;
        }
        server.JDS.call({
          service: 'snSerialNumberMaintainFacadeService.checkSerialNumber',
          data: [serialNumberDefId, maintainUuids],
          version: '',
          success: function (result) {
            var checkResultMap = result.data;
            showCheckResultDialog(selects, rawSelection, checkResultMap);
          }
        });
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
                service: 'snSerialNumberMaintainFacadeService.deleteAllByUuids',
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
      var text = $.trim($('#sn_serial_number_maintain', this.widget.element).find('.fixed-table-toolbar .search input').val());
      // 特殊字符%模糊查询转义
      if (commons.StringUtils.contains(text, '%')) {
        text = text.replace(new RegExp(/%/g), '/%');
      }
      if (text != '') {
        dataProvider.setKeyword(text);
        var orcriterion = {
          conditions: [
            {
              columnIndex: 'pointer',
              value: text,
              type: 'like'
            },
            {
              columnIndex: 'pointerResetRuleValue',
              value: text,
              type: 'like'
            }
          ],
          type: 'or'
        };
        criterions.push(orcriterion);
      }

      dataProvider.addParam('id', $('#id', this.widget.element).val());
      dataProvider.addParam('serialNumberDefUuid', $('#uuid', this.widget.element).val());
      return criterions;
    },

    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var pageContainer = _self.getPageContainer();

      // 新增
      pageContainer.off('AppSnSerialNumberDefinitionListView.editRow');
      pageContainer.on('AppSnSerialNumberDefinitionListView.editRow', function (e) {
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
          AppPtMgrCommons.idGenerator.generate($container.find('#id'), 'SN_');
          $('#code', $container).val($('#id', $container).val().replace('SN_', ''));
          // ID可编辑
          $('#id', $container).prop('readonly', false);
          $('#sn_serial_number_maintain_li', $container).hide();
          $('#categoryUuid', $container).trigger('change');
          $('#moduleId', $container).trigger('change');
          // 新年度开始时间默认值
          AppPtMgrCommons.json2form({
            json: {
              initialValue: '1',
              incremental: '1',
              enableDefaultDigits: '1',
              defaultDigits: '3',
              enablePointerReset: '0',
              pointerResetType: '1',
              pointerResetRule1: '10',
              nextYearStartDate: '01-01'
            },
            container: $container
          });
          $('input[name="enableDefaultDigits"]', $container).trigger('change');
          $('input[name="enablePointerReset"]', $container).trigger('change');
          $('select[name="pointerResetType"]', $container).trigger('change');
        } else {
          $('#sn_serial_number_maintain_li', $container).show();
          //编辑
          server.JDS.restfulGet({
            url: ctx + '/proxy/api/sn/serial/number/definition/get',
            data: {
              uuid: e.detail.rowData.uuid
            },
            contentType: 'application/x-www-form-urlencoded',
            success: function (result) {
              if (result.success) {
                var bean = _self._bean();
                $.extend(bean, result.data);
                bean = result.data;

                AppPtMgrCommons.json2form({
                  json: bean,
                  container: $container
                });

                // 指针默认位数开关
                AppPtMgrCommons.json2form({
                  json: {
                    enableDefaultDigits: commons.StringUtils.isNotBlank(bean.defaultDigits) ? '1' : '0'
                  },
                  container: $container
                });

                // 按周期重置
                if (bean.pointerResetType == '1') {
                  $("select[name='pointerResetRule1']", $container).val(bean.pointerResetRule);
                } else if (bean.pointerResetType == '2') {
                  // 按变量重置
                  $("input[name='pointerResetRule2']", $container).val(bean.pointerResetRule);
                }
                // ID只读
                $('#id', $container).prop('readonly', true);
                validator.form();

                $('#categoryUuid', $container).trigger('change');
                $('#moduleId', $container).trigger('change');
                $('#moduleId', $container).prop('readonly', bean.moduleId != null);

                $('input[name="enableDefaultDigits"]', $container).trigger('change');
                $('input[name="enablePointerReset"]', $container).trigger('change');
                $('select[name="pointerResetType"]', $container).trigger('change');

                $('#table_sn_maintain_info', $container).bootstrapTable('refresh');
                $('#table_sn_maintain_info', $container).bootstrapTable('resetView');

                _self.updateFullFormat();
              }
            }
          });
        }

        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');

        listView = e.detail.ui;
      });

      // 分类
      $('#categoryUuid', $container).wSelect2({
        valueField: 'categoryUuid',
        remoteSearch: false,
        serviceName: 'snSerialNumberCategoryFacadeService',
        queryMethod: 'loadSelectData'
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

      // 初始值
      $('#initialValue', $container).on('keyup', function () {
        _self.updateFullFormat();
      });

      // 前缀变量
      $('#prefix', $container).on('click', function () {
        _self.openVariableDialog({
          expression: $('#prefix', $container).val(),
          callback: function (result) {
            $('#prefix', $container).val(result.expression);
            _self.updateFullFormat();
          }
        });
      });

      // 后缀变量
      $('#suffix', $container).on('click', function () {
        _self.openVariableDialog({
          expression: $('#suffix', $container).val(),
          callback: function (result) {
            $('#suffix', $container).val(result.expression);
            _self.updateFullFormat();
          }
        });
      });

      // 重置周期变量
      $('#pointerResetRule2', $container).on('click', function () {
        _self.openVariableDialog({
          expression: $('#pointerResetRule2', $container).val(),
          callback: function (result) {
            $('#pointerResetRule2', $container).val(result.expression);
          }
        });
      });

      // 指针默认位数
      _self.switchFun('enableDefaultDigits', function (val) {
        if (val == '1') {
          $('#defaultDigits', $container).show();
        } else {
          $('#defaultDigits', $container).hide();
        }
        _self.updateFullFormat();
      });
      $('input[name="enableDefaultDigits"]', $container).trigger('change');
      // 默认位数变更
      $('#defaultDigits', $container).on('keyup', function () {
        _self.updateFullFormat();
      });

      // 指针重置开关
      _self.switchFun('enablePointerReset', function (val) {
        if (val == '1') {
          $('.enablePointerReset', $container).show();
        } else {
          $('.enablePointerReset', $container).hide();
        }
      });
      $('input[name="enablePointerReset"]', $container).trigger('change');

      // 指针重置类型
      $('select[name="pointerResetType"]', $container)
        .on('change', function () {
          var pointerResetType = $(this).val();
          $('.pointerResetType', $container).hide();
          $('.pointerResetType-' + pointerResetType, $container).show();
        })
        .trigger('change');

      $('#btn_save_serialnumber', $container).on('click', function () {
        _self._saveSerialNumber();
        return false;
      });

      $('a[data-toggle="tab"]', $container).on('shown.bs.tab', function (e) {
        if ($(e.target).attr('aria-controls') == 'sn_serial_number_maintain') {
          $('#table_sn_maintain_info', _self.widget.element).bootstrapTable('resetView');
        }
        return true;
      });
    },
    // 更新完整的流水号格式
    updateFullFormat: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var prefix = $('#prefix', $container).val();
      var fillString = '';
      var initialValue = parseInt($('#initialValue', $container).val()) + '';
      var enableDefaultDigits = $('input[name="enableDefaultDigits"]:checked', $container).val() == '1';
      if (enableDefaultDigits) {
        var defaultDigits = parseInt($('#defaultDigits', $container).val());
        if (defaultDigits && defaultDigits > initialValue.length) {
          for (var i = initialValue.length; i < defaultDigits; i++) {
            fillString += '0';
          }
        }
      }
      var suffix = $('#suffix', $container).val();
      var fullFormat = prefix + fillString + initialValue + suffix;
      $('.full-format', $container).text(fullFormat);
    },
    _uneditableForm: function (uneditable) {
      uneditable = uneditable == undefined ? true : Boolean(uneditable);
      AppPtMgrCommons.uneditableForm(
        {
          container: this.widget.element
        },
        uneditable
      );

      //保存按钮不可用
      $('#btn_save_serialnumber', this.widget.element).prop('disabled', uneditable);

      $('#moduleId', this.widget.element).prop('readonly', $('#moduleId').val() != '');
    },
    // 变量选择弹出框
    openVariableDialog: function (options) {
      var _self = this;
      var html = _self.getVariableDialogHtml();
      var $dialog = appModal.dialog({
        title: '表达式配置',
        message: html,
        size: 'large',
        shown: function () {
          _self.setExpressionSelect($dialog, 'defaultField', true, [
            '年',
            '月',
            '周',
            '日',
            '时',
            '分',
            '秒',
            '简年',
            '大写年',
            '大写月',
            '大写日'
          ]);
          _self.setExpressionSelect($dialog, 'workflowVariable', true, ['流程名称', '流程ID', '流程编号']);
          $('#chooseDyform', $dialog)
            .wSelect2({
              serviceName: 'dyFormFacade',
              queryMethod: 'queryAllPforms',
              selectionMethod: 'getSelectedFormDefinition',
              defaultBlank: true,
              placeholder: '请选择表单',
              width: '100%',
              height: 250
            })
            .on('change', function (event) {
              var formUuid = $('#chooseDyform', $dialog).val();
              _self.resetFormFieldVariable(formUuid, $dialog);
            });

          _self.bindSelectEvent($dialog);

          // 初始值
          if (options.expression) {
            $('textarea', $dialog).val(options.expression);
          }
        },
        buttons: {
          ok: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              if (options.callback) {
                options.callback.call(_self, { expression: $('textarea', $dialog).val() });
              }
            }
          },
          cancel: {
            label: '关闭',
            className: 'btn-default'
          }
        }
      });
    },
    bindSelectEvent: function ($dialog, $container) {
      var $selectContainer = $container || $dialog;
      $(top.document)
        .off()
        .on('click', function (e) {
          if ($('.choose-item', $selectContainer)[0] === $(e.target).parents('.well-select')[0]) return;
          $('.choose-item', $selectContainer).removeClass('well-select-visible');
        });
      $('.choose-item', $selectContainer)
        .off()
        .on('click', function (e) {
          var $this = $(this);
          e.stopPropagation();
          console.log($this);
          $this.toggleClass('well-select-visible');
          $('.choose-item', $selectContainer).each(function () {
            var _$this = $(this);
            if (!_$this.is($this)) {
              _$this.removeClass('well-select-visible');
            }
          });
        });

      $('.well-select-input', $selectContainer)
        .off()
        .on('input propertychange', function () {
          var $that = $(this);
          var keyword = $.trim($that.val()).toUpperCase();
          var $wellSelect = $that.closest('.well-select');
          var $wellSelectItem = $wellSelect.find('.well-select-item');
          var $wellSelectNotFound = $wellSelect.find('.well-select-not-found');
          var showNum = 0;
          $wellSelectItem.each(function () {
            var $this = $(this);
            if ($this.data('value').toUpperCase().indexOf(keyword) > -1 || $this.data('name').toUpperCase().indexOf(keyword) > -1) {
              $this.show();
              showNum++;
            } else {
              $this.hide();
            }
          });
          if (showNum) {
            $wellSelectNotFound.hide();
          } else {
            $wellSelectNotFound.show();
          }
        });

      $('.well-select-dropdown', $selectContainer)
        .off()
        .on('click', function (e) {
          e.stopPropagation();
        });
      $('.well-select-item', $selectContainer)
        .off()
        .on('click', function (e) {
          var $expressionControl = $('#expressionControl', $dialog)[0];
          var value = $expressionControl.value;
          var start = $expressionControl.selectionStart;
          var value1 = value.substr(0, start);
          var value2 = value.substr(start);
          var finalValue = value1 + '${' + $(this).attr('data-value') + '}' + value2;
          $('textarea', $dialog).val(finalValue);
          $('.choose-item', $dialog).removeClass('well-select-visible');
        });
    },
    resetFormFieldVariable: function (formUuid, $dialog) {
      var _self = this;
      $.ajax({
        url: '/pt/dyform/definition/getFormDefinition',
        cache: false,
        type: 'POST',
        data: {
          formUuid: formUuid,
          justDataAndDef: false
        },
        dataType: 'json',
        success: function (data) {
          var fields = data.fields || {};
          var dataList = ['表单名称', '表单ID'];
          $.each(fields, function (i, field) {
            dataList.push('表单字段.' + $.trim(field.displayName));
          });
          _self.setExpressionSelect($dialog, 'formFieldVariable', true, dataList);
          _self.bindSelectEvent($dialog, $('#formFieldVariable', $dialog));
        }
      });
    },
    getVariableDialogHtml: function () {
      return '<div class="title_expression_wrap">\
               <div class="content ">\
                  <div class=" choose-btns clear">\
                    <div id="defaultField" class="choose-item well-select"><span>插入内置变量</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia well-select-arrow"></i></div>\
                    <div id="workflowVariable" class="choose-item well-select"><span>插入流程变量</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia well-select-arrow"></i></div>\
                    <div class="well-select" style="relative: relative; float: left; width:auto; min-width:250px;" ><input type="text" class="form-control" id="chooseDyform" name="chooseDyform" placeholder="选择表单" /></div>\
                    <div id="formFieldVariable" class="choose-item well-select"><span>插入表单变量</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia well-select-arrow"></i></div>\
                  </div>\
                  <textarea id="expressionControl" class="form-control" rows="10"></textarea>\
               </div>\
             </div>';
    },
    setExpressionSelect: function ($dialog, id, showSearch, data) {
      $dialog
        .find('#' + id)
        .find('.well-select-dropdown')
        .remove();
      $dialog
        .find('#' + id)
        .append(
          '<div class="well-select-dropdown" x-placement="bottom-start"' +
            '    style="position: absolute; left: 0; top: 34px; width: 300px;">' +
            '    <div class="well-select-search" style="display: ' +
            (showSearch ? 'block' : 'none') +
            ';"><input class="well-select-input" placeholder="搜索">' +
            '        <div class="search-icon"><i class="iconfont icon-ptkj-sousuochaxun"></i></div>' +
            '    </div>' +
            '    <ul class="well-select-not-found" style="display: none;">' +
            '        <li>无匹配数据</li>' +
            '    </ul>' +
            '    <ul class="well-select-dropdown-list"></ul>' +
            '</div>'
        );
      if (id === 'defaultField') {
        $.each(data, function (i, item) {
          $dialog
            .find('#defaultField .well-select-dropdown-list')
            .append('<li class="well-select-item" data-name="' + item + '" data-value="' + item + '"><span>' + item + '</span></li>');
        });
      } else if (id === 'workflowVariable') {
        $.each(data, function (i, item) {
          $dialog
            .find('#workflowVariable .well-select-dropdown-list')
            .append('<li class="well-select-item" data-name="' + item + '" data-value="' + item + '"><span>' + item + '</span></li>');
        });
      } else if (id == 'formFieldVariable') {
        var displayNameMap = {};
        $.each(data, function (i, item) {
          // 重复的显示名称不显示
          if (displayNameMap[item]) {
            return;
          } else {
            displayNameMap[item] = item;
          }
          $dialog
            .find('#formFieldVariable .well-select-dropdown-list')
            .append('<li class="well-select-item" data-name="' + item + '" data-value="' + item + '"><span>' + item + '</span></li>');
        });
      }
    }
  });
  return AppSnSerialNumberDefinitionDetailsWidgetDevelopment;
});
