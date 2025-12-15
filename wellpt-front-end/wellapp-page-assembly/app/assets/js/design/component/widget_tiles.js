define([ "design_commons", "constant", "commons", "formBuilder", "ui_component", "jquery-ui" ], function(designCommons,
		Constant, commons, formBuilder) {
	var component = $.ui.component.BaseComponent();
	var StringUtils = commons.StringUtils;
	var collectClass = "w-configurer-option";
	var checkedFormat = function(value, row, index) {
		if (value) {
			return true;
		}
		return false;
	};
	var clearChecked = function(row) {
		row.checked = false;
		return row;
	};
	var setElementValue = function(key, value) {
		if (typeof (key) == 'string') {
			$("input[name='" + key + "']").each(function() {
				$element = $(this);
				var type = $element.attr("type");
				if (type == "text" || type == "hidden") {
					$element.val(value);
				} else if (type == "checkbox") {
					$element.prop("checked", Boolean(value));
				} else if (type == "radio") {
					var checked = $element.val() == value;
					$element.prop("checked", checked);
				}
			});
			$("select[name='" + key + "']").val(value);
		}
	};
	component.prototype.create = function() {
		$(this.element).find(".widget-body").html(this.options.content);
	}
	// 使用属性配置器
	component.prototype.usePropertyConfigurer = function() {
		return true;
	}
	// 返回属性配置器
	component.prototype.getPropertyConfigurer = function() {
		var configurer = $.ui.component.BaseComponentConfigurer();
		configurer.prototype.onLoad = function($container, options) {
			// 初始化页签项
			$("#widget_tiles_tabs ul a", $container).on("click", function(e) {
				e.preventDefault();
				$(this).tab("show");
			})
			console.log("初始化数据");

			var configuration = $.extend(true, {}, options.configuration);
			console.log(configuration);
			$.each(configuration, function(key, value) {
				if (key != 'query') {
					setElementValue(key, value);
				}
			});
			// 分类
			// $("#categoryCode", $container).wSelect2({
			// 	serviceName : "dataDictionaryService",
			// 	params : {
			// 		type : "MODULE_CATEGORY"
			// 	},
			// 	labelField : "categoryName",
			// 	valueField : "categoryCode",
			// 	remoteSearch : false
			// });
			// 加载的JS模块
			$("#jsModule", $container).wSelect2({
				serviceName : "appJavaScriptModuleMgr",
				params : {
					dependencyFilter : "TileWidgetDevelopment"
				},
				labelField : "jsModuleName",
				valueField : "jsModule",
				remoteSearch : false,
				multiple : true
			});

			// 启用分页
			var $titleToolbarDefinition = $("#div_titleToolbar_definition", $container);
			$("#showTitleToolbar", $container).on('change', function() {
				if ($(this).is(':checked')) {
					$titleToolbarDefinition.show();
				} else {
					$titleToolbarDefinition.hide();
					$("#title", $container).val("");
				}
			}).trigger("change");
			$("#backgroundColor", $container).minicolors('create', options.minicolors || {});
			this.initTileInfo($container, configuration);
		};
		configurer.prototype.initTileInfo = function($container, configuration) {
			var piUuid = this.component.pageDesigner.getPiUuid();
			var system = appContext.getCurrentUserAppData().getSystem();
			var productUuid = system.productUuid;
			var $tileInfoTable = $("#table_tile_info", $container);
			// 列定义上移事件
			formBuilder.bootstrapTable.addRowUpButtonClickEvent({
				tableElement : $tileInfoTable,
				button : $("#btn_row_up_tile", $container)
			});
			// 列定义下移事件
			formBuilder.bootstrapTable.addRowDownButtonClickEvent({
				tableElement : $tileInfoTable,
				button : $("#btn_row_down_tile", $container)
			});
			// 列定义添加一行事件
			formBuilder.bootstrapTable.addAddRowButtonClickEvent({
				tableElement : $tileInfoTable,
				button : $("#btn_add_tile", $container),
				bean : {
					"uuid" : "",
					"checked" : false,
					"backgroundColor" : "#1ba1e2",
					"foregroundColor" : "#ffffff",
					"badgeBackgroundColor" : "#9a1616",
					"contentOptions" : {
						imgPaths : []
					},
					"code" : "",
					"size" : "square",
					"subhead" : "",
					"badge" : ""
				}
			});
			// 列定义删除一行事件
			formBuilder.bootstrapTable.addDeleteRowButtonClickEvent({
				tableElement : $tileInfoTable,
				button : $("#btn_delete_tile", $container)
			});
			var buildTextContent = function($container, options) {
				if (options.type == 'text') {
					formBuilder.buildInput({
						container : $container,
						label : "文本",
						name : "text",
						value : options.text,
						inputClass : 'w-custom-collect',
						labelColSpan : "3",
						controlColSpan : "9",
					});
					$("#text", $container).attr("maxlength", "1");
				}
			};
			var buildIconContent = function($container, options) {
				if (options.type == 'icon') {
					formBuilder.buildInput({
						container : $container,
						label : "图标",
						name : "icon",
						value : options.icon,
						inputClass : 'w-custom-collect',
						labelColSpan : "3",
						controlColSpan : "9",
						events : {
							change : function() {
								var newClass = $(this).val() + " form-control-feedback";
								$(this).next("span").removeClass().addClass(newClass);
							}

						}
					});
					var span = "<span class= '" + options.icon + " form-control-feedback' aria-hidden='true' ";
					span += "style='right: 0;margin-right:20px;font-size:20px;'></span>";
					$container.find("#icon").css("padding-right", "15px").after(span);
				}
			};
			var buildImageContent4Image = function($container, options) {
				$("body").on('click', ".wCommonPictureLib", function(event) {
					event.stopPropagation();
				});
				var img = "<img src='{0}' style='width: 100%;margin-left: auto;margin-bottom:3px;' />";
				var fieldHtml = new commons.StringBuilder();
				fieldHtml.appendFormat("<div class='form-group formbuilder clear'>");
				fieldHtml.appendFormat("	<label class='col-xs-3 control-label label-formbuilder' >图片</label>");
				fieldHtml.appendFormat("	<div class='col-xs-9 controls'>");
				fieldHtml.appendFormat("		<div id='image_container_div'>");
				if (options.imgPaths) {
					$.each(options.imgPaths, function(index, imagePath) {
						fieldHtml.appendFormat(img, imagePath);
					});
				}
				fieldHtml.appendFormat("		</div>");
				fieldHtml.appendFormat("		<div style='float:right;margin-right: 3%;'>");
				fieldHtml.appendFormat("		<button type='button' class='btn btn-primary' ");
				fieldHtml.appendFormat("		id='logoFileImageSelectBtn'>选择图片</button>");
				fieldHtml.appendFormat("		<button type='button' class='btn btn-danger' ");
				fieldHtml.appendFormat("		id='logoFileImageRemoveBtn'>删除</button>");
				fieldHtml.appendFormat("		</div>");
				fieldHtml.appendFormat("	</div>");
				fieldHtml.appendFormat("</div>");
				$container.append(fieldHtml.toString());
				$("#image_container_div", $container).on('click', "img", function() {
					var cls = $(this).attr('class') || '';
					if (cls.indexOf('element-selected') != -1) {
						cls = cls.replace('element-selected', '');
					} else {
						cls = cls + " element-selected ";
					}
					$(this).attr('class', cls);
				});

				$("#logoFileImageSelectBtn", $container).on("click", function() {
					$.WCommonPictureLib.show({
						confirm : function(data) {
							var pictureFilePath = data.filePaths;
							if (StringUtils.isBlank(pictureFilePath)) {
								return;
							}
							pictureFilePath=ctx+pictureFilePath;
							var imgHtml = new commons.StringBuilder();
							imgHtml.appendFormat(img, pictureFilePath);
							$("#image_container_div", $container).empty().append(imgHtml.toString());
						}
					});
				});
				$("#logoFileImageRemoveBtn", $container).on("click", function() {
					$("#image_container_div", $container).find("img").each(function() {
						var cls = $(this).attr('class') || '';
						if (cls.indexOf('element-selected') != -1) {
							$(this).remove();
						}
					});
				});
			};
			var buildImageContent4MultipleImage = function($container, options) {
				$("body").on('click', ".wCommonPictureLib", function(event) {
					event.stopPropagation();
				});
				var img = "<img src='{0}' style='width: 48%;margin-left: auto;margin-bottom:3px;' />";
				var fieldHtml = new commons.StringBuilder();
				fieldHtml.appendFormat("<div class='form-group formbuilder clear'>");
				fieldHtml.appendFormat("	<label class='col-xs-3 control-label label-formbuilder' >图片</label>");
				fieldHtml.appendFormat("	<div class='col-xs-9 controls'>");
				fieldHtml.appendFormat("		<div id='image_container_div'>");
				if (options.imgPaths) {
					$.each(options.imgPaths, function(index, imagePath) {
						fieldHtml.appendFormat(img, imagePath);
					});
				}
				fieldHtml.append("</div>");
				fieldHtml.append("<div style='float:right;margin-right: 3%;'>");
				fieldHtml.append("<button type='button' class='btn btn-primary' ");
				fieldHtml.append(" id='logoFileImageSelectBtn'>选择图片</button>");
				fieldHtml.append("<button type='button' class='btn btn-danger' ");
				fieldHtml.append(" id='logoFileImageRemoveBtn'>删除</button>");
				fieldHtml.append("</div>");
				fieldHtml.append("</div>");
				fieldHtml.append("</div>");
				$container.append(fieldHtml.toString());
				$("#image_container_div", $container).on('click', "img", function() {
					var cls = $(this).attr('class') || '';
					if (cls.indexOf('element-selected') != -1) {
						cls = cls.replace('element-selected', '');
					} else {
						cls = cls + " element-selected ";
					}
					$(this).attr('class', cls);
				});
				$("#logoFileImageSelectBtn", $container).on("click", function() {
					$.WCommonPictureLib.show({
						confirm : function(data) {
							var pictureFilePath = data.filePaths;
							if (StringUtils.isBlank(pictureFilePath)) {
								return;
							}
							var imgHtml = new commons.StringBuilder();
							imgHtml.appendFormat(img, pictureFilePath);
							$("#image_container_div", $container).append(imgHtml.toString());
						}
					});
				});
				$("#logoFileImageRemoveBtn", $container).on("click", function() {
					$("#image_container_div", $container).find("img").each(function() {
						var cls = $(this).attr('class') || '';
						if (cls.indexOf('element-selected') != -1) {
							$(this).remove();
						}
					});
				});
			};
			var buildImageContent = function($container, options) {
				if (options.type == 'image') {
					formBuilder.buildSelect2({
						container : $container,
						label : "效果",
						name : "effect",
						value : options.effect,
						inputClass : 'w-custom-collect',
						labelColSpan : "3",
						controlColSpan : "9",
						select2 : {
							data : [ {
								id : 'fit',
								text : '固定'
							}, {
								id : 'imageSet',
								text : '图片集'
							}, {
								id : 'carousel',
								text : '轮播'
							}, {
								id : 'container',
								text : '图片容器'
							}, {
								id : 'slide',
								text : '滑动(一)'
							}, {
								id : 'slide2',
								text : '滑动(二)'
							}, {
								id : 'zooming',
								text : '放大'
							}, {
								id : 'zooming-out',
								text : '缩小'
							} ]
						},
						events : {
							change : function() {
								var effect = $(this).val();
								buildImageContentByEffect($container, {
									effect : effect
								});
							}
						}
					});
					var span = "<span class= '" + options.icon;
					span += " form-control-feedback' aria-hidden='true' ";
					span += "style='right: 0;margin-right:20px;font-size:20px;'></span>";
					$container.find("#icon").css("padding-right", "15px").after(span);
					buildImageContentByEffect($container, options);
				}
			};
			buildImageContentByColors = function($container, options) {
				formBuilder.buildColorsInput({
					container : $container,
					label : "背景色",
					name : "slideBg",
					value : options.slideBg,
					inputClass : 'w-custom-collect',
					labelColSpan : "3",
					controlColSpan : "9",
				});
			};
			buildImageContentImageText = function($container, options) {
				formBuilder.buildInput({
					container : $container,
					label : "标题",
					name : "contentTitle",
					value : options.contentTitle,
					inputClass : 'w-custom-collect',
					labelColSpan : "3",
					controlColSpan : "9",
				});
				formBuilder.buildTextarea({
					container : $container,
					label : "内容",
					name : "contentText",
					value : options.contentText,
					inputClass : 'w-custom-collect',
					labelColSpan : "3",
					controlColSpan : "9",
				});
			}
			buildImageContentRadio = function($container, options, label, name, items) {
				formBuilder.buildRadio({
					container : $container,
					label : label,
					name : name,
					value : options[name],
					inputClass : 'w-custom-collect',
					labelColSpan : "3",
					controlColSpan : "9",
					items : items
				});
			};
			buildImageContentByEffect = function($container, options) {
				var $containerDiv = $container.find(".div_image_content_option");
				if ($containerDiv[0]) {
					$containerDiv.empty();
				} else {
					$containerDiv = $("<div class='div_image_content_option'></div>");
					$container.append($containerDiv);
				}
				if ($.inArray(options.effect, [ 'fit', 'zooming', 'zooming-out' ]) != -1) {
					buildImageContent4Image($containerDiv, options);
				}
				if ($.inArray(options.effect, [ 'carousel', 'imageSet' ]) != -1) {
					buildImageContent4MultipleImage($containerDiv, options);
				}
				var slideDirections = [ {
					id : "up",
					text : "上"
				}, {
					id : "down",
					text : "下"
				}, {
					id : "left",
					text : "左"
				}, {
					id : "right",
					text : "右"
				} ];
				if (options.effect == 'slide') {
					buildImageContentRadio($containerDiv, options, "滑动方向", "slideDirection", slideDirections);
					buildImageContentImageText($containerDiv, options);
					buildImageContent4Image($containerDiv, options);
				}
				if (options.effect == 'slide2') {
					buildImageContentRadio($containerDiv, options, "滑动方向", "slideDirection", slideDirections);
					buildImageContentImageText($containerDiv, options);
					buildImageContentByColors($containerDiv, options);
					buildImageContent4Image($containerDiv, options);
				}
				if (options.effect == 'container') {
					buildImageContentImageText($containerDiv, options);
					buildImageContent4Image($containerDiv, options);
				}
			};
			var renderContentViewByType = function($container, value) {
				var $containerDiv = $container.find(".div_content_option");
				if ($containerDiv[0]) {
					$containerDiv.empty();
				} else {
					$containerDiv = $("<div class='div_content_option'></div>");
					$container.append($containerDiv);
				}
				buildTextContent($containerDiv, value);
				buildIconContent($containerDiv, value);
				buildImageContent($containerDiv, value);
			};
			var typeItems = [ {
				id : "text",
				text : "文本"
			}, {
				id : "icon",
				text : "图标"
			}, {
				id : "image",
				text : "图片"
			} ];
			var value2input = function(value) {
				var $input = this.$input;
				$input.closest("form").removeClass("form-inline");
				$input.css("width", "400");
				$input.empty();
				formBuilder.buildRadio({
					container : $input,
					label : "内容类型",
					name : "type",
					value : value.type,
					inputClass : 'w-custom-collect',
					labelColSpan : "3",
					controlColSpan : "9",
					items : typeItems,
					events : {
						change : function() {
							var type = $(this).val();
							renderContentViewByType($input, {
								type : type
							});
						}
					}
				});
				renderContentViewByType($input, value);
			};
			var value2display = function(value) {
				var display = "";
				$.each(typeItems, function(index, item) {
					if (item.id == value.type) {
						display = item.text;
					}
				});
				return display;
			};
			var input2value = function(value) {
				var $input = this.$input;
				value.imgPaths = [];
				$("#image_container_div", $input).find("img").each(function() {
					value.imgPaths.push($(this).attr('src'));
				});
				return value;
			};
			var colorDefinitionField = [ 'backgroundColor', 'foregroundColor', 'badgeBackgroundColor' ];
			var value2input2 = designCommons.bootstrapTable.badge.value2input(null,piUuid);
			var value2display2 = designCommons.bootstrapTable.badge.value2display;
			$tileInfoTable.bootstrapTable("destroy").bootstrapTable({
				data : configuration.tiles,
				idField : "uuid",
				showColumns : true,
				striped : true,
				width : 500,
				onEditableShown : function(field, row, $el, editable) {
					if ($.inArray(field, colorDefinitionField) != -1) {
						var value = editable.input.$input.val();
						editable.input.$input.minicolors('create', {});
					}
					return false;
				},
				onEditableHidden : function(field, row, $el, reason) {
					if ($.inArray(field, colorDefinitionField) != -1) {
						if (StringUtils.isNotBlank(row[field])) {
							$el.css("color", row[field]);
						} else {
							$el.css("color", '');
						}
					}
					$el.closest("table").bootstrapTable("resetView");
					return false;
				},
				onEditableInit : function() {
					$tileInfoTable.find("a").each(function() {
						var $el = $(this);
						if ($.inArray($el.attr("data-name"), colorDefinitionField) != -1) {
							var val = $el.text();
							if (StringUtils.isNotBlank(val) && val != 'Empty' && val != 'undefined') {
								$el.css("color", val);
							} else {
								$el.css("color", '');
							}
						}

					});
					return false;
				},
				toolbar : $("#div_tile_toolbar", $container),
				columns : [ {
					field : "checked",
					checkbox : true
				}, {
					field : "uuid",
					title : "UUID",
					visible : false
				}, {
					field : "code",
					title : "编码",
					editable : {
						type : "text",
						showbuttons : false,
						onblur : "submit",
						mode : "inline"
					}
				}, {
					field : "size",
					title : "规格",
					editable : {
						type : "select",
						mode : "inline",
						showbuttons : false,
						source : [ {
							value : 'small',
							text : "小磁贴"
						}, {
							value : 'square',
							text : "标准磁贴"
						}, {
							value : 'wide',
							text : "宽磁贴"
						}, {
							value : 'large',
							text : "大磁贴"
						} ]
					}
				}, {
					field : "backgroundColor",
					title : "背景色",
					editable : {
						type : "text",
						showbuttons : false,
						onblur : "submit",
						mode : "inline"
					}
				}, {
					field : "foregroundColor",
					title : "前景色",
					editable : {
						type : "text",
						showbuttons : false,
						onblur : "submit",
						mode : "inline"
					}
				}, {
					field : "contentOptions",
					title : "内容",
					editable : {
						onblur : "cancel",
						type : "wCustomForm",
						placement : 'bottom',
						savenochange : true,
						value2input : value2input,
						input2value : input2value,
						value2display : value2display
					}
				}, {
					field : "subhead",
					title : "小标题",
					editable : {
						type : "text",
						showbuttons : false,
						onblur : "submit",
						mode : "inline"
					}
				}, {
					field : "badgeBackgroundColor",
					title : "徽章背景",
					editable : {
						type : "text",
						showbuttons : false,
						onblur : "submit",
						mode : "inline"
					}
				}, {
					field : "badge",
					title : "徽章",
					editable : {
						onblur : "cancel",
						type : "wCustomForm",
						placement : 'bottom',
						savenochange : true,
						value2input : value2input2,
						value2display : value2display2
					}
				}, {
					field : "target",
					title : "目标位置",
					width : 100,
					editable : {
						onblur : "cancel",
						type : "wCustomForm",
						placement : "bottom",
						savenochange : true,
						value2input : designCommons.bootstrapTable.targePosition.value2input,
						value2display : designCommons.bootstrapTable.targePosition.value2display,
						inputCompleted : designCommons.bootstrapTable.targePosition.inputCompleted
					}
				}, {
					field: "eventHandler",
                    title: "事件处理",
                    width: 150,
                    editable: {
                        onblur: "ignore",
                        type: "wCustomForm",
                        placement: "left",
                        savenochange: true,
                        value2input: designCommons.bootstrapTable.eventHandler.value2input,
                        input2value: designCommons.bootstrapTable.eventHandler.input2value,
                        validate: designCommons.bootstrapTable.eventHandler.validate,
                        value2display: designCommons.bootstrapTable.eventHandler.value2display
                    }
				}, {
					field : "eventParams",
					title : "事件参数",
					editable : {
						mode: "modal",
						onblur : 'ignore',
						type : "wCustomForm",
						placement : "left",
						savenochange : true,
						value2input : designCommons.bootstrapTable.eventParams.value2input,
						input2value : designCommons.bootstrapTable.eventParams.input2value,
						value2display : designCommons.bootstrapTable.eventParams.value2display
					}
				} ]
			});
		}
		configurer.prototype.onOk = function($container) {
			var opt = designCommons.collectConfigurerData($("#widget_tiles_tabs_base_info", $container), collectClass);
			opt.showTitleToolbar = Boolean(opt.showTitleToolbar);
			var $tileInfoTable = $("#table_tile_info", $container);
			var tiles = $tileInfoTable.bootstrapTable("getData");
			tiles = $.map(tiles, clearChecked);
			if (tiles.length == 0) {
				appModal.error("请配置磁贴列！");
				return false;
			}
			opt.tiles = tiles;
			this.component.options.configuration = $.extend(true, {}, opt);
		}
		return configurer;
	}

	// 返回组件定义
	component.prototype.getDefinitionJson = function() {
		var options = this.options;
		var id = this.getId();
		options.id = id;
		return options;
	}
	return component;
});