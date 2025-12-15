define(['ui_component', 'constant', 'formBuilder', 'appContext', 'design_commons'], function (
  ui,
  constant,
  formBuilder,
  appContext,
  designCommons
) {
  var component = $.ui.component.BaseComponent();
  component.prototype.create = function () {
    var $element = $(this.element);
    var $panelContainer = $element.find('.ui-sortable');
    var options = this.options;
    var _self = this;
    var defaultSortableOptions = _self.pageDesigner.getDefaultSortableOptions();
    this.$panelContainer = $panelContainer;
    $panelContainer.sortable(
      $.extend(defaultSortableOptions, {
        receive: function (event, ui) {
          if (ui.helper != null) {
            _self.pageDesigner.drop(_self, $panelContainer, $(ui.helper));
          } else {
            _self.pageDesigner.drop(_self, $panelContainer, $(ui.item));
          }
          _self.updateChildren($panelContainer.children('.widget'));
        },
        remove: function (event, ui) {
          _self.updateChildren($panelContainer.children('.widget'));
        },
        update: function (event, ui) {
          _self.updateChildren($panelContainer.children('.widget'));
        }
      })
    );
    // 初始化容器结点
    if (options.items != null) {
      $.each(options.items, function (j, item) {
        var $draggable = _self.pageDesigner.createDraggableByDefinitionJson(item);
        _self.pageDesigner.drop(_self, $panelContainer, $draggable, item);
      });
    }
  };

  // 使用属性配置器
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  var clearChecked = function (row) {
    row.checked = false;
    return row;
  };
  var checkedFormat = function (value, row, index) {
    if (value) {
      return true;
    }
    return false;
  };
  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    var configurer = $.ui.component.BaseComponentConfigurer();
    configurer.prototype.onLoad = function ($container, options) {
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;
      // 初始化页签项
      $('#widget_mobile_panel_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });
      var _self = this.component;
      var configuration = $.extend(true, {}, options.configuration);
      // 设置值
      designCommons.setElementValue(configuration, $container);
      $('#hideBack', $container)
        .on('change', function (event) {
          $('.left-event-handler')[this.checked ? 'removeClass' : 'addClass']('hidden');
        })
        .prop('checked', configuration.hideBack)
        .trigger('change');
      $('#showNavMenu', $container)
        .on('change', function (event) {
          var fn = $(this).prop('checked') ? 'show' : 'hide';
          $('.navMenuLength-from', $container)[fn]();
          $('#grid_cell_info_tab', $container)[fn]();
        })
        .prop('checked', configuration.showNavMenu)
        .trigger('change');
      // 加载的JS模块
      $('#jsModule', $container).val(configuration.jsModule);
      $('#jsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        labelField: 'jsModuleName',
        valueField: 'jsModule',
        params: {
          dependencyFilter: 'mui-MobilePanelDevelopmentBase'
        },
        remoteSearch: false,
        multiple: false
      });

      $('#eventHanlderName', $container).AppEvent({
        ztree: {
          params: [productUuid]
        },
        okCallback: function ($el, data) {
          if (data) {
            $('#eventHanlderId', $container).val(data.id);
            $('#eventHanlderType', $container).val(data.data.type);
            $('#eventHanlderPath', $container).val(data.data.path);
          }
        }
      });

      // 选择图标
      $('#eventHanlderIconSelectBtn', $container).on('click', function () {
        var _thiz = this;
        $.WCommonPictureLib.show({
          selectTypes: [3],
          confirm: function (data) {
            var fileIDs = data.fileIDs;
            $('#eventHanlderIcon').val(fileIDs);
            $('#eventHanlderIconSnap', $container).attr('class', fileIDs);
            $('#eventHanlderIconSnap', $container).attr('iconClass', fileIDs);
          }
        });
      });
      $('#eventHanlderName', $container).AppEvent('setValue', configuration.eventHanlderId);
      $('#eventHanlderIconSnap', $container).attr('class', configuration.eventHanlderIcon);
      $('#jsModuleName', $container).val();
      this.initGridCellInfo(configuration, $container);
    };
    configurer.prototype.onOk = function ($container) {
      var _self = this.component;
      var options = _self.options;
      // 按钮定义
      var $tableButtonInfo = $('#table_grid_cell_info', $container);
      var gridCells = $tableButtonInfo.bootstrapTable('getData');
      gridCells = $.map(gridCells, clearChecked);
      var $dropDownNavtable = $('#table_grid', $container);
      var navGridcells = $dropDownNavtable.bootstrapTable('getData');
      navGridcells = $.map(navGridcells, clearChecked);
      var opt = designCommons.collectConfigurerData($container, 'w-configurer-option');
      opt.hideBack = $('#hideBack', $container)[0].checked;
      opt.showNavMenu = $('#showNavMenu', $container)[0].checked;
      opt.navMenuLength = $('input[name=navMenuLength]', $container).filter(':checked').val();
      // opt.showDropDownNav = $('#showdropdowmMenu', $container)[0].checked;
      opt.jsModule = $('#jsModule', $container).val();
      opt.jsModuleName = $('#jsModuleName', $container).val();
      opt.menuItems = gridCells;
      opt.navItems = navGridcells;
      this.component.options.configuration = $.extend({}, opt);
    };
    configurer.prototype.initGridCellInfo = function (configuration, $container) {
      var menuItems = configuration.menuItems ? configuration.menuItems : [];
      var navItems = configuration.navItems ? configuration.navItems : [];
      var piUuid = this.component.pageDesigner.getPiUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      if (system != null && system.piUuid != null) {
        piUuid = system.piUuid;
      }

      // 按钮定义
      var $buttonInfoTable = $('#table_grid_cell_info', $container);

      // 按钮定义上移事件
      formBuilder.bootstrapTable.addRowUpButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_row_up_button', $container)
      });
      // 按钮定义下移事件
      formBuilder.bootstrapTable.addRowDownButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_row_down_button', $container)
      });
      // 按钮定义添加一行事件
      formBuilder.bootstrapTable.addAddRowButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_add_button', $container),
        bean: {
          checked: false,
          uuid: '',
          text: '',
          hidden: '0',
          iconClass: 'mui-icon'
        }
      });
      // 按钮定义删除一行事件
      formBuilder.bootstrapTable.addDeleteRowButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_delete_button', $container)
      });

      $buttonInfoTable.bootstrapTable('destroy').bootstrapTable({
        data: menuItems,
        idField: 'uuid',
        showColumns: true,
        striped: true,
        width: 500,
        onEditableHidden: function (field, row, $el, reason) {
          $buttonInfoTable.bootstrapTable('resetView');
        },
        toolbar: $('#div_button_info_toolbar', $container),
        columns: [{
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
              onblur: 'submit'
            }
          },
          {
            field: 'iconClass',
            title: '图标',
            width: 80,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          },
          {
            field: 'hidden',
            title: '是否隐藏',
            width: 80,
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: [{
                  value: '0',
                  text: '显示'
                },
                {
                  value: '1',
                  text: '隐藏'
                }
              ]
            }
          },
          {
            field: 'target',
            title: '目标位置',
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              value2input: designCommons.bootstrapTable.targePosition.value2input,
              value2display: designCommons.bootstrapTable.targePosition.value2display,
              inputCompleted: designCommons.bootstrapTable.targePosition.inputCompleted
            }
          },
          {
            field: 'eventHandler',
            title: '事件逻辑',
            width: 150,
            editable: {
              type: 'wCommonComboTree',
              mode: 'modal',
              showbuttons: false,
              onblur: 'submit',
              placement: 'bottom',
              otherProperties: {
                type: 'data.type',
                path: 'data.path'
              },
              otherPropertyPath: 'data',
              wCommonComboTree: {
                inlineView: true,
                service: 'appProductIntegrationMgr.getTreeNodeByUuid',
                serviceParams: [piUuid, [],
                  []
                ],
                multiSelect: false, // 是否多选
                parentSelect: true
                // 父节点选择有效，默认无效
              }
            }
          },
          {
            field: 'eventParams',
            title: '事件参数',
            editable: {
              mode: 'modal',
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              value2input: designCommons.bootstrapTable.eventParams.value2input,
              input2value: designCommons.bootstrapTable.eventParams.input2value,
              value2display: designCommons.bootstrapTable.eventParams.value2display
            }
          }
        ]
      });

      /*// 按钮定义
			var $dropDowmNavTable = $("#table_grid", $container);

			// 按钮定义上移事件
			formBuilder.bootstrapTable.addRowUpButtonClickEvent({
				tableElement : $dropDowmNavTable,
				button : $("#btn_row_up", $container)
			});
			// 按钮定义下移事件
			formBuilder.bootstrapTable.addRowDownButtonClickEvent({
				tableElement : $dropDowmNavTable,
				button : $("#btn_row_down", $container)
			});
			// 按钮定义添加一行事件
			formBuilder.bootstrapTable.addAddRowButtonClickEvent({
				tableElement : $dropDowmNavTable,
				button : $("#btn_add", $container),
				bean : {
					checked : false,
					uuid : '',
					text : '',
					hidden : '0',
					iconClass : 'mui-icon'
				}
			});
			// 按钮定义删除一行事件
			formBuilder.bootstrapTable.addDeleteRowButtonClickEvent({
				tableElement : $dropDowmNavTable,
				button : $("#btn_delete", $container)
			});

			$dropDowmNavTable.bootstrapTable("destroy").bootstrapTable({
				data : navItems,
				idField : "uuid",
				showColumns : true,
				striped : true,
				width : 500,
				onEditableHidden : function(field, row, $el, reason) {
					$dropDowmNavTable.bootstrapTable("resetView")
				},
				toolbar : $("#div_table_toolbar", $container),
				columns : [ {
					field : "checked",
					checkbox : true,
					formatter : checkedFormat
				}, {
					field : "uuid",
					title : "UUID",
					visible : false
				}, {
					field : "text",
					title : "名称",
					width : 80,
					editable : {
						type : "text",
						mode : "inline",
						showbuttons : false,
						onblur : "submit"
					}
				},  {
					field : "hidden",
					title : "是否隐藏",
					width : 80,
					editable : {
						type : "select",
						mode : "inline",
						onblur : "submit",
						showbuttons : false,
						source : [ {
							value : "0",
							text : "显示"
						}, {
							value : "1",
							text : "隐藏"
						} ]
					}
				}, {
					field : "eventHandler",
					title : "事件逻辑",
					width : 150,
					editable : {
						type : "wCommonComboTree",
						mode : "inline",
						showbuttons : false,
						onblur : "submit",
						otherProperties : {
							'type' : 'data.type',
							'path' : 'data.path'
						},
						otherPropertyPath : "data",
						wCommonComboTree : {
							service : "appProductIntegrationMgr.getTreeNodeByUuid",
							serviceParams : [ piUuid, [], [] ],
							multiSelect : false, // 是否多选
							parentSelect : true
						// 父节点选择有效，默认无效
						}
					}
				} ]
			});*/
    };
    return configurer;
  };

  component.prototype.toHtml = function () {
    var options = this.options;
    var children = this.getChildren();
    var id = this.getId();
    var html = "<div id='" + id + "' class='panel panel-default'>";
    html += '<header class="mui-bar mui-bar-nav">';
    html += '</header>';
    html += "<div class='mui-content'>";
    if (children != null) {
      $.each(children, function (i) {
        var child = this;
        var childHtml = child.toHtml.call(child);
        html += childHtml;
      });
    }
    html += '</div>';
    html += '</div>';
    return html;
  };
  component.prototype.getDefinitionJson = function ($element) {
    var definitionJson = this.options;
    definitionJson.id = this.getId();
    // definitionJson.wtype = "wMobilePanel";
    definitionJson.items = [];
    var children = this.getChildren();
    $.each(children, function (i) {
      var child = this;
      definitionJson.items.push(child.getDefinitionJson());
    });
    return definitionJson;
  };
  return component;
});
