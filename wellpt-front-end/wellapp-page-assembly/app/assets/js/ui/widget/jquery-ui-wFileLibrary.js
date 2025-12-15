(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define([
      'jquery',
      'commons',
      'server',
      'constant',
      'dataStoreBase',
      'formBuilder',
      'moment',
      'appModal',
      'api-wBootstrapTable',
      'layDate',
      'select2',
      'wSelect2',
      'bootstrap-table-label-mark'
    ], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function (jquery, commons, server, constant, DataStore, formBuilder, moment, appModal, Api, layDate) {
  'use strict';
  // moment.
  var $ = jquery;
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var rowCheckItem = 'rowCheckItem';
  var lineEnderToolbar = 'lineEnderToolbar';
  $.widget('ui.wFileLibrary', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {},
      jsModules: {}
    },

    /**
     * 获取数据源对象
     */
    getDataProvider: function () {
      return this._dataProvider;
    },
    /**
     * 获取配置对象
     */
    getConfiguration: function () {
      return this.options.widgetDefinition.configuration;
    },
    /**
     * 获取数据源ID
     */
    getDataStoreId: function () {
      return this.getConfiguration().dataStoreId;
    },
    /**
     * 通知数据源回调根据条件加载数据
     */
    load: function (request) {
      var _self = this;
      _self.invokeDevelopmentMethod('beforeLoadData', [_self.options, _self.getConfiguration(), request]);
      _self.loadSuccess = request.success;
      var notifyChange = request.data.notifyChange;
      delete request.data.notifyChange;
      _self.getDataProvider().load(request.data, {
        loadSuccess: request.success,
        notifyChange: notifyChange
      });
    },

    loadFieldRenderData: function (renderer, data) {
      var _self = this;
      var datas = _self.getDataProvider().getData();

      var renderers = [];
      var index = 1;
      if (_self.getConfiguration().hideChecked) {
        //列数据在隐藏checkboc情况下，索引从0开始
        index = 0;
      }
      $.each(_self.getConfiguration().columns, function (i, column) {
        if (column.renderer && StringUtils.isNotBlank(column.renderer.rendererType) && column.renderer.loadType == '1') {
          renderers.push({
            index: index,
            columnIndex: column.name,
            param: column.renderer
          });
        }
        if (column['hidden'] == '0') {
          index = index + 1;
        }
      });
      var $table = $('#' + _self.getId() + '_table');
      for (var i = 0; i < renderers.length; i++) {
        for (var j = 0; j < datas.length; j++) {
          var data = datas[j];
          var renderer = renderers[i];
          server.JDS.call({
            async: true,
            service: 'cdDataStoreService.loadFieldRenderData',
            data: [renderer, data, { trIndex: j, tdIndex: renderer['index'] }],
            version: '',
            success: function (result) {
              if (result.msg == 'success') {
                var trIndex = null;
                var tdIndex = null;
                var field = null;
                var value = null;
                for (var k in result.data) {
                  if (k == 'indexMap') {
                    trIndex = result.data[k]['trIndex'];
                    tdIndex = result.data[k]['tdIndex'];
                  } else {
                    field = k;
                    value = result.data[k];
                  }
                }
                if (field && value) {
                  datas[trIndex][field] = value;
                }
                var $td = $table.find('tbody tr:eq(' + trIndex + ') td:eq(' + tdIndex + ')');
                $td.html(value);
                $td.attr('title', $td.text());
              }
            }
          });
        }
      }
    },

    /**
     * 通知数据变更
     */
    onDataChange: function (params) {
      var _self = this;
      var data = _self.getDataProvider().getData();
      var total = _self.getDataProvider().getCount();
      params.loadSuccess({
        rows: data,
        total: total
      });
      _self.trigger(constant.WIDGET_EVENT.Refresh, {
        viewData: {
          rows: data,
          total: total
        },
        rows: data,
        total: total
      });
      if (params.notifyChange) {
        _self.trigger(constant.WIDGET_EVENT.Change, {
          viewData: {
            rows: data,
            total: total
          },
          rows: data,
          total: total
        });
      }

      if (total > 0 && data.length == 0) {
        _self.selectPage(1);
      }
      _self.loadFieldRenderData();
    },
    /**
     * 创建组件
     */
    _createView: function () {
      // 创建js数据源对象
      var _self = this;
      var configuration = _self.getConfiguration();
      _self._dataProvider = new DataStore({
        dataStoreId: _self.getDataStoreId(),
        onDataChange: function (data, count, params) {
          _self.onDataChange(params);
        },
        receiver: _self,
        renderers: _self.getRenderers(),
        pageSize: configuration.pageSize
      });

      _self._beforeRenderView();
      _self._renderView();
      _self._afterRenderView();
      _self._setEvent();
      _self.Api = new Api(this); //绑定Api，提供给其他组件代码获取到表格Api

      //监听刷新数据的请求
      _self.pageContainer.on(constant.WIDGET_EVENT.BootstrapTableRefresh, function () {
        _self.refresh();
      });
    },

    /**
     * 渲染视图前
     */
    _beforeRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
      var configuration = this.getConfiguration();
      if (configuration.defineEventJs && configuration.defineEventJs['beforeRenderCode']) {
        var defineFunc = appContext.eval(configuration.defineEventJs['beforeRenderCode'], $(this), {
          $this: _self,
          configuration: configuration,
          commons: commons,
          server: server,
          Api: new Api(_self)
        });
        console.log('在表格组件渲染之前触发自定义代码块', defineFunc);
      }
    },

    /**
     * 拼接按钮工具栏
     */
    _renderButton: function () {
      var _self = this;
      var configuration = this.getConfiguration();
      var $headerToolbar = $("<div class='div_header_toolbar btn-group'></div>");
      var $footerToolbar = $("<div class='div_footer_toolbar btn-group'></div>");
      var buttonGroup = {};
      _self.invokeDevelopmentMethod('beforeRenderButtons', [configuration.buttons || []]);
      if (configuration.defineEventJs && configuration.defineEventJs['onPreButtonCode']) {
        var defineFunc = appContext.eval(configuration.defineEventJs['onPreButtonCode'], $(this), {
          $this: _self,
          buttons: configuration.buttons || [],
          commons: commons,
          server: server,
          Api: new Api(_self)
        });
        console.log('在按钮渲染之前触发自定义代码块', defineFunc);
      }

      if (configuration.buttons) {
        var $headerToolbarHtmls = [];
        var $footerToolbarHtmls = [];
        var $lineEnderButtonHtmls = [];
        var $floatButtonHtmls = [];
        $.each(configuration.buttons, function (index, button) {
          var positions = button.position.btnPositionArr || button.position;
          if (StringUtils.isBlank(button.group)) {
            var buttonHtml = new commons.StringBuilder();
            var btnClass = StringUtils.isBlank(button.cssClass) ? 'btn-default' : button.cssClass + ' ' + button.cssStr;
            if (button.icon && button.icon.className) {
              btnClass += ' ' + button.icon.className;
            }
            if (button.btnLib) {
              if ($.inArray(button.btnLib.btnInfo.type, ['primary', 'minor', 'line', 'noLine']) > -1) {
                if (button.btnLib.iconInfo) {
                  buttonHtml.appendFormat(
                    '<button type="button" class="well-btn {0} {1} {2} btn_class_{3}"><i class="{4}"></i>{5}</button>',
                    button.btnLib.btnColor,
                    button.btnLib.btnInfo['class'],
                    button.btnLib.btnSize,
                    button.code,
                    button.btnLib.iconInfo.fileIDs,
                    button.text
                  );
                } else {
                  buttonHtml.appendFormat(
                    '<button type="button" class="well-btn {0} {1} {2} btn_class_{3}">{4}</button>',
                    button.btnLib.btnColor,
                    button.btnLib.btnInfo['class'],
                    button.btnLib.btnSize,
                    button.code,
                    button.text
                  );
                }
              } else {
                if (button.btnLib.btnInfo.icon) {
                  buttonHtml.appendFormat(
                    '<button type="button" class="well-btn {0} {1} btn_class_{2}"><i class="{3}"></i>{4}</button>',
                    button.btnLib.btnInfo['class'],
                    button.btnLib.btnSize,
                    button.code,
                    button.btnLib.btnInfo.icon,
                    button.btnLib.btnInfo.text
                  );
                } else {
                  buttonHtml.appendFormat(
                    '<button type="button" class="well-btn {0} {1} btn_class_{2}">{3}</button>',
                    button.btnLib.btnInfo['class'],
                    button.btnLib.btnSize,
                    button.code,
                    button.btnLib.btnInfo.text
                  );
                }
              }
            } else {
              buttonHtml.appendFormat(
                "<button type='button' class='btn {0} btn_class_{1}' title='{2}'>{3}</button>",
                btnClass,
                button.code,
                button.text,
                button.text
              );
            }

            $.each(positions, function (i, item) {
              var _position = item.split('-')[0];
              var float_btn_column_value = item.split('-')[1];
              switch (_position) {
                case '1':
                  $headerToolbarHtmls.push(buttonHtml.toString());
                  break;
                case '2':
                  $footerToolbarHtmls.push(buttonHtml.toString());
                  break;
                case '3':
                  $lineEnderButtonHtmls.push(buttonHtml.toString());
                  break;
                case '4':
                  $floatButtonHtmls.push({
                    buttonHtml: buttonHtml.toString(),
                    column: float_btn_column_value
                  });
                  break;
              }
            });
          } else {
            if (!buttonGroup[button.group]) {
              buttonGroup[button.group] = {};
              if ($.inArray('1', positions) > -1) {
                $headerToolbarHtmls.push({
                  group: button.group
                }); //放一个占位
              } else if ($.inArray('3', positions) > -1) {
                $lineEnderButtonHtmls.push({
                  group: button.group
                }); //放一个占位
              }
            }
            $.each(positions, function (index, position) {
              if (!buttonGroup[button.group][position]) {
                buttonGroup[button.group][position] = [];
              }
              buttonGroup[button.group][position].push(button);
            });
          }
        });
      }
      var replaceToolbarBtnGroupHtml = function (group, htmlArray, buttonHtml) {
        var i = _.findIndex(htmlArray, { group: group });
        if (i !== -1) {
          //替换当前的按钮组对象为HTML
          htmlArray[i] = buttonHtml.toString();
        }
      };
      $.each(buttonGroup, function (group, positions) {
        $.each(positions, function (position, buttons) {
          var buttonHtml = new commons.StringBuilder();
          buttonHtml.append("<div class='btn-group' style='overflow: visible'>");
          var btnClass = StringUtils.isBlank(buttons[0].cssClass) ? 'btn-default' : buttons[0].cssClass;
          if (buttons[0].isHide) {
            btnClass += ' hide';
          }
          if (buttons[0].icon && buttons[0].icon.className) {
            btnClass += ' ' + buttons[0].icon.className;
          }
          buttonHtml.appendFormat("<button type='button' class='btn {0} dropdown-toggle'", btnClass);
          buttonHtml.append("	data-toggle='dropdown'>");
          buttonHtml.append(group);
          buttonHtml.append("		<i class='fa fa-angle-down'></i>");
          buttonHtml.append('	</button>');
          buttonHtml.append("	<ul class='dropdown-menu' role='menu'>");
          $.each(buttons, function (index, button) {
            var liClass = 'li_class_' + button.code;
            buttonHtml.append('	<li class=' + liClass + '><a>' + button.text + '</a></li>');
          });
          buttonHtml.append('	</ul>');
          buttonHtml.append('</div>');
          if (position == '1') {
            replaceToolbarBtnGroupHtml(group, $headerToolbarHtmls, buttonHtml);
          } else if (position == '2') {
            replaceToolbarBtnGroupHtml(group, $footerToolbarHtmls, buttonHtml);
          } else if (position == '3') {
            replaceToolbarBtnGroupHtml(group, $lineEnderButtonHtmls, buttonHtml);
          } else if (position == '4') {
            replaceToolbarBtnGroupHtml(group, $floatButtonHtmls, buttonHtml);
          }
        });
      });
      var buttonElement = {};
      if ($headerToolbarHtmls.length > 0) {
        $headerToolbar.append($headerToolbarHtmls);
        buttonElement.headerToolbar = $headerToolbar;
      } else {
        // setTimeout(function () {
        //     _self.element.find('.fixed-table-toolbar .btn-group').hide();
        // },0)
      }
      if ($footerToolbarHtmls.length > 0) {
        $footerToolbar.append($footerToolbarHtmls);
        buttonElement.footerToolbar = $footerToolbar;
      }
      if ($lineEnderButtonHtmls.length > 0) {
        buttonElement.lineEnderButtonHtml = $lineEnderButtonHtmls.join('');
      }
      if ($floatButtonHtmls.length > 0) {
        buttonElement.floatButtonHtml = $floatButtonHtmls;
      }
      return buttonElement;
    },

    /**
     * 渲染BootstrapTable控件和相关查询条件，按钮等
     */
    _renderTable: function ($element, buttonElement) {
      var _self = this;
      var options = this.options;
      var configuration = this.getConfiguration();
      var tableId = options.widgetDefinition.id + '_table';
      var $tableElement = $("<table id='" + tableId + "' ></table>");
      if (configuration.showTitle) {
        $element.prepend("<div class='ui-wFileLibrary-title'>" + configuration.name + '</div>');
      }
      $element.append($tableElement);
	  // bug#59120
	  $element.addClass("ui-wBootstrapTable");
      var bootstrapTableOptions = {};
      if (buttonElement.headerToolbar) {
        bootstrapTableOptions.toolbar = buttonElement.headerToolbar;
      }
      bootstrapTableOptions.showHeader = !configuration.hideColumnHeader;

      bootstrapTableOptions.columns = [];
      if (!configuration.hideChecked) {
        bootstrapTableOptions.columns.push({
          field: rowCheckItem,
          checkbox: true
        });
      }
      if (configuration.hideTdBorder) {
        $element.addClass('hideTdBorder');
      }

      var rendererFormat = function (value, row, index) {
        return row[this.field + 'RenderValue'];
      };
      var idField = '';
      $.each(configuration.columns, function (index, column) {
        var visible = column.hidden != '1';
        var sortable = column.sortable == '1';
        if (column.idField == '1') {
          idField = column.name;
        }
        var columnOptions = {
          field: column.name,
          title: column.header,
          width: column.width,
          sortable: sortable,
          visible: visible
        };
        if (column.renderer && StringUtils.isNotBlank(column.renderer.rendererType)) {
          columnOptions.formatter = rendererFormat;
        }
        bootstrapTableOptions.columns.push(columnOptions);
      });
      if (buttonElement.lineEnderButtonHtml) {
        //获取行按钮的整体宽度
        var $container = _self.pageContainer.element ? _self.pageContainer.element : _self.pageContainer;
        $container.append(
          $('<div>', {
            id: 'testButtonWidth',
            class: 'div_lineEnd_toolbar',
            style: 'display:inline-block;margin-left:10px;margin-right:10px;padding-bottom:1px;opacity:0;'
          }).append($(buttonElement.lineEnderButtonHtml.toString()))
        );
        var toolbarWidth = $('#testButtonWidth', $container).width() == 0 ? 150 : $('#testButtonWidth', $container).width() + 50;
        $('#testButtonWidth').remove();
        bootstrapTableOptions.columns.push({
          field: lineEnderToolbar,
          width: toolbarWidth,
          title: '操作',
          sortable: false,
          formatter: function (value, row, index) {
            var buttonToolbar = new commons.StringBuilder();
            buttonToolbar.appendFormat(
              "<div class='div_lineEnd_toolbar' style='display:inline-block;margin-left:10px;margin-right:10px;padding-bottom:1px;overflow: visible' index='{0}'>{1}</div>",
              index,
              buttonElement.lineEnderButtonHtml
            );
            //行按钮格式化
            var format = {
              before: buttonToolbar.toString(),
              after: null
            };
            _self.invokeDevelopmentMethod('lineEnderButtonHtmlFormat', [format, row, index]);
            if (format.after) {
              return format.after;
            }
            return buttonToolbar.toString();
          }
        });
      }

      var treeGrid = configuration.treeGrid || {};
      _self.invokeDevelopmentMethod('getTableOptions', [bootstrapTableOptions]);
      $tableElement.bootstrapTable('destroy').bootstrapTable(
        $.extend(
          {
            sidePagination: 'server',
            pagination: true,
            parentIdField: treeGrid.parentIdField,
            treeView: treeGrid.treeView,
            treeNodeFields: treeGrid.treeNodes,
            allPageDataTreeFormate: treeGrid.allPageDataTreeFormate,
            treeNodeDisplayField: treeGrid.treeNodeDisplayField,
            treeBuildType: treeGrid.treeBuildType,
            collapseIcon: treeGrid.collapseIcon,
            expandIcon: treeGrid.expandIcon,
            initTreeShowStyle: treeGrid.initTreeShowStyle,
            treeCascadeCheck: treeGrid.treeCascadeCheck,
            height: configuration.height,
            striped: false,
            escape: true,
            idField: idField,
            resizable: configuration.columnDnd,
            fixedColumns: configuration.fixedColumns,
            fixedNumber: 3,
            showColumns: configuration.showColumns,
            formatNoMatches: function () {
              return configuration.formatNoMatchText ? configuration.formatNoMatchText : '没有找到匹配的记录';
            },
            ajax: function (request) {
              _self.load(request);
            },
            onClickRow: function (row, $element, field) {
              var rowNum = $element.attr('data-index');
              console.log(rowNum, row);
              if (field !== lineEnderToolbar && field !== rowCheckItem) {
                $.map(_self.getData(), function (row, index) {
                  if (rowNum == index) {
                    row.rowCheckItem = true;
                  } else {
                    row.rowCheckItem = false;
                  }
                });
                $element.addClass('selected').siblings().removeClass('selected');
                _self.invokeDevelopmentMethod('onClickRow', [rowNum, row, $element, field]);
                _self.trigger('ClickRow', {
                  rowNum: rowNum,
                  row: row,
                  element: $element,
                  field: field
                });
              }
            },
            onDblClickRow: function (row, $element, field) {
              var rowNum = $element.attr('data-index');
              if (field !== lineEnderToolbar && field !== rowCheckItem) {
                _self.invokeDevelopmentMethod('onDblClickRow', [rowNum, row, $element, field]);
              }
            },
            onClickCell: function () {
              _self.invokeDevelopmentMethod('onClickCell', arguments);
            },

            onPostBody: function (data) {
               // 重新设置行内按钮列宽
              var $inlineButton = $tableElement.find('tbody tr td.table-handle');
              if ($inlineButton.length) {
                var maxButtonWidth = 0;
                $.each($inlineButton, function (idx, el) {
                  var $button = $(el).find('.div_lineEnd_toolbar:first');
                  var width = _self._getElementWidth($button.clone());
                  if (width > maxButtonWidth) {
                    maxButtonWidth = width;
                  }
                });

                $tableElement.find('tr > .table-handle').width(maxButtonWidth + 50);
                $tableElement.find('tr > .table-handle .th-inner').width('');
              } else {
                $tableElement.find('tr > th.table-handle .th-inner').width('');
              }

              // 设置提示
              $tableElement.find('tr>td').each(function () {
                var $cell = $(this);
                if ($cell.find('.div_lineEnd_toolbar').length > 0) {
                  //如果是操作按钮组，不需要设置title
                  return true;
                }
                var options = $tableElement.bootstrapTable('getOptions');
                if (options.cardView !== true) {
                  $cell.attr('title', $cell.text());
                }
              });

              // _self._debounce(function () {
              var $container = $tableElement.closest('.ui-wBootstrapTable');
              var options = $tableElement.bootstrapTable('getOptions');
              if (!options.columns) {
                return;
              }
              var widthSetColumns = [];
              $.each(options.columns[0], function (idx, col) {
                if (!options.fixedColumns || col.width) {
                  widthSetColumns.push(col.field);
                }
              });

              // 设置最多行数
              if (configuration.maxRows && configuration.maxRows > 1) {
                var isIEBrowser = !!(navigator.userAgent.indexOf('MSIE ') > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./));

                // 计算行高
                if (options.cardView) {
                  $container.find('.card-view > .title').css('width', '');

                  $container.find('.card-view > .title, .card-view > .value').each(function () {
                    var $cell = $(this);
                    $cell.wrapInner('<span class="multiple-rows-wrapper" style="-webkit-line-clamp: ' + configuration.maxRows + ';">');
                  });
                } else {
                  var height = configuration.maxRows * 24 + 8;

                  $container.find('tr').css('max-height', height);
                  $container.find('tr > td').each(function () {
                    var $cell = $(this);
                    var field = $cell.attr('data-field');
                    if ($.inArray(field, widthSetColumns) > -1 && $cell.children('.multiple-rows-wrapper').length === 0) {
                      $cell.wrapInner('<span class="multiple-rows-wrapper" style="-webkit-line-clamp: ' + configuration.maxRows + ';">');
                    }
                  });
                  $container.find('tr > th > .th-inner').each(function () {
                    var $cell = $(this);
                    var field = $($cell.closest('th')).attr('data-field');
                    if ($.inArray(field, widthSetColumns) > -1 && $cell.children('.multiple-rows-wrapper').length === 0) {
                      $cell.css({
                        '-webkit-line-clamp': configuration.maxRows,
                        'max-height': height
                      });
                      $cell.addClass('multiple-rows-wrapper');
                    }
                  });

                  if (isIEBrowser) {
                    $container.find('.multiple-rows-wrapper').css({
                      display: 'inline-block',
                      'max-height': height + 3
                    });
                  }
                }
              }
			  $tableElement.bootstrapTable('resetView');

              // if(_self.triggerOnDataChange){
              //   debugger;
              //   _self.triggerOnDataChange = false;
              //   _self.fixedColumns();
              // }
              // }, 50);

              // $('.fixed-table-body-columns').html('');

              _self.invokeDevelopmentMethod('onPostBody', arguments);

              //$tableElement.bootstrapTable('toggleTreeView');
            },
            rowStyle: function (row, index) {
              var style = {};
              style.classes = (configuration.rowClass || []).join(' ');
              return style;
            },
			queryParamsType: '',
            queryParams: function (params) {
              var newParams = {
                pagination: {
                  pageSize: params.pageSize,
                  currentPage: params.pageNumber
                }
              };

              if (StringUtils.isNotBlank(params.sortName)) {
                newParams.orders = [
                  {
                    sortName: params.sortName,
                    sortOrder: params.sortOrder
                  }
                ];
              }

              return newParams;
            },
            undefinedText: '',
            //数据加载完后触发
            onLoadSuccess: function (data) {
              console.log('表格数据加载完毕,触发onLoadSuccess方法');
              if (configuration.fixedColumns) {
                $tableElement.css('table-layout', 'unset'); //如果是固定列，重置表格table-layout样式
                $tableElement.bootstrapTable('fixedColumns');
              }
              _self.invokeDevelopmentMethod('onLoadSuccess', arguments);

              function showOrHidePageDetail() {
                var paginationWrapW = _self.element.find('.fixed-table-pagination').width();
                var paginationW = _self.element.find('.pagination').outerWidth();
                var paginationDetailW = _self.element.find('.pagination-detail').outerWidth();
                if (paginationW + paginationDetailW > paginationWrapW) {
                  _self.element.find('.pagination-detail').hide();
                } else {
                  _self.element.find('.pagination-detail').show();
                }
              }
              showOrHidePageDetail();
              $(window).resize(function () {
                showOrHidePageDetail();
              });
            }
          },
          bootstrapTableOptions
        )
      );

      if (buttonElement.floatButtonHtml) {
        //获取行按钮的整体宽度
        var $container = _self.pageContainer.element ? _self.pageContainer.element : _self.pageContainer;
        //todo
        $tableElement
          .on('mouseenter', 'tr:gt(0)', function (e) {
            var $this = $(this);
            var rowData = _self.getData()[parseInt($(this).attr('data-index'))];
            $.each(buttonElement.floatButtonHtml, function (i, item) {
              var renderTd = $this
                .find('td')
                .css('position', 'relative')
                .eq(configuration.hideChecked ? item.column - 1 : item.column);
              if (!renderTd.find('.table-float-btn').length) {
                renderTd.append('<div class="table-float-btn"></div>');
              }
              renderTd.find('.table-float-btn').append(item.buttonHtml);
            });
          })
          .on('mouseleave', 'tr:gt(0)', function (e) {
            var $this = $(this);
            $.each(buttonElement.floatButtonHtml, function (i, item) {
              $this
                .find('td')
                .eq(configuration.hideChecked ? item.column - 1 : item.column)
                .find('.table-float-btn')
                .remove();
            });
          });
      }

      if (treeGrid.enableTreegrid) {
        var toolbar = new commons.StringBuilder();
        toolbar.append("<div class='columns columns-right btn-group pull-right'>");

        // 树形/卡片列表切换
        if (treeGrid.enableTreegrid && treeGrid.showFolderBtn) {
          toolbar.append('<div class="btn-group split-btn-group">');
          toolbar.append(
            "<button type=\"button\" class=\"btn btn-success\" name='toggleTreeView' title='切换树形列表'><i class='iconfont icon-ptkj-liebiaoshitu' ></i></button>"
          );
          toolbar.append(
            '<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'
          );
          toolbar.append('<span class="fa fa-angle-down"></span>');
          toolbar.append('<span class="sr-only">Toggle Dropdown</span>');
          toolbar.append('</button>');
          toolbar.append('<ul class="dropdown-menu">');
          toolbar.append('<li class=\'allTreeNodeExpand\'><a href="#">全部展开</a></li>');
          toolbar.append('<li class=\'allTreeNodeCollapse\'><a href="#">全部折叠</a></li>');
          toolbar.append('</ul>');
          toolbar.append('</div>');
        }
        // else{
        //     $tableElement.on('post-body.bs.table', function () {
        //         var options = $tableElement.bootstrapTable('getOptions');
        //         if (options.treeView && !options.postBody2TreeView) {
        //             // $treeIcon.trigger('treeIconChange');
        //             options.treeView = false;
        //             options.postBody2TreeView = true;//表示是有表格渲染事件触发的树形结构转换
        //             $tableElement.bootstrapTable('toggleTreeView');
        //         }
        //     });
        // }

        toolbar.append('</div>');
        var $tableToolbar = $element.find('.fixed-table-toolbar');
        $tableToolbar.prepend(toolbar.toString());
        if (treeGrid.enableTreegrid) {
          var $treeIcon = $tableToolbar.find("button[name='toggleTreeView']");
          //监听表格渲染完成事件，如果树形列表启用状态，则需要进行树形展示
          $tableElement.on('post-body.bs.table', function () {
            var options = $tableElement.bootstrapTable('getOptions');
            if (options.treeView && !options.postBody2TreeView) {
              $treeIcon.trigger('treeIconChange');
              options.treeView = false;
              options.postBody2TreeView = true; //表示是有表格渲染事件触发的树形结构转换
              $tableElement.bootstrapTable('toggleTreeView');
            }
          });

          $treeIcon.on('click', function () {
            $tableElement.bootstrapTable('getOptions').postBody2TreeView = true; //在post-body.bs.table事件处理时候，避免重复加载树形
            $tableElement.bootstrapTable('toggleTreeView'); //切换树形列表方法，bootstrap-table-treegrid.js

            $(this).trigger('treeIconChange');
          });
          $tableElement.on('post-body.bs.table toggle.bs.table', function () {
            var options = $tableElement.bootstrapTable('getOptions');
            if (options.treeView) {
              $treeIcon.attr('title', '恢复初始列表');
              $treeIcon.find('i').removeClass('icon-ptkj-pingjishitu');
              $treeIcon.find('i').addClass('icon-ptkj-liebiaoshitu');
            } else {
              $treeIcon.attr('title', '切换树形列表');
              $treeIcon.find('i').removeClass('icon-ptkj-liebiaoshitu');
              $treeIcon.find('i').addClass('icon-ptkj-pingjishitu');
            }
          });
        }
      }
      if (buttonElement.footerToolbar) {
        $element.append(buttonElement.footerToolbar);
      }
      return $tableElement;
    },

    /**
     * 视图组件渲染主体方法
     */
    _renderView: function () {
      var _self = this;
      var $element = $(this.element);
      var configuration = this.getConfiguration();
      if (StringUtils.isNotBlank(configuration.width)) {
        $element.width(configuration.width);
      }
      if (StringUtils.isNotBlank(configuration.height)) {
        $element.height(configuration.height);
      }

      this.buttonElement = this._renderButton();
      this.$tableElement = this._renderTable($element, this.buttonElement);
      // bootstrap-table-resizable中initResizable延后100秒执行，需要跟着延后，确保是初始化完列拖动后才处理高度。
      setTimeout(function () {
        _self.resetHeight();
      }, 150);
    },

    /**
     * 视图组件渲染结束调用
     */
    _afterRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('afterRender', [this.options, this.getConfiguration()]);
      var configuration = this.getConfiguration();
      if (configuration.defineEventJs && configuration.defineEventJs['afterRenderCode']) {
        var defineFunc = appContext.eval(configuration.defineEventJs['afterRenderCode'], $(this), {
          $this: _self,
          configuration: configuration,
          commons: commons,
          server: server,
          Api: new Api(_self)
        });
        console.log('在表格组件渲染之后触发自定义代码块', defineFunc);
      }
    },

    /**
     * 获取列渲染器
     */
    getRenderers: function () {
      var renderers = [];
      var configuration = this.getConfiguration();
      $.each(configuration.columns, function (index, column) {
        if (column.renderer && StringUtils.isNotBlank(column.renderer.rendererType)) {
          renderers.push({
            columnIndex: column.name,
            param: column.renderer
          });
        }
      });
      return renderers;
    },
    /**
     * 清除视图参数
     */
    clearParams: function () {
      this.getDataProvider().clearParams();
    },
    /**
     * 添加参数
     */
    addParam: function (key, value) {
      this.getDataProvider().addParam(key, value);
    },
    /**
     * 移除参数
     */
    removeParam: function (key) {
      return this.getDataProvider().removeParam(key);
    },
    /**
     * 获取参数
     */
    getParam: function (key) {
      return this.getDataProvider().getParam(key);
    },
    /**
     * 获取选中列数据集合
     */
    getSelections: function () {
      return this.$tableElement.bootstrapTable('getSelections');
    },
    /**
     * 获取选中行索引集合
     */
    getSelectionIndexes: function () {
      return $.map(this.getData(), function (row, index) {
        if (row.rowCheckItem) {
          return index;
        }
      });
    },

    /**
     * 获取选中的主键值
     */
    getSelectionPrimaryKeys: function () {
      var primaryColumnName = this.getPrimaryColumnName();
      if (!primaryColumnName) {
        return [];
      }
      return $.map(this.getData(), function (row, index) {
        if (row.rowCheckItem) {
          return row[primaryColumnName];
        }
      });
    },

    /**
     * 获取数据集合的指定列值
     */
    getDataColumnValues: function (columnName) {
      if (columnName) {
        return $.map(this.getData(), function (row, index) {
          return row[columnName];
        });
      }
      return [];
    },

    /**
     * 获取列定义的主键列名称
     * @returns {*}
     */
    getPrimaryColumnName: function () {
      var columns = this.getConfiguration().columns;
      for (var i = 0; i < columns.length; i++) {
        if (columns[i]['idField'] == '1') {
          return columns[i]['name'];
        }
      }
      return null;
    },

    /**
     * 获取视图数据集合
     */
    getData: function () {
      return this.$tableElement.bootstrapTable('getData');
    },
    /**
     * 根据唯一键获取列数据
     */
    getRowByUniqueId: function (id) {
      return this.$tableElement.bootstrapTable('getRowByUniqueId', id);
    },
    /**
     * 刷新数据
     */
    refresh: function (notifyChange) {
      this.$tableElement.bootstrapTable('refresh', {
        query: {
          notifyChange: notifyChange
        }
      });
    },

    /**
     * 重新计算（设置）视图高度
     */
    resetHeight: function (height) {
      var _self = this;
      if (!_self.$tableElement) {
        return;
      }
      var configuration = _self.getConfiguration();
      if (StringUtils.isNotBlank(height)) {
        configuration.height = height;
        var $element = $(_self.element);
        $element.height(configuration.height);
      }
      if (StringUtils.isBlank(configuration.height)) {
        return;
      }
      var fieldSearchHeight = _self._getRealHeight(_self.$fieldSearchElement);
      var footerToolbarHeight = _self._getRealHeight(_self.buttonElement.footerToolbar);
      var newHeight = configuration.height - fieldSearchHeight - footerToolbarHeight;
      console.log('new height==' + newHeight);
      _self.$tableElement.bootstrapTable('resetView', {
        height: newHeight
      });
    },

    /**
     * 注册按钮事件，调用相关二开接口
     */
    _setEvent: function () {
      var _self = this;
      var options = _self.options;
      var $contextElement = $(_self.element);
      var $table = $('#' + options.widgetDefinition.id);
      var configuration = _self.getConfiguration();
//      $('.div_lineEnd_toolbar').parent().css('overflow', 'visible');
//      $('tr').hover(function () {
//        var $this = $(this);
//        var _index = $this.data('index');
//        $this
//          .parents('.bootstrap-table')
//          .find('tr')
//          .each(function () {
//            if ($(this).data('index') === _index) {
//              $(this).addClass('hover');
//            } else {
//              $(this).removeClass('hover');
//            }
//          });
//      });

      $.each(configuration.buttons, function (index, button) {
        var buttonSeletor = (StringUtils.isBlank(button.group) ? '.btn_class_' : '.li_class_') + button.code;
        //自定义脚本函数处理
        if (!$.isEmptyObject(button.defineEventJs)) {
          for (var k in button.defineEventJs) {
            var bindDefineEvent = function (eventName) {
              $table.on(eventName, buttonSeletor, function (event) {
                var $toolbarDiv = $(event.target).closest('div');
                var rowData = [];
                if ($toolbarDiv.is('.div_lineEnd_toolbar')) {
                  var index = $toolbarDiv.attr('index');
                  var allData = _self.$tableElement.bootstrapTable('getData');
                  rowData = [allData[index]];
                } else if ($toolbarDiv.is('.table-float-btn')) {
                  var index = $toolbarDiv.closest('tr').data('index');
                  var allData = _self.$tableElement.bootstrapTable('getData');
                  rowData = [allData[index]];
                } else {
                  rowData = _self.getSelections();
                }
                var defineFunc = appContext.eval(
                  button.defineEventJs[eventName],
                  $(this),
                  {
                    $this: _self,
                    event: event,
                    commons: commons,
                    server: server,
                    Api: new Api(_self),
                    rowData: rowData
                  },
                  function (v) {
                    if (v === false) {
                      event.preventDefault();
                      event.stopImmediatePropagation();
                    }
                  }
                );
                console.log('执行按钮脚本：', defineFunc);
              });
              if (eventName === 'init') {
                //立即触发初始化事件
                $(buttonSeletor, $contextElement).trigger(eventName);
              }
            };

            bindDefineEvent(k);
          }
        }

        if (!$.isEmptyObject(button.eventHandler)) {
          var eventHandler = button.eventHandler;
          var eventParams = button.eventParams || {};
          var target = button.target || {};
          $contextElement.on('click', buttonSeletor, function (event) {
            event.stopPropagation();

            //按钮事件显示位置为导航tab时获取导航tab的widgetId
            function getNavTabWidget(appContext, curWindow) {
              var widgetMap = appContext.widgetMap;
              var hasWidget = false;
              $.each(widgetMap, function (i) {
                if (i.indexOf('wNavTab_') > -1) {
                  opt.targetWidgetId = i;
                  opt.renderNavTab = true;
                  opt.appContext = appContext;
                  hasWidget = true;
                  return false;
                }
              });
              if (!hasWidget) {
                getNavTabWidget(curWindow.parent.appContext, curWindow.parent);
              }
            }

            var opt = {
              target: target.position,
              targetWidgetId: target.widgetId,
              refreshIfExists: target.refreshIfExists,
              eventTarget: target,
              event: event,
              appId: eventHandler.id,
              appType: eventHandler.type,
              appPath: eventHandler.path,
              params: $.extend({}, eventParams.params, appContext.parseEventHashParams(eventHandler, "menuid")),
              button: $.extend({}, button),
              view: _self,
              appData: appContext.getCurrentUserAppData().appData,
              viewOptions: options
            };
            var $toolbarDiv = $(event.target).closest('div');
            if ($toolbarDiv.is('.div_lineEnd_toolbar')) {
              var index = $toolbarDiv.attr('index');
              var allData = _self.$tableElement.bootstrapTable('getData');
              opt.rowData = [allData[index]];
            } else {
              opt.rowData = _self.getSelections();
            }
            if (opt.target === '_navTab') {
              getNavTabWidget(appContext, window);
              opt.buttonEvent = true;
              if (window !== top) {
                var allIframe = $(top.document).find('iframe');
                allIframe.each(function () {
                  var $this = $(this);
                  if ($this[0].src === location.href) {
                    opt.pNavTabId = $this.parent().attr('id');
                    return false;
                  }
                });
              } else {
                opt.pNavTabId = _self.element.closest('.well-nav-tab').attr('id');
              }

              opt.buttonEventId = 'buttonEventId_' + _self.options.widgetDefinition.id;
              opt.text = _self.getConfiguration().name + '-' + button.text;
            }
            _self.startApp(opt);
          });
        } else {
          $contextElement.on('click', buttonSeletor, function (event) {
            event.stopPropagation();
            var $toolbarDiv = $(event.target).closest('div');
            var eventParams = button.eventParams || {};
            var opt = {
              ui: _self,
              view: _self,
              viewOptions: options
            };
            opt.params = appContext.resolveParams(eventParams.params, opt);
            var args = [event, opt];
            if ($toolbarDiv.is('.div_lineEnd_toolbar')) {
              var index = $toolbarDiv.attr('index');
              var allData = _self.$tableElement.bootstrapTable('getData');
              args.push(allData[index]);
            }
            _self.invokeDevelopmentMethod(button.code, args);
          });
        }
      });

      _self.on('dmsEventHanlderActionDone', function (e) {
        var detail = e.detail;
        // 处理数据管理操作完成的代码块
        if (detail && detail.invokeResult && detail.invokeResult.success && detail.options.ui) {
          if (detail.options.data.action) {
            $.each(configuration.buttons, function (index, button) {
              if (button.eventHandler && button.eventHandler.id) {
                var paths = button.eventHandler.path.split('/');
                if (paths[paths.length - 1] == detail.options.data.action.id) {
                  appContext.eval(button.eventHandler['afterEventHandlerCodes'], $(_self), {
                    $this: _self,
                    commons: commons,
                    server: server,
                    Api: new Api($(_self))
                  });
                  return false;
                }
              }
            });
          }
        }
      });

      //监听表格刷新动作：触发执行二开的刷新事件
      _self.on(constant.WIDGET_EVENT.Refresh, function (e) {
        _self.invokeDevelopmentMethod('onRefresh', [e.details]);
      });
    },
    /**
     * 获取JS模块
     */
    getDevelopmentModules: function () {
      var _self = this;
      var jsModule = _self.getConfiguration().jsModule;
      if (StringUtils.isBlank(jsModule)) {
        _self.devJsModules = [];
      } else {
        _self.devJsModules = jsModule.split(constant.Separator.Semicolon);
      }
      return _self.devJsModules;
    },

    /**
     * 获取控件真实高度
     */
    _getRealHeight: function ($el) {
      var height = 0;
      if ($el && $el.is(':visible ')) {
        height = $el.outerHeight(true);
        $el.children().each(function () {
          if (height < $(this).outerHeight(true)) {
            height = $(this).outerHeight(true);
          }
        });
      }
      return height;
    },
    /**
     * 获取某个列的字段类型
     */
    _getFieldDataType: function (columnIndex) {
      var configuration = this.getConfiguration();
      var dataType = '';
      $.each(configuration.columns, function (index, column) {
        if (columnIndex == column.name) {
          dataType = column.dataType;
          return false;
        }
      });
      return dataType;
    },

    /**
     * 固定列方法
     */
    fixedColumns: function () {
      this.$tableElement.bootstrapTable('fixedColumns'); //固定列方法，bootstrap-table-fixed-columns.js
    },

    /**
     * 树形列表切换
     */
    toggleTreeView: function () {
      this.$tableElement.bootstrapTable('getOptions').postBody2TreeView = true; //在post-body.bs.table事件处理时候，避免重复加载树形
      this.$tableElement.bootstrapTable('toggleTreeView'); //切换树形列表方法，bootstrap-table-treegrid.js
      var $treeIcon = $(this.element).find('.fixed-table-toolbar').find("button[name='toggleTreeView']");
      if ($treeIcon.length > 0) {
        $treeIcon.trigger('treeIconChange');
      }
    },

    /**
     * 展开树节点
     * @param dataIndex null或者undefined 则展开所有树节点
     */
    expandTreeView: function (dataIndex) {
      this.$tableElement.bootstrapTable('expandTreeView', dataIndex);
    },
    /**
     * 折叠树节点
     * @param dataIndex null或者undefined 则折叠所有树节点
     */
    collapseTreeView: function (dataIndex) {
      this.$tableElement.bootstrapTable('collapseTreeView', dataIndex);
    }
  });
});
