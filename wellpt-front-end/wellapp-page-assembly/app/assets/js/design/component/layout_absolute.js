define([ "ui_component", "commons" ], function(ui_component, commons) {
	var component = $.ui.component.BaseComponent();
	var full_width = "100%";
	// $widgetHeader.height() + $widgetHeader.css("padding-top") in px +
	// $widgetHeader.css("padding-bottom") in px
	var fixed_height = 26;
	component.prototype.create = function() {
		var _self = this;
		var options = _self.options;
		var $element = $(_self.element);
		var width = options.width;
		var height = Number(options.height) + fixed_height;
		var newLayoutWidth = $element.width();
		_self.lastLayoutWidth = width;
		_self.layoutDensity = 1;
		width = newLayoutWidth;
		height = _self.layoutDensity * height;

		var $widgetHeader = $element.find(".widget-header");
		var $widgetBody = $element.find(".widget-body");
		_self.$widgetHeader = $widgetHeader;
		_self.$widgetBody = $widgetBody;
		$widgetBody.addClass("ui-sortable");
		var $panelContainer = $element.find(".ui-sortable");

		var defaultSortableOptions = _self.pageDesigner.getDefaultSortableOptions();
		this.$panelContainer = $panelContainer;
		$panelContainer.sortable($.extend(defaultSortableOptions, {
			receive : function(event, ui) {
				if (ui.helper != null) {
					var width = $(ui.helper).width();
					var height = $(ui.helper).height() + fixed_height;
					var top = ui.position.top;
					var left = ui.position.left;
					var $wbox = createWbox.call(_self, $panelContainer, width, height, top, left);
					_self.pageDesigner.drop(_self, $wbox, $(ui.helper));
					_self.recreateWboxIfRequire(ui, $wbox, width, height, top, left);
				} else {
					var width = $($panelContainer).width() / 2;
					var height = $($panelContainer).height() / 2;
					var offset = $(ui.item).offset();
					var top = offset.top / 2;
					var left = offset.left / 2;
					var $wbox = createWbox.call(_self, $panelContainer, width, height, top, left);
					$wbox.append($(ui.item));
					_self.pageDesigner.drop(_self, $wbox, $(ui.item));
				}

				_self.updateChildren($panelContainer.children(".wbox").children(".widget"));
				// 刷新
				_self.refresh();
			},
			remove : function(event, ui) {
				_self.updateChildren($panelContainer.children(".wbox").children(".widget"));
			},
			update : function(event, ui) {
				_self.updateChildren($panelContainer.children(".wbox").children(".widget"));
			}
		}));
		_self.resizeLayout(width, height);
		$element.resizable({
			maxWidth : $element.width(),
			minWidth : $element.width(),
			handles : "s",
			containment : "parent",
			resize : function(event, ui) {
				_self.resizeLayout(ui.size.width, ui.size.height);
			}
		});
		setTimeout(function() {
			// 初始化容器结点
			if (options.items != null) {
				$.each(options.items, function(j, item) {
					var width = _self._getDisplaySizeInInPixels(item.wboxWidth);
					var height = _self._getDisplaySizeInInPixels(item.wboxHeight);
					var top = _self._getDisplaySizeInInPixels(item.positionTop);
					var left = _self._getDisplaySizeInInPixels(item.positionLeft);
					var $wbox = createWbox.call(_self, $panelContainer, width, height, top, left);
					// mod by zhongzh 2017-10-18 // wboxId已存在
					if(item && item["wboxId"]) {
						$wbox.attr("id", item["wboxId"]);
					}
					// mod end
					var $draggable = _self.pageDesigner.createDraggableByDefinitionJson(item);
					_self.pageDesigner.drop(_self, $wbox, $draggable, item);
				});
			}
		}, 100);
	}
	component.prototype.recreateWboxIfRequire = function(ui, $wbox, width, height, top, left) {
		var _self = this;
		var $panelContainer = _self.$panelContainer;
		// 组件不是来原于页面设计外面的组件列表，直接返回
		if (!_self.pageDesigner.isSortableItemFromOutside(ui)) {
			return;
		}
		var $widget = $wbox.children(".widget");
		if ($widget.length === 0) {
			return;
		}
		var child = $widget.data("component");
		var childJson = child.getDefinitionJson();
		childJson.wboxId = $wbox.prop("id");
		childJson.wboxWidth = $wbox.width();
		childJson.wboxHeight = $wbox.height();
		childJson.positionTop = $wbox.position().top;
		childJson.positionLeft = $wbox.position().left;

		$wbox.remove();

		var $newWbox = createWbox.call(_self, $panelContainer, width, height, top, left);
		var $draggable = _self.pageDesigner.createDraggableByDefinitionJson(childJson);
		_self.pageDesigner.drop(_self, $newWbox, $draggable, childJson);
	}
	component.prototype.refresh = function() {
		var _self = this;
		var $element = $(_self.element);
		// $element.resizable("destroy");
		$element.resizable({
			maxWidth : $element.width(),
			minWidth : $element.width(),
			handles : "s",
			containment : "parent",
			resize : function(event, ui) {
				_self.resizeLayout(ui.size.width, ui.size.height);
			}
		});

		// 更新高度
		$element.height(_self.lastLayoutHeight);

		var children = _self.getChildren();
		$.each(children, function(i, child) {
			var $wbox = $(child.element).parent();
			$wbox.css("max-width", _self.$panelContainer.width());
		});
	}
	component.prototype.resizeLayout = function(width, height) {
		var $element = $(this.element);
		$element.height(height);
		this.lastLayoutWidth = width;
		this.lastLayoutHeight = height;

		var bodyHeight = height - fixed_height;
		this.options.width = width;
		this.options.height = bodyHeight;

		var $widgetBody = this.$widgetBody;
		$widgetBody.css("width", "100%");
		$widgetBody.css("height", bodyHeight);
		$widgetBody.css("overflow", "auto");
		$widgetBody.css("position", "absolute");
	}
	component.prototype._getDisplaySizeInInPixels = function(rawSize) {
		return this.layoutDensity * rawSize;
	}
	function createWbox($container, width, height, top, left) {
		var containerWidth = $container.width();
		if (width > containerWidth) {
			width = containerWidth;
		}
		if (top < 0) {
			top = 0;
		}
		if (left < 0) {
			left = 0;
		}
		var $wbox = $("<div class='wbox'><div class='absolute-drag-inside'>绝对布局内拖动</div></div>");
		$wbox.prop("id", commons.UUID.createUUID());
		$wbox.css("position", "absolute");
		$wbox.css("border", "1px solid #ddd");
		$wbox.css("overflow", "hidden");
		$wbox.css("max-width", containerWidth);
		$wbox.css("width", numberToPixel(width));
		$wbox.css("height", numberToPixel(height));
		$wbox.css("top", top);
		$wbox.css("left", left);
		$container.append($wbox);
		$wbox.resizable({
			containment : $container
		});
		// 可拖动，不可再拖出绝对布局
		var defaultSortableOptions = this.pageDesigner.getDefaultSortableOptions();
		$wbox.draggable({
			handle : ".absolute-drag-inside",
			cancel : defaultSortableOptions.cancel,
			scroll : true,
			containment : $container,
			stop : function(event, ui) {
				console.log("position: " + $(this).position().top + " " + $(this).position().left);
				console.log("width/height: " + $(this).width() + " " + $(this).height());
			}
		});
		return $wbox;
	}
	component.prototype.childDestroy = function(child) {
		$(child.element).parent().remove();
	}
	component.prototype.toHtml = function() {
		var options = this.options;
		var children = this.getChildren();
		var id = this.getId();
		var html = "<div id='" + id + "' style='position:relative;'>";
		if (children != null) {
			$.each(children, function(i) {
				var child = this;
				var childHtml = child.toHtml.call(child);
				var $wbox = $(child.element).parent();
				var wboxId = $wbox.prop("id");
				html += "<div id='" + wboxId + "' class='wbox' style='position:absolute;'>" + childHtml + "</div>";
			});
		}
		html += "</div>";
		return html;
	}
	component.prototype.getDefinitionJson = function() {
		var definitionJson = this.options;
		definitionJson.id = this.getId();
		definitionJson.width = $(this.element).width();
		definitionJson.height = this.options.height;
		definitionJson.items = [];
		var children = this.getChildren();
		$.each(children, function(i) {
			var child = this;
			var childJson = child.getDefinitionJson();
			var $wbox = $(child.element).parent();
			childJson.wboxId = $wbox.prop("id");
			childJson.wboxWidth = getCssWidth($wbox);
			childJson.wboxHeight = getCssHeight($wbox);
			childJson.positionTop = $wbox.position().top;
			childJson.positionLeft = $wbox.position().left;
			definitionJson.items.push(childJson);
		});
		return definitionJson;
	}
	function getCssWidth($el) {
		return pixelToNumber($el.css("width"));
	}
	function getCssHeight($el) {
		return pixelToNumber($el.css("height"));
	}
	function pixelToNumber(pixel) {
		return Number(pixel.substring(0, pixel.length - 2));
	}
	function numberToPixel(number) {
		return number + "px";
	}
	return component;
});