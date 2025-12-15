define([
  'constant',
  'commons',
  'server',
  'appModal',
  'AppPtMgrDetailsWidgetDevelopment',
  'AppPtMgrCommons',
  'dataStoreBase',
  'formBuilder',
  'multiOrg',
  'select2',
  'system-admin'
], function (constant, commons, server, appModal, AppPtMgrDetailsWidgetDevelopment, AppPtMgrCommons, DataStore, formBuilder, multiOrg) {
  var validator;
  var listView;
  var iptags;
  // 平台管理_产品集成_定时任务详情_HTML组件二开
  var AppJobDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  var entityColumns = {};
  var orginalColumnData = [];

  // 接口方法
  commons.inherit(AppJobDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      var form_selector = '#job_basic_info_form';
      // 验证器
      validator = $.common.validation.validate(form_selector, 'jobDetails');

      _self._formInputRender();

      // 绑定事件
      _self._bindEvents();
    },

    _formInputRender: function () {
      var _self = this;
      var $container = this.widget.element;

      /*$("#moduleId", $container).val(_self._moduleId());
        $("#moduleId", $container).prop('readonly', $("#moduleId").val() != '');
        //模块选择
        $("#moduleId", $container).wSelect2({
            valueField: "moduleId",
            remoteSearch: false,
            serviceName: "appModuleMgr",
            queryMethod: "loadSelectData",
        });*/
      var labeStr = '';
      for (var i = 1; i <= 31; i++) {
        labeStr +=
          "<input type='checkbox' id='repeatDay_" +
          i +
          "' value='" +
          i +
          "' " +
          "name='repeatDayOfMonth' " +
          "style='margin-top:4px;'><label for='repeatDay_" +
          i +
          "'>" +
          (i < 10 ? '0' + i : i) +
          '</label>';
      }
      $('#timingMode_month_repeat_day', $container).prepend(labeStr);

      $("input[name='timingMode']", $container).on('change', function () {
        $('.timingMode').hide();
        var $checked = $("input[name='timingMode']:checked", $container);
        if ($checked.length === 1) {
          var id = $checked.attr('id');
          $('.' + id).show();
          if (id !== 'timingMode_interval') {
            $('.timingMode_day').show();
          }
        }
      });

      formBuilder.buildDatetimepicker({
        container: '#datetimepicker_timePoint',
        label: '时间点',
        labelColSpan: 2,
        controlColSpan: 10,
        name: 'timePoint',
        timePicker: {
          format: 'time|HH:mm:ss'
        }
      });

      formBuilder.buildDatetimepicker({
        container: '#datetimepicker_startTime',
        label: '开始时间',
        labelColSpan: 2,
        controlColSpan: 10,
        name: 'startTime',
        timePicker: {
          format: 'datetime|yyyy-MM-dd HH:mm:ss'
        }
      });
      formBuilder.buildDatetimepicker({
        container: '#datetimepicker_endTime',
        label: '结束时间',
        labelColSpan: 2,
        controlColSpan: 10,
        name: 'endTime',
        timePicker: {
          format: 'datetime|yyyy-MM-dd HH:mm:ss'
        }
      });
      formBuilder.buildDatetimepicker({
        container: '#datetimepicker_repeatIntervalTime',
        label: '间隔时间',
        labelColSpan: 2,
        controlColSpan: 10,
        name: 'repeatIntervalTime',
        timePicker: {
          format: 'time|HH:mm:ss'
        }
      });

      $('#jobClassName', $container).wSelect2({
        valueField: 'jobClassName',
        remoteSearch: false,
        serviceName: 'jobDetailsService',
        queryMethod: 'loadJobClassSelection'
      });

      $('#starterName').on('click', function () {
        $.unit2.open({
          valueField: 'starter',
          labelField: 'starterName',
          title: '选择成员',
          type: 'all',
          unitId: server.SpringSecurityUtils.getCurrentUserUnitId(),
          multiple: false,
          selectTypes: 'U',
          valueFormat: 'justId',
          callback: function () {}
        });
      });

      $('#btn_task_job_trigger_his').on('click', function () {
        _self.showTaskTriggerHisDialog();
      });

      $('#assignIp').select2({
        tags: _self.getIpTags(),
        tokenSeparators: [',', ' '],
        multiple: true
      });
    },

    getIpTags: function () {
      if (iptags) {
        return iptags;
      }
      iptags = (function () {
        var tags = [];
        JDS.call({
          service: 'serverRegisterCenterFacadeService.loadIpPortSelections',
          data: [{}],
          async: false,
          version: '',
          success: function (result) {
            if (result.data && result.data.results) {
              for (var i = 0, len = result.data.results.length; i < len; i++) {
                tags.push(result.data.results[i].text);
              }
            }
          }
        });
        return tags;
      })();
      return iptags;
    },

    _moduleId: function () {
      return this.getWidgetParams().moduleId;
    },

    showTaskTriggerHisDialog: function () {
      var _self = this;
      var $dialog;
      var $container = this.widget.element;
      var name = $('#name', $container).val();
      var dialogOpts = {
        title: '任务触发历史',
        message:
          '<div class="container-fluid job-his-dialog-container" style="max-height: 600px;overflow-y: auto;">' +
          '<div id="div_job_trigger_his_toolbar" class="btn-group btn_job_trigger_his_info">\n' +
          '                <button id="btn_clea_his" type="button"\n' +
          '                        class="btn btn-danger btn-sm" title="清空历史">清空历史\n' +
          '                </button>\n' +
          '            </div>\n' +
          '            <table id="table_job_trigger_his_info" data-height="500"></table></div>',
        buttons: {
          cancel: {
            label: '关闭',
            className: 'btn-default',
            callback: function (result) {}
          }
        },
        shown: function () {
          var $hisTable = $('#table_job_trigger_his_info', $dialog);
          var _dataProvider = _self.buildDataProvider($hisTable);
          $hisTable.data('_dataProvider', _dataProvider);
          var loadedData = {};
          $hisTable.bootstrapTable('destroy').bootstrapTable({
            data: [],
            idField: 'entryId',
            uniqueId: 'entryId',
            pagination: true,
            striped: false,
            showColumns: false,
            toolbar: $('#div_job_trigger_his_toolbar', $dialog),
            sidePagination: 'server',
            width: 500,
            pageSize: 25,
            search: false,
            ajax: function (request) {
              _dataProvider.load(request.data, {
                loadSuccess: request.success,
                notifyChange: request.data.notifyChange
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
                criterions: _self._collectJobTriggerHisCriterion($dialog)
              };

              newParams.orders = [
                {
                  sortName: 'modify_time',
                  sortOrder: 'desc'
                }
              ];

              return newParams;
            },
            columns: [
              {
                field: 'entryId',
                title: 'entryId',
                visible: false
              },
              {
                field: 'instanceName',
                title: '运行实例ID'
              },
              {
                field: 'firedTime',
                title: '触发时间',
                formatter: function (value, row, index) {
                  if (value) {
                    return new Date(value).format('yyyy-MM-dd HH:mm:ss');
                  }
                  return '';
                }
              },
              {
                field: 'state',
                title: '状态',
                formatter: function (v, r, i) {
                  var STATE = {
                    FINISH: '结束',
                    EXECUTING: '运行任务中',
                    ACQUIRED: '获取任务',
                    ERROR: '执行错误'
                  };
                  return STATE[v];
                }
              },
              {
                field: 'executeTime',
                title: '执行Job时间'
              },
              {
                field: 'finishTime',
                title: '执行结束时间'
              }
            ]
          });

          $('#btn_clea_his', $dialog).on('click', function () {
            appModal.confirm('是否确定清空历史？', function (res) {
              if (res) {
                server.JDS.call({
                  service: 'qrtzFiredTriggerHisService.deleteByJobName',
                  data: [name],
                  version: '',
                  async: false,
                  success: function (result) {
                    appModal.info('清空历史成功!');
                    $hisTable.bootstrapTable('refresh');
                  }
                });
              }
            });
          });
        },
        size: 'large'
      };
      $dialog = appModal.dialog(dialogOpts);
    },

    _collectJobTriggerHisCriterion: function ($dialog) {
      var criterions = [];
      var dataProvider = $('#table_job_trigger_his_info').data('_dataProvider');
      var text = $.trim($dialog.find('.fixed-table-toolbar .search input').val());
      // 特殊字符%模糊查询转义
      if (commons.StringUtils.contains(text, '%')) {
        text = text.replace(new RegExp(/%/g), '/%');
      }
      if (text != '') {
        dataProvider.setKeyword(text);
        var orcriterion = {
          conditions: [
            {
              columnIndex: 'jobName',
              value: text,
              type: 'like'
            }
          ],
          type: 'or'
        };
        criterions.push(orcriterion);
      }
      dataProvider.addParam('jobName', $('#name', this.widget.element).val());
      return criterions;
    },

    buildDataProvider: function (table) {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      return new DataStore({
        dataStoreId: 'PT_PI_JOB_TRIGGE_HIS',
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

    _bean: function () {
      return {
        moduleId: this._moduleId(),
        uuid: null,
        creator: null,
        createTime: null,
        modifier: null,
        modifyTime: null,
        name: null,
        id: null,
        starter: null,
        code: null,
        jobClassName: null,
        type: null,
        expression: null,
        repeatInterval: null,
        remark: null,
        tenantId: null,
        nextFireTime: null,
        state: null,
        timingMode: null,
        repeatDayOfWeek: null,
        repeatDayOfMonth: null,
        repeatDayOfSeason: null,
        repeatDayOfYear: null,
        timePoint: null,
        repeatIntervalTime: null,
        repeatCount: null,
        startTime: null,
        endTime: null,
        assignIp: null,
        lastExecuteInstance: null
      };
    },

    _saveJob: function () {
      var _self = this;
      var $container = $(this.widget.element);
      var bean = _self._bean();
      AppPtMgrCommons.form2json({
        json: bean,
        container: $container
      });

      bean.systemUnitId = server.SpringSecurityUtils.getCurrentUserUnitId();

      if (bean.timingMode == '4') {
        bean.repeatDayOfSeason = $('#repeatDayOfSeason_day', $container).val() + ' ' + $('#repeatDayOfSeason_month', $container).val();
      } else if (bean.timingMode == '5') {
        bean.repeatDayOfYear = $('#repeatDayOfYear_day', $container).val() + ' ' + $('#repeatDayOfYear_month', $container).val();
      }
      if (bean.repeatDayOfWeek.length > 0) {
        bean.repeatDayOfWeek = bean.repeatDayOfWeek.join(',');
      } else {
        delete bean.repeatDayOfWeek;
      }
      if (bean.repeatDayOfMonth.length > 0) {
        bean.repeatDayOfMonth = bean.repeatDayOfMonth.join(',');
      } else {
        delete bean.repeatDayOfMonth;
      }

      if (bean.assignIp) {
        var parts = bean.assignIp.split(',');
        for (var p = 0; p < parts.length; p++) {
          if (parts[p].indexOf('/') != -1) {
            //选择的是应用节点，解析出ip:port保存
            parts[p] = parts[p].split('/')[1];
          }
        }
        bean.assignIp = parts.join(',');
      }

      if (!validator.form()) {
        return false;
      }
      server.JDS.call({
        service: 'jobDetailsService.saveBean',
        data: [bean],
        version: '',
        success: function (result) {
          if (result.success) {
            appModal.success('保存成功！', function () {
              // 保存成功刷新列表
              listView.trigger('AppJobListView.refresh');
              // 清空表单
              AppPtMgrCommons.clearForm({
                container: $container,
                includeHidden: true,
                afterClear: function () {
                  $("input[name='timingMode']", $container).trigger('change');
                  $('#jobClassName').trigger('change');
                }
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
      pageContainer.off('AppTaskJobListView.editRow');
      pageContainer.on('AppTaskJobListView.editRow', function (e) {
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });
        $('#assignIp', $container).wellSelect('val', null);

        if (!e.detail.rowData) {
          //新增
          // 生成ID
          AppPtMgrCommons.idGenerator.generate($container.find('#id'), 'JOB_');
          $('#code', $container).val($('#id', $container).val().replace('JOB_', ''));
          // ID可编辑
          $('#id', $container).prop('readonly', false);
          $("input[name='timingMode']", $container).trigger('change');
          $('#jobClassName').trigger('change');
          $('#btn_task_job_trigger_his', $container).hide();
        } else {
          //编辑
          server.JDS.call({
            service: 'jobDetailsService.getBean',
            data: [e.detail.rowData.uuid],
            version: '',
            success: function (result) {
              if (result.success) {
                var bean = _self._bean();
                $.extend(bean, result.data);
                bean = result.data;
                if (bean.repeatDayOfWeek) {
                  bean.repeatDayOfWeek = bean.repeatDayOfWeek.split(',');
                }
                if (bean.repeatDayOfMonth) {
                  bean.repeatDayOfMonth = bean.repeatDayOfMonth.split(',');
                }
                if (bean.repeatDayOfSeason) {
                  var seasons = bean.repeatDayOfSeason.split(' ');
                  bean.repeatDayOfSeason_day = seasons[0];
                  bean.repeatDayOfSeason_month = seasons[1];
                }
                if (bean.repeatDayOfYear) {
                  var seasons = bean.repeatDayOfYear.split(' ');
                  bean.repeatDayOfYear_day = seasons[0];
                  bean.repeatDayOfYear_month = seasons[1];
                }

                AppPtMgrCommons.json2form({
                  json: bean,
                  container: $container
                });
                // ID只读
                $('#id', $container).prop('readonly', true);
                $('#btn_task_job_trigger_his', $container).show();
                $("input[name='timingMode']", $container).trigger('change');
                $('#jobClassName', $container).trigger('change');
                if (bean.starter) {
                  var userBean = OrgUtils.getUserBeanById(bean.starter);
                  $('#starterName', $container).val(userBean.userName);
                } else {
                  $('#starterName', $container).val('');
                }

                //解析ip对应的节点
                var ips = $('#assignIp', $container).val();
                if (ips) {
                  var parts = ips.split(',');
                  for (var i = 0; i < parts.length; i++) {
                    for (var j = 0; j < iptags.length; j++) {
                      var iptagsParts = iptags[j].split('/');
                      if (iptagsParts.length == 2 && iptagsParts[1] === parts[i]) {
                        parts[i] = iptags[j];
                      }
                    }
                  }
                  $('#assignIp', $container).wellSelect('val', parts.join(','));
                }
                $('#assignIp', $container).trigger('change');

                validator.form();
              }
            }
          });
        }

        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');

        listView = e.detail.ui;
      });

      $('#btn_save_task_job', $container).on('click', function () {
        _self._saveJob();
        return false;
      });
    }
  });
  return AppJobDetailsWidgetDevelopment;
});
