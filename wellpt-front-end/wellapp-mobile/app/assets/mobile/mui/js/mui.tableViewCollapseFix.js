/**
 * tableViewCollapseFix:友好解决mui的table view没有collapse事件
 * 
 * @param {type} $
 * @param {type}
 *            window
 * @param {type}
 *            document
 * @returns {undefined}
 */
(function($, window, document) {
	var CLASS_ACTIVE = 'mui-active';
	var EXPANDED_BEFORE = $.EXPANDED_BEFORE = "expanded.before";
	var COLLAPSE_BEFORE = $.COLLAPSE_BEFORE = "collapse.before";
	var EXPANDED_AFTER = $.EXPANDED_AFTER = "expanded.after";
	var COLLAPSE_AFTER = $.COLLAPSE_AFTER = "collapse.after";
	var fixTableCollspse = function(event) {
		var self = this;
		if (!self) {
			return;
		}
		var classList = self.classList;
		if (classList.contains('mui-collapse') && !self.parentNode.classList.contains('mui-unfold')) {
			var isExpand = !classList.contains(CLASS_ACTIVE);
			if (isExpand) {
				// 触发展开事件
				$.trigger(self, EXPANDED_BEFORE);
			} else {
				// 触发关闭事件
				$.trigger(self, COLLAPSE_BEFORE);
			}
			var oldtoggle = classList.toggle;
			classList.toggle = function() {
				delete classList["toggle"];
				classList.toggle = oldtoggle;
				classList.toggle.apply(this, arguments);
				if (isExpand) {
					// 触发展开事件
					$.trigger(self, EXPANDED_AFTER);
				} else {
					// 触发关闭事件
					$.trigger(self, COLLAPSE_AFTER);
				}
			}
		}
	};
	// 冒泡在window之前捕获：element->body->document->window
	$(document).on("tap", ".mui-table-view-cell", fixTableCollspse);
})(mui, window, document);
