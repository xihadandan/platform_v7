define(
		[ "ui_component", "commons", "wSelect2" ],
		function(ui_component, commons, wSelect2) {
			var component = $.ui.component.BaseComponent();
			// 创建配置界面
			function createConfigurationView(ui) {
				var StringBuilder = commons.StringBuilder;
				var sb = new StringBuilder();
				sb.append('<div class="container-fluid">');
				sb.append('<ul class="nav nav-tabs">');
				sb.append('<li class="active"><a data-toggle="tab" href="#ace_page_home">页面效果</a></li>');
				sb.append('<li><a data-toggle="tab" href="#ace_page_content_configuration">内容配置</a></li>');
				sb.append('<li><a data-toggle="tab" href="#ace_page_other_configuration">其他配置</a></li>');
				sb.append('</ul>');
				sb.append('<div class="tab-content">');
				sb.append('<div id="ace_page_home" class="tab-pane fade in active" style="height:500px;">');
				sb.append('<iframe src="' + ctx + '/web/app/system3.html" style="width:100%;height:100%;"></iframe>');
				sb.append('</div>');
				sb.append('<div id="ace_page_content_configuration" class="tab-pane fade">');
				// 头部开始
				sb.append('<div class="form-group">');
				sb.append('<label for="header_widget_name" class="col-sm-2 control-label">头部</label>');
				sb.append('<div class="col-sm-10">');
				sb.append('<input type="text" name="header_widget_name" id="header_widget_name">');
				sb
						.append('<input type="hidden" class="form-control" name="header_widget_uuid" id="header_widget_uuid">');
				sb.append('</div>');
				sb.append('</div>');
				// 头部结束
				// 左导航开始
				sb.append('<div class="form-group">');
				sb.append('<label for="left_sidebar_widget_name" class="col-sm-2 control-label">左导航</label>');
				sb.append('<div class="col-sm-10">');
				sb.append('<input type="text" name="left_sidebar_widget_name" id="left_sidebar_widget_name">');
				sb
						.append('<input type="hidden" class="form-control" name="left_sidebar_widget_uuid" id="left_sidebar_widget_uuid">');
				sb.append('</div>');
				sb.append('</div>');
				// 左导航结束
				// 仪表盘开始
				sb.append('<div class="form-group">');
				sb.append('<label for="dashboard_widget_name" class="col-sm-2 control-label">仪表盘</label>');
				sb.append('<div class="col-sm-10">');
				sb.append('<input type="text" name="dashboard_widget_name" id="dashboard_widget_name">');
				sb
						.append('<input type="hidden" class="form-control" name="dashboard_widget_uuid" id="dashboard_widget_uuid">');
				sb.append('</div>');
				sb.append('</div>');
				// 仪表盘结束
				// 底部开始
				sb.append('<div class="form-group">');
				sb.append('<label for="footer_widget_name" class="col-sm-2 control-label">底部</label>');
				sb.append('<div class="col-sm-10">');
				sb.append('<input type="text" name="footer_widget_name" id="footer_widget_name">');
				sb
						.append('<input type="hidden" class="form-control" name="footer_widget_uuid" id="footer_widget_uuid">');
				sb.append('</div>');
				sb.append('</div>');
				// 底部结束
				sb.append('</div>');
				sb.append('<div id="ace_page_other_configuration" class="tab-pane fade">');
				sb.append('</div>');
				sb.append('</div>');
				sb.append('</div>');

				$(ui.element).append(sb.toString());
			}
			// 初始化配置信息
			function initConfiguration(ui) {
				var options = ui.options;
				var $element = $(ui.element);
				// 加载的系统
				// 头部
				$("#header_widget_name", $element).wSelect2({
					serviceName : "appWidgetDefinitionMgr",
					labelField : "header_widget_name",
					valueField : "header_widget_uuid",
					params : {
						wtype : "wHeader"
					},
					width : "100%",
					height : 250
				});
				setWidgetValues("header", $element, options.header);

				// 左导航
				$("#left_sidebar_widget_name", $element).wSelect2({
					serviceName : "appWidgetDefinitionMgr",
					labelField : "left_sidebar_widget_name",
					valueField : "left_sidebar_widget_uuid",
					params : {
						wtype : "wLeftSidebar"
					},
					width : "100%",
					height : 250
				});
				setWidgetValues("left_sidebar", $element, options.leftSidebar);

				// 仪表盘
				$("#dashboard_widget_name", $element).wSelect2({
					serviceName : "appWidgetDefinitionMgr",
					labelField : "dashboard_widget_name",
					valueField : "dashboard_widget_uuid",
					params : {},
					multiple : true,
					width : "100%",
					height : 250
				});
				setWidgetValues("dashboard", $element, options.dashboard);

				// 底部
				$("#footer_widget_name", $element).wSelect2({
					serviceName : "appWidgetDefinitionMgr",
					labelField : "footer_widget_name",
					valueField : "footer_widget_uuid",
					params : {
						wtype : "wFooter"
					},
					width : "100%",
					height : 250
				});
				setWidgetValues("footer", $element, options.footer);
			}
			function setWidgetValues(propertyName, $element, widgetOption) {
				if (widgetOption == null) {
					return;
				}
				var nameSelector = "#" + propertyName + "_widget_name";
				var uuidSelector = "#" + propertyName + "_widget_uuid";
				$(nameSelector, $element).val(widgetOption.name);
				$(uuidSelector, $element).val(widgetOption.uuid);
				$(nameSelector, $element).trigger("change");
			}

			component.prototype.create = function() {
				// 更改设计器信息
				$(".navbar-brand").text("Ace页面设计器");

				var $element = $(this.element);
				var options = this.options;
				var _self = this;

				// 创建配置界面
				createConfigurationView(this);
				// 初始化配置信息
				initConfiguration(this);
			}
			component.prototype.toHtml = function() {
				var options = this.options;
				var children = this.getChildren();
				var id = this.getId();
				var html = "<div id='" + id + "' class='web-app-container container-fluid'>";
				if (children != null) {
					$.each(children, function(i) {
						var child = this;
						var childHtml = child.toHtml.call(child);
						html += childHtml;
					});
				}
				html += "</div>";
				return html;
			}
			component.prototype.getDefinitionJson = function($element) {
				var definitionJson = this.options;
				definitionJson.id = this.getId();
				// definitionJson.title = "我的主页";
				definitionJson.items = [];
				var children = this.getChildren();
				$.each(children, function(i) {
					var child = this;
					definitionJson.items.push(child.getDefinitionJson());
				});

				var $element = this.$element;
				// 头部
				definitionJson.header = {};
				definitionJson.header.name = $("#header_widget_name", $element).val();
				definitionJson.header.uuid = $("#header_widget_uuid", $element).val();
				// 左导航
				definitionJson.leftSidebar = {};
				definitionJson.leftSidebar.name = $("#left_sidebar_widget_name", $element).val();
				definitionJson.leftSidebar.uuid = $("#left_sidebar_widget_uuid", $element).val();
				// 仪表盘
				definitionJson.dashboard = {};
				definitionJson.dashboard.name = $("#dashboard_widget_name", $element).val();
				definitionJson.dashboard.uuid = $("#dashboard_widget_uuid", $element).val();
				// 底部
				definitionJson.footer = {};
				definitionJson.footer.name = $("#footer_widget_name", $element).val();
				definitionJson.footer.uuid = $("#footer_widget_uuid", $element).val();
				return definitionJson;
			}
			return component;
		});