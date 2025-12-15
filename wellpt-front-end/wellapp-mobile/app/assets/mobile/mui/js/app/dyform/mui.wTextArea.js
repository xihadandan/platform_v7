define([ "mui", "constant", "commons", "server", "mui-DyformField" ], function($, constant, commons, server, DyformField) {
	// 文本域
	var wTextArea = function($placeHolder, options) {
		DyformField.apply(this, arguments);
	};
	function htmlDecode(html) {
		var elem = document.createElement('span');
		elem.innerHTML = html;
		return elem.textContent || elem.innerText;
	}
	var placeHolderTplId_1 = 'mui-DyformField-placeHolder-textarea';
	var placeHolderTplId_2 = 'mui-DyformField-placeHolder-textarea-2';
	commons.inherit(wTextArea, DyformField, {
		init : function() {
			var self = this;
			// html解码
			// self.value = htmlDecode(self.value);
			self._superApply();
		},
		render : function() {
			var self = this;
			var displayStyle = parseInt( self.options.fieldDefinition.displayStyle );
			var placeHolderTplId = displayStyle == 2 ? placeHolderTplId_2 : placeHolderTplId_1;
			self._superApply([ placeHolderTplId ]);
			// $(self.$editableElem[0].parentNode.parentNode).scroll();
		},
		// 渲染编辑元素
		renderEditableElem : function($editableElem) {
			var self = this;
			if (self.$editableElem) {
				self.$editableElem[0].value = self.getValue();
				self.$editableElem[0].innerHTML = self.getValue(); // 
			}
			self.autoTextarea(self.$editableElem[0]);

		},
		// 渲染文本元素
		renderLabelElem : function($labelElem) {
			var self = this;
			if (self.$labelElem) {
				var inputMode= self.definition.inputMode;
				var value = self.getValue();
				if(typeof value === "string" && !(inputMode === dyFormInputMode.ckedit)){
					value = value.replace(new RegExp("\n", "gm"), "<br/>");
				}
				self.$labelElem[0].innerHTML = value;
				$("table[style*=width], img[style*=width]", self.$labelElem[0]).each(function() {
					var self = this;
					self.style.width = "100%";
					self.setAttribute("width", "100%");
				});
			}
		},
		autoTextarea:function (elem, extra, maxHeight) {
	        extra = extra || 0;
	        var isFirefox = !!document.getBoxObjectFor || 'mozInnerScreenX' in window,
	        isOpera = !!window.opera && !!window.opera.toString().indexOf('Opera'),
	                addEvent = function (type, callback) {
	                        elem.addEventListener ?
	                                elem.addEventListener(type, callback, false) :
	                                elem.attachEvent('on' + type, callback);
	                },
	                getStyle = elem.currentStyle ? function (name) {
	                        var val = elem.currentStyle[name];
	 
	                        if (name === 'height' && val.search(/px/i) !== 1) {
	                                var rect = elem.getBoundingClientRect();
	                                return rect.bottom - rect.top -
	                                        parseFloat(getStyle('paddingTop')) -
	                                        parseFloat(getStyle('paddingBottom')) + 'px';        
	                        };
	 
	                        return val;
	                } : function (name) {
	                                return getComputedStyle(elem, null)[name];
	                },
	                minHeight = parseFloat(getStyle('height'));
	 
	        elem.style.resize = 'none';
	 
	        var change = function () {
	                var scrollTop, height,
	                        padding = 0,
	                        style = elem.style;
	 
	                if (elem._length === elem.value.length) return;
	                elem._length = elem.value.length;
	 
	                if (!isFirefox && !isOpera) {
	                        padding = parseInt(getStyle('paddingTop')) + parseInt(getStyle('paddingBottom'));
	                };
	                scrollTop = document.body.scrollTop || document.documentElement.scrollTop;
	 
	                elem.style.height = minHeight + 'px';
	                if (elem.scrollHeight > minHeight) {
	                        if (maxHeight && elem.scrollHeight > maxHeight) {
	                                height = maxHeight + (padding/2);
	                                style.overflowY = 'auto';
	                        } else {
	                                height = elem.scrollHeight + (padding/2);
	                                style.overflowY = 'hidden';
	                        };
	                        style.height = height + extra + 'px';
	                        scrollTop += parseInt(style.height) - elem.currHeight;
	                        document.body.scrollTop = scrollTop;
	                        document.documentElement.scrollTop = scrollTop;
	                        elem.currHeight = parseInt(style.height);
	                };
	        };
	 
	        addEvent('propertychange', change);
	        addEvent('input', change);
	        addEvent('focus', change);
	        setTimeout(change, 0);
		}
	});
	return wTextArea;
});