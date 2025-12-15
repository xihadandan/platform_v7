define([
  'appContext',
  'design_commons',
  'constant',
  'commons',
  'server',
  'bootbox',
  'formBuilder',
  'appModal',
  'select2',
  'wSelect2',
  'ace_code_editor',
  'ace_ext_language_tools',
  'ace_ext_zoom_win',
  'ace_ext_code_var_snippets'
], function (appContext, designCommons, constant, commons, server, bootbox, formBuilder, appModal) {
  var component = $.ui.component.BaseComponent();
  var StringUtils = commons.StringUtils;
  var collectClass = 'w-configurer-option';
  var collectCardGridClass = 'w-card-grid-configurer-option';
  var clearChecked = function (row) {
    row.checked = false;
    return row;
  };
  var checkRequire = function (propertyNames, options, $container) {
    for (var i = 0; i < propertyNames.length; i++) {
      var propertyName = propertyNames[i];
      if (StringUtils.isBlank(options[propertyName])) {
        var title = $("label[for='" + propertyName + "']", $container).text();
        if ('exportDataType' == propertyName || 'fileName' == propertyName || 'exportTypes' == propertyName) {
          appModal.error('导出配置的' + '' + title.replace('*', '') + '不允许为空！');
        } else {
          appModal.error('基础信息的' + '' + title.replace('*', '') + '不允许为空！');
        }
        return false;
      }
    }
    return true;
  };
  var onEditHidden = function (field, row, $el, reason) {
    $el.closest('table').bootstrapTable('resetView');
  };
  var checkedFormat = function (value, row, index) {
    if (value) {
      return true;
    }
    return false;
  };
  var columnRowBean = {
    checked: false,
    uuid: '',
    header: '',
    name: '',
    dataType: '',
    className: '',
    renderer: {},
    width: '',
    hidden: '1',
    sortable: '0',
    keywordQuery: '0',
    editable: '0'
  };

  var exportColumnBean = {
    checked: false,
    uuid: '',
    header: '',
    name: '',
    renderer: {}
  };

  var clearInputValue = function ($container) {
    $container.find('.w-configurer-option').each(function () {
      var $element = $(this);
      var type = $element.attr('type');
      if (type == 'text' || type == 'hidden') {
        $element.val('');
      } else if (type == 'checkbox' || type == 'radio') {
        $element.prop('checked', false);
      }
      $element.trigger('change');
    });
  };

  var loadColumnNames = function (alwaysLoad) {
    // var source = [];
    var dataStoreId = $('#dataStoreId').val();
    var dataStoreIdKey = 'data-' + dataStoreId;
    var nameSource = $('#dataStoreId').data(dataStoreIdKey);
    if (nameSource == null || typeof nameSource === 'undefined') {
      server.JDS.call({
        service: 'viewComponentService.getColumnsById',
        data: [dataStoreId],
        async: false,
        success: function (result) {
          if (result.msg == 'success') {
            // nameSource = [].concat([{
            //     value: '', text: '', dataType: '', title: '', id: ''
            // }]);
            nameSource = $.map(result.data, function (data) {
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
      $('#dataStoreId').data(dataStoreIdKey, nameSource);
    }
    // 必须套一个空数组来合并，不然每次会被 wselect2 初始化的时候给添加一个空字符值尽量
    return [].concat(nameSource);
  };
  var loadOperator = function () {
    var operatorSource = [];
    server.JDS.call({
      service: 'viewComponentService.getQueryOperators',
      async: false,
      success: function (result) {
        if (result.msg == 'success') {
          operatorSource = result.data;
        }
      }
    });
    return operatorSource;
  };

  var configurer = $.ui.component.BaseComponentConfigurer();
  configurer.prototype.initBaseInfo = function (configuration, $container) {
    var _self = this;
    // 序号默认隐藏
    if (typeof configuration.hideColumnIndex === 'undefined') {
      configuration.hideColumnIndex = true;
    }

    designCommons.setElementValue(configuration, $container, ['query', 'exportDataType']);

    // 数据源
    $('#dataStoreId', $container).wSelect2({
      serviceName: 'viewComponentService',
      queryMethod: 'loadSelectData',
      labelField: 'dataStoreName',
      valueField: 'dataStoreId',
      remoteSearch: false,
      params: {
        piUuid: _self.component.pageDesigner.getPiUuid()
      }
    });
    if (configuration.dataStoreId) {
      if ($('#showColumns', $container).val() == 'true' || configuration.showColumns) {
        $('#showColumnsSwitch', $container).trigger('click', true);
      }
    }

    $('#dataStoreId', $container).change(function () {
      // 数据仓库变更时，相关配置项重置开关
      if ($('#resetConfigurationOnDataStoreChange', $container).attr('checked') != 'checked') {
        return;
      }
      var newColumnNames = loadColumnNames(true);
      var columns = $.map(newColumnNames, function (column) {
        var bean = $.extend({}, columnRowBean);
        bean.uuid = commons.UUID.createUUID();
        bean.header = column.title;
        bean.name = column.id;
        bean.dataType = column.dataType;
        return bean;
      });
      var $columnInfoTable = $('#table_column_info', $container);
      $columnInfoTable.bootstrapTable('removeAll').bootstrapTable('load', columns);
      clearInputValue($('#widget_bootstrap_table_tabs_query_info', $container));
      var $sortInfoTable = $('#table_sort_info', $container);
      $sortInfoTable.bootstrapTable('removeAll');

      //导出配置
      var exportColumns = $.map(newColumnNames, function (column) {
        var bean = $.extend({}, exportColumnBean);
        bean.uuid = commons.UUID.createUUID();
        bean.header = column.title;
        bean.name = column.id;
        return bean;
      });
      var $exportInfoTable = $('#table_export_info', $container);
      $exportInfoTable.bootstrapTable('removeAll').bootstrapTable('load', exportColumns);

      $('#readMarkerField').val('').wSelect2({
        data: newColumnNames
      });

      $('#treeField').val('').wSelect2({
        data: newColumnNames
      });
      $('#parentIdField').val('').wSelect2({
        data: newColumnNames
      });

      // 还要触发一次用户的自定义显示列
      if ($('#showColumns', $container).val() == 'true' || configuration.showColumns) {
        $('#showColumnsSwitch', $container).trigger('click', true);
      }
    });
    // 加载的JS模块
    $('#jsModule', $container).wSelect2({
      serviceName: 'appJavaScriptModuleMgr',
      params: {
        dependencyFilter: 'ListViewWidgetDevelopment'
      },
      labelField: 'jsModuleName',
      valueField: 'jsModule',
      remoteSearch: false,
      multiple: true
    });

    $('#seeTemplateUseInfo').on('click', function () {
      appModal.dialog({
        size: 'middle',
        title: '说明',
        message: $('#exportTemplateHelp').html()
      });
    });

    // 启用分页
    $('#pageStyle', $container).wSelect2({
      data: [
        {
          id: 'default',
          text: '默认分页'
        },
        {
          id: 'waterfall',
          text: '瀑布式分页'
        },
        {
          id: 'turn',
          text: '翻页式分页'
        }
      ],
      labelField: 'pageStyleName',
      valueField: 'pageStyle',
      remoteSearch: false
    });

    if (configuration.pagination && !configuration.pageStyle) {
      //兼容旧分页配置
      $('#pageStyle').val('default');
    }

    $('#pageStyle', $container)
      .on('change', function () {
        var v = $(this).val();
        if (v) {
          $('#div_page_definition').show();
          $('#div_page_definition .form-group').hide();
          $('#div_page_definition .pageStyle-' + v).show();
          $('#pagination').prop('checked', true);
        } else {
          $('#div_page_definition').hide();
          $('#pagination').prop('checked', false);
        }
      })
      .trigger('change');

    $('#pageList', $container).wSelect2({
      separator: constant.Separator.Comma,
      multiple: true,
      data: [
        {
          id: '5',
          text: '5'
        },
        {
          id: '10',
          text: '10'
        },
        {
          id: '15',
          text: '15'
        },
        {
          id: '20',
          text: '20'
        },
        {
          id: '50',
          text: '50'
        },
        {
          id: '100',
          text: '100'
        },
        {
          id: '200',
          text: '200'
        },
        {
          id: '500',
          text: '500'
        },
        {
          id: '1000',
          text: '1000'
        },
        {
          id: '5000',
          text: '5000'
        }
      ]
    });

    // 启用已阅未阅
    var $readMarkerDefinition = $('#div_readMarker_definition', $container);
    $('#readMarker', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $readMarkerDefinition.show();
        } else {
          $readMarkerDefinition.hide();
          clearInputValue($readMarkerDefinition);
        }
      })
      .trigger('change');
    $('#readMarkerField').wSelect2({
      data: loadColumnNames()
    });

    $('#rowClass').wSelect2({});

    /**
     * 表格列冻结
     * 表格配置项
     *  */
    $('#fixedColumns', $container)
      .on('change', function () {
        var $this = $(this);
        if ($this.attr('checked')) {
          $('#fixedNumber', $container).val(configuration.fixedNumber || 1);
          $('#fixedRightNumber', $container).val(configuration.fixedRightNumber || 0);
          $('#fixedTableColumnsSet', $container).show();
        } else {
          $('#fixedTableColumnsSet', $container).hide();
        }
      })
      .trigger('change');

    /**
     * 用户自定义选项
     * 用户自定义项
     * */
    // 自定义显示列
    if (configuration.showColumns || configuration.showColumns == undefined) {
      var $columnInfoTable = $('#table_column_info', $container);
      $('#showColumnsSwitch', $container).addClass('active');

      $columnInfoTable.bootstrapTable('showColumn', 'defaultDisplay');
      $columnInfoTable.bootstrapTable('showColumn', 'alwaysDisplay');
    } else if (configuration.showColumns == '') {
      var $columnInfoTable = $('#table_column_info', $container);
      $('#showColumnsSwitch', $container).removeClass('active');
      $('#showColumns', $container).val('false');
      $('#showColumnsSwitch', $container).siblings('.items-explain').hide();
      // 列定义tab 的隐藏列
      $columnInfoTable.bootstrapTable('hideColumn', 'defaultDisplay');
      $columnInfoTable.bootstrapTable('hideColumn', 'alwaysDisplay');
    }
    $('#showColumnsSwitch', $container).on('click', function (event, isTrigger) {
      var $columnInfoTable = $('#table_column_info', $container);
      if (isTrigger) {
        $columnInfoTable.bootstrapTable('showColumn', 'defaultDisplay');
        $columnInfoTable.bootstrapTable('showColumn', 'alwaysDisplay');
      } else {
        if ($(this).hasClass('active')) {
          $(this).removeClass('active');
          $('#showColumns', $container).val('false');
          $(this).siblings('.items-explain').hide();

          // 异常列定义tab 的隐藏列
          $columnInfoTable.bootstrapTable('hideColumn', 'defaultDisplay');
          $columnInfoTable.bootstrapTable('hideColumn', 'alwaysDisplay');
        } else {
          $(this).addClass('active');
          $('#showColumns', $container).val('true');
          $(this).siblings('.items-explain').show();

          //
          $columnInfoTable.bootstrapTable('showColumn', 'defaultDisplay');
          $columnInfoTable.bootstrapTable('showColumn', 'alwaysDisplay');
        }
      }
    });
    // 自定义冻结列
    // if (configuration.fixedColumns) {
    //   $('#fixedColumnsSwitch', $container).addClass('active');
    // }
    // $('#fixedColumnsSwitch', $container).on('click', function () {
    //   if ($(this).hasClass('active')) {
    //     $(this).removeClass('active');
    //     $('#fixedColumns', $container).val('false');
    //   } else {
    //     $(this).addClass('active');
    //     $('#fixedColumns', $container).val('true');

    //     // 自定列宽开关关闭
    //     $('#columnDndSwitch', $container).removeClass('active');
    //     $('#columnDnd', $container).val('false');
    //   }
    // });

    // 自定义列宽
    if (configuration.columnDnd || configuration.columnDnd == undefined) {
      $('#columnDndSwitch', $container).addClass('active');
      $('#columnDndSwitch', $container).siblings('.items-explain').show();
    } else {
      $('#columnDndSwitch', $container).removeClass('active');
      $('#columnDndSwitch', $container).siblings('.items-explain').hide();
    }
    $('#columnDndSwitch', $container).on('click', function () {
      if ($(this).hasClass('active')) {
        $(this).removeClass('active');
        $('#columnDnd', $container).val('false');
        $(this).siblings('.items-explain').hide();
      } else {
        $(this).addClass('active');
        $('#columnDnd', $container).val('true');
        $(this).siblings('.items-explain').show();
      }
    });
    /** 用户自定义选项结束 */

    // 自动刷新
    if (configuration.autoRefresh) {
      $('#autoRefreshSetSwitch').addClass('active');
      $('#autoRefreshSetBox', $container).show();
      $('#autoRefresh', $container).val(configuration.autoRefresh);
      $('#autoRefreshTime', $container).val(configuration.autoRefreshTime);
    } else {
      $('#autoRefreshSetSwitch').removeClass('active');
      $('#autoRefreshSetBox', $container).hide();
      $('#autoRefresh', $container).val('false');
    }
    $('#autoRefreshSetSwitch', $container).on('click', function () {
      if ($(this).hasClass('active')) {
        $(this).removeClass('active');
        $('#autoRefresh', $container).val('false');
        $('#autoRefreshSetBox', $container).hide();
      } else {
        $(this).addClass('active');
        $('#autoRefresh', $container).val('true');
        $('#autoRefreshSetBox', $container).show();
      }
    });
    // 自动刷新

    var editor = ace.edit('default-condition');
    editor.setTheme('ace/theme/clouds');
    editor.session.setMode('ace/mode/sql');
    //启用提示菜单
    ace.require('ace/ext/language_tools');
    editor.setOptions({
      enableBasicAutocompletion: true,
      enableSnippets: true,
      enableLiveAutocompletion: true,
      showPrintMargin: false,
      enableVarSnippets: {
        value: 'wComponentDataStoreCondition',
        showSnippetsTabs: ['内置变量', '常用代码逻辑'],
        scope: ['sql']
      },
      enableCodeHis: {
        relaBusizUuid: this.component.options.id,
        codeType: 'wBootstrapTable.defaultCondition',
        enable: true
      }
    });
    if (configuration.defaultCondition) {
      editor.setValue(configuration.defaultCondition);
    }
    $('#default-condition').data('codeEditor', editor);
  };

  configurer.prototype.initSortInfo = function (sorts, $container) {
    var sortData = sorts ? sorts : [];
    // 默认排序
    var $sortInfoTable = $('#table_sort_info', $container);
    // 定义添加，删除，上移，下移4按钮事件
    var sortRowBean = {
      sortOrder: 'asc'
    };
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_sort_info', 'sort', $container, sortRowBean);

    // 列定义
    var $columnInfoTable = $('#table_column_info', $container);
    $sortInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: sortData,
      idField: 'uuid',
      striped: true,
      showColumns: true,
      toolbar: $('#div_sort_toolbar', $container),
      onEditableHidden: onEditHidden,
      width: 500,
      columns: [
        {
          field: 'checked',
          formatter: checkedFormat,
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'sortName',
          title: '排序列',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            emptytext: '请选择',
            source: function () {
              var columnNames = loadColumnNames(true);
              var sortNames = $.extend(true, {}, columnNames);
              var $columnInfoTable = $('#table_column_info');
              var columns = $columnInfoTable.bootstrapTable('getData');
              var columnMap = {};
              $.each(columns, function (index, column) {
                columnMap[column.name] = column.header;
              });
              return $.map(sortNames, function (sort) {
                if (columnMap.hasOwnProperty(sort.value)) {
                  sort.text = columnMap[sort.value];
                }
                return sort;
              });
            }
          }
        },
        {
          field: 'sortOrder',
          title: '排序方式',
          width: 400,
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            emptytext: '请选择',
            source: [
              {
                value: 'asc',
                text: '升序'
              },
              {
                value: 'desc',
                text: '降序'
              }
            ]
          }
        }
      ]
    });
  };

  configurer.prototype.initTreeGrid = function (treeGrid, $container) {
    designCommons.setElementValue(treeGrid, $container);
    // 启用树形列表
    var $treeDefinition = $('.div_treegrid_definition', $container);
    $('#enableTreegrid', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $treeDefinition.show();
        } else {
          $treeDefinition.hide();
          clearInputValue($treeDefinition);
        }
      })
      .trigger('change');

    // 默认树形列表视图与默认卡片列表视图互斥
    $('#treeView', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $('#cardView', $container).removeAttr('checked');
        }
      })
      .trigger('change');

    //树形未初始化配置的默认处理
    if (treeGrid == undefined || !treeGrid.enableTreegrid) {
      $('#expandIcon', $container).val('glyphicon glyphicon-chevron-down'); //默认展开图标
      $('#collapseIcon', $container).val('glyphicon glyphicon-chevron-right'); //默认折叠图标
      $('#initTreeShowStyle', $container).val('EXPAND_ALL');
    }

    $('#parentIdField').wSelect2({
      data: loadColumnNames()
    });
    $('#treeNodeDisplayField').wSelect2({
      data: loadColumnNames()
    });

    $('#treeBuildType').wSelect2({
      data: (function () {
        return [
          {
            text: '父级字段构造树形列表',
            id: 'parentField'
          },
          {
            text: '分类多字段构造树形列表',
            id: 'multiField'
          }
        ];
      })()
    });
    $('#initTreeShowStyle').wSelect2({
      defaultBlank: false,
      data: (function () {
        return [
          {
            text: '默认全部展开',
            id: 'EXPAND_ALL'
          },
          {
            text: '默认全部折叠',
            id: 'COLLAPSE_ALL'
          },
          {
            text: '默认第一个节点展开',
            id: 'FIRST_EXPAND'
          }
        ];
      })()
    });

    $('#lastLevelDataParentTree').on('change', function () {
      if ($(this).is(':checked')) {
        $('.treegrid_parentField').show();
      } else {
        //父级字段选择清空
        $('#parentIdField').val('');
        $('#parentIdField').trigger('change');
        $('.treegrid_parentField').hide();
      }
    });

    $('#treeBuildType')
      .on('change', function () {
        var value = $(this).val();
        $('.treeBuildType').hide();
        if (value) {
          $('.treegrid_' + value).show();
        }

        if (value == 'multiField') {
          $('#lastLevelDataParentTree').trigger('change');
        }
      })
      .trigger('change');

    var treeNodes = treeGrid && treeGrid.treeNodes ? treeGrid.treeNodes : [];
    // 默认排序
    var $treeNodeTable = $('#table_tree_node_info', $container);
    //定义添加，删除，上移，下移4按钮事件
    var treeNodeBean = {
      name: '',
      nodeField: ''
    };
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_tree_node_info', 'tree_node', $container, treeNodeBean);

    // 列定义
    var $treeNodeTable = $('#table_tree_node_info', $container);
    $treeNodeTable.bootstrapTable('destroy').bootstrapTable({
      data: treeNodes,
      idField: 'uuid',
      striped: true,
      showColumns: true,
      toolbar: $('#div_tree_node_toolbar', $container),
      onEditableHidden: onEditHidden,
      width: 500,
      columns: [
        {
          field: 'checked',
          formatter: checkedFormat,
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'name',
          title: "名称<b style='color: red;'>*</b>",
          width: 100,
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            savenochange: true,
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入名称!';
              }
            }
          }
        },
        {
          field: 'nodeField',
          title: "分类字段<b style='color: red;'>*</b>",
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: loadColumnNames
          }
        },
        {
          field: 'nodeFieldDisplay',
          title: '分类显示字段',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: function () {
              return [
                {
                  value: '',
                  text: ''
                }
              ].concat(loadColumnNames());
            }
          }
        },
        {
          field: 'multiNodeData',
          title: '是否多分类<br>(通过<code>;</code>分割)',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
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

    // 选择图标
    $('.iconSelectBtn', $container).on('click', function () {
      var _thiz = this;
      $.WCommonPictureLib.show({
        selectTypes: [3],
        confirm: function (data) {
          var fileIDs = data.fileIDs;
          $(_thiz).prev().prev().val(fileIDs);
          $(_thiz).prev().prev().trigger('iconClassChange');
        }
      });
    });

    $('.treeIconClass')
      .on('iconClassChange', function () {
        $(this).next().attr('class', $(this).val());
      })
      .trigger('iconClassChange');
  };

  configurer.prototype.initCardGrid = function (cardGrid, $container) {
    designCommons.setElementValue(cardGrid, $container);
    // 启用卡片列表
    var $cardDefinition = $('.div_cardgrid_definition', $container);
    var cardGridChanged;

    $('#enableCardgrid', $container)
      .on('change', function () {
        var $buttonInfoTable = $('#table_button_info', $container);

        if ($(this).is(':checked')) {
          $cardDefinition.show();
          $('#usedIndividualCardGroup', $container).val(true);
          $buttonInfoTable.bootstrapTable('showColumn', 'cardGroup');

          if (cardGridChanged || (cardGrid && cardGrid.usedIndividualCardGroup)) {
            // 如果切换过或者保存过卡片组别，后续普通列表组别和卡片列表组织的编辑将互不影响
            cardGridChanged = true;
            return;
          } else {
            // 首次使用卡用组别，默认显示内容与普通列表组别一致
            var buttonInfoData = $buttonInfoTable.bootstrapTable('getData');
            $.each(buttonInfoData, function (idx, item) {
              $buttonInfoTable.bootstrapTable('updateCell', {
                index: idx,
                field: 'cardGroup',
                value: item.group
              });
            });
          }
        } else {
          $cardDefinition.hide();
          $buttonInfoTable.bootstrapTable('hideColumn', 'cardGroup');
        }
        cardGridChanged = true;
      })
      .trigger('change');
    cardGridChanged = false;

    // 默认卡片列表视图与默认树形列表视图互斥
    $('#cardView', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $('#treeView', $container).removeAttr('checked');
        }
      })
      .trigger('change');
    var gridColumnData = [];
    for (var i = 1; i <= 12; i++) {
      gridColumnData.push({
        id: i,
        text: i + ''
      });
    }
    // 卡片列数
    $('#gridColumn', $container).wSelect2({
      separator: constant.Separator.Comma,
      multiple: true,
      data: gridColumnData
    });
  };

  configurer.prototype.initColumnData = function (columns, $container, configuration) {
    var columnsData = columns ? columns : [];
    $.each(columnsData, function (i, item) {
      if (!item.className) {
        item.className = '';
      }
    });
    var $columnInfoTable = $('#table_column_info', $container);
    $columnInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: columnsData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      // width: 500,
      onEditableHidden: onEditHidden,
      onEditableSave: function (field, row, oldValue, $el) {
        if (field == 'idField' && row[field] == '1') {
          var data = $columnInfoTable.bootstrapTable('getData');
          $.each(data, function (index, rowData) {
            if (row != rowData) {
              rowData.idField = 0;
              $columnInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
        if (field == 'name') {
          var rowDatas = $columnInfoTable.bootstrapTable('getData');
          $.each(rowDatas, function (index, rowData) {
            if (row == rowData) {
              $.each(loadColumnNames(), function (index, val) {
                if (val.value == row.name) {
                  rowData.header = val.title;
                  rowData.dataType = val.dataType;
                }
              });
              $columnInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
      },
      toolbar: $('#div_column_toolbar', $container),
      columns: [
        {
          field: 'checked',
          formatter: checkedFormat,
          checkbox: true
        },
        {
          field: 'uuid',
          width: 120,
          title: 'UUID',
          visible: false
        },
        {
          field: 'header',
          title: '标题',
          width: 120,
          editable: {
            type: 'text',
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline'
          }
        },
        {
          field: 'name',
          title: '字段名',
          width: 120,
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: loadColumnNames
          }
        },
        {
          field: 'dataType',
          title: '数据类型',
          width: 100,
          visible: false
        },
        {
          field: 'className',
          editableOriginalTitle: '列样式',
          title:
            '列样式 <i class="iconfont icon-ptkj-tishishuoming" title="为渲染在表格td上的类，平台提供列样式class：&#10; ' +
            'table-handle (标记为操作栏，重置表格overflow样式)"></i>',
          width: 100,
          editable: {
            type: 'text',
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline'
          }
        },
        {
          field: 'width',
          title: '列宽',
          width: 80,
          editable: {
            type: 'text',
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline',
            validate: function (value) {
              if (StringUtils.isNotBlank(value)) {
                value = value.replace('%', '').replace('px', '');
                var regu = '^(([1-9][0-9]*)|([1-9][0-9]*.[0-9]+)|[0]|([0].[0-9]+))$';
                var re = new RegExp(regu);
                if (!re.test(value)) {
                  return '请输入正确的数字!';
                }
              }
            }
          }
        },
        {
          field: 'renderer',
          title: '渲染器',
          width: 120,
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            value2input: designCommons.bootstrapTable.renderOption.value2input,
            input2value: designCommons.bootstrapTable.renderOption.input2value,
            value2display: designCommons.bootstrapTable.renderOption.value2display,
            value2html: designCommons.bootstrapTable.renderOption.value2html
          }
        },
        {
          field: 'idField',
          title: '是否主键',
          width: 80,
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
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
        },
        {
          field: 'sortable',
          title: '点击排序',
          width: 80,
          formatter: function (value, row, index) {
            return designCommons.bootstrapTable.checkbox.formatter(value, 'sortable');
          },
          events: designCommons.bootstrapTable.checkbox.events
        },
        {
          field: 'hidden',
          title: '隐藏',
          width: 50,
          formatter: function (value, row, index) {
            return designCommons.bootstrapTable.checkbox.formatter(value, 'hidden');
          },
          events: designCommons.bootstrapTable.checkbox.events
        },
        {
          field: 'alwaysDisplay',
          title: '是否必选',
          visible: !!configuration.showColumns,
          editableOriginalTitle: '是否必选',
          title: '是否必选 <i class="iconfont icon-ptkj-tishishuoming" title="控制前端自定义列中，列是否必选"></i>',
          // title:
          //   "是否必选 <div class='bt-td-help-wrapper'><i class='iconfont icon-ptkj-tishishuoming'></i>" +
          //   "<div class='bt-td-help-wrapper-popup'>控制前端自定义列中，列是否必选</div>",
          width: 100,
          formatter: function (value, row, index) {
            return designCommons.bootstrapTable.checkbox.formatter(typeof value === 'undefined' ? '0' : value, 'alwaysDisplay');
          },
          events: {
            'change :checkbox': function (event, value, row, index) {
              if (this.checked) {
                row.alwaysDisplay = '1';
                row.defaultDisplay = '1';
                var $chk = $(event.target);
                $chk.closest('tr').find('input[prop="defaultDisplay"]').val(1).prop('disabled', 'disabled').prop('checked', 'checked');
              } else {
                row.alwaysDisplay = '0';
                var $chk = $(event.target);
                $chk.closest('tr').find('input[prop="defaultDisplay"]').removeProp('disabled');
              }
            }
          }
        },
        {
          field: 'defaultDisplay',
          editableOriginalTitle: '是否默认显示',
          visible: !!configuration.showColumns,
          title:
            '是否默认显示 <i class="iconfont icon-ptkj-tishishuoming" title="控制前端自定义列中的列是否默认选中、列表中是否默认显示"></i>',
          // title:
          //   "是否默认显示 <div class='bt-td-help-wrapper'><i class='iconfont icon-ptkj-tishishuoming'></i>" +
          //   "<div class='bt-td-help-wrapper-popup'>控制前端自定义列中的列是否默认选中、列表中是否默认显示</div>",
          width: 120,
          formatter: function (value, row, index) {
            var checked = row.alwaysDisplay === '1' || typeof value === 'undefined' || value === '1';
            var checkedProp = checked ? 'checked ' : '';
            var disabledProp = row.alwaysDisplay == '1' ? 'disabled ' : '';
            var html =
              '<input type="checkbox" prop="defaultDisplay"' +
              checkedProp +
              disabledProp +
              ' id="' +
              'defaultDisplay_' +
              row.uuid +
              '"' +
              '>';
            html += '<label for="' + 'defaultDisplay_' + row.uuid + '"></label>';
            return html;
          },
          events: designCommons.bootstrapTable.checkbox.events
        },
        {
          field: 'keywordQuery',
          title: '参与关键字查询',
          width: 150,
          formatter: function (value, row, index) {
            return designCommons.bootstrapTable.checkbox.formatter(value, 'keywordQuery');
          },
          events: designCommons.bootstrapTable.checkbox.events
        }
      ]
    });
  };

  configurer.prototype.initColumnInfo = function (columns, $container, configuration) {
    // 列定义
    // var $columnInfoTable = $("#table_column_info", $container);
    // 定义添加，删除，上移，下移4按钮事件
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_column_info', 'column', $container, columnRowBean);

    this.initColumnData(columns, $container, configuration);
  };

  configurer.prototype.initQueryInfo = function (queryConfiguration, $container) {
    var fieldData = [];
    var multiKeywordData = [];
    var propertyData = [];
    if (queryConfiguration) {
      if (queryConfiguration.fields) {
        fieldData = queryConfiguration.fields;
      }
      if (queryConfiguration.multiKeywordData) {
        multiKeywordData = queryConfiguration.multiKeywordData;
      }
      if (queryConfiguration.propertyData) {
        propertyData = queryConfiguration.propertyData;
      }
      // $.each(queryConfiguration, function(key, value) {
      // setElementValue(key, value);
      // });
      designCommons.setElementValue(queryConfiguration, $container);

      if (queryConfiguration.fieldRowColumns) {
        $container.find('#fieldRowColumns').val(queryConfiguration.fieldRowColumns);
      }

      if (queryConfiguration.fieldSearch) {
        $container.find('#fieldsConfigTab a').tab('show');
      } else if (queryConfiguration.keyword) {
        $container.find('#keywordConfigTab a').tab('show');
      }
    }

    // 查询
    // 按字段查询
    var $fieldSearchInfo = $('#div_field_search_info', $container);
    var $multiKeywordWrap = $('#multiKeywordWrap', $container);
    var $propertyInfoTable = $('#table_property_search_info', $container);
    var $fieldSearchInfoTable = $('#table_field_search_info', $container);
    var $fieldsConfigTab = $('#fieldsConfigTab', $container);
    $('#fieldSearch', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $fieldSearchInfo.show();
          $fieldsConfigTab.show();
        } else {
          $fieldSearchInfo.hide();
          $fieldsConfigTab.hide();
          $fieldSearchInfoTable.bootstrapTable('removeAll');
        }
      })
      .trigger('change');

    //关键字查询
    var $multiKeywordInfo = $('#div_keyword_search_info', $container);
    var $multiKeywordInfoTable = $('#table_multi_keyword_search_info', $container);
    var $keywordConfigTab = $('#keywordConfigTab', $container);
    $('#keyword', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $multiKeywordInfo.show();
          $keywordConfigTab.show();
        } else {
          $multiKeywordInfo.hide();
          $keywordConfigTab.hide();
          $multiKeywordInfoTable.bootstrapTable('removeAll');
        }
      })
      .trigger('change');

    $('#multiKeyword', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $multiKeywordWrap.show();
        } else {
          $multiKeywordWrap.hide();
          $multiKeywordInfoTable.bootstrapTable('removeAll');
        }
      })
      .trigger('change');

    function changeQueryType(queryType) {
      $('#' + queryType)
        .show()
        .siblings('.query-type-wrap')
        .hide();
      switch (queryType) {
        case 'keywordFieldQueryWrap':
          $propertyInfoTable.bootstrapTable('removeAll');
          break;
        case 'propertyQueryWrap':
          $fieldSearchInfoTable.bootstrapTable('removeAll');
          $multiKeywordInfoTable.bootstrapTable('removeAll');
          $('#keyword', $container).prop('checked', false);
          $('#multiKeyword', $container).prop('checked', false);
          $('#fieldSearch', $container).prop('checked', false);
          $('#expandFieldSearch', $container).prop('checked', false);
          break;
      }
    }
    //切换搜索类型
    var _queryType = $("input[name='queryType']:checked").val();
    if (_queryType) {
      changeQueryType(_queryType);
    }
    $("input[name='queryType']:radio").click(function () {
      _queryType = $(this).val();
      changeQueryType(_queryType);
    });

    var fieldRowBean = {
      checked: false,
      uuid: '',
      name: '',
      label: '',
      defaultValue: '',
      queryOptions: {
        queryTypeLabel: '文本框',
        queryType: 'text'
      },
      operator: 'like'
    };
    // 定义添加，删除，上移，下移4按钮事件
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_field_search_info', 'field', $container, fieldRowBean);

    var $columnInfoTable = $('#table_column_info', $container);
    var operatorSource = loadOperator();
    $fieldSearchInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: fieldData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      onClickCell: function (field, value, row, $element) {
        if (field == 'operator') {
          $('.input-sm')
            .off()
            .on('change', function () {
              if ($(this).val() == 'exists') {
                var html =
                  "<textarea name='custom_sql' type='text'id='custom_sql' style='width: 100%;border-radius: 4px;min-height:300px;'></textarea>" +
                  '<p>备注：输入查询sql语句,查询字段值为values</p>';

                appModal.dialog({
                  message: html,
                  title: '自定义sql语句',
                  size: 'middle',
                  buttons: {
                    ok: {
                      label: '确定',
                      className: 'well-btn w-btn-primary',
                      callback: function () {
                        row.custom_sql = $('#custom_sql').val();
                      }
                    },
                    cancel: {
                      label: '取消',
                      className: 'btn btn-default'
                    }
                  },
                  shown: function () {
                    $('#custom_sql').val(row.custom_sql);
                  }
                });
              }
            });
        }
      },
      onEditableSave: function (field, row, oldValue, $el) {
        if (field == 'name') {
          var columns = $columnInfoTable.bootstrapTable('getData');
          $.each(columns, function (index, column) {
            if (column.name == row.name) {
              row.dataType = column.dataType;
            }
          });
          if (StringUtils.isBlank(row.label)) {
            // 选择名称时，标题为空，设置标题为选择的名称
            $.each(columns, function (index, column) {
              if (column.name == row.name) {
                row.label = column.header;
              }
            });
            var data = $fieldSearchInfoTable.bootstrapTable('getData');
            $.each(data, function (index, rowData) {
              if (row == rowData) {
                $fieldSearchInfoTable.bootstrapTable('updateRow', index, rowData);
              }
            });
          }
        }

        // 选择查询类型为时间时，如果操作符为包含，设置为区间
        if (field == 'queryOptions' && row.queryOptions && row.queryOptions.queryType == 'date' && row.operator == 'like') {
          row.operator = 'between';
          var data = $fieldSearchInfoTable.bootstrapTable('getData');
          $.each(data, function (index, rowData) {
            if (row == rowData) {
              $fieldSearchInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
      },
      toolbar: $('#div_field_search_info_toolbar', $container),
      columns: [
        {
          field: 'checked',
          formatter: checkedFormat,
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'label',
          title: '标题',
          editable: {
            type: 'text',
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline'
          }
        },
        {
          field: 'name',
          title: '字段名',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            emptytext: '请选择',
            source: function () {
              var columns = $columnInfoTable.bootstrapTable('getData');
              return $.map(columns, function (column) {
                if (column.name) {
                  return {
                    value: column.name,
                    text: column.name,
                    dataType: column.dataType
                  };
                }
              });
            }
          }
        },
        {
          field: 'queryOptions',
          title: '查询类型',
          editable: {
            onblur: 'ignore',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            value2input: designCommons.bootstrapTable.queryFieldType.value2input,
            input2value: designCommons.bootstrapTable.queryFieldType.input2value,
            value2display: designCommons.bootstrapTable.queryFieldType.value2display,
            validate: function (value) {
              if (!value || !value.queryType) {
                return '请选择控件类型!';
              }
            }
          }
        },
        {
          field: 'defaultValue',
          title: '默认值',
          editable: {
            onblur: 'submit',
            type: 'text',
            mode: 'inline',
            showbuttons: false
          }
        },
        {
          field: 'operator',
          title: '操作符',
          editable: {
            type: 'select',
            mode: 'inline',
            onblur: 'submit',
            showbuttons: false,
            source: operatorSource
          }
        },
        {
          field: 'custom_sql',
          title: '自定义sql',
          visible: false
        }
      ]
    });
    // 定义多关键字搜索添加，删除，上移，下移4按钮事件
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_multi_keyword_search_info', 'multi_keyword', $container, fieldRowBean);

    // var $columnInfoTable = $("#table_column_info", $container);
    // var operatorSource = loadOperator();
    $multiKeywordInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: multiKeywordData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      onEditableSave: function (field, row, oldValue, $el) {
        // 选择名称时，标题为空，设置标题为选择的名称
        if (field == 'name' && StringUtils.isBlank(row.label)) {
          var columns = $columnInfoTable.bootstrapTable('getData');
          $.each(columns, function (index, column) {
            if (column.name == row.name) {
              row.label = column.header;
            }
          });
          var data = $multiKeywordInfoTable.bootstrapTable('getData');
          $.each(data, function (index, rowData) {
            if (row == rowData) {
              $multiKeywordInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
        // 选择查询类型为时间时，如果操作符为包含，设置为区间
        if (field == 'queryOptions' && row.queryOptions && row.queryOptions.queryType == 'date' && row.operator == 'like') {
          row.operator = 'in';
          var data = $multiKeywordInfoTable.bootstrapTable('getData');
          $.each(data, function (index, rowData) {
            if (row == rowData) {
              $multiKeywordInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
      },
      toolbar: $('#div_multi_keyword_search_info_toolbar', $container),
      columns: [
        {
          field: 'checked',
          formatter: checkedFormat,
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'label',
          title: '标题',
          editable: {
            type: 'text',
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline'
          }
        },
        {
          field: 'name',
          title: '字段名',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            emptytext: '请选择',
            source: function () {
              var columns = $columnInfoTable.bootstrapTable('getData');
              return $.map(columns, function (column) {
                return {
                  value: column.name,
                  text: column.header
                };
              });
            }
          }
        },
        {
          //     field: "queryOptions",
          //     title: "查询类型",
          //     editable: {
          //         onblur: "cancel",
          //         type: "wCustomForm",
          //         placement: 'bottom',
          //         savenochange: true,
          //         value2input: designCommons.bootstrapTable.queryFieldType.value2input,
          //         input2value: designCommons.bootstrapTable.queryFieldType.input2value,
          //         value2display: designCommons.bootstrapTable.queryFieldType.value2display,
          //         validate: function (value) {
          //             if (!value || !value.queryType) {
          //                 return '请选择控件类型!';
          //             }
          //         }
          //     }
          // }, {
          //     field: "defaultValue",
          //     title: "默认值",
          //     editable: {
          //         onblur: "submit",
          //         type: "text",
          //         mode: "inline",
          //         showbuttons: false
          //     }
          // }, {
          field: 'operator',
          title: '操作符',
          editable: {
            type: 'select',
            mode: 'inline',
            onblur: 'submit',
            showbuttons: false,
            source: operatorSource
          }
        },
        {
          field: 'custom_sql',
          title: '自定义sql',
          visible: false
        }
      ],
      onClickCell: function (field, value, row, $element) {
        if (field == 'operator') {
          $('.input-sm')
            .off()
            .on('change', function () {
              if ($(this).val() == 'exists') {
                var html =
                  "<textarea name='custom_sql' type='text'id='custom_sql' style='width: 100%;border-radius: 4px;min-height:300px;'></textarea>" +
                  '<p>备注：输入查询sql语句,查询字段值为values</p>';

                appModal.dialog({
                  message: html,
                  title: '自定义sql语句',
                  size: 'middle',
                  buttons: {
                    ok: {
                      label: '确定',
                      className: 'well-btn w-btn-primary',
                      callback: function () {
                        row.custom_sql = $('#custom_sql').val();
                      }
                    },
                    cancel: {
                      label: '取消',
                      className: 'btn btn-default'
                    }
                  },
                  shown: function () {
                    $('#custom_sql').val(row.custom_sql);
                  }
                });
              }
            });
        }
      }
    });

    var property_search_fieldRowBean = {
      checked: false,
      uuid: '',
      name: '',
      label: '',
      defaultValue: '',
      operator: 'eq'
    };
    // 定义多关键字搜索添加，删除，上移，下移4按钮事件
    formBuilder.bootstrapTable.initTableTopButtonToolbar(
      'table_property_search_info',
      'property',
      $container,
      property_search_fieldRowBean
    );

    // var $columnInfoTable = $("#table_column_info", $container);
    // var operatorSource = loadOperator();
    $propertyInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: propertyData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      onEditableSave: function (field, row, oldValue, $el) {
        // 选择名称时，标题为空，设置标题为选择的名称
        if (field == 'name' && StringUtils.isBlank(row.label)) {
          var columns = $columnInfoTable.bootstrapTable('getData');
          $.each(columns, function (index, column) {
            if (column.name == row.name) {
              row.label = column.header;
            }
          });
          var data = $propertyInfoTable.bootstrapTable('getData');
          $.each(data, function (index, rowData) {
            if (row == rowData) {
              $propertyInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
        // 选择查询类型为时间时，如果操作符为包含，设置为区间
        if (field == 'queryOptions' && row.queryOptions) {
          var _optionType = row.queryOptions.optionType;
          row.queryOptions.queryTypeLabel =
            _optionType === '1' ? '常量' : _optionType === '2' ? '数据字典' : _optionType === '3' ? '数据仓库' : '未知';
          var data = $propertyInfoTable.bootstrapTable('getData');
          $.each(data, function (index, rowData) {
            if (row == rowData) {
              $propertyInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
      },
      toolbar: $('#div_property_search_info_toolbar', $container),
      columns: [
        {
          field: 'checked',
          formatter: checkedFormat,
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'label',
          title: '标题',
          editable: {
            type: 'text',
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline'
          }
        },
        {
          field: 'name',
          title: '字段名',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            emptytext: '请选择',
            source: function () {
              var columns = $columnInfoTable.bootstrapTable('getData');
              return $.map(columns, function (column) {
                return {
                  value: column.name,
                  text: column.header
                };
              });
            }
          }
        },
        {
          field: 'queryOptions',
          title: '属性来源',
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'left',
            savenochange: true,
            renderParams: {
              property_source: true
            },
            emptytext: '请选择',
            value2input: designCommons.bootstrapTable.queryFieldType.value2input,
            input2value: designCommons.bootstrapTable.queryFieldType.input2value,
            value2display: designCommons.bootstrapTable.queryFieldType.value2display,
            validate: function (value) {
              if (!value) {
                return '请选择属性来源!';
              }
            }
          }
          // }, {
          //     field: "defaultValue",
          //     title: "默认值",
          //     editable: {
          //         onblur: "submit",
          //         type: "text",
          //         mode: "inline",
          //         showbuttons: false
          //     }
          // }, {
          //     field: "operator",
          //     title: "操作符",
          //     editable: {
          //         type: "select",
          //         mode: "inline",
          //         onblur: "submit",
          //         showbuttons: false,
          //         source: operatorSource
          //     }
        }
      ]
    });
  };

  configurer.prototype.initButtonInfo = function (configuration, $container) {
    var buttonData = configuration.buttons ? configuration.buttons : [];
    var piUuid = this.component.pageDesigner.getPiUuid();
    var system = appContext.getCurrentUserAppData().getSystem();
    var productUuid = system.productUuid;
    if (StringUtils.isNotBlank(system.piUuid)) {
      piUuid = system.piUuid;
    }
    // 按钮定义
    var $buttonInfoTable = $('#table_button_info', $container);

    var buttonRowBean = {
      checked: false,
      uuid: '',
      code: '',
      text: '',
      position: ['1'],
      group: '',
      cardGroup: '',
      cssStr: 'btn-bg-color',
      cssClass: 'btn-default'
    };
    // 定义添加，删除，上移，下移4按钮事件
    formBuilder.bootstrapTable.initTableTopButtonToolbar(
      'table_button_info',
      'button',
      $container,
      buttonRowBean,
      'uuid',
      function (type, datas) {
        if (type === 'delete') {
          //触发按钮变更事件
          $container.trigger('bootstrapTable.button.change', [datas]);
        }
      }
    );

    //兼容旧的事件处理配置
    for (var i = 0, len = buttonData.length; i < len; i++) {
      if (!buttonData[i].eventManger) {
        buttonData[i].eventManger = {
          eventHandler: buttonData[i].eventHandler
        };
        if (buttonData[i].eventParams) {
          $.extend(buttonData[i].eventManger, buttonData[i].eventParams);
        }
      }
    }

    $buttonInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: buttonData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      onEditableSave: function (field, row, oldValue, $el) {
        if ((field === 'text' || field === 'code') && row.text != '' && row.code != '') {
          //触发按钮变更事件
          $container.trigger('bootstrapTable.button.change');
        }
      },
      toolbar: $('#div_button_info_toolbar', $container),
      columns: [
        {
          field: 'checked',
          checkbox: true,
          formatter: checkedFormat
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'text',
          title: '名称',
          width: 80,
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入名称!';
              }
            }
          }
        },
        {
          field: 'code',
          title: '编码',
          width: 80,
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            savenochange: true,
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入编码!';
              }
              // var regu = "^[a-zA-Z\_][0-9a-zA-Z]*$";
              // var re = new RegExp(regu);
              // if (!re.test(value)) {
              // return '请输入数字、字母、下划线且不以数字开头!';
              // }
            }
          }
        },
        {
          field: 'position',
          title: '展示位置',
          width: 120,
          editable: {
            onblur: 'save',
            type: 'wCustomForm',
            renderParams: {
              columnTableData: this.getListViewTableColumnData()
            },
            placement: 'right',
            savenochange: true,
            source: [
              {
                value: '1',
                text: '表格头部'
              },
              {
                value: '2',
                text: '表格底部'
              },
              {
                value: '3',
                text: '行末'
              },
              {
                value: '4',
                text: '悬浮行上'
              },
              {
                value: '5',
                text: '行下'
              }
            ],
            validate: function (value) {
              if (!value.length) {
                return '请选择按钮位置!';
              } else {
                var tip = '';
                $.each(value, function (i, item) {
                  if (item.split('-')[0] === '4' && !item.split('-')[1]) {
                    tip = '请选择按钮浮动列!';
                  } else if (item.split('-')[0] === '5' && !item.split('-')[1]) {
                    tip = '请选择按钮行下列';
                  }
                });
                if (tip) {
                  return tip;
                }
              }
            },
            value2input: designCommons.bootstrapTable.btnPosition.value2input,
            input2value: designCommons.bootstrapTable.btnPosition.input2value,
            value2display: designCommons.bootstrapTable.btnPosition.value2display,
            value2html: designCommons.bootstrapTable.btnPosition.value2html
          }
        },
        {
          field: 'group',
          title: '普通列表组别',
          width: 100,
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit'
          }
        },
        {
          field: 'cardGroup',
          title: '卡片列表组别',
          hide: true,
          width: 100,
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit'
          }
        },
        {
          field: 'btnLib',
          title: '按钮库',
          width: 100,
          editable: {
            onblur: 'save',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            value2input: designCommons.bootstrapTable.btnLib.value2input,
            input2value: designCommons.bootstrapTable.btnLib.input2value,
            value2display: designCommons.bootstrapTable.btnLib.value2display,
            value2html: designCommons.bootstrapTable.btnLib.value2html
          }
        },
        {
          field: 'btnConstraint',
          title: '按钮约束',
          width: 100,
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            value2input: designCommons.bootstrapTable.btnConstraint.value2input,
            input2value: designCommons.bootstrapTable.btnConstraint.input2value,
            value2display: designCommons.bootstrapTable.btnConstraint.value2display
          }
        },
        {
          field: 'resource',
          title: '资源',
          width: 100,
          editable: {
            mode: 'modal',
            type: 'wCommonComboTree',
            showbuttons: false,
            onblur: 'submit',
            placement: 'bottom',
            wCommonComboTree: {
              inlineView: true,
              service: 'appProductIntegrationMgr.getTreeNodeByUuid',
              serviceParams: [piUuid, [], ['BUTTON']],
              multiSelect: false
              // 是否多选
            }
          }
        },
        {
          field: 'target',
          title: '目标位置',
          width: 100,
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'left',
            savenochange: true,
            value2input: designCommons.bootstrapTable.targePosition.value2input,
            value2display: designCommons.bootstrapTable.targePosition.value2display,
            inputCompleted: designCommons.bootstrapTable.targePosition.inputCompleted
          }
        },
        {
          field: 'eventManger',
          title: '事件管理',
          width: 100,
          editable: {
            mode: 'modal',
            onblur: 'ignore',
            type: 'wCustomForm',
            placement: 'left',
            savenochange: true,
            value2input: designCommons.bootstrapTable.eventManager.value2input,
            input2value: designCommons.bootstrapTable.eventManager.input2value,
            validate: designCommons.bootstrapTable.eventManager.validate,
            value2display: designCommons.bootstrapTable.eventManager.value2display
          }
        }
      ]
    });
  };

  configurer.prototype.initStatusInfo = function (columns, $container, configuration) {
    var readStatus = configuration.readStatus;
    if (readStatus == '1') {
      $('#readStatusSetBox').show();
      $("input[name='readStatus']").val('1');
      $('#readStatusSetSwitch').addClass('active');
    } else {
      $('#readStatusSetBox').hide();
      $("input[name='readStatus']").val('0');
      $('#readStatusSetSwitch').removeClass('active');
    }

    $('#readStatusSetSwitch')
      .off('click')
      .on('click', function () {
        if ($(this).hasClass('active')) {
          $(this).removeClass('active');
          $(this).prev().val('0');
          $('#readStatusSetBox').hide();
        } else {
          $(this).addClass('active');
          $(this).prev().val('1');
          $('#readStatusSetBox').show();
        }
      });
    var tagDataKeyData = [];
    $.each(columns, function (index, item) {
      tagDataKeyData.push({
        id: item.name,
        text: item.header,
        desc: item.name
      });
    });
    $('#tagDataKeyName').wellSelect('destroy').wellSelect({
      valueField: 'tagDataKey',
      labelField: 'tagDataKeyName',
      data: tagDataKeyData,
      showEmpty: false
    });
  };

  configurer.prototype.initExportInfo = function (configuration, $container) {
    // 定义添加，删除，上移，下移4按钮事件
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_export_info', 'export', $container, exportColumnBean);

    // 导出文件类型
    $('#exportTypes', $container).wSelect2({
      serviceName: 'viewComponentService',
      queryMethod: 'loadExportTypeSelectData',
      labelField: 'exportTypeShows',
      valueField: 'exportTypes',
      multiple: false,
      remoteSearch: false
    });
    $('#exportTypes', $container).on('change', function () {
      if ($(this).val() == 'templateExcel') {
        $('#exportTemplateContainer', $container).show();
      } else {
        $('#exportTemplateContainer', $container).hide();
      }
    });

    // 启用导出
    var $exprotDefinition = $('#div_exprot_definition', $container);
    $('#hasExport', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $exprotDefinition.show();
        } else {
          $exprotDefinition.hide();
          clearInputValue($exprotDefinition);
        }
      })
      .trigger('change');

    var getExportBtnDatas = function () {
      var buttonRows = $('#table_button_info', $container).bootstrapTable('getData');
      var selections = [];
      for (var i = 0; i < buttonRows.length; i++) {
        selections.push({
          id: buttonRows[i].uuid,
          text: buttonRows[i].text
        });
      }
      return selections;
    };
    $('#exportBtn', $container).wSelect2({
      data: getExportBtnDatas()
    });

    $container.on('bootstrapTable.button.change', function (e, args) {
      if (args) {
        var rows = args[0];
        var v = $('#exportBtn', $container).val();
        if (Array.isArray(rows)) {
          for (var a = 0; a < rows.length; a++) {
            if (rows[a].uuid == v) {
              $('#exportBtn', $container).val('');
              break;
            }
          }
        } else if (rows.uuid == v) {
          $('#exportBtn', $container).val('');
        }
      }
      $('#exportBtn', $container).wSelect2({
        data: getExportBtnDatas()
      });
    });

    //导出方式比较特殊，需要特殊处理
    if (StringUtils.isNotBlank(configuration.exportDataType)) {
      var types = configuration.exportDataType.split(';');
      $.each(types, function (index, t) {
        $container.find(':checkbox[name=exportDataType][value=' + t + ']').attr('checked', 'checked');
      });
    }
    this.initExportColumnData(configuration.exportColumns, $container);

    $('#exportTypes').trigger('change');

    //导出模板文件上传
    formBuilder.buildFileUpload({
      container: '#exportTemplateFileUpload',
      labelColSpan: '2',
      label: '导出模板',
      name: 'exportTemplateFile',
      controlOption: {
        addFileText: '上传导出模板'
      },
      value: configuration.exportTemplateFileId,
      singleFile: true
    });

    //导出前事件处理，支持js代码编写
    var editor = ace.edit('before-export-code');
    editor.setTheme('ace/theme/clouds');
    editor.session.setMode('ace/mode/javascript');
    //启用提示菜单
    ace.require('ace/ext/language_tools');
    editor.setOptions({
      enableBasicAutocompletion: true,
      enableSnippets: true,
      enableLiveAutocompletion: true,
      showPrintMargin: false,
      enableVarSnippets: 'wBootstrapTable.beforeExport',
      enableCodeHis: {
        relaBusizUuid: this.component.options.id,
        codeType: 'wBootstrapTable.beforeExportCode',
        enable: true
      }
    });
    if (configuration.beforeExportCode && configuration.beforeExportCode) {
      editor.setValue(configuration.beforeExportCode);
    }
    $('#before-export-code').data('codeEditor', editor);
  };

  configurer.prototype.initTableEventCodeInfo = function (configuration, $container) {
    var _this = this;
    var varSnippets = {
      //各个代码处理框都有不同的内置变量，值相同的内置变量是一样的，在ext-code_var_snippets.js解析使用
      beforeRenderCode: 'wBootstrapTable.render',
      afterRenderCode: 'wBootstrapTable.afterRenderCode',
      onPreBodyCode: 'wBootstrapTable.render',
      onPostBodyCode: 'wBootstrapTable.render',
      onPreButtonCode: 'wBootstrapTable.beforeRenderButton',
      onMouseOverCode: 'wBootstrapTable.clickTr',
      onMouseClickCode: 'wBootstrapTable.clickTr',
      onMouseOutCode: 'wBootstrapTable.mouseOutTr'
    };
    $('#widget_bootstrap_table_tabs_js_event_info', $container)
      .find('pre')
      .each(function () {
        var editor = ace.edit($(this).attr('id'));
        editor.setTheme('ace/theme/clouds');
        editor.session.setMode('ace/mode/javascript');
        //启用提示菜单
        ace.require('ace/ext/language_tools');
        ace.require('ace/ext/code_var_snippets');
        editor.setOptions({
          enableBasicAutocompletion: true,
          enableSnippets: true,
          enableLiveAutocompletion: true,
          showPrintMargin: false,
          enableVarSnippets: varSnippets[$(this).attr('id')],
          enableCodeHis: {
            relaBusizUuid: _this.component.options.id,
            codeType: 'wBootstrapTable.' + $(this).attr('id'),
            enable: true
          }
        });
        if (configuration.defineEventJs && configuration.defineEventJs[$(this).attr('id')]) {
          editor.setValue(configuration.defineEventJs[$(this).attr('id')]);
        }

        $(this).data('codeEditor', editor);
      });
  };

  configurer.prototype.initExportColumnData = function (columns, $container) {
    var columnsData = columns ? columns : [];
    var $exportInfoTable = $('#table_export_info', $container);

    $exportInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: columnsData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      onEditableSave: function (field, row, oldValue, $el) {
        if (field == 'idField' && row[field] == '1') {
          var data = $exportInfoTable.bootstrapTable('getData');
          $.each(data, function (index, rowData) {
            if (row != rowData) {
              rowData.idField = 0;
              $exportInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
        if (field == 'name') {
          var rowDatas = $exportInfoTable.bootstrapTable('getData');
          $.each(rowDatas, function (index, rowData) {
            if (row == rowData) {
              $.each(loadColumnNames(), function (index, val) {
                if (val.value == row.name) {
                  rowData.header = val.title;
                  rowData.dataType = val.dataType;
                }
              });
              $exportInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
      },
      toolbar: $('#div_export_info_toolbar', $container),
      columns: [
        {
          field: 'checked',
          formatter: checkedFormat,
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'header',
          title: '标题',
          width: '200px',
          editable: {
            type: 'text',
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline'
          }
        },
        {
          field: 'name',
          title: '字段名',
          width: '200px',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: loadColumnNames
          }
        },
        {
          field: 'renderer',
          title: '渲染器',
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            value2input: designCommons.bootstrapTable.renderOption.value2input,
            input2value: designCommons.bootstrapTable.renderOption.input2value,
            value2display: designCommons.bootstrapTable.renderOption.value2display
          }
        }
      ]
    });
  };

  //导入配置初始化
  configurer.prototype.initImportInfo = function (configuration, $container) {
    // 定义添加，删除2个按钮事件
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_import_info', 'import', $container, exportColumnBean);
    this.initImportColumnData(configuration.dataImport, $container);
  };

  configurer.prototype.initImportColumnData = function (columns, $container) {
    var _self = this;
    var columnsData = columns ? columns : [];
    var $importInfoTable = $('#table_import_info', $container);

    $importInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: columnsData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      toolbar: $('#div_import_info_toolbar', $container),
      columns: [
        {
          field: 'checked',
          formatter: checkedFormat,
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'dataImportBtn',
          title: '导入按钮',
          width: '200px',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: _self.getListViewTableButtonData //按钮数据源
          }
        },
        {
          field: 'dataImportConfiguration',
          title: '导入配置',
          width: '200px',
          editable: {
            mode: 'modal',
            onblur: 'ignore',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            value2input: designCommons.bootstrapTable.importConfig.value2input,
            input2value: designCommons.bootstrapTable.importConfig.input2value,
            value2display: designCommons.bootstrapTable.importConfig.value2display
          }
        }
      ]
    });
  };

  configurer.prototype.getListViewTableButtonData = function () {
    var btnDatas = [];
    var $columnInfoTable = $('#table_button_info');
    if ($columnInfoTable) {
      var buttonColumnData = $columnInfoTable.bootstrapTable('getData');
      for (var i = 0; i < buttonColumnData.length; i++) {
        btnDatas.push({
          value: buttonColumnData[i].code,
          id: buttonColumnData[i].code,
          text: buttonColumnData[i].text
        });
      }
      btnDatas = [
        {
          value: '',
          text: ''
        }
      ].concat(btnDatas);
    }
    return btnDatas;
  };
  configurer.prototype.getListViewTableColumnData = function () {
    var colDatas = [];
    var $columnInfoTable = $('#table_column_info');
    if ($columnInfoTable) {
      var columnTableData = $columnInfoTable.bootstrapTable('getData');
      var j = 1;
      for (var i = 0; i < columnTableData.length; i++) {
        if (columnTableData[i].hidden == 0) {
          colDatas.push({
            value: j + '',
            id: j + '',
            text: '[第' + j++ + '列] ' + columnTableData[i].header
          });
        }
      }
    }
    return colDatas;
  };
  configurer.prototype.onLoad = function ($container, options) {
    // 初始化页签项
    $('#widget_bootstrap_table_tabs ul a', $container).on('click', function (e) {
      e.preventDefault();
      $(this).tab('show');
      var panelId = $(this).attr('href');
      $(panelId + ' .definition_info').bootstrapTable('resetView');
    });
    var configuration = $.extend(
      true,
      {
        maxRows: 1
      },
      options.configuration
    );

    // 固定列开启时默认开启自定义列
    // if (configuration.fixedColumns) {
    //   configuration.showColumns = true;
    // }

    this.initBaseInfo(configuration, $container);
    this.initColumnInfo(configuration.columns, $container, configuration);
    this.initStatusInfo(configuration.columns, $container, configuration);
    this.initQueryInfo(configuration.query, $container);
    this.initButtonInfo(configuration, $container);
    this.initSortInfo(configuration.defaultSorts, $container);
    this.initTreeGrid(configuration.treeGrid, $container);
    this.initCardGrid(configuration.cardGrid, $container);
    this.initExportInfo(configuration, $container);
    this.initTableEventCodeInfo(configuration, $container);
    this.initImportInfo(configuration, $container);
  };
  configurer.prototype.onOk = function ($container) {
    if (this.component.isReferenceWidget()) {
      return;
    }
    var $columnInfoTable = $('#table_column_info', $container);
    var opt = designCommons.collectConfigurerData($('#widget_bootstrap_table_tabs_base_info', $container), collectClass);
    if (typeof opt.columnDnd == 'string') {
      opt.columnDnd = opt.columnDnd == 'true' ? true : false;
    } else {
      opt.columnDnd = Boolean(opt.columnDnd);
    }
    if (typeof opt.showColumns == 'string') {
      opt.showColumns = opt.showColumns == 'true' ? true : false;
    } else {
      opt.showColumns = Boolean(opt.showColumns);
    }

    //自动刷新设置
    if (typeof opt.autoRefresh == 'string') {
      opt.autoRefresh = opt.autoRefresh == 'true' ? true : false;
      if (opt.autoRefresh) {
        if (opt.autoRefreshTime) {
          if (!/(^[1-9]\d*$)/.test(opt.autoRefreshTime)) {
            appModal.error('请输入正整数!');
            return false;
          } else if (opt.autoRefreshTime < 30) {
            appModal.error('刷新频率最少30秒!');
            return false;
          }
        } else {
          appModal.error('请输入自动刷新频率数值!');
          return false;
        }
      } else {
        opt.autoRefreshTime = 60;
      }
    } else {
      opt.autoRefresh = Boolean(opt.autoRefresh);
      opt.autoRefreshTime = 60;
    }

    opt.pagination = Boolean(opt.pagination);
    // opt.editable = Boolean(opt.editable);
    opt.readMarker = Boolean(opt.readMarker);
    opt.multiSelect = Boolean(opt.multiSelect);
    opt.hideChecked = Boolean(opt.hideChecked);
    opt.clickToSelect = Boolean(opt.clickToSelect);
    opt.hideColumnHeader = Boolean(opt.hideColumnHeader);
    opt.hideColumnIndex = Boolean(opt.hideColumnIndex);
    opt.paginationStackColumnIndex = Boolean(opt.paginationStackColumnIndex);
    opt.hideTdBorder = Boolean(opt.hideTdBorder);
    opt.defaultCondition = $('#default-condition').data('codeEditor').getValue();

    opt.fixedColumns = Boolean(opt.fixedColumns);

    if (opt.jumpPage.length > 0) {
      opt.jumpPage = Boolean(opt.jumpPage[0]);
    }
    var requeryFields = ['name', 'dataStoreId', 'maxRows'];
    if (opt.readMarker) {
      requeryFields.push('readMarkerField');
    }
    if (!checkRequire(requeryFields, opt, $container)) {
      return false;
    }

    if (StringUtils.isNotBlank(opt.width)) {
      var regu = '^(([1-9][0-9]*)|([1-9][0-9]*.[0-9]+)|([0].[0-9]+))$';
      var re = new RegExp(regu);
      if (!re.test(opt.width)) {
        appModal.error('宽度必须为正浮点数!');
        return false;
      }
    }
    if (StringUtils.isNotBlank(opt.height)) {
      var regu = '^(([1-9][0-9]*)|([1-9][0-9]*.[0-9]+)|([0].[0-9]+))$';
      var re = new RegExp(regu);
      if (!re.test(opt.height)) {
        appModal.error('高度必须为正浮点数!');
        return false;
      }
    }
    if (StringUtils.isNotBlank(opt.pageSize)) {
      var regu = '^[1-9][0-9]*$';
      var re = new RegExp(regu);
      if (!re.test(opt.pageSize)) {
        appModal.error('每页条数必须为正整数!');
        return false;
      }
    }
    if (StringUtils.isNotBlank(opt.maxRows)) {
      var regu = '^[1-9][0-9]*$';
      var re = new RegExp(regu);
      if (!re.test(opt.maxRows)) {
        appModal.error('单元格数据最多行数只能输入≥1的正整数!');
        return false;
      }
    }

    if (opt.fixedColumns) {
      if (StringUtils.isNotBlank(opt.fixedNumber) && StringUtils.isNotBlank(opt.fixedRightNumber)) {
        var re = /^(0|\+?[1-9][0-9]*)$/;
        if (!re.test(opt.fixedNumber) || !re.test(opt.fixedRightNumber)) {
          appModal.error('表格冻结列必须为正整数或0!');
          return false;
        }
      } else {
        appModal.error('表格冻结列不可为空!');
        return false;
      }
    }

    // 对分页数组进行排序
    if (StringUtils.isNotBlank(opt.pageList)) {
      var pageList = opt.pageList.split(constant.Separator.Comma);
      pageList.sort(function (prev, next) {
        return prev - next;
      });
      opt.pageList = pageList.join(constant.Separator.Comma);
    }

    var columns = $columnInfoTable.bootstrapTable('getData');
    columns = $.map(columns, clearChecked);
    if (columns.length === 0) {
      appModal.error('请配置数据列！');
      return false;
    }
    /*
     * for (var i = 0; i < columns.length; i++) { var column = columns[i];
     * if (StringUtils.isBlank(column.header) && column.hidden != '1') {
     * appModal.error("展示字段标题不允许为空！"); return false; } }
     */
    // 查询
    var query = designCommons.collectConfigurerData($('#widget_bootstrap_table_tabs_query_info', $container), collectClass);
    var $fieldSearchInfoTable = $('#table_field_search_info', $container);
    var fields = $fieldSearchInfoTable.bootstrapTable('getData');

    var $multiKeywordInfoTable = $('#table_multi_keyword_search_info', $container);
    var multiKeywords = $multiKeywordInfoTable.bootstrapTable('getData');

    var $propertyInfoTable = $('#table_property_search_info', $container);
    var propertys = $propertyInfoTable.bootstrapTable('getData');

    query.keyword = Boolean(query.keyword);
    query.fieldSearch = Boolean(query.fieldSearch);
    query.expandFieldSearch = Boolean(query.expandFieldSearch);
    query.allowSaveTemplate = Boolean(query.allowSaveTemplate);
    query.multiKeyword = Boolean(query.multiKeyword);
    fields = $.map(fields, clearChecked);
    if (query.fieldSearch && fields.length === 0) {
      appModal.error('请配置字段查询内容！');
      return false;
    }
    for (var i = 0; i < fields.length; i++) {
      var field = fields[i];
      if (StringUtils.isBlank(field.name)) {
        appModal.error('字段查询的字段名不允许为空！');
        return false;
      }
      if (StringUtils.isBlank(field.label)) {
        appModal.error('字段查询的标题不允许为空！');
        return false;
      }
      if (field.operator === 'between') {
        if ($.inArray(field.queryOptions.queryType, ['date', 'text']) < 0) {
          appModal.error('字段查询中只有日期或文本才允许配置区间！');
          return false;
        }
        if (field.queryOptions.queryType === 'text' && $.inArray(field.dataType, ['String', 'Date', '']) > -1) {
          //排除字符串、日期、未知类型
          appModal.error('字段为文本时数据类型只有数字才允许配置区间！');
          return false;
        }
      }
    }
    multiKeywords = $.map(multiKeywords, clearChecked);
    if (query.multiKeyword && multiKeywords.length === 0) {
      appModal.error('请配置组合关键字查询内容！');
      return false;
    }
    for (var i = 0; i < multiKeywords.length; i++) {
      var keyword = multiKeywords[i];
      if (StringUtils.isBlank(keyword.name)) {
        appModal.error('组合关键字查询的字段名不允许为空！');
        return false;
      }
      if (StringUtils.isBlank(keyword.label)) {
        appModal.error('组合关键字查询的标题不允许为空！');
        return false;
      }
      if (keyword.operator === 'between' && keyword.queryOptions.queryType !== 'date') {
        appModal.error('组合关键字查询中只有日期才允许配置区间！');
        return false;
      }
    }

    propertys = $.map(propertys, clearChecked);
    if (query.queryType === 'propertyQueryWrap' && propertys.length === 0) {
      appModal.error('请配置属性查询内容！');
      return false;
    }
    for (var i = 0; i < propertys.length; i++) {
      var _property = propertys[i];
      if (StringUtils.isBlank(_property.name)) {
        appModal.error('属性查询的字段名不允许为空！');
        return false;
      }
      if (StringUtils.isBlank(_property.label)) {
        appModal.error('属性查询的标题不允许为空！');
        return false;
      }
      if (_property.operator === 'between' && _property.queryOptions.queryType !== 'date') {
        appModal.error('属性查询中只有日期才允许配置区间！');
        return false;
      }
    }
    // 按钮定义
    var $tableButtonInfo = $('#table_button_info', $container);
    var buttons = $tableButtonInfo.bootstrapTable('getData');

    buttons = $.map(buttons, clearChecked);
    for (var i = 0; i < buttons.length; i++) {
      var button = buttons[i];
      if (StringUtils.isBlank(button.text)) {
        appModal.error('按钮的名称不允许为空！');
        return false;
      }
      if (StringUtils.isBlank(button.code)) {
        appModal.error('按钮的编码不允许为空！');
        return false;
      }
      if (StringUtils.isBlank(button.position)) {
        appModal.error('按钮的位置不允许为空！');
        return false;
      }
      //事件管理的各参数值提到上一层，兼容老代码
      if (!$.isEmptyObject(button.eventManger)) {
        for (var ek in button.eventManger) {
          button[ek] = button.eventManger[ek];
        }
      }
    }
    // 默认排序
    var $tableSotrInfo = $('#table_sort_info', $container);
    var defaultSorts = $tableSotrInfo.bootstrapTable('getData');
    defaultSorts = $.map(defaultSorts, clearChecked);
    for (var i = 0; i < defaultSorts.length; i++) {
      var defaultSort = defaultSorts[i];
      if (StringUtils.isBlank(defaultSort.sortName)) {
        appModal.error('排序列不允许为空！');
        return false;
      }
    }

    // 树节点配置
    requeryFields = [];
    var treeGrid = designCommons.collectConfigurerData($('#widget_bootstrap_table_tabs_treeorcard_info', $container), collectClass);
    treeGrid.enableTreegrid = Boolean(treeGrid.enableTreegrid);
    treeGrid.allPageDataTreeFormate = Boolean(treeGrid.allPageDataTreeFormate);
    treeGrid.treeCollapseAll = Boolean(treeGrid.treeCollapseAll);
    treeGrid.treeView = Boolean(treeGrid.treeView);
    treeGrid.treeCascadeCheck = Boolean(treeGrid.treeCascadeCheck);
    treeGrid.lastLevelDataParentTree = Boolean(treeGrid.lastLevelDataParentTree);
    var $treeNodeTable = $('#table_tree_node_info', $container);
    var treeNodes = $treeNodeTable.bootstrapTable('getData');
    treeNodes = $.map(treeNodes, clearChecked);
    for (var i = 0; i < treeNodes.length; i++) {
      var node = treeNodes[i];
      if (StringUtils.isBlank(node.name)) {
        appModal.error('名称不允许为空！');
        return false;
      }
    }
    treeGrid.treeNodes = treeNodes;
    if (!checkRequire(requeryFields, treeGrid, $container)) {
      return false;
    }

    var status = designCommons.collectConfigurerData($('#widget_bootstrap_table_tabs_status_info', $container), collectClass);
    // console.log(status)
    opt.readStatus = status.readStatus;
    opt.tagDataKeyName = status.tagDataKeyName;
    opt.tagDataKey = status.tagDataKey;
    if (opt.readStatus == '1' && opt.tagDataKey == '') {
      appModal.error('标记数据主键不能为空!');
      return false;
    } else if (opt.readStatus == '0') {
      opt.tagDataKeyName = '';
      opt.tagDataKey = '';
    }
    // return

    // 卡片视图
    var cardGrid = designCommons.collectConfigurerData($('#widget_bootstrap_table_tabs_treeorcard_info', $container), collectCardGridClass);
    cardGrid.enableCardgrid = Boolean(cardGrid.enableCardgrid);
    cardGrid.usedIndividualCardGroup = Boolean(cardGrid.usedIndividualCardGroup);
    if (cardGrid.enableCardgrid === true) {
      if (StringUtils.isBlank(cardGrid.gridColumn)) {
        appModal.error('卡片列表的列数不能为空!');
        return false;
      }
    }
    cardGrid.cardView = Boolean(cardGrid.cardView);

    // 导出相关
    var exportDefineOpt = designCommons.collectConfigurerData($('#widget_bootstrap_table_tabs_export_info', $container), collectClass);
    $.extend(opt, exportDefineOpt);
    opt.hasExport = Boolean(opt.hasExport);
    if (opt.hasExport) {
      requeryFields.push('exportDataType', 'fileName', 'exportTypes');
      if (!checkRequire(requeryFields, opt, $container)) {
        return false;
      }
      opt.exportDataType = opt.exportDataType.join(';');
    }
    opt.exportTemplateFileId = $("input[name='exportTemplateFile']").val();
    if (opt.exportTypes === 'templateExcel') {
      if (!opt.exportTemplateFileId) {
        appModal.error('请选择导出模版！');
        return false;
      }
    }
    var $exportInfoTable = $('#table_export_info', $container);
    var exportColumns = $exportInfoTable.bootstrapTable('getData');

    //自定义js事件代码
    opt.defineEventJs = {};
    $('#widget_bootstrap_table_tabs_js_event_info pre', $container).each(function () {
      opt.defineEventJs[$(this).attr('id')] = $(this).data('codeEditor').getValue();
    });

    // 导入相关
    var importDefineOpt = designCommons.collectConfigurerData($('#widget_bootstrap_table_tabs_import_info', $container), collectClass);
    $.extend(opt, importDefineOpt);
    var $importInfoTable = $('#table_import_info', $container);
    var dataImportConfiguration = $importInfoTable.bootstrapTable('getData');
    opt.beforeExportCode = $('#before-export-code').data('codeEditor').getValue();

    opt.columns = columns;
    opt.query = query;
    opt.query.fields = fields;
    opt.query.multiKeywordData = multiKeywords;
    opt.query.propertyData = propertys;
    opt.buttons = buttons;
    opt.defaultSorts = defaultSorts;
    opt.treeGrid = treeGrid;
    opt.cardGrid = cardGrid;
    opt.exportColumns = exportColumns;
    opt.dataImport = dataImportConfiguration;

    this.component.options.configuration = $.extend({}, opt);

    this.closeCustomConfig(opt);

    // $(this.element).data('configuration',
    // this.component.options.configuration);
  };

  configurer.prototype.closeCustomConfig = function (options) {
    var config = {};
    var widgetId = this.component.options.id;
    if (!options.showColumns) {
      config.fixedNumber = undefined;
      config.fixedRightNumber = undefined;
      config.fixedColumns = undefined;
      $.ajax({
        url: ctx + '/api/user/preferences/save',
        type: 'POST',
        data: {
          dataKey: widgetId,
          dataValue: JSON.stringify(config),
          moduleId: 'BOOTSTRAPTABLE',
          remark: '表格自定义列'
        },
        dataType: 'json',
        success: function (result) {}
      });
    }
  };

  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    return configurer;
  };
  component.prototype.create = function () {
    $(this.element).find('.widget-body').html(this.options.content);
  };
  component.prototype.getDefinitionJson = function () {
    var options = this.options;
    var id = this.getId();
    options.id = id;
    return options;
  };

  component.prototype.defaultConfiguration = function () {
    return {
      pageStyle: 'default',
      pageList: '10,20,50,100,200',
      pageSize: 10
    };
  };

  return component;
});
