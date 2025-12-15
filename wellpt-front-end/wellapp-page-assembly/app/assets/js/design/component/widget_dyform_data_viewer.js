define([ "ui_component", "constant", "commons", "server", "formBuilder", "appContext", "design_commons", "wSelect2" ],
		function(ui_component, constant, commons, server, formBuilder, appContext, designCommons) {
			var collectClass = "w-configurer-option";
			var StringUtils = commons.StringUtils;
			var component = $.ui.component.BaseComponent();
			var isJSON = function(str) {
				try {
					JSON.parse(str);
					return true;
				} catch (e) {
					return false;
				}
			}
			var checkedFormat = function(value, row, index) {
				if (value) {
					return true;
				}
				return false;
			};
			
			var checkRequire = function(propertyNames, options, $container) {
				for (var i = 0; i < propertyNames.length; i++) {
					var propertyName = propertyNames[i];
					if (StringUtils.isBlank(options[propertyName])) {
						var title = $("label[for='" + propertyName + "']", $container).text();
						appModal.error(title.replace("*", "") + "不允许为空！");
						return false;
					}
				}
				return true;
			};
			var clearInputValue = function($container) {
				$container.find(".w-configurer-option").each(function() {
					var $element = $(this);
					var type = $element.attr("type");
					if (type == "text" || type == "hidden") {
						$element.val('');
					} else if (type == "checkbox" || type == "radio") {
						$element.prop("checked", false);
					}
					$element.trigger('change');
				});

			};
			var onEditHidden = function(field, row, $el, reason) {
				$el.closest("table").bootstrapTable("resetView")
			};
			
			var configurer = $.ui.component.BaseComponentConfigurer();
			component.prototype.create = function() {
				$(this.element).find(".widget-body").html(this.options.content);
			}
			component.prototype.usePropertyConfigurer = function() {
				return true;
			}
			configurer.prototype.initBaseInfo = function(configuration, $container) {
				$("#formName", $container).wSelect2({
					serviceName : "dataManagementViewerComponentService",
					queryMethod : "getDataTypeOfDyFormSelectData",
					selectionMethod : "getDataTypeOfDyFormSelectDataByIds",
					labelField : "formName",
					valueField : "formUuid",
					defaultBlank : true,
					width : "100%",
					height : 250
				});
				$("#formName", $container).on("change", function() {
					// 重新绑定表单选择的列信息
					//bindDyformColumns();
				});
				// 二开JS模块
				$("#jsModule", $container).wSelect2({
					serviceName : "appJavaScriptModuleMgr",
					params : {
						dependencyFilter : "DyformDataViewerWidgetDevelopment"
					},
					labelField : "jsModuleName",
					valueField : "jsModule",
					remoteSearch : false,
					multiple : true
				});
			}
			
			configurer.prototype.initDyformFieldInfo = function(configuration, $container) {
				var $tabelInfo = $("#table_field_info");
				var fiedlBean={name:''}
				formBuilder.bootstrapTable.initTableTopButtonToolbar( "table_field_info", "column", $container, fiedlBean );
				
				$tabelInfo.bootstrapTable("destroy").bootstrapTable({
					data : configuration.fieldOptions,
					idField : "type",
					striped : true,
					width : 500,
					onEditableHidden : onEditHidden,
					columns : [ {
						field : "checked",
						formatter : checkedFormat,
						checkbox : true,
					}, {
						field : "name",
						title : "字段名",
						editable : {
							type : "select",
							mode : "inline",
							showbuttons : false,
							source : ["a", "b"]
						}
					}, {
						field : "isEdit",
						title : "是否可编辑",
						editable : {
							type : "select",
							mode : "inline",
							showbuttons : false,
							source : [ {
								value : '1',
								text : "是"
							}, {
								value : '0',
								text : "否"
							} ]
						}
					}, {
						field : "isShow",
						title : "是否展示",
						editable : {
							type : "select",
							mode : "inline",
							showbuttons : false,
							source : [ {
								value : '1',
								text : "是"
							}, {
								value : '0',
								text : "否"
							} ]
						}
					} ]
				});				
				
			}			
			
			configurer.prototype.onLoad = function($container, options) {
				var configuration = $.extend(true, {}, options.configuration);
				$("#widget_tree_tabs ul a", $container).on("click", function(e) {
					e.preventDefault();
					$(this).tab("show");
				});
				designCommons.setElementValue(configuration, $container, 'fieldOptions');				
				//初始化基础信息tab
				this.initBaseInfo(configuration, $container);
				//初始化表单字段tab
				this.initDyformFieldInfo(configuration, $container);
			};			
			
			configurer.prototype.onOk = function($container) {
				if (this.component.isReferenceWidget()) {
					return;
				}
				var opt = designCommons.collectConfigurerData($("#widget_tree_tabs_base_info", $container),
						collectClass);
				opt.fieldOptions = $("#table_field_info").bootstrapTable("getData");
				var requeryFields = [ 'name', 'formName' ];
				if (!checkRequire(requeryFields, opt, $container)) {
					return false;
				}
				this.component.options.configuration = $.extend({}, opt);
			}
			
			component.prototype.getPropertyConfigurer = function() {
				return configurer;
			}
			component.prototype.toHtml = function() {
				var options = this.options;
				var id = this.getId();
				var html = '<div id="' + id + '" class="ui-wDyformDataViewer"></div>';
				return html;
			}
			component.prototype.getDefinitionJson = function() {
				var options = this.options;
				var id = this.getId();
				options.id = id;
				return options;
			}
			return component;
		});