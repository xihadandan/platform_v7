define([ "jquery", "server", "commons", "constant", "appContext", "appModal" ], function($, server, commons, constant,
		appContext, appModal) {
	var UUID = commons.UUID;
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	// 工具栏
	var WorkFlowToolbar = function(workView, toolbarPlaceholder) {
		var _self = this;
		_self.workView = workView;
		_self.toolbarPlaceholder = toolbarPlaceholder;
		_self.$element = $(toolbarPlaceholder, workView.element);
	};
	// 按钮HTML模板
	var btnTpl = '<button btnId={0} class="btn {1}" title="{2}">{3}</button>';
	$.extend(WorkFlowToolbar.prototype, {
		// 初始化
		init : function() {
		},
		// 显示
		show : function() {
			this.$element.show();
		},
		// 隐藏
		hide : function() {
			this.$element.hide();
		},
		// 添加操作按钮，
		// text，按钮显示名称
		// title，鼠标显示名称
		// className，按钮样式
		// click，点击事件
		// context，执行事件的上下文对象，为空为按钮本身
		addButton : function(options) {
			var _self = this;
			var btnId = UUID.createUUID();
			var text = options.text;
			var title = text;
			var className = options.className || "btn-primary";
			var click = options.click || $.noop;
			var sb = new StringBuilder();
			sb.appendFormat(btnTpl, btnId, className, title, text);
			_self.$element.append(sb.toString());
			$("button[btnId='" + btnId + "']", _self.$element).on("click", function() {
				var context = options.context || this;
				if ($.isFunction(click)) {
					click.apply(context, arguments);
				}
			});
		},
		// 删除所有操作按钮
		removeAllButton : function() {
			this.$element.html("");
		}
	});
	return WorkFlowToolbar;
});