define([ "ui_component" ], function() {
	var component = $.ui.component.BaseComponent();
	component.prototype.create = function() {
		var $element = $(this.element);
		var options = this.options;
		var _self = this;
		var defaultSortableOptions = _self.pageDesigner.getDefaultSortableOptions();
		$element.sortable($.extend(defaultSortableOptions, {
			receive : function(event, ui) {
				if (ui.helper != null) {
					_self.pageDesigner.drop(_self, $element, $(ui.helper));
				}
				_self.updateChildren($element.children(".widget"));
			},
			remove : function(event, ui) {
				_self.updateChildren($element.children(".widget"));
			},
			update : function(event, ui) {
				_self.updateChildren($element.children(".widget"));
			}
		}));
		// 初始化容器结点
		if (options.items != null) {
			$.each(options.items, function(i) {
				var item = this;
				var $draggable = _self.pageDesigner.createDraggableByDefinitionJson(item);
				_self.pageDesigner.drop(_self, $element, $draggable, item);
			});
		}
	}
	component.prototype.toHtml = function() {
		var options = this.options;
		var children = this.getChildren();
		var id = this.getId();
		var html = "<div id='" + id + "' class='mui-fullscreen'>";
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
		// definitionJson.title = "我的主页"; // WebApp.currentUserAppData.module.name
		definitionJson.items = [];
		var children = this.getChildren();
		$.each(children, function(i) {
			var child = this;
			definitionJson.items.push(child.getDefinitionJson());
		});
		return definitionJson;
	}
	return component;
});