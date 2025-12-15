;
(function($) {
	/*
	 * POPUPWINDOW CLASS DEFINITION ======================
	 */
	var PopupViewWindow = function(element, options) {
		this.init("popupViewWindow", element, options);
	};

	PopupViewWindow.prototype = {
		constructor : PopupViewWindow,
		init : function(type, element, options) {
			this.$element = $(element);
			var $element = this.$element;
			this.options = this.getOptions(options);
			var options = this.options;
			var dialogId = $element.attr("id") + "_" + "dialog";
			var tableId = $element.attr("id") + "_" + "select";
			var tableDiv = "<div id='" + dialogId + "'>" + "<table id='" + tableId
					+ "'><tr><td></td></tr></table>" + "</div>";
			this.dialogId = dialogId;
			this.tableId = tableId;
			$(tableDiv).insertAfter($element);

			this.$element.click($.proxy(this.open, this));

			var $this = this;
			// 初始化窗口
			$("#" + this.dialogId)
					.dialog(
							{
								title : options.title,
								resizable : options.resizable,
								autoOpen : options.autoOpen,
								height : options.height,
								width : options.width,
								modal : options.modal,
								open : function() {
									$this._open();
								},
								close : function() {
									$this._close();
								},
								buttons : {
									"确定" : function() {
										}
									},
									"取消" : function() {
										if ($this.options.afterCancel) {
											$this.options.afterCancel
													.call($this.$element[0]);
										}
										$(this).dialog("close");
									}
								}
							);

		},
		getOptions : function(options) {
			options = $.extend({}, $.fn.popupViewWindow.defaults, options,
					this.$element.data());
			return options;
		},
		open : function() {
			$("#" + this.dialogId).dialog("open");
		},
		_open : function() {
			
			
		},
		_close : function() {

		}
	};

	/*
	 * POPUPWINDOW PLUGIN DEFINITION =========================
	 */
	$.fn.popupViewWindow = function(option) {
		return this
				.each(function() {
					var $this = $(this), data = $this.data("popupViewWindow"), options = $
							.extend({}, $this.data(), typeof option == 'object'
									&& option);
					if (!data) {
						$this.data('popupViewWindow',
								(data = new PopupViewWindow(this, options)));
					}
					if (typeof option == 'string') {
						data[option]();
					}
				});
	};

	$.fn.popupViewWindow.Constructor = PopupViewWindow;

	$.fn.popupViewWindow.defaults = {
		title : "列值",
		autoOpen : false,
		resizable : false,
		height : 400,
		width : 450,
		modal : true,
		initValues : null,
		enableTreeView : true
	};
})(jQuery);