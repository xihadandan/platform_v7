/**
 * 网页预览组件
 * varstion 0.4.0
 * by Houfeng
 * Houfeng@DCloud.io
 */

(function($, document) {


	if (!Element.prototype.matches) {
	    Element.prototype.matches = 
	        Element.prototype.matchesSelector || 
	        Element.prototype.mozMatchesSelector ||
	        Element.prototype.msMatchesSelector || 
	        Element.prototype.oMatchesSelector || 
	        Element.prototype.webkitMatchesSelector ||
	        function(s) {
	            var matches = (this.document || this.ownerDocument).querySelectorAll(s),
	                i = matches.length;
	            while (--i >= 0 && matches.item(i) !== this) {}
	            return i > -1;            
	        };
	}
	
	$.fn.parents = function(selector, end) {
		var elem = this[0];
		end = end || document.body;
		for(; elem && elem !== end; elem = elem.parentElement) {
			if(elem.matches && elem.matches(selector)){
				return elem;
			}
		}
	};
	
	$.init({
		gestureConfig: {
			tap: true, //默认为true
			doubletap: true, //默认为false
			longtap: true, //默认为false
			swipe: false, //默认为true
			drag: true, //默认为true
			hold: true, //默认为false，不监听
			release: true //默认为false，不监听
		}
	});

	
	var touchSupport = ('ontouchstart' in document);
	var tapEventName = touchSupport ? 'tap' : 'click';
	var enterEventName = touchSupport ? 'tap' : 'click';
	var htmlClassName = $.className('html');


	//创建DOM (此函数是否可放在 mui.js 中)
	$.dom = function(str) {
		if (!$.__create_dom_div__) {
			$.__create_dom_div__ = document.createElement('div');
		}
		$.__create_dom_div__.innerHTML = str;
		return $.__create_dom_div__.childNodes;
	};

	//图片预览组件类
	var HtmlViewer = $.HtmlViewer = $.Class.extend({
		//构造函数
		init: function(selector, options) {
			var self = this;
			self.options = options || {};
			self.selector = selector || 'div[data-html]';
			self.options.dbl = true;
			if (self.options.dbl) {
				enterEventName = touchSupport ? 'doubletap' : 'dblclick';
			}
			self.findAllIframe();
			self.maskAllIframe();
			self.createViewer();
			self.bindEvent();
		},
		//创建图片预览组件的整体 UI
		createViewer: function() {
			var self = this;
			self.viewer = $.dom("<div class='mui-htmlviewer'><div class='mui-htmlviewer-mask'></div><div class='mui-htmlviewer-header'><i class='mui-icon mui-icon-closeempty mui-htmlviewer-close'></i><span class='mui-htmlviewer-state'></span></div><i class='mui-icon mui-icon-arrowleft  mui-htmlviewer-left mui-hidden'></i><i class='mui-icon mui-icon-arrowright mui-htmlviewer-right mui-hidden'></i></div>");
			self.viewer = self.viewer[0] || self.viewer;
			//self.viewer.style.height = screen.height;
			self.closeButton = self.viewer.querySelector('.mui-htmlviewer-close');
			self.state = self.viewer.querySelector('.mui-htmlviewer-state');
			self.leftButton = self.viewer.querySelector('.mui-htmlviewer-left');
			self.rightButton = self.viewer.querySelector('.mui-htmlviewer-right');
			self.mask = self.viewer.querySelector('.mui-htmlviewer-mask');
			document.body.appendChild(self.viewer);
		},
		//查找所有符合的图片
		findAllIframe: function() {
			var self = this;
			self.htmls = [].slice.call($(self.selector));
		},
		//查找所有符合的图片
		maskAllIframe: function() {
			var self = this;
			$(self.htmls).each(function(i, html) {
			});
		},
		//检查图片是否为启动预览的图片
		checkIframe: function(target) {
			var self = this;
			if (!target.matches(self.selector)) return false;
			return self.htmls.some(function(html) {
				return html == target;
			});
		},
		//绑定事件
		bindEvent: function() {
			var self = this;
			//绑定图片 tap 事件
			document.addEventListener(enterEventName, function(event) {
				if (!self.viewer) return;
				var target = event.target;
				var targetItem = $(target).parents(self.selector);
				if (!targetItem) return;
				self.viewer.style.display = 'block';
				setTimeout(function() {
					self.viewer.style.opacity = 1;
				}, 0);
				self.index = self.htmls.indexOf(targetItem);
				self.currentItem = self.createIframe(self.index);
			}, false);
			//关系按钮事件
			self.closeButton.addEventListener(tapEventName, function(event) {
				self.viewer.style.opacity = 0;
				setTimeout(function() {
					self.viewer.style.display = 'none';
					self.disposeIframe(true);
				}, 600);
				event.preventDefault();
				event.cancelBubble = true;
			}, false);
			//处理左右按钮
			self.leftButton.addEventListener(tapEventName, function() {
				self.prev();
			}, false);
			self.rightButton.addEventListener(tapEventName, function() {
				self.next();
			}, false);
			//处理划动
			self.mask.addEventListener($.EVENT_MOVE, function(event) {
				event.preventDefault();
				event.cancelBubble = true;
			}, false);
			self.viewer.addEventListener('swipeleft', function(event) {
				if (self.scaleValue == 1) self.next();
				event.preventDefault();
				event.cancelBubble = true;
			}, false);
			self.viewer.addEventListener('swiperight', function(event) {
				if (self.scaleValue == 1) self.prev();
				event.preventDefault();
				event.cancelBubble = true;
			}, false);
			//处理缩放开始
			self.viewer.addEventListener($.EVENT_START, function(event) {
				var touches = event.touches;
				if (touches.length == 2) {
					var p1 = touches[0];
					var p2 = touches[1];
					var x = p1.pageX - p2.pageX; //x1-x2
					var y = p1.pageY - p2.pageY; //y1-y2
					self.scaleStart = Math.sqrt(x * x + y * y);
					self.isMultiTouch = true;
				} else if (touches.length = 1) {
					self._dragX = self.dragX;
					self._dragY = self.dragY;
					self.dragStart = touches[0];
				}
			}, false);
			self.viewer.addEventListener($.EVENT_MOVE, function(event) {
				var html = self.currentItem.querySelector('div.mui-htmlviewer-content');
				var touches = event.changedTouches;
				if (touches.length == 2) {
					event.preventDefault();
					event.cancelBubble = true;
					var p1 = touches[0];
					var p2 = touches[1];
					var x = p1.pageX - p2.pageX;
					var y = p1.pageY - p2.pageY;
					self.scaleEnd = Math.sqrt(x * x + y * y);
					self._scaleValue = (self.scaleValue * (self.scaleEnd / self.scaleStart));
					//self.state.innerText = self._scaleValue;
					html.style.webkitTransform = "scale(" + self._scaleValue + "," + self._scaleValue + ") "; // + " translate(" + self.dragX || 0 + "px," + self.dragY || 0 + "px)";
				} else if (!self.isMultiTouch && touches.length == 1 /*&& self.scaleValue != 1*/) {
					event.preventDefault();
					event.cancelBubble = true;
					self.dragEnd = touches[0];
					self._dragX = self.dragX + (self.dragEnd.pageX - self.dragStart.pageX);
					self._dragY = self.dragY + (self.dragEnd.pageY - self.dragStart.pageY);
					html.style.left = self._dragX + 'px';
					html.style.top = self._dragY + 'px';
					//html.style.transform = "translate(" + self._dragX + "px," + self._dragY + "px) " + " scale(" + self.scaleValue || 1 + "," + self.scaleValue || 1 + ")";
				}
			}, false);
			self.viewer.addEventListener($.EVENT_END, function() {
				self.scaleValue = self._scaleValue || self.scaleValue;
				self._scaleValue = null;
				self.dragX = self._dragX;
				self.dragY = self._dragY;
				self._dragX = null;
				self._dragY = null;
				var touches = event.touches;
				self.isMultiTouch = (touches.length != 0);
			});
			// doubletap 好像不能用
			self.viewer.addEventListener('doubletap', function() {
				var html = self.currentItem.querySelector('div.mui-htmlviewer-content');
				var origScaleValue = self.scaleValue;
				if (self.scaleValue === 1) {
					self.scaleValue = 2;
				} else {
					self.scaleValue = 1;
				}
				self.dragX = 0;
				self.dragY = 0;
				html.style.left = self.dragX + 'px';
				html.style.top = self.dragY + 'px';
				html.style.webkitTransform = "scale(" + self.scaleValue + "," + self.scaleValue + ") "; //+ " translate(" + self.dragX || 0 + "px," + self.dragY || 0 + "px)";
				self.viewer.__tap_num = 0;
			}, false);
			//处理缩放结束
		},
		//下一张图片
		next: function() {
			var self = this;
			self.mask.style.display = 'block';
			self.index++;
			var newItem = self.createIframe(self.index, 'right');
			setTimeout(function() {
				self.currentItem.classList.remove('mui-htmlviewer-item-center');
				self.currentItem.classList.add('mui-htmlviewer-item-left');
				newItem.classList.remove('mui-htmlviewer-item-right');
				newItem.classList.add('mui-htmlviewer-item-center');
				self.oldItem = self.currentItem;
				self.currentItem = newItem;
				// TODO: 临时,稍候将调整
				setTimeout(function() {
					self.disposeIframe();
					self.mask.style.display = 'none';
				}, 600);
			}, 25);
		},
		//上一张图片
		prev: function() {
			var self = this;
			self.mask.style.display = 'block';
			self.index--;
			var newItem = self.createIframe(self.index, 'left');
			setTimeout(function() {
				self.currentItem.classList.remove('mui-htmlviewer-item-center');
				self.currentItem.classList.add('mui-htmlviewer-item-right');
				newItem.classList.remove('mui-htmlviewer-item-left');
				newItem.classList.add('mui-htmlviewer-item-center');
				self.oldItem = self.currentItem;
				self.currentItem = newItem;
				// TODO: 临时,稍候将调整
				setTimeout(function() {
					self.disposeIframe();
					self.mask.style.display = 'none';
				}, 600);
			}, 25);
		},
		//释放不显示的图片
		disposeIframe: function(all) {
			var sel = '.mui-htmlviewer-item-left,.mui-htmlviewer-item-right';
			if (all) sel += ",.mui-htmlviewer-item";
			var willdisposes = $(sel);
			willdisposes.each(function(i, item) {
				if (item.parentNode && item.parentNode.removeChild)
					item.parentNode.removeChild(item, true);
			});
		},
		//创建一个图片
		createIframe: function(index, type) {
			var self = this;
			type = type || 'center';
			if (index < 0) index = self.htmls.length - 1;
			if (index > self.htmls.length - 1) index = 0;
			self.index = index;
			var item = $.dom("<div class='mui-htmlviewer-item'></div>")[0];
			var html = self.htmls[self.index];
			var content = null;
			if(html.dataset && html.dataset.html) {
				content = $.dom('<div class="mui-htmlviewer-content">' + html.dataset.html + '</div>')[0];
				item.appendChild(content);
			}else {
				content = $.dom('<div class="mui-htmlviewer-content">'+html.innerHTML+'</div>')[0];
				item.appendChild(content);
			}
			item.classList.add('mui-htmlviewer-item-' + type);
			self.viewer.appendChild(item);
			self.state.innerText = (self.index + 1) + "/" + self.htmls.length;
			//重置初始缩放比例
			self.scaleValue = 1;
			self.dragX = 0;
			self.dragY = 0;
			var screenWidth = $.screen ? $.screen.availWidth : window.screen.availWidth;
			var screenHeight = $.screen ? $.screen.availHeight : window.screen.availHeight;
			var contentWidth = self.contentWidth = content.offsetWidth;
			var contentHeight = self.contentHeight = content.offsetHeight;
			content.style.width = contentWidth + "px";// 固定宽度
			if(contentWidth > screenWidth) {
				self.scaleValue = screenWidth/contentWidth; // 缩小
				content.style.webkitTransform = "scale(" + self.scaleValue + "," + self.scaleValue + ") ";
				self.dragX = (screenWidth - contentWidth) / 2;
				content.style.left = self.dragX + "px";
				self.dragY = contentHeight * (self.scaleValue - 1) / 2;
				content.style.top = self.dragY + "px";
			}
			return item;
		}
	});

	$.HtmlViewer = function(selector, options) {
		return new HtmlViewer(selector, options);
	};
}(mui, document));