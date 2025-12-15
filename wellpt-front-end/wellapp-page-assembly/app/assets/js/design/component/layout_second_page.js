define([ "ui_component", "commons", "formBuilder", "appContext" ], function(ui_component, commons, formBuilder,
		appContext) {
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
		return callback();
	}
	// 创建列信息
	component.prototype.createColumns = function() {
		var $bootgrid = $(this.element).find(".bootgrid");	
		var columns = this.options.configuration.columns;
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
		html.appendFormat('<section id="{0}" class="second-page clearfix">', id);
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
			if(i === 0){
				html.appendFormat('<aside class="menu-sidebar col-idx-{1} column"><div class="scroll-inner">', colspan, i);
				html.appendFormat(childHtml);
				html.appendFormat('</div></aside>');
			}else{
				html.appendFormat('<article class="content col-idx-{1} column">', colspan, i);
				html.appendFormat(childHtml);
				html.appendFormat('</article>');
			}
			
		});
		html.appendFormat('</section>');
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
			this.initGridCellInfo(configuration, $container);
		};
		configurerPrototype.onOk = function($container) {
			var _self = this.component;
			var options = _self.options;
			// 按钮定义
			var $tableButtonInfo = $("#table_grid_cell_info", $container);
			var gridCells = $tableButtonInfo.bootstrapTable("getData");
			gridCells = $.map(gridCells, clearChecked);
			var opt = {};
			opt.columns = gridCells;
			this.component.options.configuration = $.extend({}, opt);
		};
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
		var configurer = $.ui.component.BaseComponentConfigurer(configurerPrototype);
		return configurer;
	}

	// 返回组件定义
	component.prototype.getDefinitionJson = function() {
		var $element = $(this.element);
		var options = this.options;
		var columnMap = converColumnMap(options);
		var id = this.getId();
		options.id = id;
		options.items = [];
		options.configuration = {};
		options.configuration.columns = [];
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
				options.items.push(childJson);
			});
			// 标记所在列
			var column = {};
			column.index = i;
			column.colspan = colspan;
			if (columnMap[i]) {
				column.width = columnMap[i].width;
			}
			options.configuration.columns.push(column);
		});
		return options;
	}
	function converColumnMap(options) {
		if (options == null || options.configuration == null || options.configuration.columns == null) {
			return {};
		}
		var map = {};
		var columns = options.configuration.columns;
		$.each(columns, function(i) {
			map[this.index] = this;
		})
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
				// "width: calc(100% - 270px)";
				this.renderWidth = "calc(100% - " + widthInfo.totalWidth / len + "px)";
			});
			$.each(widthInfo.notEmptyWidths, function() {
				this.renderWidth = this.width + "px";
			});
		}
		return widthInfo;
	}

	return component;
});