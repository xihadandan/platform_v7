define([ "ui_component", "commons", "formBuilder", "appContext", "design_commons" ], function(ui_component, commons,
		formBuilder, appContext, designCommons) {
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	var component = $.ui.component.BaseComponent();
	component.prototype.create = function() {
		var $element = $(this.element);
    var options = this.options;
		var _self = this;
		function callback() {
			// 创建列数
			_self.createColumns();
			_self.$columns = $element.find("div.column");
			_self.$columns.addClass("ui-sortable");
			_self.pageDesigner.sortable(_self, _self.$columns, $element);
			// 初始化容器结点
			if (options.items != null && options.items.length > 0) {
				$element.find(".ui-sortable").each(function(i) {
					var $placeHolder = $(this);
					$.each(options.items, function(j) {
						var item = this;
						if (item.columnIndex == i) {
							var $draggable = _self.pageDesigner.createDraggableByDefinitionJson(item);
							_self.pageDesigner.drop(_self, $placeHolder, $draggable, item);
						}
					});
				});
			}
		}
		if (options["wtype"] === "wBootgrid_Custom" && (options.items == null || options.items.length === 0)) {
			var msg = "<input id='custom_layout' name='custom_layout' style='width:100%;'/>";
			msg += "输入每列布局所占的等分数，每列布局以空格隔开，例如 3 3或4 4 4等";
			msg += "<br/>";
			msg += "共12等分数，不足12自动补上，超过12以12布局";
			return $.WCommonDialog({
				title : '输入布局列定义',
				message : msg,
				buttons : {
					confirm : {
						label : "确定",
						className : "btn-primary",
						callback : $.proxy(function(e) {
							var totalCols = 0;
							var availableCols = 12;
							var html = "";
							var layoutString = $.trim($("#custom_layout").val())
							var colspanArray = layoutString.split(" ", 12);
							options.configuration = {};
							options.configuration.columns = [];
							$.each(colspanArray, function(index, colspan) {
								if (StringUtils.isBlank(colspan)) {
									return;
								}
								totalCols = totalCols + parseInt(colspan, 10);
								html += getColumnHtml(colspan);
								var column = {};
								column.colspan = colspan;
								options.configuration.columns.push(column);
							});
							if (totalCols > availableCols) {
								// 超过12按12布局
								html = getColumnHtml(availableCols);
								var column = {};
								column.colspan = availableCols;
								options.configuration.columns = [];
								options.configuration.columns.push(column);
							} else if (totalCols < availableCols) {
								// 不够12添加占位符：
								// 1、在添加布局时,可根据所占列是否满足12列自动合并布局
								html += getColumnHtml(availableCols - totalCols);
								var column = {};
								column.colspan = availableCols - totalCols;
								options.configuration.columns.push(column);
							}
              $element.find("div.column").after(html).remove();
              if (_self.callback) {
                _self.callback();
              } else {
                callback.call(this);
              }
						}, _self)
					},
					cancel : {
						label : "关闭",
						className : "btn-default"
					}
				},
				shown : function() {
				}
			});
		}
		return callback();
	}
	// 创建列信息
	component.prototype.createColumns = function() {
		var $bootgrid = $(this.element).find(".bootgrid");
		var columns = null;
		if (this.options.configuration == null) {
			this.options.configuration = {};
			columns = [];
			if (this.options.cssClass != null && this.options.cssClass.indexOf("col-sm-4") != -1) {
				columns.push({
					colspan : 4,
					index : 0
				});
				columns.push({
					colspan : 4,
					index : 1
				});
				columns.push({
					colspan : 4,
					index : 2
				});
			} else if (this.options.cssClass != null && this.options.cssClass.indexOf("col-sm-6") != -1) {
				columns.push({
					colspan : 6,
					index : 0
				});
				columns.push({
					colspan : 6,
					index : 1
				});
			} else if (this.options.cssClass != null && this.options.cssClass.indexOf("col-sm-12") != -1) {
				columns.push({
					colspan : 12,
					index : 0
				});
			}
			this.options.configuration.columns = columns;
		} else {
			columns = this.options.configuration.columns;
		}
		$.each(columns, function(index, column) {
			var colspan = column.colspan;
			$bootgrid.append(getColumnHtml(colspan));
		});
	}

	// 生成随机颜色
	function randomColor() {
		return "#" + ("00000" + ((Math.random() * 16777215 + 0.5) >> 0).toString(16)).slice(-6);
	}

	// 获取列HTML
	function getColumnHtml(colspan) {
		return '<div class="col-xs-' + colspan + '"><div class="column" colspan="' + colspan + '"></div></div>';
	}

	// 返回定义的HTML
	component.prototype.toHtml = function($element) {
		var $element = $(this.element);
		var options = this.options;
		// 计算生成渲染的列宽度
		calcColumnRenderWidth(options);
		var columnMap = converColumnMap(options);
		var id = this.getId();
		var html = new StringBuilder();
		html.appendFormat('<div id="{0}" class="row clearfix">', id);
		var children = this.getChildren();
		var placeHolders = this.$columns;
		$.each(placeHolders, function(i) {
			var $holder = $(this);
			var colspan = $holder.attr("colspan");
			var holderChildren = [];
			for (var index = 0; index < children.length; index++) {
				var child = children[index];
				if ($holder.has(child.element).length > 0) {
					holderChildren.push(child);
				}
			}
			// 渲染的列宽度
			var renderWidth = columnMap[i].renderWidth;
			var widthStyle = '';
			if (StringUtils.isNotBlank(renderWidth)) {
				widthStyle = ' style="width:' + renderWidth + '" ';
			}
			// 子结点HTML
			var childHtml = "";
			$.each(holderChildren, function(i, child) {
				childHtml += child.toHtml();
			});
			html.appendFormat('<div class="col-xs-{0} col-idx-{1} column" {2} >', colspan, i, widthStyle);
			html.appendFormat(childHtml);
			html.appendFormat('</div>');
		});
		html.appendFormat('</div>');
		return html.toString();
	}

	// 使用属性配置器
	component.prototype.usePropertyConfigurer = function() {
		return true;
	}
	var clearChecked = function(row) {
		row.checked = false;
		return row;
	};
	var checkedFormat = function(value, row, index) {
		if (value) {
			return true;
		}
		return false;
	};
	// 返回属性配置器
	component.prototype.getPropertyConfigurer = function() {
		var collectClass = "w-configurer-option";
		var configurerPrototype = {};
		configurerPrototype.getTemplateUrl = function() {
			// 调用父类提交方法
			var wtype = this._superApply(arguments);
			wtype = wtype.replace("widget", "layout");
			return wtype;
		};
		configurerPrototype.onLoad = function($container, options) {
			// 初始化页签项
			$("#layout_bootgrid_custom_tabs ul a", $container).on("click", function(e) {
				e.preventDefault();
				$(this).tab("show");
			})
			var _self = this.component;
			var configuration = $.extend(true, {}, options.configuration);
			// 基本信息
			this.initBaseInfo(configuration, $container);
			// 列信息
			this.initGridCellInfo(configuration, $container);
		};
		configurerPrototype.onLoadFromWidgetDefinition = function($container, options) {
			this.onLoad($container, options);
			// 刷新元素
			var placeHolders = this.component.$columns;
			var columns = options.configuration.columns;
			for(var i=0,len=columns.length;i<len;i++){
				$(placeHolders[i]).attr('colspan',columns[i].colspan);
				var $parent = $(placeHolders[i]).parent();
				$parent.attr('class','col-xs-'+columns[i].colspan);
			}
		}
		configurerPrototype.onOk = function($container) {
			var _self = this.component;
			var options = _self.options;

			var configuration = {};
			// 基本信息
			this.collectBaseInfo(configuration, $container);
			// 列信息
			this.collectGridCellInfo(configuration, $container);

			this.component.options.configuration = configuration;
		};
		configurerPrototype.initBaseInfo = function(configuration, $container) {
			// 设置值
			designCommons.setElementValue(configuration, $container);
		}
		configurerPrototype.initGridCellInfo = function(configuration, $container) {
			var columns = configuration.columns ? configuration.columns : [];
			$.each(columns, function(i) {
				if (StringUtils.isBlank(this.width)) {
					this.width = "";
				}
			});
			var piUuid = this.component.pageDesigner.getPiUuid();
			var system = appContext.getCurrentUserAppData().getSystem();
			if (system != null && system.piUuid != null) {
				piUuid = system.piUuid;
			}

			// 按钮定义
			var $buttonInfoTable = $("#table_grid_cell_info", $container);

			// 按钮定义上移事件
			formBuilder.bootstrapTable.addRowUpButtonClickEvent({
				tableElement : $buttonInfoTable,
				button : $("#btn_row_up_button", $container)
			});
			// 按钮定义下移事件
			formBuilder.bootstrapTable.addRowDownButtonClickEvent({
				tableElement : $buttonInfoTable,
				button : $("#btn_row_down_button", $container)
			});
			// 按钮定义添加一行事件
			formBuilder.bootstrapTable.addAddRowButtonClickEvent({
				tableElement : $buttonInfoTable,
				button : $("#btn_add_button", $container),
				bean : {
					checked : false,
					uuid : '',
					index : '',
					colspan : '',
					width : '200'
				}
			});
			// 按钮定义删除一行事件
			formBuilder.bootstrapTable.addDeleteRowButtonClickEvent({
				tableElement : $buttonInfoTable,
				button : $("#btn_delete_button", $container)
			});

			$buttonInfoTable.bootstrapTable("destroy").bootstrapTable({
				data : columns,
				idField : "uuid",
				showColumns : true,
				striped : true,
				width : 500,
				onEditableHidden : function(field, row, $el, reason) {
					$buttonInfoTable.bootstrapTable("resetView")
				},
				toolbar : $("#div_button_info_toolbar", $container),
				columns : [ {
					field : "checked",
					checkbox : true,
					formatter : checkedFormat
				}, {
					field : "uuid",
					title : "UUID",
					visible : false
				}, {
					field : "index",
					title : "索引",
					width : 80
				}, {
					field : "colspan",
					title : "跨列数(共12列)",
					width : 180
				}, {
					field : "width",
					title : "宽度",
					width : 280,
					editable : {
						type : "text",
						mode : "inline",
						showbuttons : false,
						onblur : "submit",
						validate : function(value) {
							if (StringUtils.isNotBlank(value)) {
								var regu = "^(([1-9][0-9]*)|([1-9][0-9]*\.[0-9]+)|([0]\.[0-9]+))$";
								var re = new RegExp(regu);
								if (!re.test(value)) {
									return '请输入正确的数字!';
								}
							}
						}
					}
				} ]
			});
		};
		configurerPrototype.collectBaseInfo = function(configuration, $container) {
			var $form = $("#layout_bootgrid_custom_tabs_base_info", $container);
			var opt = designCommons.collectConfigurerData($form, collectClass);
			$.extend(configuration, opt);
		};
		configurerPrototype.collectGridCellInfo = function(configuration, $container) {
			var $tableButtonInfo = $("#table_grid_cell_info", $container);
			var gridCells = $tableButtonInfo.bootstrapTable("getData");
			gridCells = $.map(gridCells, clearChecked);
			var opt = {};
			opt.columns = gridCells;
			$.extend(configuration, opt);
		};
		var configurer = $.ui.component.BaseComponentConfigurer(configurerPrototype);
		return configurer;
	}

	// 从组件定义加载确认前回调
	component.prototype.beforeFromOtherDefintionLoad = function(definitionJson){
		if(definitionJson.configuration.columns.length === this.options.configuration.columns.length){
			return true;
		}else{
			appModal.warning('布局组件仅支持相同列数的组件定义加载');
			return false;
		}
	}

	component.prototype.afterFromOtherDefintionLoad = function(definitionJson){

	}

	// 返回组件定义
	component.prototype.getDefinitionJson = function() {
		var _self = this;
		var $element = $(_self.element);
		var definitionJson = _self.options;
		var columnMap = converColumnMap(definitionJson);
		var id = _self.getId();
		definitionJson.id = id;
		definitionJson.items = [];
		definitionJson.configuration.columns = [];
		var children = this.getChildren();
		var placeHolders = this.$columns;
		$.each(placeHolders, function(i) {
			var $holder = $(this);
			var colspan = $holder.attr("colspan");
			var holderChildren = [];
			for (var index = 0; index < children.length; index++) {
				var child = children[index];
				if ($holder.has(child.element).length > 0) {
					holderChildren.push(child);
				}
			}
			// 收集子结点定义
			$.each(holderChildren, function(j, child) {
				var childJson = child.getDefinitionJson();
				childJson.columnIndex = i;
				definitionJson.items.push(childJson);
			});
			// 标记所在列
			var column = {};
			column.index = i;
			column.colspan = colspan;
			if (columnMap[i]) {
				column.width = columnMap[i].width;
			}
			definitionJson.configuration.columns.push(column);
		});
		return definitionJson;
	};
	function converColumnMap(definitionJson) {
		if (definitionJson == null || definitionJson.configuration == null
				|| definitionJson.configuration.columns == null) {
			return {};
		}
		var map = {};
		var columns = definitionJson.configuration.columns;
		$.each(columns, function(i) {
			map[this.index] = this;
		});
		return map;
	}
	function calcColumnRenderWidth(options) {
		var widthInfo = {
			totalWidth : 0,
			notEmptyWidths : [],
			emptyWidths : []
		};
		if (options == null || options.configuration == null || options.configuration.columns == null) {
			return widthInfo;
		}
		var columns = options.configuration.columns;
		$.each(columns, function(i) {
			delete this.renderWidth;
			if (StringUtils.isNotBlank(this.width)) {
				widthInfo.totalWidth += parseInt(this.width, 10);
				widthInfo.notEmptyWidths.push(this);
			} else {
				widthInfo.emptyWidths.push(this);
			}
		})
		var len = widthInfo.emptyWidths.length;
		if (widthInfo.totalWidth > 0) {
			$.each(widthInfo.emptyWidths, function() {
				this.renderWidth = "calc((100% - " + widthInfo.totalWidth + "px)/"+len+")";
				this.renderWidthArg = widthInfo.totalWidth + '|' + len;
			});
			$.each(widthInfo.notEmptyWidths, function() {
				this.renderWidth = this.width + "px";
			});
		}
		return widthInfo;
	}

	return component;
});
