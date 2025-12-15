define([ "mui", "constant", "commons", "server", "appModal", "mui-DyformField", "formBuilder" ], function($, constant, commons, server, appModal, DyformField, formBuilder) {
	// URL嵌入页面
	var wEmbedded = function($placeHolder, options) {
		DyformField.apply(this, arguments);
	};
	var placeHolderTplId = "mui-DyformField-placeHolder-embedded";
	commons.inherit(wEmbedded, DyformField, {
		render : function() {
			var self = this;
			self._superApply([ placeHolderTplId ]);
			var fn = function(event) {
				var self = this;
				event.preventDefault();
				event.stopPropagation();
				$.ui.openIframe(self.getAttrubute("href"));
				return false;
			}
			self.$labelElem[0].addEventListener("tap", fn, false);
		},
		// 渲染文本元素
		renderLabelElem : function($labelElem) {
			var self = this;
			if (self.$labelElem) {
				self.$labelElem[0].setAttribute("href", self.getDisplayValue());
			}
		},
		// 设置字段显示为文本
		setDisplayAsLabel : function() {
			var self = this;
			self._superApply();
		}
	});
	return wEmbedded;
});